package com.code.aon.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PersistentClass;

public class HibernateDataManager {
	
	private static final int DEFAULT_MAX_IMPORT = 1000;
	
	public static final int DEFAULT_MAX_EXPORT = 5000;
	
	private static final int DEFAULT_EXPORT_FLUSH = 500;
	
	private static Log LOGGER = LogFactory.getLog(HibernateDataManager.class.getName());
	
	private File directory;
	
	private File configurationFile;
	
	private File importProperties;
	
	private File exportProperties;
	
	private int maxImport;
	
	private int maxExport;
	
	private int exportFlush;
	
	private boolean exportData;
	
	private boolean importData;
	
	private boolean onTheFly;
	
	private boolean ignoreDependencies;
	
	private boolean insert;
	
	private List<Class<? extends Serializable>> includeEntities;
	
	private List<Class<? extends Serializable>> excludeEntities;
	
	private AnnotationConfiguration importConfiguration;
	
	private SessionFactory importFactory;
	
	private AnnotationConfiguration exportConfiguration;	
	
	private SessionFactory exportFactory;
	
	private IEntityManager importer;
	
	public HibernateDataManager() {
		setMaxExport( DEFAULT_MAX_EXPORT );
		setMaxImport( DEFAULT_MAX_IMPORT );
		setExportFlush( DEFAULT_EXPORT_FLUSH );
	}

	public File getDirectory() {
		return directory;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}
	
	public File getConfigurationFile() {
		return configurationFile;
	}

	public void setConfigurationFile(File configurationFile) {
		this.configurationFile = configurationFile;
	}

	public File getImportProperties() {
		return importProperties;
	}

	public void setImportProperties(File importProperties) {
		this.importProperties = importProperties;
	}

	public File getExportProperties() {
		return exportProperties;
	}

	public void setExportProperties(File exportProperties) {
		this.exportProperties = exportProperties;
	}
	
	private Configuration getImportConfiguration() {
		if ( importConfiguration == null ) {
			importConfiguration = createConfiguration(configurationFile, importProperties);
		}
		return importConfiguration;
	}

	public SessionFactory getImportFactory() {
		if ( importFactory == null ) {
			importFactory = getImportConfiguration().buildSessionFactory();
		}
		return importFactory;
	}

	private Configuration getExportConfiguration() {
		if ( exportConfiguration == null ) {
			exportConfiguration = createConfiguration(configurationFile, exportProperties);
		}
		return exportConfiguration;
	}
	
	public SessionFactory getExportFactory() {
		if ( exportFactory == null ) {
			exportFactory = getExportConfiguration().buildSessionFactory();
		}
		return exportFactory;
	}
	
	public File getFile( Class entity ) {
		String name = entity.getName().replace('.', '_') + ".xml";
		return new File( getDirectory(), name );
	}
	
	public List<Class<? extends Serializable>> getEntities() {
		if ( includeEntities == null ) {
			includeEntities = new ArrayList<Class<? extends Serializable>>();
			Configuration cfg = ( isExportData() ? getExportConfiguration() : getImportConfiguration() );
			Iterator i = cfg.getClassMappings();
			while ( i.hasNext() ) {
				PersistentClass pc = (PersistentClass) i.next();
				includeEntities.add( pc.getMappedClass() );
			}
		}
		if ( this.excludeEntities != null ) {
			this.includeEntities.removeAll( this.excludeEntities );			
		}
		return includeEntities;
	}

	public void setIncludeEntities(List<Class<? extends Serializable>> entities) {
		this.includeEntities = entities;
	}

	public void setExcludeEntities(List<Class<? extends Serializable>> excludeEntities) {
		this.excludeEntities = excludeEntities;
	}

	public int getMaxImport() {
		return maxImport;
	}

	public void setMaxImport(int maxImport) {
		this.maxImport = maxImport;
	}

	public int getMaxExport() {
		return maxExport;
	}

	public void setMaxExport(int maxExport) {
		this.maxExport = maxExport;
	}
	
	public int getExportFlush() {
		return exportFlush;
	}

	public void setExportFlush(int exportFlush) {
		this.exportFlush = exportFlush;
	}

	public boolean isExportData() {
		return exportData;
	}

	public void setExportData(boolean exportData) {
		this.exportData = exportData;
	}

	public boolean isImportData() {
		return importData;
	}

	public void setImportData(boolean importData) {
		this.importData = importData;
	}
	
