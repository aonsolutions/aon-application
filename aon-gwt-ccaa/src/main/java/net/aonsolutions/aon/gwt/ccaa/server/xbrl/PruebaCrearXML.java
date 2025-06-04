package net.aonsolutions.aon.gwt.ccaa.server.xbrl;

import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.Constantes.D_ACTUAL;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.Constantes.D_ANTERIOR;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.Constantes.I_ACTUAL;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.Constantes.I_ANTERIOR;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.ConstantesAbreviado.CONTEXTOS_MEM_ABREVIADO;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.ConstantesAbreviado.CONVERSION_BAL_ABREVIADO;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.ConstantesAbreviado.CONVERSION_MEM_ABREVIADO;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.ConstantesAbreviado.CONVERSION_MEM_CUADROS_ABREVIADO;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.ConstantesAbreviado.CONVERSION_PYG_ABREVIADO;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.ConstantesAbreviado.NAME_SPACES_URI_ABREVIADO;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.ConstantesAbreviado.NAME_SPACES_URI_ABREVIADO_MEMORIA;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.ConstantesAbreviado.URI_SCHEMA_ABREVIADO;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.ConstantesAbreviado.URI_SCHEMA_ABREVIADO_MEMORIA;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.ConstantesPymes.CONTEXTOS_MEM_PYMES;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.ConstantesPymes.CONVERSION_BAL_PYMES;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.ConstantesPymes.CONVERSION_MEM_CUADROS_PYMES;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.ConstantesPymes.CONVERSION_MEM_PYMES;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.ConstantesPymes.CONVERSION_PYG_PYMES;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.ConstantesPymes.NAME_SPACES_URI_PYMES;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.ConstantesPymes.NAME_SPACES_URI_PYMES_MEMORIA;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.ConstantesPymes.URI_SCHEMA_PYMES;
import static net.aonsolutions.aon.gwt.ccaa.server.xbrl.ConstantesPymes.URI_SCHEMA_PYMES_MEMORIA;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema;
import com.esferalia.aon.watson.util.AonStringUtils;

public class PruebaCrearXML {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat DATE_FORMAT_YEAR = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat DATE_FORMAT_MONTH = new SimpleDateFormat("MM");
	private static final SimpleDateFormat DATE_FORMAT_DAY = new SimpleDateFormat("dd");
	
	private static ArrayList<String> notas = new ArrayList<>(); // Notas de la Memoria
	private static Esquema schemaXml;
	
