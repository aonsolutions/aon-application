package com.code.aon.ui.employee.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
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
import com.code.aon.employee.ContractDeduction;
import com.code.aon.employee.ContractPayment;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.EnterpriseTree;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class ContractController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContractController.class.getName());
	
	private final String CONTRACT_PAYMENT_CONTROLLER = "contractPayment";
    private final String CONTRACT_DEDUCTION_CONTROLLER = "contractDeduction";

	private Enterprise enterprise;
	
	private List<SelectItem> workPlaces;
	
	private List<SelectItem> CCCs;
	
	private double totalPayment;
	private double totalDeduction;
	private double totalLiquid;
	
	public double getTotalPayment() {
		return totalPayment;
	}

	public void setTotalPayment(double totalPayment) {
		this.totalPayment = totalPayment;
	}

	public double getTotalDeduction() {
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
    
    private void calculateTotalPayment(List<ITransferObject> list){
    	Double total = 0.0;
    	for(ITransferObject to: list){
    		ContractPayment cp = (ContractPayment)to;
    		cp.getFunction();
    		total++;
    	}
    	setTotalPayment(total);
    }
    private void calculateTotalDeduction(List<ITransferObject> list){
    	Double total = 0.0;
    	for(ITransferObject to: list){
    		ContractDeduction cd = (ContractDeduction)to;
    		cd.getFunction();
    		total++;
    	}
    	setTotalDeduction(total);
    }
    private void calculateTotalLiquid(){
    	setTotalLiquid(getTotalPayment()-getTotalDeduction());
    }
    
    private void loadTotals() throws ManagerBeanException{
    	Expression expr1;
    	Expression expr2;
    	IController pBean = FormUtil.getController(CONTRACT_PAYMENT_CONTROLLER);
    	pBean.getCriteria().addLessThanOrEqualExpression(pBean.getFieldName(IEmployeeAlias.CONTRACT_PAYMENT_START_DATE), new Date()); 
    	expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(pBean.getFieldName(IEmployeeAlias.CONTRACT_PAYMENT_END_DATE), new Date());
    	expr2 = ExpressionUtilities.getNullExpression(pBean.getFieldName(IEmployeeAlias.CONTRACT_PAYMENT_END_DATE));
    	pBean.getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));	
    	pBean.initializeModel();
    	IController dBean = FormUtil.getController(CONTRACT_DEDUCTION_CONTROLLER);
    	dBean.getCriteria().addLessThanOrEqualExpression(dBean.getFieldName(IEmployeeAlias.CONTRACT_DEDUCTION_START_DATE), new Date()); 
    	expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(dBean.getFieldName(IEmployeeAlias.CONTRACT_DEDUCTION_END_DATE), new Date());
    	expr2 = ExpressionUtilities.getNullExpression(dBean.getFieldName(IEmployeeAlias.CONTRACT_DEDUCTION_END_DATE));
    	dBean.getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));	
    	dBean.initializeModel();
    	
    	calculateTotalPayment(pBean.getManagerBean().getList(pBean.getCriteria()));
    	calculateTotalDeduction(dBean.getManagerBean().getList(dBean.getCriteria()));
    	calculateTotalLiquid();
    }
    
    @Override
    public void onSelect(ActionEvent event) {
    	super.onSelect(event);
    	try {
			loadTotals();
		} catch (ManagerBeanException e) {
//			String msg = "No se han podido calcular los totales";
//			AonUtil.addWarningMessage(msg);
			LOGGER.error(e.getMessage(), e);
		}
    }

    public void onSalarySave(ActionEvent event) {
    	String msg = "Creacion de la nomina";
		AonUtil.addWarningMessage(msg);
    }
    
	public void onEdit( ActionEvent event ) {
		try {
			EnterpriseTree tree = (EnterpriseTree) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_TREE_CONTROLLER_NAME);
			select( event, tree.getContract() );
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onEdit exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}						
	}    
	
}
