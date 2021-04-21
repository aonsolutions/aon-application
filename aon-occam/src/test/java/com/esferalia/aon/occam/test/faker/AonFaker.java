package com.esferalia.aon.occam.test.faker;

import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountOperatingAccount;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatement;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatementType;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport.AccountTrialBalance;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.DateInterval;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.accounting.BalanceType;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.Tariff;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskHolderType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.MediaType.IMediaTypeVisitor;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.github.javafaker.Faker;

public class AonFaker {
	private static Faker faker = new Faker(new Locale("es"));
	private static String documentRegexp = "(\\d|[XYZ])\\d{7}[A-Z]";
	
	public static Registry getRegistry( AONContext ctx ) {
		return  new Registry()
			.setDomain(new Domain().setId(ctx.getDomainId()))
			.setDocument(faker.regexify(documentRegexp))
			.setDocumentType( AonRandom.randomEnum(DocumentType.class) )
			.setDocumentCountry( AonRandom.gt(5) ? Country.ES: AonRandom.randomEnum(Country.class))
			.setName( faker.company().name() )
			.setAlias( faker.company().profession() )
			.setNationality( AonRandom.gt(5) ? Country.ES: AonRandom.randomEnum(Country.class))
			.setConfidential( !AonRandom.gt(3) );
	}

	public static Customer getCustomer( AONContext ctx ) {
		return getCustomer(ctx, getRegistry(ctx));
	}
	
	public static Customer getCustomer( AONContext ctx , Registry registry) {
		Tariff tariff = AonRandom.getTariff(ctx);
		// Account account = AonRandom.getAccount(ctx, p -> p.getCodeProperty().like("430%"));
		Account account = null;
		Scope scope = AonRandom.random( SecurityDAO.getAvailableScopes (ctx) );
		return new Customer()
			.copy(registry)
			.setTariff( tariff == null? null : tariff.getId() )
			.setSurcharge( AonRandom.gt(95) )
			.setWithholding( AonRandom.gt(85) )
			.setTransaction( AonRandom.gt(10) ? InvoiceTransactionType.NATIONAL : AonRandom.randomEnum(InvoiceTransactionType.class))
			.setStatus( AonRandom.gt(2) ? RegistryStatus.ACTIVE: AonRandom.randomEnum(RegistryStatus.class))
			.setScope( scope == null ? null : scope.getId() )
			.setEInvoice( AonRandom.gt(40) )
			// TODO
			.setInvoicingGroup( null )
			.setProjectGrouped( AonRandom.gt(4) )
			.setDeliveryGrouped( AonRandom.gt(50) )
			.setDeliveryValuated( AonRandom.gt(50) )
			// TODO
			.setAccount( account == null? null : account.getId() );
		
	}
	
	public static Creditor getCreditor( AONContext ctx ) {
		return getCreditor(ctx, getRegistry(ctx));
	}

	public static Creditor getCreditor(AONContext ctx, Registry registry) {
		Scope scope =  AonRandom.random( SecurityDAO.getAvailableScopes (ctx) );
		//Account account = AonRandom.getAccount(ctx, p -> p.getCodeProperty().like("410%"));
		Account account = null;
		return new Creditor()
			.copy(registry)
			.setWithholding( AonRandom.gt(85) )
			.setVatAccrualPayment( AonRandom.gt(98) )
			.setTransaction( AonRandom.gt(10) ? InvoiceTransactionType.NATIONAL : AonRandom.randomEnum(InvoiceTransactionType.class))
			.setStatus( AonRandom.gt(2) ? RegistryStatus.ACTIVE: AonRandom.randomEnum(RegistryStatus.class))
			.setScope( scope == null ? null : scope.getId() )
			.setAccount( account == null? null : account.getId() );
	}

	public static Supplier getSupplier( AONContext ctx ) {
		return getSupplier(ctx, getRegistry(ctx));
	}

	public static Supplier getSupplier(AONContext ctx, Registry registry) {
		Tariff tariff = AonRandom.getTariff(ctx);
		Scope scope =  AonRandom.random( SecurityDAO.getAvailableScopes (ctx) );
		//Account account = AonRandom.getAccount(ctx, p -> p.getCodeProperty().like("410%"));
		Account account = null;
		return new Supplier()
			.copy(registry)
			.setTariff( tariff == null? null : tariff.getId() )
			.setWithholding( AonRandom.gt(95) )
			.setWithholdingFarmer( AonRandom.gt(90) )
			.setVatAccrualPayment( AonRandom.gt(92) )
			.setTransaction( AonRandom.gt(10) ? InvoiceTransactionType.NATIONAL : AonRandom.randomEnum(InvoiceTransactionType.class))
			.setStatus( AonRandom.gt(2) ? RegistryStatus.ACTIVE: AonRandom.randomEnum(RegistryStatus.class))
			.setScope( scope == null ? null : scope.getId() )
			.setPurchaseValuated(AonRandom.gt(50) )
			.setAccount( account == null? null : account.getId() );
	}
	
