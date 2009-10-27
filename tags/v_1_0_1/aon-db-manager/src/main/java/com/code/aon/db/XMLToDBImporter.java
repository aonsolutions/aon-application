package com.code.aon.db;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;

public class XMLToDBImporter implements IEntityManager {

	private static Log LOGGER = LogFactory.getLog(XMLToDBImporter.class.getName());
	
	private HibernateDataManager hdm;
	
	public XMLToDBImporter(HibernateDataManager hdm) {
		this.hdm = hdm;
	}
	
	public void proccess(Class entity) throws EntityProcessException {
		LOGGER.info( "Importing entity " + entity );
		try {
			String entityName = ClassUtils.getShortClassName(entity);
			IEntityVisitor visitor = new EntityImportVisitor(hdm.getImportFactory(), hdm.getMaxImport(), entity);
			SAXEntityReader reader = new SAXEntityReader( entityName, visitor );
			InputStream in = new FileInputStream( hdm.getFile(entity) );
			reader.parse( new InputSource(in) );
			in.close();
		} catch ( Throwable th ) {
			throw new EntityProcessException( th );			
		}
	}
	
}
