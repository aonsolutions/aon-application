package net.aonsolutions.occam.impl.handler;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.esferalia.aon.watson.error.AonCoreException;
import com.github.javafaker.Faker;

import net.aonsolutions.occam.api.model.Activity;
import net.aonsolutions.occam.api.model.AonRandom;
import net.aonsolutions.occam.api.model.Creditor;
import net.aonsolutions.occam.api.model.Customer;
import net.aonsolutions.occam.api.model.Invoice;
import net.aonsolutions.occam.api.model.InvoiceAddress;
import net.aonsolutions.occam.api.model.InvoiceBuilder;
import net.aonsolutions.occam.api.model.Registry;
import net.aonsolutions.occam.api.model.Supplier;
import net.aonsolutions.occam.api.model.type.InvoiceSource;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.InvoiceType;
import net.aonsolutions.occam.api.model.type.InvoiceType.InvoiceTypeVisitor;
import net.aonsolutions.occam.api.model.type.StreetType;
import net.aonsolutions.occam.api.model.type.WithholdingType;
import net.aonsolutions.occam.api.model.type.WithholdingType.WithholdingTypeVisitor;
import net.aonsolutions.occam.impl.AONContext;

public class InvoiceFaker {
	private static Faker faker = new Faker( Locale.of("es") );
	
	static enum InvoiceFakerTypes {
		// Venta Nacional
		SALES_NATIONAL {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }
			
			@Override
			public Invoice build( AONContext ctx, int domain ) {
				return fill(ctx,domain
					,getHeader(ctx,domain)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setWithholding(false)
					);
			}
		},
		// Venta Nacional
		SALES_NATIONAL_SURCHARGE {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }
			
