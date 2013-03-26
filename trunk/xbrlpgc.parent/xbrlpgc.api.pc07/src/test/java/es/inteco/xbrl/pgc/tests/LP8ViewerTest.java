package es.inteco.xbrl.pgc.tests;

import java.util.ArrayList;
import junit.framework.TestCase;

public class LP8ViewerTest extends TestCase implements ILPUsoTest{
	
	private String id;
	private String inputPath;
	private String outputPath;
	private int repeat;
	private String module;
	
	public LP8ViewerTest() {
		super();
	}
	
	public LP8ViewerTest(String name) {
		super(name);
	}
	
	public String getName(){
		return id;
	}
	
	public void initialize(String name, String input, String output, String expected){
		id = name;
		inputPath = input;
		outputPath = output + ".";
		module = expected.substring(0, expected.indexOf("x"));
		repeat = Integer.parseInt((expected.substring(expected.indexOf("x")+1)).trim());
	}
	
	public void testLP8(){
		try{
			int i = 0;
			LP8ViewerTestCmd command = null;
			ArrayList<Thread> at = new ArrayList<Thread>();
			Thread t = null;
			for(i = 0; i < repeat; i++){
				command = new LP8ViewerTestCmd(inputPath, module, outputPath + module + "." + i + ".html");
				t = new Thread(command, id + "-hilo" + i);
				at.add(t);
			}
			for(i = 0; i < repeat; i++){
				(at.get(i)).start();
			}
			for(i = 0; i < repeat; i++){
				(at.get(i)).join();
			}
			assertTrue(true);
		}catch(Exception e){
			fail(e.getMessage());
		}
	}
}
