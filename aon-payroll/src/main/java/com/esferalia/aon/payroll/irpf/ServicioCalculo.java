package com.esferalia.aon.payroll.irpf;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.core.aeat.v2022.jaxb.AEATRetencionesEntrada2022;
import net.aonsolutions.core.aeat.v2022.jaxb.AEATRetencionesError2022;
import net.aonsolutions.core.aeat.v2022.jaxb.AEATRetencionesSalida2022;
import net.aonsolutions.core.aeat.v2023.jaxb.AEATRetencionesEntrada2023;
import net.aonsolutions.core.aeat.v2023.jaxb.AEATRetencionesError2023;
import net.aonsolutions.core.aeat.v2023.jaxb.AEATRetencionesSalida2023;
import net.aonsolutions.core.aeat.v2024.jaxb.AEATRetencionesEntrada2024;
import net.aonsolutions.core.aeat.v2024.jaxb.AEATRetencionesError2024;
import net.aonsolutions.core.aeat.v2024.jaxb.AEATRetencionesSalida2024;

class ServicioCalculo {
	
	public static final Date FEBRUARY_2023 = AonDateUtils.getDate(2023, Calendar.FEBRUARY , 1);
	public static final Date FEBRUARY_2024 = AonDateUtils.getDate(2024, Calendar.FEBRUARY , 8);

    // Al servicio se le pasará por el método POST del protocolo http una cadena de
    // caracteres encapsulada como si fuese desde un formulario. Las variables irán
    // etiquetadas como nombre=valor, separadas mediante el símbolo &, y sólo con el
    // valor en codificación URLEncoded.
    static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
	StringBuilder result = new StringBuilder();

	for (Map.Entry<String, String> entry : params.entrySet()) {
	    result.append(entry.getKey());
	    result.append("=");
	    result.append(URLEncoder.encode(entry.getValue(), "ISO-8859-1"));
	    //result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
	    result.append("&");
	}

