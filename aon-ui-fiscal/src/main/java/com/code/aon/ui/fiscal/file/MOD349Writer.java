package com.code.aon.ui.fiscal.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.company.Company;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD349.MOD349;
import com.code.aon.file.tax.model.MOD349.MOD349Format;
import com.code.aon.file.tax.model.MOD349.data.Deponent;
import com.code.aon.file.tax.model.MOD349.data.Operator;
import com.code.aon.fiscal.Mod349;
import com.code.aon.fiscal.Mod349Detail;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.fiscal.controller.FiscalParametersController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class MOD349Writer implements IFinanceConstants{
	
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

	public FileOutput createMOD349(Mod349 mod349) throws ManagerBeanException {
		try {
			MOD349Format format = obtainFormat(mod349);
			if (format == null) {
				throw new ManagerBeanException("No existe soporte para el formato de la declaración " +
						"349 del ejercicio " + mod349.getYear() + " en la administración " + 
						mod349.getAdministration());	
			}
			Deponent deponent = getDeponent(mod349,format);
			File file = File.createTempFile("MOD349_", ".txt");
			FileFiller mod349Filler = new MOD349(deponent, format, file.getAbsolutePath());
			FileOutput output = new FileOutput();
			output.setFile(file);
			output.setErrors(mod349Filler.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}

	private MOD349Format obtainFormat(Mod349 mod349) {
		int year = mod349.getYear();
		Administration adm = mod349.getAdministration();
		MOD349Format f = null;
		for (MOD349Format format : MOD349Format.values() ) {
			if (format.getAdministration() == adm && year >= format.getYear()) {
				if (f == null || f.getYear() < format.getYear() ) {
					f = format;	
				}
			}
		}
		return f;
	}
	
	private Deponent getDeponent(Mod349 mod349, MOD349Format format)
			throws ManagerBeanException {
		FiscalParametersController fiscalParams = (FiscalParametersController) AonUtil
				.getRegisteredBean(FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);

		Deponent deponent = new Deponent();
		deponent.setDocument(getCompany().getDocument());
		deponent.setComplementary(mod349.isComplementary());
		deponent.setReplacement(mod349.isReplacement());
		String p = mod349.getPeriod().getName();
		if ("T1".equals(p)) {
			deponent.setPeriod("1T");
		} else if ("T2".equals(p)) {
			deponent.setPeriod("2T");
		} else if ("T3".equals(p)) {
			deponent.setPeriod("3T");
		} else if ("T4".equals(p)) {
			deponent.setPeriod("4T");
		} else if ("An".equals(p)) {
			deponent.setPeriod("0A");
		} else {
			deponent.setPeriod(p);
		}
		long a = 3490000000000L + mod349.getNumber();
		deponent.setNumber(a);
		deponent.setName(getCompany().getName());
		RegistryAddress address = getCompany().getDefaultAddress();
		if (address != null) {
			GeoZone geozone = address.getGeozone();
			if (geozone != null) {
				deponent.setProvince(geozone.getCode());
			}
		}
		String relName = fiscalParams.getContactPerson();
		String relPhone = fiscalParams.getContactPhone();
		
		deponent.setRelName(StringUtils.isEmpty(relName)?getCompany().getName():relName);
		if (StringUtils.isEmpty(relPhone)) {
			RegistryMedia phone = getCompany().getPhone();
			relPhone = phone.getValue();
		}
		if (relPhone != null) {
			try {
				deponent.setRelPhone(Integer.parseInt( relPhone ));
			} catch (NumberFormatException e) {
				// Nothing
			}
		}
		
		if (mod349.getReplacedNumber() != null) {
			long b = 3490000000000L + mod349.getReplacedNumber();
			deponent.setReplacedNumber(b);
		}
		deponent.setType("T");
		deponent.setYear(mod349.getYear());
		fillOperator(deponent, mod349, format);
		return deponent;
	}
	
	private void fillOperator(Deponent deponent, Mod349 mod349, MOD349Format format)  throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Mod349Detail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.MOD349DETAIL_MOD349_ID), mod349.getId());
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to: list) {
			Mod349Detail detail = (Mod349Detail) to;	
			Operator op = new Operator();
			op.setDocument(detail.getDocument()==null?null:StringUtils.trim(detail.getDocument()));
			op.setName(detail.getName());
			op.setKey(detail.getType().getValue());
			String c = null;
			if (detail.getCountry()!=null) {
				c= detail.getCountry() == Country.GR ? "EL" : detail.getCountry().getValue();
			}
			op.setCountry(c);
			op.setAmount(detail.getAmount());
			deponent.getOperators().add(op);
		}
	}

}
