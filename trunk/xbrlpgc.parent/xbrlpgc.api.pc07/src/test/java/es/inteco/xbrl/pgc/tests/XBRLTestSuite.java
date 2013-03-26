package es.inteco.xbrl.pgc.tests;

import java.io.File;
import java.io.FileReader;
import junit.framework.Test;
import junit.framework.TestSuite;

public class XBRLTestSuite extends TestSuite{
	
	public static Test suite(){
		TestSuite suite = new TestSuite("Test for API XBRL");	
		String inputPath;
		String outputPath;
		String expectedResult;
		String name;
		int i=0;
		try{
			PGCTestSuite collection = PGCTestSuite.unmarshal(new FileReader(new File("./testConfig/LP8.xml")));
    		for(i = 0; i < collection.getPGCTestCount(); i++){
    			PGCTest currentTest = collection.getPGCTest(i);
    			ILPUsoTest newTest = createTest(currentTest.getId());
    			name = currentTest.getId();
    			inputPath = "Z:\\testCases\\" + currentTest.getId().substring(0, 3) + "\\" + currentTest.getId().substring(currentTest.getId().indexOf("-")+1) + "\\" + currentTest.getInputPath();
    			outputPath = "Z:\\testCases\\" + currentTest.getId().substring(0, 3) + "\\" + currentTest.getId().substring(currentTest.getId().indexOf("-")+1) + "\\" + currentTest.getOutputPath();
    			expectedResult = currentTest.getExpectedResult();
    			newTest.initialize(name, inputPath, outputPath, expectedResult);
    			suite.addTest(newTest);
    		}
    	}catch(Exception t){
    		t.printStackTrace();
    	}
		return suite;
    }
	
	private static ILPUsoTest createTest(String id){
		if (id.substring(0, id.indexOf("-")).equals("LP1Uso1")){
			return new LP1Uso1Test("testLP1Uso1");
		}
		else if (id.substring(0, id.indexOf("-")).equals("LP1Uso2")){
			return new LP1Uso2Test("testLP1Uso2");
		}
		else if (id.substring(0, id.indexOf("-")).equals("LP2Uso1")){
			return new LP2Uso1Test("testLP2Uso1");
		}
		else if (id.substring(0, id.indexOf("-")).equals("LP2Uso2")){
			return new LP2Uso2Test("testLP2Uso2");
		}
		else if (id.substring(0, id.indexOf("-")).equals("LP3")){
			return new LP3Test("testLP3");
		}
		else if (id.substring(0, id.indexOf("-")).equals("LP4")){
			return new LP4Test("testLP4");
		}
		else if (id.substring(0, id.indexOf("-")).equals("LP8toXbrlUso1")){
			return new LP8toXbrlUso1Test("testLP8");
		}
		else if (id.substring(0, id.indexOf("-")).equals("LP8toXbrlUso2")){
			return new LP8toXbrlUso2Test("testLP8");
		}
		else if (id.substring(0, id.indexOf("-")).equals("LP8toXmlUso1")){
			return new LP8toXmlUso1Test("testLP8");
		}
		else if (id.substring(0, id.indexOf("-")).equals("LP8toXmlUso2")){
			return new LP8toXmlUso2Test("testLP8");
		}
		else if (id.substring(0, id.indexOf("-")).equals("LP8Validate")){
			return new LP8ValidateTest("testLP8");
		}
		else if (id.substring(0, id.indexOf("-")).equals("LP8Viewer")){
			return new LP8ViewerTest("testLP8");
		}
		return null;
	}
}
