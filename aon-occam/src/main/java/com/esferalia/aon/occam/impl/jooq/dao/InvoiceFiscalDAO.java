package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceFiscal.INVOICE_FISCAL;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jooq.InsertSetMoreStep;
import org.jooq.Record;

import com.esferalia.aon.jooq.tables.records.InvoiceFiscalRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IVATTaxRegimeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceFiscal;
import com.esferalia.aon.occam.api.model.finance.VATTaxRegime;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class InvoiceFiscalDAO {
	
	private InvoiceFiscalDAO() {
	}
	
	protected static class InvoiceFiscalFiller extends Filler implements Function<Record,InvoiceFiscal> {

		@Override
		public InvoiceFiscal apply(Record r) {
			return buildInvoiceFiscal(r);
		}
		
		public static InvoiceFiscal buildInvoiceFiscal(final Record r) {
			final InvoiceFiscal invFiscal = new InvoiceFiscal()
				.setId(r.getValue(INVOICE_FISCAL.ID))
				.setInvoice(r.getValue(INVOICE_FISCAL.INVOICE))
				.setDomain(r.getValue(INVOICE_FISCAL.DOMAIN))
			;
			IVATTaxRegimeVisitor visitor = new IVATTaxRegimeVisitor() {
				
				@Override 
				public void visitVatGeneral() {
					invFiscal.setVatRegime(VATTaxRegime.VAT_GENERAL, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_GENERAL)));
				}
				
				@Override
				public void visitVatSurcharge() {
					invFiscal.setVatRegime(VATTaxRegime.VAT_SURCHARGE, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_SURCHARGE)));
				}
				@Override
				public void visitVatSimplified() {
					invFiscal.setVatRegime(VATTaxRegime.VAT_SIMPLIFIED, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_SIMPLIFIED)));
				}
				@Override
				public void visitVatAccrualPayment() {
					invFiscal.setVatRegime(VATTaxRegime.VAT_ACCRUAL_PAYMENT, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_ACCRUAL_PAYMENT)));
				}
				@Override
				public void visitVatRebuOperation() {
					invFiscal.setVatRegime(VATTaxRegime.VAT_REBU_OPERATION, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_REBU_OPERATION)));
				}
				@Override
				public void visitVatRebuProfit() {
					invFiscal.setVatRegime(VATTaxRegime.VAT_REBU_PROFIT, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_REBU_PROFIT)));
				}
				@Override
				public void visitVatTravelAgency() {
					invFiscal.setVatRegime(VATTaxRegime.VAT_TRAVEL_AGENCY, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_TRAVEL_AGENCY)));
				}
				@Override
				public void visitVatAgriculture() {
					invFiscal.setVatRegime(VATTaxRegime.VAT_AGRICULTURE, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_AGRICULTURE)));
				}
				@Override
				public void visitVatGold() {
					invFiscal.setVatRegime(VATTaxRegime.VAT_GOLD, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_GOLD)));
				}
				@Override 
				public void visitVatUnionExternal() {
					invFiscal.setVatRegime(VATTaxRegime.VAT_UNION_EXTERNAL, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_UNION_EXTERNAL)));
				}
				@Override 
				public void visitVatUnion() {
					invFiscal.setVatRegime(VATTaxRegime.VAT_UNION, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_UNION)));
				}
				@Override
				public void visitVatImportation() {
					invFiscal.setVatRegime(VATTaxRegime.VAT_IMPORTATION, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_IMPORTATION)));
				}
			};
			
			for (VATTaxRegime vatTaxRegime : VATTaxRegime.values()) {
				vatTaxRegime.visit(visitor);	
			}
			return invFiscal;
		}
	}
	
	private static class AonConfigurationContext {
		
		private AONContext ctx;
		private AonConfiguration config;
		
		private AonConfigurationContext (AONContext ctx,AonConfiguration config) {
			this.ctx = ctx;
			this.config = config;
		}
		@SuppressWarnings("unused")
		private AONContext getContext() {
			return ctx;
		}
		private AonConfiguration getConfiguration() {
			return config;
		}
	}

	private static class Validation {
		
		private static final BiConsumer<AonConfigurationContext,InvoiceFiscal> EMPTY_DOMAIN = (ctx,invoiceFiscal) -> {
			if (invoiceFiscal.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		private static final BiConsumer<AonConfigurationContext,InvoiceFiscal> EMPTY_INVOICE = (ctx,invoiceFiscal) -> {
			if (invoiceFiscal.getInvoice() == null) 
				throw new AonCoreException(AonError.EMPTY_NAME.getMessage());
		};
		
		public static void validate(AonConfigurationContext ctx,InvoiceFiscal invoiceFiscal) throws AonCoreException{
			EMPTY_DOMAIN
			.andThen(EMPTY_INVOICE)
			.accept(ctx, invoiceFiscal);
		}

	}

	private static class AutoComplete {
		private static final BiConsumer<AonConfigurationContext,Invoice> COMPLETE_INVOICE = (ctx,invoice) -> {
			if (invoice.ensureFiscal().getInvoice() == null) {
				invoice.ensureFiscal().setInvoice( invoice.getId() );
				ctx.getContext().log().debug("\t saving invoice_fiscal autocomplete invoice: {0}",invoice.getId());
			}
		};
		
		private static final BiConsumer<AonConfigurationContext,Invoice> COMPLETE_DOMAIN = (ctx,invoice) -> {
			if (invoice.ensureFiscal().getDomain() == null) {
				invoice.ensureFiscal().setDomain( invoice.getDomain() );
				ctx.getContext().log().debug("\t saving invoice_fiscal autocomplete domain: {0}",invoice.getDomain());
			}
		};

		private static final BiConsumer<AonConfigurationContext,Invoice> COMPLETE_VAT_TAX_REGIME = (ctx,invoice) -> {
			IVATTaxRegimeVisitor visitor = new IVATTaxRegimeVisitor() {
				
				@Override 
				public void visitVatGeneral() {
					boolean assigned = false;
					if (invoice.getActivity() != null) {
						EnterpriseActivity act = ctx.getConfiguration().getActivity(invoice.getActivity());
						if (act != null && act.getVatRegime() != null) {
							invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_GENERAL, (act.getVatRegime() == VATRegime.GENERAL) );
							assigned = true;
						}
					}
					if (!assigned) {
						invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_GENERAL, true );
					}
				}
				
				@Override
				public void visitVatSurcharge() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_SURCHARGE, invoice.isSurcharge());
				}
				
				@Override
				public void visitVatSimplified() {
					boolean assigned = false;
					if (invoice.getActivity() != null) {
						EnterpriseActivity act = ctx.getConfiguration().getActivity(invoice.getActivity());
						if (act != null && act.getVatRegime() != null) {
							invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_SIMPLIFIED, (act.getVatRegime() == VATRegime.SIMPLIFIED) );
							assigned = true;
						}
					}
					if (!assigned) {
						invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_SIMPLIFIED, false );
					}
				}
				
				@Override
				public void visitVatAccrualPayment() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_ACCRUAL_PAYMENT, invoice.isVatAccrualPayment());
				}
				
				@Override
				public void visitVatRebuOperation() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_REBU_OPERATION, false );
				}
				
				@Override
				public void visitVatRebuProfit() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_REBU_PROFIT, false );
				}
				
				@Override
				public void visitVatTravelAgency() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_TRAVEL_AGENCY, false );
				}
				
				@Override
				public void visitVatAgriculture() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_ACCRUAL_PAYMENT, invoice.isWithholdingFarmer());
				}
				
				@Override
				public void visitVatGold() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_GOLD, false );
				}
				
				@Override 
				public void visitVatUnionExternal() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_UNION_EXTERNAL, false );
				}
				
				@Override 
				public void visitVatUnion() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_UNION, false );
				}
				
				@Override
				public void visitVatImportation() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_IMPORTATION, invoice.isVatImportation());
				}
			};
			for (VATTaxRegime vatTaxRegime : VATTaxRegime.values()) {
				vatTaxRegime.visit(visitor);	
			}
		};

		public static void autoComplete(AonConfigurationContext ctx, Invoice invoice) throws AonCoreException {
			COMPLETE_INVOICE
			.andThen(COMPLETE_DOMAIN)
			.andThen(COMPLETE_VAT_TAX_REGIME)
			.accept(ctx, invoice);
		}

	}

	protected static InvoiceFiscal insertInvoiceFiscal(AONContext ctx, AonConfiguration config, Invoice invoice) {
		AutoComplete.autoComplete(new AonConfigurationContext(ctx,config), invoice);
		Validation.validate(new AonConfigurationContext(ctx,config), invoice.getFiscal());
		InvoiceFiscal invFiscal = invoice.getFiscal();
		InsertSetMoreStep<InvoiceFiscalRecord> insert = ctx.getDslContext()
				.insertInto(INVOICE_FISCAL)
				.set(INVOICE_FISCAL.DOMAIN, invFiscal.getDomain() )
				.set(INVOICE_FISCAL.INVOICE, invFiscal.getInvoice() );
		IVATTaxRegimeVisitor visitor = new IVATTaxRegimeVisitor() {
			
			@Override 
			public void visitVatGeneral() {
				insert.set(INVOICE_FISCAL.VAT_GENERAL, AonEnumUtils.getByte( invoice.getFiscal().isVatRegimeEnabled(VATTaxRegime.VAT_GENERAL)));
			}
			@Override
			public void visitVatSurcharge() {
				insert.set(INVOICE_FISCAL.VAT_SURCHARGE, AonEnumUtils.getByte( invoice.getFiscal().isVatRegimeEnabled(VATTaxRegime.VAT_SURCHARGE)));
			}
			@Override
			public void visitVatSimplified() {
				insert.set(INVOICE_FISCAL.VAT_SIMPLIFIED, AonEnumUtils.getByte( invoice.getFiscal().isVatRegimeEnabled(VATTaxRegime.VAT_SIMPLIFIED)));
			}
			@Override
			public void visitVatAccrualPayment() {
				insert.set(INVOICE_FISCAL.VAT_ACCRUAL_PAYMENT, AonEnumUtils.getByte( invoice.getFiscal().isVatRegimeEnabled(VATTaxRegime.VAT_ACCRUAL_PAYMENT)));
			}
			@Override
			public void visitVatRebuOperation() {
				insert.set(INVOICE_FISCAL.VAT_REBU_OPERATION, AonEnumUtils.getByte( invoice.getFiscal().isVatRegimeEnabled(VATTaxRegime.VAT_REBU_OPERATION)));
			}
			@Override
			public void visitVatRebuProfit() {
				insert.set(INVOICE_FISCAL.VAT_REBU_PROFIT, AonEnumUtils.getByte( invoice.getFiscal().isVatRegimeEnabled(VATTaxRegime.VAT_REBU_PROFIT)));
			}
			@Override
			public void visitVatTravelAgency() {
				insert.set(INVOICE_FISCAL.VAT_TRAVEL_AGENCY, AonEnumUtils.getByte( invoice.getFiscal().isVatRegimeEnabled(VATTaxRegime.VAT_TRAVEL_AGENCY)));
			}
			@Override
			public void visitVatAgriculture() {
				insert.set(INVOICE_FISCAL.VAT_AGRICULTURE, AonEnumUtils.getByte( invoice.getFiscal().isVatRegimeEnabled(VATTaxRegime.VAT_AGRICULTURE)));
			}
			@Override
			public void visitVatGold() {
				insert.set(INVOICE_FISCAL.VAT_GOLD, AonEnumUtils.getByte( invoice.getFiscal().isVatRegimeEnabled(VATTaxRegime.VAT_GOLD)));
			}
			@Override 
			public void visitVatUnionExternal() {
				insert.set(INVOICE_FISCAL.VAT_UNION_EXTERNAL, AonEnumUtils.getByte( invoice.getFiscal().isVatRegimeEnabled(VATTaxRegime.VAT_UNION_EXTERNAL)));
			}
			@Override 
			public void visitVatUnion() {
				insert.set(INVOICE_FISCAL.VAT_UNION, AonEnumUtils.getByte( invoice.getFiscal().isVatRegimeEnabled(VATTaxRegime.VAT_UNION)));
			}
			@Override
			public void visitVatImportation() {
				insert.set(INVOICE_FISCAL.VAT_IMPORTATION, AonEnumUtils.getByte( invoice.getFiscal().isVatRegimeEnabled(VATTaxRegime.VAT_IMPORTATION)));
			}
		};
		for (VATTaxRegime vatTaxRegime : VATTaxRegime.values()) {
			vatTaxRegime.visit(visitor);	
		}
		Integer id = insert
			.returning(ACCOUNT.ID)
			.fetchOne()
			.getValue(ACCOUNT.ID);
;		invFiscal.setId(id);
		ctx.log().debug("\tINSERT INVOICE_FISCAL invoice: {0,number,integer}",invoice.getId());
		return invoice.getFiscal();
	}
	
}
