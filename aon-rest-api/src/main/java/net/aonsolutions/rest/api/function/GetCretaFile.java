package net.aonsolutions.rest.api.function;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.esferalia.aon.payroll.tgss.creta.Borrador;
import com.esferalia.aon.payroll.tgss.creta.Calculo;
import com.esferalia.aon.payroll.tgss.creta.Confirmacion;
import com.esferalia.aon.payroll.tgss.creta.DBA;
import com.esferalia.aon.payroll.tgss.creta.SolicitudTrabajadoresTramos;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.rest.api.model.ServerlessInput;
import net.aonsolutions.rest.api.model.ServerlessOutput;

public class GetCretaFile implements RequestHandler<ServerlessInput, ServerlessOutput>, CretaService.File.Visitor<ServerlessInput, ServerlessOutput, Exception> {
	
	public static final String FILE = "file";

    @Override
    public ServerlessOutput handleRequest(ServerlessInput serverlessInput, Context context) {
    	
		
		

		String fichero = serverlessInput.getPathParameters().get(FILE);
		CretaService.File file = CretaService.File.valueOf(fichero);
		
        ServerlessOutput serverlessOutput = new ServerlessOutput();

        try {
			file.accept(this, serverlessInput, serverlessOutput);
            serverlessOutput.setStatusCode(200);
		} catch (Exception e) {
			
	    	StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
            String body = sw.toString();

            Map<String, String> headers = new HashMap<String,String>();
            serverlessOutput.setBody(body);
            serverlessOutput.setStatusCode(500);
            headers.put("Content-Type", "text/plain");
            CORSUtils.addOriginHeader(serverlessInput, headers);
            serverlessOutput.setHeaders(headers);
		}
		
    	return serverlessOutput;
    	
    }
    
    
    // ------------------------------------------------------------------------
    // CretaService.File.Visitor
    
    
	@Override
	public void visitBases(ServerlessInput t, ServerlessOutput l) throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void visitRespuesta(ServerlessInput t, ServerlessOutput l) throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void visitTrabajadoresTramos(ServerlessInput t, ServerlessOutput l) throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void visitSolicitudBorrador(ServerlessInput t, ServerlessOutput l) throws Exception {
    	String body = t.getBody();
    	Map<String,List<String>> params = processParams(body);
		
    	String tipo = getValue(CretaService.Parameter.TIPO, params);
		String cccs [] = getValues(CretaService.Parameter.CCC, params);
		String desdeMes = getValue(CretaService.Parameter.DESDE_MES, params);
		String desdeAnho = getValue(CretaService.Parameter.DESDE_ANHO, params);
		String hastaMes = getValue(CretaService.Parameter.HASTA_MES, params);
		String hastaAnho = getValue(CretaService.Parameter.HASTA_ANHO, params);
		String autorizado = getValue(CretaService.Parameter.AUTORIZADO, params);
		boolean aceptarBasesAnteriores = AonStringUtils.equalsIgnoreCase("on",
				getValue(CretaService.Parameter.ACEPTAR_BASES_ANTERIORES, params));

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Borrador.generate(autorizado, desdeMes, desdeAnho, hastaMes, hastaAnho, tipo, aceptarBasesAnteriores, cccs, os);
		String charset = Charset.defaultCharset().name();
		int length = os.size();
		l.setBody(os.toString(charset));
		
        Map<String, String> headers = new HashMap<String,String>();
        CORSUtils.addOriginHeader(t, headers);
        headers.put("Content-Type", "text/xml; charset=" + charset);
        headers.put("Content-Length", Integer.toString(length));
		l.setHeaders(headers);
	}


	@Override
	public void visitSolicitudCalculos(ServerlessInput t, ServerlessOutput l) throws Exception {
    	String body = t.getBody();
    	Map<String,List<String>> params = processParams(body);
		
    	String tipo = getValue(CretaService.Parameter.TIPO, params);
		String cccs [] = getValues(CretaService.Parameter.CCC, params);
		String desdeMes = getValue(CretaService.Parameter.DESDE_MES, params);
		String desdeAnho = getValue(CretaService.Parameter.DESDE_ANHO, params);
		String hastaMes = getValue(CretaService.Parameter.HASTA_MES, params);
		String hastaAnho = getValue(CretaService.Parameter.HASTA_ANHO, params);
		String autorizado = getValue(CretaService.Parameter.AUTORIZADO, params);
		boolean claculosDesglosados = AonStringUtils.equalsIgnoreCase("on",
				getValue(CretaService.Parameter.CALCULOS_DESGLOSADOS, params));

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Calculo.generate(autorizado, desdeMes, desdeAnho, hastaMes, hastaAnho, tipo, claculosDesglosados, cccs, os);
		String charset = Charset.defaultCharset().name();
		int length = os.size();
		l.setBody(os.toString(charset));
		
        Map<String, String> headers = new HashMap<String,String>();
        CORSUtils.addOriginHeader(t, headers);
        headers.put("Content-Type", "text/xml; charset=" + charset);
        headers.put("Content-Length", Integer.toString(length));
		l.setHeaders(headers);
	}


