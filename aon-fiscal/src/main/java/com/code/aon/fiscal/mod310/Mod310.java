package com.code.aon.fiscal.mod310;

import java.util.Collection;
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
import com.code.aon.finance.Finance;
import com.code.aon.fiscal.FiscalActivity;
import com.code.aon.fiscal.FiscalActivityInfo;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.FiscalActivityInfoKey;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.IFiscalModelKey;
import com.code.aon.fiscal.enumeration.Mod310Key;
import com.code.aon.fiscal.model.IFiscalDeclaration;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod310 implements IFiscalDeclaration {

	private FiscalModel fiscalModel;
	private Map<Mod310Key,FiscalModelDetail> map;
	
	public Mod310() {
		
	}
	@Override
	public FiscalModelType getType() {
		return FiscalModelType.M310;
	}

	public void initializeDetails() throws ManagerBeanException {
		clearMap();
		Administration admin = fiscalModel.getAdministration();
		boolean cacAdded = false;
		for (Mod310Key key : Mod310Key.values()) {
			if (key.accept(admin)) {
				if (key == Mod310Key.CAC1 
				 || key == Mod310Key.CAC2
				 || key == Mod310Key.CAC3
				 || key == Mod310Key.CAC4
				 || key == Mod310Key.CAC5
				 || key == Mod310Key.CAG1
				 || key == Mod310Key.CAG2
				 || key == Mod310Key.CAG3
				 || key == Mod310Key.CAG4) {
					if (!cacAdded) {
						addFiscalActivities();
						cacAdded = true;
					}
				} else {
					ensureDetail(key);	
				}
				
			}
		}
	}

	private void addFiscalActivities() throws ManagerBeanException {
		int year = fiscalModel.getYear();
		IManagerBean bean = BeanManager.getManagerBean(FiscalActivity.class);
		IManagerBean infoBean = BeanManager.getManagerBean(FiscalActivityInfo.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_ACTIVITY_YEAR), year);
		List<ITransferObject> list = bean.getList(criteria);
		int ac = 1;
		int ag = 1;
		for (ITransferObject to : list) {
			FiscalActivity fa = (FiscalActivity) to;
			if ( fa.isFarmer() ) {
				FiscalModelDetail detail = new FiscalModelDetail();
				detail.setFiscalModel(getHeader());
				detail.setType(Mod310Key.FARMING_ACTIVITIES_PREFIX + ag);
				detail.setDescription( fa.getEpigraph() + " - " + fa.getDescription() );
				addDetail(detail);
				// TODO Añadir importes. Tener en cuente "sin actividad"
				ag++;
			} else {
				criteria = new Criteria();
				criteria.addEqualExpression(infoBean.getFieldName(IEntityAlias.FISCAL_ACTIVITY_INFO_FISCAL_ACTIVITY_ID), fa.getId());
				criteria.addEqualExpression(infoBean.getFieldName(IEntityAlias.FISCAL_ACTIVITY_INFO_INFO_KEY), FiscalActivityInfoKey.V06);
				List<ITransferObject> infos = infoBean.getList(criteria);
				for (ITransferObject infoTo : infos) {
					FiscalActivityInfo info = (FiscalActivityInfo) infoTo;
					FiscalModelDetail detail = new FiscalModelDetail();
					detail.setFiscalModel(getHeader());
					detail.setType(Mod310Key.ACTIVITIES_PREFIX + ac);
					if (!fiscalModel.isWithoutActivity()) {
						detail.setAccumulatedAmount( info.getDoubleValue() );	
					}
					detail.setDescription( fa.getEpigraph() + " - " + fa.getDescription() );
					addDetail(detail);
					ac++;
				}
			}
		}
		
	}

    @Override
    public void clearMap() {
    	map = null;
    }

    public Map<Mod310Key, FiscalModelDetail> getMap() {
		if (map == null) {
			map = new TreeMap<Mod310Key, FiscalModelDetail>();	
		}
		return map;
	}

	@Override
	public FiscalModel getHeader() {
		return fiscalModel;
	}
	@Override
	public void setHeader(FiscalModel fiscalModel) {
		this.fiscalModel = fiscalModel;
	}
	public void setFiscalModel(FiscalModel fiscalModel) {
		this.fiscalModel = fiscalModel;
	}

	@Override
	public List<Mod310Key> getKeys() {
		return new LinkedList<Mod310Key>( getMap().keySet() );
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
		Mod310CalculatorFactory factory = new Mod310CalculatorFactory();
		int year = getHeader().getYear();
		Administration admin = getHeader().getAdministration(); 
		IMod310Calculator calculator = factory.getCalculator( year , admin );
		calculator.calculate(this);
	}
	
	@Override
	public void addDetail(FiscalModelDetail detail) {
		Mod310Key key = Mod310Key.getKeyWithValue( detail.getType() );
		getMap().put(key, detail);
	}
	
	@Override
	public IFiscalModelKey getKey(String value) {
		return Mod310Key.getKeyWithValue(value);
	}
	
	@Override
	public Finance getFinance() {
		return fiscalModel!=null?fiscalModel.getFinance():null;
	}
	
	@Override
	public double getResult() {
		Mod310CalculatorFactory factory = new Mod310CalculatorFactory();
		int year = getHeader().getYear();
		Administration admin = getHeader().getAdministration(); 
		IMod310Calculator calculator = factory.getCalculator( year , admin );
		return calculator.getResult(this);
	}
	
	@Override
	public boolean isDeclarationNegativeAvailable() {
		return false;
	}

	@Override
	public boolean isToDeductDeclarationAvailable() {
		return false;
	}

	@Override
	public boolean isWithoutActivityDeclarationAvailable() {
		return true;
	}

	@Override
	public boolean isNegative() {
		return false;
	}

	@Override
	public boolean isToDeduct() {
		return false;
	}
	
	@Override
	public boolean isCompensateDeclarationAvailable() {
		return true;
	}
	@Override
	public boolean isCompensate() {
		return (getResult() < 0);
	}
	
}
