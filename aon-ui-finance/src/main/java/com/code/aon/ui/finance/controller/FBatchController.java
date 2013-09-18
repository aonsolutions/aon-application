package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_BATCH_DISK_ERROR;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_BATCH_UNRECORD_ERROR;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_BUNDLE;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.bridge.AccountEntryFinanceBatch;
import com.code.aon.account.bridge.writer.AccountEntryFinanceWriter;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.BankStatement;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.event.FinanceListSearchListener;
import com.code.aon.ui.finance.file.AEB19Writer;
import com.code.aon.ui.finance.file.AEB32Writer;
import com.code.aon.ui.finance.file.AEB34Writer;
import com.code.aon.ui.finance.file.AEB58Writer;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class FBatchController extends BasicController implements ICollectionProvider, IFinanceConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(FBatchController.class.getName());

	private Company company;
	private boolean payment;
	private FileOutput aebOutput;
	private Date recordDate;
	private boolean showFbatchRecordWindow;
	private AccountEntryFinanceWriter writer;

	public Company getCompany() {
		if (company == null) {
			CompanyController companyController = (CompanyController)AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			setCompany(companyController.obtainCompany());
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

	public Integer getAccountEntryId() throws ManagerBeanException {
    	FinanceBatch to = (FinanceBatch)this.getTo();
		if (to != null && to.getId() != null) {
			IManagerBean accEntryFBatchBean = BeanManager.getManagerBean(AccountEntryFinanceBatch.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accEntryFBatchBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_FINANCE_BATCH_FINANCE_BATCH_ID), to.getId());
			Iterator<?> iterator = accEntryFBatchBean.getList(criteria).iterator();
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
		criteria.addOrder(payMethodBean.getFieldName(IEntityAlias.PAY_METHOD_NAME));
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
        criteria.addEqualExpression(financeController.getFieldName(IEntityAlias.FINANCE_PAYMENT), new Boolean(payment));
        if (to.getFinanceBatchType() != (FinanceBatchType.NONE)) {
            criteria.addGreaterThanExpression(financeController.getFieldName(IEntityAlias.FINANCE_AMOUNT), new Double(0));
        	if (to.getFinanceBatchType() != FinanceBatchType.AEB_34) {
        		String payMethodTypeAlias = financeController.getFieldName(IEntityAlias.FINANCE_PAY_METHOD_TYPE);
        		criteria.addEqualExpression(payMethodTypeAlias, PayMethodType.NEGOTIABLE_DOCUMENT);
        	} else {
        		String payMethodTypeAlias = financeController.getFieldName(IEntityAlias.FINANCE_PAY_METHOD_TYPE);
        		Expression transferExpr = ExpressionUtilities.getEqualExpression(payMethodTypeAlias, PayMethodType.BANK_TRANSFER);
                Expression chequeExpr = ExpressionUtilities.getEqualExpression(payMethodTypeAlias, PayMethodType.CHEQUE);
                criteria.addExpression(ExpressionUtilities.getOrExpression(transferExpr, chequeExpr));
        	}
            if ((to.getFinanceBatchType() != FinanceBatchType.AEB_58) && (to.getFinanceBatchType() != FinanceBatchType.AEB_58_D)) {
            	criteria.addNotNullExpression(financeController.getFieldName(IEntityAlias.FINANCE_BANK_ACCOUNT));
            	criteria.addNotEqualExpression(financeController.getFieldName(IEntityAlias.FINANCE_BANK_ACCOUNT), new BankAccount());
                if ((to.getFinanceBatchType() != FinanceBatchType.AEB_32) && (to.getFinanceBatchType() != FinanceBatchType.AEB_34)) {
                    criteria.addLessThanOrEqualExpression(financeController.getFieldName(IEntityAlias.FINANCE_DUE_DATE), to.getIssueDate());
                }
            }
        }
    	criteria.addEqualExpression(financeController.getFieldName(IEntityAlias.FINANCE_SECURITY_LEVEL), to.getSecurityLevel());	
        criteria.addOrder(financeController.getFieldName(IEntityAlias.FINANCE_DUE_DATE));
        criteria.addOrder(financeController.getFieldName(IEntityAlias.FINANCE_CONCEPT));
		return criteria;
	}

	public void onSearchFinance(ActionEvent event)  {
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

    public void loadDetails(ActionEvent event) {
    	loadDetails();
    }

    private void loadDetails() {
        LinesController fBatchDetailController = (LinesController)FormUtil.getController(FINANCE_BATCH_DETAIL_CONTROLLER_NAME);
        fBatchDetailController.onSearch(null);
    }

	public void onBatchSelected(ActionEvent event) {
		
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Finance.class.getName());
		Session session = HibernateUtil.getSession(sessionName);
		
		FinanceListController financeController = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);

	        FinanceBatch fBatch = (FinanceBatch)getTo();
	        if (FinanceBatchStatus.TODO != fBatch.getFinanceBatchStatus()) {
	            fBatch.setFinanceBatchStatus(FinanceBatchStatus.TODO);
	            getManagerBean().update(fBatch);
	            setAebOutput(null);
	        }

	        IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			IManagerBean financeBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
	        Iterator<Finance> iterator = financeController.getCheckedFinances().iterator();
	        int i = 0;
	        while (iterator.hasNext()) {
	        	i++;
				Finance finance = (Finance)financeBean.get(iterator.next().getId());
				if (finance.getFinanceStatus() == FinanceStatus.PENDING || finance.getFinanceStatus() == FinanceStatus.RETURNED) {
		            FinanceBatchDetail fBatchDetail = new FinanceBatchDetail();
					fBatchDetail.setFinance(finance);
					fBatchDetail.setFinanceBatch(fBatch);
		            fBatchDetail.setAmount(finance.getTotalAmount());
		            fBatchDetail.setStatus(FinanceStatus.BATCHED);
					financeBatchDetailBean.insert(fBatchDetail);
				}
				if (i % 20 == 0) {
					session.flush();
					session.clear();
				}
	        }
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			String msg = "Error al añadir vtos. a la remesa. ";
			AonUtil.addErrorMessage(msg  + e.getMessage());
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);

	        financeController.clearCheckedFinances();
	        loadDetails();
	        onSearchFinance(event);
		}
		
	}

	public void onRemoveSelected(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Finance.class.getName());
		Session session = HibernateUtil.getSession(sessionName);
        FBatchDetailController fBatchDetailController = (FBatchDetailController)FormUtil.getController(FINANCE_BATCH_DETAIL_CONTROLLER_NAME);
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);

			FinanceBatch fBatch = (FinanceBatch)getTo();
	        if (FinanceBatchStatus.TODO != fBatch.getFinanceBatchStatus()) {
	            fBatch.setFinanceBatchStatus(FinanceBatchStatus.TODO);
	            getManagerBean().update(fBatch);
	            setAebOutput(null);
	        }
	
			IManagerBean financeBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
			Iterator<?> iterator = fBatchDetailController.getCheckedFinanceBatchDetails().iterator();
	        int i = 0;
	        while(iterator.hasNext()){
	        	i++;
	        	FinanceBatchDetail fBatchDetail = (FinanceBatchDetail)iterator.next();
	        	financeBatchDetailBean.remove(fBatchDetail);
				if (i % 20 == 0) {
					session.flush();
					session.clear();
				}
	        }
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			String msg = "Error al quitar vtos. de la remesa. ";
			AonUtil.addErrorMessage(msg  + e.getMessage());
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
			
			fBatchDetailController.clearCheckedFinanceBatchDetails();
	        loadDetails();
	        onSearchFinance(event);
		}
    }

	public void onCreateDisk(ActionEvent event) throws ManagerBeanException {
    	FinanceBatch fbatch = (FinanceBatch)this.getTo();

        Collection<?> fbatchDetailCollection = obtainDetailsCollection(fbatch);
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
        		AonUtil.addErrorMessageFromBundle(FINANCE_BUNDLE, FINANCE_BATCH_DISK_ERROR);
        	} else {
                fbatch.setFinanceBatchStatus(FinanceBatchStatus.DONE);
                getManagerBean().update(fbatch);
        	}
        }
	}

	private Collection<?> obtainDetailsCollection(FinanceBatch fbatch) {
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
        loadDetails();
    }

    public void onUnrecord(ActionEvent event) throws ManagerBeanException {
        FinanceBatch fBatch = (FinanceBatch)this.getTo();
        if (getWriter().canRemoveAccountEntryFinanceBatch(fBatch)) {
            getWriter().removeAccountEntryFinanceBatch(fBatch);
            loadDetails();
        } else {
            AonUtil.addErrorMessageFromBundle(FINANCE_BUNDLE, FINANCE_BATCH_UNRECORD_ERROR);
            throw new AbortProcessingException();
        }
    }

	public void onLoadBankStatement(ActionEvent event) throws ManagerBeanException {
        BankStatement statement = ((FinanceBatch)this.getTo()).getBankStatementLink().getBankStatement();
        BankStatementController statementController = (BankStatementController) AonUtil.getRegisteredBean(BANK_STATEMENT_CONTROLLER_NAME);
		statementController.onLoad(event, statement, FINANCE_BATCH_FORM_NAME, FINANCE_BATCH_CONTROLLER_NAME + ".onBackFinanceBatch");
	}

	public void onBackFinanceBatch(ActionEvent event) throws ManagerBeanException {
		refresh(event);

		loadDetails();
		loadAvailableFinances();
	}

}