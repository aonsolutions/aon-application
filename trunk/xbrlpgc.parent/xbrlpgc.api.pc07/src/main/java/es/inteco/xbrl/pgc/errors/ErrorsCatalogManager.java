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

import java.io.FileReader;
import java.util.HashMap;

import org.apache.log4j.Logger;

import es.inteco.xbrl.pgc.errors.catalog.PGC07ErrorsCatalog;
import es.inteco.xbrl.pgc.errors.catalog.PGCError;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLPGCException;

import es.inteco.xbrl.pgc.utils.XbrlApiConfiguration;



/**
 *
 *
 * Clase que gestiona el acceso al catálogo de errores de PGC2007.
 * 
 * <br>
 * 
 * El catálogo consiste en una recopilación de todos los errores que se pueden generar al invocar
 * los servicios de PGC2007, así como una codificación y clasificación de los mismos.
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


public class ErrorsCatalogManager
{
    private static final Logger logger = Logger.getLogger(ErrorsCatalogManager.class);

    private static ErrorsCatalogManager manager = null;
    
    private final HashMap<String, PGCError> indexErrorsCatalog = new HashMap<String, PGCError>();
    
    
    /**
     * Constructor
     */
    public ErrorsCatalogManager()
    {
	
	try
	{
	    FileReader reader = new FileReader(XbrlApiConfiguration.getInstance().getPgcErrorsCatalogPath());
	    PGC07ErrorsCatalog errorsCatalog = PGC07ErrorsCatalog.unmarshal(reader);
	    reader.close();
	    
	    int numErros = errorsCatalog.getPGCErrorCount();
	    
	    for (int i=0; i<numErros; i++)
	    {
		PGCError currentError = errorsCatalog.getPGCError(i);
		indexErrorsCatalog.put(currentError.getId(), currentError);
	    }
	    
	} catch (Throwable e)
	{
		e.printStackTrace();
	    logger.error(e);
	}
	
    }
    
    
    /**
     * Metodo inicializador
     * @throws XBRLPGCException
     */
    private synchronized static void initialize() throws XBRLPGCException
    {
	if (manager == null)
	{
	    manager = new ErrorsCatalogManager();
	}
    }

    /**
     * Devuelve una instancia de la propia clase
     * @return
     * instancia de la clase ErrorsCatalogManager
     * @throws XBRLPGCException
     */
    public static final ErrorsCatalogManager getInstance() throws XBRLPGCException
    {
	if (manager == null)
	{
	    initialize();
	}

	return manager;
    }
    
    
    
    /**
     * Devuelve un error a partir de un ID
     * @param errorID
     * 
     * @return
     * error de tipo PGCError
     */
    public PGCError getError(String errorID)
    {
	return indexErrorsCatalog.get(errorID);
    }
    
    
}
