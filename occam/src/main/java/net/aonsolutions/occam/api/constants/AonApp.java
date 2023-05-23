package net.aonsolutions.occam.api.constants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import net.aonsolutions.watson.client.util.AonStringUtils;

public enum AonApp implements Serializable {

	INVOICE("Facturas"), 
	DOCUMENTAL("Documental", new AonModule[]{AonModule.DOCUMENT}),
	MESSENGER("Mensajería", new AonModule[]{AonModule.CALL_CENTER}),
	ACCOUNTING("Contabilidad", new AonModule[]{AonModule.ACCOUNTING}),
	FISCAL("Fiscal", new AonModule[]{AonModule.FISCAL}),
	PAYROLL("Laboral", new AonModule[]{AonModule.PAYROLL}),
	OCR("OCR"),
	AIO("AIO"),
	ALMA("Alma"),
	COMUNICA("Comunica"),
	BIDOQ("Bidoq"),
	CONVENIOS("Convenios"),
	BANK("Bancos"),
	TIMECONTROL("Control Horario"),
	@Deprecated	MANAGEMENT("Gestión", new AonModule[]{AonModule.MANAGEMENT}),
	PACK_SUITE("Suite Completa"),
	PACK_PORTAL("Pack Portal"),
	PACK_PAYROLL("Pack Cotización"),
	PACK_FISCAL_ACCOUNTING("Pack Tributación"),
	SELFCONTA("Selfconta"),
	CUSTOM_VIEW("Vista Personalizada"),
	AULA("Aula"),
	NOTES("Notas"),
	SALTRA("Saltra"),
	BASIC_MANAGEMENT("Gestión Básica"),
	STANDAR_MANAGEMENT("Gestión Estándar", new AonModule[]{AonModule.AON_ONE}),
	PROFESSIONAL_MANAGEMENT("Gestión Profesional"),
	@Deprecated	KIT_DIGITAL_FACE("Kit Digital FACe", new AonModule[]{AonModule.MANAGEMENT,AonModule.CRM}),
	@Deprecated	KIT_DIGITAL_CRM("Kit Digital CRM", new AonModule[]{AonModule.CRM,AonModule.MARKETING}),
	@Deprecated	KIT_DIGITAL_ERP("Kit Digital ERP", new AonModule[]{AonModule.MANAGEMENT,AonModule.TREASURY}),
	API_SERVICE("Servicio API"),
	WAREHOUSE("Almacén",new AonModule[]{AonModule.WAREHOUSE} ),
	COMMERCIAL("Comercial", new AonModule[]{AonModule.CRM}),
	MARKETING("Marketing", new AonModule[]{AonModule.MARKETING}),
	TREASURY("Tesorería", new AonModule[]{AonModule.TREASURY}),
	GROUPWARE("Expedientes", new AonModule[]{AonModule.GROUPWARE})
	;
	
	private String description;
	private AonModule[] modules;
	
	private AonApp(String description, AonModule[] modules) {
		this.description = description;
		this.modules = modules;
	}
	private AonApp(String description) {
		this(description, new AonModule[]{});
	}
	
	public String getDescription() {
		return description;
	}
	
	public AonModule[] getModules() {
		return modules;
	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static Optional<AonApp> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty();
		return safeValueOf( i.intValue() ); 
	}
	
	public static Optional<AonApp> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= AonApp.values().length) return Optional.empty();
		return Optional.of( AonApp.values()[i]);
	}
	
	public static Optional<AonApp> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name()))
			.findFirst();
	}
	
}
