package com.code.aon.webservice.common;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.OldTask;
import com.esferalia.aon.occam.api.model.task.IssueFilter;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Utils {
	
	private static final Logger LOGGER  = Logger.getLogger(Utils.class.getName());

	private Utils() {
		throw new IllegalAccessError("Utility class");
	}
	
	public static void giveBackData(HttpServletResponse resp, byte[] data, String name) throws ServletException, IOException{
		Integer length = data.length;
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
	        
		resp.addHeader("Content-Disposition","attachment; filename=\""+name +"\"");
		resp.setContentType("application/msexcel");
		
		if (length > 0 && length <= Integer.MAX_VALUE)
        	resp.setContentLength((int)length);
        ServletOutputStream out = resp.getOutputStream();
        resp.setBufferSize(32768);
        int bufSize = resp.getBufferSize();
        byte[] buffer = new byte[bufSize];
        BufferedInputStream bis = new BufferedInputStream(bais,bufSize);
        int bytes;
        while ((bytes = bis.read(buffer, 0, bufSize)) >= 0)
        	out.write(buffer, 0, bytes);
    
        bis.close();
        bais.close();
        out.flush();
        out.close();
	}
	
	public static void giveBack(HttpServletRequest req, HttpServletResponse resp,
			Object object, JSONObject meta) {
		try {
			String js = req.getParameter(MSG.CALLBACK);
			if(js != null){
				resp.setContentType("application/javascript; charset=utf-8");     
				PrintWriter out = resp.getWriter();
				out.print(js + "({" +"\"meta\":"+ meta +", \"data\":" + object +"});");
				out.flush();
			} else {
				resp.setContentType("application/json");     
				PrintWriter out = resp.getWriter();
				out.print(object);
				out.flush();
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
	public static String getMd5(String str){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
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
		StringBuilder title = new StringBuilder();
		String[] string = str.split(" ");
		for(Integer i = 0; i < string.length; i++){
			String s = string[i];
			while(s.length()>30){
				title.append(s.substring(0, 29)+ " ");
				s = s.substring(30);
			}
			title.append(s + " ");
		}
		return title.toString();
	}
	
	public static String checkString(String str){
		return new String(str.getBytes(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8") );
	}
	
	public static JSONObject getRequestJSON(HttpServletRequest req){
		String line = "";
		StringBuilder bld = new StringBuilder();
		try {
			while((line = req.getReader().readLine()) != null){
				bld.append(" " + line);
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		String s = bld.toString();
		if(s == null || s.isBlank()){
			s = "{}";
		}
		return new JSONObject(s);
	}
	
	public static String getStatusColor(OldTask task){
		TaskStatus taskStatus = TaskStatus.safeValueOf(task.getStatus());
		if(taskStatus.equals(TaskStatus.DELETED))
			return "gray";
		else if(taskStatus.equals(TaskStatus.IN_PROGRESS)
				|| taskStatus.equals(TaskStatus.PENDING)){
			if(task.getParent() != null){
				return "red";
			}
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
		if(local){
			url = url + "aon-aio/";
		}
		return url;
	}
	
    public static void addCorsHeader(HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        response.addHeader("Access-Control-Max-Age", "1728000");
    }

    private static LinkedList<Integer> getList(String str){
		if(str == null){
			return new LinkedList<>();
		}
		String[] arr = str.split("@@");
		LinkedList<Integer> list = new LinkedList<>();
		for (String s : arr) {
			if(AonStringUtils.isNumeric(s))
				list.add(Integer.parseInt(s));
		}
		return list;
	}
    
    public static IssueFilter getFilter(HashMap<String,String> parameters){
    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    	Date from = AonDateUtils.getDateWithoutTime(new Date());
		AonDateUtils.addYears(from, -1);
		Date to = AonDateUtils.getDateWithoutTime(new Date());
		try {
			if(parameters.containsKey("from") && !"".equals(parameters.get("from")))
				from = dateFormat.parse(parameters.get("from"));
			if(parameters.containsKey("to") && !"".equals(parameters.get("to")))
				to = dateFormat.parse(parameters.get("to"));
		} catch (ParseException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
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
