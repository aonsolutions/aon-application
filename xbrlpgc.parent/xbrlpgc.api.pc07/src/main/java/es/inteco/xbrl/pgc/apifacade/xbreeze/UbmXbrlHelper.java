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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.xerces.dom.DeferredElementNSImpl;
import org.w3c.dom.Node;

import ubmatrix.xbrl.common.exception.src.CoreException;
import ubmatrix.xbrl.common.formatter.src.FormatterException;
import ubmatrix.xbrl.common.memo.registry.src.DiscreteMemoRegistry;
import ubmatrix.xbrl.common.memo.src.IMemo;
import ubmatrix.xbrl.common.src.IDTS;
import ubmatrix.xbrl.common.src.IDTSNode;
import ubmatrix.xbrl.common.src.ILocationHandle;
import ubmatrix.xbrl.common.src.IPrefixResolver;
import ubmatrix.xbrl.common.src.IXbrlDomain;
import ubmatrix.xbrl.common.src.LocationHandleType;
import ubmatrix.xbrl.common.utility.src.CommonUtilities;
import ubmatrix.xbrl.common.utility.src.ResetableIterator;
import ubmatrix.xbrl.common.utility.src.Utility;
import ubmatrix.xbrl.common.xml.xPath.src.IExtensionFunctionArgs;
import ubmatrix.xbrl.domain.query.src.IDTSQuery;
import ubmatrix.xbrl.domain.src.ValuedXbrlInstanceDomainImpl;
import ubmatrix.xbrl.domain.xbrl21Domain.dts.src.DTSDiscoverer;
import ubmatrix.xbrl.domain.xbrl21Domain.dts.src.DTSPath;
import ubmatrix.xbrl.domain.xbrl21Domain.dts.src.XPath20;
import ubmatrix.xbrl.domain.xbrl21Domain.dts.src.XQuery10;
import ubmatrix.xbrl.domain.xbrl21Domain.src.XbrlDomainUri;
import ubmatrix.xbrl.extensions.extensionFunctionServices.src.ExtensionFunctionArgs;
import ubmatrix.xbrl.extensions.extensionFunctionServices.src.ExtensionFunctionException;
import ubmatrix.xbrl.extensions.extensionFunctionServices.src.ExtensionFunctionHelper;
import ubmatrix.xbrl.extensions.ubmExtensions.src.UBMExtensions_Instance;
import ubmatrix.xbrl.extensions.ubmExtensions.src.UBMExtensions_Taxonomy;
import ubmatrix.xbrl.extensions.xfExtensions.src.XFExtensions_Instance;
import ubmatrix.xbrl.locationController.factory.src.LocationHandleFactory;


/**
 *
 * Esta clase contiene métodos de utilidad y de apoyo para los procesos de trabajo con XBREEZE.
 *
 *<br><br>
 * <b>Proyecto</b>: API XBRL-PGC2007 - Grupo de utilidades y librerías en código abierto para facilitar 
 *                             la integración del formato XBRL en las herramientas software de gestión de  terceros
 *                             aislándose de la complejidad en el procesamiento del modelo de datos de las taxonomías.
 *                             Ayudando de esta forma a las empresas finales en la labor de realización de informes XBRL
 *                             y asegurar el éxito de implantación del nuevo Plan General de Contabilidad 2007 en formato XBRL
 *
 *
 * @version 1.0, 14/01/2009
 * @author difusioncalidad@inteco.es
 *
 */


public abstract class UbmXbrlHelper
{

    static XFExtensions_Instance extInstance = new XFExtensions_Instance();
    static UBMExtensions_Instance ubmExtInstance = new UBMExtensions_Instance();

    private final static String c_external_global_item_facts = "global-item-facts";
    private final static String c_external_global_tuple_facts = "global-tuple-facts";

    private static String s_GlobalItemFactQuery = CommonUtilities.formatString("/'{0}'", new Object[] { XbrlDomainUri.c_GlobalItemFact });
    private static String s_GlobalTupleFactQuery = CommonUtilities.formatString("/'{0}'", new Object[] { XbrlDomainUri.c_GlobalTupleFact });



