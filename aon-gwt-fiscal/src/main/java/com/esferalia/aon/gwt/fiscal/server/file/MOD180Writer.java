package com.esferalia.aon.gwt.fiscal.server.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.ArrayList;

import com.code.aon.config.enumeration.Administration;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD180.Deponent;
import com.code.aon.file.tax.model.MOD180.MOD180;
import com.code.aon.file.tax.model.MOD180.MOD180Format;
import com.code.aon.file.tax.model.MOD180.Receiver;
import com.esferalia.aon.gwt.fiscal.shared.AonSQLException;
import com.esferalia.aon.gwt.fiscal.shared.AonUtil;
import com.esferalia.aon.gwt.fiscal.shared.Mod180;
import com.esferalia.aon.gwt.fiscal.sql.SQLMod180;

public class MOD180Writer {

	public FileOutput createMOD180(Connection conn, Integer mod180, int year,
			Integer administration) throws AonSQLException {
		try {
			MOD180Format format = obtainFormat(year, administration);
			if (format == null) {
				throw new AonSQLException(
						"No existe soporte para el formato de la declaraci\u00F3n "
								+ "180 del ejercicio " + year
								+ " en la administraci\u00F3n "
								+ administration);
			}
			Deponent deponent = getDeponent(conn, mod180, format);
			
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

	private MOD180Format obtainFormat(int year, int administration) {
		Administration adm = Administration.values()[administration];
		MOD180Format f = null;
		for (MOD180Format format : MOD180Format.values()) {
			if (format.getAdministration() == adm && year >= format.getYear()) {
				if (f == null || f.getYear() < format.getYear()) {
					f = format;
				}
			}
		}
		return f;
	}

	private Deponent getDeponent(Connection conn, Integer id,
			MOD180Format format) throws AonSQLException {
		Mod180 mod180 = SQLMod180.getById(id, conn);
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
		deponent.setReceipt( (AonUtil.isEmpty(mod180.getReceipt()))?"0":mod180.getReceipt() );
		deponent.setReplacedReceipt( (AonUtil.isEmpty(mod180.getReplacedReceipt()))?"0":mod180.getReplacedReceipt());
		fillReceivers(conn, deponent, id, format);
		return deponent;
	}

	private void fillReceivers(Connection conn, Deponent deponent,
			Integer mod180, MOD180Format format) throws AonSQLException {
		ArrayList<com.esferalia.aon.gwt.fiscal.shared.Mod180Receiver> list = SQLMod180
				.getReceiversByMod180(mod180, conn);
		int c001 = 0;
		double c002 = 0;
		double c003 = 0;		
		for (com.esferalia.aon.gwt.fiscal.shared.Mod180Receiver det : list) {
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
