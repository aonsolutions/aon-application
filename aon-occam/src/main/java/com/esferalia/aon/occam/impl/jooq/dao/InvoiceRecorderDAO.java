package com.esferalia.aon.occam.impl.jooq.dao;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;

public class InvoiceRecorderDAO {
	
	private InvoiceRecorderDAO() {
		
	}
	
	public static Stream<Invoice> getUnrecordedInvoices(AONContext ctx, InvoiceFilter filter) {
		return InvoiceDAO.getInvoiceStream(ctx, filter)
			.filter( Invoice::isRecorded )
			.map( i -> InvoiceDAO.getFullInvoice(ctx, i.getId()) )
			.map( i -> fillRecorderMessages(ctx, i) );
	}
	
	public static AccountEntry getEntryBase(AONContext ctx, AonConfiguration aonCtx, Invoice invoice) {
		EnterpriseActivity ea = !invoice.getActivity().isEmpty() ? invoice.getActivity() : aonCtx.getMainActivity();
		Integer activity = (ea==null?null:ea.getId());
		Integer periodId = null;
		if (invoice.getIssueDate() != null) {
			AccountPeriod period = ACCOUNTING.ensurePeriod(ctx, ctx.getDomainId(), invoice.getIssueDate());
			periodId = (period == null? null : period.getId());
		}
		AccountEntry accountEntry = new AccountEntry()
				.setPeriod(periodId)
				.setDomain(invoice.getDomain())
				.setConfidential(false)
				.setEntryDate(invoice.getIssueDate())
				.setActivity(activity)
				.setComments(invoice.getComments())
				.setDirty(false);
		invoice.getType().visit(invoice,  new IInvoiceTypeVisitor<Void>() {
			@Override 
			public Void visitUndeductible(Invoice invoice) {
				accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);
				accountEntry.setUndeductible(true);
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
				accountEntry.setUndeductible(false);
				return null;
			}
		});
		return accountEntry;
	}

	// ************************************************************************************** 	
	// ************************************************************************************** 	
	// ************************************************************************************** 	
	// ************************************************************************************** 	
	// ************************************************************************************** 	
	// ************************************************************************************** 	
	// ************************************************************************************** 	
	// ************************************************************************************** 	
	// ************************************************************************************** 	
	// ************************************************************************************** 	
	// ************************************************************************************** 	
	// ************************************************************************************** 	
	// ************************************************************************************** 	
	private static Invoice fillRecorderMessages(AONContext ctx, Invoice inv) {
		return inv;
	}
	/*
	private static Invoice fillRecorderMessages(AONContext ctx, Invoice inv) {
		inv.clearMessages();
		inv.setRecordable(true);
		if (!inv.isRecorded()) {
				checkFinanceInaccuracyPresent(ctx, inv);
				checkInvestmentAmortizationFormPresent();
				InvoiceType type = getInvoice().getType();
	
				if ( getInvoice().isWithholding()) {
					addMessage("Factura con retenciones I.R.P.F.");
				}
				if ( getInvoice().isSurcharge()) {
					addMessage("Factura con Recargo de Equivalencia.");
				}
				if ( getInvoice().getTransaction() != InvoiceTransactionType.NATIONAL) {
					Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
					addMessage("Factura de tipo " + getInvoice().getTransaction().getName(locale));
				}
				if ( getInvoice().isInvestment() ) {
					addMessage("Factura marcada como inversión.");
				}
				if (!isDateEquals()) {
					addMessage("Fecha de IVA diferente a fecha de factura.");
				}
				
				if (type == InvoiceType.SALES) {
					setAccount( getAccountBridgeUtil().getCustomerAccount(getInvoice().getRegistry()));	
				} else if (type == InvoiceType.PURCHASE) {
					setAccount( getAccountBridgeUtil().getSupplierAccount(getInvoice().getRegistry()));	
				} else if (type == InvoiceType.EXPENSES) {
					setAccount( getAccountBridgeUtil().getCreditorAccount(getInvoice().getRegistry()));	
				}
				if (getAccount() != null &&  (invoice.getType() == InvoiceType.EXPENSES || invoice.getType() == InvoiceType.UNDEDUCTIBLE)) {
					checkExpenseAccount();
				}
			} catch (ManagerBeanException ex) {
				addMessage("Error en el chequeo. " +  ex.getMessage());
			}
		}
		setRefresh(false);
	}
	
	private void checkExpenseAccount() throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), getInvoice().getId());
		List<ITransferObject> list = invoiceDetailBean.getList(criteria);
		boolean wrong = false;
		for (ITransferObject to: list) {
			InvoiceDetail invoiceDetail = (InvoiceDetail) to;
			if (invoiceDetail.getItem() != null) {
				Account expenseAccount = invoiceDetail.getItem().getProduct().getPurchaseAccount();
				if (invoiceDetail.getItem().getProduct().getType() == ProductType.EXPENSE) {
					if (expenseAccount == null || expenseAccount.getId() == null) {
						wrong = true;
						addMessage("El gasto: \"" + invoiceDetail.getDescription() + "\" no tiene cuenta contable asociada.");			
					} else {
						Connection connection = null; 
						try {
//							IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
							connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
							DSLContext ctx = DSL.using(connection, AccountingUtil.getDefaultSettings());
							AggregateFunction<Integer> countFunc = DSL.countDistinct(ACCOUNT_ENTRY_DETAIL.ID);
							Result<Record4<Integer,String,String,Integer>> r = 
								ctx.select(ACCOUNT_ENTRY_DETAIL.ACCOUNT,ACCOUNT.CODE,ACCOUNT.DESCRIPTION,countFunc)
									.from(ACCOUNT_ENTRY_DETAIL)
									.innerJoin(ACCOUNT).on(ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT))
									.innerJoin(DOMAIN).on(DOMAIN.ID.eq(ACCOUNT_ENTRY_DETAIL.DOMAIN))
									.where(ACCOUNT_ENTRY_DETAIL.DOMAIN.equal(DomainManager.getCurrentDomain()))
									.and(ACCOUNT.DOMAIN.in(DOMAIN.ID, DOMAIN.PARENT))
									.and(ACCOUNT.CODE.like(getAccount().getCode()))
									.groupBy(ACCOUNT_ENTRY_DETAIL.ACCOUNT)
									.orderBy(countFunc.desc())
									.fetch();
							Account first = null;
							boolean used = false;
							for (Record4<Integer,String,String,Integer> step : r) {
								Integer id = step.getValue(ACCOUNT_ENTRY_DETAIL.ACCOUNT);
								String code = step.getValue(ACCOUNT.CODE);
								if (first == null && "6".startsWith(code)) {
									first = new Account();
									first.setId(id);
									first.setCode(code);
									first.setDescription(step.getValue(ACCOUNT.DESCRIPTION));
								}
								if (id.equals(expenseAccount.getId()) ) {
									used = true;
									break;
								}
							}
							if (!used) {
								String msg = "Este acreedor nunca ha registrado una factura de gasto \"" + invoiceDetail.getDescription() + "\""; 
								if (first != null) {
									msg += " y su cuenta de gastos más utilizada es \"" + first.getFullDescription() +"\".";
								}
								addMessage(msg);
							}
						} catch (AonConnectionException e) {
							throw new ManagerBeanException(e.getMessage(), e);
						} finally {
							DatabaseUtil.closeQuietly(connection);
						}
					}
					
				}
			}
		}
		if (wrong) {
			setRecordable(false);
		}
	}

	private void checkFinanceInaccuracyPresent(AONContext ctx, Invoice inv) {
		double total = inv.getTotal();
		double financeTotal = AonMathUtils.round(AonCollectionUtils.stream(inv.getFinances()).mapToDouble( f -> f.getAmount()).sum());
		boolean ok = !inv.isRecorded() && (financeTotal == 0 || total == financeTotal);
		if (!ok) {
			inv.setRecordable(false);
			inv.addMessage(TediErrorMessages.C200.err(TediContextKey.FINANCE_TOTAL_AMOUNT));
		}
		if (financeTotal == 0) {
			addMessage("Factura sin Vencimientos.");			
		}
	}
	
	private void checkInvestmentAmortizationFormPresent() throws ManagerBeanException {
		if (getInvoice().isInvestment() && !hasAmortizationLinked()) {
			setRecordable(false);
			addFinanceNoAmortizationForm();
		}
	}
	
	
*/	
}