    /**
     * This method loads a file from the specified fileUri. A fileType,
     * indicating whether it is a Taxonomy, Linkbase or Instance document, is
     * also passed.
     * 
     * After successful load, this method returns the DTS.
     * 
     * @param fileUri
     *            : The file to be loaded
     * 
     * @param fileType
     *            : Indicating whether the file is Taxonomy or Instance or
     *            Linkbase
     * 
     * @return The DTS
     */
    public static IDTS load(String fileUri, int fileType, UbmXbrlMessages messages) throws FormatterException
    {

	IDTS dts = null;

	DiscreteMemoRegistry discreteMemoRegistry = new DiscreteMemoRegistry();
	long cookie = discreteMemoRegistry.attach(messages);

	ILocationHandle handle = LocationHandleFactory.createNewLocationHandle(null, fileUri, fileType);

	switch (fileType)
	{

	case LocationHandleType.c_Taxonomy:
	    handle.setLocationHandleType(LocationHandleType.c_Taxonomy);
	    break;

	case LocationHandleType.c_Linkbase:
	    handle.setLocationHandleType(LocationHandleType.c_Linkbase);
	    break;

	case LocationHandleType.c_Instance:
	    handle.setLocationHandleType(LocationHandleType.c_Instance);
	    break;
	}

	handle.setMemoRegistry(discreteMemoRegistry);

	try
	{
	    dts = DTSDiscoverer.establishEntryPoint(handle);

	} catch (CoreException ce)
	{
	    discreteMemoRegistry.add((IMemo) ce);

	} finally
	{

	    if (dts != null)
		discreteMemoRegistry.detach(cookie);

	}

	return dts;
    }

    /**
     * Invoke a command on a domainobject
     * 
     * @param dts
     *            : The active DTS
     * 
     * @param workingDO
     *            : The context domain object
     * 
     * @param methodName
     *            : The method name to invoke, e.g., "CreateFact"
     * 
     * @param methodArgs
     *            : The args to pass to the method
     * 
     * @return A domain object
     */
    protected static Object invokeDO(IDTS dts, IXbrlDomain workingDO, String methodName, Object[] methodArgs) throws CoreException
    {

	Object retObject = null;

	if (methodArgs == null)
	    methodArgs = new Object[] { null };

	retObject = workingDO.invoke(dts, methodName, methodArgs);

	return retObject;
    }

    /**
     * Find the correct language to use for parser
     * 
     * @param query
     *            : query
     * 
     * @param dts
     *            : Active DTS
     * 
     * @param prefixMap
     *            : Prefix/namespaceURI pairs
     * 
     * @return Compiled expression
     */
    protected static IDTSQuery findPath(IDTSNode node, HashMap<String, String> prefixMap, String query)
    {

	IDTSQuery path = null;

	ILocationHandle lh = node.getLocationHandle();
	IPrefixResolver resolver = lh.getPrefixResolver();
	String savedQuery = query;

	try
	{
	    path = DTSPath.compile(query);

	    if (path != null)
		return path;

	    else
	    {

		updateResolver(resolver, prefixMap);
		path = new XQuery10(resolver, null, savedQuery);

		if (path != null)
		    return path;

		else
		{
		    updateResolver(resolver, prefixMap);
		    path = new XPath20(resolver, null, savedQuery);
		    return path;

		}

	    }

	} catch (Exception ex)
	{
	    System.out.println(ex.getMessage());
	}

	return null;
    }

    /**
     * Update the resolver with a new collection of prefix/namespace pairs
     * 
     * @param resolver
     *            : Prefix resolver
     * 
     * @param prefixMap
     *            : New prefixes to add
     */
    protected static void updateResolver(IPrefixResolver resolver, HashMap<String, String> prefixMap)
    {

	if (resolver == null && prefixMap != null)
	    resolver = new ubmatrix.xbrl.common.xml.xPath.xerces.src.XercesPrefixResolverImpl();
	else if (prefixMap == null && resolver == null)
	    return;

	if (prefixMap.size() > 0)
	{
	    Iterator itr = ResetableIterator.getIterator(prefixMap.entrySet());
	    Utility.resetIterator(itr);

	    while (itr.hasNext())
	    {

		Map.Entry me = (Map.Entry) itr.next();
		String prefix = (String) me.getKey();
		String ns = (String) me.getValue();

		resolver.addNamespace(prefix, ns);
	    }
	}
    }

