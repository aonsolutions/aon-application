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
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD111.MOD111;
import com.code.aon.file.tax.model.MOD111.MOD111Format;
import com.code.aon.file.tax.model.MOD111.data.Declaration;
import com.code.aon.finance.Finance;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.Mod111Key;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.fiscal.controller.FiscalParametersController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class MOD111Writer implements IFinanceConstants{
	
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

	public FileOutput createMOD111(List<FiscalModel> fiscalModels,MOD111Format format) throws ManagerBeanException {
		List<Declaration> declarations = new LinkedList<Declaration>();
		for (FiscalModel fiscalModel : fiscalModels) {
			Declaration declaration = getDeclaration(fiscalModel);
			declarations.add(declaration);
		}
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(output);
		MOD111 mod111 = new MOD111();
		FileOutput fileOutput = new FileOutput();
		fileOutput.setErrors(mod111.create(declarations, format, writer));
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
		Administration admon = fiscalModel.getAdministration();
		int year = fiscalModel.getYear();
		declaration.setYear(year); 
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
		
		declaration.setPayInCash("X");
		declaration.setPayInAccount(" ");
		declaration.setCcc1("");
		declaration.setCcc2("");
		declaration.setCcc3("");
		declaration.setCcc4("");
		Finance finance = fiscalModel.getFinance();
		if (finance != null) {
			if (finance.getPayMethod() != null) {
				if (finance.getPayMethod().getType() != PayMethodType.CASH_BASIS) {
					declaration.setPayInCash(" ");
					declaration.setPayInAccount("X");
					if (finance.getBankAccount() == null) {
						throw new ManagerBeanException("Si la forma de pago no es efectivo, el banco no puede estar vacio.");
					}
					declaration.setCcc1(finance.getBankAccount().getEntity());
					declaration.setCcc2(finance.getBankAccount().getOffice());
					declaration.setCcc3(finance.getBankAccount().getControl());
					declaration.setCcc4(finance.getBankAccount().getAccount());
				}
			}
		}
		
		FiscalParametersController fpc = (FiscalParametersController) AonUtil.getRegisteredBean( FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
		declaration.setContactPerson( fpc.getContactPerson() );
		declaration.setContactPhone(fpc.getContactPhone() );
		declaration.setContactCellular( fpc.getContactCellular() );
		declaration.setContactMail( fpc.getContactMail() );
		
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
			declaration.getBoxes().put(detail.getType(), detail.getAmount());
		}
		
		double d = 0.0; 
		if (fiscalModel.getAdministration() == Administration.ALAVA) {
			d = declaration.getBoxes().get(Mod111Key.AR_C31.getValue());
		} else if (fiscalModel.getAdministration() == Administration.BIZKAIA) {
			d = declaration.getBoxes().get(Mod111Key.BZ_C37.getValue());
		} else if (fiscalModel.getAdministration() == Administration.GIPUZKOA) {
			d = declaration.getBoxes().get(Mod111Key.GP_C25.getValue());
		} else {
			d = declaration.getBoxes().get(Mod111Key.CT_C30.getValue());
		}
		declaration.setDeclarationType("I");
		if (d <= 0) {
			declaration.setDeclarationType("N");
		} else {
			if (StringUtils.isNotBlank(declaration.getCcc1())) {
				declaration.setDeclarationType("U");
			} 
		}
		return declaration;
	}

}
