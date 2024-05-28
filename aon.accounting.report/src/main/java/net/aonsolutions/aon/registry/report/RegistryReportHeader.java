package net.aonsolutions.aon.registry.report;

import java.util.Arrays;
import java.util.stream.Stream;

enum RegistryReportHeader implements IHeader {
	
	 ID("C\u00F3digo",40)
	,DOC("N.I.F.",80)
	,NAME("Nombre",180)
	,ALIAS("Alias",85)
	,PHONE("Tel\u00E9fono",70)
	,STATUS("Estado",50)
	;
	
	private String label;
	private float width;
	
	private RegistryReportHeader (String label, float width) {
		this.label = label;
		this.width = width;
	}
	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public float getWidth() {
		return width;
	}
	
	public static Stream<RegistryReportHeader> stream() {
		return Arrays.stream(RegistryReportHeader.values());
	}
	
}
