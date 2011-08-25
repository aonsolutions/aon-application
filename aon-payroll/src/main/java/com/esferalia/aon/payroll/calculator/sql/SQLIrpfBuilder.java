package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.esferalia.aon.payroll.sql.AbstractSQL;
import com.esferalia.aon.payroll.sql.SQLWriter;
import com.esferalia.aon.salary.ISalaryBuilderListener;

public class SQLIrpfBuilder  {

	private ISalaryBuilderListener listener;
	private SQLWriter sqlWriter;
	private int insertedContractData;
	private static final String FORMAT = "[%s]: %s - %s";
	
	public SQLIrpfBuilder(Connection connection) 
	throws SQLException
	{
		super();
		sqlWriter = new SQLWriter(connection);
	}
	
	public void saveIrpf() {
		try {
			insertIrpf();
			if (listener.isDebugEnabled()) {
				String msg = String.format(FORMAT, 
						contractData.getContract(),
						contractData.getName(),
						contractData.getExpression());
				listener.onDebug(msg);
			}
			++insertedContractData;
		} catch (SQLException e) {
		}
	}

	public void begin() throws SQLException{
		sqlWriter.begin();
	}
	
	public void commit() throws SQLException{
		sqlWriter.commit();
	}

	public void rollback() throws SQLException{
		sqlWriter.rollback();
	}

	public void insertIrpf() throws SQLException{
		sqlWriter.insertContractData(contractData);
	}

	public void setListener(ISalaryBuilderListener listener) {
		this.listener = listener;		
	}
	public ISalaryBuilderListener getListener() {
		return listener;
	}

	public int getInsertedContractData() {
		return insertedContractData;
	}
	
	
	
	//*********************************************************
	//*********************************************************
	//	AbtractSQLSalaryBuilder
	//*********************************************************
	//*********************************************************
	
	protected AbstractSQL.ContractData contractData;
	
	public void createNewContractData() {
		contractData = null;
		
		contractData = new AbstractSQL.ContractData();
	}

	public void setContract(Integer contract) {
		contractData.setContract(contract);
	}
	
	public void setStartDate(Date date){
		contractData.setStartDate(date);
	}
	
	public void setEndDate(Date date){
		contractData.setEndDate(date);
	}
	
	public void setExpression(String expression){
		contractData.setExpression(expression);
	}
	
	public void setName(String name){
		contractData.setName(name);
	}



}
