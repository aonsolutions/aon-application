package com.esferalia.aon.occam.api.model.console;

public interface ConsoleLogger {
	void title(String id,String msg);
	void subtitle(String id,String msg);
	void ok(String id,String msg);
	void error(String id, String msg );
	void warning(String id, String msg);
	void message(String id,String msg);
	void progress(String id,int count, int progress, String msg);
	void progress(String id,int count, int progress);
	void mainProgress(String id, int count, int progress);
}
