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

/**
 * Interfaz que debe cumplir una clase que ejecute llamadas a la API en sus funcionalidades básicas.
 * 
 * 
 *<br><br>
 * <b>Proyecto</b>: API XBRL-PGC2007 - Grupo de utilidades y librerías en código abierto para facilitar 
 *                             la integración del formato XBRL en las herramientas software de gestión de  terceros
 *                             aislándose de la complejidad en el procesamiento del modelo de datos de las taxonomías.
 *                             Ayudando de esta forma a las empresas finales en la labor de realización de informes XBRL
 *                             y asegurar el éxito de implantación del nuevo Plan General de Contabilidad 2007 en formato XBRL
 *
 * @version 1.0, 16/02/2009
 * @author difusioncalidad@inteco.es
 *
 */
public interface IApiDispatcher {

	/**
	 * Obtiene la ruta y nombre del documento XBRL
	 * @return
	 * Ruta y nombre
	 */
	public abstract String getDocXbrlPath() ;

	/**
	 * Establece la ruta y nombre del documento XBRL
	 * @param docXbrlPath
	 * ruta y nombre
	 */
	public void setDocXbrlPath(String docXbrlPath);
	

	/**Obtiene la ruta y nombre del documento XML
	 * @return
	 * Ruta y nombre
	 */
	public String getDocXMLPath() ;

	
	
	/**
	 * Establece la ruta y nombre del documento XML
	 * @param docXMLPath
	 * ruta y nombre
	 */
	public void setDocXMLPath(String docXMLPath) ;

	
	
	/**
	 * Obtiene la ruta y nombre del documento HTML
	 * @return
	 * Ruta y nombre del documento HTML
	 */
	public String getDocHTMLPath() ;

	/**
	 * Establece la ruta y nombre del documento HTML
	 * @param docHTMLPath
	 * nombre y ruta
	 */
	public void setDocHTMLPath(String docHTMLPath) ;

	
	
	
	/**
	 * Obtiene un indicador de si se solicita una validación.
	 * @return
	 * (true/false)
	 */
	public boolean isValidationRequired() ;

	/**
	 * Establece un indicador de si se solicita una validación de XBRL
	 * @param validationRequired
	 * indicador
	 */
	public void setValidationRequired(boolean validationRequired) ;
	
	
	
	/**
	 * Obtiene el nombre del modulo que se quiere visualizar
	 * @return
	 * Nombre de modulo
	 */
	public String getModuleName() ;

	/**
	 * Establece el nombre del modulo que se quiere visualizar
	 * @param moduleName
	 * nombre del modulo
	 */
	public void setModuleName(String moduleName) ;

	/**
	 * Obtiene ruta y nombre del fichero donde se almacenaran los errores
	 * @return
	 * Ruta y nombre
	 */
	public String getErrorFilePath() ;

	/**
	 * Establece ruta y nombre del fichero donde se almacenaran los errores
	 * @param errorFilePath
	 * ruta y nombre
	 */
	public void setErrorFilePath(String errorFilePath) ;

	
	
	
	/**
	 * Obtiene ruta y nombre del fichero donde se almacenaran los resultados de validacion
	 * @return
	 * Ruta y nombre
	 */
	public String getValidationResultFilePath() ;

	/**
	 * Establece ruta y nombre del fichero donde se almacenaran los resultados de validacion
	 * @param validationResultFilePath
	 * ruta y nombre
	 */
	public void setValidationResultFilePath(String validationResultFilePath) ;
	
	
	/**
	 * Metodo que ejecuta la validacion XBRL
	 * @return
	 * indicador del resultado del proceso<br>
	 * <b>0: </b>El proceso finalizó sin errores<br>
	 * <b>-1: </b>El proceso no finalizó correctamente<br>
	 * <b>1: </b>El proceso finalizó con errores tipo Warning<br>
	 */
	public abstract int runApiValidation();
	
	
	/**
	 * Metodo que ejecuta la transformacion a XML
	 * @return
	 * indicador del resultado del proceso<br>
	 * <b>0: </b>El proceso finalizó sin errores<br>
	 * <b>-1: </b>El proceso no finalizó correctamente<br>
	 * <b>1: </b>El proceso finalizó con errores tipo Warning<br>
	 */
	public abstract  int runApiTransformToXML();
	
	
	/**
	 * Metodo que ejecuta la transformacion a XBRL
	 * @return
	 * indicador del resultado del proceso<br>
	 * <b>0: </b>El proceso finalizó sin errores<br>
	 * <b>-1: </b>El proceso no finalizó correctamente<br>
	 * <b>1: </b>El proceso finalizó con errores tipo Warning<br>
	 */
	public abstract  int runApiTransformToXBRL();
	
	
	/**
	 * Metodo que ejecuta la visualizacion de un XML
	 * @return
	 * indicador del resultado del proceso<br>
	 * <b>0: </b>El proceso finalizó sin errores<br>
	 * <b>-1: </b>El proceso no finalizó correctamente<br>
	 * <b>1: </b>El proceso finalizó con errores tipo Warning<br> 
	 */
	public abstract  int runApiVisualizationFromXML();
	
	
	/**
	 * Metodo que ejecuta la visualizacion de un XBRL
	 * @return
	 * indicador del resultado del proceso<br>
	 * <b>0: </b>El proceso finalizó sin errores<br>
	 * <b>-1: </b>El proceso no finalizó correctamente<br>
	 * <b>1: </b>El proceso finalizó con errores tipo Warning<br> 
	 */
	public abstract  int runApiVisualizationFromXBRL();
	
}
