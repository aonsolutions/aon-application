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


package es.inteco.xbrl.pgc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.xerces.util.XMLCatalogResolver;



/**
 *
 *
 * Clase de configuración utilizada por la API para determinar la localización de los recursos
 * y otro tipo de propiedades las cuales deben ser configuradas mediante un archivo de configuración.
 * <br /><br />
 * El archivo de configuración está ubicado en el directorio config y se llama pgc.properties.  Este
 * archivo contiene una serie de propiedades necesarias para el funcionamiento del sistema en general.
 * <br /><br />
 * <ul>
 * <li>
 * taxonomyRoot: identifica el directorio raíz de la taxonomía.
 * </li>
 * <li>
 * basePath: directorio raíz en el cual ha sido instalada la API.  En este directorio se encontrarán todos
 *	los recursos necesarios, es decir, los archivos .cmd para línea de comando así como los directorios
 * 	config, lib, properties, resources con sus contenidos.
 * </li>
 * <li>
 * tempDirectory: directorio temporal, necesario para guardar ficheros de modo temporal, necesario para 
 * 	los procesos realizados por el sistema.
 * </li>
 * <li>
 * schemaLocationIOInterface: schemaLocation de los XML de entrada en formato común, necesario para poder
 * 	resolver la localización del esquema con el cual será validado.
 * </li>
 * </ul>
 * 
 * Esta clase, además de proporcionar acceso a las propiedades previametne mencionadas y que se encuentran
 * en el archivo de configuración pgc.properties, provee otras propiedades compuestas a partir de 
 * éstas.  Dichas propiedades son comentadas en sus respectivos métodos de acceso a las mismas.
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


public class XbrlApiConfiguration
{

    private static final Logger logger = Logger.getLogger(XbrlApiConfiguration.class);
    
    private static final String PROPERTY_FILE = "/pgc.properties";
    private static final String XSL_TRANSFORMATION_EXECUTABLE_BAT_FILE = "runXslTransformer.bat";
    
    private String taxonomyRoot = null;
    
    private String m_coreRoot = null;
    private String mapsPath = null;
    private String globalPath = null;
    private String taxonomyConfigurationFile = null;
    private String tempDirectory = null;
    private String xslTransformerPath = null;
    private String schemaLocationIOInterface = null;
    private XMLCatalogResolver resolver =null;
    private String pgcErrorsCatalogPath = null;
    
  
    
    private static XbrlApiConfiguration config = null;
    
    /*Constructor*/
    private XbrlApiConfiguration() 
    {
	super();
    }
    
    
    /**
     * Este método se encarga de crear una única instancia inicalizando la misma con las 
     * propiedades que serán publicadas.
     * 
     */
    private static void initialize()
    {
	config = new XbrlApiConfiguration();
	Properties pgcProperties = new Properties();
	try
	{
		// Carga el fichero de configuración con las propiedades
		// pgcProperties.load(new FileInputStream(new File(ClassLoader.getSystemResource(PROPERTY_FILE).toURI())));
		URL url = XbrlApiConfiguration.class.getResource(PROPERTY_FILE);
		URI uri = url.toURI();
		File file = new File(uri);
	    pgcProperties.load(new FileInputStream(file));

	    config.setTaxonomyRoot(pgcProperties.getProperty("taxonomyRoot"));
	    //config.setTaxonomyRoot(prepareTaxonomyRoot(pgcProperties.getProperty("taxonomyRoot")));
	    config.setBasePath(pgcProperties.getProperty("basePath"));
	    config.setTempDirectory(pgcProperties.getProperty("tempDirectory"));
	    config.setSchemaLocationIOInterface(pgcProperties.getProperty("schemaLocationIOInterface"));


	} catch (FileNotFoundException e)
	{
	    logger.fatal(e);

	} catch (IOException e)
	{
	    logger.fatal(e);

	} catch (URISyntaxException e)
	{
	    logger.fatal(e);
	}
    }    
    
    
    
    static
    {
	initialize();
    }
    



 

    
    

    /**
     * Devuelve una instancia de configuración, si no ha sido creada aún la crea para
     * de modo estático de modo que las sucesivas veces que se acceda se obtenga la 
     * misma.
     * 
     * @return
     * referencia a la configuración
     * 
     */
    public static final XbrlApiConfiguration getInstance()
    {

	return config;

    }

    /**
     * Devuelve el path raíz en el cual se encuentra la taxonomía.
     * 
     * @return
     * path 
     */
    public final String getTaxonomyRoot()
    {
    	return taxonomyRoot;
    }
    
    
    /**
     * Devuelve el directorio en el cual se encuentran los mapas de configuración
     * de PGC2007.
     * 
     * @return
     * path de mapas.
     * 
     */
    public final String getMapsPath()
    {

	return mapsPath;
    }

    
    /**
     * Devuelve el directorio en el cual se encuentran los recursos necesarios para
     * poder invocar al transformador XSL 2.0.
     * 
     * @return
     * path de transformación XSL
     * 
     */
    public final String getXslTransformerPath()
    {
	return xslTransformerPath;
    }

    
    /**
     * Devuelve el path absoluto a la localización del archivo de configuración global.xml.
     * 
     * @return
     * path del archivo global.xml
     * 
     */
    public final String getGlobalPath()
    {
	return globalPath;
    }

    private final void setBasePath(String m_basePath)
    {
	String coreRootRelative = m_basePath;
	
	Class<? extends XbrlApiConfiguration> c = this.getClass();
	URL u = c.getProtectionDomain().getCodeSource().getLocation();		
	String fileUri = "";
	try
	{
		fileUri =  URLDecoder.decode(u.getFile(), "UTF-8") ;
	}
	catch(UnsupportedEncodingException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}		
	  
	File f = new File(fileUri); 
	String coreRootBeforeChanges = f.getParent();
	if (coreRootRelative != null)
	{
	    coreRootBeforeChanges = coreRootBeforeChanges + coreRootRelative;
	}

	if (!coreRootBeforeChanges.endsWith("\\"))
	{
	    coreRootBeforeChanges += "\\";
	}
	
	m_coreRoot = PGCUtils.replace(coreRootBeforeChanges, "\\", "/");
	m_coreRoot = PGCUtils.replace(m_coreRoot, "//", "/");
	
	File coreRootPath = new File(m_coreRoot + "");
	if (!coreRootPath.exists()) 
	{
		coreRootBeforeChanges = f.getAbsolutePath();
		if (coreRootRelative != null) 
		{
			coreRootBeforeChanges = coreRootBeforeChanges + coreRootRelative;
		}
		if (!coreRootBeforeChanges.endsWith("\\")) 
		{
			coreRootBeforeChanges += "\\";
		}
		m_coreRoot = PGCUtils.replace(coreRootBeforeChanges, "\\", "/");
		m_coreRoot = PGCUtils.replace(m_coreRoot, "//", "/");
	}
	
	mapsPath = PGCUtils.getFullPath(m_coreRoot + "\\resources\\PGCMaps\\");
	mapsPath = mapsPath + "/";
	mapsPath = PGCUtils.replace(mapsPath, "\\", "/");
	mapsPath = PGCUtils.replace(mapsPath, "//", "/");
	
	xslTransformerPath = PGCUtils.getFullPath(m_coreRoot + "\\resources\\XSLTransformer\\");
	xslTransformerPath = PGCUtils.replace(xslTransformerPath, "\\", "/");
	xslTransformerPath = PGCUtils.replace(xslTransformerPath, "//", "/");	

	globalPath = PGCUtils.getFullPath(m_coreRoot + "\\config\\global.xml");
	globalPath = PGCUtils.replace(globalPath, "\\", "/");
	globalPath = PGCUtils.replace(globalPath, "//", "/");

	taxonomyConfigurationFile = PGCUtils.getFullPath(m_coreRoot + "\\config\\TaxonomyConfiguration.xml");
	taxonomyConfigurationFile = PGCUtils.replace(taxonomyConfigurationFile, "\\", "/");
	taxonomyConfigurationFile = PGCUtils.replace(taxonomyConfigurationFile, "//", "/");
	
	taxonomyRoot = PGCUtils.getFullPath(m_coreRoot +  "\\" + getTaxonomyRoot());
	taxonomyRoot = PGCUtils.replace(taxonomyRoot, "\\", "/");
	taxonomyRoot = PGCUtils.replace(taxonomyRoot, "//", "/");
	taxonomyRoot = taxonomyRoot +"/";
	
	String catalogFile = PGCUtils.getFullPath(m_coreRoot + "\\config\\catalog.xml");
	catalogFile = PGCUtils.replace(catalogFile, "\\", "/");
	catalogFile = PGCUtils.replace(catalogFile, "//", "/");	
	
	resolver = getCatalog(catalogFile);
	
//	pgcErrorsCatalogPath = PGCUtils.getFullPath(m_coreRoot + "\\config\\pgc07-errors.xml");
//	pgcErrorsCatalogPath = PGCUtils.replace(pgcErrorsCatalogPath, "\\", "/");
//	pgcErrorsCatalogPath = PGCUtils.replace(pgcErrorsCatalogPath, "//", "/");
	
    }
    
    private XMLCatalogResolver getCatalog(String catalogFile)
    {
	if (resolver == null) 
	{
	    resolver = new XMLCatalogResolver(new String[]{catalogFile});
	}

	return resolver;
    }

    private final void setTaxonomyRoot(String taxonomyRoot)
    {
        this.taxonomyRoot = taxonomyRoot;
    }


    /**
     * Obtiene el path en el que se encuentra el fichero de configuración que resuelve
     * los paths externos de la taxonomía.
     * 
     * @return
     * path.
     */
    public final String getTaxonomyConfigurationFile()
    {
	return taxonomyConfigurationFile;
    }

    /**
     * Devuelve el path absoluto al directorio temporal de trabajo.
     * 
     * @return
     * path directorio temporal
     */
    public final String getTempDirectory()
    {
    	if (tempDirectory == null) {
			try {
				File tempFile = File.createTempFile("prefix","suffix");
	    		tempDirectory = tempFile.getParent(); 
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
        return tempDirectory;
    }

    public final void setTempDirectory(String tempDirectory)
    {
        this.tempDirectory = tempDirectory;
    }

    
    public final String getXslTransformationExecutableBatFileName()
    {
	return XSL_TRANSFORMATION_EXECUTABLE_BAT_FILE;
    }

    public final String getSchemaLocationIOInterface()
    {
        return schemaLocationIOInterface;
    }

    public final void setSchemaLocationIOInterface(String schemaLocationIOInterface)
    {
        this.schemaLocationIOInterface = schemaLocationIOInterface;
    }

    public final XMLCatalogResolver getResolver()
    {
        return resolver;
    }


    public final String getPgcErrorsCatalogPath()
    {
    	if (pgcErrorsCatalogPath == null) {
    		pgcErrorsCatalogPath = "/pgc07-errors.xml";
    		try {
        		URL url = XbrlApiConfiguration.class.getResource(pgcErrorsCatalogPath);
				File file = new File(url.toURI());
				pgcErrorsCatalogPath = file.getAbsolutePath();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
    	}
        return pgcErrorsCatalogPath;
    }
    
    
    /**
     * Convertimos la ruta para la taxonomia que se indica en pgc.properties
     * en una ruta absoluta para que nuestra aplicación pueda funcionar correctamente 
     * @param originalProperty
     * ruta de taxonomia leida de pgc.properties
     * @return
     * ruta de taxonomia en path absoluto
     */
    private static String prepareTaxonomyRoot(String originalProperty){
    	String returnValue = originalProperty;
    	if (originalProperty != null) {
	    	try{
	    		//logger.debug("readed taxonomy root from properties file: " + originalProperty);
	    		String newValue=originalProperty;
	    	
	    		File file = new File(newValue);
	    		file = file.getAbsoluteFile();  
	   		
	    		newValue= file.getAbsoluteFile().getAbsolutePath();
	    	   
	    		newValue= newValue.replace('\\', '/');
	    	    if  ( !(newValue.endsWith("/"))){
	    	    	newValue += "/";
	    	    }
	    	    
	    	    returnValue=newValue;
	    	}
	    	catch(Exception e){
	    		//registro el error pero no paro la aplicación. Intentaré ejecutar con el parámetro original
	    		logger.error(e.getMessage());
	    	}
	    	//logger.debug("working with this taxonomy root: " + returnValue);
    	}
    	return returnValue;
    	
    }
}
