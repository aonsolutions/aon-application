package com.esferalia.aon.occam.api.model.invoice;

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
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTransactionTypeVisitor;
import com.esferalia.aon.occam.api.model.invoice.Invoice.InvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.handler.FinanceHandler;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.github.javafaker.Faker;

public class InvoiceFaker {
	private static Faker faker = new Faker( Locale.of("es") );
	
	private static enum InvoiceFakerTypes {
		// Venta Nacional
		SALES_NATIONAL {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }
			
			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx,
					getHeader(ctx)
						.setTransaction(InvoiceTransactionType.NATIONAL)
				);
			}
		},
		// Venta Nacional
		SALES_NATIONAL_SURCHARGE {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }
			
			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx, 
					getHeader(ctx)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setSurcharge(true)
				);
			}
		},
		// Venta Canarias, Ceuta y Melilla
		SALES_CAN_CEU_MEL {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }

			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx, 
					getHeader(ctx)
						.setTransaction(InvoiceTransactionType.CAN_CEU_MEL)
				);
			}
		},
		// Prestacion de servicio Canarias, Ceuta y Melilla
		SALES_CAN_CEU_MEL_SERVICE {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }

			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx, 
					getHeader(ctx)
						.setTransaction(InvoiceTransactionType.CAN_CEU_MEL)
						.setService(true)
				);
				
			}
		},
		// Venta Nacional Criterio de caja
		SALES_NATIONAL_ACCRUAL_PAYMENT {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }

			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx, 
					getHeader(ctx)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setVatAccrualPayment(true)
				);
			}
		},
		// Compra Nacional 
		PURCHASE_NATIONAL {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx, 
					getHeader(ctx)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setVatAccrualPayment(false)
				);
			}
		},
		// Compra Nacional de servicios 
		PURCHASE_NATIONAL_SERVICE {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx, 
					getHeader(ctx)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setService(true)
				);
			}
		},
		// Compra Nacional Criterio de caja
		PURCHASE_NATIONAL_ACCRUAL_PAYMENT {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }
			
			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx, 
					getHeader(ctx)
					.setTransaction(InvoiceTransactionType.NATIONAL)
					.setVatAccrualPayment(true)
				);
			}
		},
		// Compra Intracomunitaria
		PURCHASE_INTRACOMMUNITY {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx, 
					getHeader(ctx)
						.setTransaction(InvoiceTransactionType.INTRACOMMUNITY)
				);
			}
		},
		// Compra ISP
		PURCHASE_ISP {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx, 
					getHeader(ctx)
						.setTransaction(InvoiceTransactionType.OTHER_ISP)
				);
			}
		},
		// Compra Nacional
		PURCHASE_EXTRACOMMUNITY {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx, 
					getHeader(ctx)
						.setTransaction(InvoiceTransactionType.EXTRACOMMUNITY)
						.setVatImportation(false)
				);
			}
		},
		// Compra Nacional Reg Importacion
		PURCHASE_EXTRACOMMUNITY_VAT_IMPORT {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx, 
					getHeader(ctx)
						.setTransaction(InvoiceTransactionType.EXTRACOMMUNITY)
						.setVatImportation(true)
				);
			}
		},
		// Compra Canarias, Ceuta, Melilla
		PURCHASE_CAN_CEU_MEL {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx, 
					getHeader(ctx)
						.setTransaction(InvoiceTransactionType.CAN_CEU_MEL)
						.setVatImportation(false)
				);
			}
		},
		// Compra Canarias, Ceuta, Melilla Reg Importacion
		PURCHASE_CAN_CEU_MEL_VAT_IMPORT {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx, 
					getHeader(ctx)
						.setTransaction(InvoiceTransactionType.CAN_CEU_MEL)
						.setVatImportation(true)
				);
			}
		},
		
		// Gasto Nacional
		EXPENSES_NATIONAL {
			@Override 
			public InvoiceType getType() { return InvoiceType.EXPENSES; }

			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx, 
					getHeader(ctx)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setVatImportation(true)
				);
			}
		},
		// Gasto Nacional Criterio de caja
		EXPENSES_NATIONAL_ACCRUAL_PAYMENT {
			@Override 
			public InvoiceType getType() { return InvoiceType.EXPENSES; }

			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx, 
					getHeader(ctx)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setVatAccrualPayment(true)
				);
			}
		},
		// Gasto nacional con retención. Se debe suministrar en 
		EXPENSES_RETENTION {
			@Override 
			public InvoiceType getType() { return InvoiceType.EXPENSES; }
			
			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx, 
					getHeader(ctx)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setWithholding( true )
						.setWithholdingFarmer( false)
				);
			}
		},
		SALES_RETENTION {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }
			
			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx, 
					getHeader(ctx)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setWithholding( true )
						.setWithholdingFarmer( false)
				);
			}
		},
		// Venta con retención en régimen agríccola
		SALES_FARMER_RETENTION{
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }

			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx, 
					getHeader(ctx)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setWithholding( true )
						.setWithholdingFarmer( true )
				);
			}
		},
		// Compra con retención en régimen agríccola
		PURCHASE_FARMER_RETENTION{
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice get( AONContext ctx ) {
				return fill(ctx, 
					getHeader(ctx)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setWithholding( true )
						.setWithholdingFarmer( true )
				);
			}
		},
		;
		
		public abstract Invoice get( AONContext ctx );
		public abstract InvoiceType getType();
		
		public boolean isSales() {
			return getType() == InvoiceType.SALES;
		}
		
		Invoice getHeader( AONContext ctx) {
			Invoice invoice = new Invoice();
			invoice.setDomain(ctx.getDomainId());
			Date issueDate = faker.date().past(10, TimeUnit.DAYS);
			invoice.setIssueDate( issueDate );
			invoice.setTaxDate( issueDate );
			invoice.setType( getType() );
			invoice.setSecurityLevel( AonRandom.gt(95)?SecurityLevel.CONFIDENTIAL:SecurityLevel.OFFICIAL);
			invoice.setActivity(AonRandom.getRandomActivity(ctx));
			
			invoice.getType().visit(invoice, new InvoiceTypeVisitor<Void>() {
				
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
					Customer customer = AonRandom.getCustomer( ctx );
					fillRegistryData(invoice, customer);
					invoice.setSeries(ctx.getConfig().getDefaultInvoiceSeries());
					invoice.setNumber( AON.getInvoiceNextNumber(ctx, new Byte[]{invoice.getType().value()}, invoice.getSeries()));
					
					invoice.setScope(customer.getScope());
					invoice.setTransaction( customer.getTransaction() );
					
					invoice.setService( AonRandom.gt(80) );
					invoice.setVatAccrualPayment(invoice.isNational() && ctx.getConfig().getCompany().isVatAccrualPayment());
					invoice.setSurcharge(customer.isSurcharge());
					invoice.setWithholding(customer.isWithholding() && ctx.getConfig().getCompany().isWithholding());
					invoice.setWithholdingFarmer(false);
					return null;
				}
				
				@Override
				public Void visitPurchase(Invoice invoice) {
					Supplier supplier = AonRandom.getSupplier( ctx );
					fillRegistryData(invoice, supplier);
					invoice.setReferenceCode(AonRandom.uuid(32));
					invoice.setScope(supplier.getScope());
					invoice.setTransaction(supplier.getTransaction());
					
					invoice.setService( AonRandom.gt(40) );
					invoice.setVatAccrualPayment(invoice.isNational() && supplier.isVatAccrualPayment());
					invoice.setSurcharge(ctx.getConfig().getCompany().isSurcharge());
					invoice.setWithholding(supplier.isWithholding());
					invoice.setWithholdingFarmer(supplier.isWithholdingFarmer());
					return null;
				}
				
				@Override
				public Void visitExpenses(Invoice invoice) {
					Creditor creditor = AonRandom.getCreditor( ctx );
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
			return invoice;
		}
		
		Invoice fill( AONContext ctx, Invoice invoice) {
			checkInvoice( invoice );
			System.out.println( this.name() + " ---> " + invoice.isWithholding() );
			IntStream.range(1, 15)
				.mapToObj( i -> getInvoiceDetail( ctx, invoice, i))
				.forEach( d -> invoice.addDetail(d));
			invoice.setFinances(FinanceHandler.getFinancesForInvoice(ctx, invoice));
			return invoice;
		}
		
		private void checkInvoice(Invoice invoice) {
			invoice.getTransaction().visit( new IInvoiceTransactionTypeVisitor() {
				
				@Override
				public void visitOtherISP() {
					invoice.setVatImportation(false);
					invoice.setWithholding(false);
					invoice.setWithholdingFarmer(false);
				}
				
				@Override
				public void visitNational() {
					invoice.setVatImportation(false);
				}
				
				@Override
				public void visitIntracommunity() {
					invoice.setVatImportation(false);
					invoice.setWithholding(false);
					invoice.setWithholdingFarmer(false);
					invoice.setVatAccrualPayment(false);
				}
				
				@Override
				public void visitExtracommunity() {
					invoice.setWithholding(false);
					invoice.setWithholdingFarmer(false);
					invoice.setVatAccrualPayment(false);
				}
				
				@Override
				public void visitCanCeuMel() {
					visitExtracommunity();
				}
			});
			if (invoice.isWithholding() && !invoice.isWithholdingFarmer()) {
				invoice.setWithholding( 
					new InvoiceWithholding()
						.setPercentage(getRetentionPercent())
						.setWithholdingType( AonRandom.getRandomWithholdingType())
				);
				invoice.getWithholding()
					.map(w -> w.getWithholdingType())
					.ifPresent(wt -> {
						if (wt == WithholdingType.FARMER) {
							invoice.setWithholdingFarmer(true);			
						}
					});
			}
			if (invoice.isWithholding() && invoice.isWithholdingFarmer()) {
				invoice.setWithholding( 
					new InvoiceWithholding()
						.setPercentage(getRetentionPercent())
						.setWithholdingType(WithholdingType.FARMER)
				);
			}
		}
		
		private InvoiceDetail getInvoiceDetail(AONContext ctx, Invoice invoice, int i) {
			int basePrecision = 2;
			if ( AonRandom.gt( 97 )) {
				basePrecision = AonRandom.gt( 50 )? 3 : 4;
			}
			InvoiceDetail detail = new InvoiceDetail();
			detail.setDomain(invoice.getDomain())
				.setInvoice(invoice.getId())
				.setLine((short) i)
				.setWorkplace( ctx.getConfig().getWorkplaces().getFirst() )
				.setDescription(AonRandom.item(5, 50))
				.setQuantity(AonRandom.getDouble(0, 10))
				.setDiscountExpression( AonRandom.gt(10)
						?null 
						:AonNumberUtils.toString( AonRandom.getDouble(0, 100)))
				.setPrice(AonRandom.getDouble(0, 100, basePrecision))
				.setSource(InvoiceSource.DIRECT_INVOICE)
				.setPrepayment(AonRandom.gt(98))
			;
			if (detail.isTaxEnabled(invoice)) {
				detail.addInvoiceTax( invoice, getVatInvoiceTax(invoice, detail) );
				if ( invoice.isWithholding()) {
					detail.addInvoiceTax( invoice, getRetentionInvoiceTax(invoice, detail));	
				}
			}
			return detail;
		}

		private InvoiceWithholding getInvoiceWithholding(Invoice invoice) {
			InvoiceWithholding withholding = invoice.getWithholding().orElse(null);
			if (withholding == null ) {
				withholding = new InvoiceWithholding()
					.setPercentage( getRetentionPercent( AonRandom.number(0, 100)) )
					.setWithholdingType(WithholdingType.PROFESSIONAL);
				invoice.setWithholding(withholding, invoice.isWithholdingFarmer());
			}
			return withholding;
		}
		
		double getRetentionPercent() {
			return getRetentionPercent(AonRandom.number(0, 100));
		}
		private double getRetentionPercent(int x) {
			if ( x >= 0 && x <= 50) return 15.0;
			if ( x > 50 && x <= 75) return 10.0;
			if ( x > 75 && x <= 95) return 8.0;
			return 7.0;
		}
		
		private InvoiceTax getRetentionInvoiceTax(Invoice invoice, InvoiceDetail detail) {
			InvoiceWithholding witholding = getInvoiceWithholding(invoice);
			return new InvoiceTax()
				.setTaxType(TaxType.RETENTION)
				.setBase(detail.getTaxableBase())
				.setPercentage( witholding.getPercentage())
				.setSurcharge(0.0)
				.setDeductiblePercent( 
					(invoice.isNotNational() || invoice.isSales())
						? 0.0 
						: getDeductiblePercent( AonRandom.number(0, 80)))
				.setWithholdingType(witholding.getWithholdingType())
			;
		}
		
		private InvoiceTax getVatInvoiceTax(Invoice invoice, InvoiceDetail detail) {
			double vatPercent = getVatPercent( invoice, AonRandom.number(0, 100));
			return new InvoiceTax()
				.setTaxType(TaxType.VAT)
				.setBase(detail.getTaxableBase())
				.setPercentage( vatPercent )
				.setSurcharge(invoice.isSurcharge()?getSurchargePercent( vatPercent ):0.0)
				.setDeductiblePercent( getDeductiblePercent( AonRandom.number(0, 100)))
			;
			 
		}
		
		private static double getVatPercent(Invoice invoice, int x) {
			Date octoberFirst = AonDateUtils.getDate(2024, 9,1);
			boolean previous = AonDateUtils.compare(invoice.getIssueDate(), octoberFirst) == -1; 
			if ( x >= 0 && x <= 50) return 21.0;
			if ( x > 50 && x <= 75) return 10.0;
			if ( x > 75 && x <= 90) return 4.0;
			if ( x > 90 && x <= 95) return previous?0.0:2.0;
			if ( x > 95 && x <= 98) return previous?5.0:7.5;
			return 0.0;
		}
		
		private static double getSurchargePercent(double vatPercent) {
			if (vatPercent == 21) return 5.2;
			else if (vatPercent == 10) return 1.4;
			else if (vatPercent == 7.5) return 1;
			else if (vatPercent == 5) return 0.62;
			else if (vatPercent == 4) return 0.5;
			else if (vatPercent == 2) return 0.26;
			else {
				int x = AonRandom.number(0, 100);
				if ( x <= 70) return 1.75; 
				return 0.0;
			}
		}

		private double getDeductiblePercent(int x) {
			if ( x >= 0 && x <= 50) return 0.0;
			if ( x > 50 && x <= 85) return 100.0;
			return AonRandom.number(0, 100);
		}
	}

	public static Invoice getRandomSales(AONContext ctx) {
		List<InvoiceFakerTypes> list = Arrays.stream(InvoiceFakerTypes.values())
			.filter(t -> t.isSales() )
			.collect(Collectors.toCollection(LinkedList::new));
		return list.get(faker.random().nextInt(list.size()-1)).get(ctx);
	}
	
	public static Invoice getRandomNotSales(AONContext ctx) {
		List<InvoiceFakerTypes> list = Arrays.stream(InvoiceFakerTypes.values())
			.filter(t -> !t.isSales() )
			.collect(Collectors.toCollection(LinkedList::new));
		return list.get(faker.random().nextInt(list.size()-1)).get(ctx);
	}
	
	public static Invoice getRandom(AONContext ctx) {
		return AonRandom.gt(60)
			?getRandomSales(ctx)
			:getRandomNotSales(ctx);
	}
	
}

