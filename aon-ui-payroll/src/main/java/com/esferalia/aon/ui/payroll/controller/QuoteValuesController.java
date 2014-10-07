package com.esferalia.aon.ui.payroll.controller;

import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.event.ActionEvent;

import org.jooq.DSLContext;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ui.util.AonUtil;


public class QuoteValuesController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(QuoteValuesController.class.getName());
	
	private Integer year;
	private String filter;
	private Map<String, Double> values;
	
	
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
	public Map<String, Double> getValues() {
		return values;
	}
	public void setValues(Map<String, Double> values) {
		this.values = values;
	}
	
	public void onInitialize(ActionEvent event) throws ManagerBeanException{
		setFilter(null);
		setYear(CommonUtil.getYear(new Date()));
		setValues(null);
		initializeModel();
	}
	
	public void onReloadModel(ActionEvent event) throws ManagerBeanException{
		initializeModel();
	}
	
	private void initializeModel() throws ManagerBeanException {
		values = new HashMap<String, Double>();
		
		Result<Record4<String, String, java.sql.Date, java.sql.Date>> record = getGeneralRegimeQuoteValues();
		for (Record4<String, String, java.sql.Date, java.sql.Date> step : record) {
			String name = step.value1();
			String expression = step.value2();
			
			/////////////////////
			// BASES
			/////////////////////
			Map<String, Double> bases = new HashMap<String, Double>();
			if(name.equals("BASE_CGC_MIN")){
				bases = obtainBaseCgcMin(expression);
			} else if(name.equals("BASE_CGC_MAX")){
				bases = obtainBaseCgcMax(expression);
			} else if(name.equals("BASE_CGP_MIN")){
				bases = obtainBaseCgpMin(expression);
			} else if(name.equals("BASE_CGP_MAX")){
				bases = obtainBaseCgpMax(expression);
			}
			for(String key: bases.keySet()){
				values.put(key, bases.get(key));
			}

			
			/////////////////////
			// TIPOS
			/////////////////////
			if(name.equals("PORCENTAJE_CGC")){
				putValue(values, "CGC", expression);
			}
			if(name.equals("PORCENTAJE_EXTR")){
				putValue(values, "EXTR", expression);
			}
			if(name.equals("PORCENTAJE_NEXTR")){
				putValue(values, "NEXTR", expression);
			}
			if(name.equals("PORCENTAJE_FP")){
				putValue(values, "FP", expression);
			}
			
			if(name.equals("PORCENTAJE_CGC_E")){
				putValue(values, "CGC_E", expression);
			}
			if(name.equals("PORCENTAJE_EXTR_E")){
				putValue(values, "EXTR_E", expression);
			}
			if(name.equals("PORCENTAJE_NEXTR_E")){
				putValue(values, "NEXTR_E", expression);
			}
			if(name.equals("PORCENTAJE_FP_E")){
				putValue(values, "FP_E", expression);
			}
			
			if(name.equals("PORCENTAJE_FOGASA")){
				putValue(values, "FOGASA", expression);
			}
			
			if(name.equals("PORCENTAJE_CORTA_DURACION")){
				putValue(values, "CONTRATO_CORTA_DURACION", expression);
			}
			
			if(name.equals("SMI")){
				putValue(values, "SMI", expression);
			}
			if(name.equals("IPREM")){
				putValue(values, "IPREM", expression);
			}
			
		}
		
		//////////////////////////
		// TIPOS -DESEMPLEO-
		//////////////////////////
		Result<Record4<String, String, java.sql.Date, java.sql.Date>> unemploymentEmployee = getGeneralRegimeUnemploymentEmployeeValues();
		for (Record4<String, String, java.sql.Date, java.sql.Date> step : unemploymentEmployee) {
			String expression = step.value2();
			Map<String, Double> types = obtainUnemploymentEmployeeType(expression);
			for(String key: types.keySet()){
				try {
					values.put(key, Double.valueOf(types.get(key)));
				} catch (NumberFormatException e) {
					AonUtil.addErrorMessage("No se reconoce el valor: " + types.get(key) );
					LOGGER.error("No se reconoce el valor: " + types.get(key) );
				}
			}
		}
			
		Result<Record4<String, String, java.sql.Date, java.sql.Date>> unemploymentEnterprise = getGeneralRegimeUnemploymentEnterpriseValues();
		for (Record4<String, String, java.sql.Date, java.sql.Date> step : unemploymentEnterprise) {
			String expression = step.value2();
			Map<String, Double> types = obtainUnemploymentEnterpriseType(expression);
			for(String key: types.keySet()){
				try {
					values.put(key, types.get(key));
				} catch (NumberFormatException e) {
					AonUtil.addErrorMessage("No se reconoce el valor: " + types.get(key) );
					LOGGER.error("No se reconoce el valor: " + types.get(key) );
				}
			}
		}
	}
	
	
	private Map<String, Double> obtainUnemploymentEmployeeType(String expression) {
		Map<String, Double> values = new HashMap<String, Double>();
		String DESEMPL_BINARY_REGEX = ".*PORCENTAJE_DESMPL.*\\(INDEFINIDO.*(\\d{1}.\\d{2}).*(\\d{1}.\\d{2}).*\\).*";
		String DESEMPL_TERNARY_REGEX = ".*PORCENTAJE_DESMPL.*\\(INDEFINIDO.*(\\d{1}.\\d{2}).*\\(TIEMPO_COMPLETO.*(\\d{1}.\\d{2}).*(\\d{1}.\\d{2}).*\\)\\).*";
		
		if(expression.matches(DESEMPL_TERNARY_REGEX)){
			putValue(values, expression, DESEMPL_TERNARY_REGEX, "DESEMPL", "DESEMPL_TC", "DESEMPL_TP");
		} else {
			if(expression.matches(DESEMPL_BINARY_REGEX)){
				Pattern PATTERN = Pattern.compile(DESEMPL_BINARY_REGEX); 
				Matcher m = PATTERN.matcher(expression);
				if(m.find()) {
					putValue(values, "DESEMPL", m.group(1));
					putValue(values, "DESEMPL_TC", m.group(2));
					putValue(values, "DESEMPL_TP", m.group(2));
				}
			} else {
				AonUtil.addErrorMessage("No se reconoce el valor: " + expression);
				LOGGER.error("No se reconoce el valor: " + expression);
			}
		}
		return values;
	}
	
	private Map<String, Double> obtainUnemploymentEnterpriseType(String expression) {
		Map<String, Double> values = new HashMap<String, Double>();
		String DESEMPL_BINARY_REGEX = ".*PORCENTAJE_DESMPL_E.*\\(INDEFINIDO.*(\\d{1}.\\d{2}).*(\\d{1}.\\d{2}).*\\).*";
		String DESEMPL_TERNARY_REGEX = ".*PORCENTAJE_DESMPL_E.*\\(INDEFINIDO.*(\\d{1}.\\d{2}).*\\(TIEMPO_COMPLETO.*(\\d{1}.\\d{2}).*(\\d{1}.\\d{2})\\)\\).*"; 
		
		if(expression.matches(DESEMPL_TERNARY_REGEX)){
			putValue(values, expression, DESEMPL_TERNARY_REGEX, "DESEMPL_E", "DESEMPL_TC_E", "DESEMPL_TP_E");
		} else {
			if(expression.matches(DESEMPL_BINARY_REGEX)){
				Pattern PATTERN = Pattern.compile(DESEMPL_BINARY_REGEX); 
				Matcher m = PATTERN.matcher(expression);
				if(m.find()) {
					putValue(values, "DESEMPL_E", m.group(1));
					putValue(values, "DESEMPL_TC_E", m.group(2));
					putValue(values, "DESEMPL_TP_E", m.group(2));
				}
			} else {
				AonUtil.addErrorMessage("No se reconoce el valor: " + expression);
				LOGGER.error("No se reconoce el valor: " + expression);
			}
		}
		return values;
	}
	
	private Map<String, Double> obtainBaseCgcMin(String expression) {
		Map<String, Double> values = new HashMap<String, Double>();

		String BASE_CGC_MIN_REGEX_01 = ".*\"01\".*(\\d{4}.\\d{2}).+(\\d.\\d{2}).*\"02\".*"; 
		String BASE_CGC_MIN_REGEX_02 = ".*\"02\".*(\\d{3}.\\d{2}).+(\\d.\\d{2}).*\"03\".*"; 
		String BASE_CGC_MIN_REGEX_03 = ".*\"03\".*(\\d{3}.\\d{2}).+(\\d.\\d{2}).*\"04\".*"; 
		String BASE_CGC_MIN_REGEX_04 = ".*\"04\".*(\\d{3}.\\d{2}).+(\\d.\\d{2}).*\"05\".*"; 
		String BASE_CGC_MIN_REGEX_05 = ".*\"05\".*(\\d{3}.\\d{2}).+(\\d.\\d{2}).*\"06\".*"; 
		String BASE_CGC_MIN_REGEX_06 = ".*\"06\".*(\\d{3}.\\d{2}).+(\\d.\\d{2}).*\"07\".*"; 
		String BASE_CGC_MIN_REGEX_07 = ".*\"07\".*(\\d{3}.\\d{2}).+(\\d.\\d{2}).*\"08\".*"; 
		String BASE_CGC_MIN_REGEX_08 = ".*\"08\".*(\\d{2}.\\d{2}).+(\\d.\\d{2}).*\"09\".*"; 
		String BASE_CGC_MIN_REGEX_09 = ".*\"09\".*(\\d{2}.\\d{2}).+(\\d.\\d{2}).*\"10\".*"; 
		String BASE_CGC_MIN_REGEX_10 = ".*\"10\".*(\\d{2}.\\d{2}).+(\\d.\\d{2}).*\"11\".*"; 
		String BASE_CGC_MIN_REGEX_11 = ".*\"11\".*(\\d{2}.\\d{2}).+(\\d.\\d{2}).*"; 

		
		putValue(values, expression, BASE_CGC_MIN_REGEX_01, "1_BASE_CGC_MIN", "1_BASE_CGC_MIN_PARTIAL");
		putValue(values, expression, BASE_CGC_MIN_REGEX_02, "2_BASE_CGC_MIN", "2_BASE_CGC_MIN_PARTIAL");
		putValue(values, expression, BASE_CGC_MIN_REGEX_03, "3_BASE_CGC_MIN", "3_BASE_CGC_MIN_PARTIAL");
		putValue(values, expression, BASE_CGC_MIN_REGEX_04, "4_BASE_CGC_MIN", "4_BASE_CGC_MIN_PARTIAL");
		putValue(values, expression, BASE_CGC_MIN_REGEX_05, "5_BASE_CGC_MIN", "5_BASE_CGC_MIN_PARTIAL");
		putValue(values, expression, BASE_CGC_MIN_REGEX_06, "6_BASE_CGC_MIN", "6_BASE_CGC_MIN_PARTIAL");
		putValue(values, expression, BASE_CGC_MIN_REGEX_07, "7_BASE_CGC_MIN", "7_BASE_CGC_MIN_PARTIAL");
		putValue(values, expression, BASE_CGC_MIN_REGEX_08, "8_BASE_CGC_MIN", "8_BASE_CGC_MIN_PARTIAL");
		putValue(values, expression, BASE_CGC_MIN_REGEX_09, "9_BASE_CGC_MIN", "9_BASE_CGC_MIN_PARTIAL");
		putValue(values, expression, BASE_CGC_MIN_REGEX_10, "10_BASE_CGC_MIN", "10_BASE_CGC_MIN_PARTIAL");
		putValue(values, expression, BASE_CGC_MIN_REGEX_11, "11_BASE_CGC_MIN", "11_BASE_CGC_MIN_PARTIAL");
		return values;
	}
	
	private Map<String, Double> obtainBaseCgcMax(String expression) {
		Map<String, Double> values = new HashMap<String, Double>();
		
		String BASE_CGC_MAX_REGEX_01 = ".*\"01\".*(\\d{4}.\\d{2}).*\"02\".*"; 
		String BASE_CGC_MAX_REGEX_02 = ".*\"02\".*(\\d{4}.\\d{2}).*\"03\".*"; 
		String BASE_CGC_MAX_REGEX_03 = ".*\"03\".*(\\d{4}.\\d{2}).*\"04\".*"; 
		String BASE_CGC_MAX_REGEX_04 = ".*\"04\".*(\\d{4}.\\d{2}).*\"05\".*"; 
		String BASE_CGC_MAX_REGEX_05 = ".*\"05\".*(\\d{4}.\\d{2}).*\"06\".*"; 
		String BASE_CGC_MAX_REGEX_06 = ".*\"06\".*(\\d{4}.\\d{2}).*\"07\".*"; 
		String BASE_CGC_MAX_REGEX_07 = ".*\"07\".*(\\d{4}.\\d{2}).*\"08\".*"; 
		String BASE_CGC_MAX_REGEX_08 = ".*\"08\".*(\\d{3}.\\d{2}).*\"09\".*"; 
		String BASE_CGC_MAX_REGEX_09 = ".*\"09\".*(\\d{3}.\\d{2}).*\"10\".*"; 
		String BASE_CGC_MAX_REGEX_10 = ".*\"10\".*(\\d{3}.\\d{2}).*\"11\".*"; 
		String BASE_CGC_MAX_REGEX_11 = ".*\"11\".*(\\d{3}.\\d{2}).*.*"; 
		
		putValue(values, expression, BASE_CGC_MAX_REGEX_01, "1_BASE_CGC_MAX");
		putValue(values, expression, BASE_CGC_MAX_REGEX_02, "2_BASE_CGC_MAX");
		putValue(values, expression, BASE_CGC_MAX_REGEX_03, "3_BASE_CGC_MAX");
		putValue(values, expression, BASE_CGC_MAX_REGEX_04, "4_BASE_CGC_MAX");
		putValue(values, expression, BASE_CGC_MAX_REGEX_05, "5_BASE_CGC_MAX");
		putValue(values, expression, BASE_CGC_MAX_REGEX_06, "6_BASE_CGC_MAX");
		putValue(values, expression, BASE_CGC_MAX_REGEX_07, "7_BASE_CGC_MAX");
		putValue(values, expression, BASE_CGC_MAX_REGEX_08, "8_BASE_CGC_MAX");
		putValue(values, expression, BASE_CGC_MAX_REGEX_09, "9_BASE_CGC_MAX");
		putValue(values, expression, BASE_CGC_MAX_REGEX_10, "10_BASE_CGC_MAX");
		putValue(values, expression, BASE_CGC_MAX_REGEX_11, "11_BASE_CGC_MAX");
		return values;
	}
	
	private Map<String, Double> obtainBaseCgpMin(String expression) {
		Map<String, Double> values = new HashMap<String, Double>();
		String BASE_CGP_MIN_REGEX = ".*TIEMPO_COMPLETO.*(\\d{3}.\\d{2}).*\\(DIAS_NOMINA.*DIAS_MES.*1.*DIAS_NOMINA.*30\\).*(\\d{1}.\\d{2}).*HORAS_NOMINA.*"; 
		putValue(values, expression, BASE_CGP_MIN_REGEX, "BASE_CGP_MIN_FULL", "BASE_CGP_MIN_PARTIAL");
		return  values;
	}
	
	private Map<String, Double> obtainBaseCgpMax(String expression) {
		Map<String, Double> values = new HashMap<String, Double>();
		String BASE_CGP_MAX_REGEX = ".*(\\d{4}.\\d{2}).*\\(DIAS_NOMINA.*DIAS_MES.*1.*DIAS_NOMINA.*30\\).*";
		putValue(values, expression, BASE_CGP_MAX_REGEX, "BASE_CGP_MAX");
		return  values;
	}
	
	private void putValue(Map<String, Double> values, String key, String value) {
		try {
			values.put(key, Double.valueOf(value));
		} catch (NumberFormatException e) {
			AonUtil.addErrorMessage("No se reconoce el valor: " + value);
			LOGGER.error("No se reconoce el valor: " + value);
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
				if(keys.length>1){
					try {
						values.put(keys[1], Double.valueOf(m.group(2)));
					} catch (NumberFormatException e) {
						AonUtil.addErrorMessage("No se reconoce el valor: " + m.group(2));
						LOGGER.error("No se reconoce el valor: " + m.group(2));
					}
				}
				if(keys.length>2){
					try {
						values.put(keys[2], Double.valueOf(m.group(3)));
					} catch (NumberFormatException e) {
						AonUtil.addErrorMessage("No se reconoce el valor: " + m.group(3));
						LOGGER.error("No se reconoce el valor: " + m.group(3));
					}
				}
			}
		} else {
			AonUtil.addErrorMessage("No se reconoce el valor: " + keys.toString());
			LOGGER.error("No se reconoce el valor: " + keys.toString());
		}
	}
	
	private java.sql.Date getSqlEndDate(Integer year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return new java.sql.Date(calendar.getTime().getTime());
	}
	private java.sql.Date getSqlStartDate(Integer year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getMinimum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return new java.sql.Date(calendar.getTime().getTime());
	}
	
	/////////////////////////////////////////
	// SQL
	/////////////////////////////////////////
	
	public Result<Record4<String, String, java.sql.Date, java.sql.Date>>  getGeneralRegimeQuoteValues() {
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
					.where(SYSTEM_DATA.DOMAIN.equal(0))
					.and(SYSTEM_DATA.START_DATE.lessOrEqual(getSqlEndDate(getYear())))
					.and(SYSTEM_DATA.END_DATE.greaterOrEqual(getSqlStartDate(getYear())).or(SYSTEM_DATA.END_DATE.isNull()))
					.orderBy(SYSTEM_DATA.START_DATE.asc())
					.fetch();
			return record;
		} catch (AonConnectionException e) {
			LOGGER.error(e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
		return null;
	}
	
	private Result<Record4<String, String, java.sql.Date, java.sql.Date>> getGeneralRegimeUnemploymentEmployeeValues() {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Settings SETTINGS = null;
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
			DSLContext ctx = DSL.using(connection, SETTINGS);
			Result<Record4<String, String, java.sql.Date, java.sql.Date>> record = ctx
					.select(SYSTEM_DEDUCTION.DESCRIPTION, SYSTEM_DEDUCTION.EXPRESSION, SYSTEM_DEDUCTION.START_DATE, SYSTEM_DEDUCTION.END_DATE)
					.from(SYSTEM_DEDUCTION)
					.where(SYSTEM_DEDUCTION.DOMAIN.equal(0))
					.and(SYSTEM_DEDUCTION.EXPRESSION.like("%PORCENTAJE_DESMPL%"))
					.and(SYSTEM_DEDUCTION.START_DATE.lessOrEqual(getSqlEndDate(getYear())))
					.and(SYSTEM_DEDUCTION.END_DATE.greaterOrEqual(getSqlStartDate(getYear())).or(SYSTEM_DEDUCTION.END_DATE.isNull()))
					.orderBy(SYSTEM_DEDUCTION.START_DATE.asc())
					.fetch();
			return record;
		} catch (AonConnectionException e) {
			LOGGER.error(e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
		return null;
	}
	
	private Result<Record4<String, String, java.sql.Date, java.sql.Date>> getGeneralRegimeUnemploymentEnterpriseValues() {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Settings SETTINGS = null;
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
			DSLContext ctx = DSL.using(connection, SETTINGS);
			Result<Record4<String, String, java.sql.Date, java.sql.Date>> record = ctx
					.select(SYSTEM_COST.CODE, SYSTEM_COST.EXPRESSION, SYSTEM_COST.START_DATE, SYSTEM_COST.END_DATE)
					.from(SYSTEM_COST)
					.where(SYSTEM_COST.DOMAIN.equal(0))
					.and(SYSTEM_COST.CODE.equal("DESMPL_E"))
					.and(SYSTEM_COST.START_DATE.lessOrEqual(getSqlEndDate(getYear())))
					.and(SYSTEM_COST.END_DATE.greaterOrEqual(getSqlStartDate(getYear())).or(SYSTEM_COST.END_DATE.isNull()))
					.orderBy(SYSTEM_COST.START_DATE.asc())
					.fetch();
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