package com.esferalia.aon.payroll.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.esferalia.aon.payroll.Empleado;
import com.esferalia.aon.payroll.Empresa;
import com.esferalia.aon.payroll.Nomina;
import com.esferalia.aon.payroll.Persona;
import com.esferalia.aon.payroll.ParteIT;
import com.esferalia.aon.payroll.ConfirmacionParteIT;
import com.esferalia.aon.payroll.Contrato;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IPayrollAlias {



	/** 
	* DAOConstantsEntry for Empleado entity.
	*/ 
	DAOConstantsEntry EMPLEADO_ENTRY = DAOConstants.getDAOConstant(Empleado.class);

	/** 
	* Alias value: Empleado_id
	* Hibernate value: Empleado.id
	*/
	String  EMPLEADO_ID = EMPLEADO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Empleado_codccc
	* Hibernate value: Empleado.codccc
	*/
	String  EMPLEADO_CODCCC = EMPLEADO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Empleado_fechaFin
	* Hibernate value: Empleado.fechaFin
	*/
	String  EMPLEADO_FECHA_FIN = EMPLEADO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Empleado_fechaInicio
	* Hibernate value: Empleado.fechaInicio
	*/
	String  EMPLEADO_FECHA_INICIO = EMPLEADO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Empleado_mayor65
	* Hibernate value: Empleado.mayor65
	*/
	String  EMPLEADO_MAYOR65 = EMPLEADO_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Empleado_empresa_cdg
	* Hibernate value: Empleado.empresa.cdg
	*/
	String  EMPLEADO_EMPRESA_CDG = EMPLEADO_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Empleado_empresa_name
	* Hibernate value: Empleado.empresa.name
	*/
	String  EMPLEADO_EMPRESA_NAME = EMPLEADO_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Empleado_persona_id
	* Hibernate value: Empleado.persona.id
	*/
	String  EMPLEADO_PERSONA_ID = EMPLEADO_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Empleado_persona_lastName
	* Hibernate value: Empleado.persona.lastName
	*/
	String  EMPLEADO_PERSONA_LAST_NAME = EMPLEADO_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Empleado_persona_name
	* Hibernate value: Empleado.persona.name
	*/
	String  EMPLEADO_PERSONA_NAME = EMPLEADO_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Empleado_persona_numSS
	* Hibernate value: Empleado.persona.numSS
	*/
	String  EMPLEADO_PERSONA_NUM_SS = EMPLEADO_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Empleado_persona_registry_document_value
	* Hibernate value: Empleado.persona.registry.document.value
	*/
	String  EMPLEADO_PERSONA_REGISTRY_DOCUMENT_VALUE = EMPLEADO_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Empleado_persona_surname
	* Hibernate value: Empleado.persona.surname
	*/
	String  EMPLEADO_PERSONA_SURNAME = EMPLEADO_ENTRY.getAliasNames()[12];



	/** 
	* DAOConstantsEntry for Empresa entity.
	*/ 
	DAOConstantsEntry EMPRESA_ENTRY = DAOConstants.getDAOConstant(Empresa.class);

	/** 
	* Alias value: Empresa_cdg
	* Hibernate value: Empresa.cdg
	*/
	String  EMPRESA_CDG = EMPRESA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Empresa_name
	* Hibernate value: Empresa.name
	*/
	String  EMPRESA_NAME = EMPRESA_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Nomina entity.
	*/ 
	DAOConstantsEntry NOMINA_ENTRY = DAOConstants.getDAOConstant(Nomina.class);

	/** 
	* Alias value: Nomina_baseAccidentesTrabajo
	* Hibernate value: Nomina.baseAccidentesTrabajo
	*/
	String  NOMINA_BASE_ACCIDENTES_TRABAJO = NOMINA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Nomina_baseAccidentesTrabajoSinHorasExtras
	* Hibernate value: Nomina.baseAccidentesTrabajoSinHorasExtras
	*/
	String  NOMINA_BASE_ACCIDENTES_TRABAJO_SIN_HORAS_EXTRAS = NOMINA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Nomina_baseContingenciasGenerales
	* Hibernate value: Nomina.baseContingenciasGenerales
	*/
	String  NOMINA_BASE_CONTINGENCIAS_GENERALES = NOMINA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Nomina_baseHorasExtrasEstructurales
	* Hibernate value: Nomina.baseHorasExtrasEstructurales
	*/
	String  NOMINA_BASE_HORAS_EXTRAS_ESTRUCTURALES = NOMINA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Nomina_baseHorasExtrasNoEstructurales
	* Hibernate value: Nomina.baseHorasExtrasNoEstructurales
	*/
	String  NOMINA_BASE_HORAS_EXTRAS_NO_ESTRUCTURALES = NOMINA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Nomina_cdg
	* Hibernate value: Nomina.cdg
	*/
	String  NOMINA_CDG = NOMINA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Nomina_diasNomina
	* Hibernate value: Nomina.diasNomina
	*/
	String  NOMINA_DIAS_NOMINA = NOMINA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Nomina_empleado_id
	* Hibernate value: Nomina.empleado.id
	*/
	String  NOMINA_EMPLEADO_ID = NOMINA_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Nomina_mes
	* Hibernate value: Nomina.mes
	*/
	String  NOMINA_MES = NOMINA_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Nomina_tipoNomina
	* Hibernate value: Nomina.tipoNomina
	*/
	String  NOMINA_TIPO_NOMINA = NOMINA_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Nomina_year
	* Hibernate value: Nomina.year
	*/
	String  NOMINA_YEAR = NOMINA_ENTRY.getAliasNames()[10];



	/** 
	* DAOConstantsEntry for Persona entity.
	*/ 
	DAOConstantsEntry PERSONA_ENTRY = DAOConstants.getDAOConstant(Persona.class);

	/** 
	* Alias value: Persona_id
	* Hibernate value: Persona.id
	*/
	String  PERSONA_ID = PERSONA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Persona_lastName
	* Hibernate value: Persona.lastName
	*/
	String  PERSONA_LAST_NAME = PERSONA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Persona_name
	* Hibernate value: Persona.name
	*/
	String  PERSONA_NAME = PERSONA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Persona_numSS
	* Hibernate value: Persona.numSS
	*/
	String  PERSONA_NUM_SS = PERSONA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Persona_registry_document
	* Hibernate value: Persona.registry.document
	*/
	String  PERSONA_REGISTRY_DOCUMENT = PERSONA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Persona_surname
	* Hibernate value: Persona.surname
	*/
	String  PERSONA_SURNAME = PERSONA_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for ParteIT entity.
	*/ 
	DAOConstantsEntry PARTE_IT_ENTRY = DAOConstants.getDAOConstant(ParteIT.class);

	/** 
	* Alias value: ParteIT_altaProcesadaBD
	* Hibernate value: ParteIT.altaProcesadaBD
	*/
	String  PARTE_IT_ALTA_PROCESADA_BD = PARTE_IT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ParteIT_bajaProcesadaBD
	* Hibernate value: ParteIT.bajaProcesadaBD
	*/
	String  PARTE_IT_BAJA_PROCESADA_BD = PARTE_IT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ParteIT_baseDiariaAccidentesTrabajo
	* Hibernate value: ParteIT.baseDiariaAccidentesTrabajo
	*/
	String  PARTE_IT_BASE_DIARIA_ACCIDENTES_TRABAJO = PARTE_IT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ParteIT_baseDiariaContingenciasComunes
	* Hibernate value: ParteIT.baseDiariaContingenciasComunes
	*/
	String  PARTE_IT_BASE_DIARIA_CONTINGENCIAS_COMUNES = PARTE_IT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ParteIT_baseReguladoraDiaria
	* Hibernate value: ParteIT.baseReguladoraDiaria
	*/
	String  PARTE_IT_BASE_REGULADORA_DIARIA = PARTE_IT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ParteIT_baseRetribucionPeriodoAnterior
	* Hibernate value: ParteIT.baseRetribucionPeriodoAnterior
	*/
	String  PARTE_IT_BASE_RETRIBUCION_PERIODO_ANTERIOR = PARTE_IT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ParteIT_ciasAlta
	* Hibernate value: ParteIT.ciasAlta
	*/
	String  PARTE_IT_CIAS_ALTA = PARTE_IT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ParteIT_ciasBaja
	* Hibernate value: ParteIT.ciasBaja
	*/
	String  PARTE_IT_CIAS_BAJA = PARTE_IT_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ParteIT_diasPeriodoAnterior
	* Hibernate value: ParteIT.diasPeriodoAnterior
	*/
	String  PARTE_IT_DIAS_PERIODO_ANTERIOR = PARTE_IT_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: ParteIT_empleado_id
	* Hibernate value: ParteIT.empleado.id
	*/
	String  PARTE_IT_EMPLEADO_ID = PARTE_IT_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: ParteIT_fechaAlta
	* Hibernate value: ParteIT.fechaAlta
	*/
	String  PARTE_IT_FECHA_ALTA = PARTE_IT_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: ParteIT_id_cdg
	* Hibernate value: ParteIT.id.cdg
	*/
	String  PARTE_IT_ID_CDG = PARTE_IT_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: ParteIT_id_fechaBaja
	* Hibernate value: ParteIT.id.fechaBaja
	*/
	String  PARTE_IT_ID_FECHA_BAJA = PARTE_IT_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: ParteIT_numeroColegiadoAlta
	* Hibernate value: ParteIT.numeroColegiadoAlta
	*/
	String  PARTE_IT_NUMERO_COLEGIADO_ALTA = PARTE_IT_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: ParteIT_numeroColegiadoBaja
	* Hibernate value: ParteIT.numeroColegiadoBaja
	*/
	String  PARTE_IT_NUMERO_COLEGIADO_BAJA = PARTE_IT_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: ParteIT_prestacionDiaria60
	* Hibernate value: ParteIT.prestacionDiaria60
	*/
	String  PARTE_IT_PRESTACION_DIARIA60 = PARTE_IT_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: ParteIT_prestacionDiaria75
	* Hibernate value: ParteIT.prestacionDiaria75
	*/
	String  PARTE_IT_PRESTACION_DIARIA75 = PARTE_IT_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: ParteIT_procesadaBD
	* Hibernate value: ParteIT.procesadaBD
	*/
	String  PARTE_IT_PROCESADA_BD = PARTE_IT_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: ParteIT_prorrateo
	* Hibernate value: ParteIT.prorrateo
	*/
	String  PARTE_IT_PRORRATEO = PARTE_IT_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: ParteIT_recaidaBD
	* Hibernate value: ParteIT.recaidaBD
	*/
	String  PARTE_IT_RECAIDA_BD = PARTE_IT_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: ParteIT_riesgo
	* Hibernate value: ParteIT.riesgo
	*/
	String  PARTE_IT_RIESGO = PARTE_IT_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: ParteIT_tipoIT
	* Hibernate value: ParteIT.tipoIT
	*/
	String  PARTE_IT_TIPO_IT = PARTE_IT_ENTRY.getAliasNames()[21];



	/** 
	* DAOConstantsEntry for ConfirmacionParteIT entity.
	*/ 
	DAOConstantsEntry CONFIRMACION_PARTE_IT_ENTRY = DAOConstants.getDAOConstant(ConfirmacionParteIT.class);

	/** 
	* Alias value: ConfirmacionParteIT_cias
	* Hibernate value: ConfirmacionParteIT.cias
	*/
	String  CONFIRMACION_PARTE_IT_CIAS = CONFIRMACION_PARTE_IT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ConfirmacionParteIT_fecha
	* Hibernate value: ConfirmacionParteIT.fecha
	*/
	String  CONFIRMACION_PARTE_IT_FECHA = CONFIRMACION_PARTE_IT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ConfirmacionParteIT_id_cdg
	* Hibernate value: ConfirmacionParteIT.id.cdg
	*/
	String  CONFIRMACION_PARTE_IT_ID_CDG = CONFIRMACION_PARTE_IT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ConfirmacionParteIT_id_fechaBaja
	* Hibernate value: ConfirmacionParteIT.id.fechaBaja
	*/
	String  CONFIRMACION_PARTE_IT_ID_FECHA_BAJA = CONFIRMACION_PARTE_IT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ConfirmacionParteIT_id_numero
	* Hibernate value: ConfirmacionParteIT.id.numero
	*/
	String  CONFIRMACION_PARTE_IT_ID_NUMERO = CONFIRMACION_PARTE_IT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ConfirmacionParteIT_numeroColegiado
	* Hibernate value: ConfirmacionParteIT.numeroColegiado
	*/
	String  CONFIRMACION_PARTE_IT_NUMERO_COLEGIADO = CONFIRMACION_PARTE_IT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ConfirmacionParteIT_parteIT_id_cdg
	* Hibernate value: ConfirmacionParteIT.parteIT.id.cdg
	*/
	String  CONFIRMACION_PARTE_IT_PARTE_IT_ID_CDG = CONFIRMACION_PARTE_IT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ConfirmacionParteIT_parteIT_id_fechaBaja
	* Hibernate value: ConfirmacionParteIT.parteIT.id.fechaBaja
	*/
	String  CONFIRMACION_PARTE_IT_PARTE_IT_ID_FECHA_BAJA = CONFIRMACION_PARTE_IT_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ConfirmacionParteIT_procesadoBD
	* Hibernate value: ConfirmacionParteIT.procesadoBD
	*/
	String  CONFIRMACION_PARTE_IT_PROCESADO_BD = CONFIRMACION_PARTE_IT_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for Contrato entity.
	*/ 
	DAOConstantsEntry CONTRATO_ENTRY = DAOConstants.getDAOConstant(Contrato.class);

	/** 
	* Alias value: Contrato_empleado_id
	* Hibernate value: Contrato.empleado.id
	*/
	String  CONTRATO_EMPLEADO_ID = CONTRATO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Contrato_fechaFin
	* Hibernate value: Contrato.fechaFin
	*/
	String  CONTRATO_FECHA_FIN = CONTRATO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Contrato_id_cdg
	* Hibernate value: Contrato.id.cdg
	*/
	String  CONTRATO_ID_CDG = CONTRATO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Contrato_id_fecini
	* Hibernate value: Contrato.id.fecini
	*/
	String  CONTRATO_ID_FECINI = CONTRATO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Contrato_indtp
	* Hibernate value: Contrato.indtp
	*/
	String  CONTRATO_INDTP = CONTRATO_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Contrato_prorrateo
	* Hibernate value: Contrato.prorrateo
	*/
	String  CONTRATO_PRORRATEO = CONTRATO_ENTRY.getAliasNames()[5];


}