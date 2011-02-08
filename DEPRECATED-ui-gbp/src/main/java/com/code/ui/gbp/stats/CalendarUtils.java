package com.code.ui.gbp.stats;

public class CalendarUtils {

	public static boolean esBisiesto(int year){
		if ((( year % 4 == 0 ) && ( year % 100 != 0 )) || ( year % 400 == 0 ))                    
			return true;        
		else             
			return false;            
	}
	
	public static int diasDelMes(int month, int year){
		if (month == 0 ||
				month == 2 ||
				month == 4 ||
				month == 6 ||
				month == 7 ||
				month == 9 ||
				month == 11){
			return 31;
		}else if (month == 3 ||
					month == 5 ||
					month == 8 ||
					month == 10){
			return 30;
		}else{
			if (esBisiesto(year)){
				return 29;
			}else{
				return 28;
			}
		}
	}
}
