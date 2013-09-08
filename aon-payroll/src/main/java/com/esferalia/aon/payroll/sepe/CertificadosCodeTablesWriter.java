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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.util.Classpath;

public class CertificadosCodeTablesWriter {
	
	final static String ENUMERATIONS_FOLDER_PATH 		= "/AON-TRUNK/aon.parent/aon-payroll/src/main/java/com/esferalia/aon/payroll/certificados/enumeration/";
	final static String COLLECTIONS_CLASS_PATH			= "/AON-TRUNK/aon.parent/aon-ui-sepe/src/main/java/com/esferalia/aon/ui/sepe/controller/";
	final static String COLLECTIONS_CLASS_NAME			= "CertificadosCollectionsController";
	final static String COLLECTIONS_CLASS_PACKAGE_NAME	= "com.esferalia.aon.ui.sepe.controller";
	
	public final static String ENUMERATION_CLASS_PACKAGE_NAME = "com.esferalia.aon.payroll.certificados.enumeration";

	final static String JAVA_FILE_EXTENSION = ".java";
	
	final static String ZIP_CONTAINER_URL			= "com/esferalia/aon/payroll/sepe/certificados/";
	final static String CODE_FILE_NAME				= "TABLAS_CODIGOS";
	final static String ERROR_CODE_FILE_NAME		= "TABLA_CODIGOS_RESPUESTA";
	
	final static String TABLES_ENUM_NAME = "CertificadosCodeTables";
	
	
	final static String DCODEDTC_TABLE_DESCRIPTION = "";
	final static String DCSPCPTC_TABLE_DESCRIPTION = "";
	final static String DGRCOTTC_TABLE_DESCRIPTION = "Grupo de cotización";
	final static String DSTEMCTC_TABLE_DESCRIPTION = "";
	final static String SACECOTC_TABLE_DESCRIPTION = "";
	final static String TAICLAOC_TABLE_DESCRIPTION = "Códigos de ocupación, profesiones";
	final static String TCGPROVI_TABLE_DESCRIPTION = "";
	final static String TCHRGCOT_TABLE_DESCRIPTION = "";
	final static String Terrores_TABLE_DESCRIPTION = "Códigos de errores";
	final static String TKCSITEM_TABLE_DESCRIPTION = "";
	final static String TKDIASAC_TABLE_DESCRIPTION = "";
	final static String TKEINDUC_TABLE_DESCRIPTION = "Indicador duración del contrato";
	final static String TKFCOEFI_TABLE_DESCRIPTION = "";
	final static String TKZCARPS_TABLE_DESCRIPTION = "Cargos públicos o sindicales";
	final static String TLDCAUSS_TABLE_DESCRIPTION = "Códigos de causas de suspensión o extinción";
	final static String TMJMINSS_TABLE_DESCRIPTION = "Códigos de la mineria del carbón";
	final static String TMPORCRD_TABLE_DESCRIPTION = "Causas de porcentaje de reducción de jornada";
	final static String TMQTDIST_TABLE_DESCRIPTION = "Distribución de jornadas (regular o irregular) para los contratos a tiempo parcial";
	final static String TNWTPCOM_TABLE_DESCRIPTION = "";

	
	private static int tablesCount;
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		
		String CODE_TXT_FILE_PATH = "/tmp/certificados/codeTables/";
		String ERROR_CODE_TXT_FILE_PATH = "/tmp/certificados/errorCodeTables/";
		
		URL[] codeTablesZip = Classpath.search(cl, ZIP_CONTAINER_URL, ".zip");
		if(codeTablesZip.length==0){
			throw new IOException("¡¡¡¡¡¡¡¡¡ Fichero de tablas de codigos no encontrado !!!!!!");
		}
		
//		tablesCount = codeTableUrls.length;
		int enumCount = 0;
		int propertiesCount = 0;
		
