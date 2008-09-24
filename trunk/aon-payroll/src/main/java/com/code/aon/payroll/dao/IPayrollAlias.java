package com.code.aon.payroll.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.payroll.cotizacion.PorcentajeMaestro;
import com.code.aon.payroll.cotizacion.Porcentaje;
import com.code.aon.payroll.tipos.Documento;
import com.code.aon.payroll.tipos.Autorizacion;
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

/** 
* Interface for holding entity properties constants.
*/ 
public interface IPayrollAlias {



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


}