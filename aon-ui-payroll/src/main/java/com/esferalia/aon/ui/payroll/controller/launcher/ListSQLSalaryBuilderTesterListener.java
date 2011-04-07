package com.esferalia.aon.ui.payroll.controller.launcher;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilderTester;
import com.esferalia.aon.payroll.sql.AbstractSQL.ISalary;
import com.esferalia.aon.salary.SalaryBuilderListenerLevel;

public class ListSQLSalaryBuilderTesterListener extends
		ListSalaryBuilderListener {

	
	private SQLSalaryBuilderTester sqlSalaryBuilderTester;
	private LinkedList<TestLogMessage> list; // Usado como una pila FIFO.
	
	public ListSQLSalaryBuilderTesterListener( SQLSalaryBuilderTester sqlSalaryBuilderTester) {
		super();
		this.sqlSalaryBuilderTester = sqlSalaryBuilderTester;
	}

	public ISalary getSalaryDraft() {
		return sqlSalaryBuilderTester.getSalaryDraft();
	}

	public ISalary getDBSalary() {
		return sqlSalaryBuilderTester.getDBSalary();
	}
	
	public LinkedList<TestLogMessage> getTestList() {
		if (list == null) {
			list = new LinkedList<TestLogMessage>();
		}
		return list;
	}
	
	@Override
	protected void addMessage(SalaryBuilderListenerLevel level, String msg ) {
		if (getTestList().size() > 256 ) {
			getTestList().pop();
		}
		
		buf = new StringBuffer(PREFIX0);
		buf.append(level);
		buf.append(PREFIX1);
		buf.append(level);
		buf.append(SPACE);
		buf.append(msg);
		buf.append(SUFIX);

		ISalary salaryDraft = getSalaryDraft();

		TestLogMessage testMsg = new TestLogMessage(level, msg );
		if ( salaryDraft != null ) {
			try {
				testMsg.setContractId(salaryDraft.getContract());
				testMsg.setEmployeeName(salaryDraft.getEmployeeName());
				testMsg.setEnterpriseName(salaryDraft.getEnterpriseName());
			} catch (SQLException e) {
				// TODO employee & enterprise name a null ???
			}
		}

		getTestList().add(testMsg);
		saveToLog(testMsg);
	}

	public void onInfo(TestLogMessage msg) {
		getTestList().add(msg);
		saveToLog(msg);
	}

	
	public static class TestLogMessage {
		private String msg;
		private Integer salaryId;
		private Integer contractId;
		
		private SalaryBuilderListenerLevel level;
		private String employeeName ;
		private String enterpriseName;
		
		
		public TestLogMessage(SalaryBuilderListenerLevel level, String msg) {
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
	}
	
	
	private void saveToLog(TestLogMessage msg) {
		if (isSaveLog()) {
			try {
				FileWriter fstream = new FileWriter(getFile(), true);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(DateFormat.getDateTimeInstance().format(new Date()));
				out.write(SPACE);
				out.write(msg.level.toString());
				out.write(SPACE);
				out.write(msg.getMsg());
				out.write("\r\n");
				out.close();
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}
		}
	}
}
