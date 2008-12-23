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
	* Alias value: Avisos_fecha
	* Hibernate value: Avisos.fecha
	*/
	String  AVISOS_FECHA = AVISOS_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Avisos_tipo
	* Hibernate value: Avisos.tipo
	*/
	String  AVISOS_TIPO = AVISOS_ENTRY.getAliasNames()[6];



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
	* Alias value: Trabajador_emprnif_cdg
	* Hibernate value: Trabajador.emprnif.cdg
	*/
	String  TRABAJADOR_EMPRNIF_CDG = TRABAJADOR_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Trabajador_fecalt
	* Hibernate value: Trabajador.fecalt
	*/
	String  TRABAJADOR_FECALT = TRABAJADOR_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Trabajador_fecbaj
	* Hibernate value: Trabajador.fecbaj
	*/
	String  TRABAJADOR_FECBAJ = TRABAJADOR_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Trabajador_fecmod
	* Hibernate value: Trabajador.fecmod
	*/
	String  TRABAJADOR_FECMOD = TRABAJADOR_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Trabajador_fecnew
	* Hibernate value: Trabajador.fecnew
	*/
	String  TRABAJADOR_FECNEW = TRABAJADOR_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Trabajador_hormod
	* Hibernate value: Trabajador.hormod
	*/
	String  TRABAJADOR_HORMOD = TRABAJADOR_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Trabajador_hornew
	* Hibernate value: Trabajador.hornew
	*/
	String  TRABAJADOR_HORNEW = TRABAJADOR_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Trabajador_indagrario
	* Hibernate value: Trabajador.indagrario
	*/
	String  TRABAJADOR_INDAGRARIO = TRABAJADOR_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Trabajador_indgrupo
	* Hibernate value: Trabajador.indgrupo
	*/
	String  TRABAJADOR_INDGRUPO = TRABAJADOR_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Trabajador_mayor65
	* Hibernate value: Trabajador.mayor65
	*/
	String  TRABAJADOR_MAYOR65 = TRABAJADOR_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Trabajador_pariente
	* Hibernate value: Trabajador.pariente
	*/
	String  TRABAJADOR_PARIENTE = TRABAJADOR_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Trabajador_persona_cdg
	* Hibernate value: Trabajador.persona.cdg
	*/
	String  TRABAJADOR_PERSONA_CDG = TRABAJADOR_ENTRY.getAliasNames()[16];



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
	* Alias value: Otrperc_clave
	* Hibernate value: Otrperc.clave
	*/
	String  OTRPERC_CLAVE = OTRPERC_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Otrperc_concepto
	* Hibernate value: Otrperc.concepto
	*/
	String  OTRPERC_CONCEPTO = OTRPERC_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Otrperc_empresa_cdg
	* Hibernate value: Otrperc.empresa.cdg
	*/
	String  OTRPERC_EMPRESA_CDG = OTRPERC_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Otrperc_fecha
	* Hibernate value: Otrperc.fecha
	*/
	String  OTRPERC_FECHA = OTRPERC_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Otrperc_importe
	* Hibernate value: Otrperc.importe
	*/
	String  OTRPERC_IMPORTE = OTRPERC_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Otrperc_ingreso
	* Hibernate value: Otrperc.ingreso
	*/
	String  OTRPERC_INGRESO = OTRPERC_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Otrperc_natret
	* Hibernate value: Otrperc.natret
	*/
	String  OTRPERC_NATRET = OTRPERC_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Otrperc_persona_apellido2
	* Hibernate value: Otrperc.persona.apellido2
	*/
	String  OTRPERC_PERSONA_APELLIDO2 = OTRPERC_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Otrperc_persona_cdg
	* Hibernate value: Otrperc.persona.cdg
	*/
	String  OTRPERC_PERSONA_CDG = OTRPERC_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Otrperc_persona_descripcion
	* Hibernate value: Otrperc.persona.descripcion
	*/
	String  OTRPERC_PERSONA_DESCRIPCION = OTRPERC_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Otrperc_persona_nombre
	* Hibernate value: Otrperc..persona.nombre
	*/
	String  OTRPERC_PERSONA_NOMBRE = OTRPERC_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Otrperc_prcret
	* Hibernate value: Otrperc.prcret
	*/
	String  OTRPERC_PRCRET = OTRPERC_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Otrperc_retencion
	* Hibernate value: Otrperc.retencion
	*/
	String  OTRPERC_RETENCION = OTRPERC_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Otrperc_subclave
	* Hibernate value: Otrperc.subclave
	*/
	String  OTRPERC_SUBCLAVE = OTRPERC_ENTRY.getAliasNames()[17];



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


}