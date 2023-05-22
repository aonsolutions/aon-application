package com.code.aon.fiscal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.IFiscalModelKey;
import com.code.aon.fiscal.enumeration.Mod303Key;
import com.esferalia.aon.entity.master.FiscalModelDetailDB;

@Entity
@Table(name="fs_model_detail")
public class FiscalModelDetail extends FiscalModelDetailDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void addAccumulatedAmount(double amount) {
		setAccumulatedAmount( CommonUtil.round(getAccumulatedAmount()) + amount);
	}
	public void addDeclaredAmount(double amount) {
		setDeclaredAmount( CommonUtil.round(getDeclaredAmount()) + amount);
	}
	public void addResultAmount(double amount) {
		setResultAmount( CommonUtil.round(getResultAmount()) + amount);
	}
	public void addAdjustAmount(double amount) {
		setAdjustAmount( CommonUtil.round(getAdjustAmount()) + amount);
	}
	public void addAmount(double amount) {
		setAmount( CommonUtil.round(getAmount()) + amount);
	}
	@Transient
	public boolean isActivity(){ 
		if (getFiscalModel() != null && getFiscalModel().getModel() != null ) {
			if (getFiscalModel().getModel() == FiscalModelType.M303) {
				Mod303Key key = (Mod303Key) getKey(); 
				return (key != null && ( key.getValue().startsWith(Mod303Key.ACTIVITIES_PREFIX)
						|| key.getValue().startsWith(Mod303Key.FARMING_ACTIVITIES_PREFIX)));
			}
		}
		return false;
	}
	@Transient
	public IFiscalModelKey getKey() {
		if (getFiscalModel() != null && getFiscalModel().getModel() != null ) {
			if (getFiscalModel().getModel() == FiscalModelType.M303) {
				return Mod303Key.getKeyWithValue( getType() );	
			} else if (getFiscalModel().getModel() == FiscalModelType.M303_AI) {
				return Mod303Key.getKeyWithValue( getType() );	
			}
		}
		return null;
	}
}
