package net.aonsolutions.aon.invoice.communication;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.security.User;

public class CommunicatorContext {
	
	private final Domain domain; 
	private final User user;
	private final Invoice invoice; 
	private final Integer rawdocId;
	private final JSONObject inputInvoiceJSON;
	private InvoiceCommunicationConfiguration config;
	private Company company;
	private JSONObject outputInvoiceJSON;
	
	private DataResponse response;
	

	public CommunicatorContext(Domain domain,User user,Invoice invoice, JSONObject inputInvoiceJSON) {
		this.domain = domain;
		this.user = user;
		this.invoice = invoice;
		this.rawdocId = invoice.getId();
		invoice.setId(null);
		this.inputInvoiceJSON = inputInvoiceJSON;
	}
	
	public Domain getDomain() {
		return domain;
	}
	public User getUser() {
		return user;
	}
	public Invoice getInvoice() {
		return invoice;
	}
	public Integer getRawdocId() {
		return rawdocId;
	}
	public JSONObject getInvoiceJSON() {
		return inputInvoiceJSON;
	}
	public String getTbaiId() {
		return JsonUtils.getString(inputInvoiceJSON, IJsonNames.TBAI_ID);
	}
	public Integer getCertificateId() {
		return JsonUtils.getInteger(inputInvoiceJSON, IJsonNames.CERT);
	}
	public JSONObject getFileJSON() {
		return JsonUtils.getJSONObject(inputInvoiceJSON, IJsonNames.FILE);
	}
	public InvoiceCommunicationConfiguration getConfig() {
		return config;
	}
	public CommunicatorContext setConfig(InvoiceCommunicationConfiguration config) {
		this.config = config;
		return this;
	}
	public Company getCompany() {
		return company;
	}
	public CommunicatorContext setCompany(Company company) {
		this.company = company;
		return this;
	}
	
	public JSONObject getOutputInvoiceJSON() {
		return outputInvoiceJSON;
	}
	public CommunicatorContext setOutputInvoiceJSON(JSONObject outputInvoiceJSON) {
		this.outputInvoiceJSON = outputInvoiceJSON;
		return this;
	}

	public DataResponse getResponse() {
		return response;
	}
	public void setResponse(DataResponse response) {
		this.response = response;
	}
	
	
}
