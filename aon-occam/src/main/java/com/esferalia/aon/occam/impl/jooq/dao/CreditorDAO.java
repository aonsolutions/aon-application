package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFilter;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CreditorPropertiesDAO;

public class CreditorDAO {
	
	private static final CreditorPropertiesDAO CREDITOR_PROPERTIES = new CreditorPropertiesDAO();
	
	public static Stream<Creditor> getBasicCreditors(AONContext ctx, CreditorFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(CREDITOR.REGISTRY,CREDITOR.DOMAIN)
			.select(REGISTRY.fields())
			.from(CREDITOR)
			.join(REGISTRY).on(CREDITOR.REGISTRY.equal(REGISTRY.ID))
			.where(CREDITOR_PROPERTIES.getConditions(filter))
			.and( CREDITOR.DOMAIN.eq(ctx.getDomainId()) )
			.and(SecurityDAO.getSecurityLevelCondition(ctx, ctx.getUser(), REGISTRY.SECURITY_LEVEL))
			.and(SecurityDAO.getUserScopesCondition(ctx,ctx.getUser(),CREDITOR.SCOPE))
			.orderBy(REGISTRY.NAME)
			.fetch()
			.stream()
			.map(new MinimalCreditorFiller());			
	}
	
	private static class MinimalCreditorFiller  implements Function<Record,Creditor> {
		@Override
		public Creditor apply(Record record) {
			return new Creditor()
				.setId(record.getValue(CREDITOR.REGISTRY))
				.setRegistry(new RegistryDAO.RegistryFiller().apply(record))
				.setDomain(record.getValue(CREDITOR.DOMAIN));
		}
	}
	
	
}
