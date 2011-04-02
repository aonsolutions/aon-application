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

		TestLogMessage testMsg = new TestLogMessage();
		if(level==SalaryBuilderListenerLevel.ERROR){
			testMsg.setLink(true);
			try {
				testMsg.setContractId(getDBSalary().getContract());
			} catch (SQLException e1) {
				// NADA, el id de contrato se queda a null
			}
		} else {
			testMsg.setLink(false);
		}
		testMsg.setMsg(buf.toString());
		
		getTestList().add(testMsg);
		if (isSaveLog()) {
			try {
				FileWriter fstream = new FileWriter(getFile(), true);
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
	
	public class TestLogMessage {
		private String msg;
		private boolean link;
		private Integer salaryId;
		private Integer contractId;
		
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public boolean isLink() {
			return link;
		}
		public void setLink(boolean link) {
			this.link = link;
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
	}
	
}
