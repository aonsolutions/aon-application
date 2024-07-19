package net.aonsolutions.aon.api.servlet;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.json.ContractExtendedDataJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.AuxSalaryInfo;
import com.esferalia.aon.occam.api.model.ContractExtendedData;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.ContractExtendedDataProperties;
import com.esferalia.aon.occam.api.model.Properties.SalaryNewPortalProperties;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiContractServlet", urlPatterns = {"/ms/api/contract-api/*"})
public class ContractServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(ContractServlet.class.getName());
	
	public static final String CONTRACT_LIST = "/";
	public static final String CONTRACT_BY_ID = "/one/";
	public static final String CONTRACT_LIST_SIMPLIFIED = "/simple";
	public static final String CONTRACT_COUNT = "/count";
	public static final String EMPLOYEE_SALARY = "/salary";
	public static final String EMPLOYEE_SALARY_COUNT = "/salary_count";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			Object object = new AonRouting(api)
				.addRoute(CONTRACT_LIST, ContractServlet::getContractList)
				.addRoute(CONTRACT_BY_ID, ContractServlet::getContractById)
				.addRoute(CONTRACT_LIST_SIMPLIFIED, ContractServlet::getContractSimplifiedList)
				.addRoute(CONTRACT_COUNT, ContractServlet::getContractCount)
				.addRoute(EMPLOYEE_SALARY,ContractServlet::getEmployeeSalaries)
				.addRoute(EMPLOYEE_SALARY_COUNT, ContractServlet::getEmployeeSalariesCount)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONArray getContractList(AonApiData api) {
		LOGGER.info("GET LIST METHOD");
		JSONObject params = api.getData();
		JSONArray array = new JSONArray();
		PAYROLL.getContractExtendedDataStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
				f -> buildFilter(f, api),
				params.optInt(IJsonNames.PAGE), 
				params.optInt(IJsonNames.PER_PAGE)).forEach(element -> {
					array.put(ContractExtendedDataJSON.toJSON(element));
				});
		return array;
	}
	
	private static JSONArray getContractSimplifiedList(AonApiData api) {
		LOGGER.info("GET SIMPLIFIED LIST METHOD");
		JSONObject params = api.getData();
		JSONArray array = new JSONArray();
		PAYROLL.getContractSimplifiedDataStream(api.getDomain().getName() , api.getDomain().getId(), api.getUser().getLogin(),
				f -> buildFilter(f, api),
				params.optInt(IJsonNames.PAGE), 
				params.optInt(IJsonNames.PER_PAGE)).forEach(element -> {
					array.put(ContractExtendedDataJSON.toJSONSimple(element));
				});
		
		return array;
	}
	
	private static long getContractCount(AonApiData api) {
		LOGGER.info("GET COUNT METHOD");
		return PAYROLL.getContractCount(api.getDomain().getName() , api.getDomain().getId(), api.getUser().getLogin(),
				f -> buildFilter(f, api));
	}
	
	private static JSONArray getEmployeeSalaries(AonApiData api) {
		 JSONArray array = new JSONArray();
		 	Integer page = api.getData().optInt("page");
		 	Integer perPage = api.getData().optInt("per_page");
		    List<AuxSalaryInfo> salaryList = PAYROLL.getEmployeeSalary(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
		            f -> buildFilterSalariesNewPortal(f, api) , page, perPage);
		    for (AuxSalaryInfo s : salaryList) {
		        array.put(toJSONSalaryInfo(s));
		    }
		    return array;
	}
	
	private static long getEmployeeSalariesCount(AonApiData api) {
		LOGGER.info("GET COUNT METHOD");
		return PAYROLL.getEmployeeSalaryCount(api.getDomain().getName() , api.getDomain().getId(), api.getUser().getLogin(),
				f -> buildFilterSalariesNewPortal(f, api));
	}
	
	
	private static Filter buildFilter(ContractExtendedDataProperties properties, AonApiData api) {
		JSONObject params = api.getData();
		Filter filter = properties.getDomainProperty().eq(api.getDomain().getId());
		Boolean status = JsonUtils.getBoolean(params, IJsonNames.STATUS);
		Integer workplace = JsonUtils.getInteger(params, IJsonNames.WORKPLACE);
		Integer contractId = JsonUtils.getInteger(params, "contract");
		byte salaryType = JsonUtils.getByte(params, "salary_type");
		Integer auxSalaryType = (int) salaryType;
		Date to = JsonUtils.getDate(params, IJsonNames.TO);
		Date from = JsonUtils.getDate(params, IJsonNames.FROM);
		
//		if(name != null) {
//			filter = filter.and(properties.getPersonFullNameProperty().like("%"+name+"%"));
//		}
		if(!AonStringUtils.isEmpty(api.getData().optString("global"))) {
			filter = filter.and(properties.getNameProperty().like("%"+api.getData().optString("global")+"%"));
//					.or(properties.getDateStringProperty().like("%"+api.getData().optString("global")+"%")));
		}
		
		if(workplace != null) {
			filter = filter.and(properties.getWorkplaceProperty().eq(workplace));
		}
		if(status != null) {			
			if(Boolean.TRUE.equals(status)) {
				if(to != null) {
					filter = filter.and(properties.getEndDateProperty().ge(AonDateUtils.toSql(to)).or(properties.getEndDateProperty().isNull()));
				} else {
					filter = filter.and(properties.getEndDateProperty().ge(AonDateUtils.toSql(new Date())).or(properties.getEndDateProperty().isNull()));
				}
			} else {
				if(to != null) {
					filter = filter.and(properties.getEndDateProperty().le(AonDateUtils.toSql(to)));
				} else {
					filter = filter.and(properties.getEndDateProperty().le(AonDateUtils.toSql(new Date())));
				}
			}
		}
		if(from != null) {
			filter = filter.and(properties.getStartDateProperty().le(AonDateUtils.toSql(from)));
		}
		
		if(contractId != null) {
			filter = filter.and(properties.getIdProperty().eq(contractId));
		}
		
		if(auxSalaryType != null && contractId != null) {
			filter = filter.and(properties.getSalaryType().eq(salaryType));
		}
		return filter;
	}
	
	private static Filter buildFilterSalariesNewPortal(SalaryNewPortalProperties properties , AonApiData api) {
		JSONObject params = api.getData();
		Filter filter = properties.getDomainProperty().eq(api.getDomain().getId());
		byte salaryType = JsonUtils.getByte(params, "salary_type");
		Integer auxSalaryType = (int) salaryType;
		String contractIds = JsonUtils.getString(params, "contract_ids");

		if(!AonStringUtils.isEmpty(api.getData().optString("global"))) {
			filter = filter.and(properties.getNameProperty().like("%"+api.getData().optString("global")+"%")
					.or(properties.getDateStringProperty().like("%"+api.getData().optString("global")+"%")));
		}
		
		if(auxSalaryType != null) {
			filter = filter.and(properties.getSalaryType().eq(salaryType));
		}
				
		if(contractIds != null) {
			String [] stringIdArray = contractIds.split(",");
			Integer [] intIdArray = new Integer[stringIdArray.length];

			for (int i = 0; i < stringIdArray .length; i++) {
				intIdArray [i] = Integer.parseInt(stringIdArray[i]);			
			}
			filter = filter.and(properties.getContractProperty().in(intIdArray));

		}
		return filter;
	}
	
	private static JSONObject getContractById(AonApiData api) {
		LOGGER.info("GET BY ID METHOD");
//		   Integer contractId = 29037;
		   Integer contractId = api.getData().optInt("contractId");
		ContractExtendedData contract = PAYROLL.getContractByid(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
				f -> buildFilter(f, api) ,contractId);

		return toJSONContractId(contract);
	}
	
	private static JSONObject toJSONSalaryInfo(AuxSalaryInfo salaryInfo) {
		return new JSONObject().put("contract", salaryInfo.getContract())
			.put("domain", salaryInfo.getDomain())
			.put("employeeName", salaryInfo.getEmployeeName())
			.put("enterpriseId",salaryInfo.getEnterpriseId())
			.put("enterpriseName", salaryInfo.getEnterpriseName())
			.put("id", salaryInfo.getId())
			.put("totalDeduction", salaryInfo.getTotalDeduction())
			.put("totalLiquid", salaryInfo.getTotalLiquid())
			.put("totalPayment", salaryInfo.getTotalPayment())
			.put("type", salaryInfo.getType())
			.put("workplaceId", salaryInfo.getWorkplaceId())
			.put("workplaceName", salaryInfo.getWorkplaceName())
			.put("startDate", salaryInfo.getStartDate())
	        .put("endDate", salaryInfo.getEndDate())
	        .put("issue_date", salaryInfo.getIssueDate());
	}
	
	private static JSONObject toJSONContractId(ContractExtendedData data) {
		return new JSONObject()
		.put(IJsonNames.ID, data.getId())
		.put(IJsonNames.START_DATE, data.getStartDate())
		.put(IJsonNames.END_DATE, data.getEndDate())
		.put(IJsonNames.WORKPLACE, data.getWorkplace())
		.put(IJsonNames.NAME, data.getPersonName())
		.put("first_surname", data.getPersonFirstName())
		.put("second_surname", data.getPersonSecondName())
		.put(IJsonNames.TYPE, data.getContractType() != null ? data.getContractType().replace("\"", "") : "")
		.put(IJsonNames.DOCUMENT, data.getPersonDocument())
		.put("time", data.getTotalMarksLastMonth() != null ? data.getTotalMarksLastMonth().longValue() : 0)
		.put("quote_group", data.getQuoteGroup() != null ? data.getQuoteGroup().replace("\"", "") : "")
		.put("enterprise_ccc", data.getEnterpriseCCC())
		.put("social_security_number", data.getPersonSsNumber())
		.put("category", data.getCategoryDescription())
		.put("cno", data.getCno())
		.put("RLCE", data.getRlce());
	}
}
