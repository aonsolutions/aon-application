/**
 *
 * API XBRL-PGC2007 is a set of packages for the treatment of instances XBRL
 * (eXtensible Business Reporting Language) corresponding to the taxonomy PGC2007.
 * The General Plan of Accounting 2007 is the legal text that regulates the accounting of
 * the companies in Spain.
 *
 * This program is part of the API XBRL-PGC2007.
 *
 * Copyright (C) 2009  INTECO (Instituto Nacional de Tecnologías de la
 * Comunicación, S.A.)
 *
 * Authors: Members of Software Quality Department inside INTECO
 *
 * E-mail: difusioncalidad@inteco.es
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 3 of the License, or (at your opinion) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this program. If not, see http://www.gnu.org/licenses/
 */


package es.inteco.xbrl.pgc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.inteco.xbrl.pgc.errors.catalog.PGC07ErrorsCatalog;
import es.inteco.xbrl.pgc.errors.catalog.PGCError;
import es.inteco.xbrl.pgc.errors.catalog.types.PGCErrorTypeType;




/**
 *
 *
 * Clase que trabaja en la gestión de los diferentes catalogos de error
 *
 *
 *
 *<br><br>
 * <b>Proyecto</b>: API XBRL-PGC2007 - Grupo de utilidades y librerías en código abierto para facilitar 
 *                             la integración del formato XBRL en las herramientas software de gestión de  terceros
 *                             aislándose de la complejidad en el procesamiento del modelo de datos de las taxonomías.
 *                             Ayudando de esta forma a las empresas finales en la labor de realización de informes XBRL
 *                             y asegurar el éxito de implantación del nuevo Plan General de Contabilidad 2007 en formato XBRL
 *
 * @version 1.0, 14/01/2009
 * @author difusioncalidad@inteco.es
 *
 */


public abstract class GenerateErrorsCatalog
{