    /*
     * This is a small utility method that dispalys all the memos
     */
    /**
     * This is a small utility method that dispalys all the memos
     * @param memos
     * @throws FormatterException
     */
    public static void displayMemos(ArrayList memos) throws FormatterException
    {

	if (memos.size() > 0)
	{

	    System.out.println("Following errors occurred:");

	    // print any error memos
	    for (int i = 0; i < memos.size(); ++i)
	    {

		IMemo memo = (IMemo) memos.get(i);
		System.out.println(memo.toString());
	    }
	}
    }

    
    /**
     * Devuelve la lista de memos como string
     * @param memos
     * @return
     * texto compuesto por la concatenación de los memos del parametro de entrada
     * @throws FormatterException
     */
    public static String getStringMemos(ArrayList<IMemo> memos) throws FormatterException
    {

	StringBuffer sbMemos = new StringBuffer();
	
	if (memos.size() > 0)
	{
	    for (int i = 0; i < memos.size(); ++i)
	    {

		IMemo memo =  memos.get(i);
		sbMemos.append(memo.toString());
		sbMemos.append(";");
	    }
	}
	
	return sbMemos.toString();
    }
    
    
    
    /**
     * Comprueba si el periodo es de tipo duration
     * @param period
     * @param dts
     * @return
     * indicador de si el periodo es de tipo duration
     * @throws CoreException
     */
    static boolean isDurationPeriod(IXbrlDomain period, IDTS dts) throws CoreException
    {

	boolean booleanResp = false;

	IExtensionFunctionArgs args = new ExtensionFunctionArgs("is-start-end-period", new Object[] { period }, dts, null, null);

	booleanResp = (Boolean) extInstance.invoke(args);

	return booleanResp;

    }

    /**
     * Devuelve un objeto del tipo ValuedXbrlInstanceDomainImpl encontrado a través del contexto y del dts
     * @param context
     * @param dts
     * @return
     * objeto del tipo ValuedXbrlInstanceDomainImpl
     * @throws CoreException
     */
    static ValuedXbrlInstanceDomainImpl getPeriod(IXbrlDomain context, IDTS dts) throws CoreException
    {
	ValuedXbrlInstanceDomainImpl period = null;

	XFExtensions_Instance extInstance = new XFExtensions_Instance();

	IExtensionFunctionArgs args = new ExtensionFunctionArgs("period", new Object[] { context }, dts, null, null);

	Iterator iterPeriod = (Iterator) extInstance.invoke(args);

	if (iterPeriod.hasNext())
	{
	    period = (ValuedXbrlInstanceDomainImpl) iterPeriod.next();
	}

	return period;

    }

    /**
     * Devuelve la fecha de comienzo de un periodo
     * @param context
     * @param dts
     * @return
     * fecha de comienzo
     * @throws CoreException
     */
    static Date getPeriodStart(Object context, IDTS dts) throws CoreException
    {
	Date periodStart = null;

	IExtensionFunctionArgs args = new ExtensionFunctionArgs("period-start", new Object[] { context }, dts, null, null);

	periodStart = (Date) extInstance.invoke(args);

	return periodStart;

    }

    /**
     * Devuelve la fecha de fin de un periodo
     * @param context
     * @param dts
     * @return
     * fecha de fin
     * @throws CoreException
     */
    static Date getPeriodEnd(Object context, IDTS dts) throws CoreException
    {
	Date periodEnd = null;

	IExtensionFunctionArgs args = new ExtensionFunctionArgs("period-end", new Object[] { context }, dts, null, null);

	periodEnd = (Date) extInstance.invoke(args);

	return periodEnd;

    }

