package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Carrier.CARRIER;
import static com.esferalia.aon.jooq.tables.Category.CATEGORY;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Question.QUESTION;
import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;
import static com.esferalia.aon.jooq.tables.RecordData.RECORD_DATA;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Ritem.RITEM;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Rnote.RNOTE;
import static com.esferalia.aon.jooq.tables.Rpaymethod.RPAYMETHOD;
import static com.esferalia.aon.jooq.tables.Rprofile.RPROFILE;
import static com.esferalia.aon.jooq.tables.Rsegment.RSEGMENT;
import static com.esferalia.aon.jooq.tables.Rseller.RSELLER;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Segment.SEGMENT;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;
import static com.esferalia.aon.jooq.tables.Target.TARGET;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.jooq.tables.records.CategoryRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.RmediaRecord;
import com.esferalia.aon.jooq.tables.records.SegmentRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.CarrierFilter;
import com.esferalia.aon.occam.api.model.Filter.CategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.CustomerFilter;
import com.esferalia.aon.occam.api.model.Filter.PersonFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RDirStaffFilter;
import com.esferalia.aon.occam.api.model.Filter.RecordDataFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddInfoFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryBankFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryItemFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryNoteFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryPayMethodFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistrySellerFilter;
import com.esferalia.aon.occam.api.model.Filter.SellerFilter;
import com.esferalia.aon.occam.api.model.Filter.SupplierFilter;
import com.esferalia.aon.occam.api.model.Filter.TargetFilter;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.Properties.RegistryAddressProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryMediaProperties;
import com.esferalia.aon.occam.api.model.commission.CommissionType;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFilter;
import com.esferalia.aon.occam.api.model.registry.Question;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.RDirStaff;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;
import com.esferalia.aon.occam.api.model.registry.RegistryProfile;
import com.esferalia.aon.occam.api.model.registry.Segment;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.CarrierFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.CreditorFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.CustomerFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.PersonFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.RItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.RNoteFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.RecordDataFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.SupplierFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.TargetFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CarrierPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CategoryPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CreditorPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CustomerPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.PersonPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RBankPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RDirStaffPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RItemPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RNotePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RPayMethodPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RecordDataPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RegistryAddInfoPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RegistryPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RegistrySellerPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.SellerPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.SupplierPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.TargetPropertiesDAO;

public class RegistryDAO {
	
	private static final CustomerPropertiesDAO CUSTOMER_PROPERTIES = new CustomerPropertiesDAO();
	private static final SellerPropertiesDAO SELLER_PROPERTIES = new SellerPropertiesDAO();
	private static final RegistrySellerPropertiesDAO RSELLER_PROPERTIES = new RegistrySellerPropertiesDAO();
	private static final CarrierPropertiesDAO CARRIER_PROPERTIES = new CarrierPropertiesDAO();
	private static final RecordDataPropertiesDAO RECORD_DATA_PROPERTIES = new RecordDataPropertiesDAO();
	private static final RBankPropertiesDAO RBANK_PROPERTIES = new RBankPropertiesDAO();
	private static final RPayMethodPropertiesDAO RPAYMETHOD_PROPERTIES = new RPayMethodPropertiesDAO();
	private static final RegistryAddInfoPropertiesDAO RADDINFO_PROPERTIES = new RegistryAddInfoPropertiesDAO();
	private static final RDirStaffPropertiesDAO RDIRSTAFF_PROPERTIES = new RDirStaffPropertiesDAO();
	private static final SupplierPropertiesDAO SUPPLIER_PROPERTIES = new SupplierPropertiesDAO();

	private static final TargetPropertiesDAO TARGET_PROPERTIES = new TargetPropertiesDAO();
	private static final RegistryPropertiesDAO REGISTRY_PROPERTIES = new RegistryPropertiesDAO();
	private static final CategoryPropertiesDAO CATEGORY_PROPERTIES = new CategoryPropertiesDAO();
	private static final PersonPropertiesDAO PERSON_PROPERTIES = new PersonPropertiesDAO();
	
