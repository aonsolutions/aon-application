package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Geotree.GEOTREE;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;

import java.util.HashMap;
import java.util.List;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.occam.impl.jooq.dao.GeoZoneDAO.GeoZoneFiller;

public class DBRegistry {
	
	private DBRegistry() {
		
	}
	
	public static List<CustomerFull> getCustomers(Domain domain, User user) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			com.esferalia.aon.jooq.tables.Geozone parent = GEOZONE.as("parentGeozone");
			com.esferalia.aon.jooq.tables.Geozone child = GEOZONE.as("childGeozone");
			
			HashMap<Integer, CustomerFull> map = new HashMap<>();
			ctx.getDslContext().select()
				.from(REGISTRY)
				.join(CUSTOMER).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
				.join(RADDRESS).on(REGISTRY.ID.eq(RADDRESS.REGISTRY))
				.leftOuterJoin(child).on(child.ID.eq(RADDRESS.GEOZONE))
				.leftOuterJoin(GEOTREE).on(GEOTREE.CHILD.eq(RADDRESS.GEOZONE))
				.leftOuterJoin(parent).on(parent.ID.eq(GEOTREE.PARENT))
				.where(CUSTOMER.DOMAIN.eq(domain.getId()))
				.fetch().stream().forEach(r -> {
					
					Integer id = r.getValue(REGISTRY.ID);
					String document = r.getValue(REGISTRY.DOCUMENT);
					String name = r.getValue(REGISTRY.NAME);
					
					if(!map.containsKey(id)) {
						CustomerFull customerFull = new CustomerFull();
						Customer customer = new Customer();
						customer.setId(id);
						customer.setDocument(document);
						customer.setName(name);

						customerFull.setRegistry(customer);
						map.put(id, customerFull);
					}
					
					RegistryAddress address = new RegistryAddress()
							.setId(r.getValue(RADDRESS.ID))
							.setDomain(r.getValue(RADDRESS.DOMAIN))
							.setRecipient(r.getValue(RADDRESS.RECIPIENT))
							.setStreetType(StreetType.getForAeatCode(r.getValue(RADDRESS.STREET_TYPE), AonLanguage.SPANISH))
							.setAddress(r.getValue(RADDRESS.ADDRESS))
							.setNumber(r.getValue(RADDRESS.NUMBER))
							.setAddress2(r.getValue(RADDRESS.ADDRESS2))
							.setAddress3(r.getValue(RADDRESS.ADDRESS3))
							.setZip(r.getValue(RADDRESS.ZIP))
							.setCity(r.getValue(RADDRESS.CITY))
							.setGeozone(r.getValue(RADDRESS.GEOZONE))
							.setGeozoneCode(r.getValue(child.CODE))
							.setGeozoneName(r.getValue(child.NAME))
							.setChild(GeoZoneFiller.build(r, child))
							.setParent(GeoZoneFiller.build(r, parent))						
							.setAlias(r.getValue(RADDRESS.ALIAS))
							.setMunicipalityCode(r.getValue(RADDRESS.MUNICIPALITY_CODE));					
					
					map.get(id).addAddress(address);
				});
			
