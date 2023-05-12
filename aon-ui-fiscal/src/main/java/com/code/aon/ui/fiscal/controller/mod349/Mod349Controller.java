package com.code.aon.ui.fiscal.controller.mod349;


import static com.code.aon.ui.common.ICommonMessages.FINANCE_BATCH_DISK_ERROR;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.fiscal.Mod349;
import com.code.aon.fiscal.enumeration.Mod349Status;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.mod349.Mod349Parameters;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.fiscal.controller.FiscalParametersController;
import com.code.aon.ui.fiscal.controller.IFiscalModelController;
import com.code.aon.ui.fiscal.file.MOD349Writer;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod349Controller extends BasicController implements IFiscalModelController{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private FileOutput fileOutput;
	private FiscalParametersController fiscalParams;
	private Mod349Parameters params;
	
	public FiscalParametersController getFiscalParams() {
		if (fiscalParams == null) {
			fiscalParams = (FiscalParametersController) AonUtil.getRegisteredBean( FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
		}
		return fiscalParams;
	}

	public Mod349Parameters getParams() {
		return params;
	}
	public void setParams(Mod349Parameters params) {
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
		MOD349Writer mod349Writer = new MOD349Writer();
		Mod349 mod349 = (Mod349) getTo();
		try {
			setFileOutput(mod349Writer.createMOD349(mod349));
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
			Mod349 mod349 = (Mod349) getTo();
			String fileName = "MOD349_" + mod349.getYear();
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

	public void onFinish(ActionEvent event){
		Mod349 mod349 = (Mod349) getTo();
		mod349.setStatus(Mod349Status.FINISHED);
		accept(event);
	}
	public void onReopen(ActionEvent event){
		Mod349 mod349 = (Mod349) getTo();
		mod349.setStatus(Mod349Status.PENDING);
		accept(event);
	}
	
	@Override
	public void accept(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Mod349.class.getName());
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
			throw new AbortProcessingException(e.getMessage());
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
		return ((Mod349) getTo()).getYear();
	}

	@Override
	public String editModel(Administration administration, int year,Period period) throws ManagerBeanException {
		onEditSearch(null);
		Criteria criteria = getCriteria();
		String yearAlias = getManagerBean().getFieldName(IEntityAlias.MOD349_YEAR);
		String periodAlias = getManagerBean().getFieldName(IEntityAlias.MOD349_PERIOD);
		String admonAlias = getManagerBean().getFieldName(IEntityAlias.MOD349_ADMINISTRATION);
		criteria.addEqualExpression(yearAlias,year);
		criteria.addEqualExpression(periodAlias,period);
		criteria.addEqualExpression(admonAlias,administration);
		setCriteria(criteria);
		onSearch(null);
		getModel().setRowIndex(0);
		onSelect(null);
		return "mod349_form";
	}

	@Override
	public String newModel(Administration administration, int year,
			Period period) throws ManagerBeanException{
		onReset(null);
		Mod349 fm = (Mod349) getTo();
		fm.setYear(year);
		fm.setPeriod(period);
		fm.setAdministration(administration);
		return "mod349_form";
	}
	
	@Override
	public void printModel(Administration administration, int year,Period period) throws ManagerBeanException {
		onEditSearch(null);
		Criteria criteria = getCriteria();
		String yearAlias = getManagerBean().getFieldName(IEntityAlias.MOD349_YEAR);
		String periodAlias = getManagerBean().getFieldName(IEntityAlias.MOD349_PERIOD);
		String admonAlias = getManagerBean().getFieldName(IEntityAlias.MOD349_ADMINISTRATION);
		criteria.addEqualExpression(yearAlias,year);
		criteria.addEqualExpression(periodAlias,period);
		criteria.addEqualExpression(admonAlias,administration);
		setCriteria(criteria);
		onSearch(null);
		getModel().setRowIndex(0);
		onSelect(null);
	}
	
}
