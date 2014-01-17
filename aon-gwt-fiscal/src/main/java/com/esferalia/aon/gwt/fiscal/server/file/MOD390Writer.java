package com.esferalia.aon.gwt.fiscal.server.file;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.code.aon.file.format.output.FileOutput;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013;
import com.esferalia.aon.gwt.fiscal.shared.AonSQLException;
import com.esferalia.aon.gwt.fiscal.shared.Mod390;
import com.esferalia.aon.gwt.fiscal.sql.SQLMod390;

public class MOD390Writer {

	public FileOutput createMOD390(Connection conn, Integer mod390Id) throws AonSQLException {
		try {
			Mod390 mod390 = getMod390(conn, mod390Id);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);

			AEATIVA2013 m390 = new AEATIVA2013();
			JAXBContext context = JAXBContext.newInstance(AEATIVA2013.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(m390, writer);
			
			FileOutput fileOutput = new FileOutput();
			fileOutput.setContent(output.toByteArray());
			return fileOutput;
		} catch (JAXBException e) {
			throw new AonSQLException(e.getMessage());
		}
	}

	private Mod390 getMod390(Connection conn, Integer id) throws AonSQLException {
		Mod390 mod390 = SQLMod390.getById(id, conn);
		
		return mod390;
	}

}
