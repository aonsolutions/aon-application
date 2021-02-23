package com.esferalia.aon.occam.test.faker;

import java.util.Locale;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.product.Tariff;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.MediaType.IMediaTypeVisitor;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.github.javafaker.Faker;

public class AonFaker {
	private static Faker faker = new Faker(new Locale("es"));
	private static String documentRegexp = "(\\d|[XYZ])\\d{7}[A-Z]";
	
	public static Registry getRegistry( AONContext ctx ) {
		Registry registry = new Registry();
		registry.setDomain(new Domain().setId(ctx.getDomainId()));
		registry.setDocument(faker.regexify(documentRegexp));
		registry.setDocumentType( AonRandom.randomEnum(DocumentType.class) );
		registry.setDocumentCountry( AonRandom.b(95) ? Country.ES: AonRandom.randomEnum(Country.class));
		registry.setName( faker.company().name() );
		registry.setAlias( faker.company().profession() );
		registry.setNationality( AonRandom.b(95) ? Country.ES: AonRandom.randomEnum(Country.class));
		registry.setConfidential( !AonRandom.b(98) );
		return registry;
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
			.setSurcharge( AonRandom.b(10) )
			.setWithholding( AonRandom.b(10) )
			.setTransaction( AonRandom.b(90) ? InvoiceTransactionType.NATIONAL : AonRandom.randomEnum(InvoiceTransactionType.class))
			.setStatus( AonRandom.b(90) ? RegistryStatus.ACTIVE: AonRandom.randomEnum(RegistryStatus.class))
			.setScope( scope == null ? null : scope.getId() )
			.setEInvoice( AonRandom.b(40) )
			// TODO
			.setInvoicingGroup( null )
			.setProjectGrouped( AonRandom.b(4) )
			.setDeliveryGrouped( AonRandom.b(90) )
			.setDeliveryValuated( AonRandom.b(90) )
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
			.setWithholding( AonRandom.b(10) )
			.setVatAccrualPayment( AonRandom.b(98) )
			.setTransaction( AonRandom.b(90) ? InvoiceTransactionType.NATIONAL : AonRandom.randomEnum(InvoiceTransactionType.class))
			.setStatus( AonRandom.b(90) ? RegistryStatus.ACTIVE: AonRandom.randomEnum(RegistryStatus.class))
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
			.setWithholding( AonRandom.b(10) )
			.setWithholdingFarmer( AonRandom.b(99) )
			.setVatAccrualPayment( AonRandom.b(98) )
			.setTransaction( AonRandom.b(90) ? InvoiceTransactionType.NATIONAL : AonRandom.randomEnum(InvoiceTransactionType.class))
			.setStatus( AonRandom.b(90) ? RegistryStatus.ACTIVE: AonRandom.randomEnum(RegistryStatus.class))
			.setScope( scope == null ? null : scope.getId() )
			.setPurchaseValuated(AonRandom.b(50) )
			.setAccount( account == null? null : account.getId() );
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
		MediaType mediaType = AonRandom.randomEnum(MediaType.class); 
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
		media.setComment( AonRandom.b(25) ? faker.lorem().characters(0, 64) : null); 
		
		media.setAdministrative(AonRandom.b(95));
		media.setCommercial(AonRandom.b(55));
		media.setTechnical(AonRandom.b(25));
		Integer registryId = (registry !=null)?registry.getId():null;
		if (registryId != null && AonRandom.b(15)) {
			RegistryAddress address = AonRandom.getRegistryAddress(ctx, f-> f.getRegistryProperty().eq(registryId));
			if (address != null) {
				media.setRaddress(address.getId());
			}
		}
		return media;
	}

	public static Tariff getTariff( AONContext ctx ) {
		Tariff tariff = new Tariff();
		tariff.setDomain(ctx.getDomainId());
		tariff.setCode( faker.number().digits( 6) );
		tariff.setName( faker.commerce().productName());
		tariff.setPurchase( AonRandom.b(10) );
		tariff.setDiscount( AonRandom.getDouble(0, 100, 2));
		tariff.setActive( !AonRandom.b(98) );
		return tariff;
	}
	
}

