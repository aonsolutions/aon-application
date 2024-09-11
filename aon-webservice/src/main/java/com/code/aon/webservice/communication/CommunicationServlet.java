package com.code.aon.webservice.communication;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Properties.AttachProperties;
import com.esferalia.aon.occam.api.model.Properties.DataResponseProperties;
import com.esferalia.aon.occam.api.model.Properties.DeliveryProperties;
import com.esferalia.aon.occam.api.model.Properties.SalesProperties;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoicePropertiesOLD;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.SalesStatus;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.watson.server.AonDateUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(name = "CommunicationServlet", urlPatterns = { "/communication/*", "/aon_gwt_aio/ms/communication/*" })
public class CommunicationServlet extends HttpServlet {

	private static final Logger LOGGER = Logger.getLogger(CommunicationServlet.class.getName());
	
	final static String SUMMARY = "summary";
	final static String SERES = "seres";
	final static String INGENET = "ingenet";
	
	final static String OUTCOME_DELIVERY = "outcome_delivery";
	final static String OUTCOME_INVOICE = "outcome_invoice";
	final static String INCOME_SALES = "income_sales";
	final static String INCOME_INVOICE = "income_invoice";
	
	final static String INGENET_SALES = "ingenet_sales";
	final static String INGENET_DELIVERY = "ingenet_delivery";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Communication Servlet - GET METHOD");
		String accessToken = req.getParameter(MSG.ACCESS_TOKEN);
		String[] pathInfo = req.getPathInfo().split("/");
		String userName = pathInfo[2];
		String domainName = pathInfo[1];
		String md5 = Utils.getMd5(userName + domainName);

