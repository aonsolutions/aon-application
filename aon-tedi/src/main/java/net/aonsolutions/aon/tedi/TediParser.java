package net.aonsolutions.aon.tedi;

import java.util.LinkedList;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.tedi.TediContextKey;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingInvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingInvoiceDAO.InvoiceRegistryInitializer;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceDetail;
import es.translogia.tedi.ewok.TediInvoiceTax;
import es.translogia.tedi.ewok.TediTaxType;
import net.aonsolutions.aon.tedi.visitors.InvoiceTypeVisitor;

public class TediParser {
	
	@FunctionalInterface
	private static interface ITediInvoiceToAonInvoice {
		void to(AONContext ctx, AonConfiguration aonCtx, TediResult result);
	}
	
	@FunctionalInterface
	private static interface ITediInvoiceDetailToAonInvoiceDetail {
		void to(TediResult result,TediInvoiceDetail tediDetail,InvoiceDetail aonDetail);
	}
	
	private enum TediInvoiceDetailTransfer {
		DESCRIPTION( (result,tedi,aon) -> aon.setDescription( tedi.getDescription())),
		QUANTITY( (result,tedi,aon) -> aon.setQuantity( AonNumberUtils.zeroIfNull(tedi.getQuantity()))),
		PRICE( (result,tedi,aon) -> aon.setPrice( AonNumberUtils.zeroIfNull(tedi.getPrice()))),
		DISCOUNT( (result,tedi,aon) -> aon.setDiscountExpression( AonNumberUtils.toString(tedi.getDiscount()))),
		AMOUNT( (result,tedi,aon) -> aon.setTaxableBase( AonNumberUtils.zeroIfNull(tedi.getAmount()))),
		BASE( (result,tedi,aon) -> {
			if (AonMathUtils.isZero(aon.getTaxableBase())) {
				aon.setTaxableBase( AonNumberUtils.zeroIfNull(tedi.getBase()));	
			}
		}),
		VAT( (result,tedi,aon) -> {
			if (tedi.getVat() != null) {
				double base = AonNumberUtils.zeroIfNull(tedi.getAmount());
				double percent = AonNumberUtils.zeroIfNull(tedi.getVat());
				double surcharge = AonNumberUtils.zeroIfNull(tedi.getSurcharge());
				double quota = AonMathUtils.round( base * percent / 100 );
				double surchargeQuota = AonMathUtils.round( base * surcharge / 100 );
				double deductibleQuota = AonMathUtils.round( quota );
				aon.addInvoiceTax( 
					new InvoiceTax()
						.setTaxType( TaxType.VAT )
						.setBase( base )
						.setPercentage( percent )
						.setQuota( quota )
						.setSurcharge( surcharge )
						.setSurchargeQuota( surchargeQuota )
						.setVatDeductionType( VatDeductionType.WITH_RIGHT )
						.setDeductiblePercent( 100.0 )
						.setDeductibleQuota( deductibleQuota )
						); 
			}
		}),
		;
		
		private ITediInvoiceDetailToAonInvoiceDetail toAon;

		private TediInvoiceDetailTransfer(ITediInvoiceDetailToAonInvoiceDetail toAon) {
			this.toAon = toAon;
		}


		private TediResult to(TediResult result,TediInvoiceDetail tediDetail,InvoiceDetail aonDetail) {
			toAon.to(result,tediDetail,aonDetail);
			return result;
		}

		private static TediResult toAon(TediResult result,TediInvoiceDetail tediDetail,InvoiceDetail aonDetail) {
			for (TediInvoiceDetailTransfer token : TediInvoiceDetailTransfer.values()) {
				token.to(result,tediDetail,aonDetail);
			}
			return result;
		}
	}