	public static TaskHolder getTaskHolder( AONContext ctx ) {
		return getTaskHolder(ctx, getRegistry(ctx));
	}
	
	public static TaskHolder getTaskHolder( AONContext ctx , Registry registry) {
		return new TaskHolder()
			.copy(registry)
			.setType(TaskHolderType.INTERNAL)
			.setActive(true)
			.setUserId(null)
			.setCostProfile(null);
		
	}
	
	public static RegistryAddress getRegistryAddress( AONContext ctx) {
		return getRegistryAddress(ctx, null); 
	}
	public static RegistryAddress getRegistryAddress( AONContext ctx, Registry registry ) {
		if (registry == null) {
			registry = AonRandom.getRegistry(ctx);
		}
		GeoZone geozone = AonRandom.getGeozone(ctx,30);
		RegistryAddress address = new RegistryAddress()
			.setDomain(ctx.getDomainId())
			.setRegistry(registry.getId())
			.setMain(AonRandom.gt(50))
			.setStreetType(AonRandom.randomEnum(StreetType.class,75))
			.setRecipient( AonRandom.name(20, RADDRESS.RECIPIENT.getDataType().length()) )
			.setAddress( AonRandom.gt(10)?faker.address().streetName():null )
			.setNumber( AonRandom.gt(12)?faker.address().streetAddressNumber():null)
			.setAddress2( AonRandom.gt(90)?faker.address().secondaryAddress():null )
			.setAddress3( AonRandom.gt(97)?faker.address().secondaryAddress():null )
			.setZip( AonRandom.gt(10)?faker.address().zipCode():null )
			.setCity( AonRandom.gt(10)?faker.address().city():null )
			.setGeozone(geozone == null? null : geozone.getId())
			.setGeozoneCode(geozone == null? null : geozone.getCode())
			.setGeozoneName(geozone == null? null : geozone.getName())
			.setRecipient( AonRandom.alias(20, RADDRESS.ALIAS.getDataType().length()) )
			.setMunicipalityCode(AonRandom.gt(30)?faker.address().zipCode():null);
		return address;
	}

	public static RegistryMedia getRegistryMedia( AONContext ctx) {
		return getRegistryMedia(ctx, null); 
	}
	public static RegistryMedia getRegistryMedia( AONContext ctx, Registry registry ) {
		if (registry == null) {
			registry = AonRandom.getRegistry(ctx);
		}
		RegistryMedia media = new RegistryMedia();
		media.setDomain(ctx.getDomainId());
		media.setRegistry(registry.getId());
		MediaType mediaType = AonRandom.randomEnum(MediaType.class, 5); 
		media.setMedia(mediaType);
		if (mediaType != null) {
			mediaType.visit(new IMediaTypeVisitor() {
				@Override public void visitWeb() { media.setValue( faker.internet().url() ); }
				@Override public void visitUnknown() { media.setValue( null ); }
				@Override public void visitFixedPhone() { media.setValue( faker.phoneNumber().phoneNumber() ); }
				@Override public void visitFax() { media.setValue( faker.phoneNumber().phoneNumber() ); }
				@Override public void visitEmail() {
					String email = faker.internet().safeEmailAddress();
					email = AonStringUtils.remove(email, ' ');
					media.setValue( email );
				}
				@Override public void visitCellular() {media.setValue( faker.phoneNumber().cellPhone() );}
			});
		}
		media.setComment( AonRandom.gt(25) ? faker.lorem().characters(0, 64) : null); 
		
		media.setAdministrative(AonRandom.gt(5));
		media.setCommercial(AonRandom.gt(40));
		media.setTechnical(AonRandom.gt(75));
		Integer registryId = (registry !=null)?registry.getId():null;
		if (registryId != null && AonRandom.gt(15)) {
			RegistryAddress address = AonRandom.getRegistryAddress(ctx, f-> f.getRegistryProperty().eq(registryId));
			if (address != null) {
				media.setRaddress(address.getId());
			}
		}
		return media;
	}

	public static Tariff getTariff( AONContext ctx ) {
		return new Tariff()
			.setDomain(ctx.getDomainId())
			.setCode( faker.number().digits( 6) )
			.setName( faker.commerce().productName())
			.setPurchase( AonRandom.gt(50) )
			.setDiscount( AonRandom.getDouble(0, 100, 2))
			.setActive( !AonRandom.gt(2) );
	}
	
