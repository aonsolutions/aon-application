package com.code.aon.ui.finance.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD347.MOD347;
import com.code.aon.file.tax.model.MOD347.MOD347Format;
import com.code.aon.file.tax.model.MOD347.data.Declared;
import com.code.aon.file.tax.model.MOD347.data.Deponent;
import com.code.aon.finance.enumeration.Model347Type;
import com.code.aon.finance.model347.Model347;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.util.AonUtil;

public class MOD347Writer implements IFinanceConstants {
	
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

	public FileOutput createMOD347(Integer year, MOD347Format format, List<Model347> summary) throws ManagerBeanException {
		try {
			Deponent deponent = getDeponent(year,summary);
			File file = File.createTempFile("MOD347_", ".txt");
			FileFiller csb19 = new MOD347(deponent, format, file.getAbsolutePath());
			FileOutput output = new FileOutput();
			output.setFile(file);
			output.setErrors(csb19.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}

	private Deponent getDeponent(Integer year, List<Model347> summary) throws ManagerBeanException {
			Deponent deponent = new  Deponent();
			deponent.setCode(getCompany().getDocument());
			deponent.setComplementary(false);
			
			// TODO
			deponent.setJustify(347000); 

			
			deponent.setName(getCompany().getName());
			deponent.setRelName(getCompany().getName());
			RegistryMedia  phone = getCompany().getPhone();
			deponent.setRelPhone(null);
			if (phone != null){
				try {
					deponent.setRelPhone(Integer.parseInt( phone.getValue() ));
				} catch (NumberFormatException e) {
					// Nothing
				}
			}
			deponent.setReplacedJustify(null);
			deponent.setReplaces(false);
			deponent.setType("T");
			deponent.setYear(year);
			for (Model347 mod347 : summary) {
				if (!mod347.isDisabled()) {
					Declared dec = new Declared();
					dec.setCode(mod347.getDocument()==null?null:mod347.getDocument().getDocument());
					dec.setManagerCode(null);
					dec.setName(mod347.getName());
					dec.setKey(mod347.getType()==Model347Type.A_KEY?"A":"B");
					dec.setInsurance(false);
					dec.setProvince(mod347.getGeozone());
					dec.setCountry(mod347.getCountry());
					dec.setQuantity(mod347.getTotal());
					dec.setRenting(false);
					deponent.getDeclareds().add(dec);
				}
			}
			return deponent;
	}

}
