package net.aonsolutions.db.up2date.finance;

import java.sql.Connection;
import java.util.stream.Stream;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.Geozone;
import com.esferalia.aon.jooq.tables.Invoice;
import com.esferalia.aon.jooq.tables.Raddress;

import net.aonsolutions.db.up2date.Update;

public class SagardoBusUpdate implements Update {

	public static final SagardoBusUpdate SAGARDOBUS_UPDATE = new SagardoBusUpdate();
	private static final int SAGARDOBUS_ID = 33683;
	private static final String SAGARDOBUS_NAME = "sagardobus-imazasesores.aonsolutions.net";
	
	private SagardoBusUpdate() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		if(isSagardobus(dslContext)) {
			getInvoiceWithoutAddress(dslContext).forEach(inv -> {
				Address address = getAddress(dslContext, inv);
				if(address.getId() != null) {
					updateAddress(dslContext, address);
					updateInvoiceAddress(dslContext, inv, address);
				}
			});
		}
		
	}
	
	
	private boolean isSagardobus(DSLContext dslContext) {
		String domainName = dslContext.select(Domain.DOMAIN.NAME)
			.from(Domain.DOMAIN)
			.where(Domain.DOMAIN.ID.eq(SAGARDOBUS_ID)
				.and(Domain.DOMAIN.NAME.eq(SAGARDOBUS_NAME)))
			.fetch().stream().map(r -> r.getValue(Domain.DOMAIN.NAME))
			.findFirst().orElse(null);
		
		return domainName != null && SAGARDOBUS_NAME.equalsIgnoreCase(domainName);
	}
	
	private Stream<Inv> getInvoiceWithoutAddress(DSLContext dslContext) {
		return dslContext.select(Invoice.INVOICE.ID, Invoice.INVOICE.REGISTRY)
		.from(Invoice.INVOICE)
		.where(Invoice.INVOICE.DOMAIN.eq(SAGARDOBUS_ID))
		.and(Invoice.INVOICE.RADDRESS.isNull())
		.fetch().stream()
		.map(r -> new Inv()
				.setId(r.getValue(Invoice.INVOICE.ID))
				.setRegistry(r.getValue(Invoice.INVOICE.REGISTRY)));
		
	}
	
	private void updateInvoiceAddress(DSLContext dslContext, Inv inv, Address address) {
		dslContext
		.update(Invoice.INVOICE)
		.set(Invoice.INVOICE.RADDRESS, address.getId())
		.where(Invoice.INVOICE.ID.eq(inv.getId()))
		.and(Invoice.INVOICE.DOMAIN.eq(SAGARDOBUS_ID))
		.execute();
	}
	
	private Address getAddress(DSLContext dslContext, Inv inv) {
		return dslContext.select(Raddress.RADDRESS.ID, Raddress.RADDRESS.ZIP, Raddress.RADDRESS.GEOZONE)
		.from(Raddress.RADDRESS).where(Raddress.RADDRESS.REGISTRY.eq(inv.getRegistry()))
		.and(Raddress.RADDRESS.DOMAIN.eq(SAGARDOBUS_ID))
		.fetch().stream().map(r -> new Address()
			.setId(r.getValue(Raddress.RADDRESS.ID))
			.setZip(r.getValue(Raddress.RADDRESS.ZIP))
			.setGeozone(r.getValue(Raddress.RADDRESS.GEOZONE))).findFirst()
		.orElse(new Address());
	}
	
	private void updateAddress(DSLContext dslContext, Address address) {	
		if(address.getGeozone() == null &&  address.getZip() != null && address.getZip().length() > 2) {
			String code = address.getZip().substring(0, 2);
			Integer geozone = getGeozone(dslContext, code);

			dslContext.update(Raddress.RADDRESS)
			.set(Raddress.RADDRESS.GEOZONE, geozone)
			.where(Raddress.RADDRESS.ID.eq(address.getId()))
			.and(Raddress.RADDRESS.DOMAIN.eq(SAGARDOBUS_ID))
			.execute();
		}
	}

	private Integer getGeozone(DSLContext dslContext, String code) {
		return dslContext.select(Geozone.GEOZONE.ID)
		.from(Geozone.GEOZONE)
		.where(Geozone.GEOZONE.DOMAIN.eq(SAGARDOBUS_ID))
		.and(Geozone.GEOZONE.CODE.eq(code))
		.fetch().stream().map(r -> r.getValue(Geozone.GEOZONE.ID)).findFirst().orElse(null);
	}
	
	public static class Address {
		Integer id;
		String zip;
		Integer geozone;
		
		public Integer getId() {
			return id;
		}
		
		public Address setId(Integer id) {
			this.id = id;
			return this;
		}
		
		public String getZip() {
			return zip;
		}
		
		public Address setZip(String zip) {
			this.zip = zip;
			return this;
		}
		
		public Integer getGeozone() {
			return geozone;
		}
		
		public Address setGeozone(Integer geozone) {
			this.geozone = geozone;
			return this;
		}
	}
	
	public static class Inv {
		Integer id;
		Integer registry;
		
		public Integer getId() {
			return id;
		}
		
		public Inv setId(Integer id) {
			this.id = id;
			return this;
		}
		
		public Integer getRegistry() {
			return registry;
		}
		
		public Inv setRegistry(Integer registry) {
			this.registry = registry;
			return this;
		}
	}
}