	public static PayMethod getPayMethod( AONContext ctx ) {
		return new PayMethod()
			.setDomain(ctx.getDomainId())
			.setName( faker.lorem().characters(1, 10))
			.setType( AonRandom.randomEnum(PayMethodType.class, 5));
	}
	
	public static AccountPeriod getTodayActiveAccountPeriod(AONContext ctx) {
		return getAccountPeriod(ctx, new Date(), AccountPeriodStatus.ACTIVE); 
	}

	public static AccountPeriod getAccountPeriod(AONContext ctx, Date date, AccountPeriodStatus status) {
		return  new AccountPeriod()
			.setDomain(ctx.getDomainId())	
			.setDomain(ctx.getDomainId())
			.setName(AonNumberUtils.toString( AonDateUtils.getYear(date)))
			.setInitiationDate(AonDateUtils.getYearFirstDay(date))
			.setDeadline(AonDateUtils.getYearLastDay(date))
			.setStatus(status);
	}

	public static AccountingReportParams getAccountingReportParams(AONContext ctx) {
		return new AccountingReportParams()
			.setDomainName(faker.internet().domainName())
			.setDomain( AonRandom.getInt(0, 10000000) )
			.setUser( AonRandom.string(5, 15 ))
			.setPeriod( AonRandom.integer(15, 1000) )
			.setFromDate( AonRandom.getPastDate(10) )
			.setToDate( AonRandom.getPastDate(10) )
			.setAccount( AonRandom.getAccount(ctx,80))
			.setLevel( AonRandom.getInt(0, 9) )
			.setActivity( AonRandom.integer(75, 1000) )
			.setSecurityLevel( AonRandom.randomEnum(SecurityLevel.class))
			.setDocumentNumber( AonRandom.string(5, 15 ))
			.setPreviousPeriods( AonRandom.getInt(0, 9) )
			.setLowLevelAccountVisible(AonRandom.gt( 50 ))
			.setNoActivityAccountVisible(AonRandom.gt( 50 ))
			.setPercentsEnabled(AonRandom.gt( 50 ))
			.setByMonth(AonRandom.gt( 50 ))
			.setOpeningEntriesExcluded(AonRandom.gt( 50 ))
			.setOperatingEntriesExcluded(AonRandom.gt( 50 ))
			.setClosingEntriesExcluded(AonRandom.gt( 50 ))
			.setReverseOrder(AonRandom.gt( 50 ))
			.setBalanceType(AonRandom.randomEnum(BalanceType.class))
			.setSelectedPeriod(AonRandom.getAccountPeriod(ctx, 85))
			.setSelectedActivity(AonRandom.getRandomActivity(ctx))
			.setSelectedAccount( AonRandom.getAccount(ctx,80))
			.setBreakdownEnabled(AonRandom.gt( 50 ))
			.setLedgerAccount( AonRandom.integer(75, 1000) )
			.setLedgerDebitBalance( AonRandom.getDouble(0, 1000, 2))
			.setLedgerUnpaidBalance( AonRandom.getDouble(0, 1000, 2))
			.setConsolidation(AonRandom.gt( 50 ))
			.setRegistry( AonRandom.integer(75, 1000) )
			.setOutput(AonRandom.gt( 50 ))
			.setVatSummaryType(AonRandom.randomEnum(VatSummaryType.class))
			.setPercent( AonRandom.getDouble(0, 100, 2))
			.setRectificationType(AonRandom.randomEnum(RectificationType.class))
			.setSurcharge(AonRandom.gt( 50 ))
			.setFarmerRegime(AonRandom.gt( 50 ))
			.setAccrualRegime(AonRandom.gt( 50 ))
			.setInvestment(AonRandom.gt( 50 ))
			.setService(AonRandom.gt( 50 ))
			.setTitle( AonRandom.string(50, 100 ))
			.setSubject( AonRandom.string(50, 100 ))
			.setShowCover(AonRandom.gt( 50 ))
			.setPageOffset( AonRandom.getInt(0, 50 ))
			.setPageOffsetText(AonRandom.string(50, 100 ))
			.setHideFilter(AonRandom.gt( 50 ))
			.setHeaderText(AonRandom.string(50, 100 ))
			.setHideDateTimeOnFooter(AonRandom.gt( 50 ))
			.setFooterText(AonRandom.string(50, 100 ))
		;
//		private Integer[] invoices;
//		private LinkedList<Domain> domains;
//		private HashSet<String> costCenters;
	}