			@Override
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setSurcharge(true)
						.setWithholding(false)
					);
			}
		},
		// Venta Canarias, Ceuta y Melilla
		SALES_CAN_CEU_MEL {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }

			@Override
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
						.setTransaction(InvoiceTransactionType.CAN_CEU_MEL)
					);
			}
		},
		// Prestacion de servicio Canarias, Ceuta y Melilla
		SALES_CAN_CEU_MEL_SERVICE {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }

			@Override
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
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
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setWithholding(false)
						.setVatAccrualPayment(true)
					);
			}
		},
		// Compra Nacional 
		PURCHASE_NATIONAL {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setVatAccrualPayment(false)
						.setWithholding(false)
					);
			}
		},
		// Compra Nacional de servicios 
		PURCHASE_NATIONAL_SERVICE {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setService(true)
						.setWithholding(false)
					);
			}
		},
		// Compra Nacional de servicios con Retención 
		PURCHASE_NATIONAL_RETENTION_SERVICE {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setService(true)
						.setWithholding( true )
						.setWithholdingPercent(getRetentionPercent())
					);
			}
		},
		// Compra Nacional Criterio de caja
		PURCHASE_NATIONAL_ACCRUAL_PAYMENT {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }
			
			@Override
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setVatAccrualPayment(true)
						.setWithholding(isSales())
					);
			}
		},
		// Compra Intracomunitaria
		PURCHASE_INTRACOMMUNITY {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
						.setTransaction(InvoiceTransactionType.INTRACOMMUNITY)
					);
			}
		},
		// Compra ISP
		PURCHASE_ISP {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
						.setTransaction(InvoiceTransactionType.OTHER_ISP)
					);
			}
		},
		// Compra Nacional
		PURCHASE_EXTRACOMMUNITY {
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
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
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
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
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
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
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
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
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
						.setTransaction(InvoiceTransactionType.NATIONAL)
					);
			}
		},
		// Gasto Nacional Criterio de caja
		EXPENSES_NATIONAL_ACCRUAL_PAYMENT {
			@Override 
			public InvoiceType getType() { return InvoiceType.EXPENSES; }

			@Override
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
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
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setWithholding( true )
						.setWithholdingPercent(getRetentionPercent())
					);
			}
		},
		SALES_RETENTION {
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }
			
			@Override
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setWithholding( true )
						.setWithholdingPercent(getRetentionPercent())
					);
			}
		},
		// Venta con retención en régimen agríccola
		SALES_FARMER_RETENTION{
			@Override 
			public InvoiceType getType() { return InvoiceType.SALES; }

			@Override
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setWithholdingFarmer( true )
						.setWithholdingPercent(getRetentionPercent())
					);
			}
		},
		// Compra con retención en régimen agríccola
		PURCHASE_FARMER_RETENTION{
			@Override 
			public InvoiceType getType() { return InvoiceType.PURCHASE; }

			@Override
			public Invoice build( AONContext ctx, int domain) {
				return fill(ctx,domain 
					,getHeader(ctx,domain)
						.setTransaction(InvoiceTransactionType.NATIONAL)
						.setWithholdingFarmer( true )
						.setWithholdingPercent(getRetentionPercent())
					);
			}
		},
		;
		
		public abstract Invoice build( AONContext ctx, int domain );
		public abstract InvoiceType getType();
		
		public boolean isSales() {
			return getType() == InvoiceType.SALES;
		}
		
		InvoiceBuilder getHeader( AONContext ctx, int domain) {
			Date issueDate = faker.date().past(10, TimeUnit.DAYS);
			Activity activity = AonDBRandom.getActivity(ctx, domain);
			
			final InvoiceBuilder builder = new InvoiceBuilder();
			builder.invoice( new Invoice() )
				.setDomain( domain )
				.setIssueDate( issueDate )
				.setTaxDate( issueDate )
				.setType( getType() )
				.setConfidential( AonRandom.gt(95) )
				.setActivity( activity )
				.setSeller( AonRandom.gt(90) ? AonDBRandom.getSeller(ctx, domain) : null)
				.visit(new InvoiceTypeVisitor<InvoiceBuilder>() {
				
					private InvoiceBuilder fillRegistryData(Registry reg) {
						return builder
							.setRegistry(reg.getId())
							.setRegistryDocumentType(reg.getDocumentType())
							.setRegistryDocumentCountry(reg.getDocumentCountry())
							.setRegistryDocument(reg.getDocument())
							.setRegistryName(reg.getName())
						;
					}
					
					@Override
					public InvoiceBuilder visitSales() {
						Customer customer = AonDBRandom.getCustomer( ctx, domain );
						boolean withholding = customer.isWithholding() && ctx.getCompany(domain).get().isWithholding(); 
						return fillRegistryData( customer )
							.setSeries(ctx.getApplicationParameters(domain).getDefaultInvoiceSeries().orElse(null))
							.setScope(customer.getScope())
							.setTransaction( customer.getTransaction() )
							.setService( AonRandom.gt(80) )
							.setVatAccrualPayment(ctx.getCompany(domain).get().isVatAccrualPayment())
							.setSurcharge(customer.isSurcharge())
							.setWithholding(withholding)
							.setWithholdingPercent(withholding?getRetentionPercent():0.0)
						;
					}
					
					@Override
					public InvoiceBuilder visitPurchase() {
						Supplier supplier = AonDBRandom.getSupplier( ctx, domain );
						boolean withholding = supplier.isWithholding() || supplier.isWithholdingFarmer();
						return fillRegistryData(supplier)
							.setReferenceCode(AonRandom.uuid(32))
							.setScope(supplier.getScope())
							.setTransaction(supplier.getTransaction())
							.setService( AonRandom.gt(40) )
							.setVatAccrualPayment(supplier.isVatAccrualPayment())
							.setSurcharge(ctx.getCompany(domain).get().isSurcharge())
							.setWithholding(withholding)
							.setWithholdingPercent(withholding?getRetentionPercent():0.0)
						;
					}
					
					@Override
					public InvoiceBuilder visitExpenses() {
						Creditor creditor = AonDBRandom.getCreditor( ctx, domain);
						return fillRegistryData(creditor)
							.setReferenceCode(AonRandom.uuid(32))
							.setScope(creditor.getScope())
							.setTransaction( creditor.getTransaction() )
							.setService( AonRandom.gt(50) )
							.setVatAccrualPayment(creditor.isVatAccrualPayment())
							.setSurcharge(false)
							.setWithholding(creditor.isWithholding())
							.setWithholdingPercent(creditor.isWithholding()?getRetentionPercent():0.0)
						;
					}
					@Override
					public InvoiceBuilder visitUndeductible() {
						return visitExpenses()
							.setSurcharge(false)
							.setWithholding(false)
							.setService( true );
					}
				}
			);
			return builder;
		}
		
		Invoice fill( AONContext ctx, int domain, InvoiceBuilder builder) {
			IntStream.range(1, 15)
				.forEach( i -> addInvoiceDetail( ctx, domain, builder));
			setInvoiceAddress(ctx, domain, builder);
			Invoice inv = builder.build();
			FinanceHandler.getFinancesForInvoice(ctx, inv).forEach( inv::addFinance);
			return inv;
		}
		
		private void addInvoiceDetail(AONContext ctx, int domain, InvoiceBuilder builder) {
			int basePrecision = 2;
			if ( AonRandom.gt( 97 )) {
				basePrecision = AonRandom.gt( 50 )? 3 : 4;
			}
			double vatPercent = getVatPercent( AonRandom.integer(0, 100));
			builder
				.addDetail()
					.setWorkplace( ctx.getDefaultWorkplace(domain).map(w -> w.getId()).orElse(null) )
					.setDescription(AonRandom.item(5, 50))
					.setQuantity(AonRandom.getDouble(0, 10))
					.setDiscount( AonRandom.gt(90) ? AonRandom.getDouble(0, 100) : 0.0)
					.setPrice(AonRandom.getDouble(0, 100, basePrecision))
					.setPrepayment(AonRandom.gt(95))
					.setSource(InvoiceSource.DIRECT_INVOICE)
					.setInvestAsset( AonRandom.gt(90 , () -> AonDBRandom.getInvestAsset(ctx,domain) ))
					.setVatPercent( vatPercent )
					.setSurchargePercent( getSurchargePercent( vatPercent ))
			;
		}
		
		public InvoiceBuilder setInvoiceAddress(AONContext ctx, int domain, InvoiceBuilder builder) {
			if (AonRandom.gt( 10 )) {
				builder.setInvoiceAddress( 
					new InvoiceAddress()
					.setStreetType(AonRandom.getEnum(StreetType.class, 75))
					.setAddress( AonRandom.gt(10)?faker.address().streetName():null )
					.setNumber( AonRandom.gt(12)?faker.address().streetAddressNumber():null)
					.setAddress2( AonRandom.gt(90)?faker.address().secondaryAddress():null )
					.setZip( AonRandom.gt(10)?faker.address().zipCode():null )
					.setCity( AonRandom.gt(10)?faker.address().city():null )
					.setProvince( AonRandom.gt(10)?faker.address().state():null )
					.setGeozone((AonRandom.gt( 30 )) ? AonDBRandom.getGeozone(ctx,domain) : null)
					.setParent((AonRandom.gt( 90 )) ? AonDBRandom.getGeozone(ctx,domain) : null)
				);
			}
			return builder;
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
				.build(ctx, domain);
	}
	
	public static Invoice getRandomNotSales(AONContext ctx, int domain) {
		List<InvoiceFakerTypes> list = Arrays.stream(InvoiceFakerTypes.values())
			.filter(t -> !t.isSales() )
			.collect(Collectors.toCollection(LinkedList::new));
		return list.get(faker.random().nextInt(list.size()-1)).build(ctx, domain);
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
				return InvoiceFakerTypes.SALES_FARMER_RETENTION.build(ctx, domain);
			}

			private Invoice getRetentionInvoice( final WithholdingType wt) {
				Invoice inv = AonRandom.gt(60)
					?InvoiceFakerTypes.SALES_RETENTION.build(ctx, domain)
					:InvoiceFakerTypes.EXPENSES_RETENTION.build(ctx, domain);
				inv.getWithholding().map( iw -> iw.setWithholdingType(wt))
					.orElseThrow(() -> new AonCoreException("No hay retención definida"));
				return inv;
			}
			
		});
	}
	
	public static Invoice getSalesNationalInvoice(final AONContext ctx, int domain) {
		return InvoiceFakerTypes.SALES_NATIONAL.build(ctx, domain);		
	}
}

