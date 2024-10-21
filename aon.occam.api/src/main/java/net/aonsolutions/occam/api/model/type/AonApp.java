package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum AonApp implements Serializable {
	INVOICE	("Facturas"		
			,null ), 
	DOCUMENTAL("Documental"	
			,new Module[] {Module.DOCUMENT}),
	MESSENGER("Mensajer\u00eda"
			,new Module[] {Module.CALL_CENTER}),
	ACCOUNTING("Contabilidad"
			,new Module[] {Module.ACCOUNTING}),
	FISCAL("Fiscal"
			,new Module[] {Module.FISCAL}),
	PAYROLL("Laboral"
			,new Module[] {Module.PAYROLL}),
	OCR("OCR"
			,null ), 
	AIO("AIO"
			,null ), 
	ALMA("Alma"
			,null ), 
	COMUNICA("Comunica"
			,null ), 
	BIDOQ("Bidoq"
			,null ), 
	CONVENIOS("Convenios"
			,null ), 
	BANK("Bancos"
			,null ), 
	TIMECONTROL("Control Horario"
			,null ), 
	@Deprecated	MANAGEMENT("Gesti\u00f3n"
			,new Module[] {Module.MANAGEMENT}),
	PACK_SUITE("Suite Completa"
			,null ), 
	PACK_PORTAL("Pack Portal"
			,null ), 
	PACK_PAYROLL("Pack Cotizaci\u00f3n"
			,null ), 
	PACK_FISCAL_ACCOUNTING("Pack Tributaci\u00f3n"
			,null ), 
	SELFCONTA("Selfconta"
			,null ), 
	CUSTOM_VIEW("Vista Personalizada"
			,null ), 
	AULA("Aula"
			,null ), 
	NOTES("Notas"
			,null ), 
	SALTRA("Saltra"
			,null ), 
	BASIC_MANAGEMENT("Gesti\u00f3n B\u00e1sica"
			,null ), 
	STANDAR_MANAGEMENT("Gesti\u00f3n Est\u00e1ndar"
			,new Module[] {Module.AON_ONE}),
	PROFESSIONAL_MANAGEMENT("Gesti\u00f3n Profesional"
			,null ), 
	@Deprecated KIT_DIGITAL_FACE("Kit Digital FACe"
			,new Module[] {Module.MANAGEMENT,Module.CRM}),
	@Deprecated	KIT_DIGITAL_CRM("Kit Digital CRM"
			,new Module[] {Module.CRM,Module.MARKETING}),
	@Deprecated	KIT_DIGITAL_ERP("Kit Digital ERP"
			,new Module[] {Module.MANAGEMENT,Module.TREASURY}),
	API_SERVICE("Servicio API"
			,null),
	WAREHOUSE("Almac\u00e9n"
			,new Module[] {Module.WAREHOUSE}),
	COMMERCIAL("Comercial"
			,new Module[] {Module.CRM}),
	MARKETING("Marketing"
			,new Module[] {Module.MARKETING}),
	TREASURY("Tesorer\u00eda"
			,new Module[] {Module.TREASURY}),
	GROUPWARE("Expedientes"
			,new Module[] {Module.GROUPWARE}),
	INVOFOX("OCR Invofox"
			,null),
	SERES("Seres"
			,null),
	FACTURAE("Factura Electr\u00f3nica"
			,null)
	;
	
	private Module[] modules;
	private String description;
	
	private AonApp(String description, Module[] modules) {
		this.description = description;
		this.modules = modules;
	}
	
	public Optional<Module[]> getModules() {
		return Optional.ofNullable( modules );
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static Optional<AonApp> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<AonApp> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= AonApp.values().length) return Optional.empty();
		return Optional.of(AonApp.values()[i]);
	}
	
	public static Optional<AonApp> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
	
}
