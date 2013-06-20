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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import org.apache.log4j.Logger;

import es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade;
import es.inteco.xbrl.pgc.apifacade.impl.XbrlApiFactory;
import es.inteco.xbrl.pgc.errors.GenericErrorsHandler;
import es.inteco.xbrl.pgc.errors.exceptions.DocumentNotFoundException;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLPGCException;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLValidatorException;
import es.inteco.xbrl.pgc.utils.PGCUtils;
import es.inteco.xbrl.pgc.utils.XbrlApiConfiguration;




/**
 *
 *
 * Clase que gestiona la validación de un documento XBRL contra la taxonomía.
 * 
 * Esta clase se compone de dos métodos públicos mediante los cuales es posible
 * invocar la funcionalidad de validación XBRL, devolviendo en ambos casos un 
 * objeto que encapsula el resultado de la validación, así como un informe de
 * errores de validación que hayan podido encontrarse.
 * 
 * La validación XBRL está basada en la API de XBreeze, a la cual se accede a través
 * de la funcionalidad que publica la facade implementada para dicha API.
 * 
 * El resultado de la validación consiste en indicar si la instancia es correcta, para
 * ello se utilizará un método booleano que así lo indica.
 * 
 * Si durante la validación se detectaron errores, dichos errores serán
 * informados según el formato definido para ello mediante el esquema de errores
 * config/schemas/pgc07-errors-output.xsd.
 * 
 * En dichos errores se informa del código, la descripción por defecto y los parámetros
 * en caso de que los hubiera.
 * 
 * Los errores que se pueden devolver están clasificados en tres tipos, de validación
 * de esquema, validación Xbreeze (XBRL) o errores de la propia API (error de sistema
 * genérico).
 * 
 * Todos los errores quedan catalogados en el fichero pgc07.errors.xml.
 *
 *
 *<br><br>
 * <b>Proyecto</b>: API XBRL-PGC2007 - Grupo de utilidades y librerías en código abierto para facilitar 
 *                             la integración del formato XBRL en las herramientas software de gestión de  terceros
 *                             aislándose de la complejidad en el procesamiento del modelo de datos de las taxonomías.
 *                             Ayudando de esta forma a las empresas finales en la labor de realización de informes XBRL
 *                             y asegurar el éxito de implantación del nuevo Plan General de Contabilidad 2007 en formato XBRL
 *
 * @version 1.1, 18/02/2009
 * @author difusioncalidad@inteco.es
 *
 */


public class XBRLValidator implements IXBRLValidator
{
    private static final Logger logger = Logger.getLogger(XBRLValidator.class);
    
    /**
     * Constructor
     */
    protected XBRLValidator(){}
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.validator.IXBRLValidator#validate(java.lang.String)
     */
    public ValidateResult validate(String documentPath) throws XBRLPGCException
    {
	ValidateResult validateResult = null;
	
	try
	{
	    
	    logger.info("Processing the request XBRL validation of " + documentPath);

	    File documentFile = new File(documentPath);
	    if (!documentFile.exists())
	    {
		throw new DocumentNotFoundException(DocumentNotFoundException.notFoundInputDocumentToValidate,new String[]{documentPath});
	    }

	    ByteArrayOutputStream documentByteArray = new ByteArrayOutputStream();
	    PGCUtils.saveFileToByteArray(documentFile, documentByteArray);

	    validateResult = validate(documentByteArray.toByteArray());


	} catch (XBRLPGCException ex)
	{
	    throw ex;
	} catch (Exception e)
	{
	    logger.error(e.getMessage());
	    throw new XBRLValidatorException(XBRLPGCException.genericErrorValidatingInstance, new String[]{documentPath},e);
	}
	
	return validateResult;
    }
    
    
    /*Devuelve un manejador de errores*/
    protected GenericErrorsHandler getErrorsHandler()
    {
	return new GenericErrorsHandler();
	
    }
    
    
    
    
    
