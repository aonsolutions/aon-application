package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.InvoiceFiscal.INVOICE_FISCAL;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jooq.Record;

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
import com.esferalia.aon.watson.server.AonDateUtils;
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
			return new InvoiceFiscal()
				.setInvoice(r.getValue(INVOICE_FISCAL.INVOICE))
				.setDomain(r.getValue(INVOICE_FISCAL.DOMAIN))
				.setIssueDate(r.getValue(INVOICE_FISCAL.ISSUE_DATE))
				.setTaxDate(r.getValue(INVOICE_FISCAL.TAX_DATE))
				.setExpDate(getValue(r, INVOICE_FISCAL.EXP_DATE))
				.setVatRegime(VATTaxRegime.VAT_GENERAL, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_GENERAL)))
				.setVatRegime(VATTaxRegime.VAT_SURCHARGE, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_SURCHARGE)))
				.setVatRegime(VATTaxRegime.VAT_SIMPLIFIED, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_SIMPLIFIED)))
				.setVatRegime(VATTaxRegime.VAT_ACCRUAL_PAYMENT, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_ACCRUAL_PAYMENT)))
				.setVatRegime(VATTaxRegime.VAT_REBU_OPERATION, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_REBU_OPERATION)))
				.setVatRegime(VATTaxRegime.VAT_REBU_PROFIT, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_REBU_PROFIT)))
				.setVatRegime(VATTaxRegime.VAT_TRAVEL_AGENCY, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_TRAVEL_AGENCY)))
				.setVatRegime(VATTaxRegime.VAT_AGRICULTURE, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_AGRICULTURE)))
				.setVatRegime(VATTaxRegime.VAT_GOLD, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_GOLD)))
				.setVatRegime(VATTaxRegime.VAT_UNION_EXTERNAL, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_UNION_EXTERNAL)))
				.setVatRegime(VATTaxRegime.VAT_UNION, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_UNION)))
				.setVatRegime(VATTaxRegime.VAT_IMPORTATION, AonEnumUtils.getBoolean(r.getValue(INVOICE_FISCAL.VAT_IMPORTATION)))
			;
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

		private static final BiConsumer<AonConfigurationContext,Invoice> COMPLETE_INVOICE_DATA = (ctx,invoice) -> {
			invoice.ensureFiscal().setInvoice( invoice.getId() );
			invoice.ensureFiscal().setDomain( invoice.getDomain() );
			invoice.ensureFiscal().setIssueDate( invoice.getIssueDate() );
			invoice.ensureFiscal().setTaxDate( invoice.getTaxDate() );
		};
		
		private static final BiConsumer<AonConfigurationContext,Invoice> COMPLETE_VAT_TAX_REGIME = (ctx,invoice) -> {
			IVATTaxRegimeVisitor visitor = new IVATTaxRegimeVisitor() {
				
				@Override 
				public void visitVatGeneral() {
					boolean assigned = false;
					if (invoice.getActivity() != null) {
						EnterpriseActivity act = ctx.getConfiguration().getActivity(invoice.getActivity().getId());
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
						EnterpriseActivity act = ctx.getConfiguration().getActivity(invoice.getActivity().getId());
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
//					if (invoice.isVatImportationAvailable() && AonMathUtils.isLessThan(invoice.getTotal(), 150.00 )) {
					if (invoice.isVatImportationAvailable() && invoice.isVatImportationAmountValid() ) {
						invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_IMPORTATION, invoice.isVatImportation());
					} else {
						invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_IMPORTATION, false);
					}
				}

				@Override
				public void visitVatExempt() {
					// TODO Auto-generated method stub
					
				}
			};
			for (VATTaxRegime vatTaxRegime : VATTaxRegime.values()) {
				vatTaxRegime.visit(visitor);	
			}
		};

		public static void autoComplete(AonConfigurationContext ctx, Invoice invoice) throws AonCoreException {
			COMPLETE_INVOICE_DATA
			.andThen(COMPLETE_VAT_TAX_REGIME)
			.accept(ctx, invoice);
		}

	}
	
	public static InvoiceFiscal save(AONContext ctx, AonConfiguration config, Invoice invoice) {
		AutoComplete.autoComplete(new AonConfigurationContext(ctx,config), invoice);
		Validation.validate(new AonConfigurationContext(ctx,config), invoice.getFiscal());
		InvoiceFiscal invFiscal = invoice.ensureFiscal();
		ctx.getDslContext()
			.insertInto(INVOICE_FISCAL)
			.set(INVOICE_FISCAL.DOMAIN, invFiscal.getDomain() )
			.set(INVOICE_FISCAL.INVOICE, invFiscal.getInvoice() )
			.set(INVOICE_FISCAL.ISSUE_DATE, AonDateUtils.toSql(invFiscal.getIssueDate()))
			.set(INVOICE_FISCAL.TAX_DATE, AonDateUtils.toSql(invFiscal.getTaxDate()))
			.set(INVOICE_FISCAL.EXP_DATE, AonDateUtils.toSql(invFiscal.getExpDate()))
			.set(INVOICE_FISCAL.VAT_GENERAL, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_GENERAL)))
			.set(INVOICE_FISCAL.VAT_SURCHARGE, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_SURCHARGE)))
			.set(INVOICE_FISCAL.VAT_SIMPLIFIED, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_SIMPLIFIED)))
			.set(INVOICE_FISCAL.VAT_ACCRUAL_PAYMENT, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_ACCRUAL_PAYMENT)))
			.set(INVOICE_FISCAL.VAT_REBU_OPERATION, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_REBU_OPERATION)))
			.set(INVOICE_FISCAL.VAT_REBU_PROFIT, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_REBU_PROFIT)))
			.set(INVOICE_FISCAL.VAT_TRAVEL_AGENCY, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_TRAVEL_AGENCY)))
			.set(INVOICE_FISCAL.VAT_AGRICULTURE, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_AGRICULTURE)))
			.set(INVOICE_FISCAL.VAT_GOLD, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_GOLD)))
			.set(INVOICE_FISCAL.VAT_UNION_EXTERNAL, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_UNION_EXTERNAL)))
			.set(INVOICE_FISCAL.VAT_UNION, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_UNION)))
			.set(INVOICE_FISCAL.VAT_IMPORTATION, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_IMPORTATION)))
			.onDuplicateKeyUpdate()
			.set(INVOICE_FISCAL.DOMAIN, invFiscal.getDomain() )
			.set(INVOICE_FISCAL.ISSUE_DATE, AonDateUtils.toSql(invFiscal.getIssueDate()))
			.set(INVOICE_FISCAL.TAX_DATE, AonDateUtils.toSql(invFiscal.getTaxDate()))
			.set(INVOICE_FISCAL.VAT_GENERAL, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_GENERAL)))
			.set(INVOICE_FISCAL.VAT_SURCHARGE, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_SURCHARGE)))
			.set(INVOICE_FISCAL.VAT_SIMPLIFIED, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_SIMPLIFIED)))
			.set(INVOICE_FISCAL.VAT_ACCRUAL_PAYMENT, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_ACCRUAL_PAYMENT)))
			.set(INVOICE_FISCAL.VAT_REBU_OPERATION, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_REBU_OPERATION)))
			.set(INVOICE_FISCAL.VAT_REBU_PROFIT, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_REBU_PROFIT)))
			.set(INVOICE_FISCAL.VAT_TRAVEL_AGENCY, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_TRAVEL_AGENCY)))
			.set(INVOICE_FISCAL.VAT_AGRICULTURE, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_AGRICULTURE)))
			.set(INVOICE_FISCAL.VAT_GOLD, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_GOLD)))
			.set(INVOICE_FISCAL.VAT_UNION_EXTERNAL, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_UNION_EXTERNAL)))
			.set(INVOICE_FISCAL.VAT_UNION, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_UNION)))
			.set(INVOICE_FISCAL.VAT_IMPORTATION, AonEnumUtils.getByte( invFiscal.isVatRegimeEnabled(VATTaxRegime.VAT_IMPORTATION)))
			.execute()
		;
		ctx.log().debug("\tINSERT INVOICE_FISCAL invoice: {0,number,integer}",invFiscal.getInvoice());
		return invFiscal;
	}
	
	public static void delete(AONContext ctx, Integer invoice) {
		int count = ctx.getDslContext()
			.delete(INVOICE_FISCAL)
			.where(INVOICE_FISCAL.INVOICE.equal(invoice))
			.execute();
		ctx.log().debug("DELETE INVOICE_FISCAL factura: {0} ({1} filas)",invoice,count);
	}

		

}
