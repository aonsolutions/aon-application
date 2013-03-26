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

import java.util.ArrayList;

import es.inteco.xbrl.pgc.errors.exceptions.XBRLPGCException;


/**
 *
 *
 * La clase principal es PGCTransformer, es la clase que contiene los métodos encargados de realizar 
 * los procesos de transformación. Dicha clase se basa en el uso de clases de otros paquetes del sistema.
 * <br /><br />
 * Los procesos de transformación consisten en convertir los datos proporcionados en formato común y pasarlos 
 * a XBRL o viceversa.  
 * <br /><br />
 * Antes y después de realizar dichas conversiones, realizará las validaciones oportunas, 
 * basándose en los servicios de validación.
 * <br /><br />
 * Para realizar las transformaciones, esta clase se apoya en el uso de la librería XBReeze, 
 * la cual proporciona métodos y clases para un acceso estructurado a los informes y taxonomías XBRL.
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
 * @version 1.1, 18/02/2009
 * @author difusioncalidad@inteco.es
 *
 */


public interface IPGCTransformer
{

    /**
     * Realiza la transformación de un documento en formato común (XML) a XBRL, devolviendo la salida en un fichero, 
     * cuyo path es indicado en el parámetro de entrada outputPath.
     * <br /><br />
     * Antes de realizar la transformación a XBRL, el transformador comprueba si el XML de entrada en formato
     * común cumple con el esquema definido para tal, realizando una validación de esquema.
     * <br /><br />
     * Si se encuentran errores en la validación, se eleva una excepción del tipo XSDValidatorExcetion, indicando
     * en el mensaje los errores de validación encontrados.
     * <br /><br />
     * Una vez finalizada la transformación y antes de devolver la respuesta, en función del parámetro de entrada
     * validateXbrl se valida la instancia XBRL.
     * <br /><br />
     * En el caso de encontrarse errores en la validación del XBRL, el transformación eleva una excepción 
     * del tipo XBRLValidatorException, en cuyo mensaje se encuentran los erroes de validación encontrados.
     * 
     * @param inputPath
     * ruta del documento XML de entrada
     * 
     * @param outputPath
     * ruta donde se guardará el documento respuesta
     * 
     * @throws XBRLPGCException
     * Devuelve esta excepción en el caso de encontrar un error durante el proceso de transformación
     * diferente a los errores de validación.
     * 
     * @throws XSDValidatorException 
     * En el caso de encontrar errores en la validación del documento XML en formato común
     * de entrada.
     * 
     * @throws XBRLValidatorException
     * En el caso de encontrar errores en la validación del documento XBRL de salida.
     * 
     */
    public abstract void transformToXbrl(String inputPath, String outputPath) throws XBRLPGCException;

    
    
    /**
     * Realiza la transformación de un documento en formato común (XML) a XBRL, devolviendo la salida en un fichero, 
     * cuyo path es indicado en el parámetro de entrada outputPath.
     * <br /><br />
     * Antes de realizar la transformación a XBRL, el transformador comprueba si el XML de entrada en formato
     * común cumple con el esquema definido para tal, realizando una validación de esquema.
     * <br /><br />
     * Si se encuentran errores en la validación, se eleva una excepción del tipo XSDValidatorExcetion, indicando
     * en el mensaje los errores de validación encontrados.
     * <br /><br />
     * Una vez finalizada la transformación y antes de devolver la respuesta, en función del parámetro de entrada
     * validateXbrl se valida la instancia XBRL.
     * <br /><br />
     * En el caso de encontrarse errores en la validación del XBRL, el transformación eleva una excepción 
     * del tipo XBRLValidatorException, en cuyo mensaje se encuentran los erroes de validación encontrados.
     * 
     * @param inputPath
     * ruta del documento XML de entrada
     * 
     * @param outputPath
     * ruta donde se guardará el documento respuesta
     * 
     * @param validateXbrl
     * indica si se valida el Xbrl de salida
     * 
     * @throws XBRLPGCException
     * Devuelve esta excepción en el caso de encontrar un error durante el proceso de transformación
     * diferente a los errores de validación.
     * 
     * @throws XSDValidatorException 
     * En el caso de encontrar errores en la validación del documento XML en formato común
     * de entrada.
     * 
     * @throws XBRLValidatorException
     * En el caso de encontrar errores en la validación del documento XBRL de salida.
     * 
     */
    public abstract void transformToXbrl(String inputPath, String outputPath, boolean validateXbrl) throws XBRLPGCException;
    
    
    