		System.out.println("*****************************************");
		System.out.println("*** SEPE - TABLAS DE CODIGOS DE CERTIFICADOS ");
		System.out.println("*****************************************");
		System.out.println("*** Iniciando proceso. " + new Date());
		for(URL url: codeTablesZip){
			if(getFileNameWithoutExtension(url).equals(CODE_FILE_NAME)){
				uncompressZipData(url.openStream(), new File(CODE_TXT_FILE_PATH));
				File codeDir = new File(CODE_TXT_FILE_PATH);
				File[] filesList = codeDir.listFiles();
				for(File file: filesList){
					File newFile = new File(ENUMERATIONS_FOLDER_PATH + getFileNameWithoutExtension(file) + JAVA_FILE_EXTENSION);
					System.out.print("Enum "+getFileNameWithoutExtension(file)+" en proceso ...");
					writeEnum(file, newFile);
					System.out.println(" generado!");
					enumCount++;
					tablesCount++;
				}
				FileUtils.deleteQuietly(codeDir);
			} else if(getFileNameWithoutExtension(url).equals(ERROR_CODE_FILE_NAME)){
				uncompressZipData(url.openStream(), new File(ERROR_CODE_TXT_FILE_PATH));
				File errorCodeDir = new File(ERROR_CODE_TXT_FILE_PATH);
				File[] errorFilesList = errorCodeDir.listFiles();
				for(File file: errorFilesList){
					File newFile = new File(ENUMERATIONS_FOLDER_PATH + getFileNameWithoutExtension(file) + JAVA_FILE_EXTENSION);
					System.out.print("Enum "+getFileNameWithoutExtension(file)+" en proceso ...");
					writeEnum(file, newFile);
					System.out.println(" generado!");
					enumCount++;
					tablesCount++;
				}
				FileUtils.deleteQuietly(errorCodeDir);
			}
		}
		
		File newFile = new File(ENUMERATIONS_FOLDER_PATH + TABLES_ENUM_NAME + JAVA_FILE_EXTENSION);
		System.out.print("Enum " + TABLES_ENUM_NAME + " en proceso ...");
		writeTablesEnum(newFile);
		System.out.println(" generado!");
		enumCount++;
		tablesCount++;
		
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
	    
