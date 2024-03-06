package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Category.CATEGORY;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Question.QUESTION;
import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.RecordData.RECORD_DATA;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Ritem.RITEM;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Rprofile.RPROFILE;
import static com.esferalia.aon.jooq.tables.Rsegment.RSEGMENT;
import static com.esferalia.aon.jooq.tables.Rseller.RSELLER;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Segment.SEGMENT;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;
import static com.esferalia.aon.occam.impl.jooq.dao.SellerDAO.SELLER_ALIAS;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.jooq.tables.Item;
import com.esferalia.aon.jooq.tables.Product;
import com.esferalia.aon.jooq.tables.records.CategoryRecord;
import com.esferalia.aon.jooq.tables.records.SegmentRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.CarrierFilter;
import com.esferalia.aon.occam.api.model.Filter.CategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.CreditorFilter;
import com.esferalia.aon.occam.api.model.Filter.PersonFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RecordDataFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddInfoFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryBankFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryItemFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistrySegmentFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistrySellerFilter;
import com.esferalia.aon.occam.api.model.Filter.SupplierFilter;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.Properties.RegistryAddressProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryMediaProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistrySegmentProperties;
import com.esferalia.aon.occam.api.model.commission.CommissionType;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Question;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryProfile;
import com.esferalia.aon.occam.api.model.registry.Segment;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO.CreditorFiller;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO.CreditorPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CustomerPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.PersonFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.RItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.RecordDataFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CategoryPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.PersonPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RItemPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RecordDataPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RegistryAddInfoPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RegistryPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RegistrySellerPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.SellerPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.SupplierPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.TargetPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryBankDAO.RBankPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryBankDAO.RegistryBankFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SellerDAO.SellerFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO.SupplierFiller;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;

@Deprecated
public class RegistryOldDAO {
	
	private static final CustomerPropertiesDAO CUSTOMER_PROPERTIES = new CustomerPropertiesDAO();
	private static final SellerPropertiesDAO SELLER_PROPERTIES = new SellerPropertiesDAO();
	private static final RegistrySellerPropertiesDAO RSELLER_PROPERTIES = new RegistrySellerPropertiesDAO();
	private static final RecordDataPropertiesDAO RECORD_DATA_PROPERTIES = new RecordDataPropertiesDAO();
	private static final RBankPropertiesDAO RBANK_PROPERTIES = new RBankPropertiesDAO();
	private static final RegistryAddInfoPropertiesDAO RADDINFO_PROPERTIES = new RegistryAddInfoPropertiesDAO();
	private static final SupplierPropertiesDAO SUPPLIER_PROPERTIES = new SupplierPropertiesDAO();

	private static final TargetPropertiesDAO TARGET_PROPERTIES = new TargetPropertiesDAO();
	private static final RegistryPropertiesDAO REGISTRY_PROPERTIES = new RegistryPropertiesDAO();
	private static final CategoryPropertiesDAO CATEGORY_PROPERTIES = new CategoryPropertiesDAO();
	private static final PersonPropertiesDAO PERSON_PROPERTIES = new PersonPropertiesDAO();
	
	private static final CreditorPropertiesDAO CREDITOR_PROPERTIES = new CreditorPropertiesDAO();

	/**
	 * @deprecated  Replaced by RegistryAddressDAO.RAddressPropertiesDAO
	 */
	@Deprecated(forRemoval = true )
	private static final RAddressPropertiesDAO RADDRESS_PROPERTIES = new RAddressPropertiesDAO();
	/**
	 * @deprecated  Replaced by RegistryAddressDAO.RAddressPropertiesDAO
	 */
	@Deprecated(forRemoval = true )
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
	
