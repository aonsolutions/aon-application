package net.aonsolutions.occam.api.constants;

import static net.aonsolutions.occam.api.constants.WithholdingTypeGroup.CAPITAL_INMOBILIARIO;
import static net.aonsolutions.occam.api.constants.WithholdingTypeGroup.CAPITAL_MOBILIARIO;
import static net.aonsolutions.occam.api.constants.WithholdingTypeGroup.DERECHOS_IMAGEN;
import static net.aonsolutions.occam.api.constants.WithholdingTypeGroup.GANANCIAS_PATRIMONIALES;
import static net.aonsolutions.occam.api.constants.WithholdingTypeGroup.OTRAS;
import static net.aonsolutions.occam.api.constants.WithholdingTypeGroup.PROFESIONAL;
import static net.aonsolutions.occam.api.constants.WithholdingTypeGroup.TRABAJO;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import net.aonsolutions.watson.client.util.AonStringUtils;

public enum WithholdingType implements Serializable {
	
	// MODELO 190 - CLAVE G - 01 
	PROFESSIONAL(PROFESIONAL,"Profesional (G/01)","Actividades profesionales con car\u00E1cter general.") {
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitProfessional(t);}
	}
	// MODELO 180 
	,RENTING 	(CAPITAL_INMOBILIARIO,"Arrendamiento","Arrendamiento o subarrendamiento de bienes inmuebles urbanos.") {
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitRenting(t);}
	}
	// MODELO 193 - CLAVE A  
	,MOVABLE_CAPITAL	(CAPITAL_MOBILIARIO,"Cap. Mobiliario (A)","Derivados de la participaci\u00F3n en fondos propios de entidades."){
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitMovableCapital(t);}
	}
	// MODELO 190 - CLAVE H - 01  
	,FARMER	(OTRAS,"Agr\u00EDcola (H/01)","Actividades agr\u00EDcolas y ganaderas en general.")  {
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitFarmer(t);}
	}	
	// MODELO 190 - CLAVE H - 04  
	,TRANSPORT_OPERATOR	(OTRAS,"Est. Obj. (H/04)","Determinadas actividades empresariales en Estimaci\u00F3n Objetiva.") {
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitTransportOperator(t);}
	}
	
	// ------------------------------------------------------- NEW TYPES
	// MODELO 190 - CLAVE G - 02  
	,M190_G_02	(PROFESIONAL,"Profesional (G/02)","Determinadas actividades profesionales (recaudadores municipales, mediadores de seguros ...)") {
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitM190G02(t);}
	}	
	// MODELO 190 - CLAVE G - 03  
	,M190_G_03	(PROFESIONAL,"Profesional (G/03)","Profesionales de nuevo inicio.") {
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitM190G03(t);}
	}	
	// MODELO 190 - CLAVE H - 02  
	,M190_H_02	(OTRAS,"Agr\u00EDcola (H/02)","Actividades de engorde de porcino y avicultura.") {
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitM190H02(t);}
	}	
	// MODELO 190 - CLAVE H - 03  
	,M190_H_03	(OTRAS,"Forestales (H/03)","Actividades forestales.") {
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitM190H03(t);}
	}	
	// MODELO 190 - CLAVE I - 01  
	,M190_I_01	(OTRAS,"Der.Imagen (I/01)","Rendimientos del art. 75.2.b): cesi\u00F3n derecho de imagen") {
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitM190I01(t);}
	}
	// MODELO 190 - CLAVE I - 02
	,M190_I_02	(OTRAS,"Otras (I/02)","Rendimientos del art. 75.2.b): resto de conceptos") {
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitM190I02(t);}
	}	
	// MODELO 190 - CLAVE J
	,M190_J	(DERECHOS_IMAGEN,"Cesi\u00F3n Der. Imagen(J)","Imputaci\u00F3n rentas por cesi\u00F3n derechos imagen") {
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitM190J(t);}
	}	
	// MODELO 190 - CLAVE K - 01
	,M190_K_01	(GANANCIAS_PATRIMONIALES,"Premios (K/01)","Premios de juegos, concursos, rifas... sujetos a retenci\u00F3n, (K - 01)") {
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitM190K01(t);}
	}	
	// MODELO 190 - CLAVE K - 03
	,M190_K_03	(GANANCIAS_PATRIMONIALES,"Premios (K/03)","Premios de juegos, concursos, rifas... sujetos a retenci\u00F3n, (K - 03)") {
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitM190K03(t);}
	}	
	// MODELO 190 - CLAVE K - 02
	,M190_K_02	(GANANCIAS_PATRIMONIALES,"Gan. forestal (K/02)","Aprovechamientos forestales en montes p\u00FAblicos.") {
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitM190K02(t);}
	}	
	// MODELO 193 - CLAVE C 1
	,M193_C1	(CAPITAL_MOBILIARIO,"Cap. Mobiliario (C/1)","Propiedad intelectual, industrial, prestaci\u00F3n de asistencia t\u00E9cnica."){
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitM193C1(t);}
	}
	// MODELO 193 - CLAVE C 2
	,M193_C2	(CAPITAL_MOBILIARIO,"Cap. Mobiliario (C/2)","Propiedad intelectual cuando el contribuyente perceptor no sea el autor."){
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitM193C2(t);}
	}
	// MODELO 193 - CLAVE C 3
	,M193_C3	(CAPITAL_MOBILIARIO,"Cap. Mobiliario (C/3)","Rendimientos derivados de la cesi\u00F3n del derecho de explotaci\u00F3n de derechos de imagen siempre que no sean en el desarrollo de una actividad econ\u00F3mica"){		
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitM193C3(t);}
	}
	// MODELO 190 - CLAVE F 01
	,M190_F_01	(TRABAJO,"Trabajo (F/01)","Premios literarios, art\u00EDsticos o cient\u00EDficos no exentos de IRPF, cuando tengan la consideraci\u00F3n de rendimientos del trabajo."){
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitM190F01(t);}
	}
	,M190_F_02_1(TRABAJO,"Cursos (F/02)","Cursos, conferencias, seminarios."){
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitM190F021(t);}
	}
	,M190_F_02_2(TRABAJO,"Elab. obras (F/02)","Elaboraci\u00F3n de obras literarias, art\u00EDsticas o cient\u00EDficas."){
		@Override public <R,T> R visit(WithholdingTypeVisitor<R,T> v, T t) {return v.visitM190F022(t);}
	}
	;
	
	private WithholdingTypeGroup group;
	private String description;
	private String abbrev;
	
	private WithholdingType(WithholdingTypeGroup group, String abbrev, String description){
		this.group = group;
		this.abbrev = abbrev;
		this.description = description;
	}
	
	public WithholdingTypeGroup getGroup() {
		return group;
	}
	public String getAbbreviatedDescription() {
		return abbrev;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) ordinal();
	}	

	public static Optional<WithholdingType> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty();
		return safeValueOf( i.intValue() ); 
	}
	
	public static Optional<WithholdingType> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= WithholdingType.values().length) return Optional.empty();
		return Optional.of( WithholdingType.values()[i]);
	}
	
	public static Optional<WithholdingType> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name()))
			.findFirst();
	}

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

	public static WithholdingType[] getTypes(WithholdingTypeGroup group) {
		return Arrays.stream(WithholdingType.values())
			.filter(t -> t.getGroup() == group)
			.toArray( size -> new WithholdingType[size]);
	}
	public static Byte[] getValueTypes(WithholdingTypeGroup group) {
		return Arrays.stream(WithholdingType.values())
			.filter(t -> t.getGroup() == group)
			.map( t -> t.value())
			.toArray( size -> new Byte[size]);
	}
	
	public abstract <R,T> R visit(WithholdingTypeVisitor<R,T> visitor, T t);
	public static interface WithholdingTypeVisitor<R,T> {
		R visitProfessional(T t);
		R visitRenting(T t);
		R visitMovableCapital(T t);
		R visitFarmer(T t);
		R visitTransportOperator(T t);
		R visitM190G02(T t);
		R visitM190G03(T t);
		R visitM190H02(T t);
		R visitM190H03(T t);
		R visitM190I01(T t);
		R visitM190I02(T t);
		R visitM190J(T t);
		R visitM190K01(T t);
		R visitM190K03(T t);
		R visitM190K02(T t);
		R visitM193C1(T t);
		R visitM193C2(T t);
		R visitM193C3(T t);
		R visitM190F01(T t);
		R visitM190F021(T t);
		R visitM190F022(T t);
	}
}
