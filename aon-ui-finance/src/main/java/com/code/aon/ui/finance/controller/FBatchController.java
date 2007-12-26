package com.code.aon.ui.finance.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.account.AccountEntry;
import com.code.aon.account.AccountEntryDetail;
import com.code.aon.account.bridge.AccountEntryFinanceBatch;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.writer.AccountEntryFinanceWriter;
import com.code.aon.account.bridge.writer.FinanceRecordingTo;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.account.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.csb.CSBOutput;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.PayMethodType;
import com.code.aon.finance.invoicing.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.finance.csb.CSB19Writer;
import com.code.aon.ui.finance.csb.CSB32Writer;
import com.code.aon.ui.finance.csb.CSB58Writer;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.report.OutputFormat;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the fbatch maintenance.
 * 
 */
public class FBatchController extends BasicController implements ICollectionProvider {

	private static final Logger LOGGER = Logger.getLogger(FBatchController.class.getName());

	private static final String FINANCE_CONTROLLER_NAME = "finance";

	private static final String FINANCE_BATCH_DETAIL_CONTROLLER_NAME = "fBatchDetail";

	private CSBOutput csbOutput;

	/** Determines if the fbatch is a payment or a charge. */
	private Boolean payment;

	public CSBOutput getCsbOutput() {
		return csbOutput;
	}

	public void setCsbOutput(CSBOutput csbOutput) {
		this.csbOutput = csbOutput;
	}

	/**
	 * Gets if the fbatch is a payment or a charge.
	 * 
	 * @return true if the fbatch is a payment
	 */
	public Boolean getPayment() {
		return payment;
	}

	/**
	 * Sets if the fbatch is a payment or a charge.
	 * 
	 * @param payment true if the fbatch is a payment
	 */
	public void setPayment(Boolean payment) {
		this.payment = payment;
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		setPayment(null);
		super.onEditSearch(event);
	}

    public boolean isTodo() {
        return FinanceBatchStatus.TODO.equals(((FinanceBatch)this.getTo()).getFinanceBatchStatus());
    }

    public boolean isDone() {
        return FinanceBatchStatus.DONE.equals(((FinanceBatch)this.getTo()).getFinanceBatchStatus());
    }

    public boolean isRecorded() {
        return FinanceBatchStatus.RECORDED.equals(((FinanceBatch)this.getTo()).getFinanceBatchStatus());
    }

    public boolean isDiskMode() {
        return !FinanceBatchType.NONE.equals(((FinanceBatch)this.getTo()).getFinanceBatchType());
    }

    @SuppressWarnings("unchecked")
    public void onRBankChanged(ValueChangeEvent event) {
    	if(event.getNewValue() != null){
    		try {
				IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(rBankBean.getFieldName(IFinanceAlias.REGISTRY_BANK_ID), event.getNewValue());
				Iterator iter =rBankBean.getList(criteria, 0, 1).iterator();
				if(iter.hasNext()){
					((FinanceBatch)this.getTo()).setRegistryBank((RegistryBank)iter.next());
				}
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error obtaining bank info", e);
				AonUtil.addErrorMessage("Error obtaining bank info");
				throw new AbortProcessingException(e);
			}
    	}
    }
    
	/**
	 * Adds to criteria the generic equal expression.
	 * 
	 * @param event the event that contains the new value
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 * @throws ExpressionException the expression exception
	 */
	public void addEqualExpression(ValueChangeEvent event) throws ManagerBeanException, ExpressionException {
	    if (event.getNewValue() != null && !event.getNewValue().equals(new Integer(Integer.MAX_VALUE))) {
	    	Criteria criteria = getCriteria();
			Object value = event.getNewValue();
            criteria.addExpression(getFieldName(event.getComponent().getId()), value.toString());
			setCriteria(criteria);
		}
	}
	
