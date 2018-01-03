package co.id.idpay.ektp.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created      : Rahman on 9/22/2017.
 * Project      : EKTP.
 * ================================
 * Package      : com.esimtek.helper.
 * Copyright    : idpay.com 2017.
 */
public class TimeWatch {
    long starts;

    public static TimeWatch start() {
        return new TimeWatch();
    }

    private TimeWatch() {
        reset();
    }

    public TimeWatch reset() {
        starts = System.currentTimeMillis();
        return this;
    }

    public long time() {
        long ends = System.currentTimeMillis();
        return ends - starts;
    }

    public long time(TimeUnit unit) {
        return unit.convert(time(), TimeUnit.MILLISECONDS);
    }


    public static String getDifferentBetweenTwoDates(String pastDate){//your input date format should be same as your dateformater

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date inputDate = null;
        try {
            inputDate = format.parse(pastDate); // format.parse("2016-04-04 20:10:10");

        }catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar pastCalendar = Calendar.getInstance();
        if(inputDate!=null){
            pastCalendar.setTime(inputDate);
        }
        long pastDateTimeInMilli = pastCalendar.getTimeInMillis();

        Calendar currentCalendar = Calendar.getInstance();
        long currentDateTimeInMilli = currentCalendar.getTimeInMillis();

        String strDifference="";
        long different = currentDateTimeInMilli - pastDateTimeInMilli;

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        /*int elapsedDays = (int)(different / daysInMilli);
        different = different % daysInMilli;

        if(elapsedDays>0){
            strDifference = ""+elapsedDays;
            return strDifference;
        }

        int elapsedHours = (int)(different / hoursInMilli);
        different = different % hoursInMilli;
        if(elapsedHours>0){
            strDifference = ""+elapsedHours;
            return strDifference;
        }

        int elapsedMinutes = (int)(different / minutesInMilli);
        different = different % minutesInMilli;
        if(elapsedMinutes>0){
            strDifference = ""+elapsedMinutes;
            return strDifference;
        }*/

        int elapsedSeconds = (int)(different / secondsInMilli);
        if(elapsedSeconds>0){
            strDifference = ""+elapsedSeconds;
            return strDifference ;
        }else{
            return null;
        }

    }
}
