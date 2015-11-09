package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainApplication.DOMAIN_APPLICATION;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.util.List;

import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;

public class DomainDAO {

	public static Domain insertDomain(AONContext ctx, Integer parentDomain,
			String document, String name, List<String> messages) {

		Domain domain = null;

		Record domainRecord = ctx
				.getDslContext()
				.select()
				.from(DOMAIN.join(REGISTRY).on(DOMAIN.ID.eq(REGISTRY.DOMAIN))
						.join(COMPANY).on(REGISTRY.ID.eq(COMPANY.REGISTRY)))
				.where(REGISTRY.DOCUMENT.eq(document).and(
						DOMAIN.PARENT.eq(parentDomain))).fetchAny();

		if (domainRecord != null) {

			domain = new Domain();

			domain.setId(domainRecord.getValue(DOMAIN.ID));
			domain.setName(domainRecord.getValue(DOMAIN.NAME));
			domain.setParentId(domainRecord.getValue(DOMAIN.PARENT));

			messages.add(name
					+ " ya se encuentra registrada en la base de datos. No se crea el dominio.");

			return domain;

		}

		DomainRecord parent = getParentDomain(ctx, parentDomain);

		String lowerDocument = document.toLowerCase().concat("-")
				.concat(parent.getValue(DOMAIN.SUBDOMAINSUFFIX));

		// DOMAIN

		int newDomainId = ctx
				.getDslContext()
				.insertInto(DOMAIN)
				.set(DOMAIN.CREATION_USER,
						parent.getValue(DOMAIN.CREATION_USER))
				.set(DOMAIN.CREATION_DATE,
						new java.sql.Timestamp(System.currentTimeMillis()))
				.set(DOMAIN.DOMAINMANAGEMENT, (byte) 0)
				.set(DOMAIN.TYPE, (byte) 0)
				.set(DOMAIN.PARENT, parent.getValue(DOMAIN.ID))
				.set(DOMAIN.OWNER, parent.getValue(DOMAIN.OWNER))
				.set(DOMAIN.NAME, lowerDocument).set(DOMAIN.DESCRIPTION, name)
				.set(DOMAIN.ENABLEHEREDITY, (byte) 1)
				.set(DOMAIN.MAXDEFINEDUSERS, 0).set(DOMAIN.MAXDOCUMENTSIZE, 1)
				.set(DOMAIN.MAXTOTALDOCUMENTSIZE, 16).returning(DOMAIN.ID)
				.fetchOne().getId();

		messages.add("Dominio " + lowerDocument + " insertado correctamente");

		ctx.getDslContext().insertInto(DOMAIN_APPLICATION)
				.set(DOMAIN_APPLICATION.DOMAIN, newDomainId)
				.set(DOMAIN_APPLICATION.APPLICATION, 28)
				.set(DOMAIN_APPLICATION.ACTIVE, (byte) 1)
				.set(DOMAIN_APPLICATION.AUDIT_LEVEL, (byte) 0).execute();

		messages.add("Aplicacion de dominio insertada correctamente");

		int newRegistryId = ctx.getDslContext().insertInto(REGISTRY)
				.set(REGISTRY.DOMAIN, newDomainId)
				.set(REGISTRY.DOCUMENT, document)
				.set(REGISTRY.DOCUMENT_TYPE, (byte) 0).set(REGISTRY.NAME, name)
				.set(REGISTRY.TYPE, (byte) 1).returning(REGISTRY.ID).fetchOne()
				.getId();

		messages.add("Registrado insertado correctamente");

		ctx.getDslContext().insertInto(COMPANY)
				.set(COMPANY.REGISTRY, newRegistryId)
				.set(COMPANY.DOMAIN, newDomainId).execute();

		EnterpriseRecord registryRecord = ctx.getDslContext()
				.selectFrom(ENTERPRISE)
				.where(ENTERPRISE.DOMAIN.eq(parentDomain)).fetchAny();

		messages.add("Empresa insertada correctamente");

		ctx.getDslContext()
				.insertInto(ENTERPRISE)
				.set(ENTERPRISE.REGISTRY, newRegistryId)
				.set(ENTERPRISE.DOMAIN, newDomainId)
				.set(ENTERPRISE.SCOPE,
						registryRecord.getValue(ENTERPRISE.SCOPE)).execute();

		domain = new Domain();
		domain.setId(newDomainId);
		domain.setName(lowerDocument);
		domain.setParentId(parent.getValue(DOMAIN.ID));

		return domain;
	}

	public static DomainRecord getParentDomain(AONContext ctx, Integer domain) {
		return ctx.getDslContext().selectFrom(DOMAIN)
				.where(DOMAIN.ID.eq(domain)).fetchOne();
	}
}
