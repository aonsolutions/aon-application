
package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel184.FS_MODEL184;
import static com.esferalia.aon.jooq.tables.FsModel184Detail.FS_MODEL184_DETAIL;

import java.util.ArrayList;

import org.jooq.Result;

import com.esferalia.aon.jooq.tables.records.FsModel184DetailRecord;
import com.esferalia.aon.jooq.tables.records.FsModel184Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Income;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Partner;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod184DAO {

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
				.set(FS_MODEL184.ADMINISTRATION,(byte) mod184.getAdministration())
				.set(FS_MODEL184.STATUS,(byte) 0)
				.set(FS_MODEL184.SECURITY_LEVEL,AonEnumUtils.getByte(mod184.isConfidential()) ) 
				.set(FS_MODEL184.DOCUMENT,mod184.getDocument())
				.set(FS_MODEL184.NAME,mod184.getName())
				.set(FS_MODEL184.CONTACT_PERSON,mod184.getContactPerson())
				.set(FS_MODEL184.CONTACT_PHONE,mod184.getContactPhone())
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
			.returning(FS_MODEL184.ID)
			.fetchOne();
		mod184.setId(record.getId());
		return mod184;
	}

	private static Mod184 update(AONContext ctx, Mod184 mod184) {
		ctx.getDslContext().update(FS_MODEL184)
		.set(FS_MODEL184.YEAR,mod184.getYear())
		.set(FS_MODEL184.ADMINISTRATION,(byte) mod184.getAdministration())
		.set(FS_MODEL184.STATUS,(byte) 0)
		.set(FS_MODEL184.SECURITY_LEVEL,AonEnumUtils.getByte(mod184.isConfidential()) ) 
		.set(FS_MODEL184.DOCUMENT,mod184.getDocument())
		.set(FS_MODEL184.NAME,mod184.getName())
		.set(FS_MODEL184.CONTACT_PERSON,mod184.getContactPerson())
		.set(FS_MODEL184.CONTACT_PHONE,mod184.getContactPhone())
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
		.where(FS_MODEL184.ID.equal(mod184.getId()))
	.execute();
	return mod184;
	}

	private static void saveIncome(AONContext ctx, Mod184 mod184, Mod184Income income) {
		ctx.checkWrite();
		if (income.getId() == null || income.getId() < 0) {
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
		if (partner.getId() == null || partner.getId() < 0) {
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
			.set(FS_MODEL184_DETAIL.ADDRESS,partner.getAddress())
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
			.set(FS_MODEL184_DETAIL.ADDRESS,partner.getAddress())
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


	public static ArrayList<Mod184> getByDomain(AONContext ctx, int domain) {
		ctx.checkRead();
		ArrayList<Mod184> list = new ArrayList<Mod184>();
		ctx.getDslContext().select(FS_MODEL184.fields())
			.from(FS_MODEL184)
			.join(DOMAIN).on(FS_MODEL184.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL184.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.orderBy(FS_MODEL184.YEAR.desc()
					,FS_MODEL184.NAME.asc()
					,FS_MODEL184.REPLACEMENT.asc())
			.fetchInto(FsModel184Record.class )
			.stream()
			.forEach( record -> {
				Mod184 mod184 = new Mod184();
				populate( record, mod184);
				fillDetails(ctx, mod184);
				list.add(mod184);
			});
		return list;
	}

	public static Mod184 getById(AONContext ctx, int id) {
		ctx.checkRead();
		Mod184 mod184 = new Mod184();
		ctx.getDslContext().selectFrom(FS_MODEL184)
		.where(FS_MODEL184.ID.equal(id))
		.fetch()
		.stream()
		.forEach( record -> {
			populate(record, mod184);
			fillDetails(ctx, mod184);
		});
		if ( mod184.getId() != null ) {
			return mod184;
		}
		return null;
	}

	private static void fillDetails(AONContext ctx, Mod184 mod184) {
		ctx.checkRead();
		Result<FsModel184DetailRecord> records = ctx.getDslContext()
			.selectFrom(FS_MODEL184_DETAIL)
			.where(FS_MODEL184_DETAIL.FS_MODEL184.equal(mod184.getId()))
			.fetch();
		ArrayList<Mod184Income> incomes = new ArrayList<Mod184Income>();
		Mod184Income income = null;
		ArrayList<Mod184Partner> partners = new ArrayList<Mod184Partner>();
		Mod184Partner partner = null;
		for (FsModel184DetailRecord record : records) {
			String type = record.getValue(FS_MODEL184_DETAIL.TYPE);
			if ("I".equals(type)) {
				income = new Mod184Income();
				populateIncome(record, income);
				incomes.add(income);
			} else {
				partner = new Mod184Partner();
				populatePartner(record, partner);
				partners.add(partner);
			}
		}
		mod184.setIncomes(incomes);
		mod184.setPartners(partners);
	}

	private static void populate(FsModel184Record record, Mod184 mod184) {
		mod184.setId(record.getValue(FS_MODEL184.ID));
		mod184.setDomain(record.getValue(FS_MODEL184.DOMAIN));
		mod184.setEnterprise(record.getValue(FS_MODEL184.ENTERPRISE));
		mod184.setYear(record.getValue(FS_MODEL184.YEAR));
		mod184.setAdministration( record.getValue(FS_MODEL184.ADMINISTRATION));
		mod184.setReplacement( AonEnumUtils.getBoolean( record.getValue(FS_MODEL184.REPLACEMENT)) );
		mod184.setDocument(record.getValue(FS_MODEL184.DOCUMENT));
		mod184.setName(record.getValue(FS_MODEL184.NAME));
		mod184.setContactPerson(record.getValue(FS_MODEL184.CONTACT_PERSON));
		mod184.setContactPhone(record.getValue(FS_MODEL184.CONTACT_PHONE));
		mod184.setReceipt(record.getValue(FS_MODEL184.RECEIPT));
		mod184.setReplacedReceipt(record.getValue(FS_MODEL184.REPLACED_RECEIPT));
		mod184.setComments(record.getValue(FS_MODEL184.COMMENTS));
		mod184.setEntityType(record.getValue(FS_MODEL184.ENTITY_TYPE));
		mod184.setMainActivity(record.getValue(FS_MODEL184.MAIN_ACTIVITY));
		mod184.setForeignEntityType(record.getValue(FS_MODEL184.FOREIGN_ENTITY_TYPE));
		mod184.setForeignObject(record.getValue(FS_MODEL184.FOREIGN_OBJECT));
		mod184.setCountry(record.getValue(FS_MODEL184.COUNTRY));
		mod184.setResidentPercent(record.getValue(FS_MODEL184.RESIDENT_PERCENT));
		mod184.setTaxIS(AonEnumUtils.getBoolean( record.getValue(FS_MODEL184.TAX_IS)) );
		mod184.setNetSalesAmount(record.getValue(FS_MODEL184.NET_SALES_AMOUNT));
		mod184.setLrDocument(record.getValue(FS_MODEL184.LRDOCUMENT));
		mod184.setLrName(record.getValue(FS_MODEL184.LRNAME));
	}

	private static void populateIncome(FsModel184DetailRecord record, Mod184Income income) {
		income.setId(record.getId());
		income.setKey(record.getKey());
		income.setSubKey(record.getSubkey());
		income.setCountry(record.getCountry());
		income.setRegime(record.getRegime());
		income.setActivityType(record.getActivityType());
		income.setEpigraph(record.getEpigraph());
		income.setGranteeDocument(record.getGranteeDocument());
		income.setGranteeName(record.getGranteeName());
		income.setAdqDate(record.getAdqDate());
		income.setIncrease(record.getIncrease());
		income.setDecrease(record.getDecrease());
		income.setAccountingResult(record.getAccountingResult());
		income.setExpenses(record.getExpenses());
		income.setNetYield(record.getNetYield());
		income.setReductionPercent(record.getReductionPercent());
		income.setDeductionRightRent(record.getDeductionRightRent());
		income.setResult(record.getResult());
		income.setDeductionBase(record.getDeductionBase());
		income.setRetention(record.getRetention());
	}

	private static void populatePartner(FsModel184DetailRecord record, Mod184Partner partner) {
		partner.setId(record.getId());
		partner.setDocument(record.getDocument());
		partner.setRepresentativeDocument(record.getRepresentativeDocument());
		partner.setName(record.getName());
		partner.setProvince(record.getProvince());
		partner.setCountry(record.getCountry());
		partner.setPartType(record.getPartType());
		partner.setMemberEndOfYear(AonEnumUtils.getBoolean( record.getMemberEndOfYear()));
		partner.setMemberDays(record.getMemberDays());
		partner.setPartPercent(record.getPartPercent());
		partner.setKey(record.getKey());
		partner.setSubKey(record.getSubkey());
		partner.setAmount(record.getAmount());
		partner.setReduction(record.getReduction());
		partner.setAddress(record.getAddress());
	}

	public static Mod184 initialize(AONContext ctx, int year) {
		Mod184 mod184 = new Mod184();
		FiscalParameters params = AppParamDAO.getFiscalParameters(ctx);
		mod184.setEnterprise(params.getCompany());
		mod184.setDomain(ctx.getDomainId());
		mod184.setDocument(params.getDocument());
		mod184.setName(params.getName());
		mod184.setYear(year);
		mod184.setAdministration((byte) (params.getAdministration() != null ? params.getAdministration() : 4));
		mod184.setContactPerson(params.getContactPerson());
		mod184.setContactPhone(params.getContactPhone());
		mod184.setIncomes(new ArrayList<Mod184Income>());
		mod184.setPartners(new ArrayList<Mod184Partner>());
		return mod184;
	}
}
