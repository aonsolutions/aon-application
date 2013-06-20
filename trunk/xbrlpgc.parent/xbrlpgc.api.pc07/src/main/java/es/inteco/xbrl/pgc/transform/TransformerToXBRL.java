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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.exolab.castor.types.Date;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade;
import es.inteco.xbrl.pgc.apifacade.IXbrlContext;
import es.inteco.xbrl.pgc.apifacade.IXbrlFact;
import es.inteco.xbrl.pgc.apifacade.IXbrlInstance;
import es.inteco.xbrl.pgc.apifacade.IXbrlUnit;
import es.inteco.xbrl.pgc.apifacade.impl.XbrlApiFactory;
import es.inteco.xbrl.pgc.errors.exceptions.DocumentNotFoundException;
import es.inteco.xbrl.pgc.errors.exceptions.TransformException;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLPGCException;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLValidatorException;
import es.inteco.xbrl.pgc.errors.exceptions.XSDValidatorException;
import es.inteco.xbrl.pgc.transform.config.NsItem;
import es.inteco.xbrl.pgc.transform.format.Item;
import es.inteco.xbrl.pgc.transform.format.Module;
import es.inteco.xbrl.pgc.transform.format.Note;
import es.inteco.xbrl.pgc.transform.format.Record;
import es.inteco.xbrl.pgc.transform.format.Report;
import es.inteco.xbrl.pgc.transform.format.Row;
import es.inteco.xbrl.pgc.transform.format.Table;
import es.inteco.xbrl.pgc.transform.format.types.Unittype;
import es.inteco.xbrl.pgc.transform.maps.ConceptMap;
import es.inteco.xbrl.pgc.transform.maps.Member;
import es.inteco.xbrl.pgc.utils.NotesComparator;
import es.inteco.xbrl.pgc.utils.PGCUtils;
import es.inteco.xbrl.pgc.utils.UtilConstants;
import es.inteco.xbrl.pgc.utils.XbrlFactComparator;

import es.inteco.xbrl.pgc.utils.RowsComparator;
import es.inteco.xbrl.pgc.utils.XbrlApiConfiguration;
import es.inteco.xbrl.pgc.validator.PGCXBRLValidator;
import es.inteco.xbrl.pgc.validator.ValidateResult;




