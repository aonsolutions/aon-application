package net.aonsolutions.occam.api.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonObjectUtils;

import net.aonsolutions.occam.api.model.metadata.TaxMetadata;
import net.aonsolutions.occam.api.model.type.TaxType;
import net.aonsolutions.occam.api.model.type.VatDeductionType;
import net.aonsolutions.occam.api.model.type.WithholdingType;

public class Tax extends AonEntity<TaxMetadata> implements HasAudit {

	private static final long serialVersionUID = 5540194869415219773L;
	
	private Integer id;
	private Integer domain;
	private String name;
	private TaxType type;
	private double percentage;
	private double surcharge;
	private Date startDate;
	private VatDeductionType vatDeductionType;
	private WithholdingType withholdingType;
	private Account salesAccount;
	private Account purchaseAccount;
	
	private String creationUser;
	private Timestamp creationDate;
	private String modificationUser;
	private Timestamp modificationDate;
	
	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public Tax markAsClean() {
		super.markAsClean();
		return this; 
	}
	
	@Override
	public Tax setSelected( boolean selected) {
		super.setSelected(selected);
		return this; 
	}
	
	@Override
	public Tax setDeleted( boolean selected) {
		super.setDeleted(selected);
		return this; 
	}
	
	public Integer getId() {
		return id;
	}
	public Tax setId(Integer id) {
		checkIfDirty( this.id,id, TaxMetadata.ID);
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public Tax setDomain(Integer domain) {
		checkIfDirty( this.domain,domain, TaxMetadata.DOMAIN);
		this.domain = domain;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public Tax setName(String name) {
		checkIfDirty( this.name, name, TaxMetadata.NAME);
		this.name = name;
		return this;
	}
	
	public TaxType getType() {
		return type;
	}
	public Tax setType(TaxType type) {
		checkIfDirty( this.type, type, TaxMetadata.TAX_TYPE);
		this.type = type;
		return this;
	}
	
	public double getPercentage() {
		return percentage;
	}
	public Tax setPercentage(double percentage) {
		checkIfDirty( this.percentage, percentage, TaxMetadata.PERCENTAGE);
		this.percentage = percentage;
		return this;
	}
	
	public double getSurcharge() {
		return surcharge;
	}
	public Tax setSurcharge(double surcharge) {
		checkIfDirty( this.surcharge, surcharge, TaxMetadata.SURCHARGE);
		this.surcharge = surcharge;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}
	public Tax setStartDate(Date startDate) {
		checkIfDirty( this.startDate, startDate, TaxMetadata.START_DATE);
		this.startDate = startDate;
		return this;
	}
	
	public VatDeductionType getVatDeductionType() {
		return vatDeductionType;
	}
	public Tax setVatDeductionType(VatDeductionType vatDeductionType) {
		checkIfDirty( this.vatDeductionType, vatDeductionType, TaxMetadata.VAT_DEDUCTION_TYPE);
		this.vatDeductionType = vatDeductionType;
		return this;
	}
	
	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	public Tax setWithholdingType(WithholdingType withholdingType) {
		checkIfDirty( this.withholdingType, withholdingType, TaxMetadata.WITHHOLDING_TYPE);
		this.withholdingType = withholdingType;
		return this;
	}

	public Optional<Account> getSalesAccount() {
		return Optional.ofNullable(salesAccount);
	}
	public Tax setSalesAccount(Account salesAccount) {
		checkIfDirty( this.salesAccount, salesAccount, TaxMetadata.SALES_ACCOUNT);
		this.salesAccount = salesAccount;
		return this;
	}
	
	public Optional<Account> getPurchaseAccount() {
		return Optional.ofNullable(purchaseAccount);
	}
	public Tax setPurchaseAccount(Account purchaseAccount) {
		checkIfDirty( this.purchaseAccount, purchaseAccount, TaxMetadata.PURCHASE_ACCOUNT);
		this.purchaseAccount = purchaseAccount;
		return this;
	}
	
	// ---------------------------------------------------------- AUDIT
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public Tax setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	@Override
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public Tax setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Tax setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Timestamp getModificationDate() {
		return modificationDate;
	}
	public Tax setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Tax) {
			return AonObjectUtils.equals( this.getUuid(),((Tax) obj).getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + AonObjectUtils.requireNonNullElse(getUuid(), 0).hashCode();
	}
}

