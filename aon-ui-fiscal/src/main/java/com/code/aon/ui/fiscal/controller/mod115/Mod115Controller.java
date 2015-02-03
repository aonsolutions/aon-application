package com.code.aon.ui.fiscal.controller.mod115;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_BATCH_DISK_ERROR;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.tax.model.MOD115.MOD115Format;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.ui.fiscal.aeat.AeatUtils;
import com.code.aon.ui.fiscal.controller.model.FiscalModelController;
import com.code.aon.ui.fiscal.file.MOD115Writer;
import com.code.aon.ui.util.AonUtil;

public class Mod115Controller extends FiscalModelController  {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	protected FiscalModelType getModelType() {
		return 	FiscalModelType.M115;
	}

	@Override
	public boolean isDifEnabled() {
		return true;
	}
	
	public void onCreateDisk(ActionEvent event) {
		try { 
			MOD115Writer mod115Writer = new MOD115Writer();
			FiscalModel fm = (FiscalModel) getTo();
			List<FiscalModel> list = new LinkedList<FiscalModel>();
			list.add(fm);
			MOD115Format format = MOD115Format.getFormat(fm.getAdministration(), fm.getYear());
			setFileOutput( mod115Writer.createMOD115(list, format) );
		    if (getFileOutput() != null) {
		    	if (getFileOutput().getErrors().size() > 0) {
		    		AonUtil.addErrorMessageFromBundle(FINANCE_BATCH_DISK_ERROR);
		        }
		    }
		    if (isAeatValidable()) {
		    	validateAeatFile();	
		    }
		} catch (IllegalArgumentException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		}
	}
	
	@Override
	public MimeType getMimeType() {
		FiscalModel fm = (FiscalModel) getTo();
		MOD115Format format = MOD115Format.getFormat(fm.getAdministration(), fm.getYear());
		return format.getMimeType();
	}

	@Override
	protected String getFormPage() {
		return "mod115_form";
	}
	
	protected String validateAeatFile() {
		FiscalModel fiscalModel = (FiscalModel) getTo();
		if (fiscalModel.getYear() > 2014) {
			return null;
		} 
		return super.validateAeatFile();
	}
	
	public String aeatReport() {
		FiscalModel fiscalModel = (FiscalModel) getTo();
		if (fiscalModel.getYear() > 2014) {
			try {
				if (getFileOutput() == null) {
					onCreateDisk(null);
				}
				InputStream input = getFileOutput().getFile() != null
						?new FileInputStream(getFileOutput().getFile())
						:new ByteArrayInputStream(getFileOutput().getContent());

				FacesContext faces = FacesContext.getCurrentInstance();
	            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
	            String fileName = getAutomaticFileName();
	            response.setHeader("Content-disposition", "attachment; filename=\""+fileName+"\";");
				AeatUtils.printMod115(getDeclaration().getHeader().getYear(),
						getDeclaration().getHeader().getPeriod(),
						input,response.getOutputStream());					
		        response.flushBuffer();
		        faces.responseComplete();
			} catch (FileNotFoundException e) {
				AonUtil.addErrorMessage(e.getMessage()); 
				throw new AbortProcessingException(e.getMessage(),e);
			} catch (UnsupportedEncodingException e) {
				AonUtil.addErrorMessage(e.getMessage()); 
				throw new AbortProcessingException(e.getMessage(),e);
			} catch (IOException e) {
				AonUtil.addErrorMessage(e.getMessage()); 
				throw new AbortProcessingException(e.getMessage(),e);
			} catch (AonException e) {
				AonUtil.addErrorMessage(e.getMessage()); 
				throw new AbortProcessingException(e.getMessage(),e);
			}
		} 
		return super.aeatReport();
	}
	
}
