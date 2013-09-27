package com.esferalia.aon.payroll.sepe;

import java.io.IOException;

public class SEPECodeTablesWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ContrataCodeTablesWriter.main(args);
		CertificadosCodeTablesWriter.main(args);
	}
	
	public interface ISepeEnum {
		
		public String getCode();

		public String getDescription();

		public boolean isActive();
		
	}
		
}