    /**
     * Realiza la transformación de un documento en formato común (XML) a XBRL.  El documento XBRL
     * resultado es devuelto como un array de bytes. 
     * <br /><br />
     * Antes de realizar la transformación a XBRL, el transformador comprueba si el XML de entrada en formato
     * común cumple con el esquema definido para tal, realizando una validación de esquema.
     * <br /><br />
     * Si se encuentran errores en la validación, se eleva una excepción del tipo XSDValidatorExcetion, indicando
     * en el mensaje los errores de validación encontrados.
     * <br /><br />
     * Una vez finalizada la transformación y antes de devolver la respuesta, en función del parámetro de entrada
     * validateXbrl se valida la instancia XBRL.
     * <br /><br />
     * En el caso de encontrarse errores en la validación del XBRL, el transformación eleva una excepción 
     * del tipo XBRLValidatorException, en cuyo mensaje se encuentran los erroes de validación encontrados.
     * 
     * @param inputPath
     * Ruta del documento XML de entrada
     * 
     * @param validateXbrl
     * Valor booleano que indicará si se debe validar el documento XBRL antes de devolver.
     * 
     * @return
     * Objeto con el documento XBRL resultado de la transformación
     * 
     * @throws XBRLPGCException
     * Devuelve esta excepción en el caso de encontrar un error durante el proceso de transformación
     * diferente a los errores de validación.
     * 
     * @throws XSDValidatorException 
     * En el caso de encontrar errores en la validación del documento XML en formato común
     * de entrada.
     * 
     * @throws XBRLValidatorException
     * En el caso de encontrar errores en la validación del documento XBRL de salida.
     */
    public abstract byte[] transformToXbrl(String inputPath, boolean validateXbrl) throws XBRLPGCException;
    
    
    
