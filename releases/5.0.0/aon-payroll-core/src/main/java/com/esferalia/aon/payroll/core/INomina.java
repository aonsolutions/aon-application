package com.esferalia.aon.payroll.core;

import java.io.Serializable;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.payroll.core.enumeration.TipoNomina;

public interface INomina extends Serializable{

	IEmpleado getEmpleado();
	void setEmpleado(IEmpleado empleado);
	
	// Mes de la nomina
	Month getMes();
	void setMes(Month mes);
	
	// Año de la nomina
	Integer getYear();
	void setYear(Integer year);
	
	//Tipo de nomina.
	TipoNomina getTipo();
	void setTipo(TipoNomina salaryType);
	
	//Dias nomina.
	Integer getDiasNomina();
	void setDiasNomina(Integer diasNomina);

	// Base Contingencias Generales
	Double getBaseContingenciasGenerales();
	void setBaseContingenciasGenerales(Double baseContingenciasGenerales);

	// Accidentes Trabajo - Enfermedad Profesional
	Double getBaseAccidentesTrabajo();
	void setBaseAccidentesTrabajo(Double baseAccidentes);

	// Accidentes Trabajo sin Horas Extras
	Double getBaseAccidentesTrabajoSinHorasExtras();
	void setBaseAccidentesTrabajoSinHorasExtras(Double baseAccidentesTrabajoSinHorasExtras);
	
	//Horas Estras Estructurales
	Double getBaseHorasExtrasEstructurales();
	void setBaseHorasExtrasEstrcturales(Double baseHorasExtrasEstrcturales);

