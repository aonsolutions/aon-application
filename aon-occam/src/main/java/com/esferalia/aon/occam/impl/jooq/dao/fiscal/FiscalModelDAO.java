package com.esferalia.aon.occam.impl.jooq.dao.fiscal;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModelDetail.FS_MODEL_DETAIL;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.BatchBindStep;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectOnConditionStep;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Filter.FiscalModelFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.FiscalModelProperties;
import com.esferalia.aon.occam.api.model.config.ConfigBlock;
import com.esferalia.aon.occam.api.model.config.ConfigParams;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.InvoiceFiscalModels;
import com.esferalia.aon.occam.api.model.fiscal.InvoiceModelReportParams;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FilterDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelValidation;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class FiscalModelDAO {
	
	protected FiscalModelDAO() {
	}
	
	private static void log(AONContext ctx, String msg, Object ... params ) {
		ctx.log().debug(msg,params);
	}
	
	private static final FiscalModelPropertiesDAO FS_MODEL_PROPERTIES = new FiscalModelPropertiesDAO();
	private static class FiscalModelPropertiesDAO implements FiscalModelProperties {
		private Condition[] getConditions(FiscalModelFilter filter) {
			if (filter==null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(FS_MODEL.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(FS_MODEL.DOMAIN);}
		@Override public Property<Integer> getDomainScopeProperty() {return new FilterDAO.PropertyDAO<>(DOMAIN.SCOPE);}
		@Override public Property<Integer> getParentDomainProperty() {return new FilterDAO.PropertyDAO<>(DOMAIN.PARENT);}
		@Override public Property<Integer> getYearProperty() {return new FilterDAO.PropertyDAO<>(FS_MODEL.YEAR);}
		@Override public Property<String> getModelProperty() {return new FilterDAO.PropertyDAO<>(FS_MODEL.MODEL);}
		@Override public Property<Byte> getPeriodProperty() {return new FilterDAO.PropertyDAO<>(FS_MODEL.PERIOD);}
		@Override public Property<Byte> getAdministrationProperty() {return new FilterDAO.PropertyDAO<>(FS_MODEL.ADMINISTRATION);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(FS_MODEL.STATUS);}
		@Override public Property<Byte> getConfidentialProperty() {return new FilterDAO.PropertyDAO<>(FS_MODEL.SECURITY_LEVEL);}
		@Override public Property<Byte> getComplementaryProperty() {return new FilterDAO.PropertyDAO<>(FS_MODEL.COMPLEMENTARY);}
		@Override public Property<Byte> getReplacementProperty() {return new FilterDAO.PropertyDAO<>(FS_MODEL.REPLACEMENT);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<>(FS_MODEL.DOCUMENT);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(FS_MODEL.NAME);}
		@Override public Property<String> getSurnameProperty() {return new FilterDAO.PropertyDAO<>(FS_MODEL.SURNAME);}
		@Override public Property<Integer> getAccountEntryProperty() {return new FilterDAO.PropertyDAO<>(FS_MODEL.ACCOUNT_ENTRY);}
		@Override public Property<Double> getResultProperty() {return new FilterDAO.PropertyDAO<>(FS_MODEL.RESULT);}
		@Override public Property<Byte> getResultTypeProperty() {return new FilterDAO.PropertyDAO<>(FS_MODEL.DECLARATION_TYPE);}
	}
	
	protected static class FiscalModelFiller<T extends FiscalModel>  implements BiFunction<Record,Supplier<T>,T> {
		public FiscalModelFiller() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public T apply(Record rec,Supplier<T> modelSupplier) {
			T model = modelSupplier.get();
			model.setId(rec.getValue(FS_MODEL.ID));
			model.setDomain(rec.getValue(FS_MODEL.DOMAIN));
			model.setDomainName(rec.getValue(DOMAIN.DESCRIPTION));
			model.setYear(rec.getValue(FS_MODEL.YEAR));
			model.setPeriod( Period.safeValueOf(rec.getValue(FS_MODEL.PERIOD)));
			model.setAdministration(Administration.safeValueOf(rec.getValue(FS_MODEL.ADMINISTRATION)));
			model.setStatus(FiscalStatus.safeValueOf(rec.getValue(FS_MODEL.STATUS)));
			model.setConfidential(AonEnumUtils.getBoolean( rec.getValue(FS_MODEL.SECURITY_LEVEL)));
			model.setComplementary(AonEnumUtils.getBoolean( rec.getValue(FS_MODEL.COMPLEMENTARY)));
			model.setReplacement(AonEnumUtils.getBoolean( rec.getValue(FS_MODEL.REPLACEMENT)));
			model.setWithoutActivity(AonEnumUtils.getBoolean( rec.getValue(FS_MODEL.WITHOUTACTIVITY)));
			model.setModel(FiscalModelType.safeValueOf( rec.getValue(FS_MODEL.MODEL)));
			model.setNumber(rec.getValue(FS_MODEL.NUMBER));
			model.setReplacedNumber(rec.getValue(FS_MODEL.REPLACED_NUMBER));
			model.setComments(rec.getValue(FS_MODEL.COMMENTS));
			model.setFinance(rec.getValue(FS_MODEL.FINANCE) == null?null:new FinanceDAO.FullFinanceFiller().apply(rec));
			model.setDocument(rec.getValue(FS_MODEL.DOCUMENT));
			model.setSurname(rec.getValue(FS_MODEL.SURNAME));
			model.setName(rec.getValue(FS_MODEL.NAME));
			model.setStreetInitial(rec.getValue(FS_MODEL.STREET_INITIAL));
			model.setStreetName(rec.getValue(FS_MODEL.STREET_NAME));
			model.setStreetNumber(rec.getValue(FS_MODEL.STREET_NUMBER));
			model.setStreetStair(rec.getValue(FS_MODEL.STREET_STAIR));
			model.setStreetFloor(rec.getValue(FS_MODEL.STREET_FLOOR));
			model.setStreetDoor(rec.getValue(FS_MODEL.STREET_DOOR));
			model.setPhone(rec.getValue(FS_MODEL.PHONE));
			model.setTown(rec.getValue(FS_MODEL.TOWN));
			model.setProvince(rec.getValue(FS_MODEL.PROVINCE));
			model.setZip(rec.getValue(FS_MODEL.ZIP));
			model.setAdmonAeat(rec.getValue(FS_MODEL.ADMON_AEAT));
			model.setContactPerson(rec.getValue(FS_MODEL.CONTACT_PERSON));
			model.setContactPhone(rec.getValue(FS_MODEL.CONTACT_PHONE));
			model.setContactCellular(rec.getValue(FS_MODEL.CONTACT_CELLULAR));
			model.setContactEmail(rec.getValue(FS_MODEL.CONTACT_EMAIL));
			model.setDeclarationResult(rec.getValue(FS_MODEL.RESULT));
			model.setDeclarationResultType(FiscalModelDeclarationType.safeValueOf(rec.getValue(FS_MODEL.DECLARATION_TYPE)));
			model.setAccountEntry(rec.getValue(FS_MODEL.ACCOUNT_ENTRY));
			model.setCreationUser(rec.getValue(FS_MODEL.CREATION_USER));
			model.setCreationDate(rec.getValue(FS_MODEL.CREATION_DATE));
			model.setModificationUser(rec.getValue(FS_MODEL.MODIFICATION_USER));
			model.setModificationDate(rec.getValue(FS_MODEL.MODIFICATION_DATE));
			return model; 
		}
	}
	
	private static class FiscalModelDetailFiller  implements Function<Record,FiscalModelDetail> {
		@Override
		public FiscalModelDetail apply(Record rec) {
			return new FiscalModelDetail()
				.setId(rec.getValue(FS_MODEL_DETAIL.ID))
				.setType(rec.getValue(FS_MODEL_DETAIL.TYPE))
				.setDescription(rec.getValue(FS_MODEL_DETAIL.DESCRIPTION))
				.setAccumulatedAmount(rec.getValue(FS_MODEL_DETAIL.ACU_AMOUNT))
				.setDeclaredAmount(rec.getValue(FS_MODEL_DETAIL.DEC_AMOUNT))
				.setResultAmount(rec.getValue(FS_MODEL_DETAIL.RES_AMOUNT))
				.setAdjustAmount(rec.getValue(FS_MODEL_DETAIL.ADJ_AMOUNT))
				.setAmount(rec.getValue(FS_MODEL_DETAIL.AMOUNT));
		}
	}

	protected static SelectOnConditionStep<Record> getSelect(final AONContext ctx) {
		return ctx.getDslContext()
			.select()
			.from(FS_MODEL)
			.join(DOMAIN).on(DOMAIN.ID.equal(FS_MODEL.DOMAIN))
			.leftOuterJoin(FINANCE).on(FINANCE.ID.equal(FS_MODEL.FINANCE))
			.leftOuterJoin(REGISTRY).on(REGISTRY.ID.equal(FINANCE.REGISTRY))
			.leftOuterJoin(SCOPE).on(FINANCE.SCOPE.equal(SCOPE.ID))
			.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
			;		
	}

	public static <T extends FiscalModel> T get(final AONContext ctx,Supplier<T> modelSupplier, int id) {
		ctx.checkRead();
		return getSelect(ctx)
			.where(FS_MODEL.ID.eq(id))
			.fetch()
			.stream()
			.map(rec -> new FiscalModelFiller<T>().apply(rec,modelSupplier))
			.map(mod -> fillModelDetails(ctx,mod))
			.findFirst()
			.orElse(null);
	}
	
	public static <T extends FiscalModel> Stream<T> getFiscalModels(AONContext ctx,int domain, FiscalModelType model, Supplier<T> modelSupplier) {
		return getFiscalModels(ctx,domain,model, null, modelSupplier); 	
	}
	public static <T extends FiscalModel> Stream<T> getFiscalModels(AONContext ctx, int domain, FiscalModelType model, FiscalModelFilter filter, Supplier<T> modelSupplier)  {
		ctx.checkRead();
		return getSelect(ctx)
			.where(FS_MODEL_PROPERTIES.getConditions(filter))
			.and(FS_MODEL.MODEL.eq(model.getValue()))
			.and(FS_MODEL.DOMAIN.eq(domain))
			.orderBy(FS_MODEL.YEAR.desc(),FS_MODEL.MODEL.asc(),FS_MODEL.ADMINISTRATION.desc(),FS_MODEL.PERIOD.desc(),FS_MODEL.ID.desc())
			.fetch()
			.stream()
			.map(rec -> new FiscalModelFiller<T>().apply(rec, modelSupplier));
	}
	public static <T extends FiscalModel> Stream<T> getFullFiscalModels(AONContext ctx, int domain, FiscalModelType model, FiscalModelFilter filter, Supplier<T> modelSupplier)  {
		return getFiscalModels(ctx,domain,model, filter, modelSupplier)
				.map(mod -> fillModelDetails(ctx,mod));
	}
	
	public static <T extends FiscalModel> Stream<T> getPreviousModels(AONContext ctx,FiscalModel fm, Supplier<T> modelSupplier) {
		return getPreviousModels(ctx,fm,false,modelSupplier);
	}
	public static <T extends FiscalModel> Stream<T> getPreviousModels(AONContext ctx,FiscalModel fm, boolean desc, Supplier<T> modelSupplier) {
		ctx.checkRead();
		return getSelect(ctx)
			.where(FS_MODEL.DOMAIN.eq(fm.getDomain()))
			.and(FS_MODEL.MODEL.eq(fm.getModel().getValue()))
			.and(FS_MODEL.YEAR.eq(fm.getYear()))
			.and(FS_MODEL.ADMINISTRATION.eq(fm.getAdministration().value()))
			.and(FS_MODEL.PERIOD.lessThan(fm.getPeriod().value()))
			.orderBy(desc?FS_MODEL.PERIOD.desc():FS_MODEL.PERIOD.asc())
			.fetch()
			.stream()
			.map(rec -> new FiscalModelFiller<T>().apply(rec, modelSupplier))
			.filter(fim -> fim.getPeriod().isMonthPeriod() == fm.getPeriod().isMonthPeriod())
			.filter(fim -> fim.getPeriod().isQuarterPeriod() == fm.getPeriod().isQuarterPeriod())
			.map(mod -> fillModelDetails(ctx,mod));
	}
	
	public static <T extends FiscalModel> Stream<T> getSamePeriodFiscalModels(AONContext ctx,FiscalModel fm, Supplier<T> modelSupplier) {
		ctx.checkRead();
		return getSelect(ctx)
			.where(FS_MODEL.DOMAIN.eq(fm.getDomain()))
			.and(FS_MODEL.MODEL.eq(fm.getModel().getValue()))
			.and(FS_MODEL.YEAR.eq(fm.getYear()))
			.and(FS_MODEL.ADMINISTRATION.eq(fm.getAdministration().value()))
			.and(FS_MODEL.PERIOD.eq(fm.getPeriod().value()))
			.and(fm.getId()==null?DSL.trueCondition():FS_MODEL.ID.lt(fm.getId()))
			.orderBy(FS_MODEL.ID.desc())
			.fetch()
			.stream()
			.map(rec -> new FiscalModelFiller<T>().apply(rec, modelSupplier))
			;
	}
	public static <T extends FiscalModel> Stream<T> getSamePeriodModels(AONContext ctx,FiscalModel fm, Supplier<T> modelSupplier) {
		return getSamePeriodFiscalModels(ctx, fm, modelSupplier)
			.map(mod -> fillModelDetails(ctx,mod));
	}
	
//	public static <T extends FiscalModel> Stream<T> getEffectivePreviousModels(AONContext ctx,FiscalModel fiscalModel, Supplier<T> modelSupplier) {
//		LinkedList<T> effectivePreviousModels = new LinkedList<>();
//		LinkedList<T> previousModels = getPreviousModels(ctx, fiscalModel, modelSupplier)
//				.collect(Collectors.toCollection(LinkedList::new));
//		for ( T fm : previousModels ) {
//			if (fm.isComplementary() || (!fm.isComplementary() && 
//				 previousModels.stream().noneMatch(fm2 -> fm2.isComplementary() 
//					&& 	fm2.getYear() == fm.getYear()
//					&& 	fm2.getPeriod().ordinal() == fm.getPeriod().ordinal()
//				))) {
//				effectivePreviousModels.add(fm);
//			}
//		}
//		return effectivePreviousModels.stream();
//	}
	
	public static <T extends FiscalModel> Stream<T> getLastPeriodModels(AONContext ctx,FiscalModel fiscalModel, Supplier<T> modelSupplier) {
		ctx.checkRead();
		if (fiscalModel.getPeriod() == Period.M01 || fiscalModel.getPeriod() == Period.T1) {
			return Stream.empty();
		}
		return getSelect(ctx)
			.where(FS_MODEL.DOMAIN.eq(fiscalModel.getDomain()))
			.and(FS_MODEL.MODEL.eq(fiscalModel.getModel().getValue()))
			.and(FS_MODEL.YEAR.eq(fiscalModel.getYear()))
			.and(FS_MODEL.ADMINISTRATION.eq(fiscalModel.getAdministration().value()))
			.and(FS_MODEL.PERIOD.eq((byte) ( fiscalModel.getPeriod().value() - 1 )))
			.orderBy(FS_MODEL.PERIOD.desc(),FS_MODEL.COMPLEMENTARY.desc()
					,FS_MODEL.REPLACEMENT.desc(),FS_MODEL.ID.desc())
			.fetch()
			.stream()
			.map(rec -> new FiscalModelFiller<T>().apply(rec, modelSupplier))
			.map(mod -> fillModelDetails(ctx,mod));
	}

	protected static <T extends FiscalModel> T fillModelDetails(AONContext ctx,T model) {
		getModelDetails(ctx,model).forEach( model::put );
		return model;
	}
	private static <T extends FiscalModel> Stream<FiscalModelDetail> getModelDetails(AONContext ctx,T fm) {
		ctx.checkRead();
		return ctx.getDslContext().selectFrom( FS_MODEL_DETAIL)
			.where(FS_MODEL_DETAIL.FS_MODEL.eq(fm.getId()))
			.fetch()
			.stream()
			.map(new FiscalModelDetailFiller() )
			;
	}
		
	public static <T extends FiscalModel> T save(AONContext ctx, T fm) {
		try {
			ctx.checkWrite();
			FiscalModelValidation.validate(ctx,fm);
			if (fm.getId() == null) {
				insert(ctx, fm);
			} else {
				update(ctx, fm);
			}
			return get(ctx, () -> fm, fm.getId() );
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Exception t) {
			throw new AonCoreException(t.getMessage());
		}
	}

	private static <T extends FiscalModel> T insert(AONContext ctx, T fm) {
		Integer id = ctx.getDslContext()
			.insertInto(FS_MODEL)
				.set(FS_MODEL.DOMAIN, fm.getDomain())
				.set(FS_MODEL.YEAR, fm.getYear())
				.set(FS_MODEL.PERIOD, fm.getPeriod().value() )
				.set(FS_MODEL.ADMINISTRATION, fm.getAdministration().value() )
				.set(FS_MODEL.STATUS, AonEnumUtils.getByte( fm.getStatus() ) )
				.set(FS_MODEL.SECURITY_LEVEL,AonEnumUtils.getByte( fm.isConfidential() ))
				.set(FS_MODEL.COMPLEMENTARY,AonEnumUtils.getByte( fm.isComplementary() ))
				.set(FS_MODEL.REPLACEMENT,AonEnumUtils.getByte( fm.isReplacement() ))
				.set(FS_MODEL.WITHOUTACTIVITY,AonEnumUtils.getByte( fm.isWithoutActivity()  ))
				.set(FS_MODEL.MODEL, fm.getModel().getValue() )
				.set(FS_MODEL.NUMBER,fm.getNumber())
				.set(FS_MODEL.REPLACED_NUMBER,fm.getReplacedNumber())
				.set(FS_MODEL.COMMENTS,fm.getComments())
				.set(FS_MODEL.FINANCE, fm.getFinance() == null?null:fm.getFinance().getId())
				.set(FS_MODEL.DOCUMENT, fm.getDocument() )
				.set(FS_MODEL.SURNAME,fm.getSurname())
				.set(FS_MODEL.NAME,fm.getName())
				.set(FS_MODEL.STREET_INITIAL,fm.getStreetInitial())
				.set(FS_MODEL.STREET_NAME,fm.getStreetName())
				.set(FS_MODEL.STREET_NUMBER,fm.getStreetNumber())
				.set(FS_MODEL.STREET_STAIR,fm.getStreetStair())
				.set(FS_MODEL.STREET_FLOOR,fm.getStreetFloor())
				.set(FS_MODEL.STREET_DOOR,fm.getStreetDoor())
				.set(FS_MODEL.PHONE,fm.getPhone())
				.set(FS_MODEL.TOWN,fm.getTown())
				.set(FS_MODEL.PROVINCE,fm.getProvince())
				.set(FS_MODEL.ZIP,fm.getZip())
				.set(FS_MODEL.ADMON_AEAT,fm.getAdmonAeat())
				.set(FS_MODEL.CONTACT_PERSON,fm.getContactPerson())
				.set(FS_MODEL.CONTACT_PHONE,fm.getContactPhone())
				.set(FS_MODEL.CONTACT_CELLULAR,fm.getContactCellular())
				.set(FS_MODEL.CONTACT_EMAIL,fm.getContactEmail())
				.set(FS_MODEL.RESULT,fm.getDeclarationResult())
				.set(FS_MODEL.DECLARATION_TYPE,AonEnumUtils.getByte( fm.getDeclarationResultType() ) )
				.set(FS_MODEL.ACCOUNT_ENTRY,fm.getAccountEntry())
				.set(FS_MODEL.CREATION_USER,ctx.getUser())
				.set(FS_MODEL.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.returning(FS_MODEL.ID)
			.fetchOne()
			.getValue(FS_MODEL.ID);
		fm.setId(id);
		log(ctx,"INSERT FS_MODEL id: {0} Mod: {1}",id, fm.getModel());
		insertDetails(ctx, fm);
		return fm;
	}
	
	private static <T extends FiscalModel> T update(AONContext ctx, T fm) {
		ctx.getDslContext().update(FS_MODEL)
			.set(FS_MODEL.DOMAIN, fm.getDomain())
			.set(FS_MODEL.YEAR, fm.getYear())
			.set(FS_MODEL.ADMINISTRATION, fm.getAdministration().value() )
			.set(FS_MODEL.STATUS, AonEnumUtils.getByte( fm.getStatus()  ) )
			.set(FS_MODEL.SECURITY_LEVEL,AonEnumUtils.getByte( fm.isConfidential() ))
			.set(FS_MODEL.COMPLEMENTARY,AonEnumUtils.getByte( fm.isComplementary() ))
			.set(FS_MODEL.REPLACEMENT,AonEnumUtils.getByte( fm.isReplacement() ))
			.set(FS_MODEL.WITHOUTACTIVITY,AonEnumUtils.getByte( fm.isWithoutActivity()  ))
			.set(FS_MODEL.MODEL, fm.getModel().getValue() )
			.set(FS_MODEL.NUMBER,fm.getNumber())
			.set(FS_MODEL.REPLACED_NUMBER,fm.getReplacedNumber())
			.set(FS_MODEL.COMMENTS,fm.getComments())
			.set(FS_MODEL.FINANCE, fm.getFinance() == null?null:fm.getFinance().getId())
			.set(FS_MODEL.DOCUMENT, fm.getDocument() )
			.set(FS_MODEL.SURNAME,fm.getSurname())
			.set(FS_MODEL.NAME,fm.getName())
			.set(FS_MODEL.STREET_INITIAL,fm.getStreetInitial())
			.set(FS_MODEL.STREET_NAME,fm.getStreetName())
			.set(FS_MODEL.STREET_NUMBER,fm.getStreetNumber())
			.set(FS_MODEL.STREET_STAIR,fm.getStreetStair())
			.set(FS_MODEL.STREET_FLOOR,fm.getStreetFloor())
			.set(FS_MODEL.STREET_DOOR,fm.getStreetDoor())
			.set(FS_MODEL.PHONE,fm.getPhone())
			.set(FS_MODEL.TOWN,fm.getTown())
			.set(FS_MODEL.PROVINCE,fm.getProvince())
			.set(FS_MODEL.ZIP,fm.getZip())
			.set(FS_MODEL.ADMON_AEAT,fm.getAdmonAeat())
			.set(FS_MODEL.CONTACT_PERSON,fm.getContactPerson())
			.set(FS_MODEL.CONTACT_PHONE,fm.getContactPhone())
			.set(FS_MODEL.CONTACT_CELLULAR,fm.getContactCellular())
			.set(FS_MODEL.CONTACT_EMAIL,fm.getContactEmail())
			.set(FS_MODEL.RESULT,fm.getDeclarationResult())
			.set(FS_MODEL.DECLARATION_TYPE,AonEnumUtils.getByte( fm.getDeclarationResultType() ) )
			.set(FS_MODEL.ACCOUNT_ENTRY,fm.getAccountEntry())
			.set(FS_MODEL.MODIFICATION_USER,ctx.getUser())
			.set(FS_MODEL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
		.where(FS_MODEL.ID.equal(fm.getId()))
		.execute();
		log(ctx,"UPDATE FS_MODEL id: {0} Mod: {1}",fm.getId(), fm.getModel());
		deleteDetails(ctx, fm);
		insertDetails(ctx, fm);
		return fm;
	}

	private static void deleteDetails(AONContext ctx, FiscalModel fm) {
		int count = ctx.getDslContext()
			.delete(FS_MODEL_DETAIL)
			.where(FS_MODEL_DETAIL.FS_MODEL.equal(fm.getId()))
			.execute();
		log(ctx,"\tDELETE FS_MODEL_DETAIL id: {0} Mod: {1} {2} rows",fm.getId(), fm.getModel(), count);
	}

	private static void insertDetails(AONContext ctx, FiscalModel fm) {
		BatchBindStep batch = ctx.getDslContext()
			.batch(ctx.getDslContext().insertInto(FS_MODEL_DETAIL
				,FS_MODEL_DETAIL.DOMAIN
				,FS_MODEL_DETAIL.FS_MODEL
				,FS_MODEL_DETAIL.TYPE
				,FS_MODEL_DETAIL.DESCRIPTION
				,FS_MODEL_DETAIL.ACU_AMOUNT
				,FS_MODEL_DETAIL.DEC_AMOUNT
				,FS_MODEL_DETAIL.RES_AMOUNT
				,FS_MODEL_DETAIL.ADJ_AMOUNT
				,FS_MODEL_DETAIL.AMOUNT)
			.values(null,null,(String) null, (String) null,null,null,null,null,null));
		fm.getMap().values().stream()
		.forEach(detail -> batch.bind(
			fm.getDomain()
			,fm.getId()
			,detail.getType() 
			,AonStringUtils.abbreviate(detail.getDescription(), FS_MODEL_DETAIL.DESCRIPTION.getDataType().length())
			,detail.getAccumulatedAmount()  
			,detail.getDeclaredAmount() 
			,detail.getResultAmount() 
			,detail.getAdjustAmount() 
			,detail.getAmount()));
		batch.execute();
		log(ctx,"\tINSERT FS_MODEL_DETAIL id: {0} Mod: {1} {2} rows",fm.getId(), fm.getModel(), batch.size());
	}
	
	public static <T extends FiscalModel> void delete(AONContext ctx, T fm) {
		ctx.checkWrite();
		FiscalModelValidation.validateDelete(ctx, fm);
		deleteDetails(ctx, fm);
		AlcatrazDAO.deleteFiscalModel(ctx, fm);
		int count = ctx.getDslContext()
			.delete(FS_MODEL)
				.where(FS_MODEL.ID.equal(fm.getId()))
			.execute();
		log(ctx,"DELETE id: {0} Mod: {1} {2} rows",fm.getId(), fm.getModel(), count);
	}

	protected static <T extends FiscalModel> T initializeFiscalModel(AONContext ctx, T fm) {
		AonConfiguration conf = ConfigurationDAO.getConfiguration(ctx);		 
		if (fm.getDomain() == 0) throw new AonCoreException("[INTERNO] No se ha indicado el dominio para la declaraci\u00F3n.");
		fm.setDocument(conf.getCompany().getDocument());
		fm.setName(conf.getCompany().getName());
		if (fm.getAdministration() == null) {
			fm.setAdministration(conf.fiscal().getAdministration(Administration.COMMON_TERRITORY));
		}
		if (fm.getYear() < 2005 || fm.getYear() > 2050) {
			Date today = new Date();
			int year = AonDateUtils.getYear(today);
			int month = AonDateUtils.getMonth(today);
			if (month == 0) {
				year = year - 1;
				month = 12;  // Mas abajo restamos.
			}
			fm.setYear(year);
			if (fm.getModel().isYearly()) {
				fm.setPeriod( Period.YEAR );
			}else {
				fm.setPeriod( Period.getQuarterlyPeriod(month-1));
			}
		}
		fm.setAdmonAeat(conf.fiscal().getAdministrationCode());
		fm.setStatus(FiscalStatus.PENDING);
		
		return initializeIdentificationData(ctx, fm, conf);
	}
	
	protected static <T extends FiscalModel> T initializeIdentificationData(AONContext ctx, T fm) {
		return initializeIdentificationData(ctx, fm, ConfigurationDAO.getConfiguration(ctx));	
	}
	
	private static <T extends FiscalModel> T initializeIdentificationData(AONContext ctx, T fm,AonConfiguration conf) {
		Company company = CompanyDAO.getCompany(ctx,fm.getDomain());
		Enterprise enterprise = CompanyDAO.getEnterprise(ctx, company.getId() );
		fm.setDocument(enterprise==null?company.getDocument():enterprise.getDocument());
		String name = enterprise==null?company.getName():enterprise.getName();
		DocumentType docType = enterprise==null?company.getDocumentType():enterprise.getDocumentType();
		if (docType != DocumentType.CIF) {
			if (AonStringUtils.contains(name, ',')) {
				fm.setName(AonStringUtils.trim(AonStringUtils.substringAfter(name, ",")));
				fm.setSurname(AonStringUtils.trim(AonStringUtils.substringBefore(name, ",")));
			} else {
				fm.setName(AonStringUtils.trim(AonStringUtils.substringBefore(name, " ")));
				fm.setSurname(AonStringUtils.trim(AonStringUtils.substringAfter(name, " ")));
			}
		} else {
			fm.setName(name);	
			fm.setSurname(null);
		}
		if (enterprise != null) {
			fm.setStreetInitial( enterprise.getStreetType() == null?null:enterprise.getStreetType().getAeatCode() );
			fm.setStreetName( AonStringUtils.left(enterprise.getAddress(),17) );
			fm.setStreetNumber( enterprise.getNumber() ); 
			fm.setTown( AonStringUtils.left(enterprise.getCity(),20));
			fm.setProvince(enterprise.getProvince()==null?"":enterprise.getProvince().toString());
			fm.setZip(AonStringUtils.defaultIfBlank(enterprise.getZip(), "00000"));
			fm.setPhone(enterprise.getPhone() );
		}
		fm.setContactPerson( conf.fiscal().getContactPerson() );
		fm.setContactPhone(conf.fiscal().getContactPhone() );
		fm.setContactCellular( conf.fiscal().getContactCellular() );
		fm.setContactEmail( conf.fiscal().getContactMail() );
		
		fm.setName( AonStringUtils.substring(fm.getName(), 0, FS_MODEL.NAME.getDataType().length()));
		fm.setSurname( AonStringUtils.substring(fm.getSurname(), 0, FS_MODEL.SURNAME.getDataType().length()));
		fm.setStreetName( AonStringUtils.substring(fm.getStreetName(), 0, FS_MODEL.STREET_NAME.getDataType().length()));
		fm.setTown( AonStringUtils.substring(fm.getTown(), 0, FS_MODEL.TOWN.getDataType().length())); 
		fm.setZip( AonStringUtils.substring(fm.getZip(), 0, FS_MODEL.ZIP.getDataType().length()));
		fm.setPhone( AonStringUtils.substring(fm.getPhone(), 0, FS_MODEL.PHONE.getDataType().length()));
		fm.setContactPerson( AonStringUtils.substring(fm.getContactPerson(), 0, FS_MODEL.CONTACT_PERSON.getDataType().length()));
		fm.setContactPhone( AonStringUtils.substring(fm.getContactPhone(), 0, FS_MODEL.CONTACT_PHONE.getDataType().length()));
		fm.setContactCellular( AonStringUtils.substring(fm.getContactCellular(), 0, FS_MODEL.CONTACT_CELLULAR.getDataType().length()));
		fm.setContactEmail( AonStringUtils.substring(fm.getContactEmail(), 0, FS_MODEL.CONTACT_EMAIL.getDataType().length())); 
		
		return fm;
	}

	public static <T extends FiscalModel> T initializeForFinish(AONContext ctx,T fiscalModel) {
		if (fiscalModel.getDeclarationResultType() != null && 
			fiscalModel.getDeclarationResultType().mustCreateFinance()) {
			Creditor creditor = getCreditor(ctx,fiscalModel);
			String concept = "Mod." + FiscalModelUtils.getModelName(fiscalModel) 
				+ " - " + fiscalModel.getYear() 
				+ " / " + fiscalModel.getPeriod().getName( );

			concept = AonStringUtils.abbreviate(concept, 32);
			Finance finance = new Finance()
					.setPayment(true)
					.setRegistry(creditor)
					.setRegistryDocument(creditor!=null?creditor.getDocument():null)
					.setRegistryDocumentCountry(creditor!=null?creditor.getDocumentCountry():null)
					.setRegistryDocumentType(creditor!=null?creditor.getDocumentType():null)
					.setRegistryName(creditor!=null?creditor.getName():null)
					.setConfidential(fiscalModel.isConfidential())
					.setAmount(fiscalModel.getDeclarationResult())
					.setFinanceStatus(FinanceStatus.PENDING)
					.setDueDate(FiscalUtils.getPeriodEnd(fiscalModel))
					.setConcept(concept)
					;
			fiscalModel.setFinance(finance);
		}
		return fiscalModel;
	}

	private static <T extends FiscalModel> Creditor getCreditor(AONContext ctx,T fiscalModel) {
		AonConfiguration conf = ConfigurationDAO.getConfiguration(ctx,new ConfigParams().setBlocks(ConfigBlock.FISCAL)); 
		Creditor creditor = null;
		if (fiscalModel.getModel() != null && fiscalModel.getModel().isVat()) {
			creditor = conf.fiscal().getAdmonVatCreditor();
		} else if (fiscalModel.getModel() != null && fiscalModel.getModel().isRetention()) {
			creditor = conf.fiscal().getAdmonRetentionCreditor();
		}
		if ( creditor == null ) {
			creditor = conf.fiscal().getAdmonCreditor();	
		}
		return creditor;
	}
	
	protected static <T extends FiscalModel> Integer getFinance(AONContext ctx,T fm) {
		if (fm != null && fm.getId() != null) {
			return ctx.getDslContext().select( FS_MODEL.FINANCE)
				.from(FS_MODEL)
				.where( FS_MODEL.ID.eq(fm.getId()))
				.fetch()
				.stream()
				.map( r -> r.getValue(FS_MODEL.FINANCE))
				.filter( Objects::nonNull)
				.findFirst()
				.orElse(null);
			
		}
		return null;
	}

	protected static <T extends FiscalModel> T finish(AONContext ctx,T fm) {
		fm.setStatus(FiscalStatus.FINISHED);
		if (fm.getDeclarationResultType() != null && fm.getDeclarationResultType().mustCreateFinance()) {
			if (fm.getFinance().getRegistry() == null || fm.getFinance().getRegistry().getId() == null) {
				throw new AonCoreException("Acreedor no v\u00E1lido.");
			}
			fm.getFinance().setFinanceStatus(FinanceStatus.PENDING);
			fm.getFinance().setDomain(fm.getDomain());
			Integer financeId = FinanceDAO.save(ctx, fm.getFinance());
			fm.setFinance(fm.getFinance().setId(financeId));	
		} else {
			fm.setFinance(null);
		}
		return fm;
	}

	protected static FiscalModel saveComments(AONContext ctx, FiscalModel fm) {
		try {
			ctx.checkWrite();
			if (fm.getId() != null) {
				ctx.getDslContext().update(FS_MODEL)
					.set(FS_MODEL.COMMENTS,fm.getComments())
					.where(FS_MODEL.ID.equal(fm.getId()))
					.execute();
			}
			return fm;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Exception t) {
			throw new AonCoreException(t.getMessage());
		}
	}
	
	public static Stream<FiscalModel> getMatrixRecords(AONContext ctx,int domain, FiscalModelFilter filter)  {
		ctx.checkRead();
		return ctx.getDslContext()
				.select()
				.from(FS_MODEL)
				.leftOuterJoin(DOMAIN).on(FS_MODEL.DOMAIN.equal(DOMAIN.ID))
				.leftOuterJoin(SCOPE).on(DOMAIN.SCOPE.equal(SCOPE.ID))
				.leftOuterJoin(FINANCE).on(FINANCE.ID.equal(FS_MODEL.FINANCE))
				.leftOuterJoin(REGISTRY).on(REGISTRY.ID.equal(FINANCE.REGISTRY))
				.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
				.where(FS_MODEL_PROPERTIES.getConditions(filter))
				.orderBy(FS_MODEL.YEAR.desc(),FS_MODEL.MODEL.asc(),FS_MODEL.PERIOD.desc(),FS_MODEL.COMPLEMENTARY.desc(),FS_MODEL.ID.desc())
				.fetch()
				.stream()
				.map( rec -> new FiscalModelFiller<FiscalModel>().apply(rec,FiscalModel::new));
	}
/*
p ->
				p.getDomainProperty().eq(ctx.getDomainId())
				 .and(p.getTypeProperty().ne(InvoiceType.UNDEDUCTIBLE.value()))
				 .and(p.getStartIssueDateProperty().ge(params.getFromDate())) 
				 .and(p.getEndIssueDateProperty().le(params.getToDate()))
				);
 
 */
	
	public static LinkedList<InvoiceFiscalModels> getInvoicesModels(AONContext ctx,InvoiceModelReportParams params) {
		ctx.checkRead();
		return InvoiceDAO.getInvoiceStream(ctx, p-> getFilter(p,params))
			.map(inv -> new InvoiceFiscalModels().setInvoice(inv))
			.map(ifm -> ifm.setModels( 
				getSelect(ctx)
					.leftOuterJoin(ALCATRAZ).on(ALCATRAZ.FS_MODEL.eq(FS_MODEL.ID))
					.where(ALCATRAZ.INVOICE.eq(ifm.getInvoice().getId()))
					.fetch()
					.stream()
					.map(rec -> new FiscalModelFiller<FiscalModel>().apply(rec,FiscalModel::new))
					.collect(Collectors.toCollection(LinkedList::new))
			))
			.filter(ifm -> !params.isUnbound()
					||  (params.isUnbound() && (ifm.getModels() == null || ifm.getModels().isEmpty())))
			.collect(Collectors.toCollection(LinkedList::new));			
	}

	private static Filter getFilter(InvoiceProperties p, InvoiceModelReportParams params) {
		if (params.getDomain() == null) {
			throw new IllegalArgumentException("No se ha indicado el dominio");
		}
		Filter f = p.getDomainProperty().eq(params.getDomain())
		 .and(p.getTypeProperty().ne(InvoiceType.UNDEDUCTIBLE.value()));
		if ( params.getFromDate() != null) {
			f = f.and(p.getStartIssueDateProperty().ge(params.getFromDate())); 	
		}
		if ( params.getToDate() != null) {
			f = f.and(p.getEndIssueDateProperty().le(params.getToDate()));	
		}
		if ( params.getActivity() != null) {
			f = f.and(p.getActivityProperty().eq(params.getActivity()));
		}
		return f;
	}  
	
	
	public static void unrecord(AONContext ctx, Integer accountEntryId) {
		ctx.getDslContext()
			.select( FS_MODEL.ID,FS_MODEL.MODEL )
			.from(FS_MODEL)
			.where(FS_MODEL.ACCOUNT_ENTRY.eq(accountEntryId))
			.fetch()
			.stream()
			.forEach(rec -> ctx.getDslContext()
				.update(FS_MODEL)
				.setNull(FS_MODEL.ACCOUNT_ENTRY)
				.set(FS_MODEL.MODIFICATION_USER,ctx.getUser())
				.set(FS_MODEL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
				.where(FS_MODEL.ID.eq(rec.getValue(FS_MODEL.ID )))
				.execute()
			);
	}  
	
	public static void doRecord(AONContext ctx, Integer modelId, Integer accountEntryId) {
		ctx.getDslContext()
			.update(FS_MODEL)
			.set(FS_MODEL.ACCOUNT_ENTRY, accountEntryId)
			.set(FS_MODEL.MODIFICATION_USER,ctx.getUser())
			.set(FS_MODEL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(FS_MODEL.ID.eq(modelId))
			.execute()
			;
	}
	
}
