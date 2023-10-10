package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.management.ShipmentPeriod;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.ShipmentStatus;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Delivery implements Serializable {
	
	private static final long serialVersionUID = 5693712945852955230L;
	private Integer id;
	private Integer domain;
	private Project project;
	private String series;
	private int number;
	private Customer customer;

	private RegistryAddress address;
	
	private Date date;
	private PayMethod payMethod;
	private SecurityLevel securityLevel;
	private DeliveryStatus status; 
	private String comments;
	private String remarks;
	
	private Workplace workplace;
	private Scope scope;
	private short numberOfPymnts;
	private short daysToFirstPymnt;
	private short daysBetweenPymnt;
	private String pymntDays;
	private String bankAccount;
	private String bankAlias;
	private String bic;
	
	private Integer carrier;
	private Integer carrierPacking;
	private String numberPlate;
	private String driver;
	private String driverDocument;
	private Double totalPackages;
	private Double totalWeight;
	private String shippingAlternativeAddress;
	private String shippingAlternativeAddress2;
	private String shippingAlternativeZip;
	private String shippingAlternativeCity;
	private String shippingAlternativePhone;
	private String shippingAlternativeRecipient;
	private String shippingContact;
	private ShipmentPeriod shippingPeriod;
	private String trackingNumber;	
	private ShipmentStatus shippingStatus;
	private Date statusModificationDate;	
	// shippingAlternative
	
	private List<DeliveryDetail> details;
	private List<DeliveryPackaging> packaging;
	
	private Date creationDate;
	private String creationUser;
	private Date modificationDate;
	private String modificationUser;
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public Integer getId() {
		return id;
	}
	
	public Delivery setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public boolean hasId() {
		return getId() != null;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public Delivery setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Project getProject() {
		if(project == null) {
			project = new Project();
		}
		return project;
	}
	
	public Delivery setProject(Project project) {
		this.project = project;
		return this;
	}
	public String getSeries() {
		return series;
	}
	public Delivery setSeries(String series) {
		this.series = series;
		return this;
	}
	public int getNumber() {
		return number;
	}
	public Delivery setNumber(int number) {
		this.number = number;
		return this;
	}
	public RegistryAddress getAddress() {
		if(address == null) {
			address = new RegistryAddress();
		}
		return address;
	}
	public Delivery setAddress(RegistryAddress address) {
		this.address = address;
		return this;
	}
	
	@Deprecated
	public StreetType getAddressStreetType() {
		return getAddress().getStreetType();
	}
	
	@Deprecated
	public Delivery setAddressStreetType(StreetType addressStreetType) {
		getAddress().setStreetType(addressStreetType);
		return this;
	}
	
	@Deprecated
	public String getAddressName() {
		return getAddress().getAddress();
	}
	
	@Deprecated
	public Delivery setAddressName(String addressName) {
		getAddress().setAddress(addressName);
		return this;
	}

	@Deprecated
	public String getAddressNumber() {
		return getAddress().getNumber();
	}
	
	@Deprecated
	public Delivery setAddressNumber(String addressNumber) {
		getAddress().setNumber(addressNumber);
		return this;
	}

	@Deprecated
	public String getAddressTown() {
		return getAddress().getCity();
	}
	
	@Deprecated
	public Delivery setAddressTown(String addressTown) {
		getAddress().setCity(addressTown);
		return this;
	}

	@Deprecated
	public String getAddressZIP() {
		return getAddress().getZip();
	}
	
	@Deprecated
	public Delivery setAddressZIP(String addressZIP) {
		getAddress().setZip(addressZIP);
		return this;
	}
	
	@Deprecated
	public String getAddressGeozoneCode() {
		return getAddress().getGeozoneCode();
	}

	@Deprecated
	public Delivery setAddressGeozoneCode(String addressGeozoneCode) {
		getAddress().setGeozoneCode(addressGeozoneCode);
		return this;
	}

	@Deprecated
	public String getAddressGeozone() {
		return getAddress().getGeozoneName();
	}

	@Deprecated
	public Delivery setAddressGeozone(String addressGeozone) {
		getAddress().setGeozoneName(addressGeozone);
		return this;
		
	}

	public Date getDate() {
		return date;
	}
	
	public Delivery setDate(Date date) {
		this.date = date;
		return this;
	}
	
	@Deprecated
	public Date getIssueTime() {
		return date;
	}
	
	@Deprecated
	public Delivery setIssueTime(Date issueTime) {
		this.date = issueTime;
		return this;
	}
	
	public PayMethod getPayMethod() {
		if(payMethod == null) {
			payMethod = new PayMethod();
		}
		return payMethod;
	}
	
	public Delivery setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
		return this;
	}

	public SecurityLevel getSecurityLevel() {
		if(securityLevel == null) {
			securityLevel = SecurityLevel.OFFICIAL;
		}
		return securityLevel;
	}
	
	public Delivery setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	
	public Delivery setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
		return this;
	}
	
	public String getComments() {
		return comments;
	}
	
	public Delivery setComments(String comments) {
		this.comments = comments;
		return this;
	}
	
	public String getRemarks() {
		return remarks;
	}
	
	public Delivery setRemarks(String remarks) {
		this.remarks = remarks;
		return this;
	}
	
	public Scope getScope() {
		if(scope == null) {
			scope = new Scope();
		}
		return scope;
	}
	
	public Delivery setScope(Scope scope) {
		this.scope = scope;
		return this;
	}
	
	@Deprecated
	public String getScopeName() {
		return getScope().getDescription();
	}

	@Deprecated
	public Delivery setScopeName(String scopeName) {
		getScope().setDescription(scopeName);
		return this;
	}
	public short getNumberOfPymnts() {
		return numberOfPymnts;
	}
	public Delivery setNumberOfPymnts(short numberOfPymnts) {
		this.numberOfPymnts = numberOfPymnts;
		return this;
	}
	public short getDaysToFirstPymnt() {
		return daysToFirstPymnt;
	}
	public Delivery setDaysToFirstPymnt(short daysToFirstPymnt) {
		this.daysToFirstPymnt = daysToFirstPymnt;
		return this;
	}
	public short getDaysBetweenPymnt() {
		return daysBetweenPymnt;
	}
	public Delivery setDaysBetweenPymnt(short daysBetweenPymnt) {
		this.daysBetweenPymnt = daysBetweenPymnt;
		return this;
	}
	public String getPymntDays() {
		return pymntDays;
	}
	public Delivery setPymntDays(String pymntDays) {
		this.pymntDays = pymntDays;
		return this;
	}
	public String getBankAccount() {
		return bankAccount;
	}
	public Delivery setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
		return this;
	}
	public String getBankAlias() {
		return bankAlias;
	}
	public Delivery setBankAlias(String bankAlias) {
		this.bankAlias = bankAlias;
		return this;
	}
	public String getBic() {
		return bic;
	}
	public Delivery setBic(String bic) {
		this.bic = bic;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public Delivery setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public Delivery setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	
	public Date getModificationDate() {
		return modificationDate;
	}
	
	public Delivery setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public String getModificationUser() {
		return modificationUser;
	}
	
	public Delivery setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	
	public Customer getCustomer() {
		if(customer == null)
			customer = new Customer();
		return customer;
	}
	
	public Delivery setCustomer(Customer customer) {
		this.customer = customer;
		return this;
	}
	
	public DeliveryStatus getStatus() {
		return status;
	}
	
	public Delivery setStatus(DeliveryStatus status) {
		this.status = status;
		return this;
	}
	
	public Workplace getWorkplace() {
		if(workplace == null) {
			workplace = new Workplace();
		}
		return workplace;
	}
	public Delivery setWorkplace(Workplace workplace) {
		this.workplace = workplace;
		return this;
	}
	
	@Deprecated
	public String getWorkplaceName() {
		return getWorkplace().getDescription();
	}
	
	@Deprecated
	public Delivery setWorkplaceName(String workplaceName) {
		getWorkplace().setDescription(workplaceName);
		return this;
	}
	
	public Integer getCarrier() {
		return carrier;
	}
	
	public Delivery setCarrier(Integer carrier) {
		this.carrier = carrier;
		return this;
	}
	
	public Integer getCarrierPacking() {
		return carrierPacking;
	}
	
	public Delivery setCarrierPacking(Integer carrierPacking) {
		this.carrierPacking = carrierPacking;
		return this;
	}
	
	public String getDriver() {
		return driver;
	}
	
	public Delivery setDriver(String driver) {
		this.driver = driver;
		return this;
	}
	
	public String getDriverDocument() {
		return driverDocument;
	}
	
	public Delivery setDriverDocument(String driverDocument) {
		this.driverDocument = driverDocument;
		return this;
	}
	
	public Double getTotalPackages() {
		return totalPackages;
	}
	
	public Delivery setTotalPackages(Double totalPackages) {
		this.totalPackages = totalPackages;
		return this;
	}
	
	public Double getTotalWeight() {
		return totalWeight;
	}
	
	public Delivery setTotalWeight(Double totalWeight) {
		this.totalWeight = totalWeight;
		return this;
	}
	
	public String getShippingAlternativeAddress() {
		return shippingAlternativeAddress;
	}
	
	public Delivery setShippingAlternativeAddress(String shippingAlternativeAddress) {
		this.shippingAlternativeAddress = shippingAlternativeAddress;
		return this;
	}
	
	public String getShippingAlternativeAddress2() {
		return shippingAlternativeAddress2;
	}
	
	public Delivery setShippingAlternativeAddress2(String shippingAlternativeAddress2) {
		this.shippingAlternativeAddress2 = shippingAlternativeAddress2;
		return this;
	}
	
	public String getShippingAlternativeZip() {
		return shippingAlternativeZip;
	}
	
	public Delivery setShippingAlternativeZip(String shippingAlternativeZip) {
		this.shippingAlternativeZip = shippingAlternativeZip;
		return this;
	}
	
	public String getShippingAlternativeCity() {
		return shippingAlternativeCity;
	}
	
	public Delivery setShippingAlternativeCity(String shippingAlternativeCity) {
		this.shippingAlternativeCity = shippingAlternativeCity;
		return this;
	}
	
	public String getShippingAlternativePhone() {
		return shippingAlternativePhone;
	}
	
	public Delivery setShippingAlternativePhone(String shippingAlternativePhone) {
		this.shippingAlternativePhone = shippingAlternativePhone;
		return this;
	}
	
	public String getShippingAlternativeRecipient() {
		return shippingAlternativeRecipient;
	}
	
	public Delivery setShippingAlternativeRecipient(String shippingAlternativeRecipient) {
		this.shippingAlternativeRecipient = shippingAlternativeRecipient;
		return this;
	}
	
	public String getShippingContact() {
		return shippingContact;
	}
	
	public Delivery setShippingContact(String shippingContact) {
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
	
	public Delivery setShippingPeriod(ShipmentPeriod shippingPeriod) {
		this.shippingPeriod = shippingPeriod;
		return this;
	}
	
	public String getTrackingNumber() {
		return trackingNumber;
	}
	
	public Delivery setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
		return this;
	}
	
	public ShipmentStatus getShippingStatus() {
		return shippingStatus;
	}
	
	public Byte getShippingStatusValue() {
		return getShippingStatus() != null
			? getShippingStatus().value() : null;
	}
	
	public Delivery setShippingStatus(ShipmentStatus shippingStatus) {
		this.shippingStatus = shippingStatus;
		return this;
	}
	
	public Date getStatusModificationDate() {
		return statusModificationDate;
	}
	
	public Delivery setStatusModificationDate(Date statusModificationDate) {
		this.statusModificationDate = statusModificationDate;
		return this;
	}
	
	public String getNumberPlate() {
		return numberPlate;
	}
	
	public Delivery setNumberPlate(String numberPlate) {
		this.numberPlate = numberPlate;
		return this;
	}
	
	@Deprecated
	public String getCustomerName() {
		return getCustomer().getName();
	}
	
	@Deprecated
	public Delivery setCustomerName(String customerName) {
		getCustomer().setName(customerName);
		return this;
	}
	
	@Deprecated
	public String getCustomerDocument() {
		return getCustomer().getDocument();
	}
	
	@Deprecated
	public Delivery setCustomerDocument(String customerDocument) {
		getCustomer().setDocument(customerDocument);
		return this;
	}
	
	public List<DeliveryDetail> getDetails() {
		if(details == null) {
			details = new LinkedList<>();
		}
		return details;
	}
	
	public Delivery setDetails(List<DeliveryDetail> details) {
		this.details = details;
		return this;
	}
	
	public List<DeliveryDetail> addDetail(DeliveryDetail detail) {
		getDetails().add(detail);
		return getDetails();
	}
	
	public List<DeliveryPackaging> getPackaging() {
		if(packaging == null) {
			packaging = new LinkedList<>();
		}
		return packaging;
	}
	
	public Delivery setPackaging(List<DeliveryPackaging> packaging) {
		this.packaging = packaging;
		return this;
	}
	
	public List<DeliveryPackaging> addDetail(DeliveryPackaging packaging) {
		getPackaging().add(packaging);
		return getPackaging();
	}
	
	public String getReferenceCode() {
    	String referenceCode = AonStringUtils.leftPad(Integer.toString(getNumber()), 6, "0");
		if (!AonStringUtils.isEmpty(getSeries())) {
			referenceCode = getSeries() + "/" + referenceCode;
		}
    	return referenceCode;
    }
	
	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Delivery ))
			return false;
		Delivery delivery = (Delivery) obj;
		return Objects.equals(id, delivery.id);
	}
}
