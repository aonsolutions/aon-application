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
import com.code.aon.config.enumeration.Administration;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD123.Declaration;
import com.code.aon.file.tax.model.MOD123.MOD123;
import com.code.aon.file.tax.model.MOD123.MOD123Format;
import com.code.aon.finance.Finance;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.Mod123Key;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class MOD123Writer implements IFinanceConstants{
	
	public FileOutput createMOD123(List<FiscalModel> fiscalModels,MOD123Format format) throws ManagerBeanException {
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
		MOD123 mod123 = new MOD123();
		FileOutput fileOutput = new FileOutput();
		fileOutput.setErrors(mod123.create(declarations, format, writer));
		fileOutput.setContent(output.toByteArray());
		return fileOutput;
	}

	private Declaration getDeclaration(FiscalModel fiscalModel) throws ManagerBeanException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
		Declaration declaration = new  Declaration();
		declaration.setPerson((fiscalModel.getDocument().matches("[0-9|K|L|M|X|Y|Z].*")));
		declaration.setStartPeriod(0);
		declaration.setEndPeriod(0);
		int year = fiscalModel.getYear();
		declaration.setYear(year);
		Administration admon = fiscalModel.getAdministration();
		declaration.setPeriod(fiscalModel.getPeriod().getName(admon)); 
		declaration.setComplementary(fiscalModel.isComplementary());
		declaration.setReplacement( fiscalModel.isReplacement() );
		declaration.setReplacedNumber( fiscalModel.getReplacedNumber() );
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

		if (fiscalModel.getPeriod().isQuarterPeriod()) {
			declaration.setNavarraModel("716");
			declaration.setQuarter(fiscalModel.getPeriod().ordinal() - 11);
			declaration.setMonth((fiscalModel.getPeriod().ordinal() - 11) * 3);
		} else {
			declaration.setNavarraModel("746");
			declaration.setQuarter((fiscalModel.getPeriod().ordinal() + 1) % 3);
			declaration.setMonth(fiscalModel.getPeriod().ordinal() + 1);
		}
		
		declaration.setDocument(fiscalModel.getDocument());
		declaration.setName(fiscalModel.getName());
		declaration.setSurname(fiscalModel.getSurname());
		if (!declaration.isPerson() && fiscalModel.getAdministration() == Administration.COMMON_TERRITORY) {
			declaration.setSurname(fiscalModel.getName());
			declaration.setName(null);
		}
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

		if (fiscalModel.getYear() < 2015 && fiscalModel.getAdministration() == Administration.COMMON_TERRITORY) {
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
		
		double d = declaration.getBoxes().get(Mod123Key.C13.getValue()); 
		declaration.setPayMethod("0");
		declaration.setPayment("0");
		if (d <= 0) {
			declaration.setDeclarationType("N");
		} else {
			declaration.setDeclarationType("I");
			declaration.setCcc("");
			declaration.setIban("");
			Finance finance = fiscalModel.getFinance();
			if (finance != null) {
				if (finance.getPayMethod() != null) {
					if (finance.getPayMethod().getType() != PayMethodType.CASH_BASIS) {
						if (finance.getBankAccount() == null) {
							throw new ManagerBeanException("Si la forma de pago no es efectivo, el banco no puede estar vacio.");
						}
						declaration.setPayMethod("1");
						declaration.setPayment("3");
						declaration.setCcc(finance.getBankAccount().getBban());
						declaration.setIban(finance.getBankAccount().getIban());
						declaration.setDeclarationType("U");
					} else {
						declaration.setPayment("1");
					}
				}
			}
		}
		declaration.changeInvalidCharacters();
		return declaration;
	}

}
