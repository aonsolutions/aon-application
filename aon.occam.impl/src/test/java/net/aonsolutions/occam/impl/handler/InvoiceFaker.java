package net.aonsolutions.occam.impl.handler;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javafaker.Faker;

import net.aonsolutions.occam.api.model.Activity;
import net.aonsolutions.occam.api.model.AonRandom;
import net.aonsolutions.occam.api.model.Creditor;
import net.aonsolutions.occam.api.model.Customer;
import net.aonsolutions.occam.api.model.Invoice;
import net.aonsolutions.occam.api.model.InvoiceAddress;
import net.aonsolutions.occam.api.model.InvoiceBuilder;
import net.aonsolutions.occam.api.model.InvoiceCalculator;
import net.aonsolutions.occam.api.model.InvoiceHeader;
import net.aonsolutions.occam.api.model.Registry;
import net.aonsolutions.occam.api.model.Supplier;
import net.aonsolutions.occam.api.model.type.InvoiceSource;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType.InvoiceTransactionTypeVisitor;
import net.aonsolutions.occam.api.model.type.InvoiceType;
import net.aonsolutions.occam.api.model.type.InvoiceType.InvoiceTypeVisitor;
import net.aonsolutions.occam.api.model.type.StreetType;
import net.aonsolutions.occam.api.model.type.WithholdingType;
import net.aonsolutions.occam.api.model.type.WithholdingType.WithholdingTypeVisitor;
import net.aonsolutions.occam.impl.AONContext;

public class InvoiceFaker {
	private static Faker faker = new Faker( Locale.of("es") );
	