	    return fileNameWithOutExt;
	}

	/**
	 * Write data to enum class.
	 * 
	 * @param url
	 * @param file
	 * @throws IOException
	 */
	private static void writeEnum( File file, File newfile ) throws IOException {
		
		BufferedWriter out = new BufferedWriter( new FileWriter(newfile) );
		
		writeEnumHeader(out, file, obtainTableDescription(getFileNameWithoutExtension(file)));
		
		if(getFileNameWithoutExtension(file).equals("Terrores")){
			writeErrorEnum( file, newfile, out );
		} else if( !getFileNameWithoutExtension(file).equals("TMJMINSS") && !getFileNameWithoutExtension(file).equals("TAICLAOC") ){
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String readerCurrentLine;
			while( (readerCurrentLine = reader.readLine()) != null ) {
				readerCurrentLine = StringUtils.strip(readerCurrentLine);
				
				// *********** 
				// This code is for error code file, the formatting of this file is especially a disaster
				List<String> splitedLines = new LinkedList<String>();
//				if(StringUtils.countMatches(readerCurrentLine, "DH")>1){
//					for(String line: StringUtils.split(readerCurrentLine, "DH")){
//						if(StringUtils.isNotBlank(line)){
//							splitedLines.add("DH"+line);
//						}
//					}
//				}
//				if(StringUtils.countMatches(readerCurrentLine, "DW")>1){
//					for(String line: StringUtils.split(readerCurrentLine, "DW")){
//						if(StringUtils.isNotBlank(line) && line.length()>1){
//							splitedLines.add("DW"+line);
//						}
//					}
//				}
				// ***********
				if(splitedLines.isEmpty()){
					splitedLines.add(readerCurrentLine);
				}
				
				for(String currentLine: splitedLines){
					StringTokenizer token = null;
					if(currentLine.contains(";") ){
						currentLine = currentLine.substring(0, currentLine.lastIndexOf("\""));
						token = new StringTokenizer(currentLine, "\"");
					} else if(currentLine.contains("\t") ){
						token = new StringTokenizer(currentLine, "\t");
					} else if(currentLine.contains(",") ){
						currentLine = currentLine.replace("\"","");
						token = new StringTokenizer(currentLine, ",");
					}
					if(token!=null && token.hasMoreTokens()){
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
			}
		}
		
		writeEnumLastContent(out, file, obtainTableDescription(getFileNameWithoutExtension(file)));
		
		out.close();
	
	}
	
	private static void writeErrorEnum( File file, File newfile, BufferedWriter out ) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String readerCurrentLine;
		while( (readerCurrentLine = reader.readLine()) != null ) {
			readerCurrentLine = StringUtils.strip(readerCurrentLine);
			
			// *********** 
			// This code is for error code file, the formatting of this file is especially a disaster
			List<String> splitedLines = new LinkedList<String>();
			if(StringUtils.countMatches(readerCurrentLine, "DH")>1){
				for(String line: StringUtils.split(readerCurrentLine, "DH")){
					if(StringUtils.isNotBlank(line)){
						splitedLines.add("DH"+line);
					}
				}
			}
			if(StringUtils.countMatches(readerCurrentLine, "DW")>1){
				for(String line: StringUtils.split(readerCurrentLine, "DW")){
					if(StringUtils.isNotBlank(line) && line.length()>1){
						splitedLines.add("DW"+line);
					}
				}
			}
			// ***********
			if(splitedLines.isEmpty()){
				splitedLines.add(readerCurrentLine);
			}
			
			for(String currentLine: splitedLines){
				
				if(StringUtils.containsIgnoreCase(currentLine, "Vigente") || StringUtils.containsIgnoreCase(currentLine, "OBSOLETO")) { 
					currentLine = StringUtils.replace(currentLine, ",\"Vigente", ";\"Vigente");
					currentLine = StringUtils.replace(currentLine, ",\"vigente", ";\"vigente");
					currentLine = StringUtils.replace(currentLine, ",\"OBSOLETO", ";\"OBSOLETO");
					currentLine = StringUtils.replace(currentLine, ",\"Obsoleto", ";\"Obsoleto");
					currentLine = StringUtils.replace(currentLine, ",\"obsoleto", ";\"obsoleto");
				} else if(StringUtils.isNotBlank(currentLine)){
					currentLine += ";\"\"";
				}
				
				
				StringTokenizer token = null;
				if(currentLine.contains(";") ){
					currentLine = currentLine.substring(0, currentLine.lastIndexOf("\""));
					token = new StringTokenizer(currentLine, "\"");
				} else if(currentLine.contains("\t") ){
					token = new StringTokenizer(currentLine, "\t");
				} else if(currentLine.contains(",") ){
					currentLine = currentLine.replace("\"","");
					token = new StringTokenizer(currentLine, ",");
				}
				if(token!=null && token.hasMoreTokens()){
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
		}
		
	}
	
	private static void writeEnumHeader( BufferedWriter out, File file, String enumDescription ) throws IOException {
		out.write( "package " + ENUMERATION_CLASS_PACKAGE_NAME + ";" );
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
		out.write( " * Enumeration for represent Certific@2 (S.E.P.E.) " + getFileNameWithoutExtension(file) + " table codes." );
		out.newLine();
		out.write( " * Generation main class: " + CertificadosCodeTablesWriter.class.getCanonicalName() );
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
	 * Write data to collections file.
	 * 
	 * @throws IOException
	 */
	private static void writeCollections( ) throws IOException {
		File file = new File(COLLECTIONS_CLASS_PATH + COLLECTIONS_CLASS_NAME + JAVA_FILE_EXTENSION);
		BufferedWriter out = new BufferedWriter( new FileWriter(file) );
		
		out.write( "package " + COLLECTIONS_CLASS_PACKAGE_NAME + ";" );
		out.newLine();
		out.newLine();

		out.write( "import java.util.LinkedList;" );
		out.newLine();
		out.write( "import java.util.List;" );
		out.newLine();
		out.write( "import javax.faces.model.SelectItem;" );
		out.newLine();
		out.write( "import " + ENUMERATION_CLASS_PACKAGE_NAME + ".*;" );
		out.newLine();
		out.newLine();

		out.write( "/** " );
		out.newLine();
		out.write( " * Autogenerated class" );
		out.newLine();
		out.write( " * " );
		out.newLine();
		out.write( " * Certific@2 code tables Collections." );
		out.newLine();
		out.write( " * Generation main class: " + CertificadosCodeTablesWriter.class.getCanonicalName() );
		out.newLine();
		out.write( "*/ " );
		out.newLine();
		out.newLine();
		
		out.write( "public class " + COLLECTIONS_CLASS_NAME + " {");
		out.newLine();
		out.newLine();
		
		File folder = new File(ENUMERATIONS_FOLDER_PATH);
		File[] listOfFiles = folder.listFiles();
		for (File enumFile : listOfFiles) {
		    if (enumFile.isFile() && enumFile.getName().endsWith(JAVA_FILE_EXTENSION)) {
		    	String enumName = enumFile.getName().replaceAll(JAVA_FILE_EXTENSION, "");
				
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
	
	private static String obtainTableDescription(String tableName) {
		return "No description found";
	}
	
	/**
	 * Write table codes to enum class.
	 * 
	 * @param url
	 * @param file
	 * @throws IOException
	 */
	private static void writeTablesEnum( File file ) throws IOException {
		
		try {
			File folder = new File(ENUMERATIONS_FOLDER_PATH);
			File[] listOfFiles = folder.listFiles();
			
			
			BufferedWriter out = new BufferedWriter( new FileWriter(file) );
			
			out.write( "package " + ENUMERATION_CLASS_PACKAGE_NAME + ";" );
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
			out.write( " * Enumeration for represent Certific@2 (S.E.P.E.) table codes." );
			out.newLine();
			out.write( " * Generation main class: " + CertificadosCodeTablesWriter.class.getCanonicalName() );
			out.newLine();
			out.write( " */ " );
			out.newLine();
			out.write( "public enum " + getFileNameWithoutExtension(file) + " {");
			out.newLine();
			out.newLine();
			
			
			for (File enumFile : listOfFiles) {
			    if (enumFile.isFile() && enumFile.getName().endsWith(JAVA_FILE_EXTENSION)) {
			    	String enumName = enumFile.getName().replaceAll(JAVA_FILE_EXTENSION, "");
			    	String enumDescription = null;
			    	
			    	if( !enumName.equals(TABLES_ENUM_NAME) ){
			    		if(getFileNameWithoutExtension(enumFile).equals("DCODEDTC")){
			    			enumDescription = DCODEDTC_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("DCSPCPTC")){
			    			enumDescription = DCSPCPTC_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("DGRCOTTC")){
			    			enumDescription = DGRCOTTC_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("DSTEMCTC")){
			    			enumDescription = DSTEMCTC_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("SACECOTC")){
			    			enumDescription = SACECOTC_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("TAICLAOC")){
			    			enumDescription = TAICLAOC_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("TCGPROVI")){
			    			enumDescription = TCGPROVI_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("TCHRGCOT")){
			    			enumDescription = TCHRGCOT_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("Terrores")){
			    			enumDescription = Terrores_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("TKCSITEM")){
			    			enumDescription = TKCSITEM_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("TKDIASAC")){
			    			enumDescription = TKDIASAC_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("TKEINDUC")){
			    			enumDescription = TKEINDUC_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("TKFCOEFI")){
			    			enumDescription = TKFCOEFI_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("TKZCARPS")){
			    			enumDescription = TKZCARPS_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("TLDCAUSS")){
			    			enumDescription = TLDCAUSS_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("TMJMINSS")){
			    			enumDescription = TMJMINSS_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("TMPORCRD")){
			    			enumDescription = TMPORCRD_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("TMQTDIST")){
			    			enumDescription = TMQTDIST_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("TNWTPCOM")){
			    			enumDescription = TNWTPCOM_TABLE_DESCRIPTION;
			    		}
			    		out.write( "\t"+"T_"+enumName+"( \""+enumName+"\", \"" + enumDescription + "\",null)," );

//			    		out.write( "\t"+"T_"+enumName+"( \""+enumName+"\", " );
//			    		out.write( "\t"+"T_"+enumName+"( \""+enumName+"\", \"\",null)," );
//			    		out.write( ",null)," );
			    		
			    		out.newLine();
			    	}
			    }
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