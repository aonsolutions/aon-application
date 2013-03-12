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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;

import es.inteco.xbrl.pgc.errors.ErrorsCatalogManager;
import es.inteco.xbrl.pgc.errors.out.PGC07ErrorsOutput;
import es.inteco.xbrl.pgc.errors.out.PGCErrorOut;
import es.inteco.xbrl.pgc.utils.UtilConstants;

/**
*
*
* Clase que contiene métodos comunes para el tratamiento de los ficheros de errores y de excepciones
* 
*<br><br>
* <b>Proyecto</b>: API XBRL-PGC2007 - Grupo de utilidades y librerías en código abierto para facilitar 
*                             la integración del formato XBRL en las herramientas software de gestión de  terceros
*                             aislándose de la complejidad en el procesamiento del modelo de datos de las taxonomías.
*                             Ayudando de esta forma a las empresas finales en la labor de realización de informes XBRL
*                             y asegurar el éxito de implantación del nuevo Plan General de Contabilidad 2007 en formato XBRL
*
*
* @version 1.0, 16/02/2009
* @author difusioncalidad@inteco.es
*
*/


public  abstract class LogFilesManager {
	
    /**
     * Metodo que separa la extensión de un fichero de el nombre del mismo, 
     * devuelve una cadena con el nombre sin extensión.
     * 
     * @param file
     * nombre completo del fichero
     * @return
     * devuelve una cadena con el nombre sin extensión
     */
	protected static String getNoExtensionFileName(String file) {

		String result="";

		File file1 = new File(file);
		String fileName = file1.getName();
		String[] splitFileName = fileName.split("\\.");
		result = splitFileName[0];

		return result;
	}
	
 	
	  /**
     * Metodo que separa la extensión de un fichero de el nombre del mismo, 
     * devuelve una cadena con el nombre sin extensión.
     * 
     * @param file
     * nombre completo del fichero
     * @return
     * devuelve una cadena con el nombre sin extensión
     */
	protected static String getFilePathAndNoExtensionName(String file) {

		String result="";

		File file1 = new File(file);
		String fileName = file1.getParent();

		if (file1.getParent()==null){
			File file2 = new File(file1.getAbsolutePath());
			fileName = file2.getParent();
		}

		if (fileName==null){
			result= getNoExtensionFileName(file);
		}
		else{
			result = fileName.concat("\\").concat(getNoExtensionFileName(file));
		}
		
		return result;
	}
	
	/**
	 * Metodo que genera un fichero XML según el formato común de salida de errores de la API
	 * @param filePath
	 * ubicación del fichero de salida
	 * @param text
	 * texto XML a grabar en el fichero
	 * @throws Throwable
	 */
	protected static void createXMLFile(String filePath, String text)throws Throwable{
		
		java.io.Reader txtReader=null;
		java.io.Writer diskFile =null;
		try{
			txtReader = new StringReader(text);
			PGC07ErrorsOutput xmlError = PGC07ErrorsOutput.unmarshal(txtReader);
			diskFile = new FileWriter(filePath);
			xmlError.marshal(diskFile);
			
		}
		catch(Throwable ex)	{
			throw ex;
		}
		finally{
			try{txtReader.close();}catch(Exception e1){}
			try{diskFile.close();}catch(Exception e2){}
		}
	}
	
	
	
	

	/**
	 * Metodo que genera un fichero XML según el formato común de salida de errores de la API
	 * @param filePath
	 * ubicación del fichero de salida
	 * @param loggerTraceList
	 * lista de mensajes de tipo warning o errores que llegan al logger
	 * @throws Throwable
	 */
	protected static void createXMLFile(String filePath, ArrayList<String> loggerTraceList)throws Throwable{

		java.io.Writer diskFile=null;
		try{

			PGC07ErrorsOutput xmlError = LogFilesManager.createErrorOutFromList(loggerTraceList) ;   //PGC07ErrorsOutput.unmarshal(txtReader);
			diskFile = new FileWriter(filePath);
			xmlError.marshal(diskFile);
		}
		catch(Throwable ex)	{
			throw ex;
		}
		finally{
			try{diskFile.close();}catch(Exception e2){}
		}
	}

	
	
	
	
	/**
	 * Metodo que genera un fichero XML según el formato común de salida de errores de la API.
	 * Si ya existiera el fichero, agrega el contenido
	 * @param filePath
	 * ubicación del fichero de salida
	 * @param text
	 * texto XML a grabar en el fichero
	 * 
	 * @param loggerTraceList
	 * lista de warnings o errores que llegan al logger y que vamos a grabar en el fichero
	 * 
	 * @throws Throwable
	 */
	protected static void createOrAppendXMLFile(String filePath, String text, ArrayList<String> loggerTraceList)throws Throwable{
		
		if ( loggerTraceList !=null &&  loggerTraceList.size()>0){
			LogFilesManager.createOrAppendXMLFile(filePath,loggerTraceList);	
		}
		
		LogFilesManager.createOrAppendXMLFile(filePath, text);
		
	}
	
	
	
	
	
