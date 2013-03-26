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



/**
 *
 *
 * Clase que encapsula el resultado de la validación.  Es usada para devolver la respuesta
 * del servicio de validación.
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


public class ValidateResult
{
	private String _Errors = null;
	private String generalError = null;
	private boolean valid = true;
	
	
	
	
	/**
	 * Constructor por defecto
	 */
	public ValidateResult() {
		super();
	}



	
	/**
	 * Constructor con parametros
	 * @param errors
	 * Lista de errores que presenta el documento
	 * @param isValid
	 * Indicador de si el documento es válido
	 */
	public ValidateResult  (String errors, boolean isValid) {
		_Errors= errors;
		valid = isValid;
	}
	
	
	/**
	 * @return
	 * Lista de errores encontrados durante el proceso de validación.
	 */
	public String getErrors()
	{
		return _Errors;
	}

	/**
	 * @return
	 * Indicador de si el documento enviado a validar es valido
	 */
	public boolean isValid(){

		return valid;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
	    return _Errors;
	}


	
	
	/**
	 * @return
	 * Cadena de errores, en caso de que los hubiera, producidos en el proceso de validación.
	 */
	public final String getGeneralError()
	{
		return generalError;
	}

	/**
	 * @param generalError
	 * Cadena de errores, en caso de que los hubiera, producidos en el proceso de validación.
	 */
	public final void setGeneralError(String generalError)
	{
		this.generalError = generalError;
	}

	
}
