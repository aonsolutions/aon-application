package com.esferalia.aon.occam.impl.jooq.dao.accounting;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntryFinanceTracking.ACCOUNT_ENTRY_FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceDua.INVOICE_DUA;
import static com.esferalia.aon.jooq.tables.InvoiceTaxAccount.INVOICE_TAX_ACCOUNT;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.AggregateFunction;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountingDUAInfo;
import com.esferalia.aon.occam.api.model.AccountingDUAInvoice;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType.AccountingRegistryTypeVisitor;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.FinanceTrackingType;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingRegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.OCRDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TbaiConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountingInvoiceDAO {
	
	private static final com.esferalia.aon.jooq.tables.Account DUT_ACCOUNT = ACCOUNT.as("DUT_ACCOUNT");
	private static final com.esferalia.aon.jooq.tables.Account VAT_ACCOUNT = ACCOUNT.as("VAT_ACCOUNT");

	private AccountingInvoiceDAO() {
		
	}
	
	public static Optional<AccountingInvoice> getFromAccountEntry(final AONContext ctx, final Integer accountEntryId) {
		return ctx.getDslContext()
			.select( ACCOUNT_ENTRY_INVOICE.INVOICE )
			.from( ACCOUNT_ENTRY_INVOICE )
			.where(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.eq(accountEntryId))
			.fetch()
			.stream()
			.map(rec -> rec.getValue(ACCOUNT_ENTRY_INVOICE.INVOICE))
			.findFirst()
			.map( invoiceId -> InvoiceDAO.getFull(ctx, invoiceId)
				.orElseThrow(() -> new AonCoreException("No se pudo encontrar la factura")))
			.map( invoice -> new AccountingInvoice().setInvoice(invoice))
			.map( ai -> ai.fillAccountEntry(AccountEntryDAO.getAccountEntry(ctx, accountEntryId)))
			.map( ai -> fillAccountingInvoice(ctx, ai) )
		;
	}
	
	public static Optional<AccountingInvoice> getFromInvoice(final AONContext ctx, final Integer invoiceId) {
		return getFromInvoice(ctx, InvoiceDAO.getFull(ctx, invoiceId)
				.orElseThrow(() -> new AonCoreException("No se pudo encontrar la factura")));
	}
	
	public static Optional<AccountingInvoice> getFromInvoice(final AONContext ctx, final Invoice invoice) {
		if (invoice == null) throw new IllegalArgumentException("Invoice can not be null");
		AccountingInvoice ai = new AccountingInvoice().setInvoice(invoice);
		AccountEntry ae = null;
		if (invoice.getId() == null) {
			ae = InvoiceRecorder.getInvoiceEntry(ctx, ai.getInvoice()); 
		} else {
			ae = ctx.getDslContext()
				.select( ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY)
				.from( ACCOUNT_ENTRY_INVOICE )
				.where(ACCOUNT_ENTRY_INVOICE.INVOICE.eq(invoice.getId()))
				.fetch()
				.stream()
				.map(rec -> rec.getValue(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY))
				.findFirst()
				.map(accountEntryId -> AccountEntryDAO.getAccountEntry(ctx, accountEntryId))
				.orElse(null) 
				;
		}
		ai.setAccountEntry(ae);
		fillAccountingInvoice(ctx, ai);
		return Optional.of(ai); 
	}
	

	public static Optional<Account> getInvoiceDetailAccount(AONContext ctx, Integer invoiceDetailId) {
		return ctx.getDslContext()
				.select()
				.from(ACCOUNT)
				.join(INVOICE_DETAIL_ACCOUNT).on(INVOICE_DETAIL_ACCOUNT.ACCOUNT.eq(ACCOUNT.ID))
				.where(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.eq(invoiceDetailId)).limit(1)
				.fetch()
				.stream()
				.map(new FullAccountFiller())
				.findFirst();
	}
	
	public static void saveInvoiceDetailAccount(AONContext ctx, InvoiceDetail invoiceDetail) {
		ctx.getDslContext()
			.delete(INVOICE_DETAIL_ACCOUNT)
			.where(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.eq(invoiceDetail.getId()))
			.and(INVOICE_DETAIL_ACCOUNT.DOMAIN.eq(invoiceDetail.getDomain()))
			.execute();
		ctx.log().debug("\tDELETE INVOICE_DETAIL_ACCOUNT");
		ctx.getDslContext().insertInto(INVOICE_DETAIL_ACCOUNT)
			.set(INVOICE_DETAIL_ACCOUNT.DOMAIN, invoiceDetail.getDomain())
			.set(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL, invoiceDetail.getId())
			.set(INVOICE_DETAIL_ACCOUNT.ACCOUNT, invoiceDetail.getExpAccount().getId())
			.execute();
		ctx.log().debug("\tINSERT INVOICE_DETAIL_ACCOUNT");
	}

	public static void saveInvoiceTaxAccount(AONContext ctx, Invoice invoice, InvoiceDetail invoiceDetail, InvoiceTax invoiceTax) {
		if (invoiceDetail.isTaxEnabled(invoice) ) {
			Integer accountId = Optional.ofNullable( invoice.isSales()?invoiceTax.getOutputAccount():invoiceTax.getInputAccount() )
					.map( Account::getId )
					.orElse( invoiceDetail.getExpAccount().getId() );
			ctx.getDslContext().insertInto(INVOICE_TAX_ACCOUNT)
				.set(INVOICE_TAX_ACCOUNT.DOMAIN,invoiceDetail.getDomain())
				.set(INVOICE_TAX_ACCOUNT.INVOICE_TAX, invoiceDetail.getId())
				.set(INVOICE_TAX_ACCOUNT.ACCOUNT, accountId )
			.execute();
			ctx.log().debug("\t\tINSERT INVOICE_TAX_ACCOUNT");
		} else {
			ctx.log().debug("\t\tSKIPPING INVOICE TAX ACCOUNT CREATION ({0})",
					(invoiceDetail.isPrepayment()?"PREPAYMENT":"UNDEDUCTIBLE INVOICE"));
		}
	}
	
	
	public static Optional<Account> getInvoiceTaxAccount(AONContext ctx, Integer invoiceTaxId) {
		return ctx.getDslContext()
				.select()
				.from(ACCOUNT)
				.join(INVOICE_TAX_ACCOUNT).on(INVOICE_TAX_ACCOUNT.ACCOUNT.eq(ACCOUNT.ID))
				.where(INVOICE_TAX_ACCOUNT.INVOICE_TAX.eq(invoiceTaxId)).limit(1)
				.fetch()
				.stream()
				.map(new FullAccountFiller())
				.findFirst();
	}

	public static AccountingInvoice save(final AONContext ctx, final AccountingInvoice accInvoice) {
		checkRegistryAccount(ctx,accInvoice);
		if (accInvoice.getAccountEntry().getId() == null) {
			accInvoice.setAccountEntries( insert(ctx,accInvoice) );	
		} else {
			accInvoice.setAccountEntries( update(ctx,accInvoice) );
		}
		return accInvoice;
	}
	
	public static AccountingInvoice initializeInvoice(final AONContext ctx, final InvoiceType type, final Integer registry) {
		return initializeInvoice(ctx, type, registry, new Date());
	}
	
	public static AccountingInvoice initializeInvoice(final AONContext ctx , final InvoiceType type, final Integer registry, final Date issueDate) {
		return initializeInvoice(ctx
			, type
			, registry
			, Optional.ofNullable( ctx.getConfiguration().getMainActivity() ).map(a -> a.getId()).orElse(null)
			, new Date());
	}

	public static AccountingInvoice initializeInvoice(final AONContext ctx, final InvoiceType type, final Integer registry, final Integer activity, final Date issueDate) {
		AccountingRegistry reg =  
			AccountingRegistryDAO.getAccountingRegistries(ctx, filter -> filter.getIdProperty().eq(registry))
				.filter(f -> AccountingRegistryType.getFor(type).equals(f.getType()))
				.findFirst()
				.orElseThrow(() -> new AonCoreException(
						MessageFormat.format("No se pudo encontrar al titular de factura \"{0}\"", registry)));
		return initializeInvoice(ctx, type, reg, activity, issueDate);
	}
	
	public static AccountingInvoice initializeInvoice(final AONContext ctx, final InvoiceType type, final AccountingRegistry reg) {
		return initializeInvoice(ctx, type, reg, new Date());
	}
	
	public static AccountingInvoice initializeInvoice(final AONContext ctx , final InvoiceType type, final AccountingRegistry reg, final Date issueDate) {
		return initializeInvoice(ctx
			, type
			, reg
			, Optional.ofNullable( ctx.getConfiguration().getMainActivity() ).map(a -> a.getId()).orElse(null)
			, issueDate);
	}
	
	public static AccountingInvoice initializeInvoice(final AONContext ctx, final InvoiceType type, AccountingRegistry reg, final Integer activity, final Date issueDate) {
		checkTBAIForSales( ctx, type);	
		AccountingInvoice ai = new AccountingInvoice()
			.setRegistry(reg)
			.setWorkplace(ctx.getConfiguration().getFirstWorkplace().map(w -> w.getId()).orElse(null))
			.setAuthFinanceCalculation(true)
			.setInvoice( InvoiceDAO.initialize(ctx, ctx.getDomainId(), type, reg.getId(), issueDate,activity))
		;
		ai.setSuggestedAccounts(getSuggestedAccounts(ctx , ai.getInvoice().getDomain(), ai.getRegistry().getId(), reg.getType().getInvoiceType()));
		InvoiceDetail id = createNewInvoiceDetail(ctx ,ai);
		ai.getInvoice().addDetail(id);
		return ai;
	}

	private static InvoiceDetail createNewInvoiceDetail(final AONContext ctx,AccountingInvoice ai) {
		InvoiceDetail detail = new InvoiceDetail()
			.setSource(InvoiceSource.ACCOUNT);
		
		if (ctx.getConfiguration().getDefaultVatPercent() != null) {
			if (ai.isSales() && !ai.isNational()) {
				detail.ensureVatTax().setPercentage(0.0);
			} else {
				detail.ensureVatTax().setPercentage(ctx.getConfiguration().getDefaultVatPercent().getPercentage());
			}
			if (ai.isSurcharge()) {
				detail.ensureVatTax().setSurcharge(ctx.getConfiguration().getDefaultVatPercent().getSurcharge());	
			}
			detail.ensureVatTax().setInputAccount( ctx.getConfiguration().getDefaultVatPercent().getPurchaseAccount());
			detail.ensureVatTax().setOutputAccount( ctx.getConfiguration().getDefaultVatPercent().getSalesAccount());
		}
		if (detail.ensureVatTax().getInputAccount() == null) {
			detail.ensureVatTax().setInputAccount( ctx.getConfiguration().accounting().getDefaultPaidVatAccount());
		}
		if (detail.ensureVatTax().getOutputAccount() == null) {
			detail.ensureVatTax().setOutputAccount( ctx.getConfiguration().accounting().getDefaultChargedVatAccount()) ;
		}
		if (ctx.getConfiguration().accounting().getVatNegativeAdjustAccount() != null) {
			detail.ensureVatTax().setAdjAccount( ctx.getConfiguration().accounting().getVatNegativeAdjustAccount());
		}
		if (ctx.getConfiguration().accounting().getDirectTaxAdjustAccount() != null) {
			detail.ensureVatTax().setAdjDirectTaxAccount( ctx.getConfiguration().accounting().getDirectTaxAdjustAccount());
		}
		if (ai.isSales()) {
			detail.setExpAccount(ctx.getConfiguration().accounting().getDefaultSalesAccount());
		}
		if (ai.isPurchase()) {
			detail.setExpAccount(ctx.getConfiguration().accounting().getDefaultPurchaseAccount());
		}
		if ( ai.isWithholding() ) {
			detail.addWithholdingTax(ai.getInvoice());
		}
		return detail;
	}

	private static Stream<Account> getSuggestedAccountsStream(AONContext ctx, int domain, Integer registry, InvoiceType type) {
		AggregateFunction<Integer> count = DSL.count(ACCOUNT.ID);
		return ctx.getDslContext().select(ACCOUNT.ID, ACCOUNT.CODE, ACCOUNT.DESCRIPTION, count)
			.from(INVOICE)
			.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
			.join(INVOICE_DETAIL_ACCOUNT).on(INVOICE_DETAIL.ID.eq(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL))
			.join(ACCOUNT).on(INVOICE_DETAIL_ACCOUNT.ACCOUNT.eq(ACCOUNT.ID))
			.where(INVOICE.DOMAIN.eq(domain))
				.and(INVOICE.REGISTRY.eq(registry))
				.and(INVOICE.TYPE.eq(type.value()))
			.groupBy(ACCOUNT.ID)
			.orderBy(count.desc())
			.fetch().stream().map(r -> new Account()
				.setId(r.getValue(ACCOUNT.ID))
				.setCode(r.getValue(ACCOUNT.CODE))
				.setDescription(r.getValue(ACCOUNT.DESCRIPTION)));
	}

	public static LinkedList<Account> getSuggestedAccounts(AONContext ctx, int domain, Integer registry, InvoiceType type) {
		return getSuggestedAccountsStream(ctx, domain, registry, type)
			.collect(Collectors.toCollection(LinkedList::new));	
	}
	
	// *************************************************************
	// ************************************************* [PRIVATE] *
	// *************************************************************
	
	private static AccountingInvoice fillAccountingInvoice(AONContext ctx, AccountingInvoice ai) {
		Invoice invoice = ai.getInvoice();
		
		if (AonCollectionUtils.isNotEmpty(ai.getAccountEntry().getDetails())) {
			String concept = ai.getAccountEntry().getDetails().get(0).getConcept();
			ai.setManualConcept(AonStringUtils.substringBetween(concept,"[","]"));
		}
		
		AccountingRegistry reg = AccountingRegistryDAO.getAccountingRegistries(ctx
			,f -> f.getIdProperty().eq(invoice.getRegistry()))
			.filter(f -> AccountingRegistryType.getFor(invoice.getType()).equals(f.getType()))
			.findFirst()
			.orElse(null);
		ai.setRegistry(reg);
		
		
		if (ai.getInvoice().isDUAAllowed()) {
			fillDUAInfo(ctx, ai);
		}
		
		if (ai.getInvoice().isDUALinkAllowed()) {
			ai.setDuaNationalInvoice( 
				ctx.getDslContext()
					.select(INVOICE_DUA.INVOICE_NATIONAL)
					.from(INVOICE_DUA)
					.where(INVOICE_DUA.INVOICE_IMPORT.equal(ai.getInvoice().getId()))
					.and(INVOICE_DUA.DOMAIN.eq(invoice.getDomain()))
					.fetch()
					.stream()
					.map( rec -> rec.getValue(INVOICE_DUA.INVOICE_NATIONAL))				
					.findFirst()
					.orElse(null)
			);
		}
		
		if (!ai.isUndeductible()) {
			AonCollectionUtils.stream(ai.getInvoice().getDetails())
				.filter(d -> !d.isPrepayment())
				.forEach(d -> fillInvoiceTax(ctx, ai, d));
		}
		return ai;
	}

	private static void fillDUAInfo(AONContext ctx, AccountingInvoice ai) {
		AccountingDUAInvoice accountingDUAInvoice = ctx.getDslContext()
			.select()
			.from(INVOICE_DUA)
			.leftOuterJoin(DUT_ACCOUNT).on(DUT_ACCOUNT.ID.eq(INVOICE_DUA.DUTY_ACCOUNT))
			.leftOuterJoin(VAT_ACCOUNT).on(VAT_ACCOUNT.ID.eq(INVOICE_DUA.VAT_ACCOUNT))
			.where(INVOICE_DUA.INVOICE_NATIONAL.eq(ai.getInvoice().getId()))
			.and(INVOICE_DUA.DOMAIN.eq(ai.getInvoice().getDomain()))
			.fetch()
			.stream()
			.map( rec ->  new AccountingDUAInvoice()
				.setInfo( new AccountingDUAInfo()
					.setId(rec.getValue(INVOICE_DUA.ID))
					.setDomain(rec.getValue(INVOICE_DUA.DOMAIN))
					.setCode(rec.getValue(INVOICE_DUA.CODE))
					.setPrice(rec.getValue(INVOICE_DUA.PRICE))
					.setAdjust(rec.getValue(INVOICE_DUA.ADJUST))
					.setStatisticalValue(rec.getValue(INVOICE_DUA.STATISTICAL_VALUE))
					.setDutyAccount(new Account()
						.setId(rec.getValue(DUT_ACCOUNT.ID))
						.setDomain(rec.getValue(DUT_ACCOUNT.DOMAIN))
						.setCode(rec.getValue(DUT_ACCOUNT.CODE))
						.setDescription(rec.getValue(DUT_ACCOUNT.DESCRIPTION))
						.setAlias(rec.getValue(DUT_ACCOUNT.ALIAS))
						.setEntryEnabled( AonEnumUtils.getBoolean(rec.getValue(DUT_ACCOUNT.ENTRYENABLED)))
						.setLevel(rec.getValue(DUT_ACCOUNT.LEVEL))
						.setActive(AonEnumUtils.getBoolean(rec.getValue(DUT_ACCOUNT.ACTIVE)))
						.setCostCenter(rec.getValue(DUT_ACCOUNT.COST_CENTER))
					)
					.setDutyBase(rec.getValue(INVOICE_DUA.DUTY_BASE))
					.setDutyPercent(rec.getValue(INVOICE_DUA.DUTY_PERCENT))
					.setDutyTotal(rec.getValue(INVOICE_DUA.DUTY_TOTAL))
					.setVatAccount(new Account()
						.setId(rec.getValue(VAT_ACCOUNT.ID))
						.setDomain(rec.getValue(VAT_ACCOUNT.DOMAIN))
						.setCode(rec.getValue(VAT_ACCOUNT.CODE))
						.setDescription(rec.getValue(VAT_ACCOUNT.DESCRIPTION))
						.setAlias(rec.getValue(VAT_ACCOUNT.ALIAS))
						.setEntryEnabled( AonEnumUtils.getBoolean(rec.getValue(VAT_ACCOUNT.ENTRYENABLED)))
						.setLevel(rec.getValue(VAT_ACCOUNT.LEVEL))
						.setActive(AonEnumUtils.getBoolean(rec.getValue(VAT_ACCOUNT.ACTIVE)))
						.setCostCenter(rec.getValue(VAT_ACCOUNT.COST_CENTER))
					)
				)
				.setAccountingInvoice( getFromInvoice(ctx, rec.getValue(INVOICE_DUA.INVOICE_IMPORT)).orElse(null) )
			)
			.findFirst()
			.orElse(null);
		if (accountingDUAInvoice != null ) {
			ai.setDuaLinked(true);
			if (accountingDUAInvoice.getAccountingInvoice() != null) {
				LinkedList<InvoiceDetail> duaDetails = new LinkedList<>();
				for (InvoiceDetail ori : accountingDUAInvoice.getAccountingInvoice().getInvoice().getDetails()) {
					InvoiceDetail vat = ori.copy();
					vat.setAutoGenerated(true);
					duaDetails.add(vat);
				}
				accountingDUAInvoice.getInfo().setDuaDetails(duaDetails);
			}
			ai.setDuaInvoice(accountingDUAInvoice);
			
			accountingDUAInvoice.getInfo().setAuthCalcEnabled(false);
		}
	}

	private static void checkTBAIForSales( final AONContext ctx, final InvoiceType type ) {
		TbaiConfiguration tbaiConfig = TbaiConfigurationDAO.get(ctx);
		if (tbaiConfig.isActive() && type == InvoiceType.SALES) {
			throw new AonCoreException("No se pueden crear facturas emitidas en entornos con TicketBai activado");
		}
	}
	
	private static void fillInvoiceTax(AONContext ctx, AccountingInvoice ai, InvoiceDetail invoiceDetail) {
		// *********************
		// Al no guardar el porcentaje de imposición directa en BD, se "supone" su activación en función
		// de la existencia de la cuenta en apuntes.
		// Si la cuenta ha cambiad, el apunte fallará....
		// Si el porcentaje de invest_asset ha cambiado, el apunte fallará-
		boolean directTaxEnabledPre = false;
		Account directTaxAccount = ctx.getConfiguration().accounting().getDirectTaxAdjustAccount();
		if (directTaxAccount != null && ai.getAccountEntry() != null) {
			directTaxEnabledPre = AonCollectionUtils.stream(ai.getAccountEntry().getDetails())
				.anyMatch( aed -> AonNumberUtils.equals(aed.getAccount(),directTaxAccount.getId()));
		}
		final boolean directTaxEnabled = directTaxEnabledPre;
		// *********************
		
		InvoiceTax vat = invoiceDetail.ensureVatTax();
		Account vatAccount = getInvoiceTaxAccount(ctx, invoiceDetail.getVatTax().get().getId()).orElse(null);
		
		Integer investAsset = invoiceDetail.getInvestAsset().map(ia -> ia.getId()).orElse(null);

		Double  directTaxPercent = Double.valueOf(0);
		if (directTaxEnabled && investAsset != null) {
			directTaxPercent = AonCollectionUtils.stream( ctx.getConfiguration().getInvestAssets() )
				.filter( ia -> AonNumberUtils.equals(ia.getId(),investAsset))
				.map( ia -> ia.getRetentionPercent())
				.findFirst()
				.orElse(Double.valueOf(0));
		}
		vat.setDirectTaxPercent(directTaxPercent);
		
		if (ai.isSales()) {
			vat.setOutputAccount(vatAccount);
		}
		if (!ai.isSales()) {
			vat.setInputAccount(vatAccount);
			if (ai.isOutputVatEnabled() && ctx.getConfiguration().accounting().getDefaultChargedVatAccount() != null) {
				vat.setOutputAccount(ctx.getConfiguration().accounting().getDefaultChargedVatAccount());
			}
			if (investAsset != null && ctx.getConfiguration().accounting().getVatNegativeAdjustAccount() != null) {
				vat.setAdjAccount(ctx.getConfiguration().accounting().getVatNegativeAdjustAccount());
			}
			if (investAsset != null && directTaxEnabled && ctx.getConfiguration().accounting().getDirectTaxAdjustAccount() != null) {
				vat.setAdjDirectTaxAccount(ctx.getConfiguration().accounting().getDirectTaxAdjustAccount());
			}
		}
	}
	
	private static void checkRegistryAccount(final AONContext ctx, AccountingInvoice accInvoice) {
		if (accInvoice.getRegistry().getAccountId() == null) {
			accInvoice.getRegistry().getType().visit(new AccountingRegistryTypeVisitor() {
				@Override
				public void visitSupplier() {
					Account account = createAccountAndFill(accInvoice.getRegistry());
					SupplierDAO.updateSupplierAccount(ctx, accInvoice.getRegistry().getId(),account.getId());
				}
				
				@Override
				public void visitCustomer() {
					Account account = createAccountAndFill(accInvoice.getRegistry());
					CustomerDAO.updateCustomerAccount(ctx, accInvoice.getRegistry().getId(),account.getId());
				}
				
				@Override
				public void visitCreditor() {
					Account account = createAccountAndFill(accInvoice.getRegistry());
					CreditorDAO.updateCreditorAccount(ctx, accInvoice.getRegistry().getId(),account.getId());
				}
				
				@Override
				public void visitUndedCreditor() {
					visitCreditor();
				}
				
				private Account createAccountAndFill(AccountingRegistry reg) {
					String code = AccountDAO.getNextAccountCode(ctx, reg.getType().getAccountPrefix());
					Account account = new Account()
						.setDomain(accInvoice.getInvoice().getDomain())
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
	
	private static LinkedList<AccountEntry> insert(final AONContext ctx, final AccountingInvoice accInvoice) {
		try {
			ctx.log().debug("------ [START] INSERT INVOICE");
			LinkedList<AccountEntry> entries = new LinkedList<>();
			InvoiceDAO.save(ctx, accInvoice.getInvoice());
			
//			if (accInvoice.isDuaLinked()) {
//				insertInvoiceDUA( ctx, config, accInvoice);
//			}
			
			recordInvoice( ctx, accInvoice, entries);
			
			if ( accInvoice.isTediParsed() ) {
				try {
					new Thread( () -> {
							ctx.log().info("OPENING Thread");		
							OCRDAO.teachReferenceCode(ctx.getUser(), accInvoice.getInvoice().getRegistryDocument(), accInvoice.getInvoice().getReferenceCode());
						}).start();
				} catch (Exception t) {
					t.printStackTrace();
					ctx.log().info("ERROR");
				}
			}
			
			ctx.log().debug("------ [END OK] INSERT INVOICE");
			return entries;
		} catch (Exception t) {
			t.printStackTrace();
			ctx.log().debug("------ [END FAIL] INSERT INVOICE [{0}]",t.getMessage());
			throw t;
		}
	}

	private static void recordInvoice(AONContext ctx, AccountingInvoice invoice, List<AccountEntry> entries) {
		AccountEntry ae = InvoiceRecorder.getInvoiceEntry(ctx, invoice.getInvoice());
		Integer entryId = AccountEntryDAO.save(ctx, ae);
		AccountEntry entry = AccountEntryDAO.getAccountEntry(ctx, entryId);
		entries.add(entry);
		
		ctx.getDslContext().insertInto(ACCOUNT_ENTRY_INVOICE)
			.set(ACCOUNT_ENTRY_INVOICE.DOMAIN,invoice.getInvoice().getDomain())
			.set(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY, entryId)
			.set(ACCOUNT_ENTRY_INVOICE.INVOICE, invoice.getInvoice().getId())
			.execute();
		ctx.log().debug("INSERT ACCOUNT_ENTRY_INVOICE");
		ctx.getDslContext().update(INVOICE)
			.set(INVOICE.STATUS, InvoiceStatus.SCORED.value() )
			.where(INVOICE.ID.eq( invoice.getInvoice().getId()))
			.execute();
		ctx.log().debug("INVOICE MARKED AS SCORED");
		
		if (invoice.getInvoice().hasFinances()) {
			entries.addAll( recordFinances(ctx, invoice) );				
		}
	}
	
	private static LinkedList<AccountEntry> recordFinances(AONContext ctx, AccountingInvoice accInvoice) {
		LinkedList<AccountEntry> entries = new LinkedList<>();
		for (Finance finance : accInvoice.getInvoice().getFinances() ) {
			if (finance.isPending() && accInvoice.getPayAccountId() != null) {
				if (finance.getId() == null) {
					ctx.log().debug("** FINANCE NOT SAVED, NO ENTRY WILL BE RECORDED.");
				} else {
					ctx.log().debug("** READY TO RECORD FINANCE.");
					AccountEntry ae = com.esferalia.aon.occam.api.model.finance.InvoiceRecorder.getFinanceEntry(accInvoice, finance);
					Integer entryId = AccountEntryDAO.insert(ctx, ae);
					AccountEntry newEntry = AccountEntryDAO.getAccountEntry(ctx, entryId);
					entries.add(newEntry);
					Integer financeTrackingId = ctx.getDslContext().insertInto(FINANCE_TRACKING)
							.set(FINANCE_TRACKING.DOMAIN,ctx.getDomainId())
							.set(FINANCE_TRACKING.FINANCE, finance.getId() )
							.set(FINANCE_TRACKING.TRACKING_DATE, AonDateUtils.toSql(finance.getDueDate()))
							.set(FINANCE_TRACKING.TYPE,FinanceTrackingType.PAID.value())
							.set(FINANCE_TRACKING.AMOUNT, finance.getAmount())
							.set(FINANCE_TRACKING.RECORDED, AonEnumUtils.getByte(true))
							.set(FINANCE_TRACKING.DESCRIPTION, "Asiento: " + entryId)
							.set(FINANCE_TRACKING.CREATION_USER,ctx.getUser())
							.set(FINANCE_TRACKING.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
							.returning(FINANCE_TRACKING.ID)
							.fetchOne()
							.getValue(FINANCE_TRACKING.ID);
					ctx.log().debug("\tINSERT FINANCE_TRACKING (finance.id: {0}) finance_tracking.id: {1}",finance.getId(),financeTrackingId);
					
					Integer accountEntryFinanceTrackingId = ctx.getDslContext().insertInto(ACCOUNT_ENTRY_FINANCE_TRACKING)
						.set(ACCOUNT_ENTRY_FINANCE_TRACKING.DOMAIN,ctx.getDomainId())
						.set(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY, entryId )
						.set(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING, financeTrackingId )
						.execute();
					ctx.log().debug("\tINSERT ACCOUNT_ENTRY_FINANCE_TRACKING (account_entry_finance_tracking.id: {0}) account_entry.id: {1}",accountEntryFinanceTrackingId,entryId);
					ctx.getDslContext().update(FINANCE)
						.set(FINANCE.STATUS,FinanceStatus.PAID.value())
						.where(FINANCE.ID.eq(finance.getId()))
						.and(FINANCE.DOMAIN.eq(ctx.getDomainId()))
						.execute();
					ctx.log().info("UPDATE FINANCE STATUS - PAID (finance.id: {0}",finance.getId());
				}
			}
		}
		return entries;
	}
	
	
	private static LinkedList<AccountEntry> update(final AONContext ctx, final AccountingInvoice accInvoice) {
		return null;
//		try {
//			ctx.log().debug("------ [START] UPDATE INVOICE");
//			LinkedList<AccountEntry> entries = new LinkedList<>();
//			LinkedList<InvoiceDetail> details = generateDetails(accInvoice);
//			for (InvoiceDetail detail : accInvoice.getInvoice().getDetails()) {
//				detail.setId(detail.getId() * -1);
//			}
//			accInvoice.getInvoice().getDetails().addAll(details);
//			
//			// Si sólo tiene un vencimiento y está pendiente, se actualiza el importe para que sea igual al total factura 
//			if (accInvoice.getInvoice().getFinances() != null && accInvoice.getInvoice().getFinances().size() == 1) {
//				Finance finance = accInvoice.getInvoice().getFinances().get(0);
//				if (finance.isPending() && !AonNumberUtils.equals(accInvoice.getInvoice().getTotal(),finance.getAmount())) {
//					finance.setAmount(accInvoice.getInvoice().getTotal())
//						.setDirty(true);
//				}
//			}
//			// ---------------------------
//			
//			InvoiceOLDDAO.update(ctx, config, accInvoice.getInvoice());
//			if (accInvoice.isDuaLinked()) {
//				updateInvoiceDUA( ctx, config, accInvoice);
//			}
//			AccountEntry ae = InvoiceRecorder.getInvoiceEntry(accInvoice);
//			for (AccountEntryDetail detail : accInvoice.getAccountEntry().getDetails()) {
//				detail.setId(detail.getId() * -1);
//			}
//			accInvoice.getAccountEntry().getDetails().addAll(ae.getDetails());
//			AccountEntryDAO.update(ctx, accInvoice.getAccountEntry());
//			Integer entryId = accInvoice.getAccountEntry().getId();
//			AccountEntry newEntry = AccountEntryDAO.getAccountEntry(ctx, entryId);
//			accInvoice.setAccountEntry(newEntry);
//			entries.add(newEntry);
//			saveFinances(ctx, accInvoice);
//			ctx.log().debug("------ [END OK] UPDATE INVOICE");
//			return entries;
//		} catch (Exception t) {
//			t.printStackTrace();
//			ctx.log().debug("------ [END FAIL] UPDATE INVOICE [{0}]",t.getMessage());
//			throw t;
//		}
	}
}

