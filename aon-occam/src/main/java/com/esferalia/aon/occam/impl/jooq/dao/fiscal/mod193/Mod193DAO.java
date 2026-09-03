
package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod193;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel193.FS_MODEL193;
import static com.esferalia.aon.jooq.tables.FsModel193Detail.FS_MODEL193_DETAIL;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

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
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IWithholdingTypeVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

public class Mod193DAO {
	
	private Mod193DAO() {
		
	}

	private static final byte ZERO_BYTE = 0;

	public static Stream<Mod193> getHeaders(AONContext ctx, int domain) {
		return getHeaders(ctx, domain, null);
	}
	public static Stream<Mod193> getHeaders(AONContext ctx, int domain, Integer scope) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(FS_MODEL193.fields())
			.select(DOMAIN.DESCRIPTION)
			.from(FS_MODEL193)
			.join(DOMAIN).on(FS_MODEL193.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL193.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.and( scope == null ? DSL.trueCondition() : DOMAIN.SCOPE.equal(scope))
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
			.select(DOMAIN.DESCRIPTION)
			.from(FS_MODEL193)
			.join(DOMAIN).on(FS_MODEL193.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL193.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.orderBy(FS_MODEL193.YEAR.desc()
					,FS_MODEL193.NAME.asc()
					,FS_MODEL193.REPLACEMENT.asc())
			.fetch()
			.stream()
			.map(new Mod193Filler())
			.map( mod193 -> mod193.setDetails( getDetails(ctx, mod193.getId()) ))
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static Mod193 getById(AONContext ctx, int id) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(FS_MODEL193.fields())
			.select(DOMAIN.DESCRIPTION)
			.from(FS_MODEL193)
			.join(DOMAIN).on(FS_MODEL193.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL193.DOMAIN.equal(ctx.getDomainId()).or(DOMAIN.PARENT.equal(ctx.getDomainId())))
			.and(FS_MODEL193.ID.equal(id))
			.fetch()
			.stream()
			.map(new Mod193Filler())
			.map( mod193 -> mod193.setDetails( getDetails(ctx, mod193.getId()) ))
			.map( mod193 -> mod193.setExpenses( getExpenses(ctx, mod193.getId()) ))
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
		} catch (Exception t) {
			throw new AonCoreException(t.getMessage());
		}
	}

	public static Mod193 save(AONContext ctx, Mod193 mod193) {
		ctx.checkWrite();
		if (mod193.getId() == null) {
			insert(ctx, mod193);
		} else {
			update(ctx, mod193);
			ArrayList<Mod193Detail> details = new ArrayList<>();
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
		} catch (Exception t) {
			throw new AonCoreException(t.getMessage());
		}
	}
	private static Mod193 insert(AONContext ctx, Mod193 mod193) {
		return insert(ctx, mod193, true);
	}

	private static Mod193 insert(AONContext ctx, Mod193 mod193, boolean generateDetails) {
		validate(ctx, mod193);
		FsModel193Record rec = ctx
				.getDslContext()
				.insertInto(FS_MODEL193)
				.set(FS_MODEL193.DOMAIN, mod193.getDomain())
				.set(FS_MODEL193.ENTERPRISE, mod193.getEnterprise())
				.set(FS_MODEL193.YEAR, mod193.getYear())
				.set(FS_MODEL193.ADMINISTRATION,mod193.getAdministration().value())
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
				.set(FS_MODEL193.NATURE,AonEnumUtils.getByte(mod193.isNature()))
				.returning(FS_MODEL193.ID).fetchOne();
		mod193.setId(rec.getId());
		if (generateDetails) {
			insertDetailsFromInvoice(ctx, mod193);
		}
		return mod193;
	}

