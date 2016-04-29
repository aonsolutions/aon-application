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
import com.code.aon.common.util.CommonUtil;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ui.util.AonUtil;


public class QuoteValuesController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(QuoteValuesController.class.getName());
	
	private Integer year;
	private String regimeType;
	
	private RegimeType regime;
	
	
	public String getRegimeType() {
		return regimeType;
	}
	public void setRegimeType(String regimeType) {
		this.regimeType = regimeType;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public boolean isNevv(){
		return false;
	}
	public String getBackActionListener(){
		return null;
	}
	public Map<String, Double> getValues() {
		return regime.getValues();
	}
	
	public void onInitialize(ActionEvent event) {
		setYear(CommonUtil.getYear(new Date()));
		setRegimeType("GENERAL");
		onReloadModel(event);
	}
	
	public void onReloadModel(ActionEvent event) {
		if(getRegimeType().equals("GENERAL")){
			regime = new RegimeGeneral();
		} else if(getRegimeType().equals("AGRICULTURAL")){
			regime = new RegimeAgricultural();
		} else if(getRegimeType().equals("HOME_EMPLOYEE")){
			regime = new RegimeHomeEmployees();
		}
		regime.initializeModel(year);
	}
	

	/*
	 * INNER CLASSES
	 */
	public static abstract class RegimeType {
		
		protected final Integer GENERAL_QUOTE_DOMAIN_ID 		= 0;
		protected final Integer UNKNOWN_1_DOMAIN_ID 			= -8;
		protected final Integer UNKNOWN_2_DOMAIN_ID 			= -9;
		protected final Integer FORMATION_DOMAIN_ID 			= -101;
		protected final Integer HOME_EMPLOYEES_QUOTE_DOMAIN_ID 	= -106;
		protected final Integer AGRICULTURAL_QUOTE_DOMAIN_ID 	= -107;
		
		public abstract Map<String, Double> getValues();
		protected abstract void initializeModel(Integer year);
		
		protected void putValue(Map<String, Double> values, String key, String value) {
			try {
				values.put(key, Double.valueOf(value));
			} catch (NumberFormatException e) {
				AonUtil.addErrorMessage("No se reconoce el valor: " + key);
				LOGGER.error("No se reconoce el valor: " + key);
			}
		}
		
		protected void putValue(Map<String, Double> values, String expression, String REGEX, String... keys) {
			expression = expression.replace("\n",  "").replace("\r",  "").replace("\t",  "");
			if(expression.matches(REGEX)){
				Pattern BASE_CGC_MIN_PATTERN = Pattern.compile(REGEX); 
				Matcher m = BASE_CGC_MIN_PATTERN.matcher(expression);
				if(m.find()) {
					if(keys.length>0){
						putValue(values, keys[0], (m.group(1)));
					}
					if(keys.length>1){
						putValue(values, keys[1], (m.group(2)));
					}
					if(keys.length>2){
						putValue(values, keys[2], (m.group(3)));
					}
				}
			} else {
				AonUtil.addErrorMessage("Expresion incorrecta: " + expression);
				LOGGER.error("Expresion incorrecta: " + expression);
			}
		}
		
		protected java.sql.Date getSqlEndDate(Integer year) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			return new java.sql.Date(calendar.getTime().getTime());
		}
		protected java.sql.Date getSqlStartDate(Integer year) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, Calendar.JANUARY);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getMinimum(Calendar.DAY_OF_MONTH));
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			return new java.sql.Date(calendar.getTime().getTime());
		}
		
		// *******************************************************
		// SQL
		// *******************************************************
		public Result<Record4<String, String, java.sql.Date, java.sql.Date>>  getQuoteValues(Integer year, Integer regimeDomainId) {
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
						.where(SYSTEM_DATA.DOMAIN.equal(regimeDomainId))
						.and(SYSTEM_DATA.START_DATE.lessOrEqual(getSqlEndDate(year)))
						.and(SYSTEM_DATA.END_DATE.greaterOrEqual(getSqlStartDate(year)).or(SYSTEM_DATA.END_DATE.isNull()))
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
		
		protected Result<Record4<String, String, java.sql.Date, java.sql.Date>> getUnemploymentEmployeeValues(Integer year, Integer regimeDomainId) {
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
						.where(SYSTEM_DEDUCTION.DOMAIN.equal(regimeDomainId))
						.and(SYSTEM_DEDUCTION.EXPRESSION.like("%PORCENTAJE_DESMPL%"))
						.and(SYSTEM_DEDUCTION.START_DATE.lessOrEqual(getSqlEndDate(year)))
						.and(SYSTEM_DEDUCTION.END_DATE.greaterOrEqual(getSqlStartDate(year)).or(SYSTEM_DEDUCTION.END_DATE.isNull()))
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
		
		protected Result<Record4<String, String, java.sql.Date, java.sql.Date>> getUnemploymentEnterpriseValues(Integer year, Integer regimeDomainId) {
			return getEnterpriseCostValues(year, regimeDomainId, "DESMPL_E");
		}
		
		protected Result<Record4<String, String, java.sql.Date, java.sql.Date>> getEnterpriseCostValues(Integer year, Integer regimeDomainId, String codeName) {
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
						.where(SYSTEM_COST.DOMAIN.equal(regimeDomainId))
						.and(SYSTEM_COST.CODE.equal(codeName))
						.and(SYSTEM_COST.START_DATE.lessOrEqual(getSqlEndDate(year)))
						.and(SYSTEM_COST.END_DATE.greaterOrEqual(getSqlStartDate(year)).or(SYSTEM_COST.END_DATE.isNull()))
						.and(SYSTEM_COST.EXPRESSION.notEqual("REMOVE()"))
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
	
	
	/* 
	 * *************************************
	 * REGIMEN GENERAL
	 * *************************************
	 */
	public static class RegimeGeneral extends RegimeType implements Serializable{
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private Map<String, Double> values;
		
		public Map<String, Double> getValues() {
			return values;
		}
		public void setValues(Map<String, Double> values) {
			this.values = values;
		}
		
		@Override
		protected void initializeModel(Integer year) {
			values = new HashMap<String, Double>();
			
			Result<Record4<String, String, java.sql.Date, java.sql.Date>> record = super.getQuoteValues(year, GENERAL_QUOTE_DOMAIN_ID);
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
					putValue(values, "PORCENTAJE_CGC", expression);
				}
				if(name.equals("PORCENTAJE_EXTR")){
					putValue(values, "PORCENTAJE_EXTR", expression);
				}
				if(name.equals("PORCENTAJE_NEXTR")){
					putValue(values, "PORCENTAJE_NEXTR", expression);
				}
				if(name.equals("PORCENTAJE_FP")){
					putValue(values, "PORCENTAJE_FP", expression);
				}
				
				if(name.equals("PORCENTAJE_CGC_E")){
					putValue(values, "PORCENTAJE_CGC_E", expression);
				}
				if(name.equals("PORCENTAJE_EXTR_E")){
					putValue(values, "PORCENTAJE_EXTR_E", expression);
				}
				if(name.equals("PORCENTAJE_NEXTR_E")){
					putValue(values, "PORCENTAJE_NEXTR_E", expression);
				}
				if(name.equals("PORCENTAJE_FP_E")){
					putValue(values, "PORCENTAJE_FP_E", expression);
				}
				
				if(name.equals("PORCENTAJE_FOGASA")){
					putValue(values, "PORCENTAJE_FOGASA", expression);
				}
				
				if(name.equals("PORCENTAJE_CORTA_DURACION")){
					putValue(values, "PORCENTAJE_CORTA_DURACION", expression);
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
			Result<Record4<String, String, java.sql.Date, java.sql.Date>> unemploymentEmployee = super.getUnemploymentEmployeeValues(year, GENERAL_QUOTE_DOMAIN_ID);
			for (Record4<String, String, java.sql.Date, java.sql.Date> step : unemploymentEmployee) {
				String expression = step.value2();
				Map<String, Double> types = obtainUnemploymentEmployeeType(expression);
				for(String key: types.keySet()){
					putValue(values, key, types.get(key).toString());
				}
			}
				
			Result<Record4<String, String, java.sql.Date, java.sql.Date>> unemploymentEnterprise = super.getUnemploymentEnterpriseValues(year, GENERAL_QUOTE_DOMAIN_ID);
			for (Record4<String, String, java.sql.Date, java.sql.Date> step : unemploymentEnterprise) {
				String expression = step.value2();
				Map<String, Double> types = obtainUnemploymentEnterpriseType(expression);
				for(String key: types.keySet()){
					putValue(values, key, types.get(key).toString());
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
					AonUtil.addErrorMessage("Expresion incorrecta: " + expression);
					LOGGER.error("Expresion incorrecta: " + expression);
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
					AonUtil.addErrorMessage("Expresion incorrecta: " + expression);
					LOGGER.error("Expresion incorrecta: " + expression);
				}
			}
			return values;
		}
		
		private Map<String, Double> obtainBaseCgcMin(String expression) {
			Map<String, Double> values = new HashMap<String, Double>();

			String BASE_CGC_MIN_REGEX_01 = ".*\"01\".*(\\d{4}.\\d{2}).*(\\d.\\d{2}).*\"02\".*"; 
			String BASE_CGC_MIN_REGEX_02 = ".*\"02\".*(\\d{3}.\\d{2}).*(\\d.\\d{2}).*\"03\".*"; 
			String BASE_CGC_MIN_REGEX_03 = ".*\"03\".*(\\d{3}.\\d{2}).*(\\d.\\d{2}).*\"04\".*"; 
			String BASE_CGC_MIN_REGEX_04 = ".*\"04\".*(\\d{3}.\\d{2}).*(\\d.\\d{2}).*\"05\".*"; 
			String BASE_CGC_MIN_REGEX_05 = ".*\"05\".*(\\d{3}.\\d{2}).*(\\d.\\d{2}).*\"06\".*"; 
			String BASE_CGC_MIN_REGEX_06 = ".*\"06\".*(\\d{3}.\\d{2}).*(\\d.\\d{2}).*\"07\".*"; 
			String BASE_CGC_MIN_REGEX_07 = ".*\"07\".*(\\d{3}.\\d{2}).*(\\d.\\d{2}).*\"08\".*"; 
			String BASE_CGC_MIN_REGEX_08 = ".*\"08\".*(\\d{2}.\\d{2}).*(\\d.\\d{2}).*\"09\".*"; 
			String BASE_CGC_MIN_REGEX_09 = ".*\"09\".*(\\d{2}.\\d{2}).*(\\d.\\d{2}).*\"10\".*"; 
			String BASE_CGC_MIN_REGEX_10 = ".*\"10\".*(\\d{2}.\\d{2}).*(\\d.\\d{2}).*\"11\".*"; 
			String BASE_CGC_MIN_REGEX_11 = ".*\"11\".*(\\d{2}.\\d{2}).*(\\d.\\d{2}).*";
					
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
			String BASE_CGP_MIN_REGEX = ".*TIEMPO_COMPLETO.*(\\d{3}.\\d{2}).*\\([DIAS_NOMINA|DIAS_COTIZADOS].*DIAS_MES.*1.*[DIAS_NOMINA|DIAS_COTIZADOS].*30\\).*(\\d{1}.\\d{2}).*HORAS_NOMINA.*"; 
			putValue(values, expression, BASE_CGP_MIN_REGEX, "BASE_CGP_MIN_FULL", "BASE_CGP_MIN_PARTIAL");
			return  values;
		}
		
		private Map<String, Double> obtainBaseCgpMax(String expression) {
			Map<String, Double> values = new HashMap<String, Double>();
			String BASE_CGP_MAX_REGEX = ".*(\\d{4}.\\d{2}).*\\(DIAS_NOMINA.*DIAS_MES.*1.*DIAS_NOMINA.*30\\).*";
			putValue(values, expression, BASE_CGP_MAX_REGEX, "BASE_CGP_MAX");
			return  values;
		}
		
		
		
	}
	
	/* 
	 * *************************************
	 * REGIMEN AGRARIO
	 * *************************************
	 */
	public static class RegimeAgricultural extends RegimeGeneral implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		@Override
		protected void initializeModel(Integer year) {
			super.initializeModel(year);
			
			Result<Record4<String, String, java.sql.Date, java.sql.Date>> record = super.getQuoteValues(year, AGRICULTURAL_QUOTE_DOMAIN_ID);
			for (Record4<String, String, java.sql.Date, java.sql.Date> step : record) {
				String name = step.value1();
				String expression = step.value2();
				
				/////////////////////
				// BASES
				/////////////////////
				Map<String, Double> bases = new HashMap<String, Double>();
				if(name.equals("BASE_CGC_MIN_MES")){
					bases = obtainBaseCgcMinMes(expression);
				} else if(name.equals("BASE_CGC_MAX_MES")){
					bases = obtainBaseCgcMaxMes(expression);
				} else if(name.equals("BASE_CGC_MIN_DIA")){
					bases = obtainBaseCgcMinDia(expression);
				} else if(name.equals("BASE_CGC_MAX_DIA")){
					bases = obtainBaseCgcMaxDia(expression);
				}
				for(String key: bases.keySet()){
					getValues().put(key, bases.get(key));
				}
				
				/////////////////////
				// REDUCCIONES
				/////////////////////
				Map<String, Double> reductions = new HashMap<String, Double>();
				if(name.equals("REDUCCION_CGC_E_01")){
					reductions = obtainReductionGroup1(expression);
				} else if(name.equals("REDUCCION_CGC_E_02")){
					reductions = obtainReductionGroup2(expression);
				}
				for(String key: reductions.keySet()){
					getValues().put(key, reductions.get(key));
				}
				
				/////////////////////
				// TIPOS
				/////////////////////
				Map<String, Double> percent = new HashMap<String, Double>();
				if(name.equals("PORCENTAJE_CGC_E")){
					percent = obtainCgcPercent(expression);
				} else if(name.equals("PORCENTAJE_CGC")){
				}
				for(String key: percent.keySet()){
					getValues().put(key, percent.get(key));
				}
				
				if(name.equals("PORCENTAJE_FP")){
					putValue(getValues(), "PORCENTAJE_FP", expression);
				}
				if(name.equals("PORCENTAJE_FP_E")){
					putValue(getValues(), "PORCENTAJE_FP_E", expression);
				}
				if(name.equals("PORCENTAJE_FOGASA")){
					putValue(getValues(), "PORCENTAJE_FOGASA", expression);
				}
				
			}
			
			
			Result<Record4<String, String, java.sql.Date, java.sql.Date>> enterpriseCgc = super.getEnterpriseCostValues(year, AGRICULTURAL_QUOTE_DOMAIN_ID, "CGC_E");
			for (Record4<String, String, java.sql.Date, java.sql.Date> step : enterpriseCgc) {
				String expression = step.value2();
				Map<String, Double> types = obtainCgcEnterpriseLimitAmount(expression);
				for(String key: types.keySet()){
					putValue(getValues(), key, types.get(key).toString());
				}
			}
			
			Result<Record4<String, String, java.sql.Date, java.sql.Date>> enterpriseAtep = super.getEnterpriseCostValues(year, AGRICULTURAL_QUOTE_DOMAIN_ID, "ATEP_E");
			for (Record4<String, String, java.sql.Date, java.sql.Date> step : enterpriseAtep) {
				String expression = step.value2();
				Map<String, Double> types = obtainAtepEnterprisePercent(expression);
				for(String key: types.keySet()){
					putValue(getValues(), key, types.get(key).toString());
				}
			}
				
		}
		
		private Map<String, Double> obtainBaseCgcMinMes(String expression) {
			Map<String, Double> values = new HashMap<String, Double>();

			String BASE_CGC_MIN_REGEX_01 = ".*\"01\".*(\\d{4}.\\d{2}).*\"02\".*"; 
			String BASE_CGC_MIN_REGEX_02 = ".*\"02\".*(\\d{3}.\\d{2}).*\"03\".*"; 
			String BASE_CGC_MIN_REGEX_03 = ".*\"03\".*(\\d{3}.\\d{2}).*\"04\".*"; 
			String BASE_CGC_MIN_REGEX_04 = ".*\"04\".*(\\d{3}.\\d{2}).*\"05\".*"; 
			String BASE_CGC_MIN_REGEX_05 = ".*\"05\".*(\\d{3}.\\d{2}).*\"06\".*"; 
			String BASE_CGC_MIN_REGEX_06 = ".*\"06\".*(\\d{3}.\\d{2}).*\"07\".*"; 
			String BASE_CGC_MIN_REGEX_07 = ".*\"07\".*(\\d{3}.\\d{2}).*\"08\".*"; 
			String BASE_CGC_MIN_REGEX_08 = ".*\"08\".*(\\d{3}.\\d{2}).*\"09\".*"; 
			String BASE_CGC_MIN_REGEX_09 = ".*\"09\".*(\\d{3}.\\d{2}).*\"10\".*"; 
			String BASE_CGC_MIN_REGEX_10 = ".*\"10\".*(\\d{3}.\\d{2}).*\"11\".*"; 
			String BASE_CGC_MIN_REGEX_11 = ".*\"11\".*(\\d{3}.\\d{2}).*";
					
			putValue(values, expression, BASE_CGC_MIN_REGEX_01, "1_BASE_CGC_MIN_MES");
			putValue(values, expression, BASE_CGC_MIN_REGEX_02, "2_BASE_CGC_MIN_MES");
			putValue(values, expression, BASE_CGC_MIN_REGEX_03, "3_BASE_CGC_MIN_MES");
			putValue(values, expression, BASE_CGC_MIN_REGEX_04, "4_BASE_CGC_MIN_MES");
			putValue(values, expression, BASE_CGC_MIN_REGEX_05, "5_BASE_CGC_MIN_MES");
			putValue(values, expression, BASE_CGC_MIN_REGEX_06, "6_BASE_CGC_MIN_MES");
			putValue(values, expression, BASE_CGC_MIN_REGEX_07, "7_BASE_CGC_MIN_MES");
			putValue(values, expression, BASE_CGC_MIN_REGEX_08, "8_BASE_CGC_MIN_MES");
			putValue(values, expression, BASE_CGC_MIN_REGEX_09, "9_BASE_CGC_MIN_MES");
			putValue(values, expression, BASE_CGC_MIN_REGEX_10, "10_BASE_CGC_MIN_MES");
			putValue(values, expression, BASE_CGC_MIN_REGEX_11, "11_BASE_CGC_MIN_MES");
			return values;
		}
		
		private Map<String, Double> obtainBaseCgcMinDia(String expression) {
			Map<String, Double> values = new HashMap<String, Double>();
			
			String BASE_CGC_MIN_REGEX_01 = ".*\"01\".*(\\d{2}.\\d{2}).*\"02\".*"; 
			String BASE_CGC_MIN_REGEX_02 = ".*\"02\".*(\\d{2}.\\d{2}).*\"03\".*"; 
			String BASE_CGC_MIN_REGEX_03 = ".*\"03\".*(\\d{2}.\\d{2}).*\"04\".*"; 
			String BASE_CGC_MIN_REGEX_04 = ".*\"04\".*(\\d{2}.\\d{2}).*\"05\".*"; 
			String BASE_CGC_MIN_REGEX_05 = ".*\"05\".*(\\d{2}.\\d{2}).*\"06\".*"; 
			String BASE_CGC_MIN_REGEX_06 = ".*\"06\".*(\\d{2}.\\d{2}).*\"07\".*"; 
			String BASE_CGC_MIN_REGEX_07 = ".*\"07\".*(\\d{2}.\\d{2}).*\"08\".*"; 
			String BASE_CGC_MIN_REGEX_08 = ".*\"08\".*(\\d{2}.\\d{2}).*\"09\".*"; 
			String BASE_CGC_MIN_REGEX_09 = ".*\"09\".*(\\d{2}.\\d{2}).*\"10\".*"; 
			String BASE_CGC_MIN_REGEX_10 = ".*\"10\".*(\\d{2}.\\d{2}).*\"11\".*"; 
			String BASE_CGC_MIN_REGEX_11 = ".*\"11\".*(\\d{2}.\\d{2}).*";
			
			putValue(values, expression, BASE_CGC_MIN_REGEX_01, "1_BASE_CGC_MIN_DIA");
			putValue(values, expression, BASE_CGC_MIN_REGEX_02, "2_BASE_CGC_MIN_DIA");
			putValue(values, expression, BASE_CGC_MIN_REGEX_03, "3_BASE_CGC_MIN_DIA");
			putValue(values, expression, BASE_CGC_MIN_REGEX_04, "4_BASE_CGC_MIN_DIA");
			putValue(values, expression, BASE_CGC_MIN_REGEX_05, "5_BASE_CGC_MIN_DIA");
			putValue(values, expression, BASE_CGC_MIN_REGEX_06, "6_BASE_CGC_MIN_DIA");
			putValue(values, expression, BASE_CGC_MIN_REGEX_07, "7_BASE_CGC_MIN_DIA");
			putValue(values, expression, BASE_CGC_MIN_REGEX_08, "8_BASE_CGC_MIN_DIA");
			putValue(values, expression, BASE_CGC_MIN_REGEX_09, "9_BASE_CGC_MIN_DIA");
			putValue(values, expression, BASE_CGC_MIN_REGEX_10, "10_BASE_CGC_MIN_DIA");
			putValue(values, expression, BASE_CGC_MIN_REGEX_11, "11_BASE_CGC_MIN_DIA");
			return values;
		}
		
		
		private Map<String, Double> obtainBaseCgcMaxMes(String expression) {
			Map<String, Double> values = new HashMap<String, Double>();
			String BASE_CGC_MAX_MES_REGEX = ".*(\\d{4}.\\d{2}).*";
			putValue(values, expression, BASE_CGC_MAX_MES_REGEX, "BASE_CGC_MAX_MES");
			return values;
		}
		
		private Map<String, Double> obtainBaseCgcMaxDia(String expression) {
			Map<String, Double> values = new HashMap<String, Double>();
			String BASE_CGC_MAX_DIA_REGEX = ".*(\\d{3}.\\d{2}).*";
			putValue(values, expression, BASE_CGC_MAX_DIA_REGEX, "BASE_CGC_MAX_DIA");
			return values;
		}
		
		/*
		 * REDUCCION_CGC_E_01
		 * 8.10
		 */
		private Map<String, Double> obtainReductionGroup1(String expression) {
			Map<String, Double> values = new HashMap<String, Double>();
			String GRUPO_1_REDUCTION_REGEX = ".*(\\d{1}.\\d{2}).*";
			putValue(values, expression, GRUPO_1_REDUCTION_REGEX, "REDUCCION_CGC_E_01");
			return values;
		}
		/*
		 * REDUCCION_CGC_E_02
		 * 
		 * COTIZACION_MENSUAL 
		 * 		? ((BASE_CGC <= 986.70) 
		 * 			? 6.68 
		 * 			: ((BASE_CGC <= 3063.30) ? (6.68 * ( 1 + (BASE_CGC - 986.70)/ BASE_CGC * 2.52 * 6.15 / 6.68)) : 0.00)) 
		 * 		: ((BASE_CGC / JORNADAS_REALES <= 42.90) 
		 * 			? 6.68 
		 * 			: ((BASE_CGC / JORNADAS_REALES <= 133.19 )
		 * 				? (6.68 * ( 1 + (BASE_CGC / JORNADAS_REALES - 42.90) / ( BASE_CGC / JORNADAS_REALES ) * 2.52 * 6.15 / 6.68)) 
		 * 				: 0.00))
		 */
		private Map<String, Double> obtainReductionGroup2(String expression) {
			Map<String, Double> values = new HashMap<String, Double>();
			String GRUPO_2_11_REDUCTION_REGEX = ".*\\?\\s?(\\d{1}.\\d{2})\\s?\\:.*";
			String GRUPO_2_11_MONTHLY_BASE_MIN_REGEX = ".*BASE_CGC\\s*<=\\s*(\\d{3}.\\d{2})\\s?\\).*";
			String GRUPO_2_11_DAYLY_BASE_MIN_REGEX = ".*BASE_CGC\\s\\/\\sJORNADAS_REALES\\s*<=\\s*(\\d{2}.\\d{2})\\s?\\).*";
			String GRUPO_2_11_MONTHLY_BASE_MAX_REGEX = ".*BASE_CGC\\s*<=\\s*(\\d{4}.\\d{2})\\s?\\).*";
			String GRUPO_2_11_DAYLY_BASE_MAX_REGEX = ".*BASE_CGC\\s\\/\\sJORNADAS_REALES\\s*<=\\s*(\\d{3}.\\d{2})\\s?\\).*";
			putValue(values, expression, GRUPO_2_11_REDUCTION_REGEX, "REDUCCION_CGC_E_02");
			putValue(values, expression, GRUPO_2_11_MONTHLY_BASE_MIN_REGEX, "BASE_MIN_REDUCCION_MES");
			putValue(values, expression, GRUPO_2_11_DAYLY_BASE_MIN_REGEX, "BASE_MIN_REDUCCION_DIA");
			putValue(values, expression, GRUPO_2_11_MONTHLY_BASE_MAX_REGEX, "BASE_MAX_REDUCCION_MES");
			putValue(values, expression, GRUPO_2_11_DAYLY_BASE_MAX_REGEX, "BASE_MAX_REDUCCION_DIA");
			return values;
		}
		
		/*
		 * PORCENTAJE_CGC_E
		 * 
		 * (GRUPO_COTIZACION == "01") ? (23.60 - REDUCCION_CGC_E_01) : (17.30 -REDUCCION_CGC_E_02)
		 */
		private Map<String, Double> obtainCgcPercent(String expression) {
			Map<String, Double> values = new HashMap<String, Double>();
			String CGC_GRUPO_1_REGEX = ".*(\\d{2}.\\d{2}).*REDUCCION_CGC_E_01.*";
			String CGC_GRUPO_2_11_REGEX = ".*REDUCCION_CGC_E_01.*(\\d{2}.\\d{2}).*REDUCCION_CGC_E_02.*";
			putValue(values, expression, CGC_GRUPO_1_REGEX, "PORCENTAJE_CGC_E_01");
			putValue(values, expression, CGC_GRUPO_2_11_REGEX, "PORCENTAJE_CGC_E_02");
			return values;
		}
		
		/*
		 * CGC_E  
		 _CUOTA=(( BASE_CGC_E=( BASE_CGC + ( isdef BASE_MTNAD ? BASE_MTNAD : 0.00 ) ) ) * PORCENTAJE_CGC_E/100); 
				(GRUPO_COTIZACION == "01") 
					? (COTIZACION_MENSUAL ? MIN(_CUOTA,279.00): MIN(_CUOTA, 12.13 * JORNADAS_REALES)) 
					: (COTIZACION_MENSUAL ? MAX(_CUOTA,60.25): MAX(_CUOTA,2.62 * JORNADAS_REALES)) 
		 */
		private Map<String, Double> obtainCgcEnterpriseLimitAmount(String expression) {
			Map<String, Double> values = new HashMap<String, Double>();
			String CGC_GRUPO_1_REGEX = ".*(\\d{3}.\\d{2})\\s?\\)\\s?\\:\\s?MIN\\(_CUOTA,\\s?(\\d{2}.\\d{2}).*";
			String CGC_GRUPO_2_11_REGEX = ".*(\\d{2}.\\d{2})\\s?\\)\\s?\\:\\s?MAX\\(_CUOTA,\\s?(\\d{1}.\\d{2}).*";
			putValue(values, expression, CGC_GRUPO_1_REGEX, "CGC_E_01_TOPE_MAX_MONTHLY", "CGC_E_01_TOPE_MAX_DAYLY");
			putValue(values, expression, CGC_GRUPO_2_11_REGEX, "CGC_E_01_TOPE_MIN_MONTHLY", "CGC_E_01_TOPE_MIN_DAYLY");
			return values;
		}
		
		/*
		 * ATEP_E 
			( COTIZACION_MENSUAL  ? 30 - ( DIAS_MES - DIAS_IT) : DIAS_IT ) 
			* BASE_REGULADORA 
			* ( INDEFINIDO 
			 		? ((GRUPO_COTIZACION == "01") ? 15.50 : 2.75)
			 		: PORCENTAJE_CGC_E )
		 */
		private Map<String, Double> obtainAtepEnterprisePercent(String expression) {
			Map<String, Double> values = new HashMap<String, Double>();
			String ATEP_GRUPO_REGEX = ".*GRUPO_COTIZACION.*(\\d{2}.\\d{2})\\s?\\:\\s?(\\d{1}.\\d{2}).*";
			putValue(values, expression, ATEP_GRUPO_REGEX, "PORCENTAJE_ATEP_01", "PORCENTAJE_ATEP_02");
			return values;
		}
	}
	
	/* 
	 * *************************************
	 * HOME EMPLOYEE
	 * *************************************
	 */
	public static class RegimeHomeEmployees extends RegimeGeneral implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		@Override
		protected void initializeModel(Integer year) {
			setValues(new HashMap<String, Double>());
			
			Result<Record4<String, String, java.sql.Date, java.sql.Date>> record = super.getQuoteValues(year, HOME_EMPLOYEES_QUOTE_DOMAIN_ID);
			for (Record4<String, String, java.sql.Date, java.sql.Date> step : record) {
				String name = step.value1();
				String expression = step.value2();
				
				/////////////////////
				// BASES
				/////////////////////
				Map<String, Double> bases = new HashMap<String, Double>();
				if(name.equals("BASE_CGC_MIN")){
					bases = obtainBaseCgcMin(expression);
				}
				for(String key: bases.keySet()){
					getValues().put(key, bases.get(key));
				}
				
				/////////////////////
				// TIPOS
				/////////////////////
				if(name.equals("PORCENTAJE_CGC")){
					putValue(getValues(), "PORCENTAJE_CGC", expression);
				}
				if(name.equals("PORCENTAJE_CGC_E")){
					putValue(getValues(), "PORCENTAJE_CGC_E", expression);
				}
			}
		}
		
		/*
		 * BASE_CGC_MIN
			($ in [
		        [172.91,148.60],
		        [270.10,245.84],
		        [367.40,343.10],
		        [464.70,440.36],
		        [561.90,537.63],
		        [658.40,634.89],
		        [756.60,756.60],
		        [Double.MAX_VALUE,794.60]
		        ] if $[0] >= BASE_CGC )[0][1] | 2015-01-01 
		 */
		private Map<String, Double> obtainBaseCgcMin(String expression) {
			Map<String, Double> values = new HashMap<String, Double>();
			
			String BASE_CGC_MIN_REGEX = "" 
					+ ".*\\[(\\d{3}.\\d{2}).?(\\d{3}.\\d{2})\\].?" 
					+ "\\[(\\d{3}.\\d{2}).?(\\d{3}.\\d{2})\\].?" 
					+ "\\[(\\d{3}.\\d{2}).?(\\d{3}.\\d{2})\\].?" 
					+ "\\[(\\d{3}.\\d{2}).?(\\d{3}.\\d{2})\\].?" 
					+ "\\[(\\d{3}.\\d{2}).?(\\d{3}.\\d{2})\\].?" 
					+ "\\[(\\d{3}.\\d{2}).?(\\d{3}.\\d{2})\\].?" 
					+ "\\[(\\d{3}.\\d{2}).?(\\d{3}.\\d{2})\\].?" 
					+ "\\[Double.MAX_VALUE.?(\\d{3}.\\d{2})\\].*"; 
			
			expression = expression.replace("\n",  "").replace("\r",  "").replace("\t",  "").replace(" ",  "");
			if(expression.matches(BASE_CGC_MIN_REGEX)){
				Pattern BASE_CGC_MIN_PATTERN = Pattern.compile(BASE_CGC_MIN_REGEX); 
				Matcher m = BASE_CGC_MIN_PATTERN.matcher(expression);
				if(m.find()) {
					putValue(values, "1_AMOUNT_CGC_MIN_MES"	, (m.group(1)));
					putValue(values, "1_BASE_CGC_MIN_MES"	, (m.group(2)));
					putValue(values, "2_AMOUNT_CGC_MIN_MES"	, (m.group(3)));
					putValue(values, "2_BASE_CGC_MIN_MES"	, (m.group(4)));
					putValue(values, "3_AMOUNT_CGC_MIN_MES"	, (m.group(5)));
					putValue(values, "3_BASE_CGC_MIN_MES"	, (m.group(6)));
					putValue(values, "4_AMOUNT_CGC_MIN_MES"	, (m.group(7)));
					putValue(values, "4_BASE_CGC_MIN_MES"	, (m.group(8)));
					putValue(values, "5_AMOUNT_CGC_MIN_MES"	, (m.group(9)));
					putValue(values, "5_BASE_CGC_MIN_MES"	, (m.group(10)));
					putValue(values, "6_AMOUNT_CGC_MIN_MES"	, (m.group(11)));
					putValue(values, "6_BASE_CGC_MIN_MES"	, (m.group(12)));
					putValue(values, "7_AMOUNT_CGC_MIN_MES"	, (m.group(13)));
					putValue(values, "7_BASE_CGC_MIN_MES"	, (m.group(14)));
					putValue(values, "8_BASE_CGC_MIN_MES"	, (m.group(15)));
				}
			} else {
				AonUtil.addErrorMessage("Expresion incorrecta: " + expression);
				LOGGER.error("Expresion incorrecta: " + expression);
			}
			
			return values;
		}
		
	}
	

	
}