	String resultString = result.toString();
	return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
    }
    
    static String read(InputStream in, String charset) throws IOException {
	try ( Reader reader = new InputStreamReader(in, charset) ) {
	    	
        	char cbuf [] = new char [1024*10];
        	StringBuffer content = new StringBuffer();
        	for ( int read = reader.read(cbuf); read != -1; read = reader.read(cbuf) ) {
        	    content.append(cbuf, 0, read);
        	}
        	return content.toString();
	}
    }

    static String procesarFicheroXml(String ficheroEntrada, Integer ejercicio, Integer periodo)
	    throws IOException {
	URL url = new URL("https://www2.agenciatributaria.gob.es/wlpl/PRET-C200/mc");
	
	HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

	con.setRequestMethod("POST");

	Map<String, String> params = new HashMap<>();
	params.put("EJER", ejercicio.toString());
	params.put("PER", periodo.toString());
	params.put("F01", ficheroEntrada);

	con.setDoOutput(true);
	DataOutputStream out = new DataOutputStream(con.getOutputStream());
	out.writeBytes(getParamsString(params));
	out.flush();
	out.close();

	int status = con.getResponseCode();

	String contentType = con.getContentType();
	String charset = getCharset(contentType);
	if (status > 299) {
	    throw new IOException(read(con.getErrorStream(), charset));
	} else {
	    return read(con.getInputStream(), charset);
	}
    }
    
    static String getCharset(String contentType) {
	try {
	    return Pattern.compile("charset=([^;]+)").matcher(contentType).group(1);
	} catch ( Exception e ) {
	    return "ISO-8859-15";
	}
    }
    
    public static  AEATRetencionesSalida2022 procesarFicheroXML(AEATRetencionesEntrada2022 entrada2022) throws JAXBException, IrpfCalculateException, IOException {
	    Marshaller marshaller = JAXBContext.newInstance(
		    AEATRetencionesEntrada2022.class).createMarshaller();
	    StringWriter writer = new StringWriter();
	    marshaller.marshal(entrada2022, writer);
	    
	    String str = ServicioCalculo.procesarFicheroXml(writer.toString(), 2022,0);
	    
	    try {
		    StringReader reader = new StringReader(str);
		    Unmarshaller unmarshaller = JAXBContext.newInstance(
			    AEATRetencionesSalida2022.class).createUnmarshaller();
		    AEATRetencionesSalida2022 salida2022 = 
			    (AEATRetencionesSalida2022)unmarshaller.unmarshal(reader);
		    System.out.println("Retenciones IRPF."
		    	+ "Servicio de Módulo de Cálculo de Retenciones "
		    	+ "EJERCICIOS 2022 y SIGUIENTES");
		    return salida2022;
	    } catch (JAXBException | IllegalArgumentException e ) {
		    StringReader reader = new StringReader(str);
		    Unmarshaller unmarshaller = JAXBContext.newInstance(
			    AEATRetencionesError2022.class).createUnmarshaller();
		    AEATRetencionesError2022 error2022 = 
			    (AEATRetencionesError2022)unmarshaller.unmarshal(reader);
		    throw new IrpfCalculateException(error2022);
	    }
    }
    
    public static  AEATRetencionesSalida2023 procesarFicheroXML(AEATRetencionesEntrada2023 entrada2023, Date fecha) throws JAXBException, IrpfCalculateException, IOException {
	    Marshaller marshaller = JAXBContext.newInstance(
		    AEATRetencionesEntrada2023.class).createMarshaller();
	    StringWriter writer = new StringWriter();
	    marshaller.marshal(entrada2023, writer);
	    
	    String str = ServicioCalculo.procesarFicheroXml(writer.toString(), 2023, fecha.before(FEBRUARY_2023) ? 0 : 1);
	    
	    try {
		    StringReader reader = new StringReader(str);
		    Unmarshaller unmarshaller = JAXBContext.newInstance(
			    AEATRetencionesSalida2023.class).createUnmarshaller();
		    AEATRetencionesSalida2023 salida2023 = 
			    (AEATRetencionesSalida2023)unmarshaller.unmarshal(reader);
		    System.out.println("Retenciones IRPF."
		    	+ "Servicio de Módulo de Cálculo de Retenciones "
		    	+ "EJERCICIOS 2023 y SIGUIENTES");
		    return salida2023;
	    } catch (JAXBException | IllegalArgumentException e ) {
		    StringReader reader = new StringReader(str);
		    Unmarshaller unmarshaller = JAXBContext.newInstance(
			    AEATRetencionesError2023.class).createUnmarshaller();
		    AEATRetencionesError2023 error2023 = 
			    (AEATRetencionesError2023)unmarshaller.unmarshal(reader);
		    throw new IrpfCalculateException(error2023);
	    }
    }

    public static  AEATRetencionesSalida2024 procesarFicheroXML(AEATRetencionesEntrada2024 entrada2024, Date fecha) throws JAXBException, IrpfCalculateException, IOException {
	    Marshaller marshaller = JAXBContext.newInstance(
		    AEATRetencionesEntrada2024.class).createMarshaller();
	    StringWriter writer = new StringWriter();
	    marshaller.marshal(entrada2024, writer);
	    
	    String str = ServicioCalculo.procesarFicheroXml(writer.toString(), 2024, fecha.before(FEBRUARY_2024) ? 0 : 1);
	    
	    try {
		    StringReader reader = new StringReader(str);
		    Unmarshaller unmarshaller = JAXBContext.newInstance(
			    AEATRetencionesSalida2024.class).createUnmarshaller();
		    AEATRetencionesSalida2024 salida2024 = 
			    (AEATRetencionesSalida2024)unmarshaller.unmarshal(reader);
		    System.out.println("Retenciones IRPF."
		    	+ "Servicio de Módulo de Cálculo de Retenciones "
		    	+ "EJERCICIOS 2024 y SIGUIENTES");
		    return salida2024;
	    } catch (JAXBException | IllegalArgumentException e ) {
		    StringReader reader = new StringReader(str);
		    Unmarshaller unmarshaller = JAXBContext.newInstance(
			    AEATRetencionesError2024.class).createUnmarshaller();
		    AEATRetencionesError2024 error2024 = 
			    (AEATRetencionesError2024)unmarshaller.unmarshal(reader);
		    throw new IrpfCalculateException(error2024);
	    }
    }

    public static void main(String[] args) throws IOException, JAXBException, IrpfCalculateException {
//	String ejemploSalida2022 = procesarFicheroXml(EJEMPLOENTRADA2022, 2022);
//	StringReader reader = new StringReader(ejemploSalida2022);
//	Unmarshaller unmarshaller = JAXBContext.newInstance(AEATRetencionesSalida2022.class).createUnmarshaller();
//	AEATRetencionesSalida2022 salida2022 = (AEATRetencionesSalida2022) unmarshaller.unmarshal(reader);
	
//	try ( StringReader ejemploEntrada2022Reader = new StringReader(EJEMPLOENTRADA2022) ) {
//	    AEATRetencionesEntrada2022 entrada2022 = (AEATRetencionesEntrada2022)
//	    JAXBContext.newInstance(AEATRetencionesEntrada2022.class).createUnmarshaller().unmarshal( ejemploEntrada2022Reader );
//        	
//	    AEATRetencionesSalida2022 salida2022 = procesarFicheroXML(entrada2022);
//	    JAXBContext.newInstance(AEATRetencionesSalida2022.class).createMarshaller().marshal(salida2022, System.out);
//	}

//	try ( StringReader ejemploEntrada2023Reader = new StringReader(EJEMPLOENTRADA2023) ) {
//	    AEATRetencionesEntrada2023 entrada2023 = (AEATRetencionesEntrada2023)
//	    JAXBContext.newInstance(AEATRetencionesEntrada2023.class).createUnmarshaller().unmarshal( ejemploEntrada2023Reader );
//    	
//	    AEATRetencionesSalida2023 salida2023 = procesarFicheroXML(entrada2023);
//	    JAXBContext.newInstance(AEATRetencionesSalida2023.class).createMarshaller().marshal(salida2023, System.out);
//	}
	
	try ( StringReader ejemploEntrada2024Reader = new StringReader(EJEMPLOENTRADA2024) ) {
	    AEATRetencionesEntrada2024 entrada2024 = (AEATRetencionesEntrada2024)
	    JAXBContext.newInstance(AEATRetencionesEntrada2024.class).createUnmarshaller().unmarshal( ejemploEntrada2024Reader );
    	
	    AEATRetencionesSalida2024 salida2024 = procesarFicheroXML(entrada2024, FEBRUARY_2024);
	    JAXBContext.newInstance(AEATRetencionesSalida2024.class).createMarshaller().marshal(salida2024, System.out);
	}
    }
    
    private static final String EJEMPLOENTRADA2022 = 
	    	"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
	    	+ "<AEATRetencionesEntrada2022>\n"
	    	+ "<IdDoc>\n"
	    	+ "<CodModelo>RET</CodModelo>\n"
	    	+ "<Ejercicio>2022</Ejercicio>\n"
	    	+ "</IdDoc>\n"
	    	+ "<Retenedor>\n"
	    	+ "<Nif>Z7896423E</Nif>\n"
	    	+ "<ApellidosNombre>LINUX FOUNDATION</ApellidosNombre>\n"
	    	+ "<Retenido>\n"
	    	+ "<Nif>87449445H</Nif>\n"
	    	+ "<ApellidosNombre>TORVALDS BENEDICT LINUS</ApellidosNombre>\n"
	    	+ "<AñoNacimiento>1982</AñoNacimiento>\n"
	    	+ "<SituacionFamiliar><Situacion3/></SituacionFamiliar>\n"
	    	+ "<SituacionLaboral><TrabajadorActivo><Contrato>1</Contrato></TrabajadorActivo></SituacionLaboral>\n"
	    	+ "<RetribAnuales>20897.84</RetribAnuales>\n"
	    	+ "<Cotizaciones>1327.01</Cotizaciones>\n"
	    	+ "</Retenido>\n"
	    	+ "</Retenedor>"
	    	+ "</AEATRetencionesEntrada2022>";

    private static final String EJEMPLOENTRADA2023 = 
	    	"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
	    	+ "<AEATRetencionesEntrada2023>\n"
	    	+ "<IdDoc>\n"
	    	+ "<CodModelo>RET</CodModelo>\n"
	    	+ "<Ejercicio>2023</Ejercicio>\n"
	    	+ "</IdDoc>\n"
	    	+ "<Retenedor>\n"
	    	+ "<Nif>Z7896423E</Nif>\n"
	    	+ "<ApellidosNombre>LINUX FOUNDATION</ApellidosNombre>\n"
	    	+ "<Retenido>\n"
	    	+ "<Nif>87449445H</Nif>\n"
	    	+ "<ApellidosNombre>TORVALDS BENEDICT LINUS</ApellidosNombre>\n"
	    	+ "<AñoNacimiento>1982</AñoNacimiento>\n"
	    	+ "<SituacionFamiliar><Situacion3/></SituacionFamiliar>\n"
	    	+ "<SituacionLaboral><TrabajadorActivo><Contrato>1</Contrato></TrabajadorActivo></SituacionLaboral>\n"
	    	+ "<RetribAnuales>17594.52</RetribAnuales>\n"
	    	+ "<Cotizaciones>1153.08</Cotizaciones>\n"
	    	+ "</Retenido>\n"
	    	+ "</Retenedor>"
	    	+ "</AEATRetencionesEntrada2023>";

    private static final String EJEMPLOENTRADA2024 = 
	    	"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
	    	+ "<AEATRetencionesEntrada2024>"
	    	+ "<IdDoc>"
	    	+ "<CodModelo>RET</CodModelo>"
	    	+ "<Ejercicio>2024</Ejercicio>"
	    	+ "</IdDoc>"
	    	+ "<Retenedor>"
	    	+ "<Nif>Z7896423E</Nif>"
	    	+ "<ApellidosNombre>LINUX FOUNDATION</ApellidosNombre>"
	    	+ "<Retenido><Nif>87449445H</Nif><ApellidosNombre>TORVALDS BENEDICT LINUS</ApellidosNombre>"
	    	+ "<Nacimiento>1974</Nacimiento>"
	    	+ "<SituacionFamiliar><Situacion3/></SituacionFamiliar>"
	    	+ "<SituacionLaboral><TrabajadorActivo><Contrato>1</Contrato></TrabajadorActivo></SituacionLaboral>"
	    	+ "<RetribAnuales>15120.00</RetribAnuales>"
	    	+ "<Cotizaciones>978.26</Cotizaciones>"
	    	+ "</Retenido>"
	    	+ "</Retenedor></AEATRetencionesEntrada2024>";

    

}
