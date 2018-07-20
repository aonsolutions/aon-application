package net.aonsolutions.rest.api.function;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Month;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.solicitud.trabajadorestramos.SolicitudTrabajadoresTramosBuilder;
import net.aonsolutions.rest.api.model.ServerlessInput;
import net.aonsolutions.rest.api.model.ServerlessOutput;

public class GetCretaFile implements RequestHandler<ServerlessInput, ServerlessOutput> {
	
	public static final String FILE = "file";

    @Override
    public ServerlessOutput handleRequest(ServerlessInput serverlessInput, Context context) {
    	
    	
    	String file = serverlessInput.getPathParameters().get(FILE);
    	
    	
    	Calendar calendar = Calendar.getInstance();
    	
		String tipo = "L00";
		int autorizado= 666;
		int desdeAnho = calendar.get(Calendar.YEAR);
		Month desdeMes = getMonth(calendar);
		int hastaAnho = calendar.get(Calendar.YEAR);
		Month hastaMes = getMonth(calendar);
		int ctrlAnho = calendar.get(Calendar.YEAR);
		Month ctrlMes = getMonth(calendar);
		String cccs [] = {"00000000000"} ; //Collections.emptyList();
		
		
		
    	
		SolicitudTrabajadoresTramosBuilder builder = new SolicitudTrabajadoresTramosBuilder()
				.setAutorizado(autorizado);

		for (String cCC : cccs) {
			builder.setCCC(cCC)
			.setTipo(tipo)
			.setMesDesde(desdeMes)
			.setAnhoDesde(desdeAnho)
			.setMesHasta(hastaMes)
			.setAnhoHasta(hastaAnho)
			.setMesControl(ctrlMes)
			.setAnhoControl(ctrlAnho)
			.addLiquidacion();
		}
		net.aonsolutions.core.tgss.creta.jaxb.solicitud.trabajadorestramos.SolicitudTrabajadoresTramos solicitudTrabajadoresTramos = builder
				.createSolicitudBorrador();

    	StringWriter sw = new StringWriter();
    	
        ServerlessOutput output = new ServerlessOutput();
    	Map<String,String> headers = new HashMap<String,String>();

    	try {
    		Utils.marshal(solicitudTrabajadoresTramos, sw);
			output.setStatusCode(200);
            headers.put("Content-Type", "text/xml");
		} catch (Exception e) {
            e.printStackTrace(new PrintWriter(sw));
            headers.put("Content-Type", "text/plain");
		} finally {
			output.setStatusCode(500);
		}
    	
    	String body = sw.toString();
        output.setBody(body);
        headers.put("Content-Length", Integer.toString(body.length()));
        output.setHeaders(headers);
        
        return output;
    	
    }
    
    
    private static Month getMonth(Calendar calendar) {
    	int month = calendar.get(Calendar.MONTH);
    	switch (month) {
		case Calendar.JANUARY:
			return Month.JANUARY;
		case Calendar.FEBRUARY:
			return Month.FEBRUARY;
		case Calendar.MARCH:
			return Month.MARCH;
		case Calendar.APRIL:
			return Month.APRIL;
		case Calendar.MAY:
			return Month.MAY;
		case Calendar.JUNE:
			return Month.JUNE;
		case Calendar.JULY:
			return Month.JULY;
		case Calendar.AUGUST:
			return Month.AUGUST;
		case Calendar.SEPTEMBER:
			return Month.SEPTEMBER;
		case Calendar.OCTOBER:
			return Month.OCTOBER;
		case Calendar.NOVEMBER:
			return Month.NOVEMBER;
		case Calendar.DECEMBER:
			return Month.DECEMBER;
		case Calendar.UNDECIMBER:
			return null;
		default:
			return null;
		}
    }
}
