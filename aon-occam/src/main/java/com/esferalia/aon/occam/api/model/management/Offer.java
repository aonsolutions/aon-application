package com.esferalia.aon.occam.api.model.management;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.OfferStatus;
import com.esferalia.aon.occam.api.model.type.OfferType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Offer implements Serializable {
	
	private static final long serialVersionUID = -204612166516482195L;
	
	private Integer id;
	private int domain;
	private String series;
	private int number;
	private int version;
	private Date issueDate;
	private Scope scope;
	private Target target;
	private Seller seller;
	private Supplier supplier;
	private Project project;
	private Workplace workPlace;
	private OfferStatus status;
	private OfferType type;
	private String externalReference;
	private Boolean signed;
	
	private String bankAccount;
	private String bic;

	public Integer getId() {
		return id;
	}
	public Offer setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public Offer setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public String getSeries() {
		return series;
	}
	public Offer setSeries(String series) {
		this.series = series;
		return this;
	}
	public Integer getNumber() {
		return number;
	}
	public Offer setNumber(int number) {
		this.number = number;
		return this;
	}
	public Integer getVersion() {
		return version;
	}
	public Offer setVersion(int version) {
		this.version = version;
		return this;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public Offer setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	public Scope getScope() {
		return scope;
	}
	public Offer setScope(Scope scope) {
		this.scope = scope;
		return this;
	}
	public Target getTarget() {
		return target;
	}
	public Offer setTarget(Target target) {
		this.target = target;
		return this;
	}
	public Seller getSeller() {
		return seller;
	}
	public Offer setSeller(Seller seller) {
		this.seller = seller;
		return this;
	}
	public Supplier getSupplier() {
		return supplier;
	}
	public Offer setSupplier(Supplier supplier) {
		this.supplier = supplier;
		return this;
	}
	public Project getProject() {
		return project;
	}
	public Offer setProject(Project project) {
		this.project = project;
		return this;
	}
	public Workplace getWorkPlace() {
		return workPlace;
	}
	public Offer setWorkPlace(Workplace workPlace) {
		this.workPlace = workPlace;
		return this;
	}
	public OfferStatus getStatus() {
		return status;
	}
	public Offer setStatus(OfferStatus status) {
		this.status = status;
		return this;
	}
	public OfferType getType() {
		return type;
	}
	public Offer setType(OfferType type) {
		this.type = type;
		return this;
	}
	
    public String getExternalReference() {
		return externalReference;
	}
	public Offer setExternalReference(String externalReference) {
		this.externalReference = externalReference;
		return this;
	}
	
	public Boolean getSigned() {
		return signed;
	}
	public Boolean isSigned() {
		return signed;
	}
	public Offer setSigned(Boolean signed) {
		this.signed = signed;
		return this;
	}
	
	public String getBankAccount() {
		return bankAccount;
	}
	public Offer setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
		return this;
	}
	
	public String getBic() {
		return bic;
	}
	public Offer setBic(String bic) {
		this.bic = bic;
		return this;
	}
	
	public String getReferenceCode() {
    	String referenceCode = AonStringUtils.leftPad(Integer.toString(getNumber()), 6, '0');
    	referenceCode += '/' + getVersion();
		if (!AonStringUtils .isEmpty(getSeries())) {
			referenceCode = getSeries() + "/" + referenceCode;
		}
    	return referenceCode;
    }
}