	private static final CreditorPropertiesDAO CREDITOR_PROPERTIES = new CreditorPropertiesDAO();

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
	
	
	public static Category insertCategory(AONContext ctx, Category category){
		CategoryRecord cr = ctx.getDslContext()
				.insertInto(CATEGORY)
				.set(CATEGORY.DOMAIN, category.getDomain())
				.set(CATEGORY.NAME, category.getName())
				.set(CATEGORY.TYPE, category.getType())
				.returning().fetchOne();
		return new FullCategoryFiller().apply(cr);
	}
	
	public static Category updateCategory(AONContext ctx, Category category){
		ctx.getDslContext().update(CATEGORY)
			.set(CATEGORY.NAME, category.getName())
			.where(CATEGORY.ID.eq(category.getId()))
			.execute();
		return category;
	}
	
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
	
	
	public static Stream<Registry> getAonRegistryStream(AONContext ctx, RegistryFilter filter){
		return ctx.getDslContext().select().from(REGISTRY)
				.leftOuterJoin(RADDRESS).on(RADDRESS.REGISTRY.eq(REGISTRY.ID))
				.leftOuterJoin(GEOZONE).on(GEOZONE.ID.eq(RADDRESS.GEOZONE))
				.where(REGISTRY_PROPERTIES.getConditions(filter)).fetch()
			.stream().map(new FullRegistryFiller());
	}
	
	public static Stream<Registry> getRegistryStream(AONContext ctx, RegistryFilter filter){
		return ctx.getDslContext().select().from(REGISTRY)
				.where(REGISTRY_PROPERTIES.getConditions(filter)).fetch()
			.stream().map(new RegistryFiller());
	}
	
	public static Registry getRegistry(AONContext ctx, RegistryFilter filter){
		return getRegistryStream(ctx, filter).findFirst().orElse(new Registry());
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
					.setDomain(new Domain().setId(record.getValue(REGISTRY.DOMAIN)))
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
					.setType(record.getValue(REGISTRY.TYPE))
					.setAddress(address)
				;
		}
		
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
	
