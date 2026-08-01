package net.aonsolutions.db.up2date;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.aonsolutions.db.up2date.payroll.DeleteUnusedPaymentConcepts;
import net.aonsolutions.db.up2date.payroll.EreFzaExoneradoFixII;
import net.aonsolutions.db.up2date.payroll.ExoneEreFMayComp3707Fix;
import net.aonsolutions.db.up2date.payroll.FixPreavisoQuote;
import net.aonsolutions.db.up2date.scope.InsertDefaultScope;
import net.aonsolutions.db.up2date.security.DeleteDomainAppApp10;
import net.aonsolutions.db.up2date.security.DeleteDomainAppApp19;
import net.aonsolutions.db.up2date.security.MigrateApp23To9;
import net.aonsolutions.db.up2date.security.UserScopeAlterStartEndAuditory;
import net.aonsolutions.db.up2date.timecontrol.CalendarFixNulls;

public class Up2Date {

	private static Update[] UPDATES = {

			// IRPF2018UPDATE,
			// AGREEMENTUPDATE,
			// BASES2018UPDATE,
			// ISSUE1760UPDATE,
			// REMOVE2HIDEUPDATE,
			// HOMEBASESUPDATE,
			// TRAININGBASES2018UPDATE,
			// HOMEPERCENTAGEUPDATE
			// FELLOWSBASES2018UPDATE,
			// SYSTEMPAYMENTREADONLYUPDATE
			// BASES2018UPDATEII
			// PRESTITCOMMONDISEASEATLACKINSERT,
			// GEROA_INSERT
			// UNEMPLOYMENT
			// TRAININGBASES2018FIX,
			// FELLOWBASES2018FIX,
			// FUNDUPV_UPDATE_II
			// CRA2018UPDATE
			// ONACCOUNTAGREEMENTUPDATE
			// MEJORAINSERT
			// HOLIDAYS2018INSERT
			// CRA0057UPDATE,
			// CRA0058UPDATE
			// SYSTEM_OFF_INSERT
			// HOLIDAYS2019INSERT,
			// BASES2019UPDATE
			// BASES2019UPDATEII,
			// FUNDUPV_UPDATE_2019
			// DEFAULTAGREEMENTINSERT,
			// CRA0035UPDATE,
			// ITIMS2019INSERT,
			// NOTICEDAYSUPDATE,
			// HOMEPERCENTAGE2023UPDATE,
			// FELLOWSBASES2019UPDATE,
			// TRAINNINGBASES2019UPDATE,
			// FELLOWSBASES2019FIX,
			// TRAINNINGBASES2019FIXE
			// ALTER_SALARY_CCC
			// CGC2019FIX
			// ALTER_FS_MODEL_200_2018,
			// PRESTITDESCRIPTIONSUPDATE
			// EMBARGODESCRIPTIONSUPDATE,
			// SALARIOBASEDESCRIPTIONSUPDATE,
			// PlusSalarialInsert.PLUSSALARIALINSERT,
			// TrainningPercentages2019Update.TRAINNINGPERCENTAGES2019UPDATE,
			// FellowsPercentages2019Fix.FELLOWSPERCENTAGES2019UPDATE,
			// AgriculturalITInsert.AGRICULTURALITINSERT,
			// Bases2019UpdateIII.BASES2019UPDATEIII,
			// SalarioBaseReadOnlyUpdate.SALARIOBASEREADONLYUPDATE,
			// NetoAndBrutoReadOnlyUpdate.NETOBRUTOREADONLYUPDATE,
			// DefaultAgreementUpdate.DEFAULTAGREEMENTUPDATE,
			// SalaryHours2019Insert.SALARYHOURS2019INSERT
			// KaldeviFinanceFix.KALDEVIFINANCEFIX
			// FundUpvUpdateIII.FUNDUPV_UPDATE_III
			// Artist2019Insert.ARTIST2019INSERT,
			// DropDays2019Insert.DROPDAYS2019INSERT
			// Irpf2020Update.IRPF2020UPDATE
			// AyudaTUpdate.AYUDATUPDATE
			// Irpf2020UpdateII.IRPF2020UPDATEII
			// AlterFsMod184Detail2019.ALTER_FS_MODEL_184_DETAIL_2019
			// Holidays2020Insert.HOLIDAYS2020INSERT
			// FundUpvUpdate2020.FUNDUPV_UPDATE_2020,
			// SMI2020Update.SMI2020UPDATE,
			// PagaExtraUpdate.PAGAEXTRAUPDATE,
			// TempPaymentInsert.TEMPPAYMENTINSERT
			// SalaryHoursUpdate.SALARYHOURSUPDATE
			// RegistryInsert.REGISTRYINSERT
			// FundUpvUpdateIV.FUNDUPV_UPDATE_IV,
			// FundUpvUpdateV.FUNDUPV_UPDATE_V,
			// IndemnizacionEditableUpdate.INDEMNIZACIONEDITABLEUPDATE,
			// IndemnizacionDescriptionUpdate.INDEMNIZACIONDESCRIPTIONUPDATE,
			// EreFzaInsert.EREFZAINSERT,
			// SMIWarn2020Delete.SMIWARN2020DELETE,
			// AyudaTWarnDelete.AYUDATWARNDELETE,SMI2026Update
			// EreFzaExoneradoInsert.EREFZAEXONERADOINSERT,
			// AlterCertifica2BatchDetail4ERE.ALTER_CERTIFICA2_BATCH_DETAIL_ERE,
			// EreFzaExoneradoUpdate.EREFZAEXONERADOUPDATE[] 
			// EresUpdate.ERESUPDATE
			// PrestITDescriptionsUpdateII.PRESTITDESCRIPTIONSUPDATEII,
			// FellowsPercentages2019Fix.FELLOWSPERCENTAGES2019FIX,
			// TrainningPercentages2019Fix.TRAINNINGPERCENTAGES2019FIX,
			// GarantizadoUpdate.GARANTIZADOUPDATE,
			// DefaultAgreementUpdate2.DEFAULTAGREEMENTUPDATE2,
			// FixERESalaries.FIXERESALARIES,
			// FixERESalariesII.FIXERESALARIESII,
			// SalaryHoursUpdateFactor.SALARYHOURSUPDATEFACTOR,
			// GarantizadoFix.GARANTIZADOFIX
			// EresUpdateII.ERESUPDATE,
			// MejoraInsert.MEJORAINSERT,
			// DomainName2LowerCase.DOMAINNAME2LOWERCASE
			// EmbargarFix.EMBARGARFIX
			// RealDecreeLaw182020Insert.REALDECREELAW182020INSERT
			// InvoiceDUACreation.INVOICEDUACREATION
			// RealDecreeLaw182020Update.REALDECREELAW182020UPDATE
			// DefaultAgreementDeleteWarn.DEFAULTAGREEMENTDELETaon-db-up2date/src/main/java/net/aonsolutions/db/up2date/payroll/PPEITUpdate.javaEWARN
			// RealDecreeLaw182020UpdateII.REALDECREELAW182020UPDATEII
			// SnapshotCreation.AUTH_CREATION,
			// SalaryHoursUpdateMonthDays.SALARYHOURSUPDATEMONTHDAYS,
			// RealDecreeLaw182020UpdateIII.REALDECREELAW182020UPDATEIII,
			// AlterFsMod2002019.ALTER_FS_MODEL_200_2019
			// DomainAppCreation.DOMAIN_APP_CREATION,
			// UserAppRoleCreation.USER_APP_ROLE_CREATION,
			// RealDecreeLaw182020UpdateIV.REALDECREELAW182020UPDATEIV
			// TrainningBases2020Fix.TRAINNINGBASES2020FIX
			// AuthUpdate.AUTH_UPDATE
			// AlterAccountEntryDetailConcept.ALTER_ACCOUNT_ENTRY_DETAIL_CONCEPT,
			// EresUpdateIII.ERESUPDATEIII,
			// SMI2020UpdateRollback.SMI2020UPDATEROLLBACK
			// DropTableAccountHelper.DROP_TABLE_ACCOUNT_HELPER
			// RawdocCreation.RAWDOC_CREATION,
			// Irpf2021Update.IRPF2021UPDATEII,
			// LocationCreation.LOCATION_CREATION,			.set(SYSTEM_DATA.DOMAIN, DOMAIN)

			// TimeControlCreation.TIMECONTROL_CREATION,
			// CoordinatesUpdate.COORDINATES_UPDATE,
			// AlterAgreement4SSNumber.ALTER_AGREEMENT_SSNUM,
			// Holidays2021Insert.HOLIDAYS2021INSERT,
			// AdditionalHoursUpdate.ADDITIONALHOURSUPDATE,
			// AuthDeviceCreation.AUTH_DEVICE_CREATION,
			// PermissionNotPaidDaysInsert.PERMISSIONNOTPAIDDAYSINSERT,
			// IfDaysInsert.IFDAYSINSERT
			// Bases2021Update.BASES2021UPDATE
			// AuthDeviceUpdate.AUTH_DEVICE_UPDATE,
			// IntegrityFix.INTEGRITYFIX,
			// RetirementInsert.RETIREMENTINSERT,
			// NotificationCreation.NOTIFICATION_CREATION,
			// NotificationReceiverCreation.NOTIFICATION_RECEIVER_CREATION,SMI2026Update
			// Bases2021UpdateII.BASES2021UPDATEII,
			// InKindDeductionInsert.INKIND_DEDUCTION_INSERT,
			// WorkAccidentInsuranceInsert.WORKACCIDENTINSURANCEINSERT
			// InKindDeductionInsertRETA.INKIND_DEDUCTION_INSERT_RETA,
			// UpdateInvoiceSource.UPDATE_INVOICE_SOURCE
			// Bases2021Rollback.BASES2021ROLLBACK,
			// IndemnizacionFinIRPFFix.INDEMNIZACIONFINIRPFFIX,
			// IrpfNavarra2021Update.IRPFNAVARRA2021UPDATE,
			// IrpfEuskadi2021Insert.IRPFEUSKADI2021INSERT
			// AuthAttachCreation.AUTH_ATTACH_CREATION,
			// AlterFsMod2002020.ALTER_FS_MODEL_200_2020,
			// TaskWorkflowCreation.TASK_WORKFLOW_CREATION,aon-db-up2date/src/main/java/net/aonsolutions/db/up2date/payroll/PPEITUpdate.java
			// TaskAttachCreation.TASK_ATTACH_CREATION
			// ContractCostCreation.CONTRACTCOSTCREATION,
			// CRA0062Insert.CRA0062INSERT
			// IrpfHomeUpdate.IRPFHOMEUPDBASE_HORAS_COMPLATE
			// TrainningPercentages2019FixII.TRAINNINGPERCENTAGES2021FIXII
			// BaseCgcMin2019Fix.BASECGCMIN2019FIX
			// OvertimeCostsFix.OVERTIMECOSTSFIX
			// ReduccionCgcE02Fix.REDUCCIONCGCE02FIX,
			// AgriculturalATEPEFix.AGRICULTURALATEPEFIX
			// AdditionalHoursKiss.ADDITIONALHOURSKISS,
			// TaskWorkflowUpdate.TASK_WORKFLOW_UPDATE,
			// ProjectHolderCreation.PROJECT_HOLDER_CREATION,
			// SMI2021Update.SMI2020UPDATE,
			// InvoiceFiscalCreation.INVOICE_FISCAL_CREATION,
			// BofFormYTutoriaInsert.BOFFORMYTUTORIAINSERT,
			// RemoveDomainAppBankForParent.REMOVE_DOMAIN_APP_BANK_FOR_PARENT,
			// RemoveDataResponse.REMOVE_DATA_RESPONSE,
			// DataRequestCreation.DATA_REQUEST_CREATION,
			// CertificatesUpdate.CERTIFICATESUPDATE,
			// DataRequestUpdate.DATA_REQUEST_UPDATE,
			// Bases2021UpdateIII.BASES2021UPDATEIII,
			// HomeBases2019Close.HOMEBASES2019CLOSE,
			// HomeBases2021UpdateIII.HOMEBASES2021UPDATEIII,
			// SalaryHours2021UpdateIII.SALARYHOURS2021UPDATEIII,
			// TrainingBases2021UpdateIII.TRAININGBASES2021UPDATEIII,
			// AgriculturalBases2021UpdateIII.AGRICULTURALBASES2021UPDATEIII,
			// TrainingPercentages2021EUpdate.TRAINNINGPERCENTAGES2021UPDATE,
			// FellowsPercentages2021Update.FELLOWSPERCENTAGES2021UPDATE,
			// DataRequestUpdate.DATA_REQUEST_UPDATE,
			// WorkplaceUpdate.WORKPLACE_UPDATE,
			// IrpfM190Update.IRPFM190UPDATE,
			// ContractCleanUpdate.CONTRACTCLEANUPDATE,
			// TimeControlUpdate.TIMECONTROL_UPDATE,
			// Holidays2022Insert.HOLIDAYS2022INSERT,
			// Bases2022Update.BASES2022UPDATE,
			// AgriculturalBases2022Update.AGRICULTURALBASES2022UPDATE,
			// AgriculturalPercentages2022Update.AGRICULTURALPERCENTAGES2022UPDATE,
			// IPREM2022Update.IPREM2022UPDATE,
			// IrpfEuskadi2022Insert.IRPFEUSKADI2022INSERT,
			// IrpfQuotasInsert.IRPFQUOTASINSERT,
			// IrpfQuotasCheck.IRPFQUOTASCHECK,
			// AlterAgreement4Owner.ALTER_AGREEMENT_OWNER,
			// AgreementOwnerUpdate.AGREEMENTOWNERUPDATE,
			// RealDecreeLaw322021Art151Update.REALDECREELAW322021ART151UPDATE,
			// SMI2022Update.SMI2022UPDATE,
			// PrestITFactorUpdate.PRESTITFACTORUPDATE,
			// CertificateDomainFix.CERTIFICATEDOMAINFIX,
			// SalaryHoursUpdateDaily.SALARYHOURSUPDATEDAILY,
			// AgreementClean.AGREEMENTCLEAN,
			// CertificatesClean.CERTIFICATESCLEAN,
			// Bases2022UpdateII.BASES2022UPDATEIII,
			// HomeBases2022Update.HOMEBASES2022UPDATE,
			// TrainningBases2022Update.TRAINNINGBASES2022UPDATE,
			// TrainingPercentages2022Update.TRAINNINGPERCENTAGES2022UPDATE,
			// FellowsPercentages2022Update.FELLOWSPERCENTAGES2022UPDATE,
			// Artist2022Update.ARTIST2022UPDATE,
			// RefreshMod303Result.REFRESH_MOD303_RESULT,
			// Artist2022Fix.ARTIST2022FIX,
			// Bases2022Fix.BASES2022FIX,
			// SalaryHours2022UpdateDaily.SALARYHOURS2022UPDATEDAILY,
			// UdapaSalesFix.UDAPA_SALES_FIX,
			// SalaryHours2022Fix.SALARYHOURS2022FIX,
			// BaseCgpMin2022Fix.BASECGPMIN2022FIX,
			// AgriculturalBases2022UpdateII.AGRICULTURALBASES2021UPDATEIII,
			// TaskEvent2TaskWorkfow.TASK_COMMENT_2_TASK_WORKFLOW,
			// TaskComment2TaskWorkfow.TASK_EVEMT_2_TASK_WORKFLOW,
			// UpdateFinanceRname.UPDATE_FINANCE_RNAME,
			// BaseCgpMin2022FixIII.BASECGPMIN2022FIX,
			// DomainName2LowerCase.DOMAINNAME2LOWERCASE,
			// AlterSalaryEmbargo.ALTERSALARYEMBARGO,
			// Holidays2022Update.HOLIDAYS2022UPDATE,
			// Art1512022Update.ART1512022UPDATE,
			// ContractAttachUpdate.CONTRACTATTACHUPDATE,
			// AlterSalaryDeduction.ALTERSALARYDEDUCTION,
			// TrainingPercentages2022UpdateII.TRAINNINGPERCENTAGES2022UPDATEII,
			// TaskAddEvaluation.TASK_ADD_EVALUATION,
			// AlterContractBonus.ALTERCONTRACTBONUS,
			// SalaryHoursFix.SALARYHOURSFIX,
			// ArtistPartialFactorFix.ARTISTPARTIALFACTORFIX,
			// AgriculturalBases2022UpdateIII.AGRICULTURALBASES2021UPDATEIII,
			// CnoUpdate.CNOUPDATE,
			// AssimilatedInsert.ASSIMILATEDINSERT,
			// Bases2022FixHourly.BASES2022FIXHOURLY,
			// AssimilatedInsert.ASSIMILATEDINSERT,
			// Bases2022FixHourly.BASES2022FIXHOURLY,
			// InsertIAE863.INSERT_IAE_863,
			// SalaryHoursFixII.SALARYHOURSFIXII,
			// AlterAgreement4Log.ALTERAGREEMENTLOG,
			// RealDecreeLaw322021Art151Fix.REALDECREELAW322021ART151FIX,
			// FellowsITIMS2022UpdateII.FELLOWSITIMS2022UPDATEII,
			// JacalInvoiceUpdateFix.JACA_INVOICE_UPDATE,
			// AgriculturalMonthlyQuote2022Fix.AGRICULTURALMONTHLYQUOTE2022FIX,
			// ContractTransfromUnify.CONTRACTTRANSFORMUNIFY,
			// AlterFsMod200Audit.ALTER_FS_MOD200_AUDIT,
			// TaskSourceUpdate.TASK_SOURCE_UPDATE,
			// DocumentalUpdateFix.DOCUMENTAL_UPDATE_FIX,
			// AlterFsMod200FsModel.ALTER_FS_MOD200_FS_MODEL,
			// ElaborationDetailUpdate.ELABORATION_DETAIL_UPDATE,
			// IrpfEuskadiSeptember2022Insert.IRPFEUSKADISEPTEMBER2022INSERT,
			// AlterSalaryAddIndexSocialSecurityNumber.ALTER_SALARY_ADD_INDEX_SOCIALSECURITYNUMBER,
			// MaxEmbargableFix.MAXEMBARGABLEFIX,
			// ContractExtraCreation.CONTRACTEXTRACREATION,
			// IrpfInKindSystemInsert.IRPFINKINDSYSTEMINSERT,
			// TrainingBases2022Fix.TRAININGBASES2022FIX,
			// TrainingBases2022FixII.TRAININGBASES2022FIXII,
			// UdapaPfondoUpdateFix.UDAPA_PFONDO_UPDATE_FIX,
			// SevenConsultingDomainNameUpdate.SEVEN_CONSULTING_DOMAIN_NAME_UPDATE,
			// InvoiceAddAnnulledColumn.INVOICE_ADD_ANNULLED_COLUMN,
			// HomeQuote2022Update.HOMEQUOTE2022UPDATE,
			// ContractDocCreate.CONTRACTDOCCREATE
			// AlterSalaryBonusConcept.ALTERSALARYBONUSCONCEPT,
			// HomeBonus2012Update.HOMEBONUS2012UPDATE,
			// DefaultPaymentConcepts.DEFAULTPAYMENTCONCEPTS,
			// AgriculturalITRemove.AGRICULTURALITREMOVE,
			// AlterBankStatementReference2.ALTER_BANK_STATEMENT_REFERENCE_2,
			// AlterContractBonusExpression.ALTER_CONTRACT_BONUS_EXPRESSION,
			// AlterContractBonusDescription.ALTER_CONTRACT_BONUS_DESCRIPTION,
			// AlterContractPaymentDescription.ALTERCONTRACTPAYMENTDESCRIPTION,
			// FixPreaviso.FIXPREAVISO,
			// RefreshMod390HFResult.REFRESH_MOD390_RESULT,
			// AlterRbankAddRequisition.ALTER_RBANK_ADD_REQUISITION,
			// AlterRbankAddSepaMandateRef.ALTER_RBANK_ADD_SEPA_MANDATE_REF,
			// Holidays2023Insert.HOLIDAYS2023INSERT,
			// AgreementPaymentsConceptFix.AGREEMENTPAYMENTSCONCEPTFIX,
			// DeathOfEmployeeInsert.DEATHOFEMPLOYEEINSERT,
			// AlterAgreementPaymentDescription.ALTERAGREEMENTPAYMENTDESCRIPTION,
			// UpdateInvoiceRegistryDocument.UPDATE_INVOICE_REGISTRY_DOCUMENT,
			// AlterAlcatrazFinance.ALTER_ALCATRAZ_FINANCE,
			// IrpfEuskadi2023Insert.IRPFEUSKADI2023INSERT,
			// BasesMax2023Update.BASESMAX2023UPDATE,
			// MEIInsert.MEIINSERT,
			// AlterFsMod190Detail2022.ALTER_FS_MODEL_190_DETAIL_2022,
			// HomeBases2023Update.HOMEBASES2023UPDATE,
			// MEIPECUpdate.MEIPECUPDATE,
			// AlterFsMod190Detail2022.ALTER_FS_MODEL_190_DETAIL_2022,
			// InvoiceFiscalAddExpDate.INVOICE_FISCAL_ADD_EXP_DATE,
			// AlterFsMod184Detail2022.ALTER_FS_MODEL_184_DETAIL_2022,
			// BiEmbargarInsert.BIEMBARGARINSERT,
			// AlterFsMod184Detail2022.ALTER_FS_MODEL_184_DETAIL_2022,
			// IrpfDeductionSplit.IRPFDEDUCTIONSPLIT,
			// HomePercentage2023Update.HOMEPERCENTAGE2023UPDATE,
			// UnemploymentPercentageFix.UNEMPLOYMENTPERCENTAGEFIX,
			// MEITrainingRemove.MEITRAININGREMOVE,
			// MEIPECUpdateUndo.MEIPECUPDATEUNDO,
			// MEITrainingRemoveUndo.MEITRAININGREMOVEUNDO,
			// Embargar4Many.BIEMBARGAR4MANY,
			// FinanceTrackingAmountFix.FINANCE_TRACKING_AMOUNT_FIX,
			// OcupationITIMSFix.OCUPATIONITIMSFIX,
			// Embargar4Many.BIEMBARGAR4MANY,
			// BasesMin2023Update.BASESMIN2023UPDATE,
			// AlterAlcatrazFinanceTracking.ALTER_ALCATRAZ_FINANCE_TRACKING,
			// SMI2023Update.SMI20223UPDATE,
			// IPREM2023Update.IPREM2022UPDATE,
			// BasesMin2023Guess.BASESMIN2023GUESS,
			// EmbargoDescriptionUpdate.EMBARGODESCRIPTIONUPDATE,
			// AlterCostCode.ALTERCOSTCODE,
			// MaxEmbargable4Daily.MAXEMBARGABLE4DAILY,
			// AgreementEmptyTrash.AGREEMENTEMPTYTRASH,
			// UdpatePortalConectaUsers.UPDATE_PORTAL_CONECTA_USERS,
			// UdpatePacksDomainApp.UPDATE_PACKS_DOMAIN_APP,
			// InsertTreasuryAndMarketingModule.INSERT_TREASURY_AND_MARKETING_MODULE,
			// UdpateAonSmb.UPDATE_AON_SMB,
			// EmbargoDescriptionUpdate.EMBARGODESCRIPTIONUPDATE,
			// AgreementEmptyTrash.AGREEMENTEMPTYTRASH,
			// UpdateBankAccountUpperCase.UPDATE_BANK_ACCOUNT_UPPERCASE,
			// AlterCostCode.ALTERCOSTCODE,
			// MEITrainingBaseCgcMinFix.MEITRAININGBASECGCMINFIX,
			// ActiveRetirementInsert.ACTIVERETIREMENTINSERT,
			// FixPec01Quota01.FIXPEC01QUOTA01,
			// RefreshMod130Result.REFRESH_MOD130_RESULT,
			// AlterFsMod349FsModel.ALTER_FS_MOD349_FS_MODEL,
			// MaxEmbargable4ProrratedFix.MAXEMBARGABLE4PRORRATEDFIX,
			// ArtistBaseCgcMax2023Update.ARTISTBASECGCMAX2023UPDATE,
			// MaxEmbargable4ProrratedFix.MAXEMBARGABLE4PRORRATEDFIX,
			// RefreshMod130Result.REFRESH_MOD130_RESULT,
			// AgreementPaymentStartDateUpdate.AGREEMENT_PAYMENT_START_DATE_UPDATE,
			// Mod145StartDateUpdate.MOD145STARTDATEUPDATE,
			// IrpfDelaysUpdate.IRPFDELAYSUPDATE,
			// Bases2023Update.BASES2023UPDATE,
			// HomeBases2023UpdateII.HOMEBASES2023UPDATEII,
			// FellowBases2023Update.FELLOWBASES2023UPDATE,
			// TrainningBases2023Update.TRAINNINGBASES2023UPDATE,
			// MEITrainingBaseCgcMinUndo.MEITRAININGBASECGCMINUNDO,
			// AgriculturalBases2023Update.AGRICULTURALBASES2023UPDATE,
			// MEIUpdate.MEIUPDATE,
			// Art1512023Update.ART1512023UPDATE,
			// FellowsPercentages2023Update.FELLOWSPERCENTAGES2023UPDATE,
			// TrainingPercentages2023Update.TRAINNINGPERCENTAGES2023UPDATE,
			// PrestITSexAndHealthInsert.PRESTITSEXANDHEALTHINSERT,
			// IndemnizacionTemporalUpdate.INDEMNIZACIONTEMPORALUPDATE,
			// PrestITSexAndHealthIrpfFix.PRESTITSEXANDHEALTHIRPFFIX,
			// TrainningBases2023Fix.TRAINNINGBASES2023FIX,
			// CGPJInsert.CGPJINSERT,
			// Trainning421ExcessQuote.TRAINNING421EXCESSQUOTE,
			// HomeBases2023UpdateIII.HOMEBASES2023UPDATEIII,
			// TrainningBaseCgcMinUndo.TRAINNINGBASECGCMINUNDO,
			// TrainingQuote2023Fix.TRAINNINGPERCENTAGES2023UPDATE,
			// CRA0050ExcesoRefactor.CRA0050EXCESOREFACTOR,
			// DomainAonStatus.DOMAIN_AON_STATUS,
			// DomainAonCustomer.DOMAIN_AON_CUSTOMER,
			// FixBofFormTDistan.FIXBOFFORMTDISTAN,
			// PermissionUnPaidDaysFix.PERMISSIONNOTPAIDDAYSFIX,
			// NoteInsert.NOTEINSERT,
			// SuspendJobAndSalaryDaysInsert.SUSPENDJOBANDSALARYDAYSINSERT,
			// RealDecreeLaw012023Insert.REALDECREELAW012023INSERT,
			// RealDecreeLaw012023421Fix.REALDECREELAW012023421FIX,
			// TrainingUnemployment2023Fix.TRAININGUNEMPLOYMENT2023FIX,
			// Holidays2024Insert.HOLIDAYS2024INSERT,
			// IrpfEuskadi2024Insert.IRPFEUSKADI2024INSERT,
			// MEI2024Insert.MEI2024INSERT,
			// BasesMax2024Update.BASESMAX2024UPDATE,
			// SMI2024Update.SMI2024UPDATE,
			// BasesMin2024Update.BASESMIN2024UPDATE,ALTER TABLE elaboration ADD INDEX `IDX_ELABORATION_SOURCE_SOURCEID` (`source`,`source_id`);
			// AlterRitem.ALTER_RITEM,
			// AlterRsellerAddType.ALTER_RSELLER_ADD_TYPE,
			// AlterInvestAsset.ALTER_INVEST_ASSET,
			// AgreementPurge.AGREEMENTPURGE,
			// SiltraRattachOldDelete.SILTRARATTACHOLDDELETE,
			// SagardoBusUpdate.SAGARDOBUS_UPDATE,
			// AgreementPaymentStartDateUpdate.AGREEMENT_PAYMENT_START_DATE_UPDATE,
			// UdpateAonSmb.UPDATE_AON_SMB,
			// UdpateAonProfessional.UPDATE_AON_PROFESSIONAL,
			// UdpateAonSmb.UPDATE_AON_SMB,
			// UdpateAonProfessional.UPDATE_AON_PROFESSIONAL,
			// InsertIAE575576.INSERT_IAE_755756,PERMISO NO RETRIBUIDO 26/09
			// ProductPerishableUpdate.PRODUCT_PEISHABLE_UPDATE,
			// DeliveryPackagingCreation.DELIVERY_PACKAGING_CREATION,
			// SalesInfoCreation.SALES_INFO_CREATION,
			// InsertIAE1516.INSERT_IAE_1516,
			// UdpateAonPacks.UPDATE_AON_PACKS,
			// InsertIAE474.INSERT_IAE_474,
			// AlterRegistryBankBalance.ALTER_REGISTRY_BANK_BALANCE,
			// RemovePayrollPortal.REMOVE_PAYROLL_PORTAL,
			// DeliveryInfoCreation.DELIVERY_INFO_CREATION,
			// DeliveryInfoInsert.DELIVERY_INFO_INSERT,
			// SalesDetailAddDeliveryColumn.SALES_DETAIL_ADD_DELIVERY_COLUMN,
			// CraBatchResetDateUpdate.CRABATCHRESETDATEUPDATE,
			// AlterNoteArchiveTag.ALTERNOTEARCHIVETAG,
			// AlterNoteDropNoteTag.ALTERNOTEDROPNOTETAG,
			// AlterNoteTagLength.ALTERNOTETAGLENGTH,
			// AlterFsMod190Detail2023.ALTER_FS_MODEL_190_DETAIL_2023,
			// AddColumnActivityExemptionCause.ADD_COLUMN_ACTIVITY_EXEMPTION_CAUSE,
			// InsertIAE3034.INSERT_IAE_3034,
			// AlterFsMod193Detail2023.ALTER_FS_MODEL_193_DETAIL_2023,
			// AlterFsMod184Detail2023.ALTER_FS_MODEL_184_DETAIL_2023,
			// AlterContractDeductionExpression.ALTER_CONTRACT_DEDUCTION_EXPRESSION,
			// InvoiceInfoLroeUpdate.INVOICE_INFO_LROE_UPDATE,

			// HomeBases2023UpdateIV.HOMEBASES2023UPDATEIV,
			// CodinucovaInvoiceTaxFix.INSTANCE,
			// InsertIAE848889.INSERT_IAE_848889,
			// DomainAddDomainPayerColumn.DOMAIN_ADD_DOMAIN_PAYER_COLUMN,
			// PPEInsert.PPE_INSERT
			// BasesMin2024UpdateII.BASESMIN2024UPDATEII,
			// BasesMin2024UpdateIII.BASESMIN2024UPDATEIII
			// RItemAddCustomerFeeColumn.RITEM_ADD_CUSTOMER_FEE_COLUMN,
			// RItemAddSellerColumn.RITEM_ADD_SELLER_COLUMN,
			// InvoiceDocCreate.INVOICEDOCCREATE,
			// RawdocAddColumS3Key.RAWDOC_ADD_COLUMN_S3_KEY,
			// BasesMin2024UpdateIV.BASESMIN2024UPDATEIV,
			// HomeBases2024Update.HOMEBASES2024UPDATEIV,
			// Art1512024Update.ART1512024UPDATE,
			// FellowsBases2024Update.FELLOWSBASES2024UPDATE,
			// TrainningBases2024Update.TRAINNINGBASES2024UPDATE,
			// TrainingPercentages2024Update.TRAINNINGPERCENTAGES2023UPDATE,
			// FellowsPercentages2024Update.FELLOWSPERCENTAGES2023UPDATE,
			// RefreshMod131Result.REFRESH_MOD131_RESULT

			// AgriculturalBases2024Update.AGRICULTURALBASES2024UPDATE,
			// InvoiceDuplicateFix.INVOICE_DUPLICATE_FIX,
			// UdpateDomainApp.UPDATE_DOMAIN_APP,
			// AlterRitemEdiSalesCode.ALTER_RITEM_EDI_SALES_CODE,
			// PPEUpdate.PPE_UPDATE,
			// InvoiceInfoAddAudit.INVOICE_INFO_ADD_AUDIT
			// AddColumnMarketingCampaignBudget.ADD_COLUMN_MARKETING_CAMPAIGN_BUDGET,
			// AddColumnMarketingActionBudget.ADD_COLUMN_MARKETING_ACTION_BUDGET,

			// MEIFellows2024Remove.MEIFELLOWSREMOVE,

			// AddColumnMarketingCampaignExpense.ADD_COLUMN_MARKETING_CAMPAIGN_EXPENSE,
			// AddColumnMarketingActionExpense.ADD_COLUMN_MARKETING_ACTION_EXPENSE,
			// AddColumnMarketingCampaignWorkgroup.ADD_COLUMN_MARKETING_CAMPAIGN_WORKGROUP,
			// AddColumnMarketingActionWorkgroup.ADD_COLUMN_MARKETING_ACTION_WORKGROUP,
			// AddColumnMarketingCampaignTaskHolder.ADD_COLUMN_MARKETING_CAMPAIGN_TASK_HOLDER,
			// AddColumnMarketingActionTaskHolder.ADD_COLUMN_MARKETING_ACTION_TASK_HOLDER,

			// AddColumnMarketingActionTargetProject.ADD_COLUMN_MARKETING_ACTION_TARGET_PROJECT,
			// AddColumnMarketingActionTargetAudit.ADD_COLUMN_MARKETING_ACTION_TARGET_AUDIT,
			// AddColumnSellerTaskHolder.ADD_COLUMN_SELLER_TASK_HOLDER,

			// CleanDuplicatesPayrollWorkplace.CLEAN_DUPLICATE_PAYROLL_WORKPLACE,
			// AnnuledInvoiceCreation.ANNULED_INVOICE_CREATION,
			// AddColumnMarketingActionTag.ADD_COLUMN_MARKETING_ACTION_TAG,

			// AlterFsMod2002023.ALTER_FS_MODEL200_2023,
			// AlterFsMod200Registry2023.ALTER_FS_MODEL200_REGISTRY_2023,
			// ElaborationSerialNumberUpdate.ELABORATION_SERIAL_NUMBER_UPDATE,

			// AddColumnTaskHolderWorkgroupDatesType.ADD_COLUMN_TASK_HOLDER_WORKGROUP_DATES_TYPE,
			// InsertIAE864.INSERT_IAE_864,

			// AgreementUpdateHideMessage.AGREEMENTUPDATEHIDEMESSAGE,
			// AgreementUpdateHideToDisable.AGREEMENTUPDATEHIDETODISABLE,

			// InKindDeductionUpdate.INKIND_DEDUCTION_UPDATE,
			// InvoiceDataCreation.INVOICE_DATA_CREATION,
			// InvoiceDocAddS3Bucket.INVOICEDOC_ADDS3BUCKET

			// UrlShortenCreation.URLSHORTEN_CREATION,
			// Artist2024Update.ARTIST2024UPDATE,
			// InvofoxEnterpriseDataFix.INVOFOX_ENTERPRISE_DATA_FIX,
			// AlterFsMod200Nrc.ALTER_FS_MODEL200_NRC,
			// InvoiceBatchAlter.INVOICE_BATCH_ALTER

			// AlterFsMod2002020.ALTER_FS_MODEL_200_2020
			// SolidarityInsert.SOLIDARITYINSERT,

			// FsModel369Creation.FS_MODEL369_CREATION,
			// AlterFsMod193Detail2024.ALTER_FS_MODEL_193_DETAIL_2024

			// LackPayInsert.LACKPAYINSERT

			// TrainingExcessDeductions2024Insert.TRAININGEXCESSDEDUCTIONS2024INSERT,
			// FPPercentage2024Fix.FPPERCENTAGE2024FIX,
			// AlterFsMod190Detail2024.ALTER_FS_MODEL_190_DETAIL_2024

			// MEI2025Insert.MEI2025INSERT,
			// Holidays2025Insert.HOLIDAYS2025INSERT
			// BasesMax2025Update.BASESMAX2025UPDATEl
			
			// SalaryHoursFixIII.SALARYHOURSFIXIII
			
			// MEI2025Rollback.MEI2025ROLLBACK
			// MEI2025Insert.MEI2025INSERT,
			// RemakeTakaraFix.REMAKETAKARAFIX
			// AlterUserAddIndexAuth.ALTER_USER_ADD_INDEX_AUTH,
			// AlterInvoiceAddIndexDomainStatus.ALTER_INVOICE_ADD_INDEX_DOMAIN_STATUS
			
			// SMI2025Update.SMI2025UPDATE,
			// PartialRetirementInsert.PARTIAL_RETIREMENT_INSERT
			// HomeSolidarityRemove.HOME_SOLIDARITY_REMOVE
			
			// BasesMin2025Update.BASESMIN2025UPDATE,
			// HomeBases2025Update.HOMEBASES2025UPDATE,
			// FellowsBases2025Update.FELLOWSBASES2025UPDATE,
			// TrainningBases2025Update.TRAINNINGBASES2025UPDATE,
			// FellowsPercentages2025Update.FELLOWSPERCENTAGES2024UPDATE,
			// TrainingPercentages2025Update.TRAINNINGPERCENTAGES2025UPDATE,
			
			// Artist2025Update.ARTIST2025UPDATE,
			// AgriculturalBases2025Update.AGRICULTURALBASES2025UPDATE,
			// AlterFsMod369Detail2025.ALTER_FS_MODEL_369_DETAIL_2025,
			// RefreshMod202Result.REFRESH_MOD202_RESULT
			// RealDecreeLaw1782025Art28Update.REALDECREELAW1782025ART28UPDATE,
			// DropDaysFix.DROPDAYSFIX
			
			// MEITrainingFix2025.MEITRAININGFIX2025,
			// DocumentalCategoryTree.DOCUMENTAL_CATEGORY_TREE,
			// DocumentalRDoc.DOCUMENTAL_RDOC,
			// AgreementPaymentsMonthFix.AGREEMENTPAYMENTSMONTHFIX,
			// RealDecreeLaw1782025Art28300Fix.REALDECREELAW1782025ART28300FIX

			// AgriculturalPercentageCgcE2025Fix.AGRICULTURALPERCENTAGECGCE2025FIX
			// SalaryHoursFixIV.SALARYHOURSFIXIV,
			// SalaryHoursFixV.SALARYHOURSFIXV,
			// TrainingExcessDeductions2024InsertFaseIII.TRAININGEXCESSDEDUCTIONS2024INSERTFASEIII,
			// AgriculturalRealDecreeLaw1782025Art28Fix.AGRICULTURALREALDECREELAW1782025ART28FIX,
			// AlterContractCostExpression.ALTERCONTRACTCOSTEXPRESSION,
			// HomePrestITInsert.HOMEPRESTITINSERT,
			// PPEITUpdate.PPE_IT_UPDATE,
			// PPEITFix.PPE_IT_FIX,
			// PPEITUpdateII.PPE_IT_UPDATEII,
			// PPEUpdate2Cost.PPE_IT_UPDATE2COST,
			// Artist2025CgcTempRemove.ARTIST2025CGCTEMPREMOVE,
			// TrainingExcessDeductionsITIMSFix.TRAININGEXCESSDEDUCTIONSITIMSFIX,
			// TrainingExcessRoundFix.TRAININGEXCESSROUNDFIX,
			// TrainingExcessDeductionsNamesFix.TRAININGEXCESSDEDUCTIONSNAMESFIX,
			// IrpfEuskadi2025Insert.IRPFEUSKADI2025INSERT,
			// AlterFsMod2002024.ALTER_FS_MODEL_200_2024, 
			// InsertIAE869.INSERT_IAE_869,
			// RegistryFulltextKey.REGISTRY_FULLTEXT_KEY,
			// TaskParentIndex.TASK_PARENT_INDEX,
			// NotificationReceiverAuthIndex.NOTIFICATION_RECEIVER_AUTH_INDEX,
			// RegistryDocumentIndex.REGISTRY_DOCUMENT_INDEX,
			// TaskWorkflowCommentModify.TASK_WORKFLOW_COMMENT_MODIFY,
			// AlterAlcatraz4Liquidation.ALTER_ALCATRAZ_4_LIQUIDATION,
			// InvoiceBatchEndDateTime.INVOICEBATCH_ENDDATETIME,
			// BaseCgpMin2025Fix.BASECGPMIN2025FIX,
			// RealDecreeLaw1782025Art28300Fix510.REALDECREELAW1782025ART28300FIX,
			// PPEUpdate2CostFix.PPEUPDATE2COSTFIX,
			// BonificEntrenadoresYMonitoresInsert.BONIFICENTRENADORESYMONITORESINSERT,
			// AddAonClassic.ADD_AON_CLASSIC,
			// PermissionUnPaidDescriptionFix.PERMISSIONUNPAIDDESCRIPTIONFIX,
			// PrestITDescriptionFix.PRESTITDESCRIPTIONFIX,
			// PermissionUnPaidDescriptionFixII.PERMISSIONUNPAIDDESCRIPTIONFIXII,
			// AlterFsMod193Detail2025.ALTER_FS_MODEL_193_DETAIL_2025,
			// AlterFsMod347Detail2025.ALTER_FS_MODEL_347_DETAIL_2025,
			// AlterFsMod190Detail2025.ALTER_FS_MODEL_190_DETAIL_2025,
			// BasesMax2026Update.BASESMAX2026UPDATE,
			// MEI2026Insert.MEI2026INSERT,
			// Solidarity2026Insert.SOLIDARITY2026INSERT,
			// Holidays2026Insert.HOLIDAYS2026INSERT,
			// FlexibleInsert.FLEXIBLEINSERT,
			// FlexibleUpdate.FLEXIBLEUPDATE,
			// AlterFsMod1902025.ALTER_FS_MODEL_190_2025,
			// FlexibleConceptsFix.FLEXIBLECONCEPTSFIX,
			// FlexibleInsurance.FLEXIBLEINSURANCE,
			// FlexibleInsuranceFix.FLEXIBLEINSURANCEFIX,
			// FlexibleCRAFix.FLEXIBLECRAFIX,
			// FlexibleSumFix.FLEXIBLESUMFIX,
			// FlexibleDTOInsert.FLEXIBLEDTOINSERT,
			// FlexibleChecksInsert.FLEXIBLECHECKSINSERT,
			// InKindConceptsIrpfFix.INKINDCONCEPTSIRPFFIX,
			// FlexibleIrpfFix.FLEXIBLEIRPFFIX,
			// FlexibleChecksDelete.FLEXIBLECHECKSDELETE,
			// MEISolidarity2026RDL162025Fix.MEISOLIDARITYRDL162025FIX,
			// IrpfEuskadi2026Insert.IRPFEUSKADI2026INSERT,
			// MEISolidarity2026RDL162025Undo.MEISOLIDARITYRDL162025UNDO,
			// FlexibleInsurancePremiumsInsert.FLEXIBLEINSURANCEPREMIUMSINSERT,
			// PlusTransporte0001Insert.PLUSTRANSPORTE0001INSERT,
			// SociosCoopBases2025Update.SOCIOSCOOPBASES2025UPDATE,
			// MEITrainingUpdate2026.MEITRAININGUPDATE2026,
			// AlterFsMod347Detail2025Bis.ALTER_FS_MOD_347_DETAIL_2025_BIS,
			// SanitizeDomainRegistries.SANITIZE_SIG_AONSOLUTIONS_ORG,
			// SMI2026Update.SMI2026UPDATE,
			// RealDecreeLaw1782025Art284XXFix.REALDECREELAW1782025ART284XXFIX,
			// Customize.CUSTOMIZE_OPENGES,
			// Customize.CUSTOMIZE_AYUDAT,
			// Customize.CUSTOMIZE_INFOAUTONOMOS,
			// FinObra7PercentFix.FINOBRA7PERCENTFIX,
			// ExtraHoursSwap.EXTRAHOURSSWAP,
			// BasesMin2026Update.BASESMIN2026UPDATE,
			// HomeSolidarityRemoveFix.HOME_SOLIDARITY_REMOVE_FIX,
			// HomeBases2026Update.HOMEBASES2026UPDATE,
			// FellowsBases2026Update.FELLOWSBASES2026UPDATE,
			// TrainningBases2026Update.TRAINNINGBASES2026UPDATE,
			// FellowsPercentages2026Update.FELLOWSPERCENTAGES2026UPDATE,
			// TrainingPercentages2026Update.TRAINNINGPERCENTAGES2026UPDATE,
			// SociosCoopBases2026Update.SOCIOSCOOPBASES2026UPDATE,
			// OrderPJC2972026Art28Update.ORDERPJC2972026ART28UPDATE,
			// TrainingMEI2026Fix.TRAININGMEI2026FIX,
			// AlterFsMod190Detail2026.ALTER_FS_MODEL_190_DETAIL_2026,
			// InsuranceInsert.INSURANCEINSERT,
			// AgriculturalBases2026Update.AGRICULTURALBASES2026UPDATE,
			// Customize.CUSTOMIZE_GESTORIAMUNOZ,
			// MaxEmbargable4DailyFix.MAXEMBARGABLE4DAILYFIX,
			// OneCnaeTableToRuleThemAll.ONECNAETABLETORULETHEMALL,
			// InsertTarifaPrimasDF_1_3_RDL_3_2026.INSERTTARIFAPRIMASDF_1_3_RDL_3_2026,
			// AddColumnCustomerFiscalStatus.ADD_COLUMN_CUSTOMER_FISCAL_STATUS,
			// SalaryTypeUpdate.ALL,
			// BonificEntrenadoresYMonitoresFix.BONIFICENTRENADORESYMONITORESFIX,
			// DropNoJustifiedFix.DROPNOJUSTIFIEDFIX,
			// CgcEMinInsert.CGCEMININSERT,
			// ContractCostExpressionText.CONTRACT_COST_EXPRESSION_TEXT,
			// ContractCostSeaExpressionFix.CONTRACT_COST_SEA_EXPRESSION_FIX,
			// AgriculturalBaseCgcJornadasTotalesFix.AGRICULTURALBASECGCJORNADASTOTALESFIX,
			// ImporteHoraFormacion2026Update.IMPORTEHORAFORMACION2026UPDATE,
			// UserScopeStartDateNotNull.USER_SCOPE_START_DATE_NOT_NULL,
			// ContractForeignKeysAddIfNotExist.CONTRACTFOREIGNKEYSADDIFNOTEXITS,
			// EreFzaExoneradoFix.EREFZAEXONERADOFIX,
			EreFzaExoneradoFixII.EREFZAEXONERADOFIXII,
			ExoneEreFMayComp3707Fix.EXONEEREFMAYCOMP3707FIX,
			FixPreavisoQuote.FIXPREAVISOQUOTE,	
			


			// ----------------------------------------------------------------
			// Warning. Don't delete or comment following instructions
			// MoveAgreementPaymentConcepts.MOVEAGREEMENTPAYMENTCONCEPTS,
			CalendarFixNulls.CALENDAR_FIX_NULLS,
			InsertDefaultScope.INSERT_DEAFULT_SCOPE,
			DeleteUnusedPaymentConcepts.DELETEUNUSEDPAYMENTCONCEPTS,
			// RdocTag.RDOC_TAG,
			// UpdateCategoryTree.UPDATE_CATEGORY_TREE,
			// NordigenCallsLog.NORDIGEN_CALL_LOG_TABLE,
			// AddRegistryBankAgreement.ADD_AGREEMENT_COLUMN
			// AlterBankStatementAddNordigenInternalId.ALTER_BANK_STATEMENT_ADD_NORDIGEN_INTERNAL_ID

			// ----------------------------------------------------------------
			// By now only for `grupo-udapa-aonsolutions-net` database
			// ElaborationSourceSourceIdIndex.ELABORATION_SOURCE_SOURCEID_INDEX,
			// DeliveryCarrierPackingIndex.DELIVERY_CARRIER_PACKING_INDEX,
			// DataResponseSourceResponseDateIndex.DATA_RESPONSE_SOURCE_RESPONSE_DATE_INDEX,	
	
			// ----------------------- ProductBooking alters

			//ProductBookingCreation.PRODUCT_BOOKING_CREATION,
			//AddColumnMarketingActionProduct.ADD_COLUMN_MARKETING_ACTION_PRODUCT,
			//LocationRegistryAlter.LOCATION_REGISTRY_ALTER,
			//TimecontrolLocationAlter.TIMECONTROL_LOCATION_ALTER
			
			// CalendarHolidaysAlter.CALENDAR_HOLIDAYS_ALTER,
			// CalendarPersonalDaysAlter.CALENDAR_PERSONAL_DAYS_ALTER,

			// EnterpriseDataAddAudit.ENTERPRISE_DATA_ADD_AUDIT,
			
			// RecordDataAlterTable.RECORD_DATA_ALTER_TABLE
			// DeleteDomainAppApp7.DELETE_DOMAIN_APP_APP7
			DeleteDomainAppApp10.DELETE_DOMAIN_APP_APP10,
			DeleteDomainAppApp19.DELETE_DOMAIN_APP_APP19,
			MigrateApp23To9.MIGRATE_APP_23_TO_9,

			// InvoiceTrackingAlterTableJson.INVOICE_TRACKING_ALTER_TABLE_JSON,
			
			// UpdateMailAccountProtocolReplayTo.UPDATEMAILACCOUNTPROTOCOLREPLAYTO,
			// UpdateMailAccountType.UPDATEMAILACCOUNTTYPE,
			UserScopeAlterStartEndAuditory.USERSCOPEALTERSTARTENDAUDITORY
	};

