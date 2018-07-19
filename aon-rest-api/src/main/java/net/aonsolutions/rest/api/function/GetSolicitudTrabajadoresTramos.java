package net.aonsolutions.rest.api.function;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Month;
import java.util.Calendar;

import javax.xml.bind.JAXBException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.solicitud.trabajadorestramos.SolicitudTrabajadoresTramos;
import net.aonsolutions.core.tgss.creta.jaxb.solicitud.trabajadorestramos.SolicitudTrabajadoresTramosBuilder;

public class GetSolicitudTrabajadoresTramos implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

        // TODO: Implement your stream handler. See https://docs.aws.amazon.com/lambda/latest/dg/java-handler-io-type-stream.html for more information.
        // This demo implementation capitalizes the characters from the input stream.
        // int letter = 0;
        // while((letter = input.read()) >= 0) {
        //     output.write(Character.toUpperCase(letter));
        // }
    	
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
    	
    	try {
			Utils.marshal(solicitudTrabajadoresTramos, output);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
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
