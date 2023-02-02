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

import net.aonsolutions.db.up2date.finance.InvoiceFiscalAddExpDate;
import net.aonsolutions.db.up2date.fiscal.AlterFsMod184Detail2022;
import net.aonsolutions.db.up2date.payroll.BiEmbargarInsert;
import net.aonsolutions.db.up2date.payroll.IrpfDeductionSplit;
import net.aonsolutions.db.up2date.tgss.BasesMax2023Update;
import net.aonsolutions.db.up2date.tgss.HomeBases2023Update;
import net.aonsolutions.db.up2date.tgss.HomePercentage2023Update;
import net.aonsolutions.db.up2date.tgss.MEIInsert;
import net.aonsolutions.db.up2date.tgss.MEIPECUpdate;
import net.aonsolutions.db.up2date.tgss.SiltraRattachOldDelete;
import net.aonsolutions.db.up2date.tgss.UnemploymentPercentageFix;
public class Up2Date {

    private static Update [] UPDATES  = {
    		//IRPF2018UPDATE,
    		//AGREEMENTUPDATE,
    		//BASES2018UPDATE,
    		//ISSUE1760UPDATE,
    		//REMOVE2HIDEUPDATE,
    		//HOMEBASESUPDATE,
    		//TRAININGBASES2018UPDATE,
    		//HOMEPERCENTAGEUPDATE
    		//FELLOWSBASES2018UPDATE,
    		//SYSTEMPAYMENTREADONLYUPDATE
    		//BASES2018UPDATEII
    		//PRESTITCOMMONDISEASEATLACKINSERT,
    		//GEROA_INSERT
    		//UNEMPLOYMENT
    		//TRAININGBASES2018FIX,
    		//FELLOWBASES2018FIX,
    		//FUNDUPV_UPDATE_II
    		//CRA2018UPDATE
    		//ONACCOUNTAGREEMENTUPDATE
    		//MEJORAINSERT
    		//HOLIDAYS2018INSERT
    		//CRA0057UPDATE,
    		//CRA0058UPDATE
    		//SYSTEM_OFF_INSERT
    		//HOLIDAYS2019INSERT,
    		//BASES2019UPDATE
    		//BASES2019UPDATEII,
    		//FUNDUPV_UPDATE_2019
    		//DEFAULTAGREEMENTINSERT,
    		//CRA0035UPDATE,
    		//ITIMS2019INSERT,
    		//NOTICEDAYSUPDATE,
    		//HOMEPERCENTAGE2023UPDATE,
    		//FELLOWSBASES2019UPDATE,
    		//TRAINNINGBASES2019UPDATE,
    		//FELLOWSBASES2019FIX,
    		//TRAINNINGBASES2019FIXE
    		//ALTER_SALARY_CCC
    		//CGC2019FIX
    		//ALTER_FS_MODEL_200_2018,
    		//PRESTITDESCRIPTIONSUPDATE
    		//EMBARGODESCRIPTIONSUPDATE,
    		//SALARIOBASEDESCRIPTIONSUPDATE,
    		//PlusSalarialInsert.PLUSSALARIALINSERT,
    		//TrainningPercentages2019Update.TRAINNINGPERCENTAGES2019UPDATE,
    		//FellowsPercentages2019Fix.FELLOWSPERCENTAGES2019UPDATE,
    		//AgriculturalITInsert.AGRICULTURALITINSERT,"http://payroll-test.aonsolutions.org:8080/aon-aio/"
    		//Bases2019UpdateIII.BASES2019UPDATEIII,
    		//SalarioBaseReadOnlyUpdate.SALARIOBASEREADONLYUPDATE,
    		//NetoAndBrutoReadOnlyUpdate.NETOBRUTOREADONLYUPDATE,
    		//DefaultAgreementUpdate.DEFAULTAGREEMENTUPDATE,
    		//SalaryHours2019Insert.SALARYHOURS2019INSERT
    		//KaldeviFinanceFix.KALDEVIFINANCEFIX
    		//FundUpvUpdateIII.FUNDUPV_UPDATE_III                                                                                    
    		//Artist2019Insert.ARTIST2019INSERT,
    		//DropDays2019Insert.DROPDAYS2019INSERT
    		//Irpf2020Update.IRPF2020UPDATE
    		//AyudaTUpdate.AYUDATUPDATE
    		//Irpf2020UpdateII.IRPF2020UPDATEII
    		//AlterFsMod184Detail2019.ALTER_FS_MODEL_184_DETAIL_2019
    		//Holidays2020Insert.HOLIDAYS2020INSERT
    		//FundUpvUpdate2020.FUNDUPV_UPDATE_2020,
    		//SMI2020Update.SMI2020UPDATE,
    		//PagaExtraUpdate.PAGAEXTRAUPDATE,
    		//TempPaymentInsert.TEMPPAYMENTINSERT
    		//SalaryHoursUpdate.SALARYHOURSUPDATE
    		//RegistryInsert.REGISTRYINSERT
    		//FundUpvUpdateIV.FUNDUPV_UPDATE_IV,
    		//FundUpvUpdateV.FUNDUPV_UPDATE_V,
    		//IndemnizacionEditableUpdate.INDEMNIZACIONEDITABLEUPDATE,
    		//IndemnizacionDescriptionUpdate.INDEMNIZACIONDESCRIPTIONUPDATE,
    		//EreFzaInsert.EREFZAINSERT,
    		//SMIWarn2020Delete.SMIWARN2020DELETE,
    		//AyudaTWarnDelete.AYUDATWARNDELETE,
    		//EreFzaExoneradoInsert.EREFZAEXONERADOINSERT,
    		//AlterCertifica2BatchDetail4ERE.ALTER_CERTIFICA2_BATCH_DETAIL_ERE,
    		//EreFzaExoneradoUpdate.EREFZAEXONERADOUPDATE
    		//EresUpdate.ERESUPDATE
    		//PrestITDescriptionsUpdateII.PRESTITDESCRIPTIONSUPDATEII,
    		//FellowsPercentages2019Fix.FELLOWSPERCENTAGES2019FIX,
    		//TrainningPercentages2019Fix.TRAINNINGPERCENTAGES2019FIX,
    		//GarantizadoUpdate.GARANTIZADOUPDATE,
    		//DefaultAgreementUpdate2.DEFAULTAGREEMENTUPDATE2,
    		//FixERESalaries.FIXERESALARIES,
    		//FixERESalariesII.FIXERESALARIESII,
    		//SalaryHoursUpdateFactor.SALARYHOURSUPDATEFACTOR,
    		//GarantizadoFix.GARANTIZADOFIX
    		//EresUpdateII.ERESUPDATE,
    		//MejoraInsert.MEJORAINSERT,
    		//DomainName2LowerCase.DOMAINNAME2LOWERCASE
    		//EmbargarFix.EMBARGARFIX
    		//RealDecreeLaw182020Insert.REALDECREELAW182020INSERT
    		//InvoiceDUACreation.INVOICEDUACREATION
    		//RealDecreeLaw182020Update.REALDECREELAW182020UPDATE
    		//DefaultAgreementDeleteWarn.DEFAULTAGREEMENTDELETEWARN
    		//RealDecreeLaw182020UpdateII.REALDECREELAW182020UPDATEII
    		//SnapshotCreation.AUTH_CREATION,
    		//SalaryHoursUpdateMonthDays.SALARYHOURSUPDATEMONTHDAYS,
    		//RealDecreeLaw182020UpdateIII.REALDECREELAW182020UPDATEIII,
    		//AlterFsMod2002019.ALTER_FS_MODEL_200_2019
    		//DomainAppCreation.DOMAIN_APP_CREATION,
    		//UserAppRoleCreation.USER_APP_ROLE_CREATION,
    		//RealDecreeLaw182020UpdateIV.REALDECREELAW182020UPDATEIV
    		//TrainningBases2020Fix.TRAINNINGBASES2020FIX
    		//AuthUpdate.AUTH_UPDATE
    		//AlterAccountEntryDetailConcept.ALTER_ACCOUNT_ENTRY_DETAIL_CONCEPT,
    		//EresUpdateIII.ERESUPDATEIII,
    		//SMI2020UpdateRollback.SMI2020UPDATEROLLBACK
    		//DropTableAccountHelper.DROP_TABLE_ACCOUNT_HELPER
    		//RawdocCreation.RAWDOC_CREATION,
    		//Irpf2021Update.IRPF2021UPDATEII,
    		//LocationCreation.LOCATION_CREATION,
    		//TimeControlCreation.TIMECONTROL_CREATION,
    		//CoordinatesUpdate.COORDINATES_UPDATE,
    		//AlterAgreement4SSNumber.ALTER_AGREEMENT_SSNUM,
    		//Holidays2021Insert.HOLIDAYS2021INSERT,
    		//AdditionalHoursUpdate.ADDITIONALHOURSUPDATE,
    		//AuthDeviceCreation.AUTH_DEVICE_CREATION,
    		//PermissionNotPaidDaysInsert.PERMISSIONNOTPAIDDAYSINSERT,
    		//IfDaysInsert.IFDAYSINSERT
    		//Bases2021Update.BASES2021UPDATE
    		//AuthDeviceUpdate.AUTH_DEVICE_UPDATE,
    		// IntegrityFix.INTEGRITYFIX,
    		//RetirementInsert.RETIREMENTINSERT,
    		//NotificationCreation.NOTIFICATION_CREATION,
    		//NotificationReceiverCreation.NOTIFICATION_RECEIVER_CREATION,
    		//Bases2021UpdateII.BASES2021UPDATEII,
    		//InKindDeductionInsert.INKIND_DEDUCTION_INSERT,
    		//WorkAccidentInsuranceInsert.WORKACCIDENTINSURANCEINSERT
    		//InKindDeductionInsertRETA.INKIND_DEDUCTION_INSERT_RETA,
    		//UpdateInvoiceSource.UPDATE_INVOICE_SOURCE
    		//Bases2021Rollback.BASES2021ROLLBACK,
    		//IndemnizacionFinIRPFFix.INDEMNIZACIONFINIRPFFIX,
    		//IrpfNavarra2021Update.IRPFNAVARRA2021UPDATE,
    		//IrpfEuskadi2021Insert.IRPFEUSKADI2021INSERT
    		//AuthAttachCreation.AUTH_ATTACH_CREATION,
    		//AlterFsMod2002020.ALTER_FS_MODEL_200_2020,
    		//TaskWorkflowCreation.TASK_WORKFLOW_CREATION,
    		//TaskAttachCreation.TASK_ATTACH_CREATION
    		//ContractCostCreation.CONTRACTCOSTCREATION,
    		//CRA0062Insert.CRA0062INSERT
    		//IrpfHomeUpdate.IRPFHOMEUPDATE
    		//TrainningPercentages2019FixII.TRAINNINGPERCENTAGES2021FIXII
    		//BaseCgcMin2019Fix.BASECGCMIN2019FIX
    		//OvertimeCostsFix.OVERTIMECOSTSFIX
    		//ReduccionCgcE02Fix.REDUCCIONCGCE02FIX,
    		//AgriculturalATEPEFix.AGRICULTURALATEPEFIX
    		//AdditionalHoursKiss.ADDITIONALHOURSKISS,
    		//TaskWorkflowUpdate.TASK_WORKFLOW_UPDATE,
    		//ProjectHolderCreation.PROJECT_HOLDER_CREATION,
    		//SMI2021Update.SMI2020UPDATE,
    		//InvoiceFiscalCreation.INVOICE_FISCAL_CREATION,
    		//BofFormYTutoriaInsert.BOFFORMYTUTORIAINSERT,
    		//RemoveDomainAppBankForParent.REMOVE_DOMAIN_APP_BANK_FOR_PARENT,
    		//RemoveDataResponse.REMOVE_DATA_RESPONSE,
    		//DataRequestCreation.DATA_REQUEST_CREATION,
    		//CertificatesUpdate.CERTIFICATESUPDATE,
    		//DataRequestUpdate.DATA_REQUEST_UPDATE,
    		//Bases2021UpdateIII.BASES2021UPDATEIII,
    		//HomeBases2019Close.HOMEBASES2019CLOSE,
    		//HomeBases2021UpdateIII.HOMEBASES2021UPDATEIII,
    		//SalaryHours2021UpdateIII.SALARYHOURS2021UPDATEIII,
    		//TrainingBases2021UpdateIII.TRAININGBASES2021UPDATEIII,
    		//AgriculturalBases2021UpdateIII.AGRICULTURALBASES2021UPDATEIII,
    		//TrainingPercentages2021Update.TRAINNINGPERCENTAGES2021UPDATE,
    		//FellowsPercentages2021Update.FELLOWSPERCENTAGES2021UPDATE,
    		//DataRequestUpdate.DATA_REQUEST_UPDATE,
    		//WorkplaceUpdate.WORKPLACE_UPDATE,
    		//IrpfM190Update.IRPFM190UPDATE,
    		//ContractCleanUpdate.CONTRACTCLEANUPDATE,
    		//TimeControlUpdate.TIMECONTROL_UPDATE,
    		//Holidays2022Insert.HOLIDAYS2022INSERT,
    		//Bases2022Update.BASES2022UPDATE,
    		//AgriculturalBases2022Update.AGRICULTURALBASES2022UPDATE,
    		//AgriculturalPercentages2022Update.AGRICULTURALPERCENTAGES2022UPDATE,
    		//IPREM2022Update.IPREM2022UPDATE,
    		//IrpfEuskadi2022Insert.IRPFEUSKADI2022INSERT,
    		//IrpfQuotasInsert.IRPFQUOTASINSERT,
    		//IrpfQuotasCheck.IRPFQUOTASCHECK,
    		//AlterAgreement4Owner.ALTER_AGREEMENT_OWNER,
    		//AgreementOwnerUpdate.AGREEMENTOWNERUPDATE,
    		//RealDecreeLaw322021Art151Update.REALDECREELAW322021ART151UPDATE,
    		//SMI2022Update.SMI2022UPDATE,
    		//PrestITFactorUpdate.PRESTITFACTORUPDATE,
    		//CertificateDomainFix.CERTIFICATEDOMAINFIX,
    		//SalaryHoursUpdateDaily.SALARYHOURSUPDATEDAILY,
    		//AgreementClean.AGREEMENTCLEAN,
    		//CertificatesClean.CERTIFICATESCLEAN,
    		//Bases2022UpdateII.BASES2022UPDATEIII,
    		//HomeBases2022Update.HOMEBASES2022UPDATE,
    		//TrainningBases2022Update.TRAINNINGBASES2022UPDATE,
    		//TrainingPercentages2022Update.TRAINNINGPERCENTAGES2022UPDATE,
    		//FellowsPercentages2022Update.FELLOWSPERCENTAGES2022UPDATE,
    		//Artist2022Update.ARTIST2022UPDATE,
    		//RefreshMod303Result.REFRESH_MOD303_RESULT,
    		//Artist2022Fix.ARTIST2022FIX,
    		//Bases2022Fix.BASES2022FIX,
    		//SalaryHours2022UpdateDaily.SALARYHOURS2022UPDATEDAILY,
    		//UdapaSalesFix.UDAPA_SALES_FIX,
    		//SalaryHours2022Fix.SALARYHOURS2022FIX,
    		//BaseCgpMin2022Fix.BASECGPMIN2022FIX,
    		//AgriculturalBases2022UpdateII.AGRICULTURALBASES2021UPDATEIII,
    		//TaskEvent2TaskWorkfow.TASK_COMMENT_2_TASK_WORKFLOW,
    		//TaskComment2TaskWorkfow.TASK_EVEMT_2_TASK_WORKFLOW,
    		//UpdateFinanceRname.UPDATE_FINANCE_RNAME,
    		//BaseCgpMin2022FixIII.BASECGPMIN2022FIX,
    		//DomainName2LowerCase.DOMAINNAME2LOWERCASE,
    		//AlterSalaryEmbargo.ALTERSALARYEMBARGO,
    		//Holidays2022Update.HOLIDAYS2022UPDATE,
    		//Art1512022Update.ART1512022UPDATE,
    		//ContractAttachUpdate.CONTRACTATTACHUPDATE,
    		//AlterSalaryDeduction.ALTERSALARYDEDUCTION,
    		//TrainingPercentages2022UpdateII.TRAINNINGPERCENTAGES2022UPDATEII,
    		//TaskAddEvaluation.TASK_ADD_EVALUATION,
    		//AlterContractBonus.ALTERCONTRACTBONUS,
    		//SalaryHoursFix.SALARYHOURSFIX,
    		//ArtistPartialFactorFix.ARTISTPARTIALFACTORFIX,
    		//AgriculturalBases2022UpdateIII.AGRICULTURALBASES2021UPDATEIII,
    		//CnoUpdate.CNOUPDATE,
    		//AssimilatedInsert.ASSIMILATEDINSERT,
    		//Bases2022FixHourly.BASES2022FIXHOURLY,
    		//AssimilatedInsert.ASSIMILATEDINSERT,
    		//Bases2022FixHourly.BASES2022FIXHOURLY,
    		//InsertIAE863.INSERT_IAE_863,
    		//SalaryHoursFixII.SALARYHOURSFIXII,
    		//AlterAgreement4Log.ALTERAGREEMENTLOG,
    		//RealDecreeLaw322021Art151Fix.REALDECREELAW322021ART151FIX,
    		//FellowsITIMS2022UpdateII.FELLOWSITIMS2022UPDATEII,
	   	//JacalInvoiceUpdateFix.JACA_INVOICE_UPDATE,
    		//AgriculturalMonthlyQuote2022Fix.AGRICULTURALMONTHLYQUOTE2022FIX,
    		//ContractTransfromUnify.CONTRACTTRANSFORMUNIFY,
    		//AlterFsMod200Audit.ALTER_FS_MOD200_AUDIT,
    		//TaskSourceUpdate.TASK_SOURCE_UPDATE,
    		//DocumentalUpdateFix.DOCUMENTAL_UPDATE_FIX,
    		//AlterFsMod200FsModel.ALTER_FS_MOD200_FS_MODEL,
    		//ElaborationDetailUpdate.ELABORATION_DETAIL_UPDATE,
    		//IrpfEuskadiSeptember2022Insert.IRPFEUSKADISEPTEMBER2022INSERT,
    		//AlterSalaryAddIndexSocialSecurityNumber.ALTER_SALARY_ADD_INDEX_SOCIALSECURITYNUMBER,
    		//MaxEmbargableFix.MAXEMBARGABLEFIX,
    		//ContractExtraCreation.CONTRACTEXTRACREATION,
    		//IrpfInKindSystemInsert.IRPFINKINDSYSTEMINSERT,
    		//TrainingBases2022Fix.TRAININGBASES2022FIX,
    		//TrainingBases2022FixII.TRAININGBASES2022FIXII,
    		//UdapaPfondoUpdateFix.UDAPA_PFONDO_UPDATE_FIX,
    		//SevenConsultingDomainNameUpdate.SEVEN_CONSULTING_DOMAIN_NAME_UPDATE,
    		//InvoiceAddAnnulledColumn.INVOICE_ADD_ANNULLED_COLUMN,
    		//HomeQuote2022Update.HOMEQUOTE2022UPDATE,
    		//ContractDocCreate.CONTRACTDOCCREATE
    		//AlterSalaryBonusConcept.ALTERSALARYBONUSCONCEPT,
    		//HomeBonus2012Update.HOMEBONUS2012UPDATE,
    		//DefaultPaymentConcepts.DEFAULTPAYMENTCONCEPTS,
    		//AgriculturalITRemove.AGRICULTURALITREMOVE,
    		//AlterBankStatementReference2.ALTER_BANK_STATEMENT_REFERENCE_2,
    		//AlterContractBonusExpression.ALTER_CONTRACT_BONUS_EXPRESSION,
    		//AlterContractBonusDescription.ALTER_CONTRACT_BONUS_DESCRIPTION,
    		//AlterContractPaymentDescription.ALTERCONTRACTPAYMENTDESCRIPTION,
    		//FixPreaviso.FIXPREAVISO,
    		//RefreshMod390HFResult.REFRESH_MOD390_RESULT,
    		//AlterRbankAddRequisition.ALTER_RBANK_ADD_REQUISITION,
    		//AlterRbankAddSepaMandateRef.ALTER_RBANK_ADD_SEPA_MANDATE_REF,
    		//Holidays2023Insert.HOLIDAYS2023INSERT,
    		//AgreementPaymentsConceptFix.AGREEMENTPAYMENTSCONCEPTFIX,
    		//DeathOfEmployeeInsert.DEATHOFEMPLOYEEINSERT,
    		//AlterAgreementPaymentDescription.ALTERAGREEMENTPAYMENTDESCRIPTION,
    		//UpdateInvoiceRegistryDocument.UPDATE_INVOICE_REGISTRY_DOCUMENT,
    		//AlterAlcatrazFinance.ALTER_ALCATRAZ_FINANCE,
    		//IrpfEuskadi2023Insert.IRPFEUSKADI2023INSERT,
	    	//BasesMax2023Update.BASESMAX2023UPDATE,
	    	//MEIInsert.MEIINSERT,
    		//AlterFsMod190Detail2022.ALTER_FS_MODEL_190_DETAIL_2022,
    		//HomeBases2023Update.HOMEBASES2023UPDATE,
	    	//MEIPECUpdate.MEIPECUPDATE,
    		//AlterFsMod190Detail2022.ALTER_FS_MODEL_190_DETAIL_2022,
    		//InvoiceFiscalAddExpDate.INVOICE_FISCAL_ADD_EXP_DATE, 		
    		//AlterFsMod184Detail2022.ALTER_FS_MODEL_184_DETAIL_2022,
	    	//BiEmbargarInsert.BIEMBARGARINSERT,
    		//AlterFsMod184Detail2022.ALTER_FS_MODEL_184_DETAIL_2022,
	    	//IrpfDeductionSplit.IRPFDEDUCTIONSPLIT,
	    	HomePercentage2023Update.HOMEPERCENTAGE2023UPDATE,
	    	UnemploymentPercentageFix.UNEMPLOYMENTPERCENTAGEFIX,
	    	
    		//Important, not remove
    		SiltraRattachOldDelete.SILTRARATTACHOLDDELETE
    		
    };

    
	// ------------------------------------------------------------------------


