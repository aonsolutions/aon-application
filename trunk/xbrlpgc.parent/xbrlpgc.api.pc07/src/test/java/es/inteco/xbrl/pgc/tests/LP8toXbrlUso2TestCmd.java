package es.inteco.xbrl.pgc.tests;

import es.inteco.xbrl.pgc.transform.IPGCTransformer;
import es.inteco.xbrl.pgc.transform.PGCTransformer;

public class LP8toXbrlUso2TestCmd implements Runnable {
	
	private String inputPath;
	
	public LP8toXbrlUso2TestCmd(String input){
		inputPath = input;
	}
	
	public void run(){
		try{
			IPGCTransformer transformer = PGCTransformer.createInstance();
			transformer.transformToXbrl(inputPath, true);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
