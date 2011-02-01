package com.code.aon.ui.fiscal.controller;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.VatTaxDetail;
import com.code.aon.fiscal.enumeration.VatTaxColumn;
import com.code.aon.fiscal.enumeration.VatTaxKey;
import com.code.aon.fiscal.enumeration.VatTaxStatus;
import com.code.aon.fiscal.vat.tax.VatTaxCollectionProvider;
import com.code.aon.fiscal.vat.tax.VatTaxParameters;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class VatTaxController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(VatTaxController.class.getName());
	public static final String PAY_TAB = "payTab";
	public static final String DETAIL_TAB = "detailTab";
	
	private DataModel vatTaxModel;
	private List<VatTaxDetail> summary;
	private VatTaxParameters params;
	private  VatTaxCollectionProvider provider;
	private String selectedTab;
	private FiscalParametersController fiscalParams;

	public FiscalParametersController getFiscalParams() {
		if (fiscalParams == null) {
			fiscalParams = (FiscalParametersController) AonUtil.getRegisteredBean( FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
		}
		return fiscalParams;
	}

	public VatTaxCollectionProvider getProvider() {
		if (provider == null) {
			provider = new VatTaxCollectionProvider();
		}
		return provider;
	}

	public DataModel getVatTaxModel() {
		if (vatTaxModel == null) {
			vatTaxModel = new ListDataModel(getSummary());
		}
		return vatTaxModel;
	}
	public void setVatTaxModel(DataModel vatTaxModel) {
		this.vatTaxModel = vatTaxModel;
	}

	public VatTaxParameters getParams() {
		return params;
	}
	public void setParams(VatTaxParameters params) {
		this.params = params;
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

	public void initializeVatTax(boolean isNew) throws ManagerBeanException {
		VatTax vatTax = (VatTax) getTo();
		setParams(new VatTaxParameters()); 
		getParams().setVatTax( vatTax );
		getParams().setYear( vatTax.getYear() );
		getParams().setPeriod( vatTax.getPeriod() );
		if (isNew) {
			setSummary(getProvider().getVatTax(getParams()));
			getProvider().fillDeclared(getParams(),getSummary());
			calculateTax();
			saveVatTax(); //TODO OJO ¡Se inicia otra transaccion! PROBAR
		} else {
			setSummary(getProvider().getDetailList(getParams()));
		}
		setVatTaxModel(new ListDataModel(getSummary()));
	}

	private void calculateTax() {
		for (VatTaxDetail detail:getSummary()) {
			detail.calculate();
		}
	}
	
	public void onRecalculate(ActionEvent event ) {
		getProvider().initializeTotals(getSummary());
		getProvider().calculate(getSummary());
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
			
			onRecalculate(event);
			VatTax vatTax = (VatTax) getTo();
			vatTax.setStatus(VatTaxStatus.FINISHED);
			accept(event);
			vatTax = (VatTax) HibernateUtil.getSession(sessionName).merge(vatTax);
			VatTaxDeclarationController vtdc = (VatTaxDeclarationController) FormUtil.getController(VatTaxDeclarationController.BEAN_NAME);
			vtdc.tryAutomaticCreation();

			
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
		onRecalculate(event);		
	}
	
	public List<VatTaxColumn> getColumns() {
		return Arrays.asList( VatTaxColumn.values() );
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
			AonUtil.addErrorMessage("\"Total a Deducir\" y \"Total cuota deducible\" deben tener el mismo valor.");
			return false;
		}
		return true;
	}

	private void save() throws ManagerBeanException{
		onRecalculate(null);
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

}
