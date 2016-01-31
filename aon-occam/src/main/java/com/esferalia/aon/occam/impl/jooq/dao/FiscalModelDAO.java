package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModelDetail.FS_MODEL_DETAIL;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import com.esferalia.aon.jooq.tables.records.FsModelDetailRecord;
import com.esferalia.aon.jooq.tables.records.FsModelRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class FiscalModelDAO {
	
	static final DateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

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

	static void fillModelDetails(AONContext ctx,FiscalModel fm) {
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

	static FiscalModel populate(FiscalModel fm, Record record) {
		return fm.setId(record.getValue(FS_MODEL.ID))
			.setDomain(record.getValue(FS_MODEL.DOMAIN))
			.setYear(record.getValue(FS_MODEL.YEAR))
			.setPeriod(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(
					Period.class, record.getValue(FS_MODEL.PERIOD)))
			.setAdministration(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(
					Administration.class,record.getValue(FS_MODEL.ADMINISTRATION)))
			.setStatus( com.esferalia.aon.watson.util.AonEnumUtils.enumValue(
					FiscalStatus.class,record.getValue(FS_MODEL.STATUS)))
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
			.setContactEmail(record.getValue(FS_MODEL.CONTACT_EMAIL))
			.setCreationUser(record.getValue(FS_MODEL.CREATION_USER))
			.setCreationDate(record.getValue(FS_MODEL.CREATION_DATE))
			.setModificationUser(record.getValue(FS_MODEL.MODIFICATION_USER))
			.setModificationDate(record.getValue(FS_MODEL.MODIFICATION_DATE))
		;
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
			return getModel(ctx, fm.getId());
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
	
	static void initializeFiscalModel(AONContext ctx, FiscalModel fm) {
		FiscalParameters params = AppParamDAO.getFiscalParameters(ctx);
		fm.setDomain(ctx.getDomainId());
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
				month = 11;
			}
			fm.setYear(year);
			fm.setPeriod( Period.getQuarterlyPeriod(month));
		}
		fm.setAdmonAeat(params.getAdministrationCode());
		fm.setStatus(FiscalStatus.PENDING);
		
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

	public static <V extends FiscalModel> V map(V fm,Record record) {
		((FiscalModel) fm).setId(record.getValue(FS_MODEL.ID))
			.setDomain(record.getValue(FS_MODEL.DOMAIN))
			.setYear(record.getValue(FS_MODEL.YEAR))
			.setPeriod(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(
					Period.class, record.getValue(FS_MODEL.PERIOD)))
			.setAdministration(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(
					Administration.class,record.getValue(FS_MODEL.ADMINISTRATION)))
			.setStatus( com.esferalia.aon.watson.util.AonEnumUtils.enumValue(
					FiscalStatus.class,record.getValue(FS_MODEL.STATUS)))
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
			.setContactEmail(record.getValue(FS_MODEL.CONTACT_EMAIL))
			.setCreationUser(record.getValue(FS_MODEL.CREATION_USER))
			.setCreationDate(record.getValue(FS_MODEL.CREATION_DATE))
			.setModificationUser(record.getValue(FS_MODEL.MODIFICATION_USER))
			.setModificationDate(record.getValue(FS_MODEL.MODIFICATION_DATE))
			;
		return fm;	
	}
	
}
