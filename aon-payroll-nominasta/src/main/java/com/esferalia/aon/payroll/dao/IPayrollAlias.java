package com.esferalia.aon.payroll.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.esferalia.aon.payroll.Actividad;
import com.esferalia.aon.payroll.ActividadCCC;
import com.esferalia.aon.payroll.cotizacion.Bonificacion;
import com.esferalia.aon.payroll.Contrato;
import com.esferalia.aon.payroll.Empleado;
import com.esferalia.aon.payroll.Empresa;
import com.esferalia.aon.payroll.Nomina;
import com.esferalia.aon.payroll.ParteConfirmacionIT;
import com.esferalia.aon.payroll.ParteIT;
import com.esferalia.aon.payroll.Persona;
import com.esferalia.aon.payroll.cotizacion.TipoBonificacion;
import com.esferalia.aon.payroll.Usuario;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IPayrollAlias {



	/** 
	* DAOConstantsEntry for Actividad entity.
	*/ 
	DAOConstantsEntry ACTIVIDAD_ENTRY = DAOConstants.getDAOConstant(Actividad.class);

	/** 
	* Alias value: Actividad_alias
	* Hibernate value: Actividad.alias
	*/
	String  ACTIVIDAD_ALIAS = ACTIVIDAD_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Actividad_empresa_id
	* Hibernate value: Actividad.empresa.id
	*/
	String  ACTIVIDAD_EMPRESA_ID = ACTIVIDAD_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Actividad_fecfin
	* Hibernate value: Actividad.fecfin
	*/
	String  ACTIVIDAD_FECFIN = ACTIVIDAD_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Actividad_fecini
	* Hibernate value: Actividad.fecini
	*/
	String  ACTIVIDAD_FECINI = ACTIVIDAD_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Actividad_id
	* Hibernate value: Actividad.id
	*/
	String  ACTIVIDAD_ID = ACTIVIDAD_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Actividad_indregimen
	* Hibernate value: Actividad.indregimen
	*/
	String  ACTIVIDAD_INDREGIMEN = ACTIVIDAD_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Actividad_name
	* Hibernate value: Actividad.name
	*/
	String  ACTIVIDAD_NAME = ACTIVIDAD_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for ActividadCCC entity.
	*/ 
	DAOConstantsEntry ACTIVIDAD_CCC_ENTRY = DAOConstants.getDAOConstant(ActividadCCC.class);

	/** 
	* Alias value: ActividadCCC_actividad_id
	* Hibernate value: ActividadCCC.actividad.id
	*/
	String  ACTIVIDAD_CCC_ACTIVIDAD_ID = ACTIVIDAD_CCC_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ActividadCCC_descripcion
	* Hibernate value: ActividadCCC.descripcion
	*/
	String  ACTIVIDAD_CCC_DESCRIPCION = ACTIVIDAD_CCC_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ActividadCCC_id_cdg
	* Hibernate value: ActividadCCC.id.cdg
	*/
	String  ACTIVIDAD_CCC_ID_CDG = ACTIVIDAD_CCC_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ActividadCCC_id_tipccc
	* Hibernate value: ActividadCCC.id.tipccc
	*/
	String  ACTIVIDAD_CCC_ID_TIPCCC = ACTIVIDAD_CCC_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Bonificacion entity.
	*/ 
	DAOConstantsEntry BONIFICACION_ENTRY = DAOConstants.getDAOConstant(Bonificacion.class);

	/** 
	* Alias value: Bonificacion_empleado_id
	* Hibernate value: Bonificacion.empleado.id
	*/
	String  BONIFICACION_EMPLEADO_ID = BONIFICACION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Bonificacion_fechaFin
	* Hibernate value: Bonificacion.fechaFin
	*/
	String  BONIFICACION_FECHA_FIN = BONIFICACION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Bonificacion_horas
	* Hibernate value: Bonificacion.horas
	*/
	String  BONIFICACION_HORAS = BONIFICACION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Bonificacion_id_cdg
	* Hibernate value: Bonificacion.id.cdg
	*/
	String  BONIFICACION_ID_CDG = BONIFICACION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Bonificacion_id_fechaInicio
	* Hibernate value: Bonificacion.id.fechaInicio
	*/
	String  BONIFICACION_ID_FECHA_INICIO = BONIFICACION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Bonificacion_id_numero
	* Hibernate value: Bonificacion.id.numero
	*/
	String  BONIFICACION_ID_NUMERO = BONIFICACION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Bonificacion_importe
	* Hibernate value: Bonificacion.importe
	*/
	String  BONIFICACION_IMPORTE = BONIFICACION_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Bonificacion_tipoBonificacion_id
	* Hibernate value: Bonificacion.tipoBonificacion.id
	*/
	String  BONIFICACION_TIPO_BONIFICACION_ID = BONIFICACION_ENTRY.getAliasNames()[7];



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
	* Alias value: Empleado_actividad_cdg
	* Hibernate value: Empleado.actividad.cdg
	*/
	String  EMPLEADO_ACTIVIDAD_CDG = EMPLEADO_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Empleado_actividad_name
	* Hibernate value: Empleado.actividad.name
	*/
	String  EMPLEADO_ACTIVIDAD_NAME = EMPLEADO_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Empleado_actividad_empresa_cdg
	* Hibernate value: Empleado.actividad.empresa.cdg
	*/
	String  EMPLEADO_ACTIVIDAD_EMPRESA_CDG = EMPLEADO_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Empleado_actividad_empresa_name
	* Hibernate value: Empleado.actividad.empresa.name
	*/
	String  EMPLEADO_ACTIVIDAD_EMPRESA_NAME = EMPLEADO_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Empleado_persona_id
	* Hibernate value: Empleado.persona.id
	*/
	String  EMPLEADO_PERSONA_ID = EMPLEADO_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Empleado_persona_lastName
	* Hibernate value: Empleado.persona.lastName
	*/
	String  EMPLEADO_PERSONA_LAST_NAME = EMPLEADO_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Empleado_persona_name
	* Hibernate value: Empleado.persona.name
	*/
	String  EMPLEADO_PERSONA_NAME = EMPLEADO_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Empleado_persona_numSS
	* Hibernate value: Empleado.persona.numSS
	*/
	String  EMPLEADO_PERSONA_NUM_SS = EMPLEADO_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Empleado_persona_registry_document_tipo
	* Hibernate value: Empleado.persona.registry.document.tipo
	*/
	String  EMPLEADO_PERSONA_REGISTRY_DOCUMENT_TIPO = EMPLEADO_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Empleado_persona_registry_document_pais
	* Hibernate value: Empleado.persona.registry.document.pais
	*/
	String  EMPLEADO_PERSONA_REGISTRY_DOCUMENT_PAIS = EMPLEADO_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Empleado_persona_registry_document_value
	* Hibernate value: Empleado.persona.registry.document.value
	*/
	String  EMPLEADO_PERSONA_REGISTRY_DOCUMENT_VALUE = EMPLEADO_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Empleado_persona_surname
	* Hibernate value: Empleado.persona.surname
	*/
	String  EMPLEADO_PERSONA_SURNAME = EMPLEADO_ENTRY.getAliasNames()[16];



	/** 
	* DAOConstantsEntry for Empresa entity.
	*/ 
	DAOConstantsEntry EMPRESA_ENTRY = DAOConstants.getDAOConstant(Empresa.class);

	/** 
	* Alias value: Empresa_id
	* Hibernate value: Empresa.id
	*/
	String  EMPRESA_ID = EMPRESA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Empresa_registry_document_tipo
	* Hibernate value: Empresa.registry.document.tipo
	*/
	String  EMPRESA_REGISTRY_DOCUMENT_TIPO = EMPRESA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Empresa_registry_document_pais
	* Hibernate value: Empresa.registry.document.pais
	*/
	String  EMPRESA_REGISTRY_DOCUMENT_PAIS = EMPRESA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Empresa_registry_document_value
	* Hibernate value: Empresa.registry.document.value
	*/
	String  EMPRESA_REGISTRY_DOCUMENT_VALUE = EMPRESA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Empresa_descripcion
	* Hibernate value: Empresa.descripcion
	*/
	String  EMPRESA_DESCRIPCION = EMPRESA_ENTRY.getAliasNames()[4];



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
	* DAOConstantsEntry for ParteConfirmacionIT entity.
	*/ 
	DAOConstantsEntry PARTE_CONFIRMACION_IT_ENTRY = DAOConstants.getDAOConstant(ParteConfirmacionIT.class);

	/** 
	* Alias value: ParteConfirmacionIT_id_cdg
	* Hibernate value: ParteConfirmacionIT.id.cdg
	*/
	String  PARTE_CONFIRMACION_IT_ID_CDG = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ParteConfirmacionIT_id_fechaBaja
	* Hibernate value: ParteConfirmacionIT.id.fechaBaja
	*/
	String  PARTE_CONFIRMACION_IT_ID_FECHA_BAJA = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ParteConfirmacionIT_id_numero
	* Hibernate value: ParteConfirmacionIT.id.numero
	*/
	String  PARTE_CONFIRMACION_IT_ID_NUMERO = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ParteConfirmacionIT_numeroColegiado
	* Hibernate value: ParteConfirmacionIT.numeroColegiado
	*/
	String  PARTE_CONFIRMACION_IT_NUMERO_COLEGIADO = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ParteConfirmacionIT_cias
	* Hibernate value: ParteConfirmacionIT.cias
	*/
	String  PARTE_CONFIRMACION_IT_CIAS = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ParteConfirmacionIT_fecha
	* Hibernate value: ParteConfirmacionIT.fecha
	*/
	String  PARTE_CONFIRMACION_IT_FECHA = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ParteConfirmacionIT_procesadoBD
	* Hibernate value: ParteConfirmacionIT.procesadoBD
	*/
	String  PARTE_CONFIRMACION_IT_PROCESADO_BD = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ParteConfirmacionIT_parteIT_id_cdg
	* Hibernate value: ParteConfirmacionIT.parteIT.id_cdg
	*/
	String  PARTE_CONFIRMACION_IT_PARTE_IT_ID_CDG = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ParteConfirmacionIT_parteIT_id_fechaBaja
	* Hibernate value: ParteConfirmacionIT.parteIT.id_fechaBaja
	*/
	String  PARTE_CONFIRMACION_IT_PARTE_IT_ID_FECHA_BAJA = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: ParteConfirmacionIT_parteIT_empleado_id
	* Hibernate value: ParteConfirmacionIT.parteIT.empleado.id
	*/
	String  PARTE_CONFIRMACION_IT_PARTE_IT_EMPLEADO_ID = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: ParteConfirmacionIT_parteIT_empleado_actividad_cdg
	* Hibernate value: ParteConfirmacionIT.parteIT.empleado.actividad.cdg
	*/
	String  PARTE_CONFIRMACION_IT_PARTE_IT_EMPLEADO_ACTIVIDAD_CDG = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: ParteConfirmacionIT_parteIT_empleado_actividad_name
	* Hibernate value: ParteConfirmacionIT.parteIT.empleado.actividad.name
	*/
	String  PARTE_CONFIRMACION_IT_PARTE_IT_EMPLEADO_ACTIVIDAD_NAME = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: ParteConfirmacionIT_parteIT_empleado_actividad_empresa_id
	* Hibernate value: ParteConfirmacionIT.parteIT.empleado.actividad.empresa.id
	*/
	String  PARTE_CONFIRMACION_IT_PARTE_IT_EMPLEADO_ACTIVIDAD_EMPRESA_ID = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: ParteConfirmacionIT_parteIT_empleado_actividad_empresa_name
	* Hibernate value: ParteConfirmacionIT.parteIT.empleado.actividad.empresa.name
	*/
	String  PARTE_CONFIRMACION_IT_PARTE_IT_EMPLEADO_ACTIVIDAD_EMPRESA_NAME = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: ParteConfirmacionIT_parteIT_empleado_persona_id
	* Hibernate value: ParteConfirmacionIT.parteIT.empleado.persona.id
	*/
	String  PARTE_CONFIRMACION_IT_PARTE_IT_EMPLEADO_PERSONA_ID = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: ParteConfirmacionIT_parteIT_empleado_persona_registry_document_tipo
	* Hibernate value: ParteConfirmacionIT.parteIT.empleado.persona.registry.document.tipo
	*/
	String  PARTE_CONFIRMACION_IT_PARTE_IT_EMPLEADO_PERSONA_REGISTRY_DOCUMENT_TIPO = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: ParteConfirmacionIT_parteIT_empleado_persona_registry_document_pais
	* Hibernate value: ParteConfirmacionIT.parteIT.empleado.persona.registry.document.pais
	*/
	String  PARTE_CONFIRMACION_IT_PARTE_IT_EMPLEADO_PERSONA_REGISTRY_DOCUMENT_PAIS = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: ParteConfirmacionIT_parteIT_empleado_persona_registry_document_value
	* Hibernate value: ParteConfirmacionIT.parteIT.empleado.persona.registry.document.value
	*/
	String  PARTE_CONFIRMACION_IT_PARTE_IT_EMPLEADO_PERSONA_REGISTRY_DOCUMENT_VALUE = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: ParteConfirmacionIT_parteIT_empleado_persona_name
	* Hibernate value: ParteConfirmacionIT.parteIT.empleado.persona.name
	*/
	String  PARTE_CONFIRMACION_IT_PARTE_IT_EMPLEADO_PERSONA_NAME = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: ParteConfirmacionIT_parteIT_empleado_persona_lastName
	* Hibernate value: ParteConfirmacionIT.parteIT.empleado.persona.lastName
	*/
	String  PARTE_CONFIRMACION_IT_PARTE_IT_EMPLEADO_PERSONA_LAST_NAME = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: ParteConfirmacionIT_parteIT_empleado_persona_surname
	* Hibernate value: ParteConfirmacionIT.parteIT.empleado.persona.surname
	*/
	String  PARTE_CONFIRMACION_IT_PARTE_IT_EMPLEADO_PERSONA_SURNAME = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: ParteConfirmacionIT_parteIT_empleado_persona_numSS
	* Hibernate value: ParteConfirmacionIT.parteIT.empleado.persona.numSS
	*/
	String  PARTE_CONFIRMACION_IT_PARTE_IT_EMPLEADO_PERSONA_NUM_SS = PARTE_CONFIRMACION_IT_ENTRY.getAliasNames()[21];



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
	* Alias value: ParteIT_empleado_actividad_cdg
	* Hibernate value: ParteIT.empleado.actividad.cdg
	*/
	String  PARTE_IT_EMPLEADO_ACTIVIDAD_CDG = PARTE_IT_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: ParteIT_empleado_actividad_name
	* Hibernate value: ParteIT.empleado.actividad.name
	*/
	String  PARTE_IT_EMPLEADO_ACTIVIDAD_NAME = PARTE_IT_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: ParteIT_empleado_actividad_empresa_id
	* Hibernate value: ParteIT.empleado.actividad.empresa.id
	*/
	String  PARTE_IT_EMPLEADO_ACTIVIDAD_EMPRESA_ID = PARTE_IT_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: ParteIT_empleado_actividad_empresa_name
	* Hibernate value: ParteIT.empleado.actividad.empresa.name
	*/
	String  PARTE_IT_EMPLEADO_ACTIVIDAD_EMPRESA_NAME = PARTE_IT_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: ParteIT_empleado_persona_id
	* Hibernate value: ParteIT.empleado.persona.id
	*/
	String  PARTE_IT_EMPLEADO_PERSONA_ID = PARTE_IT_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: ParteIT_empleado_persona_registry_document_tipo
	* Hibernate value: ParteIT.empleado.persona.registry.document.tipo
	*/
	String  PARTE_IT_EMPLEADO_PERSONA_REGISTRY_DOCUMENT_TIPO = PARTE_IT_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: ParteIT_empleado_persona_registry_document_pais
	* Hibernate value: ParteIT.empleado.persona.registry.document.pais
	*/
	String  PARTE_IT_EMPLEADO_PERSONA_REGISTRY_DOCUMENT_PAIS = PARTE_IT_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: ParteIT_empleado_persona_registry_document_value
	* Hibernate value: ParteIT.empleado.persona.registry.document.value
	*/
	String  PARTE_IT_EMPLEADO_PERSONA_REGISTRY_DOCUMENT_VALUE = PARTE_IT_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: ParteIT_empleado_persona_name
	* Hibernate value: ParteIT.empleado.persona.name
	*/
	String  PARTE_IT_EMPLEADO_PERSONA_NAME = PARTE_IT_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: ParteIT_empleado_persona_lastName
	* Hibernate value: ParteIT.empleado.persona.lastName
	*/
	String  PARTE_IT_EMPLEADO_PERSONA_LAST_NAME = PARTE_IT_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: ParteIT_empleado_persona_surname
	* Hibernate value: ParteIT.empleado.persona.surname
	*/
	String  PARTE_IT_EMPLEADO_PERSONA_SURNAME = PARTE_IT_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: ParteIT_empleado_persona_numSS
	* Hibernate value: ParteIT.empleado.persona.numSS
	*/
	String  PARTE_IT_EMPLEADO_PERSONA_NUM_SS = PARTE_IT_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: ParteIT_fechaAlta
	* Hibernate value: ParteIT.fechaAlta
	*/
	String  PARTE_IT_FECHA_ALTA = PARTE_IT_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: ParteIT_id_cdg
	* Hibernate value: ParteIT.id.cdg
	*/
	String  PARTE_IT_ID_CDG = PARTE_IT_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: ParteIT_id_fechaBaja
	* Hibernate value: ParteIT.id.fechaBaja
	*/
	String  PARTE_IT_ID_FECHA_BAJA = PARTE_IT_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: ParteIT_numeroColegiadoAlta
	* Hibernate value: ParteIT.numeroColegiadoAlta
	*/
	String  PARTE_IT_NUMERO_COLEGIADO_ALTA = PARTE_IT_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: ParteIT_numeroColegiadoBaja
	* Hibernate value: ParteIT.numeroColegiadoBaja
	*/
	String  PARTE_IT_NUMERO_COLEGIADO_BAJA = PARTE_IT_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: ParteIT_prestacionDiaria60
	* Hibernate value: ParteIT.prestacionDiaria60
	*/
	String  PARTE_IT_PRESTACION_DIARIA60 = PARTE_IT_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: ParteIT_prestacionDiaria75
	* Hibernate value: ParteIT.prestacionDiaria75
	*/
	String  PARTE_IT_PRESTACION_DIARIA75 = PARTE_IT_ENTRY.getAliasNames()[28];

	/** 
	* Alias value: ParteIT_procesadaBD
	* Hibernate value: ParteIT.procesadaBD
	*/
	String  PARTE_IT_PROCESADA_BD = PARTE_IT_ENTRY.getAliasNames()[29];

	/** 
	* Alias value: ParteIT_prorrateo
	* Hibernate value: ParteIT.prorrateo
	*/
	String  PARTE_IT_PRORRATEO = PARTE_IT_ENTRY.getAliasNames()[30];

	/** 
	* Alias value: ParteIT_recaidaBD
	* Hibernate value: ParteIT.recaidaBD
	*/
	String  PARTE_IT_RECAIDA_BD = PARTE_IT_ENTRY.getAliasNames()[31];

	/** 
	* Alias value: ParteIT_riesgo
	* Hibernate value: ParteIT.riesgo
	*/
	String  PARTE_IT_RIESGO = PARTE_IT_ENTRY.getAliasNames()[32];

	/** 
	* Alias value: ParteIT_tipoIT
	* Hibernate value: ParteIT.tipoIT
	*/
	String  PARTE_IT_TIPO_IT = PARTE_IT_ENTRY.getAliasNames()[33];



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
	* Alias value: Persona_registry_document_tipo
	* Hibernate value: Persona.registry.document.tipo
	*/
	String  PERSONA_REGISTRY_DOCUMENT_TIPO = PERSONA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Persona_registry_document_pais
	* Hibernate value: Persona.registry.document.pais
	*/
	String  PERSONA_REGISTRY_DOCUMENT_PAIS = PERSONA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Persona_registry_document_value
	* Hibernate value: Persona.registry.document.value
	*/
	String  PERSONA_REGISTRY_DOCUMENT_VALUE = PERSONA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Persona_name
	* Hibernate value: Persona.name
	*/
	String  PERSONA_NAME = PERSONA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Persona_surname
	* Hibernate value: Persona.surname
	*/
	String  PERSONA_SURNAME = PERSONA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Persona_lastName
	* Hibernate value: Persona.lastName
	*/
	String  PERSONA_LAST_NAME = PERSONA_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for TipoBonificacion entity.
	*/ 
	DAOConstantsEntry TIPO_BONIFICACION_ENTRY = DAOConstants.getDAOConstant(TipoBonificacion.class);

	/** 
	* Alias value: TipoBonificacion_descripcion
	* Hibernate value: TipoBonificacion.descripcion
	*/
	String  TIPO_BONIFICACION_DESCRIPCION = TIPO_BONIFICACION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: TipoBonificacion_id
	* Hibernate value: TipoBonificacion.id
	*/
	String  TIPO_BONIFICACION_ID = TIPO_BONIFICACION_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Usuario entity.
	*/ 
	DAOConstantsEntry USUARIO_ENTRY = DAOConstants.getDAOConstant(Usuario.class);

	/** 
	* Alias value: Usuario_autorizacion
	* Hibernate value: Usuario.autorizacion
	*/
	String  USUARIO_AUTORIZACION = USUARIO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Usuario_login
	* Hibernate value: Usuario.login
	*/
	String  USUARIO_LOGIN = USUARIO_ENTRY.getAliasNames()[1];


}