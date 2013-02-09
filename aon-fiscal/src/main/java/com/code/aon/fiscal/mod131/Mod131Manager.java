package com.code.aon.fiscal.mod131;

import java.util.List;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.model.FiscalModelManager;
import com.code.aon.fiscal.model.IFiscalDeclaration;
import com.code.aon.fiscal.model.IFiscalModelManager;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod131Manager extends FiscalModelManager implements IFiscalModelManager{
	
	@Override
	public boolean accept(FiscalModelType type) {
		return type == FiscalModelType.M131;
	}

	@Override
	public Mod131 initializeFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod131 mod131 = new Mod131();
		mod131.setFiscalModel(fiscalModel);
//		mod131.initializeDetails();
		return mod131;
	}
	
	@Override
	public Mod131 initializeFiscalModelDetails(IFiscalDeclaration declaration) throws AonException {
		Mod131 mod130 = (Mod131) declaration;
		mod130.initializeDetails();
		mod130.calculate();
		return mod130;
	}

	@Override
	public IFiscalDeclaration loadFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod131 mod130 = new Mod131();
		mod130.setFiscalModel(fiscalModel);
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName( IEntityAlias.FISCAL_MODEL_DETAIL_FISCAL_MODEL_ID );
		criteria.addEqualExpression(alias, fiscalModel.getId());
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			FiscalModelDetail detail = (FiscalModelDetail) to;
			mod130.addDetail(detail);
		}
		return mod130;
	}
	
}
