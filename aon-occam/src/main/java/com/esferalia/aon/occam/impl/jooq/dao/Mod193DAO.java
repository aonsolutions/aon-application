
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
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.FsModel193Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod193DAO {

	private static byte ZERO_BYTE = 0;

	public static Stream<Mod193> getHeaders(AONContext ctx, int domain) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(FS_MODEL193.fields())
			.from(FS_MODEL193)
			.join(DOMAIN).on(FS_MODEL193.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL193.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.orderBy(FS_MODEL193.YEAR.desc()
					,FS_MODEL193.NAME.asc()
					,FS_MODEL193.REPLACEMENT.asc())
			.fetch()
			.stream()
			.map(new Mod193Filler());
	}

	public static LinkedList<Mod193> getByDomain(AONContext ctx, int domain) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(FS_MODEL193.fields())
			.from(FS_MODEL193)
			.join(DOMAIN).on(FS_MODEL193.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL193.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.orderBy(FS_MODEL193.YEAR.desc()
					,FS_MODEL193.NAME.asc()
					,FS_MODEL193.REPLACEMENT.asc())
			.fetch()
			.stream()
			.map(new Mod193Filler())
			.peek( mod193 -> mod193.setDetails( getDetails(ctx, mod193.getId()) ))
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static Mod193 getById(AONContext ctx, int id) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(FS_MODEL193.fields())
			.from(FS_MODEL193)
			.join(DOMAIN).on(FS_MODEL193.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL193.DOMAIN.equal(ctx.getDomainId()).or(DOMAIN.PARENT.equal(ctx.getDomainId())))
			.and(FS_MODEL193.ID.equal(id))
			.fetch()
			.stream()
			.map(new Mod193Filler())
			.peek( mod193 -> mod193.setDetails( getDetails(ctx, mod193.getId()) ))
			.peek( mod193 -> mod193.setExpenses( getExpenses(ctx, mod193.getId()) ))
			.findFirst()
			.orElse(null);
	}

	public static Mod193 saveComments(AONContext ctx, Mod193 fm) {
		try {
			ctx.checkWrite();
			if (fm.getId() != null) {
				ctx.getDslContext().update(FS_MODEL193)
					.set(FS_MODEL193.COMMENTS,fm.getComments())
					.where(FS_MODEL193.ID.equal(fm.getId()))
					.execute();
			}
			return fm;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Throwable t) {
			throw new AonCoreException(t.getMessage());
		}
	}

	public static Mod193 save(AONContext ctx, Mod193 mod193) {
		ctx.checkWrite();
		if (mod193.getId() == null) {
			mod193 = insert(ctx, mod193);
			//insertDetails(ctx, mod193);
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

	public static Mod193 changeStatus(AONContext ctx, Mod193 mod193, FiscalStatus newStatus) {
		try {
			ctx.checkWrite();
			if (mod193.getId() != null) {
				mod193.setStatus(newStatus);
				ctx.getDslContext().update(FS_MODEL193)
					.set(FS_MODEL193.STATUS,AonEnumUtils.getByte( mod193.getStatus()))
					.where(FS_MODEL193.ID.equal(mod193.getId()))
					.execute();
			}
			return mod193;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Throwable t) {
			throw new AonCoreException(t.getMessage());
		}
	}
	private static Mod193 insert(AONContext ctx, Mod193 mod193) {
		return insert(ctx, mod193, true);
	}

	private static Mod193 insert(AONContext ctx, Mod193 mod193, boolean generateDetails) {
		validate(ctx, mod193);
		FsModel193Record record = ctx
				.getDslContext()
				.insertInto(FS_MODEL193)
				.set(FS_MODEL193.DOMAIN, mod193.getDomain())
				.set(FS_MODEL193.ENTERPRISE, mod193.getEnterprise())
				.set(FS_MODEL193.YEAR, mod193.getYear())
				.set(FS_MODEL193.ADMINISTRATION,mod193.getAdministration().getValue())
				.set(FS_MODEL193.STATUS, (byte) 0)
				.set(FS_MODEL193.SECURITY_LEVEL,AonEnumUtils.getByte(mod193.isConfidential()))
				.set(FS_MODEL193.DOCUMENT, mod193.getDocument())
				.set(FS_MODEL193.NAME, mod193.getName())
				.set(FS_MODEL193.CONTACT_PERSON, mod193.getContactPerson())
				.set(FS_MODEL193.CONTACT_PHONE, mod193.getContactPhone())
				.set(FS_MODEL193.CONTACT_MAIL, mod193.getContactMail())
				.set(FS_MODEL193.COMPLEMENTARY, AonEnumUtils.getByte(mod193.isComplementary()))
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
		if (generateDetails) {
			insertDetailsFromInvoice(ctx, mod193);
		}
		return mod193;
	}

	private static Mod193 update(AONContext ctx, Mod193 mod193) {
		ctx.getDslContext()
				.update(FS_MODEL193)
				.set(FS_MODEL193.YEAR, mod193.getYear())
				.set(FS_MODEL193.ADMINISTRATION,mod193.getAdministration().getValue())
				.set(FS_MODEL193.STATUS, (byte) 0)
				.set(FS_MODEL193.SECURITY_LEVEL,AonEnumUtils.getByte(mod193.isConfidential()))
				.set(FS_MODEL193.DOCUMENT, mod193.getDocument())
				.set(FS_MODEL193.NAME, mod193.getName())
				.set(FS_MODEL193.CONTACT_PERSON, mod193.getContactPerson())
				.set(FS_MODEL193.CONTACT_PHONE, mod193.getContactPhone())
				.set(FS_MODEL193.CONTACT_MAIL, mod193.getContactMail())
				.set(FS_MODEL193.COMPLEMENTARY,AonEnumUtils.getByte(mod193.isComplementary()))
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

//	private static void insertDetails(AONContext ctx, Mod193 mod193) {
//		BatchBindStep batch = ctx
//				.getDslContext()
//				.batch(ctx
//						.getDslContext()
//						.insertInto(
//								FS_MODEL193_DETAIL,
//								FS_MODEL193_DETAIL.DOMAIN,
//								FS_MODEL193_DETAIL.FS_MODEL193,
//								FS_MODEL193_DETAIL.TYPE,
//								FS_MODEL193_DETAIL.DOCUMENT,
//								FS_MODEL193_DETAIL.NAME,
//								FS_MODEL193_DETAIL.REPRESENTATIVE_DOCUMENT,
//								FS_MODEL193_DETAIL.INTERMEDIARY_PAYMENT,
//								FS_MODEL193_DETAIL.PROVINCE,
//								FS_MODEL193_DETAIL.KEY_CODE,
//								FS_MODEL193_DETAIL.ISSUING_CODE,
//								FS_MODEL193_DETAIL.KEY,
//								FS_MODEL193_DETAIL.NATURE,
//								FS_MODEL193_DETAIL.PAYMENT,
//								FS_MODEL193_DETAIL.CODE_TYPE,
//								FS_MODEL193_DETAIL.ACCOUNT_CODE,
//								FS_MODEL193_DETAIL.PENDING,
//								FS_MODEL193_DETAIL.ACCRUAL_YEAR,
//								FS_MODEL193_DETAIL.IN_KIND,
//								FS_MODEL193_DETAIL.PERCEPTION,
//								FS_MODEL193_DETAIL.REDUCTION,
//								FS_MODEL193_DETAIL.RETENTION_BASE,
//								FS_MODEL193_DETAIL.PERCENT,
//								FS_MODEL193_DETAIL.RETENTION,
//								FS_MODEL193_DETAIL.DEPONENT_NATURE,
//								FS_MODEL193_DETAIL.LOAN_START_DATE,
//								FS_MODEL193_DETAIL.LOAN_DUE_DATE,
//								FS_MODEL193_DETAIL.COMPENSATION,
//								FS_MODEL193_DETAIL.GUARANTEE,
//								FS_MODEL193_DETAIL.EXPENSES
//								)
//						.values(null, null, null, null, null, null, null, null,
//								null, null, null, null, null, null, null, null,
//								null, null, null, null, null, null, null, null,
//								null, null, null, null, null));
//		ArrayList<Mod193Detail> details = new ArrayList<Mod193Detail>();
//		details.addAll(mod193.getDetails());
//		details.addAll(mod193.getExpenses());
//		for (Mod193Detail detail : details) {
//			batch.bind(detail.getDomain()
//					 , detail.getMod193()
//					 , detail.getType()
//					 , AonStringUtils.substring(detail.getDocument(), 0, 9)
//					 , AonStringUtils.substring(detail.getName(), 0, 40)
//					 , AonStringUtils.substring(detail.getRepresentativeDocument(), 0, 9)
//					 , detail.isIntermediaryPayment()
//					 , detail.getProvince()
//					 , detail.getKeyCode()
//					 , detail.getIssuingCode()
//					 , detail.getKey()
//					 , detail.getNature()
//					 , detail.getPayment()
//					 , detail.getCodeType()
//					 , detail.getAccountCode()
//					 , detail.isPending()
//					 , detail.getAccrualYear()
//					 , detail.isInKind()
//					 , detail.getPerception()
//					 , detail.getReduction()
//					 , detail.getRetentionBase()
//					 , detail.getPercent()
//					 , detail.getRetention()
//					 , detail.isDeponentNature()
//					 , detail.getLoanStartDate()
//					 , detail.getLoanDueDate()
//					 , detail.getCompensation()
//					 , detail.getGuarantee()
//					 , detail.getExpenses()
//					);
//		}
//		batch.execute();
//	}

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
			.set(FS_MODEL193_DETAIL.PENALIZATION,detail.getPenalization())
			.set(FS_MODEL193_DETAIL.DECLARANT_NATURE,AonEnumUtils.getByte( detail.isDeclarantNature()))
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
				.set(FS_MODEL193_DETAIL.PENALIZATION,detail.getPenalization())
				.set(FS_MODEL193_DETAIL.DECLARANT_NATURE,AonEnumUtils.getByte( detail.isDeclarantNature()))
				.where(FS_MODEL193_DETAIL.ID.equal(detail.getId())).execute();
	}

	private static void validate(AONContext ctx, Mod193 mod193) {
		if (mod193.isReplacement() || mod193.isComplementary()) {
			if (!ctx.getDslContext().selectOne()
					.from(FS_MODEL193)
					.where(FS_MODEL193.YEAR.equal(mod193.getYear())
					.and(FS_MODEL193.ENTERPRISE.equal(mod193.getEnterprise())))
					.fetch()
					.stream()
					.findFirst()
					.isPresent()) 
				throw new AonCoreException(
						AonError.FISCAL_NO_REPLACED_DECLARATION.getMessage());
		} else {
			// Se comprueba que no exista ya una declaración.
			if (ctx.getDslContext().selectOne()
				.from(FS_MODEL193)
				.where(FS_MODEL193.YEAR.equal(mod193.getYear())
				.and(FS_MODEL193.ENTERPRISE.equal(mod193.getEnterprise()))
				.and(FS_MODEL193.REPLACEMENT.equal(ZERO_BYTE)))
				.fetch()
				.stream()
				.findFirst()
				.isPresent()) 
				throw new AonCoreException(
						AonError.FISCAL_DECLARATION_ALREADY_EXISTS.getMessage());
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

	private static class Mod193Filler implements Function<Record, Mod193> {

		@Override
		public Mod193 apply(Record record) {
			return new Mod193()
				.setId(record.getValue(FS_MODEL193.ID))
				.setDomain(record.getValue(FS_MODEL193.DOMAIN))
				.setEnterprise(record.getValue(FS_MODEL193.ENTERPRISE))
				.setYear(record.getValue(FS_MODEL193.YEAR))
				.setAdministration( com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Administration.class,record.getValue(FS_MODEL193.ADMINISTRATION)))
				.setReplacement(record.getValue(FS_MODEL193.REPLACEMENT) == 1)
				.setComplementary(record.getValue(FS_MODEL193.COMPLEMENTARY)==1 )
				.setStatus(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(FiscalStatus.class,record.getValue(FS_MODEL193.STATUS)))
				.setDocument(record.getValue(FS_MODEL193.DOCUMENT))
				.setName(record.getValue(FS_MODEL193.NAME))
				.setContactPerson(record.getValue(FS_MODEL193.CONTACT_PERSON))
				.setContactPhone(record.getValue(FS_MODEL193.CONTACT_PHONE))
				.setContactMail(record.getValue(FS_MODEL193.CONTACT_MAIL))
				.setReceipt(record.getValue(FS_MODEL193.RECEIPT))
				.setReplacedReceipt(record.getValue(FS_MODEL193.REPLACED_RECEIPT))
				.setReceiverCountTotal(record.getValue(FS_MODEL193.RECEIVER_COUNT_TOTAL))
				.setRetentionBaseTotal(record.getValue(FS_MODEL193.RETENTION_BASE_TOTAL))
				.setRetentionTotal(record.getValue(FS_MODEL193.RETENTION_TOTAL))
				.setDepositRetentionTotal(record.getValue(FS_MODEL193.DEPOSIT_RETENTION_TOTAL))
				.setExpensesTotal(record.getValue(FS_MODEL193.EXPENSES_TOTAL))
				.setComments(record.getValue(FS_MODEL193.COMMENTS));
			
		}
	}
		
					
	public static LinkedList<Mod193Detail> getDetails(AONContext ctx, int mod193) {
		ctx.checkRead();
		return ctx.getDslContext()
			.selectFrom(FS_MODEL193_DETAIL)
			.where(FS_MODEL193_DETAIL.FS_MODEL193.eq(mod193))
			.and(FS_MODEL193_DETAIL.TYPE.eq(Mod193Detail.DETAIL_TYPE))
			.fetch()
			.stream()
			.map( new Mod193DetailFiller() )
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static LinkedList<Mod193Detail> getExpenses(AONContext ctx, int mod193) {
		ctx.checkRead();
		return ctx.getDslContext()
			.selectFrom(FS_MODEL193_DETAIL)
			.where(FS_MODEL193_DETAIL.FS_MODEL193.eq(mod193))
			.and(FS_MODEL193_DETAIL.TYPE.eq(Mod193Detail.EXPENSE_TYPE))
			.fetch()
			.stream()
			.map( new Mod193DetailFiller() )
			.collect(Collectors.toCollection(LinkedList::new));
	}

	private static class Mod193DetailFiller implements Function<Record, Mod193Detail> {

		@Override
		public Mod193Detail apply(Record record) {
			return new Mod193Detail()
				.setId(record.getValue(FS_MODEL193_DETAIL.ID))
				.setType(record.getValue(FS_MODEL193_DETAIL.TYPE))
				.setDocument(record.getValue(FS_MODEL193_DETAIL.DOCUMENT))
				.setName(record.getValue(FS_MODEL193_DETAIL.NAME))
				.setRepresentativeDocument(record.getValue(FS_MODEL193_DETAIL.REPRESENTATIVE_DOCUMENT))
				.setIntermediaryPayment(AonEnumUtils.getBoolean( record.getValue(FS_MODEL193_DETAIL.INTERMEDIARY_PAYMENT)))
				.setProvince(record.getValue(FS_MODEL193_DETAIL.PROVINCE))
				.setKeyCode(record.getValue(FS_MODEL193_DETAIL.KEY_CODE))
				.setIssuingCode(record.getValue(FS_MODEL193_DETAIL.ISSUING_CODE))
				.setKey(record.getValue(FS_MODEL193_DETAIL.KEY))
				.setNature(record.getValue(FS_MODEL193_DETAIL.NATURE))
				.setPayment(record.getValue(FS_MODEL193_DETAIL.PAYMENT))
				.setCodeType(record.getValue(FS_MODEL193_DETAIL.CODE_TYPE))
				.setLenderAmount(record.getValue(FS_MODEL193_DETAIL.LENDER_AMOUNT))
				.setAccountCode(record.getValue(FS_MODEL193_DETAIL.ACCOUNT_CODE))
				.setPending(AonEnumUtils.getBoolean( record.getValue(FS_MODEL193_DETAIL.PENDING)))
				.setAccrualYear(record.getValue(FS_MODEL193_DETAIL.ACCRUAL_YEAR))
				.setInKind(AonEnumUtils.getBoolean( record.getValue(FS_MODEL193_DETAIL.IN_KIND)))
				.setPerception(record.getValue(FS_MODEL193_DETAIL.PERCEPTION))
				.setReduction(record.getValue(FS_MODEL193_DETAIL.REDUCTION))
				.setRetentionBase(record.getValue(FS_MODEL193_DETAIL.RETENTION_BASE))
				.setPercent(record.getValue(FS_MODEL193_DETAIL.PERCENT))
				.setRetention(record.getValue(FS_MODEL193_DETAIL.RETENTION))
				.setDeponentNature(AonEnumUtils.getBoolean( record.getValue(FS_MODEL193_DETAIL.DEPONENT_NATURE)))
				.setLoanStartDate(record.getValue(FS_MODEL193_DETAIL.LOAN_START_DATE))
				.setLoanDueDate(record.getValue(FS_MODEL193_DETAIL.LOAN_DUE_DATE))
				.setCompensation(record.getValue(FS_MODEL193_DETAIL.COMPENSATION))
				.setGuarantee(record.getValue(FS_MODEL193_DETAIL.GUARANTEE))
				.setExpenses(record.getValue(FS_MODEL193_DETAIL.EXPENSES))
				.setPenalization(record.getValue(FS_MODEL193_DETAIL.PENALIZATION))
				.setDeclarantNature(AonEnumUtils.getBoolean( record.getValue(FS_MODEL193_DETAIL.DECLARANT_NATURE)));
		}
	}

	private static void insertDetailsFromInvoice(AONContext ctx,final Mod193 mod193) {
		java.sql.Date firstDay = AonDateUtils.toSql(AonDateUtils.getYearFirstDay(mod193.getYear()));
		java.sql.Date lastDay = AonDateUtils.toSql(AonDateUtils.getYearLastDay(mod193.getYear()));

		Field<Integer> minRegistry = DSL.min(INVOICE.REGISTRY).as(INVOICE.REGISTRY.getName());
		Field<BigDecimal> sumBase = DSL.sum(INVOICE_TAX.BASE).as(INVOICE_TAX.BASE.getName());
		Field<Double> invoiceTaxSum = DSL.round((INVOICE_TAX.BASE.mul(INVOICE_TAX.PERCENTAGE)).div(100), 2);
		Field<BigDecimal> quotaOp = DSL.sum(DSL.decode()
				.when(INVOICE_TAX.QUOTA.notEqual(0.0), INVOICE_TAX.QUOTA)
				.when(INVOICE_TAX.QUOTA.equal(0.0), invoiceTaxSum)
				.as(INVOICE_TAX.QUOTA.getName()));
		ctx.getDslContext()
			.select(INVOICE.RDOCUMENT,INVOICE.RNAME,minRegistry,sumBase,quotaOp)
			.from(INVOICE)
			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
			.where(INVOICE.DOMAIN.equal(mod193.getDomain()))
			.and(INVOICE.TYPE.notEqual( InvoiceType.SALES.value() )) // No Ventas
			.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.RETENTION.value() )) // IRPF
			.and(INVOICE_TAX.WITHHOLDING_TYPE.equal( WithholdingType.MOVABLE_CAPITAL.value() ))	// IRPF de Capital Mobiliario
			.and(INVOICE.ISSUE_DATE.between(firstDay,lastDay))
			.groupBy(INVOICE.RDOCUMENT, INVOICE.RNAME,INVOICE_TAX.WITHHOLDING_TYPE)
			.fetch()
			.stream()
			.map(record -> new Mod193Detail()
					.setDomain(mod193.getDomain())
					.setType(Mod193Detail.DETAIL_TYPE)
					.setMod193(mod193.getId())
					.setDocument(record.getValue(INVOICE.RDOCUMENT))
					.setName(record.getValue(INVOICE.RNAME))
					.setKey("A")
					.setPerception(record.getValue(sumBase).doubleValue())
					.setRetention(record.getValue(quotaOp).doubleValue())
					.setProvince( getRegistryMainAddressProvince(ctx, record.getValue(minRegistry)) ))
			.forEach(detail -> insertDetail(ctx,detail));
	}

	public static Mod193 initialize(AONContext ctx, int year) {
		Mod193 mod193 = new Mod193();
		FiscalParameters params = AppParamDAO.getFiscalParameters(ctx);
		mod193.setEnterprise(params.getCompany());
		mod193.setDomain(ctx.getDomainId());
		mod193.setDocument(params.getDocument());
		mod193.setName(AonStringUtils.left(params.getName(), FS_MODEL193.NAME.getDataType().length()));
		mod193.setYear(year);
		mod193.setReceipt("1930000000001");
		mod193.setAdministration(params.getAdministration()!=null?Administration.safeValueOf(params.getAdministration()):Administration.COMMON_TERRITORY);
		mod193.setContactPerson(AonStringUtils.left(params.getContactPerson(),FS_MODEL193.CONTACT_PERSON.getDataType().length()));
		mod193.setContactPhone(AonStringUtils.left(params.getContactPhone(),FS_MODEL193.CONTACT_PHONE.getDataType().length()));
		mod193.setContactMail(AonStringUtils.left(params.getContactMail(),FS_MODEL193.CONTACT_MAIL.getDataType().length()));
		return mod193;
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

	public static Mod193 duplicateNextYear(AONContext ctx, int id) {
		Mod193 mod193 = getById(ctx, id);
		mod193.setYear( mod193.getYear() + 1 );
		mod193.setId(null);
		mod193 = insert(ctx, mod193, false);
		Mod193 original = getById(ctx, id);
		for (Mod193Detail detail : original.getDetails()) {
			detail.setId(null);
			detail.setMod193(mod193.getId());
			saveDetail(ctx,mod193,detail);
		}
		return getById(ctx, mod193 .getId());
	}
}
