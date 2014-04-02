package com.code.aon.fiscal.mod303;

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
import com.code.aon.fiscal.enumeration.Mod303Key;
import com.code.aon.fiscal.enumeration.VatTaxKey;
import com.code.aon.fiscal.model.FiscalModelManager;
import com.code.aon.fiscal.model.IFiscalDeclaration;
import com.code.aon.fiscal.vat.tax.VatTaxManager;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod303Manager extends FiscalModelManager {
	
	private String domainName;
	
	public Mod303Manager(String domainName) {
		this.domainName = domainName;
	}
	
	public String getDomainName() {
		return domainName;
	}

	@Override
	public boolean accept(FiscalModelType type) {
		return type == FiscalModelType.M303;
	}

	@Override
	public Mod303 initializeFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod303 mod303 = new Mod303(domainName);
		mod303.setFiscalModel(fiscalModel);
		return mod303;
	}
	
	@Override
	public Mod303 initializeFiscalModelDetails(IFiscalDeclaration declaration) throws AonException {
		Mod303 mod303 = (Mod303) declaration;
		FiscalModel fiscalModel = mod303.getHeader();
		mod303.initializeDetails();
		Date fromDate = fiscalModel.getPeriod().getStartDate(  fiscalModel.getYear() );
		Date toDate = fiscalModel.getPeriod().getDueDate(  fiscalModel.getYear() );
		VatTaxManager taxManager = new VatTaxManager(getDomainName());
		List<VatTaxDetail> vatDetails = taxManager.getVatTax(fiscalModel.getDomain(),fromDate, toDate );
		double c51 = 0.0;
		double c53 = 0.0;
		double c55 = 0.0;
		for (VatTaxDetail vatDetail : vatDetails) {
			// Adquisiciones intracomunitarias de bienes corrientes
			if (vatDetail.getKey() == VatTaxKey.A3 ) {
				c51 = c51 + vatDetail.getQuotaAccumulated(); 
			}
			// Inversión de sujeto pasivo
			if (vatDetail.getKey() == VatTaxKey.A4 ) {
				c53 = c53 + vatDetail.getQuotaAccumulated(); 
			}
			// Adquisiciones o importacion de activos fijos
			if (vatDetail.getKey() == VatTaxKey.D2
			 || vatDetail.getKey() == VatTaxKey.C2) {
				c55 = c55 + vatDetail.getQuotaAccumulated(); 
			}
			
		}
		mod303.ensureDetail( Mod303Key.C65).addAccumulatedAmount(100.0);
		mod303.ensureDetail( Mod303Key.C51).addAccumulatedAmount(CommonUtil.round(c51));
		mod303.ensureDetail( Mod303Key.C53).addAccumulatedAmount(CommonUtil.round(c53));
		mod303.ensureDetail( Mod303Key.C55).addAccumulatedAmount(CommonUtil.round(c55));
		vatDetails = null;
		return mod303;
	}

	@Override
	public IFiscalDeclaration loadFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod303 mod303 = new Mod303(domainName);
		mod303.setFiscalModel(fiscalModel);
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName( IEntityAlias.FISCAL_MODEL_DETAIL_FISCAL_MODEL_ID );
		criteria.addEqualExpression(alias, fiscalModel.getId());
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			FiscalModelDetail detail = (FiscalModelDetail) to;
			if (detail.getKey() == Mod303Key.CAG1_V2 || detail.getKey() == Mod303Key.CAG2_V2) {
				detail.setAccumulatedAmount( CommonUtil.round(detail.getAccumulatedAmount() / 10000,5));
				detail.setAmount( CommonUtil.round(detail.getAmount() / 10000,5));
			}
			mod303.addDetail(detail);
		}
		return mod303;
	}
}
