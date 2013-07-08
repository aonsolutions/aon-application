package com.esferalia.aon.payroll.sepe;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.util.Classpath;

public class ContrataCodeTablesWriter {
	
	final static String ENUMERATIONS_FOLDER_PATHNAME 		= "/AON-TRUNK/aon.parent/aon-payroll/src/main/java/com/esferalia/aon/payroll/contrata/enumeration/";
	final static String TOWNS_PROPERTIES_PATHNAME 			= "/AON-TRUNK/aon.parent/aon-payroll/src/main/resources/com/esferalia/aon/payroll/i18n/towns.properties";
	final static String QUALIFICATIONS_PROPERTIES_PATHNAME 	= "/AON-TRUNK/aon.parent/aon-payroll/src/main/resources/com/esferalia/aon/payroll/i18n/qualifications.properties";
	final static String ZIP_PROPERTIES_PATHNAME 			= "/AON-TRUNK/aon.parent/aon-payroll/src/main/resources/com/esferalia/aon/payroll/i18n/zip.properties";
	final static String COLLECTIONS_CLASS_PATHNAME 			= "/AON-TRUNK/aon.parent/aon-ui-payroll/src/main/java/com/esferalia/aon/ui/payroll/controller/ContrataCollectionsController.java";
	
	final static String ZIP_CONTAINER_URL			= "com/esferalia/aon/payroll/sepe/contrata/";
	final static String CODE_FILE_NAME				= "TABLASXML50";
	final static String ERROR_CODE_FILE_NAME		= "TRespuestaXML50";
	
	final static String TOWNS_FILE_NAME 			= "TDPMUNIC";
	final static String LEAME_FILE_NAME 			= "LEAME";
	final static String LEAME_FILE_ENUM_NAME 		= "ContrataCodeTables";
	final static String LEAME_RESPUESTA_FILE_NAME 	= "LEAME_RESPUESTA";
	final static String CNO_1994_FILE_NAME 			= "TAICLAOC1994";
	final static String ZIP_FILE_NAME 				= "TAPCOPOS";
	final static String QUALIFICATIONS_FILE_NAME	= "THITIACA";
	
	private static int tablesCount;
	private static String CODE_TXT_FILE_URL = "/tmp/contrata/codeTables/";
	private static String ERROR_CODE_TXT_FILE_URL = "/tmp/contrata/errorCodeTables/";
	
	private static String leamePath;
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		
		URL[] codeTablesZip = Classpath.search(cl, ZIP_CONTAINER_URL, ".zip");
		if(codeTablesZip.length==0){
			throw new IOException("¡¡¡¡¡¡¡¡¡ Fichero de tablas de codigos no encontrado !!!!!!");
		}
		
		int enumCount = 0;
		int propertiesCount = 0;
		
