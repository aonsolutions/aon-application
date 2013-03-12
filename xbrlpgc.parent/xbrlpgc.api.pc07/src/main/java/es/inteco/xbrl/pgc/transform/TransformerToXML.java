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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;


import org.apache.log4j.Logger;

import es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade;
import es.inteco.xbrl.pgc.apifacade.IXbrlContext;
import es.inteco.xbrl.pgc.apifacade.IXbrlFact;
import es.inteco.xbrl.pgc.apifacade.IXbrlInstance;
import es.inteco.xbrl.pgc.apifacade.impl.XbrlApiFactory;
import es.inteco.xbrl.pgc.apifacade.impl.XbrlFact;
import es.inteco.xbrl.pgc.errors.exceptions.TransformException;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLPGCException;
import es.inteco.xbrl.pgc.transform.config.ConfigReport;
import es.inteco.xbrl.pgc.transform.format.Entity;
import es.inteco.xbrl.pgc.transform.format.Item;
import es.inteco.xbrl.pgc.transform.format.Module;
import es.inteco.xbrl.pgc.transform.format.Note;
import es.inteco.xbrl.pgc.transform.format.Record;
import es.inteco.xbrl.pgc.transform.format.Report;
import es.inteco.xbrl.pgc.transform.format.Row;
import es.inteco.xbrl.pgc.transform.format.Table;
import es.inteco.xbrl.pgc.transform.format.types.ItemSignType;
import es.inteco.xbrl.pgc.transform.format.types.Unittype;
import es.inteco.xbrl.pgc.transform.maps.ConceptMap;
import es.inteco.xbrl.pgc.transform.maps.Member;
import es.inteco.xbrl.pgc.utils.PGCUtils;
import es.inteco.xbrl.pgc.utils.UtilConstants;

