package net.aonsolutions.occam.api.invoicing;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import net.aonsolutions.occam.api.OccamEntity;
import net.aonsolutions.occam.api.config.Audit;
import net.aonsolutions.occam.api.constants.InvoiceSource;
import net.aonsolutions.occam.api.metadata.InvoiceDetailMetadata;
import net.aonsolutions.watson.server.AonObjectUtils;

public class InvoiceDetail extends OccamEntity<InvoiceDetailMetadata> {

	private static final long serialVersionUID = 7597157186868662372L;
	
	private Integer id;
	private Integer item;
	private Integer line;
	private String description;
	private Double quantity;
	private Double price;
	private String discountExpression;
	private Double taxableBase;
	private boolean prepayment;
	private InvoiceSource source;
	private Integer sourceId;
	private LinkedList<InvoiceTax> taxes;
	private Audit audit;	
	

	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public InvoiceDetail markAsClean() {
		super.markAsClean();
		return this;
	}

	public Integer getId() {
		return id;
	}
	public InvoiceDetail setId(Integer id) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.id,id), () -> markAsDirty(InvoiceDetailMetadata.ID));
		this.id = id;
		return this;
	}
	
	public Integer getItem() {
		return item;
	}
	public InvoiceDetail setItem(Integer item) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.item,item), () -> markAsDirty(InvoiceDetailMetadata.ITEM));
		this.item = item;
		return this;
	}
	public Integer getLine() {
		return line;
	}
	public InvoiceDetail setLine(Integer line) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.line,line), () -> markAsDirty(InvoiceDetailMetadata.LINE));
		this.line = line;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public InvoiceDetail setDescription(String description) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.description,description), () -> markAsDirty(InvoiceDetailMetadata.DESCRIPTION));
		this.description = description;
		return this;
	}
	public Double getQuantity() {
		return quantity;
	}
	public InvoiceDetail setQuantity(Double quantity) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.quantity,quantity), () -> markAsDirty(InvoiceDetailMetadata.QUANTITY));
		this.quantity = quantity;
		return this;
	}
	public Double getPrice() {
		return price;
	}
	public InvoiceDetail setPrice(Double price) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.price,price), () -> markAsDirty(InvoiceDetailMetadata.PRICE));
		this.price = price;
		return this;
	}
	public String getDiscountExpression() {
		if(discountExpression == null) {
			discountExpression = "0.0";
		}
		return discountExpression;
	}
	public InvoiceDetail setDiscountExpression(String discountExpression) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.discountExpression,discountExpression), () -> markAsDirty(InvoiceDetailMetadata.DISCOUNT_EXPRESSION));
		this.discountExpression = discountExpression;
		return this;
	}
	public InvoiceSource getSource() {
		return source;
	}
	public InvoiceDetail setSource(InvoiceSource source) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.source,source), () -> markAsDirty(InvoiceDetailMetadata.SOURCE));
		this.source = source;
		return this;
	}
	public Integer getSourceId() {
		return sourceId;
	}
	public InvoiceDetail setSourceId(Integer sourceId) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.sourceId,sourceId), () -> markAsDirty(InvoiceDetailMetadata.SOURCE_ID));
		this.sourceId = sourceId;
		return this;
	}
	public Double getTaxableBase() {
		return taxableBase;
	}
	public InvoiceDetail setTaxableBase(Double taxableBase) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.taxableBase,taxableBase), () -> markAsDirty(InvoiceDetailMetadata.TAXABLE_BASE));
		this.taxableBase = taxableBase;
		return this;
	}
	public boolean isPrepayment() {
		return prepayment;
	}
	public InvoiceDetail setPrepayment(boolean prepayment) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.prepayment,prepayment), () -> markAsDirty(InvoiceDetailMetadata.PREPAYMENT));
		this.prepayment = prepayment;
		return this;
	}

	public Optional<List<InvoiceTax>> getTaxes() {
		return Optional.ofNullable(taxes);
	}
	public InvoiceDetail setTaxes(List<InvoiceTax> taxes) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.taxes,taxes), () -> markAsDirty(InvoiceDetailMetadata.TAXES));
		this.taxes = new LinkedList<>();
		this.taxes.addAll(taxes);
		return this;
	}
	public InvoiceDetail addTax(InvoiceTax invoiceTax) {
		AonObjectUtils.ifTrue(invoiceTax.isDirty(), () -> markAsDirty(InvoiceDetailMetadata.TAXES));
		getTaxes().orElse(new LinkedList<>()).add(invoiceTax);
		return this;
	}

	// ---------------------------------------------------------- AUDIT
	public Optional<Audit> getAudit() {
		return Optional.ofNullable(audit);
	}
	public InvoiceDetail setAudit(Audit audit) {
		this.audit = audit;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof InvoiceDetail other) {
			return AonObjectUtils.equals(this.getUuid(),other.getUuid());
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
}
