package com.code.aon.ui.fiscal.file;


import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.config.BankAccount;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD303.Breakdown;
import com.code.aon.file.tax.model.MOD303.Declaration;
import com.code.aon.file.tax.model.MOD303.GeneralRegime;
import com.code.aon.file.tax.model.MOD303.MOD303;
import com.code.aon.file.tax.model.MOD303.MOD303Format;
import com.code.aon.file.tax.model.MOD303.SimplifiedRegime;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.VatTaxDeclaration;
import com.code.aon.fiscal.VatTaxDetail;
import com.code.aon.fiscal.enumeration.Mod303Key;
import com.code.aon.fiscal.enumeration.VatTaxKey;
import com.code.aon.fiscal.mod303.IMod303Declaration;
import com.code.aon.fiscal.mod303.Mod303;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.watson.util.AonStringUtils;

public class MOD303Writer {
	
	private Company getCompany(int domain) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Company.class);
		Criteria c  = new Criteria();
		c.addEqualExpression("Company.domain", domain);
		c.setSkipDomainFilter(true);
		List<ITransferObject> list = bean.getList(c);
		if (list != null && list.size() > 0 ){
			return (Company) list.get(0);
		}
		throw new ManagerBeanException("No pudo encontrar 'Company' para el dominio " + domain);
	}

	public FileOutput createMOD303(List<IMod303Declaration> mod303s,MOD303Format format, Mod303 additionalInfo) throws ManagerBeanException {
		List<Declaration> declarations = new LinkedList<Declaration>();
		if (mod303s != null) {
			for (IMod303Declaration generalRegimeDeclaration : mod303s) {
				Declaration declaration = getDeclaration(generalRegimeDeclaration, additionalInfo);
			// EL VALOR DEL RESULTADO (CLAVE 69) ERA SIEMPRE 0 Y AL GENERAR EL FICHERA DABA ERROR EN AEAT
				if(declaration.getDeposit() > 0){
					declaration.setResult0(declaration.getDeposit());
				} else if(declaration.getCompensate() > 0){
					declaration.setResult0(-declaration.getCompensate());
				} else if(declaration.getPayBack() > 0){
					declaration.setResult0(-declaration.getPayBack());
				}
			// ----------------------------------------------------------------------------------------- //
				declarations.add(declaration);
			}
		}
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		OutputStreamWriter wr = null;
		try {
			wr = new OutputStreamWriter(output,"ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			wr = new OutputStreamWriter(output);
		}
		PrintWriter writer = new PrintWriter(wr);
		MOD303 mod303 = new MOD303();
		FileOutput fileOutput = new FileOutput();
		fileOutput.setErrors(mod303.create(declarations, format, writer));
		fileOutput.setContent(output.toByteArray());
		return fileOutput;
	}

	private Declaration getDeclaration(IMod303Declaration mod303Declaration,Mod303 additionalInfo) throws ManagerBeanException {
			SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
			Declaration declaration = new  Declaration();
			int year = mod303Declaration.getYear();
			declaration.setYear(year);
			Administration admon = mod303Declaration.getAdministration();
			declaration.setPeriod(mod303Declaration.getPeriod().getName(admon));
			if (mod303Declaration.getBankAccountContainer() != null && mod303Declaration.getBankAccountContainer().getBankAccount() != null) {
				BankAccount ba = mod303Declaration.getBankAccountContainer().getBankAccount();
				declaration.setCcc(ba.getBban());
				declaration.setBankAccount(ba.getIban());
				declaration.setBankName(mod303Declaration.getBankAccountContainer().getBankAlias());
			}
			Company company = getCompany(mod303Declaration.getDomain());
			declaration.setPerson(company.getRegistry().getType() == RegistryType.NATURAL 
					|| company.getDocumentType() != DocumentType.CIF);
			declaration.setDocument(company.getDocument());
			declaration.setStartPeriod(0);
			declaration.setEndPeriod(0);
			String startDate = formatter.format(mod303Declaration.getPeriod().getStartDate(year));
			declaration.setStartPeriod(Integer.parseInt( startDate));
			String endDate = formatter.format(mod303Declaration.getPeriod().getDueDate(year));
			declaration.setEndPeriod(Integer.parseInt( endDate));
			String name = company.getName();
			declaration.setSurname(name);	
			declaration.setName(null);
			if (mod303Declaration.getAdministration() == Administration.COMMON_TERRITORY) {
				if (declaration.isPerson()) {
					if (StringUtils.contains(name, ',')) {
						declaration.setName(StringUtils.trim(StringUtils.substringAfter(name, ",")));
						declaration.setSurname(StringUtils.trim(StringUtils.substringBefore(name, ",")));
					} else {
						declaration.setName(StringUtils.trim(StringUtils.substringBefore(name, " ")));
						declaration.setSurname(StringUtils.trim(StringUtils.substringAfter(name, " ")));
					}
				} 
			}
			RegistryMedia rm =company.getPhone(); 
			declaration.setTelephone((rm!=null && StringUtils.isNotBlank(rm.getValue()))?rm.getValue():"0");
			rm =company.getFax();
			declaration.setFax((rm != null && StringUtils.isNotBlank(rm.getValue()))?rm.getValue():"0");
			rm =company.getEmail();
			declaration.setEmail((rm!=null)?rm.getValue():"");
			RegistryAddress address = company.getDefaultAddress();
			declaration.setAddress( address.getAddress() );
			declaration.setAddressNumber(0);
			if (StringUtils.isNotEmpty(address.getNumber())){
				try {
					declaration.setAddressNumber(Integer.parseInt(address.getNumber()));
				} catch (NumberFormatException e) {
					// Nothing
				}
			}
			declaration.setEntity(address.getCity());
			declaration.setCity(address.getCity());
			declaration.setProvinceID(address.getGeozone()==null?"":address.getGeozone().getCode());
			declaration.setProvince(address.getGeozone()==null?"":address.getGeozone().getName());
			String zip = address.getZip();
			declaration.setZip(0);
			if (zip != null){
				try {
					declaration.setZip(Integer.parseInt( zip ));
				} catch (NumberFormatException e) {
					// Nothing
				}
			}
			
			Date today = new Date();
			declaration.setTodayDay( CommonUtil.getDay(today) );
			Month month = Month.getMonthByValue( CommonUtil.getMonth(today) ); 
			declaration.setTodayMonth( month.getName(AonUtil.getCurrentLocale()) );
			declaration.setTodayYear( CommonUtil.getYear(today) );
			declaration.setReplacement( mod303Declaration.isReplacement() );
			declaration.setComplementary( mod303Declaration.isComplementary() );
			declaration.setReplacedNumber(null);
			if (mod303Declaration.isReplacement() || mod303Declaration.isComplementary()) {
				declaration.setReplacedNumber( mod303Declaration.getReplacedNumber() );	
			}
			declaration.setTaxRefundRegistry( mod303Declaration.isTaxRefundRegistry());
			
			double prorata = mod303Declaration.getProrata();
			if (prorata != 100.0) {
				declaration.setProrata(prorata);
				declaration.setGeneralProrataApplied(true);
			}
			declaration.setSpecialProrataApplied(false);
			
			if (mod303Declaration.isGeneralRegime()) {
				declaration.setSimplRegimeOnly(false);
				VatTaxDeclaration vtd = (VatTaxDeclaration) mod303Declaration;
				populateDeclarationDetail(vtd,declaration,additionalInfo);
				populateDeclaration(vtd,declaration);
			} else {
				declaration.setSimplRegimeOnly(true);
				Mod303 mod303 =  (Mod303) mod303Declaration;
				populateDeclarationDetail(mod303,declaration);
				populateDeclaration(mod303,declaration);
			} 
			
			declaration.setVatAccrualRegimeReceiver( declaration.getVatAccrualInputBase() != 0 || declaration.getVatAccrualInputQuota() != 0 );
			declaration.setVatAccrualRegime( company.isVatAccrualPayment() );
			declaration.setConcursoAuto(" ");
			declaration.changeInvalidCharacters();
			return declaration;
	}
	
	private void populateDeclarationDetail(VatTaxDeclaration vatTaxDeclaration, Declaration declaration, Mod303 additionalInfo) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(VatTaxDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_VAT_TAX_ID), vatTaxDeclaration.getVatTax().getId());
		criteria.addOrder(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_KEY));
		criteria.setSkipDomainFilter(true);
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to: list) {
			VatTaxDetail detail = (VatTaxDetail) to;
			fillDeclaration(detail,declaration);
		}
		
		if (additionalInfo != null) {
			fillAdditionalInfo(declaration, additionalInfo);
		}
	
	}
	
	private void fillAdditionalInfo(Declaration declaration, Mod303 mod303) {
		if ( mod303.getEnsuredAmount(Mod303Key.D) == 0) {
			declaration.setMod347(" ");	
		} else {
			declaration.setMod347("X");
		}
		String k1 = mod303.ensureDetail(Mod303Key.IAC_01).getDescription();
		declaration.setIae1Key(StringUtils.isBlank(k1)?0:Integer.parseInt(k1) );
		String k2 = mod303.ensureDetail(Mod303Key.IAC_02).getDescription();
		declaration.setIae2Key(StringUtils.isBlank(k2)?0:Integer.parseInt(k2) );
		String k3 = mod303.ensureDetail(Mod303Key.IAC_03).getDescription();
		declaration.setIae3Key(StringUtils.isBlank(k3)?0:Integer.parseInt(k3) );
		String k4 = mod303.ensureDetail(Mod303Key.IAC_04).getDescription();
		declaration.setIae4Key(StringUtils.isBlank(k4)?0:Integer.parseInt(k4) );
		String k5 = mod303.ensureDetail(Mod303Key.IAC_05).getDescription();
		declaration.setIae5Key(StringUtils.isBlank(k5)?0:Integer.parseInt(k5) );
		String k6 = mod303.ensureDetail(Mod303Key.IAC_06).getDescription();
		declaration.setIae6Key(StringUtils.isBlank(k6)?0:Integer.parseInt(k6) );
		
		String epi1 = mod303.ensureDetail(Mod303Key.IAE_01).getDescription();
		String epi2 = mod303.ensureDetail(Mod303Key.IAE_02).getDescription();
		String epi3 = mod303.ensureDetail(Mod303Key.IAE_03).getDescription();
		String epi4 = mod303.ensureDetail(Mod303Key.IAE_04).getDescription();
		String epi5 = mod303.ensureDetail(Mod303Key.IAE_05).getDescription();
		String epi6 = mod303.ensureDetail(Mod303Key.IAE_06).getDescription();
		declaration.setIae1Epigraph(epi1);
		declaration.setIae2Epigraph(epi2);
		declaration.setIae3Epigraph(epi3);
		declaration.setIae4Epigraph(epi4);
		declaration.setIae5Epigraph(epi5);
		declaration.setIae6Epigraph(epi6);
		
		if (AonStringUtils.isNotBlank(epi1)
		 || AonStringUtils.isNotBlank(epi2)
		 || AonStringUtils.isNotBlank(epi3)
		 || AonStringUtils.isNotBlank(epi4)
		 || AonStringUtils.isNotBlank(epi5)
		 || AonStringUtils.isNotBlank(epi6)) {
			declaration.setC80(mod303.getEnsuredAmount(Mod303Key.C80));
			declaration.setC81(mod303.getEnsuredAmount(Mod303Key.C81));
			declaration.setC82(mod303.getEnsuredAmount(Mod303Key.C82));
			declaration.setC83(mod303.getEnsuredAmount(Mod303Key.C83));
			declaration.setC84(mod303.getEnsuredAmount(Mod303Key.C84));
			declaration.setC85(mod303.getEnsuredAmount(Mod303Key.C85));
			declaration.setC86(mod303.getEnsuredAmount(Mod303Key.C86));
			declaration.setC87(mod303.getEnsuredAmount(Mod303Key.C87));
			declaration.setC88(mod303.getEnsuredAmount(Mod303Key.C88));
		}
	}

	private void fillDeclaration(VatTaxDetail detail, Declaration declaration) {
		GeneralRegime gr = declaration.getGeneralRegime();
		if (gr == null) {
			gr = new GeneralRegime();
			declaration.setGeneralRegime(gr);
		}
		VatTaxKey key = detail.getKey();
		double percent = detail.getPercent();
		String mapKey = new Double(percent).toString(); 
		double taxableBase = detail.getTaxableBase();
		double quota = detail.getQuota();
		double deductiblequota = detail.getDeductibleQuota()!=0.0?detail.getDeductibleQuota():detail.getQuota();
		Breakdown bd = new Breakdown(percent,taxableBase,quota,deductiblequota);
		if (key == VatTaxKey.A1 ) {
			gr.getOutputVat().put(mapKey, bd);
			
			if (gr.getOutputVatInvPasive().containsKey((mapKey))) {
				Breakdown old = gr.getOutputVatInvPasive().get(mapKey);
				bd.setTaxableBase( CommonUtil.round(bd.getTaxableBase() + old.getTaxableBase()) ); 
				bd.setQuota( CommonUtil.round(bd.getQuota() + old.getQuota()) ); 
				bd.setDeductibleQuota( CommonUtil.round(bd.getDeductibleQuota() + old.getDeductibleQuota()) ); 
			}
			gr.getOutputVatInvPasive().put(mapKey, bd);
		} else if (key == VatTaxKey.A2 ) {
			gr.getSurcharge().put(mapKey, bd);
		} else if (key == VatTaxKey.A21) {
			gr.setBaseSurchrageModifications( taxableBase );
			gr.setQuotaSurchrageModifications( quota );
		} else if (key == VatTaxKey.A3 ) {
			if (gr.getIntracommunitary().containsKey((mapKey))) {
				Breakdown old = gr.getIntracommunitary().get(mapKey);
				bd.setTaxableBase( CommonUtil.round(bd.getTaxableBase() + old.getTaxableBase()) ); 
				bd.setQuota( CommonUtil.round(bd.getQuota() + old.getQuota()) ); 
				bd.setDeductibleQuota( CommonUtil.round(bd.getDeductibleQuota() + old.getDeductibleQuota()) ); 
			}
			gr.getIntracommunitary().put(mapKey, bd);
			gr.setBaseIntracommunitary( CommonUtil.round(gr.getBaseIntracommunitary() +taxableBase,2));
			gr.setQuotaIntracommunitary( CommonUtil.round(gr.getQuotaIntracommunitary() +quota,2));
			gr.setBaseIntracommunitaryCT( CommonUtil.round(gr.getBaseIntracommunitaryCT() +taxableBase,2));
			gr.setQuotaIntracommunitaryCT( CommonUtil.round(gr.getQuotaIntracommunitaryCT() +quota,2));
		} else if (key == VatTaxKey.A31 ) {
			if (gr.getIntracommunitary().containsKey((mapKey))) {
				Breakdown old = gr.getIntracommunitary().get(mapKey);
				bd.setTaxableBase( CommonUtil.round(bd.getTaxableBase() + old.getTaxableBase()) ); 
				bd.setQuota( CommonUtil.round(bd.getQuota() + old.getQuota()) ); 
				bd.setDeductibleQuota( CommonUtil.round(bd.getDeductibleQuota() + old.getDeductibleQuota()) ); 
			}
			gr.getIntracommunitary().put(mapKey, bd);
			gr.setBaseIntracommunitary( CommonUtil.round(gr.getBaseIntracommunitary() +taxableBase,2));
			gr.setQuotaIntracommunitary( CommonUtil.round(gr.getQuotaIntracommunitary() +quota,2));
			gr.setBaseIntracommunitaryCT( CommonUtil.round(gr.getBaseIntracommunitaryCT() +taxableBase,2));
			gr.setQuotaIntracommunitaryCT( CommonUtil.round(gr.getQuotaIntracommunitaryCT() +quota,2));
		} else if (key == VatTaxKey.A4) {
			gr.setBaseISPCT( CommonUtil.round(gr.getBaseISPCT() +taxableBase,2));
			gr.setQuotaISPCT( CommonUtil.round(gr.getQuotaISPCT() +quota,2));

			gr.getInvPasive().put(mapKey, bd);
			gr.setBaseInvPasive( CommonUtil.round(gr.getBaseInvPasive() +taxableBase,2) );
			gr.setQuotaInvPasive( CommonUtil.round(gr.getQuotaInvPasive() +quota,2) );
			if (gr.getOutputVatInvPasive().containsKey((mapKey))) {
				Breakdown old = gr.getOutputVatInvPasive().get(mapKey);
				bd.setTaxableBase( CommonUtil.round(bd.getTaxableBase() + old.getTaxableBase()) ); 
				bd.setQuota( CommonUtil.round(bd.getQuota() + old.getQuota()) ); 
				bd.setDeductibleQuota( CommonUtil.round(bd.getDeductibleQuota() + old.getDeductibleQuota()) ); 
			}
			gr.getOutputVatInvPasive().put(mapKey, bd);
		} else if (key == VatTaxKey.A5) {
			gr.setBaseModifications( taxableBase );
			gr.setQuotaModifications( quota );
		} else if (key == VatTaxKey.AT) {
			gr.setOutputTotal( quota );
		} else if (key == VatTaxKey.B1) {
			gr.setInnerCommonOperationsQuota( quota );
			gr.setInnerCommonOperationsBase( taxableBase );
		} else if (key == VatTaxKey.B2) {
			gr.setInnerInvestmentOperationsQuota( quota );
			gr.setInnerInvestmentOperationsBase( taxableBase );
		} else if (key == VatTaxKey.B3) {
			gr.setInnerExpensesOperationsQuota( quota );
			gr.setInnerExpensesOperationsBase( taxableBase );
		} else if (key == VatTaxKey.C1) {
			gr.setImportedCommonOperationsQuota( quota );
			gr.setImportedCommonOperationsBase( taxableBase );
		} else if (key == VatTaxKey.C2) {
			gr.setImportedInvestmentOperationsQuota( quota );
			gr.setImportedInvestmentOperationsBase( taxableBase );
		} else if (key == VatTaxKey.D1) {
			gr.setIntracommunitaryCommonOperationsQuota( CommonUtil.round(gr.getIntracommunitaryCommonOperationsQuota() + quota ,2));
			gr.setIntracommunitaryCommonOperationsBase( CommonUtil.round(gr.getIntracommunitaryCommonOperationsBase() + taxableBase  ,2));
		} else if (key == VatTaxKey.D2) {
			gr.setIntracommunitaryInvestmentOperationsQuota( quota );
			gr.setIntracommunitaryInvestmentOperationsBase( taxableBase );
		} else if (key == VatTaxKey.D3) {
			gr.setIntracommunitaryExpensesOperationsQuota( quota );
			gr.setIntracommunitaryExpensesOperationsBase( taxableBase );
		} else if (key == VatTaxKey.ET) {
			gr.setAgriculturalRegimeCompensation( quota );
		} else if (key == VatTaxKey.RD) {
			gr.setDeductionRestificationBase( taxableBase );
			gr.setDeductionRestificationQuota( quota );
		} else if (key == VatTaxKey.RI) {
			gr.setInvestmentNormalization( quota );
		} else if (key == VatTaxKey.RP) {
			gr.setProrataNormalization( quota );
		} else if (key == VatTaxKey.FT) {
			gr.setDeductTotal( quota );
		} else if (key == VatTaxKey.DF) {
			gr.setResult( quota );
			declaration.setDifference( quota );
		} else if (key == VatTaxKey.XI) {
			declaration.setVatAccrualInputBase( taxableBase );
			declaration.setVatAccrualInputQuota( quota );
		} else if (key == VatTaxKey.XO) {
			declaration.setVatAccrualOutputBase( taxableBase );
			declaration.setVatAccrualOutputQuota( quota );
		}else if (key == VatTaxKey.EI) {
			declaration.setIntracommunitaryDeliveries( taxableBase );
		}else if (key == VatTaxKey.PS) {
			declaration.setIntracommunitaryServiceDeliveries( taxableBase );
		}else if (key == VatTaxKey.EX1) {
			double d = declaration.getExportationTotal();
			declaration.setExportationTotal( d + taxableBase );
		}else if (key == VatTaxKey.EX2) { 
			double d = declaration.getExportationTotal();
			declaration.setExportationTotal( d + taxableBase );
		}else if (key == VatTaxKey.OO) {
			double d = declaration.getNonTaxableTotal();
			declaration.setNonTaxableTotal( d + taxableBase );
		}else if (key == VatTaxKey.OS) {
			double d = declaration.getNonTaxableTotal();
			declaration.setNonTaxableTotal( d + taxableBase );
		}else if (key == VatTaxKey.OI) {
			double d = declaration.getNonTaxableTotal();
			declaration.setNonTaxableTotal( d + taxableBase );
		} else if (key == VatTaxKey.CP ) {
			if (percent != 4 && percent != 10 && percent != 21) {
				mapKey = "?";
			}
			gr.getInnerAssetPurchases().put(mapKey, bd);
			gr.setBaseInnerAssetPurchases( CommonUtil.round(gr.getBaseInnerAssetPurchases() +taxableBase,2));
			gr.setQuotaInnerAssetPurchases( CommonUtil.round(gr.getQuotaInnerAssetPurchases() +quota,2));
			gr.setDeductibleQuotaInnerAssetPurchases( CommonUtil.round(gr.getDeductibleQuotaInnerAssetPurchases() +deductiblequota,2));
			gr.setBaseTotalAddInfo( CommonUtil.round(gr.getBaseTotalAddInfo() +taxableBase,2));
			gr.setQuotaTotalAddInfo( CommonUtil.round(gr.getQuotaTotalAddInfo() +quota,2));
			gr.setDeductibleQuotaTotalAddInfo( CommonUtil.round(gr.getDeductibleQuotaTotalAddInfo() +deductiblequota,2));
		} else if (key == VatTaxKey.GT ) {
			if (percent != 4 && percent != 10 && percent != 21) {
				mapKey = "?";
			}
			gr.getExpenses().put(mapKey, bd);
			gr.setBaseExpenses( CommonUtil.round(gr.getBaseExpenses() +taxableBase,2));
			gr.setQuotaExpenses( CommonUtil.round(gr.getQuotaExpenses() +quota,2));
			gr.setDeductibleQuotaExpenses( CommonUtil.round(gr.getDeductibleQuotaExpenses() +deductiblequota,2));
			gr.setBaseTotalAddInfo( CommonUtil.round(gr.getBaseTotalAddInfo() +taxableBase,2));
			gr.setQuotaTotalAddInfo( CommonUtil.round(gr.getQuotaTotalAddInfo() +quota,2));
			gr.setDeductibleQuotaTotalAddInfo( CommonUtil.round(gr.getDeductibleQuotaTotalAddInfo() +deductiblequota,2));
		} else if (key == VatTaxKey.BI ) {
			if (percent != 4 && percent != 10 && percent != 21) {
				mapKey = "?";
			}
			gr.getInvestmentAsset().put(mapKey, bd);
			gr.setBaseInvestmentAsset( CommonUtil.round(gr.getBaseInvestmentAsset() +taxableBase,2));
			gr.setQuotaInvestmentAsset( CommonUtil.round(gr.getQuotaInvestmentAsset() +quota,2));
			gr.setDeductibleQuotaInvestmentAsset( CommonUtil.round(gr.getDeductibleQuotaInvestmentAsset() +deductiblequota,2));
			gr.setBaseTotalAddInfo( CommonUtil.round(gr.getBaseTotalAddInfo() +taxableBase,2));
			gr.setQuotaTotalAddInfo( CommonUtil.round(gr.getQuotaTotalAddInfo() +quota,2));
			gr.setDeductibleQuotaTotalAddInfo( CommonUtil.round(gr.getDeductibleQuotaTotalAddInfo() +deductiblequota,2));
		}
	}

	private void populateDeclaration(VatTaxDeclaration dec, Declaration declaration) throws ManagerBeanException {
		VatTax vatTax = dec.getVatTax(); 

		IManagerBean bean = BeanManager.getManagerBean(VatTaxDeclaration.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_ID), vatTax.getId());
		criteria.setSkipDomainFilter(true);
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to: list) {
			VatTaxDeclaration d = (VatTaxDeclaration) to;
			if (d.getAdministration() == Administration.ALAVA) {
				declaration.setAlavaPercent(d.getPercent()); 
			} else if (d.getAdministration() == Administration.GIPUZKOA) {
				declaration.setGipuzkoaPercent(d.getPercent()); 
			} else if (d.getAdministration() == Administration.BIZKAIA) {
				declaration.setBizkaiaPercent(d.getPercent()); 
			} else if (d.getAdministration() == Administration.NAVARRA) {
				declaration.setNavarraPercent(d.getPercent()); 
			} else {
				declaration.setCommonTerritoryPercent(d.getPercent());
			}
		}
		declaration.setQuota(dec.getQuota());
		declaration.setPreviousYearCompensateQuota(dec.getPreviousYearCompensateQuota());
		declaration.setResult(dec.getResult());
		declaration.setExtraCharge(dec.getExtraCharge());
		declaration.setDelayInterest(dec.getDelayInterest());
		declaration.setDeposit(dec.getDeposit());
		declaration.setCompensate(dec.getCompensate());
		declaration.setPayBack(dec.getPayBack());
		if (dec.getVatTax().isComplementary()){
			declaration.setPreviousPayBack( dec.getPreviousPayBack());
			declaration.setPreviousDeposit( dec.getPreviousDeposit() );
		}
		if (!dec.isCompensateEnabled()) {
			declaration.setTotalDebt( dec.getTotalTaxDebt() );
		}
		declaration.setWithoutActivity(dec.isWithoutActivity());
		if ( dec.getRegistryBank() != null && dec.getRegistryBank().getId() != null && dec.getRegistryBank().getBankAccount() != null) {
			BankAccount bankAccount = dec.getRegistryBank().getBankAccount();
			if (dec.getDeposit() > 0) {
				declaration.setDepositBankAccount(bankAccount.getBban());
			}
			if (dec.getPayBack() > 0) {
				declaration.setPayBackBankAccount(bankAccount.getBban());
			}
		}

	}
	
	private void populateDeclarationDetail(Mod303 mod303,Declaration declaration) {
		SimplifiedRegime sr = declaration.getSimplifiedRegime();
		if (sr == null) {
			sr = new SimplifiedRegime();
			declaration.setSimplifiedRegime(sr);
		}
		sr.setAgr1Code((int) mod303.getEnsuredAmount(Mod303Key.CAG1));
		sr.setAgr1Ingreso(mod303.getEnsuredAmount(Mod303Key.CAG1_V1));
		double agr1 = mod303.getEnsuredAmount(Mod303Key.CAG1_V2);
		sr.setAgr1Indice(agr1);
		sr.setAgr1Cuota(mod303.getEnsuredAmount(Mod303Key.CAG1_V3));
		sr.setAgr1Porce(mod303.getEnsuredAmount(Mod303Key.CAG1_V4));
		sr.setAgr1IngCta(mod303.getEnsuredAmount(Mod303Key.CAG1_V5));
		sr.setAgr1CuotaSop4T(mod303.getEnsuredAmount(Mod303Key.CAG1_V6));
		sr.setAgr1CuotaDer4T(mod303.getEnsuredAmount(Mod303Key.CAG1_V7));
		
		double epi = mod303.getEnsuredAmount(Mod303Key.CAC1);
		epi = ensureEpigraph(epi);
		sr.setIndAux1((epi == 453 || epi == 691.9 || epi == 722)?"1":" ");
		sr.setAct1epi( epi == 0?"":Integer.toString((int) epi));
		sr.setAct1Uni1(mod303.getEnsuredAmount(Mod303Key.CAC1_M1U));
		sr.setAct1Imp1(mod303.getEnsuredAmount(Mod303Key.CAC1_M1I));
		sr.setAct1Uni2(mod303.getEnsuredAmount(Mod303Key.CAC1_M2U));
		sr.setAct1Imp2(mod303.getEnsuredAmount(Mod303Key.CAC1_M2I));
		sr.setAct1Uni3(mod303.getEnsuredAmount(Mod303Key.CAC1_M3U));
		sr.setAct1Imp3(mod303.getEnsuredAmount(Mod303Key.CAC1_M3I));
		sr.setAct1Uni4(mod303.getEnsuredAmount(Mod303Key.CAC1_M4U));
		sr.setAct1Imp4(mod303.getEnsuredAmount(Mod303Key.CAC1_M4I));
		sr.setAct1Uni5(mod303.getEnsuredAmount(Mod303Key.CAC1_M5U));
		sr.setAct1Imp5(mod303.getEnsuredAmount(Mod303Key.CAC1_M5I));
		sr.setAct1Uni6(mod303.getEnsuredAmount(Mod303Key.CAC1_M6U));
		sr.setAct1Imp6(mod303.getEnsuredAmount(Mod303Key.CAC1_M6I));
		sr.setAct1Uni7(mod303.getEnsuredAmount(Mod303Key.CAC1_M7U));
		sr.setAct1Imp7(mod303.getEnsuredAmount(Mod303Key.CAC1_M7I));
		sr.setAct1Cuota(mod303.getEnsuredAmount(Mod303Key.CAC1_C));
		sr.setAct1Reduc(mod303.getEnsuredAmount(Mod303Key.CAC1_D));
		sr.setAct1IndTemp(mod303.getEnsuredAmount(Mod303Key.CAC1_Z));
		sr.setAct1Porce(mod303.getEnsuredAmount(Mod303Key.CAC1_E));
		sr.setAct1IngCta(mod303.getEnsuredAmount(Mod303Key.CAC1_F));
		double g1 = CommonUtil.round(mod303.getEnsuredAmount(Mod303Key.CAC1_G0) 
				+ mod303.getEnsuredAmount(Mod303Key.CAC1_G));
		sr.setAct1CuotaSop4T(g1);
		sr.setAct1IndTemp4T(mod303.getEnsuredAmount(Mod303Key.CAC1_H));
		sr.setAct1Resultado4T(mod303.getEnsuredAmount(Mod303Key.CAC1_I));
		sr.setAct1PorCuoMin4T(mod303.getEnsuredAmount(Mod303Key.CAC1_J));
		sr.setAct1DevCuoPai4T(mod303.getEnsuredAmount(Mod303Key.CAC1_K));
		sr.setAct1CuoMin4T(mod303.getEnsuredAmount(Mod303Key.CAC1_L));
		sr.setAct1CuoAnuDer4T(mod303.getEnsuredAmount(Mod303Key.CAC1_M));		
		
		sr.setAgr2Code( (int)  mod303.getEnsuredAmount(Mod303Key.CAG2) );
		sr.setAgr2Ingreso(mod303.getEnsuredAmount(Mod303Key.CAG2_V1));
		double agr2 = mod303.getEnsuredAmount(Mod303Key.CAG2_V2); 
		sr.setAgr2Indice(agr2);
		sr.setAgr2Indice( mod303.getEnsuredAmount(Mod303Key.CAG2_V2) );
		sr.setAgr2Cuota( mod303.getEnsuredAmount(Mod303Key.CAG2_V3) );
		sr.setAgr2Porce( mod303.getEnsuredAmount(Mod303Key.CAG2_V4) );
		sr.setAgr2IngCta( mod303.getEnsuredAmount(Mod303Key.CAG2_V5) );
		sr.setAgr2CuotaSop4T( mod303.getEnsuredAmount(Mod303Key.CAG2_V6) );
		sr.setAgr2CuotaDer4T( mod303.getEnsuredAmount(Mod303Key.CAG2_V7) );

		epi = mod303.getEnsuredAmount(Mod303Key.CAC2);
		epi = ensureEpigraph(epi);
		sr.setIndAux2((epi == 453 || epi == 691.9 || epi == 722)?"1":" ");
		sr.setAct2epi( epi == 0?"":Integer.toString((int) epi));
		sr.setAct2Uni1(mod303.getEnsuredAmount(Mod303Key.CAC2_M1U));
		sr.setAct2Imp1(mod303.getEnsuredAmount(Mod303Key.CAC2_M1I));
		sr.setAct2Uni2(mod303.getEnsuredAmount(Mod303Key.CAC2_M2U));
		sr.setAct2Imp2(mod303.getEnsuredAmount(Mod303Key.CAC2_M2I));
		sr.setAct2Uni3(mod303.getEnsuredAmount(Mod303Key.CAC2_M3U));
		sr.setAct2Imp3(mod303.getEnsuredAmount(Mod303Key.CAC2_M3I));
		sr.setAct2Uni4(mod303.getEnsuredAmount(Mod303Key.CAC2_M4U));
		sr.setAct2Imp4(mod303.getEnsuredAmount(Mod303Key.CAC2_M4I));
		sr.setAct2Uni5(mod303.getEnsuredAmount(Mod303Key.CAC2_M5U));
		sr.setAct2Imp5(mod303.getEnsuredAmount(Mod303Key.CAC2_M5I));
		sr.setAct2Uni6(mod303.getEnsuredAmount(Mod303Key.CAC2_M6U));
		sr.setAct2Imp6(mod303.getEnsuredAmount(Mod303Key.CAC2_M6I));
		sr.setAct2Uni7(mod303.getEnsuredAmount(Mod303Key.CAC2_M7U));
		sr.setAct2Imp7(mod303.getEnsuredAmount(Mod303Key.CAC2_M7I));
		sr.setAct2Cuota(mod303.getEnsuredAmount(Mod303Key.CAC2_C));
		sr.setAct2Reduc(mod303.getEnsuredAmount(Mod303Key.CAC2_D));
		sr.setAct2IndTemp(mod303.getEnsuredAmount(Mod303Key.CAC2_Z));
		sr.setAct2Porce(mod303.getEnsuredAmount(Mod303Key.CAC2_E));
		sr.setAct2IngCta(mod303.getEnsuredAmount(Mod303Key.CAC2_F));
		double g2 = CommonUtil.round(mod303.getEnsuredAmount(Mod303Key.CAC2_G0) 
				+ mod303.getEnsuredAmount(Mod303Key.CAC2_G));
		sr.setAct2CuotaSop4T(g2);
		sr.setAct2IndTemp4T(mod303.getEnsuredAmount(Mod303Key.CAC2_H));
		sr.setAct2Resultado4T(mod303.getEnsuredAmount(Mod303Key.CAC2_I));
		sr.setAct2PorCuoMin4T(mod303.getEnsuredAmount(Mod303Key.CAC2_J));
		sr.setAct2DevCuoPai4T(mod303.getEnsuredAmount(Mod303Key.CAC2_K));
		sr.setAct2CuoMin4T(mod303.getEnsuredAmount(Mod303Key.CAC2_L));
		sr.setAct2CuoAnuDer4T(mod303.getEnsuredAmount(Mod303Key.CAC2_M));
		
		sr.setSumIngCta(mod303.getEnsuredAmount(Mod303Key.C47));
		sr.setSumCuoDer4T(mod303.getEnsuredAmount(Mod303Key.C48));
		sr.setSumIngCta4T(mod303.getEnsuredAmount(Mod303Key.C49));
		sr.setResult4T(mod303.getEnsuredAmount(Mod303Key.C50));
		sr.setAdqIntrac(mod303.getEnsuredAmount(Mod303Key.C51));
		sr.setEntrActFijo(mod303.getEnsuredAmount(Mod303Key.C52));
		sr.setIvaISP(mod303.getEnsuredAmount(Mod303Key.C53));
		sr.setTotalCuota(mod303.getEnsuredAmount(Mod303Key.C54));
		sr.setAdqActFijo(mod303.getEnsuredAmount(Mod303Key.C55));
		sr.setRegularizacion(mod303.getEnsuredAmount(Mod303Key.C56));
		sr.setTotalDeducible(mod303.getEnsuredAmount(Mod303Key.C57));
		sr.setResult(mod303.getEnsuredAmount(Mod303Key.C58));
		
		declaration.setIntracommunitaryDeliveries(mod303.getEnsuredAmount(Mod303Key.C59));
		declaration.setExportationTotal(mod303.getEnsuredAmount(Mod303Key.C60));
		declaration.setNonTaxableTotal(mod303.getEnsuredAmount(Mod303Key.C61));
		declaration.setVatAccrualOutputBase(mod303.getEnsuredAmount(Mod303Key.C62));
		declaration.setVatAccrualOutputQuota(mod303.getEnsuredAmount(Mod303Key.C63));
		declaration.setVatAccrualInputBase(mod303.getEnsuredAmount(Mod303Key.C74));
		declaration.setVatAccrualInputQuota(mod303.getEnsuredAmount(Mod303Key.C75));
		
		if ( mod303.getEnsuredAmount(Mod303Key.D) == 0) {
			declaration.setMod347(" ");	
		} else {
			declaration.setMod347("X");
		}
		String k1 = mod303.ensureDetail(Mod303Key.IAC_01).getDescription();
		declaration.setIae1Key(StringUtils.isBlank(k1)?0:Integer.parseInt(k1) );
		String k2 = mod303.ensureDetail(Mod303Key.IAC_02).getDescription();
		declaration.setIae2Key(StringUtils.isBlank(k2)?0:Integer.parseInt(k2) );
		String k3 = mod303.ensureDetail(Mod303Key.IAC_03).getDescription();
		declaration.setIae3Key(StringUtils.isBlank(k3)?0:Integer.parseInt(k3) );
		String k4 = mod303.ensureDetail(Mod303Key.IAC_04).getDescription();
		declaration.setIae4Key(StringUtils.isBlank(k4)?0:Integer.parseInt(k4) );
		String k5 = mod303.ensureDetail(Mod303Key.IAC_05).getDescription();
		declaration.setIae5Key(StringUtils.isBlank(k5)?0:Integer.parseInt(k5) );
		String k6 = mod303.ensureDetail(Mod303Key.IAC_06).getDescription();
		declaration.setIae6Key(StringUtils.isBlank(k6)?0:Integer.parseInt(k6) );
		
		String epi1 = mod303.ensureDetail(Mod303Key.IAE_01).getDescription();
		String epi2 = mod303.ensureDetail(Mod303Key.IAE_02).getDescription();
		String epi3 = mod303.ensureDetail(Mod303Key.IAE_03).getDescription();
		String epi4 = mod303.ensureDetail(Mod303Key.IAE_04).getDescription();
		String epi5 = mod303.ensureDetail(Mod303Key.IAE_05).getDescription();
		String epi6 = mod303.ensureDetail(Mod303Key.IAE_06).getDescription();
		declaration.setIae1Epigraph(epi1);
		declaration.setIae2Epigraph(epi2);
		declaration.setIae3Epigraph(epi3);
		declaration.setIae4Epigraph(epi4);
		declaration.setIae5Epigraph(epi5);
		declaration.setIae6Epigraph(epi6);
		
		if (AonStringUtils.isNotBlank(epi1)
		 || AonStringUtils.isNotBlank(epi2)
		 || AonStringUtils.isNotBlank(epi3)
		 || AonStringUtils.isNotBlank(epi4)
		 || AonStringUtils.isNotBlank(epi5)
		 || AonStringUtils.isNotBlank(epi6)) {
			declaration.setC80(mod303.getEnsuredAmount(Mod303Key.C80));
			declaration.setC81(mod303.getEnsuredAmount(Mod303Key.C81));
			declaration.setC82(mod303.getEnsuredAmount(Mod303Key.C82));
			declaration.setC83(mod303.getEnsuredAmount(Mod303Key.C83));
			declaration.setC84(mod303.getEnsuredAmount(Mod303Key.C84));
			declaration.setC85(mod303.getEnsuredAmount(Mod303Key.C85));
			declaration.setC86(mod303.getEnsuredAmount(Mod303Key.C86));
			declaration.setC87(mod303.getEnsuredAmount(Mod303Key.C87));
			declaration.setC88(mod303.getEnsuredAmount(Mod303Key.C88));
		}
		
	}
	
	private double ensureEpigraph(double epi) {
		if (epi == 3141 || epi == 3142) {
			epi = 314.0;
		} else if (epi == 3151 || epi == 3152) {
			epi = 315.0;
		}
		return epi;
	}

	private void populateDeclaration(Mod303 mod303, Declaration declaration) {
		declaration.setDifference(mod303.getEnsuredAmount(Mod303Key.C64));
		declaration.setAlavaPercent(0.0); 
		declaration.setGipuzkoaPercent(0.0); 
		declaration.setBizkaiaPercent(0.0); 
		declaration.setNavarraPercent(0.0); 
		declaration.setCommonTerritoryPercent(mod303.getEnsuredAmount(Mod303Key.C65));
		declaration.setQuota(mod303.getEnsuredAmount(Mod303Key.C66));
		declaration.setPreviousYearCompensateQuota(mod303.getEnsuredAmount(Mod303Key.C67));
		declaration.setRegularizationResult(mod303.getEnsuredAmount(Mod303Key.C68));
		declaration.setResult0(mod303.getEnsuredAmount(Mod303Key.C69));
		declaration.setToDeduct(mod303.getEnsuredAmount(Mod303Key.C70));
		declaration.setResult(mod303.getEnsuredAmount(Mod303Key.C71));
		declaration.setExtraCharge(0.0);
		declaration.setDelayInterest(0.0);
		
		declaration.setComplementary(mod303.getHeader().isComplementary());
		declaration.setWithoutActivity(mod303.getHeader().isWithoutActivity());
		
		if (declaration.getResult() < 0) {
			if (mod303.getEnsuredAmount(Mod303Key.PBK) != 0) {
				declaration.setCompensate(0.0);
				declaration.setPayBack(CommonUtil.round(declaration.getResult() * -1));		
			} else {
				declaration.setPayBack(0.0);
				declaration.setCompensate(CommonUtil.round(declaration.getResult() * -1));
				declaration.setBankAccount(null);
			}
		}
		
		if ( mod303.getBankAccountContainer() != null 
			&& mod303.getBankAccountContainer().getBankAccount() != null
			&& !StringUtils.isBlank(mod303.getBankAccountContainer().getBankAccount().getBban())) {
			BankAccount bankAccount = mod303.getBankAccountContainer().getBankAccount();
			declaration.setBankAccount(bankAccount.getIban());
		}
	}

}
