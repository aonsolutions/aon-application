package net.aonsolutions.aon.api.excel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

public class UtilsExcel {
	
	public static Cell createCellDouble(Row row, int column, Double value, CellStyle style) {
		Cell cell = row.createCell(column);
		if (value!=null) cell.setCellValue(value);
		if(style!=null) cell.setCellStyle(style);
		return cell;
	}
	
	public static long sumColumn(Map<String, Long> values, String columnLetters, int min, int max) {
		long sum = values.entrySet().stream().filter(map-> map.getKey().contains(columnLetters)).filter(el->{
			Integer key = Integer.parseInt(el.getKey().toString().substring(1));
			return key >=min && key<= max;
		}).mapToLong(el-> el.getValue()).sum();
		System.out.println(columnLetters + " " + min + " "+ max + " " + sum + " "+ sumTime(sum) + " "+ timeDecimals(sum));
		return sum;
	}


	public static Integer indexOf(String[] arr, String str){
		   for (int i = 0; i < arr.length; i++)
		      if(arr[i].equals(str)) return i;
		   return -1;
	}
	
	public static Integer indexOf(ArrayList<String> arr, String str){
		   for (int i = 0; i < arr.size(); i++)
		      if(arr.get(i).toString().equals(str)) return i;
		   return -1;
	}
	public static String timeParse(double time) {
		  Integer msecPerMinute = 1000 * 60;
		  Integer msecPerHour = msecPerMinute * 60;
		  // Calcular las horas: min: seg
		  int hours = (int) Math.floor(time / msecPerHour );
		  time = time - (hours * msecPerHour );
		  int minutes = (int) Math.floor(time / msecPerMinute );
//		  time = time - (minutes * msecPerMinute );
//		  int seconds = (int) Math.floor(time / 1000 );
		  
		  return (hours < 10 ? "0" : "") + hours + ":"
		    + (minutes < 10 ? "0" : "") + minutes;
	}
	
	public static String[] dateString(Date fecha) {
		String dia="";
		String mes="";
		String hour="";
		String min="";
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(fecha);
		String anio = ""+(calendar.get(Calendar.YEAR));
		if(calendar.get(Calendar.DATE)<10) dia="0"+calendar.get(Calendar.DATE);
		else dia=""+calendar.get(Calendar.DATE);
		if((calendar.get(Calendar.MONTH)+1)<10) mes="0"+(calendar.get(Calendar.MONTH)+1);
		else mes=""+(calendar.get(Calendar.MONTH)+1);
		
		if(calendar.get(Calendar.HOUR)<10) hour="0"+calendar.get(Calendar.HOUR);
		else hour=""+calendar.get(Calendar.HOUR);
		
		if(calendar.get(Calendar.MINUTE)<10) min="0"+calendar.get(Calendar.MINUTE);
		else min=""+calendar.get(Calendar.MINUTE);
		
		return new String[] {dia, mes , anio, hour, min};
	}

	public static double timeDecimals(double time) {
		String tm = timeParse(time).replace(":", ".");
		double doble = Double.parseDouble(tm);
		return (Double) doble;
	}
	
	
	public static String sumTime(long timeV) {
	     int s = (int) timeV;
	     int ms = s % 1000;
	     s = (s - ms) / 1000;
	  	 int secs = s % 60;
	  	 s = (s - secs) / 60;
	  	 int mins = s % 60;
	  	 int hrs = (s - mins) / 60;
		 return (hrs < 10 ? "0" : "") + hrs + ":"
		    + (mins < 10 ? "0" : "") + mins;
	}
	
	public static int getWeekOfYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.WEEK_OF_YEAR);
	}

}
