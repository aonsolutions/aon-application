package com.code.aon.google.apis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.esferalia.aon.google.sql.AbstractSQL.Rattach;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.FileList;


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
		byte[] data = IOUtils.toByteArray(fileInfo.getData());
		File aux = new File("/tmp/" + fileInfo.getTitle() );
		if(!aux.isDirectory())
		org.apache.commons.io.FileUtils.writeByteArrayToFile(aux, data);
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

}