    @SuppressWarnings("static-access")
    public static void main(String[] args) {


		 Option hostOption = OptionBuilder
		     .hasArg()
			 .isRequired()
		     .withLongOpt("host")
		     .withArgName("name")
		     .withDescription("Connect to host.")
		     .create("h");
		 Option portOption = OptionBuilder
		     .hasArg()
		     .withLongOpt("port")
		     .withArgName("name")
		     .withDescription("Port number to use for connection, default (3306).")
		     .create("P");
		 Option userOption = OptionBuilder
		     .hasArg()
			 .isRequired()
		     .withLongOpt("user")
		     .withArgName("name")
		     .withDescription("User for login if not current user.")
		     .create("u");
		 Option passwordOption = OptionBuilder
		     .hasArg()
			 .isRequired()
		     .withLongOpt("password")
		     .withArgName("name")
		     .withDescription("Password to use when connecting to server.")
		     .create("p");
		 Option helpOption = OptionBuilder
				 .withLongOpt("help")
		         .withDescription("Display this help and exit.")
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
			Class.forName("com.mysql.jdbc.Driver");
			CommandLine commandLine = parser.parse(options, args);

			String host = commandLine.getOptionValue(hostOption.getLongOpt());
			String port = commandLine.getOptionValue(portOption.getLongOpt(), "3306");
			String user = commandLine.getOptionValue(userOption.getLongOpt());
			String password = commandLine.getOptionValue(passwordOption.getLongOpt());

			Properties properties = new Properties();
			properties.setProperty("user", user);
			properties.setProperty("password", password);
			properties.setProperty("useSSL", "false");
			properties.setProperty("serverTimezone", TimeZone.getDefault().getID());
			String url = String.format("jdbc:mysql://%s:%s/information_schema", host, port);
			connection = DriverManager.getConnection(url, properties);

			statement = connection.createStatement();
			databasesRs = statement.executeQuery("SELECT `TABLE_SCHEMA` FROM `TABLES` WHERE `TABLE_NAME`='registry'");
			List<String> databases = new ArrayList<String>();
			while ( databasesRs.next() )
				databases.add(databasesRs.getString(1));

			for ( String database : databases ) {

				System.out.println(String.format("Updating database  `%s`" ,database  ));

				statement.executeQuery(String.format("USE `%s`", database));

				for ( Update update : UPDATES ) {
					try {
						update.upgrade(connection);
						System.out.println("Success." );
					} catch ( Throwable t) {
						System.out.println("Error: " + t.getLocalizedMessage());
					}
				}


			}

		} catch (ParseException e) {
			// oops, somthing went wrong
			System.out.println("Error: " + e.getLocalizedMessage());
			new HelpFormatter().printHelp(Up2Date.class.getSimpleName(), options);
		} catch (SQLException e) {
			System.out.println("Error: " + e.getLocalizedMessage() );
		} catch (ClassNotFoundException e) {
			System.out.println("Error: " + e.getLocalizedMessage());
		} finally {
			try {
				if ( databasesRs != null )
					databasesRs.close();
				if ( statement != null )
					statement.close();
				if ( connection != null )
					connection.close();
			} catch ( SQLException e ) {
				System.err.println("Oops, something went wrong, " + e.getLocalizedMessage());
			}
		}

	}

}
