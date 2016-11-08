package com.esferalia.aon.occam.api.model.project;

import java.io.Serializable;
import java.util.Date;
import com.esferalia.aon.occam.api.model.Domain;

@SuppressWarnings("serial")
public class ProjectReservation implements Serializable{

	Double advance;
	Byte advanceInvoiced;
	Integer agency;
	Double agencyCommissionAmount;
	Double agencyCommissionPercent;
	Byte agencyRebate;
	String bankTransaction;
	Byte bookingHolder;
	Date cancellationDate;
	String cancellationUser;
	Byte checkStatus;
	String code;
	String comments;
	Integer company;
	Date creationDate;
	String creationUser;
	String creditCardCvv;
	String creditCardExpirationMonth;
	String creditCardExpirationYear;
	String creditCardHolder;
	String creditCardNumber;
	String crsCode;
	Double discountAmount;
	Double discountPercent;
	Domain domain;
	Byte earlyCheckOut;
	Date endDate;
	Date endTime; // ** TimeStamp
	Integer hotel;
	Integer hotelReservation;
	Date modificationDate;
	String modificationUser;
	Double otherTaxQuota;
	Integer penaltyDays;
	Byte prepay;
	Integer project;
	String remarks;
	Integer seller;
	Byte source;
	Date startDate;
	Date startTime;
	Byte status;
	Double taxableBase;
	Double total;
	Double vatQuota;
	
	String token;
	Double penalty;
	
