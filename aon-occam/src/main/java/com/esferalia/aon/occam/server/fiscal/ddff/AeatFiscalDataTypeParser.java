package com.esferalia.aon.occam.server.fiscal.ddff;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import com.esferalia.aon.occam.api.model.ddff.AeatFiscalData;
import com.esferalia.aon.occam.api.model.ddff.AeatFiscalDataType;
import com.esferalia.aon.occam.api.model.ddff.AeatFiscalDataType.AeatFiscalDataTypeContext;

public class AeatFiscalDataTypeParser {
	
	public static AeatFiscalData parse( String data) throws IOException {
		return parse( new StringReader( data ) );
	}
	
	public static AeatFiscalData parse(byte[] data) throws IOException {
		return parse( new ByteArrayInputStream( data ) );
	}

	public static AeatFiscalData parse( InputStream in) throws IOException {
		return parse( new InputStreamReader(in, StandardCharsets.UTF_8) );
	}
	
	public static AeatFiscalData parse( Reader r) throws IOException {
		LineNumberReader reader = new LineNumberReader( r );
		final AeatFiscalData fiscalData = new AeatFiscalData();
		while (reader.ready()) {
			String line = reader.readLine();
			AeatFiscalDataTypeContext ctx = new AeatFiscalDataTypeContext(fiscalData).setLine(line);
			AeatFiscalDataType.parse( new AeatFiscalDataTypeVisitor(), ctx);			
		}
		return fiscalData;
	}

}