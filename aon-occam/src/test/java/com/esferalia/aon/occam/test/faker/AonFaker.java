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
import com.esferalia.aon.occam.api.model.Series;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.catalogue.Catalogue;
import com.esferalia.aon.occam.api.model.catalogue.CatalogueCategory;
import com.esferalia.aon.occam.api.model.catalogue.CatalogueItem;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.InvoiceData;
import com.esferalia.aon.occam.api.model.finance.InvoiceDataName;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
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
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
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
import com.esferalia.aon.occam.api.model.seres.EdiCodes;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskHolderType;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.CarrierStatus;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.type.DocumentType;
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
import com.esferalia.aon.occam.api.model.type.ShipmentStatus;
import com.esferalia.aon.occam.api.model.type.TargetStatus;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.WorkgroupStatus;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.impl.jooq.dao.CarrierDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CatalogueDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO;
import com.esferalia.aon.occam.impl.jooq.dao.OfferDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductCategoryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectTypeDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDetailDAO;
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

	public static Catalogue getCatalogue( AONContext ctx ) {
		return new Catalogue()
			.setDomain(ctx.getDomainId())
			.setName(faker.commerce().department())
			.setPurchase(AonRandom.gt(50))
			.setStart(new Date());
	}

	public static CatalogueItem getCatalogueItem( AONContext ctx ) {
		Catalogue catalogue = CatalogueDAO.getStream(ctx, p -> p.getDomainProperty().eq(ctx.getDomainId()))
			.findFirst().orElse(null);
		if (catalogue == null || catalogue.getId() == null) catalogue = CatalogueDAO.insert(ctx, getCatalogue(ctx));

		Product product = ProductDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if (product == null || product.getId() == null) product = ProductDAO.save(ctx, getProduct(ctx));

		return new CatalogueItem()
			.setDomain(ctx.getDomainId())
			.setCatalogue(catalogue.getId())
			.setProduct(product.getId())
			.setItem(null)
			.setQuantity(AonRandom.getDouble(0, 100, 4))
			.setPrice(AonRandom.getDouble(0, 1000, 4))
			.setDiscount(AonRandom.getDouble(0, 100, 2));
	}

	public static CatalogueCategory getCatalogueCategory( AONContext ctx ) {
		Catalogue catalogue = CatalogueDAO.getStream(ctx, p -> p.getDomainProperty().eq(ctx.getDomainId()))
			.findFirst().orElse(null);
		if (catalogue == null || catalogue.getId() == null) catalogue = CatalogueDAO.insert(ctx, getCatalogue(ctx));

		ProductCategory category = ProductCategoryDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if (category == null || category.getId() == null) category = ProductCategoryDAO.insert(ctx, getProductCategory(ctx));

		return new CatalogueCategory()
			.setDomain(ctx.getDomainId())
			.setCatalogue(catalogue.getId())
			.setCategory(category.getId())
			.setQuantity(AonRandom.getDouble(0, 100, 4))
			.setDiscount(AonRandom.getDouble(0, 100, 2));
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
	
	public static AmortizationType getAmortizationType(AONContext ctx) {
		return new AmortizationType()
				.setDomain(new Domain() 
				.setId(ctx.getDomainId())
				.setName(AonRandom.string(-1, 1, 14))) //creo q no hace falta, comprobar luego
				.setFixedAssetAccount(	AonRandom.string(-1, 1, 4))
				.setAccumulatedAccount(AonRandom.string(-1, 1, 4))
				.setAllocationAccount(AonRandom.string(-1, 1, 4))
				.setPercentage(AonRandom.getDouble(0, 15))
				.setDescription(faker.beer().name());
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
		if(workplace == null || workplace.getId() == null) workplace = WorkplaceDAO.save(ctx, getWorkplace(ctx));
		return new Warehouse()
			.setDomain(ctx.getDomainId())	
			.setWorkplace(workplace.getId())
			.setName(AonRandom.string(0, 1, 30))
			.setActive(true);
	}
	
	public static EdiCodes getEdiCodes(AONContext ctx) {
		return new EdiCodes()
				.setBycode(Faker.instance().letterify("?????????"))
				.setCompanyEdiCode(Faker.instance().letterify("?????????"))
				.setCustomerEdiCode(Faker.instance().letterify("?????????"))
				.setCustomerEdiHeader(Faker.instance().letterify("?????????"))
				.setCustomerEdiInvoice(Faker.instance().letterify("?????????"))
				.setCustomerEdiPoint(Faker.instance().letterify("?????????"))
				.setCustomerPackage(Faker.instance().book().title())
				.setDeliveryPointEdiCode(Faker.instance().letterify("?????????"))
				.setDepartment(Faker.instance().job().field())
				.setDpcode(Faker.instance().letterify("?????????"))
				.setIvcode(Faker.instance().letterify("?????????"))
				.setMrcode(Faker.instance().letterify("?????????"))
				.setMscode(Faker.instance().letterify("?????????"))
				.setPwcode(Faker.instance().letterify("?????????"))
				.setShcode(Faker.instance().letterify("?????????"))
				.setSucode(Faker.instance().letterify("?????????"))
				.setUccode(Faker.instance().letterify("?????????"));
	}
	
	public static Delivery getDelivery(AONContext ctx) {
		Random random = new Random();

		Registry registry = AonFaker.getRegistry(ctx);
		
		RegistryAddress registryAddress = AonFaker.getRegistryAddress(ctx);
		registryAddress.setRegistry(registry.getId());
		
		BankAccount bankAccount = AonFaker.getBankAccount(ctx);
		
		SecurityLevel[] securityLevelValues = SecurityLevel.values();
		ShipmentPeriod[] shipmentPeriodValues = ShipmentPeriod.values();
		ShipmentStatus[] shipmentStatusValues = ShipmentStatus.values();
		DeliveryStatus[] deliveryStatusValues = DeliveryStatus.values();
		
		Integer domain = ctx.getDomainId();
		
		Carrier carrier = CarrierDAO.get(ctx, f -> f.getDomainProperty().eq(domain));
		
		return new Delivery()
				.setAddress(registryAddress)
				.setBankAccount(bankAccount.getIban())
				.setBankAlias(Faker.instance().zelda().character())
				.setBic(Faker.instance().finance().bic())
				.setCarrier(carrier.getId())
				.setComments(Faker.instance().gameOfThrones().quote())
				.setConfidential(random.nextBoolean())
				.setCreationDate(new Date())
				.setCreationUser(ctx.getUser())
				.setCustomer(AonRandom.getCustomer(ctx))
				.setDate(new Date())
				.setDaysBetweenPymnt((short) random.nextInt(0, 9))
				.setDaysToFirstPymnt((short) random.nextInt(0, 9))
				.setDetails(new LinkedList<>())
				.setDomain(ctx.getDomainId())
				.setDriver(Faker.instance().lordOfTheRings().character())
				.setDriverDocument(Faker.instance().letterify("?????????"))
				.setEdiCodes(AonFaker.getEdiCodes(ctx))
				.setModificationDate(new Date())
				.setModificationUser(ctx.getUser())
				.setNumber(Integer.parseInt(Faker.instance().numerify("#####").toString()))
				.setNumberOfPymnts((short) random.nextInt(0, 9))
				.setNumberPlate(Faker.instance().numerify("#####").toString())
				.setPackaging(new LinkedList<>())
				.setPayMethod(AonFaker.getPayMethod(ctx))
				.setProject(AonFaker.getProject(ctx))
				.setPymntDays(Faker.instance().numerify("##"))
				.setRemarks(Faker.instance().zelda().game())
				.setScope(SecurityDAO.getAvailableScopes(ctx).getFirst())
				.setSecurityLevel(securityLevelValues[random.nextInt(securityLevelValues.length)])
				.setSeries(Faker.instance().numerify("#####").toString())
				.setShippingAlternativeAddress(Faker.instance().address().fullAddress())
				.setShippingAlternativeAddress2(Faker.instance().address().fullAddress())
				.setShippingAlternativeCity(Faker.instance().address().cityName())
				.setShippingAlternativePhone(Faker.instance().phoneNumber().toString())
				.setShippingAlternativeRecipient(Faker.instance().address().secondaryAddress())
				.setShippingAlternativeZip(Faker.instance().address().zipCode())
				.setShippingContact(Faker.instance().friends().character())
				.setShippingPeriod(shipmentPeriodValues[random.nextInt(shipmentPeriodValues.length)])
				.setShippingStatus(shipmentStatusValues[random.nextInt(shipmentStatusValues.length)])
				.setStatus(deliveryStatusValues[random.nextInt(deliveryStatusValues.length)])
				.setStatusModificationDate(new Date())
				.setTotalPackages(Double.parseDouble(Faker.instance().numerify("#####")))
				.setTotalWeight(Double.parseDouble(Faker.instance().numerify("#####").toString()))
				.setTrackingNumber(Faker.instance().numerify("#####").toString())
				.setWorkplace(AonFaker.getWorkplace(ctx));
	}
	
	public static DeliveryDetail getDeliveryDetail(AONContext ctx, Delivery delivery) {
		Item item = ItemDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(item == null || item.getId() == null) item = ItemDAO.save(ctx, getItem(ctx));
		
		Warehouse warehouse = WarehouseDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(warehouse == null || warehouse.getId() == null) warehouse = WarehouseDAO.save(ctx, getWarehouse(ctx));
		
		SalesDetail salesDetail = SalesDetailDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		
		Random random = new Random();
		
		return new DeliveryDetail()
				.setCreationDate(new Date())
				.setCreationUser(ctx.getUser())
				.setDelivery(delivery)
				.setDescription(Faker.instance().gameOfThrones().quote())
				.setDiscountExpression("0.0")
				.setDomain(ctx.getDomainId())
				.setItem(item)
				.setLine((short) random.nextInt(0, 9))
				.setModificationDate(new Date())
				.setModificationUser(ctx.getUser())
				.setPrice(Double.parseDouble(Faker.instance().numerify("#####")))
				.setPurchaseReference(Faker.instance().letterify("?????????"))
				.setQuantity(Double.parseDouble(Faker.instance().numerify("#####")))
				.setSalesDetail(salesDetail.getId())
				.setWarehouse(warehouse.getId());	
	}
	
	public static Sales getSales(AONContext ctx) {
		Customer customer = CustomerDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(customer.isEmpty()) customer = CustomerDAO.save(ctx, getCustomer(ctx));
		
		Workplace workplace = WorkplaceDAO.getWorkplace(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		if(workplace == null || workplace.getId() == null) workplace = WorkplaceDAO.save(ctx, getWorkplace(ctx));
		
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
		if(workplace == null || workplace.getId() == null) workplace = WorkplaceDAO.save(ctx, getWorkplace(ctx));
		
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
	
	public static String getValidIban(AONContext ctx) {
		String value = getCountry(ctx).toString() + Faker.instance().numerify("################################");

		Country country = Country.safeValueOf(AonStringUtils.substring(value, 0, 2));
		value = AonStringUtils.substring(value, 0, country.getIbanLength());
		
		BankAccount bankAccount = new BankAccount(value);
		
		if (country == Country.ES) {
			// bban is valid
			bankAccount = new BankAccount(value);
			String controlDigit = bankAccount.calculateBbanControlDigit();
			
			value = AonStringUtils.substring(value, 0, 12) + controlDigit + AonStringUtils.substring(value, 14, 24);
			
			// iban is valid
			bankAccount = new BankAccount(value);
			controlDigit = bankAccount.calculateIbanControlDigit();
			
			value = AonStringUtils.substring(value, 0, 2) + controlDigit + AonStringUtils.substring(value, 4, 24);
		}
		
		return value;
	}
	
	public static BankAccount getBankAccount(AONContext ctx) {
		BankAccount bankAccount = new BankAccount(AonFaker.getValidIban(ctx));
		
		if (bankAccount.getCountry() == Country.ES) {
			bankAccount.setBban6(AonStringUtils.EMPTY);
			bankAccount.setBban7(AonStringUtils.EMPTY);
			bankAccount.setBban8(AonStringUtils.EMPTY);
		}

		return bankAccount;		
	}
	
	public static RegistryBank getRegistryBank(AONContext ctx) {	
		Random random = new Random();
		
		String bic = Faker.instance().finance().bic();
		if (bic.length() > RBANK.BIC.getDataType().length()) {
			bic = bic.substring(0, RBANK.BIC.getDataType().length());
		}
		
		String suffix = AonRandom.string(50,RBANK.SUFIX.getDataType().length());
		String alias = AonRandom.string(50,RBANK.ALIAS.getDataType().length());
		String requisition = AonRandom.string(50,RBANK.REQUISITION.getDataType().length());
		String sepaMandateRef = AonRandom.string(50,RBANK.SEPA_MANDATE_REF.getDataType().length());		
		
		return new RegistryBank()
				.setDomain(ctx.getDomainId())
				.setAccount(AonRandom.getAccount(ctx, 572))
				.setActive(random.nextBoolean())
				.setAlias(alias)
				.setBankAccount(AonFaker.getBankAccount(ctx))
				.setBic(bic)
				.setRegistry(AonRandom.getRegistry(ctx).getId())
				.setRequisition(requisition)
				.setSepaMandateRef(sepaMandateRef)
				.setSuffix(suffix);
	}
	
	public static Carrier getCarrier(AONContext ctx) {
		Registry registry = getRegistry();
		return getCarrier(ctx, registry);
	}
	
	public static Carrier getCarrier(AONContext ctx, Registry registry) {
		Random random = new Random();
		CarrierStatus[] status = CarrierStatus.values();
		
		return new Carrier()
				.copy(registry)
				.setId(Integer.parseInt(Faker.instance().numerify("#####")))
				.setScope(getScope())
				.setStatus(status[random.nextInt(status.length)]);
	}
	
	/** Methods independent from AONContext **/
	
	public static Domain getDomain() {
		return new Domain()
			.setId(Integer.parseInt(Faker.instance().numerify("#####")));
	}
	
	public static Scope getScope() {
		return new Scope()
				.setDescription(Faker.instance().gameOfThrones().quote())
				.setDomain(getDomain().getId())
				.setId(Integer.parseInt(Faker.instance().numerify("#####")));
	}
	
	public static Registry getRegistry() {
		Random random = new Random();
		
		Country[] countries = Country.values();
		DocumentType[] documentTypes = DocumentType.values();
		SecurityLevel[] securityLevels = SecurityLevel.values();
		
		Country country = countries[random.nextInt(countries.length)];
		
		return new Registry()
				.setAlias(Faker.instance().zelda().character())
				.setConfidential(random.nextBoolean())
				.setDirty(random.nextBoolean())
				.setDocument(Faker.instance().bothify("?########").toUpperCase())
				.setDocumentCountry(country)
				.setDocumentType(documentTypes[random.nextInt(documentTypes.length)])
				.setDomain(getDomain())
				.setGlobal(random.nextBoolean())
				.setId(Integer.parseInt(Faker.instance().numerify("#####")))
				.setLegalPerson(random.nextBoolean())
				.setName(Faker.instance().friends().character())
				.setNationality(country)
				.setSecurityLevel(securityLevels[random.nextInt(securityLevels.length)])
				.setSelected(random.nextBoolean());
	}
	
	public static Workplace getWorkplace() {
		Random random = new Random();
		
		Administration[] administrations = Administration.values();
		return new Workplace()
				.setActive(random.nextBoolean())
				.setAddress(Integer.parseInt(Faker.instance().numerify("#####")))
				.setCustomer(getCustomer().getId())
				.setDescription(Faker.instance().zelda().game())
				.setDomain(getDomain().getId())
				.setEconomicAgreement(administrations[random.nextInt(administrations.length)])
				.setEnterprise(Integer.parseInt(Faker.instance().numerify("#####")))
				.setId(Integer.parseInt(Faker.instance().numerify("#####")))
				.setScope(getScope().getId())
				;
	}
	
	public static Warehouse getWarehouse() {
	
		return new Warehouse()
				.setActive(true)
				.setDepartment(Integer.parseInt(Faker.instance().numerify("#####")))
				.setDomain(getDomain().getId())
				.setName(Faker.instance().job().field() + " S.A")
				.setWorkplace(getWorkplace().getId());
	}
	
	public static Project getProject() {
		Random random = new Random();
		
		return new Project()
				.setActive(random.nextBoolean())
				.setAlias(Faker.instance().lordOfTheRings().character())
				.setCommercial(random.nextBoolean())
				.setDate(Faker.instance().date().birthday())
				.setDirty(false)
				.setDomain(new Domain())
				.setId(Integer.parseInt(Faker.instance().numerify("#####")))
				.setName(Faker.instance().cat().name())
				.setProjectActivities(null)
				.setProjectHolder(new ProjectHolder())
				.setRegistry(new Registry())
				.setReservation(random.nextBoolean())
				.setTas(random.nextBoolean())
				.setType(new ProjectType())
				;
	}
	
	public static RegistryAddress getRegistryAddress(Registry registry) {
		Random random = new Random();
		
		Country[] countries = Country.values();
		
		return new RegistryAddress()
				.setAddress(Faker.instance().address().streetAddress())
				.setAddress2(Faker.instance().address().streetAddress())
				.setAddress3(Faker.instance().address().streetAddress())
				.setCity(Faker.instance().address().city())
				.setProvince(Faker.instance().address().country())
				.setZip(Faker.instance().address().zipCode())
				.setCountry(countries[random.nextInt(countries.length)])
				.setRegistry(registry.getId())
				.setMain(true)
				;
	}
	
	public static RegistryMedia getRegistryMedia(Registry registry) {
		Random random = new Random();
		
		MediaType[] mediaType = MediaType.values();
		MediaType media = mediaType[random.nextInt(mediaType.length)];
		String value;
		
		switch(media) {
			case FIXED_PHONE:
				value = Faker.instance().phoneNumber().phoneNumber(); break;
			case CELLULAR:
				value = Faker.instance().phoneNumber().cellPhone(); break;
			case FAX:
				value = Faker.instance().phoneNumber().phoneNumber(); break;
			case EMAIL:
				value = Faker.instance().internet().emailAddress(); break;
			case WEB:
				value = Faker.instance().internet().url(); break;
			default:
				value = Faker.instance().phoneNumber().phoneNumber();
		}
		
		return new RegistryMedia()
				.setMedia(media)
				.setValue(value)
				.setRegistry(registry.getId());
	}
	
	public static LinkedList<RegistryMedia> getRegistryMediaList(Registry registry) {
		RegistryMedia registryMedia1 = new RegistryMedia()
				.setMedia(MediaType.FIXED_PHONE)
				.setValue(Faker.instance().phoneNumber().phoneNumber());
		
		RegistryMedia registryMedia2 = new RegistryMedia()
				.setMedia(MediaType.CELLULAR)
				.setValue(Faker.instance().phoneNumber().cellPhone());
		
		RegistryMedia registryMedia3 = new RegistryMedia()
				.setMedia(MediaType.FAX)
				.setValue(Faker.instance().phoneNumber().phoneNumber());
		
		RegistryMedia registryMedia4 = new RegistryMedia()
				.setMedia(MediaType.EMAIL)
				.setValue(Faker.instance().internet().emailAddress());
		
		RegistryMedia registryMedia5 = new RegistryMedia()
				.setMedia(MediaType.WEB)
				.setValue(Faker.instance().internet().url());
		
		LinkedList<RegistryMedia> list = new LinkedList<>();
		
		list.add(registryMedia1);
		list.add(registryMedia2);
		list.add(registryMedia3);
		list.add(registryMedia4);
		list.add(registryMedia5);
		
		return list;
	}
	
	public static DeliveryDetail getDeliveryDetail() {
		Random random = new Random();
		
		return new DeliveryDetail()
				.setSalesDetail(random.nextInt(0, 9))
				.setDescription(Faker.instance().food().ingredient())
				.setPrice(Double.parseDouble(Faker.instance().numerify("##.#").toString()))
				.setDiscountExpression("0.0")
				.setQuantity(random.nextInt(1, 9))
				.setPurchaseReference(Faker.instance().bothify("P##/######"))
				.setCreationDate(new Date())
				.setPurchaseReference(Faker.instance().numerify("##########"))
				.setItem(getItem());
	}
	
	public static Item getItem() {
		return new Item()
				.setProduct(getProduct());
	}
	
	public static Product getProduct() {
		Random random = new Random();
		Integer[] taxes = {4,10,21};
		
		return new Product()
				.setVat(new Tax()
						.setPercentage(taxes[random.nextInt(taxes.length)]));
	}
	
	public static Delivery getDelivery() {
		Random random = new Random();
		
		Registry registry = getRegistry();
		
		DeliveryStatus[] deliveryStatus = DeliveryStatus.values();
		
		LinkedList<DeliveryDetail> details = new LinkedList<>();
		DeliveryDetail deliveryDetail1 = getDeliveryDetail();
		DeliveryDetail deliveryDetail2 = getDeliveryDetail();
		DeliveryDetail deliveryDetail3 = getDeliveryDetail();
		DeliveryDetail deliveryDetail4 = getDeliveryDetail();
		DeliveryDetail deliveryDetail5 = getDeliveryDetail();
		DeliveryDetail deliveryDetail6 = getDeliveryDetail();
		DeliveryDetail deliveryDetail7 = getDeliveryDetail();
		DeliveryDetail deliveryDetail8 = getDeliveryDetail();
		DeliveryDetail deliveryDetail9 = getDeliveryDetail();
		DeliveryDetail deliveryDetail10 = getDeliveryDetail();
		DeliveryDetail deliveryDetail11 = getDeliveryDetail();
		DeliveryDetail deliveryDetail12 = getDeliveryDetail();
		DeliveryDetail deliveryDetail13 = getDeliveryDetail();
		DeliveryDetail deliveryDetail14 = getDeliveryDetail();
		DeliveryDetail deliveryDetail15 = getDeliveryDetail();
		DeliveryDetail deliveryDetail16 = getDeliveryDetail();
		DeliveryDetail deliveryDetail17 = getDeliveryDetail();
		DeliveryDetail deliveryDetail18 = getDeliveryDetail();
		DeliveryDetail deliveryDetail19 = getDeliveryDetail();
		DeliveryDetail deliveryDetail20 = getDeliveryDetail();
		details.add(deliveryDetail1);
		details.add(deliveryDetail2);
		details.add(deliveryDetail3);
		details.add(deliveryDetail4);
		details.add(deliveryDetail5);
		details.add(deliveryDetail6);
		details.add(deliveryDetail7);
		details.add(deliveryDetail8);
		details.add(deliveryDetail9);
		details.add(deliveryDetail10);
		details.add(deliveryDetail11);
		details.add(deliveryDetail12);
		details.add(deliveryDetail13);
		details.add(deliveryDetail14);
		details.add(deliveryDetail15);
		details.add(deliveryDetail16);
		details.add(deliveryDetail17);
		details.add(deliveryDetail18);
		details.add(deliveryDetail19);
		details.add(deliveryDetail20);

		
		return new Delivery()
				.setDomain(getDomain().getId())
				.setProject(getProject())
				.setDate(Faker.instance().date().birthday())
				.setDetails(details)
				.setSeries(Faker.instance().letterify("????????"))
				.setNumber(Integer.parseInt(Faker.instance().numerify("#####")))
				.setCustomer(AonFaker.getCustomer())
				.setStatus(deliveryStatus[random.nextInt(deliveryStatus.length)])
				.setScope(AonFaker.getScope()) 
				.setAddress(getRegistryAddress(registry))
				.setShippingContact(Faker.instance().zelda().character())
				.setWorkplace(getWorkplace());
	}
	
	public static Customer getCustomer() {
		Random random = new Random();
		
		RegistryStatus[] valuesRegistryStatus = RegistryStatus.values();
		InvoiceTransactionType[] valuesInvoiceTransactionType = InvoiceTransactionType.values();
		
		return new Customer()
				.copy(getRegistry())
				.setAccount(Integer.parseInt(Faker.instance().numerify("#####")))
				.setBillable(random.nextBoolean())
				.setCreationDate(Faker.instance().date().birthday())
				.setCreationUser(Faker.instance().zelda().character())
				.setDeliveryGrouped(random.nextBoolean())
				.setDeliveryValuated(random.nextBoolean())
				.setEInvoice(random.nextBoolean())
				.setId(Integer.parseInt(Faker.instance().numerify("#####")))
				.setInvoicingGroup(Integer.parseInt(Faker.instance().numerify("#####")))
				.setModificationDate(Faker.instance().date().birthday())
				.setModificationUser(Faker.instance().zelda().character())
				.setProjectGrouped(random.nextBoolean())
				.setRelationship(random.nextBoolean())
				.setScope(AonFaker.getScope())
				.setStatus(valuesRegistryStatus[random.nextInt(valuesRegistryStatus.length)])
				.setSurcharge(random.nextBoolean())
				.setTariff(Integer.parseInt(Faker.instance().numerify("#####")))
				.setTransaction(valuesInvoiceTransactionType[random.nextInt(valuesInvoiceTransactionType.length)])
				.setWithholding(random.nextBoolean())
				;
	}
	
	public static CustomerFull getCustomerFull(Customer customer) {
		RegistryAddress registryAddress = getRegistryAddress(customer);
		LinkedList<RegistryAddress> addresses = new LinkedList<>();
		addresses.add(registryAddress);
		
		LinkedList<RegistryMedia> medias = getRegistryMediaList(customer);
		
		return (CustomerFull) new CustomerFull()
				.setRegistry(customer)
				.setAddresses(addresses)
				.setMedias(medias)
				.setBanks(new LinkedList<>())
				.setRecordDatas(new LinkedList<>());
	}
	
	public static Company getCompany() {
		Registry registry = getRegistry();
		registry.setName(Faker.instance().job().field() + " S.A.");
		
		return new Company()
				.copy(registry);
	}
	
	public static CompanyFull getCompanyFull(Company company) {
		RegistryAddress registryAddress = getRegistryAddress(company);
		LinkedList<RegistryAddress> addresses = new LinkedList<>();
		addresses.add(registryAddress);
		
		LinkedList<RegistryMedia> medias = getRegistryMediaList(company);
		
		return (CompanyFull) new CompanyFull()
				.setRegistry(company)
				.setAddresses(addresses)
				.setMedias(medias)
				.setBanks(new LinkedList<>())
				.setRecordDatas(new LinkedList<>());
	}
	
	public static Series getSeries() {
		return new Series()
			.setId( AonRandom.number( 20, 10, 99999 ) )
			.setDomain( AonRandom.getInt( 10, 99999 ) )
			.setDescription( AonRandom.name( 10, 30 )) 
			.setScope( AonRandom.number( 20, 10, 99999 ) ) 
			.setCode( AonRandom.name( 10, 5 ))
			.setActive(faker.random().nextBoolean() )
			.setTas(faker.random().nextBoolean() )
			.setOffer(faker.random().nextBoolean() )
			.setSales(faker.random().nextBoolean() )
			.setDelivery(faker.random().nextBoolean() )
			.setInvoice(faker.random().nextBoolean() )
			.setRectification(faker.random().nextBoolean() )
			.setPos( faker.random().nextBoolean() )
			.setSecurityLevel( AonRandom.getRandomSecurityLevel() );
	}
	
	public static InvoiceSeries getInvoiceSeries() {
		return new InvoiceSeries()
			.setDescription( AonRandom.name( 10, 30 )) 
			.setSales(faker.random().nextBoolean() )
			.setFromNumber( AonRandom.getInt( 0, 99999 ) )
			.setToNumber( AonRandom.getInt( 0, 99999 ) )
			.setCount( AonRandom.getInt( 0, 99999 ) )
			;
	}
	
	public static InvoiceData getInvoiceData(Integer domain, Integer invoice) {
		return new InvoiceData()
			.setDomain(domain)
			.setInvoice(invoice)
			.setName( InvoiceDataName.values()[faker.random().nextInt(InvoiceDataName.values().length)]) 
			.setValue(AonStringUtils.abbreviate(faker.beer().style(), 128))
			.setStartDate(new Date())
			.setEndDate(new Date());
	}
}

