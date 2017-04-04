package com.code.aon.conexflow.rpm;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.code.aon.conexflow.ConexFlow;
import com.code.aon.conexflow.ConexFlow.Query;
import com.code.aon.conexflow.ConexFlowConnection;
import com.code.aon.conexflow.ConexFlowConstant;
import com.code.aon.conexflow.ConexFlowPost;
import com.code.aon.conexflow.ConexFlowStatus;
import com.code.aon.conexflow.ConexFlowUtils;
import com.code.aon.conexflow.jooq.DBConsults;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.pool.ConnectionInfo;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.project.ProjectReservation;
import com.esferalia.aon.occam.api.model.project.ReservationStatus;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Preauthorization {
	
	private static final Logger LOGGER  = Logger.getLogger(DBConsults.class.getName());

	private static final String SERVER = "https://test.conexflow.com/CF_WEB_PLAYASOL/WebPlugin/xtn.asp";
	private static final String SERVER_ACK = "https://test.conexflow.com/CF_WEB_PLAYASOL/WebPlugin/ack.asp";
	private static final String CF_USER = "0000028302830283";
	private static final String KEY_A = "193D59719B1B2662";
	private static final String KEY_B = "A8DB8A2EF1223B4E";
	
	private static ConexFlowConnection getTestConnection() {
		return new ConexFlowConnection()
				.setActive(true)
				.setServer(SERVER)
				.setServerAck(SERVER_ACK)
				.setCfUser(CF_USER)
				.setKeyA(KEY_A)
				.setKeyB(KEY_B);
	}
	
	protected static List<String> getDomains() throws AonConnectionException{
		// Obtiene todos los dominios de la BD.
		ConnectionInfo connectionInfo = ConnectionInfo.getDefaultConnectionInfo();
		Map<String, String> domains = connectionInfo.getDomains();
		
		// Ordena los dominios por orden alfabetico.
		List<String> list = new LinkedList<String>(domains.keySet());
		Collections.sort(list, (String s1, String s2) -> s1.compareTo(s2));
		
		return list;
	}
	
	public static void preauthorization001(Domain domain, String login){
		final int[] cont = {0};
		Date currentDate = new Date(new java.util.Date().getTime());
		Stream<ProjectReservation> stream = DBConsults.getProjectReservationStream(domain, login,
			f -> f.getStartDateProperty().ge(currentDate).and(f.getTokenProperty().isNotNull())); 
		stream.forEach(r -> {
			if(number == -1 || cont[0] <= number){
				Domain d = AON.getDomain(domain.getName(), r.getDomain().getId(), login);
				/*
				ConexFlow cf = DBConsults.getConexFlowLastStatusX(d, login, r.getProject(), r.getToken(), ConexFlowStatus.PREAUTHORIZATION_CHECK);
				ConexFlow pcFail = DBConsults.getConexFlowLastStatusX(d, login, r.getProject(), r.getToken(), ConexFlowStatus.PREAUTHORIZATION_CHECK_FAIL);
				ConexFlow sale = DBConsults.getConexFlowLastStatusX(d, login, r.getProject(), r.getToken(), ConexFlowStatus.SALE);
				ConexFlow sFail = DBConsults.getConexFlowLastStatusX(d, login, r.getProject(), r.getToken(), ConexFlowStatus.SALE_FAIL);
				*/
				String[] descriptions = {
					DBConsults.getStatusDescription(r.getToken(), ConexFlowStatus.PREAUTHORIZATION_CHECK),
					DBConsults.getStatusDescription(r.getToken(), ConexFlowStatus.PREAUTHORIZATION_CHECK_FAIL),
					DBConsults.getStatusDescription(r.getToken(), ConexFlowStatus.SALE),
					DBConsults.getStatusDescription(r.getToken(), ConexFlowStatus.SALE_FAIL)
				};
				if(!DBConsults.hasConexFlow(d, login, r.getProject(), descriptions)){
				//if(cf == null && pcFail == null && sale == null && sFail == null){
					if(!dryRun){
						ConexFlowConnection connection = DBConsults.getConection(d);
						Query query = ConexFlowUtils.getConexFlowPreauthorizationPaymentQuery(connection
								, r.getHotelReservation().toString(), r.getToken(), 0.01);
					
						ConexFlow conexFlow = ConexFlowPost.execute(connection,ConexFlowConstant.PREAUTHORIZATION_OP, query);
						Boolean ok = conexFlow.getRespuesta().getResultado().equals("000");
						conexFlow.setStatus(ok ? ConexFlowStatus.PREAUTHORIZATION_CHECK : ConexFlowStatus.PREAUTHORIZATION_CHECK_FAIL);
						String description = "CONEXFLOW_(" + r.getToken().substring(r.getToken().length()-5) + ")_"
								+ conexFlow.getStatus().getName() + "#" + conexFlow.getRespuesta().getImporte();
						DBConsults.insertConexFlow(d, login, conexFlow, r.getProject(), description);
						
						String msg = "";
						if (!ok){
							msg = "Error " + conexFlow.getRespuesta().getResultado() + ": " + conexFlow.getRespuesta().getDesResultado() + ".";
						} else {
							msg = "PREAUTHORIZATION OK";
					
							// CANCELAR PREAUTHORIZACION 001
							Query cancelQuery = ConexFlowUtils.getConexFlowCancelationQuery(connection
									, conexFlow.getRespuesta().getOperacion(), Double.parseDouble(conexFlow.getRespuesta().getImporte())
									, Double.parseDouble(conexFlow.getRespuesta().getImporte()) 
									, conexFlow.getRespuesta().getAutorizacion(), r.getHotelReservation().toString()
									, conexFlow.getRespuesta().getIdOperacion(), conexFlow.getRespuesta().getFecha());
							ConexFlowPost.execute(connection,ConexFlowConstant.CANCELATION_OP, cancelQuery);
						}
						String projectName = DBConsults.getProjectName(d, login, r.getProject());
						View.preauthorized(projectName, r.getProject(), msg, 0.01);
					} else {
						ConexFlowConnection connection = getTestConnection();
						Query query = ConexFlowUtils.getConexFlowPreauthorizationPaymentQuery(connection
								, r.getHotelReservation().toString(), r.getToken(), 0.01);
						
						ConexFlow conexFlow = ConexFlowPost.execute(connection,ConexFlowConstant.PREAUTHORIZATION_OP, query);
						Boolean ok = conexFlow.getRespuesta().getResultado().equals("000");
						String msg = "";
						if (!ok){
							msg = "Error " + conexFlow.getRespuesta().getResultado() + ": " + conexFlow.getRespuesta().getDesResultado() + ".";
						} else {
							msg = "PREAUTHORIZATION OK";
						}
						String projectName = DBConsults.getProjectName(d, login, r.getProject());
						View.preauthorized(projectName, r.getProject(), msg, 0.01);
					}
					cont[0]++;
				}
			}
		});
	}
	
	public static void preauthorizationPenalty(Domain domain,  String login){
		final int[] cont = {0};
		DBConsults.getProjectReservationStream(domain, login, f ->
				f.getPenaltyDateProperty().le(new Timestamp(new java.util.Date().getTime()))
				.and(f.getTokenProperty().isNotNull()).and(f.getStatusProperty().eq(ReservationStatus.ACTIVE.value())
				.or(f.getStatusProperty().eq(ReservationStatus.BLOCKED.value()))))
		.forEach(r ->{
			if(number == -1 || cont[0] <= number){
				Domain d = AON.getDomain(domain.getName(), r.getDomain().getId(), login);		
				ConexFlow p = DBConsults.getConexFlowLastStatusX(d, login, r.getProject(), r.getToken(), ConexFlowStatus.PREAUTHORIZATION);
				ConexFlow pFail = DBConsults.getConexFlowLastStatusX(d, login, r.getProject(), r.getToken(), ConexFlowStatus.CONFIRM_PREAUTHORIZATION_FAIL);
				ConexFlow pcFail = DBConsults.getConexFlowLastStatusX(d, login, r.getProject(), r.getToken(), ConexFlowStatus.PREAUTHORIZATION_CHECK_FAIL);
				ConexFlow sale = DBConsults.getConexFlowLastStatusX(d, login, r.getProject(), r.getToken(), ConexFlowStatus.SALE);
				ConexFlow sFail = DBConsults.getConexFlowLastStatusX(d, login, r.getProject(), r.getToken(), ConexFlowStatus.SALE_FAIL);

				java.util.Date date2 = AonDateUtils.addDays(new java.util.Date(), -7);
				if((p == null && pFail == null && pcFail == null && sale == null && sFail == null) ||  (p != null && p.getDate().compareTo(date2) <= 1  && 
						Double.parseDouble(p.getRespuesta().getImporte()) < r.getPenaltyAmount())){
					if(!dryRun){
						ConexFlowConnection connection = DBConsults.getConection(d);
						
						if(p != null){
							// CANCELAR PREAUTHORIZACION 
							Query cancelQuery = ConexFlowUtils.getConexFlowCancelationQuery(connection
									, p.getRespuesta().getOperacion(), Double.parseDouble(p.getRespuesta().getImporte())
									, Double.parseDouble(p.getRespuesta().getImporte()) 
									, p.getRespuesta().getAutorizacion(), r.getHotelReservation().toString()
									, p.getRespuesta().getIdOperacion(), p.getRespuesta().getFecha());
							ConexFlow cancelConexFlow = ConexFlowPost.execute(connection,ConexFlowConstant.CANCELATION_OP, cancelQuery);
							
							Boolean ok = cancelConexFlow.getRespuesta().getResultado().equals("000");
							cancelConexFlow.setStatus(ok ? ConexFlowStatus.CANCEL : ConexFlowStatus.CANCEL_FAIL);
							String description = "CONEXFLOW_(" + r.getToken().substring(r.getToken().length()-5) + ")_"
									+ cancelConexFlow.getStatus().getName() + "#" + cancelConexFlow.getRespuesta().getImporte();
							DBConsults.insertConexFlow(d, login, cancelConexFlow, r.getProject(), description);
				
							String description2 = "CONEXFLOW_(" + r.getToken().substring(r.getToken().length()-5) + ")_"
									+ p.getStatus().cancel().getName() + "#" + p.getRespuesta().getImporte();			
							DBConsults.updateConexFlowDescription(d, login, p.getId(), description2);
						}
					
						Query query = ConexFlowUtils.getConexFlowPreauthorizationPaymentQuery(connection
								, r.getHotelReservation().toString(), r.getToken(), r.getPenaltyAmount());
						ConexFlow conexFlow = ConexFlowPost.execute(connection,ConexFlowConstant.PREAUTHORIZATION_OP, query);
						Boolean ok = conexFlow.getRespuesta().getResultado().equals("000");
						conexFlow.setStatus(ok ? ConexFlowStatus.PREAUTHORIZATION : ConexFlowStatus.PREAUTHORIZATION_FAIL);
						String description = "CONEXFLOW_(" + r.getToken().substring(r.getToken().length()-5) + ")_"
								+ conexFlow.getStatus().getName() + "#" + conexFlow.getRespuesta().getImporte();
						DBConsults.insertConexFlow(d, login, conexFlow, r.getProject(), description);
					
						String msg = "";
						if(!ok){
							msg = "Error " + conexFlow.getRespuesta().getResultado() + ": " + conexFlow.getRespuesta().getDesResultado() + ".";
						} else msg = "PREAUTHORIZATION OK";
						String projectName = DBConsults.getProjectName(d, login, r.getProject());
						View.preauthorized(projectName, r.getProject(), msg, r.getPenaltyAmount());
					} else {
						String msg = "PREAUTHORIZATION OK";
						String projectName = DBConsults.getProjectName(d, login, r.getProject());
						View.preauthorized(projectName, r.getProject(), msg, r.getPenaltyAmount());
					}
					cont[0]++;
				}
			}
		});
	}
	
	public static void preauthorizationPenaltyDays(Domain domain, String login){
		final int[] cont = {0};
		Calendar calendar = Calendar.getInstance();
		Date currentDate = new Date(calendar.getTime().getTime());
		calendar.add(Calendar.DAY_OF_YEAR, days); 
		Date date = new Date(calendar.getTime().getTime());
		Stream<ProjectReservation> stream = DBConsults.getProjectReservationStream(domain, login,
			f -> f.getStartDateProperty().ge(currentDate).and(f.getStartDateProperty().le(date))
			.and(f.getTokenProperty().isNotNull()).and(f.getStatusProperty().eq(ReservationStatus.ACTIVE.value())
					.or(f.getStatusProperty().eq(ReservationStatus.BLOCKED.value())))); 
		stream.forEach(r -> {
			if(number == -1 || cont[0] <= number){
				Domain d = AON.getDomain(domain.getName(), r.getDomain().getId(), login);		
				ConexFlow cf = DBConsults.getConexFlowLastStatusX(d, login, r.getProject(), r.getToken(), ConexFlowStatus.PREAUTHORIZATION);
				ConexFlow pFail = DBConsults.getConexFlowLastStatusX(d, login, r.getProject(), r.getToken(), ConexFlowStatus.CONFIRM_PREAUTHORIZATION_FAIL);
				ConexFlow pcFail = DBConsults.getConexFlowLastStatusX(d, login, r.getProject(), r.getToken(), ConexFlowStatus.PREAUTHORIZATION_CHECK_FAIL);
				ConexFlow sale = DBConsults.getConexFlowLastStatusX(d, login, r.getProject(), r.getToken(), ConexFlowStatus.SALE);
				ConexFlow sFail = DBConsults.getConexFlowLastStatusX(d, login, r.getProject(), r.getToken(), ConexFlowStatus.SALE_FAIL);
				java.util.Date date2 = AonDateUtils.addDays(new java.util.Date(), -7);
				if((cf == null && pFail == null && pcFail == null && sale == null && sFail == null) ||  (cf != null && cf.getDate().compareTo(date2) <= 1  && 
						Double.parseDouble(cf.getRespuesta().getImporte()) < r.getPenaltyAmount())){
					if(!dryRun){
						ConexFlowConnection connection = DBConsults.getConection(d);
					
						if(cf != null){
							// CANCELAR PREAUTHORIZACION 
							Query cancelQuery = ConexFlowUtils.getConexFlowCancelationQuery(connection
									, cf.getRespuesta().getOperacion(), Double.parseDouble(cf.getRespuesta().getImporte())
									, Double.parseDouble(cf.getRespuesta().getImporte()) 
									, cf.getRespuesta().getAutorizacion(), r.getHotelReservation().toString()
									, cf.getRespuesta().getIdOperacion(), cf.getRespuesta().getFecha());
							ConexFlow cancelConexFlow = ConexFlowPost.execute(connection,ConexFlowConstant.CANCELATION_OP, cancelQuery);
					
							Boolean ok = cancelConexFlow.getRespuesta().getResultado().equals("000");
							cancelConexFlow.setStatus(ok ? ConexFlowStatus.CANCEL : ConexFlowStatus.CANCEL_FAIL);
							String description = "CONEXFLOW_(" + r.getToken().substring(r.getToken().length()-5) + ")_"
									+ cancelConexFlow.getStatus().getName() + "#" + cancelConexFlow.getRespuesta().getImporte();
							DBConsults.insertConexFlow(d, login, cancelConexFlow, r.getProject(), description);
				
							String description2 = "CONEXFLOW_(" + r.getToken().substring(r.getToken().length()-5) + ")_"
									+ cf.getStatus().cancel().getName() + "#" + cf.getRespuesta().getImporte();			
							DBConsults.updateConexFlowDescription(d, login, cf.getId(), description2);
						}
					
						Query query = ConexFlowUtils.getConexFlowPreauthorizationPaymentQuery(connection
								, r.getHotelReservation().toString(), r.getToken(), r.getPenaltyAmount());
						ConexFlow conexFlow = ConexFlowPost.execute(connection,ConexFlowConstant.PREAUTHORIZATION_OP, query);
						Boolean ok = conexFlow.getRespuesta().getResultado().equals("000");
						conexFlow.setStatus(ok ? ConexFlowStatus.PREAUTHORIZATION : ConexFlowStatus.PREAUTHORIZATION_FAIL);
						String description = "CONEXFLOW_(" + r.getToken().substring(r.getToken().length()-5) + ")_"
								+ conexFlow.getStatus().getName() + "#" + conexFlow.getRespuesta().getImporte();
						DBConsults.insertConexFlow(d, login, conexFlow, r.getProject(), description);
					
						String msg = "";
						if(!ok){
							msg = "Error " + conexFlow.getRespuesta().getResultado() + ": " + conexFlow.getRespuesta().getDesResultado() + ".";
						} else msg = "PREAUTHORIZATION OK";
						String projectName = DBConsults.getProjectName(d, login, r.getProject());
						View.preauthorized(projectName, r.getProject(), msg, r.getPenaltyAmount());
					} else {
						String msg = "PREAUTHORIZATION OK";
						String projectName = DBConsults.getProjectName(d, login, r.getProject());
						View.preauthorized(projectName, r.getProject(), msg, r.getPenaltyAmount());
					}
					cont[0]++;
				}
			}
		});
	}

	public static void preauthorizationRenovate(Domain domain, String login){
		final int[] cont = {0};
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -days);
		Date date = new Date(calendar.getTime().getTime());
		DBConsults.getConexFlowStatusStreamX(domain, login, ConexFlowStatus.PREAUTHORIZATION)
		.filter(f -> f.getDate().compareTo(date)<= 0).forEach(p -> {
			if(number == -1 || cont[0] <= number){
				ProjectReservation r = AON.getProjectReservation(domain.getName(), domain.getId(), login, 
					f -> f.getProjectProperty().eq(p.getProject()));
				Domain d = AON.getDomain(domain.getName(), r.getDomain().getId(), login);		
				if(!dryRun){
					ConexFlowConnection connection = DBConsults.getConection(d);
				
					// CANCELAR PREAUTHORIZACION 
					Query cancelQuery = ConexFlowUtils.getConexFlowCancelationQuery(connection
						, p.getRespuesta().getOperacion(), Double.parseDouble(p.getRespuesta().getImporte())
						, Double.parseDouble(p.getRespuesta().getImporte()) 
						, p.getRespuesta().getAutorizacion(), r.getHotelReservation().toString()
						, p.getRespuesta().getIdOperacion(), p.getRespuesta().getFecha());
					ConexFlow cancelConexFlow = ConexFlowPost.execute(connection,ConexFlowConstant.CANCELATION_OP, cancelQuery);

					Boolean ok = cancelConexFlow.getRespuesta().getResultado().equals("000");
					cancelConexFlow.setStatus(ok ? ConexFlowStatus.CANCEL : ConexFlowStatus.CANCEL_FAIL);
					String description = "CONEXFLOW_(" + r.getToken().substring(r.getToken().length()-5) + ")_"
						+ cancelConexFlow.getStatus().getName() + "#" + cancelConexFlow.getRespuesta().getImporte();
					DBConsults.insertConexFlow(d, login, cancelConexFlow, r.getProject(), description);
			
					String description2 = "CONEXFLOW_(" + r.getToken().substring(r.getToken().length()-5) + ")_"
						+ p.getStatus().cancel().getName() + "#" + p.getRespuesta().getImporte();			
					DBConsults.updateConexFlowDescription(d, login, p.getId(), description2);
		
					Query query = ConexFlowUtils.getConexFlowPreauthorizationPaymentQuery(connection
						, r.getHotelReservation().toString(), r.getToken(), r.getPenaltyAmount());
					ConexFlow conexFlow = ConexFlowPost.execute(connection,ConexFlowConstant.PREAUTHORIZATION_OP, query);
					Boolean ok3 = conexFlow.getRespuesta().getResultado().equals("000");
					conexFlow.setStatus(ok3 ? ConexFlowStatus.PREAUTHORIZATION : ConexFlowStatus.PREAUTHORIZATION_FAIL);
					String description3 = "CONEXFLOW_(" + r.getToken().substring(r.getToken().length()-5) + ")_"
						+ conexFlow.getStatus().getName() + "#" + conexFlow.getRespuesta().getImporte();
					DBConsults.insertConexFlow(d, login, conexFlow, r.getProject(), description3);
					String msg = "";
					if(!ok3){
						msg = "Error " + conexFlow.getRespuesta().getResultado() + ": " + conexFlow.getRespuesta().getDesResultado() + ".";
					} else msg = "PREAUTHORIZATION OK";
					String projectName = DBConsults.getProjectName(d, login, r.getProject());
					View.preauthorized(projectName, r.getProject(), msg, r.getPenaltyAmount());
				} else {
					String msg = "PREAUTHORIZATION OK";
					String projectName = DBConsults.getProjectName(d, login, r.getProject());
					View.preauthorized(projectName, r.getProject(), msg, r.getPenaltyAmount());	
				}
				cont[0]++;
			}
		});
	}
	
	public static void main(String[] args) throws AonConnectionException{
		if (!parse(args))
			return;
		
		String domainName = getDomains().get(0);
		Domain domain = null;
		AONContext ctx = null;
		try {
			ctx = new AONContext(DBConsults.getConnection(domainName));
			domain = DBConsults.getDomain(ctx, domainName);
		}finally{
			if(ctx != null) ctx.close();
		}
		if(p001){
			preauthorization001(domain, "system");
		}
		
		if(penalty){
			if(days == -1){
				preauthorizationPenalty(domain, "system");
			} else preauthorizationPenaltyDays(domain, "system");
		}
		
		if(renovate){
			if(days == -1){
				days = 7;
			}
			preauthorizationRenovate(domain, "system");
		}
	}

	private static String hotels[];
	private static boolean dryRun;
	
	private static boolean penalty;
	private static boolean p001;
	private static boolean renovate;
	
	private static Integer days;
	private static Integer number;
	
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
				.withDescription("specify a hotel list, consisting of hotel names separated by commas, e.g., \"Hotel Barcelo , Hotel Gran Lakua\"");
		OptionBuilder.withValueSeparator(',');
		OptionBuilder.withLongOpt("hotels");
		Option hotelOption = OptionBuilder.create("hotel");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("perform a trial run with no changes made");
		OptionBuilder.withLongOpt("dry-run");
		Option dryOption = OptionBuilder.create('n');
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("");
		OptionBuilder.withLongOpt("penalty");
		Option penaltyOption = OptionBuilder.create("penalty");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("");
		OptionBuilder.withLongOpt("renovate");
		Option renovateOption = OptionBuilder.create("renovate");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("perform a trial run with no changes made");
		OptionBuilder.withLongOpt("001");
		Option p001Option = OptionBuilder.create("001");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("penalty preauthorization days | renovate expiration days");
		OptionBuilder.withLongOpt("days");
		Option daysOption = OptionBuilder.create("days");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("number of iterations");
		OptionBuilder.withLongOpt("number");
		Option numberOption = OptionBuilder.create("number");

		options.addOption(helpOption);
		options.addOption(hotelOption);
		options.addOption(dryOption);
		options.addOption(penaltyOption);
		options.addOption(renovateOption);
		options.addOption(p001Option);
		options.addOption(daysOption);
		options.addOption(numberOption);

		try {
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(helpOption.getOpt())) {
				helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
				return false;
			}

			dryRun = line.hasOption(dryOption.getOpt());
			if (dryRun)
				LOGGER.info("DryRun ON: Perform a trial run with no changes made.");

			penalty = line.hasOption(penaltyOption.getOpt());
			renovate = line.hasOption(renovateOption.getOpt());
			p001 = line.hasOption(p001Option.getOpt());
			
			hotels = line.getOptionValues(hotelOption.getOpt());
			if (hotels == null)
				hotels = new String[] {};
			
			String daysString = line.getOptionValue(daysOption.getOpt());
			if(daysString == null)
				days = -1;
			else days = Integer.parseInt(daysString);
			
			String numberString = line.getOptionValue(numberOption.getOpt());
			if(numberString == null){
				number = -1;
			} else number = Integer.parseInt(numberString);
				
		} catch (ParseException e) {
			System.out.print(e.getMessage());
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
			return false;
		}
		return true;
	}
}
