package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;

import java.util.LinkedList;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOrderByStep;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryType;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryPropertiesDAO;

public class RegistrySuggestionDAO {
	private static final RegistryPropertiesDAO REGISTRY_PROPERTIES = new RegistryPropertiesDAO();
	private static final Field<Integer> REG_FIELD = DSL.field("reg", REGISTRY.ID.getType() ); 
	private static final String AR = "aonRegTable";	

	private static SelectConditionStep<Record1<Integer>> getSelect(AONContext ctx, RegistryType type) {
		if(RegistryType.CREDITOR.equals(type)) {
			return ctx.getDslContext().select(CREDITOR.REGISTRY.as(REG_FIELD))
					.from(CREDITOR)
					.where(CREDITOR.DOMAIN.eq(ctx.getDomainId()));
		} else if(RegistryType.SUPPLIER.equals(type)) {
			return ctx.getDslContext().select(SUPPLIER.REGISTRY.as(REG_FIELD))
					.from(SUPPLIER)
					.where(SUPPLIER.DOMAIN.eq(ctx.getDomainId()));
		} else if(RegistryType.CUSTOMER.equals(type)) {
			return ctx.getDslContext().select(CUSTOMER.REGISTRY.as(REG_FIELD))
					.from(CUSTOMER)
					.where(CUSTOMER.DOMAIN.eq(ctx.getDomainId()));
		}
		return null;
	}
	
	public static Stream<Registry> getSuggestionRegistries(AONContext ctx, LinkedList<RegistryType> types, RegistryFilter filter) {
		ctx.checkRead();	
		if(types == null || types.isEmpty()) {
			return getSuggestionRegistries(ctx, filter);
		} else {
			SelectOrderByStep<Record1<Integer>> s = getSelect(ctx, types.get(0));
			for(Integer i = 1; i < types.size(); i++) {
				s = s.unionAll(getSelect(ctx, types.get(i)));
			}
			
			Table<Record1<Integer>> ar = s.asTable(AR);

			return 	ctx.getDslContext().selectDistinct(REG_FIELD, REGISTRY.ID, REGISTRY.DOCUMENT, REGISTRY.NAME)
					.from(ar)
					.join(REGISTRY).on(REGISTRY.ID.eq(REG_FIELD))
					.where(REGISTRY_PROPERTIES.getConditions(filter))
					.and(SecurityDAO.getSecurityLevelCondition(ctx, ctx.getUser(), REGISTRY.SECURITY_LEVEL))
					.orderBy(REGISTRY.NAME)
					.limit(30)
					.fetch()
					.stream()
					.map(r -> new Registry().setId(r.getValue(REGISTRY.ID)).setDocument(r.getValue(REGISTRY.DOCUMENT))
							.setName(r.getValue(REGISTRY.NAME)));	
		}
	}
	
	public static Stream<Registry> getSuggestionRegistries(AONContext ctx, RegistryFilter filter) {
		ctx.checkRead();	
		return 	ctx.getDslContext().selectDistinct(REGISTRY.ID, REGISTRY.DOCUMENT, REGISTRY.NAME)
			.from(REGISTRY)
			.where(REGISTRY_PROPERTIES.getConditions(filter))
			.orderBy(REGISTRY.NAME)
			.limit(30)
			.fetch()
			.stream()
			.map(r -> new Registry().setId(r.getValue(REGISTRY.ID)).setDocument(r.getValue(REGISTRY.DOCUMENT))
					.setName(r.getValue(REGISTRY.NAME)));	
	}
}
