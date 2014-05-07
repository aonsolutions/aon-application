package com.esferalia.aon.ui.payroll.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.GeozoneIrpf;
import com.esferalia.aon.payroll.GeozoneIrpfDescendant;
import com.esferalia.aon.payroll.GeozoneIrpfHandicap;


public class GeozoneIrpfController {
	
	private final static Integer ARABA_ID = 1;
	private final static Integer BIZKAIA_ID = 48;
	private final static Integer GIPUZKOA_ID = 20;
	private final static Integer NAFARROA_ID = 31;
	private final static String ARABA_CODE = "01";
	private final static String BIZKAIA_CODE = "48";
	private final static String GIPUZKOA_CODE = "20";
	private final static String NAFARROA_CODE = "31";
	
	private DataModel irpfModel;
	private DataModel descendantModel;
	private DataModel handicapModel;
	private GeoIrpf selectedIrpf;
	private Administration administration;
	private Integer year;
	private List<SelectItem> administrationList;
	private String beanName;
	
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	public Administration getAdministration() {
		return administration;
	}
	public void setAdministration(Administration administration) {
		this.administration = administration;
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
	public GeoIrpf getSelectedIrpf(){
		return selectedIrpf;
	}
	public void setSelectedIrpf(GeoIrpf selectedIrpf) {
		this.selectedIrpf = selectedIrpf;
	}
	
	public DataModel getIrpfModel() {
		return irpfModel;
	}
	public void setIrpfModel(DataModel irpfModel) {
		this.irpfModel = irpfModel;
	}
	
	public DataModel getDescendantModel() {
		return descendantModel;
	}
	public void setDescendantModel(DataModel descendantModel) {
		this.descendantModel = descendantModel;
	}
	public DataModel getHandicapModel() {
		return handicapModel;
	}
	public void setHandicapModel(DataModel handicapModel) {
		this.handicapModel = handicapModel;
	}
	
	public List<SelectItem> getAdministrationList() {
		if (administrationList == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			administrationList = new LinkedList<SelectItem>();
			for( Administration a : Administration.values() ) {
				if(a!=Administration.COMMON_TERRITORY){
					String name = a.getName(locale);
					SelectItem item = new SelectItem(a, name);
					administrationList.add(item);			
				}
			}
		}
		return administrationList;
	}
	
	public void onInitialize(ActionEvent event){
		setIrpfModel(null);
		setDescendantModel(null);
		setHandicapModel(null);
		setSelectedIrpf(null);
		setAdministration(null);
		setYear(CommonUtil.getYear(new Date()));		
	}
	
	public void onSearch(ActionEvent event){
		initializeIrpfModel();
	}
	
	public void onSelectGeozone(ActionEvent event){
		setSelectedIrpf((GeoIrpf) getIrpfModel().getRowData());
		try {
			initializeDescendantsModel();
			initializeHandicapModel();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("error on onSelectGeozone");
		}
	}
	
	public void onReloadModel(ActionEvent event){
		try {
			if(getAdministration()!=null && getYear()!=null){
				GeoIrpf g = new GeoIrpf();
				if(getAdministration()==Administration.ALAVA){
					g.setGeozoneCode(ARABA_CODE);
				} else if(getAdministration()==Administration.BIZKAIA){
					g.setGeozoneCode(BIZKAIA_CODE);
				} else if(getAdministration()==Administration.GIPUZKOA){
					g.setGeozoneCode(GIPUZKOA_CODE);
				} else if(getAdministration()==Administration.NAVARRA){
					g.setGeozoneCode(NAFARROA_CODE);
				} else {
					g.setGeozoneCode(null);
				}
				g.setYear(getYear());
				setSelectedIrpf(g);
				initializeDescendantsModel();
				initializeHandicapModel();
			} else {
				setSelectedIrpf(null);
				setDescendantModel(null);
				setHandicapModel(null);
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("error on onSelectGeozone");
		}
	}
	
	private void initializeIrpfModel() {
		List<GeoIrpf> list = new LinkedList<GeoIrpf>();
		GeoIrpf g;
		try {
			IManagerBean bean = BeanManager.getManagerBean(GeozoneIrpf.class);
			ProjectionList pl = new ProjectionList();
			pl.add(Projection.group(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_GEOZONE_CODE)));
			Criteria criteria = new Criteria();
			completeCriteria(criteria);
			criteria.addOrder(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_GEOZONE_CODE));
			criteria.addOrder(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_START_DATE));
			for(Object o: bean.getList(new ProjectionList(Projection.group(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_GEOZONE_CODE))), criteria) ){
				g = new GeoIrpf();
				g.setGeozoneCode(o.toString());
				g.setYear(getYear());
				if(!list.contains(g)){
					list.add(g);
				}
			}
			setIrpfModel(new ListDataModel(list));
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("error on initializeIrpfModel");
		}
	}
	private void initializeDescendantsModel() throws ManagerBeanException {
		GeoIrpf irpf = getSelectedIrpf();
		IManagerBean bean = BeanManager.getManagerBean(GeozoneIrpfDescendant.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF_GEOZONE_CODE), irpf.getGeozoneCode());
		criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF_START_DATE), getPeriodStartDate(irpf.getYear()));
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF_END_DATE), getPeriodEndDate(irpf.getYear()));
		criteria.addOrder(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF_AMOUNT));
		criteria.addOrder(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_DESCENDANT_DESCENDANT));
		List<ITransferObject> descList = bean.getList(criteria);
		List<GeoIrpfDescendants> list = new LinkedList<GeoIrpfDescendants>();
		GeoIrpfDescendants d = new GeoIrpfDescendants();
		for(ITransferObject to: descList){
			GeozoneIrpfDescendant g = (GeozoneIrpfDescendant) to;
			if(list.isEmpty()){
				d.setFromAmount(g.getGeozoneIrpf().getAmount());
				list.add(d);
			} else if(!d.getFromAmount().equals(g.getGeozoneIrpf().getAmount())){
				d.setToAmount(g.getGeozoneIrpf().getAmount());
				d = new GeoIrpfDescendants();
				d.setFromAmount(g.getGeozoneIrpf().getAmount());
				list.add(d);
			}
			GeozoneIrpfCount descendants = new GeozoneIrpfCount();
			descendants.setCount(g.getDescendant());
			descendants.setPercent(g.getPercent());
			d.getDescendants().add(descendants);
		}
		
		setDescendantModel(new ListDataModel(list));
	}
	private void initializeHandicapModel() throws ManagerBeanException {
		GeoIrpf irpf = getSelectedIrpf();
		IManagerBean bean = BeanManager.getManagerBean(GeozoneIrpfHandicap.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF_GEOZONE_CODE), irpf.getGeozoneCode());
		criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF_START_DATE), getPeriodStartDate(irpf.getYear()));
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF_END_DATE), getPeriodEndDate(irpf.getYear()));
		criteria.addOrder(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF_AMOUNT));
		criteria.addOrder(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_HANDICAP_HANDICAP));
		List<ITransferObject> handicapList = bean.getList(criteria);
		List<GeoIrpfHandicap> list = new LinkedList<GeoIrpfHandicap>();
		GeoIrpfHandicap h = new GeoIrpfHandicap();
		for(ITransferObject to: handicapList){
			GeozoneIrpfHandicap g = (GeozoneIrpfHandicap) to;
			if(list.isEmpty()){
				h.setFromAmount(g.getGeozoneIrpf().getAmount());
				list.add(h);
			} else if(!h.getFromAmount().equals(g.getGeozoneIrpf().getAmount())){
				h.setToAmount(g.getGeozoneIrpf().getAmount());
				h = new GeoIrpfHandicap();
				h.setFromAmount(g.getGeozoneIrpf().getAmount());
				list.add(h);
			}
			GeozoneIrpfCount handicap = new GeozoneIrpfCount();
			handicap.setCount(g.getHandicap());
			handicap.setPercent(g.getPercent());
			h.getHandicap().add(handicap);
		}
		setHandicapModel(new ListDataModel(list));
	}
	
	private Object getPeriodEndDate(Integer year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}
	private Object getPeriodStartDate(Integer year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getMinimum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}
	private void completeCriteria(Criteria criteria) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(GeozoneIrpf.class);
		if(getYear()!=null){
			Calendar startCal = Calendar.getInstance();
			startCal.set(Calendar.YEAR, getYear());
			startCal.set(Calendar.MONTH, Calendar.JANUARY);
			startCal.set(Calendar.DAY_OF_MONTH, 1);
			Calendar endCal = Calendar.getInstance();
			endCal.set(Calendar.YEAR, getYear());
			endCal.set(Calendar.MONTH, Calendar.DECEMBER);
			endCal.set(Calendar.DAY_OF_MONTH, 31);
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_START_DATE), startCal.getTime());
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_END_DATE), endCal.getTime());
		}
		if(getAdministration()!=null){
			if(getAdministration()== Administration.ALAVA){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_GEOZONE_CODE), ARABA_CODE);
			} else if(getAdministration()== Administration.BIZKAIA){				
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_GEOZONE_CODE), BIZKAIA_CODE);
			} else if(getAdministration()== Administration.GIPUZKOA){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_GEOZONE_CODE), GIPUZKOA_CODE);
			} else if(getAdministration()== Administration.NAVARRA){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.GEOZONE_IRPF_GEOZONE_CODE), NAFARROA_CODE);
			}
		}
	}
	

	public class GeoIrpf {
		private String geozoneCode;
		private Integer year;
		
		public String getGeozoneCode() {
			return geozoneCode;
		}
		public void setGeozoneCode(String geozoneCode) {
			this.geozoneCode = geozoneCode;
		}
		public String getGeozoneName() {
			try {
				IManagerBean bean = BeanManager.getManagerBean(GeoZone.class);
				return ((GeoZone)bean.get(Integer.parseInt(this.getGeozoneCode()))).getName();
			} catch (ManagerBeanException e) {
				// NADA 
			}
			return "";
		}
		public Integer getYear() {
			return year;
		}
		public void setYear(Integer year) {
			this.year = year;
		}
	}
	public class GeozoneIrpfCount{
		private Integer count;
		private Double percent;
		public Double getPercent() {
			return percent;
		}
		public Integer getCount() {
			return count;
		}
		public void setCount(Integer count) {
			this.count = count;
		}
		public void setPercent(Double percent) {
			this.percent = percent;
		}
	}
	public class GeoIrpfDescendants{
		private Double fromAmount;
		private Double toAmount;
		private List<GeozoneIrpfCount> descendants;
		public Double getFromAmount() {
			return fromAmount;
		}
		public void setFromAmount(Double fromAmount) {
			this.fromAmount = fromAmount;
		}
		public Double getToAmount() {
			return toAmount;
		}
		public void setToAmount(Double toAmount) {
			this.toAmount = toAmount;
		}
		public List<GeozoneIrpfCount> getDescendants() {
			if(descendants==null){
				descendants = new LinkedList<GeozoneIrpfCount>();
			}
			return descendants;
		}
		public void setDescendants(List<GeozoneIrpfCount> descendants) {
			this.descendants = descendants;
		}
	}
	public class GeoIrpfHandicap{
		private Double fromAmount;
		private Double toAmount;
		private List<GeozoneIrpfCount> handicap;
		public Double getFromAmount() {
			return fromAmount;
		}
		public void setFromAmount(Double fromAmount) {
			this.fromAmount = fromAmount;
		}
		public Double getToAmount() {
			return toAmount;
		}
		public void setToAmount(Double toAmount) {
			this.toAmount = toAmount;
		}
		public List<GeozoneIrpfCount> getHandicap() {
			if(handicap==null){
				handicap = new LinkedList<GeozoneIrpfCount>();
			}
			return handicap;
		}
		public void setHandicap(List<GeozoneIrpfCount> handicap) {
			this.handicap = handicap;
		}
	}

}