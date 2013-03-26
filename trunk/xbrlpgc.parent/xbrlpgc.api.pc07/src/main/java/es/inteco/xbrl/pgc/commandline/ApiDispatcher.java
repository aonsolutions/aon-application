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

package es.inteco.xbrl.pgc.commandline;


import java.util.ArrayList;

import org.apache.log4j.Logger;

import es.inteco.xbrl.pgc.errors.exceptions.TransformException;
import es.inteco.xbrl.pgc.errors.exceptions.ViewerException;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLPGCException;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLValidatorException;
import es.inteco.xbrl.pgc.transform.IPGCTransformer;
import es.inteco.xbrl.pgc.transform.PGCTransformer;
import es.inteco.xbrl.pgc.validator.IXBRLValidator;
import es.inteco.xbrl.pgc.validator.PGCXBRLValidator;
import es.inteco.xbrl.pgc.validator.ValidateResult;
import es.inteco.xbrl.pgc.viewer.IPGCViewer;
import es.inteco.xbrl.pgc.viewer.PGCViewer;

/**
 * Clase que realiza las llamadas a al API para ejecutar las diferentes funcionalidades
 * 
 * 
 *<br><br>
 * <b>Proyecto</b>: API XBRL-PGC2007 - Grupo de utilidades y librerías en código abierto para facilitar 
 *                             la integración del formato XBRL en las herramientas software de gestión de  terceros
 *                             aislándose de la complejidad en el procesamiento del modelo de datos de las taxonomías.
 *                             Ayudando de esta forma a las empresas finales en la labor de realización de informes XBRL
 *                             y asegurar el éxito de implantación del nuevo Plan General de Contabilidad 2007 en formato XBRL
 *
 * @version 1.0, 18/02/2009
 * @author difusioncalidad@inteco.es
 *
 */
public class ApiDispatcher implements IApiDispatcher{

	
	private String docXbrlPath =null;
	private String docXMLPath =null;
	private String docHTMLPath =null;
	private boolean validationRequired =false;
	private String moduleName =null;
	private String errorFilePath =null;
	private String validationResultFilePath =null;
	
	 static Logger logger = Logger.getLogger(ApiDispatcher.class);

	
	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.commandline.IApiDispatcher#getDocXbrlPath()
	 */
	public String getDocXbrlPath() {
		return docXbrlPath;
	}

	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.commandline.IApiDispatcher#setDocXbrlPath(java.lang.String)
	 */
	public void setDocXbrlPath(String docXbrlPath) {
		this.docXbrlPath = docXbrlPath;
	}

	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.commandline.IApiDispatcher#getDocXMLPath()
	 */
	public String getDocXMLPath() {
		return docXMLPath;
	}

	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.commandline.IApiDispatcher#setDocXMLPath(java.lang.String)
	 */
	public void setDocXMLPath(String docXMLPath) {
		this.docXMLPath = docXMLPath;
	}

	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.commandline.IApiDispatcher#getDocHTMLPath()
	 */
	public String getDocHTMLPath() {
		return docHTMLPath;
	}

	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.commandline.IApiDispatcher#setDocHTMLPath(java.lang.String)
	 */
	public void setDocHTMLPath(String docHTMLPath) {
		this.docHTMLPath = docHTMLPath;
	}

	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.commandline.IApiDispatcher#isValidationRequired()
	 */
	public boolean isValidationRequired() {
		return validationRequired;
	}

	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.commandline.IApiDispatcher#setValidationRequired(boolean)
	 */
	public void setValidationRequired(boolean validationRequired) {
		this.validationRequired = validationRequired;
	}

	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.commandline.IApiDispatcher#getModuleName()
	 */
	public String getModuleName() {
		return moduleName;
	}

	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.commandline.IApiDispatcher#setModuleName(java.lang.String)
	 */
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.commandline.IApiDispatcher#getErrorFilePath()
	 */
	public String getErrorFilePath() {
		return errorFilePath;
	}

	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.commandline.IApiDispatcher#setErrorFilePath(java.lang.String)
	 */
	public void setErrorFilePath(String errorFilePath) {
		this.errorFilePath = errorFilePath;
	}

	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.commandline.IApiDispatcher#getValidationFilePath()
	 */
	public String getValidationResultFilePath() {
		return validationResultFilePath;
	}

	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.commandline.IApiDispatcher#setValidationFilePath(java.lang.String)
	 */
	public void setValidationResultFilePath(String validationResultFilePath) {
		this.validationResultFilePath = validationResultFilePath;
	}
	
	
	
