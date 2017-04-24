package com.esferalia.aon.gwt.fiscal.server;

import java.io.File;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DefaultDocumentFormatRegistry;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

public class JODConverterUtils {

	private static final int DEFAULT_OFFICE_PORT = 2002;

	public static synchronized void process(File inputFile, File outputFile) {
		OfficeManager officeManager = null;
		try {
			DocumentFormatRegistry formatRegistry = new DefaultDocumentFormatRegistry();
			officeManager = new DefaultOfficeManagerConfiguration()
					.setPortNumber(DEFAULT_OFFICE_PORT)
					.buildOfficeManager();
			officeManager.start();
			OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager, formatRegistry);
			converter.convert(inputFile, outputFile);
		} finally {
			if (officeManager != null) officeManager.stop();		
		}
	}

}
