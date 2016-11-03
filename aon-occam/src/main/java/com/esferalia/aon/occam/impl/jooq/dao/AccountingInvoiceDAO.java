package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntryFinanceTracking.ACCOUNT_ENTRY_FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.InvoiceTaxAccount.INVOICE_TAX_ACCOUNT;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.IAccountEntryTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceRecorder;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.IAccountingRegistryTypeVisitor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.FinanceTrackingType;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class AccountingInvoiceDAO {
	
	private static final com.esferalia.aon.jooq.tables.Account EXP_ACCOUNT = ACCOUNT.as("EXP_ACCOUNT");
	private static final com.esferalia.aon.jooq.tables.Account VAT_ACCOUNT = ACCOUNT.as("VAT_ACCOUNT");
	
	public static AccountingInvoice getAccountingInvoice(final AONContext ctx, final Integer accountEntry) {
		final AonConfiguration config = ConfigurationDAO.getConfiguration(ctx, null);
		Integer invoiceId = ctx.getDslContext()
			.select( ACCOUNT_ENTRY_INVOICE.INVOICE )
			.from( ACCOUNT_ENTRY_INVOICE )
			.where(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.eq(accountEntry))
			.and(ACCOUNT_ENTRY_INVOICE.DOMAIN.eq(ctx.getDomainId()))
			.fetch()
			.stream()
			.mapToInt(rec -> rec.getValue(ACCOUNT_ENTRY_INVOICE.INVOICE))
			.findFirst()
			.orElse( Integer.MIN_VALUE );
		if (invoiceId != null && invoiceId != Integer.MIN_VALUE) {
			Invoice invoice = InvoiceDAO.getInvoice(ctx, invoiceId);
			if (invoice != null) {
				final AccountingInvoice ai = new AccountingInvoice();
				ai.setAccountEntry(AccountEntryDAO.getAccountEntry(ctx, accountEntry));
				ai.setInvoice(invoice);
				AccountingRegistry reg =  RegistryDAO.getAccountingRegistries(ctx
						, filter -> filter.getIdProperty().eq(invoice.getRegistry()))
						.findFirst()
						.orElse(null);
				ai.setRegistry(reg);
				ctx.getDslContext()
					.select(
							INVOICE_DETAIL.ID,
							INVOICE_DETAIL.INVEST_ASSET, 
							INVOICE_DETAIL.PROJECT, 
							INVOICE_DETAIL.QUANTITY, 
							INVOICE_DETAIL.PRICE, 
							INVOICE_DETAIL.SOURCE, 
							INVOICE_DETAIL.TAXABLE_BASE,
							INVOICE_DETAIL.WORKPLACE,
							EXP_ACCOUNT.ID,
							EXP_ACCOUNT.CODE, 
							EXP_ACCOUNT.DESCRIPTION 
							) 
					.from( INVOICE_DETAIL )
					.join( INVOICE_DETAIL_ACCOUNT ).on(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
					.join( EXP_ACCOUNT ).on( INVOICE_DETAIL_ACCOUNT.ACCOUNT.eq(EXP_ACCOUNT.ID))
					.where(INVOICE_DETAIL.INVOICE.eq(invoice.getId()))
					.fetch()
					.stream()
					.forEach( det -> {
						final Integer invoideDetailId = det.getValue(INVOICE_DETAIL.ID);
						final LinkedList<InvoiceVAT> vats = new LinkedList<InvoiceVAT>();
						// TODO ¿Más de uno?
						ai.setWorkplace(det.getValue(INVOICE_DETAIL.WORKPLACE));
						// -----------------
						final InvoiceVAT vat = new InvoiceVAT();
						ctx.getDslContext()
						.select( 
								INVOICE_TAX.INVOICE_DETAIL,
								INVOICE_TAX.TAX_TYPE,
								INVOICE_TAX.BASE,
								INVOICE_TAX.PERCENTAGE,
								INVOICE_TAX.SURCHARGE,
								INVOICE_TAX.QUOTA,
								INVOICE_TAX.SURCHARGE_QUOTA,
								INVOICE_TAX.VAT_DEDUCTION_TYPE,
								INVOICE_TAX.WITHHOLDING_TYPE,
								INVOICE_TAX.DEDUCTIBLE_PERCENT,
								INVOICE_TAX.DEDUCTIBLE_QUOTA,
								VAT_ACCOUNT.ID,
								VAT_ACCOUNT.CODE, 
								VAT_ACCOUNT.DESCRIPTION 
								) 
						.from( INVOICE_TAX )
						.leftOuterJoin( INVOICE_TAX_ACCOUNT ).on(INVOICE_TAX_ACCOUNT.INVOICE_TAX.equal(INVOICE_TAX.ID))
						.leftOuterJoin( VAT_ACCOUNT ).on( INVOICE_TAX_ACCOUNT.ACCOUNT.eq(VAT_ACCOUNT.ID))
						.where(INVOICE_TAX.INVOICE_DETAIL.eq(invoideDetailId))
						.fetch()
						.stream()
						.forEach( tax -> {
							boolean withholding = tax.getValue(INVOICE_TAX.TAX_TYPE) == TaxType.RETENTION.ordinal();
							if (!withholding) {
								vats.add(vat);
									vat
									.setVatDeductionType(AonEnumUtils.enumValue(VatDeductionType.class,tax.getValue(INVOICE_TAX.VAT_DEDUCTION_TYPE)))
									.setBase(tax.getValue(INVOICE_TAX.BASE))
									.setPercentage(tax.getValue(INVOICE_TAX.PERCENTAGE))
									.setQuota(tax.getValue(INVOICE_TAX.QUOTA))
									.setSurcharge(tax.getValue(INVOICE_TAX.SURCHARGE))
									.setSurchargeQuota(tax.getValue(INVOICE_TAX.SURCHARGE_QUOTA))
									.setInvestAsset(det.getValue(INVOICE_DETAIL.INVEST_ASSET))
									.setDeductiblePercent(tax.getValue(INVOICE_TAX.DEDUCTIBLE_PERCENT))
									.setDeductibleQuota(tax.getValue(INVOICE_TAX.DEDUCTIBLE_QUOTA))
									.setExpAccountId(det.getValue(EXP_ACCOUNT.ID))
									.setExpAccountCode(det.getValue(EXP_ACCOUNT.CODE))
									.setExpAccountDescription(det.getValue(EXP_ACCOUNT.DESCRIPTION));
							}
							if (withholding) {
								vat.setWithholding(withholding);
								if (ai.getWithholdingData() == null) {
									ai.setWithholdingData( new InvoiceWithholding());
								}
								InvoiceWithholding wd = ai.getWithholdingData();
								wd.setBase(tax.getValue(INVOICE_TAX.BASE))
									.setPercentage(tax.getValue(INVOICE_TAX.PERCENTAGE))
									.setQuota(tax.getValue(INVOICE_TAX.QUOTA))
									.setWithholdingType(AonEnumUtils.enumValue(WithholdingType.class,tax.getValue(INVOICE_TAX.WITHHOLDING_TYPE)))
									.setAccountId(tax.getValue(VAT_ACCOUNT.ID))
									.setAccountCode(tax.getValue(VAT_ACCOUNT.CODE))
									.setAccountDescription(tax.getValue(VAT_ACCOUNT.DESCRIPTION))
									;
							}
							if (!withholding) {
								if (ai.isSales()) {
									vat.setOutputAccountId(tax.getValue(VAT_ACCOUNT.ID))
									.setOutputAccountCode(tax.getValue(VAT_ACCOUNT.CODE))
									.setOutputAccountDescription(tax.getValue(VAT_ACCOUNT.DESCRIPTION));
								}
								if (!ai.isSales()) {
									vat.setInputAccountId(tax.getValue(VAT_ACCOUNT.ID))
									.setInputAccountCode(tax.getValue(VAT_ACCOUNT.CODE))
									.setInputAccountDescription(tax.getValue(VAT_ACCOUNT.DESCRIPTION));
									if (ai.isOutputVatEnabled() && config.getDefaultChargedVatAccount() != null) {
										vat.setOutputAccountId(config.getDefaultChargedVatAccount().getId())
										.setOutputAccountCode(config.getDefaultChargedVatAccount().getCode())
										.setOutputAccountDescription(config.getDefaultChargedVatAccount().getDescription());
									}
								}
								if (vat.getInvestAsset() != null && config.getVatNegativeAdjustAccount() != null) {
									vat.setAdjAccountId(config.getVatNegativeAdjustAccount().getId())
									.setAdjAccountCode(config.getVatNegativeAdjustAccount().getCode())
									.setAdjAccountDescription(config.getVatNegativeAdjustAccount().getDescription());
								}
							}
						}
						);
						if (!vats.isEmpty()) {
							ai.addVat(vats.get(0));
						}
					}
					);
				return ai;
			}
		}
		return null;
	}
	
	public static AccountingInvoice initializeInvoice(final AONContext ctx, final AccountEntry entry, AccountingRegistry registry) {
		if (entry == null) {
			throw new AonCoreException("No se pudo inicializar, no hay apunte base");
		}
		if (registry == null) {
			throw new AonCoreException("No se pudo encontrar al titular de factura \"" + registry + "\"");
		}
		if (registry.getType() == null) {
			throw new AonCoreException("No se puede inicializar una factura sin tipo");
		}
		AccountingInvoice invoice = initializeInvoice(ctx, registry.getType().getInvoiceType(), registry.getId(), entry.getEntryDate());
		invoice.setAccountEntry(entry);
		return invoice;
	}
			
	
	public static AccountingInvoice initializeInvoice(final AONContext ctx, final InvoiceType type, final Integer registry,
			final Date issueDate) {
		AccountingRegistry reg =  RegistryDAO.getAccountingRegistries(ctx
					, filter -> filter.getIdProperty().eq(registry))
				.findFirst()
				.orElse(null);
		if (reg == null) {
			throw new AonCoreException("No se pudo encontrar al titular de factura \"" + registry + "\"");
		}
		if (reg.getType().getInvoiceType() != type) {
			throw new AonCoreException("No se puede inicializar una factura de " 
					+ type.getDescription() + ". El titular suministrado "
					+ "genera facturas de " 
					+ reg.getType().getInvoiceType().getDescription() );
		}
		final AonConfiguration config = ConfigurationDAO.getConfiguration(ctx, issueDate);
		AccountingInvoice ai = new AccountingInvoice()
				.setRegistry(reg)
				.setWorkplace(config.getWorkplaces().get(0).getId())
				.setPayDate(issueDate)
				.setInvoice(new Invoice()
					.setDomain(ctx.getDomainId())
					.setRegistry(registry)
					.setRecorded(false)
					.setRectificationType(RectificationType.NONE)
					.setConfidential(false)
					.setIssueDate(issueDate)
					.setTaxDate(issueDate)
					.setType(reg.getType().getInvoiceType())
					.setTransaction(reg.getTransaction())
					.setService( reg.getType().getInvoiceType() == InvoiceType.EXPENSES 
							  || reg.getType().getInvoiceType() == InvoiceType.UNDEDUCTIBLE)
					.setSeries(null)
					.setNumber(0)
					.setReferenceCode(null));
		reg.getType().visit(reg, new  InvoiceRegistryInitializer(ctx, ai.getInvoice(), config));
		ai.setSuggestedAccounts(getSuggestedAccounts(ctx,ai.getRegistry().getId()));
		ai.addVat(createNewInvoiceVAT(ai, config));

		/// RETENCIÓN
		if (ai.isWithholding()) {
			ai.setWithholdingData(new InvoiceWithholding());
			Account withholdingAccount = null;
			if (config.getDefaultWithholdingPercent() != null) {
				ai.getWithholdingData().setPercentage(config.getDefaultWithholdingPercent().getPercentage());
				ai.getWithholdingData().setWithholdingType(config.getDefaultWithholdingPercent().getWithholdingType());
				withholdingAccount = (ai.isSales())
						?config.getDefaultWithholdingPercent().getSalesAccount()
						:config.getDefaultWithholdingPercent().getPurchaseAccount();
			}
			if (withholdingAccount == null) {
				withholdingAccount = ai.isSales()
					?config.getDefaultChargedRetAccount()
					:config.getDefaultPaidRetAccount(); 
			}
			if (withholdingAccount != null) {
				ai.getWithholdingData().setAccountId(withholdingAccount.getId());
				ai.getWithholdingData().setAccountCode(withholdingAccount.getCode());
				ai.getWithholdingData().setAccountDescription(withholdingAccount.getDescription());
			}
		}
		return ai;
	}

	private static InvoiceVAT createNewInvoiceVAT(AccountingInvoice ai,AonConfiguration config) {
		InvoiceVAT vat = new InvoiceVAT();
		Account inputVatAccount = null;
		Account outputVatAccount = null;
		if (config.getDefaultVatPercent() != null) {
			vat.setPercentage(config.getDefaultVatPercent().getPercentage());
			if (ai.isSurcharge()) {
				vat.setSurcharge(config.getDefaultVatPercent().getSurcharge());	
			}
			inputVatAccount = config.getDefaultVatPercent().getPurchaseAccount();
			outputVatAccount = config.getDefaultVatPercent().getSalesAccount();
		}
		if (inputVatAccount == null) inputVatAccount = config.getDefaultPaidVatAccount();
		if (inputVatAccount != null) {
			vat.setInputAccountId(inputVatAccount.getId());
			vat.setInputAccountCode(inputVatAccount.getCode());
			vat.setInputAccountDescription(inputVatAccount.getDescription());
		}
		if (outputVatAccount== null) outputVatAccount = config.getDefaultChargedVatAccount();
		if (outputVatAccount != null) {
			vat.setOutputAccountId(outputVatAccount.getId());
			vat.setOutputAccountCode(outputVatAccount.getCode());
			vat.setOutputAccountDescription(outputVatAccount.getDescription());
		}
		if (config.getVatNegativeAdjustAccount() != null) {
			vat.setAdjAccountId( config.getVatNegativeAdjustAccount().getId());
			vat.setAdjAccountCode( config.getVatNegativeAdjustAccount().getCode());
			vat.setAdjAccountDescription( config.getVatNegativeAdjustAccount().getDescription());
		}
		if (ai.isSales() && config.getDefaultSalesAccount() != null) {
			vat.setExpAccountId(config.getDefaultSalesAccount().getId());
			vat.setExpAccountCode(config.getDefaultSalesAccount().getCode());
			vat.setExpAccountDescription(config.getDefaultSalesAccount().getDescription());
		}
		if (ai.isPurchase() && config.getDefaultPurchaseAccount() != null) {
			vat.setExpAccountId(config.getDefaultPurchaseAccount().getId());
			vat.setExpAccountCode(config.getDefaultPurchaseAccount().getCode());
			vat.setExpAccountDescription(config.getDefaultPurchaseAccount().getDescription());
		}
		vat.setWithholding(ai.isWithholding());
		return vat;
	}

	private static LinkedList<Account> getSuggestedAccounts(final AONContext ctx, Integer registry) {
		return ctx.getDslContext()
			.select( ACCOUNT.ID, ACCOUNT.CODE, ACCOUNT.DESCRIPTION)
			.from(INVOICE)
			.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
			.join(INVOICE_DETAIL_ACCOUNT).on(INVOICE_DETAIL.ID.eq(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL))
			.join(ACCOUNT).on(INVOICE_DETAIL_ACCOUNT.ACCOUNT.eq(ACCOUNT.ID))
			.where(INVOICE.REGISTRY.eq(registry))
			.and(INVOICE.DOMAIN.eq(ctx.getDomainId()))
			.orderBy(INVOICE.ISSUE_DATE.desc(), INVOICE.ID.asc() , INVOICE_DETAIL.LINE.asc())
			.limit(3)
			.fetch()
			.stream()
			.map( rec -> new Account()
						.setId(rec.getValue(ACCOUNT.ID))
						.setCode(rec.getValue(ACCOUNT.CODE))
						.setDescription(rec.getValue(ACCOUNT.DESCRIPTION))
				)
			.collect(Collectors.toCollection(LinkedList::new));	
	}
	

	private static class InvoiceRegistryInitializer implements IAccountingRegistryTypeVisitor {
		private AONContext ctx;
		private Invoice invoice;
		private AonConfiguration config;
		
		private InvoiceRegistryInitializer(AONContext ctx,Invoice invoice,AonConfiguration config) {
			this.ctx = ctx;
			this.invoice = invoice;
			this.config = config;
		}
		
		private void visitCommon(AccountingRegistry reg) {
			invoice.setScope(new Scope().setId( reg.getScope() ));
			invoice.setRegistryDocumentType(reg.getDocumentType());
			invoice.setRegistryDocumentCountry(reg.getDocumentCountry());
			invoice.setRegistryDocument(reg.getDocument());
			invoice.setRegistryName(reg.getName());
			invoice.setVatAccrualPayment(invoice.isNational()
					&& !invoice.getIssueDate().before(InvoiceDAO.VAT_ACCRUAL_START_DATE)
					&& ( config.getCompany().isVatAccrualPayment() || reg.isVatAccrualPayment()));
		}

		@Override
		public void visitCustomer(AccountingRegistry reg) {
			visitCommon(reg);
			invoice.setSurcharge(reg.isSurcharge());
			invoice.setWithholding(reg.isWithholding() && config.getCompany().isWithholding());
			invoice.setWithholdingFarmer(false);
			invoice.setSeries(config.getDefaultInvoiceSeries());
			invoice.setNumber( InvoiceDAO.getNextNumber(ctx, new Byte[]{invoice.getType().value()}, invoice.getSeries()));
		}

		@Override
		public void visitSupplier(AccountingRegistry reg) {
			invoice.setSurcharge(reg.isSurcharge() && config.getCompany().isSurcharge());
			invoice.setWithholding(reg.isWithholding());
			invoice.setWithholdingFarmer(reg.isWithholdingFarmer());
			visitCommon(reg);
		}

		@Override
		public void visitCreditor(AccountingRegistry reg) {
			invoice.setSurcharge(false);
			invoice.setWithholding(reg.isWithholding());
			invoice.setWithholdingFarmer(reg.isWithholdingFarmer());
			visitCommon(reg);
		}
	}


	public static AccountingInvoice save(final AONContext ctx, AonConfiguration config, final AccountingInvoice accInvoice) {
		if (accInvoice.getAccountEntry().getId() != null) {
			AccountEntryDAO.delete(ctx, accInvoice.getAccountEntry().getId());
			accInvoice.getAccountEntry().setId( null );
		}
		Invoice invoice  = accInvoice.getInvoice();
		invoice.setRecorded(true);
		checkRegistryAccount(ctx,accInvoice);
		generateDetails(ctx,config,accInvoice);
		InvoiceDAO.insert(ctx, config, invoice);
		insertAccountLinks(ctx, invoice);
		LinkedList<AccountEntry> entries = new LinkedList<AccountEntry>();
		AccountEntry[] array = InvoiceRecorder.recordInvoice(accInvoice);
		for (AccountEntry entry : array) {
			final Integer entryId = AccountEntryDAO.save(ctx, entry);
			entry.getEntryType().visit(entry, new IAccountEntryTypeVisitor() {
				@Override public void visitTax(AccountEntry entry) {}
				@Override public void visitStockVariation(AccountEntry entry) {}
				@Override public void visitSocialInsuranceAdjust(AccountEntry entry) {}
				@Override public void visitSocialInsurance(AccountEntry entry) {}
				@Override public void visitSalary(AccountEntry entry) {}
				@Override public void visitReturnedPayment(AccountEntry entry) {}
				@Override public void visitReturnedCollection(AccountEntry entry) {}
				
				@Override
				public void visitSalesInvoice(AccountEntry entry) {
					insertLink( ctx, invoice.getDomain(), entryId, invoice.getId());
				}

				@Override
				public void visitPurchaseInvoice(AccountEntry entry) {
					insertLink( ctx, invoice.getDomain(), entryId, invoice.getId() );
				}
				@Override 
				public void visitExpenseInvoice(AccountEntry entry) {
					insertLink( ctx, invoice.getDomain(), entryId, invoice.getId());
				}
				
				@Override
				public void visitPayment(AccountEntry entry) {
					saveFinance(ctx, invoice.getDomain(), entryId, invoice.getId(), accInvoice );
				}
				
				@Override public void visitOperating(AccountEntry entry) {}
				@Override public void visitOpening(AccountEntry entry) {}
				@Override public void visitManual(AccountEntry entry) {}
				@Override public void visitLoanFee(AccountEntry entry) {}
				@Override public void visitLoan(AccountEntry entry) {}
				@Override public void visitLeasingFee(AccountEntry entry) {}
				@Override public void visitLeasing(AccountEntry entry) {}
				@Override public void visitInvestmentInvoice(AccountEntry entry) {}
				@Override public void visitExpenses(AccountEntry entry) {}
				@Override public void visitCollection(AccountEntry entry) {}
				@Override public void visitClosing(AccountEntry entry) {}
				@Override public void visitAmortization(AccountEntry entry) {}
			});
			entries.add( AccountEntryDAO.getAccountEntry(ctx, entryId) );
		}
		accInvoice.setAccountEntries(entries);
		return accInvoice;
	}

	private static void saveFinance(AONContext ctx, int domain, Integer entryId, Integer invoiceId, AccountingInvoice accInvoice) {
		Invoice invoice = accInvoice.getInvoice();
		Integer financeId = FinanceDAO.insert(ctx, new Finance()
				.setDomain(ctx.getDomainId())
				.setInvoice(new Invoice().setId(invoiceId))
				.setPayment(!invoice.isSales())
				.setPayMethod(new PayMethod().setId(accInvoice.getPayMethod()))
				.setRegistry(new Registry().setId(invoice.getRegistry()))
				.setRegistryDocument(invoice.getRegistryDocument())
				.setRegistryDocumentType(invoice.getRegistryDocumentType())
				.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry())
				.setRegistryName(invoice.getRegistryName())
				.setScope(invoice.getScope())
				.setAmount(invoice.getTotal())
				.setDueDate(accInvoice.getPayDate())
				.setFinanceStatus(FinanceStatus.PAID)
				.setSecurityLevel(invoice.getSecurityLevel())
				.setConcept(invoice.getDocumentNumber())
			);
		Integer financTrackingId = ctx.getDslContext().insertInto(FINANCE_TRACKING)
			.set(FINANCE_TRACKING.DOMAIN,ctx.getDomainId())
			.set(FINANCE_TRACKING.FINANCE, financeId )
			.set(FINANCE_TRACKING.TRACKING_DATE, AonDateUtils.toSql(accInvoice.getPayDate()))
			.set(FINANCE_TRACKING.TYPE,FinanceTrackingType.PAID.value())
			.set(FINANCE_TRACKING.AMOUNT, invoice.getTotal())
			.set(FINANCE_TRACKING.RECORDED, AonEnumUtils.getByte(true))
			.set(FINANCE_TRACKING.DESCRIPTION, "Asiento: " + entryId)
			.set(FINANCE_TRACKING.CREATION_USER,ctx.getUser())
			.set(FINANCE_TRACKING.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.returning(FINANCE_TRACKING.ID)
			.fetchOne()
			.getValue(FINANCE_TRACKING.ID);
		ctx.getDslContext().insertInto(ACCOUNT_ENTRY_FINANCE_TRACKING)
			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.DOMAIN,ctx.getDomainId())
			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY, entryId )
			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING, financTrackingId )
			.execute();
	}
	
	private static void checkRegistryAccount(final AONContext ctx, AccountingInvoice accInvoice) {
		if (accInvoice.getRegistry().getAccountId() == null) {
			accInvoice.getRegistry().getType().visit(accInvoice.getRegistry(),  new IAccountingRegistryTypeVisitor() {
				@Override
				public void visitSupplier(AccountingRegistry reg) {
					Account account = createAccountAndFill(reg);
					RegistryDAO.updateSupplierAccount(ctx,reg.getId(),account.getId());
				}
				
				@Override
				public void visitCustomer(AccountingRegistry reg) {
					Account account = createAccountAndFill(reg);
					RegistryDAO.updateCustomerAccount(ctx,reg.getId(),account.getId());
				}
				
				@Override
				public void visitCreditor(AccountingRegistry reg) {
					Account account = createAccountAndFill(reg);
					RegistryDAO.updateCreditorAccount(ctx,reg.getId(),account.getId());
				}
				
				private Account createAccountAndFill(AccountingRegistry reg) {
					String code = AccountDAO.getNextAccountCode(ctx, reg.getType().getAccountPrefix());
					Account account = new Account()
						.setDomain(ctx.getDomainId())
						.setCode(code)
						.setDescription(accInvoice.getRegistry().getName())
						.setAlias(accInvoice.getRegistry().getAlias())
						.setActive(true)
						;
					account = AccountDAO.insert(ctx, account);
					reg.setAccountId(account.getId());
					reg.setAccountCode(account.getCode());
					reg.setAccountDescription(account.getDescription());		
					return account;
					
				}
			});
		}
	}

	private static void insertAccountLinks(AONContext ctx, Invoice invoice) {
		for (InvoiceDetail detail : invoice.getDetails() ) {
			if (detail.getAccount() == null) 
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_NO_EXP_ACCOUNT.getMessage());
			ctx.getDslContext().insertInto(INVOICE_DETAIL_ACCOUNT)
				.set(INVOICE_DETAIL_ACCOUNT.DOMAIN, detail.getDomain())
				.set(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL, detail.getId())
				.set(INVOICE_DETAIL_ACCOUNT.ACCOUNT, detail.getAccount())
				.execute();
			for (InvoiceTax tax : detail.getInvoiceTaxes() ) {
				if (tax.getAccount() == null) 
					throw new AonCoreException(AonError.ACCOUNT_ENTRY_NO_TAX_ACCOUNT.getMessage());
				ctx.getDslContext().insertInto(INVOICE_TAX_ACCOUNT)
					.set(INVOICE_TAX_ACCOUNT.DOMAIN,detail.getDomain())
					.set(INVOICE_TAX_ACCOUNT.INVOICE_TAX, tax.getId())
					.set(INVOICE_TAX_ACCOUNT.ACCOUNT, tax.getAccount())
					.execute();
			}
		}
	}

	private static void generateDetails(AONContext ctx, AonConfiguration config, AccountingInvoice accInvoice) {
		short line = 1;
		LinkedList<InvoiceDetail> details = new LinkedList<InvoiceDetail>();
		for (InvoiceVAT vat :  accInvoice.getVats()) {
			InvoiceDetail detail = new InvoiceDetail()
					.setDomain(accInvoice.getInvoice().getDomain())
					.setInvoice(accInvoice.getInvoice())
					.setInvestAsset(vat.getInvestAsset())
					.setWorkPlace( accInvoice.getWorkplace())
					.setLine(line)
					.setDescription(null)
					.setQuantity(1)
					.setPrice(vat.getBase())
					.setDiscountExpression("0.0")
					.setSource(InvoiceSource.ACCOUNT)
					.setTaxableBase(vat.getBase())
					.setAccount(vat.getExpAccountId())
					.addInvoiceTax(new InvoiceTax()
						.setTaxType(TaxType.VAT)
						.setBase(vat.getBase())
						.setPercentage(vat.getPercentage())
						.setQuota(vat.getQuota())
						.setSurcharge(vat.getSurcharge())
						.setSurchargeQuota(vat.getSurchargeQuota())
						.setVatDeductionType(vat.getVatDeductionType())
						.setDeductiblePercent(vat.getDeductiblePercent())
						.setDeductibleQuota(vat.getDeductibleQuota())
						// TODO Se deben grabar las dos cuentas!!
						.setAccount(accInvoice.isSales() ? vat.getOutputAccountId() : vat.getInputAccountId() )
						// --------------------------------------
					);
					if (vat.isWithholding() && accInvoice.isWithholding()) {
						detail.addInvoiceTax(new InvoiceTax()
							.setTaxType(TaxType.RETENTION)
							.setBase(vat.getBase())
							.setPercentage(accInvoice.getWithholdingData().getPercentage())
							.setQuota(accInvoice.getWithholdingData().getQuota())
							.setWithholdingType(accInvoice.getWithholdingData().getWithholdingType())
							.setAccount(accInvoice.getWithholdingData().getAccountId()));
					};
			details.add( detail );
			line++;
		}
		accInvoice.getInvoice().setDetails(details);
	}

	private static void insertLink(AONContext ctx, Integer domain, Integer entryId, Integer invoiceId) {
		ctx.getDslContext().insertInto(ACCOUNT_ENTRY_INVOICE)
			.set(ACCOUNT_ENTRY_INVOICE.DOMAIN,domain)
			.set(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY, entryId)
			.set(ACCOUNT_ENTRY_INVOICE.INVOICE, invoiceId)
			.execute();
	}
}
