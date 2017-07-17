package com.esferalia.aon.ui.pms.controller;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_TRACKING_FRACTIONED;
import static com.code.aon.ui.common.ICommonMessages.PRICE_PATTERN;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.PayMethod;
import com.code.aon.customer.Customer;
import net.aonsolutions.core.dbutils.AonSQLException;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;

public class PaymentCardSettleController extends DataScrollerState implements ISQLConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final PayMethod EMPTY_PAYMETHOD = new PayMethod();

	private final String PAYMENT_CARD_FINANCE_TAB = "PaymentCardFinanceTab";
	private final String PENDING_FINANCE_TAB = "PendingFinanceTab";
	private String selectedTab;

	private List<Customer> agencies;
	private Customer agency;
	private Hotel hotel;
	private List<String> referenceCodes;
	private String referenceCode;
	private Date issueDateFrom;
	private Date issueDateTo;
	private Date dueDateFrom;
	private Date dueDateTo;
	private PayMethod[] payMethods;
	private Double amount;
	private Integer reservationId;
	private List<String> reservationCodes;
	private String reservationCode;
	private String guestName;
	private String guestSurname;

	private ArrayList<PaymentCardFinance> financeChecks = new ArrayList<PaymentCardFinance>();
	private List<PaymentCardFinance> paymentCardFinances;
	private DataScrollerState paymentCardState;
	
	private PayMethod fractionPayMethod;
	private boolean newBatch;
	private FinanceBatch getFinanceBatch;

	private boolean showFinanceSearchWindow;
	private boolean showFinanceFractionWindow;
	private boolean showFinanceBatchWindow;

	public String getPaymentCardFinancesTabName() {
		return PAYMENT_CARD_FINANCE_TAB;
	}

	public String getPendingFinancesTabName() {
		return PENDING_FINANCE_TAB;
	}

	public String getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public List<Customer> getAgencies() {
		if (agencies == null) {
			agencies = new LinkedList<Customer>();
		}
		return agencies;
	}
	public void setAgencies(List<Customer> agencies) {
		this.agencies = agencies;
	}
	public int getAgenciesSize() {
		return getAgencies().size();
	}
	private String getAgencyIds() {
		List<Integer> agencyIds = new LinkedList<Integer>();
		for (Customer agency : getAgencies()) {
			agencyIds.add(agency.getId());
		}
		return StringUtils.join(agencyIds, ",");
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

	public List<String> getReferenceCodes() {
		if(referenceCodes == null){
			referenceCodes = new LinkedList<String>();
		}
		return referenceCodes;
	}
	public void setReferenceCodes(List<String> referenceCodes) {
		this.referenceCodes = referenceCodes;
	}
	public int getReferenceCodesSize() {
		return getReferenceCodes().size();
	}
	private String getReferenceCodeList() {
		List<String> codes = new LinkedList<String>();
		for (String code : getReferenceCodes()) {
			codes.add("'"+code+"'");
		}
		return StringUtils.join(codes, ",");
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public Date getIssueDateFrom() {
		return issueDateFrom;
	}
	public void setIssueDateFrom(Date issueDateFrom) {
		this.issueDateFrom = issueDateFrom;
	}

	public Date getIssueDateTo() {
		return issueDateTo;
	}
	public void setIssueDateTo(Date issueDateTo) {
		this.issueDateTo = issueDateTo;
	}

	public Date getDueDateFrom() {
		return dueDateFrom;
	}
	public void setDueDateFrom(Date dueDateFrom) {
		this.dueDateFrom = dueDateFrom;
	}

	public Date getDueDateTo() {
		return dueDateTo;
	}
	public void setDueDateTo(Date dueDateTo) {
		this.dueDateTo = dueDateTo;
	}

	public PayMethod[] getPayMethods() {
		if (payMethods == null) {
			payMethods = new PayMethod[]{EMPTY_PAYMETHOD};
		}	
		return payMethods;
	}
	public void setPayMethods(PayMethod[] payMethods) {
		this.payMethods = payMethods;
	}
	public int getPayMethodsSize() {
		return getPayMethods().length;
	}
	private String getPayMethodIds() {
		List<Integer> payMethodIds = new LinkedList<Integer>();
		for (PayMethod payMethod : getPayMethods()) {
			if (payMethod != null && payMethod.getId() != null) {
				payMethodIds.add(payMethod.getId());
			}
		}
		return StringUtils.join(payMethodIds, ",");
	}

	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Integer getReservationId() {
		return reservationId;
	}
	public void setReservationId(Integer reservationId) {
		this.reservationId = reservationId;
	}

	public List<String> getReservationCodes() {
		if(reservationCodes == null){
			reservationCodes = new LinkedList<String>();
		}
		return reservationCodes;
	}
	public void setReservationCodes(List<String> reservationCodes) {
		this.reservationCodes = reservationCodes;
	}
	public int getReservationCodesSize() {
		return getReservationCodes().size();
	}
	private String getReservationCodeList() {
		List<String> codes = new LinkedList<String>();
		for (String code : getReservationCodes()) {
			codes.add("'"+code+"'");
		}
		return StringUtils.join(codes, ",");
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

	public List<PaymentCardFinance> getPaymentCardFinances() {
		if (paymentCardFinances == null) {
			paymentCardFinances = new LinkedList<PaymentCardFinance>();
		}
		return paymentCardFinances;
	}
	public void setPaymentCardFinances(List<PaymentCardFinance> paymentCardFinances) {
		this.paymentCardFinances = paymentCardFinances;
	}
	public int getPaymentCardFinancesSize() {
		return getPaymentCardFinances().size();
	}
	private String getPaymentCardFinanceIds() {
		List<Integer> paymentCardFinanceIds = new LinkedList<Integer>();
		for (PaymentCardFinance paymentCardFinance : getPaymentCardFinances()) {
			paymentCardFinanceIds.add(paymentCardFinance.getFinanceId());
		}
		return StringUtils.join(paymentCardFinanceIds, ",");
	}

	public DataScrollerState getPaymentCardState() {
		if (paymentCardState == null) {
			paymentCardState = new DataScrollerState(new SerializableListDataModel(getPaymentCardFinances()), "paymentCardState");
		}
		return paymentCardState;
	}
	public void setPaymentCardState(DataScrollerState paymentCardState) {
		this.paymentCardState = paymentCardState;
	}
	public DataModel getPaymentCardModel() {
		return getPaymentCardState().getDirectModel();
	}

	public PayMethod getFractionPayMethod() {
		return fractionPayMethod;
	}
	public void setFractionPayMethod(PayMethod fractionPayMethod) {
		this.fractionPayMethod = fractionPayMethod;
	}

	public boolean isNewBatch() {
		return newBatch;
	}
	public void setNewBatch(boolean newBatch) {
		this.newBatch = newBatch;
	}

	public FinanceBatch getFinanceBatch() {
		return getFinanceBatch;
	}
	public void setFinanceBatch(FinanceBatch financeBatch) {
		this.getFinanceBatch = financeBatch;
	}

	public boolean isShowFinanceSearchWindow() {
		return showFinanceSearchWindow;
	}
	public void setShowFinanceSearchWindow(boolean showFinanceSearchWindow) {
		this.showFinanceSearchWindow = showFinanceSearchWindow;
	}

	public boolean isShowFinanceFractionWindow() {
		return showFinanceFractionWindow;
	}
	public void setShowFinanceFractionWindow(boolean showFinanceFractionWindow) {
		this.showFinanceFractionWindow = showFinanceFractionWindow;
	}

	public boolean isShowFinanceBatchWindow() {
		return showFinanceBatchWindow;
	}
	public void setShowFinanceBatchWindow(boolean showFinanceBatchWindow) {
		this.showFinanceBatchWindow = showFinanceBatchWindow;
	}

	public void onInit(ActionEvent event) {
		try {
			init();
			resetPaymentCardState();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
	}

	private void init() throws ManagerBeanException{
		setAgencies(null);
		setAgency((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		resetOptionalSearchParams();
	}

	private void resetOptionalSearchParams() {
		setHotel(null);
		setReferenceCodes(null);
		setReferenceCode(null);
		setIssueDateFrom(null);
		setIssueDateTo(null);
		setDueDateFrom(null);
		setDueDateTo(null);
		setPayMethods(null);
		setAmount(null);
		setReservationId(null);
		setReservationCodes(null);
		setReservationCode(null);
		setGuestName(null);
		setGuestSurname(null);
	}

	private void resetPaymentCardState() {
		setPaymentCardFinances(null);
		setPaymentCardState(null);
	}

	public void onAddAgency(ActionEvent event) {
		if (!getAgencies().contains(getAgency())) {
			getAgencies().add(getAgency());
		}
		setAgency(null);
	}

	public void onRemoveAgency(ActionEvent event) throws ManagerBeanException {
		FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("agencyIdx"));
		getAgencies().remove(index);
	}

	public void onAddPayMethod(ActionEvent event) {
		setPayMethods((PayMethod[])ArrayUtils.add(getPayMethods(), EMPTY_PAYMETHOD));
	}

	public void onRemovePayMethod(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("payMethodIdx"));
		setPayMethods((PayMethod[])ArrayUtils.remove(getPayMethods(), index));
		if (ArrayUtils.isEmpty(this.payMethods)) {
			setPayMethods(new PayMethod[]{EMPTY_PAYMETHOD});
		}
	}

	public void onAddReferenceCode(ActionEvent event) {
		if (!getReferenceCodes().contains(getReferenceCode())) {
			getReferenceCodes().add(getReferenceCode());
		}
		setReferenceCode(null);
	}
	
	public void onRemoveReferenceCode(ActionEvent event) throws ManagerBeanException {
		FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("referenceCodeIdx"));
		getReferenceCodes().remove(index);
	}

	public void onAddReservationCode(ActionEvent event) {
		if (!getReservationCodes().contains(getReservationCode())) {
			getReservationCodes().add(getReservationCode());
		}
		setReservationCode(null);
	}
	
	public void onRemoveReservationCode(ActionEvent event) throws ManagerBeanException {
		FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("reservationCodeIdx"));
		getReservationCodes().remove(index);
	}
	
	public void onClear(ActionEvent event) {
		resetOptionalSearchParams();
	}

	public void onSearch(ActionEvent event) {
		setSelectedTab(PENDING_FINANCE_TAB);
		try {
			if (getAgency() != null && getAgency().getId() != null && !getAgencies().contains(getAgency())) {
				getAgencies().add(getAgency());
			}
			if (StringUtils.isNotBlank(getReferenceCode()) && !getReferenceCodes().contains(getReferenceCode())) {
				getReferenceCodes().add(getReferenceCode());
			}
			if (StringUtils.isNotBlank(getReservationCode()) && !getReservationCodes().contains(getReservationCode())) {
				getReservationCodes().add(getReservationCode());
			}
			onSearchFinances();
		} catch (AonSQLException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
	}

	private void onSearchFinances() throws AonSQLException {
		clearCheckedFinances();
		setModel(new SerializableListDataModel(getFinanceList()));
	}

	private List<PaymentCardFinance> getFinanceList() throws AonSQLException {
		Connection connection = null;
		PreparedStatement financeStmt = null;
		ResultSet financeRs = null;
		try {
			List<PaymentCardFinance> financeList = new LinkedList<PaymentCardFinance>();

			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			financeStmt = connection.prepareStatement(getFinanceSQL());
			int i = 0;
			if (getIssueDateFrom() != null) {
				SQLUtils.setDate(financeStmt, ++i, getIssueDateFrom());
			}
			if (getIssueDateTo() != null) {
				SQLUtils.setDate(financeStmt, ++i, getIssueDateTo());
			}
			if (getDueDateFrom() != null) {
				SQLUtils.setDate(financeStmt, ++i, getDueDateFrom());
			}
			if (getDueDateTo() != null) {
				SQLUtils.setDate(financeStmt, ++i, getDueDateTo());
			}
			financeRs = financeStmt.executeQuery();
			while (financeRs.next()) {
				PaymentCardFinance paymentCardFinance = new PaymentCardFinance();
				paymentCardFinance.setFinanceId(financeRs.getInt(FINANCE));
				paymentCardFinance.setFinanceDate(financeRs.getDate(DUE_DATE));
				paymentCardFinance.setAgency(financeRs.getString(AGENCY));
				paymentCardFinance.setPayMethod(financeRs.getString(PAY_METHOD));
				paymentCardFinance.setAmount(financeRs.getDouble(AMOUNT));
				paymentCardFinance.setExpenses(financeRs.getDouble(EXPENSES));
				paymentCardFinance.setInvoiceId(financeRs.getInt(INVOICE));
				paymentCardFinance.setInvoiceDate(financeRs.getDate(ISSUE_DATE));
				paymentCardFinance.setInvoiceCode(financeRs.getString(REFERENCE_CODE));
				paymentCardFinance.setRectificationType(RectificationType.values()[financeRs.getInt(RECTIFICATION_TYPE)]);
				paymentCardFinance.setRectificationInvoiceCode(financeRs.getString(RECTIFICATION_INVOICE));
				paymentCardFinance.setReservationId(financeRs.getInt(RESERVATION));
				paymentCardFinance.setReservationCode(financeRs.getString(CODE));
				paymentCardFinance.setReservationStartDate(financeRs.getDate(START_DATE));
				paymentCardFinance.setReservationEndDate(financeRs.getDate(END_DATE));
				paymentCardFinance.setReservationHotel(financeRs.getString(HOTEL));
				paymentCardFinance.setReservationGuest(financeRs.getString(GUEST));
				financeList.add(paymentCardFinance);
			}

			return financeList;
		} catch (ManagerBeanException e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e.getMessage());
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(financeRs);
			SQLUtils.closeQuietly(financeStmt);
			SQLUtils.closeQuietly(connection);
		}
	}

	private String getFinanceSQL() throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT F.id AS " + FINANCE + ", F.due_date AS " + DUE_DATE + ", F.rname AS " + AGENCY + ", PM.name AS " + PAY_METHOD);
		stmt.append(", F.amount AS " + AMOUNT + ", F.expenses AS " + EXPENSES + ", I.id AS " + INVOICE + ", I.issue_date AS " + ISSUE_DATE);
		stmt.append(", IFNULL(I.reference_code, F.concept) AS " + REFERENCE_CODE + ", I.rectification_type AS " + RECTIFICATION_TYPE);
		stmt.append(", (SELECT I2.reference_code AS " + RECTIFICATION_INVOICE + " FROM invoice as I2");
		stmt.append("     WHERE I.rectification_invoice IS NOT NULL AND I.rectification_invoice = I2.id) AS " + RECTIFICATION_INVOICE);
		stmt.append(", PR.project AS " + RESERVATION + ", PR.code AS " + CODE + ", PR.start_date AS " + START_DATE + ", PR.end_date AS " + END_DATE);
		stmt.append(", W.description AS " + HOTEL + ", CONCAT(PRG.name, ' ', PRG.surname) AS " + GUEST);
		stmt.append(" FROM finance AS F");
		stmt.append(" LEFT JOIN pay_method AS PM ON F.pay_method = PM.id");
		stmt.append(" LEFT JOIN invoice AS I ON F.invoice = I.id");
		stmt.append(" LEFT JOIN project_reservation AS PR ON I.project = PR.project");
		stmt.append(" LEFT JOIN hotel AS H ON PR.hotel_reservation = H.id");
		stmt.append(" LEFT JOIN workplace AS W ON H.workplace = W.id");
		stmt.append(" LEFT JOIN project_reservation_guest AS PRG ON PR.project = PRG.project_reservation AND PRG.guest_index = 1");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("F.domain"));
		stmt.append(" AND F.status IN (" + FinanceStatus.PENDING.ordinal() + ", " + FinanceStatus.RETURNED.ordinal() + ")");
		stmt.append(" AND F.payment = 0");
		stmt.append(" AND I.service = 0");
		stmt.append(" AND F.registry IN (" + getAgencyIds() + ")");
		stmt.append(" AND H.id IN (" + getHotelIds() + ")");
		if (getReferenceCodes()!=null && getReferenceCodes().size()>0) {
			stmt.append(" AND I.reference_code IN (" + getReferenceCodeList() + ")");
		}
		if (getIssueDateFrom() != null) {
			stmt.append(" AND I.issue_date >= ?");
		}
		if (getIssueDateTo() != null) {
			stmt.append(" AND I.issue_date <= ?");
		}
		if (getDueDateFrom() != null) {
			stmt.append(" AND F.due_date >= ?");
		}
		if (getDueDateTo() != null) {
			stmt.append(" AND F.due_date <= ?");
		}
		if (getPayMethodsSize() > 0) {
			String payMethodIds = getPayMethodIds();
			if (StringUtils.isNotBlank(payMethodIds)) {
				stmt.append(" AND F.pay_method IN (" + payMethodIds + ")");
			}
		}
		if (getAmount() != null) {
			stmt.append(" AND F.amount = " + getAmount());
		}
		if (getReservationId() != null) {
			stmt.append(" AND PR.project = " + getReservationId());
		}
		if (getReservationCodes()!=null && getReservationCodes().size()>0) {
			stmt.append(" AND PR.code IN (" + getReservationCodeList() + ")");
		}
		if (StringUtils.isNotBlank(getGuestName())) {
			stmt.append(" AND PRG.name LIKE '%" + getGuestName() + "%'");
		}
		if (StringUtils.isNotBlank(getGuestSurname())) {
			stmt.append(" AND PRG.surname LIKE '%" + getGuestSurname() + "%'");
		}
		if (getPaymentCardFinancesSize() > 0) {
			stmt.append(" AND F.id NOT IN (" + getPaymentCardFinanceIds() + ")");
		}
		stmt.append(" ORDER BY " + DUE_DATE + ", " + REFERENCE_CODE);

		return stmt.toString();
	}

	private String getHotelIds() throws ManagerBeanException {
		String hotelIds = "";
		if (getHotel() != null) {
			hotelIds = getHotel().getId().toString();
		} else {
			PmsCollectionsController collectionsController = (PmsCollectionsController)AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
			hotelIds = StringUtils.join(collectionsController.getCurrentUserHotelIds(), ",");
		}
		return hotelIds;
	}

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	public boolean getRowChecked() {
		PaymentCardFinance to = (PaymentCardFinance)getDirectModel().getRowData();
		return financeChecks.contains(to);
	}
	
	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			PaymentCardFinance to = (PaymentCardFinance)getDirectModel().getRowData();
			if (!financeChecks.contains(to)) {
				financeChecks.add(to);
			}
		} else {
			PaymentCardFinance to = (PaymentCardFinance)getDirectModel().getRowData();
			if (financeChecks.contains(to)) {
				financeChecks.remove(to);
			}
		}
	}
	
	public ArrayList<PaymentCardFinance> getCheckedFinances() {
		return financeChecks;
	}
	
	public void clearCheckedFinances() {
		financeChecks = new ArrayList<PaymentCardFinance>();
	}
	
	public void checkAll(ActionEvent event) throws AonSQLException {
		for (PaymentCardFinance paymentCardFinance : getFinanceList()) {
			if (!financeChecks.contains(paymentCardFinance)) {
				financeChecks.add(paymentCardFinance);
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedFinances();
	}

	public int getCheckedCount() {
		return getCheckedFinances().size();
	}

	public void onAddFinancesToCard(ActionEvent event) throws AonSQLException {
		for (PaymentCardFinance paymentCardFinance : getCheckedFinances()) {
			if (!getPaymentCardFinances().contains(paymentCardFinance)) {
				paymentCardFinance.setNewAmount(paymentCardFinance.getTotalAmount());
				getPaymentCardFinances().add(paymentCardFinance);
			}
		}
        onSearchFinances();
	}

	public void onRemoveFinanceFromCard(ActionEvent event) throws AonSQLException {
		PaymentCardFinance paymentCardFinance = (PaymentCardFinance)getPaymentCardModel().getRowData();
		getPaymentCardFinances().remove(paymentCardFinance);
		onSearchFinances();
	}

	public void onRemoveAllFinancesFromCard(ActionEvent event) throws AonSQLException {
		getPaymentCardFinances().clear();
		onSearchFinances();
	}

	public Double getCardFinanceTotalAmount() {
		Double total = 0.0;
		for (PaymentCardFinance paymentCardFinance : getPaymentCardFinances()) {
			total = CommonUtil.round(total + paymentCardFinance.getTotalAmount());
		}
		return total;
	}

	public Double getCardFinanceTotalNewAmount() {
		Double total = 0.0;
		for (PaymentCardFinance paymentCardFinance : getPaymentCardFinances()) {
			total = CommonUtil.round(total + paymentCardFinance.getNewAmount());
		}
		return total;
	}
	
	public Double getCardFinanceTotalDifference() {
		return CommonUtil.round(getCardFinanceTotalNewAmount() - getCardFinanceTotalAmount());
	}

	public boolean isExistAmountDifferences(){
		for (PaymentCardFinance paymentCardFinance : getPaymentCardFinances()) {
			if (paymentCardFinance.getTotalAmount() != paymentCardFinance.getNewAmount()) {
				return true;
			}
		}
		return false;
	}

	public void onFractionDifferences(ActionEvent event) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		for (PaymentCardFinance paymentCardFinance : getPaymentCardFinances()) {
			Double difference = CommonUtil.round(paymentCardFinance.getTotalAmount() - paymentCardFinance.getNewAmount());
			if (difference != 0) {
				Finance finance = (Finance)financeBean.get(paymentCardFinance.getFinanceId());
				double sourceAmount = finance.getTotalAmount();
				double newAmount = CommonUtil.round(finance.getAmount() - difference);
				finance.setAmount(newAmount);
				finance = (Finance)financeBean.update(finance);
				createFinanceTracking(finance, 1, sourceAmount);

				paymentCardFinance.setAmount(finance.getAmount());
				paymentCardFinance.setNewAmount(finance.getAmount());

				Finance fraction = createFractionFinance(finance, difference);
				fraction = (Finance)financeBean.insert(fraction);
				createFinanceTracking(fraction, 2, sourceAmount);
			}
		}
		setFractionPayMethod(null);
	}

	private Finance createFractionFinance(Finance finance, Double amount) {
		finance.setId(null);
		finance.setAmount(amount);
		finance.setExpenses(0.0);
		finance.setPayMethod(getFractionPayMethod());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		return finance;
	}

	private void createFinanceTracking(Finance finance, int fractionNumber, Double amount) {
		String message = AonUtil.getMessage(FINANCE_TRACKING_FRACTIONED, fractionNumber, 2);
		FinanceTrackingWriter.addFinanceTracking(finance, new Date(), FinanceTrackingType.FRACTIONED, message, amount);
	}

	public void onFinanceBatchShow(ActionEvent event) throws ManagerBeanException {
		setNewBatch(true);
		createFinanceBatch();
	}

	private void createFinanceBatch() {
		setFinanceBatch(new FinanceBatch());
		getFinanceBatch().setDescription(obtainFBatchDescription());
		getFinanceBatch().setIssueDate(new Date());
	}

	private String obtainFBatchDescription() {
		String bank = "";
		if (getFinanceBatch().getRegistryBank() != null && getFinanceBatch().getRegistryBank().getId() != null) {
			bank = getFinanceBatch().getRegistryBank().getAlias();
		}
		String agency = getAgencies().get(0).getRegistry().getFullName();
		if ((bank+agency).length() > 32) {
			if (bank.length() > 10) {
				bank = StringUtils.substring(bank, 0, 10);
			}
			if (agency.length() > 21) {
				agency = StringUtils.substring(agency, 0, 21);
			}
		}
		return bank + " " + agency;
	}
	
	public void onBatchModeChanged(ActionEvent event) {
		if (isNewBatch()) {
			createFinanceBatch();
		}
	}

	public void onFinanceBatchBankChange(ActionEvent event) throws ManagerBeanException {
		getFinanceBatch().setDescription(obtainFBatchDescription());
	}

	public List<SelectItem> getFinanceBatchList() throws ManagerBeanException {
		IManagerBean fBatchBean = BeanManager.getManagerBean(FinanceBatch.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_PAYMENT), false);
		criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_FINANCE_BATCH_STATUS), FinanceBatchStatus.TODO);
		criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_FINANCE_BATCH_TYPE), FinanceBatchType.NONE);
		if (getIssueDateTo() != null){
			criteria.addGreaterThanOrEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_ISSUE_DATE), getIssueDateTo());
		}
		criteria.addOrder(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_ID), false);
		List<SelectItem> fBatchList = new LinkedList<SelectItem>();
		for (ITransferObject ito : fBatchBean.getList(criteria)) {
			FinanceBatch fBatch = (FinanceBatch)ito;
			String amount = new DecimalFormat(AonUtil.getMessage(PRICE_PATTERN)).format(fBatch.getFinanceBatchTotalAmount());

			SelectItem item = new SelectItem(fBatch, fBatch.getId() + " - " + fBatch.getDescription() + StringUtils.leftPad(amount, 50 - fBatch.getDescription().length()-amount.length(), "·") + "EUR.");
			fBatchList.add(item);
		}
		return fBatchList;
	}

	public void onFinanceBatch(ActionEvent event) throws ManagerBeanException {
		if (isNewBatch()) {
			IManagerBean fBatchBean = BeanManager.getManagerBean(FinanceBatch.class);
			getFinanceBatch().setPayment(false);
			getFinanceBatch().setFinanceBatchType(FinanceBatchType.NONE);
			getFinanceBatch().setFinanceBatchStatus(FinanceBatchStatus.TODO);
			getFinanceBatch().setConfidential(false);
			getFinanceBatch().setSecurityLevel(SecurityLevel.OFFICIAL);
			setFinanceBatch((FinanceBatch)fBatchBean.insert(getFinanceBatch()));
		}

		if (getFinanceBatch().getFinanceBatchStatus() == FinanceBatchStatus.TODO) {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
			for (PaymentCardFinance paymentCardFinance : getPaymentCardFinances()) {
				Finance finance = (Finance)financeBean.get(paymentCardFinance.getFinanceId());
				if (finance.isPending() || finance.isReturned()) {
					FinanceBatchDetail fBatchDetail = new FinanceBatchDetail();
					fBatchDetail.setFinanceBatch(getFinanceBatch());
					fBatchDetail.setFinance(finance);
					fBatchDetail.setAmount(finance.getTotalAmount());
					fBatchDetail.setStatus(FinanceStatus.BATCHED);
					fBatchDetailBean.insert(fBatchDetail);
				}
			}
		}
		getPaymentCardFinances().clear();
		onLoadFinanceBatch(event);
	}

	public void onLoadFinanceBatch(ActionEvent event) throws ManagerBeanException {
		if (getFinanceBatch() != null && getFinanceBatch().getId() != null) {
			BasicController controller = (BasicController)AonUtil.getRegisteredBean(IPmsConstants.FINANCE_BATCH_CONTROLLER_NAME);
			controller.onLoad(event, getFinanceBatch().getId(), IPmsConstants.PAYMENT_CARD_SETTLE_LIST_NAME, null);
		}
	}

	public void onLoadInvoice(ActionEvent event) throws ManagerBeanException {
		PaymentCardFinance paymentCardFinance = null;
		if (getSelectedTab().equals(PAYMENT_CARD_FINANCE_TAB)) {
			paymentCardFinance = (PaymentCardFinance)getPaymentCardModel().getRowData();
		} else if (getSelectedTab().equals(PENDING_FINANCE_TAB)) {
			paymentCardFinance = (PaymentCardFinance)getModel().getRowData();
		}
		BasicController invoiceController = (BasicController)AonUtil.getRegisteredBean(IPmsConstants.SALE_INVOICE_CONTROLLER_NAME);
		invoiceController.onLoad(event, paymentCardFinance.getInvoiceId(), IPmsConstants.PAYMENT_CARD_SETTLE_LIST_NAME, null);
	}

	public void onLoadReservation(ActionEvent event) throws ManagerBeanException {
		PaymentCardFinance paymentCardFinance = null;
		if (getSelectedTab().equals(PAYMENT_CARD_FINANCE_TAB)) {
			paymentCardFinance = (PaymentCardFinance)getPaymentCardModel().getRowData();
		} else if (getSelectedTab().equals(PENDING_FINANCE_TAB)) {
			paymentCardFinance = (PaymentCardFinance)getModel().getRowData();
		}
		BasicController reservationController = (BasicController)AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_CONTROLLER_NAME);
		reservationController.onLoad(event, paymentCardFinance.getReservationId(), IPmsConstants.PAYMENT_CARD_SETTLE_LIST_NAME, null);
	}


	public static class PaymentCardFinance implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private Integer financeId;
		private Date financeDate;
		private String agency;
		private String payMethod;
		private double amount;
		private double expenses;
		private Integer invoiceId;
		private Date invoiceDate;
		private String invoiceCode;
		private RectificationType rectificationType;
		private String rectificationInvoiceCode;
		private Integer reservationId;
		private String reservationCode;
		private Date reservationStartDate;
		private Date reservationEndDate;
		private String reservationHotel;
		private String reservationGuest;
		private Double newAmount;

		public Integer getFinanceId() {
			return financeId;
		}

		public void setFinanceId(Integer financeId) {
			this.financeId = financeId;
		}

		public Date getFinanceDate() {
			return financeDate;
		}
		public void setFinanceDate(Date financeDate) {
			this.financeDate = financeDate;
		}

		public String getAgency() {
			return agency;
		}
		public void setAgency(String agency) {
			this.agency = agency;
		}
		public int getAgencyLength() {
			return agency.length();
		}

		public String getPayMethod() {
			return payMethod;
		}
		public void setPayMethod(String payMethod) {
			this.payMethod = payMethod;
		}
		public int getPayMethodLength() {
			return payMethod.length();
		}

		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}

		public double getExpenses() {
			return expenses;
		}
		public void setExpenses(double expenses) {
			this.expenses = expenses;
		}

		public Integer getInvoiceId() {
			return invoiceId;
		}
		public void setInvoiceId(Integer invoiceId) {
			this.invoiceId = invoiceId;
		}

		public Date getInvoiceDate() {
			return invoiceDate;
		}
		public void setInvoiceDate(Date invoiceDate) {
			this.invoiceDate = invoiceDate;
		}

		public String getInvoiceCode() {
			return invoiceCode;
		}
		public void setInvoiceCode(String invoiceCode) {
			this.invoiceCode = invoiceCode;
		}

		public RectificationType getRectificationType() {
			return rectificationType;
		}
		public void setRectificationType(RectificationType rectificationType) {
			this.rectificationType = rectificationType;
		}

		public String getRectificationInvoiceCode() {
			return rectificationInvoiceCode;
		}
		public void setRectificationInvoiceCode(String rectificationInvoiceCode) {
			this.rectificationInvoiceCode = rectificationInvoiceCode;
		}

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
		public int getReservationCodeLength() {
			return reservationCode.length();
		}

		public Date getReservationStartDate() {
			return reservationStartDate;
		}
		public void setReservationStartDate(Date reservationStartDate) {
			this.reservationStartDate = reservationStartDate;
		}

		public Date getReservationEndDate() {
			return reservationEndDate;
		}
		public void setReservationEndDate(Date reservationEndDate) {
			this.reservationEndDate = reservationEndDate;
		}

		public String getReservationHotel() {
			return reservationHotel;
		}
		public void setReservationHotel(String reservationHotel) {
			this.reservationHotel = reservationHotel;
		}
		public int getReservationHotelLength() {
			return reservationHotel.length();
		}

		public String getReservationGuest() {
			return reservationGuest;
		}
		public void setReservationGuest(String reservationGuest) {
			this.reservationGuest = reservationGuest;
		}
		public int getReservationGuestLength() {
			return reservationGuest.length();
		}

		public double getNewAmount() {
			return newAmount;
		}
		public void setNewAmount(double newAmount) {
			this.newAmount = newAmount;
		}

		public double getTotalAmount() {
			return CommonUtil.round(amount + expenses);
		}

		public boolean isRectifier() {
			return (getRectificationType() == RectificationType.NORMAL_RECTIFIER || getRectificationType() == RectificationType.SPECIAL_RECTIFIER);
		}
		public boolean isRectified() {
			return (getRectificationType() == RectificationType.RECTIFIED);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			final PaymentCardFinance o = (PaymentCardFinance)obj;
			return o.getFinanceId().equals(getFinanceId());
		}

	}

}