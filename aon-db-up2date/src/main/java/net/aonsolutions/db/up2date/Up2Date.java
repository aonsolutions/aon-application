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

import net.aonsolutions.db.up2date.payroll.AdditionalHoursUpdate;
import net.aonsolutions.db.up2date.payroll.AlterAgreement4SSNumber;
import net.aonsolutions.db.up2date.payroll.Holidays2021Insert;
import net.aonsolutions.db.up2date.security.AuthDeviceCreation;
import net.aonsolutions.db.up2date.timecontrol.CoordinatesUpdate;

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
    		//HOMEPERCENTAGE2019UPDATE,
    		//FELLOWSBASES2019UPDATE,
    		//TRAINNINGBASES2019UPDATE,
    		//FELLOWSBASES2019FIX,
    		//TRAINNINGBASES2019FIX
    		//ALTER_SALARY_CCC
    		//CGC2019FIX
    		//ALTER_FS_MODEL_200_2018,
    		//PRESTITDESCRIPTIONSUPDATE
    		//EMBARGODESCRIPTIONSUPDATE,
    		//SALARIOBASEDESCRIPTIONSUPDATE,
    		//PlusSalarialInsert.PLUSSALARIALINSERT,
    		//TrainningPercentages2019Update.TRAINNINGPERCENTAGES2019UPDATE,
    		//FellowsPercentages2019Fix.FELLOWSPERCENTAGES2019UPDATE,
    		//AgriculturalITInsert.AGRICULTURALITINSERT,
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
    		//MejoraUpdate.MEJORAUPDATE
    		//EmbargarFix.EMBARGARFIX
    		//RealDecreeLaw182020Insert.REALDECREELAW182020INSERT
    		//InvoiceDUACreation.INVOICEDUACREATION
    		//RealDecreeLaw182020Update.REALDECREELAW182020UPDATE
    		//DefaultAgreementDeleteWarn.DEFAULTAGREEMENTDELETEWARN
    		//RealDecreeLaw182020UpdateII.REALDECREELAW182020UPDATEII
    		//AuthCreation.AUTH_CREATION,
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
    		AlterAgreement4SSNumber.ALTER_AGREEMENT_SSNUM,
    		Holidays2021Insert.HOLIDAYS2021INSERT,
    		AdditionalHoursUpdate.ADDITIONALHOURSUPDATE,
    		AuthDeviceCreation.AUTH_DEVICE_CREATION
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

				System.out.print(String.format("Updating database  `%s`" ,database  ));

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
			System.out.println("Error: " + e.getLocalizedMessage());
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
