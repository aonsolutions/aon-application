package com.esferalia.aon.occam.api.model.type;

import static com.esferalia.aon.occam.api.model.type.WithholdingTypeGroup.CAPITAL_INMOBILIARIO;
import static com.esferalia.aon.occam.api.model.type.WithholdingTypeGroup.CAPITAL_MOBILIARIO;
import static com.esferalia.aon.occam.api.model.type.WithholdingTypeGroup.DERECHOS_IMAGEN;
import static com.esferalia.aon.occam.api.model.type.WithholdingTypeGroup.GANANCIAS_PATRIMONIALES;
import static com.esferalia.aon.occam.api.model.type.WithholdingTypeGroup.OTRAS;
import static com.esferalia.aon.occam.api.model.type.WithholdingTypeGroup.PROFESIONAL;
import static com.esferalia.aon.occam.api.model.type.WithholdingTypeGroup.TRABAJO;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IWithholdingTypeVisitor;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum WithholdingType implements Serializable {
	
	// MODELO 190 - CLAVE G - 01 
	PROFESSIONAL 	(PROFESIONAL,"Actividades profesionales con car\u00E1cter general.") {
			
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitProfessional(t);
		}
	}
	// MODELO 180 
	,RENTING 	(CAPITAL_INMOBILIARIO,"Arrendamiento o subarrendamiento de bienes inmuebles urbanos.") {
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitRenting(t);
		}
	}
	// MODELO 193 - CLAVE A  
	,MOVABLE_CAPITAL	(CAPITAL_MOBILIARIO,"Derivados de la participaci\u00F3n en fondos propios de entidades."){
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitMovableCapital(t);
		}
	}
	// MODELO 190 - CLAVE H - 01  
	,FARMER	(OTRAS,"Actividades agr\u00EDcolas y ganaderas en general.")  {
		
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitProfessional(t);
		}
	}	
	// MODELO 190 - CLAVE H - 04  
	,TRANSPORT_OPERATOR	(OTRAS,"Determinadas actividades empresariales en Estimaci\u00F3n Objetiva.") {
		
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitProfessional(t);
		}
	}
	
	// ------------------------------------------------------- NEW TYPES
	// MODELO 190 - CLAVE G - 02  
	,M190_G_02	(PROFESIONAL,"Determinadas actividades profesionales (recaudadores municipales, mediadores de seguros ...)") {
		
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitM190G02(t);
		}
	}	
	// MODELO 190 - CLAVE G - 03  
	,M190_G_03	(PROFESIONAL,"Profesionales de nuevo inicio.") {
		
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitM190G03(t);
		}
	}	
	// MODELO 190 - CLAVE H - 02  
	,M190_H_02	(OTRAS,"Actividades de engorde de porcino y avicultura.") {
		
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitM190H02(t);
		}
	}	
	// MODELO 190 - CLAVE H - 03  
	,M190_H_03	(OTRAS,"Actividades forestales.") {
		
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitM190H03(t);
		}
	}	
	// MODELO 190 - CLAVE I - 01  
	,M190_I_01	(OTRAS,"Rendimientos del art. 75.2.b): cesi\u00F3n derecho de imagen") {
		
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitM190I01(t);
		}
	}
	// MODELO 190 - CLAVE I - 02
	,M190_I_02	(OTRAS,"Rendimientos del art. 75.2.b): resto de conceptos") {
		
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitM190I02(t);
		}
	}	
	// MODELO 190 - CLAVE J
	,M190_J	(DERECHOS_IMAGEN,"Imputaci\u00F3n rentas por cesi\u00F3n derechos imagen") {
		
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitM190J(t);
		}
	}	
	// MODELO 190 - CLAVE K - 01
	,M190_K_01	(GANANCIAS_PATRIMONIALES,"Premios de juegos, concursos, rifas... sujetos a retenci\u00F3n, (K - 01)") {
		
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitM190K01(t);
		}
	}	
	// MODELO 190 - CLAVE K - 03
	,M190_K_03	(GANANCIAS_PATRIMONIALES,"Premios de juegos, concursos, rifas... sujetos a retenci\u00F3n, (K - 03)") {
		
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitM190K03(t);
		}
	}	
	// MODELO 190 - CLAVE K - 02
	,M190_K_02	(GANANCIAS_PATRIMONIALES,"Aprovechamientos forestales en montes p\u00FAblicos.") {
		
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitM190K02(t);
		}
	}	
	// MODELO 193 - CLAVE C 1
	,M193_C1	(CAPITAL_MOBILIARIO,"Propiedad intelectual, industrial, prestaci\u00F3n de asistencia t\u00E9cnica."){
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitM193C1(t);
		}
	}
	// MODELO 193 - CLAVE C 2
	,M193_C2	(CAPITAL_MOBILIARIO,"Propiedad intelectual cuando el contribuyente perceptor no sea el autor."){
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitM193C2(t);
		}
	}
	// MODELO 193 - CLAVE C 3
	,M193_C3	(CAPITAL_MOBILIARIO,"Rendimientos derivados de la cesi\u00F3n del derecho de explotaci\u00F3n de derechos de imagen siempre que no sean en el desarrollo de una actividad econ\u00F3mica"){
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitM193C3(t);
		}
	}
	// MODELO 190 - CLAVE F 01
	,M190_F_01	(TRABAJO,"Premios literarios, art\u00EDsticos o cient\u00EDficos no exentos de IRPF, cuando tengan la consideraci\u00F3n de rendimientos del trabajo."){
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitM190F01(t);
		}
	}
	,M190_F_02_1(TRABAJO,"Cursos, conferencias, seminarios."){
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitM190F021(t);
		}
	}
	,M190_F_02_2(TRABAJO,"Elaboraci\u00F3n de obras literarias, art\u00EDsticas o cient\u00EDficas."){
		@Override 
		public <T> T visit(IWithholdingTypeVisitor<T> visitor, T t) {
			return visitor.visitM190F022(t);
		}
	}
	;
	
	private WithholdingTypeGroup group;
	private String description;
	
	private WithholdingType(WithholdingTypeGroup group, String description){
		this.group = group;
		this.description = description;
	}
	
	public WithholdingTypeGroup getGroup() {
		return group;
	}
	public String getDescription() {
		return description;
	}
	public byte value() {
		return (byte) ordinal();
	}	

	public static WithholdingType safeValueOf(Byte i) {
		if (i == null) return null;
		return safeValueOf(i.intValue());
	}

	public static WithholdingType safeValueOf(Integer i) {
		if (i == null) return null;
		if (i < 0 || i >= WithholdingType.values().length) return null;
		return WithholdingType.values()[i];
	}
	
	public static WithholdingType safeValueOf(String str) {
		if(AonStringUtils.isBlank(str)) return PROFESSIONAL;
		
		if("IRPF_PROF".equalsIgnoreCase(str)) {
			return PROFESSIONAL;
		} else if("IRPF_ALQ".equalsIgnoreCase(str)) {
			return RENTING;
		} if("IRPF_AGRI".equalsIgnoreCase(str)) {
			return FARMER;
		}
		
		for (WithholdingType rs : values()) {
			if(rs.name().equalsIgnoreCase(str) || rs.getDescription().equalsIgnoreCase(str))
				return rs;
		}
		return PROFESSIONAL;
	}
	
	public abstract <T> T visit(IWithholdingTypeVisitor<T> visitor, T t);

	public static WithholdingType[] ORDERED_VALUES = new WithholdingType[] { 
			 // PROFESIONAL 
		PROFESSIONAL,M190_G_02,M190_G_03
		// CAPITAL_INMOBILIARIO
		,RENTING
		//  OTRAS
		,FARMER,M190_H_02,M190_H_03,TRANSPORT_OPERATOR,M190_I_01,M190_I_02
		// TRABAJO
		,M190_F_01,M190_F_02_1,M190_F_02_2
		// CAPITAL_MOBILIARIO
		,MOVABLE_CAPITAL,M193_C1,M193_C2,M193_C3
		// DERECHOS_IMAGEN
		,M190_J
		// GANANCIAS_PATRIMONIALES
		,M190_K_01,M190_K_03,M190_K_02
	};


	
	
}
