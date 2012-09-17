package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
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
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.ast.RelationalType;
import com.code.aon.ql.ast.impl.ConstantExpressionImpl;
import com.code.aon.ql.ast.impl.RelationalExpressionImpl;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.IVariableData;
import com.esferalia.aon.payroll.SystemData;
import com.esferalia.aon.payroll.enumeration.CNO;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractWorkingDay;
import com.esferalia.aon.payroll.enumeration.InactiveLastPeriod;
import com.esferalia.aon.payroll.enumeration.OccupationType;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.payroll.enumeration.VariableType;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.ui.payroll.controller.PayrollVariablesCollectionsController;

public abstract class AbstractVariableHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractVariableHandler.class.getName());
	
	private VariableData data;
	private DataModel variablesModel;
	private DataModel undefinedVariablesModel;
	
	private Date inactiveDate;
	private InactiveLastPeriod inactiveLastPeriod;
	private Boolean searchCurrentVariables;
	private boolean isNew;
	
	private IController controller;
	
	private String suggestAlias;
	private String filter;
	private Integer[] variableScopeFilter;
	
	public String getSuggestAlias() {
		return suggestAlias;
	}

	public void setSuggestAlias(String suggestAlias) {
		this.suggestAlias = suggestAlias;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
		
	public Integer[] getVariableScopeFilter() {
		return variableScopeFilter;
	}

	public void setVariableScopeFilter(Integer[] variableScopeFilter) {
		this.variableScopeFilter = variableScopeFilter;
	}
	
	private boolean isSystemVariableScopeFilter() {
		return Arrays.asList(getVariableScopeFilter()).contains(0);
	}

	private boolean isContextVariableScopeFilter() {
		return Arrays.asList(getVariableScopeFilter()).contains(1);
	}
	
	public List<SelectItem> getFilterVariableScopes(){
		List<SelectItem> types = new LinkedList<SelectItem>();
		SelectItem item = new SelectItem(0, "Sistema");
		types.add(item);
		item = new SelectItem(1, "Contexto");
		types.add(item);
		return types;
	}

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

	public boolean isNew() {
		return isNew;
	}
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public Date getInactiveDate() {
		return inactiveDate;
	}
	public void setInactiveDate(Date inactiveDate) {
		this.inactiveDate = inactiveDate;
	}
	
	public VariableData getData() {
		return data;
	}
	public void setData(VariableData data) {
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
		setNew(true);
		resetVariable();
		getData().setExpression("");
		getData().setStartDate(new Date());
	}
	public void onSelectVariable(ActionEvent event) {
		initEditor();
		setData((VariableData) getVariablesModel().getRowData());
		getData().checkVariableNature();
		getData().setSelected(true);
		getData().setEnableExpressionEditor(getData().isExpressionValue());
		handleEditorExpression();
	}
	
	public void onSaveVariable(ActionEvent event) {
		handleDataExpression();
		try {
			getVariableManagerBean().insertOrUpdate((ITransferObject) getData().getVariableData());
		} catch (ManagerBeanException e) {
			String msg = "Imposible guardar la variable del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
		setNew(false);
		initEditor();
		initializeVariables(event);
	}
	public void onCancelVariable(ActionEvent event) {
		setNew(false);
		getData().checkVariableNature();
		getData().setSelected(false);
		getData().setEnableExpressionEditor(false);
		initEditor();
	}
	public void onRemoveVariable(ActionEvent event) {
		try {
			getVariableManagerBean().remove((ITransferObject) getData().getVariableData());
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
		setData(new VariableData());
		getData().setVariableData((VariableData) getUndefinedVariablesModel().getRowData());
		setData((VariableData) getUndefinedVariablesModel().getRowData());
		setNew(true);
	}
	
	public void onSelectExpressionEditor(ActionEvent event) {
		VariableData data = (VariableData)getVariablesModel().getRowData();
		data.setEnableExpressionEditor(!data.isEnableExpressionEditor());
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getNewVariableList(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		if(getVariablesModel()!=null){
			for(VariableData data: (List<VariableData>)getVariablesModel().getWrappedData()){
				String name = data.getName();
				SelectItem item = new SelectItem(name, name);
				list.add(item);
			}
		}
		if(getUndefinedVariablesModel()!=null){
			for(VariableData data: (List<VariableData>)getUndefinedVariablesModel().getWrappedData()){
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
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getVariablesFilterList(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		if(getVariablesModel()!=null){
			for(VariableData data: (List<VariableData>)getVariablesModel().getWrappedData()){
				String name = data.getName();
				SelectItem item = new SelectItem(name, name);
				list.add(item);
			}
		}
		if(getUndefinedVariablesModel()!=null){
			for(VariableData data: (List<VariableData>)getUndefinedVariablesModel().getWrappedData()){
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
		if(getData().getVariable()==ContextVariable.CNO){
			return c.getCnoList();
		}else if(getData().getVariable()==ContextVariable.TC2){
			return c.getTc2List();
		}else if(getData().getVariable()==ContextVariable.CATEGORY){
			return c.getCategoryList();
		}else if(getData().getVariable()==ContextVariable.QUOTE_GROUP){
			 return c.getQuoteGroupList();
		}else if(getData().getVariable()==ContextVariable.OCCUPATION){
			return c.getOccupationList();
		}else if(getData().getVariable()==ContextVariable.QUOTE_IT){
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
//		if(variableHelperModel == null){
			variableHelperModel = new ListDataModel(getVariableHelpList());
//		}
		return variableHelperModel;
	}
	public void setVariableHelperModel(DataModel variableHelperModel) {
		this.variableHelperModel = variableHelperModel;
	}
//	@Override
//	public DataModel getModel() throws ManagerBeanException {
//		this.clearCriteria();
//		if (model == null || StringUtils.isBlank(getFilter())) {
//			initializeModel();
//		} else {
//			Expression exp;
//			try {
//				exp = ExpressionUtilities.getExpression(getFilter(), getFieldName(suggestAlias));
//				updateTextExpression(exp);
//				getCriteria().addExpression(exp);
//			} catch (ExpressionException e) {
//				throw new ManagerBeanException(e.getMessage(), e);
//			}
//		}
//		this.onSearch(null);
//		return super.getModel();
//	}
	
	private void updateTextExpression( Expression expression ) {
		RelationalExpressionImpl re = (RelationalExpressionImpl) expression;
		re.setType(RelationalType.LIKE);
		ConstantExpressionImpl ce = (ConstantExpressionImpl) re.getRightExpression();
		ce.setData( "%" + ce.getData().toString().toLowerCase() + "%" );
	}
	private List<SimpleVariable> getVariableHelpList() {
		List<SimpleVariable> list = new LinkedList<SimpleVariable>();
//		if(isExpressionHelp()){
			if(isSystemVariableScopeFilter()){				
				try {
					IManagerBean bean = BeanManager.getManagerBean(SystemData.class);
					Criteria criteria = new Criteria();
					criteria.addLessThanOrEqualExpression( bean.getFieldName(IEntityAlias.SYSTEM_DATA_START_DATE),new Date());
					String alias = bean.getFieldName(IEntityAlias.SYSTEM_DATA_END_DATE);
					Expression ex1 = ExpressionUtilities.getNullExpression(alias);
					Expression ex2 = ExpressionUtilities.getGreaterThanOrEqualExpression(alias,new Date());
					criteria.addOrExpression( ExpressionUtilities.getOrExpression(ex1, ex2));
					if (StringUtils.isNotBlank(getFilter())) {
						Expression exp1;
						Expression exp2;
						try {
							exp1 = ExpressionUtilities.getExpression(getFilter().toLowerCase(), bean.getFieldName(IEntityAlias.SYSTEM_DATA_COMMENTS));
							updateTextExpression(exp1);
							exp2 = ExpressionUtilities.getExpression(getFilter().toLowerCase(), bean.getFieldName(IEntityAlias.SYSTEM_DATA_NAME));
							updateTextExpression(exp2);
							criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));
						} catch (ExpressionException e) {
							throw new ManagerBeanException(e.getMessage(), e);
						}
					}
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
//		}
		if(isContextVariableScopeFilter()){
			for(ContextVariable v: ContextVariable.values()){
				SimpleVariable sv = new SimpleVariable();
				sv.setName(v.getName());
				sv.setDescription(v.getDescription(FacesContext.getCurrentInstance().getViewRoot().getLocale()));
				if(StringUtils.isBlank(getFilter()) 
						|| StringUtils.contains(sv.getDescription().toLowerCase(), getFilter().toLowerCase())
						|| StringUtils.contains(sv.getName().toLowerCase(),getFilter().toLowerCase())){
					list.add(sv);
				}
			}
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
		Integer[] types = {0, 1};
		setVariableScopeFilter( types );
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
		String var = ((SimpleVariable) variableHelperModel.getRowData()).getName();
		if(isExpressionHelp()){
			getData().setExpression((getData().getExpression()==null?"":getData().getExpression()) +" "+ var);
		} else {
			getData().setName(var);
		}
	}
	
	private void initEditor(){
		setData(null);
		setData(null);
		setCno(null);
		setQuoteGroup(null);
		setContractCode(null);
	}
	private void handleEditorExpression() {
		if( StringUtils.startsWith(getData().getExpression(), "\"") && StringUtils.endsWith(getData().getExpression(), "\"")){
			getData().setExpression( getData().getExpression().substring(1, getData().getExpression().length()-1) );
		}
		
		if (data.getVariable() == ContextVariable.CNO) {
			setCno(CNO.getCnoByValue(data.getExpression()));
		} else if (data.getVariable() == ContextVariable.TC2) {
			setContractCode(ContractCode.getContractCodeByValue(data.getExpression()));
		} else if (data.getVariable() == ContextVariable.CATEGORY) {
			;
		} else if (data.getVariable() == ContextVariable.QUOTE_GROUP) {
			setQuoteGroup(QuoteGroup.getQuoteGroupByValue(data.getExpression()));
		} else if (data.getVariable() == ContextVariable.OCCUPATION) {
			setOccupationType(OccupationType.getOccupationTypeByValue(data.getExpression()));
		} else if (data.getVariable() == ContextVariable.QUOTE_IT) {
			;
		} else if (data.getVariable() != null && data.getVariable().getType() == VariableType.BOOLEAN) {
			if (data.getExpression().equals("true") || data.getExpression().equals("false")) {
			} else {
			}
		} else if (data.getVariable() != null && data.getVariable().getType() == VariableType.INTEGER) {
			try {
				Integer.parseInt(data.getExpression());
			} catch (NumberFormatException e) {
			}
		} else if (data.getVariable() != null && data.getVariable().getType() == VariableType.DOUBLE) {
			try {
				Double.parseDouble(data.getExpression());
			} catch (NumberFormatException e) {
			}
		}
	}
	
	private void handleDataExpression() {
		if(!getData().isEnableExpressionEditor()){
			if(getData().getVariable()==ContextVariable.CNO){
				getData().setExpression("\""+String.valueOf(getCno().ordinal())+"\"");
			}else if(getData().getVariable()==ContextVariable.TC2){
				getData().setExpression("\""+getContractCode().getValue()+"\"");
			}else if(getData().getVariable()==ContextVariable.CATEGORY){
				;
			}else if(getData().getVariable()==ContextVariable.QUOTE_GROUP){
				getData().setExpression("\""+getQuoteGroup().getValue()+"\"");
			}else if(getData().getVariable()==ContextVariable.OCCUPATION){
				getData().setExpression("\""+getOccupationType().getValue()+"\"");
			}else if(getData().getVariable()==ContextVariable.QUOTE_IT){
				;
			}else if(getData().getVariable()!=null && getData().getVariable().getType()==VariableType.BOOLEAN){
//				getData().setExpression( Boolean.valueOf(getData().getExpression()) );
				;
			}else if(getData().getVariable()!=null && getData().getVariable().getType()==VariableType.INTEGER){
				;
			}else if(getData().getVariable()!=null && getData().getVariable().getType()==VariableType.DOUBLE){
				getData().setExpression( String.valueOf(CommonUtil.round(Double.parseDouble(getData().getExpression()))) );
			}
		}
	}
	
//	private boolean isExpression(AbstractVariableData data) {
//		ExpressionContext e = new ExpressionContext();
//		Calendar start = Calendar.getInstance();
//		Calendar end = Calendar.getInstance();
//		start.set(2011, 11, 1);
//		end.set(2011, 11, 30);
//		
//		
//		for(AbstractVariableData d: (List<AbstractVariableData>)getVariablesModel().getWrappedData()){
//			e.addVariable(d.getName(), d.getExpression(), d.getStartDate(), d.getEndDate());
//		}
//		try {
//			List<ITimedObject<Object>> list = e.eval(data.getExpression(), data.getStartDate(), data.getEndDate());
//			list.isEmpty();
//		} catch (ExpressionException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		return false;
//	}
	
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
	
	public class VariableData implements IVariableData {
		
		private boolean enableExpressionEditor;
		private boolean expressionValue;
		private boolean selected;
		private IVariableData variableData;
		
		public boolean isEnableExpressionEditor() {
			return enableExpressionEditor;
		}

		public void setEnableExpressionEditor(boolean enableExpressionEditor) {
			this.enableExpressionEditor = enableExpressionEditor;
		}

		public boolean isExpressionValue() {
			return expressionValue;
		}

		public void setExpressionValue(boolean expressionValue) {
			this.expressionValue = expressionValue;
		}
		
		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public IVariableData getVariableData() {
			return variableData;
		}

		public void setVariableData(IVariableData variableData) {
			this.variableData = variableData;
		}

		public ContextVariable getVariable(){
			ContextVariable var = ContextVariable.getVariableByName(getName()!=null?getName().toUpperCase():null);
			if(var!=null){
				setName(getName().toUpperCase());
			}
			return var;
		}

		public VariableType getVariableType(){
			if( getVariable()==null  ){
				return VariableType.EXPRESSION;
			}
			checkVariableNature();
			if( isSelected() ){
				if( isEnableExpressionEditor() ){
					return VariableType.EXPRESSION;
				}
			} else {
				if( isExpressionValue() ){
					return VariableType.EXPRESSION;
				}
			}
			return getVariable().getType();
		}
		
		public void checkVariableNature(){
			
			if( StringUtils.startsWith(getExpression(), "\"") && StringUtils.endsWith(getExpression(), "\"")){
				setExpression( getExpression().substring(1, getExpression().length()-1) );
			}

			setExpressionValue(false);
			if(getVariable()==ContextVariable.CNO && CNO.getCnoByValue(getExpression())==null ){
				setExpressionValue(true);
			}else if(getVariable()==ContextVariable.TC2 && ContractCode.getContractCodeByValue(getExpression())==null ){
				setExpressionValue(true);
			}else if(getVariable()==ContextVariable.CATEGORY){
				;
			}else if(getVariable()==ContextVariable.QUOTE_GROUP && QuoteGroup.getQuoteGroupByValue(getExpression())==null ){
				setExpressionValue(true);
			}else if(getVariable()==ContextVariable.OCCUPATION && OccupationType.getOccupationTypeByValue(getExpression())==null ){
				setExpressionValue(true);
			}else if(getVariable()==ContextVariable.QUOTE_IT){
				;
			}else if(getVariable()!=null && getVariable().getType()==VariableType.BOOLEAN){
				if( getExpression().equals("true") || getExpression().equals("false") ){
					setExpressionValue(false);
				} else {
					setExpressionValue(true);
				}
			}else if(getVariable()!=null && getVariable().getType()==VariableType.INTEGER){
				try {
					Integer.parseInt(getExpression());
					setExpressionValue(false);
				} catch (NumberFormatException e) {
					setExpressionValue(true);
				}
			}else if(getVariable()!=null && getVariable().getType()==VariableType.DOUBLE){
				try {
					Double.parseDouble(getExpression());
					setExpressionValue(false);
				} catch (NumberFormatException e) {
					setExpressionValue(true);
				}
			}
		}
		
		public Enum<?> getVariableEnum(){
			if(getName().equals(ContextVariable.CNO.getName())){
				return CNO.getCnoByValue(handleSelectItemExpression(getExpression()));
			} else if(getName().equals(ContextVariable.TC2.getName())){
				return ContractCode.getContractCodeByValue(handleSelectItemExpression(getExpression()));
			} else if(getName().equals(ContextVariable.QUOTE_GROUP.getName())){
				return QuoteGroup.getQuoteGroupByValue(handleSelectItemExpression(getExpression()));
			} else if(getName().equals(ContextVariable.OCCUPATION.getName())){
				return OccupationType.getOccupationTypeByValue(handleSelectItemExpression(getExpression()));
			} else if(getName().equals(ContextVariable.QUOTE_IT.getName())){
				return null;
			}
			return null;
		}
		
		private String handleSelectItemExpression(String expression) {
			if( StringUtils.startsWith(expression, "\"") && StringUtils.endsWith(expression, "\"")){
				return expression = expression.substring(1, expression.length()-1);
			}
			return expression;
		}
		
		public Double getDoubleExpression(){
			return Double.valueOf(getExpression());
		}

		@Override
		public Date getEndDate() {
			return getVariableData().getEndDate();
		}

		@Override
		public String getExpression() {
			return getVariableData().getExpression();
		}

		@Override
		public Integer getId() {
			return getVariableData().getId();
		}

		@Override
		public String getName() {
			return getVariableData().getName();
		}

		@Override
		public Date getStartDate() {
			return getVariableData().getStartDate();
		}

		@Override
		public void setEndDate(Date endDate) {
			getVariableData().setEndDate(endDate);
		}

		@Override
		public void setExpression(String expression) {
			getVariableData().setExpression(expression);
		}

		@Override
		public void setId(Integer id) {
			getVariableData().setId(id);
		}

		@Override
		public void setName(String name) {
			getVariableData().setName(name);
		}

		@Override
		public void setStartDate(Date startDate) {
			getVariableData().setStartDate(startDate);
		}

		@Override
		public ExpressionScope getScope() {
			return getVariableData().getScope();
		}

		@Override
		public boolean isReadOnly() {
			return getVariableData().isReadOnly();
		}
		
	}
	
}
