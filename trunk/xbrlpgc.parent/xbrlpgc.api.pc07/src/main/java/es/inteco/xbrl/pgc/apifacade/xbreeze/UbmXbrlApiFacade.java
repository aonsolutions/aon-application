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


package es.inteco.xbrl.pgc.apifacade.xbreeze;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xerces.dom.DeferredElementNSImpl;

import ubmatrix.xbrl.common.exception.src.CoreException;
import ubmatrix.xbrl.common.formatter.src.FormatterException;
import ubmatrix.xbrl.common.memo.src.FlyweightErrorMemo;
import ubmatrix.xbrl.common.memo.src.FlyweightFatalErrorMemo;
import ubmatrix.xbrl.common.memo.src.IMemo;
import ubmatrix.xbrl.common.src.Configuration;
import ubmatrix.xbrl.common.src.IDTS;
import ubmatrix.xbrl.common.src.IDTSNode;
import ubmatrix.xbrl.common.src.IDTSResultSet;
import ubmatrix.xbrl.common.src.ILocationHandle;
import ubmatrix.xbrl.common.src.IXbrlDomain;
import ubmatrix.xbrl.common.src.LocationHandleType;
import ubmatrix.xbrl.common.utility.src.CommonUtilities;
import ubmatrix.xbrl.domain.query.src.IDTSQuery;
import ubmatrix.xbrl.domain.relationship.src.ArcRelationship;
import ubmatrix.xbrl.domain.src.ValuedXbrlInstanceDomainImpl;
import ubmatrix.xbrl.domain.src.ValuedXbrlSchemaDomainImpl;
import ubmatrix.xbrl.domain.xbrl21Domain.behavior.src.InstanceBehavior;
import ubmatrix.xbrl.domain.xbrl21Domain.dts.src.DTSNode;
import ubmatrix.xbrl.domain.xbrl21Domain.dts.src.DTSPath;
import ubmatrix.xbrl.domain.xbrl21Domain.dts.src.Hypercube;
import ubmatrix.xbrl.domain.xbrl21Domain.dts.src.HypercubeHelper;
import ubmatrix.xbrl.domain.xbrl21Domain.dts.src.ParsedQuery;
import ubmatrix.xbrl.domain.xbrl21Domain.src.Xbrl21DomainHelper;
import ubmatrix.xbrl.domain.xbrl21Domain.src.XbrlDomainUri;
import ubmatrix.xbrl.domain.xbrl21Domain.taxonomy.src.Context;
import ubmatrix.xbrl.domain.xbrl21Domain.taxonomy.src.Role;
import ubmatrix.xbrl.extensions.extensionFunctionServices.src.ExtensionFunctionArgs;
import ubmatrix.xbrl.extensions.ubmExtensions.src.UBMExtensions_Taxonomy;
import ubmatrix.xbrl.locationController.xbrlLocationController.locationHandle.src.FileLocationHandle;
import ubmatrix.xbrl.src.Xbrl;
import es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade;
import es.inteco.xbrl.pgc.apifacade.IXbrlContext;
import es.inteco.xbrl.pgc.apifacade.IXbrlFact;
import es.inteco.xbrl.pgc.apifacade.IXbrlInstance;
import es.inteco.xbrl.pgc.apifacade.IXbrlUnit;
import es.inteco.xbrl.pgc.apifacade.impl.XbrlApiFactory;
import es.inteco.xbrl.pgc.errors.GenericErrorsHandler;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLPGCException;
import es.inteco.xbrl.pgc.transform.TransformerHelper;
import es.inteco.xbrl.pgc.transform.config.NsItem;
import es.inteco.xbrl.pgc.transform.maps.ConceptMap;
import es.inteco.xbrl.pgc.transform.maps.Member;
import es.inteco.xbrl.pgc.transform.maps.Statement;
import es.inteco.xbrl.pgc.utils.PGCUtils;
import es.inteco.xbrl.pgc.utils.XbrlApiConfiguration;




/**
 *
 *
 * Clase que implementa las funcionalidades necesarias y que darán soporte a los servicios
 * de PGC.  
 * 
 * Esta clase implementa el interfaz IXbrlApiFacade, el cual será usado a lo largo de todo
 * el código que constituye los servicios de PGC.
 * 
 * En concreto, estas funcionalidades hacen uso de la API Open Source para el tratamiento de XBRL
 * XBreeze de UBMarix.
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
 * @version 1.1, 18/02/2009
 * @author difusioncalidad@inteco.es
 *
 */


public class UbmXbrlApiFacade implements IXbrlApiFacade
{
    static Logger logger = Logger.getLogger(UbmXbrlApiFacade.class);

    private static DTSPath c_measureFromUnitQuery = new DTSPath("/+'" + XbrlDomainUri.c_SpecifiesMeasure + "'" + "/'" + XbrlDomainUri.c_Measure + "'");
    private static DTSPath c_contextFromEntity = new DTSPath("/-'" + XbrlDomainUri.c_ContextSpecifiesEntity + "'" + "/'" + XbrlDomainUri.c_Context + "'");

    private static String s_definitionArcsFromConceptQuery = CommonUtilities.formatString("/+'{0}'", new Object[] {XbrlDomainUri.c_DefinitionArc});

    private static IDTSQuery s_definitionArcsFromConceptPath = 	new DTSPath(s_definitionArcsFromConceptQuery);   
	
    
    /**
     * Se almacenará el último locator utilizado para crear el footNote de un fact.
     * El fin de almacenarlo es su reutilización. Cuando un fact tiene asociados más de un comentario,
     * se le asigna el mismo locator a todos ellos
     */
    private IXbrlDomain lastFootnoteLocator=null;
    
    /**
     * Lista donde serán almacenados los warnings o errores que llegan al logger
     */
    private ArrayList<String> loggerTraceList=null;

    

	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#setLoggerTraceList(java.util.ArrayList)
	 */
	public void setLoggerTraceList(ArrayList<String> loggerList) {
		this.loggerTraceList = loggerList;
	}
	
	
    
