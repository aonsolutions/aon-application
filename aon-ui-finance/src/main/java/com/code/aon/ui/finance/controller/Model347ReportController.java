package com.code.aon.ui.finance.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD347.MOD347Format;
import com.code.aon.finance.enumeration.Model347ReportOrder;
import com.code.aon.finance.enumeration.Model347Type;
import com.code.aon.finance.model347.Model347;
import com.code.aon.finance.model347.Model347CollectionProvider;
import com.code.aon.finance.model347.Model347Parameters;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.finance.file.MOD347Writer;
import com.code.aon.ui.util.AonUtil;

public class Model347ReportController implements ICollectionProvider, IFinanceMessages {

	private List<SelectItem> mod347Formats;
	
	private Model347Parameters params;
	private List<Model347> summary;
	private DataModel model;
	private Integer year;
	private MOD347Format format;
	private Double totalKeyA;
	private Double totalKeyB;
	private FileOutput fileOutput;
	private List<Model347> summaryActive;
	
	
	public List<Model347> getSummaryActive() {
		setSummaryActive(null);
		if (summaryActive == null) {
			summaryActive = new LinkedList<Model347>();
			for (Model347 mod:summary) {
				if (!mod.isDisabled()){
					summaryActive.add(mod);
				}
			}
		}
		return summaryActive;
	}
	public void setSummaryActive(List<Model347> summaryActive) {
		this.summaryActive = summaryActive;
	}
	
	public List<Model347> getSummary() {
		return summary;
	}
	public void setSummary(List<Model347> summary) {
		this.summary = summary;
	}

	public Model347Parameters getParams() {
		if (params == null) {
			params = new Model347Parameters();
		}
		return params;
	}
	public void setParams(Model347Parameters params) {
		this.params = params;
	}

	public DataModel getModel() {
		if (model == null) {
			model = new ListDataModel( getSummary() );
		}
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}

	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}

	public MOD347Format getFormat() {
		return format;
	}
	public void setFormat(MOD347Format format) {
		this.format = format;
	}
	
	public Double getTotalKeyA() {
		if (totalKeyA == null) {
			initializeTotals();
		}
		return totalKeyA;
	}
	public void setTotalKeyA(Double totalKeyA) {
		this.totalKeyA = totalKeyA;
	}

	public Double getTotalKeyB() {
		if (totalKeyB == null) {
			initializeTotals();
		}
		return totalKeyB;
	}
	public void setTotalKeyB(Double totalKeyB) {
		this.totalKeyB = totalKeyB;
	}

	public FileOutput getFileOutput() {
		return fileOutput;
	}
	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
	}

	public void onReset(ActionEvent event) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		getParams().setDate(c.getTime());
		setYear(c.get(Calendar.YEAR));
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		getParams().setFromDate(c.getTime());
		c.set(Calendar.MONTH, 11);
		c.set(Calendar.DAY_OF_MONTH, 31);
		getParams().setToDate(c.getTime());
		getParams().setType(Model347Type.ALL);
		getParams().setOrder(Model347ReportOrder.INVOICE_REGISTRY_DOCUMENT);
		getParams().setSecurityLevel(null);
		getParams().setMinimunAmount(3000.0);
		setModel(null);
		setSummary(null);
		setTotalKeyA(null);
		setTotalKeyB(null);
		setFileOutput(null);
	}

	
	public List<Model347> search() {
		try {
			setFileOutput(null);
			Model347CollectionProvider provider = new Model347CollectionProvider();
			return provider.getList(getParams());
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public void onSearch(ActionEvent event) {
		setSummary(null);
		setTotalKeyA(null);
		setTotalKeyB(null);
		setModel(null);
		setSummary( search());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<?> getCollection(boolean arg0) throws ManagerBeanException {
		return (Collection) getModel().getWrappedData();
	}

	@Override
	public Collection<?> getCollection() {
		try {
			return getCollection(false);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	public void onYearChanged(ActionEvent event) {
		getParams().setFromDate(null);
		getParams().setToDate(null);
		if (getYear() != null) {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.MONTH, 0);
			c.set(Calendar.DAY_OF_MONTH, 1);
			c.set(Calendar.YEAR, getYear());
			getParams().setFromDate(c.getTime());
			c.set(Calendar.MONTH, 11);
			c.set(Calendar.DAY_OF_MONTH, 31);
			c.set(Calendar.YEAR, getYear());
			getParams().setToDate(c.getTime());
		}
	}

	public void onDisable(ActionEvent event) {
		Model347  to = (Model347) getModel().getRowData();
		to.setDisabled(!to.isDisabled());
		if (to.getType() == Model347Type.A_KEY) {
			setTotalKeyA( CommonUtil.round( getTotalKeyA() + (to.getTotal() * (to.isDisabled()?-1:1))));	
		}
		if (to.getType() == Model347Type.B_KEY) {
			setTotalKeyB( CommonUtil.round( getTotalKeyB() + (to.getTotal() * (to.isDisabled()?-1:1))));	
		}
	}
	
	private void initializeTotals() {
		setTotalKeyA(0.0);
		setTotalKeyB(0.0);
		for (Model347 m347: getSummary()) {
			if (m347.getType() == Model347Type.A_KEY) {
				setTotalKeyA( CommonUtil.round( getTotalKeyA() + m347.getTotal()));				
			}
			if (m347.getType() == Model347Type.B_KEY) {
				setTotalKeyB( CommonUtil.round( getTotalKeyB() + m347.getTotal()));				
			}
		}
	}
	
	public boolean isKeyAVisible() {
		return getParams().getType() != Model347Type.B_KEY;
	}
	public boolean isKeyBVisible() {
		return getParams().getType() != Model347Type.A_KEY;
	}

	public boolean isDiskOk() {
		int errors = 0;
		if (getFileOutput() != null) {
			errors = getFileOutput().getErrors().size();
		}
		return (errors==0);
	}

	public void onCreateDisk(ActionEvent event) throws ManagerBeanException {
		MOD347Writer mod347Writer = new MOD347Writer();
		setFileOutput( mod347Writer.createMOD347(getYear(),getFormat(),getSummary()) );
        if (getFileOutput() != null) {
        	if (getFileOutput().getErrors().size() > 0) {
        		AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_BATCH_DISK_ERROR);
        		for (Exception e : getFileOutput().getErrors()) {
        			AonUtil.addErrorMessage(e.getMessage());
        			e.printStackTrace();
        		}
            }
        }
	}
	
	public void downloadDisk(ActionEvent event) throws ManagerBeanException {
        try {
    		FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();

        	String fileName = "MOD347_" + getYear();
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
	
	public List<SelectItem> getMod347Formats() {
		if (mod347Formats == null) {
			mod347Formats = new LinkedList<SelectItem>();
			for (MOD347Format format:MOD347Format.values()) {
				String name = format.getDescription();
				SelectItem item = new SelectItem(format, name);
				mod347Formats.add(item);
			}
		}
		return mod347Formats;
	}
	
}
