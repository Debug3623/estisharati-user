package estisharatibussiness.users.com.UtilsClasses;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

public class InventumContextWrapper extends ContextWrapper {

    public InventumContextWrapper(Context base) {
        super(base);

    }


    public static ContextWrapper wrap(Context context, String language_code) {

        Locale locale = new Locale(language_code);
        Configuration configuration = context.getResources().getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            LocaleList localeList = new LocaleList(locale);
//            LocaleList.setDefault(localeList);
//            configuration.setLocales(localeList);
            configuration.setLocale(locale);
            context = context .createConfigurationContext(configuration);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
            context = context.createConfigurationContext(configuration);
        } else {
            configuration.locale = locale;
            context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        }
        return new InventumContextWrapper(context);
    }
}
