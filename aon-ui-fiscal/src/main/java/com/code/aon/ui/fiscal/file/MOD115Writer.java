package com.code.aon.ui.fiscal.file;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.file.format.Numeric;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD115.MOD115;
import com.code.aon.file.tax.model.MOD115.MOD115Format;
import com.code.aon.file.tax.model.MOD115.data.Declaration;
import com.code.aon.fiscal.Renting;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.util.AonUtil;

public class MOD115Writer implements IFinanceConstants{
	
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

	public FileOutput createMOD115(Renting renting,MOD115Format format) throws ManagerBeanException {
		try {
			Declaration declaration = getDeclaration(renting);
			File file = File.createTempFile("MOD115_", ".txt");
			FileFiller mod115 = new MOD115(declaration, format, file.getAbsolutePath());
			FileOutput output = new FileOutput();
			output.setFile(file);
			output.setErrors(mod115.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}

	private Declaration getDeclaration(Renting renting) throws ManagerBeanException {
			SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
			Declaration declaration = new  Declaration();
			declaration.setDocument(getCompany().getDocument());
			declaration.setStartPeriod(0);
			declaration.setEndPeriod(0);
			int year = renting.getYear();
			String startDate = formatter.format(renting.getPeriod().getStartDate(year));
			declaration.setStartPeriod(Integer.parseInt( startDate));
			String endDate = formatter.format(renting.getPeriod().getDueDate(year));
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
			declaration.setAddressNumber(0); // TODO parse address
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
			declaration.setResult( renting.getTotalTaxDebt());
			declaration.setAmounts(getAmounts(renting));
			return declaration;
	}
	
	private String getAmounts(Renting renting) {
		Numeric form = new Numeric();
		form.applyPattern("S9(12)V99");

		StringBuffer buf = new StringBuffer();
		
		int i = 0;
		if ( renting.isReplacement() || renting.isComplementary()) {
			buf.append( "901");
			buf.append( form.format(1.0));
			++i;
			buf.append( "902");		
			buf.append( form.format(0.0)); //TODO Numero de la declaracion anterior.
			++i;
		}
		if (renting.getLessorCount() != 0) {
			buf.append( "001");		
			buf.append( form.format( renting.getLessorCount() )); 
			++i;
		}
		
		if (renting.getRentingAmount() != 0.0) {
			buf.append( "002");		
			buf.append( form.format( renting.getRentingAmount() )); 
			++i;
		}

		if (renting.getRetention() != 0.0) {
			buf.append( "003");		
			buf.append( form.format( renting.getRetention() )); 
			++i;
		}
		if (renting.getLessorCountInKind() != 0.0) {
			buf.append( "004");		
			buf.append( form.format( renting.getLessorCountInKind())); 
			++i;
		}
		if (renting.getRemunerationInKind() != 0.0) {
			buf.append( "005");		
			buf.append( form.format( renting.getRemunerationInKind())); 
			++i;
		}

		if (renting.getAccountDeposit() != 0.0) {
			buf.append( "006");		
			buf.append( form.format( renting.getAccountDeposit())); 
			++i;
		}
		if ( renting.getDeposit() != 0.0) {
			buf.append( "007");		
			buf.append( form.format( renting.getDeposit())); 
			++i;
		}

		if ( renting.getExtraCharge() != 0.0) {
			buf.append( "009");		
			buf.append( form.format( renting.getExtraCharge())); 
			++i;
		}

		if ( renting.getDelayInterest() != 0.0) {
			buf.append( "010");		
			buf.append( form.format( renting.getDelayInterest())); 
			++i;
		}

		buf.append( "011");		
		buf.append( form.format( renting.getTotalTaxDebt())); 
		++i;

		if ( renting.getRegistryBank() != null && renting.getRegistryBank().getId() != null && renting.getRegistryBank().getBankAccount() != null) {
			buf.append( "091");
			buf.append( form.format( new Double(renting.getRegistryBank().getBankAccount().getEntity())));
			++i;
			
			buf.append( "092");
			buf.append( form.format( new Double(renting.getRegistryBank().getBankAccount().getOffice())));
			++i;

			buf.append( "093");
			buf.append( form.format( new Double(renting.getRegistryBank().getBankAccount().getControl())));
			++i;

			buf.append( "094");
			buf.append( form.format( new Double(renting.getRegistryBank().getBankAccount().getAccount())));
			++i;
		}
	
		for (;i<53;i++) {
			buf.append( "000");		
			buf.append( form.format( 0.0 )); 
		}

		return buf.toString();
		
	}

}
