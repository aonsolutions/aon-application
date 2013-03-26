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


package es.inteco.xbrl.pgc.transform;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import es.inteco.xbrl.pgc.errors.exceptions.DocumentNotFoundException;
import es.inteco.xbrl.pgc.errors.exceptions.TransformException;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLPGCException;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLValidatorException;
import es.inteco.xbrl.pgc.errors.exceptions.XSDValidatorException;
import es.inteco.xbrl.pgc.utils.PGCUtils;
import es.inteco.xbrl.pgc.utils.XbrlApiConfiguration;
import es.inteco.xbrl.pgc.validator.PGCXBRLValidator;
import es.inteco.xbrl.pgc.validator.ValidateResult;
import es.inteco.xbrl.pgc.validator.XSDValidator;



/**
 *
 *
 * Esta clase implementa un transformador PGC.  Este transformador cumple con los requerimientos
 * definidos y documentados por el interfaz IPGCTransformer.
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
 * @see es.inteco.xbrl.pgc.transform.IPGCTransformer
 *
 */


public class PGCTransformer implements IPGCTransformer
{

    static Logger logger = Logger.getLogger(PGCTransformer.class);
    
    
    
    private ArrayList<String> loggerTraceList=null;
    
    
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.transform.IPGCTransformer#getLoggerList()
     */
    public ArrayList<String> getLoggerTraceList() {
		return loggerTraceList;
	}
	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.transform.IPGCTransformer#setLoggerList(java.util.ArrayList)
	 */
	public void setLoggerTraceList(ArrayList<String> loggerList) {
		this.loggerTraceList = loggerList;
	}
    
    
    /**
     * Constructor
     */
    private PGCTransformer() {
	
    }
    
    
    /**
     * Valida un XSD
     * @param documentPath
     * @throws XSDValidatorException
     * @throws DocumentNotFoundException
     */
    private void validateXSD(String documentPath) throws XSDValidatorException, DocumentNotFoundException
    {
	ValidateResult validateResult = XSDValidator.createInstance().validate(documentPath);
	if (!validateResult.isValid())
	{
	    throw new XSDValidatorException(validateResult.getErrors(), new String[]{});
	}
	if(logger.isInfoEnabled())
	{
	    logger.info("Successfully validated document at " + documentPath);
	}
    }
    
    
    /**
     * Valida un XBRL
     * @param documentPath
     * @throws XBRLPGCException
     */
    private void validateXBRL(String documentPath) throws XBRLPGCException{
	
	ValidateResult validateResult = PGCXBRLValidator.createInstance().validate(documentPath);
	if (!validateResult.isValid())
	{
	    throw new XBRLValidatorException(validateResult.getErrors(), new String[]{});
	}
	if(logger.isInfoEnabled())
	{
	    logger.info("Successfully validated document at " + documentPath);
	}
    }
    

