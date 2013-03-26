package com.code.aon.fiscal.mod310;

import java.util.Date;
import java.util.List;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.VatTaxDetail;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.Mod310Key;
import com.code.aon.fiscal.enumeration.VatTaxKey;
import com.code.aon.fiscal.model.FiscalModelManager;
import com.code.aon.fiscal.model.IFiscalDeclaration;
import com.code.aon.fiscal.vat.tax.VatTaxManager;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod310Manager extends FiscalModelManager {
	
	@Override
	public boolean accept(FiscalModelType type) {
		return type == FiscalModelType.M310;
	}

	@Override
	public Mod310 initializeFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod310 mod310 = new Mod310();
		mod310.setFiscalModel(fiscalModel);
		return mod310;
	}
	
	@Override
	public Mod310 initializeFiscalModelDetails(IFiscalDeclaration declaration) throws AonException {
		Mod310 mod310 = (Mod310) declaration;
		FiscalModel fiscalModel = mod310.getHeader();
		mod310.initializeDetails();
		Date fromDate = fiscalModel.getPeriod().getStartDate(  fiscalModel.getYear() );
		Date toDate = fiscalModel.getPeriod().getDueDate(  fiscalModel.getYear() );
		VatTaxManager taxManager = new VatTaxManager();
		List<VatTaxDetail> vatDetails = taxManager.getVatTax(fromDate, toDate );
		double c02 = 0.0;
		double c04 = 0.0;
		double c06 = 0.0;
		for (VatTaxDetail vatDetail : vatDetails) {
			// Adquisiciones intracomunitarias de bienes corrientes
			if (vatDetail.getKey() == VatTaxKey.A3 ) {
				c02 = c02 + vatDetail.getQuotaAccumulated(); 
			}
			// Inversión de sujeto pasivo
			if (vatDetail.getKey() == VatTaxKey.A4 ) {
				c04 = c04 + vatDetail.getQuotaAccumulated(); 
			}
			// Adquisiciones o importacion de activos fijos
			if (vatDetail.getKey() == VatTaxKey.D2
			 || vatDetail.getKey() == VatTaxKey.C2) {
				c06 = c06 + vatDetail.getQuotaAccumulated(); 
			}
			
		}
		mod310.ensureDetail( Mod310Key.C02).addAccumulatedAmount(CommonUtil.round(c02));
		mod310.ensureDetail( Mod310Key.C04).addAccumulatedAmount(CommonUtil.round(c04));
		mod310.ensureDetail( Mod310Key.C06).addAccumulatedAmount(CommonUtil.round(c06));
		vatDetails = null;
		return mod310;
	}

	@Override
	public IFiscalDeclaration loadFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod310 mod310 = new Mod310();
		mod310.setFiscalModel(fiscalModel);
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName( IEntityAlias.FISCAL_MODEL_DETAIL_FISCAL_MODEL_ID );
		criteria.addEqualExpression(alias, fiscalModel.getId());
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			FiscalModelDetail detail = (FiscalModelDetail) to;
			mod310.addDetail(detail);
		}
		return mod310;
	}
}
