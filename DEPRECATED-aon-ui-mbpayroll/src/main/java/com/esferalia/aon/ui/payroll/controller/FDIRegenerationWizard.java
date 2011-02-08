package com.esferalia.aon.ui.payroll.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.it.IParteITDAO;
import com.esferalia.aon.payroll.core.it.ParteITDAOFactory;
import com.esferalia.aon.payroll.core.remesa.IRemesaINSS;
import com.esferalia.aon.payroll.core.remesa.IRemesaParteIT;
import com.esferalia.aon.ui.payroll.file.FDIWriter;

public class FDIRegenerationWizard implements Serializable {

	private static final long serialVersionUID = 6728360777902767565L;

	private int currentStep;
	private static final String[] STEPS = { "FDIRegenerationWizard_step0", "FDIRegenerationWizard_step1", "FDIRegenerationWizard_step2" };
	private FileOutput fileOutput;
	private FDIWriter fdiWriter;
	private IRemesaINSS remesaINSS;
	private IParteITDAO parteITDAO;
	private DataModel remesaINSSModel;
	
	public DataModel getRemesaINSSModel() {
		if(remesaINSSModel==null){
			remesaINSSModel = new ListDataModel(getRemesaINSSList());
		}
		return remesaINSSModel;
	}
	
	public void setRemesaINSSModel(DataModel remesaINSSModel) {
		this.remesaINSSModel = remesaINSSModel;
	}
	
	private FDIWriter getFDIWriter() {
		if (fdiWriter == null) {
			fdiWriter = new FDIWriter();
		}
		return fdiWriter;
	}
	
	public FileOutput getFileOutput() {
		return fileOutput;
	}

	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
	}
	
	public void setRemesaINSS(IRemesaINSS remesaINSS) {
		this.remesaINSS = remesaINSS;
	}
	public IRemesaINSS getRemesaINSS(){
		return remesaINSS;
	}

	public int getCurrentStep() {
		return this.currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}

	public void onNext(ActionEvent event) {
		if (getCurrentStep() == 0) {
			onValidate(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 1) {
			onDiskGenerate(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 2) {
			onFinish(event);
		} 
	}

	public void onPrevious(ActionEvent event) {
		setCurrentStep(getCurrentStep() - 1);
	}

	public String previous() {
		return STEPS[getCurrentStep()];
	}

	public String next() {
		return STEPS[getCurrentStep()];
	}

	public boolean isPreviousAvailable() {
		return (getCurrentStep() > 0);
	}

	public boolean isNextAvailable() {
		return (getCurrentStep() < 2);
	}

	// ***************************************************
	public void onStart(ActionEvent event) {
		setRemesaINSS(null);
		setCurrentStep(0);
	}
	
	private void onValidate(ActionEvent event) {
		if (getRemesaINSS()==null) {
			String msg = "Realice alguna selección";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void onDiskGenerate(ActionEvent event) {
		try {
			String loggedUser = AonUtil.getRemoteUser();
			loggedUser = StringUtils.substringBefore(loggedUser, "@");
			List<IRemesaParteIT> list = getParteITDAO().getRemesaParteITList(getRemesaINSS());
			setFileOutput(getFDIWriter().createFDI(list, loggedUser));
			if (getFileOutput() != null) {
				if (getFileOutput().getErrors().size() > 0) {
					AonUtil.addErrorMessage("Se han producido errores en la generación del fichero.");
				}
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			// No se lanza excepción, que vaya a la última página.
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void onDownloadDisk(ActionEvent event) {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = getFDIWriter().getEti().getFichero() + ".FDI";
			response.setContentType(MimeType.MIME_TXT.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".txt\";");

			ServletOutputStream output = response.getOutputStream();
			InputStream input = new FileInputStream(getFileOutput().getFile());
			int size = IOUtils.copy(input, output);
			if (size > 0) {
				response.setHeader("Content-Length", String.valueOf(size));
			}
			output.close();
			input.close();

			response.flushBuffer();
			faces.responseComplete();
		} catch (IOException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public boolean isDiskOk() {
		int errors = 0;
		if (getFileOutput() != null) {
			errors = getFileOutput().getErrors().size();
		}
		return (errors == 0);
	}

	private void onFinish(ActionEvent event) {
		onStart(event);
	}

	public void onSelect(ActionEvent event) {
		setRemesaINSS((IRemesaINSS)getRemesaINSSModel().getRowData());
		setCurrentStep(1);
	}

	public IParteITDAO getParteITDAO() {
		if (parteITDAO == null) {
			parteITDAO = ParteITDAOFactory.getInstance().getParteITDAO();
		}
		return parteITDAO;
	}
	
	public List<IRemesaINSS> getRemesaINSSList(){
		try {
			return getParteITDAO().getRemesaINSS(null);
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	public List<IRemesaParteIT> getRemesaParteITList(){
		try {
			return getParteITDAO().getRemesaParteITList(getRemesaINSS());
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	public DataModel getRemesaParteITModel(){
		return new ListDataModel(getRemesaParteITList());
	}
	
}
