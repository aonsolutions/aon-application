package com.esferalia.aon.ui.pms.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.PosShift;
import com.esferalia.aon.pms.enumeration.Shift;


public class PosFinanceBatchController extends BasicController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PosFinanceBatchController.class.getName());
	
	private Hotel hotel;
	
	private Date startDate;
	
	private Date endDate;
	
	private PayMethod payMethod;
	
	private Shift shift;
	
	private ArrayList<Finance> checks = new ArrayList<Finance>();
	
	private FinanceBatch financeBatch;
	
	private boolean showFinanceBatchWindow;
	
	private boolean saved;
	
	private DataModel financeBatchModel;
	
	public DataModel getFinanceBatchModel() {
		return financeBatchModel;
	}

	public void setFinanceBatchModel(DataModel financeBatchModel) {
		this.financeBatchModel = financeBatchModel;
	}

	public boolean isShowFinanceBatchWindow() {
		return showFinanceBatchWindow;
	}

	public void setShowFinanceBatchWindow(boolean showFinanceBatchWindow) {
		this.showFinanceBatchWindow = showFinanceBatchWindow;
	}
	
	public boolean isSaved() {
		return saved;
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	public FinanceBatch getFinanceBatch() {
		return financeBatch;
	}

	public void setFinanceBatch(FinanceBatch financeBatch) {
		this.financeBatch = financeBatch;
	}

	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		this.startDate = cal.getTime();
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(endDate);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		this.endDate = cal.getTime();
	}

	public PayMethod getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}

	public Shift getShift() {
		return shift;
	}

	public void setShift(Shift shift) {
		this.shift = shift;
	}
	
	public Double getSelectedAmount(){
		Double total = 0.0;
		for(Finance finance: getCheckedFinances()){
			total += finance.getTotalAmount();
		}
		return total;
	}
	
	public void onSearch(ActionEvent event) {
		setSaved(false);
		clearCheckedFinances();
		try {
			completeCriteria();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
			throw new AbortProcessingException(e.getMessage(),e);
		}
		super.onSearch(event);
	}

	private void completeCriteria() throws ManagerBeanException {
		this.getCriteria().addBetweenExpression(this.getFieldName(IEntityAlias.FINANCE_INVOICE_CREATION_DATE), getStartDate(), getEndDate() );
		if(getPayMethod()!=null && getPayMethod().getId()!=null){
			this.getCriteria().addEqualExpression(this.getFieldName(IEntityAlias.FINANCE_PAY_METHOD_ID), getPayMethod().getId());
		}
		this.getCriteria().addEqualExpression(this.getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
		this.getCriteria().addEqualExpression(this.getFieldName(IEntityAlias.FINANCE_PAYMENT), false);
		if(getUserList()!=null && !getUserList().isEmpty()){
			this.getCriteria().addInExpression(this.getFieldName(IEntityAlias.FINANCE_INVOICE_CREATION_USER), getUserList());
		}
		this.getCriteria().addOrder(this.getFieldName(IEntityAlias.FINANCE_PAY_METHOD_NAME));
	}
	

	private List<String> getUserList() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(PosShift.class);
		Criteria criteria = new Criteria();
		criteria.addBetweenExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_START_TIME), getStartDate(), getEndDate() );
		criteria.addBetweenExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_END_TIME), getStartDate(), getEndDate() );
		if(getShift()!=null){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_SHIFT), getShift());
		}
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_POS_WORK_PLACE_ID), getHotel().getWorkPlace().getId());
		List<String> list = new LinkedList<String>();
		for(ITransferObject to: bean.getList(criteria)){
			PosShift posShift = (PosShift) to;
			list.add(posShift.getUser().getLogin());
		}
		return list;
	}

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	public boolean getRowChecked() {
		Finance to = (Finance) model.getRowData();
		return checks.contains(to);
	}
	
	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			Finance to = (Finance) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			Finance to = (Finance) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}
	
	public ArrayList<Finance> getCheckedFinances() {
		return checks;
	}
	
	public void clearCheckedFinances() {
		checks = new ArrayList<Finance>();
	}
	
	public void checkAll(ActionEvent event) throws ManagerBeanException {
		Iterator<ITransferObject> iterator = this.getManagerBean().getList(this.getCriteria()).iterator();
		while (iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			if (!checks.contains(finance)) {
				checks.add(finance);
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedFinances();
	}
	
	public void onInit(ActionEvent event) throws ManagerBeanException {
		setSaved(false);
		setHotel(null);
		setStartDate(new Date());
		setEndDate(new Date());
		setPayMethod(null);
		setShift(null);
		setFinanceBatch(null);
	}
	
	public void onCompleteBatch(ActionEvent event) throws ManagerBeanException {
		setFinanceBatch(null);
		loadBatchModel();
		if(getFinanceBatchModel().getRowCount()<=0){
			createNewBatch();
		}
		setShowFinanceBatchWindow(true);
	}
	
	public void onAcceptBatch(ActionEvent event) throws ManagerBeanException {
		if(getFinanceBatch()!=null && getFinanceBatch().getId()==null){
			insertFinanceBatch();
		} 
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			insertFinanceBatchDetails();
			
			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg,daoe);
			}
			removeFinanceBatch();
			LOGGER.error(e.getMessage());
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
		setSaved(true);
	}

	private void loadBatchModel() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(FinanceBatch.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FINANCE_BATCH_FINANCE_BATCH_STATUS), FinanceBatchStatus.TODO);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FINANCE_BATCH_FINANCE_BATCH_TYPE), FinanceBatchType.NONE);
		criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.FINANCE_BATCH_ISSUE_DATE), getBatchMaxDate() );
		setFinanceBatchModel(new ListDataModel(bean.getList(criteria)));
	}
	
	private Date getBatchMaxDate() throws ManagerBeanException {
		Projection projection = Projection.max(this.getFieldName(IEntityAlias.FINANCE_DUE_DATE));
		Object result = this.getManagerBean().getUniqueResult(projection, this.getCriteria());
		return (Date) result;
	}
	
	public void onSelectBatch(ActionEvent event) throws ManagerBeanException {
		setFinanceBatch((FinanceBatch) getFinanceBatchModel().getRowData());
		setFinanceBatchModel(null);
	}
	
	public void onResetBatch(ActionEvent event) throws ManagerBeanException {
		createNewBatch();
		setFinanceBatchModel(null);
	}
	
	public void onLoadFinanceBatch(ActionEvent event) throws ManagerBeanException {
		if (getFinanceBatch()!=null && getFinanceBatch().getId()!=null) {
			BasicController controller = (BasicController) AonUtil.getRegisteredBean("fbatch");
			controller.onLoad(event, getFinanceBatch().getId(), IPmsConstants.POS_FINANCE_BATCH_LIST_NAME, null);
		}
	}
	
	private void createNewBatch() {
		setFinanceBatch(new FinanceBatch());
		String description = (new SimpleDateFormat("dd/MM/yyyy")).format(new Date()) +". "+ getHotel().getWorkPlace().getDescription() +". "+ getPayMethod().getName();
		getFinanceBatch().setConfidential(false);
		getFinanceBatch().setDescription(description);
		getFinanceBatch().setIssueDate(new Date());
		getFinanceBatch().setFinanceBatchType(FinanceBatchType.NONE);
		getFinanceBatch().setFinanceBatchStatus(FinanceBatchStatus.TODO);
		getFinanceBatch().setPayment(false);
		getFinanceBatch().setSecurityLevel(SecurityLevel.OFFICIAL);
	}
	
	private void insertFinanceBatch() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(FinanceBatch.class);
		FinanceBatch batch = (FinanceBatch) bean.insert(getFinanceBatch());
		setFinanceBatch(batch);
	}

	private void removeFinanceBatch() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(FinanceBatch.class);
		bean.remove(getFinanceBatch());
	}
	
	private void insertFinanceBatchDetails() throws ManagerBeanException {
		IManagerBean batchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		for(Finance finance: getCheckedFinances()){
			finance.setFinanceStatus(FinanceStatus.BATCHED);
			FinanceBatchDetail detail = new FinanceBatchDetail();
			detail.setAmount(finance.getTotalAmount());
			detail.setFinance((Finance) financeBean.update(finance));
			detail.setFinanceBatch(getFinanceBatch());
			detail.setStatus(FinanceStatus.BATCHED);
			batchDetailBean.insert(detail);
		}
	}

}
