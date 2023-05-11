package net.aonsolutions.occam.test.faker;

import java.util.Locale;

import com.github.javafaker.Faker;

import net.aonsolutions.occam.api.config.Audit;
import net.aonsolutions.occam.api.config.Booking;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.config.DomainAudit;
import net.aonsolutions.occam.api.config.Scope;
import net.aonsolutions.occam.api.config.User;

public class AonFaker {
	private static Faker faker = Faker.instance(Locale.of("es"));

	public static User getUser( ) {
		return  new User()
			.setId(AonRandom.integer(50))
			.setDomain(AonRandom.integer(50))
			.setName(faker.pokemon().name())
			.setLogin(AonRandom.string(50, 10))
			.setActive(AonRandom.gt(50));
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

	public static Domain getDomain( ) {
		return  new Domain()
			.setId(AonRandom.integer(50))
			.setName(AonRandom.string(50, 10))
			.setDescription(AonRandom.string(50, 10))
			.setParent( AonRandom.gt(50) ? getDomain() : null)
			.setType(AonRandom.getDomainType(20).orElse(null))
			.setEnableHeredity(AonRandom.gt(50))
			.setActive(AonRandom.gt(50))
			.setId(AonRandom.integer(50))
			.setBooking( AonRandom.gt(50) ? getBooking() : null)
			.setAudit( AonRandom.gt(50) ? getDomainAudit() : null)
		;
	}
	public static Booking getBooking() {
		return new Booking()
			.setOwner((AonRandom.string(50, 10)))
			.setExpirationDate(AonRandom.getPastDate(50))
			.setDomainManagement(AonRandom.gt(50))
			.setDisableDomainManagement(AonRandom.gt(50))
			.setMaxDefinedUsers(AonRandom.integer(50))
			.setAonCustomer(AonRandom.integer(50))
			.setAonStatus(AonRandom.getAonStatus(20).orElse(null))
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

	public static Audit getAudit() {
		return new Audit()
			.setCreationUser(AonRandom.string(50, 10))
			.setCreationDate(AonRandom.getPastDate(50))
			.setModificationUser(AonRandom.string(50, 10))
			.setModificationDate(AonRandom.getPastDate(50));
	}
}
