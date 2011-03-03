package com.code.aon.ui.fiscal.file;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
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
import com.code.aon.fiscal.dao.IFiscalAlias;
import com.code.aon.fiscal.enumeration.VatTaxKey;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.util.AonUtil;

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
			File file = File.createTempFile("MOD303_", ".txt");
			FileFiller mod303 = new MOD303(declaration, format, file.getAbsolutePath());
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
			RegistryMedia  phone = getCompany().getPhone();
			declaration.setTelephone(null);
			if (phone != null){
				try {
					declaration.setTelephone(Integer.parseInt( phone.getValue() ));
				} catch (NumberFormatException e) {
					// Nothing
				}
			}
			RegistryAddress address = getCompany().getDefaultAddress();
			declaration.setAddress( address.getAddress() );
			if (StringUtils.isNotEmpty(address.getNumber())){
				try {
					declaration.setAddressNumber(Integer.parseInt(address.getNumber()));
				} catch (NumberFormatException e) {
					// Nothing
				}
			}
			declaration.setEntity(address.getCity());
			declaration.setCity(address.getCity());
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
			declaration.setAmounts(getAmounts(vatTaxDeclaration));
			return declaration;
	}
	
	private String getAmounts(VatTaxDeclaration dec) throws ManagerBeanException {
		VatTax vatTax = dec.getVatTax(); 

		StringBuffer buf = new StringBuffer();
		
		int i = 0;
		if ( vatTax.isReplacement() || vatTax.isComplementary()) {
			buf.append( "091");
			buf.append( getForm().format(1.0));
			++i;
			buf.append( "902");		
			buf.append( getForm().format(0.0)); //TODO Numero de la declaracion anterior.
			++i;
		}

		buf.append(vatTax.isTaxRefundRegistry()?"092":"093");
		buf.append( getForm().format(1.0));
		++i;

		IManagerBean bean = BeanManager.getManagerBean(VatTaxDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IFiscalAlias.VAT_TAX_DETAIL_VAT_TAX_ID), vatTax.getId());
		criteria.addOrder(bean.getFieldName(IFiscalAlias.VAT_TAX_DETAIL_KEY));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to: list) {
			VatTaxDetail detail = (VatTaxDetail) to;
			String value = getValue(detail, i);
			if (value != null) {
				buf.append( value );
			}
		}

		bean = BeanManager.getManagerBean(VatTaxDeclaration.class);
		criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IFiscalAlias.VAT_TAX_DECLARATION_VAT_TAX_ID), vatTax.getId());
		list = bean.getList(criteria);
		double c40 = 0.0;
		double c41 = 0.0;
		double c42 = 0.0;
		double c43 = 0.0;
		for (ITransferObject to: list) {
			VatTaxDeclaration d = (VatTaxDeclaration) to;
			if (d.getAdministration() == Administration.ALAVA) {
				c40 = d.getPercent(); 
			} else if (d.getAdministration() == Administration.GIPUZKOA) {
				c41 = d.getPercent(); 
			} else if (d.getAdministration() == Administration.BIZKAIA) {
				c42 = d.getPercent(); 
			} else {
				c43 = CommonUtil.round(c43 + d.getPercent());
			}
		}
		if (c40 != 0.0) {
			buf.append( "040");
			buf.append( form.format( c40) );
			++i;
		}
		if (c41 != 0.0) {
			buf.append( "041");
			buf.append( form.format( c41) );
			++i;
		}
		if (c42 != 0.0) {
			buf.append( "042");
			buf.append( form.format( c42) );
			++i;
		}
		if (c43 != 0.0) {
			buf.append( "043");
			buf.append( form.format( c43) );
			++i;
		}
		if (dec.getQuota() != 0) {
			buf.append( "044");
			buf.append( form.format( dec.getQuota() ) );
			++i;
		}
		if (dec.getPreviousYearCompensateQuota() != 0) {
			buf.append( "045");
			buf.append( form.format( dec.getPreviousYearCompensateQuota() ) );
			++i;
		}
		if (dec.getResult() != 0) {
			buf.append( "060");
			buf.append( form.format( dec.getResult() ) );
			++i;
		}
		if (dec.getExtraCharge() != 0) {
			buf.append( "061");
			buf.append( form.format( dec.getExtraCharge() ) );
			++i;
		}
		if (dec.getDelayInterest() != 0) {
			buf.append( "062");
			buf.append( form.format( dec.getDelayInterest() ) );
			++i;
		}
		if (dec.getDeposit() != 0) {
			buf.append( "080");
			buf.append( form.format( dec.getDeposit() ) );
			++i;
		}
		if (dec.getCompensate() != 0) {
			buf.append( "081");
			buf.append( form.format( CommonUtil.round(dec.getCompensate() * (-1)) ) );
			++i;
		}
		if (dec.getPayBack() != 0) {
			buf.append( "082");
			buf.append( form.format( CommonUtil.round(dec.getPayBack() * (-1)) ) );
			++i;
		}
		buf.append( "301");
		buf.append( form.format( new Double(dec.isWithoutActivity()?1:0)));
		++i;
		
		if ( dec.getRegistryBank() != null && dec.getRegistryBank().getId() != null && dec.getRegistryBank().getBankAccount() != null) {
			BankAccount bankAccount = dec.getRegistryBank().getBankAccount();
			buf.append( "301");
			buf.append( form.format( new Double(bankAccount.getEntity())));
			++i;
			
			buf.append( "302");
			buf.append( form.format( new Double(bankAccount.getOffice())));
			++i;

			buf.append( "303");
			buf.append( form.format( new Double(bankAccount.getControl())));
			++i;

			buf.append( "304");
			buf.append( form.format( new Double(bankAccount.getAccount())));
			++i;
		}
	
		for (;i<95;i++) {
			buf.append( "000");		
			buf.append( form.format( 0.0 )); 
		}

		return buf.toString();
		
	}
	private String getValue(VatTaxDetail detail, int i) {
		StringBuffer buf = new StringBuffer();
		VatTaxKey key = detail.getKey();
		if (key == VatTaxKey.A1 && detail.getPercent() == 4) {
			append(buf,i,"001",detail.getTaxableBase());
			append(buf,i,"002",detail.getPercent());
			append(buf,i,"003",detail.getQuota());
		} else if (key == VatTaxKey.A1 && detail.getPercent() == 8) {
			append(buf,i,"104",detail.getTaxableBase());
			append(buf,i,"105",detail.getPercent());
			append(buf,i,"106",detail.getQuota());
		} else if (key == VatTaxKey.A1 && detail.getPercent() == 18) {
			append(buf,i,"107",detail.getTaxableBase());
			append(buf,i,"108",detail.getPercent());
			append(buf,i,"109",detail.getQuota());
		} else if (key == VatTaxKey.A1 && detail.getPercent() == 7) {
			append(buf,i,"004",detail.getTaxableBase());
			append(buf,i,"005",detail.getPercent());
			append(buf,i,"006",detail.getQuota());
		} else if (key == VatTaxKey.A1 && detail.getPercent() == 16) {
			append(buf,i,"007",detail.getTaxableBase());
			append(buf,i,"008",detail.getPercent());
			append(buf,i,"009",detail.getQuota());
		} else if (key == VatTaxKey.A2 && detail.getPercent() == 0.5) {
			append(buf,i,"010",detail.getTaxableBase());
			append(buf,i,"011",detail.getPercent());
			append(buf,i,"012",detail.getQuota());
		} else if (key == VatTaxKey.A2 && detail.getPercent() == 4) {
			append(buf,i,"013",detail.getTaxableBase());
			append(buf,i,"014",detail.getPercent());
			append(buf,i,"015",detail.getQuota());
		} else if (key == VatTaxKey.A3 && detail.getPercent() == 4) {
			append(buf,i,"019",detail.getTaxableBase());
			append(buf,i,"020",detail.getPercent());
			append(buf,i,"021",detail.getQuota());
		} else if (key == VatTaxKey.A3 && detail.getPercent() == 8) {
			append(buf,i,"122",detail.getTaxableBase());
			append(buf,i,"123",detail.getPercent());
			append(buf,i,"124",detail.getQuota());
		} else if (key == VatTaxKey.A3 && detail.getPercent() == 18) {
			append(buf,i,"125",detail.getTaxableBase());
			append(buf,i,"126",detail.getPercent());
			append(buf,i,"127",detail.getQuota());
		} else if (key == VatTaxKey.A3 && detail.getPercent() == 7) {
			append(buf,i,"022",detail.getTaxableBase());
			append(buf,i,"023",detail.getPercent());
			append(buf,i,"024",detail.getQuota());
		} else if (key == VatTaxKey.A3 && detail.getPercent() == 16) {
			append(buf,i,"025",detail.getTaxableBase());
			append(buf,i,"026",detail.getPercent());
			append(buf,i,"027",detail.getQuota());
		} else if (key == VatTaxKey.AT) {
			append(buf,i,"028",detail.getQuota());
		} else if (key == VatTaxKey.B1) {
			append(buf,i,"030",detail.getQuota());
		} else if (key == VatTaxKey.B2) {
			append(buf,i,"031",detail.getQuota());
		} else if (key == VatTaxKey.C1) {
			append(buf,i,"032",detail.getQuota());
		} else if (key == VatTaxKey.C2) {
			append(buf,i,"033",detail.getQuota());
		} else if (key == VatTaxKey.D1) {
			append(buf,i,"034",detail.getQuota());
		} else if (key == VatTaxKey.D2) {
			append(buf,i,"035",detail.getQuota());
		} else if (key == VatTaxKey.ET) {
			append(buf,i,"036",detail.getQuota());
		// TODO SOPORTE PARA LA REGULARACION DE INVERSIONES.
		} else if (key == VatTaxKey.FT) {
			append(buf,i,"038",detail.getQuota());
		} else if (key == VatTaxKey.DF) {
			append(buf,i,"039",detail.getQuota());
		}else if (key == VatTaxKey.EI) {
			append(buf,i,"050",detail.getTaxableBase());
		}else if (key == VatTaxKey.EX1) { // TODO Tener en cuenta EX2
			append(buf,i,"051",detail.getTaxableBase());
		}else if (key == VatTaxKey.OO) {
			append(buf,i,"052",detail.getTaxableBase());
		}
				
		return buf.length() > 0?buf.toString():null;
	}

	private void append(StringBuffer buf, int i, String box, double value) {
		if (value != 0.0 ) {
			buf.append( box );
			buf.append( form.format( value ));
			++i;
		}
	}

}
