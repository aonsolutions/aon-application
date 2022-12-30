package com.code.aon.ui.finance.controller;

import java.io.Serializable;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.ProgressionState;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.Series;
import com.code.aon.config.util.SeriesUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.InvoicingParameters;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.common.LongProcessThread;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.finance.util.DeliveryInvoicingProcess;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;

public class DeliveryInvoicingController implements IFinanceConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private InvoicingParameters invoicingParams;

	private Integer[] invoiceIds;
	
	private ProgressionState progressionState;

	public InvoicingParameters getParams() {
		return invoicingParams;
	}

	public void setParams(InvoicingParameters invoicingParams) {
		this.invoicingParams = invoicingParams;
	}

	private SaleInvoiceController getSaleInvoiceController() {
		return (SaleInvoiceController) AonUtil.getRegisteredBean(IFinanceConstants.SALE_INVOICE_CONTROLLER_NAME);
	}			
	
	public void onInitialize(ActionEvent event) throws ManagerBeanException {
		InvoicingParameters params = new InvoicingParameters();
		params.initializeParams();
		SaleInvoiceController controller = getSaleInvoiceController();
		controller.onCancel(event);
		Series series = SeriesUtil.getSeries(controller.initSeries(false));
		params.setInvoiceSeries(series);
		params.setConfidential(isSeriesConfidential(series));
		params.setInvoiceNumber(0);
		params.setInvoiceDate(new Date());
		params.setInvoiceRecordable(AonUtil.getRoleManager().isAccountingOperator());
		setParams(params);
		setProgressionState(new ProgressionState());
	}

	public void onInvoiceSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		Series series = (Series) event.getNewValue();
		if ( getSaleInvoiceController().isNumberEditable() ) {
			updateInvoiceNumber(series);
		}
		getParams().setConfidential(isSeriesConfidential(series));
	}
	
	public void onInvoiceNumberEditable(ActionEvent event) throws ManagerBeanException {
		updateInvoiceNumber(getParams().getInvoiceSeries());		
	}				

	private void updateInvoiceNumber(Series series) throws ManagerBeanException {
		boolean tbai = isTbai();
	    if(tbai) {
	        	String domainName = AonUtil.getDomainName();
				Integer domainId = DomainManager.getCurrentDomain();
				Integer number = AON.getInvoiceMinNumber(domainName, domainId, "", com.esferalia.aon.occam.api.model.type.InvoiceType.SALES, series.getCode());
				getParams().setInvoiceNumber(number);
	    } else getParams().setInvoiceNumber(obtainMaxNumber(series));
	}
	
	private int obtainMaxNumber(Series series) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		if ((series == null) ||StringUtils.isBlank(series.getCode()) ) {
			criteria.addNullExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERIES));
		} else {
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERIES), series.getCode());
		}
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE), InvoiceType.SALES);
		Projection projection = Projection.max(invoiceBean.getFieldName(IEntityAlias.INVOICE_NUMBER));
		Object value = invoiceBean.getUniqueResult(projection, criteria);
		if (value != null) {
			return ((Integer) value).intValue() + 1;
		}
		return 1;
	}

	public void updateSeries() throws ManagerBeanException {
		Series series = getParams().getInvoiceSeries();
        if ( (series!=null) && StringUtils.isBlank(series.getCode()) ) {
        	series = null; 
        	getParams().setInvoiceSeries(null);
        }
        if ( getParams().getInvoiceNumber() == 0 ) {
        	boolean tbai = isTbai();
    	    if(tbai) {
    	        	String domainName = AonUtil.getDomainName();
    				Integer domainId = DomainManager.getCurrentDomain();
    				Integer number = AON.getInvoiceMinNumber(domainName, domainId, "", com.esferalia.aon.occam.api.model.type.InvoiceType.SALES, series.getCode());
    				getParams().setInvoiceNumber(number);
    	    } else getParams().setInvoiceNumber(obtainMaxNumber(series));
        }
	}	
	
	private boolean isSeriesConfidential(Series series) {
		if (series != null) {
			return (series.getSecurityLevel() == SecurityLevel.CONFIDENTIAL);
		}
		return false;
	}

	public void onInvoice(ActionEvent event) {
		getProgressionState().start();
		DeliveryInvoicingProcess dip = new DeliveryInvoicingProcess(this);
		LongProcessThread thread = new LongProcessThread(dip); 
		thread.start();		
	}

	public String invoiceAction() {
		return (getInvoiceIds() != null) ? IFinanceConstants.SALE_INVOICE_LIST_NAME : null;
	}
	
	private void loadInvoices( ActionEvent event ) {
		if ( getInvoiceIds() != null ) {
			try {
				IController controller = FormUtil.getController(SALE_INVOICE_CONTROLLER_NAME);
				Criteria criteria = new Criteria();
				criteria.addBetweenExpression(controller.getFieldName(IEntityAlias.INVOICE_ID), getInvoiceIds()[0], getInvoiceIds()[1]);
				controller.onEditSearch(event);
				controller.setCriteria(criteria);
				controller.onSearch(event);
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(),e);
			}				
		}		
	}
	
	public void onShowPanel(ActionEvent event) {
		getProgressionState().start(false);		
	}
	
	public void onClosePanel(ActionEvent event) {
		if ( getProgressionState().isFinish() ) {
			loadInvoices(event);
		}
		getProgressionState().finish();
		getParams().setInvoiceNumber(0);
	}

	public ProgressionState getProgressionState() {
		return progressionState;
	}

	public void setProgressionState(ProgressionState progressionState) {
		this.progressionState = progressionState;
	}

	public Integer[] getInvoiceIds() {
		return invoiceIds;
	}

	public void setInvoiceIds(Integer[] invoiceIds) {
		this.invoiceIds = invoiceIds;
	}	
	
	public boolean isTbai() {
		return getTbaiConfiguration().isActive();
	}
	
	public TbaiConfiguration getTbaiConfiguration() {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		return AON.getTbaiConfiguration(domainName, domainId, login);
	}

	
}