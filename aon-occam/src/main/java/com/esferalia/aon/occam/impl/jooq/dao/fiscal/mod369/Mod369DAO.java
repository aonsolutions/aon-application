package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod369;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModel369.FS_MODEL369;
import static com.esferalia.aon.jooq.tables.FsModel369Detail.FS_MODEL369_DETAIL;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.impl.DSL;
import org.json.JSONArray;

import com.esferalia.aon.jooq.tables.records.FsModel369Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.VatContextJSON;
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
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO.Alcatraz;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class Mod369DAO {
	
	private static final byte ZERO_BYTE = 0;
	private static final byte ONE_BYTE = 1;
//	private static final Field<Integer> ALCATRAZ_INVOICE_ID = ALCATRAZ.INVOICE.as("alcatrazInvoice");
//	private static final String MODEL_INVOICE = "modelInvoice";
//	private static final Field<BigDecimal> SUM_BASE = DSL.sum(INVOICE_TAX.BASE);
//	private static final Field<BigDecimal> SUM_QUOTA = DSL.sum(DSL.decode()
//			.when(INVOICE_TAX.QUOTA.notEqual(0.0), INVOICE_TAX.QUOTA)
//			.when(INVOICE_TAX.QUOTA.equal(0.0), DSL.round((INVOICE_TAX.BASE.mul(INVOICE_TAX.PERCENTAGE)).div(100),2)));
	
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
		validate(ctx, mod369);
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
				.set(FS_MODEL369.RESULT, mod369.getResult())
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
		// A partir del ejercicio 2025, los registros de detalle se crean de forma automática desde las facturas
		if (mod369.getYear() >= 2025) {
			insertDetailsFromInvoice(ctx, mod369);
		}
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
			.set(FS_MODEL369.RESULT, mod369.getResult())
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
	
	private static void validate(AONContext ctx, Mod369 mod369) {
		
		// Comprobar que esta cumplimentado ejercicio, periodo y régimen
		if (mod369.getYear() == 0 || mod369.getPeriod() == null || mod369.getRegime() == null)
			throw new AonCoreException("Debe cumplimentar Ejercicio y Periodo.");
		
		// Se comprueba que no exista ya una declaracion, para el periodo y regimen indicado
		if (ctx.getDslContext().selectOne()
			.from(FS_MODEL369)
			.where(FS_MODEL369.DOMAIN.equal(mod369.getDomain())
					.and(FS_MODEL369.YEAR.equal(mod369.getYear()))
					.and(FS_MODEL369.PERIOD.equal(mod369.getPeriod().value()))
					.and(FS_MODEL369.REGIME.equal(mod369.getRegime().value()))
					.and(FS_MODEL369.ADMINISTRATION.equal(mod369.getAdministration().value())))				
			.fetch()
			.stream()
			.findFirst()
			.isPresent()) 
			throw new AonCoreException(AonError.FISCAL_DECLARATION_ALREADY_EXISTS.format(mod369.getModel().getName()));
	}

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
			    .setResult(rec.getValue(FS_MODEL369.RESULT))
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
				// FALTA - DATOS ORIGINALES PARA SABER SI LA LINEA ES MANUAL, FALTA AÑADIR LOS CAMPOS
				.setOriginalCountry(Country.safeValueOf(rec.getValue(FS_MODEL369_DETAIL.COUNTRY)))
				.setOriginalVatPercent(rec.getValue(FS_MODEL369_DETAIL.VAT_PERCENT))
				.setOriginalBase(rec.getValue(FS_MODEL369_DETAIL.BASE))
				.setOriginalQuota(rec.getValue(FS_MODEL369_DETAIL.QUOTA))
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
		
		return ctx.getDslContext()
			.insertInto(FS_MODEL)
				.set(FS_MODEL.DOMAIN, mod369.getDomain())
				.set(FS_MODEL.YEAR, mod369.getYear())
				.set(FS_MODEL.PERIOD, mod369.getPeriod().value())
				.set(FS_MODEL.ADMINISTRATION, mod369.getAdministration().value())
				.set(FS_MODEL.STATUS, AonEnumUtils.getByte(mod369.getStatus()))
				.set(FS_MODEL.SECURITY_LEVEL,AonEnumUtils.getByte( mod369.isConfidential() ))
//				.set(FS_MODEL.COMPLEMENTARY, AonEnumUtils.getByte( mod369.isComplementary() ))
//				.set(FS_MODEL.REPLACEMENT, AonEnumUtils.getByte( mod369.isReplacement() )) 
//				.set(FS_MODEL.WITHOUTACTIVITY,AonEnumUtils.getByte( mod369.isWithoutActivity()  ))
				.set(FS_MODEL.MODEL, mod369.getModel().getValue() )
//				.set(FS_MODEL.NUMBER, mod369.getNumber())
//				.set(FS_MODEL.REPLACED_NUMBER, mod369.getReplacedNumber())
				.set(FS_MODEL.COMMENTS, mod369.getComments())
				.set(FS_MODEL.FINANCE, mod369.getFinance() == null?null:mod369.getFinance().getId())
				.set(FS_MODEL.DOCUMENT, mod369.getDocument() )
//				.set(FS_MODEL.SURNAME, mod369.getSurname())
				.set(FS_MODEL.NAME, mod369.getName())
//				.set(FS_MODEL.STREET_INITIAL,mod369.getStreetInitial())
//				.set(FS_MODEL.STREET_NAME,mod369.getStreetName())
//				.set(FS_MODEL.STREET_NUMBER,mod369.getStreetNumber())
//				.set(FS_MODEL.STREET_STAIR,mod369.getStreetStair())
//				.set(FS_MODEL.STREET_FLOOR,mod369.getStreetFloor())
//				.set(FS_MODEL.STREET_DOOR,mod369.getStreetDoor())
//				.set(FS_MODEL.PHONE,mod369.getPhone())
//				.set(FS_MODEL.TOWN,mod369.getTown())
//				.set(FS_MODEL.PROVINCE,mod369.getProvince())
//				.set(FS_MODEL.ZIP,mod369.getZip())
//				.set(FS_MODEL.ADMON_AEAT,mod369.getAdmonAeat())
//				.set(FS_MODEL.CONTACT_PERSON,mod369.getContactPerson())
//				.set(FS_MODEL.CONTACT_PHONE,mod369.getContactPhone())
//				.set(FS_MODEL.CONTACT_CELLULAR,mod369.getContactCellular())
//				.set(FS_MODEL.CONTACT_EMAIL,mod369.getContactMail())
				.set(FS_MODEL.RESULT, mod369.getDeclarationResult())
				.set(FS_MODEL.DECLARATION_TYPE, AonEnumUtils.getByte( mod369.getDeclarationResultType() ) )
//				.set(FS_MODEL.ACCOUNT_ENTRY, mod369.getAccountEntry())
				.set(FS_MODEL.CREATION_USER, mod369.getCreationUser())
				.set(FS_MODEL.CREATION_DATE, AonDateUtils.toTimestamp(mod369.getCreationDate()))
				.set(FS_MODEL.MODIFICATION_USER, mod369.getModificationUser())
				.set(FS_MODEL.MODIFICATION_DATE, AonDateUtils.toTimestamp(mod369.getModificationDate()))				
			.returning(FS_MODEL.ID)
			.fetchOne()
			.getValue(FS_MODEL.ID);
		
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
//			.set(FS_MODEL.WITHOUTACTIVITY,AonEnumUtils.getByte( mod369.isWithoutActivity()  ))
			.set(FS_MODEL.MODEL, mod369.getModel().getValue())
//			.set(FS_MODEL.NUMBER, mod369.getNumber())
//			.set(FS_MODEL.REPLACED_NUMBER, mod369.getReplacedNumber())
			.set(FS_MODEL.COMMENTS, mod369.getComments())
			.set(FS_MODEL.FINANCE, mod369.getFinance() == null?null:mod369.getFinance().getId())
			.set(FS_MODEL.DOCUMENT, mod369.getDocument())
//			.set(FS_MODEL.SURNAME, mod369.getSurname())
			.set(FS_MODEL.NAME, mod369.getName())
//			.set(FS_MODEL.STREET_INITIAL,mod369.getStreetInitial())
//			.set(FS_MODEL.STREET_NAME,mod369.getStreetName())
//			.set(FS_MODEL.STREET_NUMBER,mod369.getStreetNumber())
//			.set(FS_MODEL.STREET_STAIR,mod369.getStreetStair())
//			.set(FS_MODEL.STREET_FLOOR,mod369.getStreetFloor())
//			.set(FS_MODEL.STREET_DOOR,mod369.getStreetDoor())
//			.set(FS_MODEL.PHONE,mod369.getPhone())
//			.set(FS_MODEL.TOWN,mod369.getTown())
//			.set(FS_MODEL.PROVINCE,mod369.getProvince())
//			.set(FS_MODEL.ZIP,mod369.getZip())
//			.set(FS_MODEL.ADMON_AEAT,mod369.getAdmonAeat())
//			.set(FS_MODEL.CONTACT_PERSON,mod369.getContactPerson())
//			.set(FS_MODEL.CONTACT_PHONE,mod369.getContactPhone())
//			.set(FS_MODEL.CONTACT_CELLULAR,mod369.getContactCellular())
//			.set(FS_MODEL.CONTACT_EMAIL,mod369.getContactMail())
			.set(FS_MODEL.RESULT, mod369.getDeclarationResult())
			.set(FS_MODEL.DECLARATION_TYPE,AonEnumUtils.getByte( mod369.getDeclarationResultType() ) )
//			.set(FS_MODEL.ACCOUNT_ENTRY,mod369.getAccountEntry())
			.set(FS_MODEL.MODIFICATION_USER, mod369.getModificationUser())
			.set(FS_MODEL.MODIFICATION_DATE, AonDateUtils.toTimestamp(mod369.getModificationDate()))
		.where(FS_MODEL.ID.equal(mod369.getFsModel()))
		.execute();
		return mod369.getFsModel();
		
	}
	
	// -----------------------------------------------------------------------------------------------------
	
	private static FiscalModel getFiscalModel(Mod369 mod369) {
		return new FiscalModel()
		         	.setId(mod369.getFsModel())
		         	.setDomain(mod369.getDomain())
		         	.setModel(mod369.getModel())
		         	.setYear(mod369.getYear())
		         	.setPeriod(mod369.getPeriod())
		         	.setAdministration(mod369.getAdministration());		
	}
	
