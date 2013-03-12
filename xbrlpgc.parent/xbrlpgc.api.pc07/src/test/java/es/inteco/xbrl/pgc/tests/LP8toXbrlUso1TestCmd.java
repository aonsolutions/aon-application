package es.inteco.xbrl.pgc.tests;

import es.inteco.xbrl.pgc.transform.IPGCTransformer;
import es.inteco.xbrl.pgc.transform.PGCTransformer;

public class LP8toXbrlUso1TestCmd implements Runnable {
	
	private String inputPath;
	private String outputPath;
	
	public LP8toXbrlUso1TestCmd(String input, String output){
		inputPath = input;
		outputPath = output;
	}
	
	public void run(){
		try{
			IPGCTransformer transformer = PGCTransformer.createInstance();
			transformer.transformToXbrl(inputPath, outputPath);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