/**
*
*
* Clase utilizada por PGCTransformer para realizar la transformación desde un documento
* XBRL a formato común (XML).
* <br /><br />
* Esta clase solo es accesible a nivel de paquete, y su uso queda delimintado al proceso
* interno de transformación.
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

class TransformerToXML
{

    static Logger logger = Logger.getLogger(TransformerToXML.class);

    /**
     * Referencia a la configuración de mapas de PGC2007.
     */
    private ConfigurationManager configM = null;
    /**
     * Referencia a la facade de la API XBRL utilizada para la implementación de los servicios PGC2007.
     */
    private IXbrlApiFacade apiFacade = null;
    /**
     * Refencia a la instancia XBRL que se transformará a formato común.
     */
    private IXbrlInstance instance = null;
    /**
     * Objeto report del formato común que se irá creando con la información leída en la instancia XBRL.
     */
    private Report report = null;
    /**
     * Tabla de contextos
     */
    private Hashtable<String, IXbrlContext> contextTable = null;

    /**
     * Tabla de módulos, nos permite llevar el control de los módulos creados, clasificándolos por el
     * identificador y periodos que abarca.
     */
    private ModuleTable moduleTable = new ModuleTable();
    
    private StringBuffer factsInBadContext = null;
    
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
	 * Constructor
	 * @throws XBRLPGCException
	 */
	private TransformerToXML() throws XBRLPGCException {
	configM = ConfigurationManager.getInstance();
	apiFacade = XbrlApiFactory.get_instance();
    }
    


    /**
     * Dado un fact y su conceptMap, se crea un elemento Item del formato común.
     * 
     * Si el identificador del contexto incluye la palabra "inicio" o "fin" quiere decir que el item
     * es un item instant dentro de un periodo duration, con lo cual, se deberá añadir el atributo
     * "reportingDate" con la fecha instant del contexto incluido, ya que este tipo de identificadores
     * solo tienen sentido en contextos de tipo instant.
     * 
     * @param currentFact
     * fact que encapsula la información leída de la instancia XBRL
     * 
     * @param conceptMap
     * referencia a información de configuración de mapas
     * 
     * @return
     * referencia al item creado
     * 
     * @throws XBRLPGCException
     * en caso de producirse algún error en la creación del item
     * 
     */
    private Item createItem(IXbrlFact currentFact, ConceptMap conceptMap) throws XBRLPGCException
    {
	Item item = new Item();

	item.setId(conceptMap.getInputID());
	item.setDecimals(currentFact.getDecimals());

	if (currentFact.getSign() != null)
	{
	    item.setSign(ItemSignType.fromValue(currentFact.getSign()));
	}
	if ((currentFact.getUnit() != null) && (currentFact.getUnit().getKey() != null))
	{
	    String unitKey = currentFact.getUnit().getKey();
	    Unittype unit = Unittype.fromValue(unitKey);
	    item.setUnit(unit);
	}
	item.setValue(currentFact.getValue());

	ArrayList<String> footNotes = currentFact.getFootNotes();

	if (footNotes != null)
	{
	    int numFootNotes = footNotes.size();

	    for (int i = 0; i < numFootNotes; i++)
	    {
		Note newNote = new Note();

		newNote.setId(i);
		newNote.setText(footNotes.get(i));

		item.addNote(newNote);
	    }
	}

	if ((currentFact.getContext().getId().indexOf("inicio") > 0) || (currentFact.getContext().getId().indexOf("fin") >0))
	{
	    item.setReportingDate(new org.exolab.castor.types.Date(currentFact.getContext().getPeriodInstant()));
	}

	return item;
    }


    
    /**
     * Método utilizado internamente para la creación de una lista de items, con la posibilidad de indicar
     * un padre y un path. El padre y el path son necesarios cuando se están crando items contenidos en tuplas
     * ya que el path es obligatorio para poder acceder al conceptMap correcto.
     * 
     * Por cada uno de los elementos de la lista, obtiene el concepto de la configuración
     * de mapas PGC2007.  Una vez obtenido el concepto, se crea un nuevo item y se obtiene el módulo 
     * en el cual debe ser incluido dicho item.
     * Los módulos que se van creando, en función del periodo y el identificador, se van manteniendo
     * en la tabla "moduleTable".
     * Al obtener el módulo, se hace una carga de los items ya incluidos en dicho módulo, el cual puede
     * ser nuevo o haber sido creado previamente.
     * La carga de los items se hace para determinar si el item recien creado se se repite o no.
     * Si el item se repite y sus valores son diferentes, se elevará un error de items repetidos, en otro
     * caso se indicarán como warnings en las trazas del servicio.
     * Finalmente, si no se produce ningún error, el item será añadido al módulo previamente obtenido o
     * al padre si no es nulo (caso en el cual se invocaría desde la creación de un registro, correspondiente
     * a una tupla XBRL)
     * 
     * @param parent
     * registro padre, para el caso de registro(tuplas) anidadas
     * 
     * @param factList
     * lista de facts que representan a las tuplas
     * 
     * @param sbPath
     * concatenación de identificadores correspondientes al camino de anidamiento completo
     * 
     * @return
     * module creado
     * 
     * @throws XBRLPGCException
     */
    private Module createItems(Record parent, ArrayList<IXbrlFact> factList, String sbPath) throws XBRLPGCException
    {
    	Module module = null;

    	Hashtable<String, Item> itemTable = new Hashtable<String, Item>(); 
    	StringBuffer duplicateItemErrors = new StringBuffer();

    	Iterator<IXbrlFact> factEnum = factList.iterator();
    	while (factEnum.hasNext())
    	{

    		IXbrlFact currentFact = factEnum.next();
    		ConceptMap conceptMap = configM.getInfoByOutputID(report.getId(), null, sbPath + currentFact.getConcept());

    		if (conceptMap == null)
    		{
    			logger.warn("Not exist ConceptMap for outputID = " + sbPath.toString() + currentFact.getConcept() + " and report " + report.getId());

    			if (!(loggerTraceList==null)){loggerTraceList.add("WARNING: Not exist ConceptMap for outputID = " + sbPath.toString() + currentFact.getConcept() + " and report " + report.getId());}

    			continue;
    		}

    		Item item = createItem(currentFact, conceptMap);
    		boolean itemInModule = true;
    		
    		if (module != null)	{
    			itemInModule = TransformerHelper.checkItemInModule(currentFact.getConcept(), report.getId(), 
    					module.getId(), configM, null,null,logger,factsInBadContext);
    		}
    		
   		  	//si no se ha obtenido módulo o bien el modulo encontrado no se corresponde con el item, buscamos módulo
    		if (module == null || !itemInModule)
    		{
    			module = getModule(configM.getModuleID(report.getId(), conceptMap.getOutputID(),null, null), currentFact.getContext(), report);
    			if (parent == null)
    			{
    				TransformerHelper.loadItems(module.getItem(), itemTable);
    			}
    			else
    			{
    				TransformerHelper.loadItems(parent.getItem(), itemTable);
    			}
    		}
    		else
    		{
    			itemInModule = TransformerHelper.checkItemInModule(currentFact.getConcept(), report.getId(), module.getId(), configM, null,null,logger, factsInBadContext);
    		}

    		if (itemInModule)
    		{
    			if (item.getDecimals() != null)
    			{
    				module.setBaseDecimals(item.getDecimals());
    			}
    			if (item.getUnit() != null)
    			{
    				module.setBaseUnit(item.getUnit());
    			}
    		}

    		TransformerHelper.checkDuplicateItems(itemTable, duplicateItemErrors,  item,logger,loggerTraceList);

    		if (parent == null)
    		{
    			module.addItem(item);
    		} else
    		{
    			parent.addItem(item);
    		}
    	}

    	//Si se han detectado errores de items duplicados, se eleva una excepción
    	if (duplicateItemErrors.length() > 0)
    	{
    		throw new TransformException(TransformException.duplicateIemsError,new String[]{duplicateItemErrors.toString()});
    	}

    	return module;
    }


    /**
     * Se crean registros correspondientes a la lista de facts pasada por parámetro. Dicha lista de facts
     * corresponde a tuplas XBRL.
     * 
     * @param parent
     * registro padre, para el caso de registro(tuplas) anidadas
     * 
     * @param factList
     * lista de facts que representan a las tuplas
     * 
     * @param sbPath
     * concatenación de identificadores correspondientes al camino de anidamiento completo
     * 
     * @return
     * módulo en el cual se han añadido los nuevos registros
     * 
     * @throws XBRLPGCException
     * 
     */
    private Module createRecords(Record parent, ArrayList<IXbrlFact> factList, String sbPath) throws XBRLPGCException
    {

	Module module = null;

	Iterator<IXbrlFact> factEnum = factList.iterator();
	int index = 0;
	while (factEnum.hasNext())
	{
	    Record newRecord = new Record();
	    if (parent != null)
	    {
		parent.addRecord(newRecord);
	    }

	    IXbrlFact tuple = factEnum.next();
	    
	    ConceptMap conceptMap = configM.getInfoByOutputID(report.getId(), null, sbPath.toString() + tuple.getConcept());

	    if (conceptMap == null)
	    {
		logger.warn("Not exist ConceptMap for outputID = " +sbPath.toString() + tuple.getConcept() + " and report " + report.getId());
		if (!(loggerTraceList==null)){loggerTraceList.add("WARNING: Not exist ConceptMap for outputID = " +sbPath.toString() + tuple.getConcept() + " and report " + report.getId());}		
		
		continue;
	    }

	    newRecord.setId(conceptMap.getInputID());
  
	    //Una vez creado el nuevo registro, se crean los items contenidos en la tupla actual, si es el caso
	    ArrayList<IXbrlFact> globalFactList = apiFacade.getGlobalFacts(instance, tuple, contextTable);
	    if (globalFactList.size() > 0)
	    {
		
		module = createItems(newRecord, globalFactList, sbPath + newRecord.getId()+ ":");
	    }

	    //Se crean los registros de forma recursiva, en el caso de que la tupla contenga tuplas anidadas
	    ArrayList<IXbrlFact> globalTupleList = apiFacade.getGlobalTuples(instance, tuple, contextTable);
	    if (globalTupleList.size() > 0)
	    {
		module = createRecords(newRecord, globalTupleList, sbPath + newRecord.getId()+ ":");
	    }

	    index++;
	    if ((module != null) && (parent == null))
	    {
		module.addRecord(newRecord);
	    }
	}
	
	return module;

    }


    /**
     * Crea las tablas correspondientes a un contexto dimensional.
     * 
     * @param dimensionalContext
     * referencia al contexto dimensional
     * 
     * @throws XBRLPGCException
     */
    private void createTables(IXbrlContext dimensionalContext) throws XBRLPGCException
    {
	//Se obtienen los facts asociados al contexto dimensional
	ArrayList<IXbrlFact> globalFactList = apiFacade.getGlobalFacts(instance, dimensionalContext, contextTable);

	Iterator<IXbrlFact> factEnum = globalFactList.iterator();

	//Con el primer fact, se obtiene su conceptMap para determinar los miembros, el resto de facts contendrán
	//los mismos miembros
	IXbrlFact currentFact = factEnum.next();

	ConceptMap conceptMap = null;

	conceptMap = configM.getInfoByOutputID(report.getId(), null, currentFact.getConcept(), dimensionalContext.getDimension(), dimensionalContext.getMember());

	if (conceptMap == null)
	{
	    logger.warn("Not exist ConceptMap for outputID = " + currentFact.getConcept() + " and report " + report.getId() + " for dimension =" + dimensionalContext.getDimension() + " and member=" + dimensionalContext.getMember() );
	    
		if (!(loggerTraceList==null)){loggerTraceList.add("WARNING: Not exist ConceptMap for outputID = " + currentFact.getConcept() + " and report " + report.getId() + " for dimension =" + dimensionalContext.getDimension() + " and member=" + dimensionalContext.getMember());}
	    return;
	}

	Member currentMember = TransformerHelper.getMemberByName(conceptMap, dimensionalContext.getMember());

	if (currentMember == null)
	{
	    logger.warn("Not exist Member = "+ dimensionalContext.getMember()  +  " for conceptMap = " + currentFact.getConcept() + " and report " + report.getId());
	    
		if (!(loggerTraceList==null)){loggerTraceList.add("WARNING: Not exist Member = "+ dimensionalContext.getMember()  +  " for conceptMap = " + currentFact.getConcept() + " and report " + report.getId());}
	    return;
	}
	if (currentMember.getId() == null)
	{
	    logger.warn("Member without 'id' member= "+ dimensionalContext.getMember()  +  " for conceptMap = " + currentFact.getConcept() + " and report " + report.getId());
	    if (!(loggerTraceList==null)){loggerTraceList.add("WARNING: Member without 'id' member= "+ dimensionalContext.getMember()  +  " for conceptMap = " + currentFact.getConcept() + " and report " + report.getId());}
	    return;
	    
	}

	//Se localiza la tabla, la cual puede existir de otro contexto, procesado previamente
	Table dimensionTable = getTable(dimensionalContext.getDimension(), configM.getModuleID(report.getId(), conceptMap.getOutputID(), dimensionalContext.getDimension(),dimensionalContext.getMember()), dimensionalContext);
	
	//Se obtiene la row, en caso de no existir se crea nueva
	Row domainMemberRow = TransformerHelper.getRow(dimensionTable, currentMember.getId());
	if (domainMemberRow == null)
	{
	    domainMemberRow = new Row();
	    dimensionTable.addRow(domainMemberRow);
	    domainMemberRow.setId(currentMember.getId());	    
	}

	Module module = null;

	//A partir de aquí, todos los items se crearán sobre la row y table localizadas previamente.
	do
	{
	    if (conceptMap == null)
	    {
		logger.warn("Not exist ConceptMap for outputID = " + currentFact.getConcept() + " and report " + report.getId() + " for dimension =" + dimensionalContext.getDimension() + " and member=" + dimensionalContext.getMember() );
		if (!(loggerTraceList==null)){loggerTraceList.add("WARNING: Not exist ConceptMap for outputID = " + currentFact.getConcept() + " and report " + report.getId() + " for dimension =" + dimensionalContext.getDimension() + " and member=" + dimensionalContext.getMember());}
	    }
	    else
	    {
		Item item = createItem(currentFact, conceptMap);
		
		boolean itemInModule = true;
		
		if (module == null)
		{
		    module = getModule(configM.getModuleID(report.getId(), conceptMap.getOutputID(),dimensionalContext.getDimension(), dimensionalContext.getMember()), dimensionalContext, report);
		}
		else
		{
		    itemInModule = TransformerHelper.checkItemInModule(currentFact.getConcept(), report.getId(), module.getId(), configM, dimensionalContext.getDimension(), dimensionalContext.getMember(), logger, factsInBadContext);
		}

		if (itemInModule)
		{    
		    if (item.getDecimals() != null)
		    {
			module.setBaseDecimals(item.getDecimals());
		    }
		    if (item.getUnit() != null)
		    {
			module.setBaseUnit(item.getUnit());
		    }

		    domainMemberRow.addItem(item);
		}
	    }
	    if (factEnum.hasNext())
	    {
		currentFact = factEnum.next();
		conceptMap = configM.getInfoByOutputID(report.getId(), null, currentFact.getConcept(),dimensionalContext.getDimension(), dimensionalContext.getMember());
	    } else
	    {
		currentFact = null;
	    }

	} while (currentFact != null);

    }



    /**
     * Localiza la tabla en la cual se guardarán los elementos.  Dicha tabla se clasifica según el periodo
     * y la dimensión.
     * 
     * @param dimension
     * @param moduleID
     * @param factContext
     * @return
     * @throws XBRLPGCException
     */
    private Table getTable(String dimension, String moduleID, IXbrlContext factContext) throws XBRLPGCException
    {
	Table tableResp = null;

	Module module = getModule(moduleID, factContext, report);
	Period period = moduleTable.searchPeriod(moduleID, factContext);
	
	tableResp = period.getTable(dimension);
	
	if (tableResp == null)
	{
	    tableResp = new Table();
	    tableResp.setId(dimension);
	    period.addTable(dimension, tableResp);
	    module.addTable(tableResp);
	}

	return tableResp;
    }
    
    
    
    /*procesa un contexto para acceder a los datos de la entidad y darles valor*/
    private Entity processContexts(Hashtable<String,IXbrlContext> contextTable,Entity commonFormatEntity) throws XBRLPGCException
    {
	Enumeration<IXbrlContext> enumContexts = contextTable.elements();
	commonFormatEntity = null;
	for (; enumContexts.hasMoreElements();)
	{
	    IXbrlContext context = enumContexts.nextElement();

	    if (commonFormatEntity == null)
	    {
		commonFormatEntity = new Entity();
		commonFormatEntity.setId(context.getEntityIdentifierValue());
		commonFormatEntity.setUri(context.getEntityIdentifierSchema());
		report.setEntity(commonFormatEntity);
	    }

	    if (!context.isScenarioIsDimensional())
	    {
		ArrayList<IXbrlFact> globalFactList = apiFacade.getGlobalFacts(instance, context, contextTable);
		createItems(null,globalFactList,"");
	    } else
	    {
		createTables(context);
	    }
	}
	return commonFormatEntity;
    }
    


    /*Realiza la transformación en local*/
    private void localTransform(String inputPath) throws XBRLPGCException
    {
	String instanceSchemaRef = null;
	
	factsInBadContext = new StringBuffer();
	
	moduleTable.initialize();
	
	instance = apiFacade.loadInstance(inputPath);

	instanceSchemaRef = apiFacade.getXBRLschemaRef(instance);
	ConfigReport globalReportRelated = configM.getGlobalReportFromSchemaRef(instanceSchemaRef);

	report = new Report();
	report.setId(globalReportRelated.getId());
	report.setDate(PGCUtils.getCurrentDate());

	contextTable = apiFacade.getContexts(instance);

	Hashtable<String, IXbrlContext> durationContexts = new Hashtable<String, IXbrlContext>();
	Hashtable<String, IXbrlContext> instandContexts = new Hashtable<String, IXbrlContext>();
	
	TransformerHelper.classifyContexts(contextTable, durationContexts, instandContexts);
	
	//Es importante procesar primero los duration para que al procesar los instant puedan encajar en los módulos duration
	Entity entity = processContexts(durationContexts,null);
	processContexts(instandContexts, entity);

	TransformerHelper.sortTableRows(report);
	
	ArrayList<IXbrlFact> globalTupleList = apiFacade.getGlobalTuples(instance, contextTable);
	createRecords(null, globalTupleList, "");
	
	apiFacade.closeInstance(instance);
	
	if (factsInBadContext.length() > 0)
	{	//no se considerará una excepción; así admitiremos instancias XBRL con varios modulos en un mismo contexto
	    //throw new TransformException(TransformException.errorItemInBadModule, new String[]{factsInBadContext.toString(),"",report.getId()});
	}

    }


    
    /*Realiza la transformacion a XML*/
    void transform(String inputPath, String outputPath) throws XBRLPGCException
    {
	try
	{
	    localTransform(inputPath);
	    TransformerHelper.marshalReport(outputPath,report);
	} catch (Exception e)
	{
	    throw new TransformException(TransformException.errorInTransformationToXml, new String[]{inputPath},e);
	}
    }

    /*Realiza la transformacion a XML*/
    byte[] transform(String inputPath) throws XBRLPGCException
    {


	byte[] xmlResult = null;
	try
	{
	    ByteArrayOutputStream osResult = new ByteArrayOutputStream();
	    OutputStreamWriter osWriterResult = new OutputStreamWriter(osResult,UtilConstants.ENCODING);
	    
	    localTransform(inputPath);
	    TransformerHelper.marshalReport(osWriterResult,report);
	    
	    xmlResult = osResult.toByteArray();

	}
	catch (TransformException ex)
	{
	    throw ex;
	}
	catch (XBRLPGCException exp)
	{
	    throw exp;
	}
	catch (Exception e)
	{
	    throw new TransformException(TransformException.errorInTransformationToXml, new String[]{inputPath},e);
	}

	return xmlResult;

    }



    /*Crea un module dentro de un contexto*/
    private Module createModule(IXbrlContext factContext, String moduleID)
    {
	Module moduleResp = new Module();

	moduleResp.setId(moduleID);

	if (factContext.getPeriodInstant() == null )
	{
	    moduleResp.setReportingDateStart(new org.exolab.castor.types.Date(factContext.getPeriodStartDate()));
	    moduleResp.setReportingDateEnd(new org.exolab.castor.types.Date(factContext.getPeriodEndDate()));

	} else
	{
	    moduleResp.setReportingDateStart(new org.exolab.castor.types.Date(factContext.getPeriodInstant()));
	    moduleResp.setReportingDateEnd(new org.exolab.castor.types.Date(factContext.getPeriodInstant()));
	}

	return moduleResp;
    }

    /*Genera un module con un id, un contexto y el report padre*/
    private Module getModule(String moduleID, IXbrlContext factContext, Report parentReport) throws XBRLPGCException
    {
	Module moduleResp = null;
	try
	{
	    Period period = moduleTable.searchPeriod(moduleID, factContext);
	    if (period != null)
	    {
		moduleResp = period.getModule();
		period.upgradePeriod(factContext);

	    } else
	    {
		moduleResp = createModule(factContext, moduleID);
		parentReport.addModule(moduleResp);
		period = moduleTable.addPeriod(moduleID, factContext);
		period.setModule(moduleResp);
	    }

	} catch (Exception e)
	{
	    throw new TransformException(TransformException.errorGettingModule, new String[]{moduleID, parentReport.getId()},e);
	}
	return moduleResp;
    }


    
    /**
     * Crea una instancia de la propia clase
     * @return
     * nueva instancia de TransformerToXML
     * @throws XBRLPGCException
     */
    static TransformerToXML createInstance() throws XBRLPGCException
    {

	return new TransformerToXML();
    }

    


}
