
package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod190;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel190.FS_MODEL190;
import static com.esferalia.aon.jooq.tables.FsModel190Detail.FS_MODEL190_DETAIL;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.BatchBindStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.FsModel190DetailRecord;
import com.esferalia.aon.jooq.tables.records.FsModel190Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod190DAO {
	private static final byte ZERO_BYTE = 0;
	
	private Mod190DAO() {
		
	}

	public static Mod190 saveComments(AONContext ctx, Mod190 fm) {
		try {
			ctx.checkWrite();
			if (fm.getId() != null) {
				ctx.getDslContext().update(FS_MODEL190)
					.set(FS_MODEL190.COMMENTS,fm.getComments())
					.where(FS_MODEL190.ID.equal(fm.getId()))
					.execute();
			}
			return fm;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Exception t) {
			throw new AonCoreException(t.getMessage());
		}
	}

	public static Mod190 save(AONContext ctx, Mod190 mod190) {
		ctx.checkWrite();
		if (mod190.getId() == null) {
			insert(ctx, mod190);
			if (mod190.getDetails() != null && !mod190.getDetails().isEmpty()) {
				insertDetails(ctx, mod190);
			}
		} else {
			update(ctx, mod190);
			for (Mod190Detail detail : mod190.getDetails()) {
				saveDetail(ctx, mod190, detail);
			}
		}
		return getById(ctx, mod190.getId());
	}

	public static Mod190 changeStatus(AONContext ctx, Mod190 mod190, FiscalStatus newStatus) {
		try {
			ctx.checkWrite();
			if (mod190.getId() != null) {
				mod190.setStatus(newStatus);
				ctx.getDslContext().update(FS_MODEL190)
					.set(FS_MODEL190.STATUS,AonEnumUtils.getByte( mod190.getStatus()))
					.where(FS_MODEL190.ID.equal(mod190.getId()))
					.execute();
			}
			return mod190;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Exception t) {
			throw new AonCoreException(t.getMessage());
		}
	}
	
	private static Mod190 insert(AONContext ctx, Mod190 mod190) {
		return insert(ctx, mod190, true);
	}

	private static Mod190 insert(AONContext ctx, Mod190 mod190, boolean generateDetails) {
		validate(ctx, mod190);
		FsModel190Record rec = ctx
				.getDslContext()
				.insertInto(FS_MODEL190)
				.set(FS_MODEL190.DOMAIN, mod190.getDomain())
				.set(FS_MODEL190.ENTERPRISE, mod190.getEnterprise())
				.set(FS_MODEL190.YEAR, mod190.getYear())
				.set(FS_MODEL190.ADMINISTRATION,mod190.getAdministration().value())
				.set(FS_MODEL190.STATUS,AonEnumUtils.getByte( mod190.getStatus()  ))
				.set(FS_MODEL190.SECURITY_LEVEL,AonEnumUtils.getByte(mod190.isConfidential()))
				.set(FS_MODEL190.DOCUMENT, mod190.getDocument())
				.set(FS_MODEL190.NAME, mod190.getName())
				.set(FS_MODEL190.CONTACT_PERSON, mod190.getContactPerson())
				.set(FS_MODEL190.CONTACT_PHONE, mod190.getContactPhone())
				.set(FS_MODEL190.CONTACT_MAIL, mod190.getContactMail())
				.set(FS_MODEL190.COMPLEMENTARY, AonEnumUtils.getByte(mod190.isComplementary()))
				.set(FS_MODEL190.REPLACEMENT, AonEnumUtils.getByte(mod190.isReplacement()))
				.set(FS_MODEL190.COMMENTS, mod190.getComments())
				.set(FS_MODEL190.RECEIPT, mod190.getReceipt())
				.set(FS_MODEL190.REPLACED_RECEIPT, mod190.getReplacedReceipt())
				.set(FS_MODEL190.RECEIVER_COUNT_TOTAL,mod190.getReceiverCountTotal())
				.set(FS_MODEL190.RECEIPT_TOTAL, mod190.getReceiptTotal())
				.set(FS_MODEL190.RETENTION_TOTAL, mod190.getRetentionTotal())
				.set(FS_MODEL190.CREATION_USER,ctx.getUser())
				.set(FS_MODEL190.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
				.returning(FS_MODEL190.ID).fetchOne();
		mod190.setId(rec.getId());
		
		if (generateDetails) {
			Mod190Declaration dec = Mod190Declaration.getInstance(mod190);
			dec.insertDetailsFromInvoice(ctx, mod190);
			dec.insertDetailsFromSalary(ctx, mod190);
		}
		return mod190;
	}

	private static Mod190 update(AONContext ctx, Mod190 mod190) {
		ctx.getDslContext()
				.update(FS_MODEL190)
				.set(FS_MODEL190.YEAR, mod190.getYear())
				.set(FS_MODEL190.ADMINISTRATION,mod190.getAdministration().value())
				.set(FS_MODEL190.STATUS,AonEnumUtils.getByte( mod190.getStatus()  ))
				.set(FS_MODEL190.SECURITY_LEVEL,AonEnumUtils.getByte(mod190.isConfidential()))
				.set(FS_MODEL190.DOCUMENT, mod190.getDocument())
				.set(FS_MODEL190.NAME, mod190.getName())
				.set(FS_MODEL190.CONTACT_PERSON, mod190.getContactPerson())
				.set(FS_MODEL190.CONTACT_PHONE, mod190.getContactPhone())
				.set(FS_MODEL190.CONTACT_MAIL, mod190.getContactMail())
				.set(FS_MODEL190.COMPLEMENTARY, AonEnumUtils.getByte(mod190.isComplementary()))
				.set(FS_MODEL190.REPLACEMENT,AonEnumUtils.getByte(mod190.isReplacement()))
				.set(FS_MODEL190.COMMENTS, mod190.getComments())
				.set(FS_MODEL190.RECEIPT, mod190.getReceipt())
				.set(FS_MODEL190.REPLACED_RECEIPT, mod190.getReplacedReceipt())
				.set(FS_MODEL190.RECEIVER_COUNT_TOTAL,mod190.getReceiverCountTotal())
				.set(FS_MODEL190.RECEIPT_TOTAL, mod190.getReceiptTotal())
				.set(FS_MODEL190.RETENTION_TOTAL, mod190.getRetentionTotal())
				.set(FS_MODEL190.MODIFICATION_USER,ctx.getUser())
				.set(FS_MODEL190.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
				.where(FS_MODEL190.ID.equal(mod190.getId())).execute();
		return mod190;
	}

	private static void insertDetails(AONContext ctx, Mod190 mod190) {
		BatchBindStep batch = ctx
				.getDslContext()
				.batch(ctx
						.getDslContext()
						.insertInto(
								FS_MODEL190_DETAIL,
								FS_MODEL190_DETAIL.DOMAIN,
								FS_MODEL190_DETAIL.FS_MODEL190,
								FS_MODEL190_DETAIL.DOCUMENT,
								FS_MODEL190_DETAIL.NAME,
								FS_MODEL190_DETAIL.REPRESENTATIVE_DOCUMENT,
								FS_MODEL190_DETAIL.PROVINCE,
								FS_MODEL190_DETAIL.KEY,
								FS_MODEL190_DETAIL.SUBKEY,
								FS_MODEL190_DETAIL.PERCEPTION,
								FS_MODEL190_DETAIL.RETENTION,
								FS_MODEL190_DETAIL.IN_KIND_PERCEPTION,
								FS_MODEL190_DETAIL.IN_KIND_DEPOSIT,
								FS_MODEL190_DETAIL.IN_KIND_OUTPUT_DEPOSIT,
								FS_MODEL190_DETAIL.ACCRUAL_YEAR,
								FS_MODEL190_DETAIL.CEUTA_MELILLA,
								FS_MODEL190_DETAIL.BIRTH_YEAR,
								FS_MODEL190_DETAIL.FAMILY_SITUATION,
								FS_MODEL190_DETAIL.SPOUSE_DOCUMENT,
								FS_MODEL190_DETAIL.DISABILITY,
								FS_MODEL190_DETAIL.CONTRACT,
								FS_MODEL190_DETAIL.LABOUR_PROLONGATION,
								FS_MODEL190_DETAIL.GEOGRAPHIC_MOBILITY,
								FS_MODEL190_DETAIL.APPLICABLE_REDUCTION,
								FS_MODEL190_DETAIL.DEDUCIBLE_EXPENSES,
								FS_MODEL190_DETAIL.SPOUSAL_SUPPORT,
								FS_MODEL190_DETAIL.FOOD_ANNUITY,
								FS_MODEL190_DETAIL.LESS_THAN_3_DESCENDENT,
								FS_MODEL190_DETAIL.LESS_THAN_3_DESCENDENT_RATIO,
								FS_MODEL190_DETAIL.OTHER_DESCENDENT,
								FS_MODEL190_DETAIL.OTHER_DESCENDENT_RATIO,
								FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_33,
								FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_33_RATIO,
								FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_DEPENDENCE,
								FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_DEPENDENCE_RATIO,
								FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_65,
								FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_65_RATIO,
								FS_MODEL190_DETAIL.LESS_THAN_75_ASCENDANT,
								FS_MODEL190_DETAIL.LESS_THAN_75_ASCENDANT_RATIO,
								FS_MODEL190_DETAIL.ASCENDANT,
								FS_MODEL190_DETAIL.ASCENDANT_RATIO,
								FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_33,
								FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_33_RATIO,
								FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_DEPENDENCE,
								FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_DEPENDENCE_RATIO,
								FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_65,
								FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_65_RATIO,
								FS_MODEL190_DETAIL.FIRST_CHILD_CALCULATION,
								FS_MODEL190_DETAIL.SECOND_CHILD_CALCULATION,
								FS_MODEL190_DETAIL.THIRD_CHILD_CALCULATION,
								FS_MODEL190_DETAIL.HOME_LOAN_COMMUNNICATION,
								FS_MODEL190_DETAIL.PERCEPTION_IL,
								FS_MODEL190_DETAIL.RETENTION_IL,
								FS_MODEL190_DETAIL.OUTPUT_RETENTION_IL,
								FS_MODEL190_DETAIL.IN_KIND_PERCEPTION_IL,
								FS_MODEL190_DETAIL.IN_KIND_DEPOSIT_IL,
								FS_MODEL190_DETAIL.IN_KIND_OUTPUT_DEPOSIT_IL,
								FS_MODEL190_DETAIL.TIT_CONVIVENCIA,
								FS_MODEL190_DETAIL.COMP_INFANCIA,
								FS_MODEL190_DETAIL.COMMON_RETENTION,
								FS_MODEL190_DETAIL.NAVARRA_RETENTION,
								FS_MODEL190_DETAIL.ARABA_RETENTION,
								FS_MODEL190_DETAIL.BIZKAIA_RETENTION,
								FS_MODEL190_DETAIL.GIPUZKOA_RETENTION,
								FS_MODEL190_DETAIL.EXCESSES								
								)
						.values(null, null, null, null, null, null, null, null,
								null, null, null, null, null, null, null, null,
								null, null, null, null, null, null, null, null,
								null, null, null, null, null, null, null, null,
								null, null, null, null, null, null, null, null,
								null, null, null, null, null, null, null, null,
								null, null, null, null, null, null, null, null,
								null, null, null, null, null, null, null, null));
		for (Mod190Detail detail : mod190.getDetails()) {
			batch.bind(detail.getDomain()
					, detail.getMod190()
					, AonStringUtils.substring(detail.getDocument(), 0, 9)
					, AonStringUtils.substring(detail.getName(), 0, 40)
					, AonStringUtils.substring(detail.getRepresentativeDocument(), 0, 9)
					, detail.getProvince()
					, detail.getKey()
					, detail.getSubKey()
					, detail.getPerception()
					, detail.getRetention()
					, detail.getInKindPerception()
					, detail.getInKindDeposit()
					, detail.getInKindOutputDeposit()
					, detail.getAccrualYear()
					, detail.getCeutaMelillaPalma()
					, detail.getBirthYear()
					, detail.getFamilySituation()
					, detail.getSpouseDocument()
					, detail.getDisability()
					, detail.getContract()
					, AonEnumUtils.getByte(detail.isWorkActivityExtension())
					, AonEnumUtils.getByte(detail.isGeographicMobility())
					, detail.getApplicableReduction()
					, detail.getDeducibleExpense()
					, detail.getCompensatoryPension()
					, detail.getFoodAnnuality()
					, detail.getLessThan3Descendent()
					, detail.getLessThan3DescendentRatio()
					, detail.getOtherDescendent()
					, detail.getOtherDescendentRatio()
					, detail.getDisabilityDescendent33()
					, detail.getDisabilityDescendent33Ratio()
					, detail.getDisabilityDescendentDependence()
					, detail.getDisabilityDescendentDependenceRatio()
					, detail.getDisabilityDescendent65()
					, detail.getDisabilityDescendent65Ratio()
					, detail.getLessThan75Ascendant()
					, detail.getLessThan75AscendantRatio()
					, detail.getAscendant()
					, detail.getAscendantRatio()
					, detail.getDisabilityAscendant33()
					, detail.getDisabilityAscendant33Ratio()
					, detail.getDisabilityAscendantDependence()
					, detail.getDisabilityAscendantDependenceRatio()
					, detail.getDisabilityAscendant65()
					, detail.getDisabilityAscendant65Ratio()
					, detail.getFirstChildCalculation()
					, detail.getSecondChildCalculation()
					, detail.getThirdChildCalculation()
					, AonEnumUtils.getByte(detail.isHomeLoanCommunnication())
					, detail.getPerceptionIL()
					, detail.getRetentionIL()
					, detail.getOutputRetentionIL()
					, detail.getInKindPerceptionIL()
					, detail.getInKindDepositIL()
					, detail.getInKindOutputDepositIL()
					, detail.getTitConvivencia()
					, detail.getCompInfancia()
					, detail.getCommonRetention()
					, detail.getNavarraRetention()
					, detail.getArabaRetention()
					, detail.getBizkaiaRetention()
					, detail.getGipuzkoaRetention()
					, AonEnumUtils.getByte(detail.isExcesses())
					);
		}
		batch.execute();
	}

	public static void saveDetail(AONContext ctx, Mod190 mod190, Mod190Detail detail) {
		ctx.checkWrite();
		if (detail.getId() == null) {
			if (!detail.isDeleted()) {
				detail.setDomain(mod190.getDomain());
				detail.setMod190(mod190.getId());
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

	private static InsertSetMoreStep<FsModel190DetailRecord> getinsertDetailStatement(
			AONContext ctx, Mod190Detail detail) {
		return ctx.getDslContext()
				.insertInto(FS_MODEL190_DETAIL)
				.set(FS_MODEL190_DETAIL.DOMAIN, detail.getDomain())
				.set(FS_MODEL190_DETAIL.FS_MODEL190, detail.getMod190())
				.set(FS_MODEL190_DETAIL.DOCUMENT,AonStringUtils.substring(detail.getDocument(), 0, 9))
				.set(FS_MODEL190_DETAIL.NAME,AonStringUtils.substring(detail.getName(), 0, 40))
				.set(FS_MODEL190_DETAIL.REPRESENTATIVE_DOCUMENT,AonStringUtils.substring(detail.getRepresentativeDocument(), 0, 9))
				.set(FS_MODEL190_DETAIL.PROVINCE, detail.getProvince())
				.set(FS_MODEL190_DETAIL.KEY, detail.getKey())
				.set(FS_MODEL190_DETAIL.SUBKEY, detail.getSubKey())
				.set(FS_MODEL190_DETAIL.PERCEPTION, detail.getPerception())
				.set(FS_MODEL190_DETAIL.RETENTION, detail.getRetention())
				.set(FS_MODEL190_DETAIL.IN_KIND_PERCEPTION,detail.getInKindPerception())
				.set(FS_MODEL190_DETAIL.IN_KIND_DEPOSIT,detail.getInKindDeposit())
				.set(FS_MODEL190_DETAIL.IN_KIND_OUTPUT_DEPOSIT,detail.getInKindOutputDeposit())
				.set(FS_MODEL190_DETAIL.ACCRUAL_YEAR, detail.getAccrualYear())
				.set(FS_MODEL190_DETAIL.CEUTA_MELILLA, detail.getCeutaMelillaPalma())
				.set(FS_MODEL190_DETAIL.BIRTH_YEAR, detail.getBirthYear())
				.set(FS_MODEL190_DETAIL.FAMILY_SITUATION,detail.getFamilySituation())
				.set(FS_MODEL190_DETAIL.SPOUSE_DOCUMENT,detail.getSpouseDocument())
				.set(FS_MODEL190_DETAIL.DISABILITY, detail.getDisability())
				.set(FS_MODEL190_DETAIL.CONTRACT, detail.getContract())
				.set(FS_MODEL190_DETAIL.LABOUR_PROLONGATION,AonEnumUtils.getByte(detail.isWorkActivityExtension()))
				.set(FS_MODEL190_DETAIL.GEOGRAPHIC_MOBILITY,AonEnumUtils.getByte(detail.isGeographicMobility()))
				.set(FS_MODEL190_DETAIL.APPLICABLE_REDUCTION,detail.getApplicableReduction())
				.set(FS_MODEL190_DETAIL.DEDUCIBLE_EXPENSES,detail.getDeducibleExpense())
				.set(FS_MODEL190_DETAIL.SPOUSAL_SUPPORT,detail.getCompensatoryPension())
				.set(FS_MODEL190_DETAIL.FOOD_ANNUITY,detail.getFoodAnnuality())
				.set(FS_MODEL190_DETAIL.LESS_THAN_3_DESCENDENT,detail.getLessThan3Descendent())
				.set(FS_MODEL190_DETAIL.LESS_THAN_3_DESCENDENT_RATIO,detail.getLessThan3DescendentRatio())
				.set(FS_MODEL190_DETAIL.OTHER_DESCENDENT,detail.getOtherDescendent())
				.set(FS_MODEL190_DETAIL.OTHER_DESCENDENT_RATIO,detail.getOtherDescendentRatio())
				.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_33,detail.getDisabilityDescendent33())
				.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_33_RATIO,detail.getDisabilityDescendent33Ratio())
				.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_DEPENDENCE,detail.getDisabilityDescendentDependence())
				.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_DEPENDENCE_RATIO,detail.getDisabilityDescendentDependenceRatio())
				.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_65,detail.getDisabilityDescendent65())
				.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_65_RATIO,detail.getDisabilityDescendent65Ratio())
				.set(FS_MODEL190_DETAIL.LESS_THAN_75_ASCENDANT,detail.getLessThan75Ascendant())
				.set(FS_MODEL190_DETAIL.LESS_THAN_75_ASCENDANT_RATIO,detail.getLessThan75AscendantRatio())
				.set(FS_MODEL190_DETAIL.ASCENDANT, detail.getAscendant())
				.set(FS_MODEL190_DETAIL.ASCENDANT_RATIO,detail.getAscendantRatio())
				.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_33,detail.getDisabilityAscendant33())
				.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_33_RATIO,detail.getDisabilityAscendant33Ratio())
				.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_DEPENDENCE,detail.getDisabilityAscendantDependence())
				.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_DEPENDENCE_RATIO,detail.getDisabilityAscendantDependenceRatio())
				.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_65,detail.getDisabilityAscendant65())
				.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_65_RATIO,detail.getDisabilityAscendant65Ratio())
				.set(FS_MODEL190_DETAIL.FIRST_CHILD_CALCULATION,detail.getFirstChildCalculation())
				.set(FS_MODEL190_DETAIL.SECOND_CHILD_CALCULATION,detail.getSecondChildCalculation())
				.set(FS_MODEL190_DETAIL.THIRD_CHILD_CALCULATION,detail.getThirdChildCalculation())
				.set(FS_MODEL190_DETAIL.HOME_LOAN_COMMUNNICATION,AonEnumUtils.getByte(detail.isHomeLoanCommunnication()))
				.set(FS_MODEL190_DETAIL.PERCEPTION_IL, detail.getPerceptionIL())
				.set(FS_MODEL190_DETAIL.RETENTION_IL, detail.getRetentionIL())
				.set(FS_MODEL190_DETAIL.OUTPUT_RETENTION_IL, detail.getOutputRetentionIL())
				.set(FS_MODEL190_DETAIL.IN_KIND_PERCEPTION_IL, detail.getInKindPerceptionIL())
				.set(FS_MODEL190_DETAIL.IN_KIND_DEPOSIT_IL, detail.getInKindDepositIL())
				.set(FS_MODEL190_DETAIL.IN_KIND_OUTPUT_DEPOSIT_IL, detail.getInKindOutputDepositIL())
				.set(FS_MODEL190_DETAIL.TIT_CONVIVENCIA, detail.getTitConvivencia())
				.set(FS_MODEL190_DETAIL.COMP_INFANCIA, detail.getCompInfancia())
				.set(FS_MODEL190_DETAIL.COMMON_RETENTION, detail.getCommonRetention())
				.set(FS_MODEL190_DETAIL.NAVARRA_RETENTION, detail.getNavarraRetention())
				.set(FS_MODEL190_DETAIL.ARABA_RETENTION, detail.getArabaRetention())
				.set(FS_MODEL190_DETAIL.BIZKAIA_RETENTION, detail.getBizkaiaRetention())
				.set(FS_MODEL190_DETAIL.GIPUZKOA_RETENTION, detail.getGipuzkoaRetention())
				.set(FS_MODEL190_DETAIL.EXCESSES, AonEnumUtils.getByte(detail.isExcesses()))
				;
	}

	private static void insertDetail(AONContext ctx, Mod190Detail detail) {
		getinsertDetailStatement(ctx, detail).execute();
	}

	private static void updateDetail(AONContext ctx, Mod190Detail detail) {
		ctx.getDslContext()
				.update(FS_MODEL190_DETAIL)
				.set(FS_MODEL190_DETAIL.DOCUMENT,AonStringUtils.substring(detail.getDocument(), 0, 9))
				.set(FS_MODEL190_DETAIL.NAME,AonStringUtils.substring(detail.getName(), 0, 40))
				.set(FS_MODEL190_DETAIL.REPRESENTATIVE_DOCUMENT,AonStringUtils.substring(detail.getRepresentativeDocument(), 0, 9))
				.set(FS_MODEL190_DETAIL.PROVINCE, detail.getProvince())
				.set(FS_MODEL190_DETAIL.KEY, detail.getKey())
				.set(FS_MODEL190_DETAIL.SUBKEY, detail.getSubKey())
				.set(FS_MODEL190_DETAIL.PERCEPTION, detail.getPerception())
				.set(FS_MODEL190_DETAIL.RETENTION, detail.getRetention())
				.set(FS_MODEL190_DETAIL.IN_KIND_PERCEPTION,detail.getInKindPerception())
				.set(FS_MODEL190_DETAIL.IN_KIND_DEPOSIT,detail.getInKindDeposit())
				.set(FS_MODEL190_DETAIL.IN_KIND_OUTPUT_DEPOSIT,detail.getInKindOutputDeposit())
				.set(FS_MODEL190_DETAIL.ACCRUAL_YEAR, detail.getAccrualYear())
				.set(FS_MODEL190_DETAIL.CEUTA_MELILLA, detail.getCeutaMelillaPalma())
				.set(FS_MODEL190_DETAIL.BIRTH_YEAR, detail.getBirthYear())
				.set(FS_MODEL190_DETAIL.FAMILY_SITUATION,detail.getFamilySituation())
				.set(FS_MODEL190_DETAIL.SPOUSE_DOCUMENT,detail.getSpouseDocument())
				.set(FS_MODEL190_DETAIL.DISABILITY, detail.getDisability())
				.set(FS_MODEL190_DETAIL.CONTRACT, detail.getContract())
				.set(FS_MODEL190_DETAIL.LABOUR_PROLONGATION,AonEnumUtils.getByte(detail.isWorkActivityExtension()))
				.set(FS_MODEL190_DETAIL.GEOGRAPHIC_MOBILITY,AonEnumUtils.getByte(detail.isGeographicMobility()))
				.set(FS_MODEL190_DETAIL.APPLICABLE_REDUCTION,detail.getApplicableReduction())
				.set(FS_MODEL190_DETAIL.DEDUCIBLE_EXPENSES,detail.getDeducibleExpense())
				.set(FS_MODEL190_DETAIL.SPOUSAL_SUPPORT,detail.getCompensatoryPension())
				.set(FS_MODEL190_DETAIL.FOOD_ANNUITY,detail.getFoodAnnuality())
				.set(FS_MODEL190_DETAIL.LESS_THAN_3_DESCENDENT,detail.getLessThan3Descendent())
				.set(FS_MODEL190_DETAIL.LESS_THAN_3_DESCENDENT_RATIO,detail.getLessThan3DescendentRatio())
				.set(FS_MODEL190_DETAIL.OTHER_DESCENDENT,detail.getOtherDescendent())
				.set(FS_MODEL190_DETAIL.OTHER_DESCENDENT_RATIO,detail.getOtherDescendentRatio())
				.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_33,detail.getDisabilityDescendent33())
				.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_33_RATIO,detail.getDisabilityDescendent33Ratio())
				.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_DEPENDENCE,detail.getDisabilityDescendentDependence())
				.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_DEPENDENCE_RATIO,detail.getDisabilityDescendentDependenceRatio())
				.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_65,detail.getDisabilityDescendent65())
				.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_65_RATIO,detail.getDisabilityDescendent65Ratio())
				.set(FS_MODEL190_DETAIL.LESS_THAN_75_ASCENDANT,detail.getLessThan75Ascendant())
				.set(FS_MODEL190_DETAIL.LESS_THAN_75_ASCENDANT_RATIO,detail.getLessThan75AscendantRatio())
				.set(FS_MODEL190_DETAIL.ASCENDANT, detail.getAscendant())
				.set(FS_MODEL190_DETAIL.ASCENDANT_RATIO,detail.getAscendantRatio())
				.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_33,detail.getDisabilityAscendant33())
				.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_33_RATIO,detail.getDisabilityAscendant33Ratio())
				.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_DEPENDENCE,detail.getDisabilityAscendantDependence())
				.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_DEPENDENCE_RATIO,detail.getDisabilityAscendantDependenceRatio())
				.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_65,detail.getDisabilityAscendant65())
				.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_65_RATIO,detail.getDisabilityAscendant65Ratio())
				.set(FS_MODEL190_DETAIL.FIRST_CHILD_CALCULATION,detail.getFirstChildCalculation())
				.set(FS_MODEL190_DETAIL.SECOND_CHILD_CALCULATION,detail.getSecondChildCalculation())
				.set(FS_MODEL190_DETAIL.THIRD_CHILD_CALCULATION,detail.getThirdChildCalculation())
				.set(FS_MODEL190_DETAIL.HOME_LOAN_COMMUNNICATION,AonEnumUtils.getByte(detail.isHomeLoanCommunnication()))
				.set(FS_MODEL190_DETAIL.PERCEPTION_IL, detail.getPerceptionIL())
				.set(FS_MODEL190_DETAIL.RETENTION_IL, detail.getRetentionIL())
				.set(FS_MODEL190_DETAIL.OUTPUT_RETENTION_IL, detail.getOutputRetentionIL())
				.set(FS_MODEL190_DETAIL.IN_KIND_PERCEPTION_IL, detail.getInKindPerceptionIL())
				.set(FS_MODEL190_DETAIL.IN_KIND_DEPOSIT_IL, detail.getInKindDepositIL())
				.set(FS_MODEL190_DETAIL.IN_KIND_OUTPUT_DEPOSIT_IL, detail.getInKindOutputDepositIL())
				.set(FS_MODEL190_DETAIL.TIT_CONVIVENCIA, detail.getTitConvivencia())
				.set(FS_MODEL190_DETAIL.COMP_INFANCIA, detail.getCompInfancia())
				.set(FS_MODEL190_DETAIL.COMMON_RETENTION, detail.getCommonRetention())
				.set(FS_MODEL190_DETAIL.NAVARRA_RETENTION, detail.getNavarraRetention())
				.set(FS_MODEL190_DETAIL.ARABA_RETENTION, detail.getArabaRetention())
				.set(FS_MODEL190_DETAIL.BIZKAIA_RETENTION, detail.getBizkaiaRetention())
				.set(FS_MODEL190_DETAIL.GIPUZKOA_RETENTION, detail.getGipuzkoaRetention())
				.set(FS_MODEL190_DETAIL.EXCESSES, AonEnumUtils.getByte(detail.isExcesses()))
				.where(FS_MODEL190_DETAIL.ID.equal(detail.getId())).execute();
	}

	private static void validate(AONContext ctx, Mod190 mod190) {
		if (mod190.isReplacement() || mod190.isComplementary()) {
			// Se comprueba que exista la declaración sustituida.
			if (!ctx.getDslContext().selectOne()
					.from(FS_MODEL190)
					.where(FS_MODEL190.YEAR.equal(mod190.getYear())
					.and(FS_MODEL190.ADMINISTRATION.equal(mod190.getAdministration().value()))
					.and(FS_MODEL190.ENTERPRISE.equal(mod190.getEnterprise()))
					)
					.fetch()
					.stream()
					.findFirst()
					.isPresent()) 
				throw new AonCoreException(
						AonError.FISCAL_NO_REPLACED_DECLARATION.getMessage());
		} else {
			// Se comprueba que no exista ya una declaración.
			if (ctx.getDslContext().selectOne()
				.from(FS_MODEL190)
				.where(FS_MODEL190.YEAR.equal(mod190.getYear())
				.and(FS_MODEL190.ADMINISTRATION.equal(mod190.getAdministration().value()))
				.and(FS_MODEL190.ENTERPRISE.equal(mod190.getEnterprise()))
				.and(FS_MODEL190.REPLACEMENT.equal(ZERO_BYTE))
				.and(FS_MODEL190.COMPLEMENTARY.equal(ZERO_BYTE)))
				.fetch()
				.stream()
				.findFirst()
				.isPresent()) 
				throw new AonCoreException(
						AonError.FISCAL_DECLARATION_ALREADY_EXISTS.getMessage());
		}
	}

	public static void delete(AONContext ctx, Mod190 mod190) {
		ctx.checkWrite();
		deleteDetails(ctx, mod190);
		ctx.getDslContext().delete(FS_MODEL190)
				.where(FS_MODEL190.ID.equal(mod190.getId())).execute();
	}

	private static void deleteDetails(AONContext ctx, Mod190 mod190) {
		ctx.getDslContext().delete(FS_MODEL190_DETAIL)
				.where(FS_MODEL190_DETAIL.FS_MODEL190.equal(mod190.getId()))
				.execute();
	}

	private static void deleteDetail(AONContext ctx, Mod190Detail detail) {
		ctx.getDslContext().delete(FS_MODEL190_DETAIL)
				.where(FS_MODEL190_DETAIL.ID.equal(detail.getId())).execute();
	}

	public static Stream<Mod190> getHeaders(AONContext ctx, int domain) {
		return getHeaders(ctx, domain, null);
	}
	public static Stream<Mod190> getHeaders(AONContext ctx, int domain, Integer scope) {
		ctx.checkRead();
		return  ctx.getDslContext()
				.select(FS_MODEL190.fields())
				.select(DOMAIN.DESCRIPTION)
				.from(FS_MODEL190)
				.join(DOMAIN)
				.on(FS_MODEL190.DOMAIN.equal(DOMAIN.ID))
				.where(FS_MODEL190.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
				.and( scope == null ? DSL.trueCondition() : DOMAIN.SCOPE.equal(scope))
				.orderBy(FS_MODEL190.YEAR.desc(), FS_MODEL190.NAME.asc(),FS_MODEL190.REPLACEMENT.asc())
				.fetch()
				.stream()
				.map(new Mod190Filler());
	}

	public static LinkedList<Mod190> getByDomain(AONContext ctx, int domain) {
		ctx.checkRead();
		return  ctx.getDslContext()
				.select(FS_MODEL190.fields())
				.select(DOMAIN.DESCRIPTION)
				.from(FS_MODEL190)
				.join(DOMAIN).on(FS_MODEL190.DOMAIN.equal(DOMAIN.ID))
				.where(FS_MODEL190.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
				.orderBy(FS_MODEL190.YEAR.desc(), FS_MODEL190.NAME.asc(),FS_MODEL190.REPLACEMENT.asc())
				.fetch()
				.stream()
				.map(new Mod190Filler())
				.map( mod190 -> mod190.setDetails( getDetails(ctx, mod190.getId()) ))
				.collect(Collectors.toCollection(LinkedList::new));
	}

	public static Mod190 getById(AONContext ctx, int id) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(FS_MODEL190.fields())
			.select(DOMAIN.DESCRIPTION)
			.from(FS_MODEL190)
			.join(DOMAIN).on(FS_MODEL190.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL190.ID.equal(id))
			.fetch()
			.stream()
			.map(new Mod190Filler())
			.map( mod190 -> mod190.setDetails( getDetails(ctx, mod190.getId()) ))
			.findFirst()
			.orElse(null);
	}

	public static Mod190Detail getDetail(AONContext ctx, int id) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(FS_MODEL190_DETAIL.fields())
			.from(FS_MODEL190_DETAIL)
			.where(FS_MODEL190_DETAIL.ID.equal(id))
			.fetch()
			.stream()
			.map( new Mod190DetailFiller())
			.findFirst()
			.orElse(null);
	}

	public static LinkedList<Mod190Detail> getDetails(AONContext ctx, int mod190) {
		ctx.checkRead();
		return ctx.getDslContext()
			.selectFrom(FS_MODEL190_DETAIL)
			.where(FS_MODEL190_DETAIL.FS_MODEL190.equal(mod190))
			.orderBy(FS_MODEL190_DETAIL.NAME,FS_MODEL190_DETAIL.ACCRUAL_YEAR)
			.fetch()
			.stream()
			.map( new Mod190DetailFiller() )
			.collect(Collectors.toCollection(LinkedList::new));
	}



	public static Mod190 initialize(AONContext ctx, int year) {
		Mod190 mod190 = new Mod190();
		AonConfiguration conf = ConfigurationDAO.getConfiguration(ctx);
		mod190.setEnterprise(conf.getCompany().getId());
		mod190.setDomain(ctx.getDomainId());
		mod190.setDocument(conf.getCompany().getDocument());
		mod190.setName(AonStringUtils.left(conf.getCompany().getName(), FS_MODEL190.NAME.getDataType().length()));
		mod190.setYear(year);
		mod190.setReceipt("1900000000001");
		mod190.setAdministration(conf.fiscal().getAdministration()!=null?Administration.safeValueOf(conf.fiscal().getAdministration()):Administration.COMMON_TERRITORY);
		mod190.setContactPerson(AonStringUtils.left(conf.fiscal().getContactPerson(),FS_MODEL190.CONTACT_PERSON.getDataType().length()));
		mod190.setContactPhone(AonStringUtils.left(conf.fiscal().getContactPhone(),FS_MODEL190.CONTACT_PHONE.getDataType().length()));
		mod190.setContactMail(AonStringUtils.left(conf.fiscal().getContactMail(),FS_MODEL190.CONTACT_MAIL.getDataType().length()));
		mod190.setStatus(FiscalStatus.PENDING);
		return mod190;
	}

	private static class Mod190Filler implements Function<Record, Mod190> {

		@Override
		public Mod190 apply(Record rec) {
			return new Mod190()
				.setId(rec.getValue(FS_MODEL190.ID))
				.setDomain(rec.getValue(FS_MODEL190.DOMAIN))
				.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
				.setEnterprise(rec.getValue(FS_MODEL190.ENTERPRISE))
				.setYear(rec.getValue(FS_MODEL190.YEAR))
				.setAdministration( com.esferalia.aon.watson.util.AonEnumUtils.enumValue(Administration.class,rec.getValue(FS_MODEL190.ADMINISTRATION)))
				.setReplacement( rec.getValue(FS_MODEL190.REPLACEMENT)==1 )
				.setComplementary(rec.getValue(FS_MODEL190.COMPLEMENTARY)==1 )
				.setStatus(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(FiscalStatus.class,rec.getValue(FS_MODEL190.STATUS)))
				.setDocument(rec.getValue(FS_MODEL190.DOCUMENT))
				.setName(rec.getValue(FS_MODEL190.NAME))
				.setContactPerson(rec.getValue(FS_MODEL190.CONTACT_PERSON))
				.setContactPhone(rec.getValue(FS_MODEL190.CONTACT_PHONE))
				.setContactMail(rec.getValue(FS_MODEL190.CONTACT_MAIL))
				.setReceipt(rec.getValue(FS_MODEL190.RECEIPT))
				.setReplacedReceipt(rec.getValue(FS_MODEL190.REPLACED_RECEIPT))
				.setReceiverCountTotal(rec.getValue(FS_MODEL190.RECEIVER_COUNT_TOTAL))
				.setReceiptTotal(rec.getValue(FS_MODEL190.RECEIPT_TOTAL))
				.setRetentionTotal(rec.getValue(FS_MODEL190.RETENTION_TOTAL))
				.setComments(rec.getValue(FS_MODEL190.COMMENTS))
				.setCreationDate(rec.getValue(FS_MODEL190.CREATION_DATE))
				.setCreationUser(rec.getValue(FS_MODEL190.CREATION_USER))
				.setModificationDate(rec.getValue(FS_MODEL190.MODIFICATION_DATE))
				.setModificationUser(rec.getValue(FS_MODEL190.MODIFICATION_USER));
		}
	}
	
	private static class Mod190DetailFiller implements Function<Record, Mod190Detail> {

		@Override
		public Mod190Detail apply(Record rec) {
			return new Mod190Detail()
				.setId(rec.getValue(FS_MODEL190_DETAIL.ID))
				.setDocument(rec.getValue(FS_MODEL190_DETAIL.DOCUMENT))
				.setName(rec.getValue(FS_MODEL190_DETAIL.NAME))
				.setRepresentativeDocument(rec.getValue(FS_MODEL190_DETAIL.REPRESENTATIVE_DOCUMENT))
				.setProvince(rec.getValue(FS_MODEL190_DETAIL.PROVINCE))
				.setKey(rec.getValue(FS_MODEL190_DETAIL.KEY))
				.setSubKey(rec.getValue(FS_MODEL190_DETAIL.SUBKEY))
				.setPerception(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.PERCEPTION)))
				.setInKindPerception(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.IN_KIND_PERCEPTION)))
				.setInKindDeposit(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.IN_KIND_DEPOSIT)))
				.setInKindOutputDeposit(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.IN_KIND_OUTPUT_DEPOSIT)))
				.setAccrualYear(rec.getValue(FS_MODEL190_DETAIL.ACCRUAL_YEAR))
				.setRetention(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.RETENTION)))
				.setPerceptionIL(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.PERCEPTION_IL)))
				.setRetentionIL(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.RETENTION_IL)))
				.setOutputRetentionIL(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.OUTPUT_RETENTION_IL)))
				.setInKindPerceptionIL(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.IN_KIND_PERCEPTION_IL)))
				.setInKindDepositIL(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.IN_KIND_DEPOSIT_IL)))
				.setInKindOutputDepositIL(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.IN_KIND_OUTPUT_DEPOSIT_IL)))
				.setBirthYear(rec.getValue(FS_MODEL190_DETAIL.BIRTH_YEAR))
				.setCeutaMelillaPalma(rec.getValue(FS_MODEL190_DETAIL.CEUTA_MELILLA))
				.setFamilySituation(rec.getValue(FS_MODEL190_DETAIL.FAMILY_SITUATION))
				.setSpouseDocument(rec.getValue(FS_MODEL190_DETAIL.SPOUSE_DOCUMENT))
				.setDisability(rec.getValue(FS_MODEL190_DETAIL.DISABILITY))
				.setContract(rec.getValue(FS_MODEL190_DETAIL.CONTRACT))
				.setWorkActivityExtension(AonEnumUtils.getBoolean(rec.getValue(FS_MODEL190_DETAIL.LABOUR_PROLONGATION)))
				.setGeographicMobility(AonEnumUtils.getBoolean(rec.getValue(FS_MODEL190_DETAIL.GEOGRAPHIC_MOBILITY)))
				.setApplicableReduction(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.APPLICABLE_REDUCTION)))
				.setDeducibleExpense(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.DEDUCIBLE_EXPENSES)))
				.setCompensatoryPension(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.SPOUSAL_SUPPORT)))
				.setFoodAnnuality(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.FOOD_ANNUITY)))
				.setHomeLoanCommunnication(AonEnumUtils.getBoolean(rec.getValue(FS_MODEL190_DETAIL.HOME_LOAN_COMMUNNICATION)))
				.setLessThan3Descendent(rec.getValue(FS_MODEL190_DETAIL.LESS_THAN_3_DESCENDENT))
				.setLessThan3DescendentRatio(rec.getValue(FS_MODEL190_DETAIL.LESS_THAN_3_DESCENDENT_RATIO))
				.setOtherDescendent(rec.getValue(FS_MODEL190_DETAIL.OTHER_DESCENDENT))
				.setOtherDescendentRatio(rec.getValue(FS_MODEL190_DETAIL.OTHER_DESCENDENT_RATIO))
				.setDisabilityDescendent33(rec.getValue(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_33))
				.setDisabilityDescendent33Ratio(rec.getValue(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_33_RATIO))
				.setDisabilityDescendentDependence(rec.getValue(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_DEPENDENCE))
				.setDisabilityDescendentDependenceRatio(rec.getValue(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_DEPENDENCE_RATIO))
				.setDisabilityDescendent65(rec.getValue(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_65))
				.setDisabilityDescendent65Ratio(rec.getValue(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_65_RATIO))
				.setLessThan75Ascendant(rec.getValue(FS_MODEL190_DETAIL.LESS_THAN_75_ASCENDANT))
				.setLessThan75AscendantRatio(rec.getValue(FS_MODEL190_DETAIL.LESS_THAN_75_ASCENDANT_RATIO))
				.setAscendant(rec.getValue(FS_MODEL190_DETAIL.ASCENDANT))
				.setAscendantRatio(rec.getValue(FS_MODEL190_DETAIL.ASCENDANT_RATIO))
				.setDisabilityAscendant33(rec.getValue(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_33))
				.setDisabilityAscendant33Ratio(rec.getValue(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_33_RATIO))
				.setDisabilityAscendantDependence(rec.getValue(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_DEPENDENCE))
				.setDisabilityAscendantDependenceRatio(rec.getValue(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_DEPENDENCE_RATIO))
				.setDisabilityAscendant65(rec.getValue(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_65))
				.setDisabilityAscendant65Ratio(rec.getValue(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_65_RATIO))
				.setFirstChildCalculation(rec.getValue(FS_MODEL190_DETAIL.FIRST_CHILD_CALCULATION))
				.setSecondChildCalculation(rec.getValue(FS_MODEL190_DETAIL.SECOND_CHILD_CALCULATION))
				.setThirdChildCalculation(rec.getValue(FS_MODEL190_DETAIL.THIRD_CHILD_CALCULATION))
				.setTitConvivencia(rec.getValue(FS_MODEL190_DETAIL.TIT_CONVIVENCIA))
				.setCompInfancia(rec.getValue(FS_MODEL190_DETAIL.COMP_INFANCIA))
				.setCommonRetention(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.COMMON_RETENTION)))
				.setNavarraRetention(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.NAVARRA_RETENTION)))
				.setArabaRetention(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.ARABA_RETENTION)))
				.setBizkaiaRetention(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.BIZKAIA_RETENTION)))
				.setGipuzkoaRetention(AonMathUtils.round(rec.getValue(FS_MODEL190_DETAIL.GIPUZKOA_RETENTION)))
				.setExcesses(AonEnumUtils.getBoolean(rec.getValue(FS_MODEL190_DETAIL.EXCESSES)))
				;
		}
	}

	public static Mod190 duplicate(AONContext ctx, Mod190 mod190) {
		
		int id = mod190.getId();
		mod190.setId(null);
		mod190 = insert(ctx, mod190, false);
		
		if (!mod190.isComplementary()) {
			Mod190 original = getById(ctx, id);
			for (Mod190Detail detail : original.getDetails()) {
				detail.setId(null);
				detail.setMod190(mod190.getId());
				saveDetail(ctx,mod190,detail);
			}
		}
		return getById(ctx, mod190 .getId());
	}
	
	public static LinkedList<Mod190Detail> validateSalaries(AONContext ctx, Mod190 mod190) {
		Mod190Declaration dec = Mod190Declaration.getInstance(mod190);
		return dec.validateSalaries(ctx, mod190);
	}
	
    // Grabar resultado y pdf en response y marcar el modelo como enviado
	public static Mod190 aeatPresentation(AONContext ctx, Mod190 mod, String aeatResponse) {
		if (AonStringUtils.isNotBlank(aeatResponse)) {			
			
			// Antes de nada se borra la presentaci�n anterior
			DataResponseDAO.deleteAEATResponse(ctx, mod);			
			
			// Grabar los datos en data_response y sus tablas asociadas
			DataResponseDAO.insertAEATResponse(ctx, mod, aeatResponse);
			
			// Marcar el modelo como enviado					
			if (mod != null && mod.getId() != null) {
				ctx.getDslContext().update(FS_MODEL190)					
					.set(FS_MODEL190.STATUS, FiscalStatus.SENT.value())
					.where(FS_MODEL190.ID.equal(mod.getId()))
					.execute();
				return getById(ctx, mod.getId());
			}
		}
		return mod;
	}	
	
}
