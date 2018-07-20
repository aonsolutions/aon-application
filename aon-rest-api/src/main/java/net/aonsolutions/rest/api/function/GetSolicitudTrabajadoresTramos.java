package net.aonsolutions.rest.api.function;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Month;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.solicitud.trabajadorestramos.SolicitudTrabajadoresTramos;
import net.aonsolutions.core.tgss.creta.jaxb.solicitud.trabajadorestramos.SolicitudTrabajadoresTramosBuilder;
import net.aonsolutions.rest.api.model.ServerlessInput;
import net.aonsolutions.rest.api.model.ServerlessOutput;

public class GetSolicitudTrabajadoresTramos implements RequestHandler<ServerlessInput, ServerlessOutput> {

    @Override
    public ServerlessOutput handleRequest(ServerlessInput serverlessInput, Context context) {

    	
    	String ltype = "L00";
    	String ccc = "1234567890";

    	Calendar calendar = Calendar.getInstance();
    	calendar.add(Calendar.MONTH, -1);
    	
    	int year = calendar.get(Calendar.YEAR);
    	Month month = getMonth(calendar);
    	
    	
    	SolicitudTrabajadoresTramos solicitudTrabajadoresTramos = 
	    	new SolicitudTrabajadoresTramosBuilder()
	    	.setCCC(ccc)
	    	.setTipo(ltype)
	    	.setAutorizado(666)
	    	.setAnhoDesde(year)
	    	.setMesDesde(month)
	    	.setAnhoHasta(year)
	    	.setMesHasta(month)
	    	.setCCCConcertado(ccc)
	    	.addLiquidacion()

	    	.setAnhoControl(year)
	    	.setMesControl(month)
	    	
	    	.createSolicitudBorrador()
    	;
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
