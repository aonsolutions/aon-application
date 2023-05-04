package net.aonsolutions.occam.test.faker;

import java.util.Locale;

import com.github.javafaker.Faker;

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

}
