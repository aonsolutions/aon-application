package com.code.aon.common.dao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.metadata.ClassMetadata;

import com.code.aon.common.dao.hibernate.HibernateUtil;

/**
 * This class manages the generation of DAOConstantsEntry collections interface.
 * 
 * @author Consulting & Development. 
 *
 */
public class AliasWriter {

	private String relativePackage;
	
	private Configuration configuration;

	/**
	 * Constructor the hibernate configuration and a package of classes.
	 * 
	 * @param configuration
	 * @param package1
	 */
	public AliasWriter(Configuration configuration, String package1) {
		this.configuration = configuration;
		this.relativePackage = package1;
	}

	/**
	 * Constructor a package of classes.
	 * 
	 * @param package1
	 */
	public AliasWriter( String package1) {
		this.relativePackage = package1;
	}

	/**
	 * Return the Class mappings.
	 * 
	 * @return The Class mappings.
	 */
	private String[] getClassMappings() {
		List<String> list = new ArrayList<String>();
		if ( this.configuration != null ) {
			Iterator i = this.configuration.getClassMappings();
			while ( i.hasNext() ) {
				PersistentClass pc = (PersistentClass) i.next();
				list.add( pc.getClassName() );
			}
		} else {
			Map map = HibernateUtil.getSessionFactory().getAllClassMetadata();
			Iterator i = map.values().iterator();
			while ( i.hasNext() ) {
				ClassMetadata cmd = (ClassMetadata) i.next();
				list.add( cmd.getEntityName() );
			}
		}
		return list.toArray( new String[list.size()] );
	}

	/**
	 * Format the Class field name.
	 * 
	 * @param name
	 * @return The Class field name.
	 */
	private static String format( String name ) {
		StringBuffer sb = new StringBuffer( name );
		for( int i = sb.length()-1; i > 0; i-- ) {
			if ( Character.isUpperCase(sb.charAt(i)) && Character.isLowerCase(sb.charAt(i-1)) ) {
				sb.insert( i, "_" );
			}
		}
		sb.setCharAt( 0, Character.toUpperCase(sb.charAt(0)) );
		return sb.toString().toUpperCase();
	}

	/**
	 * Write mappings to file.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void write( File file ) throws IOException {
		write( getClassMappings(), file );
	}

	/**
	 * Write mappings to file.
	 * 
	 * @param classes
	 * @param file
	 * @throws IOException
	 */
	public void write( String[] classes, File file ) throws IOException {
		BufferedWriter out = new BufferedWriter( new FileWriter(file) );
		
		out.write( "package " );
		out.write( this.relativePackage );
		out.write( ';' );
		out.newLine();
		out.newLine();
		
		out.write( "import com.code.aon.common.dao.DAOConstants;" );
		out.newLine();
		out.write( "import com.code.aon.common.dao.DAOConstantsEntry;" );
		out.newLine();

		for( String _class: classes ) {
			out.write( "import " );
			out.write( _class );
			out.write( ';' );
			out.newLine();
		}
		
		out.newLine();
		out.write( "/** " );
		out.newLine();
		out.write( "* Interface for holding entity properties constants." );
		out.newLine();
		out.write( "*/ " );
		out.newLine();
		
		out.write( "public interface " );
		String name = file.getName();
		int pos = name.lastIndexOf( '.' );
		if ( pos != -1 ) {
			name = name.substring( 0, pos );
		}
		out.write( name );
		out.write( " {" );
		out.newLine();
		out.newLine();
		
		for( String _class: classes ) {
			write( out, _class );
		}
		
		out.newLine();
		out.write( "}" );
		out.close();
	}

	/**
	 * Write mappings to file.
	 * 
	 * @param out
	 * @param className
	 * @throws IOException
	 */
	private void write( BufferedWriter out, String className ) throws IOException {
		DAOConstantsEntry entry = DAOConstants.getDAOConstant( className );

		out.newLine();		
		out.newLine();
		out.write( "\t/** " );
		out.newLine();
		out.write( "\t* DAOConstantsEntry for " + entry.getName() +  " entity.");
		out.newLine();
		out.write( "\t*/ " );
		out.newLine();
		String classAliasConstant =  format( entry.getName() ) + "_ENTRY";
		out.write( "\tDAOConstantsEntry " );
		out.write( classAliasConstant );
		out.write( " = DAOConstants.getDAOConstant(" );
		out.write( entry.getName() );
		out.write( ".class);" );
		out.newLine();
		out.newLine();
	
		String[] aliases = entry.getAliasNames();
		Map hibernates = entry.getHibernateMap();		
		for( int i = 0; i < aliases.length; i++ ) {
			String alias = aliases[i];
			String hibernate = hibernates.get(alias).toString();
			out.write( "\t/** " );
			out.newLine();
			out.write( "\t* Alias value: " );
			out.write( alias );
			out.newLine();
			out.write( "\t* Hibernate value: " );
			out.write( hibernate );
			out.newLine();
			out.write( "\t*/" );
			out.newLine();
			out.write( "\tString  " );
			out.write( format(alias) );
			out.write( " = " );
			out.write( classAliasConstant );
			out.write( ".getAliasNames()[" + i );
			out.write( "];" );
			out.newLine();
			out.newLine();			
		}
	}

}