	public Double getAdvance() {
		return advance;
	}
	public ProjectReservation setAdvance(Double advance) {
		this.advance = advance;
		return this;
	}
	public Byte getAdvanceInvoiced() {
		return advanceInvoiced;
	}
	public ProjectReservation setAdvanceInvoiced(Byte advanceInvoiced) {
		this.advanceInvoiced = advanceInvoiced;
		return this;
	}
	public Integer getAgency() {
		return agency;
	}
	public ProjectReservation setAgency(Integer agency) {
		this.agency = agency;
		return this;
	}
	public Double getAgencyCommissionAmount() {
		return agencyCommissionAmount;
	}
	public ProjectReservation setAgencyCommissionAmount(Double agencyCommissionAmount) {
		this.agencyCommissionAmount = agencyCommissionAmount;
		return this;
	}
	public Double getAgencyCommissionPercent() {
		return agencyCommissionPercent;
	}
	public ProjectReservation setAgencyCommissionPercent(Double agencyCommissionPercent) {
		this.agencyCommissionPercent = agencyCommissionPercent;
		return this;
	}
	public Byte getAgencyRebate() {
		return agencyRebate;
	}
	public ProjectReservation setAgencyRebate(Byte agencyRebate) {
		this.agencyRebate = agencyRebate;
		return this;
	}
	public String getBankTransaction() {
		return bankTransaction;
	}
	public ProjectReservation setBankTransaction(String bankTransaction) {
		this.bankTransaction = bankTransaction;
		return this;
	}
	public Byte getBookingHolder() {
		return bookingHolder;
	}
	public ProjectReservation setBookingHolder(Byte bookingHolder) {
		this.bookingHolder = bookingHolder;
		return this;
	}
	public Date getCancellationDate() {
		return cancellationDate;
	}
	public ProjectReservation setCancellationDate(Date cancellationDate) {
		this.cancellationDate = cancellationDate;
		return this;
	}
	public String getCancellationUser() {
		return cancellationUser;
	}
	public ProjectReservation setCancellationUser(String cancellationUser) {
		this.cancellationUser = cancellationUser;
		return this;
	}
	public Byte getCheckStatus() {
		return checkStatus;
	}
	public ProjectReservation setCheckStatus(Byte checkStatus) {
		this.checkStatus = checkStatus;
		return this;
	}
	public String getCode() {
		return code;
	}
	public ProjectReservation setCode(String code) {
		this.code = code;
		return this;
	}
	public String getComments() {
		return comments;
	}
	public ProjectReservation setComments(String comments) {
		this.comments = comments;
		return this;
	}
	public Integer getCompany() {
		return company;
	}
	public ProjectReservation setCompany(Integer company) {
		this.company = company;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public ProjectReservation setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public ProjectReservation setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public String getCreditCardCvv() {
		return creditCardCvv;
	}
	public ProjectReservation setCreditCardCvv(String creditCardCvv) {
		this.creditCardCvv = creditCardCvv;
		return this;
	}
	public String getCreditCardExpirationMonth() {
		return creditCardExpirationMonth;
	}
	public ProjectReservation setCreditCardExpirationMonth(String creditCardExpirationMonth) {
		this.creditCardExpirationMonth = creditCardExpirationMonth;
		return this;
	}
	public String getCreditCardExpirationYear() {
		return creditCardExpirationYear;
	}
	public ProjectReservation setCreditCardExpirationYear(String creditCardExpirationYear) {
		this.creditCardExpirationYear = creditCardExpirationYear;
		return this;
	}
	public String getCreditCardHolder() {
		return creditCardHolder;
	}
	public ProjectReservation setCreditCardHolder(String creditCardHolder) {
		this.creditCardHolder = creditCardHolder;
		return this;
	}
	public String getCreditCardNumber() {
		return creditCardNumber;
	}
	public ProjectReservation setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
		return this;
	}
	public String getCrsCode() {
		return crsCode;
	}
	public ProjectReservation setCrsCode(String crsCode) {
		this.crsCode = crsCode;
		return this;
	}
	public Double getDiscountAmount() {
		return discountAmount;
	}
	public ProjectReservation setDiscountAmount(Double discountAmount) {
		this.discountAmount = discountAmount;
		return this;
	}	
	public Double getDiscountPercent() {
		return discountPercent;
	}
	public ProjectReservation setDiscountPercent(Double discountPercent) {
		this.discountPercent = discountPercent;
		return this;
	}
	public Domain getDomain() {
		return domain;
	}
	public ProjectReservation setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	public Byte getEarlyCheckOut() {
		return earlyCheckOut;
	}
	public ProjectReservation setEarlyCheckOut(Byte earlyCheckOut) {
		this.earlyCheckOut = earlyCheckOut;
		return this;
	}
	public Date getEndDate() {
		return endDate;
	}
	public ProjectReservation setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	public Date getEndTime() {
		return endTime;
	}
	public ProjectReservation setEndTime(Date endTime) {
		this.endTime = endTime;
		return this;
	}
	public Integer getHotel() {
		return hotel;
	}
	public ProjectReservation setHotel(Integer hotel) {
		this.hotel = hotel;
		return this;
	}
	public Integer getHotelReservation() {
		return hotelReservation;
	}
	public ProjectReservation setHotelReservation(Integer hotelReservation) {
		this.hotelReservation = hotelReservation;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public ProjectReservation setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public ProjectReservation setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	public Double getOtherTaxQuota() {
		return otherTaxQuota;
	}
	public ProjectReservation setOtherTaxQuota(Double otherTaxQuota) {
		this.otherTaxQuota = otherTaxQuota;
		return this;
	}
	public Integer getPenaltyDays() {
		return penaltyDays;
	}
	public ProjectReservation setPenaltyDays(Integer penaltyDays) {
		this.penaltyDays = penaltyDays;
		return this;
	}
	public Byte getPrepay() {
		return prepay;
	}
	public ProjectReservation setPrepay(Byte prepay) {
		this.prepay = prepay;
		return this;
	}
	public Integer getProject() {
		return project;
	}
	public ProjectReservation setProject(Integer project) {
		this.project = project;
		return this;
	}
	public String getRemarks() {
		return remarks;
	}
	public ProjectReservation setRemarks(String remarks) {
		this.remarks = remarks;
		return this;
	}
	public Integer getSeller() {
		return seller;
	}
	public ProjectReservation setSeller(Integer seller) {
		this.seller = seller;
		return this;
	}
	public Byte getSource() {
		return source;
	}
	public ProjectReservation setSource(Byte source) {
		this.source = source;
		return this;
	}
	public Date getStartDate() {
		return startDate;
	}
	public ProjectReservation setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	public Date getStartTime() {
		return startTime;
	}
	public ProjectReservation setStartTime(Date startTime) {
		this.startTime = startTime;
		return this;
	}
	public Byte getStatus() {
		return status;
	}
	public ProjectReservation setStatus(Byte status) {
		this.status = status;
		return this;
	}
	public Double getTaxableBase() {
		return taxableBase;
	}
	public ProjectReservation setTaxableBase(Double taxableBase) {
		this.taxableBase = taxableBase;
		return this;
	}
	public Double getTotal() {
		return total;
	}
	public ProjectReservation setTotal(Double total) {
		this.total = total;
		return this;
	}
	public Double getVatQuota() {
		return vatQuota;
	}
	public ProjectReservation setVatQuota(Double vatQuota) {
		this.vatQuota = vatQuota;
		return this;
	}
	
	public String getToken() {
		return token;
	}
	public ProjectReservation setToken(String token) {
		this.token = token;
		return this;
	}
	
	public Double getPenalty() {
		return penalty;
	}
	public ProjectReservation setPenalty(Double penalty) {
		this.penalty = penalty;
		return this;
	}
	
}
