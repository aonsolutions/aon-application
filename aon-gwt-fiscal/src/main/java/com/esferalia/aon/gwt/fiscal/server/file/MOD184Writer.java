package com.esferalia.aon.gwt.fiscal.server.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD184.Deponent;
import com.code.aon.file.tax.model.MOD184.Income;
import com.code.aon.file.tax.model.MOD184.MOD184;
import com.code.aon.file.tax.model.MOD184.MOD184Format;
import com.code.aon.file.tax.model.MOD184.Partner;
import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Income;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Partner;
import com.esferalia.aon.watson.util.AonStringUtils;

public class MOD184Writer {
	private static DateFormat DATE_FORMAT  = new SimpleDateFormat("yyyyMMdd");
	
	public FileOutput createMOD184(String domainName, int domainId, Integer mod184, int year,
			Integer administration) throws AonSQLException {
		try {
			MOD184Format format = MOD184Format.obtainFormat(year, administration);
			if (format == null) {
				throw new AonSQLException(
						"No existe soporte para el formato de la declaraci\u00F3n "
								+ "184 del ejercicio " + year
								+ " en la administraci\u00F3n "
								+ administration);
			}
			Deponent deponent = getDeponent(domainName,domainId, mod184, format);
			
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			FileFiller filler = new MOD184(deponent, format, writer);
			FileOutput fileOutput = new FileOutput();
			fileOutput.setErrors(filler.create());
			if (fileOutput.getErrors() != null && fileOutput.getErrors().size() > 0) {
				StringBuilder sw = new StringBuilder();
				sw.append("Se han producido errores durante la generación del fichero.");
				for (Exception e : fileOutput.getErrors()) {
					sw.append("[")
					.append(e.getMessage())
					.append("]");
				}
				throw new AonSQLException(sw.toString());
			}
			fileOutput.setContent(output.toByteArray());
			return fileOutput;
		} catch (IOException e) {
			throw new AonSQLException(e.getMessage());
		}
	}

	private Deponent getDeponent(String domainName, int domainId, Integer id,
			MOD184Format format) throws AonSQLException {
		Mod184 mod184 = AON.getMod184(domainName,domainId, id);
		Deponent deponent = new Deponent();
		deponent.setYear(mod184.getYear());
		deponent.setDocument(mod184.getDocument());
		deponent.setName(mod184.getName());
		deponent.setContactPhone(AonUtil.isEmpty(mod184.getContactPhone()) ? "0"
				: mod184.getContactPhone());
		deponent.setContactPerson(mod184.getContactPerson());
		deponent.setComplementary("");
		deponent.setReplacement(mod184.isReplacement()?"S":"");
		deponent.setReceipt( (AonUtil.isEmpty(mod184.getReceipt()))?"1840000000001":mod184.getReceipt() );
		deponent.setReplacedReceipt( (AonUtil.isEmpty(mod184.getReplacedReceipt()))?"0":mod184.getReplacedReceipt());
		deponent.setPartnerTotal(mod184.getPartnerTotal());
		
		if (AonStringUtils.isEmpty(mod184.getEntityType())) {
			deponent.setEntityType("0");
		} else {
			deponent.setEntityType(mod184.getEntityType());
		}
		if (AonStringUtils.isEmpty(mod184.getMainActivity())) {
			deponent.setMainActivity("0");
		} else {
			deponent.setMainActivity(mod184.getMainActivity());
		}
		if (AonStringUtils.isEmpty(mod184.getForeignEntityType())) {
			deponent.setForeignEntityType("0");
		} else {
			deponent.setForeignEntityType(mod184.getForeignEntityType());
		}
		deponent.setForeignObject(mod184.getForeignObject());
		deponent.setCountry(mod184.getCountry());
		deponent.setResidentPercent(mod184.getResidentPercent());
		deponent.setTaxIS(mod184.isTaxIS()?"S":"");
		deponent.setNetSalesAmount(mod184.getNetSalesAmount());
		deponent.setLrDocument(mod184.getLrDocument());
		deponent.setLrName(mod184.getLrName());
		fillIncomes(mod184, deponent);
		fillPartners(mod184, deponent);
		return deponent;
	}

	private void fillIncomes(Mod184 mod184, Deponent deponent) throws AonSQLException {
		for (Mod184Income inc : mod184.getIncomes()) {
			Income income = new Income();
			
			income.setKey(inc.getKey());
			income.setSubKey(inc.getSubKey());
			income.setCountry(inc.getCountry());
			income.setRegime( Byte.toString( inc.getRegime()) );	
			income.setActivityType( Byte.toString( inc.getActivityType()) );	
			income.setEpigraph(inc.getEpigraph());
			income.setGranteeDocument(inc.getGranteeDocument());
			income.setGranteeName(inc.getGranteeName());
			if (inc.getAdqDate() != null) {
				income.setAdqDate( Integer.parseInt( DATE_FORMAT.format(inc.getAdqDate()) ));	
			} else {
				income.setAdqDate(0);	
			}
			income.setIncrease(inc.getIncrease());
			income.setDecrease(inc.getDecrease());
			income.setAccountingResult(inc.getAccountingResult());
			income.setExpenses(inc.getExpenses());
			income.setNetYield(inc.getNetYield());
			income.setReductionPercent(inc.getReductionPercent());
			income.setDeductionRightRent(inc.getDeductionRightRent());
			income.setResult(inc.getResult());
			income.setDeductionBase(inc.getDeductionBase());
			income.setRetention(inc.getRetention());
			
			deponent.getIncomes().add(income);
		}
	}

	private void fillPartners(Mod184 mod184, Deponent deponent) throws AonSQLException {
		for (Mod184Partner par : mod184.getPartners()) {
			Partner partner = new Partner();
			
			partner.setDocument(par.getDocument()); 
			partner.setRepresentativeDocument(par.getRepresentativeDocument()); 
			partner.setName(par.getName());
			partner.setProvince(par.getProvince()); 
			partner.setCountry(par.getCountry());
			partner.setPartType(Byte.toString( par.getPartType())); 
			partner.setMemberEndOfYear(par.isMemberEndOfYear()?"X":""); 
			partner.setMemberDays(par.getMemberDays());
			partner.setPartPercent(par.getPartPercent());
			partner.setKey(par.getKey());
			partner.setSubKey(par.getSubKey()); 
			partner.setAmount(par.getAmount());
			partner.setReduction(par.getReduction());
			partner.setAddress(par.getAddress());
			
			deponent.getPartners().add(partner);
		}
		deponent.setPartnerTotal(deponent.getPartners().size());
	}
}
