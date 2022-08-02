package com.esferalia.aon.occam.mod200.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModel200.FS_MODEL200;
import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.mod200.api.model.Mod200;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyAdministrator;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class Mod200DAO {
	
	public static Stream<Mod200> getMod200s(AONContext ctx,int domain) {
		ctx.checkRead();
		return ctx.getDslContext()
				.select()
				.from(FS_MODEL200)
				.join(DOMAIN).on(FS_MODEL200.DOMAIN.equal(DOMAIN.ID))
//				.leftOuterJoin(FINANCE).on(FINANCE.ID.equal(FS_MODEL.FINANCE))
//				.leftOuterJoin(REGISTRY).on(REGISTRY.ID.equal(FINANCE.REGISTRY))
//				.leftOuterJoin(SCOPE).on(FINANCE.SCOPE.equal(SCOPE.ID))
//				.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
				.where(FS_MODEL200.DOMAIN.eq(domain))
				.orderBy(FS_MODEL200.YEAR.desc(),FS_MODEL200.ID.desc())
				.fetch()
				.stream()
				.map( new Mod200Filler() )
				;
	}
	
	public static class Mod200Filler implements Function<Record,Mod200> {

		@Override
		public Mod200 apply(Record record) {
			return new Mod200() 
				.setId(record.getValue(FS_MODEL200.ID))
				.setDomain(record.getValue(FS_MODEL200.DOMAIN))
				.setDomainName(record.getValue(DOMAIN.DESCRIPTION))
				.setYear(record.getValue(FS_MODEL200.YEAR))
				.setAdministration(Administration.safeValueOf(record.getValue(FS_MODEL200.ADMINISTRATION)))
				.setStatus(FiscalStatus.safeValueOf(record.getValue(FS_MODEL200.STATUS)))
				
				// TODO - Support
				.setFinance(null)
				
				.setComplementary( AonEnumUtils.getBoolean( record.getValue(FS_MODEL200.COMPLEMENTARY)))
				.setReplacement( false )
				.setNumber(record.getValue(FS_MODEL200.RECEIPT ))
				.setReplacedNumber(record.getValue(FS_MODEL200.COMPLEMENTARY_RECEIPT))
				.setComments(record.getValue(FS_MODEL200.COMMENTS ))
				.setDocument(record.getValue(FS_MODEL200.DOCUMENT ))
				.setName(record.getValue(FS_MODEL200.NAME))
				.setResultType(record.getValue(FS_MODEL200.RESULT_TYPE))
				.setAmount(record.getValue(FS_MODEL200.AMOUNT) == null? 0.0 : record.getValue(FS_MODEL200.AMOUNT) )
				.setCreationUser(record.getValue(FS_MODEL200.CREATION_USER))
				.setCreationDate(record.getValue(FS_MODEL200.CREATION_DATE))
				.setModificationUser(record.getValue(FS_MODEL200.MODIFICATION_USER))
				.setModificationDate(record.getValue(FS_MODEL200.MODIFICATION_DATE))
				
				.setFsModel(record.getValue(FS_MODEL200.FS_MODEL))
			;
		}
	}

	public static LinkedList<Mod200CompanyAdministrator> getDirStaff(AONContext ctx,int domain) {
		return ctx.getDslContext()
				.select(RDIR_STAFF.DOCUMENT,RDIR_STAFF.NAME,RDIR_STAFF.DIRECTOR,RDIR_STAFF.SHAREHOLDER,RDIR_STAFF.PERCENT_SHARE,RDIR_STAFF.NOMINAL_VALUE,RDIR_STAFF.REPRESENTATIVE)
				.from(COMPANY)
				.join(RDIR_STAFF).on( COMPANY.REGISTRY.equal(RDIR_STAFF.REGISTRY) )
				.where(COMPANY.DOMAIN.equal(domain))
				.fetch()
				.stream()
				.map( rec -> new Mod200CompanyAdministrator()
						.setDocument(rec.getValue(RDIR_STAFF.DOCUMENT) )
						.setName(rec.getValue(RDIR_STAFF.NAME) )
						.setShareholder( rec.getValue(RDIR_STAFF.SHAREHOLDER) == 1 )
						.setAdministrator( rec.getValue(RDIR_STAFF.DIRECTOR) == 1 )
						.setPercent(rec.getValue(RDIR_STAFF.PERCENT_SHARE) )
						.setNominalValue(rec.getValue(RDIR_STAFF.NOMINAL_VALUE)) 
						.setRepresentative( rec.getValue(RDIR_STAFF.REPRESENTATIVE) == 1 )
					)
				.collect(Collectors.toCollection(LinkedList::new ));
	}

	public static Mod200 saveComments(AONContext ctx, Mod200 mod200) {
		try {
			ctx.checkWrite();
			if (mod200.getId() != null) {
				ctx.getDslContext().update(FS_MODEL200)
					.set(FS_MODEL200.COMMENTS,mod200.getComments())
					.where(FS_MODEL200.ID.equal(mod200.getId()))
					.execute();
			}
			return mod200;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Exception t) {
			throw new AonCoreException(t.getMessage());
		}
	}
	
	// Mantenimiento de la fila en fs_model (se utilizará a partir del ejercicio 2022)
	
	public static Integer saveFsModel(AONContext ctx, Mod200 mod200) {

		// Comprobar si es necesario añadir o actualizar el registro en fs_model
		if (mod200.getFsModel() == null)
			return insertFsModel(ctx, mod200);
		else 
			return updateFsModel(ctx, mod200);
		
	}
	
	public static void deleteFsModel(AONContext ctx, Integer idFsModel) {
		
		if (idFsModel != null) {
			ctx.getDslContext()
				.delete(FS_MODEL)
				.where(FS_MODEL.ID.equal(idFsModel))
				.execute();
		}

	}
	
	private static Integer insertFsModel(AONContext ctx, Mod200 mod200) {
		
		Integer id = ctx.getDslContext()
			.insertInto(FS_MODEL)
				.set(FS_MODEL.DOMAIN, mod200.getDomain())
				.set(FS_MODEL.YEAR, mod200.getYear())
				.set(FS_MODEL.PERIOD, mod200.getPeriod().value())
				.set(FS_MODEL.ADMINISTRATION, mod200.getAdministration().value())
				.set(FS_MODEL.STATUS, AonEnumUtils.getByte(mod200.getStatus()))
//				.set(FS_MODEL.SECURITY_LEVEL,AonEnumUtils.getByte( mod200.isConfidential() ))
				.set(FS_MODEL.COMPLEMENTARY, AonEnumUtils.getByte( mod200.isComplementary() ))
//				.set(FS_MODEL.REPLACEMENT, AonEnumUtils.getByte( mod200.isReplacement() )) // Modelo 200 solo hay complementaria
//				.set(FS_MODEL.WITHOUTACTIVITY,AonEnumUtils.getByte( mod200.isWithoutActivity()  ))
				.set(FS_MODEL.MODEL, mod200.getModel().getValue() )
				.set(FS_MODEL.NUMBER, mod200.getNumber())
				.set(FS_MODEL.REPLACED_NUMBER, mod200.getReplacedNumber())
				.set(FS_MODEL.COMMENTS, mod200.getComments())
				.set(FS_MODEL.FINANCE, mod200.getFinance() == null?null:mod200.getFinance().getId())
				.set(FS_MODEL.DOCUMENT, mod200.getDocument() )
				.set(FS_MODEL.SURNAME, mod200.getSurname())
				.set(FS_MODEL.NAME, mod200.getName())
//				.set(FS_MODEL.STREET_INITIAL,mod200.getStreetInitial())
//				.set(FS_MODEL.STREET_NAME,mod200.getStreetName())
//				.set(FS_MODEL.STREET_NUMBER,mod200.getStreetNumber())
//				.set(FS_MODEL.STREET_STAIR,mod200.getStreetStair())
//				.set(FS_MODEL.STREET_FLOOR,mod200.getStreetFloor())
//				.set(FS_MODEL.STREET_DOOR,mod200.getStreetDoor())
//				.set(FS_MODEL.PHONE,mod200.getPhone())
//				.set(FS_MODEL.TOWN,mod200.getTown())
//				.set(FS_MODEL.PROVINCE,mod200.getProvince())
//				.set(FS_MODEL.ZIP,mod200.getZip())
//				.set(FS_MODEL.ADMON_AEAT,mod200.getAdmonAeat())
//				.set(FS_MODEL.CONTACT_PERSON,mod200.getContactPerson())
//				.set(FS_MODEL.CONTACT_PHONE,mod200.getContactPhone())
//				.set(FS_MODEL.CONTACT_CELLULAR,mod200.getContactCellular())
//				.set(FS_MODEL.CONTACT_EMAIL,mod200.getContactEmail())
				.set(FS_MODEL.RESULT, mod200.getDeclarationResult())
				.set(FS_MODEL.DECLARATION_TYPE, AonEnumUtils.getByte( mod200.getDeclarationResultType() ) )
//				.set(FS_MODEL.ACCOUNT_ENTRY, mod200.getAccountEntry())
				.set(FS_MODEL.CREATION_USER, mod200.getCreationUser())
				.set(FS_MODEL.CREATION_DATE, AonDateUtils.toTimestamp(mod200.getCreationDate()))
				.set(FS_MODEL.MODIFICATION_USER, mod200.getModificationUser())
				.set(FS_MODEL.MODIFICATION_DATE, AonDateUtils.toTimestamp(mod200.getModificationDate()))
			.returning(FS_MODEL.ID)
			.fetchOne()
			.getValue(FS_MODEL.ID);
		return id;
		
	}	
	
	private static Integer updateFsModel(AONContext ctx, Mod200 mod200) {
		
		ctx.getDslContext().update(FS_MODEL)
			.set(FS_MODEL.DOMAIN, mod200.getDomain())
			.set(FS_MODEL.YEAR, mod200.getYear())
			.set(FS_MODEL.ADMINISTRATION, mod200.getAdministration().value())
			.set(FS_MODEL.STATUS, AonEnumUtils.getByte(mod200.getStatus()))
//			.set(FS_MODEL.SECURITY_LEVEL,AonEnumUtils.getByte( mod200.isConfidential() ))
			.set(FS_MODEL.COMPLEMENTARY, AonEnumUtils.getByte( mod200.isComplementary()))
//			.set(FS_MODEL.REPLACEMENT,AonEnumUtils.getByte( mod200.isReplacement() ))  // Modelo 200 solo hay complementaria
//			.set(FS_MODEL.WITHOUTACTIVITY,AonEnumUtils.getByte( mod200.isWithoutActivity()  ))
			.set(FS_MODEL.MODEL, mod200.getModel().getValue())
			.set(FS_MODEL.NUMBER, mod200.getNumber())
			.set(FS_MODEL.REPLACED_NUMBER, mod200.getReplacedNumber())
			.set(FS_MODEL.COMMENTS, mod200.getComments())
			.set(FS_MODEL.FINANCE, mod200.getFinance() == null?null:mod200.getFinance().getId())
			.set(FS_MODEL.DOCUMENT, mod200.getDocument())
			.set(FS_MODEL.SURNAME, mod200.getSurname())
			.set(FS_MODEL.NAME, mod200.getName())
//			.set(FS_MODEL.STREET_INITIAL,mod200.getStreetInitial())
//			.set(FS_MODEL.STREET_NAME,mod200.getStreetName())
//			.set(FS_MODEL.STREET_NUMBER,mod200.getStreetNumber())
//			.set(FS_MODEL.STREET_STAIR,mod200.getStreetStair())
//			.set(FS_MODEL.STREET_FLOOR,mod200.getStreetFloor())
//			.set(FS_MODEL.STREET_DOOR,mod200.getStreetDoor())
//			.set(FS_MODEL.PHONE,mod200.getPhone())
//			.set(FS_MODEL.TOWN,mod200.getTown())
//			.set(FS_MODEL.PROVINCE,mod200.getProvince())
//			.set(FS_MODEL.ZIP,mod200.getZip())
//			.set(FS_MODEL.ADMON_AEAT,mod200.getAdmonAeat())
//			.set(FS_MODEL.CONTACT_PERSON,mod200.getContactPerson())
//			.set(FS_MODEL.CONTACT_PHONE,mod200.getContactPhone())
//			.set(FS_MODEL.CONTACT_CELLULAR,mod200.getContactCellular())
//			.set(FS_MODEL.CONTACT_EMAIL,mod200.getContactEmail())
			.set(FS_MODEL.RESULT, mod200.getDeclarationResult())
			.set(FS_MODEL.DECLARATION_TYPE,AonEnumUtils.getByte( mod200.getDeclarationResultType() ) )
//			.set(FS_MODEL.ACCOUNT_ENTRY,mod200.getAccountEntry())
			.set(FS_MODEL.MODIFICATION_USER, mod200.getModificationUser())
			.set(FS_MODEL.MODIFICATION_DATE, AonDateUtils.toTimestamp(mod200.getModificationDate()))
		.where(FS_MODEL.ID.equal(mod200.getFsModel()))
		.execute();
		return mod200.getFsModel();
		
	}
	
}
