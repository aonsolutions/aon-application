package es.inteco.xbrl.pgc.tests;

import java.io.File;
import junit.framework.TestCase;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLPGCException;
import es.inteco.xbrl.pgc.errors.exceptions.XBRLValidatorException;
import es.inteco.xbrl.pgc.errors.exceptions.XSDValidatorException;
import es.inteco.xbrl.pgc.validator.IXBRLValidator;
import es.inteco.xbrl.pgc.validator.ValidateResult;
import es.inteco.xbrl.pgc.validator.XBRLValidator;

public class LP3Test extends TestCase implements ILPUsoTest{
	
	private String id;
	private String inputPath;
	private File documentFileInput;
	private String expectedResult;
	private boolean error;
	
	public LP3Test() {
		super();
	}

	public LP3Test(String name) {
		super(name);
	}
	
	public String getName(){
		return id;
	}

	public void initialize(String name, String input, String output, String expected){
		id = name;
		inputPath = input;
		expectedResult = expected;
		error = false;
	}
	
	public void testLP3(){
		IXBRLValidator validator = XBRLValidator.createInstance();
		ValidateResult validateResult = null;
		//Prerequisito: el .xml debe exisistir
		try{
			documentFileInput = new File(inputPath);
			assertTrue(documentFileInput.exists());
		}catch(Throwable e){
			error = true;
			fail("El fichero XBRL no existe");
		}
		//Resultado: la transformación se ha realizado
		if (error != true){
			try{
				validateResult = validator.validate(inputPath);	
				if(validateResult.isValid() && expectedResult.equals("true")){
					assertTrue(true);
				}
				else if(!validateResult.isValid() && expectedResult.equals("false")){
					assertTrue(true);
				}
				else{
					fail(validateResult.getErrors());
				}
				//assertTrue(validateResult.toString(), expectedResult, validateResult.isValid()); 
			}catch(Throwable e){
				if(e instanceof XSDValidatorException){
					assertEquals(e.toString(), expectedResult, "XSDValidatorException");
				}
				else if(e instanceof XBRLValidatorException){
					assertEquals(e.toString(), expectedResult, "XBRLValidatorException");
				}
				else if(e instanceof XBRLPGCException){
					assertEquals(e.toString(), expectedResult, "XBRLPGCException");
				}
				else{
					fail(e.toString());
				}
			}
		}
	}
}
