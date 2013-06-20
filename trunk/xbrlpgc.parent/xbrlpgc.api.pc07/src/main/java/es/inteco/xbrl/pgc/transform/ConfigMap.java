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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import es.inteco.xbrl.pgc.transform.maps.ConceptMap;
import es.inteco.xbrl.pgc.transform.maps.Statement;



/**
 *
 *
 * Clase que encapsula un mapa de configuración. Esta clase permite el acceso
 * indexado por nombre a los ConceptMaps contenidos en un Statement.
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


public class ConfigMap
{

    private Statement map = null;

    /**
     * Tabla que mantiene índices a los ConceptMaps de un Statement mediante el atributo outputID.
     */
    private HashMap<String, ConceptMap> indexTableByOutputID = new HashMap<String, ConceptMap>();
    
    /**
     * Tabla que mantiene índices a los ConceptMaps de un Statement mediante el atributo outputID para mapas dimensionales.
     */
    private HashMap<String, ArrayList<ConceptMap>> indexTableDimensionalByOutputID = new HashMap<String, ArrayList<ConceptMap>>();
    
    
    /**
     * Tabla que mantiene índices a los ConceptMaps de un Statement mediante el atributo inputID.
     */
    private HashMap<String, ConceptMap> indexTableByInputID = new HashMap<String, ConceptMap>();

   
    /**
     * Tabla que mantiene una indexación para los codes traducidos
     */
    private HashMap<String, String> indexTableToTranslate = new HashMap<String, String>();
    
  
    
    
    
    /**
     * Constructor de la clase, a la cual se le pasa un Statement sobre el cual se crearán los índices
     * para permitir un acceso más eficiente a los ConceptMaps que contiene.
     * 
     * @param map
     * Statement que se pretende indexar
     * 
     */
    public ConfigMap(Statement map) {

	this.map = map;

	int numConcpetMaps = map.getConceptMapCount();

	for (int i = 0; i < numConcpetMaps; i++)
	{
	    ConceptMap currentConceptMap = map.getConceptMap(i);
	    
	    //Quitar este if cuando no se generen mapas con inputID vacíos.
	    if ((currentConceptMap.getInputID() != null) && (!currentConceptMap.getInputID().trim().isEmpty()))
	    {
		indexTableByInputID.put(currentConceptMap.getInputID(), currentConceptMap);
		
		//Si es dimensional se añade a la lista de conceptMaps
		if ((currentConceptMap.getDomain() != null) && (!currentConceptMap.equals("")))
		{
		    ArrayList<ConceptMap> conceptMapList = indexTableDimensionalByOutputID.get(currentConceptMap.getOutputID());
		    if (conceptMapList == null)
		    {
			conceptMapList = new ArrayList<ConceptMap>();
			indexTableDimensionalByOutputID.put(currentConceptMap.getOutputID(),conceptMapList);
		    }
		    conceptMapList.add(currentConceptMap);
		}
		else
		{
		    indexTableByOutputID.put(currentConceptMap.getOutputID(), currentConceptMap);
		}

		//Se añade la indexación para optimizar la traducción de códigos
		String translateKey =  getOutputIDWithoutPath(currentConceptMap.getOutputID());
		String translateValue = "";
		if (indexTableToTranslate.containsKey(translateKey))
		{
		    translateValue = indexTableToTranslate.get(translateKey);
		}
		if (!translateValue.isEmpty())
		{
		    if (!isIncludedInputID(currentConceptMap.getInputID(),translateValue))
		    {
			translateValue += "|" + currentConceptMap.getInputID();
		    }
		}
		else
		{
		    translateValue = currentConceptMap.getInputID();
		}
		indexTableToTranslate.put(translateKey,translateValue);
	    }
	}
    }
    
    /*Devuelve el outputId sin el path*/
    private String getOutputIDWithoutPath(String outputID)
    {
	String outputIDWithoutPath = outputID;
	int lastIndexOfDoubleDot = outputIDWithoutPath.lastIndexOf(":");
	if (lastIndexOfDoubleDot>0)
	{
	    outputIDWithoutPath = outputIDWithoutPath.substring(lastIndexOfDoubleDot+1);
	}
	return outputIDWithoutPath;
    }
    
    
    /*Comprueba si el inputID está en la lista*/
    private boolean isIncludedInputID(String inputID, String inputIDList)
    {
	boolean isIncluded = false;

	Pattern p = Pattern.compile("([0-9]+)");
	Matcher m = p.matcher(inputIDList);

	boolean result = m.find();
	while(result && (!isIncluded)) 
	{
	    String inputIDTemp = inputIDList.substring(m.start(), m.end());
	    isIncluded = inputIDTemp.equals(inputID);
	    result = m.find();
	}

	return isIncluded;
    }
    
    

    /**
     * Accesor al Statement contenido en ConfigMap
     * 
     * @return
     * referencia al Statement contenido
     * 
     */
    public Statement getStatement()
    {
	return map;
    }

    
    /**
     * Accede a un ConceptMap a través de su identificador inputID.
     * 
     * @param inputID
     * Identificador del ConceptMap que se pretende consultar
     * 
     * @return
     * ConceptMap correspondiente al inputID indicado
     * 
     */
    public ConceptMap getConceptMapByInputID(String inputID)
    {

	return indexTableByInputID.get(inputID);
    }

    /**
     * Accede a un ConceptMap a través de su identificador outputID.
     * 
     * @param outputID
     * Identificador del ConceptMap que se pretende consultar
     * 
     * @param dimension
     * nombre de la dimensión
     * 
     * @param memberName
     * nombre del miembro
     * 
     * @return
     * ConceptMap correspondiente al outputID indicado
     * 
     */    
    public ConceptMap getConceptMapByOutputID(String outputID, String dimension, String memberName)
    {
	ConceptMap respConceptMap = null;
	
	if ((dimension != null) && (!dimension.equals("")))
	{
	    //Se busca por los conceptos para determinar cuál tiene la misma dimension y contiene el member con el nombre
	    //indicado en los parámetros de entrada.
	   ConceptMap currentConceptMap = null;
	   ArrayList<ConceptMap> conceptMapList = indexTableDimensionalByOutputID.get(outputID);
	   if (conceptMapList != null)
	   {
	       int numConceptMaps = conceptMapList.size();
	       int i=0;
	       while ((respConceptMap == null) && (i<numConceptMaps))
	       {
		   currentConceptMap = conceptMapList.get(i);
		   String currConMapDimName = currentConceptMap.getDomain();
		   currConMapDimName = currConMapDimName.substring(currConMapDimName.indexOf(":")+1);
		   
		   if (currConMapDimName.equals(dimension))
		   {
		       Object member = TransformerHelper.getMemberByName(currentConceptMap, memberName); 
		       if (member != null)
		       {
			   respConceptMap = currentConceptMap;
		       }
		   }
		   i++;
	       }
	       
	   }
	}
	else
	{
	    respConceptMap = indexTableByOutputID.get(outputID);
	}
	return respConceptMap;
    }

    /**
     * Devuelve un ConceptMap obtenido de la lista de traducciones entre nombres xbrl y codigos de formato común
     * @param outputID
     * @return
     * Devuelve un ConceptMap obtenido de la lista de traducciones entre nombres xbrl y codigos de formato común
     */
    public String getConceptMapToTranslate(String outputID)
    {
	return indexTableToTranslate.get(outputID);
    }
    
    
    
    /**
     * Devuelve una lista de objetos ConceptMap que concuerdan con un InputID.
     * El mismo InputID podría estar repetido para varios outputIDs. En general, pasará si existen tuplas
     * @param inputID
     * inputId de busqueda
     * @return
     * ArrayList de ConceptMap
     */
    public ArrayList<ConceptMap> getAllConceptMapByInputID(String inputID)
    {
    	ArrayList<ConceptMap> returnValue= new ArrayList<ConceptMap>();

    	int countD= indexTableByOutputID.size();
    	
    	for (int i = 0; i < countD; i++)
    	{
    	    ConceptMap currentConceptMap = map.getConceptMap(i);
    	    if ( !(currentConceptMap.getInputID()==null)    &&  !(currentConceptMap.getInputID().trim().isEmpty())  &&     currentConceptMap.getInputID().equals(inputID) ){
    	    returnValue.add(currentConceptMap);
    	    }
    	}
    	
    	return returnValue;

    }
    
    
    
    
}
