package com.esferalia.aon.occam.api.model.type;

import static com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage.CATALAN;
import static com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage.SPANISH;
import static com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage.BASQUE;
import static com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage.GALICIAN;
import static com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage.VALENCIAN;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.watson.util.AonStringUtils;

// Á --> \u00C1 á --> \u00E1
// É --> \u00C9 é --> \u00E9
// Í --> \u00CD í --> \u00ED
// Ó --> \u00D3 ó --> \u00F3
// Ú --> \u00DA ú --> \u00FA
// Ñ --> \u00D1 ñ --> \u00F1
// º --> \u00AA ª --> \u00BA
// ¿ --> \u00BF 
// Ç --> \u00C7	ç --> \u00E7

/**
 * 
 * http://www.agenciatributaria.es/AEAT.internet/Inicio_es_ES/_Configuracion_/_top_/Ayuda/Tablas_auxiliares_de_domicilios__provincias__municipios____/Tabla_de_Tipos_de_Vias/Tabla_de_Tipos_de_Vias.shtml
 *
 */
public enum StreetType {
	
	ACCE 	("ACCE ","AC","ACCES", CATALAN),
	ACCES	("ACCES","AC","ACCESO", SPANISH),
	ACEQ 	("ACEQ ","AE","ACEQUIA", SPANISH),
	ACERA	("ACERA","AA","ACERA", SPANISH),
	ALAM 	("ALAM ","AL","ALAMEDA", SPANISH),
	ALDAP	("ALDAP","CT","ALDAPA", BASQUE),
	ALDEA	("ALDEA","AD","ALDEA", SPANISH),
	ALQUE	("ALQUE","AQ","ALQUERIA", SPANISH),
	ALTO 	("ALTO ","AT","ALTO", SPANISH),
	ANDAD	("ANDAD","AN","ANDADOR", SPANISH),
	ANGTA	("ANGTA","AG","ANGOSTA", SPANISH),
	APDRO	("APDRO","AO","APEADERO", SPANISH),
	APTES	("APTES","AP","APARTAMENTS", CATALAN),
	APTOS	("APTOS","AP","APARTAMENTOS", SPANISH),
	ARB  	("ARB  ","AB","ARBOLEDA", SPANISH),
	ARRAL	("ARRAL","AR","ARRABAL", SPANISH),
	ARREK	("ARREK","AY","ERREKA", BASQUE),
	ARRY 	("ARRY ","AY","ARROYO", SPANISH),
	ASSEG	("ASSEG","AS","ASSEGADOR", VALENCIAN),
	ATAJO	("ATAJO","AJ","ATAJO", SPANISH),
	ATAL 	("ATAL ","AJ","ATALL", CATALAN),
	ATALL	("ATALL","AJ","ATALLO", GALICIAN),
	ATZUC	("ATZUC","AZ","ATZUCAT", CATALAN),
	AUTO 	("AUTO ","AU","AUTOPISTA", SPANISH),
	AUTOV	("AUTOV","AI","AUTOVIA", SPANISH),
	AUZO 	("AUZO ","BO","AUZO", BASQUE),
	AUZOT	("AUZOT","BA","AUZOTEGI", BASQUE),
	AUZUN	("AUZUN","BO","AUZUNEA", BASQUE),
	AV   	("AV   ","AV","AV", null),
	AVDA 	("AVDA ","AV","AVENIDA", SPANISH),
	AVGDA	("AVGDA","AV","AVINGUDA", CATALAN),
	AVIA 	("AVIA ","AI","AUTOVIA", CATALAN),
	BALNR	("BALNR","BN","BALNEARIO", SPANISH),
	BARDA	("BARDA","BA","BARRIADA", SPANISH),
	BARRI	("BARRI","BO","BARRI", CATALAN),
	BARRO	("BARRO","BO","BARRIO", SPANISH),
	BDA  	("BDA  ","BJ","BAIXADA", GALICIAN),
	BELNA	("BELNA","BE","BELENA", SPANISH),
	BIDE 	("BIDE ","VI","BIDE", BASQUE),
	BIDEB	("BIDEB","GL","BIDEBIETA", BASQUE),
	BJADA	("BJADA","BJ","BAJADA", SPANISH),
	BLOC 	("BLOC ","BL","BLOC", CATALAN),
	BLQUE	("BLQUE","BL","BLOQUE", SPANISH),
	BQLLO	("BQLLO","BQ","BARRANQUIL", SPANISH),
	BRANC	("BRANC","BR","BARRANCO", SPANISH),
	BRDLA	("BRDLA","BD","BARREDUELA", SPANISH),
	BRZAL	("BRZAL","BZ","BRAZAL", SPANISH),
	BSRIA	("BSRIA","BS","BASERRIA", BASQUE),
	BULEV	("BULEV","BV","BULEVAR", SPANISH),
	BV   	("BV   ","BV","BULEVAR", CATALAN),
	C_H 	("C.H. ","KH","CMNO HONDO", SPANISH),
	C_N 	("C.N. ","KN","CMNO NUEVO", SPANISH),
	C_V 	("C.V. ","KV","CMNO VIEJO", SPANISH),
	C   	("C/   ","C/","C/", SPANISH),
	CNADA	("C\u00D1ADA","C\u00D1","CA\u00D1ADA", SPANISH),
	CALLE	("CALLE","CL","CALLE", SPANISH),
	CAMI 	("CAMI ","CM","CAMI", CATALAN),
	CAMIN	("CAMIN","CM","CAMIN", null),
	CAMPA	("CAMPA","CP","CAMPA", SPANISH),
	CAMPG	("CAMPG","CG","CAMPING", SPANISH),
	CAMPO	("CAMPO","CP","CAMPO", SPANISH),
	CANNO	("CAN\u00D1O","K\u00D1","CANTI\u00D1O", GALICIAN),
	CANAL	("CANAL","CA","CANAL", SPANISH),
	CANT 	("CANT ","CQ","CANTON", SPANISH),
	CANTI	("CANTI","QT","CANTINA", SPANISH),
	CANTO	("CANTO","CQ","CANTO", CATALAN),
	CANTR	("CANTR","QA","CANTERA", SPANISH),
	CARRA	("CARRA","QD","CARRERADA", CATALAN),
	CARRE	("CARRE","CL","CARRER", CATALAN),
	CARRY	("CARRY","VR","CARRERANY", CATALAN),
	CASA 	("CASA ","CS","CASA", SPANISH),
	CBTIZ	("CBTIZ","CB","COBERTIZO", SPANISH),
	CCVCN	("CCVCN","CV","CIRCUNVALACION", SPANISH),
	CELLA	("CELLA","QN","CANELLA", null),
	CERRO	("CERRO","CE","CERRO", SPANISH),
	CHLET	("CHLET","CH","CHALET", SPANISH),
	CINT 	("CINT ","CI","CINTURON", SPANISH),
	CINY 	("CINY ","CI","CINYELL", CATALAN),
	CIRCU	("CIRCU","CV","CIRCUNVALACION", null),
	CJLA 	("CJLA ","CU","CALLEJUELA", SPANISH),
	CJTO 	("CJTO ","CN","CONJUNTO", SPANISH),
	CLEYA	("CLEYA","CY","CALEYA", SPANISH),
	CLLJA	("CLLJA","CJ","CALLEJA", SPANISH),
	CLLON	("CLLON","CK","CALLEJON", SPANISH),
	CLLZO	("CLLZO","KZ","CALLIZO", SPANISH),
	CLYON	("CLYON","KY","CALEYON", SPANISH),
	CMNIO 	("CM\u00D1O ","CM","CAMI\u00D1O", GALICIAN),
	CMNET	("CMNET","CM","CAMINET", CATALAN),
	CMNO 	("CMNO ","CM","CAMINO", SPANISH),
	CNLLA	("CNLLA","QN","CANELLA", SPANISH),
	CNVT 	("CNVT ","CW","CONVENT", CATALAN),
	CNVTO	("CNVTO","CW","CONVENTO", SPANISH),
	COL  	("COL  ","CO","COLONIA", SPANISH),
	COMPJ	("COMPJ","QJ","COMPLEJO", SPANISH),
	COOP 	("COOP ","KP","COOPERATIVA", SPANISH),
	COSTA	("COSTA","KO","COSTA", SPANISH),
	COSTE	("COSTE","KR","COSTERA", SPANISH),
	CRA  	("CRA  ","KA","CARRERA", SPANISH),
	CRCRO	("CRCRO","CC","CRUCEIRO", GALICIAN),
	CRLLO	("CRLLO","RL","CORRILLO", SPANISH),
	CRO  	("CRO  ","QR","CARRERO", SPANISH),
	CRRAL	("CRRAL","QL","CORRAL", SPANISH),
	CRRCI	("CRRCI","KD","CORREDORCILLO", SPANISH),
	CRRDA	("CRRDA","KD","CORREDOIRA", GALICIAN),
	CRRDE	("CRRDE","KD","CORREDERA", SPANISH),
	CRRDO	("CRRDO","KD","CORREDOR", SPANISH),
	CRRIL	("CRRIL","KL","CARRIL", SPANISH),
	CRRLO	("CRRLO","QO","CORRALILLO", SPANISH),
	CRROL	("CRROL","RR","CORRIOL", CATALAN),
	CRTIL	("CRTIL","QI","CARRETIL", SPANISH),
	CRTJO	("CRTJO","KT","CORTIJO", SPANISH),
	CSRIO	("CSRIO","CS","CASERIO", SPANISH),
	CSTAN	("CSTAN","KS","COSTANILLA", SPANISH),
	CTRA 	("CTRA ","CR","CARRETERA", SPANISH),
	CTRIN	("CTRIN","QE","CARRETERIN", SPANISH),
	CUADR	("CUADR","CD","CUADRA", SPANISH),
	CUEVA	("CUEVA","QV","CUEVA/S", SPANISH),
	CUSTA	("CUSTA","CT","CUESTA", SPANISH),
	CXON 	("CXON ","CX","CALEXON", GALICIAN),
	CZADA	("CZADA","CZ","CALZADA", SPANISH),
	CZADS	("CZADS","C\u00C7","CALZADAS", SPANISH),
	DEMAR	("DEMAR","DM","DEMARCACION", SPANISH),
	DHSA 	("DHSA ","DH","DEHESA", SPANISH),
	DISEM	("DISEM","DS","DISEMINADO", SPANISH),
	DISSE	("DISSE","DS","DISSEMINAT", CATALAN),
	DRERA	("DRERA","DR","DRE\u00C7ERA", CATALAN),
	EDIFC	("EDIFC","ED","EDIFICIO/S", SPANISH),
	EIRAD	("EIRAD","EI","EIRADO", GALICIAN),
	EMPR 	("EMPR ","ER","EMPRESA", SPANISH),
	ENTD 	("ENTD ","EP","ENTRADA", SPANISH),
	EPTZA	("EPTZA","PZ","ENPARANTZA", BASQUE),
	ERREB	("ERREB","AR","ERREBAL", BASQUE),
	ERREP	("ERREP","CR","ERREPIDE", BASQUE),
	ERRIB	("ERRIB","VG","ERRIBERA", BASQUE),
	ESC  	("ESC  ","EC","ESCALA/S", SPANISH),
	ESCA 	("ESCA ","EC","ESCALERA/S", SPANISH),
	ESCAL	("ESCAL","EW","ESCALINATA", SPANISH),
	ESLDA	("ESLDA","ES","ESPALDA", SPANISH),
	ESPIG	("ESPIG","EG","ESPIGO", SPANISH),
	ESTAC	("ESTAC","EN","ESTACIO", CATALAN),
	ESTCN	("ESTCN","EN","ESTACION", SPANISH),
	ESTDA	("ESTDA","EX","ESTRADA", SPANISH),
	ETDEA	("ETDEA","AV","ETORBIDEA", BASQUE),
	ETXAD	("ETXAD","GR","ETXADI", null),
	ETXAR	("ETXAR","CK","ETXARTE", BASQUE),
	EXPLA	("EXPLA","EZ","EXPLANADA", SPANISH),
	EXTRM	("EXTRM","EM","EXTRAMUROS", SPANISH),
	EXTRR	("EXTRR","ET","EXTRARRADIO", SPANISH),
	FALDA	("FALDA","FD","FALDA", SPANISH),
	FBRCA	("FBRCA","FC","FABRICA", SPANISH),
	FINCA	("FINCA","FN","FINCA", SPANISH),
	G_V 	("G.V. ","GV","GRAN VIA", SPANISH),
	GAIN 	("GAIN ","AT","GAIN", BASQUE),
	GALE 	("GALE ","GA","GALERIA", SPANISH),
	GLLZO	("GLLZO","GZ","GALLIZO", SPANISH),
	GORAB	("GORAB","SB","GORABIDE", BASQUE),
	GRANJ	("GRANJ","GJ","GRANJA", SPANISH),
	GRUP 	("GRUP ","GR","GRUP", CATALAN),
	GRUPO	("GRUPO","GR","GRUPO/S", SPANISH),
	GTA  	("GTA  ","GL","GLORIETA", SPANISH),
	HEGI 	("HEGI ","HG","HEGI", BASQUE),
	HIPOD	("HIPOD","HP","HIPODROMO", SPANISH),
	HIRIB	("HIRIB","AV","HIRIBIDEA", SPANISH),
	HONDA	("HONDA","PY","HONDARTZA", BASQUE),
	HOYA 	("HOYA ","HY","HOYA", SPANISH),
	ILLA 	("ILLA ","IL","ILLA", CATALAN),
	INDA 	("INDA ","IN","INDA", SPANISH),
	JARD 	("JARD ","JR","JARDI", CATALAN),
	JDIN 	("JDIN ","JR","JARDIN", SPANISH),
	JDINS	("JDINS","JR","JARDINES", null),
	KAI  	("KAI  ","ML","KAI", BASQUE),
	KALE 	("KALE ","CL","KALEA", BASQUE),
	KARIK	("KARIK","CJ","KARRIK", BASQUE),
	KARRE	("KARRE","KA","KARRERA", BASQUE),
	KARRI	("KARRI","CL","KARRIKA", BASQUE),
	KOSTA	("KOSTA","KO","KOSTA", BASQUE),
	KRRIL	("KRRIL","KL","KARRIL", BASQUE),
	LAGO 	("LAGO ","LA","LAGO", SPANISH),
	LASTE	("LASTE","AJ","LASTERBIDE", BASQUE),
	LDERA	("LDERA","LD","LADERA", SPANISH),
	LEKU 	("LEKU ","LG","LEKU", BASQUE),
	LLNRA	("LLNRA","LL","LLANURA", SPANISH),
	LLOC 	("LLOC ","LG","LLOC", CATALAN),
	LOMA 	("LOMA ","LM","LOMA", SPANISH),
	LOMO 	("LOMO ","LO","LOMO", SPANISH),
	LORAK	("LORAK","JR","LORATEGIAK", null),
	LORAT	("LORAT","JR","LORATEGI", BASQUE),
	LUGAR	("LUGAR","LG","LUGAR", SPANISH),
	MALEC	("MALEC","MA","MALECON", SPANISH),
	MASIA	("MASIA","MS","MASIA/S", SPANISH),
	MAZO 	("MAZO ","MZ","MAZO", SPANISH),
	MENDI	("MENDI","MT","MENDI", BASQUE),
	MERC 	("MERC ","MC","MERCADO", SPANISH),
	MERCT	("MERCT","MC","MERCAT", CATALAN),
	MIRAD	("MIRAD","MD","MIRADOR", SPANISH),
	MOLL 	("MOLL ","ML","MOLL", CATALAN),
	MONTE	("MONTE","MT","MONTE", SPANISH),
	MRDOR	("MRDOR","MD","MIRADOR", SPANISH),
	MTRIO	("MTRIO","MO","MONASTERIO", SPANISH),
	MUELL	("MUELL","ML","MUELLE", SPANISH),
	NAVE 	("NAVE ","NV","NAVE/S", SPANISH),
	NCLEO	("NCLEO","UN","NUCLEO", SPANISH),
	NUDO 	("NUDO ","ND","NUDO", SPANISH),
	ONDA 	("ONDA ","PY","ONDARTZA", BASQUE),
	PAGO 	("PAGO ","PP","PAGO", SPANISH),
	PALAC	("PALAC","PC","PALACIO", SPANISH),
	PANT 	("PANT ","P ","PANTANO", SPANISH),
	PARC 	("PARC ","PQ","PARC", CATALAN),
	PARKE	("PARKE","PQ","PARKE", BASQUE),
	PARTI	("PARTI","PF","PARTICULAR", SPANISH),
	PAS  	("PAS  ","PA","PAS", SPANISH),
	PASAI	("PASAI","PJ","PASAIA", BASQUE),
	PASEA	("PASEA","PS","PASEABIDE", BASQUE),
	PASEO	("PASEO","PS","PASEO", SPANISH),
	PASSE	("PASSE","PS","PASSEIG", CATALAN),
	PATIO	("PATIO","PK","PATIO", SPANISH),
	PBDO 	("PBDO ","PB","POBLADO", SPANISH),
	PBLO 	("PBLO ","PB","PUEBLO", SPANISH),
	PDA  	("PDA  ","PV","PUJADA", SPANISH),
	PDIS 	("PDIS ","P\u00C7","PASSADIS", CATALAN),
	PG   	("PG   ","PG","PG", null),
	PGIND	("PGIND","PG","POLIGONO INDUST", null),
	PINAR	("PINAR","PN","PINAR", SPANISH),
	PISTA	("PISTA","PI","PISTA", SPANISH),
	PJDA 	("PJDA ","SU","PUJADA, SUBIDA", SPANISH),
	PL   	("PL   ","PL","PL", SPANISH),
	PLA  	("PLA  ","PW","PLA", null),
	PLACA	("PLA\u00C7A","PZ","PLA\u00C7A", CATALAN),
	PLAYA	("PLAYA","PY","PLAYA", SPANISH),
	PLAZA	("PLAZA","PZ","PLAZA", SPANISH),
	PLCET	("PLCET","PL","PLACETA", null),
	PLLOP	("PLLOP","PX","PASILLO", null),
	PLZLA	("PLZLA","PL","PLAZUELA", null),
	PNTE 	("PNTE ","PT","PUENTE", SPANISH),
	POLIG	("POLIG","PG","POLIGONO", SPANISH),
	PONT 	("PONT ","PT","PONT", CATALAN),
	PONTE	("PONTE","PT","PONTE", GALICIAN),
	PORT 	("PORT ","PO","PORT", CATALAN),
	PQUE 	("PQUE ","PQ","PARQUE", SPANISH),
	PRANA	("PRA\u00D1A","P\u00D1","PRACI\u00D1A", GALICIAN),
	PRAGE	("PRAGE","PE","PARATGE", CATALAN),
	PRAIA	("PRAIA","PY","PRAIA", GALICIAN),
	PRAJE	("PRAJE","PE","PARAJE", SPANISH),
	PRAXE	("PRAXE","PE","PARAXE", GALICIAN),
	PRAZA	("PRAZA","PZ","PRAZA", GALICIAN),
	PROL 	("PROL ","PR","PROLONGACION", SPANISH),
	PRTAL	("PRTAL","PH","PORTAL", SPANISH),
	PRTCO	("PRTCO","PH","PORTICO", SPANISH),
	PRZLA	("PRZLA","PL","PRAZUELA", null),
	PSAJE	("PSAJE","PJ","PASAJE", SPANISH),
	PSAXE	("PSAXE","PJ","PASAXE", GALICIAN),
	PSLLO	("PSLLO","PX","PASILLO", SPANISH),
	PSMAR	("PSMAR","PM","PASEO MARITIMO", SPANISH),
	PTA  	("PTA  ","PU","PUERTA", SPANISH),
	PTDA 	("PTDA ","PD","PARTIDA", SPANISH),
	PTGE 	("PTGE ","PJ","PASSATGE", CATALAN),
	PTILO	("PTILO","PO","PORTILLO", null),
	PTLLO	("PTLLO","PO","PUERTILO", null),
	PTO  	("PTO  ","PO","PUERTO", SPANISH),
	PZO  	("PZO  ","P\u00C7","PASADIZO", SPANISH),
	PZTA 	("PZTA ","PL","PLAZOLETA", null),
	RABAL	("RABAL","AR","RABAL", SPANISH),
	RACDA	("RACDA","RA","RACONADA",CATALAN),
	RACO 	("RACO ","RC","RACO", CATALAN),
	RAMAL	("RAMAL","RM","RAMAL", SPANISH),
	RAMPA	("RAMPA","RP","RAMPA", SPANISH),
	RAVAL	("RAVAL","AR","RAVAL", CATALAN),
	RBLA 	("RBLA ","RB","RAMBLA", CATALAN),
	RBRA 	("RBRA ","RI","RIBERA", SPANISH),
	RCDA 	("RCDA ","RN","RINCONADA", SPANISH),
	RCON 	("RCON ","RC","RINCON", SPANISH),
	RENTO	("RENTO","RT","RENTO", SPANISH),
	RESID	("RESID","RS","RESIDENCIAL", SPANISH),
	RIERA	("RIERA","AY","RIERA", CATALAN),
	RONDA	("RONDA","RD","RONDA",SPANISH),
	RTDA 	("RTDA ","RO","ROTONDA", SPANISH),
	RUA  	("RUA  ","RU","RUA", GALICIAN),
	RUELA	("RUELA","CU","RUELA", GALICIAN),
	RUERO	("RUERO","RE","RUEIRO", GALICIAN),
	SANAT	("SANAT","SA","SANATORIO", SPANISH),
	SANTU	("SANTU","ST","SANTUARIO", SPANISH),
	SARBI	("SARBI","AC","SARBIDE", BASQUE),
	SBIDA	("SBIDA","SB","SUBIDA", SPANISH),
	SECT 	("SECT ","SC","SECTOR", SPANISH),
	SEDER	("SEDER","SD","SENDER", CATALAN),
	SEDRA	("SEDRA","SR","SENDERA", SPANISH),
	SEKT 	("SEKT ","SC","SEKTORE", BASQUE),
	SEND 	("SEND ","SN","SENDERO", SPANISH),
	SENDA	("SENDA","SD","SENDA", SPANISH),
	SVTIA	("SVTIA","CM","SERVENTIA", null),
	TALDE	("TALDE","GR","TALDE", BASQUE),
	TOKI 	("TOKI ","PE","TOKI", BASQUE),
	TRANS	("TRANS","TS","TRANSITO", SPANISH),
	TRAS 	("TRAS ","TA","TRASERA", SPANISH),
	TRAV 	("TRAV ","TR","TRAVESSERA", CATALAN),
	TRRNT	("TRRNT","TO","TORRENTE", SPANISH),
	TRSSI	("TRSSI","TR","TRAVESSIA", CATALAN),
	TRVA 	("TRVA ","TR","TRAVESIA", SPANISH),
	TRVAL	("TRVAL","TV","TRANSVERSAL", SPANISH),
	URB  	("URB  ","UR","URBANIZACION", SPANISH),
	URBAT	("URBAT","UR","URBANITZACIO", CATALAN),
	URBAZ	("URBAZ","UR","URBANIZAZIO", BASQUE),
	VALLE	("VALLE","VA","VALLE", SPANISH),
	VCTE 	("VCTE ","VD","VIADUCTE", CATALAN),
	VCTO 	("VCTO ","VD","VIADUCTO", SPANISH),
	VECIN	("VECIN","VC","VECINDARIO", SPANISH),
	VEGA 	("VEGA ","VG","VEGA", SPANISH),
	VENAT	("VENAT","VE","VEINAT", SPANISH),
	VENLA	("VENLA","VN","VENELA", SPANISH),
	VIA  	("VIA  ","VI","VIA", SPANISH),
	VIAL 	("VIAL ","VL","VIAL", SPANISH),
	VIANY	("VIANY","SN","VIARANY", CATALAN),
	VILLA	("VILLA","V ","VILLA", SPANISH),
	VREDA	("VREDA","VR","VEREDA", SPANISH),
	VVDAS	("VVDAS","VV","VIVIENDAS", SPANISH),
	XDIN 	("XDIN ","JR","XARDIN", GALICIAN),
	ZEHAR	("ZEHAR","ZE","ZEARKALETA", BASQUE),
	ZONA 	("ZONA ","ZO","ZONA", SPANISH),
	ZUBI 	("ZUBI ","PT","ZUBI", BASQUE),
	ZUHAI	("ZUHAI","AB","ZUHAIZTI", BASQUE),
	ZUMAR	("ZUMAR","AL","ZUMARDI", BASQUE),
	