    /**
     * Valida un documento XBRL
     * @param inputDocument
     * array de bytes que forman el XBRL origen de la transformación
     * @throws XBRLPGCException
     */
    public void validateXBRL(byte[] inputDocument) throws XBRLPGCException{
	
	ValidateResult validateResult = PGCXBRLValidator.createInstance().validate(inputDocument);
	if (!validateResult.isValid())
	{
	     throw new XBRLValidatorException(validateResult.getErrors(), new String[]{});
	}
	if(logger.isInfoEnabled())
	{
	    logger.info("Successfully validated document");
	}
    }
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.transform.IPGCTransformer#transformToXbrl(java.lang.String, java.lang.String)
     */
    public void transformToXbrl(String inputPath, String outputPath) throws XBRLPGCException
    {
	transformToXbrl(inputPath,outputPath, true);
    }

        

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.transform.IPGCTransformer#transformToXbrl(java.lang.String, java.lang.String, boolean)
     */
    public void transformToXbrl(String inputPath, String outputPath, boolean validateXbrl) throws XBRLPGCException
    {
	validateXSD(inputPath);
	
	if (logger.isInfoEnabled())
	    logger.info("Init transformation to XBRL inputPath=" + inputPath + " outputPath=" + outputPath );
	
	TransformerToXBRL transformerToXBRL =  TransformerToXBRL.createInstance();
	
	transformerToXBRL.setLoggerTraceList(this.loggerTraceList);
	
	transformerToXBRL.transform(inputPath, outputPath);
	
	if (logger.isInfoEnabled())
	    logger.info("Finish transformation to XBRL inputPath=" + inputPath + " outputPath=" + outputPath );
	
	if (validateXbrl)
	{
	    validateXBRL(outputPath);
	}
	
    }
    
    
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.transform.IPGCTransformer#transformToXbrl(java.lang.String, boolean)
     */
    public byte[] transformToXbrl(String inputPath, boolean validate) throws XBRLPGCException
    {
	
	validateXSD(inputPath);

	if (logger.isInfoEnabled())
	    logger.info("Init transformation to XBRL inputPath=" + inputPath);
	
	TransformerToXBRL transformerToXBRL =  TransformerToXBRL.createInstance();
	
	transformerToXBRL.setLoggerTraceList(this.loggerTraceList);
	
	byte[] result = transformerToXBRL.transform(inputPath, validate); 
	
	
	if (logger.isInfoEnabled())
	    logger.info("Finish transformation to XBRL inputPath=" + inputPath);
	
	if (validate)
	{
	    validateXBRL(result);
	}
	
	return result; 

    }
   
    
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.transform.IPGCTransformer#transformToXbrl(byte[], boolean)
     */
    public byte[] transformToXbrl(byte[] inputDocument, boolean validateXbrl) throws XBRLPGCException
    {

	byte[] brlResult = null;
	File inputDocumentFile = null;
	String inputPath = null;

	try
	{
	    inputDocumentFile = PGCUtils.createTempFile(inputDocument,"transformtoxml", ".xml");
	    
	    inputPath = inputDocumentFile.getAbsolutePath();

	    validateXSD(inputPath);
	    
	    if (logger.isInfoEnabled())
		logger.info("Init transformation to XBRL inputPath=" + inputPath);

	    TransformerToXBRL transformerToXBRL =  TransformerToXBRL.createInstance();
	
		transformerToXBRL.setLoggerTraceList(this.loggerTraceList);

	    brlResult = transformerToXBRL.transform(inputPath,validateXbrl);

	    if (logger.isInfoEnabled())
		logger.info("Finish transformation to XBRL inputPath=" + inputPath);


	    if (validateXbrl)
	    {
		validateXBRL(brlResult);
	    }
	}catch (XBRLPGCException exp)
	{
	    throw exp;
	}
	catch (Exception e)
	{
	    throw new TransformException(TransformException.errorInTransformationToXbrl, new String[]{inputPath},e);
	} finally
	{
	    if ((inputDocumentFile != null) && inputDocumentFile.exists())
	    {
		inputDocumentFile.delete();
	    }
	}

	return brlResult;

    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.transform.IPGCTransformer#transformToXml(java.lang.String, java.lang.String, boolean)
     */
    public void transformToXml(String inputPath, String outputPath, boolean validateXbrl) throws XBRLPGCException
    {
	FileOutputStream fileOutput = null;
	try
	{
	    ByteArrayOutputStream documentByteArray = new ByteArrayOutputStream();
	    PGCUtils.saveFileToByteArray(new File(inputPath), documentByteArray);
	    fileOutput = new FileOutputStream(outputPath);
	    fileOutput.write(transformToXml(documentByteArray.toByteArray(), validateXbrl));
	    fileOutput.close();

	}catch (XBRLPGCException exp)
	{
	    throw exp;
	}
	catch (Exception e)
	{
	    throw new TransformException("Error transform to xml inputPath " + inputPath + " " + e.getMessage(), e);
	}
    }

    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.transform.IPGCTransformer#transformToXml(java.lang.String, boolean)
     */
    public byte[] transformToXml(String inputPath, boolean validateXbrl) throws XBRLPGCException
    {
	ByteArrayOutputStream documentByteArray = new ByteArrayOutputStream();
	PGCUtils.saveFileToByteArray(new File(inputPath), documentByteArray);
	return transformToXml(documentByteArray.toByteArray(), validateXbrl);
    }

    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.transform.IPGCTransformer#transformToXml(byte[], boolean)
     */
    public byte[] transformToXml(byte[] inputDocument, boolean validateXbrl) throws XBRLPGCException
    {
	
	    byte[] xmlResult = null;
	    File inputDocumentFile = null;
	    try
	    {
		if (validateXbrl)
		{
		    validateXBRL(inputDocument);
		}

		inputDocumentFile = PGCUtils.createTempFile(inputDocument, "transformtoxml",  ".xbrl");
		PGCUtils.normalizeSchema(new ByteArrayInputStream(inputDocument), XbrlApiConfiguration.getInstance().getTaxonomyRoot(), inputDocumentFile.getAbsolutePath());
		String inputPath = inputDocumentFile.getAbsolutePath();
		    
		if (logger.isInfoEnabled())
		    logger.info("Init transformation to XML inputPath=" + inputPath);
		
		TransformerToXML transformerToXML =  TransformerToXML.createInstance();
		
		
		transformerToXML.setLoggerTraceList(this.loggerTraceList);
		
		xmlResult = transformerToXML.transform(inputPath);
		
	
		if (logger.isInfoEnabled())
		    logger.info("Finish transformation to XML inputPath=" + inputPath);
		
	    }catch (XBRLPGCException exp)
	    {
		throw exp;
	    }
	    catch (Exception e)
	    {
		throw new TransformException("Error transform to xml " + e.getMessage(), e);
	    } finally
	    {
		if ((inputDocumentFile != null) && inputDocumentFile.exists())
		{
		    inputDocumentFile.delete();
		}
	    }
	
	    return xmlResult;
    }
    
   

    
    /**
     * Crea una instancia del transformador PGC.
     * 
     * @return
     * Nueva instancia del transformador.
     * 
     */
    public static IPGCTransformer createInstance(){
	
	return new PGCTransformer();
    }

    /**
     * Punto de entrada de ejecución
     * @param args
     */
    public static void main(String args[])
    {
    	String inputPath = null;
    	String outputPath = null;
    	String action = null;
    	boolean validateXbrl = false;
    	
    	if(args.length == 0 || (args.length != 1 && args.length != 5 && args.length != 7))
    	{
    		errorParameters();
    	}else
    	{
    	    for (int i=0; i<args.length; i++) 
    	    {
    		if (args[i].charAt(0) == '-') 
    		{

    		    switch (args[i].charAt(1)) 
    		    {
    		    case 'h': use(); System.exit(0);
    		    case 't':
    			action = args[i];
    			break;
    		    case 'i':
    			if (i+1 < args.length && action != null) 
    			{
    			    inputPath = args[++i];
    			}
    			else
    			{
    			    errorParameters();
    			}
    			break;
    		    case 'o':
    			if (i+1 < args.length && action != null)
    			{
    			    outputPath = args[++i];
    			}
    			else
    			{
    			    errorParameters();
    			}
    			break;
    		    case 'b':
    			if (i+1 < args.length && action != null)
    			{
    			    if(args[i+1].equals("false"))
    			    {
    				validateXbrl = false;
    				++i;
    			    }
    			    else if(args[i+1].equals("true"))
    			    {
    				validateXbrl = true;
    				++i;
    			    }
    			    else
    			    {
    				errorParameters();
    			    }
    			}
    			else
    			{
    			    errorParameters();
    			}
    			break;
    		    default:
    			errorParameters();
    		    }
    		}
    		else
    		{
    		    errorParameters();
    		}
    	    }
    	}
   	
	IPGCTransformer transformer = PGCTransformer.createInstance();
	
	try
	{
	    if(action.equals("-toXbrl")){
		transformer.transformToXbrl(inputPath, outputPath, validateXbrl);
		System.exit(-1);
	    }
	    if(action.equals("-toXml")){
		transformer.transformToXml(inputPath, outputPath, validateXbrl);
		System.exit(-1);
	    }
	} catch (Throwable e)
	{
	    logger.error(e);
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
	    System.out.println("Uso: pgctranformer [-toXbrl] [-i inputPath] [-o outputPath] [-b validateXbrl]");
	    System.out.println("					Convierte un fichero [inputPath] xml a un fichero [outputPath] xbrl");
	    System.out.println();    	
	    System.out.println("Uso: pgctranformer [-toXml] [-i inputPath] [-o outputPath] [-b validateXbrl]");
	    System.out.println("					Convierte un fichero [inputPath] xbrl a un fichero [outputPath] xml");
	    System.out.println();    	
	    System.out.println("Uso: pgctranformer -h");
	    System.out.println("     				Muestra esta ayuda");
	    System.out.println();
    }

}
