package net.aonsolutions.occam.api.model;

import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.InvoiceSource;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType.InvoiceTransactionTypeVisitor;
import net.aonsolutions.occam.api.model.type.InvoiceType;
import net.aonsolutions.occam.api.model.type.InvoiceType.InvoiceTypeVisitor;
import net.aonsolutions.occam.api.model.type.WithholdingType;

public class InvoiceBuilder {
	
	private Invoice invoice;
	private InvoiceDetail detail;
	
	// *****************************************************************************
	// ***********************************************************[INVOICE] ********
	// *****************************************************************************
	public InvoiceBuilder invoice( Invoice invoice ) {
		this.invoice =  invoice;
		return this;
	}
	
	public InvoiceBuilder setId(Integer id) {
		this.invoice.getHeader().setId(id);
		return this;
	}
	public InvoiceBuilder setDomain(Integer domain) {
		this.invoice.getHeader().setDomain(domain);
		return this;
	}
	public InvoiceBuilder setActivity(Activity activity) {
		this.invoice.getHeader().setActivity(activity);
		return this;
	}
	public InvoiceBuilder setProject(Integer project) {
		this.invoice.getHeader().setProject(project);
		return this;
	}
	public InvoiceBuilder setScope(Integer scope) {
		this.invoice.getHeader().setScope(scope);
		return this;
	}
	public InvoiceBuilder setType(InvoiceType type) {
		this.invoice.getHeader().setType(type);
		return this;
	}
	public InvoiceBuilder setSeries(String series) {
		this.invoice.getHeader().setSeries(series);
		return this;
	}
	public InvoiceBuilder setNumber(Integer number) {
		this.invoice.getHeader().setNumber(number);
		return this;
	}
	
