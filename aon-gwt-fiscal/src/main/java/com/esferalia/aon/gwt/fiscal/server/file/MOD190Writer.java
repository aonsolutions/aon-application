package com.esferalia.aon.gwt.fiscal.server.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD190.Deponent;
import com.code.aon.file.tax.model.MOD190.MOD190;
import com.code.aon.file.tax.model.MOD190.MOD190Format;
import com.code.aon.file.tax.model.MOD190.Receiver;
import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;

public class MOD190Writer {

	public FileOutput createMOD190(String domainName,Integer domainId, Integer mod190, int year,
			Byte administration) throws AonSQLException {
		try {
			MOD190Format format = obtainFormat(year, administration);
			if (format == null) {
				throw new AonSQLException(
						"No existe soporte para el formato de la declaraci\u00F3n "
								+ "190 del ejercicio " + year
								+ " en la administraci\u00F3n "
								+ administration);
			}
			Deponent deponent = getDeponent(domainName,domainId, mod190, format);
			
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			FileFiller filler = new MOD190(deponent, format, writer);
			FileOutput fileOutput = new FileOutput();
			fileOutput.setErrors(filler.create());
			fileOutput.setContent(output.toByteArray());
			return fileOutput;
		} catch (IOException e) {
			throw new AonSQLException(e.getMessage());
		}
	}

	private MOD190Format obtainFormat(int year, int administration) {
		Administration adm = Administration.values()[administration];
		MOD190Format f = null;
		for (MOD190Format format : MOD190Format.values()) {
			if (format.getAdministration() == adm && year >= format.getYear()) {
				if (f == null || f.getYear() < format.getYear()) {
					f = format;
				}
			}
		}
		return f;
	}

	private Deponent getDeponent(String domainName, int domainId, Integer id,
			MOD190Format format) throws AonSQLException {
		Mod190 mod190 = AON.getMod190(domainName,domainId, id);
		Deponent deponent = new Deponent();
		deponent.setYear(mod190.getYear());
		deponent.setDocument(mod190.getDocument());
		deponent.setName(mod190.getName());
		deponent.setContactPhone(AonUtil.isEmpty(mod190.getContactPhone()) ? "0"
				: mod190.getContactPhone());
		deponent.setContactPerson(mod190.getContactPerson());
		deponent.setC001(0);
		deponent.setC002(0);
		deponent.setC003(0);
		deponent.setComplementary("");
		deponent.setReplacement(mod190.isReplacement()?"S":"");
		// TODO Soporte al número de justificante
		deponent.setReceipt( (AonUtil.isEmpty(mod190.getReceipt()))?"0":mod190.getReceipt() );
		deponent.setReplacedReceipt( (AonUtil.isEmpty(mod190.getReplacedReceipt()))?"0":mod190.getReplacedReceipt());
		fillReceivers(mod190, deponent, format);
		
		return deponent;
	}

