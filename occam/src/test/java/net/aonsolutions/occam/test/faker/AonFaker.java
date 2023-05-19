package net.aonsolutions.occam.test.faker;

import java.util.Locale;

import com.github.javafaker.Faker;

import net.aonsolutions.occam.api.accounting.Account;
import net.aonsolutions.occam.api.config.Audit;
import net.aonsolutions.occam.api.config.Booking;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.config.DomainAudit;
import net.aonsolutions.occam.api.config.GeoZone;
import net.aonsolutions.occam.api.config.Registry;
import net.aonsolutions.occam.api.config.Scope;
import net.aonsolutions.occam.api.config.User;
import net.aonsolutions.occam.api.constants.Country;

public class AonFaker {
	private static Faker faker = Faker.instance(Locale.of("es"));
	private static final String DOCUMENT_REGEXP = "(\\d|[XYZ])\\d{7}[A-Z]";

	public static Account getAccount(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)?getAccount():null;
	}
	public static Account getAccount( ) {
		return  new Account()
			.setId(AonRandom.integer(50))
			.setDomain(AonRandom.integer(50))
			.setCode( faker.regexify("\\d{9}") )
			.setDescription( faker.animal().name() )
			.setAlias( faker.animal().name() )
			.setActive( !AonRandom.gt(3) );
	}

	public static Audit getAudit() {
		return new Audit()
			.setCreationUser(AonRandom.string(50, 10))
			.setCreationDate(AonRandom.getPastDate(50))
			.setModificationUser(AonRandom.string(50, 10))
			.setModificationDate(AonRandom.getPastDate(50));
	}

	public static Booking getBooking() {
		return new Booking()
			.setOwner((AonRandom.string(50)))
			.setExpirationDate(AonRandom.getPastDate(50))
			.setDomainManagement(AonRandom.gt(50))
			.setDisableDomainManagement(AonRandom.gt(50))
			.setMaxDefinedUsers(AonRandom.integer(50))
			.setAonCustomer(AonRandom.integer(50))
			.setAonStatus(AonRandom.getAonStatus(20).orElse(null))
		;
	}

	public static Domain getDomain() {
		return getDomain(false); 
	}
	public static Domain getDomain( boolean bookingRequired) {
		return  new Domain()
			.setId(AonRandom.integer(50))
			.setName(AonRandom.domainName())
			.setDescription(AonRandom.lorem(50))
			.setParent( AonRandom.gt(50) ? getDomain() : null)
			.setType(AonRandom.getDomainType(20).orElse(null))
			.setEnableHeredity(AonRandom.gt(50))
			.setActive(AonRandom.gt(50))
			.setId(AonRandom.integer(50))
			.setBooking( bookingRequired || AonRandom.gt(50) ? getBooking() : null)
			.setAudit( AonRandom.gt(50) ? getDomainAudit() : null)
		;
	}

	public static DomainAudit getDomainAudit() {
		return new DomainAudit()
			.setLastAccessUser(AonRandom.string(50, 10))
			.setLastAccessDate(AonRandom.getPastDate(50))
			.setCreationUser(AonRandom.string(50, 10))
			.setCreationDate(AonRandom.getPastDate(50))
			.setModificationUser(AonRandom.string(50, 10))
			.setModificationDate(AonRandom.getPastDate(50));
	}
	
	public static GeoZone getGeoZone( ) {
		return new GeoZone()
			.setId(AonRandom.integer(50))
			.setDomain(AonRandom.integer())
			.setName(faker.country().name())
			.setCode(faker.country().countryCode2())
			;
	}

	public static Registry getRegistry(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)?getRegistry():null;
	}
	public static Registry getRegistry( ) {
		return  new Registry()
			.setId(AonRandom.integer(50))
			.setDomain(AonRandom.integer(50))
			.setDocument(faker.regexify(DOCUMENT_REGEXP))
			.setDocumentType( AonRandom.getDocumentType(90).orElse(null) )
			.setDocumentCountry( AonRandom.gt(5) ? Country.ES: AonRandom.getCountry(10).orElse(null))
			.setName( faker.company().name() )
			.setAlias( faker.company().profession() )
			.setNationality( AonRandom.gt(5) ? Country.ES: AonRandom.getCountry(50).orElse(null))
			.setConfidential( !AonRandom.gt(3) );
	}
	
	public static Scope getScope(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)?getScope():null;
	}
	public static Scope getScope( ) {
		return new Scope()
			.setId(AonRandom.integer(50))
			.setDomain(AonRandom.integer(50))
			.setDescription(faker.pokemon().name());
	}
	
	public static User getUser( ) {
		return  new User()
			.setId(AonRandom.integer(50))
			.setDomain(AonRandom.integer(50))
			.setName(faker.pokemon().name())
			.setLogin(AonRandom.string(50, 10))
			.setActive(AonRandom.gt(50));
	}
	
}
