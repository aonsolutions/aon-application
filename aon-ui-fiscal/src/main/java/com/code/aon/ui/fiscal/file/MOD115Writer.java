package com.code.aon.ui.fiscal.file;


import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.company.Company;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD115.MOD115;
import com.code.aon.file.tax.model.MOD115.MOD115Format;
import com.code.aon.file.tax.model.MOD115.data.Declaration;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.Mod115Key;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.fiscal.controller.FiscalParametersController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

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

	public FileOutput createMOD115(List<FiscalModel> fiscalModels,MOD115Format format) throws ManagerBeanException {
		List<Declaration> declarations = new LinkedList<Declaration>();
		for (FiscalModel fiscalModel : fiscalModels) {
			Declaration declaration = getDeclaration(fiscalModel);
			declarations.add(declaration);
		}
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(output);
		MOD115 mod115 = new MOD115();
		FileOutput fileOutput = new FileOutput();
		fileOutput.setErrors(mod115.create(declarations, format, writer));
		fileOutput.setContent(output.toByteArray());
		return fileOutput;
	}

	private Declaration getDeclaration(FiscalModel fiscalModel) throws ManagerBeanException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
		Declaration declaration = new  Declaration();
		declaration.setDocument(getCompany().getDocument());
		declaration.setPerson(getCompany().getRegistry().getType() == RegistryType.NATURAL);
		declaration.setStartPeriod(0);
		declaration.setEndPeriod(0);
		int year = fiscalModel.getYear();
		declaration.setYear(year); 
		declaration.setPeriod(fiscalModel.getPeriod().getName()); 
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		declaration.setCurrentDay(c.get(Calendar.DAY_OF_MONTH));
		declaration.setCurrentMonth(c.get(Calendar.MONTH) + 1);
		declaration.setCurrentLetterMonth( Month.getMonthByValue( c.get(Calendar.MONTH) ).getName( AonUtil.getCurrentLocale() ) );
		declaration.setCurrentYear(c.get(Calendar.YEAR)); 
		String startDate = formatter.format(fiscalModel.getPeriod().getStartDate(year));
		declaration.setStartPeriod(Integer.parseInt( startDate));
		String endDate = formatter.format(fiscalModel.getPeriod().getDueDate(year));
		declaration.setEndPeriod(Integer.parseInt( endDate));
		declaration.setName(getCompany().getName());
		RegistryMedia  phone = getCompany().getPhone();
		declaration.setTelephone(null);
		if (phone != null){
			declaration.setTelephone(phone.getValue() );
		}
		RegistryAddress address = getCompany().getDefaultAddress();
		declaration.setStreetType(address.getStreetType().getValue());
		declaration.setAddress( address.getAddress() );
		if (StringUtils.isNotEmpty( address.getNumber() )) {
			try {
				declaration.setAddressNumber( Integer.parseInt(address.getNumber())); 
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
		
		// TODO. Conseguir los datos del futuro vencimiento que tiene que ir vinculado a la declaración.
		declaration.setPayInCash("X");
		declaration.setPayInAccount(" ");
		declaration.setCcc1("");
		declaration.setCcc2("");
		declaration.setCcc3("");
		declaration.setCcc4("");
		// -------------------------------
		
		
		if (fiscalModel.getAdministration() == Administration.COMMON_TERRITORY) {
			FiscalParametersController fpc = (FiscalParametersController) AonUtil.getRegisteredBean( FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
			String administrationCode = fpc.getAdministrationCode();
			declaration.setAdministrationCode(administrationCode);
		}
		
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IEntityAlias.FISCAL_MODEL_DETAIL_FISCAL_MODEL_ID); 
		criteria.addEqualExpression(alias, fiscalModel.getId());
		criteria.setSkipDomainFilter(true);
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			FiscalModelDetail detail = (FiscalModelDetail) to;
			declaration.getBoxes().put(detail.getType(), detail.getAmount());
		}
		
		double d = 0.0; 
		if (fiscalModel.getAdministration() == Administration.ALAVA) {
			d = declaration.getBoxes().get(Mod115Key.C11.getValue());
		} else {
			d = declaration.getBoxes().get(Mod115Key.C08.getValue());
		}
		declaration.setDeclarationType("I");
		if (d < 0) {
			declaration.setDeclarationType("N");
		} else {
			if (StringUtils.isNotBlank(declaration.getCcc1())) {
				declaration.setDeclarationType("U");
			} 
		}
		return declaration;
	}

}
