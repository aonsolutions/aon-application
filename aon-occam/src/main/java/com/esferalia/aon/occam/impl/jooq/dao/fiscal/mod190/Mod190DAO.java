
package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod190;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.FsModel190.FS_MODEL190;
import static com.esferalia.aon.jooq.tables.FsModel190Detail.FS_MODEL190_DETAIL;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.IrpfData.IRPF_DATA;
import static com.esferalia.aon.jooq.tables.IrpfDataAscendants.IRPF_DATA_ASCENDANTS;
import static com.esferalia.aon.jooq.tables.IrpfDataDescendients.IRPF_DATA_DESCENDIENTS;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.BatchBindStep;
import org.jooq.Field;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.FsModel190DetailRecord;
import com.esferalia.aon.jooq.tables.records.FsModel190Record;
import com.esferalia.aon.jooq.tables.records.IrpfDataAscendantsRecord;
import com.esferalia.aon.jooq.tables.records.IrpfDataDescendientsRecord;
import com.esferalia.aon.jooq.tables.records.IrpfDataRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod1902014Key;
import com.esferalia.aon.occam.api.model.type.Mod1902015Key;
import com.esferalia.aon.occam.api.model.type.Mod1902016Key;
import com.esferalia.aon.occam.api.model.type.PaymentType;
import com.esferalia.aon.occam.api.model.type.PaymentType.PaymentTypeVisitor;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod190DAO {
	private static final byte ZERO_BYTE = 0;
	private static final String PREST_IT = "PREST_IT";
	
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
			insertDetailsFromInvoice(ctx, mod190);
			if (mod190.getYear() < 2017) {
				insertDetailsFromSalary2016(ctx, mod190);
			} else {
				insertDetailsFromSalary2017(ctx, mod190);
			}
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
								FS_MODEL190_DETAIL.IN_KIND_OUTPUT_DEPOSIT_IL
								)
						.values(null, null, null, null, null, null, null, null,
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
					, AonEnumUtils.getByte(detail.isCeutaMelilla())
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
				.set(FS_MODEL190_DETAIL.CEUTA_MELILLA,AonEnumUtils.getByte(detail.isCeutaMelilla()))
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
				.set(FS_MODEL190_DETAIL.CEUTA_MELILLA,AonEnumUtils.getByte(detail.isCeutaMelilla()))
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
			.orderBy(FS_MODEL190_DETAIL.NAME)
			.fetch()
			.stream()
			.map( new Mod190DetailFiller() )
			.collect(Collectors.toCollection(LinkedList::new));
	}

	private static void insertDetailsFromInvoice(AONContext ctx, final Mod190 mod190) {
		java.sql.Date firstDay = AonDateUtils.toSql(AonDateUtils.getYearFirstDay(mod190.getYear()));
		java.sql.Date lastDay = AonDateUtils.toSql(AonDateUtils.getYearLastDay(mod190.getYear()));

		Field<Integer> minRegistry = DSL.min(INVOICE.REGISTRY).as(INVOICE.REGISTRY.getName());
		Field<BigDecimal> sumBase = DSL.sum(INVOICE_TAX.BASE).as(INVOICE_TAX.BASE.getName());
		Field<Double> invoiceTaxSum = DSL.round((INVOICE_TAX.BASE.mul(INVOICE_TAX.PERCENTAGE)).div(100), 2);
		Field<BigDecimal> quotaOp = DSL.sum(DSL.decode()
				.when(INVOICE_TAX.QUOTA.notEqual(0.0), INVOICE_TAX.QUOTA)
				.when(INVOICE_TAX.QUOTA.equal(0.0), invoiceTaxSum));
		Map<String,Mod190Detail> map = new LinkedHashMap<>();
		ctx.getDslContext()
				.select(INVOICE.RDOCUMENT, INVOICE.RNAME,INVOICE_TAX.WITHHOLDING_TYPE, INVOICE_TAX.PERCENTAGE, minRegistry, sumBase,quotaOp)
				.from(INVOICE)
				.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
				.where(INVOICE.DOMAIN.equal(mod190.getDomain()))
				.and(INVOICE.TYPE.notEqual((byte) 1)) 		// No Ventas
				.and(INVOICE_TAX.TAX_TYPE.equal((byte) 2))	// IRPF
				.and(INVOICE_TAX.WITHHOLDING_TYPE.in((byte) 0, (byte) 3,(byte) 4)) 
				.and(INVOICE.ISSUE_DATE.between(firstDay,lastDay))
				.groupBy(INVOICE.RDOCUMENT, INVOICE.RNAME,INVOICE_TAX.WITHHOLDING_TYPE,INVOICE_TAX.PERCENTAGE)
				.fetch()
				.stream()
				.forEach(
						rec -> {
							
							String document = rec.getValue(INVOICE.RDOCUMENT);
							String key = null;
							String subKey = null;
							
							double percent = rec.getValue(INVOICE_TAX.PERCENTAGE);
							WithholdingType withholding = WithholdingType.safeValueOf(rec.getValue(INVOICE_TAX.WITHHOLDING_TYPE));
							if (withholding == WithholdingType.PROFESSIONAL) {
								if (mod190.getYear() == 2014) {
									key = Mod1902014Key.getDefaultKeyForProfessionalRetentions().getValue();
									subKey = Mod1902014Key.getDefaultSubkeyForProfessionalRetentions();
								}else if (mod190.getYear() == 2015) {
									key = Mod1902015Key.getDefaultKeyForProfessionalRetentions().getValue();
									subKey = Mod1902015Key.getDefaultSubkeyForProfessionalRetentions();
								} else{
									key = Mod1902016Key.getDefaultKeyForProfessionalRetentions().getValue();
									if (AonNumberUtils.equals(percent, 7.0) ) {
										subKey = Mod1902016Key.getDefaultSubkeyForNewProfessionalRetentions();
									} else {
										subKey = Mod1902016Key.getDefaultSubkeyForProfessionalRetentions();
									}
								}
							} else if (withholding == WithholdingType.FARMER) { // AGRICULTOR - FARMER
								if (mod190.getYear() == 2014) {
									key = Mod1902014Key.getDefaultKeyForFarmerRetentions().getValue();
									subKey = Mod1902014Key.getDefaultSubkeyForFarmerRetentions();
								} else if (mod190.getYear() == 2015) {
									key = Mod1902015Key.getDefaultKeyForFarmerRetentions().getValue();
									subKey = Mod1902015Key.getDefaultSubkeyForFarmerRetentions();
								} else {
									key = Mod1902016Key.getDefaultKeyForFarmerRetentions().getValue();
									subKey = Mod1902016Key.getDefaultSubkeyForFarmerRetentions();
								}
							} else if (withholding == WithholdingType.TRANSPORT_OPERATOR) { // TRANSPORTISTAS Y ASIMILADOS - TRANSPORT_OPERATOR
								if (mod190.getYear() == 2014) {
									key = Mod1902014Key.getDefaultKeyForTransportRetentions().getValue();
									subKey = Mod1902014Key.getDefaultSubkeyForTransportRetentions();
								} else if (mod190.getYear() == 2015) {
									key = Mod1902015Key.getDefaultKeyForTransportRetentions().getValue();
									subKey = Mod1902015Key.getDefaultSubkeyForTransportRetentions();
								} else {
									key = Mod1902016Key.getDefaultKeyForTransportRetentions().getValue();
									subKey = Mod1902016Key.getDefaultSubkeyForTransportRetentions();
								}
							}
							String mapKey = document + "|" + key + "|" + subKey;
							Mod190Detail detail = null; 
							if (!map.containsKey(mapKey)) {
								detail = new Mod190Detail();
								detail.setDomain(mod190.getDomain());
								detail.setMod190(mod190.getId());
								detail.setDocument(document);
								detail.setName(rec.getValue(INVOICE.RNAME));
								detail.setProvince( RegistryAddressDAO.getMainAddressProvince(ctx, rec.getValue(minRegistry)) );
								detail.setKey(key);
								detail.setSubKey(subKey);
								map.put(mapKey, detail);
							}
							detail = map.get(mapKey);
							detail.setPerception(AonMathUtils.round(detail.getPerception() + rec.getValue(sumBase).doubleValue()));
							detail.setRetention(AonMathUtils.round(detail.getRetention() + rec.getValue(quotaOp).doubleValue()));
						});
		mod190.getDetails().addAll(map.values());
	}

	private static void fillLastIrpfDataByPerson(AONContext ctx, int person, Date fromDate, Date toDate, Mod190Detail detail) {
		ctx.checkRead();
		List<IrpfDataRecord> list = ctx.getDslContext()
				.select(IRPF_DATA.fields())
				.from(IRPF_DATA)
				.join(CONTRACT)
				.on(IRPF_DATA.CONTRACT.equal(CONTRACT.ID))
				.where(IRPF_DATA.END_DATE.isNull().or(
						IRPF_DATA.END_DATE.between(
								AonDateUtils.toSql(fromDate),
								AonDateUtils.toSql(toDate))))
				.and(CONTRACT.PERSON.equal(person))
				.orderBy(IRPF_DATA.END_DATE.desc(),IRPF_DATA.START_DATE.asc())
				.fetchInto(IrpfDataRecord.class);
		
		detail.setContract((byte) 1);
		
		if (list != null && list.size() > 0) { 
			IrpfDataRecord record = list.get(0);
			detail.setCeutaMelilla(AonEnumUtils.getBoolean(record.getCeutaMelilla()));
			Byte familySituation = record.getFamilySituation();
			if (familySituation != null) {
				familySituation = (byte) (familySituation + 1);
			} else {
				familySituation = (byte) 0;
			}
			detail.setFamilySituation(familySituation);
			detail.setSpouseDocument(record.getSpouseDocument());
			Byte disabilityLevel = record.getDisabilityLevel();
			if (disabilityLevel != null) {
				disabilityLevel = (byte) (disabilityLevel + 1);
			} else {
				disabilityLevel = (byte) 0;
			}
			detail.setDisability(disabilityLevel);
			detail.setContract((byte) (record.getContractType() + 1));
			detail.setWorkActivityExtension(AonEnumUtils.getBoolean(record.getLabourProlongation()));
			detail.setGeographicMobility(record.getMovingDate() != null);
			
			List<IrpfDataDescendientsRecord> descs = ctx.getDslContext()
				.select(IRPF_DATA_DESCENDIENTS.fields())
				.from(IRPF_DATA_DESCENDIENTS)
				.where(IRPF_DATA_DESCENDIENTS.IRPF_DATA.eq(record.getId()))
				.orderBy(IRPF_DATA_DESCENDIENTS.BIRTH_YEAR)
				.fetchInto(IrpfDataDescendientsRecord.class);
			int curYear = AonDateUtils.getYear(fromDate);
			byte ZERO = 0;
			byte ONE = 1;
			byte TWO = 2;
			if (descs != null && descs.size() > 0) {
				int i = 1;
				for (IrpfDataDescendientsRecord desc : descs) {
					int descYear = desc.getAdoptionYear() == null?desc.getBirthYear():desc.getAdoptionYear();
					boolean lessThan3 = ( curYear - 3 ) <=  descYear;
					boolean disability = desc.getDisabilityLevel() != null;
					boolean disability33 = desc.getDisabilityLevel() != null && desc.getDisabilityLevel() == 0;
					boolean disability65 = desc.getDisabilityLevel() != null && desc.getDisabilityLevel() == 2;
					boolean dependence = desc.getDependence() != null && desc.getDependence() == 1;
					boolean byInteger = desc.getUniqueParent() != null && desc.getUniqueParent() == 1;
					if (lessThan3) {
						detail.setLessThan3Descendent( (byte) (detail.getLessThan3Descendent() + ONE) );
						detail.setLessThan3DescendentRatio( (byte) (detail.getLessThan3DescendentRatio() + (byInteger?ONE:ZERO)));
					} else {
						detail.setOtherDescendent( (byte) (detail.getOtherDescendent() + ONE) );
						detail.setOtherDescendentRatio( (byte) (detail.getOtherDescendentRatio() + (byInteger?ONE:ZERO)));
					}
					if (disability) {
						if (disability33) {
							detail.setDisabilityDescendent33( (byte) (detail.getDisabilityDescendent33() + ONE) );
							detail.setDisabilityDescendent33Ratio( (byte) (detail.getDisabilityDescendent33Ratio() + (byInteger?ONE:ZERO)));
							if ( dependence ) {
								detail.setDisabilityDescendentDependence( (byte) (detail.getDisabilityDescendentDependence() + ONE) );
								detail.setDisabilityDescendentDependenceRatio( (byte) (detail.getDisabilityDescendentDependenceRatio() + (byInteger?ONE:ZERO)));
							}
						}
						if (disability65) {
							detail.setDisabilityDescendent65( (byte) (detail.getDisabilityDescendent65() + ONE) );
							detail.setDisabilityDescendent65Ratio( (byte) (detail.getDisabilityDescendent65Ratio() + (byInteger?ONE:ZERO)));
						}
					}
					if (i == 1) {
						detail.setFirstChildCalculation(byInteger?ONE:TWO);
					} else if (i == 2) {
						detail.setSecondChildCalculation(byInteger?ONE:TWO);
					} else if (i == 3) {
						detail.setThirdChildCalculation(byInteger?ONE:TWO);
					}
					i++;
				}
			}
			
			List<IrpfDataAscendantsRecord> ascs = ctx.getDslContext()
					.select(IRPF_DATA_ASCENDANTS.fields())
					.from(IRPF_DATA_ASCENDANTS)
					.where(IRPF_DATA_ASCENDANTS.IRPF_DATA.eq(record.getId()))
					.orderBy(IRPF_DATA_ASCENDANTS.BIRTH_YEAR)
					.fetchInto(IrpfDataAscendantsRecord.class);
			if (ascs != null && ascs.size() > 0) {
				for (IrpfDataAscendantsRecord asc : ascs) {
					boolean lessThan75 = ( curYear - 75 ) <  asc.getBirthYear();
					boolean byInteger = asc.getAnotherDescendient() != null && asc.getAnotherDescendient() == 0;
					boolean disability = asc.getDisabilityLevel() != null;
					boolean disability33 = asc.getDisabilityLevel() != null && asc.getDisabilityLevel() == 0;
					boolean disability65 = asc.getDisabilityLevel() != null && asc.getDisabilityLevel() == 2;
					boolean dependence = asc.getDependence() != null && asc.getDependence() == 1;
					
					if (lessThan75) {
						detail.setLessThan75Ascendant( (byte) (detail.getLessThan75Ascendant() + ONE) );
						detail.setLessThan75AscendantRatio( (byte) (detail.getLessThan75AscendantRatio() + (byInteger?ONE:ZERO)));
					} else {
						detail.setAscendant( (byte) (detail.getAscendant() + ONE) );
						detail.setAscendantRatio( (byte) (detail.getAscendantRatio() + (byInteger?ONE:ZERO)));
					}
					
					if (disability) {
						if (disability33) {
							detail.setDisabilityAscendant33( (byte) (detail.getDisabilityAscendant33() + ONE) );
							detail.setDisabilityAscendant33Ratio( (byte) (detail.getDisabilityAscendant33Ratio() + (byInteger?ONE:ZERO)));
							if ( dependence ) {
								detail.setDisabilityAscendantDependence( (byte) (detail.getDisabilityAscendantDependence() + ONE) );
								detail.setDisabilityAscendantDependenceRatio( (byte) (detail.getDisabilityAscendantDependenceRatio() + (byInteger?ONE:ZERO)));
							}
						}
						if (disability65) {
							detail.setDisabilityAscendant65( (byte) (detail.getDisabilityAscendant65() + ONE) );
							detail.setDisabilityAscendant65Ratio( (byte) (detail.getDisabilityAscendant65Ratio() + (byInteger?ONE:ZERO)));
						}
					}
				}
			}
				
			
		}
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
				.setCeutaMelilla(AonEnumUtils.getBoolean(rec.getValue(FS_MODEL190_DETAIL.CEUTA_MELILLA)))
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
				;
		}
	}

	private static void insertDetailsFromSalary2016(AONContext ctx, final Mod190 mod190) {
		Date firstDay = AonDateUtils.getYearFirstDay(mod190.getYear());
		Date lastDay = AonDateUtils.getYearLastDay(mod190.getYear());
		
		Field<Integer> birthYear = DSL.year(PERSON.BIRTH_DATE);
		final TreeMap<String, Mod190Detail> map = new TreeMap<String, Mod190Detail>();
		ctx.getDslContext()
				.select(SALARY.EMPLOYEE_DOCUMENT
						,SALARY.EMPLOYEE_NAME
						,SALARY.IRPF_BASE
						,SALARY.MONEY_IRPF_BASE
						,SALARY.INKIND_IRPF_BASE
						,SALARY.TOTAL_IRPF
						,SALARY.SOCIAL_SECURITY_CONTRIBUTIONS
						,PERSON.REGISTRY
						,birthYear)
				.from(SALARY)
				.join(CONTRACT).on(SALARY.CONTRACT.equal(CONTRACT.ID))
				.join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
				.join(PERSON).on(PERSON.REGISTRY.equal(CONTRACT.PERSON))
				.where(SALARY.ISSUE_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
				.and(WORKPLACE.ENTERPRISE.equal(mod190.getEnterprise()))
				.and(WORKPLACE.ECONOMICAGREEMENT.equal(mod190.getAdministration().value()))
				.and(SALARY.TYPE.in(SalaryType.IRPF_SALARIES )) // Skip SLD ( L00, L13... )
				.orderBy(SALARY.EMPLOYEE_DOCUMENT)
				.fetch()
				.stream()
				.forEach(
						salaryData -> {
							String document = salaryData.getValue(SALARY.EMPLOYEE_DOCUMENT);
							Integer person = salaryData.getValue(PERSON.REGISTRY);
							String key = document + "_" + person;
							if (!map.containsKey(key)) {
								final Mod190Detail detail = new Mod190Detail();
								map.put(key, detail);
								detail.setDomain(mod190.getDomain());
								detail.setMod190(mod190.getId());
								detail.setDocument(salaryData.getValue(SALARY.EMPLOYEE_DOCUMENT));
								detail.setName(salaryData.getValue(SALARY.EMPLOYEE_NAME));
								Integer birthData = salaryData.getValue(birthYear);
								detail.setBirthYear(birthData==null?0:birthData);
								fillLastIrpfDataByPerson(ctx,salaryData.getValue(PERSON.REGISTRY),firstDay, lastDay, detail);
								ctx.getDslContext()
									.select(GEOZONE.CODE)
									.from(RADDRESS)
									.join(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
									.where(RADDRESS.REGISTRY.equal(salaryData.getValue(PERSON.REGISTRY)))
									.and(RADDRESS.TYPE.equal((byte) 0))
									.limit(1)
									.fetch()
									.stream()
									.forEach(
										province -> {
											try {detail.setProvince(Integer.parseInt(province.getValue(GEOZONE.CODE)));
											} catch (NumberFormatException e) {
												// nothing. If not a number,  not a valid province.
											}
										});
							}
							Mod190Detail detail = map.get(key);
							
							double irpfBase = AonMathUtils.round( salaryData.getValue(SALARY.IRPF_BASE));
							double moneyIrpfBase = AonMathUtils.round( salaryData.getValue(SALARY.MONEY_IRPF_BASE));
							double inKindIrpfBase = AonMathUtils.round( salaryData.getValue(SALARY.INKIND_IRPF_BASE));
							double totalIrpf = AonMathUtils.round( salaryData.getValue(SALARY.TOTAL_IRPF));
							double socialSecurityContributions = AonMathUtils.round( salaryData.getValue(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS));
							double moneyQuota = 0;
							double inKindQuota = 0;
							
							if (inKindIrpfBase == 0.0) {
								moneyIrpfBase = irpfBase;
								moneyQuota = totalIrpf; 	
								inKindQuota = 0;
							} else {
								moneyQuota = AonMathUtils.round(moneyIrpfBase * totalIrpf / irpfBase);
								inKindQuota = AonMathUtils.round(totalIrpf - moneyQuota);
							}
							
							detail.setKey(Mod1902015Key.A.getValue());
							if (mod190.getYear() == 2015) {
								detail.setSubKey("01");
							} 
							detail.setPerception(AonMathUtils.round( detail.getPerception() + moneyIrpfBase ));
							detail.setRetention(AonMathUtils.round( detail.getRetention() + moneyQuota));
							
							detail.setInKindPerception(AonMathUtils.round( detail.getInKindPerception() + inKindIrpfBase));
							detail.setInKindDeposit(AonMathUtils.round( detail.getInKindDeposit() + inKindQuota));

							detail.setDeducibleExpense(AonMathUtils.round( detail.getDeducibleExpense() + socialSecurityContributions ));
						});
		mod190.getDetails().addAll( map.values() );
	}

	private static void insertDetailsFromSalary2017(AONContext ctx, final Mod190 mod190) {
		Date firstDay = AonDateUtils.getYearFirstDay(mod190.getYear());
		Date lastDay = AonDateUtils.getYearLastDay(mod190.getYear());
		Field<Integer> birthYear = DSL.year(PERSON.BIRTH_DATE);
		final TreeMap<String, Mod190Detail> map = new TreeMap<String, Mod190Detail>();
		final HashSet<Integer> salaries = new HashSet<Integer>();
		ctx.getDslContext().select(
				 SALARY.ID
				,SALARY.EMPLOYEE_DOCUMENT
				,SALARY.EMPLOYEE_NAME
				,SALARY.TOTAL_IRPF
				,SALARY.IRPF_BASE
				,SALARY.SOCIAL_SECURITY_CONTRIBUTIONS

				,ENTERPRISE_CCC.TYPE
				
				,SALARY_PAYMENT.TYPE
				,SALARY_PAYMENT.PAYMENT_CONCEPT
				,SALARY_PAYMENT.AMOUNT
				,SALARY_PAYMENT.IRPF
				
				,PERSON.REGISTRY
				,birthYear)
		.from(SALARY_PAYMENT)
		.join(SALARY).on(SALARY_PAYMENT.SALARY.equal(SALARY.ID))
		.join(CONTRACT).on(SALARY.CONTRACT.equal(CONTRACT.ID))
		.join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
		.join(PERSON).on(PERSON.REGISTRY.equal(CONTRACT.PERSON))
		.leftOuterJoin(ENTERPRISE_CCC).on(CONTRACT.ENTERPRISE_CCC.equal(ENTERPRISE_CCC.ID))
		.where(SALARY.ISSUE_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
		.and(WORKPLACE.ENTERPRISE.equal(mod190.getEnterprise()))
		.and(WORKPLACE.ECONOMICAGREEMENT.equal(mod190.getAdministration().value()))
		.and(SALARY.TYPE.in(SalaryType.IRPF_SALARIES )) // Skip SLD ( L00, L13... )
		.orderBy(SALARY.EMPLOYEE_DOCUMENT)
		.fetch()
		.stream()
		.forEach(rec -> {
				String document = rec.getValue(SALARY.EMPLOYEE_DOCUMENT);
				Integer person = rec.getValue(PERSON.REGISTRY);
				
				Byte p = rec.getValue(SALARY_PAYMENT.TYPE);
				PaymentType paymentType = (p == null)?PaymentType.CRA_0001 : PaymentType.values()[ (int) p];
				paymentType.accept(new PaymentTypeVisitor() {

					@Override
					public void visitNonStructuralHours(PaymentType paymentType) {
						visitOther(paymentType);
					}

					@Override
					public void visitStructuralHours(PaymentType paymentType) {
						visitOther(paymentType);
					}

					@Override
					public void visitOther(PaymentType paymentType) {
						if (!isBoss()) visitAKey();
					}

					@Override
					public void visitSalaryInKind(PaymentType paymentType) {
						if (!isBoss()) visitAInKindKey();
					}
					
					@Override
					public void visitCompensation(PaymentType paymentType) {
						if (!isBoss()) visitCompensation();
					}
					
					@Override
					public void visitExpenses(PaymentType paymentType) {
						if (!isBoss()) visitExpenses();
					}

					// -------------------------------------------------------------------------------------
					// -------------------------------------------------------------------------------------
					
					private boolean isBoss() {
						Byte boss = rec.getValue(ENTERPRISE_CCC.TYPE);
						if ( boss != null && boss.byteValue() == 4) {
							double totalIrpf = rec.getValue(SALARY.TOTAL_IRPF);
							double totalIrpfBase = rec.getValue(SALARY.IRPF_BASE);
							double irpfBase = rec.getValue(SALARY_PAYMENT.IRPF);
							double irpfQuota = ( AonMathUtils.isZero( irpfBase) || AonMathUtils.isZero( totalIrpfBase) )
									?0.0
									:(irpfBase * totalIrpf / totalIrpfBase);
							Mod190Detail detail = getDetail(document,person,Mod1902016Key.E,"01");
							detail.setPerception(AonMathUtils.round(detail.getPerception() + irpfBase ));
							detail.setRetention(AonMathUtils.round(detail.getRetention() + irpfQuota ));
							Integer salary = rec.getValue(SALARY.ID);
							if (!salaries.contains(salary)) {
								salaries.add(salary);
								double ss = rec.getValue(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
								detail.setDeducibleExpense(AonMathUtils.round(detail.getDeducibleExpense() + ss ));
							}
							return true;
						}
						return false;
					}

					private void visitExpenses() {
						double amount = rec.getValue(SALARY_PAYMENT.AMOUNT);
						double irpfBase = rec.getValue(SALARY_PAYMENT.IRPF);
						
						// Parte exenta va a la L
						double expense = AonMathUtils.round(amount - irpfBase);  
						Mod190Detail detail = getDetail(document,person,Mod1902016Key.L,"01");
						detail.setPerception(AonMathUtils.round(detail.getPerception() + expense ));
						
						// Parte no exenta va a la A
						visitAKey();	
					}
						
					private void visitCompensation() {
						double amount = rec.getValue(SALARY_PAYMENT.AMOUNT);
						double totalIrpf = rec.getValue(SALARY.TOTAL_IRPF);
						double totalIrpfBase = rec.getValue(SALARY.IRPF_BASE);
						double irpfBase = rec.getValue(SALARY_PAYMENT.IRPF);
						double irpfQuota = ( AonMathUtils.isZero( irpfBase) ||  AonMathUtils.isZero( totalIrpfBase) )
								?0.0
								:(irpfBase * totalIrpf / totalIrpfBase);
						if ( AonMathUtils.isGreatherThanZero(irpfBase )) {
							visitAKey();		
						} else {
							Mod190Detail detail = getDetail(document,person,Mod1902016Key.L,"05");
							detail.setPerception(AonMathUtils.round(detail.getPerception() + amount ));
							detail.setRetention(AonMathUtils.round(detail.getRetention() + irpfQuota ));
						}
					}
					
					private void visitAKey() {
						double totalIrpf = rec.getValue(SALARY.TOTAL_IRPF);
						double totalIrpfBase = rec.getValue(SALARY.IRPF_BASE);
						double irpfBase = rec.getValue(SALARY_PAYMENT.IRPF);
						double irpfQuota = ( AonMathUtils.isZero( irpfBase) || AonMathUtils.isZero( totalIrpfBase) )
								?0.0
								:(irpfBase * totalIrpf / totalIrpfBase);
						Mod190Detail detail = getDetail(document,person,Mod1902016Key.A,null);
						Integer salary = rec.getValue(SALARY.ID);
						if (!salaries.contains(salary)) {
							salaries.add(salary);
							double ss = rec.getValue(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
							detail.setDeducibleExpense(AonMathUtils.round(detail.getDeducibleExpense() + ss ));
						}
						String prest = rec.getValue( SALARY_PAYMENT.PAYMENT_CONCEPT);
						if (PREST_IT.equals(prest)) {
							detail.setPerceptionIL(AonMathUtils.round(detail.getPerceptionIL() + irpfBase ));
							detail.setRetentionIL(AonMathUtils.round(detail.getRetentionIL() + irpfQuota ));
						} else {
							detail.setPerception(AonMathUtils.round(detail.getPerception() + irpfBase ));
							detail.setRetention(AonMathUtils.round(detail.getRetention() + irpfQuota ));
						}
					}
					
					private void visitAInKindKey() {
						double totalIrpf = rec.getValue(SALARY.TOTAL_IRPF);
						double totalIrpfBase = rec.getValue(SALARY.IRPF_BASE);
						double irpfBase = rec.getValue(SALARY_PAYMENT.IRPF);
						double irpfQuota = ( AonMathUtils.isZero( irpfBase) || AonMathUtils.isZero( totalIrpfBase ) )
								?0.0
								:(irpfBase * totalIrpf / totalIrpfBase);
						Mod190Detail detail = getDetail(document,person,Mod1902016Key.A,null);
						String prest = rec.getValue( SALARY_PAYMENT.PAYMENT_CONCEPT);
						if (PREST_IT.equals(prest)) {
							detail.setInKindPerceptionIL(AonMathUtils.round(detail.getInKindPerceptionIL() + irpfBase ));
							detail.setInKindDepositIL(AonMathUtils.round(detail.getInKindDepositIL() + irpfQuota ));
						} else {
							detail.setInKindPerception(AonMathUtils.round(detail.getInKindPerception() + irpfBase ));
							detail.setInKindDeposit(AonMathUtils.round(detail.getInKindDeposit() + irpfQuota ));
						}
					}
					
					private Mod190Detail getDetail(String document,int person, Mod1902016Key key, String subKey) {
						String mapKey =  document + "_" + person + "_" + key.getValue() + (subKey != null?("_" + subKey):"");
						if ( !map.containsKey(mapKey) ) {
							final Mod190Detail detail = new Mod190Detail();
							map.put(mapKey, detail);
							detail.setDomain(mod190.getDomain());
							detail.setMod190(mod190.getId());
							detail.setKey(key.getValue());
							detail.setSubKey(subKey);
							detail.setDocument(rec.getValue(SALARY.EMPLOYEE_DOCUMENT));
							detail.setName(rec.getValue(SALARY.EMPLOYEE_NAME));
							ctx.getDslContext().select(GEOZONE.CODE)
								.from(RADDRESS)
								.join(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
								.where(RADDRESS.REGISTRY.equal(rec.getValue(PERSON.REGISTRY)))
								.and(RADDRESS.TYPE.equal((byte) 0))
								.limit(1)
								.fetch()
								.stream()
								.forEach(
									province -> {
										try {detail.setProvince(Integer.parseInt(province.getValue(GEOZONE.CODE)));
										} catch (NumberFormatException e) {} // nothing. If not a number,  not a valid province.
									}
								);
							if (key == Mod1902016Key.A) {
								Integer birthData = rec.getValue(birthYear);
								detail.setBirthYear(birthData==null?0:birthData);
								fillLastIrpfDataByPerson(ctx,rec.getValue(PERSON.REGISTRY),firstDay, lastDay, detail);
							}
						}
						return map.get(mapKey);
					}
					
				}); 
		});
		mod190.getDetails().addAll(map.values());
		fixRoundProblems( ctx, mod190); 
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
		if (mod190 == null) return null; 
		if (mod190.getDetails() == null || mod190.getDetails().isEmpty()) return null;		
		java.sql.Date firstDay = AonDateUtils.toSql(AonDateUtils.getYearFirstDay(mod190.getYear()));
		java.sql.Date lastDay = AonDateUtils.toSql(AonDateUtils.getYearLastDay(mod190.getYear()));
		LinkedHashMap<String,Mod190Detail> map = getUniqueMap( mod190 );	
		for (Mod190Detail detail : map.values()) {
			ctx.getDslContext().select(SALARY.IRPF_BASE,SALARY.TOTAL_IRPF)
				.from(SALARY)
				.join(CONTRACT).on(SALARY.CONTRACT.equal(CONTRACT.ID))
				.join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
				.where(SALARY.ISSUE_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
				.and(SALARY.EMPLOYEE_DOCUMENT.eq(detail.getDocument()))
				.and(WORKPLACE.ENTERPRISE.equal(mod190.getEnterprise()))
				.and(WORKPLACE.ECONOMICAGREEMENT.equal(mod190.getAdministration().value()))
				.and(SALARY.TYPE.in(SalaryType.IRPF_SALARIES )) // Skip SLD ( L00, L13... )
				.orderBy(SALARY.EMPLOYEE_DOCUMENT)
				.fetch()
				.stream()
				.forEach(rec -> {
					double base = AonMathUtils.round(rec.getValue(SALARY.IRPF_BASE));
					double quota = AonMathUtils.round(rec.getValue(SALARY.TOTAL_IRPF));
					detail.setSalaryPerception(AonMathUtils.round(detail.getSalaryPerception() + base));				
					detail.setSalaryRetention(AonMathUtils.round(detail.getSalaryRetention() + quota));
				});
		}
		LinkedList<Mod190Detail> retList = new LinkedList<>();
		for (Mod190Detail detail : map.values()) {
			double per = AonMathUtils.round(detail.getPerception() + detail.getPerceptionIL() + detail.getInKindPerception() + detail.getInKindPerceptionIL());
			double ret = AonMathUtils.round(detail.getRetention() + detail.getInKindDeposit() + detail.getRetentionIL() + detail.getInKindDepositIL());
			if ( AonNumberUtils.notEquals(detail.getSalaryPerception(), per) || AonNumberUtils.notEquals(detail.getSalaryRetention(), ret)) {
				retList.add(detail);
			}
		}
		return retList;
	}
	
	private static LinkedHashMap<String, Mod190Detail> getUniqueMap(Mod190 mod190) {
		LinkedHashMap<String,Mod190Detail> map = new LinkedHashMap<>();	
		for (Mod190Detail detail : mod190.getDetails()) {
			double ret = AonMathUtils.round(detail.getRetention() + detail.getInKindDeposit() + detail.getRetentionIL() + detail.getInKindDepositIL());
			if (!AonStringUtils.equals("G", detail.getKey()) &&
				!AonStringUtils.equals("H", detail.getKey()) &&
				AonMathUtils.isNotZero(ret)) {
				
				Mod190Detail det = null;
				if (!map.containsKey(detail.getDocument())) {
					det = new Mod190Detail();
					det.setDocument(detail.getDocument());
					det.setName(detail.getName());
					map.put(detail.getDocument(), det);
				} else {
					det = map.get(detail.getDocument());
				}
				det.setPerception( AonMathUtils.round(det.getPerception() + detail.getPerception()));
				det.setPerceptionIL( AonMathUtils.round(det.getPerceptionIL() + detail.getPerceptionIL()));
				det.setInKindPerception( AonMathUtils.round(det.getInKindPerception() + detail.getInKindPerception())); 
				det.setInKindPerceptionIL( AonMathUtils.round(det.getInKindPerceptionIL() + detail.getInKindPerceptionIL()));
				det.setRetention( AonMathUtils.round(det.getRetention() + detail.getRetention()));
				det.setRetentionIL( AonMathUtils.round(det.getRetentionIL() + detail.getRetentionIL()));
				det.setInKindDeposit( AonMathUtils.round(det.getInKindDeposit() + detail.getInKindDeposit())); 
				det.setInKindDepositIL( AonMathUtils.round(det.getInKindDepositIL() + detail.getInKindDepositIL()));
			}
		}
		return map;
	}

	private static void fixRoundProblems(AONContext ctx, Mod190 mod190) {
		if (mod190.getDetails() == null || mod190.getDetails().isEmpty()) return;		
		java.sql.Date firstDay = AonDateUtils.toSql(AonDateUtils.getYearFirstDay(mod190.getYear()));
		java.sql.Date lastDay = AonDateUtils.toSql(AonDateUtils.getYearLastDay(mod190.getYear()));
		LinkedHashMap<String,Mod190Detail> map = getUniqueMap( mod190 );	
		for (Mod190Detail detail : map.values()) {
			ctx.getDslContext().select(SALARY.IRPF_BASE,SALARY.TOTAL_IRPF)
				.from(SALARY)
				.join(CONTRACT).on(SALARY.CONTRACT.equal(CONTRACT.ID))
				.join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
				.where(SALARY.ISSUE_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
				.and(SALARY.EMPLOYEE_DOCUMENT.eq(detail.getDocument()))
				.and(WORKPLACE.ENTERPRISE.equal(mod190.getEnterprise()))
				.and(WORKPLACE.ECONOMICAGREEMENT.equal(mod190.getAdministration().value()))
				.and(SALARY.TYPE.in(SalaryType.IRPF_SALARIES )) // Skip SLD ( L00, L13... )
				.orderBy(SALARY.EMPLOYEE_DOCUMENT)
				.fetch()
				.stream()
				.forEach(rec -> {
					double base = AonMathUtils.round(rec.getValue(SALARY.IRPF_BASE));
					double quota = AonMathUtils.round(rec.getValue(SALARY.TOTAL_IRPF));
					detail.setSalaryPerception(AonMathUtils.round(detail.getSalaryPerception() + base));				
					detail.setSalaryRetention(AonMathUtils.round(detail.getSalaryRetention() + quota));
				});
		}
		LinkedList<Mod190Detail> retList = new LinkedList<>();
		for (Mod190Detail detail : map.values()) {
			double per = AonMathUtils.round(detail.getPerception() + detail.getPerceptionIL() + detail.getInKindPerception() + detail.getInKindPerceptionIL());
			double ret = AonMathUtils.round(detail.getRetention() + detail.getInKindDeposit() + detail.getRetentionIL() + detail.getInKindDepositIL());
			double perDiff = AonMathUtils.round(detail.getSalaryPerception() - per);
			double retDiff = AonMathUtils.round(detail.getSalaryRetention() - ret);
			if ( (AonMathUtils.isNotZero(perDiff) && AonMathUtils.absRounded(perDiff) <=0.5)
 			  || (AonMathUtils.isNotZero(retDiff) && AonMathUtils.absRounded(retDiff) <=0.5)) {
				LinkedList<Mod190Detail> selected = new LinkedList<>();
				for (Mod190Detail det : mod190.getDetails() ) {
					double tmpRet = AonMathUtils.round(det.getRetention() + det.getInKindDeposit() + det.getRetentionIL() + det.getInKindDepositIL());
					if ( AonStringUtils.equals (detail.getDocument(),det.getDocument()) && AonMathUtils.isNotZero(tmpRet))  {
						selected.add(det);		
					}
				}
				if ( selected.size() == 1) {
					if (AonMathUtils.isNotZero(per)) {
						selected.get(0).setPerception(AonMathUtils.round( selected.get(0).getPerception() + getPercetionDiff(detail)));
					}
					if (AonMathUtils.isNotZero(ret)) {
						selected.get(0).setRetention(AonMathUtils.round( selected.get(0).getRetention() + getRetentionDiff(detail)));
					}
				}
				retList.add(detail);
			}
		}
	}

	private static double getPercetionDiff(Mod190Detail detail) {
		return AonMathUtils.round(detail.getSalaryPerception() 
				- detail.getPerception()
				- detail.getPerceptionIL() 
				- detail.getInKindPerception() 
				- detail.getInKindPerceptionIL());
	}
	private static double getRetentionDiff(Mod190Detail detail) {
		return AonMathUtils.round(detail.getSalaryRetention() 
				- detail.getRetention()
				- detail.getRetentionIL() 
				- detail.getInKindDeposit() 
				- detail.getInKindDepositIL());			
	}
	
}
