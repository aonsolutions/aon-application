package net.aonsolutions.aon.invoice.communication.visitor;

import java.util.Date;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.DataRequestType;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonDocumentUtil;

import net.aonsolutions.aon.tbai.TbaiBlockchain;

public class BasicCommunicationInvoiceTypeVisitor {

	
	private Domain domain;
	private User user;
	private Invoice invoice;
	private Integer certificateId;
	
	
	private Company company;
	private Person person;
	private TbaiConfiguration tbaiConfiguration;
	
	public BasicCommunicationInvoiceTypeVisitor(Domain domain, User user, Invoice invoice) {
		this.domain = domain;
		this.user = user;
		this.invoice = invoice;
	}
	
	public BasicCommunicationInvoiceTypeVisitor(Domain domain, User user, Invoice invoice, Integer certificateId) {
		this.domain = domain;
		this.user = user;
		this.invoice = invoice;
		this.certificateId = certificateId;
	}
	
	public Domain getDomain() {
		return domain;
	}
	
	public void setDomain(Domain domain) {
		this.domain = domain;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Invoice getInvoice() {
		return invoice;
	}
	
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
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
			: AON.getCompany(getDomain(), getUser(), f -> f.getDomainProperty().eq(getDomain().getId()));
	}
	
	public BasicCommunicationInvoiceTypeVisitor setCompany(Company company) {
		this.company = company;
		return this;
	}
	
	protected Person getPerson(Integer companyId) {
		return person != null
			? person : AON.getPerson(getDomain(), getUser().getLogin(), f -> f.getDomainProperty().eq(getDomain().getId()).and(f.getIdProperty().eq(companyId)));
	}
	
	public BasicCommunicationInvoiceTypeVisitor setPerson(Person person) {
		this.person = person;
		return this;
	}
	
	protected SiiConfiguration getSiiConfiguration() {
		return AON.getSiiConfiguration(getDomain(), getUser())
				.setCertificate(getCertificate());
	}
	
	protected TbaiConfiguration getTbaiConfiguration() {
		return tbaiConfiguration != null
			? tbaiConfiguration
			: AON.getTbaiConfiguration(getDomain(), getUser())
					.setCertificate(getCertificate());
	}
	
	public BasicCommunicationInvoiceTypeVisitor setTbaiConfiguration(TbaiConfiguration tbaiConfiguration) {
		this.tbaiConfiguration = tbaiConfiguration;
		return this;
	}
	
	protected TbaiBlockchain getBlockchain(Integer actualInvoice) {
		DataResponseSource source = getTbaiConfiguration().isTest() ? DataResponseSource.TBAI_TEST : DataResponseSource.TBAI;
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
