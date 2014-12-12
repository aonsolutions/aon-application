package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel190.FS_MODEL190;
import static com.esferalia.aon.jooq.tables.FsModel190Detail.FS_MODEL190_DETAIL;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.IrpfData.IRPF_DATA;
import static com.esferalia.aon.jooq.tables.IrpfResult.IRPF_RESULT;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import org.jooq.BatchBindStep;
import org.jooq.Field;
import org.jooq.InsertSetMoreStep;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.FsModel190DetailRecord;
import com.esferalia.aon.jooq.tables.records.FsModel190Record;
import com.esferalia.aon.jooq.tables.records.IrpfDataRecord;
import com.esferalia.aon.jooq.tables.records.IrpfResultRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.IrpfData;
import com.esferalia.aon.occam.api.model.IrpfResult;
import com.esferalia.aon.occam.api.model.Mod190;
import com.esferalia.aon.occam.api.model.Mod190Detail;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod190DAO {
	private static final IrpfData EMPTY_IRPF_DATA = new IrpfData();
	private static final IrpfResult EMPTY_IRPF_RESULT = new IrpfResult();

	public static Mod190 save(AONContext ctx, Mod190 mod190) {
		ctx.checkWrite();
		if (mod190.getId() == null) {
			mod190 = insert(ctx, mod190);
			insertDetails(ctx, mod190);
		} else {
			mod190 = update(ctx, mod190);
			for (Mod190Detail detail : mod190.getDetails() ) {
				saveDetail(ctx, mod190, detail);
			}
		}
		return getById(ctx, mod190.getId());
	}

	private static Mod190 insert(AONContext ctx, Mod190 mod190) {
		validate(ctx, mod190);
		FsModel190Record record = ctx
				.getDslContext()
				.insertInto(FS_MODEL190)
				.set(FS_MODEL190.DOMAIN, mod190.getDomain())
				.set(FS_MODEL190.ENTERPRISE, mod190.getEnterprise())
				.set(FS_MODEL190.YEAR, mod190.getYear())
				.set(FS_MODEL190.ADMINISTRATION, mod190.getAdministration())
				.set(FS_MODEL190.STATUS, (byte) 0)
				.set(FS_MODEL190.SECURITY_LEVEL,
						AonEnumUtils.getByte(mod190.isConfidential()))
				.set(FS_MODEL190.DOCUMENT, mod190.getDocument())
				.set(FS_MODEL190.NAME, mod190.getName())
				.set(FS_MODEL190.CONTACT_PERSON, mod190.getContactPerson())
				.set(FS_MODEL190.CONTACT_PHONE, mod190.getContactPhone())
				.set(FS_MODEL190.COMPLEMENTARY, (byte) 0)
				.set(FS_MODEL190.REPLACEMENT,
						AonEnumUtils.getByte(mod190.isReplacement()))
				.set(FS_MODEL190.COMMENTS, mod190.getComments())
				.set(FS_MODEL190.RECEIPT, mod190.getReceipt())
				.set(FS_MODEL190.REPLACED_RECEIPT, mod190.getReplacedReceipt())
				.set(FS_MODEL190.RECEIVER_COUNT_TOTAL,
						mod190.getReceiverCountTotal())
				.set(FS_MODEL190.RECEIPT_TOTAL, mod190.getReceiptTotal())
				.set(FS_MODEL190.RETENTION_TOTAL, mod190.getRetentionTotal())
				.returning(FS_MODEL190.ID).fetchOne();
		mod190.setId(record.getId());
		insertDetailsFromInvoice(ctx, mod190);
		insertDetailsFromSalary(ctx, mod190);
		return mod190;
	}

	private static Mod190 update(AONContext ctx, Mod190 mod190) {
		ctx.getDslContext()
				.update(FS_MODEL190)
				.set(FS_MODEL190.YEAR, mod190.getYear())
				.set(FS_MODEL190.ADMINISTRATION, mod190.getAdministration())
				.set(FS_MODEL190.STATUS, (byte) 0)
				.set(FS_MODEL190.SECURITY_LEVEL,
						AonEnumUtils.getByte(mod190.isConfidential()))
				.set(FS_MODEL190.DOCUMENT, mod190.getDocument())
				.set(FS_MODEL190.NAME, mod190.getName())
				.set(FS_MODEL190.CONTACT_PERSON, mod190.getContactPerson())
				.set(FS_MODEL190.CONTACT_PHONE, mod190.getContactPhone())
				.set(FS_MODEL190.COMPLEMENTARY, (byte) 0)
				.set(FS_MODEL190.REPLACEMENT,
						AonEnumUtils.getByte(mod190.isReplacement()))
				.set(FS_MODEL190.COMMENTS, mod190.getComments())
				.set(FS_MODEL190.RECEIPT, mod190.getReceipt())
				.set(FS_MODEL190.REPLACED_RECEIPT, mod190.getReplacedReceipt())
				.set(FS_MODEL190.RECEIVER_COUNT_TOTAL,
						mod190.getReceiverCountTotal())
				.set(FS_MODEL190.RECEIPT_TOTAL, mod190.getReceiptTotal())
				.set(FS_MODEL190.RETENTION_TOTAL, mod190.getRetentionTotal())
				.where(FS_MODEL190.ID.equal(mod190.getId())).execute();
		return mod190;
	}
	
	private static void insertDetails(AONContext ctx, Mod190 mod190) {
		BatchBindStep batch = ctx.getDslContext().batch(
				ctx.getDslContext()
					.insertInto(FS_MODEL190_DETAIL,
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
								FS_MODEL190_DETAIL.HOME_LOAN_COMMUNNICATION
					)
					.values( null,null,null,null,null,null,null,null,null,null
							,null,null,null,null,null,null,null,null,null,null
							,null,null,null,null,null,null,null,null,null,null
							,null,null,null,null,null,null,null,null,null,null
							,null,null,null,null,null,null,null,null,null,null)
										);
		IrpfData irpfData = null;
		IrpfResult irpfResult = null;
		for (Mod190Detail detail : mod190.getDetails()) {
			irpfData = detail.getIrpfData() == null ? EMPTY_IRPF_DATA
					: detail.getIrpfData();
			irpfResult = detail.getIrpfResult() == null ? EMPTY_IRPF_RESULT
					: detail.getIrpfResult();
			batch.bind(
					detail.getDomain()
					,detail.getMod190()
					,AonStringUtils.substring(detail.getDocument(), 0, 9)
					,AonStringUtils.substring(detail.getName(), 0, 40)
					,AonStringUtils.substring(detail.getRepresentativeDocument(), 0, 9)
					,detail.getProvince()
					,detail.getKey()
					,detail.getSubKey()
					,detail.getPerception()
					,detail.getRetention()
					,detail.getInKindPerception()
					,detail.getInKindDeposit()
					,detail.getInKindOutputDeposit()
					, detail.getAccrualYear()
					, AonEnumUtils.getByte(irpfData.isCeutaMelilla())
					, irpfData.getBirthYear()
					,irpfData.getFamilySituation()
					,irpfData.getSpouseDocument()
					,irpfData.getDisability()
					,irpfData.getContract()
					,AonEnumUtils.getByte(irpfData.isWorkActivityExtension())
					,AonEnumUtils.getByte(irpfData.isGeographicMobility())
					,irpfResult.getApplicableReduction()
					,irpfResult.getDeducibleExpense()
					,irpfResult.getCompensatoryPension()
					,irpfResult.getFoodAnnuality()
					,irpfResult.getLessThan3Descendent()
					,irpfResult.getLessThan3DescendentRatio()
					,irpfResult.getOtherDescendent()
					,irpfResult.getOtherDescendentRatio()
					,irpfResult.getDisabilityDescendent33()
					,irpfResult.getDisabilityDescendent33Ratio()
					,irpfResult.getDisabilityDescendentDependence()
					,irpfResult.getDisabilityDescendentDependenceRatio()
					,irpfResult.getDisabilityDescendent65()
					,irpfResult.getDisabilityDescendent65Ratio()
					,irpfResult.getLessThan75Ascendant()
					,irpfResult.getLessThan75AscendantRatio()
					,irpfResult.getAscendant()
					,irpfResult.getAscendantRatio()
					,irpfResult.getDisabilityAscendant33()
					,irpfResult.getDisabilityAscendant33Ratio()
					,irpfResult.getDisabilityAscendantDependence()
					,irpfResult.getDisabilityAscendantDependenceRatio()
					,irpfResult.getDisabilityAscendant65()
					,irpfResult.getDisabilityAscendant65Ratio()
					,irpfResult.getFirstChildCalculation()
					,irpfResult.getSecondChildCalculation()
					,irpfResult.getThirdChildCalculation()
					,AonEnumUtils.getByte(irpfResult.isHomeLoanCommunnication())
					);
		}
		batch.execute();		
	}

	
	public static void saveDetail(AONContext ctx, Mod190 mod190,
			Mod190Detail detail) {
		ctx.checkWrite();
		if (detail.getId() == null || detail.getId() < 0) {
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

	private static InsertSetMoreStep<FsModel190DetailRecord>
		getinsertDetailStatement(AONContext ctx, Mod190Detail detail) {

		IrpfData irpfData = detail.getIrpfData() == null ? EMPTY_IRPF_DATA
			: detail.getIrpfData();
		IrpfResult irpfResult = detail.getIrpfResult() == null ? EMPTY_IRPF_RESULT
			: detail.getIrpfResult();

		return ctx.getDslContext()
			.insertInto(FS_MODEL190_DETAIL)
			.set(FS_MODEL190_DETAIL.DOMAIN, detail.getDomain())
			.set(FS_MODEL190_DETAIL.FS_MODEL190, detail.getMod190())
			.set(FS_MODEL190_DETAIL.DOCUMENT,
					AonStringUtils.substring(detail.getDocument(), 0, 9))
			.set(FS_MODEL190_DETAIL.NAME,
					AonStringUtils.substring(detail.getName(), 0, 40))
			.set(FS_MODEL190_DETAIL.REPRESENTATIVE_DOCUMENT,
					AonStringUtils.substring(
							detail.getRepresentativeDocument(), 0, 9))
			.set(FS_MODEL190_DETAIL.PROVINCE, detail.getProvince())
			.set(FS_MODEL190_DETAIL.KEY, detail.getKey())
			.set(FS_MODEL190_DETAIL.SUBKEY, detail.getSubKey())
			.set(FS_MODEL190_DETAIL.PERCEPTION, detail.getPerception())
			.set(FS_MODEL190_DETAIL.RETENTION, detail.getRetention())
			.set(FS_MODEL190_DETAIL.IN_KIND_PERCEPTION,
					detail.getInKindPerception())
			.set(FS_MODEL190_DETAIL.IN_KIND_DEPOSIT,
					detail.getInKindDeposit())
			.set(FS_MODEL190_DETAIL.IN_KIND_OUTPUT_DEPOSIT,
					detail.getInKindOutputDeposit())
			.set(FS_MODEL190_DETAIL.ACCRUAL_YEAR, detail.getAccrualYear())
			.set(FS_MODEL190_DETAIL.CEUTA_MELILLA,
					AonEnumUtils.getByte(irpfData.isCeutaMelilla()))
			.set(FS_MODEL190_DETAIL.BIRTH_YEAR, irpfData.getBirthYear())
			.set(FS_MODEL190_DETAIL.FAMILY_SITUATION,
					irpfData.getFamilySituation())
			.set(FS_MODEL190_DETAIL.SPOUSE_DOCUMENT,
					irpfData.getSpouseDocument())
			.set(FS_MODEL190_DETAIL.DISABILITY, irpfData.getDisability())
			.set(FS_MODEL190_DETAIL.CONTRACT, irpfData.getContract())
			.set(FS_MODEL190_DETAIL.LABOUR_PROLONGATION,
					AonEnumUtils.getByte(irpfData.isWorkActivityExtension()))
			.set(FS_MODEL190_DETAIL.GEOGRAPHIC_MOBILITY,
					AonEnumUtils.getByte(irpfData.isGeographicMobility()))
			.set(FS_MODEL190_DETAIL.APPLICABLE_REDUCTION,
					irpfResult.getApplicableReduction())
			.set(FS_MODEL190_DETAIL.DEDUCIBLE_EXPENSES,
					irpfResult.getDeducibleExpense())
			.set(FS_MODEL190_DETAIL.SPOUSAL_SUPPORT,
					irpfResult.getCompensatoryPension())
			.set(FS_MODEL190_DETAIL.FOOD_ANNUITY,
					irpfResult.getFoodAnnuality())
			.set(FS_MODEL190_DETAIL.LESS_THAN_3_DESCENDENT,
					irpfResult.getLessThan3Descendent())
			.set(FS_MODEL190_DETAIL.LESS_THAN_3_DESCENDENT_RATIO,
					irpfResult.getLessThan3DescendentRatio())
			.set(FS_MODEL190_DETAIL.OTHER_DESCENDENT,
					irpfResult.getOtherDescendent())
			.set(FS_MODEL190_DETAIL.OTHER_DESCENDENT_RATIO,
					irpfResult.getOtherDescendentRatio())
			.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_33,
					irpfResult.getDisabilityDescendent33())
			.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_33_RATIO,
					irpfResult.getDisabilityDescendent33Ratio())
			.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_DEPENDENCE,
					irpfResult.getDisabilityDescendentDependence())
			.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_DEPENDENCE_RATIO,
					irpfResult.getDisabilityDescendentDependenceRatio())
			.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_65,
					irpfResult.getDisabilityDescendent65())
			.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_65_RATIO,
					irpfResult.getDisabilityDescendent65Ratio())
			.set(FS_MODEL190_DETAIL.LESS_THAN_75_ASCENDANT,
					irpfResult.getLessThan75Ascendant())
			.set(FS_MODEL190_DETAIL.LESS_THAN_75_ASCENDANT_RATIO,
					irpfResult.getLessThan75AscendantRatio())
			.set(FS_MODEL190_DETAIL.ASCENDANT, irpfResult.getAscendant())
			.set(FS_MODEL190_DETAIL.ASCENDANT_RATIO,
					irpfResult.getAscendantRatio())
			.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_33,
					irpfResult.getDisabilityAscendant33())
			.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_33_RATIO,
					irpfResult.getDisabilityAscendant33Ratio())
			.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_DEPENDENCE,
					irpfResult.getDisabilityAscendantDependence())
			.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_DEPENDENCE_RATIO,
					irpfResult.getDisabilityAscendantDependenceRatio())
			.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_65,
					irpfResult.getDisabilityAscendant65())
			.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_65_RATIO,
					irpfResult.getDisabilityAscendant65Ratio())
			.set(FS_MODEL190_DETAIL.FIRST_CHILD_CALCULATION,
					irpfResult.getFirstChildCalculation())
			.set(FS_MODEL190_DETAIL.SECOND_CHILD_CALCULATION,
					irpfResult.getSecondChildCalculation())
			.set(FS_MODEL190_DETAIL.THIRD_CHILD_CALCULATION,
					irpfResult.getThirdChildCalculation())
			.set(FS_MODEL190_DETAIL.HOME_LOAN_COMMUNNICATION,
					AonEnumUtils.getByte(irpfResult
							.isHomeLoanCommunnication()));
	}
	
	private static void insertDetail(AONContext ctx, Mod190Detail detail) {
		getinsertDetailStatement(ctx,detail).execute();
	}

	private static void updateDetail(AONContext ctx, Mod190Detail detail) {
		IrpfData irpfData = detail.getIrpfData() == null ? EMPTY_IRPF_DATA
				: detail.getIrpfData();
		IrpfResult irpfResult = detail.getIrpfResult() == null ? EMPTY_IRPF_RESULT
				: detail.getIrpfResult();
		ctx.getDslContext()
				.update(FS_MODEL190_DETAIL)
				.set(FS_MODEL190_DETAIL.DOCUMENT,
						AonStringUtils.substring(detail.getDocument(), 0, 9))
				.set(FS_MODEL190_DETAIL.NAME,
						AonStringUtils.substring(detail.getName(), 0, 40))
				.set(FS_MODEL190_DETAIL.REPRESENTATIVE_DOCUMENT,
						AonStringUtils.substring(
								detail.getRepresentativeDocument(), 0, 9))
				.set(FS_MODEL190_DETAIL.PROVINCE, detail.getProvince())
				.set(FS_MODEL190_DETAIL.KEY, detail.getKey())
				.set(FS_MODEL190_DETAIL.SUBKEY, detail.getSubKey())
				.set(FS_MODEL190_DETAIL.PERCEPTION, detail.getPerception())
				.set(FS_MODEL190_DETAIL.RETENTION, detail.getRetention())
				.set(FS_MODEL190_DETAIL.IN_KIND_PERCEPTION,
						detail.getInKindPerception())
				.set(FS_MODEL190_DETAIL.IN_KIND_DEPOSIT,
						detail.getInKindDeposit())
				.set(FS_MODEL190_DETAIL.IN_KIND_OUTPUT_DEPOSIT,
						detail.getInKindOutputDeposit())
				.set(FS_MODEL190_DETAIL.ACCRUAL_YEAR, detail.getAccrualYear())
				.set(FS_MODEL190_DETAIL.CEUTA_MELILLA,
						AonEnumUtils.getByte(irpfData.isCeutaMelilla()))
				.set(FS_MODEL190_DETAIL.BIRTH_YEAR, irpfData.getBirthYear())
				.set(FS_MODEL190_DETAIL.FAMILY_SITUATION,
						irpfData.getFamilySituation())
				.set(FS_MODEL190_DETAIL.SPOUSE_DOCUMENT,
						irpfData.getSpouseDocument())
				.set(FS_MODEL190_DETAIL.DISABILITY, irpfData.getDisability())
				.set(FS_MODEL190_DETAIL.CONTRACT, irpfData.getContract())
				.set(FS_MODEL190_DETAIL.LABOUR_PROLONGATION,
						AonEnumUtils.getByte(irpfData.isWorkActivityExtension()))
				.set(FS_MODEL190_DETAIL.GEOGRAPHIC_MOBILITY,
						AonEnumUtils.getByte(irpfData.isGeographicMobility()))
				.set(FS_MODEL190_DETAIL.APPLICABLE_REDUCTION,
						irpfResult.getApplicableReduction())
				.set(FS_MODEL190_DETAIL.DEDUCIBLE_EXPENSES,
						irpfResult.getDeducibleExpense())
				.set(FS_MODEL190_DETAIL.SPOUSAL_SUPPORT,
						irpfResult.getCompensatoryPension())
				.set(FS_MODEL190_DETAIL.FOOD_ANNUITY,
						irpfResult.getFoodAnnuality())
				.set(FS_MODEL190_DETAIL.LESS_THAN_3_DESCENDENT,
						irpfResult.getLessThan3Descendent())
				.set(FS_MODEL190_DETAIL.LESS_THAN_3_DESCENDENT_RATIO,
						irpfResult.getLessThan3DescendentRatio())
				.set(FS_MODEL190_DETAIL.OTHER_DESCENDENT,
						irpfResult.getOtherDescendent())
				.set(FS_MODEL190_DETAIL.OTHER_DESCENDENT_RATIO,
						irpfResult.getOtherDescendentRatio())
				.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_33,
						irpfResult.getDisabilityDescendent33())
				.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_33_RATIO,
						irpfResult.getDisabilityDescendent33Ratio())
				.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_DEPENDENCE,
						irpfResult.getDisabilityDescendentDependence())
				.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_DEPENDENCE_RATIO,
						irpfResult.getDisabilityDescendentDependenceRatio())
				.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_65,
						irpfResult.getDisabilityDescendent65())
				.set(FS_MODEL190_DETAIL.DISABILITY_DESCENDENT_65_RATIO,
						irpfResult.getDisabilityDescendent65Ratio())
				.set(FS_MODEL190_DETAIL.LESS_THAN_75_ASCENDANT,
						irpfResult.getLessThan75Ascendant())
				.set(FS_MODEL190_DETAIL.LESS_THAN_75_ASCENDANT_RATIO,
						irpfResult.getLessThan75AscendantRatio())
				.set(FS_MODEL190_DETAIL.ASCENDANT, irpfResult.getAscendant())
				.set(FS_MODEL190_DETAIL.ASCENDANT_RATIO,
						irpfResult.getAscendantRatio())
				.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_33,
						irpfResult.getDisabilityAscendant33())
				.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_33_RATIO,
						irpfResult.getDisabilityAscendant33Ratio())
				.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_DEPENDENCE,
						irpfResult.getDisabilityAscendantDependence())
				.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_DEPENDENCE_RATIO,
						irpfResult.getDisabilityAscendantDependenceRatio())
				.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_65,
						irpfResult.getDisabilityAscendant65())
				.set(FS_MODEL190_DETAIL.DISABILITY_ASCENDANT_65_RATIO,
						irpfResult.getDisabilityAscendant65Ratio())
				.set(FS_MODEL190_DETAIL.FIRST_CHILD_CALCULATION,
						irpfResult.getFirstChildCalculation())
				.set(FS_MODEL190_DETAIL.SECOND_CHILD_CALCULATION,
						irpfResult.getSecondChildCalculation())
				.set(FS_MODEL190_DETAIL.THIRD_CHILD_CALCULATION,
						irpfResult.getThirdChildCalculation())
				.set(FS_MODEL190_DETAIL.HOME_LOAN_COMMUNNICATION,
						AonEnumUtils.getByte(irpfResult
								.isHomeLoanCommunnication()))
				.where(FS_MODEL190_DETAIL.ID.equal(detail.getId())).execute();
	}

	private static void validate(AONContext ctx, Mod190 mod190) {
		if (mod190.isReplacement()) {
			// Se comprueba que exista la declaración ssustituida.
			if (ctx.getDslContext().selectOne()
					.from(FS_MODEL190)
					.where(FS_MODEL190.YEAR.equal(mod190.getYear())
					.and(FS_MODEL190.ENTERPRISE.equal(mod190.getEnterprise()))
					.and(FS_MODEL190.RECEIPT.equal(mod190.getReplacedReceipt()))).fetchCount() == 0) 
				throw new AonCoreException(
						AonError.FISCAL_NO_REPLACED_DECLARATION.getMessage());

			// Se comprueba que no exista una declaraci?n sustitutiva.
			if (ctx.getDslContext().selectOne()
					.from(FS_MODEL190)
					.where(FS_MODEL190.YEAR.equal(mod190.getYear())
					.and(FS_MODEL190.ENTERPRISE.equal(mod190.getEnterprise()))
					.and(FS_MODEL190.REPLACEMENT.equal((byte) 1))					
					.and(FS_MODEL190.REPLACED_RECEIPT.equal(mod190.getReplacedReceipt()))).fetchCount() > 0 )
				throw new AonCoreException(AonError.FISCAL_DECLARATION_ALREADY_REPLACED.getMessage());
		} else {
			// Se comprueba que no exista ya una declaraci?n.
			if (ctx.getDslContext().selectOne()
					.from(FS_MODEL190)
					.where(FS_MODEL190.YEAR.equal(mod190.getYear())
					.and(FS_MODEL190.ENTERPRISE.equal(mod190.getEnterprise()))
					.and(FS_MODEL190.REPLACEMENT.equal((byte) 0))).fetchCount() > 0 ) 
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

	public static ArrayList<Mod190> getByDomain(AONContext ctx, int domain) {
		ctx.checkRead();
		ArrayList<Mod190> list = new ArrayList<Mod190>();
		ctx.getDslContext()
				.select(FS_MODEL190.fields())
				.from(FS_MODEL190)
				.join(DOMAIN)
				.on(FS_MODEL190.DOMAIN.equal(DOMAIN.ID))
				.where(FS_MODEL190.DOMAIN.equal(domain).or(
						DOMAIN.PARENT.equal(domain)))
				.orderBy(FS_MODEL190.YEAR.desc(), FS_MODEL190.NAME.asc(),
						FS_MODEL190.REPLACEMENT.asc())
				.fetchInto(FsModel190Record.class)
				.stream()
				.forEach(
						record -> {
							Mod190 mod190 = new Mod190();
							populate(record, mod190);
							ArrayList<Mod190Detail> details = getDetails(ctx,
									mod190.getId());
							mod190.setDetails(details);
							list.add(mod190);
						});
		return list;
	}

	public static Mod190 getById(AONContext ctx, int id) {
		ctx.checkRead();
		Mod190 mod190 = new Mod190();
		ctx.getDslContext()
				.selectFrom(FS_MODEL190)
				.where(FS_MODEL190.ID.equal(id))
				.fetch()
				.stream()
				.forEach(
						record -> {
							populate(record, mod190);
							ArrayList<Mod190Detail> details = getDetails(ctx,
									mod190.getId());
							mod190.setDetails(details);
						});
		if (mod190.getId() != null) {
			return mod190;
		}
		return null;
	}

	private static void populate(FsModel190Record record, Mod190 mod190) {
		mod190.setId(record.getValue(FS_MODEL190.ID));
		mod190.setDomain(record.getValue(FS_MODEL190.DOMAIN));
		mod190.setEnterprise(record.getValue(FS_MODEL190.ENTERPRISE));
		mod190.setYear(record.getValue(FS_MODEL190.YEAR));
		mod190.setAdministration(record.getValue(FS_MODEL190.ADMINISTRATION));
		mod190.setReplacement(record.getValue(FS_MODEL190.REPLACEMENT) == 1);
		mod190.setDocument(record.getValue(FS_MODEL190.DOCUMENT));
		mod190.setName(record.getValue(FS_MODEL190.NAME));
		mod190.setContactPerson(record.getValue(FS_MODEL190.CONTACT_PERSON));
		mod190.setContactPhone(record.getValue(FS_MODEL190.CONTACT_PHONE));
		mod190.setReceipt(record.getValue(FS_MODEL190.RECEIPT));
		mod190.setReplacedReceipt(record.getValue(FS_MODEL190.REPLACED_RECEIPT));
		mod190.setReceiverCountTotal(record
				.getValue(FS_MODEL190.RECEIVER_COUNT_TOTAL));
		mod190.setReceiptTotal(record.getValue(FS_MODEL190.RECEIPT_TOTAL));
		mod190.setRetentionTotal(record.getValue(FS_MODEL190.RETENTION_TOTAL));
		mod190.setComments(record.getValue(FS_MODEL190.COMMENTS));
	}

	public static Mod190Detail getDetail(AONContext ctx, int id) {
		FsModel190DetailRecord record = ctx.getDslContext()
				.selectFrom(FS_MODEL190_DETAIL)
				.where(FS_MODEL190_DETAIL.ID.equal(id)).fetchOne();
		ctx.checkRead();
		Mod190Detail detail = null;
		if (record != null) {
			detail = new Mod190Detail();
			populateDetail(record, detail);
		}
		return detail;
	}

	public static ArrayList<Mod190Detail> getDetails(AONContext ctx, int mod190) {
		Result<FsModel190DetailRecord> records = ctx.getDslContext()
				.selectFrom(FS_MODEL190_DETAIL)
				.where(FS_MODEL190_DETAIL.FS_MODEL190.equal(mod190)).fetch();
		ctx.checkRead();
		ArrayList<Mod190Detail> list = new ArrayList<>();
		Mod190Detail detail = null;
		for (FsModel190DetailRecord record : records) {
			detail = new Mod190Detail();
			populateDetail(record, detail);
			list.add(detail);
		}
		return list;
	}

	private static void populateDetail(FsModel190DetailRecord record, Mod190Detail detail) {
		detail.setId(record.getId());
		detail.setDocument(record.getDocument());
		detail.setName(record.getName());
		detail.setRepresentativeDocument(record.getRepresentativeDocument());
		detail.setProvince(record.getProvince());
		detail.setKey(record.getKey());
		detail.setSubKey(record.getSubkey());
		detail.setPerception(record.getPerception());
		detail.setInKindPerception(record.getInKindPerception());
		detail.setInKindDeposit(record.getInKindDeposit());
		detail.setInKindOutputDeposit(record.getInKindOutputDeposit());
		detail.setAccrualYear(record.getAccrualYear());
		detail.setRetention(record.getRetention());
		IrpfData irpfData = new IrpfData();
		irpfData.setBirthYear(record.getBirthYear());
		irpfData.setCeutaMelilla(AonEnumUtils.getBoolean(record.getCeutaMelilla()));
		irpfData.setFamilySituation(record.getFamilySituation());
		irpfData.setSpouseDocument(record.getSpouseDocument());
		irpfData.setDisability(record.getDisability());
		irpfData.setContract(record.getContract());
		irpfData.setWorkActivityExtension(AonEnumUtils.getBoolean(record.getLabourProlongation()));
		irpfData.setGeographicMobility(AonEnumUtils.getBoolean(record.getGeographicMobility()));
		detail.setIrpfData(irpfData);
		IrpfResult irpfResult = new IrpfResult();
		irpfResult.setApplicableReduction(record.getApplicableReduction());
		irpfResult.setDeducibleExpense(record.getDeducibleExpenses());
		irpfResult.setCompensatoryPension(record.getSpousalSupport());
		irpfResult.setFoodAnnuality(record.getFoodAnnuity());
		irpfResult.setHomeLoanCommunnication(AonEnumUtils.getBoolean(record.getHomeLoanCommunnication()));
		irpfResult.setLessThan3Descendent(record.getLessThan_3Descendent());
		irpfResult.setLessThan3DescendentRatio(record.getLessThan_3DescendentRatio());
		irpfResult.setOtherDescendent(record.getOtherDescendent());
		irpfResult.setOtherDescendentRatio(record.getOtherDescendentRatio());
		irpfResult.setDisabilityDescendent33(record.getDisabilityDescendent_33());
		irpfResult.setDisabilityDescendent33Ratio(record.getDisabilityDescendent_33Ratio());
		irpfResult.setDisabilityDescendentDependence(record.getDisabilityDescendentDependence());
		irpfResult.setDisabilityDescendentDependenceRatio(record.getDisabilityDescendentDependenceRatio());
		irpfResult.setDisabilityDescendent65(record.getDisabilityDescendent_65());
		irpfResult.setDisabilityDescendent65Ratio(record.getDisabilityDescendent_65Ratio());
		irpfResult.setLessThan75Ascendant(record.getLessThan_75Ascendant());
		irpfResult.setLessThan75AscendantRatio(record.getLessThan_75AscendantRatio());
		irpfResult.setAscendant(record.getAscendant());
		irpfResult.setAscendantRatio(record.getAscendantRatio());
		irpfResult.setDisabilityAscendant33(record.getDisabilityAscendant_33());
		irpfResult.setDisabilityAscendant33Ratio(record.getDisabilityAscendant_33Ratio());
		irpfResult.setDisabilityAscendantDependence(record.getDisabilityAscendantDependence());
		irpfResult.setDisabilityAscendantDependenceRatio(record.getDisabilityAscendantDependenceRatio());
		irpfResult.setDisabilityAscendant65(record.getDisabilityAscendant_65());
		irpfResult.setDisabilityAscendant65Ratio(record.getDisabilityAscendant_65Ratio());
		irpfResult.setFirstChildCalculation(record.getFirstChildCalculation());
		irpfResult.setSecondChildCalculation(record.getSecondChildCalculation());
		irpfResult.setThirdChildCalculation(record.getThirdChildCalculation());
		detail.setIrpfResult(irpfResult);
	}

	private static void insertDetailsFromInvoice(AONContext ctx, Mod190 mod190) {
		Date firstDay = AonDateUtils.getYearFirstDay(mod190.getYear());
		Date lastDay = AonDateUtils.getYearLastDay(mod190.getYear());

		Field<Integer> minRegistry = DSL.min(INVOICE.REGISTRY).as(INVOICE.REGISTRY.getName());
		Field<BigDecimal> sumBase = DSL.sum(INVOICE_TAX.BASE).as(INVOICE_TAX.BASE.getName());
		Field<BigDecimal> invoiceTaxSum = DSL.sum((DSL.round(
				(INVOICE_TAX.BASE.mul(INVOICE_TAX.PERCENTAGE)).div(100), 2)));
		Field<BigDecimal> quotaOp = DSL
				.decode()
				.when(INVOICE_TAX.QUOTA.notEqual(0.0),
						INVOICE_TAX.QUOTA.cast(BigDecimal.class))
				.when(INVOICE_TAX.QUOTA.equal(0.0), invoiceTaxSum)
				.as(INVOICE_TAX.QUOTA.getName());
		ctx.getDslContext()
				.select(INVOICE.RDOCUMENT, INVOICE.RNAME,
						INVOICE_TAX.WITHHOLDING_TYPE, minRegistry, sumBase,
						quotaOp)
				.from(INVOICE)
				.join(INVOICE_DETAIL)
				.on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.join(INVOICE_TAX)
				.on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
				.where(INVOICE.DOMAIN.equal(mod190.getDomain()))
				.and(INVOICE.TYPE.notEqual((byte) 1))				// No Ventas
				.and(INVOICE_TAX.TAX_TYPE.equal((byte) 2))			// IRPF
				.and(INVOICE_TAX.WITHHOLDING_TYPE.in((byte) 0, (byte) 3, (byte) 4)) // IRPF de Alquiler
				.and(INVOICE.ISSUE_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
				.groupBy(INVOICE.RDOCUMENT, INVOICE.RNAME,INVOICE_TAX.WITHHOLDING_TYPE)
				.fetch()
				.stream()
				.forEach(
						record -> {
							Mod190Detail detail = new Mod190Detail();
							detail.setDomain(mod190.getDomain());
							detail.setMod190(mod190.getId());
							detail.setDocument(record
									.getValue(INVOICE.RDOCUMENT));
							detail.setName(record.getValue(INVOICE.RNAME));
							byte withholding = record
									.getValue(INVOICE_TAX.WITHHOLDING_TYPE);
							if (withholding == 0) { // PROFESIONALES -
													// PROFESSIONAL
								detail.setKey("G");
								detail.setSubKey("01");
							} else if (withholding == 1) { // ARRENDAMIENTO -
															// RENTING
								// Ignore for 190 --> 180
							} else if (withholding == 2) { // CAPITAL MOBILIARIO
															// - MOVABLE_CAPITAL
								// Ignore for 190 --> 184
							} else if (withholding == 3) { // AGRICULTOR -
															// FARMER
								detail.setKey("H");
								detail.setSubKey("01");
							} else if (withholding == 4) { // TRANSPORTISTAS Y
															// ASIMILADOS -
															// TRANSPORT_OPERATOR
								detail.setKey("H");
								detail.setSubKey("04");
							}
							detail.setPerception(record.getValue(sumBase)
									.doubleValue());
							detail.setRetention(record.getValue(quotaOp)
									.doubleValue());

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
							mod190.getDetails().add(detail);
						});
	}

	private static void insertDetailsFromSalary(AONContext ctx, Mod190 mod190) {
		Date firstDay = AonDateUtils.getYearFirstDay(mod190.getYear());
		Date lastDay = AonDateUtils.getYearLastDay(mod190.getYear());

		Field<BigDecimal> moneyIrpfBase = DSL.sum(SALARY.MONEY_IRPF_BASE).as(
				SALARY.MONEY_IRPF_BASE.getName());
		Field<BigDecimal> inKindIrpfBase = DSL.sum(SALARY.INKIND_IRPF_BASE).as(
				SALARY.INKIND_IRPF_BASE.getName());
		Field<BigDecimal> irpfBase = DSL.sum(SALARY.IRPF_BASE).as(
				SALARY.IRPF_BASE.getName());
		Field<BigDecimal> totalIrpf = DSL.sum(SALARY.TOTAL_IRPF).as(
				SALARY.TOTAL_IRPF.getName());
		Field<Integer> birthYear = DSL.year(PERSON.BIRTH_DATE).as(
				PERSON.BIRTH_DATE.getName());

		ctx.getDslContext()
				.select(SALARY.EMPLOYEE_DOCUMENT, SALARY.EMPLOYEE_NAME,
						moneyIrpfBase, inKindIrpfBase, irpfBase, totalIrpf,
						PERSON.REGISTRY, birthYear)
				.from(SALARY)
				.join(CONTRACT)
				.on(SALARY.CONTRACT.equal(CONTRACT.ID))
				.join(WORKPLACE)
				.on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
				.join(PERSON)
				.on(PERSON.REGISTRY.equal(CONTRACT.PERSON))
				.where(SALARY.ISSUE_DATE.between(AonDateUtils.toSql(firstDay),
						AonDateUtils.toSql(lastDay)))
				.and(WORKPLACE.ENTERPRISE.equal(mod190.getEnterprise()))
				.and(WORKPLACE.ECONOMICAGREEMENT.equal(mod190
						.getAdministration()))
				.groupBy(SALARY.EMPLOYEE_DOCUMENT)
				.fetch()
				.stream()
				.forEach(
						salaryData -> {
							Mod190Detail detail = new Mod190Detail();
							detail.setDomain(mod190.getDomain());
							detail.setMod190(mod190.getId());
							detail.setDocument(salaryData.getValue(SALARY.EMPLOYEE_DOCUMENT));
							detail.setName(salaryData.getValue(SALARY.EMPLOYEE_NAME));
							detail.setKey("A");
							detail.setPerception(salaryData.getValue(moneyIrpfBase).doubleValue());
							detail.setInKindPerception(salaryData.getValue(inKindIrpfBase).doubleValue());
							detail.setRetention(salaryData.getValue(totalIrpf).doubleValue());
							detail.setIrpfData(getLastIrpfDataByPerson(ctx,
									salaryData.getValue(PERSON.REGISTRY), firstDay,
									lastDay));
							detail.getIrpfData().setBirthYear(salaryData.getValue(birthYear));
							detail.setIrpfResult(getLastIrpfResultByPerson(ctx,
									salaryData.getValue(PERSON.REGISTRY), firstDay,
									lastDay));
							ctx.getDslContext()
									.select(GEOZONE.CODE)
									.from(RADDRESS)
									.join(GEOZONE)
									.on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
									.where(RADDRESS.REGISTRY.equal(salaryData.getValue(PERSON.REGISTRY)))
									.and(RADDRESS.TYPE.equal((byte) 0))
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
							mod190.getDetails().add(detail);
						});

	}

	private static IrpfData getLastIrpfDataByPerson(AONContext ctx, int person,
			Date fromDate, Date toDate) {
		ctx.checkRead();
		IrpfDataRecord record = ctx
				.getDslContext()
				.select(IRPF_DATA.fields())
				.from(IRPF_DATA)
				.join(CONTRACT)
				.on(IRPF_DATA.CONTRACT.equal(CONTRACT.ID))
				.where(IRPF_DATA.END_DATE.isNull().or(
						IRPF_DATA.END_DATE.between(
								AonDateUtils.toSql(fromDate),
								AonDateUtils.toSql(toDate))))
				.and(CONTRACT.PERSON.equal(person))
				.orderBy(IRPF_DATA.END_DATE.desc())
				.fetchOneInto(IrpfDataRecord.class);
		IrpfData irpfData = new IrpfData();
		if (record != null) {
			irpfData.setCeutaMelilla(AonEnumUtils.getBoolean(record.getCeutaMelilla()));
			irpfData.setFamilySituation(record.getFamilySituation());
			irpfData.setSpouseDocument(record.getSpouseDocument());
			Byte disabilityLevel = record.getDisabilityLevel();
			if (disabilityLevel != null) {
				disabilityLevel = (byte) (disabilityLevel + 1);
			}
			irpfData.setDisability(disabilityLevel);
			irpfData.setContract((byte) (record.getContractType() + 1));
			irpfData.setWorkActivityExtension(
				AonEnumUtils.getBoolean(record.getLabourProlongation()));
			irpfData.setGeographicMobility(record.getMovingDate() != null);
		}
		return irpfData;
	}

	private static IrpfResult getLastIrpfResultByPerson(AONContext ctx,
			int person, Date fromDate, Date toDate) {
		ctx.checkRead();
		IrpfResultRecord record = ctx
				.getDslContext()
				.select(IRPF_RESULT.fields())
				.from(IRPF_RESULT)
				.join(CONTRACT)
				.on(IRPF_RESULT.CONTRACT.equal(CONTRACT.ID))
				.where(IRPF_RESULT.EFFECTIVE_DATE.between(
						AonDateUtils.toSql(fromDate),
						AonDateUtils.toSql(toDate)))
				.and(CONTRACT.PERSON.equal(person))
				.orderBy(IRPF_RESULT.EFFECTIVE_DATE.desc())
				.fetchOneInto(IrpfResultRecord.class);
		IrpfResult irpfResult = new IrpfResult();
		if (record != null) {
			double value = record.getIrregular_18_2Reduction()
					+ record.getIrregular_18_3Reduction()
					+ record.getWorkRemunerationReduction()
					+ record.getWorkProlongationReduction()
					+ record.getWorkMovingReduction()
					+ record.getWorkDisabilityReduction();
			irpfResult.setApplicableReduction(AonMathUtils.round(value));
			irpfResult.setDeducibleExpense(record.getDeducciblesExpenses());
			irpfResult.setCompensatoryPension(record.getSpousalSupport());
			irpfResult.setFoodAnnuality(record.getFoodAnnuity());
			irpfResult.setHomeLoanCommunnication(record.getDeductHomeLoanAmount() == 0);
			irpfResult.setLessThan3Descendent(record.getDescendentsMinor_3Total());
			irpfResult.setLessThan3DescendentRatio(record.getDescendentsMinor_3Entirely());
			irpfResult.setOtherDescendent(record.getDescendentsRemainderTotal());
			irpfResult.setOtherDescendentRatio(record.getDescendentsRemainderEntirely());
			irpfResult.setDisabilityDescendent33(record.getDescendents_33_65Total());
			irpfResult.setDisabilityDescendent33Ratio(record.getDescendents_33_65Entirely());
			irpfResult.setDisabilityDescendentDependence(record
					.getDescendentsMovingTotal());
			irpfResult.setDisabilityDescendentDependenceRatio(record.getDescendentsMovingEntirely());
			irpfResult.setDisabilityDescendent65(record.getDescendents_65Total());
			irpfResult.setDisabilityDescendent65Ratio(record.getDescendents_65Entirely());
			irpfResult.setLessThan75Ascendant(record.getAscendentsMinor_75Total());
			irpfResult.setLessThan75AscendantRatio(record.getAscendentsMinor_75Entirely());
			irpfResult.setAscendant(record.getAscendentsMayor_75Total());
			irpfResult.setAscendantRatio(record.getAscendentsMayor_75Total());
			irpfResult.setDisabilityAscendant33(record.getAscendents_33_65Total());
			irpfResult.setDisabilityAscendant33Ratio(record.getAscendents_33_65Entirely());
			irpfResult.setDisabilityAscendantDependence(record.getAscendentsMovingTotal());
			irpfResult.setDisabilityAscendantDependenceRatio(record.getAscendentsMovingEntirely());
			irpfResult.setDisabilityAscendant65(record.getAscendents_65Total());
			irpfResult.setDisabilityAscendant65Ratio(record.getAscendents_65Entirely());
			irpfResult.setFirstChildCalculation(record.getDescendentsFirst());
			irpfResult.setSecondChildCalculation(record.getDescendentsSecond());
			irpfResult.setThirdChildCalculation(record.getDescendentsThird());
		}
		return irpfResult;
	}
	
}