/**
*
*
* Clase encargada de realizar la transformación de XML a XBRL.  Es una clase de ámbito paquete, 
* su funcionalidad es publicada mediante la clase PGCTransformer.
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


class TransformerToXBRL
{
    private static final Logger logger = Logger.getLogger(TransformerToXBRL.class);

    /**
     * Objeto utilizado para acceder a la configuación necesaria para realizar el proceso de transformación,
     * mapas, global, etc.
     */
    private ConfigurationManager configM = null;
    /**
     * Objeto que expone la funcionalidad de la API XBRL en la que se basa el proceso de transformación.
     */
    private IXbrlApiFacade apiFacade = null;
    /**
     * Objeto report que contendrá la instancia del XML de entrada el cual se transformará a XBRL.
     */
    private Report report = null;
    /**
     * Instancia XBRL sobre la cual se irán añadiendo todos los elementos encontrados en el objeto report.
     */
    private IXbrlInstance instance = null;

    
    /**
     * Tabla global de unidades.  Se utiliza como almacenamiento temporal de las unidades creadas durante
     * el proceso de transformación, de modo que se evite la creación duplicada de unidades en la instancia
     * XBRL.
     */
    private HashMap<String, IXbrlUnit> units = new HashMap<String, IXbrlUnit>();

    
    
    /**
     * Lista donde serán almacenados los warnings o errores que llegan al logger
     */
    private ArrayList<String> loggerTraceList=null;

    
	/**
	 * Establece el arraylist destinado al almacenar los warnings o errores que llegan al logger producidos durante la transformación
	 * @param loggerList
	 * ArrayList de String
	 */
	protected void setLoggerTraceList(ArrayList<String> loggerList) {
		this.loggerTraceList = loggerList;
		try{
			apiFacade.setLoggerTraceList(loggerList);
		}
		catch(Exception e){}
	}
    
    
    /**
     * Constructor de la clase, en el cual se inicializa la configuración y la facade.
     * 
     * @throws XBRLPGCException
     * Eleva el error que se haya podido producir durante el proceso
     * 
     */
    private TransformerToXBRL() throws XBRLPGCException {
	configM = ConfigurationManager.getInstance();
	apiFacade = XbrlApiFactory.get_instance();
    }



    /*Devuelve el id de la Entity de un report*/
    private String getIdentity(){
	return report.getEntity().getId();
    }

    /*Devuelve el URI de la entity de un report*/
    private String getIdentityUri(){
	return report.getEntity().getUri();
    }


    /**
     * Método encargado de deserializar el report indicado en el path que se pasa como 
     * parámetro.
     * 
     * @param inputPath
     * path en el cual se encuentra el report a transformar
     * 
     * @return
     * instancia del report deserializado
     * 
     * @throws XBRLPGCException
     * si hay algún error durante la deserialziación
     * 
     */
    private Report loadReport(String inputPath) throws XBRLPGCException
    {

	Report inputReport = null;

	File inputFile = new File(inputPath);

	try
	{
	    inputReport = Report.unmarshal(new InputStreamReader(new FileInputStream(inputFile), UtilConstants.ENCODING));
	    
	} catch (MarshalException e)
	{
	    logger.error(e.getMessage());
	    throw new XBRLPGCException(XBRLPGCException.errorInReportFileDeserialization, new String[]{inputPath}, e);

	} catch (ValidationException e)
	{
	    logger.error(e.getMessage());
	    throw new XSDValidatorException(XSDValidatorException.errorInValidationXmlInput, new String[]{inputPath},e );

	} catch (FileNotFoundException e)
	{
	    logger.error(e.getMessage());
	    throw new DocumentNotFoundException(DocumentNotFoundException.notExistReportFile, new String[]{inputPath});
	} catch (UnsupportedEncodingException e)
	{
	    logger.error(e.getMessage());
	    throw new XBRLPGCException(XBRLPGCException.unsuportedEncoding, new String[]{inputPath}, e);
	}

	return inputReport;

    }

    
    
    /**
     * Las unidades, a medida que se van requiriendo son solicitadas a través de este 
     * método, ya que es el encargado de mantener una tabla de unidades creadas durante
     * el proceso de transformación, permitiendo así su reutilización.
     * 
     * @param unitKey
     * identificar de las unidad solicitada
     * 
     * @return
     * devuelve un objeto encapsulador de unidad
     * 
     * @throws XBRLPGCException
     * en caso de producirse algún error
     * 
     */
    private IXbrlUnit getUnit(String unitKey) throws XBRLPGCException
    {
	IXbrlUnit unit = units.get(unitKey);

	if (unit == null){

	    // Get information necessary for create unit element from configuration
	    String unitCode = configM.getUnit(unitKey, report.getId());
	    if (unitCode == null)
	    {
		throw new TransformException(TransformException.notFoudUnitCode, new String[]{unitKey});
	    }
	    String unitPrefix = configM.getUnitPrefix(unitKey, report.getId());

	    unit = XbrlApiFactory.createUnit();
	    unit.setCode(unitCode);
	    unit.setPrefix(unitPrefix);
	    unit.setKey(unitKey);

	    // Create the unit element in the XBRL instance
	    unit = apiFacade.createUnit(unit, instance);
	    units.put(unitKey, unit);
	}

	return unit;

    }


    /**
     * Crea un objeto intermedio para encapsular el valor de un contexto XBRL y la instancia
     * una vez haya sido procesada por la facade.
     * <br /><br />
     * Este método comprueba si las fechas de reporte inicio y fin son las mismas y el periodo
     * indicado es duration, en cuyo caso, el contexto no es creado, ya que no tendría sentido
     * crear un contexto con esta configuración.
     * <br /><br />
     * Si el tipo de perido es INSTANT, entonces sólo se le da valor al periodo instante, en 
     * caso contrario, dicho periodo se dejaría nulo y se asignaría perido end y period start.
     * 
     * @param periodType
     * tipo de periodo
     * 
     * @param reportingDateStart
     * fecha inicial de reporte del contexto
     * 
     * @param reportingDateEnd
     * fecha fin de reporte del contexto
     * 
     * @param contextID
     * identificador del contexto
     * 
     * @return
     * objeto encapsulador del contexto
     * 
     * @throws XBRLPGCException
     * 
     */
    private IXbrlContext createContext(String periodType, Date reportingDateStart, Date reportingDateEnd, String contextID) throws XBRLPGCException
    {

	IXbrlContext context= null;
	boolean equalsDates = (reportingDateEnd.toDate().compareTo(reportingDateStart.toDate())==0);
	if (!((periodType.equals(UtilConstants.DURATION)) && (equalsDates)))
	{

	    context = XbrlApiFactory.createContext();
	    context.setId(contextID);
	    context.setEntityIdentifierSchema(getIdentityUri());
	    context.setEntityIdentifierValue(getIdentity());

	    if (periodType.equals(UtilConstants.INSTANT))
	    {
		context.setPeriodInstant(reportingDateEnd.toDate());    
	    }
	    else
	    {
		context.setPeriodEndDate(reportingDateEnd.toDate());
		context.setPeriodStartDate(reportingDateStart.toDate());
	    }
	}
	
	return context;
    }
    
    
    
    /**
     * Crea un objeto encapsulador decontexto con periodo de tipo instant para una fecha dada.
     * 
     * @param reportingDate
     * fecha de reporting
     * 
     * @param contextID
     * identificador del contexto
     * 
     * @return
     * contexto creado
     * @throws XBRLPGCException
     */
    private IXbrlContext createContext(Date reportingDate, String contextID) throws XBRLPGCException
    {
	IXbrlContext context = null;

	context = XbrlApiFactory.createContext();
	context.setId(contextID);
	context.setEntityIdentifierSchema(getIdentityUri());
	context.setEntityIdentifierValue(getIdentity());
	
	context.setPeriodInstant(reportingDate.toDate());
	context = apiFacade.createContext(context, instance);
	apiFacade.createInstantPeriod(context,instance);

	return context;
    }
    
    
    
    /**
     * Se proceso un módulo pasado como argumento.  Además se pasa una tabla con los contextos
     * en la cual se van referenciando los contextos que se van creando.
     * <br /><br />
     * Antes de crear un contexto se comprueba si el contexto ya existe, en cuyo caso se reutiliza.
     * 
     * @param module
     * referencia al módulo
     * 
     * @param tempContextTable
     * tabla con los contextos
     * 
     * @throws XBRLPGCException
     * en caso de que se produzca algún error
     * 
     */
    private void processModule(Module module, Hashtable<String,IXbrlContext> tempContextTable) throws XBRLPGCException
    {
	IXbrlContext instantContext = null;
	IXbrlContext durationContext = null;

	// En primer lugar se obtiene la unidad para el módulo, la cual viene dada en el atributo baseunit
	IXbrlUnit unit = null;
	//string no se creará como unidad en el XBRL	
	if (!(Unittype.STRING.value().equalsIgnoreCase(module.getBaseUnit().value()) )){
			unit = getUnit(module.getBaseUnit().value());
	}
	
	// Se crean dos contextos uno duration y otro instant, aunque primero se comprueba si existen ya en la tabla de contextos
	// para ello, en primer lugar se generan los identificadores en función del identificador del módulo, y la fecha de reporte.
	String instantContextKey = TransformerHelper.getContextID("Y1", module.getId(), module.getReportingDateEnd(), UtilConstants.INSTANT);
	
	if (tempContextTable.containsKey(instantContextKey)){
	    instantContext = tempContextTable.get(instantContextKey);
	}
	else
	{
	    instantContext = createContext(UtilConstants.INSTANT, module.getReportingDateStart(),module.getReportingDateEnd(), instantContextKey);
	    instantContext = apiFacade.createContext(instantContext, instance);
	    apiFacade.createInstantPeriod(instantContext,instance);
	    tempContextTable.put(instantContextKey, instantContext);
	}

	String durationContextKey = TransformerHelper.getContextID("Y1", module.getId(), module.getReportingDateEnd(), UtilConstants.DURATION);
	
	if (tempContextTable.containsKey(durationContextKey))
	{
	    durationContext = tempContextTable.get(durationContextKey);
	}
	else
	{
	    durationContext = createContext(UtilConstants.DURATION, module.getReportingDateStart(),module.getReportingDateEnd(),durationContextKey);
	    //Puede ser nulo, en el caso de que las fechas inicio y fin sean iguales
	    if (durationContext != null)
	    {
		durationContext = apiFacade.createContext(durationContext, instance);
		apiFacade.createDurationPeriod(durationContext, instance);
		tempContextTable.put(durationContextKey, durationContext);
	    }
	}
	
	// Procesa los records incluidods en el módulo, creando tuples en la instancia XBRL
	if (module.getRecordCount() > 0)
	{
	    Hashtable<String,Item> itemsTable = new Hashtable<String,Item>();
	    Hashtable<String,Record> recordsTable = new Hashtable<String,Record>();


	    //En primer lugar se unen los items (la primera vez tabla vacía) y los records para poder ordenarlos y procesarlos en orden
	    Hashtable<IXbrlFact,ConceptMap> factsTable = TransformerHelper.unionXbrlFacts(configM, new Item[]{}, 
		    module.getRecord(), itemsTable, recordsTable, report.getId(), module.getId(),loggerTraceList);


	    processSortedRecords(instantContext, durationContext, null, factsTable, itemsTable, recordsTable, module.getId(), unit, module.getBaseDecimals(), tempContextTable);

	}

	// Se procesan las tablas incluidas en el módulo, creando los facts y contextos dimensionales sobre la instancia XBRL
	processTables(module);

	// Se procesan los items incluidos en el módulo creando los facts equivalentes en la instancia XBRL
	processItems(null, instantContext, durationContext, module.getId(),unit,module.getBaseDecimals(), module.getItem(),tempContextTable);

    }

    
    
    /**
     * Procesa un fact(item) dado, creando el elemento equivalente en la instancia XBRL.  Para ello se realizan
     * varias comprobaciones:
     * <br />
     * <ul>
     * <li>
     * 	Las unidades se sobrescriben a las indicadas en el módulo al que pertenece dicho item, en caso de que el item 
     * 	  sea informado con dicha unidad.
     * </li>
     * <li>
     * 	Igual que las unidades, ocurre con los decimales.
     * </li>
     * <li>
     *  Se genera el namespace al cual pertenece dicho item, en caso de que exista.
     * </li>
     * <li>
     *  Si el item contiene una fecha de reporte se creará un contexto especial del tipo instante con la fecha indicada.
     *    Esto se utiliza para indicar que un item que se encuentra dentro de un modulo de tipo duration, se está reportando
     *    sólo para el instante inicio o fin del periodo indicado en el módulo.
     * </li>
     * <li>
     * 	Si no existe ninguna fecha de reporte en el item, se utilizará el contexto instant o duration en función de si
     * 	  si el concepto al que pertenece dicho item es de un tipo o de otro.
     * </li>
     * <li>
     *  Si el atributo parent no es nulo, quiere decir que el item es hijo de una tupla, por lo tanto se creará como tal
     *    en caso contrario se creará como un fact de la instancia.
     * </li>
     *</ul>
     *    
     * @param parent
     * referencia a la tupla padre, en caso de que de a lugar
     * 
     * @param instantContext
     * referencia al contexto instante, sobre el cual se generarán los items de tipo instant
     * 
     * @param durationContext
     * referencia al contexto duration, sobre el cual se generarán los items de tipo duration
     * 
     * @param moduleID
     * identificador del módulo al que pertenece el item
     * 
     * @param unit
     * unidad del módulo, y que se sobrescribirá con lo que se informe en el item
     * 
     * @param baseDecimals
     * decimales del módulo, y que se sobrescribirán con lo que se indique en el item
     * 
     * @param item
     * item que se quiere generar como fact en la instancia XBRL
     * 
     * @param fact
     * elemento IXbrlFact que encapsula la información para intercambio con la facade
     * 
     * @param itemInfo
     * información obtenida de los mapas de configuración para el item (ConceptMap)
     * 
     * @param tempContextTable
     * tabla temporal de contextos en la cual se guardarán aquellos que se creen durante el proceso
     * 
     * @throws XBRLPGCException
     */
    private void processItem(IXbrlFact parent, IXbrlContext instantContext,IXbrlContext durationContext, String moduleID, IXbrlUnit unit, String baseDecimals, Item item, IXbrlFact fact, ConceptMap itemInfo,Hashtable<String,IXbrlContext> tempContextTable) throws XBRLPGCException
    {
	//Item unit override module unit
	Unittype unitType = item.getUnit();
	if (unitType != null){
	    String itemUnitKey = item.getUnit().value();
	    unit = getUnit(itemUnitKey);
	}

	//Item decimals override module decimals
	String itemDecimals = item.getDecimals();
	if ((itemDecimals != null ) && (!itemDecimals.trim().isEmpty())){
	    baseDecimals = itemDecimals;
	}	    

	NsItem nsItem = new NsItem();
	nsItem.setPrefix(itemInfo.getNsPrefix());
	nsItem.setUri(itemInfo.getNs());

	apiFacade.createNamespaces(new NsItem[]{nsItem}, instance);

	fact.setDecimals(baseDecimals);
	fact.setUnit(unit);
	IXbrlContext context = apiFacade.isInstant(fact, instance) ? instantContext : durationContext;

	if (context == null)
	{
	    //throw new XBRLPGCException("itemDurationInModuleInstant", new String[]{});
	    logger.warn("Item " + fact.getConcept() + " of duration type in a instant module");
	    if (!(loggerTraceList==null)){loggerTraceList.add("WARNING: Item " + fact.getConcept() + " of duration type in a instant module");}
	    context = instantContext;
	}

	if (item.getReportingDate()!= null)
	{
	    String periodType = item.getReportingDate().toDate().equals(durationContext.getPeriodStartDate()) ? UtilConstants.INSTANT_INI : UtilConstants.INSTANT_FIN;
	    String contextID = TransformerHelper.getContextID("Y" , moduleID, item.getReportingDate(), periodType);
	    if (tempContextTable.containsKey(contextID))
	    {
		context = tempContextTable.get(contextID);
	    }
	    else
	    {
		context = createContext(item.getReportingDate(), contextID);
		tempContextTable.put(context.getId(), context);
	    }
	}

	fact.setContext(context);

	if (parent == null)
	{
	    fact = apiFacade.createFact(fact, instance);

	} else
	{
	    fact = apiFacade.createTupleChild(fact, parent, instance);
	}

	if (item.getNote() != null) {

	    processNotes(fact, item.getNote());
	}

    }
    
    
    /**
     * Este método se encarga de procesar una tabla de IXbrlFact compuesta por la unión de 
     * records (tuples) e items(facts).
     * <br /><br />
     * La unión de ambos permite establecer un orden en función del atributo order obtenido 
     * desde la información del mapa.
     * <br /><br />
     * Va recorriendo la estructura antes mencionada y si se encuentra un record, genera una tupla
     * y si dicho record contiene hijos, realiza una llamada recursiva a sí mismo con la información
     * contenida en éste.
     * <br /><br />
     * Si el elemento encontrado es un item, se genera un elemento fact sobre la instancia.
     * 
     * @param instantContext
     * contexto para elementos del tipo instant
     * 
     * @param durationContext
     * contexto para elementos del tipo duration
     * 
     * @param parent
     * tupla padre o nulo si no existe
     * 
     * @param factsTable
     * tabla de IXbrlFact's con la unión de recors e items.
     * 
     * @param itemsTable
     * tabla de items indexada por inputID, optimiza la localización de los elementos durante el proceso
     * 
     * @param recordsTable
     * tabla de records indexada por inputID, optimiza la localización de los elementos durante el proceso
     * 
     * @param moduleID
     * identificador del módulo
     * 
     * @param unit
     * unidad
     * 
     * @param baseDecimals
     * decimales
     * 
     * @param tempContextTable
     * tabla de contextos
     * 
     * @throws XBRLPGCException
     * 
     */
    private void processSortedRecords(IXbrlContext instantContext,
	    			      IXbrlContext durationContext, 
	    			      IXbrlFact parent, 
	    			      Hashtable<IXbrlFact, 
	    			      ConceptMap> factsTable, 
	    			      Hashtable<String,
	    			      Item>itemsTable,
	    			      Hashtable<String,Record> recordsTable,
	    			      String moduleID, 
	    			      IXbrlUnit unit, 
	    			      String baseDecimals,
	    			      Hashtable<String,IXbrlContext> tempContextTable
	    			      ) throws XBRLPGCException
    {
	IXbrlFact[] arrayFacts = factsTable.keySet().toArray(new IXbrlFact[]{});
	List<IXbrlFact> factsList = Arrays.asList(arrayFacts);
	Collections.sort(factsList, new XbrlFactComparator());

	
	Iterator<IXbrlFact> factsListIter = factsList.iterator();
	IXbrlFact currentFact = null;
	while (factsListIter.hasNext())
	{
	    currentFact = factsListIter.next();
	    ConceptMap elementInfo = factsTable.get(currentFact);
	    if (currentFact.isTuple())
	    {
		IXbrlFact nextParent = createTuple(parent, elementInfo);

		
		Record currentRecord = recordsTable.get(elementInfo.getInputID());
		if ((currentRecord.getItemCount()> 0) || (currentRecord.getRecordCount()>0))
		{
			//mod mar09. el codigo de record (tupla padre) se utilizará en el metodo "unionXbrlFacts"
			String parentOut=null;
			if(!(nextParent==null)){
				parentOut=elementInfo.getInputID();
			}				
	
		    Hashtable<IXbrlFact,ConceptMap> childFactsTable = TransformerHelper.unionXbrlFacts(configM, currentRecord.getItem(), currentRecord.getRecord(), 
		    		itemsTable, recordsTable, report.getId(), moduleID, loggerTraceList, parentOut );
		    
		    processSortedRecords(instantContext, durationContext, nextParent, childFactsTable, itemsTable, recordsTable, moduleID, unit, baseDecimals,tempContextTable);
		}
	    }
	    else
	    {
		Item currentItem = itemsTable.get(elementInfo.getInputID());
		processItem(parent, instantContext, durationContext, moduleID, unit, baseDecimals, currentItem, currentFact, elementInfo,tempContextTable);
	    }
	}
    }


    /**
     * Crea una tupla dado el conceptMap con la información necesaria y el padre si existe.
     * 
     * @param parent
     * padre en caso de existir
     * 
     * @param elementInfo
     * información para la tupla obtenida del mapa de configuración
     * 
     * @return
     * IXbrlFact que encapsula la nueva tupla
     * 
     * @throws XBRLPGCException
     * en caso de producirse algún error
     * 
     */
    private IXbrlFact createTuple(IXbrlFact parent, ConceptMap elementInfo) throws XBRLPGCException
    {
	NsItem nsItem = new NsItem();
	nsItem.setPrefix(elementInfo.getNsPrefix());
	nsItem.setUri(elementInfo.getNs());

	apiFacade.createNamespaces(new NsItem[]{nsItem}, instance);

	IXbrlFact tuple = TransformerHelper.createFact(elementInfo,null,true);
	if (parent == null)
	{
	    tuple = apiFacade.createTuple(tuple, instance);
	}
	else
	{
	    tuple = apiFacade.createTupleChildTuple(tuple, parent, instance);
	}
	return tuple;
    }
    
   
    /*procesa las tablas de un module*/
    private void processTables(Module module) throws XBRLPGCException
    {
	Table[] tables = module.getTable();
	int numTables = tables.length;
	Hashtable<String,IXbrlContext> tempContextTable = new Hashtable<String,IXbrlContext>();
	for (int i = 0; i < numTables; i++)
	{
	    Table table = tables[i];

	    processRows(table.getRow(), table.getId(), module, tempContextTable);
	}
    }
    
    /*procesa una lista de objetos Row*/
    private void processRows(Row rows[], String tableID, Module module, Hashtable<String, IXbrlContext>tempContextTable) throws XBRLPGCException
    {
	
	
	int numRows = rows.length;

	List<Row> rowsList = Arrays.asList(rows);
	Collections.sort(rowsList,new RowsComparator());

	for (int i = 0; i < numRows; i++)
	{
	    Row row = rowsList.get(i);

	    Item item = row.getItem(0);
	    ConceptMap itemInfo = configM.getInfoByInputID(report.getId(), module.getId(), item.getId());
	    if (itemInfo == null)
	    {
		logger.warn("Not exists the conceptMap " + item.getId() + " in the map " + module.getId());
	    if (!(loggerTraceList==null)){loggerTraceList.add("WARNING: Not exists the conceptMap " + item.getId() + " in the map " + module.getId());}
		continue;
	    }	    

	    Member domainMember = TransformerHelper.getMemberByIndex(itemInfo, row.getId());
	    if (domainMember == null)
	    {
		throw new XBRLPGCException(XBRLPGCException.notExistDomainMemberForModule,new String[]{Integer.toString(i) ,module.getId()});
	    }
	    
	    String dimensionValue = domainMember.getQname();

	    String dimension = itemInfo.getDomain();

	    IXbrlContext instantContext = null;
	    IXbrlContext durationContext = null;

	    String instantContextKey = TransformerHelper.getContextID("Y1", module.getId(),module.getReportingDateEnd(), UtilConstants.INSTANT, dimensionValue, dimension);

	    if (tempContextTable.containsKey(instantContextKey))
	    {
		instantContext = tempContextTable.get(instantContextKey);
	    }
	    else
	    {
		instantContext = createContext(UtilConstants.INSTANT, module.getReportingDateStart(),module.getReportingDateEnd(), instantContextKey);
		instantContext = apiFacade.createContext(instantContext, instance);
		apiFacade.createInstantPeriod(instantContext,instance);
		apiFacade.createDimension(dimension, dimensionValue, instantContext, instance);
		tempContextTable.put(instantContext.getId(),instantContext);
	    }

	    String durationContextKey = TransformerHelper.getContextID("Y1",  module.getId(),module.getReportingDateEnd(), UtilConstants.DURATION, dimensionValue, dimension);
	    if (tempContextTable.containsKey(durationContextKey))
	    {
		durationContext = tempContextTable.get(durationContextKey);
	    }
	    else
	    {
		durationContext = createContext(UtilConstants.DURATION,module.getReportingDateStart(),module.getReportingDateEnd(), durationContextKey);
	    
		if (durationContext != null)
		{
		    durationContext = apiFacade.createContext(durationContext, instance);
		    apiFacade.createDurationPeriod(durationContext, instance);
		    apiFacade.createDimension(dimension, dimensionValue, durationContext, instance);
		    tempContextTable.put(durationContext.getId(),durationContext);
		}
	    }

	    IXbrlUnit unit = getUnit(module.getBaseUnit().value());
	    processItems(null, instantContext, durationContext, module.getId(), unit, module.getBaseDecimals(), row.getItem(),null);
	}
    }

    
    /*recorre una colección de items trabajando con ella*/
    private void processItems(IXbrlFact parent, IXbrlContext instantContext,IXbrlContext durationContext, String moduleID, IXbrlUnit unit, String baseDecimals, Item items[], Hashtable<String,IXbrlContext> tempContextTable) throws XBRLPGCException
    {

	int numItem = items.length;
	Hashtable<String, Item> itemTable = new Hashtable<String, Item>(); 
	StringBuffer duplicateItemErrors = new StringBuffer();
	
	for (int i = 0; i < numItem; i++)
	{
	    Item item = items[i];
	    
	    TransformerHelper.checkDuplicateItems(itemTable, duplicateItemErrors, item, logger,loggerTraceList);
	    
	    IXbrlFact fact = null;

	    ConceptMap itemInfo = configM.getInfoByInputID(report.getId(),moduleID, item.getId());

	    if (itemInfo == null)
	    {
		logger.warn("Not exists the conceptMap " + item.getId() + " in the map " + moduleID);
	    if (!(loggerTraceList==null)){loggerTraceList.add("WARNING: Not exists the conceptMap " + item.getId() + " in the map " + moduleID);}
		continue;
	    }
	    
	    fact = TransformerHelper.createFact(itemInfo,item, false);	    
	    
	    processItem(parent, instantContext, durationContext, moduleID, unit, baseDecimals, item, fact, itemInfo, tempContextTable);
	}

	if (duplicateItemErrors.length() > 0)
	{
	    throw new TransformException(TransformException.duplicateIemsError,new String[]{duplicateItemErrors.toString()});
	}
    }


    /*Crea los comentarios para el fact*/
    private void processNotes(IXbrlFact fact, Note notes[]) throws XBRLPGCException{

	int numNote = notes.length;

	List<Note> notesList = Arrays.asList(notes);
	Collections.sort(notesList,new NotesComparator());


	//esta variable sirve de indicador. Necesitamos saber si es la segunda (o siguientes) vez
	//que se ejecuta la creación de footNotes sobre el mismo fact porque, en este caso,
	//se reutilizará el locator del primer footNote (no se creará uno nuevo para cada "Note")
	boolean locatorExists=false;
	
	for (int i=0; i< numNote; i++){

	    Note note = notesList.get(i);

	    if (instance.getFootNoteLink() == null){
		instance.setFootNoteLink(apiFacade.createFootNoteLink(instance));
	    }
	    apiFacade.createFootNote(instance.getFootNoteLink(), fact, "es", note.getText(), String.valueOf(new Long(note.getId()).intValue()) ,instance, locatorExists);
	    locatorExists=true;
	}
	
	
	
    }


    /*Genera el XBRL*/
    private void generateXbrl() throws XBRLPGCException
    {
	units.clear();

	//Generate basic namespaces
	NsItem basicNS[] = configM.getBasicNamespaces(report.getId());
	apiFacade.createNamespaces(basicNS, instance);

	Hashtable<String,IXbrlContext> tempContextTable = new Hashtable<String,IXbrlContext>();
	
	//Generate modules included in Report
	Module modules[] = report.getModule();
	int numModules = report.getModuleCount();
	
	
	for (int i = 0; i < numModules; i++)
	{
	    processModule(modules[i], tempContextTable);
	}

    }

    
    /*valida el XBRL*/
    void validateXBRL(String documentPath) throws XBRLPGCException{
	
	ValidateResult validateResult = PGCXBRLValidator.createInstance().validate(documentPath);
	if (!validateResult.isValid())
	{
	    throw new XBRLValidatorException(validateResult.getErrors(), new String[]{});
	}
	if(logger.isInfoEnabled()){
	    logger.info("Successfully validated document at " + documentPath);
	}
    }
    
    

    /*Realiza la transformación*/
    byte[]  transform(String inputPath, String outputPath) throws XBRLPGCException
    {
	byte[] xbrlResult = null;
	try
	{
	    //Load Report from XML instance
	    report = loadReport(inputPath);

	    //Create XBRL instance
	    instance = apiFacade.createInstance(outputPath,configM.getTargetNamespace(report.getId()), configM.getShemaRefUri(report.getId()));

	    //Get XBRL instance from Report information
	    generateXbrl();

	    xbrlResult = apiFacade.generateXbrlByteArray(instance);

	    ByteArrayOutputStream outputStreamXBRL = new ByteArrayOutputStream();

	    String normalizedSchemaRef = configM.getBaseUrl(report.getId()) + "/" + configM.getShemaRefUri(report.getId());
	    PGCUtils.normalizeSchema(new ByteArrayInputStream(xbrlResult), XbrlApiConfiguration.getInstance().getTaxonomyRoot(), normalizedSchemaRef, outputStreamXBRL);

	    xbrlResult = outputStreamXBRL.toByteArray();
	    
	    FileOutputStream outputStremFile = new FileOutputStream(outputPath);
	    outputStremFile.write(xbrlResult);
	    
	} catch (TransformException ex)
	{
	    throw ex;
	} catch (XBRLPGCException exp)
	{
	    throw exp;
	}
	catch (Exception e)
	{
	   logger.error(e);
	   throw new TransformException(TransformException.errorInTransformationToXbrl,new String[]{});
	} 
	
	return xbrlResult;

    }


    /*Realiza la tranformación*/
    byte[] transform(String inputPath, boolean validateXbrl) throws XBRLPGCException{

	File instanceFile= null;
	byte[] xbrlResult = null;
	String outputPath = null;
	
	

	try
	{
	    instanceFile = PGCUtils.createTempFile("transformToXbrl", ".xbrl");
	    
	    outputPath = instanceFile.getAbsolutePath();
	    
	    xbrlResult = transform(inputPath, outputPath);
	    
	    
	} catch (XBRLPGCException e)
	{
	    throw e;
	}
	finally
	{
	    try
	    {
		apiFacade.closeInstance(instance);
	    } catch (Exception e)
	    {
		logger.warn(e);
		if (!(loggerTraceList==null)){loggerTraceList.add("WARNING: " + e.toString());}
	    }
	    
	    if ((instanceFile!=null) && instanceFile.exists())
	    {
		instanceFile.delete();
	    }	    
	}

	return xbrlResult;
    }


    
    /**
     * Crea una instancia de la propia clase
     * @return 
     * Objeto del tipo TransformerToXBRL
     * @throws XBRLPGCException
     */
    static TransformerToXBRL createInstance() throws XBRLPGCException{

	return new TransformerToXBRL();
    }

}
