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
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jooq.Field;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.FsModel180DetailRecord;
import com.esferalia.aon.jooq.tables.records.FsModel180Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod180DAO {
	
	private static Logger LOGGER = Logger.getLogger(Mod180DAO.class.getName());

	public static ArrayList<Mod180> getByDomain(AONContext ctx, int domain) {
		ctx.checkRead();
		ArrayList<Mod180> list = new ArrayList<Mod180>();
		ctx.getDslContext().select(FS_MODEL180.fields())
			.from(FS_MODEL180)
			.join(DOMAIN).on(FS_MODEL180.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL180.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.orderBy(FS_MODEL180.YEAR.desc()
					,FS_MODEL180.NAME.asc()
					,FS_MODEL180.REPLACEMENT.asc())
			.fetchInto(FsModel180Record.class )
			.stream()
			.forEach( record -> {
				Mod180 mod180 = new Mod180();
				populate( record, mod180);
				ArrayList<Mod180Detail> details = getDetails(ctx, mod180.getId());
				mod180.setDetails( details );
				list.add(mod180);
			});
		return list;
	}

	public static Mod180 getById(AONContext ctx, int id) {
		ctx.checkRead();
		Mod180 mod180 = new Mod180();
		ctx.getDslContext().selectFrom(FS_MODEL180)
		.where(FS_MODEL180.ID.equal(id))
		.fetch()
		.stream()
		.forEach( record -> {
			populate(record, mod180);
			ArrayList<Mod180Detail> details = getDetails(ctx, mod180.getId());
			mod180.setDetails( details );
		});
		if ( mod180.getId() != null ) {
			return mod180;
		}
		return null;
	}
	
	private static void populate(FsModel180Record record, Mod180 mod180) {
		mod180.setId(record.getValue(FS_MODEL180.ID));
		mod180.setDomain(record.getValue(FS_MODEL180.DOMAIN));
		mod180.setEnterprise(record.getValue(FS_MODEL180.ENTERPRISE));
		mod180.setYear(record.getValue(FS_MODEL180.YEAR));
		mod180.setAdministration( (int) record.getValue(FS_MODEL180.ADMINISTRATION));
		mod180.setReplacement( record.getValue(FS_MODEL180.REPLACEMENT)==1 );
		mod180.setDocument(record.getValue(FS_MODEL180.DOCUMENT));
		mod180.setName(record.getValue(FS_MODEL180.NAME));
		mod180.setContactPerson(record.getValue(FS_MODEL180.CONTACT_PERSON));
		mod180.setContactPhone(record.getValue(FS_MODEL180.CONTACT_PHONE));
		mod180.setReceipt(record.getValue(FS_MODEL180.RECEIPT));
		mod180.setReplacedReceipt(record.getValue(FS_MODEL180.REPLACED_RECEIPT));
		mod180.setReceiverCountTotal( record.getValue(FS_MODEL180.RECEIVER_COUNT_TOTAL));
		mod180.setReceiptTotal(record.getValue(FS_MODEL180.RECEIPT_TOTAL));
		mod180.setRetentionTotal(record.getValue(FS_MODEL180.RETENTION_TOTAL));
		mod180.setComments(record.getValue(FS_MODEL180.COMMENTS));
	}
	
	public static Mod180 save(AONContext ctx, Mod180 mod180) {
		ctx.checkWrite();
		if (mod180.getId() == null) {
			LOGGER.log(Level.INFO, "INSERTING Mod180");
			mod180 = insert(ctx, mod180); 
			 
		} else {
			LOGGER.log(Level.INFO, "UPDATING Mod180");
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
			.set(FS_MODEL180.ADMINISTRATION,(byte) mod180.getAdministration())
			.set(FS_MODEL180.STATUS,(byte) 0)
			.set(FS_MODEL180.SECURITY_LEVEL,AonEnumUtils.getByte(mod180.isConfidential()) ) 
			.set(FS_MODEL180.DOCUMENT,mod180.getDocument())
			.set(FS_MODEL180.NAME,mod180.getName())
			.set(FS_MODEL180.CONTACT_PERSON,mod180.getContactPerson())
			.set(FS_MODEL180.CONTACT_PHONE,mod180.getContactPhone())
			.set(FS_MODEL180.COMPLEMENTARY, (byte) 0)
			.set(FS_MODEL180.REPLACEMENT,AonEnumUtils.getByte(mod180.isReplacement()))
			.set(FS_MODEL180.COMMENTS,mod180.getComments())
			.set(FS_MODEL180.RECEIPT,mod180.getReceipt())
			.set(FS_MODEL180.REPLACED_RECEIPT,mod180.getReplacedReceipt())
			.set(FS_MODEL180.RECEIVER_COUNT_TOTAL,mod180.getReceiverCountTotal())
			.set(FS_MODEL180.RECEIPT_TOTAL,mod180.getReceiptTotal())
			.set(FS_MODEL180.RETENTION_TOTAL,mod180.getRetentionTotal())			
		.returning(FS_MODEL180.ID)
		.fetchOne();
		mod180.setId(record.getId());
		insertDetailsFromInvoice(ctx, mod180);
		return mod180;
	}

	private static Mod180 update(AONContext ctx, Mod180 mod180) {
		ctx.getDslContext().update(FS_MODEL180)
			.set(FS_MODEL180.YEAR,mod180.getYear())
			.set(FS_MODEL180.ADMINISTRATION,(byte) mod180.getAdministration())
			.set(FS_MODEL180.STATUS,(byte) 0)
			.set(FS_MODEL180.SECURITY_LEVEL,AonEnumUtils.getByte(mod180.isConfidential()) ) 
			.set(FS_MODEL180.DOCUMENT,mod180.getDocument())
			.set(FS_MODEL180.NAME,mod180.getName())
			.set(FS_MODEL180.CONTACT_PERSON,mod180.getContactPerson())
			.set(FS_MODEL180.CONTACT_PHONE,mod180.getContactPhone())
			.set(FS_MODEL180.COMPLEMENTARY, (byte) 0)
			.set(FS_MODEL180.REPLACEMENT,AonEnumUtils.getByte(mod180.isReplacement()))
			.set(FS_MODEL180.COMMENTS,mod180.getComments())
			.set(FS_MODEL180.RECEIPT,mod180.getReceipt())
			.set(FS_MODEL180.REPLACED_RECEIPT,mod180.getReplacedReceipt())
			.set(FS_MODEL180.RECEIVER_COUNT_TOTAL,mod180.getReceiverCountTotal())
			.set(FS_MODEL180.RECEIPT_TOTAL,mod180.getReceiptTotal())
			.set(FS_MODEL180.RETENTION_TOTAL,mod180.getRetentionTotal())
			.where(FS_MODEL180.ID.equal(mod180.getId()))
		.execute();
		return mod180;
	}

	public static void delete(AONContext ctx, Mod180 mod180) {
		ctx.checkWrite();
		deleteDetails(ctx, mod180);
		LOGGER.log(Level.INFO, "DELETING DECLARATION(" + mod180.getId() + ")");
		ctx.getDslContext().delete(FS_MODEL180)
			.where(FS_MODEL180.ID.equal(mod180.getId()))
			.execute();
	}

	private static void validate(AONContext ctx, Mod180 mod180) {
		if (mod180.isReplacement()) {
			// Se comprueba que exista la declaración ssustituida.
			if (ctx.getDslContext().selectOne()
					.from(FS_MODEL180)
					.where(FS_MODEL180.YEAR.equal(mod180.getYear())
					.and(FS_MODEL180.ENTERPRISE.equal(mod180.getEnterprise()))
					.and(FS_MODEL180.RECEIPT.equal(mod180.getReplacedReceipt()))).fetchCount() == 0) 
				throw new AonCoreException(
						AonError.FISCAL_NO_REPLACED_DECLARATION.getMessage());

			// Se comprueba que no exista una declaraci?n sustitutiva.
			if (ctx.getDslContext().selectOne()
					.from(FS_MODEL180)
					.where(FS_MODEL180.YEAR.equal(mod180.getYear())
					.and(FS_MODEL180.ENTERPRISE.equal(mod180.getEnterprise()))
					.and(FS_MODEL180.REPLACEMENT.equal((byte) 1))					
					.and(FS_MODEL180.REPLACED_RECEIPT.equal(mod180.getReplacedReceipt()))).fetchCount() > 0 )
				throw new AonCoreException(AonError.FISCAL_DECLARATION_ALREADY_REPLACED.getMessage());
		} else {
			// Se comprueba que no exista ya una declaraci?n.
			if (ctx.getDslContext().selectOne()
					.from(FS_MODEL180)
					.where(FS_MODEL180.YEAR.equal(mod180.getYear())
					.and(FS_MODEL180.ENTERPRISE.equal(mod180.getEnterprise()))
					.and(FS_MODEL180.REPLACEMENT.equal((byte) 0))).fetchCount() > 0 ) 
				throw new AonCoreException(
						AonError.FISCAL_DECLARATION_ALREADY_EXISTS.getMessage());
		}
	}
	//		DETAIL
	
	public static Mod180Detail getDetail(AONContext ctx, int id) {
		ctx.checkRead();
		FsModel180DetailRecord record = ctx.getDslContext()
			.selectFrom(FS_MODEL180_DETAIL)
			.where(FS_MODEL180_DETAIL.ID.equal(id))
			.fetchOne();
		Mod180Detail detail = null;
		if (record != null) {
			detail = new Mod180Detail();
			populateDetail(record, detail);
		}
		return detail;
	}

	public static ArrayList<Mod180Detail> getDetails(AONContext ctx, int mod180) {
		ctx.checkRead();
		Result<FsModel180DetailRecord> records = ctx.getDslContext()
			.selectFrom(FS_MODEL180_DETAIL)
			.where(FS_MODEL180_DETAIL.FS_MODEL180.equal(mod180))
			.fetch();
		ArrayList<Mod180Detail> list = new ArrayList<>();
		Mod180Detail detail = null;
		for (FsModel180DetailRecord record : records) {
			detail = new Mod180Detail();
			populateDetail(record, detail);
			list.add(detail);
		}
		return list;
	}

	private static void populateDetail(FsModel180DetailRecord record, Mod180Detail detail) {
		detail.setId(record.getId());
		detail.setDocument(record.getDocument());
		detail.setName(record.getName());
		detail.setRepresentativeDocument(record.getRepresentativeDocument());
		detail.setProvince(record.getProvince());
		detail.setInKind(record.getInkind()==1);
		detail.setPerception(record.getPerception());
		detail.setRetention(record.getRetention());
		detail.setPercent(record.getPercentage());
		detail.setAccrualYear(record.getAccrualYear());
		detail.setLocation(record.getLocation());
		detail.setCadasdralReference(record.getCadasdralReference());
		detail.setStreetType(record.getStreetType());
		detail.setStreetName(record.getStreetName());
		detail.setNumberType(record.getNumberType());
		detail.setNumber(record.getNumber());
		detail.setNumberSuffix(record.getNumberSuffix());
		detail.setBlock(record.getBlock());
		detail.setHall(record.getHall());
		detail.setStair(record.getStair());
		detail.setFloor(record.getFloor());
		detail.setDoor(record.getDoor());
		detail.setComplement(record.getComplement());
		detail.setCity(record.getCity());
		detail.setTown(record.getTown());
		detail.setTownCode(record.getTownCode());
		detail.setProvinceCode(record.getProvinceCode());
		detail.setZip(record.getZip());
		
	}

	public static void saveDetail(AONContext ctx, Mod180 mod180, Mod180Detail detail){
		ctx.checkWrite();
		if (detail.getId() == null || detail.getId() < 0) {
			if (!detail.isDeleted()) {
				detail.setDomain(mod180.getDomain());
				detail.setMod180(mod180.getId());
				insertDetail(ctx, detail);
				LOGGER.log(Level.INFO, "INSERTING Mod180Detail");
			}
		} else {
			if (detail.isDeleted()) {
				deleteDetail(ctx, detail);
				LOGGER.log(Level.INFO, "DELETING Mod180Detail");
			} else {
				updateDetail(ctx, detail);
				LOGGER.log(Level.INFO, "UPDATING Mod180Detail");
			}
		}
	}
	
	private static void insertDetail(AONContext ctx, Mod180Detail detail) {
		LOGGER.log(Level.INFO, "INSERTING RECEIVERS BY MOD180 (" + detail.getDocument() + " )");
		ctx.getDslContext().insertInto(FS_MODEL180_DETAIL)
			.set(FS_MODEL180_DETAIL.DOMAIN,detail.getDomain())
			.set(FS_MODEL180_DETAIL.FS_MODEL180,detail.getMod180())
			.set(FS_MODEL180_DETAIL.DOCUMENT,AonStringUtils.substring(detail.getDocument(), 0, 9))
			.set(FS_MODEL180_DETAIL.NAME,AonStringUtils.substring(detail.getName(), 0, 40))
			.set(FS_MODEL180_DETAIL.REPRESENTATIVE_DOCUMENT,AonStringUtils.substring(detail.getRepresentativeDocument(), 0, 9))
			.set(FS_MODEL180_DETAIL.PROVINCE,detail.getProvince())
			.set(FS_MODEL180_DETAIL.INKIND,(byte) (detail.isInKind()?1:0))
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
		LOGGER.log(Level.INFO, "UPDATING RECEIVERS BY MOD180 (" + detail.getDocument() + " )");
		ctx.getDslContext().update(FS_MODEL180_DETAIL)
			.set(FS_MODEL180_DETAIL.DOCUMENT,AonStringUtils.substring(detail.getDocument(), 0, 9))
			.set(FS_MODEL180_DETAIL.NAME,AonStringUtils.substring(detail.getName(), 0, 40))
			.set(FS_MODEL180_DETAIL.REPRESENTATIVE_DOCUMENT,AonStringUtils.substring(detail.getRepresentativeDocument(), 0, 9))
			.set(FS_MODEL180_DETAIL.PROVINCE,detail.getProvince())
			.set(FS_MODEL180_DETAIL.INKIND,(byte) (detail.isInKind()?1:0))
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
		LOGGER.log(Level.INFO, "DELETING RECEIVERS BY ID (" + detail.getDocument() + " )");
		ctx.getDslContext().delete(FS_MODEL180_DETAIL)
			.where(FS_MODEL180_DETAIL.ID.equal(detail.getId()))
			.execute();
	}

	private static void deleteDetails(AONContext ctx, Mod180 mod180) {
		LOGGER.log(Level.INFO, "DELETING RECEIVERS BY MOD180 (" + mod180.getId() + " )");
		ctx.getDslContext().delete(FS_MODEL180_DETAIL)
			.where(FS_MODEL180_DETAIL.FS_MODEL180.equal(mod180.getId()))
			.execute();
	}

	private static void insertDetailsFromInvoice(AONContext ctx , Mod180 mod180) {
		Date firstDay = AonDateUtils.getYearFirstDay(mod180.getYear());
		Date lastDay = AonDateUtils.getYearLastDay(mod180.getYear());

		Field<Integer> minRegistry = DSL.min(INVOICE.REGISTRY).as(INVOICE.REGISTRY.getName());
		Field<BigDecimal> sumBase = DSL.sum(INVOICE_TAX.BASE).as(INVOICE_TAX.BASE.getName());
		Field<Double> invoiceTaxSum = DSL.round( (INVOICE_TAX.BASE.mul(INVOICE_TAX.PERCENTAGE)).div(100) ,2);
		Field<Double> maxPercent = DSL.max(INVOICE_TAX.PERCENTAGE).as(INVOICE_TAX.PERCENTAGE.getName());
		Field<BigDecimal> quotaOp = DSL.sum(DSL.decode()
				.when(INVOICE_TAX.QUOTA.notEqual(0.0), INVOICE_TAX.QUOTA)
				.when(INVOICE_TAX.QUOTA.equal(0.0), invoiceTaxSum)
				.as(INVOICE_TAX.QUOTA.getName()));
		ctx.getDslContext().select(
			INVOICE.RDOCUMENT
			,INVOICE.RNAME
			,minRegistry
			,sumBase
			,quotaOp
			,maxPercent)
		.from(INVOICE)
		.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
		.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
		.where(INVOICE.DOMAIN.equal(mod180.getDomain()))
			.and(INVOICE.TYPE.notEqual((byte) 1))				// No Ventas
			.and(INVOICE_TAX.TAX_TYPE.equal((byte) 2))			// IRPF
			.and(INVOICE_TAX.WITHHOLDING_TYPE.equal((byte) 1))	// IRPF de Alquiler
			.and(INVOICE.ISSUE_DATE.between(AonDateUtils.toSql(firstDay), AonDateUtils.toSql(lastDay)))
		.groupBy(INVOICE.RDOCUMENT,INVOICE.RNAME)
		.fetch()
		.stream()
		.forEach(record -> {
			Mod180Detail detail = new Mod180Detail();
			detail.setDomain(mod180.getDomain());
			detail.setMod180(mod180.getId());
			detail.setDocument(record.getValue(INVOICE.RDOCUMENT));
			detail.setName(record.getValue(INVOICE.RNAME));
			detail.setInKind(false);
			detail.setPerception(record.getValue(sumBase).doubleValue());
			detail.setRetention(record.getValue(quotaOp).doubleValue());
			detail.setPercent(record.getValue(maxPercent));
			ctx.getDslContext()
				.select(GEOZONE.CODE)
				.from(RADDRESS)
				.join(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
				.where(RADDRESS.REGISTRY.equal(record.getValue(minRegistry)))
				.and(RADDRESS.TYPE.equal((byte) 0))		// Dirección principal.
				.limit(1)
				.fetch()
				.stream()
				.forEach(province -> {
					try {
						detail.setProvince(Integer.parseInt(province.getValue(GEOZONE.CODE)));
					} catch (NumberFormatException e) {
						// nothing. Si la clave no es numero, no es provincia válida.
					}
				});
			insertDetail(ctx,detail);
		});
	}

	public static Mod180 initialize(AONContext ctx, int year) {
		Mod180 mod180 = new Mod180();
		FiscalParameters params = AppParamDAO.getFiscalParameters(ctx, year);
		mod180.setEnterprise(params.getCompany());
		mod180.setDomain(ctx.getDomainId());
		mod180.setDocument(params.getDocument());
		mod180.setName(AonStringUtils.left(params.getName(), FS_MODEL180.NAME
				.getDataType().length()));
		mod180.setYear(year);
		mod180.setAdministration(params.getAdministration() != null ? params
				.getAdministration() : 4);
		mod180.setContactPerson(AonStringUtils.left(params.getContactPerson(),
				FS_MODEL180.CONTACT_PERSON.getDataType().length()));
		mod180.setContactPhone(AonStringUtils.left(params.getContactPhone(),
				FS_MODEL180.CONTACT_PHONE.getDataType().length()));
		mod180.setDetails(new ArrayList<Mod180Detail>());
		return mod180;
	}
}
