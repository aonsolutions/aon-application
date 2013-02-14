package com.code.aon.ui.fiscal.controller.model;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.FiscalModelStatus;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.model.FiscalModelManagerFactory;
import com.code.aon.fiscal.model.IFiscalDeclaration;
import com.code.aon.fiscal.model.IFiscalModelManager;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public abstract class FiscalModelController extends BasicController {
	
	private IFiscalDeclaration declaration;
	private IFiscalModelManager manager;
	private FileOutput fileOutput;
	
	public IFiscalModelManager getFiscalModelManager() throws AonException {
		if (manager == null) {
			FiscalModelManagerFactory factory = new FiscalModelManagerFactory();
			manager = factory.getManager( getModelType() );
		}
		return manager;
	}
	
	public FileOutput getFileOutput() {
		return fileOutput;
	}
	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
	}

	
	public IFiscalDeclaration getDeclaration() {
		return declaration;
	}

	public void setDeclaration(IFiscalDeclaration fiscalModel) {
		this.declaration = fiscalModel;
	}

	public void initialize() throws AonException {
		FiscalModel to = (FiscalModel) getTo();
		to.setModel(getModelType());
		setDeclaration( (getFiscalModelManager().initializeFiscalModel(to)) );
	}
	
	public void initializeDetails() throws AonException {
		setDeclaration( (getFiscalModelManager().initializeFiscalModelDetails(getDeclaration())));
		insertOrUpdateDetails();
	}
	
	public void load() throws AonException {
		FiscalModel to = (FiscalModel) getTo();
		setDeclaration( (getFiscalModelManager().loadFiscalModel(to)) );
	}
	
	public void unload() throws AonException {
		setDeclaration(null);
	}
	
	public void onFinish(ActionEvent event) {
		try {
			FiscalModel to = (FiscalModel) getTo();
			to.setStatus(FiscalModelStatus.FINISHED);
			accept(event);
		} catch (Exception e) {
			String msg = "No se pueden finalizar la declaración." + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void onReopen(ActionEvent event) {
		try {
			FiscalModel to = (FiscalModel) getTo();
			to.setStatus(FiscalModelStatus.PENDING);
			accept(event);
		} catch (Exception e) {
			String msg = "No se pueden reabrir la declaración." + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		
	}
	
	public void onRecalculate(ActionEvent event) {
		try {
			getDeclaration().calculate();
		} catch (AonException e) {
			String msg="No se pudo recalcular la declaración";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	public void insertOrUpdateDetails() throws AonException {
		getDeclaration().calculate();
		FiscalModel to = (FiscalModel) getTo();
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		for (FiscalModelDetail detail : getDeclaration().getDetails()) {
			detail.setFiscalModel(to);
			detail = (FiscalModelDetail) bean.insertOrUpdate(detail);
		}
	}

	public void removeDetails() throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		for (FiscalModelDetail detail : getDeclaration().getDetails()) {
			bean.remove(detail);
		}
	}
	
	public boolean isDiskOk() {
		int errors = 0;
		if (getFileOutput() != null) {
			errors = getFileOutput().getErrors().size();
		}
		return (errors==0);
	}

	public void downloadDisk(ActionEvent event) throws ManagerBeanException {
        try {
    		FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
        	String fileName = getFileName();
        	MimeType mimeType = getMimeType();
	        response.setContentType(mimeType.getName());
	        response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "." + mimeType.getExtension()+"\";");
	        ServletOutputStream output = response.getOutputStream();
	        InputStream input = getFileOutput().getFile() != null
	        		?new FileInputStream(getFileOutput().getFile())
	        		:new ByteArrayInputStream(getFileOutput().getContent());
	        int size = IOUtils.copy(input, output);
	        if (size > 0) {
		        response.setHeader("Content-Length", String.valueOf(size));
	        }
	        output.close();
	        input.close();
	        response.flushBuffer();
	        faces.responseComplete();
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	protected abstract FiscalModelType getModelType();
	public abstract boolean isDifEnabled();
	public abstract String getFileName();
	public abstract MimeType getMimeType();
	public abstract void onCreateDisk(ActionEvent event);
	
	
}
