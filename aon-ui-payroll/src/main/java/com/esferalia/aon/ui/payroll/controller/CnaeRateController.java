package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.CNAE2009Rate;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.esferalia.aon.entity.IEntityAlias;


public class CnaeRateController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private DataModel model;
	private Integer year;
	private String filter;
	
	
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public boolean isNew(){
		return false;
	}
	public String getBackActionListener(){
		return null;
	}
	
	public DataModel getModel() {
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}
	
	public void onInitialize(ActionEvent event){
		setFilter(null);
		setYear(CommonUtil.getYear(new Date()));
		setModel(null);
	}
	
	public void onReloadModel(ActionEvent event) throws ManagerBeanException{
		initializeModel();
	}
	
	private void initializeModel() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(CNAE2009Rate.class);
		Criteria criteria = new Criteria();
		if(StringUtils.isNotBlank(filter)){
			Expression expr1 = ExpressionUtilities.getLikeExpression("CNAE2009Rate.cnae2009.title", "%"+getFilter()+"%");
			Expression expr2 = ExpressionUtilities.getLikeExpression("CNAE2009Rate.cnae2009.code", "%"+getFilter()+"%");
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		}
		criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CNAE2009RATE_START_DATE), getPeriodStartDate(getYear()));
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CNAE2009RATE_START_DATE), getPeriodEndDate(getYear()));
		criteria.addOrder("CNAE2009Rate.cnae2009.code");
		List<ITransferObject> list = bean.getList(criteria);
		setModel(new SerializableListDataModel(list));
	}
	
	
	private Object getPeriodEndDate(Integer year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}
	private Object getPeriodStartDate(Integer year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getMinimum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
}