	// ------------------------------------------------------------------------

	@SuppressWarnings("static-access")
	public static void main(String[] args) {

		Option hostOption = OptionBuilder.hasArg().isRequired().withLongOpt("host").withArgName("name")
				.withDescription("Connect to host.").create("h");
		Option portOption = OptionBuilder.hasArg().withLongOpt("port").withArgName("name")
				.withDescription("Port number to use for connection, default (3306).").create("P");
		Option userOption = OptionBuilder.hasArg().isRequired().withLongOpt("user").withArgName("name")
				.withDescription("User for login if not current user.").create("u");
		Option passwordOption = OptionBuilder.hasArg().isRequired().withLongOpt("password").withArgName("name")
				.withDescription("Password to use when connecting to server.").create("p");
		Option helpOption = OptionBuilder.withLongOpt("help").withDescription("Display this help and exit.")
				.create("?");

		Options options = new Options();
		options.addOption(helpOption);
		options.addOption(hostOption);
		options.addOption(portOption);
		options.addOption(userOption);
		options.addOption(passwordOption);

		Statement statement = null;
		ResultSet databasesRs = null;
		Connection connection = null;

		CommandLineParser parser = new GnuParser();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			CommandLine commandLine = parser.parse(options, args);

			String host = commandLine.getOptionValue(hostOption.getLongOpt());
			String port = commandLine.getOptionValue(portOption.getLongOpt(), "3306");
			String user = commandLine.getOptionValue(userOption.getLongOpt());
			String password = commandLine.getOptionValue(passwordOption.getLongOpt());

			Properties properties = new Properties();
			properties.setProperty("user", user);
			properties.setProperty("password", password);
			properties.setProperty("useSSL", "true");
			properties.setProperty("serverTimezone", TimeZone.getDefault().getID());
			String url = String.format("jdbc:mysql://%s:%s/information_schema", host, port);
			connection = DriverManager.getConnection(url, properties);

			statement = connection.createStatement();
			databasesRs = statement.executeQuery("SELECT `TABLE_SCHEMA` FROM `TABLES` WHERE `TABLE_NAME`='registry'");
			List<String> databases = new ArrayList<String>();
			while (databasesRs.next())
				databases.add(databasesRs.getString(1));

			for (String database : databases) {

				System.out.println(String.format("Updating database  `%s`", database));

				statement.execute(String.format("USE `%s`", database));

				upgrade(connection);

			}

		} catch (ParseException e) {
			// oops, somthing went wrong
			System.out.println("Error: " + e.getLocalizedMessage());
			new HelpFormatter().printHelp(Up2Date.class.getSimpleName(), options);
		} catch (SQLException e) {
			System.out.println("Error: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("Error: " + e.getLocalizedMessage());
		} finally {
			try {
				if (databasesRs != null)
					databasesRs.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				System.err.println("Oops, something went wrong, " + e.getLocalizedMessage());
			}
		}

	}

	public static void upgrade(Connection connection) {
		for (Update update : UPDATES) {
			try {
				update.upgrade(connection);
				System.out.println("Success.");
			} catch (Throwable t) {
				t.printStackTrace();
				System.out.println("Error: " + t.getLocalizedMessage());
			}
		}
	}

}
