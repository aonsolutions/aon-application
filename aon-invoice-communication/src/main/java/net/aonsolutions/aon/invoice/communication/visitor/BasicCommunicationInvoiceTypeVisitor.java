package net.aonsolutions.aon.invoice.communication.visitor;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.watson.util.AonDocumentUtil;

import net.aonsolutions.aon.tbai.TbaiBlockchain;
import net.aonsolutions.aon.verifactu.VerifactuBlockchain;

public class BasicCommunicationInvoiceTypeVisitor {


	private Occam occam;
	
	private List<Invoice> invoices;
	// LIST<Invoice>
	private Integer certificateId;
	
	
	private Company company;
	private Person person;
	
	private InvoiceCommunicationConfiguration configuration;
	
	public BasicCommunicationInvoiceTypeVisitor(Occam occam, Invoice invoice) {
		this.occam = occam;
		this.invoices = new LinkedList<>();
		this.invoices.add(invoice);
	}
	
	public BasicCommunicationInvoiceTypeVisitor(Occam occam, List<Invoice> invoices) {
		this.occam = occam;
		this.invoices = invoices;
	}
	
	public BasicCommunicationInvoiceTypeVisitor(Occam occam, Invoice invoice, Integer certificateId) {
		this.occam = occam;
		this.invoices = new LinkedList<>();
		this.invoices.add(invoice);
		this.certificateId = certificateId;
	}
	
	public BasicCommunicationInvoiceTypeVisitor(Domain domain, User user, Invoice invoice) {
		this.occam = new Occam()
				.setDomain(domain.getId())
				.setDomainName(domain.getName())
				.setUser(user.getLogin());
		this.invoices = new LinkedList<>();
		this.invoices.add(invoice);
	}
	
	public BasicCommunicationInvoiceTypeVisitor(Domain domain, User user, Invoice invoice, Integer certificateId) {
		this.occam = new Occam()
				.setDomain(domain.getId())
				.setDomainName(domain.getName())
				.setUser(user.getLogin());
		this.invoices = new LinkedList<>();
		this.invoices.add(invoice);
		this.certificateId = certificateId;
	}
	
	public Occam getOccam() {
		return occam;
	}
	
	public void setOccam(Occam occam) {
		this.occam = occam;
	}
	
	@Deprecated
	public Domain getDomain() {
		return new Domain().setName(getOccam().getDomainName()).setId(getOccam().getDomain());
	}
	
	@Deprecated
	public User getUser() {
		return new User().setLogin(getOccam().getUser());
	}
	
	public List<Invoice> getInvoices() {
		if(invoices == null) {
			invoices = new LinkedList<>();
		}
		return invoices;
	}
	
	@Deprecated
	public Invoice getInvoice() {
		if(getInvoices().size() == 1) {
			return getInvoices().get(0);
		}
		return null;
	}
	
	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}
	
	public Integer getCertificateId() {
		return certificateId;
	}
	
	public void setCertificateId(Integer certificateId) {
		this.certificateId = certificateId;
	}
	
	protected Company getCompany() {
		return company != null
			? company
			: AON.getCompany(getOccam(), f -> f.getDomainProperty().eq(getOccam().getDomain()));
	}
	
	public BasicCommunicationInvoiceTypeVisitor setCompany(Company company) {
		this.company = company;
		return this;
	}
	
	protected Person getPerson(Integer companyId) {
		return person != null
			? person 
			: AON.getPerson(getOccam(), f -> 
				f.getDomainProperty().eq(getOccam().getDomain())
				.and(f.getIdProperty().eq(companyId)));
	}
	
	public BasicCommunicationInvoiceTypeVisitor setPerson(Person person) {
		this.person = person;
		return this;
	}
	
	public InvoiceCommunicationConfiguration getConfiguration() {
		return configuration != null
			? configuration 
			: AON.getInvoiceCommunicationConfiguration(getOccam())
				.setCertificate(getCertificate());
	}
	
	public BasicCommunicationInvoiceTypeVisitor setConfiguration(InvoiceCommunicationConfiguration configuration) {
		this.configuration = configuration;
		return this;
	}
	
	protected TbaiBlockchain getBlockchain(Integer actualInvoice) {
		DataResponseSource source = getConfiguration().getTbaiData()
			.filter(cc -> cc.isTest() )
			.map(cc -> DataResponseSource.TBAI_TEST)
			.orElse(DataResponseSource.TBAI);
		// DataResponseSource source = getConfiguration().isTbaiTest() ? DataResponseSource.TBAI_TEST : DataResponseSource.TBAI;
		DataResponse dr = AON.getLastDataResponse(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> 
			f.getDomainProperty().eq(getDomain().getId())
			.and(f.getSourceProperty().eq(source.value()))
			.and(f.getSourceIdProperty().ne(actualInvoice))
			.and(f.getCodeProperty().ne("baja"))
			);
		
		DataResponseDetail drd = dr.getId() != null ? AON.getDataResponseDetail(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> 
			f.getDomainProperty().eq(getDomain().getId())
			.and(f.getDataResponseProperty().eq(dr.getId()))
			.and(f.getDataVariableProperty().eq("blockchain"))).orElse(new DataResponseDetail()) : new DataResponseDetail();
		
		return TbaiBlockchain.fromJSON(drd.getDataValue());
	}
	
	protected VerifactuBlockchain getVerifactuBlockchain() {	
		return new VerifactuBlockchain()
			.setDocument(AON.getApplicationParameter(getOccam(), AppParam.VERIFACTU_BLOCKCHAIN_DOCUMENT).getValue())
			.setReference(AON.getApplicationParameter(getOccam(), AppParam.VERIFACTU_BLOCKCHAIN_REFERENCE).getValue())
			.setDate(AON.getApplicationParameter(getOccam(), AppParam.VERIFACTU_BLOCKCHAIN_DATE).getValue())
			.setHuella(AON.getApplicationParameter(getOccam(), AppParam.VERIFACTU_BLOCKCHAIN_HUELLA).getValue());
	}
	
	protected Certificate getCertificate() {
		Certificate cert = null;
		if(getCertificateId() != null) {
			cert = AON.getCertificates(getDomain(), getUser(), f -> f.getIdProperty().eq(getCertificateId()))
					.findFirst().orElse(null);	
		} 
		
		if(cert == null) {
			cert =  AON.getCertificate(getDomain(), getUser(), CertificateType.AEAT.name());
		}
		
		return cert;
	}
	
	public boolean isPersonaFisica(String document) {
		return !AonDocumentUtil.isValidCIF(document) || AonDocumentUtil.isAssetCommunity(document)
			|| AonDocumentUtil.isOwnerCommunity(document) || AonDocumentUtil.isCivilSociety(document);
	}
	

}
