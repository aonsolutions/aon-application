package com.esferalia.aon.occam.test.faker;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
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
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.github.javafaker.Faker;

public class InvoiceFaker {
	private static Faker faker = new Faker( Locale.of("es") );
	
	public static class InvoiceFakerParams {
		private AONContext ctx;
		private Date issueDate;
		
		public InvoiceFakerParams(AONContext ctx) {
			this.ctx = ctx;
		}
		
		public AONContext getCtx() {
			return ctx;
		}
		public InvoiceFakerParams setCtx(AONContext ctx) {
			this.ctx = ctx;
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
		// Venta Nacional
		SALES_NATIONAL {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }
			
			@Override 
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

			@Override 
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

			@Override 
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

			@Override 
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

			@Override 
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

			@Override 
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
			
			@Override 
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

			@Override 
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

			@Override 
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

			@Override 
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

			@Override 
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

			@Override 
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

			@Override 
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

			@Override 
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

			@Override 
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
			
			@Override 
			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.EXPENSES);
				invoice.setTransaction(InvoiceTransactionType.NATIONAL);
				invoice.setWithholding(true);
				invoice.setWithholdingFarmer(false);
				invoice.setWithholding(new InvoiceWithholding()
					.setPercentage(getRetentionPercent())
					.setWithholdingType(WithholdingType.PROFESSIONAL));
				return InvoiceFaker.fill(params, invoice);
			}
		},
		SALES_RETENTION {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }
			
			@Override 
			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.SALES);
				invoice.setTransaction(InvoiceTransactionType.NATIONAL);
				invoice.setWithholding(true);
				invoice.setWithholdingFarmer(false);
				invoice.setWithholding(new InvoiceWithholding()
					.setPercentage(getRetentionPercent())
					.setWithholdingType(WithholdingType.PROFESSIONAL));
				return InvoiceFaker.fill(params, invoice);
			}
		},
		// Venta con retención en régimen agríccola
		SALES_FARMER_RETENTION{
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }

			@Override 
			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.SALES);
				invoice.setTransaction(InvoiceTransactionType.NATIONAL);
				invoice.setWithholding(true);
				invoice.setWithholdingFarmer(true);
				invoice.setWithholding(new InvoiceWithholding()
					.setPercentage(getRetentionPercent())
					.setWithholdingType(WithholdingType.FARMER));
				return InvoiceFaker.fill(params, invoice);
			}
		},
		// Compra con retención en régimen agríccola
		PURCHASE_FARMER_RETENTION{
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override 
			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.PURCHASE);
				invoice.setTransaction(InvoiceTransactionType.NATIONAL);
				invoice.setWithholding(true);
				invoice.setWithholdingFarmer(true);
				invoice.setWithholding(new InvoiceWithholding()
					.setPercentage(getRetentionPercent())
					.setWithholdingType(WithholdingType.FARMER));
				return InvoiceFaker.fill(params, invoice);
			}
		},
		// Gasto con retención en régimen agríccola
		EXPENSE_FARMER_RETENTION{
			@Override 
			public InvoiceType getType() { return InvoiceType.EXPENSES; }

			@Override 
			public Invoice get( InvoiceFakerParams params ) {
				Invoice invoice = InvoiceFaker.getHeader(params, InvoiceType.EXPENSES);
				invoice.setTransaction(InvoiceTransactionType.NATIONAL);
				invoice.setWithholding(true);
				invoice.setWithholdingFarmer(true);
				invoice.setWithholding(new InvoiceWithholding()
					.setPercentage(getRetentionPercent())
					.setWithholdingType(WithholdingType.FARMER));
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
				Customer customer = AonRandom.ensureCustomer( params.getCtx() );
				fillRegistryData(invoice, customer);
				invoice.setSeries(params.getCtx().getConfiguration().getDefaultInvoiceSeries());
				invoice.setNumber( AON.getInvoiceNextNumber(params.getCtx(), new Byte[]{invoice.getType().value()}, invoice.getSeries()));
				
				invoice.setScope(customer.getScope());
				invoice.setTransaction( customer.getTransaction() );
				
				invoice.setService( AonRandom.gt(80) );
				invoice.setVatAccrualPayment(invoice.isNational() && params.getCtx().getConfiguration().getCompany().isVatAccrualPayment());
				invoice.setSurcharge(customer.isSurcharge());
				invoice.setWithholding(customer.isWithholding() && params.getCtx().getConfiguration().getCompany().isWithholding());
				invoice.setWithholdingFarmer(false);
				return null;
			}
			
			@Override
			public Void visitPurchase(Invoice invoice) {
				Supplier supplier = AonRandom.ensureSupplier( params.getCtx() );
				if (supplier == null ) {
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
				invoice.setSurcharge(params.getCtx().getConfiguration().getCompany().isSurcharge());
				invoice.setWithholding(supplier.isWithholding());
				invoice.setWithholdingFarmer(supplier.isWithholdingFarmer());
				return null;
			}
			
			@Override
			public Void visitExpenses(Invoice invoice) {
				Creditor creditor = AonRandom.ensureCreditor( params.getCtx() );
				if (creditor == null ) {
					creditor = AonFaker.getCreditor( params.getCtx() );
					creditor = CreditorDAO.save(params.getCtx(), creditor);
				}
				fillRegistryData(invoice, creditor);
				invoice.setReferenceCode(AonRandom.uuid(32));

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
		if (params.getIssueDate() != null) {
			invoice.setIssueDate(params.getIssueDate());
			invoice.setTaxDate(params.getIssueDate());
		}
		return invoice;
	}
	
	public static Invoice fill( InvoiceFakerParams params , Invoice invoice) {
		checkInvoice( invoice );
		invoice.deleteDetails();
		IntStream.range(1, AonRandom.number(2, 2)) //15))
			.forEach(d -> invoice.addDetail(getInvoiceDetail( params, invoice)));
		InvoiceCalculator.calculate(invoice);
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


	private static InvoiceDetail getInvoiceDetail(InvoiceFakerParams params, Invoice invoice) {
		int basePrecision = 2;
		if ( AonRandom.gt( 97 )) {
			basePrecision = AonRandom.gt( 50 )? 3 : 4;
		}
		InvoiceDetail detail = new InvoiceDetail();
		detail.setDomain(invoice.getDomain())
			.setInvoice(invoice.getId())
			.setWorkplace( params.getCtx().getConfiguration().getWorkplaces().getFirst() )
			.setDescription(AonRandom.item(5, 50))
			.setQuantity(AonRandom.getDouble(0, 10))
			.setDiscountExpression( AonRandom.gt(10)
					?null 
					:AonNumberUtils.toString( AonRandom.getDouble(0, 100)))
			.setPrice(AonRandom.getDouble(0, 100, basePrecision))
			.setSource(InvoiceSource.DIRECT_INVOICE) 
			.setPrepayment(AonRandom.gt(98))
		;
		InvoiceCalculator.calculateDetail(invoice, detail);
		addInvoiceTaxes(params,invoice, detail);
		return detail;
	}

	private static void addInvoiceTaxes(InvoiceFakerParams params, Invoice invoice, InvoiceDetail detail) {
		if (detail.isTaxEnabled(invoice)) {
			double vatPercent = getVatPercent( AonRandom.number(0, 100));
			InvoiceTax vat = detail.ensureVatTax(invoice)
				.setBase(detail.getTaxableBase())
				.setPercentage( vatPercent )
				.setSurcharge(invoice.isSurcharge()?getSurchargePercent( vatPercent ):0.0)
				.setDeductiblePercent( getDeductiblePercent( AonRandom.number(0, 100)));
			if ( invoice.isWithholding() || invoice.isWithholdingFarmer()) {
				
				double base = invoice.isWithholdingFarmer()
					?detail.getTaxableBase() + vat.getQuota() + (invoice.isSurcharge()?vat.getSurchargeQuota():0.0)
					:detail.getTaxableBase();
				detail.ensureWithholdingTax(invoice)
					.setBase(base)
					.setPercentage( invoice.getWithholding().map(iw -> iw.getPercentage()).orElse(0.0) )
					.setSurcharge(0.0)
					.setDeductiblePercent( 
						(invoice.isNotNational() || invoice.isSales())
							? 0.0 
							: getDeductiblePercent( AonRandom.number(0, 80)))
					.setWithholdingType(invoice.getWithholding().map(iw -> iw.getWithholdingType()).orElse(WithholdingType.PROFESSIONAL))
					;
				;
			}
		}
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
	
	public static Invoice getRandom(AONContext ctx) {
		return getRandom( new InvoiceFakerParams(ctx) ); 
	}
	
	public static Invoice getRandom(InvoiceFakerParams params) {
		return AonRandom.gt(60)
			?getRandomSales(params)
			:getRandomNotSales(params);
	}
	public static Invoice getSalesNational(AONContext ctx) {
		InvoiceFakerParams params = new InvoiceFakerParams(ctx);
		return InvoiceFakerTypes.SALES_NATIONAL.get(params);
	}
	public static Invoice getSalesCanCeuService(AONContext ctx) {
		return getSalesCanCeuService(new InvoiceFakerParams(ctx));
	}
	public static Invoice getSalesCanCeuService(InvoiceFakerParams params) {
		return InvoiceFakerTypes.SALES_CAN_CEU_MEL_SERVICE.get(params);
	}
	public static Invoice getSalesCanCeu(AONContext ctx) {
		return getSalesCanCeu(new InvoiceFakerParams(ctx));
	}
	public static Invoice getSalesCanCeu(InvoiceFakerParams params) {
		return InvoiceFakerTypes.SALES_CAN_CEU_MEL.get(params);
	}
	public static Invoice getExpensesNational(AONContext ctx) {
		InvoiceFakerParams params = new InvoiceFakerParams(ctx);
		return InvoiceFakerTypes.EXPENSES_NATIONAL.get(params);
	}
	public static Invoice getPurchaseNational(AONContext ctx) {
		InvoiceFakerParams params = new InvoiceFakerParams(ctx);
		return InvoiceFakerTypes.PURCHASE_NATIONAL.get(params);
	}
	public static Invoice getPurchaseExtracommunity(AONContext ctx) {
		return getPurchaseExtracommunity( new InvoiceFakerParams(ctx));
	}
	public static Invoice getPurchaseExtracommunity(InvoiceFakerParams params) {
		return InvoiceFakerTypes.PURCHASE_EXTRACOMMUNITY.get(params);
	}
	public static Invoice getPurchaseExtracommunityVatImport(AONContext ctx) {
		return getPurchaseExtracommunityVatImport( new InvoiceFakerParams(ctx) ); 
	}
	public static Invoice getPurchaseExtracommunityVatImport(InvoiceFakerParams params) {
		return InvoiceFakerTypes.PURCHASE_EXTRACOMMUNITY_VAT_IMPORT.get(params);
	}
	public static Invoice getPurchaseCanCeu(AONContext ctx) {
		return getPurchaseCanCeu(new InvoiceFakerParams(ctx)); 
	}
	public static Invoice getPurchaseCanCeu(InvoiceFakerParams params) {
		return InvoiceFakerTypes.PURCHASE_CAN_CEU_MEL.get(params);
	}
	public static Invoice getPurchaseCanCeuVatImport(AONContext ctx) {
		return getPurchaseCanCeuVatImport(new InvoiceFakerParams(ctx)); 
	}
	public static Invoice getPurchaseCanCeuVatImport(InvoiceFakerParams params) {
		return InvoiceFakerTypes.PURCHASE_CAN_CEU_MEL_VAT_IMPORT.get(params);
	}
	
	public static Invoice getRetentionInvoice( AONContext ctx, final WithholdingType wt) {
		InvoiceFakerParams params = new InvoiceFakerParams(ctx)
			.setIssueDate(AonRandom.getYearDay(new Date()));
		return InvoiceFaker.getExpensesRetention(params);
	}
	
	public static Invoice getExpensesRetention(AONContext ctx) {
		return getExpensesRetention(new InvoiceFakerParams(ctx));
	}
	public static Invoice getExpensesRetention(InvoiceFakerParams invParams) {
		return InvoiceFakerTypes.EXPENSES_RETENTION.get(invParams);
	}

	public static Invoice getPurchaseFarmerRetention(AONContext ctx) {
		InvoiceFakerParams invParams = new InvoiceFakerParams(ctx);
		return getPurchaseFarmerRetention(invParams);
	}
	public static Invoice getPurchaseFarmerRetention(InvoiceFakerParams invParams) {
		return InvoiceFakerTypes.PURCHASE_FARMER_RETENTION.get(invParams);
	}
	
	public static Invoice getSalesFarmerRetention(AONContext ctx) {
		InvoiceFakerParams invParams = new InvoiceFakerParams(ctx);
		return getSalesFarmerRetention(invParams);
	}
	
	public static Invoice getSalesFarmerRetention(InvoiceFakerParams invParams) {
		return InvoiceFakerTypes.SALES_FARMER_RETENTION.get(invParams);
	}
	
	public static Invoice getSalesRetentionInvoice( AONContext ctx, final WithholdingType wt) {
		InvoiceFakerParams params = new InvoiceFakerParams(ctx)
			.setIssueDate(AonRandom.getYearDay(new Date()));
		return InvoiceFakerTypes.SALES_RETENTION.get(params);
	}

	public static void randomUpdate(CloseableAONContext ctx, Invoice inv) {
		
		if (AonRandom.gt(90)) inv.setComments( AonRandom.lorem(50, 100)  );
		if (AonRandom.gt(90)) inv.setRemarks( AonRandom.lorem(50, 100)  );
		if (AonRandom.gt(90)) inv.setActivity( AonRandom.getRandomActivity(ctx) );
		if (AonRandom.gt(90)) inv.setInvestAsset( AonRandom.getRandomInvestAssetId(ctx) );
		if (AonRandom.gt(90)) inv.setSeries(AonRandom.string(5));
		if (AonRandom.gt(90)) inv.setNumber(AonRandom.getInt(0, 500000));
		if (AonRandom.gt(90)) inv.setReferenceCode(AonRandom.string(15));
		if (AonRandom.gt(90)) inv.setIssueDate( AonRandom.getPastDate(-1));
		if (AonRandom.gt(90)) inv.setTaxDate( AonRandom.getPastDate(-1));
		if (AonRandom.gt(90)) inv.setConfidential( !inv.isConfidential() );
		if (AonRandom.gt(90)) inv.setRegistryDocument(AonRandom.string(9));
		if (AonRandom.gt(90)) inv.setRegistryDocumentType(AonRandom.getRandomDocumentType());
		if (AonRandom.gt(90)) inv.setRegistryDocumentCountry(AonRandom.getRandomCountry());
		if (AonRandom.gt(90)) inv.setRegistryName(AonRandom.name(-1, 40));
		
//		private Integer project;
//		private Integer registryAddress;
//		private RegistryAddress address;
//		
//		private Integer registry;
//		private Account registryAccount;
//		
//		private Scope scope;
//		private InvoiceType type;
//		private InvoiceTransactionType transaction;
//		private boolean recorded;
//		private boolean surcharge;
//		private boolean withholding;
//		private boolean withholdingFarmer;
//		private boolean vatAccrualPayment;
//		private boolean investment;
//		private boolean service;
//		private boolean advance;
//		private boolean signed;
//		private boolean annulled;
//		private double taxableBase;
//		private double vatQuota;
//		private double retentionQuota;
//		private double total;
//		private Integer posShift;
//		private Integer seller;
//		private String sellerName;
		
		
	}

	public static InvoiceDetail getRandomDetail(Invoice inv) {
		return AonRandom.random(inv.getDetails());
	}

}

