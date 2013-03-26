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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.exolab.castor.types.Date;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;

import es.inteco.xbrl.pgc.apifacade.IXbrlContext;
import es.inteco.xbrl.pgc.apifacade.IXbrlFact;
import es.inteco.xbrl.pgc.apifacade.impl.XbrlApiFactory;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLPGCException;
import es.inteco.xbrl.pgc.transform.format.Item;
import es.inteco.xbrl.pgc.transform.format.Module;
import es.inteco.xbrl.pgc.transform.format.Record;
import es.inteco.xbrl.pgc.transform.format.Report;
import es.inteco.xbrl.pgc.transform.format.Row;
import es.inteco.xbrl.pgc.transform.format.Table;
import es.inteco.xbrl.pgc.transform.format.types.ItemSignType;
import es.inteco.xbrl.pgc.transform.maps.ConceptMap;
import es.inteco.xbrl.pgc.transform.maps.Member;
import es.inteco.xbrl.pgc.utils.PGCUtils;
import es.inteco.xbrl.pgc.utils.RowsComparator;
import es.inteco.xbrl.pgc.utils.XbrlApiConfiguration;


/**
 *
 *
 * Esta clase contiene métodos de utilidad y de apoyo para los procesos de transformación.
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


public abstract class TransformerHelper
{
    static final Logger logger = Logger.getLogger(TransformerHelper.class);
    
    /**
     * Comprueba si el item pasado como parámetro está duplicado, en cuyo caso deja un log a través
     * del logger dado.
     * <br /><br />
     * En el caso de que el item exista pero tenga un valor diferente se dejará un error en el buffer
     * duplicateItemErrors y una traza de error a través del logger.  En cambio, si los valores
     * son iguales, sólo se dejará un warning a través del logger.
     * 
     * @param itemTable
     * tabla de items donde se comprobará si existe el item dado
     * 
     * @param duplicateItemErrors
     * buffer en el cual se van concatenando los errores de items duplicados
     * 
     * @param conceptMap
     * conceptMap del item.
     * 
     * @param item
     * item del cual se quiere comprobar su duplicidad
     * 
     * @param logger
     * objeto a través del cual se dejarán las trazas
     * 
     * @param loggerTraceList
     * objeto a través del cual se dejarán los warnings o errores que llegan al logger
     * 
     */
    public static final void checkDuplicateItems(Hashtable<String, Item> itemTable, StringBuffer duplicateItemErrors, 
    		ConceptMap conceptMap, Item item, Logger logger, ArrayList<String> loggerTraceList)
    {
	if (itemTable.containsKey(conceptMap.getInputID()))
	{
	    Item tmpItem =  itemTable.get(conceptMap.getInputID());
	    String message = "Duplicate element: " + PGCUtils.getTraceObject(tmpItem) + " and " + PGCUtils.getTraceObject(item);
	    if ((tmpItem.getValue() != null) && (!tmpItem.getValue().equals(item.getValue())))
	    {
		logger.error(message);
		duplicateItemErrors.append(message + ";");
		if (!(loggerTraceList==null) ){loggerTraceList.add( message);}
	    }
	    else
	    {
		logger.warn(message);
		if (!(loggerTraceList==null) ){loggerTraceList.add("WARNING: " + message);}
	    }
	}
	else
	{
	    itemTable.put(conceptMap.getInputID(),item);
	}
    }
    
    
    
    
    /**
     * Comprueba si el item pasado como parámetro está duplicado, en cuyo caso deja un log a través
     * del logger dado.
     * <br /><br />
     * En el caso de que el item exista pero tenga un valor diferente se dejará un error en el buffer
     * duplicateItemErrors y una traza de error a través del logger.  En cambio, si los valores
     * son iguales, sólo se dejará un warning a través del logger.
     * 
     * @param itemTable
     * tabla de items donde se comprobará si existe el item dado
     * 
     * @param duplicateItemErrors
     * buffer en el cual se van concatenando los errores de items duplicados
     * 
     * @param conceptMap
     * conceptMap del item.
     * 
     * @param item
     * item del cual se quiere comprobar su duplicidad
     * 
     * @param logger
     * objeto a través del cual se dejarán las trazas
     * 
     */
    public static final void checkDuplicateItems(Hashtable<String, Item> itemTable, StringBuffer duplicateItemErrors, 
    		ConceptMap conceptMap, Item item, Logger logger)
    {
    	checkDuplicateItems(itemTable,duplicateItemErrors,conceptMap,item, logger, null);
    }
    
    
    
    
    
    
    
    
    
    

    /**
     * Comprueba si el item pasado como parámetro está duplicado, en cuyo caso deja un log a través
     * del logger dado.
     * <br /><br />
     * En el caso de que el item exista pero tenga un valor diferente se dejará un error en el buffer
     * duplicateItemErrors y una traza de error a través del logger.  En cambio, si los valores
     * son iguales, sólo se dejará un warning a través del logger.
     * 
     * @param itemTable
     * tabla de items donde se comprobará si existe el item dado
     * 
     * @param duplicateItemErrors
     * buffer en el cual se van concatenando los errores de items duplicados
     * 
     * @param item
     * item del cual se quiere comprobar su duplicidad
     * 
     * @param logger
     * objeto a través del cual se dejarán las trazas
     * 
     * @param loggerTraceList
     * objeto a través del cual se dejarán los warnings o errores que llegan al logger
     * 
     */
    public static final void checkDuplicateItems(Hashtable<String, Item> itemTable, StringBuffer duplicateItemErrors, 
    		Item item, Logger logger, ArrayList<String> loggerTraceList)
    {
	if (itemTable.containsKey(item.getId()))
	{
	    Item tmpItem =  itemTable.get(item.getId());
	    String message = "Duplicate element: " + PGCUtils.getTraceObject(tmpItem) + " and " + PGCUtils.getTraceObject(item);
	    if ((tmpItem.getValue() != null) && (!tmpItem.getValue().equals(item.getValue())))
	    {
		logger.error(message);
		duplicateItemErrors.append(message + ";");
		if (!(loggerTraceList==null) ){loggerTraceList.add(message);}
	    }
	    else
	    {
		logger.warn(message);
		if (!(loggerTraceList==null) ){loggerTraceList.add("WARNING: " + message);}
	    }
	}
	else
	{
	    itemTable.put(item.getId(),item);
	}
    }
    
    
    
    /**
     * Comprueba si el item pasado como parámetro está duplicado, en cuyo caso deja un log a través
     * del logger dado.
     * <br /><br />
     * En el caso de que el item exista pero tenga un valor diferente se dejará un error en el buffer
     * duplicateItemErrors y una traza de error a través del logger.  En cambio, si los valores
     * son iguales, sólo se dejará un warning a través del logger.
     * 
     * @param itemTable
     * tabla de items donde se comprobará si existe el item dado
     * 
     * @param duplicateItemErrors
     * buffer en el cual se van concatenando los errores de items duplicados
     * 
     * @param item
     * item del cual se quiere comprobar su duplicidad
     * 
     * @param logger
     * objeto a través del cual se dejarán las trazas
     * 
     */
    public static final void checkDuplicateItems(Hashtable<String, Item> itemTable, StringBuffer duplicateItemErrors, 
    		Item item, Logger logger)
    {
    	checkDuplicateItems(itemTable,duplicateItemErrors,item,logger,null);
    	
    }
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Genera el id para un footnote
     * @return
     * Id del footnote
     */
    public static final String generateFootNoteID()
    {
    
        return "id_footnote_elem_" + UUID.randomUUID();
    }

    /**
     * Se genera un footnote label identificador adecuado a las especificaciones
     * de PGC2007.
     * <br /><br />
     * Este identificador se compone mediante la constante footnote_ concatenado
     * con un número aleatorio.
     * 
     * @return
     * footnote generado
     */
    public static final String generateFootNoteLabelID()
    {
    
        return "footnote_" + UUID.randomUUID();
    }

    
    /**
     * Devuelve el numero aleatorio (code) generado formando parte del footnote
     * @param footNoteID
     * @return
     * code del id del footnote
     */
    public static final String getCodeFromFootNoteID(String footNoteID)
    {

        return footNoteID.substring(footNoteID.lastIndexOf("_")+1);
    }

    
    /**
     * Busca un row en un table a través de un index dado.
     * 
     * @param table
     * referencia al Table
     * 
     * @param indexRow
     * índice del row
     * 
     * @return
     * row si se encuentra o null en caso contrario
     * 
     */
    public static final Row getRow(Table table, String indexRow)
    {
        Row rowResponse = null;
        int numRows = table.getRowCount();
        for (int i=0; i<numRows; i++)
        {
            if (table.getRow(i).getId().equals(indexRow))
            {
        	rowResponse = table.getRow(i);
        	break;
            }
        }
        return rowResponse;
    }

    /**
     * Serializa un Report en un path dado por el atributo outputPath, añadiendo
     * el schemalocation configurado para los Reports.
     * 
     * @param outputPath
     * path en el cual se guardará el report
     * 
     * @param report
     * referencia al objeto report que se pretende serializar
     * 
     * @throws MarshalException
     * Excepción en caso de que no se pueda serializar
     * 
     * @throws ValidationException
     * Excepción en caso de formato no válido
     * 
     * @throws IOException
     * Excepción de entrada y salida
     * 
     */
    public static final void marshalReport(String outputPath, Report report) throws MarshalException, ValidationException, IOException
    {
    
        Marshaller marshaller = new Marshaller();
        marshaller.setSchemaLocation(XbrlApiConfiguration.getInstance().getSchemaLocationIOInterface());
        marshaller.setWriter(new FileWriter(new File(outputPath)));
        marshaller.marshal(report);
    }

    /**
     * Serializa un Report en un Writer dado por el atributo osWriterResult, añadiendo
     * el schemalocation configurado para los Reports.
     * 
     * @param osWriterResult
     * Writer a través del cual se guardará el report
     * 
     * @param report
     * referencia al objeto report que se pretende serializar
     * 
     * @throws MarshalException
     * Excepción en caso de que no se pueda serializar
     * 
     * @throws ValidationException
     * Excepción en caso de formato no válido
     * 
     * @throws IOException
     * Excepción de entrada y salida
     * 
     */
    public static final void marshalReport(OutputStreamWriter osWriterResult, Report report) throws MarshalException, ValidationException, IOException
    {
    
        Marshaller marshaller = new Marshaller();
        marshaller.setSchemaLocation(XbrlApiConfiguration.getInstance().getSchemaLocationIOInterface());
        marshaller.setWriter(osWriterResult);
        marshaller.marshal(report);
    }

    /**
     * Carga los items contenidos en el array itemList sobre la tabla itemTable,
     * indexados por el id del item.
     * 
     * @param itemList
     * array de items
     * 
     * @param itemTable
     * tabla de items
     * 
     */
    public static  final void loadItems(Item[] itemList, Hashtable<String, Item> itemTable)
    {
        int numItems = itemList.length;
        for (int i=0; i<numItems; i++)
        {
            Item currentItem = itemList[i];
            itemTable.put(currentItem.getId(), currentItem);
        }
    }

    
  
    
    
    
    
    
    
    
    
    
    
    /**
     * Se unifican los items y records pasados en los parámetros items y records en una tabla 
     * indexada por el IXbrlFact resultado de la creación del XbrlFact correspondiente a cada
     * uno de dichos items y records.
     * <br /><br />
     * Al mismo tiempo, dichos items y records quedan indexados por el identificador de ambos sobre
     * las tablas recordsTable e itemsTable para records e items respectivamente.
     * <br /><br />
     * Este preproceso permite ordenar los items y records a través del order indicado en el 
     * conceptMap, previamente a ser generado el XBRL.  Dicho orden es necesario debido a que
     * sobre todo, para las tuplas es imprescindible que se generen en el mismo orden en el cual
     * fueron definidas en el esquema de la taxonomía mediante "sequence".
     * 
     * @param configM
     * configuración para acceder a los conceptMaps.
     * 
     * @param items
     * lista de items
     * 
     * @param records
     * lista de records
     * 
     * @param itemsTable
     * tabla de items indexados
     * 
     * @param recordsTable
     * tabla de records indexados
     * 
     * @param reportID
     * identificador del report, necesario para acceder a la configuración
     * 
     * @param moduleID
     * identificador del módulo, necesario para acceder a la configuración
     * 
     * @param loggerTraceList
     * lista donde se almacenarán los warnings o errores que llegan al logger durante proceso 
     * 
     * @param parentCode
     * codigo PGC padre del item
     * 
     * @return
     * tabla unión de items y records
     * 
     * @throws XBRLPGCException
     * en caso de producirse algún error
     * 
     */
    public static final Hashtable<IXbrlFact, ConceptMap> unionXbrlFacts(ConfigurationManager configM, Item[] items, Record[] records, 
    		Hashtable<String,Item> itemsTable, Hashtable<String, Record>recordsTable,String reportID, 
    		String moduleID, ArrayList<String> loggerTraceList, String parentCode) throws XBRLPGCException
    {
	Hashtable<IXbrlFact, ConceptMap> factsTable = new Hashtable<IXbrlFact, ConceptMap>();
	
	int numItems = items.length;
	for (int i=0; i<numItems; i++)
	{
	    Item currentItem = items[i];
	    ConceptMap itemInfo = configM.getInfoByInputID(reportID,moduleID, currentItem.getId());

	    if (itemInfo == null)
	    {
		logger.warn("Not exists the conceptMap " + currentItem.getId() + " in the map " + moduleID);
		if (!(loggerTraceList==null)){loggerTraceList.add("WARNING: Not exists the conceptMap " + currentItem.getId() + " in the map " + moduleID);}
		continue;
	    } 
	   	    
	    //mar09. rectificamos el order del item si fuera necesario (usualmente en items que pertenecen a varias tuplas y, en cada una de ellas tienen un orden distinto)
	    if (!(parentCode==null)){
	    	BigDecimal newOrder= null;
	    	try{
	    		newOrder= configM.getOrderToReplace(reportID,moduleID,currentItem.getId() , parentCode);
	    	}catch(Exception e3){}
	    	if(!(newOrder==null)  &&  newOrder.intValue()>-1  &&   !(newOrder.equals(itemInfo.getOrder()))  ){
	    		logger.debug("Replaced order in item " + currentItem.getId() + " map: " + moduleID + " first parent code: " + parentCode + " old order: " + itemInfo.getOrder().toString() + " new order: " +  newOrder.toString());
	    		itemInfo.setOrder(newOrder);
	    	}
	    }
	    
	    IXbrlFact fact = createFact(itemInfo,currentItem,false);
	    factsTable.put(fact, itemInfo);
	    itemsTable.put(itemInfo.getInputID(), currentItem);
	}
	
	int numRecords = records.length;
	for (int i=0; i<numRecords; i++)
	{
	    Record currentRecord = records[i];
	    ConceptMap itemInfo = configM.getInfoByInputID(reportID,moduleID, currentRecord.getId());
	    
	    if (itemInfo == null)
	    {
		logger.warn("Not exists the conceptMap " + currentRecord.getId() + " in the map " + moduleID);
		if (!(loggerTraceList==null)){loggerTraceList.add("WARNING: Not exists the conceptMap " + currentRecord.getId() + " in the map " + moduleID);}
		continue;
	    } 
	    
	    IXbrlFact fact = createFact(itemInfo,null,true);
	    
	    factsTable.put(fact, itemInfo);
	    recordsTable.put(itemInfo.getInputID(),currentRecord);
	    
	}
	return factsTable;
    }    
    
    
    
    
    /**
     * Se unifican los items y records pasados en los parámetros items y records en una tabla 
     * indexada por el IXbrlFact resultado de la creación del XbrlFact correspondiente a cada
     * uno de dichos items y records.
     * <br /><br />
     * Al mismo tiempo, dichos items y records quedan indexados por el identificador de ambos sobre
     * las tablas recordsTable e itemsTable para records e items respectivamente.
     * <br /><br />
     * Este preproceso permite ordenar los items y records a través del order indicado en el 
     * conceptMap, previamente a ser generado el XBRL.  Dicho orden es necesario debido a que
     * sobre todo, para las tuplas es imprescindible que se generen en el mismo orden en el cual
     * fueron definidas en el esquema de la taxonomía mediante "sequence".
     * 
     * @param configM
     * configuración para acceder a los conceptMaps.
     * 
     * @param items
     * lista de items
     * 
     * @param records
     * lista de records
     * 
     * @param itemsTable
     * tabla de items indexados
     * 
     * @param recordsTable
     * tabla de records indexados
     * 
     * @param reportID
     * identificador del report, necesario para acceder a la configuración
     * 
     * @param moduleID
     * identificador del módulo, necesario para acceder a la configuración
     * 
     * @return
     * tabla unión de items y records
     * 
     * @throws XBRLPGCException
     * en caso de producirse algún error
     * 
     */
    public static final Hashtable<IXbrlFact, ConceptMap> unionXbrlFacts(ConfigurationManager configM, Item[] items, Record[] records, 
    		Hashtable<String,Item> itemsTable, Hashtable<String, Record>recordsTable,String reportID, 
    		String moduleID) throws XBRLPGCException
    {
    	return unionXbrlFacts(configM,items,records,itemsTable,recordsTable,reportID,moduleID,null);
    }
    
    
    
    
    
    /**
     * Se unifican los items y records pasados en los parámetros items y records en una tabla 
     * indexada por el IXbrlFact resultado de la creación del XbrlFact correspondiente a cada
     * uno de dichos items y records.
     * <br /><br />
     * Al mismo tiempo, dichos items y records quedan indexados por el identificador de ambos sobre
     * las tablas recordsTable e itemsTable para records e items respectivamente.
     * <br /><br />
     * Este preproceso permite ordenar los items y records a través del order indicado en el 
     * conceptMap, previamente a ser generado el XBRL.  Dicho orden es necesario debido a que
     * sobre todo, para las tuplas es imprescindible que se generen en el mismo orden en el cual
     * fueron definidas en el esquema de la taxonomía mediante "sequence".
     * 
     * @param configM
     * configuración para acceder a los conceptMaps.
     * 
     * @param items
     * lista de items
     * 
     * @param records
     * lista de records
     * 
     * @param itemsTable
     * tabla de items indexados
     * 
     * @param recordsTable
     * tabla de records indexados
     * 
     * @param reportID
     * identificador del report, necesario para acceder a la configuración
     * 
     * @param moduleID
     * identificador del módulo, necesario para acceder a la configuración
     * 
     * @param loggerTraceList
     * lista donde se almacenarán los warnings o errores que llegan al logger durante proceso 
     * 
     * @return
     * tabla unión de items y records
     * 
     * @throws XBRLPGCException
     * en caso de producirse algún error
     * 
     */
    public static final Hashtable<IXbrlFact, ConceptMap> unionXbrlFacts(ConfigurationManager configM, Item[] items, Record[] records, 
    		Hashtable<String,Item> itemsTable, Hashtable<String, Record>recordsTable,String reportID, 
    		String moduleID, ArrayList<String> loggerTraceList) throws XBRLPGCException
    {
    	return unionXbrlFacts(configM,items,records,itemsTable,recordsTable,reportID,moduleID,loggerTraceList,null);
    }
    
    
    
    
    
    
    
    
    
    
    /**
     * Dado un item y un info, crea un elemento XbrlTuple si isTuple es cierto
     * y un XbrlFact si es falso.
     * 
     * @param info
     * conceptMap con la configuración
     * 
     * @param item
     * item con la información para el IXbrlFact
     * 
     * @param isTuple
     * indica si es una tupla
     * 
     * @return
     * IXbrlFact creado
     * 
     */
    public static final IXbrlFact createFact(ConceptMap info, Item item, boolean isTuple){
        
            IXbrlFact fact = null;
        
            if (isTuple)
            {
        	fact = XbrlApiFactory.createTuple();
            }
            else
            {
        	fact = XbrlApiFactory.createFact();
            }
            String outputID =info.getOutputID(); 
            if (outputID.indexOf(":") > 0)
            {
        	outputID = outputID.substring(outputID.lastIndexOf(":")+1);
            }
            
            fact.setConcept(outputID);
            fact.setPrefix(info.getNsPrefix());
            fact.setNamespace(info.getNs());
            	    
            if (item != null)
            {
        	ItemSignType sign = item.getSign();
        	if (sign != null)
        	{
        	    fact.setSign(item.getSign().toString());
        	}
        	fact.setValue(item.getValue());
            }
            
            if (info.getOrder() != null)
            {
        	fact.setOrder(info.getOrder().intValue());
            }
            
            return fact;
    }
    
    /**
     * Comprueba si el identificador de un item dado está en el mismo módulo que el identificador de
     * módulo moduleID.
     * <br /><br />
     * En caso de que el identificador del módulo es diferente se eleva una excepción
     * indicando el problema.
     * <br /><br />
     * Esta comprobación es necesaria para determinar si en un contexto XBRL vienen elementos que
     * pertenecen a diferentes módulos, lo cual no está permitido en PGC2007.
     * 
     * @param itemID
     * identificador del item
     * 
     * @param reportID
     * identificador del report
     * 
     * @param moduleID
     * identificador del módulo
     * 
     * @param configM
     * objeto para acceder a la configuración
     * 
     * @param dimension
     * identificador de la dimension
     * 
     * @param member
     * identificador del member
     * 
     * @param logger
     * objeto de trazas.
     * 
     * @param factsInBadContext
     * variable acumulador de los facts erroneos o en un contexto equivocado
     * 
	 * @param loggerTraceList
     * lista de warnings o errores generada durante el proceso
     * 
     * @return
     * Indicador de si el item está en el module
     * 
     * @throws XBRLPGCException
     * eleva un error indicando si el identificador no coincide
     * 
     */
    public static final boolean checkItemInModule(String itemID, String reportID, String moduleID, ConfigurationManager configM, 
    		String dimension, String member,Logger logger, StringBuffer factsInBadContext,ArrayList<String> loggerTraceList) throws XBRLPGCException
    {
	boolean result = true;
	String newModuleID = configM.getModuleID(reportID, itemID, dimension, member);
	
	if ((newModuleID != null) &&(!newModuleID.equals(moduleID)))
	{
	    String trace = "itemID="+ itemID + " dimension=" + dimension + " member= " + member + " instanceModule=" + moduleID  + " foundModule=" + newModuleID + " reportID=" + reportID;
	    trace +=". You can check: it is not advisable to associate elements of different modules at the same context";
	    
	    //se considera un warning, no un error, para que sean admitidas instancias con varios modulos asociados al mismo context
	    logger.warn(trace);
	    if (!(loggerTraceList==null) ){loggerTraceList.add("WARNING: " + trace);}
	    factsInBadContext.append(trace);
	    factsInBadContext.append(";");
	    result = false;
	}
	return result;
    }
    
    
    
    
    
    
    /**
     * Comprueba si el identificador de un item dado está en el mismo módulo que el identificador de
     * módulo moduleID.
     * <br /><br />
     * En caso de que el identificador del módulo es diferente se eleva una excepción
     * indicando el problema.
     * <br /><br />
     * Esta comprobación es necesaria para determinar si en un contexto XBRL vienen elementos que
     * pertenecen a diferentes módulos, lo cual no está permitido en PGC2007.
     * 
     * @param itemID
     * identificador del item
     * 
     * @param reportID
     * identificador del report
     * 
     * @param moduleID
     * identificador del módulo
     * 
     * @param configM
     * objeto para acceder a la configuración
     * 
     * @param dimension
     * identificador de la dimension
     * 
     * @param member
     * identificador del member
     * 
     * @param logger
     * objeto de trazas.
     * 
     * @param factsInBadContext
     * variable acumulador de los facts erroneos o en un contexto equivocado
     * 
     * @return
     * Indicador de si el item está en el module
     * 
     * @throws XBRLPGCException
     * eleva un error indicando si el identificador no coincide
     * 
     */
    public static final boolean checkItemInModule(String itemID, String reportID, String moduleID, ConfigurationManager configM, 
    		String dimension, String member,Logger logger, StringBuffer factsInBadContext) throws XBRLPGCException
    {
		return checkItemInModule(itemID,reportID,moduleID,configM,dimension,member,logger,factsInBadContext,null);
    }    
    
    
    
    
    
    

    /**
     * Obtiene el identificador que para un contexto XBRL.  Este identificador se compone mediante
     * la concatenación de un prefijo dado, el año de reporte, el identificador del módulo que
     * se reporta en dicho contexto y el tipo de periodo.
     * <br /><br />
     * Un ejemplo de identificador de contexto sería: Y1_2008_patnetB_Instant, lo que quiere decir
     * que es un contexto para reportar facts del 2008, del módulo patrimonio neto B y el periodo
     * es de tipo instante.
     * 
     * @param prefix
     * prefijo que se pondrá al identificador
     * 
     * @param moduleID
     * identificador del módulo
     * 
     * @param reportingDate
     * fecha de reporting del contexto para el cual se genera el ID
     * 
     * @param periodType
     * tipo de periodo
     * 
     * @return
     * identificador
     * 
     * @throws XBRLPGCException
     * error producido
     * 
     */
    public static final String getContextID(String prefix,String moduleID, Date reportingDate, String periodType) throws XBRLPGCException
    {
        int century = reportingDate.getCentury() * 100;
        int year =century +  reportingDate.getYear();
        return prefix + "_" + year + "_" + moduleID + "_" + periodType;
    }

    /**
     * Obtiene el identificador que para un contexto dimensional XBRL.  Este identificador se compone 
     * mediante la concatenación de un prefijo dado, el año de reporte, el identificador del módulo que
     * se reporta en dicho contexto y el tipo de periodo y el nombre de la dimensión.
     * <br /><br />
     * Un ejemplo de identificador de contexto sería: Y1_2008_patnetB_Instant_dimension, lo que quiere decir
     * que es un contexto para reportar facts del 2008, del módulo patrimonio neto B y el periodo
     * es de tipo instante.
     * 
     * @param prefix
     * prefijo que se pondrá al identificador
     * 
     * @param moduleID
     * identificador del módulo
     * 
     * @param reportingDate
     * fecha de reporting del contexto para el cual se genera el ID
     * 
     * @param periodType
     * tipo de periodo
     * 
     * @param dimensionValue
     * valor de la dimensión a la que pertenece el contexto
     * 
     * @return
     * identificador
     * 
     * @throws XBRLPGCException
     * error producido
     * 
     */
    public static final String getContextID(String prefix,String moduleID, Date reportingDate, String periodType, String dimensionValue, String dimension) throws XBRLPGCException
    {
        String dimensionValueWithoutPrefix = dimensionValue.substring(dimensionValue.indexOf(":")+1);
        String dimensionWithoutPrefix = dimension.substring(dimension.indexOf(":")+1);
        return getContextID("Y1", moduleID, reportingDate, periodType) + "_" + dimensionValueWithoutPrefix + "_" + dimensionWithoutPrefix;
    }

    /**
     * Obtiene un miembro, buscándolo en la lista de de Members de un conceptMap dimensional,
     * mediante el identificador del member.
     * 
     * @param conceptMap
     * referencia al conceptMap donde se busca el member
     * 
     * @param index
     * identificador del member
     * 
     * @return
     * member, si es encontrado o nulo en caso contrario
     * 
     */
    public static final Member getMemberByIndex(ConceptMap conceptMap, String index)
    {
        
        Member[] members = conceptMap.getMember();
        int numMembers = members.length;
        for (int i=0; i<numMembers; i++)
        {
            if ( ( members[i].getId() !=null)  &&   members[i].getId().equals(index)   ){
        	return members[i];
            }
        }
        return null;
    }
    
    /**
     * Obtiene un miembro, buscándolo en la lista de de Members de un conceptMap dimensional
     * mediante el nombre el member.
     * 
     * @param conceptMap
     * referencia al conceptMap donde se busca el member
     * 
     * @param memberName
     * nombre del member
     * 
     * @return
     * member, si es encontrado o nulo en caso contrario
     * 
     */
    public static final Member getMemberByName(ConceptMap conceptMap, String memberName)
    {
	Member memberResponse = null;

	Member[] memberList = conceptMap.getMember();
	int numMembers = memberList.length;
	for (int i = 0; i < numMembers; i++)
	{
	    Member currentMember = memberList[i];
	    if (currentMember.getQname().equals(memberName))
	    {
		memberResponse = currentMember;
		break;
	    }
	}

	return memberResponse;
    }

        
    /**
     * Dada una tabla de objetos encapsuladores de contextos IXbrlContext, este método los clasifica en otras
     * dos tablas de salida según su tipo de periodo (duration o instant).
     * 
     * @param contextTable
     * tabla de contextos de entrada
     * 
     * @param durationContexts
     * tabla de contextos duration de salida
     * 
     * @param instandContexts
     * tabla de contextos instant de salida
     * 
     */
    public static final void classifyContexts(Hashtable<String, IXbrlContext> contextTable,Hashtable<String, IXbrlContext>  durationContexts, Hashtable<String, IXbrlContext> instandContexts )
    {
    
        Enumeration<IXbrlContext> enumContexts = contextTable.elements();
        while(enumContexts.hasMoreElements())
        {
            IXbrlContext currentContext = enumContexts.nextElement();
            if (currentContext.getPeriodInstant() != null)
            {
        	instandContexts.put(currentContext.getId(),currentContext);
            }
            else
            {
        	durationContexts.put(currentContext.getId(),currentContext);
            }
        }
    }
    
    
    /**
     * Ordena la tabla de rows de un report por su index.  Este método es usado
     * previamente a la serialización del report para devolver un XML con los
     * rows ordenados.
     * 
     * @param report
     * report sobre el cual se realizará el orden
     * 
     * 
     */
    public static final void sortTableRows(Report report)
    {
	int numModules = report.getModuleCount();
	for (int i=0; i<numModules; i++)
	{
	    Module currentModule = report.getModule(i);
	    int numTables = currentModule.getTableCount();
	    for (int j=0; j<numTables; j++)
	    {
		Table currentTable = currentModule.getTable(j);
		List<Row> rowsList = Arrays.asList(currentTable.getRow());
		Collections.sort(rowsList,new RowsComparator());
		currentTable.setRow(rowsList.toArray(new Row[]{}));
	    }
	}
    }
    
}
