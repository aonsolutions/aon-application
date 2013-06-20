/**
 * 
 */
package es.inteco.xbrl.pgc.tests;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import es.inteco.xbrl.pgc.transform.IPGCTransformer;
import es.inteco.xbrl.pgc.transform.PGCTransformer;
import es.inteco.xbrl.pgc.transform.format.Report;
import es.inteco.xbrl.pgc.transform.format.compare.ReportCompare;

/**
 * @author A141980
 *
 */
public class GenerateXmlFiles {
	
	public static String xbrlPath = "Z:\\testCases\\xbrl\\";
	public static String xmlPath = "Z:\\testCases\\xml\\";
	public static String xbrlTmpPath = "Z:\\testCases\\tmpXbrl\\";
	public static String xmlTmpPath = "Z:\\testCases\\tmpXml\\";
	public static String parejas = "Z:\\testCases\\parejas\\";
	
	/**
	 * Punto de entrada de ejecución
	 * @param args
	 */
	public static void main(String[] args) {
		
		IPGCTransformer transformer = PGCTransformer.createInstance();

		int i = 0;
		
		File xbrlDir = new File(xbrlPath);
		ArrayList<String> xbrlFiles = new ArrayList<String>();
				
		int numXbrlDirFiles = xbrlDir.list().length;
		
		for(i = 0; i < numXbrlDirFiles; i++){
			xbrlFiles.add(xbrlDir.list()[i]);
		}
		
		//Generamos los xml temporales
		toTmpXml(numXbrlDirFiles, xbrlFiles);
		/*for(i = 0; i < numXbrlDirFiles; i++){
			try{
				System.out.println("Conversión a XML del fichero " + xbrlPath + xbrlFiles.get(i));
				transformer.transformToXml(xbrlPath + xbrlFiles.get(i), 
						   xmlTmpPath + ((String)xbrlFiles.get(i)).substring(0, ((String)xbrlFiles.get(i)).indexOf(".xbrl")) + ".xml", 
						   true);
			}catch(Throwable e){
				e.printStackTrace();
			}	
		}*/
		
		File xmlTmpDir = new File(xmlTmpPath);
		ArrayList<String> xmlTmpFiles = new ArrayList<String>();
		
		int numXmlTmpDirFiles = xmlTmpDir.list().length;
		
		for(i = 0; i < numXmlTmpDirFiles; i++){
			xmlTmpFiles.add(xmlTmpDir.list()[i]);
		}
		
		//Generamos los xbrl temporales
		toTmpXbrl(numXmlTmpDirFiles, xmlTmpFiles);
		/*for(i = 0; i < numXmlTmpDirFiles; i++){		
			try{
				System.out.println("Conversión a XBRL del fichero " + xmlTmpPath + xmlTmpFiles.get(i));
				transformer.transformToXbrl(xmlTmpPath + xmlTmpFiles.get(i), 
						   xbrlTmpPath + ((String)xmlTmpFiles.get(i)).substring(0, ((String)xmlTmpFiles.get(i)).indexOf(".xml")) + ".xbrl");
			}catch(Throwable e){
				e.printStackTrace();
			}
		}*/
		
		File xbrlTmpDir = new File(xbrlTmpPath);
		ArrayList<String> xbrlTmpFiles = new ArrayList<String>();
		
		int numXbrlTmpDirFiles = xbrlTmpDir.list().length;
		
		for(i = 0; i < numXbrlTmpDirFiles; i++){
			xbrlTmpFiles.add(xbrlTmpDir.list()[i]);
		}
		
		//Generamos los xml finales
		toXml(numXbrlTmpDirFiles, xbrlTmpFiles);
		/*for(i = 0; i < numXbrlTmpDirFiles; i++){
			try{
				System.out.println("Conversión a XML del fichero " + xbrlTmpPath + xbrlTmpFiles.get(i));
				transformer.transformToXml(xbrlTmpPath + xbrlTmpFiles.get(i), 
						   xmlPath + ((String)xbrlTmpFiles.get(i)).substring(0, ((String)xbrlTmpFiles.get(i)).indexOf(".xbrl")) + ".xml",
						   true);	
			}catch(Throwable e){
				e.printStackTrace();
			}
		}*/
		
		File xmlDir = new File(xmlPath);
		ArrayList<String> xmlFiles = new ArrayList<String>();
		
		int numXmlDirFiles = xmlDir.list().length;
		
		for(i = 0; i < numXmlDirFiles; i++){
			xmlFiles.add(xmlDir.list()[i]);
		}
		
		compare(numXmlTmpDirFiles, xmlTmpFiles);
		/*for(i = 0; i < numXmlTmpDirFiles; i++){
			try{
				report1 = Report.unmarshal(new FileReader(new File(xmlTmpPath + xmlTmpFiles.get(i))));
				report2 = Report.unmarshal(new FileReader(new File(xmlPath + xmlTmpFiles.get(i))));
				if(comparator.equals(report1, report2)){
					inputFileXml = new File(xmlPath + xmlTmpFiles.get(i));
				    outputFileXml = new File(parejas + xmlTmpFiles.get(i));
				    inXml = new FileReader(inputFileXml);
				    outXml = new FileWriter(outputFileXml);
				    int c;
				    while ((c = inXml.read()) != -1)
				      outXml.write(c);
				    inXml.close();
				    outXml.close();
				    
				    inputFileXbrl = new File(xbrlPath + (xmlTmpFiles.get(i)).substring(0, (xmlTmpFiles.get(i)).indexOf(".xml")) + ".xbrl");
				    outputFileXbrl = new File(parejas + (xmlTmpFiles.get(i)).substring(0, (xmlTmpFiles.get(i)).indexOf(".xml")) + ".xbrl");
				    inXbrl = new FileReader(inputFileXbrl);
				    outXbrl = new FileWriter(outputFileXbrl);
				    while ((c = inXbrl.read()) != -1)
				      outXbrl.write(c);
				    inXbrl.close();
				    outXbrl.close();
				}
				else{
					System.out.println("Hubo un error en la generación del xml " + xmlTmpFiles.get(i));
					}
			}catch(Throwable e){
				System.out.println("Hubo un error en la generación del xml " + xmlTmpFiles.get(i));
			}
		}*/
	}
	
