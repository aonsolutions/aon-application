package com.code.aon.ui.accounting.controller;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountBudget;
import com.code.aon.accounting.AccountBudgetDetail;
import com.code.aon.accounting.Period;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class AccountBudgetController extends BasicController {

	private static final Logger LOGGER = Logger.getLogger(AccountBudgetController.class.getName());
	private Account account;
	private Month month;
	private Period period;
	private ArrayList<AccountBudgetDetail> valueList;
	private Double credit;
	private Double debit;
	private Double creditTotal;
	private Double debitTotal;
	private List<Double> debitList;
	private List<Double> creditList;	
	
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public Double getCredit() {
		return credit;
	}

	public void setCredit(Double credit) {
		this.credit = credit;
	}

	public Double getDebit() {
		return debit;
	}

	public void setDebit(Double debit) {
		this.debit = debit;
	}
	
	public ArrayList<AccountBudgetDetail> getValueList() {
		return valueList;
	}

	public void setValueList(ArrayList<AccountBudgetDetail> valueList) {
		this.valueList = valueList;
	}
	
	public Double getCreditTotal() {
		return creditTotal;
	}

	public void setCreditTotal(Double creditTotal) {
		this.creditTotal = creditTotal;
	}

	public Double getDebitTotal() {
		return debitTotal;
	}

	public void setDebitTotal(Double debitTotal) {
		this.debitTotal = debitTotal;
	}
	
	public List<Double> getDebitList() {
		if(debitList==null)
			debitList = new ArrayList<Double>();
		return debitList;
	}

	public void setDebitList(List<Double> debitList) {
		this.debitList = debitList;
	}

	public List<Double> getCreditList() {
		if(creditList==null)
			creditList = new ArrayList<Double>();
		return creditList;
	}

	public void setCreditList(List<Double> creditList) {
		this.creditList = creditList;
	}
	
	/**
	 * Devuelve un array de nombres de mes
	 * @return
	 */
	public Month[] getMonthNames(){
		return Month.values();
	}

	/**
	 * Construye la lista con las nuevas lineas del presupuesto.
	 * De ésta se obtienen los valores para insertar en la bd. 
	 */
	public void buildDetailValueList(){
		AccountBudgetDetail detail = new AccountBudgetDetail();
		detail.setAccountBudget((AccountBudget)getTo());
		valueList=new ArrayList<AccountBudgetDetail>();
		creditTotal=new Double(0.0);
		debitTotal=new Double(0.0);
		for(int i=0;i<Month.values().length;i++){
			if(credit!=null){
				detail.setCredit(getCredit());
				creditTotal+=credit;
			} else {
				detail.setCredit(0.0);
			}
			if(debit!=null){
				detail.setDebit(getDebit());
				debitTotal+=debit;
			} else {
				detail.setDebit(0.0);
			}
			valueList.add(i, detail);
		}
	}
	
	/**
	 * Inicia una transaccion insertando, con sus correspondientes valores debe y haber,
	 * una nueva fila por cada mes en la tabla account_budget_detail
	 * @throws ManagerBeanException
	 */
	private void insertBudgetDetail() {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, Integer.parseInt(((AccountBudget)getTo()).getPeriod()));
				cal.set(Calendar.DAY_OF_MONTH, 1);
				IManagerBean detailBean = BeanManager.getManagerBean(AccountBudgetDetail.class);
				for(int i=0;i<Month.values().length;i++){
					AccountBudgetDetail detailTo = new AccountBudgetDetail();
					detailTo.setAccountBudget((AccountBudget)getTo());
					// begiratzeke
					detailTo.setAccount(((AccountBudget)getTo()).getAccount());
					detailTo.setPeriod(((AccountBudget)getTo()).getPeriod());
					//
					cal.set(Calendar.MONTH, i);
					detailTo.setDate(cal.getTime());
					detailTo.setDebit(valueList.get(i).getDebit());
					detailTo.setCredit(valueList.get(i).getCredit());
					detailTo.setAccountBudget((AccountBudget)getTo());
					detailBean.insert(detailTo);
				}
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.log(Level.SEVERE, msg, e);
				}
				String msg = "Error on aon-account:  " + e.getMessage() ;
				LOGGER.log(Level.SEVERE, msg, e);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	@Override
	public void accept(ActionEvent event) {
		if(isNew() && isValidAccountPeriod()){
			super.accept(event);
		}
		insertBudgetDetail();
	}
	
	private boolean isValidAccountPeriod(){
		AccountBudget to = (AccountBudget)getTo();
		List<ITransferObject> list = null;
		try {
			list = getManagerBean().getList(getCriteria());
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		
		for(ITransferObject b: list){
			AccountBudget budget = (AccountBudget)b; 
			if(to.getPeriod().equals(budget.getPeriod()) && to.getAccount().equals(budget.getAccount())){
				String msg = "CUENTA REPETIDA EN EL PERIODO INDICADO";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}
		return true;
	}
	
	public void calculateCreditDebitTotals(){
		PreparedStatement sum = null;
		ResultSet sumSet = null;
		try {
			StringWriter sumStmt = new StringWriter();
			sumStmt.append("SELECT SUM(s.debit),SUM(s.credit)");
			sumStmt.append(" FROM account_budget_detail s ");
			sumStmt.append(" WHERE s.account LIKE ?");
			sumStmt.append(" AND s.account_period = ?");
			sum = HibernateUtil.getSQLConnection().prepareStatement(sumStmt.toString());
			sum.setString(1, ((AccountBudget)getTo()).getAccount().getId());
			sum.setString(2, ((AccountBudget)getTo()).getPeriod());
			sumSet = sum.executeQuery();
			if (sumSet.next()) {
				debitTotal = sumSet.getDouble(1);
				creditTotal = sumSet.getDouble(2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void calculateCreditDebitLists(){
		PreparedStatement sum = null;
		ResultSet sumSet = null;
		try {
			Iterator<ITransferObject> it = getManagerBean().getList(null).iterator();
			while(it.hasNext()){
				AccountBudget to = (AccountBudget)it.next();
				String id = to.getAccount().getId();
				String period = to.getPeriod();
				try {
					StringWriter sumStmt = new StringWriter();
					sumStmt.append("SELECT SUM(s.debit),SUM(s.credit)");
					sumStmt.append(" FROM account_budget_detail s ");
					sumStmt.append(" WHERE s.account LIKE ?");
					sumStmt.append(" AND s.account_period = ?");
					sum = HibernateUtil.getSQLConnection().prepareStatement(sumStmt.toString());
					sum.setString(1, id);
					sum.setString(2, period);
					sumSet = sum.executeQuery();
					if (sumSet.next()) {
						getDebitList().add(sumSet.getDouble(1));
						getCreditList().add(sumSet.getDouble(2));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

}