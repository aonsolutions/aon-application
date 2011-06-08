package com.code.aon.ui.finance.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.account.bridge.AccountEntryFinanceBatch;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.writer.AccountEntryFinanceWriter;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.BankStatement;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.finance.event.FinanceListSearchListener;
import com.code.aon.ui.finance.file.AEB19Writer;
import com.code.aon.ui.finance.file.AEB32Writer;
import com.code.aon.ui.finance.file.AEB34Writer;
import com.code.aon.ui.finance.file.AEB58Writer;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class FBatchController extends BasicController implements ICollectionProvider, IFinanceConstants {

	private Company company;
	private boolean payment;
	private FileOutput aebOutput;
	private Date recordDate;
	private boolean showFbatchRecordWindow;
	private AccountEntryFinanceWriter writer;

	public Company getCompany() {
		if (company == null) {
			CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			setCompany( companyController.obtainCompany() );
		}
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public boolean isPayment() {
		return payment;
	}

	public void setPayment(boolean payment) {
		this.payment = payment;
	}

	public FileOutput getAebOutput() {
		return aebOutput;
	}

	public void setAebOutput(FileOutput aebOutput) {
		this.aebOutput = aebOutput;
	}

	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

	public boolean isShowFbatchRecordWindow() {
		return showFbatchRecordWindow;
	}

	public void setShowFbatchRecordWindow(boolean value) {
		this.showFbatchRecordWindow = value;
	}
	
	public AccountEntryFinanceWriter getWriter() {
		if(writer == null){
			writer = new AccountEntryFinanceWriter();
		}
		return writer;
	}

    public boolean isTodo() {
        return FinanceBatchStatus.TODO == ((FinanceBatch)this.getTo()).getFinanceBatchStatus();
    }

    public boolean isDone() {
        return FinanceBatchStatus.DONE == ((FinanceBatch)this.getTo()).getFinanceBatchStatus();
    }

    public boolean isRecorded() {
        return FinanceBatchStatus.RECORDED == ((FinanceBatch)this.getTo()).getFinanceBatchStatus();
    }

    public boolean isDiskMode() {
        return FinanceBatchType.NONE != ((FinanceBatch)this.getTo()).getFinanceBatchType();
    }

    public boolean isInStatement() {
    	FinanceBatch fBatch = (FinanceBatch)this.getTo();
    	return (fBatch.getBankStatementLink() != null && fBatch.getBankStatementLink().getId() != null);
    }

    public boolean isReadOnly() {
    	return isRecorded() || isInStatement();
    }

    public boolean isFilled() {
		FinanceBatch fbatch = (FinanceBatch) this.getTo();
        return (fbatch.getFinanceBatchTotalDetails().intValue() > 0);
    }

	public void onEditSearchCharge(ActionEvent event) {
		setPayment(false);
		super.onEditSearch(event);
	}

	public void onEditSearchPayment(ActionEvent event) {
		setPayment(true);
		super.onEditSearch(event);
	}

	@SuppressWarnings("unchecked")
	public Integer getAccountEntryId() throws ManagerBeanException {
    	FinanceBatch to = (FinanceBatch)this.getTo();
		if (to != null && to.getId() != null) {
			IManagerBean accEntryFBatchBean = BeanManager.getManagerBean(AccountEntryFinanceBatch.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accEntryFBatchBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_FINANCE_BATCH_FINANCE_BATCH_ID), to.getId());
			Iterator iterator = accEntryFBatchBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				AccountEntryFinanceBatch accountEntryFbatch = (AccountEntryFinanceBatch)iterator.next();
				return accountEntryFbatch.getAccountEntry().getId();
			}
		}
    	return null;
	}

	public void onEditSearchFinance(ActionEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
		financeList.onEditSearch(event);
        financeList.setCriteria(getAvailableFinancesCriteria());
	}

	public List<SelectItem> getSearchPayMethods() throws ManagerBeanException {
        FinanceBatch to = (FinanceBatch)this.getTo();

        List<SelectItem> payMethods = new LinkedList<SelectItem>();
		IManagerBean payMethodBean = BeanManager.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(payMethodBean.getFieldName(IConfigAlias.PAY_METHOD_NAME));
		Iterator<?> iter = payMethodBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			PayMethod payMethod = (PayMethod) iter.next();
			boolean validPayMethod = true;
			if (to.getFinanceBatchType() != (FinanceBatchType.NONE)) {
				if (to.getFinanceBatchType() != FinanceBatchType.AEB_34) {
					validPayMethod = (payMethod.getType() == PayMethodType.NEGOTIABLE_DOCUMENT);
				} else {
					validPayMethod = (payMethod.getType() == PayMethodType.CHEQUE || payMethod.getType() == PayMethodType.BANK_TRANSFER);
				}
			}

			if (validPayMethod) {
				SelectItem item = new SelectItem(payMethod, payMethod.getName());
				payMethods.add(item);
			}
		}
		return payMethods;
	}

	private Criteria getAvailableFinancesCriteria() throws ManagerBeanException {
        FinanceBatch to = (FinanceBatch)this.getTo();

        FinanceListSearchListener financeSearch = (FinanceListSearchListener)AonUtil.getRegisteredBean(FINANCE_LIST_SEARCH_LISTENER_NAME);
		FinanceStatus[] financeStatuses = {FinanceStatus.PENDING, FinanceStatus.RETURNED};
		financeSearch.setFinanceStatuses(financeStatuses);

        FinanceListController financeController = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(financeController.getFieldName(IFinanceAlias.FINANCE_PAYMENT), new Boolean(payment));
        criteria.addGreaterThanExpression(financeController.getFieldName(IFinanceAlias.FINANCE_AMOUNT), new Double(0));
        if (to.getFinanceBatchType() != (FinanceBatchType.NONE)) {
        	if (to.getFinanceBatchType() != FinanceBatchType.AEB_34) {
        		String payMethodTypeAlias = financeController.getFieldName(IFinanceAlias.FINANCE_PAY_METHOD_TYPE);
        		criteria.addEqualExpression(payMethodTypeAlias, PayMethodType.NEGOTIABLE_DOCUMENT);
        	} else {
        		String payMethodTypeAlias = financeController.getFieldName(IFinanceAlias.FINANCE_PAY_METHOD_TYPE);
        		Expression transferExpr = ExpressionUtilities.getEqualExpression(payMethodTypeAlias, PayMethodType.BANK_TRANSFER);
                Expression chequeExpr = ExpressionUtilities.getEqualExpression(payMethodTypeAlias, PayMethodType.CHEQUE);
                criteria.addExpression(ExpressionUtilities.getOrExpression(transferExpr, chequeExpr));
        	}
            if ((to.getFinanceBatchType() != FinanceBatchType.AEB_58) && (to.getFinanceBatchType() != FinanceBatchType.AEB_58_D)) {
            	criteria.addNotNullExpression(financeController.getFieldName(IFinanceAlias.FINANCE_BANK_ACCOUNT));
            	criteria.addNotEqualExpression(financeController.getFieldName(IFinanceAlias.FINANCE_BANK_ACCOUNT), new BankAccount());
                if ((to.getFinanceBatchType() != FinanceBatchType.AEB_32) && (to.getFinanceBatchType() != FinanceBatchType.AEB_34)) {
                    criteria.addLessThanOrEqualExpression(financeController.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), to.getIssueDate());
                }
            }
        }
    	criteria.addEqualExpression(financeController.getFieldName(IFinanceAlias.FINANCE_SECURITY_LEVEL), to.getSecurityLevel());	
        criteria.addOrder(financeController.getFieldName(IFinanceAlias.FINANCE_DUE_DATE));
        criteria.addOrder(financeController.getFieldName(IFinanceAlias.FINANCE_CONCEPT));
		return criteria;
	}

	public void onSearchFinance(ActionEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
		financeList.onSearch(event);
	}

	public void loadAvailableFinances(ActionEvent event) throws ManagerBeanException {
		loadAvailableFinances();
	}

	public void loadAvailableFinances() throws ManagerBeanException {
		this.onEditSearchFinance(null);
		this.onSearchFinance(null);
    }

    private void loadDetails(FinanceBatch fbatch) {
        LinesController fBatchDetailController = (LinesController)FormUtil.getController(FINANCE_BATCH_DETAIL_CONTROLLER_NAME);
        fBatchDetailController.onSearch(null);
    }

    @SuppressWarnings("unchecked")
	public void onBatchSelected(ActionEvent event) throws ManagerBeanException {
        FinanceBatch fBatch = (FinanceBatch)getTo();
        if (FinanceBatchStatus.TODO != fBatch.getFinanceBatchStatus()) {
            fBatch.setFinanceBatchStatus(FinanceBatchStatus.TODO);
            getManagerBean().update(fBatch);
            setAebOutput(null);
        }

        IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		IManagerBean financeBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
        FinanceListController financeController = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
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

			String message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_TRACKING_BATCHED);
			message += " " + fBatch.getId() + " - " + fBatch.getDescription();
			FinanceTrackingWriter.addFinanceTracking(finance, fBatch.getIssueDate(), FinanceTrackingType.BATCHED, message);
        }
        financeController.clearCheckedFinances();
        loadDetails(fBatch);
        onSearchFinance(event);
	}

	@SuppressWarnings("unchecked")
	public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
        FinanceBatch fBatch = (FinanceBatch)getTo();
        if (FinanceBatchStatus.TODO != fBatch.getFinanceBatchStatus()) {
            fBatch.setFinanceBatchStatus(FinanceBatchStatus.TODO);
            getManagerBean().update(fBatch);
            setAebOutput(null);
        }

		IManagerBean financeBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
        FBatchDetailController fBatchDetailController = (FBatchDetailController)FormUtil.getController(FINANCE_BATCH_DETAIL_CONTROLLER_NAME);
		Iterator iterator = fBatchDetailController.getCheckedFinanceBatchDetails().iterator();
        while(iterator.hasNext()){
        	FinanceBatchDetail fBatchDetail = (FinanceBatchDetail)iterator.next();
        	financeBatchDetailBean.remove(fBatchDetail);
        }
		fBatchDetailController.clearCheckedFinanceBatchDetails();
        loadDetails(fBatch);
        onSearchFinance(event);
    }

	@SuppressWarnings("unchecked")
	public void onCreateDisk(ActionEvent event) throws ManagerBeanException {
    	FinanceBatch fbatch = (FinanceBatch)this.getTo();

        Collection fbatchDetailCollection = obtainDetailsCollection(fbatch);
        if ((fbatch.getFinanceBatchType() == FinanceBatchType.AEB_19) || (fbatch.getFinanceBatchType() == FinanceBatchType.AEB_19_D)) {
			AEB19Writer aeb19Writer = new AEB19Writer();
			aebOutput = aeb19Writer.createAEB19(getCompany(), fbatch, fbatchDetailCollection);
		}
		else if (fbatch.getFinanceBatchType() == FinanceBatchType.AEB_32) {
			AEB32Writer aeb32Writer = new AEB32Writer();
			aebOutput = aeb32Writer.createAEB32(getCompany(), fbatch, fbatchDetailCollection);
		}
		else if (fbatch.getFinanceBatchType() == FinanceBatchType.AEB_34) {
			AEB34Writer aeb34Writer = new AEB34Writer();
			aebOutput = aeb34Writer.createAEB34(getCompany(), fbatch, fbatchDetailCollection);
		}
		else if ((fbatch.getFinanceBatchType() == FinanceBatchType.AEB_58) || (fbatch.getFinanceBatchType() == FinanceBatchType.AEB_58_D)) {
			AEB58Writer aeb58Writer = new AEB58Writer();
			aebOutput = aeb58Writer.createAEB58(getCompany(), fbatch, fbatchDetailCollection);
		}

        if (aebOutput != null) {
        	if (aebOutput.getErrors().size() > 0) {
        		AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_BATCH_DISK_ERROR);
        	} else {
                fbatch.setFinanceBatchStatus(FinanceBatchStatus.DONE);
                getManagerBean().update(fbatch);
        	}
        }
	}

	@SuppressWarnings("unchecked")
	private Collection obtainDetailsCollection(FinanceBatch fbatch) {
		String select = "select fbatchDetail " +
    					"from FinanceBatchDetail as fbatchDetail " +
    					"where fbatchDetail.financeBatch.id = " + fbatch.getId() + " " +
    					"order by substring(fbatchDetail.finance.bankAccount, 1, 8), fbatchDetail.finance.registry.id";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
    	Query query = session.createQuery(select);
    	return query.list(); 
	}

	public boolean isDiskOk() {
		int errors = 0;
		if (aebOutput != null) {
			errors = aebOutput.getErrors().size();
		}
		return (errors==0);
	}

	public void downloadDisk(ActionEvent event) throws ManagerBeanException {
        try {
    		FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();

        	String fileName = ((FinanceBatch)this.getTo()).getFinanceBatchType().getName(AonUtil.getCurrentLocale());
	        fileName += "-" + ((FinanceBatch)this.getTo()).getDescription();
	        fileName = ((aebOutput.getErrors().size()>0) ? "ERROR-" : "") + fileName;

	        response.setContentType(MimeType.MIME_TXT.getName());
	        response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".txt\"");

	        ServletOutputStream output = response.getOutputStream();
	        InputStream input = new FileInputStream(aebOutput.getFile());
	        int size = IOUtils.copy(input, output);
	        if (size > 0) {
		        response.setHeader("Content-Length", String.valueOf(size));
	        }
	        output.close();
	        input.close();
	        response.flushBuffer();
	        faces.responseComplete();

	        aebOutput.getFile().delete();
	        setAebOutput(null);
        } catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}

    public void onRecord(ActionEvent event) throws ManagerBeanException {
        FinanceBatch fBatch = (FinanceBatch)this.getTo();
        getWriter().recordFBatch(fBatch, getRecordDate());
        loadDetails(fBatch);
    }

    public void onUnrecord(ActionEvent event) throws ManagerBeanException {
        FinanceBatch fBatch = (FinanceBatch)this.getTo();
        if (getWriter().canRemoveAccountEntryFinanceBatch(fBatch)) {
            getWriter().removeAccountEntryFinanceBatch(fBatch);
            loadDetails(fBatch);
        } else {
            AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_BATCH_UNRECORD_ERROR);
            throw new AbortProcessingException();
        }
    }

	public void onLoadBankStatement(ActionEvent event) throws ManagerBeanException {
        BankStatement statement = ((FinanceBatch)this.getTo()).getBankStatementLink().getBankStatement();
		BankStatementController statementController = (BankStatementController) AonUtil.getRegisteredBean(BANK_STATEMENT_CONTROLLER_NAME);
		statementController.onLoadBankStatement(event, statement, FINANCE_BATCH_FORM_NAME, FINANCE_BATCH_CONTROLLER_NAME + ".refresh");
	}

	public void onLoadFinanceBatch(ActionEvent event, FinanceBatch fBatch, String backAction, String backActionListener) 
		throws ManagerBeanException{
		onEditSearch(event);
		getCriteria().addEqualExpression(getFieldName(IFinanceAlias.FINANCE_BATCH_ID), fBatch.getId());
		onSearch(event);
		getModel().setRowIndex(0);
		onSelect(event);

		setBackAction(backAction);
		setBackActionListener(backActionListener);
	}

}