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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import es.inteco.xbrl.pgc.errors.catalog.PGCError;
import es.inteco.xbrl.pgc.errors.out.PGC07ErrorsOutput;
import es.inteco.xbrl.pgc.errors.out.PGCErrorOut;
import es.inteco.xbrl.pgc.errors.out.PGCErrorParameter;
import es.inteco.xbrl.pgc.utils.UtilConstants;


/**
 *
 *
 * Clase manejadora de errores.  Mantiene una estructura de los errores que se van
 * reportando.
 * <br />
 * <br />
 * Por tanto, contiene métodos para agregar errores y mediante el método sobrescrito
 * toString() devuelve la serialización de la estructura XML conteniendo todos los
 * errores que han sido reportados.
 * 
 * 
 *<br><br>
 * <b>Proyecto</b>: API XBRL-PGC2007 - Grupo de utilidades y librerías en código abierto para facilitar 
 *                             la integración del formato XBRL en las herramientas software de gestión de  terceros
 *                             aislándose de la complejidad en el procesamiento del modelo de datos de las taxonomías.
 *                             Ayudando de esta forma a las empresas finales en la labor de realización de informes XBRL
 *                             y asegurar el éxito de implantación del nuevo Plan General de Contabilidad 2007 en formato XBRL
 *
 * @version 1.1, 18/02/2009
 * @author difusioncalidad@inteco.es
 *
 */


public class GenericErrorsHandler
{

    private static final Logger logger = Logger.getLogger(GenericErrorsHandler.class);
    
    PGC07ErrorsOutput errors = null;
    
    /**
     * Constructor
     */
    public GenericErrorsHandler ()
    {
	errors = new PGC07ErrorsOutput();
    }
    
    /**
     * Devuelve una traduccion del parametro sustituyendo nombres de elementos xbrl por codigos PGC
     * @return
     * parametro de error traducido
     */
    protected IErrorParamTranslate getErrorParamTranslate()
    {
	return null;
    }
    
    
    
    /**
     * Devuelve una descripcion de error donde se sustituye nombres XBRL por codigos PGC
     * @return
     * descripcion de error
     */
    protected IErrorDescriptionTranslate getErrorDescriptionTranslate()
    {
	return null;
    }
    
    
    /**
     * Permite agregar un error a la estructura, a partir del identificador del error, la descripción
     * por defecto del error y una lista de parámetros.
     * 
     * @param errorID
     * identificador del error
     * 
     * @param defaultDescription
     * descripció por defecto
     * 
     * @param parameters
     * lista de parámetros para componer la descripción del error
     * 
     * @throws Exception
     * devuelve ante cualquier error que se produzca
     * 
     */
    public void addError(String errorID, String defaultDescription, ArrayList<String>parameters) throws Exception
    {
	PGCErrorOut newError = createErrorOutput(errorID, defaultDescription, parameters);
	errors.addPGCErrorOut(newError);
    }
    