		if (accessToken.equals(md5)) {
			Domain domain = AON.getDomain(domainName, 1, userName, f -> f.getNameProperty().eq(domainName));
			if (pathInfo.length > 3) {
				Object object = new Object();
				JSONObject meta = new JSONObject();
				switch (pathInfo[3]) {
				case SUMMARY:
					object = getSummary(domain, userName, req);
					break;
				case SERES:
					if(pathInfo.length > 4){
						if (OUTCOME_DELIVERY.equals(pathInfo[4])) {
							object = getOutcomeDelivery(domain, userName, req);
						} else if (OUTCOME_INVOICE.equals(pathInfo[4])) {
							object = getOutcomeInvoice(domain, userName, req);
						} else if (INCOME_SALES.equals(pathInfo[4])) {
							object = getIncomeSales(domain, userName, req);
						} else if (INCOME_INVOICE.equals(pathInfo[4])) {
							object = getIncomeInvoice(domain, userName, req);
						}
						if ("ftp".equals(pathInfo[4])) {
							if(pathInfo.length > 5){
								if (OUTCOME_DELIVERY.equals(pathInfo[5])) {
									// TODO seres ftp - outcome delivery 
								} else if (OUTCOME_INVOICE.equals(pathInfo[5])) {
									// TODO seres ftp - outcome invoice
								} else if (INCOME_SALES.equals(pathInfo[5])) {
									// TODO seres ftp - income sales
								} else if (INCOME_INVOICE.equals(pathInfo[5])) {
									// TODO seres ftp - income invoice
								}
							}		
						}
					}
					break;
				case INGENET:
					if(pathInfo.length > 4){
						if (MSG.DELIVERY.equals(pathInfo[4])) {
							object = getIngenetDeliveryAttach(domain, userName, req);
						} else if (MSG.SALES.equals(pathInfo[4])) {
							object = getIngenetSales(domain, userName, req);
						}
					}
					break;
				case "history":
					object = getHistory(domain, userName);
					break;
				case "history_detail":
					object = getHistoryDetail(domain, userName, Integer.parseInt(req.getParameter("id")));
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
		LOGGER.info("Communication Servlet - POST METHOD");
		
//		JSONObject json = Utils.getRequestJSON(req);
		
		Map<String, String[]> filterMap = req.getParameterMap();
		String[] idList = null;
		if (filterMap.containsKey("id_list")) {
			idList = filterMap.get("id_list");
		} else if (filterMap.containsKey("?id_list")) {
			idList = filterMap.get("?id_list");
		}
		

		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];
		
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		if(pathInfo.length > 3){				
			Object object = new Object();
			if(SERES.equals(pathInfo[3])){
				
			} else if(INGENET.equals(pathInfo[3])){
				if(pathInfo.length > 4){
					if(MSG.SALES.equals(pathInfo[4])){
						if(pathInfo.length > 5){
							if("reopen".equals(pathInfo[5])){
								object = reopenIngenetSales(domain, userName, idList);
							} 
						}
					}
				}
			}
			
			resp.setContentType("application/json;charset=UTF-8");
			Utils.addCorsHeader(resp);
			PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
			os.println(object.toString());
			os.flush();
			os.close();
		}
	}
	
	
	private Map<String, String[]> getFilterMap(HttpServletRequest req) {
		return req.getParameterMap();
	}

	private JSONArray getSummary(Domain domain, String login, HttpServletRequest req) {
		JSONArray array = new JSONArray();
//		JSONArray outcome = getOutcomeAll(domain, login, req);
//		JSONArray income = getIncomeAll(domain, login, req);
		JSONArray ingenetSales = getIngenetSalesAll(domain, login, req);
		JSONArray ingenetDelivery = getIngenetDeliveryAll(domain, login, req);
		
//		for (int i = 0; i < outcome.length(); i++) 
//	        array.put(outcome.get(i));
//		for (int i = 0; i < income.length(); i++) 
//	        array.put(income.get(i));
		for (int i = 0; i < ingenetSales.length(); i++) 
	        array.put(ingenetSales.get(i));
		for (int i = 0; i < ingenetDelivery.length(); i++) 
	        array.put(ingenetDelivery.get(i));

		return array;
	}

	// TODO getLastDataResponseDetailStream cambiar..
	private JSONArray getIngenetSalesAll(Domain domain, String login, HttpServletRequest req) {
		Map<String, String[]> filterMap = getFilterMap(req);
		JSONArray array = new JSONArray();
		
		Supplier<Stream<DataResponseDetail>> responseDetailSupplier = () -> AON.getLastDataResponseDetailStream(domain.getName(), domain.getId(), login,
				f -> dataResponseFilter(domain, filterMap, f, DataResponseSource.INGENET_SALES));
		List<Integer> dataAttachIds = responseDetailSupplier.get().map(DataResponseDetail::getId).collect(Collectors.toList());
		List<Integer> orphansIds = responseDetailSupplier.get().filter(o -> o.getDataValue().equals("PENDING"))
				.map(DataResponseDetail::getId).collect(Collectors.toList());
		List<Integer> errorIds = responseDetailSupplier.get().filter(o -> o.getDataValue().equals("ERROR"))
				.map(DataResponseDetail::getId).collect(Collectors.toList());
		
		array.put(new JSONObject()
				.put("label", INGENET_SALES)
				.put("quantity", dataAttachIds.size())
				.put("pending", orphansIds.size())
				.put("error", errorIds.size())
		);

		return array;
	}
	
	private JSONArray getIngenetDeliveryAll(Domain domain, String login, HttpServletRequest req) {
		Map<String, String[]> filterMap = getFilterMap(req);
		JSONArray array = new JSONArray();
		
		Supplier<Stream<Attach>> dataAttachStreamSupplier = () -> AON
				.getAttachStream(domain.getName(), domain.getId(), login,
						f -> dataAttachAllFilter(domain, filterMap, f)
								.and(f.getSourceTypeProperty().eq(DataAttachSource.INGENET.value())),
						AttachType.DATA, false);
		
		List<Integer> dataAttachIds = dataAttachStreamSupplier.get().map(Attach::getId).collect(Collectors.toList());
		List<Integer> orphansIds = dataAttachStreamSupplier.get().filter(o -> o.getSourceBatch() == null)
				.map(Attach::getId).collect(Collectors.toList());
		List<Integer> errorIds = dataAttachStreamSupplier.get().filter(o -> o.getType() == DataAttachType.RESPONSE_ERROR.value())
				.map(Attach::getId).collect(Collectors.toList());
		
		array.put(new JSONObject()
				.put("label", INGENET_DELIVERY)
				.put("quantity", dataAttachIds.size())
				.put("pending", orphansIds.size())
				.put("error", errorIds.size())
		);

		return array;
	}
	
	private JSONArray getOutcomeDelivery(Domain domain, String login, HttpServletRequest req){
		Map<String, String[]> filterMap = getFilterMap(req);
		JSONArray array = new JSONArray();
		
		Integer[] ediRegistryIds = getEdiActiveRegistry(domain, login);
		
    	if(req.getParameterMap().containsKey("seres")){
    		Date from = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.FROM)[0])));
			List<DataResponse> dataResponseList = AON
					.getDataResponseStream(domain.getName(), domain.getId(), login, DataResponseSource.SERES_DELIVERY,
							f -> f.getDomainProperty().eq(domain.getId())
									.and(f.getSourceProperty().eq(DataResponseSource.SERES_DELIVERY.value())
											.and(f.getIssueDateProperty().ge(AonDateUtils.toSql(from)))))
					.collect(Collectors.toList());
    		
    		AON.getDeliveryStream(domain.getName(), domain.getId(), login,
    				f -> deliveryFilter(domain, ediRegistryIds, filterMap, f))
    		.forEach(o -> array.put(toSeresFileJSON(o, dataResponseList)));
    	}
    	return array;
	}
	
	private JSONArray getOutcomeInvoice(Domain domain, String login, HttpServletRequest req){
		Map<String, String[]> filterMap = getFilterMap(req);
		JSONArray array = new JSONArray();
		
		Integer[] ediRegistryIds = getEdiActiveRegistry(domain, login);
		
    	if(req.getParameterMap().containsKey("seres")){
    		
			Date from = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.FROM)[0])));
			List<DataResponse> dataResponseList = AON
					.getDataResponseStream(domain.getName(), domain.getId(), login, DataResponseSource.SERES_INVOICE,
							f -> f.getDomainProperty().eq(domain.getId())
									.and(f.getSourceProperty().eq(DataResponseSource.SERES_INVOICE.value())
											.and(f.getIssueDateProperty().ge(AonDateUtils.toSql(from)))))
					.collect(Collectors.toList());
    		
    		AON.getInvoiceList(domain.getName(), domain.getId(), login, 	
        			f -> saleInvoiceFilter(domain, ediRegistryIds, filterMap, f))
	    		.forEach(o -> {
	    			JSONObject json = toSeresFileJSON(o, dataResponseList);
	    			array.put(json);
	    		});
    	}
    	return array;
    }
	
	private JSONArray getIncomeSales(Domain domain, String login, HttpServletRequest req) {
		Map<String, String[]> filterMap = getFilterMap(req);
		JSONArray array = new JSONArray();

		Integer[] ediRegistryIds = getEdiActiveRegistry(domain, login);

		if (req.getParameterMap().containsKey("seres")) {
			AON.getSalesStream(domain.getName(), domain.getId(), login,
					f -> salesFilter(domain, ediRegistryIds, filterMap, f)).forEach(o -> array.put(toSeresFileJSON(o)));
		}
		return array;
	}
	
	private JSONArray getIncomeInvoice(Domain domain, String login, HttpServletRequest req) {
		Map<String, String[]> filterMap = getFilterMap(req);
		JSONArray array = new JSONArray();

		Integer[] ediRegistryIds = getEdiActiveRegistry(domain, login);

		if (req.getParameterMap().containsKey("seres")) {
			AON.getInvoiceList(domain.getName(), domain.getId(), login,
					f -> saleInvoiceFilter(domain, ediRegistryIds, filterMap, f))
					.forEach(o -> array.put(toSeresFileJSON(o, null)));
		}
		return array;
	}

	private JSONArray getIngenetDeliveryAttach(Domain domain, String login, HttpServletRequest req) {
		Map<String, String[]> filterMap = getFilterMap(req);
		JSONArray array = new JSONArray();
		AON.getAttachStream(domain.getName(), domain.getId(), login,
				f -> dataAttachFilter(domain, filterMap, f)
						.and(f.getSourceTypeProperty().eq(DataAttachSource.INGENET.value())),
				AttachType.DATA, false).forEach(o -> array.put(toAttachJSON((o))));
		return array;
	}

	
	// TODO getLastDataResponseDetailStream cambiar..
	private JSONArray getIngenetSales(Domain domain, String login, HttpServletRequest req) {
		Map<String, String[]> filterMap = getFilterMap(req);
		JSONArray array = new JSONArray();
		
		Map<Integer, String> statusMap = new HashMap<>();
		AON.getLastDataResponseDetailStream(domain.getName(), domain.getId(), login,
				f -> dataResponseFilter(domain, filterMap, f, DataResponseSource.INGENET_SALES))
			.forEach(detail -> {
				statusMap.put(detail.getDataResponse(), detail.getDataValue());
			});
		
		AON.getDataResponseStream(domain.getName(), domain.getId(), login,
				DataResponseSource.INGENET_SALES,
				f -> dataResponseFilter(domain, filterMap, f, DataResponseSource.INGENET_SALES))
				.forEach(o -> array.put(toSeresFileJSON(o, statusMap)));
		
		return array;
	}
	
	private JSONArray getHistory(Domain domain, String login) {
		JSONArray array = new JSONArray();

		AON.getDataResponseStream(domain.getName(), domain.getId(), login, null,
				f -> f.getDomainProperty().eq(domain.getId())
						.and(f.getSourceProperty().eq(DataResponseSource.SERES_DELIVERY.value())))
				.sorted((e1, e2) -> e2.getCreationDate().compareTo(e1.getCreationDate())).forEach(r -> {
					array.put(ToJSON.dataResponseToJSON(r));
				});
		return array;
	}

	private JSONArray getHistoryDetail(Domain domain, String login, Integer id) {
		JSONArray array = new JSONArray();
		Integer[] ids = AON
				.getDataResponseStream(domain.getName(), domain.getId(), login, null,
						f -> f.getDomainProperty().eq(domain.getId())
								.and(f.getSourceProperty().eq(DataResponseSource.SERES_INVOICE.value()))
								.and(f.getDetailVariableProperty().eq("send"))
								.and(f.getDetailValueProperty().eq(id.toString())))
				.map(m -> m.getSourceId()).toArray(Integer[]::new);

		AON.getInvoiceStream(domain.getName(), domain.getId(), login, f -> f.getIdProperty().in(ids)).forEach(r -> {
			array.put(ToJSON.invoiceToJSON(r));
		});
		return array;
	}
	
	private Integer[] getEdiActiveRegistry(Domain domain, String login) {
		Integer[] ids = AON.getRegistryNoteStream(domain.getName(), domain.getId(), login,
						f -> f.getDomainProperty().eq(domain.getId())
								.and(f.getDescriptionProperty().eq("EDI_ACTIVE"))
								.and(f.getCommentsProperty().eq("true")))
				.map(m -> m.getRegistry()).toArray(Integer[]::new);
		return ids;
	}
	
	// TODO getLastDataResponseDetailStream cambiar..
	private Object reopenIngenetSales(Domain domain, String userName, String[] idList) {
		List<Integer> _idList = Arrays.asList(idList).stream().map(o -> Integer.parseInt(o))
				.collect(Collectors.toCollection(LinkedList::new));
		for(Integer id: _idList) {
			// restore sales status to PENDING
			DataResponse response = AON.getDataResponse(domain.getName(),
					domain.getId(), userName, f ->
						f.getDomainProperty().eq(domain.getId())
						.and(f.getSourceProperty().eq(DataResponseSource.INGENET_SALES.value()))
						.and(f.getIdProperty().eq(id)));
			Options options = new Options().setFull(true);
			Sales sales = AON.getSales(domain.getName(), domain.getId(), userName,
					f -> f.getIdProperty().eq(response.getSourceId()), options);
			sales.setStatus(SalesStatus.PENDING);
			AON.saveSales(domain, userName, sales);
			
			// add new ingenet status -> REOPENED
			DataResponseDetail lastDetail = AON.getLastDataResponseDetailStream(domain.getName(), domain.getId(), userName, f->f.getIdProperty().eq(id)).findFirst().orElse(null);
			if("PENDING".equals(lastDetail.getDataValue())) {
				AON.deleteDataResponseDetail(domain.getName(), domain.getId(), userName, f->f.getIdProperty().eq(lastDetail.getId()));
				AON.deleteDataResponse(domain.getName(), domain.getId(), userName, f->f.getIdProperty().eq(id));
			} else {
				DataResponseDetail detail = new DataResponseDetail();
				detail.setDomain(domain.getId());
				detail.setDataResponse(id);
				detail.setDataVariable("STATUS");
				detail.setDataValue("REOPENED");
				AON.insertDataResponseDetail(domain.getName(), domain.getId(), userName, detail);
			}
		}
		return ToJSON.objectToJSON(idList.length, "reopen");
	}
	
	
	/**
	 * 
	 * JSON transform
	 * 
	 */
	
	public static JSONObject toSeresFileJSON(Invoice invoice, List<DataResponse> list) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, invoice.getId());
		json.put("registry_name", invoice.getRegistryName());
		json.put("address", invoice.getAddress().getFullAddress());
		json.put("reference_code", invoice.getReferenceCode());
		json.put("date", AonDateUtils.format(invoice.getTaxDate(), "dd-MM-yyyy"));
		
		if(list!=null) {
			long count = list.stream().filter(dr -> dr.getSourceId().equals(invoice.getId())).count();
			if(count > 0)
				json.put("status", "Enviado" );
			else
				json.put("status", "Pendiente" );
		}
		// TODO invoice status
