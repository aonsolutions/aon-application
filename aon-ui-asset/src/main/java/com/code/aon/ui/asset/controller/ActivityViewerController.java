package com.code.aon.ui.asset.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.asset.Asset;
import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.AssetType;
import com.code.aon.asset.Feature;
import com.code.aon.asset.enumeration.ViewerType;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ActivityViewerController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private ViewerType viewerType;
	private AssetType assetType;
	private Feature feature;
	private Date viewerStartDate;
	private Integer startDateIncrease;
	private Integer assetAvailability;
	private List<Integer> timeFractionList;
	private AssetActivity selectedAssetActivity;
	private DataModel assetModel;
	private List<DayAssetList> dayAssetList;
	private Integer[] featureFilter;
	private List<AssetDayList> assetDayList = new ArrayList<AssetDayList>();
	
	
	public Integer getAssetAvailability() {
		return assetAvailability;
	}
	public void setAssetAvailability(Integer assetAvailability) {
		this.assetAvailability = assetAvailability;
	}
	public Integer getStartDateIncrease() {
		return startDateIncrease;
	}
	public void setStartDateIncrease(Integer startDateIncrease) {
		this.startDateIncrease = startDateIncrease;
	}
	public Date getViewerStartDate() {
		if(viewerStartDate==null){
			viewerStartDate = new Date();
		}
		return viewerStartDate;
	}
	public void setViewerStartDate(Date viewerStartDate) {
		this.viewerStartDate = viewerStartDate;
	}
	public ViewerType getViewerType() {
		return viewerType;
	}
	public void setViewerType(ViewerType viewerType) {
		this.viewerType = viewerType;
	}
	public AssetType getAssetType() {
		return assetType;
	}
	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
	}
	public Feature getFeature() {
		return feature;
	}
	public void setFeature(Feature feature) {
		this.feature = feature;
	}
	
	
	
	public AssetActivity getSelectedAssetActivity() {
		return selectedAssetActivity;
	}
	public void setSelectedAssetActivity(AssetActivity selectedAssetActivity) {
		this.selectedAssetActivity = selectedAssetActivity;
	}
	public void onSelectActivity(ActionEvent event){
		getSelectedAssetActivity();
	}
	
	
	public boolean isPrintWeekend(){
		AssetReservation r;
		try {
			r = (AssetReservation) getAssetModel().getRowData();
			Date d = getViewerDays().get(r.getAssetDayModel().getRowIndex());
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			return cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY;
		} catch (ManagerBeanException e) {
			// TODO: handle exception
		}
		return false;
	}
	
	public void increaseStartDate(ActionEvent event) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getViewerStartDate());
		cal.add(Calendar.DAY_OF_MONTH, startDateIncrease);
		setViewerStartDate(cal.getTime());
		initialize();
	}
	public void decreaseStartDate(ActionEvent event) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getViewerStartDate());
		cal.add(Calendar.DAY_OF_MONTH, -1*startDateIncrease);
		setViewerStartDate(cal.getTime());
		initialize();
	}
	public void onInitialize(ActionEvent event) {
		initialize();
	}
	public void reloadViewer(LookupChangeEvent event) {
		initialize();
	}
	public void reloadViewer(ActionEvent event) {
		initialize();
	}
	public void initialize() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Feature.class);
			setFeature((Feature) bean.createNewTo());
			
			setAssetModel(null);
			initializeAssetReservationModel();
			buildAssetDayList();
		} catch (ManagerBeanException e) {
			// nada...
		}
		ActivityDialogController adc = new ActivityDialogController();
		adc.onInitializeRequest(null);
	}
	
	public List<Date> getViewerDays() {
		List<Date> dayList = new ArrayList<Date>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(getViewerStartDate());
		for(int i=0; i<31; i++){
			dayList.add(cal.getTime());
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		return dayList;
	}
	
	
	public DataModel getAssetModel() throws ManagerBeanException {
		if(assetModel==null){
			initialize();
			ActivityDialogController adc = (ActivityDialogController)AonUtil.getRegisteredBean(IAssetConstants.ACTIVITY_DIALOG_CONTROLLER_NAME);
			adc.onInitializeRequest(null);
		}
		return assetModel;
	}
	public void setAssetModel(DataModel assetModel) {
		this.assetModel = assetModel;
	}
	
	public List<DayAssetList> getDayAssetList() {
		return dayAssetList;
	}

	public void setDayAssetList(List<DayAssetList> dayAssetList) {
		this.dayAssetList = dayAssetList;
	}	
	
	
	public Integer[] getFeatureFilter() {
		return featureFilter;
	}
	public void setFeatureFilter(Integer[] featureFilter) {
		this.featureFilter = featureFilter;
	}
	public List<SelectItem> getFeaturesList(){
		List<SelectItem> itemsList;
		try {
			IManagerBean bean = BeanManager.getManagerBean(Feature.class);
			List<ITransferObject> list = bean.getList(null);
			if(list!=null){
				itemsList = new LinkedList<SelectItem>();
				for(ITransferObject to: list){
					Feature f = (Feature) to;
					String name = f.getName();
					SelectItem item = new SelectItem(f.getId(), name);
					itemsList.add(item);			
				}
				return itemsList;
			}
			
		} catch (ManagerBeanException e) {
			// TODO: handle exception
		}
		return null;
	}

	
	public List<Integer> getTimeFractionList() {
		if(timeFractionList != null){
			timeFractionList = new ArrayList<Integer>();
			for(int i=0; i<31; i++){
				timeFractionList.add(i,i);
			}
		}
		return timeFractionList;
	}

	public int getFractionTime() {
		return IAssetConstants.FRACTION_TIME;
	}
	
	public void initializeAssetReservationModel() throws ManagerBeanException {

		String featureClause = null;
		if( getFeatureFilter() != null && getFeatureFilter().length > 0 ){
			for( Integer id : getFeatureFilter() ) {
				if ( featureClause == null ) {
					featureClause = "AssetFeature.feature = ";
				} else {
					featureClause += "OR AssetFeature.feature = ";
				}
				featureClause += id + " ";
			}
			featureClause = "AND (" + featureClause + ") ";
		}

		String availableClause2 = null;
		if( getAssetAvailability() != null && getAssetAvailability() > 0 ){
			Calendar cal = Calendar.getInstance();
			cal.setTime(getViewerStartDate());
			availableClause2 = "AND Asset.id not in (select asset from asset_activity where " 
								+ DomainManager.getSQLWhereClause("asset_activity.domain") 
								+ "AND day BETWEEN '";
			availableClause2 += new java.sql.Date(cal.getTimeInMillis()) + "' ";
			availableClause2 += "AND '";
			cal.add(Calendar.DAY_OF_MONTH, getAssetAvailability()-1);
			availableClause2 += new java.sql.Date(cal.getTimeInMillis()) + "' ";
			availableClause2 += " )";
			availableClause2 += " ";
		}
		
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		String sqlSelect = "SELECT Asset.* "
			+ " FROM asset as Asset "
			+ " LEFT JOIN asset_feature as AssetFeature on AssetFeature.asset = Asset.id "
			+ "WHERE " + DomainManager.getSQLWhereClause("Asset.domain")
			+ (featureClause != null || availableClause2 != null?"":" AND ")
			+ (featureClause == null ? "" : featureClause) 
			+ (availableClause2 == null ? "" : availableClause2) 
			+ "GROUP BY Asset.id "
			+ "ORDER BY Asset.id"
			;
		Query sqlQuery = session.createSQLQuery(sqlSelect);
		
		List<AssetReservation> list = new LinkedList<AssetReservation>();
		for(Object to: sqlQuery.list()){
			Asset a = getAsset((Integer)(((Object[])to)[0]));
			AssetReservation r = new AssetReservation();
			r.setAsset(a);
			r.setAssetDayModel(new SerializableListDataModel(buildAssetDayList(a)));
			list.add(r);
		}

		assetModel = new SerializableListDataModel(list);	
	}
	
	private Asset getAsset(Integer id) {
		try {
			return (Asset) BeanManager.getManagerBean(Asset.class).get(id);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	protected void addFeaturesToCriteria( Criteria criteria, String alias, Object[] values ) throws ManagerBeanException {
		Expression expToAdd = null;
		for( Object value : values ) {
			if ( value != null ) {
				if ( expToAdd == null ) {
					expToAdd = ExpressionUtilities.getEqualExpression(alias, value);				
				} else {
					Expression exp  = ExpressionUtilities.getEqualExpression(alias, value);
					expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				}
			}
		}
		if ( expToAdd != null ) {
			criteria.addExpression(expToAdd);
		}
	}	
	
	public void initializeAssetModel() throws ManagerBeanException {
		List<ITransferObject> assetList = BeanManager.getManagerBean(Asset.class).getList(null);
		assetModel = new SerializableListDataModel(assetList);
	}
	
	public List<AssetDayList> getAssetDayList(){
		return assetDayList;
	}
	public List<AssetDayList> buildAssetDayList(){
		if(assetDayList==null){
			Asset a = null;
			try {
				a = (Asset) getAssetModel().getRowData();
			} catch (ManagerBeanException e1) {
				// TODO: handle exception
			}
			AssetDayList d;
			for(int i=0; i<getViewerDays().size(); i++){
				d = new AssetDayList();
				d.setAsset(a);
				d.setFractions(new LinkedList<Fraction>());
				d.getFractions().add(new Fraction(false));
				assetDayList.add(new AssetDayList());
			}
		}
		
		return assetDayList;
	}
	public List<AssetDayList> buildAssetDayList(Asset a){
		List<AssetDayList> assetDayList = new ArrayList<AssetDayList>(31);
		AssetDayList ad;
		for(int i=0; i<getViewerDays().size(); i++){
			ad = new AssetDayList();
			ad.setAsset(a);
			ad.setFractions(new LinkedList<Fraction>());
			ad.getFractions().add(new Fraction(false));
			assetDayList.add(ad);
		}
		try {
			IManagerBean bean = BeanManager.getManagerBean(AssetActivity.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ASSET_ACTIVITY_ASSET_ID), a.getId());
			criteria.addBetweenExpression(bean.getFieldName(IEntityAlias.ASSET_ACTIVITY_DATE), getViewerDays().get(0), getViewerDays().get(getViewerDays().size()-1));
			criteria.addOrder(bean.getFieldName(IEntityAlias.ASSET_ACTIVITY_DATE));
			List<ITransferObject> activityList = bean.getList(criteria);
			if(!activityList.isEmpty()){
				for(ITransferObject to: activityList){
					AssetActivity aa = (AssetActivity) to;
					int pos = getActivityDayPos(aa.getDate());
					if(pos>=0 && pos<getViewerDays().size()){
						AssetDayList d = assetDayList.get(pos);
						d.setAsset(aa.getAsset());
						d.setFractions(new LinkedList<Fraction>());
						Fraction f = new Fraction(true);
						f.setActivity(aa);
						d.getFractions().add(f);
					}
				}
			}
		} catch (ManagerBeanException e) {
			// TODO: handle exception
		}
		return assetDayList;
	}
	
	private int getActivityDayPos(Date date){
		ListIterator<Date> i = getViewerDays().listIterator();
		Calendar dayCal = Calendar.getInstance();
		dayCal.setTime(date);
		dayCal.set(Calendar.HOUR_OF_DAY, 0);
		dayCal.set(Calendar.MINUTE, 0);
		dayCal.set(Calendar.SECOND, 0);
		dayCal.set(Calendar.MILLISECOND, 0);
		Calendar cal = Calendar.getInstance();
		while(i.hasNext()){
			Date d = i.next();
			cal.setTime(d);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			if(dayCal.equals(cal)){
				return i.nextIndex()-1;
//				return getViewerDays().indexOf(d);
			}
		}
		return -1;
	}
	
	/* ****************/
	/* CLASES WRAPPED */
	/* ****************/
	
	public class AssetReservation {
		private Asset asset;
		private DataModel assetDayModel;
		public Asset getAsset() {
			return asset;
		}
		public void setAsset(Asset asset) {
			this.asset = asset;
		}
		public DataModel getAssetDayModel() {
			return assetDayModel;
		}
		public void setAssetDayModel(DataModel assetDayModel) {
			this.assetDayModel = assetDayModel;
		}
	}
	
	public static class AssetDayList implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private Asset asset;
		private List<Fraction> fractions;

		public Asset getAsset() {
			return asset;
		}
		public void setAsset(Asset asset) {
			this.asset = asset;
		}
		public List<Fraction> getFractions() {
			return fractions;
		}
		public void setFractions(List<Fraction> fractions) {
			this.fractions = fractions;
		}
		
	}
	
	
	public static class DayAssetList implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private Asset asset;
		private List<Fraction> fractions;

		public List<Fraction> getFractions() {
			return fractions;
		}

		public void setFractions(List<Fraction> fractions) {
			this.fractions = fractions;
		}
		
		public Asset getAsset() {
			return asset;
		}
		public void setAsset(Asset asset) {
			this.asset = asset;
		}
	}

	public static class Fraction implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private AssetActivity activity;
		private boolean reserved;
		private boolean first;
		
		public Fraction(boolean reserved){
			this.reserved = reserved;
			//this.first = true;
		}

		public boolean isReserved() {
			return reserved;
		}

		public void setReserved(boolean reserved) {
			this.reserved = reserved;
		}

		public boolean isFirst() {
			return first;
		}

		public void setFirst(boolean first) {
			this.first = first;
		}
		public AssetActivity getActivity() {
			return activity;
		}

		public void setActivity(AssetActivity activity) {
			this.activity = activity;
		}
		
		public boolean isPrintWeekend(){
			if(getActivity()==null || getActivity().getDate()==null){
				return false;
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(getActivity().getDate());
			return cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY;
		}
	}
		
}
