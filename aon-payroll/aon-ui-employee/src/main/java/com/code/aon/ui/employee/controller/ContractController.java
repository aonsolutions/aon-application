package com.code.aon.ui.employee.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Enterprise;
import com.code.aon.company.EnterpriseCCC;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.employee.Contract;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;

public class ContractController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContractController.class.getName());

	private Enterprise enterprise;
	
	private List<SelectItem> workPlaces;
	
	private List<SelectItem> CCCs;
	
	private double totalPayment;
	private double totalDeduction;
	private double totalLiquid;
	
	public double getTotalPayment() {
		calculateTotalPayment();
		return totalPayment;
	}

	public void setTotalPayment(double totalPayment) {
		this.totalPayment = totalPayment;
	}

	public double getTotalDeduction() {
		calculateTotalDeduction();
		return totalDeduction;
	}

	public void setTotalDeduction(double totalDeduction) {
		this.totalDeduction = totalDeduction;
	}

	public double getTotalLiquid() {
		calculateTotalLiquid();
		return totalLiquid;
	}

	public void setTotalLiquid(double totalLiquid) {
		this.totalLiquid = totalLiquid;
	}
	  
	
	
	public ContractController() {
		this.workPlaces = new LinkedList<SelectItem>();
		this.CCCs = new LinkedList<SelectItem>();
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
		if ( isEnterpriseSelected() ) {
			try {
				this.CCCs = loadCCCs();
				this.workPlaces = loadWorkPlaces();
			} catch (ManagerBeanException e) {
				LOGGER.error( "Error in setEnterprise " + enterprise, e);
			}			
		} else {
			this.CCCs.clear();
			this.workPlaces.clear();
		}
	}
	
	public boolean isEnterpriseSelected() {
		return (enterprise != null) && (enterprise.getId() != null);
	}
	
	public List<SelectItem> getWorkPlaces() {
		return workPlaces;
	}

	public List<SelectItem> getCCCs() {
		return CCCs;
	}

	public void onEnterpriseChanged( LookupChangeEvent event ) {
		if ( event.getNewValue() != null ) {
			setEnterprise( (Enterprise) event.getNewValue() );
		} else {
			setEnterprise( new Enterprise() );
		}
	}
	
    public List<SelectItem> loadCCCs() throws ManagerBeanException {
    	LinkedList<SelectItem> cccs = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(EnterpriseCCC.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICompanyAlias.ENTERPRISE_CCC_ACTIVITY_ENTERPRISE_ID), enterprise.getId());
		criteria.addOrder(bean.getFieldName(ICompanyAlias.ENTERPRISE_CCC_CCC));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			EnterpriseCCC ccc = (EnterpriseCCC)to;
			cccs.add(new SelectItem(ccc, ccc.getCCC()));
		}
    	return cccs;
    }	  	

    public List<SelectItem> loadWorkPlaces() throws ManagerBeanException {
    	LinkedList<SelectItem> workPlaces = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICompanyAlias.WORK_PLACE_ENTERPRISE_ID), enterprise.getId());
		criteria.addOrder(bean.getFieldName(ICompanyAlias.WORK_PLACE_DESCRIPTION));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			WorkPlace workPlace = (WorkPlace) to;
			workPlaces.add(new SelectItem(workPlace, workPlace.getDescription()));
		}
    	return workPlaces;
    }	  	
    
    public void onDownloadContract( ActionEvent event ) {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		try {
			Contract c = (Contract)this.getModel().getRowData();
			byte[] buffer = c.getDocument();
			InputStream in = new ByteArrayInputStream(buffer);
			int bytes = in.read(buffer);
			while (bytes != -1) {
				response.getOutputStream().write(buffer, 0, bytes);
				bytes = in.read(buffer);
			}
			in.close();
			response.setContentType(MimeType.MIME_PDF.getName()); 
			response.flushBuffer();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		context.responseComplete();
	}
    
    private final String CONTRACT_PAYMENT_CONTROLLER = "contractPayment";
    private final String CONTRACT_DEDUCTION_CONTROLLER = "contractDeduction";
    
    private void calculateTotalPayment(){
//    	BasicController bean = (BasicController)AonUtil.getRegisteredBean(CONTRACT_PAYMENT_CONTROLLER);
//    	IController bean = FormUtil.getController(CONTRACT_PAYMENT_CONTROLLER);
//    	bean.getManagerBean().getList(criteria);
    	
    	
    	this.getTo();
    }
    private void calculateTotalDeduction(){
    	
    }
    private void calculateTotalLiquid(){
    	
    }
    
    public void onCalculateTotals(ActionEvent event){
    	try {
			loadTotals();
		} catch (ManagerBeanException e) {
			
		}
    }
    
    private void loadTotals() throws ManagerBeanException{
    	Criteria criteria = new Criteria();
    	
    	IController pBean = FormUtil.getController(CONTRACT_PAYMENT_CONTROLLER);
    	IController dBean = FormUtil.getController(CONTRACT_DEDUCTION_CONTROLLER);
    	pBean.getManagerBean().getList(criteria);
    	dBean.getManagerBean().getList(criteria);
    	
    	
    	
    }

}