    /**
     * Devuelve la fecha tipo instant de un periodo
     * @param period
     * @param dts
     * @return
     * fecha tipo instant
     * @throws CoreException
     */
    static Date getPeriodInstant(Object period, IDTS dts) throws CoreException
    {
	Date periodInstant = null;

	IExtensionFunctionArgs args = new ExtensionFunctionArgs("period-instant", new Object[] { period }, dts, null, null);

	periodInstant = (Date) extInstance.invoke(args);

	return periodInstant;

    }

    /**
     * Devuelve la entidad de un contexto bajo un tipo ValuedXbrlInstanceDomainImpl
     * @param context
     * @param dts
     * @return
     * entidad del contexto
     * @throws CoreException
     */
    static ValuedXbrlInstanceDomainImpl getEntity(IXbrlDomain context, IDTS dts) throws CoreException
    {
	ValuedXbrlInstanceDomainImpl entity = null;

	IExtensionFunctionArgs args = new ExtensionFunctionArgs("entity", new Object[] { context }, dts, null, null);

	Iterator iterEntity = (Iterator) extInstance.invoke(args);

	if (iterEntity.hasNext())
	{
	    entity = (ValuedXbrlInstanceDomainImpl) iterEntity.next();
	}

	return entity;

    }

    /**
     * Devuelve el identifier de una entity
     * @param entity
     * @param dts
     * @return
     * identifier
     * @throws CoreException
     */
    static ValuedXbrlInstanceDomainImpl getIdentifier(Object entity, IDTS dts) throws CoreException
    {
	ValuedXbrlInstanceDomainImpl identifier = null;

	IExtensionFunctionArgs args = new ExtensionFunctionArgs("identifier", new Object[] { entity }, dts, null, null);

	Iterator iterIdentifier = (Iterator) extInstance.invoke(args);

	if (iterIdentifier.hasNext())
	{
	    identifier = (ValuedXbrlInstanceDomainImpl) iterIdentifier.next();
	}

	return identifier;

    }

    /**
     * Devuelve el scheme de un identifier
     * @param identifier
     * @param dts
     * @return
     * scheme identifier
     * @throws CoreException
     */
    static String getIdentifierScheme(Object identifier, IDTS dts) throws CoreException
    {
	String identifierScheme = null;

	IExtensionFunctionArgs args = new ExtensionFunctionArgs("identifier-scheme", new Object[] { identifier }, dts, null, null);

	identifierScheme = (String) extInstance.invoke(args);

	return identifierScheme;

    }

    /**
     * Devuelve el name de una measure perteneciente a una unit
     * @param unit
     * @param dts
     * @return
     * name del measure
     * @throws CoreException
     */
    static String getMeasureName(Object unit, IDTS dts) throws CoreException
    {
	String measureName = null;

	IExtensionFunctionArgs args = new ExtensionFunctionArgs("measure-name", new Object[] { unit }, dts, null, null);

	measureName = (String) extInstance.invoke(args);

	return measureName;

    }
    
      
    
    /**
     * Devuelve un indicador marcando el fact como tupla
     * @param factObject
     * @param dts
     * @return
     * (true/false) indicando si el fact es una tupla
     * @throws CoreException
     */
    static boolean isTupleFact(Object factObject, IDTS dts) throws CoreException
    {

	boolean booleanResp = false;

	IExtensionFunctionArgs args = new ExtensionFunctionArgs("is-tuple-fact", new Object[] { factObject }, dts, null, null);

	booleanResp = (Boolean) ubmExtInstance.invoke(args);

	return booleanResp;

    }
    
    /**
     * Devuelve un indicador de si el object es de tipo fact
     * @param factObject
     * @param dts
     * @return
     * (true/false) indicando si el object es de tipo fact 
     * @throws CoreException
     */
    static boolean isFact(Object factObject, IDTS dts) throws CoreException
    {

	boolean booleanResp = false;

	IExtensionFunctionArgs args = new ExtensionFunctionArgs("is-fact", new Object[] { factObject }, dts, null, null);

	booleanResp = (Boolean) ubmExtInstance.invoke(args);

	return booleanResp;

    }
    
    

