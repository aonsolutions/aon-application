package es.inteco.xbrl.pgc.tests;

import es.inteco.xbrl.pgc.viewer.IPGCViewer;
import es.inteco.xbrl.pgc.viewer.PGCViewer;
import junit.framework.TestCase;

public class LP4Test extends TestCase implements ILPUsoTest{
	
	private String id;
	private String inputPath;
	private String outputPath;
	private String module;
	
	public LP4Test() {
		super();
	}

	public LP4Test(String name) {
		super(name);
	}
	
	public String getName(){
		return id;
	}

	public void initialize(String name, String input, String output, String expected){
		id = name;
		inputPath = input;
		outputPath = output + ".";
		module = expected;
	}
	
	public void testLP4(){
		try{
			IPGCViewer viewer = PGCViewer.createInstance();
			viewer.view(inputPath, module, outputPath + module + "." + "html");
			assertTrue(true);
		}catch(Exception e){
			fail(e.toString());
		}
	}
}