	private enum TediInvoiceTransfer {
		TYPE	( (ctx, aonCtx,result) -> {
			if (result.getTedi().getType() != null) {
				result.getInvoice().setType(new InvoiceTypeVisitor(result.getTedi()).getInvoiceType()); 	
			}
		}), 
		ISSUE_DATE( (ctx, aonCtx,result) -> {
			result.getInvoice().setIssueDate(result.getTedi().getDate());
			result.getInvoice().setTaxDate(result.getTedi().getDate());
		}),
		TOTAL( (ctx, aonCtx,result) -> {
			result.getInvoice().setTotal( AonNumberUtils.todouble(result.getTedi().getTotal()));
		}),
		REGISTRY ( (ctx, aonCtx,result) -> {
			if (result.getTedi().getRegistry() != null) {
				Invoice invoice = result.getInvoice();
				invoice.setDomain(ctx.getDomainId());
				
				if (invoice.getRegistry() == null) {
					LinkedList<AccountingRegistry> registries = 
						RegistryDAO.getAccountingRegistries(ctx, f -> f.getDocumentProperty().eq(result.getTedi().getRdocument()))
							.filter( ar -> (invoice.isSales() && ar.getType() == AccountingRegistryType.CUSTOMER) 
										|| (!invoice.isSales() && ar.getType() != AccountingRegistryType.CUSTOMER))
							.collect(Collectors.toCollection(LinkedList::new));
					boolean ok = false;
					if (registries != null && registries.size() == 1) {
						AccountingRegistry ar = registries.get(0);
						if (invoice.isSales()) {
							if (ar.getType() == AccountingRegistryType.CUSTOMER) {
								ok = true;
							}
						} else {
							if (ar.getType() == AccountingRegistryType.CREDITOR) {
								if (invoice.getType() == InvoiceType.PURCHASE) {
									invoice.setType( InvoiceType.EXPENSES );
									result.add( TediErrorMessages.C003.inf(TediContextKey.TYPE,TediContextKey.TYPE.getDescription(),InvoiceType.EXPENSES.getDescription()));
								}
								ok = true;
							}
							if (ar.getType() == AccountingRegistryType.SUPPLIER) {
								if (invoice.getType() != InvoiceType.PURCHASE) {
									invoice.setType( InvoiceType.PURCHASE );
									result.add( TediErrorMessages.C003.inf(TediContextKey.TYPE,TediContextKey.TYPE.getDescription(),InvoiceType.PURCHASE.getDescription()));
								}
								ok = true;
							}
						}
						if (ok) {
							AccountingInvoice ai = result.getAccountingInvoice();
							ai.setRegistry(ar);
							ai.setSuggestedAccounts(AccountingInvoiceDAO.getSuggestedAccounts(ctx,ar.getId()));
							invoice.setRegistry(ar.getId())
								.setType(ar.getType().getInvoiceType())
								.setTransaction(ar.getTransaction());
							ar.getType().visit(ar, new  InvoiceRegistryInitializer(ctx, ai.getInvoice(), aonCtx));
						}
					}
					
					// Gestion para cuando hay mas de un registry válido.
					
					if (!ok) {
						result.getInvoice().setRegistryDocument(result.getTedi().getRegistry().getDocument());
						result.getInvoice().setRegistryDocumentCountry(Country.safeValueOf(result.getTedi().getRegistry().getDocumentCountry()));
						if (result.getInvoice().getRegistryDocumentCountry() == null) {
							result.getInvoice().setRegistryDocumentCountry(Country.ES);
							result.add( TediErrorMessages.C003.inf(TediContextKey.RDOCUMENT_COUNTRY,TediContextKey.RDOCUMENT_COUNTRY.getDescription(),Country.ES.getIso2()));
						}
						if (result.getInvoice().getRegistryDocumentType() == null) {
							// TDOO 
						}
						result.getInvoice().setRegistryName(result.getTedi().getRegistry().getName());
						if (result.getInvoice().getRegistryDocumentCountry() == null || result.getInvoice().getRegistryDocumentCountry() == Country.ES) {
							result.getInvoice().setTransaction( InvoiceTransactionType.NATIONAL);
							result.add( TediErrorMessages.C003.inf(TediContextKey.TRANSACTION,TediContextKey.TRANSACTION.getDescription(),InvoiceTransactionType.NATIONAL.getDescription()));
						} else if ( result.getInvoice().getRegistryDocumentCountry().isIntracommunityCountry() ) {
							result.getInvoice().setTransaction( InvoiceTransactionType.INTRACOMMUNITY);
							result.add( TediErrorMessages.C003.inf(TediContextKey.TRANSACTION,TediContextKey.TRANSACTION.getDescription(),InvoiceTransactionType.INTRACOMMUNITY.getDescription()));
						}
					}
				}
			} else {
				result.getInvoice().setRegistryDocument(result.getTedi().getRdocument());
				result.getInvoice().setRegistryName(result.getTedi().getRname());
			}
		}),
		SERIES	( (ctx, aonCtx,result) -> {
			if (result.getTedi().isEmitida()) {
				result.getInvoice().setSeries(result.getTedi().getSeries());
			}
		}), 
		NUMBER	( (ctx, aonCtx,result) -> {
			if (result.getTedi().getNumber() != null) {
				result.getInvoice().setNumber(result.getTedi().getNumber());
			} else {
				if (result.getTedi().isEmitida()) {
					result.add( TediErrorMessages.C003.inf(TediContextKey.NUMBER, TediContextKey.NUMBER.getDescription(), 0) );
				}
				result.getInvoice().setNumber(0);
			}
		}), 
		REFERENCE_CODE( (ctx, aonCtx,result) -> result.getInvoice().setReferenceCode(result.getTedi().getReference())), 
		ADDRESS( (ctx, aonCtx,result) -> {
			if (result.getTedi().getRegistry() != null && result.getTedi().getRegistry().getAddress() != null) {
				result.getInvoice().setAddress(result.getTedi().getRegistry().getAddress().getAddress());
				result.getInvoice().setAddressTown(result.getTedi().getRegistry().getAddress().getCity());
				result.getInvoice().setAddressZIP(result.getTedi().getRegistry().getAddress().getPostalCode());
				result.getInvoice().setAddressProvince(result.getTedi().getRegistry().getAddress().getProvince());

				// *************************************************************************
				// TODO Soporte en OCCAM -> Invoice para el pais de la dirección. !!!! IMPORTANTE!!!
				result.getInvoice().setAddressProvinceCode(result.getTedi().getRegistry().getAddress().getCountry());
				// *************************************************************************

			}
		}),
		DETAILS( (ctx, aonCtx,result) -> {
			if ( result.getTedi().getDetails() != null) {
				for ( int i = 0; i < result.getTedi().getDetails().size(); i++) {
					TediInvoiceDetail tid = result.getTedi().getDetails().get(i);
					if (tid.getVat() != null) {
						InvoiceDetail id = new InvoiceDetail()
								.setSource(InvoiceSource.ACCOUNT)
								.setLine( (short) (1 + i));
						if (result.getInvoice().getDetails() == null) {
							result.getInvoice().setDetails( new LinkedList<InvoiceDetail>());
						}
						result.getInvoice().getDetails().add(id);
						TediInvoiceDetailTransfer.toAon(result,tid,id);
					}
					
				}
			}
		}),
		TAXES( (ctx, aonCtx,result) -> {
			boolean hasDetails = (result.getInvoice().getDetails() != null && result.getInvoice().getDetails().size() > 0);
			if ( result.getTedi().getTaxes() != null) {
				for ( int i = 0; i < result.getTedi().getTaxes().size(); i++) {
					TediInvoiceTax tit = result.getTedi().getTaxes().get(i);
					InvoiceBreakdown ib = new InvoiceBreakdown()
							.setTaxType(tit.getTaxType() == TediTaxType.IVA? TaxType.VAT : TaxType.RETENTION )
							.setBase( tit.getBase() )
							.setPercentage( tit.getPercentage() )
							.setQuota( AonNumberUtils.todouble(tit.getQuota()) )
							.setSurcharge( AonNumberUtils.todouble( tit.getSurcharge()) )
							.setSurchargeQuota( AonNumberUtils.todouble( tit.getSurchargeQuota()))
							;
					if (result.getInvoice().getBreakdown() == null) {
						result.getInvoice().setBreakdown( new LinkedList<InvoiceBreakdown>());
					}
					result.getInvoice().getBreakdown().add(ib);
					if (!hasDetails) {
						TediInvoiceDetail tid = new TediInvoiceDetail()
								.setDescription("AutoGenerated")
								.setQuantity(1.0)
								.setPrice(ib.getBase())
								.setDiscount(0.0)
								.setAmount(ib.getBase())
								.setVat(ib.getPercentage())
								.setSurcharge(ib.getSurcharge());
						InvoiceDetail id = new InvoiceDetail()
							.setSource(InvoiceSource.ACCOUNT)
							.setLine( (short) (1 + i));
						if (result.getInvoice().getDetails() == null) {
							result.getInvoice().setDetails( new LinkedList<InvoiceDetail>());
						}
						result.getInvoice().getDetails().add(id);
						TediInvoiceDetailTransfer.toAon(result,tid,id);
					}
				}
			}
		})
		
