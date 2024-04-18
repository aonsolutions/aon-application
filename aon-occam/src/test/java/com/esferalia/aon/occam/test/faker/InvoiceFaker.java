package com.esferalia.aon.occam.test.faker;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTransactionTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.github.javafaker.Faker;

public class InvoiceFaker {
	private static Faker faker = new Faker( new Locale("es") );
	
	public static class InvoiceFakerParams {
		private AONContext ctx;
		private AonConfiguration config;
		private Date issueDate;
		private InvoiceWithholding withholding;
		private boolean mustForceRegistry;
		
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

		public InvoiceWithholding getWithholding() {
			return withholding;
		}
		public InvoiceFakerParams setWithholding(InvoiceWithholding withholding) {
			this.withholding = withholding;
			return this;
		}

		public boolean mustForceRegistry() {
			return mustForceRegistry;
		}

		public InvoiceFakerParams setMustForceRegistry(boolean mustForceRegistry) {
			this.mustForceRegistry = mustForceRegistry;
			return this;
		}
		
	}
	
	private static void fillHeader( Invoice invoice, InvoiceFakerParams params) {
		if (params.getIssueDate() != null) {
			invoice.setIssueDate(params.getIssueDate());
			invoice.setTaxDate(params.getIssueDate());
		}
		invoice.setSurcharge(false);
		invoice.setWithholding( params.getWithholding() != null );
	}
	