	public void addRBankExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			Criteria criteria = getCriteria();
			criteria.addEqualExpression(getFieldName(IFinanceAlias.FINANCE_BATCH_REGISTRY_BANK_ID), event.getNewValue());
			setCriteria(criteria);
		}
	}
	
	/**
	 * Adds to criteria the invoice issueDate. Greater than or equal
	 * 
	 * @param event the event that contains the new value
	 * @throws ManagerBeanException
	 */
	public void addIssueDate1Expression(ValueChangeEvent event) throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria criteria = getCriteria();
			Object value = event.getNewValue();
            criteria.addGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.FINANCE_BATCH_ISSUE_DATE), value);
			setCriteria(criteria);
		}
	}
	
	/**
	 * Adds to criteria the invoice issueDate. Less than or equal
	 * 
	 * @param event the event that contains the new value
	 * @throws ManagerBeanException
	 */
	public void addIssueDate2Expression(ValueChangeEvent event) throws ManagerBeanException {
	    if (event.getNewValue() != null) {
			Object value = event.getNewValue();
			Criteria criteria = getCriteria();
            criteria.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.FINANCE_BATCH_ISSUE_DATE), value);
			setCriteria(criteria);
		}
	}

    public void loadAvailableFinances(boolean payment) {
        try {
            FinanceController controller = (FinanceController)AonUtil.getController(FINANCE_CONTROLLER_NAME);
            FinanceBatch to = (FinanceBatch)this.getTo();

            Criteria criteria = new Criteria();
            criteria.addEqualExpression(controller.getFieldName(IFinanceAlias.FINANCE_PAYMENT), new Boolean(payment));
            Expression pendingExpr = 
                ExpressionUtilities.getEqualExpression(controller.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
            Expression returnedExpr = 
                ExpressionUtilities.getEqualExpression(controller.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.RETURNED);
            criteria.addExpression(ExpressionUtilities.getOrExpression(pendingExpr, returnedExpr));
            if (!to.getFinanceBatchType().equals(FinanceBatchType.NONE)) {
                criteria.addEqualExpression(controller.getFieldName(IFinanceAlias.FINANCE_PAY_METHOD_TYPE), PayMethodType.NEGOTIABLE_DOCUMENT);
                if (!to.getFinanceBatchType().equals(FinanceBatchType.CSB_58)) {
                	criteria.addNotNullExpression(controller.getFieldName(IFinanceAlias.FINANCE_BANK_ACCOUNT));
                	criteria.addExpression(ExpressionUtilities.getNotEqualExpression(controller.getFieldName(IFinanceAlias.FINANCE_BANK_ACCOUNT), ""));
                    if (!to.getFinanceBatchType().equals(FinanceBatchType.CSB_32)) {
                        criteria.addLessThanOrEqualExpression(controller.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), to.getIssueDate());
                    }
                }
            }
            criteria.addOrder(controller.getFieldName(IFinanceAlias.FINANCE_INVOICE_SERIES));
            criteria.addOrder(controller.getFieldName(IFinanceAlias.FINANCE_INVOICE_NUMBER));
            criteria.addOrder(controller.getFieldName(IFinanceAlias.FINANCE_DUE_DATE));

            controller.setCriteria(criteria);
            controller.onSearch(null);
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error reloading Finance model", e);
        }
    }

    public void loadDetails(FinanceBatch fbatch) {
        try {
            LinesController fBatchDetailController = (LinesController)AonUtil.getController(FINANCE_BATCH_DETAIL_CONTROLLER_NAME);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(fBatchDetailController.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_BATCH_ID), fbatch.getId());
            criteria.addOrder(fBatchDetailController.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_INVOICE_SERIES));
            criteria.addOrder(fBatchDetailController.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_INVOICE_NUMBER));
            criteria.addOrder(fBatchDetailController.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_DUE_DATE));

            fBatchDetailController.setCriteria(criteria);
            fBatchDetailController.onSearch(null);
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error loading FinanceBatchDetail model", e);
        }
    }

    @SuppressWarnings({"unused","unchecked"})
	public void onBatchSelected(ActionEvent event) {
        FinanceBatch fBatch = (FinanceBatch)getTo();
        if (!FinanceBatchStatus.TODO.equals(fBatch.getFinanceBatchStatus())) {
            try {
                fBatch.setFinanceBatchStatus(FinanceBatchStatus.TODO);
                getManagerBean().update(fBatch);
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error updating FinanceBatch with id=" + fBatch.getId(), e);
            }
        }

        String bundleName = AonUtil.getConfigurationController().getApplicationBundles().get("financeBundle");
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName, FacesContext.getCurrentInstance().getViewRoot().getLocale());
        String trackingDescription = bundle.getString("aon_finance_tracking_batched") + " " + fBatch.getId() + " - " + fBatch.getDescription();

        FinanceController financeController = (FinanceController)AonUtil.getController(FINANCE_CONTROLLER_NAME);
        try {
            IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			IManagerBean financeBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);

            Iterator iterator = financeController.getCheckedFinances().iterator();
            while (iterator.hasNext()) {
				Finance finance = (Finance)iterator.next();
                finance.setFinanceStatus(FinanceStatus.BATCHED);
                financeBean.update(finance);

                FinanceBatchDetail fBatchDetail = new FinanceBatchDetail();
				fBatchDetail.setFinance(finance);
				fBatchDetail.setFinanceBatch(fBatch);
                fBatchDetail.setAmount(finance.getTotalAmount());
                fBatchDetail.setStatus(FinanceStatus.BATCHED);
				financeBatchDetailBean.insert(fBatchDetail);

                FinanceTrackingWriter.addFinanceTracking(finance, FinanceTrackingType.BATCHED, trackingDescription);
            }
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error adding selected finances to the FinanceBatch with id=" + fBatch.getId(), e);
		}

        financeController.clearCheckedFinances();
        loadDetails(fBatch);
        loadAvailableFinances(fBatch.isPayment());
	}

	@SuppressWarnings({"unused","unchecked"})
	public void onRemoveSelected(ActionEvent event) {
        FinanceBatch fBatch = (FinanceBatch)getTo();
        if (!FinanceBatchStatus.TODO.equals(fBatch.getFinanceBatchStatus())) {
            try {
                fBatch.setFinanceBatchStatus(FinanceBatchStatus.TODO);
                getManagerBean().update(fBatch);
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error updating FinanceBatch with id=" + fBatch.getId(), e);
            }
        }

        FBatchDetailController fBatchDetailController = (FBatchDetailController)AonUtil.getController(FINANCE_BATCH_DETAIL_CONTROLLER_NAME);
        try {
			IManagerBean financeBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);

			Iterator iterator = fBatchDetailController.getCheckedFinanceBatchDetails().iterator();
	        while(iterator.hasNext()){
	        	FinanceBatchDetail fBatchDetail = (FinanceBatchDetail)iterator.next();
	        	financeBatchDetailBean.remove(fBatchDetail);

	        	updateRelatedInfo(fBatchDetail);
	        }
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error removing selected finances to the FinanceBatch with id=" + fBatch.getId(), e);
		}

		fBatchDetailController.clearCheckedFinanceBatchDetails();
        loadDetails(fBatch);
		loadAvailableFinances(fBatch.isPayment());
    }

	public void updateRelatedInfo(FinanceBatchDetail fBatchDetail) {
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			FinanceStatus financeStatus = (wasFinanceReturned(fBatchDetail.getFinance()))?FinanceStatus.RETURNED:FinanceStatus.PENDING;
			fBatchDetail.getFinance().setFinanceStatus(financeStatus);
			financeBean.update(fBatchDetail.getFinance());
			FinanceTrackingWriter.removeLastTrackingByType(fBatchDetail.getFinance(), FinanceTrackingType.BATCHED);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error updating finances in FinanceBatch with id="+ fBatchDetail.getFinanceBatch().getId(), e);
		}
	}

	public boolean wasFinanceReturned(Finance finance) {
		try {
			IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_FINANCE_ID),finance.getId());
			criteria.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_TYPE),FinanceTrackingType.RETURNED);
			return (trackingBean.getCount(criteria) > 0);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error obtaining FinanceTracking of Finance with id="+ finance.getId(), e);
		}
		return false;
	}

    @SuppressWarnings("unused")
	public void onCreateDisk(ActionEvent event) throws ManagerBeanException {
		FinanceBatch fbatch = (FinanceBatch)this.getTo();

        Company company = obtainCompany();
        if (fbatch.getFinanceBatchType().equals(FinanceBatchType.CSB_19_D) || fbatch.getFinanceBatchType().equals(FinanceBatchType.CSB_19)) {
			CSB19Writer csb19Writer = new CSB19Writer();
			csbOutput = csb19Writer.createCSB19(company, fbatch);
		}
		else if (fbatch.getFinanceBatchType().equals(FinanceBatchType.CSB_32)) {
			CSB32Writer csb32Writer = new CSB32Writer();
			csbOutput = csb32Writer.createCSB32(company, fbatch);
		}
		else if (fbatch.getFinanceBatchType().equals(FinanceBatchType.CSB_58)) {
			CSB58Writer csb58Writer = new CSB58Writer();
			csbOutput = csb58Writer.createCSB58(company, fbatch);
		}

        if (csbOutput != null) {
        	if (csbOutput.getErrors().size() > 0) {
                String bundleName = AonUtil.getConfigurationController().getApplicationBundles().get("financeBundle");
        		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, FacesContext.getCurrentInstance().getViewRoot().getLocale());
        		AonUtil.addErrorMessage(bundle.getString("aon_finance_batch_disk_error"));
        	} else {
                fbatch.setFinanceBatchStatus(FinanceBatchStatus.DONE);
                getManagerBean().update(fbatch);
        	}
        }
	}

	@SuppressWarnings("unchecked")
    private Company obtainCompany() throws ManagerBeanException {
        IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
        Iterator iter = companyBean.getList(null).iterator();
        if (iter.hasNext()) {
            return (Company)iter.next();
        }
        return null;
    }

	@SuppressWarnings({"unused"})
	public boolean isDiskOk() throws ManagerBeanException {
		int errors = 0;
		if (csbOutput != null) {
			errors = csbOutput.getErrors().size();
		}
		return (errors==0);
	}

	@SuppressWarnings({"unused"})
	public void downloadDisk(ActionEvent event) throws ManagerBeanException {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
	        HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();

	        OutputStream out = response.getOutputStream();
	        InputStream input = new FileInputStream(csbOutput.getFile());
	        int BUFFER = 2048;
	        byte data[] = new byte[BUFFER];
	        int count;
	        while ((count = input.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
	        out.close();
	        input.close();

	        String fileName = ((FinanceBatch)this.getTo()).getFinanceBatchType().getName(AonUtil.getCurrentLocale());
	        fileName += "-" + ((FinanceBatch)this.getTo()).getDescription();
	        fileName = ((csbOutput.getErrors().size()>0)?"ERROR-":"") + fileName;

	        response.setContentType(MimeType.MIME_TXT.getName());
	        response.setContentLength(data.length);
	        response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
	        faces.responseComplete();
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}

	@SuppressWarnings({"unused","unchecked"})
    public void onRecord(ActionEvent event) throws ManagerBeanException {
        FinanceBatch fbatch = (FinanceBatch)this.getTo();
        fbatch.setRegistryBank(fbatch.getRegistryBank().getId() == null?null:fbatch.getRegistryBank());
        
        List<Finance> financeList = new LinkedList<Finance>();
        Iterator iterator = fbatch.getDetailList().iterator();
        while (iterator.hasNext()) {
            FinanceBatchDetail fbatchDetail = (FinanceBatchDetail)iterator.next();
            financeList.add(fbatchDetail.getFinance());
        }

        FinanceRecordingTo recordingTo = new FinanceRecordingTo();
        recordingTo.setDate(fbatch.getIssueDate());
        recordingTo.setType(fbatch.isPayment() ? AccountEntryType.PAYMENT : AccountEntryType.COLLECTION);
        recordingTo.setRegistryBank(fbatch.getRegistryBank());
        recordingTo.setFinanceList(financeList);

        AccountEntryFinanceWriter accountEntryWriter = new AccountEntryFinanceWriter();
        AccountEntry entry = accountEntryWriter.recordFinances(recordingTo);

        IManagerBean accountEntryFbatchBean = BeanManager.getManagerBean(AccountEntryFinanceBatch.class);
        AccountEntryFinanceBatch accountEntryFinanceBatch = new AccountEntryFinanceBatch();
        accountEntryFinanceBatch.setFinanceBatch(fbatch);
        accountEntryFinanceBatch.setAccountEntry(entry);
        accountEntryFbatchBean.insert(accountEntryFinanceBatch);

        String bundleName = AonUtil.getConfigurationController().getApplicationBundles().get("financeBundle");
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName, FacesContext.getCurrentInstance().getViewRoot().getLocale());
        String trackingDescription = bundle.getString("aon_finance_tracking_recorded") + " " + entry.getId();

        IManagerBean fbatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
        IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
        IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
        iterator = fbatch.getDetailList().iterator();
        while (iterator.hasNext()) {
            FinanceBatchDetail fbatchDetail = (FinanceBatchDetail)iterator.next();
            fbatchDetail.setStatus(FinanceStatus.PAID);
            fbatchDetailBean.update(fbatchDetail);

            fbatchDetail.getFinance().setFinanceStatus(FinanceStatus.PAID);
            financeBean.update(fbatchDetail.getFinance());

            if (!fbatchDetail.getFinance().getInvoice().getStatus().equals(InvoiceStatus.SCORED)) {
                fbatchDetail.getFinance().getInvoice().setStatus(InvoiceStatus.PAID);
                invoiceBean.update(fbatchDetail.getFinance().getInvoice());
            }

            FinanceTrackingWriter.addFinanceTracking(fbatchDetail.getFinance(), FinanceTrackingType.RECORDED, trackingDescription);
        }

        fbatch.setFinanceBatchStatus(FinanceBatchStatus.RECORDED);
        getManagerBean().update(fbatch);
        loadDetails(fbatch);
        fbatch.setRegistryBank(fbatch.getRegistryBank() == null?new RegistryBank():fbatch.getRegistryBank());
    }

	@SuppressWarnings({"unused","unchecked"})
    public void onUnrecord(ActionEvent event) throws ManagerBeanException {
        FinanceBatch fbatch = (FinanceBatch)this.getTo();

        String bundleName = AonUtil.getConfigurationController().getApplicationBundles().get("financeBundle");
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName, FacesContext.getCurrentInstance().getViewRoot().getLocale());

        IManagerBean fbatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(fbatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_BATCH_ID), fbatch.getId());
        criteria.addEqualExpression(fbatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_STATUS), FinanceStatus.RETURNED);
        if (fbatchDetailBean.getList(criteria).size() > 0) {
            AonUtil.addErrorMessage(bundle.getString("aon_finance_batch_unrecord_error"));
            throw new AbortProcessingException();
        }

        AccountEntry entry = null;
        IManagerBean accountEntryFbatchBean = BeanManager.getManagerBean(AccountEntryFinanceBatch.class);
        IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
        IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
        criteria = new Criteria();
        criteria.addEqualExpression(accountEntryFbatchBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_FINANCE_BATCH_FINANCE_BATCH_ID), fbatch.getId());
        List accountEntryFbatchList = accountEntryFbatchBean.getList(criteria);
        if (accountEntryFbatchList.size() > 0) {
            AccountEntryFinanceBatch accountEntryFinanceBatch = (AccountEntryFinanceBatch)accountEntryFbatchList.get(0);
            entry = accountEntryFinanceBatch.getAccountEntry();
            accountEntryFbatchBean.remove(accountEntryFinanceBatch);

            criteria = new Criteria();
            criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), entry.getId());
            Iterator iterator = accountEntryDetailBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
                AccountEntryDetail accountEntryDetail = (AccountEntryDetail)iterator.next();
                accountEntryDetailBean.remove(accountEntryDetail);
            }

            accountEntryBean.remove(entry);
        }

        IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
        IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
        Iterator iterator = fbatch.getDetailList().iterator();
        while (iterator.hasNext()) {
            FinanceBatchDetail fbatchDetail = (FinanceBatchDetail)iterator.next();
            fbatchDetail.setStatus(FinanceStatus.BATCHED);
            fbatchDetailBean.update(fbatchDetail);

            fbatchDetail.getFinance().setFinanceStatus(FinanceStatus.BATCHED);
            financeBean.update(fbatchDetail.getFinance());

            if (!fbatchDetail.getFinance().getInvoice().getStatus().equals(InvoiceStatus.SCORED)) {
                criteria = new Criteria();
                criteria.addExpression(ExpressionUtilities.getNotEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_ID), fbatchDetail.getFinance().getId()));
                criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), fbatchDetail.getFinance().getInvoice().getId());
                Expression batchedExpr = ExpressionUtilities.getEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.BATCHED);
                Expression paidExpr = ExpressionUtilities.getEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PAID);
                Expression orExpr = ExpressionUtilities.getOrExpression(batchedExpr, paidExpr);
                criteria.addExpression(orExpr);
                if(financeBean.getCount(criteria) == 0){
                    fbatchDetail.getFinance().getInvoice().setStatus(InvoiceStatus.PENDING);
                    invoiceBean.update(fbatchDetail.getFinance().getInvoice());
                }
            }

            FinanceTrackingWriter.removeLastTrackingByType(fbatchDetail.getFinance(), FinanceTrackingType.RECORDED);
        }
        
        fbatch.setRegistryBank(fbatch.getRegistryBank().getId() == null?null:fbatch.getRegistryBank());
        fbatch.setFinanceBatchStatus(fbatch.getFinanceBatchType().equals(FinanceBatchType.NONE) ? FinanceBatchStatus.TODO : FinanceBatchStatus.DONE);
        getManagerBean().update(fbatch);
        loadDetails(fbatch);
        fbatch.setRegistryBank(fbatch.getRegistryBank() == null?new RegistryBank():fbatch.getRegistryBank());
    }

    public Double getModelToTotalAmount(){
		try {
			FinanceBatch fbatch = (FinanceBatch) this.getModel().getRowData();
			return getFinanceBatchTotalAmount(fbatch);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining modelTo fbatch total amount", e);
		}
		return new Double(0);
	}

	public Double getToTotalAmount(){
		FinanceBatch fbatch = (FinanceBatch) this.getTo();
		return getFinanceBatchTotalAmount(fbatch);
	}

	public Double getFinanceBatchTotalAmount(FinanceBatch fbatch){
		try {
			IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_BATCH_ID), fbatch.getId());
			Projection projection = Projection.sum(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_AMOUNT));
			Object value = fBatchDetailBean.getUniqueResult(projection, criteria);
			if (value != null) {
				return (Double)value;
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining fbatch total amount", e);
		}
		return new Double(0);
	}

    public Integer getModelToTotalDetails(){
		try {
			FinanceBatch fbatch = (FinanceBatch) this.getModel().getRowData();
			return getFinanceBatchTotalDetails(fbatch);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining modelTo fbatch total details", e);
		}
		return new Integer(0);
	}

	public Integer getToTotalDetails(){
		FinanceBatch fbatch = (FinanceBatch) this.getTo();
		return getFinanceBatchTotalDetails(fbatch);
	}

	public Integer getFinanceBatchTotalDetails(FinanceBatch fbatch){
		try {
			IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_BATCH_ID), fbatch.getId());
			return new Integer (fBatchDetailBean.getList(criteria).size());
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining fbatch total details", e);
		}
		return new Integer(0);
	}

	@SuppressWarnings("unused")
    public void onReport(ActionEvent event) {
        ReportManager manager = (ReportManager)AonUtil.getRegisteredBean("report");
        manager.setReportKey("fBatch");
        manager.setOutputFormat(OutputFormat.PDF);
    }
}