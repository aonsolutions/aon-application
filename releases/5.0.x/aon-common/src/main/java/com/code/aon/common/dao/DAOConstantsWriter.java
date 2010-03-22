package com.code.aon.common.dao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.Joinable;

import com.code.aon.common.dao.hibernate.HibernateUtil;

/**
 * Writes field constants to a phisical file.
 * 
 * @author Consulting & Development. 
 *
 */
public class DAOConstantsWriter {

	/**
	 * Write field constants to file.
	 * 
	 * @param file
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void write( File file ) throws IOException {
		Map map = HibernateUtil.getSessionFactory().getAllClassMetadata();
		Collection values = map.values();
		write( (ClassMetadata[]) values.toArray(new ClassMetadata[0]), file );
	}

	/**
	 * Write field constants to file.
	 * 
	 * @param classes
	 * @param file
	 * @throws IOException
	 */
	public static void write( ClassMetadata[] classes, File file ) throws IOException {
		BufferedWriter out = new BufferedWriter( new FileWriter(file) );
		
		out.write( "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\"" );
		out.newLine();
		out.write( "<dao>" );
		out.newLine();		
		
		for( ClassMetadata _class: classes ) {
			write( out, _class );
		}
		
		out.write( "</dao>" );
		out.close();
	}

	/**
	 * Write field constants to file.
	 * 
	 * @param out
	 * @param cmd
	 * @throws IOException
	 */
	private static void write( BufferedWriter out, ClassMetadata cmd ) throws IOException {
		Class c = null;
		try {
			c = Class.forName(cmd.getEntityName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		DAOConstantsEntry entry = DAOConstants.getDAOConstant( c );

		out.write( "\t<bean class=\"" );
		out.write( cmd.getEntityName() );
		out.write( '\"' );		
		if ( cmd instanceof Joinable ) {
			out.write( " sqlName=\"" );
			out.write( ((Joinable) cmd).getTableName() );
			out.write( '\"' );			
		}

		if ( entry.getParent() != null ) {
			out.write( " parent=\"" );
			out.write( entry.getParent().getPojo() );
			out.write( '\"' );
		}
		out.write( ">" );
		out.newLine();
		
		out.write( "\t\t<alias names=\"" );
		String[] aliases = entry.getAliasNames();
		for( int i = 0; i < aliases.length; i++ ) {
			String alias = aliases[i];
			int pos = alias.indexOf( "_" ) + 1;
			out.write( alias.substring(pos) );
			if ( i < (aliases.length-1) ) {
				out.write( ',' );
			}
		}
		out.write( "\"/>" );
		out.newLine();
		
		out.write( "\t\t<hibernate names=\"" );
		Map hibernates = entry.getHibernateMap();
		for( int i = 0; i < aliases.length; i++ ) {
			String hibernate = hibernates.get(aliases[i]).toString();
			int pos = hibernate.indexOf( "." ) + 1;
			out.write( hibernate.substring(pos) );
			if ( i < (aliases.length-1) ) {
				out.write( ',' );
			}
		}
		out.write( "\"/>" );
		out.newLine();

		out.write( "\t</bean>" );
		out.newLine();		
	}

}