    /**
     * Devuelve una lista de facts
     * @param dts
     * @param instance
     * @param context
     * @return
     * Iterator lista de facts
     * @throws CoreException
     */
    static Iterator getGlobalFacts(IDTS dts, Object instance, Object context) throws CoreException
    {
	IExtensionFunctionArgs args = new ExtensionFunctionArgs(c_external_global_item_facts, new Object[] { instance, context, "*" }, dts, null, null);

	Iterator iterLocalFacts = handleFacts(s_GlobalItemFactQuery, args);

	return iterLocalFacts;

    }
    
    /**
     * Devuelve los hijos de una tupla
     * @param dts
     * @param tuple
     * @return
     * Iterator lista de hijos de una tupla
     * @throws CoreException
     */
    static Iterator getTupleChilds(IDTS dts, Object tuple) throws CoreException
    {
	IExtensionFunctionArgs args = new ExtensionFunctionArgs("tuple-children", new Object[] {tuple }, dts, null, null);

	Iterator iterLocalFacts = (Iterator) ubmExtInstance.invoke(args);
	
	return iterLocalFacts;

    }
    

    /**
     * Devuelve una lista de tuplas
     * @param dts
     * @param instance
     * @return
     * lista de tuplas
     * @throws CoreException
     */
    static Iterator getGlobalTuples(IDTS dts, Object instance) throws CoreException
    {
	IExtensionFunctionArgs args = new ExtensionFunctionArgs(c_external_global_tuple_facts, new Object[] { instance}, dts, null, null);

	Iterator iterLocalFacts = handleFacts(s_GlobalTupleFactQuery, args);

	return iterLocalFacts;

    }

    
    
    /**
     * Devuelve una lista de context
     * @param dts
     * @param instance
     * @return
     * Iterator lista de context
     * @throws CoreException
     */
    static Iterator getContexts(IDTS dts, Object instance) throws CoreException
    {
	IExtensionFunctionArgs args = new ExtensionFunctionArgs("contexts", new Object[] { instance }, dts, null, null);

	Iterator iterLocalFacts = (Iterator) ubmExtInstance.invoke(args);

	return iterLocalFacts;

    }

    /**
     * Devuelve una lista de units
     * @param dts
     * @param instance
     * @return
     * iterator lista de units
     * @throws CoreException
     */
    static Iterator getUnits(IDTS dts, Object instance) throws CoreException
    {
	IExtensionFunctionArgs args = new ExtensionFunctionArgs("units", new Object[] { instance }, dts, null, null);

	Iterator iterLocalFacts = (Iterator) ubmExtInstance.invoke(args);

	return iterLocalFacts;
    }
    
    
    /**
     * Devuelve el scenario de un context
     * @param context
     * @param dts
     * @return
     * scenario del context
     * @throws CoreException
     */
    static ValuedXbrlInstanceDomainImpl getScenario(Object context, IDTS dts) throws CoreException
    {
	ValuedXbrlInstanceDomainImpl scenario = null;
	
	
	String s_scenarioFromContextQuery = CommonUtilities.formatString("/+'{0}'/'{1}'", new Object[] {
		XbrlDomainUri.c_ContextSpecifiesScenario,
		XbrlDomainUri.c_Scenario});

	IDTSQuery s_scenarioFromContextPath = new DTSPath(s_scenarioFromContextQuery);

	IExtensionFunctionArgs args = new ExtensionFunctionArgs("", new Object[] { context }, dts, null, null);

	Iterator iterScenario = ExtensionFunctionHelper.handleDTSFindFromContextNode(s_scenarioFromContextPath, args);
	

	if (iterScenario.hasNext())
	{
	    scenario = (ValuedXbrlInstanceDomainImpl) iterScenario.next();
	}

	return scenario;

    }
    
    
	

