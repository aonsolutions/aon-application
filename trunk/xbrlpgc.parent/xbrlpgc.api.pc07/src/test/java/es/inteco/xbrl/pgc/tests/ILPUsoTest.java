package es.inteco.xbrl.pgc.tests;

import junit.framework.Test;

public interface ILPUsoTest extends Test{

	public abstract void initialize(String name, String input, String output, String expected);
	public abstract String getName();
	
}
