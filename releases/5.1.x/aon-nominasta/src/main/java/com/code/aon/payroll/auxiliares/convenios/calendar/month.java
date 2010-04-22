package com.code.aon.payroll.auxiliares.convenios.calendar;

import java.util.*;

/**
 * -----------------------------------------------------------------------------
 * Used to provide an example of several Date features used to produce a
 * convenient view of a month in compact form.
 * 
 * @version 1.0
 * @author  Jeffrey M. Hunter  (jhunter@idevelopment.info)
 * @author  http://www.idevelopment.info
 * -----------------------------------------------------------------------------
 */
 
public class month {

    /** List names of the month */
    public final static String[] months = {
        "Enero" , "Febrero" , "Marzo",
        "Abril"   , "Mayo"      , "Junio",
        "Julio"    , "Agosto"   , "Septiembre",
        "Octubre" , "Noviembre" , "Diciembre"
    };


    /** List the days in each month */
    public final static int dom[] = {
        31, 28, 31,  /* jan, feb, mar */
        30, 31, 30,  /* apr, may, jun */
        31, 31, 30,  /* jul, aug, sep */
        31, 30, 31   /* oct, nov, dec */
    };


    /**
     * Helper utility used to print
     * a String to STDOUT.
     * @param s String that will be printed to STDOUT.
     */
    private void printMonth(int mm, int yy) {

        // The number of days to leave blank at
        // the start of this month.
        int leadSpaces = 0;

        System.out.println();
        System.out.println("  " + months[mm] + " " + yy);

        if (mm < 0 || mm > 11) {
            throw new IllegalArgumentException(
                "Month " + mm + " bad, must be 0-11");
        }

        GregorianCalendar cal = new GregorianCalendar(yy, mm, 1);

        System.out.println("Mo Tu We Th Fr Sa Su");

        // Compute how much to leave before before the first day of the month.
        // getDay() returns 0 for Sunday.
        leadSpaces = cal.get(Calendar.DAY_OF_WEEK)-1;
        System.out.println(leadSpaces);
        int daysInMonth = dom[mm];

        if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1) {
            ++daysInMonth;
        }

        // Blank out the labels before 1st day of the month
        for (int i = 0; i < leadSpaces; i++) {
            System.out.print("   ");
        }

        for (int i = 1; i <= daysInMonth; i++) {

            // This "if" statement is simpler than messing with NumberFormat
            if (i<=9) {
                System.out.print(" ");
            }
            System.out.print(i);

            if ((leadSpaces + i) % 7 == 0) { // Wrap if EOL
                System.out.println();
            } else {
                System.out.print(" ");
            }

        }
        System.out.println();
    }



    /**
     * Sole entry point to the class and application.
     * @param args Array of String arguments.
     */
    public static void main(String[] args) {

        int month, year;
        int anyo= 2009;
        for (int i = 0; i <= 11; i++) {
        
        
        month mv = new month();

       
            mv.printMonth(i, anyo);        
            System.out.println(" ");

            
        }

    }
    

}
