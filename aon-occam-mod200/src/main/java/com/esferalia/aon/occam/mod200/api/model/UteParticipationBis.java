package com.esferalia.aon.occam.mod200.api.model;

import java.io.Serializable;

public class UteParticipationBis implements Serializable {

	private static final long serialVersionUID = -332936877303579815L;
	
	private String document; // Datos de la participada: NIF (o equivalente al NIF del país de residencia, si no tiene NIF en España)
	private String name;     // Datos de la participada: Nombre o razón social
	private int province;    // Datos de la participada: Código provincia
	private String country;  // Datos de la participada: Código país
	private int entityType;  // Datos de la participada: Tipo de entidad: 0-No aplicable, 1-Agrupación de interés económico española, 2-Agrupación europea de interés económico, 3-Unión temporal de empresas, 4-Colaboraciones en el extranjero análogas a las uniones temporales
	private int imputationCriteria; // Criterio de imputación art. 46.2 LIS: 0-No aplicable, 1-En la fecha de finalización del periodo impositivo de la entidad, 2-En el siguiente periodo impositivo
	
	private double c01279;  // Datos relativos a la participación: Valoración de la participación al comienzo del período impositivo                                                                                                       
	private double c01455;  // Datos relativos a la participación: Valoración de la participación al final del período impositivo                                                                                                          
	private double c01456;  // Datos relativos a la participación: Ingresos financieros de la participación                                                                                                                                
	private double c01458;  // Importes imputados: Importe del resultado contable imputado                                                                                                                                                 
	private double c01459;  // Importes imputados: Gastos financieros netos imputados                                                                                                                                                      
	private double c01460;  // Importes imputados: Reserva de capitalización que no haya sido aplicada imputada                                                                                                                            
	private double c01461;  // Importes imputados: Base imponible imputada                                                                                                                                                                 
	private double c01467;  // Importes imputados: Importe de la deducción generada por bases de deducción para evitar la doble imposición imputadas                                                                                       
	private double c01468;  // Importes imputados: Importe bonificación generada de las bases de bonificación imputadas                                                                                                                    
	private double c01523;  // Importes imputados: Importe de la deducción generada por activos fijos por bases de deducción por inversión en Canarias imputadas                                                                           
	private double c01601;  // Importes imputados: Importe de la deducción generada de investigación y desarrollo e innovación tecnológica por bases de deducción por inversión en Canarias imputadas                                      
	private double c01638;  // Importes imputados: Importe de la deducción generada de producciones cinematográficas españolas y espectáculos en vivo de artes escénicas y musicales por deducciones por inversión en Canarias imputadas   
	private double c01639;  // Importes imputados: Importe de la deducción generada del resto de deducciones por inversión en Canarias imputadas                                                                                           
	private double c01640;  // Importes imputados: Importe de la deducción generada de investigación y desarrollo e innovación tecnológica por bases de deducción imputadas                                                                
	private double c01743;  // Importes imputados: Importe de la deducción generada de producciones cinematográficas españolas y espectáculos en vivo de artes escénicas y musicales por bases de deducción imputadas                      
	private double c01909;  // Importes imputados: Importe del resto de deducciones generadas para incentivar determinadas actividades por bases de deducción imputadas                                                                    
	private double c01910;  // Importes imputados: Importe del resto de deducciones generadas por bases de deducción imputadas no mencionadas anteriormente                                                                                
	private double c01911;  // Importes imputados: Retenciones e ingresos a cuenta imputados                                                                                                                                               
	private double c01912;  // Importes imputados: Dividendos y participaciones en beneficios percibidos procedentes de ejercicios anteriores a la adquisición de la participación                                                         
	private double c01934;  // Importes imputados: Dividendos y participaciones en beneficios percibidos procedentes de ejercicios posteriores a la adquisición de la participación                                                        

	public String getDocument() {
		return document;
	}

	public UteParticipationBis setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getName() {
		return name;
	}

