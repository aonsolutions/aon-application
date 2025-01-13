package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceDua.INVOICE_DUA;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.Account;
import net.aonsolutions.occam.api.model.AccountEntry;
import net.aonsolutions.occam.api.model.AccountingDUAInfo;
import net.aonsolutions.occam.api.model.AccountingDUAInvoice;
import net.aonsolutions.occam.api.model.AccountingInvoice;
import net.aonsolutions.occam.api.model.Invoice;
import net.aonsolutions.occam.api.model.InvoiceDetail;
import net.aonsolutions.occam.api.model.InvoiceTax;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.handler.AccountHandler.AccountFiller;

class AccountingInvoiceHandler {
	
	private static final com.esferalia.aon.jooq.tables.Account DUT_ACCOUNT = ACCOUNT.as("DUT_ACCOUNT");
	private static final com.esferalia.aon.jooq.tables.Account VAT_ACCOUNT = ACCOUNT.as("VAT_ACCOUNT");

	private AccountingInvoiceHandler() {
	}
	
	static Optional<Account> getInvoiceDetailAccount(AONContext ctx, Integer invoiceDetailId) {
		return ctx.getDslContext()
			.select()
			.from(ACCOUNT)
			.join(INVOICE_DETAIL_ACCOUNT).on(INVOICE_DETAIL_ACCOUNT.ACCOUNT.eq(ACCOUNT.ID))
			.where(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.eq(invoiceDetailId)).limit(1)
			.fetch()
			.stream()
			.map(new AccountFiller())
			.findFirst();
	}
	
	static Optional<AccountingInvoice> getFromAccountEntry(final AONContext ctx, final int domain, final Integer accountEntryId) {
		return ctx.getDslContext()
			.select( ACCOUNT_ENTRY_INVOICE.INVOICE )
			.from( ACCOUNT_ENTRY_INVOICE )
			.where(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.eq(accountEntryId))
			.and(ACCOUNT_ENTRY_INVOICE.DOMAIN.eq(domain))
			.fetch()
			.stream()
			.map(rec -> rec.getValue(ACCOUNT_ENTRY_INVOICE.INVOICE))
			.findFirst()
			.map( invoiceId -> InvoiceHandler.get(ctx, domain, invoiceId).orElseThrow(() -> new AonCoreException("No se pudo encontrar la factura")))
			.map( invoice -> new AccountingInvoice().setInvoice(invoice))
			.map( ai -> ai.setAccountEntry(AccountEntryHandler.get(ctx, domain, accountEntryId).orElseThrow(() -> new AonCoreException("No se pudo encontrar en asiento contable"))))
			.map( ai -> fillAccountingInvoice(ctx, domain, ai) )
		;
	}
	
