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


import org.apache.log4j.Logger;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import es.inteco.xbrl.pgc.errors.GenericErrorsHandler;
import es.inteco.xbrl.pgc.utils.PGCUtils;




/**
 *
 *
 * Clase utilizada como manejador de errores en la validación XSD.
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


public class ErrorHandler extends DefaultHandler
{
    private static final Logger logger = Logger.getLogger(XBRLValidator.class);
    
    private boolean valid = true;
    private GenericErrorsHandler errorHandler = new GenericErrorsHandler();
    
    
    /**
     * Constructor por defecto
     */
    public ErrorHandler() 
    {
    	super();
    }
    
    /**
     * Devuelve la lista de errores en String
     * @return
     * Lista de errores
     */
    public String getErrors()
    {
	return errorHandler.toString();
    }

    /**
     * Devuelve un indicador de si el documento es valido
     * @return
     * Indicador de si el documento es valido
     */
    public boolean isValid(){
	
	return valid;
    }
    
    /**
     * Procesa el error de schema
     * @param exception
     * Excepcion analizada
     */
    private void ctrlError(SAXParseException exception)
    {
	try
	{
	    PGCUtils.processSchemaValidationError(exception.getMessage(), errorHandler);
	    
	} catch (Exception e)
	{
	    logger.error(e);
	    valid = false;
	}
	
	valid = false;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException exception)
    {
	ctrlError(exception);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException exception)
    {
	ctrlError(exception);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException exception)
    {
	ctrlError(exception);
    }
}
