package com.code.aon.ui.company.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.enumeration.InvestAssetRegime;
import com.code.aon.company.enumeration.InvestAssetType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.EnterpriseActivity;

public class InvestAssetSearchListener extends ControllerSearchListenerEx {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private InvestAssetType type;
	private InvestAssetRegime regime;
	private EnterpriseActivity activity;
	private boolean withdrawnAsset;

	public InvestAssetType getType() {
		return type;
	}
	public void setType(InvestAssetType type) {
		this.type = type;
	}
	
	public InvestAssetRegime getRegime() {
		return regime;
	}
	public void setRegime(InvestAssetRegime regime) {
		this.regime = regime;
	}
	
	public EnterpriseActivity getActivity() {
		return activity;
	}
	public void setActivity(EnterpriseActivity activity) {
		this.activity = activity;
	}
	
	public boolean isWithdrawnAsset() {
		return withdrawnAsset;
	}

	public void setWithdrawnAsset(boolean withdrawnAsset) {
		this.withdrawnAsset = withdrawnAsset;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setType(null);
		setRegime(null);
		setActivity((EnterpriseActivity)BeanManager.getManagerBean(EnterpriseActivity.class).createNewTo());
		setWithdrawnAsset(false);
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		super.completeCriteria(criteria);
		if (getType() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVEST_ASSET_TYPE), getType());
		}
		if (getRegime() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVEST_ASSET_REGIME), getRegime());
		}
		if ((getActivity() != null && getActivity().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.INVEST_ASSET_ACTIVITY_ID), getActivity().getId());
		}
		if (!isWithdrawnAsset()) {
			criteria.addNullExpression(getFieldName(IEntityAlias.INVEST_ASSET_END_DATE));
		}
	}

}