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


package es.inteco.xbrl.pgc.errors.exceptions;



/**
 *
 * Clase utilizada para notificar los errores encontrados en el proceso de transformación.
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


public class TransformException extends XBRLPGCException
{

    private static final long serialVersionUID = 1L;
    
    /**
     * No se encuentra en la configuración el código definido en el documento XBRL o XML, según 
     * el proceso que se esté ejecutando.
     */
    public static final String notFoudUnitCode = "notFoudUnitCode";
    /**
     * Error general encontrado en el proceso de transformación de un documento en formato común
     * (XML) a XBRL.
     */
    public static final String errorInTransformationToXbrl ="errorInTransformationToXbrl";
    /**
     * Error general encontrado en el proceso de transformación de un documento XBRL a XML en formato común.
     */
    public static final String errorInTransformationToXml ="errorInTransformationToXml";
    /**
     * Error obteniendo un módulo sobre el cual se guardarán los elementos, durante el proceso de generación
     * a XML.
     */
    public static final String errorGettingModule = "errorGettingModule";

    
    /**
     * Indica que viene varias veces el mismo concepto bajo el mismo periodo y con el mismo valor.
     */
    public static final String duplicateIemsError = "duplicateIemsError";
    
    /**
     * En PGC2007 todos los facts reportados sobre un contexto XBRL deben pertenecer al mismo módulo,
     * es decir, si un contexto tiene asociado un fact de patnetA no puede tener otro u otros pertenecientes
     * a patnetB o cualquier otro módulo diferente a patnetA. 
     */
    public static final String errorItemInBadModule="errorItemInBadModule";
    
    /**
     * No se ha encontrado conceptMap en el mapa.
     */
    public static final String errorIdNotFoundInMap="errorIdNotFoundInMap";
    
    
    public TransformException(String arg0, Throwable arg1)
    {
	super(arg0, arg1);

    }

    public TransformException(String message, String[] parameters, Throwable cause) {
	super(message, parameters, cause);

    }

    public TransformException(String message, String[] parameters) {
	super(message, parameters);
    }



    
}