	//Horas Estras No Estructurales
	Double getBaseHorasExtrasNoEstructurales();
	void setBaseHorasExtrasNoEstrcturales(Double baseHorasExtrasNoEstrcturales);
	
	
	/*
Self.cdg                =     0;    // INTEGER      NOT NULL LABEL "Codigo de Nomina",
** Self.numero             =     0;    // INTEGER      NOT NULL LABEL "Numero Empresa Persona",
** Self.mes                =     0;    // SMALLINT     NOT NULL LABEL "Mes de Nomina",
** Self.anio               =     0;    // SMALLINT     NOT NULL LABEL "Ano de Nomina",
Self.orden              =     0;    // SMALLINT     NOT NULL LABEL "Secuencial dentro del Mes - Ano",
** Self.tipo               =     0;    // CHAR(1)      NOT NULL DEFAULT "N" UPSHIFT LABEL "Tipo de Nomina",
Self.nomemp             =     0;    // CHAR(60)     NOT NULL UPSHIFT LABEL "Nombre de Empresa",
Self.fecemi             =     today;// DATE         NOT NULL DEFAULT TODAY LABEL "Fecha de Emision",
Self.nomper             =     0;    // CHAR(60)     NOT NULL UPSHIFT LABEL "Nombre de Persona",
Self.direccion          =     0;    // CHAR(60)     NOT NULL UPSHIFT LABEL "Datos Direccion",
Self.localidad          =     0;    // CHAR(60)     NOT NULL UPSHIFT LABEL "Datos Localidad",
Self.descat             =     0;    // CHAR(35)     NOT NULL UPSHIFT LABEL "Descripcion Categoria",
Self.profesion          =     0;    // CHAR(25)     NOT NULL LABEL "Profesion",
Self.nummat             =     0;    // SMALLINT     DEFAULT 0 LABEL "Numero de Matricula",
Self.fecant             =     today;// DATE         NOT NULL LABEL "Fecha de Antiguedad",
Self.fecini             =     today;// DATE         NOT NULL LABEL "Inicio Periodo Nomina",
Self.fecfin             =     today;// DATE         NOT NULL LABEL "Fin Periodo Nomina",
** Self.diasnomina         =     0;    // SMALLINT     NOT NULL DEFAULT 0 LABEL "Numero de Dias Periodo Nomina",
Self.total_devengos     =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Total Devengos",
Self.total_devengos_e   =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Total Devengos Especie",
Self.total_deducir      =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Total a Deducir",
Self.total_liquido      =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Total Liquido",
Self.feccob             =     today;// DATE         NOT NULL LABEL "Fecha de Cobro",
Self.base_concom        =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Contingencias Comunes",
Self.base_acctra        =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Accidentes Trabajo",
Self.base_proext        =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Prorrata Pagas Extras",
Self.base_con_it        =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Contingencias Comunes IT",
Self.base_acc_it        =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Accidentes Trabajo IT",
Self.base_con_mat       =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Contingencias Comunes Maternidad",
Self.base_acc_mat       =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Accidentes Trabajo Maternidad",
Self.base_con_mat_no    =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Contingencias Comunes Maternidad No Aporta",
Self.base_acc_mat_no    =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Accidentes Trabajo no Aporta",
Self.base_fogasa        =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Fondo Garantia Salarial",
Self.base_fp            =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Formacion Profesional",
Self.base_desempleo     =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Desempleo",
** Self.base_hextras       =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Horas Estras Estructurales",
** Self.base_hextras_no    =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Horas Extras No Extructurales",
Self.base_exceso        =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Exceso Extrasalariales",
Self.base_nocotiza      =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "No cotiza a S.S.",
Self.base_especie       =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Base en Especie Repercutida",
Self.base_especie_no    =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Base en Especie no Repercutida",
Self.base_irpf          =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Base IRPF Dinararia",
Self.base_irpf_especie  =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "IRPF en Especie Repercutido",
Self.base_irpf_espec_no =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "IRPF en Especie no Repercutido",
Self.base_irpf_nocotiza =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "IRPF no Cotiza Dinerario",
Self.base_irpf_nocoti_e =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "IRPF no Cotiza Especie",
Self.base_horascom      =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Horas Complementarias",
Self.base_perdes        =     0;    // MONEY(11,2)  DEFAULT 0 LABEL "Percepcion por Desempleo",
Self.remuneracion       =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Remuneracion",
Self.base_it            =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Base IT",
Self.total_1            =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Total 1",
Self.codbas             =     0;    // CHAR(2)      NOT NULL LABEL "Grupo de Tarifa",
** Self.base_cg            =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Contingencias Generales",
** Self.base_acc           =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Accidentes Trabajo - Enfermedad Profesional",
Self.prc_cg             =     0;    // MONEY(5,2)   NOT NULL DEFAULT 0 LABEL "Porcentaje Contingencias Generales",
Self.prc_acc            =     0;    // MONEY(5,2)   NOT NULL DEFAULT 0 LABEL "Porcentaje Accidentes",
Self.prc_hex            =     0;    // MONEY(5,2)   NOT NULL DEFAULT 0 LABEL "Porcentaje Horas Extras Estructurales",
Self.prc_hexno          =     0;    // MONEY(5,2)   NOT NULL DEFAULT 0 LABEL "Porcentaje Horas Extras NO Estructurales",
Self.importe_cg         =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Importe Contingencias Comunes",
Self.importe_acc        =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Importe Accidentes Trabajo",
Self.importe_hex        =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Importe Horas Extras Estructurales",
Self.importe_hexno      =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Importe Horas Extras NO Estructurales",
Self.mincg              =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Tope Minimo para C.G.",
Self.maxcg              =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Tope Maximo para C.G.",
Self.minacc             =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Tope Minimo para Accidentes",
Self.maxacc             =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Tope Maximo para Accidentes",
Self.cuota_empresa      =     0;    // MONEY(11,2)  DEFAULT 0 LABEL "Cuota Total de la Emrpesa",
Self.importe_cuotas     =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Importe Cuotas Deducciones",
Self.prc_irpf           =     0;    // MONEY(5,2)   NOT NULL DEFAULT 0 LABEL "Porcentaje IRPF",
Self.importe_irpf       =     0;    // MONEY(11,2)  NOT NULL DEFAULT 0 LABEL "Importe IRPF",
Self.fecnew             =     lFnFechaServidor();// DATE         DEFAULT TODAY LABEL "Fecha Creacion Fila",
Self.hornew             =     lFnHoraServidor();  // TIME         DEFAULT NOW LABEL "Hora Creacion Fila",
Self.fecmod             =     lFnFechaServidor();// DATE         LABEL "Fecha Modificacion Fila",
Self.hormod             =     lFnHoraServidor();  // TIME         LABEL "Hora Modificacion Fila",
Self.diastrab           =     0;    // SMALLINT     DEFAULT 0 LABEL "Dias Trabajados",
Self.diasefec           =     0;    // SMALLINT     NOT NULL DEFAULT 0 LABEL "Dias Efectivos",
Self.baseant            =     0;    // MONEY(8,2)   DEFAULT 0 LABEL "Base Calculo Antiguedad",
Self.proret             =     0;    // CHAR(1)      DEFAULT "M" UPSHIFT LABEL "Prorrateo Retribucion",
Self.procot             =     0;    // CHAR(1)      DEFAULT "M" UPSHIFT LABEL "Prorrateo Cotizacion",
Self.codcon             =     0;    // CHAR(2)      LABEL "Codigo Convenio",
Self.codpct             =     0;    // CHAR(8)      LABEL "Asimilado a % Cotizacion",
Self.feccobreal         =     today;// DATE         LABEL "Fecha Cobro Real"
** Self.base_acc_sin_hex   =     0;    // MONEY(11,2)  DEFAULT 0 LABEL "Accidentes Trabajo sin Horas Extras"
Self.base_irpf_ant      =     0;    // MONEY(11,2)  DEFAULT 0 LABEL "Base IRPF Ejercicios Anteriores"
Self.importe_irpf_ant   =     0;    // MONEY(11,2)  DEFAULT 0 LABEL "Importe IRPF Ejercicios Anteriores"
Self.importe_cuotas_ant =     0;    // MONEY(11,2)  DEFAULT 0 LABEL "Importe cuotas (S.S.) Ejercicios Anteriores"
Self.base_cg_pts        =     0;    // MONEY(11,2)  DEFAULT 0 LABEL "Base de Contingencias Generales en Pesetas"
Self.base_acc_pts       =     0;    // MONEY(11,2)  DEFAULT 0 LABEL "Base de Accidentes de Trabajo en Pesetas"
Self.base_acc_sin_h_pts =     0;    // MONEY(11,2)  DEFAULT 0 LABEL "Base de Accidentes de Trabajo sin Horas Extras en Pesetas"
	 */

}
