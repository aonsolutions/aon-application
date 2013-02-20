package com.code.aon.ui.fiscal.controller.model;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Finance;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.fiscal.FiscalActivity;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.FiscalModelStatus;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.model.FiscalModelManagerFactory;
import com.code.aon.fiscal.model.IFiscalDeclaration;
import com.code.aon.fiscal.model.IFiscalModelManager;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.fiscal.controller.FiscalParametersController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public abstract class FiscalModelController extends BasicController {
	
	private IFiscalDeclaration declaration;
	private IFiscalModelManager manager;
	private FileOutput fileOutput;
	
	private boolean finalizePanelVisible;
	private RegistryBank registryBank;
	
	public IFiscalModelManager getFiscalModelManager() throws AonException {
		if (manager == null) {
			FiscalModelManagerFactory factory = new FiscalModelManagerFactory();
			manager = factory.getManager( getModelType() );
		}
		return manager;
	}
	
	private Registry getAdmonCreditor() {
		FiscalParametersController fpc = (FiscalParametersController) 
				AonUtil.getRegisteredBean( FiscalParametersController.FISCAL_PARAMS_BEAN_NAME );
		Creditor creditor = fpc.getAdmonCreditor();
		return (creditor==null?null:creditor.getRegistry());
	}
	
	public FileOutput getFileOutput() {
		return fileOutput;
	}
	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
	}

	public boolean isFinalizePanelVisible() {
		return finalizePanelVisible;
	}
	public void setFinalizePanelVisible(boolean finalizePanelVisible) {
		this.finalizePanelVisible = finalizePanelVisible;
	}
	public RegistryBank getRegistryBank() {
		return registryBank;
	}
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	public IFiscalDeclaration getDeclaration() {
		return declaration;
	}

	public void setDeclaration(IFiscalDeclaration fiscalModel) {
		this.declaration = fiscalModel;
	}

	public void initialize() throws AonException {
		FiscalModel to = (FiscalModel) getTo();
		FiscalParametersController fiscalParams = (FiscalParametersController) AonUtil.getRegisteredBean( FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
		String defYear = fiscalParams.getDefaultYear();
		to.setYear( defYear==null?null:Integer.parseInt(defYear) );
		Administration admon = fiscalParams.getDefaultAdministration();
		to.setAdministration( admon==null?null:admon);
		to.setModel(getModelType());
		setDeclaration( (getFiscalModelManager().initializeFiscalModel(to)) );
	}
	
	public void initializeDetails() throws AonException {
		setDeclaration( (getFiscalModelManager().initializeFiscalModelDetails(getDeclaration())));
		insertOrUpdateDetails();
	}
	
	public void load() throws AonException {
		FiscalModel to = (FiscalModel) getTo();
		setDeclaration( (getFiscalModelManager().loadFiscalModel(to)) );
	}
	
	public void unload() throws AonException {
		setDeclaration(null);
	}
	
	public void onShowFinalizePanel(ActionEvent event)  {
		FiscalModel to = (FiscalModel) getTo();
		if (mustCreateFinance(to)) {
			setFinalizePanelVisible(true);
			setRegistryBank(null);
			to.getFinance().setRegistry( getAdmonCreditor() ); 
		} else {
			finish(to, event);
		}
	}
	public void onHideFinalizePanel(ActionEvent event)  {
		setFinalizePanelVisible(false);
		setRegistryBank(null);
	}
	
	public void onFinish(ActionEvent event) {
		try {
			FiscalModel to = (FiscalModel) getTo();
			if (mustCreateFinance(to)) {
				createFinance(to);	
			}
			finish(to, event);
			onHideFinalizePanel(event);
		} catch (Exception e) {
			String msg = "No se pueden finalizar la declaración." + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	private void finish(FiscalModel to,ActionEvent event) {
		to.setStatus(FiscalModelStatus.FINISHED);
		accept(event);
	}
	
	public List<SelectItem> getActiveBanks() throws ManagerBeanException {
		CompanyCollectionsController c = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		return c.getActiveCompanyBanks();
	}
	
	private boolean mustCreateFinance(FiscalModel to) {
		if (getDeclaration().isWithoutActivityDeclarationAvailable() && to.isWithoutActivity()) {
			return false;
		}
		if (getDeclaration().isToDeductDeclarationAvailable() && getDeclaration().isToDeduct()) {
			return false;
		}
		if (getDeclaration().isDeclarationNegativeAvailable() && getDeclaration().isNegative()) {
			return false;
		}
		return true;
	}
	
	public boolean isBankAccountVisible() {
		FiscalModel to = (FiscalModel) getTo();
		Finance finance = to.getFinance();
		if (finance != null &&
			finance.getPayMethod() != null &&
			finance.getPayMethod().getType() != PayMethodType.CASH_BASIS) {
			return true;
		}
		return false;
	}
	
	private void createFinance(FiscalModel to) throws ManagerBeanException {
		Finance finance = to.getFinance();
		Registry registry = finance.getRegistry();
		if (registry == null || registry.getId() == null) {
			throw new ManagerBeanException("No se ha indicado un acreedor válido.");
		}
		finance.setRegistryDocument( registry.getDocument() );
		finance.setRegistryDocumentCountry( registry.getDocumentCountry() );
		finance.setRegistryDocumentType( registry.getDocumentType() );
		finance.setRegistryName(registry.getName());
		finance.setPayment(true);
		finance.setSecurityLevel( to.getSecurityLevel() );
		finance.setAmount( declaration.getResult() );
		if (finance.getPayMethod() == null) {
			throw new ManagerBeanException("No se ha indicado una forma de pago válida.");
		}
		if (finance.getPayMethod().getType() == PayMethodType.CASH_BASIS) {
			
		} else if (finance.getPayMethod().getType() == PayMethodType.NEGOTIABLE_DOCUMENT) {
			finance.setBank(getRegistryBank().getBank());
			finance.setBankAccount(getRegistryBank().getBankAccount());
		}
		IManagerBean bean = BeanManager.getManagerBean(Finance.class);
		bean.restoreNullSubPOJOs(finance);
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setConcept( to.getModel() + " " + to.getYear() + "-" + to.getPeriod() );
		finance = (Finance) bean.insert(finance);
		to.setFinance(finance);
	}

	public void onReopen(ActionEvent event) {
		try {
			FiscalModel to = (FiscalModel) getTo();
			Finance finance = to.getFinance();
			if (finance.getId() != null && finance.getFinanceStatus() != FinanceStatus.PENDING) {
				throw new ManagerBeanException("No se pueden reabrir la declaración. El vencimiento asociado no está pendiente");
			}
			to.setStatus(FiscalModelStatus.PENDING);
			to.setFinance(null);
			accept(event);
			if (finance.getId() != null) {
				IManagerBean bean = BeanManager.getManagerBean(Finance.class);
				bean.remove(finance);
			}
		} catch (Exception e) {
			String msg = "No se pueden reabrir la declaración." + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		
	}
	
	public void onRecalculate(ActionEvent event) {
		try {
			getDeclaration().calculate();
		} catch (AonException e) {
			String msg="No se pudo recalcular la declaración";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	public void insertOrUpdateDetails() throws AonException {
		getDeclaration().calculate();
		FiscalModel to = (FiscalModel) getTo();
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		for (FiscalModelDetail detail : getDeclaration().getDetails()) {
			detail.setFiscalModel(to);
			detail = (FiscalModelDetail) bean.insertOrUpdate(detail);
		}
	}

	public void removeDetails() throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		for (FiscalModelDetail detail : getDeclaration().getDetails()) {
			bean.remove(detail);
		}
	}
	
	public boolean isDiskOk() {
		int errors = 0;
		if (getFileOutput() != null) {
			errors = getFileOutput().getErrors().size();
		}
		return (errors==0);
	}

	public void downloadDisk(ActionEvent event) throws ManagerBeanException {
        try {
    		FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
        	String fileName = getFileName();
        	MimeType mimeType = getMimeType();
	        response.setContentType(mimeType.getName());
	        response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "." + mimeType.getExtension()+"\";");
	        ServletOutputStream output = response.getOutputStream();
	        InputStream input = getFileOutput().getFile() != null
	        		?new FileInputStream(getFileOutput().getFile())
	        		:new ByteArrayInputStream(getFileOutput().getContent());
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
	
	protected void checkFiscalActivity(FiscalModelType type) {
		try {
			int year = Calendar.getInstance().get(Calendar.YEAR);
			IManagerBean bean = BeanManager.getManagerBean(FiscalActivity.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_ACTIVITY_YEAR), year);
			int count = bean.getCount(criteria);
			if (count == 0) {
				AonUtil.addErrorMessage("No se ha realizado la introducción de los datos previos de actividades, necesarios para la confección del impuesto.");	
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
	}	
	
	protected abstract FiscalModelType getModelType();
	public abstract boolean isDifEnabled();
	public abstract String getFileName();
	public abstract MimeType getMimeType();
	public abstract void onCreateDisk(ActionEvent event);
	
	
}
