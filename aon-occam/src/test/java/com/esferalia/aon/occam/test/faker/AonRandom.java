package com.esferalia.aon.occam.test.faker;

import java.util.List;
import java.util.Locale;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.product.Tariff;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryMediaDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryOldDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TariffDAO;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.github.javafaker.Faker;

public class AonRandom {
	private static Faker faker = new Faker(new Locale("es"));
	
    public static boolean b( int threshold ) {
    	return faker.random().nextInt(0, 100) <= threshold;
    }

    public static int number( int from, int to) {
    	return faker.random().nextInt(from, to);
    }
    public static double getDouble( int from, int to) {
    	return getDouble(from, to , 2);
    }
    public static double getDouble( int from, int to, int precision ) {
    	double r = faker.random().nextDouble();
    	return AonMathUtils.round(from + ((to - from) * r), precision);
    }

    public static <T> T random(List<T> list){
    	if (list == null || list.isEmpty()) return null;
        return list.get(faker.random().nextInt(0, (list.size() - 1)));
    }	

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = faker.random().nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }	

	public static Tariff getTariff(AONContext ctx) {
		return TariffDAO.getRandom(ctx, null );
	}

	public static Registry getRegistry(AONContext ctx) {
		return RegistryDAO.getRandom(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
	}
	public static RegistryMedia getRegistryMedia(AONContext ctx, RegistryMediaFilter filter) {
		return RegistryMediaDAO.getRandom(ctx, null );
	}
	public static RegistryAddress getRegistryAddress(AONContext ctx, RegistryAddressFilter filter) {
		return RegistryAddressDAO.getRandom(ctx, filter );
	}

	public static Customer getCustomer(AONContext ctx) {
		return CustomerDAO.getRandom(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
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