		;
		private ITediInvoiceToAonInvoice toAon;

		private TediInvoiceTransfer(ITediInvoiceToAonInvoice toAon) {
			this.toAon = toAon;
		}


		private TediResult to(AONContext ctx, AonConfiguration aonCtx, TediResult result) {
			toAon.to(ctx, aonCtx,result);
			return result;
		}

		private static TediResult toAon(AONContext ctx, AonConfiguration aonCtx,TediResult result) {
			for (TediInvoiceTransfer token : TediInvoiceTransfer.values()) {
				token.to(ctx, aonCtx,result);
			}
			return result;
		}
	}
	
	public static TediResult toFullInvoice(AONContext ctx, AonConfiguration aonCtx, TediInvoice tedi) {
		AccountingInvoice ai = new AccountingInvoice();
		ai.setInvoice(new Invoice());
		
		// TODO
		ai.setWorkplace(aonCtx.getWorkplaces().get(0).getId());
		// ----
		TediResult result = new TediResult(tedi, ai);
		TediInvoiceTransfer.toAon(ctx, aonCtx,result);
		fillVats(ctx, aonCtx, result);
		ai.setAccountEntry(getEntryBase(ctx,aonCtx,ai));
		TediValidator.validateInvoice(result);
		return result; 
	}
	
