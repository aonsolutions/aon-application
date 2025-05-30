package net.aonsolutions.aon.gwt.ccaa.server.xbrl;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.esferalia.aon.watson.util.AonStringUtils;

public class PruebaLeerXML {
	
//	private static final String FILE_NAME_PLANTILLA = "C:\\TMP\\PlantillaPymesMemoria.xml"; // PYMES
//	private static final String FILE_NAME_CONVERSION = "C:\\TMP\\ConversionPymes.xml"; // PYMES
//	private static final String CONTEXTO = "CONTEXTOS_MEM_PYMES";
//	private static final String CONVERSION = "CONVERSION_MEM_CUADROS_PYMES";
	
	private static final String FILE_NAME_PLANTILLA = "C:\\TMP\\PlantillaAbreviadoMemoria.xml"; // ABREVIADO
	private static final String FILE_NAME_CONVERSION = "C:\\TMP\\ConversionAbreviado.xml"; // ABREVIADO
	private static final String CONTEXTO = "CONTEXTOS_MEM_ABREVIADO";
	private static final String CONVERSION = "CONVERSION_MEM_CUADROS_ABREVIADO";
	
    private static Map<String, String[]> ObtenerContext() {
    	Map<String, String[]> contextos = new LinkedHashMap<>();
        try {        	
            File xmlFile = new File(FILE_NAME_PLANTILLA);
            
            // Crear el constructor de documentos
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // Obtener el nodo raíz
            Element root = doc.getDocumentElement();
//            System.out.println("Raíz: " + root.getNodeName());
            System.out.println("----- CONTEXTOS -------------------------------------------------------------------------------------------");
            System.out.println("");

            // Obtener todos los nodos de cierto tipo (ejemplo: "elemento")
            NodeList nodeList = doc.getElementsByTagName("xbrli:context"); // Cambiar por el nombre deseado

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    
                    // Obtener ID
                    String id = comprobarContextRef(element.getAttribute("id"));
                    if (id.startsWith("Y1_")) {
//                    	System.out.println("Elemento encontrado: " + element.getTagName());
//                    	System.out.println("id: " + id);
                        NodeList childNodesList = element.getChildNodes();
                        for (int j = 0; j < childNodesList.getLength(); j++) {
                            Node childNode = childNodesList.item(j);
                            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                            	Element childElement = (Element) childNode;
//                            	System.out.println("Elemento hijo: " + childElement.getTagName());
//                            	if (childElement.getTagName() == "xbrli:period") {
//                            		NodeList list = childElement.getElementsByTagName("xbrli:instant");
//                            		if (list.getLength() > 0)
//                            			System.out.println("Period: Existe instant");
//                            	}
                            	if (childElement.getTagName() == "xbrli:scenario") {
//                            		Node child = childElement.getFirstChild();
//                            		Element element2 = (Element) child;
                            		
//                            		String dimension = element2.getAttribute("dimension");
//                            		String member = element2.getTextContent();
                            		
                            		NodeList childNodesList2 = childElement.getChildNodes();
                            		for (int k = 0; k < childNodesList2.getLength(); k++) {
                            			Node childNode2 = childNodesList2.item(k);
                            			if (childNode2.getNodeType() == Node.ELEMENT_NODE) {
                                        	Element childElement2 = (Element) childNode2;
//                                        	System.out.println("Elemento hijo: " + childElement2.getTagName());
                                        	String dimension = childElement2.getAttribute("dimension");
                                    		String member = childElement2.getTextContent();
//                                    		System.out.println("dimension: " + dimension);
//                                    		System.out.println("member: " + member);
                                    		System.out.println(CONTEXTO + ".put(\"" + id +"\", new String[]{\""+dimension+"\", \""+member+"\"});");
                                    		contextos.put(id,new String[] {dimension,member});
                            			}
                            		}
//                            		String dimension = childElement.getAttribute("dimension");
//                            		String member = childElement.getTextContent();
//                            		
//                            		System.out.println("dimension: " + dimension);
//                            		System.out.println("member: " + member);
                            	}
                            	
                            }
                        }
                        
//                        System.out.println("");
                    }
                    
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("");
        System.out.println("");
        return contextos;
    }
    
