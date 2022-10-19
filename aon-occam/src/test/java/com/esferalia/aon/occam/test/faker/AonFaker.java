package com.esferalia.aon.occam.test.faker;

import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;

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
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.DateInterval;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.management.Offer;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.management.ShipmentPeriod;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.Tariff;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RDirStaff;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskHolderType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.MediaType.IMediaTypeVisitor;
import com.esferalia.aon.occam.api.model.type.OfferDetailStatus;
import com.esferalia.aon.occam.api.model.type.OfferStatus;
import com.esferalia.aon.occam.api.model.type.OfferType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.SalesDetailStatus;
import com.esferalia.aon.occam.api.model.type.SalesStatus;
import com.esferalia.aon.occam.api.model.type.SalesType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.TargetStatus;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.WorkgroupStatus;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DeliveryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO;
import com.esferalia.aon.occam.impl.jooq.dao.OfferDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectTypeDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TargetDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WorkgroupDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.github.javafaker.Faker;

public class AonFaker {
	private static Faker faker = new Faker(new Locale("es"));
	private static String nafRegexp = "\\d{2}\\d{8}\\d{2}";
	private static String cccRegexp = "\\d{2}\\d{7}\\d{2}";
	private static String documentRegexp = "(\\d|[XYZ])\\d{7}[A-Z]";
	
	public static String getCCC() {
		return faker.regexify(cccRegexp);
	}
	
	public static Registry getRegistry( AONContext ctx ) {
		return  new Registry()
			.setDomain(new Domain().setId(ctx.getDomainId()))
			.setDocument(faker.regexify(documentRegexp))
			.setDocumentType( AonRandom.getRandomDocumentType() )
			.setDocumentCountry( AonRandom.gt(5) ? Country.ES: AonRandom.getRandomCountry())
			.setName( faker.company().name() )
			.setAlias( faker.company().profession() )
			.setNationality( AonRandom.gt(5) ? Country.ES: AonRandom.getRandomCountry())
			.setConfidential( !AonRandom.gt(3) );
	}

	public static Company getCompany( AONContext ctx , Registry registry) {
		return  new Company()
			.copy(registry)
			.setActive( AonRandom.gt(2) )
			.setSurcharge( AonRandom.gt(95) )
			.setWithholding( AonRandom.gt(85) )
			.setWithholding( AonRandom.gt(85) )
			.setVatAccrualPayment( AonRandom.gt(99) )
			.seteInvoice( AonRandom.gt(50) )
			;
	}
	
	public static EnterpriseActivity getEnterpriseActivity(AONContext ctx) {
		return new EnterpriseActivity()
			.setDescription(faker.job().title())
			.setPrincipal(AonRandom.gt(50))
			
			;			
//			private Iae iae;
//			private Integer cnae;
//			private VATRegime vatRegime; 
//			.setEnterprise(company.getId())
//			.setScope(scope.getId())
//			.setAddress(company.getAddresses().getFirst().getId());
	}

	public static Employee getEmployee( AONContext ctx, Date startDate, Date endDate) {
		return getEmployee(ctx, getCCC(), startDate, endDate);
	}

//	public static Employee getEmployee( AONContext ctx, Date startDate, Date endDate, String ccc ) {
//		return new Employee()
//		.setCcc(ccc)		
//		.setEndDate(endDate)
//		.setStartDate(startDate)
//		.setCategory(faker.job().title())
//		.setName(faker.name().fullName())
//		.setNaf(faker.regexify(nafRegexp))
//		//.setCif(faker.regexify(documentRegexp))
//		.setDni(faker.regexify(documentRegexp))
//		.setRegime(AonRandom.getRandomSSRegime())
//		.setQuoteGroup(AonRandom.getRandomQuoteGroup(5))
//		.setOccupation(AonRandom.getRandomOccupation(75))
//		.setContractType(AonRandom.getRandomContractType(5))
//		.setFactor(AonRandom.gt(50)? faker.random().nextDouble(): null)
//
//		.setSex(AonRandom.gt(33) ? AonRandom.getRandomSex() : "U")
//		.setBirthDate(AonRandom.gt(75) ? faker.date().birthday():null)
//		.setPhone(AonRandom.gt(75) ? faker.phoneNumber().phoneNumber() :null)
//		;
//	}

