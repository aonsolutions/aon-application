package com.code.aon.payroll.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.payroll.auxiliares.contratos.ContratosInternos;
import com.code.aon.payroll.auxiliares.contratos.ContratosTc2;
import com.code.aon.payroll.auxiliares.convenios.Complemento;
import com.code.aon.payroll.cotizacion.PorcentajeMaestro;
import com.code.aon.payroll.cotizacion.Porcentaje;
import com.code.aon.payroll.cotizacion.ElementoMaestro;
import com.code.aon.payroll.cotizacion.Elemento;
import com.code.aon.payroll.cotizacion.OcupacionMaestro;
import com.code.aon.payroll.cotizacion.Ocupacion;
import com.code.aon.payroll.cotizacion.CnaeMaestro;
import com.code.aon.payroll.cotizacion.Cnae;
import com.code.aon.payroll.cotizacion.Cnae2009Maestro;
import com.code.aon.payroll.cotizacion.Cnae2009;
import com.code.aon.payroll.tipos.Documento;
import com.code.aon.payroll.tipos.Autorizacion;
import com.code.aon.payroll.cotizacion.Base;
import com.code.aon.payroll.cotizacion.Linbasec;
import com.code.aon.payroll.tipos.Incidencia;
import com.code.aon.payroll.tipos.Registro;
import com.code.aon.payroll.tipos.Empresario;
import com.code.aon.payroll.tipos.TipoCnae;
import com.code.aon.payroll.tipos.TipoCnae2009;
import com.code.aon.payroll.cotizacion.Bonificacion;
import com.code.aon.payroll.irpf.Cuota;
import com.code.aon.payroll.tipos.Tipovia;
import com.code.aon.payroll.geograficas.Nacion;
import com.code.aon.payroll.irpfforal.CuotaRetencionAlava;
import com.code.aon.payroll.irpfforal.CuotaRetencionVizcaya;
import com.code.aon.payroll.irpfforal.CuotaRetencionNavarra;
import com.code.aon.payroll.irpfforal.CuotaRetencionGuipuzcoa;
import com.code.aon.payroll.irpfforal.MinoracionesAlava;
import com.code.aon.payroll.irpfforal.MinoracionesVizcaya;
import com.code.aon.payroll.irpfforal.MinoracionesGuipuzcoa;
import com.code.aon.payroll.irpfforal.MinoracionesNavarra;
import com.code.aon.payroll.auxiliares.Admon;
import com.code.aon.payroll.auxiliares.Colectivos;
import com.code.aon.payroll.irpf.Exclusion;
import com.code.aon.payroll.geograficas.Pais;
import com.code.aon.payroll.geograficas.Comunidad;
import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.payroll.auxiliares.organismosyentidades.Delegacion;
import com.code.aon.payroll.cotizacion.Epigrafe;
import com.code.aon.payroll.cotizacion.Linepigr;
import com.code.aon.payroll.divisa.Divisa;
import com.code.aon.payroll.divisa.LinDivisa;
import com.code.aon.payroll.auxiliares.convenios.Convenio;
import com.code.aon.payroll.auxiliares.convenios.Nivel;
import com.code.aon.payroll.auxiliares.convenios.Pagaext;
import com.code.aon.payroll.auxiliares.convenios.Categoria;
import com.code.aon.payroll.auxiliares.convenios.Percniv;
import com.code.aon.payroll.auxiliares.organismosyentidades.Entidad;
import com.code.aon.payroll.auxiliares.organismosyentidades.Sucursal;
import com.code.aon.payroll.auxiliares.organismosyentidades.Mutua;
import com.code.aon.payroll.auxiliares.organismosyentidades.Linmutua;
import com.code.aon.payroll.irpf.Elemirpf;
import com.code.aon.payroll.irpf.Linirpf;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.payroll.principales.Cuentas;
import com.code.aon.payroll.principales.Avisos;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.payroll.principales.empresa.Actividad;
import com.code.aon.payroll.principales.empresa.Emprdom;
import com.code.aon.payroll.principales.empresa.Emprlban;
import com.code.aon.payroll.avanzadas.gestel.Variable;
import com.code.aon.payroll.avanzadas.gestel.Linvariable;
import com.code.aon.payroll.avanzadas.kartel.Percepcion;
import com.code.aon.payroll.avanzadas.kartel.Linpercepcion;
import com.code.aon.payroll.principales.persona.Embargo;
import com.code.aon.payroll.principales.persona.Trabajador;
import com.code.aon.payroll.principales.personas.Otrperc;
import com.code.aon.payroll.principales.autonomos.Autonomos;
import com.code.aon.payroll.resultados.salarios.Nominaex;
import com.code.aon.payroll.resultados.salarios.Nomdtoex;
import com.code.aon.payroll.principales.autonomos.Autbases;
import com.code.aon.payroll.principales.personas.Percep;
import com.code.aon.payroll.principales.empresa.Emprctra;
import com.code.aon.payroll.principales.empresa.Emprccc;
import com.code.aon.payroll.principales.empresa.Emprccos;
import com.code.aon.payroll.principales.personas.Persona;
import com.code.aon.payroll.resultados.irpf.Impresos11x;
import com.code.aon.payroll.resultados.irpf.Impresos190;
import com.code.aon.payroll.resultados.irpf.LinImpresos190;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httaviso;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httbonificacion;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httcomplemento;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httrabajador;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httincidencia;
import com.code.aon.payroll.resultados.salarios.Finidto;
import com.code.aon.payroll.resultados.salarios.Finindem;
import com.code.aon.payroll.resultados.salarios.Finipext;
import com.code.aon.payroll.resultados.salarios.Finiquito;
import com.code.aon.payroll.resultados.nomina.Nomina;
import com.code.aon.payroll.resultados.nomina.Nominadev;
import com.code.aon.payroll.resultados.nomina.Nomdto;
import com.code.aon.payroll.resultados.irpf.Calculo;
import com.code.aon.payroll.resultados.irpf.Lincalcu;
import com.code.aon.payroll.principales.personas.Bonifica;
import com.code.aon.payroll.principales.personas.Nominait;
import com.code.aon.payroll.principales.personas.Parteit;
import com.code.aon.payroll.principales.personas.Parteconf;
import com.code.aon.payroll.principales.personas.Prcdivtrab;
import com.code.aon.payroll.principales.personas.Tipocont;
import com.code.aon.payroll.principales.personas.Trabajo;
import com.code.aon.payroll.principales.personas.Trabdto;
import com.code.aon.payroll.principales.personas.Trabinci;
import com.code.aon.payroll.resultados.seguros.Tc2;
import com.code.aon.payroll.resultados.seguros.Lintc2;
import com.code.aon.payroll.resultados.seguros.Tc1;
import com.code.aon.payroll.avanzadas.simulacion.Costes;
import com.code.aon.payroll.avanzadas.simulacion.Lbonifica;
import com.code.aon.payroll.avanzadas.simulacion.Lcomunica;
import com.code.aon.payroll.auxiliares.convenios.calendar.Calendario;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IPayrollAlias {



	/** 
	* DAOConstantsEntry for ContratosInternos entity.
	*/ 
	DAOConstantsEntry CONTRATOS_INTERNOS_ENTRY = DAOConstants.getDAOConstant(ContratosInternos.class);

	/** 
	* Alias value: ContratosInternos_cdg
	* Hibernate value: ContratosInternos.cdg
	*/
	String  CONTRATOS_INTERNOS_CDG = CONTRATOS_INTERNOS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ContratosInternos_description
	* Hibernate value: ContratosInternos.description
	*/
	String  CONTRATOS_INTERNOS_DESCRIPTION = CONTRATOS_INTERNOS_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ContratosInternos_desemple
	* Hibernate value: ContratosInternos.desemple
	*/
	String  CONTRATOS_INTERNOS_DESEMPLE = CONTRATOS_INTERNOS_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ContratosInternos_excsocial
	* Hibernate value: ContratosInternos.excsocial
	*/
	String  CONTRATOS_INTERNOS_EXCSOCIAL = CONTRATOS_INTERNOS_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ContratosInternos_gradomin
	* Hibernate value: ContratosInternos.gradomin
	*/
	String  CONTRATOS_INTERNOS_GRADOMIN = CONTRATOS_INTERNOS_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ContratosInternos_incaread
	* Hibernate value: ContratosInternos.incaread
	*/
	String  CONTRATOS_INTERNOS_INCAREAD = CONTRATOS_INTERNOS_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ContratosInternos_mujersub
	* Hibernate value: ContratosInternos.mujersub
	*/
	String  CONTRATOS_INTERNOS_MUJERSUB = CONTRATOS_INTERNOS_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ContratosInternos_porcentajeMaestro_cdg
	* Hibernate value: ContratosInternos.porcentajeMaestro.cdg
	*/
	String  CONTRATOS_INTERNOS_PORCENTAJE_MAESTRO_CDG = CONTRATOS_INTERNOS_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ContratosInternos_primertra
	* Hibernate value: ContratosInternos.primertra
	*/
	String  CONTRATOS_INTERNOS_PRIMERTRA = CONTRATOS_INTERNOS_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for ContratosTc2 entity.
	*/ 
	DAOConstantsEntry CONTRATOS_TC2_ENTRY = DAOConstants.getDAOConstant(ContratosTc2.class);

	/** 
	* Alias value: ContratosTc2_cdg
	* Hibernate value: ContratosTc2.cdg
	*/
	String  CONTRATOS_TC2_CDG = CONTRATOS_TC2_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ContratosTc2_cdgant
	* Hibernate value: ContratosTc2.cdgant
	*/
	String  CONTRATOS_TC2_CDGANT = CONTRATOS_TC2_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ContratosTc2_desabr
	* Hibernate value: ContratosTc2.desabr
	*/
	String  CONTRATOS_TC2_DESABR = CONTRATOS_TC2_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ContratosTc2_description
	* Hibernate value: ContratosTc2.description
	*/
	String  CONTRATOS_TC2_DESCRIPTION = CONTRATOS_TC2_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Complemento entity.
	*/ 
	DAOConstantsEntry COMPLEMENTO_ENTRY = DAOConstants.getDAOConstant(Complemento.class);

	/** 
	* Alias value: Complemento_cdg
	* Hibernate value: Complemento.cdg
	*/
	String  COMPLEMENTO_CDG = COMPLEMENTO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Complemento_desabr
	* Hibernate value: Complemento.desabr
	*/
	String  COMPLEMENTO_DESABR = COMPLEMENTO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Complemento_description
	* Hibernate value: Complemento.description
	*/
	String  COMPLEMENTO_DESCRIPTION = COMPLEMENTO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Complemento_dinesp
	* Hibernate value: Complemento.dinesp
	*/
	String  COMPLEMENTO_DINESP = COMPLEMENTO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Complemento_fijovar
	* Hibernate value: Complemento.fijovar
	*/
	String  COMPLEMENTO_FIJOVAR = COMPLEMENTO_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Complemento_indcom
	* Hibernate value: Complemento.indcom
	*/
	String  COMPLEMENTO_INDCOM = COMPLEMENTO_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Complemento_tipcom
	* Hibernate value: Complemento.tipcom
	*/
	String  COMPLEMENTO_TIPCOM = COMPLEMENTO_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Complemento_tipcot
	* Hibernate value: Complemento.tipcot
	*/
	String  COMPLEMENTO_TIPCOT = COMPLEMENTO_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for PorcentajeMaestro entity.
	*/ 
	DAOConstantsEntry PORCENTAJE_MAESTRO_ENTRY = DAOConstants.getDAOConstant(PorcentajeMaestro.class);

	/** 
	* Alias value: PorcentajeMaestro_cdg
	* Hibernate value: PorcentajeMaestro.cdg
	*/
	String  PORCENTAJE_MAESTRO_CDG = PORCENTAJE_MAESTRO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: PorcentajeMaestro_description
	* Hibernate value: PorcentajeMaestro.description
	*/
	String  PORCENTAJE_MAESTRO_DESCRIPTION = PORCENTAJE_MAESTRO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: PorcentajeMaestro_ordpct
	* Hibernate value: PorcentajeMaestro.ordpct
	*/
	String  PORCENTAJE_MAESTRO_ORDPCT = PORCENTAJE_MAESTRO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: PorcentajeMaestro_porcentajes_fecfin
	* Hibernate value: PorcentajeMaestro.porcentajes.fecfin
	*/
	String  PORCENTAJE_MAESTRO_PORCENTAJES_FECFIN = PORCENTAJE_MAESTRO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: PorcentajeMaestro_porcentajes_id_cdg
	* Hibernate value: PorcentajeMaestro.porcentajes.id.cdg
	*/
	String  PORCENTAJE_MAESTRO_PORCENTAJES_ID_CDG = PORCENTAJE_MAESTRO_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: PorcentajeMaestro_porcentajes_id_fecini
	* Hibernate value: PorcentajeMaestro.porcentajes.id.fecini
	*/
	String  PORCENTAJE_MAESTRO_PORCENTAJES_ID_FECINI = PORCENTAJE_MAESTRO_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Porcentaje entity.
	*/ 
	DAOConstantsEntry PORCENTAJE_ENTRY = DAOConstants.getDAOConstant(Porcentaje.class);

	/** 
	* Alias value: Porcentaje_fecfin
	* Hibernate value: Porcentaje.fecfin
	*/
	String  PORCENTAJE_FECFIN = PORCENTAJE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Porcentaje_id_cdg
	* Hibernate value: Porcentaje.id.cdg
	*/
	String  PORCENTAJE_ID_CDG = PORCENTAJE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Porcentaje_id_fecini
	* Hibernate value: Porcentaje.id.fecini
	*/
	String  PORCENTAJE_ID_FECINI = PORCENTAJE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Porcentaje_pctemp
	* Hibernate value: Porcentaje.pctemp
	*/
	String  PORCENTAJE_PCTEMP = PORCENTAJE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Porcentaje_pcttot
	* Hibernate value: Porcentaje.pcttot
	*/
	String  PORCENTAJE_PCTTOT = PORCENTAJE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Porcentaje_pcttra
	* Hibernate value: Porcentaje.pcttra
	*/
	String  PORCENTAJE_PCTTRA = PORCENTAJE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Porcentaje_porcentajeMaestro_cdg
	* Hibernate value: Porcentaje.porcentajeMaestro.cdg
	*/
	String  PORCENTAJE_PORCENTAJE_MAESTRO_CDG = PORCENTAJE_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for ElementoMaestro entity.
	*/ 
	DAOConstantsEntry ELEMENTO_MAESTRO_ENTRY = DAOConstants.getDAOConstant(ElementoMaestro.class);

	/** 
	* Alias value: ElementoMaestro_cdg
	* Hibernate value: ElementoMaestro.cdg
	*/
	String  ELEMENTO_MAESTRO_CDG = ELEMENTO_MAESTRO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ElementoMaestro_description
	* Hibernate value: ElementoMaestro.description
	*/
	String  ELEMENTO_MAESTRO_DESCRIPTION = ELEMENTO_MAESTRO_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Elemento entity.
	*/ 
	DAOConstantsEntry ELEMENTO_ENTRY = DAOConstants.getDAOConstant(Elemento.class);

	/** 
	* Alias value: Elemento_dato1
	* Hibernate value: Elemento.dato1
	*/
	String  ELEMENTO_DATO1 = ELEMENTO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Elemento_dato2
	* Hibernate value: Elemento.dato2
	*/
	String  ELEMENTO_DATO2 = ELEMENTO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Elemento_elementoMaestro_cdg
	* Hibernate value: Elemento.elementoMaestro.cdg
	*/
	String  ELEMENTO_ELEMENTO_MAESTRO_CDG = ELEMENTO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Elemento_fecfin
	* Hibernate value: Elemento.fecfin
	*/
	String  ELEMENTO_FECFIN = ELEMENTO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Elemento_id_cdg
	* Hibernate value: Elemento.id.cdg
	*/
	String  ELEMENTO_ID_CDG = ELEMENTO_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Elemento_id_fecini
	* Hibernate value: Elemento.id.fecini
	*/
	String  ELEMENTO_ID_FECINI = ELEMENTO_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for OcupacionMaestro entity.
	*/ 
	DAOConstantsEntry OCUPACION_MAESTRO_ENTRY = DAOConstants.getDAOConstant(OcupacionMaestro.class);

	/** 
	* Alias value: OcupacionMaestro_cdg
	* Hibernate value: OcupacionMaestro.cdg
	*/
	String  OCUPACION_MAESTRO_CDG = OCUPACION_MAESTRO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: OcupacionMaestro_description
	* Hibernate value: OcupacionMaestro.description
	*/
	String  OCUPACION_MAESTRO_DESCRIPTION = OCUPACION_MAESTRO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: OcupacionMaestro_exclusivo
	* Hibernate value: OcupacionMaestro.exclusivo
	*/
	String  OCUPACION_MAESTRO_EXCLUSIVO = OCUPACION_MAESTRO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: OcupacionMaestro_ocupaciones_fecfin
	* Hibernate value: OcupacionMaestro.ocupaciones.fecfin
	*/
	String  OCUPACION_MAESTRO_OCUPACIONES_FECFIN = OCUPACION_MAESTRO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: OcupacionMaestro_ocupaciones_id_cdg
	* Hibernate value: OcupacionMaestro.ocupaciones.id.cdg
	*/
	String  OCUPACION_MAESTRO_OCUPACIONES_ID_CDG = OCUPACION_MAESTRO_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: OcupacionMaestro_ocupaciones_id_fecini
	* Hibernate value: OcupacionMaestro.ocupaciones.id.fecini
	*/
	String  OCUPACION_MAESTRO_OCUPACIONES_ID_FECINI = OCUPACION_MAESTRO_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Ocupacion entity.
	*/ 
	DAOConstantsEntry OCUPACION_ENTRY = DAOConstants.getDAOConstant(Ocupacion.class);

	/** 
	* Alias value: Ocupacion_fecfin
	* Hibernate value: Ocupacion.fecfin
	*/
	String  OCUPACION_FECFIN = OCUPACION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Ocupacion_id_cdg
	* Hibernate value: Ocupacion.id.cdg
	*/
	String  OCUPACION_ID_CDG = OCUPACION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Ocupacion_id_fecini
	* Hibernate value: Ocupacion.id.fecini
	*/
	String  OCUPACION_ID_FECINI = OCUPACION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Ocupacion_ocupacionMaestro_cdg
	* Hibernate value: Ocupacion.ocupacionMaestro.cdg
	*/
	String  OCUPACION_OCUPACION_MAESTRO_CDG = OCUPACION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Ocupacion_ocupacionMaestro_exclusivo
	* Hibernate value: Ocupacion.ocupacionMaestro.exclusivo
	*/
	String  OCUPACION_OCUPACION_MAESTRO_EXCLUSIVO = OCUPACION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Ocupacion_pctims
	* Hibernate value: Ocupacion.pctims
	*/
	String  OCUPACION_PCTIMS = OCUPACION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Ocupacion_pctit
	* Hibernate value: Ocupacion.pctit
	*/
	String  OCUPACION_PCTIT = OCUPACION_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Ocupacion_pcttotal
	* Hibernate value: Ocupacion.pcttotal
	*/
	String  OCUPACION_PCTTOTAL = OCUPACION_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for CnaeMaestro entity.
	*/ 
	DAOConstantsEntry CNAE_MAESTRO_ENTRY = DAOConstants.getDAOConstant(CnaeMaestro.class);

	/** 
	* Alias value: CnaeMaestro_cdg
	* Hibernate value: CnaeMaestro.cdg
	*/
	String  CNAE_MAESTRO_CDG = CNAE_MAESTRO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CnaeMaestro_description
	* Hibernate value: CnaeMaestro.description
	*/
	String  CNAE_MAESTRO_DESCRIPTION = CNAE_MAESTRO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CnaeMaestro_ocupacion
	* Hibernate value: CnaeMaestro.ocupacion
	*/
	String  CNAE_MAESTRO_OCUPACION = CNAE_MAESTRO_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for Cnae entity.
	*/ 
	DAOConstantsEntry CNAE_ENTRY = DAOConstants.getDAOConstant(Cnae.class);

	/** 
	* Alias value: Cnae_cnaeMaestro_cdg
	* Hibernate value: Cnae.cnaeMaestro.cdg
	*/
	String  CNAE_CNAE_MAESTRO_CDG = CNAE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Cnae_fecfin
	* Hibernate value: Cnae.fecfin
	*/
	String  CNAE_FECFIN = CNAE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Cnae_id_cdg
	* Hibernate value: Cnae.id.cdg
	*/
	String  CNAE_ID_CDG = CNAE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Cnae_id_fecini
	* Hibernate value: Cnae.id.fecini
	*/
	String  CNAE_ID_FECINI = CNAE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Cnae_pctims
	* Hibernate value: Cnae.pctims
	*/
	String  CNAE_PCTIMS = CNAE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Cnae_pctit
	* Hibernate value: Cnae.pctit
	*/
	String  CNAE_PCTIT = CNAE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Cnae_pcttotal
	* Hibernate value: Cnae.pcttotal
	*/
	String  CNAE_PCTTOTAL = CNAE_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for Cnae2009Maestro entity.
	*/ 
	DAOConstantsEntry CNAE2009MAESTRO_ENTRY = DAOConstants.getDAOConstant(Cnae2009Maestro.class);

	/** 
	* Alias value: Cnae2009Maestro_cdg
	* Hibernate value: Cnae2009Maestro.cdg
	*/
	String  CNAE2009MAESTRO_CDG = CNAE2009MAESTRO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Cnae2009Maestro_description
	* Hibernate value: Cnae2009Maestro.description
	*/
	String  CNAE2009MAESTRO_DESCRIPTION = CNAE2009MAESTRO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Cnae2009Maestro_ocupacion
	* Hibernate value: Cnae2009Maestro.ocupacion
	*/
	String  CNAE2009MAESTRO_OCUPACION = CNAE2009MAESTRO_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for Cnae2009 entity.
	*/ 
	DAOConstantsEntry CNAE2009_ENTRY = DAOConstants.getDAOConstant(Cnae2009.class);

	/** 
	* Alias value: Cnae2009_cnae2009Maestro_cdg
	* Hibernate value: Cnae2009.cnae2009Maestro.cdg
	*/
	String  CNAE2009_CNAE2009MAESTRO_CDG = CNAE2009_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Cnae2009_fecfin
	* Hibernate value: Cnae2009.fecfin
	*/
	String  CNAE2009_FECFIN = CNAE2009_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Cnae2009_id_cdg
	* Hibernate value: Cnae2009.id.cdg
	*/
	String  CNAE2009_ID_CDG = CNAE2009_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Cnae2009_id_fecini
	* Hibernate value: Cnae2009.id.fecini
	*/
	String  CNAE2009_ID_FECINI = CNAE2009_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Cnae2009_pctims
	* Hibernate value: Cnae2009.pctims
	*/
	String  CNAE2009_PCTIMS = CNAE2009_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Cnae2009_pctit
	* Hibernate value: Cnae2009.pctit
	*/
	String  CNAE2009_PCTIT = CNAE2009_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Cnae2009_pcttotal
	* Hibernate value: Cnae2009.pcttotal
	*/
	String  CNAE2009_PCTTOTAL = CNAE2009_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for Documento entity.
	*/ 
	DAOConstantsEntry DOCUMENTO_ENTRY = DAOConstants.getDAOConstant(Documento.class);

	/** 
	* Alias value: Documento_cdg
	* Hibernate value: Documento.cdg
	*/
	String  DOCUMENTO_CDG = DOCUMENTO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Documento_description
	* Hibernate value: Documento.description
	*/
	String  DOCUMENTO_DESCRIPTION = DOCUMENTO_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Autorizacion entity.
	*/ 
	DAOConstantsEntry AUTORIZACION_ENTRY = DAOConstants.getDAOConstant(Autorizacion.class);

	/** 
	* Alias value: Autorizacion_cdg
	* Hibernate value: Autorizacion.cdg
	*/
	String  AUTORIZACION_CDG = AUTORIZACION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Autorizacion_description
	* Hibernate value: Autorizacion.description
	*/
	String  AUTORIZACION_DESCRIPTION = AUTORIZACION_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Base entity.
	*/ 
	DAOConstantsEntry BASE_ENTRY = DAOConstants.getDAOConstant(Base.class);

	/** 
	* Alias value: Base_cdg
	* Hibernate value: Base.cdg
	*/
	String  BASE_CDG = BASE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Base_description
	* Hibernate value: Base.description
	*/
	String  BASE_DESCRIPTION = BASE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Base_indpro
	* Hibernate value: Base.indpro
	*/
	String  BASE_INDPRO = BASE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Base_linbases_fecfin
	* Hibernate value: Base.linbases.fecfin
	*/
	String  BASE_LINBASES_FECFIN = BASE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Base_linbases_id_cdg
	* Hibernate value: Base.linbases.id.cdg
	*/
	String  BASE_LINBASES_ID_CDG = BASE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Base_linbases_id_fecini
	* Hibernate value: Base.linbases.id.fecini
	*/
	String  BASE_LINBASES_ID_FECINI = BASE_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Linbasec entity.
	*/ 
	DAOConstantsEntry LINBASEC_ENTRY = DAOConstants.getDAOConstant(Linbasec.class);

	/** 
	* Alias value: Linbasec_acdiaart
	* Hibernate value: Linbasec.acdiaart
	*/
	String  LINBASEC_ACDIAART = LINBASEC_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Linbasec_basecoti_cdg
	* Hibernate value: Linbasec.basecoti.cdg
	*/
	String  LINBASEC_BASECOTI_CDG = LINBASEC_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Linbasec_fecfin
	* Hibernate value: Linbasec.fecfin
	*/
	String  LINBASEC_FECFIN = LINBASEC_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Linbasec_id_cdg
	* Hibernate value: Linbasec.id.cdg
	*/
	String  LINBASEC_ID_CDG = LINBASEC_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Linbasec_id_fecini
	* Hibernate value: Linbasec.id.fecini
	*/
	String  LINBASEC_ID_FECINI = LINBASEC_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Linbasec_jordiaagr
	* Hibernate value: Linbasec.jordiaagr
	*/
	String  LINBASEC_JORDIAAGR = LINBASEC_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Linbasec_maxcot
	* Hibernate value: Linbasec.maxcot
	*/
	String  LINBASEC_MAXCOT = LINBASEC_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Linbasec_mincot
	* Hibernate value: Linbasec.mincot
	*/
	String  LINBASEC_MINCOT = LINBASEC_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Linbasec_mindia
	* Hibernate value: Linbasec.mindia
	*/
	String  LINBASEC_MINDIA = LINBASEC_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Linbasec_mindiaart1
	* Hibernate value: Linbasec.mindiaart1
	*/
	String  LINBASEC_MINDIAART1 = LINBASEC_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Linbasec_mindiaart2
	* Hibernate value: Linbasec.mindiaart2
	*/
	String  LINBASEC_MINDIAART2 = LINBASEC_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Linbasec_minhor
	* Hibernate value: Linbasec.minhor
	*/
	String  LINBASEC_MINHOR = LINBASEC_ENTRY.getAliasNames()[11];



	/** 
	* DAOConstantsEntry for Incidencia entity.
	*/ 
	DAOConstantsEntry INCIDENCIA_ENTRY = DAOConstants.getDAOConstant(Incidencia.class);

	/** 
	* Alias value: Incidencia_cdg
	* Hibernate value: Incidencia.cdg
	*/
	String  INCIDENCIA_CDG = INCIDENCIA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Incidencia_description
	* Hibernate value: Incidencia.description
	*/
	String  INCIDENCIA_DESCRIPTION = INCIDENCIA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Incidencia_inddto
	* Hibernate value: Incidencia.inddto
	*/
	String  INCIDENCIA_INDDTO = INCIDENCIA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Incidencia_indresta
	* Hibernate value: Incidencia.indresta
	*/
	String  INCIDENCIA_INDRESTA = INCIDENCIA_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Registro entity.
	*/ 
	DAOConstantsEntry REGISTRO_ENTRY = DAOConstants.getDAOConstant(Registro.class);

	/** 
	* Alias value: Registro_cdg
	* Hibernate value: Registro.cdg
	*/
	String  REGISTRO_CDG = REGISTRO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Registro_description
	* Hibernate value: Registro.description
	*/
	String  REGISTRO_DESCRIPTION = REGISTRO_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Empresario entity.
	*/ 
	DAOConstantsEntry EMPRESARIO_ENTRY = DAOConstants.getDAOConstant(Empresario.class);

	/** 
	* Alias value: Empresario_cdg
	* Hibernate value: Empresario.cdg
	*/
	String  EMPRESARIO_CDG = EMPRESARIO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Empresario_description
	* Hibernate value: Empresario.description
	*/
	String  EMPRESARIO_DESCRIPTION = EMPRESARIO_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for TipoCnae entity.
	*/ 
	DAOConstantsEntry TIPO_CNAE_ENTRY = DAOConstants.getDAOConstant(TipoCnae.class);

	/** 
	* Alias value: TipoCnae_cdg
	* Hibernate value: TipoCnae.cdg
	*/
	String  TIPO_CNAE_CDG = TIPO_CNAE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: TipoCnae_description
	* Hibernate value: TipoCnae.description
	*/
	String  TIPO_CNAE_DESCRIPTION = TIPO_CNAE_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for TipoCnae2009 entity.
	*/ 
	DAOConstantsEntry TIPO_CNAE2009_ENTRY = DAOConstants.getDAOConstant(TipoCnae2009.class);

	/** 
	* Alias value: TipoCnae2009_cdg
	* Hibernate value: TipoCnae2009.cdg
	*/
	String  TIPO_CNAE2009_CDG = TIPO_CNAE2009_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: TipoCnae2009_description
	* Hibernate value: TipoCnae2009.description
	*/
	String  TIPO_CNAE2009_DESCRIPTION = TIPO_CNAE2009_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: TipoCnae2009_seccion
	* Hibernate value: TipoCnae2009.seccion
	*/
	String  TIPO_CNAE2009_SECCION = TIPO_CNAE2009_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for Bonificacion entity.
	*/ 
	DAOConstantsEntry BONIFICACION_ENTRY = DAOConstants.getDAOConstant(Bonificacion.class);

	/** 
	* Alias value: Bonificacion_boniss
	* Hibernate value: Bonificacion.boniss
	*/
	String  BONIFICACION_BONISS = BONIFICACION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Bonificacion_calculo
	* Hibernate value: Bonificacion.calculo
	*/
	String  BONIFICACION_CALCULO = BONIFICACION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Bonificacion_cdg
	* Hibernate value: Bonificacion.cdg
	*/
	String  BONIFICACION_CDG = BONIFICACION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Bonificacion_description
	* Hibernate value: Bonificacion.description
	*/
	String  BONIFICACION_DESCRIPTION = BONIFICACION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Bonificacion_mayor60
	* Hibernate value: Bonificacion.mayor60
	*/
	String  BONIFICACION_MAYOR60 = BONIFICACION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Bonificacion_prcAcc
	* Hibernate value: Bonificacion.prcAcc
	*/
	String  BONIFICACION_PRC_ACC = BONIFICACION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Bonificacion_prcAccfgs
	* Hibernate value: Bonificacion.prcAccfgs
	*/
	String  BONIFICACION_PRC_ACCFGS = BONIFICACION_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Bonificacion_prcCg
	* Hibernate value: Bonificacion.prcCg
	*/
	String  BONIFICACION_PRC_CG = BONIFICACION_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Bonificacion_rdl052006
	* Hibernate value: Bonificacion.rdl052006
	*/
	String  BONIFICACION_RDL052006 = BONIFICACION_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Bonificacion_restait
	* Hibernate value: Bonificacion.restait
	*/
	String  BONIFICACION_RESTAIT = BONIFICACION_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for Cuota entity.
	*/ 
	DAOConstantsEntry CUOTA_ENTRY = DAOConstants.getDAOConstant(Cuota.class);

	/** 
	* Alias value: Cuota_fecfin
	* Hibernate value: Cuota.fecfin
	*/
	String  CUOTA_FECFIN = CUOTA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Cuota_hasta
	* Hibernate value: Cuota.hasta
	*/
	String  CUOTA_HASTA = CUOTA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Cuota_id_fecini
	* Hibernate value: Cuota.id.fecini
	*/
	String  CUOTA_ID_FECINI = CUOTA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Cuota_id_numTramo
	* Hibernate value: Cuota.id.numTramo
	*/
	String  CUOTA_ID_NUM_TRAMO = CUOTA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Cuota_pesetas
	* Hibernate value: Cuota.pesetas
	*/
	String  CUOTA_PESETAS = CUOTA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Cuota_porcentaje
	* Hibernate value: Cuota.porcentaje
	*/
	String  CUOTA_PORCENTAJE = CUOTA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Cuota_resto
	* Hibernate value: Cuota.resto
	*/
	String  CUOTA_RESTO = CUOTA_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for Tipovia entity.
	*/ 
	DAOConstantsEntry TIPOVIA_ENTRY = DAOConstants.getDAOConstant(Tipovia.class);

	/** 
	* Alias value: Tipovia_cdg
	* Hibernate value: Tipovia.cdg
	*/
	String  TIPOVIA_CDG = TIPOVIA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Tipovia_description
	* Hibernate value: Tipovia.description
	*/
	String  TIPOVIA_DESCRIPTION = TIPOVIA_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Nacion entity.
	*/ 
	DAOConstantsEntry NACION_ENTRY = DAOConstants.getDAOConstant(Nacion.class);

	/** 
	* Alias value: Nacion_cdg
	* Hibernate value: Nacion.cdg
	*/
	String  NACION_CDG = NACION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Nacion_description
	* Hibernate value: Nacion.description
	*/
	String  NACION_DESCRIPTION = NACION_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for CuotaRetencionAlava entity.
	*/ 
	DAOConstantsEntry CUOTA_RETENCION_ALAVA_ENTRY = DAOConstants.getDAOConstant(CuotaRetencionAlava.class);

	/** 
	* Alias value: CuotaRetencionAlava_desdeImporte
	* Hibernate value: CuotaRetencionAlava.desdeImporte
	*/
	String  CUOTA_RETENCION_ALAVA_DESDE_IMPORTE = CUOTA_RETENCION_ALAVA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CuotaRetencionAlava_fecfin
	* Hibernate value: CuotaRetencionAlava.fecfin
	*/
	String  CUOTA_RETENCION_ALAVA_FECFIN = CUOTA_RETENCION_ALAVA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CuotaRetencionAlava_h0
	* Hibernate value: CuotaRetencionAlava.h0
	*/
	String  CUOTA_RETENCION_ALAVA_H0 = CUOTA_RETENCION_ALAVA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CuotaRetencionAlava_h1
	* Hibernate value: CuotaRetencionAlava.h1
	*/
	String  CUOTA_RETENCION_ALAVA_H1 = CUOTA_RETENCION_ALAVA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CuotaRetencionAlava_h2
	* Hibernate value: CuotaRetencionAlava.h2
	*/
	String  CUOTA_RETENCION_ALAVA_H2 = CUOTA_RETENCION_ALAVA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: CuotaRetencionAlava_h3
	* Hibernate value: CuotaRetencionAlava.h3
	*/
	String  CUOTA_RETENCION_ALAVA_H3 = CUOTA_RETENCION_ALAVA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: CuotaRetencionAlava_h4
	* Hibernate value: CuotaRetencionAlava.h4
	*/
	String  CUOTA_RETENCION_ALAVA_H4 = CUOTA_RETENCION_ALAVA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: CuotaRetencionAlava_h5
	* Hibernate value: CuotaRetencionAlava.h5
	*/
	String  CUOTA_RETENCION_ALAVA_H5 = CUOTA_RETENCION_ALAVA_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: CuotaRetencionAlava_h6
	* Hibernate value: CuotaRetencionAlava.h6
	*/
	String  CUOTA_RETENCION_ALAVA_H6 = CUOTA_RETENCION_ALAVA_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: CuotaRetencionAlava_hastaImporte
	* Hibernate value: CuotaRetencionAlava.hastaImporte
	*/
	String  CUOTA_RETENCION_ALAVA_HASTA_IMPORTE = CUOTA_RETENCION_ALAVA_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: CuotaRetencionAlava_id_fecini
	* Hibernate value: CuotaRetencionAlava.id.fecini
	*/
	String  CUOTA_RETENCION_ALAVA_ID_FECINI = CUOTA_RETENCION_ALAVA_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: CuotaRetencionAlava_id_numTramo
	* Hibernate value: CuotaRetencionAlava.id.numTramo
	*/
	String  CUOTA_RETENCION_ALAVA_ID_NUM_TRAMO = CUOTA_RETENCION_ALAVA_ENTRY.getAliasNames()[11];



	/** 
	* DAOConstantsEntry for CuotaRetencionVizcaya entity.
	*/ 
	DAOConstantsEntry CUOTA_RETENCION_VIZCAYA_ENTRY = DAOConstants.getDAOConstant(CuotaRetencionVizcaya.class);

	/** 
	* Alias value: CuotaRetencionVizcaya_desdeImporte
	* Hibernate value: CuotaRetencionVizcaya.desdeImporte
	*/
	String  CUOTA_RETENCION_VIZCAYA_DESDE_IMPORTE = CUOTA_RETENCION_VIZCAYA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CuotaRetencionVizcaya_fecfin
	* Hibernate value: CuotaRetencionVizcaya.fecfin
	*/
	String  CUOTA_RETENCION_VIZCAYA_FECFIN = CUOTA_RETENCION_VIZCAYA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CuotaRetencionVizcaya_h0
	* Hibernate value: CuotaRetencionVizcaya.h0
	*/
	String  CUOTA_RETENCION_VIZCAYA_H0 = CUOTA_RETENCION_VIZCAYA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CuotaRetencionVizcaya_h1
	* Hibernate value: CuotaRetencionVizcaya.h1
	*/
	String  CUOTA_RETENCION_VIZCAYA_H1 = CUOTA_RETENCION_VIZCAYA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CuotaRetencionVizcaya_h2
	* Hibernate value: CuotaRetencionVizcaya.h2
	*/
	String  CUOTA_RETENCION_VIZCAYA_H2 = CUOTA_RETENCION_VIZCAYA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: CuotaRetencionVizcaya_h3
	* Hibernate value: CuotaRetencionVizcaya.h3
	*/
	String  CUOTA_RETENCION_VIZCAYA_H3 = CUOTA_RETENCION_VIZCAYA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: CuotaRetencionVizcaya_h4
	* Hibernate value: CuotaRetencionVizcaya.h4
	*/
	String  CUOTA_RETENCION_VIZCAYA_H4 = CUOTA_RETENCION_VIZCAYA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: CuotaRetencionVizcaya_h5
	* Hibernate value: CuotaRetencionVizcaya.h5
	*/
	String  CUOTA_RETENCION_VIZCAYA_H5 = CUOTA_RETENCION_VIZCAYA_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: CuotaRetencionVizcaya_h6
	* Hibernate value: CuotaRetencionVizcaya.h6
	*/
	String  CUOTA_RETENCION_VIZCAYA_H6 = CUOTA_RETENCION_VIZCAYA_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: CuotaRetencionVizcaya_hastaImporte
	* Hibernate value: CuotaRetencionVizcaya.hastaImporte
	*/
	String  CUOTA_RETENCION_VIZCAYA_HASTA_IMPORTE = CUOTA_RETENCION_VIZCAYA_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: CuotaRetencionVizcaya_id_fecini
	* Hibernate value: CuotaRetencionVizcaya.id.fecini
	*/
	String  CUOTA_RETENCION_VIZCAYA_ID_FECINI = CUOTA_RETENCION_VIZCAYA_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: CuotaRetencionVizcaya_id_numTramo
	* Hibernate value: CuotaRetencionVizcaya.id.numTramo
	*/
	String  CUOTA_RETENCION_VIZCAYA_ID_NUM_TRAMO = CUOTA_RETENCION_VIZCAYA_ENTRY.getAliasNames()[11];



	/** 
	* DAOConstantsEntry for CuotaRetencionNavarra entity.
	*/ 
	DAOConstantsEntry CUOTA_RETENCION_NAVARRA_ENTRY = DAOConstants.getDAOConstant(CuotaRetencionNavarra.class);

	/** 
	* Alias value: CuotaRetencionNavarra_desdeImporte
	* Hibernate value: CuotaRetencionNavarra.desdeImporte
	*/
	String  CUOTA_RETENCION_NAVARRA_DESDE_IMPORTE = CUOTA_RETENCION_NAVARRA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CuotaRetencionNavarra_fecfin
	* Hibernate value: CuotaRetencionNavarra.fecfin
	*/
	String  CUOTA_RETENCION_NAVARRA_FECFIN = CUOTA_RETENCION_NAVARRA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CuotaRetencionNavarra_h0
	* Hibernate value: CuotaRetencionNavarra.h0
	*/
	String  CUOTA_RETENCION_NAVARRA_H0 = CUOTA_RETENCION_NAVARRA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CuotaRetencionNavarra_h1
	* Hibernate value: CuotaRetencionNavarra.h1
	*/
	String  CUOTA_RETENCION_NAVARRA_H1 = CUOTA_RETENCION_NAVARRA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CuotaRetencionNavarra_h2
	* Hibernate value: CuotaRetencionNavarra.h2
	*/
	String  CUOTA_RETENCION_NAVARRA_H2 = CUOTA_RETENCION_NAVARRA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: CuotaRetencionNavarra_h3
	* Hibernate value: CuotaRetencionNavarra.h3
	*/
	String  CUOTA_RETENCION_NAVARRA_H3 = CUOTA_RETENCION_NAVARRA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: CuotaRetencionNavarra_h4
	* Hibernate value: CuotaRetencionNavarra.h4
	*/
	String  CUOTA_RETENCION_NAVARRA_H4 = CUOTA_RETENCION_NAVARRA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: CuotaRetencionNavarra_h5
	* Hibernate value: CuotaRetencionNavarra.h5
	*/
	String  CUOTA_RETENCION_NAVARRA_H5 = CUOTA_RETENCION_NAVARRA_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: CuotaRetencionNavarra_h6
	* Hibernate value: CuotaRetencionNavarra.h6
	*/
	String  CUOTA_RETENCION_NAVARRA_H6 = CUOTA_RETENCION_NAVARRA_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: CuotaRetencionNavarra_hastaImporte
	* Hibernate value: CuotaRetencionNavarra.hastaImporte
	*/
	String  CUOTA_RETENCION_NAVARRA_HASTA_IMPORTE = CUOTA_RETENCION_NAVARRA_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: CuotaRetencionNavarra_id_fecini
	* Hibernate value: CuotaRetencionNavarra.id.fecini
	*/
	String  CUOTA_RETENCION_NAVARRA_ID_FECINI = CUOTA_RETENCION_NAVARRA_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: CuotaRetencionNavarra_id_numTramo
	* Hibernate value: CuotaRetencionNavarra.id.numTramo
	*/
	String  CUOTA_RETENCION_NAVARRA_ID_NUM_TRAMO = CUOTA_RETENCION_NAVARRA_ENTRY.getAliasNames()[11];



	/** 
	* DAOConstantsEntry for CuotaRetencionGuipuzcoa entity.
	*/ 
	DAOConstantsEntry CUOTA_RETENCION_GUIPUZCOA_ENTRY = DAOConstants.getDAOConstant(CuotaRetencionGuipuzcoa.class);

	/** 
	* Alias value: CuotaRetencionGuipuzcoa_desdeImporte
	* Hibernate value: CuotaRetencionGuipuzcoa.desdeImporte
	*/
	String  CUOTA_RETENCION_GUIPUZCOA_DESDE_IMPORTE = CUOTA_RETENCION_GUIPUZCOA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CuotaRetencionGuipuzcoa_fecfin
	* Hibernate value: CuotaRetencionGuipuzcoa.fecfin
	*/
	String  CUOTA_RETENCION_GUIPUZCOA_FECFIN = CUOTA_RETENCION_GUIPUZCOA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CuotaRetencionGuipuzcoa_h0
	* Hibernate value: CuotaRetencionGuipuzcoa.h0
	*/
	String  CUOTA_RETENCION_GUIPUZCOA_H0 = CUOTA_RETENCION_GUIPUZCOA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CuotaRetencionGuipuzcoa_h1
	* Hibernate value: CuotaRetencionGuipuzcoa.h1
	*/
	String  CUOTA_RETENCION_GUIPUZCOA_H1 = CUOTA_RETENCION_GUIPUZCOA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CuotaRetencionGuipuzcoa_h2
	* Hibernate value: CuotaRetencionGuipuzcoa.h2
	*/
	String  CUOTA_RETENCION_GUIPUZCOA_H2 = CUOTA_RETENCION_GUIPUZCOA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: CuotaRetencionGuipuzcoa_h3
	* Hibernate value: CuotaRetencionGuipuzcoa.h3
	*/
	String  CUOTA_RETENCION_GUIPUZCOA_H3 = CUOTA_RETENCION_GUIPUZCOA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: CuotaRetencionGuipuzcoa_h4
	* Hibernate value: CuotaRetencionGuipuzcoa.h4
	*/
	String  CUOTA_RETENCION_GUIPUZCOA_H4 = CUOTA_RETENCION_GUIPUZCOA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: CuotaRetencionGuipuzcoa_h5
	* Hibernate value: CuotaRetencionGuipuzcoa.h5
	*/
	String  CUOTA_RETENCION_GUIPUZCOA_H5 = CUOTA_RETENCION_GUIPUZCOA_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: CuotaRetencionGuipuzcoa_h6
	* Hibernate value: CuotaRetencionGuipuzcoa.h6
	*/
	String  CUOTA_RETENCION_GUIPUZCOA_H6 = CUOTA_RETENCION_GUIPUZCOA_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: CuotaRetencionGuipuzcoa_hastaImporte
	* Hibernate value: CuotaRetencionGuipuzcoa.hastaImporte
	*/
	String  CUOTA_RETENCION_GUIPUZCOA_HASTA_IMPORTE = CUOTA_RETENCION_GUIPUZCOA_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: CuotaRetencionGuipuzcoa_id_fecini
	* Hibernate value: CuotaRetencionGuipuzcoa.id.fecini
	*/
	String  CUOTA_RETENCION_GUIPUZCOA_ID_FECINI = CUOTA_RETENCION_GUIPUZCOA_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: CuotaRetencionGuipuzcoa_id_numTramo
	* Hibernate value: CuotaRetencionGuipuzcoa.id.numTramo
	*/
	String  CUOTA_RETENCION_GUIPUZCOA_ID_NUM_TRAMO = CUOTA_RETENCION_GUIPUZCOA_ENTRY.getAliasNames()[11];



	/** 
	* DAOConstantsEntry for MinoracionesAlava entity.
	*/ 
	DAOConstantsEntry MINORACIONES_ALAVA_ENTRY = DAOConstants.getDAOConstant(MinoracionesAlava.class);

	/** 
	* Alias value: MinoracionesAlava_desdeImporte
	* Hibernate value: MinoracionesAlava.desdeImporte
	*/
	String  MINORACIONES_ALAVA_DESDE_IMPORTE = MINORACIONES_ALAVA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: MinoracionesAlava_fecfin
	* Hibernate value: MinoracionesAlava.fecfin
	*/
	String  MINORACIONES_ALAVA_FECFIN = MINORACIONES_ALAVA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: MinoracionesAlava_hastaImporte
	* Hibernate value: MinoracionesAlava.hastaImporte
	*/
	String  MINORACIONES_ALAVA_HASTA_IMPORTE = MINORACIONES_ALAVA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: MinoracionesAlava_id_fecini
	* Hibernate value: MinoracionesAlava.id.fecini
	*/
	String  MINORACIONES_ALAVA_ID_FECINI = MINORACIONES_ALAVA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: MinoracionesAlava_id_numTramo
	* Hibernate value: MinoracionesAlava.id.numTramo
	*/
	String  MINORACIONES_ALAVA_ID_NUM_TRAMO = MINORACIONES_ALAVA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: MinoracionesAlava_minAyuda
	* Hibernate value: MinoracionesAlava.minAyuda
	*/
	String  MINORACIONES_ALAVA_MIN_AYUDA = MINORACIONES_ALAVA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: MinoracionesAlava_minGranminus
	* Hibernate value: MinoracionesAlava.minGranminus
	*/
	String  MINORACIONES_ALAVA_MIN_GRANMINUS = MINORACIONES_ALAVA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: MinoracionesAlava_minMinus
	* Hibernate value: MinoracionesAlava.minMinus
	*/
	String  MINORACIONES_ALAVA_MIN_MINUS = MINORACIONES_ALAVA_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for MinoracionesVizcaya entity.
	*/ 
	DAOConstantsEntry MINORACIONES_VIZCAYA_ENTRY = DAOConstants.getDAOConstant(MinoracionesVizcaya.class);

	/** 
	* Alias value: MinoracionesVizcaya_desdeImporte
	* Hibernate value: MinoracionesVizcaya.desdeImporte
	*/
	String  MINORACIONES_VIZCAYA_DESDE_IMPORTE = MINORACIONES_VIZCAYA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: MinoracionesVizcaya_fecfin
	* Hibernate value: MinoracionesVizcaya.fecfin
	*/
	String  MINORACIONES_VIZCAYA_FECFIN = MINORACIONES_VIZCAYA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: MinoracionesVizcaya_hastaImporte
	* Hibernate value: MinoracionesVizcaya.hastaImporte
	*/
	String  MINORACIONES_VIZCAYA_HASTA_IMPORTE = MINORACIONES_VIZCAYA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: MinoracionesVizcaya_id_fecini
	* Hibernate value: MinoracionesVizcaya.id.fecini
	*/
	String  MINORACIONES_VIZCAYA_ID_FECINI = MINORACIONES_VIZCAYA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: MinoracionesVizcaya_id_numTramo
	* Hibernate value: MinoracionesVizcaya.id.numTramo
	*/
	String  MINORACIONES_VIZCAYA_ID_NUM_TRAMO = MINORACIONES_VIZCAYA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: MinoracionesVizcaya_minAyuda
	* Hibernate value: MinoracionesVizcaya.minAyuda
	*/
	String  MINORACIONES_VIZCAYA_MIN_AYUDA = MINORACIONES_VIZCAYA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: MinoracionesVizcaya_minGranminus
	* Hibernate value: MinoracionesVizcaya.minGranminus
	*/
	String  MINORACIONES_VIZCAYA_MIN_GRANMINUS = MINORACIONES_VIZCAYA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: MinoracionesVizcaya_minMinus
	* Hibernate value: MinoracionesVizcaya.minMinus
	*/
	String  MINORACIONES_VIZCAYA_MIN_MINUS = MINORACIONES_VIZCAYA_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for MinoracionesGuipuzcoa entity.
	*/ 
	DAOConstantsEntry MINORACIONES_GUIPUZCOA_ENTRY = DAOConstants.getDAOConstant(MinoracionesGuipuzcoa.class);

	/** 
	* Alias value: MinoracionesGuipuzcoa_desdeImporte
	* Hibernate value: MinoracionesGuipuzcoa.desdeImporte
	*/
	String  MINORACIONES_GUIPUZCOA_DESDE_IMPORTE = MINORACIONES_GUIPUZCOA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: MinoracionesGuipuzcoa_fecfin
	* Hibernate value: MinoracionesGuipuzcoa.fecfin
	*/
	String  MINORACIONES_GUIPUZCOA_FECFIN = MINORACIONES_GUIPUZCOA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: MinoracionesGuipuzcoa_hastaImporte
	* Hibernate value: MinoracionesGuipuzcoa.hastaImporte
	*/
	String  MINORACIONES_GUIPUZCOA_HASTA_IMPORTE = MINORACIONES_GUIPUZCOA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: MinoracionesGuipuzcoa_id_fecini
	* Hibernate value: MinoracionesGuipuzcoa.id.fecini
	*/
	String  MINORACIONES_GUIPUZCOA_ID_FECINI = MINORACIONES_GUIPUZCOA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: MinoracionesGuipuzcoa_id_numTramo
	* Hibernate value: MinoracionesGuipuzcoa.id.numTramo
	*/
	String  MINORACIONES_GUIPUZCOA_ID_NUM_TRAMO = MINORACIONES_GUIPUZCOA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: MinoracionesGuipuzcoa_minAyuda
	* Hibernate value: MinoracionesGuipuzcoa.minAyuda
	*/
	String  MINORACIONES_GUIPUZCOA_MIN_AYUDA = MINORACIONES_GUIPUZCOA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: MinoracionesGuipuzcoa_minGranminus
	* Hibernate value: MinoracionesGuipuzcoa.minGranminus
	*/
	String  MINORACIONES_GUIPUZCOA_MIN_GRANMINUS = MINORACIONES_GUIPUZCOA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: MinoracionesGuipuzcoa_minMinus
	* Hibernate value: MinoracionesGuipuzcoa.minMinus
	*/
	String  MINORACIONES_GUIPUZCOA_MIN_MINUS = MINORACIONES_GUIPUZCOA_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for MinoracionesNavarra entity.
	*/ 
	DAOConstantsEntry MINORACIONES_NAVARRA_ENTRY = DAOConstants.getDAOConstant(MinoracionesNavarra.class);

	/** 
	* Alias value: MinoracionesNavarra_desdeImporte
	* Hibernate value: MinoracionesNavarra.desdeImporte
	*/
	String  MINORACIONES_NAVARRA_DESDE_IMPORTE = MINORACIONES_NAVARRA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: MinoracionesNavarra_fecfin
	* Hibernate value: MinoracionesNavarra.fecfin
	*/
	String  MINORACIONES_NAVARRA_FECFIN = MINORACIONES_NAVARRA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: MinoracionesNavarra_hastaImporte
	* Hibernate value: MinoracionesNavarra.hastaImporte
	*/
	String  MINORACIONES_NAVARRA_HASTA_IMPORTE = MINORACIONES_NAVARRA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: MinoracionesNavarra_id_fecini
	* Hibernate value: MinoracionesNavarra.id.fecini
	*/
	String  MINORACIONES_NAVARRA_ID_FECINI = MINORACIONES_NAVARRA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: MinoracionesNavarra_id_numTramo
	* Hibernate value: MinoracionesNavarra.id.numTramo
	*/
	String  MINORACIONES_NAVARRA_ID_NUM_TRAMO = MINORACIONES_NAVARRA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: MinoracionesNavarra_minGranminus
	* Hibernate value: MinoracionesNavarra.minGranminus
	*/
	String  MINORACIONES_NAVARRA_MIN_GRANMINUS = MINORACIONES_NAVARRA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: MinoracionesNavarra_minMinus
	* Hibernate value: MinoracionesNavarra.minMinus
	*/
	String  MINORACIONES_NAVARRA_MIN_MINUS = MINORACIONES_NAVARRA_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for Admon entity.
	*/ 
	DAOConstantsEntry ADMON_ENTRY = DAOConstants.getDAOConstant(Admon.class);

	/** 
	* Alias value: Admon_cdg
	* Hibernate value: Admon.cdg
	*/
	String  ADMON_CDG = ADMON_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Admon_description
	* Hibernate value: Admon.description
	*/
	String  ADMON_DESCRIPTION = ADMON_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Colectivos entity.
	*/ 
	DAOConstantsEntry COLECTIVOS_ENTRY = DAOConstants.getDAOConstant(Colectivos.class);

	/** 
	* Alias value: Colectivos_cdg
	* Hibernate value: Colectivos.cdg
	*/
	String  COLECTIVOS_CDG = COLECTIVOS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Colectivos_descripcorta
	* Hibernate value: Colectivos.descripcorta
	*/
	String  COLECTIVOS_DESCRIPCORTA = COLECTIVOS_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Colectivos_description
	* Hibernate value: Colectivos.description
	*/
	String  COLECTIVOS_DESCRIPTION = COLECTIVOS_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for Exclusion entity.
	*/ 
	DAOConstantsEntry EXCLUSION_ENTRY = DAOConstants.getDAOConstant(Exclusion.class);

	/** 
	* Alias value: Exclusion_fecfin
	* Hibernate value: Exclusion.fecfin
	*/
	String  EXCLUSION_FECFIN = EXCLUSION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Exclusion_id_fecini
	* Hibernate value: Exclusion.id.fecini
	*/
	String  EXCLUSION_ID_FECINI = EXCLUSION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Exclusion_id_hijos
	* Hibernate value: Exclusion.id.hijos
	*/
	String  EXCLUSION_ID_HIJOS = EXCLUSION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Exclusion_id_situacion
	* Hibernate value: Exclusion.id.situacion
	*/
	String  EXCLUSION_ID_SITUACION = EXCLUSION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Exclusion_importe
	* Hibernate value: Exclusion.importe
	*/
	String  EXCLUSION_IMPORTE = EXCLUSION_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for Pais entity.
	*/ 
	DAOConstantsEntry PAIS_ENTRY = DAOConstants.getDAOConstant(Pais.class);

	/** 
	* Alias value: Pais_cdg
	* Hibernate value: Pais.cdg
	*/
	String  PAIS_CDG = PAIS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Pais_descripcion
	* Hibernate value: Pais.descripcion
	*/
	String  PAIS_DESCRIPCION = PAIS_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Comunidad entity.
	*/ 
	DAOConstantsEntry COMUNIDAD_ENTRY = DAOConstants.getDAOConstant(Comunidad.class);

	/** 
	* Alias value: Comunidad_cdg
	* Hibernate value: Comunidad.cdg
	*/
	String  COMUNIDAD_CDG = COMUNIDAD_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Comunidad_descripcion
	* Hibernate value: Comunidad.descripcion
	*/
	String  COMUNIDAD_DESCRIPCION = COMUNIDAD_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Comunidad_pais_cdg
	* Hibernate value: Comunidad.pais.cdg
	*/
	String  COMUNIDAD_PAIS_CDG = COMUNIDAD_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for Provincia entity.
	*/ 
	DAOConstantsEntry PROVINCIA_ENTRY = DAOConstants.getDAOConstant(Provincia.class);

	/** 
	* Alias value: Provincia_cdg
	* Hibernate value: Provincia.cdg
	*/
	String  PROVINCIA_CDG = PROVINCIA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Provincia_comunidad_cdg
	* Hibernate value: Provincia.comunidad.cdg
	*/
	String  PROVINCIA_COMUNIDAD_CDG = PROVINCIA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Provincia_descripcion
	* Hibernate value: Provincia.descripcion
	*/
	String  PROVINCIA_DESCRIPCION = PROVINCIA_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for Delegacion entity.
	*/ 
	DAOConstantsEntry DELEGACION_ENTRY = DAOConstants.getDAOConstant(Delegacion.class);

	/** 
	* Alias value: Delegacion_cdg
	* Hibernate value: Delegacion.cdg
	*/
	String  DELEGACION_CDG = DELEGACION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Delegacion_codpos
	* Hibernate value: Delegacion.codpos
	*/
	String  DELEGACION_CODPOS = DELEGACION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Delegacion_descripcion
	* Hibernate value: Delegacion.descripcion
	*/
	String  DELEGACION_DESCRIPCION = DELEGACION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Delegacion_localidad
	* Hibernate value: Delegacion.localidad
	*/
	String  DELEGACION_LOCALIDAD = DELEGACION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Delegacion_nomvia
	* Hibernate value: Delegacion.nomvia
	*/
	String  DELEGACION_NOMVIA = DELEGACION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Delegacion_numero
	* Hibernate value: Delegacion.numero
	*/
	String  DELEGACION_NUMERO = DELEGACION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Delegacion_otrdir
	* Hibernate value: Delegacion.otrdir
	*/
	String  DELEGACION_OTRDIR = DELEGACION_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Delegacion_provincia_cdg
	* Hibernate value: Delegacion.provincia.cdg
	*/
	String  DELEGACION_PROVINCIA_CDG = DELEGACION_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Delegacion_telefono
	* Hibernate value: Delegacion.telefono
	*/
	String  DELEGACION_TELEFONO = DELEGACION_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Delegacion_tipovia_cdg
	* Hibernate value: Delegacion.tipovia.cdg
	*/
	String  DELEGACION_TIPOVIA_CDG = DELEGACION_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for Epigrafe entity.
	*/ 
	DAOConstantsEntry EPIGRAFE_ENTRY = DAOConstants.getDAOConstant(Epigrafe.class);

	/** 
	* Alias value: Epigrafe_cdg
	* Hibernate value: Epigrafe.cdg
	*/
	String  EPIGRAFE_CDG = EPIGRAFE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Epigrafe_description
	* Hibernate value: Epigrafe.description
	*/
	String  EPIGRAFE_DESCRIPTION = EPIGRAFE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Epigrafe_epigrafes_fecfin
	* Hibernate value: Epigrafe.epigrafes.fecfin
	*/
	String  EPIGRAFE_EPIGRAFES_FECFIN = EPIGRAFE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Epigrafe_epigrafes_id_cdg
	* Hibernate value: Epigrafe.epigrafes.id.cdg
	*/
	String  EPIGRAFE_EPIGRAFES_ID_CDG = EPIGRAFE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Epigrafe_epigrafes_id_fecini
	* Hibernate value: Epigrafe.epigrafes.id.fecini
	*/
	String  EPIGRAFE_EPIGRAFES_ID_FECINI = EPIGRAFE_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for Linepigr entity.
	*/ 
	DAOConstantsEntry LINEPIGR_ENTRY = DAOConstants.getDAOConstant(Linepigr.class);

	/** 
	* Alias value: Linepigr_canipm
	* Hibernate value: Linepigr.canipm
	*/
	String  LINEPIGR_CANIPM = LINEPIGR_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Linepigr_canit
	* Hibernate value: Linepigr.canit
	*/
	String  LINEPIGR_CANIT = LINEPIGR_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Linepigr_epigrafe_cdg
	* Hibernate value: Linepigr.epigrafe.cdg
	*/
	String  LINEPIGR_EPIGRAFE_CDG = LINEPIGR_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Linepigr_fecfin
	* Hibernate value: Linepigr.fecfin
	*/
	String  LINEPIGR_FECFIN = LINEPIGR_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Linepigr_id_cdg
	* Hibernate value: Linepigr.id.cdg
	*/
	String  LINEPIGR_ID_CDG = LINEPIGR_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Linepigr_id_fecini
	* Hibernate value: Linepigr.id.fecini
	*/
	String  LINEPIGR_ID_FECINI = LINEPIGR_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Linepigr_indipm
	* Hibernate value: Linepigr.indipm
	*/
	String  LINEPIGR_INDIPM = LINEPIGR_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Linepigr_indit
	* Hibernate value: Linepigr.indit
	*/
	String  LINEPIGR_INDIT = LINEPIGR_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for Divisa entity.
	*/ 
	DAOConstantsEntry DIVISA_ENTRY = DAOConstants.getDAOConstant(Divisa.class);

	/** 
	* Alias value: Divisa_cdg
	* Hibernate value: Divisa.cdg
	*/
	String  DIVISA_CDG = DIVISA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Divisa_description
	* Hibernate value: Divisa.description
	*/
	String  DIVISA_DESCRIPTION = DIVISA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Divisa_mask1
	* Hibernate value: Divisa.mask1
	*/
	String  DIVISA_MASK1 = DIVISA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Divisa_mask2
	* Hibernate value: Divisa.mask2
	*/
	String  DIVISA_MASK2 = DIVISA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Divisa_redondeo
	* Hibernate value: Divisa.redondeo
	*/
	String  DIVISA_REDONDEO = DIVISA_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for LinDivisa entity.
	*/ 
	DAOConstantsEntry LIN_DIVISA_ENTRY = DAOConstants.getDAOConstant(LinDivisa.class);

	/** 
	* Alias value: LinDivisa_divisa0_cdg
	* Hibernate value: LinDivisa.divisa0.cdg
	*/
	String  LIN_DIVISA_DIVISA0_CDG = LIN_DIVISA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: LinDivisa_divisa1_cdg
	* Hibernate value: LinDivisa.divisa1.cdg
	*/
	String  LIN_DIVISA_DIVISA1_CDG = LIN_DIVISA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: LinDivisa_fecfin
	* Hibernate value: LinDivisa.fecfin
	*/
	String  LIN_DIVISA_FECFIN = LIN_DIVISA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: LinDivisa_id_cdg
	* Hibernate value: LinDivisa.id.cdg
	*/
	String  LIN_DIVISA_ID_CDG = LIN_DIVISA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: LinDivisa_id_divisaFinal
	* Hibernate value: LinDivisa.id.divisaFinal
	*/
	String  LIN_DIVISA_ID_DIVISA_FINAL = LIN_DIVISA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: LinDivisa_id_fecini
	* Hibernate value: LinDivisa.id.fecini
	*/
	String  LIN_DIVISA_ID_FECINI = LIN_DIVISA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: LinDivisa_importe
	* Hibernate value: LinDivisa.importe
	*/
	String  LIN_DIVISA_IMPORTE = LIN_DIVISA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: LinDivisa_unidades
	* Hibernate value: LinDivisa.unidades
	*/
	String  LIN_DIVISA_UNIDADES = LIN_DIVISA_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for Convenio entity.
	*/ 
	DAOConstantsEntry CONVENIO_ENTRY = DAOConstants.getDAOConstant(Convenio.class);

	/** 
	* Alias value: Convenio_cdg
	* Hibernate value: Convenio.cdg
	*/
	String  CONVENIO_CDG = CONVENIO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Convenio_description
	* Hibernate value: Convenio.description
	*/
	String  CONVENIO_DESCRIPTION = CONVENIO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Convenio_inddia
	* Hibernate value: Convenio.inddia
	*/
	String  CONVENIO_INDDIA = CONVENIO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Convenio_tipcon
	* Hibernate value: Convenio.tipcon
	*/
	String  CONVENIO_TIPCON = CONVENIO_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Nivel entity.
	*/ 
	DAOConstantsEntry NIVEL_ENTRY = DAOConstants.getDAOConstant(Nivel.class);

	/** 
	* Alias value: Nivel_convenio_cdg
	* Hibernate value: Nivel.convenio.cdg
	*/
	String  NIVEL_CONVENIO_CDG = NIVEL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Nivel_id_cdg
	* Hibernate value: Nivel.id.cdg
	*/
	String  NIVEL_ID_CDG = NIVEL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Nivel_id_codcon
	* Hibernate value: Nivel.id.codcon
	*/
	String  NIVEL_ID_CODCON = NIVEL_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for Pagaext entity.
	*/ 
	DAOConstantsEntry PAGAEXT_ENTRY = DAOConstants.getDAOConstant(Pagaext.class);

	/** 
	* Alias value: Pagaext_complemento_cdg
	* Hibernate value: Pagaext.complemento.cdg
	*/
	String  PAGAEXT_COMPLEMENTO_CDG = PAGAEXT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Pagaext_convenio_cdg
	* Hibernate value: Pagaext.convenio.cdg
	*/
	String  PAGAEXT_CONVENIO_CDG = PAGAEXT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Pagaext_feccob
	* Hibernate value: Pagaext.feccob
	*/
	String  PAGAEXT_FECCOB = PAGAEXT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Pagaext_id_cdg
	* Hibernate value: Pagaext.id.cdg
	*/
	String  PAGAEXT_ID_CDG = PAGAEXT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Pagaext_id_codcom
	* Hibernate value: Pagaext.id.codcom
	*/
	String  PAGAEXT_ID_CODCOM = PAGAEXT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Pagaext_indfin
	* Hibernate value: Pagaext.indfin
	*/
	String  PAGAEXT_INDFIN = PAGAEXT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Pagaext_indini
	* Hibernate value: Pagaext.indini
	*/
	String  PAGAEXT_INDINI = PAGAEXT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Pagaext_perfin
	* Hibernate value: Pagaext.perfin
	*/
	String  PAGAEXT_PERFIN = PAGAEXT_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Pagaext_perini
	* Hibernate value: Pagaext.perini
	*/
	String  PAGAEXT_PERINI = PAGAEXT_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Pagaext_prorat
	* Hibernate value: Pagaext.prorat
	*/
	String  PAGAEXT_PRORAT = PAGAEXT_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for Categoria entity.
	*/ 
	DAOConstantsEntry CATEGORIA_ENTRY = DAOConstants.getDAOConstant(Categoria.class);

	/** 
	* Alias value: Categoria_base_cdg
	* Hibernate value: Categoria.base.cdg
	*/
	String  CATEGORIA_BASE_CDG = CATEGORIA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Categoria_cdgnivel
	* Hibernate value: Categoria.cdgnivel
	*/
	String  CATEGORIA_CDGNIVEL = CATEGORIA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Categoria_cno
	* Hibernate value: Categoria.cno
	*/
	String  CATEGORIA_CNO = CATEGORIA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Categoria_convenio_cdg
	* Hibernate value: Categoria.convenio.cdg
	*/
	String  CATEGORIA_CONVENIO_CDG = CATEGORIA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Categoria_description
	* Hibernate value: Categoria.description
	*/
	String  CATEGORIA_DESCRIPTION = CATEGORIA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Categoria_epigrafe_cdg
	* Hibernate value: Categoria.epigrafe.cdg
	*/
	String  CATEGORIA_EPIGRAFE_CDG = CATEGORIA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Categoria_id_cdg
	* Hibernate value: Categoria.id.cdg
	*/
	String  CATEGORIA_ID_CDG = CATEGORIA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Categoria_id_codcon
	* Hibernate value: Categoria.id.codcon
	*/
	String  CATEGORIA_ID_CODCON = CATEGORIA_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Categoria_nivel_id_cdg
	* Hibernate value: Categoria.nivel.id.cdg
	*/
	String  CATEGORIA_NIVEL_ID_CDG = CATEGORIA_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Categoria_nivel_id_codcon
	* Hibernate value: Categoria.nivel.id.codcon
	*/
	String  CATEGORIA_NIVEL_ID_CODCON = CATEGORIA_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for Percniv entity.
	*/ 
	DAOConstantsEntry PERCNIV_ENTRY = DAOConstants.getDAOConstant(Percniv.class);

	/** 
	* Alias value: Percniv_calculo
	* Hibernate value: Percniv.calculo
	*/
	String  PERCNIV_CALCULO = PERCNIV_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Percniv_complemento1_cdg
	* Hibernate value: Percniv.complemento1.cdg
	*/
	String  PERCNIV_COMPLEMENTO1_CDG = PERCNIV_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Percniv_complemento_cdg
	* Hibernate value: Percniv.complemento.cdg
	*/
	String  PERCNIV_COMPLEMENTO_CDG = PERCNIV_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Percniv_convenio_cdg
	* Hibernate value: Percniv.convenio.cdg
	*/
	String  PERCNIV_CONVENIO_CDG = PERCNIV_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Percniv_convenio_description
	* Hibernate value: Percniv.convenio.description
	*/
	String  PERCNIV_CONVENIO_DESCRIPTION = PERCNIV_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Percniv_desabr
	* Hibernate value: Percniv.desabr
	*/
	String  PERCNIV_DESABR = PERCNIV_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Percniv_descom
	* Hibernate value: Percniv.descom
	*/
	String  PERCNIV_DESCOM = PERCNIV_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Percniv_dinesp
	* Hibernate value: Percniv.dinesp
	*/
	String  PERCNIV_DINESP = PERCNIV_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Percniv_fecmod
	* Hibernate value: Percniv.fecmod
	*/
	String  PERCNIV_FECMOD = PERCNIV_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Percniv_fecnew
	* Hibernate value: Percniv.fecnew
	*/
	String  PERCNIV_FECNEW = PERCNIV_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Percniv_fijovar
	* Hibernate value: Percniv.fijovar
	*/
	String  PERCNIV_FIJOVAR = PERCNIV_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Percniv_garilt
	* Hibernate value: Percniv.garilt
	*/
	String  PERCNIV_GARILT = PERCNIV_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Percniv_hormod
	* Hibernate value: Percniv.hormod
	*/
	String  PERCNIV_HORMOD = PERCNIV_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Percniv_hornew
	* Hibernate value: Percniv.hornew
	*/
	String  PERCNIV_HORNEW = PERCNIV_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Percniv_id_cdg
	* Hibernate value: Percniv.id.cdg
	*/
	String  PERCNIV_ID_CDG = PERCNIV_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Percniv_id_codcom
	* Hibernate value: Percniv.id.codcom
	*/
	String  PERCNIV_ID_CODCOM = PERCNIV_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Percniv_id_nivel
	* Hibernate value: Percniv.id.nivel
	*/
	String  PERCNIV_ID_NIVEL = PERCNIV_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Percniv_importe
	* Hibernate value: Percniv.importe
	*/
	String  PERCNIV_IMPORTE = PERCNIV_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Percniv_impuni
	* Hibernate value: Percniv.impuni
	*/
	String  PERCNIV_IMPUNI = PERCNIV_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Percniv_indcom
	* Hibernate value: Percniv.indcom
	*/
	String  PERCNIV_INDCOM = PERCNIV_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Percniv_mes
	* Hibernate value: Percniv.mes
	*/
	String  PERCNIV_MES = PERCNIV_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Percniv_nivel_id_cdg
	* Hibernate value: Percniv.nivel.id.cdg
	*/
	String  PERCNIV_NIVEL_ID_CDG = PERCNIV_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Percniv_nivel_id_codcon
	* Hibernate value: Percniv.nivel.id.codcon
	*/
	String  PERCNIV_NIVEL_ID_CODCON = PERCNIV_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Percniv_redext
	* Hibernate value: Percniv.redext
	*/
	String  PERCNIV_REDEXT = PERCNIV_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Percniv_tipcom
	* Hibernate value: Percniv.tipcom
	*/
	String  PERCNIV_TIPCOM = PERCNIV_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Percniv_tipcot
	* Hibernate value: Percniv.tipcot
	*/
	String  PERCNIV_TIPCOT = PERCNIV_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Percniv_unidades
	* Hibernate value: Percniv.unidades
	*/
	String  PERCNIV_UNIDADES = PERCNIV_ENTRY.getAliasNames()[26];



	/** 
	* DAOConstantsEntry for Entidad entity.
	*/ 
	DAOConstantsEntry ENTIDAD_ENTRY = DAOConstants.getDAOConstant(Entidad.class);

	/** 
	* Alias value: Entidad_cdg
	* Hibernate value: Entidad.cdg
	*/
	String  ENTIDAD_CDG = ENTIDAD_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Entidad_description
	* Hibernate value: Entidad.description
	*/
	String  ENTIDAD_DESCRIPTION = ENTIDAD_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Sucursal entity.
	*/ 
	DAOConstantsEntry SUCURSAL_ENTRY = DAOConstants.getDAOConstant(Sucursal.class);

	/** 
	* Alias value: Sucursal_cpsuc
	* Hibernate value: Sucursal.cpsuc
	*/
	String  SUCURSAL_CPSUC = SUCURSAL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Sucursal_domsuc
	* Hibernate value: Sucursal.domsuc
	*/
	String  SUCURSAL_DOMSUC = SUCURSAL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Sucursal_entidad_cdg
	* Hibernate value: Sucursal.entidad.cdg
	*/
	String  SUCURSAL_ENTIDAD_CDG = SUCURSAL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Sucursal_id_cdg
	* Hibernate value: Sucursal.id.cdg
	*/
	String  SUCURSAL_ID_CDG = SUCURSAL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Sucursal_id_codent
	* Hibernate value: Sucursal.id.codent
	*/
	String  SUCURSAL_ID_CODENT = SUCURSAL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Sucursal_munsuc
	* Hibernate value: Sucursal.munsuc
	*/
	String  SUCURSAL_MUNSUC = SUCURSAL_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Mutua entity.
	*/ 
	DAOConstantsEntry MUTUA_ENTRY = DAOConstants.getDAOConstant(Mutua.class);

	/** 
	* Alias value: Mutua_cdg
	* Hibernate value: Mutua.cdg
	*/
	String  MUTUA_CDG = MUTUA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Mutua_description
	* Hibernate value: Mutua.description
	*/
	String  MUTUA_DESCRIPTION = MUTUA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Mutua_mutuas_fecfin
	* Hibernate value: Mutua.mutuas.fecfin
	*/
	String  MUTUA_MUTUAS_FECFIN = MUTUA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Mutua_mutuas_id_cdg
	* Hibernate value: Mutua.mutuas.id.cdg
	*/
	String  MUTUA_MUTUAS_ID_CDG = MUTUA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Mutua_mutuas_id_fecini
	* Hibernate value: Mutua.mutuas.id.fecini
	*/
	String  MUTUA_MUTUAS_ID_FECINI = MUTUA_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for Linmutua entity.
	*/ 
	DAOConstantsEntry LINMUTUA_ENTRY = DAOConstants.getDAOConstant(Linmutua.class);

	/** 
	* Alias value: Linmutua_fecfin
	* Hibernate value: Linmutua.fecfin
	*/
	String  LINMUTUA_FECFIN = LINMUTUA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Linmutua_id_cdg
	* Hibernate value: Linmutua.id.cdg
	*/
	String  LINMUTUA_ID_CDG = LINMUTUA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Linmutua_id_fecini
	* Hibernate value: Linmutua.id.fecini
	*/
	String  LINMUTUA_ID_FECINI = LINMUTUA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Linmutua_mutua_cdg
	* Hibernate value: Linmutua.mutua.cdg
	*/
	String  LINMUTUA_MUTUA_CDG = LINMUTUA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Linmutua_prcacctrab
	* Hibernate value: Linmutua.prcacctrab
	*/
	String  LINMUTUA_PRCACCTRAB = LINMUTUA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Linmutua_prcit
	* Hibernate value: Linmutua.prcit
	*/
	String  LINMUTUA_PRCIT = LINMUTUA_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Elemirpf entity.
	*/ 
	DAOConstantsEntry ELEMIRPF_ENTRY = DAOConstants.getDAOConstant(Elemirpf.class);

	/** 
	* Alias value: Elemirpf_cdg
	* Hibernate value: Elemirpf.cdg
	*/
	String  ELEMIRPF_CDG = ELEMIRPF_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Elemirpf_description
	* Hibernate value: Elemirpf.description
	*/
	String  ELEMIRPF_DESCRIPTION = ELEMIRPF_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Elemirpf_porcentaje
	* Hibernate value: Elemirpf.porcentaje
	*/
	String  ELEMIRPF_PORCENTAJE = ELEMIRPF_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for Linirpf entity.
	*/ 
	DAOConstantsEntry LINIRPF_ENTRY = DAOConstants.getDAOConstant(Linirpf.class);

	/** 
	* Alias value: Linirpf_elemirpf_cdg
	* Hibernate value: Linirpf.elemirpf.cdg
	*/
	String  LINIRPF_ELEMIRPF_CDG = LINIRPF_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Linirpf_fecfin
	* Hibernate value: Linirpf.fecfin
	*/
	String  LINIRPF_FECFIN = LINIRPF_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Linirpf_hasta
	* Hibernate value: Linirpf.hasta
	*/
	String  LINIRPF_HASTA = LINIRPF_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Linirpf_id_cdg
	* Hibernate value: Linirpf.id.cdg
	*/
	String  LINIRPF_ID_CDG = LINIRPF_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Linirpf_id_fecini
	* Hibernate value: Linirpf.id.fecini
	*/
	String  LINIRPF_ID_FECINI = LINIRPF_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Linirpf_importe
	* Hibernate value: Linirpf.importe
	*/
	String  LINIRPF_IMPORTE = LINIRPF_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Cliente entity.
	*/ 
	DAOConstantsEntry CLIENTE_ENTRY = DAOConstants.getDAOConstant(Cliente.class);

	/** 
	* Alias value: Cliente_alias
	* Hibernate value: Cliente.alias
	*/
	String  CLIENTE_ALIAS = CLIENTE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Cliente_cdg
	* Hibernate value: Cliente.cdg
	*/
	String  CLIENTE_CDG = CLIENTE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Cliente_codpos
	* Hibernate value: Cliente.codpos
	*/
	String  CLIENTE_CODPOS = CLIENTE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Cliente_delegacion_cdg
	* Hibernate value: Cliente.delegacion.cdg
	*/
	String  CLIENTE_DELEGACION_CDG = CLIENTE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Cliente_descripcion
	* Hibernate value: Cliente.descripcion
	*/
	String  CLIENTE_DESCRIPCION = CLIENTE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Cliente_divisa_cdg
	* Hibernate value: Cliente.divisa.cdg
	*/
	String  CLIENTE_DIVISA_CDG = CLIENTE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Cliente_email
	* Hibernate value: Cliente.email
	*/
	String  CLIENTE_EMAIL = CLIENTE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Cliente_envioss
	* Hibernate value: Cliente.envioss
	*/
	String  CLIENTE_ENVIOSS = CLIENTE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Cliente_fax
	* Hibernate value: Cliente.fax
	*/
	String  CLIENTE_FAX = CLIENTE_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Cliente_fecfin
	* Hibernate value: Cliente.fecfin
	*/
	String  CLIENTE_FECFIN = CLIENTE_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Cliente_fecini
	* Hibernate value: Cliente.fecini
	*/
	String  CLIENTE_FECINI = CLIENTE_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Cliente_fecmod
	* Hibernate value: Cliente.fecmod
	*/
	String  CLIENTE_FECMOD = CLIENTE_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Cliente_fecnew
	* Hibernate value: Cliente.fecnew
	*/
	String  CLIENTE_FECNEW = CLIENTE_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Cliente_hormod
	* Hibernate value: Cliente.hormod
	*/
	String  CLIENTE_HORMOD = CLIENTE_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Cliente_hornew
	* Hibernate value: Cliente.hornew
	*/
	String  CLIENTE_HORNEW = CLIENTE_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Cliente_inactivo
	* Hibernate value: Cliente.inactivo
	*/
	String  CLIENTE_INACTIVO = CLIENTE_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Cliente_indcal
	* Hibernate value: Cliente.indcal
	*/
	String  CLIENTE_INDCAL = CLIENTE_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Cliente_indcoste
	* Hibernate value: Cliente.indcoste
	*/
	String  CLIENTE_INDCOSTE = CLIENTE_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Cliente_indnom
	* Hibernate value: Cliente.indnom
	*/
	String  CLIENTE_INDNOM = CLIENTE_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Cliente_localidad
	* Hibernate value: Cliente.localidad
	*/
	String  CLIENTE_LOCALIDAD = CLIENTE_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Cliente_nomvia
	* Hibernate value: Cliente.nomvia
	*/
	String  CLIENTE_NOMVIA = CLIENTE_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Cliente_numdoc
	* Hibernate value: Cliente.numdoc
	*/
	String  CLIENTE_NUMDOC = CLIENTE_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Cliente_numero
	* Hibernate value: Cliente.numero
	*/
	String  CLIENTE_NUMERO = CLIENTE_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Cliente_obscli
	* Hibernate value: Cliente.obscli
	*/
	String  CLIENTE_OBSCLI = CLIENTE_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Cliente_otrdir
	* Hibernate value: Cliente.otrdir
	*/
	String  CLIENTE_OTRDIR = CLIENTE_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Cliente_pais_cdg
	* Hibernate value: Cliente.pais.cdg
	*/
	String  CLIENTE_PAIS_CDG = CLIENTE_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Cliente_persona
	* Hibernate value: Cliente.persona
	*/
	String  CLIENTE_PERSONA = CLIENTE_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: Cliente_provincia_cdg
	* Hibernate value: Cliente.provincia.cdg
	*/
	String  CLIENTE_PROVINCIA_CDG = CLIENTE_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: Cliente_soloases
	* Hibernate value: Cliente.soloases
	*/
	String  CLIENTE_SOLOASES = CLIENTE_ENTRY.getAliasNames()[28];

	/** 
	* Alias value: Cliente_telefono
	* Hibernate value: Cliente.telefono
	*/
	String  CLIENTE_TELEFONO = CLIENTE_ENTRY.getAliasNames()[29];

	/** 
	* Alias value: Cliente_telefono2
	* Hibernate value: Cliente.telefono2
	*/
	String  CLIENTE_TELEFONO2 = CLIENTE_ENTRY.getAliasNames()[30];

	/** 
	* Alias value: Cliente_telefono3
	* Hibernate value: Cliente.telefono3
	*/
	String  CLIENTE_TELEFONO3 = CLIENTE_ENTRY.getAliasNames()[31];

	/** 
	* Alias value: Cliente_tipdoc_cdg
	* Hibernate value: Cliente.tipdoc.cdg
	*/
	String  CLIENTE_TIPDOC_CDG = CLIENTE_ENTRY.getAliasNames()[32];

	/** 
	* Alias value: Cliente_tipempr_cdg
	* Hibernate value: Cliente.tipempr.cdg
	*/
	String  CLIENTE_TIPEMPR_CDG = CLIENTE_ENTRY.getAliasNames()[33];

	/** 
	* Alias value: Cliente_tipovia_cdg
	* Hibernate value: Cliente.tipovia.cdg
	*/
	String  CLIENTE_TIPOVIA_CDG = CLIENTE_ENTRY.getAliasNames()[34];



	/** 
	* DAOConstantsEntry for Domicilio entity.
	*/ 
	DAOConstantsEntry DOMICILIO_ENTRY = DAOConstants.getDAOConstant(Domicilio.class);

	/** 
	* Alias value: Domicilio_aclaracion
	* Hibernate value: Domicilio.aclaracion
	*/
	String  DOMICILIO_ACLARACION = DOMICILIO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Domicilio_cdg
	* Hibernate value: Domicilio.cdg
	*/
	String  DOMICILIO_CDG = DOMICILIO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Domicilio_cliente_cdg
	* Hibernate value: Domicilio.cliente.cdg
	*/
	String  DOMICILIO_CLIENTE_CDG = DOMICILIO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Domicilio_codnsz
	* Hibernate value: Domicilio.codnsz
	*/
	String  DOMICILIO_CODNSZ = DOMICILIO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Domicilio_codpos
	* Hibernate value: Domicilio.codpos
	*/
	String  DOMICILIO_CODPOS = DOMICILIO_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Domicilio_email
	* Hibernate value: Domicilio.email
	*/
	String  DOMICILIO_EMAIL = DOMICILIO_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Domicilio_fax
	* Hibernate value: Domicilio.fax
	*/
	String  DOMICILIO_FAX = DOMICILIO_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Domicilio_linea1
	* Hibernate value: Domicilio.linea1
	*/
	String  DOMICILIO_LINEA1 = DOMICILIO_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Domicilio_linea2
	* Hibernate value: Domicilio.linea2
	*/
	String  DOMICILIO_LINEA2 = DOMICILIO_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Domicilio_localidad
	* Hibernate value: Domicilio.localidad
	*/
	String  DOMICILIO_LOCALIDAD = DOMICILIO_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Domicilio_nomvia
	* Hibernate value: Domicilio.nomvia
	*/
	String  DOMICILIO_NOMVIA = DOMICILIO_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Domicilio_numero
	* Hibernate value: Domicilio.numero
	*/
	String  DOMICILIO_NUMERO = DOMICILIO_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Domicilio_otrdir
	* Hibernate value: Domicilio.otrdir
	*/
	String  DOMICILIO_OTRDIR = DOMICILIO_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Domicilio_persona
	* Hibernate value: Domicilio.persona
	*/
	String  DOMICILIO_PERSONA = DOMICILIO_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Domicilio_provincia_cdg
	* Hibernate value: Domicilio.provincia.cdg
	*/
	String  DOMICILIO_PROVINCIA_CDG = DOMICILIO_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Domicilio_telefono
	* Hibernate value: Domicilio.telefono
	*/
	String  DOMICILIO_TELEFONO = DOMICILIO_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Domicilio_telefono2
	* Hibernate value: Domicilio.telefono2
	*/
	String  DOMICILIO_TELEFONO2 = DOMICILIO_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Domicilio_telefono3
	* Hibernate value: Domicilio.telefono3
	*/
	String  DOMICILIO_TELEFONO3 = DOMICILIO_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Domicilio_tipovia_cdg
	* Hibernate value: Domicilio.tipovia.cdg
	*/
	String  DOMICILIO_TIPOVIA_CDG = DOMICILIO_ENTRY.getAliasNames()[18];



	/** 
	* DAOConstantsEntry for Cuentas entity.
	*/ 
	DAOConstantsEntry CUENTAS_ENTRY = DAOConstants.getDAOConstant(Cuentas.class);

	/** 
	* Alias value: Cuentas_cdg
	* Hibernate value: Cuentas.cdg
	*/
	String  CUENTAS_CDG = CUENTAS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Cuentas_cliente_cdg
	* Hibernate value: Cuentas.cliente.cdg
	*/
	String  CUENTAS_CLIENTE_CDG = CUENTAS_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Cuentas_dc
	* Hibernate value: Cuentas.dc
	*/
	String  CUENTAS_DC = CUENTAS_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Cuentas_entidad_cdg
	* Hibernate value: Cuentas.entidad.cdg
	*/
	String  CUENTAS_ENTIDAD_CDG = CUENTAS_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Cuentas_entidad_description
	* Hibernate value: Cuentas.entidad.description
	*/
	String  CUENTAS_ENTIDAD_DESCRIPTION = CUENTAS_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Cuentas_numcta
	* Hibernate value: Cuentas.numcta
	*/
	String  CUENTAS_NUMCTA = CUENTAS_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Cuentas_sucursal_id_cdg
	* Hibernate value: Cuentas.sucursal.id.cdg
	*/
	String  CUENTAS_SUCURSAL_ID_CDG = CUENTAS_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Cuentas_sucursal_id_codent
	* Hibernate value: Cuentas.sucursal.id.codent
	*/
	String  CUENTAS_SUCURSAL_ID_CODENT = CUENTAS_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for Avisos entity.
	*/ 
	DAOConstantsEntry AVISOS_ENTRY = DAOConstants.getDAOConstant(Avisos.class);

	/** 
	* Alias value: Avisos_actividad_cdg
	* Hibernate value: Avisos.actividad.cdg
	*/
	String  AVISOS_ACTIVIDAD_CDG = AVISOS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Avisos_cdg
	* Hibernate value: Avisos.cdg
	*/
	String  AVISOS_CDG = AVISOS_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Avisos_cliente_cdg
	* Hibernate value: Avisos.cliente.cdg
	*/
	String  AVISOS_CLIENTE_CDG = AVISOS_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Avisos_descripcion
	* Hibernate value: Avisos.descripcion
	*/
	String  AVISOS_DESCRIPCION = AVISOS_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Avisos_empresa_cdg
	* Hibernate value: Avisos.empresa.cdg
	*/
	String  AVISOS_EMPRESA_CDG = AVISOS_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Avisos_emprper_cdg
	* Hibernate value: Avisos.emprper.cdg
	*/
	String  AVISOS_EMPRPER_CDG = AVISOS_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Avisos_fecha
	* Hibernate value: Avisos.fecha
	*/
	String  AVISOS_FECHA = AVISOS_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Avisos_tipo
	* Hibernate value: Avisos.tipo
	*/
	String  AVISOS_TIPO = AVISOS_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for Empresa entity.
	*/ 
	DAOConstantsEntry EMPRESA_ENTRY = DAOConstants.getDAOConstant(Empresa.class);

	/** 
	* Alias value: Empresa_admon_cdg
	* Hibernate value: Empresa.admon.cdg
	*/
	String  EMPRESA_ADMON_CDG = EMPRESA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Empresa_alias
	* Hibernate value: Empresa.alias
	*/
	String  EMPRESA_ALIAS = EMPRESA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Empresa_cargo
	* Hibernate value: Empresa.cargo
	*/
	String  EMPRESA_CARGO = EMPRESA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Empresa_cdg
	* Hibernate value: Empresa.cdg
	*/
	String  EMPRESA_CDG = EMPRESA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Empresa_cecon
	* Hibernate value: Empresa.cecon
	*/
	String  EMPRESA_CECON = EMPRESA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Empresa_cliente_cdg
	* Hibernate value: Empresa.cliente.cdg
	*/
	String  EMPRESA_CLIENTE_CDG = EMPRESA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Empresa_cliente_fecfin
	* Hibernate value: Empresa.cliente.fecfin
	*/
	String  EMPRESA_CLIENTE_FECFIN = EMPRESA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Empresa_cliente_fecini
	* Hibernate value: Empresa.cliente.fecini
	*/
	String  EMPRESA_CLIENTE_FECINI = EMPRESA_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Empresa_datreg
	* Hibernate value: Empresa.datreg
	*/
	String  EMPRESA_DATREG = EMPRESA_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Empresa_descripcion
	* Hibernate value: Empresa.descripcion
	*/
	String  EMPRESA_DESCRIPCION = EMPRESA_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Empresa_divisa_cdg
	* Hibernate value: Empresa.divisa.cdg
	*/
	String  EMPRESA_DIVISA_CDG = EMPRESA_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Empresa_envioss
	* Hibernate value: Empresa.envioss
	*/
	String  EMPRESA_ENVIOSS = EMPRESA_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Empresa_feccon
	* Hibernate value: Empresa.feccon
	*/
	String  EMPRESA_FECCON = EMPRESA_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Empresa_fecfin
	* Hibernate value: Empresa.fecfin
	*/
	String  EMPRESA_FECFIN = EMPRESA_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Empresa_fecini
	* Hibernate value: Empresa.fecini
	*/
	String  EMPRESA_FECINI = EMPRESA_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Empresa_fecmod
	* Hibernate value: Empresa.fecmod
	*/
	String  EMPRESA_FECMOD = EMPRESA_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Empresa_fecnac
	* Hibernate value: Empresa.fecnac
	*/
	String  EMPRESA_FECNAC = EMPRESA_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Empresa_fecnew
	* Hibernate value: Empresa.fecnew
	*/
	String  EMPRESA_FECNEW = EMPRESA_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Empresa_hormod
	* Hibernate value: Empresa.hormod
	*/
	String  EMPRESA_HORMOD = EMPRESA_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Empresa_hornew
	* Hibernate value: Empresa.hornew
	*/
	String  EMPRESA_HORNEW = EMPRESA_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Empresa_indcal
	* Hibernate value: Empresa.indcal
	*/
	String  EMPRESA_INDCAL = EMPRESA_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Empresa_indcoste
	* Hibernate value: Empresa.indcoste
	*/
	String  EMPRESA_INDCOSTE = EMPRESA_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Empresa_indirpf
	* Hibernate value: Empresa.indirpf
	*/
	String  EMPRESA_INDIRPF = EMPRESA_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Empresa_indnom
	* Hibernate value: Empresa.indnom
	*/
	String  EMPRESA_INDNOM = EMPRESA_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Empresa_modimpuesto
	* Hibernate value: Empresa.modimpuesto
	*/
	String  EMPRESA_MODIMPUESTO = EMPRESA_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Empresa_nrodocrep
	* Hibernate value: Empresa.nrodocrep
	*/
	String  EMPRESA_NRODOCREP = EMPRESA_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Empresa_numdoc
	* Hibernate value: Empresa.numdoc
	*/
	String  EMPRESA_NUMDOC = EMPRESA_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: Empresa_obsnif
	* Hibernate value: Empresa.obsnif
	*/
	String  EMPRESA_OBSNIF = EMPRESA_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: Empresa_pais1_cdg
	* Hibernate value: Empresa.pais1.cdg
	*/
	String  EMPRESA_PAIS1_CDG = EMPRESA_ENTRY.getAliasNames()[28];

	/** 
	* Alias value: Empresa_pais_cdg
	* Hibernate value: Empresa.pais.cdg
	*/
	String  EMPRESA_PAIS_CDG = EMPRESA_ENTRY.getAliasNames()[29];

	/** 
	* Alias value: Empresa_representante
	* Hibernate value: Empresa.representante
	*/
	String  EMPRESA_REPRESENTANTE = EMPRESA_ENTRY.getAliasNames()[30];

	/** 
	* Alias value: Empresa_sexo
	* Hibernate value: Empresa.sexo
	*/
	String  EMPRESA_SEXO = EMPRESA_ENTRY.getAliasNames()[31];

	/** 
	* Alias value: Empresa_tipdoc1_cdg
	* Hibernate value: Empresa.tipdoc1.cdg
	*/
	String  EMPRESA_TIPDOC1_CDG = EMPRESA_ENTRY.getAliasNames()[32];

	/** 
	* Alias value: Empresa_tipdoc_cdg
	* Hibernate value: Empresa.tipdoc.cdg
	*/
	String  EMPRESA_TIPDOC_CDG = EMPRESA_ENTRY.getAliasNames()[33];

	/** 
	* Alias value: Empresa_tipempr_cdg
	* Hibernate value: Empresa.tipempr.cdg
	*/
	String  EMPRESA_TIPEMPR_CDG = EMPRESA_ENTRY.getAliasNames()[34];



	/** 
	* DAOConstantsEntry for Actividad entity.
	*/ 
	DAOConstantsEntry ACTIVIDAD_ENTRY = DAOConstants.getDAOConstant(Actividad.class);

	/** 
	* Alias value: Actividad_acteco
	* Hibernate value: Actividad.acteco
	*/
	String  ACTIVIDAD_ACTECO = ACTIVIDAD_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Actividad_alias
	* Hibernate value: Actividad.alias
	*/
	String  ACTIVIDAD_ALIAS = ACTIVIDAD_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Actividad_cdg
	* Hibernate value: Actividad.cdg
	*/
	String  ACTIVIDAD_CDG = ACTIVIDAD_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Actividad_cnae
	* Hibernate value: Actividad.cnae
	*/
	String  ACTIVIDAD_CNAE = ACTIVIDAD_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Actividad_cnae2009
	* Hibernate value: Actividad.cnae2009
	*/
	String  ACTIVIDAD_CNAE2009 = ACTIVIDAD_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Actividad_codnsz
	* Hibernate value: Actividad.codnsz
	*/
	String  ACTIVIDAD_CODNSZ = ACTIVIDAD_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Actividad_colss
	* Hibernate value: Actividad.colss
	*/
	String  ACTIVIDAD_COLSS = ACTIVIDAD_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Actividad_convenio_cdg
	* Hibernate value: Actividad.convenio.cdg
	*/
	String  ACTIVIDAD_CONVENIO_CDG = ACTIVIDAD_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Actividad_descripcion
	* Hibernate value: Actividad.descripcion
	*/
	String  ACTIVIDAD_DESCRIPCION = ACTIVIDAD_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Actividad_empresa_cdg
	* Hibernate value: Actividad.empresa.cdg
	*/
	String  ACTIVIDAD_EMPRESA_CDG = ACTIVIDAD_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Actividad_envioss
	* Hibernate value: Actividad.envioss
	*/
	String  ACTIVIDAD_ENVIOSS = ACTIVIDAD_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Actividad_epiiae
	* Hibernate value: Actividad.epiiae
	*/
	String  ACTIVIDAD_EPIIAE = ACTIVIDAD_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Actividad_fecfin
	* Hibernate value: Actividad.fecfin
	*/
	String  ACTIVIDAD_FECFIN = ACTIVIDAD_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Actividad_fecini
	* Hibernate value: Actividad.fecini
	*/
	String  ACTIVIDAD_FECINI = ACTIVIDAD_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Actividad_fecmod
	* Hibernate value: Actividad.fecmod
	*/
	String  ACTIVIDAD_FECMOD = ACTIVIDAD_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Actividad_fecnew
	* Hibernate value: Actividad.fecnew
	*/
	String  ACTIVIDAD_FECNEW = ACTIVIDAD_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Actividad_flc
	* Hibernate value: Actividad.flc
	*/
	String  ACTIVIDAD_FLC = ACTIVIDAD_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Actividad_hormod
	* Hibernate value: Actividad.hormod
	*/
	String  ACTIVIDAD_HORMOD = ACTIVIDAD_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Actividad_hornew
	* Hibernate value: Actividad.hornew
	*/
	String  ACTIVIDAD_HORNEW = ACTIVIDAD_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Actividad_indcal
	* Hibernate value: Actividad.indcal
	*/
	String  ACTIVIDAD_INDCAL = ACTIVIDAD_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Actividad_indcoste
	* Hibernate value: Actividad.indcoste
	*/
	String  ACTIVIDAD_INDCOSTE = ACTIVIDAD_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Actividad_indfirma
	* Hibernate value: Actividad.indfirma
	*/
	String  ACTIVIDAD_INDFIRMA = ACTIVIDAD_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Actividad_indlogo
	* Hibernate value: Actividad.indlogo
	*/
	String  ACTIVIDAD_INDLOGO = ACTIVIDAD_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Actividad_indmutua
	* Hibernate value: Actividad.indmutua
	*/
	String  ACTIVIDAD_INDMUTUA = ACTIVIDAD_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Actividad_indnom
	* Hibernate value: Actividad.indnom
	*/
	String  ACTIVIDAD_INDNOM = ACTIVIDAD_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Actividad_indred
	* Hibernate value: Actividad.indred
	*/
	String  ACTIVIDAD_INDRED = ACTIVIDAD_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Actividad_indregimen
	* Hibernate value: Actividad.indregimen
	*/
	String  ACTIVIDAD_INDREGIMEN = ACTIVIDAD_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: Actividad_indtc1
	* Hibernate value: Actividad.indtc1
	*/
	String  ACTIVIDAD_INDTC1 = ACTIVIDAD_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: Actividad_ingespemp
	* Hibernate value: Actividad.ingespemp
	*/
	String  ACTIVIDAD_INGESPEMP = ACTIVIDAD_ENTRY.getAliasNames()[28];

	/** 
	* Alias value: Actividad_modpago
	* Hibernate value: Actividad.modpago
	*/
	String  ACTIVIDAD_MODPAGO = ACTIVIDAD_ENTRY.getAliasNames()[29];

	/** 
	* Alias value: Actividad_prevencion
	* Hibernate value: Actividad.prevencion
	*/
	String  ACTIVIDAD_PREVENCION = ACTIVIDAD_ENTRY.getAliasNames()[30];

	/** 
	* Alias value: Actividad_tiponomina
	* Hibernate value: Actividad.tiponomina
	*/
	String  ACTIVIDAD_TIPONOMINA = ACTIVIDAD_ENTRY.getAliasNames()[31];



	/** 
	* DAOConstantsEntry for Emprdom entity.
	*/ 
	DAOConstantsEntry EMPRDOM_ENTRY = DAOConstants.getDAOConstant(Emprdom.class);

	/** 
	* Alias value: Emprdom_actividad_cdg
	* Hibernate value: Emprdom.actividad.cdg
	*/
	String  EMPRDOM_ACTIVIDAD_CDG = EMPRDOM_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Emprdom_cdg
	* Hibernate value: Emprdom.cdg
	*/
	String  EMPRDOM_CDG = EMPRDOM_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Emprdom_cliente_cdg
	* Hibernate value: Emprdom.cliente.cdg
	*/
	String  EMPRDOM_CLIENTE_CDG = EMPRDOM_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Emprdom_domicilio_cdg
	* Hibernate value: Emprdom.domicilio.cdg
	*/
	String  EMPRDOM_DOMICILIO_CDG = EMPRDOM_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Emprdom_empresa_cdg
	* Hibernate value: Emprdom.empresa.cdg
	*/
	String  EMPRDOM_EMPRESA_CDG = EMPRDOM_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Emprdom_tipdom
	* Hibernate value: Emprdom.tipdom
	*/
	String  EMPRDOM_TIPDOM = EMPRDOM_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Emprlban entity.
	*/ 
	DAOConstantsEntry EMPRLBAN_ENTRY = DAOConstants.getDAOConstant(Emprlban.class);

	/** 
	* Alias value: Emprlban_actividad_cdg
	* Hibernate value: Emprlban.actividad.cdg
	*/
	String  EMPRLBAN_ACTIVIDAD_CDG = EMPRLBAN_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Emprlban_cdg
	* Hibernate value: Emprlban.cdg
	*/
	String  EMPRLBAN_CDG = EMPRLBAN_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Emprlban_cliente_cdg
	* Hibernate value: Emprlban.cliente.cdg
	*/
	String  EMPRLBAN_CLIENTE_CDG = EMPRLBAN_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Emprlban_cuenta_cdg
	* Hibernate value: Emprlban.cuenta.cdg
	*/
	String  EMPRLBAN_CUENTA_CDG = EMPRLBAN_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Emprlban_empresa_cdg
	* Hibernate value: Emprlban.empresa.cdg
	*/
	String  EMPRLBAN_EMPRESA_CDG = EMPRLBAN_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Emprlban_tipcta
	* Hibernate value: Emprlban.tipcta
	*/
	String  EMPRLBAN_TIPCTA = EMPRLBAN_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Variable entity.
	*/ 
	DAOConstantsEntry VARIABLE_ENTRY = DAOConstants.getDAOConstant(Variable.class);

	/** 
	* Alias value: Variable_cdg
	* Hibernate value: Variable.cdg
	*/
	String  VARIABLE_CDG = VARIABLE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Variable_descripcion
	* Hibernate value: Variable.descripcion
	*/
	String  VARIABLE_DESCRIPCION = VARIABLE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Variable_linvariable_diurna
	* Hibernate value: Variable.linvariable.diurna
	*/
	String  VARIABLE_LINVARIABLE_DIURNA = VARIABLE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Variable_linvariable_domingo
	* Hibernate value: Variable.linvariable.domingo
	*/
	String  VARIABLE_LINVARIABLE_DOMINGO = VARIABLE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Variable_linvariable_fecfin
	* Hibernate value: Variable.linvariable.fecfin
	*/
	String  VARIABLE_LINVARIABLE_FECFIN = VARIABLE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Variable_linvariable_festdiurna
	* Hibernate value: Variable.linvariable.festdiurna
	*/
	String  VARIABLE_LINVARIABLE_FESTDIURNA = VARIABLE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Variable_linvariable_festivo
	* Hibernate value: Variable.linvariable.festivo
	*/
	String  VARIABLE_LINVARIABLE_FESTIVO = VARIABLE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Variable_linvariable_festivoesp
	* Hibernate value: Variable.linvariable.festivoesp
	*/
	String  VARIABLE_LINVARIABLE_FESTIVOESP = VARIABLE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Variable_linvariable_festnocturna
	* Hibernate value: Variable.linvariable.festnocturna
	*/
	String  VARIABLE_LINVARIABLE_FESTNOCTURNA = VARIABLE_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Variable_linvariable_id_cdg
	* Hibernate value: Variable.linvariable.id.cdg
	*/
	String  VARIABLE_LINVARIABLE_ID_CDG = VARIABLE_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Variable_linvariable_id_fecini
	* Hibernate value: Variable.linvariable.id.fecini
	*/
	String  VARIABLE_LINVARIABLE_ID_FECINI = VARIABLE_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Variable_Linvariable_nocturna
	* Hibernate value: Variable.Linvariable.nocturna
	*/
	String  VARIABLE_LINVARIABLE_NOCTURNA = VARIABLE_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Variable_Linvariable_nocturnidad
	* Hibernate value: Variable.Linvariable.nocturnidad
	*/
	String  VARIABLE_LINVARIABLE_NOCTURNIDAD = VARIABLE_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Variable_Linvariable_transporte
	* Hibernate value: Variable.Linvariable.transporte
	*/
	String  VARIABLE_LINVARIABLE_TRANSPORTE = VARIABLE_ENTRY.getAliasNames()[13];



	/** 
	* DAOConstantsEntry for Linvariable entity.
	*/ 
	DAOConstantsEntry LINVARIABLE_ENTRY = DAOConstants.getDAOConstant(Linvariable.class);

	/** 
	* Alias value: Linvariable_diurna
	* Hibernate value: Linvariable.diurna
	*/
	String  LINVARIABLE_DIURNA = LINVARIABLE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Linvariable_domingo
	* Hibernate value: Linvariable.domingo
	*/
	String  LINVARIABLE_DOMINGO = LINVARIABLE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Linvariable_fecfin
	* Hibernate value: Linvariable.fecfin
	*/
	String  LINVARIABLE_FECFIN = LINVARIABLE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Linvariable_festdiurna
	* Hibernate value: Linvariable.festdiurna
	*/
	String  LINVARIABLE_FESTDIURNA = LINVARIABLE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Linvariable_festivo
	* Hibernate value: Linvariable.festivo
	*/
	String  LINVARIABLE_FESTIVO = LINVARIABLE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Linvariable_festivoesp
	* Hibernate value: Linvariable.festivoesp
	*/
	String  LINVARIABLE_FESTIVOESP = LINVARIABLE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Linvariable_festnocturna
	* Hibernate value: Linvariable.festnocturna
	*/
	String  LINVARIABLE_FESTNOCTURNA = LINVARIABLE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Linvariable_id_cdg
	* Hibernate value: Linvariable.id.cdg
	*/
	String  LINVARIABLE_ID_CDG = LINVARIABLE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Linvariable_id_fecini
	* Hibernate value: Linvariable.id.fecini
	*/
	String  LINVARIABLE_ID_FECINI = LINVARIABLE_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Linvariable_nocturna
	* Hibernate value: Linvariable.nocturna
	*/
	String  LINVARIABLE_NOCTURNA = LINVARIABLE_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Linvariable_nocturnidad
	* Hibernate value: Linvariable.nocturnidad
	*/
	String  LINVARIABLE_NOCTURNIDAD = LINVARIABLE_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Linvariable_transporte
	* Hibernate value: Linvariable.transporte
	*/
	String  LINVARIABLE_TRANSPORTE = LINVARIABLE_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Linvariable_variable_cdg
	* Hibernate value: Linvariable.variable.cdg
	*/
	String  LINVARIABLE_VARIABLE_CDG = LINVARIABLE_ENTRY.getAliasNames()[12];



	/** 
	* DAOConstantsEntry for Percepcion entity.
	*/ 
	DAOConstantsEntry PERCEPCION_ENTRY = DAOConstants.getDAOConstant(Percepcion.class);

	/** 
	* Alias value: Percepcion_cdg
	* Hibernate value: Percepcion.cdg
	*/
	String  PERCEPCION_CDG = PERCEPCION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Percepcion_descripcion
	* Hibernate value: Percepcion.descripcion
	*/
	String  PERCEPCION_DESCRIPCION = PERCEPCION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Percepcion_linpercepciones_fecfin
	* Hibernate value: Percepcion.linpercepciones.fecfin
	*/
	String  PERCEPCION_LINPERCEPCIONES_FECFIN = PERCEPCION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Percepcion_linpercepciones_id_cdg
	* Hibernate value: Percepcion.linpercepciones.id.cdg
	*/
	String  PERCEPCION_LINPERCEPCIONES_ID_CDG = PERCEPCION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Percepcion_linpercepciones_id_fecini
	* Hibernate value: Percepcion.linpercepciones.id.fecini
	*/
	String  PERCEPCION_LINPERCEPCIONES_ID_FECINI = PERCEPCION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Percepcion_tipo
	* Hibernate value: Percepcion.tipo
	*/
	String  PERCEPCION_TIPO = PERCEPCION_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Linpercepcion entity.
	*/ 
	DAOConstantsEntry LINPERCEPCION_ENTRY = DAOConstants.getDAOConstant(Linpercepcion.class);

	/** 
	* Alias value: Linpercepcion_empresa
	* Hibernate value: Linpercepcion.empresa
	*/
	String  LINPERCEPCION_EMPRESA = LINPERCEPCION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Linpercepcion_fecfin
	* Hibernate value: Linpercepcion.fecfin
	*/
	String  LINPERCEPCION_FECFIN = LINPERCEPCION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Linpercepcion_id_cdg
	* Hibernate value: Linpercepcion.id.cdg
	*/
	String  LINPERCEPCION_ID_CDG = LINPERCEPCION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Linpercepcion_id_fecini
	* Hibernate value: Linpercepcion.id.fecini
	*/
	String  LINPERCEPCION_ID_FECINI = LINPERCEPCION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Linpercepcion_importe
	* Hibernate value: Linpercepcion.importe
	*/
	String  LINPERCEPCION_IMPORTE = LINPERCEPCION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Linpercepcion_nocturno
	* Hibernate value: Linpercepcion.nocturno
	*/
	String  LINPERCEPCION_NOCTURNO = LINPERCEPCION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Linpercepcion_percepcion_cdg
	* Hibernate value: Linpercepcion.percepcion.cdg
	*/
	String  LINPERCEPCION_PERCEPCION_CDG = LINPERCEPCION_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for Embargo entity.
	*/ 
	DAOConstantsEntry EMBARGO_ENTRY = DAOConstants.getDAOConstant(Embargo.class);

	/** 
	* Alias value: Embargo_afecta
	* Hibernate value: Embargo.afecta
	*/
	String  EMBARGO_AFECTA = EMBARGO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Embargo_codper
	* Hibernate value: Embargo.codper
	*/
	String  EMBARGO_CODPER = EMBARGO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Embargo_concepto
	* Hibernate value: Embargo.concepto
	*/
	String  EMBARGO_CONCEPTO = EMBARGO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Embargo_fecmod
	* Hibernate value: Embargo.fecmod
	*/
	String  EMBARGO_FECMOD = EMBARGO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Embargo_fecnew
	* Hibernate value: Embargo.fecnew
	*/
	String  EMBARGO_FECNEW = EMBARGO_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Embargo_hormod
	* Hibernate value: Embargo.hormod
	*/
	String  EMBARGO_HORMOD = EMBARGO_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Embargo_hornew
	* Hibernate value: Embargo.hornew
	*/
	String  EMBARGO_HORNEW = EMBARGO_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Embargo_id_cdg
	* Hibernate value: Embargo.id.cdg
	*/
	String  EMBARGO_ID_CDG = EMBARGO_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Embargo_id_fecha
	* Hibernate value: Embargo.id.fecha
	*/
	String  EMBARGO_ID_FECHA = EMBARGO_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Embargo_importe
	* Hibernate value: Embargo.importe
	*/
	String  EMBARGO_IMPORTE = EMBARGO_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Embargo_trabajador_cdg
	* Hibernate value: Embargo.trabajador.cdg
	*/
	String  EMBARGO_TRABAJADOR_CDG = EMBARGO_ENTRY.getAliasNames()[10];



	/** 
	* DAOConstantsEntry for Trabajador entity.
	*/ 
	DAOConstantsEntry TRABAJADOR_ENTRY = DAOConstants.getDAOConstant(Trabajador.class);

	/** 
	* Alias value: Trabajador_actividad_cdg
	* Hibernate value: Trabajador.actividad.cdg
	*/
	String  TRABAJADOR_ACTIVIDAD_CDG = TRABAJADOR_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Trabajador_afi
	* Hibernate value: Trabajador.afi
	*/
	String  TRABAJADOR_AFI = TRABAJADOR_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Trabajador_cdg
	* Hibernate value: Trabajador.cdg
	*/
	String  TRABAJADOR_CDG = TRABAJADOR_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Trabajador_codnsz
	* Hibernate value: Trabajador.codnsz
	*/
	String  TRABAJADOR_CODNSZ = TRABAJADOR_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Trabajador_contrTemp
	* Hibernate value: Trabajador.contrTemp
	*/
	String  TRABAJADOR_CONTR_TEMP = TRABAJADOR_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Trabajador_domicilio_cdg
	* Hibernate value: Trabajador.domicilio.cdg
	*/
	String  TRABAJADOR_DOMICILIO_CDG = TRABAJADOR_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Trabajador_emprccc_id_cdg
	* Hibernate value: Trabajador.emprccc.id.cdg
	*/
	String  TRABAJADOR_EMPRCCC_ID_CDG = TRABAJADOR_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Trabajador_emprccc_id_tipccc
	* Hibernate value: Trabajador.emprccc.id.tipccc
	*/
	String  TRABAJADOR_EMPRCCC_ID_TIPCCC = TRABAJADOR_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Trabajador_emprccos_cdg
	* Hibernate value: Trabajador.emprccos.cdg
	*/
	String  TRABAJADOR_EMPRCCOS_CDG = TRABAJADOR_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Trabajador_empresa_cdg
	* Hibernate value: Trabajador.empresa.cdg
	*/
	String  TRABAJADOR_EMPRESA_CDG = TRABAJADOR_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Trabajador_fecalt
	* Hibernate value: Trabajador.fecalt
	*/
	String  TRABAJADOR_FECALT = TRABAJADOR_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Trabajador_fecbaj
	* Hibernate value: Trabajador.fecbaj
	*/
	String  TRABAJADOR_FECBAJ = TRABAJADOR_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Trabajador_fecmod
	* Hibernate value: Trabajador.fecmod
	*/
	String  TRABAJADOR_FECMOD = TRABAJADOR_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Trabajador_fecnew
	* Hibernate value: Trabajador.fecnew
	*/
	String  TRABAJADOR_FECNEW = TRABAJADOR_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Trabajador_hormod
	* Hibernate value: Trabajador.hormod
	*/
	String  TRABAJADOR_HORMOD = TRABAJADOR_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Trabajador_hornew
	* Hibernate value: Trabajador.hornew
	*/
	String  TRABAJADOR_HORNEW = TRABAJADOR_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Trabajador_indagrario
	* Hibernate value: Trabajador.indagrario
	*/
	String  TRABAJADOR_INDAGRARIO = TRABAJADOR_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Trabajador_indgrupo
	* Hibernate value: Trabajador.indgrupo
	*/
	String  TRABAJADOR_INDGRUPO = TRABAJADOR_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Trabajador_mayor65
	* Hibernate value: Trabajador.mayor65
	*/
	String  TRABAJADOR_MAYOR65 = TRABAJADOR_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Trabajador_pariente
	* Hibernate value: Trabajador.pariente
	*/
	String  TRABAJADOR_PARIENTE = TRABAJADOR_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Trabajador_persona_cdg
	* Hibernate value: Trabajador.persona.cdg
	*/
	String  TRABAJADOR_PERSONA_CDG = TRABAJADOR_ENTRY.getAliasNames()[20];



	/** 
	* DAOConstantsEntry for Otrperc entity.
	*/ 
	DAOConstantsEntry OTRPERC_ENTRY = DAOConstants.getDAOConstant(Otrperc.class);

	/** 
	* Alias value: Otrperc_anio
	* Hibernate value: Otrperc.anio
	*/
	String  OTRPERC_ANIO = OTRPERC_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Otrperc_aportaSs
	* Hibernate value: Otrperc.aportaSs
	*/
	String  OTRPERC_APORTA_SS = OTRPERC_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Otrperc_base
	* Hibernate value: Otrperc.base
	*/
	String  OTRPERC_BASE = OTRPERC_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Otrperc_cdg
	* Hibernate value: Otrperc.cdg
	*/
	String  OTRPERC_CDG = OTRPERC_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Otrperc_codemp
	* Hibernate value: Otrperc.codemp
	*/
	String  OTRPERC_CODEMP = OTRPERC_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Otrperc_codper
	* Hibernate value: Otrperc.codper
	*/
	String  OTRPERC_CODPER = OTRPERC_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Otrperc_clave
	* Hibernate value: Otrperc.clave
	*/
	String  OTRPERC_CLAVE = OTRPERC_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Otrperc_concepto
	* Hibernate value: Otrperc.concepto
	*/
	String  OTRPERC_CONCEPTO = OTRPERC_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Otrperc_fecha
	* Hibernate value: Otrperc.fecha
	*/
	String  OTRPERC_FECHA = OTRPERC_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Otrperc_importe
	* Hibernate value: Otrperc.importe
	*/
	String  OTRPERC_IMPORTE = OTRPERC_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Otrperc_ingreso
	* Hibernate value: Otrperc.ingreso
	*/
	String  OTRPERC_INGRESO = OTRPERC_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Otrperc_natret
	* Hibernate value: Otrperc.natret
	*/
	String  OTRPERC_NATRET = OTRPERC_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Otrperc_prcret
	* Hibernate value: Otrperc.prcret
	*/
	String  OTRPERC_PRCRET = OTRPERC_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Otrperc_retencion
	* Hibernate value: Otrperc.retencion
	*/
	String  OTRPERC_RETENCION = OTRPERC_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Otrperc_subclave
	* Hibernate value: Otrperc.subclave
	*/
	String  OTRPERC_SUBCLAVE = OTRPERC_ENTRY.getAliasNames()[14];



	/** 
	* DAOConstantsEntry for Autonomos entity.
	*/ 
	DAOConstantsEntry AUTONOMOS_ENTRY = DAOConstants.getDAOConstant(Autonomos.class);

	/** 
	* Alias value: Autonomos_baseelegida
	* Hibernate value: Autonomos.baseelegida
	*/
	String  AUTONOMOS_BASEELEGIDA = AUTONOMOS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Autonomos_basemaxima
	* Hibernate value: Autonomos.basemaxima
	*/
	String  AUTONOMOS_BASEMAXIMA = AUTONOMOS_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Autonomos_baseminima
	* Hibernate value: Autonomos.baseminima
	*/
	String  AUTONOMOS_BASEMINIMA = AUTONOMOS_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Autonomos_cdg
	* Hibernate value: Autonomos.cdg
	*/
	String  AUTONOMOS_CDG = AUTONOMOS_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Autonomos_codpos
	* Hibernate value: Autonomos.codpos
	*/
	String  AUTONOMOS_CODPOS = AUTONOMOS_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Autonomos_codregistro
	* Hibernate value: Autonomos.codregistro
	*/
	String  AUTONOMOS_CODREGISTRO = AUTONOMOS_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Autonomos_cuenta
	* Hibernate value: Autonomos.cuenta
	*/
	String  AUTONOMOS_CUENTA = AUTONOMOS_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Autonomos_dc
	* Hibernate value: Autonomos.dc
	*/
	String  AUTONOMOS_DC = AUTONOMOS_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Autonomos_desregistro
	* Hibernate value: Autonomos.desregistro
	*/
	String  AUTONOMOS_DESREGISTRO = AUTONOMOS_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Autonomos_entidad_cdg
	* Hibernate value: Autonomos.entidad.cdg
	*/
	String  AUTONOMOS_ENTIDAD_CDG = AUTONOMOS_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Autonomos_fecalta
	* Hibernate value: Autonomos.fecalta
	*/
	String  AUTONOMOS_FECALTA = AUTONOMOS_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Autonomos_fecconstitucion
	* Hibernate value: Autonomos.fecconstitucion
	*/
	String  AUTONOMOS_FECCONSTITUCION = AUTONOMOS_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Autonomos_fecfingestion
	* Hibernate value: Autonomos.fecfingestion
	*/
	String  AUTONOMOS_FECFINGESTION = AUTONOMOS_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Autonomos_fecinigestion
	* Hibernate value: Autonomos.fecinigestion
	*/
	String  AUTONOMOS_FECINIGESTION = AUTONOMOS_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Autonomos_fecmod
	* Hibernate value: Autonomos.fecmod
	*/
	String  AUTONOMOS_FECMOD = AUTONOMOS_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Autonomos_fecnew
	* Hibernate value: Autonomos.fecnew
	*/
	String  AUTONOMOS_FECNEW = AUTONOMOS_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Autonomos_folio
	* Hibernate value: Autonomos.folio
	*/
	String  AUTONOMOS_FOLIO = AUTONOMOS_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Autonomos_hoja
	* Hibernate value: Autonomos.hoja
	*/
	String  AUTONOMOS_HOJA = AUTONOMOS_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Autonomos_honorarios
	* Hibernate value: Autonomos.honorarios
	*/
	String  AUTONOMOS_HONORARIOS = AUTONOMOS_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Autonomos_hormod
	* Hibernate value: Autonomos.hormod
	*/
	String  AUTONOMOS_HORMOD = AUTONOMOS_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Autonomos_hornew
	* Hibernate value: Autonomos.hornew
	*/
	String  AUTONOMOS_HORNEW = AUTONOMOS_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Autonomos_incapacidad
	* Hibernate value: Autonomos.incapacidad
	*/
	String  AUTONOMOS_INCAPACIDAD = AUTONOMOS_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Autonomos_incremento
	* Hibernate value: Autonomos.incremento
	*/
	String  AUTONOMOS_INCREMENTO = AUTONOMOS_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Autonomos_libro
	* Hibernate value: Autonomos.libro
	*/
	String  AUTONOMOS_LIBRO = AUTONOMOS_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Autonomos_localidad
	* Hibernate value: Autonomos.localidad
	*/
	String  AUTONOMOS_LOCALIDAD = AUTONOMOS_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Autonomos_mutua_cdg
	* Hibernate value: Autonomos.mutua.cdg
	*/
	String  AUTONOMOS_MUTUA_CDG = AUTONOMOS_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Autonomos_nomvia
	* Hibernate value: Autonomos.nomvia
	*/
	String  AUTONOMOS_NOMVIA = AUTONOMOS_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: Autonomos_numero
	* Hibernate value: Autonomos.numero
	*/
	String  AUTONOMOS_NUMERO = AUTONOMOS_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: Autonomos_observaciones
	* Hibernate value: Autonomos.observaciones
	*/
	String  AUTONOMOS_OBSERVACIONES = AUTONOMOS_ENTRY.getAliasNames()[28];

	/** 
	* Alias value: Autonomos_otrdir
	* Hibernate value: Autonomos.otrdir
	*/
	String  AUTONOMOS_OTRDIR = AUTONOMOS_ENTRY.getAliasNames()[29];

	/** 
	* Alias value: Autonomos_otros
	* Hibernate value: Autonomos.otros
	*/
	String  AUTONOMOS_OTROS = AUTONOMOS_ENTRY.getAliasNames()[30];

	/** 
	* Alias value: Autonomos_persona_apellido2
	* Hibernate value: Autonomos.persona.apellido2
	*/
	String  AUTONOMOS_PERSONA_APELLIDO2 = AUTONOMOS_ENTRY.getAliasNames()[31];

	/** 
	* Alias value: Autonomos_persona_cdg
	* Hibernate value: Autonomos.persona.cdg
	*/
	String  AUTONOMOS_PERSONA_CDG = AUTONOMOS_ENTRY.getAliasNames()[32];

	/** 
	* Alias value: Autonomos_persona_codpos
	* Hibernate value: Autonomos.persona.codpos
	*/
	String  AUTONOMOS_PERSONA_CODPOS = AUTONOMOS_ENTRY.getAliasNames()[33];

	/** 
	* Alias value: Autonomos_persona_descripcion
	* Hibernate value: Autonomos.persona.descripcion
	*/
	String  AUTONOMOS_PERSONA_DESCRIPCION = AUTONOMOS_ENTRY.getAliasNames()[34];

	/** 
	* Alias value: Autonomos_persona_localidad
	* Hibernate value: Autonomos.persona.localidad
	*/
	String  AUTONOMOS_PERSONA_LOCALIDAD = AUTONOMOS_ENTRY.getAliasNames()[35];

	/** 
	* Alias value: Autonomos_persona_nombre
	* Hibernate value: Autonomos.persona.nombre
	*/
	String  AUTONOMOS_PERSONA_NOMBRE = AUTONOMOS_ENTRY.getAliasNames()[36];

	/** 
	* Alias value: Autonomos_persona_nomvia
	* Hibernate value: Autonomos.persona.nomvia
	*/
	String  AUTONOMOS_PERSONA_NOMVIA = AUTONOMOS_ENTRY.getAliasNames()[37];

	/** 
	* Alias value: Autonomos_persona_numero
	* Hibernate value: Autonomos.persona.numero
	*/
	String  AUTONOMOS_PERSONA_NUMERO = AUTONOMOS_ENTRY.getAliasNames()[38];

	/** 
	* Alias value: Autonomos_persona_numss
	* Hibernate value: Autonomos.persona.numss
	*/
	String  AUTONOMOS_PERSONA_NUMSS = AUTONOMOS_ENTRY.getAliasNames()[39];

	/** 
	* Alias value: Autonomos_persona_otrdir
	* Hibernate value: Autonomos.persona.otrdir
	*/
	String  AUTONOMOS_PERSONA_OTRDIR = AUTONOMOS_ENTRY.getAliasNames()[40];

	/** 
	* Alias value: Autonomos_persona_provincia_cdg
	* Hibernate value: Autonomos.persona.provincia.cdg
	*/
	String  AUTONOMOS_PERSONA_PROVINCIA_CDG = AUTONOMOS_ENTRY.getAliasNames()[41];

	/** 
	* Alias value: Autonomos_persona_tipovia_cdg
	* Hibernate value: Autonomos.persona.tipovia.cdg
	*/
	String  AUTONOMOS_PERSONA_TIPOVIA_CDG = AUTONOMOS_ENTRY.getAliasNames()[42];

	/** 
	* Alias value: Autonomos_provincia_cdg
	* Hibernate value: Autonomos.provincia.cdg
	*/
	String  AUTONOMOS_PROVINCIA_CDG = AUTONOMOS_ENTRY.getAliasNames()[43];

	/** 
	* Alias value: Autonomos_seccion
	* Hibernate value: Autonomos.seccion
	*/
	String  AUTONOMOS_SECCION = AUTONOMOS_ENTRY.getAliasNames()[44];

	/** 
	* Alias value: Autonomos_sucursal1_cpsuc
	* Hibernate value: Autonomos.sucursal1.cpsuc
	*/
	String  AUTONOMOS_SUCURSAL1_CPSUC = AUTONOMOS_ENTRY.getAliasNames()[45];

	/** 
	* Alias value: Autonomos_sucursal1_id_cdg
	* Hibernate value: Autonomos.sucursal1.id.cdg
	*/
	String  AUTONOMOS_SUCURSAL1_ID_CDG = AUTONOMOS_ENTRY.getAliasNames()[46];

	/** 
	* Alias value: Autonomos_sucursal1_id_codent
	* Hibernate value: Autonomos.sucursal1.id.codent
	*/
	String  AUTONOMOS_SUCURSAL1_ID_CODENT = AUTONOMOS_ENTRY.getAliasNames()[47];

	/** 
	* Alias value: Autonomos_sucursal1_domsuc
	* Hibernate value: Autonomos.sucursal1.domsuc
	*/
	String  AUTONOMOS_SUCURSAL1_DOMSUC = AUTONOMOS_ENTRY.getAliasNames()[48];

	/** 
	* Alias value: Autonomos_sucursal1_munsuc
	* Hibernate value: Autonomos.sucursal1.munsuc
	*/
	String  AUTONOMOS_SUCURSAL1_MUNSUC = AUTONOMOS_ENTRY.getAliasNames()[49];

	/** 
	* Alias value: Autonomos_tipautonomo
	* Hibernate value: Autonomos.tipautonomo
	*/
	String  AUTONOMOS_TIPAUTONOMO = AUTONOMOS_ENTRY.getAliasNames()[50];

	/** 
	* Alias value: Autonomos_tipovia_cdg
	* Hibernate value: Autonomos.tipovia.cdg
	*/
	String  AUTONOMOS_TIPOVIA_CDG = AUTONOMOS_ENTRY.getAliasNames()[51];

	/** 
	* Alias value: Autonomos_tomo
	* Hibernate value: Autonomos.tomo
	*/
	String  AUTONOMOS_TOMO = AUTONOMOS_ENTRY.getAliasNames()[52];



	/** 
	* DAOConstantsEntry for Nominaex entity.
	*/ 
	DAOConstantsEntry NOMINAEX_ENTRY = DAOConstants.getDAOConstant(Nominaex.class);

	/** 
	* Alias value: Nominaex_anio
	* Hibernate value: Nominaex.anio
	*/
	String  NOMINAEX_ANIO = NOMINAEX_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Nominaex_complemento_cdg
	* Hibernate value: Nominaex.complemento.cdg
	*/
	String  NOMINAEX_COMPLEMENTO_CDG = NOMINAEX_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Nominaex_descat
	* Hibernate value: Nominaex.descat
	*/
	String  NOMINAEX_DESCAT = NOMINAEX_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Nominaex_descom
	* Hibernate value: Nominaex.descom
	*/
	String  NOMINAEX_DESCOM = NOMINAEX_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Nominaex_direccion
	* Hibernate value: Nominaex.direccion
	*/
	String  NOMINAEX_DIRECCION = NOMINAEX_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Nominaex_divisa_cdg
	* Hibernate value: Nominaex.divisa.cdg
	*/
	String  NOMINAEX_DIVISA_CDG = NOMINAEX_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Nominaex_divisa_description
	* Hibernate value: Nominaex.divisa.description
	*/
	String  NOMINAEX_DIVISA_DESCRIPTION = NOMINAEX_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Nominaex_emprper_cdg
	* Hibernate value: Nominaex.emprper.cdg
	*/
	String  NOMINAEX_EMPRPER_CDG = NOMINAEX_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Nominaex_fecant
	* Hibernate value: Nominaex.fecant
	*/
	String  NOMINAEX_FECANT = NOMINAEX_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Nominaex_feccob
	* Hibernate value: Nominaex.feccob
	*/
	String  NOMINAEX_FECCOB = NOMINAEX_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Nominaex_feccobreal
	* Hibernate value: Nominaex.feccobreal
	*/
	String  NOMINAEX_FECCOBREAL = NOMINAEX_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Nominaex_fecemi
	* Hibernate value: Nominaex.fecemi
	*/
	String  NOMINAEX_FECEMI = NOMINAEX_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Nominaex_fecfin
	* Hibernate value: Nominaex.fecfin
	*/
	String  NOMINAEX_FECFIN = NOMINAEX_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Nominaex_fecini
	* Hibernate value: Nominaex.fecini
	*/
	String  NOMINAEX_FECINI = NOMINAEX_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Nominaex_fecmod
	* Hibernate value: Nominaex.fecmod
	*/
	String  NOMINAEX_FECMOD = NOMINAEX_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Nominaex_fecnew
	* Hibernate value: Nominaex.fecnew
	*/
	String  NOMINAEX_FECNEW = NOMINAEX_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Nominaex_fijovar
	* Hibernate value: Nominaex.fijovar
	*/
	String  NOMINAEX_FIJOVAR = NOMINAEX_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Nominaex_hormod
	* Hibernate value: Nominaex.hormod
	*/
	String  NOMINAEX_HORMOD = NOMINAEX_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Nominaex_hornew
	* Hibernate value: Nominaex.hornew
	*/
	String  NOMINAEX_HORNEW = NOMINAEX_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Nominaex_id_cdg
	* Hibernate value: Nominaex.id.cdg
	*/
	String  NOMINAEX_ID_CDG = NOMINAEX_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Nominaex_id_numero
	* Hibernate value: Nominaex.id.numero
	*/
	String  NOMINAEX_ID_NUMERO = NOMINAEX_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Nominaex_impirpf
	* Hibernate value: Nominaex.impirpf
	*/
	String  NOMINAEX_IMPIRPF = NOMINAEX_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Nominaex_importe
	* Hibernate value: Nominaex.importe
	*/
	String  NOMINAEX_IMPORTE = NOMINAEX_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Nominaex_irpf
	* Hibernate value: Nominaex.irpf
	*/
	String  NOMINAEX_IRPF = NOMINAEX_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Nominaex_liquido
	* Hibernate value: Nominaex.liquido
	*/
	String  NOMINAEX_LIQUIDO = NOMINAEX_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Nominaex_localidad
	* Hibernate value: Nominaex.localidad
	*/
	String  NOMINAEX_LOCALIDAD = NOMINAEX_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Nominaex_mes
	* Hibernate value: Nominaex.mes
	*/
	String  NOMINAEX_MES = NOMINAEX_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: Nominaex_nomdtoex_concepto
	* Hibernate value: Nominaex.nomdtoex.concepto
	*/
	String  NOMINAEX_NOMDTOEX_CONCEPTO = NOMINAEX_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: Nominaex_nomdtoex_fecmod
	* Hibernate value: Nominaex.nomdtoex.fecmod
	*/
	String  NOMINAEX_NOMDTOEX_FECMOD = NOMINAEX_ENTRY.getAliasNames()[28];

	/** 
	* Alias value: Nominaex_nomdtoex_fecnew
	* Hibernate value: Nominaex.nomdtoex.fecnew
	*/
	String  NOMINAEX_NOMDTOEX_FECNEW = NOMINAEX_ENTRY.getAliasNames()[29];

	/** 
	* Alias value: Nominaex_nomdtoex_hormod
	* Hibernate value: Nominaex.nomdtoex.hormod
	*/
	String  NOMINAEX_NOMDTOEX_HORMOD = NOMINAEX_ENTRY.getAliasNames()[30];

	/** 
	* Alias value: Nominaex_nomdtoex_hornew
	* Hibernate value: Nominaex.nomdtoex.hornew
	*/
	String  NOMINAEX_NOMDTOEX_HORNEW = NOMINAEX_ENTRY.getAliasNames()[31];

	/** 
	* Alias value: Nominaex_nomdtoex_id_cdg
	* Hibernate value: Nominaex.nomdtoex.id.cdg
	*/
	String  NOMINAEX_NOMDTOEX_ID_CDG = NOMINAEX_ENTRY.getAliasNames()[32];

	/** 
	* Alias value: Nominaex_nomdtoex_id_linea
	* Hibernate value: Nominaex.nomdtoex.id.linea
	*/
	String  NOMINAEX_NOMDTOEX_ID_LINEA = NOMINAEX_ENTRY.getAliasNames()[33];

	/** 
	* Alias value: Nominaex_nomdtoex_id_numero
	* Hibernate value: Nominaex.nomdtoex.id.numero
	*/
	String  NOMINAEX_NOMDTOEX_ID_NUMERO = NOMINAEX_ENTRY.getAliasNames()[34];

	/** 
	* Alias value: Nominaex_nomdtoex_importe
	* Hibernate value: Nominaex.nomdtoex.importe
	*/
	String  NOMINAEX_NOMDTOEX_IMPORTE = NOMINAEX_ENTRY.getAliasNames()[35];

	/** 
	* Alias value: Nominaex_nomemp
	* Hibernate value: Nominaex.nomemp
	*/
	String  NOMINAEX_NOMEMP = NOMINAEX_ENTRY.getAliasNames()[36];

	/** 
	* Alias value: Nominaex_nomper
	* Hibernate value: Nominaex.nomper
	*/
	String  NOMINAEX_NOMPER = NOMINAEX_ENTRY.getAliasNames()[37];

	/** 
	* Alias value: Nominaex_nummat
	* Hibernate value: Nominaex.nummat
	*/
	String  NOMINAEX_NUMMAT = NOMINAEX_ENTRY.getAliasNames()[38];

	/** 
	* Alias value: Nominaex_profesion
	* Hibernate value: Nominaex.profesion
	*/
	String  NOMINAEX_PROFESION = NOMINAEX_ENTRY.getAliasNames()[39];

	/** 
	* Alias value: Nominaex_totalDeducir
	* Hibernate value: Nominaex.totalDeducir
	*/
	String  NOMINAEX_TOTAL_DEDUCIR = NOMINAEX_ENTRY.getAliasNames()[40];



	/** 
	* DAOConstantsEntry for Nomdtoex entity.
	*/ 
	DAOConstantsEntry NOMDTOEX_ENTRY = DAOConstants.getDAOConstant(Nomdtoex.class);

	/** 
	* Alias value: Nomdtoex_concepto
	* Hibernate value: Nomdtoex.concepto
	*/
	String  NOMDTOEX_CONCEPTO = NOMDTOEX_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Nomdtoex_fecmod
	* Hibernate value: Nomdtoex.fecmod
	*/
	String  NOMDTOEX_FECMOD = NOMDTOEX_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Nomdtoex_fecnew
	* Hibernate value: Nomdtoex.fecnew
	*/
	String  NOMDTOEX_FECNEW = NOMDTOEX_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Nomdtoex_hormod
	* Hibernate value: Nomdtoex.hormod
	*/
	String  NOMDTOEX_HORMOD = NOMDTOEX_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Nomdtoex_hornew
	* Hibernate value: Nomdtoex.hornew
	*/
	String  NOMDTOEX_HORNEW = NOMDTOEX_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Nomdtoex_id_cdg
	* Hibernate value: Nomdtoex.id.cdg
	*/
	String  NOMDTOEX_ID_CDG = NOMDTOEX_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Nomdtoex_id_linea
	* Hibernate value: Nomdtoex.id.linea
	*/
	String  NOMDTOEX_ID_LINEA = NOMDTOEX_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Nomdtoex_id_numero
	* Hibernate value: Nomdtoex.id.numero
	*/
	String  NOMDTOEX_ID_NUMERO = NOMDTOEX_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Nomdtoex_importe
	* Hibernate value: Nomdtoex.importe
	*/
	String  NOMDTOEX_IMPORTE = NOMDTOEX_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Nomdtoex_nominaex_id_cdg
	* Hibernate value: Nomdtoex.nominaex.id.cdg
	*/
	String  NOMDTOEX_NOMINAEX_ID_CDG = NOMDTOEX_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Nomdtoex_nominaex_id_numero
	* Hibernate value: Nomdtoex.nominaex.id.numero
	*/
	String  NOMDTOEX_NOMINAEX_ID_NUMERO = NOMDTOEX_ENTRY.getAliasNames()[10];



	/** 
	* DAOConstantsEntry for Autbases entity.
	*/ 
	DAOConstantsEntry AUTBASES_ENTRY = DAOConstants.getDAOConstant(Autbases.class);

	/** 
	* Alias value: Autbases_autonomos_cdg
	* Hibernate value: Autbases.autonomos.cdg
	*/
	String  AUTBASES_AUTONOMOS_CDG = AUTBASES_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Autbases_base
	* Hibernate value: Autbases.base
	*/
	String  AUTBASES_BASE = AUTBASES_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Autbases_cuota
	* Hibernate value: Autbases.cuota
	*/
	String  AUTBASES_CUOTA = AUTBASES_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Autbases_fecfin
	* Hibernate value: Autbases.fecfin
	*/
	String  AUTBASES_FECFIN = AUTBASES_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Autbases_id_cdg
	* Hibernate value: Autbases.id.cdg
	*/
	String  AUTBASES_ID_CDG = AUTBASES_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Autbases_id_fecini
	* Hibernate value: Autbases.id.fecini
	*/
	String  AUTBASES_ID_FECINI = AUTBASES_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Percep entity.
	*/ 
	DAOConstantsEntry PERCEP_ENTRY = DAOConstants.getDAOConstant(Percep.class);

	/** 
	* Alias value: Percep_calculo
	* Hibernate value: Percep.calculo
	*/
	String  PERCEP_CALCULO = PERCEP_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Percep_complemento1_cdg
	* Hibernate value: Percep.complemento1.cdg
	*/
	String  PERCEP_COMPLEMENTO1_CDG = PERCEP_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Percep_complemento_cdg
	* Hibernate value: Percep.complemento.cdg
	*/
	String  PERCEP_COMPLEMENTO_CDG = PERCEP_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Percep_desabr
	* Hibernate value: Percep.desabr
	*/
	String  PERCEP_DESABR = PERCEP_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Percep_descom
	* Hibernate value: Percep.descom
	*/
	String  PERCEP_DESCOM = PERCEP_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Percep_dinesp
	* Hibernate value: Percep.dinesp
	*/
	String  PERCEP_DINESP = PERCEP_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Percep_fecfin
	* Hibernate value: Percep.fecfin
	*/
	String  PERCEP_FECFIN = PERCEP_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Percep_fecini
	* Hibernate value: Percep.fecini
	*/
	String  PERCEP_FECINI = PERCEP_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Percep_fecmod
	* Hibernate value: Percep.fecmod
	*/
	String  PERCEP_FECMOD = PERCEP_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Percep_fecnew
	* Hibernate value: Percep.fecnew
	*/
	String  PERCEP_FECNEW = PERCEP_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Percep_fecret
	* Hibernate value: Percep.fecret
	*/
	String  PERCEP_FECRET = PERCEP_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Percep_fijovar
	* Hibernate value: Percep.fijovar
	*/
	String  PERCEP_FIJOVAR = PERCEP_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Percep_garilt
	* Hibernate value: Percep.garilt
	*/
	String  PERCEP_GARILT = PERCEP_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Percep_hormod
	* Hibernate value: Percep.hormod
	*/
	String  PERCEP_HORMOD = PERCEP_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Percep_hornew
	* Hibernate value: Percep.hornew
	*/
	String  PERCEP_HORNEW = PERCEP_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Percep_id_cdg
	* Hibernate value: Percep.id.cdg
	*/
	String  PERCEP_ID_CDG = PERCEP_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Percep_id_numero
	* Hibernate value: Percep.id.numero
	*/
	String  PERCEP_ID_NUMERO = PERCEP_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Percep_importe
	* Hibernate value: Percep.importe
	*/
	String  PERCEP_IMPORTE = PERCEP_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Percep_impuni
	* Hibernate value: Percep.impuni
	*/
	String  PERCEP_IMPUNI = PERCEP_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Percep_indcom
	* Hibernate value: Percep.indcom
	*/
	String  PERCEP_INDCOM = PERCEP_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Percep_mes
	* Hibernate value: Percep.mes
	*/
	String  PERCEP_MES = PERCEP_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Percep_redext
	* Hibernate value: Percep.redext
	*/
	String  PERCEP_REDEXT = PERCEP_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Percep_tipcom
	* Hibernate value: Percep.tipcom
	*/
	String  PERCEP_TIPCOM = PERCEP_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Percep_tipcot
	* Hibernate value: Percep.tipcot
	*/
	String  PERCEP_TIPCOT = PERCEP_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Percep_trabajador_cdg
	* Hibernate value: Percep.trabajador.cdg
	*/
	String  PERCEP_TRABAJADOR_CDG = PERCEP_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Percep_unidades
	* Hibernate value: Percep.unidades
	*/
	String  PERCEP_UNIDADES = PERCEP_ENTRY.getAliasNames()[25];



	/** 
	* DAOConstantsEntry for Emprctra entity.
	*/ 
	DAOConstantsEntry EMPRCTRA_ENTRY = DAOConstants.getDAOConstant(Emprctra.class);

	/** 
	* Alias value: Emprctra_actividad_cdg
	* Hibernate value: Emprctra.actividad.cdg
	*/
	String  EMPRCTRA_ACTIVIDAD_CDG = EMPRCTRA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Emprctra_convenio_cdg
	* Hibernate value: Emprctra.convenio.cdg
	*/
	String  EMPRCTRA_CONVENIO_CDG = EMPRCTRA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Emprctra_domicilio_cdg
	* Hibernate value: Emprctra.domicilio.cdg
	*/
	String  EMPRCTRA_DOMICILIO_CDG = EMPRCTRA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Emprctra_empresa_cdg
	* Hibernate value: Emprctra.empresa.cdg
	*/
	String  EMPRCTRA_EMPRESA_CDG = EMPRCTRA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Emprctra_envioss
	* Hibernate value: Emprctra.envioss
	*/
	String  EMPRCTRA_ENVIOSS = EMPRCTRA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Emprctra_fiestas
	* Hibernate value: Emprctra.fiestas
	*/
	String  EMPRCTRA_FIESTAS = EMPRCTRA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Emprctra_horario
	* Hibernate value: Emprctra.horario
	*/
	String  EMPRCTRA_HORARIO = EMPRCTRA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Emprctra_id_cdg
	* Hibernate value: Emprctra.id.cdg
	*/
	String  EMPRCTRA_ID_CDG = EMPRCTRA_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Emprctra_id_codact
	* Hibernate value: Emprctra.id.codact
	*/
	String  EMPRCTRA_ID_CODACT = EMPRCTRA_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Emprctra_id_domicilio
	* Hibernate value: Emprctra.id.domicilio
	*/
	String  EMPRCTRA_ID_DOMICILIO = EMPRCTRA_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Emprctra_indcal
	* Hibernate value: Emprctra.indcal
	*/
	String  EMPRCTRA_INDCAL = EMPRCTRA_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Emprctra_indcoste
	* Hibernate value: Emprctra.indcoste
	*/
	String  EMPRCTRA_INDCOSTE = EMPRCTRA_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Emprctra_inddia
	* Hibernate value: Emprctra.inddia
	*/
	String  EMPRCTRA_INDDIA = EMPRCTRA_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Emprctra_indnom
	* Hibernate value: Emprctra.indnom
	*/
	String  EMPRCTRA_INDNOM = EMPRCTRA_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Emprctra_jornada
	* Hibernate value: Emprctra.jornada
	*/
	String  EMPRCTRA_JORNADA = EMPRCTRA_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Emprctra_maquina
	* Hibernate value: Emprctra.maquina
	*/
	String  EMPRCTRA_MAQUINA = EMPRCTRA_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Emprctra_pelectri
	* Hibernate value: Emprctra.pelectri
	*/
	String  EMPRCTRA_PELECTRI = EMPRCTRA_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Emprctra_represen
	* Hibernate value: Emprctra.represen
	*/
	String  EMPRCTRA_REPRESEN = EMPRCTRA_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Emprctra_superficie
	* Hibernate value: Emprctra.superficie
	*/
	String  EMPRCTRA_SUPERFICIE = EMPRCTRA_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Emprctra_toxicos
	* Hibernate value: Emprctra.toxicos
	*/
	String  EMPRCTRA_TOXICOS = EMPRCTRA_ENTRY.getAliasNames()[19];



	/** 
	* DAOConstantsEntry for Emprccc entity.
	*/ 
	DAOConstantsEntry EMPRCCC_ENTRY = DAOConstants.getDAOConstant(Emprccc.class);

	/** 
	* Alias value: Emprccc_actividad_cdg
	* Hibernate value: Emprccc.actividad.cdg
	*/
	String  EMPRCCC_ACTIVIDAD_CDG = EMPRCCC_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Emprccc_concol
	* Hibernate value: Emprccc.concol
	*/
	String  EMPRCCC_CONCOL = EMPRCCC_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Emprccc_descripcion
	* Hibernate value: Emprccc.descripcion
	*/
	String  EMPRCCC_DESCRIPCION = EMPRCCC_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Emprccc_id_cdg
	* Hibernate value: Emprccc.id.cdg
	*/
	String  EMPRCCC_ID_CDG = EMPRCCC_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Emprccc_id_tipccc
	* Hibernate value: Emprccc.id.tipccc
	*/
	String  EMPRCCC_ID_TIPCCC = EMPRCCC_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Emprccc_indss
	* Hibernate value: Emprccc.indss
	*/
	String  EMPRCCC_INDSS = EMPRCCC_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Emprccc_mutua_cdg
	* Hibernate value: Emprccc.mutua.cdg
	*/
	String  EMPRCCC_MUTUA_CDG = EMPRCCC_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Emprccc_seguro
	* Hibernate value: Emprccc.seguro
	*/
	String  EMPRCCC_SEGURO = EMPRCCC_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for Emprccos entity.
	*/ 
	DAOConstantsEntry EMPRCCOS_ENTRY = DAOConstants.getDAOConstant(Emprccos.class);

	/** 
	* Alias value: Emprccos_actividad_cdg
	* Hibernate value: Emprccos.actividad.cdg
	*/
	String  EMPRCCOS_ACTIVIDAD_CDG = EMPRCCOS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Emprccos_cdg
	* Hibernate value: Emprccos.cdg
	*/
	String  EMPRCCOS_CDG = EMPRCCOS_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Emprccos_descripcion
	* Hibernate value: Emprccos.descripcion
	*/
	String  EMPRCCOS_DESCRIPCION = EMPRCCOS_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for Persona entity.
	*/ 
	DAOConstantsEntry PERSONA_ENTRY = DAOConstants.getDAOConstant(Persona.class);

	/** 
	* Alias value: Persona_alias
	* Hibernate value: Persona.alias
	*/
	String  PERSONA_ALIAS = PERSONA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Persona_aliastc2
	* Hibernate value: Persona.aliastc2
	*/
	String  PERSONA_ALIASTC2 = PERSONA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Persona_apellido2
	* Hibernate value: Persona.apellido2
	*/
	String  PERSONA_APELLIDO2 = PERSONA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Persona_cdg
	* Hibernate value: Persona.cdg
	*/
	String  PERSONA_CDG = PERSONA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Persona_codpos
	* Hibernate value: Persona.codpos
	*/
	String  PERSONA_CODPOS = PERSONA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Persona_descripcion
	* Hibernate value: Persona.descripcion
	*/
	String  PERSONA_DESCRIPCION = PERSONA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Persona_email
	* Hibernate value: Persona.email
	*/
	String  PERSONA_EMAIL = PERSONA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Persona_estciv
	* Hibernate value: Persona.estciv
	*/
	String  PERSONA_ESTCIV = PERSONA_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Persona_fax
	* Hibernate value: Persona.fax
	*/
	String  PERSONA_FAX = PERSONA_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Persona_fecmod
	* Hibernate value: Persona.fecmod
	*/
	String  PERSONA_FECMOD = PERSONA_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Persona_fecnac
	* Hibernate value: Persona.fecnac
	*/
	String  PERSONA_FECNAC = PERSONA_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Persona_fecnew
	* Hibernate value: Persona.fecnew
	*/
	String  PERSONA_FECNEW = PERSONA_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Persona_hormod
	* Hibernate value: Persona.hormod
	*/
	String  PERSONA_HORMOD = PERSONA_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Persona_hornew
	* Hibernate value: Persona.hornew
	*/
	String  PERSONA_HORNEW = PERSONA_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Persona_localidad
	* Hibernate value: Persona.localidad
	*/
	String  PERSONA_LOCALIDAD = PERSONA_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Persona_lugnac
	* Hibernate value: Persona.lugnac
	*/
	String  PERSONA_LUGNAC = PERSONA_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Persona_madre
	* Hibernate value: Persona.madre
	*/
	String  PERSONA_MADRE = PERSONA_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Persona_nacion_cdg
	* Hibernate value: Persona.nacion.cdg
	*/
	String  PERSONA_NACION_CDG = PERSONA_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Persona_nombre
	* Hibernate value: Persona.nombre
	*/
	String  PERSONA_NOMBRE = PERSONA_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Persona_nomvia
	* Hibernate value: Persona.nomvia
	*/
	String  PERSONA_NOMVIA = PERSONA_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Persona_numdoc
	* Hibernate value: Persona.numdoc
	*/
	String  PERSONA_NUMDOC = PERSONA_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Persona_numero
	* Hibernate value: Persona.numero
	*/
	String  PERSONA_NUMERO = PERSONA_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Persona_numss
	* Hibernate value: Persona.numss
	*/
	String  PERSONA_NUMSS = PERSONA_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Persona_obsper
	* Hibernate value: Persona.obsper
	*/
	String  PERSONA_OBSPER = PERSONA_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Persona_otrdir
	* Hibernate value: Persona.otrdir
	*/
	String  PERSONA_OTRDIR = PERSONA_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Persona_padre
	* Hibernate value: Persona.padre
	*/
	String  PERSONA_PADRE = PERSONA_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Persona_pais1_cdg
	* Hibernate value: Persona.pais1.cdg
	*/
	String  PERSONA_PAIS1_CDG = PERSONA_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: Persona_pais_cdg
	* Hibernate value: Persona.pais.cdg
	*/
	String  PERSONA_PAIS_CDG = PERSONA_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: Persona_provincia1_cdg
	* Hibernate value: Persona.provincia1.cdg
	*/
	String  PERSONA_PROVINCIA1_CDG = PERSONA_ENTRY.getAliasNames()[28];

	/** 
	* Alias value: Persona_provincia_cdg
	* Hibernate value: Persona.provincia.cdg
	*/
	String  PERSONA_PROVINCIA_CDG = PERSONA_ENTRY.getAliasNames()[29];

	/** 
	* Alias value: Persona_sexo
	* Hibernate value: Persona.sexo
	*/
	String  PERSONA_SEXO = PERSONA_ENTRY.getAliasNames()[30];

	/** 
	* Alias value: Persona_telefono
	* Hibernate value: Persona.telefono
	*/
	String  PERSONA_TELEFONO = PERSONA_ENTRY.getAliasNames()[31];

	/** 
	* Alias value: Persona_tipdoc_cdg
	* Hibernate value: Persona.tipdoc.cdg
	*/
	String  PERSONA_TIPDOC_CDG = PERSONA_ENTRY.getAliasNames()[32];

	/** 
	* Alias value: Persona_tipovia_cdg
	* Hibernate value: Persona.tipovia.cdg
	*/
	String  PERSONA_TIPOVIA_CDG = PERSONA_ENTRY.getAliasNames()[33];



	/** 
	* DAOConstantsEntry for Impresos11x entity.
	*/ 
	DAOConstantsEntry IMPRESOS11X_ENTRY = DAOConstants.getDAOConstant(Impresos11x.class);

	/** 
	* Alias value: Impresos11x_actdinimp
	* Hibernate value: Impresos11x.actdinimp
	*/
	String  IMPRESOS11X_ACTDINIMP = IMPRESOS11X_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Impresos11x_actdinper
	* Hibernate value: Impresos11x.actdinper
	*/
	String  IMPRESOS11X_ACTDINPER = IMPRESOS11X_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Impresos11x_actdinret
	* Hibernate value: Impresos11x.actdinret
	*/
	String  IMPRESOS11X_ACTDINRET = IMPRESOS11X_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Impresos11x_actespimp
	* Hibernate value: Impresos11x.actespimp
	*/
	String  IMPRESOS11X_ACTESPIMP = IMPRESOS11X_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Impresos11x_actespper
	* Hibernate value: Impresos11x.actespper
	*/
	String  IMPRESOS11X_ACTESPPER = IMPRESOS11X_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Impresos11x_actespret
	* Hibernate value: Impresos11x.actespret
	*/
	String  IMPRESOS11X_ACTESPRET = IMPRESOS11X_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Impresos11x_admon_cdg
	* Hibernate value: Impresos11x.admon.cdg
	*/
	String  IMPRESOS11X_ADMON_CDG = IMPRESOS11X_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Impresos11x_anio
	* Hibernate value: Impresos11x.anio
	*/
	String  IMPRESOS11X_ANIO = IMPRESOS11X_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Impresos11x_cdg
	* Hibernate value: Impresos11x.cdg
	*/
	String  IMPRESOS11X_CDG = IMPRESOS11X_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Impresos11x_cuenta
	* Hibernate value: Impresos11x.cuenta
	*/
	String  IMPRESOS11X_CUENTA = IMPRESOS11X_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Impresos11x_dc
	* Hibernate value: Impresos11x.dc
	*/
	String  IMPRESOS11X_DC = IMPRESOS11X_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Impresos11x_divisa_cdg
	* Hibernate value: Impresos11x.divisa.cdg
	*/
	String  IMPRESOS11X_DIVISA_CDG = IMPRESOS11X_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Impresos11x_emprnif_cdg
	* Hibernate value: Impresos11x.emprnif.cdg
	*/
	String  IMPRESOS11X_EMPRNIF_CDG = IMPRESOS11X_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Impresos11x_entidad
	* Hibernate value: Impresos11x.entidad
	*/
	String  IMPRESOS11X_ENTIDAD = IMPRESOS11X_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Impresos11x_fecha
	* Hibernate value: Impresos11x.fecha
	*/
	String  IMPRESOS11X_FECHA = IMPRESOS11X_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Impresos11x_fecmod
	* Hibernate value: Impresos11x.fecmod
	*/
	String  IMPRESOS11X_FECMOD = IMPRESOS11X_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Impresos11x_fecnew
	* Hibernate value: Impresos11x.fecnew
	*/
	String  IMPRESOS11X_FECNEW = IMPRESOS11X_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Impresos11x_fecremimp
	* Hibernate value: Impresos11x.fecremimp
	*/
	String  IMPRESOS11X_FECREMIMP = IMPRESOS11X_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Impresos11x_fpago
	* Hibernate value: Impresos11x.fpago
	*/
	String  IMPRESOS11X_FPAGO = IMPRESOS11X_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Impresos11x_hormod
	* Hibernate value: Impresos11x.hormod
	*/
	String  IMPRESOS11X_HORMOD = IMPRESOS11X_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Impresos11x_hornew
	* Hibernate value: Impresos11x.hornew
	*/
	String  IMPRESOS11X_HORNEW = IMPRESOS11X_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Impresos11x_imgimp
	* Hibernate value: Impresos11x.imgimp
	*/
	String  IMPRESOS11X_IMGIMP = IMPRESOS11X_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Impresos11x_imgper
	* Hibernate value: Impresos11x.imgper
	*/
	String  IMPRESOS11X_IMGPER = IMPRESOS11X_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Impresos11x_imgret
	* Hibernate value: Impresos11x.imgret
	*/
	String  IMPRESOS11X_IMGRET = IMPRESOS11X_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Impresos11x_liqtotal
	* Hibernate value: Impresos11x.liqtotal
	*/
	String  IMPRESOS11X_LIQTOTAL = IMPRESOS11X_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Impresos11x_mes
	* Hibernate value: Impresos11x.mes
	*/
	String  IMPRESOS11X_MES = IMPRESOS11X_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Impresos11x_modimpuesto
	* Hibernate value: Impresos11x.modimpuesto
	*/
	String  IMPRESOS11X_MODIMPUESTO = IMPRESOS11X_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: Impresos11x_nrc
	* Hibernate value: Impresos11x.nrc
	*/
	String  IMPRESOS11X_NRC = IMPRESOS11X_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: Impresos11x_predinimp
	* Hibernate value: Impresos11x.predinimp
	*/
	String  IMPRESOS11X_PREDINIMP = IMPRESOS11X_ENTRY.getAliasNames()[28];

	/** 
	* Alias value: Impresos11x_predinper
	* Hibernate value: Impresos11x.predinper
	*/
	String  IMPRESOS11X_PREDINPER = IMPRESOS11X_ENTRY.getAliasNames()[29];

	/** 
	* Alias value: Impresos11x_predinret
	* Hibernate value: Impresos11x.predinret
	*/
	String  IMPRESOS11X_PREDINRET = IMPRESOS11X_ENTRY.getAliasNames()[30];

	/** 
	* Alias value: Impresos11x_preespimp
	* Hibernate value: Impresos11x.preespimp
	*/
	String  IMPRESOS11X_PREESPIMP = IMPRESOS11X_ENTRY.getAliasNames()[31];

	/** 
	* Alias value: Impresos11x_preespper
	* Hibernate value: Impresos11x.preespper
	*/
	String  IMPRESOS11X_PREESPPER = IMPRESOS11X_ENTRY.getAliasNames()[32];

	/** 
	* Alias value: Impresos11x_preespret
	* Hibernate value: Impresos11x.preespret
	*/
	String  IMPRESOS11X_PREESPRET = IMPRESOS11X_ENTRY.getAliasNames()[33];

	/** 
	* Alias value: Impresos11x_provincia_cdg
	* Hibernate value: Impresos11x.provincia.cdg
	*/
	String  IMPRESOS11X_PROVINCIA_CDG = IMPRESOS11X_ENTRY.getAliasNames()[34];

	/** 
	* Alias value: Impresos11x_sucursal
	* Hibernate value: Impresos11x.sucursal
	*/
	String  IMPRESOS11X_SUCURSAL = IMPRESOS11X_ENTRY.getAliasNames()[35];

	/** 
	* Alias value: Impresos11x_tipo
	* Hibernate value: Impresos11x.tipo
	*/
	String  IMPRESOS11X_TIPO = IMPRESOS11X_ENTRY.getAliasNames()[36];

	/** 
	* Alias value: Impresos11x_tradinimp
	* Hibernate value: Impresos11x.tradinimp
	*/
	String  IMPRESOS11X_TRADINIMP = IMPRESOS11X_ENTRY.getAliasNames()[37];

	/** 
	* Alias value: Impresos11x_tradinper
	* Hibernate value: Impresos11x.tradinper
	*/
	String  IMPRESOS11X_TRADINPER = IMPRESOS11X_ENTRY.getAliasNames()[38];

	/** 
	* Alias value: Impresos11x_tradinret
	* Hibernate value: Impresos11x.tradinret
	*/
	String  IMPRESOS11X_TRADINRET = IMPRESOS11X_ENTRY.getAliasNames()[39];

	/** 
	* Alias value: Impresos11x_traespimp
	* Hibernate value: Impresos11x.traespimp
	*/
	String  IMPRESOS11X_TRAESPIMP = IMPRESOS11X_ENTRY.getAliasNames()[40];

	/** 
	* Alias value: Impresos11x_traespper
	* Hibernate value: Impresos11x.traespper
	*/
	String  IMPRESOS11X_TRAESPPER = IMPRESOS11X_ENTRY.getAliasNames()[41];

	/** 
	* Alias value: Impresos11x_traespret
	* Hibernate value: Impresos11x.traespret
	*/
	String  IMPRESOS11X_TRAESPRET = IMPRESOS11X_ENTRY.getAliasNames()[42];

	/** 
	* Alias value: Impresos11x_trimestre
	* Hibernate value: Impresos11x.trimestre
	*/
	String  IMPRESOS11X_TRIMESTRE = IMPRESOS11X_ENTRY.getAliasNames()[43];



	/** 
	* DAOConstantsEntry for Impresos190 entity.
	*/ 
	DAOConstantsEntry IMPRESOS190_ENTRY = DAOConstants.getDAOConstant(Impresos190.class);

	/** 
	* Alias value: Impresos190_admon_cdg
	* Hibernate value: Impresos190.admon.cdg
	*/
	String  IMPRESOS190_ADMON_CDG = IMPRESOS190_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Impresos190_anio
	* Hibernate value: Impresos190.anio
	*/
	String  IMPRESOS190_ANIO = IMPRESOS190_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Impresos190_cdg
	* Hibernate value: Impresos190.cdg
	*/
	String  IMPRESOS190_CDG = IMPRESOS190_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Impresos190_descuadrado
	* Hibernate value: Impresos190.descuadrado
	*/
	String  IMPRESOS190_DESCUADRADO = IMPRESOS190_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Impresos190_disco
	* Hibernate value: Impresos190.disco
	*/
	String  IMPRESOS190_DISCO = IMPRESOS190_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Impresos190_divisa_cdg
	* Hibernate value: Impresos190.divisa.cdg
	*/
	String  IMPRESOS190_DIVISA_CDG = IMPRESOS190_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Impresos190_emprnif1_cdg
	* Hibernate value: Impresos190.emprnif1.cdg
	*/
	String  IMPRESOS190_EMPRNIF1_CDG = IMPRESOS190_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Impresos190_emprnif2_cdg
	* Hibernate value: Impresos190.emprnif2.cdg
	*/
	String  IMPRESOS190_EMPRNIF2_CDG = IMPRESOS190_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Impresos190_emprnif_cdg
	* Hibernate value: Impresos190.emprnif.cdg
	*/
	String  IMPRESOS190_EMPRNIF_CDG = IMPRESOS190_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Impresos190_fecha
	* Hibernate value: Impresos190.fecha
	*/
	String  IMPRESOS190_FECHA = IMPRESOS190_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Impresos190_fecmod
	* Hibernate value: Impresos190.fecmod
	*/
	String  IMPRESOS190_FECMOD = IMPRESOS190_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Impresos190_fecnew
	* Hibernate value: Impresos190.fecnew
	*/
	String  IMPRESOS190_FECNEW = IMPRESOS190_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Impresos190_hormod
	* Hibernate value: Impresos190.hormod
	*/
	String  IMPRESOS190_HORMOD = IMPRESOS190_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Impresos190_hornew
	* Hibernate value: Impresos190.hornew
	*/
	String  IMPRESOS190_HORNEW = IMPRESOS190_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Impresos190_impPercep
	* Hibernate value: Impresos190.impPercep
	*/
	String  IMPRESOS190_IMP_PERCEP = IMPRESOS190_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Impresos190_impRetenc
	* Hibernate value: Impresos190.impRetenc
	*/
	String  IMPRESOS190_IMP_RETENC = IMPRESOS190_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Impresos190_lineasImpresos190_anionac
	* Hibernate value: Impresos190.lineasImpresos190.anionac
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_ANIONAC = IMPRESOS190_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Impresos190_lineasImpresos190_ascdi33
	* Hibernate value: Impresos190.lineasImpresos190.ascdi33
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_ASCDI33 = IMPRESOS190_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Impresos190_lineasImpresos190_ascdi33e
	* Hibernate value: Impresos190.lineasImpresos190.ascdi33e
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_ASCDI33E = IMPRESOS190_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Impresos190_lineasImpresos190_ascdi65
	* Hibernate value: Impresos190.lineasImpresos190.ascdi65
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_ASCDI65 = IMPRESOS190_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Impresos190_lineasImpresos190_ascdi65e
	* Hibernate value: Impresos190.lineasImpresos190.ascdi65e
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_ASCDI65E = IMPRESOS190_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Impresos190_lineasImpresos190_ascdimr
	* Hibernate value: Impresos190.lineasImpresos190.ascdimr
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_ASCDIMR = IMPRESOS190_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Impresos190_lineasImpresos190_ascdimre
	* Hibernate value: Impresos190.lineasImpresos190.ascdimre
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_ASCDIMRE = IMPRESOS190_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Impresos190_lineasImpresos190_ascentero
	* Hibernate value: Impresos190.lineasImpresos190.ascentero
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_ASCENTERO = IMPRESOS190_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Impresos190_lineasImpresos190_ascma75
	* Hibernate value: Impresos190.lineasImpresos190.ascma75
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_ASCMA75 = IMPRESOS190_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Impresos190_lineasImpresos190_ascma75e
	* Hibernate value: Impresos190.lineasImpresos190.ascma75e
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_ASCMA75E = IMPRESOS190_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Impresos190_lineasImpresos190_ascme75
	* Hibernate value: Impresos190.lineasImpresos190.ascme75
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_ASCME75 = IMPRESOS190_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: Impresos190_lineasImpresos190_ascme75e
	* Hibernate value: Impresos190.lineasImpresos190.ascme75e
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_ASCME75E = IMPRESOS190_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: Impresos190_lineasImpresos190_ascminus33
	* Hibernate value: Impresos190.lineasImpresos190.ascminus33
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_ASCMINUS33 = IMPRESOS190_ENTRY.getAliasNames()[28];

	/** 
	* Alias value: Impresos190_lineasImpresos190_ascminus65
	* Hibernate value: Impresos190.lineasImpresos190.ascminus65
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_ASCMINUS65 = IMPRESOS190_ENTRY.getAliasNames()[29];

	/** 
	* Alias value: Impresos190_lineasImpresos190_cM
	* Hibernate value: Impresos190.lineasImpresos190.cM
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_C_M = IMPRESOS190_ENTRY.getAliasNames()[30];

	/** 
	* Alias value: Impresos190_lineasImpresos190_clave
	* Hibernate value: Impresos190.lineasImpresos190.clave
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_CLAVE = IMPRESOS190_ENTRY.getAliasNames()[31];

	/** 
	* Alias value: Impresos190_lineasImpresos190_descdi33
	* Hibernate value: Impresos190.lineasImpresos190.descdi33
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_DESCDI33 = IMPRESOS190_ENTRY.getAliasNames()[32];

	/** 
	* Alias value: Impresos190_lineasImpresos190_descdi33e
	* Hibernate value: Impresos190.lineasImpresos190.descdi33e
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_DESCDI33E = IMPRESOS190_ENTRY.getAliasNames()[33];

	/** 
	* Alias value: Impresos190_lineasImpresos190_descdi65
	* Hibernate value: Impresos190.lineasImpresos190.descdi65
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_DESCDI65 = IMPRESOS190_ENTRY.getAliasNames()[34];

	/** 
	* Alias value: Impresos190_lineasImpresos190_descdi65e
	* Hibernate value: Impresos190.lineasImpresos190.descdi65e
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_DESCDI65E = IMPRESOS190_ENTRY.getAliasNames()[35];

	/** 
	* Alias value: Impresos190_lineasImpresos190_descdimr
	* Hibernate value: Impresos190.lineasImpresos190.descdimr
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_DESCDIMR = IMPRESOS190_ENTRY.getAliasNames()[36];

	/** 
	* Alias value: Impresos190_lineasImpresos190_descdimre
	* Hibernate value: Impresos190.lineasImpresos190.descdimre
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_DESCDIMRE = IMPRESOS190_ENTRY.getAliasNames()[37];

	/** 
	* Alias value: Impresos190_lineasImpresos190_descentero
	* Hibernate value: Impresos190.lineasImpresos190.descentero
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_DESCENTERO = IMPRESOS190_ENTRY.getAliasNames()[38];

	/** 
	* Alias value: Impresos190_lineasImpresos190_descma3
	* Hibernate value: Impresos190.lineasImpresos190.descma3
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_DESCMA3 = IMPRESOS190_ENTRY.getAliasNames()[39];

	/** 
	* Alias value: Impresos190_lineasImpresos190_descma3e
	* Hibernate value: Impresos190.lineasImpresos190.descma3e
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_DESCMA3E = IMPRESOS190_ENTRY.getAliasNames()[40];

	/** 
	* Alias value: Impresos190_lineasImpresos190_descme3
	* Hibernate value: Impresos190.lineasImpresos190.descme3
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_DESCME3 = IMPRESOS190_ENTRY.getAliasNames()[41];

	/** 
	* Alias value: Impresos190_lineasImpresos190_descme3e
	* Hibernate value: Impresos190.lineasImpresos190.descme3e
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_DESCME3E = IMPRESOS190_ENTRY.getAliasNames()[42];

	/** 
	* Alias value: Impresos190_lineasImpresos190_devengo
	* Hibernate value: Impresos190.lineasImpresos190.devengo
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_DEVENGO = IMPRESOS190_ENTRY.getAliasNames()[43];

	/** 
	* Alias value: Impresos190_lineasImpresos190_hijo16
	* Hibernate value: Impresos190.lineasImpresos190.hijo16
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_HIJO16 = IMPRESOS190_ENTRY.getAliasNames()[44];

	/** 
	* Alias value: Impresos190_lineasImpresos190_hijo25
	* Hibernate value: Impresos190.lineasImpresos190.hijo25
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_HIJO25 = IMPRESOS190_ENTRY.getAliasNames()[45];

	/** 
	* Alias value: Impresos190_lineasImpresos190_hijo3
	* Hibernate value: Impresos190.lineasImpresos190.hijo3
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_HIJO3 = IMPRESOS190_ENTRY.getAliasNames()[46];

	/** 
	* Alias value: Impresos190_lineasImpresos190_hijos
	* Hibernate value: Impresos190.lineasImpresos190.hijos
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_HIJOS = IMPRESOS190_ENTRY.getAliasNames()[47];

	/** 
	* Alias value: Impresos190_lineasImpresos190_id_cdg
	* Hibernate value: Impresos190.lineasImpresos190.id.cdg
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_ID_CDG = IMPRESOS190_ENTRY.getAliasNames()[48];

	/** 
	* Alias value: Impresos190_lineasImpresos190_id_linea
	* Hibernate value: Impresos190.lineasImpresos190.id.linea
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_ID_LINEA = IMPRESOS190_ENTRY.getAliasNames()[49];

	/** 
	* Alias value: Impresos190_lineasImpresos190_impAnual
	* Hibernate value: Impresos190.lineasImpresos190.impAnual
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_IMP_ANUAL = IMPRESOS190_ENTRY.getAliasNames()[50];

	/** 
	* Alias value: Impresos190_lineasImpresos190_impGastos
	* Hibernate value: Impresos190.lineasImpresos190.impGastos
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_IMP_GASTOS = IMPRESOS190_ENTRY.getAliasNames()[51];

	/** 
	* Alias value: Impresos190_lineasImpresos190_impIngCta
	* Hibernate value: Impresos190.lineasImpresos190.impIngCta
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_IMP_ING_CTA = IMPRESOS190_ENTRY.getAliasNames()[52];

	/** 
	* Alias value: Impresos190_lineasImpresos190_impIngRep
	* Hibernate value: Impresos190.lineasImpresos190.impIngRep
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_IMP_ING_REP = IMPRESOS190_ENTRY.getAliasNames()[53];

	/** 
	* Alias value: Impresos190_lineasImpresos190_impPension
	* Hibernate value: Impresos190.lineasImpresos190.impPension
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_IMP_PENSION = IMPRESOS190_ENTRY.getAliasNames()[54];

	/** 
	* Alias value: Impresos190_lineasImpresos190_impPerDin
	* Hibernate value: Impresos190.lineasImpresos190.impPerDin
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_IMP_PER_DIN = IMPRESOS190_ENTRY.getAliasNames()[55];

	/** 
	* Alias value: Impresos190_lineasImpresos190_impPerEsp
	* Hibernate value: Impresos190.lineasImpresos190.impPerEsp
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_IMP_PER_ESP = IMPRESOS190_ENTRY.getAliasNames()[56];

	/** 
	* Alias value: Impresos190_lineasImpresos190_impReducc
	* Hibernate value: Impresos190.lineasImpresos190.impReducc
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_IMP_REDUCC = IMPRESOS190_ENTRY.getAliasNames()[57];

	/** 
	* Alias value: Impresos190_lineasImpresos190_impRetDin
	* Hibernate value: Impresos190.lineasImpresos190.impRetDin
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_IMP_RET_DIN = IMPRESOS190_ENTRY.getAliasNames()[58];

	/** 
	* Alias value: Impresos190_lineasImpresos190_impresos190_cdg
	* Hibernate value: Impresos190.lineasImpresos190.impresos190.cdg
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_IMPRESOS190_CDG = IMPRESOS190_ENTRY.getAliasNames()[59];

	/** 
	* Alias value: Impresos190_lineasImpresos190_minus33
	* Hibernate value: Impresos190.lineasImpresos190.minus33
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_MINUS33 = IMPRESOS190_ENTRY.getAliasNames()[60];

	/** 
	* Alias value: Impresos190_lineasImpresos190_minus65
	* Hibernate value: Impresos190.lineasImpresos190.minus65
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_MINUS65 = IMPRESOS190_ENTRY.getAliasNames()[61];

	/** 
	* Alias value: Impresos190_lineasImpresos190_movilidad
	* Hibernate value: Impresos190.lineasImpresos190.movilidad
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_MOVILIDAD = IMPRESOS190_ENTRY.getAliasNames()[62];

	/** 
	* Alias value: Impresos190_lineasImpresos190_nifcony
	* Hibernate value: Impresos190.lineasImpresos190.nifcony
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_NIFCONY = IMPRESOS190_ENTRY.getAliasNames()[63];

	/** 
	* Alias value: Impresos190_lineasImpresos190_nomapel
	* Hibernate value: Impresos190.lineasImpresos190.nomapel
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_NOMAPEL = IMPRESOS190_ENTRY.getAliasNames()[64];

	/** 
	* Alias value: Impresos190_lineasImpresos190_numdoc
	* Hibernate value: Impresos190.lineasImpresos190.numdoc
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_NUMDOC = IMPRESOS190_ENTRY.getAliasNames()[65];

	/** 
	* Alias value: Impresos190_lineasImpresos190_prolongacion
	* Hibernate value: Impresos190.lineasImpresos190.prolongacion
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_PROLONGACION = IMPRESOS190_ENTRY.getAliasNames()[66];

	/** 
	* Alias value: Impresos190_lineasImpresos190_provincia
	* Hibernate value: Impresos190.lineasImpresos190.provincia
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_PROVINCIA = IMPRESOS190_ENTRY.getAliasNames()[67];

	/** 
	* Alias value: Impresos190_lineasImpresos190_relacion
	* Hibernate value: Impresos190.lineasImpresos190.relacion
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_RELACION = IMPRESOS190_ENTRY.getAliasNames()[68];

	/** 
	* Alias value: Impresos190_lineasImpresos190_sitfam
	* Hibernate value: Impresos190.lineasImpresos190.sitfam
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_SITFAM = IMPRESOS190_ENTRY.getAliasNames()[69];

	/** 
	* Alias value: Impresos190_lineasImpresos190_subclave
	* Hibernate value: Impresos190.lineasImpresos190.subclave
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_SUBCLAVE = IMPRESOS190_ENTRY.getAliasNames()[70];

	/** 
	* Alias value: Impresos190_lineasImpresos190_totalasc
	* Hibernate value: Impresos190.lineasImpresos190.totalasc
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_TOTALASC = IMPRESOS190_ENTRY.getAliasNames()[71];

	/** 
	* Alias value: Impresos190_lineasImpresos190_xminus
	* Hibernate value: Impresos190.lineasImpresos190.xminus
	*/
	String  IMPRESOS190_LINEAS_IMPRESOS190_XMINUS = IMPRESOS190_ENTRY.getAliasNames()[72];

	/** 
	* Alias value: Impresos190_numPercep
	* Hibernate value: Impresos190.numPercep
	*/
	String  IMPRESOS190_NUM_PERCEP = IMPRESOS190_ENTRY.getAliasNames()[73];

	/** 
	* Alias value: Impresos190_provincia_cdg
	* Hibernate value: Impresos190.provincia.cdg
	*/
	String  IMPRESOS190_PROVINCIA_CDG = IMPRESOS190_ENTRY.getAliasNames()[74];



	/** 
	* DAOConstantsEntry for LinImpresos190 entity.
	*/ 
	DAOConstantsEntry LIN_IMPRESOS190_ENTRY = DAOConstants.getDAOConstant(LinImpresos190.class);

	/** 
	* Alias value: LinImpresos190_anionac
	* Hibernate value: LinImpresos190.anionac
	*/
	String  LIN_IMPRESOS190_ANIONAC = LIN_IMPRESOS190_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: LinImpresos190_ascdi33
	* Hibernate value: LinImpresos190.ascdi33
	*/
	String  LIN_IMPRESOS190_ASCDI33 = LIN_IMPRESOS190_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: LinImpresos190_ascdi33e
	* Hibernate value: LinImpresos190.ascdi33e
	*/
	String  LIN_IMPRESOS190_ASCDI33E = LIN_IMPRESOS190_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: LinImpresos190_ascdi65
	* Hibernate value: LinImpresos190.ascdi65
	*/
	String  LIN_IMPRESOS190_ASCDI65 = LIN_IMPRESOS190_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: LinImpresos190_ascdi65e
	* Hibernate value: LinImpresos190.ascdi65e
	*/
	String  LIN_IMPRESOS190_ASCDI65E = LIN_IMPRESOS190_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: LinImpresos190_ascdimr
	* Hibernate value: LinImpresos190.ascdimr
	*/
	String  LIN_IMPRESOS190_ASCDIMR = LIN_IMPRESOS190_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: LinImpresos190_ascdimre
	* Hibernate value: LinImpresos190.ascdimre
	*/
	String  LIN_IMPRESOS190_ASCDIMRE = LIN_IMPRESOS190_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: LinImpresos190_ascentero
	* Hibernate value: LinImpresos190.ascentero
	*/
	String  LIN_IMPRESOS190_ASCENTERO = LIN_IMPRESOS190_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: LinImpresos190_ascma75
	* Hibernate value: LinImpresos190.ascma75
	*/
	String  LIN_IMPRESOS190_ASCMA75 = LIN_IMPRESOS190_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: LinImpresos190_ascma75e
	* Hibernate value: LinImpresos190.ascma75e
	*/
	String  LIN_IMPRESOS190_ASCMA75E = LIN_IMPRESOS190_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: LinImpresos190_ascme75
	* Hibernate value: LinImpresos190.ascme75
	*/
	String  LIN_IMPRESOS190_ASCME75 = LIN_IMPRESOS190_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: LinImpresos190_ascme75e
	* Hibernate value: LinImpresos190.ascme75e
	*/
	String  LIN_IMPRESOS190_ASCME75E = LIN_IMPRESOS190_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: LinImpresos190_ascminus33
	* Hibernate value: LinImpresos190.ascminus33
	*/
	String  LIN_IMPRESOS190_ASCMINUS33 = LIN_IMPRESOS190_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: LinImpresos190_ascminus65
	* Hibernate value: LinImpresos190.ascminus65
	*/
	String  LIN_IMPRESOS190_ASCMINUS65 = LIN_IMPRESOS190_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: LinImpresos190_cM
	* Hibernate value: LinImpresos190.cM
	*/
	String  LIN_IMPRESOS190_C_M = LIN_IMPRESOS190_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: LinImpresos190_clave
	* Hibernate value: LinImpresos190.clave
	*/
	String  LIN_IMPRESOS190_CLAVE = LIN_IMPRESOS190_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: LinImpresos190_descdi33
	* Hibernate value: LinImpresos190.descdi33
	*/
	String  LIN_IMPRESOS190_DESCDI33 = LIN_IMPRESOS190_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: LinImpresos190_descdi33e
	* Hibernate value: LinImpresos190.descdi33e
	*/
	String  LIN_IMPRESOS190_DESCDI33E = LIN_IMPRESOS190_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: LinImpresos190_descdi65
	* Hibernate value: LinImpresos190.descdi65
	*/
	String  LIN_IMPRESOS190_DESCDI65 = LIN_IMPRESOS190_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: LinImpresos190_descdi65e
	* Hibernate value: LinImpresos190.descdi65e
	*/
	String  LIN_IMPRESOS190_DESCDI65E = LIN_IMPRESOS190_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: LinImpresos190_descdimr
	* Hibernate value: LinImpresos190.descdimr
	*/
	String  LIN_IMPRESOS190_DESCDIMR = LIN_IMPRESOS190_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: LinImpresos190_descdimre
	* Hibernate value: LinImpresos190.descdimre
	*/
	String  LIN_IMPRESOS190_DESCDIMRE = LIN_IMPRESOS190_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: LinImpresos190_descentero
	* Hibernate value: LinImpresos190.descentero
	*/
	String  LIN_IMPRESOS190_DESCENTERO = LIN_IMPRESOS190_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: LinImpresos190_descma3
	* Hibernate value: LinImpresos190.descma3
	*/
	String  LIN_IMPRESOS190_DESCMA3 = LIN_IMPRESOS190_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: LinImpresos190_descma3e
	* Hibernate value: LinImpresos190.descma3e
	*/
	String  LIN_IMPRESOS190_DESCMA3E = LIN_IMPRESOS190_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: LinImpresos190_descme3
	* Hibernate value: LinImpresos190.descme3
	*/
	String  LIN_IMPRESOS190_DESCME3 = LIN_IMPRESOS190_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: LinImpresos190_descme3e
	* Hibernate value: LinImpresos190.descme3e
	*/
	String  LIN_IMPRESOS190_DESCME3E = LIN_IMPRESOS190_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: LinImpresos190_devengo
	* Hibernate value: LinImpresos190.devengo
	*/
	String  LIN_IMPRESOS190_DEVENGO = LIN_IMPRESOS190_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: LinImpresos190_hijo16
	* Hibernate value: LinImpresos190.hijo16
	*/
	String  LIN_IMPRESOS190_HIJO16 = LIN_IMPRESOS190_ENTRY.getAliasNames()[28];

	/** 
	* Alias value: LinImpresos190_hijo25
	* Hibernate value: LinImpresos190.hijo25
	*/
	String  LIN_IMPRESOS190_HIJO25 = LIN_IMPRESOS190_ENTRY.getAliasNames()[29];

	/** 
	* Alias value: LinImpresos190_hijo3
	* Hibernate value: LinImpresos190.hijo3
	*/
	String  LIN_IMPRESOS190_HIJO3 = LIN_IMPRESOS190_ENTRY.getAliasNames()[30];

	/** 
	* Alias value: LinImpresos190_hijos
	* Hibernate value: LinImpresos190.hijos
	*/
	String  LIN_IMPRESOS190_HIJOS = LIN_IMPRESOS190_ENTRY.getAliasNames()[31];

	/** 
	* Alias value: LinImpresos190_id_cdg
	* Hibernate value: LinImpresos190.id.cdg
	*/
	String  LIN_IMPRESOS190_ID_CDG = LIN_IMPRESOS190_ENTRY.getAliasNames()[32];

	/** 
	* Alias value: LinImpresos190_id_linea
	* Hibernate value: LinImpresos190.id.linea
	*/
	String  LIN_IMPRESOS190_ID_LINEA = LIN_IMPRESOS190_ENTRY.getAliasNames()[33];

	/** 
	* Alias value: LinImpresos190_impAnual
	* Hibernate value: LinImpresos190.impAnual
	*/
	String  LIN_IMPRESOS190_IMP_ANUAL = LIN_IMPRESOS190_ENTRY.getAliasNames()[34];

	/** 
	* Alias value: LinImpresos190_impGastos
	* Hibernate value: LinImpresos190.impGastos
	*/
	String  LIN_IMPRESOS190_IMP_GASTOS = LIN_IMPRESOS190_ENTRY.getAliasNames()[35];

	/** 
	* Alias value: LinImpresos190_impIngCta
	* Hibernate value: LinImpresos190.impIngCta
	*/
	String  LIN_IMPRESOS190_IMP_ING_CTA = LIN_IMPRESOS190_ENTRY.getAliasNames()[36];

	/** 
	* Alias value: LinImpresos190_impIngRep
	* Hibernate value: LinImpresos190.impIngRep
	*/
	String  LIN_IMPRESOS190_IMP_ING_REP = LIN_IMPRESOS190_ENTRY.getAliasNames()[37];

	/** 
	* Alias value: LinImpresos190_impPension
	* Hibernate value: LinImpresos190.impPension
	*/
	String  LIN_IMPRESOS190_IMP_PENSION = LIN_IMPRESOS190_ENTRY.getAliasNames()[38];

	/** 
	* Alias value: LinImpresos190_impPerDin
	* Hibernate value: LinImpresos190.impPerDin
	*/
	String  LIN_IMPRESOS190_IMP_PER_DIN = LIN_IMPRESOS190_ENTRY.getAliasNames()[39];

	/** 
	* Alias value: LinImpresos190_impPerEsp
	* Hibernate value: LinImpresos190.impPerEsp
	*/
	String  LIN_IMPRESOS190_IMP_PER_ESP = LIN_IMPRESOS190_ENTRY.getAliasNames()[40];

	/** 
	* Alias value: LinImpresos190_impReducc
	* Hibernate value: LinImpresos190.impReducc
	*/
	String  LIN_IMPRESOS190_IMP_REDUCC = LIN_IMPRESOS190_ENTRY.getAliasNames()[41];

	/** 
	* Alias value: LinImpresos190_impRetDin
	* Hibernate value: LinImpresos190.impRetDin
	*/
	String  LIN_IMPRESOS190_IMP_RET_DIN = LIN_IMPRESOS190_ENTRY.getAliasNames()[42];

	/** 
	* Alias value: LinImpresos190_impresos190_cdg
	* Hibernate value: LinImpresos190.impresos190.cdg
	*/
	String  LIN_IMPRESOS190_IMPRESOS190_CDG = LIN_IMPRESOS190_ENTRY.getAliasNames()[43];

	/** 
	* Alias value: LinImpresos190_minus33
	* Hibernate value: LinImpresos190.minus33
	*/
	String  LIN_IMPRESOS190_MINUS33 = LIN_IMPRESOS190_ENTRY.getAliasNames()[44];

	/** 
	* Alias value: LinImpresos190_minus65
	* Hibernate value: LinImpresos190.minus65
	*/
	String  LIN_IMPRESOS190_MINUS65 = LIN_IMPRESOS190_ENTRY.getAliasNames()[45];

	/** 
	* Alias value: LinImpresos190_movilidad
	* Hibernate value: LinImpresos190.movilidad
	*/
	String  LIN_IMPRESOS190_MOVILIDAD = LIN_IMPRESOS190_ENTRY.getAliasNames()[46];

	/** 
	* Alias value: LinImpresos190_nifcony
	* Hibernate value: LinImpresos190.nifcony
	*/
	String  LIN_IMPRESOS190_NIFCONY = LIN_IMPRESOS190_ENTRY.getAliasNames()[47];

	/** 
	* Alias value: LinImpresos190_nomapel
	* Hibernate value: LinImpresos190.nomapel
	*/
	String  LIN_IMPRESOS190_NOMAPEL = LIN_IMPRESOS190_ENTRY.getAliasNames()[48];

	/** 
	* Alias value: LinImpresos190_numdoc
	* Hibernate value: LinImpresos190.numdoc
	*/
	String  LIN_IMPRESOS190_NUMDOC = LIN_IMPRESOS190_ENTRY.getAliasNames()[49];

	/** 
	* Alias value: LinImpresos190_prolongacion
	* Hibernate value: LinImpresos190.prolongacion
	*/
	String  LIN_IMPRESOS190_PROLONGACION = LIN_IMPRESOS190_ENTRY.getAliasNames()[50];

	/** 
	* Alias value: LinImpresos190_provincia
	* Hibernate value: LinImpresos190.provincia
	*/
	String  LIN_IMPRESOS190_PROVINCIA = LIN_IMPRESOS190_ENTRY.getAliasNames()[51];

	/** 
	* Alias value: LinImpresos190_relacion
	* Hibernate value: LinImpresos190.relacion
	*/
	String  LIN_IMPRESOS190_RELACION = LIN_IMPRESOS190_ENTRY.getAliasNames()[52];

	/** 
	* Alias value: LinImpresos190_sitfam
	* Hibernate value: LinImpresos190.sitfam
	*/
	String  LIN_IMPRESOS190_SITFAM = LIN_IMPRESOS190_ENTRY.getAliasNames()[53];

	/** 
	* Alias value: LinImpresos190_subclave
	* Hibernate value: LinImpresos190.subclave
	*/
	String  LIN_IMPRESOS190_SUBCLAVE = LIN_IMPRESOS190_ENTRY.getAliasNames()[54];

	/** 
	* Alias value: LinImpresos190_totalasc
	* Hibernate value: LinImpresos190.totalasc
	*/
	String  LIN_IMPRESOS190_TOTALASC = LIN_IMPRESOS190_ENTRY.getAliasNames()[55];

	/** 
	* Alias value: LinImpresos190_xminus
	* Hibernate value: LinImpresos190.xminus
	*/
	String  LIN_IMPRESOS190_XMINUS = LIN_IMPRESOS190_ENTRY.getAliasNames()[56];



	/** 
	* DAOConstantsEntry for Httaviso entity.
	*/ 
	DAOConstantsEntry HTTAVISO_ENTRY = DAOConstants.getDAOConstant(Httaviso.class);

	/** 
	* Alias value: Httaviso_fecha
	* Hibernate value: Httaviso.fecha
	*/
	String  HTTAVISO_FECHA = HTTAVISO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Httaviso_httrabajador_cdg
	* Hibernate value: Httaviso.httrabajador.cdg
	*/
	String  HTTAVISO_HTTRABAJADOR_CDG = HTTAVISO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Httaviso_id_cdg
	* Hibernate value: Httaviso.id.cdg
	*/
	String  HTTAVISO_ID_CDG = HTTAVISO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Httaviso_id_orden
	* Hibernate value: Httaviso.id.orden
	*/
	String  HTTAVISO_ID_ORDEN = HTTAVISO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Httaviso_texto
	* Hibernate value: Httaviso.texto
	*/
	String  HTTAVISO_TEXTO = HTTAVISO_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Httaviso_tipo
	* Hibernate value: Httaviso.tipo
	*/
	String  HTTAVISO_TIPO = HTTAVISO_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Httbonificacion entity.
	*/ 
	DAOConstantsEntry HTTBONIFICACION_ENTRY = DAOConstants.getDAOConstant(Httbonificacion.class);

	/** 
	* Alias value: Httbonificacion_bonificacion
	* Hibernate value: Httbonificacion.bonificacion
	*/
	String  HTTBONIFICACION_BONIFICACION = HTTBONIFICACION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Httbonificacion_fecfin
	* Hibernate value: Httbonificacion.fecfin
	*/
	String  HTTBONIFICACION_FECFIN = HTTBONIFICACION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Httbonificacion_fecinicio
	* Hibernate value: Httbonificacion.fecinicio
	*/
	String  HTTBONIFICACION_FECINICIO = HTTBONIFICACION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Httbonificacion_horas
	* Hibernate value: Httbonificacion.horas
	*/
	String  HTTBONIFICACION_HORAS = HTTBONIFICACION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Httbonificacion_httrabajador_cdg
	* Hibernate value: Httbonificacion.httrabajador.cdg
	*/
	String  HTTBONIFICACION_HTTRABAJADOR_CDG = HTTBONIFICACION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Httbonificacion_id_cdg
	* Hibernate value: Httbonificacion.id.cdg
	*/
	String  HTTBONIFICACION_ID_CDG = HTTBONIFICACION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Httbonificacion_id_orden
	* Hibernate value: Httbonificacion.id.orden
	*/
	String  HTTBONIFICACION_ID_ORDEN = HTTBONIFICACION_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Httbonificacion_importe
	* Hibernate value: Httbonificacion.importe
	*/
	String  HTTBONIFICACION_IMPORTE = HTTBONIFICACION_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for Httcomplemento entity.
	*/ 
	DAOConstantsEntry HTTCOMPLEMENTO_ENTRY = DAOConstants.getDAOConstant(Httcomplemento.class);

	/** 
	* Alias value: Httcomplemento_calculo
	* Hibernate value: Httcomplemento.calculo
	*/
	String  HTTCOMPLEMENTO_CALCULO = HTTCOMPLEMENTO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Httcomplemento_complemento
	* Hibernate value: Httcomplemento.complemento
	*/
	String  HTTCOMPLEMENTO_COMPLEMENTO = HTTCOMPLEMENTO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Httcomplemento_cotizacion
	* Hibernate value: Httcomplemento.cotizacion
	*/
	String  HTTCOMPLEMENTO_COTIZACION = HTTCOMPLEMENTO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Httcomplemento_garantizado
	* Hibernate value: Httcomplemento.garantizado
	*/
	String  HTTCOMPLEMENTO_GARANTIZADO = HTTCOMPLEMENTO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Httcomplemento_httrabajador_cdg
	* Hibernate value: Httcomplemento.httrabajador.cdg
	*/
	String  HTTCOMPLEMENTO_HTTRABAJADOR_CDG = HTTCOMPLEMENTO_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Httcomplemento_id_cdg
	* Hibernate value: Httcomplemento.id.cdg
	*/
	String  HTTCOMPLEMENTO_ID_CDG = HTTCOMPLEMENTO_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Httcomplemento_id_orden
	* Hibernate value: Httcomplemento.id.orden
	*/
	String  HTTCOMPLEMENTO_ID_ORDEN = HTTCOMPLEMENTO_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Httcomplemento_importe
	* Hibernate value: Httcomplemento.importe
	*/
	String  HTTCOMPLEMENTO_IMPORTE = HTTCOMPLEMENTO_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Httcomplemento_mes
	* Hibernate value: Httcomplemento.mes
	*/
	String  HTTCOMPLEMENTO_MES = HTTCOMPLEMENTO_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Httcomplemento_redondeo
	* Hibernate value: Httcomplemento.redondeo
	*/
	String  HTTCOMPLEMENTO_REDONDEO = HTTCOMPLEMENTO_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for Httrabajador entity.
	*/ 
	DAOConstantsEntry HTTRABAJADOR_ENTRY = DAOConstants.getDAOConstant(Httrabajador.class);

	/** 
	* Alias value: Httrabajador_actividad_cdg
	* Hibernate value: Httrabajador.actividad.cdg
	*/
	String  HTTRABAJADOR_ACTIVIDAD_CDG = HTTRABAJADOR_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Httrabajador_actualizar
	* Hibernate value: Httrabajador.actualizar
	*/
	String  HTTRABAJADOR_ACTUALIZAR = HTTRABAJADOR_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Httrabajador_apellido1
	* Hibernate value: Httrabajador.apellido1
	*/
	String  HTTRABAJADOR_APELLIDO1 = HTTRABAJADOR_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Httrabajador_apellido2
	* Hibernate value: Httrabajador.apellido2
	*/
	String  HTTRABAJADOR_APELLIDO2 = HTTRABAJADOR_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Httrabajador_baseantiguedad
	* Hibernate value: Httrabajador.baseantiguedad
	*/
	String  HTTRABAJADOR_BASEANTIGUEDAD = HTTRABAJADOR_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Httrabajador_basecoti_cdg
	* Hibernate value: Httrabajador.basecoti.cdg
	*/
	String  HTTRABAJADOR_BASECOTI_CDG = HTTRABAJADOR_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Httrabajador_categoria
	* Hibernate value: Httrabajador.categoria
	*/
	String  HTTRABAJADOR_CATEGORIA = HTTRABAJADOR_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Httrabajador_ccc
	* Hibernate value: Httrabajador.ccc
	*/
	String  HTTRABAJADOR_CCC = HTTRABAJADOR_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Httrabajador_cdg
	* Hibernate value: Httrabajador.cdg
	*/
	String  HTTRABAJADOR_CDG = HTTRABAJADOR_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Httrabajador_cno
	* Hibernate value: Httrabajador.cno
	*/
	String  HTTRABAJADOR_CNO = HTTRABAJADOR_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Httrabajador_codpos
	* Hibernate value: Httrabajador.codpos
	*/
	String  HTTRABAJADOR_CODPOS = HTTRABAJADOR_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Httrabajador_coeficiente
	* Hibernate value: Httrabajador.coeficiente
	*/
	String  HTTRABAJADOR_COEFICIENTE = HTTRABAJADOR_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Httrabajador_cotizacion
	* Hibernate value: Httrabajador.cotizacion
	*/
	String  HTTRABAJADOR_COTIZACION = HTTRABAJADOR_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Httrabajador_cuenta
	* Hibernate value: Httrabajador.cuenta
	*/
	String  HTTRABAJADOR_CUENTA = HTTRABAJADOR_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Httrabajador_dc
	* Hibernate value: Httrabajador.dc
	*/
	String  HTTRABAJADOR_DC = HTTRABAJADOR_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Httrabajador_descategoria
	* Hibernate value: Httrabajador.descategoria
	*/
	String  HTTRABAJADOR_DESCATEGORIA = HTTRABAJADOR_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Httrabajador_diascontrato
	* Hibernate value: Httrabajador.diascontrato
	*/
	String  HTTRABAJADOR_DIASCONTRATO = HTTRABAJADOR_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Httrabajador_documento_cdg
	* Hibernate value: Httrabajador.documento.cdg
	*/
	String  HTTRABAJADOR_DOCUMENTO_CDG = HTTRABAJADOR_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Httrabajador_domicilio_cdg
	* Hibernate value: Httrabajador.domicilio.cdg
	*/
	String  HTTRABAJADOR_DOMICILIO_CDG = HTTRABAJADOR_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Httrabajador_dtoincidencia
	* Hibernate value: Httrabajador.dtoincidencia
	*/
	String  HTTRABAJADOR_DTOINCIDENCIA = HTTRABAJADOR_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Httrabajador_dtoit
	* Hibernate value: Httrabajador.dtoit
	*/
	String  HTTRABAJADOR_DTOIT = HTTRABAJADOR_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Httrabajador_entidad_cdg
	* Hibernate value: Httrabajador.entidad.cdg
	*/
	String  HTTRABAJADOR_ENTIDAD_CDG = HTTRABAJADOR_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Httrabajador_epigrafe_cdg
	* Hibernate value: Httrabajador.epigrafe.cdg
	*/
	String  HTTRABAJADOR_EPIGRAFE_CDG = HTTRABAJADOR_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Httrabajador_especial
	* Hibernate value: Httrabajador.especial
	*/
	String  HTTRABAJADOR_ESPECIAL = HTTRABAJADOR_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Httrabajador_estado
	* Hibernate value: Httrabajador.estado
	*/
	String  HTTRABAJADOR_ESTADO = HTTRABAJADOR_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Httrabajador_fecalta
	* Hibernate value: Httrabajador.fecalta
	*/
	String  HTTRABAJADOR_FECALTA = HTTRABAJADOR_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Httrabajador_fecantiguedad
	* Hibernate value: Httrabajador.fecantiguedad
	*/
	String  HTTRABAJADOR_FECANTIGUEDAD = HTTRABAJADOR_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: Httrabajador_fecbaja
	* Hibernate value: Httrabajador.fecbaja
	*/
	String  HTTRABAJADOR_FECBAJA = HTTRABAJADOR_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: Httrabajador_fecfin
	* Hibernate value: Httrabajador.fecfin
	*/
	String  HTTRABAJADOR_FECFIN = HTTRABAJADOR_ENTRY.getAliasNames()[28];

	/** 
	* Alias value: Httrabajador_fecinicio
	* Hibernate value: Httrabajador.fecinicio
	*/
	String  HTTRABAJADOR_FECINICIO = HTTRABAJADOR_ENTRY.getAliasNames()[29];

	/** 
	* Alias value: Httrabajador_fecnac
	* Hibernate value: Httrabajador.fecnac
	*/
	String  HTTRABAJADOR_FECNAC = HTTRABAJADOR_ENTRY.getAliasNames()[30];

	/** 
	* Alias value: Httrabajador_fecpluriempleo
	* Hibernate value: Httrabajador.fecpluriempleo
	*/
	String  HTTRABAJADOR_FECPLURIEMPLEO = HTTRABAJADOR_ENTRY.getAliasNames()[31];

	/** 
	* Alias value: Httrabajador_indirpf
	* Hibernate value: Httrabajador.indirpf
	*/
	String  HTTRABAJADOR_INDIRPF = HTTRABAJADOR_ENTRY.getAliasNames()[32];

	/** 
	* Alias value: Httrabajador_irpf
	* Hibernate value: Httrabajador.irpf
	*/
	String  HTTRABAJADOR_IRPF = HTTRABAJADOR_ENTRY.getAliasNames()[33];

	/** 
	* Alias value: Httrabajador_jornada
	* Hibernate value: Httrabajador.jornada
	*/
	String  HTTRABAJADOR_JORNADA = HTTRABAJADOR_ENTRY.getAliasNames()[34];

	/** 
	* Alias value: Httrabajador_jornadatp
	* Hibernate value: Httrabajador.jornadatp
	*/
	String  HTTRABAJADOR_JORNADATP = HTTRABAJADOR_ENTRY.getAliasNames()[35];

	/** 
	* Alias value: Httrabajador_localidad
	* Hibernate value: Httrabajador.localidad
	*/
	String  HTTRABAJADOR_LOCALIDAD = HTTRABAJADOR_ENTRY.getAliasNames()[36];

	/** 
	* Alias value: Httrabajador_lugnac
	* Hibernate value: Httrabajador.lugnac
	*/
	String  HTTRABAJADOR_LUGNAC = HTTRABAJADOR_ENTRY.getAliasNames()[37];

	/** 
	* Alias value: Httrabajador_madre
	* Hibernate value: Httrabajador.madre
	*/
	String  HTTRABAJADOR_MADRE = HTTRABAJADOR_ENTRY.getAliasNames()[38];

	/** 
	* Alias value: Httrabajador_matricula
	* Hibernate value: Httrabajador.matricula
	*/
	String  HTTRABAJADOR_MATRICULA = HTTRABAJADOR_ENTRY.getAliasNames()[39];

	/** 
	* Alias value: Httrabajador_maxpluriempleo
	* Hibernate value: Httrabajador.maxpluriempleo
	*/
	String  HTTRABAJADOR_MAXPLURIEMPLEO = HTTRABAJADOR_ENTRY.getAliasNames()[40];

	/** 
	* Alias value: Httrabajador_minpluriempleo
	* Hibernate value: Httrabajador.minpluriempleo
	*/
	String  HTTRABAJADOR_MINPLURIEMPLEO = HTTRABAJADOR_ENTRY.getAliasNames()[41];

	/** 
	* Alias value: Httrabajador_minutosdiastp
	* Hibernate value: Httrabajador.minutosdiastp
	*/
	String  HTTRABAJADOR_MINUTOSDIASTP = HTTRABAJADOR_ENTRY.getAliasNames()[42];

	/** 
	* Alias value: Httrabajador_nivel
	* Hibernate value: Httrabajador.nivel
	*/
	String  HTTRABAJADOR_NIVEL = HTTRABAJADOR_ENTRY.getAliasNames()[43];

	/** 
	* Alias value: Httrabajador_nombre
	* Hibernate value: Httrabajador.nombre
	*/
	String  HTTRABAJADOR_NOMBRE = HTTRABAJADOR_ENTRY.getAliasNames()[44];

	/** 
	* Alias value: Httrabajador_nomvia
	* Hibernate value: Httrabajador.nomvia
	*/
	String  HTTRABAJADOR_NOMVIA = HTTRABAJADOR_ENTRY.getAliasNames()[45];

	/** 
	* Alias value: Httrabajador_numdoc
	* Hibernate value: Httrabajador.numdoc
	*/
	String  HTTRABAJADOR_NUMDOC = HTTRABAJADOR_ENTRY.getAliasNames()[46];

	/** 
	* Alias value: Httrabajador_numero
	* Hibernate value: Httrabajador.numero
	*/
	String  HTTRABAJADOR_NUMERO = HTTRABAJADOR_ENTRY.getAliasNames()[47];

	/** 
	* Alias value: Httrabajador_numeross
	* Hibernate value: Httrabajador.numeross
	*/
	String  HTTRABAJADOR_NUMEROSS = HTTRABAJADOR_ENTRY.getAliasNames()[48];

	/** 
	* Alias value: Httrabajador_otrdir
	* Hibernate value: Httrabajador.otrdir
	*/
	String  HTTRABAJADOR_OTRDIR = HTTRABAJADOR_ENTRY.getAliasNames()[49];

	/** 
	* Alias value: Httrabajador_padre
	* Hibernate value: Httrabajador.padre
	*/
	String  HTTRABAJADOR_PADRE = HTTRABAJADOR_ENTRY.getAliasNames()[50];

	/** 
	* Alias value: Httrabajador_pais_cdg
	* Hibernate value: Httrabajador.pais.cdg
	*/
	String  HTTRABAJADOR_PAIS_CDG = HTTRABAJADOR_ENTRY.getAliasNames()[51];

	/** 
	* Alias value: Httrabajador_pluriempleo
	* Hibernate value: Httrabajador.pluriempleo
	*/
	String  HTTRABAJADOR_PLURIEMPLEO = HTTRABAJADOR_ENTRY.getAliasNames()[52];

	/** 
	* Alias value: Httrabajador_profesion
	* Hibernate value: Httrabajador.profesion
	*/
	String  HTTRABAJADOR_PROFESION = HTTRABAJADOR_ENTRY.getAliasNames()[53];

	/** 
	* Alias value: Httrabajador_provincia1_cdg
	* Hibernate value: Httrabajador.provincia1.cdg
	*/
	String  HTTRABAJADOR_PROVINCIA1_CDG = HTTRABAJADOR_ENTRY.getAliasNames()[54];

	/** 
	* Alias value: Httrabajador_provincia_cdg
	* Hibernate value: Httrabajador.provincia.cdg
	*/
	String  HTTRABAJADOR_PROVINCIA_CDG = HTTRABAJADOR_ENTRY.getAliasNames()[55];

	/** 
	* Alias value: Httrabajador_retribucion
	* Hibernate value: Httrabajador.retribucion
	*/
	String  HTTRABAJADOR_RETRIBUCION = HTTRABAJADOR_ENTRY.getAliasNames()[56];

	/** 
	* Alias value: Httrabajador_sucursal1_id_cdg
	* Hibernate value: Httrabajador.sucursal1.id.cdg
	*/
	String  HTTRABAJADOR_SUCURSAL1_ID_CDG = HTTRABAJADOR_ENTRY.getAliasNames()[57];

	/** 
	* Alias value: Httrabajador_sucursal1_id_codent
	* Hibernate value: Httrabajador.sucursal1.id.codent
	*/
	String  HTTRABAJADOR_SUCURSAL1_ID_CODENT = HTTRABAJADOR_ENTRY.getAliasNames()[58];

	/** 
	* Alias value: Httrabajador_tiempoparcial
	* Hibernate value: Httrabajador.tiempoparcial
	*/
	String  HTTRABAJADOR_TIEMPOPARCIAL = HTTRABAJADOR_ENTRY.getAliasNames()[59];

	/** 
	* Alias value: Httrabajador_tipcotc2_cdg
	* Hibernate value: Httrabajador.tipcotc2.cdg
	*/
	String  HTTRABAJADOR_TIPCOTC2_CDG = HTTRABAJADOR_ENTRY.getAliasNames()[60];

	/** 
	* Alias value: Httrabajador_tipocont_cdg
	* Hibernate value: Httrabajador.tipocont.cdg
	*/
	String  HTTRABAJADOR_TIPOCONT_CDG = HTTRABAJADOR_ENTRY.getAliasNames()[61];

	/** 
	* Alias value: Httrabajador_tipovia_cdg
	* Hibernate value: Httrabajador.tipovia.cdg
	*/
	String  HTTRABAJADOR_TIPOVIA_CDG = HTTRABAJADOR_ENTRY.getAliasNames()[62];



	/** 
	* DAOConstantsEntry for Httincidencia entity.
	*/ 
	DAOConstantsEntry HTTINCIDENCIA_ENTRY = DAOConstants.getDAOConstant(Httincidencia.class);

	/** 
	* Alias value: Httincidencia_cantidad
	* Hibernate value: Httincidencia.cantidad
	*/
	String  HTTINCIDENCIA_CANTIDAD = HTTINCIDENCIA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Httincidencia_fecfin
	* Hibernate value: Httincidencia.fecfin
	*/
	String  HTTINCIDENCIA_FECFIN = HTTINCIDENCIA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Httincidencia_fecinicio
	* Hibernate value: Httincidencia.fecinicio
	*/
	String  HTTINCIDENCIA_FECINICIO = HTTINCIDENCIA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Httincidencia_httrabajador_cdg
	* Hibernate value: Httincidencia.httrabajador.cdg
	*/
	String  HTTINCIDENCIA_HTTRABAJADOR_CDG = HTTINCIDENCIA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Httincidencia_id_cdg
	* Hibernate value: Httincidencia.id.cdg
	*/
	String  HTTINCIDENCIA_ID_CDG = HTTINCIDENCIA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Httincidencia_id_orden
	* Hibernate value: Httincidencia.id.orden
	*/
	String  HTTINCIDENCIA_ID_ORDEN = HTTINCIDENCIA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Httincidencia_id_tipo
	* Hibernate value: Httincidencia.id.tipo
	*/
	String  HTTINCIDENCIA_ID_TIPO = HTTINCIDENCIA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Httincidencia_importe
	* Hibernate value: Httincidencia.importe
	*/
	String  HTTINCIDENCIA_IMPORTE = HTTINCIDENCIA_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for Finidto entity.
	*/ 
	DAOConstantsEntry FINIDTO_ENTRY = DAOConstants.getDAOConstant(Finidto.class);

	/** 
	* Alias value: Finidto_finiquito_cdg
	* Hibernate value: Finidto.finiquito.cdg
	*/
	String  FINIDTO_FINIQUITO_CDG = FINIDTO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Finidto_id_cdg
	* Hibernate value: Finidto.id.cdg
	*/
	String  FINIDTO_ID_CDG = FINIDTO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Finidto_id_orden
	* Hibernate value: Finidto.id.orden
	*/
	String  FINIDTO_ID_ORDEN = FINIDTO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Finidto_importe
	* Hibernate value: Finidto.importe
	*/
	String  FINIDTO_IMPORTE = FINIDTO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Finidto_texto
	* Hibernate value: Finidto.texto
	*/
	String  FINIDTO_TEXTO = FINIDTO_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for Finindem entity.
	*/ 
	DAOConstantsEntry FININDEM_ENTRY = DAOConstants.getDAOConstant(Finindem.class);

	/** 
	* Alias value: Finindem_finiquito_cdg
	* Hibernate value: Finindem.finiquito.cdg
	*/
	String  FININDEM_FINIQUITO_CDG = FININDEM_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Finindem_id_cdg
	* Hibernate value: Finindem.id.cdg
	*/
	String  FININDEM_ID_CDG = FININDEM_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Finindem_id_orden
	* Hibernate value: Finindem.id.orden
	*/
	String  FININDEM_ID_ORDEN = FININDEM_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Finindem_importe
	* Hibernate value: Finindem.importe
	*/
	String  FININDEM_IMPORTE = FININDEM_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Finindem_irpf
	* Hibernate value: Finindem.irpf
	*/
	String  FININDEM_IRPF = FININDEM_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Finindem_texto
	* Hibernate value: Finindem.texto
	*/
	String  FININDEM_TEXTO = FININDEM_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Finipext entity.
	*/ 
	DAOConstantsEntry FINIPEXT_ENTRY = DAOConstants.getDAOConstant(Finipext.class);

	/** 
	* Alias value: Finipext_complemento_cdg
	* Hibernate value: Finipext.complemento.cdg
	*/
	String  FINIPEXT_COMPLEMENTO_CDG = FINIPEXT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Finipext_descom
	* Hibernate value: Finipext.descom
	*/
	String  FINIPEXT_DESCOM = FINIPEXT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Finipext_fecfin
	* Hibernate value: Finipext.fecfin
	*/
	String  FINIPEXT_FECFIN = FINIPEXT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Finipext_finiquito_cdg
	* Hibernate value: Finipext.finiquito.cdg
	*/
	String  FINIPEXT_FINIQUITO_CDG = FINIPEXT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Finipext_id_cdg
	* Hibernate value: Finipext.id.cdg
	*/
	String  FINIPEXT_ID_CDG = FINIPEXT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Finipext_id_codcom
	* Hibernate value: Finipext.id.codcom
	*/
	String  FINIPEXT_ID_CODCOM = FINIPEXT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Finipext_id_fecini
	* Hibernate value: Finipext.id.fecini
	*/
	String  FINIPEXT_ID_FECINI = FINIPEXT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Finipext_importe
	* Hibernate value: Finipext.importe
	*/
	String  FINIPEXT_IMPORTE = FINIPEXT_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for Finiquito entity.
	*/ 
	DAOConstantsEntry FINIQUITO_ENTRY = DAOConstants.getDAOConstant(Finiquito.class);

	/** 
	* Alias value: Finiquito_base
	* Hibernate value: Finiquito.base
	*/
	String  FINIQUITO_BASE = FINIQUITO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Finiquito_baseacc
	* Hibernate value: Finiquito.baseacc
	*/
	String  FINIQUITO_BASEACC = FINIQUITO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Finiquito_basecg
	* Hibernate value: Finiquito.basecg
	*/
	String  FINIQUITO_BASECG = FINIQUITO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Finiquito_causa
	* Hibernate value: Finiquito.causa
	*/
	String  FINIQUITO_CAUSA = FINIQUITO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Finiquito_cdg
	* Hibernate value: Finiquito.cdg
	*/
	String  FINIQUITO_CDG = FINIQUITO_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Finiquito_codbas
	* Hibernate value: Finiquito.codbas
	*/
	String  FINIQUITO_CODBAS = FINIQUITO_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Finiquito_costessemp
	* Hibernate value: Finiquito.costessemp
	*/
	String  FINIQUITO_COSTESSEMP = FINIQUITO_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Finiquito_diasvac
	* Hibernate value: Finiquito.diasvac
	*/
	String  FINIQUITO_DIASVAC = FINIQUITO_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Finiquito_divisa_cdg
	* Hibernate value: Finiquito.divisa.cdg
	*/
	String  FINIQUITO_DIVISA_CDG = FINIQUITO_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Finiquito_emprper_cdg
	* Hibernate value: Finiquito.emprper.cdg
	*/
	String  FINIQUITO_EMPRPER_CDG = FINIQUITO_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Finiquito_fecbaj
	* Hibernate value: Finiquito.fecbaj
	*/
	String  FINIQUITO_FECBAJ = FINIQUITO_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Finiquito_feccobreal
	* Hibernate value: Finiquito.feccobreal
	*/
	String  FINIQUITO_FECCOBREAL = FINIQUITO_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Finiquito_fecmod
	* Hibernate value: Finiquito.fecmod
	*/
	String  FINIQUITO_FECMOD = FINIQUITO_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Finiquito_fecnew
	* Hibernate value: Finiquito.fecnew
	*/
	String  FINIQUITO_FECNEW = FINIQUITO_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Finiquito_hormod
	* Hibernate value: Finiquito.hormod
	*/
	String  FINIQUITO_HORMOD = FINIQUITO_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Finiquito_hornew
	* Hibernate value: Finiquito.hornew
	*/
	String  FINIQUITO_HORNEW = FINIQUITO_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Finiquito_importeIrpf
	* Hibernate value: Finiquito.importeIrpf
	*/
	String  FINIQUITO_IMPORTE_IRPF = FINIQUITO_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Finiquito_importeacc
	* Hibernate value: Finiquito.importeacc
	*/
	String  FINIQUITO_IMPORTEACC = FINIQUITO_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Finiquito_importecg
	* Hibernate value: Finiquito.importecg
	*/
	String  FINIQUITO_IMPORTECG = FINIQUITO_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Finiquito_importesin
	* Hibernate value: Finiquito.importesin
	*/
	String  FINIQUITO_IMPORTESIN = FINIQUITO_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Finiquito_irpf
	* Hibernate value: Finiquito.irpf
	*/
	String  FINIQUITO_IRPF = FINIQUITO_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Finiquito_liquido
	* Hibernate value: Finiquito.liquido
	*/
	String  FINIQUITO_LIQUIDO = FINIQUITO_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Finiquito_prcacc
	* Hibernate value: Finiquito.prcacc
	*/
	String  FINIQUITO_PRCACC = FINIQUITO_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Finiquito_prccg
	* Hibernate value: Finiquito.prccg
	*/
	String  FINIQUITO_PRCCG = FINIQUITO_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Finiquito_simula
	* Hibernate value: Finiquito.simula
	*/
	String  FINIQUITO_SIMULA = FINIQUITO_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Finiquito_totalConceptos
	* Hibernate value: Finiquito.totalConceptos
	*/
	String  FINIQUITO_TOTAL_CONCEPTOS = FINIQUITO_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Finiquito_vacfecini
	* Hibernate value: Finiquito.vacfecini
	*/
	String  FINIQUITO_VACFECINI = FINIQUITO_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: Finiquito_vacimporte
	* Hibernate value: Finiquito.vacimporte
	*/
	String  FINIQUITO_VACIMPORTE = FINIQUITO_ENTRY.getAliasNames()[27];



	/** 
	* DAOConstantsEntry for Nomina entity.
	*/ 
	DAOConstantsEntry NOMINA_ENTRY = DAOConstants.getDAOConstant(Nomina.class);

	/** 
	* Alias value: Nomina_anio
	* Hibernate value: Nomina.anio
	*/
	String  NOMINA_ANIO = NOMINA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Nomina_baseAcc
	* Hibernate value: Nomina.baseAcc
	*/
	String  NOMINA_BASE_ACC = NOMINA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Nomina_baseAccIt
	* Hibernate value: Nomina.baseAccIt
	*/
	String  NOMINA_BASE_ACC_IT = NOMINA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Nomina_baseAccMat
	* Hibernate value: Nomina.baseAccMat
	*/
	String  NOMINA_BASE_ACC_MAT = NOMINA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Nomina_baseAccMatNo
	* Hibernate value: Nomina.baseAccMatNo
	*/
	String  NOMINA_BASE_ACC_MAT_NO = NOMINA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Nomina_baseAccPts
	* Hibernate value: Nomina.baseAccPts
	*/
	String  NOMINA_BASE_ACC_PTS = NOMINA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Nomina_baseAccSinHPts
	* Hibernate value: Nomina.baseAccSinHPts
	*/
	String  NOMINA_BASE_ACC_SIN_HPTS = NOMINA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Nomina_baseAccSinHex
	* Hibernate value: Nomina.baseAccSinHex
	*/
	String  NOMINA_BASE_ACC_SIN_HEX = NOMINA_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Nomina_baseAcctra
	* Hibernate value: Nomina.baseAcctra
	*/
	String  NOMINA_BASE_ACCTRA = NOMINA_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Nomina_baseCg
	* Hibernate value: Nomina.baseCg
	*/
	String  NOMINA_BASE_CG = NOMINA_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Nomina_baseCgPts
	* Hibernate value: Nomina.baseCgPts
	*/
	String  NOMINA_BASE_CG_PTS = NOMINA_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Nomina_baseConIt
	* Hibernate value: Nomina.baseConIt
	*/
	String  NOMINA_BASE_CON_IT = NOMINA_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Nomina_baseConMat
	* Hibernate value: Nomina.baseConMat
	*/
	String  NOMINA_BASE_CON_MAT = NOMINA_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Nomina_baseConMatNo
	* Hibernate value: Nomina.baseConMatNo
	*/
	String  NOMINA_BASE_CON_MAT_NO = NOMINA_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Nomina_baseConcom
	* Hibernate value: Nomina.baseConcom
	*/
	String  NOMINA_BASE_CONCOM = NOMINA_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Nomina_baseDesempleo
	* Hibernate value: Nomina.baseDesempleo
	*/
	String  NOMINA_BASE_DESEMPLEO = NOMINA_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Nomina_baseEspecie
	* Hibernate value: Nomina.baseEspecie
	*/
	String  NOMINA_BASE_ESPECIE = NOMINA_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Nomina_baseExceso
	* Hibernate value: Nomina.baseExceso
	*/
	String  NOMINA_BASE_EXCESO = NOMINA_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Nomina_baseFogasa
	* Hibernate value: Nomina.baseFogasa
	*/
	String  NOMINA_BASE_FOGASA = NOMINA_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Nomina_baseFp
	* Hibernate value: Nomina.baseFp
	*/
	String  NOMINA_BASE_FP = NOMINA_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Nomina_baseHextras
	* Hibernate value: Nomina.baseHextras
	*/
	String  NOMINA_BASE_HEXTRAS = NOMINA_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Nomina_baseHextrasNo
	* Hibernate value: Nomina.baseHextrasNo
	*/
	String  NOMINA_BASE_HEXTRAS_NO = NOMINA_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Nomina_baseHorascom
	* Hibernate value: Nomina.baseHorascom
	*/
	String  NOMINA_BASE_HORASCOM = NOMINA_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Nomina_baseIrpf
	* Hibernate value: Nomina.baseIrpf
	*/
	String  NOMINA_BASE_IRPF = NOMINA_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Nomina_baseIrpfAnt
	* Hibernate value: Nomina.baseIrpfAnt
	*/
	String  NOMINA_BASE_IRPF_ANT = NOMINA_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Nomina_baseIrpfEspecie
	* Hibernate value: Nomina.baseIrpfEspecie
	*/
	String  NOMINA_BASE_IRPF_ESPECIE = NOMINA_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Nomina_baseIrpfNocotiza
	* Hibernate value: Nomina.baseIrpfNocotiza
	*/
	String  NOMINA_BASE_IRPF_NOCOTIZA = NOMINA_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: Nomina_baseIt
	* Hibernate value: Nomina.baseIt
	*/
	String  NOMINA_BASE_IT = NOMINA_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: Nomina_baseNocotiza
	* Hibernate value: Nomina.baseNocotiza
	*/
	String  NOMINA_BASE_NOCOTIZA = NOMINA_ENTRY.getAliasNames()[28];

	/** 
	* Alias value: Nomina_basePerdes
	* Hibernate value: Nomina.basePerdes
	*/
	String  NOMINA_BASE_PERDES = NOMINA_ENTRY.getAliasNames()[29];

	/** 
	* Alias value: Nomina_baseProext
	* Hibernate value: Nomina.baseProext
	*/
	String  NOMINA_BASE_PROEXT = NOMINA_ENTRY.getAliasNames()[30];

	/** 
	* Alias value: Nomina_baseant
	* Hibernate value: Nomina.baseant
	*/
	String  NOMINA_BASEANT = NOMINA_ENTRY.getAliasNames()[31];

	/** 
	* Alias value: Nomina_cdg
	* Hibernate value: Nomina.cdg
	*/
	String  NOMINA_CDG = NOMINA_ENTRY.getAliasNames()[32];

	/** 
	* Alias value: Nomina_codbas
	* Hibernate value: Nomina.codbas
	*/
	String  NOMINA_CODBAS = NOMINA_ENTRY.getAliasNames()[33];

	/** 
	* Alias value: Nomina_codcon
	* Hibernate value: Nomina.codcon
	*/
	String  NOMINA_CODCON = NOMINA_ENTRY.getAliasNames()[34];

	/** 
	* Alias value: Nomina_codpct
	* Hibernate value: Nomina.codpct
	*/
	String  NOMINA_CODPCT = NOMINA_ENTRY.getAliasNames()[35];

	/** 
	* Alias value: Nomina_cuotaEmpresa
	* Hibernate value: Nomina.cuotaEmpresa
	*/
	String  NOMINA_CUOTA_EMPRESA = NOMINA_ENTRY.getAliasNames()[36];

	/** 
	* Alias value: Nomina_descat
	* Hibernate value: Nomina.descat
	*/
	String  NOMINA_DESCAT = NOMINA_ENTRY.getAliasNames()[37];

	/** 
	* Alias value: Nomina_diasefec
	* Hibernate value: Nomina.diasefec
	*/
	String  NOMINA_DIASEFEC = NOMINA_ENTRY.getAliasNames()[38];

	/** 
	* Alias value: Nomina_diasnomina
	* Hibernate value: Nomina.diasnomina
	*/
	String  NOMINA_DIASNOMINA = NOMINA_ENTRY.getAliasNames()[39];

	/** 
	* Alias value: Nomina_diastrab
	* Hibernate value: Nomina.diastrab
	*/
	String  NOMINA_DIASTRAB = NOMINA_ENTRY.getAliasNames()[40];

	/** 
	* Alias value: Nomina_direccion
	* Hibernate value: Nomina.direccion
	*/
	String  NOMINA_DIRECCION = NOMINA_ENTRY.getAliasNames()[41];

	/** 
	* Alias value: Nomina_divisa_cdg
	* Hibernate value: Nomina.divisa.cdg
	*/
	String  NOMINA_DIVISA_CDG = NOMINA_ENTRY.getAliasNames()[42];

	/** 
	* Alias value: Nomina_fecant
	* Hibernate value: Nomina.fecant
	*/
	String  NOMINA_FECANT = NOMINA_ENTRY.getAliasNames()[43];

	/** 
	* Alias value: Nomina_feccob
	* Hibernate value: Nomina.feccob
	*/
	String  NOMINA_FECCOB = NOMINA_ENTRY.getAliasNames()[44];

	/** 
	* Alias value: Nomina_feccobreal
	* Hibernate value: Nomina.feccobreal
	*/
	String  NOMINA_FECCOBREAL = NOMINA_ENTRY.getAliasNames()[45];

	/** 
	* Alias value: Nomina_fecemi
	* Hibernate value: Nomina.fecemi
	*/
	String  NOMINA_FECEMI = NOMINA_ENTRY.getAliasNames()[46];

	/** 
	* Alias value: Nomina_fecfin
	* Hibernate value: Nomina.fecfin
	*/
	String  NOMINA_FECFIN = NOMINA_ENTRY.getAliasNames()[47];

	/** 
	* Alias value: Nomina_fecini
	* Hibernate value: Nomina.fecini
	*/
	String  NOMINA_FECINI = NOMINA_ENTRY.getAliasNames()[48];

	/** 
	* Alias value: Nomina_fecmod
	* Hibernate value: Nomina.fecmod
	*/
	String  NOMINA_FECMOD = NOMINA_ENTRY.getAliasNames()[49];

	/** 
	* Alias value: Nomina_fecnew
	* Hibernate value: Nomina.fecnew
	*/
	String  NOMINA_FECNEW = NOMINA_ENTRY.getAliasNames()[50];

	/** 
	* Alias value: Nomina_hormod
	* Hibernate value: Nomina.hormod
	*/
	String  NOMINA_HORMOD = NOMINA_ENTRY.getAliasNames()[51];

	/** 
	* Alias value: Nomina_hornew
	* Hibernate value: Nomina.hornew
	*/
	String  NOMINA_HORNEW = NOMINA_ENTRY.getAliasNames()[52];

	/** 
	* Alias value: Nomina_importeAcc
	* Hibernate value: Nomina.importeAcc
	*/
	String  NOMINA_IMPORTE_ACC = NOMINA_ENTRY.getAliasNames()[53];

	/** 
	* Alias value: Nomina_importeCg
	* Hibernate value: Nomina.importeCg
	*/
	String  NOMINA_IMPORTE_CG = NOMINA_ENTRY.getAliasNames()[54];

	/** 
	* Alias value: Nomina_importeCuotas
	* Hibernate value: Nomina.importeCuotas
	*/
	String  NOMINA_IMPORTE_CUOTAS = NOMINA_ENTRY.getAliasNames()[55];

	/** 
	* Alias value: Nomina_importeCuotasAnt
	* Hibernate value: Nomina.importeCuotasAnt
	*/
	String  NOMINA_IMPORTE_CUOTAS_ANT = NOMINA_ENTRY.getAliasNames()[56];

	/** 
	* Alias value: Nomina_importeHex
	* Hibernate value: Nomina.importeHex
	*/
	String  NOMINA_IMPORTE_HEX = NOMINA_ENTRY.getAliasNames()[57];

	/** 
	* Alias value: Nomina_importeHexno
	* Hibernate value: Nomina.importeHexno
	*/
	String  NOMINA_IMPORTE_HEXNO = NOMINA_ENTRY.getAliasNames()[58];

	/** 
	* Alias value: Nomina_importeIrpf
	* Hibernate value: Nomina.importeIrpf
	*/
	String  NOMINA_IMPORTE_IRPF = NOMINA_ENTRY.getAliasNames()[59];

	/** 
	* Alias value: Nomina_importeIrpfAnt
	* Hibernate value: Nomina.importeIrpfAnt
	*/
	String  NOMINA_IMPORTE_IRPF_ANT = NOMINA_ENTRY.getAliasNames()[60];

	/** 
	* Alias value: Nomina_localidad
	* Hibernate value: Nomina.localidad
	*/
	String  NOMINA_LOCALIDAD = NOMINA_ENTRY.getAliasNames()[61];

	/** 
	* Alias value: Nomina_maxacc
	* Hibernate value: Nomina.maxacc
	*/
	String  NOMINA_MAXACC = NOMINA_ENTRY.getAliasNames()[62];

	/** 
	* Alias value: Nomina_maxcg
	* Hibernate value: Nomina.maxcg
	*/
	String  NOMINA_MAXCG = NOMINA_ENTRY.getAliasNames()[63];

	/** 
	* Alias value: Nomina_mes
	* Hibernate value: Nomina.mes
	*/
	String  NOMINA_MES = NOMINA_ENTRY.getAliasNames()[64];

	/** 
	* Alias value: Nomina_minacc
	* Hibernate value: Nomina.minacc
	*/
	String  NOMINA_MINACC = NOMINA_ENTRY.getAliasNames()[65];

	/** 
	* Alias value: Nomina_mincg
	* Hibernate value: Nomina.mincg
	*/
	String  NOMINA_MINCG = NOMINA_ENTRY.getAliasNames()[66];

	/** 
	* Alias value: Nomina_nomemp
	* Hibernate value: Nomina.nomemp
	*/
	String  NOMINA_NOMEMP = NOMINA_ENTRY.getAliasNames()[67];

	/** 
	* Alias value: Nomina_nomper
	* Hibernate value: Nomina.nomper
	*/
	String  NOMINA_NOMPER = NOMINA_ENTRY.getAliasNames()[68];

	/** 
	* Alias value: Nomina_nummat
	* Hibernate value: Nomina.nummat
	*/
	String  NOMINA_NUMMAT = NOMINA_ENTRY.getAliasNames()[69];

	/** 
	* Alias value: Nomina_orden
	* Hibernate value: Nomina.orden
	*/
	String  NOMINA_ORDEN = NOMINA_ENTRY.getAliasNames()[70];

	/** 
	* Alias value: Nomina_prcAcc
	* Hibernate value: Nomina.prcAcc
	*/
	String  NOMINA_PRC_ACC = NOMINA_ENTRY.getAliasNames()[71];

	/** 
	* Alias value: Nomina_prcCg
	* Hibernate value: Nomina.prcCg
	*/
	String  NOMINA_PRC_CG = NOMINA_ENTRY.getAliasNames()[72];

	/** 
	* Alias value: Nomina_prcHex
	* Hibernate value: Nomina.prcHex
	*/
	String  NOMINA_PRC_HEX = NOMINA_ENTRY.getAliasNames()[73];

	/** 
	* Alias value: Nomina_prcHexno
	* Hibernate value: Nomina.prcHexno
	*/
	String  NOMINA_PRC_HEXNO = NOMINA_ENTRY.getAliasNames()[74];

	/** 
	* Alias value: Nomina_prcIrpf
	* Hibernate value: Nomina.prcIrpf
	*/
	String  NOMINA_PRC_IRPF = NOMINA_ENTRY.getAliasNames()[75];

	/** 
	* Alias value: Nomina_procot
	* Hibernate value: Nomina.procot
	*/
	String  NOMINA_PROCOT = NOMINA_ENTRY.getAliasNames()[76];

	/** 
	* Alias value: Nomina_profesion
	* Hibernate value: Nomina.profesion
	*/
	String  NOMINA_PROFESION = NOMINA_ENTRY.getAliasNames()[77];

	/** 
	* Alias value: Nomina_proret
	* Hibernate value: Nomina.proret
	*/
	String  NOMINA_PRORET = NOMINA_ENTRY.getAliasNames()[78];

	/** 
	* Alias value: Nomina_remuneracion
	* Hibernate value: Nomina.remuneracion
	*/
	String  NOMINA_REMUNERACION = NOMINA_ENTRY.getAliasNames()[79];

	/** 
	* Alias value: Nomina_tipo
	* Hibernate value: Nomina.tipo
	*/
	String  NOMINA_TIPO = NOMINA_ENTRY.getAliasNames()[80];

	/** 
	* Alias value: Nomina_total1
	* Hibernate value: Nomina.total1
	*/
	String  NOMINA_TOTAL1 = NOMINA_ENTRY.getAliasNames()[81];

	/** 
	* Alias value: Nomina_totalDeducir
	* Hibernate value: Nomina.totalDeducir
	*/
	String  NOMINA_TOTAL_DEDUCIR = NOMINA_ENTRY.getAliasNames()[82];

	/** 
	* Alias value: Nomina_totalDevengos
	* Hibernate value: Nomina.totalDevengos
	*/
	String  NOMINA_TOTAL_DEVENGOS = NOMINA_ENTRY.getAliasNames()[83];

	/** 
	* Alias value: Nomina_totalLiquido
	* Hibernate value: Nomina.totalLiquido
	*/
	String  NOMINA_TOTAL_LIQUIDO = NOMINA_ENTRY.getAliasNames()[84];

	/** 
	* Alias value: Nomina_trabajador_cdg
	* Hibernate value: Nomina.trabajador.cdg
	*/
	String  NOMINA_TRABAJADOR_CDG = NOMINA_ENTRY.getAliasNames()[85];



	/** 
	* DAOConstantsEntry for Nominadev entity.
	*/ 
	DAOConstantsEntry NOMINADEV_ENTRY = DAOConstants.getDAOConstant(Nominadev.class);

	/** 
	* Alias value: Nominadev_codcom
	* Hibernate value: Nominadev.codcom
	*/
	String  NOMINADEV_CODCOM = NOMINADEV_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Nominadev_descom
	* Hibernate value: Nominadev.descom
	*/
	String  NOMINADEV_DESCOM = NOMINADEV_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Nominadev_dinesp
	* Hibernate value: Nominadev.dinesp
	*/
	String  NOMINADEV_DINESP = NOMINADEV_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Nominadev_fecmod
	* Hibernate value: Nominadev.fecmod
	*/
	String  NOMINADEV_FECMOD = NOMINADEV_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Nominadev_fecnew
	* Hibernate value: Nominadev.fecnew
	*/
	String  NOMINADEV_FECNEW = NOMINADEV_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Nominadev_fijovar
	* Hibernate value: Nominadev.fijovar
	*/
	String  NOMINADEV_FIJOVAR = NOMINADEV_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Nominadev_hormod
	* Hibernate value: Nominadev.hormod
	*/
	String  NOMINADEV_HORMOD = NOMINADEV_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Nominadev_hornew
	* Hibernate value: Nominadev.hornew
	*/
	String  NOMINADEV_HORNEW = NOMINADEV_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Nominadev_id_cdg
	* Hibernate value: Nominadev.id.cdg
	*/
	String  NOMINADEV_ID_CDG = NOMINADEV_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Nominadev_id_orden
	* Hibernate value: Nominadev.id.orden
	*/
	String  NOMINADEV_ID_ORDEN = NOMINADEV_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Nominadev_importe
	* Hibernate value: Nominadev.importe
	*/
	String  NOMINADEV_IMPORTE = NOMINADEV_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Nominadev_impuni
	* Hibernate value: Nominadev.impuni
	*/
	String  NOMINADEV_IMPUNI = NOMINADEV_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Nominadev_indcom
	* Hibernate value: Nominadev.indcom
	*/
	String  NOMINADEV_INDCOM = NOMINADEV_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Nominadev_nomina_cdg
	* Hibernate value: Nominadev.nomina.cdg
	*/
	String  NOMINADEV_NOMINA_CDG = NOMINADEV_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Nominadev_tipcom
	* Hibernate value: Nominadev.tipcom
	*/
	String  NOMINADEV_TIPCOM = NOMINADEV_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Nominadev_unidades
	* Hibernate value: Nominadev.unidades
	*/
	String  NOMINADEV_UNIDADES = NOMINADEV_ENTRY.getAliasNames()[15];



	/** 
	* DAOConstantsEntry for Nomdto entity.
	*/ 
	DAOConstantsEntry NOMDTO_ENTRY = DAOConstants.getDAOConstant(Nomdto.class);

	/** 
	* Alias value: Nomdto_concepto
	* Hibernate value: Nomdto.concepto
	*/
	String  NOMDTO_CONCEPTO = NOMDTO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Nomdto_fecmod
	* Hibernate value: Nomdto.fecmod
	*/
	String  NOMDTO_FECMOD = NOMDTO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Nomdto_fecnew
	* Hibernate value: Nomdto.fecnew
	*/
	String  NOMDTO_FECNEW = NOMDTO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Nomdto_hormod
	* Hibernate value: Nomdto.hormod
	*/
	String  NOMDTO_HORMOD = NOMDTO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Nomdto_hornew
	* Hibernate value: Nomdto.hornew
	*/
	String  NOMDTO_HORNEW = NOMDTO_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Nomdto_id_cdg
	* Hibernate value: Nomdto.id.cdg
	*/
	String  NOMDTO_ID_CDG = NOMDTO_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Nomdto_id_linea
	* Hibernate value: Nomdto.id.linea
	*/
	String  NOMDTO_ID_LINEA = NOMDTO_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Nomdto_importe
	* Hibernate value: Nomdto.importe
	*/
	String  NOMDTO_IMPORTE = NOMDTO_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Nomdto_nomina_cdg
	* Hibernate value: Nomdto.nomina.cdg
	*/
	String  NOMDTO_NOMINA_CDG = NOMDTO_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for Calculo entity.
	*/ 
	DAOConstantsEntry CALCULO_ENTRY = DAOConstants.getDAOConstant(Calculo.class);

	/** 
	* Alias value: Calculo_aplicado
	* Hibernate value: Calculo.aplicado
	*/
	String  CALCULO_APLICADO = CALCULO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Calculo_baseCalculo
	* Hibernate value: Calculo.baseCalculo
	*/
	String  CALCULO_BASE_CALCULO = CALCULO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Calculo_cuotaAnualid
	* Hibernate value: Calculo.cuotaAnualid
	*/
	String  CALCULO_CUOTA_ANUALID = CALCULO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Calculo_cuotaCalculo
	* Hibernate value: Calculo.cuotaCalculo
	*/
	String  CALCULO_CUOTA_CALCULO = CALCULO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Calculo_diascont
	* Hibernate value: Calculo.diascont
	*/
	String  CALCULO_DIASCONT = CALCULO_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Calculo_difret
	* Hibernate value: Calculo.difret
	*/
	String  CALCULO_DIFRET = CALCULO_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Calculo_emprper_cdg
	* Hibernate value: Calculo.emprper.cdg
	*/
	String  CALCULO_EMPRPER_CDG = CALCULO_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Calculo_fecmod
	* Hibernate value: Calculo.fecmod
	*/
	String  CALCULO_FECMOD = CALCULO_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Calculo_fecnew
	* Hibernate value: Calculo.fecnew
	*/
	String  CALCULO_FECNEW = CALCULO_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Calculo_hijos
	* Hibernate value: Calculo.hijos
	*/
	String  CALCULO_HIJOS = CALCULO_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Calculo_hormod
	* Hibernate value: Calculo.hormod
	*/
	String  CALCULO_HORMOD = CALCULO_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Calculo_hornew
	* Hibernate value: Calculo.hornew
	*/
	String  CALCULO_HORNEW = CALCULO_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Calculo_id_anio
	* Hibernate value: Calculo.id.anio
	*/
	String  CALCULO_ID_ANIO = CALCULO_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Calculo_id_cdg
	* Hibernate value: Calculo.id.cdg
	*/
	String  CALCULO_ID_CDG = CALCULO_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Calculo_id_dia
	* Hibernate value: Calculo.id.dia
	*/
	String  CALCULO_ID_DIA = CALCULO_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Calculo_id_mes
	* Hibernate value: Calculo.id.mes
	*/
	String  CALCULO_ID_MES = CALCULO_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Calculo_impAcuSs
	* Hibernate value: Calculo.impAcuSs
	*/
	String  CALCULO_IMP_ACU_SS = CALCULO_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Calculo_impAnualid
	* Hibernate value: Calculo.impAnualid
	*/
	String  CALCULO_IMP_ANUALID = CALCULO_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Calculo_impAscen
	* Hibernate value: Calculo.impAscen
	*/
	String  CALCULO_IMP_ASCEN = CALCULO_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Calculo_impAsistencia
	* Hibernate value: Calculo.impAsistencia
	*/
	String  CALCULO_IMP_ASISTENCIA = CALCULO_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Calculo_impCss
	* Hibernate value: Calculo.impCss
	*/
	String  CALCULO_IMP_CSS = CALCULO_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Calculo_impCuidadohijo
	* Hibernate value: Calculo.impCuidadohijo
	*/
	String  CALCULO_IMP_CUIDADOHIJO = CALCULO_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Calculo_impDescen
	* Hibernate value: Calculo.impDescen
	*/
	String  CALCULO_IMP_DESCEN = CALCULO_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Calculo_impDiscapacidad
	* Hibernate value: Calculo.impDiscapacidad
	*/
	String  CALCULO_IMP_DISCAPACIDAD = CALCULO_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Calculo_impDiscapacidadt
	* Hibernate value: Calculo.impDiscapacidadt
	*/
	String  CALCULO_IMP_DISCAPACIDADT = CALCULO_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Calculo_impFamiliar
	* Hibernate value: Calculo.impFamiliar
	*/
	String  CALCULO_IMP_FAMILIAR = CALCULO_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Calculo_impIrreg
	* Hibernate value: Calculo.impIrreg
	*/
	String  CALCULO_IMP_IRREG = CALCULO_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: Calculo_impMovilidad
	* Hibernate value: Calculo.impMovilidad
	*/
	String  CALCULO_IMP_MOVILIDAD = CALCULO_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: Calculo_impPension
	* Hibernate value: Calculo.impPension
	*/
	String  CALCULO_IMP_PENSION = CALCULO_ENTRY.getAliasNames()[28];

	/** 
	* Alias value: Calculo_impPensionista
	* Hibernate value: Calculo.impPensionista
	*/
	String  CALCULO_IMP_PENSIONISTA = CALCULO_ENTRY.getAliasNames()[29];

	/** 
	* Alias value: Calculo_impPersonal
	* Hibernate value: Calculo.impPersonal
	*/
	String  CALCULO_IMP_PERSONAL = CALCULO_ENTRY.getAliasNames()[30];

	/** 
	* Alias value: Calculo_impPreSs
	* Hibernate value: Calculo.impPreSs
	*/
	String  CALCULO_IMP_PRE_SS = CALCULO_ENTRY.getAliasNames()[31];

	/** 
	* Alias value: Calculo_impProlongacion
	* Hibernate value: Calculo.impProlongacion
	*/
	String  CALCULO_IMP_PROLONGACION = CALCULO_ENTRY.getAliasNames()[32];

	/** 
	* Alias value: Calculo_impRentas
	* Hibernate value: Calculo.impRentas
	*/
	String  CALCULO_IMP_RENTAS = CALCULO_ENTRY.getAliasNames()[33];

	/** 
	* Alias value: Calculo_indirpf
	* Hibernate value: Calculo.indirpf
	*/
	String  CALCULO_INDIRPF = CALCULO_ENTRY.getAliasNames()[34];

	/** 
	* Alias value: Calculo_irpf
	* Hibernate value: Calculo.irpf
	*/
	String  CALCULO_IRPF = CALCULO_ENTRY.getAliasNames()[35];

	/** 
	* Alias value: Calculo_irpfAcu
	* Hibernate value: Calculo.irpfAcu
	*/
	String  CALCULO_IRPF_ACU = CALCULO_ENTRY.getAliasNames()[36];

	/** 
	* Alias value: Calculo_irpfAnterior
	* Hibernate value: Calculo.irpfAnterior
	*/
	String  CALCULO_IRPF_ANTERIOR = CALCULO_ENTRY.getAliasNames()[37];

	/** 
	* Alias value: Calculo_irpfCal
	* Hibernate value: Calculo.irpfCal
	*/
	String  CALCULO_IRPF_CAL = CALCULO_ENTRY.getAliasNames()[38];

	/** 
	* Alias value: Calculo_irpfanual
	* Hibernate value: Calculo.irpfanual
	*/
	String  CALCULO_IRPFANUAL = CALCULO_ENTRY.getAliasNames()[39];

	/** 
	* Alias value: Calculo_minforal
	* Hibernate value: Calculo.minforal
	*/
	String  CALCULO_MINFORAL = CALCULO_ENTRY.getAliasNames()[40];

	/** 
	* Alias value: Calculo_regula
	* Hibernate value: Calculo.regula
	*/
	String  CALCULO_REGULA = CALCULO_ENTRY.getAliasNames()[41];

	/** 
	* Alias value: Calculo_retanualb
	* Hibernate value: Calculo.retanualb
	*/
	String  CALCULO_RETANUALB = CALCULO_ENTRY.getAliasNames()[42];

	/** 
	* Alias value: Calculo_retanualn
	* Hibernate value: Calculo.retanualn
	*/
	String  CALCULO_RETANUALN = CALCULO_ENTRY.getAliasNames()[43];

	/** 
	* Alias value: Calculo_retrAcuFij
	* Hibernate value: Calculo.retrAcuFij
	*/
	String  CALCULO_RETR_ACU_FIJ = CALCULO_ENTRY.getAliasNames()[44];

	/** 
	* Alias value: Calculo_retrAcuVar
	* Hibernate value: Calculo.retrAcuVar
	*/
	String  CALCULO_RETR_ACU_VAR = CALCULO_ENTRY.getAliasNames()[45];

	/** 
	* Alias value: Calculo_retrAnt
	* Hibernate value: Calculo.retrAnt
	*/
	String  CALCULO_RETR_ANT = CALCULO_ENTRY.getAliasNames()[46];

	/** 
	* Alias value: Calculo_retrConsid
	* Hibernate value: Calculo.retrConsid
	*/
	String  CALCULO_RETR_CONSID = CALCULO_ENTRY.getAliasNames()[47];

	/** 
	* Alias value: Calculo_retrEstimada
	* Hibernate value: Calculo.retrEstimada
	*/
	String  CALCULO_RETR_ESTIMADA = CALCULO_ENTRY.getAliasNames()[48];

	/** 
	* Alias value: Calculo_retrPreFij
	* Hibernate value: Calculo.retrPreFij
	*/
	String  CALCULO_RETR_PRE_FIJ = CALCULO_ENTRY.getAliasNames()[49];

	/** 
	* Alias value: Calculo_retrPreVar
	* Hibernate value: Calculo.retrPreVar
	*/
	String  CALCULO_RETR_PRE_VAR = CALCULO_ENTRY.getAliasNames()[50];



	/** 
	* DAOConstantsEntry for Lincalcu entity.
	*/ 
	DAOConstantsEntry LINCALCU_ENTRY = DAOConstants.getDAOConstant(Lincalcu.class);

	/** 
	* Alias value: Lincalcu_calculo_id_anio
	* Hibernate value: Lincalcu.calculo.id.anio
	*/
	String  LINCALCU_CALCULO_ID_ANIO = LINCALCU_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Lincalcu_calculo_id_cdg
	* Hibernate value: Lincalcu.calculo.id.cdg
	*/
	String  LINCALCU_CALCULO_ID_CDG = LINCALCU_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Lincalcu_calculo_id_dia
	* Hibernate value: Lincalcu.calculo.id.dia
	*/
	String  LINCALCU_CALCULO_ID_DIA = LINCALCU_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Lincalcu_calculo_id_mes
	* Hibernate value: Lincalcu.calculo.id.mes
	*/
	String  LINCALCU_CALCULO_ID_MES = LINCALCU_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Lincalcu_codcom
	* Hibernate value: Lincalcu.codcom
	*/
	String  LINCALCU_CODCOM = LINCALCU_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Lincalcu_descom
	* Hibernate value: Lincalcu.descom
	*/
	String  LINCALCU_DESCOM = LINCALCU_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Lincalcu_fecini
	* Hibernate value: Lincalcu.fecini
	*/
	String  LINCALCU_FECINI = LINCALCU_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Lincalcu_fijovar
	* Hibernate value: Lincalcu.fijovar
	*/
	String  LINCALCU_FIJOVAR = LINCALCU_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Lincalcu_id_anio
	* Hibernate value: Lincalcu.id.anio
	*/
	String  LINCALCU_ID_ANIO = LINCALCU_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Lincalcu_id_dia
	* Hibernate value: Lincalcu.id.dia
	*/
	String  LINCALCU_ID_DIA = LINCALCU_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Lincalcu_id_linea
	* Hibernate value: Lincalcu.id.linea
	*/
	String  LINCALCU_ID_LINEA = LINCALCU_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Lincalcu_id_mes
	* Hibernate value: Lincalcu.id.mes
	*/
	String  LINCALCU_ID_MES = LINCALCU_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Lincalcu_id_numero
	* Hibernate value: Lincalcu.id.numero
	*/
	String  LINCALCU_ID_NUMERO = LINCALCU_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Lincalcu_importe
	* Hibernate value: Lincalcu.importe
	*/
	String  LINCALCU_IMPORTE = LINCALCU_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Lincalcu_importeUni
	* Hibernate value: Lincalcu.importeUni
	*/
	String  LINCALCU_IMPORTE_UNI = LINCALCU_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Lincalcu_unidades
	* Hibernate value: Lincalcu.unidades
	*/
	String  LINCALCU_UNIDADES = LINCALCU_ENTRY.getAliasNames()[15];



	/** 
	* DAOConstantsEntry for Bonifica entity.
	*/ 
	DAOConstantsEntry BONIFICA_ENTRY = DAOConstants.getDAOConstant(Bonifica.class);

	/** 
	* Alias value: Bonifica_emprper_cdg
	* Hibernate value: Bonifica.emprper.cdg
	*/
	String  BONIFICA_EMPRPER_CDG = BONIFICA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Bonifica_fecfin
	* Hibernate value: Bonifica.fecfin
	*/
	String  BONIFICA_FECFIN = BONIFICA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Bonifica_horas
	* Hibernate value: Bonifica.horas
	*/
	String  BONIFICA_HORAS = BONIFICA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Bonifica_id_cdg
	* Hibernate value: Bonifica.id.cdg
	*/
	String  BONIFICA_ID_CDG = BONIFICA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Bonifica_id_fecini
	* Hibernate value: Bonifica.id.fecini
	*/
	String  BONIFICA_ID_FECINI = BONIFICA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Bonifica_id_numero
	* Hibernate value: Bonifica.id.numero
	*/
	String  BONIFICA_ID_NUMERO = BONIFICA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Bonifica_importe
	* Hibernate value: Bonifica.importe
	*/
	String  BONIFICA_IMPORTE = BONIFICA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Bonifica_prorrateo
	* Hibernate value: Bonifica.prorrateo
	*/
	String  BONIFICA_PRORRATEO = BONIFICA_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Bonifica_tipboni_cdg
	* Hibernate value: Bonifica.tipboni.cdg
	*/
	String  BONIFICA_TIPBONI_CDG = BONIFICA_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Bonifica_tipo
	* Hibernate value: Bonifica.tipo
	*/
	String  BONIFICA_TIPO = BONIFICA_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for Nominait entity.
	*/ 
	DAOConstantsEntry NOMINAIT_ENTRY = DAOConstants.getDAOConstant(Nominait.class);

	/** 
	* Alias value: Nominait_baseacc
	* Hibernate value: Nominait.baseacc
	*/
	String  NOMINAIT_BASEACC = NOMINAIT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Nominait_baseaccTotal
	* Hibernate value: Nominait.baseaccTotal
	*/
	String  NOMINAIT_BASEACC_TOTAL = NOMINAIT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Nominait_basecon
	* Hibernate value: Nominait.basecon
	*/
	String  NOMINAIT_BASECON = NOMINAIT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Nominait_baseconTotal
	* Hibernate value: Nominait.baseconTotal
	*/
	String  NOMINAIT_BASECON_TOTAL = NOMINAIT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Nominait_dias60
	* Hibernate value: Nominait.dias60
	*/
	String  NOMINAIT_DIAS60 = NOMINAIT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Nominait_dias75
	* Hibernate value: Nominait.dias75
	*/
	String  NOMINAIT_DIAS75 = NOMINAIT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Nominait_diasSs
	* Hibernate value: Nominait.diasSs
	*/
	String  NOMINAIT_DIAS_SS = NOMINAIT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Nominait_diasemp
	* Hibernate value: Nominait.diasemp
	*/
	String  NOMINAIT_DIASEMP = NOMINAIT_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Nominait_diasin
	* Hibernate value: Nominait.diasin
	*/
	String  NOMINAIT_DIASIN = NOMINAIT_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Nominait_diasit
	* Hibernate value: Nominait.diasit
	*/
	String  NOMINAIT_DIASIT = NOMINAIT_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Nominait_emprper_cdg
	* Hibernate value: Nominait.emprper.cdg
	*/
	String  NOMINAIT_EMPRPER_CDG = NOMINAIT_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Nominait_fecfin
	* Hibernate value: Nominait.fecfin
	*/
	String  NOMINAIT_FECFIN = NOMINAIT_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Nominait_id_cdg
	* Hibernate value: Nominait.id.cdg
	*/
	String  NOMINAIT_ID_CDG = NOMINAIT_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Nominait_id_fecini
	* Hibernate value: Nominait.id.fecini
	*/
	String  NOMINAIT_ID_FECINI = NOMINAIT_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Nominait_id_feciniit
	* Hibernate value: Nominait.id.feciniit
	*/
	String  NOMINAIT_ID_FECINIIT = NOMINAIT_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Nominait_ptsSs
	* Hibernate value: Nominait.ptsSs
	*/
	String  NOMINAIT_PTS_SS = NOMINAIT_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Nominait_ptsemp
	* Hibernate value: Nominait.ptsemp
	*/
	String  NOMINAIT_PTSEMP = NOMINAIT_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Nominait_riesgo
	* Hibernate value: Nominait.riesgo
	*/
	String  NOMINAIT_RIESGO = NOMINAIT_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Nominait_simula
	* Hibernate value: Nominait.simula
	*/
	String  NOMINAIT_SIMULA = NOMINAIT_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Nominait_tipoit
	* Hibernate value: Nominait.tipoit
	*/
	String  NOMINAIT_TIPOIT = NOMINAIT_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Nominait_totaldias
	* Hibernate value: Nominait.totaldias
	*/
	String  NOMINAIT_TOTALDIAS = NOMINAIT_ENTRY.getAliasNames()[20];



	/** 
	* DAOConstantsEntry for Parteit entity.
	*/ 
	DAOConstantsEntry PARTEIT_ENTRY = DAOConstants.getDAOConstant(Parteit.class);

	/** 
	* Alias value: Parteit_altproc
	* Hibernate value: Parteit.altproc
	*/
	String  PARTEIT_ALTPROC = PARTEIT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Parteit_bajproc
	* Hibernate value: Parteit.bajproc
	*/
	String  PARTEIT_BAJPROC = PARTEIT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Parteit_baseant
	* Hibernate value: Parteit.baseant
	*/
	String  PARTEIT_BASEANT = PARTEIT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Parteit_basediaacc
	* Hibernate value: Parteit.basediaacc
	*/
	String  PARTEIT_BASEDIAACC = PARTEIT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Parteit_basediacg
	* Hibernate value: Parteit.basediacg
	*/
	String  PARTEIT_BASEDIACG = PARTEIT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Parteit_baseregdia
	* Hibernate value: Parteit.baseregdia
	*/
	String  PARTEIT_BASEREGDIA = PARTEIT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Parteit_ciasalt
	* Hibernate value: Parteit.ciasalt
	*/
	String  PARTEIT_CIASALT = PARTEIT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Parteit_ciasbaj
	* Hibernate value: Parteit.ciasbaj
	*/
	String  PARTEIT_CIASBAJ = PARTEIT_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Parteit_diasant
	* Hibernate value: Parteit.diasant
	*/
	String  PARTEIT_DIASANT = PARTEIT_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Parteit_emprper_cdg
	* Hibernate value: Parteit.emprper.cdg
	*/
	String  PARTEIT_EMPRPER_CDG = PARTEIT_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Parteit_fecfin
	* Hibernate value: Parteit.fecfin
	*/
	String  PARTEIT_FECFIN = PARTEIT_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Parteit_feciniori
	* Hibernate value: Parteit.feciniori
	*/
	String  PARTEIT_FECINIORI = PARTEIT_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Parteit_id_cdg
	* Hibernate value: Parteit.id.cdg
	*/
	String  PARTEIT_ID_CDG = PARTEIT_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Parteit_id_fecini
	* Hibernate value: Parteit.id.fecini
	*/
	String  PARTEIT_ID_FECINI = PARTEIT_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Parteit_numcolalt
	* Hibernate value: Parteit.numcolalt
	*/
	String  PARTEIT_NUMCOLALT = PARTEIT_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Parteit_numcolbaj
	* Hibernate value: Parteit.numcolbaj
	*/
	String  PARTEIT_NUMCOLBAJ = PARTEIT_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Parteit_prest60
	* Hibernate value: Parteit.prest60
	*/
	String  PARTEIT_PREST60 = PARTEIT_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Parteit_prest75
	* Hibernate value: Parteit.prest75
	*/
	String  PARTEIT_PREST75 = PARTEIT_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Parteit_procesado
	* Hibernate value: Parteit.procesado
	*/
	String  PARTEIT_PROCESADO = PARTEIT_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Parteit_proret
	* Hibernate value: Parteit.proret
	*/
	String  PARTEIT_PRORET = PARTEIT_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Parteit_recaida
	* Hibernate value: Parteit.recaida
	*/
	String  PARTEIT_RECAIDA = PARTEIT_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Parteit_riesgo
	* Hibernate value: Parteit.riesgo
	*/
	String  PARTEIT_RIESGO = PARTEIT_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Parteit_tipoit
	* Hibernate value: Parteit.tipoit
	*/
	String  PARTEIT_TIPOIT = PARTEIT_ENTRY.getAliasNames()[22];



	/** 
	* DAOConstantsEntry for Parteconf entity.
	*/ 
	DAOConstantsEntry PARTECONF_ENTRY = DAOConstants.getDAOConstant(Parteconf.class);

	/** 
	* Alias value: Parteconf_cias
	* Hibernate value: Parteconf.cias
	*/
	String  PARTECONF_CIAS = PARTECONF_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Parteconf_fecconf
	* Hibernate value: Parteconf.fecconf
	*/
	String  PARTECONF_FECCONF = PARTECONF_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Parteconf_id_cdg
	* Hibernate value: Parteconf.id.cdg
	*/
	String  PARTECONF_ID_CDG = PARTECONF_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Parteconf_id_fecini
	* Hibernate value: Parteconf.id.fecini
	*/
	String  PARTECONF_ID_FECINI = PARTECONF_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Parteconf_id_numero
	* Hibernate value: Parteconf.id.numero
	*/
	String  PARTECONF_ID_NUMERO = PARTECONF_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Parteconf_numcol
	* Hibernate value: Parteconf.numcol
	*/
	String  PARTECONF_NUMCOL = PARTECONF_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Parteconf_parproc
	* Hibernate value: Parteconf.parproc
	*/
	String  PARTECONF_PARPROC = PARTECONF_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Parteconf_parteit_id_cdg
	* Hibernate value: Parteconf.parteit.id.cdg
	*/
	String  PARTECONF_PARTEIT_ID_CDG = PARTECONF_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Parteconf_parteit_id_fecini
	* Hibernate value: Parteconf.parteit.id.fecini
	*/
	String  PARTECONF_PARTEIT_ID_FECINI = PARTECONF_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for Prcdivtrab entity.
	*/ 
	DAOConstantsEntry PRCDIVTRAB_ENTRY = DAOConstants.getDAOConstant(Prcdivtrab.class);

	/** 
	* Alias value: Prcdivtrab_emprper_cdg
	* Hibernate value: Prcdivtrab.emprper.cdg
	*/
	String  PRCDIVTRAB_EMPRPER_CDG = PRCDIVTRAB_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Prcdivtrab_fecfin
	* Hibernate value: Prcdivtrab.fecfin
	*/
	String  PRCDIVTRAB_FECFIN = PRCDIVTRAB_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Prcdivtrab_id_cdg
	* Hibernate value: Prcdivtrab.id.cdg
	*/
	String  PRCDIVTRAB_ID_CDG = PRCDIVTRAB_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Prcdivtrab_id_fecini
	* Hibernate value: Prcdivtrab.id.fecini
	*/
	String  PRCDIVTRAB_ID_FECINI = PRCDIVTRAB_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Prcdivtrab_id_orden
	* Hibernate value: Prcdivtrab.id.orden
	*/
	String  PRCDIVTRAB_ID_ORDEN = PRCDIVTRAB_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Prcdivtrab_prc
	* Hibernate value: Prcdivtrab.prc
	*/
	String  PRCDIVTRAB_PRC = PRCDIVTRAB_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Prcdivtrab_texto
	* Hibernate value: Prcdivtrab.texto
	*/
	String  PRCDIVTRAB_TEXTO = PRCDIVTRAB_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for Tipocont entity.
	*/ 
	DAOConstantsEntry TIPOCONT_ENTRY = DAOConstants.getDAOConstant(Tipocont.class);

	/** 
	* Alias value: Tipocont_cdg
	* Hibernate value: Tipocont.cdg
	*/
	String  TIPOCONT_CDG = TIPOCONT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Tipocont_descripcion
	* Hibernate value: Tipocont.descripcion
	*/
	String  TIPOCONT_DESCRIPCION = TIPOCONT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Tipocont_desemple
	* Hibernate value: Tipocont.desemple
	*/
	String  TIPOCONT_DESEMPLE = TIPOCONT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Tipocont_excsocial
	* Hibernate value: Tipocont.excsocial
	*/
	String  TIPOCONT_EXCSOCIAL = TIPOCONT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Tipocont_gradomin
	* Hibernate value: Tipocont.gradomin
	*/
	String  TIPOCONT_GRADOMIN = TIPOCONT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Tipocont_incaread
	* Hibernate value: Tipocont.incaread
	*/
	String  TIPOCONT_INCAREAD = TIPOCONT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Tipocont_mujersub
	* Hibernate value: Tipocont.mujersub
	*/
	String  TIPOCONT_MUJERSUB = TIPOCONT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Tipocont_porcoti_cdg
	* Hibernate value: Tipocont.porcoti.cdg
	*/
	String  TIPOCONT_PORCOTI_CDG = TIPOCONT_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Tipocont_primertra
	* Hibernate value: Tipocont.primertra
	*/
	String  TIPOCONT_PRIMERTRA = TIPOCONT_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for Trabajo entity.
	*/ 
	DAOConstantsEntry TRABAJO_ENTRY = DAOConstants.getDAOConstant(Trabajo.class);

	/** 
	* Alias value: Trabajo_baseant
	* Hibernate value: Trabajo.baseant
	*/
	String  TRABAJO_BASEANT = TRABAJO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Trabajo_basecoti_cdg
	* Hibernate value: Trabajo.basecoti.cdg
	*/
	String  TRABAJO_BASECOTI_CDG = TRABAJO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Trabajo_cantp
	* Hibernate value: Trabajo.cantp
	*/
	String  TRABAJO_CANTP = TRABAJO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Trabajo_cno
	* Hibernate value: Trabajo.cno
	*/
	String  TRABAJO_CNO = TRABAJO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Trabajo_codcat
	* Hibernate value: Trabajo.codcat
	*/
	String  TRABAJO_CODCAT = TRABAJO_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Trabajo_coered
	* Hibernate value: Trabajo.coered
	*/
	String  TRABAJO_COERED = TRABAJO_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Trabajo_colectivos_cdg
	* Hibernate value: Trabajo.colectivos.cdg
	*/
	String  TRABAJO_COLECTIVOS_CDG = TRABAJO_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Trabajo_concol
	* Hibernate value: Trabajo.concol
	*/
	String  TRABAJO_CONCOL = TRABAJO_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Trabajo_convenio_cdg
	* Hibernate value: Trabajo.convenio.cdg
	*/
	String  TRABAJO_CONVENIO_CDG = TRABAJO_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Trabajo_ctacar
	* Hibernate value: Trabajo.ctacar
	*/
	String  TRABAJO_CTACAR = TRABAJO_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Trabajo_dc
	* Hibernate value: Trabajo.dc
	*/
	String  TRABAJO_DC = TRABAJO_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Trabajo_descat
	* Hibernate value: Trabajo.descat
	*/
	String  TRABAJO_DESCAT = TRABAJO_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Trabajo_destc2
	* Hibernate value: Trabajo.destc2
	*/
	String  TRABAJO_DESTC2 = TRABAJO_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Trabajo_diascont
	* Hibernate value: Trabajo.diascont
	*/
	String  TRABAJO_DIASCONT = TRABAJO_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Trabajo_emprper_cdg
	* Hibernate value: Trabajo.emprper.cdg
	*/
	String  TRABAJO_EMPRPER_CDG = TRABAJO_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Trabajo_entidad_cdg
	* Hibernate value: Trabajo.entidad.cdg
	*/
	String  TRABAJO_ENTIDAD_CDG = TRABAJO_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Trabajo_epigrafe_cdg
	* Hibernate value: Trabajo.epigrafe.cdg
	*/
	String  TRABAJO_EPIGRAFE_CDG = TRABAJO_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Trabajo_especial
	* Hibernate value: Trabajo.especial
	*/
	String  TRABAJO_ESPECIAL = TRABAJO_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Trabajo_fecant
	* Hibernate value: Trabajo.fecant
	*/
	String  TRABAJO_FECANT = TRABAJO_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Trabajo_fecaut
	* Hibernate value: Trabajo.fecaut
	*/
	String  TRABAJO_FECAUT = TRABAJO_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Trabajo_fecfin
	* Hibernate value: Trabajo.fecfin
	*/
	String  TRABAJO_FECFIN = TRABAJO_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Trabajo_fecfincont
	* Hibernate value: Trabajo.fecfincont
	*/
	String  TRABAJO_FECFINCONT = TRABAJO_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Trabajo_fecinicont
	* Hibernate value: Trabajo.fecinicont
	*/
	String  TRABAJO_FECINICONT = TRABAJO_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Trabajo_fecmod
	* Hibernate value: Trabajo.fecmod
	*/
	String  TRABAJO_FECMOD = TRABAJO_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Trabajo_fecnew
	* Hibernate value: Trabajo.fecnew
	*/
	String  TRABAJO_FECNEW = TRABAJO_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Trabajo_historico
	* Hibernate value: Trabajo.historico
	*/
	String  TRABAJO_HISTORICO = TRABAJO_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Trabajo_hormod
	* Hibernate value: Trabajo.hormod
	*/
	String  TRABAJO_HORMOD = TRABAJO_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: Trabajo_hornew
	* Hibernate value: Trabajo.hornew
	*/
	String  TRABAJO_HORNEW = TRABAJO_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: Trabajo_id_cdg
	* Hibernate value: Trabajo.id.cdg
	*/
	String  TRABAJO_ID_CDG = TRABAJO_ENTRY.getAliasNames()[28];

	/** 
	* Alias value: Trabajo_id_fecini
	* Hibernate value: Trabajo.id.fecini
	*/
	String  TRABAJO_ID_FECINI = TRABAJO_ENTRY.getAliasNames()[29];

	/** 
	* Alias value: Trabajo_indactcon
	* Hibernate value: Trabajo.indactcon
	*/
	String  TRABAJO_INDACTCON = TRABAJO_ENTRY.getAliasNames()[30];

	/** 
	* Alias value: Trabajo_indalt
	* Hibernate value: Trabajo.indalt
	*/
	String  TRABAJO_INDALT = TRABAJO_ENTRY.getAliasNames()[31];

	/** 
	* Alias value: Trabajo_indceutamelilla
	* Hibernate value: Trabajo.indceutamelilla
	*/
	String  TRABAJO_INDCEUTAMELILLA = TRABAJO_ENTRY.getAliasNames()[32];

	/** 
	* Alias value: Trabajo_inddtoit
	* Hibernate value: Trabajo.inddtoit
	*/
	String  TRABAJO_INDDTOIT = TRABAJO_ENTRY.getAliasNames()[33];

	/** 
	* Alias value: Trabajo_inddtootr
	* Hibernate value: Trabajo.inddtootr
	*/
	String  TRABAJO_INDDTOOTR = TRABAJO_ENTRY.getAliasNames()[34];

	/** 
	* Alias value: Trabajo_indirpf
	* Hibernate value: Trabajo.indirpf
	*/
	String  TRABAJO_INDIRPF = TRABAJO_ENTRY.getAliasNames()[35];

	/** 
	* Alias value: Trabajo_indtp
	* Hibernate value: Trabajo.indtp
	*/
	String  TRABAJO_INDTP = TRABAJO_ENTRY.getAliasNames()[36];

	/** 
	* Alias value: Trabajo_irpf
	* Hibernate value: Trabajo.irpf
	*/
	String  TRABAJO_IRPF = TRABAJO_ENTRY.getAliasNames()[37];

	/** 
	* Alias value: Trabajo_nivel
	* Hibernate value: Trabajo.nivel
	*/
	String  TRABAJO_NIVEL = TRABAJO_ENTRY.getAliasNames()[38];

	/** 
	* Alias value: Trabajo_numcta
	* Hibernate value: Trabajo.numcta
	*/
	String  TRABAJO_NUMCTA = TRABAJO_ENTRY.getAliasNames()[39];

	/** 
	* Alias value: Trabajo_nummat
	* Hibernate value: Trabajo.nummat
	*/
	String  TRABAJO_NUMMAT = TRABAJO_ENTRY.getAliasNames()[40];

	/** 
	* Alias value: Trabajo_ocupacion
	* Hibernate value: Trabajo.ocupacion
	*/
	String  TRABAJO_OCUPACION = TRABAJO_ENTRY.getAliasNames()[41];

	/** 
	* Alias value: Trabajo_plufecaut
	* Hibernate value: Trabajo.plufecaut
	*/
	String  TRABAJO_PLUFECAUT = TRABAJO_ENTRY.getAliasNames()[42];

	/** 
	* Alias value: Trabajo_plunumaut
	* Hibernate value: Trabajo.plunumaut
	*/
	String  TRABAJO_PLUNUMAUT = TRABAJO_ENTRY.getAliasNames()[43];

	/** 
	* Alias value: Trabajo_pluprcmax
	* Hibernate value: Trabajo.pluprcmax
	*/
	String  TRABAJO_PLUPRCMAX = TRABAJO_ENTRY.getAliasNames()[44];

	/** 
	* Alias value: Trabajo_pluprcmin
	* Hibernate value: Trabajo.pluprcmin
	*/
	String  TRABAJO_PLUPRCMIN = TRABAJO_ENTRY.getAliasNames()[45];

	/** 
	* Alias value: Trabajo_porcoti_cdg
	* Hibernate value: Trabajo.porcoti.cdg
	*/
	String  TRABAJO_PORCOTI_CDG = TRABAJO_ENTRY.getAliasNames()[46];

	/** 
	* Alias value: Trabajo_procot
	* Hibernate value: Trabajo.procot
	*/
	String  TRABAJO_PROCOT = TRABAJO_ENTRY.getAliasNames()[47];

	/** 
	* Alias value: Trabajo_profesion
	* Hibernate value: Trabajo.profesion
	*/
	String  TRABAJO_PROFESION = TRABAJO_ENTRY.getAliasNames()[48];

	/** 
	* Alias value: Trabajo_proret
	* Hibernate value: Trabajo.proret
	*/
	String  TRABAJO_PRORET = TRABAJO_ENTRY.getAliasNames()[49];

	/** 
	* Alias value: Trabajo_relacion
	* Hibernate value: Trabajo.relacion
	*/
	String  TRABAJO_RELACION = TRABAJO_ENTRY.getAliasNames()[50];

	/** 
	* Alias value: Trabajo_semana
	* Hibernate value: Trabajo.semana
	*/
	String  TRABAJO_SEMANA = TRABAJO_ENTRY.getAliasNames()[51];

	/** 
	* Alias value: Trabajo_semanatp
	* Hibernate value: Trabajo.semanatp
	*/
	String  TRABAJO_SEMANATP = TRABAJO_ENTRY.getAliasNames()[52];

	/** 
	* Alias value: Trabajo_sucursal_id_cdg
	* Hibernate value: Trabajo.sucursal.id.cdg
	*/
	String  TRABAJO_SUCURSAL_ID_CDG = TRABAJO_ENTRY.getAliasNames()[53];

	/** 
	* Alias value: Trabajo_sucursal_id_codent
	* Hibernate value: Trabajo.sucursal.id.codent
	*/
	String  TRABAJO_SUCURSAL_ID_CODENT = TRABAJO_ENTRY.getAliasNames()[54];

	/** 
	* Alias value: Trabajo_tipaut_cdg
	* Hibernate value: Trabajo.tipaut.cdg
	*/
	String  TRABAJO_TIPAUT_CDG = TRABAJO_ENTRY.getAliasNames()[55];

	/** 
	* Alias value: Trabajo_tipcotc2_cdg
	* Hibernate value: Trabajo.tipcotc2.cdg
	*/
	String  TRABAJO_TIPCOTC2_CDG = TRABAJO_ENTRY.getAliasNames()[56];

	/** 
	* Alias value: Trabajo_tipocont_cdg
	* Hibernate value: Trabajo.tipocont.cdg
	*/
	String  TRABAJO_TIPOCONT_CDG = TRABAJO_ENTRY.getAliasNames()[57];



	/** 
	* DAOConstantsEntry for Trabdto entity.
	*/ 
	DAOConstantsEntry TRABDTO_ENTRY = DAOConstants.getDAOConstant(Trabdto.class);

	/** 
	* Alias value: Trabdto_afecta
	* Hibernate value: Trabdto.afecta
	*/
	String  TRABDTO_AFECTA = TRABDTO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Trabdto_concepto
	* Hibernate value: Trabdto.concepto
	*/
	String  TRABDTO_CONCEPTO = TRABDTO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Trabdto_emprper_cdg
	* Hibernate value: Trabdto.emprper.cdg
	*/
	String  TRABDTO_EMPRPER_CDG = TRABDTO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Trabdto_fecfin
	* Hibernate value: Trabdto.fecfin
	*/
	String  TRABDTO_FECFIN = TRABDTO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Trabdto_fecini
	* Hibernate value: Trabdto.fecini
	*/
	String  TRABDTO_FECINI = TRABDTO_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Trabdto_fecmod
	* Hibernate value: Trabdto.fecmod
	*/
	String  TRABDTO_FECMOD = TRABDTO_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Trabdto_fecnew
	* Hibernate value: Trabdto.fecnew
	*/
	String  TRABDTO_FECNEW = TRABDTO_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Trabdto_hormod
	* Hibernate value: Trabdto.hormod
	*/
	String  TRABDTO_HORMOD = TRABDTO_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Trabdto_hornew
	* Hibernate value: Trabdto.hornew
	*/
	String  TRABDTO_HORNEW = TRABDTO_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Trabdto_id_cdg
	* Hibernate value: Trabdto.id.cdg
	*/
	String  TRABDTO_ID_CDG = TRABDTO_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Trabdto_id_orden
	* Hibernate value: Trabdto.id.orden
	*/
	String  TRABDTO_ID_ORDEN = TRABDTO_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Trabdto_importe
	* Hibernate value: Trabdto.importe
	*/
	String  TRABDTO_IMPORTE = TRABDTO_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Trabdto_indimp
	* Hibernate value: Trabdto.indimp
	*/
	String  TRABDTO_INDIMP = TRABDTO_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Trabdto_linea
	* Hibernate value: Trabdto.linea
	*/
	String  TRABDTO_LINEA = TRABDTO_ENTRY.getAliasNames()[13];



	/** 
	* DAOConstantsEntry for Trabinci entity.
	*/ 
	DAOConstantsEntry TRABINCI_ENTRY = DAOConstants.getDAOConstant(Trabinci.class);

	/** 
	* Alias value: Trabinci_cantidad
	* Hibernate value: Trabinci.cantidad
	*/
	String  TRABINCI_CANTIDAD = TRABINCI_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Trabinci_emprper_cdg
	* Hibernate value: Trabinci.emprper.cdg
	*/
	String  TRABINCI_EMPRPER_CDG = TRABINCI_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Trabinci_fecfin
	* Hibernate value: Trabinci.fecfin
	*/
	String  TRABINCI_FECFIN = TRABINCI_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Trabinci_fecmod
	* Hibernate value: Trabinci.fecmod
	*/
	String  TRABINCI_FECMOD = TRABINCI_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Trabinci_fecnew
	* Hibernate value: Trabinci.fecnew
	*/
	String  TRABINCI_FECNEW = TRABINCI_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Trabinci_hormod
	* Hibernate value: Trabinci.hormod
	*/
	String  TRABINCI_HORMOD = TRABINCI_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Trabinci_hornew
	* Hibernate value: Trabinci.hornew
	*/
	String  TRABINCI_HORNEW = TRABINCI_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Trabinci_id_cdg
	* Hibernate value: Trabinci.id.cdg
	*/
	String  TRABINCI_ID_CDG = TRABINCI_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Trabinci_id_codinc
	* Hibernate value: Trabinci.id.codinc
	*/
	String  TRABINCI_ID_CODINC = TRABINCI_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Trabinci_id_fecini
	* Hibernate value: Trabinci.id.fecini
	*/
	String  TRABINCI_ID_FECINI = TRABINCI_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Trabinci_importe
	* Hibernate value: Trabinci.importe
	*/
	String  TRABINCI_IMPORTE = TRABINCI_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Trabinci_tipinc_cdg
	* Hibernate value: Trabinci.tipinc.cdg
	*/
	String  TRABINCI_TIPINC_CDG = TRABINCI_ENTRY.getAliasNames()[11];



	/** 
	* DAOConstantsEntry for Tc2 entity.
	*/ 
	DAOConstantsEntry TC2_ENTRY = DAOConstants.getDAOConstant(Tc2.class);

	/** 
	* Alias value: Tc2_anio
	* Hibernate value: Tc2.anio
	*/
	String  TC2_ANIO = TC2_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Tc2_anioref
	* Hibernate value: Tc2.anioref
	*/
	String  TC2_ANIOREF = TC2_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Tc2_baseAcctra
	* Hibernate value: Tc2.baseAcctra
	*/
	String  TC2_BASE_ACCTRA = TC2_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Tc2_baseCccemp
	* Hibernate value: Tc2.baseCccemp
	*/
	String  TC2_BASE_CCCEMP = TC2_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Tc2_baseConcom
	* Hibernate value: Tc2.baseConcom
	*/
	String  TC2_BASE_CONCOM = TC2_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Tc2_baseHexest
	* Hibernate value: Tc2.baseHexest
	*/
	String  TC2_BASE_HEXEST = TC2_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Tc2_baseHexno
	* Hibernate value: Tc2.baseHexno
	*/
	String  TC2_BASE_HEXNO = TC2_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Tc2_baseOccemp
	* Hibernate value: Tc2.baseOccemp
	*/
	String  TC2_BASE_OCCEMP = TC2_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Tc2_cdg
	* Hibernate value: Tc2.cdg
	*/
	String  TC2_CDG = TC2_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Tc2_codact_cdg
	* Hibernate value: Tc2.codact.cdg
	*/
	String  TC2_CODACT_CDG = TC2_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Tc2_codccc
	* Hibernate value: Tc2.codccc
	*/
	String  TC2_CODCCC = TC2_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Tc2_codcon
	* Hibernate value: Tc2.codcon
	*/
	String  TC2_CODCON = TC2_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Tc2_compAcc
	* Hibernate value: Tc2.compAcc
	*/
	String  TC2_COMP_ACC = TC2_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Tc2_compEcal
	* Hibernate value: Tc2.compEcal
	*/
	String  TC2_COMP_ECAL = TC2_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Tc2_divisa
	* Hibernate value: Tc2.divisa
	*/
	String  TC2_DIVISA = TC2_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Tc2_fecmod
	* Hibernate value: Tc2.fecmod
	*/
	String  TC2_FECMOD = TC2_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Tc2_fecnew
	* Hibernate value: Tc2.fecnew
	*/
	String  TC2_FECNEW = TC2_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Tc2_hormod
	* Hibernate value: Tc2.hormod
	*/
	String  TC2_HORMOD = TC2_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Tc2_hornew
	* Hibernate value: Tc2.hornew
	*/
	String  TC2_HORNEW = TC2_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Tc2_imprime
	* Hibernate value: Tc2.imprime
	*/
	String  TC2_IMPRIME = TC2_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Tc2_indregimen
	* Hibernate value: Tc2.indregimen
	*/
	String  TC2_INDREGIMEN = TC2_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Tc2_mes
	* Hibernate value: Tc2.mes
	*/
	String  TC2_MES = TC2_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Tc2_mesref
	* Hibernate value: Tc2.mesref
	*/
	String  TC2_MESREF = TC2_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Tc2_numtra
	* Hibernate value: Tc2.numtra
	*/
	String  TC2_NUMTRA = TC2_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Tc2_redConcom
	* Hibernate value: Tc2.redConcom
	*/
	String  TC2_RED_CONCOM = TC2_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Tc2_redInem
	* Hibernate value: Tc2.redInem
	*/
	String  TC2_RED_INEM = TC2_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Tc2_tc2red
	* Hibernate value: Tc2.tc2red
	*/
	String  TC2_TC2RED = TC2_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: Tc2_tipo
	* Hibernate value: Tc2.tipo
	*/
	String  TC2_TIPO = TC2_ENTRY.getAliasNames()[27];



	/** 
	* DAOConstantsEntry for Lintc2 entity.
	*/ 
	DAOConstantsEntry LINTC2_ENTRY = DAOConstants.getDAOConstant(Lintc2.class);

	/** 
	* Alias value: Lintc2_aliastc2
	* Hibernate value: Lintc2.aliastc2
	*/
	String  LINTC2_ALIASTC2 = LINTC2_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Lintc2_base
	* Hibernate value: Lintc2.base
	*/
	String  LINTC2_BASE = LINTC2_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Lintc2_clave
	* Hibernate value: Lintc2.clave
	*/
	String  LINTC2_CLAVE = LINTC2_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Lintc2_codepi
	* Hibernate value: Lintc2.codepi
	*/
	String  LINTC2_CODEPI = LINTC2_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Lintc2_codtc2
	* Hibernate value: Lintc2.codtc2
	*/
	String  LINTC2_CODTC2 = LINTC2_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Lintc2_dcClave
	* Hibernate value: Lintc2.dcClave
	*/
	String  LINTC2_DC_CLAVE = LINTC2_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Lintc2_dcDias
	* Hibernate value: Lintc2.dcDias
	*/
	String  LINTC2_DC_DIAS = LINTC2_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Lintc2_dcFecha
	* Hibernate value: Lintc2.dcFecha
	*/
	String  LINTC2_DC_FECHA = LINTC2_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Lintc2_dcImporte
	* Hibernate value: Lintc2.dcImporte
	*/
	String  LINTC2_DC_IMPORTE = LINTC2_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Lintc2_id_cdg
	* Hibernate value: Lintc2.id.cdg
	*/
	String  LINTC2_ID_CDG = LINTC2_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Lintc2_id_linea
	* Hibernate value: Lintc2.id.linea
	*/
	String  LINTC2_ID_LINEA = LINTC2_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Lintc2_inddoc
	* Hibernate value: Lintc2.inddoc
	*/
	String  LINTC2_INDDOC = LINTC2_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Lintc2_numdh
	* Hibernate value: Lintc2.numdh
	*/
	String  LINTC2_NUMDH = LINTC2_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Lintc2_numdoc
	* Hibernate value: Lintc2.numdoc
	*/
	String  LINTC2_NUMDOC = LINTC2_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Lintc2_numero
	* Hibernate value: Lintc2.numero
	*/
	String  LINTC2_NUMERO = LINTC2_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Lintc2_numss
	* Hibernate value: Lintc2.numss
	*/
	String  LINTC2_NUMSS = LINTC2_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Lintc2_persona_cdg
	* Hibernate value: Lintc2.persona.cdg
	*/
	String  LINTC2_PERSONA_CDG = LINTC2_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Lintc2_sitesp
	* Hibernate value: Lintc2.sitesp
	*/
	String  LINTC2_SITESP = LINTC2_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Lintc2_tc2_cdg
	* Hibernate value: Lintc2.tc2.cdg
	*/
	String  LINTC2_TC2_CDG = LINTC2_ENTRY.getAliasNames()[18];



	/** 
	* DAOConstantsEntry for Tc1 entity.
	*/ 
	DAOConstantsEntry TC1_ENTRY = DAOConstants.getDAOConstant(Tc1.class);

	/** 
	* Alias value: Tc1_apellidos
	* Hibernate value: Tc1.apellidos
	*/
	String  TC1_APELLIDOS = TC1_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Tc1_baseAcc
	* Hibernate value: Tc1.baseAcc
	*/
	String  TC1_BASE_ACC = TC1_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Tc1_baseAcctra
	* Hibernate value: Tc1.baseAcctra
	*/
	String  TC1_BASE_ACCTRA = TC1_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Tc1_baseConcom
	* Hibernate value: Tc1.baseConcom
	*/
	String  TC1_BASE_CONCOM = TC1_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Tc1_baseConcomCe
	* Hibernate value: Tc1.baseConcomCe
	*/
	String  TC1_BASE_CONCOM_CE = TC1_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Tc1_baseDedcol
	* Hibernate value: Tc1.baseDedcol
	*/
	String  TC1_BASE_DEDCOL = TC1_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Tc1_baseDesem
	* Hibernate value: Tc1.baseDesem
	*/
	String  TC1_BASE_DESEM = TC1_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Tc1_baseDesemCe
	* Hibernate value: Tc1.baseDesemCe
	*/
	String  TC1_BASE_DESEM_CE = TC1_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Tc1_baseHexest
	* Hibernate value: Tc1.baseHexest
	*/
	String  TC1_BASE_HEXEST = TC1_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Tc1_baseHexno
	* Hibernate value: Tc1.baseHexno
	*/
	String  TC1_BASE_HEXNO = TC1_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Tc1_baseMora
	* Hibernate value: Tc1.baseMora
	*/
	String  TC1_BASE_MORA = TC1_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Tc1_baseOtrcon
	* Hibernate value: Tc1.baseOtrcon
	*/
	String  TC1_BASE_OTRCON = TC1_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Tc1_baseRedcc
	* Hibernate value: Tc1.baseRedcc
	*/
	String  TC1_BASE_REDCC = TC1_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Tc1_baseRedit
	* Hibernate value: Tc1.baseRedit
	*/
	String  TC1_BASE_REDIT = TC1_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Tc1_baseReducc
	* Hibernate value: Tc1.baseReducc
	*/
	String  TC1_BASE_REDUCC = TC1_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Tc1_baseServcom
	* Hibernate value: Tc1.baseServcom
	*/
	String  TC1_BASE_SERVCOM = TC1_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Tc1_cdg
	* Hibernate value: Tc1.cdg
	*/
	String  TC1_CDG = TC1_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Tc1_cdgOtrcon
	* Hibernate value: Tc1.cdgOtrcon
	*/
	String  TC1_CDG_OTRCON = TC1_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Tc1_cdgred
	* Hibernate value: Tc1.cdgred
	*/
	String  TC1_CDGRED = TC1_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Tc1_codact_cdg
	* Hibernate value: Tc1.codact.cdg
	*/
	String  TC1_CODACT_CDG = TC1_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Tc1_codbas
	* Hibernate value: Tc1.codbas
	*/
	String  TC1_CODBAS = TC1_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Tc1_codccc
	* Hibernate value: Tc1.codccc
	*/
	String  TC1_CODCCC = TC1_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Tc1_codepi
	* Hibernate value: Tc1.codepi
	*/
	String  TC1_CODEPI = TC1_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Tc1_codper_cdg
	* Hibernate value: Tc1.codper.cdg
	*/
	String  TC1_CODPER_CDG = TC1_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Tc1_codtc2_cdg
	* Hibernate value: Tc1.codtc2.cdg
	*/
	String  TC1_CODTC2_CDG = TC1_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Tc1_comision
	* Hibernate value: Tc1.comision
	*/
	String  TC1_COMISION = TC1_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Tc1_compIt
	* Hibernate value: Tc1.compIt
	*/
	String  TC1_COMP_IT = TC1_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: Tc1_cuotaConcom
	* Hibernate value: Tc1.cuotaConcom
	*/
	String  TC1_CUOTA_CONCOM = TC1_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: Tc1_cuotaConcomCe
	* Hibernate value: Tc1.cuotaConcomCe
	*/
	String  TC1_CUOTA_CONCOM_CE = TC1_ENTRY.getAliasNames()[28];

	/** 
	* Alias value: Tc1_cuotaDedcol
	* Hibernate value: Tc1.cuotaDedcol
	*/
	String  TC1_CUOTA_DEDCOL = TC1_ENTRY.getAliasNames()[29];

	/** 
	* Alias value: Tc1_cuotaDesem
	* Hibernate value: Tc1.cuotaDesem
	*/
	String  TC1_CUOTA_DESEM = TC1_ENTRY.getAliasNames()[30];

	/** 
	* Alias value: Tc1_cuotaDesemCe
	* Hibernate value: Tc1.cuotaDesemCe
	*/
	String  TC1_CUOTA_DESEM_CE = TC1_ENTRY.getAliasNames()[31];

	/** 
	* Alias value: Tc1_cuotaHexest
	* Hibernate value: Tc1.cuotaHexest
	*/
	String  TC1_CUOTA_HEXEST = TC1_ENTRY.getAliasNames()[32];

	/** 
	* Alias value: Tc1_cuotaHexno
	* Hibernate value: Tc1.cuotaHexno
	*/
	String  TC1_CUOTA_HEXNO = TC1_ENTRY.getAliasNames()[33];

	/** 
	* Alias value: Tc1_cuotaMora
	* Hibernate value: Tc1.cuotaMora
	*/
	String  TC1_CUOTA_MORA = TC1_ENTRY.getAliasNames()[34];

	/** 
	* Alias value: Tc1_cuotaOtrcon
	* Hibernate value: Tc1.cuotaOtrcon
	*/
	String  TC1_CUOTA_OTRCON = TC1_ENTRY.getAliasNames()[35];

	/** 
	* Alias value: Tc1_cuotaServcom
	* Hibernate value: Tc1.cuotaServcom
	*/
	String  TC1_CUOTA_SERVCOM = TC1_ENTRY.getAliasNames()[36];

	/** 
	* Alias value: Tc1_cuotasAcc
	* Hibernate value: Tc1.cuotasAcc
	*/
	String  TC1_CUOTAS_ACC = TC1_ENTRY.getAliasNames()[37];

	/** 
	* Alias value: Tc1_cuotasIms
	* Hibernate value: Tc1.cuotasIms
	*/
	String  TC1_CUOTAS_IMS = TC1_ENTRY.getAliasNames()[38];

	/** 
	* Alias value: Tc1_cuotasIt
	* Hibernate value: Tc1.cuotasIt
	*/
	String  TC1_CUOTAS_IT = TC1_ENTRY.getAliasNames()[39];

	/** 
	* Alias value: Tc1_desdeAnio
	* Hibernate value: Tc1.desdeAnio
	*/
	String  TC1_DESDE_ANIO = TC1_ENTRY.getAliasNames()[40];

	/** 
	* Alias value: Tc1_desdeMes
	* Hibernate value: Tc1.desdeMes
	*/
	String  TC1_DESDE_MES = TC1_ENTRY.getAliasNames()[41];

	/** 
	* Alias value: Tc1_desglose
	* Hibernate value: Tc1.desglose
	*/
	String  TC1_DESGLOSE = TC1_ENTRY.getAliasNames()[42];

	/** 
	* Alias value: Tc1_dias
	* Hibernate value: Tc1.dias
	*/
	String  TC1_DIAS = TC1_ENTRY.getAliasNames()[43];

	/** 
	* Alias value: Tc1_diasit
	* Hibernate value: Tc1.diasit
	*/
	String  TC1_DIASIT = TC1_ENTRY.getAliasNames()[44];

	/** 
	* Alias value: Tc1_diasmat
	* Hibernate value: Tc1.diasmat
	*/
	String  TC1_DIASMAT = TC1_ENTRY.getAliasNames()[45];

	/** 
	* Alias value: Tc1_divisa
	* Hibernate value: Tc1.divisa
	*/
	String  TC1_DIVISA = TC1_ENTRY.getAliasNames()[46];

	/** 
	* Alias value: Tc1_fecha
	* Hibernate value: Tc1.fecha
	*/
	String  TC1_FECHA = TC1_ENTRY.getAliasNames()[47];

	/** 
	* Alias value: Tc1_fecmod
	* Hibernate value: Tc1.fecmod
	*/
	String  TC1_FECMOD = TC1_ENTRY.getAliasNames()[48];

	/** 
	* Alias value: Tc1_fecnew
	* Hibernate value: Tc1.fecnew
	*/
	String  TC1_FECNEW = TC1_ENTRY.getAliasNames()[49];

	/** 
	* Alias value: Tc1_hastaAnio
	* Hibernate value: Tc1.hastaAnio
	*/
	String  TC1_HASTA_ANIO = TC1_ENTRY.getAliasNames()[50];

	/** 
	* Alias value: Tc1_hastaMes
	* Hibernate value: Tc1.hastaMes
	*/
	String  TC1_HASTA_MES = TC1_ENTRY.getAliasNames()[51];

	/** 
	* Alias value: Tc1_horas
	* Hibernate value: Tc1.horas
	*/
	String  TC1_HORAS = TC1_ENTRY.getAliasNames()[52];

	/** 
	* Alias value: Tc1_horcomp
	* Hibernate value: Tc1.horcomp
	*/
	String  TC1_HORCOMP = TC1_ENTRY.getAliasNames()[53];

	/** 
	* Alias value: Tc1_hordist
	* Hibernate value: Tc1.hordist
	*/
	String  TC1_HORDIST = TC1_ENTRY.getAliasNames()[54];

	/** 
	* Alias value: Tc1_hormod
	* Hibernate value: Tc1.hormod
	*/
	String  TC1_HORMOD = TC1_ENTRY.getAliasNames()[55];

	/** 
	* Alias value: Tc1_hornew
	* Hibernate value: Tc1.hornew
	*/
	String  TC1_HORNEW = TC1_ENTRY.getAliasNames()[56];

	/** 
	* Alias value: Tc1_horpres
	* Hibernate value: Tc1.horpres
	*/
	String  TC1_HORPRES = TC1_ENTRY.getAliasNames()[57];

	/** 
	* Alias value: Tc1_impcomp
	* Hibernate value: Tc1.impcomp
	*/
	String  TC1_IMPCOMP = TC1_ENTRY.getAliasNames()[58];

	/** 
	* Alias value: Tc1_impdist
	* Hibernate value: Tc1.impdist
	*/
	String  TC1_IMPDIST = TC1_ENTRY.getAliasNames()[59];

	/** 
	* Alias value: Tc1_importeTc1
	* Hibernate value: Tc1.importeTc1
	*/
	String  TC1_IMPORTE_TC1 = TC1_ENTRY.getAliasNames()[60];

	/** 
	* Alias value: Tc1_imppres
	* Hibernate value: Tc1.imppres
	*/
	String  TC1_IMPPRES = TC1_ENTRY.getAliasNames()[61];

	/** 
	* Alias value: Tc1_indregimen
	* Hibernate value: Tc1.indregimen
	*/
	String  TC1_INDREGIMEN = TC1_ENTRY.getAliasNames()[62];

	/** 
	* Alias value: Tc1_liqAcc
	* Hibernate value: Tc1.liqAcc
	*/
	String  TC1_LIQ_ACC = TC1_ENTRY.getAliasNames()[63];

	/** 
	* Alias value: Tc1_liqCotgen
	* Hibernate value: Tc1.liqCotgen
	*/
	String  TC1_LIQ_COTGEN = TC1_ENTRY.getAliasNames()[64];

	/** 
	* Alias value: Tc1_liqOtras
	* Hibernate value: Tc1.liqOtras
	*/
	String  TC1_LIQ_OTRAS = TC1_ENTRY.getAliasNames()[65];

	/** 
	* Alias value: Tc1_mostrar
	* Hibernate value: Tc1.mostrar
	*/
	String  TC1_MOSTRAR = TC1_ENTRY.getAliasNames()[66];

	/** 
	* Alias value: Tc1_mutuaccc
	* Hibernate value: Tc1.mutuaccc
	*/
	String  TC1_MUTUACCC = TC1_ENTRY.getAliasNames()[67];

	/** 
	* Alias value: Tc1_nombre
	* Hibernate value: Tc1.nombre
	*/
	String  TC1_NOMBRE = TC1_ENTRY.getAliasNames()[68];

	/** 
	* Alias value: Tc1_numero
	* Hibernate value: Tc1.numero
	*/
	String  TC1_NUMERO = TC1_ENTRY.getAliasNames()[69];

	/** 
	* Alias value: Tc1_numtra
	* Hibernate value: Tc1.numtra
	*/
	String  TC1_NUMTRA = TC1_ENTRY.getAliasNames()[70];

	/** 
	* Alias value: Tc1_prcConcom
	* Hibernate value: Tc1.prcConcom
	*/
	String  TC1_PRC_CONCOM = TC1_ENTRY.getAliasNames()[71];

	/** 
	* Alias value: Tc1_prcConcomCe
	* Hibernate value: Tc1.prcConcomCe
	*/
	String  TC1_PRC_CONCOM_CE = TC1_ENTRY.getAliasNames()[72];

	/** 
	* Alias value: Tc1_prcDedcol
	* Hibernate value: Tc1.prcDedcol
	*/
	String  TC1_PRC_DEDCOL = TC1_ENTRY.getAliasNames()[73];

	/** 
	* Alias value: Tc1_prcDesem
	* Hibernate value: Tc1.prcDesem
	*/
	String  TC1_PRC_DESEM = TC1_ENTRY.getAliasNames()[74];

	/** 
	* Alias value: Tc1_prcDesemCe
	* Hibernate value: Tc1.prcDesemCe
	*/
	String  TC1_PRC_DESEM_CE = TC1_ENTRY.getAliasNames()[75];

	/** 
	* Alias value: Tc1_prcHexest
	* Hibernate value: Tc1.prcHexest
	*/
	String  TC1_PRC_HEXEST = TC1_ENTRY.getAliasNames()[76];

	/** 
	* Alias value: Tc1_prcHexno
	* Hibernate value: Tc1.prcHexno
	*/
	String  TC1_PRC_HEXNO = TC1_ENTRY.getAliasNames()[77];

	/** 
	* Alias value: Tc1_prcMora
	* Hibernate value: Tc1.prcMora
	*/
	String  TC1_PRC_MORA = TC1_ENTRY.getAliasNames()[78];

	/** 
	* Alias value: Tc1_prcOtrcon
	* Hibernate value: Tc1.prcOtrcon
	*/
	String  TC1_PRC_OTRCON = TC1_ENTRY.getAliasNames()[79];

	/** 
	* Alias value: Tc1_prcServcom
	* Hibernate value: Tc1.prcServcom
	*/
	String  TC1_PRC_SERVCOM = TC1_ENTRY.getAliasNames()[80];

	/** 
	* Alias value: Tc1_redInem
	* Hibernate value: Tc1.redInem
	*/
	String  TC1_RED_INEM = TC1_ENTRY.getAliasNames()[81];

	/** 
	* Alias value: Tc1_sitesp
	* Hibernate value: Tc1.sitesp
	*/
	String  TC1_SITESP = TC1_ENTRY.getAliasNames()[82];

	/** 
	* Alias value: Tc1_tc2
	* Hibernate value: Tc1.tc2
	*/
	String  TC1_TC2 = TC1_ENTRY.getAliasNames()[83];

	/** 
	* Alias value: Tc1_tipotc1
	* Hibernate value: Tc1.tipotc1
	*/
	String  TC1_TIPOTC1 = TC1_ENTRY.getAliasNames()[84];



	/** 
	* DAOConstantsEntry for Costes entity.
	*/ 
	DAOConstantsEntry COSTES_ENTRY = DAOConstants.getDAOConstant(Costes.class);

	/** 
	* Alias value: Costes_alto
	* Hibernate value: Costes.alto
	*/
	String  COSTES_ALTO = COSTES_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Costes_anionac
	* Hibernate value: Costes.anionac
	*/
	String  COSTES_ANIONAC = COSTES_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Costes_aprendiz
	* Hibernate value: Costes.aprendiz
	*/
	String  COSTES_APRENDIZ = COSTES_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Costes_basecoti_cdg
	* Hibernate value: Costes.basecoti.cdg
	*/
	String  COSTES_BASECOTI_CDG = COSTES_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Costes_cantp
	* Hibernate value: Costes.cantp
	*/
	String  COSTES_CANTP = COSTES_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Costes_codemp_cdg
	* Hibernate value: Costes.codemp.cdg
	*/
	String  COSTES_CODEMP_CDG = COSTES_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Costes_costeActual
	* Hibernate value: Costes.costeActual
	*/
	String  COSTES_COSTE_ACTUAL = COSTES_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Costes_costeDiferencia
	* Hibernate value: Costes.costeDiferencia
	*/
	String  COSTES_COSTE_DIFERENCIA = COSTES_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Costes_costeSupuesto
	* Hibernate value: Costes.costeSupuesto
	*/
	String  COSTES_COSTE_SUPUESTO = COSTES_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Costes_descripcion
	* Hibernate value: Costes.descripcion
	*/
	String  COSTES_DESCRIPCION = COSTES_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Costes_epigrafe_cdg
	* Hibernate value: Costes.epigrafe.cdg
	*/
	String  COSTES_EPIGRAFE_CDG = COSTES_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Costes_id_cdg
	* Hibernate value: Costes.id.cdg
	*/
	String  COSTES_ID_CDG = COSTES_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Costes_id_numero
	* Hibernate value: Costes.id.numero
	*/
	String  COSTES_ID_NUMERO = COSTES_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Costes_impActual
	* Hibernate value: Costes.impActual
	*/
	String  COSTES_IMP_ACTUAL = COSTES_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Costes_impAnual
	* Hibernate value: Costes.impAnual
	*/
	String  COSTES_IMP_ANUAL = COSTES_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Costes_impDiferencia
	* Hibernate value: Costes.impDiferencia
	*/
	String  COSTES_IMP_DIFERENCIA = COSTES_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Costes_impExtActual
	* Hibernate value: Costes.impExtActual
	*/
	String  COSTES_IMP_EXT_ACTUAL = COSTES_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Costes_impExtDiferencia
	* Hibernate value: Costes.impExtDiferencia
	*/
	String  COSTES_IMP_EXT_DIFERENCIA = COSTES_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Costes_impExtSupuesto
	* Hibernate value: Costes.impExtSupuesto
	*/
	String  COSTES_IMP_EXT_SUPUESTO = COSTES_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Costes_impIrreg
	* Hibernate value: Costes.impIrreg
	*/
	String  COSTES_IMP_IRREG = COSTES_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Costes_impPension
	* Hibernate value: Costes.impPension
	*/
	String  COSTES_IMP_PENSION = COSTES_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Costes_impSs
	* Hibernate value: Costes.impSs
	*/
	String  COSTES_IMP_SS = COSTES_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Costes_impSupuesto
	* Hibernate value: Costes.impSupuesto
	*/
	String  COSTES_IMP_SUPUESTO = COSTES_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Costes_indtp
	* Hibernate value: Costes.indtp
	*/
	String  COSTES_INDTP = COSTES_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Costes_irpfActual
	* Hibernate value: Costes.irpfActual
	*/
	String  COSTES_IRPF_ACTUAL = COSTES_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Costes_irpfDiferencia
	* Hibernate value: Costes.irpfDiferencia
	*/
	String  COSTES_IRPF_DIFERENCIA = COSTES_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Costes_irpfSupuesto
	* Hibernate value: Costes.irpfSupuesto
	*/
	String  COSTES_IRPF_SUPUESTO = COSTES_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: Costes_minimo
	* Hibernate value: Costes.minimo
	*/
	String  COSTES_MINIMO = COSTES_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: Costes_netoActual
	* Hibernate value: Costes.netoActual
	*/
	String  COSTES_NETO_ACTUAL = COSTES_ENTRY.getAliasNames()[28];

	/** 
	* Alias value: Costes_netoDiferencia
	* Hibernate value: Costes.netoDiferencia
	*/
	String  COSTES_NETO_DIFERENCIA = COSTES_ENTRY.getAliasNames()[29];

	/** 
	* Alias value: Costes_netoSupuesto
	* Hibernate value: Costes.netoSupuesto
	*/
	String  COSTES_NETO_SUPUESTO = COSTES_ENTRY.getAliasNames()[30];

	/** 
	* Alias value: Costes_plunumaut
	* Hibernate value: Costes.plunumaut
	*/
	String  COSTES_PLUNUMAUT = COSTES_ENTRY.getAliasNames()[31];

	/** 
	* Alias value: Costes_pluprcmax
	* Hibernate value: Costes.pluprcmax
	*/
	String  COSTES_PLUPRCMAX = COSTES_ENTRY.getAliasNames()[32];

	/** 
	* Alias value: Costes_pluprcmin
	* Hibernate value: Costes.pluprcmin
	*/
	String  COSTES_PLUPRCMIN = COSTES_ENTRY.getAliasNames()[33];

	/** 
	* Alias value: Costes_porcoti_cdg
	* Hibernate value: Costes.porcoti.cdg
	*/
	String  COSTES_PORCOTI_CDG = COSTES_ENTRY.getAliasNames()[34];

	/** 
	* Alias value: Costes_procot
	* Hibernate value: Costes.procot
	*/
	String  COSTES_PROCOT = COSTES_ENTRY.getAliasNames()[35];

	/** 
	* Alias value: Costes_proret
	* Hibernate value: Costes.proret
	*/
	String  COSTES_PRORET = COSTES_ENTRY.getAliasNames()[36];

	/** 
	* Alias value: Costes_semana
	* Hibernate value: Costes.semana
	*/
	String  COSTES_SEMANA = COSTES_ENTRY.getAliasNames()[37];

	/** 
	* Alias value: Costes_semanatp
	* Hibernate value: Costes.semanatp
	*/
	String  COSTES_SEMANATP = COSTES_ENTRY.getAliasNames()[38];

	/** 
	* Alias value: Costes_sitfam
	* Hibernate value: Costes.sitfam
	*/
	String  COSTES_SITFAM = COSTES_ENTRY.getAliasNames()[39];

	/** 
	* Alias value: Costes_solicita
	* Hibernate value: Costes.solicita
	*/
	String  COSTES_SOLICITA = COSTES_ENTRY.getAliasNames()[40];

	/** 
	* Alias value: Costes_ssActual
	* Hibernate value: Costes.ssActual
	*/
	String  COSTES_SS_ACTUAL = COSTES_ENTRY.getAliasNames()[41];

	/** 
	* Alias value: Costes_ssDiferencia
	* Hibernate value: Costes.ssDiferencia
	*/
	String  COSTES_SS_DIFERENCIA = COSTES_ENTRY.getAliasNames()[42];

	/** 
	* Alias value: Costes_ssEmpActual
	* Hibernate value: Costes.ssEmpActual
	*/
	String  COSTES_SS_EMP_ACTUAL = COSTES_ENTRY.getAliasNames()[43];

	/** 
	* Alias value: Costes_ssEmpDiferencia
	* Hibernate value: Costes.ssEmpDiferencia
	*/
	String  COSTES_SS_EMP_DIFERENCIA = COSTES_ENTRY.getAliasNames()[44];

	/** 
	* Alias value: Costes_ssEmpSupuesto
	* Hibernate value: Costes.ssEmpSupuesto
	*/
	String  COSTES_SS_EMP_SUPUESTO = COSTES_ENTRY.getAliasNames()[45];

	/** 
	* Alias value: Costes_ssSupuesto
	* Hibernate value: Costes.ssSupuesto
	*/
	String  COSTES_SS_SUPUESTO = COSTES_ENTRY.getAliasNames()[46];

	/** 
	* Alias value: Costes_temporal
	* Hibernate value: Costes.temporal
	*/
	String  COSTES_TEMPORAL = COSTES_ENTRY.getAliasNames()[47];

	/** 
	* Alias value: Costes_xminus
	* Hibernate value: Costes.xminus
	*/
	String  COSTES_XMINUS = COSTES_ENTRY.getAliasNames()[48];



	/** 
	* DAOConstantsEntry for Lbonifica entity.
	*/ 
	DAOConstantsEntry LBONIFICA_ENTRY = DAOConstants.getDAOConstant(Lbonifica.class);

	/** 
	* Alias value: Lbonifica_costes_id_cdg
	* Hibernate value: Lbonifica.costes.id.cdg
	*/
	String  LBONIFICA_COSTES_ID_CDG = LBONIFICA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Lbonifica_costes_id_numero
	* Hibernate value: Lbonifica.costes.id.numero
	*/
	String  LBONIFICA_COSTES_ID_NUMERO = LBONIFICA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Lbonifica_fecfin
	* Hibernate value: Lbonifica.fecfin
	*/
	String  LBONIFICA_FECFIN = LBONIFICA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Lbonifica_horas
	* Hibernate value: Lbonifica.horas
	*/
	String  LBONIFICA_HORAS = LBONIFICA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Lbonifica_id_cdg
	* Hibernate value: Lbonifica.id.cdg
	*/
	String  LBONIFICA_ID_CDG = LBONIFICA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Lbonifica_id_codbon
	* Hibernate value: Lbonifica.id.codbon
	*/
	String  LBONIFICA_ID_CODBON = LBONIFICA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Lbonifica_id_fecini
	* Hibernate value: Lbonifica.id.fecini
	*/
	String  LBONIFICA_ID_FECINI = LBONIFICA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Lbonifica_id_numero
	* Hibernate value: Lbonifica.id.numero
	*/
	String  LBONIFICA_ID_NUMERO = LBONIFICA_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Lbonifica_importe
	* Hibernate value: Lbonifica.importe
	*/
	String  LBONIFICA_IMPORTE = LBONIFICA_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for Lcomunica entity.
	*/ 
	DAOConstantsEntry LCOMUNICA_ENTRY = DAOConstants.getDAOConstant(Lcomunica.class);

	/** 
	* Alias value: Lcomunica_anionac
	* Hibernate value: Lcomunica.anionac
	*/
	String  LCOMUNICA_ANIONAC = LCOMUNICA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Lcomunica_conviv
	* Hibernate value: Lcomunica.conviv
	*/
	String  LCOMUNICA_CONVIV = LCOMUNICA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Lcomunica_costes_id_cdg
	* Hibernate value: Lcomunica.costes.id.cdg
	*/
	String  LCOMUNICA_COSTES_ID_CDG = LCOMUNICA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Lcomunica_costes_id_numero
	* Hibernate value: Lcomunica.costes.id.numero
	*/
	String  LCOMUNICA_COSTES_ID_NUMERO = LCOMUNICA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Lcomunica_desAsc
	* Hibernate value: Lcomunica.desAsc
	*/
	String  LCOMUNICA_DES_ASC = LCOMUNICA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Lcomunica_descenEnt
	* Hibernate value: Lcomunica.descenEnt
	*/
	String  LCOMUNICA_DESCEN_ENT = LCOMUNICA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Lcomunica_id_cdg
	* Hibernate value: Lcomunica.id.cdg
	*/
	String  LCOMUNICA_ID_CDG = LCOMUNICA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Lcomunica_id_numero
	* Hibernate value: Lcomunica.id.numero
	*/
	String  LCOMUNICA_ID_NUMERO = LCOMUNICA_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Lcomunica_id_orden
	* Hibernate value: Lcomunica.id.orden
	*/
	String  LCOMUNICA_ID_ORDEN = LCOMUNICA_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Lcomunica_xminus
	* Hibernate value: Lcomunica.xminus
	*/
	String  LCOMUNICA_XMINUS = LCOMUNICA_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for Calendario entity.
	*/ 
	DAOConstantsEntry CALENDARIO_ENTRY = DAOConstants.getDAOConstant(Calendario.class);

	/** 
	* Alias value: Calendario_actividad
	* Hibernate value: Calendario.actividad
	*/
	String  CALENDARIO_ACTIVIDAD = CALENDARIO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Calendario_cdg
	* Hibernate value: Calendario.cdg
	*/
	String  CALENDARIO_CDG = CALENDARIO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Calendario_domicilio
	* Hibernate value: Calendario.domicilio
	*/
	String  CALENDARIO_DOMICILIO = CALENDARIO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Calendario_empresa
	* Hibernate value: Calendario.empresa
	*/
	String  CALENDARIO_EMPRESA = CALENDARIO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Calendario_feccal
	* Hibernate value: Calendario.feccal
	*/
	String  CALENDARIO_FECCAL = CALENDARIO_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Calendario_tipdia
	* Hibernate value: Calendario.tipdia
	*/
	String  CALENDARIO_TIPDIA = CALENDARIO_ENTRY.getAliasNames()[5];


}