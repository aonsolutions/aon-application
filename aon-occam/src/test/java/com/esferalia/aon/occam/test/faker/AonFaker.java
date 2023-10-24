package com.esferalia.aon.occam.test.faker;

import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Question;
import com.esferalia.aon.occam.api.model.QuestionValue;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
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
import com.esferalia.aon.occam.api.model.registry.QuestionType;
import com.esferalia.aon.occam.api.model.registry.RDirStaff;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskHolderType;
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
			.setStatus(  AonRandom.getRandomProductStatus())
			.setDetail("11")
			.setDetail2("22")
			.setDetail2("33")
			.setPackUnits( AonRandom.number(0,100));
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
	
	public static Question getQuestion(AONContext ctx) {
		QuestionType type = getQuestionType();
			
		QuestionValue questionValue = new QuestionValue()
				.setDomain(ctx.getDomainId())
				.setQuestion(new Question());
		
		if (type.equals(QuestionType.TEXT)) {
			questionValue.setValueText(faker.zelda().character());
		}
		if (type.equals(QuestionType.DATE)) {
			questionValue.setValueDate(faker.date().birthday());
		}
		if (type.equals(QuestionType.NUMBER)) {
			questionValue.setValueNumber(faker.number().randomDouble(2,(int) Double.MIN_VALUE, (int) Double.MAX_VALUE));
		}
		

	
		List<QuestionValue> questionValues = new LinkedList<>();
		questionValues.add(questionValue);

		return new Question()
				.setDomain(ctx.getDomainId())
				.setActive(true)
				.setText(faker.gameOfThrones().quote())
				.setType(type)
				.setArgument(faker.gameOfThrones().house())
				.setAlias(faker.gameOfThrones().character())
				.setValues(questionValues);
	}
	
	public static QuestionType getQuestionType() {
		Random random = new Random();
		QuestionType[] types = QuestionType.values();
		int type = random.nextInt(types.length);
		return types[type];
	}
	
	public static Account getAccount(AONContext ctx) {
		Random random = new Random();
		int b = random.nextInt(0,1);

		return new Account()
			.setActive(true)
			.setAlias(faker.lordOfTheRings().character())
			.setCode(faker.gameOfThrones().quote())
			.setCostCenter(faker.gameOfThrones().character())
			.setDescription(faker.lordOfTheRings().location())
			.setDomain(ctx.getDomainId())
			.setEntryEnabled(random.nextBoolean())
			.setHasRegistry(random.nextBoolean())
			.setLevel((byte) b);
	}
	
	public static Country getCountry(AONContext ctx) {
		Random random = new Random();
		Country[] countries = Country.values();
		Country randomCountry = countries[random.nextInt(countries.length)];
		return randomCountry;
	}
	
	public static BankAccount getBankAccount(AONContext ctx) {
		BankAccount bankAccount = new BankAccount(Faker.instance().finance().iban());
		
		if (bankAccount.getCountry() == null) { bankAccount.setCountry(AonFaker.getCountry(ctx)); }
		if (bankAccount.getCheck() == null) { bankAccount.setCheck(faker.letterify("??")); }
		if (bankAccount.getBban1() == null) { bankAccount.setBban1(faker.letterify("????")); }
		if (bankAccount.getBban2() == null) { bankAccount.setBban2(faker.letterify("????")); }
		if (bankAccount.getBban3() == null) { bankAccount.setBban3(faker.letterify("????")); }
		if (bankAccount.getBban4() == null) { bankAccount.setBban4(faker.letterify("????")); }
		if (bankAccount.getBban5() == null) { bankAccount.setBban5(faker.letterify("????")); }
		if (bankAccount.getBban6() == null) { bankAccount.setBban6(faker.letterify("????")); }
		if (bankAccount.getBban7() == null) { bankAccount.setBban7(faker.letterify("????")); }
		if (bankAccount.getBban8() == null) { bankAccount.setBban8(faker.letterify("????")); }

		return bankAccount;		
	}
	
	public static RegistryBank getRegistryBank(AONContext ctx) {	
		Random random = new Random();
		
		String bic = Faker.instance().finance().bic();
		if (bic.length() > RBANK.BIC.getDataType().length()) {
			bic = bic.substring(0, RBANK.BIC.getDataType().length());
		}
		
		String suffix = faker.friends().character();
		if (suffix.length() > RBANK.SUFIX.getDataType().length()) {
			suffix = suffix.substring(0, RBANK.SUFIX.getDataType().length());
		}
		
		String alias = faker.gameOfThrones().dragon();
		if (alias.length() > RBANK.ALIAS.getDataType().length()) {
			alias = alias.substring(0, RBANK.ALIAS.getDataType().length());
		}
		
		String requisition = faker.rickAndMorty().character();
		if (requisition.length() > RBANK.REQUISITION.getDataType().length()) {
			requisition = requisition.substring(0, RBANK.REQUISITION.getDataType().length());
		}
		
		String sepaMandateRef = faker.gameOfThrones().dragon();
		if (sepaMandateRef.length() > RBANK.SEPA_MANDATE_REF.getDataType().length()) {
			sepaMandateRef = sepaMandateRef.substring(0, RBANK.SEPA_MANDATE_REF.getDataType().length());
		}
		
		
		return new RegistryBank()
				.setDomain(ctx.getDomainId())
				.setAccount(AonRandom.getAccount(ctx, "572"))
				.setActive(random.nextBoolean())
				.setAlias(alias)
				.setBankAccount(AonFaker.getBankAccount(ctx))
				.setBic(bic)
				.setRegistry(AonRandom.getRegistry(ctx).getId())
				.setRequisition(requisition)
				.setSepaMandateRef(sepaMandateRef)
				.setSuffix(suffix);
	}
}

