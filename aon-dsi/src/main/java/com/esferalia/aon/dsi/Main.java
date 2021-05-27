package com.esferalia.aon.dsi;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.aonsolutions.core.dbutils.AonSQLException;
import com.esferalia.aon.dsi.DSI2AON.Listener;
import com.esferalia.aon.dsi.util.DBUtils;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.jooq.tables.records.PersonRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;

public class Main {
	
	private static String DEFAULT_HOST = "localhost";

	// ------------------------------------------------------------------------

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws SQLException, AonSQLException {
		// TODO Auto-generated method stub

		//@formatter:off
		Option dsiDirOption = OptionBuilder
			.hasArg()
			.isRequired(true)
			.withArgName("dir")
			.withLongOpt("dir")
			.withDescription("specifies 'DSI' directory.")
			.create();
		
		Option aonHostOption =  OptionBuilder
			.hasArg(true)
			.isRequired(false)
			.withArgName("name")
			.withLongOpt("host")
			.withDescription("Connect to 'AON' host.")
			.create("h");

		Option aonUserOption =  OptionBuilder
			.hasArg()
			.isRequired(true)
			.withArgName("name")
			.withLongOpt("user")
			.withDescription("User for 'AON' login.")
			.create("u");

		Option aonPasswordOption =  OptionBuilder
			.hasArg()
			.isRequired()
			.withArgName("name")
			.withLongOpt("password")
			.withDescription("Password to use when connecting to 'AON' server.")
			.create("p");

		Option aonDatabaseOption =  OptionBuilder
			.hasArg()
			.isRequired()
			.withArgName("name")
			.withLongOpt("database")
			.withDescription("'AON' Database to use.")
			.create();

		Option aonDomainOption =  OptionBuilder
			.hasArg()
			.isRequired(true)
			.withArgName("name")
			.withLongOpt("domain")
			.withDescription("'AON' Domain to use.")
			.create();

		Option aonOwnerOption =  OptionBuilder
			.hasArg()
			.isRequired(true)
			.withArgName("name")
			.withLongOpt("owner")
			.withDescription("'AON' owner to use.")
			.create();
		
		Option dryRunOption =  OptionBuilder
			.isRequired(false)
			.withLongOpt("dry-run")
			.withDescription("perform a trial run with no changes made")
			.create('n');
		
		Option ignoreExistingOption =  OptionBuilder
			.isRequired(false)
			.withLongOpt("ignore-existing")
			.withDescription("skip updating files that already exist on receiver")
			.create();

		Option quietOption =  OptionBuilder
				.isRequired(false)
				.withLongOpt("quiet")
				.withDescription("Quiet output - only show errors")
				.create('q');

		
		Options options = new Options()
				.addOption(dsiDirOption)
				.addOption(aonHostOption)
				.addOption(aonUserOption)
				.addOption(aonDatabaseOption)
				.addOption(aonDomainOption)
				.addOption(aonPasswordOption)
				.addOption(aonOwnerOption)
				.addOption(dryRunOption)
				.addOption(ignoreExistingOption)
				.addOption(quietOption)
				;

		//@formatter:on
		
		// create the parser
		CommandLineParser parser = new GnuParser();
		
		try {
			// parse the command line arguments
			CommandLine cmd = parser.parse(options, args);
			
			String dsiDir = cmd.getOptionValue(dsiDirOption.getLongOpt());
			Connection dsiConn = DBUtils.getDsiConnection(String.format("jdbc:paradox:%s", dsiDir));
			
			String host = cmd.getOptionValue(aonHostOption.getLongOpt(),DEFAULT_HOST);
			String user = cmd.getOptionValue(aonUserOption.getLongOpt());
			String password = cmd.getOptionValue(aonPasswordOption.getLongOpt());
			String database = cmd.getOptionValue(aonDatabaseOption.getLongOpt());

			Connection aonConn = DBUtils.getAonConnection(String.format("jdbc:mysql://%s/%s", host, database), user, password);
			
			String domain = cmd.getOptionValue(aonDomainOption.getLongOpt());
			String owner = cmd.getOptionValue(aonOwnerOption.getLongOpt());
			
			boolean commit =  !cmd.hasOption(dryRunOption.getLongOpt());
			boolean replace =  !cmd.hasOption(ignoreExistingOption.getLongOpt());
			boolean verbose =  !cmd.hasOption(quietOption.getLongOpt());
			
			DSI2AON dsi2aon = new DSI2AON(dsiConn, aonConn);
			
			dsi2aon.setCommit(commit);
			dsi2aon.setReplace(replace);
			if ( verbose )
				dsi2aon.addListener(new Verbose());
			
			dsi2aon.run(domain, owner /*, conditions */);
			
		} catch ( ParseException e){
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("dsi2aon", options);			
		}

	}
	
	// ------------------------------------------------------------------------
	
	private static class Verbose implements Listener {
		
		long start = System.currentTimeMillis();
		
		// --------------------------------------------------------------------
		
		private void log(String format , Object ...args) {
			System.out.printf(format, args);
			System.out.println();
		}

		// --------------------------------------------------------------------

		@Override
		public void onCommited() {
			log("total time %d",  (System.currentTimeMillis()-start)/1000);
		}

		@Override
		public void onRollbacked() {
			// TODO Auto-generated method stub
			log("total time %d s (DRY RUN)" , (System.currentTimeMillis()-start)/1000);
		}

		@Override
		public void onPaymentConceptUpdated(PaymentConceptRecord concept) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPaymentConceptIgnored(PaymentConceptRecord concept) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPaymentConceptInserted(PaymentConceptRecord concept) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onContractIgnored(ContractRecord contract, PersonRecord person) {
			log("IGNORED EMPLOYEE [%s]: %s,  [%tF..%tF]", 
					person.getSocialSecurityNum(), 
					person.getName(), 
					contract.getStartDate(), 
					contract.getEndDate());
		}

		@Override
		public void onContractUpdated(ContractRecord contract, PersonRecord person) {
			log("UPDATED EMPLOYEE [%s]: %s,  [%tF..%tF]", 
					person.getSocialSecurityNum(), 
					person.getName(), 
					contract.getStartDate(), 
					contract.getEndDate());
		}

		@Override
		public void onContractInserted(ContractRecord contract, PersonRecord person) {
			log("INSERTED EMPLOYEE [%s]: %s,  [%tF.."+(contract.getEndDate() != null ? "%tF": "")+"]", 
					person.getSocialSecurityNum(), 
					person.getName(), 
					contract.getStartDate(), 
					contract.getEndDate());
		}

		@Override
		public void onEnterpriseIgnored(RegistryRecord enterprise) {
			log("IGNORED ENTERPRISE [%s]: %s", enterprise.getDocument(), enterprise.getName());
		}

		@Override
		public void onEnterpriseUpdated(RegistryRecord enterprise) {
			log("UPDATED ENTERPRISE [%s]: %s", enterprise.getDocument(), enterprise.getName());
		}

		@Override
		public void onEnterpriseInserted(RegistryRecord enterprise) {
			log("INSERTED ENTERPRISE [%s]: %s", enterprise.getDocument(), enterprise.getName());
		}
		
	}

}
