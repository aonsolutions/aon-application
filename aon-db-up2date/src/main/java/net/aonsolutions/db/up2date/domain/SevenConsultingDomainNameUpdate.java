package net.aonsolutions.db.up2date.domain;

import java.sql.Connection;
import java.util.stream.Stream;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Company;
import com.esferalia.aon.jooq.tables.Registry;

import net.aonsolutions.db.up2date.Update;

public class SevenConsultingDomainNameUpdate implements Update {

	public static final SevenConsultingDomainNameUpdate SEVEN_CONSULTING_DOMAIN_NAME_UPDATE = new SevenConsultingDomainNameUpdate();
	
	private SevenConsultingDomainNameUpdate() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		if(getDomain(dslContext) != null) 
			getDomainStream(dslContext).forEach(domain -> updateDomain(dslContext, domain));
	}
	
	private Domain getDomain(DSLContext dslContext) {
		return dslContext.select(com.esferalia.aon.jooq.tables.Domain.DOMAIN.ID, com.esferalia.aon.jooq.tables.Domain.DOMAIN.NAME)
				.from(com.esferalia.aon.jooq.tables.Domain.DOMAIN)
				.where(com.esferalia.aon.jooq.tables.Domain.DOMAIN.ID.eq(7372))
				.and(com.esferalia.aon.jooq.tables.Domain.DOMAIN.NAME.eq("sevenconsulting.aonsolutions.net"))
				.fetch().stream().map(r -> new Domain()
						.setId(r.getValue(com.esferalia.aon.jooq.tables.Domain.DOMAIN.ID))
						.setName(r.getValue(com.esferalia.aon.jooq.tables.Domain.DOMAIN.NAME)))
				.findFirst().orElse(null);
	}

	private Stream<Domain> getDomainStream(DSLContext dslContext) {
		return dslContext.select(com.esferalia.aon.jooq.tables.Domain.DOMAIN.ID, com.esferalia.aon.jooq.tables.Domain.DOMAIN.NAME,
					Registry.REGISTRY.ID, Registry.REGISTRY.DOCUMENT)
				.from(com.esferalia.aon.jooq.tables.Domain.DOMAIN)
				.join(Company.COMPANY).on(Company.COMPANY.DOMAIN.eq(com.esferalia.aon.jooq.tables.Domain.DOMAIN.ID))
				.join(Registry.REGISTRY).on(Registry.REGISTRY.ID.eq(Company.COMPANY.REGISTRY))
				.where(com.esferalia.aon.jooq.tables.Domain.DOMAIN.PARENT.eq(7372))
				.fetch().stream().map(r -> new Domain()
						.setId(r.getValue(com.esferalia.aon.jooq.tables.Domain.DOMAIN.ID))
						.setName(r.getValue(com.esferalia.aon.jooq.tables.Domain.DOMAIN.NAME))
						.setRegistry(r.getValue(Registry.REGISTRY.ID))
						.setDocument(r.getValue(Registry.REGISTRY.DOCUMENT)));
	}

	private void updateDomain(DSLContext dslContext, Domain domain) {
		try {
			if("demo-ayudatcadiz.aonsolutions.net".equalsIgnoreCase(domain.getName())) {
				dslContext.update(com.esferalia.aon.jooq.tables.Domain.DOMAIN)
				.set(com.esferalia.aon.jooq.tables.Domain.DOMAIN.NAME, "demo-sevenconsulting.aonsolutions.net")
				.where(com.esferalia.aon.jooq.tables.Domain.DOMAIN.ID.eq(domain.getId()))
				.execute();
			} else {
				if(!isValidDocument(domain.getDocument())) {
					domain.setDocument(domain.getName().substring(0, 9).toUpperCase());
					dslContext.update(Registry.REGISTRY)
					.set(Registry.REGISTRY.DOCUMENT, domain.getDocument())
					.where(Registry.REGISTRY.ID.eq(domain.getRegistry()))
					.execute();
				}
		
				if(isValidDocument(domain.getDocument())) {
					String name = domain.getDocument().toLowerCase() + "-sevenconsulting.aonsolutions.net";
					if(!name.equals(domain.getName())) {
						dslContext.update(com.esferalia.aon.jooq.tables.Domain.DOMAIN)
							.set(com.esferalia.aon.jooq.tables.Domain.DOMAIN.NAME, name)
							.where(com.esferalia.aon.jooq.tables.Domain.DOMAIN.ID.eq(domain.getId()))
							.execute();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean isValidDocument(String document) {
		return document != null && document.length() == 9;
	}
	
	public class Domain {
		Integer id;
		String name;
		Integer registry;
		String document;
		
		public Integer getId() {
			return id;
		}
		
		public Domain setId(Integer id) {
			this.id = id;
			return this;
		}
		
		public String getName() {
			return name;
		}
		
		public Domain setName(String name) {
			this.name = name;
			return this;
		}
		
		public Integer getRegistry() {
			return registry;
		}
		
		public Domain setRegistry(Integer registry) {
			this.registry = registry;
			return this;
		}
		
		public String getDocument() {
			return this.document;
		}
		
		public Domain setDocument(String document) {
			this.document = document;
			return this;
		}
	}
}
