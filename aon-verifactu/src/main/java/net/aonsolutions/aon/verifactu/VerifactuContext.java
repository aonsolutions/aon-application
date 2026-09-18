package net.aonsolutions.aon.verifactu;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationQuery;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.consultalr.ConsultaFactuSistemaFacturacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;

public class VerifactuContext  {
	private final InvoiceCommunicatorContext invoiceCommunicatorContext;
	private List<EnterpriseActivity> activities;
	private VerifactuBlockchain blockchain;
	private InvoiceCommunicationOperation operation;
	private RegFactuSistemaFacturacion request;
	private byte[] requestBytes;	
	private VerifactuResponse response;
	private ConsultaFactuSistemaFacturacionType queryRequest;
	
	public VerifactuContext(InvoiceCommunicatorContext invoiceCommunicatorContext) {
		this.invoiceCommunicatorContext = invoiceCommunicatorContext;
	}
	
	public InvoiceCommunicatorContext getInvoiceCommunicatorContext() {
		return invoiceCommunicatorContext;
	}
	public Integer getDomainId() {
		return getDomain() == null ? null : getDomain().getId();
	}
	public Domain getDomain() {
		return getInvoiceCommunicatorContext().getDomain();
	}
	public User getUser() {
		return getInvoiceCommunicatorContext().getUser();
	}
	public Company getCompany() {
		return getInvoiceCommunicatorContext().getCompany();
	}
	public InvoiceCommunicationConfiguration getConfig() {
		return getInvoiceCommunicatorContext().getConfig();
	}
	public boolean isCommonTerritory() {
		return getInvoiceCommunicatorContext().getConfig().isAEAT();
	}
	public boolean isCanarias() {
		return getInvoiceCommunicatorContext().getConfig().isCanarias();
	}
	public boolean isVerifactuAdmon() {
		return isCommonTerritory() || isCanarias();
	}
	
	public InvoiceCommunicationQuery getInvoiceCommunicationQuery() {
		return getInvoiceCommunicatorContext().getCommunicationQuery();
	}
	public Integer getCertificateId() {
		return getInvoiceCommunicatorContext().getCertificateId();
	}
	public DataResponse getDataResponse() {
		return getInvoiceCommunicatorContext().getDataResponse();
	}
	public VerifactuContext setDataResponse(DataResponse dataResponse) {
		getInvoiceCommunicatorContext().setDataResponse(dataResponse);
		return this;
	}
	
	public Stream<Invoice> invoiceStream() {
		return getInvoiceCommunicatorContext().invoiceStream();
	}
	public int invoiceCount() {
		return getInvoiceCommunicatorContext().invoiceCount();
	}
	public boolean isVerifactuTest() {
		return getInvoiceCommunicatorContext().getConfig().isVerifactuTest();

	}
	public List<EnterpriseActivity> getActivities() {
		return activities;
	}
	public VerifactuContext setActivities(List<EnterpriseActivity> activities) {
		this.activities = activities;
		return this;
	}
	public Optional<EnterpriseActivity> getActivity(Integer activity) {
		return AonCollectionUtils.stream(activities)
			.filter(a -> AonNumberUtils.equals(a.getId(), activity))
			.findAny();
	}
	
	public VerifactuBlockchain getBlockchain() {
		return blockchain;
	}
	public VerifactuContext setBlockchain(VerifactuBlockchain blockchain) {
		this.blockchain = blockchain;
		return this;
	}

	public RegFactuSistemaFacturacion getRequest() {
		return request;
	}
	public VerifactuContext setRequest(RegFactuSistemaFacturacion request) {
		this.request = request;
		return this;
	}
	
	public byte[] getRequestBytes() {
		return requestBytes;
	}
	public VerifactuContext setRequestBytes(byte[] requestBytes) {
		this.requestBytes = requestBytes;
		return this;
	}
	
	public VerifactuResponse getResponse() {
		return response;
	}
	public VerifactuContext setResponse(VerifactuResponse response) {
		this.response = response;
		return this;
	}
	
	public InvoiceCommunicationOperation getOperation() {
		return operation;
	}
	
	public VerifactuContext setOperation(InvoiceCommunicationOperation operation) {
		this.operation = operation;
		return this;
	}
	
	public boolean isAnnulment() {
		return getOperation() != null && getOperation().isAnnulment();
	}
	
	public boolean isResponseIncorrecto(){
		return getResponse() == null || getResponse().isIncorrecto(); 
	}
	public boolean isResponseIncorrecta( Integer invoiceId ){
		if (isResponseIncorrecto()) return true;
		return getResponse().isIncorrecta(invoiceId);
	}
	public boolean isResponseCorrecta( Integer invoiceId ){
		return !isResponseIncorrecta(invoiceId);
	}

	public ConsultaFactuSistemaFacturacionType getQueryRequest() {
		return queryRequest;
	}
	public VerifactuContext setQueryRequest(ConsultaFactuSistemaFacturacionType queryRequest) {
		this.queryRequest = queryRequest;
		return this;
	}
	
}
