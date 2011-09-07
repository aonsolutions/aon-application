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

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.GeozoneIrpf;
import com.esferalia.aon.payroll.GeozoneIrpfDescendant;
import com.esferalia.aon.payroll.GeozoneIrpfHandicap;
import com.esferalia.aon.payroll.dao.IPayrollAlias;


public class GeozoneIrpfController {
	
	private final static Integer ARABA_ID = 1;
	private final static Integer BIZKAIA_ID = 48;
	private final static Integer GIPUZKOA_ID = 20;
	private final static Integer NAFARROA_ID = 31;
	
	private DataModel irpfModel;
	private DataModel descendantModel;
	private DataModel handicapModel;
	private GeozoneIrpfList selectedIrpf;
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
	public GeozoneIrpfList getSelectedIrpf(){
		return selectedIrpf;
	}
	public void setSelectedIrpf(GeozoneIrpfList selectedIrpf) {
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
		setYear(CommonUtil.getYear(new Date()));
	}
	
	public void onSearch(ActionEvent event){
		initializeIrpfModel();
	}
	
	public void onSelectGeozone(ActionEvent event){
		setSelectedIrpf((GeozoneIrpfList) getIrpfModel().getRowData());
		try {
			initializeDescendantsModel();
			initializeHasndicapModel();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("error on onSelectGeozone");
		}
	}
	
	private void initializeIrpfModel() {
		List<GeozoneIrpfList> list = new LinkedList<GeozoneIrpfList>();
		GeozoneIrpfList g;
		try {
			IManagerBean bean = BeanManager.getManagerBean(GeozoneIrpf.class);
			Criteria criteria = new Criteria();
			completeCriteria(criteria);
			criteria.addOrder(bean.getFieldName(IPayrollAlias.GEOZONE_IRPF_GEOZONE_ID));
			criteria.addOrder(bean.getFieldName(IPayrollAlias.GEOZONE_IRPF_START_DATE));
			List<ITransferObject> irpfList = bean.getList(criteria);
			for(ITransferObject to: irpfList){
				GeozoneIrpf irpf = (GeozoneIrpf) to;
				g = new GeozoneIrpfList();
				g.setGeozone(irpf.getGeozone());
				g.setYear(irpf.getYear());
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
		GeozoneIrpfList irpf = getSelectedIrpf();
		IManagerBean bean = BeanManager.getManagerBean(GeozoneIrpfDescendant.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF_GEOZONE_ID), irpf.getGeozone().getId());
		criteria.addOrder(bean.getFieldName(IPayrollAlias.GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF_AMOUNT));
		criteria.addOrder(bean.getFieldName(IPayrollAlias.GEOZONE_IRPF_DESCENDANT_DESCENDANT));
		List<ITransferObject> descList = bean.getList(criteria);
		List<GeozoneIrpfDescendantsList> list = new LinkedList<GeozoneIrpfDescendantsList>();
		GeozoneIrpfDescendantsList gl = new GeozoneIrpfDescendantsList();
		for(ITransferObject to: descList){
			GeozoneIrpfDescendant g = (GeozoneIrpfDescendant) to;
			if(list.isEmpty()){
				gl.setFromAmount(g.getGeozoneIrpf().getAmount());
				list.add(gl);
			} else if(!gl.getFromAmount().equals(g.getGeozoneIrpf().getAmount())){
				gl.setToAmount(g.getGeozoneIrpf().getAmount());
				gl = new GeozoneIrpfDescendantsList();
				gl.setFromAmount(g.getGeozoneIrpf().getAmount());
				list.add(gl);
			}
			GeozoneIrpfCount descendants = new GeozoneIrpfCount();
			descendants.setCount(g.getDescendant());
			descendants.setPercent(g.getPercent());
			gl.getDescendants().add(descendants);
		}
		
		setDescendantModel(new ListDataModel(list));
	}
	private void initializeHasndicapModel() throws ManagerBeanException {
		GeozoneIrpfList irpf = getSelectedIrpf();
		IManagerBean bean = BeanManager.getManagerBean(GeozoneIrpfHandicap.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF_GEOZONE_ID), irpf.getGeozone().getId());
		criteria.addOrder(bean.getFieldName(IPayrollAlias.GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF_AMOUNT));
		criteria.addOrder(bean.getFieldName(IPayrollAlias.GEOZONE_IRPF_HANDICAP_HANDICAP));
		List<ITransferObject> handicapList = bean.getList(criteria);
		List<GeozoneIrpfHandicapList> list = new LinkedList<GeozoneIrpfHandicapList>();
		GeozoneIrpfHandicapList gl = new GeozoneIrpfHandicapList();
		for(ITransferObject to: handicapList){
			GeozoneIrpfHandicap g = (GeozoneIrpfHandicap) to;
			if(list.isEmpty()){
				gl.setFromAmount(g.getGeozoneIrpf().getAmount());
				list.add(gl);
			} else if(!gl.getFromAmount().equals(g.getGeozoneIrpf().getAmount())){
				gl.setToAmount(g.getGeozoneIrpf().getAmount());
				gl = new GeozoneIrpfHandicapList();
				gl.setFromAmount(g.getGeozoneIrpf().getAmount());
				list.add(gl);
			}
			GeozoneIrpfCount handicap = new GeozoneIrpfCount();
			handicap.setCount(g.getHandicap());
			handicap.setPercent(g.getPercent());
			gl.getHandicap().add(handicap);
		}
		setHandicapModel(new ListDataModel(list));
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
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IPayrollAlias.GEOZONE_IRPF_START_DATE), startCal.getTime());
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IPayrollAlias.GEOZONE_IRPF_END_DATE), endCal.getTime());
		}
		if(getAdministration()!=null){
			if(getAdministration()== Administration.ALAVA){
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.GEOZONE_IRPF_GEOZONE_ID), ARABA_ID);
			} else if(getAdministration()== Administration.BIZKAIA){				
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.GEOZONE_IRPF_GEOZONE_ID), BIZKAIA_ID);
			} else if(getAdministration()== Administration.GIPUZKOA){
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.GEOZONE_IRPF_GEOZONE_ID), GIPUZKOA_ID);
			} else if(getAdministration()== Administration.NAVARRA){
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.GEOZONE_IRPF_GEOZONE_ID), NAFARROA_ID);
			}
		}
	}
	
	

	public class GeozoneIrpfList{
		private Integer id;
		private GeoZone geozone;
		private Integer year;
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public GeoZone getGeozone() {
			return geozone;
		}
		public void setGeozone(GeoZone geozone) {
			this.geozone = geozone;
		}
		public Integer getYear() {
			return year;
		}
		public void setYear(Integer year) {
			this.year = year;
		}
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (this == obj) return true;
			if (obj.getClass() != getClass()) return false;
			final GeozoneIrpfList o =  (GeozoneIrpfList) obj;
			if (o.getId() == null && getId() == null) {
				return new EqualsBuilder()
					.append(this.geozone, o.geozone)			
					.append(this.year, o.year)			
					.isEquals();
			}
			return ObjectUtils.equals(getId(), o.getId());		
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
	public class GeozoneIrpfDescendantsList{
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
	public class GeozoneIrpfHandicapList{
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