    private static final Logger logger = Logger.getLogger(GenerateErrorsCatalog.class);
    
    
    /**
	 * Constructor por defecto
	 */
	public GenerateErrorsCatalog() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
     * Obtiene un Element dado un name y tomando como referencia un Node
     * @param node
     * @param findName
     * @return
     * tipo Element
     */
    public static final Element getElementByName(Element node, String findName)
    {
	Node currentElement = null;
	boolean foundElement = false;
	NodeList nodeList = node.getChildNodes();
	int nodeNum  = nodeList.getLength();
	
	for (int i=0; ((i<nodeNum) && (!foundElement)) ; i++)
	{
	    currentElement = nodeList.item(i);
	    foundElement = currentElement.getNodeName().equals(findName);
		
	}
	if (!foundElement)
	{
	    currentElement = null;
	}
	
	return (Element)currentElement;
    }
    
    
    /**
     * Devuelve la lista de identificadores que hay en el error separados por coma(,)
     * @param errorMessage
     * @return
     * lista de idenfificadores
     */
    private static String getIdList(String errorMessage)
    {

	StringBuffer idList = new StringBuffer();
            try
	    {
		Pattern p = Pattern.compile("([{]+[0-9]+[}]+)");
		Matcher m = p.matcher(errorMessage);
		
		while(m.find()) 
		{
		    String id = errorMessage.substring(m.start()+1, m.end()-1);
		    if (idList.length() > 0)
		    {
			idList.append(",");
		    }
		    
		    idList.append(id);

		}
		
		
		
	    } catch (Exception e)
	    {
		logger.error(e);
	    }
	    
	    
	    
            return idList.toString();
        
    }
    
    
    /**
     * Agrega al catalogo de errores aquellos que sean de tipo XBREEZE
     * @param localizerPath
     * @param fromCount
     * @param errors
     * @return
     * Numero de errores generados
     */
    public static int generateXBReezeErrors(String localizerPath, int fromCount, PGC07ErrorsCatalog errors)
    {
	try
	{
	    System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

	    javax.xml.parsers.DocumentBuilderFactory dfactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
	    dfactory.setNamespaceAware(true);
	    dfactory.setIgnoringElementContentWhitespace(true);
	    Document doc = dfactory.newDocumentBuilder().parse(new FileInputStream(localizerPath));
	    
	    NodeList uriNodes = doc.getDocumentElement().getElementsByTagName("uri");
	    int numUriNodes = uriNodes.getLength();
	    
	    for (int i=0; i<numUriNodes; i++)
	    {
		Element currentNode = (Element)uriNodes.item(i);
		if ((currentNode.getAttribute("id").startsWith("memo://ubmatrix.com/Validation/Instance")) ||
			(currentNode.getAttribute("id").startsWith("memo://ubmatrix.com/Validation/Taxonomy") ) ||
			(currentNode.getAttribute("id").startsWith("memo://ubmatrix.com/Validation/Linkbase")) )
		{
		    PGCError error = new PGCError();
		    error.setErrorCode(fromCount++);
		    error.setId(currentNode.getAttribute("id"));
		    
		    Element localizeStringNode = getElementByName(currentNode, "LocalizedString");
		
		    
		    error.setParameterList(getIdList(localizeStringNode.getTextContent()));

		    error.setType(PGCErrorTypeType.XBREEZE);
		    
		    errors.addPGCError(error);		    
		}

	    }
	    
	} catch (Throwable e)
	{
	    e.printStackTrace();
	} 
	return fromCount;
    }

        
    /**
     * Agrega al catalogo de errores aquellos que sean de tipo Xerces
     * @param fromCount
     * @param errors
     * @return
     * Numero de errores generados
     */
    public static int generateXercesErrors(int fromCount, PGC07ErrorsCatalog errors)
    {
	
	Properties xercesErrors = new Properties();
	try
	{
	    xercesErrors.load(PGCUtils.class.getClassLoader().getResourceAsStream("org/apache/xerces/impl/msg/XMLSchemaMessages.properties"));
	    Enumeration<Object> enumKeys = xercesErrors.keys();
	    while(enumKeys.hasMoreElements())
	    {
		String currentKey = (String)enumKeys.nextElement();
		PGCError currentError = new PGCError();
		currentError.setErrorCode(fromCount++);
		currentError.setId(currentKey);
		currentError.setParameterList(getIdList(xercesErrors.getProperty(currentKey)));
		currentError.setType(PGCErrorTypeType.XERCES);
		errors.addPGCError(currentError);
	    }
	    
	} catch (Throwable e)
	{
	    e.printStackTrace();
	}
	
	return fromCount;
	
    }

    /**
     * Agrega al catalogo de errores aquellos que sean propios de la API
     * @param fromCount
     * @param errors
     * @return
     * Numero de errores generados
     */
    public static int generatePGCXBRLAPIErrors(int fromCount, PGC07ErrorsCatalog errors)
    {
	
	Properties apiErrors = new Properties();
	try
	{
	    apiErrors.load(PGCUtils.class.getClassLoader().getResourceAsStream("errorMessages.properties"));
	    Enumeration<Object> enumKeys = apiErrors.keys();
	    while(enumKeys.hasMoreElements())
	    {
		String currentKey = (String)enumKeys.nextElement();
		PGCError currentError = new PGCError();
		currentError.setErrorCode(fromCount++);
		currentError.setId(currentKey);
		currentError.setParameterList(getIdList(apiErrors.getProperty(currentKey)));
		currentError.setType(PGCErrorTypeType.XAPINTECO);
		errors.addPGCError(currentError);
	    }
	    
	} catch (Throwable e)
	{
	    e.printStackTrace();
	}
	
	return fromCount;
	
    }

    /*
    public static void main(String args[])
    {
	try
	{
	    PGC07ErrorsCatalog catalogErrors = new PGC07ErrorsCatalog();
	    int fromCount = generateXBReezeErrors("d:\\tmp\\localizer.xml", 0, catalogErrors);
	    fromCount = generateXercesErrors(fromCount, catalogErrors);
	    fromCount = generatePGCXBRLAPIErrors(fromCount, catalogErrors);
	    
	    FileWriter writer = new FileWriter(new File("d:\\tmp\\pgc07-errors.xml"));
	    catalogErrors.marshal(writer);
	    writer.close();
	    
	} catch (Throwable e)
	{
	    e.printStackTrace();
	}
	
    }*/
    
    
    
    
    
}