//	private static void insertDetailsFromInvoiceOld(AONContext ctx, final Mod369 mod369) {
//		
//		Field<?>[] selectFields;
//		GroupField[] groupByFields;		
//		if (mod369.getRegime() == Mod369Regime.UNION ) {
//			// Régimen de la Unión
//			selectFields = new Field[] { INVOICE.SERVICE,INVOICE.RDOCUMENT_COUNTRY,INVOICE_TAX.PERCENTAGE,SUM_BASE,SUM_QUOTA };
//			groupByFields = new GroupField[]{ INVOICE.SERVICE,INVOICE.RDOCUMENT_COUNTRY,INVOICE_TAX.PERCENTAGE };
//		} else {
//			// Regimen Exterior a la Unión y Régimen de Importación
//			selectFields = new Field[] { INVOICE.RDOCUMENT_COUNTRY,INVOICE_TAX.PERCENTAGE,SUM_BASE,SUM_QUOTA };
//			groupByFields = new GroupField[]{ INVOICE.RDOCUMENT_COUNTRY,INVOICE_TAX.PERCENTAGE };
//		}
//		
//		// CREAR LOS REGISTRO DE DETALLE
//		
//		getCommonSelect(ctx, mod369, selectFields)
//			.groupBy(groupByFields)
//			.fetch()
//			.stream()
//			.forEach(rec -> {
//				// Régimen de la Unión (modalidad 0 y 1), Régimen Exterior a la Unión y Régimen de Importación
//				// Las modalidades 2 y 3 del Régimen de la Unión, no están soportadas actualmente en las facturas
//				Mod369Detail mod369Detail = new Mod369Detail()
//					.setDomain(mod369.getDomain())
//					.setMod369(mod369.getId())
//					.setCountry(Country.safeValueOf(rec.getValue(INVOICE.RDOCUMENT_COUNTRY)))
//					.setVatPercent(rec.getValue(INVOICE_TAX.PERCENTAGE).doubleValue())
//					.setVatType(rec.getValue(INVOICE_TAX.PERCENTAGE).doubleValue() < 15.0 ? Mod369VatType.REDUCED : Mod369VatType.STANDARD) // Según normativa europea el tipo de IVA normal en la Union Europea no puede ser inferior al 15%
//					.setBase(rec.getValue(SUM_BASE).doubleValue())
//					.setQuota(rec.getValue(SUM_QUOTA).doubleValue());
//				
//				byte detailType = 0;
//				if (mod369.getRegime() == Mod369Regime.UNION && !AonEnumUtils.getBoolean(rec.getValue(INVOICE.SERVICE))) {
//					detailType = 1;
//				}			
//				insertDetail(ctx, mod369Detail, detailType);
//			});
//		
//		// CREAR LOS REGISTROS DE LAS FACTURAS VINCULADAS EN ALCATRAZ
//		
//		Set<Alcatraz> alcatrazInvoices = new HashSet<>();
//		
//		getCommonSelect(ctx, mod369, new Field[] { INVOICE.ID })
//			.fetch()
//			.stream()
//			.forEach(rec -> {
//				// Solo a partir del ejercicio 2025 se empiezan a vincular las facturas con este modelo
//				if (mod369.getYear() >= 2025)			
//					alcatrazInvoices.add( new Alcatraz().setInvoice(rec.getValue(INVOICE.ID)) );			
//			});
//		
//		// Grabar en alcatraz las facturas vinculadas al modelo que se está generando
//		AlcatrazDAO.deleteFiscalModel(ctx, getFiscalModel(mod369));
//		AlcatrazDAO.saveModelInvoices(ctx, getFiscalModel(mod369), alcatrazInvoices); 
//
//	}
	