    /*Devuelve la lista de ids incluidos en la lista de entrada*/
    private ArrayList<String> getIdList(String parametersList)
    {
	ArrayList<String> idList = new ArrayList<String>();
	try
	{
	    Pattern p = Pattern.compile("([0-9])");
	    Matcher m = p.matcher(parametersList);

	    while(m.find()) 
	    {
		String id = parametersList.substring(m.start(), m.end());
		idList.add(id);
	    }
	} catch (Exception e)
	{
	    logger.error(e);
	}
	return idList;
        
    }
    
    
    
   
    /*Crea un error de tipo PGCErrorOut*/
    private PGCErrorOut createErrorOutput(String errorID, String defaultDescription,  ArrayList<String>parameters) throws Exception
    {
    	PGCError errorDefinition = ErrorsCatalogManager.getInstance().getError(errorID);

    	if (errorDefinition == null)
    	{
    		errorDefinition =  ErrorsCatalogManager.getInstance().getError(UtilConstants.notCataloguedError);
    	}

    	PGCErrorOut errorOutput = new PGCErrorOut();

    	errorOutput.setDefaultDescription(defaultDescription);

    	errorOutput.setErrorCode(errorDefinition.getErrorCode());
    	try{
     	errorOutput.setErrorType(ErrorsCatalogManager.getInstance().getError(errorID).getType().value());
    	}catch(Exception e){/*si no podemos conseguir error type, continuamos el proceso sin lanazar una excepción*/}
    	
    	ArrayList<String> idList = getIdList(errorDefinition.getParameterList());

    	Iterator<String> parametersIter = null;   //parameters.iterator();
    	if (!(parameters==null)){
    		parametersIter = parameters.iterator();
    	}
    	
    	
    	

    	int i=0;
    	if (!(parameters==null)){
	    	while(parametersIter.hasNext())
	    	{
	
	    		PGCErrorParameter parameter = new PGCErrorParameter();
	    		String currentParam = parametersIter.next();
	    		if (getErrorParamTranslate()!= null)
	    		{
	    			String pgcCode = getErrorParamTranslate().getPGCCode(currentParam);
	    			parameter.setPGCCode(pgcCode);
	    		}
	
	    		parameter.setContent(currentParam);
	    		if (idList.size() > i)
	    		{
	    			parameter.setId(Long.parseLong(idList.get(i)));
	    		}
	    		errorOutput.addPGCErrorParameter(i,parameter);
	    		i++;
	    	}
    	}

    	//creamos translated description    	
    	if (getErrorDescriptionTranslate()!=null){
    		errorOutput.setTranslatedDescription(getErrorDescriptionTranslate().getTranslatedDescription(errorOutput) );
    	}
    	
    	return errorOutput;
    }

    
    
    
    
    
    /* 
     * Devuelve la estructura de errores reportados en un XML serializado como String.
     * 
     * @return
     * lista de errores con formato XML
     */
    @Override
    public String toString()
    {
    	return this.toString(errors);
    }

    
    
    
    
    
    /* 
     * Devuelve la estructura de errores reportados en un XML serializado como String.
     * 
     * @return
     * lista de errores con formato XML
     */
    private String toString(PGC07ErrorsOutput errorIn)
    {
	
	String resultToString = null;
	
	StringWriter writer = new StringWriter();
	
	try
	{
		errorIn.marshal(writer);
	    
	    resultToString = writer.toString();
	    
	    writer.close();
	    
	} catch (Throwable e)
	{
	    logger.error(e);
	}
	
	return resultToString;
    }
    
    
    
    
    
    
    
    

    /**
     * 
     * Devuelve la estructura de errores reportados en un XML serializado como String.
     * Procesa los errores para generar un resultado en un formato más legible 
     * 
     * @return
     * lista de errores con formato XML
     * 
     */
    public String toStringPostProcess()
    {
    	if (errors==null ||  errors.getPGCErrorOutCount()<1){
    		return this.toString();
    	}
    	
    	
    	PGC07ErrorsOutput errorsModified=postProcess();
    	
    	return this.toString(errorsModified);

    }
    
    
    
