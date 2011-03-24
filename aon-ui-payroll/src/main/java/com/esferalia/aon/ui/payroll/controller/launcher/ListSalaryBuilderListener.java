package com.esferalia.aon.ui.payroll.controller.launcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.SalaryBuilderListenerLevel;

public class ListSalaryBuilderListener implements ISalaryBuilderListener {

	private static final String SPACE = " ";
	private static final String STYLE =
		"<style type=\"text/css\">"
		+"	.lst {margin-left: 20px; font-family: Courier; white-space: nowrap;}"
		+"	.lst_ERROR {color:red; font-weight: bold;}"
		+"	.lst_WARNING {color:red;}"
		+"	.lst_INFO {color:black;}"
		+"   .lst_DEBUG {color: blue;}"
		+"<style>";
	private static final String PREFIX0 = "<li><span class=\"lst lst_";
	private static final String PREFIX1 = "\">";
	private static final String SUFIX = "</span></li>";

	private LinkedList<String> list; // Usado como una pila FIFO.
	private StringBuffer buf;
	private File file;
	
	private int errorCounter;
	private int warningCounter;
	
	private boolean debugEnabled;
	private boolean saveLog;
	
	public ListSalaryBuilderListener() {
		errorCounter = 0;
		warningCounter = 0;
	}

	public File getFile() {
		return file;
	}
	public String getStyle() {
		return STYLE;
	}
	public int getErrorCounter() {
		return errorCounter;
	}
	public int getWarningCounter() {
		return warningCounter;
	}
	public LinkedList<String> getList() {
		if (list == null) {
			list = new LinkedList<String>();
		}
		return list;
	}
	
	private void addMessage(SalaryBuilderListenerLevel level, String msg ) {
		if (getList().size() > 25 ) {
			getList().pop();
		}
		
		buf = new StringBuffer(PREFIX0);
		buf.append(level);
		buf.append(PREFIX1);
		buf.append(level);
		buf.append(SPACE);
		buf.append(msg);
		buf.append(SUFIX);

		getList().add(buf.toString());
		if (isSaveLog()) {
			try {
				FileWriter fstream = new FileWriter(file, true);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(DateFormat.getDateTimeInstance().format(new Date()));
				out.write(SPACE);
				out.write(level.toString());
				out.write(SPACE);
				out.write(msg);
				out.write("\r\n");
				out.close();
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}
		}
	}

	@Override
	public boolean isDebugEnabled() {
		return debugEnabled;
	}
	public void setDebugEnabled(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
	}
	
	public boolean isSaveLog() {
		return saveLog;
	}
	public void setSaveLog(boolean saveLog) {
		if (saveLog) {
			try {
				file = File.createTempFile("salaryBuilder", ".txt");
			} catch (IOException e) {
				setSaveLog(false);
			}	
		} else {
			file = null;	
		}
		this.saveLog = saveLog;
	}

	@Override
	public void onError(String msg) {
		++errorCounter;
		addMessage(SalaryBuilderListenerLevel.ERROR,msg);
	}

	@Override
	public void onWarning(String msg) {
		++warningCounter;
		addMessage(SalaryBuilderListenerLevel.WARNING,msg);
	}

	@Override
	public void onInfo(String msg) {
		addMessage(SalaryBuilderListenerLevel.INFO,msg);
	}

	@Override
	public void onDebug(String msg) {
		if (isDebugEnabled()) {
			addMessage(SalaryBuilderListenerLevel.DEBUG,msg);	
		}
	}
}