	public boolean isOnTheFly() {
		return onTheFly;
	}

	public void setOnTheFly(boolean onTheFly) {
		this.onTheFly = onTheFly;
	}
	
	public boolean isIgnoreDependencies() {
		return ignoreDependencies;
	}

	public void setIgnoreDependencies(boolean ignoreDependencies) {
		this.ignoreDependencies = ignoreDependencies;
	}
	
	public boolean isInsert() {
		return insert;
	}

	public void setInsert(boolean insert) {
		this.insert = insert;
	}

	public static Properties loadProperties( File file ) {
		Properties properties = new Properties();
		try {
			InputStream in = new FileInputStream( file );
			properties.load( in );
			in.close();			
		} catch (IOException e) {
			LOGGER.error( e.getMessage(), e );
		}
		return properties;
	}
	
    public static AnnotationConfiguration createConfiguration( File configurationFle, File propertiesFile) {
    	AnnotationConfiguration configuration = null;
        try {
    		configuration = new AnnotationConfiguration();
    		Properties properties = loadProperties(propertiesFile);
    		configuration.addProperties( properties );
    		if ( configurationFle != null ) {
    			configuration.configure( configurationFle );
    		} else {
       			configuration.configure();    			
    		}
   			configuration.buildMappings();
        } catch (HibernateException he) {
        	throw new RuntimeException("Configuration problem: " + he.getMessage(), he); 
        }
        return configuration;
    }
	
    public Document parse( File file ) throws IOException, DocumentException {
        SAXReader reader = new SAXReader();
        FileReader fr = new FileReader( file );
        Document document = reader.read(fr);
        return document;
    }
    
    public void exportData() throws EntityProcessException {
    	DBToXMLExporter exporter = new DBToXMLExporter( this, getElementEntityIterable() );
    	for( Class entity : getEntities() ) {
    		exporter.proccess(entity);
    	}
    }
    
    private Set<Class> getDependencies( Class entity ) {
    	PersistentClass pc = getImportConfiguration().getClassMapping(entity.getName());
    	Set<Class> result = new HashSet<Class>();
    	Iterator it = pc.getTable().getForeignKeyIterator();
    	while ( it.hasNext() ) {
    		ForeignKey fk = (ForeignKey) it.next();
    		PersistentClass foreignPC = getImportConfiguration().getClassMapping(fk.getReferencedEntityName());
    		result.add( foreignPC.getMappedClass() );
    	}
    	return result;
    }
    
    private void importDataEx( Set<Class> imported, Set<Class> processed, Class entity ) throws EntityProcessException {
    	if (! processed.contains(entity) ) {
       		if (! imported.contains(entity) ) {
	        	processed.add( entity );
	    		for( Class dependency : getDependencies( entity ) ) {
               		importDataEx( imported, processed, dependency );        			
	           	}
    			importer.proccess( entity );
	           	imported.add( entity );
	           	processed.remove( entity );
       		}	           
    	} else {
    		LOGGER.warn( "Entity is being processed: " + entity );
    	}
    }
    
    public void importData() throws EntityProcessException {
    	Set<Class> imported = new HashSet<Class>();
    	Set<Class> processed = new HashSet<Class>();
    	for( Class entity : getEntities() ) {
    		if ( ignoreDependencies ) {
    			importer.proccess( entity );    			
    		} else {
    			importDataEx( imported, processed, entity );
    		}
    	}
    }
    
    private QueryIterable<Object> getEntityIterable() {
    	QueryIterable<Object> entityIterable = new QueryIterable<Object>();
		entityIterable.setMaxResults( getMaxExport() );
		entityIterable.setSessionFactory( getExportFactory() );
    	return entityIterable;
    }

    private QueryIterable<Element> getElementEntityIterable() {
    	QueryIterable<Element> entityIterable = new QueryIterable<Element>();
		entityIterable.setMaxResults( getMaxExport() );
		entityIterable.setSessionFactory( getExportFactory() );
		entityIterable.setAsElement(true);
    	return entityIterable;
    }
    
    public void execute() throws EntityProcessException {
    	if ( isOnTheFly() ) {
    		importer = new OnTheFlyReplicator( this, getEntityIterable(), isInsert() );
        	importData();    		
    	} else {
	    	if ( isExportData() ) {
	    		exportData();
	    	}
	    	if ( isImportData() ) {
	    		importer = new XMLToDBImporter( this );
	        	importData();    		
	    	}
    	}
    }
	
}