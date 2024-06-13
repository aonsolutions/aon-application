package aon.solutions;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.esferalia.aon.jooq.tables.Task;
import com.esferalia.aon.jooq.tables.TaskWorkflow;
import com.esferalia.aon.occam.api.AONContext;

public class taskToCsv {
	
	public static void main(String[] args) throws SQLException {
		
		Option hostOption = Option.builder("h").hasArg().required().longOpt("host").argName("name")
				.desc("Connect to host.").build();

			Option portOption = Option.builder("P").hasArg().longOpt("port").argName("name")
				.desc("Port number to use for connection, default (3306).").build();

			Option userOption = Option.builder("u").hasArg().required().longOpt("user").argName("name")
				.desc("User for login if not current user.").build();

			Option passwordOption = Option.builder("p").hasArg().required().longOpt("password").argName("name")
				.desc("Password to use when connecting to server.").build();

			Option databaseOption = Option.builder("d").hasArg().required().longOpt("database").argName("name")
				.desc("Database to use").build();

			Option helpOption = Option.builder("?").longOpt("help").desc("Display this help and exit.").build();

			Options options = new Options();
			options.addOption(helpOption);
			options.addOption(hostOption);
			options.addOption(portOption);
			options.addOption(userOption);
			options.addOption(passwordOption);
			options.addOption(databaseOption);

			CommandLine commandLine = null;

			try {
			    CommandLineParser parser = new DefaultParser();
			    commandLine = parser.parse(options, args);
			} catch (ParseException e) {
			    // oops, something went wrong
			    System.out.println("Error: " + e.getLocalizedMessage());
			    new HelpFormatter().printHelp(taskToCsv.class.getSimpleName(), options);
			    return;
			}

			String dbHost = commandLine.getOptionValue(hostOption.getLongOpt());
			String pdbPrt = commandLine.getOptionValue(portOption.getLongOpt(), "3306");
			String dbUser = commandLine.getOptionValue(userOption.getLongOpt());
			String dbPassword = commandLine.getOptionValue(passwordOption.getLongOpt());
			String dbName = commandLine.getOptionValue(databaseOption.getLongOpt());

			Properties properties = new Properties();
			properties.setProperty("user", dbUser);
			properties.setProperty("password", dbPassword);
			properties.setProperty("useSSL", "false");
			properties.setProperty("serverTimezone", TimeZone.getDefault().getID());
			String url = String.format("jdbc:mysql://%s:%s/%s", dbHost, pdbPrt, dbName);
		
			try (Connection connection = DriverManager.getConnection(url, properties)) {
				
				getTask(connection);
			}
		
	}
	
	
    private static void getTask(Connection conn) throws SQLException {
				
			// AONContext ctx = new AONContext(conn);
    	    Settings sett = new Settings();
    	    sett.setRenderSchema(false);
    	    sett.setParamType(ParamType.INLINED);
			DSLContext ctx = DSL.using(conn, sett);
			
			List<String[]> qaList = new ArrayList<>();
			
			
			var maxDates = ctx
					.select(TaskWorkflow.TASK_WORKFLOW.TASK, DSL.max(TaskWorkflow.TASK_WORKFLOW.CREATION_DATE).as("max_date"))
					.from(TaskWorkflow.TASK_WORKFLOW)
					.where(TaskWorkflow.TASK_WORKFLOW.COMMENT.isNotNull())
					.groupBy(TaskWorkflow.TASK_WORKFLOW.TASK)
					.asTable("max_dates");
			
			
			Result<Record3<Integer, String, String>> result = 
			ctx
			.selectDistinct(Task.TASK.ID, Task.TASK.COMMENTS, TaskWorkflow.TASK_WORKFLOW.COMMENT)
			.from(Task.TASK)
			.join(TaskWorkflow.TASK_WORKFLOW).on(Task.TASK.ID.eq(TaskWorkflow.TASK_WORKFLOW.TASK))
			.join(maxDates).on(TaskWorkflow.TASK_WORKFLOW.TASK.eq(maxDates.field(TaskWorkflow.TASK_WORKFLOW.TASK)))
			.and(TaskWorkflow.TASK_WORKFLOW.CREATION_DATE.eq(maxDates.field("max_date", SQLDataType.TIMESTAMP)))
			.where(TaskWorkflow.TASK_WORKFLOW.COMMENT.isNotNull())
			.fetch();
			

	        
			for (Record3<Integer, String, String> record : result) {
	            Integer id = record.get(Task.TASK.ID);
	            String comments = record.get(Task.TASK.COMMENTS);
	            String workflowComment = record.get(TaskWorkflow.TASK_WORKFLOW.COMMENT);
	            //System.out.println("ID: " + id + ", Comments: " + comments + ", Workflow Comment: " + workflowComment);
	            qaList.add(new String[]{comments, workflowComment});
	        }
			
			int fileCount = 0;
	        int recordCount = 0;
	        int maxRows = 4000;
	        int maxCellLength = 32767;

	        try {
	            while (recordCount < qaList.size()) {
	                
	            	
	                String excelFile = "Preguntas_Respuestas_" + fileCount + ".xlsx";
	                Workbook workbook = new XSSFWorkbook();
	                Sheet sheet = workbook.createSheet("Preguntas y Respuestas");

	              
	                Row headerRow = sheet.createRow(0);
	                Cell headerCell1 = headerRow.createCell(0);
	                headerCell1.setCellValue("Preguntas");
	                Cell headerCell2 = headerRow.createCell(1);
	                headerCell2.setCellValue("Respuestas");
                     
	              
	                int rowNum = 1;
	                for (int i = 0; i < maxRows && recordCount < qaList.size(); recordCount++) {
	                    String[] qa = qaList.get(recordCount);
	                    if (qa[0].length() <= maxCellLength && qa[1].length() <= maxCellLength) {
	                        Row row = sheet.createRow(rowNum++);
	                        Cell cell1 = row.createCell(0);
	                        cell1.setCellValue(qa[0]);
	                        Cell cell2 = row.createCell(1);
	                        cell2.setCellValue(qa[1]);
	                        i++;
	                    } else {
	                        System.out.println("Registro omitido debido a longitud excesiva.");
	                    }
	                }

	               
	                try (FileOutputStream fileOut = new FileOutputStream(excelFile)) {
	                    workbook.write(fileOut);
	                    System.out.println("Archivo Excel creado exitosamente: " + excelFile);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }

	                
	                workbook.close();
	                fileCount++;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        
	}
	
 
	

}

