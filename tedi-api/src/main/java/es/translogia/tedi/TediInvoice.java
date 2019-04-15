package es.translogia.tedi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.json.JSONObject;

public class TediInvoice {

	public static final String SRC = "https://europe-west1-tedicenter.cloudfunctions.net";
	public static final String SRC_SNAPSHOT = "https://europe-west1-tedi-snapshot.cloudfunctions.net";

	public TediInvoice() {}

	public TediInvoice(JSONObject json) {
		this.json = json;
		this.company = json.getString("company");
		this.uuid = json.getString("uuid");

		this.series = json.optString("series");
		this.number = json.optInt("number");
		this.date = dateTimeParse(json.getString("date"));
		this.reference = json.optString("reference");
		this.type = TediInvoiceType.getTediInvoiceType(json.getString("type"));
		this.sender = new TediRegistry(json.optJSONObject("sender"));
		this.receiver = new TediRegistry(json.optJSONObject("receiver"));
		this.source = json.optString("source");
		this.status = json.optString("status");
		this.total = json.getDouble("total");
		this.comments = json.optString("comments");
		this.taxes = json.opt("taxes") != null ? StreamSupport.stream(json.optJSONArray("taxes").spliterator(), false).map(r -> new TediTax((JSONObject) r))
				.collect(Collectors.toCollection(LinkedList::new)): null;
		this.details = json.opt("details") != null ? StreamSupport.stream(json.optJSONArray("details").spliterator(), false).map(r -> new TediDetail((JSONObject) r))
				.collect(Collectors.toCollection(LinkedList::new)) : null;
		this.finances = json.opt("finances") != null ? StreamSupport.stream(json.optJSONArray("finances").spliterator(), false).map(r -> new TediFinance((JSONObject) r))
		.collect(Collectors.toCollection(LinkedList::new)) : null;
		this.date = json.opt("date") != null ? TediDateUtils.parse(json.optString("date"), "dd/MM/yyyy") : null;
		this.category = json.optString("category");
		this.transaction = TediTransaction.getTediInvoiceType(json.getString("transaction"));
		this.investment = json.optBoolean("investment");
	}
	
	private JSONObject json;
	
	private String company;
	private String uuid;
	
	private String series;
	private Integer number;
	
	private TediRegistry sender;
	private TediRegistry receiver;
		
	private String reference;
	private String status;
	private String source;
	private Date date;
	private Double total;
	private TediInvoiceType type;
	private LinkedList<TediTax> taxes;
	private LinkedList<TediDetail> details;
	private LinkedList<TediFinance> finances;
	private String comments;
	private String category;
	private TediFile file;
	
	private HashMap<String, Object> properties;
	
	private TediTransaction transaction;
	private Boolean investment;
    
	public String getCompany() {
		return company;
	}

	public TediInvoice setCompany(String company) {
		this.company = company;
		return this;
	}

	public String getUuid() {
		return uuid;
	}

	public TediInvoice setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}
	
	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public Integer getNumber() {
		return number;
	}

	public TediInvoice setNumber(Integer number) {
		this.number = number;
		return this;
	}

	public TediRegistry getSender() {
		return sender;
	}

	public TediInvoice setSender(TediRegistry sender) {
		this.sender = sender;
		return this;
	}

	public TediRegistry getReceiver() {
		return receiver;
	}

	public TediInvoice setReceiver(TediRegistry receiver) {
		this.receiver = receiver;
		return this;
	}

	public String getReference() {
		return reference;
	}

	public TediInvoice setReference(String reference) {
		this.reference = reference;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public TediInvoice setDate(Date date) {
		this.date = date;
		return this;
	}

	public Double getTotal() {
		return total;
	}

	public TediInvoice setTotal(Double total) {
		this.total = total;
		return this;
	}

	public TediInvoiceType getType() {
		return type;
	}

	public TediInvoice setType(TediInvoiceType type) {
		this.type = type;
		return this;
	}

	public LinkedList<TediTax> getTaxes() {
		return taxes;
	}

	public TediInvoice setTaxes(LinkedList<TediTax> taxes) {
		this.taxes = taxes;
		return this;
	}

	public LinkedList<TediDetail> getDetails() {
		return details;
	}

	public TediInvoice setDetails(LinkedList<TediDetail> details) {
		this.details = details;
		return this;
	}
	
	public String getComments() {
		return comments;
	}

	public TediInvoice setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public LinkedList<TediFinance> getFinances() {
		return finances;
	}

	public TediInvoice setFinances(LinkedList<TediFinance> finances) {
		this.finances = finances;
		return this;
	}

	
	public String getCategory() {
		return category;
	}

	public TediInvoice setCategory(String category) {
		this.category = category;
		return this;
	}
	
	public TediTransaction getTransaction() {
		return transaction;
	}

	public TediInvoice setTransaction(TediTransaction transaction) {
		this.transaction = transaction;
		return this;
	}
	
	public String getStatus() {
		return status;
	}

	public TediInvoice setStatus(String status) {
		this.status = status;
		return this;
	}

	public String getSource() {
		return source;
	}

	public TediInvoice setSource(String source) {
		this.source = source;
		return this;
	}

	public Boolean getInvestment() {
		return investment;
	}

	public TediInvoice setInvestment(Boolean investment) {
		this.investment = investment;
		return this;
	}

	public TediFile getFile() {
		return file;
	}

	public void setFile(TediFile file) {
		this.file = file;
	}
	
	public HashMap<String, Object> getProperties() {
		if(properties == null) properties = new HashMap<>();
		return properties;
	}

	public TediInvoice setProperties(HashMap<String, Object> properties) {
		this.properties = properties;
		return this;
	}
	
	public TediInvoice addProperties(String key, Object value) {
		getProperties().put(key, value);
		return this;
	}
	
	public JSONObject getProperty(String key) {
		return json.opt(key) != null ? json.getJSONObject(key) : new JSONObject();
	}
	
	public JSONObject getJSON() {
		JSONObject info = new JSONObject();
		JSONObject json =  new JSONObject()
				.put("company", getCompany())
				.put("number", getNumber())
				.put("info", info);
		
		getProperties().keySet().stream().forEach(key -> {
			json.put(key, getProperties().get(key));
		});
		System.out.println(json);
		return json;
	}

	private final static SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
	private String dateTimeFormat(Date date) {
		return date == null ? null : DATE_TIME_FORMAT.format(date);
	}
	private Date dateTimeParse(String date) {
		try {
			return date == null ? null : DATE_TIME_FORMAT.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
}


