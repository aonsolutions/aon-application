package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFilter;
import com.esferalia.aon.occam.api.model.registry.CreditorProperties;

public class CreditorDAO {
	
	private static final CreditorPropertiesDAO CREDITOR_PROPERTIES = new CreditorPropertiesDAO();
	private static class CreditorPropertiesDAO implements CreditorProperties {
		private Condition[] getConditions(CreditorFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(CREDITOR.REGISTRY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(CREDITOR.DOMAIN);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.DOCUMENT);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.NAME);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.ALIAS);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<Byte>(CREDITOR.STATUS);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<Byte>(REGISTRY.SECURITY_LEVEL);}
	}
	
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
