package net.aonsolutions.aon.tedi;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceRecorder;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.tedi.TediContextKey;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingInvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingInvoiceDAO.InvoiceRegistryInitializer;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingRegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import es.translogia.tedi.ewok.TediComments;
import es.translogia.tedi.ewok.TediFinance;
import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceDetail;
import es.translogia.tedi.ewok.TediInvoiceTax;
import es.translogia.tedi.ewok.TediPayMethod;
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
	
	@FunctionalInterface
	private static interface ITediFinanceToAonFinance {
		void to(AonConfiguration aonCtx,TediResult result,TediFinance tediFinance,Finance finance);
	}
	
	private enum TediFinanceTransfer {
		PAYMENT( (aonCtx,result,tedi,aon) -> aon.setPayment( !result.getInvoice().isSales())),
		DUE_DATE( (aonCtx,result,tedi,aon) -> aon.setDueDate( tedi.getDueDate() == null? result.getTedi().getDate() : tedi.getDueDate() )),
		AMOUNT( (aonCtx,result,tedi,aon) ->{
			aon.setAmount( tedi != null && tedi.getAmount() != null && AonMathUtils.isNotZero(tedi.getAmount())
					? tedi.getAmount() 
					: AonNumberUtils.zeroIfNull( result.getTedi().getTotal()));
		}),
		IBAN( (aonCtx,result,tedi,aon) -> aon.setBankAccount( new BankAccount(tedi.getIban()))),
		PAYMETHOD( (aonCtx,result,tedi,aon) -> {
			if ( tedi.getPayMethod() != null) {
				PayMethodType temp = null;
				if (tedi.getPayMethod() == TediPayMethod.CASH) {
					temp = PayMethodType.CASH_BASIS;	
				} else if (tedi.getPayMethod() == TediPayMethod.CARD) {
					temp = PayMethodType.CREDIT_CARD;
				} else if (tedi.getPayMethod() == TediPayMethod.TRANSFER) {
					temp = PayMethodType.BANK_TRANSFER;
				} else if (tedi.getPayMethod() == TediPayMethod.BANK) {
					temp = PayMethodType.NEGOTIABLE_DOCUMENT;
				} else if (tedi.getPayMethod() == TediPayMethod.DRAFT) {
					temp = PayMethodType.CHEQUE;
				}
				if (temp != null) {
					boolean hasPaymethods = aonCtx != null && aonCtx.getPayMethods() != null && aonCtx.getPayMethods().size() > 0;
					if (hasPaymethods) {
						for ( PayMethod paymethod : aonCtx.getPayMethods() ) {
							if ( temp == paymethod.getType() ) {
								aon.setPayMethod(paymethod.getId());								
								aon.setPayMethodName(paymethod.getName());
								if ( temp == PayMethodType.CASH_BASIS ) {
									Account cashAccount = aonCtx.getDefaultCashAccount();
									if (cashAccount != null) {
										result.getAccountingInvoice().setPayAccountId(cashAccount.getId());	
										result.getAccountingInvoice().setPayAccountCode(cashAccount.getCode());
										result.getAccountingInvoice().setPayAccountDescription(cashAccount.getDescription());
										aon.setRecordable(true);
									}
								}
								break;
							}
						}
					}
					aon.setPayMethodType(temp);
				}
			}
		}),
		PENDING( (aonCtx,result,tedi,aon) -> aon.setFinanceStatus(FinanceStatus.PENDING ))
		;
		
		private ITediFinanceToAonFinance toAon;

		private TediFinanceTransfer(ITediFinanceToAonFinance toAon) {
			this.toAon = toAon;
		}


		private TediResult to(AonConfiguration aonCtx,TediResult result,TediFinance tediFinance,Finance finance) {
			toAon.to(aonCtx,result,tediFinance,finance);
			return result;
		}

		private static TediResult toAon(AonConfiguration aonCtx,TediResult result,TediFinance tediFinance,Finance finance) {
			for (TediFinanceTransfer token : TediFinanceTransfer.values()) {
				token.to(aonCtx,result,tediFinance,finance);
			}
			return result;
		}
	}

	public static TediResult toFullInvoice(AONContext ctx, AonConfiguration aonCtx, TediInvoice tedi) {
		return toAccountingInvoice(ctx, aonCtx, tedi);
	}
	
	private static AccountEntry getEntryBase(AONContext ctx, AonConfiguration aonCtx,AccountingInvoice ai) {
		Integer activity = null;
		if (aonCtx != null) {
			EnterpriseActivity ea = aonCtx.getMainActivity();
			activity = (ea==null?null:ea.getId());
		}
		Integer periodId = null;
		if (ctx != null && ai.getInvoice().getIssueDate() != null) {
			AccountPeriod period = AccountPeriodDAO.getPeriod(ctx, ai.getInvoice().getIssueDate());
			periodId = (period == null? null : period.getId());
		}
		AccountEntry accountEntry = new AccountEntry()
				.setPeriod(periodId)
				.setDomain(ai.getInvoice().getDomain())
				.setConfidential(false)
				.setEntryDate(ai.getInvoice().getIssueDate())
				.setActivity(activity)
				.setComments(ai.getInvoice().getComments())
				.setDirty(false);
		if (ai.getInvoice().getType() != null) {
			ai.getInvoice().getType().visit(ai.getInvoice(),  new IInvoiceTypeVisitor() {
				@Override public void visitUndeductible(Invoice invoice) {
					accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);
					accountEntry.setUndeductible(true);
				}
				@Override public void visitSales(Invoice invoice) {accountEntry.setEntryType(AccountEntryType.SALES_INVOICE);}
				@Override public void visitPurchase(Invoice invoice) {accountEntry.setEntryType(AccountEntryType.PURCHASE_INVOICE);}
				@Override public void visitExpenses(Invoice invoice) {
					accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);
					accountEntry.setUndeductible(false);
				}
			});
		}
		return accountEntry;
	}
	

	private static void fillVats(AONContext ctx, AonConfiguration aonCtx, TediResult result) {
		AccountingInvoice ai = result.getAccountingInvoice();
		Invoice invoice = result.getInvoice();
				
		if (ai.getVats() == null) {
			ai.setVats( new LinkedList<InvoiceVAT>());
		}

		boolean withholding = false;
		if (invoice.getBreakdown() != null) {
			for (InvoiceBreakdown ib : invoice.getBreakdown() ) {
				if (ib.getTaxType() == TaxType.RETENTION) {
					Account retentionAccount =(invoice.isSales() )
						?(aonCtx!=null?aonCtx.getDefaultPaidRetAccount():null)
						:(aonCtx!=null?aonCtx.getDefaultChargedRetAccount():null);
					withholding = true;
					invoice.setWithholding(true);
					InvoiceWithholding iw = new InvoiceWithholding()
						.setWithholdingType( WithholdingType.PROFESSIONAL )
						.setBase( ib.getBase() )
						.setPercentage( ib.getPercentage() )
						.setQuota( ib.getQuota() )
						.setAccountId( retentionAccount == null? null : retentionAccount.getId() )
						.setAccountCode( retentionAccount == null? null : retentionAccount.getCode() )
						.setAccountDescription( retentionAccount == null? null : retentionAccount.getDescription() )
						;
					ai.setWithholdingData(iw);
					break;
				}
			}
		}
		
		if (aonCtx != null) {
			Account outputAccount = aonCtx.getDefaultChargedVatAccount();
			Account inputAccount = aonCtx.getDefaultPaidVatAccount();
			Account adjAccount = aonCtx.getVatNegativeAdjustAccount();
			Account expAccount = null;
			if (invoice.isSales() ) {
				expAccount = getSalesAccount( ctx,aonCtx,result);
			} else if (invoice.isPurchase() ) {
				expAccount = getPurchaseAccount( ctx,aonCtx,result);
			} else if (invoice.isExpenses() ) {
				expAccount = getExpenseAccount( ctx,aonCtx,result);
			} else if (invoice.isUndeductible() ) {
				expAccount = getUndeductibleAccount( ctx,aonCtx,result);
			}
			if (invoice.getDetails() != null) {
				for (InvoiceDetail detail : invoice.getDetails()) {
					InvoiceVAT vat = null;
					if (detail.getInvoiceTaxes() != null && detail.getInvoiceTaxes().size() > 0) {
						for (InvoiceTax tax : detail.getInvoiceTaxes()) {
							if (tax.getTaxType() == TaxType.VAT) {
								vat = getInvoiceVAT( detail, tax,outputAccount,inputAccount,adjAccount,expAccount, withholding);					
							}
						}
					} else {
						vat = getInvoiceVAT( detail, new InvoiceTax(),outputAccount,inputAccount,adjAccount,expAccount, withholding);
					}
					ai.addVat(vat);
				}
			}
		}
	}
	
	private static InvoiceVAT getInvoiceVAT( InvoiceDetail detail, InvoiceTax tax,Account outputAccount,Account inputAccount,Account adjAccount,Account expAccount, boolean withholding) {
		return new InvoiceVAT()
				.setVatDeductionType(VatDeductionType.WITH_RIGHT)
				.setBase(detail.getTaxableBase())
				.setPercentage(tax.getPercentage())
				.setQuota(tax.getQuota())
				.setSurcharge(tax.getSurcharge())
				.setSurchargeQuota(tax.getSurchargeQuota())
				.setInvestAsset(tax.getInvestAsset())
				.setDeductiblePercent(tax.getDeductiblePercent())
				.setDeductibleQuota(tax.getDeductibleQuota()).setWithholding(withholding)

				.setOutputAccountId(outputAccount == null ? null : outputAccount.getId())
				.setOutputAccountCode(outputAccount == null ? null : outputAccount.getCode())
				.setOutputAccountDescription(outputAccount == null ? null : outputAccount.getDescription())

				.setInputAccountId(inputAccount == null ? null : inputAccount.getId())
				.setInputAccountCode(inputAccount == null ? null : inputAccount.getCode())
				.setInputAccountDescription(inputAccount == null ? null : inputAccount.getDescription())

				.setAdjAccountId(adjAccount == null ? null : adjAccount.getId())
				.setAdjAccountCode(adjAccount == null ? null : adjAccount.getCode())
				.setAdjAccountDescription(adjAccount == null ? null : adjAccount.getDescription())

				.setExpAccountId(expAccount == null ? null : expAccount.getId())
				.setExpAccountCode(expAccount == null ? null : expAccount.getCode())
				.setExpAccountDescription(expAccount == null ? null : expAccount.getDescription());
	}
	
	private static Account getSalesAccount(AONContext ctx, AonConfiguration aonCtx, TediResult result) {
		return aonCtx.getDefaultSalesAccount();
	}
	
	private static Account getPurchaseAccount(AONContext ctx, AonConfiguration aonCtx, TediResult result) {
		Account purchaseAccount = aonCtx.getDefaultPurchaseAccount(); 
		return purchaseAccount;
	}
	private static Account getUndeductibleAccount(AONContext ctx, AonConfiguration aonCtx, TediResult result) {
		return getExpenseAccount(ctx, aonCtx, result);
	}
	
	private static Account getExpenseAccount(AONContext ctx, AonConfiguration aonCtx, TediResult result) {
		AccountingInvoice ai = result.getAccountingInvoice();
		Account expAccount;
		if ( ai.getSuggestedAccounts() != null && ai.getSuggestedAccounts().size() > 0) {
			expAccount = ai.getSuggestedAccounts().get(0);
		} else {
			expAccount = AccountDAO.get(ctx, "629000000");
			if (expAccount == null) {
				expAccount = AccountDAO.getAccounts(ctx, filter -> filter.getCodeProperty().like("629%")
					.and( filter.getEntryEnabledProperty().eq((byte) 1) ) )
					.findFirst()
					.orElse(null);
			}
		}
		return expAccount;
	}
	
	// ***********************************************************************************
	// ***********************************************************************************
	// ***********************************************************************************
	// ***********************************************************************************
	// ***********************************************************************************
	// ***********************************************************************************
	// ***********************************************************************************
	//
	//							TEDI PARSER REFACTOR
	//
	// ***********************************************************************************
	// ***********************************************************************************
	// ***********************************************************************************
	// ***********************************************************************************
	// ***********************************************************************************
	// ***********************************************************************************
	// ***********************************************************************************
	private static class TediParserContext {
		private AONContext ctx;
		private AonConfiguration config;
		private TediResult result;
		
		private TediParserContext(AONContext ctx, AonConfiguration config, TediResult result) {
			this.ctx=ctx;
			this.config=config;
			this.result=result;
		}
		private AONContext getAONContext() {
			return ctx;
		}
		public AonConfiguration getAonConfiguration() {
			return config;
		}
		public TediResult getTediResult() {
			return result;
		}
	}
	
	private static class TediParserContextInvoiceDetail extends TediParserContext {
		private TediInvoiceDetail tediDetail;
		private InvoiceDetail aonDetail;
		
		public TediParserContextInvoiceDetail(TediParserContext ctx, TediInvoiceDetail tediDetail, InvoiceDetail aonDetail) {
			super(ctx.getAONContext(), ctx.getAonConfiguration(), ctx.getTediResult());
			this.tediDetail = tediDetail;
			this.aonDetail = aonDetail;
		}
		public TediInvoiceDetail getTediDetail() {
			return tediDetail;
		}
		public InvoiceDetail getAonDetail() {
			return aonDetail;
		}
	}
	
	
	private static Consumer<TediParserContext> ACCOUNTING_INVOICE_WORKPLACE = (ctx) -> {
		if (ctx.getAonConfiguration() != null && ctx.getAonConfiguration().getWorkplaces() != null && ctx.getAonConfiguration().getWorkplaces().size() > 0) {
			ctx.getTediResult().getAccountingInvoice().setWorkplace(ctx.getAonConfiguration().getWorkplaces().get(0).getId()); 	
		} else {
			ctx.getTediResult().add( TediErrorMessages.C016.err(TediContextKey.WORKPLACE));
		}
	};
	
	private static Consumer<TediParserContext> INVOICE_DOMAIN = (ctx) -> {
		if (ctx.getAONContext() != null) {
			ctx.getTediResult().getInvoice().setDomain( ctx.getAONContext().getDomainId() );
		}
	};
	
	private static Consumer<TediParserContext> INVOICE_TYPE = (ctx) -> {
		if (ctx.getTediResult().getTedi().getType() != null) {
			ctx.getTediResult().getInvoice().setType(new InvoiceTypeVisitor(ctx.getTediResult().getTedi()).getInvoiceType()); 	
		}
	};
	private static Consumer<TediParserContext> INVOICE_ISSUE_DATE = (ctx) -> {
		ctx.getTediResult().getInvoice().setIssueDate(ctx.getTediResult().getTedi().getDate());
	};
	
	private static Consumer<TediParserContext> INVOICE_TAX_DATE = (ctx) -> {
		ctx.getTediResult().getInvoice().setTaxDate(ctx.getTediResult().getTedi().getDate());
	};
	private static Consumer<TediParserContext> INVOICE_EMITIDA_REGISTRY = (ctx) -> {
		if (ctx.getTediResult().getTedi().isEmitida()) {
			fillRegistry(ctx, ar -> ctx.getTediResult().getInvoice().isSales() && ar.getType() == AccountingRegistryType.CUSTOMER);
		}
	};
	private static Consumer<TediParserContext> INVOICE_RECIBIDA_REGISTRY = (ctx) -> {
		TediResult result = ctx.getTediResult();
		if (ctx.getTediResult().getTedi().isRecibida()) {
			if (fillRegistry(ctx, ar -> !result.getInvoice().isSales() && (ar.getType() == AccountingRegistryType.SUPPLIER || ar.getType() == AccountingRegistryType.CREDITOR))) {
				AccountingRegistry ar = result.getAccountingInvoice().getRegistry();
				if (ar.getType() == AccountingRegistryType.CREDITOR) {
					result.getTedi().getSender().setName(ar.getName());
					if (result.getInvoice().getType() == InvoiceType.PURCHASE) {
						result.getInvoice().setType( InvoiceType.EXPENSES );
						result.add( TediErrorMessages.C003.inf(TediContextKey.TYPE,TediContextKey.TYPE.getDescription(),InvoiceType.EXPENSES.getDescription()));
					}
				} else  if (ar.getType() == AccountingRegistryType.SUPPLIER) {
					result.getTedi().getSender().setName(ar.getName());
					if (result.getInvoice().getType() != InvoiceType.EXPENSES) {
						result.getInvoice().setType( InvoiceType.PURCHASE );
						result.add( TediErrorMessages.C003.inf(TediContextKey.TYPE,TediContextKey.TYPE.getDescription(),InvoiceType.PURCHASE.getDescription()));
					}
				}
			}
		}		
	};
	private static Consumer<TediParserContext> INVOICE_TICKET_REGISTRY = (ctx) -> {
		TediResult result = ctx.getTediResult();
		AonConfiguration aonCtx = ctx.getAonConfiguration();
		 if (ctx.getTediResult().getTedi().isTicket()) {
			if (fillRegistry(ctx, ar -> !result.getInvoice().isSales() && ar.getType() == AccountingRegistryType.CREDITOR)){
				if (aonCtx.getDefaultCreditor() != null) {
					AccountingRegistry ar = aonCtx.getDefaultCreditor();
					AccountingInvoice ai = result.getAccountingInvoice();
					Invoice invoice = result.getInvoice();
					TediInvoice tedi = result.getTedi();
					ai.setRegistry(ar);
					ai.setSuggestedAccounts(AccountingInvoiceDAO.getSuggestedAccounts(ctx.getAONContext(), ar.getId()));
					invoice
						.setRegistry(ar.getId())
						.setTransaction(ar.getTransaction());
					ar.getType().visit(ar, new InvoiceRegistryInitializer(ctx.getAONContext(), ai.getInvoice(), aonCtx));
					if (tedi.getSender() != null) {
						invoice.setRegistryDocument(tedi.getSender().getDocument());
						invoice.setRegistryName(tedi.getSender().getName());
					}
				}
			};
		}
	};
	
	private static boolean fillRegistry(TediParserContext ctx,Predicate<AccountingRegistry> filterExpression) {
		TediResult result = ctx.getTediResult();
		Invoice invoice = result.getInvoice();
		if (result.getTedi().getRegistry() != null) {
			invoice.setRegistryDocument(result.getTedi().getRegistry().getDocument());
			invoice.setRegistryName(result.getTedi().getRegistry().getName());
			invoice.setRegistryDocumentCountry(Country.safeValueOf(result.getTedi().getRegistry().getDocumentCountry()));
		}
		if (ctx.getAONContext() != null) {
			LinkedList<AccountingRegistry> registries = AccountingRegistryDAO
					.getAccountingRegistries(ctx.getAONContext(), f -> f.getDocumentProperty().eq(invoice.getRegistryDocument()))
					.filter(filterExpression)
					.collect(Collectors.toCollection(LinkedList::new));
			if (registries != null && registries.size() > 0) {
				if (registries.size() == 1) {
					AccountingRegistry ar = registries.get(0);
					AccountingInvoice ai = result.getAccountingInvoice();
					ai.setRegistry(ar);
					ai.setSuggestedAccounts(AccountingInvoiceDAO.getSuggestedAccounts(ctx.getAONContext(), ar.getId()));
					invoice.setRegistry(ar.getId())
					.setTransaction(ar.getTransaction());
					ar.getType().visit(ar, new InvoiceRegistryInitializer(ctx.getAONContext(), ai.getInvoice(), ctx.getAonConfiguration()));
					return true;
				} else {
					result.setPosibleRegistries(registries);
					result.add( TediErrorMessages.C011.err(TediContextKey.AMBIGUOUS_REGISTRY));
				}
			}
		}
		if (result.getInvoice().getRegistryDocumentCountry() == null) {
			result.getInvoice().setRegistryDocumentCountry(Country.ES);
			result.add( TediErrorMessages.C003.inf(TediContextKey.RDOCUMENT_COUNTRY,TediContextKey.RDOCUMENT_COUNTRY.getDescription(),Country.ES.getIso2()));
		}
		return false;
	}		
	
	private static Consumer<TediParserContext> INVOICE_SERIES = (ctx) -> {
		if (ctx.getTediResult().getTedi().isEmitida()) {
			ctx.getTediResult().getInvoice().setSeries(ctx.getTediResult().getTedi().getSeries());
		}
	};
	
	private static Consumer<TediParserContext> INVOICE_NUMBER = (ctx) -> {
		TediResult result = ctx.getTediResult();
		if (result.getTedi().isEmitida()) {
			if (result.getTedi().getNumber() != null) {
				result.getInvoice().setNumber(result.getTedi().getNumber());
			} else {
				result.add( TediErrorMessages.C003.inf(TediContextKey.NUMBER, TediContextKey.NUMBER.getDescription(), 0) );
				result.getInvoice().setNumber(0);
			}
		}
	};
	
	private static Consumer<TediParserContext> INVOICE_REFERENCE_CODE = (ctx) -> {
		TediResult result = ctx.getTediResult();
		result.getInvoice().setReferenceCode(result.getTedi().getReference());
		if (result.getTedi().isTicket() && AonStringUtils.isBlank(result.getTedi().getReference())) {
			result.getInvoice().setReferenceCode("<auto>"); 
		}
	};

	private static Consumer<TediParserContext> INVOICE_COMMENTS = (ctx) -> {
		TediResult result = ctx.getTediResult();
		if (result.getTedi().getComments() != null) {
			StringBuilder builder = new StringBuilder();
			boolean counter = result.getTedi().getComments().size() > 1;
			int c = 1;
			for (TediComments comment : result.getTedi().getComments()) {
				if (AonStringUtils.isNotBlank(builder.toString())){
					builder.append(AonStringUtils.CR_LF);
				}
				if (counter) {
					builder.append(c + " - ");
				}
				builder.append(comment.getComment());
				c++;
			}
			result.getInvoice().setComments(builder.toString());	
		}
	};

	private static Consumer<TediParserContext> INVOICE_ADDRESS = (ctx) -> {
		TediResult result = ctx.getTediResult();
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
	};
	
	// ******************************************************************
	// ************** INVOICE DETAILS ************************************
	// ******************************************************************
	private static Consumer<TediParserContextInvoiceDetail> INVOICE_DETAIL_SOURCE = (ctx) -> {
		ctx.getAonDetail().setSource(InvoiceSource.ACCOUNT);
	};
	
	private static Consumer<TediParserContextInvoiceDetail> INVOICE_DETAIL_DESCRIPTION = (ctx) -> {
		ctx.getAonDetail().setDescription( ctx.getTediDetail().getDescription() );
	};

	private static Consumer<TediParserContextInvoiceDetail> INVOICE_DETAIL_QUANTITY = (ctx) -> {
		ctx.getAonDetail().setQuantity( AonNumberUtils.zeroIfNull(ctx.getTediDetail().getQuantity()));
	};
	private static Consumer<TediParserContextInvoiceDetail> INVOICE_DETAIL_PRICE = (ctx) -> {
		ctx.getAonDetail().setPrice( AonNumberUtils.zeroIfNull(ctx.getTediDetail().getPrice()));
	};
	private static Consumer<TediParserContextInvoiceDetail> INVOICE_DETAIL_DISCOUNT = (ctx) -> {
		ctx.getAonDetail().setDiscountExpression( AonNumberUtils.toString(ctx.getTediDetail().getDiscount()));
	};
	private static Consumer<TediParserContextInvoiceDetail> INVOICE_DETAIL_TAXABLE_BASE = (ctx) -> {
		ctx.getAonDetail().setTaxableBase( AonNumberUtils.zeroIfNull(ctx.getTediDetail().getAmount()));
		if (AonMathUtils.isZero(ctx.getAonDetail().getTaxableBase())) {
			ctx.getAonDetail().setTaxableBase( AonNumberUtils.zeroIfNull(ctx.getTediDetail().getBase()));	
		}
	};

	private static Consumer<TediParserContextInvoiceDetail> INVOICE_DETAIL_VAT = (ctx) -> {
		TediInvoiceDetail tedi = ctx.getTediDetail();
		InvoiceDetail aon = ctx.getAonDetail();
		if (tedi.getVat() != null) {
			double base = AonNumberUtils.zeroIfNull(aon.getTaxableBase());
			double percent = AonNumberUtils.zeroIfNull(tedi.getVat());
			double surcharge = AonNumberUtils.zeroIfNull(tedi.getSurcharge());
			double quota = AonMathUtils.round( base * percent / 100 );
			double surchargeQuota = AonMathUtils.round( base * surcharge / 100 );
			double deductibleQuota = AonMathUtils.round( quota + surchargeQuota );
			ctx.getAonDetail().addInvoiceTax( 
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
	};
	


	private static Consumer<TediParserContext> INVOICE_DETAILS = (ctx) -> {
		TediResult result = ctx.getTediResult();
		if ( result.getTedi().getDetails() != null) {
			if (result.getInvoice().getDetails() == null) {
				result.getInvoice().setDetails( new LinkedList<InvoiceDetail>());
			}
			for ( int i = 0; i < result.getTedi().getDetails().size(); i++) {
				TediInvoiceDetail tediDetail = result.getTedi().getDetails().get(i);
				InvoiceDetail aonDetail = new InvoiceDetail()
					.setLine( (short) (1 + i));
				result.getInvoice().getDetails().add(aonDetail);
				toAccountingInvoiceDetail( ctx, tediDetail,aonDetail);
			}
		}
	};
	
	private static Consumer<TediParserContext> INVOICE_VAT_BREAKDOWN = (ctx) -> {
		TediResult result = ctx.getTediResult();
		if ( result.getTedi().getTaxes() != null) {
			if (result.getInvoice().getBreakdown() == null) {
				result.getInvoice().setBreakdown( new LinkedList<InvoiceBreakdown>());
			}
			for ( TediInvoiceTax tediTax : result.getTedi().getTaxes()) {
				if (tediTax.getTaxType() == TediTaxType.IVA) {
					double base = result.getInvoice().isUndeductible()
							?AonMathUtils.round(AonNumberUtils.zeroIfNull(tediTax.getBase() + AonNumberUtils.zeroIfNull(tediTax.getQuota())))
							:AonNumberUtils.zeroIfNull(tediTax.getBase());
					double percent = result.getInvoice().isUndeductible()
							?0.0
							:AonNumberUtils.zeroIfNull(tediTax.getPercentage());
					double quota = result.getInvoice().isUndeductible()
							?0.0
							:AonNumberUtils.zeroIfNull(tediTax.getQuota());
					InvoiceBreakdown ib = new InvoiceBreakdown()
						.setTaxType(TaxType.VAT)
						.setBase( base )
						.setPercentage( percent )
						.setQuota( quota )
						.setSurcharge( result.getInvoice().isUndeductible()?0.0:AonNumberUtils.todouble( tediTax.getSurcharge()) )
						.setSurchargeQuota( result.getInvoice().isUndeductible()?0.0:AonNumberUtils.todouble( tediTax.getSurchargeQuota()));
					result.getInvoice().getBreakdown().add(ib);
				}
			}
		}
	};
	
	private static Consumer<TediParserContext> INVOICE_IRPF_BREAKDOWN = (ctx) -> {
		TediResult result = ctx.getTediResult();
		if (!result.getInvoice().isUndeductible()) {
			if ( result.getTedi().getTaxes() != null) {
				if (result.getInvoice().getBreakdown() == null) {
					result.getInvoice().setBreakdown( new LinkedList<InvoiceBreakdown>());
				}
				InvoiceBreakdown irpfTax = null;
				for ( TediInvoiceTax tediTax : result.getTedi().getTaxes()) {
					if (tediTax.getTaxType() == TediTaxType.IRPF) {
						double base = AonNumberUtils.zeroIfNull(tediTax.getBase());
						double percent = AonNumberUtils.zeroIfNull(tediTax.getPercentage());
						double quota = AonNumberUtils.zeroIfNull(tediTax.getQuota());
						irpfTax = new InvoiceBreakdown()
							.setTaxType(tediTax.getTaxType() == TediTaxType.IVA? TaxType.VAT : TaxType.RETENTION )
							.setBase( base )
							.setPercentage( percent )
							.setQuota( quota );
						result.getInvoice().getBreakdown().add(irpfTax);
						break; // TODO ¿¿¿En Tedi solo puede haber un IRPF???
					}
				}
				if (irpfTax != null && result.getInvoice().getDetails() != null) {
					for ( InvoiceDetail id : result.getInvoice().getDetails()) {
						double irpfQuota = AonMathUtils.round(id.getTaxableBase() * irpfTax.getPercentage() / 100);
						id.addInvoiceTax( 
							new InvoiceTax()
								.setTaxType( TaxType.RETENTION )
								.setBase( id.getTaxableBase() )
								.setPercentage( irpfTax.getPercentage() )
								.setQuota( irpfQuota )
								.setWithholdingType( WithholdingType.PROFESSIONAL )
								.setDeductiblePercent( 100.0 )
								.setDeductibleQuota( irpfQuota )
							); 
					}
				}
			}
		}
	};
	
	private static Consumer<TediParserContext> GENERATE_INVOICE_TAXES_IF_NEEDED = (ctx) -> {
		TediResult result = ctx.getTediResult();
		boolean hasDetails = (result.getInvoice().getDetails() != null && result.getInvoice().getDetails().size() > 0);
		if (!hasDetails && result.getInvoice().getBreakdown() != null) {
			InvoiceBreakdown irpfTax = null;
			for (InvoiceBreakdown ib : result.getInvoice().getBreakdown()) {
				if (ib.getTaxType() == TaxType.RETENTION) {
					irpfTax = ib; 		
				}
				if (ib.getTaxType() == TaxType.VAT) {
					InvoiceDetail id = new InvoiceDetail()
						.setSource(InvoiceSource.ACCOUNT)
						.setLine( (short) (1))
						.setDescription("AutoGenerated")
						.setQuantity(1.0)
						.setPrice(ib.getBase())
						.setDiscountExpression("0.0")
						.setTaxableBase(ib.getBase())
						;
					result.getInvoice().setDetails( new LinkedList<InvoiceDetail>());
					result.getInvoice().getDetails().add(id);
					id.addInvoiceTax( new InvoiceTax()
						.setTaxType( TaxType.VAT )
						.setBase( ib.getBase() )
						.setPercentage( ib.getPercentage() )
						.setQuota( ib.getQuota() )
						.setSurcharge( ib.getSurcharge() )
						.setSurchargeQuota( ib.getSurchargeQuota() )
						.setVatDeductionType( VatDeductionType.WITH_RIGHT )
						.setDeductiblePercent( 100.0 )
						.setDeductibleQuota( ib.getQuota() )
						);
				}
			}
			if (irpfTax != null && result.getInvoice().getDetails() != null) {
				result.getInvoice().getBreakdown().add(irpfTax);
				for ( InvoiceDetail id : result.getInvoice().getDetails()) {
					double irpfQuota = AonMathUtils.round(id.getTaxableBase() * irpfTax.getPercentage() / 100);
					id.addInvoiceTax( 
							new InvoiceTax()
								.setTaxType( TaxType.RETENTION )
								.setBase( id.getTaxableBase() )
								.setPercentage( irpfTax.getPercentage() )
								.setQuota( irpfQuota )
								.setWithholdingType( WithholdingType.PROFESSIONAL )
								.setDeductiblePercent( 100.0 )
								.setDeductibleQuota( irpfQuota )
							); 
				}
			}

		}
	};
	
	private static Consumer<TediParserContext> INVOICE_FINANCES = (ctx) -> {
		TediResult result = ctx.getTediResult();
		boolean hasFinances = (result.getTedi().getFinances() != null && result.getTedi().getFinances().size() > 0);
		if (hasFinances) {
			for ( int i = 0; i < result.getTedi().getFinances().size(); i++) {
				TediFinance tfin = result.getTedi().getFinances().get(i);
				Finance fin = new Finance();
				result.getAccountingInvoice().getInvoice().addFinance(fin);
				TediFinanceTransfer.toAon(ctx.getAonConfiguration(),result,tfin,fin);
			}
		}
		if (result.getTedi().isTicket()) {
			TediFinance tfin = new TediFinance()
					.setDueDate(result.getInvoice().getIssueDate())
					.setAmount( result.getInvoice().getTotal())
					.setPayMethod(TediPayMethod.CASH);
				Finance fin = new Finance();
				result.getAccountingInvoice().getInvoice().addFinance(fin);
				TediFinanceTransfer.toAon(ctx.getAonConfiguration(),result,tfin,fin);
		} else {
			AccountingInvoice ai = result.getAccountingInvoice();
			if (ai.getInvoice().getFinances() == null || ai.getInvoice().getFinances().size() == 0) {
				ai.setAuthFinanceCalculation(true);
				if (ctx.getAONContext() != null) {
					ai.getInvoice().setFinances( FinanceDAO.getFinancesForInvoice(ctx.getAONContext(), ai.getInvoice())); 
				}
				if (ai.getInvoice().getFinances() == null || ai.getInvoice().getFinances().size() == 0) {
					ai.getInvoice().addFinance(new Finance()
							.setDueDate(ai.getInvoice().getIssueDate())
							.setAmount(ai.getInvoice().getTotal())
							.setPayment(!ai.isSales())
							.setFinanceStatus(FinanceStatus.PENDING));
				}
			}
		}
	};

	private static Consumer<TediParserContext> INVOICE_TOTAL = (ctx) -> {
		TediResult result = ctx.getTediResult();
		double total = AonNumberUtils.todouble(result.getTedi().getTotal());
		result.getInvoice().setTotal( total );
		boolean hasDetails = result.getTedi().getDetails() != null && result.getTedi().getDetails().size() > 0; 
		boolean hasTaxes = result.getTedi().getTaxes() != null && result.getTedi().getTaxes().size() > 0;
		if ( !hasDetails && !hasTaxes ) {
			TediInvoiceDetail tediDetail = new TediInvoiceDetail()
					.setDescription("AutoGenerated")
					.setQuantity(1.0)
					.setPrice(total)
					.setDiscount(0.0)
					.setAmount(total)
					.setVat(null)
					.setSurcharge(null);
			InvoiceDetail aonDetail = new InvoiceDetail()
					.setLine( (short) (1));
			if (result.getInvoice().getDetails() == null) {
				result.getInvoice().setDetails( new LinkedList<InvoiceDetail>());
			}
			result.getInvoice().getDetails().add(aonDetail);
			toAccountingInvoiceDetail( ctx, tediDetail,aonDetail);
		}
	};

	public static TediResult toAccountingInvoice(AONContext ctx, AonConfiguration aonCtx, TediInvoice tedi) {
		AccountingInvoice ai = new AccountingInvoice();
		ai.setInvoice(new Invoice());
		TediResult result = new TediResult(tedi, ai);
		TediParserContext tctx = new  TediParserContext(ctx, aonCtx, result);
		
		ACCOUNTING_INVOICE_WORKPLACE
		.andThen(INVOICE_DOMAIN)
		.andThen(INVOICE_TYPE)
		.andThen(INVOICE_ISSUE_DATE)
		.andThen(INVOICE_TAX_DATE)
		.andThen(INVOICE_EMITIDA_REGISTRY)
		.andThen(INVOICE_RECIBIDA_REGISTRY)
		.andThen(INVOICE_TICKET_REGISTRY)
		.andThen(INVOICE_SERIES)
		.andThen(INVOICE_NUMBER)
		.andThen(INVOICE_REFERENCE_CODE)
		.andThen(INVOICE_ADDRESS)
		.andThen(INVOICE_DETAILS)
		.andThen(INVOICE_VAT_BREAKDOWN)
		.andThen(INVOICE_IRPF_BREAKDOWN)
		.andThen(GENERATE_INVOICE_TAXES_IF_NEEDED)
//		.andThen(INVOICE_TAXES)
		.andThen(INVOICE_TOTAL)
		.andThen(INVOICE_FINANCES)		
		.andThen(INVOICE_COMMENTS)
		.accept(tctx);
		
		fillVats(ctx, aonCtx, result);
		ai.setAccountEntry(getEntryBase(ctx,aonCtx,ai));
		if (result.isImportable()) {
			ai.setAccountEntry(InvoiceRecorder.getInvoiceEntry(ai));
		}
		// ----------
//		InvoiceCalculator.calculate(ai);
		// ----------
		TediValidator.validateInvoice(ctx,result);
		
		return result;
	}

	public static void toAccountingInvoiceDetail(TediParserContext ctx, TediInvoiceDetail tediDetail, InvoiceDetail aonDetail) {
		TediParserContextInvoiceDetail tctx = new  TediParserContextInvoiceDetail(ctx, tediDetail ,aonDetail);
		INVOICE_DETAIL_SOURCE
			.andThen(INVOICE_DETAIL_DESCRIPTION)
			.andThen(INVOICE_DETAIL_QUANTITY)
			.andThen(INVOICE_DETAIL_PRICE)
			.andThen(INVOICE_DETAIL_DISCOUNT)
			.andThen(INVOICE_DETAIL_TAXABLE_BASE)
			.andThen(INVOICE_DETAIL_VAT)
			.accept(tctx);
	}
}
 


/*	
private static Consumer<TediParserContext> INVOICE_TAXES = (ctx) -> {
	TediResult result = ctx.getTediResult();
	boolean hasDetails = (result.getInvoice().getDetails() != null && result.getInvoice().getDetails().size() > 0);
	if ( result.getTedi().getTaxes() != null) {
		InvoiceBreakdown irpfTax = null;
		if (result.getInvoice().getBreakdown() == null) {
			result.getInvoice().setBreakdown( new LinkedList<InvoiceBreakdown>());
		}
		for ( TediInvoiceTax tediTax : result.getTedi().getTaxes()) {
			
			double base = result.getInvoice().isUndeductible()
				?AonMathUtils.round(AonNumberUtils.zeroIfNull(tediTax.getBase() + AonNumberUtils.zeroIfNull(tediTax.getQuota())))
				:AonNumberUtils.zeroIfNull(tediTax.getBase());
			double percent = result.getInvoice().isUndeductible()
				?0.0
				:AonNumberUtils.zeroIfNull(tediTax.getPercentage());
			double quota = result.getInvoice().isUndeductible()
				?0.0
				:AonNumberUtils.zeroIfNull(tediTax.getQuota());
			InvoiceBreakdown ib = new InvoiceBreakdown()
					.setTaxType(tediTax.getTaxType() == TediTaxType.IVA? TaxType.VAT : TaxType.RETENTION )
					.setBase( base )
					.setPercentage( percent )
					.setQuota( quota )
					.setSurcharge( result.getInvoice().isUndeductible()?0.0:AonNumberUtils.todouble( tediTax.getSurcharge()) )
					.setSurchargeQuota( result.getInvoice().isUndeductible()?0.0:AonNumberUtils.todouble( tediTax.getSurchargeQuota()))
					;
			if (tediTax.getTaxType() == TediTaxType.IRPF) {
				irpfTax = ib;
			} else {
				result.getInvoice().getBreakdown().add(ib);
				
				if (!hasDetails) {
					InvoiceDetail id = new InvoiceDetail()
							.setSource(InvoiceSource.ACCOUNT)
							.setLine( (short) (1))
							.setDescription("AutoGenerated")
							.setQuantity(1.0)
							.setPrice(ib.getBase())
							.setDiscountExpression("0.0")
							.setTaxableBase(ib.getBase())
							;
					if (tediTax.getTaxType() == TediTaxType.IVA) {
						id.addInvoiceTax( 
								new InvoiceTax()
									.setTaxType( TaxType.VAT )
									.setBase( ib.getBase() )
									.setPercentage( ib.getPercentage() )
									.setQuota( ib.getQuota() )
									.setSurcharge( ib.getSurcharge() )
									.setSurchargeQuota( ib.getSurchargeQuota() )
									.setVatDeductionType( VatDeductionType.WITH_RIGHT )
									.setDeductiblePercent( 100.0 )
									.setDeductibleQuota( ib.getQuota() )
									); 
					}
					if (result.getInvoice().getDetails() == null) {
						result.getInvoice().setDetails( new LinkedList<InvoiceDetail>());
					}
					result.getInvoice().getDetails().add(id);
				}
			}
		}
		if (irpfTax != null && result.getInvoice().getDetails() != null) {
			result.getInvoice().getBreakdown().add(irpfTax);
			for ( InvoiceDetail id : result.getInvoice().getDetails()) {
				double irpfQuota = AonMathUtils.round(id.getTaxableBase() * irpfTax.getPercentage() / 100);
				id.addInvoiceTax( 
						new InvoiceTax()
							.setTaxType( TaxType.RETENTION )
							.setBase( id.getTaxableBase() )
							.setPercentage( irpfTax.getPercentage() )
							.setQuota( irpfQuota )
							.setWithholdingType( WithholdingType.PROFESSIONAL )
							.setDeductiblePercent( 100.0 )
							.setDeductibleQuota( irpfQuota )
							); 
			}
		}
	}
};
*/	
