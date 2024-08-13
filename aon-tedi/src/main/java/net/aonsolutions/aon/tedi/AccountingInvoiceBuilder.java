package net.aonsolutions.aon.tedi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorMessages;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
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
import com.esferalia.aon.occam.impl.jooq.dao.AccountingRegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PayMethodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.InvoiceRecorder;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.InvoiceRegistryInitializer;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import es.translogia.tedi.ewok.TediAddress;
import es.translogia.tedi.ewok.TediComments;
import es.translogia.tedi.ewok.TediFinance;
import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceDetail;
import es.translogia.tedi.ewok.TediInvoiceTax;
import es.translogia.tedi.ewok.TediNif;
import es.translogia.tedi.ewok.TediPayMethod;
import es.translogia.tedi.ewok.TediRegistry;
import es.translogia.tedi.ewok.TediTaxType;
import net.aonsolutions.aon.tedi.visitors.InvoiceTypeVisitor;

public class AccountingInvoiceBuilder {
	
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
		AMOUNT( (aonCtx,result,tedi,aon) -> {
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
					boolean hasPaymethods = aonCtx.getPayMethods() != null && aonCtx.getPayMethods().size() > 0;
					if (hasPaymethods) {
						for ( PayMethod paymethod : aonCtx.getPayMethods() ) {
							if ( temp == paymethod.getType() ) {
								aon.setPayMethod(paymethod.getId());								
								aon.setPayMethodName(paymethod.getName());
								if ( temp == PayMethodType.CASH_BASIS ) {
									Account cashAccount = aonCtx.accounting().getDefaultCashAccount();
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
			double total = AonNumberUtils.todouble(result.getTedi().getTotal());
			result.getInvoice().setTotal( total );
			boolean hasDetails = result.getTedi().getDetails() != null && result.getTedi().getDetails().size() > 0; 
			boolean hasTaxes = result.getTedi().getTaxes() != null && result.getTedi().getTaxes().size() > 0;
			if ( !hasDetails && !hasTaxes ) {
				TediInvoiceDetail tid = new TediInvoiceDetail()
						.setDescription("AutoGenerated")
						.setQuantity(1.0)
						.setPrice(total)
						.setDiscount(0.0)
						.setAmount(total)
						.setVat(null)
						.setSurcharge(null);
				InvoiceDetail id = new InvoiceDetail()
					.setSource(InvoiceSource.ACCOUNT)
					.setLine( (short) (1));
				if (result.getInvoice().getDetails() == null) {
					result.getInvoice().setDetails( new LinkedList<InvoiceDetail>());
				}
				result.getInvoice().getDetails().add(id);
				TediInvoiceDetailTransfer.toAon(result,tid,id);
			}
		}),
		DOMAIN ( (ctx, aonCtx,result) -> {
			Invoice invoice = result.getInvoice();
			invoice.setDomain(ctx.getDomainId());
		}),		
		REGISTRY ( (ctx, aonCtx,result) -> AccountingInvoiceBuilder.fillRegistry(ctx, aonCtx,result)),
		SERIES	( (ctx, aonCtx,result) -> {
			if (result.getTedi().isEmitida()) {
				result.getInvoice().setSeries(result.getTedi().getSeries());
			}
		}), 
		NUMBER	( (ctx, aonCtx,result) -> {
			if (result.getTedi().isEmitida()) {
				if (result.getTedi().getNumber() != null) {
					result.getInvoice().setNumber(result.getTedi().getNumber());
				} else {
					result.add( InvoiceErrorMessages.C003.inf(InvoiceErrorKey.NUMBER, InvoiceErrorKey.NUMBER.getDescription(), 0) );
					result.getInvoice().setNumber(0);
				}
			}
		}), 
		REFERENCE_CODE( (ctx, aonCtx,result) -> {
			result.getInvoice().setReferenceCode(result.getTedi().getReference());
			if (result.getTedi().isTicket() && AonStringUtils.isBlank(result.getTedi().getReference())) {
				result.getInvoice().setReferenceCode("<auto>"); 
			}
		}), 
		COMMENTS( (ctx, aonCtx,result) -> {
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
			}
		),
		ADDRESS( (ctx, aonCtx,result) -> {
			if (result.getTedi().getRegistry() != null && result.getTedi().getRegistry().getAddress() != null) {
				result.getInvoice().setAddress(new RegistryAddress()
					.setAddress(result.getTedi().getRegistry().getAddress().getAddress())
					.setCity(result.getTedi().getRegistry().getAddress().getCity())
					.setZip(result.getTedi().getRegistry().getAddress().getPostalCode())
					.setProvince(result.getTedi().getRegistry().getAddress().getProvince())
					.setCountry(Country.safeValueOf(result.getTedi().getRegistry().getAddress().getCountry())));
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
				InvoiceBreakdown irpfTax = null;
				if (result.getInvoice().getBreakdown() == null) {
					result.getInvoice().setBreakdown( new LinkedList<InvoiceBreakdown>());
				}
				for ( int i = 0; i < result.getTedi().getTaxes().size(); i++) {
					TediInvoiceTax tit = result.getTedi().getTaxes().get(i);
					double quota = result.getInvoice().isUndeductible()
						?0.0
						:AonNumberUtils.todouble(tit.getQuota());
					double base = result.getInvoice().isUndeductible()
						?AonMathUtils.round(AonNumberUtils.zeroIfNull(tit.getBase()) + quota)
						:AonNumberUtils.zeroIfNull(tit.getBase());
					double percent = result.getInvoice().isUndeductible()
						?0.0
						:AonNumberUtils.zeroIfNull(tit.getPercentage());
					InvoiceBreakdown ib = new InvoiceBreakdown()
							.setTaxType(tit.getTaxType() == TediTaxType.IVA? TaxType.VAT : TaxType.RETENTION )
							.setBase( base )
							.setPercentage( percent )
							.setQuota( quota )
							.setSurcharge( result.getInvoice().isUndeductible()?0.0:AonNumberUtils.todouble( tit.getSurcharge()) )
							.setSurchargeQuota( result.getInvoice().isUndeductible()?0.0:AonNumberUtils.todouble( tit.getSurchargeQuota()))
							;
					if (tit.getTaxType() == TediTaxType.IRPF) {
						irpfTax = ib;
					} else {
						result.getInvoice().getBreakdown().add(ib);
						
						if (!hasDetails) {
							InvoiceDetail id = new InvoiceDetail()
									.setSource(InvoiceSource.ACCOUNT)
									.setLine( (short) (1 + i))
									.setDescription("AutoGenerated")
									.setQuantity(1.0)
									.setPrice(ib.getBase())
									.setDiscountExpression("0.0")
									.setTaxableBase(ib.getBase())
									;
							if (tit.getTaxType() == TediTaxType.IVA) {
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
		}),
		FINANCE( (ctx, aonCtx,result) -> {
			boolean hasFinances = (result.getTedi().getFinances() != null && result.getTedi().getFinances().size() > 0);
			if (hasFinances) {
				for ( int i = 0; i < result.getTedi().getFinances().size(); i++) {
					TediFinance tfin = result.getTedi().getFinances().get(i);
					Finance fin = new Finance();
					result.getAccountingInvoice().getInvoice().addFinance(fin);
					TediFinanceTransfer.toAon(aonCtx,result,tfin,fin);
				}
			} else if (result.getTedi().isTicket()) {
				TediFinance tfin = new TediFinance()
					.setDueDate(result.getInvoice().getIssueDate())
					.setAmount( result.getInvoice().getTotal())
					.setPayMethod(TediPayMethod.CASH);
				Finance fin = new Finance();
				result.getAccountingInvoice().getInvoice().addFinance(fin);
				TediFinanceTransfer.toAon(aonCtx,result,tfin,fin);
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
		
		checkSenderAndReceiver(aonCtx, tedi);
		setSender(aonCtx, tedi);
		setReceiver(aonCtx, tedi);
		// TODO
		ai.setWorkplace(aonCtx.getWorkplaces().get(0).getId());
		// ----
		TediResult result = new TediResult(tedi, ai);
		aonCtx.setPayMethods(PayMethodDAO.getOrderByIds(ctx));
		TediInvoiceTransfer.toAon(ctx, aonCtx,result);
		fillVats(ctx, aonCtx, result);
		if (ai.getInvoice().getFinances() == null || ai.getInvoice().getFinances().size() == 0) {
			ai.getInvoice().addFinance(new Finance()
					.setDueDate(ai.getInvoice().getIssueDate())
					.setAmount(ai.getInvoice().getTotal())
					.setPayment(!ai.isSales())
					.setFinanceStatus(FinanceStatus.PENDING));
		}
		ai.setAccountEntry(getEntryBase(ctx,aonCtx,ai));
		if (result.isImportable()) {
			ai.setAccountEntry(InvoiceRecorder.getInvoiceEntry(ctx, ai.getInvoice()));
		}
		// ----------
		InvoiceCalculator.calculate(ai.getInvoice());
		// ----------
		TediValidator.validateInvoice(ctx,result);
		return result; 
	}
	
	private static void setReceiver(AonConfiguration aonCtx, TediInvoice tedi) {
		if ( tedi.getReceiver() != null )
			return ;
		if ( tedi.getInsight() == null )
			return;
		if ( tedi.getInsight().getNifs() == null )
			return;
		if ( tedi.getInsight().getNifs().length == 0 )
			return;
		
		Company company = aonCtx.getCompany();
		TediNif[] nifs = tedi.getInsight().getNifs();
		
		if ( nifs.length == 1  ) {
			tedi.setReceiver(newRegistry(company));
			tedi.setCompany(company.getName());
			return;
		}
		
		for (TediNif nif : nifs) {
			if ( AonStringUtils.equalsIgnoreCase(nif.getStr(), company.getDocument()) ) {
				tedi.setReceiver(newRegistry(company));
				tedi.setCompany(company.getName());
			}
		}
		
		
	}

	private static void setSender(AonConfiguration aonCtx, TediInvoice tedi) {
		if ( tedi.getSender() != null )
			return ;
		if ( tedi.getInsight() == null )
			return;
		if ( tedi.getInsight().getNifs() == null )
			return;
		if ( tedi.getInsight().getNifs().length == 0 )
			return;
		
		Company company = aonCtx.getCompany();
		TediNif[] nifs = tedi.getInsight().getNifs();
		
		if ( nifs.length == 1  ) {
			tedi.setSender(newRegistry(nifs[0].getStr()));
			return;
		}
		
		for (TediNif nif : nifs) {
			if ( !AonStringUtils.equalsIgnoreCase(nif.getStr(), company.getDocument()) ) {
				tedi.setSender(newRegistry(nif.getStr()));
			}
		}
		
		
	}

	private static void checkSenderAndReceiver(AonConfiguration aonCtx, TediInvoice tedi) {
		Company company = aonCtx.getCompany();
		
		List<TediRegistry> registries =  new ArrayList<TediRegistry>();
		if ( tedi.getSender() != null )
			registries.add( tedi.getSender());
		if ( tedi.getReceiver() != null )
			registries.add(tedi.getReceiver());
		
		tedi.setSender(null);
		tedi.setReceiver(null);
		
		registries.stream()
		.filter(r -> AonStringUtils.equalsIgnoreCase(r.getDocument(), company.getDocument()))
		.findFirst().ifPresent(r -> tedi.setReceiver(r));
		;
		
		registries.stream()
		.filter(r -> !AonStringUtils.equalsIgnoreCase(r.getDocument(), company.getDocument()))
		.findFirst().ifPresent(r -> tedi.setSender(r));
		;
		
	}

	private static TediRegistry newRegistry(String document) {
		TediRegistry registry = new TediRegistry();
		registry.setDocument(document);
		registry.setDocumentCountry(Country.ES.getIso2());
		return registry;
	}
	
	private static TediRegistry newRegistry(Company company) {
		TediRegistry registry = new TediRegistry();
		registry.setName(company.getName());
		registry.setDocument(company.getDocument());
		if ( company.getDocumentCountry() != null )
			registry.setDocumentCountry(company.getDocumentCountry().getIso2());
		else 
			registry.setDocumentCountry(Country.ES.getIso2());
		
		if ( company.getMainAddress() == null ) 
			return registry;
		
		TediAddress address = new TediAddress();
		address.setCity(company.getMainAddress().getCity());
		address.setProvince(company.getMainAddress().getGeozoneName());
		address.setAddress(company.getMainAddress().getFullAddress());
		address.setPostalCode(company.getMainAddress().getZip());
		registry.setAddress(address);
		
		if ( address.getCountry() == null )
			return registry;
		
		address.setCountry(company.getMainAddress().getCountry().getIso2());		
		
		return registry;
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
				.setComments(ai.getInvoice().getComments())
				.setDirty(false);
		ai.getInvoice().getType().visit(ai.getInvoice(),  new IInvoiceTypeVisitor<Void>() {
			@Override 
			public Void visitUndeductible(Invoice invoice) {
				accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);
				return null;
			}
			@Override 
			public Void visitSales(Invoice invoice) {
				accountEntry.setEntryType(AccountEntryType.SALES_INVOICE);
				return null;
			}
			@Override 
			public Void visitPurchase(Invoice invoice) {
				accountEntry.setEntryType(AccountEntryType.PURCHASE_INVOICE);
				return null;
			}
			@Override 
			public Void visitExpenses(Invoice invoice) {
				accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);
				return null;
			}
		});
		return accountEntry;
	}
	

	private static void fillVats(AONContext ctx, AonConfiguration aonCtx, TediResult result) {
		AccountingInvoice ai = result.getAccountingInvoice();
		Invoice invoice = result.getInvoice();

		boolean withholding = false;
		if (invoice.getBreakdown() != null) {
			for (InvoiceBreakdown ib : invoice.getBreakdown() ) {
				if (ib.getTaxType() == TaxType.RETENTION) {
					Account retentionAccount =(invoice.isSales() )
						?aonCtx.accounting().getDefaultPaidRetAccount()
						:aonCtx.accounting().getDefaultChargedRetAccount();
					withholding = true;
					invoice.setWithholding(true);
					InvoiceWithholding iw = new InvoiceWithholding()
						.setWithholdingType( WithholdingType.PROFESSIONAL )
						.setBase( ib.getBase() )
						.setPercentage( ib.getPercentage() )
						.setQuota( ib.getQuota() )
						.setAccount( retentionAccount )
					;
					ai.setWithholdingData(iw);
					break;
				}
			}
		}
		
		Account outputAccount = aonCtx.accounting().getDefaultChargedVatAccount();
		Account inputAccount = aonCtx.accounting().getDefaultPaidVatAccount();
		Account adjAccount = aonCtx.accounting().getVatNegativeAdjustAccount();
		Account adjDirectTaxAccount = aonCtx.accounting().getDirectTaxAdjustAccount();
		
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
				detail.setExpAccount(expAccount);
			}
		}
	}
	
	public static void fillRegistry(AONContext ctx, AonConfiguration aonCtx, TediResult result) {
		for ( IRegistryFiller filler : FILLERS ) {
			boolean accepted = filler.accept(result);
			if (accepted) {
				filler.fill(ctx, aonCtx, result);
				break;
			}
		}
	}

	private static interface IRegistryFiller {
		boolean accept(TediResult result);
		void fill(AONContext ctx, AonConfiguration aonCtx, TediResult result);	
	}
	private static IRegistryFiller[] FILLERS = new IRegistryFiller[] {new EmitidaFiller(),new RecibidaFiller(),new TicketFiller() };

	private static abstract class RegistryFiller implements IRegistryFiller {
		
		protected boolean fillRegistry(AONContext ctx, AonConfiguration aonCtx, TediResult result,Predicate<AccountingRegistry> filterExpression) {
			Invoice invoice = result.getInvoice();
			if (result.getTedi().getRegistry() != null) {
				invoice.setRegistryDocument(result.getTedi().getRegistry().getDocument());
				invoice.setRegistryName(result.getTedi().getRegistry().getName());
				invoice.setRegistryDocumentCountry(Country.safeValueOf(result.getTedi().getRegistry().getDocumentCountry()));
			}
			LinkedList<AccountingRegistry> registries = AccountingRegistryDAO
					.getAccountingRegistries(ctx, f -> f.getDocumentProperty().eq(invoice.getRegistryDocument()))
					.filter(filterExpression)
					.collect(Collectors.toCollection(LinkedList::new));
			if (registries != null && registries.size() > 0) {
				if (registries.size() == 1) {
					AccountingRegistry ar = registries.get(0);
					AccountingInvoice ai = result.getAccountingInvoice();
					ai.setRegistry(ar);
					ai.setSuggestedAccounts(AccountingInvoiceDAO.getSuggestedAccounts(ctx, ar.getId(), ar.getType().getInvoiceType()));
					invoice.setRegistry(ar.getId())
						.setTransaction(ar.getTransaction());
					ar.getType().visit(new InvoiceRegistryInitializer(ctx, ai.getInvoice(), aonCtx, ar));
					return true;
				} else {
					result.setPosibleRegistries(registries);
					result.add( InvoiceErrorMessages.C011.err(InvoiceErrorKey.AMBIGUOUS_REGISTRY));
				}
			}
			if (result.getInvoice().getRegistryDocumentCountry() == null) {
				result.getInvoice().setRegistryDocumentCountry(Country.ES);
				result.add( InvoiceErrorMessages.C003.inf(InvoiceErrorKey.RDOCUMENT_COUNTRY,InvoiceErrorKey.RDOCUMENT_COUNTRY.getDescription(),Country.ES.getIso2()));
			}
			return false;
		}		
	}
	
	private static class EmitidaFiller extends RegistryFiller {
		@Override
		public boolean accept(TediResult result) {
			return result.getTedi().isEmitida();
		}

		@Override
		public void fill(AONContext ctx, AonConfiguration aonCtx, TediResult result) {
			fillRegistry(ctx, aonCtx, result, ar -> result.getInvoice().isSales() && ar.getType() == AccountingRegistryType.CUSTOMER);
		}
	}

	private static class RecibidaFiller extends RegistryFiller {
		@Override
		public boolean accept(TediResult result) {
			return result.getTedi().isRecibida();
		}

		@Override
		public void fill(AONContext ctx, AonConfiguration aonCtx, TediResult result) {
			boolean filled = fillRegistry(ctx, aonCtx, result, ar -> !result.getInvoice().isSales() 
				&& (ar.getType() == AccountingRegistryType.SUPPLIER || ar.getType() == AccountingRegistryType.CREDITOR));
			if (filled) {
				AccountingRegistry ar = result.getAccountingInvoice().getRegistry();
				if (ar.getType() == AccountingRegistryType.CREDITOR) {
					if (result.getInvoice().getType() == InvoiceType.PURCHASE) {
						result.getInvoice().setType( InvoiceType.EXPENSES );
						result.add( InvoiceErrorMessages.C003.inf(InvoiceErrorKey.TYPE,InvoiceErrorKey.TYPE.getDescription(),InvoiceType.EXPENSES.getDescription()));
					}
				} else  if (ar.getType() == AccountingRegistryType.SUPPLIER) {
					if (result.getInvoice().getType() != InvoiceType.PURCHASE) {
						result.getInvoice().setType( InvoiceType.PURCHASE );
						result.add( InvoiceErrorMessages.C003.inf(InvoiceErrorKey.TYPE,InvoiceErrorKey.TYPE.getDescription(),InvoiceType.PURCHASE.getDescription()));
					}
				}
			}
		}
		
	}
	private static class TicketFiller extends RegistryFiller {
		@Override
		public boolean accept(TediResult result) {
			return result.getTedi().isTicket();
		}

		@Override
		public void fill(AONContext ctx, AonConfiguration aonCtx, TediResult result) {
			if (!fillRegistry(ctx, aonCtx, result, ar -> !result.getInvoice().isSales() && ar.getType() == AccountingRegistryType.CREDITOR)){
				if (aonCtx.getDefaultCreditor() != null) {
					AccountingRegistry ar = aonCtx.getDefaultCreditor();
					AccountingInvoice ai = result.getAccountingInvoice();
					Invoice invoice = result.getInvoice();
					TediInvoice tedi = result.getTedi();
					ai.setRegistry(ar);
					ai.setSuggestedAccounts(AccountingInvoiceDAO.getSuggestedAccounts(ctx, ar.getId(), ar.getType().getInvoiceType()));
					invoice
						.setRegistry(ar.getId())
						.setTransaction(ar.getTransaction());
					ar.getType().visit(new InvoiceRegistryInitializer(ctx, ai.getInvoice(), aonCtx, ar));
					if (tedi.getSender() != null) {
						invoice.setRegistryDocument(tedi.getSender().getDocument());
						invoice.setRegistryName(tedi.getSender().getName());
					}
				}
			};
		}
	}

	private static Account getSalesAccount(AONContext ctx, AonConfiguration aonCtx, TediResult result) {
		return aonCtx.accounting().getDefaultSalesAccount();
	}
	
	private static Account getPurchaseAccount(AONContext ctx, AonConfiguration aonCtx, TediResult result) {
		Account purchaseAccount = aonCtx.accounting().getDefaultPurchaseAccount(); 
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
				expAccount = AccountDAO.getAccounts(ctx, filter -> filter.getCodeProperty().like("629%") )
						.findFirst()
						.orElse(null);
			}
		}
		return expAccount;
	}

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	public static AccountingInvoice build(TediInvoice invoice) {
		AccountingInvoice ai = new AccountingInvoice();
		return ai;
	}
	
}
 