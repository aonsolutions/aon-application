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
import com.code.aon.payroll.tipos.Documento;
import com.code.aon.payroll.tipos.Autorizacion;
import com.code.aon.payroll.cotizacion.Base;
import com.code.aon.payroll.cotizacion.Linbasec;
import com.code.aon.payroll.tipos.Incidencia;
import com.code.aon.payroll.tipos.Registro;
import com.code.aon.payroll.tipos.Empresario;
import com.code.aon.payroll.tipos.TipoCnae;
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
import com.code.aon.payroll.auxiliares.convenios.Percepcion;
import com.code.aon.payroll.auxiliares.organismosyentidades.Entidad;
import com.code.aon.payroll.auxiliares.organismosyentidades.Sucursal;
import com.code.aon.payroll.auxiliares.organismosyentidades.Mutua;
import com.code.aon.payroll.auxiliares.organismosyentidades.Linmutua;
import com.code.aon.payroll.irpf.Elemirpf;
import com.code.aon.payroll.irpf.Linirpf;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.payroll.principales.Cuentas;

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
	* Alias value: Categoria_cno
	* Hibernate value: Categoria.cno
	*/
	String  CATEGORIA_CNO = CATEGORIA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Categoria_convenio_cdg
	* Hibernate value: Categoria.convenio.cdg
	*/
	String  CATEGORIA_CONVENIO_CDG = CATEGORIA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Categoria_description
	* Hibernate value: Categoria.description
	*/
	String  CATEGORIA_DESCRIPTION = CATEGORIA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Categoria_epigrafe_cdg
	* Hibernate value: Categoria.epigrafe.cdg
	*/
	String  CATEGORIA_EPIGRAFE_CDG = CATEGORIA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Categoria_id_cdg
	* Hibernate value: Categoria.id.cdg
	*/
	String  CATEGORIA_ID_CDG = CATEGORIA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Categoria_id_codcon
	* Hibernate value: Categoria.id.codcon
	*/
	String  CATEGORIA_ID_CODCON = CATEGORIA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Categoria_nivel_id_cdg
	* Hibernate value: Categoria.nivel.id.cdg
	*/
	String  CATEGORIA_NIVEL_ID_CDG = CATEGORIA_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Categoria_nivel_id_codcon
	* Hibernate value: Categoria.nivel.id.codcon
	*/
	String  CATEGORIA_NIVEL_ID_CODCON = CATEGORIA_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for Percepcion entity.
	*/ 
	DAOConstantsEntry PERCEPCION_ENTRY = DAOConstants.getDAOConstant(Percepcion.class);

	/** 
	* Alias value: Percepcion_calculo
	* Hibernate value: Percepcion.calculo
	*/
	String  PERCEPCION_CALCULO = PERCEPCION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Percepcion_complemento1_cdg
	* Hibernate value: Percepcion.complemento1.cdg
	*/
	String  PERCEPCION_COMPLEMENTO1_CDG = PERCEPCION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Percepcion_complemento_cdg
	* Hibernate value: Percepcion.complemento.cdg
	*/
	String  PERCEPCION_COMPLEMENTO_CDG = PERCEPCION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Percepcion_convenio_cdg
	* Hibernate value: Percepcion.convenio.cdg
	*/
	String  PERCEPCION_CONVENIO_CDG = PERCEPCION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Percepcion_convenio_description
	* Hibernate value: Percepcion.convenio.description
	*/
	String  PERCEPCION_CONVENIO_DESCRIPTION = PERCEPCION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Percepcion_desabr
	* Hibernate value: Percepcion.desabr
	*/
	String  PERCEPCION_DESABR = PERCEPCION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Percepcion_descom
	* Hibernate value: Percepcion.descom
	*/
	String  PERCEPCION_DESCOM = PERCEPCION_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Percepcion_dinesp
	* Hibernate value: Percepcion.dinesp
	*/
	String  PERCEPCION_DINESP = PERCEPCION_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Percepcion_fecmod
	* Hibernate value: Percepcion.fecmod
	*/
	String  PERCEPCION_FECMOD = PERCEPCION_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Percepcion_fecnew
	* Hibernate value: Percepcion.fecnew
	*/
	String  PERCEPCION_FECNEW = PERCEPCION_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Percepcion_fijovar
	* Hibernate value: Percepcion.fijovar
	*/
	String  PERCEPCION_FIJOVAR = PERCEPCION_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Percepcion_garilt
	* Hibernate value: Percepcion.garilt
	*/
	String  PERCEPCION_GARILT = PERCEPCION_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Percepcion_hormod
	* Hibernate value: Percepcion.hormod
	*/
	String  PERCEPCION_HORMOD = PERCEPCION_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Percepcion_hornew
	* Hibernate value: Percepcion.hornew
	*/
	String  PERCEPCION_HORNEW = PERCEPCION_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Percepcion_id_cdg
	* Hibernate value: Percepcion.id.cdg
	*/
	String  PERCEPCION_ID_CDG = PERCEPCION_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Percepcion_id_codcom
	* Hibernate value: Percepcion.id.codcom
	*/
	String  PERCEPCION_ID_CODCOM = PERCEPCION_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Percepcion_id_nivel
	* Hibernate value: Percepcion.id.nivel
	*/
	String  PERCEPCION_ID_NIVEL = PERCEPCION_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Percepcion_importe
	* Hibernate value: Percepcion.importe
	*/
	String  PERCEPCION_IMPORTE = PERCEPCION_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Percepcion_impuni
	* Hibernate value: Percepcion.impuni
	*/
	String  PERCEPCION_IMPUNI = PERCEPCION_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Percepcion_indcom
	* Hibernate value: Percepcion.indcom
	*/
	String  PERCEPCION_INDCOM = PERCEPCION_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Percepcion_mes
	* Hibernate value: Percepcion.mes
	*/
	String  PERCEPCION_MES = PERCEPCION_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Percepcion_nivel_id_cdg
	* Hibernate value: Percepcion.nivel.id.cdg
	*/
	String  PERCEPCION_NIVEL_ID_CDG = PERCEPCION_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Percepcion_nivel_id_codcon
	* Hibernate value: Percepcion.nivel.id.codcon
	*/
	String  PERCEPCION_NIVEL_ID_CODCON = PERCEPCION_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Percepcion_redext
	* Hibernate value: Percepcion.redext
	*/
	String  PERCEPCION_REDEXT = PERCEPCION_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Percepcion_tipcom
	* Hibernate value: Percepcion.tipcom
	*/
	String  PERCEPCION_TIPCOM = PERCEPCION_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Percepcion_tipcot
	* Hibernate value: Percepcion.tipcot
	*/
	String  PERCEPCION_TIPCOT = PERCEPCION_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Percepcion_unidades
	* Hibernate value: Percepcion.unidades
	*/
	String  PERCEPCION_UNIDADES = PERCEPCION_ENTRY.getAliasNames()[26];



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
	* Alias value: Cuentas_numcta
	* Hibernate value: Cuentas.numcta
	*/
	String  CUENTAS_NUMCTA = CUENTAS_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Cuentas_sucursal_id_cdg
	* Hibernate value: Cuentas.sucursal.id.cdg
	*/
	String  CUENTAS_SUCURSAL_ID_CDG = CUENTAS_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Cuentas_sucursal_id_codent
	* Hibernate value: Cuentas.sucursal.id.codent
	*/
	String  CUENTAS_SUCURSAL_ID_CODENT = CUENTAS_ENTRY.getAliasNames()[6];


}