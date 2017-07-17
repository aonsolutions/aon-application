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

import com.code.aon.conexflow.jooq.DBConsults;
import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.ConnectionInfo;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;

public class ProjectReservationList {

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
	
	protected static void list(AONContext ctx, Domain domain, String hotel){
		//TODO 
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
				Domain domain = DBConsults.getDomain(ctx, domainName);
				View.domain(domainName);
				List<String> hotelList = new ArrayList<String>(); 
				if(hotels == null || hotels.length == 0 || hotels[0].equals("ALL"))
					hotelList = getHotels(ctx, domain);
				else hotelList = Arrays.asList(hotels);
			
				for(String hotel : hotelList){
					if(DBConsults.estaHotel(ctx,domain, hotel)){
						View.hotel(hotel);
						list(ctx, domain, hotel);
					}
				}
			}finally{
				if(ctx != null) ctx.close();
			}
		}
	}

	private static String domains[];
	private static String hotels[];
	private static boolean error;
	
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
				.withDescription("");
		OptionBuilder.withLongOpt("error");
		Option errorOption = OptionBuilder.create("error");
		
		options.addOption(helpOption);
		options.addOption(domainOption);
		options.addOption(hotelOption);
		options.addOption(errorOption);


		try {
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(helpOption.getOpt())) {
				helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
				return false;
			}

			error = line.hasOption(errorOption.getOpt());
			if(error){}
			
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
