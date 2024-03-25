package net.aonsolutions.aon.registry.report;

import java.util.Arrays;
import java.util.stream.Stream;

enum RegistryReportHeader implements IHeader {
	
	 ID("C\u00F3digo",40,20)
	,DOC("N.I.F.",80,40)
	,NAME("Nombre",180,90)
	,ALIAS("Alias",85,42)
	,PHONE("Tel\u00E9fono",70,35)
	,STATUS("Estado",50,25)
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
