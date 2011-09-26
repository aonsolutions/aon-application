/********************************************************************
* Copyright (c) 2011, esferalia NETWORKS S.A
*
* The copyright of the computer program herein is the property 
* of esferalia NETWORKS.
*********************************************************************
* The program may be used and/or copied only with the written 
* permission of esferalia NETWORKS, or in accordance with the 
* terms and conditions stipulated in the agreement contract 
* under which the program has been supplied.
*********************************************************************
*/

package com.esferalia.aon.ui.payroll.controller.launcher;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.aeat.jaxb.AEATRetencionesEntrada2011;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.config.enumeration.Administration;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.irpf.IrpfCalculator;
import com.esferalia.aon.payroll.irpf.IrpfException;
import com.esferalia.aon.payroll.irpf.sql.SQLAEAT2011Factory;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;

public abstract class AbstractIrpfLauncher implements IrpfCalculator.CallbackHandler{
	
	public static class LogMessage {
		
		public enum Level {
			ERROR,
			WARNING,
			INFO,
			DEBUG;
		}
		
		private String msg;
		private Integer contractId;
		
		private Level level;
		private String employeeName ;
		private String enterpriseName;
		
		private Date startDate;
		private Date endDate;
		
		private String currentIrpf;
		private String newIrpf;
		
		public LogMessage(Level level, String msg) {
			this.level = level;
			this.msg = msg;
		}
		
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public Integer getContractId() {
			return contractId;
		}
		public void setContractId(Integer contractId) {
			this.contractId = contractId;
		}
		public Level getLevel() {
			return level;
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

		public String getCurrentIrpf() {
			return currentIrpf;
		}
		public void setCurrentIrpf(String currentIrpf) {
			this.currentIrpf = currentIrpf;
		}

		public String getNewIrpf() {
			return newIrpf;
		}

		public void setNewIrpf(String newIrpf) {
			this.newIrpf = newIrpf;
		}
		
	}
	
	public static class InfoMessage extends LogMessage {
		public InfoMessage(String msg) {
			super(Level.INFO, msg);
		}
	}
	
	public static class WarnMessage extends LogMessage {
		public WarnMessage(String msg) {
			super(Level.WARNING, msg);
		}
	}

	public static class ErrorMessage extends LogMessage {
		public ErrorMessage(String msg) {
			super(Level.ERROR, msg);
		}
	}
	
	public static class DebugMessage extends LogMessage {
		public DebugMessage(String msg) {
			super(Level.DEBUG, msg);
		}
	}

	private boolean saveLog;
	private boolean pollEnabled;
	private boolean debugEnabled;
	private boolean refreshEnabled;
	private boolean saveEnabled;

	private IrpfLauncherParams params;
	
	
	private SQLAEAT2011Factory sqlaeat2011Factory; 
	
	
	private int warns = 0;
	private int errors = 0;
	
	private List<LogMessage> messages = 
		new LinkedList<LogMessage>(){
			@Override
			public boolean add(LogMessage e) {
				
				switch (e.level) {
				case ERROR:
					errors++;
					break;
				case WARNING:
					warns++;
					break;
				}
				return super.add(e);
			}
			
			public void clear() {
				errors = 0;
				warns = 0;
				super.clear();
			};
	}; // FIFO
	

	public final boolean isSaveLog() {
		return saveLog;
	}
	
	public final void setSaveLog(boolean saveLog) {
		this.saveLog = saveLog;
	}

	public final boolean isDebugEnabled() {
		return debugEnabled;
	}
	public final void setDebugEnabled(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
	}
	
	public final boolean isSaveEnabled() {
		return saveEnabled;
	}
	public final void setSaveEnabled(boolean saveEnabled) {
		this.saveEnabled = saveEnabled;
	}
	
	public final boolean isRefreshEnabled() {
		return refreshEnabled;
	}
	public final void setRefreshEnabled(boolean refreshEnabled) {
		this.refreshEnabled = refreshEnabled;
	}

	public final boolean isPollEnabled() {
		return pollEnabled;
	}
	
	public final void setPollEnabled(boolean pollEnabled) {
		this.pollEnabled = pollEnabled;
	}