//	private static SelectConditionStep<Record> getCommonSelect(AONContext ctx, final Mod369 mod369, Field<?>[] selectFields) {
//		
//		// FALTA - FECHAS DEL PERIODO O DESDE EL INICIO DEL EJERCICIO HASTA EL FINAL DEL PERIODO, POR AHORA DESDE EL INICIO DEL EJERCICIO
//		//         TENER EN CUENTA QUE LA PRIMERA VEZ QUE SE GENERE UN M369 NO ESTARA VINCULADO A NINGUNA FACTURA EN ALCATRAZ 
//		java.sql.Date firstDay = AonDateUtils.toSql(AonDateUtils.getYearFirstDay(mod369.getYear()));  // FiscalUtils.getPeriodStart(mod349);
//		java.sql.Date lastDay = AonDateUtils.toSql(FiscalUtils.getPeriodEnd(mod369));
//		
//		Table<Record1<Integer>> modelInvoice = ctx.getDslContext().select(ALCATRAZ_INVOICE_ID)
//				.from(ALCATRAZ)
//				.join(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
//				.where(FS_MODEL.DOMAIN.eq(mod369.getDomain()))
//				.and(ALCATRAZ.FINANCE.isNull())
//				.and(ALCATRAZ.FINANCE_TRACKING.isNull())
//				.and(FS_MODEL.YEAR.eq(mod369.getYear()))
//				.and(FS_MODEL.MODEL.eq(mod369.getModel().getValue()))
//				.asTable(MODEL_INVOICE);
//		
//		return ctx.getDslContext().select(selectFields)
//			.from(INVOICE)
//			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
//			.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
//			.join(INVOICE_FISCAL).on(INVOICE_FISCAL.INVOICE.equal(INVOICE.ID))
//			.leftAntiJoin(modelInvoice).on(ALCATRAZ_INVOICE_ID.equal(INVOICE.ID))  // Facturas que no estén vinculadas con ningún otro modelo 369 del ejercicio
//			.where(INVOICE.DOMAIN.equal(mod369.getDomain()))
//				.and(INVOICE.TYPE.equal(InvoiceType.SALES.value()))    // Ventas
//				.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.VAT.value()))  // IVA
//				.and(INVOICE_FISCAL.VAT_UNION.equal(AonEnumUtils.getByte(mod369.getRegime() == Mod369Regime.UNION)))             // Régimen de la Union
//				.and(INVOICE_FISCAL.VAT_UNION_EXTERNAL.equal(AonEnumUtils.getByte(mod369.getRegime() == Mod369Regime.OUTSIDE)))  // Régimen Exterior a la Union
//				.and(INVOICE_FISCAL.VAT_IMPORTATION.equal(AonEnumUtils.getByte(mod369.getRegime() == Mod369Regime.IMPORT)))      // Régimen de Importación
//				.and(INVOICE.TAX_DATE.between(firstDay, lastDay));
//		
//	}
	
	private static void insertDetailsFromInvoice(AONContext ctx, final Mod369 mod369) {
		
		// FALTA - POSIBILIDAD DE CUMPLIMENTAR ALGUNOS DATOS DE LA CABECERA DEL MODELO, DE FORMA AUTOMATICA
		// FALTA - LEER LAS FACTURAS SIEMPRE DESDE PRINCIPIO DE AÑO, O SIEMPRE EXCEPTO EL MES DE FEBRERO, QUE SE SUPONE QUE PUEDEN HABER HECHO ALGUN M369 EN ENERO
		
		// Guardaremos los ids de las facturas, para que después de crear los registros en Mod369Detail, creemos los registros en Alcatraz
		Set<Alcatraz> alcatrazInvoices = new HashSet<>();
		
		// Leer las facturas no vinculadas a ningún modelo 369 del ejercicio y administración, desde el inicio del ejercicio 
		VATDAO.getNotInModelVatBreakdown(ctx, getFiscalModel(mod369).setGenerateFromYearStart(true))
			.filter( vat -> vat.getInvoiceType() == InvoiceType.SALES )  // Sólo ventas 
			.filter( vat -> (mod369.getRegime() == Mod369Regime.UNION && vat.isVatUnion()) || (mod369.getRegime() == Mod369Regime.OUTSIDE && vat.isVatUnionExternal()) || (mod369.getRegime() == Mod369Regime.IMPORT && vat.isVatImportation()) )  // Sólo del Régimen del modelo
			.peek(vat -> {
				// Régimen Exterior a la Unión y Régimen de Importación, no se separan por servicio
				if (mod369.getRegime() != Mod369Regime.UNION && vat.isService()) {
					vat.setService(false);
				}
				
				// Guardar la factura, para añadirla a Alcatraz: Solo a partir del ejercicio 2025 se empiezan a vincular las facturas con este modelo
				if (mod369.getYear() >= 2025)			
					alcatrazInvoices.add( new Alcatraz().setInvoice(vat.getInvoice()) );
			})
			.collect(Collectors.toMap( // Agrupar por servicio, pais y porcentaje de IVA, sumando base y cuota
                    item -> Arrays.asList(item.isService(), item.getRegistryDocumentCountry(), item.getPercentage()),
                    item -> item,
                    (existing, replacement) -> {
                        existing.setBase(existing.getBase() + replacement.getBase());
                        existing.setQuota(existing.getQuota() + replacement.getQuota());
                        return existing;
                    }
                ))
			.forEach((key, value) -> {
				// Régimen de la Unión (modalidad 0 y 1), Régimen Exterior a la Unión y Régimen de Importación
				// Las modalidades 2 y 3 del Régimen de la Unión, no están soportadas actualmente en las facturas
				Mod369Detail mod369Detail = new Mod369Detail()
					.setDomain(mod369.getDomain())
					.setMod369(mod369.getId())
					.setCountry(value.getRegistryDocumentCountry())
					.setVatPercent(value.getPercentage())
					.setVatType(value.getPercentage() < 15.0 ? Mod369VatType.REDUCED : Mod369VatType.STANDARD) // Según normativa europea el tipo de IVA normal en la Union Europea no puede ser inferior al 15%
					.setBase(value.getBase())
					.setQuota(value.getQuota())
					.setOriginalCountry(value.getRegistryDocumentCountry())
					.setOriginalVatPercent(value.getPercentage())
					.setOriginalBase(value.getBase())
					.setOriginalQuota(value.getQuota());
				
				byte detailType = 0;
				if (mod369.getRegime() == Mod369Regime.UNION && !value.isService()) {
					detailType = 1;
				}			
				insertDetail(ctx, mod369Detail, detailType);
	        });
		
		// Grabar en alcatraz las facturas vinculadas al modelo que se está generando
		AlcatrazDAO.deleteFiscalModel(ctx, getFiscalModel(mod369));
		AlcatrazDAO.saveModelInvoices(ctx, getFiscalModel(mod369), alcatrazInvoices); 

	}	

	public static String getInfo(AONContext ctx, Mod369 mod369, Mod369Detail mod369Detail, byte detailType) {
		return Objects.requireNonNullElse(
				getModelInvoicesInfo(ctx, mod369, mod369Detail, detailType)
					.map( VatContextJSON::toJSON)
					.collect(JSONArray::new,JSONArray::put,JSONArray::put)
					,new JSONArray()).toString();
	}
	
	private static Stream<VatContext> getModelInvoicesInfo(AONContext ctx, Mod369 mod369, Mod369Detail mod369Detail, byte detailType) {
		if (mod369Detail.isManual() ) {
			return VATDAO.getVatBreakdown(ctx, getFiscalModel(mod369))
					.filter( vat -> vat.getInvoiceType() == InvoiceType.SALES )
					.filter( vat -> (mod369.getRegime() == Mod369Regime.UNION && vat.isVatUnion()) || (mod369.getRegime() == Mod369Regime.OUTSIDE && vat.isVatUnionExternal()) || (mod369.getRegime() == Mod369Regime.IMPORT && vat.isVatImportation()) )
					.filter( vat -> (mod369.getRegime() != Mod369Regime.UNION) || (detailType == ZERO_BYTE && vat.isService()) || (detailType == ONE_BYTE && !vat.isService()) )  // Prestaciones de Servicio o Entrega de bienes (solo Régimen de la Unión)
					.filter( vat -> vat.getRegistryDocumentCountry() == mod369Detail.getCountry() ) // País
					.filter( vat -> vat.getPercentage() == mod369Detail.getVatPercent() ); // Porcentaje de IVA
		} else {		
			return VATDAO.getModelVatBreakdown(ctx, getFiscalModel(mod369))
					.filter( vat -> (mod369.getRegime() != Mod369Regime.UNION) || (detailType == ZERO_BYTE && vat.isService()) || (detailType == ONE_BYTE && !vat.isService()) )  // Prestaciones de Servicio o Entrega de bienes (solo Régimen de la Unión)
					.filter( vat -> vat.getRegistryDocumentCountry() == mod369Detail.getCountry() ) // País
					.filter( vat -> vat.getPercentage() == mod369Detail.getVatPercent() ); // Porcentaje de IVA
		}
	}
	
}
