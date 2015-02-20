/**
 * Created by anton on 26.01.2015.
 */

import org.joda.time.DateTime;

import java.io.UnsupportedEncodingException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateParser {

    private static DateFormatSymbols symbols;

    static{
        Locale uaLocale = new Locale("uk");
        Locale.setDefault(uaLocale);

        symbols = new DateFormatSymbols(uaLocale);
        String[] months = symbols.getShortMonths();
        for(int i=0;i< months.length;i++){
            if(months[i].length()>3) months[i] = months[i].substring(0,3);
        }
        symbols.setShortMonths(months);
    }

    public static DateTime parse(String dateStr) throws ParseException, UnsupportedEncodingException {

        dateStr = dateStr.toLowerCase();

        SimpleDateFormat sdfmt1 = new SimpleDateFormat("dd.MMM.yyyy",symbols);
        Date dDate = sdfmt1.parse( dateStr );

        DateTime dT = new DateTime(dDate);

        return dT;

    }
}
