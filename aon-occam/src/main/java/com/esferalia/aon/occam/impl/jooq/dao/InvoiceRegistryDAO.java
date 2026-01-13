package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;

import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistryType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceRegistryDAO {

	private static final Field<Integer> TYPE_FIELD = DSL.field("type_field", Integer.class);
	private static final Field<Integer> CUSTOMER_TYPE = DSL.val(InvoiceRegistryType.CUSTOMER.ordinal()).as(TYPE_FIELD);
	private static final Field<Integer> CREDITOR_TYPE = DSL.val(InvoiceRegistryType.CREDITOR.ordinal()).as(TYPE_FIELD);
	private static final Field<Integer> SUPPLIER_TYPE = DSL.val(InvoiceRegistryType.SUPPLIER.ordinal()).as(TYPE_FIELD);
	
	private static final Field<Integer> SCOPE_FIELD = DSL.field("scope_field", Integer.class);
	private static final Field<Integer> CUSTOMER_SCOPE = CUSTOMER.SCOPE.as(SCOPE_FIELD);
	private static final Field<Integer> CREDITOR_SCOPE = CREDITOR.SCOPE.as(SCOPE_FIELD);
	private static final Field<Integer> SUPPLIER_SCOPE = SUPPLIER.SCOPE.as(SCOPE_FIELD);
	
	private static Field<Integer> REGISTRY_ID = DSL.field("REGISTRY_ID", Integer.class);
	private static Field<Integer> REGISTRY_DOMAIN = DSL.field("REGISTRY_DOMAIN", Integer.class);
	private static Field<Byte> REGISTRY_DOCUMENT_TYPE = DSL.field("REGISTRY_DOCUMENT_TYPE", Byte.class);
	private static Field<String> REGISTRY_DOCUMENT_COUNTRY = DSL.field("REGISTRY_DOCUMENT_COUNTRY", String.class);
	private static Field<String> REGISTRY_DOCUMENT = DSL.field("REGISTRY_DOCUMENT", String.class);
	private static Field<String> REGISTRY_NATIONALITY = DSL.field("REGISTRY_NATIONALITY", String.class);
	private static Field<String> REGISTRY_NAME = DSL.field("REGISTRY_NAME", String.class);
	private static Field<String> REGISTRY_ALIAS = DSL.field("REGISTRY_ALIAS", String.class);

	private static final Field<?>[] REGISTRY_FIELDS = new Field[] {
		REGISTRY_ID, REGISTRY_DOMAIN, REGISTRY_DOCUMENT, REGISTRY_DOCUMENT_COUNTRY, REGISTRY_DOCUMENT_TYPE, 
		REGISTRY_NATIONALITY, REGISTRY_NAME, REGISTRY_ALIAS
	};

	private static final Field<Integer> REGISTRY_ID_F = REGISTRY.ID.as( REGISTRY_ID );
	private static final Field<Integer> REGISTRY_DOMAIN_F = REGISTRY.DOMAIN.as( REGISTRY_DOMAIN);
	private static final Field<Byte> REGISTRY_DOCUMENT_TYPE_F = REGISTRY.DOCUMENT_TYPE.as(REGISTRY_DOCUMENT_TYPE);
	private static final Field<String> REGISTRY_DOCUMENT_COUNTRY_F = REGISTRY.DOCUMENT_COUNTRY.as(REGISTRY_DOCUMENT_COUNTRY);
	private static final Field<String> REGISTRY_DOCUMENT_F = REGISTRY.DOCUMENT.as(REGISTRY_DOCUMENT);
	private static final Field<String> REGISTRY_NATIONALITY_F = REGISTRY.NATIONALITY.as(REGISTRY_NATIONALITY);
	private static final Field<String> REGISTRY_NAME_F = REGISTRY.NAME.as(REGISTRY_NAME);
	private static final Field<String> REGISTRY_ALIAS_F = REGISTRY.ALIAS.as(REGISTRY_ALIAS);

	private static final Field<?>[] REGISTRY_SUB_FIELDS = new Field[] {
		REGISTRY_ID_F
		,REGISTRY_DOMAIN_F	
		,REGISTRY_DOCUMENT_F
		,REGISTRY_DOCUMENT_COUNTRY_F
		,REGISTRY_DOCUMENT_TYPE_F
		,REGISTRY_NATIONALITY_F
		,REGISTRY_NAME_F
		,REGISTRY_ALIAS_F
	};
	
	private InvoiceRegistryDAO() {

	}
	
	public static Stream<InvoiceRegistry> getInvoiceRegistriesSuggestion(AONContext ctx, Integer domainId, String query) {
		String pattern = AonStringUtils.SQLlike(query);
		
		Condition c = REGISTRY.DOMAIN.eq(domainId)
			.and( REGISTRY.DOCUMENT.like(pattern)
			 .or( REGISTRY.NAME.like(pattern) )
			 .or( REGISTRY.ALIAS.like(pattern) ) )
			.and(SecurityDAO.getUserScopesCondition(ctx,ctx.getUser(),INVOICE.SCOPE))
			.and(SecurityDAO.getSecurityLevelCondition(ctx, ctx.getUser(), INVOICE.SECURITY_LEVEL))
		;
		
		SelectConditionStep<Record> customerSelect = ctx.getDslContext()
			.select(REGISTRY_SUB_FIELDS)
			.select(CUSTOMER_SCOPE, CUSTOMER_TYPE)
				.from(REGISTRY)
				.innerJoin(CUSTOMER).on(CUSTOMER.REGISTRY.eq(REGISTRY.ID))
				.where(c);
		
		SelectConditionStep<Record> supplierSelect = ctx.getDslContext()
			.select(REGISTRY_SUB_FIELDS)
			.select(SUPPLIER_SCOPE, SUPPLIER_TYPE)
				.from(REGISTRY)
				.innerJoin(SUPPLIER).on(SUPPLIER.REGISTRY.eq(REGISTRY.ID))
				.where(c);
		
		SelectConditionStep<Record> creditorSelect = ctx.getDslContext()
			.select(REGISTRY_SUB_FIELDS)
			.select(CREDITOR_SCOPE, CREDITOR_TYPE)
				.from(REGISTRY)
				.innerJoin(CREDITOR).on(CREDITOR.REGISTRY.eq(REGISTRY.ID))
				.where(c);

		return ctx.getDslContext().select( REGISTRY_FIELDS ).select(SCOPE_FIELD, TYPE_FIELD)
			.from(customerSelect.unionAll(supplierSelect).unionAll(creditorSelect))
			.orderBy(REGISTRY_NAME)
			.limit(30)
			.fetch()
			.stream()
			.map(rec -> new InvoiceRegistry()
				.setId( rec.getValue(REGISTRY_ID_F) )
				.setDomain( rec.getValue(REGISTRY_DOMAIN_F) )
				.setName(rec.getValue(REGISTRY_NAME_F))
				.setAlias(rec.getValue(REGISTRY_ALIAS_F))
				.setDocumentType(DocumentType.safeValueOf(rec.getValue(REGISTRY_DOCUMENT_TYPE_F)))
				.setDocumentCountry(Country.safeValueOf(rec.getValue(REGISTRY_DOCUMENT_COUNTRY_F)))
				.setDocument(rec.getValue(REGISTRY_DOCUMENT_F))
				.setNationality(Country.safeValueOf(rec.getValue(REGISTRY_NATIONALITY_F)))
				.setScope( rec.getValue(SCOPE_FIELD))
				.setType( InvoiceRegistryType.values()[ rec.getValue(TYPE_FIELD) ] )
			);
	}
}

