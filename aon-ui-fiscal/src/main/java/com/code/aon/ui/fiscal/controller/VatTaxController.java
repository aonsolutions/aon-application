package com.code.aon.ui.fiscal.controller;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.VatTaxDeclaration;
import com.code.aon.fiscal.VatTaxDetail;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.TaxColumn;
import com.code.aon.fiscal.enumeration.VatTaxKey;
import com.code.aon.fiscal.enumeration.VatTaxStatus;
import com.code.aon.fiscal.invoice.InvoiceReportParams;
import com.code.aon.fiscal.invoice.InvoiceReportParamsDetail;
import com.code.aon.fiscal.vat.tax.VatTaxManager;
import com.code.aon.fiscal.vat.tax.VatTaxParameters;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class VatTaxController extends BasicController implements IFiscalModelController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(VatTaxController.class.getName());
	public static final String PAY_TAB = "payTab";
	public static final String DETAIL_TAB = "detailTab";
	
	private DataModel vatTaxModel;
	private DataModel declaredModel;
	
	private VatTaxDetail newDetail;
	private boolean detailNew;

	private List<VatTaxDetail> summary;
	private VatTaxParameters params;
	private VatTaxManager provider;
	private String selectedTab;
	private FiscalParametersController fiscalParams;
	
	private boolean scoredInvoices;
	private boolean declaredPanelVisible;
	private boolean anyPreviousAdjust;
	private VatTaxDetail detail;
	
	public FiscalParametersController getFiscalParams() {
		if (fiscalParams == null) {
			fiscalParams = (FiscalParametersController) AonUtil.getRegisteredBean( FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
		}
		return fiscalParams;
	}

	public VatTaxManager getManager() {
		if (provider == null) {
			provider = new VatTaxManager(AonUtil.getDomainName());
		}
		return provider;
	}

	public DataModel getVatTaxModel() {
		if (vatTaxModel == null) {
			vatTaxModel = new SerializableListDataModel(getSummary());
		}
		return vatTaxModel;
	}
	public void setVatTaxModel(DataModel vatTaxModel) {
		this.vatTaxModel = vatTaxModel;
	}

	
	public DataModel getDeclaredModel() {
		return declaredModel;
	}
	public void setDeclaredModel(DataModel declaredModel) {
		this.declaredModel = declaredModel;
	}

	public VatTaxDetail getNewDetail() {
		return newDetail;
	}
	public void setNewDetail(VatTaxDetail newDetail) {
		this.newDetail = newDetail;
	}
	public boolean isLastPeriod() {
		VatTax vatTax = (VatTax) getTo();
		return (vatTax != null && (vatTax.getPeriod() == Period.M12 || vatTax.getPeriod() == Period.T4));
	}

	public boolean isDetailNew() {
		return detailNew;
	}
	public void setDetailNew(boolean detailNew) {
		this.detailNew = detailNew;
	}

	public VatTaxParameters getParams() {
		if (params == null) {
			setParams(new VatTaxParameters(AonUtil.getDomainName(),DomainManager.getCurrentDomain()));
		}
		params.setMod303AvailableByDifferenceDisabled( getFiscalParams().isMod303AvailableByDifferenceDisabled());
		return params;
	}
	public void setParams(VatTaxParameters params) {
		this.params = params;
	}

	public boolean isAnyPreviousAdjust() {
		return anyPreviousAdjust;
	}
	public void setAnyPreviousAdjust(boolean anyPreviousAdjust) {
		this.anyPreviousAdjust = anyPreviousAdjust;
	}

	public boolean isDeclaredPanelVisible() {
		return declaredPanelVisible;
	}
	public void setDeclaredPanelVisible(boolean declaredPanelVisible) {
		this.declaredPanelVisible = declaredPanelVisible;
	}

	public boolean isScoredInvoices() {
		return scoredInvoices;
	}
	public void setScoredInvoices(boolean scoredInvoices) {
		this.scoredInvoices = scoredInvoices;
	}
	
	public VatTaxDetail getDetail() {
		return detail;
	}
	public void setDetail(VatTaxDetail detail) {
		this.detail = detail;
	}

	public List<VatTaxDetail> getSummary() {
		if (summary == null) {
			summary = new LinkedList<VatTaxDetail>();
		}
		return summary;
	}
	public void setSummary(List<VatTaxDetail> summary) {
		this.summary = summary;
	}

	public String getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	public void initializeVatTax(boolean isNevv) throws ManagerBeanException {
		setAnyPreviousAdjust(false);
		VatTax vatTax = (VatTax) getTo();
		getParams().setVatTax( vatTax );
		getParams().setYear( vatTax.getYear() );
		getParams().setPeriod( vatTax.getPeriod() );
		getParams().setInvoiceStatus( isScoredInvoices()?InvoiceStatus.SCORED: null);
		if (isNevv) {
			setSummary( getManager().getVatTax(params));
			calculateTax();
			saveVatTax();
		} else {
			setSummary(getManager().getDetailList(getParams()));
		}
		setVatTaxModel(new SerializableListDataModel(getSummary()));
	}

	public void onChangePeriod(ActionEvent event) {
		try {
			if (isNevv()) {
				VatTax vatTax = (VatTax) getTo();
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(getManagerBean().getFieldName(IEntityAlias.VAT_TAX_YEAR), vatTax.getYear());
				criteria.addLessThanExpression(getManagerBean().getFieldName(IEntityAlias.VAT_TAX_PERIOD), vatTax.getPeriod());
				criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.VAT_TAX_PERIOD), false);
				System.out.println(criteria);
				List<ITransferObject> list = getManagerBean().getList(criteria);
				if (list != null && list.size() > 0) {
					VatTax previous = (VatTax) list.get(0);
					vatTax.setProrata(previous.getProrata());
				}
			}
		} catch (ManagerBeanException e) {
			// NAda. No se inicializa la prorrata.
		}
	}

	private void calculateTax() {
		for (VatTaxDetail detail:getSummary()) {
			detail.calculate();
		}
	} 
	
	public void onRecalculate(ActionEvent event ) {
		recalculate();
		accept(event);
	}
	
	private void recalculate() {
		getManager().initializeTotals(getSummary());
		getManager().calculate(getSummary());
		calculateTax();
	}
	
	public void onFinish(ActionEvent event ) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			recalculate();
			VatTax vatTax = (VatTax) getTo();
			vatTax.setStatus(VatTaxStatus.FINISHED);
			accept(event);
			vatTax = (VatTax) HibernateUtil.getSession(sessionName).merge(vatTax);
			VatTaxDeclarationController vtdc = (VatTaxDeclarationController) FormUtil.getController(VatTaxDeclarationController.BEAN_NAME);
			vtdc.tryAutomaticCreation(vatTax);

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			String msg = "No se pudo generar la declaración. " + e.getMessage();
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	public void onReopen(ActionEvent event ) {
		try {
			VatTaxDeclarationController vtdc = (VatTaxDeclarationController) FormUtil.getController(VatTaxDeclarationController.BEAN_NAME);
			if (vtdc.getModel().getRowCount() > 0 ) {
				String msg = "La declaración tiene resultados grabados. No se puede reabrir.";
				LOGGER.error(msg);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			VatTax vatTax = (VatTax) getTo();
			vatTax.setStatus(VatTaxStatus.PENDING);
			accept(event);
		} catch (ManagerBeanException e) {
			String msg = "No se pueden reabrir la declaración." + e.getMessage();
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onRecalculateLine(ActionEvent event ) {
		recalculate();		
	}
	
	public List<TaxColumn> getColumns() {
		return Arrays.asList( TaxColumn.values() );
	}

	public void saveVatTax() throws ManagerBeanException{
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			if (check()) {
				save();	
			}
			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			String msg = "No se pudo generar la declaración. " + e.getMessage();
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new ManagerBeanException(msg);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	private boolean check() {
		double td = 0.0;
		double tcd = 0.0;
		for (VatTaxDetail detail : getSummary()) {
			if (detail.getKey() == VatTaxKey.FT) {
				td = detail.getQuota(); 
			}
			if (detail.getKey() == VatTaxKey.TD) {
				tcd = detail.getQuota(); 
			}
		}
		if (td != tcd) {
			AonUtil.addErrorMessage("La declaración se ha grabado pero \"Total a Deducir\" y \"Total cuota deducible\" no tienen el mismo valor.");
//			return false;
		}
		return true;
	}

	private void save() throws ManagerBeanException{
		recalculate();;
		IManagerBean bean = BeanManager.getManagerBean(VatTaxDetail.class);
		VatTax vatTax = (VatTax) getTo();
		for (VatTaxDetail detail : getSummary()) {
			if (detail.getId() == null) {
				detail.setVatTax(vatTax);
				bean.insert(detail);	
			} else {
				bean.update(detail);
			}
		}
	}

	public void refreshPreviousAdjustFlag() {
		try {
			VatTax vatTax = (VatTax) getTo();
			IManagerBean bean = BeanManager.getManagerBean(VatTaxDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_VAT_TAX_YEAR), vatTax.getYear());
			criteria.addLessThanExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_VAT_TAX_PERIOD), vatTax.getPeriod());
			Expression e1 = ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_TAXABLE_BASE_ADJUST), 0.0);
			Expression e2 = ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_DEDUCTIBLE_QUOTA_ADJUST), 0.0);
			Expression e3 = ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_QUOTA_ADJUST), 0.0);
			Expression e4 = ExpressionUtilities.getOrExpression(e1, e2);
			criteria.addExpression(ExpressionUtilities.getOrExpression(e3, e4));
			int count = bean.getCount(criteria);
			setAnyPreviousAdjust(count>0);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo inicializar el marcador de ajustes en periodos anteriores. " + e.getMessage();
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onAdjustCopy(ActionEvent event) {
		try {
			
			VatTax vatTax = (VatTax) getTo();
			IManagerBean bean = BeanManager.getManagerBean(VatTaxDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_VAT_TAX_YEAR), vatTax.getYear());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_VAT_TAX_PERIOD), getPreviousPeriod());
			List<ITransferObject> list = bean.getList(criteria);
			for (ITransferObject to: list) {
				VatTaxDetail detail = (VatTaxDetail) to;
				if (detail.getDeductibleQuotaAdjust() != 0.0 || detail.getQuotaAdjust() != 0.0 || detail.getTaxableBaseAdjust() != 0.0) {
					
					VatTaxKey key = detail.getKey();
					Double percent = detail.getPercent();
					for (VatTaxDetail d : getSummary() ) {
						if (d.getKey() == key && ObjectUtils.equals(percent, d.getPercent())) {
							d.setQuotaAdjust( detail.getQuotaAdjust() );
							d.setTaxableBaseAdjust( detail.getTaxableBaseAdjust() );			
							d.setDeductibleQuotaAdjust( detail.getDeductibleQuotaAdjust());
							break;
						}
					}
				}
			}
			onRecalculate(event);
		} catch (ManagerBeanException e) {
			String msg = "No se pudieron copiar los ajustes del periodo anterior. " + e.getMessage();
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public Period getPreviousPeriod() {
		VatTax vatTax = (VatTax) getTo();
		if (vatTax != null && vatTax.getPeriod() != null && vatTax.getPeriod().ordinal() > 0) {
			return Period.values()[vatTax.getPeriod().ordinal() - 1];
		}
		return null;
	}
	
	public void onHideDeclared(ActionEvent event) {
		setDeclaredPanelVisible(false);
	}

	public void onShowDeclared(ActionEvent event) {
		try {
			setDeclaredPanelVisible(true);
			VatTaxDetail detail = (VatTaxDetail) getVatTaxModel().getRowData();
			setDetail(detail);
			VatTaxManager manager = getManager();
			setDeclaredModel( new SerializableListDataModel( manager.getPeriodDeclaredDetails( detail ) ) );
		} catch (ManagerBeanException e) {
			String msg = "No se pudo mostrar el desglose de lo declarado. " + e.getMessage();
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public String onListAccummulated() {
		VatTaxDetail detail = (VatTaxDetail) getVatTaxModel().getRowData();
		InvoiceReportController irc = (InvoiceReportController) AonUtil.getRegisteredBean("invoiceReport");
		InvoiceReportParams params = getInvoiceReportParams(detail);
		params.setFromInvoiceDate(null);
		params.setToInvoiceDate(null);
		params.setTaxType( TaxType.VAT );
		irc.setParams(params);
		return irc.onExcelReport();
	}

	public void onChangeTaxableBaseAdjust(ActionEvent event) {
		VatTaxDetail detail = (VatTaxDetail) getVatTaxModel().getRowData();
		detail.setQuota(CommonUtil.round(detail.getTaxableBase() * detail.getPercent() / 100));
		detail.setTaxableBaseAdjust( CommonUtil.round(detail.getTaxableBase() - detail.getTaxableBaseResult()));
		detail.setQuotaAdjust(CommonUtil.round(detail.getQuota() - detail.getQuotaResult()));
		recalculate();
	}
	
	public void onChangeQuotaAdjust(ActionEvent event) {
		VatTaxDetail detail = (VatTaxDetail) getVatTaxModel().getRowData();
		detail.setQuotaAdjust(CommonUtil.round(detail.getQuota() - detail.getQuotaResult()));
		if (detail.getKey().isTaxableBaseVisible() && detail.getTaxableBase() == 0 && detail.getPercent() != 0) {
			detail.setTaxableBase(CommonUtil.round(detail.getQuota() *  100 / detail.getPercent()));
			detail.setTaxableBaseAdjust( CommonUtil.round(detail.getTaxableBase() - detail.getTaxableBaseResult()));
		}
		recalculate();
	}
	
	public void onChangeNewTaxableBase(ActionEvent event) {
		VatTaxDetail detail = getNewDetail();
		if ( detail.getQuota() == 0 ) {
			detail.setQuota(CommonUtil.round(detail.getTaxableBase() * detail.getPercent() / 100)); 
		}
	}
	public void onChangeNewQuota(ActionEvent event) {
		VatTaxDetail detail = getNewDetail();
		if (detail.getKey().isTaxableBaseVisible() && detail.getTaxableBase() == 0 && detail.getPercent() != 0) {
			detail.setTaxableBase(CommonUtil.round(detail.getQuota() *  100 / detail.getPercent()));
		}
	}

	@SuppressWarnings("unchecked")
	public List<VatTaxDeclaration> getDeclarations() {
		VatTax vatTax = (VatTax) getTo();
		if  (vatTax != null) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(VatTaxDeclaration.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_ID), vatTax.getId());
				criteria.addOrder(bean.getFieldName(IEntityAlias.VAT_TAX_DECLARATION_PERCENT),false);
				List<?> list = bean.getList(criteria);
				return (List<VatTaxDeclaration>) list;
			} catch (ManagerBeanException e) {
			}
		}
		return null;
		
	}
	
	private InvoiceReportParams getInvoiceReportParams(VatTaxDetail detail) {
		InvoiceReportParams params = new InvoiceReportParams(DomainManager.getCurrentDomain());
		params.reset();
		int year = detail.getVatTax().getYear();
		params.setFromTaxDate(CommonUtil.getYearFirstDay(year));
		params.setToTaxDate(detail.getVatTax().getPeriod().getDueDate(year));
		
		VatTaxKey key = detail.getKey();
		if (key == VatTaxKey.A1 ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.NATIONAL);
			pd.setType(InvoiceType.SALES);
			pd.setRectificationTypeSpecial(false);
			pd.setPercent(detail.getPercent());
			params.addDetail(pd);
		}
		
		if (key == VatTaxKey.A2 ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.NATIONAL);
			pd.setType(InvoiceType.SALES);
			pd.setRectificationTypeSpecial(false);
			pd.setPercent(detail.getPercent());
			pd.setSurcharge(true);
			params.addDetail(pd);
		}
		
		if (key == VatTaxKey.A3 ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.INTRACOMMUNITY);
			pd.setType(InvoiceType.PURCHASE);
			pd.setPercent(detail.getPercent());
			params.addDetail(pd);
		}
		
		if (key == VatTaxKey.A4 ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.CAN_CEU_MEL);
			pd.setType(InvoiceType.EXPENSES);
			params.addDetail(pd);

			pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.OTHER_ISP);
			pd.setType(InvoiceType.EXPENSES);
			params.addDetail(pd);

			pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.EXTRACOMMUNITY);
			pd.setType(InvoiceType.EXPENSES);
			params.addDetail(pd);

			pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.INTRACOMMUNITY);
			pd.setType(InvoiceType.EXPENSES);
			params.addDetail(pd);

			pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.OTHER_ISP);
			pd.setType(InvoiceType.PURCHASE);
			pd.setPercent(detail.getPercent());
			params.addDetail(pd);			
		}
		
		if (key == VatTaxKey.A5 ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.NATIONAL);
			pd.setType(InvoiceType.SALES);
			pd.setRectificationTypeSpecial(true);
			params.addDetail(pd);
		}
		
		if (key == VatTaxKey.B1 ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.NATIONAL);
			pd.setType(InvoiceType.PURCHASE);
			pd.setInvestment(false);
			params.addDetail(pd);
			
			pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.OTHER_ISP);
			pd.setType(InvoiceType.PURCHASE);
			pd.setInvestment(false);
			params.addDetail(pd);
		}
		
		if (key == VatTaxKey.B2 ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.NATIONAL);
			pd.setType(InvoiceType.PURCHASE);
			pd.setInvestment(true);
			params.addDetail(pd);
			
			pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.OTHER_ISP);
			pd.setType(InvoiceType.PURCHASE);
			pd.setInvestment(true);
			params.addDetail(pd);

			pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.NATIONAL);
			pd.setType(InvoiceType.EXPENSES);
			pd.setInvestment(true);
			params.addDetail(pd);

			pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.CAN_CEU_MEL);
			pd.setType(InvoiceType.EXPENSES);
			pd.setInvestment(true);
			params.addDetail(pd);

			pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.OTHER_ISP);
			pd.setType(InvoiceType.EXPENSES);
			pd.setInvestment(true);
			params.addDetail(pd);
		}
		
		if (key == VatTaxKey.B3 ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setType(InvoiceType.EXPENSES);
			pd.setInvestment(false);
			params.addDetail(pd);
		}
		
		if (key == VatTaxKey.C1 ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.EXTRACOMMUNITY);
			pd.setType(InvoiceType.PURCHASE);
			pd.setInvestment(false);
			params.addDetail(pd);
			
			pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.CAN_CEU_MEL);
			pd.setType(InvoiceType.PURCHASE);
			pd.setInvestment(false);
			params.addDetail(pd);
		}
		
		if (key == VatTaxKey.C2 ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.EXTRACOMMUNITY);
			pd.setType(InvoiceType.PURCHASE);
			pd.setInvestment(true);
			params.addDetail(pd);
			
			pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.CAN_CEU_MEL);
			pd.setType(InvoiceType.PURCHASE);
			pd.setInvestment(true);
			params.addDetail(pd);
		}
		
		if (key == VatTaxKey.D1 ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.INTRACOMMUNITY);
			pd.setType(InvoiceType.PURCHASE);
			pd.setInvestment(false);
			params.addDetail(pd);
		}
		
		if (key == VatTaxKey.D2 ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.INTRACOMMUNITY);
			pd.setType(InvoiceType.PURCHASE);
			pd.setInvestment(true);
			params.addDetail(pd);
		}
		
		if (key == VatTaxKey.CP ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setType(InvoiceType.PURCHASE);
			pd.setInvestment(false);
			pd.setPercent(detail.getPercent());
			params.addDetail(pd);
		}
		if (key == VatTaxKey.GT ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setType(InvoiceType.EXPENSES);
			pd.setTransaction(InvoiceTransactionType.NATIONAL);
			pd.setInvestment(false);
			pd.setPercent(detail.getPercent());
			params.addDetail(pd);
			
			pd = new InvoiceReportParamsDetail();
			pd.setType(InvoiceType.EXPENSES);
			pd.setTransaction(InvoiceTransactionType.INTRACOMMUNITY);
			pd.setInvestment(false);
			pd.setPercent(detail.getPercent());
			params.addDetail(pd);
			
			pd = new InvoiceReportParamsDetail();
			pd.setType(InvoiceType.EXPENSES);
			pd.setTransaction(InvoiceTransactionType.EXTRACOMMUNITY);
			pd.setInvestment(false);
			pd.setPercent(detail.getPercent());
			params.addDetail(pd);
			
			pd = new InvoiceReportParamsDetail();
			pd.setType(InvoiceType.EXPENSES);
			pd.setTransaction(InvoiceTransactionType.CAN_CEU_MEL);
			pd.setInvestment(false);
			pd.setPercent(detail.getPercent());
			params.addDetail(pd);
			
			pd = new InvoiceReportParamsDetail();
			pd.setType(InvoiceType.EXPENSES);
			pd.setTransaction(InvoiceTransactionType.OTHER_ISP);
			pd.setInvestment(false);
			pd.setPercent(detail.getPercent());
			params.addDetail(pd);
		}
		if (key == VatTaxKey.BI ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setType(InvoiceType.PURCHASE);
			pd.setInvestment(true);
			params.addDetail(pd);
			
			pd = new InvoiceReportParamsDetail();
			pd.setType(InvoiceType.EXPENSES);
			pd.setInvestment(true);
			params.addDetail(pd);
		}
		
		if (key == VatTaxKey.EI ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.INTRACOMMUNITY);
			pd.setType(InvoiceType.SALES);
			pd.setService(false);
			params.addDetail(pd);
		}
		if (key == VatTaxKey.EX1 ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.EXTRACOMMUNITY);
			pd.setType(InvoiceType.SALES);
			pd.setService(false);
			params.addDetail(pd);
		}
		
		if (key == VatTaxKey.EX2 ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.CAN_CEU_MEL);
			pd.setType(InvoiceType.SALES);
			pd.setService(false);
			params.addDetail(pd);
		}
		
		if (key == VatTaxKey.OO ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setType(InvoiceType.SALES);
			pd.setService(true);
			pd.setVatDeductionTypeWithoutRight(false);
			pd.setTransaction(InvoiceTransactionType.EXTRACOMMUNITY);
			params.addDetail(pd);
			
			pd = new InvoiceReportParamsDetail();
			pd.setType(InvoiceType.SALES);
			pd.setService(true);
			pd.setVatDeductionTypeWithoutRight(false);
			pd.setTransaction(InvoiceTransactionType.CAN_CEU_MEL);
			params.addDetail(pd);			
		}
		
		if (key == VatTaxKey.OS ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setType(InvoiceType.SALES);
			pd.setService(true);
			pd.setVatDeductionTypeWithoutRight(true);
			params.addDetail(pd);
		}
		
		if (key == VatTaxKey.OI ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd.setTransaction(InvoiceTransactionType.OTHER_ISP);
			pd.setType(InvoiceType.SALES);
			pd.setService(false);
			params.addDetail(pd);
			
			pd = new InvoiceReportParamsDetail();
			pd.setType(InvoiceType.SALES);
			pd.setService(true);
			pd.setVatDeductionTypeWithoutRight(false);
			pd.setTransaction(InvoiceTransactionType.OTHER_ISP);
			params.addDetail(pd);			
		}
		
		if (key == VatTaxKey.PS ) {
			InvoiceReportParamsDetail pd = new InvoiceReportParamsDetail();
			pd = new InvoiceReportParamsDetail();
			pd.setType(InvoiceType.SALES);
			pd.setService(true);
			pd.setVatDeductionTypeWithoutRight(false);
			pd.setTransaction(InvoiceTransactionType.INTRACOMMUNITY);
			params.addDetail(pd);			
		}
		return params;
	}
	
	public void onRemoveDetail(ActionEvent event) {
		try {
			VatTaxDetail detail = (VatTaxDetail) vatTaxModel.getRowData();
			IManagerBean bean = BeanManager.getManagerBean(VatTaxDetail.class);
			bean.remove(detail);
			initializeVatTax(false);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}
	
	public void onNewDetail(ActionEvent event) {
		VatTaxDetail detail = new VatTaxDetail();
		setNewDetail(detail);
		setDetailNew(true);
	}
	public void onCancelNewDetail(ActionEvent event) {
		setNewDetail(null);
		setDetailNew(false);
	}
	public void onSaveNewDetail(ActionEvent event) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(VatTaxDetail.class);
			VatTax tax = (VatTax) getTo();
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_VAT_TAX_ID), tax.getId());
			c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_KEY), getNewDetail().getKey());
			c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_PERCENT), getNewDetail().getPercent());	
			List<ITransferObject> list = bean.getList(c);
			if (list != null && list.size() > 0) {
				String msg = "Ya existe una línea en la declaración para el tipo y porcentaje indicados.";
				LOGGER.error(msg);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			getNewDetail().setVatTax(tax);
			getNewDetail().reverseCalculate();
			bean.insert(getNewDetail());
			initializeVatTax(false);
			setDetailNew(false);
			setNewDetail(null);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo generar grabar la línea. " + e.getMessage();
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		
	}
	
	public List<SelectItem> getAvailableKeys() {
		List<SelectItem> keys = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		for (VatTaxKey key:VatTaxKey.values()) {
			@SuppressWarnings("unchecked")
			List<VatTaxDetail> declaredList = (List<VatTaxDetail>) getVatTaxModel().getWrappedData();
			boolean found = false;
			for (VatTaxDetail declared:declaredList) {
				if (declared.getKey() == key && !key.isPercentVisible()) {
					found = true;
				}
			}
			if (!found) {
				if ((key.isQuotaVisible() ||  key.isTaxableBaseVisible()) && !key.isSubtotal() && !key.isTotal() ) {
					String name = key.getName(locale);
					SelectItem item = new SelectItem(key, name);
					keys.add(item);
				}
			}
		}
		return keys;
	}
	
	@Override
	public String editModel(Administration administration, int year,Period period) throws ManagerBeanException {
		onEditSearch(null);
		Criteria criteria = getCriteria();
		String yearAlias = getManagerBean().getFieldName(IEntityAlias.VAT_TAX_YEAR); 
		String periodAlias = getManagerBean().getFieldName(IEntityAlias.VAT_TAX_PERIOD);
		criteria.addEqualExpression(yearAlias,year);
		criteria.addEqualExpression(periodAlias,period);
		setCriteria(criteria);
		onSearch(null);
		getModel().setRowIndex(0);
		onSelect(null);
		return "vatTax_form";
	}

	@Override
	public String newModel(Administration administration, int year,
			Period period) throws ManagerBeanException{
		onReset(null);
		VatTax fm = (VatTax) getTo();
		fm.setYear(year);
		fm.setPeriod(period);
		return "vatTax_form";
	}
	
	@Override
	public void printModel(Administration administration, int year,Period period) throws ManagerBeanException {
		onEditSearch(null);
		Criteria criteria = getCriteria();
		String yearAlias = getManagerBean().getFieldName(IEntityAlias.VAT_TAX_YEAR); 
		String periodAlias = getManagerBean().getFieldName(IEntityAlias.VAT_TAX_PERIOD);
		criteria.addEqualExpression(yearAlias,year);
		criteria.addEqualExpression(periodAlias,period);
		setCriteria(criteria);
		onSearch(null);
		getModel().setRowIndex(0);
		onSelect(null);
	}
	
}