		for(URL url: codeTablesZip){
			if(getFileNameWithoutExtension(url).equals(CODE_FILE_NAME)){
				uncompressZipData(url.openStream(), new File(CODE_TXT_FILE_URL));
				File codeDir = new File(CODE_TXT_FILE_URL);
				
				leamePath = codeDir.getAbsolutePath() + "\\" + LEAME_FILE_NAME + ".txt";
				
				File[] filesList = codeDir.listFiles();
				tablesCount = filesList.length;
				
				
				for(File file: filesList){
					if(getFileNameWithoutExtension(file).equals(TOWNS_FILE_NAME)){
						File newFile = new File(TOWNS_PROPERTIES_PATHNAME);
						System.out.print("#### towns.properties en proceso ...");
						writeProperties(file, newFile);
						System.out.println(" generado!");
						propertiesCount++;
						tablesCount++;
					} else if(getFileNameWithoutExtension(file).equals(ZIP_FILE_NAME) ){
						File newFile = new File(ZIP_PROPERTIES_PATHNAME);
						System.out.print("#### zip.properties en proceso ...");
						writeProperties(file, newFile);
						System.out.println(" generado!");
						propertiesCount++;
						tablesCount++;
					} else if(getFileNameWithoutExtension(file).equals(QUALIFICATIONS_FILE_NAME) ){
//						file = new File(ENUMERATIONS_FOLDER_PATHNAME+getFileNameWithoutExtension(url)+".java");
//						System.out.print("Enum "+getFileNameWithoutExtension(url)+" en proceso ...");
//						writeQualificationsEnum(url, file);
						File newFile = new File(QUALIFICATIONS_PROPERTIES_PATHNAME);
						System.out.print("#### qualifications.properties en proceso ...");
						writeQualificationsProperties(file, newFile);
						System.out.println(" generado!");
						propertiesCount++;
						tablesCount++;
					} else if( getFileNameWithoutExtension(file).equals(LEAME_FILE_ENUM_NAME) ){
						File newFile = new File(ENUMERATIONS_FOLDER_PATHNAME+getFileNameWithoutExtension(file)+".java");
						System.out.print("Enum "+getFileNameWithoutExtension(file)+" en proceso ...");
						writeTablesEnum(file, newFile);
						System.out.println(" generado!");
						enumCount++;
						tablesCount++;
					} else if(!getFileNameWithoutExtension(file).equals(LEAME_FILE_ENUM_NAME)
							&& !getFileNameWithoutExtension(file).equals(CNO_1994_FILE_NAME) 
							&& !getFileNameWithoutExtension(file).equals(ZIP_FILE_NAME) 
							&& !getFileNameWithoutExtension(file).equals(QUALIFICATIONS_FILE_NAME)
							){
						File newFile = new File(ENUMERATIONS_FOLDER_PATHNAME+getFileNameWithoutExtension(file)+".java");
						System.out.print("Enum "+getFileNameWithoutExtension(file)+" en proceso ...");
						writeEnum(file, newFile);
						System.out.println(" generado!");
						enumCount++;
						tablesCount++;
					}
				}
			} else if(getFileNameWithoutExtension(url).equals(ERROR_CODE_FILE_NAME)){
				uncompressZipData(url.openStream(), new File(ERROR_CODE_TXT_FILE_URL));
				File errorCodeDir = new File(ERROR_CODE_TXT_FILE_URL);
				File[] errorFilesList = errorCodeDir.listFiles();
				tablesCount += errorFilesList.length;
				for(File file: errorFilesList){
					if(!getFileNameWithoutExtension(file).equals(LEAME_RESPUESTA_FILE_NAME)){
						File newFile = new File(ENUMERATIONS_FOLDER_PATHNAME+getFileNameWithoutExtension(file)+".java");
						System.out.print("Enum "+getFileNameWithoutExtension(file)+" en proceso ...");
						writeEnum(file, newFile);
						System.out.println(" generado!");
						enumCount++;
					}
				}
			}
		}
		System.out.println("*** Enumeraciones generadas. ("+enumCount+")");
		System.out.println("*** Ficheros de propiedades generados. ("+propertiesCount+")");
		writeCollections();
		System.out.println("*** Colecciones generadas.");
		System.out.println("Proceso finalizado !!!!!!!");
	}
	
	public static void uncompressZipData(InputStream in, File destinationFolder) throws IOException {
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(in));
		ZipEntry entry = zis.getNextEntry();
		byte[] buffer = new byte[1024];
		while ( entry != null) {
			String filename = entry.getName();
			File newfile = new File(destinationFolder, filename);
			if (entry.isDirectory()) {
				newfile.mkdirs();
			} else {
				if (!newfile.getParentFile().exists()) {
					newfile.getParentFile().mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(newfile);
				int len;
	            while ((len = zis.read(buffer)) > 0) {
	            	fos.write(buffer, 0, len);
	            }
	            fos.close();   
			}
			entry = zis.getNextEntry();
		}
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
		return getFileNameWithoutExtension(new File(url.getPath()));
	}
	
	private static String getFileNameWithoutExtension(File file) {
	    if (file==null || !file.canRead()) {
	        return null;
	    }
	    if (file.isDirectory()) {
	        return null;
	    }

	    String fileNameWithExt = file.getName();

	    int sepPosition = fileNameWithExt.lastIndexOf(".");
	    String fileNameWithOutExt = null;
	    if (sepPosition >= 0) {
	        fileNameWithOutExt = fileNameWithExt.substring(0,sepPosition);
	    } else {
	        fileNameWithOutExt = fileNameWithExt;
	    }
	    
	    if(fileNameWithOutExt.equals(LEAME_FILE_NAME)) {
	    	return LEAME_FILE_ENUM_NAME;
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
		
		writeEnumHeader(out, file, obtainTableDescription(file));
		
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
		
		writeEnumLastContent(out, file, obtainTableDescription(file));
		
		out.close();
	}
		
	/**
	 * Write data to enum class.
	 * 
	 * @param url
	 * @param file
	 * @throws IOException
	 */
	private static void writeEnum( File file, File newFile ) throws IOException {
		BufferedWriter out = new BufferedWriter( new FileWriter(newFile) );
		
		writeEnumHeader(out, file, obtainTableDescription(file));
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String currentLine;
		while((currentLine = reader.readLine()) != null) {
			currentLine = StringUtils.strip(currentLine);
			currentLine = currentLine.substring(0, currentLine.lastIndexOf("\""));
			StringTokenizer token = new StringTokenizer(currentLine, "\"");
			if(token.hasMoreTokens()){
				String code = token.nextToken();
				out.write( "\t"+getFileNameWithoutExtension(file)+"_"+code.replace("\"", "").toUpperCase()+"( \""+code+"\"" );
				if(token.hasMoreTokens()){
					token.nextToken();
					if(token.hasMoreTokens()){
						String label = StringUtils.strip(token.nextToken());
						out.write(", \""+(label)+"\"");
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
		
		writeEnumLastContent(out, file, obtainTableDescription(file));
		
		out.close();
	}
	
	private static void writeEnumHeader( BufferedWriter out, File file, String enumDescription ) throws IOException {
		out.write( "package com.esferalia.aon.payroll.contrata.enumeration;" );
		out.newLine();
		out.newLine();
		
		out.write( "import java.text.ParseException;" );
		out.newLine();
		out.write( "import java.text.SimpleDateFormat;" );
		out.newLine();
		out.write( "import java.util.Date;" );
		out.newLine();
		out.newLine();
		
		
		out.write( "/** " );
		out.newLine();
		out.write( " * Enumeration for represent Contrata (S.E.P.E.) "+getFileNameWithoutExtension(file)+" table codes." );
		out.newLine();
		out.write( " * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter." );
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
		out.write( "public enum " + getFileNameWithoutExtension(file) + " {");
		out.newLine();
		out.newLine();
	}
	private static void writeEnumLastContent( BufferedWriter out, File file, String enumDescription ) throws IOException {
		
		out.write( "\t;" );
		out.newLine();
		out.write( "\tpublic static final String TABLE_NAME = \"" + getFileNameWithoutExtension(file) + "\";" );
		out.newLine();
		out.write( "\tpublic static final String TABLE_DESCRIPTION = \"" + enumDescription + "\";" );
		out.newLine();
		out.write( "\tprivate final SimpleDateFormat sdf = new SimpleDateFormat(\"yyyyMMdd\");" );
		out.newLine();
		out.write( "\tprivate String code;" );
		out.newLine();
		out.write( "\tprivate String description;" );
		out.newLine();
		out.write( "\tprivate String startDate;" );
		out.newLine();
		out.write( "\tprivate String endDate;" );
		out.newLine();
		out.newLine();
		
		out.write( "\t"+getFileNameWithoutExtension(file)+"( String code, String description, String startDate, String endDate ) {" );
		out.newLine();
		out.write( "\t\tthis.code = code;" );
		out.newLine();
		out.write( "\t\tthis.description = description;" );
		out.newLine();
		out.write( "\t\tthis.startDate = startDate;" );
		out.newLine();
		out.write( "\t\tthis.endDate = endDate;" );
		out.newLine();
		out.write( "\t}" );
		out.newLine();
		out.newLine();
		
		out.write("\tpublic String getCode() {");
		out.newLine();
		out.write("\t\treturn code;");
		out.newLine();
		out.write("\t}");
		out.newLine();
		out.newLine();
		
		out.write("\tpublic String getDescription() {");
		out.newLine();
		out.write("\t\treturn description;");
		out.newLine();
		out.write("\t}");
		out.newLine();
		out.newLine();
		
		out.write("\tpublic Date getStartDate(){");
		out.newLine();
		out.write("\t\ttry {");
		out.newLine();
		out.write("\t\t\tif(startDate!=null){");
		out.newLine();
		out.write("\t\t\t\treturn sdf.parse(startDate);");
		out.newLine();
		out.write("\t\t\t}");
		out.newLine();
		out.write("\t\t} catch (ParseException e) {");
		out.newLine();
		out.write("\t\t\t// nothing to do");
		out.newLine();
		out.write("\t\t}");
		out.newLine();
		out.write("\t\treturn null;");
		out.newLine();
		out.write("\t}");
		out.newLine();
		out.newLine();
		
		out.write("\tpublic Date getEndDate(){");
		out.newLine();
		out.write("\t\ttry {");
		out.newLine();
		out.write("\t\t\tif(endDate!=null){");
		out.newLine();
		out.write("\t\t\t\treturn sdf.parse(endDate);");
		out.newLine();
		out.write("\t\t\t}");
		out.newLine();
		out.write("\t\t} catch (ParseException e) {");
		out.newLine();
		out.write("\t\t\t// nothing to do");
		out.newLine();
		out.write("\t\t}");
		out.newLine();
		out.write("\treturn null;");
		out.newLine();
		out.write("\t}");
		out.newLine();
		out.newLine();
		
		out.write("\tpublic static "+getFileNameWithoutExtension(file)+" getEnumByValue(String expression) {");
		out.newLine();
		out.write("\t\tfor( "+getFileNameWithoutExtension(file)+" o : "+getFileNameWithoutExtension(file)+".values() ) {");
		out.newLine();
		out.write("\t\t\tif ( o.getCode().equals(expression) ) {");
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
	private static void writeProperties( File file, File newFile ) throws IOException {
		BufferedWriter out = new BufferedWriter( new FileWriter(newFile) );
		
		out.write( " ################################### " );
		out.newLine();
		out.write( " # Values for represent Contrata (S.E.P.E.) "+getFileNameWithoutExtension(file)+" table codes." );
		out.newLine();
		out.write( " # Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter." );
		out.newLine();
		out.write( " #  ------------------------------------------------------------------------" );
		out.newLine();
		out.write( " #  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN." );
		out.newLine();
		out.write( " # " + obtainTableDescription(file));
		out.newLine();
		out.write( " #  ------------------------------------------------------------------------" );
		out.newLine();
		out.write( " ################################### " );
		out.newLine();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String currentLine;
		while((currentLine = reader.readLine()) != null) {
			StringTokenizer token = new StringTokenizer(currentLine, ";");
			String code = token.nextToken();
			String label = token.hasMoreTokens()?token.nextToken():code;
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
	private static void writeQualificationsProperties( File file, File newfile ) throws IOException {
		BufferedWriter out = new BufferedWriter( new FileWriter(newfile) );
		
		out.write( " ################################### " );
		out.newLine();
		out.write( " # Values for represent Contrata (S.E.P.E.) "+getFileNameWithoutExtension(file)+" table codes." );
		out.newLine();
		out.write( " # Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter." );
		out.newLine();
		out.write( " #  ------------------------------------------------------------------------" );
		out.newLine();
		out.write( " #  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN." );
		out.newLine();
		out.write( " # " + obtainTableDescription(file) );
		out.newLine();
		out.write( " #  ------------------------------------------------------------------------" );
		out.newLine();
		out.write( " ################################### " );
		out.newLine();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
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
		out.write( " * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter." );
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
				out.write( "\t * " + obtainTableDescription(enumFile) );
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
				out.write( "\t\t\t\tString name = (obj.getDescription().length()>80?(obj.getDescription().substring(0, 80)+\"...\"):obj.getDescription());");
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
	
	private static String obtainTableDescription(File file) {
		try {
//			ClassLoader cl = Thread.currentThread().getContextClassLoader();
//			URL[] codeUrls = Classpath.search(cl, CODE_TXT_FILE_URL, LEAME_FILE_NAME+".txt");
//			URL url = codeUrls[0];
		
			final String HEADER = " TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN";
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(leamePath)));
			String currentLine;
			while((currentLine = reader.readLine()) != null) {
				if(currentLine.contains(HEADER)){
					break;
				}
			}
			reader.readLine();
			while((currentLine = reader.readLine()) != null) {
				if( currentLine.contains(getFileNameWithoutExtension(file)) ){
					return currentLine;
				}
			}
		} catch (IOException e) {
			// nothing to do
			System.out.print("");
		}
		return "No description found";
	}
	
	/**
	 * Write table codes to enum class.
	 * 
	 * @param url
	 * @param file
	 * @throws IOException
	 */
	private static void writeTablesEnum( File file, File newFile ) throws IOException {
		try {
//			ClassLoader cl = Thread.currentThread().getContextClassLoader();
//			URL[] codeUrls = Classpath.search(cl, CODE_TXT_FILE_URL, LEAME_FILE_NAME+".txt");
//			URL url = codeUrls[0];
		
			final String HEADER = " TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN";
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String currentLine;
			while((currentLine = reader.readLine()) != null) {
				if(currentLine.contains(HEADER)){
					break;
				}
			}
			reader.readLine();
			
			
			BufferedWriter out = new BufferedWriter( new FileWriter(newFile) );
			
			out.write( "package com.esferalia.aon.payroll.contrata.enumeration;" );
			out.newLine();
			out.newLine();
			
			out.write( "import java.text.ParseException;" );
			out.newLine();
			out.write( "import java.text.SimpleDateFormat;" );
			out.newLine();
			out.write( "import java.util.Date;" );
			out.newLine();
			out.newLine();
			
			out.write( "/** " );
			out.newLine();
			out.write( " * Enumeration for represent Contrata (S.E.P.E.) table codes." );
			out.newLine();
			out.write( " * Generation main class: com.esferalia.aon.payroll.sepe.ContrataCodeTablesWriter." );
			out.newLine();
			out.write( " */ " );
			out.newLine();
			out.write( "public enum " + getFileNameWithoutExtension(file) + " {");
			out.newLine();
			out.newLine();
			
			int i = 0;
			while((currentLine = reader.readLine()) != null && i < tablesCount) {
				i++;
				currentLine = StringUtils.strip(currentLine);
				
				String lastUpdateDate = "";
				if(currentLine.length()>10){
					lastUpdateDate = currentLine.substring(currentLine.length()-10, currentLine.length());
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
					sdf.setLenient(false);
					try {
						sdf.parse(lastUpdateDate);
						currentLine = currentLine.substring(0, currentLine.length()-10);
					} catch (ParseException e) {
						lastUpdateDate = "";
					}
				}
				
				
				if(currentLine.contains("TAICLAOC1994") || currentLine.contains("TAICLAOC2011")){
					String code = currentLine.substring(0, 12);
					out.write( "\t"+"T_"+code.replace("*", "").toUpperCase()+"( \""+code+"\"" );
					
					String label = StringUtils.strip(currentLine.substring(12, currentLine.length()));
					
					
					out.write(", \""+(label)+"\"");						
					out.write(", \""+lastUpdateDate+"\" ),");
					out.newLine();
					continue;
				} 
				
				StringTokenizer token = new StringTokenizer(currentLine, "\t");
				if(token.hasMoreTokens()){
					String code = token.nextToken();
					out.write( "\t"+"T_"+code.replace("*", "").toUpperCase()+"( \""+code.trim()+"\"" );
					if(token.hasMoreTokens()){
						String label = StringUtils.strip(token.nextToken());
						out.write(", \""+(label)+"\"");
						}
						out.write(", \""+lastUpdateDate+"\" ),");
				}
				out.newLine();
			}
			out.write( "\t;" );
			out.newLine();
			out.write( "\tprivate final SimpleDateFormat sdf = new SimpleDateFormat(\"dd-MM-yyyy\");" );
			out.newLine();
			out.write( "\tprivate String code;" );
			out.newLine();
			out.write( "\tprivate String description;" );
			out.newLine();
			out.write( "\tprivate String lastUpdateDate;" );
			out.newLine();
			out.newLine();
			
			out.write( "\t"+getFileNameWithoutExtension(file)+"( String code, String description, String lastUpdateDate) {" );
			out.newLine();
			out.write( "\t\tthis.code = code;" );
			out.newLine();
			out.write( "\t\tthis.description = description;" );
			out.newLine();
			out.write( "\t\tthis.lastUpdateDate = lastUpdateDate;" );
			out.newLine();
			out.write( "\t}" );
			out.newLine();
			out.newLine();
			
			out.write("\tpublic String getCode() {");
			out.newLine();
			out.write("\t\treturn code;");
			out.newLine();
			out.write("\t}");
			out.newLine();
			out.newLine();
			
			out.write("\tpublic String getDescription() {");
			out.newLine();
			out.write("\t\treturn description;");
			out.newLine();
			out.write("\t}");
			out.newLine();
			out.newLine();
			
			out.write("\tpublic Date getLastUpdateDate(){");
			out.newLine();
			out.write("\t\ttry {");
			out.newLine();
			out.write("\t\t\tif(lastUpdateDate!=null){");
			out.newLine();
			out.write("\t\t\t\treturn sdf.parse(lastUpdateDate);");
			out.newLine();
			out.write("\t\t\t}");
			out.newLine();
			out.write("\t\t} catch (ParseException e) {");
			out.newLine();
			out.write("\t\t\t// nothing to do");
			out.newLine();
			out.write("\t\t}");
			out.newLine();
			out.write("\treturn null;");
			out.newLine();
			out.write("\t}");
			out.newLine();
			out.newLine();
			
			out.write("\tpublic static "+getFileNameWithoutExtension(file)+" getEnumByValue(String expression) {");
			out.newLine();
			out.write("\t\tfor( "+getFileNameWithoutExtension(file)+" o : "+getFileNameWithoutExtension(file)+".values() ) {");
			out.newLine();
			out.write("\t\t\tif ( o.getCode().equals(expression) ) {");
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
			
			out.close();
		} catch (IOException e) {
			// nothing to do
		}
	}
	
}