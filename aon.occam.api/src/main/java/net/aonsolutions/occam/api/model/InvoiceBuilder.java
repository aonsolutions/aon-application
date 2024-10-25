package net.aonsolutions.occam.api.model;

import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.InvoiceSource;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.InvoiceType;
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
	public InvoiceBuilder setConfidential(boolean confidential) {
		this.invoice.getHeader().setConfidential(confidential);
		return this;
	}
	public InvoiceBuilder setSurcharge(boolean surcharge) {
		this.invoice.getHeader().setSurcharge(surcharge);
		return this;
	}
	public InvoiceBuilder setWithholding(boolean withholding) {
		if (withholding) {
			this.invoice.enableWithholding();	
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
	
	// *****************************************************************************
	// ***********************************************************[INVOICE DETAIL] *
	// *****************************************************************************
	public InvoiceBuilder addDetail() {
		return addDetail(new InvoiceDetail());
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
	public InvoiceBuilder setSeller(Seller seller) {
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

	
	// *****************************************************************************
	// ***********************************************************[INVOICE TAX] ****
	// *****************************************************************************
	public InvoiceBuilder setVatPercent(double percentage) {
		detail
			.getVatTax()
			.orElse(detail.enableVatTax())
			.setPercentage(percentage);
		return this;
	}
	public InvoiceBuilder setSurchargePercent(double percentage) {
		Optional<InvoiceTax> o = detail.getVatTax();
		if (!o.isPresent()) {
			detail.enableVatTax()
			.setSurcharge(percentage);
		} else {
			o.get().setSurcharge(percentage);
		}
		return this;
	}
	
	public InvoiceBuilder ifTaxEnabled(Consumer<InvoiceBuilder> consumer) {
		if (detail.isTaxEnabled(invoice)) {
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

	public InvoiceBuilder ifWithholdingEnabled(Consumer<InvoiceBuilder> consumer) {
		if (invoice.getHeader().isWithholding()) {
			consumer.accept(this);
		}
		return this;
	}
	
	// *****************************************************************************
	// ***********************************************************[BUILD] **********
	// *****************************************************************************
	public Invoice build() {
	return InvoiceCalculator.calculate(this.invoice);
	}

}

