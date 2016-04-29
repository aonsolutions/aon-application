package com.esferalia.aon.ui.payroll.controller;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.lang.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.CNAE2009Rate;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;


public class CnaeRateController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CnaeRateController.class.getName());
	
	private final Integer GENERAL_QUOTE_DOMAIN_ID = 0;
	
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
	public Integer getCurrentYear(){
		return CommonUtil.getYear(new Date());
	}
	public boolean isNevv(){
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
	
	public void onInitialize(ActionEvent event) throws ManagerBeanException{
		setFilter(null);
		setYear(CommonUtil.getYear(new Date()));
		setModel(null);
		initializeModel();
		initializeOccupationModel();
	}
	
	public void onReloadModel(ActionEvent event) throws ManagerBeanException{
		initializeModel();
		initializeOccupationModel();
	}
	
	private void initializeModel() throws ManagerBeanException {
		Object value = obtainMaxYear();
		Integer year = value!=null?CommonUtil.getYear((Date) value):2010;
		
		IManagerBean bean = BeanManager.getManagerBean(CNAE2009Rate.class);
		Criteria criteria = new Criteria();
		if (StringUtils.isNotBlank(filter)) {
			Expression expr1 = ExpressionUtilities.getLikeExpression(
					"CNAE2009Rate.cnae2009.title", "%" + getFilter() + "%");
			Expression expr2 = ExpressionUtilities.getLikeExpression(
					"CNAE2009Rate.cnae2009.code", "%" + getFilter() + "%");
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1,
					expr2));
		}
		criteria.addEqualExpression(
				bean.getFieldName(IEntityAlias.CNAE2009RATE_START_DATE),
				getPeriodStartDate(year));
		criteria.addOrder("CNAE2009Rate.cnae2009.code");
		List<ITransferObject> list = bean.getList(criteria);
		setModel(new SerializableListDataModel(list));
	}
	
	private Object obtainMaxYear() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(CNAE2009Rate.class);
		Criteria criteria = new Criteria();
		Expression startLessPeriod = ExpressionUtilities
				.getLessThanOrEqualExpression(
						bean.getFieldName(IEntityAlias.CNAE2009RATE_START_DATE),
						getPeriodStartDate(getYear()));
		Expression endGreaterPeriod = ExpressionUtilities
				.getGreaterThanOrEqualExpression(
						bean.getFieldName(IEntityAlias.CNAE2009RATE_END_DATE),
						getPeriodEndDate(getYear()));
		Expression endNull = ExpressionUtilities.getNullExpression(bean
				.getFieldName(IEntityAlias.CNAE2009RATE_END_DATE));
		criteria.addExpression(ExpressionUtilities.getAndExpression(
				startLessPeriod,
				ExpressionUtilities.getOrExpression(endGreaterPeriod, endNull)));
		Projection projection = Projection.max(bean.getFieldName(IEntityAlias.CNAE2009RATE_START_DATE));
		Object value = bean.getUniqueResult(projection, criteria);
		return value;
	}
	
	
	private Date getPeriodEndDate(Integer year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}
	private Date getPeriodStartDate(Integer year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getMinimum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	Map<String, Double> itValues = null;
	Map<String, Double> imsValues = null;
	
	public Map<String, Double> getItValues() {
		return itValues;
	}
	public void setItValues(Map<String, Double> itValues) {
		this.itValues = itValues;
	}
	public Map<String, Double> getImsValues() {
		return imsValues;
	}
	public void setImsValues(Map<String, Double> imsValues) {
		this.imsValues = imsValues;
	}
	private void initializeOccupationModel() throws ManagerBeanException {
		String OCUPACION_IT = "OCUPACION_IT";
		String OCUPACION_IMS = "OCUPACION_IMS";
		String OCUPACION_REGEX_a = ".*\"a\":\\s*(\\d{1}.\\d{2}).*"; 
		String OCUPACION_REGEX_b = ".*\"b\":\\s*(\\d{1}.\\d{2}).*"; 
		String OCUPACION_REGEX_d = ".*\"d\":\\s*(\\d{1}.\\d{2}).*"; 
		String OCUPACION_REGEX_e = ".*\"e\":\\s*(\\d{1}.\\d{2}).*"; 
		String OCUPACION_REGEX_f = ".*\"f\":\\s*(\\d{1}.\\d{2}).*"; 
		String OCUPACION_REGEX_g = ".*\"g\":\\s*(\\d{1}.\\d{2}).*"; 
		String OCUPACION_REGEX_h = ".*\"h\":\\s*(\\d{1}.\\d{2}).*"; 
		itValues = new HashMap<String, Double>();
		Result<Record4<String, String, java.sql.Date, java.sql.Date>> itRecord = getSystemDataValues( OCUPACION_IT );
		if(itRecord.size()>0){
			String expression = itRecord.get(0).value2();
			putValue(itValues, expression, OCUPACION_REGEX_a, "a");
			putValue(itValues, expression, OCUPACION_REGEX_b, "b");
			putValue(itValues, expression, OCUPACION_REGEX_d, "d");
			putValue(itValues, expression, OCUPACION_REGEX_e, "e");
			putValue(itValues, expression, OCUPACION_REGEX_f, "f");
			putValue(itValues, expression, OCUPACION_REGEX_g, "g");
			putValue(itValues, expression, OCUPACION_REGEX_h, "h");
		}
		imsValues = new HashMap<String, Double>();
		Result<Record4<String, String, java.sql.Date, java.sql.Date>> imsRecord = getSystemDataValues( OCUPACION_IMS );
		if(itRecord.size()>0){
			String expression = imsRecord.get(0).value2();
			putValue(imsValues, expression, OCUPACION_REGEX_a, "a");
			putValue(imsValues, expression, OCUPACION_REGEX_b, "b");
			putValue(imsValues, expression, OCUPACION_REGEX_d, "d");
			putValue(imsValues, expression, OCUPACION_REGEX_e, "e");
			putValue(imsValues, expression, OCUPACION_REGEX_f, "f");
			putValue(imsValues, expression, OCUPACION_REGEX_g, "g");
			putValue(imsValues, expression, OCUPACION_REGEX_h, "h");
		}
	}
		
	private void putValue(Map<String, Double> values, String expression, String REGEX, String... keys) {
		if(expression.matches(REGEX)){
			Pattern BASE_CGC_MIN_PATTERN = Pattern.compile(REGEX); 
			Matcher m = BASE_CGC_MIN_PATTERN.matcher(expression);
			if(m.find()) {
				if(keys.length>0){
					try {
						values.put(keys[0], Double.valueOf(m.group(1)));
					} catch (NumberFormatException e) {
						AonUtil.addErrorMessage("No se reconoce el valor: " + m.group(1));
						LOGGER.error("No se reconoce el valor: " + m.group(1));
					}
				}
			}
		} else {
			AonUtil.addErrorMessage("No se reconoce el valor: " + keys.toString());
			LOGGER.error("No se reconoce el valor: " + keys.toString());
		}
	}
	
	
	/////////////////////////////////////////
	// SQL
	/////////////////////////////////////////

	public Result<Record4<String, String, java.sql.Date, java.sql.Date>> getSystemDataValues(String name) {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Settings SETTINGS = null;
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
			DSLContext ctx = DSL.using(connection, SETTINGS);
			Result<Record4<String, String, java.sql.Date, java.sql.Date>> record = ctx
					.select(SYSTEM_DATA.NAME, SYSTEM_DATA.EXPRESSION, SYSTEM_DATA.START_DATE, SYSTEM_DATA.END_DATE)
					.from(SYSTEM_DATA)
					.where(SYSTEM_DATA.DOMAIN.equal(GENERAL_QUOTE_DOMAIN_ID))
					.and(SYSTEM_DATA.NAME.equal(name))
					.and(SYSTEM_DATA.START_DATE.lessOrEqual( new java.sql.Date(getPeriodEndDate(getYear()).getTime())) )
					.and(SYSTEM_DATA.END_DATE.greaterOrEqual( new java.sql.Date(getPeriodStartDate(getYear()).getTime() ))
					.or(SYSTEM_DATA.END_DATE.isNull()))
					.orderBy(SYSTEM_DATA.START_DATE.asc()).fetch();
			return record;
		} catch (AonConnectionException e) {
			LOGGER.error(e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
		return null;
	}
	
}