    /**
     * Constructor por defecto
     */
    public UbmXbrlApiFacade() {
    	super();
    }

    
    /**
     * Inicializa la facade, inicializando el procesador de XBreeze. Al inicializar XBreeze, carga
     * en memoria las tablas con todas las configuraciones necesarias para que la API pueda resolver las
     * dependencias no locales.
     */
    private static void initialize()
    {
	try
	{
	    Xbrl xbrl = Xbrl.getInstance();
	    xbrl.Initialize();
	    Configuration configUBMatrix = ubmatrix.xbrl.common.src.Configuration.getInstance();

	    UbmTaxonomyConfiguration.addTaxonomyConfiguration(configUBMatrix);

	} catch (Exception e)
	{
	    logger.error(e);
	}
    }
    
    
    static
    {
	initialize();
    }


    
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#generateXbrlDocument(es.inteco.xbrl.pgc.apifacade.IXbrlInstance, java.lang.String)
     */
    public void generateXbrlDocument(IXbrlInstance instance, String resultPath) throws XBRLPGCException
    {
	ILocationHandle newLH = null;
	IDTSNode dtsNode = null;
	try
	{

	    dtsNode = ((IDTS) instance.getDts()).getEntryNode();

	    newLH = new FileLocationHandle(resultPath);
	    dtsNode.write(newLH);
	    dtsNode.unlock((IDTS) instance.getDts());

	} 
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.failedWriteXbrlDocument, new String[] { dtsNode.getLocationHandle().getPhysicalUri(),
		    newLH.getPhysicalUri() }, e);
	}
    }

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#generateXbrlByteArray(es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public byte[] generateXbrlByteArray(IXbrlInstance instance) throws XBRLPGCException
    {
	try
	{
	    return ((IXbrlDomain) instance.getInstance()).toXml().getBytes("UTF-8");
	}
	catch (UnsupportedEncodingException e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.unsuportedEncodingInstanceByte, e);

	} catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.failedGettingInstanceByte, e);
	}

    }



    /**
     * Prefijos necesarios para realizar las consultas XPath.
     * 
     * @return Tabla con equivalencias entre prefijos y uris.
     * @throws XBRLPGCException
     */
    private HashMap<String, String> getPrefixMap() throws XBRLPGCException
    {

	HashMap<String, String> prefixMap = new HashMap<String, String>();
	try
	{
	    if (prefixMap == null)
	    {
		prefixMap.put("xbrli", "http://www.xbrl.org/2003/instance");
		prefixMap.put("ci", "http://www.UBmatrix.com/Patterns/BasicCalculation");
		prefixMap.put("xs", "http://www.w3.org/2001/XMLSchema");
		prefixMap.put("xbrldi", "http://xbrl.org/2006/xbrldi");
	    }
	} 
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorGettingXPathPrefixMaps, e);
	}
	return prefixMap;
    }

    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#createNamespaces(es.inteco.xbrl.pgc.transform.config.NsItem[], es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public void createNamespaces(NsItem[] namespacesList, IXbrlInstance instance) throws XBRLPGCException
    {
	try
	{
	    IXbrlDomain instanceDO = (IXbrlDomain) instance.getInstance();
	    IDTS dts = (IDTS) instance.getDts();

	    // Estos namespaces son temporal para pruebas
	    UbmXbrlHelper.invokeDO(dts, instanceDO, "AddNamespaceDeclaration", new Object[] { "http://www.xbrl.org/2003/instance", "xbrli" });
	    UbmXbrlHelper.invokeDO(dts, instanceDO, "AddNamespaceDeclaration", new Object[] { "http://www.w3.org/1999/xhtml", "xhtml" });

	    int numNsItems = namespacesList.length;
	    for (int i = 0; i < numNsItems; i++)
	    {
		NsItem nsIem = namespacesList[i];
		UbmXbrlHelper.invokeDO(dts, instanceDO, "AddNamespaceDeclaration", new Object[] { nsIem.getUri(), nsIem.getPrefix() });
	    }

	} 
	catch (CoreException e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.coreErrorCreatingNamespaces, e);
	} catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingNamespaces, e);
	}

    }
    

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#createInstance(java.lang.String, java.lang.String, java.lang.String)
     */
    public IXbrlInstance createInstance(String documentPath, String namespaceUri, String schema) throws XBRLPGCException
    {

	IDTS dts = null;
	UbmXbrlMessages messages = new UbmXbrlMessages();
	IXbrlDomain instanceDO = null;

	try
	{

	    File instanceDocumentFile = new File(documentPath);
	    String instanceDocumentUri = null;
	    
	    try{
	    	instanceDocumentUri = instanceDocumentFile.getAbsolutePath();
	    } catch (NullPointerException  npe){
	    	logger.debug("Catching error NullPointerException line instanceDocumentFile.getAbsolutePath(). See UbmXbrlApiFacade - CreateInstance.");
			logger.error("File " + documentPath + ". Can not read the path. During the creation of instance",npe);
			throw new XBRLPGCException( XBRLPGCException.fileNotFound, new String[] { 
					documentPath, "During the creation of instance, reading path" }, npe);	    	
	    }
	    
	    
	    //validación previa
	    if ( XbrlApiConfiguration.getInstance()==null || XbrlApiConfiguration.getInstance().getTaxonomyRoot()==null  ){
	    	
	    	if(XbrlApiConfiguration.getInstance()!=null){
		    	logger.debug("Error found. See UbmXbrlApiFacade - CreateInstance. During the creation of a XBRL, application try get an instance of XbrlApiConfiguration. Process fails. XbrlApiConfiguration.getInstance()_isNull=false" 
		    			+ " XbrlApiConfiguration.getInstance().getTaxonomyRoot_isNull=" + (XbrlApiConfiguration.getInstance().getTaxonomyRoot()==null)  );	    		
	    	}
	    	else{
		    	logger.debug("Error found. See UbmXbrlApiFacade - CreateInstance. During the creation of a XBRL, application try get an instance of XbrlApiConfiguration. Process fails. XbrlApiConfiguration.getInstance()_isNull=true");	    		
	    	}
			logger.error("During the creation of a XBRL, application try get an instance of XbrlApiConfiguration. Process will fail.");	    	
	    }
	    
	    
	    
	    File taxonomyFile = new File(XbrlApiConfiguration.getInstance().getTaxonomyRoot() + schema);
	    if (!(taxonomyFile.exists()))
	    {
		logger.error("File " + XbrlApiConfiguration.getInstance().getTaxonomyRoot() + schema + " not found. During the creation of instance");
		throw new XBRLPGCException(XBRLPGCException.fileNotFound, new String[] { XbrlApiConfiguration.getInstance().getTaxonomyRoot() + schema,
			"During the creation of instance" });
	    }
	    String taxonomyUri = taxonomyFile.getAbsolutePath();

	    // Obtiene el DTS
	    messages.clearMemoList();

	    //dts = UbmXbrlHelper.load(taxonomyUri, LocationHandleType.c_Taxonomy, messages);
	    try{
	    	dts = UbmXbrlHelper.load(taxonomyUri, LocationHandleType.c_Taxonomy, messages);
	    } catch (NullPointerException  npe){
	    	logger.debug("Catching error NullPointerException line UbmXbrlHelper.load(). See UbmXbrlApiFacade - CreateInstance. messages_isNull=" + (messages==null) + "  taxonomyUri_isNull=" + (taxonomyUri==null));
			logger.error("Failed to load dts/taxonomy. During the creation of instance",npe);
			throw new XBRLPGCException(XBRLPGCException.errorCreatingXbrlInstance, new String[] { documentPath, namespaceUri, schema },npe);	    
	    }	    
	    
	    

	    if (messages.getMemoList().size() > 0)
	    {
		UbmXbrlHelper.displayMemos(messages.getMemoList());
	    }

	    // Bloquea la instancia durante la edición
	    IXbrlDomain taxonomyDO;

	    taxonomyDO = (IXbrlDomain) dts.findSingle("/'&" + namespaceUri + "#'");
    
	    // Crea el documento con la instancia
	    //UbmXbrlHelper.invokeDO(dts, taxonomyDO, "CreateInstance", new Object[] { instanceDocumentUri });
	    try{
	    	UbmXbrlHelper.invokeDO(dts, taxonomyDO, "CreateInstance", new Object[] { instanceDocumentUri });
	    } catch (NullPointerException  npe){
	    	logger.debug("Catching error NullPointerException line UbmXbrlHelper.invokeDO(). See UbmXbrlApiFacade - CreateInstance. dts_IsNull=" + (dts==null) +  " taxonomyDO_IsNull=" + (taxonomyDO==null) + " instanceDocumentUri_Valueof=" + instanceDocumentUri);
			logger.error("Failed to invoke ubMatrix 'CreateInstance'. During the creation of instance",npe); 
			throw new XBRLPGCException(XBRLPGCException.errorCreatingXbrlInstance, new String[] { documentPath, namespaceUri, schema },npe);	    
	    }	    
	    
	    
	    
	    
	    dts.close();

	    messages.clearMemoList();
	    dts = UbmXbrlHelper.load(instanceDocumentUri, LocationHandleType.c_Instance, messages);

	    logger.debug("CreateInstance: Xbrl instance is loaded. Checking memos.");
	    
	    if (messages.getMemoList().size() > 0)
	    {
		logger.error(UbmXbrlHelper.getStringMemos(messages.getMemoList()));
		throw new XBRLPGCException(XBRLPGCException.errorCreatingXbrlInstance, new String[] { documentPath, namespaceUri, schema });
	    }

	    // Busca la instancia que se acaba de crear
	    IDTSNode entryNode = dts.getEntryNode();
	    entryNode.lock(dts);
	    
	    logger.debug("CreateInstance: Find root node on Xbrl instance.");

	    IDTSQuery query = UbmXbrlHelper.findPath(entryNode, getPrefixMap(), "/xbrli:xbrl");
	    instanceDO = (IXbrlDomain) dts.findSingle(query);

	}catch (XBRLPGCException ex)
	{
		logger.error(ex);
	    throw ex;
	} catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingXbrlInstance, new String[] { documentPath, namespaceUri, schema }, e);
	}

	IXbrlInstance instance = XbrlApiFactory.createInstance();
	instance.setDts(dts);
	instance.setInstance(instanceDO);

	return instance;
    }

    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#createUnit(es.inteco.xbrl.pgc.apifacade.IXbrlUnit, es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public IXbrlUnit createUnit(IXbrlUnit unit, IXbrlInstance instance) throws XBRLPGCException
    {
	IXbrlDomain unitDO = null;
	try
	{
	    unitDO = (IXbrlDomain) UbmXbrlHelper
		    .invokeDO((IDTS) instance.getDts(), (IXbrlDomain) instance.getInstance(), "CreateUnit", new Object[] { "null" });
	    UbmXbrlHelper.invokeDO((IDTS) instance.getDts(), unitDO, "SetAttributeValue", new Object[] { "null", "id", unit.getKey() });
	    unit.setUnitObject(unitDO);
	    createMeasure(unit, instance);
	}
	catch (XBRLPGCException ex)
	{
	    throw ex;
	}
	
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingUnit, e);
	}

	return unit;
    }

    
    /**
     * Dado un objeto IXbrlUnit con la información de una measure XBRL, se crea un objeto
     * measure para la instancia unit que se pasa como parámetro de entrada.
     * 
     * @param unit 
     * Objeto que encapsula la información para una unidad XBRL
     * 
     * @param instance
     * Instancia XBRL
     * 
     * @return
     * Objeto con la información de la unidad XBRL, relleno con la measure recien creada.
     * 
     * @throws XBRLPGCException
     */
    private IXbrlUnit createMeasure(IXbrlUnit unit, IXbrlInstance instance) throws XBRLPGCException
    {
	IDTS dts = (IDTS) instance.getDts();
	IXbrlDomain measureDO = null;
	try
	{
	    IXbrlDomain unitDO = (IXbrlDomain) unit.getUnitObject();
	    measureDO = (IXbrlDomain) dts.findSingle(unitDO, c_measureFromUnitQuery);

	    UbmXbrlHelper.invokeDO(dts, unitDO, "RemoveMeasureDO", new Object[] { measureDO });
	    measureDO = (IXbrlDomain) UbmXbrlHelper.invokeDO(dts, unitDO, "CreateMeasure", new Object[] {});
	    UbmXbrlHelper.invokeDO(dts, measureDO, "SetValue", new Object[] { unit.getPrefix() + ":" + unit.getCode() });

	    measureDO = (IXbrlDomain) dts.findSingle(unitDO, c_measureFromUnitQuery);

	} 
	catch (Exception e)
	{

	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingMeasure, e);
	}

	unit.setMeasure(measureDO);
	return unit;
    }

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#createContext(es.inteco.xbrl.pgc.apifacade.IXbrlContext, es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public IXbrlContext createContext(IXbrlContext context, IXbrlInstance instance) throws XBRLPGCException
    {
	IDTS dts = (IDTS) instance.getDts();
	IXbrlDomain contextDO = null;
	try
	{

	    contextDO = (IXbrlDomain) UbmXbrlHelper.invokeDO(dts, (IXbrlDomain) instance.getInstance(), "CreateContext", new Object[] { "null" });
	    UbmXbrlHelper.invokeDO(dts, contextDO, "SetAttributeValue", new Object[] { "null", "id", context.getId() });
	    UbmXbrlHelper.invokeDO(dts, contextDO, "RemovePeriod", new Object[] {});
	    UbmXbrlHelper.invokeDO(dts, contextDO, "RemoveEntity", new Object[] {});

	    IXbrlDomain entityDO = (IXbrlDomain) UbmXbrlHelper.invokeDO(dts, contextDO, "CreateEntity", new Object[] { "null" });
	    IXbrlDomain identifierDO = (IXbrlDomain) UbmXbrlHelper.invokeDO(dts, entityDO, "CreateIdentifier", new Object[] { "null", "null" });
	    UbmXbrlHelper.invokeDO(dts, identifierDO, "SetAttributeValue", new Object[] { "null", "scheme", context.getEntityIdentifierSchema() });
	    UbmXbrlHelper.invokeDO(dts, identifierDO, "SetValue", new Object[] { context.getEntityIdentifierValue() });

	    contextDO = (IXbrlDomain) dts.findSingle(entityDO, c_contextFromEntity);

	} 
	catch (Exception e)
	{

	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingContext, e);
	}

	context.setContextObject(contextDO);
	return context;

    }

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#createDurationPeriod(es.inteco.xbrl.pgc.apifacade.IXbrlContext, es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public IXbrlContext createDurationPeriod(IXbrlContext context, IXbrlInstance instance) throws XBRLPGCException
    {
	IDTS dts = (IDTS) instance.getDts();
	IXbrlDomain periodDO = null;
	try
	{

	    periodDO = (IXbrlDomain) UbmXbrlHelper.invokeDO(dts, (IXbrlDomain) context.getContextObject(), "CreatePeriod", new Object[] { "null" });

	    IXbrlDomain startDateDO = (IXbrlDomain) UbmXbrlHelper.invokeDO(dts, periodDO, "CreateStartDate", new Object[] { "null" });
	    UbmXbrlHelper.invokeDO(dts, startDateDO, "SetValue", new Object[] { new org.exolab.castor.types.Date(context.getPeriodStartDate()).toString() });
	    IXbrlDomain endDateDO = (IXbrlDomain) UbmXbrlHelper.invokeDO(dts, periodDO, "CreateEndDate", new Object[] { "null" });
	    UbmXbrlHelper.invokeDO(dts, endDateDO, "SetValue", new Object[] { new org.exolab.castor.types.Date(context.getPeriodEndDate()).toString() });

	}
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingDurationPeriod, e);
	}

	context.setPeriodObject(periodDO);
	return context;

    }

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#createInstantPeriod(es.inteco.xbrl.pgc.apifacade.IXbrlContext, es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public IXbrlContext createInstantPeriod(IXbrlContext context, IXbrlInstance instance) throws XBRLPGCException
    {
	IDTS dts = (IDTS) instance.getDts();
	IXbrlDomain periodDO = null;

	try
	{
	    periodDO = (IXbrlDomain) UbmXbrlHelper.invokeDO(dts, (IXbrlDomain) context.getContextObject(), "CreatePeriod", new Object[] { "null" });

	    IXbrlDomain instantDO = (IXbrlDomain) UbmXbrlHelper.invokeDO(dts, periodDO, "CreateInstant", new Object[] { "null" });
	    UbmXbrlHelper.invokeDO(dts, instantDO, "SetValue", new Object[] { new org.exolab.castor.types.Date(context.getPeriodInstant()).toString() });

	}
	catch (Exception e)
	{

	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingInstantPeriod, e);
	}

	context.setPeriodObject(periodDO);
	return context;

    }

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#createFact(es.inteco.xbrl.pgc.apifacade.IXbrlFact, es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public IXbrlFact createFact(IXbrlFact fact, IXbrlInstance instance) throws XBRLPGCException
    {

	IDTS dts = null;
	IXbrlDomain instanceDO = null;
	IXbrlDomain factDO = null;
	try
	{
	    dts = (IDTS) instance.getDts();
	    instanceDO = (IXbrlDomain) instance.getInstance();

	    // Temporal, hasta que se quite el prefijo en el outputID de los
	    // conceptmaps
	    String info = fact.getConcept();
	    if (info.startsWith(fact.getPrefix()))
		info = info.substring(fact.getPrefix().length() + 1);

	    IXbrlDomain concept = (IXbrlDomain) dts.findSingle("/'&" + fact.getNamespace() + "#" + info + "'");
	    if (concept == null)
	    {
		logger.error("Error creating fact. Concept not found. Search expression: " + "/'&" + fact.getNamespace() + "#" + info + "'");
		throw new XBRLPGCException(XBRLPGCException.errorCreatingFactConceptNotFound, new String[] { "/'&" + fact.getNamespace() + "#" + info + "'" });
	    }

	    String value = fact.getValue();
	    if ((fact.getSign() != null) && (fact.getSign().equals("-")))
	    {
		value =  fact.getSign() + value;
	    }
	    factDO = (IXbrlDomain) UbmXbrlHelper.invokeDO(dts, instanceDO, "CreateFact", new Object[] { concept, value });
	    
	    if (isDecimal(dts,concept) || isMonetaryItem(dts, concept) || isNumeric(concept))
	    {
		if (fact.getDecimals() != null)
		{
		    UbmXbrlHelper.invokeDO(dts, factDO, "SetAttributeValue", new Object[] { "null", "decimals", fact.getDecimals() });
		}
		if (fact.getUnit() != null)
		{
		    UbmXbrlHelper.invokeDO(dts, factDO, "SetUnit", new Object[] { fact.getUnit().getUnitObject() });
		}
	    }

	    UbmXbrlHelper.invokeDO(dts, factDO, "SetContext", new Object[] { fact.getContext().getContextObject() });

	} 
	catch (XBRLPGCException ex)
	{
		logger.error(ex);
		throw ex;
	}
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingConcept, e);
	}

	fact.setFactObject(factDO);
	return fact;
    }

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#createTuple(es.inteco.xbrl.pgc.apifacade.IXbrlFact, es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public IXbrlFact createTuple(IXbrlFact tuple, IXbrlInstance instance) throws XBRLPGCException
    {

	IDTS dts = null;
	IXbrlDomain instanceDO = null;
	IXbrlDomain factDO = null;
	try
	{
	    dts = (IDTS) instance.getDts();
	    instanceDO = (IXbrlDomain) instance.getInstance();

	    IXbrlDomain concept = (IXbrlDomain) dts.findSingle("/'&" + tuple.getNamespace() + "#" + tuple.getConcept() + "'");
	    if (concept == null)
	    {
		logger.error("Error creating tuple fact. Concept not found. Search expression: " + "/'&" + tuple.getNamespace() + "#" + tuple.getConcept()
			+ "'");
		throw new XBRLPGCException(XBRLPGCException.errorCreatingTupleConceptNotFound, new String[] { "/'&" + tuple.getNamespace() + "#"
			+ tuple.getConcept() + "'" });
	    }

	    factDO = (IXbrlDomain) UbmXbrlHelper.invokeDO(dts, instanceDO, "CreateFact", new Object[] { concept, "" });

	} 
	
	catch (XBRLPGCException ex)
	{
		logger.error(ex);
	    throw ex;
	}
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingTuple, e);
	}
	tuple.setFactObject(factDO);

	return tuple;
    }

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#createDimension(java.lang.String, java.lang.String, es.inteco.xbrl.pgc.apifacade.IXbrlContext, es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public IXbrlContext createDimension(String domain, String member, IXbrlContext context, IXbrlInstance instance) throws XBRLPGCException
    {
	IDTS dts = (IDTS) instance.getDts();
	Context contextDO = null;
	try
	{
	    contextDO = (Context) context.getContextObject();
	    UbmXbrlHelper.invokeDO(dts, contextDO, "RemoveScenario", new Object[] {});
	    UbmXbrlHelper.invokeDO(dts, contextDO, "SetScenarioExplicitMember", new Object[] { contextDO, domain, member });

	} 
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingDimension, e);
	}

	return context;

    }

    /**
     * Determina si un concpeto es un item es decimal.
     * 
     * @param dts
     * referencia al dts.
     * 
     * @param concept
     * concepto sobre el que se realiza la consulta.
     * 
     * @return
     * verdadero si el concepto es monetario y falso en caso contrario.
     * 
     * @throws CoreException
     */
    private boolean isDecimal(IDTS dts, IXbrlDomain concept) throws CoreException
    {
	boolean isDecimal = false;

	String attributeValueType = concept.getAttributeValue(null, "type");

	if (attributeValueType != null)
	{
	    int hashIdx = attributeValueType.indexOf(":");
	    String qNamespace = null;
	    String qLocalName = null;

	    if (hashIdx == -1)
	    {
		qNamespace = concept.getNamespaceForPrefix(null);
		qLocalName = attributeValueType;
	    }

	    // prefix:localname
	    else
	    {
		qNamespace = concept.getNamespaceForPrefix(attributeValueType.substring(0,hashIdx));
		qLocalName = attributeValueType.substring(hashIdx+1);
	    }

	    String fullNameQuery = "&" + qNamespace + "#" + qLocalName;
	    IXbrlDomain dataTypeDO =  (IXbrlDomain)dts.getSchemaDomainObjectByFullname(fullNameQuery);

	    Iterator dataTypesIter = Xbrl21DomainHelper.datatypeAncestors(dataTypeDO, dts, true);

	    while (dataTypesIter.hasNext() && (!isDecimal))
	    {
		ValuedXbrlSchemaDomainImpl currentDataTypeVX = (ValuedXbrlSchemaDomainImpl)dataTypesIter.next();
		String typeName = currentDataTypeVX.getAttributeValue(null, "name");
		isDecimal = (typeName != null) && (typeName.equals("decimal"));
	    }

	}
	return isDecimal;
    }
    

    /**
     * Determina si un concpeto es un item monetario.
     * 
     * @param dts
     * referencia al dts.
     * 
     * @param concept
     * concepto sobre el que se realiza la consulta.
     * 
     * @return
     * verdadero si el concepto es monetario y falso en caso contrario.
     * 
     * @throws CoreException
     */
    private boolean isMonetaryItem(IDTS dts, IXbrlDomain concept) throws CoreException
    {
	boolean resp = false;
	String attributeValueType = concept.getAttributeValue(null, "type");

	if (attributeValueType != null)
	{
	    attributeValueType =  attributeValueType.substring(attributeValueType.indexOf(":")+1);  
	    resp= attributeValueType.equals("monetaryItemType") || attributeValueType.equals("monetaryPositiveItemType") || attributeValueType.equals("monetaryNegativeItemType") || attributeValueType.equals("monetaryZeroItemType");
	}
	
	return resp;
    }

    /**
     * Determina si un concepto es de tipo numérico.
     * 
     * @param concept
     * Concepto sobre el que se realiza la consulta.
     * 
     * @return
     * True si el concepto es numérico y false en caso contrario.
     * 
     * @throws CoreException
     */
    private boolean isNumeric(IXbrlDomain concept) throws CoreException
    {
	boolean resp = false;
	String attributeValueType = concept.getAttributeValue(null, "type");
	
	if (attributeValueType != null)
	{
	    attributeValueType = "http://www.xbrl.org/2003/instance#" + attributeValueType.substring(attributeValueType.indexOf(":")+1);  
	    resp = Xbrl21DomainHelper.getNumericTable().containsKey(attributeValueType);
	}
	return resp;
    }
    
    

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#createTupleChildTuple(es.inteco.xbrl.pgc.apifacade.IXbrlFact, es.inteco.xbrl.pgc.apifacade.IXbrlFact, es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public IXbrlFact createTupleChildTuple(IXbrlFact fact, IXbrlFact parentFact, IXbrlInstance instance) throws XBRLPGCException
    {
	IXbrlDomain childFactDO = null;
	try
	{
	    IDTS dts = (IDTS) instance.getDts();

	    IXbrlDomain concept = (IXbrlDomain) dts.findSingle("/'&" + fact.getNamespace() + "#" + fact.getConcept() + "'");

	    if (concept == null)
	    {
		logger
			.error("Error creating tuple child. Concept not found. Search expression: " + "/'&" + fact.getNamespace() + "#" + fact.getConcept()
				+ "'");
		throw new XBRLPGCException(XBRLPGCException.errorCreatingTupleChildConceptNotFound, new String[] { "/'&" + fact.getNamespace() + "#"
			+ fact.getConcept() + "'" });
	    }

	    childFactDO = (IXbrlDomain) UbmXbrlHelper.invokeDO(dts, (IXbrlDomain) parentFact.getFactObject(), "CreateChildFact", new Object[] { concept, "null"});

	} 
	catch (XBRLPGCException ex)
	{
		logger.error(ex);
	    throw ex;
	}
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingTupleChild, e);
	}
	fact.setFactObject(childFactDO);
	return fact;
    }
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#createTupleChild(es.inteco.xbrl.pgc.apifacade.IXbrlFact, es.inteco.xbrl.pgc.apifacade.IXbrlFact, es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public IXbrlFact createTupleChild(IXbrlFact fact, IXbrlFact parentFact, IXbrlInstance instance) throws XBRLPGCException
    {
	IXbrlDomain childFactDO = null;
	try
	{
	    IDTS dts = (IDTS) instance.getDts();

	    IXbrlDomain concept = (IXbrlDomain) dts.findSingle("/'&" + fact.getNamespace() + "#" + fact.getConcept() + "'");

	    if (concept == null)
	    {
		logger
			.error("Error creating tuple child. Concept not found. Search expression: " + "/'&" + fact.getNamespace() + "#" + fact.getConcept()
				+ "'");
		throw new XBRLPGCException(XBRLPGCException.errorCreatingTupleChildConceptNotFound, new String[] { "/'&" + fact.getNamespace() + "#"
			+ fact.getConcept() + "'" });
	    }

	    String value = fact.getValue();
	    if (fact.getSign() != null)
	    {
		value = fact.getSign() + value;
	    }

	    childFactDO = (IXbrlDomain) UbmXbrlHelper.invokeDO(dts, (IXbrlDomain) parentFact.getFactObject(), "CreateChildFact", new Object[] { concept,
		    value });

		UbmXbrlHelper.invokeDO(dts, childFactDO, "SetContext", new Object[] { fact.getContext().getContextObject() });
	    
	    if (isDecimal(dts,concept) || isMonetaryItem(dts, concept) || isNumeric(concept))
	    {
		UbmXbrlHelper.invokeDO(dts, childFactDO, "SetAttributeValue", new Object[] { "null", "decimals", fact.getDecimals() });
		UbmXbrlHelper.invokeDO(dts, childFactDO, "SetUnit", new Object[] { fact.getUnit().getUnitObject() });
	    }


	} 
	
	catch (XBRLPGCException ex)
	{
		logger.error(ex);
	    throw ex;
	}
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingTupleChild, e);
	}
	fact.setFactObject(childFactDO);
	return fact;
    }

    
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#isInstant(es.inteco.xbrl.pgc.apifacade.IXbrlFact, es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public boolean isInstant(IXbrlFact fact, IXbrlInstance instance) throws XBRLPGCException
    {
	boolean result = false;

	try
	{
	    String info = fact.getConcept();
	    if (fact.getConcept().startsWith(fact.getPrefix()))
		info = info.substring(fact.getPrefix().length() + 1);
	    IXbrlDomain concept = (IXbrlDomain) ((IDTS) instance.getDts()).findSingle("/'&" + fact.getNamespace() + "#" + info + "'");
	    if (concept == null)
	    {
		logger.error("periodType query error, cheking if it's instant. Concept not found. Search expression: " + "/'&" + fact.getNamespace() + "#"
			+ info + "'");
		throw new XBRLPGCException(XBRLPGCException.errorCheckingPeridoTypeIsInstant, new String[] { "/'&" + fact.getNamespace() + "#" + info + "'" });
	    }

	    String attributeValuePeriodType = concept.getAttributeValue("http://www.xbrl.org/2003/instance", "periodType");
	    if (attributeValuePeriodType != null)
		result = attributeValuePeriodType.equals("instant");

	} 
	
	catch (XBRLPGCException ex)
	{
		logger.error(ex);
	    throw ex;
	}
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorReadingPeridodTypeAttribute, e);
	}

	return result;
    }

    
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#createFootNoteLink(es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public Object createFootNoteLink(IXbrlInstance instance) throws XBRLPGCException
    {

	IXbrlDomain linkDO = null;
	IDTS dts = null;
	IXbrlDomain instanceDO = null;
	try
	{
	    dts = (IDTS) instance.getDts();
	    instanceDO = (IXbrlDomain) instance.getInstance();
	    ParsedQuery query = new ParsedQuery(XbrlDomainUri.c_defaultLinkRoleUri);
	    IXbrlDomain linkRole = (IXbrlDomain) dts.findSingle(query);

	    if (linkRole == null)
	    {
		logger.error("Error creating footnote link. LinkRole not found. Search expression: " + query.getExpression());
		throw new XBRLPGCException(XBRLPGCException.errorCreatingFootNoteLinkLinkRoleNotFound, new String[] { query.getExpression() });
	    }

	    linkDO = InstanceBehavior.createFootnoteLink(instanceDO, dts, linkRole);

	} 
	
	catch (XBRLPGCException ex)
	{
		logger.error(ex);
	    throw ex;
	}
	catch (Exception e)
	{

	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingFootNoteLink, e);
	}
	return linkDO;
    }

    

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#createFootNote(java.lang.Object, es.inteco.xbrl.pgc.apifacade.IXbrlFact, java.lang.String, java.lang.String, java.lang.String, es.inteco.xbrl.pgc.apifacade.IXbrlInstance, boolean)
     */
    public Object createFootNote(Object linkDO, IXbrlFact fact, String language, String text, String order, IXbrlInstance instance, boolean locatorExists) throws XBRLPGCException
    {

	IXbrlDomain footNoteDO = null;
	IDTS dts = null;
	try
	{
	    dts = (IDTS) instance.getDts();

	    ParsedQuery query = new ParsedQuery("http://www.xbrl.org/2003/role/footnote");
	    IXbrlDomain noteRole = (IXbrlDomain) dts.findSingle(query);
	    if (noteRole == null)
	    {
		logger.error("Error creating FootNote. Note role not found. Search expression: " + query.getExpression());
		throw new XBRLPGCException(XBRLPGCException.errorCreatingFootNoteNoteRoleNotFound, new String[] { query.getExpression() });
	    }

	    query = new ParsedQuery("http://www.xbrl.org/2003/arcrole/fact-footnote");
	    IXbrlDomain arcRole = (IXbrlDomain) dts.findSingle(query);
	    if (arcRole == null)
	    {
		logger.error("Error creating FootNote. Arc role not found. Search expression: " + query.getExpression());
		throw new XBRLPGCException(XBRLPGCException.errorCreatingFootNoteArcRoleNotFound, new String[] { query.getExpression() });
	    }

	    IXbrlDomain factDO = (IXbrlDomain) fact.getFactObject();

	    //si el fact ya tuviera asignado un id para el footnote, lo utilizamos
	    //esto podría pasar porque un fact puede tener varios comentarios y, al crear el primero de ellos,
	    //ya se le crea y asocia el ID al fact
	    String existingId= factDO.getAttributeValue(null, "id");
	    if (existingId==null){
	    	factDO.setAttributeValue(null, "id", TransformerHelper.generateFootNoteID());
	    }else{
	    	factDO.setAttributeValue(null, "id", existingId);
	    }
	    
	    //esta lista almacena el locator asociado al footNote del fact por si hubiera que
	    //volver a utilizarlo
	    ArrayList<IXbrlDomain> locatorList=new ArrayList<IXbrlDomain>();
	    
	
	    //si el locator no existe (porque es la primera vez que asociamos footNotes a este fact),
	    //limpiamos la variable lastFootnoteLocator
	    if (locatorExists==false){
	    	lastFootnoteLocator=null;
	    }
	
	    
	    footNoteDO = PGCLinkBehaviour.createArcWithSingleConceptAsEnd((IXbrlDomain) linkDO, dts, factDO, arcRole, noteRole, 
	    						language, text, "", order, locatorList, lastFootnoteLocator);
	    	 
	    
	    //locatorList tendrá almacenado el locator que se asoció al foonoteProcesado
	    //volcamos el locator a la variable lastFootnoteLocator por si fuera necesario reutilizarlo.
	    //Se reutilizará si asociamos a este fact varios footNotes
		if ( (!(locatorList==null)) &&  locatorList.size()>0){
			lastFootnoteLocator =  (IXbrlDomain)locatorList.get(0);
		}
		else{
			lastFootnoteLocator=null;
		}
	    
	    
	    

	} 
	
	catch (XBRLPGCException ex)
	{
		logger.error(ex);
	    throw ex;
	}
	catch (Exception e)
	{

	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingFootNote, new String[] { text }, e);
	}
	return footNoteDO;
    }

    
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#validate(java.lang.String)
     */
    public String[] validate(String documentPath) throws XBRLPGCException
    {
	// get the instance of Xbrl processor
	Xbrl xbrlInstance = Xbrl.getInstance();

	try
	{

	    boolean loadOK = false;
	    loadOK = xbrlInstance.load(documentPath);

	    if (loadOK)
	    {
		if (xbrlInstance.validate(false))
		{
		    return null;
		}
	    }

	    if (xbrlInstance.getMemos().length > 0)
	    {
		return xbrlInstance.getMemos();
	    } else
	    {
		if (!(loadOK))
		{
		    logger.error("Error loading instance " + documentPath);
		    throw new XBRLPGCException(XBRLPGCException.errorValidatingInstanceNotLoad, new String[] { documentPath });
		} else
		{
		    return null;
		}
	    }
	} 
	catch (XBRLPGCException ex)
	{
		logger.error(ex);
	    throw ex;
	}
	catch (Exception e)
	{
	    // get any memos generated during load / validation process

	    if (xbrlInstance.getMemos().length > 0)
	    {
		return xbrlInstance.getMemos();
	    } else
	    {
		logger.error(e);
		throw new XBRLPGCException(XBRLPGCException.errorValidatingInstance, new String[] { documentPath }, e);

	    }
	} finally
	{
	    try
	    {
		// done with validation operation - close xbrl instance
		if (!xbrlInstance.close())
		{
		    logger.warn("Error closing instance " + documentPath);
		    if (!(loggerTraceList==null)){loggerTraceList.add("WARNING: Error closing instance " + documentPath);}
		}

	    } catch (Exception e)
	    {
		logger.warn("Error closing instance " + documentPath);
		if (!(loggerTraceList==null)){loggerTraceList.add("WARNING: Error closing instance " + documentPath);}
	    }
	}

    }

    /**
     * Devuelve una lista de parametros obtenidos del Object que llega como parametro
     * @param arrayParams
     * @return
     * lista de parametros
     */
    private ArrayList<String> getParameters(Object[] arrayParams)
    {
	int numParams = arrayParams.length;
	ArrayList<String> parameters = new ArrayList<String>(numParams);
	for (int i=0; i<numParams; i++)
	{
	    parameters.add((String)arrayParams[i]);
	}	
	return parameters;
    }

   
    
    
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#validate(java.lang.String, es.inteco.xbrl.pgc.errors.GenericErrorsHandler)
     */
    public boolean validate(String documentPath, GenericErrorsHandler errorHandler) throws XBRLPGCException
    {
	boolean isValid = false;
	UbmXbrlValidator ubmValidator =null;
	try
	{
		ubmValidator = new UbmXbrlValidator();

	    IDTS dts = ubmValidator.load(documentPath, LocationHandleType.c_Instance);
	    
	    if ( ubmValidator.getMemoList().size() == 0 ) 
	    {
		ubmValidator.validate(dts); 
	    }
	} 
	
	catch (XBRLPGCException ex)
	{
	    ex.printStackTrace();
		throw ex;
	}
	catch (Throwable e)
	{
		e.printStackTrace();
	    throw new XBRLPGCException(XBRLPGCException.errorValidatingInstance, new String[] { documentPath }, e); 
	}
	try
	{
	    isValid = true;
	    for (int i = 0; i < ubmValidator.getMemoList().size(); ++i) 
	    { 
	
			IMemo memo = (IMemo)ubmValidator.getMemoList().get(i);
	    	try 
	    	{

				if ((memo instanceof FlyweightErrorMemo ) || (memo instanceof FlyweightFatalErrorMemo))
				{
				    isValid = false;
				    if (memo.getMemoURI().equals("memo://ubmatrix.com/Xbrl/Common/Xml/SchemaValidation#SchemaValidationError"))
				    {
							PGCUtils.processSchemaValidationError((String)memo.getParticles()[memo.getParticles().length-1], errorHandler);
				    }
				    else
				    {
							String defaultDescription = ubmValidator.getSubstitutedLocalizedString(memo);
							errorHandler.addError(memo.getMemoURI(), defaultDescription, getParameters(memo.getParticles()));
				    }
				}

			}
			catch (Throwable e2)
			{
				//error recorriendo los memos
				logger.error(e2);
				if (!(loggerTraceList==null)){loggerTraceList.add(e2.toString());}
			}
	    }
	} 
	catch (Throwable e3)
	{
	   	throw new XBRLPGCException(XBRLPGCException.errorValidatingInstance, new String[] { documentPath }, e3); 
	}
	
	return isValid;
    }
    
    
    
    /**
     * Obtiene el code label para un primari item de entrada.
     * 
     * @param primary
     * elemento para el cual se consulta el code label
     * 
     * @param dts
     * referencia al dts
     * 
     * @return
     * devuelve un string con el code label asociado
     * 
     * @throws XBRLPGCException
     */
    private String getCodeLabel(IXbrlDomain primary, String roleUri,IDTS dts) throws XBRLPGCException
    {

	String codeLabel = null;

	try
	{

	    UBMExtensions_Taxonomy ext = new UBMExtensions_Taxonomy();
	    ExtensionFunctionArgs args = new ExtensionFunctionArgs("label-arcs", new Object[] { primary }, dts, null, null);

	    Iterator iterLabels = (Iterator) ext.invoke(args);

	    ArcRelationship arcRelation = null;
	    while (iterLabels.hasNext())
	    {
		arcRelation = (ArcRelationship) iterLabels.next();
		if (((DeferredElementNSImpl) arcRelation.getParent()).getAttributeNS("http://www.w3.org/1999/xlink", "role").equals(roleUri))
		{
		    codeLabel = ((ValuedXbrlInstanceDomainImpl) arcRelation.getTo()).getValue();
		}
	    }

	} catch (CoreException e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.coreErrorGettingCodeLabel, e);
	} catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorGettingCodeLabel, e);
	}
	return codeLabel;
    }

    
    /**
     * Agrega los members de la lista al ConceptMap
     * @param members
     * lista tipo vector de members que necesitamos agregar
     * @param conceptMap
     * ConceptMap al que se agregan los members
     * @throws XBRLPGCException
     */
    private void addMembers(Vector<Member> members, ConceptMap conceptMap) throws XBRLPGCException
    {

	try
	{
	    int numMembers = members.size();
	    for (int i = 0; i < numMembers; i++)
	    {
		conceptMap.addMember(members.get(i));
	    }
	} 

	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorAddMembersToConceptMap, e);
	}
    }

    
    /**
     * Crea un ConceptMap
     * @param primary
     * @param labelRoleUri
     * @param members
     * @param domain
     * @param dts
     * @return
     * ConceptMap creado
     * @throws CoreException
     * @throws XBRLPGCException
     */
    private ConceptMap createConceptMap(IXbrlDomain primary, String labelRoleUri,Vector<Member> members, String domain, IDTS dts) throws CoreException,
	    XBRLPGCException
    {
	ConceptMap conceptMap = null;
	try
	{

	    conceptMap = createConceptMap(primary,labelRoleUri, dts);
	    conceptMap.setDomain(domain);
	    addMembers(members, conceptMap);

	} 
	catch (XBRLPGCException ex)
	{
	    throw ex;
	}
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingConceptMap, e);
	}
	return conceptMap;
    }

    /**
     * Crea un ConceptMap
     * @param primary
     * @param labelRoleUri
     * @param dts
     * @return
     * ConceptMap creado
     * @throws CoreException
     * @throws XBRLPGCException
     */
    private ConceptMap createConceptMap(IXbrlDomain primary, String labelRoleUri, IDTS dts) throws CoreException, XBRLPGCException
    {

	ConceptMap conceptMap = new ConceptMap();
	String inputCodeLabel="";

	try
	{

	    inputCodeLabel=getCodeLabel(primary, labelRoleUri,dts);
	    if ( (inputCodeLabel==null) || (inputCodeLabel.equals(""))  ){
	    	
		logger.warn("Can not find input ID for " + primary.getName());
		if (!(loggerTraceList==null)){loggerTraceList.add("WARNING: Can not find input ID for " + primary.getName());}
	    }
	    
	    conceptMap.setInputID(inputCodeLabel);
	    conceptMap.setOutputID(primary.getName());
	    conceptMap.setNs(primary.getNamespaceUri());
	    conceptMap.setNsPrefix(primary.getPrefixForNamespace(primary.getNamespaceUri()));

	} 
	catch (XBRLPGCException ex)
	{
	    throw ex;
	}
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingConceptMap, e);
	}

	return conceptMap;
    }

    /**
     * Crea una lista de members a partir de un hypercube
     * @param hypercube
     * @param labelRoleUri
     * @param dts
     * @return
     * lisa de members
     * @throws CoreException
     * @throws XBRLPGCException
     */
    private Vector<Member> createMembers(Hypercube hypercube, String labelRoleUri, IDTS dts) throws CoreException, XBRLPGCException
    {

	Vector<Member> members = new Vector<Member>();

	try
	{
	    //Obtenemos los domain members
	    Iterator iterDomainMembersArcs = hypercube.getDomainMemberArcs().getEnumerator();
	    ArcRelationship arcRelation = null;
	    ValuedXbrlSchemaDomainImpl domainMember = null;
	    while (iterDomainMembersArcs.hasNext())
	    {
		arcRelation = (ArcRelationship) iterDomainMembersArcs.next();
		domainMember = (ValuedXbrlSchemaDomainImpl) arcRelation.getTo();

		String usableAttribute = arcRelation.getAttributeValue("http://xbrl.org/2005/xbrldt", "usable");
		if ((usableAttribute == null) || (!usableAttribute.equalsIgnoreCase("false")))
		{

		    Member member = new Member();
		    member.setId(getCodeLabel(domainMember,labelRoleUri, dts));
		    member.setQname(domainMember.getQName());
		    if (member.getId() == null)
		    {
			logger.warn("Not found id for member " + domainMember.getName());
			if (!(loggerTraceList==null)){loggerTraceList.add("WARNING: Not found id for member " + domainMember.getName());}
		    }
		    members.add(member);
		}
	    }
	    
	    //Obtenemos los dimensiondomain
	    Iterator iterDimensionDomainArcs = hypercube.getDimensionDomainArcs().getEnumerator();
	    ValuedXbrlSchemaDomainImpl dimensionDomain = null;
	    while (iterDimensionDomainArcs.hasNext())
	    {
		arcRelation = (ArcRelationship) iterDimensionDomainArcs.next();
		dimensionDomain = (ValuedXbrlSchemaDomainImpl) arcRelation.getTo();

		String usableAttribute = arcRelation.getAttributeValue("http://xbrl.org/2005/xbrldt", "usable");
		if ((usableAttribute == null) || (!usableAttribute.equalsIgnoreCase("false")))
		{

		    Member member = new Member();
		    member.setId(getCodeLabel(dimensionDomain,labelRoleUri, dts));
		    member.setQname(dimensionDomain.getQName());
		    if (member.getId() == null)
		    {
			logger.warn("Not found id for member " + dimensionDomain.getName());
			if (!(loggerTraceList==null)){loggerTraceList.add("WARNING: Not found id for member " + dimensionDomain.getName());}
		    }
		    members.add(member);
		}
	    }
	    
	    

	}
	catch (XBRLPGCException ex)
	{
	    throw ex;
	}
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorGettingMembers, e);
	}

	return members;
    }

    /**
     * Crea un nuevo Statement
     * @param mainSchemaNamespace
     * @param mainSchemaPrefix
     * @param nameMap
     * @return
     * Statement creado
     * @throws CoreException
     * @throws XBRLPGCException
     */
    private Statement createStatement(String mainSchemaNamespace, String mainSchemaPrefix, String nameMap) throws CoreException, XBRLPGCException
    {
	Statement map = new Statement();
	try
	{
	    map.setMainSchemaNamespace(mainSchemaNamespace);
	    map.setMainSchemaPrefix(mainSchemaPrefix);
	    map.setName(nameMap);
	} 
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingStatement, new String[] { mainSchemaNamespace, mainSchemaPrefix, nameMap }, e);
	}
	return map;
    }
    
    

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#loadTaxonomy(java.lang.String)
     */
    public IXbrlInstance loadTaxonomy(String schema) throws XBRLPGCException
    {

	IDTS dts = null;
	UbmXbrlMessages messages = new UbmXbrlMessages();
	File taxonomyFile = new File(XbrlApiConfiguration.getInstance().getTaxonomyRoot() + schema);

	if (!(taxonomyFile.exists()))
	{
	    logger.error("File " + XbrlApiConfiguration.getInstance().getTaxonomyRoot() + schema + " not found. During load taxonomy");
	    throw new XBRLPGCException(XBRLPGCException.fileNotFound, new String[] { XbrlApiConfiguration.getInstance().getTaxonomyRoot() + schema,
		    "During load taxonomy" });
	}

	String taxonomyUri = taxonomyFile.getAbsolutePath();
	try
	{
	    dts = UbmXbrlHelper.load(taxonomyUri, LocationHandleType.c_Taxonomy, messages);
	} 

	catch (FormatterException e)
	{

	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorLoadingTaxonomy, new String[] { schema, taxonomyFile.getPath(), taxonomyUri }, e);
	}

	IXbrlInstance instance = XbrlApiFactory.createInstance();
	instance.setDts(dts);

	return instance;

    }

    /**
     * 
     * Genera los primary items con los parametros de entrada
     * @param roleUri
     * @param labelRoleUri
     * @param iterPrimaryItems
     * @param members
     * @param dimension
     * @param dts
     * @param map
     * @throws CoreException
     * @throws IndexOutOfBoundsException
     * @throws XBRLPGCException
     */
    private void generatePrimaryItems(String roleUri, String labelRoleUri, Iterator iterPrimaryItems, Vector<Member> members, String dimension, IDTS dts, Statement map) throws CoreException, IndexOutOfBoundsException, XBRLPGCException
    {

	while (iterPrimaryItems.hasNext())
	{
	    ValuedXbrlSchemaDomainImpl primary = (ValuedXbrlSchemaDomainImpl) iterPrimaryItems.next();
	    ValuedXbrlSchemaDomainImpl primaryItem = null;
	    
	    IDTSResultSet defArcs = dts.find(primary, s_definitionArcsFromConceptPath);	
	    Iterator iterPrimaryArcs = defArcs.getEnumerator();
	    Vector<ValuedXbrlSchemaDomainImpl> primaryItemsFound = new Vector<ValuedXbrlSchemaDomainImpl>();
	    
	    ArcRelationship arcRelation = null;
	    while (iterPrimaryArcs.hasNext())
	    {
		arcRelation = (ArcRelationship) iterPrimaryArcs.next();
		if (((DeferredElementNSImpl) arcRelation.getParent()).getAttributeNS("http://www.w3.org/1999/xlink", "role").equals(roleUri))
		{
		    primaryItem = (ValuedXbrlSchemaDomainImpl) arcRelation.getTo();
		    String abstractAttribute = primaryItem.getAttributeValue(null, "abstract");
		    if ((abstractAttribute == null) || (abstractAttribute.equalsIgnoreCase("false")))
		    {
			ConceptMap newConceptMap = createConceptMap(primaryItem, labelRoleUri,members,dimension, dts);
			if (newConceptMap.getInputID() != null)
			{
			    map.addConceptMap(newConceptMap);
			}
			
		    }		
		    primaryItemsFound.add(primaryItem);
		}
	    }
	    if (primaryItemsFound.size() > 0)
	    {
		generatePrimaryItems(roleUri,labelRoleUri, primaryItemsFound.iterator(), members, dimension, dts, map);
	    }
	}
	
    }
    
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#generateDimensionMap(java.lang.String, java.lang.String, java.lang.String, java.lang.String, es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public void generateDimensionMap(Statement map, String roleUri,String labelRoleUri, String nameMap, String mainSchemaNamespace, String mainSchemaPrefix, IXbrlInstance instance)
	    throws XBRLPGCException
    {
	IDTS dts = (IDTS) instance.getDts();
	

	
	try
	{
	    IDTSResultSet hypercubes = null;
	    Iterator iterhypercubes = null;
	    IDTSResultSet primarItems = null;
	    hypercubes = HypercubeHelper.determineHypercubesInBasesetByPrimaryItem(dts, roleUri); // "http://www.icac.meh.es/es/fr/gaap/pgc07/cuentas/normal/patrimonioNeto/B/2008-01-01/role/SeccionVariacionSaldo");

	    if (hypercubes.getCount() == 0)
	    {
		logger.error("During dimension map generation. Not exists hypercube in role " + roleUri);
		throw new XBRLPGCException(XBRLPGCException.hyperCubeNotFoundInRoleGDM, new String[] { roleUri });
	    }

	    iterhypercubes = hypercubes.getEnumerator();
	    Hypercube hypercube = null;
	    while (iterhypercubes.hasNext())
	    {
		hypercube = (Hypercube) iterhypercubes.next();
		IXbrlDomain dimension = (IXbrlDomain) hypercube.getDimensions().toArray()[0];

		Vector<Member> members = createMembers(hypercube,labelRoleUri, dts);

		Iterator iterPrimarItems = hypercube.getPrimaryItems().getEnumerator();
		generatePrimaryItems(roleUri,labelRoleUri, iterPrimarItems, members, dimension.getQName(), dts, map);
	    }
	}
	
	catch (XBRLPGCException ex)
	{
		logger.error(ex);
	    throw ex;
	}
	catch (Exception e)
	{

	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingStatementDimensionMap, new String[] { roleUri, nameMap, mainSchemaNamespace,
		    mainSchemaPrefix }, e);
	}

    }

    /**
     * 
     * Obtiene un role a partir de la información que le llega en los parametros
     * @param roleUri
     * @param dts
     * @return
     * role encontrado
     * @throws CoreException
     * @throws XBRLPGCException
     */
    private Role getRole(String roleUri, IDTS dts) throws CoreException, XBRLPGCException
    {

	Role role = null;
	try
	{
	    ParsedQuery query = new ParsedQuery(XbrlDomainUri.c_Role);
	    IDTSResultSet rolesResultSet = dts.find(query);

	    Iterator iterRoles = rolesResultSet.getEnumerator();
	    while (iterRoles.hasNext())
	    {

		Role currentRole = (Role) iterRoles.next();
		if (roleUri.equals(currentRole.toString()))
		{
		    role = currentRole;
		    break;
		}
	    }

	} 
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorGettingRoleFromDTS, new String[] { roleUri }, e);
	}

	return role;
    }

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#generateMap(java.lang.String, java.lang.String, java.lang.String, java.lang.String, es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public Statement generateMap(String roleUri,String labelRoleUri, String nameMap, String mainSchemaNamespace, String mainSchemaPrefix, IXbrlInstance instance)
	    throws XBRLPGCException
    {
	Statement map = null;
	IDTS dts = (IDTS) instance.getDts();

	try
	{
	    Role role = getRole(roleUri, dts);
	    if (role == null)
	    {
		logger.error("Error during map generation. Not exist role " + roleUri + "for map " + nameMap);
		throw new XBRLPGCException(XBRLPGCException.roleNotFoundInMap, new String[] { roleUri, nameMap });
	    }

	    map = createStatement(mainSchemaNamespace, mainSchemaPrefix, nameMap);

	    ParsedQuery query = new ParsedQuery(XbrlDomainUri.c_PresentationArc);
	    IDTSResultSet rolesResultSet = dts.find(query);
	    Iterator iterRoleChildrens = rolesResultSet.getEnumerator();
	    ArcRelationship arcRelation = null;
	    while (iterRoleChildrens.hasNext())
	    {
		arcRelation = (ArcRelationship) iterRoleChildrens.next();
		if (((DeferredElementNSImpl) arcRelation.getParent()).getAttributeNS("http://www.w3.org/1999/xlink", "role").equals(roleUri))
		{
		    ValuedXbrlSchemaDomainImpl to = (ValuedXbrlSchemaDomainImpl) arcRelation.getTo();
		    ConceptMap conceptMap = createConceptMap(to,labelRoleUri, dts);
		    if (conceptMap.getInputID() != null)
		    {
			map.addConceptMap(conceptMap);
		    } else
		    {
			logger.warn("Not exist code label for -> " + conceptMap.getOutputID());
			if (!(loggerTraceList==null)){loggerTraceList.add("WARNING: Not exist code label for -> " + conceptMap.getOutputID());}
		    }
		}
	    }
	} 
	catch (XBRLPGCException ex)
	{
		logger.error(ex);
	    throw ex;
	}
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingMap, new String[] { roleUri, nameMap, mainSchemaNamespace, mainSchemaPrefix }, e);
	}
	return map;
    }

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#loadInstance(java.lang.String)
     */
    public IXbrlInstance loadInstance(String documentPath) throws XBRLPGCException
    {

	IDTS dts = null;
	UbmXbrlMessages messages = new UbmXbrlMessages();
	IXbrlInstance returnInstance = XbrlApiFactory.createInstance();
	IXbrlDomain instanceDO = null;

	try
	{
	    dts = UbmXbrlHelper.load(documentPath, LocationHandleType.c_Instance, messages);

	    if (messages.getMemoList().size() > 0)
	    {
		UbmXbrlHelper.displayMemos(messages.getMemoList());
		throw new XBRLPGCException(XBRLPGCException.errorLoadingInstance, new String[] {documentPath,UbmXbrlHelper.getStringMemos(messages.getMemoList())  });
	    }

	    // Find the newly created instance document
	    IDTSNode entryNode = dts.getEntryNode();
	    entryNode.lock(dts);

	    IDTSQuery query = UbmXbrlHelper.findPath(entryNode, getPrefixMap(), "/xbrli:xbrl");
	    instanceDO = (IXbrlDomain) dts.findSingle(query);

	}catch (XBRLPGCException ex){
		logger.error(ex);
	    throw ex;
	} catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorLoadingInstance, new String[] { documentPath }, e);
	}

	returnInstance.setInstance(instanceDO);
	returnInstance.setDts(dts);
	return returnInstance;

    }

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#getXBRLschemaRef(es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public String getXBRLschemaRef(IXbrlInstance xbrlInstance) throws XBRLPGCException
    {
	String returnSchema = null;
	try
	{

	    IDTS dts = (IDTS) xbrlInstance.getDts();

	    IDTSResultSet dtsRs = dts.getImmediatelyReferencedSchemas();
	    if (dtsRs.getCount() != 1)
	    {
		// TODO: arreglarlo para que, si no encuentra schemas, devuelva
		// null pero no lance excepcion
		// en las llamadas, sustituir try/catch por if isnull...
		throw new XBRLPGCException("Not found schemaRef", (Throwable) null);
	    }

	    Iterator i = dtsRs.getEnumerator();

	    DTSNode node = (DTSNode) i.next();
	    returnSchema = node.getPrimaryLocationHandle().getLogicalUri();

	} 
	catch (XBRLPGCException ex)
	{
		logger.error(ex);
	    throw ex;
	}
	catch (Exception e)
	{

	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorGettingSchemaFromInstance, e);
	}
	return returnSchema;
    }

    /**
     * Crea un fact con la información que le llega como parámetro
     * @param newFact
     * @param dts
     * @return
     * fact creado
     * @throws XBRLPGCException
     */
    private IXbrlFact createXBRLFact(ValuedXbrlInstanceDomainImpl newFact,  IDTS dts) throws XBRLPGCException
    {
	IXbrlFact createdFact = null;
	
	try
	{
	    IXbrlDomain concept = (IXbrlDomain) dts.findSingle("/'&" + newFact.getNamespaceUri() + "#" + newFact.getName() + "'");
	    
	    String decimals = newFact.getAttributeValue(null, "decimals");
	    String sign = null;
	    String value = newFact.getValue();
	    if ((value != null) && ((isNumeric(concept) || isMonetaryItem(dts, concept) || (isDecimal(dts, concept)))))
	    {
		float valueFloat = Float.parseFloat(value);
		if (valueFloat >= 0){
		    sign="+";
		    
		}
		else
		{
		    sign="-";
		}
		if ((value.startsWith("+")) || (value.startsWith("-")))
		    value = value.substring(1);
	    }
	    

	    createdFact = XbrlApiFactory.createFact();
	    createdFact.setConcept(newFact.getName());
	    createdFact.setValue(value);
	    createdFact.setDecimals(decimals);
	    createdFact.setFactObject(newFact);
	    createdFact.setSign(sign);

	    IXbrlUnit unit = XbrlApiFactory.createUnit();
	    String unitKey = newFact.getAttributeValue(null, "unitRef");
	    unit.setKey(unitKey);
	    createdFact.setUnit(unit);

	    // Footnote ID
	    String id = newFact.getAttributeValue(null, "id");
	    if (id != null)
	    {
		ArrayList<String> footNoteList = new ArrayList<String>();

		Iterator footNoteIterator = UbmXbrlHelper.getFootNotes(newFact.getValueObject(), dts);

		while (footNoteIterator.hasNext())
		{
		    ValuedXbrlInstanceDomainImpl currentNote =(ValuedXbrlInstanceDomainImpl) footNoteIterator.next();
		    
		    footNoteList.add((String) currentNote.getValue());
		}
		
		createdFact.setFootNotes(footNoteList);
		
	    }

	} 
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingXBRLFact, e);
	}
	return createdFact;
    }

    /**
     * Crea un contexto con la informaicón que le llega como parametro
     * @param newContext
     * @param instance
     * @return
     * @throws XBRLPGCException
     */
    private IXbrlContext createXBRLContext(Context newContext, IXbrlInstance instance) throws XBRLPGCException
    {
	IXbrlContext createdContext = null;
	IDTS dts = (IDTS) instance.getDts();
	try
	{
	    String contextId = newContext.getAttributeValue(null, "id");

	    boolean dimensional = newContext.getDimensions().getCount() > 0;

	    createdContext = XbrlApiFactory.createContext();
	    if (dimensional)
	    {
		IDTSResultSet dimensions = newContext.getDimensions();
		ValuedXbrlSchemaDomainImpl dimensionValue = (ValuedXbrlSchemaDomainImpl) dimensions.getEnumerator().next();
		ValuedXbrlInstanceDomainImpl scenarioValue = UbmXbrlHelper.getScenario(newContext.getValueObject(), dts);
		if (scenarioValue == null)
		{
		    throw new XBRLPGCException(XBRLPGCException.errorContextWithoutScenario, new String[]{contextId});
		}
		String domainMember = UbmXbrlHelper.getExplicitMember(dts, scenarioValue);
		createdContext.setMember(domainMember);
		createdContext.setDimension(dimensionValue.getName());
	    }

	    createdContext.setId(contextId);
	    createdContext.setScenarioIsDimensional(dimensional);

	    ValuedXbrlInstanceDomainImpl period = UbmXbrlHelper.getPeriod(newContext, dts);

	    createdContext.setPeriodObject(period);

	    if (UbmXbrlHelper.isDurationPeriod(period, dts))
	    {
		createdContext.setPeriodStartDate(UbmXbrlHelper.getPeriodStart(period.getValueObject(), dts));
		createdContext.setPeriodEndDate(UbmXbrlHelper.getPeriodEnd(period.getValueObject(), dts));
	    } else
	    {
		createdContext.setPeriodInstant(UbmXbrlHelper.getPeriodInstant(period.getValueObject(), dts));
	    }

	    ValuedXbrlInstanceDomainImpl entity = UbmXbrlHelper.getEntity(newContext, dts);
	    ValuedXbrlInstanceDomainImpl identifier = UbmXbrlHelper.getIdentifier(entity.getValueObject(), dts);
	    createdContext.setEntityIdentifierValue(identifier.getValue());
	    createdContext.setEntityIdentifierSchema(UbmXbrlHelper.getIdentifierScheme(identifier.getValueObject(), dts));
	    createdContext.setContextObject(newContext);

	} 
	catch (XBRLPGCException ex)
	{
		logger.error(ex);
	    throw ex;
	} 
	catch (Exception e)
	{

	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCreatingXBRLContext, e);
	}
	return createdContext;
    }

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#isTuple(es.inteco.xbrl.pgc.apifacade.IXbrlFact, es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public boolean isTuple(IXbrlFact fact, IXbrlInstance instance) throws XBRLPGCException
    {
	try
	{
	    return UbmXbrlHelper.isTupleFact(((ValuedXbrlInstanceDomainImpl) fact.getFactObject()).getValueObject(), (IDTS) instance.getDts());

	} 
	catch (Exception e)
	{

	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorCheckingFactIsTuple, e);
	}
    }

    /**
     * Busca un context partiendo de la información que le llega como parametro
     * @param fact
     * @param contextTable
     * @return
     * context encontrado
     * @throws XBRLPGCException
     */
    private IXbrlContext getContext(ValuedXbrlInstanceDomainImpl fact, Hashtable<String, IXbrlContext> contextTable) throws XBRLPGCException
    {
	IXbrlContext contextResp = null;
	try
	{
	    String contextRef = fact.getAttributeValue(null, "contextRef");
	    if (contextRef != null)
	    {
		contextResp = contextTable.get(contextRef);
	    }

	} 

	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorExtractingContextFromFact, e);
	}
	return contextResp;
    }

    /**
     * Crea facts partiendo de una lista
     * @param iterFacts
     * @param instance
     * @param contextTable
     * @param dts
     * @return
     * lista de facts creados
     * @throws XBRLPGCException
     */
    private ArrayList<IXbrlFact> getFactListFromIterator(Iterator iterFacts, IXbrlInstance instance, Hashtable<String, IXbrlContext> contextTable,
	    IDTS dts) throws XBRLPGCException
    {
	ArrayList<IXbrlFact> listFactResp = new ArrayList<IXbrlFact>();
	try
	{
	    while (iterFacts.hasNext())
	    {

		ValuedXbrlInstanceDomainImpl currentFact = (ValuedXbrlInstanceDomainImpl) iterFacts.next();

		IXbrlFact newFact = createXBRLFact(currentFact, dts);
		newFact.setContext(getContext(currentFact, contextTable));

		if (!isTuple(newFact, instance))
		{
		    listFactResp.add(newFact);
		}

	    }

	} 
	catch (XBRLPGCException ex)
	{
	    throw ex;
	}
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorTranslatingFactsFromIteratorToTable, e);
	}

	return listFactResp;
    }

    
    
    /**
     * Crea tuples partiendo de una lista
     * @param iterFacts
     * @param instance
     * @param contextTable
     * @param dts
     * @return
     * lista facts de tipo tupla generada
     * @throws XBRLPGCException
     */
    private ArrayList<IXbrlFact> getTupleListFromIterator(Iterator iterFacts, IXbrlInstance instance, Hashtable<String, IXbrlContext> contextTable,
	    IDTS dts) throws XBRLPGCException
    {
	ArrayList<IXbrlFact> tupleListResp = new ArrayList<IXbrlFact>();
	try
	{
	    while (iterFacts.hasNext())
	    {

		ValuedXbrlInstanceDomainImpl currentFact = (ValuedXbrlInstanceDomainImpl) iterFacts.next();

		IXbrlFact newFact = createXBRLFact(currentFact, dts);
		newFact.setContext(getContext(currentFact, contextTable));

		if (isTuple(newFact, instance))
		{
		    tupleListResp.add(newFact);
		}

	    }

	} 
	catch (XBRLPGCException ex)
	{
	    throw ex;
	}
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorTranslatingFactsFromIteratorToTable, e);
	}

	return tupleListResp;
    }
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#getGlobalFacts(es.inteco.xbrl.pgc.apifacade.IXbrlInstance, es.inteco.xbrl.pgc.apifacade.IXbrlContext, java.util.Hashtable)
     */
    public ArrayList<IXbrlFact> getGlobalFacts(IXbrlInstance instance, IXbrlContext context, Hashtable<String, IXbrlContext> contextTable)
	    throws XBRLPGCException
    {
	try
	{
	    Iterator iterGlobalFacts = UbmXbrlHelper.getGlobalFacts((IDTS) instance.getDts(), ((ValuedXbrlInstanceDomainImpl) instance.getInstance())
		    .getValueObject(), ((Context) context.getContextObject()).getValueObject());

	    return getFactListFromIterator(iterGlobalFacts, instance, contextTable, (IDTS) instance.getDts());

	} 
	catch (XBRLPGCException ex)
	{
	    throw ex;
	}
	catch (Exception e)
	{
	    // throw new XBRLPGCException(e.getMessage(), e);
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorGettingGlobalFactsTable, e);
	}
    }

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#getGlobalFacts(es.inteco.xbrl.pgc.apifacade.IXbrlInstance, es.inteco.xbrl.pgc.apifacade.IXbrlFact, java.util.Hashtable)
     */
    public ArrayList<IXbrlFact> getGlobalFacts(IXbrlInstance instance, IXbrlFact tupleFact, Hashtable<String, IXbrlContext> contextTable)
	    throws XBRLPGCException
    {
	try
	{
	    Iterator iterGlobalFacts = UbmXbrlHelper.getTupleChilds((IDTS) instance.getDts(), ((ValuedXbrlInstanceDomainImpl) tupleFact.getFactObject())
		    .getValueObject());

	    return getFactListFromIterator(iterGlobalFacts, instance, contextTable, (IDTS) instance.getDts());

	} 
	catch (XBRLPGCException ex)
	{
	    throw ex;
	}
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorGettingGlobalTupleChildsTable, e);
	}
    }

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#getGlobalTuples(es.inteco.xbrl.pgc.apifacade.IXbrlInstance, java.util.Hashtable)
     */
    public ArrayList<IXbrlFact> getGlobalTuples(IXbrlInstance instance, Hashtable<String, IXbrlContext> contextTable) throws XBRLPGCException
    {
	try
	{
	    Iterator iterGlobalTuples = UbmXbrlHelper.getGlobalTuples((IDTS) instance.getDts(), ((ValuedXbrlInstanceDomainImpl) instance.getInstance())
		    .getValueObject());

	    return getTupleListFromIterator(iterGlobalTuples, instance, contextTable, (IDTS) instance.getDts());

	}
	catch (XBRLPGCException ex)
	{
	    throw ex;
	}
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorGettingGlobalTupleTable, e);
	}
    }

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#getGlobalTuples(es.inteco.xbrl.pgc.apifacade.IXbrlInstance, es.inteco.xbrl.pgc.apifacade.IXbrlFact, java.util.Hashtable)
     */
    public ArrayList<IXbrlFact> getGlobalTuples(IXbrlInstance instance, IXbrlFact tupleParent, Hashtable<String, IXbrlContext> contextTable)
	    throws XBRLPGCException
    {
	try
	{
	    Iterator iterTupleChilds = UbmXbrlHelper.getTupleChilds((IDTS) instance.getDts(), ((ValuedXbrlInstanceDomainImpl) tupleParent.getFactObject()));

	    ArrayList<IXbrlFact> tupleListResp = new ArrayList<IXbrlFact>();

	    while (iterTupleChilds.hasNext())
	    {
		ValuedXbrlInstanceDomainImpl currentFact = (ValuedXbrlInstanceDomainImpl) iterTupleChilds.next();
		if (UbmXbrlHelper.isTupleFact(currentFact.getValueObject(), (IDTS) instance.getDts()))
		{
		    IXbrlFact newFact = createXBRLFact(currentFact, (IDTS) instance.getDts());
		    newFact.setContext(getContext(currentFact, contextTable));
		    tupleListResp.add(newFact);
		}

	    }
	    return tupleListResp;

	}
	catch (XBRLPGCException ex)
	{
	    throw ex;
	}
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorGettingGlobalTupleTableByParent, e);
	}
    }

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#getContexts(es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public Hashtable<String, IXbrlContext> getContexts(IXbrlInstance instance) throws XBRLPGCException
    {
	Hashtable<String, IXbrlContext> contextTable = new Hashtable<String, IXbrlContext>();
	try
	{
	    Iterator iterContexts = UbmXbrlHelper.getContexts((IDTS) instance.getDts(), ((ValuedXbrlInstanceDomainImpl) instance.getInstance())
		    .getValueObject());

	    while (iterContexts.hasNext())
	    {
		Context currentContext = (Context) iterContexts.next();
		IXbrlContext newContext = createXBRLContext(currentContext, instance);
		contextTable.put(newContext.getId(), newContext);
	    }

	}catch (XBRLPGCException ex)
	{
	    throw ex;
	
	} catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorGettingContextTable, e);
	}
	return contextTable;
    }

    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#getUnits(es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public ArrayList<IXbrlUnit> getUnits(IXbrlInstance instance) throws XBRLPGCException
    {
	ArrayList<IXbrlUnit> arrayListResp = new ArrayList<IXbrlUnit>();

	try
	{
	    Iterator iterUnits = UbmXbrlHelper.getContexts((IDTS) instance.getDts(), ((ValuedXbrlInstanceDomainImpl) instance.getInstance()).getValueObject());

	    while (iterUnits.hasNext())
	    {
		Object unitObject = iterUnits.next();
		IXbrlUnit unit = XbrlApiFactory.createUnit();
		unit.setCode(UbmXbrlHelper.getMeasureName(((ValuedXbrlInstanceDomainImpl) unit).getValueObject(), (IDTS) instance.getDts()));
		unit.setUnitObject(unitObject);

		arrayListResp.add(unit);
	    }

	} 
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorGettingUnitList, e);
	}
	return arrayListResp;
    }
    
    /* (non-Javadoc)
     * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#closeInstance(es.inteco.xbrl.pgc.apifacade.IXbrlInstance)
     */
    public  void closeInstance(IXbrlInstance instance) throws XBRLPGCException
    {
	try{
	    IDTS dts = (IDTS)instance.getDts();
	    dts.unlock();
	    dts.close();
	} 
	catch (Exception e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.closingInstance, e);
	}
    }
    
    
    
    
    
    
	
	/* (non-Javadoc)
	 * @see es.inteco.xbrl.pgc.apifacade.IXbrlApiFacade#checkErrorListSeverity(java.lang.String[])
	 */
	public boolean checkErrorListSeverity(String[] messageList){
		boolean returnValue=true;
		
		for (int i = 0; i < messageList.length; i++) 
		{
			String single=messageList[i];
			if ( !(checkSigleError(single)) ){
				returnValue=false;
				break;
			}
		}
		
	
		
		return returnValue;
	}
	
	/**
	 * @param error
	 * Mensaje de tipo memo que será evaluado a fin de establecer si su severidad es de tipo error
	 * @return
	 * Verdadero si el mensaje es un error ó fatal error, falso si es warning, info...
	 */
	private boolean checkSigleError(String error){
		boolean returnValue=true;

		try{
			javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance(); 
			javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder(); 
			org.w3c.dom.Document document = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(error)));
			
			
			//org.apache.xerces.dom.DeepNodeListImpl nodeMessagesList =(org.apache.xerces.dom.DeepNodeListImpl)document.getElementsByTagName("messages");
			
			org.w3c.dom.NodeList nodeMessagesList =document.getElementsByTagName("messages");
			
			for (int i = 0; i < nodeMessagesList.getLength(); i++) {
				org.w3c.dom.Node nodeMessages = nodeMessagesList.item(i);
				org.w3c.dom.NodeList messageList = nodeMessages.getChildNodes(); 
				for (int j = 0; j < messageList.getLength(); j++) 
				{
					org.w3c.dom.Node nodeMessage = messageList.item(j);
					if (nodeMessage.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

						org.w3c.dom.NamedNodeMap attrs = nodeMessage.getAttributes();
						for (int z = 0; z < attrs.getLength(); z++) {
							org.w3c.dom.Attr attr = (org.w3c.dom.Attr)attrs.item(z);
							if (attr.getName().equals("type"))
							{ 
								if ( (attr.getValue().trim().equalsIgnoreCase("Error")) || attr.getValue().trim().equalsIgnoreCase("Fatal Error") ){
									returnValue=false;
									break;
								}
								
							}
						}

						
					}
					if (!(returnValue)){break;}

				}	             

				if (!(returnValue)){break;}
			}






		}
		catch(Exception e){
			returnValue=false;
			
		}  	

		return returnValue;
	}
    
    
    
    


}
