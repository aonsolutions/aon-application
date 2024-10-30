package net.aonsolutions.occam.api.model.type;

import static net.aonsolutions.occam.api.model.type.WithholdingTypeGroup.PROFESIONAL;
import static net.aonsolutions.occam.api.model.type.WithholdingTypeGroup.OTRAS;
import static net.aonsolutions.occam.api.model.type.WithholdingTypeGroup.DERECHOS_IMAGEN;
import static net.aonsolutions.occam.api.model.type.WithholdingTypeGroup.GANANCIAS_PATRIMONIALES;
import static net.aonsolutions.occam.api.model.type.WithholdingTypeGroup.CAPITAL_MOBILIARIO;
import static net.aonsolutions.occam.api.model.type.WithholdingTypeGroup.CAPITAL_INMOBILIARIO;
import static net.aonsolutions.occam.api.model.type.WithholdingTypeGroup.TRABAJO;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum WithholdingType implements Serializable {
	
	// MODELO 190 - CLAVE G - 01 
	PROFESSIONAL 	(PROFESIONAL,"Profesional (G/01)","Actividades profesionales con car\u00E1cter general.") {
			
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitProfessional();
		}
	}
	// MODELO 180 
	,RENTING 	(CAPITAL_INMOBILIARIO,"Arrendamiento","Arrendamiento o subarrendamiento de bienes inmuebles urbanos.") {
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitRenting();
		}
	}
	// MODELO 193 - CLAVE A  
	,MOVABLE_CAPITAL	(CAPITAL_MOBILIARIO,"Cap. Mobiliario (A)","Derivados de la participaci\u00F3n en fondos propios de entidades."){
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitMovableCapital();
		}
	}
	// MODELO 190 - CLAVE H - 01  
	,FARMER	(OTRAS,"Agr\u00EDcola (H/01)","Actividades agr\u00EDcolas y ganaderas en general.")  {
		
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitFarmer();
		}
	}	
	// MODELO 190 - CLAVE H - 04  
	,TRANSPORT_OPERATOR	(OTRAS,"Est. Obj. (H/04)","Determinadas actividades empresariales en Estimaci\u00F3n Objetiva.") {
		
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitProfessional();
		}
	}
	
	// ------------------------------------------------------- NEW TYPES
	// MODELO 190 - CLAVE G - 02  
	,M190_G_02	(PROFESIONAL,"Profesional (G/02)","Determinadas actividades profesionales (recaudadores municipales, mediadores de seguros ...)") {
		
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitM190G02();
		}
	}	
	// MODELO 190 - CLAVE G - 03  
	,M190_G_03	(PROFESIONAL,"Profesional (G/03)","Profesionales de nuevo inicio.") {
		
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitM190G03();
		}
	}	
	// MODELO 190 - CLAVE H - 02  
	,M190_H_02	(OTRAS,"Agr\u00EDcola (H/02)","Actividades de engorde de porcino y avicultura.") {
		
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitM190H02();
		}
	}	
	// MODELO 190 - CLAVE H - 03  
	,M190_H_03	(OTRAS,"Forestales (H/03)","Actividades forestales.") {
		
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitM190H03();
		}
	}	
	// MODELO 190 - CLAVE I - 01  
	,M190_I_01	(OTRAS,"Der.Imagen (I/01)","Rendimientos del art. 75.2.b): cesi\u00F3n derecho de imagen") {
		
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitM190I01();
		}
	}
	// MODELO 190 - CLAVE I - 02
	,M190_I_02	(OTRAS,"Otras (I/02)","Rendimientos del art. 75.2.b): resto de conceptos") {
		
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitM190I02();
		}
	}	
	// MODELO 190 - CLAVE J
	,M190_J	(DERECHOS_IMAGEN,"Cesi\u00F3n Der. Imagen(J)","Imputaci\u00F3n rentas por cesi\u00F3n derechos imagen") {
		
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitM190J();
		}
	}	
	// MODELO 190 - CLAVE K - 01
	,M190_K_01	(GANANCIAS_PATRIMONIALES,"Premios (K/01)","Premios de juegos, concursos, rifas... sujetos a retenci\u00F3n, (K - 01)") {
		
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitM190K01();
		}
	}	
	// MODELO 190 - CLAVE K - 03
	,M190_K_03	(GANANCIAS_PATRIMONIALES,"Premios (K/03)","Premios de juegos, concursos, rifas... sujetos a retenci\u00F3n, (K - 03)") {
		
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitM190K03();
		}
	}	
	// MODELO 190 - CLAVE K - 02
	,M190_K_02	(GANANCIAS_PATRIMONIALES,"Gan. forestal (K/02)","Aprovechamientos forestales en montes p\u00FAblicos.") {
		
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitM190K02();
		}
	}	
	// MODELO 193 - CLAVE C 1
	,M193_C1	(CAPITAL_MOBILIARIO,"Cap. Mobiliario (C/1)","Propiedad intelectual, industrial, prestaci\u00F3n de asistencia t\u00E9cnica."){
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitM193C1();
		}
	}
	// MODELO 193 - CLAVE C 2
	,M193_C2	(CAPITAL_MOBILIARIO,"Cap. Mobiliario (C/2)","Propiedad intelectual cuando el contribuyente perceptor no sea el autor."){
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitM193C2();
		}
	}
	// MODELO 193 - CLAVE C 3
	,M193_C3	(CAPITAL_MOBILIARIO,"Cap. Mobiliario (C/3)","Rendimientos derivados de la cesi\u00F3n del derecho de explotaci\u00F3n de derechos de imagen siempre que no sean en el desarrollo de una actividad econ\u00F3mica"){
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitM193C3();
		}
	}
	// MODELO 190 - CLAVE F 01
	,M190_F_01	(TRABAJO,"Trabajo (F/01)","Premios literarios, art\u00EDsticos o cient\u00EDficos no exentos de IRPF, cuando tengan la consideraci\u00F3n de rendimientos del trabajo."){
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitM190F01();
		}
	}
	,M190_F_02_1(TRABAJO,"Cursos (F/02)","Cursos, conferencias, seminarios."){
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitM190F021();
		}
	}
	,M190_F_02_2(TRABAJO,"Elab. obras (F/02)","Elaboraci\u00F3n de obras literarias, art\u00EDsticas o cient\u00EDficas."){
		@Override 
		public <T> T visit(WithholdingTypeVisitor<T> visitor) {
			return visitor.visitM190F022();
		}
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
	
	public static Optional<WithholdingType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<WithholdingType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= WithholdingType.values().length) return Optional.empty();
		return Optional.of(WithholdingType.values()[i]);
	}
	
	public static Optional<WithholdingType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
	
	public static final WithholdingType[] ORDERED_VALUES = new WithholdingType[] { 
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
	
	public static Byte value(WithholdingType t) {
		return t == null ? null : t.value();
	}

	public abstract <T> T visit(WithholdingTypeVisitor<T> visitor);
	public static interface WithholdingTypeVisitor<T> {
		T visitProfessional();
		T visitRenting();
		T visitMovableCapital();
		T visitFarmer();
		T visitTransportOperator();
		T visitM190G02();
		T visitM190G03();
		T visitM190H02();
		T visitM190H03();
		T visitM190I01();
		T visitM190I02();
		T visitM190J();
		T visitM190K01();
		T visitM190K03();
		T visitM190K02();
		T visitM193C1();
		T visitM193C2();
		T visitM193C3();
		T visitM190F01();
		T visitM190F021();
		T visitM190F022();
	}
}
