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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.calculator.IrpfCalculator;
import com.esferalia.aon.payroll.calculator.sql.SQLIrpfCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilderTester.UnExpectedValue;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.ui.payroll.controller.launcher.ListIrpfBuilderListener.LogMessage;

public abstract class AbstractIrpfLauncher {
	
	private boolean saveLog;
	private boolean pollEnabled;
	private boolean debugEnabled;
	private boolean refreshEnabled;

	private IrpfLauncherParams params;
	
	protected ListIrpfBuilderListener listener;


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
	
	public void setListener(ListIrpfBuilderListener listener) {
		this.listener = listener;
	}

	public List<LogMessage> getMessages() {
		return listener != null ? listener.getList() : null;
	}

	public boolean isEmptyLog() {
		if ( listener == null ) {
			return true;
		}
		File file = listener.getFile();
		if ( file == null ) {
			return true;
		}
		
		return file.canRead();
	}

	public void downloadDisk(ActionEvent event) {
		try {
			
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			File file = listener.getFile();
			String fileName = file.getAbsolutePath();
			response.setContentType(MimeType.MIME_TXT.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".txt\";");
			ServletOutputStream output = response.getOutputStream();
			InputStream input = new FileInputStream(file);
			int size = IOUtils.copy(input, output);
			if (size > 0) {
				response.setHeader("Content-Length", String.valueOf(size));
			}
			output.close();
			input.close();

			response.flushBuffer();
			faces.responseComplete();
		} catch (IOException e) {
			throw new AbortProcessingException("Imposible descargar fichero");
		}
	}

	public void onStart(ActionEvent event) {
		setParams(null);
		setListener(null);
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
	
	protected Connection getConnection(){
		String sessionFactory = HibernateUtil.getSessionFactoryName(Salary.class.getName());
		return  HibernateUtil.getSQLConnection(sessionFactory);
	}
	
	protected void calculate ( IrpfCalculator calculator ) 
		throws SalaryException, ExpressionException, SQLException {
		SQLIrpfCalculatorContext sqlCtx = new SQLIrpfCalculatorContext(getConnection(), params.getDate(), params.getCriteria());
		try {
			calculator.calculate(sqlCtx);
		} catch (UnExpectedValue e) {
			listener.onError(e.getLocalizedMessage());
		} catch ( SalaryException e ) {
			listener.onError(e.getLocalizedMessage());
		}
	}
	
	protected abstract void execute(IrpfLauncherParams irpfLauncherParams) throws SalaryException ;
	
	private class TestThread extends Thread {

	    public void run() {
			Date startTime = new Date();
			try {
				execute(getParams());
				String msg = "Proceso Finalizado correctamente.";
				listener.onInfo(msg);
			} catch (Throwable e) {
				listener.onError(e.getLocalizedMessage());
				String msg = "Se produjeron errores en el calculo de nóminas.";
				listener.onError(msg);
			}
			if (listener.getWarningCounter() > 0) {
				String msg = "Se produjeron " + listener.getWarningCounter() + " mesajes de aviso.";
				listener.onInfo(msg);	
			}
			
			if (listener.getErrorCounter() > 0) {
				String msg = "Se produjeron " + listener.getErrorCounter() + " mesajes de error.";
				listener.onInfo(msg);	
			}
			
			Date endTime = new Date();
			long milis = endTime.getTime() - startTime.getTime();
			
			long hora = milis/3600000;
			long restohora = milis%3600000;
			long minuto = restohora/60000;
			long restominuto = restohora%60000;
			long segundo = restominuto/1000;
			long restosegundo = restominuto%1000;
			listener.onInfo("Tiempo de proceso: " + 
						(hora>0?""+hora + " hora"+(hora==1?"":"s"):"")
						+(minuto>0?" "+minuto + " minuto"+(minuto==1?"":"s"):"")
						+(segundo>0?" "+segundo + " segundo"+(segundo==1?"":"s"):"")
						+" " + restosegundo + " milisegundos.");				
			setPollEnabled(false);
			
	    }
	}
	
}
