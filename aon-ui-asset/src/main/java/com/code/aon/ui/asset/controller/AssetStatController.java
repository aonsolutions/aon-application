package com.code.aon.ui.asset.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.AssetStat;
import com.code.aon.asset.dao.IAssetAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;

public class AssetStatController {

	private Date date;
	private Integer year;
	private Integer month;
	private String name;
	private String assetName;
	private String userName;
	private Date fromDate;
	private Date toDate;
	private String statType;
	private String dateRange;
	List<AssetStat> stats;
	private String beanName;
	private IManagerBean assetActivityBean;
	Criteria criteria;
	Locale locale = AonUtil.getCurrentLocale();
	private static final Logger LOGGER = LoggerFactory.getLogger(AssetStatController.class.getName());
	
	/**
	 * Devuelve la fecha
	 * @return
	 */
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * Devuelve la fecha inicial del periodo filtrado
	 * @return
	 */
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	/**
	 * Devuelve la fecha final del periodo filtrado
	 * @return
	 */
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	/**
	 * Devuelve el nombre del bean
	 * @return
	 */
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	/**
	 * Devuelve el anio a usar por el filtro
	 * @return
	 */
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	/**
	 * Devuelve el mes a usar por el filtro
	 * @return
	 */
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	/**
	 * Devuelve el nombre del referido a mostrar
	 * @return
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAssetName() {
		return assetName;
	}
	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * Devuelve el tipo de filtro de la estadistica (por activo o por usuario)
	 * @return
	 */
	public String getStatType() {
		return statType;
	}
	public void setStatType(String statType) {
		this.statType = statType;
	}
	/**
	 * Devuelve el tipo de rango de la fecha de la estadistica (YEAR, MONTH, DAY)
	 * @return
	 */
	public String getDateRange() {
		return dateRange;
	}
	public void setDateRange(String dateRange) {
		this.dateRange = dateRange;
	}
	/**
	 * Devuelve el nombre del mes
	 * @return
	 */
	public String getMonthName() {
		return Month.getMonthByValue(month).getName(locale);
	}
	
	/**
	 * Devuelve la lista de estadisticas
	 * @return
	 */
	public List<AssetStat> getStats(){
		return stats;
	}
	
	/**
	 * Inicializa la lista de estadisticas
	 * @param event
	 */
	public void onInitialize(ActionEvent event) {
		setYear(Calendar.getInstance().get(Calendar.YEAR));
		setStatType("ASSET");
		setDateRange("YEAR");
		refreshStats(event);
	}
	