//		json.put("status", "Error" );
//		json.put("status", "Aceptado" );
//		json.put("status", "Rechazado" );
		return json;
	}
	
	public static JSONObject toSeresFileJSON(Sales sales) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, sales.getId());
		json.put("registry_name", sales.getCustomer().getName());
		json.put("reference_code", sales.getSeries()+"/"+sales.getNumber());
		json.put("date", AonDateUtils.format(sales.getIssueDate(), "dd-MM-yyyy"));
		// TODO sales status
		json.put("status", "Pendiente" );
//		json.put("status", "Enviado" );
//		json.put("status", "Error" );
//		json.put("status", "Aceptado" );
//		json.put("status", "Rechazado" );
		return json;
	}
	
	public static JSONObject toSeresFileJSON(Delivery delivery, List<DataResponse> list) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, delivery.getId());
		json.put("registry_name", delivery.getCustomerName());
		json.put("reference_code", delivery.getReferenceCode());
		json.put("date", AonDateUtils.format(delivery.getIssueTime(), "dd-MM-yyyy"));
		if(list!=null) {
			long count = list.stream().filter(dr -> dr.getSourceId().equals(delivery.getId())).count();
			if(count > 0)
				json.put("status", "Enviado" );
			else
				json.put("status", "Pendiente" );
		}
		// TODO delivery status
