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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.util.Classpath;

public class SSCodeTablesWriter {
	
	final static String PARENT_FOLDER_PATH 				= "/AON-TRUNK/aon.parent/aon-payroll/src/main/java/com/esferalia/aon/payroll/enumeration/";
	final static String ENUMERATIONS_FOLDER_PATH 		= "/AON-TRUNK/aon.parent/aon-payroll/src/main/java/com/esferalia/aon/payroll/enumeration/ss/";
	final static String COLLECTIONS_CLASS_PATH			= "/AON-TRUNK/aon.parent/aon-ui-payroll/src/main/java/com/esferalia/aon/ui/payroll/controller/";
	final static String COLLECTIONS_CLASS_NAME			= "SSCollectionsController";
	final static String COLLECTIONS_CLASS_PACKAGE_NAME	= "com.esferalia.aon.ui.payroll.controller";

	public final static String ENUMERATION_CLASS_PACKAGE_NAME = "com.esferalia.aon.payroll.enumeration.ss";
	
	final static String JAVA_FILE_EXTENSION = ".java";
	
	final static String TXT_CONTAINER_URL	= "com/esferalia/aon/payroll/ss/";
	
	final static String TABLES_ENUM_NAME 	= "SSCodeTables";
	
	
	/* 
	 * SOURCE: http://www.seg-social.es/prdi00/groups/public/documents/binario/50045.pdf
	 */
	final static String T01_TABLE_DESCRIPTION = "Indicador de prueba";
	final static String T05_TABLE_DESCRIPTION = "Calificados de liquidación";
	final static String T06_TABLE_DESCRIPTION = "Clase de liquidación";
	final static String T07_TABLE_DESCRIPTION = "Acción";
	final static String T10_TABLE_DESCRIPTION = "Clave de entidad de AT y EP";
	final static String T18_TABLE_DESCRIPTION = "Grupo de cotización";
	final static String T21_TABLE_DESCRIPTION = "Situación";
	final static String T37_TABLE_DESCRIPTION = "Condición de desempleado";
	final static String T41_TABLE_DESCRIPTION = "Tipos de inactividad";
	final static String T54_TABLE_DESCRIPTION = "Colectivo de peculiaridad de cotización";
	final static String T58_TABLE_DESCRIPTION = "Ocupación";
	final static String T61_TABLE_DESCRIPTION = "Colectivo de trabajador";
	final static String T68_TABLE_DESCRIPTION = "Indicativo pérdida de beneficios (trabajador)";
	final static String T83_TABLE_DESCRIPTION = "Exclusión social/Víctimas";
	final static String T84_TABLE_DESCRIPTION = "Concepto retributivo";
	
	
	private static int tablesCount;
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		
		URL[] codeTablesFiles = Classpath.search(cl, TXT_CONTAINER_URL, ".txt");
		if(codeTablesFiles.length==0){
			throw new IOException("¡¡¡¡¡¡¡¡¡ Ruta de tablas de codigos no encontrado !!!!!!");
		}
		