	private static AccountingInvoice fillAccountingInvoice(AONContext ctx, int domain, AccountingInvoice ai) {
		Invoice invoice = ai.getInvoice();
		ai
			.setManualConcept(
				ai.getAccountEntry()
					.detailStream()
					.map( d -> d.getConcept())
					.map( c -> AonStringUtils.substringBetween(c,"[","]"))
					.findFirst()
					.orElse(null))
			.setRegistry(
				InvoiceRegistryHandler.get( ctx, invoice.getId() ) 
					.orElseThrow(() -> new AonCoreException("No se pudo encontrar el titular de la factura"))
			)
		;
		
		
		if (invoice.isDUAAllowed()) {
			fillDUAInfo(ctx, domain, ai);
		}
		
		if (invoice.isDUALinkAllowed()) {
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
		
		if (!invoice.isUndeductible()) {
			invoice.detailStream()
				.filter(d -> !d.isPrepayment())
				.forEach(d -> fillInvoiceTax(ctx, ai, d));
		}
		return ai;
	}
	
	private static void fillInvoiceTax(AONContext ctx, AccountingInvoice ai, InvoiceDetail invoiceDetail) {
		Invoice invoice = ai.getInvoice(); 
		int domain = invoice.getDomain();
		// *********************
		// Al no guardar el porcentaje de imposición directa en BD, se "supone" su activación en función
		// de la existencia de la cuenta en apuntes.
		// Si la cuenta ha cambiad, el apunte fallará....
		// Si el porcentaje de invest_asset ha cambiado, el apunte fallará-
		boolean directTaxEnabledPre = false;
		Account directTaxAccount = ctx
				.getApplicationParameters(domain)
				.getDirectTaxAdjustAccount()
				.flatMap( accountId -> AccountHandler.get(ctx, domain, accountId))
				.orElse(null);
		if (directTaxAccount != null && ai.getAccountEntry() != null) {
			directTaxEnabledPre = ai.getAccountEntry().detailStream()
				.anyMatch( aed -> AonNumberUtils.equals(
						aed.getAccount()
							.map(Account::getId)
							.orElse(null)
						,directTaxAccount.getId()));
		}
		final boolean directTaxEnabled = directTaxEnabledPre;
		// *********************
		
		InvoiceTax vat = invoiceDetail.enableVatTax( );
		Account vatAccount = invoiceDetail
			.getVatTax()
			.flatMap( it -> InvoiceTaxHandler.getInvoiceTaxAccount(ctx, it.getId()))
			.orElse(null)
		;
		
		Integer investAsset = invoiceDetail.getInvestAsset().map(ia -> ia.getId()).orElse(null);

		Double  directTaxPercent = Double.valueOf(0);
		if (directTaxEnabled && investAsset != null) {
			directTaxPercent = ctx
				.getInvestAssets(domain) 
				.filter( ia -> AonNumberUtils.equals(ia.getId(),investAsset))
				.map( ia -> ia.getRetentionPercent())
				.findFirst()
				.orElse(Double.valueOf(0));
		}
		invoiceDetail.setDirectTaxPercent(directTaxPercent);
		
		if (invoice.isSales()) {
			vat.setOutputAccount(vatAccount);
		}
		if (!invoice.isSales()) {
			vat.setInputAccount(vatAccount);
			if (invoice.isOutputVatEnabled()) {
				vat.setOutputAccount( ctx
					.getApplicationParameters(domain)
					.getOutputVatDefaultAccount()
					.orElse(null)
				);
			}
			if (investAsset != null) {
				vat.setAdjAccount( ctx
					.getApplicationParameters(domain)
					.getVatNegativeAdjustAccount()
					.orElse(null)
				);
			}
			if (investAsset != null && directTaxEnabled) {
				invoiceDetail.setAdjDirectTaxAccount(directTaxAccount);
			}
		}
	}
	
	private static void fillDUAInfo(AONContext ctx, int domain, AccountingInvoice ai) {
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
				.setAccountingInvoice( getFromInvoice(ctx, domain, rec.getValue(INVOICE_DUA.INVOICE_IMPORT)).orElse(null) )
			)
			.findFirst()
			.orElse(null);
		if (accountingDUAInvoice != null ) {
			ai.setDuaLinked(true);
			if (accountingDUAInvoice.getAccountingInvoice() != null) {
				accountingDUAInvoice.getInfo().setDuaDetails(
						accountingDUAInvoice.getAccountingInvoice().getInvoice().detailStream()
							.map(ori -> ori.duplicate().setAutoGenerated(true) )
							.collect(Collectors.toCollection(LinkedList::new))
				);
			}
			ai.setDuaInvoice(accountingDUAInvoice);
			
			accountingDUAInvoice.getInfo().setAuthCalcEnabled(false);
		}
	}
	
	static Optional<AccountingInvoice> getFromInvoice(final AONContext ctx, final int domain,  final Integer invoiceId) {
		return getFromInvoice(ctx, domain, InvoiceHandler.get(ctx, domain, invoiceId)
			.orElseThrow(() -> new AonCoreException("No se pudo encontrar la factura")));
	}

