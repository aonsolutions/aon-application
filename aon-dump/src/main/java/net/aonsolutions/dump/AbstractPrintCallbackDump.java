package net.aonsolutions.dump;

import java.io.PrintStream;

public abstract class AbstractPrintCallbackDump implements CallbackDump {

	protected PrintStream out;

	public AbstractPrintCallbackDump(PrintStream out) {
		this.out =out;
	}

	public void println() {
		out.println();
	}

	public void println(String s) {
		out.println(s);
	}
	
	public void print(String s) {
		out.print(s);
	}

}