	private static AccountEntry getEntryBase(AONContext ctx, AonConfiguration aonCtx,AccountingInvoice ai) {
		EnterpriseActivity ea = aonCtx.getMainActivity();
		Integer activity = (ea==null?null:ea.getId());
		Integer periodId = null;
		if (ai.getInvoice().getIssueDate() != null) {
			AccountPeriod period = AccountPeriodDAO.getPeriod(ctx, ai.getInvoice().getIssueDate());
			periodId = (period == null? null : period.getId());
		}
		AccountEntry accountEntry = new AccountEntry()
				.setPeriod(periodId)
				.setDomain(ai.getInvoice().getDomain())
				.setConfidential(false)
				.setEntryDate(ai.getInvoice().getIssueDate())
				.setActivity(activity)
				.setDirty(false);
		ai.getInvoice().getType().visit(ai.getInvoice(),  new IInvoiceTypeVisitor() {
			@Override public void visitUndeductible(Invoice invoice) {accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);}
			@Override public void visitSales(Invoice invoice) {accountEntry.setEntryType(AccountEntryType.SALES_INVOICE);}
			@Override public void visitPurchase(Invoice invoice) {accountEntry.setEntryType(AccountEntryType.PURCHASE_INVOICE);}
			@Override public void visitExpenses(Invoice invoice) {accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);}
		});
		return accountEntry;
	}
	

	private static void fillVats(AONContext ctx, AonConfiguration aonCtx, TediResult result) {
		AccountingInvoice ai = result.getAccountingInvoice();
		Invoice invoice = result.getInvoice();
		
		if (ai.getVats() == null) {
			ai.setVats( new LinkedList<InvoiceVAT>());
		}
		
		// *******************
		boolean withholding = false; // TODO Buscar en breakdown por si hay retencion.
		// *******************
		
		Account outputAccount = aonCtx.getDefaultChargedVatAccount();
		Account inputAccount = aonCtx.getDefaultPaidVatAccount();
		Account adjAccount = aonCtx.getVatNegativeAdjustAccount();
		
		Account expAccount = null;
		if (invoice.isSales() ) {
			expAccount = aonCtx.getDefaultSalesAccount();
		} else {
			if (invoice.isPurchase() ) {
				expAccount = aonCtx.getDefaultPurchaseAccount();	
			} else {
				if ( ai.getSuggestedAccounts() != null && ai.getSuggestedAccounts().size() > 0) {
					expAccount = ai.getSuggestedAccounts().get(0);
				} else {
					expAccount = AccountDAO.get(ctx, "629000000");
					if (expAccount == null) {
						expAccount = AccountDAO.getAccounts(ctx, filter -> filter.getCodeProperty().like("629%") )
								.findFirst()
								.orElse(null);
					}
				}
			}
		}
		if (invoice.getDetails() != null) {
			for (InvoiceDetail detail : invoice.getDetails()) {
				InvoiceTax tax = (detail.getInvoiceTaxes() != null && detail.getInvoiceTaxes().size() > 0)
						?detail.getInvoiceTaxes().get(0)
								:new InvoiceTax();
						InvoiceVAT vat = new InvoiceVAT()			
								.setVatDeductionType(VatDeductionType.WITH_RIGHT)
								.setBase(detail.getTaxableBase())
								.setPercentage(tax.getPercentage())
								.setQuota(tax.getQuota())
								.setSurcharge(tax.getSurcharge())
								.setSurchargeQuota(tax.getSurchargeQuota())
								.setInvestAsset(tax.getInvestAsset())
								.setDeductiblePercent(tax.getDeductiblePercent())
								.setDeductibleQuota(tax.getDeductibleQuota())
								.setWithholding(withholding)
								
								.setOutputAccountId( outputAccount == null ? null : outputAccount.getId() )
								.setOutputAccountCode( outputAccount == null ? null : outputAccount.getCode() )
								.setOutputAccountDescription( outputAccount == null ? null : outputAccount.getDescription() )
								
								.setInputAccountId( inputAccount == null ? null : inputAccount.getId() )
								.setInputAccountCode( inputAccount == null ? null : inputAccount.getCode() )
								.setInputAccountDescription( inputAccount == null ? null : inputAccount.getDescription() )
								
								.setAdjAccountId( adjAccount == null ? null : adjAccount.getId() )
								.setAdjAccountCode( adjAccount == null ? null : adjAccount.getCode() )
								.setAdjAccountDescription( adjAccount == null ? null : adjAccount.getDescription() )
								
								.setExpAccountId( expAccount == null ? null : expAccount.getId() )
								.setExpAccountCode( expAccount == null ? null : expAccount.getCode() )
								.setExpAccountDescription( expAccount == null ? null : expAccount.getDescription() )
								;
						ai.addVat(vat);
			}
		}
	}
}
