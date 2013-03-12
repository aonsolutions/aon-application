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
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import es.inteco.xbrl.pgc.utils.XbrlApiConfiguration;

import ubmatrix.xbrl.common.src.Configuration;
import ubmatrix.xbrl.src.XbrlConfigurationType;
import ubmatrix.xbrl.common.utility.src.*;
import ubmatrix.xbrl.src.*;
import ubmatrix.xbrl.locationController.xbrlLocationController.uriResolver.src.FileLogicalUriResolver;



/**
 *
 *
 * Clase utilizada para proporcionar al motor Xbreeze la localización de las
 * referencias locales necesarias.
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
 * @version 1.0, 14/01/2009
 * @author difusioncalidad@inteco.es
 *
 */


public class UbmTaxonomyConfiguration
{
    static Logger logger = Logger.getLogger(UbmTaxonomyConfiguration.class);

    private static HashMap _logicPGC;
    private static HashMap _schemaPGC;
    private static String _XMLConfigurationTaxonomia = XbrlApiConfiguration.getInstance().getTaxonomyConfigurationFile();
    public static final String basePath = XbrlApiConfiguration.getInstance().getTaxonomyRoot();

    /**
     * Constructor por defecto
     */
    public UbmTaxonomyConfiguration() {
		super();
	}
        
    
    /**
     * Carga la configuración especifica para la taxonomia de PGC2007
     * @param configInstance
     * fichero de configuración utilizado por la API
     */
    public static void addTaxonomyConfiguration(Configuration configInstance)
    {
	// leo el fichero de configuración de PGC. En el mismo método se
	// cargarán los arrays con schemas PGC
	if (!(processConfigurationPGCFile(configInstance)))
	{
	    logger.warn("Taxonomy configuration file was not processed");
	    return;
	}

	// a los arrays actuales de configuración de Xbreeze, les añado los
	// arrays de PGC
	configInstance.setLogicalUriDictionary(logicAndPGC(configInstance));
	configInstance.setSchemaLocationDictionary(schemaAndPGC(configInstance));
    }

    /*Devuelve una lista de logicaluri por defecto en la que se han agregado las propias de PGC2007*/
    private static HashMap logicAndPGC(Configuration configInstance)
    {
	HashMap hmLogicAndPGC = configInstance.getLogicalUriDictionary();

	if (!(_logicPGC == null) || _logicPGC.size() > 0)
	{

	    Iterator itr = ResetableIterator.getIterator(_logicPGC.entrySet());
	    Utility.resetIterator(itr);

	    while (itr.hasNext())
	    {

		Entry me = (Entry) itr.next();
		String key = (String) me.getKey();
		String value = (String) me.getValue();
		hmLogicAndPGC.put(key, value);

	    }
	}

	return hmLogicAndPGC;
    }

    /*Devuelve una lista de schemas por defecto en la que se han agregado las propias de PGC2007*/
    private static HashMap schemaAndPGC(Configuration configInstance)
    {

	HashMap hmSchemaAndPGC = configInstance.getSchemaLocationDictionary();

	if (!(_schemaPGC == null) || _schemaPGC.size() > 0)
	{

	    Iterator itr = ResetableIterator.getIterator(_schemaPGC.entrySet());
	    Utility.resetIterator(itr);

	    while (itr.hasNext())
	    {

		Entry me = (Entry) itr.next();
		String key = (String) me.getKey();
		String value = (String) me.getValue();
		hmSchemaAndPGC.put(key, value);
	    }
	}

	return hmSchemaAndPGC;
    }

    /*Procesa el fichero de configuración para la taxonomia utilizado por XBREEZE*/
    private static boolean processConfigurationPGCFile(Configuration configUBMatrix)
    {
	_logicPGC = new HashMap();
	_schemaPGC = new HashMap();
	File testExistsFile=null;

	String fileUri = _XMLConfigurationTaxonomia;

	if (!new File(fileUri).exists())
	{
	    // System.err.println("FATAL ERROR: Could not find " + fileUri +
	    // " - please verify path in xbrlData.properties is correct");
	    // System.exit(0);
	    logger.warn("Could not find " + fileUri + " - please verify path in xbrlData.properties is correct");
	    return false;
	}

	try
	{
	    JAXBContext jaxbcontext = JAXBContext.newInstance("ubmatrix.xbrl.src");
	    Unmarshaller unm = jaxbcontext.createUnmarshaller();
	    XbrlConfigurationType config = (XbrlConfigurationType) unm.unmarshal(new FileInputStream(fileUri));
	    Object[] choices = config.getDefaultSchemaLocationResolutionListOrPreloadOrTransformation().toArray();

	    for (int i = 0; i < choices.length; ++i)
	    {

		// NOTE: A bug in XSD.exe causes types that contain sequences of
		// 1 element to collapse.
		// As a workaround, Fake elements were placed in the sequence.
		// These Fake types should not
		// be defined in the instance. If they are, they will be skipped
		// because they are not
		// handled in the following branching.

		Object choice = choices[i];

		if (choice instanceof DefaultSchemaLocationResolutionListType)
		{

		    DefaultSchemaLocationResolutionListType type = null;
		    type = (DefaultSchemaLocationResolutionListType) choice;
		    boolean disableCompression = Configuration.getInstance().getFeature(Configuration.c_disableCompression);
		    List schemaResList = type.getSchemaLocationResolution();
		    ubmatrix.xbrl.src.SchemaLocationResolutionType[] types = (SchemaLocationResolutionType[]) schemaResList
			    .toArray(new SchemaLocationResolutionType[schemaResList.size()]);

		    for (int j = 0; j < types.length; ++j)
		    {
			String namespaceUri = types[j].getNamespace();

			// 1) first get the uri
			// 2) Replace COREROOT with the actual value
			// 3) Make MSFT happy
			String physicalUri = types[j].getPhysicalUri();
			String formattedPhysicalUri = CommonUtilities.replace(physicalUri, "$TAXONOMYROOT", XbrlApiConfiguration.getInstance()
				.getTaxonomyRoot());

			// Fix up wacked up slashes from $COREROOT/$TAXONOMYROOT
			String temp = FileLogicalUriResolver.isRecognizedUri(formattedPhysicalUri);
			if (temp != null){
			    formattedPhysicalUri = temp;
			}
			    
			String logicalUri = types[j].getLogicalUri();
			String formattedLogicalUri = CommonUtilities.replace(logicalUri, "$TAXONOMYROOT", XbrlApiConfiguration.getInstance().getTaxonomyRoot());

			// Fix up wacked up slashes from $COREROOT/$TAXONOMYROOT
			temp = FileLogicalUriResolver.isRecognizedUri(formattedLogicalUri);
			if (temp != null)
			    formattedLogicalUri = temp;

			// populate the hash table with @namespace as key and
			// the formatted physical uri as value
			_schemaPGC.put(namespaceUri, formattedLogicalUri);

			// User requested that compression mappings be ignored.
			if (disableCompression && formattedPhysicalUri.endsWith(".cdf"))
			    continue;

		    try{
			    testExistsFile= new File(formattedPhysicalUri);
			    if (testExistsFile.exists()){
			    	_logicPGC.put(formattedLogicalUri, formattedPhysicalUri);
			    }
		    } catch (Exception e){}
		    
		    

		    }

		}

	    }

	} catch (Exception e)
	{
	    return false;
	}

	return true;
    }

}
