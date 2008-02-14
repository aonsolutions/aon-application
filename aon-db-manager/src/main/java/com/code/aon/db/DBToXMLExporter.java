package com.code.aon.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;

public class DBToXMLExporter implements IEntityManager {

	private HibernateDataManager hdm;
	
	private IEntityIterable entityIterable;	
	
	public DBToXMLExporter(HibernateDataManager hdm, IEntityIterable entityIterable ) {
		this.hdm = hdm;
		this.entityIterable = entityIterable;
		this.entityIterable.setMaxResults( hdm.getMaxExport() );
		this.entityIterable.setSessionFactory( hdm.getExportFactory() );
	}

    private XMLWriter createWriter( File file ) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();   
        format.setEncoding( "UTF-8" );
        BufferedWriter out = new BufferedWriter( new FileWriter(file) );
        XMLWriter writer = new XMLWriter( out, format );
        writer.setMaximumAllowedCharacter(0x7F);
        return writer;
    }
    
	public void proccess(Class entity) throws EntityProcessException {
		try {
	        XMLWriter writer = createWriter( hdm.getFile(entity) );
	
	        Element root = new DefaultElement( "root" );
	        writer.startDocument();
	        writer.writeOpen( root );
		       
			this.entityIterable.setEntity( entity );	        
			this.entityIterable.setAsElement( true );
	        int counter = 0;
	        for( Object element : entityIterable ) {
	        	writer.write( element );
	        	if ( ++counter == hdm.getExportFlush() ) {
					writer.flush();
					counter = 0;
	        	}
	        }
	        writer.writeClose( root );
	        writer.endDocument();
	        writer.close();
		} catch ( Throwable th ) {
			throw new EntityProcessException( th );
		}
	}
	
}
