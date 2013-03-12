package es.inteco.xbrl.pgc.tests;

import es.inteco.xbrl.pgc.transform.IPGCTransformer;
import es.inteco.xbrl.pgc.transform.PGCTransformer;

public class LP8toXmlUso2TestCmd implements Runnable{
	
	private String inputPath;
	
	public LP8toXmlUso2TestCmd(String input){
		inputPath = input;
	}
	
	public void run(){
		try{
			IPGCTransformer transformer = PGCTransformer.createInstance();
			transformer.transformToXml(inputPath, false);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