	@Override
	public void visitSolicitudConfirmacion(ServerlessInput t, ServerlessOutput l) throws Exception {
    	String body = t.getBody();
    	Map<String,List<String>> params = processParams(body);
		
    	String tipo = getValue(CretaService.Parameter.TIPO, params);
		String cccs [] = getValues(CretaService.Parameter.CCC, params);
		String desdeMes = getValue(CretaService.Parameter.DESDE_MES, params);
		String desdeAnho = getValue(CretaService.Parameter.DESDE_ANHO, params);
		String hastaMes = getValue(CretaService.Parameter.HASTA_MES, params);
		String hastaAnho = getValue(CretaService.Parameter.HASTA_ANHO, params);
		String ctrlMes = getValue(CretaService.Parameter.CTRL_MES, params);
		String ctrlAnho = getValue(CretaService.Parameter.CTRL_ANHO, params);
		String autorizado = getValue(CretaService.Parameter.AUTORIZADO, params);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Confirmacion.generate(autorizado, desdeMes, desdeAnho, hastaMes, hastaAnho, ctrlMes, ctrlAnho, tipo, cccs, os);
		String charset = Charset.defaultCharset().name();
		int length = os.size();
		l.setBody(os.toString(charset));
		
        Map<String, String> headers = new HashMap<String,String>();
        CORSUtils.addOriginHeader(t, headers);
        headers.put("Content-Type", "text/xml; charset=" + charset);
        headers.put("Content-Length", Integer.toString(length));
		l.setHeaders(headers);
	}


	@Override
	public void visitSolicitudTrabajadoresTramos(ServerlessInput input, ServerlessOutput output) throws Exception {
    	String body = input.getBody();
    	Map<String,List<String>> params = processParams(body);
		
    	String tipo = getValue(CretaService.Parameter.TIPO, params);
		String cccs [] = getValues(CretaService.Parameter.CCC, params);
		String desdeMes = getValue(CretaService.Parameter.DESDE_MES, params);
		String desdeAnho = getValue(CretaService.Parameter.DESDE_ANHO, params);
		String hastaMes = getValue(CretaService.Parameter.HASTA_MES, params);
		String hastaAnho = getValue(CretaService.Parameter.HASTA_ANHO, params);
		String ctrlMes = getValue(CretaService.Parameter.CTRL_MES, params);
		String ctrlAnho = getValue(CretaService.Parameter.CTRL_ANHO, params);
		String autorizado = getValue(CretaService.Parameter.AUTORIZADO, params);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		SolicitudTrabajadoresTramos.generate(autorizado, desdeMes, desdeAnho, hastaMes, hastaAnho, ctrlMes, ctrlAnho, tipo, cccs, os);
		String charset = Charset.defaultCharset().name();
		int length = os.size();
		output.setBody(os.toString(charset));
		
        Map<String, String> headers = new HashMap<String,String>();
        CORSUtils.addOriginHeader(input, headers);
        headers.put("Content-Type", "text/xml; charset=" + charset);
        headers.put("Content-Length", Integer.toString(length));
		output.setHeaders(headers);
	}


	@Override
	public void visitComunicacionDatosBancarios(ServerlessInput t, ServerlessOutput l) throws Exception {
    	String body = t.getBody();
    	Map<String,List<String>> params = processParams(body);
		
		String autorizado = getValue(CretaService.Parameter.AUTORIZADO, params);
		String cccs[] = getValues(CretaService.Parameter.CCC, params);
		String tipoMoviento = getValue(CretaService.Parameter.TIPO_MOVIMIENTO, params);
		String tipoAccion = getValue(CretaService.Parameter.TIPO_ACCION, params);
		String iban = getValue(CretaService.Parameter.IBAN, params);
		String titular = getValue(CretaService.Parameter.TITULAR, params);
		String documento = AonStringUtils.leftPad(getValue(CretaService.Parameter.DOCUMENTO, params), 10, '0');
		String tipoDocumento = getValue(CretaService.Parameter.TIPO_DOCUMENTO, params);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		DBA.generate(autorizado, cccs, tipoMoviento, tipoAccion, iban, titular, documento, tipoDocumento, os);
		String charset = Charset.defaultCharset().name();
		int length = os.size();
		l.setBody(os.toString(charset));
		
        Map<String, String> headers = new HashMap<String,String>();
        CORSUtils.addOriginHeader(t, headers);
        headers.put("Content-Type", "text/xml; charset=" + charset);
        headers.put("Content-Length", Integer.toString(length));
		l.setHeaders(headers);
	}


	@Override
	public void visitDocumentoCalculoLiquidacion(ServerlessInput t, ServerlessOutput l) throws Exception {
		
		try {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			
			printWriter.println("<html>");
			printWriter.println("<body>");
			printWriter.println("<script>");
	
			printWriter.println("</script>");
			printWriter.println("</body>");
			printWriter.println("</html>");
	
			printWriter.flush();
			printWriter.close();
			stringWriter.flush();
			stringWriter.close();
			
			String body = stringWriter.toString();
			
			l.setBody(body);
			Map<String, String> headers = new HashMap<String,String>();
	        CORSUtils.addOriginHeader(t, headers);
	        headers.put("Content-Type", "text/html");
			l.setHeaders(headers);
		} finally {
			
		}
	}
    
    
    // ------------------------------------------------------------------------
    // Private 
	
	private static Map<String,List<String>> processParams(CharSequence input) {
		
		Map<String,List<String>> params = new HashMap<String,List<String>>();
		
		Pattern pattern =Pattern.compile("[\\?&]*(?<name>[^&=]+)=(?<value>[^&=]+)");
		Matcher matcher = pattern.matcher(input);
		while ( matcher.find() ) {
			String name = matcher.group("name");
			String value = matcher.group("value");
			List<String> values = params.getOrDefault(name, new ArrayList<String>());
			values.add(value);
			params.putIfAbsent(name, values);
		}
		
		return params;
	}
	

	private static String getValue(CretaService.Parameter param, Map<String,List<String>> params) {
		return params.get(param.name()).get(0);
	}

	private static String [] getValues(CretaService.Parameter param, Map<String,List<String>> params) {
		return params.getOrDefault(param.name(), Collections.emptyList()).toArray(new String [] {});
	}
}