    public ValidateResult validate(byte[] inputDocument) throws XBRLPGCException
    {
    	return validate(inputDocument, true);
    }
    
    
    
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.validator.IXBRLValidator#validate(byte[])
     */
    public ValidateResult validate(byte[] inputDocument, boolean postProcessResult) throws XBRLPGCException
    {

	ValidateResult validateResult = new ValidateResult();
	File inputDocumentFile = null;
	String documentPath = null;
	try 
	{
    
	    inputDocumentFile = PGCUtils.createTempFile(inputDocument,"xbrlvalidator", ".xbrl");
	        
	    PGCUtils.normalizeSchema(new ByteArrayInputStream(inputDocument), XbrlApiConfiguration.getInstance().getTaxonomyRoot(), inputDocumentFile.getAbsolutePath());

	    documentPath = inputDocumentFile.getAbsolutePath();

	    logger.info("XBRL Validation of " + documentPath + " started");
	    
	    IXbrlApiFacade apiFacade;
	    apiFacade = XbrlApiFactory.get_instance();

	    GenericErrorsHandler errorsHandler = getErrorsHandler();
	    
	    boolean valid = apiFacade.validate(documentPath, errorsHandler);
	    
	    //validateResult= new ValidateResult(errorsHandler.toString(),valid);
	    if (postProcessResult){
	    	validateResult= new ValidateResult(errorsHandler.toStringPostProcess(),valid);
	    }
	    else{
	    	validateResult= new ValidateResult(errorsHandler.toString(),valid);
	    }
	    
	    
	    logger.info("XBRL Validation of " + documentPath + " ended successfully");
	    
	}
	catch(XBRLPGCException ex)
	{
		ex.printStackTrace();
		try{
			logger.error("XBRL Validation of " + inputDocumentFile.getAbsolutePath() + " was not completed");
		}
		catch(Throwable e2){}
	    throw ex;
	}
	catch(Exception e){
		e.printStackTrace();
		logger.error("XBRL Validation of " + inputDocumentFile.getAbsolutePath() + " was not completed");
	    logger.error(e.getMessage());
	    throw new XBRLValidatorException(XBRLPGCException.genericErrorValidatingInstance, new String[]{documentPath},e);
	}
        finally
        {
            if ((inputDocumentFile!=null) && inputDocumentFile.exists())
            {
        	inputDocumentFile.delete();
            }
        }

	//logger.info("XBRL Validation of " + documentPath + " finished");

	return validateResult;
    }
    
    
    
    
    
    
    
    /**
     * Crea una instancia de la propia clase
     * @return
     * Objeto del tipo IXBRLValidator
     */
    public static IXBRLValidator createInstance(){
	return new XBRLValidator();
    }
    
    /**
     * Punto de entrada de ejecución
     * @param args
     */
    public static void main(String args[])
    {
    	String documentPath = null;
    	byte[] inputDocument = null;
    	
    	if(args.length == 0 || (args.length != 1 && args.length != 2)){
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
        				case 'i':
        					if (i+1 < args.length) 
        						inputDocument = args[++i].getBytes();
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
   	
    	IXBRLValidator validator = XBRLValidator.createInstance();
    	ValidateResult validateResult = null;
	
	try
	{
			if(documentPath != null && inputDocument == null){
				validateResult = validator.validate(documentPath);
			}
			else if(inputDocument != null && documentPath == null){
				validateResult = validator.validate(inputDocument);
			}
			else
				errorParameters();
			printResults(validateResult);
	} catch (Throwable e)
	{
	    logger.error(e);
	}
    }
    
    
    /*Muestra el contenido del ValidateResult*/
    static private void printResults(ValidateResult validateResult)
    {
    	String errors = null;
    	if(validateResult == null)
    		errorParameters();
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
	    System.out.println("Uso: xbrlvalidator [-d documentPath]");
	    System.out.println("					Realiza validación XBRL del documento [documentPath].");
	    System.out.println();    	
	    System.out.println("Uso: xbrlvalidator [-i inputPath]");
	    System.out.println("					Realiza validación XBRL del array de bytes [inputPath].");
	    System.out.println();    	
	    System.out.println("Uso: xbrlvalidator -h");
	    System.out.println("     				Muestra esta ayuda");
	    System.out.println();
    }

}