	public final IrpfLauncherParams getParams() {
		if (params == null) {
			params = new IrpfLauncherParams();
		} // end-if : Lazy init. 
		return params;
	}
	public final  void setParams(IrpfLauncherParams params) {
		this.params = params;
	}
	

	public List<LogMessage> getMessages() {
		return messages;
	}

	public boolean isEmptyLog() {
		return messages.isEmpty();
	}

	public void onStart(ActionEvent event) {
		setParams(null);
		setSaveLog(false);
		setDebugEnabled(false);
		setPollEnabled(false);
		
	}

	public void onExecute(ActionEvent event) {
		setPollEnabled(true);
		TestThread thread =  
			new TestThread();
		thread.start();
	}
	
	public void downloadDisk(ActionEvent event) {
	}

	
	protected void onInfo(String msg) {
		LogMessage infoMessage = 
			new InfoMessage(msg);
		
		messages.add(infoMessage);
	}

	protected void onError(String msg) {
		LogMessage errMessage = 
			new ErrorMessage(msg);
		messages.add(errMessage);
	}

	protected void onWarn(String msg) {
		LogMessage warnMessage = 
			new WarnMessage(msg);
		messages.add(warnMessage);
	}

	protected void onMessage(LogMessage msg) {
		messages.add(msg);
	}

	protected void calculate ( IrpfLauncherParams params) 
	throws SalaryException, ExpressionException, SQLException {
		this.sqlaeat2011Factory = 
			getSQLAEAT2011Factory();
		
		Administration administrations [] = Administration.values();
		
		for (Administration administration : administrations) {
			try {
				calculate(sqlaeat2011Factory, administration );
			} catch (IrpfException e) {
				//TODO:
			}
		}
	}
	
	
	protected void calculate ( SQLAEAT2011Factory sqlaeat2011Factory, Administration administration) 
	throws SalaryException, ExpressionException, SQLException, IrpfException {
		IrpfCalculator irpfCalculator = 
			IrpfCalculator.getCalculator(administration);
		AEATRetencionesEntrada2011 aeatRetencionesEntrada2011 = 
			sqlaeat2011Factory.getAeatRetencionesEntrada2011(administration, this);
		irpfCalculator.calculate(aeatRetencionesEntrada2011, this );
			
	}
	
	
	protected Connection getConnection(){
		String sessionFactory = HibernateUtil.getSessionFactoryName(Salary.class.getName());
		return  HibernateUtil.getSQLConnection(sessionFactory);
	}
	
	
	protected  SQLAEAT2011Factory getSQLAEAT2011Factory() throws SQLException {
		Date date = params.getDate();
		Connection connection = getConnection();

		SQLAEAT2011Factory sqlaeat2011Factory = 
			new SQLAEAT2011Factory(connection, date);
		
		return sqlaeat2011Factory;
	}

	protected void execute(IrpfLauncherParams params) throws SalaryException, ExpressionException, SQLException {
		messages.clear();
		calculate(params);
	}
	
	private class TestThread extends Thread {
		
	    public void run() {
			Date startTime = new Date();
			try {
				
				execute(getParams());
				String msg = "Proceso Finalizado correctamente.";
				onInfo(msg);
			} catch (Throwable e) {
				onError(e.getLocalizedMessage());
				String msg = "Se produjeron errores en el calculo de I.R.P.F.";
				onError(msg);
			}
			if (warns > 0) {
				String msg = "Se produjeron " + warns + " mesajes de aviso.";
				onWarn(msg);	
			}
			
			if (errors > 0) {
				String msg = "Se produjeron " + errors + " mesajes de error.";
				onError(msg);	
			}
			
			Date endTime = new Date();
			long milis = endTime.getTime() - startTime.getTime();
			
			long hora = milis/3600000;
			long restohora = milis%3600000;
			long minuto = restohora/60000;
			long restominuto = restohora%60000;
			long segundo = restominuto/1000;
			long restosegundo = restominuto%1000;
			onInfo("Tiempo de proceso: " + 
						(hora>0?""+hora + " hora"+(hora==1?"":"s"):"")
						+(minuto>0?" "+minuto + " minuto"+(minuto==1?"":"s"):"")
						+(segundo>0?" "+segundo + " segundo"+(segundo==1?"":"s"):"")
						+" " + restosegundo + " milisegundos.");				
			setPollEnabled(false);
			
	    }
	}
	
	
}
