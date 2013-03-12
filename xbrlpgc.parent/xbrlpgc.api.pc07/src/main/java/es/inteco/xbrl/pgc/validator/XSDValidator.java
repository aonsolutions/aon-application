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


package es.inteco.xbrl.pgc.validator;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import es.inteco.xbrl.pgc.errors.exceptions.DocumentNotFoundException;
import es.inteco.xbrl.pgc.errors.exceptions.XSDValidatorException;
import es.inteco.xbrl.pgc.utils.XbrlApiConfiguration;



/**
 *
 *
 * Clase que gestiona la validación XSD de documentos XML.
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


public class XSDValidator implements IXSDValidator {
    
    private static final String cParserXerces = "org.apache.xerces.parsers.SAXParser";
    private static final String cNamespaces = "http://xml.org/sax/features/namespaces";
    private static final String cNamespacePrefix = "http://xml.org/sax/features/namespace-prefixes";
    private static final String cValidation = "http://xml.org/sax/features/validation";
    private static final String cValidationSchema = "http://apache.org/xml/features/validation/schema";
    private static final String cFullCheckSchema = "http://apache.org/xml/features/validation/schema-full-checking";
    private static final String cDinamicValidation = "http://apache.org/xml/features/validation/dynamic";
    private static final String cContinuaFaltalError = "http://apache.org/xml/features/continue-after-fatal-error";
    private static final String cSchemaLenguage = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema";
    
    static Logger logger = Logger.getLogger(XBRLValidator.class);
    
    
    /**
     * Constructor
     */
    private XSDValidator() {}
    
    
    /**
     * Valida el documento XML
     * 
     * @param documentFile
     * Documento XML que se quiere validar
     * @param eh
     * ErrorHandler. Manejador de errores
     * @param ch
     * ContentHandler. Manejador de contenido
     * 
     * @throws Exception
     */
    private  void validate(File documentFile, ErrorHandler eh, ContentHandler ch) throws Exception
    {
	
	XMLReader xreader = XMLReaderFactory.createXMLReader(cParserXerces);
	
	xreader.setFeature(cValidation, true);
	xreader.setFeature(cValidationSchema, true);
	xreader.setFeature(cNamespaces, true);
	xreader.setFeature(cNamespacePrefix, true);
	xreader.setFeature(cFullCheckSchema, true);
	xreader.setFeature(cContinuaFaltalError, true);
	xreader.setFeature(cDinamicValidation, false);
	xreader.setProperty(cSchemaLenguage, W3C_XML_SCHEMA_NS_URI);

	
	xreader.setEntityResolver(XbrlApiConfiguration.getInstance().getResolver());

	if (ch != null)
	    xreader.setContentHandler(ch);

	if (eh != null)
	    xreader.setErrorHandler(eh);

	xreader.parse(new InputSource(new java.io.InputStreamReader(new FileInputStream(documentFile),"UTF-8")));

    }
    

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.validator.IXSDValidator#validate(java.lang.String)
     */
    public ValidateResult validate(String documentPath) throws DocumentNotFoundException, XSDValidatorException
    {
	
	ErrorHandler errorHandler = new ErrorHandler();
	File documentFile = new File(documentPath);
	
	if (!documentFile.exists())
	{
	    throw new DocumentNotFoundException(DocumentNotFoundException.notFoundInputDocumentToValidate, new String[]{documentPath});
	}
	
	try
	{
	    validate(documentFile, errorHandler, errorHandler);
	} catch (Exception e)
	{
	    throw new XSDValidatorException(e.getMessage(),e);
	}

	return new ValidateResult(errorHandler.getErrors(), errorHandler.isValid());
    }
    
    
    
    /**
     * Crea una instancia de la propia clase
     * @return
     * Instancia de la propia clase XSDValidator
     */
    public static IXSDValidator createInstance(){
	
	return new XSDValidator();
    }
    
    /**
     * Punto de entrada de ejecución
     * @param args
     */
    public static void main(String args[])
    {
    	String documentPath = null;
    	
    	if(args.length == 0){
    		errorParameters();
    	}else{
    		for (int i=0; i<args.length; i++) {
        		
        		if (args[i].charAt(0) == '-') {
        			switch (args[i].charAt(1)) {
        				case 'h': use(); System.exit(0);
        				case 'd':
        					if (i+1 < args.length) 
        						documentPath = args[++i];
        					else
        						errorParameters();
        					break;
        				default:
        					errorParameters();
        			}
        		}
        		else
        			errorParameters();
       		}
    	}
    	
    	IXSDValidator validator = XSDValidator.createInstance();
    	ValidateResult validateResult = null;
	
	try
	{
			if(documentPath != null)
			{
				validateResult = validator.validate(documentPath);
			}
			else
			{
				errorParameters();
			}
			printResults(validateResult);
	} 
	catch (Throwable e)
	{
	    logger.error(e);
	}
    }
    
    /*Muestra por la salida estandar el contenido del ValidateResult*/
    static private void printResults(ValidateResult validateResult)
    {
    	String errors = null;
    	if(validateResult == null)
    	{
    		errorParameters();
    	}
    	else if(!validateResult.isValid())
    	{
    		errors = validateResult.getErrors();
    		logger.error("Se encontraron errores en la validación:");
  		logger.error(errors);
    	}
    	else if(validateResult.isValid())
    	{
    		logger.info("Validación correcta");
    	}
    }
    
    /*Metodo que gestiona los mensajes al usuario cuando existe error en los parámetros*/
    static private void errorParameters()
    {
    	logger.error("Error en los parámetros");
    	use();
    	System.exit(0);
    }
    
    /*Metodo que presenta por salida estandar los errores al usuario cuando hay error en los parámetros*/
    static private void use()
    {
    	System.out.println(); 
    	System.out.println("API XBRL PGC 2007");
	    System.out.println();    	
	    System.out.println("Uso: xsdvalidator -d documentPath");
	    System.out.println("					Realiza validación XSD del documento [documentPath].");
	    System.out.println();    	
	    System.out.println("Uso: xsdvalidator -h");
	    System.out.println("     				Muestra esta ayuda");
	    System.out.println();
    }

}
