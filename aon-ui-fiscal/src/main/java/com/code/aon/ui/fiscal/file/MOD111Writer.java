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
import com.code.aon.file.tax.model.MOD111.Declaration;
import com.code.aon.file.tax.model.MOD111.MOD111;
import com.code.aon.file.tax.model.MOD111.MOD111Format;
import com.code.aon.finance.Finance;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.Mod111Key;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.watson.util.AonStringUtils;

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
		boolean moreThan2014 = false;
		for (FiscalModel fiscalModel : fiscalModels) {
			Declaration declaration = getDeclaration(fiscalModel);
			moreThan2014 = moreThan2014 || (declaration.getYear() > 2014); 
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
		MOD111 mod111 = new MOD111();
		FileOutput fileOutput = new FileOutput();
		fileOutput.setErrors(mod111.create(declarations, format, writer));
		if (moreThan2014) {
			String fileString = null;
			try {
				fileString = new String(output.toByteArray(),"ISO-8859-1");
				fileString = AonStringUtils.chomp(fileString);
				fileOutput.setContent(fileString.getBytes("ISO-8859-1"));
			} catch (UnsupportedEncodingException e) {
				fileString = new String(output.toByteArray());
				fileString = AonStringUtils.chomp(fileString);
				fileOutput.setContent(fileString.getBytes());
			}
		} else {
			fileOutput.setContent(output.toByteArray());
		}
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
		Administration admon = fiscalModel.getAdministration();
		
		if (fiscalModel.getPeriod().isQuarterPeriod()) {
			declaration.setNavarraModel("715");
			declaration.setQuarter(fiscalModel.getPeriod().ordinal() - 11);
			declaration.setMonth((fiscalModel.getPeriod().ordinal() - 11) * 3);
		} else {
			declaration.setNavarraModel("745");
			declaration.setQuarter((fiscalModel.getPeriod().ordinal() + 1) % 3);
			declaration.setMonth(fiscalModel.getPeriod().ordinal() + 1);
		}

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
		
		declaration.setPayInCash("X");
		declaration.setPayInAccount(" ");
		declaration.setCcc("");
		declaration.setIban("");
		declaration.setPayMethod("0");
		Finance finance = fiscalModel.getFinance();
		
		if (finance != null) {
			PayMethodType type = null;
			if (finance.getPayMethod() != null) {
				type = finance.getPayMethod().getType();	
			} else {
				if (finance.getBankAccount() != null && AonStringUtils.isNotBlank( finance.getBankAccount().getCCC())) {
					type = PayMethodType.NEGOTIABLE_DOCUMENT;	
				} else {
					type = PayMethodType.CASH_BASIS;
				}
			}
			if (type != PayMethodType.CASH_BASIS) {
				declaration.setPayInCash(" ");
				declaration.setPayInAccount("X");
				if (finance.getBankAccount() == null) {
					throw new ManagerBeanException("Si la forma de pago no es efectivo, el banco no puede estar vacio.");
				}
				declaration.setCcc(finance.getBankAccount().getBban());
				declaration.setIban(finance.getBankAccount().getIban());
			}
		}
		
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
		} else if (fiscalModel.getAdministration() == Administration.NAVARRA) {
			d = declaration.getBoxes().get(Mod111Key.NF_A1.getValue());
		} else {
			d = declaration.getBoxes().get(Mod111Key.CT_C30.getValue());
		}
		declaration.setDeclarationType("I");
		if (d <= 0) {
			declaration.setPayMethod("0");
			declaration.setDeclarationType("N");
			declaration.setIban("");
		} else {
			if (StringUtils.isNotBlank(declaration.getCcc())) {
				declaration.setDeclarationType("U");
				declaration.setPayMethod("1");
			} else {
				declaration.setIban("");	
			}
		}
		declaration.setResult(d);
		declaration.changeInvalidCharacters();
		return declaration;
	}

}
