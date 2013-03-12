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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.inteco.xbrl.pgc.errors.out.PGCErrorOut;
import es.inteco.xbrl.pgc.errors.out.PGCErrorParameter;




/**
*
*
* Clase que traduce la descripcion de los errores sustituyendo nombres XBRL por codigos PGC.
* 
* 
*<br><br>
* <b>Proyecto</b>: API XBRL-PGC2007 - Grupo de utilidades y librerías en código abierto para facilitar 
*                             la integración del formato XBRL en las herramientas software de gestión de  terceros
*                             aislándose de la complejidad en el procesamiento del modelo de datos de las taxonomías.
*                             Ayudando de esta forma a las empresas finales en la labor de realización de informes XBRL
*                             y asegurar el éxito de implantación del nuevo Plan General de Contabilidad 2007 en formato XBRL
*
* @version 1.0, 18/02/2009 
* @author difusioncalidad@inteco.es
*
*/
public class PGCErrorDescriptionTranslate implements IErrorDescriptionTranslate {

	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.errors.IErrorDescriptionTranslate#getTranslatedDescription(es.inteco.xbrl.pgc.errors.out.PGCErrorOut)
	 */
	public String getTranslatedDescription(PGCErrorOut errorOut)
	{

		String td = errorOut.getDefaultDescription();

		int parC =errorOut.getPGCErrorParameterCount();
		if (parC >0){
			for (int j = 0; j < parC; j++) 
			{
				PGCErrorParameter ep = errorOut.getPGCErrorParameter(j);
				if (!(ep.getPGCCode()==null ||  ep.getPGCCode().equals(""))) {
					String originalParamValue = ep.getContent();

					//las comillas simples no serán reemplazadas
					if (originalParamValue.charAt(0)=='\''){
						originalParamValue= originalParamValue.substring(1);
						
						int lastChar=originalParamValue.length();
						if (originalParamValue.charAt(lastChar -1)=='\''){
							originalParamValue= originalParamValue.substring(0,lastChar -1);
						}
					}

					//las llaves no serán reemplazadas
					if (originalParamValue.charAt(0)=='{'){
						originalParamValue= originalParamValue.substring(1);
						
						int lastChar=originalParamValue.length();
						if (originalParamValue.charAt(lastChar -1)=='}'){
							originalParamValue= originalParamValue.substring(0,lastChar -1);
						}
					}

					
					//se trata un caso especial: se observa que, en muchos parametros, los caracteres "http" vienen precedidos de "&"
					try{
						boolean containsTd=td.contains("&http://www.");
						boolean containsOp=originalParamValue.contains("&http://www.");
						if ( (containsTd!=containsOp )   &&    containsOp==true){
							originalParamValue=originalParamValue.replaceAll("&http://www.", "http://www.");
						}
					}catch(Exception e1){}					
					
					
		
					try{
						td = td.replaceAll(originalParamValue, ep.getPGCCode());
					}
					catch(java.util.regex.PatternSyntaxException e1){
						td = replaceByStep(td,originalParamValue, ep.getPGCCode());
					}
					catch(Exception e){
						td += " PGCCode(" + ep.getPGCCode() + ")";
					}
				}
			}//for (int j = 0; j < parC; j++)




		}    	
		return td;


	}

	
	
	private String replaceByStep(String text, String look, String replacement){
		
		String returnValue=text;
		
		int index1 =-1;
		try{
		
			String copyLook=notHttp(look);

			index1 = text.indexOf(copyLook);
			boolean result =false;
			if (index1>-1){result=true;}
			while(result) 
			{
				result =false;
								
				String ini =text.substring(0, index1);
				String finalT =text.substring(index1 + copyLook.length() );
				
				returnValue = ini + replacement +  finalT;
		
				text=returnValue;
				index1 = text.indexOf(copyLook);
				if (index1>-1){result=true;}
			}

		}catch(Throwable e1){
			returnValue = text += " PGCCode(" + replacement + ")";
		}
		
		return returnValue;
	}
	
	
	
	private String notHttp(String text){
		String transError = null;
		StringBuffer sb = new StringBuffer();


		Pattern p = Pattern.compile("&http");
		Matcher m = p.matcher(text);

		boolean result = m.find();
		while(result) 
		{
			transError = "http";
			m.appendReplacement(sb, transError);

			result = m.find();
		}
		m.appendTail(sb);

		transError = sb.toString();

		return transError;	
	}
}
