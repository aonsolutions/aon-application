package es.inteco.xbrl.pgc.tests;

import es.inteco.xbrl.pgc.transform.IPGCTransformer;
import es.inteco.xbrl.pgc.transform.PGCTransformer;

public class LP8toXmlUso1TestCmd implements Runnable{
	
	private String inputPath;
	private String outputPath;
	
	public LP8toXmlUso1TestCmd(String input, String output){
		inputPath = input;
		outputPath = output;
	}
	
	public void run(){
		try{
			IPGCTransformer transformer = PGCTransformer.createInstance();
			transformer.transformToXml(inputPath, outputPath, false);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
