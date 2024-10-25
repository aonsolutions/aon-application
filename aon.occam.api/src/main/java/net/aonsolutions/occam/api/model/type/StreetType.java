package net.aonsolutions.occam.api.model.type;

import static net.aonsolutions.occam.api.model.type.AonLanguage.BASQUE;
import static net.aonsolutions.occam.api.model.type.AonLanguage.CATALAN;
import static net.aonsolutions.occam.api.model.type.AonLanguage.GALICIAN;
import static net.aonsolutions.occam.api.model.type.AonLanguage.SPANISH;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

/**
 * 
 * http://www.agenciatributaria.es/AEAT.internet/Inicio_es_ES/_Configuracion_/_top_/Ayuda/Tablas_auxiliares_de_domicilios__provincias__municipios____/Tabla_de_Tipos_de_Vias/Tabla_de_Tipos_de_Vias.shtml
 *
 */
public enum StreetType implements Serializable {
	
	AC	("ACCES","ACCESO", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"ACCE ","ACCES"),
		new StreeTypeI18N( BASQUE,"SARBI","SARBIDE"),
	}),
	AE	("ACEQ ","ACEQUIA"),
	AA	("ACERA","ACERA"),
	AL	("ALAM ","ALAMEDA", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"ZUMAR","ZUMARDI"),
	}),
	AD	("ALDEA","ALDEA"),
	AQ	("ALQUE","ALQUERIA"),
	AT	("ALTO ","ALTO", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"GAIN ","GAIN"),
	}),
	AN	("ANDAD","ANDADOR"),
	AG	("ANGTA","ANGOSTA"),
	AO	("APDRO","APEADERO"),
	AP	("APTOS","APARTAMENTOS", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"APTES","APARTAMENTS"),
	}),
	AB	("ARB  ","ARBOLEDA", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"ZUHAI","ZUHAIZTI"),
	}),
	AR	("ARRAL","ARRABAL", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"ERREB","ERREBAL"),
		new StreeTypeI18N( SPANISH,"RABAL","RABAL"),
		new StreeTypeI18N( CATALAN,"RAVAL","RAVAL"),
	}),
	AY	("ARRY ","ARROYO", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"ARREK","ERREKA"),
		new StreeTypeI18N( CATALAN,"RIERA","RIERA"),
	}),
	AJ	("ATAJO","ATAJO", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"ATAL ","ATALL"),
		new StreeTypeI18N( GALICIAN,"ATALL","ATALLO"),
		new StreeTypeI18N( BASQUE,"LASTE","LASTERBIDE"),
	}),
	AU	("AUTO ","AUTOPISTA"),
	AI	("AUTOV","AUTOVIA", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"AVIA ","AUTOVIA"),
	}),
	AV	("AVDA ","AVENIDA", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"AVGDA","AVINGUDA"),
		new StreeTypeI18N( BASQUE,"ETDEA","ETORBIDEA"),
		new StreeTypeI18N( BASQUE,"HIRIB","HIRIBIDEA"),
	}),
	BN	("BALNR","BALNEARIO"),
	BA	("BARDA","BARRIADA", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"AUZOT","AUZOTEGI"),
	}),
	BO	("BARRO","BARRIO", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"AUZO ","AUZO"),
		new StreeTypeI18N( BASQUE,"AUZUN","AUZUNEA"),
		new StreeTypeI18N( CATALAN,"BARRI","BARRI"),
	}),
	BE	("BELNA","BELENA"),
	BJ	("BJADA","BAJADA", new StreeTypeI18N[] {
		new StreeTypeI18N( GALICIAN,"BDA  ","BAIXADA"),
	}),
	BL	("BLQUE","BLOQUE", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"BLOC ","BLOC"),
	}),
	BQ	("BQLLO","BARRANQUIL"),
	BR	("BRANC","BARRANCO"),
	BD	("BRDLA","BARREDUELA"),
	BZ	("BRZAL","BRAZAL"),
	BV	("BULEV","BULEVAR", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"BV   ","BULEVAR"),
	}),
	KH	("C.H. ","CMNO HONDO"),
	KN	("C.N. ","CMNO NUEVO"),
	KV	("C.V. ","CMNO VIEJO"),
	CÑ	("CÑADA","CAÑADA"),
	CL	("CALLE","CALLE", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"CARRE","CARRER"),
		new StreeTypeI18N( BASQUE,"KALE ","KALEA"),
		new StreeTypeI18N( BASQUE,"KARRI","KARRIKA"),
	}),
	CP	("CAMPA","CAMPA", new StreeTypeI18N[] {
		new StreeTypeI18N( SPANISH,"CAMPO","CAMPO"),
	}),
	CG	("CAMPG","CAMPING"),
	CA	("CANAL","CANAL"),
	CQ	("CANT ","CANTON", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"CANTO","CANTO"),
	}),
	QT	("CANTI","CANTINA"),
	QA	("CANTR","CANTERA"),
	CS	("CASA ","CASA", new StreeTypeI18N[] {
		new StreeTypeI18N( SPANISH,"CSRIO","CASERIO"),
	}),
	CB	("CBTIZ","COBERTIZO"),
	CV	("CCVCN","CIRCUNVALACION"),
	CE	("CERRO","CERRO"),
	CH	("CHLET","CHALET"),
	CI	("CINT ","CINTURON", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"CINY ","CINYELL"),
	}),
	CU	("CJLA ","CALLEJUELA", new StreeTypeI18N[] {
		new StreeTypeI18N( GALICIAN,"RUELA","RUELA"),
	}),
	CN	("CJTO ","CONJUNTO"),
	CY	("CLEYA","CALEYA"),
	CJ	("CLLJA","CALLEJA", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"KARIK","KARRIK"),
	}),
	CK	("CLLON","CALLEJON", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"ETXAR","ETXARTE"),
	}),
	KZ	("CLLZO","CALLIZO"),
	KY	("CLYON","CALEYON"),
	CM	("CMNO ","CAMINO", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"CAMI ","CAMI"),
		new StreeTypeI18N( GALICIAN,"CMÑO ","CAMIÑO"),
		new StreeTypeI18N( CATALAN,"CMNET","CAMINET"),
	}),
	QN	("CNLLA","CANELLA"),
	CW	("CNVTO","CONVENTO", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"CNVT ","CONVENT"),
	}),
	CO	("COL  ","COLONIA"),
	QJ	("COMPJ","COMPLEJO"),
	KP	("COOP ","COOPERATIVA"),
	KO	("COSTA","COSTA", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"KOSTA","KOSTA"),
	}),
	KR	("COSTE","COSTERA"),
	KA	("CRA  ","CARRERA", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"KARRE","KARRERA"),
	}),
	RL	("CRLLO","CORRILLO"),
	QR	("CRO  ","CARRERO"),
	QL	("CRRAL","CORRAL"),
	KD	("CRRDO","CORREDOR", new StreeTypeI18N[] {
		new StreeTypeI18N( SPANISH,"CRRCI","CORREDORCILLO"),
		new StreeTypeI18N( GALICIAN,"CRRDA","CORREDOIRA"),
		new StreeTypeI18N( SPANISH,"CRRDE","CORREDERA"),
	}),
	KL	("CRRIL","CARRIL", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"KRRIL","KARRIL"),
	}),
	QO	("CRRLO","CORRALILLO"),
	QI	("CRTIL","CARRETIL"),
	KT	("CRTJO","CORTIJO"),
	KS	("CSTAN","COSTANILLA"),
	CR	("CTRA ","CARRETERA", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"ERREP","ERREPIDE"),
	}),
	QE	("CTRIN","CARRETERIN"),
	CD	("CUADR","CUADRA"),
	QV	("CUEVA","CUEVA/S"),
	CT	("CUSTA","CUESTA", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"ALDAP","ALDAPA"),
	}),
	CZ	("CZADA","CALZADA"),
	CÇ	("CZADS","CALZADAS"),
	DM	("DEMAR","DEMARCACION"),
	DH	("DHSA ","DEHESA"),
	DS	("DISEM","DISEMINADO", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"DISSE","DISSEMINAT"),
	}),
	ED	("EDIFC","EDIFICIO/S"),
	ER	("EMPR ","EMPRESA"),
	EP	("ENTD ","ENTRADA"),
	EC	("ESCA ","ESCALERA/S", new StreeTypeI18N[] {
		new StreeTypeI18N( SPANISH,"ESC  ","ESCALA/S"),
	}),
	EW	("ESCAL","ESCALINATA"),
	ES	("ESLDA","ESPALDA"),
	EG	("ESPIG","ESPIGO"),
	EN	("ESTCN","ESTACION", new StreeTypeI18N[] {
	new StreeTypeI18N( CATALAN,"ESTAC","ESTACIO"),
	}),
	EX	("ESTDA","ESTRADA"),
	EZ	("EXPLA","EXPLANADA"),
	EM	("EXTRM","EXTRAMUROS"),
	ET	("EXTRR","EXTRARRADIO"),
	FD	("FALDA","FALDA"),
	FC	("FBRCA","FABRICA"),
	FN	("FINCA","FINCA"),
	GV	("G.V. ","GRAN VIA"),
	GA	("GALE ","GALERIA"),
	GZ	("GLLZO","GALLIZO"),
	GJ	("GRANJ","GRANJA"),
	GR	("GRUPO","GRUPO/S", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"GRUP ","GRUP"),
		new StreeTypeI18N( BASQUE,"TALDE","TALDE"),
	}),
	GL	("GTA  ","GLORIETA", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"BIDEB","BIDEBIETA"),
	}),
	HP	("HIPOD","HIPODROMO"),
	HY	("HOYA ","HOYA"),
	IN	("INDA ","INDA"),
	JR	("JDIN ","JARDIN", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"JARD ","JARDI"),
		new StreeTypeI18N( BASQUE,"LORAT","LORATEGI"),
		new StreeTypeI18N( GALICIAN,"XDIN ","XARDIN"),
	}),
	LA	("LAGO ","LAGO"),
	LD	("LDERA","LADERA"),
	LL	("LLNRA","LLANURA"),
	LM	("LOMA ","LOMA"),
	LO	("LOMO ","LOMO"),
	LG	("LUGAR","LUGAR", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"LEKU ","LEKU"),
		new StreeTypeI18N( CATALAN,"LLOC ","LLOC"),
	}),
	MA	("MALEC","MALECON"),
	MS	("MASIA","MASIA/S"),
	MZ	("MAZO ","MAZO"),
	MC	("MERC ","MERCADO", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"MERCT","MERCAT"),
	}),
	MD	("MIRAD","MIRADOR", new StreeTypeI18N[] {
		new StreeTypeI18N( SPANISH,"MRDOR","MIRADOR"),
	}),
	MT	("MONTE","MONTE", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"MENDI","MENDI"),
	}),
	MO	("MTRIO","MONASTERIO"),
	ML	("MUELL","MUELLE", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"KAI  ","KAI"),
		new StreeTypeI18N( CATALAN,"MOLL ","MOLL"),
	}),
	NV	("NAVE ","NAVE/S"),
	UN	("NCLEO","NUCLEO"),
	ND	("NUDO ","NUDO"),
	PP	("PAGO ","PAGO"),
	PC	("PALAC","PALACIO"),
	P 	("PANT ","PANTANO"),
	PF	("PARTI","PARTICULAR"),
	PA	("PAS  ","PAS"),
	PS	("PASEO","PASEO", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"PASEA","PASEABIDE"),
		new StreeTypeI18N( CATALAN,"PASSE","PASSEIG"),
	}),
	PK	("PATIO","PATIO"),
	PB	("PBLO ","PUEBLO", new StreeTypeI18N[] {
		new StreeTypeI18N( SPANISH,"PBDO ","POBLADO"),
	}),
	PV	("PDA  ","PUJADA"),
	PN	("PINAR","PINAR"),
	PI	("PISTA","PISTA"),
	SU	("PJDA ","PUJADA, SUBIDA"),
	PL	("PL   ","PL"),
	PY	("PLAYA","PLAYA", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"HONDA","HONDARTZA"),
		new StreeTypeI18N( BASQUE,"ONDA ","ONDARTZA"),
		new StreeTypeI18N( GALICIAN,"PRAIA","PRAIA"),
	}),
	PZ	("PLAZA","PLAZA", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"EPTZA","ENPARANTZA"),
		new StreeTypeI18N( CATALAN,"PLAÇA","PLAÇA"),
		new StreeTypeI18N( GALICIAN,"PRAZA","PRAZA"),
	}),
	PT	("PNTE ","PUENTE", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"PONT ","PONT"),
		new StreeTypeI18N( GALICIAN,"PONTE","PONTE"),
		new StreeTypeI18N( BASQUE,"ZUBI ","ZUBI"),
	}),
	PG	("POLIG","POLIGONO"),
	PQ	("PQUE ","PARQUE", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"PARC ","PARC"),
		new StreeTypeI18N( BASQUE,"PARKE","PARKE"),
	}),
	PE	("PRAJE","PARAJE", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"PRAGE","PARATGE"),
		new StreeTypeI18N( GALICIAN,"PRAXE","PARAXE"),
		new StreeTypeI18N( BASQUE,"TOKI ","TOKI"),
	}),
	PR	("PROL ","PROLONGACION"),
	PH	("PRTAL","PORTAL", new StreeTypeI18N[] {
		new StreeTypeI18N( SPANISH,"PRTCO","PORTICO"),
	}),
	PJ	("PSAJE","PASAJE", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"PASAI","PASAIA"),
		new StreeTypeI18N( GALICIAN,"PSAXE","PASAXE"),
		new StreeTypeI18N( CATALAN,"PTGE ","PASSATGE"),
	}),
	PX	("PSLLO","PASILLO"),
	PM	("PSMAR","PASEO MARITIMO"),
	PU	("PTA  ","PUERTA"),
	PD	("PTDA ","PARTIDA"),
	PO	("PTO  ","PUERTO", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"PORT ","PORT"),
	}),
	PÇ	("PZO  ","PASADIZO", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"PDIS ","PASSADIS"),
	}),
	RM	("RAMAL","RAMAL"),
	RP	("RAMPA","RAMPA"),
	RI	("RBRA ","RIBERA"),
	RN	("RCDA ","RINCONADA"),
	RC	("RCON ","RINCON", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"RACO ","RACO"),
	}),
	RT	("RENTO","RENTO"),
	RS	("RESID","RESIDENCIAL"),
	RD	("RONDA","RONDA"),
	RO	("RTDA ","ROTONDA"),
	SA	("SANAT","SANATORIO"),
	ST	("SANTU","SANTUARIO"),
	SB	("SBIDA","SUBIDA", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"GORAB","GORABIDE"),
	}),
	SC	("SECT ","SECTOR", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"SEKT ","SEKTORE"),
	}),
	SR	("SEDRA","SENDERA"),
	SN	("SEND ","SENDERO", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"VIANY","VIARANY"),
	}),
	SD	("SENDA","SENDA", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"SEDER","SENDER"),
	}),
	TS	("TRANS","TRANSITO"),
	TA	("TRAS ","TRASERA"),
	TO	("TRRNT","TORRENTE"),
	TR	("TRVA ","TRAVESIA", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"TRAV ","TRAVESSERA"),
		new StreeTypeI18N( CATALAN,"TRSSI","TRAVESSIA"),
	}),
	TV	("TRVAL","TRANSVERSAL"),
	UR	("URB  ","URBANIZACION", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"URBAT","URBANITZACIO"),
		new StreeTypeI18N( BASQUE,"URBAZ","URBANIZAZIO"),
	}),
	VA	("VALLE","VALLE"),
	VD	("VCTO ","VIADUCTO", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"VCTE ","VIADUCTE"),
	}),
	VC	("VECIN","VECINDARIO"),
	VG	("VEGA ","VEGA", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"ERRIB","ERRIBERA"),
	}),
	VE	("VENAT","VEINAT"),
	VN	("VENLA","VENELA"),
	VI	("VIA  ","VIA", new StreeTypeI18N[] {
		new StreeTypeI18N( BASQUE,"BIDE ","BIDE"),
	}),
	VL	("VIAL ","VIAL"),
	V 	("VILLA","VILLA"),
	VR	("VREDA","VEREDA", new StreeTypeI18N[] {
		new StreeTypeI18N( CATALAN,"CARRY","CARRERANY"),
	}),
	VV	("VVDAS","VIVIENDAS"),
	ZO	("ZONA ","ZONA"),
	XX	("XX","XX"),
	ZZ	("ZZ","ZZ"),
	;
	
	public static record StreeTypeI18N( AonLanguage language, String ineCode, String description ) {};
	
	private String ineCode;
	private String description;
	private LinkedList<StreeTypeI18N> i18ns;
	
	private StreetType(String ineCode, String description) {
		this( ineCode, description, null);
	}
	private StreetType(String ineCode, String description, StreeTypeI18N[] i18ns) {
		this.ineCode = ineCode; 
		this.description = description;
		this.i18ns = new LinkedList<>();
		this.i18ns.add(new StreeTypeI18N( SPANISH,ineCode,description));
		AonCollectionUtils.stream(i18ns)
			.forEach( i -> this.i18ns.add(i) );
	}

	public String getIneCode() {
		return ineCode;
	}
	public String getDescription() {
		return description;
	}
	
	public String value() {
		return name();
	}
	
	public static Optional<StreetType> value( String s ) {
		String code = ("C/".equals(s))?"CL":s; 
		return AonCollectionUtils.stream(values())
			.filter( t -> AonStringUtils.equalsIgnoreCase(t.name(), code))
			.findFirst();
	}
	
	public Optional<StreeTypeI18N> i18n( AonLanguage language ) {
		return AonCollectionUtils.stream( this.i18ns )
			.filter	( i -> i.language  == language )
			.findFirst();
	}
	
	public static String value( StreetType t ) {
		return t == null ? null : t.name();
	}
}
