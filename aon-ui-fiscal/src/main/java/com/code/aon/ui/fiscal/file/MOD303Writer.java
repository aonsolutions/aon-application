package com.code.aon.ui.fiscal.file;


import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
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
import com.code.aon.file.tax.model.MOD303.MOD303;
import com.code.aon.file.tax.model.MOD303.MOD303Format;
import com.code.aon.file.tax.model.MOD303.data.Breakdown;
import com.code.aon.file.tax.model.MOD303.data.Declaration;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.VatTaxDeclaration;
import com.code.aon.fiscal.VatTaxDetail;
import com.code.aon.fiscal.enumeration.VatTaxKey;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class MOD303Writer {
	
	private Company company;

	public Company getCompany() {
		if (company == null) {
			CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			setCompany( companyController.obtainCompany() );
		}
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	public FileOutput createMOD303(List<VatTaxDeclaration> vatTaxDeclarations,MOD303Format format) throws ManagerBeanException {
		List<Declaration> declarations = new LinkedList<Declaration>();
		for (VatTaxDeclaration vatTaxDeclaration : vatTaxDeclarations) {
			Declaration declaration = getDeclaration(vatTaxDeclaration);
			declarations.add(declaration);
		}
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(output);
		MOD303 mod303 = new MOD303();
		FileOutput fileOutput = new FileOutput();
		fileOutput.setErrors(mod303.create(declarations, format, writer));
		fileOutput.setContent(output.toByteArray());
		return fileOutput;
	}

	private Declaration getDeclaration(VatTaxDeclaration vatTaxDeclaration) throws ManagerBeanException {
			SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
			Declaration declaration = new  Declaration();
			declaration.setYear(vatTaxDeclaration.getVatTax().getYear());
			declaration.setPeriod(vatTaxDeclaration.getVatTax().getPeriod().getName());
			if (vatTaxDeclaration.getRegistryBank() != null && vatTaxDeclaration.getRegistryBank().getBankAccount() != null) {
				BankAccount ba = vatTaxDeclaration.getRegistryBank().getBankAccount();
				declaration.setCcc1(ba.getEntity());
				declaration.setCcc2(ba.getOffice());
				declaration.setCcc3(ba.getControl());
				declaration.setCcc4(ba.getAccount());
				declaration.setBankName(vatTaxDeclaration.getRegistryBank().getBank().getName());
			}
			
			declaration.setDocument(getCompany().getDocument());
			declaration.setStartPeriod(0);
			declaration.setEndPeriod(0);
			VatTax vatTax = vatTaxDeclaration.getVatTax(); 
			int year = vatTax.getYear();
			String startDate = formatter.format(vatTax.getPeriod().getStartDate(year));
			declaration.setStartPeriod(Integer.parseInt( startDate));
			String endDate = formatter.format(vatTax.getPeriod().getDueDate(year));
			declaration.setEndPeriod(Integer.parseInt( endDate));
			declaration.setName(getCompany().getName());
			RegistryMedia rm =getCompany().getPhone(); 
			declaration.setTelephone(rm!=null?rm.getValue():null);
			rm =getCompany().getFax();
			declaration.setFax(rm!=null?rm.getValue():null);
			rm =getCompany().getEmail();
			declaration.setEmail(rm!=null?rm.getValue():null);
			RegistryAddress address = getCompany().getDefaultAddress();
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
			
			declaration.setReplacement( vatTax.isReplacement() );
			declaration.setComplementary( vatTax.isComplementary() );
			declaration.setTaxRefundRegistry( vatTax.isTaxRefundRegistry());
			
			double prorata = vatTax.getProrata();
			if (prorata != 100.0) {
				declaration.setProrata(prorata);
				declaration.setGeneralProrataApplied(true);
			}
			declaration.setSpecialProrataApplied(false);
			
			populateDeclarationDetail(vatTax,declaration);
			
			populateDeclaration(vatTaxDeclaration,declaration);
			
			return declaration;
	}
	
	private void populateDeclarationDetail(VatTax vatTax, Declaration declaration) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(VatTaxDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_VAT_TAX_ID), vatTax.getId());
		criteria.addOrder(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_KEY));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to: list) {
			VatTaxDetail detail = (VatTaxDetail) to;
			fillDeclaration(detail,declaration);
		}
	}

	private void fillDeclaration(VatTaxDetail detail, Declaration declaration) {
		VatTaxKey key = detail.getKey();
		double percent = detail.getPercent();
		String mapKey = new Double(percent).toString(); 
		double taxableBase = detail.getTaxableBase();
		double quota = detail.getQuota();
		double deductiblequota = detail.getDeductibleQuota()!=0.0?detail.getDeductibleQuota():detail.getQuota();
		Breakdown bd = new Breakdown(percent,taxableBase,quota,deductiblequota);
		if (key == VatTaxKey.A1 ) {
			declaration.getOutputVat().put(mapKey, bd);
		} else if (key == VatTaxKey.A2 ) {
			declaration.getSurcharge().put(mapKey, bd);
		} else if (key == VatTaxKey.A3 ) {
			declaration.getIntracommunitary().put(mapKey, bd);
			declaration.setBaseIntracommunitary( CommonUtil.round(declaration.getBaseIntracommunitary() +taxableBase,2));
			declaration.setQuotaIntracommunitary( CommonUtil.round(declaration.getQuotaIntracommunitary() +quota,2));
		} else if (key == VatTaxKey.A4) {
			declaration.setBaseInvPasive( taxableBase );
			declaration.setQuotaInvPasive( quota );
		} else if (key == VatTaxKey.A5) {
			declaration.setBaseModifications( taxableBase );
			declaration.setQuotaModifications( quota );
		} else if (key == VatTaxKey.AT) {
			declaration.setOutputTotal( quota );
		} else if (key == VatTaxKey.B1) {
			declaration.setInnerCommonOperationsQuota( quota );
			declaration.setInnerCommonOperationsBase( taxableBase );
		} else if (key == VatTaxKey.B2) {
			declaration.setInnerInvestmentOperationsQuota( quota );
			declaration.setInnerInvestmentOperationsBase( taxableBase );
		} else if (key == VatTaxKey.B3) {
			declaration.setInnerExpensesOperationsQuota( quota );
			declaration.setInnerExpensesOperationsBase( taxableBase );
		} else if (key == VatTaxKey.C1) {
			declaration.setImportedCommonOperationsQuota( quota );
			declaration.setImportedCommonOperationsBase( taxableBase );
		} else if (key == VatTaxKey.C2) {
			declaration.setImportedInvestmentOperationsQuota( quota );
			declaration.setImportedInvestmentOperationsBase( taxableBase );
		} else if (key == VatTaxKey.D1) {
			declaration.setIntracommunitaryCommonOperationsQuota( quota );
			declaration.setIntracommunitaryCommonOperationsBase( taxableBase );
		} else if (key == VatTaxKey.D2) {
			declaration.setIntracommunitaryInvestmentOperationsQuota( quota );
			declaration.setIntracommunitaryInvestmentOperationsBase( taxableBase );
		} else if (key == VatTaxKey.D3) {
			declaration.setIntracommunitaryExpensesOperationsQuota( quota );
			declaration.setIntracommunitaryExpensesOperationsBase( taxableBase );
		} else if (key == VatTaxKey.ET) {
			declaration.setAgriculturalRegimeCompensation( quota );
		} else if (key == VatTaxKey.RI) {
			declaration.setInvestmentNormalization( quota );
		} else if (key == VatTaxKey.FT) {
			declaration.setDeductTotal( quota );
		} else if (key == VatTaxKey.DF) {
			declaration.setDifference( quota );
		}else if (key == VatTaxKey.EI) {
			declaration.setIntracommunitaryDeliveries( taxableBase );
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
			if (percent != 4 && percent != 8 && percent != 18) {
				mapKey = "?";
			}
			declaration.getInnerAssetPurchases().put(mapKey, bd);
			declaration.setBaseInnerAssetPurchases( CommonUtil.round(declaration.getBaseInnerAssetPurchases() +taxableBase,2));
			declaration.setQuotaInnerAssetPurchases( CommonUtil.round(declaration.getQuotaInnerAssetPurchases() +quota,2));
			declaration.setDeductibleQuotaInnerAssetPurchases( CommonUtil.round(declaration.getDeductibleQuotaInnerAssetPurchases() +deductiblequota,2));
			declaration.setBaseTotalAddInfo( CommonUtil.round(declaration.getBaseTotalAddInfo() +taxableBase,2));
			declaration.setQuotaTotalAddInfo( CommonUtil.round(declaration.getQuotaTotalAddInfo() +quota,2));
			declaration.setDeductibleQuotaTotalAddInfo( CommonUtil.round(declaration.getDeductibleQuotaTotalAddInfo() +deductiblequota,2));
		} else if (key == VatTaxKey.GT ) {
			if (percent != 4 && percent != 8 && percent != 18) {
				mapKey = "?";
			}
			declaration.getExpenses().put(mapKey, bd);
			declaration.setBaseExpenses( CommonUtil.round(declaration.getBaseExpenses() +taxableBase,2));
			declaration.setQuotaExpenses( CommonUtil.round(declaration.getQuotaExpenses() +quota,2));
			declaration.setDeductibleQuotaExpenses( CommonUtil.round(declaration.getDeductibleQuotaExpenses() +deductiblequota,2));
			declaration.setBaseTotalAddInfo( CommonUtil.round(declaration.getBaseTotalAddInfo() +taxableBase,2));
			declaration.setQuotaTotalAddInfo( CommonUtil.round(declaration.getQuotaTotalAddInfo() +quota,2));
			declaration.setDeductibleQuotaTotalAddInfo( CommonUtil.round(declaration.getDeductibleQuotaTotalAddInfo() +deductiblequota,2));
		} else if (key == VatTaxKey.BI ) {
			if (percent != 4 && percent != 8 && percent != 18) {
				mapKey = "?";
			}
			declaration.getInvestmentAsset().put(mapKey, bd);
			declaration.setBaseInvestmentAsset( CommonUtil.round(declaration.getBaseInvestmentAsset() +taxableBase,2));
			declaration.setQuotaInvestmentAsset( CommonUtil.round(declaration.getQuotaInvestmentAsset() +quota,2));
			declaration.setDeductibleQuotaInvestmentAsset( CommonUtil.round(declaration.getDeductibleQuotaInvestmentAsset() +deductiblequota,2));
			declaration.setBaseTotalAddInfo( CommonUtil.round(declaration.getBaseTotalAddInfo() +taxableBase,2));
			declaration.setQuotaTotalAddInfo( CommonUtil.round(declaration.getQuotaTotalAddInfo() +quota,2));
			declaration.setDeductibleQuotaTotalAddInfo( CommonUtil.round(declaration.getDeductibleQuotaTotalAddInfo() +deductiblequota,2));
		}
	}

	private void populateDeclaration(VatTaxDeclaration dec, Declaration declaration) throws ManagerBeanException {
		VatTax vatTax = dec.getVatTax(); 

		IManagerBean bean = BeanManager.getManagerBean(VatTaxDeclaration.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_ID), vatTax.getId());
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
				declaration.setDepositBankEntity(bankAccount.getEntity());
				declaration.setDepositBankOffice(bankAccount.getOffice());
				declaration.setDepositBankControl(bankAccount.getControl());
				declaration.setDepositBankAccount(bankAccount.getAccount());
			}
			if (dec.getPayBack() > 0) {
				declaration.setPayBackBankEntity(bankAccount.getEntity());
				declaration.setPayBackBankOffice(bankAccount.getOffice());
				declaration.setPayBackBankControl(bankAccount.getControl());
				declaration.setPayBackBankAccount(bankAccount.getAccount());
			}
		}
		
	}

}