    private PGC07ErrorsOutput postProcess(){
    	PGC07ErrorsOutput returnValue=null;
    	try{


    		int nextI=-1;
    		int errorsCount = errors.getPGCErrorOutCount();

    		//validacion, si solo tienen un registro de error, no hacemos más
    		if (errorsCount<2){return errors;}

    		returnValue= new PGC07ErrorsOutput();

    		for (int i = 0; i < errorsCount; i++) 
    		{
    			nextI = i + 1;

    			PGCErrorOut actualError = errors.getPGCErrorOut(i);
    			
    			//leemos un 355 ó 301 ó 260, lo procesamos para ver si tiene pareja 395
    			if (actualError.getErrorCode()==355   ||  actualError.getErrorCode()==301
    					||  actualError.getErrorCode()==260){
    				PGCErrorOut nextError=null;
        			try{
        				nextError = errors.getPGCErrorOut(nextI);
        			}catch(IndexOutOfBoundsException e1){
        				nextI=-1;
        			}

        			if (nextI==-1  || nextError==null || nextError.getErrorCode()!=395   ){
        				//grabamos el error actual
        				returnValue.addPGCErrorOut(actualError);
        			}else{
        				//tenemos que hacer un merge de los errores
        				PGCErrorOut mergeError=null;
        				mergeError= doMerge (actualError, nextError);
        				
        				
        				i++; //subimos el contador porque no tiene sentido que vuelva a procesar la pareja del 355
        				
        				//agregamos el error recién creado
        				if ( !(mergeError==null)){
        					returnValue.addPGCErrorOut(mergeError);
        				}
        			}
        			
        			
    			}else{
    				//grabamos el error actual
    				returnValue.addPGCErrorOut(actualError);
    			}
    			


    		}




    	}catch(Throwable e){
    		logger.error("Error in postProcess of validation result");
    		//por un error aquí, no para la ejecución del programa
    		return errors;
    	}
    	return returnValue;
    }
    
    
    
    private PGCErrorOut doMerge(PGCErrorOut errorA, PGCErrorOut errorB){
    	PGCErrorOut returnValue = null;
    	try{
    		//identificamos la pareja
    		//if (errorA.getErrorCode() ==355  &&  errorB.getErrorCode() ==395  ){
    			returnValue= new PGCErrorOut();

    			String newErrorCodeStr =  String.valueOf(errorA.getErrorCode()).concat( String.valueOf(errorB.getErrorCode()));
    			long newErrorCode =  Long.parseLong(newErrorCodeStr)  ;
    			
    			returnValue.setErrorCode( newErrorCode);
    			returnValue.setErrorType(errorA.getErrorType());
    			
    			
    			//parametros
    			
    			long paramsCount=0;
    			
        		for (int i = 0; i < errorA.getPGCErrorParameterCount(); i++) 
        		{
        			PGCErrorParameter newParam= new PGCErrorParameter();
        			newParam.setContent(errorA.getPGCErrorParameter(i).getContent());
        			newParam.setPGCCode(errorA.getPGCErrorParameter(i).getPGCCode());
        			newParam.setId(paramsCount);
        			returnValue.addPGCErrorParameter(newParam);
        			paramsCount+=1;
        			
        		}
        		for (int i = 0; i < errorB.getPGCErrorParameterCount(); i++) 
        		{
        			PGCErrorParameter newParam= new PGCErrorParameter();
        			newParam.setContent(errorB.getPGCErrorParameter(i).getContent());
        			newParam.setPGCCode(errorB.getPGCErrorParameter(i).getPGCCode());
        			returnValue.addPGCErrorParameter(newParam);
        			newParam.setId(paramsCount);
        			paramsCount+=1;
        		}        		
    			
    			
        		returnValue.setDefaultDescription(errorA.getDefaultDescription() + " " + errorB.getDefaultDescription());
        		
        		//debemos tener en cuenta que translatedDescription podría ser nula si no se ejecuta la aplicación a través de la clase PGCValidator
        		String newTDesc=null;
        		
        		if(errorA.getTranslatedDescription()!=null  && (!(errorA.getTranslatedDescription().trim().equals("")))  ){
        			newTDesc=errorA.getTranslatedDescription();
        		}

        		if(errorB.getTranslatedDescription()!=null  && (!(errorB.getTranslatedDescription().trim().equals("")))  ){
        			if (newTDesc!=null){
        				newTDesc += " " + errorB.getTranslatedDescription();
        			}else{
        				newTDesc = errorB.getTranslatedDescription();
        			}
        		}
        		
        		returnValue.setTranslatedDescription(newTDesc);


    			
    			
    		//}
    		
    		
    	}
    	catch(Exception e1){
    		logger.error("Error in postProcess - doMerge of validation result");
    		//no lanzo una excepción ni paro el programa 
    		returnValue = null;
    	}
    	return returnValue;
    }
    
    
    
}
