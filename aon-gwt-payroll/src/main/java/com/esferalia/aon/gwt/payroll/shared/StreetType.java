package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public enum StreetType implements Serializable {
	AQ ("AQ","Acequia"),
	AC ("AC","Acera"),
	AL ("AL","Alameda"),
	AD ("AD","Aldea"),
	AM ("AM","Ampliacion"),
	AN ("AN","Angosta"),
	AP ("AP","Apartamentos"),
	AT ("AT","Atajo"),
	AV ("AV","Avenida"),
	BA ("BA","Bajada"),
	BC ("BC","Barranco"),
	BD ("BD","Barriada"),
	BO ("BO","Barrio"),
	BL ("BL","Bloques"),
	CL ("CL","Calle"),
	CA ("CA","Calleja"),
	CJ ("CJ","Callejon"),
	CE ("CE","Callejuela"),
	CZ ("CZ","Calzada"),
	CM ("CM","Camino"),
	CR ("CR","Carrera"),
	CT ("CT","Carretera"),
	CS ("CS","Caserio"),
	CH ("CH","Chalet"),
	CO ("CO","Colonia"),
	CP ("CP","Cooperativa"),
	KO ("KO","Corral"),
	CN ("CN","Costanilla"),
	CU ("CU","Cuesta"),
	ED ("ED","Edificio"),
	EA ("EA","Escala"),
	ES ("ES","Escalera"),
	EL ("EL","Escalinata"),
	ET ("ET","Estrada"),
	GL ("GL","Glorieta"),
	GR ("GR","Grupo"),
	LL ("LL","Llano"),
	LG ("LG","Lugar"),
	MZ ("MZ","Manzana"),
	MC ("MC","Mercado"),
	MO ("MO","Montaña"),
	MN ("MN","Municipio"),
	ZZ ("ZZ","Otros"),
	PQ ("PQ","Parque"),
	PC ("PC","Particular"),
	PD ("PD","Pasadizo"),
	PJ ("PJ","Pasaje"),
	PA ("PA","Paseo"),
	PO ("PO","Paseo bajo"),
	PI ("PI","Pasillo"),
	PS ("PS","Paso"),
	PT ("PT","Patio"),
	PL ("PL","Placeta"),
	PZ ("PZ","Plaza"),
	PE ("PE","Plazoleta"),
	PU ("PU","Plazuela"),
	PB ("PB","Poblado"),
	PG ("PG","Poligono"),
	PR ("PR","Portales"),
	PV ("PV","Privada"),
	PN ("PN","Prolongacion"),
	RA ("RA","Ramal"),
	RB ("RB","Rambla"),
	RP ("RP","Rampa"),
	RR ("RR","Rivera"),
	RN ("RN","Rincon"),
	RC ("RC","Rinconada"),
	RD ("RD","Ronda"),
	SC ("SC","Sector"),
	SD ("SD","Senda"),
	SR ("SR","Sendero"),
	XX ("XX","Sin datos"),
	SU ("SU","Subida"),
	TT ("TT","Torrente"),
	TL ("TL","Transversal"),
	TS ("TS","Trasara"),
	TR ("TR","Travesia"),
	UR ("UR","Urbanizacion"),
	VI ("VI","Via"),
	VL ("VL","Villas"),
	ZO ("ZO","Zona");
	
	private String shortCode;
	private String description;
	
	private StreetType(String shortCode, String description){
		this.shortCode = shortCode;
		this.description = description;
	}
	
	public String getShortCode(){
		return this.shortCode;
	}
	
	public String getDescription(){
		return this.description;
	}
}
