package com.code.aon.ui.fiscal.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.company.Company;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD347.MOD347;
import com.code.aon.file.tax.model.MOD347.MOD347Format;
import com.code.aon.file.tax.model.MOD347.data.Declared;
import com.code.aon.file.tax.model.MOD347.data.Deponent;
import com.code.aon.fiscal.Mod347;
import com.code.aon.fiscal.Mod347Detail;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class MOD347Writer implements IFinanceConstants{
	
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

	public FileOutput createMOD347(Mod347 mod347) throws ManagerBeanException {
		try {
			MOD347Format format = obtainFormat(mod347);
			if (format == null) {
				throw new ManagerBeanException("No existe soporte para el formato de la declaración " +
						"347 del ejercicio " + mod347.getYear() + " en la administración " + 
						mod347.getAdministration());	
			}
			Deponent deponent = getDeponent(mod347,format);
			File file = File.createTempFile("MOD347_", ".txt");
			FileFiller mod347Filler = new MOD347(deponent, format, file.getAbsolutePath());
			FileOutput output = new FileOutput();
			output.setFile(file);
			output.setErrors(mod347Filler.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}

	private MOD347Format obtainFormat(Mod347 mod347) {
		int year = mod347.getYear();
		Administration adm = mod347.getAdministration();
		MOD347Format f = null;
		for (MOD347Format format : MOD347Format.values() ) {
			if (format.getAdministration() == adm && year >= format.getYear()) {
				if (f == null || f.getYear() < format.getYear() ) {
					f = format;	
				}
			}
		}
		return f;
	}
	
	private Deponent getDeponent(Mod347 mod347,MOD347Format format) throws ManagerBeanException {
			Deponent deponent = new  Deponent();
			deponent.setCode(getCompany().getDocument());
			deponent.setComplementary(mod347.isComplementary());
			deponent.setReplacement(mod347.isReplacement());
			long a = 4370000000000L + mod347.getNumber();
			deponent.setNumber(a); 
			deponent.setName(getCompany().getName());
			RegistryAddress address = getCompany().getDefaultAddress();
			if (address != null) {
				GeoZone geozone = address.getGeozone();
				if (geozone != null) {
					deponent.setProvince(geozone.getCode());		
				}
			}
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
			
			if (mod347.getReplacedNumber() != null) {
				long b = 4370000000000L + mod347.getReplacedNumber();
				deponent.setReplacedNumber(b);
			} 
			deponent.setType("T");
			deponent.setYear(mod347.getYear());
			fillDeclared(deponent,mod347,format);
			return deponent;
	}
	
	private void fillDeclared(Deponent deponent, Mod347 mod347, MOD347Format format)  throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Mod347Detail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.MOD347DETAIL_MOD347_ID), mod347.getId());
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to: list) {
			Mod347Detail detail = (Mod347Detail) to;	
			Declared dec = new Declared();
			dec.setCode(detail.getDocument()==null?null:detail.getDocument());
			dec.setManagerCode(null);
			dec.setName(detail.getName());
			dec.setKey(detail.getType().getValue());
			dec.setInsurance(false);
			int prov = detail.getProvince().ordinal();
			if (prov == 0) {
				prov = 99;
			}
			dec.setProvince(prov);
			dec.setCountry(detail.getCountry()==null?null:detail.getCountry().getValue());
			if (Country.ES == detail.getCountry()) {
				dec.setCountry("  ");		
			} else {
				dec.setProvince(99);		
			}
			dec.setQuantity(detail.getAmount());
			dec.setQuantityQuarter1(detail.getFirstQuarterAmount());
			dec.setQuantityQuarter2(detail.getSecondQuarterAmount());
			dec.setQuantityQuarter3(detail.getThirdQuarterAmount());
			dec.setQuantityQuarter4(detail.getFourthQuarterAmount());
			dec.setRenting(false);
			deponent.getDeclareds().add(dec);
		}
	}

}
