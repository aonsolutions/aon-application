
package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel184.FS_MODEL184;
import static com.esferalia.aon.jooq.tables.FsModel184Detail.FS_MODEL184_DETAIL;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import com.esferalia.aon.jooq.tables.records.FsModel184Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Income;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Partner;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod184DAO {
	private static byte ZERO_BYTE = 0;

	public static LinkedList<Mod184> getByDomain(AONContext ctx, int domain) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(FS_MODEL184.fields())
			.from(FS_MODEL184)
			.join(DOMAIN).on(FS_MODEL184.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL184.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.orderBy(FS_MODEL184.YEAR.desc()
					,FS_MODEL184.NAME.asc()
					,FS_MODEL184.REPLACEMENT.asc())
			.fetch()
			.stream()
			.map(new Mod184Filler())
			.peek( mod184 -> mod184.setPartners( getPartners(ctx, mod184.getId()) ))
			.peek( mod184 -> mod184.setIncomes ( getIncomes(ctx, mod184.getId()) ))
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static Mod184 getById(AONContext ctx, int id) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(FS_MODEL184.fields())
			.from(FS_MODEL184)
			.join(DOMAIN).on(FS_MODEL184.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL184.DOMAIN.equal(ctx.getDomainId()).or(DOMAIN.PARENT.equal(ctx.getDomainId())))
			.and(FS_MODEL184.ID.equal(id))
			.fetch()
			.stream()
			.map(new Mod184Filler())
			.peek( mod184 -> mod184.setPartners( getPartners(ctx, mod184.getId()) ))
			.peek( mod184 -> mod184.setIncomes ( getIncomes(ctx, mod184.getId()) ))
			.findFirst()
			.orElse(null);
	}
	
	public static LinkedList<Mod184Partner> getPartners(AONContext ctx, int mod184) {
		ctx.checkRead();
		return ctx.getDslContext()
			.selectFrom(FS_MODEL184_DETAIL)
			.where(FS_MODEL184_DETAIL.FS_MODEL184.equal(mod184))
			.and(FS_MODEL184_DETAIL.TYPE.eq("P"))
			.fetch()
			.stream()
			.map( new Mod184PartnerFiller() )
			.collect(Collectors.toCollection(LinkedList::new));
	}
	public static LinkedList<Mod184Income> getIncomes(AONContext ctx, int mod184) {
		ctx.checkRead();
		return ctx.getDslContext()
			.selectFrom(FS_MODEL184_DETAIL)
			.where(FS_MODEL184_DETAIL.FS_MODEL184.equal(mod184))
			.and(FS_MODEL184_DETAIL.TYPE.eq("I"))
			.fetch()
			.stream()
			.map( new Mod184IncomeFiller() )
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static Mod184 saveComments(AONContext ctx, Mod184 fm) {
		try {
			ctx.checkWrite();
			if (fm.getId() != null) {
				ctx.getDslContext().update(FS_MODEL184)
					.set(FS_MODEL184.COMMENTS,fm.getComments())
					.where(FS_MODEL184.ID.equal(fm.getId()))
					.execute();
			}
			return fm;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Throwable t) {
			throw new AonCoreException(t.getMessage());
		}
	}

	public static Mod184 save(AONContext ctx, Mod184 mod184) {
		ctx.checkWrite();
		if (mod184.getId() == null) {
			mod184 = insert(ctx, mod184);
		} else {
			mod184 = update(ctx, mod184);
			for (Mod184Income income : mod184.getIncomes()) {
				saveIncome(ctx, mod184, income);
			}
			for (Mod184Partner partner : mod184.getPartners()) {
				savePartner(ctx, mod184, partner);
			}
		}
		return getById(ctx, mod184.getId());
	}

	private static Mod184 insert(AONContext ctx, Mod184 mod184) {
		validate(ctx, mod184);
		FsModel184Record record = ctx.getDslContext().insertInto(FS_MODEL184)
				.set(FS_MODEL184.DOMAIN,mod184.getDomain())
				.set(FS_MODEL184.ENTERPRISE,mod184.getEnterprise())
				.set(FS_MODEL184.YEAR,mod184.getYear())
				.set(FS_MODEL184.ADMINISTRATION, mod184.getAdministration().getValue())
				.set(FS_MODEL184.STATUS, ZERO_BYTE )
				.set(FS_MODEL184.SECURITY_LEVEL,AonEnumUtils.getByte(mod184.isConfidential()) ) 
				.set(FS_MODEL184.DOCUMENT,mod184.getDocument())
				.set(FS_MODEL184.NAME,mod184.getName())
				.set(FS_MODEL184.CONTACT_PERSON,mod184.getContactPerson())
				.set(FS_MODEL184.CONTACT_PHONE,mod184.getContactPhone())
				.set(FS_MODEL184.CONTACT_MAIL,mod184.getContactMail())
				.set(FS_MODEL184.COMPLEMENTARY, (byte) 0)
				.set(FS_MODEL184.REPLACEMENT,AonEnumUtils.getByte(mod184.isReplacement()))
				.set(FS_MODEL184.COMMENTS,mod184.getComments())
				.set(FS_MODEL184.RECEIPT,mod184.getReceipt())
				.set(FS_MODEL184.REPLACED_RECEIPT,mod184.getReplacedReceipt())
				.set(FS_MODEL184.ENTITY_TYPE,mod184.getEntityType())
				.set(FS_MODEL184.MAIN_ACTIVITY,mod184.getMainActivity())
				.set(FS_MODEL184.FOREIGN_ENTITY_TYPE,mod184.getForeignEntityType())
				.set(FS_MODEL184.FOREIGN_OBJECT,mod184.getForeignObject())
				.set(FS_MODEL184.COUNTRY,mod184.getCountry())
				.set(FS_MODEL184.RESIDENT_PERCENT,mod184.getResidentPercent())
				.set(FS_MODEL184.TAX_IS,AonEnumUtils.getByte( mod184.isTaxIS()))
				.set(FS_MODEL184.NET_SALES_AMOUNT,mod184.getNetSalesAmount())
				.set(FS_MODEL184.LRDOCUMENT,mod184.getLrDocument())
				.set(FS_MODEL184.LRNAME,mod184.getLrName())
				.set(FS_MODEL184.CREATION_USER,ctx.getUser())
				.set(FS_MODEL184.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.returning(FS_MODEL184.ID)
			.fetchOne();
		mod184.setId(record.getId());
		return mod184;
	}

	private static Mod184 update(AONContext ctx, Mod184 mod184) {
		ctx.getDslContext().update(FS_MODEL184)
			.set(FS_MODEL184.YEAR,mod184.getYear())
			.set(FS_MODEL184.ADMINISTRATION,mod184.getAdministration().getValue())
			.set(FS_MODEL184.STATUS,AonEnumUtils.getByte( mod184.getStatus()  ))
			.set(FS_MODEL184.SECURITY_LEVEL,AonEnumUtils.getByte(mod184.isConfidential()) ) 
			.set(FS_MODEL184.DOCUMENT,mod184.getDocument())
			.set(FS_MODEL184.NAME,mod184.getName())
			.set(FS_MODEL184.CONTACT_PERSON,mod184.getContactPerson())
			.set(FS_MODEL184.CONTACT_PHONE,mod184.getContactPhone())
			.set(FS_MODEL184.CONTACT_MAIL,mod184.getContactMail())
			.set(FS_MODEL184.COMPLEMENTARY, (byte) 0)
			.set(FS_MODEL184.REPLACEMENT,AonEnumUtils.getByte(mod184.isReplacement()))
			.set(FS_MODEL184.COMMENTS,mod184.getComments())
			.set(FS_MODEL184.RECEIPT,mod184.getReceipt())
			.set(FS_MODEL184.REPLACED_RECEIPT,mod184.getReplacedReceipt())
			.set(FS_MODEL184.ENTITY_TYPE,mod184.getEntityType())
			.set(FS_MODEL184.MAIN_ACTIVITY,mod184.getMainActivity())
			.set(FS_MODEL184.FOREIGN_ENTITY_TYPE,mod184.getForeignEntityType())
			.set(FS_MODEL184.FOREIGN_OBJECT,mod184.getForeignObject())
			.set(FS_MODEL184.COUNTRY,mod184.getCountry())
			.set(FS_MODEL184.RESIDENT_PERCENT,mod184.getResidentPercent())
			.set(FS_MODEL184.TAX_IS,AonEnumUtils.getByte( mod184.isTaxIS()))
			.set(FS_MODEL184.NET_SALES_AMOUNT,mod184.getNetSalesAmount())
			.set(FS_MODEL184.LRDOCUMENT,mod184.getLrDocument())
			.set(FS_MODEL184.LRNAME,mod184.getLrName())
			.set(FS_MODEL184.MODIFICATION_USER,ctx.getUser())
			.set(FS_MODEL184.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(FS_MODEL184.ID.equal(mod184.getId()))
			.execute();
		return mod184;
	}

	private static void saveIncome(AONContext ctx, Mod184 mod184, Mod184Income income) {
		ctx.checkWrite();
		if (income.getId() == null) {
			if (!income.isDeleted()) {
				income.setDomain(mod184.getDomain());
				income.setMod184(mod184.getId());
				insertIncome(ctx, income);
			}
		} else {
			if (income.isDeleted()) {
				deleteIncome(ctx, income);
			} else {
				updateIncome(ctx, income);
			}
		}
	}

	private static void insertIncome(AONContext ctx, Mod184Income income) {
		ctx.getDslContext().insertInto(FS_MODEL184_DETAIL)
			.set(FS_MODEL184_DETAIL.DOMAIN,income.getDomain())
			.set(FS_MODEL184_DETAIL.FS_MODEL184,income.getMod184())
			.set(FS_MODEL184_DETAIL.TYPE, "I" )
			.set(FS_MODEL184_DETAIL.KEY, income.getKey())
			.set(FS_MODEL184_DETAIL.SUBKEY, income.getSubKey())
			.set(FS_MODEL184_DETAIL.COUNTRY,income.getCountry())
			.set(FS_MODEL184_DETAIL.REGIME,income.getRegime())
			.set(FS_MODEL184_DETAIL.ACTIVITY_TYPE,income.getActivityType())
			.set(FS_MODEL184_DETAIL.EPIGRAPH,income.getEpigraph())
			.set(FS_MODEL184_DETAIL.GRANTEE_DOCUMENT,AonStringUtils.substring(income.getGranteeDocument(), 0, 9))
			.set(FS_MODEL184_DETAIL.GRANTEE_NAME,AonStringUtils.substring(income.getGranteeName(), 0, 40))
			.set(FS_MODEL184_DETAIL.ADQ_DATE, AonDateUtils.toSql( income.getAdqDate() ))
			.set(FS_MODEL184_DETAIL.INCREASE,income.getIncrease())
			.set(FS_MODEL184_DETAIL.DECREASE,income.getDecrease())
			.set(FS_MODEL184_DETAIL.ACCOUNTING_RESULT,income.getAccountingResult())
			.set(FS_MODEL184_DETAIL.EXPENSES,income.getExpenses())
			.set(FS_MODEL184_DETAIL.NET_YIELD,income.getNetYield())
			.set(FS_MODEL184_DETAIL.REDUCTION_PERCENT,income.getReductionPercent())
			.set(FS_MODEL184_DETAIL.DEDUCTION_RIGHT_RENT,income.getDeductionRightRent())
			.set(FS_MODEL184_DETAIL.RESULT,income.getResult())
			.set(FS_MODEL184_DETAIL.DEDUCTION_BASE,income.getDeductionBase())
			.set(FS_MODEL184_DETAIL.RETENTION,income.getRetention())
			.set(FS_MODEL184_DETAIL.LOCATION,income.getLocation())
			.set(FS_MODEL184_DETAIL.CADASDRAL_REFERENCE,income.getCadasdralReference())
			.set(FS_MODEL184_DETAIL.STAFF_EXPENSES,income.getStaffExpenses())
			.set(FS_MODEL184_DETAIL.ASSET_ACQUISITION,income.getAssetAcquisition())
			.set(FS_MODEL184_DETAIL.TAX_DEDUCTION,income.getTaxDeduction())
			.set(FS_MODEL184_DETAIL.OTHER_TAX_DEDUCTION,income.getOtherTaxDeduction())
			.set(FS_MODEL184_DETAIL.VAT_ACCRUAL_PAYMENT,AonEnumUtils.getByte( income.isVatAccrualPayment()))
			.execute();
	}

	private static void updateIncome(AONContext ctx, Mod184Income income) {
		ctx.getDslContext().update(FS_MODEL184_DETAIL)
			.set(FS_MODEL184_DETAIL.KEY, income.getKey())
			.set(FS_MODEL184_DETAIL.SUBKEY, income.getSubKey())
			.set(FS_MODEL184_DETAIL.COUNTRY,income.getCountry())
			.set(FS_MODEL184_DETAIL.REGIME,income.getRegime())
			.set(FS_MODEL184_DETAIL.ACTIVITY_TYPE,income.getActivityType())
			.set(FS_MODEL184_DETAIL.EPIGRAPH,income.getEpigraph())
			.set(FS_MODEL184_DETAIL.GRANTEE_DOCUMENT,AonStringUtils.substring(income.getGranteeDocument(), 0, 9))
			.set(FS_MODEL184_DETAIL.GRANTEE_NAME,AonStringUtils.substring(income.getGranteeName(), 0, 40))
			.set(FS_MODEL184_DETAIL.ADQ_DATE, AonDateUtils.toSql( income.getAdqDate() ))
			.set(FS_MODEL184_DETAIL.INCREASE,income.getIncrease())
			.set(FS_MODEL184_DETAIL.DECREASE,income.getDecrease())
			.set(FS_MODEL184_DETAIL.ACCOUNTING_RESULT,income.getAccountingResult())
			.set(FS_MODEL184_DETAIL.EXPENSES,income.getExpenses())
			.set(FS_MODEL184_DETAIL.NET_YIELD,income.getNetYield())
			.set(FS_MODEL184_DETAIL.REDUCTION_PERCENT,income.getReductionPercent())
			.set(FS_MODEL184_DETAIL.DEDUCTION_RIGHT_RENT,income.getDeductionRightRent())
			.set(FS_MODEL184_DETAIL.RESULT,income.getResult())
			.set(FS_MODEL184_DETAIL.DEDUCTION_BASE,income.getDeductionBase())
			.set(FS_MODEL184_DETAIL.RETENTION,income.getRetention())
			.set(FS_MODEL184_DETAIL.LOCATION,income.getLocation())
			.set(FS_MODEL184_DETAIL.CADASDRAL_REFERENCE,income.getCadasdralReference())
			.set(FS_MODEL184_DETAIL.STAFF_EXPENSES,income.getStaffExpenses())
			.set(FS_MODEL184_DETAIL.ASSET_ACQUISITION,income.getAssetAcquisition())
			.set(FS_MODEL184_DETAIL.TAX_DEDUCTION,income.getTaxDeduction())
			.set(FS_MODEL184_DETAIL.OTHER_TAX_DEDUCTION,income.getOtherTaxDeduction())
			.set(FS_MODEL184_DETAIL.VAT_ACCRUAL_PAYMENT,AonEnumUtils.getByte( income.isVatAccrualPayment()))
			.where(FS_MODEL184_DETAIL.ID.equal(income.getId()))
			.execute();
	}
	
	private static void deleteIncome(AONContext ctx, Mod184Income detail) {
		ctx.getDslContext().delete(FS_MODEL184_DETAIL)
			.where(FS_MODEL184_DETAIL.ID.equal(detail.getId()))
			.execute();
	}

	private static void savePartner(AONContext ctx, Mod184 mod184, Mod184Partner partner) {
		ctx.checkWrite();
		if (partner.getId() == null ) {
			if (!partner.isDeleted()) {
				partner.setDomain(mod184.getDomain());
				partner.setMod184(mod184.getId());
				insertPartner(ctx, partner);
			}
		} else {
			if (partner.isDeleted()) {
				deletePartner(ctx, partner);
			} else {
				updatePartner(ctx, partner);
			}
		}
	}

	private static void insertPartner(AONContext ctx, Mod184Partner partner) {
		ctx.getDslContext().insertInto(FS_MODEL184_DETAIL)
			.set(FS_MODEL184_DETAIL.DOMAIN,partner.getDomain())
			.set(FS_MODEL184_DETAIL.FS_MODEL184,partner.getMod184())
			.set(FS_MODEL184_DETAIL.TYPE, "P")
			.set(FS_MODEL184_DETAIL.DOCUMENT,AonStringUtils.substring(partner.getDocument(), 0, 9))
			.set(FS_MODEL184_DETAIL.REPRESENTATIVE_DOCUMENT,AonStringUtils.substring(partner.getRepresentativeDocument(), 0, 9))
			.set(FS_MODEL184_DETAIL.NAME,AonStringUtils.substring(partner.getName(), 0, 40))
			.set(FS_MODEL184_DETAIL.PROVINCE,partner.getProvince())
			.set(FS_MODEL184_DETAIL.COUNTRY,partner.getCountry())
			.set(FS_MODEL184_DETAIL.PART_TYPE,partner.getPartType())
			.set(FS_MODEL184_DETAIL.MEMBER_END_OF_YEAR,AonEnumUtils.getByte( partner.isMemberEndOfYear()))
			.set(FS_MODEL184_DETAIL.MEMBER_DAYS,partner.getMemberDays())
			.set(FS_MODEL184_DETAIL.PART_PERCENT,partner.getPartPercent())
			.set(FS_MODEL184_DETAIL.KEY, partner.getKey())
			.set(FS_MODEL184_DETAIL.SUBKEY, partner.getSubKey())
			.set(FS_MODEL184_DETAIL.AMOUNT,partner.getAmount())
			.set(FS_MODEL184_DETAIL.REDUCTION,partner.getReduction())
			.set(FS_MODEL184_DETAIL.EXPENSES,partner.getExpenses())
			.set(FS_MODEL184_DETAIL.ADDRESS,partner.getAddress())
			.set(FS_MODEL184_DETAIL.NATURE,partner.getNature())
			.set(FS_MODEL184_DETAIL.LOCATION,partner.getLocation())
			.set(FS_MODEL184_DETAIL.CADASDRAL_REFERENCE,partner.getCadasdralReference())
			.set(FS_MODEL184_DETAIL.DECLARED_KEY,partner.getDeclaredKey())
			.set(FS_MODEL184_DETAIL.ASSET_PERCENT,partner.getAssetPercent())
			.execute();
	}

	private static void updatePartner(AONContext ctx, Mod184Partner partner) {
		ctx.getDslContext().update(FS_MODEL184_DETAIL)
			.set(FS_MODEL184_DETAIL.DOCUMENT,AonStringUtils.substring(partner.getDocument(), 0, 9))
			.set(FS_MODEL184_DETAIL.REPRESENTATIVE_DOCUMENT,AonStringUtils.substring(partner.getRepresentativeDocument(), 0, 9))
			.set(FS_MODEL184_DETAIL.NAME,AonStringUtils.substring(partner.getName(), 0, 40))
			.set(FS_MODEL184_DETAIL.PROVINCE,partner.getProvince())
			.set(FS_MODEL184_DETAIL.COUNTRY,partner.getCountry())
			.set(FS_MODEL184_DETAIL.PART_TYPE,partner.getPartType())
			.set(FS_MODEL184_DETAIL.MEMBER_END_OF_YEAR,AonEnumUtils.getByte( partner.isMemberEndOfYear()))
			.set(FS_MODEL184_DETAIL.MEMBER_DAYS,partner.getMemberDays())
			.set(FS_MODEL184_DETAIL.PART_PERCENT,partner.getPartPercent())
			.set(FS_MODEL184_DETAIL.KEY, partner.getKey())
			.set(FS_MODEL184_DETAIL.SUBKEY, partner.getSubKey())
			.set(FS_MODEL184_DETAIL.AMOUNT,partner.getAmount())
			.set(FS_MODEL184_DETAIL.REDUCTION,partner.getReduction())
			.set(FS_MODEL184_DETAIL.EXPENSES,partner.getExpenses())
			.set(FS_MODEL184_DETAIL.ADDRESS,partner.getAddress())
			.set(FS_MODEL184_DETAIL.NATURE,partner.getNature())
			.set(FS_MODEL184_DETAIL.LOCATION,partner.getLocation())
			.set(FS_MODEL184_DETAIL.CADASDRAL_REFERENCE,partner.getCadasdralReference())
			.set(FS_MODEL184_DETAIL.DECLARED_KEY,partner.getDeclaredKey())
			.set(FS_MODEL184_DETAIL.ASSET_PERCENT,partner.getAssetPercent())
			.where(FS_MODEL184_DETAIL.ID.equal(partner.getId()))
			.execute();
	}
	
	private static void deletePartner(AONContext ctx, Mod184Partner partner) {
		ctx.getDslContext().delete(FS_MODEL184_DETAIL)
			.where(FS_MODEL184_DETAIL.ID.equal(partner.getId()))
			.execute();
	}

	private static void validate(AONContext ctx, Mod184 mod184) {
	}

	public static void delete(AONContext ctx, Mod184 mod184) {
		ctx.checkWrite();
		deleteDetails(ctx, mod184);
		ctx.getDslContext().delete(FS_MODEL184)
			.where(FS_MODEL184.ID.equal(mod184.getId()))
			.execute();
	}

	private static void deleteDetails(AONContext ctx, Mod184 mod184) {
		ctx.getDslContext().delete(FS_MODEL184_DETAIL)
			.where(FS_MODEL184_DETAIL.FS_MODEL184.equal(mod184.getId()))
			.execute();
	}

	private static class Mod184Filler implements Function<Record, Mod184> {

		@Override
		public Mod184 apply(Record record) {
			return new Mod184()
				.setId(record.getValue(FS_MODEL184.ID))
				.setDomain(record.getValue(FS_MODEL184.DOMAIN))
				.setEnterprise(record.getValue(FS_MODEL184.ENTERPRISE))
				.setYear(record.getValue(FS_MODEL184.YEAR))
				.setAdministration( com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Administration.class,record.getValue(FS_MODEL184.ADMINISTRATION)))
				.setReplacement( record.getValue(FS_MODEL184.REPLACEMENT)==1 )
				.setComplementary(record.getValue(FS_MODEL184.COMPLEMENTARY)==1 )
				.setStatus(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(FiscalStatus.class,record.getValue(FS_MODEL184.STATUS)))
				.setDocument(record.getValue(FS_MODEL184.DOCUMENT))
				.setName(record.getValue(FS_MODEL184.NAME))
				.setContactPerson(record.getValue(FS_MODEL184.CONTACT_PERSON))
				.setContactPhone(record.getValue(FS_MODEL184.CONTACT_PHONE))
				.setContactMail(record.getValue(FS_MODEL184.CONTACT_MAIL))
				.setReceipt(record.getValue(FS_MODEL184.RECEIPT))
				.setReplacedReceipt(record.getValue(FS_MODEL184.REPLACED_RECEIPT))
				.setComments(record.getValue(FS_MODEL184.COMMENTS))
				.setEntityType(record.getValue(FS_MODEL184.ENTITY_TYPE))
				.setMainActivity(record.getValue(FS_MODEL184.MAIN_ACTIVITY))
				.setForeignEntityType(record.getValue(FS_MODEL184.FOREIGN_ENTITY_TYPE))
				.setForeignObject(record.getValue(FS_MODEL184.FOREIGN_OBJECT))
				.setCountry(record.getValue(FS_MODEL184.COUNTRY))
				.setResidentPercent(record.getValue(FS_MODEL184.RESIDENT_PERCENT))
				.setTaxIS(AonEnumUtils.getBoolean( record.getValue(FS_MODEL184.TAX_IS)) )
				.setNetSalesAmount(record.getValue(FS_MODEL184.NET_SALES_AMOUNT))
				.setLrDocument(record.getValue(FS_MODEL184.LRDOCUMENT))
				.setLrName(record.getValue(FS_MODEL184.LRNAME))
				.setCreationUser(record.getValue(FS_MODEL184.CREATION_USER))
				.setCreationDate(record.getValue(FS_MODEL184.CREATION_DATE))
				.setModificationUser(record.getValue(FS_MODEL184.MODIFICATION_USER))
				.setModificationDate(record.getValue(FS_MODEL184.MODIFICATION_DATE));
		}
	}
			
	private static class Mod184IncomeFiller implements Function<Record, Mod184Income> {

		@Override
		public Mod184Income apply(Record record) {
			return new Mod184Income()
				.setId(record.getValue(FS_MODEL184_DETAIL.ID))
				.setKey(record.getValue(FS_MODEL184_DETAIL.KEY))
				.setSubKey(record.getValue(FS_MODEL184_DETAIL.SUBKEY))
				.setCountry(record.getValue(FS_MODEL184_DETAIL.COUNTRY))
				.setRegime(record.getValue(FS_MODEL184_DETAIL.REGIME))
				.setActivityType(record.getValue(FS_MODEL184_DETAIL.ACTIVITY_TYPE))
				.setEpigraph(record.getValue(FS_MODEL184_DETAIL.EPIGRAPH))
				.setGranteeDocument(record.getValue(FS_MODEL184_DETAIL.GRANTEE_DOCUMENT))
				.setGranteeName(record.getValue(FS_MODEL184_DETAIL.GRANTEE_NAME))
				.setAdqDate(record.getValue(FS_MODEL184_DETAIL.ADQ_DATE))
				.setIncrease(record.getValue(FS_MODEL184_DETAIL.INCREASE))
				.setDecrease(record.getValue(FS_MODEL184_DETAIL.DECREASE))
				.setAccountingResult(record.getValue(FS_MODEL184_DETAIL.ACCOUNTING_RESULT))
				.setExpenses(record.getValue(FS_MODEL184_DETAIL.EXPENSES))
				.setNetYield(record.getValue(FS_MODEL184_DETAIL.NET_YIELD))
				.setReductionPercent(record.getValue(FS_MODEL184_DETAIL.REDUCTION_PERCENT))
				.setDeductionRightRent(record.getValue(FS_MODEL184_DETAIL.DEDUCTION_RIGHT_RENT))
				.setResult(record.getValue(FS_MODEL184_DETAIL.RESULT))
				.setDeductionBase(record.getValue(FS_MODEL184_DETAIL.DEDUCTION_BASE))
				.setRetention(record.getValue(FS_MODEL184_DETAIL.RETENTION))
				.setLocation(record.getValue(FS_MODEL184_DETAIL.LOCATION))
				.setCadasdralReference(record.getValue(FS_MODEL184_DETAIL.CADASDRAL_REFERENCE))
				.setStaffExpenses(record.getValue(FS_MODEL184_DETAIL.STAFF_EXPENSES))
				.setAssetAcquisition(record.getValue(FS_MODEL184_DETAIL.ASSET_ACQUISITION))
				.setTaxDeduction(record.getValue(FS_MODEL184_DETAIL.TAX_DEDUCTION))
				.setOtherTaxDeduction(record.getValue(FS_MODEL184_DETAIL.OTHER_TAX_DEDUCTION))
				.setVatAccrualPayment(AonEnumUtils.getBoolean(record.getValue(FS_MODEL184_DETAIL.VAT_ACCRUAL_PAYMENT)))
				;
		}
	}

	
	private static class Mod184PartnerFiller implements Function<Record, Mod184Partner> {

		@Override
		public Mod184Partner apply(Record record) {
			return new Mod184Partner()
				.setId(record.getValue(FS_MODEL184_DETAIL.ID))
				.setDocument(record.getValue(FS_MODEL184_DETAIL.DOCUMENT))
				.setRepresentativeDocument(record.getValue(FS_MODEL184_DETAIL.REPRESENTATIVE_DOCUMENT))
				.setName(record.getValue(FS_MODEL184_DETAIL.NAME))
				.setProvince(record.getValue(FS_MODEL184_DETAIL.PROVINCE))
				.setCountry(record.getValue(FS_MODEL184_DETAIL.COUNTRY))
				.setPartType(record.getValue(FS_MODEL184_DETAIL.PART_TYPE))
				.setMemberEndOfYear(AonEnumUtils.getBoolean( record.getValue(FS_MODEL184_DETAIL.MEMBER_END_OF_YEAR)))
				.setMemberDays(record.getValue(FS_MODEL184_DETAIL.MEMBER_DAYS))
				.setPartPercent(record.getValue(FS_MODEL184_DETAIL.PART_PERCENT))
				.setKey(record.getValue(FS_MODEL184_DETAIL.KEY))
				.setSubKey(record.getValue(FS_MODEL184_DETAIL.SUBKEY))
				.setAmount(record.getValue(FS_MODEL184_DETAIL.AMOUNT))
				.setReduction(record.getValue(FS_MODEL184_DETAIL.REDUCTION))
				.setAddress(record.getValue(FS_MODEL184_DETAIL.ADDRESS))
				.setExpenses(AonNumberUtils.zeroIfNull(record.getValue(FS_MODEL184_DETAIL.EXPENSES)))
				.setNature(record.getValue(FS_MODEL184_DETAIL.NATURE))
				.setLocation(record.getValue(FS_MODEL184_DETAIL.LOCATION))
				.setCadasdralReference(record.getValue(FS_MODEL184_DETAIL.CADASDRAL_REFERENCE))
				.setDeclaredKey(record.getValue(FS_MODEL184_DETAIL.DECLARED_KEY))
				.setAssetPercent(record.getValue(FS_MODEL184_DETAIL.ASSET_PERCENT))
				;
			
		}
	}

	public static Mod184 initialize(AONContext ctx, int year) {
		Mod184 mod184 = new Mod184();
		FiscalParameters params = AppParamDAO.getFiscalParameters(ctx);
		mod184.setEnterprise(params.getCompany());
		mod184.setDomain(ctx.getDomainId());
		mod184.setDocument(params.getDocument());
		mod184.setName(params.getName());
		mod184.setYear(year);
		mod184.setReceipt("1840000000001");
		mod184.setStatus(FiscalStatus.PENDING);
		mod184.setAdministration(params.getAdministration(Administration.COMMON_TERRITORY));
		mod184.setContactPerson(AonStringUtils.left(params.getContactPerson(),
				FS_MODEL184.CONTACT_PERSON.getDataType().length()));
		mod184.setContactPhone(AonStringUtils.left(params.getContactPhone(),
				FS_MODEL184.CONTACT_PHONE.getDataType().length()));
		mod184.setContactMail(AonStringUtils.left(params.getContactMail(),
				FS_MODEL184.CONTACT_MAIL.getDataType().length()));
		mod184.setIncomes(new LinkedList<Mod184Income>());
		mod184.setPartners(new LinkedList<Mod184Partner>());
		return mod184;
	}
	
	public static Mod184 changeStatusMod184(AONContext ctx, Mod184 mod184, FiscalStatus newStatus) {
		try {
			ctx.checkWrite();
			if (mod184.getId() != null) {
				mod184.setStatus(newStatus);
				ctx.getDslContext().update(FS_MODEL184)
					.set(FS_MODEL184.STATUS,AonEnumUtils.getByte( mod184.getStatus()))
					.where(FS_MODEL184.ID.equal(mod184.getId()))
					.execute();
			}
			return mod184;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Throwable t) {
			throw new AonCoreException(t.getMessage());
		}
	}
}
