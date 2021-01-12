package com.esferalia.aon.occam.test.faker;

import java.util.Locale;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryOldDAO;
import com.github.javafaker.Faker;

public class AonRandom {
	private static Faker faker = new Faker(new Locale("es"));
	
    public static boolean b( int threshold ) {
    	return faker.random().nextInt(0, 100) <= threshold;
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = faker.random().nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }	

	public static Customer getRandomCustomer(AONContext ctx) {
		String letter = faker.letterify("?");
		Customer c = null;
		int i = 0;
		while (c == null && i < 10 ) {
			c = RegistryOldDAO.getCustomerStream(ctx, f -> f.getNameProperty().ge(letter))
				.findFirst()
				.get();
			i++;
		}
		return c;
	}

	public static Creditor getRandomCreditor(AONContext ctx) {
		String letter = faker.letterify("?");
		Creditor c = null;
		int i = 0;
		while (c == null && i < 10 ) {
			c = RegistryOldDAO.getCreditorStream(ctx, f -> f.getNameProperty().ge(letter))
				.findFirst()
				.get();
			i++;
		}
		return c;
	}

	public static Supplier getRandomSupplier(AONContext ctx) {
		String letter = faker.letterify("?");
		Supplier s = null;
		int i = 0;
		while (s == null && i < 10 ) {
			s = RegistryOldDAO.getSupplierStream(ctx, f -> f.getNameProperty().ge(letter))
				.findFirst()
				.get();
			i++;
		}
		return s;
	}
	
	
	public static EnterpriseActivity getRandomActivity(AONContext ctx) {
		boolean mainActivity = gt(85);
		return CompanyDAO.getEnterpriseActivities(ctx, ctx.getDomainId(), null)
			.filter(act -> act.isPrincipal() == mainActivity)
			.findFirst()
			.orElse(null);
	}
	
	private static boolean gt( int percent) {
		return faker.random().nextInt(0,100) > percent;
	}
}