		int enumCount = 0;
		int propertiesCount = 0;
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		dateFormatter.applyPattern("dd/MM/yyyy HH:mm:ss");
		System.out.println("*******************************************************");
		System.out.println("*** S.S. - TABLAS DE CODIGOS DE LA SEGURIDAD SOCIAL ***");
		System.out.println("*******************************************************");
		System.out.println("*** Iniciando proceso. " + dateFormatter.format(new Date()));
		for(URL url: codeTablesFiles){

			File newFile = new File(ENUMERATIONS_FOLDER_PATH + getFileNameWithoutExtension(url) + JAVA_FILE_EXTENSION);
			System.out.print("Enum "+getFileNameWithoutExtension(url)+" en proceso ...");
			writeEnum(new File(url.getPath()), newFile);
			System.out.println(" generado!");
			enumCount++;
			tablesCount++;
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
	    
//	    if(fileNameWithOutExt.equals(LEAME_FILE_NAME)) {
//	    	return LEAME_FILE_ENUM_NAME;
//	    }
	    return fileNameWithOutExt;
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
			
			
			StringTokenizer token = null;
			
			if(file.getName().equals("T79.txt")){
				token = new StringTokenizer(currentLine, "	");
			} else {
				token = new StringTokenizer(currentLine, ";");
			}
			
			if(token.hasMoreTokens()){
				String code = token.nextToken();
				code = code.replace("~", "");
				code = code.replace("\"", "");
				code = code.trim();
				
				out.write( "\t"+getFileNameWithoutExtension(file)+"_"+code.toUpperCase()+"( \""+code+"\"" );
				if(token.hasMoreTokens()){
					String label = StringUtils.strip(token.nextToken());
					out.write(", \""+(label)+"\"");
					if(token.hasMoreTokens()){
						out.write(", \""+token.nextToken()+"\"");
						if(token.hasMoreTokens()){
							out.write(", \""+token.nextToken()+"\" ),");
						} else {
							out.write( ", null )," );
						}
					} else {
						out.write( ", null, null )," );
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
		out.write( "package " + ENUMERATION_CLASS_PACKAGE_NAME + ";" );
		out.newLine();
		out.newLine();
		
		out.write( "import java.text.ParseException;" );
		out.newLine();
		out.write( "import java.text.SimpleDateFormat;" );
		out.newLine();
		out.write( "import java.util.Calendar;" );
		out.newLine();
		out.write( "import java.util.Date;" );
		out.newLine();
		out.write( "import com.esferalia.aon.payroll.sepe.SSCodeTablesWriter.ISSEnum;" );
		out.newLine();
		out.write( "import org.apache.commons.lang.time.DateUtils;" );
		out.newLine();
		out.newLine();
		
		
		out.write( "/** " );
		out.newLine();
		out.write( " * Enumeration for represent SOCIAL SECURITY "+getFileNameWithoutExtension(file)+" table codes." );
		out.newLine();
		out.write( " * Generation main class: " + SSCodeTablesWriter.class.getCanonicalName() );
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
		out.write( "public enum " + getFileNameWithoutExtension(file) + " implements ISSEnum {");
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
		out.write("\t\t\t\treturn DateUtils.ceiling(sdf.parse(startDate), Calendar.DAY_OF_MONTH);");
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
		out.write("\t\t\t\treturn DateUtils.ceiling(sdf.parse(endDate), Calendar.DAY_OF_MONTH);");
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

		out.write("\tpublic boolean isActive(){");
		out.newLine();
		out.write("\t\tDate now = new Date();");
		out.newLine();
		out.write("\t\tnow = DateUtils.ceiling(now, Calendar.DAY_OF_MONTH);");
		out.newLine();
		out.write("\t\tif( (getStartDate()!=null && getStartDate().after(now)) || (getEndDate()!=null && getEndDate().before(now)) ){");
		out.newLine();
		out.write("\t\t\treturn false;");
		out.newLine();
		out.write("\t\t}");
		out.newLine();
		out.write("\t\treturn true;");
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
		out.write( " * Contrat@ code tables Collections." );
		out.newLine();
		out.write( " * Generation main class: " + SSCodeTablesWriter.class.getCanonicalName() );
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
				out.write( "\t\t\t\tif(obj.isActive()){");
				out.newLine();
				out.write( "\t\t\t\t\tString name = (obj.getDescription().length()>80?(obj.getDescription().substring(0, 80)+\"...\"):obj.getDescription());");
				out.newLine();
				out.write( "\t\t\t\t\tSelectItem item = new SelectItem(obj, name);");
				out.newLine();
				out.write( "\t\t\t\t\t"+enumName+"CodeList.add(item);");
				out.newLine();
				out.write( "\t\t\t\t}");
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
		return file.getName();
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
			out.write( "import com.esferalia.aon.payroll.sepe.SSCodeTablesWriter.ISSEnum;" );
			out.newLine();
			
			out.write( "/** " );
			out.newLine();
			out.write( " * Enumeration for represent SOCIAL SECURITY table codes." );
			out.newLine();
			out.write( " * Generation main class: " + SSCodeTablesWriter.class.getCanonicalName() );
			out.newLine();
			out.write( " */ " );
			out.newLine();
			out.write( "public enum " + getFileNameWithoutExtension(file) + " implements ISSEnum {");
			out.newLine();
			out.newLine();
			
			
			for (File enumFile : listOfFiles) {
			    if (enumFile.isFile() && enumFile.getName().endsWith(JAVA_FILE_EXTENSION)) {
			    	String enumName = enumFile.getName().replaceAll(JAVA_FILE_EXTENSION, "");
			    	String enumDescription = null;
			    	
			    	if( !enumName.equals(TABLES_ENUM_NAME) ){
			    		if(getFileNameWithoutExtension(enumFile).equals("T01")){
			    			enumDescription = T01_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("T05")){
			    			enumDescription = T05_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("T06")){
			    			enumDescription = T06_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("T07")){
			    			enumDescription = T07_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("T10")){
			    			enumDescription = T10_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("T18")){
			    			enumDescription = T18_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("T21")){
			    			enumDescription = T21_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("T37")){
			    			enumDescription = T37_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("T41")){
			    			enumDescription = T41_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("T54")){
			    			enumDescription = T54_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("T58")){
			    			enumDescription = T58_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("T61")){
			    			enumDescription = T61_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("T68")){
			    			enumDescription = T68_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("T83")){
			    			enumDescription = T83_TABLE_DESCRIPTION;
			    		} else if(getFileNameWithoutExtension(enumFile).equals("T84")){
			    			enumDescription = T84_TABLE_DESCRIPTION;
			    		}
			    		out.write( "\t"+"T_"+enumName+"( \""+enumName+"\", \"" + enumDescription + "\",null)," );
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
			
			out.write("\tpublic boolean isActive(){");
			out.newLine();
			out.write("\t\treturn true;");
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
	
	
	public interface ISSEnum {
		
		public String getCode();

		public String getDescription();

		public boolean isActive();
		
	}
}