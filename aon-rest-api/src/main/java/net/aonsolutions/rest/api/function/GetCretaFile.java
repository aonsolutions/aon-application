package net.aonsolutions.rest.api.function;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.time.Month;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.esferalia.aon.payroll.tgss.creta.Borrador;
import com.esferalia.aon.payroll.tgss.creta.SolicitudTrabajadoresTramos;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.solicitud.trabajadorestramos.SolicitudTrabajadoresTramosBuilder;
import net.aonsolutions.rest.api.model.ServerlessInput;
import net.aonsolutions.rest.api.model.ServerlessOutput;

public class GetCretaFile implements RequestHandler<ServerlessInput, ServerlessOutput>, CretaService.File.Visitor<ServerlessInput, ServerlessOutput, Exception> {
	
	public static final String FILE = "file";

    @Override
    public ServerlessOutput handleRequest(ServerlessInput serverlessInput, Context context) {
    	
		Map<String,String> params = serverlessInput.getQueryStringParameters();
    	
		String tipo = params.get(CretaService.Parameter.TIPO.name());
		String cccs = params.get(CretaService.Parameter.CCC.name());
		String desdeMes = params.get(CretaService.Parameter.DESDE_MES.name());
		String desdeAnho = params.get(CretaService.Parameter.DESDE_ANHO.name());
		String hastaMes = params.get(CretaService.Parameter.HASTA_MES.name());
		String hastaAnho = params.get(CretaService.Parameter.HASTA_ANHO.name());
		String ctrlMes = params.get(CretaService.Parameter.CTRL_MES.name());
		String ctrlAnho = params.get(CretaService.Parameter.CTRL_ANHO.name());
		String autorizado = params.get(CretaService.Parameter.AUTORIZADO.name());
		
		

		String fichero = serverlessInput.getPathParameters().get(FILE);
		CretaService.File file = CretaService.File.valueOf(fichero);
		
        ServerlessOutput serverlessOutput = new ServerlessOutput();

    	Map<String,String> headers = new HashMap<String,String>();
        try {
			file.accept(this, serverlessInput, serverlessOutput);
            serverlessOutput.setStatusCode(200);
            headers.put("Content-Type", "text/plain");
            serverlessOutput.setBody(
            		"tipo = '"+ tipo +"',"
            		+"ccs = '"+ cccs +"',"
            		+"desdeMes = '"+ desdeMes +"',"
            		+"desdeAnho = '"+ desdeAnho +"',"
            		+"hastaMes = '"+ hastaMes +"',"
            		+"hastaAnho = '"+ hastaAnho +"',"
            		+"ctrlMes = '"+ ctrlMes +"',"
            		+"ctrlAnho = '"+ ctrlAnho +"',"
            		+"autorizado = '"+ autorizado +"',"
            );
		} catch (Exception e) {
            headers.put("Content-Type", "text/plain");
			
	    	StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
            String body = sw.toString();

            serverlessOutput.setBody(body);
            serverlessOutput.setStatusCode(500);
		}
        serverlessOutput.setHeaders(headers);
		
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
		// TODO Auto-generated method stub
		
	}


	@Override
	public void visitSolicitudCalculos(ServerlessInput t, ServerlessOutput l) throws Exception {
		// TODO Auto-generated method stub
	}


	@Override
	public void visitSolicitudConfirmacion(ServerlessInput t, ServerlessOutput l) throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void visitSolicitudTrabajadoresTramos(ServerlessInput t, ServerlessOutput l) throws Exception {
//		Map<String,String> params = t.getQueryStringParameters();
//		
//		String tipo = params.get(CretaService.Parameter.TIPO.name());
//		String cccs[] = params.get(CretaService.Parameter.CCC.name());
//		String desdeMes = params.get(CretaService.Parameter.DESDE_MES.name());
//		String desdeAnho = params.get(CretaService.Parameter.DESDE_ANHO.name());
//		String hastaMes = params.get(CretaService.Parameter.HASTA_MES.name());
//		String hastaAnho = params.get(CretaService.Parameter.HASTA_ANHO.name());
//		String ctrlMes = params.get(CretaService.Parameter.CTRL_MES.name());
//		String ctrlAnho = params.get(CretaService.Parameter.CTRL_ANHO.name());
//		String autorizado = params.get(CretaService.Parameter.AUTORIZADO.name());
//
//		ByteArrayOutputStream os = new ByteArrayOutputStream();
//		SolicitudTrabajadoresTramos.generate(autorizado, desdeMes, desdeAnho, hastaMes, hastaAnho, ctrlMes, ctrlAnho, tipo, cccs, os);
//		l.setBody(os.toString(Charset.defaultCharset().name()));
	}


	@Override
	public void visitComunicacionDatosBancarios(ServerlessInput t, ServerlessOutput l) throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void visitDocumentoCalculoLiquidacion(ServerlessInput t, ServerlessOutput l) throws Exception {
		// TODO Auto-generated method stub
		
	}
    
    
    // ------------------------------------------------------------------------
    // Private 



}