	public UteParticipationBis setName(String name) {
		this.name = name;
		return this;
	}

	public int getProvince() {
		return province;
	}

	public UteParticipationBis setProvince(int province) {
		this.province = province;
		return this;
	}
	public String getCountry() {
		return country;
	}

	public UteParticipationBis setCountry(String country) {
		this.country = country;
		return this;
	}

	public int getEntityType() {
		return entityType;
	}

	public UteParticipationBis setEntityType(int entityType) {
		this.entityType = entityType;
		return this;
	}

	public int getImputationCriteria() {
		return imputationCriteria;
	}

	public UteParticipationBis setImputationCriteria(int imputationCriteria) {
		this.imputationCriteria = imputationCriteria;
		return this;
	}

	public double getC01279() {
		return c01279;
	}

	public UteParticipationBis setC01279(double c01279) {
		this.c01279 = c01279;
		return this;
	}

	public double getC01455() {
		return c01455;
	}

	public UteParticipationBis setC01455(double c01455) {
		this.c01455 = c01455;
		return this;
	}

	public double getC01456() {
		return c01456;
	}

	public UteParticipationBis setC01456(double c01456) {
		this.c01456 = c01456;
		return this;
	}

	public double getC01458() {
		return c01458;
	}

	public UteParticipationBis setC01458(double c01458) {
		this.c01458 = c01458;
		return this;
	}

	public double getC01459() {
		return c01459;
	}

	public UteParticipationBis setC01459(double c01459) {
		this.c01459 = c01459;
		return this;
	}

	public double getC01460() {
		return c01460;
	}

	public UteParticipationBis setC01460(double c01460) {
		this.c01460 = c01460;
		return this;
	}

	public double getC01461() {
		return c01461;
	}

	public UteParticipationBis setC01461(double c01461) {
		this.c01461 = c01461;
		return this;
	}

	public double getC01467() {
		return c01467;
	}

	public UteParticipationBis setC01467(double c01467) {
		this.c01467 = c01467;
		return this;
	}

	public double getC01468() {
		return c01468;
	}

	public UteParticipationBis setC01468(double c01468) {
		this.c01468 = c01468;
		return this;
	}

	public double getC01523() {
		return c01523;
	}

	public UteParticipationBis setC01523(double c01523) {
		this.c01523 = c01523;
		return this;
	}

	public double getC01601() {
		return c01601;
	}

	public UteParticipationBis setC01601(double c01601) {
		this.c01601 = c01601;
		return this;
	}

	public double getC01638() {
		return c01638;
	}

	public UteParticipationBis setC01638(double c01638) {
		this.c01638 = c01638;
		return this;
	}

	public double getC01639() {
		return c01639;
	}

	public UteParticipationBis setC01639(double c01639) {
		this.c01639 = c01639;
		return this;
	}

	public double getC01640() {
		return c01640;
	}

	public UteParticipationBis setC01640(double c01640) {
		this.c01640 = c01640;
		return this;
	}

	public double getC01743() {
		return c01743;
	}

	public UteParticipationBis setC01743(double c01743) {
		this.c01743 = c01743;
		return this;
	}

	public double getC01909() {
		return c01909;
	}

	public UteParticipationBis setC01909(double c01909) {
		this.c01909 = c01909;
		return this;
	}

	public double getC01910() {
		return c01910;
	}

	public UteParticipationBis setC01910(double c01910) {
		this.c01910 = c01910;
		return this;
	}

	public double getC01911() {
		return c01911;
	}

	public UteParticipationBis setC01911(double c01911) {
		this.c01911 = c01911;
		return this;
	}

	public double getC01912() {
		return c01912;
	}

	public UteParticipationBis setC01912(double c01912) {
		this.c01912 = c01912;
		return this;
	}

	public double getC01934() {
		return c01934;
	}

	public UteParticipationBis setC01934(double c01934) {
		this.c01934 = c01934;
		return this;
	}
	
}
