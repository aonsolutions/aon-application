package com.code.aon.db;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;
import org.xml.sax.SAXException;

import com.code.aon.db.hibernate.ExportConfigurationPatcher;
import com.code.aon.db.hibernate.IConfigurationPatcher;

public class HibernateDataManager {
	
	private static final int DEFAULT_MAX_IMPORT = 1000;
	
	public static final int DEFAULT_MAX_EXPORT = 5000;
	
	private static final int DEFAULT_EXPORT_FLUSH = 500;
	
	private static final Logger LOGGER = Logger.getLogger(HibernateDataManager.class.getName());
	
	private File directory;
	
	private File file;
	
	private InputStream inputStream;
	
	private XMLWriter xmlWriter;
	
	private Element root;
	
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
	
	private boolean onDemand;
	
	private List<Class<? extends Serializable>> includeEntities;
	
	private List<Class<? extends Serializable>> excludeEntities;
	
	private Configuration importConfiguration;
	
	private SessionFactory importFactory;
	
	private Configuration exportConfiguration;	
	
	private SessionFactory exportFactory;
	
	private IEntityManager importer;
	
	private IEntityVisitor visitor;
	
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
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	private boolean isOneXml() {
		return (getFile() != null) || (getInputStream() != null);
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
	
	public void setImportFactory(SessionFactory importFactory) {
		this.importFactory = importFactory;
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

	private Configuration getExportConfiguration( IConfigurationPatcher patcher ) {
		if ( exportConfiguration == null ) {
			exportConfiguration = createConfiguration(configurationFile, exportProperties, patcher);
		}
		return exportConfiguration;
	}
	
	public void setExportFactory(SessionFactory exportFactory) {
		this.exportFactory = exportFactory;
	}

	public SessionFactory getExportFactory() {
		if ( exportFactory == null ) {
			Configuration cfg = createConfiguration(configurationFile, exportProperties); 
			ExportConfigurationPatcher patcher = new ExportConfigurationPatcher();
			patcher.setSessionFactory( cfg.buildSessionFactory() );
			exportFactory = getExportConfiguration(patcher).buildSessionFactory();
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
			SessionFactory factory = ( isExportData() ? getExportFactory() : getImportFactory() );
			Iterator i = factory.getAllClassMetadata().values().iterator();
			while ( i.hasNext() ) {
				ClassMetadata cm = (ClassMetadata) i.next();
				includeEntities.add( cm.getMappedClass(EntityMode.POJO) );
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

	public boolean isOnDemand() {
		return onDemand;
	}

	public void setOnDemand(boolean onDemand) {
		this.onDemand = onDemand;
	}

	public IEntityVisitor getVisitor() {
		if ( visitor == null ) {
    		this.visitor = new EntityImportVisitor(getImportFactory(), getMaxImport());	
		}
		return visitor;
	}

	public void setVisitor(IEntityVisitor visitor) {
		this.visitor = visitor;
	}

	public static Properties loadProperties( File file ) {
		Properties properties = new Properties();
		try {
			InputStream in = new FileInputStream( file );
			properties.load( in );
			in.close();			
		} catch (IOException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		}
		return properties;
	}

    public static AnnotationConfiguration createConfiguration( File configurationFle, File propertiesFile ) {
    	return createConfiguration(configurationFle, propertiesFile, null);
    }
	
    public static AnnotationConfiguration createConfiguration( File configurationFle, File propertiesFile, IConfigurationPatcher patcher ) {
    	AnnotationConfiguration configuration = null;
        try {
    		configuration = new AnnotationConfiguration();
    		Properties properties = loadProperties(propertiesFile);
    		configuration.addProperties( properties );
    		if ( patcher != null ) {
    			patcher.completeConfiguration(configuration);
    		}
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
    
    private XMLWriter createWriter( File file ) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();   
        format.setEncoding( "UTF-8" );
        BufferedWriter out = new BufferedWriter( new FileWriter(file) );
        XMLWriter writer = new XMLWriter( out, format );
        writer.setMaximumAllowedCharacter(0x7F);
        return writer;
    }
    
    private void startDocument( XMLWriter writer ) throws IOException, SAXException {
        this.root = new DefaultElement( "root" );
        writer.startDocument();
        writer.writeOpen( root );    		    	
    }
    
    public XMLWriter startDocument( Class<Element> entity ) throws EntityProcessException {
		try {    	
	    	if ( isOneXml() ) {
	    		if ( this.xmlWriter == null ) {
	        		this.xmlWriter = createWriter( getFile() );
	        		startDocument(this.xmlWriter);    			
	    		}
	    	} else {
	    		this.xmlWriter = createWriter( getFile(entity) );
	    		startDocument(this.xmlWriter);
	    	}
		} catch ( Throwable th ) {
			throw new EntityProcessException( th );
		}
    	return this.xmlWriter;
    }

    public void endDocument( boolean force ) throws EntityProcessException {
    	if ( force || (!isOneXml()) ) {
			if ( this.xmlWriter != null ) {
				try {
			        this.xmlWriter.writeClose( root );
			        this.xmlWriter.endDocument();
			        this.xmlWriter.close();
			        this.xmlWriter = null;
			        this.root = null;
	    		} catch ( Throwable th ) {
	    			throw new EntityProcessException( th );
	    		}
			}
    	}
    }
    
    public void exportData() throws EntityProcessException {
    	DBToXMLExporter exporter = new DBToXMLExporter( this, getElementEntityIterable() );
    	List<Class<? extends Serializable>> entities = getEntities();
    	if (! ignoreDependencies ) {
	   		DependencyResolver dr = new DependencyResolver( getExportFactory() );
	   		entities = dr.organize(entities);
    	}
    	for( Class entity : entities ) {
    		exporter.proccess(entity);
    	}
    	endDocument(true);
    }
    
    public void importData() throws EntityProcessException {
    	Set<Class> imported = new HashSet<Class>();
    	Set<Class> processed = new HashSet<Class>();
    	List<Class<? extends Serializable>> entities = getEntities();
    	if (! ignoreDependencies ) {
    		DependencyResolver dr = new DependencyResolver( getImportFactory() );
    		entities = dr.organize(entities);
    	}
		if (onDemand) {
    		Collections.sort( entities, new CountComparator(getExportFactory()) );	
		}
    	for( Class entity : entities ) {
   			importer.proccess( entity );    			
    	}
    }
    
    public void importDataOneXml() throws EntityProcessException {
    	try {
	    	InputStream byteStream = null;
	    	if ( getInputStream() != null ) {
	    		byteStream = getInputStream();
	    	} else {
	    		byteStream = new BufferedInputStream( new FileInputStream(getFile()) );
	    	}
	    	XMLToDBImporter xmlImporter = (XMLToDBImporter) this.importer;
	    	xmlImporter.proccess(byteStream);
	    	if ( getFile() != null ) {
	    		byteStream.close();
	    	}
    	} catch ( IOException e) {
    		throw new EntityProcessException( e.getMessage(), e );
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
    		((OnTheFlyReplicator) importer).setOnDemand( isOnDemand() );
        	importData();    		
    	} else {
	    	if ( isExportData() ) {
	    		exportData();
	    	}
	    	if ( isImportData() ) {
	    		importer = new XMLToDBImporter( this, getVisitor() );
	    		if ( isOneXml() ) {
	    			importDataOneXml();
	    		} else {
		        	importData();	
	    		}    		
	    	}
    	}
    }
    
    private static void _export() throws EntityProcessException {
    	HibernateDataManager hdm = new HibernateDataManager();
    	hdm.setExportData(true);
    	// hdm.setFile( new File("/tmp/aon_master.xml") );
    	hdm.setDirectory( new File("/tmp/tol/export") );
    	hdm.setConfigurationFile( new File("/AON-PROJECT/aon-cse-util/ant/hibernate.cfg.xml") );
    	hdm.setExportProperties( new File("/AON-PROJECT/aon-cse-util/ant/ctsql.properties") );
    	hdm.setMaxExport(50000);
    	hdm.setIgnoreDependencies(true);
    	hdm.execute();
    }

    private static void _import() throws EntityProcessException {
    	HibernateDataManager hdm = new HibernateDataManager();
    	hdm.setImportData(true);
    	// hdm.setFile( new File("/tmp/aon_master.xml") );
    	hdm.setDirectory( new File("/tmp/tol/export") );
    	hdm.setConfigurationFile( new File("/AON-PROJECT/aon-cse-util/ant/hibernate.cfg.xml") );
    	hdm.setImportProperties( new File("/AON-PROJECT/aon-cse-util/ant/mysql.properties") );    	
    	hdm.execute();
    }
    
    private static void _onTheFly() throws EntityProcessException {
    	HibernateDataManager hdm = new HibernateDataManager();
    	hdm.setOnTheFly(true);
    	hdm.setConfigurationFile( new File("/AON-PROJECT/aon-cse-util/ant/hibernate.cfg.xml") );
    	hdm.setExportProperties( new File("/AON-PROJECT/aon-cse-util/ant/ctsql.properties") );
    	hdm.setImportProperties( new File("/AON-PROJECT/aon-cse-util/ant/mysql.properties") );
    	/*
    	hdm.setIgnoreDependencies(true);
    	hdm.setMaxImport(100);
    	hdm.setMaxExport(100);
    	hdm.setOnDemand(true);
    	 */
    	hdm.setOnDemand(true);
    	hdm.execute();
    }

    public static void main(String[] args) throws EntityProcessException {
    	_onTheFly();
	}
    
}