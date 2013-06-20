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


package es.inteco.xbrl.pgc.viewer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import es.inteco.xbrl.pgc.errors.exceptions.ViewerException;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLPGCException;
import es.inteco.xbrl.pgc.transform.PGCTransformer;
import es.inteco.xbrl.pgc.utils.PGCUtils;
import es.inteco.xbrl.pgc.utils.XbrlApiConfiguration;


/**
 *
 *
 * Clase utilizada para visualizar informes desde un documento en formato XML o XBRL.
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


public class PGCViewer implements IPGCViewer
{
    static Logger logger = Logger.getLogger(PGCViewer.class);
    static String processGeneratedInput= null;
    static String processGeneratedError=null;
    static int processTimeOut=180000;  //3 minutes

    
    
    private ArrayList<String> loggerTraceList=null;
    
    
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.viewer.IPGCViewer#getLoggerTraceList()
     */
    public ArrayList<String> getLoggerTraceList() {
		return loggerTraceList;
	}

	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.viewer.IPGCViewer#setLoggerTraceList(java.util.ArrayList)
	 */
	public void setLoggerTraceList(ArrayList<String> loggerList) {
		this.loggerTraceList = loggerList;
	}
    
    
    
    
    
    
    
    /**
     * Constructor
     */
    PGCViewer() {
    }
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.viewer.IPGCViewer#view(java.lang.String, java.lang.String, java.lang.String)
     */
    public void view (String inputPath, String module,  String outputPath) throws XBRLPGCException
    {
	try
	{
	    byte[] xmlResult = PGCTransformer.createInstance().transformToXml(inputPath,  false);
	    byte[] htmlResult = viewXML(xmlResult, module);
	    File outputFileHtml = new File(outputPath);
	    FileOutputStream fos = new FileOutputStream(outputFileHtml);
	    fos.write(htmlResult);
	    fos.flush();
	    fos.close();

	}catch (XBRLPGCException ex)
	{
	    throw ex;
	}
	catch (Exception e)
	{
	    throw new ViewerException(XBRLPGCException.errorTryingViewInstance,e);
	}

    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.viewer.IPGCViewer#view(byte[], java.lang.String)
     */
    public byte[] view (byte[] inputDocument, String module) throws XBRLPGCException
    {
	File inputDocumentFile = null;
	File outputDocumentFile = null;
	byte[] resultHtml = null;
	try{

	    inputDocumentFile = PGCUtils.createTempFile(inputDocument,"viewerxbrl", ".xbrl");
	    outputDocumentFile = PGCUtils.createTempFile("viewerxbrl", ".xml");
	    
	    FileOutputStream fos =  new FileOutputStream(outputDocumentFile);
	    
	    view(inputDocumentFile.getAbsolutePath(), module, outputDocumentFile.getAbsolutePath());
	    
	    ByteArrayOutputStream outputStreamHtml = new ByteArrayOutputStream();
	    
	    PGCUtils.saveFileToByteArray(outputDocumentFile, outputStreamHtml);
	    
	    resultHtml = outputStreamHtml.toByteArray();
	    
	    fos.close();

	}catch(XBRLPGCException ex)
	{
	    throw ex;
	}
	catch(Exception e)
	{
	    throw new ViewerException(XBRLPGCException.errorTryingViewInstance,e);
	}
	finally
	{
	    if ((inputDocumentFile != null) && inputDocumentFile.exists())
	    {
		inputDocumentFile.delete();
	    }
	    if ((outputDocumentFile != null) && outputDocumentFile.exists())
	    {
		outputDocumentFile.delete();
	    }
	}
	return resultHtml;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.viewer.IPGCViewer#viewXML(java.lang.String, java.lang.String, java.lang.String)
     */
    public void viewXML (String inputPath, String module, String outputPath)throws XBRLPGCException
    {

	String transformerRoot=XbrlApiConfiguration.getInstance().getXslTransformerPath();
	
	String executableBatFile= XbrlApiConfiguration.getInstance().getXslTransformationExecutableBatFileName();
	File testExistsFile=null;
	boolean resultProcessExec =false;
	
	
	try {
	    //validation
	    testExistsFile= new File(inputPath);
	    if (!(testExistsFile.exists())){
		logger.error("Input file [" + inputPath + "] does not exists");
		throw new ViewerException(XBRLPGCException.fileNotFound, new String[] { inputPath,"Input file" }, new Throwable());
	    }


	    transformerRoot=transformerRoot.replaceAll("\\\\", "/");
	    if (!(transformerRoot.endsWith("/"))){
		transformerRoot += "/";
	    }
	    inputPath=inputPath.replaceAll("\\\\", "/");
	    outputPath=outputPath.replaceAll("\\\\", "/");

	    String xsl1 = transformerRoot + "xml2html.xsl";

	    //validation
	    testExistsFile= new File(xsl1);
	    if (!(testExistsFile.exists())){
		logger.error("Style sheet file [" + xsl1 + "] does not exists");
		throw new ViewerException(XBRLPGCException.fileNotFound, new String[] { xsl1,"Style sheet file at view process" }, new Throwable());
	    }
	    
	    String xslParams = "\"" + "module=" + module + ";configPath=" + transformerRoot.replaceAll(" ", "%20") + "\"";

	    try
	    {
		processGeneratedInput=null;
		processGeneratedError=null;
		
		String cmdText= "\"" + transformerRoot  + executableBatFile + "\"" +  " " + "\"" + transformerRoot + "\"" + " " + "\"" + inputPath + "\"" + " " + "\"" + xsl1 + "\"" + " " + "\"" + outputPath + "\"" + " "  + xslParams;
		
		resultProcessExec = executeCommand(cmdText, processTimeOut, this.loggerTraceList);

	    }catch(Throwable e)
	    {
		logger.error(e);
		throw new ViewerException(XBRLPGCException.errorTryingViewInstance,e);
	    }

	    //check process result
	    if (resultProcessExec==false)
	    {
		logger.error("Visualization failed. See also transformer XSL log");
		if (!(loggerTraceList==null)){loggerTraceList.add("Visualization failed. See also transformer XSL log");}
		
		if (  (processGeneratedError!=null) && (!(processGeneratedError.equals(""))))
		{
		    logger.error(processGeneratedError);
		    if (!(loggerTraceList==null)){loggerTraceList.add(processGeneratedError);}
		}
		
		throw new ViewerException(XBRLPGCException.errorTryingViewInstanceExternal ,new String[]{processGeneratedError});
	    }
	    else
	    {
		if (  (processGeneratedInput!=null) && (!(processGeneratedInput.equals(""))))
		{
		    logger.info(processGeneratedError);
		    if((processGeneratedError!=null) && (!(processGeneratedError.trim().equals(""))) && (loggerTraceList!=null)   ){
		    	//if (!(loggerTraceList==null)){loggerTraceList.add("WARNING: " + processGeneratedError);}
		    	loggerTraceList.add("WARNING: " + processGeneratedError);
		    }
		}
	    }


	    testExistsFile= new File(outputPath);
	    if (!(testExistsFile.exists()))
	    {
		logger.error("Result file " + outputPath + "] has not been created");
		throw new ViewerException(XBRLPGCException.errorTryingViewInstanceResultFile , new String[]{});
	    }

	    logger.info("Visualization has been generated " + outputPath);

	}
	catch(XBRLPGCException e)
	{
	    throw e;
	}
	catch (Throwable e)
	{
	    logger.error(e);
	    throw new ViewerException(XBRLPGCException.errorTryingViewInstance,e);
	}


    }



    /*Método que lanza un comando BATCH*/
    private static boolean executeCommand(String exCommand, int exTimeout ,ArrayList<String> loggerTraceList) {

	Process process = null;

	processGeneratedInput="";
	processGeneratedError="";
	
	try {
	    logger.info("Executing external command " + exCommand);
	    process = Runtime.getRuntime().exec(exCommand);
	}
	catch (IOException e) {
	    logger.error(e);
	    if (!(loggerTraceList==null)){loggerTraceList.add(e.toString());}
	    return false;
	}


	//input information
	char inVarText;
	InputStream inputStream = process.getInputStream();
	BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

	//error information
	char errVarText;
	InputStream errorStream = process.getErrorStream();
	BufferedInputStream bufferedErrorStream = new BufferedInputStream(errorStream);

	
	
	boolean ok = false;
	int timeout = exTimeout;
	int exitValue = -999;

	while (!ok) {
	    try {
		while (bufferedInputStream.available() > 0 ||
			bufferedErrorStream.available() > 0) {
		    while (bufferedInputStream.available() > 0) {
			inVarText=(char)bufferedInputStream.read();
			processGeneratedInput += String.valueOf(inVarText);
		    }
		    while (bufferedErrorStream.available() > 0) {
			errVarText=(char)bufferedErrorStream.read();
			processGeneratedError += String.valueOf(errVarText);
		    }
		}
	    }
	    catch (IOException e) {
		logger.error("Couldn't read response of batch process",e);
		if (!(loggerTraceList==null)){loggerTraceList.add( "Couldn't read response of batch process " +  e.toString());}
	    }

	    try {
		exitValue = process.exitValue();
		ok = true;
	    }
	    catch (IllegalThreadStateException e) {
		try {
		    // still running.
		    Thread.sleep(300);
		    timeout = timeout - 300;
		    if (timeout < 0 && timeout >= -300) {
			logger.error("ALERT: Command doesn't terminate:");
			logger.error(exCommand);
			logger.error("Shutting down command...");
			
			if (!(loggerTraceList==null)){loggerTraceList.add( "ALERT: Command doesn't terminate:");}
			if (!(loggerTraceList==null)){loggerTraceList.add( exCommand);}
			if (!(loggerTraceList==null)){loggerTraceList.add( "Shutting down command...");}
			
			process.destroy();
		    }
		    else if (timeout <0) {
			logger.info("ALERT: Command STILL doesn't terminate:");
			logger.info(exCommand);
			Thread.sleep(1000);
		    }
		} catch (InterruptedException e1) {
		    // doesn't matter
		}
	    }
	}

	if (!(ok)){process.destroy();}


	try {
	    while (bufferedInputStream.available() > 0) {
		inVarText=(char)bufferedInputStream.read();
		processGeneratedInput += String.valueOf(inVarText);
	    }
	    while (bufferedErrorStream.available() > 0) {
		errVarText=(char)bufferedErrorStream.read();
		processGeneratedError += String.valueOf(errVarText);
	    }
	}
	catch (IOException e) {
	    logger.error("Couldn't read response of batch process",e);
	    if (!(loggerTraceList==null)){loggerTraceList.add( "Couldn't read response of batch process " + e.toString());}
	}


	if (process!=null){
	    process.destroy();
	}

	return exitValue == 0;
    }


    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.viewer.IPGCViewer#viewXML(byte[], java.lang.String)
     */
    public byte[] viewXML (byte[] inputDocument, String module) throws XBRLPGCException
    {

	File inputDocumentFile = null;
	File outputDocumentFile = null;
	byte[] htmlResult = null;
	try
	{
	    String tempDirectory = XbrlApiConfiguration.getInstance().getTempDirectory();
	    File tempDirectoryFile = new File(tempDirectory);
	    if (!tempDirectoryFile.exists())
	    {
		throw new XBRLPGCException(XBRLPGCException.notExistTempDirectory, new String[] { tempDirectory });
	    }
	    inputDocumentFile = PGCUtils.createTempFile(inputDocument,"viewerxml", ".xml");
	    outputDocumentFile = PGCUtils.createTempFile("viewerhtml", ".html");

	    viewXML(inputDocumentFile.getAbsolutePath(), module,  outputDocumentFile.getAbsolutePath());
	    
	    ByteArrayOutputStream outputStreamHtml = new ByteArrayOutputStream();
	    
	    PGCUtils.saveFileToByteArray(outputDocumentFile, outputStreamHtml);
	    
	    htmlResult = outputStreamHtml.toByteArray();

	}
	catch(XBRLPGCException ex)
	{
	    throw ex;
	}
	catch(Exception e)
	{
	    throw new ViewerException(XBRLPGCException.errorTryingViewInstance,e);
	}
	finally
	{
	    if ((inputDocumentFile != null) && inputDocumentFile.exists())
	    {
		inputDocumentFile.delete();
	    }
	    if ((outputDocumentFile != null) && outputDocumentFile.exists())
	    {
		outputDocumentFile.delete();
	    }
	}
	return htmlResult;

    }





    /**
     * Crea una instancia de la propia clase
     * @return
     * Objeto del tipo IPGCViewer 
     */
    public static IPGCViewer createInstance()
    {
    	return new PGCViewer();
    }

    /**
     * Punto de entrada de ejecución
     * @param args
     */
    public static void main(String args[])
    {
    	String inputPath = null;
    	String module = null;
    	String outputPath = null;
    	String action = null;
    	
    	if(args.length == 0 || (args.length != 1 && args.length != 6 && args.length != 7)){
    		errorParameters();
    	}else{
    		for (int i=0; i<args.length; i++) {
        		
        		if (args[i].charAt(0) == '-') {
        			
        			switch (args[i].charAt(1)) {
        				case 'h': use(); System.exit(0);
        				case 'v':
        					action = "xml";
        					break;
        				case 'i':
        					if (i+1 < args.length) 
        						inputPath = args[++i];
        					else
        						errorParameters();
        					break;
        				case 'm':
        					if (i+1 < args.length) 
        						module = args[++i];
        					else
        						errorParameters();
        					break;
        				case 'o':
        					if (i+1 < args.length) 
        						outputPath = args[++i];
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
    	
    	IPGCViewer viewer = PGCViewer.createInstance();
	
	try
	{
			if(action == null){
				viewer.view(inputPath, module, outputPath);
			}
			else if(action.equals("xml")){
				viewer.viewXML(inputPath, module, outputPath);
			}
			else
				errorParameters();
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
	    System.out.println("Uso: pgcviewer [-i inputPath] [-m module] [-o outputPath]");
	    System.out.println("					A partir de un documento [inputPath] XBRL, genera el [outputPath] HTML,");
	    System.out.println("						correspondiente a la información para el [module].");
	    System.out.println();    	
	    System.out.println("Uso: pgcviewer -v [-i inputPath] [-m module] [-o outputPath]");
	    System.out.println("					A partir de un documento [inputPath] XML, genera el [outputPath] HTML,");
	    System.out.println("						correspondiente a la información para el [module].");
	    System.out.println();    	
	    System.out.println("Uso: xbrlvalidator -h");
	    System.out.println("     				Muestra esta ayuda");
	    System.out.println();
    }

}
