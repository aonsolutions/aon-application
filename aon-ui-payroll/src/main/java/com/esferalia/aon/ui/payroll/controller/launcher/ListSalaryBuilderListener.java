package com.esferalia.aon.ui.payroll.controller.launcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.SalaryBuilderListenerLevel;

public class ListSalaryBuilderListener implements ISalaryBuilderListener {

	public static class LogMessage {
		private String msg;
		private Integer salaryId;
		private Integer contractId;
		
		private SalaryBuilderListenerLevel level;
		private String employeeName ;
		private String enterpriseName;
		
		private Date startDate;
		private Date endDate;
		private Date issueDate;
		
		public LogMessage(SalaryBuilderListenerLevel level, String msg) {
			this.level = level;
			this.msg = msg;
		}
		
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public Integer getSalaryId() {
			return salaryId;
		}
		public void setSalaryId(Integer salaryId) {
			this.salaryId = salaryId;
		}
		public Integer getContractId() {
			return contractId;
		}
		public void setContractId(Integer contractId) {
			this.contractId = contractId;
		}
		public SalaryBuilderListenerLevel getLevel() {
			return level;
		}
		public void setLevel(SalaryBuilderListenerLevel level) {
			this.level = level;
		}
		public String getEmployeeName() {
			return employeeName;
		}
		public void setEmployeeName(String employeeName) {
			this.employeeName = employeeName;
		}
		public String getEnterpriseName() {
			return enterpriseName;
		}
		public void setEnterpriseName(String companyName) {
			this.enterpriseName = companyName;
		}
		public Date getStartDate() {
			return startDate;
		}
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}
		public Date getEndDate() {
			return endDate;
		}
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}
		
		public void setIssueDate(Date issueDate) {
			this.issueDate = issueDate;
		}
		
		public Date getIssueDate() {
			return issueDate;
		}
	}

	protected StringBuffer buf;
	private File file;
	
	private int errorCounter;
	private int warningCounter;
	
	private boolean saveLog;
	private boolean debugEnabled;
	
	protected List<LogMessage> list; // Usado como una pila FIFO.
	
	public ListSalaryBuilderListener() {
		errorCounter = 0;
		warningCounter = 0;
		list =  new LinkedList<LogMessage>();
	}

	public File getFile() {
		return file;
	}

	public int getErrorCounter() {
		return errorCounter;
	}
	public int getWarningCounter() {
		return warningCounter;
	}
	public List<LogMessage> getList() {
		return list;
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

	public void onMessage(LogMessage msg) {
		list.add(msg);
		saveToLog(msg);
	}

	protected void addMessage(SalaryBuilderListenerLevel level, String msg ) {
		if (list.size() > 256 ) {
			list.remove(0);
		}

		LogMessage logMsg = new LogMessage(level, msg );
		list.add(logMsg);
		saveToLog(logMsg);
	}
	
	// TODO : What daemons is this ? . Use 'printf' or 'String.format' 
	protected void saveToLog(LogMessage msg) {
		if (isSaveLog()) {
			try {
				FileWriter fstream = new FileWriter(getFile(), true);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(DateFormat.getDateTimeInstance().format(new Date()));
				out.write(" ");
				out.write(msg.level.toString());
				out.write(" ");
				out.write(msg.getMsg());
				out.write("\r\n");
				out.close();
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}
		}
	}
	

}
