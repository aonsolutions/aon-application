package com.code.aon.google.apis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.commons.io.IOUtils;

import com.esferalia.aon.google.sql.AbstractSQL.Rattach;


public class Utils{
	
	public static class PasswordGenerator {

		public static final String NUMEROS = "0123456789";

		public static final String MAYUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		public static final String MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";

		//public static final String ESPECIALES = "Ò—";

		//
		public static String getPinNumber() {
			return getPassword(NUMEROS, 4);
		}

		public static String getPassword() {
			return getPassword(8);
		}

		public static String getPassword(int length) {
			return getPassword(NUMEROS + MAYUSCULAS + MINUSCULAS, length);
		}

		public static String getPassword(String key, int length) {
			String pswd = "";

			for (int i = 0; i < length; i++) {
				pswd += key.charAt((int) (Math.random() * key.length()));
			}

			return pswd;
		}
	}
	
	public static File InputStreamToFile(Rattach rattach) throws IOException{
		byte[] data = IOUtils.toByteArray(rattach.getData());
		File aux = new File("/tmp/" + rattach.getDescription() );
		if(!aux.isDirectory())
		org.apache.commons.io.FileUtils.writeByteArrayToFile(aux, data);
		return aux;
		
	}
	
	public static File InputStreamToFile(FileInfo fileInfo) throws IOException{
		System.out.println(fileInfo.getData());
		
		byte[] data = fileInfo.getData();

		File aux = new File("/tmp/"+ fileInfo.getTitle());		
		
		if(!aux.isDirectory())
			//Apache commons
			org.apache.commons.io.FileUtils.writeByteArrayToFile(aux, data);
		
		//Google Guava
		//Files.write(data, aux);
		
		return aux;
		
	}
	
	public static byte[] InputStreamToByte(InputStream file) throws IOException{
		
		byte[] data=IOUtils.toByteArray(file);
		return data;
		
		
		
	}
	
	public static InputStream fileToInputStream(File file) throws FileNotFoundException{
		InputStream is = new FileInputStream(file);
		return is;
	}

	/************************************ QUICKSORT *********************************/
	private  static List list;
	private static int number;
	
	public static <T extends Comparable<? super T>> List<T> sort(List<T> l){
		list = l;
		number = l.size();
		quicksort(0,number - 1);
		
		Collections.sort(l);
		return list;
	}

	static void quicksort(int low, int high){
		int i = low, j = high;
		/*String pivot = list.get(low + (high - low) / 2).getTitle();
		while (i <= j) {
			while ( list.get(i).getTitle().compareTo(pivot)<0) {
				i++;
			}
			while (list.get(j).getTitle().compareTo(pivot)>0) {
				j--;
			}
			if (i <= j) {
				exchange(i, j);
				i++;
				j--;
			}
		}*/
		if (low < j)
			quicksort(low, j);
		if (i < high)
			quicksort(i, high);
	}


	
	static void exchange(int i, int j) {
		Object aux = list.get(i);
		list.set(i, list.get(j));
		list.set(j, aux);
	}
	
	/******************************** SEARCH ************************************/
	
	public static <T extends Comparable<? super T>> int searchFiles(List<T> l , String dato, int n) {
		int centro;
		int inf = 0;
		int sup = n - 1;
		while (inf <= sup) {
			centro = (sup + inf) / 2;
			/*if (l.get(centro).getTitle().compareTo(dato)== 0) {
				return centro;
			} else if (l.get(centro).getTitle().compareTo(dato)>0) {
				sup = centro - 1;
			} else {
				inf = centro + 1;
			}*/
		}
		return -1;
	}

	/******************************* CHECK GMAIL *******************************/
	
	public static Boolean isGmail(String email) throws NamingException{
		  
		int pos= email.indexOf("@");
		String username = email.substring(0, pos);
		String hostname = email.substring(pos+1);
		Attribute attr = doLookup(hostname);

		int i=0;
		if (attr!=null){
			while(i<attr.size()){
				String a = (String) attr.get(i);
				if (a.contains("google.com") || a.contains("googlemail.com")){
					return true;
				}
				i++;
			}
		}
		  
		  return false;	  
	  }
	  
	  static Attribute doLookup( String hostName ) throws NamingException {
	    Hashtable env = new Hashtable();
	    env.put("java.naming.factory.initial",
	            "com.sun.jndi.dns.DnsContextFactory");
	    DirContext ictx = new InitialDirContext( env );
	    Attributes attrs = 
	       ictx.getAttributes( hostName, new String[] { "MX" });
	    Attribute attr = attrs.get( "MX" );
	   return attr;
	  }
}
