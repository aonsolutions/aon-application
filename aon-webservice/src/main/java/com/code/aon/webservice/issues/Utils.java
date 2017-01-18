package com.code.aon.webservice.issues;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.task.IssueFilter;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

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
    
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private static LinkedList<Integer> getList(String str){
		if(str == null) return new LinkedList<Integer>();
		String[] arr = str.split("@@");
		LinkedList<Integer> list = new LinkedList<Integer>();
		for (String s : arr) {
			if(AonStringUtils.isNumeric(s))
				list.add(Integer.parseInt(s));
		}
		return list;
	}
    
    public static IssueFilter getFilter(Domain domain, String userName, HashMap<String,String> parameters){
		Date from = AonDateUtils.getDateWithoutTime(new Date());
		AonDateUtils.addYears(from, -1);
		Date to = AonDateUtils.getDateWithoutTime(new Date());
		try {
			if(parameters.containsKey("from") && !parameters.get("from").equals(""))
				from = dateFormat.parse(parameters.get("from"));
			if(parameters.containsKey("to") && !parameters.get("to").equals(""))
				to = dateFormat.parse(parameters.get("to"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		to = AonDateUtils.addDays(to, 1);
		
		LinkedList<Integer> assignee = getList(parameters.get("asignee"));
		LinkedList<Integer> workgroup = getList(parameters.get("workgroup"));
	
		return new IssueFilter()
				.setTitle(parameters.get("title"))
				.setMine(parameters.get("mine"))
				.setAssignee(assignee)
				.setWorkgroup(workgroup)
				.setCreator(parameters.get("creator"))
				.setDirection(parameters.get("direction"))
				.setLabels(parameters.get("labels"))
				.setMentioned(parameters.get("mentioned"))
				.setMilestone(parameters.get("milestone"))
				.setSince(parameters.get("since"))
				.setSort(parameters.get("sort"))
				.setState(parameters.get("state"))
				.setPriority(parameters.get("priority"))
				.setType(parameters.get("type"))
				.setEnterprise(parameters.get("enterprise"))
				.setPerPage(Integer.parseInt(parameters.get("per_page")))
				.setPage(Integer.parseInt(parameters.get("page")))
				.setDateDiff(parameters.get("date_diff"))
				.setFrom(from)
				.setTo(to);
	}
}