	private void fillReceivers(Mod190 mod190, Deponent deponent,
			 MOD190Format format) throws AonSQLException {
		int c01 = 0;
		double c02 = 0.0;
		double c03 = 0.0;
		for (Mod190Detail det : mod190.getDetails()) {
			Receiver receiver = new Receiver();

			receiver.setDocument(det.getDocument());
			receiver.setName(det.getName());
			receiver.setRepresentativeDocument(det.getRepresentativeDocument());
			receiver.setProvince(det.getProvince());
			receiver.setKey(det.getKey());
			receiver.setSubKey(det.getSubKey());
			receiver.setPerception(det.getPerception());
			receiver.setRetention(det.getRetention());
			receiver.setInKindPerception(det.getInKindPerception());
			receiver.setInKindDeposit(det.getInKindDeposit());
			receiver.setInKindOutputDeposit(det.getInKindOutputDeposit());
			receiver.setAccrualYear(det.getAccrualYear());

			receiver.setCeutaMelilla(det.getIrpfData().isCeutaMelilla()?1:0);
			receiver.setBirthYear(det.getIrpfData().getBirthYear());
			receiver.setFamilySituation(det.getIrpfData().getFamilySituation());
			receiver.setSpouseDocument(det.getIrpfData().getSpouseDocument());
			receiver.setDisability(det.getIrpfData().getDisability());
			receiver.setContract(det.getIrpfData().getContract());
			receiver.setWorkActivityExtension(det.getIrpfData()
					.isWorkActivityExtension()?1:0);
			receiver.setGeographicMobility(det.getIrpfData()
					.isGeographicMobility()?1:0);

			receiver.setApplicableReduction(det.getIrpfResult()
					.getApplicableReduction());
			receiver.setDeducibleExpense(det.getIrpfResult()
					.getDeducibleExpense());
			receiver.setCompensatoryPension(det.getIrpfResult()
					.getCompensatoryPension());
			receiver.setFoodAnnuality(det.getIrpfResult().getFoodAnnuality());

			receiver.setHomeLoanCommunnication(det.getIrpfResult()
					.isHomeLoanCommunnication()?1:0);
			receiver.setLessThan3Descendent(det.getIrpfResult()
					.getLessThan3Descendent());
			receiver.setLessThan3DescendentRatio(det.getIrpfResult()
					.getLessThan3DescendentRatio());
			receiver.setOtherDescendent(det.getIrpfResult()
					.getOtherDescendent());
			receiver.setOtherDescendentRatio(det.getIrpfResult()
					.getOtherDescendentRatio());
			receiver.setDisabilityDescendent33(det.getIrpfResult()
					.getDisabilityDescendent33());
			receiver.setDisabilityDescendent33Ratio(det.getIrpfResult()
					.getDisabilityDescendent33Ratio());
			receiver.setDisabilityDescendentDependence(det.getIrpfResult()
					.getDisabilityDescendentDependence());
			receiver.setDisabilityDescendentDependenceRatio(det.getIrpfResult()
					.getDisabilityDescendentDependenceRatio());
			receiver.setDisabilityDescendent65(det.getIrpfResult()
					.getDisabilityDescendent65());
			receiver.setDisabilityDescendent65Ratio(det.getIrpfResult()
					.getDisabilityDescendent65Ratio());
			receiver.setLessThan75Ascendant(det.getIrpfResult()
					.getLessThan75Ascendant());
			receiver.setLessThan75AscendantRatio(det.getIrpfResult()
					.getLessThan75AscendantRatio());
			receiver.setAscendant(det.getIrpfResult().getAscendant());
			receiver.setAscendantRatio(det.getIrpfResult().getAscendantRatio());
			receiver.setDisabilityAscendant33(det.getIrpfResult()
					.getDisabilityAscendant33());
			receiver.setDisabilityAscendant33Ratio(det.getIrpfResult()
					.getDisabilityAscendant33Ratio());
			receiver.setDisabilityAscendantDependence(det.getIrpfResult()
					.getDisabilityAscendantDependence());
			receiver.setDisabilityAscendantDependenceRatio(det.getIrpfResult()
					.getDisabilityAscendantDependenceRatio());
			receiver.setDisabilityAscendant65(det.getIrpfResult()
					.getDisabilityAscendant65());
			receiver.setDisabilityAscendant65Ratio(det.getIrpfResult()
					.getDisabilityAscendant65Ratio());
			receiver.setFirstChildCalculation(det.getIrpfResult()
					.getFirstChildCalculation());
			receiver.setSecondChildCalculation(det.getIrpfResult()
					.getSecondChildCalculation());
			receiver.setThirdChildCalculation(det.getIrpfResult()
					.getThirdChildCalculation());

			deponent.getReceivers().add(receiver);
			++c01;
			c02 = CommonUtil.round(c02  + CommonUtil.round(det.getPerception() + det.getInKindPerception()));
			c03 = CommonUtil.round(c03  + CommonUtil.round(det.getRetention() + det.getInKindDeposit()));
		}
		deponent.setC001(c01);
		deponent.setC002(c02);
		deponent.setC003(c03);
	}

}
