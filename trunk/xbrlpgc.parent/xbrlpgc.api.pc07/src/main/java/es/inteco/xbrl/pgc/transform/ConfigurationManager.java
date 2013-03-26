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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import es.inteco.xbrl.pgc.errors.exceptions.DocumentNotFoundException;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLPGCException;
import es.inteco.xbrl.pgc.errors.exceptions.XSDValidatorException;
import es.inteco.xbrl.pgc.transform.config.ConfigReport;
import es.inteco.xbrl.pgc.transform.config.Global;
import es.inteco.xbrl.pgc.transform.config.ConfigModule;
import es.inteco.xbrl.pgc.transform.config.NsItem;
import es.inteco.xbrl.pgc.transform.config.SchemaRef;
import es.inteco.xbrl.pgc.transform.config.Unit;
import es.inteco.xbrl.pgc.transform.maps.ConceptMap;
import es.inteco.xbrl.pgc.transform.maps.Statement;
import es.inteco.xbrl.pgc.utils.XbrlApiConfiguration;




/**
 *
 *
 * Clase de configuración usada para consultar la información necesaria en los procesos
 * de transformación de formato común a XBRL y viceversa.
 * <br /><br />
 * Mediante esta clase se accede al fichero de configuración global.xml y a los mapas
 * definidos a partir de la taxonomía para facilitar los procesos de transformación.
 * <br /><br />
 * Para mantener la información lo más eficientemente posible, utiliza una caché interna
 * basada en ehCache y la cual puede ser configurada mediante el fichero de configuración
 * ehcache.xml.
 * <br /><br />
 * Para conocer más sobre cómo configurar la caché, puede visitar la web del proyecto
 * la cual se encuentra en la siguiente dirección http://ehcache.sourceforge.net
 * <br /><br />
 * Tanto el fichero global.xml como el resto de ficheros de configuración correspondientes
 * a los mapas, los cuales se encuentran bajo el directorio resources/PGCMaps, son 
 * leídos mediante Castor.
 * <br /><br />
 * Cuando los mapas son deserializados mediante Castor, son encapsulados mediante la clase 
 * ConfigMap, la cual crea tablas de índices de modo que el acceso a los elementos sea lo
 * más eficiente posible.
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


public class ConfigurationManager
{

    private static final Logger logger = Logger.getLogger(ConfigurationManager.class);

    
    private static final String MAPS_CACHE_NAME = "mapsCache";
    
    private static Global global = null;
    private static ConfigurationManager configManager = null;
    
    private CacheManager cacheManager = null;
    private Cache mapsCache = null;
    private HashMap<String,String> mapsCacheKeys = new HashMap<String,String>();

    /*Constructor*/
    private ConfigurationManager() throws XBRLPGCException 
    {

	String mapsPath = XbrlApiConfiguration.getInstance().getMapsPath();
	File directoryMaps = new File(mapsPath);
	File globalFile = null;
	
	if (!directoryMaps.exists())
	{
	    throw new DocumentNotFoundException(DocumentNotFoundException.notExistDirectoryMap, new String[]{mapsPath});
	}

	String globalFilePath = XbrlApiConfiguration.getInstance().getGlobalPath();

	globalFile = new File(globalFilePath);

	if (!globalFile.exists())
	{
	    throw new DocumentNotFoundException(DocumentNotFoundException.notExistGlobalFile, new String[]{globalFilePath});
	}

	FileReader globalFileReader = null;;
	try
	{
	    globalFileReader = new FileReader(globalFile);
	    
	} catch (FileNotFoundException e)
	{
	    throw new DocumentNotFoundException(DocumentNotFoundException.notExistGlobalFile, new String[]{globalFilePath}, e);
	}

	try
	{
	    global = Global.unmarshal(globalFileReader);

	} catch (MarshalException e)
	{
	    throw new XBRLPGCException(XBRLPGCException.errorInGlobalFileDeserialization, new String[]{globalFilePath}, e);
	    
	} catch (ValidationException e)
	{
	    throw new XSDValidatorException(XSDValidatorException.errorInValidationGlobalFile, new String[]{globalFilePath},e );
	}

	cacheManager = CacheManager.create();
	cacheManager.addCache(MAPS_CACHE_NAME);
	mapsCache = cacheManager.getCache(MAPS_CACHE_NAME);

    }

    /*Metodo que inicializa la clase*/
    private synchronized static void initialize() throws XBRLPGCException
    {
	if (configManager == null)
	{
	    configManager = new ConfigurationManager();
	}
    }

    
    /**
     * Obtiene la referencia a la configuración de mapas necesaria para realizar la 
     * transformación de formato común a XBRL y viceversa.
     * 
     * @return
     * referencia a la configuración 
     * 
     * @throws XBRLPGCException
     */
    public static final ConfigurationManager getInstance() throws XBRLPGCException
    {
	if (configManager == null)
	{
	    initialize();
	}

	return configManager;
    }

    /*Devuelve un objeto ConfigReport partiendo de un report ID*/
    private ConfigReport getReport(String reportID) throws XBRLPGCException
    {
	ConfigReport reportFound = null;

	int numReports = global.getConfigReportCount();

	for (int i = 0; i < numReports; i++)
	{

	    if (global.getConfigReport(i).getId().equals(reportID))
	    {
		reportFound = global.getConfigReport(i);
		break;
	    }
	}

	if (reportFound == null)
	{
	    throw new XBRLPGCException(XBRLPGCException.notFoundReportInGlobal, new String[]{reportID});
	}

	return reportFound;
    }

    /*Devuelve un objeto Unit partiendo de un key y un report*/
    private Unit getUnitByKey(ConfigReport report, String unitKey)
    {

	Unit unitFound = null;

	int numUnits = report.getUnitCount();

	for (int i = 0; i < numUnits; i++)
	{
	    if (report.getUnit(i).getKey().equals(unitKey))
	    {
		unitFound = report.getUnit(i);
		break;
	    }
	}
	return unitFound;
    }
    
    
    /*Devuelve un module perteneciente a un report*/
    private ConfigModule getModule(ConfigReport report, String moduleID) throws XBRLPGCException
    {

	ConfigModule moduleFound = null;

	int numModules = report.getConfigModuleCount();

	for (int i = 0; i < numModules; i++)
	{

	    if (report.getConfigModule(i).getId().equals(moduleID))
	    {
		moduleFound = report.getConfigModule(i);
		break;
	    }
	}
	if (moduleFound == null)
	{
	    throw new XBRLPGCException(XBRLPGCException.notFoundModuleForReportInGlobal, new String[]{moduleID, report.getId()});

	}

	return moduleFound;
    }

    /**
     * Obtiene la lista de espacios de nombres básicos que debe contener una instancia
     * XBRL correspondiente a un report dado.
     * 
     * @param idReport
     * identificador del report
     * 
     * @return
     * lista de espacios de nombres
     * 
     * @throws XBRLPGCException
     */
    public NsItem[] getBasicNamespaces(String idReport) throws XBRLPGCException
    {

	return getReport(idReport).getNsItem();
    }

    
    /**
     * Obtiene la URI para el schemaRef del report indicado.
     * 
     * @param reportID
     * identificador del report
     * 
     * @return
     * String contenedor de la URI encontrada
     * 
     * @throws XBRLPGCException
     */
    public String getShemaRefUri(String reportID) throws XBRLPGCException
    {
	return getReport(reportID).getSchemaRef().getUri();
    }

    
    /**
     * Devuelve el targetNamespace para el report indicado mediante un identificador dado.
     * 
     * @param reportID
     * identificador del report consultado
     * 
     * @return
     * String contenedor del targetNamespace consultado
     * 
     * @throws XBRLPGCException
     */
    public String getTargetNamespace(String reportID) throws XBRLPGCException
    {
	return getReport(reportID).getSchemaRef().getTargetNamespace();
    }

    
    /**
     * Devuelve la URL base que usa el schemaRef de un report.
     * 
     * @param reportID
     * identificador del report
     * 
     * @return
     * URL base
     * 
     * @throws XBRLPGCException
     */
    public String getBaseUrl(String reportID) throws XBRLPGCException
    {
	return getReport(reportID).getSchemaRef().getBaseUrl();
    }
    
    /**
     * Devuelve el code correspondiente a un unitKey e identificador de report dado.   
     * 
     * @param unitKey
     * identificador de la unidad para la cual se hace la consulta
     * 
     * @param reportID
     * identificador del report que contiene la unidad
     * 
     * @return
     * codido de la unidad cousltada
     * 
     * @throws XBRLPGCException
     */
    public String getUnit(String unitKey, String reportID) throws XBRLPGCException
    {
	String unitCode = null;
	
	Unit vUnit = getUnitByKey(getReport(reportID), unitKey);
	if (vUnit != null)
	{
	    unitCode = vUnit.getCode();
	}
	return unitCode;
    }


    
    
    /**
     * Devuelve el prefijo correspondiente a un identificador de report y una clave de unidad.
     * 
     * @param unitKey
     * clave de la unidad consultada
     * 
     * @param reportID
     * identificador del report que contiene la unidad consultada
     * 
     * @return
     * prefijo de la unidad dada
     * 
     * @throws XBRLPGCException
     */
    public String getUnitPrefix(String unitKey, String reportID) throws XBRLPGCException
    {
	
	String unitPrefix = null;
	
	Unit vUnit = getUnitByKey(getReport(reportID), unitKey);
	if (vUnit != null)
	{
	    unitPrefix = vUnit.getPrefix();
	}
	return unitPrefix;
    }

    
    /**
     * Devuelve un ConfigMap correspondiente a un identificador de mapa e identificador de report dados.
     * Accede a la caché de mapas, en caso de no encontrarlo accede al módulo de configuración en el cual
     * se indica el nombre del fichero que contiene el Statement correspondiente y lo deserializa, 
     * añadiéndolo a la caché antes de ser devuelto.
     * 
     * @param reportID
     * identificador del report que contiene la información del mapa
     * 
     * @param moduleID
     * identificador del módulo que contiene la información del mapa
     * 
     * @return
     * ConfigMap con los ConceptMaps correspondientes al Statement indicado con la configuración
     * encontrada bajo el módulo identificado mediante el parámetro moduleID.
     * 
     * @throws XBRLPGCException
     */
    private ConfigMap getMap(String reportID, String moduleID) throws XBRLPGCException
    {
	String mapFile = null;
	Statement statement = null;
	ConfigMap map = null;
	Object lock = new Object();
	try
	{
	    String key = reportID+ "_" + moduleID;
	    
	    if (mapsCacheKeys.containsKey(key))
	    {
		Element moduleElement = mapsCache.get(mapsCacheKeys.get(key));
		if (moduleElement != null)
		{
		    map = (ConfigMap) moduleElement.getObjectValue();
		}
	    }
	    if (map == null)
	    {
		synchronized (lock)
		{
		    
		    mapFile = getModule(getReport(reportID), moduleID).getFileMap();
		    String mapFullPath = XbrlApiConfiguration.getInstance().getMapsPath() + mapFile;
		    if (logger.isDebugEnabled())
		    {
			logger.debug("loading map: " + mapFullPath);
		    }
		    FileReader fileMapReader = new FileReader(mapFullPath);
		    statement = Statement.unmarshal(fileMapReader);
		    map = new ConfigMap(statement);
		    mapsCache.put(new Element(key, map));
		    mapsCacheKeys.put(key, key);
		}

	    }
	} catch (MarshalException e)
	{
	    logger.error(e);
	    throw new XBRLPGCException(XBRLPGCException.errorInMapFileDeserialization, new String[]{mapFile}, e);

	} catch (ValidationException e)
	{
	    logger.error(e);
	    throw new XSDValidatorException(XSDValidatorException.errorInValidationMapFile, new String[]{mapFile},e );

	} catch (FileNotFoundException e)
	{
	    logger.error(e);
	    throw new DocumentNotFoundException(DocumentNotFoundException.notExistMapFile, new String[]{mapFile});
	}
	return map;
    }

    
    /*Devuelve un ConfigMap a partir de un inputID buscando dentro de un report*/
    private ConfigMap searchConfigMapByInputID(String reportID, String inputID) throws XBRLPGCException
    {
	ConfigMap configMapResp = null;
	ConfigReport report = getReport(reportID);
	
	if (report != null)
	{
	    int numModules = report.getConfigModuleCount();
	    for (int i = 0; i < numModules; i++)
	    {
		ConfigModule currentModule = report.getConfigModule(i);
		ConfigMap currentConfigMap = getMap(reportID, currentModule.getId());
		if (currentConfigMap.getConceptMapByInputID(inputID) != null)
		{
		    configMapResp = currentConfigMap;
		    break;
		}
	    }
	}
	return configMapResp;
    }

    /*Devuelve un module buscando por un outputID*/
    private ConfigModule searchModuleByOutputID(String reportID, String outputID, String dimension, String member) throws XBRLPGCException
    {
	ConfigModule moduleResp = null;
	ConfigReport report = getReport(reportID);

	if (report != null)
	{
	    int numModules = report.getConfigModuleCount();
	    for (int i = 0; i < numModules; i++)
	    {
		ConfigModule currentModule = report.getConfigModule(i);
		ConfigMap currentConfigMap = getMap(reportID, currentModule.getId());
		
		ConceptMap currentConceptMap = currentConfigMap.getConceptMapByOutputID(outputID, dimension, member);
		if (currentConceptMap != null)
		{
		    moduleResp = currentModule;
		    break;
		}
	    }
	}
	return moduleResp;
    }

    /*Devuelve un ConfigMap buscando por outputID*/
    private ConfigMap searchConfigMapByOutputID(String reportID, String outputID, String dimension, String member) throws XBRLPGCException
    {
	ConfigMap configMapResp = null;
	ConfigReport report = getReport(reportID);

	if (report != null)
	{
	    int numModules = report.getConfigModuleCount();
	    for (int i = 0; i < numModules; i++)
	    {
		ConfigModule currentModule = report.getConfigModule(i);
		ConfigMap currentConfigMap = getMap(reportID, currentModule.getId());
		
		ConceptMap currentConceptMap = currentConfigMap.getConceptMapByOutputID(outputID, dimension, member);
		if (currentConceptMap != null)
		{
		    configMapResp = currentConfigMap;
		    break;
		}
	    }
	}
	return configMapResp;
    }
    

    /*Devuelve un ConfigMap trabajando con los codigos de formato común*/
    private ConfigMap searchConfigMapToTranslate(String reportID, String outputID) throws XBRLPGCException
    {
	ConfigMap configMapResp = null;
	ConfigReport report = getReport(reportID);

	if (report != null)
	{
	    int numModules = report.getConfigModuleCount();
	    for (int i = 0; i < numModules; i++)
	    {
		ConfigModule currentModule = report.getConfigModule(i);
		ConfigMap currentConfigMap = getMap(reportID, currentModule.getId());
		if (currentConfigMap.getConceptMapToTranslate(outputID) != null)
		{
		    configMapResp = currentConfigMap;
		    break;
		}
	    }
	}
	return configMapResp;
    }
    
    
    
    
    /*
	 * Devuelve una lista de ConfigMap trabajando con los codigos de formato común
	 */
	private ArrayList<ConfigMap> searchConfigMapToTranslateOnArray(
			String reportID, String outputID) throws XBRLPGCException {

		ConfigReport report = getReport(reportID);
		ArrayList<ConfigMap> returnList = new ArrayList<ConfigMap>();

		if (report != null) {
			int numModules = report.getConfigModuleCount();
			for (int i = 0; i < numModules; i++) {
				ConfigModule currentModule = report.getConfigModule(i);
				ConfigMap currentConfigMap = getMap(reportID, currentModule
						.getId());
				if (currentConfigMap.getConceptMapToTranslate(outputID) != null) {

					returnList.add(currentConfigMap);
				}
			}
		}

		return returnList;
	}    
    
    
    
    
    /**
     * Localiza un ConceptMap mediante el identificador del report, el identificador del módulo
     * y el identificador de entrada, inputID del propio ConceptMap.
     * 
     * @param reportID
     * identifcador del report
     * 
     * @param moduleID
     * identificador del módulo
     * 
     * @param inputID
     * identificador de entrada del ConceptMap
     * 
     * @return
     * referencia al ConceptMap encontrado
     * 
     * @throws XBRLPGCException
     */
    public ConceptMap getInfoByInputID(String reportID, String moduleID, String inputID) throws XBRLPGCException
    {
	ConceptMap conceptMapResult = null;
	ConfigMap map = null;
	if (moduleID != null)
	{
	    map = getMap(reportID, moduleID);

	} else
	{
	    map = searchConfigMapByInputID(reportID, inputID);
	}
	if (map != null)
	{
	    conceptMapResult = map.getConceptMapByInputID(inputID);
	}
	
	return conceptMapResult;
    }

    /**
     * Localiza un ConceptMap mediante el identificador del report, el identificador del módulo
     * y el identificador de entrada, outputID del propio ConceptMap, la dimensión y el miembro, para aquellos
     * conceptos que pertenecen a mapas dimensionales.
     * 
     * @param reportID
     * identificador del report
     * 
     * @param moduleID
     * identificador del módulo
     * 
     * @param outputID
     * identificador de salida del ConceptMap
     * 
     * @param dimension
     * nombre de la dimension
     * 
     * @param member
     * nombre del miembro
     * 
     * @return
     * referencia al ConceptMap encontrado
     * 
     * @throws XBRLPGCException
     */    
    public ConceptMap getInfoByOutputID(String reportID, String moduleID, String outputID, String dimension, String member) throws XBRLPGCException
    {
	ConceptMap conceptMapResult = null;
	ConfigMap map = null;
	if (moduleID != null)
	{
	    map = getMap(reportID, moduleID);

	} else
	{
	    map = searchConfigMapByOutputID(reportID, outputID, dimension, member);
	}
	if (map != null)
	{
	    conceptMapResult = map.getConceptMapByOutputID(outputID,dimension, member);
	}

	return conceptMapResult;
    }

    /**
     * Localiza un ConceptMap mediante el identificador del report, el identificador del módulo
     * y el identificador de entrada, outputID del propio ConceptMap y el dominio, para aquellos
     * conceptos que pertenecen a mapas dimensionales.
     * 
     * @param reportID
     * identifcador del report
     * 
     * @param moduleID
     * identificador del módulo
     * 
     * @param outputID
     * identificador de salida del ConceptMap
     * 
     * 
     * @return
     * referencia al ConceptMap encontrado
     * 
     * @throws XBRLPGCException
     */ 
    public ConceptMap getInfoByOutputID(String reportID, String moduleID, String outputID) throws XBRLPGCException
    {
	ConceptMap conceptMapResult = null;
	conceptMapResult = getInfoByOutputID(reportID , moduleID, outputID, null, null);
	return conceptMapResult;
    }
    
    
    /**
     * Localiza un ConceptMap mediante el identificador del report, el identificador del módulo
     * y el identificador de entrada, outputID del propio ConceptMap y el dominio, para aquellos
     * conceptos que pertenecen a mapas dimensionales. Teniendo en cuenta la traducción entre elementos 
     * XBRL y codigos de formato común.
     * 
     * @param reportID
     * identifcador del report
     * 
     * @param moduleID
     * identificador del módulo
     * 
     * @param outputID
     * identificador de salida del ConceptMap
     * 
     * 
     * @return
     * referencia al ConceptMap encontrado
     * 
     * @throws XBRLPGCException
     */
    public String getInfoToTranslate(String reportID, String moduleID, String outputID) throws XBRLPGCException
    {
	String outputToTranslate = null;
	ConfigMap map = null;
	if (moduleID != null)
	{
	    map = getMap(reportID, moduleID);

	} else
	{
	    map = searchConfigMapToTranslate(reportID, outputID);
	}
	if (map != null)
	{
	    outputToTranslate = map.getConceptMapToTranslate(outputID);
	}


	
	return outputToTranslate;
    }
    
    
    
    
    /**
     * Localiza un ConceptMap mediante el identificador del report, el identificador del módulo
     * y el identificador de entrada, outputID del propio ConceptMap y el dominio, para aquellos
     * conceptos que pertenecen a mapas dimensionales. Teniendo en cuenta la traducción entre elementos 
     * XBRL y codigos de formato común.
     * Cuando encuentra un elemento multievaluado, concatena todos los codes localizados
     * 
     * @param reportID
     * identifcador del report
     * 
     * @param moduleID
     * identificador del módulo
     * 
     * @param outputID
     * identificador de salida del ConceptMap
     * 
     * @param doList
     * indicador de acumulador de codes multievaluados 
     * 
     * 
     * 
     * @return
     * referencia al ConceptMap encontrado
     * 
     * @throws XBRLPGCException
     */
    public String getInfoToTranslate(String reportID, String moduleID, String outputID, boolean doList) throws XBRLPGCException
    {
	String outputToTranslate = null;
	ConfigMap map = null;
	ArrayList<ConfigMap> mapList=null;
	
	ArrayList<String> foundT=new ArrayList<String>();

	if (moduleID != null)
	{
	    map = getMap(reportID, moduleID);

	} else{
	    mapList = searchConfigMapToTranslateOnArray(reportID, outputID);
	}
	
	//si ha encontrado un único mapa es porque tiene la info del moduleID
	if (map != null)
	{
	    outputToTranslate = map.getConceptMapToTranslate(outputID);
	}
	else if (mapList != null  && mapList.size()>0 ) //tenemos una lista de mapas porque, en principio, el output podría estar contenido en varios mapas con code diferente
	{
		
		
		for (int i = 0; i < mapList.size(); i++) {
			ConfigMap mapSin =(ConfigMap)mapList.get(i);
			String tryCode=mapSin.getConceptMapToTranslate(outputID);

			if ( !( foundT.contains(tryCode) ) ){
				foundT.add(tryCode);  //los cargo a este array intermedio para ir comprobando si ya estuvieran
			}
		}
		
		outputToTranslate="";
		for (int j = 0; j < foundT.size(); j++) {
			String omCode=(String)foundT.get(j);
			outputToTranslate += omCode + "|";
		}
		
		//quito el último pipe
		try{
			if ( !( outputToTranslate.trim().equals("")) ){
				outputToTranslate=outputToTranslate.substring(0,outputToTranslate.length()-1);
			}
		}catch(Exception e){}
		
	}

	
	
	return outputToTranslate;
    }    
    
    
    
    
    
    /**
     * Obtiene el identificador del módulo para un identificador de report, dimension, member y ConceptMap dados.
     * Indica qué módulo contiene el ConceptMap dado.
     * 
     * @param reportID
     * identificador del report
     * 
     * @param outputID
     * identificador de salida del ConceptMap
     * 
     * @param dimension
     * dimensión
     * 
     * @param member
     * member
     * 
     * @return
     * Identificador del módulo contenedor del ConceptMap.
     * 
     * @return
     * identificador del modulo
     * 
     * @throws XBRLPGCException
     * 
     */
    public String getModuleID(String reportID, String outputID, String dimension, String member) throws XBRLPGCException
    {
	String moduleID = null;

	ConfigModule module = searchModuleByOutputID(reportID, outputID, dimension, member);
	if (module != null)
	{
	    moduleID = module.getId();
	}

	return moduleID;
    }

    
    /**
     * Devuelve la configuración de un Report para un schemaRef dado.
     * 
     * @param schemaRef
     * schemaRef del módulo a localizar.
     * 
     * @return
     * configuración del report encontrado
     * 
     */
    public ConfigReport getGlobalReportFromSchemaRef(String schemaRef)
    {

	ConfigReport foundReport = null;

	String utilSchemaRef = schemaRef;

	utilSchemaRef = schemaRef.replace('\\', '/');
	String[] arrSchemaRef = utilSchemaRef.split("/");
	int segmentCount = arrSchemaRef.length;
	utilSchemaRef = arrSchemaRef[segmentCount - 1];

	ConfigReport[] globalReportList = global.getConfigReport();

	for (int i = 0; i < globalReportList.length; i++)
	{
	    ConfigReport globalReport = globalReportList[i];
	    SchemaRef globalSchemaRef = globalReport.getSchemaRef();

	    if (globalSchemaRef.getUri().equalsIgnoreCase(utilSchemaRef))
	    {
		foundReport = globalReport;
		break;
	    }
	}

	return foundReport;
    }

    
    /**
     * Devuelve un objeto de tipo Global
     * @return
     * Objeto del tipo Global
     */
    public Global getGlobal()
    {

	return global;
    }

    
    
    /**
     * Obtiene y devuelve el orden que debe llevar un elemento dentro de la tupla en la que está anidado
     * @param reportID
     * id del report
     * @param moduleID
     * id del module
     * @param itemID
     * id del item
     * @param parentInputID
     * codigo de la tupla padre
     * @return
     * BigDecimal con el order correcto
     * @throws XBRLPGCException
     */
    public BigDecimal getOrderToReplace(String reportID, String moduleID, String itemID, String parentInputID) throws XBRLPGCException
    {
    	BigDecimal returnValue=null;
    	try{
    		
    		
    		ConfigMap map = null;
    		
    		
    		if (moduleID != null)
    		{
    		    map = getMap(reportID, moduleID);

    		} else
    		{
    		    map = searchConfigMapByInputID(reportID, itemID);
    		}
    		if (map != null)
    		{
    			 ArrayList<ConceptMap> conceptList =map.getAllConceptMapByInputID(itemID);
    			 if( !(conceptList==null) &&   conceptList.size()>1 ){
    					for (int i = 0; i < conceptList.size(); i++)
    					{
    						ConceptMap cm=conceptList.get(i);
    						String cmOutID= cm.getOutputID();
    						if( !(cmOutID==null) && !(cmOutID.trim().equals("")) ){
    						String[] arrCodesOut= cmOutID.split(":");
    						if ( arrCodesOut.length>1 && (arrCodesOut[arrCodesOut.length -2].toString().equals(parentInputID) )  ){
    							returnValue= cm.getOrder();
    							break;
    						}
    						}
    						
    					}
    			 }
    		}    		
    		

    	}catch(Throwable e3){
    		returnValue=null;
    	}
    	return returnValue;
    }    

    
    
    
}