	static Optional<AccountingInvoice> getFromInvoice(final AONContext ctx, final int domain, final Invoice invoice) {
		if (invoice == null) throw new IllegalArgumentException("Invoice can not be null");
		AccountingInvoice ai = new AccountingInvoice().setInvoice(invoice);
		AccountEntry ae = null;
		if (invoice.getId() == null) {
			ae = InvoiceRecorder.getInvoiceEntry(ctx, domain, ai.getInvoice()); 
		} else {
			ae = ctx.getDslContext()
				.select( ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY)
				.from( ACCOUNT_ENTRY_INVOICE )
				.where(ACCOUNT_ENTRY_INVOICE.INVOICE.eq(invoice.getId()))
				.fetch()
				.stream()
				.map(rec -> rec.getValue(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY))
				.findFirst()
				.flatMap(aeId -> AccountEntryHandler.get(ctx, domain, aeId))
				.orElse(null) 
				;
		}
		ai.setAccountEntry(ae);
		fillAccountingInvoice(ctx, domain, ai);
		return Optional.of(ai); 
	}

	/*
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
				.set(INVOICE_TAX_ACCOUNT.INVOICE_TAX, invoiceTax.getId())
				.set(INVOICE_TAX_ACCOUNT.ACCOUNT, accountId )
			.execute();
			ctx.log().debug("\t\tINSERT INVOICE_TAX_ACCOUNT");
		} else {
			ctx.log().debug("\t\tSKIPPING INVOICE TAX ACCOUNT CREATION ({0})",
					(invoiceDetail.isPrepayment()?"PREPAYMENT":"UNDEDUCTIBLE INVOICE"));
		}
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
		InvoiceTextPrinter.print(ai.getInvoice());
		return ai;
	}

	private static InvoiceDetail createNewInvoiceDetail(final AONContext ctx,AccountingInvoice ai) {
		Invoice inv = ai.getInvoice();
		InvoiceDetail detail = new InvoiceDetail()
			.setSource(InvoiceSource.ACCOUNT);
		
		if (ctx.getConfiguration().getDefaultVatPercent() != null) {
			if (ai.isSales() && !ai.isNational()) {
				detail.ensureVatTax(inv).setPercentage(0.0);
			} else {
				detail.ensureVatTax(inv).setPercentage(ctx.getConfiguration().getDefaultVatPercent().getPercentage());
			}
			if (ai.isSurcharge()) {
				detail.ensureVatTax(inv).setSurcharge(ctx.getConfiguration().getDefaultVatPercent().getSurcharge());	
			}
			detail.ensureVatTax(inv).setInputAccount( ctx.getConfiguration().getDefaultVatPercent().getPurchaseAccount());
			detail.ensureVatTax(inv).setOutputAccount( ctx.getConfiguration().getDefaultVatPercent().getSalesAccount());
		}
		if (detail.ensureVatTax(inv).getInputAccount() == null) {
			detail.ensureVatTax(inv).setInputAccount( ctx.getConfiguration().accounting().getDefaultPaidVatAccount());
		}
		if (detail.ensureVatTax(inv).getOutputAccount() == null) {
			detail.ensureVatTax(inv).setOutputAccount( ctx.getConfiguration().accounting().getDefaultChargedVatAccount()) ;
		}
		if (ctx.getConfiguration().accounting().getVatNegativeAdjustAccount() != null) {
			detail.ensureVatTax(inv).setAdjAccount( ctx.getConfiguration().accounting().getVatNegativeAdjustAccount());
		}
		if (ctx.getConfiguration().accounting().getDirectTaxAdjustAccount() != null) {
			detail.ensureVatTax(inv).setAdjDirectTaxAccount( ctx.getConfiguration().accounting().getDirectTaxAdjustAccount());
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
	

	private static void checkTBAIForSales( final AONContext ctx, final InvoiceType type ) {
		TbaiConfiguration tbaiConfig = TbaiConfigurationDAO.get(ctx);
		if (tbaiConfig.isActive() && type == InvoiceType.SALES) {
			throw new AonCoreException("No se pueden crear facturas emitidas en entornos con TicketBai activado");
		}
	}

	
	private static void checkRegistryAccount(final AONContext ctx, AccountingInvoice accInvoice) {
		if (accInvoice.getRegistry().getAccountId() == null) {
			accInvoice.getRegistry().getType().visit(new AccountingRegistryTypeVisitor() {
				@Override
				public void visitSupplier() {
					Account account = createAccountAndFill(accInvoice);
					SupplierDAO.updateSupplierAccount(ctx, accInvoice.getRegistry().getId(),account.getId());
				}
				
				@Override
				public void visitCustomer() {
					Account account = createAccountAndFill(accInvoice);
					CustomerDAO.updateCustomerAccount(ctx, accInvoice.getRegistry().getId(),account.getId());
				}
				
				@Override
				public void visitCreditor() {
					Account account = createAccountAndFill(accInvoice);
					CreditorDAO.updateCreditorAccount(ctx, accInvoice.getRegistry().getId(),account.getId());
				}
				
				@Override
				public void visitUndedCreditor() {
					visitCreditor();
				}
				
				private Account createAccountAndFill(AccountingInvoice accInvoice) {
					AccountingRegistry reg = accInvoice.getRegistry();
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
					accInvoice.getInvoice().setRegistryAccount(account);	
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
			teachAonOcr( ctx, accInvoice);
			InvoiceTextPrinter.print(accInvoice.getInvoice());
			ctx.log().debug("------ [END OK] INSERT INVOICE");
			return entries;
		} catch (Exception t) {
			t.printStackTrace();
			ctx.log().debug("------ [END FAIL] INSERT INVOICE [{0}]",t.getMessage());
			throw t;
		}
	}

	private static void teachAonOcr(AONContext ctx, AccountingInvoice accInvoice) {
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
		try {
			ctx.log().debug("------ [START] UPDATE INVOICE");
			LinkedList<AccountEntry> entries = new LinkedList<>();
			LinkedList<InvoiceDetail> details = generateDetails(accInvoice);
			for (InvoiceDetail detail : accInvoice.getInvoice().getDetails()) {
				detail.setId(detail.getId() * -1);
			}
			accInvoice.getInvoice().getDetails().addAll(details);
			
			// Si sólo tiene un vencimiento y está pendiente, se actualiza el importe para que sea igual al total factura 
			if (accInvoice.getInvoice().getFinances() != null && accInvoice.getInvoice().getFinances().size() == 1) {
				Finance finance = accInvoice.getInvoice().getFinances().get(0);
				if (finance.isPending() && !AonNumberUtils.equals(accInvoice.getInvoice().getTotal(),finance.getAmount())) {
					finance.setAmount(accInvoice.getInvoice().getTotal())
						.setDirty(true);
				}
			}
			// ---------------------------
			
			InvoiceOLDDAO.update(ctx, config, accInvoice.getInvoice());
			if (accInvoice.isDuaLinked()) {
				updateInvoiceDUA( ctx, config, accInvoice);
			}
			AccountEntry ae = InvoiceRecorder.getInvoiceEntry(accInvoice);
			for (AccountEntryDetail detail : accInvoice.getAccountEntry().getDetails()) {
				detail.setId(detail.getId() * -1);
			}
			accInvoice.getAccountEntry().getDetails().addAll(ae.getDetails());
			AccountEntryDAO.update(ctx, accInvoice.getAccountEntry());
			Integer entryId = accInvoice.getAccountEntry().getId();
			AccountEntry newEntry = AccountEntryDAO.getAccountEntry(ctx, entryId);
			accInvoice.setAccountEntry(newEntry);
			entries.add(newEntry);
			saveFinances(ctx, accInvoice);
			ctx.log().debug("------ [END OK] UPDATE INVOICE");
			return entries;
		} catch (Exception t) {
			t.printStackTrace();
			ctx.log().debug("------ [END FAIL] UPDATE INVOICE [{0}]",t.getMessage());
			throw t;
		}
	}
*/
}

