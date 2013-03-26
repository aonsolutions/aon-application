package com.esferalia.aon.payroll.contrata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.util.Classpath;

public class ContrataCodeTablesWriter {
	
	final static String ENUMERATIONS_FOLDER_PATHNAME 		= "/AON-TRUNK/aon.parent/aon-payroll/src/main/java/com/esferalia/aon/payroll/contrata/enumeration/";
	final static String TOWNS_PROPERTIES_PATHNAME 			= "/AON-TRUNK/aon.parent/aon-payroll/src/main/resources/com/esferalia/aon/payroll/i18n/towns.properties";
	final static String QUALIFICATIONS_PROPERTIES_PATHNAME 	= "/AON-TRUNK/aon.parent/aon-payroll/src/main/resources/com/esferalia/aon/payroll/i18n/qualifications.properties";
	final static String ZIP_PROPERTIES_PATHNAME 			= "/AON-TRUNK/aon.parent/aon-payroll/src/main/resources/com/esferalia/aon/payroll/i18n/zip.properties";
	final static String COLLECTIONS_CLASS_PATHNAME 			= "/AON-TRUNK/aon.parent/aon-ui-payroll/src/main/java/com/esferalia/aon/ui/payroll/controller/ContrataCollectionsController.java";
	
	final static String CODE_TXT_FILE_URL			= "com/esferalia/aon/payroll/contrata/codeTables/";
	final static String ERROR_CODE_TXT_FILE_URL		= "com/esferalia/aon/payroll/contrata/errorCodeTables/";
	
