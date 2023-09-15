package net.aonsolutions.aon.api.servlet;

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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.code.aon.jaas.auth.util.Util;
import com.esferalia.aon.occam.api.model.task.IssueFilter;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.ewok.IConstants;

public class Utils {
	
	private static final Logger LOGGER  = Logger.getLogger(Utils.class.getName());

	private Utils() {
		throw new IllegalAccessError("Utility class");
	}
	
	public static void giveBack(HttpServletRequest req, HttpServletResponse resp,
			Object object, JSONObject meta) {
		try {
			String js = req.getParameter(IConstants.CALLBACK);
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
	
	public static String checkString(String str) {
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
		String s = checkString(bld.toString());
		if(s == null || s.isBlank()){
			s = "{}";
		}
		return new JSONObject(s);
	}
	
	public static JSONObject getParamsJSON(ServletRequest req) {
	    JSONObject jsonObj = new JSONObject();
	    @SuppressWarnings("unchecked")
		Map<String,String[]> params = req.getParameterMap();
	    for (Map.Entry<String,String[]> entry : params.entrySet()) {
	      String v[] = entry.getValue();
	      Object o = (v.length == 1) ? v[0] : v;
	      jsonObj.put(entry.getKey(), o);
	    }
	    return jsonObj;
	}
	
	public static String getStatusColor(Task task){
		if(task.getStatus().equals(TaskStatus.DELETED))
			return "gray";
		else if(task.getStatus().equals(TaskStatus.IN_PROGRESS)
				|| task.getStatus().equals(TaskStatus.PENDING)){
			if(task.getParent() != null){
				return "red";
			}
			else return "green";
		}
		else if(task.getStatus().equals(TaskStatus.FINISHED))
			return "black";
		else if(task.getStatus().equals(TaskStatus.FAQ))
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
    	//response.addHeader("Access-Control-Allow-Credentials", "true");
    	response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "*");//X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
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
    
    public static String createPasswordHash(String username, String password) {
		String hashAlgorithm="SHA";
		String hashEncoding="BASE64";
	    String passwordHash = Util.createPasswordHash(hashAlgorithm, hashEncoding, null, username, password);
	    return passwordHash;
	}
    
	public static boolean isEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+ 
                "[a-zA-Z0-9_+&*-]+)*@" + 
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" + 
                "A-Z]{2,7}$";
		Pattern pat = Pattern.compile(emailRegex); 
		if (AonStringUtils.isBlank(email)) 
			return false; 
		return pat.matcher(email).matches() || email.contains("@aon.solutions");
	}
	
	public static String generatePassword() {
		return PasswordGenerator.getPassword(
				PasswordGenerator.MINUSCULAS +
				PasswordGenerator.MAYUSCULAS + 
				PasswordGenerator.NUMEROS, 10);
	}
	
	public static class PasswordGenerator {

		public static final String NUMEROS = "0123456789";

		public static final String MAYUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		public static final String MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";

		public static String getPinNumber() {
			return getPassword(NUMEROS, 4);
		}

		public static String getPassword() {
			return getPassword(8);
		}

		public static String getPassword(int length) {
			return getPassword(NUMEROS + MAYUSCULAS + MINUSCULAS, length);
		}

		public static String getPassword(String key, int length) {
			String pswd = "";

			for (int i = 0; i < length; i++) {
				pswd += key.charAt((int) (Math.random() * key.length()));
			}

			return pswd;
		}
	}
}
