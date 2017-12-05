package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Carrier.CARRIER;
import static com.esferalia.aon.jooq.tables.Category.CATEGORY;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Question.QUESTION;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.RecordData.RECORD_DATA;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Ritem.RITEM;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Rnote.RNOTE;
import static com.esferalia.aon.jooq.tables.Rprofile.RPROFILE;
import static com.esferalia.aon.jooq.tables.Rsegment.RSEGMENT;
import static com.esferalia.aon.jooq.tables.Rseller.RSELLER;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Segment.SEGMENT;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;

import java.sql.Timestamp;
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
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.RmediaRecord;
import com.esferalia.aon.jooq.tables.records.SegmentRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Filter.CarrierFilter;
import com.esferalia.aon.occam.api.model.Filter.CategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.CustomerFilter;
import com.esferalia.aon.occam.api.model.Filter.PersonFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RecordDataFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryItemFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryNoteFilter;
import com.esferalia.aon.occam.api.model.Filter.SellerFilter;
import com.esferalia.aon.occam.api.model.Filter.SupplierFilter;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.Properties.RegistryAddressProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryMediaProperties;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryFilter;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryProperties;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.CommissionType;
import com.esferalia.aon.occam.api.model.registry.IAccountingRegistryTypeVisitor;
import com.esferalia.aon.occam.api.model.registry.Question;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.registry.RegistryProfile;
import com.esferalia.aon.occam.api.model.registry.Segment;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.CreditorStatus;
import com.esferalia.aon.occam.api.model.type.CustomerStatus;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.SupplierStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.CarrierFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.CustomerFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.PersonFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.RItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.RNoteFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.RecordDataFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.SupplierFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CarrierPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CategoryPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CustomerPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.PersonPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RItemPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RNotePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RecordDataPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.SellerPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.SupplierPropertiesDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

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
	
	private static final CustomerPropertiesDAO CUSTOMER_PROPERTIES = new CustomerPropertiesDAO();
	private static final SellerPropertiesDAO SELLER_PROPERTIES = new SellerPropertiesDAO();
	private static final CarrierPropertiesDAO CARRIER_PROPERTIES = new CarrierPropertiesDAO();
	private static final RecordDataPropertiesDAO RECORD_DATA_PROPERTIES = new RecordDataPropertiesDAO();
	private static final SupplierPropertiesDAO SUPPLIER_PROPERTIES = new SupplierPropertiesDAO();
	private static final CategoryPropertiesDAO CATEGORY_PROPERTIES = new CategoryPropertiesDAO();
	private static final PersonPropertiesDAO PERSON_PROPERTIES = new PersonPropertiesDAO();
	
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
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(REGISTRY.DOCUMENT_TYPE);}
		@Override public Property<String> getDocumentCountryProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.DOCUMENT_COUNTRY);}
	}
	
	private static final RAddressPropertiesDAO RADDRESS_PROPERTIES = new RAddressPropertiesDAO();
	private static class RAddressPropertiesDAO implements RegistryAddressProperties {
		private Condition[] getConditions(RegistryAddressFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(RADDRESS.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(RADDRESS.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<Integer>(RADDRESS.REGISTRY);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(RADDRESS.TYPE);}
		@Override public Property<String> getRecipientProperty() {return new FilterDAO.PropertyDAO<String>(RADDRESS.RECIPIENT);}
		@Override public Property<String> getStreetTypeProperty() {return new FilterDAO.PropertyDAO<String>(RADDRESS.STREET_TYPE);}
		@Override public Property<String> getAddressProperty() {return new FilterDAO.PropertyDAO<String>(RADDRESS.ADDRESS);}
		@Override public Property<String> getAddress2Property() {return new FilterDAO.PropertyDAO<String>(RADDRESS.ADDRESS2);}
		@Override public Property<String> getAddress3Property() {return new FilterDAO.PropertyDAO<String>(RADDRESS.ADDRESS3);}
		@Override public Property<String> getNumberProperty() {return new FilterDAO.PropertyDAO<String>(RADDRESS.NUMBER);}
		@Override public Property<String> getZipProperty() {return new FilterDAO.PropertyDAO<String>(RADDRESS.ZIP);}
		@Override public Property<String> getCityProperty() {return new FilterDAO.PropertyDAO<String>(RADDRESS.CITY);}
		@Override public Property<Integer> getGeozoneProperty() {return new FilterDAO.PropertyDAO<Integer>(RADDRESS.GEOZONE);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<String>(RADDRESS.ALIAS);}
		@Override public Property<String> getMunicipalityCodeProperty() {return new FilterDAO.PropertyDAO<String>(RADDRESS.MUNICIPALITY_CODE);}
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
	
	public static Stream<Category> getCategoryStream(AONContext ctx, CategoryFilter filter){
		return ctx.getDslContext()
				.select().from(CATEGORY)
				.where(CATEGORY_PROPERTIES.getConditions(filter))
				.fetchInto(CATEGORY).stream().map(new FullCategoryFiller());
	}
	
	public static Registry getRegistry(AONContext ctx, String name){
		return ctx.getDslContext().select().from(REGISTRY).where(REGISTRY.NAME.eq(name)).fetchInto(REGISTRY)
			.stream().map(new RegistryFiller()).findFirst().orElse(new Registry());
	}
	
	public static Registry getRegistry2(AONContext ctx, String document){
		return ctx.getDslContext().select().from(REGISTRY)
				.where(REGISTRY.DOMAIN.eq(ctx.getDomainId()))
				.and(REGISTRY.DOCUMENT.eq(document)).fetchInto(REGISTRY)
				.stream().map(new RegistryFiller()).findFirst().orElse(new Registry());
	}
	
	public static Registry getRegistry(AONContext ctx, Integer domainId, String name){
		return ctx.getDslContext().select().from(REGISTRY).where(REGISTRY.NAME.eq(name))
				.and(REGISTRY.DOMAIN.eq(domainId)).fetchInto(REGISTRY)
			.stream().map(new RegistryFiller()).findFirst().orElse(new Registry());
	}
	
	public static Registry getRegistry(AONContext ctx, Integer id){
		return ctx.getDslContext().select().from(REGISTRY).where(REGISTRY.ID.eq(id)).fetchInto(REGISTRY)
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
					.and(SUPPLIER.STATUS.ne(SupplierStatus.INACTIVE.value()))
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
					.and(CUSTOMER.STATUS.ne(CustomerStatus.INACTIVE.value()))
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
					.and(CREDITOR.STATUS.ne(CreditorStatus.INACTIVE.value()))
					.and(SecurityDAO.getUserScopesCondition(ctx,ctx.getUser(),CREDITOR.SCOPE))))
				.asTable(AR)
				)
			.join(REGISTRY).on(REGISTRY.ID.eq(REG_FIELD))
			.leftOuterJoin(ACCOUNT).on(ACCOUNT.ID.eq(ACC_FIELD))
			.where(ACCOUNTING_REGISTRY_PROPERTIES.getConditions(filter))
			.and(SecurityDAO.getSecurityLevelCondition(ctx, ctx.getUser(), REGISTRY.SECURITY_LEVEL))
			.orderBy(REGISTRY.NAME)
			.limit(30)
			.fetch()
			.stream()
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
	
	public static Account getCustomerAccount(AONContext ctx, Integer registry) {
		return ctx.getDslContext().select(ACCOUNT.fields())
			.from ( CUSTOMER )
			.join( ACCOUNT ).on(CUSTOMER.ACCOUNT.eq(ACCOUNT.ID))
			.where(CUSTOMER.REGISTRY.eq(registry))
			.fetch()
			.stream()
			.map(new FullAccountFiller() )
			.findFirst()
			.orElse(null);
	}
	public static Account getSupplierAccount(AONContext ctx, Integer registry) {
		return ctx.getDslContext().select(ACCOUNT.fields())
			.from ( SUPPLIER )
			.join( ACCOUNT ).on(SUPPLIER.ACCOUNT.eq(ACCOUNT.ID))
			.where(SUPPLIER.REGISTRY.eq(registry))
			.fetch()
			.stream()
			.map(new FullAccountFiller() )
			.findFirst()
			.orElse(null);
	}
	public static Account getCreditorAccount(AONContext ctx, Integer registry) {
		return ctx.getDslContext().select(ACCOUNT.fields())
			.from ( CREDITOR )
			.join( ACCOUNT ).on(CREDITOR.ACCOUNT.eq(ACCOUNT.ID))
			.where(CREDITOR.REGISTRY.eq(registry))
			.fetch()
			.stream()
			.map(new FullAccountFiller() )
			.findFirst()
			.orElse(null);
	}
	
	public static void updateCreditorAccount(AONContext ctx, Integer registry, Integer account) {
		ctx.checkWrite();
		ctx.getDslContext().update(CREDITOR)
			.set(CREDITOR.ACCOUNT,account)
			.set(CREDITOR.MODIFICATION_USER,ctx.getUser())
			.set(CREDITOR.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(CREDITOR.REGISTRY.eq(registry))
			.execute();
		ctx.log().info("ACCOUNT " + account + " LINKED TO CREDITOR " + registry);
	}

	public static void updateCustomerAccount(AONContext ctx, Integer registry, Integer account) {
		ctx.checkWrite();
		ctx.getDslContext().update(CUSTOMER)
		.set(CUSTOMER.ACCOUNT,account)
		.set(CUSTOMER.MODIFICATION_USER,ctx.getUser())
		.set(CUSTOMER.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
		.where(CUSTOMER.REGISTRY.eq(registry))
		.execute();
	ctx.log().info("ACCOUNT " + account + " LINKED TO CUSTOMER " + registry);
	}

	public static void updateSupplierAccount(AONContext ctx, Integer registry, Integer account) {
		ctx.getDslContext().update(SUPPLIER)
		.set(SUPPLIER.ACCOUNT,account)
		.set(SUPPLIER.MODIFICATION_USER,ctx.getUser())
		.set(SUPPLIER.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
		.where(SUPPLIER.REGISTRY.eq(registry))
		.execute();
	ctx.log().info("ACCOUNT " + account + " LINKED TO SUPPLIER " + registry);
	}
	
	public static AccountingRegistry insert(AONContext ctx, AccountingRegistry reg) {
		if (reg == null) throw new AonCoreException("No se puede grabar. Es nulo. (Error Interno)");
		if (reg.getType() == null) throw new AonCoreException("No se puede determinar el tipo. Es nulo. (Error Interno)");
		ctx.checkWrite();
		final Integer registryId = insert(ctx, new Registry()
				.setId(reg.getDomain())
				.setDomain(reg.getDomain())
				.setDocument(reg.getDocument())
				.setDocumentCountry(reg.getDocumentCountry())
				.setDocumentType(reg.getDocumentType())
				.setName(reg.getName())
				.setAlias(reg.getAlias())
				.setNationality(reg.getNationality())
				.setSecurityLevel(SecurityLevel.OFFICIAL)
				.setType(AonEnumUtils.getByte( AonDocumentUtil.isEntity(reg.getDocument())))
				);
		reg.setId(registryId);
		ctx.getDslContext().insertInto(RADDRESS)
			.set(RADDRESS.DOMAIN,reg.getDomain())
			.set(RADDRESS.REGISTRY, registryId)
			.set(RADDRESS.TYPE, (byte) 0)
			.set(RADDRESS.STREET_TYPE,reg.getAddressStreetType()==null?null:reg.getAddressStreetType().getAeatCode())
			.set(RADDRESS.ADDRESS,reg.getAddress())
			.set(RADDRESS.NUMBER,reg.getAddressNumber())
			.set(RADDRESS.ZIP,reg.getAddressZIP())
			.set(RADDRESS.CITY,reg.getAddressTown())
			.set(RADDRESS.GEOZONE,reg.getGeozone())
			.execute();
		if (!AonStringUtils.isBlank(reg.getPhone())) {
			ctx.getDslContext().insertInto(RMEDIA)
				.set(RMEDIA.DOMAIN,reg.getDomain())
				.set(RMEDIA.REGISTRY, registryId)
				.set(RMEDIA.MEDIA, MediaType.FIXED_PHONE.value())
				.set(RMEDIA.VALUE,reg.getPhone())
				.set(RMEDIA.COMMENT,reg.getPhoneComments())
				.set(RMEDIA.ADMINISTRATIVE,AonEnumUtils.getByte(true))
				.set(RMEDIA.COMMERCIAL,AonEnumUtils.getByte(true))
				.set(RMEDIA.TECHNICAL,AonEnumUtils.getByte(true))
				.execute();
		}
		if (!AonStringUtils.isBlank(reg.getCellular())) {
			ctx.getDslContext().insertInto(RMEDIA)
				.set(RMEDIA.DOMAIN,reg.getDomain())
				.set(RMEDIA.REGISTRY, registryId)
				.set(RMEDIA.MEDIA, MediaType.CELLULAR.value())
				.set(RMEDIA.VALUE,reg.getCellular())
				.set(RMEDIA.COMMENT,reg.getCellularComments())
				.set(RMEDIA.ADMINISTRATIVE,AonEnumUtils.getByte(true))
				.set(RMEDIA.COMMERCIAL,AonEnumUtils.getByte(true))
				.set(RMEDIA.TECHNICAL,AonEnumUtils.getByte(true))
				.execute();
		}
		if (!AonStringUtils.isBlank(reg.getFax())) {
			ctx.getDslContext().insertInto(RMEDIA)
				.set(RMEDIA.DOMAIN,reg.getDomain())
				.set(RMEDIA.REGISTRY, registryId)
				.set(RMEDIA.MEDIA, MediaType.FAX.value())
				.set(RMEDIA.VALUE,reg.getFax())
				.set(RMEDIA.COMMENT,reg.getFaxComments())
				.set(RMEDIA.ADMINISTRATIVE,AonEnumUtils.getByte(true))
				.set(RMEDIA.COMMERCIAL,AonEnumUtils.getByte(true))
				.set(RMEDIA.TECHNICAL,AonEnumUtils.getByte(true))
				.execute();
		}
		if (!AonStringUtils.isBlank(reg.getEmail())) {
			ctx.getDslContext().insertInto(RMEDIA)
				.set(RMEDIA.DOMAIN,reg.getDomain())
				.set(RMEDIA.REGISTRY, registryId)
				.set(RMEDIA.MEDIA, MediaType.EMAIL.value())
				.set(RMEDIA.VALUE,reg.getEmail())
				.set(RMEDIA.ADMINISTRATIVE,AonEnumUtils.getByte(true))
				.set(RMEDIA.COMMERCIAL,AonEnumUtils.getByte(true))
				.set(RMEDIA.TECHNICAL,AonEnumUtils.getByte(true))
				.execute();
		}
		if (!AonStringUtils.isBlank(reg.getWeb())) {
			ctx.getDslContext().insertInto(RMEDIA)
				.set(RMEDIA.DOMAIN,reg.getDomain())
				.set(RMEDIA.REGISTRY, registryId)
				.set(RMEDIA.MEDIA, MediaType.WEB.value())
				.set(RMEDIA.VALUE,reg.getWeb())
				.set(RMEDIA.ADMINISTRATIVE,AonEnumUtils.getByte(true))
				.set(RMEDIA.COMMERCIAL,AonEnumUtils.getByte(true))
				.set(RMEDIA.TECHNICAL,AonEnumUtils.getByte(true))
				.execute();
		}
		reg.getType().visit(reg, new IAccountingRegistryTypeVisitor() {
			
			@Override
			public void visitSupplier(AccountingRegistry reg) {
				ctx.getDslContext().insertInto(SUPPLIER)
					.set(SUPPLIER.REGISTRY, registryId)
					.set(SUPPLIER.DOMAIN,reg.getDomain())
					.set(SUPPLIER.WITHHOLDING, AonEnumUtils.getByte(reg.isWithholding()))
					.set(SUPPLIER.WITHHOLDING_FARMER, AonEnumUtils.getByte(reg.isWithholdingFarmer()))
					.set(SUPPLIER.VAT_ACCRUAL_PAYMENT, AonEnumUtils.getByte(reg.isVatAccrualPayment()))
					.set(SUPPLIER.TRANSACTION, reg.getTransaction() == null 
						? InvoiceTransactionType.NATIONAL.value() 
						: reg.getTransaction().value() )
					.set(SUPPLIER.STATUS, SupplierStatus.ACTIVE.value() )
					.set(SUPPLIER.SCOPE, reg.getScope() )
					.set(SUPPLIER.ACCOUNT, reg.getAccountId() )
					.set(SUPPLIER.CREATION_USER, ctx.getUser() )
					.set(SUPPLIER.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
					.execute();
			}
			
			@Override
			public void visitCustomer(AccountingRegistry reg) {
				ctx.getDslContext().insertInto(CUSTOMER)
					.set(CUSTOMER.REGISTRY, registryId)
					.set(CUSTOMER.DOMAIN,reg.getDomain())
					.set(CUSTOMER.SURCHARGE, AonEnumUtils.getByte(reg.isSurcharge()))
					.set(CUSTOMER.WITHHOLDING, AonEnumUtils.getByte(reg.isWithholding()))
					.set(CUSTOMER.TRANSACTION, reg.getTransaction() == null 
						? InvoiceTransactionType.NATIONAL.value() 
						: reg.getTransaction().value() )
					.set(CUSTOMER.STATUS, CustomerStatus.ACTIVE.value() )
					.set(CUSTOMER.SCOPE, reg.getScope() )
					.set(CUSTOMER.ACCOUNT, reg.getAccountId() )
					.set(CUSTOMER.CREATION_USER, ctx.getUser() )
					.set(CUSTOMER.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
					.execute();
			}
			
			@Override
			public void visitCreditor(AccountingRegistry reg) {
				ctx.getDslContext().insertInto(CREDITOR)
				.set(CREDITOR.REGISTRY, registryId)
				.set(CREDITOR.DOMAIN,reg.getDomain())
				.set(CREDITOR.WITHHOLDING, AonEnumUtils.getByte(reg.isWithholding()))
				.set(CREDITOR.VAT_ACCRUAL_PAYMENT, AonEnumUtils.getByte(reg.isVatAccrualPayment()))
				.set(CREDITOR.TRANSACTION, reg.getTransaction() == null 
					? InvoiceTransactionType.NATIONAL.value() 
					: reg.getTransaction().value() )
				.set(CREDITOR.STATUS, CreditorStatus.ACTIVE.value() )
				.set(CREDITOR.SCOPE, reg.getScope() )
				.set(CREDITOR.ACCOUNT, reg.getAccountId() )
				.set(CREDITOR.CREATION_USER, ctx.getUser() )
				.set(CREDITOR.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
				.execute();
			}
		}); 
		return reg;
	}

	private static Integer insert(AONContext ctx, Registry reg) {
		ctx.checkWrite();
		return ctx.getDslContext().insertInto(REGISTRY)
			.set(REGISTRY.DOMAIN,reg.getDomain())
			.set(REGISTRY.DOCUMENT,reg.getDocument())
			.set(REGISTRY.DOCUMENT_TYPE,reg.getDocumentType()==null?null:reg.getDocumentType().value())
			.set(REGISTRY.DOCUMENT_COUNTRY,reg.getDocumentCountry()==null?null:reg.getDocumentCountry().getIso2())
			.set(REGISTRY.NAME,reg.getName())
			.set(REGISTRY.ALIAS,reg.getAlias())
			.set(REGISTRY.TYPE,reg.getType())
			.set(REGISTRY.NATIONALITY,reg.getNationality()==null?null:reg.getNationality().getIso2())
			.set(REGISTRY.SECURITY_LEVEL,(reg.isConfidential()
					?SecurityLevel.CONFIDENTIAL.value()
					:SecurityLevel.OFFICIAL.value()))
			.returning(REGISTRY.ID)
			.fetchOne()
			.getValue(REGISTRY.ID);
	}
	
	// ------------------------------------- RMEDIA
	
	private static final RMediaPropertiesDAO RMEDIA_PROPERTIES = new RMediaPropertiesDAO();
	private static class RMediaPropertiesDAO implements RegistryMediaProperties {
		private Condition[] getConditions(RegistryMediaFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(RMEDIA.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(RMEDIA.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<Integer>(RMEDIA.REGISTRY);}
		@Override public Property<Byte> getMediaProperty() {return new FilterDAO.PropertyDAO<Byte>(RMEDIA.MEDIA);}
		@Override public Property<String> getValueProperty() {return new FilterDAO.PropertyDAO<String>(RMEDIA.VALUE);}
		@Override public Property<String> getCommentProperty() {return new FilterDAO.PropertyDAO<String>(RMEDIA.COMMENT);}
		@Override public Property<Byte> getAdministrativeProperty() {return new FilterDAO.PropertyDAO<Byte>(RMEDIA.ADMINISTRATIVE);}
		@Override public Property<Byte> getCommercialProperty() {return new FilterDAO.PropertyDAO<Byte>(RMEDIA.COMMERCIAL);}
		@Override public Property<Byte> getTechnicalProperty() {return new FilterDAO.PropertyDAO<Byte>(RMEDIA.TECHNICAL);}
		@Override public Property<Integer> getRaddressProperty() {return new FilterDAO.PropertyDAO<Integer>(RMEDIA.RADDRESS);}
	}
	
	public static Stream<RegistryMedia> getRMediaStream(AONContext ctx, RegistryMediaFilter filter){
		return ctx.getDslContext().select().from(RMEDIA).where(RMEDIA_PROPERTIES.getConditions(filter))
				.fetchInto(RMEDIA).stream().map(new RMediaFiller());
	}
	
	public static class RMediaFiller  implements Function<RmediaRecord,RegistryMedia> {

		@Override
		public RegistryMedia apply(RmediaRecord r) {
			return new RegistryMedia()
					.setId(r.getId())
					.setDomain(r.getDomain())
					.setComment(r.getComment())
					.setMedia(r.getMedia())
					.setRegistry(new Registry().setId(r.getRegistry()))
					.setAdministrative(r.getAdministrative())
					.setCommercial(r.getCommercial())
					.setTechnical(r.getTechnical())
					.setRaddress(r.getRaddress())
					.setValue(r.getValue());
		}
	}
	
	// ------------------------------------- RNOTE
	
	private static final RNotePropertiesDAO RNOTE_PROPERTIES = new RNotePropertiesDAO();
	
	public static Stream<RegistryNote> getRNoteStream(AONContext ctx, RegistryNoteFilter filter){
		ctx.checkRead();
		return ctx.getDslContext().select().from(RNOTE).where(RNOTE_PROPERTIES.getConditions(filter))
				.fetchInto(RNOTE).stream().map(new RNoteFiller());
	}
	
	// ------------------------------------- RITEM
	
	private static final RItemPropertiesDAO RITEM_PROPERTIES = new RItemPropertiesDAO();

	public static Stream<RegistryItem> getRItemStream(AONContext ctx, RegistryItemFilter filter){
		ctx.checkRead();
		return ctx.getDslContext().select().from(RITEM).where(RITEM_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new RItemFiller( ));
	}
	
	// ------------------------------------- RSEGMENT

	public static Stream<Segment> getSegments(AONContext ctx){
		return ctx.getDslContext().select().from(SEGMENT)
				.where(SEGMENT.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
				.orderBy(SEGMENT.NAME)
				.fetchInto(SEGMENT)
				.stream()
				.map(new SegmentFiller());
	}

	public static Stream<Segment> getRSegmentStream(AONContext ctx, Integer registryId){
		return ctx.getDslContext().select().from(RSEGMENT)
				.join(SEGMENT).on(RSEGMENT.SEGMENT.eq(SEGMENT.ID))
				.where(RSEGMENT.REGISTRY.eq(registryId))
				.fetchInto(SEGMENT).stream().map(new SegmentFiller());
	}

	public static class SegmentFiller  implements Function<SegmentRecord,Segment> {

		@Override
		public Segment apply(SegmentRecord r) {
			return new Segment()
					.setId(r.getId())
					.setDomain(r.getDomain())
					.setName(r.getName());
		}
	}
	
	public static Stream<Seller> getRSellerStream(AONContext ctx, Integer registryId){
		return ctx.getDslContext().select().from(RSELLER)
				.join(REGISTRY).on(RSELLER.SELLER.eq(REGISTRY.ID))
				.where(RSELLER.REGISTRY.eq(registryId))
				.fetchInto(REGISTRY).stream().map(new SellerFiller());
	}
	
	public static class SellerFiller  implements Function<RegistryRecord,Seller> {

		@Override
		public Seller apply(RegistryRecord r) {
			return new Seller()
					.setId(r.getId())
					.setDomain(r.getDomain())
					.setRegistryName(r.getName());
		}
	}
	
	public static Stream<RAddress> getRAddressStream(AONContext ctx, Integer registryId){
		return ctx.getDslContext().select().from(RADDRESS)
					.join(REGISTRY).on(RADDRESS.REGISTRY.eq(REGISTRY.ID))
					.join(GEOZONE).on(RADDRESS.GEOZONE.eq(GEOZONE.ID))
				.where(RADDRESS.REGISTRY.eq(registryId))
				.fetch().stream().map(new RAddressFiller());
	}

	public static Stream<RAddress> getRAddressStream(AONContext ctx,
			RegistryAddressFilter filter) {
		return ctx.getDslContext().select().from(RADDRESS)
					.join(REGISTRY).on(RADDRESS.REGISTRY.eq(REGISTRY.ID))
					.join(GEOZONE).on(RADDRESS.GEOZONE.eq(GEOZONE.ID))
				.where(RADDRESS_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new RAddressFiller());
	}
	
	public static RAddress insertRAddress(AONContext ctx, RAddress raddress){
		return ctx.getDslContext().insertInto(RADDRESS, RADDRESS.ADDRESS, RADDRESS.ADDRESS2, RADDRESS.ADDRESS3, RADDRESS.ALIAS,
				RADDRESS.CITY, RADDRESS.DOMAIN, RADDRESS.GEOZONE, RADDRESS.MUNICIPALITY_CODE, RADDRESS.NUMBER, RADDRESS.RECIPIENT,
				RADDRESS.REGISTRY, RADDRESS.STREET_TYPE, RADDRESS.TYPE, RADDRESS.ZIP)
			.values(raddress.getAddress(), raddress.getAddress2(), raddress.getAddress3(), raddress.getAlias(),
					raddress.getCity(), raddress.getDomain(), raddress.getGeozone(), raddress.getMunicipality_code(), raddress.getNumber(), raddress.getRecipient(),
					raddress.getRegistry(), raddress.getStreet_type(), raddress.getType(), raddress.getZip()).returning()
			.fetch().stream().map(new RAddressFiller()).findFirst().orElse(new RAddress());
	}
	
	public static Registry insertRegistry(AONContext ctx, Registry registry){
		return ctx.getDslContext().insertInto(REGISTRY, REGISTRY.ALIAS, REGISTRY.DOCUMENT, REGISTRY.DOMAIN,
				REGISTRY.NAME, REGISTRY.NATIONALITY, REGISTRY.TYPE)
			.values(registry.getAlias(), registry.getDocument(), registry.getDomain(),
				registry.getName(), "ES", registry.getType()).returning()
			.fetch().stream().map(new RegistryFiller()).findFirst().orElse(new Registry());
	}
	
	public static Registry updateRegistry(AONContext ctx, Registry registry){
		return ctx.getDslContext().update(REGISTRY)
			.set(REGISTRY.ALIAS, registry.getAlias()).set(REGISTRY.NAME, registry.getName())
			.where(REGISTRY.ID.eq(registry.getId()))
			.returning().fetch().stream().map(new RegistryFiller()).findFirst().orElse(new Registry());		
	}
	
	public static Registry deleteRegistry(AONContext ctx, Integer registry){
		return ctx.getDslContext().delete(REGISTRY).where(REGISTRY.ID.eq(registry))
			.returning().fetch().stream().map(new RegistryFiller()).findFirst().orElse(new Registry());
	}
	
	public static RegistryMedia insertRMedia(AONContext ctx, RegistryMedia rmedia){
		return ctx.getDslContext().insertInto(RMEDIA, RMEDIA.ADMINISTRATIVE, RMEDIA.COMMENT, RMEDIA.COMMERCIAL, RMEDIA.DOMAIN, RMEDIA.MEDIA,
				RMEDIA.RADDRESS, RMEDIA.REGISTRY, RMEDIA.TECHNICAL, RMEDIA.VALUE)
			.values(rmedia.getAdministrative(), rmedia.getComment(), rmedia.getCommercial(), rmedia.getDomain(), rmedia.getMedia(),
					rmedia.getRaddress(), rmedia.getRegistry().getId(), rmedia.getTechnical(), rmedia.getValue()).returning()
			.fetch().stream().map(new RMediaFiller()).findFirst().orElse(new RegistryMedia());
	}
	
	public static RegistryMedia updateRMedia(AONContext ctx, RegistryMedia rmedia){
		return ctx.getDslContext().update(RMEDIA)
			.set(RMEDIA.VALUE, rmedia.getValue())
			.where(RMEDIA.ID.eq(rmedia.getId()))
			.returning()
			.fetch().stream().map(new  RMediaFiller()).findFirst().orElse(new RegistryMedia());
	}
	
	public static RegistryMedia deleteRMedia(AONContext ctx, Integer registry){
		return ctx.getDslContext().delete(RMEDIA)
				.where(RMEDIA.REGISTRY.eq(registry)).returning()
				.fetch().stream().map(new RMediaFiller()).findFirst().orElse(new RegistryMedia());
	}
	

	public static class RAddressFiller  implements Function<Record, RAddress> {

		@Override
		public RAddress apply(Record r) {
			return new RAddress()
					.setId(r.getValue(RADDRESS.ID))
					.setDomain(r.getValue(RADDRESS.DOMAIN))
					.setAddress(r.getValue(RADDRESS.ADDRESS))
					.setAddress2(r.getValue(RADDRESS.ADDRESS2))
					.setAddress3(r.getValue(RADDRESS.ADDRESS3))
					.setAlias(r.getValue(RADDRESS.ALIAS))
					.setCity(r.getValue(RADDRESS.CITY))
					.setGeozone(r.getValue(RADDRESS.GEOZONE))
					.setGeozoneName(r.getValue(GEOZONE.NAME))
					.setMunicipality_code(r.getValue(RADDRESS.MUNICIPALITY_CODE))
					.setNumber(r.getValue(RADDRESS.NUMBER))
					.setRecipient(r.getValue(RADDRESS.RECIPIENT))
					.setRegistry(r.getValue(RADDRESS.REGISTRY))
					.setRegistryName(r.getValue(REGISTRY.NAME))
					.setStreet_type(r.getValue(RADDRESS.STREET_TYPE))
					.setType(r.getValue(RADDRESS.TYPE))
					.setZip(r.getValue(RADDRESS.ZIP));
		}
	}
	
	// ------------------- CUSTOMER
	
	public static Stream<Customer> getCustomerStream(AONContext ctx, CustomerFilter filter){
		return ctx.getDslContext().select().from(CUSTOMER)
				.join(REGISTRY).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
				.where(CUSTOMER_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new CustomerFiller());
	}
	
	public static Customer insertCustomer(AONContext ctx, Customer customer){
		return ctx.getDslContext().insertInto(CUSTOMER, CUSTOMER.ACCOUNT, CUSTOMER.DELIVERY_GROUPED, CUSTOMER.DELIVERY_VALUATED,
				CUSTOMER.DOMAIN, CUSTOMER.E_INVOICE, CUSTOMER.INVOICING_GROUP, CUSTOMER.PROJECT_GROUPED, CUSTOMER.REGISTRY,
				CUSTOMER.SCOPE, CUSTOMER.STATUS, CUSTOMER.SURCHARGE, CUSTOMER.TARIFF, CUSTOMER.TRANSACTION, CUSTOMER.WITHHOLDING)
			.values(customer.getAccount(), customer.getDeliveryGrouped(), customer.getDeliveryValuated(), 
					customer.getDomain(), customer.geteInvoice(), customer.getInvoicingGroup(), customer.getProjectGrouped(), customer.getRegistry().getId(),
					customer.getScope(), customer.getStatus().value(), customer.getSurcharge(), customer.getTariff(), customer.getTransaction(), customer.getWithholding()).returning()
			.fetch().stream().map(new CustomerFiller()).findFirst().orElse(new Customer());
	}
	// ------------------- SELLER

	public static Stream<Seller> getSellerStream(AONContext ctx, SellerFilter filter){
		return ctx.getDslContext().select()
				.from(SELLER).join(SCOPE).on(SELLER.SCOPE.eq(SCOPE.ID))
				.join(REGISTRY).on(REGISTRY.ID.eq(SELLER.REGISTRY))
				.where(SELLER_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new FullSellerFiller());
	}
	
	public static Stream<Seller> getSellers(AONContext ctx){
		return ctx.getDslContext().select(SELLER.REGISTRY, SELLER.DOMAIN, SELLER.COMMISSION_TYPE, SELLER.SCOPE, SELLER.STATUS,
				SCOPE.DESCRIPTION, REGISTRY.DOCUMENT, REGISTRY.NAME, REGISTRY.ALIAS, REGISTRY.DOCUMENT_COUNTRY, REGISTRY.DOCUMENT_TYPE,
				REGISTRY.NATIONALITY, REGISTRY.SECURITY_LEVEL)
				.from(SELLER).join(SCOPE).on(SELLER.SCOPE.eq(SCOPE.ID))
				.join(REGISTRY).on(REGISTRY.ID.eq(SELLER.REGISTRY))
				.where(SELLER.DOMAIN.eq(ctx.getDomainId()))
				.and(SecurityDAO.getUserScopesCondition(ctx, SELLER.SCOPE))
				.and(SecurityDAO.getSecurityLevelCondition(ctx, REGISTRY.SECURITY_LEVEL))
				.orderBy( REGISTRY.NAME )
				.fetch()
				.stream()
				.map(new FullSellerFiller());
	}
	
	private static class FullSellerFiller implements Function<Record, Seller> {
		@Override
		public Seller apply(Record r) {
			return new Seller()
					.setDomain(r.getValue(SELLER.DOMAIN))
					.setId(r.getValue(SELLER.REGISTRY))
					.setActive(r.getValue(SELLER.STATUS) == 1)
					.setCommissionType(new CommissionType().setId(r.getValue(SELLER.COMMISSION_TYPE)))
					.setScope(r.getValue(SCOPE.DESCRIPTION))
					
					.setRegistryAlias(r.getValue(REGISTRY.ALIAS))
					.setRegistryConfidential(r.getValue(REGISTRY.SECURITY_LEVEL) == 1)
					.setRegistryDocument(r.getValue(REGISTRY.DOCUMENT))
					.setRegistryDocumentCountry(Country.valueOf(r.getValue(REGISTRY.DOCUMENT_COUNTRY)))
					.setRegistryName(r.getValue(REGISTRY.NAME))
					.setRegistryDocumentType(DocumentType.values()[r.getValue(REGISTRY.DOCUMENT_TYPE)])
					.setRegistryNationality(Country.valueOf(r.getValue(REGISTRY.NATIONALITY)))
					;			
		}
	}
	
	// ------------------- CARRIER

	public static Stream<Carrier> getCarrierStream(AONContext ctx, CarrierFilter filter){
		return ctx.getDslContext().select()
				.from(CARRIER).join(SCOPE).on(CARRIER.SCOPE.eq(SCOPE.ID))
				.join(REGISTRY).on(REGISTRY.ID.eq(CARRIER.REGISTRY))
				.where(CARRIER_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new CarrierFiller());
	}
	
	// ------------------- SUPPLIER

	public static Stream<Supplier> getSupplierStream(AONContext ctx, SupplierFilter filter){
		return SUPPLIER_PROPERTIES.build(ctx.getDslContext().select()
					.from(SUPPLIER).join(SCOPE).on(SUPPLIER.SCOPE.eq(SCOPE.ID))
					.join(REGISTRY).on(REGISTRY.ID.eq(SUPPLIER.REGISTRY))
			,filter).fetch().stream().map(new SupplierFiller());
	}
	
	// ------------------- SUPPLIER

	public static Stream<Person> getPersonStream(AONContext ctx, PersonFilter filter){
		return PERSON_PROPERTIES.build(ctx.getDslContext().select()
				.from(PERSON).join(REGISTRY).on(REGISTRY.ID.eq(PERSON.REGISTRY))
			,filter).fetch().stream().map(new PersonFiller());
	}
		
	// ------------------- RECORD DATA

	public static Stream<RecordData> getRecordDataStream(AONContext ctx, RecordDataFilter filter){
		return ctx.getDslContext().select()
				.from(RECORD_DATA)
				.where(RECORD_DATA_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new RecordDataFiller());
	}
		
	// ------------------- REGISTRY PROFILE
	
	public static Stream<Question> getRegistryQuestionStream(AONContext ctx, Integer registry){
		return ctx.getDslContext().selectDistinct(QUESTION.ID, QUESTION.DOMAIN, QUESTION.ACTIVE, QUESTION.QUESTION_TEXT,
										QUESTION.TYPE, QUESTION.ARGUMENT, QUESTION.ALIAS)
				.from(QUESTION).join(RPROFILE).on(QUESTION.ID.eq(RPROFILE.QUESTION))
				.where(RPROFILE.REGISTRY.eq(registry))
				.fetch().stream().map(new QuestionFiller());
	}
	
	public static Stream<RegistryProfile> getRegistryProfileStream(AONContext ctx, Integer registry, Integer question){
		return ctx.getDslContext().select().from(RPROFILE)
				.where(RPROFILE.REGISTRY.eq(registry))
					.and(RPROFILE.QUESTION.eq(question))
				.fetch().stream().map(new RegistryProfileFiller());
	}
	
	public static class QuestionFiller  implements Function<Record, Question> {

		@Override
		public Question apply(Record r) {
			return new Question()
					.setId(r.getValue(QUESTION.ID))
					.setActive(r.getValue(QUESTION.ACTIVE))
					.setAlias(r.getValue(QUESTION.ALIAS))
					.setArgument(r.getValue(QUESTION.ARGUMENT))
					.setDomain(r.getValue(QUESTION.DOMAIN))
					.setQuestionText(r.getValue(QUESTION.QUESTION_TEXT))
					.setType(r.getValue(QUESTION.TYPE));
		}
	}
	
	public static class RegistryProfileFiller  implements Function<Record, RegistryProfile> {

		@Override
		public RegistryProfile apply(Record r) {
			return new RegistryProfile()
					.setId(r.getValue(RPROFILE.ID))
					.setRegistry(r.getValue(RPROFILE.REGISTRY))
					.setDomain(r.getValue(RPROFILE.DOMAIN))
					.setLastUpdate(r.getValue(RPROFILE.LAST_UPDATE))
					.setQuestion(r.getValue(RPROFILE.QUESTION))
					.setValueText(r.getValue(RPROFILE.VALUE_TEXT))
					.setValueDate(r.getValue(RPROFILE.VALUE_DATE))
					.setValueNumber(r.getValue(RPROFILE.VALUE_NUMBER));
		}
	}
	
}
