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

import com.code.aon.common.ManagerBeanException;
import com.code.aon.conexflow.ConexFlow;
import com.code.aon.conexflow.ConexFlow.Query;
import com.code.aon.conexflow.ConexFlowConnection;
import com.code.aon.conexflow.ConexFlowConstant;
import com.code.aon.conexflow.ConexFlowPost;
import com.code.aon.conexflow.ConexFlowUtils;
import com.code.aon.conexflow.jooq.DBConsults;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.pool.ConnectionInfo;
import com.esferalia.aon.jooq.tables.records.ProjectReservationRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class Preauthorization {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Preauthorization.class.getName());
	
	
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
	
	protected static List<String> getHotels(AONContext ctx, Domain domain){
		return DBConsults.getHotels(ctx, domain.getName(), domain.getId());
	}
	
	protected static void preauthorization(AONContext ctx, Domain domain, String hotel){
		Integer hotelId = DBConsults.getHotelId(ctx, domain, hotel);
		List<ProjectReservationRecord> list = DBConsults.getProjectReservationList(ctx, domain, hotelId);
		ConexFlowConnection connection = DBConsults.getConection(domain);
		for(ProjectReservationRecord pr : list){
			Integer customer = pr.getHotelReservation();
			if(pr.getBookingHolder().equals(BookingHolder.AGENCY.ordinal())){
				pr.getAgency();
			}
			else if(pr.getBookingHolder().equals(BookingHolder.COMPANY.ordinal())){
				pr.getCompany();
			}
			
			ConexFlow cf2 = DBConsults.getConexFlowLastOperation(domain, "admin", pr.getProject(), ConexFlowConstant.PREAUTHORIZATION_OP);
			if(cf2 == null || !cf2.getRespuesta().getResultado().equals("000")){
				
				ConexFlow cf = DBConsults.getConexFlowLastOperation(domain, "admin", pr.getProject(), ConexFlowConstant.CREATE_TOKEN_OP);
				if(cf != null && cf.getRespuesta().getResultado().equals("000"))	{
					String errorMsg = "";
					ReservationUtils reservationUtils = new ReservationUtils(domain.getId());
					Double amount = 0.0;
					try {
						amount = reservationUtils.obtainCancellationPenaltyPrice(DBConsults.newProjectReservation(ctx,pr));
					} catch (ManagerBeanException e) {
						e.printStackTrace();
					}
					if(!dryRun){
						String token = cf.getRespuesta().getToken();
						
						
						Query query = ConexFlowUtils.getConexFlowPreauthorizationPaymentQuery(connection.getEmpresa().toString()
							, connection.getCentro().toString(), connection.getTpv().toString()
							, customer.toString(), token, amount);
						ConexFlow conexFlow = ConexFlowPost.execute(connection,ConexFlowConstant.PREAUTHORIZATION_OP, query, pr.getProject(), domain, false);
						if (!conexFlow.getRespuesta().getResultado().equals("000")) {
							errorMsg = "Error " + conexFlow.getRespuesta().getResultado() + ": " + conexFlow.getRespuesta().getDesResultado() + ".";
						}
						else errorMsg = "PREAUTHORIZATION OK";
					}
					String projectName = DBConsults.getProjectName(ctx,pr.getProject());
					View.preauthorized(projectName, pr, errorMsg, amount);
				}
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
						preauthorization(ctx, domain, hotel);
					}
				}
			}finally{
				if(ctx != null) ctx.close();
			}
		}
	}

	private static String domains[];
	private static String hotels[];
	private static boolean dryRun;
	
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
		OptionBuilder.hasArg(false);
		OptionBuilder
				.withDescription("perform a trial run with no changes made");
		OptionBuilder.withLongOpt("dry-run");
		Option dryOption = OptionBuilder.create('n');

		options.addOption(helpOption);
		options.addOption(domainOption);
		options.addOption(hotelOption);
		options.addOption(dryOption);


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
			
		} catch (ParseException e) {
			System.out.print(e.getMessage());
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
			return false;
		}
		return true;
	}
}
