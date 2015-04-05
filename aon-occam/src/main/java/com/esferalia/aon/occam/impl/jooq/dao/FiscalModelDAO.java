package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModelDetail.FS_MODEL_DETAIL;

import java.util.LinkedList;

import org.jooq.Record;

import com.esferalia.aon.jooq.tables.records.FsModelDetailRecord;
import com.esferalia.aon.jooq.tables.records.FsModelRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.server.fiscal.calc.Aeat2013Mod131Calculator;
import com.esferalia.aon.watson.server.AonEnumUtils;


public class FiscalModelDAO {
	
	public static Mod131 calculate(AONContext ctx, Mod131 mod131) {
		return Aeat2013Mod131Calculator.calculate(ctx, mod131);
	}
	
	public static Mod131 save(AONContext ctx, Mod131 mod131) {
		// TODO
		return null;
	}

	public static Mod131 getMod131(AONContext ctx,int id) {
		FiscalModel fm = getModel(ctx, id);
		if (fm != null) {
			Mod131 mod131 = new Mod131();
			mod131.setId(fm.getId())
				.setDomain(fm.getDomain())
				.setYear(fm.getYear())
				.setFinance(fm.getFinance())
				.setModel(fm.getModel())
				.setPeriod(fm.getPeriod())
				.setAdministration(fm.getAdministration())
				.setFinished(fm.isFinished())
				.setConfidential(fm.isConfidential())
				.setComplementary(fm.isComplementary())
				.setReplacement(fm.isReplacement())
				.setWithoutActivity(fm.isWithoutActivity())
				.setNumber(fm.getNumber())
				.setReplacedNumber(fm.getReplacedNumber())
				.setComments(fm.getComments())
				.setDocument(fm.getDocument())
				.setSurname(fm.getSurname())
				.setName(fm.getName())
				.setStreetInitial(fm.getStreetInitial())
				.setStreetName(fm.getStreetName())
				.setStreetNumber(fm.getStreetNumber())
				.setStreetStair(fm.getStreetStair())
				.setStreetFloor(fm.getStreetFloor())
				.setStreetDoor(fm.getStreetDoor())
				.setPhone(fm.getPhone())
				.setTown(fm.getTown())
				.setProvince(fm.getProvince())
				.setZip(fm.getZip())
				.setAdmonAeat(fm.getAdmonAeat())
				.setContactPerson(fm.getContactPerson())
				.setContactPhone(fm.getContactPhone())
				.setContactCellular(fm.getContactCellular())
				.setContactEmail(fm.getContactEmail())
			;
			for (Mod131Key key : Mod131Key.values()) {
				if (fm.getMap().containsKey(key.getValue())) {
					key.fill(fm, mod131);
				}
			}
			return mod131;
		}
		return null;
	}
	
	public static FiscalModel getModel(AONContext ctx,int id) {
		ctx.checkRead();
		FsModelRecord record = ctx.getDslContext().selectFrom(FS_MODEL)
			.where(FS_MODEL.ID.eq(id))
			.fetchOne();
		if (record != null) {
			FiscalModel fm = populate(record);
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


	public static LinkedList<FiscalModel> getModels(AONContext ctx,int domain) {
		ctx.checkRead();
		LinkedList<FiscalModel> list = new LinkedList<FiscalModel>();
		ctx.getDslContext().select(
				 FS_MODEL.ID
				,FS_MODEL.DOMAIN
				,FS_MODEL.YEAR
				,FS_MODEL.PERIOD
				,FS_MODEL.ADMINISTRATION
				,FS_MODEL.STATUS
				,FS_MODEL.SECURITY_LEVEL
				,FS_MODEL.COMPLEMENTARY
				,FS_MODEL.REPLACEMENT
				,FS_MODEL.WITHOUTACTIVITY
				,FS_MODEL.MODEL
				,FS_MODEL.NUMBER
				,FS_MODEL.REPLACED_NUMBER
				,FS_MODEL.COMMENTS
				,FS_MODEL.FINANCE
				,FS_MODEL.DOCUMENT
				,FS_MODEL.SURNAME
				,FS_MODEL.NAME
				,FS_MODEL.STREET_INITIAL
				,FS_MODEL.STREET_NAME
				,FS_MODEL.STREET_NUMBER
				,FS_MODEL.STREET_STAIR
				,FS_MODEL.STREET_FLOOR
				,FS_MODEL.STREET_DOOR
				,FS_MODEL.PHONE
				,FS_MODEL.TOWN
				,FS_MODEL.PROVINCE
				,FS_MODEL.ZIP
				,FS_MODEL.ADMON_AEAT
				,FS_MODEL.CONTACT_PERSON
				,FS_MODEL.CONTACT_PHONE
				,FS_MODEL.CONTACT_CELLULAR
				,FS_MODEL.CONTACT_EMAIL
			)
			.from(FS_MODEL)
			.where(FS_MODEL.DOMAIN.eq(domain))
			.orderBy(FS_MODEL.YEAR.desc(),FS_MODEL.MODEL.asc(),FS_MODEL.PERIOD.desc())
			.fetch()
			.forEach( record -> list.add( populate(record) ) );
		return list;
	}

	private static FiscalModel populate(Record record) {
		return new FiscalModel()
			.setId(record.getValue(FS_MODEL.ID))
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
			.where(FS_MODEL.ID.equal(fm.getId()))
			.execute();
		deleteDetails(ctx, fm);
		insertDetails(ctx, fm);
		return fm;
	}

	private static void insertDetails(AONContext ctx, FiscalModel fm) {
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
}
