package com.esferalia.aon.occam.test.faker;

import java.util.List;
import java.util.Locale;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Filter.CreditorFilter;
import com.esferalia.aon.occam.api.model.Filter.CustomerFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Filter.SupplierFilter;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.product.Tariff;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.GeoZoneDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryMediaDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TariffDAO;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.github.javafaker.Faker;

public class AonRandom {
	private static Faker faker = new Faker(new Locale("es"));
	
//    public static boolean b( int nullThreshold ) {
//    	return faker.random().nextInt(0, 100) <= nullThreshold;
//    }
    
    public static boolean gt( int threshold) {
		return faker.random().nextInt(0,100) > threshold;
	}

    public static String name( int nullThreshold, int length ) {
    	return ( gt(nullThreshold) )
    		?faker.name().fullName( )
    		:null;
    }
    public static String alias( int nullThreshold, int length ) {
    	return ( gt(nullThreshold) )
    		?faker.name().username()
    		:null;
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
    	return randomEnum(clazz,100);
    }	
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz, int nullThreshold){
    	return gt(nullThreshold)
    			?clazz.getEnumConstants()[faker.random().nextInt(clazz.getEnumConstants().length)]
				:null;
    }	

	public static Tariff getTariff(AONContext ctx) {
		return getTariff(ctx, 100);
	}
	public static Tariff getTariff(AONContext ctx, int nullThreshold){
		return gt(nullThreshold)
			?TariffDAO.getRandom(ctx, null )
			:null;
	}

	public static GeoZone getGeozone(AONContext ctx) {
		return getGeozone(ctx, 100);
	}
	public static GeoZone getGeozone(AONContext ctx, int nullThreshold){
		return gt(nullThreshold)
			?GeoZoneDAO.getRandom(ctx, null )
			:null;
	}

	public static Registry getRegistry(AONContext ctx) {
		return getRegistry(ctx,null);
	}
	public static Registry getRegistry(AONContext ctx, RegistryFilter filter) {
		return RegistryDAO.getRandom(ctx,filter);
	}
	
	public static RegistryMedia getRegistryMedia(AONContext ctx, RegistryMediaFilter filter) {
		return RegistryMediaDAO.getRandom(ctx, filter );
	}
	
	public static RegistryAddress getRegistryAddress(AONContext ctx, RegistryAddressFilter filter) {
		return RegistryAddressDAO.getRandom(ctx, filter );
	}

	public static Customer getCustomer(AONContext ctx) {
		return getCustomer(ctx,null);	
	}
	public static Customer getCustomer(AONContext ctx, CustomerFilter filter) {
		return CustomerDAO.getRandom(ctx, filter);
	}

	public static Creditor getCreditor(AONContext ctx) {
		return getCreditor(ctx,null);	
	}
	public static Creditor getCreditor(AONContext ctx, CreditorFilter filter) {
		return CreditorDAO.getRandom(ctx, filter);
	}

	public static Supplier getSupplier(AONContext ctx) {
		return getSupplier(ctx,null);	
	}
	public static Supplier getSupplier(AONContext ctx, SupplierFilter filter) {
		return SupplierDAO.getRandom(ctx, filter);
	}
	
	
	public static EnterpriseActivity getRandomActivity(AONContext ctx) {
		boolean mainActivity = gt(85);
		return CompanyDAO.getEnterpriseActivities(ctx, ctx.getDomainId(), null)
			.filter(act -> act.isPrincipal() == mainActivity)
			.findFirst()
			.orElse(null);
	}

}

