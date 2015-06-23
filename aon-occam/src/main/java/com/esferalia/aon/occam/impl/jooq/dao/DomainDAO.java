package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainApplication.DOMAIN_APPLICATION;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.Date;

import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;

public class DomainDAO {
	
	public static boolean existDomain (AONContext ctx, String document) {
		
		//EL DOMINIO YA EXISTE ?????		
		RegistryRecord registry = ctx.getDslContext().selectFrom(REGISTRY)
				.where(REGISTRY.DOCUMENT.eq(document))
				.fetchOne();
		
		return registry != null;
	}

	public static Domain insertDomain(AONContext ctx, Integer parentDomain,
			String document, String name) {

		DomainRecord parent = getParentDomain(ctx, parentDomain);

		String lowerDocument = document.toLowerCase().concat("-")
				.concat(parent.getValue(DOMAIN.SUBDOMAINSUFFIX));

		// DOMAIN

		int newDomainId = ctx.getDslContext()
				.insertInto(DOMAIN)
				.set(DOMAIN.CREATION_USER,
						parent.getValue(DOMAIN.CREATION_USER))
				.set(DOMAIN.CREATION_DATE,
						new java.sql.Timestamp(new Date().getTime()))
				.set(DOMAIN.DOMAINMANAGEMENT, (byte) 0)
				.set(DOMAIN.TYPE, (byte) 0)
				.set(DOMAIN.PARENT, parent.getValue(DOMAIN.ID))
				.set(DOMAIN.OWNER, parent.getValue(DOMAIN.OWNER))
				.set(DOMAIN.NAME, lowerDocument)
				.set(DOMAIN.DESCRIPTION, name)
				.set(DOMAIN.ENABLEHEREDITY, (byte) 1)
				.set(DOMAIN.MAXDEFINEDUSERS, 0)
				.set(DOMAIN.MAXDOCUMENTSIZE, 1)
				.set(DOMAIN.MAXTOTALDOCUMENTSIZE, 16)
				.returning(DOMAIN.ID).fetchOne().getId();
		
		ctx.getDslContext().insertInto(DOMAIN_APPLICATION)
		.set(DOMAIN_APPLICATION.DOMAIN, newDomainId)
		.set(DOMAIN_APPLICATION.APPLICATION, 28)
		.set(DOMAIN_APPLICATION.ACTIVE, (byte) 1)
		.set(DOMAIN_APPLICATION.AUDIT_LEVEL, (byte) 0)
		.execute();
		
		int newRegistryId = ctx.getDslContext().insertInto(REGISTRY)
		.set(REGISTRY.DOMAIN, newDomainId)
		.set(REGISTRY.DOCUMENT, document)
		.set(REGISTRY.DOCUMENT_TYPE, (byte)0)
		.set(REGISTRY.NAME, name)
		.set(REGISTRY.TYPE, (byte)1)
		.returning(REGISTRY.ID).fetchOne().getId();
		
		ctx.getDslContext().insertInto(COMPANY)
		.set(COMPANY.REGISTRY, newRegistryId)
		.set(COMPANY.DOMAIN, newDomainId)
		.execute();
		
		EnterpriseRecord registryRecord = ctx.getDslContext()
				.selectFrom(ENTERPRISE)
				.where(ENTERPRISE.DOMAIN.eq(parentDomain))
				.fetchAny();
		
		ctx.getDslContext().insertInto(ENTERPRISE)
		.set(ENTERPRISE.REGISTRY, newRegistryId)
		.set(ENTERPRISE.DOMAIN, newDomainId)
		.set(ENTERPRISE.SCOPE, registryRecord.getValue(ENTERPRISE.SCOPE))
		.execute();
		
		Domain myDomain = new Domain();
		myDomain.setId(newDomainId);
		myDomain.setName(lowerDocument);
		myDomain.setParentId(parent.getValue(DOMAIN.ID));
		
		return myDomain;
		
		

	}

	private static DomainRecord getParentDomain(AONContext ctx,
			Integer parentDomain) {

		return ctx.getDslContext().selectFrom(DOMAIN)
				.where(DOMAIN.ID.eq(parentDomain)).fetchOne();

	}
}
