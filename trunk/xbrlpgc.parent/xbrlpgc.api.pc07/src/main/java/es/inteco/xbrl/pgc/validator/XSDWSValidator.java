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

import java.io.File;
import java.io.FileOutputStream;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.log4j.Logger;

import es.inteco.xbrl.pgc.errors.exceptions.XBRLPGCException;
import es.inteco.xbrl.pgc.utils.XbrlApiConfiguration;





/**
 *
 *
 * Clase que publica la validación XSD.
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
 * @version 1.0, 14/01/2009
 * @author difusioncalidad@inteco.es
 *
 */

@WebService()
public class XSDWSValidator 
{

    private static final Logger logger = Logger.getLogger(XBRLWSValidator.class);


    
    
    /**
     * Operación de servicio web encargada de realizar la validación XSD de un documento en formato comun
     * @param inputDocument
     * @return
     * Devuelve un objeto ValidateResult con el resultado de la validacion.
     * 
     * @see ValidateResult    
     */
    @WebMethod(operationName = "validate")
    public ValidateResult validate(@WebParam(name = "inputDocument") final byte[] inputDocument)  
    {
        ValidateResult result = new ValidateResult();
        File inputDocumentFile = null;
        try 
        {
            String tempDirectory = XbrlApiConfiguration.getInstance().getTempDirectory();
            File tempDirectoryFile = new File(tempDirectory);
            if (!tempDirectoryFile.exists())
            {
        	throw new XBRLPGCException(XBRLPGCException.notExistTempDirectory,new String[]{tempDirectory});
            }
            inputDocumentFile = File.createTempFile("xmlvalidator", ".xml", tempDirectoryFile);
            FileOutputStream fos = new FileOutputStream(inputDocumentFile);
            fos.write(inputDocument);
            
            result = XSDValidator.createInstance().validate(inputDocumentFile.getAbsolutePath());
            
        } catch (Throwable ex) {
            logger.error(ex.getMessage());
            result.setGeneralError(ex.getMessage());
        }
        finally
        {
            if ((inputDocumentFile!=null) && inputDocumentFile.exists())
            {
        	inputDocumentFile.delete();
            }
        }
        return result;
        
    }

}