	private static enum InvoiceFakerTypes {
		// Venta Nacional
		SALES_NATIONAL {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }
			
			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.SALES);
				invoice.setTransaction(InvoiceTransactionType.NATIONAL);
				return InvoiceFaker.fill(params, invoice);
			}
		},
		// Venta Nacional
		SALES_NATIONAL_SURCHARGE {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }

			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.SALES);
				invoice.setTransaction(InvoiceTransactionType.NATIONAL);
				invoice.setSurcharge(true);
				return InvoiceFaker.fill(params, invoice);
			}
		},
		// Venta Canarias, Ceuta y Melilla
		SALES_CAN_CEU_MEL {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }

			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.SALES);
				invoice.setTransaction(InvoiceTransactionType.CAN_CEU_MEL);
				return InvoiceFaker.fill(params, invoice);
			}
		},
		// Prestacion de servicio Canarias, Ceuta y Melilla
		SALES_CAN_CEU_MEL_SERVICE {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }

			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.SALES);
				invoice.setTransaction(InvoiceTransactionType.CAN_CEU_MEL);
				invoice.setService(true);
				return InvoiceFaker.fill(params, invoice);
			}
		},
		// Venta Nacional Criterio de caja
		SALES_NATIONAL_ACCRUAL_PAYMENT {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }

			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.SALES);
				invoice.setTransaction(InvoiceTransactionType.NATIONAL);
				invoice.setVatAccrualPayment(true);
				return InvoiceFaker.fill(params, invoice);
			}
		},
		// Compra Nacional 
		PURCHASE_NATIONAL {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.PURCHASE);
				invoice.setTransaction(InvoiceTransactionType.NATIONAL);
				return InvoiceFaker.fill(params, invoice);
			}
		},
		// Compra Nacional Criterio de caja
		PURCHASE_NATIONAL_ACCRUAL_PAYMENT {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }
			
			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.PURCHASE);
				invoice.setTransaction(InvoiceTransactionType.NATIONAL);
				invoice.setVatAccrualPayment(true);
				return InvoiceFaker.fill(params, invoice);
			}
		},
		// Compra Intracomunitaria
		PURCHASE_INTRACOMMUNITY {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.PURCHASE);
				invoice.setTransaction(InvoiceTransactionType.INTRACOMMUNITY);
				invoice.setVatImportation(false);
				return InvoiceFaker.fill(params, invoice);
			}
		},
		// Compra ISP
		PURCHASE_ISP {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.PURCHASE);
				invoice.setTransaction(InvoiceTransactionType.OTHER_ISP);
				invoice.setVatImportation(false);
				return InvoiceFaker.fill(params, invoice);
			}
		},
		// Compra Nacional
		PURCHASE_EXTRACOMMUNITY {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.PURCHASE);
				invoice.setTransaction(InvoiceTransactionType.EXTRACOMMUNITY);
				invoice.setVatImportation(false);
				return InvoiceFaker.fill(params, invoice);
			}
		},
		// Compra Nacional Reg Importacion
		PURCHASE_EXTRACOMMUNITY_VAT_IMPORT {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.PURCHASE);
				invoice.setTransaction(InvoiceTransactionType.EXTRACOMMUNITY);
				invoice.setVatImportation(true);
				return InvoiceFaker.fill(params, invoice);
			}
		},
		// Compra Canarias, Ceuta, Melilla
		PURCHASE_CAN_CEU_MEL {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.PURCHASE);
				invoice.setTransaction(InvoiceTransactionType.EXTRACOMMUNITY);
				invoice.setVatImportation(false);
				return InvoiceFaker.fill(params, invoice);
			}
		},
		// Compra Canarias, Ceuta, Melilla Reg Importacion
		PURCHASE_CAN_CEU_MEL_VAT_IMPORT {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.PURCHASE);
				invoice.setTransaction(InvoiceTransactionType.EXTRACOMMUNITY);
				invoice.setVatImportation(true);
				return InvoiceFaker.fill(params, invoice);
			}
		},
		
		// Gasto Nacional
		EXPENSES_NATIONAL {
			@Override 
			public InvoiceType getType() { return InvoiceType.EXPENSES; }

			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.EXPENSES);
				invoice.setTransaction(InvoiceTransactionType.NATIONAL);
				InvoiceFaker.fill(params, invoice);
				return invoice;
			}
		},
		// Gasto Nacional Criterio de caja
		EXPENSES_NATIONAL_ACCRUAL_PAYMENT {
			@Override 
			public InvoiceType getType() { return InvoiceType.EXPENSES; }

			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.EXPENSES);
				invoice.setTransaction(InvoiceTransactionType.NATIONAL);
				invoice.setVatAccrualPayment(true);
				return InvoiceFaker.fill(params, invoice);
			}
		},
		// Gasto nacional con retención. Se debe suministrar en 
		EXPENSES_RETENTION {
			@Override 
			public InvoiceType getType() { return InvoiceType.EXPENSES; }
			
			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.EXPENSES);
				invoice.setTransaction(InvoiceTransactionType.NATIONAL);
				invoice.setWithholding(true);
				return InvoiceFaker.fill(params, invoice);
			}
		},
		SALES_RETENTION {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }
			
			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.SALES);
				invoice.setTransaction(InvoiceTransactionType.NATIONAL);
				invoice.setWithholding(true);
				invoice.setWithholdingFarmer(false);
				return InvoiceFaker.fill(params, invoice);
			}
		},
		// Venta con retención en régimen agríccola
		SALES_FARMER_RETENTION{
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }

			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.SALES);
				invoice.setTransaction(InvoiceTransactionType.NATIONAL);
				invoice.setWithholding(true);
				invoice.setWithholdingFarmer(true);
				return InvoiceFaker.fill(params, invoice);
			}
		},
		// Compra con retención en régimen agríccola
		PURCHASE_FARMER_RETENTION{
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.PURCHASE);
				invoice.setTransaction(InvoiceTransactionType.NATIONAL);
				invoice.setWithholding(true);
				invoice.setWithholdingFarmer(true);
				return InvoiceFaker.fill(params, invoice);
			}
		},
		// Gasto con retención en régimen agríccola
		EXPENSE_FARMER_RETENTION{
			@Override 
			public InvoiceType getType() { return InvoiceType.EXPENSES; }

			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.EXPENSES);
				invoice.setTransaction(InvoiceTransactionType.NATIONAL);
				invoice.setWithholding(true);
				invoice.setWithholdingFarmer(true);
				return InvoiceFaker.fill(params, invoice);
			}
		},
		;
		
		public abstract Invoice get( InvoiceFakerParams params );
		public abstract InvoiceType getType();
		
		public boolean isSales() {
			return getType() == InvoiceType.SALES;
		}
		
	}

	
	private static Invoice getHeader( InvoiceFakerParams params, InvoiceType invoiceType) {
		Invoice invoice = new Invoice();
		invoice.setDomain(params.getCtx().getDomainId());
		Date issueDate = faker.date().past(10, TimeUnit.DAYS);
		invoice.setIssueDate( issueDate );
		invoice.setTaxDate( issueDate );
		invoice.setType( invoiceType );
		
		invoice.getType().visit(invoice, new IInvoiceTypeVisitor<Void>() {
			
			private Void fillRegistryData(Invoice invoice, Registry reg) {
				invoice.setRegistry(reg.getId());
				invoice.setRegistryDocumentType(reg.getDocumentType());
				invoice.setRegistryDocumentCountry(reg.getDocumentCountry());
				invoice.setRegistryDocument(reg.getDocument());
				invoice.setRegistryName(reg.getName());
				return null;
			}
			
			@Override
			public Void visitSales(Invoice invoice) {
				Customer customer = AonRandom.getCustomer( params.getCtx() );
				fillRegistryData(invoice, customer);
				invoice.setSeries(params.getConfig().getDefaultInvoiceSeries());
				invoice.setNumber( InvoiceDAO.getNextNumber(params.getCtx(), new Byte[]{invoice.getType().value()}, invoice.getSeries()));
				
				invoice.setScope(customer.getScope());
				invoice.setTransaction( customer.getTransaction() );
				
				invoice.setService( AonRandom.gt(80) );
				invoice.setVatAccrualPayment(invoice.isNational() && params.getConfig().getCompany().isVatAccrualPayment());
				invoice.setSurcharge(customer.isSurcharge());
				invoice.setWithholding(customer.isWithholding() && params.getConfig().getCompany().isWithholding());
				invoice.setWithholdingFarmer(false);
				return null;
			}
			
			@Override
			public Void visitPurchase(Invoice invoice) {
				Supplier supplier = AonRandom.getSupplier( params.getCtx() );
				if (supplier == null && params.mustForceRegistry()) {
					supplier = AonFaker.getSupplier( params.getCtx() );
					supplier = SupplierDAO.save(params.getCtx(), supplier);
				}
				fillRegistryData(invoice, supplier);
				//invoice.setReferenceCode(AonRandom.string(-1,1,15));
				invoice.setReferenceCode(AonRandom.uuid(32));
				invoice.setScope(supplier.getScope());
				invoice.setTransaction(supplier.getTransaction());
				
				invoice.setService( AonRandom.gt(40) );
				invoice.setVatAccrualPayment(invoice.isNational() && supplier.isVatAccrualPayment());
				invoice.setSurcharge(params.getConfig().getCompany().isSurcharge());
				invoice.setWithholding(supplier.isWithholding());
				invoice.setWithholdingFarmer(supplier.isWithholdingFarmer());
				return null;
			}
			
			@Override
			public Void visitExpenses(Invoice invoice) {
				Creditor creditor = AonRandom.getCreditor( params.getCtx() );
				if (creditor == null && params.mustForceRegistry()) {
					creditor = AonFaker.getCreditor( params.getCtx() );
					creditor = CreditorDAO.save(params.getCtx(), creditor);
				}
				fillRegistryData(invoice, creditor);
//				invoice.setReferenceCode(AonRandom.string(-1,1,15));
				invoice.setReferenceCode(AonRandom.uuid(32));
//				if (AonStringUtils.isBlank(invoice.getReferenceCode())) {
//					System.out.println("NULL");
//				}

				invoice.setScope(creditor.getScope());
				invoice.setTransaction( creditor.getTransaction() );
				
				invoice.setService( AonRandom.gt(50) );
				invoice.setVatAccrualPayment(invoice.isNational() && creditor.isVatAccrualPayment());
				invoice.setSurcharge(false);
				invoice.setWithholding(creditor.isWithholding());
				invoice.setWithholdingFarmer(false);
				return null;
			}
			@Override
			public Void visitUndeductible(Invoice invoice) {
				visitExpenses(invoice);
				
				invoice.setSurcharge(false);
				invoice.setWithholding(false);
				invoice.setWithholdingFarmer(false);
				invoice.setVatAccrualPayment(false);
				invoice.setService( true );
				return null;
			}
		});
		EnterpriseActivity activity = AonRandom.getRandomActivity(params.getCtx());
		invoice.setActivity(activity==null ? new EnterpriseActivity() : activity);
		InvoiceFaker.fillHeader(invoice, params);
		return invoice;
	}
	
	public static Invoice fill( InvoiceFakerParams params , Invoice invoice) {
		checkInvoice( invoice );
		invoice.setDetails( getInvoiceDetails(params, invoice ));
		calculate(invoice);
		invoice.setFinances(FinanceDAO.getFinancesForInvoice(params.getCtx(), invoice));
		return invoice;
	}

	private static void checkInvoice(Invoice invoice) {
		invoice.getTransaction().visit( new IInvoiceTransactionTypeVisitor() {
			
			@Override
			public void visitOtherISP() {
				invoice.setVatImportation(false);
				invoice.setWithholdingFarmer(false);
			}
			
			@Override
			public void visitNational() {
				invoice.setVatImportation(false);
			}
			
			@Override
			public void visitIntracommunity() {
				invoice.setVatImportation(false);
				invoice.setWithholdingFarmer(false);
				invoice.setVatAccrualPayment(false);
			}
			
			@Override
			public void visitExtracommunity() {
				invoice.setWithholdingFarmer(false);
				invoice.setVatAccrualPayment(false);
			}
			
			@Override
			public void visitCanCeuMel() {
				invoice.setWithholdingFarmer(false);
				invoice.setVatAccrualPayment(false);
			}
		});
		
	}

	private static LinkedList<InvoiceDetail> getInvoiceDetails(InvoiceFakerParams params, Invoice invoice) {
		LinkedList<InvoiceDetail> details = new LinkedList<InvoiceDetail>();
		int times = AonRandom.number(1, 15);
		for (int i = 0; i < times; i++) {
			details.add(getInvoiceDetail( params, invoice));
		}
		return details;
	}

	private static InvoiceDetail getInvoiceDetail(InvoiceFakerParams params, Invoice invoice) {
		int basePrecision = 2;
		if ( AonRandom.gt( 97 )) {
			basePrecision = AonRandom.gt( 50 )? 3 : 4;
		}
		InvoiceDetail detail = new InvoiceDetail();
		detail.setDomain(invoice.getDomain())
			.setInvoice(invoice)
			.setWorkPlace( params.getConfig().getWorkplaces().getFirst().getId() )
			.setDescription(AonRandom.item(5, 50))
			.setQuantity(AonRandom.getDouble(0, 10))
			.setDiscountExpression( AonRandom.gt(10)
					?null 
					:AonNumberUtils.toString( AonRandom.getDouble(0, 100)))
			.setPrice(AonRandom.getDouble(0, 100, basePrecision))
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
		InvoiceWithholding witholding = getInvoiceWithholding(params,invoice);
		InvoiceTax tax =  new InvoiceTax()
			.setTaxType(TaxType.RETENTION)
			.setBase(detail.getTaxableBase())
			.setPercentage( witholding.getPercentage())
			.setSurcharge(0.0)
			.setDeductiblePercent( 
				(invoice.isNotNational() || invoice.isSales())
					? 0.0 
					: getDeductiblePercent( AonRandom.number(0, 80)))
			.setWithholdingType(witholding.getWithholdingType());
			;
		;
		return calculate(tax);
	}

	private static InvoiceWithholding getInvoiceWithholding(InvoiceFakerParams params, Invoice invoice) {
		InvoiceWithholding withholding = params.getWithholding();
		if (withholding == null && invoice.getDetails() != null) {
			withholding = invoice.getDetails()
				.stream()
				.filter( det -> AonCollectionUtils.isNotEmpty( det.getInvoiceTaxes() ))
				.map( det -> det.getInvoiceTaxes()
						.stream()
						.filter( tax -> tax.getTaxType() == TaxType.RETENTION)
						.findFirst()
						.orElse(null)
						)
				.map(tax -> new InvoiceWithholding()
						.setPercentage(tax.getPercentage())
						.setWithholdingType(tax.getWithholdingType()))
				.findFirst()
				.orElse(null)
			;
		}
		if (withholding == null ) {
			withholding = new InvoiceWithholding()
				.setPercentage( getRetentionPercent( AonRandom.number(0, 100)) )
				.setWithholdingType(WithholdingType.PROFESSIONAL);
			params.setWithholding(withholding);
		}
		return withholding;
	}

	private static InvoiceTax getVatInvoiceTax(InvoiceFakerParams params, Invoice invoice, InvoiceDetail detail) {
		double vatPercent = getVatPercent( AonRandom.number(0, 100));
		InvoiceTax tax =  new InvoiceTax()
			.setTaxType(TaxType.VAT)
			.setBase(detail.getTaxableBase())
			.setPercentage( vatPercent )
			.setSurcharge(invoice.isSurcharge()?getSurchargePercent( vatPercent ):0.0)
			.setDeductiblePercent( getDeductiblePercent( AonRandom.number(0, 100)));
		;
		return calculate(tax);
	}
	
	private static double getVatPercent(int x) {
		if ( x >= 0 && x <= 50) return 21.0;
		if ( x > 50 && x <= 75) return 10.0;
		if ( x > 75 && x <= 95) return 4.0;
		if ( x > 95 && x <= 98) return 5.0;
		return 0.0;
	}
	
	private static double getSurchargePercent(double vatPercent) {
		if (vatPercent == 21) return 5.2;
		else if (vatPercent == 10) return 1.4;
		else if (vatPercent == 4) return 0.5;
		else {
			int x = AonRandom.number(0, 100);
			if ( x <= 70) return 1.75; 
			if ( x <= 90) return 0.62;
			return 0.0;
		}
	}

	private static double getDeductiblePercent(int x) {
		if ( x >= 0 && x <= 50) return 0.0;
		if ( x > 50 && x <= 85) return 100.0;
		return AonRandom.number(0, 100);
	}

	private static double getRetentionPercent() {
		return getRetentionPercent(AonRandom.number(0, 100));
	}
	private static double getRetentionPercent(int x) {
		if ( x >= 0 && x <= 50) return 15.0;
		if ( x > 50 && x <= 75) return 10.0;
		if ( x > 75 && x <= 95) return 8.0;
		return 7.0;
	}

	private static void calculate(Invoice invoice) {
		if (invoice.getDetails() != null && !invoice.getDetails().isEmpty()) {
			double total = 0;
			double taxableBase = 0;
			double vatQuota = 0;
			double retentionQuota = 0;
			for (InvoiceDetail detail : invoice.getDetails()) {
				if (!detail.isPrepayment()) {
					taxableBase = AonMathUtils.round( taxableBase + detail.getTaxableBase(), 4);
					if (detail.getInvoiceTaxes() != null && !detail.getInvoiceTaxes().isEmpty()) {		
						for (InvoiceTax tax  : detail.getInvoiceTaxes()) {
							if (tax.getTaxType() ==TaxType.VAT) {
								vatQuota = AonMathUtils.round( vatQuota + tax.getQuota(), 2);			
							}
							if (tax.getTaxType() ==TaxType.RETENTION) {
								retentionQuota = AonMathUtils.round( retentionQuota + tax.getQuota(), 2);
							}
						}
					}
				}
			}
			total = AonMathUtils.round( taxableBase + vatQuota - retentionQuota );
			invoice.setTotal(total);
			invoice.setTaxableBase(taxableBase);
			invoice.setVatQuota(vatQuota);
			invoice.setRetentionQuota(retentionQuota);
		}
	
	}

	private static InvoiceDetail calculate(InvoiceDetail detail) {
		double taxableBase = (detail.getPrice() + detail.getTaxes()) * detail.getQuantity();
		taxableBase = taxableBase * ( 1 - detail.getDiscount() /100);
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

	public static Invoice getRandomSales(InvoiceFakerParams params) {
		List<InvoiceFakerTypes> list = Arrays.stream(InvoiceFakerTypes.values())
			.filter(t -> t.isSales() )
			.collect(Collectors.toCollection(LinkedList::new));
		InvoiceFakerTypes type = list.get(faker.random().nextInt(list.size()-1));
		return type.get(params);
	}
	
	public static Invoice getRandomNotSales(InvoiceFakerParams params) {
		List<InvoiceFakerTypes> list = Arrays.stream(InvoiceFakerTypes.values())
			.filter(t -> !t.isSales() )
			.collect(Collectors.toCollection(LinkedList::new));
		InvoiceFakerTypes type = list.get(faker.random().nextInt(list.size()-1));
		return type.get(params);
	}
	
	public static Invoice getRandom(InvoiceFakerParams params) {
		return AonRandom.gt(60)
			?getRandomSales(params)
			:getRandomNotSales(params);
	}
	public static Invoice getSalesNational(AONContext ctx, AonConfiguration configuration) {
		InvoiceFakerParams params = new InvoiceFakerParams(ctx,configuration);
		return InvoiceFakerTypes.SALES_NATIONAL.get(params);
	}
	public static Invoice getSalesCanCeuService(AONContext ctx, AonConfiguration configuration) {
		return getSalesCanCeuService(new InvoiceFakerParams(ctx,configuration));
	}
	public static Invoice getSalesCanCeuService(InvoiceFakerParams params) {
		return InvoiceFakerTypes.SALES_CAN_CEU_MEL_SERVICE.get(params);
	}
	public static Invoice getSalesCanCeu(AONContext ctx, AonConfiguration configuration) {
		return getSalesCanCeu(new InvoiceFakerParams(ctx,configuration));
	}
	public static Invoice getSalesCanCeu(InvoiceFakerParams params) {
		return InvoiceFakerTypes.SALES_CAN_CEU_MEL.get(params);
	}
	public static Invoice getExpensesNational(AONContext ctx, AonConfiguration configuration) {
		InvoiceFakerParams params = new InvoiceFakerParams(ctx,configuration);
		return InvoiceFakerTypes.EXPENSES_NATIONAL.get(params);
	}
	public static Invoice getPurchaseNational(AONContext ctx, AonConfiguration configuration) {
		InvoiceFakerParams params = new InvoiceFakerParams(ctx,configuration);
		return InvoiceFakerTypes.PURCHASE_NATIONAL.get(params);
	}
	public static Invoice getPurchaseExtracommunity(AONContext ctx, AonConfiguration configuration) {
		return getPurchaseExtracommunity( new InvoiceFakerParams(ctx,configuration));
	}
	public static Invoice getPurchaseExtracommunity(InvoiceFakerParams params) {
		return InvoiceFakerTypes.PURCHASE_EXTRACOMMUNITY.get(params);
	}
	public static Invoice getPurchaseExtracommunityVatImport(AONContext ctx, AonConfiguration configuration) {
		return getPurchaseExtracommunityVatImport( new InvoiceFakerParams(ctx,configuration) ); 
	}
	public static Invoice getPurchaseExtracommunityVatImport(InvoiceFakerParams params) {
		return InvoiceFakerTypes.PURCHASE_EXTRACOMMUNITY_VAT_IMPORT.get(params);
	}
	public static Invoice getPurchaseCanCeu(AONContext ctx, AonConfiguration configuration) {
		return getPurchaseCanCeu(new InvoiceFakerParams(ctx,configuration)); 
	}
	public static Invoice getPurchaseCanCeu(InvoiceFakerParams params) {
		return InvoiceFakerTypes.PURCHASE_CAN_CEU_MEL.get(params);
	}
	public static Invoice getPurchaseCanCeuVatImport(AONContext ctx, AonConfiguration configuration) {
		return getPurchaseCanCeuVatImport(new InvoiceFakerParams(ctx,configuration)); 
	}
	public static Invoice getPurchaseCanCeuVatImport(InvoiceFakerParams params) {
		return InvoiceFakerTypes.PURCHASE_CAN_CEU_MEL_VAT_IMPORT.get(params);
	}
	
	public static InvoiceFakerParams fillRetentionParams (AONContext ctx, InvoiceFakerParams invParams, WithholdingType wt) {
		return  invParams.setWithholding(new InvoiceWithholding()
				.setPercentage(getRetentionPercent())
				.setWithholdingType(wt))
				.setMustForceRegistry(true);
	}
	public static Invoice getRetentionInvoice( AONContext ctx, Occam occam, AonConfiguration configuration, final WithholdingType wt) {
		InvoiceFakerParams params = new InvoiceFakerParams(ctx,configuration)
			.setIssueDate(AonRandom.getYearDay(new Date()));
		InvoiceFaker.fillRetentionParams(ctx, params, wt);
		return InvoiceFaker.getExpensesRetention(params);
	}
	public static Invoice getExpensesRetention(InvoiceFakerParams invParams) {
		return InvoiceFakerTypes.EXPENSES_RETENTION.get(invParams);
	}

	public static Invoice getPurchaseFarmerRetention(AONContext ctx, AonConfiguration configuration) {
		InvoiceFakerParams invParams = new InvoiceFakerParams(ctx,configuration);
		return getPurchaseFarmerRetention(invParams);
	}
	public static Invoice getPurchaseFarmerRetention(InvoiceFakerParams invParams) {
		invParams.setWithholding(new InvoiceWithholding()
			.setPercentage(getRetentionPercent())
			.setWithholdingType(WithholdingType.FARMER))
			.setMustForceRegistry(true);
		return InvoiceFakerTypes.PURCHASE_FARMER_RETENTION.get(invParams);
	}
	public static Invoice getSalesFarmerRetention(InvoiceFakerParams invParams) {
		invParams.setWithholding(new InvoiceWithholding()
			.setPercentage(getRetentionPercent())
			.setWithholdingType(WithholdingType.FARMER))
			.setMustForceRegistry(true);
		return InvoiceFakerTypes.SALES_FARMER_RETENTION.get(invParams);
	}
	
	public static Invoice getSalesRetentionInvoice( AONContext ctx, Occam occam, AonConfiguration configuration, final WithholdingType wt) {
		InvoiceFakerParams params = new InvoiceFakerParams(ctx,configuration)
			.setIssueDate(AonRandom.getYearDay(new Date()));
		InvoiceFaker.fillRetentionParams(ctx, params, wt);
		return InvoiceFakerTypes.SALES_RETENTION.get(params);
	}
}

