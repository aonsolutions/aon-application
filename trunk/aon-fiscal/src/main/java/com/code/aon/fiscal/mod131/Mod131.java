package com.code.aon.fiscal.mod131;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.FiscalActivity;
import com.code.aon.fiscal.FiscalActivityInfo;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.FiscalActivityInfoKey;
import com.code.aon.fiscal.enumeration.IFiscalModelKey;
import com.code.aon.fiscal.enumeration.Mod131Key;
import com.code.aon.fiscal.model.IFiscalDeclaration;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod131 implements IFiscalDeclaration {

	private FiscalModel fiscalModel;
	private Map<Mod131Key,FiscalModelDetail> map;
	
	public Mod131() {
		
	}

	public void initializeDetails() throws ManagerBeanException {
		Administration admin = fiscalModel.getAdministration();
		boolean added = false;
		for (Mod131Key key : Mod131Key.values()) {
			if (key.accept(admin)) {
				if (!key.isActivityKey()){
					ensureDetail(key);	
				} else {
					if (!added) {
						addActivityData();
					}
				}
				
			}
		}
	}

	private void addActivityData() throws ManagerBeanException {
		int year = fiscalModel.getYear();
		IManagerBean bean = BeanManager.getManagerBean(FiscalActivity.class);
		IManagerBean infoBean = BeanManager.getManagerBean(FiscalActivityInfo.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_ACTIVITY_YEAR), year);
		List<ITransferObject> list = bean.getList(criteria);
		int ac = 1;
		double c01 = 0.0;
		double c02 = 0.0;
		for (ITransferObject to : list) {
			FiscalActivity fa = (FiscalActivity) to;
			criteria = new Criteria();
			criteria.addEqualExpression(infoBean.getFieldName(IEntityAlias.FISCAL_ACTIVITY_INFO_FISCAL_ACTIVITY_ID), fa.getId());
			criteria.addInExpression(infoBean.getFieldName(IEntityAlias.FISCAL_ACTIVITY_INFO_INFO_KEY),  new FiscalActivityInfoKey[] {
				FiscalActivityInfoKey.I13,FiscalActivityInfoKey.I14, FiscalActivityInfoKey.I15});
			List<ITransferObject> infos = infoBean.getList(criteria);

			FiscalModelDetail detail = new FiscalModelDetail();
			detail.setFiscalModel(getHeader());
			detail.setType(Mod131Key.ACTIVITIES_PREFIX + ac + 1);
			detail.setDescription( fa.getEpigraph() + " - " + fa.getDescription());
			addDetail(detail);
			
			
			for (ITransferObject infoTo : infos) {
				FiscalActivityInfo info = (FiscalActivityInfo) infoTo;
				int c = 0;
				if ( info.getInfoKey() == FiscalActivityInfoKey.I13) { 
					c = 2;
					c01 = c01 + info.getDoubleValue(); 
				} else if ( info.getInfoKey() == FiscalActivityInfoKey.I14) {
					c = 3;
				} else if ( info.getInfoKey() == FiscalActivityInfoKey.I15) {
					c02 = c02 + info.getDoubleValue(); 
					c = 4;
				}
				detail = new FiscalModelDetail();
				detail.setFiscalModel(getHeader());
				detail.setType(Mod131Key.ACTIVITIES_PREFIX + ac + c);
				detail.setAccumulatedAmount( info.getDoubleValue() );
				addDetail(detail);
			}
			ac++;
		}
		
		FiscalModelDetail detail = new FiscalModelDetail();
		detail.setFiscalModel(getHeader());
		detail.setType(Mod131Key.AC01.getValue());
		detail.setAccumulatedAmount( c01  );
		addDetail(detail);

		detail = new FiscalModelDetail();
		detail.setFiscalModel(getHeader());
		detail.setType(Mod131Key.AC02.getValue());
		detail.setAccumulatedAmount( c02  );
		addDetail(detail);

	}

	public Map<Mod131Key, FiscalModelDetail> getMap() {
		if (map == null) {
			map = new TreeMap<Mod131Key, FiscalModelDetail>();	
		}
		return map;
	}

	@Override
	public FiscalModel getHeader() {
		return fiscalModel;
	}
	public void setFiscalModel(FiscalModel fiscalModel) {
		this.fiscalModel = fiscalModel;
	}

	@Override
	public List<Mod131Key> getKeys() {
		return new LinkedList<Mod131Key>( getMap().keySet() );
	}
	
	
	public List<Mod131Key> getActivityKeys() {
		List<Mod131Key> activityKeys = new LinkedList<Mod131Key>();
		Iterator<Mod131Key> iter = getMap().keySet().iterator(); 
		while (iter.hasNext()) {
			Mod131Key key = iter.next();
			if ( key.isActivityKey() ) {
				activityKeys.add(key);		
			}
		}
		return activityKeys;
	}
	public List<Mod131Key> getNotActivityKeys() {
		List<Mod131Key> notActivityKeys = new LinkedList<Mod131Key>();
		Iterator<Mod131Key> iter = getMap().keySet().iterator(); 
		while (iter.hasNext()) {
			Mod131Key key = iter.next();
			if ( !key.isActivityKey() ) {
				notActivityKeys.add(key);		
			}
		}
		return notActivityKeys;
	}
	
	
	@Override
	public Collection<FiscalModelDetail> getDetails() {
		return getMap().values();
	}
	
	@Override
	public FiscalModelDetail getDetail(IFiscalModelKey key) {
		return getMap().get(key);
	}
	
	@Override
	public FiscalModelDetail ensureDetail(IFiscalModelKey key) {
		FiscalModelDetail detail = getDetail(key);
		if (detail == null) {
			detail = new FiscalModelDetail();
			detail.setFiscalModel(getHeader());
			detail.setType(key.getValue());
			addDetail(detail);
		}
		return detail;
	}

	@Override
	public void calculate() throws AonException {
		Mod131CalculatorFactory factory = new Mod131CalculatorFactory();
		int year = getHeader().getYear();
		Administration admin = getHeader().getAdministration(); 
		IMod131Calculator calculator = factory.getCalculator( year , admin );
		calculator.calculate(this);
	}
	
	@Override
	public void addDetail(FiscalModelDetail detail) {
		Mod131Key key = Mod131Key.getKeyWithValue( detail.getType() );
		getMap().put(key, detail);
	}
	
	@Override
	public IFiscalModelKey getKey(String value) {
		return Mod131Key.getKeyWithValue(value);
	}
	
	public Mod131Key getH1() {
		return Mod131Key.ACH1; 
	}
	
	
}