				return map.values().stream().toList();
		}
	}
	
	public static List<SupplierFull> getSuppliers(Domain domain, User user) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			com.esferalia.aon.jooq.tables.Geozone parent = GEOZONE.as("parentGeozone");
			com.esferalia.aon.jooq.tables.Geozone child = GEOZONE.as("childGeozone");
			
			HashMap<Integer, SupplierFull> map = new HashMap<>();
			ctx.getDslContext().select()
				.from(REGISTRY)
				.join(SUPPLIER).on(REGISTRY.ID.eq(SUPPLIER.REGISTRY))
				.join(RADDRESS).on(REGISTRY.ID.eq(RADDRESS.REGISTRY))
				.leftOuterJoin(child).on(child.ID.eq(RADDRESS.GEOZONE))
				.leftOuterJoin(GEOTREE).on(GEOTREE.CHILD.eq(RADDRESS.GEOZONE))
				.leftOuterJoin(parent).on(parent.ID.eq(GEOTREE.PARENT))
				.where(SUPPLIER.DOMAIN.eq(domain.getId()))
				.fetch().stream().forEach(r -> {
					
					Integer id = r.getValue(REGISTRY.ID);
					String document = r.getValue(REGISTRY.DOCUMENT);
					String name = r.getValue(REGISTRY.NAME);
					
					if(!map.containsKey(id)) {
						SupplierFull supplierFull = new SupplierFull();
						Supplier supplier = new Supplier();
						supplier.setId(id);
						supplier.setDocument(document);
						supplier.setName(name);

						supplierFull.setRegistry(supplier);
						map.put(id, supplierFull);
					}
					
					RegistryAddress address = new RegistryAddress()
							.setId(r.getValue(RADDRESS.ID))
							.setDomain(r.getValue(RADDRESS.DOMAIN))
							.setRecipient(r.getValue(RADDRESS.RECIPIENT))
							.setStreetType(StreetType.getForAeatCode(r.getValue(RADDRESS.STREET_TYPE), AonLanguage.SPANISH))
							.setAddress(r.getValue(RADDRESS.ADDRESS))
							.setNumber(r.getValue(RADDRESS.NUMBER))
							.setAddress2(r.getValue(RADDRESS.ADDRESS2))
							.setAddress3(r.getValue(RADDRESS.ADDRESS3))
							.setZip(r.getValue(RADDRESS.ZIP))
							.setCity(r.getValue(RADDRESS.CITY))
							.setGeozone(r.getValue(RADDRESS.GEOZONE))
							.setGeozoneCode(r.getValue(child.CODE))
							.setGeozoneName(r.getValue(child.NAME))
							.setChild(GeoZoneFiller.build(r, child))
							.setParent(GeoZoneFiller.build(r, parent))
							.setAlias(r.getValue(RADDRESS.ALIAS))
							.setMunicipalityCode(r.getValue(RADDRESS.MUNICIPALITY_CODE));
					
					map.get(id).addAddress(address);
				});
			
				return map.values().stream().toList();
		}
	}
	
	public static List<CreditorFull> getCreditors(Domain domain, User user) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			com.esferalia.aon.jooq.tables.Geozone parent = GEOZONE.as("parentGeozone");
			com.esferalia.aon.jooq.tables.Geozone child = GEOZONE.as("childGeozone");
			
			HashMap<Integer, CreditorFull> map = new HashMap<>();
			ctx.getDslContext().select()
				.from(REGISTRY)
				.join(CREDITOR).on(REGISTRY.ID.eq(CREDITOR.REGISTRY))
				.join(RADDRESS).on(REGISTRY.ID.eq(RADDRESS.REGISTRY))
				.leftOuterJoin(child).on(child.ID.eq(RADDRESS.GEOZONE))
				.leftOuterJoin(GEOTREE).on(GEOTREE.CHILD.eq(RADDRESS.GEOZONE))
				.leftOuterJoin(parent).on(parent.ID.eq(GEOTREE.PARENT))
				.where(CREDITOR.DOMAIN.eq(domain.getId()))
				.fetch().stream().forEach(r -> {
					
					Integer id = r.getValue(REGISTRY.ID);
					String document = r.getValue(REGISTRY.DOCUMENT);
					String name = r.getValue(REGISTRY.NAME);
					
					if(!map.containsKey(id)) {
						CreditorFull creditorFull = new CreditorFull();
						Creditor creditor = new Creditor();
						creditor.setId(id);
						creditor.setDocument(document);
						creditor.setName(name);

						creditorFull.setRegistry(creditor);
						map.put(id, creditorFull);
					}
					
					RegistryAddress address = new RegistryAddress()
							.setId(r.getValue(RADDRESS.ID))
							.setDomain(r.getValue(RADDRESS.DOMAIN))
							.setRecipient(r.getValue(RADDRESS.RECIPIENT))
							.setStreetType(StreetType.getForAeatCode(r.getValue(RADDRESS.STREET_TYPE), AonLanguage.SPANISH))
							.setAddress(r.getValue(RADDRESS.ADDRESS))
							.setNumber(r.getValue(RADDRESS.NUMBER))
							.setAddress2(r.getValue(RADDRESS.ADDRESS2))
							.setAddress3(r.getValue(RADDRESS.ADDRESS3))
							.setZip(r.getValue(RADDRESS.ZIP))
							.setCity(r.getValue(RADDRESS.CITY))
							.setGeozone(r.getValue(RADDRESS.GEOZONE))
							.setGeozoneCode(r.getValue(child.CODE))
							.setGeozoneName(r.getValue(child.NAME))
							.setChild(GeoZoneFiller.build(r, child))
							.setParent(GeoZoneFiller.build(r, parent))
							.setAlias(r.getValue(RADDRESS.ALIAS))
							.setMunicipalityCode(r.getValue(RADDRESS.MUNICIPALITY_CODE));
					
					map.get(id).addAddress(address);
				});
			
				return map.values().stream().toList();
		}
	}
	
	
}