	/**
	 * constructor
	 */
	private ApiDispatcher() {
		super();
	}
	 
	/**
	 * Devuelve una instancia de la propia clase
	 * @return
	 * instancia de ApiDispatcher
	 */
	protected static IApiDispatcher createInstance(){
		return new ApiDispatcher();
	}
	
	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.commandline.IApiDispatcher#runApiValidation()
	 */
	public int runApiValidation(){
		int returnValue=-1;
		
		logger.debug("CommandLine: Running API validation");
		
		
		
		//validacion inicial
		if ( getDocXbrlPath()==null || !(LogFilesManager.existsFile(getDocXbrlPath()))   ){
			try{
				
				ArrayList<String> notFound= new ArrayList<String>();
				notFound.add("Input XBRL file not found");
				LogFilesManager.createOrAppendXMLFile(getErrorFilePath(),notFound);
				logger.info("CommandLine: created XML file with warnings or errors at " + getErrorFilePath());
			}catch(Throwable e){}
			return returnValue;
		}	
		
		// Se valida el XBRL obtenido	
		IXBRLValidator validator = PGCXBRLValidator.createInstance();
		ValidateResult validateResult = null;
		try{	
				
				validateResult = validator.validate( getDocXbrlPath());
				if ( !validateResult.isValid())
				{
					try 
				    {
				    	// Se guarda el resultado de la validación en formato XML en un fichero
						
						LogFilesManager.createXMLFile(getValidationResultFilePath() , validateResult.toString());
						logger.info("CommandLine: created XML file with validation result at " + getValidationResultFilePath());
					} catch (Throwable t1) 
					{
						 logger.error(t1.toString());
					}
				}
				returnValue =0;
			}
		catch (XBRLPGCException aPIError)
		{
			try 
		    {
		    	// Se guarda el error en formato XML en un fichero
				LogFilesManager.createOrAppendXMLFile(getErrorFilePath(), aPIError.getMessage());
				logger.info("CommandLine: created XML file with exceptions at " + getErrorFilePath());
			} catch (Throwable t2) 
			{
				 logger.error(t2.toString());
			}
			returnValue= -1;
		}
		catch (Throwable throwableError) 
		{
		    try {
		    	String xbrlDoc="";
		    	if (getDocXbrlPath()!=null){xbrlDoc = getDocXbrlPath();}
		    	XBRLValidatorException myApiException=new XBRLValidatorException(XBRLPGCException.genericErrorValidatingInstance, new String[]{xbrlDoc},throwableError);
				LogFilesManager.createOrAppendXMLFile(getErrorFilePath(), myApiException.getMessage());
				logger.info("CommandLine: created XML file with exceptions at " + getErrorFilePath());
			} catch (Throwable t3) 
			{
				logger.error(t3.toString());
			}
			returnValue= -1;
		}
		return returnValue;		
		
		
		
		
	}
	
	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.commandline.IApiDispatcher#runApiTransformToXML()
	 */
	public int runApiTransformToXML(){
		int returnValue=-1;
		int returnValidationProcess =0; //por defecto, todo ha ido bien en la validación

		logger.debug("CommandLine: Running API transformation to XML");
		
		//validacion inicial
		if ( getDocXbrlPath()==null || !(LogFilesManager.existsFile(getDocXbrlPath()))   ){
			try{
				
				ArrayList<String> notFound= new ArrayList<String>();
				notFound.add("Input XBRL file not found");
				LogFilesManager.createOrAppendXMLFile(getErrorFilePath(),notFound);
				logger.info("CommandLine: created XML file with warnings or errors at " + getErrorFilePath());
			}catch(Throwable e){}
			return returnValue;
		}		
		
		
		
		if (isValidationRequired()){
			returnValidationProcess = runApiValidation();
			logger.debug("CommandLine: validation process returns " + returnValidationProcess);
		}
		
		
		IPGCTransformer transformer = PGCTransformer.createInstance();
		transformer.setLoggerTraceList( new ArrayList<String>());
		
		
		try{	
			//NO debe validar, se habrá hecho en el proceso previo
			transformer.transformToXml(getDocXbrlPath() , getDocXMLPath(), false);
			
			returnValue= 0;		
			if (transformer.getLoggerTraceList().size()>0){
				returnValue= 1;
				try {
					LogFilesManager.createOrAppendXMLFile(getErrorFilePath(),transformer.getLoggerTraceList());	
					logger.info("CommandLine: created XML file with warnings or errors at " + getErrorFilePath());
				} catch (Throwable t3) 
				{
					logger.error( t3.toString()); 
				}
			}
		}
		catch (XBRLPGCException aPIError)
		{
			try 
			{
				// Se guarda el error en formato XML en un fichero.
				LogFilesManager.createOrAppendXMLFile(getErrorFilePath(), aPIError.getMessage(),transformer.getLoggerTraceList());
				logger.info("CommandLine: created XML file with exceptions at " + getErrorFilePath());
			} catch (Throwable t1) 
			{
				logger.error( t1.toString()); 
			}
			returnValue= -1;
		}
		catch (Throwable throwableError) 
		{
			try {
		    	String xbrlDoc="";
		    	if (getDocXbrlPath()!=null){xbrlDoc = getDocXbrlPath();}
		    	TransformException myApiException=new TransformException(TransformException.errorInTransformationToXml, new String[]{xbrlDoc},throwableError);
		    	LogFilesManager.createOrAppendXMLFile(getErrorFilePath(), myApiException.getMessage(),transformer.getLoggerTraceList());
		    	logger.info("CommandLine: created XML file with exceptions at " + getErrorFilePath());
			} catch (Throwable t2) 
			{
				logger.error( t2.toString()); 
			}
			returnValue= -1;
		}


		if ( !(returnValidationProcess == returnValue)){

			if (returnValidationProcess == -1 || returnValue== -1 ){
				returnValue=-1;
			}else if (returnValidationProcess == 1 || returnValue== 1 ){
				returnValue=1;
			}	
		}

		return returnValue;
	}
	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.commandline.IApiDispatcher#runApiTransformToXBRL()
	 */
	public int runApiTransformToXBRL(){
		int returnValue=-1;
		int returnValidationProcess =0;//por defecto, todo ha ido bien en la validación

		logger.debug("CommandLine: Running API transformation to XBRL");

		
		
		//validacion rutinaria
		if ( getDocXMLPath()==null || !(LogFilesManager.existsFile(getDocXMLPath()))   ){
			try{
				
				ArrayList<String> notFound= new ArrayList<String>();
				notFound.add("Input XML file not found");
				LogFilesManager.createOrAppendXMLFile(getErrorFilePath(),notFound);
				logger.info("CommandLine: created XML file with warnings or errors at " + getErrorFilePath());
			}catch(Throwable e){}
			return returnValue;
		}
		

		IPGCTransformer transformer = PGCTransformer.createInstance();
		transformer.setLoggerTraceList(new ArrayList<String>());
		try{	
			//no pediré que se valide, el proceso de validación se hará a parte
			transformer.transformToXbrl(getDocXMLPath(), getDocXbrlPath(),false);
			

			returnValue= 0;			
			if (transformer.getLoggerTraceList().size()>0){
				returnValue= 1;
				try {
					LogFilesManager.createOrAppendXMLFile(getErrorFilePath(),transformer.getLoggerTraceList());	
					logger.info("CommandLine: created XML file with warnings or errors at " + getErrorFilePath());
				} catch (Throwable t3) 
				{
					logger.error( t3.toString());
				}
			}
			
			
		}
		catch (XBRLPGCException aPIError)
		{
			try 
			{

				// Se guarda el error en formato XML en un fichero.
				LogFilesManager.createOrAppendXMLFile(getErrorFilePath(), aPIError.getMessage(),transformer.getLoggerTraceList());
				logger.info("CommandLine: created XML file with exceptions at " + getErrorFilePath());
			} catch (Throwable t1) 
			{
				logger.error( t1.toString());
			}
			returnValue= -1;
		}
		catch (Throwable throwableError) 
		{
			try {
		    	String xmlDoc="";
		    	if (getDocXMLPath()!=null){xmlDoc = getDocXMLPath();}
		    	TransformException myApiException=new TransformException(TransformException.errorInTransformationToXbrl, new String[]{xmlDoc},throwableError);
		    	LogFilesManager.createOrAppendXMLFile(getErrorFilePath(), myApiException.getMessage(), transformer.getLoggerTraceList());	
		    	logger.info("CommandLine: created XML file with exceptions at " + getErrorFilePath());
			} catch (Throwable t2) 
			{
				logger.error( t2.toString());
			}
			returnValue= -1;
		}
		

		if (isValidationRequired()){
			returnValidationProcess = runApiValidation();
			logger.debug("CommandLine: validation process returns " + returnValidationProcess);
		}
		
		if ( !(returnValidationProcess == returnValue)){
			if (returnValidationProcess == -1 || returnValue== -1 ){
				returnValue=-1;
			}else if (returnValidationProcess == 1 || returnValue== 1 ){
				returnValue=1;
			}	
		}

		return returnValue;
	}
	
	
	
	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.commandline.IApiDispatcher#runApiVisualization()
	 */
	public int runApiVisualizationFromXML(){
		int returnValue=-1;
		logger.debug("CommandLine: Running API visualization of XML");

		
		
		//validacion rutinaria
		if ( getDocXMLPath()==null || !(LogFilesManager.existsFile(getDocXMLPath()))   ){
			try{
				
				ArrayList<String> notFound= new ArrayList<String>();
				notFound.add("Input XML file not found");
				LogFilesManager.createOrAppendXMLFile(getErrorFilePath(),notFound);
				logger.info("CommandLine: created XML file with warnings or errors at " + getErrorFilePath());
			}catch(Throwable e){}
			return returnValue;
		}
		
		
		//validacion rutinaria
		if ( getModuleName()==null || getModuleName().equals("")   ){
			try{
				
				ArrayList<String> notFound= new ArrayList<String>();
				notFound.add("Module name is required");
				LogFilesManager.createOrAppendXMLFile(getErrorFilePath(),notFound);
				logger.info("CommandLine: created XML file with warnings or errors at " + getErrorFilePath());
			}catch(Throwable e){}
			return returnValue;
		}		
		
		
		
		IPGCViewer xmlViewer = PGCViewer.createInstance();
		xmlViewer.setLoggerTraceList(new ArrayList<String>());
		
		try{	
			xmlViewer.viewXML(getDocXMLPath(), getModuleName(), getDocHTMLPath());
			returnValue= 0;	
			
			
			if (xmlViewer.getLoggerTraceList().size()>0){
				returnValue= 1;
				try {
					LogFilesManager.createOrAppendXMLFile(getErrorFilePath(),xmlViewer.getLoggerTraceList());	
					logger.info("CommandLine: created XML file with warnings or errors at " + getErrorFilePath());
				} catch (Throwable t3) 
				{
					logger.error( t3.toString());
				}
			}			
			
			
		}
		catch (XBRLPGCException aPIError)
		{
			try 
		    {
		    	// Se guarda el error en formato XML en un fichero
				LogFilesManager.createOrAppendXMLFile(getErrorFilePath(), aPIError.getMessage(),xmlViewer.getLoggerTraceList());
				logger.info("CommandLine: created XML file with exceptions at " + getErrorFilePath());
			} catch (Throwable t1) 
			{
				logger.error( t1.toString());
			}

		}
		catch (Throwable throwableError) 
		{
		    try {
		    	ViewerException myApiException=new ViewerException(XBRLPGCException.errorTryingViewInstance,/* new String[]{xmlDoc},*/throwableError);
		    	LogFilesManager.createOrAppendXMLFile(getErrorFilePath(), myApiException.getMessage(), xmlViewer.getLoggerTraceList());	
		    	logger.info("CommandLine: created XML file with exceptions at " + getErrorFilePath());
			} catch (Throwable t2) 
			{
				logger.error( t2.toString());
			}

		}

		return returnValue;
	}
	
	
	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.commandline.IApiDispatcher#runApiVisualization()
	 */
	public int runApiVisualizationFromXBRL(){

		int returnValue=-1;
		
		logger.debug("CommandLine: Running API visualization of XBRL");

		
		
		//validacion rutinaria
		if ( getDocXbrlPath()==null || !(LogFilesManager.existsFile(getDocXbrlPath()))   ){
			try{
				
				ArrayList<String> notFound= new ArrayList<String>();
				notFound.add("Input XBRL file not found");
				LogFilesManager.createOrAppendXMLFile(getErrorFilePath(),notFound);
				logger.info("CommandLine: created XML file with warnings or errors at " + getErrorFilePath());
			}catch(Throwable e){}
			return returnValue;
		}
		
		
		//validacion rutinaria
		if ( getModuleName()==null || getModuleName().equals("")   ){
			try{
				
				ArrayList<String> notFound= new ArrayList<String>();
				notFound.add("Module name is required");
				LogFilesManager.createOrAppendXMLFile(getErrorFilePath(),notFound);
				logger.info("CommandLine: created XML file with warnings or errors at " + getErrorFilePath());
			}catch(Throwable e){}
			return returnValue;
		}		
		

		
		IPGCViewer xbrlViewer = PGCViewer.createInstance();
		xbrlViewer.setLoggerTraceList(new ArrayList<String>());

		try{	
			xbrlViewer.view(getDocXbrlPath(), getModuleName(), getDocHTMLPath());
			returnValue=0;
			
			if (xbrlViewer.getLoggerTraceList().size()>0){
				returnValue= 1;
				try {
					LogFilesManager.createOrAppendXMLFile(getErrorFilePath(),xbrlViewer.getLoggerTraceList());	
					logger.info("CommandLine: created XML file with warnings or errors at " + getErrorFilePath());
				} catch (Throwable t3) 
				{
					logger.error( t3.toString());
				}
			}			
			
		}
		catch (XBRLPGCException aPIError)
		{
			try 
		    {
		    	// Se guarda el error en formato XML en un fichero
				LogFilesManager.createOrAppendXMLFile(getErrorFilePath(), aPIError.getMessage(),xbrlViewer.getLoggerTraceList());
				logger.info("CommandLine: created XML file with exceptions at " + getErrorFilePath());
			} catch (Throwable t1) 
			{
				logger.error( t1.toString());
			}
			
		}
		catch (Throwable throwableError) 
		{
		    try {
		    	ViewerException myApiException=new ViewerException(XBRLPGCException.errorTryingViewInstance,/* new String[]{xmlDoc},*/throwableError);
		    	LogFilesManager.createOrAppendXMLFile(getErrorFilePath(), myApiException.getMessage(), xbrlViewer.getLoggerTraceList());
		    	logger.info("CommandLine: created XML file with exceptions at " + getErrorFilePath());
			} catch (Throwable t2) 
			{
				logger.error( t2.toString());
			}
			
		}

		return returnValue;
	}
}
