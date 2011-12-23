package com.code.aon.ui.fiscal.file;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.config.BankAccount;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.file.format.Numeric;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD303.MOD303;
import com.code.aon.file.tax.model.MOD303.MOD303Format;
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
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class MOD303Writer implements IFinanceConstants{
	
	private Company company;
	private Numeric form;
	
	public Numeric getForm() {
		if (form == null) {
			form = new Numeric();
			form.applyPattern("S9(12)V99");			
		}
		return form;
	}

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

	public FileOutput createMOD303(VatTaxDeclaration vatTaxDeclaration,MOD303Format format) throws ManagerBeanException {
		try {
			Declaration declaration = getDeclaration(vatTaxDeclaration);
			File file = File.createTempFile("MOD303_","."+format.getMimeType().getExtension());
			PrintWriter writer = new PrintWriter(file);
			FileFiller mod303 = new MOD303(declaration, format, writer);
			FileOutput output = new FileOutput();
			output.setFile(file);
			output.setErrors(mod303.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
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
			declaration.setProvince(address.getGeozone()==null?"":address.getGeozone().getCode());
			String zip = address.getZip();
			declaration.setZip(0);
			if (zip != null){
				try {
					declaration.setZip(Integer.parseInt( zip ));
				} catch (NumberFormatException e) {
					// Nothing
				}
			}

			declaration.setReplacement( vatTax.isReplacement() );
			declaration.setComplementary( vatTax.isComplementary() );
			declaration.setTaxRefundRegistry( vatTax.isTaxRefundRegistry());
			
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
		if (key == VatTaxKey.A1 && detail.getPercent() == 4) {
			declaration.setBaseOutputVat4( detail.getTaxableBase() );
			declaration.setPercentOutputVat4( detail.getPercent() );
			declaration.setQuotaOutputVat4( detail.getQuota() );
		} else if (key == VatTaxKey.A1 && detail.getPercent() == 8) {
			declaration.setBaseOutputVat8( detail.getTaxableBase() );
			declaration.setPercentOutputVat8( detail.getPercent() );
			declaration.setQuotaOutputVat8( detail.getQuota() );
		} else if (key == VatTaxKey.A1 && detail.getPercent() == 18) {
			declaration.setBaseOutputVat18( detail.getTaxableBase() );
			declaration.setPercentOutputVat18( detail.getPercent() );
			declaration.setQuotaOutputVat18( detail.getQuota() );
		} else if (key == VatTaxKey.A1 && detail.getPercent() == 7) {
			declaration.setBaseOutputVat7( detail.getTaxableBase() );
			declaration.setPercentOutputVat7( detail.getPercent() );
			declaration.setQuotaOutputVat7( detail.getQuota() );
		} else if (key == VatTaxKey.A1 && detail.getPercent() == 16) {
			declaration.setBaseOutputVat16( detail.getTaxableBase() );
			declaration.setPercentOutputVat16( detail.getPercent() );
			declaration.setQuotaOutputVat16( detail.getQuota() );
		} else if (key == VatTaxKey.A2 && detail.getPercent() == 0.5) {
			declaration.setBaseSurcharge05( detail.getTaxableBase() );
			declaration.setPercentSurcharge05( detail.getPercent() );
			declaration.setQuotaSurcharge05( detail.getQuota() );
		} else if (key == VatTaxKey.A2 && detail.getPercent() == 1) {
			declaration.setBaseSurcharge1( detail.getTaxableBase() );
			declaration.setPercentSurcharge1( detail.getPercent() );
			declaration.setQuotaSurcharge1( detail.getQuota() );
		} else if (key == VatTaxKey.A2 && detail.getPercent() == 4) {
			declaration.setBaseSurcharge4( detail.getTaxableBase() );
			declaration.setPercentSurcharge4( detail.getPercent() );
			declaration.setQuotaSurcharge4( detail.getQuota() );
		} else if (key == VatTaxKey.A3 && detail.getPercent() == 4) {
			declaration.setBaseIntracommunitary4( detail.getTaxableBase() );
			declaration.setPercentIntracommunitary4( detail.getPercent() );
			declaration.setQuotaIntracommunitary4( detail.getQuota() );
		} else if (key == VatTaxKey.A3 && detail.getPercent() == 8) {
			declaration.setBaseIntracommunitary8( detail.getTaxableBase() );
			declaration.setPercentIntracommunitary8( detail.getPercent() );
			declaration.setQuotaIntracommunitary8( detail.getQuota() );
		} else if (key == VatTaxKey.A3 && detail.getPercent() == 18) {
			declaration.setBaseIntracommunitary18( detail.getTaxableBase() );
			declaration.setPercentIntracommunitary18( detail.getPercent() );
			declaration.setQuotaIntracommunitary18( detail.getQuota() );
		} else if (key == VatTaxKey.A3 && detail.getPercent() == 7) {
			declaration.setBaseIntracommunitary7( detail.getTaxableBase() );
			declaration.setPercentIntracommunitary7( detail.getPercent() );
			declaration.setQuotaIntracommunitary7( detail.getQuota() );
		} else if (key == VatTaxKey.A3 && detail.getPercent() == 16) {
			declaration.setBaseIntracommunitary16( detail.getTaxableBase() );
			declaration.setPercentIntracommunitary16( detail.getPercent() );
			declaration.setQuotaIntracommunitary16( detail.getQuota() );
		} else if (key == VatTaxKey.AT) {
			declaration.setOutputTotal( detail.getQuota() );
		} else if (key == VatTaxKey.B1) {
			declaration.setInnerCommonOperationsQuota( detail.getQuota() );
			declaration.setInnerCommonOperationsBase( detail.getTaxableBase() );
		} else if (key == VatTaxKey.B2) {
			declaration.setInnerInvestmentOperationsQuota( detail.getQuota() );
			declaration.setInnerInvestmentOperationsBase( detail.getTaxableBase() );
		} else if (key == VatTaxKey.B3) {
			declaration.setInnerExpensesOperationsQuota( detail.getQuota() );
			declaration.setInnerExpensesOperationsBase( detail.getTaxableBase() );
		} else if (key == VatTaxKey.C1) {
			declaration.setImportedCommonOperationsQuota( detail.getQuota() );
			declaration.setImportedCommonOperationsBase( detail.getTaxableBase() );
		} else if (key == VatTaxKey.C2) {
			declaration.setImportedInvestmentOperationsQuota( detail.getQuota() );
			declaration.setImportedInvestmentOperationsBase( detail.getTaxableBase() );
		} else if (key == VatTaxKey.D1) {
			declaration.setIntracommunitaryCommonOperationsQuota( detail.getQuota() );
			declaration.setIntracommunitaryCommonOperationsBase( detail.getTaxableBase() );
		} else if (key == VatTaxKey.D2) {
			declaration.setIntracommunitaryInvestmentOperationsQuota( detail.getQuota() );
			declaration.setIntracommunitaryInvestmentOperationsBase( detail.getTaxableBase() );
		} else if (key == VatTaxKey.D3) {
			declaration.setIntracommunitaryExpensesOperationsQuota( detail.getQuota() );
			declaration.setIntracommunitaryExpensesOperationsBase( detail.getTaxableBase() );
		} else if (key == VatTaxKey.ET) {
			declaration.setAgriculturalRegimeCompensation( detail.getQuota() );
		} else if (key == VatTaxKey.RI) {
			declaration.setInvestmentNormalization( detail.getQuota() );
		} else if (key == VatTaxKey.FT) {
			declaration.setDeductTotal( detail.getQuota() );
		} else if (key == VatTaxKey.DF) {
			declaration.setDifference( detail.getQuota() );
		}else if (key == VatTaxKey.EI) {
			declaration.setIntracommunitaryDeliveries( detail.getTaxableBase() );
		}else if (key == VatTaxKey.EX1) {
			double d = declaration.getExportationTotal();
			declaration.setExportationTotal( d + detail.getTaxableBase() );
		}else if (key == VatTaxKey.EX2) { 
			double d = declaration.getExportationTotal();
			declaration.setExportationTotal( d + detail.getTaxableBase() );
		}else if (key == VatTaxKey.OO) {
			double d = declaration.getNonTaxableTotal();
			declaration.setNonTaxableTotal( d + detail.getTaxableBase() );
		}else if (key == VatTaxKey.OS) {
			double d = declaration.getNonTaxableTotal();
			declaration.setNonTaxableTotal( d + detail.getTaxableBase() );
		}else if (key == VatTaxKey.OI) {
			double d = declaration.getNonTaxableTotal();
			declaration.setNonTaxableTotal( d + detail.getTaxableBase() );
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
