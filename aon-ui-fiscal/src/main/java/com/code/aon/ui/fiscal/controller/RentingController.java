package com.code.aon.ui.fiscal.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.fiscal.Renting;
import com.code.aon.fiscal.enumeration.RentingStatus;
import com.code.aon.fiscal.renting.RentingProvider;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class RentingController extends BasicController {

	private RentingProvider provider;
	private FiscalParametersController fiscalParams;
	private FileOutput fileOutput;

	public RentingProvider getProvider() {
		if (provider == null) {
			provider = new RentingProvider();
		}
		return provider;
	}

	public FiscalParametersController getFiscalParams() {
		if (fiscalParams == null) {
			fiscalParams = (FiscalParametersController) AonUtil.getRegisteredBean( FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
		}
		return fiscalParams;
	}

	public FileOutput getFileOutput() {
		return fileOutput;
	}
	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
	}

	public void initializeRenting() throws ManagerBeanException {
		Renting renting = (Renting) getTo();
		getProvider().initializeRenting(renting);
		getProvider().fillDeclared(renting);
		renting.calculate();
	}
	
	public void onFinish(ActionEvent event){
		Renting renting = (Renting) getTo();
		renting.setStatus(RentingStatus.FINISHED);
		accept(event);
	}
	public void onReopen(ActionEvent event){
		Renting renting = (Renting) getTo();
		renting.setStatus(RentingStatus.PENDING);
		accept(event);
	}

	public void onRecalculate(ActionEvent event ) {
		Renting renting = (Renting) getTo();
		renting.calculate();
	}
	
	public void initializeRentingDetail() throws ManagerBeanException {
		Renting renting = (Renting) getTo();
		getProvider().initializeRentingDetail(renting);
	}

	public boolean isDiskOk() {
		int errors = 0;
		if (getFileOutput() != null) {
			errors = getFileOutput().getErrors().size();
		}
		return (errors==0);
	}

	public void onCreateDisk(ActionEvent event) throws ManagerBeanException {
		AonUtil.addErrorMessage("Funcionalidad no soportada");
	}

	public void downloadDisk(ActionEvent event) throws ManagerBeanException {
        try {
    		FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
    		Renting renting = (Renting) getTo();

        	String fileName = "MOD115" + renting.getYear() + renting.getPeriod();
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
			throw new ManagerBeanException(e);
		}
	}
	
}
