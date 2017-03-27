package com.code.aon.conexflow.rpm;

import java.util.Collections;
import java.util.Date;
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
import com.esferalia.aon.watson.server.AonDateUtils;

public class Sale {
	
	private static final Logger LOGGER  = Logger.getLogger(DBConsults.class.getName());

	protected static List<String> getDomains() throws AonConnectionException{
		// Obtiene todos los dominios de la BD.
		ConnectionInfo connectionInfo = ConnectionInfo.getDefaultConnectionInfo();
		Map<String, String> domains = connectionInfo.getDomains();
		
		// Ordena los dominios por orden alfabetico.
		List<String> list = new LinkedList<String>(domains.keySet());
		Collections.sort(list, (String s1, String s2) -> s1.compareTo(s2));
		
		return list;
	}
	
	private static void sale(Domain domain, String login) {
		final int[] cont = {0};
		Stream<ProjectReservation> stream = DBConsults.getProjectReservationStream(domain, login,
				f -> f.getStartDateProperty().ge(AonDateUtils.toSql(new Date()))
				.and(f.getPrepayProperty().eq((byte) 0))
				.and(f.getAdvanceProperty().gt(0.00))
				.and(f.getTokenProperty().isNotNull())); 
		stream.forEach(r -> {
			if(number == -1 || cont[0] <= number){
				Domain d = AON.getDomain(domain.getName(), r.getDomain().getId(), login);
				ConexFlow cf = DBConsults.getConexFlowX(d, login, r.getProject(), "CONEXFLOW%ANT_TNR");
				ConexFlow sFail = DBConsults.getConexFlowLastStatusX(d, login, r.getProject(), r.getToken(), ConexFlowStatus.SALE_FAIL);
				ConexFlow pcFail = DBConsults.getConexFlowLastStatusX(d, login, r.getProject(), r.getToken(), ConexFlowStatus.PREAUTHORIZATION_CHECK_FAIL);
				ConexFlow scFail = DBConsults.getConexFlowLastStatusX(d, login, r.getProject(), r.getToken(), ConexFlowStatus.SALE_CHECK_FAIL);
				ConexFlow sale = DBConsults.getConexFlowLastStatusX(d, login, r.getProject(), r.getToken(), ConexFlowStatus.SALE);

				if(cf == null && sFail == null && pcFail == null && scFail == null && sale == null){
					ConexFlowConnection connection = DBConsults.getConection(d);
					Query query = ConexFlowUtils.getConexFlowCardPaymentQuery(connection, r.getToken(), 
						r.getAdvance(), r.getHotelReservation().toString(), null);
					ConexFlow conexFlow = ConexFlowPost.execute(connection, ConexFlowStatus.SALE.getName(), query);
					Boolean ok = conexFlow.getRespuesta().getResultado().equals("000");
					conexFlow.setStatus(ok ? ConexFlowStatus.SALE : ConexFlowStatus.SALE_FAIL);
					String description = "CONEXFLOW_(" + r.getToken().substring(r.getToken().length()-5) + ")_"
						+ conexFlow.getStatus().getName() + "#" + conexFlow.getRespuesta().getImporte() 
						+ (ok ? "_ANT_TNR" : "");
					conexFlow = DBConsults.insertConexFlow(d, login, conexFlow, r.getProject(), description);
			
					String msg = "";
					if (!ok){
						msg = "Error " + conexFlow.getRespuesta().getResultado() + ": " + conexFlow.getRespuesta().getDesResultado() + ".";
					} else {
						msg = "CHARGE OK";
						ConexFlowUtils.setVoucher(d, login,  r.getProject(), conexFlow);
					}
					String projectName = DBConsults.getProjectName(d, login, r.getProject());
					View.sale(projectName, r.getProject(), msg,r.getAdvance());
					cont[0]++;
				}
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
		
		sale(domain, "system"); 
	}

	private static boolean dryRun;
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
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("perform a trial run with no changes made");
		OptionBuilder.withLongOpt("dry-run");
		Option dryOption = OptionBuilder.create('n');
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("number of iterations");
		OptionBuilder.withLongOpt("number");
		Option numberOption = OptionBuilder.create("number");
		
		options.addOption(helpOption);
		options.addOption(dryOption);
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
