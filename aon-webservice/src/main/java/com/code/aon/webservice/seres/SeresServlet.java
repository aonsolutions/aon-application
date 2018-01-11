package com.code.aon.webservice.seres;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.AttachProperties;
import com.esferalia.aon.occam.api.model.Properties.DataResponseProperties;
import com.esferalia.aon.occam.api.model.Properties.DeliveryProperties;
import com.esferalia.aon.occam.api.model.Properties.SalesProperties;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.watson.server.AonDateUtils;

@SuppressWarnings("serial")
@WebServlet(name = "SeresServlet", urlPatterns = { "/seres/*", "/aon_gwt_aio/seres/*" })
public class SeresServlet extends HttpServlet {

	private static final Logger LOGGER = Logger.getLogger(SeresServlet.class.getName());
	
	final static String OUTCOME_DELIVERY = "outcome_delivery";
	final static String OUTCOME_INVOICE = "outcome_invoice";
	final static String INCOME_SALES = "income_sales";
	final static String INCOME_INVOICE = "income_invoice";
	final static String INGENET_DELIVERY = "ingenet_delivery";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Seres Servlet - GET METHOD");
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
				case "summary":
					object = getSummary(domain, userName, req);
					break;
				case OUTCOME_DELIVERY:
					object = getOutcomeDelivery(domain, userName, req);
					break;
				case OUTCOME_INVOICE:
					object = getOutcomeInvoice(domain, userName, req);
					break;
				case INCOME_SALES:
					object = getIncomeSales(domain, userName, req);
					break;
				case INCOME_INVOICE:
					object = getIncomeInvoice(domain, userName, req);
					break;
				case INGENET_DELIVERY:
					object = getIngenetDeliveryAttach(domain, userName, req);
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
		LOGGER.info("Seres Servlet - POST METHOD");
	}

	private JSONArray getSummary(Domain domain, String login, HttpServletRequest req) {
		JSONArray array = new JSONArray();
		JSONArray outcome = getOutcomeAll(domain, login, req);
		JSONArray income = getIncomeAll(domain, login, req);
		JSONArray ingenet = getIngenetAll(domain, login, req);
		
		for (int i = 0; i < outcome.length(); i++) 
	        array.put(outcome.get(i));
		for (int i = 0; i < income.length(); i++) 
	        array.put(income.get(i));
		for (int i = 0; i < ingenet.length(); i++) 
	        array.put(ingenet.get(i));

		return array;
	}
	
	private JSONArray getOutcomeAll(Domain domain, String login, HttpServletRequest req) {
		Map<String, String[]> filterMap = req.getParameterMap();
		JSONArray array = new JSONArray();
		
		Integer[] ediRegistryIds = getEdiActiveRegistry(domain, login);

		Supplier<Stream<Delivery>> deliveryStreamSupplier = () -> AON.getDeliveryStream(domain.getName(), domain.getId(), login,
				f -> deliveryFilter(domain, ediRegistryIds, filterMap, f));
		List<Integer> deliveryIds = deliveryStreamSupplier.get().map(Delivery::getId).collect(Collectors.toList());
		List<Integer> invoiceIds = AON.getInvoiceStream(domain.getName(), domain.getId(), login,
				f -> saleInvoiceFilter(domain, ediRegistryIds, filterMap, f)).map(Invoice::getId).collect(Collectors.toList());
		
		List<Integer> responseDeliveryIds = AON
				.getDataResponseStream(domain.getName(), domain.getId(), login, null,
						f -> f.getDomainProperty().eq(domain.getId())
								.and(f.getSourceProperty().eq(DataResponseSource.SERES_DELIVERY.value())))
				.sorted((e1, e2) -> e2.getCreationDate().compareTo(e1.getCreationDate()))
				.map(DataResponse::getSourceId).collect(Collectors.toList());
		List<Integer> responseInvoiceIds = AON
				.getDataResponseStream(domain.getName(), domain.getId(), login, null,
						f -> f.getDomainProperty().eq(domain.getId())
								.and(f.getSourceProperty().eq(DataResponseSource.SERES_INVOICE.value())))
				.sorted((e1, e2) -> e2.getCreationDate().compareTo(e1.getCreationDate()))
				.map(DataResponse::getSourceId).collect(Collectors.toList());
		
		array.put(new JSONObject()
				.put("label", OUTCOME_DELIVERY)
				.put("quantity", deliveryIds.size())
				.put("pending", deliveryIds.size() - responseDeliveryIds.size())
				.put("error", -1)
		);
		array.put(new JSONObject()
				.put("label", OUTCOME_INVOICE)
				.put("quantity", invoiceIds.size())
				.put("pending", invoiceIds.size() - responseInvoiceIds.size())
				.put("error", -1)
		);
		
		return array;
	}
	
	private JSONArray getIncomeAll(Domain domain, String login, HttpServletRequest req) {
		Map<String, String[]> filterMap = req.getParameterMap();
		JSONArray array = new JSONArray();
		
		Integer[] ediRegistryIds = getEdiActiveRegistry(domain, login);

		List<Integer> invoiceIds = AON.getInvoiceStream(domain.getName(), domain.getId(), login,
				f -> saleInvoiceFilter(domain, ediRegistryIds, filterMap, f)).map(Invoice::getId).collect(Collectors.toList());
		List<Integer> salesIds = AON.getSalesStream(domain.getName(), domain.getId(), login,
				f -> salesFilter(domain, ediRegistryIds, filterMap, f)).map(Sales::getId).collect(Collectors.toList());
		
		List<Integer> responseSalesIds = AON
				.getDataResponseStream(domain.getName(), domain.getId(), login, null,
						f -> f.getDomainProperty().eq(domain.getId())
								.and(f.getSourceProperty().eq(DataResponseSource.SERES_SALES.value())))
				.sorted((e1, e2) -> e2.getCreationDate().compareTo(e1.getCreationDate()))
				.map(DataResponse::getSourceId).collect(Collectors.toList());
		
		List<Integer> responseInvoiceIds = AON
				.getDataResponseStream(domain.getName(), domain.getId(), login, null,
						f -> f.getDomainProperty().eq(domain.getId())
								.and(f.getSourceProperty().eq(DataResponseSource.SERES_INVOICE.value())))
				.sorted((e1, e2) -> e2.getCreationDate().compareTo(e1.getCreationDate()))
				.map(DataResponse::getSourceId).collect(Collectors.toList());
		
		array.put(new JSONObject()
				.put("label", INCOME_SALES)
				.put("quantity", salesIds.size())
				.put("pending", salesIds.size() - responseSalesIds.size())
				.put("error", -1)
		);
		array.put(new JSONObject()
				.put("label", INCOME_INVOICE)
				.put("quantity", invoiceIds.size())
				.put("pending", invoiceIds.size() - responseInvoiceIds.size())
				.put("error", -1)
		);
		
		return array;
	}
	
	private JSONArray getIngenetAll(Domain domain, String login, HttpServletRequest req) {
		Map<String, String[]> filterMap = req.getParameterMap();
		JSONArray array = new JSONArray();
		
		Supplier<Stream<Attach>> dataAttachStreamSupplier = () -> AON
				.getAttachStream(domain.getName(), domain.getId(), login,
						f -> dataAttachFilter(domain, filterMap, f)
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
		Map<String, String[]> filterMap = req.getParameterMap();
		JSONArray array = new JSONArray();
		
		Integer[] ediRegistryIds = getEdiActiveRegistry(domain, login);
		
    	if(req.getParameterMap().containsKey("seres")){
    		AON.getDeliveryStream(domain.getName(), domain.getId(), login,
    				f -> deliveryFilter(domain, ediRegistryIds, filterMap, f))
    		.forEach(o -> array.put(toSeresFileJSON((o))));
    	}
    	return array;
	}
	
	private JSONArray getOutcomeInvoice(Domain domain, String login, HttpServletRequest req){
		Map<String, String[]> filterMap = req.getParameterMap();
		JSONArray array = new JSONArray();
		
		Integer[] ediRegistryIds = getEdiActiveRegistry(domain, login);
		
    	if(req.getParameterMap().containsKey("seres")){
    		
			Date from = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.FROM)[0])));
			List<DataResponse> dataResponselist = AON
					.getDataResponseStream(domain.getName(), domain.getId(), login, DataResponseSource.SERES_INVOICE,
							f -> f.getDomainProperty().eq(domain.getId())
									.and(f.getSourceProperty().eq(DataResponseSource.SERES_INVOICE.value())
											.and(f.getIssueDateProperty().ge(AonDateUtils.toSql(from)))))
					.collect(Collectors.toList());
    		
    		AON.getInvoiceList(domain.getName(), domain.getId(), login, 	
        			f -> saleInvoiceFilter(domain, ediRegistryIds, filterMap, f))
	    		.forEach(o -> {
	    			JSONObject json = toSeresFileJSON(o, dataResponselist);
//	    			json.put(MSG.SII_SENT, true);
//	    			json.put(MSG.SII, "emitida");
	    			array.put(json);
	    		});
    	}
    	return array;
    }
	
	private JSONArray getIncomeSales(Domain domain, String login, HttpServletRequest req){
		Map<String, String[]> filterMap = req.getParameterMap();
		JSONArray array = new JSONArray();
		
		Integer[] ediRegistryIds = getEdiActiveRegistry(domain, login);
		
		if(req.getParameterMap().containsKey("seres")){
    		AON.getSalesStream(domain.getName(), domain.getId(), login, 	
        			f -> salesFilter(domain, ediRegistryIds, filterMap, f))
	    		.forEach(o -> array.put(toSeresFileJSON(o)));
    	}
		return array;
	}
	
	private JSONArray getIncomeInvoice(Domain domain, String login, HttpServletRequest req){
		Map<String, String[]> filterMap = req.getParameterMap();
		JSONArray array = new JSONArray();
		
		Integer[] ediRegistryIds = getEdiActiveRegistry(domain, login);
		
		if(req.getParameterMap().containsKey("seres")){
    		AON.getInvoiceList(domain.getName(), domain.getId(), login, 	
        			f -> saleInvoiceFilter(domain, ediRegistryIds, filterMap, f))
	    		.forEach(o -> array.put(toSeresFileJSON(o, null)));
    	}
		return array;
	}

	private JSONArray getIngenetDeliveryAttach(Domain domain, String login, HttpServletRequest req){
		Map<String, String[]> filterMap = req.getParameterMap();
		JSONArray array = new JSONArray();
		
    	if(req.getParameterMap().containsKey("seres")){
    		
			AON.getAttachStream(domain.getName(), domain.getId(), login,
					f -> dataAttachFilter(domain, filterMap, f)
						.and(f.getSourceTypeProperty().eq(DataAttachSource.INGENET.value())),
					AttachType.DATA, false)
			.forEach(o -> array.put(toDeliveryAttachJSON((o))));
			
    	}
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
		Integer[] ids = AON
				.getRNoteStream(domain.getName(), domain.getId(), login,
						f -> f.getDomainProperty().eq(domain.getId())
								.and(f.getDescriptionProperty().eq("EDI_ACTIVE"))
								.and(f.getCommentsProperty().eq("true")))
				.map(m -> m.getRegistry()).toArray(Integer[]::new);
		return ids;
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
		json.put("address", invoice.getAddress());
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
//		json.put("status", "CORRECT" );
//		json.put("status", "FAIL" );
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
		return json;
	}
	
	public static JSONObject toSeresFileJSON(Delivery delivery) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, delivery.getId());
		json.put("registry_name", delivery.getCustomerName());
		json.put("reference_code", delivery.getReferenceCode());
		json.put("date", AonDateUtils.format(delivery.getIssueTime(), "dd-MM-yyyy"));
		// TODO delivery status
		json.put("status", "Pendiente" );
		return json;
	}
	
	public static JSONObject toDeliveryAttachJSON(Attach attach) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, attach.getId());
		json.put("source", attach.getSourceType());
		json.put("source_id", attach.getSourceBatch());
		json.put("description", attach.getDescription());
		json.put("type", attach.getType());
		json.put("creation_date", AonDateUtils.format(attach.getCreationDate(), "dd-MM-yyyy HH:mm:ss"));
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
	}
	
	
	private Filter deliveryFilter(Domain domain, Integer[] ediRegistryIds, Map<String, String[]> filterMap, DeliveryProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		fillPaginationFilter(filter, filterMap);
		if(ediRegistryIds!=null && ediRegistryIds.length>0)
			filter = filter.and(f.getCustomerProperty().in(ediRegistryIds));
		
		if (filterMap.containsKey(MSG.FROM)) {
			Date date = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.FROM)[0])));
			filter = filter.and(f.getIssueTimeProperty().ge(AonDateUtils.toTimestamp(date)));
		}
		return filter;
	}

	
	private Filter saleInvoiceFilter(Domain domain, Integer[] ediRegistryIds, Map<String, String[]> filterMap, InvoiceProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		filter = filter.and(f.getTypeProperty().eq(InvoiceType.SALES.value()));
		fillPaginationFilter(filter, filterMap);
		if(ediRegistryIds!=null && ediRegistryIds.length>0)
			filter = filter.and(f.getRegistryProperty().in(ediRegistryIds));
		
		if (filterMap.containsKey(MSG.FROM)) {
			Date date = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.FROM)[0])));
			filter = filter.and(f.getTaxDateProperty().ge(AonDateUtils.toTimestamp(date)));
		}
		return filter;
	}
	
	private Filter salesFilter(Domain domain, Integer[] ediRegistryIds, Map<String, String[]> filterMap, SalesProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		fillPaginationFilter(filter, filterMap);
		if(ediRegistryIds!=null && ediRegistryIds.length>0)
			filter = filter.and(f.getCustomerProperty().in(ediRegistryIds));
		
		if (filterMap.containsKey(MSG.FROM)) {
			Date date = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.FROM)[0])));
			filter = filter.and(f.getIssueDateProperty().ge(AonDateUtils.toSql(date)));
		}
		return filter;
	}
	
	private Filter dataAttachFilter(Domain domain, Map<String, String[]> filterMap, AttachProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		fillPaginationFilter(filter, filterMap);
		
		if (filterMap.containsKey(MSG.FROM)) {
			Date date = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.FROM)[0])));
			filter = filter.and(f.getCreationDateTimeStampProperty().ge(AonDateUtils.toTimestamp(date)));
		}
		return filter;
	}

	private Filter dataResponseFilter(Domain domain, Map<String, String[]> filterMap, DataResponseProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());

		if (filterMap.containsKey(MSG.FROM)) {
			Date date = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.FROM)[0])));
			filter = filter.and(f.getIssueDateProperty().ge(AonDateUtils.toSql(date)));
		}

		// if(filterMap.containsKey(MSG.DELIVERY_DATE)){
		// filter = filter.and(f.getDeliveryDateProperty().ge(new
		// Timestamp(Long.parseLong(filterMap.get(MSG.DELIVERY_DATE)[0])))
		// .or(f.getDeliveryDateProperty().isNull()));
		// }
		//
		// if(filterMap.containsKey(MSG.SERIES)){
		// Filter fseries =
		// f.getSeriesProperty().eq(filterMap.get(MSG.SERIES)[0]);
		// for(Integer i = 1; i < filterMap.get(MSG.SERIES).length ; i++){
		// fseries =
		// fseries.or(f.getSeriesProperty().eq(filterMap.get(MSG.SERIES)[i]));
		// }
		// filter = filter.and(fseries);
		// }
		//
		// if(filterMap.containsKey(MSG.CARRIER)){
		// Filter fcarrier =
		// f.getCarrierProperty().eq(Integer.parseInt(filterMap.get(MSG.CARRIER)[0]));
		// for(Integer i = 1; i < filterMap.get(MSG.CARRIER).length ; i++){
		// fcarrier =
		// fcarrier.or(f.getCarrierProperty().eq(Integer.parseInt(filterMap.get(MSG.CARRIER)[i])));
		// }
		// filter = filter.and(fcarrier);
		// }
		//
		// if(filterMap.containsKey(MSG.TYPE)){
		// Filter ftype = f.getTypeProperty().eq((byte)
		// Integer.parseInt(filterMap.get(MSG.TYPE)[0]));
		// for(Integer i = 1; i < filterMap.get(MSG.TYPE).length ; i++){
		// ftype = ftype.or(f.getTypeProperty().eq((byte)
		// Integer.parseInt(filterMap.get(MSG.TYPE)[i])));
		// }
		// filter = filter.and(ftype);
		// }
		//
		// if(filterMap.containsKey(MSG.STATUS)){
		// Filter fstatus = f.getStatusProperty().eq((byte)
		// Integer.parseInt(filterMap.get(MSG.STATUS)[0]));
		// for(Integer i = 1; i < filterMap.get(MSG.STATUS).length ; i++){
		// fstatus = fstatus.or(f.getStatusProperty().eq((byte)
		// Integer.parseInt(filterMap.get(MSG.STATUS)[i])));
		// }
		// filter = filter.and(fstatus);
		// }
		//
		// if(filterMap.containsKey("text") &&
		// !"".equals(filterMap.get("text")[0])){
		// Filter ftext = f.getNumberPlateProperty().like("%" +
		// filterMap.get("text")[0] + "%");
		// ftext = ftext.or(f.getDriverNameProperty().like("%" +
		// filterMap.get("text")[0] + "%"));
		// ftext = ftext.or(f.getDriverDocumentProperty().like("%" +
		// filterMap.get("text")[0] + "%"));
		// ftext = ftext.or(f.getCarrierReferenceProperty().like("%" +
		// filterMap.get("text")[0] + "%"));
		// ftext = filter = filter.and(ftext);
		// }
		// if(filterMap.containsKey("per_page")){
		// String per_page = filterMap.get("per_page")[0];
		// Integer perPage = Integer.parseInt(per_page);
		// filter.perPage(perPage);
		// }
		// if(filterMap.containsKey("page")){
		// String page_str = filterMap.get("page")[0];
		// Integer page = Integer.parseInt(page_str);
		// filter.page(page);
		// }
		return filter;
	}

}
