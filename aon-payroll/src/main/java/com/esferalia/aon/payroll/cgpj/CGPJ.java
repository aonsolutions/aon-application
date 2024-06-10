package com.esferalia.aon.payroll.cgpj;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.watson.util.AonStringUtils;

public class CGPJ {
    
    public static interface CompensationListener {
	void onCompensation(String title, String description, Double amount , Integer days , Integer months );
    }
    
    private static final NumberFormat ES_NUMBER_FORMAT = NumberFormat.getInstance(new Locale("es", "ES"));
    
    private static final String SCHEME = "https";
    private static final String HOST = "www.poderjudicial.es";
    private static final String PATH = "/cgpj/es/Servicios/Utilidades/Calculo-de-indemnizaciones-por-extincion-de-contrato-de-trabajo/";
    
    private static final Pattern CHARSET= Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");

    private static final Pattern DAYS = Pattern.compile( "de\\s*[^a]*as\\s*:\\s*</span>\\s*(?<days>\\d+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE );
    private static final Pattern MONTHS = Pattern.compile( "de\\s*meses\\s*:\\s*</span>\\s*(?<months>\\d+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE );
    private static final Pattern RESULTS_TABLE = Pattern.compile( "<table.*\"tablaResult\".*?</table>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE );
    private static final Pattern RESULTS_ROW = Pattern.compile( "<tr>.*?<strong>(?<title>.*?)</strong>(\\s*<!--.*?-->)*(?<description>.*?)</div>.*?<td>\\s*(?<amount>\\d+(,\\d+)?)</td>.*?</tr>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE );
    
    
    public static void calculateCompensations(Date startDate, Date endDate, Optional<Double> dailySalary, Optional<Double> monthlySalary, Optional<Double> yearSalary, CompensationListener listener) throws IOException, ParseException{
	URL url = new URL(SCHEME, HOST, PATH);
	
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setReadTimeout(5000);
        connection.setConnectTimeout(5000);

        // Set request headers (optional)
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // Enable output and input streams
        connection.setDoOutput(true);
        connection.setDoInput(true);
        
        // Create the request body
        String requestBody = String.format(
        	"ts=1685522674281"
        	+ "&fechaIni=%1$td%%2F%1$tm%%2F%1$tY"
        	+ "&fechaFin=%2$td%%2F%2$tm%%2F%2$tY"
        	+ "&importe=%3$f"
        	+ "&sueldo=%4$s"
        	+ "&accion=Consultar"
        	,
        	startDate,
        	endDate,
        	dailySalary.orElse(monthlySalary.orElse(yearSalary.orElse(0.00))),
        	dailySalary.map( d -> "diario").orElse(monthlySalary.map( m -> "mensual").orElse("anual"))
        );

        // Write the request body to the connection
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(requestBody);
        outputStream.flush();
        outputStream.close();

        String contentType = connection.getContentType();
        String charset = getCharsetFromContentType(contentType); 
        
        // Get the response from the server
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        // Parse the response
        parseResponse(response.toString(), listener);

        // Disconnect the connection
        connection.disconnect();

    }

    
    private static void parseResponse(String response, CompensationListener listener) throws ParseException {
	Integer days = null ;
	Matcher daysMatcher = DAYS.matcher(response);
	if ( daysMatcher.find() ) {
	    days = Integer.parseInt(daysMatcher.group("days"));
	}
	
	Integer months = null;
	Matcher monthsMatcher = MONTHS.matcher(response);
	if ( monthsMatcher.find() ) {
	    months = Integer.parseInt(monthsMatcher.group("months"));
	}
	
	Matcher resultsTableMatcher = RESULTS_TABLE.matcher(response);
	while ( resultsTableMatcher.find() ) {
	    String resultsTable = resultsTableMatcher.group();
	    Matcher resultsRowMatcher = RESULTS_ROW.matcher(resultsTable);
	    while ( resultsRowMatcher.find() ) {
		String title = AonStringUtils.trim(resultsRowMatcher.group("title"));
		Double amount = ES_NUMBER_FORMAT.parse(resultsRowMatcher.group("amount")).doubleValue();
		String description =  AonStringUtils.trim(resultsRowMatcher.group("description"));
		listener.onCompensation(title, description, amount, days, months);
	    }
	}
    }
    
    /**
     * Parse out a charset from a content type header.
     * 
     * @param contentType
     *            e.g. "text/html; charset=EUC-JP"
     * @return "EUC-JP", or null if not found. Charset is trimmed and
     *         uppercased.
     */
    private static String getCharsetFromContentType(String contentType) {
      if (contentType == null)
        return null;

      Matcher m = CHARSET.matcher(contentType);
      if (m.find()) {
        return m.group(1).trim().toUpperCase();
      }
      return null;
    }    
    
    public static void main(String[] args) throws IOException, ParseException {
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	Date startDate = dateFormat.parse(args[0]);
	Date endDate = dateFormat.parse(args[1]);
	
	Optional<Double> yearSalary = Optional.empty();
	Optional<Double> dailySalary = Optional.of(ES_NUMBER_FORMAT.parse(args[2]).doubleValue());
	Optional<Double> monthlySalary = Optional.empty();

	calculateCompensations(
		startDate, 
		endDate,  
		dailySalary, 
		monthlySalary, 
		yearSalary, 
		(title,description,amount,days,months) -> System.out.println( title + description + amount + "(" + days +"," + months + ")" )
	);
	
	
	
    }

}
