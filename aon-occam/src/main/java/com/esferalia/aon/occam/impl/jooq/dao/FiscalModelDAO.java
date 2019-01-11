package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModelDetail.FS_MODEL_DETAIL;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.FsModelRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class FiscalModelDAO {
	
	public static Record getModelRecord(final AONContext ctx,int id) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select()
			.from(FS_MODEL)
			.leftOuterJoin(FINANCE).on(FINANCE.ID.equal(FS_MODEL.FINANCE))
			.leftOuterJoin(REGISTRY).on(REGISTRY.ID.equal(FINANCE.REGISTRY))
			.leftOuterJoin(SCOPE).on(FINANCE.SCOPE.equal(SCOPE.ID))
			.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
			.where(FS_MODEL.ID.eq(id))
			.fetch()
			.stream()
			.findFirst()
			.orElse(null);
	}

	public static FiscalModel getFiscalModel(final AONContext ctx,int id) {
		ctx.checkRead();
		FiscalModel fm = getModelRecord(ctx, id)
			.map(record -> FiscalModelDAO.map(record));			
		if (fm != null) {
			getModelDetails(ctx,fm).forEach( detail -> fm.put( detail));	
		}
		return fm;
	}

	public static Stream<FiscalModelDetail> getModelDetails(AONContext ctx,FiscalModel fm) {
		ctx.checkRead();
		return ctx.getDslContext().selectFrom( FS_MODEL_DETAIL)
			.where(FS_MODEL_DETAIL.FS_MODEL.eq(fm.getId()))
			.fetch()
			.stream()
			.map(new FiscalModelDetailFiller() );
	}
	
	public static Stream<FiscalModel> getModels(AONContext ctx,int domain, FiscalModelType model) {
		ctx.checkRead();
		return getModelRecords(ctx, domain, model).map( record -> map(record) );
	}
	
	public static Stream<FiscalModel> getPreviousModels(AONContext ctx,FiscalModel fiscalModel, boolean desc) {
		ctx.checkRead();
		return getModelSelect(ctx, fiscalModel)
				.and(FS_MODEL.MODEL.eq(fiscalModel.getModel().getValue()))
				.and(FS_MODEL.YEAR.eq(fiscalModel.getYear()))
				.and(FS_MODEL.ADMINISTRATION.eq(fiscalModel.getAdministration().getValue()))
				.and(FS_MODEL.PERIOD.lessThan(fiscalModel.getPeriod().getValue()))
				.orderBy(desc?FS_MODEL.PERIOD.desc():FS_MODEL.PERIOD.asc())
				.fetch()
				.stream()
				.map( record -> map(record))
				.peek( model -> getModelDetails(ctx,model)
								.forEach( detail -> model.put( detail) )
					 );
	}

	public static Stream<FiscalModel> getPreviousModels(AONContext ctx,FiscalModel fiscalModel) {
		return getPreviousModels(ctx,fiscalModel,false);
	}
	
	public static Stream<FiscalModel> getEffectivePreviousModels(AONContext ctx,FiscalModel fiscalModel) {
		LinkedList<FiscalModel> effectivePreviousModels = new LinkedList<FiscalModel>();
		LinkedList<FiscalModel> previousModels = getPreviousModels(ctx, fiscalModel).collect(Collectors.toCollection(LinkedList::new));
		
		for ( FiscalModel fm : previousModels ) {
			boolean effective = fm.isComplementary() ||  
				(!fm.isComplementary() && !previousModels.stream()
				.anyMatch(fm2 -> fm2.isComplementary() 
					&& 	fm2.getYear() == fm.getYear()
					&& 	fm2.getPeriod().ordinal() == fm.getPeriod().ordinal()
				));
			if (effective) {
				effectivePreviousModels.add(fm);
			}
		}
		return effectivePreviousModels.stream();
	}
	
	protected static SelectConditionStep<Record> getModelSelect(AONContext ctx, FiscalModel fiscalModel) {
		return ctx.getDslContext()
			.select(FS_MODEL.fields())
			.select(FINANCE.fields())
			.select(REGISTRY.fields())
			.select(PAY_METHOD.fields())
			.from(FS_MODEL)
			.leftOuterJoin(FINANCE).on(FINANCE.ID.equal(FS_MODEL.FINANCE))
			.leftOuterJoin(REGISTRY).on(REGISTRY.ID.equal(FINANCE.REGISTRY))
			.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
			.where(FS_MODEL.DOMAIN.eq(fiscalModel.getDomain()));		
	}
	
	public static Stream<FiscalModel> getLastPeriodModels(AONContext ctx,FiscalModel fiscalModel) {
		ctx.checkRead();
		if (fiscalModel.getPeriod() == Period.M01 || fiscalModel.getPeriod() == Period.T1) {
			return Stream.empty();
		}
		return getModelSelect(ctx, fiscalModel)
				.and(FS_MODEL.MODEL.eq(fiscalModel.getModel().getValue()))
				.and(FS_MODEL.YEAR.eq(fiscalModel.getYear()))
				.and(FS_MODEL.ADMINISTRATION.eq(fiscalModel.getAdministration().getValue()))
				.and(FS_MODEL.PERIOD.eq((byte) ( (fiscalModel.getPeriod().getValue() - 1) )))
				.orderBy(FS_MODEL.PERIOD.desc(),FS_MODEL.COMPLEMENTARY.desc()
						,FS_MODEL.REPLACEMENT.desc(),FS_MODEL.ID.desc())
				.fetch()
				.stream()
				.map( record -> map(record))
				.peek( model -> getModelDetails(ctx,model)
								.forEach( detail -> model.put( detail) )
					 );
	}

	public static Stream<FiscalModel> getSamePeriodModels(AONContext ctx,FiscalModel fiscalModel) {
		ctx.checkRead();
		return getModelSelect(ctx, fiscalModel)
			.and(FS_MODEL.MODEL.eq(fiscalModel.getModel().getValue()))
			.and(FS_MODEL.YEAR.eq(fiscalModel.getYear()))
			.and(FS_MODEL.ADMINISTRATION.eq(fiscalModel.getAdministration().getValue()))
			.and(FS_MODEL.PERIOD.eq(fiscalModel.getPeriod().getValue()))
			.and(fiscalModel.getId()==null?DSL.trueCondition():FS_MODEL.ID.notEqual(fiscalModel.getId()))
			.fetch()
			.stream()
			.map( record -> map(record))
			.peek( model -> getModelDetails(ctx,model)
								.forEach( detail -> model.put( detail) )
				);
	}

	public static Stream<Record> getModelRecords(AONContext ctx,int domain, FiscalModelType model) {
		ctx.checkRead();
		return ctx.getDslContext()
				.select()
				.from(FS_MODEL)
				.leftOuterJoin(FINANCE).on(FINANCE.ID.equal(FS_MODEL.FINANCE))
				.leftOuterJoin(REGISTRY).on(REGISTRY.ID.equal(FINANCE.REGISTRY))
				.leftOuterJoin(SCOPE).on(FINANCE.SCOPE.equal(SCOPE.ID))
				.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
				.where(FS_MODEL.DOMAIN.eq(domain))
				.and(FS_MODEL.MODEL.eq(model.getValue()))
				.orderBy(FS_MODEL.YEAR.desc(),FS_MODEL.MODEL.asc(),FS_MODEL.PERIOD.desc(),FS_MODEL.COMPLEMENTARY.desc(),FS_MODEL.ID.desc())
				.fetch()
				.stream();
	}

	public static FiscalModel saveComments(AONContext ctx, FiscalModel fm) {
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
		} catch (Throwable t) {
			throw new AonCoreException(t.getMessage());
		}
	}

	public static FiscalModel save(AONContext ctx, FiscalModel fm) {
		try {
			ctx.checkWrite();
			FiscalModelValidation.validate(ctx,fm);
			if (fm.getId() == null) {
				fm = insert(ctx, fm);
			} else {
				fm = update(ctx, fm);
			}
			return getFiscalModel(ctx, fm.getId());
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Throwable t) {
			throw new AonCoreException(t.getMessage());
		}
	}

	private static FiscalModel insert(AONContext ctx, FiscalModel fm) {
		FsModelRecord record =  ctx.getDslContext()
			.insertInto(FS_MODEL)
				.set(FS_MODEL.DOMAIN, fm.getDomain())
				.set(FS_MODEL.YEAR, fm.getYear())
				.set(FS_MODEL.PERIOD, fm.getPeriod().getValue() )
				.set(FS_MODEL.ADMINISTRATION, fm.getAdministration().getValue() )
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
				.set(FS_MODEL.CREATION_USER,ctx.getUser())
				.set(FS_MODEL.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.returning(FS_MODEL.ID)
			.fetchOne();
		fm.setId(record.getId());
		insertDetails(ctx, fm);
		return fm;
	}
	
	private static FiscalModel update(AONContext ctx, FiscalModel fm) {
		ctx.getDslContext()
			.update(FS_MODEL)
				.set(FS_MODEL.DOMAIN, fm.getDomain())
				.set(FS_MODEL.YEAR, fm.getYear())
				.set(FS_MODEL.ADMINISTRATION, fm.getAdministration().getValue() )
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
				.set(FS_MODEL.MODIFICATION_USER,ctx.getUser())
				.set(FS_MODEL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(FS_MODEL.ID.equal(fm.getId()))
			.execute();
		deleteDetails(ctx, fm);
		insertDetails(ctx, fm);
		return fm;
	}

	private static void insertDetails(AONContext ctx, FiscalModel fm) {
		// Excepciones.
		for (FiscalModelDetail detail : fm.getMap().values() ) {
			ctx.getDslContext().insertInto(FS_MODEL_DETAIL)
				.set(FS_MODEL_DETAIL.DOMAIN, fm.getDomain())
				.set(FS_MODEL_DETAIL.FS_MODEL, fm.getId())
				.set(FS_MODEL_DETAIL.TYPE, detail.getType() )
				.set(FS_MODEL_DETAIL.DESCRIPTION, AonStringUtils.abbreviate(detail.getDescription(), FS_MODEL_DETAIL.DESCRIPTION.getDataType().length()))
				.set(FS_MODEL_DETAIL.ACU_AMOUNT, detail.getAccumulatedAmount()  )
				.set(FS_MODEL_DETAIL.DEC_AMOUNT, detail.getDeclaredAmount() )
				.set(FS_MODEL_DETAIL.RES_AMOUNT, detail.getResultAmount() )
				.set(FS_MODEL_DETAIL.ADJ_AMOUNT, detail.getAdjustAmount() )
				.set(FS_MODEL_DETAIL.AMOUNT, detail.getAmount() )
				.execute();
		}
	}
	
	public static void delete(AONContext ctx, FiscalModel fm) {
		ctx.checkWrite();
		deleteDetails(ctx, fm);
		ctx.getDslContext()
			.delete(FS_MODEL)
				.where(FS_MODEL.ID.equal(fm.getId()))
			.execute();
	}

	private static void deleteDetails(AONContext ctx, FiscalModel fm) {
		ctx.getDslContext()
			.delete(FS_MODEL_DETAIL)
			.where(FS_MODEL_DETAIL.FS_MODEL.equal(fm.getId()))
			.execute();
	}
	
	static void initializeFiscalModel(AONContext ctx, FiscalModel fm) {
		FiscalParameters params = AppParamDAO.getFiscalParameters(ctx);
		if (fm.getDomain() == 0) throw new AonCoreException("[INTERNO] No se ha indicado el dominio para la declaraci\u00F3n.");
		fm.setDocument(params.getDocument());
		fm.setName(params.getName());
		if (fm.getAdministration() == null) {
			fm.setAdministration(params.getAdministration(Administration.COMMON_TERRITORY));
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
		fm.setAdmonAeat(params.getAdministrationCode());
		fm.setStatus(FiscalStatus.PENDING);
		
		// ---------------------------
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
			fm.setZip("00000");
			if (enterprise.getZip() != null){
				fm.setZip(enterprise.getZip());
			}
			fm.setPhone(enterprise.getPhone() );
		}
		fm.setContactPerson( params.getContactPerson() );
		fm.setContactPhone(params.getContactPhone() );
		fm.setContactCellular( params.getContactCellular() );
		fm.setContactEmail( params.getContactMail() );
	}

	public static FiscalModel fullMap(AONContext ctx, Record record) {
		FiscalModel fm = map(record);
		getModelDetails(ctx, fm);
		return fm;
	}
	
	public static FiscalModel map(Record record) {
		FiscalModelType type = FiscalModelType.safeValueOf( record.getValue(FS_MODEL.MODEL));
		if (type == FiscalModelType.M111) {
			return map111(new Mod111(), record);
		} else if (type == FiscalModelType.M115) {
			return map115(new Mod115(), record);
		} else if (type == FiscalModelType.M123) {
			return map123(new Mod123(), record);
		} else if (type == FiscalModelType.M130) {
			return map130(new Mod130(), record);
		} else if (type == FiscalModelType.M131) {
			return map131(new Mod131(), record);
		} else if (type == FiscalModelType.M303) {
			return map303(new Mod303(), record);
		} else {
			return mapGeneric(new FiscalModel(), record);
		}
	}
	
	public static Mod303 map303(Mod303 mod303,Record record) {
		FiscalModelBuilder<Mod303> builder = new FiscalModelBuilder<Mod303>(new Mod303());
		return builder.create( new FiscalModelTemplate(record) );
	}
	public static Mod390HF map390HF(Mod390HF mod303,Record record) {
		FiscalModelBuilder<Mod390HF> builder = new FiscalModelBuilder<Mod390HF>(new Mod390HF());
		return builder.create( new FiscalModelTemplate(record) );
	}
	public static Mod111 map111(Mod111 mod111,Record record) {
		FiscalModelBuilder<Mod111> builder = new FiscalModelBuilder<Mod111>(new Mod111());
		return builder.create( new FiscalModelTemplate(record) );
	}
	public static Mod115 map115(Mod115 mod115,Record record) {
		FiscalModelBuilder<Mod115> builder = new FiscalModelBuilder<Mod115>(new Mod115());
		return builder.create( new FiscalModelTemplate(record) );
	}
	public static Mod123 map123(Mod123 mod123,Record record) {
		FiscalModelBuilder<Mod123> builder = new FiscalModelBuilder<Mod123>(new Mod123());
		return builder.create( new FiscalModelTemplate(record) );
	}
	public static Mod130 map130(Mod130 mod130,Record record) {
		FiscalModelBuilder<Mod130> builder = new FiscalModelBuilder<Mod130>(new Mod130());
		return builder.create( new FiscalModelTemplate(record) );
	}
	public static Mod131 map131(Mod131 mod130,Record record) {
		FiscalModelBuilder<Mod131> builder = new FiscalModelBuilder<Mod131>(new Mod131());
		return builder.create( new FiscalModelTemplate(record) );
	}
	public static Mod202 map202(Mod202 mod202,Record record) {
		FiscalModelBuilder<Mod202> builder = new FiscalModelBuilder<Mod202>(new Mod202());
		return builder.create( new FiscalModelTemplate(record) );
	}
	public static FiscalModel mapGeneric(FiscalModel model,Record record) {
		FiscalModelBuilder<FiscalModel> builder = new FiscalModelBuilder<FiscalModel>(new FiscalModel());
		return builder.create( new FiscalModelTemplate(record) );
	}
	
	public static class FiscalModelDetailFiller  implements Function<Record,FiscalModelDetail> {
		@Override
		public FiscalModelDetail apply(Record record) {
			return 	new FiscalModelDetail()
					.setId(record.getValue(FS_MODEL_DETAIL.ID))
					.setType(record.getValue(FS_MODEL_DETAIL.TYPE))
					.setDescription(record.getValue(FS_MODEL_DETAIL.DESCRIPTION))
					.setAccumulatedAmount(record.getValue(FS_MODEL_DETAIL.ACU_AMOUNT))
					.setDeclaredAmount(record.getValue(FS_MODEL_DETAIL.DEC_AMOUNT))
					.setResultAmount(record.getValue(FS_MODEL_DETAIL.RES_AMOUNT))
					.setAdjustAmount(record.getValue(FS_MODEL_DETAIL.ADJ_AMOUNT))
					.setAmount(record.getValue(FS_MODEL_DETAIL.AMOUNT));
		}
	}
	
	public static <T extends FiscalModel> T initializeForFinish(AONContext ctx,T fiscalModel) {
		fiscalModel.setDefaultDeclarationType();
		if (fiscalModel.getDeclarationType().mustCreateFinance()) {
			FiscalParameters params = AppParamDAO.getFiscalParameters(ctx);
			Integer creditorId = params.getAdmonCreditor();
			Creditor creditor = null;
			if ( creditorId != null ) {
				creditor = CreditorDAO
						.getBasicCreditors(ctx, p -> p.getIdProperty().eq(creditorId))
						.findFirst()
						.orElse(null);
			}
			String concept = "Mod." + FiscalModelUtils.getModelName(fiscalModel) 
				+ " - " + fiscalModel.getYear() 
				+ " / " + fiscalModel.getPeriod().getName( );

			concept = AonStringUtils.abbreviate(concept, 32);
			Finance finance = new Finance()
					.setPayment(true)
					.setRegistry(creditor!=null?creditor.getRegistry():null)
					.setRegistryDocument(creditor!=null?creditor.getRegistry().getDocument():null)
					.setRegistryDocumentCountry(creditor!=null?creditor.getRegistry().getDocumentCountry():null)
					.setRegistryDocumentType(creditor!=null?creditor.getRegistry().getDocumentType():null)
					.setRegistryName(creditor!=null?creditor.getRegistry().getName():null)
					.setConfidential(fiscalModel.isConfidential())
					.setAmount(fiscalModel.getResult())
					.setFinanceStatus(FinanceStatus.PENDING)
					.setDueDate(FiscalUtils.getPeriodEnd(fiscalModel))
					.setConcept(concept)
					;
			fiscalModel.setFinance(finance);
		}
		return fiscalModel;
	}
	
	public static <T extends FiscalModel> T finish(AONContext ctx,T fm) {
		fm.setStatus(FiscalStatus.FINISHED);
		if (fm.getDeclarationType() != null && fm.getDeclarationType().mustCreateFinance()) {
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
	
	private static class FiscalModelTemplate implements FiscalModelBuilder.Template {
		private Record record;
		
		private FiscalModelTemplate( Record record ) {
			this.record = record;
		}
		@Override
		public Integer getId() {
			return record.getValue(FS_MODEL.ID);
		}
		@Override
		public Integer getDomain() {
			return record.getValue(FS_MODEL.DOMAIN);
		}
		@Override
		public Integer getYear() {
			return record.getValue(FS_MODEL.YEAR);
		}
		@Override
		public Period getPeriod() {
			return com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Period.class, record.getValue(FS_MODEL.PERIOD));
		}
		@Override
		public Administration getAdministration() {
			return com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Administration.class,record.getValue(FS_MODEL.ADMINISTRATION));
		}
		@Override
		public FiscalStatus getStatus() {
			return com.esferalia.aon.watson.util.AonEnumUtils.enumValue(FiscalStatus.class,record.getValue(FS_MODEL.STATUS));
		}
		@Override
		public boolean isConfidential() {
			return AonEnumUtils.getBoolean( record.getValue(FS_MODEL.SECURITY_LEVEL));
		}
		@Override
		public boolean isComplementary() {
			return AonEnumUtils.getBoolean( record.getValue(FS_MODEL.COMPLEMENTARY));
		}
		@Override
		public boolean isReplacement() {
			return AonEnumUtils.getBoolean( record.getValue(FS_MODEL.REPLACEMENT));
		}
		@Override
		public boolean isWithoutActivity() {
			return AonEnumUtils.getBoolean( record.getValue(FS_MODEL.WITHOUTACTIVITY));
		}
		@Override
		public FiscalModelType getModel() {
			return FiscalModelType.safeValueOf( record.getValue(FS_MODEL.MODEL));
		}
		@Override
		public String getNumber() {
			return record.getValue(FS_MODEL.NUMBER);
		}
		@Override
		public String getReplacedNumber() {
			return record.getValue(FS_MODEL.REPLACED_NUMBER);
		}
		@Override
		public String getComments() {
			return record.getValue(FS_MODEL.COMMENTS);
		}
		@Override
		public Finance getFinance() {
			return record.getValue(FS_MODEL.FINANCE) == null?null:new FinanceDAO.FullFinanceFiller().apply(record);
		}
		@Override
		public String getDocument() {
			return record.getValue(FS_MODEL.DOCUMENT);
		}
		@Override
		public String getSurname() {
			return record.getValue(FS_MODEL.SURNAME);
		}
		@Override
		public String getName() {
			return record.getValue(FS_MODEL.NAME);
		}
		@Override
		public String getStreetInitial() {
			return record.getValue(FS_MODEL.STREET_INITIAL);
		}
		@Override
		public String getStreetName() {
			return record.getValue(FS_MODEL.STREET_NAME);
		}
		@Override
		public String getStreetNumber() {
			return record.getValue(FS_MODEL.STREET_NUMBER);
		}
		@Override
		public String getStreetStair() {
			return record.getValue(FS_MODEL.STREET_STAIR);
		}
		@Override
		public String getStreetFloor() {
			return record.getValue(FS_MODEL.STREET_FLOOR);
		}
		@Override
		public String getStreetDoor() {
			return record.getValue(FS_MODEL.STREET_DOOR);
		}
		@Override
		public String getPhone() {
			return record.getValue(FS_MODEL.PHONE);
		}
		@Override
		public String getTown() {
			return record.getValue(FS_MODEL.TOWN);
		}
		@Override
		public String getProvince() {
			return record.getValue(FS_MODEL.PROVINCE);
		}
		@Override
		public String getZip() {
			return record.getValue(FS_MODEL.ZIP);
		}
		@Override
		public String getAdmonAeat() {
			return record.getValue(FS_MODEL.ADMON_AEAT);
		}
		@Override
		public String getContactPerson() {
			return record.getValue(FS_MODEL.CONTACT_PERSON);
		}
		@Override
		public String getContactPhone() {
			return record.getValue(FS_MODEL.CONTACT_PHONE);
		}
		@Override
		public String getContactCellular() {
			return record.getValue(FS_MODEL.CONTACT_CELLULAR);
		}
		@Override
		public String getContactEmail() {
			return record.getValue(FS_MODEL.CONTACT_EMAIL);
		}
		@Override
		public String getCreationUser() {
			return record.getValue(FS_MODEL.CREATION_USER);
		}
		@Override
		public Date getCreationDate() {
			return record.getValue(FS_MODEL.CREATION_DATE);
		}
		@Override
		public String getModificationUser() {
			return record.getValue(FS_MODEL.MODIFICATION_USER);
		}
		@Override
		public Date getModificationDate() {
			return record.getValue(FS_MODEL.MODIFICATION_DATE);
		}
		
	}
}
