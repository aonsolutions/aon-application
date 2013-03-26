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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.xerces.dom.DeferredElementNSImpl;
import org.apache.xerces.util.XMLCatalogResolver;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import es.inteco.xbrl.pgc.errors.GenericErrorsHandler;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLPGCException;
import es.inteco.xbrl.pgc.transform.format.Entity;
import es.inteco.xbrl.pgc.transform.format.Item;
import es.inteco.xbrl.pgc.transform.format.Module;
import es.inteco.xbrl.pgc.transform.format.Note;
import es.inteco.xbrl.pgc.transform.format.Record;
import es.inteco.xbrl.pgc.transform.format.Report;
import es.inteco.xbrl.pgc.transform.format.Row;
import es.inteco.xbrl.pgc.transform.format.Table;



/**
 *
 *
 * Clase con varias funcionalidades genericas de apoyo al codigo de la API
 *
 *
 *<br><br>
 * <b>Proyecto</b>: API XBRL-PGC2007 - Grupo de utilidades y librerías en código abierto para facilitar 
 *                             la integración del formato XBRL en las herramientas software de gestión de  terceros
 *                             aislándose de la complejidad en el procesamiento del modelo de datos de las taxonomías.
 *                             Ayudando de esta forma a las empresas finales en la labor de realización de informes XBRL
 *                             y asegurar el éxito de implantación del nuevo Plan General de Contabilidad 2007 en formato XBRL
 *
 *
 * @version 1.0, 14/01/2009
 * @author difusioncalidad@inteco.es
 *
 */


public abstract class PGCUtils
{

    private static final Logger logger = Logger.getLogger(PGCUtils.class);
    
    public static String getFullPath(String args)
    {
	try
	{
	    // find the path seperator used. mostly MS and MAC use
	    // "\" but UNIX use "/"
	    String strPathSeparator = System.getProperty("file.separator");

	    if (strPathSeparator.equals("\\")) // for MS
	    {
		// now replace any illegal '/' with '\\'
		args = args.replace('/', '\\');
	    } else if (strPathSeparator.equals("/")) // for everything else
	    {
		// now replace any illegal '\\' with '/'
		args = args.replace('\\', '/');
		args = new File(args).getCanonicalPath();
	    }

	    return args;
	} catch (Exception e)
	{
	    return null;
	}
    }

    
    
    /**
     * replace substrings within string
     * @param string
     * @param pattern
     * @param newPattern
     * @return
     * new string
     */
    public static String replace(String string, String pattern, String newPattern)
    {
	try
	{
	    StringBuffer stringBuffer = new StringBuffer(string);

	    int index = string.length();
	    int offset = pattern.length();

	    while ((index = string.lastIndexOf(pattern, index - 1)) > -1)
	    {
		stringBuffer.replace(index, index + offset, newPattern);
	    }

	    return stringBuffer.toString();
	} catch (StringIndexOutOfBoundsException e)
	{
	    return string;
	}
    }
    
    
    

    /**
     * This method formats the input string with the arguments passed. Note,
     * this method assumes that user passes the braces in pair, that is, it does
     * not check matching pair etc. Also, it does not throw exception in case
     * the number of arguments passed does not match with the braces, in that
     * case, it retunrs null
     * 
     * @param template
     *            The template to be filled in
     * 
     * @param arguments
     *            The arguments that fill the braces in the template strng
     * 
     * @return the formmatted string
     */
    public static String format(String template, String[] arguments)
    {
	int numberOfPairBraces = getNumberOfPairBraces(template);

	// this used to require that numberOfPairBraces exactly match argument
	// count,
	// but this is wrong; this means that we can't reuse parameters
	if (numberOfPairBraces < arguments.length)
	    return null;

	if (arguments != null)
	{
	    // likewise, we can't index up to numberOfPairBraces, but only up to
	    // argument coungt
	    for (int i = 0; i < arguments.length; ++i)
	    {
		// using regex is too messy here; too many special regex
		// characters to worry about
		// ...so since we don't really need or want regex replacements
		// anyway, just avoid
		// the whole issue
		String toBeReplaced = "{" + i + "}";
		template = replace(template, toBeReplaced, arguments[i]);
	    }
	}

	return template;
    }