	private static Mod193 update(AONContext ctx, Mod193 mod193) {
		ctx.getDslContext()
				.update(FS_MODEL193)
				.set(FS_MODEL193.YEAR, mod193.getYear())
				.set(FS_MODEL193.ADMINISTRATION,mod193.getAdministration().value())
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
				.set(FS_MODEL193.NATURE,AonEnumUtils.getByte(mod193.isNature()))
				.where(FS_MODEL193.ID.equal(mod193.getId())).execute();
		return mod193;
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
			.set(FS_MODEL193_DETAIL.PENALIZATION,detail.getPenalization())
			.set(FS_MODEL193_DETAIL.DECLARANT_NATURE,AonEnumUtils.getByte( detail.isDeclarantNature()))
			.set(FS_MODEL193_DETAIL.CEUTA_MELILLA, detail.getCeutaMelillaPalma())
			.set(FS_MODEL193_DETAIL.COMMON_RETENTION, detail.getCommonRetention())
			.set(FS_MODEL193_DETAIL.NAVARRA_RETENTION, detail.getNavarraRetention())
			.set(FS_MODEL193_DETAIL.ARABA_RETENTION, detail.getArabaRetention())
			.set(FS_MODEL193_DETAIL.BIZKAIA_RETENTION, detail.getBizkaiaRetention())
			.set(FS_MODEL193_DETAIL.GIPUZKOA_RETENTION, detail.getGipuzkoaRetention())
			.set(FS_MODEL193_DETAIL.PREVIOUS_PAYER_DOCUMENT, detail.getPreviousPayerDocument())
			.set(FS_MODEL193_DETAIL.ACCRUAL_DATE, AonDateUtils.toSql(detail.getAccrualDate()))
			.set(FS_MODEL193_DETAIL.MARKET_KEY, detail.getMarketKey())
			.set(FS_MODEL193_DETAIL.ISIN, detail.getIsin()) 
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
				.set(FS_MODEL193_DETAIL.CEUTA_MELILLA, detail.getCeutaMelillaPalma())
				.set(FS_MODEL193_DETAIL.COMMON_RETENTION, detail.getCommonRetention())
				.set(FS_MODEL193_DETAIL.NAVARRA_RETENTION, detail.getNavarraRetention())
				.set(FS_MODEL193_DETAIL.ARABA_RETENTION, detail.getArabaRetention())
				.set(FS_MODEL193_DETAIL.BIZKAIA_RETENTION, detail.getBizkaiaRetention())
				.set(FS_MODEL193_DETAIL.GIPUZKOA_RETENTION, detail.getGipuzkoaRetention())
				.set(FS_MODEL193_DETAIL.PREVIOUS_PAYER_DOCUMENT, detail.getPreviousPayerDocument())
				.set(FS_MODEL193_DETAIL.ACCRUAL_DATE, AonDateUtils.toSql(detail.getAccrualDate()))
				.set(FS_MODEL193_DETAIL.MARKET_KEY, detail.getMarketKey())
				.set(FS_MODEL193_DETAIL.ISIN, detail.getIsin()) 
				.where(FS_MODEL193_DETAIL.ID.equal(detail.getId())).execute();
	}

