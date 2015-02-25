
package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel193.FS_MODEL193;
import static com.esferalia.aon.jooq.tables.FsModel193Detail.FS_MODEL193_DETAIL;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import org.jooq.BatchBindStep;
import org.jooq.Field;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.FsModel193DetailRecord;
import com.esferalia.aon.jooq.tables.records.FsModel193Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod193DAO {

	public static Mod193 save(AONContext ctx, Mod193 mod193) {
		ctx.checkWrite();
		if (mod193.getId() == null) {
			mod193 = insert(ctx, mod193);
			insertDetails(ctx, mod193);
		} else {
			mod193 = update(ctx, mod193);
			ArrayList<Mod193Detail> details = new ArrayList<Mod193Detail>();
			details.addAll(mod193.getDetails());
			details.addAll(mod193.getExpenses());
			for (Mod193Detail detail : details) {
				saveDetail(ctx, mod193, detail);
			}
		}
		return getById(ctx, mod193.getId());
	}

	private static Mod193 insert(AONContext ctx, Mod193 mod193) {
		validate(ctx, mod193);
		FsModel193Record record = ctx
				.getDslContext()
				.insertInto(FS_MODEL193)
				.set(FS_MODEL193.DOMAIN, mod193.getDomain())
				.set(FS_MODEL193.ENTERPRISE, mod193.getEnterprise())
				.set(FS_MODEL193.YEAR, mod193.getYear())
				.set(FS_MODEL193.ADMINISTRATION, mod193.getAdministration())
				.set(FS_MODEL193.STATUS, (byte) 0)
				.set(FS_MODEL193.SECURITY_LEVEL,AonEnumUtils.getByte(mod193.isConfidential()))
				.set(FS_MODEL193.DOCUMENT, mod193.getDocument())
				.set(FS_MODEL193.NAME, mod193.getName())
				.set(FS_MODEL193.CONTACT_PERSON, mod193.getContactPerson())
				.set(FS_MODEL193.CONTACT_PHONE, mod193.getContactPhone())
				.set(FS_MODEL193.COMPLEMENTARY, (byte) 0)
				.set(FS_MODEL193.REPLACEMENT,AonEnumUtils.getByte(mod193.isReplacement()))
				.set(FS_MODEL193.COMMENTS, mod193.getComments())
				.set(FS_MODEL193.RECEIPT, mod193.getReceipt())
				.set(FS_MODEL193.REPLACED_RECEIPT, mod193.getReplacedReceipt())
				.set(FS_MODEL193.RECEIVER_COUNT_TOTAL,mod193.getReceiverCountTotal())
				.set(FS_MODEL193.RETENTION_BASE_TOTAL, mod193.getRetentionBaseTotal())
				.set(FS_MODEL193.RETENTION_TOTAL, mod193.getRetentionTotal())
				.set(FS_MODEL193.DEPOSIT_RETENTION_TOTAL, mod193.getDepositRetentionTotal())
				.set(FS_MODEL193.EXPENSES_TOTAL, mod193.getExpensesTotal())
				.returning(FS_MODEL193.ID).fetchOne();
		mod193.setId(record.getId());
		insertDetailsFromInvoice(ctx, mod193);
		return mod193;
	}

	private static Mod193 update(AONContext ctx, Mod193 mod193) {
		ctx.getDslContext()
				.update(FS_MODEL193)
				.set(FS_MODEL193.YEAR, mod193.getYear())
				.set(FS_MODEL193.ADMINISTRATION, mod193.getAdministration())
				.set(FS_MODEL193.STATUS, (byte) 0)
				.set(FS_MODEL193.SECURITY_LEVEL,AonEnumUtils.getByte(mod193.isConfidential()))
				.set(FS_MODEL193.DOCUMENT, mod193.getDocument())
				.set(FS_MODEL193.NAME, mod193.getName())
				.set(FS_MODEL193.CONTACT_PERSON, mod193.getContactPerson())
				.set(FS_MODEL193.CONTACT_PHONE, mod193.getContactPhone())
				.set(FS_MODEL193.COMPLEMENTARY, (byte) 0)
				.set(FS_MODEL193.REPLACEMENT,AonEnumUtils.getByte(mod193.isReplacement()))
				.set(FS_MODEL193.COMMENTS, mod193.getComments())
				.set(FS_MODEL193.RECEIPT, mod193.getReceipt())
				.set(FS_MODEL193.REPLACED_RECEIPT, mod193.getReplacedReceipt())
				.set(FS_MODEL193.RECEIVER_COUNT_TOTAL,mod193.getReceiverCountTotal())
				.set(FS_MODEL193.RETENTION_BASE_TOTAL, mod193.getRetentionBaseTotal())
				.set(FS_MODEL193.RETENTION_TOTAL, mod193.getRetentionTotal())
				.set(FS_MODEL193.DEPOSIT_RETENTION_TOTAL, mod193.getDepositRetentionTotal())
				.set(FS_MODEL193.EXPENSES_TOTAL, mod193.getExpensesTotal())
				.where(FS_MODEL193.ID.equal(mod193.getId())).execute();
		return mod193;
	}

	private static void insertDetails(AONContext ctx, Mod193 mod193) {
		BatchBindStep batch = ctx
				.getDslContext()
				.batch(ctx
						.getDslContext()
						.insertInto(
								FS_MODEL193_DETAIL,
								FS_MODEL193_DETAIL.DOMAIN,
								FS_MODEL193_DETAIL.FS_MODEL193,
								FS_MODEL193_DETAIL.TYPE,
								FS_MODEL193_DETAIL.DOCUMENT,
								FS_MODEL193_DETAIL.NAME,
								FS_MODEL193_DETAIL.REPRESENTATIVE_DOCUMENT,
								FS_MODEL193_DETAIL.INTERMEDIARY_PAYMENT,
								FS_MODEL193_DETAIL.PROVINCE,
								FS_MODEL193_DETAIL.KEY_CODE,
								FS_MODEL193_DETAIL.ISSUING_CODE,
								FS_MODEL193_DETAIL.KEY,
								FS_MODEL193_DETAIL.NATURE,
								FS_MODEL193_DETAIL.PAYMENT,
								FS_MODEL193_DETAIL.CODE_TYPE,
								FS_MODEL193_DETAIL.ACCOUNT_CODE,
								FS_MODEL193_DETAIL.PENDING,
								FS_MODEL193_DETAIL.ACCRUAL_YEAR,
								FS_MODEL193_DETAIL.IN_KIND,
								FS_MODEL193_DETAIL.PERCEPTION,
								FS_MODEL193_DETAIL.REDUCTION,
								FS_MODEL193_DETAIL.RETENTION_BASE,
								FS_MODEL193_DETAIL.PERCENT,
								FS_MODEL193_DETAIL.RETENTION,
								FS_MODEL193_DETAIL.DEPONENT_NATURE,
								FS_MODEL193_DETAIL.LOAN_START_DATE,
								FS_MODEL193_DETAIL.LOAN_DUE_DATE,
								FS_MODEL193_DETAIL.COMPENSATION,
								FS_MODEL193_DETAIL.GUARANTEE,
								FS_MODEL193_DETAIL.EXPENSES
								)
						.values(null, null, null, null, null, null, null, null,
								null, null, null, null, null, null, null, null,
								null, null, null, null, null, null, null, null,
								null, null, null, null, null));
		ArrayList<Mod193Detail> details = new ArrayList<Mod193Detail>();
		details.addAll(mod193.getDetails());
		details.addAll(mod193.getExpenses());
		for (Mod193Detail detail : details) {
			batch.bind(detail.getDomain()
					 , detail.getMod193()
					 , detail.getType()
					 , AonStringUtils.substring(detail.getDocument(), 0, 9)
					 , AonStringUtils.substring(detail.getName(), 0, 40)
					 , AonStringUtils.substring(detail.getRepresentativeDocument(), 0, 9)
					 , detail.isIntermediaryPayment()
					 , detail.getProvince()
					 , detail.getKeyCode()
					 , detail.getIssuingCode()
					 , detail.getKey()
					 , detail.getNature()
					 , detail.getPayment()
					 , detail.getCodeType()
					 , detail.getAccountCode()
					 , detail.isPending()
					 , detail.getAccrualYear()
					 , detail.isInKind()
					 , detail.getPerception()
					 , detail.getReduction()
					 , detail.getRetentionBase()
					 , detail.getPercent()
					 , detail.getRetention()
					 , detail.isDeponentNature()
					 , detail.getLoanStartDate()
					 , detail.getLoanDueDate()
					 , detail.getCompensation()
					 , detail.getGuarantee()
					 , detail.getExpenses()
					);
		}
		batch.execute();
	}

	public static void saveDetail(AONContext ctx, Mod193 mod193, Mod193Detail detail) {
		ctx.checkWrite();
		if (detail.getId() == null || detail.getId() < 0) {
			if (!detail.isDeleted()) {
				detail.setDomain(mod193.getDomain());
				detail.setMod193(mod193.getId());
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

	private static void insertDetail(AONContext ctx, Mod193Detail detail) {
		ctx.getDslContext()
			.insertInto(FS_MODEL193_DETAIL)
			.set(FS_MODEL193_DETAIL.DOMAIN, detail.getDomain())
			.set(FS_MODEL193_DETAIL.FS_MODEL193, detail.getMod193())
			.set(FS_MODEL193_DETAIL.TYPE, detail.getType())
			.set(FS_MODEL193_DETAIL.DOCUMENT,AonStringUtils.substring(detail.getDocument(), 0, 9))
			.set(FS_MODEL193_DETAIL.NAME,AonStringUtils.substring(detail.getName(), 0, 40))
			.set(FS_MODEL193_DETAIL.REPRESENTATIVE_DOCUMENT,AonStringUtils.substring(detail.getRepresentativeDocument(), 0, 9))
			.set(FS_MODEL193_DETAIL.INTERMEDIARY_PAYMENT,AonEnumUtils.getByte( detail.isIntermediaryPayment()))
			.set(FS_MODEL193_DETAIL.PROVINCE,detail.getProvince())
			.set(FS_MODEL193_DETAIL.KEY_CODE,detail.getKeyCode())
			.set(FS_MODEL193_DETAIL.ISSUING_CODE,detail.getIssuingCode())
			.set(FS_MODEL193_DETAIL.KEY,detail.getKey())
			.set(FS_MODEL193_DETAIL.NATURE,detail.getNature())
			.set(FS_MODEL193_DETAIL.PAYMENT,detail.getPayment())
			.set(FS_MODEL193_DETAIL.CODE_TYPE,detail.getCodeType())
			.set(FS_MODEL193_DETAIL.LENDER_AMOUNT,detail.getLenderAmount())
			.set(FS_MODEL193_DETAIL.ACCOUNT_CODE,detail.getAccountCode())
			.set(FS_MODEL193_DETAIL.PENDING,AonEnumUtils.getByte( detail.isPending()))
			.set(FS_MODEL193_DETAIL.ACCRUAL_YEAR,detail.getAccrualYear())
			.set(FS_MODEL193_DETAIL.IN_KIND,AonEnumUtils.getByte( detail.isInKind() ))
			.set(FS_MODEL193_DETAIL.PERCEPTION,detail.getPerception())
			.set(FS_MODEL193_DETAIL.REDUCTION,detail.getReduction())
			.set(FS_MODEL193_DETAIL.RETENTION_BASE,detail.getRetentionBase())
			.set(FS_MODEL193_DETAIL.PERCENT,detail.getPercent())
			.set(FS_MODEL193_DETAIL.RETENTION,detail.getRetention())
			.set(FS_MODEL193_DETAIL.DEPONENT_NATURE,AonEnumUtils.getByte(detail.isDeponentNature()))
			.set(FS_MODEL193_DETAIL.LOAN_START_DATE,AonDateUtils.toSql(detail.getLoanStartDate()))
			.set(FS_MODEL193_DETAIL.LOAN_DUE_DATE,AonDateUtils.toSql(detail.getLoanDueDate()))
			.set(FS_MODEL193_DETAIL.COMPENSATION,detail.getCompensation())
			.set(FS_MODEL193_DETAIL.GUARANTEE,detail.getGuarantee())
			.set(FS_MODEL193_DETAIL.EXPENSES,detail.getExpenses())
			.execute();
	}

	private static void updateDetail(AONContext ctx, Mod193Detail detail) {
		ctx.getDslContext()
				.update(FS_MODEL193_DETAIL)
				.set(FS_MODEL193_DETAIL.DOCUMENT,AonStringUtils.substring(detail.getDocument(), 0, 9))
				.set(FS_MODEL193_DETAIL.NAME,AonStringUtils.substring(detail.getName(), 0, 40))
				.set(FS_MODEL193_DETAIL.REPRESENTATIVE_DOCUMENT,AonStringUtils.substring(detail.getRepresentativeDocument(), 0, 9))
				.set(FS_MODEL193_DETAIL.INTERMEDIARY_PAYMENT,AonEnumUtils.getByte(detail.isIntermediaryPayment()))
				.set(FS_MODEL193_DETAIL.PROVINCE,detail.getProvince())
				.set(FS_MODEL193_DETAIL.KEY_CODE,detail.getKeyCode())
				.set(FS_MODEL193_DETAIL.ISSUING_CODE,detail.getIssuingCode())
				.set(FS_MODEL193_DETAIL.KEY,detail.getKey())
				.set(FS_MODEL193_DETAIL.NATURE,detail.getNature())
				.set(FS_MODEL193_DETAIL.PAYMENT,detail.getPayment())
				.set(FS_MODEL193_DETAIL.CODE_TYPE,detail.getCodeType())
				.set(FS_MODEL193_DETAIL.LENDER_AMOUNT,detail.getLenderAmount())
				.set(FS_MODEL193_DETAIL.ACCOUNT_CODE,detail.getAccountCode())
				.set(FS_MODEL193_DETAIL.PENDING,AonEnumUtils.getByte(detail.isPending()))
				.set(FS_MODEL193_DETAIL.ACCRUAL_YEAR,detail.getAccrualYear())
				.set(FS_MODEL193_DETAIL.IN_KIND,AonEnumUtils.getByte(detail.isInKind()))
				.set(FS_MODEL193_DETAIL.PERCEPTION,detail.getPerception())
				.set(FS_MODEL193_DETAIL.REDUCTION,detail.getReduction())
				.set(FS_MODEL193_DETAIL.RETENTION_BASE,detail.getRetentionBase())
				.set(FS_MODEL193_DETAIL.PERCENT,detail.getPercent())
				.set(FS_MODEL193_DETAIL.RETENTION,detail.getRetention())
				.set(FS_MODEL193_DETAIL.DEPONENT_NATURE,AonEnumUtils.getByte(detail.isDeponentNature()))
				.set(FS_MODEL193_DETAIL.LOAN_START_DATE,AonDateUtils.toSql(detail.getLoanStartDate()))
				.set(FS_MODEL193_DETAIL.LOAN_DUE_DATE,AonDateUtils.toSql(detail.getLoanDueDate()))
				.set(FS_MODEL193_DETAIL.COMPENSATION,detail.getCompensation())
				.set(FS_MODEL193_DETAIL.GUARANTEE,detail.getGuarantee())
				.set(FS_MODEL193_DETAIL.EXPENSES,detail.getExpenses())
				.where(FS_MODEL193_DETAIL.ID.equal(detail.getId())).execute();
	}

	private static void validate(AONContext ctx, Mod193 mod193) {
		if (mod193.isReplacement()) {
			// Se comprueba que exista la declaración ssustituida.
			if (ctx.getDslContext()
				.selectOne()
				.from(FS_MODEL193)
				.where(FS_MODEL193.YEAR
				.equal(mod193.getYear())
				.and(FS_MODEL193.ENTERPRISE.equal(mod193.getEnterprise()))
				.and(FS_MODEL193.RECEIPT.equal(mod193.getReplacedReceipt()))).fetchCount() == 0)
				throw new AonCoreException(AonError.FISCAL_NO_REPLACED_DECLARATION.getMessage());

			// Se comprueba que no exista una declaraci?n sustitutiva.
			if (ctx.getDslContext()
					.selectOne()
					.from(FS_MODEL193)
					.where(FS_MODEL193.YEAR.equal(mod193.getYear())
					.and(FS_MODEL193.ENTERPRISE.equal(mod193.getEnterprise()))
					.and(FS_MODEL193.REPLACEMENT.equal((byte) 1))
					.and(FS_MODEL193.REPLACED_RECEIPT.equal(mod193.getReplacedReceipt()))).fetchCount() > 0)
				throw new AonCoreException(AonError.FISCAL_DECLARATION_ALREADY_REPLACED.getMessage());
		} else {
			// Se comprueba que no exista ya una declaraci?n.
			if (ctx.getDslContext()
					.selectOne()
					.from(FS_MODEL193)
					.where(FS_MODEL193.YEAR
					.equal(mod193.getYear())
					.and(FS_MODEL193.ENTERPRISE.equal(mod193.getEnterprise()))
					.and(FS_MODEL193.REPLACEMENT.equal((byte) 0)))
					.fetchCount() > 0)
				throw new AonCoreException(AonError.FISCAL_DECLARATION_ALREADY_EXISTS.getMessage());
		}
	}

	public static void delete(AONContext ctx, Mod193 mod193) {
		ctx.checkWrite();
		deleteDetails(ctx, mod193);
		ctx.getDslContext().delete(FS_MODEL193).where(FS_MODEL193.ID.equal(mod193.getId())).execute();
	}

	private static void deleteDetails(AONContext ctx, Mod193 mod193) {
		ctx.getDslContext()
			.delete(FS_MODEL193_DETAIL)
			.where(FS_MODEL193_DETAIL.FS_MODEL193.equal(mod193.getId()))
			.execute();
	}

	private static void deleteDetail(AONContext ctx, Mod193Detail detail) {
		ctx.getDslContext()
			.delete(FS_MODEL193_DETAIL)
			.where(FS_MODEL193_DETAIL.ID.equal(detail.getId()))
			.execute();
	}

	public static ArrayList<Mod193> getByDomain(AONContext ctx, int domain) {
		ctx.checkRead();
		ArrayList<Mod193> list = new ArrayList<Mod193>();
		ctx.getDslContext()
				.select(FS_MODEL193.fields())
				.from(FS_MODEL193)
				.join(DOMAIN)
				.on(FS_MODEL193.DOMAIN.equal(DOMAIN.ID))
				.where(FS_MODEL193.DOMAIN.equal(domain)
				.or(DOMAIN.PARENT.equal(domain)))
				.orderBy(FS_MODEL193.YEAR.desc(), FS_MODEL193.NAME.asc(),FS_MODEL193.REPLACEMENT.asc())
				.fetchInto(FsModel193Record.class)
				.stream()
				.forEach(
						record -> {
							Mod193 mod193 = new Mod193();
							populate(record, mod193);
							fillDetails(ctx,mod193);
							list.add(mod193);
						});
		return list;
	}

	public static Mod193 getById(AONContext ctx, int id) {
		ctx.checkRead();
		Mod193 mod193 = new Mod193();
		ctx.getDslContext()
				.selectFrom(FS_MODEL193)
				.where(FS_MODEL193.ID.equal(id))
				.fetch()
				.stream()
				.forEach(
						record -> {
							populate(record, mod193);
							fillDetails(ctx,mod193);
						});
		if (mod193.getId() != null) {
			return mod193;
		}
		return null;
	}

 private static void populate(FsModel193Record record, Mod193 mod193) {
		mod193.setId(record.getValue(FS_MODEL193.ID));
		mod193.setDomain(record.getValue(FS_MODEL193.DOMAIN));
		mod193.setEnterprise(record.getValue(FS_MODEL193.ENTERPRISE));
		mod193.setYear(record.getValue(FS_MODEL193.YEAR));
		mod193.setAdministration(record.getValue(FS_MODEL193.ADMINISTRATION));
		mod193.setReplacement(record.getValue(FS_MODEL193.REPLACEMENT) == 1);
		mod193.setDocument(record.getValue(FS_MODEL193.DOCUMENT));
		mod193.setName(record.getValue(FS_MODEL193.NAME));
		mod193.setContactPerson(record.getValue(FS_MODEL193.CONTACT_PERSON));
		mod193.setContactPhone(record.getValue(FS_MODEL193.CONTACT_PHONE));
		mod193.setReceipt(record.getValue(FS_MODEL193.RECEIPT));
		mod193.setReplacedReceipt(record.getValue(FS_MODEL193.REPLACED_RECEIPT));
		mod193.setReceiverCountTotal(record.getValue(FS_MODEL193.RECEIVER_COUNT_TOTAL));
		mod193.setRetentionBaseTotal(record.getValue(FS_MODEL193.RETENTION_BASE_TOTAL));
		mod193.setRetentionTotal(record.getValue(FS_MODEL193.RETENTION_TOTAL));
		mod193.setDepositRetentionTotal(record.getValue(FS_MODEL193.DEPOSIT_RETENTION_TOTAL));
		mod193.setExpensesTotal(record.getValue(FS_MODEL193.EXPENSES_TOTAL));
		mod193.setComments(record.getValue(FS_MODEL193.COMMENTS));
	}

	public static Mod193Detail getDetail(AONContext ctx, int id) {
		FsModel193DetailRecord record = ctx.getDslContext()
				.selectFrom(FS_MODEL193_DETAIL)
				.where(FS_MODEL193_DETAIL.ID.equal(id)).fetchOne();
		ctx.checkRead();
		Mod193Detail detail = null;
		if (record != null) {
			detail = new Mod193Detail();
			populateDetail(record, detail);
		}
		return detail;
	}

	public static void fillDetails(AONContext ctx, Mod193 mod193) {
		Result<FsModel193DetailRecord> records = ctx.getDslContext()
				.selectFrom(FS_MODEL193_DETAIL)
				.where(FS_MODEL193_DETAIL.FS_MODEL193.equal(mod193.getId())).fetch();
		ctx.checkRead();
		Mod193Detail detail = null;
		for (FsModel193DetailRecord record : records) {
			detail = new Mod193Detail();
			populateDetail(record, detail);
			if (detail.isExpense()) {
				mod193.getExpenses().add(detail);	
			} else {
				mod193.getDetails().add(detail);
			}
		}
	}

	private static void populateDetail(FsModel193DetailRecord record,Mod193Detail detail) {
		detail.setId(record.getId());
		detail.setType(record.getType());
		detail.setDocument(record.getDocument());
		detail.setName(record.getName());
		detail.setRepresentativeDocument(record.getRepresentativeDocument());
		detail.setIntermediaryPayment(AonEnumUtils.getBoolean( record.getIntermediaryPayment()));
		detail.setProvince(record.getProvince());
		detail.setKeyCode(record.getKeyCode());
		detail.setIssuingCode(record.getIssuingCode());
		detail.setKey(record.getKey());
		detail.setNature(record.getNature());
		detail.setPayment(record.getPayment());
		detail.setCodeType(record.getCodeType());
		detail.setLenderAmount(record.getLenderAmount());
		detail.setAccountCode(record.getAccountCode());
		detail.setPending(AonEnumUtils.getBoolean( record.getPending()));
		detail.setAccrualYear(record.getAccrualYear());
		detail.setInKind(AonEnumUtils.getBoolean( record.getInKind()));
		detail.setPerception(record.getPerception());
		detail.setReduction(record.getReduction());
		detail.setRetentionBase(record.getRetentionBase());
		detail.setPercent(record.getPercent());
		detail.setRetention(record.getRetention());
		detail.setDeponentNature(AonEnumUtils.getBoolean( record.getDeponentNature()));
		detail.setLoanStartDate(record.getLoanStartDate());
		detail.setLoanDueDate(record.getLoanDueDate());
		detail.setCompensation(record.getCompensation());
		detail.setGuarantee(record.getGuarantee());
		detail.setExpenses(record.getExpenses());
	}

	private static void insertDetailsFromInvoice(AONContext ctx, Mod193 mod193) {
		Date firstDay = AonDateUtils.getYearFirstDay(mod193.getYear());
		Date lastDay = AonDateUtils.getYearLastDay(mod193.getYear());

		Field<Integer> minRegistry = DSL.min(INVOICE.REGISTRY).as(
				INVOICE.REGISTRY.getName());
		Field<BigDecimal> sumBase = DSL.sum(INVOICE_TAX.BASE).as(
				INVOICE_TAX.BASE.getName());
		Field<Double> invoiceTaxSum = DSL.round(
				(INVOICE_TAX.BASE.mul(INVOICE_TAX.PERCENTAGE)).div(100), 2);
		Field<BigDecimal> quotaOp = DSL.sum(DSL.decode()
				.when(INVOICE_TAX.QUOTA.notEqual(0.0), INVOICE_TAX.QUOTA)
				.when(INVOICE_TAX.QUOTA.equal(0.0), invoiceTaxSum)
				.as(INVOICE_TAX.QUOTA.getName()));
		ctx.getDslContext()
				.select(INVOICE.RDOCUMENT
						,INVOICE.RNAME,
						minRegistry
						,sumBase
						,quotaOp)
				.from(INVOICE)
				.join(INVOICE_DETAIL)
				.on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.join(INVOICE_TAX)
				.on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
				.where(INVOICE.DOMAIN.equal(mod193.getDomain()))
				.and(INVOICE.TYPE.notEqual((byte) 1)) 					// No Ventas
				.and(INVOICE_TAX.TAX_TYPE.equal((byte) 2))				// IRPF
				.and(INVOICE_TAX.WITHHOLDING_TYPE.equal((byte) 2))		// IRPF de Capital Mobiliario
				.and(INVOICE.ISSUE_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
				.groupBy(INVOICE.RDOCUMENT, INVOICE.RNAME,INVOICE_TAX.WITHHOLDING_TYPE)
				.fetch()
				.stream()
				.forEach(
						record -> {
							Mod193Detail detail = new Mod193Detail();
							detail.setDomain(mod193.getDomain());
							detail.setType(Mod193Detail.DETAIL_TYPE);
							detail.setMod193(mod193.getId());
							detail.setDocument(record.getValue(INVOICE.RDOCUMENT));
							detail.setName(record.getValue(INVOICE.RNAME));
							detail.setKey("A");
							detail.setPerception(record.getValue(sumBase).doubleValue());
							detail.setRetention(record.getValue(quotaOp).doubleValue());
							ctx.getDslContext()
									.select(GEOZONE.CODE)
									.from(RADDRESS)
									.join(GEOZONE)
									.on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
									.where(RADDRESS.REGISTRY.equal(record
											.getValue(minRegistry)))
									.and(RADDRESS.TYPE.equal((byte) 0))
									// Direcci?n principal.
									.limit(1)
									.fetch()
									.stream()
									.forEach(
											province -> {
												try {
													detail.setProvince(Integer.parseInt(province
															.getValue(GEOZONE.CODE)));
												} catch (NumberFormatException e) {
													// nothing. If not a number,
													// not a valid province.
												}
											});
							mod193.getDetails().add(detail);
						});
	}

	public static Mod193 initialize(AONContext ctx, int year) {
		Mod193 mod193 = new Mod193();
		FiscalParameters params = AppParamDAO.getFiscalParameters(ctx);
		mod193.setEnterprise(params.getCompany());
		mod193.setDomain(ctx.getDomainId());
		mod193.setDocument(params.getDocument());
		mod193.setName(AonStringUtils.left(params.getName(), FS_MODEL193.NAME.getDataType().length()));
		mod193.setYear(year);
		mod193.setAdministration((byte) (params.getAdministration()!=null?params.getAdministration():4));
		mod193.setContactPerson(AonStringUtils.left(params.getContactPerson(),FS_MODEL193.CONTACT_PERSON.getDataType().length()));
		mod193.setContactPhone(AonStringUtils.left(params.getContactPhone(),FS_MODEL193.CONTACT_PHONE.getDataType().length()));
		return mod193;
	}

}