	public InvoiceBuilder setReferenceCode(String referenceCode) {
		this.invoice.getHeader().setReferenceCode(referenceCode);
		return this;
	}
	public InvoiceBuilder setTransaction(InvoiceTransactionType transaction) {
		this.invoice.getHeader().setTransaction(transaction);
		return this;
	}
	public InvoiceBuilder setIssueDate(Date issueDate) {
		this.invoice.getHeader().setIssueDate(issueDate);
		return this;
	}
	public InvoiceBuilder setTaxDate(Date taxDate) {
		this.invoice.getHeader().setTaxDate(taxDate);
		return this;
	}
	public InvoiceBuilder setRegistry(Integer registry) {
		this.invoice.getHeader().setRegistry(registry);
		return this;
	}
	public InvoiceBuilder setRegistryDocument(String registryDocument) {
		this.invoice.getHeader().setRegistryDocument(registryDocument);
		return this;
	}
	public InvoiceBuilder setRegistryDocumentType(DocumentType registryDocumentType) {
		this.invoice.getHeader().setRegistryDocumentType(registryDocumentType);
		return this;
	}
	public InvoiceBuilder setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.invoice.getHeader().setRegistryDocumentCountry(registryDocumentCountry);
		return this;
	}
	public InvoiceBuilder setRegistryName(String registryName) {
		this.invoice.getHeader().setRegistryName(registryName);
		return this;
	}
	public InvoiceBuilder setSeller(Seller seller) {
		this.invoice.getHeader().setSeller(seller);
		return this;
	}
	public InvoiceBuilder setConfidential(boolean confidential) {
		this.invoice.getHeader().setConfidential(confidential);
		return this;
	}
	public InvoiceBuilder setSurcharge(boolean surcharge) {
		this.invoice.getHeader().setSurcharge(surcharge);
		return this;
	}
	public InvoiceBuilder setService(boolean service) {
		this.invoice.getHeader().setService(service);
		return this;
	}
	public InvoiceBuilder setVatAccrualPayment(boolean vatAccrualPayment) {
		this.invoice.getHeader().setVatAccrualPayment(vatAccrualPayment);
		return this;
	}
	public InvoiceBuilder setVatImportation(boolean vatImportation) {
		this.invoice.setVatImportation(vatImportation);
		return this;
	}
	public InvoiceBuilder setWithholding(boolean withholding) {
		return this.setWithholding(withholding, false);
	}
	public InvoiceBuilder setWithholdingFarmer(boolean withholding) {
		return this.setWithholding(true, withholding);
	}
	private InvoiceBuilder setWithholding(boolean withholding, boolean withholdingFarmer) {
		if (withholding || withholdingFarmer) {
			InvoiceWithholding iw = new InvoiceWithholding()
				.setWithholdingType(withholdingFarmer?WithholdingType.FARMER : WithholdingType.PROFESSIONAL );
			this.invoice.enableWithholding(iw);	
		} else {
			this.invoice.disableWithholding();
		}
		return this;
	}
	
	public InvoiceBuilder setWithholdingPercent(double percent) {
		InvoiceWithholding iw = this.invoice.getWithholding()
			.orElse(new InvoiceWithholding()
					.setWithholdingType(WithholdingType.PROFESSIONAL));
		iw.setPercentage(percent);
		this.invoice.enableWithholding(iw);
		return this;
	}
	public InvoiceBuilder setWithholdingType(WithholdingType withholdingType) {
		InvoiceWithholding iw = this.invoice.getWithholding()
			.orElse(new InvoiceWithholding());
		iw.setWithholdingType(withholdingType);
		this.invoice.enableWithholding(iw);
		return this;
	}
	
	public InvoiceBuilder setInvoiceAddress(InvoiceAddress invoiceAddress) {
		if (invoiceAddress != null) {
			invoiceAddress.setInvoice(invoice.getHeader().getId());
			invoiceAddress.setDomain(invoice.getHeader().getDomain());
			invoice.setInvoiceAddress(invoiceAddress);
		}
		return this;
	}
	
	public InvoiceBuilder ifWithholdingEnabled(Consumer<InvoiceBuilder> consumer) {
		if (invoice.getHeader().isWithholding()) {
			consumer.accept(this);
		}
		return this;
	}
	
	// *****************************************************************************
	// ***********************************************************[INVOICE DETAIL] *
	// *****************************************************************************
	public InvoiceBuilder addDetail() {
		return addDetail(
			new InvoiceDetail()
				.setDomain(invoice.getHeader().getDomain())
				.setInvoice(invoice.getHeader().getId())
				.setSeller(invoice.getHeader().getSeller().orElse(null))
		);
	}
	public InvoiceBuilder addDetail(InvoiceDetail detail) {
		this.detail = detail;
		invoice.addDetail(detail);
		return this;
	}
	public InvoiceBuilder setDescription(String description) {
		detail.setDescription(description);
		return this;
	}
	public InvoiceBuilder setQuantity(double quantity) {
		detail.setQuantity(quantity);
		return this;
	}
	public InvoiceBuilder setPrice(double price) {
		detail.setPrice(price);
		return this;
	}
	public InvoiceBuilder setTaxableBase(double taxableBase) {
		detail.setTaxableBase(taxableBase);
		return this;
	}
	public InvoiceBuilder setDiscount(double discount) {
		detail.setDiscountExpression(new DiscountExpression().setDiscount(discount));
		return this;
	}
	public InvoiceBuilder setPrepayment(boolean prepayment) {
		detail.setPrepayment(prepayment);
		return this;
	}
	public InvoiceBuilder setSource(InvoiceSource source) {
		detail.setSource(source);
		return this;
	}
	public InvoiceBuilder setDetailSeller(Seller seller) {
		detail.setSeller(seller);
		return this;
	}
	public InvoiceBuilder setInvestAsset(InvestAsset investAsset) {
		detail.setInvestAsset(investAsset);
		return this;
	}
	public InvoiceBuilder setWorkplace(Integer workplace) {
		detail.setWorkplace(workplace);
		return this;
	}
	public InvoiceBuilder visit(InvoiceTypeVisitor<InvoiceBuilder> visitor) {
		if (invoice.getHeader().getType() == null) {
			throw new IllegalStateException("Set the invoice type first!");
		}
		return invoice.getHeader().getType().visit(visitor);
	}
	
	public InvoiceBuilder ifDetailTaxEnabled(Consumer<InvoiceBuilder> consumer) {
		if (detail.isTaxEnabled(invoice)) {
			consumer.accept(this);
		}
		return this;
	}
	
	public InvoiceBuilder ifDetailSurchargeEnabled(Consumer<InvoiceBuilder> consumer) {
		if (detail.isTaxEnabled(invoice) && invoice.getHeader().isSurcharge()) {
			consumer.accept(this);
		}
		return this;
	}
	
	public InvoiceBuilder ifInvestAssetEnabled(Consumer<InvoiceBuilder> consumer) {
		if (detail.getInvestAsset().isPresent()) {
			consumer.accept(this);
		}
		return this;
	}

	
	// *****************************************************************************
	// ***********************************************************[INVOICE TAX] ****
	// *****************************************************************************
	public InvoiceBuilder setVatPercent(double percentage) {
		if ( detail.isTaxEnabled(invoice) ) {
			if (!invoice.isVatEnabled()) percentage = 0.0;
			detail
				.getVatTax()
				.orElse(detail.enableVatTax())
				.setPercentage(percentage);
		}
		return this;
	}
	public InvoiceBuilder setSurchargePercent(double percentage) {
		if ( detail.isTaxEnabled(invoice) ) {
			detail.getVatTax()
				.ifPresentOrElse(
					v -> v.setSurcharge(percentage)
					,() -> {throw new IllegalStateException("Agrega primero el porcentaje de IVA.");}
			);
		}
		return this;
	}
	
	// *****************************************************************************
	// ***********************************************************[BUILD] **********
	// *****************************************************************************
	public Invoice build() {
		check();
		return InvoiceCalculator.calculate(this.invoice);
	}
	
	private void check() {
		invoice.getHeader().getTransaction().visit( new InvoiceTransactionTypeVisitor<Void>() {
			
			@Override
			public Void visitOtherISP() {
				setVatImportation(false);
				setWithholding(false,false);
				return null;
			}
			
			@Override
			public Void visitNational() {
				setVatImportation(false);
				return null;
			}
			
			@Override
			public Void visitIntracommunity() {
				setVatImportation(false);
				setWithholding(false,false);
				setVatAccrualPayment(false);
				return null;
			}
			
			@Override
			public Void visitExtracommunity() {
				setWithholding(false,false);
				setVatAccrualPayment(false);
				return null;
			}
			
			@Override
			public Void visitCanCeuMel() {
				visitExtracommunity();
				return null;
			}
		});
	}

}

