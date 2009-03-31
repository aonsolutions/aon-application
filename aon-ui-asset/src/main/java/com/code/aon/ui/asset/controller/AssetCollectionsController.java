package com.code.aon.ui.asset.controller;

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

public class AssetCollectionsController {

	private Date date;
	private Integer year;
	private Integer month;
	private String name;
	private Date fromDate;
	private Date toDate;
	// asset, user
	private String statType;
	// year, month, day
	private String dateRange;
	List<AssetStat> stats;
	private String beanName;
	private IManagerBean assetActivityBean;
	Criteria criteria;
	Locale locale = AonUtil.getCurrentLocale();
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatType() {
		return statType;
	}
	public void setStatType(String statType) {
		this.statType = statType;
	}
	public String getDateRange() {
		return dateRange;
	}
	public void setDateRange(String dateRange) {
		this.dateRange = dateRange;
	}
	public String getMonthName() {
		return Month.getMonthByValue(month).getName(locale);
	}
	
	

	
	public void onInitialize(ActionEvent event) {
		setYear(Calendar.getInstance().get(Calendar.YEAR));
		setStatType("ASSET");
		setDateRange("YEAR");
		refreshStats(event);
		
	}

	public void refreshStats(ActionEvent event) {
		Calendar cal = new GregorianCalendar();
		cal.set(year.intValue(), Calendar.JANUARY, 1);
		setFromDate(cal.getTime());
		cal.set(year.intValue(), Calendar.DECEMBER, 31);
		setToDate(cal.getTime());
		
		
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();			
		if (params.get("year")!=null){
			year = Integer.parseInt(params.get("year"));
			//fromDate.setYear(year);
			//toDate.setYear(year);
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
		if (params.get("name")!=null)
			name = params.get("name");
		if (params.get("type")!=null)
			setDateRange(params.get("type"));
		
		try {
			if(getDateRange().equals("YEAR"))
				buildStatsByYear();
			else if(getDateRange().equals("MONTH"))
				buildStatsByMonth();
			else if(getDateRange().equals("DAY"))
				buildStatsByDay();
			//else if(getStatType().equals("USER"))
				//buildStatsByUser();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<AssetStat> getStats(){
		return stats;
	}
	
	private void buildCriteria() throws ManagerBeanException{
		assetActivityBean = BeanManager.getManagerBean(AssetActivity.class);
		criteria = new Criteria();
		String identifier;
		
		if(getDateRange().equals("YEAR")){
			identifier = assetActivityBean.getFieldName(IAssetAlias.ASSET_ACTIVITY_DATE);
			criteria.addBetweenExpression(identifier, fromDate, toDate);
			identifier = assetActivityBean.getFieldName(IAssetAlias.ASSET_ACTIVITY_ASSET_ID);
			criteria.addOrder(identifier);
		} else if(getDateRange().equals("MONTH")){
			identifier = assetActivityBean.getFieldName(IAssetAlias.ASSET_ACTIVITY_DATE);
			criteria.addBetweenExpression(identifier, fromDate, toDate);
			if(getStatType().equals("ASSET"))
				identifier = assetActivityBean.getFieldName(IAssetAlias.ASSET_ACTIVITY_ASSET_NAME);
			if(getStatType().equals("USER"))
				identifier = assetActivityBean.getFieldName(IAssetAlias.ASSET_ACTIVITY_WHO);
			criteria.addEqualExpression(identifier, name);
			identifier = assetActivityBean.getFieldName(IAssetAlias.ASSET_ACTIVITY_DATE);
			criteria.addOrder(identifier);
		} else if(getDateRange().equals("DAY")){
			identifier = assetActivityBean.getFieldName(IAssetAlias.ASSET_ACTIVITY_DATE);
			criteria.addBetweenExpression(identifier, fromDate, toDate);
			if(getStatType().equals("ASSET"))
				identifier = assetActivityBean.getFieldName(IAssetAlias.ASSET_ACTIVITY_ASSET_NAME);
			if(getStatType().equals("USER"))
				identifier = assetActivityBean.getFieldName(IAssetAlias.ASSET_ACTIVITY_WHO);
			criteria.addEqualExpression(identifier, name);
			identifier = assetActivityBean.getFieldName(IAssetAlias.ASSET_ACTIVITY_DATE);
			criteria.addOrder(identifier);
			
			
		} else if(getStatType().equals("USER"))
			System.out.println("user");
		
		
		
	}
	
	public void buildStatsByYear() throws ManagerBeanException {
		//List<AssetStat> activities = new LinkedList<AssetStat>();
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
					//stat.setType(StatType.anual);
					stat.setUser(activity.getWho());
				} else {
					stat = new AssetStat();
					stat.setName(activity.getAsset().getName());
					stat.setKey(activity.getAsset().getId().toString());
					stat.setDate(activity.getDate());
					stat.setHours(hours);
					stat.setRequest(1);
					stat.setAverage(stat.getHours()/stat.getRequest());
					//stat.setType(StatType.anual);
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
					//stat.setType(StatType.anual);
					stat.setUser(activity.getWho());
				} else {
					stat = new AssetStat();
					stat.setName(activity.getWho());
					stat.setKey(activity.getId().toString());
					stat.setDate(activity.getDate());
					stat.setHours(hours);
					stat.setRequest(1);
					stat.setAverage(stat.getHours()/stat.getRequest());
					//stat.setType(StatType.anual);
					stat.setUser(activity.getWho());
					stats.add(stat);
				}
			
			}
			
			previous = activity;
		}
	}
	
	
	
	public void buildStatsByMonth() throws ManagerBeanException {
		stats = new LinkedList<AssetStat>();
		for(int i=0;i<12;i++){
			stats.add(i, new AssetStat());
			//stats.get(i).setName(Month.getMonthByValue(i).getName(FacesContext.getCurrentInstance().getViewRoot().getLocale()));
			stats.get(i).setKey(String.valueOf(i));
			stats.get(i).setName(Month.getMonthByValue(i).getName(locale));
			//stat.setKey(activity.getId().toString());
			stats.get(i).getDate().setMonth(i);
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
			
			stat = stats.get(activity.getDate().getMonth());
			stat.setDate(activity.getDate());
			stat.setHours(stat.getHours()+hours);
			stat.setRequest(stat.getRequest()+1);
			stat.setAverage(stat.getHours()/stat.getRequest());
			stat.setUser(activity.getWho());
			stats.set(stat.getDate().getMonth(), stat);
		}
	}
	
	// ESTES PA LUEGO!!!!!!
	public void buildStatsByDay() throws ManagerBeanException {
		stats = new LinkedList<AssetStat>();
		Calendar cal = new GregorianCalendar();
		cal.set(year, month, 1);
		int days = cal.getActualMaximum(Calendar.DATE);
		System.out.println(cal.getActualMaximum(Calendar.DATE));
		
		
		for(int i=0;i<days;i++){
			stats.add(i, new AssetStat());
			//stats.get(i).setName(Month.getMonthByValue(i).getName(FacesContext.getCurrentInstance().getViewRoot().getLocale()));
			stats.get(i).setKey(String.valueOf(i));
			stats.get(i).setName(String.valueOf(i));
			//stats.get(i).getDate().setMonth(i);
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
			
			stat = stats.get(activity.getDate().getDate()-1);
			stat.setDate(activity.getDate());
			stat.setHours(stat.getHours()+hours);
			stat.setRequest(stat.getRequest()+1);
			stat.setAverage(stat.getHours()/stat.getRequest());
			stat.setUser(activity.getWho());
			stats.set(stat.getDate().getDate()-1, stat);
		}
		
		
		
		
	}
	public void buildStatsByUser() {
		
	}
	
	
	
}