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

import es.inteco.xbrl.pgc.errors.exceptions.DocumentNotFoundException;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLPGCException;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLValidatorException;
import es.inteco.xbrl.pgc.errors.exceptions.XSDValidatorException;


/**
 *
 *
 * Interfaz de la clase XBRLValidator. Define los métodos de validación de documentos XBRL contra la taxonomia
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


public interface IXBRLValidator
{

    /**
     * Valida el documento XBRL contra la taxonomia
     * @param documentPath
     * ruta del documento XBRL que se quiere validar
     * 
     * @return
     * Objeto de tipo ValidateResult. Recoge los mensajes de validación (si existen) y un indicador de si el documento XBRL se considera válido
     * 
     * @throws DocumentNotFoundException
     * @throws XSDValidatorException
     * @throws XBRLValidatorException
     * @throws XBRLPGCException 
     */
    public abstract ValidateResult validate(String documentPath) throws DocumentNotFoundException, XSDValidatorException, XBRLValidatorException, XBRLPGCException;
    
    
    /**
     * Valida el documento XBRL contra la taxonomia
     * @param inputDocument
     * Array de bytes con el documento XBRL que se quiere validar
     * 
     * @param postProcessResult
     * Indicador de si se procesará el validate result original de xbreeze para facilitar su posterior interpretacion 
     * 
     * @return
     * Objeto de tipo ValidateResult. Recoge los mensajes de validación (si existen) y un indicador de si el documento XBRL se considera válido
     * 
     * @throws DocumentNotFoundException
     * @throws XSDValidatorException
     * @throws XBRLValidatorException
     * @throws XBRLPGCException 
     */
    public abstract ValidateResult validate(byte[] inputDocument, boolean postProcessResult) throws DocumentNotFoundException, XSDValidatorException, XBRLValidatorException, XBRLPGCException;



    /**
     * Valida el documento XBRL contra la taxonomia
     * @param inputDocument
     * Array de bytes con el documento XBRL que se quiere validar
     * 
     * @return
     * Objeto de tipo ValidateResult. Recoge los mensajes de validación (si existen) y un indicador de si el documento XBRL se considera válido
     * 
     * @throws DocumentNotFoundException
     * @throws XSDValidatorException
     * @throws XBRLValidatorException
     * @throws XBRLPGCException 
     */
    public abstract ValidateResult validate(byte[] inputDocument) throws DocumentNotFoundException, XSDValidatorException, XBRLValidatorException, XBRLPGCException;




}
