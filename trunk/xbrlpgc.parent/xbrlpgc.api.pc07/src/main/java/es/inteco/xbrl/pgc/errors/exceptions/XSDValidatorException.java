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
 *
 * Clase utilizada para notificar errores de validación de XML contra un esquema XSD.
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


public class XSDValidatorException extends ValidatorException
{

    private static final long serialVersionUID = 1L;
    
    
    /**
     * Error de validación de esquema del fichero de configuración global.xml
     */
    public static final String errorInValidationGlobalFile = "errorInValidationGlobalFile";
    /**
     * Error de validación de un mapa de configuración.
     */
    public static final String errorInValidationMapFile = "errorInValidationMapFile";
    /**
     * Error en la validación de un documento de entrada XML en formato común.
     */
    public static final String errorInValidationXmlInput = "errorInValidationXmlInput";

    
    public XSDValidatorException(String arg0, Throwable arg1)
    {
	super(arg0, arg1);
    }

    public XSDValidatorException(String message, String[] parameters, Throwable cause) {
	super(message, parameters, cause);
    }

    public XSDValidatorException(String message, String[] parameters) {
	super(message, parameters);
    }



}
