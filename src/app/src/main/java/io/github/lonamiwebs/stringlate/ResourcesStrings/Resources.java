package io.github.lonamiwebs.stringlate.ResourcesStrings;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

// Class to manage multiple ResourcesString,
// usually parsed from strings.xml files
public class Resources implements Iterable<ResourcesString> {

    //region Members

    private File mFile; // Keep track of the original file to be able to save()
    private ArrayList<ResourcesString> mStrings;

    // Internally keep track if the changes are saved not to look
    // over all the resources strings individually
    boolean mSavedChanges;

    //endregion

    //region Constructors

    public static Resources fromFile(File file) {
        if (!file.isFile())
            return new Resources(file, new ArrayList<ResourcesString>());

        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return new Resources(file, ResourcesParser.parseFromXml(is));
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) { }
        }
        return null;
    }

    private Resources(File file, ArrayList<ResourcesString> strings) {
        mFile = file;
        mStrings = strings;
        mSavedChanges = true;
    }

    //endregion

    //region Getting content

    public boolean contains(String resourceId) {
        for (ResourcesString rs : mStrings)
            if (rs.getId().equals(resourceId))
                return true;

        return false;
    }

    public String getContent(String resourceId) {
        for (ResourcesString rs : mStrings)
            if (rs.getId().equals(resourceId))
                return rs.getContent();

        return "";
    }

    public boolean areSaved() {
        return mSavedChanges;
    }

    //endregion

    //region Updating (setting) content

    public void setContent(String resourceId, String content) {
        boolean found = false;
        for (ResourcesString rs : mStrings)
            if (rs.getId().equals(resourceId)) {
                if (rs.setContent(content))
                    mSavedChanges = false;

                found = true;
                break;
            }

        if (!found) {
            mStrings.add(new ResourcesString(resourceId, content));
            mSavedChanges = false;
        }
    }

    // If there are unsaved changes, saves the file
    // If the file was saved successfully or there were no changes to save, returns true
    public boolean save() {
        if (mSavedChanges)
            return true;

        try {
            if (ResourcesParser.parseToXml(this, new FileWriter(mFile))) {
                return mSavedChanges = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // We do not want empty files, if it exists and it's empty delete it
        if (mFile.isFile() && mFile.length() == 0)
            mFile.delete();

        return false;
    }

    //endregion

    //region Deleting content

    public void deleteId(String resourceId) {
        for (ResourcesString rs : mStrings)
            if (rs.getId().equals(resourceId)) {
                mStrings.remove(rs);
                break;
            }
    }

    //endregion

    //region Iterator wrapper

    @Override
    public Iterator<ResourcesString> iterator() {
        return mStrings.iterator();
    }

    //endregion

    //region To string

    @Override
    public String toString() {
        StringWriter writer = new StringWriter();
        ResourcesParser.parseToXml(this, writer);
        return writer.toString();
    }

    //endregion
}
