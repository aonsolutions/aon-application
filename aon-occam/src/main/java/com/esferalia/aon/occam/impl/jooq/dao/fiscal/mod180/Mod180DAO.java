package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod180;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel180.FS_MODEL180;
import static com.esferalia.aon.jooq.tables.FsModel180Detail.FS_MODEL180_DETAIL;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.FsModel180Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod180DAO {
	
	private Mod180DAO() {
	}
	
	private static final byte ZERO_BYTE = 0;

	public static Stream<Mod180> getHeaders(AONContext ctx, int domain) {
		return getHeaders(ctx, domain, null);
	}
	public static Stream<Mod180> getHeaders(AONContext ctx, int domain, Integer scope) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(FS_MODEL180.fields())
			.select(DOMAIN.DESCRIPTION)
			.from(FS_MODEL180)
			.join(DOMAIN).on(FS_MODEL180.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL180.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.and( scope == null ? DSL.trueCondition() : DOMAIN.SCOPE.equal(scope))
			.orderBy(FS_MODEL180.YEAR.desc()
					,FS_MODEL180.NAME.asc()
					,FS_MODEL180.REPLACEMENT.asc())
			.fetch()
			.stream()
			.map(new Mod180Filler())
			;
	}

	public static LinkedList<Mod180> getByDomain(AONContext ctx, int domain) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(FS_MODEL180.fields())
			.select(DOMAIN.DESCRIPTION)
			.from(FS_MODEL180)
			.join(DOMAIN).on(FS_MODEL180.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL180.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.orderBy(FS_MODEL180.YEAR.desc()
					,FS_MODEL180.NAME.asc()
					,FS_MODEL180.REPLACEMENT.asc())
			.fetch()
			.stream()
			.map(new Mod180Filler())
			.map( mod180 -> mod180.setDetails( getDetails(ctx, mod180.getId()) ))
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static Mod180 getById(AONContext ctx, int id) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(FS_MODEL180.fields())
			.select(DOMAIN.DESCRIPTION)
			.from(FS_MODEL180)
			.join(DOMAIN).on(FS_MODEL180.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL180.DOMAIN.equal(ctx.getDomainId()).or(DOMAIN.PARENT.equal(ctx.getDomainId())))
			.and(FS_MODEL180.ID.equal(id))
			.fetch()
			.stream()
			.map(new Mod180Filler())
			.map( mod180 -> mod180.setDetails( getDetails(ctx, mod180.getId()) ))
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
		} catch (Exception t) {
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
		return insert(ctx,mod180,true);
	}

	private static Mod180 insert(AONContext ctx, Mod180 mod180, boolean generateDetails) {
		validate(ctx,mod180);
		FsModel180Record rec = ctx.getDslContext().insertInto(FS_MODEL180)
			.set(FS_MODEL180.DOMAIN,mod180.getDomain())
			.set(FS_MODEL180.ENTERPRISE,mod180.getEnterprise())
			.set(FS_MODEL180.YEAR,mod180.getYear())
			.set(FS_MODEL180.ADMINISTRATION, mod180.getAdministration().value())
			.set(FS_MODEL180.STATUS, ZERO_BYTE )
			.set(FS_MODEL180.SECURITY_LEVEL,AonEnumUtils.getByte(mod180.isConfidential()) ) 
			.set(FS_MODEL180.DOCUMENT,mod180.getDocument())
			.set(FS_MODEL180.NAME,mod180.getName())
			.set(FS_MODEL180.CONTACT_PERSON,mod180.getContactPerson())
			.set(FS_MODEL180.CONTACT_PHONE,mod180.getContactPhone())
			.set(FS_MODEL180.CONTACT_MAIL,mod180.getContactMail())
			.set(FS_MODEL180.COMPLEMENTARY,AonEnumUtils.getByte(mod180.isComplementary()))
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
		mod180.setId(rec.getId());
		if (generateDetails) {
			insertDetailsFromInvoice(ctx, mod180);
		}
		return mod180;
	}

	private static Mod180 update(AONContext ctx, Mod180 mod180) {
		ctx.getDslContext().update(FS_MODEL180)
			.set(FS_MODEL180.YEAR,mod180.getYear())
			.set(FS_MODEL180.ADMINISTRATION,mod180.getAdministration().value())
			.set(FS_MODEL180.STATUS,AonEnumUtils.getByte( mod180.getStatus()  ))
			.set(FS_MODEL180.SECURITY_LEVEL,AonEnumUtils.getByte(mod180.isConfidential()) ) 
			.set(FS_MODEL180.DOCUMENT,mod180.getDocument())
			.set(FS_MODEL180.NAME,mod180.getName())
			.set(FS_MODEL180.CONTACT_PERSON,mod180.getContactPerson())
			.set(FS_MODEL180.CONTACT_PHONE,mod180.getContactPhone())
			.set(FS_MODEL180.CONTACT_MAIL,mod180.getContactMail())
			.set(FS_MODEL180.COMPLEMENTARY,AonEnumUtils.getByte(mod180.isComplementary()))
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
		if (mod180.isReplacement() || mod180.isComplementary()) {
			// Se comprueba que exista la declaración ssustituida.
			if (!ctx.getDslContext().selectOne()
					.from(FS_MODEL180)
					.where(FS_MODEL180.YEAR.equal(mod180.getYear())
					.and(FS_MODEL180.ADMINISTRATION.equal(mod180.getAdministration().value()))
					.and(FS_MODEL180.ENTERPRISE.equal(mod180.getEnterprise())))
					.fetch()
					.stream()
					.findFirst()
					.isPresent()) 
				throw new AonCoreException(
						AonError.FISCAL_NO_REPLACED_DECLARATION.getMessage());
		} else {
			// Se comprueba que no exista ya una declaración.
			if (ctx.getDslContext().selectOne()
				.from(FS_MODEL180)
				.where(FS_MODEL180.YEAR.equal(mod180.getYear())
				.and(FS_MODEL180.ADMINISTRATION.equal(mod180.getAdministration().value()))
				.and(FS_MODEL180.ENTERPRISE.equal(mod180.getEnterprise()))
				.and(FS_MODEL180.REPLACEMENT.equal(ZERO_BYTE))
				.and(FS_MODEL180.COMPLEMENTARY.equal(ZERO_BYTE)))
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
		Field<Double> maxPercent = DSL.max(INVOICE_TAX.PERCENTAGE);
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
			return new Mod180Detail()
				.setDomain(mod180.getDomain())
				.setMod180(mod180.getId())
				.setDocument(rec.getValue(INVOICE.RDOCUMENT))
				.setName(rec.getValue(INVOICE.RNAME))
				.setInKind(false)
				.setPerception(rec.getValue(sumBase).doubleValue())
				.setRetention(rec.getValue(quotaOp).doubleValue())
				.setPercent(rec.getValue(maxPercent))
				.setProvince( RegistryAddressDAO.getMainAddressProvince(ctx, rec.getValue(minRegistry)) );
			})
		.forEach(detail -> insertDetail(ctx,detail));
	}

	public static Mod180 initialize(AONContext ctx, int year) {
		Mod180 mod180 = new Mod180();
		AonConfiguration conf = ConfigurationDAO.getConfiguration(ctx);
		mod180.setEnterprise(conf.getCompany().getId());
		mod180.setDomain(ctx.getDomainId());
		mod180.setDocument(conf.getCompany().getDocument());
		mod180.setName(AonStringUtils.left(conf.getCompany().getName(), FS_MODEL180.NAME
				.getDataType().length()));
		mod180.setYear(year);
		mod180.setAdministration(conf.fiscal().getAdministration(Administration.COMMON_TERRITORY));
		mod180.setContactPerson(AonStringUtils.left(conf.fiscal().getContactPerson(),
				FS_MODEL180.CONTACT_PERSON.getDataType().length()));
		mod180.setContactPhone(AonStringUtils.left(conf.fiscal().getContactPhone(),
				FS_MODEL180.CONTACT_PHONE.getDataType().length()));
		mod180.setContactMail(AonStringUtils.left(conf.fiscal().getContactMail(),
				FS_MODEL180.CONTACT_MAIL.getDataType().length()));
		mod180.setReceipt("1800000000001");
		mod180.setStatus(FiscalStatus.PENDING);
		mod180.setDetails(new LinkedList<>());
		return mod180;
	}
	
	private static class Mod180Filler implements Function<Record, Mod180> {

		@Override
		public Mod180 apply(Record rec) {
			return new Mod180() 
				.setId(rec.getValue(FS_MODEL180.ID))
				.setDomain(rec.getValue(FS_MODEL180.DOMAIN))
				.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
				.setEnterprise(rec.getValue(FS_MODEL180.ENTERPRISE))
				.setYear(rec.getValue(FS_MODEL180.YEAR))
				.setAdministration( com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Administration.class,rec.getValue(FS_MODEL180.ADMINISTRATION)))
				.setReplacement( rec.getValue(FS_MODEL180.REPLACEMENT)==1 )
				.setComplementary(rec.getValue(FS_MODEL180.COMPLEMENTARY)==1 )
				.setStatus(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(FiscalStatus.class,rec.getValue(FS_MODEL180.STATUS)))
				.setDocument(rec.getValue(FS_MODEL180.DOCUMENT))
				.setName(rec.getValue(FS_MODEL180.NAME))
				.setContactPerson(rec.getValue(FS_MODEL180.CONTACT_PERSON))
				.setContactPhone(rec.getValue(FS_MODEL180.CONTACT_PHONE))
				.setContactMail(rec.getValue(FS_MODEL180.CONTACT_MAIL))				
				.setReceipt(rec.getValue(FS_MODEL180.RECEIPT))
				.setReplacedReceipt(rec.getValue(FS_MODEL180.REPLACED_RECEIPT))
				.setReceiverCountTotal( rec.getValue(FS_MODEL180.RECEIVER_COUNT_TOTAL))
				.setReceiptTotal(rec.getValue(FS_MODEL180.RECEIPT_TOTAL))
				.setRetentionTotal(rec.getValue(FS_MODEL180.RETENTION_TOTAL))
				.setComments(rec.getValue(FS_MODEL180.COMMENTS))
				.setCreationUser(rec.getValue(FS_MODEL180.CREATION_USER))
				.setCreationDate(rec.getValue(FS_MODEL180.CREATION_DATE))
				.setModificationUser(rec.getValue(FS_MODEL180.MODIFICATION_USER))
				.setModificationDate(rec.getValue(FS_MODEL180.MODIFICATION_DATE))
				;
		}
	}

	private static class Mod180DetailFiller implements Function<Record, Mod180Detail> {

		@Override
		public Mod180Detail apply(Record rec) {
			return new Mod180Detail()
				.setId(rec.getValue(FS_MODEL180_DETAIL.ID))
				.setDocument(rec.getValue(FS_MODEL180_DETAIL.DOCUMENT))
				.setName(rec.getValue(FS_MODEL180_DETAIL.NAME))
				.setRepresentativeDocument(rec.getValue(FS_MODEL180_DETAIL.REPRESENTATIVE_DOCUMENT))
				.setProvince(rec.getValue(FS_MODEL180_DETAIL.PROVINCE))
				.setInKind(rec.getValue(FS_MODEL180_DETAIL.INKIND)==1)
				.setPerception(rec.getValue(FS_MODEL180_DETAIL.PERCEPTION))
				.setRetention(rec.getValue(FS_MODEL180_DETAIL.RETENTION))
				.setPercent(rec.getValue(FS_MODEL180_DETAIL.PERCENTAGE))
				.setAccrualYear(rec.getValue(FS_MODEL180_DETAIL.ACCRUAL_YEAR))
				.setLocation(rec.getValue(FS_MODEL180_DETAIL.LOCATION))
				.setCadasdralReference(rec.getValue(FS_MODEL180_DETAIL.CADASDRAL_REFERENCE))
				.setStreetType(rec.getValue(FS_MODEL180_DETAIL.STREET_TYPE))
				.setStreetName(rec.getValue(FS_MODEL180_DETAIL.STREET_NAME))
				.setNumberType(rec.getValue(FS_MODEL180_DETAIL.NUMBER_TYPE))
				.setNumber(rec.getValue(FS_MODEL180_DETAIL.NUMBER))
				.setNumberSuffix(rec.getValue(FS_MODEL180_DETAIL.NUMBER_SUFFIX))
				.setBlock(rec.getValue(FS_MODEL180_DETAIL.BLOCK))
				.setHall(rec.getValue(FS_MODEL180_DETAIL.HALL))
				.setStair(rec.getValue(FS_MODEL180_DETAIL.STAIR))
				.setFloor(rec.getValue(FS_MODEL180_DETAIL.FLOOR))
				.setDoor(rec.getValue(FS_MODEL180_DETAIL.DOOR))
				.setComplement(rec.getValue(FS_MODEL180_DETAIL.COMPLEMENT))
				.setCity(rec.getValue(FS_MODEL180_DETAIL.CITY))
				.setTown(rec.getValue(FS_MODEL180_DETAIL.TOWN))
				.setTownCode(rec.getValue(FS_MODEL180_DETAIL.TOWN_CODE))
				.setProvinceCode(rec.getValue(FS_MODEL180_DETAIL.PROVINCE_CODE))
				.setZip(rec.getValue(FS_MODEL180_DETAIL.ZIP));
		}
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
		} catch (Exception t) {
			throw new AonCoreException(t.getMessage());
		}
	}

	public static Mod180 duplicate(AONContext ctx, Mod180 mod180) {		
		int id = mod180.getId();
		mod180.setId(null);
		mod180 = insert(ctx, mod180, false);
		
		// Si la nueva es complementaria, no se duplican las lineas
		if (!mod180.isComplementary()) {
			Mod180 original = getById(ctx, id);
			if (original != null && original.getDetails() != null) {
				for (Mod180Detail detail : original.getDetails()) {
					detail.setId(null);
					detail.setMod180(mod180.getId());
					saveDetail(ctx,mod180,detail);
				}
			}
		}
		
		return getById(ctx, mod180.getId());
	}
	
    // Grabar resultado y pdf en response y marcar el modelo como enviado
	public static Mod180 aeatPresentation(AONContext ctx, Mod180 mod, String aeatResponse) {
		if (AonStringUtils.isNotBlank(aeatResponse)) {			
			
			// Antes de nada se borra la presentación anterior
			DataResponseDAO.deleteAEATResponse(ctx, mod);			
			
			// Grabar los datos en data_response y sus tablas asociadas
			DataResponseDAO.insertAEATResponse(ctx, mod, aeatResponse);
			
			// Marcar el modelo como enviado
			//AEATResponse response = AEATJson.toJSON(aeatResponse.getBytes());		
			if (mod != null && mod.getId() != null) {
				ctx.getDslContext().update(FS_MODEL180)
					//.set(FS_MODEL180.RECEIPT, response.getJustificante()) // FALTA - LA PRESENTACION NO DEVUELVE EL NUMERO JUSTIFICANTE POR ESO LO UNICO QUE SE HACE ES MARCARLO COMO ENVIADO
					.set(FS_MODEL180.STATUS, FiscalStatus.SENT.value())
					.where(FS_MODEL180.ID.equal(mod.getId()))
					.execute();
				return getById(ctx, mod.getId());
			}
		}
		return mod;
	}
	
}