    // This method returns number of pair braces in the string provided
    private static int getNumberOfPairBraces(String str)
    {
	int start = 0; // we will start from the begining of the string
	int numberOfPairBraces = 0; // number of braces found in the input
				    // string
	int at = -1; // this will have the position where a match is found
	while (start < str.length())
	{
	    at = str.indexOf("{", start);
	    if (at == -1)
		break;
	    ++numberOfPairBraces;
	    start = at + 1;
	}

	return numberOfPairBraces;

    }

    /**
     * Escribe un schemaRef valido en el documento
     * @param inputStreamDocument
     * @param taxonomyRoot
     * @param normalizedSchemaRef
     * @param ouputStreamXML
     * @throws XBRLPGCException
     */
    public static void normalizeSchema(InputStream inputStreamDocument, String taxonomyRoot, String normalizedSchemaRef, ByteArrayOutputStream ouputStreamXML) throws XBRLPGCException
    {
	try
	{
	    String logicalUri = null;
	    
	    System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

	    javax.xml.parsers.DocumentBuilderFactory dfactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
	    dfactory.setNamespaceAware(true);
	    dfactory.setIgnoringElementContentWhitespace(true);
	    Document doc = dfactory.newDocumentBuilder().parse(inputStreamDocument);

	    DeferredElementNSImpl schemaRefNode = (DeferredElementNSImpl) doc.getDocumentElement().getElementsByTagNameNS("http://www.xbrl.org/2003/linkbase",
		    "schemaRef").item(0);
	    if (schemaRefNode != null)
	    {
		logicalUri = schemaRefNode.getAttributeNS("http://www.w3.org/1999/xlink", "href");
	    }
	    if ((logicalUri == null) || (logicalUri.trim().equals("")))
	    {
		throw new XBRLPGCException("notFoundSchemaRefInTheInstance", new String[] {});
	    }

	    doc.getDocumentElement().removeAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");

	    schemaRefNode.setAttributeNS("http://www.w3.org/1999/xlink", "href", normalizedSchemaRef);

	    saveXML(doc, ouputStreamXML);

	} catch (XBRLPGCException ex)
	{
	    throw ex;

	} catch (Exception e)
	{
	    throw new XBRLPGCException("errorNormalizeSchema", new String[] { }, e);
	}
    }
    
    
    /**
     * Escribe un schemaRef valido en el documento
     * @param inputStreamDocument
     * @param taxonomyRoot
     * @param ouputStreamXML
     * @throws XBRLPGCException
     */
    public static void normalizeSchema(InputStream inputStreamDocument, String taxonomyRoot, ByteArrayOutputStream ouputStreamXML) throws XBRLPGCException
    {
	try
	{
	    String physicalUri = null;
	    String logicalUri = null;
	    
	    System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

	    javax.xml.parsers.DocumentBuilderFactory dfactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
	    dfactory.setNamespaceAware(true);
	    dfactory.setIgnoringElementContentWhitespace(true);
	    Document doc = dfactory.newDocumentBuilder().parse(inputStreamDocument);

	    DeferredElementNSImpl schemaRefNode = (DeferredElementNSImpl) doc.getDocumentElement().getElementsByTagNameNS("http://www.xbrl.org/2003/linkbase",
		    "schemaRef").item(0);
	    if (schemaRefNode != null)
	    {
		logicalUri = schemaRefNode.getAttributeNS("http://www.w3.org/1999/xlink", "href");
	    }

	    if ((logicalUri == null) || (logicalUri.trim().equals("")))
	    {
		throw new XBRLPGCException("notFoundSchemaRefInTheInstance", new String[] {});
	    }
	    
	    if (logicalUri.startsWith("http"))
	    {
		XMLCatalogResolver catalog = XbrlApiConfiguration.getInstance().getResolver();
		String resolvedUri = catalog.resolveSystem(logicalUri);
		if ((resolvedUri == null) || (resolvedUri.startsWith("http")) )
		{
		    throw new XBRLPGCException(XBRLPGCException.canNotResolveSchemaRef, new String[] {logicalUri });
		}
		physicalUri = new File(new java.net.URL(resolvedUri).getPath()).getName();

		physicalUri = taxonomyRoot + physicalUri;

		doc.getDocumentElement().removeAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");

		schemaRefNode.setAttributeNS("http://www.w3.org/1999/xlink", "href", physicalUri);
	    }
	    saveXML(doc, ouputStreamXML);

	} catch (XBRLPGCException ex)
	{
	    throw ex;

	} catch (Exception e)
	{
	    throw new XBRLPGCException("errorNormalizeSchema", new String[] { }, e);
	}
    }
    
    
    /**
     * Escribe un schemaRef valido en el documento
     * @param inputStreamDocument
     * @param taxonomyRoot
     * @param outputPath
     * @throws XBRLPGCException
     */
    public static void normalizeSchema(InputStream inputStreamDocument, String taxonomyRoot, String outputPath) throws XBRLPGCException
    {
	try
	{
	    ByteArrayOutputStream inputStream = new ByteArrayOutputStream();
	    PGCUtils.normalizeSchema(inputStreamDocument, XbrlApiConfiguration.getInstance().getTaxonomyRoot(), inputStream);
	    FileOutputStream fos = new FileOutputStream(outputPath);
	    fos.write(inputStream.toByteArray());
	    fos.flush();
	    fos.close();
	}
	catch (XBRLPGCException ex)
	{
	    throw ex;
	} 
	catch (Exception e)
	{
	    throw new XBRLPGCException("errorNormalizeSchema", new String[] { }, e);
	}

    }

