package com.esferalia.aon.occam.test.faker;

import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;

import java.util.Date;
import java.util.Locale;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
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
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
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
}