	public static Employee getEmployee( AONContext ctx, String ccc, Date ...dates) {
		Date startDate = dates[0];
		Date endDate = dates[dates.length-1];
		Employee employee = 
		new Employee()
		.setCcc(ccc)		
		.setStartDate(startDate)
		.setEndDate(endDate)
		.setCategory(faker.job().title())
		.setName(faker.name().fullName())
		.setNaf(faker.regexify(nafRegexp))
		//.setCif(faker.regexify(documentRegexp))
		.setDni(faker.regexify(documentRegexp))
		.setRegime(AonRandom.getRandomSSRegime())

		.setSex(AonRandom.gt(33) ? AonRandom.getRandomSex() : "U")
		.setBirthDate(AonRandom.gt(75) ? faker.date().birthday():null)
		.setPhone(AonRandom.gt(75) ? faker.phoneNumber().phoneNumber() :null)
		;
		
		for ( int i = 0; i < dates.length; i += 2 ) {
			Date dataStartDate = dates [i];
			Date dataEndDate  = dates [i+1];
			employee
			.addQuoteGroup(AonRandom.getRandomQuoteGroup(5), dataStartDate, dataEndDate)
			.addOccupation(AonRandom.getRandomOccupation(75), dataStartDate, dataEndDate)
			.addContractType(AonRandom.getRandomContractType(5), dataStartDate, dataEndDate)
			.addFactor(AonRandom.gt(50)? faker.random().nextDouble(): null, dataStartDate, dataEndDate);
		}
		
		return employee;
		
	}

	public static Target getTarget( AONContext ctx ) {
		return getTarget(ctx, getRegistry(ctx));
	}
	
	public static Target getTarget( AONContext ctx , Registry registry) {
		Tariff tariff = AonRandom.getTariff(ctx);
		Scope scope = AonRandom.random( SecurityDAO.getAvailableScopes (ctx) );
		return new Target()
			.copy(registry)
			.setTariff(tariff)
			.setSurcharge( AonRandom.gt(95) )
			.setWithholding( AonRandom.gt(85) )
			.setTransaction( AonRandom.gt(10) ? InvoiceTransactionType.NATIONAL : AonRandom.getRandomInvoiceTransactionType())
			.setStatus(TargetStatus.ACTIVE )
			.setScope(scope);		
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
			.setTransaction( AonRandom.gt(10) ? InvoiceTransactionType.NATIONAL : AonRandom.getRandomInvoiceTransactionType())
			.setStatus( AonRandom.gt(2) ? RegistryStatus.ACTIVE: AonRandom.getRandomRegistryStatus())
			.setScope( scope == null ? new Scope() : scope)
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
			.setTransaction( AonRandom.gt(10) ? InvoiceTransactionType.NATIONAL : AonRandom.getRandomInvoiceTransactionType())
			.setStatus( AonRandom.gt(2) ? RegistryStatus.ACTIVE: AonRandom.getRandomRegistryStatus())
			.setScope( scope == null ? new Scope() : scope)
			.setAccount( account == null? null : account.getId() );
	}


	public static Seller getSeller( AONContext ctx ) {
		return getSeller(ctx, getRegistry(ctx));
	}

