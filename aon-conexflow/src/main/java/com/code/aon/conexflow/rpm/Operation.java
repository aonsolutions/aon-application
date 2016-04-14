package com.code.aon.conexflow.rpm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.conexflow.ConexFlow;
import com.code.aon.conexflow.ConexFlowConnection;
import com.code.aon.conexflow.ConexFlowConstant;
import com.code.aon.conexflow.ConexFlowPost;
import com.code.aon.conexflow.ConexFlowUtils;
import com.code.aon.conexflow.ConexFlow.Query;
import com.code.aon.conexflow.jooq.DBConsults;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.pool.ConnectionInfo;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.pms.ProjectReservation;

public class Operation {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Operation.class.getName());
	
	private static Domain getDomain(ProjectReservation reservation) {
		Domain domain = new Domain();
		domain.setName(AonUtil.getDomainName());
		domain.setId(reservation.getDomain());
		return domain;
	}
	
	protected static List<String> getHotels(AONContext ctx, Domain domain){
		return DBConsults.getHotels(ctx, domain.getName(), domain.getId());
	}

	protected static List<String> getDomains() throws AonConnectionException{
		// Obtiene todos los dominios de la BD.
		ConnectionInfo connectionInfo = ConnectionInfo
				.getDefaultConnectionInfo();
		Map<String, String> domains = connectionInfo.getDomains();
		
		// Ordena los dominios por orden alfabetico.
		List<String> list = new ArrayList<String>(domains.keySet());
		Collections.sort(list, (String s1, String s2) -> s1.compareTo(s2));
		
		return list;
	}
	
	public static void op(ProjectReservation reservation){
		ConexFlowConnection connection = DBConsults.getConection(getDomain(reservation));
		if (connection.getActive()) {
			Query query;ConexFlow conexFlow;
			if(sale){
				//TODO
			}else if(preauthorization){
				//TODO
			}else if(refund){
				//TODO
			}else if(cancelation){
				//TODO 
			}else if(confirmPreauthorization){
				ConexFlow createToken = DBConsults.getConexFlowLastOperation(getDomain(reservation), "admin", reservation.getProject().getId(), ConexFlowConstant.CREATE_TOKEN_OP);
				ConexFlow preauthorization = DBConsults.getConexFlowLastOperation(getDomain(reservation), "admin", reservation.getProject().getId(),ConexFlowConstant.PREAUTHORIZATION_OP);
				query = ConexFlowUtils.getConexFlowConfirmPreauthorizationQuery(connection.getEmpresa().toString(), connection.getCentro().toString(), 
						connection.getTpv().toString(), reservation.getCustomer().getId().toString(), createToken.getRespuesta().getToken(),
						Double.parseDouble(preauthorization.getRespuesta().getImporte()), Double.parseDouble(preauthorization.getRespuesta().getImporte()), 
						preauthorization.getRespuesta().getCF_ExpirationDate(), preauthorization.getRespuesta().getAutorizacion(), 
						preauthorization.getRespuesta().getFecha(), preauthorization.getRespuesta().getIdOperacion());
				conexFlow = ConexFlowPost.execute(connection,ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP, query, reservation.getId(), getDomain(reservation), false);
				System.out.println(conexFlow.getRespuesta().getResultado() + ": " + conexFlow.getRespuesta().getDesResultado() + ".");
			}else if(redemption){
				//TODO
			}else if(issue){
				//TODO
			}else if(createToken){
				query = ConexFlowUtils.getConexFlowCreateTokenQuery(reservation.getCreditCardNumber()
						, connection.getEmpresa().toString(), connection.getCentro().toString(), connection.getTpv().toString()
						, reservation.getHrCreditCardExpirationMonth() + reservation.getHrCreditCardExpirationYear()
						, reservation.getCustomer().getId().toString());
				conexFlow = ConexFlowPost.execute(connection,ConexFlowConstant.CREATE_TOKEN_OP, query, reservation.getId(), getDomain(reservation), false);
				System.out.println(conexFlow.getRespuesta().getResultado() + ": " + conexFlow.getRespuesta().getDesResultado() + ".");
			}else if(deleteToken){
				//TODO
			}else if(validateCardNumber){
				query = ConexFlowUtils.getConexFlowValidateCardQuery(reservation.getCreditCardNumber(), 
						connection.getEmpresa().toString(), connection.getCentro().toString(), connection.getTpv().toString(),
						reservation.getCustomer().getId().toString());
				conexFlow = ConexFlowPost.execute(connection,ConexFlowConstant.VALIDATE_CARD_OP, query, reservation.getId(), getDomain(reservation), false);
				System.out.println(conexFlow.getRespuesta().getResultado() + ": " + conexFlow.getRespuesta().getDesResultado() + ".");
			}else if(transactionInformation){
				//TODO 
			}
		}
	}
	
	public static void main(String[] args) throws AonConnectionException{
		
		if (!parse(args))
			return;
		
		List<String> domainList = new ArrayList<String>();
		if (domains == null || domains.length == 0 || domains[0].equals("ALL")) 	
			domainList = getDomains();
		else domainList = Arrays.asList(domains);
		
		for(String domainName : domainList){
			AONContext ctx = null;
			try {
				ctx = new AONContext(DBConsults.getConnection(domainName));
				ConexFlowHibernateConnectionProvider.setDomain(domainName);
				Domain domain = DBConsults.getDomain(ctx, domainName);
				View.domain(domainName);
				List<String> hotelList = new ArrayList<String>(); 
				if(hotels == null || hotels.length == 0 || hotels[0].equals("ALL"))
					hotelList = getHotels(ctx, domain);
				else hotelList = Arrays.asList(hotels);
			
				for(String hotel : hotelList){
					if(DBConsults.estaHotel(ctx,domain, hotel)){
						View.hotel(hotel);
						//preauthorization(ctx, domain, hotel);
					}
				}
			}finally{
				if(ctx != null) ctx.close();
			}
		}
	}

	private static String domains[];
	private static String hotels[];
	private static String projects[];
	private static boolean dryRun;
	
	private static boolean sale;
	private static boolean preauthorization;
	private static boolean refund;
	private static boolean cancelation;
	private static boolean confirmPreauthorization;
	private static boolean redemption;
	private static boolean issue;
	private static boolean createToken;
	private static boolean deleteToken;
	private static boolean validateCardNumber;
	private static boolean transactionInformation;

	private static boolean parse(String args[]) {

		CommandLineParser parser = new PosixParser();
		HelpFormatter helpFormatter = new HelpFormatter();

		Options options = new Options();

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withLongOpt("help");
		OptionBuilder.withDescription("print this help.");
		Option helpOption = OptionBuilder.create('h');

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder
				.withDescription("specify a domain list, consisting of domain names separated by commas, e.g., \"aon.esferalia.net,sig.aonsolutions.org\"");
		OptionBuilder.withValueSeparator(',');
		OptionBuilder.withLongOpt("domains");
		Option domainOption = OptionBuilder.create('d');

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder
				.withDescription("specify a hotel list, consisting of hotel names separated by commas, e.g., \"Hotel Barcelo , Hotel Gran Lakua\"");
		OptionBuilder.withValueSeparator(',');
		OptionBuilder.withLongOpt("hotels");
		Option hotelOption = OptionBuilder.create("hotel");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder
				.withDescription("specify a projectId list, consisting of project id (BD) separated by commas, e.g., \"Hotel Barcelo , Hotel Gran Lakua\"");
		OptionBuilder.withValueSeparator(',');
		OptionBuilder.withLongOpt("projects");
		Option projectOption = OptionBuilder.create("project");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder
				.withDescription("perform a trial run with no changes made");
		OptionBuilder.withLongOpt("dry-run");
		Option dryOption = OptionBuilder.create('n');
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("Sale (direct charge).");
		OptionBuilder.withLongOpt("sale");
		Option saleOption = OptionBuilder.create('V');
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("Pre-Authorized Sale.");
		OptionBuilder.withLongOpt("preauthorization");
		Option preauthorizationOption = OptionBuilder.create('P');
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("Refund.");
		OptionBuilder.withLongOpt("refund");
		Option refundOption = OptionBuilder.create('D');
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("Cancelation.");
		OptionBuilder.withLongOpt("cancelation");
		Option cancelationOption = OptionBuilder.create('A');
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("Confirm preauthorization.");
		OptionBuilder.withLongOpt("confirmPreauthorization");
		Option ConfirmPreauthorizationOption = OptionBuilder.create('C');
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("Redemption.");
		OptionBuilder.withLongOpt("redemption");
		Option redemptionOption = OptionBuilder.create('R');
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("Issue.");
		OptionBuilder.withLongOpt("issue");
		Option issueOption = OptionBuilder.create('E');
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("Create Token.");
		OptionBuilder.withLongOpt("createToken");
		Option createTokenOption = OptionBuilder.create('T');
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("Delete Token.");
		OptionBuilder.withLongOpt("deleteToken");
		Option deleteTokenOption = OptionBuilder.create('B');
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("Validate Card Number Check Digit.");
		OptionBuilder.withLongOpt("validateCardNumber");
		Option validateCardNumberOption = OptionBuilder.create('N');
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("Get Transaction Information.");
		OptionBuilder.withLongOpt("transactionInformation");
		Option transactionInformationOption = OptionBuilder.create('S');
		

		options.addOption(helpOption);
		options.addOption(domainOption);
		options.addOption(hotelOption);
		options.addOption(projectOption);
		options.addOption(dryOption);
		
		options.addOption(saleOption);
		options.addOption(preauthorizationOption);
		options.addOption(refundOption);
		options.addOption(cancelationOption);
		options.addOption(ConfirmPreauthorizationOption);
		options.addOption(redemptionOption);
		options.addOption(issueOption);
		options.addOption(createTokenOption);
		options.addOption(deleteTokenOption);
		options.addOption(validateCardNumberOption);
		options.addOption(transactionInformationOption);
		
		
		options.addOption(saleOption);


		try {
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(helpOption.getOpt())) {
				helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
				return false;
			}

			dryRun = line.hasOption(dryOption.getOpt());
			if (dryRun)
				LOGGER.info("DryRun ON: Perform a trial run with no changes made.");
			
			domains = line.getOptionValues(domainOption.getOpt());
			if (domains == null)
				domains = new String[] {};
			
			hotels = line.getOptionValues(hotelOption.getOpt());
			if (hotels == null)
				hotels = new String[] {};
			
			projects = line.getOptionValues(projectOption.getOpt());
			if(projects == null)
				projects = new String[] {};
			
			sale = line.hasOption(saleOption.getOpt());
			if (sale)
				LOGGER.info("OPERATION: SALE");
			
			preauthorization = line.hasOption(preauthorizationOption.getOpt());
			if (preauthorization)
				LOGGER.info("OPERATION: PREAUTHORIZATION");
			
			refund = line.hasOption(refundOption.getOpt());
			if (refund)
				LOGGER.info("OPERATION: REFUND");
			
			cancelation = line.hasOption(cancelationOption.getOpt());
			if (cancelation)
				LOGGER.info("OPERATION: CANCELATION");
			
			confirmPreauthorization = line.hasOption(ConfirmPreauthorizationOption.getOpt());
			if (confirmPreauthorization)
				LOGGER.info("OPERATION: CONFIRM PREAUTHORIZATION");
			
			redemption = line.hasOption(redemptionOption.getOpt());
			if (redemption)
				LOGGER.info("OPERATION: REDEMPTION");
			
			issue = line.hasOption(issueOption.getOpt());
			if (issue)
				LOGGER.info("OPERATION: ISSUE");
			
			createToken = line.hasOption(createTokenOption.getOpt());
			if (createToken)
				LOGGER.info("OPERATION: CREATE TOKEN");
		
			deleteToken = line.hasOption(deleteTokenOption.getOpt());
			if (deleteToken)
				LOGGER.info("OPERATION: DELETE TOKEN");
			
			validateCardNumber = line.hasOption(validateCardNumberOption.getOpt());
			if (validateCardNumber)
				LOGGER.info("OPERATION: VALIDATE CARD NUMBER");
			
			transactionInformation = line.hasOption(transactionInformationOption.getOpt());
			if (transactionInformation)
				LOGGER.info("OPERATION: TRANSACTION OPERATION");
		} catch (ParseException e) {
			System.out.print(e.getMessage());
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
			return false;
		}
		return true;
	}
}
