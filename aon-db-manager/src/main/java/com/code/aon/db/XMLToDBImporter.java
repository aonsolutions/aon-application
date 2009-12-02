package com.code.aon.db;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import org.xml.sax.InputSource;

public class XMLToDBImporter implements IEntityManager {

	private static final Logger LOGGER = Logger.getLogger(XMLToDBImporter.class.getName());
	
	private HibernateDataManager hdm;
	
	private IEntityVisitor visitor;
	
	public XMLToDBImporter(HibernateDataManager hdm, IEntityVisitor visitor) {
		this.hdm = hdm;
		this.visitor = visitor;
	}
	
	public void proccess(Class entity) throws EntityProcessException {
		LOGGER.info( "Importing entity " + entity );
		try {
			SAXEntityReader reader = new SAXEntityReader( visitor, entity );
			InputStream in = new FileInputStream( hdm.getFile(entity) );
			reader.parse( new InputSource(in) );
			in.close();
		} catch ( Throwable th ) {
			throw new EntityProcessException( th );			
		}
	}

	public void proccess(InputStream byteStream) throws EntityProcessException {
		try {
			SAXEntityReader reader = new SAXEntityReader( visitor, hdm.getEntities() );
			reader.parse( new InputSource(byteStream) );
		} catch ( Throwable th ) {
			throw new EntityProcessException( th );			
		}
	}
	
}
