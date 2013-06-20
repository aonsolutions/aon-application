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

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import es.inteco.xbrl.pgc.transform.ConfigurationManager;



/**
 *
 *
 * Clase que traduce los errores de PGC.  Se encarga de buscar todos los valores
 * posibles en los mapas de PGC y añadir los identificadores obtenidos entre paréntesis al lado de los
 * parámetros traducidos.
 * <br />
 * Por ejemplo, si un parámetro contiene UnidadesTupla, el resultado sería UnidadesTupla(0900000)
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
 *
 *
 * @version 1.1, 18/02/2009
 * @author difusioncalidad@inteco.es
 *
 * 
 */


public class PGCErrorParamTranslate implements IErrorParamTranslate
{

    private static final Logger logger = Logger.getLogger(PGCErrorParamTranslate.class);
    
    private String reportID = null;
    
    /**
     * Constructor
     * @param reportID
     * Identificador de report
     */
    public PGCErrorParamTranslate(String reportID)
    {
	this.reportID = reportID;
    }
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.errors.IErrorParamTranslate#translate(java.lang.String)
     */
    @Override
    public String translate(String param)
    {
  	
	String transError = null;
	StringBuffer sb = new StringBuffer();
        try
        {
            Pattern p = Pattern.compile("([a-zA-Z_.0-9]+)");
            Matcher m = p.matcher(param);
            
            boolean result = m.find();
            while(result) 
            {
        	String conceptName = param.substring(m.start(), m.end());
        	String code = ConfigurationManager.getInstance().getInfoToTranslate(reportID, null, conceptName);
        	
        	if (code != null)
        	{
        	   
        	    transError = conceptName + "(" + code + ")";
        	    m.appendReplacement(sb, transError);
        	    logger.debug("Traduciendo código " + conceptName + "=" + transError);
        	}
        	
        	result = m.find();
            }
            m.appendTail(sb);

            
        } catch (Exception e)
        {
            logger.error(e);
        }

        transError = sb.toString();

	return transError;
    }
    
        
    
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.errors.IErrorParamTranslate#getPGCCode(java.lang.String)
     */
    public String getPGCCode(String param)
    {
    	ArrayList<String> arrCodes = new ArrayList<String>();
    	String transError = null;
    	StringBuffer sb = new StringBuffer();
    	try
    	{
   		
    		Pattern p = Pattern.compile("([a-zA-Z_.0-9]+)");
    		Matcher m = p.matcher(param);

    		boolean result = m.find();
    		while(result) 
    		{
    			String conceptName = param.substring(m.start(), m.end());
    			    			
    			String code = ConfigurationManager.getInstance().getInfoToTranslate(reportID, null, conceptName,true);

    			if (code != null)
    			{
    				arrCodes.add(code);
    			}

    			result = m.find();
    		}
    		m.appendTail(sb);


    	} catch (Exception e)
    	{
    		logger.error(e);
    	}

    	if (arrCodes.size()>0){
    		transError =arrCodes.toString().substring(1, arrCodes.toString().length() -1);
    	}
    	return transError;
    }    

}
