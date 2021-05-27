package com.code.aon.db;

import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBToXMLExporter implements IEntityManager<Element> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DBToXMLExporter.class.getName());
	
	private HibernateDataManager hdm;
	
	private QueryIterable<Element> entityIterable;	
	
	public DBToXMLExporter(HibernateDataManager hdm, QueryIterable<Element> entityIterable ) {
		this.hdm = hdm;
		this.entityIterable = entityIterable;
	}
    
	public void proccess(Class<Element> entity) throws EntityProcessException {
		try {
	        XMLWriter writer = hdm.startDocument(entity);
		       
			this.entityIterable.setEntity( entity );
	        LOGGER.info( "Enitity: " + entity + " rows " + entityIterable.getRowCount() );
	        int counter = 0;
	        for( Object element : entityIterable ) {
	        	writer.write( element );
	        	if ( ++counter == hdm.getExportFlush() ) {
					writer.flush();
					counter = 0;
	        	}
	        }
	        hdm.endDocument(false);
		} catch ( Throwable th ) {
			throw new EntityProcessException( th );
		}
	}
	
}