	public static Seller getSeller(AONContext ctx, Registry registry) {
		Scope scope =  AonRandom.random( SecurityDAO.getAvailableScopes (ctx) );
		return new Seller()
			.copy(registry)
			.setActive(true)
			.setScope(scope)
			.setCommissionType(null);
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
			.setTransaction( AonRandom.gt(10) ? InvoiceTransactionType.NATIONAL : AonRandom.getRandomInvoiceTransactionType())
			.setStatus( AonRandom.gt(2) ? RegistryStatus.ACTIVE: AonRandom.getRandomRegistryStatus())
			.setScope( scope == null ? new Scope() : scope)
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
		return getRegistryAddress( ctx, registry, null );	
	}
	public static RegistryAddress getRegistryAddress( AONContext ctx, Registry registry, GeoZone geozone) {
		if (registry == null) {
			registry = AonRandom.getRegistry(ctx);
		}
		if (geozone == null) {
			geozone = AonRandom.getGeozone(ctx,10);
		}
		RegistryAddress address = new RegistryAddress()
			.setDomain(ctx.getDomainId())
			.setRegistry(registry.getId())
			.setMain(AonRandom.gt(50))
			.setStreetType(AonRandom.getRandomStreetType(75))
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
		MediaType mediaType = AonRandom.getRandomMediaType(5); 
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

	public static RDirStaff getRDirStaff( AONContext ctx) {
		return getRDirStaff(ctx, null); 
	}
	public static RDirStaff getRDirStaff( AONContext ctx, Registry registry ) {
		if (registry == null) {
			registry = AonRandom.getRegistry(ctx);
		}
		
		RDirStaff rDirStaff = new RDirStaff()
			.setDomain(ctx.getDomainId())
			.setRegistry(registry.getId())
			.setDocument( AonRandom.name(-1, RDIR_STAFF.DOCUMENT.getDataType().length()) )
			.setName( AonRandom.name(-1, RDIR_STAFF.NAME.getDataType().length()) )
			.setShareHolder( AonRandom.gt(50) )
			.setRepresentative( AonRandom.gt(50) )
			.setDirector( AonRandom.gt(50) )
			.setRepresentativeLabor( AonRandom.gt(50) )
			.setDueDate( AonRandom.getFutureDate(10) )
			.setPercentShare( AonRandom.getDouble(0, 100, 2))
			.setShareNumber( AonRandom.number(20 ,0, 100))
			.setNominalValue( AonRandom.getDouble(20 ,0, 1000000, 2))
			.setChargeDescription(AonRandom.gt(60)
				?AonStringUtils.abbreviate( faker.name().username(), RDIR_STAFF.CHARGE_DESCRIPTION.getDataType().length())
				:null
			);
		return rDirStaff;
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
			.setType( AonRandom.getRandomPayMethodType(5));
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


	public static Account getAccount(AONContext ctx, String code) {
		byte level = (byte) (code.length() > 4 ? 5 : code.length());
		return  new Account()
				.setActive(true)
				.setAlias(AonStringUtils.substring(faker.artist().name(), 0, 32))
				.setCode(code)
				.setCostCenter(AonStringUtils.substring(faker.address().cityName(), 0, 32))
				.setDescription(AonStringUtils.substring(faker.gameOfThrones().house(), 0, 128))
				.setDomain(ctx.getDomainId())
				.setLevel(level)
				.setEntryEnabled(level == 5)
				;
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
			.setSecurityLevel( AonRandom.getRandomSecurityLevel())
			.setDocumentNumber( AonRandom.string(5, 15 ))
			.setPreviousPeriods( AonRandom.getInt(0, 9) )
			.setLowLevelAccountVisible(AonRandom.gt( 50 ))
			.setNoActivityAccountVisible(AonRandom.gt( 50 ))
			.setNoBalanceAccountExcluded(AonRandom.gt( 50 ))
			.setPercentsEnabled(AonRandom.gt( 50 ))
			.setByMonth(AonRandom.gt( 50 ))
			.setOpeningEntriesExcluded(AonRandom.gt( 50 ))
			.setOperatingEntriesExcluded(AonRandom.gt( 50 ))
			.setClosingEntriesExcluded(AonRandom.gt( 50 ))
			.setReverseOrder(AonRandom.gt( 50 ))
			.setBalanceType(AonRandom.getRandomBalanceType())
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
			.setVatSummaryType(AonRandom.getRandomVatSummaryType())
			.setPercent( AonRandom.getDouble(0, 100, 2)) 
			.setRectificationType(AonRandom.getRandomRectificationType())
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
			.setCode(AonRandom.string(0, 1, 14))
			.setVat(new Tax()
					.setDomain(ctx.getDomainId())
					.setName("test")
					.setType(TaxType.VAT)
					.setPercentage(21.0)
					.setStartDate(new Date()));
	}
	
	public static Item getItem(AONContext ctx ) {
		Product product = ProductDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(product == null || product.getId() == null) product = ProductDAO.save(ctx, getProduct(ctx));
		
		return  new Item()
			.setDomain(new Domain().setId(ctx.getDomainId()))
			.setProduct(product)
			.setDetail("11")
			.setDetail2("22")
			.setDetail2("33");
	}
	
	public static Brand getBrand( AONContext ctx ) {
		return new Brand()
			.setDomain(ctx.getDomainId())
			.setName(AonRandom.string(-1, 1, 14));
	}
	
	
	public static Workgroup getWorkgroup( AONContext ctx ) {
		return new Workgroup()
			.setDomain(ctx.getDomainId())
			.setDescription(faker.beer().name())
			.setStatus(WorkgroupStatus.ACTIVE);
	}
	public static Workplace getWorkplace(AONContext ctx) {
		CompanyFull company = CompanyDAO.getFull(ctx, ctx.getDomainId());
		Scope scope = AonRandom.random( SecurityDAO.getAvailableScopes (ctx) );
		return new Workplace()
			.setDomain(ctx.getDomainId())
			.setEnterprise(company.getId())
			.setDescription(faker.beer().name())
			.setScope(scope.getId())
			.setAddress(company.getAddresses().getFirst().getId());
	}
	
	public static Warehouse getWarehouse(AONContext ctx) {
		Workplace workplace = WorkplaceDAO.getWorkplace(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(workplace == null || workplace.getId() == null) workplace = WorkplaceDAO.insert(ctx, getWorkplace(ctx));
		return new Warehouse()
			.setDomain(ctx.getDomainId())	
			.setWorkplace(workplace.getId())
			.setName(faker.beer().name())
			.setActive(true);
	}

	public static Delivery getDelivery(AONContext ctx) {
		Customer customer = CustomerDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(customer.isEmpty()) customer = CustomerDAO.save(ctx, getCustomer(ctx));
		
		Workplace workplace = WorkplaceDAO.getWorkplace(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(workplace == null || workplace.getId() == null) workplace = WorkplaceDAO.insert(ctx, getWorkplace(ctx));
		
		
		String series = "TEST";
		int number = DeliveryDAO.getNextNumber(ctx, series);
		return new Delivery()
				.setDomain(ctx.getDomainId())
				.setProject(new Project())
				.setDate(new Date())
				.setSeries(series)
				.setNumber(number)
				.setCustomer(customer)
				.setWorkplace(workplace)
				.setStatus(DeliveryStatus.PENDING)
				.setScope(customer.getScope());		
	}
	
	public static DeliveryDetail getDeliveryDetail(AONContext ctx, Delivery delivery) {
		Item item = ItemDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(item == null || item.getId() == null) item = ItemDAO.save(ctx, getItem(ctx));
		
		Warehouse warehouse = WarehouseDAO.getWarehouse(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(warehouse == null || warehouse.getId() == null) warehouse = WarehouseDAO.save(ctx, getWarehouse(ctx));
		
		return new DeliveryDetail()
				.setDelivery(delivery)
				.setDomain(ctx.getDomainId())
				.setItem(item)
				.setLine((short) 1)
				.setDescription(faker.beer().name())
				.setWarehouse(warehouse.getId())
				.setQuantity(1.0)
				.setPrice(1.0)
				.setDiscountExpression("0.0");	
	}
	
	public static Sales getSales(AONContext ctx) {
		Customer customer = CustomerDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(customer.isEmpty()) customer = CustomerDAO.save(ctx, getCustomer(ctx));
		
		Workplace workplace = WorkplaceDAO.getWorkplace(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(workplace == null || workplace.getId() == null) workplace = WorkplaceDAO.insert(ctx, getWorkplace(ctx));
		
		String series = "TEST";
		int number = SalesDAO.getNextNumber(ctx, series);
		return new Sales()
				.setDomain(ctx.getDomainId())
				.setDocumentType(SalesType.NORMAL)
				.setSecurityLevel(SecurityLevel.OFFICIAL)
				.setDate(new Date())
				.setSeries(series)
				.setNumber(number)
				.setCustomer(customer)
				.setWorkplace(workplace)
				.setStatus(SalesStatus.PENDING)
				.setShippingPeriod(ShipmentPeriod.NOON)
				.setScope(customer.getScope());		
	}
	
	public static SalesDetail getSalesDetail(AONContext ctx) {
		return getSalesDetail(ctx, null);
	}
	
	public static SalesDetail getSalesDetail(AONContext ctx, Sales sales) {
		if(sales == null || sales.isEmpty()) {
			sales = SalesDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
			if(sales == null || sales.getId() == null) sales = SalesDAO.save(ctx, getSales(ctx));
		}
		Item item = ItemDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(item == null || item.getId() == null) item = ItemDAO.save(ctx, getItem(ctx));
		return new SalesDetail()
				.setSales(sales)
				.setDomain(ctx.getDomainId())
				.setLine((short) 1)
				.setItem(new Item().setId(item.getId()))
				.setDescription(faker.beer().name())
				.setQuantity(1.0)
				.setPrice(1.0)
				.setDiscountExpression("0.0")
				.setStatus(SalesDetailStatus.PENDING);		
	}
	
	public static Offer getOffer(AONContext ctx) {
		Target target = TargetDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(target.isEmpty()) target = TargetDAO.save(ctx, getTarget(ctx));
		
		Supplier supplier = SupplierDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(supplier.isEmpty()) supplier = SupplierDAO.save(ctx, getSupplier(ctx));
		
		Workplace workplace = WorkplaceDAO.getWorkplace(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(workplace == null || workplace.getId() == null) workplace = WorkplaceDAO.insert(ctx, getWorkplace(ctx));
		
		String series = "TEST";
		int number = OfferDAO.getNextNumber(ctx, series);
		
		return new Offer()
				.setDomain(ctx.getDomainId())
				.setIssueDate(new Date())
				.setSeries(series)
				.setNumber(number)
				.setType(OfferType.NORMAL)
				.setVersion(0)
				.setStatus(OfferStatus.PENDING)
				.setTarget(target)
				.setSupplier(supplier)
				.setScope(target.getScope())
				.setWorkPlace(workplace);		
	}
	
	public static OfferDetail getOfferDetail(AONContext ctx) {
		Offer offer = OfferDAO.getOffer(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(offer == null || offer.getId() == null) offer = OfferDAO.insertOffer(ctx, getOffer(ctx));
		
		return new OfferDetail()
				.setOffer(offer)
				.setDomain(ctx.getDomainId())
				.setLine((short) 1)
				.setDescription(faker.beer().name())
				.setQuantity(1.0)
				.setPrice(1.0)
				.setDiscountExpression("0.0")
				.setStatus(OfferDetailStatus.PENDING);		
	}
	
	
	public static Project getProject( AONContext ctx ) {
		ProjectType type = ProjectTypeDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(type.isEmpty()) type = ProjectTypeDAO.save(ctx, getProjectType(ctx));
		
		Registry registry = RegistryDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(registry.isEmpty()) registry = RegistryDAO.save(ctx, getRegistry(ctx));
		
		return new Project()
			.setDomain(new Domain().setId(ctx.getDomainId()))
			.setName(faker.gameOfThrones().dragon())
			.setAlias(AonRandom.alias(20, PROJECT.ALIAS.getDataType().length()))
			.setRegistry(registry)
			.setType(type)
			.setDate(new Date())
			.setTas(false)
			.setCommercial(false)
			.setReservation(false)
			.setActive(true);
	}
	
	public static ProjectType getProjectType( AONContext ctx ) {
		return new ProjectType()
			.setDomain(ctx.getDomainId())
			.setDescription(faker.gameOfThrones().dragon())
			.setActive(true);
	}
	
	public static ProjectHolder getProjectHolder(AONContext ctx) {
		TaskHolder th = TaskHolderDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(th.isEmpty()) th = TaskHolderDAO.save(ctx, getTaskHolder(ctx));
		
		Workgroup wg = WorkgroupDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(wg.isEmpty()) wg = WorkgroupDAO.save(ctx, getWorkgroup(ctx));
		
		Project p = ProjectDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(p.isEmpty()) p = ProjectDAO.save(ctx, getProject(ctx));
		
		return new ProjectHolder()
			.setDomain(ctx.getDomainId())
			.setProject(p.getId())
			.setStartDate(new Date())
			.setWorkgroup(wg)
			.setTaskHolder(th);
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

