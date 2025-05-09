package net.aonsolutions.aon.api.servlet;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.json.CompanyJSON;
import com.esferalia.aon.occam.api.json.ContractExtendedDataJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.AonCompany;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.ContractExtendedDataProperties;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiContractServlet", urlPatterns = {"/ms/api/contract-api/*"})
public class ContractServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(ContractServlet.class.getName());
	
	public static final String CONTRACT_LIST = "/";
	public static final String CONTRACT_BY_ID = "/:id";
	public static final String ALL_CONTRACT_LIST = "/all";
	
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
				.addRoute(ALL_CONTRACT_LIST, ContractServlet::getAllContractList)
				.addRoute(CONTRACT_BY_ID, ContractServlet::getContractById)
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
				params.optInt(IJsonNames.PER_PAGE))
				.forEach(element -> {
					array.put(ContractExtendedDataJSON.toJSON(element));
				});
		return array;
	}

	private static JSONArray getAllContractList(AonApiData api) {
		LOGGER.info("GET ALL LIST METHOD");
		String token = api.getToken();
		JSONObject params = api.getData();
		JSONArray array = new JSONArray();
		List<String> schemas = AONContext.getSchemas();
		int limit = params.optInt(IJsonNames.LIMIT, Integer.MAX_VALUE);
		for (String schema: schemas) {
			PAYROLL.getContractExtendedDataStream(
					token, 
					schema, 
					props -> buildFilter(props, api, props.getDomainProperty().isNotNull()), 
					limit)
			.forEach(contract -> {
				JSONObject contractJSONObject = ContractExtendedDataJSON.toJSON(contract);
				contractJSONObject.put(IJsonNames.COMPANY, contract.getDomainName());
				array.put(contractJSONObject);
			});
			if ( array.length() >= limit  )
				break;
		}
		return array;
	}
	
	private static Filter buildFilter(ContractExtendedDataProperties properties, AonApiData api) {
		return buildFilter(properties, api, properties.getDomainProperty().eq(api.getDomain().getId()) );
	}
	
	private static Filter buildFilter(ContractExtendedDataProperties properties, AonApiData api, Filter filter) {
		JSONObject params = api.getData();
		String name = JsonUtils.getString(params, IJsonNames.NAME);
		Boolean status = JsonUtils.getBoolean(params, IJsonNames.STATUS);
		Date to = JsonUtils.getDate(params, IJsonNames.TO);
		Date from = JsonUtils.getDate(params, IJsonNames.FROM);
		Integer workplace = JsonUtils.getInteger(params, IJsonNames.WORKPLACE);
		if(workplace != null) {
			filter = filter.and(properties.getWorkplaceProperty().eq(workplace));
		}
		if(name != null) {
			String[] words = name.split("\\s");
			for (String word : words) {
				filter = filter.and(properties.getPersonFullNameProperty().like("%"+word+"%"));
			}
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
		return filter;
	}

	private static JSONObject getContractById(AonApiData api) {
		LOGGER.info("GET BY ID METHOD");
		return new JSONObject();
	}
	
}