//		json.put("status", "Error" );
//		json.put("status", "Aceptado" );
//		json.put("status", "Rechazado" );
		return json;
	}
	
	public static JSONObject toAttachJSON(Attach attach) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, attach.getId());
		json.put("source", attach.getSourceType());
		json.put("source_id", attach.getSourceBatch());
		json.put("description", attach.getDescription());
		json.put("type", attach.getType());
		json.put("creation_date", AonDateUtils.format(attach.getCreationDate(), "dd-MM-yyyy HH:mm:ss"));
		return json;
	}
	
	public static JSONObject toSeresFileJSON(DataResponse dataResponse, Map<Integer, String> statusMap) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, dataResponse.getId());
		json.put("registry_name", dataResponse.getCode().split(";")[1]);
		json.put("reference_code", dataResponse.getCode().split(";")[0]);
		json.put("date", AonDateUtils.format(dataResponse.getResponseDate(), "dd-MM-yyyy"));
		json.put("status", statusMap.get(dataResponse.getId()) );
		return json;
	}
	
	/**
	 * 
	 * FILTER
	 * 
	 */
	
	private void fillPaginationFilter(Filter filter, Map<String, String[]> filterMap) {
		if (filterMap.containsKey("page") && filterMap.containsKey("per_page")) {
			Integer page =  filterMap.containsKey("page") ? Integer.parseInt(filterMap.get("page")[0]) : 1;
			Integer perPage = filterMap.containsKey("per_page") ? Integer.parseInt(filterMap.get("per_page")[0]) : 40;
			filter = filter.page(page).perPage(perPage);
		} else {
			filter = filter.page(1).perPage(40);
		}
		// TODO pagination
		filter = filter.page(null).perPage(null);
	}
	
	
	private Filter deliveryFilter(Domain domain, Integer[] ediRegistryIds, Map<String, String[]> filterMap, DeliveryProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		if(ediRegistryIds!=null && ediRegistryIds.length>0)
			filter = filter.and(f.getCustomerProperty().in(ediRegistryIds));
		
		if (filterMap.containsKey(MSG.FROM)) {
			Date date = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.FROM)[0])));
			filter = filter.and(f.getIssueTimeProperty().ge(AonDateUtils.toTimestamp(date)));
		}
		fillPaginationFilter(filter, filterMap);
		return filter;
	}

	
	private Filter saleInvoiceFilter(Domain domain, Integer[] ediRegistryIds, Map<String, String[]> filterMap, InvoicePropertiesOLD f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		filter = filter.and(f.getTypeProperty().eq(InvoiceType.SALES.value()));
		if(ediRegistryIds!=null && ediRegistryIds.length>0)
			filter = filter.and(f.getRegistryProperty().in(ediRegistryIds));
		
		if (filterMap.containsKey(MSG.FROM)) {
			Date date = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.FROM)[0])));
			filter = filter.and(f.getTaxDateProperty().ge(AonDateUtils.toTimestamp(date)));
		}
		fillPaginationFilter(filter, filterMap);
		return filter;
	}
	
	private Filter salesFilter(Domain domain, Integer[] ediRegistryIds, Map<String, String[]> filterMap, SalesProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		if(ediRegistryIds!=null && ediRegistryIds.length>0)
			filter = filter.and(f.getCustomerProperty().in(ediRegistryIds));
		
		if (filterMap.containsKey(MSG.FROM)) {
			Date date = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.FROM)[0])));
			filter = filter.and(f.getIssueDateProperty().ge(AonDateUtils.toSql(date)));
		}
		fillPaginationFilter(filter, filterMap);
		return filter;
	}
	
	private Filter dataResponseFilter(Domain domain, Map<String, String[]> filterMap, DataResponseProperties f, DataResponseSource source) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if(source!=null)
			filter = filter.and(f.getSourceProperty().eq(source.value()));
		
		if (filterMap.containsKey(MSG.FROM)) {
			Date date = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.FROM)[0])));
			filter = filter.and(f.getIssueDateProperty().ge(AonDateUtils.toSql(date)));
		}
		
		fillPaginationFilter(filter, filterMap);
		return filter;
	}
	
	private Filter dataAttachAllFilter(Domain domain, Map<String, String[]> filterMap, AttachProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if (filterMap.containsKey(MSG.FROM)) {
			Date date = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.FROM)[0])));
			filter = filter.and(f.getCreationDateTimeStampProperty().ge(AonDateUtils.toTimestamp(date)));
		}
		
		fillPaginationFilter(filter, filterMap);
		return filter;
	}
	
	private Filter dataAttachFilter(Domain domain, Map<String, String[]> filterMap, AttachProperties f) {
		Filter filter = dataAttachAllFilter(domain, filterMap, f);
		
		ArrayList<Byte> statuses = new ArrayList<>();
		if (filterMap.containsKey("pending") && new Boolean(filterMap.get("pending")[0]))
			statuses.add(DataAttachType.REQUEST.value());
		if (filterMap.containsKey("processed") && new Boolean(filterMap.get("processed")[0]))
			statuses.add(DataAttachType.RESPONSE_OK.value());
		if (filterMap.containsKey("error") && new Boolean(filterMap.get("error")[0]))
			statuses.add(DataAttachType.RESPONSE_ERROR.value());
		if(statuses.size()>0)
			filter = filter.and(f.getTypeProperty().in(statuses.toArray(new Byte[statuses.size()])));
		
		return filter;
	}


}
