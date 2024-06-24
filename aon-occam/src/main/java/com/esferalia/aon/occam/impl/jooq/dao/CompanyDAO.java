package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.InvestAsset.INVEST_ASSET;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserAppRole.USER_APP_ROLE;

import java.util.Date;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectSeekStep1;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.Rmedia;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonCompany;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Filter.CompanyFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Iae;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.Properties.CompanyProperties;
import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;
import com.esferalia.aon.occam.api.model.finance.VATExemptionCause;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.AonCompanyFiller;
import com.esferalia.aon.occam.impl.jooq.dao.InvestAssetDAO.InvestAssetFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryPropertiesDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class CompanyDAO {
	
	private CompanyDAO() {
	}

	private static final CompanyPropertiesDAO COMPANY_PROPERTIES = new CompanyPropertiesDAO();
	protected static class CompanyPropertiesDAO extends RegistryPropertiesDAO implements CompanyProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, CompanyFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(CompanyFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(COMPANY.DOMAIN);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(COMPANY.ACTIVE);}
		@Override public Property<Byte> getSurchargeProperty() {return new FilterDAO.PropertyDAO<>(COMPANY.SURCHARGE);}
		@Override public Property<Byte> getWithholdingProperty() {return new FilterDAO.PropertyDAO<>(COMPANY.WITHHOLDING);}
		@Override public Property<Byte> getVatAccrualPaymentProperty() {return new FilterDAO.PropertyDAO<>(COMPANY.VAT_ACCRUAL_PAYMENT);}
		@Override public Property<Byte> getEInvoiceProperty() {return new FilterDAO.PropertyDAO<>(COMPANY.E_INVOICE);}
 		@Override public Property<Integer> getDomainParentProperty() {return new FilterDAO.PropertyDAO<>(Domain.DOMAIN.PARENT);}
 		
 		@Override public Property<Byte> getUserSharedProperty() {return new FilterDAO.PropertyDAO<>(USER.SHARED);}
 		@Override public Property<Byte> getDomainTypeProperty() {return new FilterDAO.PropertyDAO<>(DOMAIN.TYPE);}
 		@Override public Property<Byte> getDomainActiveProperty() {return new FilterDAO.PropertyDAO<>(Domain.DOMAIN.ACTIVE);}
	}
	
	public static class CompanyFiller implements Function<Record, Company> {
		@Override
		public Company apply(Record r) {
			return buildCompany(r, REGISTRY);
		}
		
		public static Company buildCompany(Record r, com.esferalia.aon.jooq.tables.Registry registry) {
			return new Company()
					.copy(RegistryFiller.build(r, registry))
					.setActive(AonEnumUtils.getBoolean(r.getValue(COMPANY.ACTIVE)))
					.seteInvoice(AonEnumUtils.getBoolean(r.getValue(COMPANY.E_INVOICE)))
					.setSurcharge(AonEnumUtils.getBoolean(r.getValue(COMPANY.SURCHARGE)))
					.setVatAccrualPayment(AonEnumUtils.getBoolean(r.getValue(COMPANY.VAT_ACCRUAL_PAYMENT)))
					.setWithholding(AonEnumUtils.getBoolean(r.getValue(COMPANY.WITHHOLDING)));
		}
	}
	
	public static class EnterpriseActivityFiller extends Filler implements Function<Record, EnterpriseActivity> {
		@Override
		public EnterpriseActivity apply(Record r) {
			return build(r);
		}
		
		public static EnterpriseActivity build(Record r) {
			return new EnterpriseActivity()
				.setId(r.getValue(ENTERPRISE_ACTIVITY.ID) )
				.setDescription(r.getValue(ENTERPRISE_ACTIVITY.DESCRIPTION) )
				.setPrincipal(getBoolean(r, ENTERPRISE_ACTIVITY.PRINCIPAL))
				.setIae(checkField(r, IAE.ID)
						? IaeFiller.build(r)
						: new Iae().setId(r.getValue(ENTERPRISE_ACTIVITY.IAE)))
				.setCnae(getValue(r, ENTERPRISE_ACTIVITY.CNAE2009) )
				.setCnaeCode(getValue(r, CNAE2009.CODE))
				.setCnaeDescription(getValue(r, CNAE2009.TITLE) )
				.setVatRegime(AonEnumUtils.enumValue(VATRegime.class, r.getValue(ENTERPRISE_ACTIVITY.VAT_REGIME)))
				.setVatExemptionCause(VATExemptionCause.safeValueOf(getValue(r, ENTERPRISE_ACTIVITY.VAT_EXEMPTION_CAUSE)));
		}
	}
	
	public static class IaeFiller extends Filler implements Function<Record, Iae> {
		@Override
		public Iae apply(Record r) {
			return build(r);
		}
		
		public static Iae build(Record r) {
			return new Iae()
				.setId(getValue(r, IAE.ID) )
				.setSection(getValue(r, IAE.SECTION))
				.setEpigraph(getValue(r, IAE.EPIGRAPH))
				.setTitle(getValue(r, IAE.TITLE));
		}
	}
	
	public static class CompanyValidation {
		
		private CompanyValidation() {
		}
		
		public static final BiConsumer<Company,AONContext> EMPTY_DOMAIN = (company,ctx) -> {
			if (company.getDomain() == null || company.getDomain().getId() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		public static final BiConsumer<Company,AONContext> DUPLICATED_ROW = (company,ctx) -> {
			Company c = getCompany(ctx, company.getDomain().getId());
			if (c != null) 
				throw new AonCoreException(AonError.DUPLICATED_COMPANY_ROW.getMessage());
		};

		public static void validateInsert(AONContext ctx, Company company) throws AonCoreException{
			EMPTY_DOMAIN
				.andThen(DUPLICATED_ROW)
				.accept(company, ctx);
		}

	}

	private static SelectConditionStep<Record> select(AONContext ctx, CompanyFilter filter) {
		return ctx.getDslContext().select()
				.from(COMPANY)
				.join(REGISTRY).on(REGISTRY.ID.eq(COMPANY.REGISTRY))
				.join(DOMAIN).on(DOMAIN.ID.eq(COMPANY.DOMAIN))
				.leftOuterJoin(SCOPE).on(DOMAIN.SCOPE.eq(SCOPE.ID))
				.where(COMPANY_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<Company> getStream(AONContext ctx, CompanyFilter filter){
		return select(ctx,filter)
			.fetch()
			.stream()
			.map(new CompanyFiller());
	}
	
	public static Stream<Company> getStream(AONContext ctx, CompanyFilter filter, Integer page, Integer perPage){
		return select(ctx,filter)
			.limit(perPage)
			.offset(perPage * (page -1))
			.fetch()
			.stream()
			.map(new CompanyFiller());
	}
	
	public static Company getByDomain(AONContext ctx, Integer domain){
		return getStream(ctx, p -> p.getDomainProperty().eq(domain))
			.findFirst()
			.orElse(null);
	}

	public static Company getCompany(AONContext ctx,int domain) {
		return getByDomain(ctx, domain);
	}
	
	public static Company save(AONContext ctx, Company company) {
		ctx.checkWrite();
		boolean nullId = (company.getId() == null); 
		company = RegistryDAO.save(ctx, company);
		if (nullId) {
			CompanyValidation.validateInsert(ctx, company);
			insert(ctx, company);
		} else {
			update(ctx, company);			
		}
		return company;
	}
	
	private static Company insert(AONContext ctx, Company company){
		ctx.getDslContext().insertInto(COMPANY)
			.set(COMPANY.REGISTRY,company.getId())
			.set(COMPANY.DOMAIN,company.getDomain().getId())
			.set(COMPANY.ACTIVE,AonEnumUtils.getByte(company.isActive()))
			.set(COMPANY.SURCHARGE,AonEnumUtils.getByte(company.isSurcharge()))
			.set(COMPANY.WITHHOLDING,AonEnumUtils.getByte(company.isWithholding()))
			.set(COMPANY.VAT_ACCRUAL_PAYMENT,AonEnumUtils.getByte(company.isVatAccrualPayment()))
			.set(COMPANY.E_INVOICE,AonEnumUtils.getByte(company.iseInvoice()))
			.execute();
		ctx.log().debug("INSERT COMPANY id: {0}",company.getId());		
		return company;
	}
	private static Company update(AONContext ctx, Company company){
		int count = ctx.getDslContext().update(COMPANY)
			.set(COMPANY.DOMAIN,company.getDomain().getId())
			.set(COMPANY.ACTIVE,AonEnumUtils.getByte(company.isActive()))
			.set(COMPANY.SURCHARGE,AonEnumUtils.getByte(company.isSurcharge()))
			.set(COMPANY.WITHHOLDING,AonEnumUtils.getByte(company.isWithholding()))
			.set(COMPANY.VAT_ACCRUAL_PAYMENT,AonEnumUtils.getByte(company.isVatAccrualPayment()))
			.set(COMPANY.E_INVOICE,AonEnumUtils.getByte(company.iseInvoice()))
			.where(COMPANY.REGISTRY.eq(company.getId()))
			.execute();
		ctx.log().debug("UPDATE COMPANY id: {0}. ({1} rows)",company.getId(),count);		
		return company;
	}

	// ******************************************
	// ********** FULL COMPANY *****************
	// ******************************************
	public static CompanyFull getFull(AONContext ctx, Integer domain){
		CompanyFull full = new CompanyFull();
		full.setRegistry(CompanyDAO.getByDomain(ctx, domain));  
		RegistryDAO.fillChilds(ctx, full);
		return full;
	}
	public static CompanyFull fillChilds(AONContext ctx, CompanyFull full){
		full.setDirStaff(RDirStaffDAO.getStreamByRegistry(ctx, full.getId()).collect(Collectors.toCollection(LinkedList::new)));
		return full;
	}
	
	public static CompanyFull save(AONContext ctx, CompanyFull companyFull) {
		ctx.checkWrite();
		companyFull.setRegistry(CompanyDAO.save(ctx, companyFull.getRegistry()));
		RegistryDAO.saveChilds(ctx, companyFull);
		companyFull = getFull(ctx, companyFull.getId());
		return companyFull;
	}

	/* **********************************************
	 *					ANTIGUOS M�TODOS
	 * **********************************************
	 */
	
	private static final Rmedia PHONE = RMEDIA.as("rmedia_phone"); 
	private static final Rmedia FAX = RMEDIA.as("rmedia_fax");
	private static final Rmedia EMAIL = RMEDIA.as("rmedia_email");
	private static final Rmedia WEB = RMEDIA.as("rmedia_web");

	public static Enterprise getEnterprise(AONContext ctx,int id) {
		return ctx.getDslContext().select(
				ENTERPRISE.REGISTRY
				,ENTERPRISE.DOMAIN
				,REGISTRY.DOCUMENT_TYPE
				,REGISTRY.DOCUMENT_COUNTRY
				,REGISTRY.DOCUMENT
				,REGISTRY.NAME
				,REGISTRY.ALIAS
				,RADDRESS.STREET_TYPE
				,RADDRESS.ADDRESS
				,RADDRESS.NUMBER
				,RADDRESS.ADDRESS2
				,RADDRESS.ADDRESS3
				,RADDRESS.ZIP
				,RADDRESS.MUNICIPALITY_CODE
				,RADDRESS.CITY
				,GEOZONE.CODE
				,PHONE.VALUE
				,FAX.VALUE
				,EMAIL.VALUE
				,WEB.VALUE
			)
			.from(ENTERPRISE)
			.join(REGISTRY).on(ENTERPRISE.REGISTRY.equal(REGISTRY.ID))
			.leftOuterJoin(RADDRESS).on(RADDRESS.REGISTRY.equal(REGISTRY.ID)
					.and(RADDRESS.TYPE.equal((byte) 0)))
			.leftOuterJoin(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
			.leftOuterJoin(PHONE).on(PHONE.REGISTRY.equal(REGISTRY.ID)
					.and(PHONE.MEDIA.equal(MediaType.FIXED_PHONE.value())))
			.leftOuterJoin(FAX).on(FAX.REGISTRY.equal(REGISTRY.ID)
					.and(FAX.MEDIA.equal(MediaType.FAX.value())))
			.leftOuterJoin(EMAIL).on(EMAIL.REGISTRY.equal(REGISTRY.ID)
					.and(EMAIL.MEDIA.equal(MediaType.EMAIL.value())))
			.leftOuterJoin(WEB).on(WEB.REGISTRY.equal(REGISTRY.ID)
					.and(WEB.MEDIA.equal(MediaType.WEB.value())))
			.where(ENTERPRISE.REGISTRY.equal(id))
			.fetch()
			.stream()
			.map( rec -> new Enterprise().setId(rec.getValue(ENTERPRISE.REGISTRY))
				.setDomain(rec.getValue(ENTERPRISE.DOMAIN))
				.setDocumentType(AonEnumUtils.enumValue(DocumentType.class,rec.getValue(REGISTRY.DOCUMENT_TYPE)))
				.setDocumentCountry(Country.safeValueOf(rec.getValue(REGISTRY.DOCUMENT_COUNTRY)))
				.setDocument(rec.getValue(REGISTRY.DOCUMENT))
				.setName(rec.getValue(REGISTRY.NAME))
				.setAlias(rec.getValue(REGISTRY.ALIAS))
				.setStreetType(StreetType.safeValueOf(rec.getValue(RADDRESS.STREET_TYPE)))
				.setAddress(rec.getValue(RADDRESS.ADDRESS))
				.setNumber(rec.getValue(RADDRESS.NUMBER))
				.setAddress2(rec.getValue(RADDRESS.ADDRESS2))
				.setAddress3(rec.getValue(RADDRESS.ADDRESS3))
				.setProvince(Province.safeValueOf(rec.getValue(GEOZONE.CODE)))
				.setZip(rec.getValue(RADDRESS.ZIP))
				.setTown(rec.getValue(RADDRESS.MUNICIPALITY_CODE))
				.setCity(rec.getValue(RADDRESS.CITY))
				.setPhone(rec.getValue(PHONE.VALUE))
				.setFax(rec.getValue(FAX.VALUE))
				.setEmail(rec.getValue(EMAIL.VALUE))
				.setWeb(rec.getValue(WEB.VALUE))
				)
		.findFirst()
		.orElse(null);
	}
	
	public static Stream<Company> getUserCompanyStream(AONContext ctx, Integer[] userScopes){
		return ctx.getDslContext().select()
				.from(DOMAIN)
				.join(COMPANY).on(DOMAIN.ID.eq(COMPANY.DOMAIN))
				.join(REGISTRY).on(COMPANY.REGISTRY.eq(REGISTRY.ID))
				.leftOuterJoin(SCOPE).on(DOMAIN.SCOPE.eq(SCOPE.ID))
				.where(DOMAIN.PARENT.eq(ctx.getDomainId())).and(DOMAIN.SCOPE.in(userScopes).or(DOMAIN.SCOPE.isNull()))
				.fetch().stream().map(new CompanyFiller());
	}
	
	public static Stream<Company> getUserCompanyStream(AONContext ctx, Integer[] userScopes, CompanyFilter filter){
		return COMPANY_PROPERTIES.build(ctx.getDslContext().select()
				.from(DOMAIN)
				.join(COMPANY).on(DOMAIN.ID.eq(COMPANY.DOMAIN)
						.and(DOMAIN.SCOPE.in(userScopes).or(DOMAIN.SCOPE.isNull()))
						.and(DOMAIN.ID.eq(ctx.getDomainId()).or(DOMAIN.PARENT.eq(ctx.getDomainId()))))
				.join(REGISTRY).on(COMPANY.REGISTRY.eq(REGISTRY.ID))
				.leftOuterJoin(SCOPE).on(DOMAIN.SCOPE.eq(SCOPE.ID))
			, filter)
			.fetch().stream().map(new CompanyFiller());
	}
	
	public static Stream<Company> getCompanyStream(AONContext ctx, CompanyFilter filter){
		return COMPANY_PROPERTIES.build(ctx.getDslContext().select()
				.from(COMPANY).join(REGISTRY).on(COMPANY.REGISTRY.eq(REGISTRY.ID))
				.join(DOMAIN).on(DOMAIN.ID.eq(COMPANY.DOMAIN))
				.leftOuterJoin(SCOPE).on(DOMAIN.SCOPE.eq(SCOPE.ID)), filter)
			.fetch().stream().map(new CompanyFiller());
	}
	
	public static Stream<AonCompany> getCompanyStream(AONContext ctx, byte[] auth, Integer page, Integer perPage){
		Integer[] userScopes = SecurityDAO.getAuthScopes(ctx, auth);
		Integer[] domains = SecurityDAO.getAuthDomains(ctx, auth);
		
		com.esferalia.aon.jooq.tables.Domain domain = DOMAIN.as("d");
		com.esferalia.aon.jooq.tables.Domain parent = DOMAIN.as("p");
		return ctx.getDslContext().select()
			.from(COMPANY)
			.join(REGISTRY).on(COMPANY.REGISTRY.eq(REGISTRY.ID))
			.join(domain).on(COMPANY.DOMAIN.eq(domain.ID))
			.join(USER).on(USER.DOMAIN.eq(domain.ID).or(USER.DOMAIN.eq(domain.PARENT)))
			.leftOuterJoin(SCOPE).on(domain.SCOPE.eq(SCOPE.ID))
			.leftOuterJoin(APP_PARAM).on(domain.ID.eq(APP_PARAM.DOMAIN).and(APP_PARAM.NAME.eq(AppParam.FS_DEFAULT_ADMINISTRATION.getValue())))
			.leftOuterJoin(parent).on(domain.PARENT.eq(parent.ID))
			.where(
				USER.AUTH.eq(auth)
				.and(
					domain.ID.in(domains)
					.or(
						domain.PARENT.in(domains)
						.and(
							domain.SCOPE.isNull()
							.or(domain.SCOPE.in(userScopes))
						)
					)
				)
			)
			.orderBy(REGISTRY.NAME)
			.fetch().stream().map(new AonCompanyFiller());
	}
	
	public static Stream<AonCompany> getCompanyWithRolesStream(AONContext ctx, byte[] auth, Integer page, Integer perPage){
		Integer[] userScopes = SecurityDAO.getAuthScopes(ctx, auth);
		Integer[] domains = SecurityDAO.getAuthDomains(ctx, auth);
		Byte[] roles = new Byte[]{AonRole.ENTERPRISE.value(), AonRole.EMPLOYEE.value()};
		com.esferalia.aon.jooq.tables.Domain domain = DOMAIN.as("d");
		com.esferalia.aon.jooq.tables.Domain parent = DOMAIN.as("p");
		return ctx.getDslContext().select()
			.from(COMPANY)
			.join(REGISTRY).on(COMPANY.REGISTRY.eq(REGISTRY.ID))
			.join(domain).on(COMPANY.DOMAIN.eq(domain.ID))
			.join(USER).on(USER.DOMAIN.eq(domain.ID).or(USER.DOMAIN.eq(domain.PARENT)))
			.leftOuterJoin(SCOPE).on(domain.SCOPE.eq(SCOPE.ID))
			.leftOuterJoin(APP_PARAM).on(domain.ID.eq(APP_PARAM.DOMAIN).and(APP_PARAM.NAME.eq(AppParam.FS_DEFAULT_ADMINISTRATION.getValue())))
			.leftOuterJoin(parent).on(domain.PARENT.eq(parent.ID))
			.leftOuterJoin(USER_APP_ROLE).on(
					USER_APP_ROLE.USER_ID.eq(USER.ID)
					.and(USER_APP_ROLE.DOMAIN.eq(domain.ID)
					.and(USER_APP_ROLE.ROLE.in(roles)))
					)
			.where(
				USER.AUTH.eq(auth)
				.and(
					domain.ID.in(domains)
					.or(
						domain.PARENT.in(domains)
						.and(
							domain.SCOPE.isNull()
							.or(domain.SCOPE.in(userScopes))
						)
					)
				).and(USER_APP_ROLE.ID.isNotNull())
			)
			.groupBy(COMPANY.REGISTRY)
			.orderBy(REGISTRY.NAME)
			.fetch().stream().map(new AonCompanyFiller());
	}
	
	public static Stream<AonCompany> getCompanyStream(AONContext ctx, byte[] auth, CompanyFilter filter, Integer page, Integer perPage){
		com.esferalia.aon.jooq.tables.Domain parent = DOMAIN.as("p");
		
		Integer[] userScopes = SecurityDAO.getAuthScopes(ctx, auth);
		Integer[] domains = SecurityDAO.getAuthDomains(ctx, auth);
		
		SelectSeekStep1<Record, String> query = ctx.getDslContext()
		.select(COMPANY.fields())
		.select(DOMAIN.fields())
		.select(REGISTRY.fields())
		.select(REGISTRY.fields())
		.select(parent.ID, parent.NAME)
		.select(USER.SHARED)
		.select(APP_PARAM.VALUE)
		.from(COMPANY)
		.join(REGISTRY).on(COMPANY.REGISTRY.eq(REGISTRY.ID))
		.join(DOMAIN).on(COMPANY.DOMAIN.eq(DOMAIN.ID))
		.join(USER).on(USER.DOMAIN.eq(DOMAIN.ID).or(USER.DOMAIN.eq(DOMAIN.PARENT)))
		.leftOuterJoin(SCOPE).on(DOMAIN.SCOPE.eq(SCOPE.ID))
		.leftOuterJoin(APP_PARAM).on(DOMAIN.ID.eq(APP_PARAM.DOMAIN).and(APP_PARAM.NAME.eq(AppParam.FS_DEFAULT_ADMINISTRATION.getValue())))
		.leftOuterJoin(parent).on(DOMAIN.PARENT.eq(parent.ID))
		.where(COMPANY_PROPERTIES.getConditions(filter))
		.and(
			USER.AUTH.eq(auth)
			.and(
				DOMAIN.ID.in(domains)
				.or(
					DOMAIN.PARENT.in(domains)
					.and(
						DOMAIN.SCOPE.isNull()
						.or(DOMAIN.SCOPE.in(userScopes))
					)
				)
			)
		)
		.orderBy(REGISTRY.NAME);
			
		if(page!=null && perPage!=null) {
			query.limit(perPage).offset(perPage * (page -1));
		}
		
		return query.fetch().stream().map(new AonCompanyFiller());
	}
	
	public static LinkedList<CompanyBank> getBanks(AONContext ctx,int enterprise) {
		LinkedList<CompanyBank> list = new LinkedList<>();
		list.addAll(
			ctx.getDslContext().select(RBANK.ID,RBANK.BANK_ACCOUNT,RBANK.BIC,RBANK.ALIAS)
			.from(COMPANY)
			.join(RBANK).on( COMPANY.REGISTRY.equal(RBANK.REGISTRY) )
			.where(COMPANY.REGISTRY.equal(enterprise))
			.and(RBANK.ACTIVE.equal((byte) 1))
			.fetch()
			.stream()
			.map(rec -> new CompanyBank()
				.setId(rec.getValue(RBANK.ID) )
				.setBankAccount(rec.getValue(RBANK.BANK_ACCOUNT) )
				.setBic(rec.getValue(RBANK.BIC) )
				.setAlias(rec.getValue(RBANK.ALIAS) ) 
				)
			.collect(Collectors.toList()));
		return list;
	}
	
	public static Stream<EnterpriseActivity> getEnterpriseActivities(AONContext ctx,int domain) {
		return getEnterpriseActivities(ctx,domain,null);
	}

	public static Stream<EnterpriseActivity> getEnterpriseActivities(AONContext ctx,int domain, Date atDate) {
		return ctx.getDslContext()
				.select()
				.from(ENTERPRISE_ACTIVITY)
				.leftOuterJoin(CNAE2009).on(CNAE2009.ID.eq(ENTERPRISE_ACTIVITY.CNAE2009))
				.leftOuterJoin(IAE).on(IAE.ID.eq(ENTERPRISE_ACTIVITY.IAE))
				.where(ENTERPRISE_ACTIVITY.DOMAIN.equal(domain)
					.and(atDate == null
						?DSL.trueCondition()
						:(
								(
								ENTERPRISE_ACTIVITY.START_DATE.isNull()
								.or(ENTERPRISE_ACTIVITY.START_DATE.le(AonDateUtils.toSql(atDate)))
								)
							.and(
								ENTERPRISE_ACTIVITY.END_DATE.isNull()
								.or(ENTERPRISE_ACTIVITY.END_DATE.ge(AonDateUtils.toSql(atDate)))
								)
						 )
						)
					)
				.fetch()
				.stream()
				.map(new EnterpriseActivityFiller());
	}

	public static EnterpriseActivity getEnterpriseActivity(AONContext ctx,Integer id) {
		if (id == null) return null;
		return ctx.getDslContext()
				.select()
				.from(ENTERPRISE_ACTIVITY)
				.leftOuterJoin(CNAE2009).on(CNAE2009.ID.eq(ENTERPRISE_ACTIVITY.CNAE2009))
				.leftOuterJoin(IAE).on(IAE.ID.eq(ENTERPRISE_ACTIVITY.IAE))
				.where(ENTERPRISE_ACTIVITY.DOMAIN.equal(ctx.getDomainId()))
				.and(ENTERPRISE_ACTIVITY.ID.equal(id))
				.fetch()
				.stream()
				.map(new EnterpriseActivityFiller())
				.findFirst()
				.orElse(null);
	}
	
	public static Stream<InvestAsset> getInvestAssets(AONContext ctx, int domainId, Date atDate) {
		return ctx.getDslContext()
				.select()
				.from(INVEST_ASSET)
				.where(INVEST_ASSET.DOMAIN.equal(domainId)
						.and(atDate == null
							?DSL.trueCondition()
							:((INVEST_ASSET.START_DATE.isNull().or(INVEST_ASSET.START_DATE.le(AonDateUtils.toSql(atDate))))
							.and(INVEST_ASSET.END_DATE.isNull().or(INVEST_ASSET.END_DATE.ge(AonDateUtils.toSql(atDate)))))
							)
						)
				.fetch()
				.stream()
				.map(new InvestAssetFiller());
	}

}
