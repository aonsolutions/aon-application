
package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod369;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModel369.FS_MODEL369;
import static com.esferalia.aon.jooq.tables.FsModel369Detail.FS_MODEL369_DETAIL;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.FsModel369Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod369;
import com.esferalia.aon.occam.api.model.fiscal.Mod369Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod369DetailCorrection;
import com.esferalia.aon.occam.api.model.fiscal.Mod369DetailOther;
import com.esferalia.aon.occam.api.model.fiscal.Mod369PayType;
import com.esferalia.aon.occam.api.model.fiscal.Mod369Regime;
import com.esferalia.aon.occam.api.model.fiscal.Mod369VatType;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class Mod369DAO {
	
	private static final byte ZERO_BYTE = 0;
	
	private Mod369DAO() {
	}

	public static Stream<Mod369> getHeaders(AONContext ctx, int domain) {
		return getHeaders(ctx, domain, null);
	}
	public static Stream<Mod369> getHeaders(AONContext ctx, int domain, Integer scope) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(FS_MODEL369.fields())
			.select(DOMAIN.DESCRIPTION)
			.from(FS_MODEL369)
			.join(DOMAIN).on(FS_MODEL369.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL369.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.and( scope == null ? DSL.trueCondition() : DOMAIN.SCOPE.equal(scope))
			.orderBy(FS_MODEL369.YEAR.desc()
					,FS_MODEL369.NAME.asc()
					,FS_MODEL369.REGIME.asc())
			.fetch()
			.stream()
			.map(new Mod369Filler());
	}

	public static LinkedList<Mod369> getByDomain(AONContext ctx, int domain) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(FS_MODEL369.fields())
			.select(DOMAIN.DESCRIPTION)
			.from(FS_MODEL369)
			.join(DOMAIN).on(FS_MODEL369.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL369.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.orderBy(FS_MODEL369.YEAR.desc()
					,FS_MODEL369.NAME.asc()
					,FS_MODEL369.REGIME.asc())
			.fetch()
			.stream()
			.map(new Mod369Filler())
			.map( mod369 -> mod369.setDetails3( getDetails(ctx, mod369.getId(), (byte) 0) ))
			.map( mod369 -> mod369.setDetails4( getDetails(ctx, mod369.getId(), (byte) 1) ))
			.map( mod369 -> mod369.setDetails5( getDetailsOther(ctx, mod369.getId(), (byte) 2) ))
			.map( mod369 -> mod369.setDetails6( getDetailsOther(ctx, mod369.getId(), (byte) 3) ))
			.map( mod369 -> mod369.setCorrections( getDetailsCorrections(ctx, mod369.getId(), (byte) 4) ))
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static Mod369 getById(AONContext ctx, int id) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(FS_MODEL369.fields())
			.select(DOMAIN.DESCRIPTION)
			.from(FS_MODEL369)
			.join(DOMAIN).on(FS_MODEL369.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL369.DOMAIN.equal(ctx.getDomainId()).or(DOMAIN.PARENT.equal(ctx.getDomainId())))
			.and(FS_MODEL369.ID.equal(id))
			.fetch()
			.stream()
			.map(new Mod369Filler())
			.peek( mod369 -> mod369.setDetails3( getDetails(ctx, mod369.getId(), (byte) 0) ))
			.peek( mod369 -> mod369.setDetails4( getDetails(ctx, mod369.getId(), (byte) 1) ))
			.peek( mod369 -> mod369.setDetails5( getDetailsOther(ctx, mod369.getId(), (byte) 2) ))
			.peek( mod369 -> mod369.setDetails6( getDetailsOther(ctx, mod369.getId(), (byte) 3) ))
			.peek( mod369 -> mod369.setCorrections( getDetailsCorrections(ctx, mod369.getId(), (byte) 4)))
			.findFirst()
			.orElse(null);
	}
	
	private static LinkedList<Mod369Detail> getDetails(AONContext ctx, int mod369, byte type) {
		ctx.checkRead();
		return ctx.getDslContext()
			.selectFrom(FS_MODEL369_DETAIL)
			.where(FS_MODEL369_DETAIL.FS_MODEL369.equal(mod369))
			.and(FS_MODEL369_DETAIL.DETAIL_TYPE.eq(type))
//			.orderBy(FS_MODEL369_DETAIL.DOCUMENT,FS_MODEL369_DETAIL.KEY,FS_MODEL369_DETAIL.SUBKEY )
			.fetch()
			.stream()
			.map( new Mod369DetailFiller() )
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	private static LinkedList<Mod369DetailOther> getDetailsOther(AONContext ctx, int mod369, byte type) {
		ctx.checkRead();
		return ctx.getDslContext()
			.selectFrom(FS_MODEL369_DETAIL)
			.where(FS_MODEL369_DETAIL.FS_MODEL369.equal(mod369))
			.and(FS_MODEL369_DETAIL.DETAIL_TYPE.eq(type))
//			.orderBy(FS_MODEL369_DETAIL.DOCUMENT,FS_MODEL369_DETAIL.KEY,FS_MODEL369_DETAIL.SUBKEY )
			.fetch()
			.stream()
			.map( new Mod369DetailOtherFiller() )
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	private static LinkedList<Mod369DetailCorrection> getDetailsCorrections(AONContext ctx, int mod369, byte type) {
		ctx.checkRead();
		return ctx.getDslContext()
			.selectFrom(FS_MODEL369_DETAIL)
			.where(FS_MODEL369_DETAIL.FS_MODEL369.equal(mod369))
			.and(FS_MODEL369_DETAIL.DETAIL_TYPE.eq(type))
//			.orderBy(FS_MODEL369_DETAIL.DOCUMENT,FS_MODEL369_DETAIL.KEY,FS_MODEL369_DETAIL.SUBKEY )
			.fetch()
			.stream()
			.map( new Mod369DetailCorrectionFiller() )
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Mod369 saveComments(AONContext ctx, Mod369 fm) {
		try {
			ctx.checkWrite();
			if (fm.getId() != null) {
				ctx.getDslContext().update(FS_MODEL369)
					.set(FS_MODEL369.COMMENTS,fm.getComments())
					.where(FS_MODEL369.ID.equal(fm.getId()))
					.execute();
			}
			return fm;
		} catch (Exception t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		}
	}

	public static Mod369 save(AONContext ctx, Mod369 mod369) {
		ctx.checkWrite();
		if (mod369.getId() == null) {
			insert(ctx, mod369);
		} else {
			update(ctx, mod369);
			for (Mod369Detail detail : mod369.getDetails3()) {
				saveDetail(ctx, mod369, detail, (byte) 0);
			}
			for (Mod369Detail detail : mod369.getDetails4()) {
				saveDetail(ctx, mod369, detail, (byte) 1);
			}
			for (Mod369DetailOther detail : mod369.getDetails5()) {
				saveDetailOther(ctx, mod369, detail, (byte) 2);
			}
			for (Mod369DetailOther detail : mod369.getDetails6()) {
				saveDetailOther(ctx, mod369, detail, (byte) 3);
			}
			for (Mod369DetailCorrection detail : mod369.getCorrections()) {
				saveDetailCorrection(ctx, mod369, detail, (byte) 4);
			}
		}
		return getById(ctx, mod369.getId());
	}

	private static Mod369 insert(AONContext ctx, Mod369 mod369) {
//		validate(ctx, mod369);
		mod369.setCreationUser(ctx.getUser());
		mod369.setCreationDate(new Timestamp(System.currentTimeMillis()));
		mod369.setFsModel(saveFsModel(ctx, mod369)); 
		FsModel369Record rec = ctx.getDslContext().insertInto(FS_MODEL369)
				.set(FS_MODEL369.DOMAIN, mod369.getDomain())
				.set(FS_MODEL369.YEAR, mod369.getYear())
				.set(FS_MODEL369.PERIOD, mod369.getPeriod().value())
				.set(FS_MODEL369.ADMINISTRATION, mod369.getAdministration().value())
				.set(FS_MODEL369.STATUS, ZERO_BYTE )
				.set(FS_MODEL369.SECURITY_LEVEL, AonEnumUtils.getByte(mod369.isConfidential()) )
				.set(FS_MODEL369.COUNTRY, Country.safeIso2(mod369.getCountry()))
				.set(FS_MODEL369.DOCUMENT, mod369.getDocument())
				.set(FS_MODEL369.NAME, mod369.getName())
				.set(FS_MODEL369.COMMENTS, mod369.getComments())
				.set(FS_MODEL369.CREATION_USER, mod369.getCreationUser())
				.set(FS_MODEL369.CREATION_DATE, AonDateUtils.toTimestamp(mod369.getCreationDate()))
				.set(FS_MODEL369.REGIME, AonEnumUtils.getByte(mod369.getRegime()))
				.set(FS_MODEL369.PAY_TYPE, AonEnumUtils.getByte(mod369.getPayType()))
				.set(FS_MODEL369.NRC, mod369.getNrc())
				.set(FS_MODEL369.AMOUNT_PAID, mod369.getAmountPaid())
				.set(FS_MODEL369.WITHOUT_ACTIVITY, AonEnumUtils.getByte(mod369.isWithoutActivity()))
				.set(FS_MODEL369.FROM_DATE, AonDateUtils.toTimestamp(mod369.getFromDate()))
				.set(FS_MODEL369.TO_DATE, AonDateUtils.toTimestamp(mod369.getToDate()))
				.set(FS_MODEL369.OPERATOR_NUMBER, mod369.getOperatorNumber())
				.set(FS_MODEL369.INTERMEDIARY, AonEnumUtils.getByte(mod369.isIntermediary()))
				.set(FS_MODEL369.INTERMEDIARY_NUMBER, mod369.getIntermediaryNumber())
				.set(FS_MODEL369.FS_MODEL, mod369.getFsModel())
			.returning(FS_MODEL369.ID)
			.fetchOne();
		mod369.setId(rec.getId());
		return mod369;
	}

	private static Mod369 update(AONContext ctx, Mod369 mod369) {
		mod369.setModificationUser(ctx.getUser());
		mod369.setModificationDate(new Timestamp(System.currentTimeMillis()));
		mod369.setFsModel(saveFsModel(ctx, mod369));
		ctx.getDslContext().update(FS_MODEL369)
			.set(FS_MODEL369.YEAR, mod369.getYear())
			.set(FS_MODEL369.PERIOD, mod369.getPeriod().value())
			.set(FS_MODEL369.ADMINISTRATION, mod369.getAdministration().value())
			.set(FS_MODEL369.STATUS, ZERO_BYTE )
			.set(FS_MODEL369.SECURITY_LEVEL, AonEnumUtils.getByte(mod369.isConfidential()) )
			.set(FS_MODEL369.COUNTRY, Country.safeIso2(mod369.getCountry()))
			.set(FS_MODEL369.DOCUMENT, mod369.getDocument())
			.set(FS_MODEL369.NAME, mod369.getName())
			.set(FS_MODEL369.COMMENTS, mod369.getComments())
			.set(FS_MODEL369.MODIFICATION_USER, mod369.getModificationUser())
			.set(FS_MODEL369.MODIFICATION_DATE, AonDateUtils.toTimestamp(mod369.getModificationDate()))
			.set(FS_MODEL369.REGIME, AonEnumUtils.getByte(mod369.getRegime()))
			.set(FS_MODEL369.PAY_TYPE, AonEnumUtils.getByte(mod369.getPayType()))
			.set(FS_MODEL369.NRC, mod369.getNrc())
			.set(FS_MODEL369.AMOUNT_PAID, mod369.getAmountPaid())
			.set(FS_MODEL369.WITHOUT_ACTIVITY, AonEnumUtils.getByte(mod369.isWithoutActivity()))
			.set(FS_MODEL369.FROM_DATE, AonDateUtils.toTimestamp(mod369.getFromDate()))
			.set(FS_MODEL369.TO_DATE, AonDateUtils.toTimestamp(mod369.getToDate()))
			.set(FS_MODEL369.OPERATOR_NUMBER, mod369.getOperatorNumber())
			.set(FS_MODEL369.INTERMEDIARY, AonEnumUtils.getByte(mod369.isIntermediary()))
			.set(FS_MODEL369.INTERMEDIARY_NUMBER, mod369.getIntermediaryNumber())
			.set(FS_MODEL369.FS_MODEL, mod369.getFsModel())
			.where(FS_MODEL369.ID.equal(mod369.getId()))
			.execute();
		return mod369;
	}

	private static void saveDetail(AONContext ctx, Mod369 mod369, Mod369Detail detail, byte detailType) {
		ctx.checkWrite();
		if (detail.getId() == null) {
			if (!detail.isDeleted()) {
				detail.setDomain(mod369.getDomain());
				detail.setMod369(mod369.getId());
				insertDetail(ctx, detail, detailType);
			}
		} else {
			if (detail.isDeleted()) {
				deleteDetail(ctx, detail.getId());
			} else {
				updateDetail(ctx, detail);
			}
		}
	}

	private static void insertDetail(AONContext ctx, Mod369Detail detail, byte detailType) {
		ctx.getDslContext().insertInto(FS_MODEL369_DETAIL)
			.set(FS_MODEL369_DETAIL.DOMAIN,detail.getDomain())
			.set(FS_MODEL369_DETAIL.FS_MODEL369,detail.getMod369())
			.set(FS_MODEL369_DETAIL.DETAIL_TYPE, detailType )
			.set(FS_MODEL369_DETAIL.COUNTRY, Country.safeIso2(detail.getCountry()))
			.set(FS_MODEL369_DETAIL.VAT_PERCENT, detail.getVatPercent())
			.set(FS_MODEL369_DETAIL.VAT_TYPE, AonEnumUtils.getByte(detail.getVatType()))
			.set(FS_MODEL369_DETAIL.BASE, detail.getBase())
			.set(FS_MODEL369_DETAIL.QUOTA, detail.getQuota())			
			.execute();
	}

	private static void updateDetail(AONContext ctx, Mod369Detail detail) {
		ctx.getDslContext().update(FS_MODEL369_DETAIL)
			.set(FS_MODEL369_DETAIL.COUNTRY, Country.safeIso2(detail.getCountry()))
			.set(FS_MODEL369_DETAIL.VAT_PERCENT, detail.getVatPercent())
			.set(FS_MODEL369_DETAIL.VAT_TYPE, AonEnumUtils.getByte(detail.getVatType()))
			.set(FS_MODEL369_DETAIL.BASE, detail.getBase())
			.set(FS_MODEL369_DETAIL.QUOTA, detail.getQuota())			
			.where(FS_MODEL369_DETAIL.ID.equal(detail.getId()))
			.execute();
	}
	
//	private static void deleteDetail(AONContext ctx, Mod369Detail detail) {
//		ctx.getDslContext().delete(FS_MODEL369_DETAIL)
//			.where(FS_MODEL369_DETAIL.ID.equal(detail.getId()))
//			.execute();
//	}
	
	private static void deleteDetail(AONContext ctx, int id) {
		ctx.getDslContext().delete(FS_MODEL369_DETAIL)
			.where(FS_MODEL369_DETAIL.ID.equal(id))
			.execute();
	}
	
	private static void saveDetailOther(AONContext ctx, Mod369 mod369, Mod369DetailOther detail, byte detailType) {
		ctx.checkWrite();
		if (detail.getId() == null) {
			if (!detail.isDeleted()) {
				detail.setDomain(mod369.getDomain());
				detail.setMod369(mod369.getId());
				insertDetailOther(ctx, detail, detailType);
			}
		} else {
			if (detail.isDeleted()) {
				deleteDetail(ctx, detail.getId());
			} else {
				updateDetailOther(ctx, detail);
			}
		}
	}

	private static void insertDetailOther(AONContext ctx, Mod369DetailOther detail, byte detailType) {
		ctx.getDslContext().insertInto(FS_MODEL369_DETAIL)
			.set(FS_MODEL369_DETAIL.DOMAIN,detail.getDomain())
			.set(FS_MODEL369_DETAIL.FS_MODEL369,detail.getMod369())
			.set(FS_MODEL369_DETAIL.DETAIL_TYPE, detailType)
			.set(FS_MODEL369_DETAIL.COUNTRY, Country.safeIso2(detail.getCountry()))
			.set(FS_MODEL369_DETAIL.VAT_PERCENT, detail.getVatPercent())
			.set(FS_MODEL369_DETAIL.VAT_TYPE, AonEnumUtils.getByte(detail.getVatType()))
			.set(FS_MODEL369_DETAIL.BASE, detail.getBase())
			.set(FS_MODEL369_DETAIL.QUOTA, detail.getQuota())
			.set(FS_MODEL369_DETAIL.OTHER_COUNTRY, Country.safeIso2(detail.getOtherCountry()))
			.set(FS_MODEL369_DETAIL.OTHER_DOCUMENT, detail.getOtherDocument())
			.execute();
	}

	private static void updateDetailOther(AONContext ctx, Mod369DetailOther detail) {
		ctx.getDslContext().update(FS_MODEL369_DETAIL)
			.set(FS_MODEL369_DETAIL.COUNTRY, Country.safeIso2(detail.getCountry()))
			.set(FS_MODEL369_DETAIL.VAT_PERCENT, detail.getVatPercent())
			.set(FS_MODEL369_DETAIL.VAT_TYPE, AonEnumUtils.getByte(detail.getVatType()))
			.set(FS_MODEL369_DETAIL.BASE, detail.getBase())
			.set(FS_MODEL369_DETAIL.QUOTA, detail.getQuota())
			.set(FS_MODEL369_DETAIL.OTHER_COUNTRY, Country.safeIso2(detail.getOtherCountry()))
			.set(FS_MODEL369_DETAIL.OTHER_DOCUMENT, detail.getOtherDocument())
			.where(FS_MODEL369_DETAIL.ID.equal(detail.getId()))
			.execute();
	}
	
	private static void saveDetailCorrection(AONContext ctx, Mod369 mod369, Mod369DetailCorrection detail, byte detailType) {
		ctx.checkWrite();
		if (detail.getId() == null) {
			if (!detail.isDeleted()) {
				detail.setDomain(mod369.getDomain());
				detail.setMod369(mod369.getId());
				insertDetailCorrection(ctx, detail, detailType);
			}
		} else {
			if (detail.isDeleted()) {
				deleteDetail(ctx, detail.getId());
			} else {
				updateDetailCorrection(ctx, detail);
			}
		}
	}

	private static void insertDetailCorrection(AONContext ctx, Mod369DetailCorrection detail, byte detailType) {
		ctx.getDslContext().insertInto(FS_MODEL369_DETAIL)
			.set(FS_MODEL369_DETAIL.DOMAIN,detail.getDomain())
			.set(FS_MODEL369_DETAIL.FS_MODEL369,detail.getMod369())
			.set(FS_MODEL369_DETAIL.DETAIL_TYPE, detailType)
			.set(FS_MODEL369_DETAIL.COUNTRY, Country.safeIso2(detail.getCountry()))
			.set(FS_MODEL369_DETAIL.CORRECTION_YEAR, detail.getYear())
			.set(FS_MODEL369_DETAIL.CORRECTION_PERIOD, AonEnumUtils.getByte(detail.getPeriod()))			
			.set(FS_MODEL369_DETAIL.QUOTA, detail.getQuota())
			.execute();
	}

	private static void updateDetailCorrection(AONContext ctx, Mod369DetailCorrection detail) {
		ctx.getDslContext().update(FS_MODEL369_DETAIL)
			.set(FS_MODEL369_DETAIL.COUNTRY, Country.safeIso2(detail.getCountry()))
			.set(FS_MODEL369_DETAIL.CORRECTION_YEAR, detail.getYear())
			.set(FS_MODEL369_DETAIL.CORRECTION_PERIOD, AonEnumUtils.getByte(detail.getPeriod()))			
			.set(FS_MODEL369_DETAIL.QUOTA, detail.getQuota())
			.where(FS_MODEL369_DETAIL.ID.equal(detail.getId()))
			.execute();
	}
	
	// FALTA - RESTO DE DETALLES (CORRECCIONES, ...) 

//	private static void validate(AONContext ctx, Mod369 mod369) {
//		if (mod369.isReplacement() || mod369.isComplementary()) {
//			// Se comprueba que exista la declaración sustituida.
//			if (!ctx.getDslContext().selectOne()
//					.from(FS_MODEL369)
//					.where(FS_MODEL369.YEAR.equal(mod369.getYear())
//					.and(FS_MODEL369.ADMINISTRATION.equal(mod369.getAdministration().value()))
//					.and(FS_MODEL369.ENTERPRISE.equal(mod369.getEnterprise()))
//					)
//					.fetch()
//					.stream()
//					.findFirst()
//					.isPresent()) 
//				throw new AonCoreException(
//						AonError.FISCAL_NO_REPLACED_DECLARATION.getMessage());
//		} else {
//			// Se comprueba que no exista ya una declaración.
//			if (ctx.getDslContext().selectOne()
//				.from(FS_MODEL369)
//				.where(FS_MODEL369.YEAR.equal(mod369.getYear())
//				.and(FS_MODEL369.ADMINISTRATION.equal(mod369.getAdministration().value()))
//				.and(FS_MODEL369.ENTERPRISE.equal(mod369.getEnterprise()))
//				.and(FS_MODEL369.REPLACEMENT.equal(ZERO_BYTE))
//				.and(FS_MODEL369.COMPLEMENTARY.equal(ZERO_BYTE)))				
//				.fetch()
//				.stream()
//				.findFirst()
//				.isPresent()) 
//				throw new AonCoreException(
//						AonError.FISCAL_DECLARATION_ALREADY_EXISTS.getMessage());
//		}
//	}

	public static void delete(AONContext ctx, Mod369 mod369) {
		
		ctx.checkWrite();
		
		// Borrar filas en fs_model369_detail
		deleteDetails(ctx, mod369);
		
		// Borrar filas vinculadas al modelo en alcatraz
		AlcatrazDAO.deleteFiscalModel(ctx, getFiscalModel(mod369)); 
		
		// Borrar fila en fs_model369
		ctx.getDslContext().delete(FS_MODEL369)
			.where(FS_MODEL369.ID.equal(mod369.getId()))
			.execute();
		
		// Borrar fila en fs_model
		deleteFsModel(ctx, mod369.getFsModel());  

	}

	private static void deleteDetails(AONContext ctx, Mod369 mod369) {
		ctx.getDslContext().delete(FS_MODEL369_DETAIL)
			.where(FS_MODEL369_DETAIL.FS_MODEL369.equal(mod369.getId()))
			.execute();
	}

	private static class Mod369Filler implements Function<Record, Mod369> {

		@Override
		public Mod369 apply(Record rec) {
			return new Mod369()
				.setId(rec.getValue(FS_MODEL369.ID))
				.setDomain(rec.getValue(FS_MODEL369.DOMAIN))
				.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
				.setYear(rec.getValue(FS_MODEL369.YEAR))
				.setPeriod(Period.safeValueOf(rec.getValue(FS_MODEL369.PERIOD)))
				.setConfidential(AonEnumUtils.getBoolean(rec.getValue(FS_MODEL369.SECURITY_LEVEL)))
				.setAdministration( com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Administration.class,rec.getValue(FS_MODEL369.ADMINISTRATION)))
				.setStatus(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(FiscalStatus.class,rec.getValue(FS_MODEL369.STATUS)))
				.setComments(rec.getValue(FS_MODEL369.COMMENTS))
				.setCountry(Country.safeValueOf(rec.getValue(FS_MODEL369.COUNTRY)))
				.setDocument(rec.getValue(FS_MODEL369.DOCUMENT))
				.setName(rec.getValue(FS_MODEL369.NAME))
				.setCreationUser(rec.getValue(FS_MODEL369.CREATION_USER))
				.setCreationDate(rec.getValue(FS_MODEL369.CREATION_DATE))
				.setModificationUser(rec.getValue(FS_MODEL369.MODIFICATION_USER))
				.setModificationDate(rec.getValue(FS_MODEL369.MODIFICATION_DATE))
			    .setFsModel(rec.getValue(FS_MODEL369.FS_MODEL))
			    .setRegime(Mod369Regime.safeValueOf(rec.getValue(FS_MODEL369.REGIME)))
			    .setPayType(Mod369PayType.safeValueOf(rec.getValue(FS_MODEL369.PAY_TYPE)))
			    .setNrc(rec.getValue(FS_MODEL369.NRC))
			    .setAmountPaid(rec.getValue(FS_MODEL369.AMOUNT_PAID))
			    .setOperatorNumber(rec.getValue(FS_MODEL369.OPERATOR_NUMBER))
			    .setWithoutActivity(AonEnumUtils.getBoolean(rec.getValue(FS_MODEL369.WITHOUT_ACTIVITY)))
			    .setIntermediary(AonEnumUtils.getBoolean(rec.getValue(FS_MODEL369.INTERMEDIARY)))
			    .setIntermediaryNumber(rec.getValue(FS_MODEL369.INTERMEDIARY_NUMBER))
			    .setFromDate(rec.getValue(FS_MODEL369.FROM_DATE))
			    .setToDate(rec.getValue(FS_MODEL369.TO_DATE))
			    ;
		}
	}
	
	// FALTA - LOS FILLER DE LAS LINEAS NO CARGAN EL ID, DOMAIN, FS_MODEL369 NI DETAILTYPE ??
			
	private static class Mod369DetailFiller implements Function<Record, Mod369Detail> {

		@Override
		public Mod369Detail apply(Record rec) {
			return new Mod369Detail()
				.setId(rec.getValue(FS_MODEL369_DETAIL.ID))
				.setCountry(Country.safeValueOf(rec.getValue(FS_MODEL369_DETAIL.COUNTRY)))
				.setVatPercent(rec.getValue(FS_MODEL369_DETAIL.VAT_PERCENT))
				.setVatType(Mod369VatType.safeValueOf(rec.getValue(FS_MODEL369_DETAIL.VAT_TYPE)))
				.setBase(rec.getValue(FS_MODEL369_DETAIL.BASE))
				.setQuota(rec.getValue(FS_MODEL369_DETAIL.QUOTA))
				;
		}
		
	}
	
	private static class Mod369DetailOtherFiller implements Function<Record, Mod369DetailOther> {

		@Override
		public Mod369DetailOther apply(Record rec) {
			return new Mod369DetailOther()
				.setId(rec.getValue(FS_MODEL369_DETAIL.ID))
				.setCountry(Country.safeValueOf(rec.getValue(FS_MODEL369_DETAIL.COUNTRY)))
				.setVatPercent(rec.getValue(FS_MODEL369_DETAIL.VAT_PERCENT))
				.setVatType(Mod369VatType.safeValueOf(rec.getValue(FS_MODEL369_DETAIL.VAT_TYPE)))
				.setBase(rec.getValue(FS_MODEL369_DETAIL.BASE))
				.setQuota(rec.getValue(FS_MODEL369_DETAIL.QUOTA))
				.setOtherCountry(Country.safeValueOf(rec.getValue(FS_MODEL369_DETAIL.OTHER_COUNTRY)))
				.setOtherDocument(rec.getValue(FS_MODEL369_DETAIL.OTHER_DOCUMENT))
				;
		}
		
	}
	
	private static class Mod369DetailCorrectionFiller implements Function<Record, Mod369DetailCorrection> {

		@Override
		public Mod369DetailCorrection apply(Record rec) {
			return new Mod369DetailCorrection()
				.setId(rec.getValue(FS_MODEL369_DETAIL.ID))
				.setCountry(Country.safeValueOf(rec.getValue(FS_MODEL369_DETAIL.COUNTRY)))
				.setYear(rec.getValue(FS_MODEL369_DETAIL.CORRECTION_YEAR))
				.setPeriod(Period.safeValueOf(rec.getValue(FS_MODEL369_DETAIL.CORRECTION_PERIOD)))
				.setQuota(rec.getValue(FS_MODEL369_DETAIL.QUOTA))
				;
		}
		
	}
	

	
//	private static class Mod369PartnerFiller implements Function<Record, Mod369Partner> {
//
//		@Override
//		public Mod369Partner apply(Record rec) {
//			return new Mod369Partner()
//				.setId(rec.getValue(FS_MODEL369_DETAIL.ID))
//				.setDocument(rec.getValue(FS_MODEL369_DETAIL.DOCUMENT))
//				.setRepresentativeDocument(rec.getValue(FS_MODEL369_DETAIL.REPRESENTATIVE_DOCUMENT))
//				.setName(rec.getValue(FS_MODEL369_DETAIL.NAME))
//				.setProvince(rec.getValue(FS_MODEL369_DETAIL.PROVINCE))
//				.setCountry(rec.getValue(FS_MODEL369_DETAIL.COUNTRY))
//				.setPartType(rec.getValue(FS_MODEL369_DETAIL.PART_TYPE))
//				.setMemberEndOfYear(AonEnumUtils.getBoolean( rec.getValue(FS_MODEL369_DETAIL.MEMBER_END_OF_YEAR)))
//				.setMemberDays(rec.getValue(FS_MODEL369_DETAIL.MEMBER_DAYS))
//				.setPartPercent(rec.getValue(FS_MODEL369_DETAIL.PART_PERCENT))
//				.setKey(rec.getValue(FS_MODEL369_DETAIL.KEY))
//				.setSubKey(rec.getValue(FS_MODEL369_DETAIL.SUBKEY))
//				.setAmount(rec.getValue(FS_MODEL369_DETAIL.AMOUNT))
//				.setReduction(rec.getValue(FS_MODEL369_DETAIL.REDUCTION))
//				.setAddress(rec.getValue(FS_MODEL369_DETAIL.ADDRESS))
//				.setExpenses(AonNumberUtils.zeroIfNull(rec.getValue(FS_MODEL369_DETAIL.EXPENSES)))
//				.setNature(rec.getValue(FS_MODEL369_DETAIL.NATURE))
//				.setLocation(rec.getValue(FS_MODEL369_DETAIL.LOCATION))
//				.setCadasdralReference(rec.getValue(FS_MODEL369_DETAIL.CADASDRAL_REFERENCE))
//				.setDeclaredKey(rec.getValue(FS_MODEL369_DETAIL.DECLARED_KEY))
//				.setAssetPercent(rec.getValue(FS_MODEL369_DETAIL.ASSET_PERCENT))
//				.setAssetDays(rec.getValue(FS_MODEL369_DETAIL.ASSET_DAYS))
//				.setRendNetoPrevio(rec.getValue(FS_MODEL369_DETAIL.REND_NETO_PREVIO))
//				.setRendNetoMinorado(rec.getValue(FS_MODEL369_DETAIL.REND_NETO_MINORA))
//				;
//			
//		}
//	}

	public static Mod369 initialize(AONContext ctx, int year, Period period) {

		if (year == 0) {
			// Ponemos por defecto el año, segun la fecha actual, si estamos en enero ponemos
			// el año anterior (se supone que queremos hacer el del ultimo periodo del año anterior)
			// en caso contrario ponemos el año actual
			Date today = new Date();
			year = AonDateUtils.getYear(today);		
			if (AonDateUtils.getMonth(today) == 0) {
				year = year - 1;			
			}
		}

		Mod369 mod369 = new Mod369();
		AonConfiguration conf = ConfigurationDAO.getConfiguration(ctx);
		mod369.setDomain(ctx.getDomainId());
		mod369.setCountry(conf.getCompany().getDocumentCountry());
		mod369.setDocument(conf.getCompany().getDocument());
		mod369.setName(conf.getCompany().getName());
		mod369.setYear(year);
		mod369.setPeriod(period);
		mod369.setRegime(Mod369Regime.UNION); 
		mod369.setStatus(FiscalStatus.PENDING);
		mod369.setAdministration(conf.fiscal().getAdministration(Administration.COMMON_TERRITORY));
		mod369.setPayType(Mod369PayType.TOTAL);
		mod369.setDetails3(new LinkedList<>());
		mod369.setDetails4(new LinkedList<>());
		mod369.setDetails5(new LinkedList<>());
		mod369.setDetails6(new LinkedList<>());
		mod369.setCorrections(new LinkedList<>());
		return mod369;
	}
	
	public static Mod369 changeStatusMod369(AONContext ctx, Mod369 mod369, FiscalStatus newStatus) {
		try {
			ctx.checkWrite();
			if (mod369.getId() != null) {
				mod369.setStatus(newStatus);
				ctx.getDslContext().update(FS_MODEL369)
					.set(FS_MODEL369.STATUS,AonEnumUtils.getByte( mod369.getStatus()))
					.where(FS_MODEL369.ID.equal(mod369.getId()))
					.execute();
			}
			return mod369;
		} catch (Exception t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		}
	}
	
	public static Mod369 duplicate(AONContext ctx, Mod369 mod369) {
		int id = mod369.getId();
		mod369.setId(null);
		mod369 = save(ctx, mod369);
		
		Mod369 original = getById(ctx, id);
		for (Mod369Detail detail : original.getDetails3()) {
			detail.setId(null);
			detail.setMod369(mod369.getId());
			mod369.getDetails3().add(detail);
		}
		for (Mod369Detail detail : original.getDetails4()) {
			detail.setId(null);
			detail.setMod369(mod369.getId());
			mod369.getDetails4().add(detail);
		}
		for (Mod369DetailOther detail : original.getDetails5()) {
			detail.setId(null);
			detail.setMod369(mod369.getId());
			mod369.getDetails5().add(detail);
		}
		for (Mod369DetailOther detail : original.getDetails6()) {
			detail.setId(null);
			detail.setMod369(mod369.getId());
			mod369.getDetails6().add(detail);
		}
		for (Mod369DetailCorrection detail : original.getCorrections()) {
			detail.setId(null);
			detail.setMod369(mod369.getId());
			mod369.getCorrections().add(detail);
		}
		return save(ctx, mod369);
	}
	
	// Mantenimiento de la fila en fs_model 
	
	private static Integer saveFsModel(AONContext ctx, Mod369 mod369) {

		// Comprobar si es necesario añadir o actualizar el registro en fs_model
		if (mod369.getFsModel() == null)
			return insertFsModel(ctx, mod369);
		else 
			return updateFsModel(ctx, mod369);
		
	}
	
	private static void deleteFsModel(AONContext ctx, Integer idFsModel) {
		
		if (idFsModel != null) {
			ctx.getDslContext()
				.delete(FS_MODEL)
				.where(FS_MODEL.ID.equal(idFsModel))
				.execute();
		}

	}
	
	private static Integer insertFsModel(AONContext ctx, Mod369 mod369) {
		
		Integer id = ctx.getDslContext()
			.insertInto(FS_MODEL)
				.set(FS_MODEL.DOMAIN, mod369.getDomain())
				.set(FS_MODEL.YEAR, mod369.getYear())
				.set(FS_MODEL.PERIOD, mod369.getPeriod().value())
				.set(FS_MODEL.ADMINISTRATION, mod369.getAdministration().value())
				.set(FS_MODEL.STATUS, AonEnumUtils.getByte(mod369.getStatus()))
				.set(FS_MODEL.SECURITY_LEVEL,AonEnumUtils.getByte( mod369.isConfidential() ))
//				.set(FS_MODEL.COMPLEMENTARY, AonEnumUtils.getByte( mod369.isComplementary() ))
//				.set(FS_MODEL.REPLACEMENT, AonEnumUtils.getByte( mod369.isReplacement() )) 
//				.set(FS_MODEL.WITHOUTACTIVITY,AonEnumUtils.getByte( mod349.isWithoutActivity()  ))
				.set(FS_MODEL.MODEL, mod369.getModel().getValue() )
//				.set(FS_MODEL.NUMBER, mod369.getNumber())
//				.set(FS_MODEL.REPLACED_NUMBER, mod369.getReplacedNumber())
				.set(FS_MODEL.COMMENTS, mod369.getComments())
				.set(FS_MODEL.FINANCE, mod369.getFinance() == null?null:mod369.getFinance().getId())
				.set(FS_MODEL.DOCUMENT, mod369.getDocument() )
//				.set(FS_MODEL.SURNAME, mod369.getSurname())
				.set(FS_MODEL.NAME, mod369.getName())
//				.set(FS_MODEL.STREET_INITIAL,mod349.getStreetInitial())
//				.set(FS_MODEL.STREET_NAME,mod349.getStreetName())
//				.set(FS_MODEL.STREET_NUMBER,mod349.getStreetNumber())
//				.set(FS_MODEL.STREET_STAIR,mod349.getStreetStair())
//				.set(FS_MODEL.STREET_FLOOR,mod349.getStreetFloor())
//				.set(FS_MODEL.STREET_DOOR,mod349.getStreetDoor())
//				.set(FS_MODEL.PHONE,mod349.getPhone())
//				.set(FS_MODEL.TOWN,mod349.getTown())
//				.set(FS_MODEL.PROVINCE,mod349.getProvince())
//				.set(FS_MODEL.ZIP,mod349.getZip())
//				.set(FS_MODEL.ADMON_AEAT,mod349.getAdmonAeat())
//				.set(FS_MODEL.CONTACT_PERSON,mod369.getContactPerson())
//				.set(FS_MODEL.CONTACT_PHONE,mod369.getContactPhone())
//				.set(FS_MODEL.CONTACT_CELLULAR,mod349.getContactCellular())
//				.set(FS_MODEL.CONTACT_EMAIL,mod369.getContactMail())
				.set(FS_MODEL.RESULT, mod369.getDeclarationResult())
				.set(FS_MODEL.DECLARATION_TYPE, AonEnumUtils.getByte( mod369.getDeclarationResultType() ) )
//				.set(FS_MODEL.ACCOUNT_ENTRY, mod349.getAccountEntry())
				.set(FS_MODEL.CREATION_USER, mod369.getCreationUser())
				.set(FS_MODEL.CREATION_DATE, AonDateUtils.toTimestamp(mod369.getCreationDate()))
				.set(FS_MODEL.MODIFICATION_USER, mod369.getModificationUser())
				.set(FS_MODEL.MODIFICATION_DATE, AonDateUtils.toTimestamp(mod369.getModificationDate()))				
			.returning(FS_MODEL.ID)
			.fetchOne()
			.getValue(FS_MODEL.ID);
		return id;
		
	}	
	
	private static Integer updateFsModel(AONContext ctx, Mod369 mod369) {
		
		ctx.getDslContext().update(FS_MODEL)
			.set(FS_MODEL.DOMAIN, mod369.getDomain())
			.set(FS_MODEL.YEAR, mod369.getYear())
			.set(FS_MODEL.PERIOD, mod369.getPeriod().value())
			.set(FS_MODEL.ADMINISTRATION, mod369.getAdministration().value())
			.set(FS_MODEL.STATUS, AonEnumUtils.getByte(mod369.getStatus()))
			.set(FS_MODEL.SECURITY_LEVEL,AonEnumUtils.getByte( mod369.isConfidential() ))
//			.set(FS_MODEL.COMPLEMENTARY, AonEnumUtils.getByte( mod369.isComplementary()))
//			.set(FS_MODEL.REPLACEMENT,AonEnumUtils.getByte( mod369.isReplacement() ))  
//			.set(FS_MODEL.WITHOUTACTIVITY,AonEnumUtils.getByte( mod349.isWithoutActivity()  ))
			.set(FS_MODEL.MODEL, mod369.getModel().getValue())
//			.set(FS_MODEL.NUMBER, mod369.getNumber())
//			.set(FS_MODEL.REPLACED_NUMBER, mod369.getReplacedNumber())
			.set(FS_MODEL.COMMENTS, mod369.getComments())
			.set(FS_MODEL.FINANCE, mod369.getFinance() == null?null:mod369.getFinance().getId())
			.set(FS_MODEL.DOCUMENT, mod369.getDocument())
//			.set(FS_MODEL.SURNAME, mod369.getSurname())
			.set(FS_MODEL.NAME, mod369.getName())
//			.set(FS_MODEL.STREET_INITIAL,mod349.getStreetInitial())
//			.set(FS_MODEL.STREET_NAME,mod349.getStreetName())
//			.set(FS_MODEL.STREET_NUMBER,mod349.getStreetNumber())
//			.set(FS_MODEL.STREET_STAIR,mod349.getStreetStair())
//			.set(FS_MODEL.STREET_FLOOR,mod349.getStreetFloor())
//			.set(FS_MODEL.STREET_DOOR,mod349.getStreetDoor())
//			.set(FS_MODEL.PHONE,mod349.getPhone())
//			.set(FS_MODEL.TOWN,mod349.getTown())
//			.set(FS_MODEL.PROVINCE,mod349.getProvince())
//			.set(FS_MODEL.ZIP,mod349.getZip())
//			.set(FS_MODEL.ADMON_AEAT,mod349.getAdmonAeat())
//			.set(FS_MODEL.CONTACT_PERSON,mod369.getContactPerson())
//			.set(FS_MODEL.CONTACT_PHONE,mod369.getContactPhone())
//			.set(FS_MODEL.CONTACT_CELLULAR,mod349.getContactCellular())
//			.set(FS_MODEL.CONTACT_EMAIL,mod369.getContactMail())
			.set(FS_MODEL.RESULT, mod369.getDeclarationResult())
			.set(FS_MODEL.DECLARATION_TYPE,AonEnumUtils.getByte( mod369.getDeclarationResultType() ) )
//			.set(FS_MODEL.ACCOUNT_ENTRY,mod349.getAccountEntry())
			.set(FS_MODEL.MODIFICATION_USER, mod369.getModificationUser())
			.set(FS_MODEL.MODIFICATION_DATE, AonDateUtils.toTimestamp(mod369.getModificationDate()))
		.where(FS_MODEL.ID.equal(mod369.getFsModel()))
		.execute();
		return mod369.getFsModel();
		
	}
	
	private static FiscalModel getFiscalModel(Mod369 mod369) {
		return new FiscalModel()
		         	.setId(mod369.getFsModel())
		         	.setDomain(mod369.getDomain())
		         	.setModel(mod369.getModel());
		
	}
	
}