    public static void pruebaCrearXBRL(Esquema schema, File parent) throws IOException {
    	
        try {
	        // ESTOS DATOS SON PARA PROBAR, SE COGERAN DEL XML, SI FECHAINIANTERIOR = NULL Y FECHAFINANTERIOR = NULL SE SUPONE QUE NO HAY EJERCICIO ANTERIOR
//        	File f = new File("C:\\TMP\\prueba.xml");
//	        String nifEmpresa = "B50111111";
//	        Date fechaIniActual = new Date("01/01/2024");
//			Date fechaFinActual = new Date("12/31/2024");
//	        Date fechaIniAnterior = new Date("01/01/2023");
//			Date fechaFinAnterior = new Date("12/31/2023");
//			boolean formatoPymes = true; // Formato PYMES o Abreviado
//			boolean llevaMemoria = true;  // Indica si lleva memoria normalizada
			// ----------------------------------------------
        	schemaXml = schema;
	        String nifEmpresa = schema.getCabecera().getCIF();
	        Date fechaIniActual = buscarFecha("1102",""); 
			Date fechaFinActual = buscarFecha("1101","");
	        Date fechaIniAnterior = buscarFecha("1102","9");
			Date fechaFinAnterior = buscarFecha("1101","9");
			boolean formatoPymes = "PYMES".equalsIgnoreCase(schema.getCabecera().getTipoCuestionario()); // Formato PYMES o Abreviado
			boolean llevaMemoria = schema.getCabecera().isMemoriaNormalizada();  // Indica si lleva memoria normalizada
			
            // Llenamos los espacios de nombres e inicializamos algunas variables, según el formato
            String uriSchema;
            Map<String,String> nameSpacesUri;
            Map<String,String> datosConversionBal;
            Map<String,String> datosConversionPyG;
            Map<String,String> datosConversionMem;
            Map<String,String[]> contextosMem;
            Map<String,String[]> datosConversionMemCuadros;
			if (formatoPymes) {
				// PYMES
	            uriSchema = llevaMemoria ? URI_SCHEMA_PYMES_MEMORIA : URI_SCHEMA_PYMES;
	            nameSpacesUri = llevaMemoria ? NAME_SPACES_URI_PYMES_MEMORIA : NAME_SPACES_URI_PYMES;
	            datosConversionBal = CONVERSION_BAL_PYMES;
	            datosConversionPyG = CONVERSION_PYG_PYMES;
	            datosConversionMem = CONVERSION_MEM_PYMES;
	            contextosMem = CONTEXTOS_MEM_PYMES;
	            datosConversionMemCuadros = CONVERSION_MEM_CUADROS_PYMES;
			} else {
				// ABREVIADO
	            uriSchema = llevaMemoria ? URI_SCHEMA_ABREVIADO_MEMORIA : URI_SCHEMA_ABREVIADO;
	            nameSpacesUri = llevaMemoria ? NAME_SPACES_URI_ABREVIADO_MEMORIA : NAME_SPACES_URI_ABREVIADO;
	            datosConversionBal = CONVERSION_BAL_ABREVIADO;
	            datosConversionPyG = CONVERSION_PYG_ABREVIADO;
	            datosConversionMem = CONVERSION_MEM_ABREVIADO;
	            contextosMem = CONTEXTOS_MEM_ABREVIADO;
	            datosConversionMemCuadros = CONVERSION_MEM_CUADROS_ABREVIADO;
			}
			
			boolean llevaAnterior = (fechaIniAnterior != null && fechaFinAnterior != null);
        	
            // Crear una instancia de DocumentBuilderFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Crear un nuevo documento XML
            Document document = builder.newDocument();
            document.setXmlStandalone(true);
            
            // Crear la raiz  
            document.appendChild(document.createElement("xbrli:xbrl"));  
          
            // Añadir atributos xmlns a la raiz
            for (String nsu : nameSpacesUri.keySet()) {
            	document.getDocumentElement().setAttribute(nsu, nameSpacesUri.get(nsu));
            }

	  		// Añadir schemaRef
	        Element ele = document.createElement("link:schemaRef");  
	        ele.setAttribute("xlink:type", "simple"); 
	        ele.setAttribute("xlink:href", uriSchema);
	        document.getDocumentElement().appendChild(ele);
		
			// Añadir contextos I_ACTUAL y D_ACTUAL
			addContext(document, I_ACTUAL, nifEmpresa, fechaFinActual);
			addContext(document, D_ACTUAL, nifEmpresa, fechaIniActual, fechaFinActual);
			
			// Añadir contextos I_ANTERIOR y D_ANTERIOR
			if (llevaAnterior) {
		        addContext(document, I_ANTERIOR, nifEmpresa, fechaFinAnterior);
		        addContext(document, D_ANTERIOR, nifEmpresa, fechaIniAnterior, fechaFinAnterior);
		    }
			
			if (llevaMemoria) {
				// Añadir contextos para los cuadros normalizados de la memoria
				for (String id : contextosMem.keySet()) {
					String dimension = contextosMem.get(id)[0];
					String member = contextosMem.get(id)[1];
					if (id.contains("_ACTUAL_") && id.contains("_Instant_"))
						addContext(document, id, nifEmpresa, fechaFinActual, dimension, member);
					else if (id.contains("_ACTUAL_") && id.contains("_Duration_"))
						addContext(document, id, nifEmpresa, fechaIniActual, fechaFinActual, dimension, member);
					else if (id.contains("_ANTERIOR_") && id.contains("_Instant_") && llevaAnterior)
						addContext(document, id, nifEmpresa, fechaFinAnterior, dimension, member);
					else if (id.contains("_ANTERIOR_") && id.contains("_Duration_") && llevaAnterior)
						addContext(document, id, nifEmpresa, fechaIniAnterior, fechaFinAnterior, dimension, member);
				}
			}
		
	        // Añadir elementos "Unit"
	        addUnit(document, "euro", "iso4217:EUR");
	        addUnit(document, "shares", "xbrli:shares");
	        addUnit(document, "pure", "xbrli:pure");
          
	        // Páginas de Identificación 
	        addIdentificacion(document, fechaIniActual, fechaFinActual, fechaIniAnterior, fechaFinAnterior, formatoPymes);
	        
	        // Balance de situación
	        for (String key : datosConversionBal.keySet()) {
	        	addElementoBal(document.getDocumentElement(), key, datosConversionBal.get(key), I_ACTUAL, llevaAnterior ? I_ANTERIOR : null);
	        }
	        
	        // Cuenta de Perdidas y Ganancias
	        for (String key : datosConversionPyG.keySet()) {
	        	addElementoBal(document.getDocumentElement(), key, datosConversionPyG.get(key), D_ACTUAL, llevaAnterior ? D_ANTERIOR : null);
	        }
	        
	        // Notas de la memoria
	        addNotasMemoria(document);
	        
	        if (llevaMemoria) {
		        // Memoria apartados texto libre
		        for (String key : datosConversionMem.keySet()) {
		        	addElementoIde(document.getDocumentElement(), datosConversionMem.get(key), D_ACTUAL, key);
		        }
		        
		        // Memoria cuadros normalizados
		        for (String key : datosConversionMemCuadros.keySet()) {
		        	String name = datosConversionMemCuadros.get(key)[0];
		        	String context = datosConversionMemCuadros.get(key)[1];
		        	addElementoMem(document.getDocumentElement(), key, name, context);
		        }
		        
		        // Memoria cuadro normalizado apartado 10 (Otra información)
		    	addElementoIde(document.getDocumentElement(), "dgi-eco-bas:EmployeesNumberTotal", D_ACTUAL, "98007", "pure", "2");
		    	if (llevaAnterior)
		    		addElementoIde(document.getDocumentElement(), "dgi-eco-bas:EmployeesNumberTotal", D_ANTERIOR, "980079", "pure", "2");
	        }
        
            // Escribir el contenido del documento en un archivo XML
//	        File f = File.createTempFile("deposito", ".xbrl", parent);
//	        File f = new File(parent.getPath() + "\\deposito.xbrl");
//	        File f = new File("c:\\tmp\\deposito.xbrl");	        
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(parent, "deposito.xbrl"));
            transformer.transform(source, result);
            System.out.println("Archivo XBRL creado con éxito!");

        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

	private static Date buscarFecha(String key, String sufijo) {
		
		String y = buscar(key + "1" + sufijo); // Año
		String m = buscar(key + "2" + sufijo); // Mes
		String d = buscar(key + "3" + sufijo); // Día
		
		if (AonStringUtils.isNotBlank(y) && AonStringUtils.isNotBlank(m) && AonStringUtils.isNotBlank(d)) {
			try {
				return DATE_FORMAT.parse(y + "-" + m + "-" + d);
			} catch (ParseException e) {
				// FALTA - CONTROLAR EXCEPCION O DEVOLVER AL CLIENTE
				e.printStackTrace();
				return null;
			}			
		} else {
			return null;
		}
	}

	private static void addContext(Document doc, String id, String identifier, Date fechaFin) {
    	addContext(doc, id, identifier, null, fechaFin, null, null);
    }
    
    private static void addContext(Document doc, String id, String identifier, Date fechaIni, Date fechaFin) {
    	addContext(doc, id, identifier, fechaIni, fechaFin, null, null);
    }
    
    private static void addContext(Document doc, String id, String identifier, Date fechaFin, String dimension, String member) {
    	addContext(doc, id, identifier, null, fechaFin, dimension, member);
    }
    
    private static void addContext(Document doc, String id, String identifier, Date fechaIni, Date fechaFin, String dimension, String member) {
    	
	      Element ele;
	      ele = doc.createElement("xbrli:context");
	      ele.setAttribute("id", id);
	
	      Element ele2;
	      ele2 = doc.createElement("xbrli:entity");
	
	      Element ele3;
	      ele3 = doc.createElement("xbrli:identifier");
	      ele3.setAttribute("scheme", "http://www.icac.meh.es/xbrl");
	      ele3.setTextContent(identifier);  
	
	      ele2.appendChild(ele3);
	      ele.appendChild(ele2);
	
	      ele2 = doc.createElement("xbrli:period");
	      if (fechaIni == null) {
	          // Periodo instant si no lleva fecha inicio
	          ele3 = doc.createElement("xbrli:instant");
	          ele3.setTextContent(DATE_FORMAT.format(fechaFin));  // Final del ejercicio
	          ele2.appendChild(ele3);
	      } else {
	          // Periodo duration si lleva fecha ini y fecha fin
	          ele3 = doc.createElement("xbrli:startDate");
	          ele3.setTextContent(DATE_FORMAT.format(fechaIni));  // Inicio del ejercicio
	          ele2.appendChild(ele3);
	
	          ele3 = doc.createElement("xbrli:endDate");
	          ele3.setTextContent(DATE_FORMAT.format(fechaFin));  // Final del ejercicio
	          ele2.appendChild(ele3);
	      }
	      ele.appendChild(ele2);
	      
	      // Elemento scenario, se utiliza para crear los contextos de los cuadros normalizados de la memoria
		  if (AonStringUtils.isNotBlank(dimension)) {
	          ele2 = doc.createElement("xbrli:scenario");
	          ele.appendChild(ele2);
	
	          ele3 = doc.createElement("xbrldi:explicitMember");
	          ele3.setAttribute("dimension", dimension);
	          ele3.setTextContent(member);
	          ele2.appendChild(ele3);
		  }
	      
	      doc.getDocumentElement().appendChild(ele);
    	
    }
    
    private static void addUnit(Document doc, String id, String measure) {
    	
        Element ele;
        ele = doc.createElement("xbrli:unit");
        ele.setAttribute("id", id);

        Element ele2;
        ele2 = doc.createElement("xbrli:measure");
        ele2.setTextContent(measure);  

        ele.appendChild(ele2);

        doc.getDocumentElement().appendChild(ele);
        
    }
    
    // Añadir elemento: Balance y Cuenta PYG
    private static void addElementoBal(Element padre, String key, String nombre, String contextoActual, String contextoAnterior) {
    	
		// Ejercicio Actual
		String valor = buscar(key); // Se busca la clave en el XML y se obtiene el valor
		
		// Comprobar si lleva Nota de la Memoria
		String nota = buscar(key + "98"); // Se busca la clave de la nota en el XML (clave+"98")
		String idNota = "";
		if (AonStringUtils.isNotBlank(nota)) {
			// Añadimos la nota al array de las notas porque se pondrá al final del XBRL
			idNota = "id_foodnote_" + notas.size();
			notas.add(nota);
		}

		if (AonStringUtils.isNotBlank(valor) || AonStringUtils.isNotBlank(nota)) {
			addElemento(padre, nombre, contextoActual, valor, "2", "euro", idNota); // Añadir el elemento actual	
		}

		// Añadir el elemento anterior
		if (AonStringUtils.isNotBlank(contextoAnterior)) {
			valor = buscar(key + "9"); // Se busca en el XML (clave + "9")
			if (AonStringUtils.isNotBlank(valor)) {
				addElemento(padre, nombre, contextoAnterior, valor, "2", "euro", null); // Añadir el elemento anterior	
			}
		}

    }
    
    // Añadir elemento: Cuadros normalizados de la memoria
    private static void addElementoMem(Element padre, String key, String name, String context) {

    	addElementoIde(padre, name, context, key, "euro", "2");
    	
	}
    
    // Busca la clave que se le pasa en el XML y devuelve su valor  
    private static String buscar(String key) {
    	
    	// Buscar en el XML (PARA PROBAR BUSCAMOS EN datosIdentificacion)
		for (int i = 0; i < schemaXml.getClaves().getClave().size(); i++) {
			BigInteger code = new BigInteger(key);
			if (schemaXml.getClaves().getClave().get(i).getCodigo().equals(code)) {				
				return schemaXml.getClaves().getClave().get(i).getValor();
			}
		}
		return null;
		
    }
    
    // Añadir elemento buscando previamente en el XML
    private static void addElementoIde(Element padre, String nombre, String contexto, String key) {
    	addElementoIde(padre, nombre, contexto, key, null, null);
    }
    private static void addElementoIde(Element padre, String nombre, String contexto, String key, String unit, String decimals) {

    	// Se busca la clave en el XML y si contiene datos se añade el elemento
    	String valor = buscar(key); 
    	if (AonStringUtils.isNotBlank(valor)) {
    		addElemento(padre, nombre, contexto, valor, decimals, unit, null);
    	}
    	
    }
    
    private static Element addElemento(Element padre, String nombre) {
    	return addElemento(padre, nombre, null, null, null, null, null);
    }
    
    private static Element addElemento(Element padre, String nombre, String contexto, String valor) {
    	return addElemento(padre, nombre, contexto, valor, null, null, null);
    }
    
    private static Element addElemento(Element padre, String nombre, String contexto, String valor, String decimals, String unitRef, String idNota) {
        Element ele = padre.getOwnerDocument().createElement(nombre);
        if (AonStringUtils.isNotBlank(contexto)) {
            ele.setAttribute("contextRef", contexto.toString());
        }
        if (AonStringUtils.isNotBlank(decimals)) {
            ele.setAttribute("decimals", decimals);
        }
        if (AonStringUtils.isNotBlank(unitRef)) {
            ele.setAttribute("unitRef", unitRef);
        }
        if (AonStringUtils.isNotBlank(idNota)) {
            ele.setAttribute("id", idNota);
        }
        if (AonStringUtils.isNotBlank(valor)) {
            ele.setTextContent(valor);
        }
        padre.appendChild(ele);
        return ele;
    }
    
    private static void addNotasMemoria(Document doc) {
        // Bloque para poner todas las notas
        //<link:footnoteLink xlink:role="http://www.xbrl.org/2003/role/link" xlink:type="extended">
        //    <link:loc xlink:href="#id_footnote_elem_b11cea7a-c259-42f3-8203-6bfccd196368" xlink:label="PerdidasGananciasOperacionesContinuadasImporteNetoCifraNegocios_b11cea7a-c259-42f3-8203-6bfccd196368" xlink:type="locator"/>
        //    <link:footnoteArc order="1" xlink:arcrole="http://www.xbrl.org/2003/arcrole/fact-footnote" xlink:from="PerdidasGananciasOperacionesContinuadasImporteNetoCifraNegocios_b11cea7a-c259-42f3-8203-6bfccd196368" xlink:title="" xlink:to="footnote_ad908b82-93e6-452d-ab89-f00150434371" xlink:type="arc"/>
        //    <link:footnote xlink:label="footnote_ad908b82-93e6-452d-ab89-f00150434371" xlink:role="http://www.xbrl.org/2003/role/footnote" xlink:type="resource" xml:lang="es">5 y 6     </link:footnote>
        //</link:footnoteLink>
        if (notas != null && !notas.isEmpty()) {
            Element ele = doc.createElement("link:footnoteLink");
            ele.setAttribute("type", "extended");

            for (int i = 0; i < notas.size(); i++) {
                // Por cada nota ponemos 3 lineas 
                //    <link:loc xlink:href="#id_footnote_elem_b11cea7a-c259-42f3-8203-6bfccd196368" xlink:label="PerdidasGananciasOperacionesContinuadasImporteNetoCifraNegocios_b11cea7a-c259-42f3-8203-6bfccd196368" xlink:type="locator"/>
                //    <link:footnoteArc order="1" xlink:arcrole="http://www.xbrl.org/2003/arcrole/fact-footnote" xlink:from="PerdidasGananciasOperacionesContinuadasImporteNetoCifraNegocios_b11cea7a-c259-42f3-8203-6bfccd196368" xlink:title="" xlink:to="footnote_ad908b82-93e6-452d-ab89-f00150434371" xlink:type="arc"/>
                //    <link:footnote xlink:label="footnote_ad908b82-93e6-452d-ab89-f00150434371" xlink:role="http://www.xbrl.org/2003/role/footnote" xlink:type="resource" xml:lang="es">5 y 6     </link:footnote>
                String id = "#id_foodnote_" + i;
                String labelFrom = "label_" + i;
                String labelTo = "foodnote_" + i;

                Element ele2 = doc.createElement("link:loc");
                ele2.setAttribute("xlink:href", id);
                ele2.setAttribute("xlink:label", labelFrom);
                ele2.setAttribute("xlink:type", "locator");
                ele.appendChild(ele2);

                //    <link:footnoteArc order="1" xlink:arcrole="http://www.xbrl.org/2003/arcrole/fact-footnote" xlink:from="label_40100" xlink:title="" xlink:to="footnote_40100" xlink:type="arc"/>
                ele2 = doc.createElement("link:footnoteArc");
                ele2.setAttribute("order", "1");
                ele2.setAttribute("xlink:arcrole", "http://www.xbrl.org/2003/arcrole/fact-footnote");
                ele2.setAttribute("xlink:from", labelFrom);
                ele2.setAttribute("xlink:title", "");
                ele2.setAttribute("xlink:to", labelTo);
                ele2.setAttribute("xlink:type", "arc");
                ele.appendChild(ele2);

                //    <link:footnote xlink:label="footnote_40100" xlink:role="http://www.xbrl.org/2003/role/footnote" xlink:type="resource" xml:lang="es">5 y 6     </link:footnote>
                ele2 = doc.createElement("link:footnote");
                ele2.setAttribute("xlink:label", labelTo);
                ele2.setAttribute("xlink:role", "http://www.xbrl.org/2003/role/footnote");
                ele2.setAttribute("xlink:type", "resource");
                ele2.setAttribute("xml:lang", "es");  // xml:lang="es"
                ele2.setTextContent(notas.get(i));
                ele.appendChild(ele2);
            }
            doc.getDocumentElement().appendChild(ele);
        }
    }
    
    // Hojas Identificacion
    private static void addIdentificacion(Document doc, Date fechaIniActual, Date fechaFinActual, Date fechaIniAnterior, Date fechaFinAnterior, boolean formatoPymes) {
    	
        Element ele;
        Element ele2;
        Element ele3;

        // Identificación de la Empresa
        ele = addElemento(doc.getDocumentElement(), "pgc07mc-apdo0:IdentificacionEmpresaTupla");
        addElemento(ele, "dgi-lc-es:Xcode_IDC.NIF", D_ACTUAL, "NIF");
        addElementoIde(ele, "dgi-est-gen:IdentifierValue", D_ACTUAL, "1010"); // NIF
        if ("1".equals(buscar("1011")))
        	addElemento(ele, "dgi-lc-es:Xcode_LFC.001", D_ACTUAL, "001");     // Forma juridica SA // FALTA - EN EL XML VA 0 O 1
        if ("1".equals(buscar("1012")))
        	addElemento(ele, "dgi-lc-es:Xcode_LFC.023", D_ACTUAL, "023");     // Forma juridica SL // FALTA - EN EL XML VA 0 O 1               
        addElementoIde(ele, "dgi-gen-ex:OthersLegalForm", D_ACTUAL, "1013");  // Forma juridica Otras
        addElementoIde(ele, "dgi-lc-es:Xcode_IDC.LEI", D_ACTUAL, "1009");     // LEI
        addElemento(ele, "dgi-lc-es:Xcode_NMT.DS", D_ACTUAL, "DS");
        addElementoIde(ele, "dgi-est-gen:LegalNameValue", D_ACTUAL, "1020");  // Denominación Social
        addElemento(ele, "dgi-lc-es:Xcode_ADL.01", D_ACTUAL, "01");
        addElementoIde(ele, "dgi-est-gen:AddressLine", D_ACTUAL, "1022");               // Domicilio social
        addElementoIde(ele, "dgi-est-gen:MunicipalityName", D_ACTUAL, "1023");          // Municipio
//        addElementoIde(ele, "dgi-est-gen:SpecifyRegion", D_ACTUAL, "1025");             // Provincia // FALTA EN EL XML EN EL 1025 VA LA CLAVE DE PROVINCIA
        addElementoIde(ele, "dgi-lc-es:Xcode_RCI." + buscar("1025"), D_ACTUAL, "1025"); // Provincia (se guarda el código de provincia en el XML)
        addElementoIde(ele, "dgi-est-gen:ZipPostalCode", D_ACTUAL, "1024");             // Código postal
        addElementoIde(ele, "dgi-est-gen:CommunicationValue", D_ACTUAL, "1031");        // Teléfono
        addElementoIde(ele, "pgc07mc-apdo0:CommunicationValueEMail", D_ACTUAL, "1037"); // Dirección email

        // Unidades
        if (formatoPymes) {
        	// Unidades (en PYMES siempre Euros) 
            addElemento(doc.getDocumentElement(), "dgi-lc-es:Xcode_COT.01", D_ACTUAL, "01"); // UNIDAD EUROS
        } else {
        	// Unidades (en ABREVIADO Euros, Miles de Euros, Millones de Euros)
        	// FALTA - POR AHORA SE PONE EUROS
        	// ABREVIADO: EN AON SE PUEDE SELECCIONAR LA UNIDAD (EUROS, MILES DE EUROS, MILLONES DE EUROS)
            ele = addElemento(doc.getDocumentElement(), "pgc07mc-apdo0:UnidadesTupla");
            if (buscar("9002").equals("1"))
            	addElemento(ele, "dgi-lc-es:Xcode_COT.02", D_ACTUAL, "02"); // MILES DE EUROS
            else if (buscar("9003").equals("1"))
            	addElemento(ele, "dgi-lc-es:Xcode_COT.03", D_ACTUAL, "03"); // MILLONES DE EUROS
            else	
            	addElemento(ele, "dgi-lc-es:Xcode_COT.01", D_ACTUAL, "01"); // UNIDAD EUROS
        }
        
        // Pertenencia a Grupo de Sociedades (solo Abreviado)
        if (!formatoPymes)
        {
            ele = addElemento(doc.getDocumentElement(), "pgc07mc-apdo0:PertenenciaGrupoSociedadesTupla");
            ele2 = addElemento(ele, "pgc07mc-apdo0:SociedadesDominanteDirectaTupla");
            addElemento(ele2, "dgi-lc-es:Xcode_PET.03", D_ACTUAL, "03"); 
            ele3 = addElemento(ele2, "pgc07mc-apdo0:IdentificacionSociedadTupla");
            addElemento(ele3, "dgi-lc-es:Xcode_IDC.NIF", D_ACTUAL, "NIF");
            addElementoIde(ele3, "dgi-est-gen:IdentifierValue", D_ACTUAL, "1040");
            addElemento(ele3, "dgi-lc-es:Xcode_NMT.DS", D_ACTUAL, "DS");
            addElementoIde(ele3, "dgi-est-gen:LegalNameValue", D_ACTUAL, "1041");
            ele2 = addElemento(ele, "pgc07mc-apdo0:SociedadesDominanteUltimaTupla");
            addElemento(ele2, "dgi-lc-es:Xcode_PET.04", D_ACTUAL, "04"); 
            ele3 = addElemento(ele2, "pgc07mc-apdo0:IdentificacionSociedadTupla");
            addElemento(ele3, "dgi-lc-es:Xcode_IDC.NIF", D_ACTUAL, "NIF");
            addElementoIde(ele3, "dgi-est-gen:IdentifierValue", D_ACTUAL, "1060");
            addElemento(ele3, "dgi-lc-es:Xcode_NMT.DS", D_ACTUAL, "DS");
            addElementoIde(ele3, "dgi-est-gen:LegalNameValue", D_ACTUAL, "1061");
        }

        // Actividad 
        ele = addElemento(doc.getDocumentElement(), "pgc07mc-apdo0:ActividadTupla");
        addElementoIde(ele, "dgi-eco-bas:ActivityDescription", D_ACTUAL, "2009");  // Actividad: Descripción
        ele = addElemento(ele, "dgi-eco-bas:ActivityCodeCNAE2009");
        addElementoIde(ele, "dgi-cnae-09:Xcode_ACC.CNAE." + buscar("2001"), D_ACTUAL, "2001"); // Actividad: CNAE

        // Personal Asalariado - Ejercicio Actual
        ele = addElemento(doc.getDocumentElement(), "pgc07mc-apdo0:PersonalAsalariadoTupla");
        ele2 = addElemento(ele, "pgc07mc-apdo0:NumeroMediopersonasEmpleadasCursoEjercicioTipoContratoEmpleoDiscapacidad");
        addElementoIde(ele2, "dgi-eco-bas:PermanentContractTotal", D_ACTUAL, "4001", "pure", "2");
        addElementoIde(ele2, "dgi-eco-bas:TemporaryContractTotal", D_ACTUAL, "4002", "pure", "2");
        addElementoIde(ele2, "dgi-eco-bas:EmployeesDisabilityLevelHigher33Total", D_ACTUAL, "4010", "pure", "2");
        ele2 = addElemento(ele, "pgc07mc-apdo0:PersonalAsalariadoTerminoEjercicioTipoContratoSexo");
        ele3 = addElemento(ele2, "pgc07mc-apdo0:PersonalAsalariadoTerminoEjercicioTipoContratoFijo");
        addElementoIde(ele3, "dgi-eco-bas:Male", D_ACTUAL, "4120", "pure", "0");
        addElementoIde(ele3, "dgi-eco-bas:Female", D_ACTUAL, "4121", "pure", "0");
        ele3 = addElemento(ele2, "pgc07mc-apdo0:PersonalAsalariadoTerminoEjercicioTipoContratoNoFijo");
        addElementoIde(ele3, "dgi-eco-bas:Male", D_ACTUAL, "4122", "pure", "0");
        addElementoIde(ele3, "dgi-eco-bas:Female", D_ACTUAL, "4123", "pure", "0");

        // Personal Asalariado - Ejercicio Anterior
        if (fechaIniAnterior != null && fechaFinAnterior != null) {
	        ele = addElemento(doc.getDocumentElement(), "pgc07mc-apdo0:PersonalAsalariadoTupla");
	        ele2 = addElemento(ele, "pgc07mc-apdo0:NumeroMediopersonasEmpleadasCursoEjercicioTipoContratoEmpleoDiscapacidad");
	        addElementoIde(ele2, "dgi-eco-bas:PermanentContractTotal", D_ANTERIOR, "40019", "pure", "2");
	        addElementoIde(ele2, "dgi-eco-bas:TemporaryContractTotal", D_ANTERIOR, "40029", "pure", "2");
	        addElementoIde(ele2, "dgi-eco-bas:EmployeesDisabilityLevelHigher33Total", D_ANTERIOR, "40109", "pure", "2");
	        ele2 = addElemento(ele, "pgc07mc-apdo0:PersonalAsalariadoTerminoEjercicioTipoContratoSexo");
	        ele3 = addElemento(ele2, "pgc07mc-apdo0:PersonalAsalariadoTerminoEjercicioTipoContratoFijo");
	        addElementoIde(ele3, "dgi-eco-bas:Male", D_ANTERIOR, "41209", "pure", "0");
	        addElementoIde(ele3, "dgi-eco-bas:Female", D_ANTERIOR, "41219", "pure", "0");
	        ele3 = addElemento(ele2, "pgc07mc-apdo0:PersonalAsalariadoTerminoEjercicioTipoContratoNoFijo");
	        addElementoIde(ele3, "dgi-eco-bas:Male", D_ANTERIOR, "41229", "pure", "0");
	        addElementoIde(ele3, "dgi-eco-bas:Female", D_ANTERIOR, "41239", "pure", "0");
        }

        // Presentación de Cuentas - Ejercicio Actual
        ele = addElemento(doc.getDocumentElement(), "pgc07mc-apdo0:PresentacionCuentasTupla");
        addElemento(ele, "pgc07mc-apdo0:YearFechaInicioCuentas", D_ACTUAL, DATE_FORMAT_YEAR.format(fechaIniActual));
        addElemento(ele, "pgc07mc-apdo0:MonthFechaInicioCuentas", D_ACTUAL, DATE_FORMAT_MONTH.format(fechaIniActual));
        addElemento(ele, "pgc07mc-apdo0:DayFechaInicioCuentas", D_ACTUAL, DATE_FORMAT_DAY.format(fechaIniActual));
        addElemento(ele, "pgc07mc-apdo0:YearFechaCierreCuentas", D_ACTUAL, DATE_FORMAT_YEAR.format(fechaFinActual));
        addElemento(ele, "pgc07mc-apdo0:MonthFechaCierreCuentas", D_ACTUAL, DATE_FORMAT_MONTH.format(fechaFinActual));
        addElemento(ele, "pgc07mc-apdo0:DayFechaCierreCuentas", D_ACTUAL, DATE_FORMAT_DAY.format(fechaFinActual));
        addElementoIde(ele, "dgi-dat-inf:TotalPagesPresented", D_ACTUAL, "1901", "pure", "0");
        addElementoIde(ele, "dgi-dat-inf:AbsenceFinancialStatementPurpose", D_ACTUAL, "1903");

        // Presentación de Cuentas - Ejercicio Anterior
        if (fechaIniAnterior != null && fechaFinAnterior != null) {
            ele = addElemento(doc.getDocumentElement(), "pgc07mc-apdo0:PresentacionCuentasTupla");
            addElemento(ele, "pgc07mc-apdo0:YearFechaInicioCuentas", D_ANTERIOR, DATE_FORMAT_YEAR.format(fechaIniAnterior));
            addElemento(ele, "pgc07mc-apdo0:MonthFechaInicioCuentas", D_ANTERIOR, DATE_FORMAT_MONTH.format(fechaIniAnterior));
            addElemento(ele, "pgc07mc-apdo0:DayFechaInicioCuentas", D_ANTERIOR, DATE_FORMAT_DAY.format(fechaIniAnterior));
            addElemento(ele, "pgc07mc-apdo0:YearFechaCierreCuentas", D_ANTERIOR, DATE_FORMAT_YEAR.format(fechaFinAnterior));
            addElemento(ele, "pgc07mc-apdo0:MonthFechaCierreCuentas", D_ANTERIOR, DATE_FORMAT_MONTH.format(fechaFinAnterior));
            addElemento(ele, "pgc07mc-apdo0:DayFechaCierreCuentas", D_ANTERIOR, DATE_FORMAT_DAY.format(fechaFinAnterior));                
        }

        if (formatoPymes) {
            // Microempresas (Solo PYMES)
            addElementoIde(doc.getDocumentElement(), "pgc07mp-apdo0:CasoAdopcionConjuntaCrteriosEspecificosMicroPyme", D_ACTUAL, "1902"); // ) == "S" ? "true" : "false");
        }
        
        // Número de mujeres y miembros en organo de administración
        addElementoIde(doc.getDocumentElement(), "dgi-eco-bas:NumeroMujeresOrganoAdministracion", D_ACTUAL, "4212", "pure", "0");
        addElementoIde(doc.getDocumentElement(), "dgi-eco-bas:NumeroTotalMiembrosOrganoAdministracion", D_ACTUAL, "4213", "pure", "0");
        
        if (fechaIniAnterior != null && fechaFinAnterior != null) {
            addElementoIde(doc.getDocumentElement(), "dgi-eco-bas:NumeroMujeresOrganoAdministracion", D_ANTERIOR, "42129", "pure", "0");
            addElementoIde(doc.getDocumentElement(), "dgi-eco-bas:NumeroTotalMiembrosOrganoAdministracion", D_ANTERIOR, "42139", "pure", "0");
        }
                
        // Aplicación de Resultados y Periodo Medio de Pago a Proveedores - Ejercicio Actual (Abreviado y PYMES)
        addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosBaseRepartoResultadoSaldoCuentaPerdidasGanancias", D_ACTUAL, "91000", "euro", "2");
        addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosBaseRepartoResultadoRemanente", D_ACTUAL, "91001", "euro", "2");
        addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosBaseRepartoResultadoReservasVoluntarias", D_ACTUAL, "91002", "euro", "2");
        addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosRepartoOtrasReservasLibreDisposicion", D_ACTUAL, "91003", "euro", "2");
        addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosBaseTotalBaseRepartoResultadoTotalAplicacion", D_ACTUAL, "91004", "euro", "2");
        addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaResultadoAplicacionAReservaLegal", D_ACTUAL, "91005", "euro", "2");
        addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionAReservasEspeciales", D_ACTUAL, "91007", "euro", "2");
        addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionAReservasVoluntarias", D_ACTUAL, "91008", "euro", "2");
        addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionADividendos", D_ACTUAL, "91009", "euro", "2");
        addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionARemanenteYOtros", D_ACTUAL, "91010", "euro", "2");
        addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionACompensacionPerdidasEjerciciosAnteriores", D_ACTUAL, "91011", "euro", "2");
        addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionATotalAplicacionTotalBaseReparto", D_ACTUAL, "91012", "euro", "2");
        addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:PeriodoMedioPagoPeriodoMedioPagoProveedores", D_ACTUAL, "94705", "euro", "2");

        // Aplicación de Resultados y Periodo Medio de Pago a Proveedores - Ejercicio Anterior (Abreviado y PYMES)
        if (fechaIniAnterior != null && fechaFinAnterior != null) {
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosBaseRepartoResultadoSaldoCuentaPerdidasGanancias", D_ANTERIOR, "910009", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosBaseRepartoResultadoRemanente", D_ANTERIOR, "910019", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosBaseRepartoResultadoReservasVoluntarias", D_ANTERIOR, "910029", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosRepartoOtrasReservasLibreDisposicion", D_ANTERIOR, "910039", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosBaseTotalBaseRepartoResultadoTotalAplicacion", D_ANTERIOR, "910049", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaResultadoAplicacionAReservaLegal", D_ANTERIOR, "910059", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionAReservasEspeciales", D_ANTERIOR, "910079", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionAReservasVoluntarias", D_ANTERIOR, "910089", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionADividendos", D_ANTERIOR, "910099", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionARemanenteYOtros", D_ANTERIOR, "910109", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionACompensacionPerdidasEjerciciosAnteriores", D_ANTERIOR, "910119", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionATotalAplicacionTotalBaseReparto", D_ANTERIOR, "910129", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:PeriodoMedioPagoPeriodoMedioPagoProveedores", D_ANTERIOR, "947059", "euro", "2");
        }

    }
    
    public static void main(String[] args) {
//    	pruebaCrearXBRL();
    }

}
