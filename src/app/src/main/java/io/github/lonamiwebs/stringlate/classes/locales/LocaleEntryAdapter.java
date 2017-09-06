package io.github.lonamiwebs.stringlate.classes.locales;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.github.lonamiwebs.stringlate.R;

public class LocaleEntryAdapter extends RecyclerView.Adapter<LocaleEntryAdapter.ViewHolder> {

    private ArrayList<Locale> mLocales;

    class ViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout root;
        final TextView displayLang, displayCountry, langCode;

        ViewHolder(final LinearLayout root) {
            super(root);
            this.root = root;
            displayLang = root.findViewById(R.id.language_name);
            displayCountry = root.findViewById(R.id.language_country);
            langCode = root.findViewById(R.id.language_code);
        }

        void update(final Locale locale) {
            displayLang.setText(locale.getDisplayLanguage());
            langCode.setText(LocaleString.getFullCode(locale));

            final String displayCountryText = locale.getDisplayCountry();
            if (displayCountryText.isEmpty()) {
                displayCountry.setText(R.string.default_parenthesis);
                displayCountry.setTypeface(null, Typeface.ITALIC);
            } else {
                displayCountry.setText(displayCountryText);
                displayCountry.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    LocaleEntryAdapter() {
        // Create a map {locale code: Locale} to behave like a set and avoid duplicates
        final HashMap<String, Locale> locales = new HashMap<>();
        for (Locale locale : Locale.getAvailableLocales()) {
            locales.put(LocaleString.getFullCode(locale), locale);
        }

        for (String isoLang : Locale.getISOLanguages()) {
            if (!locales.containsKey(isoLang))
                locales.put(isoLang, new Locale(isoLang));
        }

        // Once everything is filtered, fill in the array list
        mLocales = new ArrayList<>(locales.size());
        for (Map.Entry<String, Locale> locale : locales.entrySet())
            mLocales.add(locale.getValue());

        Collections.sort(mLocales, new Comparator<Locale>() {
            @Override
            public int compare(Locale o1, Locale o2) {
                return o1.getDisplayLanguage().compareTo(o2.getDisplayLanguage());
            }
        });

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int i) {
        return new ViewHolder((LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_locale_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder view, final int i) {
        view.update(mLocales.get(i));
    }

    @Override
    public int getItemCount() {
        return mLocales.size();
    }

}
