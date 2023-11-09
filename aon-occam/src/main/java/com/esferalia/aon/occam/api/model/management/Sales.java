package com.esferalia.aon.occam.api.model.management;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.SalesStatus;
import com.esferalia.aon.occam.api.model.type.SalesType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Sales implements Serializable {
	
	private static final long serialVersionUID = 3076493407383383282L;
	private Integer id;
	private int domain;
	private Project project;
	private Customer customer;
	private String series;
	private int number;
	private String purchaseReference;
	private RegistryAddress shippingAddress;
	private Seller seller;
	private String discountExpr;
	private Date date;
	private PayMethod payMethod;
	private SalesType documentType;
	private SecurityLevel securityLevel;
	private SalesStatus status;
	private String comments;
	private String remarks;
	private Workplace workplace;
	private Scope scope;
	private short numberOfPymnts;
	private short daysToFirstPymnt;
	private short daysBetweenPymnts;
	private String pymntDays;
	private String bankAccount;
	private String bankAlias;
	private String bic;
	private boolean purchaseGenerated;
	private Date deliveryDate;
	private Carrier carrier;
	private Integer carrierPacking;
	
	private String shippingAlternativeAddress;
	private String shippingAlternativeAddress2;
	private String shippingAlternativeZip;
	private String shippingAlternativeCity;
	private String shippingAlternativePhone;
	private String shippingAlternativeRecipient;
	private String shippingContact;
	private ShipmentPeriod shippingPeriod;
	
	private List<SalesDetail> details;
	
	private Date creationDate;
	private String creationUser;
	private Date modificationDate;
	private String modificationUser;
	
	public Integer getId() {
		return id;
	}
	
	public Sales setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public boolean hasId() {
		return getId() != null;
	}

	public int getDomain() {
		return domain;
	}
	
	public Sales setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public Project getProject() {
		if(project == null) {
			project = new Project();
		}
		return project;
	}
	
	public Sales setProject(Project project) {
		this.project = project;
		return this;
	}
	
	public Customer getCustomer() {
		if(customer == null) {
			customer = new Customer();
		}
		return customer;
	}
	
	public Sales setCustomer(Customer customer) {
		this.customer = customer;
		return this;
	}

	public String getSeries() {
		return series;
	}
	
	public Sales setSeries(String series) {
		this.series = series;
		return this;
	}
	
	public int getNumber() {
		return number;
	}
	
	public Sales setNumber(int number) {
		this.number = number;
		return this;
	}
	
	public String getReferenceCode() {
		String reference = "";
		if(!AonStringUtils.isBlank(getSeries())) {
			reference = reference + getSeries() + "/";
		}
		reference = reference + AonStringUtils.leftPad(Integer.toString(getNumber()), 6, "0");
		return reference;
	}
	
	public String getPurchaseReference() {
		return purchaseReference;
	}
	
	public Sales setPurchaseReference(String purchaseReference) {
		this.purchaseReference = purchaseReference;
		return this;
	}
	
	public RegistryAddress getShippingAddress() {
		if(shippingAddress == null) {
			shippingAddress = new RegistryAddress();
		}
		return shippingAddress;
	}
	
	public Sales setShippingAddress(RegistryAddress shippingAddress) {
		this.shippingAddress = shippingAddress;
		return this;
	}
	
	public Seller getSeller() {
		if(seller == null) {
			seller = new Seller();
		}
		return seller;
	}
	
	public Sales setSeller(Seller seller) {
		this.seller = seller;
		return this;
	}
	
	public String getDiscountExpr() {
		return discountExpr;
	}
	
	public Sales setDiscountExpr(String discountExpr) {
		this.discountExpr = discountExpr;
		return this;
	}
	
	public Date getDate() {
		return date;
	}
	
	public Sales setDate(Date date) {
		this.date = date;
		return this;
	}
	
	@Deprecated
	public Date getIssueDate() {
		return date;
	}
	
	@Deprecated
	public Sales setIssueDate(Date issueDate) {
		this.date = issueDate;
		return this;
	}
	
	public PayMethod getPayMethod() {
		if(payMethod == null) {
			payMethod = new PayMethod();
		}
		return payMethod;
	}
	
	public Sales setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
		return this;
	}
	
	public SalesType getDocumentType() {
		return documentType;
	}
	
	public Sales setDocumentType(SalesType documentType) {
		this.documentType = documentType;
		return this;
	}
	
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	
	public Sales setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	
	public Sales setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
		return this;
	}
	
	public SalesStatus getStatus() {
		return status;
	}
	
	public Sales setStatus(SalesStatus status) {
		this.status = status;
		return this;
	}
	
	public String getComments() {
		return comments;
	}
	
	public Sales setComments(String comments) {
		this.comments = comments;
		return this;
	}
	
	public String getRemarks() {
		return remarks;
	}
	
	public Sales setRemarks(String remarks) {
		this.remarks = remarks;
		return this;
	}
	
	public Workplace getWorkplace() {
		if(workplace == null) {
			workplace = new Workplace();
		}
		return workplace;
	}
	
	public Sales setWorkplace(Workplace workplace) {
		this.workplace = workplace;
		return this;
	}
	
	public Scope getScope() {
		if(scope == null) {
			scope = new Scope();
		}
		return scope;
	}
	
	public Sales setScope(Scope scope) {
		this.scope = scope;
		return this;
	}
	
	public short getNumberOfPymnts() {
		return numberOfPymnts;
	}
	
	public Sales setNumberOfPymnts(short numberOfPymnts) {
		this.numberOfPymnts = numberOfPymnts;
		return this;
	}
	
	public short getDaysToFirstPymnt() {
		return daysToFirstPymnt;
	}
	
	public Sales setDaysToFirstPymnt(short daysToFirstPymnt) {
		this.daysToFirstPymnt = daysToFirstPymnt;
		return this;
	}
	
	public short getDaysBetweenPymnts() {
		return daysBetweenPymnts;
	}
	
	public Sales setDaysBetweenPymnts(short daysBetweenPymnts) {
		this.daysBetweenPymnts = daysBetweenPymnts;
		return this;
	}
	
	public String getPymntDays() {
		return pymntDays;
	}
	
	public Sales setPymntDays(String pymntDays) {
		this.pymntDays = pymntDays;
		return this;
	}
	
	public String getBankAccount() {
		return bankAccount;
	}
	
	public Sales setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
		return this;
	}
	
	public String getBankAlias() {
		return bankAlias;
	}
	
	public Sales setBankAlias(String bankAlias) {
		this.bankAlias = bankAlias;
		return this;
	}
	
	public String getBic() {
		return bic;
	}
	
	public Sales setBic(String bic) {
		this.bic = bic;
		return this;
	}
	
	public boolean isPurchaseGenerated() {
		return purchaseGenerated;
	}
	
	public Sales setPurchaseGenerated(boolean purchaseGenerated) {
		this.purchaseGenerated = purchaseGenerated;
		return this;
	}
	
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	
	public Sales setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
		return this;
	}
	
	public Carrier getCarrier() {
		if(carrier == null) {
			carrier = new Carrier();
		}
		return carrier;
	}
	
	public Sales setCarrier(Carrier carrier) {
		this.carrier = carrier;
		return this;
	}
	
	public Integer getCarrierPacking() {
		return carrierPacking;
	}
	
	public Sales setCarrierPacking(Integer carrierPacking) {
		this.carrierPacking = carrierPacking;
		return this;
	}
	
	public String getShippingAlternativeAddress() {
		return shippingAlternativeAddress;
	}
	
	public Sales setShippingAlternativeAddress(String shippingAlternativeAddress) {
		this.shippingAlternativeAddress = shippingAlternativeAddress;
		return this;
	}
	
	public String getShippingAlternativeAddress2() {
		return shippingAlternativeAddress2;
	}
	
	public Sales setShippingAlternativeAddress2(String shippingAlternativeAddress2) {
		this.shippingAlternativeAddress2 = shippingAlternativeAddress2;
		return this;
	}
	
	public String getShippingAlternativeZip() {
		return shippingAlternativeZip;
	}
	
	public Sales setShippingAlternativeZip(String shippingAlternativeZip) {
		this.shippingAlternativeZip = shippingAlternativeZip;
		return this;
	}
	
	public String getShippingAlternativeCity() {
		return shippingAlternativeCity;
	}
	
	public Sales setShippingAlternativeCity(String shippingAlternativeCity) {
		this.shippingAlternativeCity = shippingAlternativeCity;
		return this;
	}
	
	public String getShippingAlternativePhone() {
		return shippingAlternativePhone;
	}
	
	public Sales setShippingAlternativePhone(String shippingAlternativePhone) {
		this.shippingAlternativePhone = shippingAlternativePhone;
		return this;
	}
	
	public String getShippingAlternativeRecipient() {
		return shippingAlternativeRecipient;
	}
	
	public Sales setShippingAlternativeRecipient(String shippingAlternativeRecipient) {
		this.shippingAlternativeRecipient = shippingAlternativeRecipient;
		return this;
	}
	
	public String getShippingContact() {
		return shippingContact;
	}
	
	public Sales setShippingContact(String shippingContact) {
		this.shippingContact = shippingContact;
		return this;
	}
	
	public ShipmentPeriod getShippingPeriod() {
		return shippingPeriod;
	}
	
	public Byte getShippingPeriodValue() {
		return getShippingPeriod() != null
			? getShippingPeriod().value() : null;
	}
	
	public Sales setShippingPeriod(ShipmentPeriod shippingPeriod) {
		this.shippingPeriod = shippingPeriod;
		return this;
	}
	
	@Deprecated
	public String getScopeName() {
		return getScope().getDescription();
	}
	
	@Deprecated
	public Sales setScopeName(String scopeName) {
		getScope().setDescription(scopeName);
		return this;
	}
	
	@Deprecated
	public String getWorkplaceName() {
		return getWorkplace().getDescription();
	}
	
	@Deprecated
	public Sales setWorkplaceName(String workplaceName) {
		getWorkplace().setDescription(workplaceName);
		return this;
	}
	
	@Deprecated
	public String getProjectName() {
		return getProject().getName();
	}
	
	@Deprecated
	public Sales setProjectName(String projectName) {
		getProject().setName(projectName);
		return this;
	}
	
	public List<SalesDetail> getDetails() {
		if(details == null) {
			details = new LinkedList<>();
		}
		return details;
	}
	
	public Sales setDetails(List<SalesDetail> details) {
		this.details = details;
		return this;
	}
	
	public List<SalesDetail> addDetail(SalesDetail detail) {
		getDetails().add(detail);
		return getDetails();
	}
	
	// ----- AUDIT

	public Date getCreationDate() {
		return creationDate;
	}

	public Sales setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public String getCreationUser() {
		return creationUser;
	}

	public Sales setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public Sales setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	public String getModificationUser() {
		return modificationUser;
	}

	public Sales setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}	
	
	public boolean isEmpty() {
		return getId() == null && getSeries() == null;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Sales ) )
			return false;
		Sales sales = (Sales) obj;
		return Objects.equals(id, sales.id);
	}
	
}