    /**
     * Serializa un XML a ByteArrayOutputStream
     * @param doc
     * @param outputStreamXML
     * @throws Exception
     */
    public static void saveXML(Document doc, ByteArrayOutputStream outputStreamXML) throws Exception
    {

	System.setProperty(DOMImplementationRegistry.PROPERTY, "org.apache.xerces.dom.DOMImplementationSourceImpl");
	DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
	DOMImplementation domImpl = registry.getDOMImplementation("LS 3.0");
	DOMImplementationLS implLS = (DOMImplementationLS) domImpl;
	LSSerializer dom3Writer = implLS.createLSSerializer();

	LSOutput output = implLS.createLSOutput();

	output.setByteStream(outputStreamXML);
	output.setEncoding("UTF-8");
	dom3Writer.write(doc, output);

    }
    
    /**
     * Lee un fichero y genera el correspondiente ByteArrayOutputStream
     * @param inputFile
     * @param outputStream
     * @throws XBRLPGCException
     */
    public static void saveFileToByteArray(File inputFile, ByteArrayOutputStream outputStream) throws XBRLPGCException
    {
	FileInputStream inputStreamFile = null;
	try
	{
	    inputStreamFile = new FileInputStream(inputFile);
	    while (inputStreamFile.available()>0)
	    {
		outputStream.write(inputStreamFile.read());
		
	    }
	    
	} catch (Exception e)
	{
	    throw new XBRLPGCException("errorSavingFileToByteArray", new String[] { }, e);
	}
	finally{
	    if (inputStreamFile != null)
	    {
		try
		{
		    inputStreamFile.close();
		} catch (IOException e)
		{
		}
	    }
	}
    }
    