	public static void toTmpXml(int numXbrlDirFiles, ArrayList<String> xbrlFiles){
		int i = 0;
		IPGCTransformer transformer = PGCTransformer.createInstance();
		for(i = 0; i < numXbrlDirFiles; i++){
			transformer = PGCTransformer.createInstance();
			try{
				System.out.println("Conversión a XML del fichero " + xbrlPath + xbrlFiles.get(i));
				transformer.transformToXml(xbrlPath + xbrlFiles.get(i), 
						   xmlTmpPath + ((String)xbrlFiles.get(i)).substring(0, ((String)xbrlFiles.get(i)).indexOf(".xbrl")) + ".xml", 
						   true);
			}catch(Throwable e){
				e.printStackTrace();
			}	
		}
	}
	
	public static void toTmpXbrl(int numXmlTmpDirFiles, ArrayList<String> xmlTmpFiles){
		int i = 0;
		IPGCTransformer transformer = PGCTransformer.createInstance();
		for(i = 0; i < numXmlTmpDirFiles; i++){		
			transformer = PGCTransformer.createInstance();
			try{
				System.out.println("Conversión a XBRL del fichero " + xmlTmpPath + xmlTmpFiles.get(i));
				transformer.transformToXbrl(xmlTmpPath + xmlTmpFiles.get(i), 
						   xbrlTmpPath + ((String)xmlTmpFiles.get(i)).substring(0, ((String)xmlTmpFiles.get(i)).indexOf(".xml")) + ".xbrl");
			}catch(Throwable e){
				e.printStackTrace();
			}
		}
	}
	
	public static void toXml(int numXbrlTmpDirFiles, ArrayList<String> xbrlTmpFiles){
		int i = 0;
		IPGCTransformer transformer = PGCTransformer.createInstance();
		for(i = 0; i < numXbrlTmpDirFiles; i++){
			transformer = PGCTransformer.createInstance();
			try{
				System.out.println("Conversión a XML del fichero " + xbrlTmpPath + xbrlTmpFiles.get(i));
				transformer.transformToXml(xbrlTmpPath + xbrlTmpFiles.get(i), 
						   xmlPath + ((String)xbrlTmpFiles.get(i)).substring(0, ((String)xbrlTmpFiles.get(i)).indexOf(".xbrl")) + ".xml",
						   true);	
			}catch(Throwable e){
				e.printStackTrace();
			}
		}
	}
	
	public static void compare(int numXmlTmpDirFiles, ArrayList<String> xmlTmpFiles){
		int i = 0;
		ReportCompare comparator = new ReportCompare();
		Report report1 = new Report();
		Report report2 = new Report();
		File inputFileXml = null;
	    File outputFileXml = null;
	    FileReader inXml = null;
	    FileWriter outXml = null;
	    File inputFileXbrl = null;
	    File outputFileXbrl = null;
	    FileReader inXbrl = null;
	    FileWriter outXbrl = null;
		for(i = 0; i < numXmlTmpDirFiles; i++){
			try{
				report1 = Report.unmarshal(new FileReader(new File(xmlTmpPath + xmlTmpFiles.get(i))));
				report2 = Report.unmarshal(new FileReader(new File(xmlPath + xmlTmpFiles.get(i))));
				if(comparator.equals(report1, report2)){
					inputFileXml = new File(xmlPath + xmlTmpFiles.get(i));
				    outputFileXml = new File(parejas + xmlTmpFiles.get(i));
				    inXml = new FileReader(inputFileXml);
				    outXml = new FileWriter(outputFileXml);
				    int c;
				    while ((c = inXml.read()) != -1)
				      outXml.write(c);
				    inXml.close();
				    outXml.close();
				    
				    inputFileXbrl = new File(xbrlPath + (xmlTmpFiles.get(i)).substring(0, (xmlTmpFiles.get(i)).indexOf(".xml")) + ".xbrl");
				    outputFileXbrl = new File(parejas + (xmlTmpFiles.get(i)).substring(0, (xmlTmpFiles.get(i)).indexOf(".xml")) + ".xbrl");
				    inXbrl = new FileReader(inputFileXbrl);
				    outXbrl = new FileWriter(outputFileXbrl);
				    while ((c = inXbrl.read()) != -1)
				      outXbrl.write(c);
				    inXbrl.close();
				    outXbrl.close();
				}
				else{
					System.out.println("Hubo un error en la generación del xml " + xmlTmpFiles.get(i));
					}
			}catch(Throwable e){
				System.out.println("Hubo un error en la generación del xml " + xmlTmpFiles.get(i));
			}
		}
	}
}
