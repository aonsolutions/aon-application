package com.esferalia.aon.ui.payroll.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ui.employee.file.CertificateWriter;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.EnterpriseCertificate;
import com.esferalia.aon.payroll.EnterpriseCertificateDetail;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.enumeration.FileStatus;

public class CertificateFileGenerationWizard implements Serializable {

	private static final long serialVersionUID = 1981061969452291161L;

	private int currentStep;
	private static final String[] STEPS = { 
		"certificateFileGenerationWizard_step0", 
		"certificateFileGenerationWizard_step1", 
		"certificateFileGenerationWizard_step2", 
		"certificateFileGenerationWizard_step3" };
	private FileOutput fileOutput;
	private CertificateWriter certificateWriter;
	private DataModel model;
	private DataModel selectedModel;
	private EnterpriseCertificate batch;
	private List<RemesableCertificate> selectedBatchList;
	private List<EnterpriseCertificateDetail> detailList;
	private CertificateBatchParams params;
	
	
	public CertificateBatchParams getParams() {
		if (params == null) {
			params = new CertificateBatchParams();
		}
		return params;
	}

	public void setParams(CertificateBatchParams params) {
		this.params = params;
	}

	public EnterpriseCertificate getBatch() {
		return batch;
	}
	
	public void setBatch(EnterpriseCertificate batch) {
		this.batch = batch;
	}
	
	public List<RemesableCertificate> getSelectedBatchList() {
		return selectedBatchList;
	}

	public void setSelectedBatchList(List<RemesableCertificate> selectedBatchList) {
		this.selectedBatchList = selectedBatchList;
	}
	
	public List<EnterpriseCertificateDetail> getDetailList() {
		if(detailList==null){
			detailList = new ArrayList<EnterpriseCertificateDetail>();
		}
		return detailList;
	}

	public void setDetailList(List<EnterpriseCertificateDetail> detailList) {
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
	
	public CertificateController getCertificateController() {
		return (CertificateController)AonUtil.getRegisteredBean("enterpriseCertificate");
	}
	
	public DataModel getModel() {
		return model;
	}
	
	public void setModel(DataModel model) {
		this.model = model;
	}
	
	public DataModel getSelectedModel() {
		return selectedModel;
	}
	
	public void setSelectedModel(DataModel selectedModel) {
		this.selectedModel = selectedModel;
	}

	private void initializeBatchModel() throws PayrollException {
		model = new ListDataModel(transformBatchList(getCertificateController().getCertificateBatchList(getParams())));
	}
	
	protected void initializeBatchModel(List<EnterpriseCertificate> list) {
		model = new ListDataModel(transformBatchList(list));
	}
	
	private List<RemesableCertificate> transformBatchList(
			List<EnterpriseCertificate> remesas) {
		List<RemesableCertificate> list = new ArrayList<RemesableCertificate>();
		for (EnterpriseCertificate remesa : remesas) {
			RemesableCertificate r = new RemesableCertificate();
			r.setBatch(remesa);
			list.add(r);
		}
		return list;
	}

	// Action Listeners
	public void onNext(ActionEvent event) {
		if (getCurrentStep() == 0) {
			onSearch(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 1) {
			onValidate(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 2) {
			onZipGenerate(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 3) {
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
		return (getCurrentStep() < 3);
	}
	
	@SuppressWarnings("unchecked")
	private void generateBatchList() throws PayrollException{
		setSelectedBatchList(null);
		setDetailList(null);
		List<RemesableCertificate> list = new LinkedList<RemesableCertificate>();
		for (RemesableCertificate remesable : (List<RemesableCertificate>) getModel().getWrappedData()) {
			if (remesable.isSelected()) {
				list.add(remesable);
				getDetailList().addAll(getCertificateController().getCertificateDetailBatchList(remesable.getBatch()));
			}
		}
		setSelectedBatchList(list);
		setSelectedModel(new ListDataModel(list));
	}

	// ***************************************************
	public void onStart(ActionEvent event) {
		setCurrentStep(0);
		setParams(null);
		getParams().setDate(Calendar.getInstance().getTime());
		FileStatus[] estados = {FileStatus.PENDIENTE};
		getParams().setStatusList(estados);
		try {
			initializeBatchModel();
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	private void onValidate(ActionEvent event) {
		if(getModel().getRowCount()<=0){
			String msg = "Debe seleccionar alguna remesa";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		try {
			generateBatchList();
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	public void onDownloadDisk(ActionEvent event) {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = getCertificateWriter().getCertificate().getFile();
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
		RemesableCertificate r = (RemesableCertificate) getSelectedModel().getRowData();
		r.setShowEmployees(!r.isShowEmployees());
	}
	
	public void onSearch(ActionEvent event) {
		try {
			initializeBatchModel();
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	public void onSelectAll(ActionEvent event) {
		processAll(true);
	}

	public void onDeselectAll(ActionEvent event) {
		processAll(false);
	}

	private void processAll(boolean selected) {
		for (int i = 0; i < getModel().getRowCount(); i++) {
			getModel().setRowIndex(i);
			RemesableCertificate r = (RemesableCertificate) getModel().getRowData();
			r.setSelected(selected);
		}
	}
	
	public void onZipGenerate(ActionEvent event) {
		byte[] buf = new byte[1024];
		try {
			File file = File.createTempFile("aon-zip", MimeType.MIME_ZIP.getExtension());
			FileOutputStream fos = new FileOutputStream(file);
			ZipOutputStream out = new ZipOutputStream(fos);
			
//			for(RemesableCertificate remesable: getSelectedBatchList()){
//				setFileOutput(getCertificateWriter().createCertificate(remesable.getBatch(), getCertificate().getCertificateDetailBatchList(remesable.getBatch())));
//				FileInputStream in = new FileInputStream(getFileOutput().getFile());
//				out.putNextEntry(new ZipEntry(getCertificateWriter().getCertificate().getFichero()+".xml"));
//				int len;
//				while ((len = in.read(buf)) > 0) {
//					out.write(buf, 0, len);
//				}
//				out.closeEntry();
//				in.close();
//				
//				remesable.getBatch().setStatus(FileStatus.GENERADO);
//				getCertificate().accept(remesable.getBatch());
//			}
			
			out.close();
			FileOutput output = new FileOutput();
			output.setFile(file);
			output.setErrors(new ArrayList<Exception>());
			setFileOutput(output);
		} catch (IOException e) {
			AonUtil.addErrorMessage(e.getMessage());
		} 
//		catch (ManagerBeanException e) {
//			AonUtil.addErrorMessage(e.getMessage());
//			// No se lanza excepción, que vaya a la última página.
//		} catch (PayrollException e) {
//			AonUtil.addErrorMessage(e.getMessage());
//		} 
	}	
	
	public void onDownloadZip(ActionEvent event) {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = "aon-out";
			response.setContentType(MimeType.MIME_ZIP.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".zip\";");

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
	
}
