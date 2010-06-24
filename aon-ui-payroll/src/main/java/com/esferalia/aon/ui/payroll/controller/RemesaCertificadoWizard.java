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
import com.esferalia.aon.payroll.core.empresa.EmpresaDAOFactory;
import com.esferalia.aon.payroll.core.empresa.IEmpresaDAO;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresa;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresaDetalle;
import com.esferalia.aon.ui.payroll.file.CertificateWriter;

public class RemesaCertificadoWizard implements Serializable {

	private static final long serialVersionUID = -8284038117326971930L;
	
	private int currentStep;
	private static final String[] STEPS = { "remesaCertificadoWizard_step0", "remesaCertificadoWizard_step1", "remesaCertificadoWizard_step2" };
	private FileOutput fileOutput;
	private CertificateWriter certificateWriter;
	private IEmpresaDAO empresaDAO;
	private DataModel model;
	private IRemesaCertificadoEmpresa remesa;
	private List<IRemesaCertificadoEmpresaDetalle> detailList;
	
	public IRemesaCertificadoEmpresa getRemesa() {
		return remesa;
	}
	
	public void setRemesa(IRemesaCertificadoEmpresa remesa) {
		this.remesa = remesa;
	}
	
	public List<IRemesaCertificadoEmpresaDetalle> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<IRemesaCertificadoEmpresaDetalle> detailList) {
		this.detailList = detailList;
	}

	private CertificateWriter getCertificateWriter() {
		if (certificateWriter == null) {
			certificateWriter = new CertificateWriter();
		}
		return certificateWriter;
	}
	
	public FileOutput getFileOutput() {
		return fileOutput;
	}

	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
	}

	public int getCurrentStep() {
		return this.currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}
	
	public IEmpresaDAO getEmpresaDAO() {
		if (empresaDAO == null) {
			empresaDAO = EmpresaDAOFactory.getInstance().getEmpresaDAO();
		}
		return empresaDAO;
	}
	
	public DataModel getModel() {
		return model;
	}
	
	public void setModel(DataModel model) {
		this.model = model;
	}

	private void initializeEmpleadoModel() throws PayrollException {
		if (model == null) {
			model = new ListDataModel(getEmpresaDAO().getRemesaCertificados());
		}
	}
	
	private void refreshDetailList() throws PayrollException {
		setDetailList(getEmpresaDAO().getDetalleRemesaCertificados(getRemesa()));
	}
	

	// Action Listeners
	public void onNext(ActionEvent event) {
		if (getCurrentStep() == 0) {
			onSearch(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 1) {
			onValidate(event);
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
		setCurrentStep(0);
		try {
			initializeEmpleadoModel();
		} catch (PayrollException e) {
			// NADA
		}
	}

	private void onValidate(ActionEvent event) {
		
	}
	
	public void onDiskGenerate(ActionEvent event) {
		try {
			String loggedUser = AonUtil.getRemoteUser();
			loggedUser = StringUtils.substringBefore(loggedUser, "@");
			setFileOutput(getCertificateWriter().createCertificate(getRemesa(), getDetailList()));
			if (getFileOutput() != null) {
				if (getFileOutput().getErrors().size() > 0) {
					AonUtil.addErrorMessage("Se han producido errores en la generación del fichero.");
				}
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			// No se lanza excepción, que vaya a la última página.
		} 
	}
	
	public void onDownloadDisk(ActionEvent event) {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = getCertificateWriter().getCertificate().getFichero();
			response.setContentType(MimeType.MIME_XML.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xml\";");

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
		setCurrentStep(1);
		IRemesaCertificadoEmpresa remesa = (IRemesaCertificadoEmpresa)getModel().getRowData();
		setRemesa(remesa);
		try {
			refreshDetailList();
		} catch (PayrollException e) {
			// NADA
		}
	}
	
	private void onSearch(ActionEvent event) {
		try {
			initializeEmpleadoModel();
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	
	
}
