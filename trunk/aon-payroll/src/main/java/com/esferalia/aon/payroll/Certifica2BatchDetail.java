package com.esferalia.aon.payroll;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.esferalia.aon.payroll.enumeration.SuspensionCause;

@Entity
@Table(name="certifica2_batch_detail")
public class Certifica2BatchDetail implements ITransferObject {

	private static final long serialVersionUID = 2224935475832431494L;

	private Integer id;
	private Certifica2Batch certifica2Batch;	
	private Contract contract;
	private String enterpriseNif;
	private String ccc;
	private String document;
	private String name;
	private String firstSurname;
	private String secondSurname;
	private String ssNumber;
	private String quoteGroup;
	private String contractType;
	private String contractDuration;
	private String contractDurationIndicator;
	private String occupationCode;
	private String publicAssociationCharge;
	private String dedicationPercent;
	private Date enterpriseStartDate;
	private SuspensionCause suspensionCause;
	private Date expireDate;
	private Date expireEndDate;
	private String ere;
	private String ereReductionPercent;
	private String otherReductionPercent;
	private String reductionCauseCode;
	private Date salaryPeriodStartDate;
	private Date salaryPeriodEndDate;
	private String salaryProcessingDays;
	
	
	@Id     
	@GeneratedValue
    @Column(name="id", unique=true, nullable=false, length=10)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(targetEntity = Certifica2Batch.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "certifica2_batch", nullable = false)
	@ForeignKey(name = "FK_CERTIFICA2_BATCH_DETAIL_CERTIFICA2_BATCH")
	@Index(name = "IDX_CERTIFICA2_BATCH_DETAIL_CERTIFICA2_BATCH")
	public Certifica2Batch getCertifica2Batch() {
		return certifica2Batch;
	}
	public void setCertifica2Batch(Certifica2Batch certifica2Batch) {
		this.certifica2Batch = certifica2Batch;
	}
	
	@ManyToOne(targetEntity = Contract.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "contract", nullable = false)
	@ForeignKey(name = "FK_CERTIFICA2_BATCH_DETAIL_CONTRACT")
	@Index(name = "IDX_CERTIFICA2_BATCH_DETAIL_CONTRACT")
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.esferalia.aon.payroll.enumeration.SuspensionCause") })
//	@Column(name = "suspension_cause", length = 2, nullable = false)
	@Column(name="suspension_cause_code", nullable = false, length = 2)
	public SuspensionCause getSuspensionCause() {
		return suspensionCause;
	}
	public void setSuspensionCause(SuspensionCause suspensionCause) {
		this.suspensionCause = suspensionCause;
	}
	
	
	
	
	@Column(name = "enterprise_nif", nullable = false, length = 9)
	public String getEnterpriseNif() {
		return enterpriseNif;
	}
	public void setEnterpriseNif(String enterpriseNif) {
		this.enterpriseNif = enterpriseNif;
	}
	
	@Column(nullable = false, length = 9)
	public String getCcc() {
		return ccc;
	}
	public void setCcc(String ccc) {
		this.ccc = ccc;
	}

