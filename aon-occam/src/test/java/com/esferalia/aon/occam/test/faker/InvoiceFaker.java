package com.esferalia.aon.occam.test.faker;

import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.github.javafaker.Faker;

public class InvoiceFaker {
	private static Faker faker = new Faker(new Locale("es"));
	
	public static class InvoiceFakerParams {
		private AONContext ctx;
		private AonConfiguration config;
		private Date issueDate;
		
		public InvoiceFakerParams(AONContext ctx, AonConfiguration config) {
			this.ctx = ctx;
			this.config = config;
		}
		
		public AONContext getCtx() {
			return ctx;
		}
		public InvoiceFakerParams setCtx(AONContext ctx) {
			this.ctx = ctx;
			return this;
		}
		public AonConfiguration getConfig() {
			return config;
		}
		public InvoiceFakerParams setConfig(AonConfiguration config) {
			this.config = config;
			return this;
		}
		public Date getIssueDate() {
			return issueDate;
		}
		public InvoiceFakerParams setIssueDate(Date issueDate) {
			this.issueDate = issueDate;
			return this;
		}
		
	}
	
	private static enum InvoiceFakerTypes {
		SALES_CAN_CEU_MEL {
			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.SALES);
				if (params.getIssueDate() != null) {
					invoice.setIssueDate(params.getIssueDate());
					invoice.setTaxDate(params.getIssueDate());
				}
				invoice.setTransaction(InvoiceTransactionType.CAN_CEU_MEL);
				invoice.setSurcharge(false);
				invoice.setWithholding(false);
				invoice.setService(false);
				InvoiceFaker.fill(params, invoice);
				return invoice;
			}
		},
		SALES_CAN_CEU_MEL_SERVICE {
			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.SALES);
				if (params.getIssueDate() != null) {
					invoice.setIssueDate(params.getIssueDate());
					invoice.setTaxDate(params.getIssueDate());
				}
				invoice.setTransaction(InvoiceTransactionType.CAN_CEU_MEL);
				invoice.setSurcharge(false);
				invoice.setWithholding(false);
				invoice.setService(true);
				InvoiceFaker.fill(params, invoice);
				return invoice;
			}
		}
		;
		public abstract Invoice get( InvoiceFakerParams params );
		
	}

	
	private static Invoice getHeader( InvoiceFakerParams params, InvoiceType invoiceType) {
		Invoice invoice = new Invoice();
		invoice.setDomain(params.getCtx().getDomainId());
		Date issueDate = faker.date().past(10, TimeUnit.DAYS);
		invoice.setIssueDate( issueDate );
		invoice.setTaxDate( issueDate );
		invoice.setType( invoiceType );
		if (invoiceType == InvoiceType.SALES) {
			String series = AonRandom.string(4); 
			invoice.setSeries(series);
			invoice.setNumber(InvoiceDAO.getNextNumber(params.getCtx(), InvoiceType.SALES , series));
		} else {
			invoice.setReferenceCode(AonRandom.string(15));	
		}
		invoice.getType().visit(invoice, new IInvoiceTypeVisitor() {
			
			private void fillRegistryData(Invoice invoice, Registry reg) {
				invoice.setRegistry(reg.getId());
				invoice.setRegistryDocumentType(reg.getDocumentType());
				invoice.setRegistryDocumentCountry(reg.getDocumentCountry());
				invoice.setRegistryDocument(reg.getDocument());
				invoice.setRegistryName(reg.getName());
			}
			
			@Override
			public void visitSales(Invoice invoice) {
				Customer customer = AonRandom.getCustomer( params.getCtx() );
				fillRegistryData(invoice, customer);
				invoice.setScope(new Scope().setId( customer.getScope() ));
				invoice.setTransaction( customer.getTransaction() );
				invoice.setService( AonRandom.gt(80) );
				invoice.setVatAccrualPayment(invoice.isNational() && params.getConfig().getCompany().isVatAccrualPayment());
				invoice.setSeries(params.getConfig().getDefaultInvoiceSeries());
				invoice.setNumber( InvoiceDAO.getNextNumber(params.getCtx(), new Byte[]{invoice.getType().value()}, invoice.getSeries()));
				invoice.setSurcharge(customer.isSurcharge());
				invoice.setWithholding(customer.isWithholding() && params.getConfig().getCompany().isWithholding());
				invoice.setWithholdingFarmer(false);
			}
			
			@Override
			public void visitPurchase(Invoice invoice) {
				Supplier supplier = AonRandom.getSupplier( params.getCtx() );
				fillRegistryData(invoice, supplier);
				invoice.setScope(new Scope().setId( supplier.getScope() ));
				invoice.setTransaction(supplier.getTransaction());
				invoice.setService( AonRandom.gt(40) );
				invoice.setVatAccrualPayment(invoice.isNational() && supplier.isVatAccrualPayment());
				invoice.setSurcharge(params.getConfig().getCompany().isSurcharge());
				invoice.setWithholding(supplier.isWithholding());
				invoice.setWithholdingFarmer(supplier.isWithholdingFarmer());
			}
			
			@Override
			public void visitExpenses(Invoice invoice) {
				Creditor creditor = AonRandom.getCreditor( params.getCtx() );
				invoice.setScope(new Scope().setId( creditor.getScope() ));
				fillRegistryData(invoice, creditor);
				invoice.setTransaction( creditor.getTransaction() );
				invoice.setService( AonRandom.gt(50) );
				invoice.setVatAccrualPayment(invoice.isNational() && creditor.isVatAccrualPayment());
				invoice.setSurcharge(false);
				invoice.setWithholding(creditor.isWithholding());
				invoice.setWithholdingFarmer(false);
			}
			@Override
			public void visitUndeductible(Invoice invoice) {
				visitExpenses(invoice);
				invoice.setSurcharge(false);
				invoice.setWithholding(false);
				invoice.setWithholdingFarmer(false);
				invoice.setVatAccrualPayment(false);
				invoice.setService( true );
			}
		});
		EnterpriseActivity activity = AonRandom.getRandomActivity(params.getCtx());
		invoice.setActivity(activity==null?null:activity.getId());
		return invoice;
	}
	
	public static Invoice fill( InvoiceFakerParams params , Invoice invoice) {
		invoice.setDetails( getInvoiceDetails(params, invoice ));
		return invoice;
	}

	private static LinkedList<InvoiceDetail> getInvoiceDetails(InvoiceFakerParams params, Invoice invoice) {
		LinkedList<InvoiceDetail> details = new LinkedList<InvoiceDetail>();
		int times = AonRandom.number(1, 25);
		for (int i = 0; i < times; i++) {
			details.add(getInvoiceDetail( params, invoice));
		}
		return details;
	}

	private static InvoiceDetail getInvoiceDetail(InvoiceFakerParams params, Invoice invoice) {
		
		InvoiceDetail detail = new InvoiceDetail();
		detail.setDomain(invoice.getDomain())
			.setInvoice(invoice)
			.setWorkPlace( params.getConfig().getWorkplaces().getFirst().getId() )
			.setDescription(AonRandom.string(5, 50))
			.setQuantity(AonRandom.getDouble(0, 10))
			.setDiscountExpression( AonRandom.gt(10)
					?null 
					:AonNumberUtils.toString( AonRandom.getDouble(0, 100)))
			.setPrice(AonRandom.getDouble(0, 10000))
			.setSource(InvoiceSource.DIRECT_INVOICE) // Todo. Alternative.
			.setInvoiceTaxes( getInvoiceTaxes(params,invoice, detail ))
			.setPrepayment(AonRandom.gt(98))
		;
		return calculate(detail);
	}

	private static LinkedList<InvoiceTax> getInvoiceTaxes(InvoiceFakerParams params, Invoice invoice, InvoiceDetail detail) {
		if (detail.isPrepayment()) return null;
		LinkedList<InvoiceTax> invoiceTaxes = new LinkedList<InvoiceTax>();
		invoiceTaxes.add(getVatInvoiceTax(params, invoice, detail));
		if ( invoice.isWithholding()) {
			invoiceTaxes.add(getRetentionInvoiceTax(params, invoice, detail));	
		}
		return invoiceTaxes;
	}
	private static InvoiceTax getRetentionInvoiceTax(InvoiceFakerParams params, Invoice invoice, InvoiceDetail detail) {
		InvoiceTax tax =  new InvoiceTax()
				.setTaxType(TaxType.RETENTION)
				.setBase(detail.getTaxableBase())
				.setPercentage( getRetentionPercent( AonRandom.number(0, 100)) )
				.setSurcharge(0.0)
				.setDeductiblePercent(getDeductiblePercent( AonRandom.number(0, 100)));
			;
			return calculate(tax);
	}

	private static InvoiceTax getVatInvoiceTax(InvoiceFakerParams params, Invoice invoice, InvoiceDetail detail) {
		InvoiceTax tax =  new InvoiceTax()
			.setTaxType(TaxType.VAT)
			.setBase(detail.getTaxableBase())
			.setPercentage( getVatPercent( AonRandom.number(0, 100)) )
			.setSurcharge(invoice.isSurcharge()?getSurchargePercent( AonRandom.number(0, 100)):0.0)
			.setDeductiblePercent( getDeductiblePercent( AonRandom.number(0, 100)));
		;
		return calculate(tax);
	}
	
	private static double getVatPercent(int x) {
		if ( x >= 0 && x <= 50) return 21.0;
		if ( x > 50 && x <= 75) return 10.0;
		if ( x > 75 && x <= 95) return 4.0;
		return 0.0;
	}
	
	private static double getSurchargePercent(int x) {
		if ( x >= 0 && x <= 50) return 5.2;
		if ( x > 50 && x <= 75) return 1.4;
		if ( x > 75 && x <= 95) return 0.5;
		return 0.0;
	}

	private static double getDeductiblePercent(int x) {
		if ( x >= 0 && x <= 50) return 0.0;
		if ( x > 50 && x <= 85) return 100.0;
		return AonRandom.number(0, 100);
	}

	private static double getRetentionPercent(int x) {
		if ( x >= 0 && x <= 50) return 15.0;
		if ( x > 50 && x <= 75) return 10.0;
		if ( x > 75 && x <= 95) return 8.0;
		return 0.0;
	}

	private static InvoiceDetail calculate(InvoiceDetail detail) {
		double taxableBase = (detail.getPrice() + detail.getTaxes()) * detail.getQuantity();
		double[] discounts = getDiscounts(detail.getDiscountExpression());
		if ( discounts != null) {
			for (double discount : discounts) {
				taxableBase = taxableBase * ( 1 - discount /100);
			}
		}
		taxableBase = AonMathUtils.round(taxableBase, 4); 
		detail.setTaxableBase( taxableBase );
		if (detail.isPrepayment()) {
			detail.setInvoiceTaxes(null);
		} else {
			for (InvoiceTax tax  : detail.getInvoiceTaxes()) {
				tax.setBase(taxableBase);
				calculate(tax);
			}
		}
		return detail;
	}
	
	private static double[] getDiscounts(String discountExpr) {
		if (AonStringUtils.isBlank(discountExpr)) return null;
		String[] arr = AonStringUtils.split(discountExpr,'+');
		double[] discounts = new double[arr.length];
		for (int i = 0; i < arr.length; i++) {
			String discount = arr[i];
			discounts[i] = Double.parseDouble(discount.trim());
		}
		return discounts;
	}

	private static InvoiceTax calculate(InvoiceTax tax) {
		double quota = AonMathUtils.round(tax.getBase() * tax.getPercentage() / 100);
		tax.setQuota(quota);
		if (AonMathUtils.isZero(tax.getDeductiblePercent()) || AonMathUtils.equals(tax.getDeductiblePercent(), 100)) {
			tax.setDeductibleQuota(quota);	
		} else {
			tax.setDeductibleQuota(AonMathUtils.round(tax.getQuota() * tax.getDeductiblePercent() / 100));
		}
		if ( tax.getTaxType() == TaxType.VAT) {
			if (AonMathUtils.isZero(tax.getSurcharge())) {
				tax.setSurchargeQuota(AonMathUtils.round(tax.getBase() * tax.getSurcharge() / 100));
			}
		} else {
			tax.setSurcharge(0.0);
			tax.setSurchargeQuota(0.0);
		}
		return tax;
	}

	public static Invoice getRandom(InvoiceFakerParams params) {
		return AonRandom.randomEnum(InvoiceFakerTypes.class).get(params);
	}
	public static Invoice getSalesCanCeuService(InvoiceFakerParams params) {
		return InvoiceFakerTypes.SALES_CAN_CEU_MEL_SERVICE.get(params);
	}
	public static Invoice getSalesCanCeu(InvoiceFakerParams params) {
		return InvoiceFakerTypes.SALES_CAN_CEU_MEL.get(params);
	}

}

