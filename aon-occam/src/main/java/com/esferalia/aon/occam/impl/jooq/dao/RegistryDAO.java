package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Category.CATEGORY;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.CategoryRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryFilter;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryProperties;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class RegistryDAO {
	
	private static final Field<Integer> TYP_FIELD = DSL.field("typ", Integer.class);
	private static final Field<Integer> REG_FIELD = DSL.field("reg", REGISTRY.ID.getType() ); 
	private static final Field<Integer> ACC_FIELD = DSL.field("acc", ACCOUNT.ID.getType() );
	private static final Field<Integer> SCP_FIELD = DSL.field("scp", CUSTOMER.SCOPE.getType() );
	private static final Field<Byte> ITR_FIELD    = DSL.field("itr", CUSTOMER.TRANSACTION.getType() );
	private static final Field<Byte> WTH_FIELD    = DSL.field("wth", CUSTOMER.WITHHOLDING.getType() );
	private static final Field<Byte> WTF_FIELD    = DSL.field("wtf", SUPPLIER.WITHHOLDING_FARMER.getType() );
	private static final Field<Byte> SUR_FIELD    = DSL.field("sur", CUSTOMER.SURCHARGE.getType() );
	private static final Field<Byte> VAP_FIELD    = DSL.field("vap", CUSTOMER.SURCHARGE.getType() );
	
	private static final Param<Byte> FALSE_TYPE  = DSL.val( (byte) 0 );
	private static final Param<Integer> CUS_TYPE  = DSL.val( AccountingRegistryType.CUSTOMER.ordinal());
	private static final Param<Integer> SUP_TYPE  = DSL.val( AccountingRegistryType.SUPPLIER.ordinal());
	private static final Param<Integer> CRE_TYPE  = DSL.val( AccountingRegistryType.CREDITOR.ordinal());
	static {
		FALSE_TYPE.setInline(true);
		CUS_TYPE.setInline(true);
		SUP_TYPE.setInline(true);
		CRE_TYPE.setInline(true);
	}
	
	
	private static final AccountingRegistryPropertiesDAO ACCOUNTING_REGISTRY_PROPERTIES = new AccountingRegistryPropertiesDAO();
	private static class AccountingRegistryPropertiesDAO implements AccountingRegistryProperties {
		private Condition[] getConditions(AccountingRegistryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(REGISTRY.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(REGISTRY.DOMAIN);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.DOCUMENT);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.NAME);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.ALIAS);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<Byte>(REGISTRY.SECURITY_LEVEL);}
		@Override public Property<String> getAccountCodeProperty() {return new FilterDAO.PropertyDAO<String>(ACCOUNT.CODE);}
		@Override public Property<String> getAccountDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(ACCOUNT.DESCRIPTION);}
	}
	
	public static Category getCategory(AONContext ctx, Integer categoryId){
		return ctx.getDslContext()
				.select().from(CATEGORY).where(CATEGORY.ID.eq(categoryId)).limit(1)
				.fetchInto(CATEGORY).stream().map(new FullCategoryFiller()).findFirst().orElse(new Category());
	}
	
	public static LinkedList<Category> getCategoryList(AONContext ctx){
		return ctx.getDslContext()
				.select().from(CATEGORY).where(CATEGORY.DOMAIN.eq(ctx.getDomainId())).limit(1)
				.fetchInto(CATEGORY).stream().map(new FullCategoryFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Registry getRegistry(AONContext ctx, String name){
		return ctx.getDslContext().select().from(REGISTRY).where(REGISTRY.NAME.eq(name)).fetchInto(REGISTRY)
			.stream().map(new RegistryFiller()).findFirst().orElse(new Registry());
	}
	
	private static class FullCategoryFiller implements Function<CategoryRecord, Category> {
		@Override
		public Category apply(CategoryRecord r) {
			return new Category()
					.setDescription(r.getDescription())
					.setDomain(r.getDomain())
					.setId(r.getId())
					.setName(r.getName())
					.setRattach(r.getRattach())
					.setScope(r.getScope())
					.setType(r.getType())
					.setUrl(r.getUrl());
		}
	}
	
	public static class RegistryFiller  implements Function<Record,Registry> {

		@Override
		public Registry apply(Record record) {
			return new Registry()
					.setId(record.getValue(REGISTRY.ID))
					.setDomain(record.getValue(REGISTRY.DOMAIN))
					.setAlias(record.getValue(REGISTRY.ALIAS))
					.setDocument(record.getValue(REGISTRY.DOCUMENT))
					.setDocumentType(DocumentType.safeValueOf(record.getValue(REGISTRY.DOCUMENT_TYPE)))
					.setDocumentCountry(Country.safeValueOf(record.getValue(REGISTRY.DOCUMENT_COUNTRY)))
					.setName(record.getValue(REGISTRY.NAME))
					.setNationality(Country.safeValueOf(record.getValue(REGISTRY.NATIONALITY)))
					.setSecurityLevel(SecurityLevel.safeValueOf( record.getValue(REGISTRY.SECURITY_LEVEL)))
					.setType(record.getValue(REGISTRY.TYPE))
				;
		}
		
	}

	public static Stream<AccountingRegistry> getAccountingRegistries(AONContext ctx, AccountingRegistryFilter filter) {
		ctx.checkRead();
		String AR = "accRegTable";
		
		return 	ctx.getDslContext().select( 
				 TYP_FIELD,REG_FIELD,ACC_FIELD
				,SCP_FIELD,ITR_FIELD,WTH_FIELD
				,WTF_FIELD,SUR_FIELD,VAP_FIELD
				,REGISTRY.ID,REGISTRY.DOCUMENT,REGISTRY.DOCUMENT_TYPE
				,REGISTRY.DOCUMENT_COUNTRY,REGISTRY.NAME,REGISTRY.ALIAS
				,ACCOUNT.ID,ACCOUNT.CODE,ACCOUNT.DESCRIPTION
				)
			.from(
					ctx.getDslContext().select(SUP_TYPE.as(TYP_FIELD)
						,SUPPLIER.REGISTRY.as(REG_FIELD)
						,SUPPLIER.ACCOUNT.as(ACC_FIELD)
						,SUPPLIER.SCOPE.as(SCP_FIELD)
						,SUPPLIER.TRANSACTION.as(ITR_FIELD)
						,SUPPLIER.WITHHOLDING.as(WTH_FIELD)
						,SUPPLIER.WITHHOLDING_FARMER.as(WTF_FIELD)
						,FALSE_TYPE.as(SUR_FIELD)
						,SUPPLIER.VAT_ACCRUAL_PAYMENT.as(VAP_FIELD)
						)
					.from(SUPPLIER)
					.where(SUPPLIER.DOMAIN.eq(ctx.getDomainId())
					.and(SecurityDAO.getUserScopesCondition(ctx,ctx.getUser(),SUPPLIER.SCOPE)))
				.unionAll(
				ctx.getDslContext().select(CUS_TYPE.as(TYP_FIELD)
						,CUSTOMER.REGISTRY.as(REG_FIELD)
						,CUSTOMER.ACCOUNT.as(ACC_FIELD)
						,CUSTOMER.SCOPE.as(SCP_FIELD)
						,CUSTOMER.TRANSACTION.as(ITR_FIELD)
						,CUSTOMER.WITHHOLDING.as(WTH_FIELD)
						,FALSE_TYPE.as(WTF_FIELD)
						,CUSTOMER.SURCHARGE.as(SUR_FIELD)
						,FALSE_TYPE.as(VAP_FIELD)
						)
					.from(CUSTOMER)
					.where(CUSTOMER.DOMAIN.eq(ctx.getDomainId())
					.and(SecurityDAO.getUserScopesCondition(ctx,ctx.getUser(),CUSTOMER.SCOPE))))
				.unionAll(
				ctx.getDslContext().select(CRE_TYPE.as(TYP_FIELD)
						,CREDITOR.REGISTRY.as(REG_FIELD)
						,CREDITOR.ACCOUNT.as(ACC_FIELD)
						,CREDITOR.SCOPE.as(SCP_FIELD)
						,CREDITOR.TRANSACTION.as(ITR_FIELD)
						,CREDITOR.WITHHOLDING.as(WTH_FIELD)
						,FALSE_TYPE.as(WTF_FIELD)
						,FALSE_TYPE.as(SUR_FIELD)
						,CREDITOR.VAT_ACCRUAL_PAYMENT.as(VAP_FIELD)
						)
					.from(CREDITOR)
					.where(CREDITOR.DOMAIN.eq(ctx.getDomainId())
					.and(SecurityDAO.getUserScopesCondition(ctx,ctx.getUser(),CREDITOR.SCOPE))))
				.asTable(AR)
				)
			.join(REGISTRY).on(REGISTRY.ID.eq(REG_FIELD))
			.leftOuterJoin(ACCOUNT).on(ACCOUNT.ID.eq(ACC_FIELD))
			.where(ACCOUNTING_REGISTRY_PROPERTIES.getConditions(filter))
			.and(SecurityDAO.getSecurityLevelCondition(ctx, ctx.getUser(), REGISTRY.SECURITY_LEVEL))
			.orderBy(REGISTRY.NAME)
			.fetch()
			.stream()
			.limit(10)
			.map(rec -> new AccountingRegistry()
					.setId( rec.getValue(REGISTRY.ID) )
					.setScope(rec.getValue(SCP_FIELD))
					.setAccountId(rec.getValue(ACCOUNT.ID))
					.setAccountCode(rec.getValue(ACCOUNT.CODE))
					.setAccountDescription(rec.getValue(ACCOUNT.DESCRIPTION))
					.setAlias(rec.getValue(REGISTRY.ALIAS))
					.setDocument(rec.getValue(REGISTRY.DOCUMENT))
					.setDocumentType(DocumentType.safeValueOf(rec.getValue(REGISTRY.DOCUMENT_TYPE)))
					.setDocumentCountry(Country.safeValueOf(rec.getValue(REGISTRY.DOCUMENT_COUNTRY)))
					.setName(rec.getValue(REGISTRY.NAME))
					.setType( AccountingRegistryType.values()[rec.getValue(TYP_FIELD)] )
					.setTransaction( InvoiceTransactionType.safeValueOf(rec.getValue(ITR_FIELD)))
					.setWithholding( AonEnumUtils.getBoolean(rec.getValue(WTH_FIELD)))
					.setWithholdingFarmer( AonEnumUtils.getBoolean(rec.getValue(WTF_FIELD)))
					.setSurcharge( AonEnumUtils.getBoolean(rec.getValue(SUR_FIELD)))
					.setVatAccrualPayment( AonEnumUtils.getBoolean(rec.getValue(VAP_FIELD)))
					);			
		
	}
	
}