	/**
	 * Metodo que genera un fichero XML según el formato común de salida de errores de la API.
	 * Si ya existiera el fichero, agrega el contenido
	 * @param filePath
	 * ubicación del fichero de salida
	 * @param text
	 * texto XML a grabar en el fichero
	 * @throws Throwable
	 */
	protected static void createOrAppendXMLFile(String filePath, String text)throws Throwable{
		
		java.io.Reader txtReader=null;
		java.io.Reader myFileReader = null;
		java.io.Writer diskFile = null;
		try{
			if (!(existsFile(filePath))){  //creamos un fichero nuevo
				createXMLFile(filePath, text);
			}else{//el fichero ya existe, tenemos que agregar nuestro texto
				//cargamos lo que nos pasan como parametro
				txtReader = new StringReader(text);
				PGC07ErrorsOutput newXmlError = PGC07ErrorsOutput.unmarshal(txtReader);				
				
				//leemos lo que ya hay en el fichero
				myFileReader = new FileReader(filePath);
				PGC07ErrorsOutput existingXmlError = PGC07ErrorsOutput.unmarshal(myFileReader);

				PGCErrorOut[] newErrorsList= newXmlError.getPGCErrorOut();

				for (int i = 0; i < newErrorsList.length; i++) 
				{
					PGCErrorOut newSingleError =newErrorsList[i];
					existingXmlError.addPGCErrorOut(newSingleError);
				}
	
				diskFile = new FileWriter(filePath);
				existingXmlError.marshal(diskFile);
			}
			
			
		}
		catch(Throwable ex)	{
			throw ex;
		}
		
		finally{
			try{txtReader.close();}catch(Exception e1){}
			try{myFileReader.close();}catch(Exception e3){}
			try{diskFile.close();}catch(Exception e2){}
		}
	}
		
	
	
	
	
	
	/**
	 * Metodo que genera un fichero XML según el formato común de salida de errores de la API.
	 * Si ya existiera el fichero, agrega el contenido
	 * @param filePath
	 * ubicación del fichero de salida
	 * @param loggerTraceList
	 * lista de warnings o errores que llegan al logger
	 * @throws Throwable
	 */
	protected static void createOrAppendXMLFile(String filePath, ArrayList<String> loggerTraceList)throws Throwable{
		
		
		try{
			if (!(existsFile(filePath))){  //creamos un fichero nuevo
				createXMLFile(filePath, loggerTraceList);
			}else{//el fichero ya existe, tenemos que agregar nuestro texto
				//cargamos lo que nos pasan como parametro
				//java.io.Reader txtReader = new StringReader(text);
				PGC07ErrorsOutput newXmlError =  LogFilesManager.createErrorOutFromList(loggerTraceList);  // PGC07ErrorsOutput.unmarshal(txtReader);				
				
				//leemos lo que ya hay en el fichero
				java.io.Reader myFileReader = new FileReader(filePath);
				PGC07ErrorsOutput existingXmlError = PGC07ErrorsOutput.unmarshal(myFileReader);

				PGCErrorOut[] newErrorsList= newXmlError.getPGCErrorOut();

				for (int i = 0; i < newErrorsList.length; i++) 
				{
					PGCErrorOut newSingleError =newErrorsList[i];
					existingXmlError.addPGCErrorOut(newSingleError);
				}
	
				java.io.Writer diskFile = new FileWriter(filePath);
				existingXmlError.marshal(diskFile);
			}
			
			
		}
		catch(Throwable ex)	{
			throw ex;
		}
	}	

	
	
	
	
	

	/*
	 * Metodo que genera un fichero con una cadena que recibe como entrada
	 * 
	 * @param filePath
	 * ubicación del fichero de salida
	 * @param text
	 * texto a insertar en el fichero de salida
	 * @throws Throwable
	 */
	
	/*
	protected static void createFile(String filePath, String text)throws Throwable{
		
		FileWriter fw =null;
		try{
			fw = new FileWriter(filePath);
			fw.write(text);
		}
		catch(Throwable ex)	{
			throw ex;
		}
		finally{
			if ( ! (fw ==null)){
				fw.close();
			}
		}
	}
	*/
	
	
    /**
     * Metodo que borra un fichero
     * 
     * @param filePath
     * ubicación del fichero a eliminar
     * 
     */
	protected static void deleteFile(String filePath){
		File file= new File(filePath);
		if (file.exists()){
			file.delete();
		}
	}
	
	

 
	/**
	 * Comprueba la existencia de un fichero
	 * @param filePath
	 * fichero a verificar
	 * @return
	 * Indicador (true/false) de la existencia del fichero
	 */
	protected static boolean existsFile(String filePath){
		boolean returnValue = false;
		
		File file= new File(filePath);
		
		returnValue= file.exists();
		
		return returnValue;

	}
	
	
	
	private static PGC07ErrorsOutput createErrorOutFromList(ArrayList<String> loggerTraceList)throws Exception{
		PGC07ErrorsOutput returnValue=null;



		if ( !(loggerTraceList==null) &&  loggerTraceList.size()>0){

			returnValue= new PGC07ErrorsOutput();
			for ( Iterator<String> warnIter = loggerTraceList.listIterator(); warnIter.hasNext(); ) {
				String warnText = (String) warnIter.next();

				PGCErrorOut newSingleError = new PGCErrorOut();
				newSingleError.setDefaultDescription(warnText);
				//TODO de momento: sólo existe traducción para el validate result, no para las excepciones de la API
				//newSingleError.setTranslatedDescription(warnText);

				try{
					newSingleError.setErrorType(ErrorsCatalogManager.getInstance().getError(UtilConstants.genericTraceMessage).getType().value());
				}
				catch(Exception e1 ){/*si no consigo el tipo de error no paro la aplicacion*/}

				try{
					newSingleError.setErrorCode(ErrorsCatalogManager.getInstance().getError(UtilConstants.genericTraceMessage).getErrorCode());
				}
				catch(Exception e2 ){/*si no consigo el code de error no paro la aplicacion*/}

				
				
				returnValue.addPGCErrorOut(newSingleError);
			}

		}
		return returnValue;
	}
	

}
