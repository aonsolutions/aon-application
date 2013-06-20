package es.inteco.xbrl.pgc.tests;

import es.inteco.xbrl.pgc.viewer.IPGCViewer;
import es.inteco.xbrl.pgc.viewer.PGCViewer;

public class LP8ViewerTestCmd implements Runnable{
	
	private String inputPath;
	private String outputPath;
	private String module;
	
	public LP8ViewerTestCmd(String input, String mod, String output){
		inputPath = input;
		outputPath = output;
		module = mod;
	}
	
	public void run(){
		try{
			IPGCViewer viewer = PGCViewer.createInstance();
			viewer.view(inputPath, module, outputPath);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