	public static AccountTrialBalance getAccountTrialBalance(AONContext ctx) {
		return new AccountTrialBalance()
			.setId( AonRandom.integer(5, 1000000 ))
			.setCode(AonRandom.string( 5, 9 ))
			.setDescription(AonRandom.string( 5, 39 ))
			.setBeforePeriodDebit(AonRandom.getDouble(0, 150000))
			.setBeforePeriodCredit(AonRandom.getDouble(0, 150000))
			.setInPeriodOpeningDebit(AonRandom.getDouble(0, 150000))
			.setInPeriodOpeningCredit(AonRandom.getDouble(0, 150000))
			.setInPeriodBeforeDebit(AonRandom.getDouble(0, 150000))
			.setInPeriodBeforeCredit(AonRandom.getDouble(0, 150000))
			.setInPeriodDebit(AonRandom.getDouble(0, 150000))
			.setInPeriodCredit(AonRandom.getDouble(0, 150000))
			;
	}

	public static AccountTrialBalanceReport getAccountTrialBalanceReport(AONContext ctx) {
		AccountTrialBalanceReport report = new AccountTrialBalanceReport()
				.setParams(getAccountingReportParams(ctx))
				.setHasBeforePeriodAmounts(AonRandom.gt( 50 ))
				.setHasOpeningAmounts(AonRandom.gt( 50 ))
				.setHasInPeriodPreviousAmounts(AonRandom.gt( 50 ))
				.setTotalBalance(getAccountTrialBalance(ctx));
		Integer times = AonRandom.integer(10, 50);
		if (times != null) {
			AccountTrialBalance bal = getAccountTrialBalance(ctx);
			if (AonStringUtils.isNotBlank( bal.getCode())){
				if (report.getBalances() == null) {
					report.setBalances(new TreeMap<String, AccountTrialBalance>());
				}
				report.getBalances().put(bal.getCode(), bal);
			}
		}
		return report;
	}
	
	public static Product getProduct( AONContext ctx ) {
		return  new Product()
			.setDomain(new Domain().setId(ctx.getDomainId()))
			.setName(faker.commerce().productName())
			.setCode(AonRandom.string(0, 1, 14));
	}
	
	public static ProductCategory getProductCategory( AONContext ctx ) {
		return  new ProductCategory()
			.setDomain(ctx.getDomainId())
			.setName(faker.pokemon().name())
			.setDetail(AonRandom.string(50, 10))
			.setDetail2(AonRandom.string(50, 10))
			.setDetail3(AonRandom.string(50, 10));
	}
	
	public static AccountOperatingReport getAccountOperatingReport (AONContext ctx) {
		AccountOperatingReport report = new AccountOperatingReport();
		report.setParams(getAccountingReportParams(ctx));
		
		TreeSet<AccountOperatingAccount> accounts = new TreeSet<AccountOperatingAccount>();
		for (int i=0; i<AonRandom.getInt(1, 20); i++) {
			AccountOperatingAccount account = new AccountOperatingAccount()
				.setCode(String.valueOf(AonRandom.getInt(0, 999999999)))
				.setDescription(AonRandom.lorem(25, 9))
				.setId(AonRandom.getInt(0, 5));
			AccountOperatingStatementType[] types = AccountOperatingStatementType.values();
			account.setType(types[AonRandom.getInt(0, types.length-1)]);
			accounts.add(account);
		}
		
		report.setAccounts(accounts);
		
		TreeSet<DateInterval> intervals = new TreeSet<DateInterval>();
		for (int i=0; i<AonRandom.getInt(1, 12); i++) {
			DateInterval interval = new DateInterval();
			interval.setName(AonRandom.name(25, 10));
			Date sDate = AonRandom.getPastDate(0);
			sDate = sDate != null ? sDate : new Date(0);
			interval.setStart(sDate);
			long startTime = interval.getStart()!=null?interval.getStart().getTime():0;
			long endTime = startTime + AonRandom.getInt(0, 1000000000);
			interval.setEnd(new Date(endTime));
			intervals.add(interval);
			for (int j=0; j<AonRandom.getInt(1, 10); j++) {
				AccountOperatingStatement statement = new AccountOperatingStatement()
				.setAccount(accounts.stream().findAny().orElseGet(null))
				.setCredit(AonRandom.getDouble(0, 10000))
				.setDebit(AonRandom.getDouble(0, 10000))
				.setExpensesRatio(AonRandom.getDouble(0, 100))
				.setIncreasePercent(AonRandom.getDouble(0, 100))
				.setMonth(AonRandom.string(80, 10))
				.setPurchasesRatio(AonRandom.getDouble(0, 100))
				.setSalesRatio(AonRandom.getDouble(0, 100));
				report.put(interval, statement);
			}
		}
		report.setIntervals(intervals);
		return report;
	}
	
	public static Collection<AccountPeriod> getAccountPeriods(AONContext ctx) {
		Collection<AccountPeriod> periods = new LinkedList<AccountPeriod>();
		for (int i=0; i<AonRandom.getInt(0, 10); i++) {
			periods.add(AonRandom.getAccountPeriod(ctx, 80));
		}
		return periods;
	}
	
}

