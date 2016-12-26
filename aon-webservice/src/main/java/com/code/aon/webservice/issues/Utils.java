package com.code.aon.webservice.issues;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.task.TaskStatus;

public class Utils {
	
	public static String getMd5(String str){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        md.update(str.getBytes());
        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
        	sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        
        return sb.toString();
	}
	
	public static String getShortString(String str){
		String title = "";
		String[] string = str.split(" ");
		for(Integer i = 0; i < string.length; i++){
			String s = string[i];
			while(s.length()>30){
				title = title + s.substring(0, 29)+ " ";
				s = s.substring(30);
			}
			title = title +  s + " ";
		}
		return title;
	}
	
	public static String checkString(String str){
		return new String(str.getBytes(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8") );
	}
	
	public static String getStatusColor(Task task){
		
		TaskStatus taskStatus = TaskStatus.values()[task.getStatus()];
		if(taskStatus.equals(TaskStatus.DELETED))
			return "gray";
		else if(taskStatus.equals(TaskStatus.IN_PROGRESS)
				|| taskStatus.equals(TaskStatus.PENDING)){
			if(task.getParent() != null) return "red";
			else return "green";
		}
		else if(taskStatus.equals(TaskStatus.FINISHED))
			return "black";
		else if(taskStatus.equals(TaskStatus.FAQ))
			return "blue";
		return "black";
	}
	
	public static int getDaysBefore(Date date) {
        return (int)( (new Date().getTime() - date.getTime()) / (1000 * 60 * 60 * 24));
	}
	
	public static String getUrl(String scheme, String domain, Boolean local){
		String url = scheme + "://" + domain + "/";
		if(local) url = url + "aon-aio/";
		return url;
	}
	
    public static void addCorsHeader(HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        response.addHeader("Access-Control-Max-Age", "1728000");
    }
    
}