	private static enum InvoiceFakerTypes {
		// Venta Nacional
		SALES_NATIONAL {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }
			
			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.NATIONAL);
				return fill(ctx, inv );
			}
		},
		// Venta Nacional
		SALES_NATIONAL_SURCHARGE {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }
			
			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.NATIONAL);
				inv.getHeader().setSurcharge(true);
				return fill(ctx, inv);
			}
		},
		// Venta Canarias, Ceuta y Melilla
		SALES_CAN_CEU_MEL {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }

			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.CAN_CEU_MEL);
				return fill(ctx, inv);
			}
		},
		// Prestacion de servicio Canarias, Ceuta y Melilla
		SALES_CAN_CEU_MEL_SERVICE {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }

			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.CAN_CEU_MEL);
				inv.getHeader().setService(true);
				return fill(ctx, inv);
			}
		},
		// Venta Nacional Criterio de caja
		SALES_NATIONAL_ACCRUAL_PAYMENT {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }

			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.NATIONAL);
				inv.getHeader().setVatAccrualPayment(true);
				return fill(ctx, inv);
			}
		},
		// Compra Nacional 
		PURCHASE_NATIONAL {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.NATIONAL);
				inv.getHeader().setVatAccrualPayment(false);
				return fill(ctx,inv );
			}
		},
		// Compra Nacional de servicios 
		PURCHASE_NATIONAL_SERVICE {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.NATIONAL);
				inv.getHeader().setService(true);
				return fill(ctx,inv);
			}
		},
		// Compra Nacional Criterio de caja
		PURCHASE_NATIONAL_ACCRUAL_PAYMENT {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }
			
			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.NATIONAL);
				inv.getHeader().setVatAccrualPayment(true);
				return fill(ctx,inv );
			}
		},
		// Compra Intracomunitaria
		PURCHASE_INTRACOMMUNITY {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.INTRACOMMUNITY);
				return fill(ctx, inv );
			}
		},
		// Compra ISP
		PURCHASE_ISP {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.OTHER_ISP);
				return fill(ctx,inv);
			}
		},
		// Compra Nacional
		PURCHASE_EXTRACOMMUNITY {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.EXTRACOMMUNITY);
				inv.setVatImportation(false);
				return fill(ctx,inv );
			}
		},
		// Compra Nacional Reg Importacion
		PURCHASE_EXTRACOMMUNITY_VAT_IMPORT {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.EXTRACOMMUNITY);
				inv.setVatImportation(true);
				return fill(ctx,inv);
			}
		},
		// Compra Canarias, Ceuta, Melilla
		PURCHASE_CAN_CEU_MEL {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.CAN_CEU_MEL);
				inv.setVatImportation(false);
				return fill(ctx, inv);
			}
		},
		// Compra Canarias, Ceuta, Melilla Reg Importacion
		PURCHASE_CAN_CEU_MEL_VAT_IMPORT {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.CAN_CEU_MEL);
				inv.setVatImportation(true);
				return fill(ctx, inv );
			}
		},
		
		// Gasto Nacional
		EXPENSES_NATIONAL {
			@Override 
			public InvoiceType getType() { return InvoiceType.EXPENSES; }

			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.NATIONAL);
				inv.setVatImportation(true);
				return fill(ctx, inv );
			}
		},
		// Gasto Nacional Criterio de caja
		EXPENSES_NATIONAL_ACCRUAL_PAYMENT {
			@Override 
			public InvoiceType getType() { return InvoiceType.EXPENSES; }

			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.NATIONAL);
				inv.getHeader().setVatAccrualPayment(true);
				return fill(ctx, inv );
			}
		},
		// Gasto nacional con retención. Se debe suministrar en 
		EXPENSES_RETENTION {
			@Override 
			public InvoiceType getType() { return InvoiceType.EXPENSES; }
			
			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.NATIONAL);
				inv.getHeader().setWithholding( true );
				inv.getHeader().setWithholdingFarmer( false);
				return fill(ctx, inv );
			}
		},
		SALES_RETENTION {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }
			
			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.NATIONAL);
				inv.getHeader().setWithholding( true );
				inv.getHeader().setWithholdingFarmer( false);
				return fill(ctx, inv);
			}
		},
		// Venta con retención en régimen agríccola
		SALES_FARMER_RETENTION{
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }

			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.NATIONAL);
				inv.getHeader().setWithholding( true );
				inv.getHeader().setWithholdingFarmer( true );
				return fill(ctx,inv);
			}
		},
		// Compra con retención en régimen agríccola
		PURCHASE_FARMER_RETENTION{
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice get( AONContext ctx, int domain) {
				Invoice inv = getHeader(ctx,domain);
				inv.getHeader().setTransaction(InvoiceTransactionType.NATIONAL);
				inv.getHeader().setWithholding( true );
				inv.getHeader().setWithholdingFarmer( true );
				return fill(ctx,inv);
			}
		},
		;
		
		public abstract Invoice get( AONContext ctx, int domain );
		public abstract InvoiceType getType();
		
		public boolean isSales() {
			return getType() == InvoiceType.SALES;
		}
		
		Invoice getHeader( AONContext ctx, int domain) {
			Date issueDate = faker.date().past(10, TimeUnit.DAYS);
			Activity activity = AonDBRandom.getActivity(ctx, domain);
			Invoice invoice = new Invoice();
			InvoiceHeader header = new InvoiceHeader();
			invoice.setHeader(header);
			header.setDomain( domain )
				.setIssueDate( issueDate )
				.setTaxDate( issueDate )
				.setType( getType() )
				.setConfidential( AonRandom.gt(95) )
				.setActivity( activity )
				.setSeller( AonRandom.gt(90) ? AonDBRandom.getSeller(ctx, domain) : null)
			;
			
			header.getType().visit(new InvoiceTypeVisitor<Void>() {
				
				private Void fillRegistryData(Registry reg) {
					header.setRegistry(reg.getId());
					header.setRegistryDocumentType(reg.getDocumentType());
					header.setRegistryDocumentCountry(reg.getDocumentCountry());
					header.setRegistryDocument(reg.getDocument());
					header.setRegistryName(reg.getName());
					return null;
				}
				
				@Override
				public Void visitSales() {
					Customer customer = AonDBRandom.getCustomer( ctx, domain );
					fillRegistryData(customer);
					header.setSeries(ctx.getApplicationParameters(domain).getDefaultInvoiceSeries().orElse(null));
					header.setNumber( InvoiceHandler.getNextNumber(ctx, domain, new Byte[]{header.getType().value()}, header.getSeries()));
					
					header.setScope(customer.getScope());
					header.setTransaction( customer.getTransaction() );
					
					header.setService( AonRandom.gt(80) );
					header.setVatAccrualPayment(header.isNational() && ctx.getCompany(domain).get().isVatAccrualPayment());
					header.setSurcharge(customer.isSurcharge());
					header.setWithholding(customer.isWithholding() && ctx.getCompany(domain).get().isWithholding());
					header.setWithholdingFarmer(false);
					return null;
				}
				
				@Override
				public Void visitPurchase() {
					Supplier supplier = AonDBRandom.getSupplier( ctx, domain );
					fillRegistryData(supplier);
					header.setReferenceCode(AonRandom.uuid(32));
					header.setScope(supplier.getScope());
					header.setTransaction(supplier.getTransaction());
					
					header.setService( AonRandom.gt(40) );
					header.setVatAccrualPayment(header.isNational() && supplier.isVatAccrualPayment());
					header.setSurcharge(ctx.getCompany(domain).get().isSurcharge());
					header.setWithholding(supplier.isWithholding());
					header.setWithholdingFarmer(supplier.isWithholdingFarmer());
					return null;
				}
				
				@Override
				public Void visitExpenses() {
					Creditor creditor = AonDBRandom.getCreditor( ctx, domain);
					fillRegistryData(creditor);
					header.setReferenceCode(AonRandom.uuid(32));
					header.setScope(creditor.getScope());
					header.setTransaction( creditor.getTransaction() );
					
					header.setService( AonRandom.gt(50) );
					header.setVatAccrualPayment(header.isNational() && creditor.isVatAccrualPayment());
					header.setSurcharge(false);
					header.setWithholding(creditor.isWithholding());
					header.setWithholdingFarmer(false);
					return null;
				}
				@Override
				public Void visitUndeductible() {
					visitExpenses();
					
					header.setSurcharge(false);
					header.setWithholding(false);
					header.setWithholdingFarmer(false);
					header.setVatAccrualPayment(false);
					header.setService( true );
					return null;
				}
			});
			return invoice;
		}
		
		Invoice fill( AONContext ctx, Invoice invoice) {
			checkInvoice( invoice );
			IntStream.range(1, 15)
				.forEach( i -> addInvoiceDetail( ctx, invoice));
			invoice.setInvoiceAddress( getInvoiceAddress(ctx, invoice));
			FinanceHandler.getFinancesForInvoice(ctx, invoice)
				.forEach( f -> invoice.addFinance(f));
			return InvoiceCalculator.calculate(invoice);
		}
		
		private void checkInvoice(Invoice invoice) {
			invoice.getHeader().getTransaction().visit( new InvoiceTransactionTypeVisitor<Void>() {
				
				@Override
				public Void visitOtherISP() {
					invoice.setVatImportation(false);
					invoice.getHeader().setWithholding(false);
					invoice.getHeader().setWithholdingFarmer(false);
					return null;
				}
				
				@Override
				public Void visitNational() {
					invoice.setVatImportation(false);
					return null;
				}
				
				@Override
				public Void visitIntracommunity() {
					invoice.setVatImportation(false);
					invoice.getHeader().setWithholding(false);
					invoice.getHeader().setWithholdingFarmer(false);
					invoice.getHeader().setVatAccrualPayment(false);
					return null;
				}
				
				@Override
				public Void visitExtracommunity() {
					invoice.getHeader().setWithholding(false);
					invoice.getHeader().setWithholdingFarmer(false);
					invoice.getHeader().setVatAccrualPayment(false);
					return null;
				}
				
				@Override
				public Void visitCanCeuMel() {
					visitExtracommunity();
					return null;
				}
			});
			
			if (invoice.getHeader().isWithholding()) {
				WithholdingType wt = AonRandom.getEnum( WithholdingType.class);
				wt = invoice.getHeader().isWithholdingFarmer()? WithholdingType.FARMER: wt;
				invoice.getHeader().setWithholdingFarmer( wt == WithholdingType.FARMER);
				new InvoiceBuilder()
					.invoice(invoice)
					.setWithholdingPercent(getRetentionPercent())
					.setWithholdingType(AonRandom.getEnum( WithholdingType.class))
				;
			}
		}
		
		private void addInvoiceDetail(AONContext ctx, Invoice invoice) {
			int basePrecision = 2;
			if ( AonRandom.gt( 97 )) {
				basePrecision = AonRandom.gt( 50 )? 3 : 4;
			}
			new InvoiceBuilder()
				.invoice( invoice )
				.addDetail()
					.setWorkplace( ctx.getDefaultWorkplace(invoice.getDomain()).map(w -> w.getId()).orElse(null) )
					.setDescription(AonRandom.item(5, 50))
					.setQuantity(AonRandom.getDouble(0, 10))
					.setDiscount( AonRandom.gt(10) ? 0.0 :AonRandom.getDouble(0, 100))
					.setPrice(AonRandom.getDouble(0, 100, basePrecision))
					.setPrepayment(AonRandom.gt(95))
					.setSource(InvoiceSource.DIRECT_INVOICE)
					.setSeller(invoice.getHeader().getSeller().orElse(null))
					.setInvestAsset( AonRandom.gt(90) 
						? AonDBRandom.getInvestAsset(ctx, invoice.getDomain()) 
						: null)
					.ifTaxEnabled( b -> {
						double vatPercent = getVatPercent( AonRandom.integer(0, 100));
						b.setVatPercent(vatPercent);
						b.setSurchargePercent( invoice.getHeader().isSurcharge()
								?getSurchargePercent( vatPercent ):0.0);
					})
			;
		}
		
		public InvoiceAddress getInvoiceAddress(AONContext ctx, Invoice invoice) {
			if (AonRandom.gt( 80 )) return null;
			return new InvoiceAddress()
				.setDomain(invoice.getDomain())
				.setInvoice(invoice.getId())
				.setStreetType(AonRandom.getEnum(StreetType.class, 75))
				.setAddress( AonRandom.gt(10)?faker.address().streetName():null )
				.setNumber( AonRandom.gt(12)?faker.address().streetAddressNumber():null)
				.setAddress2( AonRandom.gt(90)?faker.address().secondaryAddress():null )
				.setZip( AonRandom.gt(10)?faker.address().zipCode():null )
				.setCity( AonRandom.gt(10)?faker.address().city():null )
				.setProvince( AonRandom.gt(10)?faker.address().state():null )
				.setGeozone((AonRandom.gt( 30 )) ? AonDBRandom.getGeozone(ctx,invoice.getDomain()) : null)
				.setParent((AonRandom.gt( 90 )) ? AonDBRandom.getGeozone(ctx,invoice.getDomain()) : null);
		}

		
		double getRetentionPercent() {
			return getRetentionPercent(AonRandom.integer(0, 100));
		}
		private double getRetentionPercent(int x) {
			if ( x >= 0 && x <= 50) return 15.0;
			if ( x > 50 && x <= 75) return 10.0;
			if ( x > 75 && x <= 95) return 8.0;
			return 7.0;
		}
		
		private double getVatPercent(int x) {
			if ( x >= 0 && x <= 50) return 21.0;
			if ( x > 50 && x <= 75) return 10.0;
			if ( x > 75 && x <= 95) return 4.0;
			if ( x > 95 && x <= 98) return 5.0;
			return 0.0;
		}
		
		private double getSurchargePercent(double vatPercent) {
			if (vatPercent == 21) return 5.2;
			else if (vatPercent == 10) return 1.4;
			else if (vatPercent == 4) return 0.5;
			else {
				int x = AonRandom.integer(0, 100);
				if ( x <= 70) return 1.75; 
				if ( x <= 90) return 0.62;
				return 0.0;
			}
		}
	}

	public static Invoice getRandomSales(AONContext ctx, int domain) {
		List<InvoiceFakerTypes> list = Arrays.stream(InvoiceFakerTypes.values())
			.filter(t -> t.isSales() )
			.collect(Collectors.toCollection(LinkedList::new));
		return list
				.get(faker.random().nextInt(list.size()-1))
				.get(ctx, domain);
	}
	
	public static Invoice getRandomNotSales(AONContext ctx, int domain) {
		List<InvoiceFakerTypes> list = Arrays.stream(InvoiceFakerTypes.values())
			.filter(t -> !t.isSales() )
			.collect(Collectors.toCollection(LinkedList::new));
		return list.get(faker.random().nextInt(list.size()-1)).get(ctx, domain);
	}
	
	public static Invoice getRandom(AONContext ctx, int domain) {
		return AonRandom.gt(60)
			?getRandomSales(ctx, domain)
			:getRandomNotSales(ctx, domain);
	}
	

	public static Invoice getRandomRetentionInvoice(final AONContext ctx, int domain, WithholdingType withholdingType) {
		return withholdingType.visit(new WithholdingTypeVisitor<Invoice>() {

			@Override public Invoice visitProfessional() { return getRetentionInvoice( WithholdingType.PROFESSIONAL);   }
			@Override public Invoice visitRenting() { return getRetentionInvoice( WithholdingType.RENTING);   }
			@Override public Invoice visitMovableCapital() { return getRetentionInvoice( WithholdingType.MOVABLE_CAPITAL);   }
			@Override public Invoice visitTransportOperator() { return getRetentionInvoice( WithholdingType.TRANSPORT_OPERATOR);   }
			@Override public Invoice visitM190G02()  { return getRetentionInvoice( WithholdingType.M190_G_02);   }
			@Override public Invoice visitM190G03()  { return getRetentionInvoice( WithholdingType.M190_G_03);   }
			@Override public Invoice visitM190H02()  { return getRetentionInvoice( WithholdingType.M190_H_02);   }
			@Override public Invoice visitM190H03()  { return getRetentionInvoice( WithholdingType.M190_H_03);   }
			@Override public Invoice visitM190I01()  { return getRetentionInvoice( WithholdingType.M190_I_01);   }
			@Override public Invoice visitM190I02()  { return getRetentionInvoice( WithholdingType.M190_I_02);   }
			@Override public Invoice visitM190J()    { return getRetentionInvoice( WithholdingType.M190_J   );   }
			@Override public Invoice visitM190K01()  { return getRetentionInvoice( WithholdingType.M190_K_01);   }
			@Override public Invoice visitM190K03()  { return getRetentionInvoice( WithholdingType.M190_K_03);   }
			@Override public Invoice visitM190K02()  { return getRetentionInvoice( WithholdingType.M190_K_02);   }
			@Override public Invoice visitM193C1()   { return getRetentionInvoice( WithholdingType.M193_C1);     }
			@Override public Invoice visitM193C2()   { return getRetentionInvoice( WithholdingType.M193_C2);     }
			@Override public Invoice visitM193C3()   { return getRetentionInvoice( WithholdingType.M193_C3);     }
			@Override public Invoice visitM190F01()  { return getRetentionInvoice( WithholdingType.M190_F_01);   }
			@Override public Invoice visitM190F021() { return getRetentionInvoice( WithholdingType.M190_F_02_1); }
			@Override public Invoice visitM190F022() { return getRetentionInvoice( WithholdingType.M190_F_02_2); }
			
			@Override
			public Invoice visitFarmer() {
				return InvoiceFakerTypes.SALES_FARMER_RETENTION.get(ctx, domain);
			}

			private Invoice getRetentionInvoice( final WithholdingType wt) {
				return AonRandom.gt(60)
					?InvoiceFakerTypes.SALES_RETENTION.get(ctx, domain)
					:InvoiceFakerTypes.EXPENSES_RETENTION.get(ctx, domain);
			}
			
		});
	}
}