    /**
     * Busca las trazas de error asociadas a un object
     * @param objToTrace
     * @return
     * trazas en String
     */
    public static String getTraceObject(Object objToTrace)
    {
	String traceResult = "null";
	
	if (objToTrace != null)
	{

	    if (objToTrace instanceof Item)
	    {
		Item item = (Item)objToTrace;
		traceResult = "id=" + item.getId() + " " + "value=" + item.getValue() + " " +  "decimals=" + item.getDecimals() + " reportingDate=" + item.getReportingDate(); 
	    }
	    else if (objToTrace instanceof Entity)
	    {
		Entity entity = (Entity)objToTrace;
		traceResult = "id=" + entity.getId() + " " + "uri=" +  entity.getUri();
	    }
	    else if (objToTrace instanceof Module)
	    {
		Module module = (Module)objToTrace;
		traceResult = "id=" + module.getId() + " " + "baseDecimals=" + module.getBaseDecimals() + " "+ "baseUnit=" + module.getBaseUnit() 
				+ " startDate=" + module.getReportingDateStart().toString() + " endDate=" + module.getReportingDateEnd().toString();
	    }
	    else if (objToTrace instanceof Note)
	    {
		Note note = (Note)objToTrace;
		traceResult = "id=" + note.getId() + " " + "text=" + note.getText();
	    }
	    else if (objToTrace instanceof Record)
	    {
		Record record = (Record)objToTrace;
		traceResult = "id=" + record.getId() ;
	    }
	    else if (objToTrace instanceof Row)
	    {
		Row row = (Row)objToTrace;
		traceResult = "id=" + row.getId() + " " + "index=" + row.getIndex();
	    }
	    else if (objToTrace instanceof Report)
	    {
		Report report = (Report)objToTrace;
		traceResult = "id=" +  report.getId() + " " +  "date=" + report.getDate();
	    }
	    else if (objToTrace instanceof Table)
	    {
		Table table = (Table)objToTrace;
		traceResult = "id=" + table.getId();
	    }
	    else
	    {
		System.out.println("Not exist trace for " + objToTrace);
		traceResult = "null";
	    }
	}
	return "[" + traceResult + "]";
	
    }
    

    
    
