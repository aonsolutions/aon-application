package es.inteco.xbrl.pgc.tests;

import es.inteco.xbrl.pgc.validator.IXBRLValidator;
import es.inteco.xbrl.pgc.validator.ValidateResult;
import es.inteco.xbrl.pgc.validator.XBRLValidator;

public class LP8ValidateTestCmd implements Runnable{
	
	private String inputPath;
	
	public LP8ValidateTestCmd(String input){
		inputPath = input;
	}
	
	public void run(){
		try{
			IXBRLValidator validator = XBRLValidator.createInstance();
			ValidateResult validateResult = null;
			validateResult = validator.validate(inputPath);	
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