    private static void ObtenerConversion(Map<String, String[]> contextos) {
        try {
            File xmlFile = new File(FILE_NAME_PLANTILLA);
            
            // Crear el constructor de documentos
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            
            File xmlFileBuscar = new File(FILE_NAME_CONVERSION);
            
            // Crear el constructor de documentos
            DocumentBuilderFactory factoryBuscar = DocumentBuilderFactory.newInstance();
            DocumentBuilder builderBuscar = factoryBuscar.newDocumentBuilder();
            Document docBuscar = builderBuscar.parse(xmlFileBuscar);
            docBuscar.getDocumentElement().normalize();
            
            // Obtener todos los nodos del xml de conversion (se usara posteriormente para buscar la clave) 
            NodeList nodeListBuscar = docBuscar.getElementsByTagName("RELACION_XBRL_CLAVES");

            // Obtener el nodo raíz
            Element root = doc.getDocumentElement();
//            System.out.println("Raíz: " + root.getNodeName());
            System.out.println("----- CONVERSION ------------------------------------------------------------------------------------------");
            System.out.println("");

            // Obtener todos los nodos
            NodeList nodeList = doc.getElementsByTagName("*");
            
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String contextRef = comprobarContextRef(element.getAttribute("contextRef"));
                    String name = element.getTagName();
                    
                    //if (contextRef.startsWith("Y1_") || contextRef.startsWith("Context_Duration_") || contextRef.startsWith("Context_Instant_")) {
                    if (contextRef.startsWith("Y1_")) {
                    	
                    	// Buscar la clave en el otro XML
                    	String clave = buscar(nodeListBuscar, name, contextos, contextRef);
                    	
//                    	System.out.println("Elemento: " + element.getTagName());
//                    	System.out.println("contextRef: " + contextRef);
//                    	System.out.println("clave: " + clave);
                    	if (AonStringUtils.isNotBlank(clave))
                    		System.out.println(CONVERSION+".put(\""+clave+"\", new String[]{\"" + name + "\", \"" + contextRef + "\"});");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    private static String buscar(NodeList nodeListBuscar, String name, Map<String, String[]> contextos, String contextRef) {
    	
        for (int i = 0; i < nodeListBuscar.getLength(); i++) {
        	Node node = nodeListBuscar.item(i);
        	if (node.getNodeType() == Node.ELEMENT_NODE) {
        		Element element = (Element) node;
//        		System.out.println("Elemento: " + element.getTagName());
        		String codigo = "";
        		String clave = "";
        		String rol = "";
                NodeList nodeListChildBuscar = element.getChildNodes();
                for (int j = 0; j < nodeListChildBuscar.getLength(); j++) {
                	Node childNode = nodeListChildBuscar.item(j);
                	if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                		Element childElement = (Element) childNode;
                		if (AonStringUtils.equals(childElement.getTagName(), "CODIGO_XBRL")) {
                			codigo = childElement.getTextContent();
                		}
                		if (AonStringUtils.equals(childElement.getTagName(), "CLAVE")) {
                			clave = childElement.getTextContent();
                		}
                		if (AonStringUtils.equals(childElement.getTagName(), "ROL")) {
                			rol = childElement.getTextContent();
                		}
                	}
                }
//                System.out.println("Codigo: " + codigo);
//                System.out.println("Clave : " + clave);
//                System.out.println("Rol   : " + rol);
                
                String member = contextos.get(contextRef)[1];
                
                String codigoBuscar = name + "@" + member;
//                System.out.println("codigoBuscar: " + codigoBuscar);
                
                if (AonStringUtils.equals(codigo, codigoBuscar) && AonStringUtils.contains(contextRef,rol)) {
                	return clave;
                }
        	}
        }
		return "";
	}
    
    private static String comprobarContextRef(String contextRef) {
    	if (contextRef.startsWith("Context_Duration_") && contextRef.endsWith("_2"))
    		return contextRef.replace("Context_Duration_", "Y1_ANTERIOR_Duration_");
    	else if (contextRef.startsWith("Context_Instant_") && contextRef.endsWith("_2"))
    		return contextRef.replace("Context_Instant_", "Y1_ANTERIOR_Instant_");
    	else if (contextRef.startsWith("Context_Duration_"))
    		return contextRef.replace("Context_Duration_", "Y1_ACTUAL_Duration_");
    	else if (contextRef.startsWith("Context_Instant_"))
    		return contextRef.replace("Context_Instant_", "Y1_ACTUAL_Instant_");
    	else
    		return contextRef;
    }

	private static void LeerGrabar() {
        try {
            File xmlFile = new File("c:\\tmp\\plantillapymesmemoria.xml");
            
            // Crear el constructor de documentos
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // Obtener el nodo raíz
            Element root = doc.getDocumentElement();
            System.out.println("Raíz: " + root.getNodeName());
            System.out.println("");

            // Obtener todos los nodos de cierto tipo
            NodeList nodeList = doc.getElementsByTagName("xbrli:instant"); 

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    
                    String value = element.getTextContent();
                    System.out.println("Instant: " + value);
                    
                    if (AonStringUtils.equals(value, "@FECHA_CIERRE_ACTUAL@")) {
                    	element.setTextContent("2024-12-31");	
                    } else if (AonStringUtils.equals(value, "@FECHA_CIERRE_ANTERIOR@")) {
                    	element.setTextContent("2023-12-31");
                    }
                    
                    // Obtener ID
                    String id = element.getAttribute("id");
                    if (id.startsWith("Y1_")) {
//                    	System.out.println("Elemento encontrado: " + element.getTagName());
//                    	System.out.println("id: " + id);
                        NodeList childNodesList = element.getChildNodes();
                        for (int j = 0; j < childNodesList.getLength(); j++) {
                            Node childNode = childNodesList.item(j);
                            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                            	Element childElement = (Element) childNode;
//                            	System.out.println("Elemento hijo: " + childElement.getTagName());
//                            	if (childElement.getTagName() == "xbrli:period") {
//                            		NodeList list = childElement.getElementsByTagName("xbrli:instant");
//                            		if (list.getLength() > 0)
//                            			System.out.println("Period: Existe instant");
//                            	}
                            	if (childElement.getTagName() == "xbrli:scenario") {
//                            		Node child = childElement.getFirstChild();
//                            		Element element2 = (Element) child;
                            		
//                            		String dimension = element2.getAttribute("dimension");
//                            		String member = element2.getTextContent();
                            		
                            		NodeList childNodesList2 = childElement.getChildNodes();
                            		for (int k = 0; k < childNodesList2.getLength(); k++) {
                            			Node childNode2 = childNodesList2.item(k);
                            			if (childNode2.getNodeType() == Node.ELEMENT_NODE) {
                                        	Element childElement2 = (Element) childNode2;
//                                        	System.out.println("Elemento hijo: " + childElement2.getTagName());
                                        	String dimension = childElement2.getAttribute("dimension");
                                    		String member = childElement2.getTextContent();
//                                    		System.out.println("dimension: " + dimension);
//                                    		System.out.println("member: " + member);
//                                    		System.out.println("CONTEXTOS_MEM_PYMES.put(\"" + id +"\", new String[]{\""+dimension+"\", \""+member+"\"});");
                            			}
                            		}
//                            		String dimension = childElement.getAttribute("dimension");
//                            		String member = childElement.getTextContent();
//                            		
//                            		System.out.println("dimension: " + dimension);
//                            		System.out.println("member: " + member);
                            	}
                            	
                            }
                        }
                        
//                        System.out.println("");
                    }
//                    
//                    // Obtener el contenido del nodo
//                    String contenido = element.getTextContent();
//                    System.out.println("Contenido: " + contenido);
                }
            }
            
            // Grabar el archivo xml con otro nombre
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("C:\\TMP\\prueba.xml"));
            transformer.transform(source, result);
            System.out.println("Archivo XBRL creado con éxito!");            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public static void main(String[] args) {
//    	ObtenerContext();
    	ObtenerConversion(ObtenerContext());
//    	LeerGrabar();
    }
    
}
