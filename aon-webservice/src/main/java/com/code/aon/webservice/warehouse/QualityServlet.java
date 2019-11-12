package com.code.aon.webservice.warehouse;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectForUpdateStep;
import org.jooq.SelectOffsetStep;
import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.CommonServlet;
import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.jooq.tables.DataResponse;
import com.esferalia.aon.jooq.tables.Income;
import com.esferalia.aon.jooq.tables.IncomeDetail;
import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.watson.server.AonDateUtils;

@SuppressWarnings("serial")
@WebServlet(name = "QualityServlet", urlPatterns = {"/quality/*",
												   "/aon_gwt_aio/ms/quality/*"})
public class QualityServlet extends HttpServlet{
	
	private static final Logger LOGGER  = Logger.getLogger(CommonServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Quality Servlet - GET METHOD");
		String accessToken = req.getParameter(MSG.ACCESS_TOKEN);
		String[] pathInfo = req.getPathInfo().split("/");
		String userName = pathInfo[2];
		String domainName = pathInfo[1]; 
		String md5 = Utils.getMd5(userName+domainName);
		
		if(accessToken.equals(md5)){
			Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
			if(pathInfo.length > 3){
				Object object = new Object();
				JSONObject meta = new JSONObject();
				switch (pathInfo[3]) {
				case MSG.DATA_RESPONSE: 
					object = getDataResponseList(domain, userName, req.getParameterMap());
					break;
				case MSG.PATURPAT: 
					object = getPaturpatQualityList(domain, userName, req.getParameterMap());
					break;
				case MSG.UDAPA: 
					object = getUdapaQualityList(domain, userName, req.getParameterMap());
					break;
				default:
					break;
				}
				Utils.giveBack(req, resp, object, meta);
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	
	private JSONArray getUdapaQualityList(Domain domain, String login, Map<String, String[]> map) {
		JSONArray array = new JSONArray();
		AON.getUdapaQualityStream(domain.getName(), domain.getId(), login, map)
		.forEach(r -> array.put(new JSONObject()
				.put(MSG.ID, r.getDataResponse().getId())
				.put(MSG.DOMAIN, r.getDataResponse().getDomain())
				.put(MSG.CODE, r.getDataResponse().getCode())
				.put(MSG.DATE,  r.getDataResponse().getResponseDate() != null ? AonDateUtils.dateTimeFormat(r.getDataResponse().getResponseDate()) : MSG.EMPTY)
				.put(MSG.CREATION_USER, r.getDataResponse().getCreationUser())
				.put(MSG.CREATION_DATE,  r.getDataResponse().getCreationDate() != null ? AonDateUtils.dateTimeFormat(r.getDataResponse().getCreationDate()) : MSG.EMPTY)
				.put(MSG.MODIFICATION_USER, r.getDataResponse().getModificationUser())
				.put(MSG.MODIFICATION_DATE, r.getDataResponse().getModificationDate() != null ? AonDateUtils.dateTimeFormat(r.getDataResponse().getModificationDate()) : MSG.EMPTY)
				.put(MSG.PRODUCT, r.getProduct())
				.put(MSG.PRODUCT, ToJSON.objectToJSON(r.getSupplier().getId(), r.getSupplier().getName()))));
		return array;
	}
	
	private JSONArray getPaturpatQualityList(Domain domain, String login, Map<String, String[]> map) {
		JSONArray array = new JSONArray();
		AON.getPaturpatQualityStream(domain.getName(), domain.getId(), login, map)
		.forEach(r -> array.put(new JSONObject()
				.put(MSG.ID, r.getDataResponse().getId())
				.put(MSG.DOMAIN, r.getDataResponse().getDomain())
				.put(MSG.CODE, r.getDataResponse().getCode())
				.put(MSG.DATE,  r.getDataResponse().getResponseDate() != null ? AonDateUtils.dateTimeFormat(r.getDataResponse().getResponseDate()) : MSG.EMPTY)
				.put(MSG.CREATION_USER, r.getDataResponse().getCreationUser())
				.put(MSG.CREATION_DATE,  r.getDataResponse().getCreationDate() != null ? AonDateUtils.dateTimeFormat(r.getDataResponse().getCreationDate()) : MSG.EMPTY)
				.put(MSG.MODIFICATION_USER, r.getDataResponse().getModificationUser())
				.put(MSG.MODIFICATION_DATE, r.getDataResponse().getModificationDate() != null ? AonDateUtils.dateTimeFormat(r.getDataResponse().getModificationDate()) : MSG.EMPTY)
				.put(MSG.PRODUCT, r.getProduct())
				.put(MSG.SUPPLIER, ToJSON.objectToJSON(1, "a"))));
		return array;
	}

	
	private JSONArray getDataResponseList(Domain domain, String login, Map<String, String[]> map) {
		AONContext ctx = null;
		try {
			ctx =AONContext.getAONContext(domain.getName(), domain.getId(), login);
			SelectConditionStep<Record> a = ctx.getDslContext().select()
			.from(DataResponse.DATA_RESPONSE).join(IncomeDetail.INCOME_DETAIL)
			.on(DataResponse.DATA_RESPONSE.SOURCE_ID.eq(IncomeDetail.INCOME_DETAIL.ID).and(DataResponse.DATA_RESPONSE.SOURCE.eq(DataResponseSource.QUALITY.value())))
			.join(Income.INCOME).on(Income.INCOME.ID.eq(IncomeDetail.INCOME_DETAIL.INCOME))
			.join(Registry.REGISTRY).on(Income.INCOME.SUPPLIER.eq(Registry.REGISTRY.ID))
			.where(DataResponse.DATA_RESPONSE.DOMAIN.eq(ctx.getDomainId()));
			
			if(map.containsKey(MSG.FROM)) {
				String from = map.get(MSG.FROM)[0];
				Date d = new Date(Long.parseLong(from));
				a = a.and(DataResponse.DATA_RESPONSE.RESPONSE_DATE.ge(AonDateUtils.toSql(d)));
			}
			
			if(map.containsKey(MSG.TO)){
				String to = map.get(MSG.TO)[0];
				Date d = new Date(Long.parseLong(to));
				a = a.and(DataResponse.DATA_RESPONSE.RESPONSE_DATE.le(AonDateUtils.toSql(d)));
			}
			
			if(map.containsKey(MSG.NUMBER)){
				Condition c = DataResponse.DATA_RESPONSE.CODE.eq(map.get(MSG.NUMBER)[0]);
				for(Integer i = 1; i < map.get(MSG.NUMBER).length ; i++){
					c = c.or(DataResponse.DATA_RESPONSE.CODE.eq(map.get(MSG.NUMBER)[i]));
				}
				a = a.and(c);
			} 
			
			if(map.containsKey(MSG.CODE)){
				a =  a.and(DataResponse.DATA_RESPONSE.CODE.like("%" + map.get(MSG.CODE)[0] + "%"));
			} 

			if(map.containsKey("source")){
				Condition c = DataResponse.DATA_RESPONSE.SOURCE_ID.eq(Integer.parseInt(map.get("source")[0]));				
				for(Integer i = 1; i < map.get("source").length ; i++){
					c = c.or(DataResponse.DATA_RESPONSE.SOURCE_ID.eq(Integer.parseInt(map.get("source")[i])));
				}
				a = a.and(c);
			}
			
			if(map.containsKey("supplier")){
				Condition c = Income.INCOME.SUPPLIER.eq(Integer.parseInt(map.get("supplier")[0]));				
				for(Integer i = 1; i < map.get("supplier").length ; i++){
					c = c.or(Income.INCOME.SUPPLIER.eq(Integer.parseInt(map.get("supplier")[i])));
				}
				a = a.and(c);
			}
			a.orderBy(DataResponse.DATA_RESPONSE.ID);

			SelectForUpdateStep<Record> b = null;
			SelectOffsetStep<Record> c = null;
			if(map.containsKey("per_page") && map.containsKey("page")) {
				String per_page = map.get("per_page")[0];
				Integer perPage = Integer.parseInt(per_page);
				String page_str = map.get("page")[0];
				Integer page = Integer.parseInt(page_str);
				b = a.limit(perPage).offset(perPage * (page -1));
			} else if(map.containsKey("per_page")){
				String per_page = map.get("per_page")[0];
				Integer perPage = Integer.parseInt(per_page);
				c = a.limit(perPage);
			}
			
			Result<Record> r = null;		
			if(c != null) r =  c.fetch();
			else if(b != null) r = b.fetch();
			else r = a.fetch();

			JSONArray array = new JSONArray();
			r.stream().forEach(h -> {
				array.put(new JSONObject()
				.put(MSG.ID, h.getValue(DataResponse.DATA_RESPONSE.ID))
				.put(MSG.DOMAIN, h.getValue(DataResponse.DATA_RESPONSE.DOMAIN))
				.put(MSG.NUMBER, h.getValue(DataResponse.DATA_RESPONSE.CODE))
				.put(MSG.ISSUE_DATE,  h.getValue(DataResponse.DATA_RESPONSE.RESPONSE_DATE) != null ? AonDateUtils.dateTimeFormat(h.getValue(DataResponse.DATA_RESPONSE.RESPONSE_DATE)) : "")
				.put(MSG.CREATION_USER, h.getValue(DataResponse.DATA_RESPONSE.CREATION_USER))
				.put(MSG.CREATION_DATE,  h.getValue(DataResponse.DATA_RESPONSE.CREATION_DATE) != null ? AonDateUtils.dateTimeFormat(h.getValue(DataResponse.DATA_RESPONSE.CREATION_DATE)) : "")
				.put(MSG.MODIFICATION_USER, h.getValue(DataResponse.DATA_RESPONSE.MODIFICATION_USER))
				.put(MSG.MODIFICATION_DATE, h.getValue(DataResponse.DATA_RESPONSE.MODIFICATION_DATE) != null ? AonDateUtils.dateTimeFormat(h.getValue(DataResponse.DATA_RESPONSE.MODIFICATION_DATE)) : "")
				.put("product", h.getValue(IncomeDetail.INCOME_DETAIL.DESCRIPTION))
				.put("supplier", ToJSON.objectToJSON(h.getValue(Income.INCOME.SUPPLIER), h.getValue(Registry.REGISTRY.NAME))));
			});
			return array;
			
		} finally {
			if(ctx != null) ctx.close();
		}
	}
	

}
