package com.code.aon.fiscal;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.IFiscalModelKey;
import com.code.aon.fiscal.enumeration.Mod111Key;
import com.code.aon.fiscal.enumeration.Mod115Key;
import com.code.aon.fiscal.enumeration.Mod123Key;
import com.code.aon.fiscal.enumeration.Mod130Key;
import com.code.aon.fiscal.enumeration.Mod131Key;
import com.code.aon.fiscal.enumeration.Mod303Key;
import com.code.aon.fiscal.enumeration.Mod310Key;
import com.code.aon.fiscal.enumeration.Mod311Key;
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
			} else if (getFiscalModel().getModel() == FiscalModelType.M311) {
				Mod311Key key = (Mod311Key) getKey(); 
				return (key != null && (key.getValue().startsWith(Mod311Key.ACTIVITIES_PREFIX)
						|| key.getValue().startsWith(Mod311Key.FARMING_ACTIVITIES_PREFIX)));
			}
		}
		return false;
	}
	@Transient
	public IFiscalModelKey getKey() {
		if (getFiscalModel() != null && getFiscalModel().getModel() != null ) {
			if (getFiscalModel().getModel() == FiscalModelType.M111) {
				return Mod111Key.getKeyWithValue( getType() );	
			} else if (getFiscalModel().getModel() == FiscalModelType.M115) {
				return Mod115Key.getKeyWithValue( getType() );	
			} else if (getFiscalModel().getModel() == FiscalModelType.M115) {
				return Mod115Key.getKeyWithValue( getType() );	
			} else if (getFiscalModel().getModel() == FiscalModelType.M123) {
				return Mod123Key.getKeyWithValue( getType() );	
			} else if (getFiscalModel().getModel() == FiscalModelType.M130) {
				return Mod130Key.getKeyWithValue( getType() );	
			} else if (getFiscalModel().getModel() == FiscalModelType.M131) {
				return Mod131Key.getKeyWithValue( getType() );	
			} else if (getFiscalModel().getModel() == FiscalModelType.M303) {
				return Mod303Key.getKeyWithValue( getType() );	
			} else if (getFiscalModel().getModel() == FiscalModelType.M303_AI) {
				return Mod303Key.getKeyWithValue( getType() );	
			} else if (getFiscalModel().getModel() == FiscalModelType.M310) {
				return Mod310Key.getKeyWithValue( getType() );	
			} else if (getFiscalModel().getModel() == FiscalModelType.M311) {
				return Mod311Key.getKeyWithValue( getType() );	
			}
		}
		return null;
	}
}
