package com.code.aon.db.ant;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import com.code.aon.db.HibernateDataManager;

public class HibernateDataManagerTask extends Task {

	private HibernateDataManager hdm;
	
	private Path classPath;
	
	private List<Entity> includeEntities;
	
	private List<Entity> excludeEntities;
	
	public HibernateDataManagerTask() {
		this.hdm = new HibernateDataManager();
		this.includeEntities = new ArrayList<Entity>();
		this.excludeEntities = new ArrayList<Entity>();
	}

	public void setConfigurationFile(File configurationFile) {
		this.hdm.setConfigurationFile( configurationFile );
	}

	public void setImportProperties(File importProperties) {
		this.hdm.setImportProperties( importProperties );
	}

	public void setExportProperties(File exportProperties) {
		this.hdm.setExportProperties( exportProperties );
	}
	
	public void setExportData(boolean exportData) {
		this.hdm.setExportData(exportData);
	}

	public void setImportData(boolean importData) {
		this.hdm.setImportData(importData);
	}
	
	public void setOnTheFly(boolean onTheFly) {
		this.hdm.setOnTheFly(onTheFly);
	}

	public void setDirectory(File directory) {
		this.hdm.setDirectory(directory);		
	}
	
	public void setIgnoreDependencies(boolean ignoreDependencies) {
		this.hdm.setIgnoreDependencies( ignoreDependencies );
	}

	public void setInsert(boolean insert) {
		this.hdm.setInsert( insert );
	}
	
	public void setMaxImport(int maxImport) {
		this.hdm.setMaxImport( maxImport );
	}

	public void setMaxExport(int maxExport) {
		this.hdm.setMaxExport( maxExport );
	}

	public void setExportFlush(int exportFlush) {
		this.hdm.setExportFlush( exportFlush );
	}

	public void setClasspath(Path s) {
    	classPath = s;
	}

	public Path createClasspath() {
		classPath = new Path(getProject());
		return classPath;
	}
	
    private void checkFile( File file, String attribute ) throws BuildException {
   		if ( file == null ) {
   			throw new BuildException( attribute + " attribute must be set" );
   		}
   		if (! file.exists() ) {
   			throw new BuildException( attribute + " doesn't exist: " + file );
   		}
    }
    
    private void validateParameters( AntClassLoader loader ) throws BuildException {
    	if (! (hdm.isExportData() || hdm.isImportData() || hdm.isOnTheFly()) ) {
    		throw new BuildException( "One of exportData, importData or onTheFly attributes must be set" );
    	}
    	if ( hdm.isExportData() || hdm.isImportData() ) {
    		checkFile( hdm.getDirectory(), "directory" );
    	}
    	checkFile( hdm.getConfigurationFile(), "configurationFile" );
    	if ( hdm.isExportData() || hdm.isOnTheFly() ) {
        	checkFile( hdm.getExportProperties(), "exportProperties" );    		
        	try {
        		hdm.getExportFactory();
        	} catch ( Throwable th ) {
        		throw new BuildException( "Error connecting to export DB", th );
        	}
    	}
    	if ( hdm.isImportData() || hdm.isOnTheFly() ) {
        	checkFile( hdm.getImportProperties(), "importProperties" );    		
        	try {
        		hdm.getImportFactory();
        	} catch ( Throwable th ) {
        		throw new BuildException( "Error connecting to import DB", th );
        	}
    	}
    	if (! includeEntities.isEmpty() ) {
    		List<Class<? extends Serializable>> entities = new ArrayList<Class<? extends Serializable>>();
    		for( Entity entity : includeEntities ) {
    			try {
					Class _class = loader.loadClass(entity.getEntity());
					entities.add( _class );
				} catch (ClassNotFoundException e) {
					throw new BuildException( "Class not found " + entity.getEntity(), e );
				}
    		}
    		this.hdm.setIncludeEntities(entities);
    	}
    	if (! excludeEntities.isEmpty() ) {
    		List<Class<? extends Serializable>> entities = new ArrayList<Class<? extends Serializable>>();
    		for( Entity entity : excludeEntities ) {
    			try {
					Class _class = loader.loadClass(entity.getEntity());
					entities.add( _class );
				} catch (ClassNotFoundException e) {
					throw new BuildException( "Class not found " + entity.getEntity(), e );
				}
    		}
    		this.hdm.setExcludeEntities(entities);
    	}
    }
    
    public Entity createIncludeEntity() {
    	Entity entity = new Entity();
        includeEntities.add(entity);
        return entity;
    }    

    public Entity createExcludeEntity() {
    	Entity entity = new Entity();
        excludeEntities.add(entity);
        return entity;
    }    
    
	@Override
	public void execute() throws BuildException {
		AntClassLoader loader = null;
		try {
			loader = getProject().createClassLoader(classPath);
	        ClassLoader classLoader = getClass().getClassLoader();
	        loader.setParent(classLoader);
	        loader.setThreadContextLoader();
			validateParameters(loader);
	        hdm.execute();
		} catch (Throwable e) {
			throw new BuildException( e.getMessage(), e );
		} finally {
			if(loader != null) {
				loader.resetThreadContextLoader();
				loader.cleanup();
			}			
		}
	}

}