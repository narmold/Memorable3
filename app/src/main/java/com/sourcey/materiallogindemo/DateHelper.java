package com.sourcey.materiallogindemo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mmiguel12345 on 11/30/15.
 */
public class DateHelper {
    private Integer month;
    private Integer day;
    private Integer year;

    public DateHelper(Integer month, Integer day, Integer year){
        this.month = month;
        this.day = day;
        this.year = year;
    }

    public static DateHelper getCurrentDate(){
        String date2 = new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
        return stringToDateHelper(date2);
    }

    public static DateHelper getFutureDate(Integer daysAhead){
        Integer newDay = daysAhead + getCurrentDate().day;
        DateHelper current = getCurrentDate();
        Integer newMonth = current.month;
        Integer newYear = current.year;

        while((newDay > 31 && hasThirtyOneDays(newMonth)) || (newDay > 30 && hasThirtyDays(newMonth))  || (newDay > 29 && hasTwentyNineDays(newMonth, newYear)) || (newDay > 28 && hasTwentyEightDays(newMonth, newYear))){
            if(hasThirtyOneDays(newMonth)){
                newDay = newDay - 31;
            }else if(hasThirtyDays(newMonth)){
                newDay = newDay - 30;
            }else if(hasTwentyNineDays(newMonth, newYear)){
                newDay = newDay - 29;
            }else{
                newDay = newDay - 28;
            }

            if(newMonth != 12){
                newMonth++;
            }else{
                newMonth = 1;
                newYear++;
            }
        }

        return new DateHelper(newMonth, newDay, newYear);

    }

    private static boolean hasThirtyDays(Integer month){
        if(month == 4 || month == 6 || month == 9 || month == 11){
            return true;
        }
        return false;
    }

    private static boolean hasThirtyOneDays(Integer month){
        if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12){
            return true;
        }
        return false;
    }

    private static boolean hasTwentyEightDays(Integer month, Integer year){
        if(month == 2 && year % 4 != 0){
            return true;
        }
        return false;
    }

    private static boolean hasTwentyNineDays(Integer month, Integer year){
        if(month == 2 && year % 4 == 0){
            return true;
        }
        return false;
    }

    public Integer getMonth(){
        return this.month;
    }

    public Integer getDay(){
        return this.day;
    }

    public Integer getYear(){
        return this.year;
    }

    public boolean isLaterThan(DateHelper check){
        boolean finalBool = false;
        if(this.getYear() == check.getYear()){
            finalBool = true;
        }
        if(this.getYear() > check.getYear() && this.getMonth()>check.getMonth()){
            finalBool = true;
        }
        if(this.getYear() == check.getYear() && this.getMonth() == check.getMonth() && this.getDay() > check.getDay()){
            finalBool = true;
        }
            return finalBool;

    }

    @Override
    public String toString(){
        return this.getMonth().toString() + "/" + this.getDay().toString() + "/"+ this.getYear();
    }

    public static DateHelper stringToDateHelper(String date){

        String[] dateArray = date.split("/");
        Integer month = Integer.parseInt(dateArray[0]);
        Integer day = Integer.parseInt(dateArray[1]);
        Integer year = Integer.parseInt(dateArray[2]);
        return new DateHelper(month, day, year);

    }

}
