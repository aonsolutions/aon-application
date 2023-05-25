package com.code.aon.ui.fiscal.controller.mod347;


import static com.code.aon.ui.common.ICommonMessages.FINANCE_BATCH_DISK_ERROR;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.fiscal.Mod347;
import com.code.aon.fiscal.enumeration.Mod347Status;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.mod347.Mod347Parameters;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.fiscal.aeat.AeatUtils;
import com.code.aon.ui.fiscal.controller.FiscalParametersController;
import com.code.aon.ui.fiscal.controller.IFiscalModelController;
import com.code.aon.ui.fiscal.file.MOD347Writer;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod347Controller extends BasicController implements IFiscalModelController{

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private FileOutput fileOutput;
	private FiscalParametersController fiscalParams;
	private Mod347Parameters params;
	
	public FiscalParametersController getFiscalParams() {
		if (fiscalParams == null) {
			fiscalParams = (FiscalParametersController) AonUtil.getRegisteredBean( FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
		}
		return fiscalParams;
	}

	public Mod347Parameters getParams() {
		return params;
	}
	public void setParams(Mod347Parameters params) {
		this.params = params;
	}


	public FileOutput getFileOutput() {
		return fileOutput;
	}

	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
	}

	public boolean isDiskOk() {
		int errors = 0;
		if (getFileOutput() != null) {
			errors = getFileOutput().getErrors().size();
		}
		return (errors == 0);
	}

	public void onCreateDisk(ActionEvent event) {
		MOD347Writer mod347Writer = new MOD347Writer();
		Mod347 mod347 = (Mod347) getTo();
		try {
			setFileOutput(mod347Writer.createMOD347(mod347));
		} catch (ManagerBeanException e) {
			String msg = "Error al generar el disco. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
		if (getFileOutput() != null) {
			if (getFileOutput().getErrors().size() > 0) {
				AonUtil.addErrorMessageFromBundle(FINANCE_BATCH_DISK_ERROR);
			}
		}
	}

	public void downloadDisk(ActionEvent event) throws ManagerBeanException {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = getAutomaticFileName();
			response.setCharacterEncoding("ISO-8859-1");
			response.setContentType(MimeType.MIME_TXT.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".txt\";");
			ServletOutputStream output = response.getOutputStream();
			InputStream input = getFileOutput().getFile() != null ? 
						new FileInputStream(getFileOutput().getFile()) : 
						new ByteArrayInputStream(getFileOutput().getContent());
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

	private String getAutomaticFileName() {
		Mod347 mod347 = (Mod347) getTo();
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		Company company = companyController.obtainCompany();		
		String s = company.getName();
	    StringBuilder sb = new StringBuilder();
	    if(!Character.isJavaIdentifierStart(s.charAt(0))) {
	        sb.append("_");
	    }
	    for (char c : s.toCharArray()) {
	        if(Character.isJavaIdentifierPart(c)) {
	            sb.append(c);
	        }
	    }		
		
		return  "Mod347_" + mod347.getYear() 
				+ "_" + sb.toString();
	}

	
	public void onFinish(ActionEvent event){
		Mod347 mod347 = (Mod347) getTo();
		mod347.setStatus(Mod347Status.FINISHED);
		accept(event);
	}
	
	
	public void onReopen(ActionEvent event){
		Mod347 mod347 = (Mod347) getTo();
		mod347.setStatus(Mod347Status.PENDING);
		accept(event);
	}
	
	public String aeatReport(){
		Mod347 mod347 = (Mod347) getTo();
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
			AeatUtils.printMod347(mod347.getYear(),					
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
		return null;
	}
	
	@Override
	public void accept(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Mod347.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			super.accept(event);
			
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {

			}
			throw new AbortProcessingException(e.getMessage(),e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	/**
	 * Método NECESARIO para el listado.
	 */
	public int getYear() {
		return ((Mod347) getTo()).getYear();
	}
	
	
	@Override
	public String editModel(Administration administration, int year,Period period) throws ManagerBeanException {
		onEditSearch(null);
		Criteria criteria = getCriteria();
		String yearAlias = getManagerBean().getFieldName(IEntityAlias.MOD347_YEAR); 
		String admonAlias = getManagerBean().getFieldName(IEntityAlias.MOD347_ADMINISTRATION);
		criteria.addEqualExpression(yearAlias,year);
		criteria.addEqualExpression(admonAlias,administration);
		setCriteria(criteria);
		onSearch(null);
		getModel().setRowIndex(0);
		onSelect(null);
		return "mod347_form";
	}

	@Override
	public String newModel(Administration administration, int year,
			Period period) throws ManagerBeanException{
		onReset(null);
		Mod347 fm = (Mod347) getTo();
		fm.setYear(year);
		fm.setAdministration(administration);
		return "mod347_form";
	}
	
	@Override
	public void printModel(Administration administration, int year,Period period) throws ManagerBeanException {
		onEditSearch(null);
		Criteria criteria = getCriteria();
		String yearAlias = getManagerBean().getFieldName(IEntityAlias.MOD347_YEAR); 
		String admonAlias = getManagerBean().getFieldName(IEntityAlias.MOD347_ADMINISTRATION);
		criteria.addEqualExpression(yearAlias,year);
		criteria.addEqualExpression(admonAlias,administration);
		setCriteria(criteria);
		onSearch(null);
		getModel().setRowIndex(0);
		onSelect(null);
	}
	
}
