package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.InvoiceFiscal.INVOICE_FISCAL;

import java.util.function.BiConsumer;

import org.jooq.Record;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;

import net.aonsolutions.occam.api.model.Invoice;
import net.aonsolutions.occam.api.model.InvoiceFiscal;
import net.aonsolutions.occam.api.model.type.VATRegime;
import net.aonsolutions.occam.api.model.type.VATTaxRegime;
import net.aonsolutions.occam.api.model.type.VATTaxRegime.VATTaxRegimeVisitor;
import net.aonsolutions.occam.impl.AONContext;

class InvoiceFiscalHandler {
	
	private InvoiceFiscalHandler() {
	}
	
	static class InvoiceFiscalFiller extends Filler<InvoiceFiscal> {

		@Override
		public InvoiceFiscal apply(Record r) {
			return build(r);
		}
		
		static InvoiceFiscal build(final Record r) {
			InvoiceFiscal inf =  new InvoiceFiscal()
				.setInvoice(getValue(r, INVOICE_FISCAL.INVOICE))
				.setDomain(getValue(r, INVOICE_FISCAL.DOMAIN))
				.setIssueDate(getValue(r, INVOICE_FISCAL.ISSUE_DATE))
				.setTaxDate(getValue(r, INVOICE_FISCAL.TAX_DATE))
				.setExpDate(getValue(r, INVOICE_FISCAL.EXP_DATE))
			;
			VATTaxRegimeVisitor<Void> visitor = new VATTaxRegimeVisitor<Void>() {
				@Override public Void visitVatGeneral() {inf.setVatRegime(VATTaxRegime.VAT_GENERAL, AonEnumUtils.getBoolean(getValue(r, INVOICE_FISCAL.VAT_GENERAL)));return null;}
				@Override public Void visitVatSurcharge() {inf.setVatRegime(VATTaxRegime.VAT_SURCHARGE, AonEnumUtils.getBoolean(getValue(r, INVOICE_FISCAL.VAT_SURCHARGE)));return null;}
				@Override public Void visitVatSimplified() {inf.setVatRegime(VATTaxRegime.VAT_SIMPLIFIED, AonEnumUtils.getBoolean(getValue(r, INVOICE_FISCAL.VAT_SIMPLIFIED)));return null;}
				@Override public Void visitVatAccrualPayment() {inf.setVatRegime(VATTaxRegime.VAT_ACCRUAL_PAYMENT, AonEnumUtils.getBoolean(getValue(r, INVOICE_FISCAL.VAT_ACCRUAL_PAYMENT)));return null;}
				@Override public Void visitVatRebuOperation() {inf.setVatRegime(VATTaxRegime.VAT_REBU_OPERATION, AonEnumUtils.getBoolean(getValue(r, INVOICE_FISCAL.VAT_REBU_OPERATION)));return null;}
				@Override public Void visitVatRebuProfit() {inf.setVatRegime(VATTaxRegime.VAT_REBU_PROFIT, AonEnumUtils.getBoolean(getValue(r, INVOICE_FISCAL.VAT_REBU_PROFIT)));return null;}
				@Override public Void visitVatTravelAgency() {inf.setVatRegime(VATTaxRegime.VAT_TRAVEL_AGENCY, AonEnumUtils.getBoolean(getValue(r, INVOICE_FISCAL.VAT_TRAVEL_AGENCY)));return null;}
				@Override public Void visitVatAgriculture() {inf.setVatRegime(VATTaxRegime.VAT_AGRICULTURE, AonEnumUtils.getBoolean(getValue(r, INVOICE_FISCAL.VAT_AGRICULTURE)));return null;}
				@Override public Void visitVatGold() {inf.setVatRegime(VATTaxRegime.VAT_GOLD, AonEnumUtils.getBoolean(getValue(r, INVOICE_FISCAL.VAT_GOLD)));return null;}
				@Override public Void visitVatUnionExternal() {inf.setVatRegime(VATTaxRegime.VAT_UNION_EXTERNAL, AonEnumUtils.getBoolean(getValue(r, INVOICE_FISCAL.VAT_UNION_EXTERNAL)));return null;}
				@Override public Void visitVatUnion() {inf.setVatRegime(VATTaxRegime.VAT_UNION, AonEnumUtils.getBoolean(getValue(r, INVOICE_FISCAL.VAT_UNION)));return null;}
				@Override public Void visitVatImportation() {inf.setVatRegime(VATTaxRegime.VAT_IMPORTATION, AonEnumUtils.getBoolean(getValue(r, INVOICE_FISCAL.VAT_IMPORTATION)));return null;}
				@Override public Void visitVatExempt() {inf.setVatRegime(VATTaxRegime.VAT_EXEMPT, false);return null;}
			};
			for (VATTaxRegime vatTaxRegime : VATTaxRegime.values()) {
				vatTaxRegime.visit(visitor);	
			}
			return inf;
			
		}
	}
	
