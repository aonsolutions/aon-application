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
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file ;
		final String SUFFIX = ".txt";
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = Classpath.search(cl, "com/esferalia/aon/payroll/contrata/", SUFFIX);
		for(URL url: urls){
			if(getFileNameWithoutExtension(url).equals("TDPMUNIC")){
				file = new File("/AON-TRUNK/aon-payroll/src/main/resources/com/esferalia/aon/payroll/i18n/towns.properties");
				writeProperties(url, file);
				System.out.println( file.getAbsolutePath() );
				System.out.println("towns.properties generado");
			} else if(!getFileNameWithoutExtension(url).equals("LEAME") && !getFileNameWithoutExtension(url).equals("TAICLAOC1994") ){
				file = new File("/AON-TRUNK/aon-payroll/src/main/java/com/esferalia/aon/payroll/contrata/enumeration/"+getFileNameWithoutExtension(url)+".java");
				writeEnum(url, file);
				System.out.println( file.getAbsolutePath() );
				System.out.println("Enum "+getFileNameWithoutExtension(url)+" generado");
			}
		}
		System.out.println("Proceso finalizado!");
	}
	
	public static String getFileNameWithoutExtension(URL url) {
	    String path = url.getPath();

	    if (StringUtils.isBlank(path)) {
	        return null;
	    }
	    if (StringUtils.endsWith(path, "/")) {
	        //is a directory ..
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
	 * Write data to enum class.
	 * 
	 * @param url
	 * @param file
	 * @throws IOException
	 */
	public static void writeEnum( URL url, File file ) throws IOException {
		BufferedWriter out = new BufferedWriter( new FileWriter(file) );
		
		out.write( "package com.esferalia.aon.payroll.contrata.enumeration;" );
		out.newLine();
		out.newLine();
		
		out.write( "import com.code.aon.common.enumeration.IStringEnum;" );
		out.newLine();
		
		out.newLine();
		out.write( "/** " );
		out.newLine();
		out.write( "* Enumeration for represent Contrata (S.E.P.E.) "+getFileNameWithoutExtension(url)+" table codes." );
		out.newLine();
		out.write( "*/ " );
		out.newLine();
		
		out.write( "public enum " + getFileNameWithoutExtension(url) + " implements IStringEnum {");
		out.newLine();
		out.newLine();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String currentLine;
		while((currentLine = reader.readLine()) != null) {
			currentLine = currentLine.substring(0, currentLine.lastIndexOf("\""));
			StringTokenizer token = new StringTokenizer(currentLine, "\"");
			if(token.hasMoreTokens()){
				String code = token.nextToken();
				out.write( "\t"+getFileNameWithoutExtension(url)+"_"+code.replace("\"", "").toUpperCase()+"( \""+code+"\"" );
				if(token.hasMoreTokens()){
					token.nextToken();
					if(token.hasMoreTokens()){
						out.write(", \""+token.nextToken()+"\"");
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
		out.write( "}" );
		out.close();
	}
	
	/**
	 * Write data to properties file.
	 * 
	 * @param url
	 * @param file
	 * @throws IOException
	 */
	public static void writeProperties( URL url, File file ) throws IOException {
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
	
}