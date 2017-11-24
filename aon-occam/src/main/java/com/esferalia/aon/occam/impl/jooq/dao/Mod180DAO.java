package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel180.FS_MODEL180;
import static com.esferalia.aon.jooq.tables.FsModel180Detail.FS_MODEL180_DETAIL;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.FsModel180Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod180DAO {
	
	private static byte ZERO_BYTE = 0;
	private static byte ONE_BYTE = 1;
	

	public static LinkedList<Mod180> getByDomain(AONContext ctx, int domain) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(FS_MODEL180.fields())
			.from(FS_MODEL180)
			.join(DOMAIN).on(FS_MODEL180.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL180.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.orderBy(FS_MODEL180.YEAR.desc()
					,FS_MODEL180.NAME.asc()
					,FS_MODEL180.REPLACEMENT.asc())
			.fetch()
			.stream()
			.map(new Mod180Filler())
			.peek( mod180 -> mod180.setDetails( getDetails(ctx, mod180.getId()) ))
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static Mod180 getById(AONContext ctx, int id) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(FS_MODEL180.fields())
			.from(FS_MODEL180)
			.join(DOMAIN).on(FS_MODEL180.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL180.DOMAIN.equal(ctx.getDomainId()).or(DOMAIN.PARENT.equal(ctx.getDomainId())))
			.and(FS_MODEL180.ID.equal(id))
			.fetch()
			.stream()
			.map(new Mod180Filler())
			.peek( mod180 -> mod180.setDetails( getDetails(ctx, mod180.getId()) ))
			.findFirst()
			.orElse(null);
	}
	
	public static Mod180 saveComments(AONContext ctx, Mod180 fm) {
		try {
			ctx.checkWrite();
			if (fm.getId() != null) {
				ctx.getDslContext().update(FS_MODEL180)
					.set(FS_MODEL180.COMMENTS,fm.getComments())
					.where(FS_MODEL180.ID.equal(fm.getId()))
					.execute();
			}
			return fm;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Throwable t) {
			throw new AonCoreException(t.getMessage());
		}
	}

	public static Mod180 save(AONContext ctx, Mod180 mod180) {
		ctx.checkWrite();
		if (mod180.getId() == null) {
			mod180 = insert(ctx, mod180); 
			 
		} else {
			mod180 = update(ctx, mod180);
		}
		for (Mod180Detail detail : mod180.getDetails()) {
			saveDetail(ctx,mod180,detail);
		}
		return getById(ctx, mod180.getId());
	}

	private static Mod180 insert(AONContext ctx, Mod180 mod180) {
		validate(ctx,mod180);
		FsModel180Record record = ctx.getDslContext().insertInto(FS_MODEL180)
			.set(FS_MODEL180.DOMAIN,mod180.getDomain())
			.set(FS_MODEL180.ENTERPRISE,mod180.getEnterprise())
			.set(FS_MODEL180.YEAR,mod180.getYear())
			.set(FS_MODEL180.ADMINISTRATION, mod180.getAdministration().getValue())
			.set(FS_MODEL180.STATUS, ZERO_BYTE )
			.set(FS_MODEL180.SECURITY_LEVEL,AonEnumUtils.getByte(mod180.isConfidential()) ) 
			.set(FS_MODEL180.DOCUMENT,mod180.getDocument())
			.set(FS_MODEL180.NAME,mod180.getName())
			.set(FS_MODEL180.CONTACT_PERSON,mod180.getContactPerson())
			.set(FS_MODEL180.CONTACT_PHONE,mod180.getContactPhone())
			.set(FS_MODEL180.COMPLEMENTARY, ZERO_BYTE )
			.set(FS_MODEL180.REPLACEMENT,AonEnumUtils.getByte(mod180.isReplacement()))
			.set(FS_MODEL180.COMMENTS,mod180.getComments())
			.set(FS_MODEL180.RECEIPT,mod180.getReceipt())
			.set(FS_MODEL180.REPLACED_RECEIPT,mod180.getReplacedReceipt())
			.set(FS_MODEL180.RECEIVER_COUNT_TOTAL,mod180.getReceiverCountTotal())
			.set(FS_MODEL180.RECEIPT_TOTAL,mod180.getReceiptTotal())
			.set(FS_MODEL180.RETENTION_TOTAL,mod180.getRetentionTotal())
			.set(FS_MODEL180.CREATION_USER,ctx.getUser())
			.set(FS_MODEL180.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
		.returning(FS_MODEL180.ID)
		.fetchOne();
		mod180.setId(record.getId());
		insertDetailsFromInvoice(ctx, mod180);
		return mod180;
	}

	private static Mod180 update(AONContext ctx, Mod180 mod180) {
		ctx.getDslContext().update(FS_MODEL180)
			.set(FS_MODEL180.YEAR,mod180.getYear())
			.set(FS_MODEL180.ADMINISTRATION,mod180.getAdministration().getValue())
			.set(FS_MODEL180.STATUS,AonEnumUtils.getByte( mod180.getStatus()  ))
			.set(FS_MODEL180.SECURITY_LEVEL,AonEnumUtils.getByte(mod180.isConfidential()) ) 
			.set(FS_MODEL180.DOCUMENT,mod180.getDocument())
			.set(FS_MODEL180.NAME,mod180.getName())
			.set(FS_MODEL180.CONTACT_PERSON,mod180.getContactPerson())
			.set(FS_MODEL180.CONTACT_PHONE,mod180.getContactPhone())
			.set(FS_MODEL180.COMPLEMENTARY, ZERO_BYTE)
			.set(FS_MODEL180.REPLACEMENT,AonEnumUtils.getByte(mod180.isReplacement()))
			.set(FS_MODEL180.COMMENTS,mod180.getComments())
			.set(FS_MODEL180.RECEIPT,mod180.getReceipt())
			.set(FS_MODEL180.REPLACED_RECEIPT,mod180.getReplacedReceipt())
			.set(FS_MODEL180.RECEIVER_COUNT_TOTAL,mod180.getReceiverCountTotal())
			.set(FS_MODEL180.RECEIPT_TOTAL,mod180.getReceiptTotal())
			.set(FS_MODEL180.RETENTION_TOTAL,mod180.getRetentionTotal())
			.set(FS_MODEL180.MODIFICATION_USER,ctx.getUser())
			.set(FS_MODEL180.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(FS_MODEL180.ID.equal(mod180.getId()))
		.execute();
		return mod180;
	}

	public static void delete(AONContext ctx, Mod180 mod180) {
		ctx.checkWrite();
		deleteDetails(ctx, mod180);
		ctx.getDslContext().delete(FS_MODEL180)
			.where(FS_MODEL180.ID.equal(mod180.getId()))
			.execute();
	}

	private static void validate(AONContext ctx, Mod180 mod180) {
		if (mod180.isReplacement()) {
			// Se comprueba que exista la declaración ssustituida.
			if (!ctx.getDslContext().selectOne()
					.from(FS_MODEL180)
					.where(FS_MODEL180.YEAR.equal(mod180.getYear())
					.and(FS_MODEL180.ADMINISTRATION.equal(mod180.getAdministration().getValue()))
					.and(FS_MODEL180.ENTERPRISE.equal(mod180.getEnterprise()))
					.and(FS_MODEL180.RECEIPT.equal(mod180.getReplacedReceipt())))
					.fetch()
					.stream()
					.findFirst()
					.isPresent()) 
				throw new AonCoreException(
						AonError.FISCAL_NO_REPLACED_DECLARATION.getMessage());

			// Se comprueba que no exista una declaración sustitutiva.
			if (ctx.getDslContext().selectOne()
				.from(FS_MODEL180)
				.where(FS_MODEL180.YEAR.equal(mod180.getYear())
				.and(FS_MODEL180.ADMINISTRATION.equal(mod180.getAdministration().getValue()))						
				.and(FS_MODEL180.ENTERPRISE.equal(mod180.getEnterprise()))
				.and(FS_MODEL180.REPLACEMENT.equal( ONE_BYTE ))					
				.and(FS_MODEL180.REPLACED_RECEIPT.equal(mod180.getReplacedReceipt())))
				.fetch()
				.stream()
				.findFirst()
				.isPresent()) 
				throw new AonCoreException(AonError.FISCAL_DECLARATION_ALREADY_REPLACED.getMessage());
		} else {
			// Se comprueba que no exista ya una declaración.
			if (ctx.getDslContext().selectOne()
				.from(FS_MODEL180)
				.where(FS_MODEL180.YEAR.equal(mod180.getYear())
				.and(FS_MODEL180.ADMINISTRATION.equal(mod180.getAdministration().getValue()))
				.and(FS_MODEL180.ENTERPRISE.equal(mod180.getEnterprise()))
				.and(FS_MODEL180.REPLACEMENT.equal(ZERO_BYTE)))
				.fetch()
				.stream()
				.findFirst()
				.isPresent()) 
				throw new AonCoreException(
						AonError.FISCAL_DECLARATION_ALREADY_EXISTS.getMessage());
		}
	}
	
	public static Mod180Detail getDetail(AONContext ctx, int id) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(FS_MODEL180_DETAIL.fields())
			.from(FS_MODEL180_DETAIL)
			.where(FS_MODEL180_DETAIL.ID.equal(id))
			.fetch()
			.stream()
			.map( new Mod180DetailFiller())
			.findFirst()
			.orElse(null);
	}

	public static LinkedList<Mod180Detail> getDetails(AONContext ctx, int mod180) {
		ctx.checkRead();
		return ctx.getDslContext()
			.selectFrom(FS_MODEL180_DETAIL)
			.where(FS_MODEL180_DETAIL.FS_MODEL180.equal(mod180))
			.fetch()
			.stream()
			.map( new Mod180DetailFiller() )
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static void saveDetail(AONContext ctx, Mod180 mod180, Mod180Detail detail){
		ctx.checkWrite();
		if (detail.getId() == null) {
			if (!detail.isDeleted()) {
				detail.setDomain(mod180.getDomain());
				detail.setMod180(mod180.getId());
				insertDetail(ctx, detail);
			}
		} else {
			if (detail.isDeleted()) {
				deleteDetail(ctx, detail);
			} else {
				updateDetail(ctx, detail);
			}
		}
	}
	
	private static void insertDetail(AONContext ctx, Mod180Detail detail) {
		ctx.getDslContext().insertInto(FS_MODEL180_DETAIL)
			.set(FS_MODEL180_DETAIL.DOMAIN,detail.getDomain())
			.set(FS_MODEL180_DETAIL.FS_MODEL180,detail.getMod180())
			.set(FS_MODEL180_DETAIL.DOCUMENT,AonStringUtils.substring(detail.getDocument(), 0, 9))
			.set(FS_MODEL180_DETAIL.NAME,AonStringUtils.substring(detail.getName(), 0, 40))
			.set(FS_MODEL180_DETAIL.REPRESENTATIVE_DOCUMENT,AonStringUtils.substring(detail.getRepresentativeDocument(), 0, 9))
			.set(FS_MODEL180_DETAIL.PROVINCE,detail.getProvince())
			.set(FS_MODEL180_DETAIL.INKIND, AonEnumUtils.getByte( detail.isInKind()))
			.set(FS_MODEL180_DETAIL.PERCEPTION,detail.getPerception())
			.set(FS_MODEL180_DETAIL.PERCENTAGE,detail.getPercent())
			.set(FS_MODEL180_DETAIL.RETENTION,detail.getRetention())
			.set(FS_MODEL180_DETAIL.ACCRUAL_YEAR,detail.getAccrualYear())
			.set(FS_MODEL180_DETAIL.LOCATION,detail.getLocation())
			.set(FS_MODEL180_DETAIL.CADASDRAL_REFERENCE,detail.getCadasdralReference())
			.set(FS_MODEL180_DETAIL.STREET_TYPE,detail.getStreetType())
			.set(FS_MODEL180_DETAIL.STREET_NAME,detail.getStreetName())
			.set(FS_MODEL180_DETAIL.NUMBER_TYPE,detail.getNumberType())
			.set(FS_MODEL180_DETAIL.NUMBER,detail.getNumber())
			.set(FS_MODEL180_DETAIL.NUMBER_SUFFIX,detail.getNumberSuffix())
			.set(FS_MODEL180_DETAIL.BLOCK,detail.getBlock())
			.set(FS_MODEL180_DETAIL.HALL,detail.getHall())
			.set(FS_MODEL180_DETAIL.STAIR,detail.getStair())
			.set(FS_MODEL180_DETAIL.FLOOR,detail.getFloor())
			.set(FS_MODEL180_DETAIL.DOOR,detail.getDoor())
			.set(FS_MODEL180_DETAIL.COMPLEMENT,detail.getComplement())
			.set(FS_MODEL180_DETAIL.CITY,detail.getCity())
			.set(FS_MODEL180_DETAIL.TOWN,detail.getTown())
			.set(FS_MODEL180_DETAIL.TOWN_CODE,detail.getTownCode())
			.set(FS_MODEL180_DETAIL.PROVINCE_CODE,detail.getProvinceCode())
			.set(FS_MODEL180_DETAIL.ZIP,detail.getZip())
			.execute();
	}

	private static void updateDetail(AONContext ctx, Mod180Detail detail) {
		ctx.getDslContext().update(FS_MODEL180_DETAIL)
			.set(FS_MODEL180_DETAIL.DOCUMENT,AonStringUtils.substring(detail.getDocument(), 0, 9))
			.set(FS_MODEL180_DETAIL.NAME,AonStringUtils.substring(detail.getName(), 0, 40))
			.set(FS_MODEL180_DETAIL.REPRESENTATIVE_DOCUMENT,AonStringUtils.substring(detail.getRepresentativeDocument(), 0, 9))
			.set(FS_MODEL180_DETAIL.PROVINCE,detail.getProvince())
			.set(FS_MODEL180_DETAIL.INKIND,AonEnumUtils.getByte( detail.isInKind()))
			.set(FS_MODEL180_DETAIL.PERCEPTION,detail.getPerception())
			.set(FS_MODEL180_DETAIL.PERCENTAGE,detail.getPercent())
			.set(FS_MODEL180_DETAIL.RETENTION,detail.getRetention())
			.set(FS_MODEL180_DETAIL.ACCRUAL_YEAR,detail.getAccrualYear())
			.set(FS_MODEL180_DETAIL.LOCATION,detail.getLocation())
			.set(FS_MODEL180_DETAIL.CADASDRAL_REFERENCE,detail.getCadasdralReference())
			.set(FS_MODEL180_DETAIL.STREET_TYPE,detail.getStreetType())
			.set(FS_MODEL180_DETAIL.STREET_NAME,detail.getStreetName())
			.set(FS_MODEL180_DETAIL.NUMBER_TYPE,detail.getNumberType())
			.set(FS_MODEL180_DETAIL.NUMBER,detail.getNumber())
			.set(FS_MODEL180_DETAIL.NUMBER_SUFFIX,detail.getNumberSuffix())
			.set(FS_MODEL180_DETAIL.BLOCK,detail.getBlock())
			.set(FS_MODEL180_DETAIL.HALL,detail.getHall())
			.set(FS_MODEL180_DETAIL.STAIR,detail.getStair())
			.set(FS_MODEL180_DETAIL.FLOOR,detail.getFloor())
			.set(FS_MODEL180_DETAIL.DOOR,detail.getDoor())
			.set(FS_MODEL180_DETAIL.COMPLEMENT,detail.getComplement())
			.set(FS_MODEL180_DETAIL.CITY,detail.getCity())
			.set(FS_MODEL180_DETAIL.TOWN,detail.getTown())
			.set(FS_MODEL180_DETAIL.TOWN_CODE,detail.getTownCode())
			.set(FS_MODEL180_DETAIL.PROVINCE_CODE,detail.getProvinceCode())
			.set(FS_MODEL180_DETAIL.ZIP,detail.getZip())
			.where(FS_MODEL180_DETAIL.ID.equal(detail.getId()))
			.execute();
	}
	
	private static void deleteDetail(AONContext ctx, Mod180Detail detail) {
		ctx.getDslContext().delete(FS_MODEL180_DETAIL)
			.where(FS_MODEL180_DETAIL.ID.equal(detail.getId()))
			.execute();
	}

	private static void deleteDetails(AONContext ctx, Mod180 mod180) {
		ctx.getDslContext().delete(FS_MODEL180_DETAIL)
			.where(FS_MODEL180_DETAIL.FS_MODEL180.equal(mod180.getId()))
			.execute();
	}

	private static void insertDetailsFromInvoice(AONContext ctx , final Mod180 mod180) {
		java.sql.Date firstDay = AonDateUtils.toSql(AonDateUtils.getYearFirstDay(mod180.getYear()));
		java.sql.Date lastDay = AonDateUtils.toSql(AonDateUtils.getYearLastDay(mod180.getYear()));

		Field<Integer> minRegistry = DSL.min(INVOICE.REGISTRY).as(INVOICE.REGISTRY.getName());
		Field<BigDecimal> sumBase = DSL.sum(INVOICE_TAX.BASE).as(INVOICE_TAX.BASE.getName());
		Field<Double> invoiceTaxSum = DSL.round( (INVOICE_TAX.BASE.mul(INVOICE_TAX.PERCENTAGE)).div(100) ,2);
		Field<String> maxPercent = DSL.groupConcat(INVOICE_TAX.PERCENTAGE, AonStringUtils.COMMA);
		Field<BigDecimal> quotaOp = DSL.sum(DSL.decode()
				.when(INVOICE_TAX.QUOTA.notEqual(0.0), INVOICE_TAX.QUOTA)
				.when(INVOICE_TAX.QUOTA.equal(0.0), invoiceTaxSum));
		
		ctx.getDslContext().select(INVOICE.RDOCUMENT,INVOICE.RNAME,minRegistry,sumBase,quotaOp,maxPercent)
		.from(INVOICE)
		.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
		.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
		.where(INVOICE.DOMAIN.equal(mod180.getDomain()))
			.and(INVOICE.TYPE.notEqual( InvoiceType.SALES.value() )) // No Ventas
			.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.RETENTION.value() )) // IRPF
			.and(INVOICE_TAX.WITHHOLDING_TYPE.equal( WithholdingType.RENTING.value() ))	// IRPF de Alquiler
			.and(INVOICE.ISSUE_DATE.between(firstDay, lastDay))
		.groupBy(INVOICE.RDOCUMENT,INVOICE.RNAME)
		.fetch()
		.stream()
		.map(rec -> {
			String percents = rec.getValue(maxPercent);
			int last = AonStringUtils.lastIndexOf(percents, AonStringUtils.COMMA);
			String percent = AonStringUtils.substring(percents, last+1);
			return new Mod180Detail()
				.setDomain(mod180.getDomain())
				.setMod180(mod180.getId())
				.setDocument(rec.getValue(INVOICE.RDOCUMENT))
				.setName(rec.getValue(INVOICE.RNAME))
				.setInKind(false)
				.setPerception(rec.getValue(sumBase).doubleValue())
				.setRetention(rec.getValue(quotaOp).doubleValue())
				.setPercent(AonNumberUtils.toDouble(percent))
				.setProvince( getRegistryMainAddressProvince(ctx, rec.getValue(minRegistry)) );
			})
		.forEach(detail -> insertDetail(ctx,detail));
	}

	public static Mod180 initialize(AONContext ctx, int year) {
		Mod180 mod180 = new Mod180();
		FiscalParameters params = AppParamDAO.getFiscalParameters(ctx);
		mod180.setEnterprise(params.getCompany());
		mod180.setDomain(ctx.getDomainId());
		mod180.setDocument(params.getDocument());
		mod180.setName(AonStringUtils.left(params.getName(), FS_MODEL180.NAME
				.getDataType().length()));
		mod180.setYear(year);
		mod180.setAdministration(params.getAdministration(Administration.COMMON_TERRITORY));
		mod180.setContactPerson(AonStringUtils.left(params.getContactPerson(),
				FS_MODEL180.CONTACT_PERSON.getDataType().length()));
		mod180.setContactPhone(AonStringUtils.left(params.getContactPhone(),
				FS_MODEL180.CONTACT_PHONE.getDataType().length()));
		mod180.setReceipt("1800000000001");
		mod180.setStatus(FiscalStatus.PENDING);
		mod180.setDetails(new LinkedList<Mod180Detail>());
		return mod180;
	}
	
	private static class Mod180Filler implements Function<Record, Mod180> {

		@Override
		public Mod180 apply(Record record) {
			return new Mod180() 
				.setId(record.getValue(FS_MODEL180.ID))
				.setDomain(record.getValue(FS_MODEL180.DOMAIN))
				.setEnterprise(record.getValue(FS_MODEL180.ENTERPRISE))
				.setYear(record.getValue(FS_MODEL180.YEAR))
				.setAdministration( com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Administration.class,record.getValue(FS_MODEL180.ADMINISTRATION)))
				.setReplacement( record.getValue(FS_MODEL180.REPLACEMENT)==1 )
				.setComplementary(record.getValue(FS_MODEL180.COMPLEMENTARY)==1 )
				.setStatus(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(FiscalStatus.class,record.getValue(FS_MODEL180.STATUS)))
				.setDocument(record.getValue(FS_MODEL180.DOCUMENT))
				.setName(record.getValue(FS_MODEL180.NAME))
				.setContactPerson(record.getValue(FS_MODEL180.CONTACT_PERSON))
				.setContactPhone(record.getValue(FS_MODEL180.CONTACT_PHONE))
				.setReceipt(record.getValue(FS_MODEL180.RECEIPT))
				.setReplacedReceipt(record.getValue(FS_MODEL180.REPLACED_RECEIPT))
				.setReceiverCountTotal( record.getValue(FS_MODEL180.RECEIVER_COUNT_TOTAL))
				.setReceiptTotal(record.getValue(FS_MODEL180.RECEIPT_TOTAL))
				.setRetentionTotal(record.getValue(FS_MODEL180.RETENTION_TOTAL))
				.setComments(record.getValue(FS_MODEL180.COMMENTS))
				.setCreationUser(record.getValue(FS_MODEL180.CREATION_USER))
				.setCreationDate(record.getValue(FS_MODEL180.CREATION_DATE))
				.setModificationUser(record.getValue(FS_MODEL180.MODIFICATION_USER))
				.setModificationDate(record.getValue(FS_MODEL180.MODIFICATION_DATE))
				;
		}
	}

	private static class Mod180DetailFiller implements Function<Record, Mod180Detail> {

		@Override
		public Mod180Detail apply(Record record) {
			return new Mod180Detail()
				.setId(record.getValue(FS_MODEL180_DETAIL.ID))
				.setDocument(record.getValue(FS_MODEL180_DETAIL.DOCUMENT))
				.setName(record.getValue(FS_MODEL180_DETAIL.NAME))
				.setRepresentativeDocument(record.getValue(FS_MODEL180_DETAIL.REPRESENTATIVE_DOCUMENT))
				.setProvince(record.getValue(FS_MODEL180_DETAIL.PROVINCE))
				.setInKind(record.getValue(FS_MODEL180_DETAIL.INKIND)==1)
				.setPerception(record.getValue(FS_MODEL180_DETAIL.PERCEPTION))
				.setRetention(record.getValue(FS_MODEL180_DETAIL.RETENTION))
				.setPercent(record.getValue(FS_MODEL180_DETAIL.PERCENTAGE))
				.setAccrualYear(record.getValue(FS_MODEL180_DETAIL.ACCRUAL_YEAR))
				.setLocation(record.getValue(FS_MODEL180_DETAIL.LOCATION))
				.setCadasdralReference(record.getValue(FS_MODEL180_DETAIL.CADASDRAL_REFERENCE))
				.setStreetType(record.getValue(FS_MODEL180_DETAIL.STREET_TYPE))
				.setStreetName(record.getValue(FS_MODEL180_DETAIL.STREET_NAME))
				.setNumberType(record.getValue(FS_MODEL180_DETAIL.NUMBER_TYPE))
				.setNumber(record.getValue(FS_MODEL180_DETAIL.NUMBER))
				.setNumberSuffix(record.getValue(FS_MODEL180_DETAIL.NUMBER_SUFFIX))
				.setBlock(record.getValue(FS_MODEL180_DETAIL.BLOCK))
				.setHall(record.getValue(FS_MODEL180_DETAIL.HALL))
				.setStair(record.getValue(FS_MODEL180_DETAIL.STAIR))
				.setFloor(record.getValue(FS_MODEL180_DETAIL.FLOOR))
				.setDoor(record.getValue(FS_MODEL180_DETAIL.DOOR))
				.setComplement(record.getValue(FS_MODEL180_DETAIL.COMPLEMENT))
				.setCity(record.getValue(FS_MODEL180_DETAIL.CITY))
				.setTown(record.getValue(FS_MODEL180_DETAIL.TOWN))
				.setTownCode(record.getValue(FS_MODEL180_DETAIL.TOWN_CODE))
				.setProvinceCode(record.getValue(FS_MODEL180_DETAIL.PROVINCE_CODE))
				.setZip(record.getValue(FS_MODEL180_DETAIL.ZIP));
		}
	}

	private static Integer getRegistryMainAddressProvince(AONContext ctx, Integer registry) {
		return ctx.getDslContext()
			.select(GEOZONE.CODE)
			.from(RADDRESS)
			.join(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
			.where(RADDRESS.REGISTRY.equal(registry))
			.and(RADDRESS.TYPE.equal( ZERO_BYTE ))		// Dirección principal.
			.limit(1)
			.fetch()
			.stream()
			.mapToInt(rec -> Integer.parseInt(rec.getValue(GEOZONE.CODE) ))
			.findFirst()
			.orElse(0);
	}

	public static Mod180 changeStatusMod180(AONContext ctx, Mod180 mod180, FiscalStatus newStatus) {
		try {
			ctx.checkWrite();
			if (mod180.getId() != null) {
				mod180.setStatus(newStatus);
				ctx.getDslContext().update(FS_MODEL180)
					.set(FS_MODEL180.STATUS,AonEnumUtils.getByte( mod180.getStatus()))
					.where(FS_MODEL180.ID.equal(mod180.getId()))
					.execute();
			}
			return mod180;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Throwable t) {
			throw new AonCoreException(t.getMessage());
		}
	}
}