    /**
     * Realiza la transformación de un documento en formato común (XML) a XBRL.  El documento XBRL
     * resultado es devuelto como un array de bytes. 
     * <br /><br />
     * El documento de entrada es pasado como un array de bytes.  Dicho array se guarda en un archivo 
     * temporal antes de continuar con el proceso.
     * <br /><br />
     * Antes de realizar la transformación a XBRL, el transformador comprueba si el XML de entrada en formato
     * común cumple con el esquema definido para tal, realizando una validación de esquema.
     * <br /><br />
     * Si se encuentran errores en la validación, se eleva una excepción del tipo XSDValidatorExcetion, indicando
     * en el mensaje los errores de validación encontrados.
     * <br /><br />
     * Una vez finalizada la transformación y antes de devolver la respuesta, en función del parámetro de entrada
     * validateXbrl se valida la instancia XBRL.
     * <br /><br />
     * En el caso de encontrarse errores en la validación del XBRL, el transformación eleva una excepción 
     * del tipo XBRLValidatorException, en cuyo mensaje se encuentran los erroes de validación encontrados.
     * 
     * @param inputDocument
     * Array de bytes con el documento XML de entrada
     * 
     * @param validateXbrl
     * Valor booleano que indicará si se debe validar el documento XBRL antes de devolver.
     * 
     * @return
     * Array de bytes con el documento XBRL resultado de la transformación
     * 
     * @throws XBRLPGCException
     * Devuelve esta excepción en el caso de encontrar un error durante el proceso de transformación
     * diferente a los errores de validación.
     * 
     * @throws XSDValidatorException 
     * En el caso de encontrar errores en la validación del documento XML en formato común
     * de entrada.
     * 
     * @throws XBRLValidatorException
     * En el caso de encontrar errores en la validación del documento XBRL de salida.
     */
    public abstract byte[] transformToXbrl(byte[] inputDocument, boolean validateXbrl) throws XBRLPGCException;

    
    
    
    /**
     * Realiza la transformación de un documento XBRL a un documento XML en formato común, validando previamente 
     * el documento XBRL de entrada si es cierto el parámetro de entrada validateXbrl.
     * <br /><br />
     * Si el documento de entrada contiene errores de validación XBRL y se ha indicado que se haga la validación
     * XBRL previa a la transformación, el transformación eleva una exceción XBRLValidatorException indicando
     * en el mensaje los errores de validación encontrados.
     * <br /><br />
     * Una vez finalizada la transformación, se hace una verficación del XML de salida.  Si se detecta algún error
     * en la validación de dicho XML, se eleva una excepción XSDValidatorException, indicando en el mensaje de error
     * los errores encontrados.

     * 
     * 
     * @param inputPath
     * Ruta del documento XBRL de entrada.
     * 
     * @param outputPath
     * Ruta donde se guardará el documento XML de respuesta.
     * 
     * @param validateXbrl
     * Valor booleano que indicará si se debe validar el documento XBRL de entrada, previamente a su transformación.
     * 
     * @throws XBRLPGCException
     * Si se produce algún error que no sea de validación, el sistema eleva una excepción de este tipo
     * indicando la causa del problema.
     * 
     * @throws XSDValidatorException 
     * En el caso de encontrar errores en la validación del documento XML en formato común de salida
     * de entrada.
     * 
     * @throws XBRLValidatorException
     * En el caso de encontrar errores en la validación del documento XBRL de entrada.
     */
    public abstract void transformToXml(String inputPath, String outputPath, boolean validateXbrl) throws XBRLPGCException;


    
    /**
     * Realiza la transformación de un documento XBRL a un documento XML en formato común, validando previamente 
     * el documento XBRL de entrada si es cierto el parámetro de entrada validateXbrl.
     * <br /><br />
     * Si el documento de entrada contiene errores de validación XBRL y se ha indicado que se haga la validación
     * XBRL previa a la transformación, el transformación eleva una exceción XBRLValidatorException indicando
     * en el mensaje los errores de validación encontrados.
     * <br /><br />
     * Una vez finalizada la transformación, se hace una verficación del XML de salida.  Si se detecta algún error
     * en la validación de dicho XML, se eleva una excepción XSDValidatorException, indicando en el mensaje de error
     * los errores encontrados.
     * <br /><br />
     * La respuesta la devuelve como un array de bytes conteniendo el documento generado.
     * 
     * 
     * 
     * @param inputPath
     * Ruta del documento XBRL de entrada
     * 
     * @param validateXbrl
     * valor booleano que indicará si se debe validar el documento XBRL de entrada, previamente a su transformación.
     * 
     * @return
     * Devuelve un documento con el resultado de la transformación.
     * 
     * @throws XBRLPGCException
     * Si se produce algún error que no sea de validación, el sistema eleva una excepción de este tipo
     * indicando la causa del problema.
     * 
     * @throws XSDValidatorException 
     * En el caso de encontrar errores en la validación del documento XML en formato común de salida
     * de entrada.
     * 
     * @throws XBRLValidatorException
     * En el caso de encontrar errores en la validación del documento XBRL de entrada.
     */
    public abstract byte[] transformToXml(String inputPath, boolean validateXbrl) throws XBRLPGCException;
    
    
    /**
     * Realiza la transformación de un documento XBRL a un documento XML en formato común, validando previamente 
     * el documento XBRL de entrada si es cierto el parámetro de entrada validateXbrl.
     * <br /><br />
     * 
     * El documento de entrada estará contenido en un array de bytes.
     * <br /><br />
     * 
     * Si el documento de entrada contiene errores de validación XBRL y se ha indicado que se haga la validación
     * XBRL previa a la transformación, el transformación eleva una exceción XBRLValidatorException indicando
     * en el mensaje los errores de validación encontrados.
     * <br /><br />
     * 
     * Una vez finalizada la transformación, se hace una verficación del XML de salida.  Si se detecta algún error
     * en la validación de dicho XML, se eleva una excepción XSDValidatorException, indicando en el mensaje de error
     * los errores encontrados.
     * <br /><br />
     * 
     * La respuesta la devuelve como un array de bytes conteniendo el documento generado.
     *  
     * 
     * @param inputDocument
     * array de bytes con el documento de entrada
     * 
     * @param validateXbrl
     * valor booleano que indicará si se debe validar el documento XBRL de entrada, previamente a su transformación.
     * 
     * @return
     * Devuelve un documento con el resultado de la transformación.
     * 
     * @throws XBRLPGCException
     * Si se produce algún error que no sea de validación, el sistema eleva una excepción de este tipo
     * indicando la causa del problema.
     * 
     * @throws XSDValidatorException 
     * En el caso de encontrar errores en la validación del documento XML en formato común de salida
     * de entrada.
     * 
     * @throws XBRLValidatorException
     * En el caso de encontrar errores en la validación del documento XBRL de entrada.
     */    
    public abstract  byte[] transformToXml(byte[] inputDocument, boolean validateXbrl) throws XBRLPGCException;
    
    
    
    
    
    /**
     * Obtiene la lista destinada a almacenar warnings o errores que llegan al logger durante el proceso
     * @return
     * ArrayList de String
     */
    public ArrayList<String> getLoggerTraceList();
    
    
    
    
    
	/**
	 * Establece la lista destinada a almacenar warnings o errores que llegan al logger durante el proceso
	 * @param loggerList
	 * ArrayList de String
	 */
	public void setLoggerTraceList(ArrayList<String> loggerList) ;

}
