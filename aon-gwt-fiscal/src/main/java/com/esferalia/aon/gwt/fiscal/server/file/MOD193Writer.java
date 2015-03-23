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
import com.code.aon.file.tax.model.MOD193.Deponent;
import com.code.aon.file.tax.model.MOD193.Expenses;
import com.code.aon.file.tax.model.MOD193.MOD193;
import com.code.aon.file.tax.model.MOD193.MOD193Format;
import com.code.aon.file.tax.model.MOD193.Receiver;
import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.watson.util.AonMathUtils;

public class MOD193Writer {
	
	private static DateFormat DATE_FORMAT  = new SimpleDateFormat("yyyyMMdd");  

	public FileOutput createMOD193(String domainName,Integer domainId, Integer mod193, int year,
			Byte administration) throws AonSQLException {
		try {
			MOD193Format format = MOD193Format.obtainFormat(year, administration);
			if (format == null) {
				throw new AonSQLException(
						"No existe soporte para el formato de la declaraci\u00F3n "
								+ "193 del ejercicio " + year
								+ " en la administraci\u00F3n "
								+ administration);
			}
			Deponent deponent = getDeponent(domainName,domainId, mod193, format);
			
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			FileFiller filler = new MOD193(deponent, format, writer);
			FileOutput fileOutput = new FileOutput();
			fileOutput.setErrors(filler.create());
			fileOutput.setContent(output.toByteArray());
			return fileOutput;
		} catch (IOException e) {
			throw new AonSQLException(e.getMessage());
		}
	}

//	private MOD193Format obtainFormat(int year, int administration) {
//		Administration adm = Administration.values()[administration];
//		MOD193Format f = null;
//		for (MOD193Format format : MOD193Format.values()) {
//			if (format.getAdministration() == adm && year >= format.getYear()) {
//				if (f == null || f.getYear() < format.getYear()) {
//					f = format;
//				}
//			}
//		}
//		return f;
//	}

	private Deponent getDeponent(String domainName, int domainId, Integer id,
			MOD193Format format) throws AonSQLException {
		Mod193 mod193 = AON.getMod193(domainName,domainId, id);
		Deponent deponent = new Deponent();
		deponent.setYear(mod193.getYear());
		deponent.setDocument(mod193.getDocument());
		deponent.setName(mod193.getName());
		deponent.setContactPhone(AonUtil.isEmpty(mod193.getContactPhone()) ? "0"
				: mod193.getContactPhone());
		deponent.setContactPerson(mod193.getContactPerson());
		deponent.setC001(0);
		deponent.setC002(0);
		deponent.setC003(0);
		deponent.setComplementary("");
		deponent.setReplacement(mod193.isReplacement()?"S":"");
		// TODO Soporte al número de justificante
		deponent.setReceipt( (AonUtil.isEmpty(mod193.getReceipt()))?"0":mod193.getReceipt() );
		deponent.setReplacedReceipt( (AonUtil.isEmpty(mod193.getReplacedReceipt()))?"0":mod193.getReplacedReceipt());
		fillReceivers(mod193, deponent, format);
		fillExpenses(mod193, deponent, format);
		return deponent;
	}

	private void fillReceivers(Mod193 mod193, Deponent deponent,
			 MOD193Format format) throws AonSQLException {
		for (Mod193Detail det : mod193.getDetails()) {
			Receiver receiver = new Receiver();
			receiver.setDocument(det.getDocument());
			receiver.setName(det.getName());
			receiver.setRepresentativeDocument(det.getRepresentativeDocument());
			receiver.setProvince(det.getProvince());
			receiver.setKey(det.getKey());
			receiver.setNature(det.getNature());
			receiver.setPending( det.isPending()?"X":"");
			receiver.setIntermediaryPayment( det.isIntermediaryPayment()?"X":"");
			receiver.setKeyCode( det.getKeyCode()==0?"": Byte.toString( det.getKeyCode() ));
			receiver.setIssuingCode( det.getIssuingCode());
			receiver.setPayment( det.getPayment()==0?"": Byte.toString( det.getPayment() ));
			receiver.setCodeType( det.getCodeType());
			receiver.setLenderAmount( det.getLenderAmount());
			receiver.setAccountCode( det.getAccountCode());
			receiver.setAccrualYear( det.getAccrualYear());
			receiver.setInKind( det.isInKind()?"2":"1");
			receiver.setReduction( det.getReduction());
			receiver.setRetentionBase( det.getRetentionBase());
			receiver.setPercent( det.getPercent());
			receiver.setRetention( det.getRetention());
			if (det.getLoanStartDate() != null) {
				receiver.setLoanStartDate( Integer.parseInt( DATE_FORMAT.format(det.getLoanStartDate()) ));	
			} 
			if (det.getLoanDueDate() != null) {
				receiver.setLoanDueDate( Integer.parseInt( DATE_FORMAT.format(det.getLoanDueDate()) ));	
			} 
			receiver.setCompensation( det.getCompensation());
			receiver.setGuarantee( det.getGuarantee());
			deponent.getReceivers().add(receiver);
			deponent.setC001( deponent.getC001( ) + 1);
			deponent.setC002( AonMathUtils.round(deponent.getC002() + det.getRetentionBase()) ); 
			deponent.setC003( AonMathUtils.round(deponent.getC003() + det.getRetention()) );
			if ( "C".equals(det.getKey()) || det.getPayment() == 1 || det.getPayment() == 3) {
				deponent.setC004( AonMathUtils.round(deponent.getC004()  + det.getRetention()));
			}
		}
	}

	private void fillExpenses(Mod193 mod193, Deponent deponent,
			 MOD193Format format) throws AonSQLException {
		for (Mod193Detail det : mod193.getExpenses()) {
			Expenses expense = new Expenses();
			expense.setDocument(det.getDocument());
			expense.setName(det.getName());
			expense.setRepresentativeDocument(det.getRepresentativeDocument());
			deponent.getExpenses().add(expense);
			deponent.setC001( deponent.getC001( ) + 1);
			deponent.setC005( AonMathUtils.round(deponent.getC005() + det.getExpenses()) );
		}
	}
}
