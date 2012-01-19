package com.esferalia.aon.ui.pms.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.asset.Asset;
import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.Feature;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.Room;

public class ReservationTableController {
	
	private AssetActivity selectedAssetActivity;
	
	private DataModel assetModel;
	
	private List<AssetDay> assetDayList = new ArrayList<AssetDay>();
	
	private FilterParams filterParams;

	private Room room;
	
	public FilterParams getFilterParams() {
		if(filterParams == null){
			filterParams = new FilterParams();
		}
		return filterParams;
	}

	public void setFilterParams(FilterParams filterParams) {
		this.filterParams = filterParams;
	}

	public List<AssetDay> getAssetDay(){
		return assetDayList;
	}
	
	public DataModel getAssetModel() throws ManagerBeanException {
		if(assetModel==null){
			initialize();
		}
		return assetModel;
	}
	public void setAssetModel(DataModel assetModel) {
		this.assetModel = assetModel;
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
	
	public AssetActivity getSelectedAssetActivity() {
		return selectedAssetActivity;
	}
	public void setSelectedAssetActivity(AssetActivity selectedAssetActivity) {
		this.selectedAssetActivity = selectedAssetActivity;
	}
	public void onSelectActivity(ActionEvent event){
		getSelectedAssetActivity();
	}
	
	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public boolean isPrintWeekend(){
		AssetRack r;
		try {
			r = (AssetRack) getAssetModel().getRowData();
			Date d = getViewerDays().get(r.getAssetDayModel().getRowIndex());
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			return cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY;
		} catch (ManagerBeanException e) {
			// TODO: handle exception
		}
		return false;
	}
	
	public void loadFilterParams( ProjectReservationRoom reservation ){
		getFilterParams().setHotel(reservation.getProjectReservation().getHotel());
		getFilterParams().setViewerStartDate(reservation.getProjectReservation().getStartDate());
		Integer availabilityDays = (int)CommonUtil.getDaysBetweenDates(reservation.getProjectReservation().getStartDate(), reservation.getProjectReservation().getEndDate());
		getFilterParams().setAssetAvailability(availabilityDays);
		getFilterParams().setItem(reservation.getItem());
	}
	
	/////////////////////////////
	// ACTION LISTENERS
	/////////////////////////////
	
	public void onSelectRoomModal(ActionEvent event) {
		try {
			AssetRack ar = (AssetRack)getAssetModel().getRowData();
			setRoom((Room) BeanManager.getManagerBean(Room.class).get(ar.getAsset().getId()));
		} catch (ManagerBeanException e) {
			// TODO 
		}
	}
	public void increaseStartDate(ActionEvent event) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getFilterParams().getViewerStartDate());
		cal.add(Calendar.DAY_OF_MONTH, getFilterParams().getStartDateIncrease());
		getFilterParams().setViewerStartDate(cal.getTime());
		initialize();
	}
	public void decreaseStartDate(ActionEvent event) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getFilterParams().getViewerStartDate());
		cal.add(Calendar.DAY_OF_MONTH, -1*getFilterParams().getStartDateIncrease());
		getFilterParams().setViewerStartDate(cal.getTime());
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
			setAssetModel( null );
			initializeRackAssetModel();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("error al iniciar.");
		}
	}
	
	public List<Date> getViewerDays() {
		List<Date> dayList = new ArrayList<Date>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(getFilterParams().getViewerStartDate());
		for(int i=0; i<31; i++){
			dayList.add(cal.getTime());
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		return dayList;
	}
	
	public void initializeRackAssetModel() throws ManagerBeanException {

		String featureClause = null;
		if( getFilterParams().getFeatureFilter() != null && getFilterParams().getFeatureFilter().length > 0 ){
			for( Integer id : getFilterParams().getFeatureFilter() ) {
				if ( featureClause == null ) {
					featureClause = "AssetFeature.feature = ";
				} else {
					featureClause += "OR AssetFeature.feature = ";
				}
				featureClause += id + " ";
			}
			featureClause = "AND (" + featureClause + ") ";
		}
		
		String availableClause = null;
		if( getFilterParams().getAssetAvailability() != null && getFilterParams().getAssetAvailability() > 0 ){
			Calendar cal = Calendar.getInstance();
			cal.setTime(getFilterParams().getViewerStartDate());
			availableClause = "AND Room.asset not in (select asset from asset_activity where date BETWEEN '";
			availableClause += new java.sql.Date(cal.getTimeInMillis()) + "' ";
			availableClause += "AND '";
			cal.add(Calendar.DAY_OF_MONTH, getFilterParams().getAssetAvailability()-1);
			availableClause += new java.sql.Date(cal.getTimeInMillis()) + "' ";
			availableClause += " )";
			availableClause += " ";
		}
		
//		String availableClause = null;
//		if( getFilterParams().getAssetAvailability() != null && getFilterParams().getAssetAvailability() > 0 ){
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(getFilterParams().getViewerStartDate());
//			availableClause = "AND Room.asset not in (select asset from project_reservation_room_detail where effective_date BETWEEN '";
//			availableClause += new java.sql.Date(cal.getTimeInMillis()) + "' ";
//			availableClause += "AND '";
//			cal.add(Calendar.DAY_OF_MONTH, getFilterParams().getAssetAvailability()-1);
//			availableClause += new java.sql.Date(cal.getTimeInMillis()) + "' ";
//			availableClause += " )";
//			availableClause += " ";
//		}
		
		String itemClause = null;
		if( getFilterParams().getItem() != null && getFilterParams().getItem().getId() != null ){
			itemClause = "AND Room.item = ";
			itemClause += getFilterParams().getItem().getId();
			itemClause += " ";
		}
		
		String hotelClause = null;
		if( getFilterParams().getHotel() != null && getFilterParams().getHotel().getId() != null ){
			hotelClause = "AND Room.hotel = ";
			hotelClause += getFilterParams().getHotel().getId();
			hotelClause += " ";
		}
		
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		
//		String select = "SELECT Asset "
//			+ "FROM AssetActivity as AssetActivity LEFT JOIN AssetActivity.asset as Asset, " 
//			+ "AssetFeature as AssetFeature RIGHT JOIN AssetFeature.asset as Asset "
//			+ "WHERE 1 = 1 "
//			+ (featureClause == null ? "" : featureClause) 
//			+ (availableClause == null ? "" : availableClause) 
//			+ "GROUP BY Asset.id "
//			+ "ORDER BY Asset.id"
//			;
//		Query query = session.createQuery(select);
//		
//		String sqlSelect = "SELECT Asset.* "
//			+ " FROM (asset as Asset "
//			+ " LEFT JOIN asset_activity as AssetActivity on AssetActivity.asset = Asset.id) "
//			+ " LEFT JOIN asset_feature as AssetFeature on AssetFeature.asset = Asset.id "
//			
////			+ " FROM (asset_activity as AssetActivity  "
////			+ " RIGHT JOIN asset as Asset on AssetActivity.asset = Asset.id) "
////			+ " RIGHT JOIN asset_feature as AssetFeature on AssetFeature.asset = Asset.id "
//	
//			+ "WHERE 1 = 1 "
//			
//			+ (featureClause == null ? "" : featureClause) 
//			
//			+ (availableClause == null ? "" : availableClause) 
//			
////			+ " AND   CommercialTracking.date >= '"
////			+ new java.sql.Date(this.params.getFromDate().getTime())
////			+ "' AND CommercialTracking.date <= '"
////			+ new java.sql.Date(this.params.getToDate().getTime())
//			+ "GROUP BY Asset.id "
//			+ "ORDER BY Asset.id, AssetActivity.day asc"
//
//			;
		
		String sqlSelect = "SELECT Room.* "
			+ " FROM room as Room "
			+ " LEFT JOIN asset_feature as AssetFeature on AssetFeature.asset = Room.asset "
			+ "WHERE 1 = 1 "
			+ (featureClause == null ? "" : featureClause) 
			+ (availableClause == null ? "" : availableClause) 
			+ (itemClause == null ? "" : itemClause) 
			+ (hotelClause == null ? "" : hotelClause) 
			+ "GROUP BY Room.asset "
			+ "ORDER BY Room.asset"
			;
		Query sqlQuery = session.createSQLQuery(sqlSelect);
		
		List<AssetRack> list = new LinkedList<AssetRack>();
		for(Object to: sqlQuery.list()){
			Asset a = getAsset((Integer)(((Object[])to)[0]));
			AssetRack r = new AssetRack();
			r.setAsset(a);
			r.setAssetDayModel(new ListDataModel(buildAssetDayList(a)));
			list.add(r);
		}

		assetModel = new ListDataModel(list);	
	}
	
	private Asset getAsset(Integer id) {
		try {
			return (Asset) BeanManager.getManagerBean(Asset.class).get(id);
		} catch (ManagerBeanException e) {
			// TODO: handle exception
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
		assetModel = new ListDataModel(assetList);
	}
	
	public List<AssetDay> buildAssetDayList(Asset a){
		List<AssetDay> assetDayList = new ArrayList<AssetDay>(31);
		AssetDay ad;
		for(int i=0; i<getViewerDays().size(); i++){
			ad = new AssetDay();
			ad.setAsset(a);
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
						AssetDay d = assetDayList.get(pos);
						d.setAsset(aa.getAsset());
						d.setActivity(aa);
					}
				}
			}
		} catch (ManagerBeanException e) {
			// TODO: handle exception
			e.printStackTrace();
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
			}
		}
		return -1;
	}
	
	/* ****************/
	/* CLASES WRAPPED */
	/* ****************/
	
	public class AssetRack{
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
	
	public class AssetDay {
		private Asset asset;
		private AssetActivity activity;

		public Asset getAsset() {
			return asset;
		}
		public void setAsset(Asset asset) {
			this.asset = asset;
		}
		public AssetActivity getActivity() {
			return activity;
		}
		public void setActivity(AssetActivity activity) {
			this.activity = activity;
		}
		
	}
	
	public class FilterParams {
		private Date viewerStartDate;
		private Integer startDateIncrease;
		private Integer assetAvailability;
		private Integer[] featureFilter;
		private Item item;
		private Hotel hotel;
		
		public FilterParams(){
			viewerStartDate = new Date();
			assetAvailability = 0;
		}
		
		public Item getItem() {
			return item;
		}
		public void setItem(Item item) {
			this.item = item;
		}
		public Integer[] getFeatureFilter() {
			return featureFilter;
		}
		public void setFeatureFilter(Integer[] featureFilter) {
			this.featureFilter = featureFilter;
		}
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
			return viewerStartDate;
		}
		public void setViewerStartDate(Date viewerStartDate) {
			this.viewerStartDate = viewerStartDate;
		}
		public Hotel getHotel() {
			return hotel;
		}
		public void setHotel(Hotel hotel) {
			this.hotel = hotel;
		}
		
	}
		
}