    /**
     * Obtiene el valor de una property a través de su name
     * @param getMethodName
     * @param objTarget
     * @return
     * valor de la property
     */
    public static Object getPropertyValueByName(String getMethodName, Object objTarget)
    {
	
	Object value = null;
	try
	{
	    Method method = objTarget.getClass().getDeclaredMethod(getMethodName, new Class[]{});
	    value = method.invoke(objTarget, new Object[]{});
	    
	    
	} catch (SecurityException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (NoSuchMethodException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalArgumentException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalAccessException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InvocationTargetException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return value;
	
	
    }
    
    
    /**
     * Obtiene la fecha actual
     * @return
     * fecha actual
     */
    public static final  java.util.Date getCurrentDate()
    {
	java.util.Date now = new java.util.Date();

	DateFormat ISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	TimeZone timeZone = TimeZone.getDefault();
	ISO8601Local.setTimeZone(timeZone);

	return now;
    }
    
    
    /**
     * Obtiene el schemaRef de una instancia
     * @param inputStreamDocument
     * @return
     * schemaRef
     * @throws XBRLPGCException
     */
    public static final String getSchemaRef(InputStream inputStreamDocument)throws XBRLPGCException
    {
	
	Document doc;
	String schemaRefUri = null;
	
	try
	{
	    System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

	    javax.xml.parsers.DocumentBuilderFactory dfactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
	    dfactory.setNamespaceAware(true);
	    dfactory.setIgnoringElementContentWhitespace(true);
	    doc = dfactory.newDocumentBuilder().parse(inputStreamDocument);

	    DeferredElementNSImpl schemaRefNode = (DeferredElementNSImpl) doc.getDocumentElement().getElementsByTagNameNS("http://www.xbrl.org/2003/linkbase",
	    "schemaRef").item(0);

	    String logicalUri = null;

	    if (schemaRefNode != null)
	    {
		logicalUri = schemaRefNode.getAttributeNS("http://www.w3.org/1999/xlink", "href");
	    }
	    
	    if (logicalUri.startsWith("http:"))
	    {
		schemaRefUri = new File(new URL(logicalUri).getPath()).getName();
	    }
	    else
	    {
		File f = new File(logicalUri);
		schemaRefUri = f.getName();
	    }
	    
	} 
	catch (Throwable e)
	{
		//throw new XBRLPGCException(XBRLPGCException.errorGettingSchema, e);
		throw new XBRLPGCException(XBRLPGCException.errorGettingSchema, new String[] { }, e);
	    //throw new XBRLPGCException("errorGettingSchema", new String[] { }, e);
	}	
	

	
	return schemaRefUri;
    }
    
    
    /**
     * Obtiene el identificador de report de una instancia
     * @param documentPath
     * @return
     * identificador del report
     */
    public static final String getReportID(String documentPath)
    {
	
	Document doc = null;
	String reportID = null;
	
	try
	{
	    System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

	    javax.xml.parsers.DocumentBuilderFactory dfactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
	    dfactory.setNamespaceAware(true);
	    dfactory.setIgnoringElementContentWhitespace(true);
	    doc = dfactory.newDocumentBuilder().parse(new FileInputStream(documentPath));

	    reportID = doc.getDocumentElement().getAttribute("id");


	    
	} catch (Throwable e)
	{
	    logger.error(e);
	}

	return reportID;
    }

    /**
     * Da un formato correcto a un error de validación de schema
     * @param message
     * @param errorHandler
     * @throws Exception
     */
    public static void processSchemaValidationError(String message, GenericErrorsHandler errorHandler) throws Exception
    {
	ArrayList<String> params = new ArrayList<String>();
	//get the error message
	String errorMessage = message;
	//split error message using :, error messages will be of the format
	//errorcode:errormessage
	int spiltpoint = errorMessage.indexOf(':');
	String errorcode = "notFoundCode";

	if (spiltpoint > 0)
	{
	    errorcode = errorMessage.substring(0, spiltpoint);		
	    String contentParams = errorMessage.substring(spiltpoint);

	    Pattern p = Pattern.compile("('[^']+')");
	    Matcher m = p.matcher(contentParams);
	    while(m.find()) 
	    {
		String foundParam = contentParams.substring(m.start(), m.end());
		params.add(foundParam);
	    }
	}
	errorHandler.addError(errorcode, message, params);
    }

    /**
     * Crea un fichero temporal
     * @param prefix
     * @param extension
     * @return
     * fichero creado
     * @throws XBRLPGCException
     */
    public static final File createTempFile(String prefix, String extension) throws XBRLPGCException
    {
        File instanceFile = null;
        try
        {
            String tempDirectory = XbrlApiConfiguration.getInstance().getTempDirectory();
            File tempDirectoryFile = new File(tempDirectory);
            if (!tempDirectoryFile.exists())
            {
        	throw new XBRLPGCException(XBRLPGCException.notExistTempDirectory,new String[]{tempDirectory});
            }
            instanceFile = File.createTempFile(prefix, extension, tempDirectoryFile);
    
        } catch (IOException e)
        {
            throw new XBRLPGCException("Error creating temporal file",e);
        }
        return instanceFile;
    }

    /**
     * Crea un fichero temporal
     * @param inputDocument
     * @param prefix
     * @param extension
     * @return
     * fichero creado
     * @throws XBRLPGCException
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static final File createTempFile(byte[] inputDocument, String prefix, String extension) throws XBRLPGCException, IOException, FileNotFoundException
    {
	File inputDocumentFile;
	try
	{

	    String tempDirectory = XbrlApiConfiguration.getInstance().getTempDirectory();
	    File tempDirectoryFile = new File(tempDirectory);
	    if (!tempDirectoryFile.exists())
	    {
		throw new XBRLPGCException(XBRLPGCException.notExistTempDirectory, new String[] { tempDirectory });
	    }
	    inputDocumentFile = File.createTempFile(prefix, extension, tempDirectoryFile);
	    FileOutputStream fos = new FileOutputStream(inputDocumentFile);
	    fos.write(inputDocument);
	    fos.flush();
	    fos.close();

	} catch (IOException e)
	{
	    throw new XBRLPGCException("Error creating temporal file",e);
	}
	return inputDocumentFile;
    }    
    
    
}
