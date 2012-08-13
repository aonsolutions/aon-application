package com.esferalia.aon.ui.pms.controller;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.PayMethod;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.finance.controller.FinanceListController;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.event.FinanceListSearchListener;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;


public class PaymentCardSettleController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(PaymentCardSettleController.class);
	
	private final String INCLUDED_FINANCE_TAB = "tab1";
	private final String PENDING_FINANCE_TAB = "tab2";
	
	private String selectedTab;

	private ArrayList<AgencyFinance> checks = new ArrayList<AgencyFinance>();
	
	private DataModel financeModel;
	private List<AgencyFinance> financeList;
	
	private Customer agency;
	private Hotel hotel;
	private Date startDate;
	private Date endDate;
	private PayMethod payMethod;
	private String guestName;
	private String guestSurname;
	private Integer reservationId;
	private String reservationCode;
	
	
	private boolean newBatch;
	private FinanceBatch financeBatch;
	
	private boolean showFinanceSearchWindow;
	private boolean showFinanceFractionWindow;
	private boolean showFinanceBatchWindow;
	private boolean fractioned;
	
	
	public Integer getReservationId() {
		return reservationId;
	}
	public void setReservationId(Integer reservationId) {
		this.reservationId = reservationId;
	}
	public String getReservationCode() {
		return reservationCode;
	}
	public void setReservationCode(String reservationCode) {
		this.reservationCode = reservationCode;
	}
	public String getGuestName() {
		return guestName;
	}
	public void setGuestName(String guestName) {
		this.guestName = guestName;
	}
	public String getGuestSurname() {
		return guestSurname;
	}
	public void setGuestSurname(String guestSurname) {
		this.guestSurname = guestSurname;
	}
	public boolean isShowFinanceSearchWindow() {
		return showFinanceSearchWindow;
	}
	public void setShowFinanceSearchWindow(boolean showFinanceSearchWindow) {
		this.showFinanceSearchWindow = showFinanceSearchWindow;
	}
	public boolean isShowFinanceBatchWindow() {
		return showFinanceBatchWindow;
	}
	public void setShowFinanceBatchWindow(boolean showFinanceBatchWindow) {
		this.showFinanceBatchWindow = showFinanceBatchWindow;
	}
	public boolean isFractioned() {
		return fractioned;
	}
	public void setFractioned(boolean fractioned) {
		this.fractioned = fractioned;
	}
	public boolean isShowFinanceFractionWindow() {
		return showFinanceFractionWindow;
	}
	public void setShowFinanceFractionWindow(boolean showFinanceFractionWindow) {
		this.showFinanceFractionWindow = showFinanceFractionWindow;
	}
	public boolean isNewBatch() {
		return newBatch;
	}
	public void setNewBatch(boolean newBatch) {
		this.newBatch = newBatch;
	}
	public FinanceBatch getFinanceBatch() {
		return financeBatch;
	}
	public void setFinanceBatch(FinanceBatch financeBatch) {
		this.financeBatch = financeBatch;
	}
	public PayMethod getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}
	public Customer getAgency() {
		return agency;
	}
	public void setAgency(Customer agency) {
		this.agency = agency;
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
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
//		this.endDate = DateUtils. addMilliseconds(endDate, 24*60*60*1000 - 1000);
		this.endDate = endDate;
	}
	
	public DataModel getFinanceModel() {
		if(financeModel == null){
			financeModel = new ListDataModel(getFinanceList());
		}
		return financeModel;
	}

	public void setFinanceModel(DataModel financeModel) {
		this.financeModel = financeModel;
	}

	public List<AgencyFinance> getFinanceList() {
		if(financeList==null){
			financeList = new LinkedList<AgencyFinance>();
		}
		return financeList;
	}

	public void setFinanceList(List<AgencyFinance> financeList) {
		this.financeList = financeList;
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public void rowSelected(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	public boolean getRowChecked() throws ManagerBeanException {
		AgencyFinance to = (AgencyFinance) getFinanceModel().getRowData();
		return checks.contains(to);
	}

	public void setRowChecked(boolean rowChecked) throws ManagerBeanException {
		AgencyFinance to = (AgencyFinance) getFinanceModel().getRowData();
		if (rowChecked) {
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}
	
	public ArrayList<AgencyFinance> getCheckedFinances() {
		return checks;
	}

	public void clearCheckedFinances() {
		checks = new ArrayList<AgencyFinance>();
	}
	
	public void checkAll(ActionEvent event) throws ManagerBeanException{
		for (AgencyFinance af : getFinanceList()) {
//			Finance detail = (Finance)ito;
			if (!checks.contains(af)) {
				checks.add( af );
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedFinances();
	}

	public int getCheckedCount() {
		return getCheckedFinances().size();
	}
	
	public Double getTotalDifferenceAmount() {
		Double total = 0.0;
		for(AgencyFinance af: getFinanceList()){
			total += (af.getFinance().getTotalAmount() - af.getAmount());
		}
		return total;
	}
	
	public Double getTotalFinanceAmount() {
		Double total = 0.0;
		for(AgencyFinance af: getFinanceList()){
			total += af.getFinance().getTotalAmount();
		}
		return total;
	}
	
	public ProjectReservation getFinanceProjectReservation() throws ManagerBeanException{
		AgencyFinance af = (AgencyFinance) getFinanceModel().getRowData();
		IManagerBean bean = BeanManager.getManagerBean(ProjectReservation.class);
		return ((ProjectReservation)bean.get(af.getFinance().getInvoice().getProject().getId()));
	}
	
	public ProjectReservation getFinanceListProjectReservation() throws ManagerBeanException{
		FinanceListController financeList = (FinanceListController)FormUtil.getController(IFinanceConstants.FINANCE_LIST_CONTROLLER_NAME);
		Finance finance = (Finance) financeList.getModel().getRowData();
		IManagerBean bean = BeanManager.getManagerBean(ProjectReservation.class);
		return ((ProjectReservation)bean.get(finance.getInvoice().getProject().getId()));
	}
	
	public List<SelectItem> getFinanceBatchList() throws ManagerBeanException {
//		PosFinanceSearchListener searchListener = (PosFinanceSearchListener)AonUtil.getRegisteredBean(POS_FINANCE_SEARCH_LISTENER_NAME);
		IManagerBean fBatchBean = BeanManager.getManagerBean(FinanceBatch.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_PAYMENT), false);
		criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_FINANCE_BATCH_STATUS), FinanceBatchStatus.TODO);
		criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_FINANCE_BATCH_TYPE), FinanceBatchType.NONE);
		if(getEndDate()!=null){
			criteria.addGreaterThanOrEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_ISSUE_DATE), getEndDate());
		}
		criteria.addOrder(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_ISSUE_DATE));
		criteria.addOrder(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_DESCRIPTION));
		List<SelectItem> fBatchList = new LinkedList<SelectItem>();
		for (ITransferObject ito : fBatchBean.getList(criteria)) {
			FinanceBatch fBatch = (FinanceBatch)ito;
			String date = new SimpleDateFormat(AonUtil.getMessage(ICommonConstants.DEFAULT_BUNDLE, "aon_date_pattern")).format(fBatch.getIssueDate());
			String amount = new DecimalFormat(AonUtil.getMessage(ICommonConstants.DEFAULT_BUNDLE, "aon_price_pattern")).format(fBatch.getFinanceBatchTotalAmount());
			
			SelectItem item = new SelectItem(fBatch, date + " " + StringUtils.leftPad(amount, 10, "·") + "EUR. - " +fBatch.getDescription());
			fBatchList.add(item);
		}
		return fBatchList;
	}
	
	public void onExecuteReport(){
//		ReportManager report = (ReportManager) AonUtil.getRegisteredBean("report");
//		report.onExecute();
//		batchFinances();
//		clearCheckedFinances();
	}
	
	public void onInit(ActionEvent event) throws ManagerBeanException{
		setFractioned(false);
		setFinanceModel(null);
		setFinanceList(null);
		setGuestName(null);
		setGuestSurname(null);
		setPayMethod(null);
		setReservationId(null);
		setReservationCode(null);
	}
	
	public void onEditSearch(ActionEvent event) {
		try {
			setAgency((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
			onInit(event);
			onEditSearchFinance(event);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onSearch(ActionEvent event) {
		setSelectedTab(PENDING_FINANCE_TAB);
		try {
			onSearchFinance(event);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void onEditSearchFinance(ActionEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(IFinanceConstants.FINANCE_LIST_CONTROLLER_NAME);
		financeList.onEditSearch(event);
        financeList.setCriteria(getAvailableFinancesCriteria());
	}

	public void onSearchFinance(ActionEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(IFinanceConstants.FINANCE_LIST_CONTROLLER_NAME);
		financeList.clearCriteria();
		financeList.getCriteria().addEqualExpression(financeList.getFieldName(IEntityAlias.FINANCE_PAYMENT), false);
		financeList.getCriteria().addEqualExpression(financeList.getFieldName(IEntityAlias.FINANCE_REGISTRY_ID), getAgency().getId());
		
		if(getStartDate()!=null){
			financeList.getCriteria().addGreaterThanOrEqualExpression(financeList.getFieldName(IEntityAlias.FINANCE_INVOICE_ISSUE_DATE), getStartDate());
		}
		if(getEndDate()!=null){
			financeList.getCriteria().addLessThanOrEqualExpression(financeList.getFieldName(IEntityAlias.FINANCE_INVOICE_ISSUE_DATE), getEndDate());
		}
		if (getPayMethod() != null && getPayMethod().getId() != null) {
			financeList.getCriteria().addEqualExpression(financeList.getFieldName(IEntityAlias.FINANCE_PAY_METHOD_ID), getPayMethod().getId());
		}
		for(AgencyFinance af: getFinanceList()){
			financeList.getCriteria().addNotEqualExpression(financeList.getFieldName(IEntityAlias.FINANCE_ID), af.getFinance().getId());
        }
		
		if(getHotel()!=null && getHotel().getWorkPlace().getId()!=null){
			financeList.getCriteria().addEqualExpression("Finance.invoice<lines.workPlace.id", getHotel().getWorkPlace().getId());
		}
		List<Integer> reservationIds =  getProjectReservationIds();
		if(!reservationIds.isEmpty()){
			financeList.getCriteria().addInExpression(financeList.getFieldName(IEntityAlias.FINANCE_INVOICE_PROJECT_ID), reservationIds);
		}
		
		financeList.onSearch(event);
	}
	
	private List<Integer> getProjectReservationIds() throws ManagerBeanException{
		List<Integer> list = new LinkedList<Integer>();
		IManagerBean bean = BeanManager.getManagerBean(ProjectReservation.class);		
		Criteria criteria = new Criteria();
		if (getReservationId()!=null) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ID), getReservationId());
		}
		if (StringUtils.isNotEmpty(getReservationCode())) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_CODE), getReservationCode());
		}
		if (StringUtils.isNotEmpty(getGuestName())) {
			criteria.addExpression(ExpressionUtilities.getLikeExpression("ProjectReservation.guests.name", "%"+getGuestName()+"%"));
		}
		if (StringUtils.isNotEmpty(getGuestSurname())) {
			criteria.addExpression(ExpressionUtilities.getLikeExpression("ProjectReservation.guests.surname", "%"+getGuestSurname()+"%"));
		}
		if(!criteria.isEmpty()){
			list.add(-1);
			for(ITransferObject to: bean.getList(criteria)){
				ProjectReservation reservation = (ProjectReservation) to;
				list.add(reservation.getId());
			}
		}
		return list;
	}
	
	private Criteria getAvailableFinancesCriteria() throws ManagerBeanException {

        FinanceListSearchListener financeSearch = (FinanceListSearchListener)AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_LIST_SEARCH_LISTENER_NAME);
		FinanceStatus[] financeStatuses = {FinanceStatus.PENDING, FinanceStatus.RETURNED};
		financeSearch.setFinanceStatuses(financeStatuses);

		FinanceListController financeList = (FinanceListController)FormUtil.getController(IFinanceConstants.FINANCE_LIST_CONTROLLER_NAME);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(financeList.getFieldName(IEntityAlias.FINANCE_PAYMENT), false);
        for(AgencyFinance af: getFinanceList()){
        	criteria.addNotEqualExpression(financeList.getFieldName(IEntityAlias.FINANCE_ID), af.getFinance().getId());
        }
        
        
    	criteria.addOrder(financeList.getFieldName(IEntityAlias.FINANCE_DUE_DATE));
        criteria.addOrder(financeList.getFieldName(IEntityAlias.FINANCE_CONCEPT));
		return criteria;
	}
	
	public void onAddSelected(ActionEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(IFinanceConstants.FINANCE_LIST_CONTROLLER_NAME);
        Iterator<Finance> iterator = financeList.getCheckedFinances().iterator();
        while(iterator.hasNext()){
        	Finance finance = iterator.next();
        	AgencyFinance af = new AgencyFinance();
        	af.setFinance(finance);
        	af.setAmount(finance.getTotalAmount());
        	getFinanceList().add(af);
        }
        onSearchFinance(event);
        financeList.clearCheckedFinances();
	}
	
	public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
		for(AgencyFinance af: getCheckedFinances()){
			getFinanceList().remove(af);
		}
		clearCheckedFinances();
		onSearchFinance(event);
		FinanceListController financeList = (FinanceListController)FormUtil.getController(IFinanceConstants.FINANCE_LIST_CONTROLLER_NAME);
		financeList.clearCheckedFinances();
	}

	public void searchFinanceList(ActionEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(IFinanceConstants.FINANCE_LIST_CONTROLLER_NAME);
//		financeList.onEditSearch(event);
        financeList.setCriteria(getAvailableFinancesCriteria());
        financeList.onSearch(event);
        financeList.clearCheckedFinances();
	}
	
	public void onAcceptFraction(ActionEvent event) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Finance.class);
		for(AgencyFinance af: getFinanceList()){
			double amount = af.getFinance().getAmount();
			Double diff = CommonUtil.round(af.getFinance().getAmount() - af.getAmount());
			if(diff != 0){
				Finance finance = af.getFinance();
				finance.setAmount(finance.getAmount() - diff);
				bean.update(finance);
				createFinanceTracking(finance, 1, amount);
				
				Finance fraction = createFractionFinance(finance, diff);
				bean.insert(fraction);
				createFinanceTracking(fraction, 2, amount);
			}
		}
		setFractioned(true);
	}
	
	private Finance createFractionFinance(Finance targetFinance, Double amount) {
		Finance finance = new Finance();
		finance.setPayment(targetFinance.isPayment());
		finance.setRegistry(targetFinance.getRegistry());
		finance.setRegistryName(targetFinance.getRegistryName());
		finance.setRegistryDocument(targetFinance.getRegistryDocument());
		finance.setRegistryDocumentType(targetFinance.getRegistryDocumentType());
		finance.setRegistryDocumentCountry(targetFinance.getRegistryDocumentCountry());
		finance.setAmount(amount);
		finance.setExpenses(0.0);
		finance.setConcept(targetFinance.getConcept());
		finance.setInvoice(targetFinance.getInvoice());
		finance.setDueDate(targetFinance.getDueDate());
		finance.setPayMethod(getPayMethod());
		finance.setBank(targetFinance.getBank());
		finance.setBankAccount(targetFinance.getBankAccount());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setSecurityLevel(targetFinance.getSecurityLevel());
		finance.setScope(targetFinance.getScope());
		return finance;
	}
	
	private void createFinanceTracking(Finance finance, int fractionNum, Double amount){
		String message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_TRACKING_FRACTIONED, fractionNum, 2);
		FinanceTrackingWriter.addFinanceTracking(finance, new Date(), FinanceTrackingType.FRACTIONED, message, amount);
	}
	
	public void onFinanceBatchShow(ActionEvent event) throws ManagerBeanException {
		setNewBatch(true);
		setFinanceBatch(createFinanceBatch());
	}

	public void onBatchModeChanged(ActionEvent event) {
		if (isNewBatch()) {
			setFinanceBatch(createFinanceBatch());
		}
	}

	private FinanceBatch createFinanceBatch() {
		String date = new SimpleDateFormat(AonUtil.getMessage(ICommonConstants.DEFAULT_BUNDLE, "aon_date_pattern")).format(new Date());
		FinanceBatch fBatch = new FinanceBatch();
		fBatch.setIssueDate(new Date());
		fBatch.setDescription(date + " " + getAgency().getRegistry().getFullName());
		return fBatch;
	}
	
	public void onFinanceBatch(ActionEvent event) throws ManagerBeanException {
		FinanceBatch financeBatch = getFinanceBatch();
		if (isNewBatch()) {
			IManagerBean fBatchBean = BeanManager.getManagerBean(FinanceBatch.class);
			financeBatch.setPayment(false);
			financeBatch.setFinanceBatchType(FinanceBatchType.NONE);
			financeBatch.setFinanceBatchStatus(FinanceBatchStatus.TODO);
			financeBatch.setConfidential(false);
			financeBatch.setSecurityLevel(SecurityLevel.OFFICIAL);
			financeBatch = (FinanceBatch)fBatchBean.insert(getFinanceBatch());
			setFinanceBatch(financeBatch);
		}

		if (financeBatch.getFinanceBatchStatus() == FinanceBatchStatus.TODO) {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
			for (AgencyFinance af : getFinanceList()) {
				Finance finance = (Finance)financeBean.get(af.getFinance().getId());
				if (finance.getFinanceStatus() == FinanceStatus.PENDING || finance.getFinanceStatus() == FinanceStatus.RETURNED) {
					FinanceBatchDetail fBatchDetail = new FinanceBatchDetail();
					fBatchDetail.setFinance(finance);
					fBatchDetail.setFinanceBatch(financeBatch);
					fBatchDetail.setAmount(finance.getTotalAmount());
					fBatchDetail.setStatus(FinanceStatus.BATCHED);
					fBatchDetailBean.insert(fBatchDetail);
				}
			}
		}

		onLoadFinanceBatch(event);
	}
	
	public void onLoadFinanceBatch(ActionEvent event) throws ManagerBeanException {
		if (getFinanceBatch() != null && getFinanceBatch().getId() != null) {
			onInit(event);
			BasicController controller = (BasicController)AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_BATCH_CONTROLLER_NAME);
			controller.onLoad(event, getFinanceBatch().getId(), "paymentCardSettle_list", "paymentCardSettle" + ".onSearch");
		}
	}

	private void batchFinances() {
//		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
//		boolean mustCloseSession = HibernateUtil.mustCloseSession();
//		String sessionName = HibernateUtil.getSessionFactoryName(FinancePaymentPrintController.class.getName());
//		try {
//			IManagerBean bean = BeanManager.getManagerBean(Finance.class);
//
//			HibernateUtil.setBeginTransaction(false);
//			HibernateUtil.setCloseSession(false);
//			HibernateUtil.beginTransaction(sessionName);
//
//			for(Finance finance: getCheckedFinances()){
//				finance.setFinanceStatus(FinanceStatus.BATCHED);
//				bean.update(finance);
//				createFinanceTracking(finance);
//			}
//
//			HibernateUtil.getSession(sessionName).flush();
//			HibernateUtil.commitTransaction(sessionName);
//		} catch (Exception e) {
//			try {
//				HibernateUtil.rollbackTransaction(sessionName);
//			} catch (DAOException daoe) {
//				String msg =  "Unable to rollback transaction!";
//				throw new AbortProcessingException(msg);
//			}
//			String msg =  "Error batching finance. " + e.getMessage();
//			AonUtil.addErrorMessage(msg);
//			throw new AbortProcessingException(msg);
//		} finally {
//			HibernateUtil.closeSession(sessionName);
//			HibernateUtil.setCloseSession(mustCloseSession);
//			HibernateUtil.setBeginTransaction(mustBeginTransaction);
//		}
	}
	
	public class AgencyFinance{
		private Finance finance;
		private Double amount;
		public Finance getFinance() {
			return finance;
		}
		public void setFinance(Finance finance) {
			this.finance = finance;
		}
		public Double getAmount() {
			return amount;
		}
		public void setAmount(Double amount) {
			if(NumberUtils.compare(finance.getAmount(),amount) < 0){
				AonUtil.addErrorMessage("No se permite introducir un valor superior al del vencimiento.");
				this.amount = finance.getAmount();
			} else {
				this.amount = amount;
			}
		}
	}
		
}