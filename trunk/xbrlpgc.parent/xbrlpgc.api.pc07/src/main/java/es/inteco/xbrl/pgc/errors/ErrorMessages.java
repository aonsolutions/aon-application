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


package es.inteco.xbrl.pgc.errors;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;



/**
 *
 * Clase utilizada para acceder a los mensajes de error definidos en el fichero errorMessages.properties.
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


public class ErrorMessages
{
    private static ErrorMessages errorMessagesInstance = null;
    private  Properties errorMessagesProperties = null;

    /**
     * Constructor
     */
    private ErrorMessages() {

    	initialize();

    }
    
    
    /**
     * Inicializador
     */
    private synchronized void initialize()
    {
	if (errorMessagesProperties == null)
	{
	    errorMessagesProperties = new java.util.Properties();
//	    InputStream isProperties = ClassLoader.getSystemResourceAsStream("errorMessages.properties");
	    InputStream isProperties = ErrorMessages.class.getResourceAsStream("/errorMessages.properties");
	    try
	    {
		errorMessagesProperties.load(isProperties);

	    } catch (IOException e)
	    {
		System.out.println("Error cargando configuración de errores");
		e.printStackTrace();
	    }
	}
    }



        
    /**
     * Crea una instancia de la propia clase
     * @return
     * Instancia de la clase ErrorMessages
     */
    public static ErrorMessages getInstance()
    {
	if (errorMessagesInstance == null)
	{
	    errorMessagesInstance = new ErrorMessages();

	}
	return errorMessagesInstance;

    }

    /**
     * Obtiene el mensaje asociado a un key
     * @param errorKey
     * @return
     * mensaje asociado al key
     */
    public String getMessage(String errorKey)
    {
	return errorMessagesProperties.getProperty(errorKey);

    }
}
