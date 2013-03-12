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



/**
 *
 *
 * Clase que encapsula el resultado de la transformación.  Es usada para devolver la respuesta
 * del servicio de transformación.
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
 * @see PGCWSTransformator
 */


public class TransformResult
{

    
    /**
     * Cadena de errores, en caso de que los hubiera, en la validación de esquema previa a la transformación.
     */
    private String xsdValidateError = null;
    
        
    /**
     * Cadena de errores, en caso de que los hubiera, en la validación XBRL previa a la generación.
     */
    private String xbrlValidateError = null;
    
    
    /**
     * Cadena de errores, en caso de que los hubiera, producidos en el proceso de generación. 
     */
    private String generalError = null;
    
    
    /**
     * Array de bytes con el resultado de la generación en caso de ser correcta. Si se produce algún error
     * este array de bytes será nulo.
     * 
     */
    private byte[] arrayResult = null;
    

    /**
     * Constructor por defecto 
     */
    public TransformResult() {
    	super();
    }



    /**
     * Devuelve un error de validación XSD
     * @return
     * Error de validación
     */
    public final String getXsdValidateError()
    {
        return xsdValidateError;
    }



    /**
     * Establece un error de validación XSD
     * @param xsdValidateError
     * error XSD
     */
    public final void setXsdValidateError(String xsdValidateError)
    {
        this.xsdValidateError = xsdValidateError;
    }



    /**
     * Devuelve un error de validación XBRL
     * @return
     * error de validación XBRL
     */
    public final String getXbrlValidateError()
    {
        return xbrlValidateError;
    }



    /**
     * Establece un error de validación XBRL
     * @param xbrlValidateError
     * Error de validación XBRL
     */
    public final void setXbrlValidateError(String xbrlValidateError)
    {
        this.xbrlValidateError = xbrlValidateError;
    }



    /**
     * Devuelve un error de tipo generico
     * @return
     * error de tipo generico
     */
    public final String getGeneralError()
    {
        return generalError;
    }



    /**
     * Establece un error de tipo genérico
     * @param generalError
     * error de tipo genérico
     */
    public final void setGeneralError(String generalError)
    {
        this.generalError = generalError;
    }



    /**
     * Devuelve los resultados en un array de bytes
     * @return
     * array de bytes resultado
     */
    public final byte[] getArrayResult()
    {
        return arrayResult;
    }



    /**
     * Establece los resultados de la transformación en un array de bytes
     * @param arrayResult
     * array de bytes resultado
     */
    public final void setArrayResult(byte[] arrayResult)
    {
        this.arrayResult = arrayResult;
    }

}
