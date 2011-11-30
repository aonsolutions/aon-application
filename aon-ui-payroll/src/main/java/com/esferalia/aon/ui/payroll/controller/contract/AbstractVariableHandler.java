package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.AbstractVariableData;
import com.esferalia.aon.payroll.SystemData;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.CNO;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.payroll.enumeration.InactiveLastPeriod;
import com.esferalia.aon.payroll.enumeration.OccupationType;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.ui.payroll.controller.PayrollVariablesCollectionsController;

public abstract class AbstractVariableHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractVariableHandler.class.getName());
	
	private AbstractVariableData data;
	private DataModel variablesModel;
	private DataModel undefinedVariablesModel;
	
	private Date inactiveDate;
	private InactiveLastPeriod inactiveLastPeriod;
	private Boolean searchCurrentVariables;
	
	private IController controller;
	
	public AbstractVariableHandler(IController controller) {
		this.controller = controller;
	}
	
	public IController getController() {
		return controller;
	}
	public void setController(IController controller) {
		this.controller = controller;
	}

	public boolean isSearchCurrentVariables() {
		if(searchCurrentVariables == null){
			searchCurrentVariables = true;
		}
		return searchCurrentVariables;
	}
	public void setSearchCurrentVariables(boolean searchCurrentVariables) {
		this.searchCurrentVariables = searchCurrentVariables;
	}
	public InactiveLastPeriod getInactiveLastPeriod() {
		return inactiveLastPeriod;
	}
	public void setInactiveLastPeriod(InactiveLastPeriod inactiveLastPeriod) {
		this.inactiveLastPeriod = inactiveLastPeriod;
	}

	public Date getInactiveDate() {
		return inactiveDate;
	}
	public void setInactiveDate(Date inactiveDate) {
		this.inactiveDate = inactiveDate;
	}
	
	public AbstractVariableData getData() {
		return data;
	}
	public void setData(AbstractVariableData data) {
		this.data = data;
	}
	public DataModel getVariablesModel() {
		return variablesModel;
	}
	public void setVariablesModel(DataModel variablesModel) {
		this.variablesModel = variablesModel;
	}
	public DataModel getUndefinedVariablesModel() {
		return undefinedVariablesModel;
	}
	public void setUndefinedVariablesModel(DataModel undefinedVariablesModel) {
		this.undefinedVariablesModel = undefinedVariablesModel;
	}
	
	public void onResetVariable(ActionEvent event) {
		resetVariable();
	}
	public void onSelectVariable(ActionEvent event) {
		setData((AbstractVariableData) getVariablesModel().getRowData());
		handleEditorExpression(getData().getExpression());
	}
	
	public void onSaveVariable(ActionEvent event) {
		handleDataExpression();
		try {
			getVariableManagerBean().insertOrUpdate((ITransferObject) getData());
		} catch (ManagerBeanException e) {
			String msg = "Imposible guardar la variable del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
		initEditor();
		initializeVariables(event);
	}
	public void onCancelVariable(ActionEvent event) {
		initEditor();
	}
	public void onRemoveVariable(ActionEvent event) {
		try {
			getVariableManagerBean().remove((ITransferObject) getData());
		} catch (ManagerBeanException e) {
			String msg = "Imposible borrar la variable del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
		initEditor();
		initializeVariables(event);
	}
	public void onAddUndefinedVariable(ActionEvent event) {
		setData((AbstractVariableData) getUndefinedVariablesModel().getRowData());
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getNewVariableList(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		if(getVariablesModel()!=null){
			for(AbstractVariableData data: (List<AbstractVariableData>)getVariablesModel().getWrappedData()){
				String name = data.getName();
				SelectItem item = new SelectItem(name, name);
				list.add(item);
			}
		}
		if(getUndefinedVariablesModel()!=null){
			for(AbstractVariableData data: (List<AbstractVariableData>)getUndefinedVariablesModel().getWrappedData()){
				String name = data.getName();
				SelectItem item = new SelectItem(name, name);
				list.add(item);
			}
		}
		return list;
	}
	
	public void reloadData( ActionEvent event ) {
		onChangeLastPeriod(event);
		initializeVariables(event);
	}
	
	protected abstract void initializeVariables(ActionEvent event);
	
	public abstract List<?> expressionContext(Object suggest); 
	
	protected abstract IManagerBean getVariableManagerBean() throws ManagerBeanException;
	
	protected abstract void resetVariable();
	
	
	//******************************************************
	// VARIABLEs FILTER
	//******************************************************
	public void onChangeInactiveDate( ActionEvent event ) {
		if(getInactiveDate()==null && getInactiveLastPeriod()!=InactiveLastPeriod.ALL){
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -1);
			setInactiveDate(cal!=null?cal.getTime():null);
		}
	}
	
	public void onChangeLastPeriod( ActionEvent event ) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		if(getInactiveLastPeriod()==InactiveLastPeriod.LAST_MONTH){
			cal.add(Calendar.MONTH, -1);
		} else if(getInactiveLastPeriod()==InactiveLastPeriod.LAST_QUARTER){
			cal.add(Calendar.MONTH, -3);
		} else if(getInactiveLastPeriod()==InactiveLastPeriod.LAST_SEMESTER){
			cal.add(Calendar.MONTH, -6);
		} else if(getInactiveLastPeriod()==InactiveLastPeriod.LAST_YEAR){
			cal.add(Calendar.YEAR, -1);
		} else if(getInactiveLastPeriod()==InactiveLastPeriod.ALL){
			cal = null;
		}
		setInactiveDate(cal!=null?cal.getTime():null);
	}
	
	private String variableFilter;
	
	public List<SelectItem> getVariablesFilterList(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		if(getVariablesModel()!=null){
			for(AbstractVariableData data: (List<AbstractVariableData>)getVariablesModel().getWrappedData()){
				String name = data.getName();
				SelectItem item = new SelectItem(name, name);
				list.add(item);
			}
		}
		if(getUndefinedVariablesModel()!=null){
			for(AbstractVariableData data: (List<AbstractVariableData>)getUndefinedVariablesModel().getWrappedData()){
				String name = data.getName();
				SelectItem item = new SelectItem(name, name);
				list.add(item);
			}
		}
		return list;
	}
	
	public String getVariableFilter() {
		return variableFilter;
	}
	public void setVariableFilter(String variableFilter) {
		this.variableFilter = variableFilter;
	}

	//******************************************************
	// VARIABLEs EDITOR
	//******************************************************
	private boolean modalHelperPanelVisible;
	
	public boolean isModalHelperPanelVisible() {
		return modalHelperPanelVisible;
	}
	public void setModalHelperPanelVisible(boolean modalHelperPanelVisible) {
		this.modalHelperPanelVisible = modalHelperPanelVisible;
	}
	public Object getExpression() {
		return getData()!=null?getObjectExpression(getData().getExpression()):null;
	}
	public void setExpression(Object expression) {
		getData().setExpression(getStringExpression(expression));
	} 
	
	public List<?> getVariablesCollection() {
		PayrollVariablesCollectionsController c = new PayrollVariablesCollectionsController();
		if(getData().getVariable()==ContractVariables.CNO){
			return c.getCnoList();
		}else if(getData().getVariable()==ContractVariables.TC2){
			return c.getTc2List();
		}else if(getData().getVariable()==ContractVariables.CATEGORY){
			return c.getCategoryList();
		}else if(getData().getVariable()==ContractVariables.QUOTE_GROUP){
			 return c.getQuoteGroupList();
		}else if(getData().getVariable()==ContractVariables.OCCUPATION){
			return c.getOccupationList();
		}else if(getData().getVariable()==ContractVariables.QUOTE_IT){
			return c.getQuoteItList();
		}
		return null;
	}
	
	private String getStringExpression(Object expression) {
		if (expression instanceof Enum<?>) {
			if (expression instanceof IResourceable) {
//				Enum<?> v = (Enum<?>) expression;
//				return v.toString();
				if (expression instanceof IStringEnum) {
					IStringEnum v = (IStringEnum) expression;
					return "\""+v.getValue()+"\"";
				} else {
					Enum<?> v = (Enum<?>) expression;
					return "\""+v.toString()+"\"";
				}
			}
		}
		return expression.toString();
	}
	
	private Object getObjectExpression(String expression) {
		if( StringUtils.startsWith(expression, "\"") && StringUtils.endsWith(expression, "\"")){
			expression = expression.substring(1, expression.length()-1);
		}
		return expression;
	}
	
	private CNO cno;
	private ContractCode contractCode;
	private QuoteGroup quoteGroup;
	private OccupationType occupationType;
	private DataModel variableHelperModel;

	public CNO getCno() {
		return cno;
	}
	public void setCno(CNO cno) {
		this.cno = cno;
	}
	public ContractCode getContractCode() {
		return contractCode;
	}
	public void setContractCode(ContractCode contractCode) {
		this.contractCode = contractCode;
	}
	public QuoteGroup getQuoteGroup() {
		return quoteGroup;
	}
	public void setQuoteGroup(QuoteGroup quoteGroup) {
		this.quoteGroup = quoteGroup;
	}
	
	public OccupationType getOccupationType() {
		return occupationType;
	}
	public void setOccupationType(OccupationType occupationType) {
		this.occupationType = occupationType;
	}
	public DataModel getVariableHelperModel() {
		if(variableHelperModel == null){
			variableHelperModel = new ListDataModel(getVariableHelpList());
		}
		return variableHelperModel;
	}
	public void setVariableHelperModel(DataModel variableHelperModel) {
		this.variableHelperModel = variableHelperModel;
	}
	private List<SimpleVariable> getVariableHelpList() {
		List<SimpleVariable> list = new LinkedList<SimpleVariable>();
		if(isExpressionHelp()){
			// system data variables
			try {
				IManagerBean bean = BeanManager.getManagerBean(SystemData.class);
				Criteria criteria = new Criteria();
				
				//TODO ¿Utilizar las fechas del pojo activo?
				Date date = new Date();
				
				String alias = bean.getFieldName(IPayrollAlias.SYSTEM_DATA_END_DATE);
				Expression ex1 = ExpressionUtilities.getNullExpression(alias);
				Expression ex2 = ExpressionUtilities.getGreaterThanOrEqualExpression(alias,date);
				criteria.addOrExpression( ExpressionUtilities.getOrExpression(ex1, ex2));
				for (ITransferObject to: bean.getList(criteria)) {
					SystemData data = (SystemData) to;
					SimpleVariable sv = new SimpleVariable();
					sv.setName(data.getName());
					sv.setDescription(data.getComments());
					list.add(sv);		
				}
			} catch (ManagerBeanException e) {
				String msg = "Imposible mostrar las variables del sistema(" + e.getMessage() +")";
				LOGGER.error(msg);
			}
		}
		for(ContractVariables v: ContractVariables.values()){
			SimpleVariable sv = new SimpleVariable();
			sv.setName(v.getName());
			sv.setDescription(v.getName(FacesContext.getCurrentInstance().getViewRoot().getLocale()));
			list.add(sv);
		}
		return list;
	}
	private boolean expressionHelp;
	public boolean isExpressionHelp() {
		return expressionHelp;
	}
	public void setExpressionHelp(boolean expressionHelp) {
		this.expressionHelp = expressionHelp;
	}
	public void onShowVariableNameHelp(ActionEvent event) {
		setVariableHelperModel(null);
		setModalHelperPanelVisible(true);
		setExpressionHelp(false);
	}
	public void onShowVariableExpressionHelp(ActionEvent event) {
		setVariableHelperModel(null);
		setModalHelperPanelVisible(true);
		setExpressionHelp(true);
	}
	public void onCancelVariableHelp(ActionEvent event) {
		setModalHelperPanelVisible(false);
	}
	public void onSelectVariableHelp(ActionEvent event) {
		onCancelVariableHelp(event);
		String var = ((SimpleVariable) getVariableHelperModel().getRowData()).getName();
		if(isExpressionHelp()){
			getData().setExpression((getData().getExpression()==null?"":getData().getExpression()) +" "+ var);
		} else {
			getData().setName(var);
		}
	}
	
	private void initEditor(){
		setData(null);
		setCno(null);
		setQuoteGroup(null);
		setContractCode(null);
	}
	private void handleEditorExpression(String expression) {
		if( StringUtils.startsWith(expression, "\"") && StringUtils.endsWith(expression, "\"")){
			expression = expression.substring(1, expression.length()-1);
		}
		if(getData().getVariable()==ContractVariables.CNO){
			setCno(CNO.getCnoByValue(expression));
		}else if(getData().getVariable()==ContractVariables.TC2){
			setContractCode(ContractCode.getContractCodeByValue(expression));
		}else if(getData().getVariable()==ContractVariables.CATEGORY){
			;
		}else if(getData().getVariable()==ContractVariables.QUOTE_GROUP){
			setQuoteGroup(QuoteGroup.getQuoteGroupByValue(expression));
		}else if(getData().getVariable()==ContractVariables.OCCUPATION){
			setOccupationType(OccupationType.getOccupationTypeByValue(expression));
		}else if(getData().getVariable()==ContractVariables.QUOTE_IT){
			;
		}
		
	}
	private void handleDataExpression() {
		if(getData().getVariable()==ContractVariables.CNO){
			getData().setExpression("\""+String.valueOf(getCno().ordinal())+"\"");
		}else if(getData().getVariable()==ContractVariables.TC2){
			getData().setExpression("\""+getContractCode().getValue()+"\"");
		}else if(getData().getVariable()==ContractVariables.CATEGORY){
			;
		}else if(getData().getVariable()==ContractVariables.QUOTE_GROUP){
			getData().setExpression("\""+getQuoteGroup().getValue()+"\"");
		}else if(getData().getVariable()==ContractVariables.OCCUPATION){
			getData().setExpression("\""+getOccupationType().getValue()+"\"");
		}else if(getData().getVariable()==ContractVariables.QUOTE_IT){
			;
		}
	}
	
	public class SimpleVariable {
		private String name;
		private String description;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
	}
	
}
