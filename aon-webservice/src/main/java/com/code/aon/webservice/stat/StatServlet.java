package com.code.aon.webservice.stat;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.issues.Utils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.stat.StatChartType;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.task.IssueFilter;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "StatServlet", urlPatterns = { "/stat/*",
												  "/aon_gwt_aio/stat/*"})
public class StatServlet extends HttpServlet{
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GET METHOD");
		String accessToken = req.getParameter("access_token");
		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1];
		String userName = pathInfo[2]; 
		String md5 = Utils.getMd5(userName+domainName);
		
		if(accessToken.equals(md5)){
			Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
			if(pathInfo.length > 3){
				Object object = new Object();
				JSONObject meta = new JSONObject();
				switch (pathInfo[3]) {
				case "task":
					object = getTaskStatData(domain, userName, pathInfo[4], getFilter(domain, userName, req));
					break;
				default:
					break;
				}
				String js = req.getParameter("callback");
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
			}
		}
	}
	
	// -------------------- TASK STAT
	
	private JSONArray getTaskStatData(Domain domain, String userName, String by, IssueFilter filter) {
		switch (by) {
		case "status":
			return getTaskStatData(domain, userName, StatChartType.TASK_BY_STATUS, filter);
		case "type":
			return getTaskStatData(domain, userName, StatChartType.TASK_BY_TYPE, filter);
		case "tag":
			return getTaskStatData(domain, userName, StatChartType.TASK_BY_TAG, filter);
		case "schedule":
			return getTaskStatData(domain, userName, StatChartType.TASK_BY_SCHEDULE, filter);
		case "day_of_week":
			return getTaskStatData(domain, userName, StatChartType.TASK_BY_DAY_OF_WEEK, filter);
		case "month":
			return getTaskStatData(domain, userName, StatChartType.TASK_BY_MONTH, filter);
		case "day":
			return getTaskStatData(domain, userName, StatChartType.TASK_BY_DAY, filter);
		default:
			return new JSONArray();
		}
	}
	
	private JSONArray getTaskStatData(Domain domain, String userName, StatChartType chartType, IssueFilter filter){
		StatParams params = new StatParams().setChartType(chartType)
				.setFrom(filter.getFrom()).setTo(filter.getTo())
				.setIssueFilter(filter);
		JSONArray array = new JSONArray();
		StatData<String, String, Double> statData = AON.getStatData(domain.getName(), domain.getId(), userName, params);
		statData.getMap().keySet().stream().forEach(row -> {
			statData.getMap().get(row).keySet().stream().forEach(col-> {
				JSONObject json = new JSONObject();
				json.put("row", row);
				json.put("column", col);
				json.put("quantity", statData.getMap().get(row).get(col));
				array.put(json);
			});
		});
		return array;
	}

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

	public static IssueFilter getFilter(Domain domain, String userName, HttpServletRequest req){
		Date from = new Date();from.setHours(0);
		AonDateUtils.addYears(from, -1);
		Date to = new Date();to.setHours(0);
		try {
			if(req.getParameter("from") != null && !req.getParameter("from").equals(""))
				from = dateFormat.parse(req.getParameter("from"));
			if(req.getParameter("to") != null && !req.getParameter("to").equals(""))
				to = dateFormat.parse(req.getParameter("to"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		to = AonDateUtils.addDays(to, 1);
		
		LinkedList<Integer> assignee = getList(req.getParameter("asignee"));
		LinkedList<Integer> workgroup = getList(req.getParameter("workgroup"));
	
		return new IssueFilter()
				.setTitle(req.getParameter("title"))
				.setMine(req.getParameter("mine"))
				.setAssignee(assignee)
				.setWorkgroup(workgroup)
				.setCreator(req.getParameter("creator"))
				.setDirection(req.getParameter("direction"))
				.setLabels(req.getParameter("labels"))
				.setMentioned(req.getParameter("mentioned"))
				.setMilestone(req.getParameter("milestone"))
				.setSince(req.getParameter("since"))
				.setSort(req.getParameter("sort"))
				.setState(req.getParameter("state"))
				.setPriority(req.getParameter("priority"))
				.setType(req.getParameter("type"))
				.setEnterprise(req.getParameter("enterprise"))
				.setPerPage(Integer.parseInt(req.getParameter("per_page")))
				.setPage(Integer.parseInt(req.getParameter("page")))
				.setDateDiff(req.getParameter("date_diff"))
				.setFrom(from)
				.setTo(to);
	}
	
}
