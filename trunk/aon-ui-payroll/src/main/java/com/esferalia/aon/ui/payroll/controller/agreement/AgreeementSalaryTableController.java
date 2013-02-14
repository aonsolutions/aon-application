package com.esferalia.aon.ui.payroll.controller.agreement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.AgreementLevel;
import com.esferalia.aon.payroll.AgreementLevelData;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.sql.SQLAgreementPaymentsFactory;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;

public class AgreeementSalaryTableController extends ControllerAdapter implements IController {
	
	private Date startDate;
	private Date endDate;
	
	private Agreement agreement;
	private DataModel salaryRows;
	private Set<String> variables;
	
	private int pageLimit = AonUtil.getConfigurationController().getPageLimit();
	
	public static class SalaryRow {
		
		private AgreementLevel level;
		private Map<String, AgreementLevelData> dataMap;
		
		public SalaryRow(AgreementLevel level, Map<String, AgreementLevelData> dataMap) {
			this.level = level;
			
			this.dataMap = dataMap;
			
		}
		
		public AgreementLevel getLevel() {
			return level;
		}
		
		public Map<String, AgreementLevelData> getData() {
			return dataMap;
		}
	}
	
	public AgreeementSalaryTableController() {
		// TODO: ¿ Could be possible setting this dates at faces-config.xml ? 
		this.startDate = Calendar.getInstance().getTime();
		this.endDate = Calendar.getInstance().getTime();
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
		this.endDate = endDate;
	}
	
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		this.reset((Agreement) event.getController().getTo());
	}
	
	
	public DataModel getSalaryRows() throws ManagerBeanException {
		if ( this.salaryRows == null && this.agreement!=null){
			this.salaryRows = newDataModel(this.agreement, this.startDate, this.endDate);
		}
		return this.salaryRows;
	}
	
	public Collection<String> getVariables() throws ManagerBeanException, SQLException {
		if ( this.variables == null && this.agreement!=null){
			this.variables = newVariables(this.agreement, this.startDate, this.endDate);
		}
		return this.variables;
	}
	
	
	
	
	
	private void reset(Agreement agreement) {
		this.salaryRows = null;
		this.variables = null;
		this.agreement = agreement;
	}
	
	@SuppressWarnings("deprecation")
	private Set<String> newVariables(Agreement agreement, Date startDate, Date endDate) throws SQLException {
		Connection sqlConnection = HibernateUtil.getSQLConnection();
		SQLAgreementPaymentsFactory factory = new SQLAgreementPaymentsFactory(sqlConnection, startDate,endDate); 
		Collection<IContractPayment> payments = factory.create(agreement.getId());
		Set<String> userVariables = new LinkedHashSet<String>();
		for (IContractPayment payment : payments) {
			PaymentType type = payment.getType();
			String expression = payment.getExpression();
			if( type == PaymentType.BASE_SALARY && expression!=null){
				Set<String> expressionVariables = ExpressionContext.getVariables(expression);
				for (String variable : expressionVariables) {
					if ( this.isUserVariable( variable)) {
						userVariables.add(variable);
					}
				}
			}
		}
		return userVariables;
	}
	
	@Override
	public int getRowCount() throws ManagerBeanException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<ITransferObject> search(int start, int count)
			throws ManagerBeanException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPageLimit() {
		return pageLimit;
	}
	
	public void setPageLimit(int pageLimit) {
		this.pageLimit = pageLimit;
	}

	@Override
	public void addExpression(ValueChangeEvent event)
			throws ManagerBeanException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addIdEqualExpression(ValueChangeEvent event)
			throws ManagerBeanException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEqualExpression(ValueChangeEvent event)
			throws ManagerBeanException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addGreaterThanOrEqualExpression(ValueChangeEvent event)
			throws ManagerBeanException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addLessThanOrEqualExpression(ValueChangeEvent event)
			throws ManagerBeanException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getFieldName(String alias) throws ManagerBeanException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String resolveAlias(String alias) throws ManagerBeanException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearCriteria() throws ManagerBeanException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Criteria getCriteria() throws ManagerBeanException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCriteria(Criteria criteria) throws ManagerBeanException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeModel() {
		this.salaryRows = null;
		try {
			this.salaryRows = newDataModel(this.agreement, this.startDate, this.endDate);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public DataModel getModel() throws ManagerBeanException {
		return this.getSalaryRows();
	}

	@Override
	public void setModel(DataModel model) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setNew(boolean isNew) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccept(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSearch(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRemove(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBack(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCancel(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReset(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSelect(ActionEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public ITransferObject getTo() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Add message to the collection of messages.
	 * 
	 * @param message
	 */
	private void addMessage(String message) {
		AonUtil.addErrorMessage(message);
	}
	
	
	private DataModel newDataModel (Agreement agreement, Date startDate, Date endDate) throws ManagerBeanException {
		
		IManagerBean levelMgr = 
			BeanManager.getManagerBean(AgreementLevel.class);

		Criteria levelCriteria = new Criteria(); 
		levelCriteria.addEqualExpression(levelMgr.getFieldName(IEntityAlias.AGREEMENT_LEVEL_AGREEMENT_ID), agreement.getId());
		List<?> levelTos = levelMgr.getList(levelCriteria);
		@SuppressWarnings("unchecked")
		List<AgreementLevel> levels = 
			(List<AgreementLevel>) levelTos;
		
		IManagerBean levelDataMgr = 
			BeanManager.getManagerBean(AgreementLevelData.class);

		List<SalaryRow> salaryRows = new LinkedList<SalaryRow>();
		
		for (AgreementLevel level : levels) {

			Criteria levelDataCriteria = new Criteria(); 
			levelDataCriteria.addEqualExpression(levelDataMgr.getFieldName(IEntityAlias.AGREEMENT_LEVEL_DATA_LEVEL_ID), level.getId());
			levelDataCriteria.addLessThanOrEqualExpression(levelDataMgr.getFieldName(IEntityAlias.AGREEMENT_LEVEL_DATA_START_DATE), startDate);
			levelDataCriteria.addExpression( ExpressionUtilities.getOrExpression(
												ExpressionUtilities.getNullExpression(levelDataMgr.getFieldName(IEntityAlias.AGREEMENT_LEVEL_DATA_END_DATE)),
												ExpressionUtilities.getGreaterThanOrEqualExpression(levelDataMgr.getFieldName(IEntityAlias.AGREEMENT_LEVEL_DATA_END_DATE), endDate))
											);
			List<?> levelDataTos = 
				levelDataMgr.getList(levelDataCriteria);
			@SuppressWarnings("unchecked")
			List<AgreementLevelData> levelDatas = 
				(List<AgreementLevelData>) levelDataTos;
			
			Map<String, AgreementLevelData> levelDataMap = 
				new HashMap<String, AgreementLevelData>();
			for (AgreementLevelData levelData : levelDatas) {
				levelDataMap.put( levelData.getName(), levelData );
			}
			
			SalaryRow salaryRow = 
				new SalaryRow(level, levelDataMap);
			salaryRows.add( salaryRow );
			
		}
		
		return new ListDataModel(salaryRows);
	}

	
	
	// TODO: This is suboptimal. We must have an orderd list with predefined names,
	// and then use binarySearch. ¿ It's this neccessary ?  
	private boolean isUserVariable(String variable ) {
		for (ContextVariable contractVariable : ContextVariable.values() ) {
			if ( variable.equals(contractVariable.getName()) ) {
				return false; // It's a 'predefined' variable
			}
		}
		return true; // It's not a 'predefined' variable
	}



}