	final static String TOWNS_FILE_NAME 			= "TDPMUNIC";
	final static String LEAME_FILE_NAME 			= "LEAME";
	final static String LEAME_RESPUESTA_FILE_NAME 	= "LEAME_RESPUESTA";
	final static String CNO_1994_FILE_NAME 			= "TAICLAOC1994";
	final static String ZIP_FILE_NAME 				= "TAPCOPOS";
	final static String QUALIFICATIONS_FILE_NAME	= "THITIACA";
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file ;
		final String SUFFIX = ".txt";
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] codeUrls = Classpath.search(cl, CODE_TXT_FILE_URL, SUFFIX);
		URL[] errorCodeUrls = Classpath.search(cl, ERROR_CODE_TXT_FILE_URL, SUFFIX);
		int enumCount = 0;
		for(URL url: codeUrls){
			if(getFileNameWithoutExtension(url).equals(TOWNS_FILE_NAME)){
				file = new File(TOWNS_PROPERTIES_PATHNAME);
				System.out.print("#### towns.properties en proceso ...");
				writeProperties(url, file);
				System.out.println(" generado!");
			} else if(getFileNameWithoutExtension(url).equals(ZIP_FILE_NAME) ){
				// exclude
			} else if(getFileNameWithoutExtension(url).equals(QUALIFICATIONS_FILE_NAME) ){
//				file = new File(ENUMERATIONS_FOLDER_PATHNAME+getFileNameWithoutExtension(url)+".java");
//				System.out.print("Enum "+getFileNameWithoutExtension(url)+" en proceso ...");
//				writeQualificationsEnum(url, file);
				file = new File(QUALIFICATIONS_PROPERTIES_PATHNAME);
				System.out.print("#### qualifications.properties en proceso ...");
				writeQualificationsProperties(url, file);
				System.out.println(" generado!");
				enumCount++;
			} else if(!getFileNameWithoutExtension(url).equals(LEAME_FILE_NAME) 
					&& !getFileNameWithoutExtension(url).equals(CNO_1994_FILE_NAME) 
					&& !getFileNameWithoutExtension(url).equals(ZIP_FILE_NAME) 
					&& !getFileNameWithoutExtension(url).equals(QUALIFICATIONS_FILE_NAME)
					){
				file = new File(ENUMERATIONS_FOLDER_PATHNAME+getFileNameWithoutExtension(url)+".java");
				System.out.print("Enum "+getFileNameWithoutExtension(url)+" en proceso ...");
				writeEnum(url, file);
				System.out.println(" generado!");
				enumCount++;
			}
		}
		for(URL url: errorCodeUrls){
			if(!getFileNameWithoutExtension(url).equals(LEAME_RESPUESTA_FILE_NAME)){
				file = new File(ENUMERATIONS_FOLDER_PATHNAME+getFileNameWithoutExtension(url)+".java");
				System.out.print("Enum "+getFileNameWithoutExtension(url)+" en proceso ...");
				writeEnum(url, file);
				System.out.println(" generado!");
				enumCount++;
			}
		}
		System.out.println("*** Enumeraciones generadas. ("+enumCount+")");
		writeCollections();
		System.out.println("*** Colecciones generadas.");
		System.out.println("Proceso finalizado !!!!!!!");
	}
	
	private static String getFileNameWithoutExtension(URL url) {
	    String path = url.getPath();

	    if (StringUtils.isBlank(path)) {
	        return null;
	    }
	    if (StringUtils.endsWith(path, "/")) {
	        //it is a directory ..
	        return null;
	    }

	    File file = new File(url.getPath());
	    String fileNameWithExt = file.getName();

	    int sepPosition = fileNameWithExt.lastIndexOf(".");
	    String fileNameWithOutExt = null;
	    if (sepPosition >= 0) {
	        fileNameWithOutExt = fileNameWithExt.substring(0,sepPosition);
	    }else{
	        fileNameWithOutExt = fileNameWithExt;
	    }

	    return fileNameWithOutExt;
	}

	/**
	 * Write data to qualifications enum class.
	 * 
	 * @param url
	 * @param file
	 * @throws IOException
	 */
	private static void writeQualificationsEnum( URL url, File file ) throws IOException {
		BufferedWriter out = new BufferedWriter( new FileWriter(file) );
		
		writeEnumHeader(out, url, obtainTableDescription(getFileNameWithoutExtension(url)));
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String currentLine;
		while((currentLine = reader.readLine()) != null) {
			currentLine = StringUtils.strip(currentLine);
			String code = currentLine.substring(0, 12);
			String value = currentLine.substring(13, currentLine.length());
			
			out.write( "\t"+getFileNameWithoutExtension(url)+"_"+code.replace("\"", "").toUpperCase()+"( \""+code+"\"" );
			out.write(", \""+value+"\"");
			out.write( ", null, null )," );
			
			out.newLine();
		}
		
		writeEnumLastContent(out, url);
		
		out.close();
	}
		
	/**
	 * Write data to enum class.
	 * 
	 * @param url
	 * @param file
	 * @throws IOException
	 */
	private static void writeEnum( URL url, File file ) throws IOException {
		BufferedWriter out = new BufferedWriter( new FileWriter(file) );
		
		writeEnumHeader(out, url, obtainTableDescription(getFileNameWithoutExtension(url)));
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String currentLine;
		while((currentLine = reader.readLine()) != null) {
			StringUtils.strip(currentLine);
			currentLine = currentLine.substring(0, currentLine.lastIndexOf("\""));
			StringTokenizer token = new StringTokenizer(currentLine, "\"");
			if(token.hasMoreTokens()){
				String code = token.nextToken();
				out.write( "\t"+getFileNameWithoutExtension(url)+"_"+code.replace("\"", "").toUpperCase()+"( \""+code+"\"" );
				if(token.hasMoreTokens()){
					token.nextToken();
					if(token.hasMoreTokens()){
						String label = StringUtils.strip(token.nextToken());
						out.write(", \""+(label.length()>80?(label.substring(0, 80)+"..."):label)+"\"");
						if(token.hasMoreTokens()){
							token.nextToken();
							if(token.hasMoreTokens()){
								out.write(", \""+token.nextToken()+"\"");
								if(token.hasMoreTokens()){
									token.nextToken();
									if(token.hasMoreTokens()){
										out.write(", \""+token.nextToken()+"\" ),");
									} else {
										out.write( ", null )," );
									}
								} else {
									out.write( ", null )," );
								}
							} else {
								out.write( ", null, null )," );
							}
						} else {
							out.write( ", null, null )," );
						}
					} else {
						out.write( ", null, null, null )," );
					}
				} else {
					out.write( ", null, null, null )," );
				}
			}
			out.newLine();
		}
		
		writeEnumLastContent(out, url);
		
		out.close();
	}
	
	private static void writeEnumHeader( BufferedWriter out, URL url, String enumDescription ) throws IOException {
		out.write( "package com.esferalia.aon.payroll.contrata.enumeration;" );
		out.newLine();
		out.newLine();
		
		out.write( "import com.code.aon.common.enumeration.IStringEnum;" );
		out.newLine();
		out.newLine();
		
		out.write( "/** " );
		out.newLine();
		out.write( " * Enumeration for represent Contrata (S.E.P.E.) "+getFileNameWithoutExtension(url)+" table codes." );
		out.newLine();
		out.write( " * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter." );
		out.newLine();
		out.write( " *  ------------------------------------------------------------------------" );
		out.newLine();
		out.write( " *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN." );
		out.newLine();
		out.write( " * " + enumDescription );
		out.newLine();
		out.write( " *  ------------------------------------------------------------------------" );
		out.newLine();
		out.write( " */ " );
		out.newLine();
		out.write( "public enum " + getFileNameWithoutExtension(url) + " implements IStringEnum {");
		out.newLine();
		out.newLine();
	}
	private static void writeEnumLastContent( BufferedWriter out, URL url ) throws IOException {
		
		out.write( "\t;" );
		out.newLine();
		out.write( "\tprivate String value;" );
		out.newLine();
		out.write( "\tprivate String label;" );
		out.newLine();
		out.write( "\tprivate String startDate;" );
		out.newLine();
		out.write( "\tprivate String endDate;" );
		out.newLine();
		out.newLine();
		
		out.write( "\t"+getFileNameWithoutExtension(url)+"( String value, String label, String startDate, String endDate ) {" );
		out.newLine();
		out.write( "\t\tthis.value = value;" );
		out.newLine();
		out.write( "\t\tthis.label = label;" );
		out.newLine();
		out.write( "\t\tthis.startDate = startDate;" );
		out.newLine();
		out.write( "\t\tthis.endDate = endDate;" );
		out.newLine();
		out.write( "\t}" );
		out.newLine();
		out.newLine();
		
		out.write("\t@Override");
		out.newLine();
		out.write("\tpublic String getValue() {");
		out.newLine();
		out.write("\t\treturn value;");
		out.newLine();
		out.write("\t}");
		out.newLine();
		out.newLine();
		
		out.write("\tpublic String getLabel() {");
		out.newLine();
		out.write("\t\treturn label;");
		out.newLine();
		out.write("\t}");
		out.newLine();
		out.newLine();
		
		out.write("\tpublic String getStartDate() {");
		out.newLine();
		out.write("\t\treturn startDate;");
		out.newLine();
		out.write("\t}");
		out.newLine();
		out.newLine();
		
		out.write("\tpublic String getEndDate() {");
		out.newLine();
		out.write("\t\treturn endDate;");
		out.newLine();
		out.write("\t}");
		out.newLine();
		out.newLine();
		
		out.write("\tpublic static "+getFileNameWithoutExtension(url)+" getEnumByValue(String expression) {");
		out.newLine();
		out.write("\t\tfor( "+getFileNameWithoutExtension(url)+" o : "+getFileNameWithoutExtension(url)+".values() ) {");
		out.newLine();
		out.write("\t\t\tif ( o.getValue().equals(expression) ) {");
		out.newLine();
		out.write("\t\t\t\treturn o;");
		out.newLine();
		out.write("\t\t\t}");
		out.newLine();
		out.write("\t\t}");
		out.newLine();
		out.write("\t\treturn null;");
		out.newLine();
		out.write("\t}");
		out.newLine();
		out.newLine();
		
		out.write( "}" );
		
	}
	
	/**
	 * Write data to properties file.
	 * 
	 * @param url
	 * @param file
	 * @throws IOException
	 */
	private static void writeProperties( URL url, File file ) throws IOException {
		BufferedWriter out = new BufferedWriter( new FileWriter(file) );
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String currentLine;
		while((currentLine = reader.readLine()) != null) {
			StringTokenizer token = new StringTokenizer(currentLine, ";");
			String code = token.nextToken();
			String label = token.nextToken();
			out.write( code.replace("\"", "")+"="+label.replace("\"", "") );
			out.newLine();
		}
		out.close();
	}
	
	/**
	 * Write data to qualifications properties file.
	 * 
	 * @param url
	 * @param file
	 * @throws IOException
	 */
	private static void writeQualificationsProperties( URL url, File file ) throws IOException {
		BufferedWriter out = new BufferedWriter( new FileWriter(file) );
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String currentLine;
		while((currentLine = reader.readLine()) != null) {
			currentLine = StringUtils.strip(currentLine);
			String code = currentLine.substring(0, 12);
			String label = currentLine.substring(13, currentLine.length());
			out.write( code.replace("\"", "")+"="+label.replace("\"", "") );
			out.newLine();
		}
		out.close();
	}
	
	
	/**
	 * Write data to collections file.
	 * 
	 * @throws IOException
	 */
	private static void writeCollections( ) throws IOException {
		File file = new File(COLLECTIONS_CLASS_PATHNAME);
		BufferedWriter out = new BufferedWriter( new FileWriter(file) );
		
		out.write( "package com.esferalia.aon.ui.payroll.controller;" );
		out.newLine();
		out.newLine();

		out.write( "import java.util.LinkedList;" );
		out.newLine();
		out.write( "import java.util.List;" );
		out.newLine();
		out.write( "import javax.faces.model.SelectItem;" );
		out.newLine();
		out.write( "import com.esferalia.aon.payroll.contrata.enumeration.*;" );
		out.newLine();
		out.newLine();

		out.write( "/** " );
		out.newLine();
		out.write( "* Autogenerated class" );
		out.newLine();
		out.write( "* " );
		out.newLine();
		out.write( "* Contrata code tables Collections." );
		out.newLine();
		out.write( " * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter." );
		out.newLine();
		out.write( "*/ " );
		out.newLine();
		out.newLine();
		
		out.write( "public class ContrataCollectionsController {");
		out.newLine();
		out.newLine();
		
		File folder = new File(ENUMERATIONS_FOLDER_PATHNAME);
		File[] listOfFiles = folder.listFiles();
		for (File enumFile : listOfFiles) {
		    if (enumFile.isFile() && enumFile.getName().endsWith(".java")) {
		    	String enumName = enumFile.getName().replaceAll(".java", "");
				
		    	out.write( "\t/** " );
				out.newLine();
				out.write( "\t *  ------------------------------------------------------------------------" );
				out.newLine();
				out.write( "\t *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN." );
				out.newLine();
				out.write( "\t * " + obtainTableDescription(enumName) );
				out.newLine();
				out.write( "\t *  ------------------------------------------------------------------------" );
				out.newLine();
				out.write( "\t */ " );
				out.newLine();
		    	out.write( "\tprivate List<SelectItem> "+enumName+"CodeList;");
				out.newLine();
				out.newLine();
				
				out.write( "\tpublic List<SelectItem> get"+enumName+"CodeList() {");
				out.newLine();
				out.write( "\t\tif ("+enumName+"CodeList == null) {");
				out.newLine();
				out.write( "\t\t\t"+enumName+"CodeList = new LinkedList<SelectItem>();");
				out.newLine();
				out.write( "\t\t\t"+enumName+"[] el = "+enumName+".values();");
				out.newLine();
				out.write( "\t\t\tfor ("+enumName+" obj : el) {");
				out.newLine();
				out.write( "\t\t\t\tString name = obj.getLabel();");
				out.newLine();
				out.write( "\t\t\t\tSelectItem item = new SelectItem(obj, name);");
				out.newLine();
				out.write( "\t\t\t\t"+enumName+"CodeList.add(item);");
				out.newLine();
				out.write( "\t\t\t}");
				out.newLine();
				out.write( "\t\t}");
				out.newLine();
				out.write( "\t\treturn "+enumName+"CodeList;");
				out.newLine();
				out.write( "\t}");
				out.newLine();
				out.newLine();
		    }
		}
		out.write( "}" );
		out.close();
	}
	
	private static String obtainTableDescription(String tableName) {
		
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URL[] codeUrls = Classpath.search(cl, CODE_TXT_FILE_URL, LEAME_FILE_NAME+".txt");
			URL url = codeUrls[0];
		
			final String HEADER = " TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN";
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String currentLine;
			while((currentLine = reader.readLine()) != null) {
				if(currentLine.contains(HEADER)){
					break;
				}
			}
			reader.readLine();
			while((currentLine = reader.readLine()) != null) {
				if( currentLine.contains(tableName) ){
					return currentLine;
				}
			}
		} catch (IOException e) {
			// nothing to do
		}
		return "No description found";
	}
	
}