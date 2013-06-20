package es.inteco.xbrl.pgc.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import junit.framework.TestCase;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLPGCException;
import es.inteco.xbrl.pgc.transform.IPGCTransformer;
import es.inteco.xbrl.pgc.transform.PGCTransformer;
import es.inteco.xbrl.pgc.transform.format.Report;
import es.inteco.xbrl.pgc.transform.format.compare.ReportCompare;

public class LP2Uso2Test extends TestCase implements ILPUsoTest{
	
	private String id;
	private String inputPath;
	private String outputPath;
	private String expectedResult;
	private String expectedFile;
	private boolean error;
	
	public LP2Uso2Test() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LP2Uso2Test(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public String getName(){
		return id;
	}

	public void initialize(String name, String input, String output, String expected){
		id = name;
		inputPath = input;
		outputPath = output;
		expectedResult = expected;
		expectedFile = "Z:\\testCases\\" + name.substring(0, 3) + "\\" + name.substring(name.indexOf("-")+1) + "\\ExpectedFile.xml";
		error = false;
	}
	
	public void testLP2Uso2(){
		IPGCTransformer transformer = PGCTransformer.createInstance();
		//Prerequisito: el .xml debe exisistir
		try{
			File documentFileInput = new File(inputPath);
			assertTrue(documentFileInput.exists());
		}catch(Throwable e){
			error = true;
			fail("El fichero XML no existe");
		}
		//Resultado: la transformación se ha realizado
		if (error != true){
			try{		
				FileOutputStream fileOutput = null;
				fileOutput = new FileOutputStream(outputPath);
			    fileOutput.write(transformer.transformToXml(inputPath, false));
			    fileOutput.close();
			    ReportCompare comparator = new ReportCompare();
				Report report1 = Report.unmarshal(new FileReader(new File(outputPath)));
				Report report2 = new Report();
				try{
					File documentFileInput = new File(expectedFile);
					if(documentFileInput.exists()){
						report2 = Report.unmarshal(new FileReader(new File(expectedFile)));
					}
					if(!expectedResult.equals("")){
						fail("Se esperaba un error " + " " + expectedResult);
					}
					else{
						assertTrue("Error en la generación", comparator.equals(report1, report2)); 
					}
				}catch(Throwable e){
					fail("Fichero esperado no encontrado");
				}
			}catch(Throwable e){
				error = true;
				if(e instanceof XBRLPGCException){
					assertEquals(e.toString(), expectedResult, ((XBRLPGCException)e).getCodeError());
				}
				else{
					fail(e.toString());
				}
			}
		}
	}
}