package es.inteco.xbrl.pgc.tests;

import java.util.ArrayList;
import junit.framework.TestCase;

public class LP8ValidateTest extends TestCase implements ILPUsoTest{
	
	private String id;
	private String inputPath;
	private int repeat;
	
	public LP8ValidateTest() {
		super();
	}
	
	public LP8ValidateTest(String name) {
		super(name);
	}
	
	public String getName(){
		return id;
	}
	
	public void initialize(String name, String input, String output, String expected){
		id = name;
		inputPath = input;
		repeat = Integer.parseInt(expected.trim());
	}
	
	public void testLP8(){
		try{
			int i = 0;
			LP8ValidateTestCmd command = null;
			ArrayList<Thread> at = new ArrayList<Thread>();
			Thread t = null;
			for(i = 0; i < repeat; i++){
				command = new LP8ValidateTestCmd(inputPath);
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
