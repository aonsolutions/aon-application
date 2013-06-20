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

import java.io.StringReader;
import es.inteco.xbrl.pgc.errors.out.PGC07ErrorsOutput;



/**
 *
 * Clase base utilizada para notificar los errores de validación.
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
 * @version 1.1, 18/02/2009
 * @author difusioncalidad@inteco.es
 *
 */
public class ValidatorException extends XBRLPGCException
{

	private static final long serialVersionUID = 1L;

	public ValidatorException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
		
		//valoro lo que llega en messageIn
		if (ValidatorException.isPGC07ErrorsOutput(arg0)){
			//si ya llegaba bien, deshago lo que ha hecho super
			this.message = arg0;
		}
	}



	public ValidatorException(String messageIn, String[] parameters, Throwable cause) {
		super(messageIn, parameters, cause);
		
		//valoro lo que llega en messageIn
		if (ValidatorException.isPGC07ErrorsOutput(messageIn)){
			//si ya llegaba bien, deshago lo que ha hecho super
			this.message = messageIn;
		}
	}



	public ValidatorException(String messageIn, String[] parameters) {
		super(messageIn, parameters);
		
		//valoro lo que llega en messageIn
		if (ValidatorException.isPGC07ErrorsOutput(messageIn)){
			//si ya llegaba bien, deshago lo que ha hecho super
			this.message = messageIn;
		}
	}



	private static boolean isPGC07ErrorsOutput(String text){
		boolean returnValue=false;

		java.io.Reader txtReader=null;
		try{
			txtReader = new StringReader(text);
			PGC07ErrorsOutput.unmarshal(txtReader);
			returnValue=true;
		}
		catch(Throwable ex)	{}
		finally{try{txtReader.close();}catch(Exception e1){}}
		
		return returnValue;
	}

}
