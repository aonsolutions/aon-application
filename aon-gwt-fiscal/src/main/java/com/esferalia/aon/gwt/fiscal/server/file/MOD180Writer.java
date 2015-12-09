package com.esferalia.aon.gwt.fiscal.server.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD180.Deponent;
import com.code.aon.file.tax.model.MOD180.MOD180;
import com.code.aon.file.tax.model.MOD180.MOD180Format;
import com.code.aon.file.tax.model.MOD180.Receiver;
import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.watson.util.AonStringUtils;

public class MOD180Writer {

	public FileOutput createMOD180(String domainName, int domainId,String user
			, Integer mod180, int year, Integer administration) throws AonSQLException {
		try {
			MOD180Format format = MOD180Format.obtainFormat(year, administration);
			if (format == null) {
				throw new AonSQLException(
						"No existe soporte para el formato de la declaraci\u00F3n "
								+ "180 del ejercicio " + year
								+ " en la administraci\u00F3n "
								+ administration);
			}
			Deponent deponent = getDeponent(domainName,domainId,user, mod180, format);
			
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			FileFiller filler = new MOD180(deponent, format, writer);
			FileOutput fileOutput = new FileOutput();
			fileOutput.setErrors(filler.create());
			fileOutput.setContent(output.toByteArray());
			return fileOutput;
		} catch (IOException e) {
			throw new AonSQLException(e.getMessage());
		}
	}

	private Deponent getDeponent(String domainName, int domainId, String user,Integer id,
			MOD180Format format) throws AonSQLException {
		Mod180 mod180 = AON.getMod180(domainName,domainId, user, id);
		Deponent deponent = new Deponent();
		deponent.setYear(mod180.getYear());
		deponent.setDocument(mod180.getDocument());
		deponent.setName(mod180.getName());
		deponent.setContactPhone(AonUtil.isEmpty(mod180.getContactPhone()) ? "0"
				: mod180.getContactPhone());
		deponent.setContactPerson(mod180.getContactPerson());
		deponent.setC001(0);
		deponent.setC002(0);
		deponent.setC003(0);
		deponent.setComplementary("");
		deponent.setReplacement(mod180.isReplacement()?"S":"");
		// TODO Soporte al número de justificante
		deponent.setReceipt( (AonUtil.isEmpty(mod180.getReceipt()))?"1800000000001":mod180.getReceipt() );
		deponent.setReplacedReceipt( (AonUtil.isEmpty(mod180.getReplacedReceipt()))?"0":mod180.getReplacedReceipt());
		fillReceivers(mod180, deponent, format);
		return deponent;
	}

	private void fillReceivers(Mod180 mod180, Deponent deponent,MOD180Format format) throws AonSQLException {
		int c001 = 0;
		double c002 = 0;
		double c003 = 0;		
		for (Mod180Detail det : mod180.getDetails()) {
			Receiver receiver = new Receiver();

			receiver.setDocument(det.getDocument());
			receiver.setName(det.getName());
			receiver.setRepresentativeDocument(det.getRepresentativeDocument());
			receiver.setProvince(det.getProvince());
			receiver.setInKind(det.isInKind()?"2":"1");
			receiver.setPerception(det.getPerception());
			receiver.setRetention(det.getRetention());
			receiver.setPercent(det.getPercent());
			receiver.setAccrualYear(det.getAccrualYear());
			receiver.setLocation(det.getLocation());
			receiver.setCadasdralReference(det.getCadasdralReference()); 
			receiver.setStreetType(det.getStreetType());
			receiver.setStreetName(det.getStreetName());
			receiver.setNumberType(det.getNumberType());
			if (AonStringUtils.isNumeric(AonStringUtils.trim(det.getNumber()))) {
				Integer i = Integer.parseInt(det.getNumber());
				receiver.setNumber(i);
			} else {
				receiver.setNumber(0);	
			}
			receiver.setNumberSuffix(det.getNumberSuffix());
			receiver.setBlock(det.getBlock());
			receiver.setHall(det.getHall());
			receiver.setStair(det.getStair());
			receiver.setFloor(det.getFloor());
			receiver.setDoor(det.getDoor());
			receiver.setComplement(det.getComplement());
			receiver.setCity(det.getCity());
			receiver.setTown(det.getTown());
			receiver.setTownCode(det.getTownCode());
			if (AonStringUtils.isNumeric(AonStringUtils.trim(det.getProvinceCode()))) {
				Integer i = Integer.parseInt(det.getProvinceCode());
				receiver.setProvinceCode(i);
			} else {
				receiver.setProvinceCode(0);	
			}
			if (AonStringUtils.isNumeric(AonStringUtils.trim(det.getZip()))) {
				Integer i = Integer.parseInt(det.getZip());
				receiver.setZip(i);
			} else {
				receiver.setZip(0);	
			}
			c001++;
			c002 += det.getPerception();
			c003 += det.getRetention();
			deponent.getReceivers().add(receiver);
		}
		deponent.setC001(c001);
		deponent.setC002(c002);
		deponent.setC003(c003);
	}
}
