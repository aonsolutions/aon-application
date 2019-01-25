package es.translogia.tedi;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.json.JSONObject;

public class TediInvoice {

	public static final String SRC = "https://api.tedi.center/invoice";

	public TediInvoice() {}

	public TediInvoice(JSONObject json) {
		this.json = json;
		this.company = json.getString("company");
		this.number = json.getInt("number");
		if(json.opt("info") != null) {
			JSONObject info = json.getJSONObject("info");
			this.reference = info.getString("reference");
			this.code = json.getJSONObject("info").getString("code");
			this.type = TediInvoiceType.getTediInvoiceType(json.getJSONObject("info").getJSONObject("type").getString("type"));
			this.sender = new TediRegistry(json.getJSONObject("info").getJSONObject("sender"));
			this.receiver = new TediRegistry(json.getJSONObject("info").getJSONObject("receiver"));
			this.total = json.getJSONObject("info").getDouble("total");
			this.taxableBase = json.getJSONObject("info").getDouble("taxable_base");
			this.comments = json.getJSONObject("info").getString("comments");
			this.remarks = json.getJSONObject("info").getString("remarks");
			this.taxes = StreamSupport.stream(json.getJSONObject("info").getJSONArray("taxes").spliterator(), false).map(r -> new TediTax((JSONObject) r))
			.collect(Collectors.toCollection(LinkedList::new));
			this.details = StreamSupport.stream(json.getJSONObject("info").getJSONArray("details").spliterator(), false).map(r -> new TediDetail((JSONObject) r))
			.collect(Collectors.toCollection(LinkedList::new));
			this.finances = StreamSupport.stream(json.getJSONObject("info").getJSONArray("finances").spliterator(), false).map(r -> new TediFinance((JSONObject) r))
			.collect(Collectors.toCollection(LinkedList::new));
			this.date = TediDateUtils.parse(json.getJSONObject("info").getString("date"), "dd/MM/yyyy");
			this.pgc = new TediPGC(json.getJSONObject("info").getJSONObject("type").getJSONObject("pgc"));
		}
	}
	
	
	private JSONObject json;
	
	private String company;
	private Integer number;
	
	private TediRegistry sender;
	private TediRegistry receiver;
		
	private String reference;
	private String code;
	private String status;
	private String source;
	private Date date;
	private Double taxableBase;
	private Double total;
	private TediInvoiceType type;
	private LinkedList<TediTax> taxes;
	private LinkedList<TediDetail> details;
	private LinkedList<TediFinance> finances;
	private String comments;
	private String remarks;
	private TediPGC pgc;
	
	private HashMap<String, Object> properties;
	
	private String transaction;
	private Boolean investment;
	private Boolean service;
	private Boolean surcharge;
	private Boolean vatAccrualRegime;
    
	public String getCompany() {
		return company;
	}

	public TediInvoice setCompany(String company) {
		this.company = company;
		return this;
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

	public String getCode() {
		return code;
	}

	public TediInvoice setCode(String code) {
		this.code = code;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public TediInvoice setDate(Date date) {
		this.date = date;
		return this;
	}

	public Double getTaxableBase() {
		return taxableBase;
	}

	public TediInvoice setTaxableBase(Double taxableBase) {
		this.taxableBase = taxableBase;
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

	public String getRemarks() {
		return remarks;
	}

	public TediInvoice setRemarks(String remarks) {
		this.remarks = remarks;
		return this;
	}

	public LinkedList<TediFinance> getFinances() {
		return finances;
	}

	public TediInvoice setFinances(LinkedList<TediFinance> finances) {
		this.finances = finances;
		return this;
	}

	
	public TediPGC getPgc() {
		return pgc;
	}

	public TediInvoice setPgc(TediPGC pgc) {
		this.pgc = pgc;
		return this;
	}
	
	public String getTransaction() {
		return transaction;
	}

	public TediInvoice setTransaction(String transaction) {
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

	public Boolean getService() {
		return service;
	}

	public TediInvoice setService(Boolean service) {
		this.service = service;
		return this;
	}

	public Boolean getSurcharge() {
		return surcharge;
	}

	public TediInvoice setSurcharge(Boolean surcharge) {
		this.surcharge = surcharge;
		return this;
	}

	public Boolean getVatAccrualRegime() {
		return vatAccrualRegime;
	}

	public TediInvoice setVatAccrualRegime(Boolean vatAccrualRegime) {
		this.vatAccrualRegime = vatAccrualRegime;
		return this;
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
	
}