	private static class Validation {
		
		private static final BiConsumer<AONContext,InvoiceFiscal> EMPTY_DOMAIN = (ctx,invoiceFiscal) -> {
			if (invoiceFiscal.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		private static final BiConsumer<AONContext,InvoiceFiscal> EMPTY_INVOICE = (ctx,invoiceFiscal) -> {
			if (invoiceFiscal.getInvoice() == null) 
				throw new AonCoreException(AonError.EMPTY_NAME.getMessage());
		};
		
		static void validate(AONContext ctx,InvoiceFiscal invoiceFiscal) throws AonCoreException{
			EMPTY_DOMAIN
			.andThen(EMPTY_INVOICE)
			.accept(ctx, invoiceFiscal);
		}

	}

	private static class AutoComplete {

		private static final BiConsumer<AONContext,Invoice> COMPLETE_INVOICE_DATA = (ctx,invoice) -> {
			invoice.ensureFiscal().setInvoice( invoice.getId() );
			invoice.ensureFiscal().setDomain( invoice.getDomain() );
			invoice.ensureFiscal().setIssueDate( invoice.getHeader().getIssueDate() );
			invoice.ensureFiscal().setTaxDate( invoice.getHeader().getTaxDate() );
		};
		
		private static final BiConsumer<AONContext,Invoice> COMPLETE_VAT_TAX_REGIME = (ctx,invoice) -> {
			VATTaxRegimeVisitor<Void> visitor = new VATTaxRegimeVisitor<Void>() {
				
				@Override 
				public Void visitVatGeneral() {
					invoice.getHeader().getActivity()
						.filter(act -> act.getVatRegime() != null)
						.ifPresentOrElse( 
							act -> invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_GENERAL, (act.getVatRegime() == VATRegime.GENERAL))
							,() -> invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_GENERAL, true )
						);
					return null;
				}
				
				@Override
				public Void visitVatSurcharge() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_SURCHARGE, invoice.getHeader().isSurcharge());
					return null;
				}
				
				@Override
				public Void visitVatSimplified() {
					invoice.getHeader().getActivity()
						.filter(act -> act.getVatRegime() != null)
						.ifPresentOrElse( 
								act -> invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_SIMPLIFIED, (act.getVatRegime() == VATRegime.SIMPLIFIED))
								,() -> invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_SIMPLIFIED, false )
						)
					;
					return null;
				}
				
				@Override
				public Void visitVatAccrualPayment() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_ACCRUAL_PAYMENT, invoice.getHeader().isVatAccrualPayment());
					return null;
				}
				
				@Override
				public Void visitVatRebuOperation() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_REBU_OPERATION, false );
					return null;
				}
				
				@Override
				public Void visitVatRebuProfit() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_REBU_PROFIT, false );
					return null;
				}
				
				@Override
				public Void visitVatTravelAgency() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_TRAVEL_AGENCY, false );
					return null;
				}
				
				@Override
				public Void visitVatAgriculture() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_AGRICULTURE, invoice.getHeader().isWithholdingFarmer());
					return null;
				}
				
				@Override
				public Void visitVatGold() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_GOLD, false );
					return null;
				}
				
				@Override 
				public Void visitVatUnionExternal() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_UNION_EXTERNAL, false );
					return null;
				}
				
				@Override 
				public Void visitVatUnion() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_UNION, false );
					return null;
				}
				
				@Override
				public Void visitVatImportation() {
					if (invoice.isVatImportationAvailable() && invoice.isVatImportationAmountValid() ) {
						invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_IMPORTATION, invoice.isVatImportation());
					} else {
						invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_IMPORTATION, false);
					}
					return null;
				}

				@Override
				public Void visitVatExempt() {
					invoice.ensureFiscal().setVatRegime(VATTaxRegime.VAT_EXEMPT, false);
					return null;
				}
			};
			for (VATTaxRegime vatTaxRegime : VATTaxRegime.values()) {
				vatTaxRegime.visit(visitor);	
			}
		};

		static void autoComplete(AONContext ctx, Invoice invoice) throws AonCoreException {
			COMPLETE_INVOICE_DATA
			.andThen(COMPLETE_VAT_TAX_REGIME)
			.accept(ctx, invoice);
		}

	}
	
	static InvoiceFiscal save(AONContext ctx, Invoice invoice) {
		InvoiceFiscal invFiscal = invoice.ensureFiscal();
		AutoComplete.autoComplete(ctx, invoice);
		Validation.validate(ctx, invFiscal);
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
	
	static void delete(AONContext ctx, Integer invoice) {
		int count = ctx.getDslContext()
			.delete(INVOICE_FISCAL)
			.where(INVOICE_FISCAL.INVOICE.equal(invoice))
			.execute();
		ctx.log().debug("DELETE INVOICE_FISCAL factura: {0} ({1} filas)",invoice,count);
	}

		

}