	protected static Integer insert(AONContext ctx, Registry reg) {
		ctx.checkWrite();
		return ctx.getDslContext().insertInto(REGISTRY)
			.set(REGISTRY.DOMAIN, reg.getDomain().getId())
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
	
	public static Stream<Seller> getRSellerStream(AONContext ctx, RegistrySellerFilter filter){
		return ctx.getDslContext().select().from(RSELLER)
				.join(REGISTRY).on(RSELLER.SELLER.eq(REGISTRY.ID))
				.where(RSELLER_PROPERTIES.getConditions(filter))
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
					.leftOuterJoin(GEOZONE).on(RADDRESS.GEOZONE.eq(GEOZONE.ID))
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
			.fetch().stream().map(new RAddressFiller2()).findFirst().orElse(new RAddress());
	}
	
	public static Registry insertRegistry(AONContext ctx, Registry registry){
		String nationality = registry.getNationality() != null ? registry.getNationality().getIso2() : "ES";
		String documentCountry = registry.getDocumentCountry() != null ? registry.getDocumentCountry().getIso2() : "ES";
		return ctx.getDslContext().insertInto(REGISTRY, REGISTRY.DOMAIN, REGISTRY.ALIAS, REGISTRY.DOCUMENT, 
				REGISTRY.DOCUMENT_COUNTRY, REGISTRY.NAME, REGISTRY.NATIONALITY,	REGISTRY.TYPE)
			.values(registry.getDomain().getId(), registry.getAlias(), registry.getDocument(), 
				documentCountry, registry.getName(), nationality, registry.getType()).returning()
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
	
	public static Stream<Customer> getCustomerStream(AONContext ctx, CustomerFilter filter){
		return ctx.getDslContext().select().from(CUSTOMER)
				.join(REGISTRY).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
				.where(CUSTOMER_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new CustomerFiller());
	}
	
	public static Customer insertCustomer(AONContext ctx, Customer customer){
		ctx.getDslContext().insertInto(CUSTOMER, CUSTOMER.ACCOUNT, CUSTOMER.DELIVERY_GROUPED, CUSTOMER.DELIVERY_VALUATED,
				CUSTOMER.DOMAIN, CUSTOMER.E_INVOICE, CUSTOMER.INVOICING_GROUP, CUSTOMER.PROJECT_GROUPED, CUSTOMER.REGISTRY,
				CUSTOMER.SCOPE, CUSTOMER.STATUS, CUSTOMER.SURCHARGE, CUSTOMER.TARIFF, CUSTOMER.TRANSACTION, CUSTOMER.WITHHOLDING,
				CUSTOMER.CREATION_USER, CUSTOMER.CREATION_DATE, CUSTOMER.MODIFICATION_USER, CUSTOMER.MODIFICATION_DATE)
			.values(customer.getAccount(), customer.getDeliveryGrouped(), customer.getDeliveryValuated(), 
					customer.getDomain().getId(), customer.geteInvoice(), customer.getInvoicingGroup(), customer.getProjectGrouped(), customer.getRegistry().getId(),
					customer.getScope(), customer.getStatus().value(), customer.getSurcharge(), customer.getTariff(), customer.getTransaction(), customer.getWithholding(),
					ctx.getUser(), new Timestamp(new Date().getTime()), ctx.getUser(), new Timestamp(new Date().getTime()))
			.execute();
		return customer;
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
	
	public static Carrier insertCarrier(AONContext ctx, Carrier carrier){
		Registry registry = insertRegistry(ctx, carrier);
		ctx.getDslContext().insertInto(CARRIER, CARRIER.DOMAIN, CARRIER.REGISTRY, CARRIER.SCOPE, CARRIER.STATUS)
			.values(carrier.getDomain().getId(), registry.getId(), carrier.getScope(), carrier.getStatus().value())
			.execute();
		carrier.setId(registry.getId());
		return carrier;
	}
	
	// ------------------- CREDITOR

	public static Stream<Creditor> getCreditorStream(AONContext ctx, CreditorFilter filter){
		return CREDITOR_PROPERTIES.build(ctx.getDslContext().select()
					.from(CREDITOR).join(SCOPE).on(CREDITOR.SCOPE.eq(SCOPE.ID))
					.join(REGISTRY).on(REGISTRY.ID.eq(CREDITOR.REGISTRY))
			,filter).fetch().stream().map(new CreditorFiller());
	}
	
	public static Creditor insertCreditor(AONContext ctx, Creditor creditor){
		ctx.getDslContext().insertInto(CREDITOR, CREDITOR.ACCOUNT, CREDITOR.DOMAIN, CREDITOR.REGISTRY,
				CREDITOR.SCOPE, CREDITOR.STATUS, CREDITOR.TRANSACTION, CREDITOR.WITHHOLDING,
				CREDITOR.CREATION_USER, CREDITOR.CREATION_DATE, CREDITOR.MODIFICATION_USER, CREDITOR.MODIFICATION_DATE)
			.values(creditor.getAccount().getId(), creditor.getDomain(), creditor.getRegistry().getId(),
					creditor.getScope(), creditor.getStatus().value(), creditor.getTransaction().value(), 
					creditor.isWithholding() ? (byte) 1 : (byte) 0,	ctx.getUser(), new Timestamp(new Date().getTime()),
					ctx.getUser(), new Timestamp(new Date().getTime()))
			.execute();
		return creditor;
	}
	
	// ------------------- SUPPLIER

	public static Stream<Supplier> getSupplierStream(AONContext ctx, SupplierFilter filter){
		return SUPPLIER_PROPERTIES.build(ctx.getDslContext().select()
					.from(SUPPLIER).join(SCOPE).on(SUPPLIER.SCOPE.eq(SCOPE.ID))
					.join(REGISTRY).on(REGISTRY.ID.eq(SUPPLIER.REGISTRY))
			,filter).fetch().stream().map(new SupplierFiller());
	}
	
	
	public static Supplier insertSupplier(AONContext ctx, Supplier supplier){
		ctx.getDslContext().insertInto(SUPPLIER, SUPPLIER.REGISTRY, SUPPLIER.DOMAIN, SUPPLIER.TARIFF, SUPPLIER.WITHHOLDING,
				SUPPLIER.WITHHOLDING_FARMER, SUPPLIER.VAT_ACCRUAL_PAYMENT, SUPPLIER.TRANSACTION, SUPPLIER.STATUS, 
				SUPPLIER.SCOPE, SUPPLIER.PURCHASE_VALUATED, SUPPLIER.ACCOUNT, SUPPLIER.CREATION_USER, SUPPLIER.CREATION_DATE,
				SUPPLIER.MODIFICATION_USER, SUPPLIER.MODIFICATION_DATE)
			.values(supplier.getId(), supplier.getDomain().getId(), supplier.getTariff(), supplier.getWithholding().byteValue(), supplier.getWithholdingFarmer().byteValue(),
					supplier.getVatAccrualPayment().byteValue(),supplier.getTransaction().byteValue(), supplier.getStatus().value(), supplier.getScope(), 
					supplier.getPurchaseValuated().byteValue(), supplier.getAccount(), ctx.getUser(), new Timestamp(new Date().getTime()), ctx.getUser(),
					new Timestamp(new Date().getTime())).execute();
		return supplier;
	}
	
	// ------------------- TARGET

	public static Stream<Target> getTargetStream(AONContext ctx, TargetFilter filter){
		return TARGET_PROPERTIES.build(ctx.getDslContext().select()
					.from(TARGET).join(SCOPE).on(TARGET.SCOPE.eq(SCOPE.ID))
					.join(REGISTRY).on(REGISTRY.ID.eq(TARGET.REGISTRY))
			,filter).fetch().stream().map(new TargetFiller());
	}
	
	public static Target insertTarget(AONContext ctx, Target target){
		ctx.getDslContext().insertInto(TARGET, TARGET.ADVERTISING, TARGET.DOMAIN, TARGET.REGISTRY, TARGET.SCOPE,
				TARGET.STATUS, TARGET.SURCHARGE, TARGET.TARIFF, TARGET.TRANSACTION, TARGET.WITHHOLDING,
				TARGET.CREATION_DATE, TARGET.CREATION_USER, TARGET.MODIFICATION_DATE, TARGET.MODIFICATION_USER)
			.values(target.getAdvertising().byteValue(), target.getDomain().getId(), target.getId(), target.getScope(), target.getStatus().value(),
					target.getSurcharge().byteValue(), target.getTariff(),target.getTransaction().byteValue(), target.getWithholding().byteValue(),
					new Timestamp(new Date().getTime()), ctx.getUser(), new Timestamp(new Date().getTime()), ctx.getUser()).execute();
		return target;
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
			.set(RBANK.ACCOUNT, rbank.getAccount())
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
	
	// ------------------- RPAYMETHOD
	
	public static Stream<RegistryPayMethod> getRPayMethodStream(AONContext ctx, RegistryPayMethodFilter filter){
		return ctx.getDslContext().select()
				.from(RPAYMETHOD)
				.where(RPAYMETHOD_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new RegistryPayMethodFiller());
	}
	
	public static RegistryPayMethod insertRPayMethod(AONContext ctx, RegistryPayMethod rpaymethod){
		return ctx.getDslContext().insertInto(RPAYMETHOD,RPAYMETHOD.DAYS_BETWEEN_PYMNTS, RPAYMETHOD.DAYS_TO_FIRST_PYMNT, 
				RPAYMETHOD.DOMAIN, RPAYMETHOD.NUMBER_OF_PYMNTS, RPAYMETHOD.PAY_METHOD, RPAYMETHOD.PYMNT_DAYS,
				RPAYMETHOD.RBANK, RPAYMETHOD.REGISTRY)
			.values(rpaymethod.getDaysBetwenPymnts(), rpaymethod.getDaysToFirstPymnt(),
					rpaymethod.getDomain(), rpaymethod.getNumberOfPymnts(), rpaymethod.getPayMethod(), rpaymethod.getPymnt_days(),
					rpaymethod.getRbank(), rpaymethod.getRegistry()).returning()
			.fetch().stream().map(new RegistryPayMethodFiller()).findFirst().orElse(new RegistryPayMethod());
	}
	
	public static RegistryPayMethod updateRPayMethod(AONContext ctx, RegistryPayMethod rpaymethod){
		// TODO
		return new RegistryPayMethod();
	}
	
	public static RegistryPayMethod deleteRPayMethod(AONContext ctx, RegistryPayMethodFilter filter){
		return ctx.getDslContext().delete(RPAYMETHOD)
				.where(RPAYMETHOD_PROPERTIES.getConditions(filter)).returning()
				.fetch().stream().map(new RegistryPayMethodFiller()).findFirst().orElse(new RegistryPayMethod());
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
	
	// ------------------- RDIRSTAFF
	
	public static Stream<RDirStaff> getRDirStaffStream(AONContext ctx, RDirStaffFilter filter){
		return ctx.getDslContext().select()
				.from(RDIR_STAFF)
				.where(RDIRSTAFF_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new RDirStaffFiller());
	}
	
	public static RDirStaff insertRDirStaff(AONContext ctx, RDirStaff rdirstaff){
		return ctx.getDslContext().insertInto(RDIR_STAFF,RDIR_STAFF.DOMAIN, RDIR_STAFF.REGISTRY, RDIR_STAFF.NAME, RDIR_STAFF.DOCUMENT)
			.values(rdirstaff.getDomain(), rdirstaff.getRegistry(), rdirstaff.getName(), rdirstaff.getDocument())
			.returning()
			.fetch().stream().map(new RDirStaffFiller()).findFirst().orElse(new RDirStaff());
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
	
	public static class RegistryBankFiller  implements Function<Record, RegistryBank> {

		@Override
		public RegistryBank apply(Record r) {
			return new RegistryBank()
					.setId(r.getValue(RBANK.ID))
					.setRegistry(r.getValue(RBANK.REGISTRY))
					.setDomain(r.getValue(RBANK.DOMAIN))
					.setAccount(r.getValue(RBANK.ACCOUNT))
					.setAccountCode(r.getValue(ACCOUNT.CODE))
					.setAccountDescription(r.getValue(ACCOUNT.DESCRIPTION))
					.setActive(r.getValue(RBANK.ACTIVE) == 1)
					.setAlias(r.getValue(RBANK.ALIAS))
					.setBankAccount(new BankAccount(r.getValue(RBANK.BANK_ACCOUNT)))
					.setBic(r.getValue(RBANK.BIC))
					.setSuffix(r.getValue(RBANK.SUFIX));
		}
	}
	
	public static class RegistryPayMethodFiller  implements Function<Record, RegistryPayMethod> {

		@Override
		public RegistryPayMethod apply(Record r) {
			return new RegistryPayMethod()
					.setId(r.getValue(RPAYMETHOD.ID))
					.setRegistry(r.getValue(RPAYMETHOD.REGISTRY))
					.setDomain(r.getValue(RPAYMETHOD.DOMAIN))
					.setDaysBetwenPymnts(r.getValue(RPAYMETHOD.DAYS_BETWEEN_PYMNTS))
					.setDaysToFirstPymnt(r.getValue(RPAYMETHOD.DAYS_TO_FIRST_PYMNT))
					.setNumberOfPymnts(r.getValue(RPAYMETHOD.NUMBER_OF_PYMNTS))
					.setPayMethod(r.getValue(RPAYMETHOD.PAY_METHOD))
					.setPymnt_days(r.getValue(RPAYMETHOD.PYMNT_DAYS))
					.setRbank(r.getValue(RPAYMETHOD.RBANK))
					;
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
	
	public static class RDirStaffFiller  implements Function<Record, RDirStaff> {

		@Override
		public RDirStaff apply(Record r) {
			return new RDirStaff()
					.setId(r.getValue(RDIR_STAFF.ID))
					.setRegistry(r.getValue(RDIR_STAFF.REGISTRY))
					.setDomain(r.getValue(RDIR_STAFF.DOMAIN))
					.setChargeDescription(r.getValue(RDIR_STAFF.CHARGE_DESCRIPTION))
					.setDirector(r.getValue(RDIR_STAFF.DIRECTOR) == 1)
					.setDocument(r.getValue(RDIR_STAFF.DOCUMENT))
					.setName(r.getValue(RDIR_STAFF.NAME))
					.setDueDate(r.getValue(RDIR_STAFF.DUE_DATE))
					.setNominalValue(r.getValue(RDIR_STAFF.NOMINAL_VALUE))
					.setPercentShare(r.getValue(RDIR_STAFF.PERCENT_SHARE))
					.setRepresentative(r.getValue(RDIR_STAFF.REPRESENTATIVE) == 1)
					.setRepresentativeLabor(r.getValue(RDIR_STAFF.REPRESENTATIVE_LABOR) == 1)
					.setShareHolder(r.getValue(RDIR_STAFF.SHAREHOLDER) == 1);
		}
	}

}
