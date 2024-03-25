package net.aonsolutions.aon.registry.report;

import java.util.Arrays;
import java.util.stream.Stream;

enum RegistryReportHeader implements IHeader {
	
	 ID("C\u00F3digo",40,15)
	,DOC("N.I.F.",80,20)
	,NAME("Nombre",180,45)
	,ALIAS("Alias",85,35)
	,PHONE("Tel\u00E9fono",70,18)
	,STATUS("Estado",50,15)
	;
	
	private String label;
	private float pdfWidth;
	private float xlsWidth;
	
	private RegistryReportHeader (String label, float pdfWidth, float xlsWidth) {
		this.label = label;
		this.pdfWidth = pdfWidth;
		this.xlsWidth = xlsWidth;
	}
	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public float getpdfWidth() {
		return pdfWidth;
	}
	@Override
	public float getxlsWidth() {
		return xlsWidth;
	}
	
	public static Stream<RegistryReportHeader> stream() {
		return Arrays.stream(RegistryReportHeader.values());
	}
	
}
