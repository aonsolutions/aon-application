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
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD311.Declaration;
import com.code.aon.file.tax.model.MOD311.MOD311;
import com.code.aon.file.tax.model.MOD311.MOD311Format;
import com.code.aon.finance.Finance;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.Mod311Key;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class MOD311Writer implements IFinanceConstants{
	
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

	public FileOutput createMOD311(List<FiscalModel> fiscalModels,MOD311Format format) throws ManagerBeanException {
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
		MOD311 mod311 = new MOD311();
		FileOutput fileOutput = new FileOutput();
		fileOutput.setErrors(mod311.create(declarations, format, writer));
		fileOutput.setContent(output.toByteArray());
		return fileOutput;
	}

	private Declaration getDeclaration(FiscalModel fiscalModel) throws ManagerBeanException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
		Declaration declaration = new  Declaration();
		Company company = getCompany(fiscalModel.getDomain());
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
		declaration.setDocument(fiscalModel.getDocument());
		
		declaration.setName(fiscalModel.getName());
		declaration.setSurname(fiscalModel.getSurname());
		declaration.setPhone( fiscalModel.getPhone() );
		declaration.setStreetInitial(fiscalModel.getStreetInitial());
		declaration.setStreetName( fiscalModel.getStreetName() );
		declaration.setStreetNumber(fiscalModel.getStreetNumber());
		declaration.setStreetStair(fiscalModel.getStreetStair());
		declaration.setStreetFloor(fiscalModel.getStreetFloor());
		declaration.setStreetDoor(fiscalModel.getStreetDoor());
		declaration.setTown(fiscalModel.getTown());
		declaration.setProvince(fiscalModel.getProvince());
		declaration.setZip(fiscalModel.getZip());
		
		declaration.setContactPerson( fiscalModel.getContactPerson() );
		declaration.setContactPhone(fiscalModel.getContactPhone() );
		declaration.setContactCellular( fiscalModel.getContactCellular() );
		declaration.setContactMail( fiscalModel.getContactEmail() );

		if (fiscalModel.getAdministration() == Administration.COMMON_TERRITORY) {
			declaration.setAdministrationCode(fiscalModel.getAdmonAeat());
			if (StringUtils.isEmpty(declaration.getAdministrationCode())) {
				throw new ManagerBeanException("No se ha indicado el Código de Administración.");
			}
		}
		
		declaration.setToDeduct("");
		
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IEntityAlias.FISCAL_MODEL_DETAIL_FISCAL_MODEL_ID); 
		criteria.addEqualExpression(alias, fiscalModel.getId());
		criteria.setSkipDomainFilter(true);
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			FiscalModelDetail detail = (FiscalModelDetail) to;
			if (Mod311Key.CAC1.getValue().equals(detail.getType())) {
				declaration.setEpi1(getEpigrafe(detail.getDescription()));
			} else if (Mod311Key.CAC2.getValue().equals(detail.getType())) {
				declaration.setEpi2(getEpigrafe(detail.getDescription()));
			} else if (Mod311Key.CAC3.getValue().equals(detail.getType())) {
				declaration.setEpi3(getEpigrafe(detail.getDescription()));
			} else if (Mod311Key.CAC4.getValue().equals(detail.getType())) {
				declaration.setEpi4(getEpigrafe(detail.getDescription()));
			} else if (Mod311Key.CAC5.getValue().equals(detail.getType())) {
				declaration.setEpi5(getEpigrafe(detail.getDescription()));
			} else if (Mod311Key.CAG1.getValue().equals(detail.getType())) {
				declaration.setAgri1(detail.getDescription());
			} else if (Mod311Key.CAG2.getValue().equals(detail.getType())) {
				declaration.setAgri2(detail.getDescription());
			} else if (Mod311Key.CAG3.getValue().equals(detail.getType())) {
				declaration.setAgri3(detail.getDescription());
			} else if (Mod311Key.CAG4.getValue().equals(detail.getType())) {
				declaration.setAgri4(detail.getDescription());
			}
			declaration.getBoxes().put(detail.getType(), detail.getAmount());
		}
		
		double d = declaration.getBoxes().get(Mod311Key.C16.getValue()); 
		double p = declaration.getBoxes().get(Mod311Key.PBK.getValue());
		declaration.setPayment("0");
		if (d == 0) {
			declaration.setDeclarationType("N");
		} else if (d < 0 && p == 0) {
			declaration.setDeclarationType("C");
			declaration.setCompensate( CommonUtil.round(d* (-1)));
		} else if (d < 0 && p != 0) {
			declaration.setDeclarationType("D");
			declaration.setPayBack( CommonUtil.round(d* (-1)));
			Finance finance = fiscalModel.getFinance();
			if (finance != null) {
				if (finance.getPayMethod() != null) {
					if (finance.getBankAccount() == null) {
						throw new ManagerBeanException("Si la declaración es una devolución, el banco no puede estar vacio.");
					}
					declaration.setPayBackCCC(finance.getBankAccount().getBban());
				}
			}
		} else {
			declaration.setDeclarationType("I");
			declaration.setCcc("");
			Finance finance = fiscalModel.getFinance();
			if (finance != null) {
				if (finance.getPayMethod() != null) {
					if (finance.getPayMethod().getType() != PayMethodType.CASH_BASIS) {
						if (finance.getBankAccount() == null) {
							throw new ManagerBeanException("Si la forma de pago no es efectivo, el banco no puede estar vacio.");
						}
						declaration.setPayment("3");
						declaration.setCcc(finance.getBankAccount().getBban());
						declaration.setDeclarationType("U");
					} else {
						declaration.setPayment("1");
					}
				}
			}
			declaration.setDeposit(d);			
		}
		declaration.changeInvalidCharacters();
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