	private static final RegistrySegmentPropertiesDAO RSEGMENT_PROPERTIES = new RegistrySegmentPropertiesDAO();
	private static class RegistrySegmentPropertiesDAO implements RegistrySegmentProperties {
		private Condition[] getConditions(RegistrySegmentFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(RSEGMENT.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(RSEGMENT.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<Integer>(RSEGMENT.REGISTRY);}
		@Override public Property<Integer> getSegmentProperty() {return new FilterDAO.PropertyDAO<Integer>(RSEGMENT.SEGMENT);}

	}
	
	
	/**
	 * @deprecated  
	 */
	public static Category getCategory(AONContext ctx, Integer categoryId){
		return ctx.getDslContext()
				.select().from(CATEGORY).where(CATEGORY.ID.eq(categoryId)).limit(1)
				.fetchInto(CATEGORY).stream().map(new FullCategoryFiller()).findFirst().orElse(new Category());
	}
	
	/**
	 * @deprecated  
	 */
	public static Category insertCategory(AONContext ctx, Category category){
		CategoryRecord cr = ctx.getDslContext()
				.insertInto(CATEGORY)
				.set(CATEGORY.DOMAIN, category.getDomain())
				.set(CATEGORY.NAME, category.getName())
				.set(CATEGORY.TYPE, category.getType())
				.returning().fetchOne();
		return new FullCategoryFiller().apply(cr);
	}
	
	/**
	 * @deprecated  
	 */
	public static Category updateCategory(AONContext ctx, Category category){
		ctx.getDslContext().update(CATEGORY)
			.set(CATEGORY.NAME, category.getName())
			.where(CATEGORY.ID.eq(category.getId()))
			.execute();
		return category;
	}
	
	/**
	 * @deprecated  
	 */
	public static Category deleteCategory(AONContext ctx, Integer categoryId){
		Integer nullvalue = null;
		ctx.getDslContext()
			.update(RATTACH)
		.set(RATTACH.CATEGORY, nullvalue)
		.where(RATTACH.CATEGORY.eq(categoryId))
		.execute();

		ctx.getDslContext().delete(CATEGORY)
			.where(CATEGORY.ID.eq(categoryId))
			.execute();
		return new Category();
	}
	
	/**
	 * @deprecated  
	 */
	public static LinkedList<Category> getCategoryList(AONContext ctx){
		return ctx.getDslContext()
				.select().from(CATEGORY).where(CATEGORY.DOMAIN.eq(ctx.getDomainId())).limit(1)
				.fetchInto(CATEGORY).stream().map(new FullCategoryFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	/**
	 * @deprecated  
	 */
	public static Stream<Category> getCategoryStream(AONContext ctx, CategoryFilter filter){
		return ctx.getDslContext()
				.select().from(CATEGORY)
				.where(CATEGORY_PROPERTIES.getConditions(filter))
				.fetchInto(CATEGORY).stream().map(new FullCategoryFiller());
	}
	
	
	/**
	 * @deprecated  Replaced by RegistryAddressDAO.getStream
	 */
	@Deprecated(forRemoval = true )
	public static Stream<Registry> getAonRegistryStream(AONContext ctx, RegistryFilter filter){
		return ctx.getDslContext().select().from(REGISTRY)
				.leftOuterJoin(RADDRESS).on(RADDRESS.REGISTRY.eq(REGISTRY.ID))
				.leftOuterJoin(GEOZONE).on(GEOZONE.ID.eq(RADDRESS.GEOZONE))
				.where(REGISTRY_PROPERTIES.getConditions(filter)).fetch()
			.stream().map(new FullRegistryFiller());
	}
	
	/**
	 * @deprecated  Replaced by com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.getRegistryStream(AONContext ctx, RegistryFilter filter)
	 */
	@Deprecated(forRemoval = true )
	public static Stream<Registry> getRegistryStream(AONContext ctx, RegistryFilter filter){
		return ctx.getDslContext().select().from(REGISTRY)
				.where(REGISTRY_PROPERTIES.getConditions(filter)).fetch()
			.stream().map(new RegistryFiller());
	}
	
	/**
	 * @deprecated  Replaced by com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.getRegistry(AONContext ctx, Integer id)
	 */
	@Deprecated(forRemoval = true )
	public static Registry getRegistry(AONContext ctx, RegistryFilter filter){
		return getRegistryStream(ctx, filter).findFirst().orElse(new Registry());
	}
	
	@Deprecated 
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
	
	/**
	 * @deprecated  Replaced by RegistryDAO.RegistryFiller
	 */
	@Deprecated(forRemoval = true )
	public static class RegistryFiller  implements Function<Record,Registry> {

		@Override
		public Registry apply(Record record) {
			return new Registry()
					.setId(record.getValue(REGISTRY.ID))
					.setDomain(new Domain().setId(record.getValue(REGISTRY.DOMAIN)))
					.setAlias(record.getValue(REGISTRY.ALIAS))
					.setDocument(record.getValue(REGISTRY.DOCUMENT))
					.setDocumentType(DocumentType.safeValueOf(record.getValue(REGISTRY.DOCUMENT_TYPE)))
					.setDocumentCountry(Country.safeValueOf(record.getValue(REGISTRY.DOCUMENT_COUNTRY)))
					.setName(record.getValue(REGISTRY.NAME))
					.setNationality(Country.safeValueOf(record.getValue(REGISTRY.NATIONALITY)))
					.setSecurityLevel(SecurityLevel.safeValueOf( record.getValue(REGISTRY.SECURITY_LEVEL)))
					.setLegalPerson( AonEnumUtils.getBoolean( record.getValue(REGISTRY.TYPE) ))
				;
		}
		
	}
	
	/**
	 * @deprecated  Replaced by RegistryDAO.RegistryFiller
	 */
	@Deprecated(forRemoval = true )
	public static class FullRegistryFiller  implements Function<Record,Registry> {

		@Override
		public Registry apply(Record record) {
			RAddress address = new RAddress()
					.setId(record.getValue(RADDRESS.ID))
					.setDomain(record.getValue(RADDRESS.DOMAIN))
					.setAddress(record.getValue(RADDRESS.ADDRESS))
					.setAddress2(record.getValue(RADDRESS.ADDRESS2))
					.setAddress3(record.getValue(RADDRESS.ADDRESS3))
					.setAlias(record.getValue(RADDRESS.ALIAS))
					.setCity(record.getValue(RADDRESS.CITY))
					.setGeozone(record.getValue(RADDRESS.GEOZONE))
					.setGeozoneName(record.getValue(GEOZONE.NAME))
					.setMunicipality_code(record.getValue(RADDRESS.MUNICIPALITY_CODE))
					.setNumber(record.getValue(RADDRESS.NUMBER))
					.setRecipient(record.getValue(RADDRESS.RECIPIENT))
					.setRegistry(record.getValue(RADDRESS.REGISTRY))
					.setRegistryName(record.getValue(REGISTRY.NAME))
					.setStreet_type(record.getValue(RADDRESS.STREET_TYPE))
					.setType(record.getValue(RADDRESS.TYPE))
					.setZip(record.getValue(RADDRESS.ZIP));
			return new Registry()
					.setId(record.getValue(REGISTRY.ID))
					.setDomain(new Domain().setId(record.getValue(REGISTRY.DOMAIN)))
					.setAlias(record.getValue(REGISTRY.ALIAS))
					.setDocument(record.getValue(REGISTRY.DOCUMENT))
					.setDocumentType(DocumentType.safeValueOf(record.getValue(REGISTRY.DOCUMENT_TYPE)))
					.setDocumentCountry(Country.safeValueOf(record.getValue(REGISTRY.DOCUMENT_COUNTRY)))
					.setName(record.getValue(REGISTRY.NAME))
					.setNationality(Country.safeValueOf(record.getValue(REGISTRY.NATIONALITY)))
					.setSecurityLevel(SecurityLevel.safeValueOf( record.getValue(REGISTRY.SECURITY_LEVEL)))
					.setLegalPerson(AonEnumUtils.getBoolean(record.getValue(REGISTRY.TYPE)))
					.setMainAddress(address)
				;
		}
		
	}
	
	/**
	 * @deprecated  Use CustomerDAO.getCustomerAccount
	 */
	@Deprecated
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
	/**
	 * @deprecated  Use SupplierDAO.getSupplierAccount
	 */
	@Deprecated
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
	/**
	 * @deprecated  Use CreditorDAO.getCreditorAccount
	 */
	@Deprecated
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
	
	/**
	 * @deprecated  Use CreditorDAO.updateCreditorAccount
	 */
	@Deprecated
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

	/**
	 * @deprecated  Use CustomerDAO.updateCustomerAccount
	 */
	@Deprecated
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

	/**
	 * @deprecated  Use SupplierDAO.updateSupplierAccount
	 */
	@Deprecated
	public static void updateSupplierAccount(AONContext ctx, Integer registry, Integer account) {
		ctx.getDslContext().update(SUPPLIER)
		.set(SUPPLIER.ACCOUNT,account)
		.set(SUPPLIER.MODIFICATION_USER,ctx.getUser())
		.set(SUPPLIER.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
		.where(SUPPLIER.REGISTRY.eq(registry))
		.execute();
	ctx.log().info("ACCOUNT " + account + " LINKED TO SUPPLIER " + registry);
	}
	
	/**
	 * @deprecated  Replaced by com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.save(AONContext ctx, Registry reg)
	 */
	@Deprecated(forRemoval = true )
	protected static Integer insert(AONContext ctx, Registry reg) {
		ctx.checkWrite();
		return ctx.getDslContext().insertInto(REGISTRY)
			.set(REGISTRY.DOMAIN, reg.getDomain().getId())
			.set(REGISTRY.DOCUMENT,reg.getDocument())
			.set(REGISTRY.DOCUMENT_TYPE,reg.getDocumentType()==null?null:reg.getDocumentType().value())
			.set(REGISTRY.DOCUMENT_COUNTRY,reg.getDocumentCountry()==null?null:reg.getDocumentCountry().getIso2())
			.set(REGISTRY.NAME,reg.getName())
			.set(REGISTRY.ALIAS,reg.getAlias())
			.set(REGISTRY.TYPE,AonEnumUtils.getByte(reg.isLegalPerson()))
			.set(REGISTRY.NATIONALITY,reg.getNationality()==null?null:reg.getNationality().getIso2())
			.set(REGISTRY.SECURITY_LEVEL,(reg.isConfidential()
					?SecurityLevel.CONFIDENTIAL.value()
					:SecurityLevel.OFFICIAL.value()))
			.returning(REGISTRY.ID)
			.fetchOne()
			.getValue(REGISTRY.ID);
	}
	
	// ------------------------------------- RMEDIA
	
	/**
	 * @deprecated  Replaced by RegistryMediaDAO.RMediaPropertiesDAO
	 */
	@Deprecated(forRemoval = true )
	private static final RMediaPropertiesDAO RMEDIA_PROPERTIES = new RMediaPropertiesDAO();
	/**
	 * @deprecated  Replaced by RegistryMediaDAO.RMediaPropertiesDAO
	 */
	@Deprecated(forRemoval = true )
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
	
	/**
	 * @deprecated  Replaced by RegistryMediaDAO.getStream
	 */
	@Deprecated(forRemoval = true )
	public static Stream<RegistryMedia> getRMediaStream(AONContext ctx, RegistryMediaFilter filter){
		return ctx.getDslContext().select().from(RMEDIA).where(RMEDIA_PROPERTIES.getConditions(filter))
				.fetchInto(RMEDIA).stream().map(new RMediaFiller());
	}
	
	/**
	 * @deprecated  Replaced by RegistryMediaDAO.RegistryMediaFiller
	 */
	@Deprecated(forRemoval = true )
	public static class RMediaFiller  implements Function<Record,RegistryMedia> {

		@Override
		public RegistryMedia apply(Record r) {
			return buildRmedia(r);
		}
		
		public static RegistryMedia buildRmedia(Record r) {
			return new RegistryMedia()
					.setId(r.getValue(RMEDIA.ID))
					.setDomain(r.getValue(RMEDIA.DOMAIN))
					.setComment(r.getValue(RMEDIA.COMMENT))
					.setMedia(MediaType.safeValueOf(r.getValue(RMEDIA.MEDIA)))
					.setRegistry(r.getValue(RMEDIA.REGISTRY))
					.setAdministrative(r.getValue(RMEDIA.ADMINISTRATIVE) == 1)
					.setCommercial(r.getValue(RMEDIA.COMMERCIAL) == 1)
					.setTechnical(r.getValue(RMEDIA.TECHNICAL) == 1)
					.setRaddress(r.getValue(RMEDIA.RADDRESS))
					.setValue(r.getValue(RMEDIA.VALUE));
		}
	}
	
	// ------------------------------------- RITEM
	
	private static final RItemPropertiesDAO RITEM_PROPERTIES = new RItemPropertiesDAO();

	public static Stream<RegistryItem> getRItemStream(AONContext ctx, RegistryItemFilter filter){
		ctx.checkRead();
		return ctx.getDslContext()
				.select()
				.from(RITEM)
				.innerJoin(ITEM).on(ITEM.ID.eq(RITEM.ITEM))
				.innerJoin(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
				.where(RITEM_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new RItemFiller( ));
	}
	
	public static Stream<RegistryItem> getRItemStream(AONContext ctx, RegistryItemFilter filter, int limit, int offset){
		ctx.checkRead();
		return ctx.getDslContext()
				.select()
				.from(RITEM)
				.innerJoin(ITEM).on(ITEM.ID.eq(RITEM.ITEM))
				.innerJoin(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
				.where(RITEM_PROPERTIES.getConditions(filter))
				.limit(limit)
				.offset(offset)
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
	
	public static Integer[] getRSegmentStream(AONContext ctx, RegistrySegmentFilter filter){
 		return ctx.getDslContext().select().from(RSEGMENT)
			.where(RSEGMENT_PROPERTIES.getConditions(filter))
				.fetch().stream().map(r -> r.getValue(RSEGMENT.REGISTRY)).toArray(Integer[]::new);
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
	
	public static Stream<Seller> getRSellerStream(AONContext ctx, RegistrySellerFilter filter){
		return ctx.getDslContext().select().from(RSELLER)
				.join(SELLER).on(RSELLER.SELLER.eq(SELLER.REGISTRY))
				.join(SELLER_ALIAS).on(SELLER_ALIAS.ID.eq(SELLER.REGISTRY))
				.where(RSELLER_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new SellerFiller());
	}
	
	/**
	 * @deprecated  Replaced by RegistryAddressDAO.getStreamByRegistry
	 */
	@Deprecated(forRemoval = true )
	public static Stream<RAddress> getRAddressStream(AONContext ctx, Integer registryId){
		return ctx.getDslContext().select().from(RADDRESS)
					.join(REGISTRY).on(RADDRESS.REGISTRY.eq(REGISTRY.ID))
					.join(GEOZONE).on(RADDRESS.GEOZONE.eq(GEOZONE.ID))
				.where(RADDRESS.REGISTRY.eq(registryId))
				.fetch().stream().map(new RAddressFiller());
	}

	/**
	 * @deprecated  Replaced by RegistryAddressDAO.getStream
	 */
	@Deprecated(forRemoval = true )
	public static Stream<RAddress> getRAddressStream(AONContext ctx,
			RegistryAddressFilter filter) {
		return ctx.getDslContext().select().from(RADDRESS)
					.join(REGISTRY).on(RADDRESS.REGISTRY.eq(REGISTRY.ID))
					.leftOuterJoin(GEOZONE).on(RADDRESS.GEOZONE.eq(GEOZONE.ID))
				.where(RADDRESS_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new RAddressFiller());
	}
	
	/**
	 * @deprecated  Replaced by RegistryAddressDAO.insert
	 */
	@Deprecated(forRemoval = true )
	public static RAddress insertRAddress(AONContext ctx, RAddress raddress){
		return ctx.getDslContext().insertInto(RADDRESS, RADDRESS.ADDRESS, RADDRESS.ADDRESS2, RADDRESS.ADDRESS3, RADDRESS.ALIAS,
				RADDRESS.CITY, RADDRESS.DOMAIN, RADDRESS.GEOZONE, RADDRESS.MUNICIPALITY_CODE, RADDRESS.NUMBER, RADDRESS.RECIPIENT,
				RADDRESS.REGISTRY, RADDRESS.STREET_TYPE, RADDRESS.TYPE, RADDRESS.ZIP)
			.values(raddress.getAddress(), raddress.getAddress2(), raddress.getAddress3(), raddress.getAlias(),
					raddress.getCity(), raddress.getDomain(), raddress.getGeozone(), raddress.getMunicipality_code(), raddress.getNumber(), raddress.getRecipient(),
					raddress.getRegistry(), raddress.getStreet_type(), raddress.getType(), raddress.getZip()).returning()
			.fetch().stream().map(new RAddressFiller2()).findFirst().orElse(new RAddress());
	}
	
	/**
	 * @deprecated  Replaced by com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.insert(AONContext ctx, Registry reg)
	 */
	@Deprecated(forRemoval = true )
	public static Registry insertRegistry(AONContext ctx, Registry registry){
		String nationality = registry.getNationality() != null ? registry.getNationality().getIso2() : "ES";
		String documentCountry = registry.getDocumentCountry() != null ? registry.getDocumentCountry().getIso2() : "ES";
		return ctx.getDslContext().insertInto(REGISTRY, REGISTRY.DOMAIN, REGISTRY.ALIAS, REGISTRY.DOCUMENT, 
				REGISTRY.DOCUMENT_COUNTRY, REGISTRY.NAME, REGISTRY.NATIONALITY,	REGISTRY.TYPE)
			.values(registry.getDomain().getId(), registry.getAlias(), registry.getDocument(), 
				documentCountry, registry.getName(), nationality, 
				AonEnumUtils.getByte(registry.isLegalPerson())).returning()
			.fetch().stream().map(new RegistryFiller()).findFirst().orElse(new Registry());
	}
	
	/**
	 * @deprecated  Replaced by com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.update(AONContext ctx, Registry reg)
	 */
	@Deprecated(forRemoval = true )
	public static Registry updateRegistry(AONContext ctx, Registry registry){
		return ctx.getDslContext().update(REGISTRY)
			.set(REGISTRY.ALIAS, registry.getAlias()).set(REGISTRY.NAME, registry.getName())
			.where(REGISTRY.ID.eq(registry.getId()))
			.returning().fetch().stream().map(new RegistryFiller()).findFirst().orElse(new Registry());		
	}
	
	/**
	 * @deprecated  Replaced by com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.delete(AONContext ctx, Integer id)
	 */
	@Deprecated(forRemoval = true )
	public static Registry deleteRegistry(AONContext ctx, Integer registry){
		return ctx.getDslContext().delete(REGISTRY).where(REGISTRY.ID.eq(registry))
			.returning().fetch().stream().map(new RegistryFiller()).findFirst().orElse(new Registry());
	}
	
	/**
	 * @deprecated  Replaced by RegistryMediaDAO.insert
	 */
	@Deprecated(forRemoval = true )
	public static RegistryMedia insertRMedia(AONContext ctx, RegistryMedia rmedia){
		return ctx.getDslContext().insertInto(RMEDIA, RMEDIA.ADMINISTRATIVE, RMEDIA.COMMENT, RMEDIA.COMMERCIAL, RMEDIA.DOMAIN, RMEDIA.MEDIA,
				RMEDIA.RADDRESS, RMEDIA.REGISTRY, RMEDIA.TECHNICAL, RMEDIA.VALUE)
			.values(AonEnumUtils.getByte( rmedia.isAdministrative()), rmedia.getComment(), 
					AonEnumUtils.getByte( rmedia.isCommercial()), rmedia.getDomain(), rmedia.getMedia().value(),
					rmedia.getRaddress(), rmedia.getRegistry(), 
					AonEnumUtils.getByte( rmedia.isTechnical()), rmedia.getValue()).returning()
			.fetch().stream().map(new RMediaFiller()).findFirst().orElse(new RegistryMedia());
	}
	
	/**
	 * @deprecated  Replaced by RegistryMediaDAO.update
	 */
	@Deprecated(forRemoval = true )
	public static RegistryMedia updateRMedia(AONContext ctx, RegistryMedia rmedia){
		return ctx.getDslContext().update(RMEDIA)
			.set(RMEDIA.VALUE, rmedia.getValue())
			.where(RMEDIA.ID.eq(rmedia.getId()))
			.returning()
			.fetch().stream().map(new  RMediaFiller()).findFirst().orElse(new RegistryMedia());
	}
	
	/**
	 * @deprecated  Replaced by RegistryMediaDAO.delete
	 */
	@Deprecated(forRemoval = true )
	public static RegistryMedia deleteRMedia(AONContext ctx, Integer registry){
		return ctx.getDslContext().delete(RMEDIA)
				.where(RMEDIA.REGISTRY.eq(registry)).returning()
				.fetch().stream().map(new RMediaFiller()).findFirst().orElse(new RegistryMedia());
	}
	

	/**
	 * @deprecated  Replaced by RegistryAddressDAO.RegistryAddressFiller
	 */
	@Deprecated(forRemoval = true )
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

	/**
	 * @deprecated  Replaced by RegistryAddressDAO.RegistryAddressFiller
	 */
	@Deprecated(forRemoval = true )
	public static class RAddressFiller2  implements Function<Record, RAddress> {

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
					.setMunicipality_code(r.getValue(RADDRESS.MUNICIPALITY_CODE))
					.setNumber(r.getValue(RADDRESS.NUMBER))
					.setRecipient(r.getValue(RADDRESS.RECIPIENT))
					.setRegistry(r.getValue(RADDRESS.REGISTRY))
					.setStreet_type(r.getValue(RADDRESS.STREET_TYPE))
					.setType(r.getValue(RADDRESS.TYPE))
					.setZip(r.getValue(RADDRESS.ZIP));
		}
	}

	// ------------------- CUSTOMER
	
	/**
	 * @deprecated  Replaced by com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.insert(AONContext ctx, Customer customer)
	 */
	@Deprecated(forRemoval = true )
	public static Customer insertCustomer(AONContext ctx, Customer customer){
		ctx.getDslContext().insertInto(CUSTOMER, CUSTOMER.ACCOUNT, CUSTOMER.DELIVERY_GROUPED, CUSTOMER.DELIVERY_VALUATED,
				CUSTOMER.DOMAIN, CUSTOMER.E_INVOICE, CUSTOMER.INVOICING_GROUP, CUSTOMER.PROJECT_GROUPED, CUSTOMER.REGISTRY,
				CUSTOMER.SCOPE, CUSTOMER.STATUS, CUSTOMER.SURCHARGE, CUSTOMER.TARIFF, CUSTOMER.TRANSACTION, CUSTOMER.WITHHOLDING,
				CUSTOMER.CREATION_USER, CUSTOMER.CREATION_DATE, CUSTOMER.MODIFICATION_USER, CUSTOMER.MODIFICATION_DATE)
			.values(customer.getAccount()
					,AonEnumUtils.getByte(customer.isDeliveryGrouped())
					,AonEnumUtils.getByte(customer.isDeliveryValuated())
					,customer.getDomain().getId()
					,AonEnumUtils.getByte(customer.isEInvoice())
					,customer.getInvoicingGroup()
					,AonEnumUtils.getByte(customer.isProjectGrouped())
					,customer.getId()
					,customer.getScope().getId(), customer.getStatus().value()
					,AonEnumUtils.getByte(customer.isSurcharge())
					,customer.getTariff()
					,AonEnumUtils.getByte(customer.getTransaction())
					,AonEnumUtils.getByte(customer.isWithholding())
					,ctx.getUser()
					,new Timestamp(new Date().getTime())
					,ctx.getUser()
					,new Timestamp(new Date().getTime()))
			.execute();
		return customer;
	}
	
	// ------------------- SELLER
	
	@Deprecated
	public static Stream<Seller> getSellers(AONContext ctx){
		return ctx.getDslContext().select(SELLER.REGISTRY, SELLER.DOMAIN, SELLER.COMMISSION_TYPE, SELLER.SCOPE, SELLER.STATUS,
				SCOPE.DESCRIPTION, REGISTRY.DOCUMENT, REGISTRY.NAME, REGISTRY.ALIAS, REGISTRY.DOCUMENT_COUNTRY, REGISTRY.DOCUMENT_TYPE,
				REGISTRY.NATIONALITY, REGISTRY.SECURITY_LEVEL, SCOPE.ID)
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
	
	@Deprecated
	private static class FullSellerFiller implements Function<Record, Seller> {
		@Override
		public Seller apply(Record r) {
			return new Seller()
					.setDomain(r.getValue(SELLER.DOMAIN))
					.setId(r.getValue(SELLER.REGISTRY))
					.setActive(r.getValue(SELLER.STATUS) == 1)
					.setCommissionType(new CommissionType().setId(r.getValue(SELLER.COMMISSION_TYPE)))
					.setScope(ScopeFiller.buildScope(r));			
		}
	}
	
	// ------------------- CREDITOR

	/**
	 * @deprecated  Replaced by com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO.getStream(AONContext ctx, CreditorFilter filter)
	 */
	@Deprecated(forRemoval = true )
	public static Stream<Creditor> getCreditorStream(AONContext ctx, CreditorFilter filter){
		return CREDITOR_PROPERTIES.build(ctx.getDslContext().select()
					.from(CREDITOR).join(SCOPE).on(CREDITOR.SCOPE.eq(SCOPE.ID))
					.join(REGISTRY).on(REGISTRY.ID.eq(CREDITOR.REGISTRY))
			,filter).fetch().stream().map(new CreditorFiller());
	}
	
	/**
	 * @deprecated  Replaced by com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO.insert(AONContext ctx, Creditor creditor)
	 */
	@Deprecated(forRemoval = true )
	public static Creditor insertCreditor(AONContext ctx, Creditor creditor){
		ctx.getDslContext().insertInto(CREDITOR, CREDITOR.ACCOUNT, CREDITOR.DOMAIN, CREDITOR.REGISTRY,
				CREDITOR.SCOPE, CREDITOR.STATUS, CREDITOR.TRANSACTION, CREDITOR.WITHHOLDING,
				CREDITOR.CREATION_USER, CREDITOR.CREATION_DATE, CREDITOR.MODIFICATION_USER, CREDITOR.MODIFICATION_DATE)
			.values(creditor.getAccount(), creditor.getDomain().getId(), creditor.getId(),
					creditor.getScope().getId(), creditor.getStatus().value(), creditor.getTransaction().value(),
					AonEnumUtils.getByte(creditor.isWithholding()),ctx.getUser(), new Timestamp(new Date().getTime()),
					ctx.getUser(), new Timestamp(new Date().getTime()))
			.execute();
		return creditor;
	}
	
	// ------------------- SUPPLIER

	/**
	 * @deprecated  Replaced by com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO.getStream(AONContext ctx, SupplierFilter filter)
	 */
	@Deprecated(forRemoval = true )
	public static Stream<Supplier> getSupplierStream(AONContext ctx, SupplierFilter filter){
		return SUPPLIER_PROPERTIES.build(ctx.getDslContext().select()
					.from(SUPPLIER).join(SCOPE).on(SUPPLIER.SCOPE.eq(SCOPE.ID))
					.join(REGISTRY).on(REGISTRY.ID.eq(SUPPLIER.REGISTRY))
			,filter).fetch().stream().map(new SupplierFiller());
	}
	
	/**
	 * @deprecated  Replaced by com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO.save(AONContext ctx, Supplier supplier)
	 */
	@Deprecated(forRemoval = true )
	public static Supplier insertSupplier(AONContext ctx, Supplier supplier){
		ctx.getDslContext().insertInto(SUPPLIER, SUPPLIER.REGISTRY, SUPPLIER.DOMAIN, SUPPLIER.TARIFF, SUPPLIER.WITHHOLDING,
				SUPPLIER.WITHHOLDING_FARMER, SUPPLIER.VAT_ACCRUAL_PAYMENT, SUPPLIER.TRANSACTION, SUPPLIER.STATUS, 
				SUPPLIER.SCOPE, SUPPLIER.PURCHASE_VALUATED, SUPPLIER.ACCOUNT, SUPPLIER.CREATION_USER, SUPPLIER.CREATION_DATE,
				SUPPLIER.MODIFICATION_USER, SUPPLIER.MODIFICATION_DATE)
			.values(supplier.getId(), supplier.getDomain().getId(), supplier.getTariff(), 
					AonEnumUtils.getByte(supplier.isWithholding()),
					AonEnumUtils.getByte(supplier.isWithholdingFarmer()),
					AonEnumUtils.getByte(supplier.isVatAccrualPayment()),
					AonEnumUtils.getByte(supplier.getTransaction()), 
					supplier.getStatus().value(), supplier.getScope().getId(), 
					AonEnumUtils.getByte(supplier.isPurchaseValuated()), 
					supplier.getAccount(), ctx.getUser(), new Timestamp(new Date().getTime()), ctx.getUser(),
					new Timestamp(new Date().getTime())).execute();
		return supplier;
	}
	
	// ------------------- PERSON

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
	
	public static RecordData saveRecordData(AONContext ctx, RecordData recordData) {
		return recordData.getId() != null 
			? updateRecordData(ctx, recordData)
			: insertRecordData(ctx, recordData); 
	}
	
	private static RecordData insertRecordData(AONContext ctx, RecordData recordData){
		Integer id = ctx.getDslContext().insertInto(RECORD_DATA)
			.set(RECORD_DATA.DOMAIN, recordData.getDomain())
			.set(RECORD_DATA.REGISTRY, recordData.getRegistry())
			.set(RECORD_DATA.DESCRIPTION, recordData.getDescription())
			.set(RECORD_DATA.CREATION_DATE, AonDateUtils.toSql(recordData.getCreationDate()))
			.set(RECORD_DATA.NOTARY, recordData.getNotary())
			.set(RECORD_DATA.NUMBER, recordData.getNumber())
			.set(RECORD_DATA.RECORD_DATE, AonDateUtils.toSql(recordData.getRecordDate()))
			.set(RECORD_DATA.VOLUME, recordData.getVolume())
			.set(RECORD_DATA.SECTION, recordData.getSection())
			.set(RECORD_DATA.PAGE, recordData.getPage())
			.set(RECORD_DATA.SHEET, recordData.getSheet())
			.set(RECORD_DATA.REGISTRATION, recordData.getRegistration())
			.set(RECORD_DATA.ATTACH, recordData.getAttach())
			.returning(RECORD_DATA.ID)
			.fetchOne()
			.getValue(RECORD_DATA.ID);
		recordData.setId(id);
		ctx.log().debug("INSERT REGISTRY RECORD DATA ( registry: {0}) id: {1}",recordData.getRegistry(),recordData.getId());
		return recordData;
	}
	private static RecordData updateRecordData(AONContext ctx, RecordData recordData){
		int count = ctx.getDslContext().update(RECORD_DATA)
			.set(RECORD_DATA.DOMAIN, recordData.getDomain())
			.set(RECORD_DATA.REGISTRY, recordData.getRegistry())
			.set(RECORD_DATA.DESCRIPTION, recordData.getDescription())
			.set(RECORD_DATA.CREATION_DATE, AonDateUtils.toSql(recordData.getCreationDate()))
			.set(RECORD_DATA.NOTARY, recordData.getNotary())
			.set(RECORD_DATA.NUMBER, recordData.getNumber())
			.set(RECORD_DATA.RECORD_DATE, AonDateUtils.toSql(recordData.getRecordDate()))
			.set(RECORD_DATA.VOLUME, recordData.getVolume())
			.set(RECORD_DATA.SECTION, recordData.getSection())
			.set(RECORD_DATA.PAGE, recordData.getPage())
			.set(RECORD_DATA.SHEET, recordData.getSheet())
			.set(RECORD_DATA.REGISTRATION, recordData.getRegistration())
			.set(RECORD_DATA.ATTACH, recordData.getAttach())
			.where(RECORD_DATA.ID.eq(recordData.getId()))
			.execute();
		ctx.log().debug("UPDATE REGISTRY RECORD DATA ( registry: {0}) id: {1}. ({2} rows)", recordData.getRegistry(), recordData.getId(), count);
		return recordData;
	}
	
	// ------------------- RBANK
	private static SelectConditionStep<Record> getRegistryBankSelect(AONContext ctx, RegistryBankFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext().select()
				.from(RBANK)
				.leftOuterJoin(ACCOUNT).on(ACCOUNT.ID.eq(RBANK.ACCOUNT))				
				.where(RBANK_PROPERTIES.getConditions(filter));
	}
	public static RegistryBank getRegistryBank(AONContext ctx, Integer id){
		return getRegistryBankSelect(ctx,filter -> filter.getIdProperty().eq(id))
				.fetch()
				.stream()
				.map(new RegistryBankFiller())
				.findFirst()
				.orElse(new RegistryBank());
	}
	public static Stream<RegistryBank> getRBankStream(AONContext ctx, RegistryBankFilter filter){
		return getRegistryBankSelect(ctx,filter)
				.fetch()
				.stream()
				.map(new RegistryBankFiller());
	}
	
	public static RegistryBank insertRBank(AONContext ctx, RegistryBank rbank){
		ctx.checkWrite();	
		Integer id = ctx.getDslContext()
			.insertInto(RBANK)
			.set(RBANK.ACCOUNT, rbank.getAccount().getId())
			.set(RBANK.ACTIVE, rbank.isActive() ? (byte) 1 : (byte) 0) 
			.set(RBANK.ALIAS, rbank.getAlias())
			.set(RBANK.BANK_ACCOUNT, rbank.getBankAccount()==null?null:rbank.getBankAccount().getIban()) 
			.set(RBANK.BIC, rbank.getBic())
			.set(RBANK.DOMAIN, rbank.getDomain())
			.set(RBANK.REGISTRY, rbank.getRegistry()) 
			.set(RBANK.SUFIX, rbank.getSuffix()) 
			.returning( RBANK.ID )
			.fetchOne()
			.getValue(REGISTRY.ID);
		ctx.log().info("INSERT RBANK id: " + id);
		return getRegistryBank(ctx, id);
	}
	
	public static RegistryBank updateRBank(AONContext ctx, RegistryBank rbank){
		// TODO
		return new RegistryBank();
	}
	
	public static void deleteRBank(AONContext ctx, Integer id){
		ctx.checkWrite();
		int i = ctx.getDslContext().delete(RBANK)
			.where(RBANK.ID.equal(id))
			.execute();
		ctx.log().info("DELETE RBANK ("+i+") id: " + id);
	}
	
	// ------------------- REGISTRY ADD INFO
	
	public static Stream<RegistryAddInfo> getRegistryAddInfoStream(AONContext ctx, RegistryAddInfoFilter filter){
		return ctx.getDslContext().select()
				.from(RADDINFO)
				.where(RADDINFO_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new RegistryAddInfoFiller());
	}
	
	public static RegistryAddInfo insertRegistryAddInfo(AONContext ctx, RegistryAddInfo raddinfo){
		return ctx.getDslContext().insertInto(RADDINFO,RADDINFO.DOMAIN, RADDINFO.REGISTRY, RADDINFO.ATTRIBUTE, RADDINFO.VALUE, RADDINFO.VALUE_DATE)
			.values(raddinfo.getDomain(), raddinfo.getRegistry(), raddinfo.getAttribute(), raddinfo.getValue(), new java.sql.Date(raddinfo.getDate().getTime()))
			.returning()
			.fetch().stream().map(new RegistryAddInfoFiller()).findFirst().orElse(new RegistryAddInfo());
	}
	
	public static RegistryAddInfo updateRegistryAddInfo(AONContext ctx, RegistryAddInfo raddinfo){
		return ctx.getDslContext().update(RADDINFO)
				.set(RADDINFO.VALUE, raddinfo.getValue())
				.set(RADDINFO.VALUE_DATE, new java.sql.Date(raddinfo.getDate().getTime()))
				.where(RADDINFO.ID.eq(raddinfo.getId()))
			.returning()
			.fetch().stream().map(new RegistryAddInfoFiller()).findFirst().orElse(new RegistryAddInfo());
	}
	
	public static void deleteRegistryAddInfo(AONContext ctx, Integer raddinfoId){
		int i = ctx.getDslContext().delete(RADDINFO).where(RADDINFO.ID.eq(raddinfoId)).execute();
		ctx.log().info("DELETE RBANK ("+i+") id: " + raddinfoId);
	}

	public static void deleteRegistryAddInfo(AONContext ctx, RegistryAddInfoFilter filter){
		ctx.getDslContext().delete(RADDINFO).where(RADDINFO_PROPERTIES.getConditions(filter)).execute();
	}
	
	// ------------------- RDIRSTAFF
	
//	public static Stream<RDirStaff> getRDirStaffStream(AONContext ctx, RDirStaffFilter filter){
//		return ctx.getDslContext().select()
//				.from(RDIR_STAFF)
//				.where(RDIRSTAFF_PROPERTIES.getConditions(filter))
//				.fetch().stream().map(new RDirStaffFiller());
//	}
//	
//	public static RDirStaff insertRDirStaff(AONContext ctx, RDirStaff rdirstaff){
//		return ctx.getDslContext().insertInto(RDIR_STAFF,RDIR_STAFF.DOMAIN, RDIR_STAFF.REGISTRY, RDIR_STAFF.NAME, RDIR_STAFF.DOCUMENT)
//			.values(rdirstaff.getDomain(), rdirstaff.getRegistry(), rdirstaff.getName(), rdirstaff.getDocument())
//			.returning()
//			.fetch().stream().map(new RDirStaffFiller()).findFirst().orElse(new RDirStaff());
//	}
	
	
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
	
	public static class RegistryAddInfoFiller  implements Function<Record, RegistryAddInfo> {

		@Override
		public RegistryAddInfo apply(Record r) {
			return new RegistryAddInfo()
					.setId(r.getValue(RADDINFO.ID))
					.setRegistry(r.getValue(RADDINFO.REGISTRY))
					.setDomain(r.getValue(RADDINFO.DOMAIN))
					.setAttribute(r.getValue(RADDINFO.ATTRIBUTE))
					.setValue(r.getValue(RADDINFO.VALUE))
					.setDate(r.getValue(RADDINFO.VALUE_DATE));
		}
	}

}
