package com.code.aon.webservice.stat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.common.Utils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.stat.StatType;
import com.esferalia.aon.occam.api.model.stat.fee.FeeChartType;
import com.esferalia.aon.occam.api.model.stat.task.TaskChartType;
import com.esferalia.aon.occam.api.model.task.IssueFilter;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "StatServlet", urlPatterns = { "/stat/*",
												  "/aon_gwt_aio/ms/stat/*"})
public class StatServlet extends HttpServlet{
	private static final Logger LOGGER  = Logger.getLogger(StatServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp){
		LOGGER.info("Stat Servlet - GET METHOD");
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
				case "fee":
					object = getFeeStatData(domain, userName, params(req));
					break;
				default:
					break;
				}
				
				Utils.giveBack(req, resp, object, meta);
			}
		}
	}
	
	// -------------------- FEE STAT

	private JSONArray getFeeStatData(Domain domain, String userName, StatParams params) {
		params.setStatType(StatType.FEE).setChartType(FeeChartType.FEE_TYPE.value());
		return toJSON(AON.getStatData(domain.getName(), domain.getId(), userName, params));
	}
	
	// -------------------- TASK STAT
	
	private JSONArray getTaskStatData(Domain domain, String userName, String by, IssueFilter filter) {
		switch (by) {
		case "status":
			return getTaskStatData(domain, userName, TaskChartType.TASK_BY_STATUS, filter);
		case "type":
			return getTaskStatData(domain, userName, TaskChartType.TASK_BY_TYPE, filter);
		case "tag":
			return getTaskStatData(domain, userName, TaskChartType.TASK_BY_TAG, filter);
		case "schedule":
			return getTaskStatData(domain, userName, TaskChartType.TASK_BY_SCHEDULE, filter);
		case "day_of_week":
			return getTaskStatData(domain, userName, TaskChartType.TASK_BY_DAY_OF_WEEK, filter);
		case "month":
			return getTaskStatData(domain, userName, TaskChartType.TASK_BY_MONTH, filter);
		case "day":
			return getTaskStatData(domain, userName, TaskChartType.TASK_BY_DAY, filter);
		case "customer":
			return getTaskStatData(domain, userName, TaskChartType.TASK_BY_CUSTOMER, filter);
		default:
			return new JSONArray();
		}
	}
	
	private JSONArray getTaskStatData(Domain domain, String userName, TaskChartType chartType, IssueFilter filter){
		StatParams params = new StatParams()
				.setStatType(StatType.TASK)
				.setChartType(chartType.value())
				.setFrom(filter.getFrom()).setTo(filter.getTo())
				.setIssueFilter(filter);
		return toJSON(AON.getStatData(domain.getName(), domain.getId(), userName, params));
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

	@SuppressWarnings("unchecked")
	private StatParams params(HttpServletRequest req) {
		StatParams params = new StatParams();
		@SuppressWarnings("rawtypes")
		Map paramsMap = req.getParameterMap();
		HashMap<String, String[]> filterMap = new  HashMap<String, String[]>();
		filterMap.putAll(paramsMap);
		params.setFilterMap( filterMap );
		if(req.getParameter("from") != null && !"".equals(req.getParameter("from"))){
			params.setFrom(new Date(Long.parseLong(req.getParameter("from"))));
		} else params.setFrom(new Date());
		return params;
	}
	
	public static IssueFilter getFilter(Domain domain, String userName, HttpServletRequest req){
		Date from = AonDateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		from = AonDateUtils.addYears(from, -1);
		Date to = AonDateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		
		if(req.getParameter("from") != null && !MSG.EMPTY.equals(req.getParameter("from"))){
			from = AonDateUtils.simpleParse(req.getParameter("from"));
		}
		if(req.getParameter("to") != null && !MSG.EMPTY.equals(req.getParameter("to"))){
			to = AonDateUtils.simpleParse(req.getParameter("to"));
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
	
	public JSONArray toJSON(StatData<String, String, Double> statData) {
		JSONArray array = new JSONArray();
		statData.getMap().keySet().stream().forEach(row -> 
			statData.getMap().get(row).keySet().stream().forEach(col-> {
				JSONObject json = new JSONObject();
				json.put("row", row);
				json.put("column", col);
				json.put("quantity", statData.getMap().get(row).get(col));
				array.put(json);
			})
		);
		return array;
	}
}
