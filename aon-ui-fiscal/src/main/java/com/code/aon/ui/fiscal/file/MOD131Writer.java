package com.code.aon.ui.fiscal.file;


import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD131.MOD131;
import com.code.aon.file.tax.model.MOD131.MOD131Format;
import com.code.aon.file.tax.model.MOD131.data.Declaration;
import com.code.aon.finance.Finance;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.Mod131Key;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.fiscal.controller.FiscalParametersController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class MOD131Writer implements IFinanceConstants{
	
	private Company getCompany(int domain) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Company.class);
		Criteria c  = new Criteria();
		c.addEqualExpression("Company.domain", domain);
		c.setSkipDomainFilter(true);
		List<ITransferObject> list = bean.getList(c);
		if (list != null && list.size() > 0 ){
			return (Company) list.get(0);
		}
		throw new ManagerBeanException("No puedo encontrar 'Company' para el dominio " + domain);
	}

	public FileOutput createMOD131(List<FiscalModel> fiscalModels,MOD131Format format) throws ManagerBeanException {
		List<Declaration> declarations = new LinkedList<Declaration>();
		for (FiscalModel fiscalModel : fiscalModels) {
			Declaration declaration = getDeclaration(fiscalModel);
			declarations.add(declaration);
		}
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		OutputStreamWriter wr = null;
		try {
			wr = new OutputStreamWriter(output,"ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			wr = new OutputStreamWriter(output);
		}
		PrintWriter writer = new PrintWriter(wr);
//		PrintWriter writer = new PrintWriter(output);
		MOD131 mod131 = new MOD131();
		FileOutput fileOutput = new FileOutput();
		fileOutput.setErrors(mod131.create(declarations, format, writer));
		fileOutput.setContent(output.toByteArray());
		return fileOutput;
	}

	private Declaration getDeclaration(FiscalModel fiscalModel) throws ManagerBeanException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
		Declaration declaration = new  Declaration();
		Company company = getCompany(fiscalModel.getDomain());
		declaration.setDocument(company.getDocument());
		declaration.setPerson(company.getRegistry().getType() == RegistryType.NATURAL 
					|| company.getDocumentType() != DocumentType.CIF);
		declaration.setStartPeriod(0);
		declaration.setEndPeriod(0);
		int year = fiscalModel.getYear();
		declaration.setYear(year); 
		Administration admon = fiscalModel.getAdministration();
		declaration.setPeriod(fiscalModel.getPeriod().getName(admon)); 
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
		declaration.setName(company.getName());
		RegistryMedia  phone = company.getPhone();
		declaration.setTelephone(null);
		if (phone != null){
			declaration.setTelephone(phone.getValue() );
		}
		RegistryAddress address = company.getDefaultAddress();
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
		
		declaration.setToDeduct("");
		
		FiscalParametersController fpc = (FiscalParametersController) AonUtil.getRegisteredBean( FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
		declaration.setContactPerson( fpc.getContactPerson() );
		declaration.setContactPhone(fpc.getContactPhone() );

		if (fiscalModel.getAdministration() == Administration.COMMON_TERRITORY) {
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
			if (Mod131Key.AC11.getValue().equals(detail.getType())) {
				declaration.setEpi1(getEpigrafe(detail.getDescription()));
			} else if (Mod131Key.AC21.getValue().equals(detail.getType())) {
				declaration.setEpi2(getEpigrafe(detail.getDescription()));
			} else if (Mod131Key.AC31.getValue().equals(detail.getType())) {
				declaration.setEpi3(getEpigrafe(detail.getDescription()));
			} else if (Mod131Key.AC41.getValue().equals(detail.getType())) {
				declaration.setEpi4(getEpigrafe(detail.getDescription()));
			} else if (Mod131Key.AC51.getValue().equals(detail.getType())) {
				declaration.setEpi5(getEpigrafe(detail.getDescription()));
			} else {
				declaration.getBoxes().put(detail.getType(), detail.getAmount());
			}
		}
		
		double d = declaration.getBoxes().get(Mod131Key.C15.getValue()); 
		declaration.setDeclarationType("I");
		declaration.setPayment("0");
		if (d < 0) {
			if (fiscalModel.getPeriod() == Period.T4) {
				declaration.setDeclarationType("N");
			} else {
				declaration.setDeclarationType("B");
				declaration.setToDeduct("X");
			}
		} else {
			declaration.setCcc1("");
			declaration.setCcc2("");
			declaration.setCcc3("");
			declaration.setCcc4("");
			Finance finance = fiscalModel.getFinance();
			if (finance != null) {
				if (finance.getPayMethod() != null) {
					if (finance.getPayMethod().getType() != PayMethodType.CASH_BASIS) {
						if (finance.getBankAccount() == null) {
							throw new ManagerBeanException("Si la forma de pago no es efectivo, el banco no puede estar vacio.");
						}
						declaration.setPayment("2");
						declaration.setCcc1(finance.getBankAccount().getEntity());
						declaration.setCcc2(finance.getBankAccount().getOffice());
						declaration.setCcc3(finance.getBankAccount().getControl());
						declaration.setCcc4(finance.getBankAccount().getAccount());
					} else {
						declaration.setPayment("1");
					}
				}
			}
			declaration.setDeposit(d);			
			if (StringUtils.isNotBlank(declaration.getCcc1())) {
				declaration.setDeclarationType("U");
			} 
		}
		return declaration;
	}
	private String getEpigrafe(String description) {
		String epi = null;
		if (StringUtils.isNotBlank(description)) {
			epi = StringUtils.substringBefore(description, " - ");
			epi = StringUtils.replace(epi, ".", "");
		}
		if (StringUtils.isBlank(epi)) {
			epi = "0";
		}
		return epi;
	}

}