    /**
     * Devuelve el explicitMember de un scenario
     * @param dts
     * @param scenario
     * @return
     * explicitMember del scenario
     * @throws CoreException
     */
    static String getExplicitMember (IDTS dts, ValuedXbrlInstanceDomainImpl scenario) throws CoreException
    {
	String explicitMember = null;
	
	DeferredElementNSImpl scenarioElement = (DeferredElementNSImpl)scenario.getValueObject();
	Node explicitMemberElement = scenarioElement.getChildNodes().item(0);
	explicitMember = explicitMemberElement.getFirstChild().getNodeValue();
	
	return explicitMember;
    }
    
    
    /**
     * Devuelve los footnotes de un fact
     * @param factObject
     * @param dts
     * @return
     * footnotes del fact
     * @throws CoreException
     */
    static Iterator getFootNotes(Object factObject, IDTS dts) throws CoreException
    {

	Iterator footNotes = null;

	IExtensionFunctionArgs args = new ExtensionFunctionArgs("footnotes", new Object[] { factObject }, dts, null, null);

	footNotes = (Iterator)ubmExtInstance.invoke(args);

	return footNotes;

    }
    
    
    /**
     * Devuelve los facts manejados por la query
     * @param factQuery
     * @param args
     * @return
     * iterator lista de facts
     * @throws CoreException
     */
    public static Iterator handleFacts(String factQuery, IExtensionFunctionArgs args) throws CoreException
    {
	Object[] parameters = args.getArguments();
	IDTS dts = args.getDTS();
	String factFromInstanceQuery = null;
	String context = null;
	String unit = null;

	switch (parameters.length)
	{
	case 1:
	{
	    factFromInstanceQuery = CommonUtilities.formatString("/+'{0}'{1}", new Object[] { XbrlDomainUri.c_InstanceSpecifiesFact, factQuery });
	    break;
	}
	case 3:
	{
	    if (parameters[1] instanceof String)
	    {
		context = (String) parameters[1];
		if (context.equals("*"))
		{
		    context = null;
		}
	    } else
	    {
		Object vo = parameters[1];

		// Get the context
		IXbrlDomain c = ExtensionFunctionHelper.lookupDomainObject(dts, vo);

		if (c == null)
		    throw new ExtensionFunctionException(ExtensionFunctionException.c_InvalidValueObject, new Object[] { vo.toString() });

		context = c.getXmlId();
	    }

	    if (parameters[2] instanceof String)
	    {
		unit = (String) parameters[2];
		if (unit.equals("*"))
		{
		    unit = null;
		}
	    } else
	    {
		Object vo = parameters[2];

		// Get the unit
		IXbrlDomain u = ExtensionFunctionHelper.lookupDomainObject(dts, vo);

		if (u == null)
		    throw new ExtensionFunctionException(ExtensionFunctionException.c_InvalidValueObject, new Object[] { vo.toString() });

		unit = u.getXmlId();
	    }

	    factFromInstanceQuery = CommonUtilities.formatString("/+'{0}'{1}[(attr('contextRef')=='{2}')]", new Object[] {
		    XbrlDomainUri.c_InstanceSpecifiesFact, factQuery, context });
	    break;
	}
	}

	IDTSQuery factFromInstancePath = new DTSPath(factFromInstanceQuery);
	return ExtensionFunctionHelper.handleDTSFindFromContextNode(factFromInstancePath, args);
    }
    
    
    
    /**
     * Devuelve un indicador de si el concept es substitutionGroup
     * @param dts
     * @param concept
     * @return
     * (true/false) indicando si el concept es substitutionGroup
     * @throws CoreException
     */
    static boolean isSustitutionGroup(IDTS dts, Object concept) throws CoreException
    {
	IExtensionFunctionArgs args = new ExtensionFunctionArgs("", new Object[] { concept}, dts, null, null);

	boolean result = ExtensionFunctionHelper.isInSubstitutionGroup("http://www.xbrl.org/2003/instance#monetaryItemType", args);

	return result;

    }

    
    
    
    
    
    
}
