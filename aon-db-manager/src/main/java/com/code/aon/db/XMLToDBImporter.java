package com.code.aon.db;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import org.xml.sax.InputSource;

public class XMLToDBImporter implements IEntityManager {

	private static final Logger LOGGER = Logger.getLogger(XMLToDBImporter.class.getName());
	
	private HibernateDataManager hdm;
	
	public XMLToDBImporter(HibernateDataManager hdm) {
		this.hdm = hdm;
	}
	
	public void proccess(Class entity) throws EntityProcessException {
		LOGGER.info( "Importing entity " + entity );
		try {
			IEntityVisitor visitor = new EntityImportVisitor(hdm.getImportFactory(), hdm.getMaxImport(), entity);
			SAXEntityReader reader = new SAXEntityReader( visitor );
			InputStream in = new FileInputStream( hdm.getFile(entity) );
			reader.parse( new InputSource(in) );
			in.close();
		} catch ( Throwable th ) {
			throw new EntityProcessException( th );			
		}
	}
	
}
