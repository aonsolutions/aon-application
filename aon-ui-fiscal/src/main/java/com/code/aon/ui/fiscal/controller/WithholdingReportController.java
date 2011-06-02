package com.code.aon.ui.fiscal.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.company.Enterprise;
import com.code.aon.fiscal.ProfessionalRetention;
import com.code.aon.fiscal.dao.IFiscalAlias;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.WithholdingDetailKey;
import com.code.aon.fiscal.enumeration.WithholdingDetailSubkey;
import com.code.aon.fiscal.withholding.EnterpriseWithholding;
import com.code.aon.fiscal.withholding.WithholdingManager;
import com.code.aon.fiscal.withholding.WithholdingParameters;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.util.AonUtil;


public class WithholdingReportController {

	private FiscalParametersController fiscalParams;
	private WithholdingParameters params;
	private Enterprise companyEnterprise;
	private String beanName;
	private DataModel model;
	private Model111 model111;
	
	private static String ENTERPRISE_ALIAS;
	private static String DATE_ALIAS;
	private static String IN_KIND_ALIAS;
	private static String KEY_ALIAS;
	private static String SUBKEY_ALIAS;
	private static String DOCUMENT_ALIAS;
	private static String TAXABLE_BASE_ALIAS;
	private static String QUOTA_ALIAS;
	