	/**
	 * Actualiza los atributos para el filtro (activo, usuario y year, month, day)
	 * y construye la lista de estadisticas 
	 * @param event
	 */
	public void refreshStats(ActionEvent event) {
		Calendar cal = new GregorianCalendar();
		cal.set(year.intValue(), Calendar.JANUARY, 1);
		setFromDate(cal.getTime());
		cal.set(year.intValue(), Calendar.DECEMBER, 31);
		setToDate(cal.getTime());
		setMonth(null);
		setName(null);
		
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();			
		if (params.get("year")!=null){
			year = Integer.parseInt(params.get("year"));
			cal = new GregorianCalendar();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, cal.getActualMinimum(Calendar.MONTH));
			fromDate = cal.getTime();
			cal.set(Calendar.MONTH, cal.getActualMaximum(Calendar.MONTH));
			toDate = cal.getTime();
		}
		if (params.get("month")!=null){
			month= Integer.parseInt(params.get("month"));
			cal = new GregorianCalendar();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
			fromDate = cal.getTime();
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			toDate = cal.getTime();
		}
		if (params.get("name")!=null){
			setName(new String(params.get("name")));
			if(getStatType().equals("ASSET")){
				setAssetName(new String(params.get("name")));
			}
			if(getStatType().equals("USER")){
				setUserName(new String(params.get("name")));
			}
		}
		if (params.get("type")!=null){
			setDateRange(params.get("type"));
		}
		try {
			if(getDateRange().equals("YEAR")){
				buildStatsByYear();
			} else if(getDateRange().equals("MONTH")){
				buildStatsByMonth();
			} else if(getDateRange().equals("DAY")){
				buildStatsByDay();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e );
		}
	}
	
	/**
	 * Construye el criteria para obtener una lista a partir del bean de assetActivity
	 * @throws ManagerBeanException
	 */
	private void buildCriteria() throws ManagerBeanException{
		assetActivityBean = BeanManager.getManagerBean(AssetActivity.class);
		criteria = new Criteria();
		String identifier;
		
		identifier = assetActivityBean.getFieldName(IAssetAlias.ASSET_ACTIVITY_DATE);
		criteria.addBetweenExpression(identifier, fromDate, toDate);
		if(getDateRange().equals("YEAR")){
			if(getStatType().equals("ASSET")){
				identifier = assetActivityBean.getFieldName(IAssetAlias.ASSET_ACTIVITY_ASSET_ID);
				criteria.addOrder(identifier);
			}
			if(getStatType().equals("USER")){
				identifier = assetActivityBean.getFieldName(IAssetAlias.ASSET_ACTIVITY_WHO);
				criteria.addOrder(identifier);
			}
		} else if(getDateRange().equals("MONTH")){
			if(getStatType().equals("ASSET")){
				identifier = assetActivityBean.getFieldName(IAssetAlias.ASSET_ACTIVITY_ASSET_NAME);
			}
			if(getStatType().equals("USER")){
				identifier = assetActivityBean.getFieldName(IAssetAlias.ASSET_ACTIVITY_WHO);
			}
			criteria.addEqualExpression(identifier, getName());
			identifier = assetActivityBean.getFieldName(IAssetAlias.ASSET_ACTIVITY_DATE);
			criteria.addOrder(identifier);
		} else if(getDateRange().equals("DAY")){
			if(getStatType().equals("ASSET")){
				identifier = assetActivityBean.getFieldName(IAssetAlias.ASSET_ACTIVITY_ASSET_NAME);
				criteria.addEqualExpression(identifier, getAssetName());
			}
			if(getStatType().equals("USER")){
				identifier = assetActivityBean.getFieldName(IAssetAlias.ASSET_ACTIVITY_WHO);
				criteria.addEqualExpression(identifier, getUserName());
			}
			identifier = assetActivityBean.getFieldName(IAssetAlias.ASSET_ACTIVITY_DATE);
			criteria.addOrder(identifier);
		} 
	}
	
	/**
	 * Construye la lista de estadisticas segun el anio
	 * @throws ManagerBeanException
	 */
	public void buildStatsByYear() throws ManagerBeanException {
		stats = new LinkedList<AssetStat>();
		buildCriteria();
		Iterator<ITransferObject> iter = assetActivityBean.getList(criteria).iterator();
		AssetStat stat = null;
		AssetActivity activity;
		AssetActivity previous = null;
		double hours;
		
		while (iter.hasNext()) {
			activity = (AssetActivity) iter.next(); 
			hours=activity.getToTime().getTime() - activity.getFromTime().getTime();
			hours /=(1000*60*60);
			if(getStatType().equals("ASSET")){
				if(previous != null && activity.getAsset().getId().equals(previous.getAsset().getId())){
					stat.setName(activity.getAsset().getName());
					stat.setKey(activity.getAsset().getId().toString());
					stat.setDate(activity.getDate());
					stat.setHours(stat.getHours()+hours);
					stat.setRequest(stat.getRequest()+1);
					stat.setAverage(stat.getHours()/stat.getRequest());
					stat.setUser(activity.getWho());
				} else {
					stat = new AssetStat();
					stat.setName(activity.getAsset().getName());
					stat.setKey(activity.getAsset().getId().toString());
					stat.setDate(activity.getDate());
					stat.setHours(hours);
					stat.setRequest(1);
					stat.setAverage(stat.getHours()/stat.getRequest());
					stat.setUser(activity.getWho());
					stats.add(stat);
				}
			} else if(getStatType().equals("USER")){
				if(previous != null && activity.getWho().equals(previous.getWho())){
					stat.setName(activity.getWho());
					stat.setKey(activity.getId().toString());
					stat.setDate(activity.getDate());
					stat.setHours(stat.getHours()+hours);
					stat.setRequest(stat.getRequest()+1);
					stat.setAverage(stat.getHours()/stat.getRequest());
					stat.setUser(activity.getWho());
				} else {
					stat = new AssetStat();
					stat.setName(activity.getWho());
					stat.setKey(activity.getId().toString());
					stat.setDate(activity.getDate());
					stat.setHours(hours);
					stat.setRequest(1);
					stat.setAverage(stat.getHours()/stat.getRequest());
					stat.setUser(activity.getWho());
					stats.add(stat);
				}
			}
			previous = activity;
		}
	}
	
	/**
	 * Construye la lista de estadisticas segun el mes del anio
	 * @throws ManagerBeanException
	 */
	public void buildStatsByMonth() throws ManagerBeanException {
		stats = new ArrayList<AssetStat>();
		Calendar cal = new GregorianCalendar();
		for(int i=0;i<12;i++){
			stats.add(i, new AssetStat());
			stats.get(i).setKey(String.valueOf(i));
			stats.get(i).setName(Month.getMonthByValue(i).getName(locale));
			cal.setTime(stats.get(i).getDate());
			cal.set(Calendar.MONTH, i);
			stats.get(i).setDate(cal.getTime());
		}
		
		buildCriteria();
		Iterator<ITransferObject> iter = assetActivityBean.getList(criteria).iterator();
		
		AssetStat stat = null;
		AssetActivity activity;
		double hours;
		
		while (iter.hasNext()) {
			activity = (AssetActivity) iter.next(); 
			hours=activity.getToTime().getTime() - activity.getFromTime().getTime();
			hours /=(1000*60*60);
			cal.setTime(activity.getDate());
			
			stat = stats.get(cal.get(Calendar.MONTH));
			stat.setDate(activity.getDate());
			stat.setHours(stat.getHours()+hours);
			stat.setRequest(stat.getRequest()+1);
			stat.setAverage(stat.getHours()/stat.getRequest());
			stat.setUser(activity.getWho());
		}
	}
	
	/**
	 * Construye la lista de estadisticas segun los dias del mes
	 * @throws ManagerBeanException
	 */
	public void buildStatsByDay() throws ManagerBeanException {
		stats = new ArrayList<AssetStat>();
		Calendar cal = new GregorianCalendar();
		cal.set(year, month, 1);
		int days = cal.getActualMaximum(Calendar.DATE);
		System.out.println(cal.getActualMaximum(Calendar.DATE));
		
		for(int i=0;i<days;i++){
			stats.add(i, new AssetStat());
			stats.get(i).setKey(String.valueOf(i+1));
			stats.get(i).setName(String.valueOf(i+1));
		}
		
		buildCriteria();
		Iterator<ITransferObject> iter = assetActivityBean.getList(criteria).iterator();
		
		AssetStat stat = null;
		AssetActivity activity;
		double hours;
		
		while (iter.hasNext()) {
			activity = (AssetActivity) iter.next(); 
			hours=activity.getToTime().getTime() - activity.getFromTime().getTime();
			hours /=(1000*60*60);
			cal.setTime(activity.getDate());
			
			stat = stats.get(cal.get(Calendar.DAY_OF_MONTH)-1);
			stat.setDate(activity.getDate());
			stat.setHours(stat.getHours()+hours);
			stat.setRequest(stat.getRequest()+1);
			stat.setAverage(stat.getHours()/stat.getRequest());
			stat.setUser(activity.getWho());
			stats.set(cal.get(Calendar.DAY_OF_MONTH)-1, stat);
		}
	}
	
}