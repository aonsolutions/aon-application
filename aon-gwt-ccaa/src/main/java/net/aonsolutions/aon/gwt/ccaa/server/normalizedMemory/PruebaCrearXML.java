package net.aonsolutions.aon.gwt.ccaa.server.normalizedMemory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PruebaCrearXML {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat DATE_FORMAT_YEAR = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat DATE_FORMAT_MONTH = new SimpleDateFormat("MM");
	private static final SimpleDateFormat DATE_FORMAT_DAY = new SimpleDateFormat("dd");
	
	private static Map<String,String> nameSpaceUri = new HashMap<>();  // Espacios de Nombres utilizados
	private static Map<String,String> datosConversion = new HashMap<>();  // Conversión de Casillas a Nombres XBRL (Balance, Cuenta de PyG)
	private static ArrayList<String> notas = new ArrayList<>(); // Notas de la Memoria
	
	private static Map<String,String> datosIdentificacion = new HashMap<>();  // Datos Identificacion, esto solo es para probar, pues esto se buscara en el XML que se graba en rattach
	
	static {
		datosIdentificacion.put("01010", "B50111111"); // NIF
//		datosIdentificacion.put("01011", ""); // Forma juridica SA
//		datosIdentificacion.put("01012", ""); // Forma juridica SL 
		datosIdentificacion.put("01013", "SOC.COOP."); // Forma juridida OTRAS
		datosIdentificacion.put("01009", "CODIGO LEI"); // LEI
		datosIdentificacion.put("01020", "DENOMINACION SOCIAL"); // Denominación social
		datosIdentificacion.put("01022", "DOMICILIO SOCIAL"); // Domicilio social
		datosIdentificacion.put("01023", "MUNICIPIO"); // Municipio
		datosIdentificacion.put("01025", "ZARAGOZA"); // Provincia
		datosIdentificacion.put("01024", "50001"); // Código postal
		datosIdentificacion.put("01031", "976000000"); // Teléfono
		datosIdentificacion.put("01037", "info@mail.com"); // Direccion email
		datosIdentificacion.put("02009", "EDICION DE LIBROS"); // Actividad principal (nombre)
		datosIdentificacion.put("02001", "5811"); // Actividad principal (codigo CNAE)
		datosIdentificacion.put("01901", "35");
		datosIdentificacion.put("01903", "CAUSAS DE NO CONSIGNAR CIFRAS");
		
        datosIdentificacion.put("04001", "1"); 
        datosIdentificacion.put("04002", "2");  
        datosIdentificacion.put("04010", "10");
        datosIdentificacion.put("04120", "120");
        datosIdentificacion.put("04121", "121");
        datosIdentificacion.put("04122", "122");
        datosIdentificacion.put("04123", "123");
        datosIdentificacion.put("040019", "19");
        datosIdentificacion.put("040029", "29");
        datosIdentificacion.put("040109", "109");
        datosIdentificacion.put("041209", "209");
        datosIdentificacion.put("041219", "219");
        datosIdentificacion.put("041229", "229");
        datosIdentificacion.put("041239", "239");
        
        datosIdentificacion.put("04212", "4212");
        datosIdentificacion.put("04213", "4213");
        datosIdentificacion.put("042129", "42129");
        datosIdentificacion.put("042139", "42139");
        
        datosIdentificacion.put("01902","0");
        
        datosIdentificacion.put("91000", "91000");
        datosIdentificacion.put("91001", "91001");
        datosIdentificacion.put("91002", "91002");
        datosIdentificacion.put("91003", "91003");
        datosIdentificacion.put("91004", "91004");
        datosIdentificacion.put("91005", "91005");
        datosIdentificacion.put("91007", "91007");
        datosIdentificacion.put("91008", "91008");
        datosIdentificacion.put("91009", "91009");
        datosIdentificacion.put("91010", "91010");
        datosIdentificacion.put("91011", "91011");
        datosIdentificacion.put("91012", "91012");
        datosIdentificacion.put("94705", "94705");
        datosIdentificacion.put("910009", "91000.9");
        datosIdentificacion.put("910019", "91001.9");
        datosIdentificacion.put("910029", "91002.9");
        datosIdentificacion.put("910039", "91003.9");
        datosIdentificacion.put("910049", "91004.9");
        datosIdentificacion.put("910059", "91005.9");
        datosIdentificacion.put("910079", "91007.9");
        datosIdentificacion.put("910089", "91008.9");
        datosIdentificacion.put("910099", "91009.9");
        datosIdentificacion.put("910109", "91010.9");
        datosIdentificacion.put("910119", "91011.9");
        datosIdentificacion.put("910129", "91012.9");
        datosIdentificacion.put("947059", "94705.9");
        
		
	}
	
    enum Contexto 
    {
        I_ACTUAL,
        D_ACTUAL,
        I_ANTERIOR,
        D_ANTERIOR
    }
	
//    public static void crearPersonasXML() {
//        try {
//            // Crear una instancia de DocumentBuilderFactory
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//
//            // Crear un nuevo documento XML
//            Document document = builder.newDocument();
//
//            // Crear el elemento raíz
//            Element rootElement = document.createElement("Personas");
//            document.appendChild(rootElement);
//
//            // Crear un elemento hijo
//            Element persona = document.createElement("Persona");
//            rootElement.appendChild(persona);
//
//            // Agregar atributos y elementos a "Persona"
//            persona.setAttribute("id", "1");
//
//            Element nombre = document.createElement("Nombre");
//            nombre.appendChild(document.createTextNode("Felix"));
//            persona.appendChild(nombre);
//
//            Element edad = document.createElement("Edad");
//            edad.appendChild(document.createTextNode("30"));
//            persona.appendChild(edad);
//
//            // Escribir el contenido del documento en un archivo XML
//            TransformerFactory transformerFactory = TransformerFactory.newInstance();
//            Transformer transformer = transformerFactory.newTransformer();
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//
//            DOMSource source = new DOMSource(document);
//            StreamResult result = new StreamResult(new File("c:\\tmp\\personas.xml"));
//
//            transformer.transform(source, result);
//
//            System.out.println("Archivo XML creado con éxito!");
//
//        } catch (ParserConfigurationException | TransformerException e) {
//            e.printStackTrace();
//        }
//    }
    
    public static void pruebaCrearXBRL() {
        try {
            // Crear una instancia de DocumentBuilderFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            // Llenamos los espacios de nombres e inicializamos algunas variables
            String uriSchema = llenarNameSpaceUri();
//            string linkUri = ObtenerUri("link");
//            string xlinkUri = ObtenerUri("xlink");

            // LLenamos los datos para la conversion
            llenarDatosConversion();

            // Crear un nuevo documento XML
            Document document = builder.newDocument();
            document.setXmlStandalone(true);
            
            // Crear la raiz  
            document.appendChild(document.createElement("xbrli:xbrl"));  // Crear la raiz
            
//            document.AppendChild(document.createElementNS("http://www.xbrl.org/2003/instance", "xbrli:xbrl"));  // Crear la raiz
//          Element rootElement = document.createElementNS("http://www.xbrl.org/2003/instance","xbrli:xbrl");
////          rootElement.setPrefix("xbrli");
////          rootElement.SetAttribute("xmlns:"+nameSpace.Key, nameSpace.Value);
//          
//          rootElement.setAttribute("xmlns:dgi-cnae-09", "http://www.xbrl.org.es/es/2008/dgi/gp/lc-cnae-2009/2023-01-01");
//          rootElement.setAttribute("xmlns:ref", "http://www.xbrl.org/2004/ref");
//          rootElement.setAttribute("xmlns:pgc-07-roles", "http://www.icac.meh.es/es/fr/gaap/pgc07/roles/2023-01-01");
//          rootElement.setAttribute("xmlns:pgc07d-clase", "http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/comun/ClasesInstrumentosFinancieros/Dimension/2023-01-01");
//          rootElement.setAttribute("xmlns:link","http://www.xbrl.org/2003/linkbase");
//          rootElement.setAttribute("xmlns:xlink","http://www.w3.org/1999/xlink");
          
          // PlantillaPymesMemoria.xml
//          Element rootElement = document.createElement("xbrli:xbrl");
//          rootElement.setAttribute("xmlns:pgc07pymes","http://www.icac.meh.es/es/fr/gaap/pgc07/modelo-pymes/2024-01-01"); 
//          rootElement.setAttribute("xmlns:xbrldi","http://xbrl.org/2006/xbrldi" );
//          rootElement.setAttribute("xmlns:pgc07mp-apdo5","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/pymes/apartado5/ActivosFinancieros/2024-01-01"); 
//          rootElement.setAttribute("xmlns:pgc-07-p-bal","http://www.icac.meh.es/es/fr/gaap/pgc07/cuentas/pymes/balance/2024-01-01" );
//          rootElement.setAttribute("xmlns:dgi-gen-bas","http://www.xbrl.org.es/es/2008/dgi/gp/gen-bas/2024-01-01" );
//          rootElement.setAttribute("xmlns:pgc-07-c-ap","http://www.icac.meh.es/es/fr/gaap/pgc07/comun-abreviadopymes/2024-01-01"); 
//          rootElement.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance" );
//          rootElement.setAttribute("xmlns:pgc07mp","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/pymes/2024-01-01"); 
//          rootElement.setAttribute("xmlns:iso4217","http://www.xbrl.org/2003/iso4217" );
//          rootElement.setAttribute("xmlns:pgc07mp-d-sf","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/pymes/SituacionFiscal/Dimension/2024-01-01"); 
//          rootElement.setAttribute("xmlns:pgc07m-roles","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/roles/2024-01-01" );
//          rootElement.setAttribute("xmlns:pgc-07-c-bs","http://www.icac.meh.es/es/fr/gaap/pgc07/comun-base/2024-01-01" );
//          rootElement.setAttribute("xmlns:pgc07mp-apdo7","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/pymes/apartado7/FondosPropios/2024-01-01"); 
//          rootElement.setAttribute("xmlns:dgi-gen-ex","http://www.xbrl.org.es/es/2008/dgi/gp/gen-ex/2024-01-01" );
//          rootElement.setAttribute("xmlns:pgc07mp-apdo8","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/pymes/apartado8/SituacionFiscal/2024-01-01"); 
//          rootElement.setAttribute("xmlns:pgc07mp-apdo9","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/pymes/apartado9/OperacionesPartesVinculadas/2024-01-01"); 
//          rootElement.setAttribute("xmlns:xlink","http://www.w3.org/1999/xlink" );
//          rootElement.setAttribute("xmlns:pgc07mp-apdo2","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/pymes/apartado2/BasesPresentacionCuentasAnuales/2024-01-01"); 
//          rootElement.setAttribute("xmlns:pgc-07-ref","http://www.icac.meh.es/es/fr/gaap/pgc07/referenceParts/2024-01-01" );
//          rootElement.setAttribute("xmlns:dgi-rel","http://www.xbrl.org.es/es/2008/dgi/gp/rel/2024-01-01" );
//          rootElement.setAttribute("xmlns:pgc07mp-apdo6","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/pymes/apartado6/PasivosFinancieros/2024-01-01"); 
//          rootElement.setAttribute("xmlns:dgi-lc-int","http://www.xbrl.org.es/es/2008/dgi/gp/lc-int/2024-01-01" );
//          rootElement.setAttribute("xmlns:pgc07mc-apdo0","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/comun/apartado0/IdentificacionGeneral/2024-01-01"); 
//          rootElement.setAttribute("xmlns:link","http://www.xbrl.org/2003/linkbase" );
//          rootElement.setAttribute("xmlns:pgc07mp-apdo10","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/pymes/apartado10/OtraInformacion/2024-01-01"); 
//          rootElement.setAttribute("xmlns:dgi-eco-bas","http://www.xbrl.org.es/es/2008/dgi/gp/eco-bas/2024-01-01" );
//          rootElement.setAttribute("xmlns:dgi-dat-inf","http://www.xbrl.org.es/es/2008/dgi/gp/dat-inf/2024-01-01" );
//          rootElement.setAttribute("xmlns:xbrli","http://www.xbrl.org/2003/instance" );
//          rootElement.setAttribute("xmlns:pgc07mp-rsm","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/pymes/resumen/2024-01-01"); 
//          rootElement.setAttribute("xmlns:pgc-07-p-pyg","http://www.icac.meh.es/es/fr/gaap/pgc07/cuentas/pymes/PerdidasGanancias/2024-01-01"); 
//          rootElement.setAttribute("xmlns:pgc07d-reclsf","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/comun/ReclasificacionInstrumentosFinancieros/Dimension/2024-01-01"); 
//          rootElement.setAttribute("xmlns:pgc07mp-apdo4","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/pymes/apartado4/InmovilizadoMaterialIntangibleInversionesInmobiliarias/2024-01-01"); 
//          rootElement.setAttribute("xmlns:pgc07m-d-pv_2","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/comun/PartesVinculadas/Dimension/2024-01-01"); 
//          rootElement.setAttribute("xmlns:pgc07pm","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/ModeloPymes/2024-01-01" );
//          rootElement.setAttribute("xmlns:dgi-est-gen","http://www.xbrl.org.es/es/2008/dgi/gp/est-gen/2024-01-01" );
//          rootElement.setAttribute("xmlns:pgc07mc-bs","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/comun/base/2024-01-01"); 
//          rootElement.setAttribute("xmlns:pgc07m-d-pv","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/comun/PartesVinculadas-ap/Dimension/2024-01-01"); 
//          rootElement.setAttribute("xmlns:pgc07d-plzven","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/comun/PlazoVencimientoInstrumentosFinancieros/Dimension/2024-01-01"); 
//          rootElement.setAttribute("xmlns:xbrldt","http://xbrl.org/2005/xbrldt" );
//          rootElement.setAttribute("xmlns:pgc07p","http://www.icac.meh.es/es/fr/gaap/pgc07/cuentas/pymes/2024-01-01"); 
//          rootElement.setAttribute("xmlns:pgc07mp-apdo3","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/pymes/apartado3/NormasRegistroValoracion/2024-01-01"); 
//          rootElement.setAttribute("xmlns:pgc-07-p","http://www.icac.meh.es/es/fr/gaap/pgc07/pymes/2024-01-01" );
//          rootElement.setAttribute("xmlns:dgi-lc-es","http://www.xbrl.org.es/es/2008/dgi/gp/lc-es/2024-01-01" );
//          rootElement.setAttribute("xmlns:pgc07mp-d-inm","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/pymes/InmovilizadoMaterialIntangibleInversionesInmobiliarias/Dimension/2024-01-01"); 
//          rootElement.setAttribute("xmlns:pgc-07-types","http://www.icac.meh.es/es/fr/gaap/pgc07/types/2024-01-01" );
//          rootElement.setAttribute("xmlns:pgc07d-clase","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/comun/ClasesInstrumentosFinancieros/Dimension/2024-01-01"); 
//          rootElement.setAttribute("xmlns:pgc07mc-ap","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/comun/abreviadoPymes/2024-01-01" );
//          rootElement.setAttribute("xmlns:dgi-types","http://www.xbrl.org.es/es/2008/dgi/gp/types/2024-01-01" );
//          rootElement.setAttribute("xmlns:pgc07mp-apdo0","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/pymes/apartado0/IdentificacionGeneral/2024-01-01"); 
//          rootElement.setAttribute("xmlns:pgc-07-roles","http://www.icac.meh.es/es/fr/gaap/pgc07/roles/2024-01-01" );
//          rootElement.setAttribute("xmlns:pgc07m-d-cp","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/comun/CategoriasProfesionales/Dimension/2024-01-01"); 
//          rootElement.setAttribute("xmlns:ref","http://www.xbrl.org/2004/ref" );
//          rootElement.setAttribute("xmlns:dgi-cnae-09","http://www.xbrl.org.es/es/2008/dgi/gp/lc-cnae-2009/2024-01-01");
          for (String nsu : nameSpaceUri.keySet()) {
        	  document.getDocumentElement().setAttribute(nsu, nameSpaceUri.get(nsu));
          }
//          document.appendChild(rootElement);

  		// Añadir el link al esquema de referencia
//          
////        String linkUri = "http://www.xbrl.org/2003/linkbase";
////        String xlinkUri = "http://www.w3.org/1999/xlink";
//        String uriSchema = "https://www.icac.gob.es/sites/default/files/pgc2007/v170/pgc07-pymes-completo.xsd";
//        
////        Element ele = document.createElementNS(linkUri, "link:schemaRef");          
////		ele.setAttributeNS(xlinkUri, "type", "simple");
////		ele.setAttributeNS(xlinkUri, "href", uriSchema);
//        Element ele = document.createElement("link:schemaRef");          
//		ele.setAttribute("xlink:href", uriSchema);
//		ele.setAttribute("xlink:type", "simple");
		
        Element ele = document.createElement("link:schemaRef");  
        ele.setAttribute("xlink:type", "simple"); 
        ele.setAttribute("xlink:href", uriSchema);
        document.getDocumentElement().appendChild(ele);
        
        String nifEmpresa = "B50111111";
        Date fechaIniActual = new Date("01/01/2024");
		Date fechaFinActual = new Date("12/31/2024");
		document.getDocumentElement().appendChild(addContext(document, Contexto.I_ACTUAL.toString(), nifEmpresa, fechaFinActual, null));
		document.getDocumentElement().appendChild(addContext(document, Contexto.D_ACTUAL.toString(), nifEmpresa, fechaFinActual, fechaIniActual));

        Date fechaIniAnterior = new Date("01/01/2023");
		Date fechaFinAnterior = new Date("12/31/2023");;
//		if (fechaIniAnterior != null && fechaFinAnterior != null)
//        {
            document.getDocumentElement().appendChild(addContext(document, Contexto.I_ANTERIOR.toString(), nifEmpresa, fechaFinAnterior, null));
            document.getDocumentElement().appendChild(addContext(document, Contexto.D_ANTERIOR.toString(), nifEmpresa, fechaFinAnterior, fechaIniAnterior));
//        }
		
        // Añadir elementos "Unit"
        document.getDocumentElement().appendChild(addUnit(document, "euro", "iso4217:EUR"));
        document.getDocumentElement().appendChild(addUnit(document, "shares", "xbrli:shares"));
        document.getDocumentElement().appendChild(addUnit(document, "pure", "xbrli:pure"));
          
        // Páginas de Identificación 
        addIdentificacion(document, fechaIniActual, fechaFinActual, fechaIniAnterior, fechaFinAnterior);
        
     // Balance de Situación y Cuenta de Perdidas y Ganancias
        // PARA PROBAR VOY A AÑADIR TODAS LAS CASILLAS A CERO
        // SE PODRIA HACER IGUALMENTE RECORRIENDO ESTOS DATOS DE CONVERSION Y BUSCANDO EN EL 
        // XML GUARDADO EN RATTACH LA CLAVE PARA OBTENER EL VALOR DEL EJERCICIO ACTUAL Y PARA OBTENER SI LLEVA NOTAS MEMORIA (CLAVE+"98")
        // ADEMAS SI HAY EJERCICIO ANTERIOR TAMBIEN SE BUSCARIA EL VALOR DEL EJERCICIO ANTERIOR EN EL XML (CLAVE+"9")
        for (String key : datosConversion.keySet()) {
//        	
//        	// Actual
//        	Contexto contexto = null; // Contexto (Balance = Instant; PyG, EIGR y EFE = Duration)
//            // Ejercio Actual 
//            if (key.startsWith("4"))
//            	contexto = Contexto.D_ACTUAL;  // PYG o EIGR o EFE
//            else
//                contexto = Contexto.I_ACTUAL;  // Balance
             
                // Ejercicio Anterior (si hay)
//                if (module.id == "bal")
//                    contexto = Contexto.I_ANTERIOR;
//                else contexto = Contexto.D_ANTERIOR;  // PYG o EIGR o EFE
//}

//             addElemento(document.getDocumentElement(), datosConversion.get(key), contexto.toString(), key, "2", "euro", "");
             
             addElementoBal(document.getDocumentElement(), key);
//             addElemento(Element padre, String nombre, String contextRef, String valor, String decimals, String unitRef, String idNota)
        	
        }
        
        // Notas en la memoria
        addNotasMemoria(document);
        
        // PRUEBAS MEMORIA
        
        addElemento(document.getDocumentElement(), "pgc07mp-rsm:DescripcionNoNormalizadaApartado1Resumen", Contexto.D_ACTUAL, "Actividad de la empresa en la memoria");  // CLAVE 9019001
        
            // Escribir el contenido del documento en un archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File("C:\\VirtualBoxVMs\\_TEMP_\\deposito.xbrl.xml"));

            transformer.transform(source, result);

            System.out.println("Archivo XML creado con éxito!");

        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }
    
    //private Element AddContext(Document doc, String id, String cif, Date fechaFin, Date fechaIni, String scenario)
    private static Element addContext(Document doc, String id, String cif, Date fechaFin, Date fechaIni)
    {
//        String uri = ObtenerUri("xbrli");

        Element ele;
        ele = doc.createElement("xbrli:context");
        ele.setAttribute("id", id);

        Element ele2;
        ele2 = doc.createElement("xbrli:entity");

//        doc.createTextNode(uri)
        
        Element ele3;
        ele3 = doc.createElement("xbrli:identifier");
        ele3.setAttribute("scheme", "http://www.icac.meh.es/xbrl");
        ele3.setTextContent(cif);  // CIF de la empresa

        ele2.appendChild(ele3);
        ele.appendChild(ele2);

        ele2 = doc.createElement("xbrli:period");

        if (fechaIni == null)
        {
            // Periodo instant si no lleva fecha inicio
            ele3 = doc.createElement("xbrli:instant");
            ele3.setTextContent(DATE_FORMAT.format(fechaFin));  // Final del ejercicio
            ele2.appendChild(ele3);
        }
        else
        {
            // Periodo duration si lleva fecha ini y fecha fin
            ele3 = doc.createElement("xbrli:startDate");
            ele3.setTextContent(DATE_FORMAT.format(fechaIni));  // Inicio del ejercicio
            ele2.appendChild(ele3);

            ele3 = doc.createElement("xbrli:endDate");
            ele3.setTextContent(DATE_FORMAT.format(fechaFin));  // Final del ejercicio
            ele2.appendChild(ele3);
        }

        ele.appendChild(ele2);

        // Elemento scenario, se utiliza para crear los contextos del ECPN Total
//        if (scenario != null)
//        {
//            ele2 = doc.CreateElement("xbrli", "scenario", uri);
//            ele.AppendChild(ele2);
//
//            ele3 = doc.CreateElement("xbrldi", "explicitMember", ObtenerUri("xbrldi"));
//            ele3.SetAttribute("dimension", "pgc07cbs-dpn:CambiosPatrimonioNetoDimension");
//            ele3.InnerText = scenario;
//            ele2.AppendChild(ele3);
//        }

        return ele;

    }
    
    private static Element addUnit(Document doc, String id, String measure)
    {
//        string uri = ObtenerUri("xbrli");

        Element ele;
        ele = doc.createElement("xbrli:unit");
        ele.setAttribute("id", id);

        Element ele2;
        ele2 = doc.createElement("xbrli:measure");
        ele2.setTextContent(measure);  

        ele.appendChild(ele2);

        return ele;

    }
    
    private static String llenarNameSpaceUri() {
    	
    	// PYMES SIN MEMORIA
		nameSpaceUri.put("xmlns:pgc07p","http://www.icac.meh.es/es/fr/gaap/pgc07/cuentas/pymes/2024-01-01"); 
		nameSpaceUri.put("xmlns:pgc-07-p-bal","http://www.icac.meh.es/es/fr/gaap/pgc07/cuentas/pymes/balance/2024-01-01"); 
		nameSpaceUri.put("xmlns:dgi-gen-bas","http://www.xbrl.org.es/es/2008/dgi/gp/gen-bas/2024-01-01"); 
		nameSpaceUri.put("xmlns:pgc-07-c-ap","http://www.icac.meh.es/es/fr/gaap/pgc07/comun-abreviadopymes/2024-01-01"); 
		nameSpaceUri.put("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance"); 
		nameSpaceUri.put("xmlns:pgc07mp","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/pymes/2024-01-01"); 
		nameSpaceUri.put("xmlns:iso4217","http://www.xbrl.org/2003/iso4217"); 
		nameSpaceUri.put("xmlns:pgc07m-roles","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/roles/2024-01-01"); 
		nameSpaceUri.put("xmlns:pgc-07-c-bs","http://www.icac.meh.es/es/fr/gaap/pgc07/comun-base/2024-01-01"); 
		nameSpaceUri.put("xmlns:dgi-gen-ex","http://www.xbrl.org.es/es/2008/dgi/gp/gen-ex/2024-01-01"); 
		nameSpaceUri.put("xmlns:xlink","http://www.w3.org/1999/xlink"); 
		nameSpaceUri.put("xmlns:pgc-07-ref","http://www.icac.meh.es/es/fr/gaap/pgc07/referenceParts/2024-01-01"); 
		nameSpaceUri.put("xmlns:dgi-rel","http://www.xbrl.org.es/es/2008/dgi/gp/rel/2024-01-01"); 
		nameSpaceUri.put("xmlns:dgi-lc-int","http://www.xbrl.org.es/es/2008/dgi/gp/lc-int/2024-01-01"); 
		nameSpaceUri.put("xmlns:pgc07mc-apdo0","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/comun/apartado0/IdentificacionGeneral/2024-01-01"); 
		nameSpaceUri.put("xmlns:link","http://www.xbrl.org/2003/linkbase"); 
		nameSpaceUri.put("xmlns:dgi-eco-bas","http://www.xbrl.org.es/es/2008/dgi/gp/eco-bas/2024-01-01"); 
		nameSpaceUri.put("xmlns:dgi-dat-inf","http://www.xbrl.org.es/es/2008/dgi/gp/dat-inf/2024-01-01"); 
		nameSpaceUri.put("xmlns:xbrli","http://www.xbrl.org/2003/instance"); 
		nameSpaceUri.put("xmlns:pgc-07-p-pyg","http://www.icac.meh.es/es/fr/gaap/pgc07/cuentas/pymes/PerdidasGanancias/2024-01-01"); 
		nameSpaceUri.put("xmlns:pgc07d-reclsf","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/comun/ReclasificacionInstrumentosFinancieros/Dimension/2024-01-01"); 
		nameSpaceUri.put("xmlns:pgc07m-d-pv","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/comun/PartesVinculadas/Dimension/2024-01-01"); 
		nameSpaceUri.put("xmlns:dgi-est-gen","http://www.xbrl.org.es/es/2008/dgi/gp/est-gen/2024-01-01"); 
		nameSpaceUri.put("xmlns:pgc07mc-bs","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/comun/base/2024-01-01"); 
		nameSpaceUri.put("xmlns:pgc07d-plzven","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/comun/PlazoVencimientoInstrumentosFinancieros/Dimension/2024-01-01"); 
		nameSpaceUri.put("xmlns:xbrldt","http://xbrl.org/2005/xbrldt"); 
		nameSpaceUri.put("xmlns:pgc-07-p","http://www.icac.meh.es/es/fr/gaap/pgc07/pymes/2024-01-01"); 
		nameSpaceUri.put("xmlns:pgc-07-types","http://www.icac.meh.es/es/fr/gaap/pgc07/types/2024-01-01"); 
		nameSpaceUri.put("xmlns:dgi-lc-es","http://www.xbrl.org.es/es/2008/dgi/gp/lc-es/2024-01-01"); 
		nameSpaceUri.put("xmlns:pgc07d-clase","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/comun/ClasesInstrumentosFinancieros/Dimension/2024-01-01"); 
		nameSpaceUri.put("xmlns:pgc07mp-apdo0","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/pymes/apartado0/IdentificacionGeneral/2024-01-01"); 
		nameSpaceUri.put("xmlns:dgi-types","http://www.xbrl.org.es/es/2008/dgi/gp/types/2024-01-01"); 
		nameSpaceUri.put("xmlns:pgc07mc-ap","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/comun/abreviadoPymes/2024-01-01"); 
		nameSpaceUri.put("xmlns:pgc-07-roles","http://www.icac.meh.es/es/fr/gaap/pgc07/roles/2024-01-01"); 
		nameSpaceUri.put("xmlns:ref","http://www.xbrl.org/2004/ref"); 
		nameSpaceUri.put("xmlns:dgi-cnae-09","http://www.xbrl.org.es/es/2008/dgi/gp/lc-cnae-2009/2024-01-01");
		
		// ESTE ES DE LA MEMORIA
		nameSpaceUri.put("xmlns:pgc07mp-rsm","http://www.icac.meh.es/es/fr/gaap/pgc07/memoria/pymes/resumen/2024-01-01");
				
		return "https://www.icac.gob.es/sites/default/files/pgc2007/v170/pgc07-pymes.xsd";    	
    	
    }
    
    // Conversion de la Casilla a Nombres del XBRL
    private static void llenarDatosConversion()
    {
        // PYMES

        // Balance de Situación
        datosConversion.put("11000", "pgc-07-c-bs:ActivoNoCorriente");
        datosConversion.put("11100", "pgc-07-c-bs:ActivoNoCorrienteInmovilizadoIntangible");
        datosConversion.put("11200", "pgc-07-c-bs:ActivoNoCorrienteInmovilizadoMaterial");
        datosConversion.put("11300", "pgc-07-c-bs:ActivoNoCorrienteInversionesInmobiliarias");
        datosConversion.put("11400", "pgc-07-c-bs:ActivoNoCorrienteInversionesEmpresasGrupoEmpresasAsociadasLargoPlazo");
        datosConversion.put("11500", "pgc-07-c-bs:ActivoNoCorrienteInversionesFinancierasLargoPlazo");
        datosConversion.put("11600", "pgc-07-c-bs:ActivoNoCorrienteActivosImpuestoDiferido");
        datosConversion.put("11700", "pgc-07-c-bs:ActivoNoCorrienteDeudasComercialesNoCorriente");
        datosConversion.put("12000", "pgc-07-c-bs:ActivoCorriente");
        datosConversion.put("12200", "pgc-07-c-bs:ActivoCorrienteExistencias");
        datosConversion.put("12300", "pgc-07-c-bs:ActivoCorrienteDeudoresComercialesOtrasCuentasCobrar");
        datosConversion.put("12380", "pgc-07-c-bs:ActivoCorrienteDeudoresComercialesOtrasCuentasCobrarClientesVentasPrestacionesServicios");
        datosConversion.put("12381", "pgc-07-c-bs:ActivoCorrienteDeudoresComercialesOtrasCuentasCobrarClientesVentasPrestacionesServiciosLargoPlazo");
        datosConversion.put("12382", "pgc-07-c-bs:ActivoCorrienteDeudoresComercialesOtrasCuentasCobrarClientesVentasPrestacionesServiciosCortoPlazo");
        datosConversion.put("12370", "pgc-07-c-bs:ActivoCorrienteDeudoresComercialesOtrasCuentasCobrarAccionistasDesembolsosExigidos");
        datosConversion.put("12390", "pgc-07-c-ap:ActivoCorrienteDeudoresComercialesOtrasCuentasCobrarOtrosDeudores");
        datosConversion.put("12400", "pgc-07-c-bs:ActivoCorrienteInversionesEmpresasGrupoEmpresasAsociadasCortoPlazo");
        datosConversion.put("12500", "pgc-07-c-bs:ActivoCorrienteInversionesFinancierasCortoPlazo");
        datosConversion.put("12600", "pgc-07-c-bs:ActivoCorrientePeriodificacionesCortoPlazo");
        datosConversion.put("12700", "pgc-07-c-bs:ActivoCorrienteEfectivoOtrosActivosLiquidosEquivalentes");
        datosConversion.put("10000", "pgc-07-c-bs:TotalActivo");
        datosConversion.put("20000", "pgc-07-c-bs:PatrimonioNeto");
        datosConversion.put("21000", "pgc-07-c-bs:PatrimonioNetoFondosPropios");
        datosConversion.put("21100", "pgc-07-c-bs:PatrimonioNetoFondosPropiosCapital");
        datosConversion.put("21110", "pgc-07-c-bs:PatrimonioNetoFondosPropiosCapitalEscriturado");
        datosConversion.put("21120", "pgc-07-c-bs:PatrimonioNetoFondosPropiosCapitalNoExigido");
        datosConversion.put("21200", "pgc-07-c-bs:PatrimonioNetoFondosPropiosPrimaEmision");
        datosConversion.put("21300", "pgc-07-c-bs:PatrimonioNetoFondosPropiosReservas");
        datosConversion.put("21350", "pgc-07-c-bs:PatrimonioNetoFondosPropiosReservasReservaCapitalizacion");
        datosConversion.put("21360", "pgc-07-c-ap:PatrimonioNetoFondosPropiosReservasOtrasReservas");
        datosConversion.put("21400", "pgc-07-c-bs:PatrimonioNetoFondosPropiosAccionesParticipacionesPatrimonioPropias");
        datosConversion.put("21500", "pgc-07-c-bs:PatrimonioNetoFondosPropiosResultadosEjerciciosAnteriores");
        datosConversion.put("21600", "pgc-07-c-bs:PatrimonioNetoFondosPropiosOtrasAportacionesSocios");
        datosConversion.put("21700", "pgc-07-c-bs:PatrimonioNetoFondosPropiosResultadoEjercicio");
        datosConversion.put("21800", "pgc-07-c-bs:PatrimonioNetoFondosPropiosDividendoCuenta");
        datosConversion.put("22000", "pgc-07-c-bs:PatrimonioNetoAjustesCambioValor");
        datosConversion.put("23000", "pgc-07-c-bs:PatrimonioNetoSubvencionesDonacionesLegadosRecibidos");
        datosConversion.put("31000", "pgc-07-c-bs:PasivoNoCorriente");
        datosConversion.put("31100", "pgc-07-c-bs:PasivoNoCorrienteProvisionesLargoPlazo");
        datosConversion.put("31200", "pgc-07-c-bs:PasivoNoCorrienteDeudasLargoPlazo");
        datosConversion.put("31220", "pgc-07-c-bs:PasivoNoCorrienteDeudasLargoPlazoDeudasEntidadesCredito");
        datosConversion.put("31230", "pgc-07-c-bs:PasivoNoCorrienteDeudasLargoPlazoAcreedoresArrendamientoFinanciero");
        datosConversion.put("31290", "pgc-07-c-ap:PasivoNoCorrienteDeudasLargoPlazoOtrasDeudas");
        datosConversion.put("31300", "pgc-07-c-bs:PasivoNoCorrienteDeudasEmpresasGrupoEmpresasAsociadasLargoPlazo");
        datosConversion.put("31400", "pgc-07-c-bs:PasivoNoCorrientePasivosImpuestoDiferido");
        datosConversion.put("31500", "pgc-07-c-bs:PasivoNoCorrientePeriodificacionesLargoPlazo");
        datosConversion.put("31600", "pgc-07-c-bs:PasivoNoCorrienteAcreedoresComercialesNoCorrientes");
        datosConversion.put("31700", "pgc-07-c-bs:PasivoNoCorrienteDeudaCaracteristicasEspecialesLargoPlazo");
        datosConversion.put("32000", "pgc-07-c-bs:PasivoCorriente");
        datosConversion.put("32200", "pgc-07-c-bs:PasivoCorrienteProvisionesCortoPlazo");
        datosConversion.put("32300", "pgc-07-c-bs:PasivoCorrienteDeudasCortoPlazo");
        datosConversion.put("32320", "pgc-07-c-bs:PasivoCorrienteDeudasCortoPlazoDeudasEntidadesCredito");
        datosConversion.put("32330", "pgc-07-c-bs:PasivoCorrienteDeudasCortoPlazoAcreedoresArrendamientoFinanciero");
        datosConversion.put("32390", "pgc-07-c-ap:PasivoCorrienteDeudasCortoPlazoOtrasDeudas");
        datosConversion.put("32400", "pgc-07-c-bs:PasivoCorrienteDeudasEmpresasGrupoEmpresasAsociadas");
        datosConversion.put("32500", "pgc-07-c-bs:PasivoCorrienteAcreedoresComercialesOtrasCuentasPagar");
        datosConversion.put("32580", "pgc-07-c-bs:PasivoCorrienteAcreedoresComercialesOtrasCuentasPagarProveedores");
        datosConversion.put("32581", "pgc-07-c-bs:PasivoCorrienteAcreedoresComercialesOtrasCuentasPagarProveedoresLargoPlazo");
        datosConversion.put("32582", "pgc-07-c-bs:PasivoCorrienteAcreedoresComercialesOtrasCuentasPagarProveedoresCortoPlazo");
        datosConversion.put("32590", "pgc-07-c-ap:PasivoCorrienteAcreedoresComercialesOtrasCuentasPagarOtrosAcreedores");
        datosConversion.put("32600", "pgc-07-c-bs:PasivoCorrientePeriodificacionesCortoPlazo");
        datosConversion.put("32700", "pgc-07-c-bs:PasivoCorrienteDeudasCaracteristicasEspecialesCortoPlazo");
        datosConversion.put("30000", "pgc-07-c-bs:PatrimonioNetoPasivoTotal");

        // Cuenta de Perdidas y Ganancias
        datosConversion.put("40100", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasImporteNetoCifraNegocios");
        datosConversion.put("40200", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasVariacionExistenciasProductosTerminadosProductosCursoFabricacion");
        datosConversion.put("40300", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasTrabajosRealizadosEmpresaActivo");
        datosConversion.put("40400", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasAprovisionamientos");
        datosConversion.put("40500", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasOtrosIngresosExplotacion");
        datosConversion.put("40600", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasGestionPersonal");
        datosConversion.put("40700", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasOtrosGastosExplotacion");
        datosConversion.put("40800", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasAmortizacionInmovilizado");
        datosConversion.put("40900", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasImputacionSubvencionesInmovilizadoNoFinancieroOtras");
        datosConversion.put("41000", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasExcesosProvisiones");
        datosConversion.put("41100", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasDeterioroResultadoEnajenacionesInmovilizado");
        datosConversion.put("41300", "pgc-07-c-bs:PerdidasGananciasOtrosResultados");
        datosConversion.put("49100", "pgc-07-c-bs:PerdidasGananciasResultadoExplotacion");
        datosConversion.put("41400", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasIngresosFinancieros");
        datosConversion.put("41430", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasIngresosFinancierosImputacionSubvencionesDonacionesLegadosCaracterFinanciero");
        datosConversion.put("41490", "pgc-07-c-ap:PerdidasGananciasOperacionesContinuadasIngresosFinancierosOtrosIngresosFinancieros");
        datosConversion.put("41500", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasGastosFinancieros");
        datosConversion.put("41600", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasVariacionValorRazonableInstrumentosFinancieros");
        datosConversion.put("41700", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasDiferenciasCambio");
        datosConversion.put("41800", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasDeterioroResultadoEnajenacionesInstrumentosFinancieros");
        datosConversion.put("42100", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasOtrosIngresosGastosCaracterFinanciero");
        datosConversion.put("42110", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasOtrosIngresosGastosCaracterFinancieroIncorporacionActivoGastosFinancieros");
        datosConversion.put("42120", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasOtrosIngresosGastosCaracterFinancieroIngresosFinancierosDerivadosConveniosAcreedores");
        datosConversion.put("42130", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasOtrosIngresosGastosCaracterFinancieroRestoIngresosGastos");
        datosConversion.put("49200", "pgc-07-c-bs:PerdidasGananciasResultadoFinanciero");
        datosConversion.put("49300", "pgc-07-c-bs:PerdidasGananciasResultadoAntesImpuestos");
        datosConversion.put("41900", "pgc-07-c-bs:PerdidasGananciasOperacionesContinuadasImpuestosSobreBeneficios");
        datosConversion.put("49500", "pgc-07-c-bs:PerdidasGananciasResultadoEjercicio");

        // POR AHORA SOLO SE PONEN LOS DE PYMES
        // ABREVIADO (ademas de las claves que lleva la de PYMES)            
//        
//        // Balance de Situacion
//        datosConversion.Add("12100", "pgc-07-c-na:ActivoCorrienteActivosNoCorrientesMantenidosParaVenta");
//        if (formato == "A")
//            datosConversion.Add("21900", "pgc-07-c-na:PatrimonioNetoFondosPropiosOtrosInstrumentosPatrimonioNeto");
//        datosConversion.Add("32100", "pgc-07-c-na:PasivoCorrientePasivosVinculadosActivosNoCorrientesMantenidosVenta");
//        
//        // Cuenta de Perdidas y Ganancias
//        datosConversion.Add("41200", "pgc-07-c-na:PerdidasGananciasOperacionesContinuadasDiferenciaNegativaCombinacionesNegocios");

        // EN AON SOLO HAY ABREVIADO Y PYMES
        // NORMAL o MIXTO (ademas de las claves que lleva ABREVIADO y PYMES)
//        if (formato == "N" || formato == "M")
//        {
//            // Balance de Situacion
//            datosConversion.Add("11110", "pgc-07-n:ActivoNoCorrienteInmovilizadoIntangibleDesarrollo");
//            datosConversion.Add("11120", "pgc-07-n:ActivoNoCorrienteInmovilizadoIntangibleConcesiones");
//            datosConversion.Add("11130", "pgc-07-n:ActivoNoCorrienteInmovilizadoIntangiblePatentesLicenciasMarcasSimilares");
//            datosConversion.Add("11140", "pgc-07-n:ActivoNoCorrienteInmovilizadoIntangibleFondoComercio");
//            datosConversion.Add("11150", "pgc-07-n:ActivoNoCorrienteInmovilizadoIntangibleAplicacionesInformaticas");
//            datosConversion.Add("11160", "pgc-07-n:ActivoNoCorrienteInmovilizadoIntangibleInvestigacion");
//            datosConversion.Add("11180", "pgc-07-n:ActivoNoCorrienteInmovilizadoIntangiblePropiedadIntelectual");
//            datosConversion.Add("11190", "pgc-07-n:ActivoNoCorrienteInmovilizadoIntangibleDerechosEmisionGasesEfectoInvernadero");
//            datosConversion.Add("11170", "pgc-07-n:ActivoNoCorrienteInmovilizadoIntangibleOtro");
//            datosConversion.Add("11210", "pgc-07-n:ActivoNoCorrienteInmovilizadoMaterialTerrenosConstrucciones");
//            datosConversion.Add("11220", "pgc-07-n:ActivoNoCorrienteInmovilizadoMaterialInstalacionesTecnicasMaquinariaUtilajeMobiliarioOtro");
//            datosConversion.Add("11230", "pgc-07-n:ActivoNoCorrienteInmovilizadoMaterialInmovilizadoEnCursoAnticipos");
//            datosConversion.Add("11510", "pgc-07-n:ActivoNoCorrienteInversionesFinancierasLargoPlazoInstrumentosPatrimonio");
//            datosConversion.Add("11520", "pgc-07-n:ActivoNoCorrienteInversionesFinancierasLargoPlazoCreditosEmpresas");
//            datosConversion.Add("11530", "pgc-07-n:ActivoNoCorrienteInversionesFinancierasLargoPlazoValoresRepresentativosDeuda");
//            datosConversion.Add("11540", "pgc-07-n:ActivoNoCorrienteInversionesFinancierasLargoPlazoDerivados");
//            datosConversion.Add("11550", "pgc-07-n:ActivoNoCorrienteInversionesFinancierasLargoPlazoOtrosActivosFinancieros");
//            datosConversion.Add("11560", "pgc-07-n:ActivoNoCorrienteInversionesFinancierasLargoPlazoOtrasInversiones");
//            datosConversion.Add("11320", "pgc-07-n:ActivoNoCorrienteInversionesInmobiliariasConstrucciones");
//            datosConversion.Add("11310", "pgc-07-n:ActivoNoCorrienteInversionesInmobiliariasTerrenos");
//            datosConversion.Add("11410", "pgc-07-n:ActivoNoCorrienteInversionesEmpresasGrupoEmpresasAsociadasLargoPlazoInstrumentosPatrimonio");
//            datosConversion.Add("11430", "pgc-07-n:ActivoNoCorrienteInversionesEmpresasGrupoEmpresasAsociadasLargoPlazoValoresRepresentativosDeuda");
//            datosConversion.Add("11420", "pgc-07-n:ActivoNoCorrienteInversionesEmpresasGrupoEmpresasAsociadasLargoPlazoCreditosEmpresas");
//            datosConversion.Add("11440", "pgc-07-n:ActivoNoCorrienteInversionesEmpresasGrupoEmpresasAsociadasLargoPlazoDerivados");
//            datosConversion.Add("11450", "pgc-07-n:ActivoNoCorrienteInversionesEmpresasGrupoEmpresasAsociadasLargoPlazoOtrosActivosFinancieros");
//            datosConversion.Add("11460", "pgc-07-n:ActivoNoCorrienteInversionesEmpresasGrupoEmpresasAsociadasLargoPlazoOtrasInversiones");
//            datosConversion.Add("12210", "pgc-07-n:ActivoCorrienteExistenciasComerciales");
//            datosConversion.Add("12220", "pgc-07-n:ActivoCorrienteExistenciasMateriasPrimasOtrosAprovisionamientos");
//            datosConversion.Add("12230", "pgc-07-n:ActivoCorrienteExistenciasProductosCurso");
//            datosConversion.Add("12232", "pgc-07-n:ActivoCorrienteExistenciasProductosCursoCicloCorto");
//            datosConversion.Add("12231", "pgc-07-n:ActivoCorrienteExistenciasProductosCursoCicloLargo");
//            datosConversion.Add("12240", "pgc-07-n:ActivoCorrienteExistenciasProductosTerminados");
//            datosConversion.Add("12241", "pgc-07-n:ActivoCorrienteExistenciasProductosTerminadosCicloLargo");
//            datosConversion.Add("12242", "pgc-07-n:ActivoCorrienteExistenciasProductosTerminadosCicloCorto");
//            datosConversion.Add("12250", "pgc-07-n:ActivoCorrienteExistenciasSubproductosResiduosMaterialesRecuperados");
//            datosConversion.Add("12260", "pgc-07-n:ActivoCorrienteExistenciasAnticiposProveedores");
//            datosConversion.Add("12410", "pgc-07-n:ActivoCorrienteInversionesEmpresasGrupoEmpresasAsociadasCortoPlazoInstrumentosPatrimonio");
//            datosConversion.Add("12420", "pgc-07-n:ActivoCorrienteInversionesEmpresasGrupoEmpresasAsociadasCortoPlazoCreditosEmpresas");
//            datosConversion.Add("12430", "pgc-07-n:ActivoCorrienteInversionesEmpresasGrupoEmpresasAsociadasCortoPlazoValoresRepresentativosDeuda");
//            datosConversion.Add("12440", "pgc-07-n:ActivoCorrienteInversionesEmpresasGrupoEmpresasAsociadasCortoPlazoDerivados");
//            datosConversion.Add("12450", "pgc-07-n:ActivoCorrienteInversionesEmpresasGrupoEmpresasAsociadasCortoPlazoOtrosActivosFinancieros");
//            datosConversion.Add("12460", "pgc-07-n:ActivoCorrienteInversionesEmpresasGrupoEmpresasAsociadasCortoPlazoOtrasInversiones");
//            datosConversion.Add("12510", "pgc-07-n:ActivoCorrienteInversionesFinancierasCortoPlazoInstrumentosPatrimonio");
//            datosConversion.Add("12520", "pgc-07-n:ActivoCorrienteInversionesFinancierasCortoPlazoCreditosEmpresas");
//            datosConversion.Add("12530", "pgc-07-n:ActivoCorrienteInversionesFinancierasCortoPlazoValoresRepresentativosDeuda");
//            datosConversion.Add("12540", "pgc-07-n:ActivoCorrienteInversionesFinancierasCortoPlazoDerivados");
//            datosConversion.Add("12550", "pgc-07-n:ActivoCorrienteInversionesFinancierasCortoPlazoOtrosActivosFinancieros");
//            datosConversion.Add("12560", "pgc-07-n:ActivoCorrienteInversionesFinancierasCortoPlazoOtrasInversiones");
//            datosConversion.Add("12710", "pgc-07-n:ActivoCorrienteEfectivoOtrosActivosLiquidosEquivalentesTesoreria");
//            datosConversion.Add("12720", "pgc-07-n:ActivoCorrienteEfectivoOtrosActivosLiquidosEquivalentesOtrosActivosLiquidosEquivalentes");
//            datosConversion.Add("12320", "pgc-07-n:ActivoCorrienteDeudoresComercialesOtrasCuentasCobrarClientesEmpresasGrupoAsociadas");
//            datosConversion.Add("12330", "pgc-07-n:ActivoCorrienteDeudoresComercialesOtrasCuentasCobrarDeudoresVarios");
//            datosConversion.Add("12340", "pgc-07-n:ActivoCorrienteDeudoresComercialesOtrasCuentasCobrarPersonal");
//            datosConversion.Add("12350", "pgc-07-n:ActivoCorrienteDeudoresComercialesOtrasCuentasCobrarActivosImpuestoCorriente");
//            datosConversion.Add("12360", "pgc-07-n:ActivoCorrienteDeudoresComercialesOtrasCuentasCobrarOtrosCreditosAdministracionesPublicas");
//            datosConversion.Add("22100", "pgc-07-n:PatrimonioNetoAjustesCambioValorActivosFinancierosDisponiblesVenta");
//            datosConversion.Add("22200", "pgc-07-n:PatrimonioNetoAjustesCambioValorOperacionesCobertura");
//            datosConversion.Add("22500", "pgc-07-n:PatrimonioNetoAjustesCambioValorOtros");
//            datosConversion.Add("22300", "pgc-07-n:PatrimonioNetoAjustesCambioValorActivosNoCorrientesPasivosVinculadosMantenidosVenta");
//            datosConversion.Add("22400", "pgc-07-n:PatrimonioNetoAjustesCambioValorDiferenciaConversion");
//            datosConversion.Add("21310", "pgc-07-n:PatrimonioNetoFondosPropiosReservasLegalEstatutarias");
//            datosConversion.Add("21320", "pgc-07-n:PatrimonioNetoFondosPropiosReservasOtrasReservas");
//            datosConversion.Add("21330", "pgc-07-n:PatrimonioNetoFondosPropiosReservasReservaRevalorizacion");
//            datosConversion.Add("21510", "pgc-07-n:PatrimonioNetoFondosPropiosResultadosEjerciciosAnterioresRemanente");
//            datosConversion.Add("21520", "pgc-07-n:PatrimonioNetoFondosPropiosResultadosEjerciciosAnterioresResultadosNegativosEjerciciosAnteriores");
//            datosConversion.Add("31110", "pgc-07-n:PasivoNoCorrienteProvisionesLargoPlazoObligacionesPrestacionesPersonalLargoPlazo");
//            datosConversion.Add("31120", "pgc-07-n:PasivoNoCorrienteProvisionesLargoPlazoActuacionesMedioAmbientales");
//            datosConversion.Add("31130", "pgc-07-n:PasivoNoCorrienteProvisionesLargoPlazoProvisionesReestructuracion");
//            datosConversion.Add("31140", "pgc-07-n:PasivoNoCorrienteProvisionesLargoPlazoOtrasProvisiones");
//            datosConversion.Add("31210", "pgc-07-n:PasivoNoCorrienteDeudasLargoPlazoObligacionesOtrosValoresNegociables");
//            datosConversion.Add("31240", "pgc-07-n:PasivoNoCorrienteDeudasLargoPlazoDerivados");
//            datosConversion.Add("31250", "pgc-07-n:PasivoNoCorrienteDeudasLargoPlazoOtrosPasivosFinancieros");
//            datosConversion.Add("32210", "pgc-07-n:PasivoCorrienteProvisionesCortoPlazoProvisionesDerechosEmisionGasesEfectoInvernadero");
//            datosConversion.Add("32220", "pgc-07-n:PasivoCorrienteProvisionesCortoPlazoOtrasProvisiones");
//            datosConversion.Add("32310", "pgc-07-n:PasivoCorrienteDeudasCortoPlazoObligacionesOtrosValoresNegociables");
//            datosConversion.Add("32340", "pgc-07-n:PasivoCorrienteDeudasCortoPlazoObligacionesDerivados");
//            datosConversion.Add("32350", "pgc-07-n:PasivoCorrienteDeudasCortoPlazoObligacionesOtrosPasivosFinancieros");
//            datosConversion.Add("32520", "pgc-07-n:PasivoCorrienteAcreedoresComercialesOtrasCuentasPagarProveedoresEmpresasGrupoAsociadas");
//            datosConversion.Add("32530", "pgc-07-n:PasivoCorrienteAcreedoresComercialesOtrasCuentasPagarAcreedoresVarios");
//            datosConversion.Add("32540", "pgc-07-n:PasivoCorrienteAcreedoresComercialesOtrasCuentasPagarPersonalRemuneracionesPendientesPago");
//            datosConversion.Add("32550", "pgc-07-n:PasivoCorrienteAcreedoresComercialesOtrasCuentasPagarPasivoImpuestoCorriente");
//            datosConversion.Add("32560", "pgc-07-n:PasivoCorrienteAcreedoresComercialesOtrasCuentasPagarOtrasDeudasAdministracionesPublicas");
//            datosConversion.Add("32570", "pgc-07-n:PasivoCorrienteAcreedoresComercialesOtrasCuentasPagarAnticiposClientes");
//
//            datosConversion.Add("12221","pgc-07-n:ActivoCorrienteExistenciasMateriasPrimasOtrosAprovisionamientosLargoPlazo");
//            datosConversion.Add("12222","pgc-07-n:ActivoCorrienteExistenciasMateriasPrimasOtrosAprovisionamientosCortoPlazo");
//
//            datosConversion.Add("12310","pgc-07-c-bs:ActivoCorrienteDeudoresComercialesOtrasCuentasCobrarClientesVentasPrestacionesServicios");
//            datosConversion.Add("12311","pgc-07-c-bs:ActivoCorrienteDeudoresComercialesOtrasCuentasCobrarClientesVentasPrestacionesServiciosLargoPlazo");
//            datosConversion.Add("12312","pgc-07-c-bs:ActivoCorrienteDeudoresComercialesOtrasCuentasCobrarClientesVentasPrestacionesServiciosCortoPlazo");
//            datosConversion.Add("21900","pgc-07-c-bs:PatrimonioNetoFondosPropiosOtrosInstrumentosPatrimonioNeto");
//
//            datosConversion.Add("32510","pgc-07-c-bs:PasivoCorrienteAcreedoresComercialesOtrasCuentasPagarProveedores");
//            datosConversion.Add("32511","pgc-07-c-bs:PasivoCorrienteAcreedoresComercialesOtrasCuentasPagarProveedoresLargoPlazo");
//            datosConversion.Add("32512","pgc-07-c-bs:PasivoCorrienteAcreedoresComercialesOtrasCuentasPagarProveedoresCortoPlazo");
//            
//            // Cuenta de Perdidas y Ganancias
//            datosConversion.Add("40110", "pgc-07-n:PerdidasGananciasOperacionesContinuadasImporteNetoCifraNegociosVentas");
//            datosConversion.Add("40120", "pgc-07-n:GananciasOperacionesContinuadasImporteNetoCifraNegociosPrestacionesServicios");
//            datosConversion.Add("40130", "pgc-07-n:GananciasOperacionesContinuadasImporteNetoCifraNegociosIngresosCaracterFinancieroSociedadesHolding");
//            datosConversion.Add("40410", "pgc-07-n:PerdidasGananciasOperacionesContinuadasAprovisionamientosConsumoMercaderias");
//            datosConversion.Add("40420", "pgc-07-n:PerdidasGananciasOperacionesContinuadasAprovisionamientosConsumoMateriasPrimasOtrasMateriasConsumibles");
//            datosConversion.Add("40430", "pgc-07-n:PerdidasGananciasOperacionesContinuadasAprovisionamientosTrabajosRealizadosOtrasEmpresas");
//            datosConversion.Add("40440", "pgc-07-n:PerdidasGananciasOperacionesContinuadasAprovisionamientosDeterioroMercaderiasMateriasPrimasOtrosAprovisionamientos");
//            datosConversion.Add("40510", "pgc-07-n:PerdidasGananciasOperacionesContinuadasOtrosIngresosExplotacionIngresosAccesoriosIngresosOtrosGestionCorriente");
//            datosConversion.Add("40520", "pgc-07-n:PerdidasGananciasOperacionesContinuadasOtrosIngresosExplotacionSubvencionesExplotacionIncorporadasResultadoEjercicio");
//            datosConversion.Add("40610", "pgc-07-n:PerdidasGananciasOperacionesContinuadasGestionPersonalSueldosSalariosAsimilados");
//            datosConversion.Add("40620", "pgc-07-n:PerdidasGananciasOperacionesContinuadasGestionPersonalCargasSociales");
//            datosConversion.Add("40630", "pgc-07-n:PerdidasGananciasOperacionesContinuadasGestionPersonalProvisiones");
//            datosConversion.Add("40710", "pgc-07-n:PerdidasGananciasOperacionesContinuadasOtrosGastosExplotacionServiciosExteriores");
//            datosConversion.Add("40720", "pgc-07-n:PerdidasGananciasOperacionesContinuadasOtrosGastosExplotacionTributos");
//            datosConversion.Add("40730", "pgc-07-n:PerdidasGananciasOperacionesContinuadasOtrosGastosExplotacionPerdidasDeterioroVariacionProvisionesOperacionesComerciales");
//            datosConversion.Add("40740", "pgc-07-n:PerdidasGananciasOperacionesContinuadasOtrosGastosExplotacionOtrosGastosGestionCorriente");
//            datosConversion.Add("40750", "pgc-07-n:PerdidasGananciasOperacionesContinuadasOtrosGastosExplotacionGastosEmisionGasesEfectoInvernadero");
//            datosConversion.Add("41110", "pgc-07-n:PerdidasGananciasOperacionesContinuadasDeterioroResultadoEnajenacionesInmovilizadoDeterioroPerdidas");
//            datosConversion.Add("41120", "pgc-07-n:PerdidasGananciasOperacionesContinuadasDeterioroResultadoEnajenacionesInmovilizadoResultadosEnajenacionesOtras");
//            datosConversion.Add("41130", "pgc-07-n:PerdidasGananciasOperacionesContinuadasDeterioroResultadoEnajenacionesInmovilizadoDeterioroResultadosEnajenacionesInmovilizadoSociedadesHolding");
//            datosConversion.Add("41410", "pgc-07-n:PerdidasGananciasOperacionesContinuadasIngresosFinancierosParticipacionesInstrumentosPatrimonio");
//            datosConversion.Add("41411", "pgc-07-n:PerdidasGananciasOperacionesContinuadasIngresosFinancierosParticipacionesInstrumentosPatrimonioEmpresasGrupoEmpresasAsociadas");
//            datosConversion.Add("41412", "pgc-07-n:PerdidasGananciasOperacionesContinuadasIngresosFinancierosParticipacionesInstrumentosPatrimonioTerceros");
//            datosConversion.Add("41420", "pgc-07-n:PerdidasGananciasOperacionesContinuadasIngresosFinancierosValoresNegociablesOtrosInstrumentosFinancieros");
//            datosConversion.Add("41421", "pgc-07-n:PerdidasGananciasOperacionesContinuadasIngresosFinancierosValoresNegociablesCreditosActivoInmovilizadoEmpresasGrupoEmpresasAsociadas");
//            datosConversion.Add("41422", "pgc-07-n:PerdidasGananciasOperacionesContinuadasIngresosFinancierosValoresNegociablesCreditosActivoInmovilizadoTerceros");
//            datosConversion.Add("41510", "pgc-07-n:PerdidasGananciasOperacionesContinuadasGastosFinancierosDeudasEmpresasGrupoEmpresasAsociadas");
//            datosConversion.Add("41520", "pgc-07-n:PerdidasGananciasOperacionesContinuadasGastosFinancierosDeudasTerceros");
//            datosConversion.Add("41530", "pgc-07-n:PerdidasGananciasOperacionesContinuadasGastosFinancierosActualizacionProvisiones");
//            datosConversion.Add("41610", "pgc-07-n:PerdidasGananciasOperacionesContinuadasVariacionValorRazonableInstrumentosFinancierosCarteraNegociacionOtros");
//            datosConversion.Add("41620", "pgc-07-n:PerdidasGananciasOperacionesContinuadasVariacionValorRazonableInstrumentosFinancierosImputacionResultadoEjercicioActivosFinancierosDisponiblesVenta");
//            datosConversion.Add("41810", "pgc-07-n:PerdidasGananciasOperacionesContinuadasDeterioroResultadoEnajenacionesInstrumentosFinancierosDeteriorosPerdidas");
//            datosConversion.Add("41820", "pgc-07-n:PerdidasGananciasOperacionesContinuadasDeterioroResultadoEnajenacionesInstrumentosFinancierosResultadoEnajenacionesOtras");
//            datosConversion.Add("42000", "pgc-07-n:PerdidasGananciasResultadoEjercicioProcedenteOperacionesInterrumpidas");
//
//            datosConversion.Add("49400", "pgc-07-c-na:PerdidasGananciasResultadoEjercicioProcedenteOperacionesContinuadas");
//
//            // Estado de Ingresos y Gastos Reconocidos
//            datosConversion.Add("50070", "pgc-07-c-bs:CambiosPatrimonioNetoIngresosGastosReconocidosIngresosGastosImputadosDirectamentePatrimonioNetoEfectoImpositivo");
//            datosConversion.Add("50040", "pgc-07-c-bs:CambiosPatrimonioNetoIngresosGastosReconocidosIngresosGastosImputadosDirectamentePatrimonioNetoGanaciasPerdidasActuarialesOtrosAjustes");
//            datosConversion.Add("50030", "pgc-07-c-bs:CambiosPatrimonioNetoIngresosGastosReconocidosIngresosGastosImputadosDirectamentePatrimonioNetoSubvencionesDonacionesLegados");
//            datosConversion.Add("50020", "pgc-07-c-bs:CambiosPatrimonioNetoIngresosGastosReconocidosIngresosGastosImputadosDirectamentePatrimonioNetoCoberturasFlujosEfectivos");
//            datosConversion.Add("50010", "pgc-07-c-bs:CambiosPatrimonioNetoIngresosGastosReconocidosIngresosGastosImputadosDirectamentePatrimonioNetoValoracionInstrumentosFinancieros");
//            datosConversion.Add("50011", "pgc-07-n:CambiosPatrimonioNetoIngresosGastosReconocidosIngresosGastosImputadosDirectamentePatrimonioNetoValoracionInstrumentosFinancierosActivosFinancierosDisponiblesVenta");
//            datosConversion.Add("50012", "pgc-07-n:CambiosPatrimonioNetoIngresosGastosReconocidosIngresosGastosImputadosDirectamentePatrimonioNetoValoracionInstrumentosFinancierosOtrosIngresosOtrosGastos");
//            datosConversion.Add("50050", "pgc-07-c-bs:CambiosPatrimonioNetoIngresosGastosReconocidosIngresosGastosImputadosDirectamentePatrimonioNetoActivosNoCorrientesPasivosVinculadosMantenidosParaVenta");
//            datosConversion.Add("50060", "pgc-07-c-bs:CambiosPatrimonioNetoIngresosGastosReconocidosIngresosGastosImputadosDirectamentePatrimonioNetoDiferenciasConversion");
//            datosConversion.Add("59300", "pgc-07-c-bs:CambiosPatrimonioNetoIngresosGastosReconocidosTransferenciasCuentaPerdidasGananciasTotal");
//            datosConversion.Add("59200", "pgc-07-c-bs:CambiosPatrimonioNetoIngresosGastosReconocidosIngresosGastosImputadosDirectamentePatrimonioNetoTotal");
//            datosConversion.Add("59400", "pgc-07-c-bs:CambiosPatrimonioNetoIngresosGastosReconocidosTotal");
//            datosConversion.Add("59100", "pgc-07-c-bs:CambiosPatrimonioNetoIngresosGastosReconocidosResultadoCuentaPerdidasGanancias");
//            datosConversion.Add("50130", "pgc-07-c-bs:CambiosPatrimonioNetoIngresosGastosReconocidosTransferenciasCuentaPerdidasGananciasEfectoImpositivo");
//            datosConversion.Add("50100", "pgc-07-c-bs:CambiosPatrimonioNetoIngresosGastosReconocidosTransferenciasCuentaPerdidasGananciasSubvencionesDonacionesLegadosRecibidos");
//            datosConversion.Add("50090", "pgc-07-c-bs:CambiosPatrimonioNetoIngresosGastosReconocidosTransferenciasCuentaPerdidasGananciasCoberturasFlujosEfectivo");
//            datosConversion.Add("50080", "pgc-07-c-bs:CambiosPatrimonioNetoIngresosGastosReconocidosTransferenciasCuentaPerdidasGananciasValoracionInstrumentosFinancieros");
//            datosConversion.Add("50081", "pgc-07-n:CambiosPatrimonioNetoIngresosGastosReconocidosTransferenciasCuentaPerdidasGananciasValoracionInstrumentosFinancierosActivosFinancierosDisponiblesVenta");
//            datosConversion.Add("50082", "pgc-07-n:CambiosPatrimonioNetoIngresosGastosReconocidosTransferenciasCuentaPerdidasGananciasValoracionInstrumentosFinancierosOtrosIngresosOtrosGastos");
//            datosConversion.Add("50110", "pgc-07-c-bs:CambiosPatrimonioNetoIngresosGastosReconocidosTransferenciasCuentaPerdidasGananciasActivosNoCorrientesPasivosVinculadosMantenidosVenta");
//            datosConversion.Add("50120", "pgc-07-c-bs:CambiosPatrimonioNetoIngresosGastosReconocidosTransferenciasCuentaPerdidasGananciasDiferenciasConversion");
//
//            // Estado de Flujos de Efectivo
//            datosConversion.Add("61100", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionResultadoAntesImpuestos");
//            datosConversion.Add("61200", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionAjustesResultado");
//            datosConversion.Add("61201", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionAjustesResultadoAmortizacionInmovilizado");
//            datosConversion.Add("61202", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionAjustesResultadoCorreccionesValorativasDeterioro");
//            datosConversion.Add("61203", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionAjustesResultadoVariacionProvisiones");
//            datosConversion.Add("61204", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionAjustesResultadoImputacionSubvenciones");
//            datosConversion.Add("61205", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionAjustesResultadoResultadosBajasEnajenacionesInmovilizado");
//            datosConversion.Add("61206", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionAjustesResultadoResultadosBajasEnajenacionesInstrumentosFinancieros");
//            datosConversion.Add("61207", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionAjustesResultadoIngresosFinancieros");
//            datosConversion.Add("61208", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionAjustesResultadoGastosFinancieros");
//            datosConversion.Add("61209", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionAjustesResultadoDiferenciasCambio");
//            datosConversion.Add("61210", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionAjustesResultadoVariacionvalorRazonableInstrumentosFinancieros");
//            datosConversion.Add("61211", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionAjustesResultadoOtrosIngresosGastos");
//            datosConversion.Add("61300", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionCambiosCapitalCorriente");
//            datosConversion.Add("61301", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionCambiosCapitalCorrienteExistencias");
//            datosConversion.Add("61302", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionCambiosCapitalDeudores");
//            datosConversion.Add("61303", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionCambiosCapitalOtrosActivosCorrientes");
//            datosConversion.Add("61304", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionCambiosCapitalCorrienteAcreedores");
//            datosConversion.Add("61305", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionCambiosCapitalCorrienteOtrosPasivosCorrientes");
//            datosConversion.Add("61306", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionCambiosCapitalCorrienteOtrosActivosPasivosNoCorrientes");
//            datosConversion.Add("61400", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionOtrosFlujosEfectivoActividadesExplotacion");
//            datosConversion.Add("61401", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionOtrosFlujosEfectivoActividadesExplotacionPagoIntereses");
//            datosConversion.Add("61402", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionOtrosFlujosEfectivoActividadesExplotacionCobrosDividendos");
//            datosConversion.Add("61403", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionOtrosFlujosEfectivoActividadesExplotacionCobrosIntereses");
//            datosConversion.Add("61404", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionOtrosFlujosEfectivoActividadesExplotacionCobrosImpuestosBeneficios");
//            datosConversion.Add("61405", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionOtrosFlujosEfectivoActividadesExplotacionOtrosPagos");
//            datosConversion.Add("61500", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesExplotacionFlujosEfectivoActividadesExplotacion");
//            datosConversion.Add("62100", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesInversionPagoInversiones");
//            datosConversion.Add("62101", "pgc-07-n-fe:EstadEstadoFlujosEfectivoActividadesInversionPagoInversionesEmpresasGrupoEmpresasAsociadas");
//            datosConversion.Add("62102", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesInversionPagoInversionesInmovilizadoIntangible");
//            datosConversion.Add("62103", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesInversionPagoInversionesInmovilizadoMaterial");
//            datosConversion.Add("62104", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesInversionPagoInversionesInversionesInmobilizarias");
//            datosConversion.Add("62105", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesInversionPagoInversionesOtrosActivosFinancieros");
//            datosConversion.Add("62106", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesInversionPagoInversionesActivosNoCorrientesMantenidosVenta");
//            datosConversion.Add("62108", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesInversionPagoInversionesOtrosActivos");
//            datosConversion.Add("62107", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesInversionPagoInversionesUnidadNegocio");
//            datosConversion.Add("62200", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesInversionCobrosDesinversiones");
//            datosConversion.Add("62201", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesInversionCobrosDesinversionesEmpresasGrupoEmpresasAsociadas");
//            datosConversion.Add("62202", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesInversionCobrosDesinversionesInmovilizadoIntangible");
//            datosConversion.Add("62203", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesInversionCobrosDesinversionesInmovilizadoMaterial");
//            datosConversion.Add("62204", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesInversionCobrosDesinversionesInversionesInmobiliarias");
//            datosConversion.Add("62205", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesInversionCobrosDesinversionesOtrosActivosFinancieros");
//            datosConversion.Add("62206", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesInversionCobrosDesinversionesActivosNoCorrientesMantenidosVenta");
//            datosConversion.Add("62208", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesInversionCobrosDesinversionesOtrosActivos");
//            datosConversion.Add("62207", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesInversionCobrosDesinversionesUnidadNegocio");
//            datosConversion.Add("62300", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesInversionCobrosDesinversionesFlujosEfectivoActividadesInversion");
//            datosConversion.Add("63100", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionCobrosPagosInstrumentosPatrimonio");
//            datosConversion.Add("63101", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionCobrosPagosInstrumentosPatrimonioEmisionInstrumentosPatrimonio");
//            datosConversion.Add("63102", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionCobrosPagosInstrumentosPatrimonioAmortizacionInstrumentosPatrimonio");
//            datosConversion.Add("63103", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionCobrosPagosInstrumentosPatrimonioAdquisicionInstrumentosPatrimonioPropio");
//            datosConversion.Add("63104", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionCobrosPagosInstrumentosPatrimonioEnajecaionInstrumentosPatrimonioPropio");
//            datosConversion.Add("63105", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionCobrosPagosInstrumentosPatrimonioSubvencionesDonancionesLegados");
//            datosConversion.Add("63200", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionCobrosPagosInstrumentosPasivoFinanciero");
//            datosConversion.Add("63201", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionCobrosPagosInstrumentosPasivoFinancieroEmision");
//            datosConversion.Add("63202", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionCobrosPagosInstrumentosPasivoFinancieroEmisionOblifacionesOtrosValoresNegociables");
//            datosConversion.Add("63203", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionCobrosPagosInstrumentosPasivoFinancieroEmisionDeudasEntidadesCredito");
//            datosConversion.Add("63204", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionCobrosPagosInstrumentosPasivoFinancieroEmisionDeudasEmpresasGrupoAsociadas");
//            datosConversion.Add("63206", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionCobrosPagosInstrumentosPasivoFinancieroEmisionOtrasDeudas");
//            datosConversion.Add("63205", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionCobrosPagosInstrumentosPasivoFinancieroEmisionDeudasCaracteristicasEspeciales");
//            datosConversion.Add("63207", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionCobrosPagosInstrumentosPasivoFinancieroDevolucionAmortizacion");
//            datosConversion.Add("63208", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionCobrosPagosInstrumentosPasivoFinancieroDevolucionAmortizacionObligaciones");
//            datosConversion.Add("63209", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionCobrosPagosInstrumentosPasivoFinancieroDevolucionAmortizacionDeudasEntidadesCredito");
//            datosConversion.Add("63210", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionCobrosPagosInstrumentosPasivoFinancieroDevolucionAmortizacionDeudasEmpresasGrupo");
//            datosConversion.Add("63212", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionCobrosPagosInstrumentosPasivoFinancieroDevolucionAmortizacionOtrasDeudas");
//            datosConversion.Add("63211", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionCobrosPagosInstrumentosPasivoFinancieroDevolucionAmortizacionDeudasCaracteristicasEspeciales");
//            datosConversion.Add("63300", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionPagosDividendosRemuneracionesOtrosInstrumentosPatrimonio");
//            datosConversion.Add("63301", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionPagosDividendosRemuneracionesOtrosInstrumentosPatrimonioDividendos");
//            datosConversion.Add("63302", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionPagosDividendosRemuneracionesOtrosInstrumentosPatrimonioRemuneracion");
//            datosConversion.Add("63400", "pgc-07-n-fe:EstadoFlujosEfectivoActividadesFinanciacionFlujosEfectivoActividadesFinanciacion");
//            datosConversion.Add("64000", "pgc-07-n-fe:EstadoFlujosEfectivoEfectoVariacionesTiposCambio");
//            datosConversion.Add("65000", "pgc-07-n-fe:EstadoFlujosEfectivoAumentoDisminucionEfectivoEquivalentes");
//            datosConversion.Add("65100", "pgc-07-n-fe:EstadoFlujosEfectivoEfectivoEquivalentesComienzoEjercicio");
//            datosConversion.Add("65200", "pgc-07-n-fe:EstadoFlujosEfectivoEfectivoEquivalentesFinalEjercicio");
//
//            // Estado Total de Cambios en el Patrimonio Neto
//            datosConversion.Add("511","pgc07cbs-dvs:VariacionSaldoSaldoInicioEjercicio");
//            datosConversion.Add("512","pgc07cbs-dvs:VariacionSaldoSaldoInicioEjercicioAjustesCambiosCriterio");
//            datosConversion.Add("513","pgc07cbs-dvs:VariacionSaldoSaldoInicioEjercicioAjustesErroresEjercicio");
//            datosConversion.Add("514","pgc07cbs-dvs:VariacionSaldoSaldoAjustadoInicioEjercicio");
//            datosConversion.Add("515","pgc07cbs-dvs:VariacionSaldoSaldoAjustadoInicioEjercicioTotalIngresosGastosReconocidos");
//            datosConversion.Add("516","pgc07cbs-dvs:VariacionSaldoSaldoAjustadoInicioEjercicioOperacionesSociosOperacionesPropietarios");
//            datosConversion.Add("517","pgc07cbs-dvs:VariacionSaldoSaldoAjustadoInicioEjercicioOperacionesSociosOperacionesPropietariosAumentosCapital");
//            datosConversion.Add("518","pgc07cbs-dvs:VariacionSaldoSaldoAjustadoInicioEjercicioOperacionesSociosOperacionesPropietariosReduccionesCapital");
//            datosConversion.Add("519","pgc07cbs-dvs:VariacionSaldoSaldoAjustadoInicioEjercicioOperacionesSociosOperacionesPropietariosConversionPasivosFinancierosPatrimonioNetoConversionObligacionesCondonacionesDeudas");
//            datosConversion.Add("520","pgc07cbs-dvs:VariacionSaldoSaldoAjustadoInicioEjercicioOperacionesSociosOperacionesPropietariosDistribucionDividendos");
//            datosConversion.Add("521","pgc07cbs-dvs:VariacionSaldoSaldoAjustadoInicioEjercicioOperacionesSociosOperacionesPropietariosOperacionesAccionesParticipacionesPropias");
//            datosConversion.Add("522","pgc07cbs-dvs:VariacionSaldoSaldoAjustadoInicioEjercicioOperacionesSociosOperacionesPropietariosIncrementoReduccionPatrimonioNetoResultanteCombinacionNegocios");
//            datosConversion.Add("523","pgc07cbs-dvs:VariacionSaldoSaldoAjustadoInicioEjercicioOperacionesSociosOperacionesPropietariosOtrasOperacionesSociosOperacionesPropietarios");
//            datosConversion.Add("524","pgc07cbs-dvs:VariacionSaldoSaldoAjustadoInicioEjercicioOtrasVariacionesPatrimonioNeto");
//            datosConversion.Add("531","pgc07cbs-dvs:VariacionSaldoSaldoAjustadoInicioEjercicioOtrasVariacionesPatrimonioNetoMovimientoReservaRevalorizacion");
//            datosConversion.Add("532","pgc07cbs-dvs:VariacionSaldoSaldoAjustadoInicioEjercicioOtrasVariacionesPatrimonioNetoOtrasVariaciones");
//            datosConversion.Add("525","pgc07cbs-dvs:VariacionSaldoSaldoFinalEjercicio");
//        }

    }
    
    // Añadir elementos de balance, pyg, etc, etc
//    private void AddElemento(Element padre, String nombre, String contexto)
//    {
//        //<prefijo:nombre contextRef="CONTEXTO" decimals="0" id="id_footnote_elem_x" unitRef="euro">value</prefijo:nombre>
//
//        // La casilla es el item.id
////        string nombre = ObtenerNombre(item.id); // Se obtiene en funcion de la casilla
//
//        if (nombre != "")
//        {
//            // Obtener el prefijo y el nombre (estarán separados por ":" en la cadena que devuelve ObtenerNombre)
//            string[] temp = nombre.Split(':');
//            string prefijo = temp[0];
//            nombre = temp[1];
//
//            // Comprobar si lleva Nota de la Memoria
//            string idNota = ""; // sale del item.note
//            if (item.note != null && item.note.Length > 0)
//            {
//                // Añadimos la nota al array de las notas porque se pondrá al final del XBRL                    
//                idNota = "id_foodnote_" + notas.Count.ToString();
//                notas.Add(item.note[0].text);
//            }
//
//            // Añadir el elemento al XBRL
//            AddElemento(padre, prefijo, nombre, contexto, "0.0", "0", "euro", "");
//
//            // PRUEBA - Poner en el valor la casilla, para ver que todas se rellenan correctamente
//            // para el ejercicio anterior, será la casilla dividido para 100
//            // Tambien se añaden todas las notas de la memoria
//            //decimal value = Convertir.ToDecimal(item.id);
//            //if (contexto.Contains("ANTERIOR"))
//            //    value = value / 100;
//            //string valueStr = value.ToString("##########0.##;;", CultureInfo.GetCultureInfo("en-US"));
//            //string idNota = "id_foodnote_" + notas.Count.ToString();
//            //notas.Add("N" + item.id);
//            //AddElemento(padre, prefijo, nombre, contexto, valueStr, "0", "euro", idNota);
//            // ------
//        }
//    }

    // Añadir elemento: Balance y Cuenta PYG
    private static void addElementoBal(Element padre, String key) {
    	
    	String nombre = datosConversion.get(key);
    	
    	// Ejercicio Actual
    	String valor = key; // Se busca la clave en el XML y se obtiene el valor (SE PONE EL VALOR DE LA CASILLA PARA PROBAR)
    	 
    	Contexto contexto = null; // Contexto (Balance = Instant; PyG, EIGR y EFE = Duration)
        if (key.startsWith("4"))
        	contexto = Contexto.D_ACTUAL;  // PYG o EIGR o EFE
        else
            contexto = Contexto.I_ACTUAL;  // Balance
        
      // Comprobar si lleva Nota de la Memoria
      String nota = key+"98"; // Se busca la clave de la nota en el XML (clave+"98")
      String idNota = ""; 
      if (AonStringUtils.isNotBlank(nota)) {
          // Añadimos la nota al array de las notas porque se pondrá al final del XBRL                    
          idNota = "id_foodnote_" + notas.size();
          notas.add(nota);
      }
      
      addElemento(padre, nombre, contexto, valor, "2", "euro", idNota); // Añadir el elemento actual
      
      // Añadir el elemento anterior , si existe
      valor = key+".9"; // Se busca en el XML (clave + "9") (SE PONE EL VALOR DE LA CASILLA PARA PROBAR)
      if (key.startsWith("4"))
      	contexto = Contexto.D_ANTERIOR;  // PYG o EIGR o EFE
      else
        contexto = Contexto.I_ANTERIOR;  // Balance
      
      addElemento(padre, nombre, contexto, valor, "2", "euro", ""); // Añadir el elemento anterior

    }
    
    private static void addElementoIde(Element padre, String nombre, Contexto contexto, String key) {
    	addElementoIde(padre, nombre, contexto, key, null, null);
    }
    
    private static void addElementoIde(Element padre, String nombre, Contexto contexto, String key, String unit, String decimals) {
    	// Se busca la clave en el XML y si contiene datos se añade el elemento
    	String valor = datosIdentificacion.get(key); // PARA PROBAR SE BUSCA EN ESTE MAP
    	
    	if (AonStringUtils.isNotBlank(valor)) {
    		addElemento(padre, nombre, contexto, valor, decimals, unit, null);
    	}
    }
    
    private static Element addElemento(Element padre, String nombre) {
    	return addElemento(padre, nombre, null, null, null, null, null);
    }
    
    private static Element addElemento(Element padre, String nombre, Contexto contexto, String valor) {
    	return addElemento(padre, nombre, contexto, valor, null, null, null);
    }
    
    private static Element addElemento(Element padre, String nombre, Contexto contexto, String valor, String decimals, String unitRef, String idNota)
    {
        Element ele = padre.getOwnerDocument().createElement(nombre);
        if (contexto != null)
        {
            ele.setAttribute("contextRef", contexto.toString());
        }
        if (AonStringUtils.isNotBlank(decimals))
        {
            ele.setAttribute("decimals", decimals);
        }
        if (AonStringUtils.isNotBlank(unitRef))
        {
            ele.setAttribute("unitRef", unitRef);
        }
        if (AonStringUtils.isNotBlank(idNota))
        {
            ele.setAttribute("id", idNota);
        }
        if (AonStringUtils.isNotBlank(valor))
        {
            ele.setTextContent(valor);
        }
        padre.appendChild(ele);
        return ele;
    }
    
    private static void addNotasMemoria(Document doc)
    {
//        string linkUri = ObtenerUri("link");
//        string xlinkUri = ObtenerUri("xlink");

        // Bloque para poner todas las notas
        //<link:footnoteLink xlink:role="http://www.xbrl.org/2003/role/link" xlink:type="extended">
        //    <link:loc xlink:href="#id_footnote_elem_b11cea7a-c259-42f3-8203-6bfccd196368" xlink:label="PerdidasGananciasOperacionesContinuadasImporteNetoCifraNegocios_b11cea7a-c259-42f3-8203-6bfccd196368" xlink:type="locator"/>
        //    <link:footnoteArc order="1" xlink:arcrole="http://www.xbrl.org/2003/arcrole/fact-footnote" xlink:from="PerdidasGananciasOperacionesContinuadasImporteNetoCifraNegocios_b11cea7a-c259-42f3-8203-6bfccd196368" xlink:title="" xlink:to="footnote_ad908b82-93e6-452d-ab89-f00150434371" xlink:type="arc"/>
        //    <link:footnote xlink:label="footnote_ad908b82-93e6-452d-ab89-f00150434371" xlink:role="http://www.xbrl.org/2003/role/footnote" xlink:type="resource" xml:lang="es">5 y 6     </link:footnote>
        //</link:footnoteLink>

        if (notas != null && !notas.isEmpty())
        {
            Element ele = doc.createElement("link:footnoteLink");
            ele.setAttribute("type", "extended");

            for (int i = 0; i < notas.size(); i++)
            {
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
    
    // Hoja Identificacion
    private static void addIdentificacion(Document doc, Date fechaIniActual, Date fechaFinActual, Date fechaIniAnterior, Date fechaFinAnterior)
    {	
        Element ele;
        Element ele2;
        Element ele3;

        // Identificación de la Empresa
        ele = addElemento(doc.getDocumentElement(), "pgc07mc-apdo0:IdentificacionEmpresaTupla");
        addElemento(ele, "dgi-lc-es:Xcode_IDC.NIF", Contexto.D_ACTUAL, "NIF");
        addElementoIde(ele, "dgi-est-gen:IdentifierValue", Contexto.D_ACTUAL, "01010"); // NIF Clave XML 01010
        addElementoIde(ele, "dgi-lc-es:Xcode_LFC.001", Contexto.D_ACTUAL, "01011");  // Forma juridica SA: Clave 01011                    
        addElementoIde(ele, "dgi-lc-es:Xcode_LFC.023", Contexto.D_ACTUAL, "01012");  // Forma juridica SL: Clave 01012                
        addElementoIde(ele, "dgi-gen-ex:OthersLegalForm", Contexto.D_ACTUAL, "01013"); // Forma juridica Otras: Clave 01013
        addElementoIde(ele, "dgi-lc-es:Xcode_IDC.LEI", Contexto.D_ACTUAL, "01009"); // LEI: Clave 01009
        addElemento(ele, "dgi-lc-es:Xcode_NMT.DS", Contexto.D_ACTUAL, "DS");
        addElementoIde(ele, "dgi-est-gen:LegalNameValue", Contexto.D_ACTUAL, "01020"); // Denominación Social: Clave 01020
        addElemento(ele, "dgi-lc-es:Xcode_ADL.01", Contexto.D_ACTUAL, "01");
        addElementoIde(ele, "dgi-est-gen:AddressLine", Contexto.D_ACTUAL, "01022");    // Domicilio social
        addElementoIde(ele, "dgi-est-gen:MunicipalityName", Contexto.D_ACTUAL, "01023"); // Municipio
        addElementoIde(ele, "dgi-est-gen:SpecifyRegion", Contexto.D_ACTUAL, "01025"); // Provincia
        addElementoIde(ele, "dgi-est-gen:ZipPostalCode", Contexto.D_ACTUAL, "01024"); // Código postal
        addElementoIde(ele, "dgi-est-gen:CommunicationValue", Contexto.D_ACTUAL, "01031"); // Teléfono
        addElementoIde(ele, "pgc07mc-apdo0:CommunicationValueEMail", Contexto.D_ACTUAL, "01037"); // Dirección email

        // Unidades (siempre Euros (unidad))
        // PYMES
//        if (ca.formato == "P")
//        {
            addElemento(doc.getDocumentElement(), "dgi-lc-es:Xcode_COT.01", Contexto.D_ACTUAL, "01"); // UNIDAD EUROS
//        }
        // ABREVIADO, NORMAL Y MIXTO : UNIDAD EN ABREVIADO PUEDE SER MILES DE EUROS O MILLONES DE EUROS
//        else if (ca.formato == "A" || ca.formato == "N" || ca.formato == "M")
//        {
//            ele = AddElemento(doc.DocumentElement, "pgc07mc-apdo0", "UnidadesTupla");
//            AddElemento(ele, "dgi-lc-es", "Xcode_COT.01", Contexto.D_ACTUAL.ToString(), "01"); // UNIDAD EUROS
//        }

        // Pertenencia a Grupo de Sociedades (solo Abreviado o Normal)
//        if (ca.formato == "A" || ca.formato == "N" || ca.formato == "M")
//        {
//            ele = AddElemento(doc.DocumentElement, "pgc07mc-apdo0", "PertenenciaGrupoSociedadesTupla");
//            ele2 = AddElemento(ele, "pgc07mc-apdo0", "SociedadesDominanteDirectaTupla");
//            AddElemento(ele2, "dgi-lc-es", "Xcode_PET.03", Contexto.D_ACTUAL.ToString(), "03"); 
//            ele3 = AddElemento(ele2, "pgc07mc-apdo0", "IdentificacionSociedadTupla");
//            AddElemento(ele3, "dgi-lc-es", "Xcode_IDC.NIF", Contexto.D_ACTUAL.ToString(), "NIF");
//            AddElemento(ele3, "dgi-est-gen", "IdentifierValue", Contexto.D_ACTUAL.ToString(), Convertir.ToStringEmpty(ca.tablaEjercicios[0]["NIFSOCIDIRECTA"]));
//            AddElemento(ele3, "dgi-lc-es", "Xcode_NMT.DS", Contexto.D_ACTUAL.ToString(), "DS");
//            AddElemento(ele3, "dgi-est-gen", "LegalNameValue", Contexto.D_ACTUAL.ToString(), Convertir.ToStringEmpty(ca.tablaEjercicios[0]["SOCIDOMDIRECTA"]));
//
//            ele2 = AddElemento(ele, "pgc07mc-apdo0", "SociedadesDominanteUltimaTupla");
//            AddElemento(ele2, "dgi-lc-es", "Xcode_PET.04", Contexto.D_ACTUAL.ToString(), "04"); 
//            ele3 = AddElemento(ele2, "pgc07mc-apdo0", "IdentificacionSociedadTupla");
//            AddElemento(ele3, "dgi-lc-es", "Xcode_IDC.NIF", Contexto.D_ACTUAL.ToString(), "NIF");
//            AddElemento(ele3, "dgi-est-gen", "IdentifierValue", Contexto.D_ACTUAL.ToString(), Convertir.ToStringEmpty(ca.tablaEjercicios[0]["NIFSOCIULTI"]));
//            AddElemento(ele3, "dgi-lc-es", "Xcode_NMT.DS", Contexto.D_ACTUAL.ToString(), "DS");
//            AddElemento(ele3, "dgi-est-gen", "LegalNameValue", Contexto.D_ACTUAL.ToString(), Convertir.ToStringEmpty(ca.tablaEjercicios[0]["SOCIDOMULTI"]));
//        }

        // Actividad 
        ele = addElemento(doc.getDocumentElement(), "pgc07mc-apdo0:ActividadTupla");
        addElementoIde(ele, "dgi-eco-bas:ActivityDescription", Contexto.D_ACTUAL, "02009");
        ele = addElemento(ele, "dgi-eco-bas:ActivityCodeCNAE2009");
        addElementoIde(ele, "dgi-cnae-09:Xcode_ACC.CNAE." + datosIdentificacion.get("02001"), Contexto.D_ACTUAL, "02001");

        // Personal Asalariado - Ejercicio Actual
        ele = addElemento(doc.getDocumentElement(), "pgc07mc-apdo0:PersonalAsalariadoTupla");
        ele2 = addElemento(ele, "pgc07mc-apdo0:NumeroMediopersonasEmpleadasCursoEjercicioTipoContratoEmpleoDiscapacidad");
        addElementoIde(ele2, "dgi-eco-bas:PermanentContractTotal", Contexto.D_ACTUAL, "04001", "pure", "2");
        addElementoIde(ele2, "dgi-eco-bas:TemporaryContractTotal", Contexto.D_ACTUAL, "04002", "pure", "2");
        addElementoIde(ele2, "dgi-eco-bas:EmployeesDisabilityLevelHigher33Total", Contexto.D_ACTUAL, "04010", "pure", "2");
        ele2 = addElemento(ele, "pgc07mc-apdo0:PersonalAsalariadoTerminoEjercicioTipoContratoSexo");
        ele3 = addElemento(ele2, "pgc07mc-apdo0:PersonalAsalariadoTerminoEjercicioTipoContratoFijo");
        addElementoIde(ele3, "dgi-eco-bas:Male", Contexto.D_ACTUAL, "04120", "pure", "0");
        addElementoIde(ele3, "dgi-eco-bas:Female", Contexto.D_ACTUAL, "04121", "pure", "0");
        ele3 = addElemento(ele2, "pgc07mc-apdo0:PersonalAsalariadoTerminoEjercicioTipoContratoNoFijo");
        addElementoIde(ele3, "dgi-eco-bas:Male", Contexto.D_ACTUAL, "04122", "pure", "0");
        addElementoIde(ele3, "dgi-eco-bas:Female", Contexto.D_ACTUAL, "04123", "pure", "0");

        // Personal Asalariado - Ejercicio Anterior
        if (fechaIniAnterior != null && fechaFinAnterior != null)
        {
	        ele = addElemento(doc.getDocumentElement(), "pgc07mc-apdo0:PersonalAsalariadoTupla");
	        ele2 = addElemento(ele, "pgc07mc-apdo0:NumeroMediopersonasEmpleadasCursoEjercicioTipoContratoEmpleoDiscapacidad");
	        addElementoIde(ele2, "dgi-eco-bas:PermanentContractTotal", Contexto.D_ANTERIOR, "040019", "pure", "2");
	        addElementoIde(ele2, "dgi-eco-bas:TemporaryContractTotal", Contexto.D_ANTERIOR, "040029", "pure", "2");
	        addElementoIde(ele2, "dgi-eco-bas:EmployeesDisabilityLevelHigher33Total", Contexto.D_ANTERIOR, "040109", "pure", "2");
	        ele2 = addElemento(ele, "pgc07mc-apdo0:PersonalAsalariadoTerminoEjercicioTipoContratoSexo");
	        ele3 = addElemento(ele2, "pgc07mc-apdo0:PersonalAsalariadoTerminoEjercicioTipoContratoFijo");
	        addElementoIde(ele3, "dgi-eco-bas:Male", Contexto.D_ANTERIOR, "041209", "pure", "0");
	        addElementoIde(ele3, "dgi-eco-bas:Female", Contexto.D_ANTERIOR, "041219", "pure", "0");
	        ele3 = addElemento(ele2, "pgc07mc-apdo0:PersonalAsalariadoTerminoEjercicioTipoContratoNoFijo");
	        addElementoIde(ele3, "dgi-eco-bas:Male", Contexto.D_ANTERIOR, "041229", "pure", "0");
	        addElementoIde(ele3, "dgi-eco-bas:Female", Contexto.D_ANTERIOR, "041239", "pure", "0");
        }

        // Presentación de Cuentas - Ejercicio Actual
        ele = addElemento(doc.getDocumentElement(), "pgc07mc-apdo0:PresentacionCuentasTupla");
        addElemento(ele, "pgc07mc-apdo0:YearFechaInicioCuentas", Contexto.D_ACTUAL, DATE_FORMAT_YEAR.format(fechaIniActual));
        addElemento(ele, "pgc07mc-apdo0:MonthFechaInicioCuentas", Contexto.D_ACTUAL, DATE_FORMAT_MONTH.format(fechaIniActual));
        addElemento(ele, "pgc07mc-apdo0:DayFechaInicioCuentas", Contexto.D_ACTUAL, DATE_FORMAT_DAY.format(fechaIniActual));
        addElemento(ele, "pgc07mc-apdo0:YearFechaCierreCuentas", Contexto.D_ACTUAL, DATE_FORMAT_YEAR.format(fechaFinActual));
        addElemento(ele, "pgc07mc-apdo0:MonthFechaCierreCuentas", Contexto.D_ACTUAL, DATE_FORMAT_MONTH.format(fechaFinActual));
        addElemento(ele, "pgc07mc-apdo0:DayFechaCierreCuentas", Contexto.D_ACTUAL, DATE_FORMAT_DAY.format(fechaFinActual));
        addElementoIde(ele, "dgi-dat-inf:TotalPagesPresented", Contexto.D_ACTUAL, "01901", "pure", "0");
        addElementoIde(ele, "dgi-dat-inf:AbsenceFinancialStatementPurpose", Contexto.D_ACTUAL, "01903");

        // Presentación de Cuentas - Ejercicio Anterior
        if (fechaIniAnterior != null && fechaFinAnterior != null)
        {
            ele = addElemento(doc.getDocumentElement(), "pgc07mc-apdo0:PresentacionCuentasTupla");
            addElemento(ele, "pgc07mc-apdo0:YearFechaInicioCuentas", Contexto.D_ANTERIOR, DATE_FORMAT_YEAR.format(fechaIniAnterior));
            addElemento(ele, "pgc07mc-apdo0:MonthFechaInicioCuentas", Contexto.D_ANTERIOR, DATE_FORMAT_MONTH.format(fechaIniAnterior));
            addElemento(ele, "pgc07mc-apdo0:DayFechaInicioCuentas", Contexto.D_ANTERIOR, DATE_FORMAT_DAY.format(fechaIniAnterior));
            addElemento(ele, "pgc07mc-apdo0:YearFechaCierreCuentas", Contexto.D_ANTERIOR, DATE_FORMAT_YEAR.format(fechaFinAnterior));
            addElemento(ele, "pgc07mc-apdo0:MonthFechaCierreCuentas", Contexto.D_ANTERIOR, DATE_FORMAT_MONTH.format(fechaFinAnterior));
            addElemento(ele, "pgc07mc-apdo0:DayFechaCierreCuentas", Contexto.D_ANTERIOR, DATE_FORMAT_DAY.format(fechaFinAnterior));                
        }

//        if (ca.formato == "P")
//        {
//            // Microempresas (Solo PYMES)
            addElementoIde(doc.getDocumentElement(), "pgc07mp-apdo0:CasoAdopcionConjuntaCrteriosEspecificosMicroPyme", Contexto.D_ACTUAL, "01902"); // ) == "S" ? "true" : "false");
//        }
//        
//        // Ejercicio 2022, nuevo campo "Porcentaje de mujeres en el órgano de administración"
//        if (Global.Ejercicio == 2022)
//        {
//            AddElemento(doc.DocumentElement, "pgc07mc-apdo0", "PorcentajeMujeresOrganoAdministracion", Contexto.D_ACTUAL.ToString(), "pure", Convertir.ToDecimal(ca.tablaEjercicios[0]["MUJERES_ACT"]));
//            if (fechaIniAnterior != null && fechaFinAnterior != null)
//                AddElemento(doc.DocumentElement, "pgc07mc-apdo0", "PorcentajeMujeresOrganoAdministracion", Contexto.D_ANTERIOR.ToString(), "pure", Convertir.ToDecimal(ca.tablaEjercicios[0]["MUJERES_ANT"]));
//        }
//
//        // A partir del ejercicio 2023, nuevos campos
//        if (Global.Ejercicio >= 2023)
//        {
//            // <dgi-eco-bas:NumeroMujeresOrganoAdministracion decimals="0" contextRef="D.ACTUAL" unitRef="pure">0</dgi-eco-bas:NumeroMujeresOrganoAdministracion>
//            // <dgi-eco-bas:NumeroMujeresOrganoAdministracion decimals="0" contextRef="D.ANTERIOR" unitRef="pure">0</dgi-eco-bas:NumeroMujeresOrganoAdministracion>
//            // <dgi-eco-bas:NumeroTotalMiembrosOrganoAdministracion decimals="0" contextRef="D.ACTUAL" unitRef="pure">0</dgi-eco-bas:NumeroTotalMiembrosOrganoAdministracion>
//            // <dgi-eco-bas:NumeroTotalMiembrosOrganoAdministracion decimals="0" contextRef="D.ANTERIOR" unitRef="pure">0</dgi-eco-bas:NumeroTotalMiembrosOrganoAdministracion>
//
            addElementoIde(doc.getDocumentElement(), "dgi-eco-bas:NumeroMujeresOrganoAdministracion", Contexto.D_ACTUAL, "04212", "pure", "0");
//            if (fechaIniAnterior != null && fechaFinAnterior != null)
                addElementoIde(doc.getDocumentElement(), "dgi-eco-bas:NumeroMujeresOrganoAdministracion", Contexto.D_ANTERIOR, "042129", "pure", "0");
//
            addElementoIde(doc.getDocumentElement(), "dgi-eco-bas:NumeroTotalMiembrosOrganoAdministracion", Contexto.D_ACTUAL, "04213", "pure", "0");
//            if (fechaIniAnterior != null && fechaFinAnterior != null)
                addElementoIde(doc.getDocumentElement(), "dgi-eco-bas:NumeroTotalMiembrosOrganoAdministracion", Contexto.D_ANTERIOR, "042139", "pure", "0");
//        }
//
                
//        if (ca.formato == "A" || ca.formato == "P")
//        {
//            // Aplicación de Resultados y Periodo Medio de Pago a Proveedores - Ejercicio Actual (Abreviado y PYMES a partir de 2016)
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosBaseRepartoResultadoSaldoCuentaPerdidasGanancias", Contexto.D_ACTUAL, "91000", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosBaseRepartoResultadoRemanente", Contexto.D_ACTUAL, "91001", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosBaseRepartoResultadoReservasVoluntarias", Contexto.D_ACTUAL, "91002", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosRepartoOtrasReservasLibreDisposicion", Contexto.D_ACTUAL, "91003", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosBaseTotalBaseRepartoResultadoTotalAplicacion", Contexto.D_ACTUAL, "91004", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaResultadoAplicacionAReservaLegal", Contexto.D_ACTUAL, "91005", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionAReservasEspeciales", Contexto.D_ACTUAL, "91007", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionAReservasVoluntarias", Contexto.D_ACTUAL, "91008", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionADividendos", Contexto.D_ACTUAL, "91009", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionARemanenteYOtros", Contexto.D_ACTUAL, "91010", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionACompensacionPerdidasEjerciciosAnteriores", Contexto.D_ACTUAL, "91011", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionATotalAplicacionTotalBaseReparto", Contexto.D_ACTUAL, "91012", "euro", "2");
            addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:PeriodoMedioPagoPeriodoMedioPagoProveedores", Contexto.D_ACTUAL, "94705", "euro", "2");
//
//            // Aplicación de Resultados y Periodo Medio de Pago a Proveedores - Ejercicio Anterior (Abreviado y PYMES a partir de 2016)
//            if (fechaIniAnterior != null && fechaFinAnterior != null)
//            {
                addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosBaseRepartoResultadoSaldoCuentaPerdidasGanancias", Contexto.D_ANTERIOR, "910009", "euro", "2");
                addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosBaseRepartoResultadoRemanente", Contexto.D_ANTERIOR, "910019", "euro", "2");
                addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosBaseRepartoResultadoReservasVoluntarias", Contexto.D_ANTERIOR, "910029", "euro", "2");
                addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosRepartoOtrasReservasLibreDisposicion", Contexto.D_ANTERIOR, "910039", "euro", "2");
                addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosBaseTotalBaseRepartoResultadoTotalAplicacion", Contexto.D_ANTERIOR, "910049", "euro", "2");
                addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaResultadoAplicacionAReservaLegal", Contexto.D_ANTERIOR, "910059", "euro", "2");
//                if (ca.formato == "A") // ESTE DATO CREO QUE EN LAS CCAA DE 2024 YA NO SE USA
//                    AddElemento(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionAReservaFondoComercio", Contexto.D_ANTERIOR, "910069", "euro", "2");
                addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionAReservasEspeciales", Contexto.D_ANTERIOR, "910079", "euro", "2");
                addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionAReservasVoluntarias", Contexto.D_ANTERIOR, "910089", "euro", "2");
                addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionADividendos", Contexto.D_ANTERIOR, "910099", "euro", "2");
                addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionARemanenteYOtros", Contexto.D_ANTERIOR, "910109", "euro", "2");
                addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionACompensacionPerdidasEjerciciosAnteriores", Contexto.D_ANTERIOR, "910119", "euro", "2");
                addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:AplicacionResultadosPropuestaAplicacionATotalAplicacionTotalBaseReparto", Contexto.D_ANTERIOR, "910129", "euro", "2");
                addElementoIde(doc.getDocumentElement(), "pgc07mc-bs:PeriodoMedioPagoPeriodoMedioPagoProveedores", Contexto.D_ANTERIOR, "947059", "euro", "2");
//            }
//        }
    }
    
    public static void main(String[] args) {
//    	crearPersonasXML();
    	pruebaCrearXBRL();
//        try {
//            // Crear una instancia de DocumentBuilderFactory
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//
//            // Crear un nuevo documento XML
//            Document document = builder.newDocument();
//
//            // Crear el elemento raíz
//            Element rootElement = document.createElement("Personas");
//            document.appendChild(rootElement);
//
//            // Crear un elemento hijo
//            Element persona = document.createElement("Persona");
//            rootElement.appendChild(persona);
//
//            // Agregar atributos y elementos a "Persona"
//            persona.setAttribute("id", "1");
//
//            Element nombre = document.createElement("Nombre");
//            nombre.appendChild(document.createTextNode("Felix"));
//            persona.appendChild(nombre);
//
//            Element edad = document.createElement("Edad");
//            edad.appendChild(document.createTextNode("30"));
//            persona.appendChild(edad);
//
//            // Escribir el contenido del documento en un archivo XML
//            TransformerFactory transformerFactory = TransformerFactory.newInstance();
//            Transformer transformer = transformerFactory.newTransformer();
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//
//            DOMSource source = new DOMSource(document);
//            StreamResult result = new StreamResult(new File("c:\\tmp\\personas.xml"));
//
//            transformer.transform(source, result);
//
//            System.out.println("Archivo XML creado con éxito!");
//
//        } catch (ParserConfigurationException | TransformerException e) {
//            e.printStackTrace();
//        }
    }

}