	@Column(nullable = false, length = 9)
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}

	@Column(nullable = false, length = 15)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(name="first_surname", nullable = false, length = 20)
	public String getFirstSurname() {
		return firstSurname;
	}
	public void setFirstSurname(String firstSurname) {
		this.firstSurname = firstSurname;
	}

	@Column(name="second_surname", length = 20)
	public String getSecondSurname() {
		return secondSurname;
	}
	public void setSecondSurname(String secondSurname) {
		this.secondSurname = secondSurname;
	}

	@Column(name="ss_number", nullable = false, length = 20)
	public String getSsNumber() {
		return ssNumber;
	}
	public void setSsNumber(String ssNumber) {
		this.ssNumber = ssNumber;
	}

	@Column(name="quote_group", length = 2)
	public String getQuoteGroup() {
		return quoteGroup;
	}
	public void setQuoteGroup(String quoteGroup) {
		this.quoteGroup = quoteGroup;
	}

	@Column(name="contract_type", nullable = false, length = 3)
	public String getContractType() {
		return contractType;
	}
	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	@Column(name="contract_duration", length = 5)
	public String getContractDuration() {
		return contractDuration;
	}
	public void setContractDuration(String contractDuration) {
		this.contractDuration = contractDuration;
	}

	@Column(name="contract_duration_indicator", length = 1)
	public String getContractDurationIndicator() {
		return contractDurationIndicator;
	}
	public void setContractDurationIndicator(String contractDurationIndicator) {
		this.contractDurationIndicator = contractDurationIndicator;
	}

	@Column(name="occupation_code", nullable = false, length = 7)
	public String getOccupationCode() {
		return occupationCode;
	}
	public void setOccupationCode(String occupationCode) {
		this.occupationCode = occupationCode;
	}

	@Column(name="public_association_charge", length = 2)
	public String getPublicAssociationCharge() {
		return publicAssociationCharge;
	}
	public void setPublicAssociationCharge(String publicAssociationCharge) {
		this.publicAssociationCharge = publicAssociationCharge;
	}

	@Column(name="dedication_percent", length = 4)
	public String getDedicationPercent() {
		return dedicationPercent;
	}
	public void setDedicationPercent(String dedicationPercent) {
		this.dedicationPercent = dedicationPercent;
	}

	@Temporal(TemporalType.DATE)
	@Column(name="enterprise_start_date", nullable=false)
	public Date getEnterpriseStartDate() {
		return enterpriseStartDate;
	}
	public void setEnterpriseStartDate(Date enterpriseStartDate) {
		this.enterpriseStartDate = enterpriseStartDate;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "expire_date", nullable = false)
	public Date getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name="expire_end_date")
	public Date getExpireEndDate() {
		return expireEndDate;
	}
	public void setExpireEndDate(Date expireEndDate) {
		this.expireEndDate = expireEndDate;
	}
	
	@Column(length = 27)
	public String getEre() {
		return ere;
	}
	public void setEre(String ere) {
		this.ere = ere;
	}
	
	@Column(name="ere_reduction_percent", length = 4)
	public String getEreReductionPercent() {
		return ereReductionPercent;
	}
	public void setEreReductionPercent(String ereReductionPercent) {
		this.ereReductionPercent = ereReductionPercent;
	}
	
	@Column(name="other_reduction_percent", length = 4)
	public String getOtherReductionPercent() {
		return otherReductionPercent;
	}
	public void setOtherReductionPercent(String otherReductionPercent) {
		this.otherReductionPercent = otherReductionPercent;
	}
	
	@Column(name="reduction_cause_code", length = 2)
	public String getReductionCauseCode() {
		return reductionCauseCode;
	}
	public void setReductionCauseCode(String reductionCauseCode) {
		this.reductionCauseCode = reductionCauseCode;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name="salary_period_start_date")
	public Date getSalaryPeriodStartDate() {
		return salaryPeriodStartDate;
	}
	public void setSalaryPeriodStartDate(Date salaryPeriodStartDate) {
		this.salaryPeriodStartDate = salaryPeriodStartDate;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name="salary_period_end_date")
	public Date getSalaryPeriodEndDate() {
		return salaryPeriodEndDate;
	}
	public void setSalaryPeriodEndDate(Date salaryPeriodEndDate) {
		this.salaryPeriodEndDate = salaryPeriodEndDate;
	}
	
	@Column(name="salary_processing_days", length = 5)
	public String getSalaryProcessingDays() {
		return salaryProcessingDays;
	}
	public void setSalaryProcessingDays(String salaryProcessingDays) {
		this.salaryProcessingDays = salaryProcessingDays;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Certifica2BatchDetail o = (Certifica2BatchDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.certifica2Batch, o.certifica2Batch)	
				.append(this.contract, o.contract)
				.append(this.suspensionCause, o.suspensionCause)
				.append(this.enterpriseNif, o.enterpriseNif)
				.append(this.ccc, o.ccc)
				.append(this.document, o.document)
				.append(this.name, o.name)
				.append(this.firstSurname, o.firstSurname)
				.append(this.secondSurname, o.secondSurname)
				.append(this.ssNumber, o.ssNumber)
				.append(this.quoteGroup, o.quoteGroup)
				.append(this.contractType, o.contractType)
				.append(this.contractDuration, o.contractDuration)
				.append(this.contractDurationIndicator, o.contractDurationIndicator)
				.append(this.occupationCode, o.occupationCode)
				.append(this.publicAssociationCharge, o.publicAssociationCharge)
				.append(this.dedicationPercent, o.dedicationPercent)
				.append(this.enterpriseStartDate, o.enterpriseStartDate)
				.append(this.expireDate, o.expireDate)
				.append(this.expireEndDate, o.expireEndDate)
				.append(this.ere, o.ere)
				.append(this.ereReductionPercent, o.ereReductionPercent)
				.append(this.otherReductionPercent, o.otherReductionPercent)
				.append(this.reductionCauseCode, o.reductionCauseCode)
				.append(this.salaryPeriodStartDate, o.salaryPeriodStartDate)
				.append(this.salaryPeriodEndDate, o.salaryPeriodEndDate)
				.append(this.salaryProcessingDays, o.salaryProcessingDays)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(id)
				.append(this.certifica2Batch)	
				.append(this.contract)
				.append(this.suspensionCause)
				.append(this.enterpriseNif)
				.append(this.ccc)
				.append(this.document)
				.append(this.name)
				.append(this.firstSurname)
				.append(this.secondSurname)
				.append(this.ssNumber)
				.append(this.quoteGroup)
				.append(this.contractType)
				.append(this.contractDuration)
				.append(this.contractDurationIndicator)
				.append(this.occupationCode)
				.append(this.publicAssociationCharge)
				.append(this.dedicationPercent)
				.append(this.enterpriseStartDate)
				.append(this.expireDate)
				.append(this.expireEndDate)
				.append(this.ere)
				.append(this.ereReductionPercent)
				.append(this.otherReductionPercent)
				.append(this.reductionCauseCode)
				.append(this.salaryPeriodStartDate)
				.append(this.salaryPeriodEndDate)
				.append(this.salaryProcessingDays)
				.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
	
}


