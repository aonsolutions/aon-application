package com.esferalia.aon.gwt.fiscal.server.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;

import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.fiscal.server.mod200.Mod200File;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200;
import com.esferalia.aon.occam.api.model.type.Administration;

public class MOD200Writer {
	
	public FileOutput createMOD200(Connection conn, Mod200 mod200) throws AonSQLException {
		try {
			MOD200Format format = obtainFormat(mod200.getYear(), mod200.getAdministration());
			if (format == null) {
				throw new AonSQLException(
						"No existe soporte para el formato de la declaraci\u00F3n "
								+ "200 del ejercicio " + mod200.getYear()
								+ " en la administraci\u00F3n "
								+ mod200.getAdministration());
			}
			Mod200File mod200file = new Mod200File( mod200 ); 
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			FileFiller filler = new MOD200(mod200file, format, writer);
			FileOutput fileOutput = new FileOutput();
			fileOutput.setErrors(filler.create());
			fileOutput.setContent(output.toByteArray());
			return fileOutput;
		} catch (IOException e) {
			throw new AonSQLException(e.getMessage());
		}
	}

	private MOD200Format obtainFormat(int year, int administration) {
		Administration adm = Administration.values()[administration];
		MOD200Format f = null;
		for (MOD200Format format : MOD200Format.values()) {
			if (format.getAdministration() == adm && year >= format.getYear()) {
				if (f == null || f.getYear() < format.getYear()) {
					f = format;
				}
			}
		}
		return f;
	}

}
