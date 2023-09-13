package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Geotree.GEOTREE;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.Country;
import com.esferalia.aon.gwt.payroll.shared.Geozone;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.GeotreeRecord;
import com.esferalia.aon.jooq.tables.records.GeozoneRecord;

public class JooqAddress {

	private static Settings SETTINGS = null;
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	public static List<Country> getCountries(Connection conn, Integer domainId) {
		
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		
		List<Integer> domains = getDomainsHeredity(dslContext, domainId);
		
		Result<GeotreeRecord> greotrees = dslContext.selectFrom(GEOTREE).where(GEOTREE.DOMAIN.in(domains)).and(GEOTREE.PARENT.isNull()).fetch();
		List<Integer> geoTreeChilds = greotrees.stream().map(geotreeRecord -> geotreeRecord.getChild()).collect(Collectors.toList());
		
		Result<GeozoneRecord> geozoneCountries = dslContext.selectFrom(GEOZONE).where(GEOZONE.ID.in(geoTreeChilds)).groupBy(GEOZONE.CODE).fetch();
		
		List<Geozone> countryGeozones = geozoneCountries.stream()
				.map(geozoneCountry -> 
					new Geozone()
						.setId(geozoneCountry.getId())
						.setDomain(geozoneCountry.getDomain())
						.setName(geozoneCountry.getName())
						.setCode(geozoneCountry.getCode())
						.setSystem(geozoneCountry.getSystem()))
				.collect(Collectors.toList());
		
		List<Country> countries = countryGeozones.stream().map(countryGeozone -> new Country().setCountry(countryGeozone)).collect(Collectors.toList());
		
		for(Country country : countries)
			country.setProvinces(getProvinces(dslContext, domainId, country.getCountry().getCode()));
		
		return countries;
	}
	
	public static List<Geozone> getProvinces(DSLContext dslContext, Integer domainId, String countryCode) {
		
		List<Integer> domains = getDomainsHeredity(dslContext, domainId);
		
		GeozoneRecord geozoneCountry = dslContext.selectFrom(GEOZONE).where(GEOZONE.DOMAIN.in(domains)).and(GEOZONE.CODE.eq(countryCode)).groupBy(GEOZONE.CODE).fetchOne();
		
		Result<GeotreeRecord> greotrees = dslContext.selectFrom(GEOTREE).where(GEOTREE.PARENT.eq(geozoneCountry.getId())).fetch();
		List<Integer> geoTreeChilds = greotrees.stream().map(geotreeRecord -> geotreeRecord.getChild()).collect(Collectors.toList());
		
		Result<GeozoneRecord> geozoneProvinces = dslContext.selectFrom(GEOZONE).where(GEOZONE.ID.in(geoTreeChilds)).groupBy(GEOZONE.CODE).fetch();
		
		List<Geozone> countryProvinces = geozoneProvinces.stream()
				.map(geozoneProvince -> 
					new Geozone()
						.setId(geozoneProvince.getId())
						.setDomain(geozoneProvince.getDomain())
						.setName(geozoneProvince.getName())
						.setCode(geozoneProvince.getCode())
						.setSystem(geozoneProvince.getSystem()))
				.collect(Collectors.toList());
		
		return countryProvinces;
	}
	
	private static List<Integer> getDomainsHeredity(DSLContext dslContext, Integer domainId) {
		List<Integer> domains = new ArrayList<>();
		domains.add(domainId);
		
		DomainRecord domainRecord = dslContext.selectFrom(DOMAIN).where(DOMAIN.ID.eq(domainId)).fetchOne();
		
		if(domainRecord.getEnableheredity() == 1) domains.add(domainRecord.getParent());
		
		return domains;
	}

}