	XX	("XX","XX","XX", null),	// TODO ???????????????
	ZZ	("ZZ","ZZ","ZZ", null),	// TODO ???????????????
	;
	private String ineCode;
	private String aeatCode;
	private String description;
	private AonLanguage language;
	
	private StreetType(String ineCode, String aeatCode, String description, AonLanguage language) {
		this.ineCode = ineCode; 
		this.aeatCode = aeatCode;
		this.description = description; 
		this.language = language; 
	}

	public String getIneCode() {
		return ineCode;
	}

	public String getAeatCode() {
		return aeatCode;
	}

	public String getDescription() {
		return description;
	}
	
	public AonLanguage getLanguage() {
		return language;
	}
	
	
	public String getDescription(AonLanguage language) {
		if (language == null)
			return this.description;
		
		List<? extends StreetType> typesList = Arrays.stream(this.getClass().getEnumConstants())
		.filter(st -> st != null && st.getAeatCode().equals(this.aeatCode))
		.collect(Collectors.toList());
		
		if (typesList.stream().anyMatch(st -> language.equals(st.getLanguage()))) {
			StreetType type = typesList.stream().filter(st -> language.equals(st.getLanguage())).findFirst().orElse(null);
			return type != null ? type.getDescription() : "";
		} else if ((language.equals(CATALAN) || language.equals(VALENCIAN)) &&
				typesList.stream().anyMatch(st -> VALENCIAN.equals(st.getLanguage()) || CATALAN.equals(st.getLanguage())))
		{
			StreetType type = typesList.stream().filter(st -> VALENCIAN.equals(st.getLanguage()) || CATALAN.equals(st.getLanguage())).findFirst().orElse(null);
			return type != null ? type.getDescription() : "";
		} else if (typesList.stream().anyMatch(st -> SPANISH.equals(st.getLanguage()))) {
			StreetType type = typesList.stream().filter(st -> SPANISH.equals(st.getLanguage())).findFirst().orElse(null);
			return type != null ? type.getDescription() : "";
		} else if (typesList.stream().anyMatch(st -> null == st.getLanguage())) {
			StreetType type = typesList.stream().filter(st -> null == st.getLanguage()).findFirst().orElse(null);
			return type != null ? type.getDescription() : "";
		} else {
			return null;
		}
		
	}

	public static StreetType getForIneCode( String ineCode ) {
		if (ineCode != null) {
			for (StreetType st : StreetType.values()) {
				if (st.getIneCode().equals(ineCode)){
					return st;
				}
			}
		}
		return null;
	}
	
	public static StreetType getForAeatCode( String aeatCode, AonLanguage lenguage ) {
		if (aeatCode != null) {
			for (StreetType st : StreetType.values()) {
				if ((st.getAeatCode() != null && st.getAeatCode().equals(aeatCode)) && (st.getLanguage() != null && st.getLanguage().equals(lenguage))){
					return st;
				}
			}
		}
		return safeValueOf(aeatCode);
	}
	
	public static StreetType safeValueOf( String value ) {
		if (AonStringUtils.isBlank(value)) {
			return null;
		}
		for (StreetType st : StreetType.values()) {
			if (AonStringUtils.equalsIgnoreCase(value, st.getAeatCode())
			 || AonStringUtils.equalsIgnoreCase(value, st.getIneCode())
			 || AonStringUtils.equalsIgnoreCase(value, st.getDescription())) {
				return st;
			}
		}
		return null;
	}
	
}
