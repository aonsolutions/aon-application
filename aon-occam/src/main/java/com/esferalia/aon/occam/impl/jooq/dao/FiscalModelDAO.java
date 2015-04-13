package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModelDetail.FS_MODEL_DETAIL;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

import org.jooq.Record;

import com.esferalia.aon.jooq.tables.records.FsModelDetailRecord;
import com.esferalia.aon.jooq.tables.records.FsModelRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.CNAE;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.server.fiscal.calc.Aeat2013Mod131Calculator;
import com.esferalia.aon.occam.server.fiscal.calc.Aeat2015Mod202Calculator;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class FiscalModelDAO {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	public static Mod131 calculateMod131(AONContext ctx, Mod131 mod131) {
		return Aeat2013Mod131Calculator.calculate(ctx, mod131);
	}
	
	public static Mod131 saveMod131(AONContext ctx, Mod131 mod131) {
		FiscalModel fm = save(ctx, mod131);
		return getMod131(ctx, fm.getId());
	}

	public static Stream<Mod131> getMod131s(AONContext ctx, int domain) {
		return getModelRecords(ctx, domain)
			.map( record -> FiscalModelDAO.map(new Mod131(),record));
	}
	
	public static Mod131 getMod131(AONContext ctx,int id) {
		ctx.checkRead();
		FsModelRecord record = ctx.getDslContext().selectFrom(FS_MODEL)
			.where(FS_MODEL.ID.eq(id))
			.fetchOne();
		if (record != null) {
			Mod131 fm = new Mod131(); 
			populate(fm,record);
			fillModelDetails(ctx,fm);
			return fm;
		}
		return null;
	}
	
	public static FiscalModel getModel(AONContext ctx,int id) {
		ctx.checkRead();
		FsModelRecord record = ctx.getDslContext().selectFrom(FS_MODEL)
			.where(FS_MODEL.ID.eq(id))
			.fetchOne();
		if (record != null) {
			FiscalModel fm = new FiscalModel(); 
			populate(fm,record);
			fillModelDetails(ctx,fm);
			return fm;
		}
		return null;
	}

	private static void fillModelDetails(AONContext ctx,FiscalModel fm) {
		ctx.getDslContext().selectFrom( FS_MODEL_DETAIL)
			.where(FS_MODEL_DETAIL.FS_MODEL.eq(fm.getId()))
			.fetch()
			.forEach( record -> fm.put( populateDetail(record) ) );
	}

	private static FiscalModelDetail populateDetail(FsModelDetailRecord record) {
		return new FiscalModelDetail()
			.setId(record.getId())
			.setType(record.getType())
			.setDescription(record.getDescription())
			.setAccumulatedAmount(record.getAcuAmount())
			.setDeclaredAmount(record.getDecAmount())
			.setResultAmount(record.getResAmount())
			.setAdjustAmount(record.getAdjAmount())
			.setAmount(record.getAmount())
			;
	}
	private static Stream<FsModelRecord> getModelRecords(AONContext ctx,int domain) {
		ctx.checkRead();
		return ctx.getDslContext().selectFrom(FS_MODEL)
			.where(FS_MODEL.DOMAIN.eq(domain))
			.orderBy(FS_MODEL.YEAR.desc(),FS_MODEL.MODEL.asc(),FS_MODEL.PERIOD.desc())
			.fetch()
			.stream();
	}
	
	public static Stream<FiscalModel> getModels(AONContext ctx,int domain) {
		ctx.checkRead();
		return getModelRecords(ctx,domain)
			.map( record -> FiscalModelDAO.map(new FiscalModel(),record) );
	}

	private static FiscalModel populate(FiscalModel fm, Record record) {
		return fm.setId(record.getValue(FS_MODEL.ID))
			.setDomain(record.getValue(FS_MODEL.DOMAIN))
			.setYear(record.getValue(FS_MODEL.YEAR))
			.setPeriod(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(
					Period.class, record.getValue(FS_MODEL.PERIOD)))
			.setAdministration(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(
					Administration.class,record.getValue(FS_MODEL.ADMINISTRATION)))
			.setFinished( AonEnumUtils.getBoolean( record.getValue(FS_MODEL.STATUS)))
			.setConfidential(AonEnumUtils.getBoolean( record.getValue(FS_MODEL.SECURITY_LEVEL)))
			.setComplementary( AonEnumUtils.getBoolean( record.getValue(FS_MODEL.COMPLEMENTARY)))
			.setReplacement( AonEnumUtils.getBoolean( record.getValue(FS_MODEL.REPLACEMENT)))
			.setWithoutActivity( AonEnumUtils.getBoolean( record.getValue(FS_MODEL.WITHOUTACTIVITY)))
			.setModel(FiscalModelType.safeValueOf( record.getValue(FS_MODEL.MODEL)))
			.setNumber(record.getValue(FS_MODEL.NUMBER))
			.setReplacedNumber(record.getValue(FS_MODEL.REPLACED_NUMBER))
			.setComments(record.getValue(FS_MODEL.COMMENTS))
			.setFinance(record.getValue(FS_MODEL.FINANCE))
			.setDocument(record.getValue(FS_MODEL.DOCUMENT))
			.setSurname(record.getValue(FS_MODEL.SURNAME))
			.setName(record.getValue(FS_MODEL.NAME))
			.setStreetInitial(record.getValue(FS_MODEL.STREET_INITIAL))
			.setStreetName(record.getValue(FS_MODEL.STREET_NAME))
			.setStreetNumber(record.getValue(FS_MODEL.STREET_NUMBER))
			.setStreetStair(record.getValue(FS_MODEL.STREET_STAIR))
			.setStreetFloor(record.getValue(FS_MODEL.STREET_FLOOR))
			.setStreetDoor(record.getValue(FS_MODEL.STREET_DOOR))
			.setPhone(record.getValue(FS_MODEL.PHONE))
			.setTown(record.getValue(FS_MODEL.TOWN))
			.setProvince(record.getValue(FS_MODEL.PROVINCE))
			.setZip(record.getValue(FS_MODEL.ZIP))
			.setAdmonAeat(record.getValue(FS_MODEL.ADMON_AEAT))
			.setContactPerson(record.getValue(FS_MODEL.CONTACT_PERSON))
			.setContactPhone(record.getValue(FS_MODEL.CONTACT_PHONE))
			.setContactCellular(record.getValue(FS_MODEL.CONTACT_CELLULAR))
			.setContactEmail(record.getValue(FS_MODEL.CONTACT_EMAIL));
	}

	public static FiscalModel save(AONContext ctx, FiscalModel fm) {
		ctx.checkWrite();
		FiscalModelValidation.validate(ctx,fm);
		if (fm.getId() == null) {
			fm = insert(ctx, fm);
		} else {
			fm = update(ctx, fm);
		}
		return getModel(ctx, fm.getId());
	}

	private static FiscalModel insert(AONContext ctx, FiscalModel fm) {
		FsModelRecord record =  ctx.getDslContext()
			.insertInto(FS_MODEL)
				.set(FS_MODEL.DOMAIN, fm.getDomain())
				.set(FS_MODEL.YEAR, fm.getYear())
				.set(FS_MODEL.PERIOD, fm.getPeriod().getValue() )
				.set(FS_MODEL.ADMINISTRATION, fm.getAdministration().getValue() )
				.set(FS_MODEL.STATUS, AonEnumUtils.getByte( fm.isFinished() ) )
				.set(FS_MODEL.SECURITY_LEVEL,AonEnumUtils.getByte( fm.isConfidential() ))
				.set(FS_MODEL.COMPLEMENTARY,AonEnumUtils.getByte( fm.isComplementary() ))
				.set(FS_MODEL.REPLACEMENT,AonEnumUtils.getByte( fm.isReplacement() ))
				.set(FS_MODEL.WITHOUTACTIVITY,AonEnumUtils.getByte( fm.isWithoutActivity()  ))
				.set(FS_MODEL.MODEL, fm.getModel().getValue() )
				.set(FS_MODEL.NUMBER,fm.getNumber())
				.set(FS_MODEL.REPLACED_NUMBER,fm.getReplacedNumber())
				.set(FS_MODEL.COMMENTS,fm.getComments())
				.set(FS_MODEL.FINANCE, (Integer) null)
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
				.set(FS_MODEL.STATUS, AonEnumUtils.getByte( fm.isFinished() ) )
				.set(FS_MODEL.SECURITY_LEVEL,AonEnumUtils.getByte( fm.isConfidential() ))
				.set(FS_MODEL.COMPLEMENTARY,AonEnumUtils.getByte( fm.isComplementary() ))
				.set(FS_MODEL.REPLACEMENT,AonEnumUtils.getByte( fm.isReplacement() ))
				.set(FS_MODEL.WITHOUTACTIVITY,AonEnumUtils.getByte( fm.isWithoutActivity()  ))
				.set(FS_MODEL.MODEL, fm.getModel().getValue() )
				.set(FS_MODEL.NUMBER,fm.getNumber())
				.set(FS_MODEL.REPLACED_NUMBER,fm.getReplacedNumber())
				.set(FS_MODEL.COMMENTS,fm.getComments())
				.set(FS_MODEL.FINANCE, (Integer) null)
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
				.set(FS_MODEL_DETAIL.DESCRIPTION, detail.getDescription() )
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
	
	private static void initializeFiscalModel(AONContext ctx, FiscalModel fm) {
		FiscalParameters params = AppParamDAO.getFiscalParameters(ctx);
		fm.setDomain(ctx.getDomainId());
		fm.setDocument(params.getDocument());
		fm.setName(params.getName());
		fm.setModel(FiscalModelType.M202);
		fm.setAdministration(params.getAdministration(Administration.COMMON_TERRITORY));
		fm.setAdmonAeat(params.getAdministrationCode());
		
		// TODO ----------------------
		fm.setYear( AonDateUtils.getYear(new Date()) );
		fm.setPeriod( Period.T1 );
		fm.setReplacement(false);
		// ---------------------------
		Company company = CompanyDAO.getCompany(ctx,ctx.getDomainId());
		Enterprise enterprise = CompanyDAO.getEnterprise(ctx, company.getId() );
		fm.setDocument(enterprise.getDocument());
		String name = enterprise.getName();
		if (enterprise.getDocumentType() != DocumentType.CIF) {
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
		fm.setStreetInitial( enterprise.getStreetType().getAeatCode() );
		fm.setStreetName( AonStringUtils.left(enterprise.getAddress(),17) );
		fm.setStreetNumber( enterprise.getNumber() ); 
		fm.setTown( AonStringUtils.left(enterprise.getCity(),20));
		fm.setProvince(enterprise.getProvince()==null?"":enterprise.getProvince().toString());
		fm.setZip("00000");
		if (enterprise.getZip() != null){
			fm.setZip(enterprise.getZip());
		}
		fm.setPhone(enterprise.getPhone() );
		fm.setContactPerson( params.getContactPerson() );
		fm.setContactPhone(params.getContactPhone() );
		fm.setContactCellular( params.getContactCellular() );
		fm.setContactEmail( params.getContactMail() );
	}

	public static Stream<Mod202> getMod202s(AONContext ctx,int domain) {
			return ctx.getDslContext().selectFrom(FS_MODEL)
					.where(FS_MODEL.DOMAIN.eq(domain))
					.and(FS_MODEL.MODEL.eq( FiscalModelType.M202.getValue() ))
					.orderBy(FS_MODEL.YEAR.desc(),FS_MODEL.MODEL.asc(),FS_MODEL.PERIOD.desc())
					.fetch()
					.stream()
					.map( record -> FiscalModelDAO.map(new Mod202(),record));
		}
		
	public static Mod202 getMod202(AONContext ctx,int id) {
		ctx.checkRead();
		FsModelRecord record = ctx.getDslContext().selectFrom(FS_MODEL)
			.where(FS_MODEL.ID.eq(id))
			.fetchOne();
		if (record != null) {
			Mod202 mod202 = new Mod202(); 
			populate(mod202,record);
			fillModelDetails(ctx,mod202);
			onFillFiscalModel(mod202);
			return mod202;
		}
		return null;
	}
	
	public static Mod202 saveMod202(AONContext ctx, Mod202 mod202) {
		ensureDetail(mod202);
		FiscalModel fm = save(ctx, mod202);
		return getMod202(ctx, fm.getId());
	}
	
	public static <V extends FiscalModel> V map(V fm,Record record) {
		((FiscalModel) fm).setId(record.getValue(FS_MODEL.ID))
			.setDomain(record.getValue(FS_MODEL.DOMAIN))
			.setYear(record.getValue(FS_MODEL.YEAR))
			.setPeriod(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(
					Period.class, record.getValue(FS_MODEL.PERIOD)))
			.setAdministration(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(
					Administration.class,record.getValue(FS_MODEL.ADMINISTRATION)))
			.setFinished( AonEnumUtils.getBoolean( record.getValue(FS_MODEL.STATUS)))
			.setConfidential(AonEnumUtils.getBoolean( record.getValue(FS_MODEL.SECURITY_LEVEL)))
			.setComplementary( AonEnumUtils.getBoolean( record.getValue(FS_MODEL.COMPLEMENTARY)))
			.setReplacement( AonEnumUtils.getBoolean( record.getValue(FS_MODEL.REPLACEMENT)))
			.setWithoutActivity( AonEnumUtils.getBoolean( record.getValue(FS_MODEL.WITHOUTACTIVITY)))
			.setModel(FiscalModelType.safeValueOf( record.getValue(FS_MODEL.MODEL)))
			.setNumber(record.getValue(FS_MODEL.NUMBER))
			.setReplacedNumber(record.getValue(FS_MODEL.REPLACED_NUMBER))
			.setComments(record.getValue(FS_MODEL.COMMENTS))
			.setFinance(record.getValue(FS_MODEL.FINANCE))
			.setDocument(record.getValue(FS_MODEL.DOCUMENT))
			.setSurname(record.getValue(FS_MODEL.SURNAME))
			.setName(record.getValue(FS_MODEL.NAME))
			.setStreetInitial(record.getValue(FS_MODEL.STREET_INITIAL))
			.setStreetName(record.getValue(FS_MODEL.STREET_NAME))
			.setStreetNumber(record.getValue(FS_MODEL.STREET_NUMBER))
			.setStreetStair(record.getValue(FS_MODEL.STREET_STAIR))
			.setStreetFloor(record.getValue(FS_MODEL.STREET_FLOOR))
			.setStreetDoor(record.getValue(FS_MODEL.STREET_DOOR))
			.setPhone(record.getValue(FS_MODEL.PHONE))
			.setTown(record.getValue(FS_MODEL.TOWN))
			.setProvince(record.getValue(FS_MODEL.PROVINCE))
			.setZip(record.getValue(FS_MODEL.ZIP))
			.setAdmonAeat(record.getValue(FS_MODEL.ADMON_AEAT))
			.setContactPerson(record.getValue(FS_MODEL.CONTACT_PERSON))
			.setContactPhone(record.getValue(FS_MODEL.CONTACT_PHONE))
			.setContactCellular(record.getValue(FS_MODEL.CONTACT_CELLULAR))
			.setContactEmail(record.getValue(FS_MODEL.CONTACT_EMAIL));
		return fm;	
	}
	
	public static Mod202 calculateMod202(AONContext ctx, Mod202 mod202) {
		return Aeat2015Mod202Calculator.calculate(ctx, mod202);
	}

	public static Mod202 initializeMod202(AONContext ctx) {
		Mod202 mod202 = new Mod202();
		initializeFiscalModel(ctx, mod202);
		for (Mod202Key key : Mod202Key.values()) {
			mod202.ensureDetail(key);
		}
		return mod202;
	}
	
	private static void onFillFiscalModel(Mod202 mod202) {
		for (Mod202Key key : Mod202Key.values()) {
			if (key == Mod202Key.P02) {
				String c = mod202.getDescription(Mod202Key.P02);
				Date initialDate = null;
				if (AonStringUtils.isNotEmpty( c )) {
					try {
						initialDate = DATE_FORMAT.parse(c);
					} catch (ParseException e) {
					}
				}
				mod202.setInitialDate(initialDate);
			} else if (key == Mod202Key.P03) {
				String c = mod202.getDescription(Mod202Key.P03);
				if (AonStringUtils.isNotEmpty( c )) {
					CNAE cnae = CNAE.valueOfCode(c); 
					mod202.setCnae(cnae==null?null:cnae.getCode());
					mod202.setCnaeDescription(cnae==null?null:cnae.getDescription());
				} else {
					mod202.setCnae(null);
				}
			}
		}
	}
	private static void ensureDetail(Mod202 mod202) {
		for (Mod202Key key : Mod202Key.values()) {
			if (key == Mod202Key.P02) {
				if (mod202.getInitialDate() != null) {
					try {
						String date = DATE_FORMAT.format(mod202.getInitialDate());
						mod202.putDescription(Mod202Key.P02,date);
					} catch (NumberFormatException e) {
						mod202.putDescription(Mod202Key.P02,null);
					}
				} else {
					mod202.putDescription(Mod202Key.P02,null);
				}
			} else if (key == Mod202Key.P03) {
				if (AonStringUtils.isNotEmpty( mod202.getCnae())) {
					try {
						mod202.putDescription(Mod202Key.P03, mod202.getCnae());
					} catch (NumberFormatException e) {
						mod202.putDescription(Mod202Key.P03,null);
					}
				} else {
					mod202.putDescription(Mod202Key.P03,null);
				}
			}
		}
	}
}
