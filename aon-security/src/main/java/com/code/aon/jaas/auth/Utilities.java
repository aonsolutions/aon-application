/**
 * 
 */
package com.code.aon.jaas.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.jmx.adaptor.rmi.RMIAdaptor;

import com.code.aon.jaas.auth.util.Util;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.core.AstLoader;
import com.code.aon.jaas.deployment.ast.AstException;
import com.code.aon.jaas.storage.DomainStorage;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 22/12/2006
 *
 */
@SuppressWarnings("unchecked")
public class Utilities {

	Properties properties = new Properties();
	Map<String, Class[]> parameters;
	
	Utilities() {
		properties.put( Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory" );
		properties.put( Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces" );
		properties.put( Context.PROVIDER_URL, "jnp://127.0.0.1:1099/" );
		
		parameters = new HashMap<String, Class[]>();
		parameters.put( "showPassword", new Class[] {Object.class, Object.class} );
		parameters.put( "loadDomain", new Class[] {String.class} );
		parameters.put( "loadUsers", new Class[] {String.class} );
	}

	public void showPassword(Object name, Object passwd) {
		String hashAlgorithm = "SHA-256";
		String hashEncoding = "base64";
		String password = 
			Util.createPasswordHash( hashAlgorithm, hashEncoding, "", (String) name, (String) passwd);
		System.out.println( name + ", Clave:" + password );
//		passwd = Util.createPasswordHash( "SHA-256", "base64", "", "nare", "nare123");
//		passwd = Util.createPasswordHash( "SHA-256", "base64", "", "admin", "md115278");
//		passwd = Util.createPasswordHash( "SHA-256", "base64", "", "invitado", "demo");
//		passwd = Util.createPasswordHash( "SHA-256", "base64", "", "ia", "ia88");
//		passwd = Util.createPasswordHash( "SHA-256", "base64", "", "MLOPEZ", "ML1");
//		System.out.println( "Invitado, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "invitado", "demo") );
//		System.out.println( "Invitado, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "x", "xxxxxxxx1") );
//	SECURITY
////	DEMO
//		System.out.println( "Admin, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "admin", "demo2007") );
//		System.out.println( "Admin, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "admin", "security2007") );
//		System.out.println( "emarcos, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "emarcos", "em210469") );
////	DEMO
//		System.out.println( "furrutxi, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "furrutxi", "txi2005") );
//	AON-ACADEMY
//	DEMO
//		System.out.println( "fernando, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "fernando", "Urrutxi") );
//		System.out.println( "eduardo, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "eduardo", "Cortaberria") );
//		System.out.println( "julio, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "julio", "Garcia") );
//		System.out.println( "marta, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "marta", "Arevalillo") );
////	DINI
//		System.out.println( "admin, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "admin", "min2156") );
//		System.out.println( "dini, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "dini", "ni2156") );
//		System.out.println( "lakua, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "lakua", "kua2156") );
//		System.out.println( "beato, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "beato", "to2156") );
//		System.out.println( "zabalgana, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "zabalgana", "na2156") );
//		System.out.println( "clemente, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "clemente", "nte2156") );
//		System.out.println( "silvia, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "silvia", "via2156") );
//		System.out.println( "mjesus, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "mjesus", "sus2156") );
//		System.out.println( "presen, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "presen", "sen2156") );
////	TEACHERS
//		System.out.println( "Admin, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "admin", "teacher2007") );
//		System.out.println( "Sonia, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "sonia", "TeSo0807a") );
//		System.out.println( "Mentxu, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "mentxu", "TeMe0807u") );
//		System.out.println( "Fran, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "fran", "TeFr0807n") );
//		System.out.println( "campus, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "campus", "pus2057") );
//		System.out.println( "contacto, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "contacto", "cto2057") );
//		System.out.println( "fguerand, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "fguerand", "and2057") );
//		System.out.println( "gaelle, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "gaelle", "lle2057") );
//		System.out.println( "heminio, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "heminio", "nio2057") );
//		System.out.println( "inge, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "inge", "nge2057") );
//		System.out.println( "jitka, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "jitka", "tka2057") );
//		System.out.println( "joanne, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "joanne", "nne2057") );
//		System.out.println( "laura, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "laura", "ura2057") );
//		System.out.println( "mathiew, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "mathiew", "iew2057") );
//		System.out.println( "michael, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "michael", "ael2057") );
//		System.out.println( "pia, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "pia", "pia2057") );
//		System.out.println( "rick, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "rick", "ick2057") );
//		System.out.println( "sandra, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "sandra", "dra2057") );
//		System.out.println( "sanprudencio, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "sanprudencio", "cio2057") );
//		System.out.println( "yuliya, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "yuliya", "iya2057") );
//		System.out.println( "marc, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "marc", "arc2057") );
//		System.out.println( "eric, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "eric", "ric2057") );
//	GTA
////	MARIGORTA
//		System.out.println( "Andoni, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "andoni", "a01006691") );
	}

	public void loadDomain(String fileName) 
				throws NamingException, IOException, AstException, MalformedObjectNameException, NullPointerException, InstanceNotFoundException, MBeanException, ReflectionException {
		InitialContext ic = new InitialContext(properties);
    	RMIAdaptor server = (RMIAdaptor) ic.lookup("jmx/invoker/RMIAdaptor");

    	FileInputStream fis = new FileInputStream( new File( fileName ) );
		DomainStorage es = (DomainStorage) AstLoader.getInstance().parse( 1, fis );
		ObjectName oname = new ObjectName( "jboss.admin:service=AonSecurity" );
		Object[] params = { es.getDomain() };
		String[] sig = {IDomain.class.getName()};
		server.invoke( oname, "loadDomain", params, sig );
	}

	public void loadUsers(String fileName)
				throws NamingException, IOException, AstException, MalformedObjectNameException, NullPointerException, InstanceNotFoundException, MBeanException, ReflectionException {
		InitialContext ic = new InitialContext(properties);
    	RMIAdaptor server = (RMIAdaptor) ic.lookup("jmx/invoker/RMIAdaptor");
    	
    	FileInputStream fis = new FileInputStream( new File( fileName ) );
		DomainStorage es = (DomainStorage) AstLoader.getInstance().parse( 1, fis );
		ObjectName oname = new ObjectName( "jboss.admin:service=AonSecurity");
		Object[] params = { es.getDomain() };
		String[] sig = {IDomain.class.getName()};
		server.invoke( oname, "loadUsers", params, sig );
	}

	public static void main(String[] args) {
		String methodName = null;
		List<Object> arguments = new ArrayList<Object>();
		for(int i=0; i < args.length; i++) {
			if ( args[i].equals( "-method" ) ) {
				methodName = args[ i + 1 ];
				for(int j=i+2; j < args.length; j++) {
					arguments.add( args[j] );
				}
		        break;
			}
		}
		if ( methodName != null ) {
			try {
				Utilities utilities = new Utilities();
				Class parameters[] = utilities.parameters.get( methodName );
				Method method = utilities.getClass().getMethod( methodName, parameters );
				method.invoke( utilities, arguments.toArray() );
			} catch (Exception e) {
				e.printStackTrace(); 
			}
		}
	}

}