	private static void validate(AONContext ctx, Mod193 mod193) {
		if (mod193.isReplacement() || mod193.isComplementary()) {
			if (!ctx.getDslContext().selectOne()
					.from(FS_MODEL193)
					.where(FS_MODEL193.YEAR.equal(mod193.getYear())
					.and(FS_MODEL193.ADMINISTRATION.equal(mod193.getAdministration().value()))
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
				.and(FS_MODEL193.ADMINISTRATION.equal(mod193.getAdministration().value()))
				.and(FS_MODEL193.ENTERPRISE.equal(mod193.getEnterprise()))
				.and(FS_MODEL193.REPLACEMENT.equal(ZERO_BYTE))
				.and(FS_MODEL193.COMPLEMENTARY.equal(ZERO_BYTE)))
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
		public Mod193 apply(Record rec) {
			return new Mod193()
				.setId(rec.getValue(FS_MODEL193.ID))
				.setDomain(rec.getValue(FS_MODEL193.DOMAIN))
				.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
				.setEnterprise(rec.getValue(FS_MODEL193.ENTERPRISE))
				.setYear(rec.getValue(FS_MODEL193.YEAR))
				.setAdministration( com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Administration.class,rec.getValue(FS_MODEL193.ADMINISTRATION)))
				.setReplacement(rec.getValue(FS_MODEL193.REPLACEMENT) == 1)
				.setComplementary(rec.getValue(FS_MODEL193.COMPLEMENTARY)==1 )
				.setStatus(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(FiscalStatus.class,rec.getValue(FS_MODEL193.STATUS)))
				.setDocument(rec.getValue(FS_MODEL193.DOCUMENT))
				.setName(rec.getValue(FS_MODEL193.NAME))
				.setContactPerson(rec.getValue(FS_MODEL193.CONTACT_PERSON))
				.setContactPhone(rec.getValue(FS_MODEL193.CONTACT_PHONE))
				.setContactMail(rec.getValue(FS_MODEL193.CONTACT_MAIL))
				.setReceipt(rec.getValue(FS_MODEL193.RECEIPT))
				.setReplacedReceipt(rec.getValue(FS_MODEL193.REPLACED_RECEIPT))
				.setReceiverCountTotal(rec.getValue(FS_MODEL193.RECEIVER_COUNT_TOTAL))
				.setRetentionBaseTotal(rec.getValue(FS_MODEL193.RETENTION_BASE_TOTAL))
				.setRetentionTotal(rec.getValue(FS_MODEL193.RETENTION_TOTAL))
				.setDepositRetentionTotal(rec.getValue(FS_MODEL193.DEPOSIT_RETENTION_TOTAL))
				.setExpensesTotal(rec.getValue(FS_MODEL193.EXPENSES_TOTAL))
				.setNature(AonEnumUtils.getBoolean(rec.getValue(FS_MODEL193.NATURE)))
				.setComments(rec.getValue(FS_MODEL193.COMMENTS));
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
		public Mod193Detail apply(Record rec) {
			return new Mod193Detail()
				.setId(rec.getValue(FS_MODEL193_DETAIL.ID))
				.setType(rec.getValue(FS_MODEL193_DETAIL.TYPE))
				.setDocument(rec.getValue(FS_MODEL193_DETAIL.DOCUMENT))
				.setName(rec.getValue(FS_MODEL193_DETAIL.NAME))
				.setRepresentativeDocument(rec.getValue(FS_MODEL193_DETAIL.REPRESENTATIVE_DOCUMENT))
				.setIntermediaryPayment(AonEnumUtils.getBoolean( rec.getValue(FS_MODEL193_DETAIL.INTERMEDIARY_PAYMENT)))
				.setProvince(rec.getValue(FS_MODEL193_DETAIL.PROVINCE))
				.setKeyCode(rec.getValue(FS_MODEL193_DETAIL.KEY_CODE))
				.setIssuingCode(rec.getValue(FS_MODEL193_DETAIL.ISSUING_CODE))
				.setKey(rec.getValue(FS_MODEL193_DETAIL.KEY))
				.setNature(rec.getValue(FS_MODEL193_DETAIL.NATURE))
				.setPayment(rec.getValue(FS_MODEL193_DETAIL.PAYMENT))
				.setCodeType(rec.getValue(FS_MODEL193_DETAIL.CODE_TYPE))
				.setLenderAmount(rec.getValue(FS_MODEL193_DETAIL.LENDER_AMOUNT))
				.setAccountCode(rec.getValue(FS_MODEL193_DETAIL.ACCOUNT_CODE))
				.setPending(AonEnumUtils.getBoolean( rec.getValue(FS_MODEL193_DETAIL.PENDING)))
				.setAccrualYear(rec.getValue(FS_MODEL193_DETAIL.ACCRUAL_YEAR))
				.setInKind(AonEnumUtils.getBoolean( rec.getValue(FS_MODEL193_DETAIL.IN_KIND)))
				.setPerception(rec.getValue(FS_MODEL193_DETAIL.PERCEPTION))
				.setReduction(rec.getValue(FS_MODEL193_DETAIL.REDUCTION))
				.setRetentionBase(rec.getValue(FS_MODEL193_DETAIL.RETENTION_BASE))
				.setPercent(rec.getValue(FS_MODEL193_DETAIL.PERCENT))
				.setRetention(rec.getValue(FS_MODEL193_DETAIL.RETENTION))
				.setDeponentNature(AonEnumUtils.getBoolean( rec.getValue(FS_MODEL193_DETAIL.DEPONENT_NATURE)))
				.setLoanStartDate(rec.getValue(FS_MODEL193_DETAIL.LOAN_START_DATE))
				.setLoanDueDate(rec.getValue(FS_MODEL193_DETAIL.LOAN_DUE_DATE))
				.setCompensation(rec.getValue(FS_MODEL193_DETAIL.COMPENSATION))
				.setGuarantee(rec.getValue(FS_MODEL193_DETAIL.GUARANTEE))
				.setExpenses(rec.getValue(FS_MODEL193_DETAIL.EXPENSES))
				.setPenalization(rec.getValue(FS_MODEL193_DETAIL.PENALIZATION))
				.setDeclarantNature(AonEnumUtils.getBoolean(rec.getValue(FS_MODEL193_DETAIL.DECLARANT_NATURE)))
				.setCeutaMelillaPalma(rec.getValue(FS_MODEL193_DETAIL.CEUTA_MELILLA))
				.setCommonRetention(rec.getValue(FS_MODEL193_DETAIL.COMMON_RETENTION))
				.setNavarraRetention(rec.getValue(FS_MODEL193_DETAIL.NAVARRA_RETENTION))
				.setArabaRetention(rec.getValue(FS_MODEL193_DETAIL.ARABA_RETENTION))
				.setBizkaiaRetention(rec.getValue(FS_MODEL193_DETAIL.BIZKAIA_RETENTION))
				.setGipuzkoaRetention(rec.getValue(FS_MODEL193_DETAIL.GIPUZKOA_RETENTION))
				.setPreviousPayerDocument(rec.getValue(FS_MODEL193_DETAIL.PREVIOUS_PAYER_DOCUMENT))
				.setAccrualDate(rec.getValue(FS_MODEL193_DETAIL.ACCRUAL_DATE))
				.setMarketKey(rec.getValue(FS_MODEL193_DETAIL.MARKET_KEY))
				.setIsin(rec.getValue(FS_MODEL193_DETAIL.ISIN)) 
				;
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
			.select(INVOICE.RDOCUMENT, INVOICE.RNAME, minRegistry, sumBase, quotaOp, INVOICE_TAX.PERCENTAGE, INVOICE_TAX.WITHHOLDING_TYPE)
			.from(INVOICE)
			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
			.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
			.where(INVOICE.DOMAIN.equal(mod193.getDomain()))
			.and(INVOICE.TYPE.notEqual( InvoiceType.SALES.value() )) // No Ventas
			.and(INVOICE_TAX.TAX_TYPE.equal( TaxType.RETENTION.value() )) // IRPF
			.and( INVOICE_TAX.WITHHOLDING_TYPE.equal(WithholdingType.MOVABLE_CAPITAL.value())
				  .or(INVOICE_TAX.WITHHOLDING_TYPE.equal(WithholdingType.M193_C1.value()))
				  .or(INVOICE_TAX.WITHHOLDING_TYPE.equal(WithholdingType.M193_C2.value()))
				  .or(INVOICE_TAX.WITHHOLDING_TYPE.equal(WithholdingType.M193_C3.value()))
				  .or(INVOICE_TAX.WITHHOLDING_TYPE.equal(WithholdingType.M193_C4.value()))
				  .or(INVOICE_TAX.WITHHOLDING_TYPE.equal(WithholdingType.M193_B_01.value()))
				  .or(INVOICE_TAX.WITHHOLDING_TYPE.equal(WithholdingType.M193_B_02.value()))
				  .or(INVOICE_TAX.WITHHOLDING_TYPE.equal(WithholdingType.M193_B_03.value()))
				  .or(INVOICE_TAX.WITHHOLDING_TYPE.equal(WithholdingType.M193_B_04.value()))
				  .or(INVOICE_TAX.WITHHOLDING_TYPE.equal(WithholdingType.M193_B_05.value()))
				  .or(INVOICE_TAX.WITHHOLDING_TYPE.equal(WithholdingType.M193_B_06.value()))
				  .or(INVOICE_TAX.WITHHOLDING_TYPE.equal(WithholdingType.M193_B_07.value()))
				  .or(INVOICE_TAX.WITHHOLDING_TYPE.equal(WithholdingType.M193_D_01.value()))
				  .or(INVOICE_TAX.WITHHOLDING_TYPE.equal(WithholdingType.M193_D_02.value()))
				  .or(INVOICE_TAX.WITHHOLDING_TYPE.equal(WithholdingType.M193_D_03.value()))
				  .or(INVOICE_TAX.WITHHOLDING_TYPE.equal(WithholdingType.M193_D_04.value()))
				  .or(INVOICE_TAX.WITHHOLDING_TYPE.equal(WithholdingType.M193_D_05.value()))
				  .or(INVOICE_TAX.WITHHOLDING_TYPE.equal(WithholdingType.M193_D_06.value()))
				  .or(INVOICE_TAX.WITHHOLDING_TYPE.equal(WithholdingType.M193_D_07.value())) )  // IRPF de Capital Mobiliario
			.and(INVOICE.ISSUE_DATE.between(firstDay,lastDay))
			.and(INVOICE.NUMBER.ge(0))   // No facturas proforma (factura proforma es la que su numero de factura es menor que cero)
			.and(InvoiceDAO.NOT_ANNULLED) // No facturas anuladas
			.groupBy(INVOICE.RDOCUMENT, INVOICE.RNAME, INVOICE_TAX.WITHHOLDING_TYPE, INVOICE_TAX.PERCENTAGE)
			.fetch()
			.stream()
			.map(rec -> {
				String key = "";
				String nature = "";
				WithholdingType type = WithholdingType.safeValueOf(rec.getValue(INVOICE_TAX.WITHHOLDING_TYPE));
				if (type != null) {
					Pair<String,String> keyNature = type.visit(new IWithholdingTypeVisitor<Pair<String,String>>() {

						@Override
						public Pair<String, String> visitProfessional(Pair<String, String> t) {
							return null;
						}

						@Override
						public Pair<String, String> visitRenting(Pair<String, String> t) {
							return null;
						}

						@Override
						public Pair<String, String> visitMovableCapital(Pair<String, String> t) {
							return new Pair<>("A","01");
						}

						@Override
						public Pair<String, String> visitFarmer(Pair<String, String> t) {
							return null;
						}

						@Override
						public Pair<String, String> visitTransportOperator(Pair<String, String> t) {
							return null;
						}

						@Override
						public Pair<String, String> visitM190G02(Pair<String, String> t) {
							return null;
						}

						@Override
						public Pair<String, String> visitM190G03(Pair<String, String> t) {
							return null;
						}

						@Override
						public Pair<String, String> visitM190H02(Pair<String, String> t) {
							return null;
						}

						@Override
						public Pair<String, String> visitM190H03(Pair<String, String> t) {
							return null;
						}

						@Override
						public Pair<String, String> visitM190I01(Pair<String, String> t) {
							return null;
						}

						@Override
						public Pair<String, String> visitM190I02(Pair<String, String> t) {
							return null;
						}

						@Override
						public Pair<String, String> visitM190J(Pair<String, String> t) {
							return null;
						}

						@Override
						public Pair<String, String> visitM190K01(Pair<String, String> t) {
							return null;
						}

						@Override
						public Pair<String, String> visitM190K03(Pair<String, String> t) {
							return null;
						}

						@Override
						public Pair<String, String> visitM190K02(Pair<String, String> t) {
							return null;
						}
						
						@Override
						public Pair<String, String> visitM193C1(Pair<String, String> t) {
							return new Pair<>("C","02");
						}

						@Override
						public Pair<String, String> visitM193C2(Pair<String, String> t) {
							return new Pair<>("C","01");
						}

						@Override
						public Pair<String, String> visitM193C3(Pair<String, String> t) {
							return new Pair<>("C","06");
						}

						@Override
						public Pair<String, String> visitM190F01(Pair<String, String> t) {
							return null;
						}

						@Override
						public Pair<String, String> visitM190F021(Pair<String, String> t) {
							return null;
						}

						@Override
						public Pair<String, String> visitM190F022(Pair<String, String> t) {
							return null;
						}

						@Override
						public Pair<String, String> visitM193C4(Pair<String, String> t) {
							return new Pair<>("C","04");
						}

						@Override
						public Pair<String, String> visitM193B01(Pair<String, String> t) {
							return new Pair<>("B","01");
						}

						@Override
						public Pair<String, String> visitM193B02(Pair<String, String> t) {
							return new Pair<>("B","02");
						}

						@Override
						public Pair<String, String> visitM193B03(Pair<String, String> t) {
							return new Pair<>("B","03");
						}

						@Override
						public Pair<String, String> visitM193B04(Pair<String, String> t) {
							return new Pair<>("B","04");
						}

						@Override
						public Pair<String, String> visitM193B05(Pair<String, String> t) {
							return new Pair<>("B","05");
						}

						@Override
						public Pair<String, String> visitM193B06(Pair<String, String> t) {
							return new Pair<>("B","06");
						}

						@Override
						public Pair<String, String> visitM193B07(Pair<String, String> t) {
							return new Pair<>("B","07");
						}

						@Override
						public Pair<String, String> visitM193D01(Pair<String, String> t) {
							return new Pair<>("D","01");
						}

						@Override
						public Pair<String, String> visitM193D02(Pair<String, String> t) {
							return new Pair<>("D","02");
						}

						@Override
						public Pair<String, String> visitM193D03(Pair<String, String> t) {
							return new Pair<>("D","03");
						}

						@Override
						public Pair<String, String> visitM193D04(Pair<String, String> t) {
							return new Pair<>("D","04");
						}

						@Override
						public Pair<String, String> visitM193D05(Pair<String, String> t) {
							return new Pair<>("D","05");
						}

						@Override
						public Pair<String, String> visitM193D06(Pair<String, String> t) {
							return new Pair<>("D","06");
						}

						@Override
						public Pair<String, String> visitM193D07(Pair<String, String> t) {
							return new Pair<>("D","07");
						}

						
					}, null);
					if (keyNature != null) {
						key = keyNature.getLeft();
						nature = keyNature.getRight();
					}
				}
				
				return new Mod193Detail()
					.setDomain(mod193.getDomain())
					.setType(Mod193Detail.DETAIL_TYPE)
					.setMod193(mod193.getId())
					.setDocument(rec.getValue(INVOICE.RDOCUMENT))
					.setName(rec.getValue(INVOICE.RNAME))
					.setKey(key)
					.setNature(nature)
					.setRetentionBase(rec.getValue(sumBase).doubleValue())
					.setPercent(rec.getValue(INVOICE_TAX.PERCENTAGE))
					.setRetention(rec.getValue(quotaOp).doubleValue())
					.setProvince( RegistryAddressDAO.getMainAddressProvince(ctx, rec.getValue(minRegistry)) );
					
			})
			.forEach(detail -> insertDetail(ctx,detail));
		
	}

	public static Mod193 initialize(AONContext ctx, int year) {
		Mod193 mod193 = new Mod193();
		AonConfiguration conf = ConfigurationDAO.getConfiguration(ctx);
		mod193.setEnterprise(conf.getCompany().getId());
		mod193.setDomain(ctx.getDomainId());
		mod193.setDocument(conf.getCompany().getDocument());
		mod193.setName(AonStringUtils.left(conf.getCompany().getName(), FS_MODEL193.NAME.getDataType().length()));
		mod193.setYear(year);
		mod193.setReceipt("1930000000001");
//		mod193.setAdministration(conf.fiscal().getAdministration()!=null?Administration.safeValueOf(conf.fiscal().getAdministration()):Administration.COMMON_TERRITORY);
		mod193.setAdministration(conf.fiscal().getAdministration(Administration.COMMON_TERRITORY, true));
		mod193.setContactPerson(AonStringUtils.left(conf.fiscal().getContactPerson(),FS_MODEL193.CONTACT_PERSON.getDataType().length()));
		mod193.setContactPhone(AonStringUtils.left(conf.fiscal().getContactPhone(),FS_MODEL193.CONTACT_PHONE.getDataType().length()));
		mod193.setContactMail(AonStringUtils.left(conf.fiscal().getContactMail(),FS_MODEL193.CONTACT_MAIL.getDataType().length()));
		return mod193;
	}

	public static Mod193 duplicate(AONContext ctx, Mod193 mod193) {
		
		int id = mod193.getId();
		mod193.setId(null);
		insert(ctx, mod193, false);
		if (!mod193.isComplementary()) {
			Mod193 original = getById(ctx, id);
			for (Mod193Detail detail : original.getDetails()) {
				detail.setId(null);
				detail.setMod193(mod193.getId());
				saveDetail(ctx,mod193,detail);
			}
		}
		return getById(ctx, mod193.getId());
		
	}
	
    // Grabar resultado y pdf en response y marcar el modelo como enviado
	public static Mod193 aeatPresentation(AONContext ctx, Mod193 mod, String aeatResponse) {
		if (AonStringUtils.isNotBlank(aeatResponse)) {			
			
			// Antes de nada se borra la presentación anterior
			DataResponseDAO.deleteAEATResponse(ctx, mod);			
			
			// Grabar los datos en data_response y sus tablas asociadas
			DataResponseDAO.insertAEATResponse(ctx, mod, aeatResponse);
			
			// Marcar el modelo como enviado					
			if (mod != null && mod.getId() != null) {
				ctx.getDslContext().update(FS_MODEL193)					
					.set(FS_MODEL193.STATUS, FiscalStatus.SENT.value())
					.where(FS_MODEL193.ID.equal(mod.getId()))
					.execute();
				return getById(ctx, mod.getId());
			}
		}
		return mod;
	}
	
	
}
