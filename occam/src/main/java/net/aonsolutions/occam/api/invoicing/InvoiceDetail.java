package net.aonsolutions.occam.api.invoicing;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import net.aonsolutions.occam.api.HasDirtyFlag;
import net.aonsolutions.occam.api.config.Audit;
import net.aonsolutions.occam.api.constants.InvoiceSource;
import net.aonsolutions.watson.client.util.AonNumberUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class InvoiceDetail implements Serializable, HasDirtyFlag<InvoiceDetail> {

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
	
	private boolean dirty;

	public Integer getId() {
		return id;
	}
	public InvoiceDetail setId(Integer id) {
		this.dirtyMark( AonObjectUtils.notEquals(this.id,id) );
		this.id = id;
		return this;
	}
	
	public Integer getItem() {
		return item;
	}
	public InvoiceDetail setItem(Integer item) {
		this.dirtyMark( AonObjectUtils.notEquals(this.item,item) );
		this.item = item;
		return this;
	}
	public Integer getLine() {
		return line;
	}
	public InvoiceDetail setLine(Integer line) {
		this.dirtyMark( AonObjectUtils.notEquals(this.line,line) );
		this.line = line;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public InvoiceDetail setDescription(String description) {
		this.dirtyMark( AonObjectUtils.notEquals(this.description,description) );
		this.description = description;
		return this;
	}
	public Double getQuantity() {
		return quantity;
	}
	public InvoiceDetail setQuantity(Double quantity) {
		this.dirtyMark( AonObjectUtils.notEquals(this.quantity,quantity) );
		this.quantity = quantity;
		return this;
	}
	public Double getPrice() {
		return price;
	}
	public InvoiceDetail setPrice(Double price) {
		this.dirtyMark( AonObjectUtils.notEquals(this.price,price) );
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
		this.dirtyMark( AonObjectUtils.notEquals(this.discountExpression,discountExpression) );
		this.discountExpression = discountExpression;
		return this;
	}
	public InvoiceSource getSource() {
		return source;
	}
	public InvoiceDetail setSource(InvoiceSource source) {
		this.dirtyMark( AonObjectUtils.notEquals(this.source,source) );
		this.source = source;
		return this;
	}
	public Integer getSourceId() {
		return sourceId;
	}
	public InvoiceDetail setSourceId(Integer sourceId) {
		this.dirtyMark( AonObjectUtils.notEquals(this.sourceId,sourceId) );
		this.sourceId = sourceId;
		return this;
	}
	public Double getTaxableBase() {
		return taxableBase;
	}
	public InvoiceDetail setTaxableBase(Double taxableBase) {
		this.dirtyMark( AonObjectUtils.notEquals(this.taxableBase,taxableBase) );
		this.taxableBase = taxableBase;
		return this;
	}
	public boolean isPrepayment() {
		return prepayment;
	}
	public InvoiceDetail setPrepayment(boolean prepayment) {
		this.dirtyMark( AonObjectUtils.notEquals(this.prepayment,prepayment) );
		this.prepayment = prepayment;
		return this;
	}

	public Optional<List<InvoiceTax>> getTaxes() {
		return Optional.ofNullable(taxes);
	}
	public InvoiceDetail setTaxes(List<InvoiceTax> invoiceTaxes) {
		this.dirtyMark( AonObjectUtils.notEquals(this.taxes,invoiceTaxes) );
		this.taxes = new LinkedList<>();
		this.taxes.addAll(taxes);
		return this;
	}
	public InvoiceDetail addTax(InvoiceTax invoiceTax) {
		this.dirtyMark( invoiceTax.isDirty() );
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
	public boolean isDirty() {
		return dirty;
	}
	@Override
	public InvoiceDetail setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof InvoiceDetail other) {
			return AonNumberUtils.equals(this.id,other.id);
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(id, 0).hashCode();
	}
}