	static {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ProfessionalRetention.class);
			ENTERPRISE_ALIAS = bean.getFieldName(IFiscalAlias.PROFESSIONAL_RETENTION_ENTERPRISE_ID);
			DATE_ALIAS = bean.getFieldName(IFiscalAlias.PROFESSIONAL_RETENTION_PAYMENT_DATE);
			IN_KIND_ALIAS = bean.getFieldName(IFiscalAlias.PROFESSIONAL_RETENTION_IN_KIND);
			KEY_ALIAS = bean.getFieldName(IFiscalAlias.PROFESSIONAL_RETENTION_KEY);
			SUBKEY_ALIAS = bean.getFieldName(IFiscalAlias.PROFESSIONAL_RETENTION_SUBKEY);
			DOCUMENT_ALIAS = bean.getFieldName(IFiscalAlias.PROFESSIONAL_RETENTION_DOCUMENT);
			TAXABLE_BASE_ALIAS = bean.getFieldName(IFiscalAlias.PROFESSIONAL_RETENTION_TAXABLE_BASE);
			QUOTA_ALIAS = bean.getFieldName(IFiscalAlias.PROFESSIONAL_RETENTION_QUOTA);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	public FiscalParametersController getFiscalParams() {
		if (fiscalParams == null) {
			fiscalParams = (FiscalParametersController) AonUtil.getRegisteredBean( FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
		}
		return fiscalParams;
	}
	
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	public DataModel getModel() {
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}
	
	public Model111 getModel111() {
		return model111;
	}
	public void setModel111(Model111 model111) {
		this.model111 = model111;
	}

	public WithholdingParameters getParams() {
		return params;
	}

	public void setParams(WithholdingParameters params) {
		this.params = params;
	}
	
	public void onReset(ActionEvent event) {
		try {
			setModel(null);
			initializeParams();
		} catch (ManagerBeanException e) {
			String msg = "No se pudieron inicializar los parámetros de búsqueda";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	public void onEditSearch(ActionEvent event) {
		setModel(null);
	}

	private void initializeParams() throws ManagerBeanException {
		setParams( new WithholdingParameters() );
		String defYear = getFiscalParams().getDefaultYear();
		if (StringUtils.isNotBlank(defYear)) {
			getParams().setYear( Integer.parseInt(defYear) );
			getParams().setDate(new Date());
			Period period = Period.getQuarterlyPeriod( CommonUtil.getMonth(getParams().getDate()));
			getParams().setFromDate(period.getStartDate(getParams().getYear()));
			getParams().setToDate(period.getStartDate(getParams().getYear()));
		}
		Boolean b = (Boolean) AonUtil.getBeanValue("fiscal","enterpriseEnabled");
		if (b == null || !b) {
			getParams().setEnterprise(getCompanyEnterprise());
		} else {
			getParams().setEnterprise((Enterprise)BeanManager.getManagerBean(Enterprise.class).createNewTo());
		}
	}
	
	private Enterprise getCompanyEnterprise() throws ManagerBeanException {
		if (companyEnterprise == null) {
			IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
			Iterator<ITransferObject> iter = companyBean.getList(null, 0, 1).iterator();
			if (iter.hasNext()) {
				Company company = ((Company) iter.next());
				IManagerBean enterpriseBean = BeanManager.getManagerBean(Enterprise.class);
				companyEnterprise = (Enterprise) enterpriseBean.get(company.getId());	
			}
		}
		return companyEnterprise;
	}
	
	public void onSearch(ActionEvent event) {
		try {
			WithholdingManager wm = new WithholdingManager();
			List<?> list = wm.getList(getParams());
			setModel(new ListDataModel(list));
		} catch (ManagerBeanException e) {
			String msg = "No se pudo realizar la consulta";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	public void onDetail(ActionEvent event) {
		AonUtil.addErrorMessage("La opción seleccionada aún no está disponible.");
	}
	
	public void onSelect(ActionEvent event) {
		try {
			EnterpriseWithholding ew = (EnterpriseWithholding) getModel().getRowData();
			IManagerBean enterpriseBean = BeanManager.getManagerBean(Enterprise.class);
			Enterprise enterprise = (Enterprise) enterpriseBean.get(ew.getEnterpriseId());
			WithholdingParameters parameters = new WithholdingParameters();
			parameters.setEnterprise(enterprise);
			parameters.setYear(getParams().getYear());
			parameters.setPeriod(getParams().getPeriod());
			setModel111(new Model111());
			getModel111().setEnterprise(enterprise);
			getModel111().setYear(getParams().getYear());
			getModel111().setPeriod(getParams().getPeriod());
			getModel111().setDetail10( getValues1(parameters,false) );
			
			getModel111().getDetail10().setCount( getModel111().getDetail10().getCount() + ew.getWorkCount() );
			getModel111().getDetail10().setAmount( getModel111().getDetail10().getAmount() + ew.getWorkTaxableBase() );
			getModel111().getDetail10().setQuota( getModel111().getDetail10().getQuota() + ew.getWorkQuota() );
			
			getModel111().setDetail11( getValues1(parameters,true) );
			getModel111().setDetail20( getValues2(parameters,false) );
			getModel111().setDetail21( getValues2(parameters,true) );
			getModel111().setDetail30( getValues3(parameters,false) );
			getModel111().setDetail31( getValues3(parameters,true) );
			getModel111().setDetail40( getValues4(parameters,false) );
			getModel111().setDetail41( getValues4(parameters,true) );
			getModel111().setDetail50( getValues5(parameters,false) );
			getModel111().setDetail51( getValues5(parameters,true) );
			System.out.println("----");
		} catch (ManagerBeanException e) {
			String msg = "No se pudo realizar la consulta";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} catch (ExpressionException e) {
			String msg = "No se pudo realizar la consulta";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	private ModelDetail getValues1(WithholdingParameters parameters, boolean inKind) throws ExpressionException, ManagerBeanException {
		Criteria criteria = new Criteria();
		addCommonExpression(parameters,criteria);
		criteria.addEqualExpression(IN_KIND_ALIAS, inKind);
		criteria.addExpression(KEY_ALIAS, "A|B|C|D|E|F");
		return getModelDetail(criteria);
	}

	private ModelDetail getValues2(WithholdingParameters parameters, boolean inKind) throws ExpressionException, ManagerBeanException {
		Criteria criteria = new Criteria();
		addCommonExpression(parameters,criteria);
		criteria.addEqualExpression(IN_KIND_ALIAS, inKind);
		criteria.addExpression(KEY_ALIAS, "G|H|I");
		return getModelDetail(criteria);
	}
	private ModelDetail getValues3(WithholdingParameters parameters, boolean inKind) throws ExpressionException, ManagerBeanException {
		Criteria criteria = new Criteria();
		addCommonExpression(parameters,criteria);
		criteria.addEqualExpression(IN_KIND_ALIAS, inKind);
		criteria.addEqualExpression(KEY_ALIAS, WithholdingDetailKey.K);
		criteria.addEqualExpression(SUBKEY_ALIAS, WithholdingDetailSubkey.K01);
		return getModelDetail(criteria);
	}
	private ModelDetail getValues4(WithholdingParameters parameters, boolean inKind) throws ExpressionException, ManagerBeanException {
		Criteria criteria = new Criteria();
		addCommonExpression(parameters,criteria);
		criteria.addEqualExpression(IN_KIND_ALIAS, inKind);
		criteria.addEqualExpression(KEY_ALIAS, WithholdingDetailKey.K);
		criteria.addEqualExpression(SUBKEY_ALIAS, WithholdingDetailSubkey.K02);
		return getModelDetail(criteria);
	}
	private ModelDetail getValues5(WithholdingParameters parameters, boolean inKind) throws ExpressionException, ManagerBeanException {
		Criteria criteria = new Criteria();
		addCommonExpression(parameters,criteria);
		criteria.addEqualExpression(IN_KIND_ALIAS, inKind);
		criteria.addEqualExpression(KEY_ALIAS, WithholdingDetailKey.J);
		return getModelDetail(criteria);
	}
	
	private void addCommonExpression(WithholdingParameters parameters, Criteria criteria) {
		criteria.addEqualExpression(ENTERPRISE_ALIAS, parameters.getEnterprise().getId());
		Date dateFrom = parameters.getPeriod().getStartDate(parameters.getYear());
		Date dateTo = parameters.getPeriod().getDueDate(parameters.getYear());
		criteria.addBetweenExpression(DATE_ALIAS, dateFrom, dateTo);
	}

	private ModelDetail getModelDetail(Criteria criteria) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProfessionalRetention.class);
		ProjectionList pl = new ProjectionList();
		pl.add(Projection.countDistinct(DOCUMENT_ALIAS));
		pl.add(Projection.sum(TAXABLE_BASE_ALIAS));
		pl.add(Projection.sum(QUOTA_ALIAS));
		List<?> list = bean.getList(pl, criteria);
		ModelDetail md = new ModelDetail();
		if (list != null && list.size() > 0) {
			Object[] arr = (Object[]) list.get(0);
			md.setCount((Integer) arr[0]);	
			md.setAmount(arr[1]!=null?(Double)arr[1]:0.0 );	
			md.setQuota(arr[2]!=null?(Double) arr[2]:0.0);	
		}
		return md;
	}

	public class ModelDetail {
		private double count; 
		private double amount; 
		private double quota;
		public double getCount() {
			return count;
		}
		public void setCount(double count) {
			this.count = count;
		}
		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}
		public double getQuota() {
			return quota;
		}
		public void setQuota(double quota) {
			this.quota = quota;
		}
	}
	
	public class Model111 {
		private Enterprise enterprise;  
		private Integer year;
		private Period period;
		private double total; 
		
		private ModelDetail detail10; 
		private ModelDetail detail11; 
		private ModelDetail detail20; 
		private ModelDetail detail21; 
		private ModelDetail detail30; 
		private ModelDetail detail31; 
		private ModelDetail detail40; 
		private ModelDetail detail41; 
		private ModelDetail detail50; 
		private ModelDetail detail51;

		public Enterprise getEnterprise() {
			return enterprise;
		}
		public void setEnterprise(Enterprise enterprise) {
			this.enterprise = enterprise;
		}
		
		public Integer getYear() {
			return year;
		}
		public void setYear(Integer year) {
			this.year = year;
		}
		
		public Period getPeriod() {
			return period;
		}
		public void setPeriod(Period period) {
			this.period = period;
		}
		
		public ModelDetail getDetail10() {
			return detail10;
		}
		public void setDetail10(ModelDetail detail10) {
			this.detail10 = detail10;
		}
		
		public ModelDetail getDetail11() {
			return detail11;
		}
		public void setDetail11(ModelDetail detail11) {
			this.detail11 = detail11;
		}
		
		public ModelDetail getDetail20() {
			return detail20;
		}
		public void setDetail20(ModelDetail detail20) {
			this.detail20 = detail20;
		}

		public ModelDetail getDetail21() {
			return detail21;
		}
		public void setDetail21(ModelDetail detail21) {
			this.detail21 = detail21;
		}

		public ModelDetail getDetail30() {
			return detail30;
		}
		public void setDetail30(ModelDetail detail30) {
			this.detail30 = detail30;
		}

		public ModelDetail getDetail31() {
			return detail31;
		}
		public void setDetail31(ModelDetail detail31) {
			this.detail31 = detail31;
		}

		public ModelDetail getDetail40() {
			return detail40;
		}
		public void setDetail40(ModelDetail detail40) {
			this.detail40 = detail40;
		}

		public ModelDetail getDetail41() {
			return detail41;
		}
		public void setDetail41(ModelDetail detail41) {
			this.detail41 = detail41;
		}
		
		public ModelDetail getDetail50() {
			return detail50;
		}
		public void setDetail50(ModelDetail detail50) {
			this.detail50 = detail50;
		}
		
		public ModelDetail getDetail51() {
			return detail51;
		}
		public void setDetail51(ModelDetail detail51) {
			this.detail51 = detail51;
		}
		
		public double getTotal() {
			return CommonUtil.round(
				getDetail10().getQuota() 	+ getDetail11().getQuota() 
				+getDetail20().getQuota()	+ getDetail21().getQuota()
				+getDetail30().getQuota()	+ getDetail31().getQuota()
				+getDetail40().getQuota() 	+ getDetail41().getQuota()
				+getDetail50().getQuota()	+ getDetail51().getQuota());
		}
		
	}
}
