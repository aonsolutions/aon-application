package com.code.aon.payroll.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.payroll.cotizacion.PorcentajeMaestro;
import com.code.aon.payroll.cotizacion.Porcentaje;
import com.code.aon.payroll.tipos.Documento;
import com.code.aon.payroll.tipos.Autorizacion;
import com.code.aon.payroll.tipos.Incidencia;
import com.code.aon.payroll.tipos.Registro;
import com.code.aon.payroll.cotizacion.Bonificacion;

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


}