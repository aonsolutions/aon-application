package com.esferalia.aon.gwt.payroll.client.i18n;

import com.google.gwt.i18n.client.Messages;

/**
 * GWT Messages interface for internationalizing UIBinder templates in aon-gwt-payroll.
 * Default messages are in Spanish (es_ES).
 * <p>
 * Unicode reference: Á=Á á=á É=É é=é Í=Í í=í
 *                    Ó=Ó ó=ó Ú=Ú ú=ú Ñ=Ñ ñ=ñ
 *                    ¿=¿ ¡=¡ º=º ª=ª ü=ü
 */
public interface PayrollMessages extends Messages {

	// ----------------------------------------------------------------- Common actions

	@DefaultMessage("Aceptar")
	String accept();

	@DefaultMessage("Cancelar")
	String cancel();

	@DefaultMessage("Guardar")
	String save();

	@DefaultMessage("GUARDAR")
	String saveAction();

	@DefaultMessage("Borrar")
	String delete();

	@DefaultMessage("BORRAR")
	String deleteAction();

	@DefaultMessage("OK")
	String ok();

	@DefaultMessage("Nuevo")
	String newAction();

	@DefaultMessage("Copiar")
	String copy();

	@DefaultMessage("Seleccionar")
	String select();

	@DefaultMessage("Descargar")
	String download();

	@DefaultMessage("Imprimir")
	String print();

	@DefaultMessage("Imprimir...")
	String printDots();

	@DefaultMessage("Publicar")
	String publish();

	@DefaultMessage("Publicar...")
	String publishDots();

	@DefaultMessage("Ver")
	String view();

	@DefaultMessage("Reducir")
	String reduce();

	@DefaultMessage("Ampliar")
	String enlarge();

	@DefaultMessage("Cerrar")
	String close();

	@DefaultMessage("Limpiar")
	String clear();

	@DefaultMessage("Actualizar")
	String refresh();

	@DefaultMessage("Calcular")
	String calculate();

	@DefaultMessage("RESETEAR")
	String reset();

	@DefaultMessage("Edición")
	String edition();

	@DefaultMessage("Avanzado")
	String advanced();

	@DefaultMessage("IMPRIMIR")
	String printUpperCase();

	// ----------------------------------------------------------------- Menu items

	@DefaultMessage("Archivo")
	String fileMenu();

	@DefaultMessage("Editor")
	String editor();

	@DefaultMessage("Excel")
	String excel();

	@DefaultMessage("PDF")
	String pdf();

	// ----------------------------------------------------------------- Common fields

	@DefaultMessage("Empresa")
	String enterprise();

	@DefaultMessage("Convenio")
	String agreement();

	@DefaultMessage("Tipo")
	String type();

	@DefaultMessage("Concepto")
	String concept();

	@DefaultMessage("Descripción")
	String description();

	@DefaultMessage("Expresión")
	String expression();

	@DefaultMessage("Código")
	String code();

	@DefaultMessage("Mes")
	String month();

	@DefaultMessage("Fecha")
	String date();

	@DefaultMessage("Fecha inicio")
	String startDate();

	@DefaultMessage("Fecha fin")
	String endDate();

	@DefaultMessage("Desde")
	String from();

	@DefaultMessage("Hasta")
	String until();

	@DefaultMessage("Nombre")
	String name();

	@DefaultMessage("Documento")
	String document();

	@DefaultMessage("IBAN")
	String iban();

	@DefaultMessage("BIC")
	String bic();

	@DefaultMessage("Importe")
	String amount();

	@DefaultMessage("Recibo")
	String receipt();

	@DefaultMessage("Periodo")
	String period();

	@DefaultMessage("Todos")
	String all();

	@DefaultMessage("Provincia")
	String province();

	@DefaultMessage("Municipio")
	String municipality();

	@DefaultMessage("Dirección")
	String address();

	@DefaultMessage("Nº")
	String number();

	@DefaultMessage("Rest. Dir")
	String addressRest();

	@DefaultMessage("C.P.")
	String zipCode();

	@DefaultMessage("Nacionalidad")
	String nationality();

	@DefaultMessage("Teléfono")
	String phone();

	@DefaultMessage("Móvil")
	String mobile();

	@DefaultMessage("eMail")
	String email();

	@DefaultMessage("Contacto")
	String contact();

	@DefaultMessage("Sexo")
	String gender();

	@DefaultMessage("Estado Civil")
	String civilStatus();

	@DefaultMessage("Fecha de nacimiento")
	String birthDate();

	@DefaultMessage("Nº S.S.")
	String socialSecurityNumber();

	@DefaultMessage("1er Apellido")
	String firstSurname();

	@DefaultMessage("2º Apellido")
	String secondSurname();

	@DefaultMessage("Nóminas")
	String payrolls();

	@DefaultMessage("Actividad")
	String activity();

	@DefaultMessage("Control")
	String control();

	@DefaultMessage("Resultado")
	String result();

	@DefaultMessage("Resultados")
	String results();

	@DefaultMessage("Devengo")
	String accrual();

	@DefaultMessage("Devengos")
	String accruals();

	@DefaultMessage("Deducciones")
	String deductions();

	@DefaultMessage("Bonificaciones")
	String bonuses();

	@DefaultMessage("Calendario")
	String calendar();

	@DefaultMessage("IRPF")
	String irpf();

	@DefaultMessage("Pago")
	String payment();

	@DefaultMessage("Extra")
	String extra();

	// ----------------------------------------------------------------- Email fields

	@DefaultMessage("De:")
	String emailFrom();

	@DefaultMessage("Para:")
	String emailTo();

	@DefaultMessage("CC:")
	String cc();

	@DefaultMessage("CCO:")
	String bcc();

	@DefaultMessage("Asunto:")
	String subject();

	// ----------------------------------------------------------------- Employee data

	@DefaultMessage("Datos Contrato")
	String contractData();

	@DefaultMessage("Datos Afiliación")
	String affiliationData();

	@DefaultMessage("Datos SEPE")
	String sepeData();

	@DefaultMessage("Datos Personales")
	String personalData();

	@DefaultMessage("Clausulas")
	String clauses();

	@DefaultMessage("Documentos")
	String documents();

	@DefaultMessage("Mod 145")
	String mod145();

	@DefaultMessage("Tipo de cotización")
	String quoteType();

	@DefaultMessage("Tipo Tributación")
	String tributationType();

	@DefaultMessage("Actividad - Cuenta de cotización")
	String activityQuoteAccount();

	@DefaultMessage("Modalidad Cotización")
	String quoteModality();

	@DefaultMessage("Tipo de contrato")
	String contractType();

	@DefaultMessage("Régimen especial de trabajadores autónomos")
	String freelanceRegime();

	@DefaultMessage("Modalidad")
	String modality();

	@DefaultMessage("Periodo contratación")
	String contractPeriod();

	@DefaultMessage("Fecha antigüedad")
	String seniorityDate();

	@DefaultMessage("Categoría/Nivel retributario")
	String categoryLevel();

	@DefaultMessage("Nivel/Categoría")
	String levelCategory();

	@DefaultMessage("Mostrar como")
	String showAs();

	@DefaultMessage("Grupo de cotización")
	String quoteGroup();

	@DefaultMessage("I. Cotización mensual")
	String monthlyQuote();

	@DefaultMessage("Ocupación")
	String occupation();

	@DefaultMessage("RLCE")
	String rlce();

	@DefaultMessage("Colectivo Trabajadores")
	String employeeCollective();

	@DefaultMessage("Tipo Jornada")
	String journeyType();

	@DefaultMessage("Duración Jornada")
	String journeyDuration();

	@DefaultMessage("Coef. Parcialidad:")
	String partialityCoef();

	@DefaultMessage("Coef. Parcialidad: ")
	String partialityCoefSpace();

	@DefaultMessage("Coeficiente Parcialidad")
	String partialityCoefficient();

	@DefaultMessage("CNO")
	String cno();

	@DefaultMessage("Código de baja")
	String settleReasonCode();

	@DefaultMessage("Fecha fin vacaciones")
	String holidaysEndDate();

	@DefaultMessage("Tipo vacaciones")
	String holidaysType();

	@DefaultMessage("Generar fichero AFI")
	String generateAfiFile();

	@DefaultMessage("Forma de pago")
	String paymentMethod();

	@DefaultMessage("Grupo Cotiz.: ")
	String quotationGroup();

	// ----------------------------------------------------------------- Agreement / Payroll

	@DefaultMessage("Todos los CCCs")
	String allCccs();

	@DefaultMessage("P. Liquidación")
	String liquidationPeriod();

	@DefaultMessage("DATOS JORNADA AGRARIA")
	String agrarianDayDataTitle();

	@DefaultMessage("Código SS")
	String ssCode();

	@DefaultMessage("Pagas extras:")
	String extraPayments();

	@DefaultMessage("CERRAR PRELIMINAR")
	String closePreview();

	@DefaultMessage("DEBE CREAR UN NIVEL PARA VISUALIZAR LA TABLA (+)")
	String mustCreateLevelMsg();

	@DefaultMessage("DEBE CREAR UN TRAMO PARA VISUALIZAR LA TABLA SALARIAL (+)")
	String mustCreateSalaryRangeMsg();

	@DefaultMessage("DEBE CREAR UN DEVENGO PARA VISUALIZAR LA TABLA DE DEVENGOS (+)")
	String mustCreateAccrualMsg();

	@DefaultMessage("DEBE CREAR UN DEVENGO PARA VISUALIZAR LA TABLA DE EXTRAS (+)")
	String mustCreateExtraAccrualMsg();

	@DefaultMessage("Seleccione un devengo sugerido para importarlo. O use el modo manual.")
	String selectSuggestedAccrual();

	@DefaultMessage("Tipo de devengo")
	String accrualType();

	@DefaultMessage("Periocidad devengo")
	String accrualFrequency();

	@DefaultMessage("Tipo importe")
	String amountType();

	@DefaultMessage("¿Aplicar parcialidad?")
	String applyPartiality();

	@DefaultMessage("Fecha Cobro")
	String paymentDate();

	@DefaultMessage("Fecha Incio Cotización")
	String quotationStartDate();

	@DefaultMessage("Fecha Fin Cotización")
	String quotationEndDate();

	@DefaultMessage("Tipo Nómina/Recibo")
	String salaryReceiptType();

	@DefaultMessage("Tributa")
	String taxable();

	@DefaultMessage("Ingreso a cuenta empresa")
	String companyAccountIncome();

	@DefaultMessage("Cotiza")
	String contributory();

	@DefaultMessage("Cálculo Antigüedad")
	String seniorityCalculation();

	@DefaultMessage("El valor seleccionado afectará a todos los cálculos de antigüedad del convenio")
	String seniorityCalculationAffectsAll();

	@DefaultMessage("Mes Cobro")
	String paymentMonth();

	@DefaultMessage("Mes de pago")
	String monthPayment();

	@DefaultMessage("Mes inicio")
	String startMonth();

	@DefaultMessage("Mes fin")
	String endMonth();

	@DefaultMessage("Selecciona los conceptos")
	String selectConcepts();

	@DefaultMessage("Selecciona los conceptos:")
	String selectConceptsColon();

	@DefaultMessage("Tipo CRA")
	String craType();

	@DefaultMessage("Pos")
	String position();

	@DefaultMessage("Autorizado")
	String authorized();

	@DefaultMessage("Tipo Movimiento")
	String movementType();

	@DefaultMessage("Tipo Acción")
	String actionType();

	@DefaultMessage("Titular")
	String holder();

	@DefaultMessage("Tipo Documento")
	String documentType();

	@DefaultMessage("Selecciona los Código de Cuenta de Cotización : ")
	String selectCccCode();

	@DefaultMessage("Fecha de abono de los salarios para liquidaciones L03 y C03.")
	String salaryPaymentDateMsg();

	@DefaultMessage("Tipo de Liquidación")
	String liquidationType();

	@DefaultMessage("Causa, indicador I 54")
	String causeIndicatorI54();

	@DefaultMessage("Aceptar Bases Anteriores")
	String acceptPreviousBases();

	@DefaultMessage("Calculos Desglosados")
	String detailedCalculations();

	@DefaultMessage("Indicador Reftificación")
	String rectificationIndicator();

	@DefaultMessage("Solicitud Recepción RNT")
	String requestRntReception();

	@DefaultMessage("Consultar TGSS IDC")
	String consultTgssIdc();

	@DefaultMessage("Reftificación Fuera de Plazo")
	String rectificationOutOfDeadline();

	@DefaultMessage("Número Liquidación")
	String liquidationNumber();

	@DefaultMessage("Mensajes Recibidos (SILTRA)")
	String receivedMessagesSiltra();

	@DefaultMessage("Trabajadores y Tramos")
	String workersAndRanges();

	@DefaultMessage("Conservar nóminas calculadas")
	String keepCalculatedPayrolls();

	@DefaultMessage("Eliminar nóminas calculadas")
	String deleteCalculatedPayrolls();

	@DefaultMessage("Selecciona los empleados: ")
	String selectEmployees();

	@DefaultMessage("Días en blanco o 0 equivale a no horas complementarias.")
	String blankDaysNoComplementaryHours();

	@DefaultMessage("Mes: ")
	String monthColon();

	// ----------------------------------------------------------------- Salary Draft

	@DefaultMessage("EMITIR NOMINA")
	String issueSalary();

	@DefaultMessage("EMITIR EXTRA")
	String issueExtra();

	@DefaultMessage("EMITIR FINIQUITO")
	String issueSettle();

	@DefaultMessage("EMITIR ATRASOS")
	String issueDelays();

	@DefaultMessage("MODELOS FISCALES")
	String fiscalModels();

	@DefaultMessage("Vista Previa")
	String preview();

	@DefaultMessage("AVISOS")
	String warnings();

	@DefaultMessage("CONCEPTOS NO ACTIVOS")
	String inactivePaymentTypes();

	@DefaultMessage("COSTES")
	String costs();

	@DefaultMessage("VARIABLES NO DEFINIDAS")
	String undefinedVariables();

	@DefaultMessage("DIFERENCIAS")
	String differences();

	@DefaultMessage("SILTRA")
	String siltra();

	@DefaultMessage("ESTÁNDAR")
	String standard();

	@DefaultMessage("CARTA (β)")
	String letter();

	@DefaultMessage("C.C.C")
	String cccHeader();

	@DefaultMessage("GRUPO")
	String group();

	@DefaultMessage("CONTRATO")
	String contract();

	@DefaultMessage("OCUPACIÓN")
	String occupationHeader();

	@DefaultMessage("CATEGORÍA PROFESIONAL")
	String professionalCategory();

	@DefaultMessage("N. AFILIACIÓN")
	String affiliationNumber();

	@DefaultMessage("N. DOCUMENTO")
	String documentNumber();

	@DefaultMessage("PERIODO LIQUIDACIÓN")
	String liquidationPeriodHeader();

	@DefaultMessage("DÍAS")
	String days();

	@DefaultMessage("ANTIGÜEDAD")
	String seniority();

	@DefaultMessage("HORAS TRABAJADAS")
	String workedHours();

	@DefaultMessage("DIAS TRABAJADOS")
	String workedDays();

	@DefaultMessage("COEFICIENTE PARCIALIDAD")
	String partialityCoefficientHeader();

	@DefaultMessage("REMUNERACIÓN TOTAL")
	String totalRemuneration();

	@DefaultMessage("PRORRATA PAGAS")
	String prorrataSalaries();

	@DefaultMessage("BASE TOTAL COTIZ.")
	String totalQuotationBase();

	@DefaultMessage("BASE IRPF")
	String irpfBase();

	@DefaultMessage("BASE H.E.")
	String overtimeBase();

	@DefaultMessage("BASE AT Y EP.")
	String workAccidentBase();

	@DefaultMessage("BASE H.E. NO AUTORIZ.")
	String unauthorizedOvertimeBase();

	@DefaultMessage("A. TOTAL DEVENGO")
	String totalEarnings();

	@DefaultMessage("B. TOTAL DEDUCIR")
	String totalDeductions();

	@DefaultMessage("LIQUIDO A PERCIBIR (A-B)")
	String netToReceive();

	@DefaultMessage("TOTAL ( A + C )")
	String total();

	@DefaultMessage("C. TOTAL EMPRESA")
	String totalCompany();

	@DefaultMessage("Variables del Sistema")
	String systemVariables();

	@DefaultMessage("Variables del Convenio")
	String agreementVariables();

	@DefaultMessage("Variables del Empleado")
	String employeeVariables();

	// ----------------------------------------------------------------- Calendar

	@DefaultMessage("Añadir día(s) no laborables")
	String addNonWorkingDays();

	@DefaultMessage("Añadir día(s) vacaciones")
	String addHolidays();

	@DefaultMessage("Añadir día(s) inactividad")
	String addInactivity();

	@DefaultMessage("Añadir día(s) ausencia")
	String addAbsence();

	@DefaultMessage("Borrar evento(s)")
	String eraseEvents();

	@DefaultMessage("Seleccionar todo")
	String selectAll();

	@DefaultMessage("Seleccionar hasta")
	String selectUntil();

	@DefaultMessage("Seleccionar sábados")
	String selectSaturdays();

	@DefaultMessage("Seleccionar domingos")
	String selectSundays();

	@DefaultMessage("Horas")
	String hours();

	@DefaultMessage("Ocultar horas")
	String hideHours();

	@DefaultMessage("No Laborables")
	String nonWorking();

	@DefaultMessage("Inactividad")
	String inactivity();

	@DefaultMessage("Ausencia")
	String absence();

	@DefaultMessage("Vacaciones")
	String vacations();

	@DefaultMessage("Parcialidad")
	String partiality();

	@DefaultMessage("Peonadas")
	String peonadas();

	@DefaultMessage("H. Extras")
	String extraHours();

	@DefaultMessage("Leyenda")
	String legend();

	@DefaultMessage("Horas Mensuales")
	String monthlyHours();

	@DefaultMessage("Laborables")
	String workingDays();

	@DefaultMessage("Perm. Retribuido")
	String paidLeave();

	@DefaultMessage("Efectivos")
	String effective();

	@DefaultMessage("Baja IT")
	String sickLeave();

	@DefaultMessage("Dias seleccionables:")
	String selectableDays();

	@DefaultMessage("Enero")
	String enero();

	@DefaultMessage("Febrero")
	String febrero();

	@DefaultMessage("Marzo")
	String marzo();

	@DefaultMessage("Abril")
	String abril();

	@DefaultMessage("Mayo")
	String mayo();

	@DefaultMessage("Junio")
	String junio();

	@DefaultMessage("Julio")
	String julio();

	@DefaultMessage("Agosto")
	String agosto();

	@DefaultMessage("Septiembre")
	String septiembre();

	@DefaultMessage("Octubre")
	String octubre();

	@DefaultMessage("Noviembre")
	String noviembre();

	@DefaultMessage("Diciembre")
	String diciembre();

	@DefaultMessage("Lunes")
	String monday();

	@DefaultMessage("Martes")
	String tuesday();

	@DefaultMessage("Miercoles")
	String wednesday();

	@DefaultMessage("Jueves")
	String thursday();

	@DefaultMessage("Viernes")
	String friday();

	@DefaultMessage("Sabado")
	String saturday();

	@DefaultMessage("Domingo")
	String sunday();

	// ----------------------------------------------------------------- Calendar dialogs

	@DefaultMessage("Fecha de inicio:")
	String startDateColon();

	@DefaultMessage("Fecha de fin:")
	String endDateColon();

	@DefaultMessage("Días en blanco equivale a no horas extras.")
	String blankDaysNoExtraHours();

	@DefaultMessage("Días en blanco equivale a No Laborable.")
	String blankDaysInfo();

	@DefaultMessage("Días con 0 equivale a Laborable pero no trabaja.")
	String zeroDaysInfo();

	@DefaultMessage("Causa de inactividad:")
	String inactivityCause();

	@DefaultMessage("Porcentaje de reducción sobre las horas totales:")
	String percentReduction();

	@DefaultMessage("La fecha fin de este tramo sera el fin de los ERTEs totales o parciales")
	String erteEnd();

	@DefaultMessage("El valor introducido no tiene formato de número.")
	String errorFormatMsg();

	@DefaultMessage("Decimales separados por '.'")
	String decimalsInfo();

	@DefaultMessage("Tipos de ausencia:")
	String dropTypes();

	@DefaultMessage("Causa:")
	String causeColon();

	@DefaultMessage("Lunes:")
	String mondayColon();

	@DefaultMessage("Martes:")
	String tuesdayColon();

	@DefaultMessage("Miércoles:")
	String wednesdayColon();

	@DefaultMessage("Jueves:")
	String thursdayColon();

	@DefaultMessage("Viernes:")
	String fridayColon();

	@DefaultMessage("Sábado:")
	String saturdayColon();

	@DefaultMessage("Domingo:")
	String sundayColon();

	@DefaultMessage("Fecha fin:")
	String endDateFin();

	@DefaultMessage("Inicio:")
	String startColon();

	@DefaultMessage("Fin:")
	String endColon();

	@DefaultMessage("Tipo de Jornada:")
	String journeyTypeColon();

	@DefaultMessage("Variable:")
	String variableColon();

	@DefaultMessage("La fecha introducida interfiere con uno de los tramos existentes.")
	String dateInterferenceError();

	// ----------------------------------------------------------------- Calendar legend

	@DefaultMessage("Huelga")
	String strike();

	@DefaultMessage("ERE")
	String ere();

	@DefaultMessage("ERE Fuerza Mayor")
	String ereFza();

	@DefaultMessage("ERE Fuerza Mayor (Exoneración de cuotas)")
	String ereFzaExon();

	@DefaultMessage("Festivos")
	String festiveDays();

	// ----------------------------------------------------------------- Employee peculiarities

	@DefaultMessage("Tipo peculiaridad:")
	String peculiarityType();

	@DefaultMessage("Fecha inicio peculiaridad:")
	String peculiarityStartDate();

	@DefaultMessage("Tipo relación laboral (TRL):")
	String trl();

	@DefaultMessage("TRABAJADOR")
	String workerHeader();

	@DefaultMessage("FECHA")
	String dateHeader();

	@DefaultMessage("VALOR")
	String valueHeader();

	@DefaultMessage("Contigencias comunes")
	String cgc();

	@DefaultMessage("Desempleo")
	String unemployment();

	@DefaultMessage("Formación profesional")
	String training();

	@DefaultMessage("EMPRESA")
	String companyHeader();

	@DefaultMessage("FOGASA")
	String fogasa();

	// ----------------------------------------------------------------- Copy popup

	@DefaultMessage("Selecciona la persona sobre la que desee copiar el contrato")
	String selectPersonCopy();

	@DefaultMessage("Inicio de contrato")
	String contractStart();

	@DefaultMessage("Fin de contrato")
	String contractEnd();

	@DefaultMessage("Compruebe los datos introducidos.")
	String checkData();

	@DefaultMessage("Añadir datos específicos de contrato")
	String addSpecificContractData();

	// ----------------------------------------------------------------- Events draft

	@DefaultMessage("Incidencias")
	String incidencias();

	@DefaultMessage("COPIAR : ")
	String copyColon();

	@DefaultMessage("Incidencia")
	String incidencia();

	@DefaultMessage("VISTA: ")
	String vistaColon();

	// ----------------------------------------------------------------- Fx dialog

	@DefaultMessage("Categoría")
	String category();

	@DefaultMessage("Función")
	String function();

	// ----------------------------------------------------------------- IT (Incapacidad Temporal)

	@DefaultMessage("Partes I.T.")
	String partesIT();

	@DefaultMessage("Parte IT")
	String partIT();

	@DefaultMessage("Trabajador:")
	String workerColon();

	@DefaultMessage("Documento:")
	String documentColon();

	@DefaultMessage("NAF:")
	String nafColon();

	@DefaultMessage("Fecha Baja")
	String dischargeDate();

	@DefaultMessage("Causa de baja")
	String dischargeReason();

	@DefaultMessage("Fecha Alta")
	String hireDate();

	@DefaultMessage("Causa de alta")
	String hireReason();

	@DefaultMessage("Información parte")
	String partInfo();

	@DefaultMessage("F. Inicio")
	String fInicio();

	@DefaultMessage("Recaída")
	String relapse();

	@DefaultMessage("Base reguladora")
	String regulatoryBase();

	@DefaultMessage("Nº colegiado")
	String collegiateNumber();

	@DefaultMessage("Observaciones")
	String observations();

	@DefaultMessage("CIAS")
	String cias();

	@DefaultMessage("Ini. pago directo")
	String directPaymentStart();

	@DefaultMessage("Partes de confirmación")
	String confirmationParts();

	@DefaultMessage("Maternidad")
	String maternity();

	@DefaultMessage("Tipo de solicitante")
	String applicantType();

	@DefaultMessage("Motivo")
	String reason();

	@DefaultMessage("C. Parcialidad")
	String cPartiality();

	@DefaultMessage("FILTRAR : ")
	String filterColon();

	@DefaultMessage("Periodo Activo del Empleado")
	String activeEmployeePeriod();

	@DefaultMessage("Activo")
	String active();

	@DefaultMessage("Enfermedad Común, Accidente no Laboral")
	String commonDiseaseNonLaboral();

	@DefaultMessage("Enfermedad Común")
	String commonDisease();

	@DefaultMessage("Enfermedad Profesional, Accidente Laboral")
	String professionalDiseaseLaboral();

	@DefaultMessage("Enfermedad Profesional")
	String professionalDisease();

	@DefaultMessage("Maternidad, Lactancia, Riesgo Durante el Embarazo")
	String maternityLactancy();

	@DefaultMessage("Baja por Paternidad")
	String paternityLeave();

	@DefaultMessage("Paternidad")
	String paternity();

	@DefaultMessage("Enfermedad Común, Periodo de Carencia")
	String commonDiseaseCarencia();

	@DefaultMessage("Enfermedad Común, Prestación Profesional (COVID-19)")
	String commonDiseaseCovid();

	// ----------------------------------------------------------------- IT Tooltip

	@DefaultMessage("Regimen:")
	String regimenColon();

	@DefaultMessage("CCC:")
	String cccColon();

	@DefaultMessage("Doc:")
	String docColon();

	@DefaultMessage("NAF:")
	String naf();

	@DefaultMessage("Baja:")
	String bajaColon();

	@DefaultMessage("Motivo:")
	String motivoColon();

	@DefaultMessage("Alta:")
	String altaColon();

	// ----------------------------------------------------------------- Gtzdo Wizard

	@DefaultMessage("Tipo IT")
	String tipoIT();

	@DefaultMessage("Periocidad")
	String frequency();

	@DefaultMessage("Sobre")
	String sobre();

	// ----------------------------------------------------------------- Main sections

	@DefaultMessage("SELECCIONE UN CONVENIO PARA SER VISUALIZADO")
	String selectAgreementMsg();

	@DefaultMessage("LIQUIDACIÓN")
	String liquidacion();

	@DefaultMessage("Emitidos")
	String emitidos();

	@DefaultMessage("Pendientes")
	String pendientes();

	@DefaultMessage("Selcs.: ")
	String selcs();

	@DefaultMessage("FILTRAR LISTADO")
	String filtrarListado();

	@DefaultMessage("OPCIONES DE BUSQUEDA")
	String opcionesBusqueda();

	@DefaultMessage("Tipo CCC")
	String tipoCCC();

	@DefaultMessage("HISTORIAL CRA")
	String historialCRA();

	@DefaultMessage("Introduzca los parámetros de cálculo:")
	String calcParams();

	@DefaultMessage("Nómina")
	String nomina();

	@DefaultMessage("Comparar con")
	String compareWith();

	@DefaultMessage("EMPLEADOS")
	String employeesLabel();

	@DefaultMessage("EMPRESAS")
	String empresas();

	@DefaultMessage("No existe contratos para este filtro")
	String noContracts();

	@DefaultMessage("Importe un fichero FIE para analizarlo")
	String importFieMsg();

	@DefaultMessage("CONVENIOS")
	String convenios();

	@DefaultMessage("Eliminar definitivamente el convenio seleccionado")
	String deleteAgreementMsg();

	@DefaultMessage("Restaurar convenio")
	String restoreAgreement();

	@DefaultMessage("CONTRATOS")
	String contratos();

	@DefaultMessage("Eliminar definitivamente el empleado seleccionado")
	String deleteEmployeeMsg();

	@DefaultMessage("Restaurar empleado")
	String restoreEmployee();

	@DefaultMessage("Vaciar papelera de empleados")
	String clearEmployeeTrash();

	@DefaultMessage("CONCEPTOS")
	String conceptsTitle();

	@DefaultMessage("DEVENGOS")
	String accrualTitle();

	@DefaultMessage("DEDUCCIONES")
	String deductionTitle();

	@DefaultMessage("BONIFICACIONES")
	String bonusTitle();

	@DefaultMessage("Actividad / CCC")
	String actividadCcc();

	// ----------------------------------------------------------------- AgreementPaymentDialog options

	@DefaultMessage("[01] SALARIO BASE ANUAL")
	String salaryBaseAnnual();

	@DefaultMessage("[02] SALARIO BASE MENSUAL")
	String salaryBaseMonthly();

	@DefaultMessage("[03] SALARIO BASE DIARIO")
	String salaryBaseDaily();

	@DefaultMessage("[04] SALARIO BASE HORAS")
	String salaryBaseHours();

	@DefaultMessage("[10] PLUS SALARIAL MENSUAL")
	String salarialBonusMonthly();

	@DefaultMessage("[11] PLUS SALARIAL DIARIO")
	String salarialBonusDaily();

	@DefaultMessage("[12] PLUS SALARIAL DIARIO LABORABLES")
	String salarialBonusDailyWorkable();

	@DefaultMessage("[13] PLUS SALARIAL FIJO")
	String salarialBonusFixed();

	@DefaultMessage("[20] PLUS EXTRA SALARIAL MENSUAL")
	String extraSalarialBonusMonthly();

	@DefaultMessage("[21] PLUS EXTRA SALARIAL DIARIO")
	String extraSalarialBonusDaily();

	@DefaultMessage("[22] PLUS EXTRA SALARIAL DIARIO LABORABLES")
	String extraSalarialBonusDailyWorkable();

	@DefaultMessage("[23] PLUS EXTRA SALARIAL FIJO")
	String extraSalarialBonusFixed();

	@DefaultMessage("[30] RETRIBUCION EN ESPECIE")
	String remunrationInKind();

	@DefaultMessage("[40] COMPLEMENTO PERSONAL DE ANTIGÜEDAD")
	String seniorityPersonalComplement();

	@DefaultMessage("[43] GASTOS PERNOCTA DIARIO")
	String overnightExpensesDaily();

	@DefaultMessage("[45] GASTOS MANUTENCION DIARIO")
	String maintenanceExpensesDaily();

	@DefaultMessage("[46] GASTOS MANUTENCION EXTRANJERO DIARIO")
	String maintenanceExpensesForeignDaily();

	@DefaultMessage("[50] GASTOS LOCOMOTION SIN JUSTIFICANTE")
	String locomotionExpensesWithoutProof();

	@DefaultMessage("[90-91] PAGA EXTRA VERANO NAVIDAD")
	String extraPaySummerChristmas();

	@DefaultMessage("PRORRATEO")
	String apportionment();

	@DefaultMessage("[92] PAGA EXTRA BENEFICIOS")
	String extraPayBenefits();

	// ----------------------------------------------------------------- CretaRequest options

	@DefaultMessage("L00 Liquidación normal en período reglamentario de ingreso")
	String liquidationL00();

	@DefaultMessage("L02 Liquidaciones complementarias por salarios de tramitación")
	String liquidationL02();

	@DefaultMessage("L03 Liquidaciones por incremento de salarios con carácter retroactivo")
	String liquidationL03();

	@DefaultMessage("L13 Liquidaciones complementarias por vacaciones retribuidas y no disfrutadas")
	String liquidationL13();

	@DefaultMessage("L90 Liquidaciones complementarias por incremento de bases")
	String liquidationL90();

	@DefaultMessage("L91 Liquidaciones complementarias por nuevos tramos y/o trabajadores")
	String liquidationL91();

	@DefaultMessage("1- Atrasos de convenio")
	String causeI541();

	@DefaultMessage("2- Normativa (disposición legal)")
	String causeI542();

	@DefaultMessage("3- Acta de conciliación")
	String causeI543();

	@DefaultMessage("4- Sentencia judicial")
	String causeI544();

	@DefaultMessage("5- Cualquier otro título legítimo")
	String causeI545();

	@DefaultMessage("1: NIF ")
	String documentTypeNif();

	@DefaultMessage("6: NIE")
	String documentTypeNie();

	@DefaultMessage("9: CIF")
	String documentTypeCif();

	@DefaultMessage("C: Liquidaciones deudoras")
	String liquidationTypeC();

	@DefaultMessage("S: Liquidaciones acreedoras")
	String liquidationTypeS();

	@DefaultMessage("A: Liquidaciones deudoras y acreedoras")
	String liquidationTypeA();

	@DefaultMessage("1: Alta o modificació de los datos bancarios ")
	String actionType1();

	@DefaultMessage("2: Eliminación de los datos bancarios")
	String actionType2();

	// ----------------------------------------------------------------- ContractJourney

	@DefaultMessage("DESDE")
	String fromHeader();

	@DefaultMessage("Jornadas")
	String journeys();

	@DefaultMessage("NO EXISTEN JORNADAS")
	String noJourneys();

	@DefaultMessage("L")
	String mondayAbbr();

	@DefaultMessage("M")
	String tuesdayAbbr();

	@DefaultMessage("X")
	String wednesdayAbbr();

	@DefaultMessage("J")
	String thursdayAbbr();

	@DefaultMessage("V")
	String fridayAbbr();

	@DefaultMessage("S")
	String saturdayAbbr();

	@DefaultMessage("D")
	String sundayAbbr();

	// ----------------------------------------------------------------- ContractOptions

	@DefaultMessage("DAR BAJA")
	String discharge();

	@DefaultMessage("MODIFICACIONES")
	String modifications();

	// ----------------------------------------------------------------- Certificate

	@DefaultMessage("Certificado adjunto")
	String attachedCertificate();

	@DefaultMessage("Contraseña certificado")
	String certificatePassword();

	@DefaultMessage("Uso del certificado")
	String certificateUsage();

	@DefaultMessage("Uso personal de mi usuario")
	String personalUse();

	@DefaultMessage("Uso compartido")
	String sharedUse();

	@DefaultMessage("Tipo certificado")
	String certificateType();

	@DefaultMessage("TGSS")
	String tgss();

	@DefaultMessage("SEPE")
	String sepe();

	@DefaultMessage("AEAT")
	String aeat();

	// ----------------------------------------------------------------- Bonus / Deduction

	@DefaultMessage("NO EXISTEN BONIFICACIONES")
	String noBonuses();

	// ----------------------------------------------------------------- Payment

	@DefaultMessage("Importe Íntegro")
	String grossAmount();

	@DefaultMessage("Exento")
	String exempt();

	@DefaultMessage("Personalizado...")
	String customized();

	@DefaultMessage("Ingreso a Cuenta")
	String accountIncome();

	@DefaultMessage("Prorrateado")
	String prorated();

	// ----------------------------------------------------------------- PaymentsCleanDialog

	@DefaultMessage("Al actualizar el convenio se volverán importar todos los devengos. Seleccione los que quiere eliminar y deje solo los que quiera mantener.")
	String paymentsCleanMsg();

	// ----------------------------------------------------------------- PayrollEmailDialog

	@DefaultMessage("Cuenta definida en el perfil del empleado")
	String employeeProfileAccount();

	@DefaultMessage("Cuenta definida en el perfil de cada empresa")
	String enterpriseProfileAccount();

	@DefaultMessage("Proteger PDF")
	String protectPdf();

	@DefaultMessage("Proteger PDF con el NIF del receptor")
	String protectPdfNif();

	@DefaultMessage("* Los valores NOMBRE_EMPLEADO, PERIODOS_NOMINA y INFORMACION_EMPRESA serán sustituidos por la información correspondiente en cada caso.")
	String variablesSubstitutionMsg();

	// ----------------------------------------------------------------- PensionPlan AFI

	@DefaultMessage("DATOS CREACIÓN AFI")
	String afiCreationData();

	// ----------------------------------------------------------------- PeriodDialog

	@DefaultMessage("Introduzca las fechas:")
	String introduceDates();

	// ----------------------------------------------------------------- SSPECDialog

	@DefaultMessage("Peculiaridades de Cotización")
	String peculiaritiesOfQuotation();

	// ----------------------------------------------------------------- SalarySelect

	@DefaultMessage("RECIBO")
	String salaryReceipt();

	@DefaultMessage("MES DE INICIO")
	String startMonthHeader();

	@DefaultMessage("FECHA DE INICIO")
	String startDateHeader();

	@DefaultMessage("MES DE CÁLCULO")
	String calculationMonth();

	@DefaultMessage("FECHA DE CÁLCULO")
	String calculationDate();

	@DefaultMessage("FECHA DE PAGO")
	String paymentDateHeader();

	// ----------------------------------------------------------------- SalaryPaymentWizard

	@DefaultMessage("Cálculo")
	String calculation();

	@DefaultMessage("Ejercicio")
	String fiscalYear();

	// ----------------------------------------------------------------- SecondaryUserDialog

	@DefaultMessage("Buscar por DNI")
	String searchByDni();

	// ----------------------------------------------------------------- SelectDialog

	@DefaultMessage("Selecciona : ")
	String selectColon();

	// ----------------------------------------------------------------- ServiAgreementDialog

	@DefaultMessage("AonSolutions: Importación de convenios INACTIVA.")
	String agreementImportInactive();

	// ----------------------------------------------------------------- VariableDialog

	@DefaultMessage("Tipo variable")
	String variableType();

	@DefaultMessage("Nombre variable")
	String variableName();

	@DefaultMessage("Valor variable")
	String variableValue();

	// ----------------------------------------------------------------- Center of work

	@DefaultMessage("Centro de trabajo")
	String workplace();

	// ----------------------------------------------------------------- EventsDraft

	@DefaultMessage("Enviar por correo electrónico al trabajador")
	String sendByEmailToWorker();

	@DefaultMessage("Enviar por correo electrónico como archivo adjunto...")
	String sendByEmailAsAttachment();

	@DefaultMessage("Descargar como...")
	String downloadAs();

	// ----------------------------------------------------------------- SalaryDraft header

	@DefaultMessage("RECIBO              FECHA DE CÁLCULO")
	String salaryCalculationDate();

	// ----------------------------------------------------------------- Documents

	@DefaultMessage(" 1 de .. ")
	String pageIndicator();

	// ----------------------------------------------------------------- Employee events

	@DefaultMessage("Empleado:")
	String employeeColon();

	// ----------------------------------------------------------------- MainCRA

	@DefaultMessage("CCC")
	String ccc();

	@DefaultMessage("Tipo CCC")
	String cccType();

	// ----------------------------------------------------------------- SalaryDraft — scope descriptions

	@DefaultMessage("Sistema")
	String sistema();

	@DefaultMessage("Contrato")
	String contractLabel();

	// ----------------------------------------------------------------- SalaryDraft — listbox items

	@DefaultMessage("COTIZACIÓN MENSUAL")
	String cotizacionMensual();

	@DefaultMessage("COTIZACIÓN DIARIA")
	String cotizacionDiaria();

	@DefaultMessage("SI")
	String yes();

	@DefaultMessage("NO")
	String no();

	// ----------------------------------------------------------------- SalaryDraft — rename dialog

	@DefaultMessage("Renombrar...")
	String rename();

	@DefaultMessage("Nuevo Nombre")
	String newName();

	// ----------------------------------------------------------------- SalaryDraft — context panel

	@DefaultMessage("Variables de calculo")
	String calculationVariables();

	@DefaultMessage("Ocultar")
	String hide();

	@DefaultMessage("Mostrar")
	String show();

	@DefaultMessage("Ocultar variables del {0}")
	String hideVariablesOf(String scope);

	@DefaultMessage("Mostrar variables del {0}")
	String showVariablesOf(String scope);

	// ----------------------------------------------------------------- SalaryDraft — emit messages

	@DefaultMessage("Emitiendo la {0}. Espere por favor.")
	String emittingMessage(String type);

	@DefaultMessage("La {0} se ha emitido correctamente.")
	String emittedSuccessMessage(String type);

	@DefaultMessage("Se ha producido un error al emitir la {0} ''{1}''. Disculpe las molestias.")
	String emittingErrorMessage(String type, String message);

	// ----------------------------------------------------------------- SalaryDraft — fiscal model menu

	@DefaultMessage("Modelo {0}")
	String fiscalModelLabel(String modelName);

	@DefaultMessage("Estado")
	String status();

	// ----------------------------------------------------------------- SalaryDraft — seniority

	@DefaultMessage("AÑO")
	String yearSingular();

	@DefaultMessage("AÑOS")
	String yearPlural();

	// ----------------------------------------------------------------- SalaryDraft — sync calcs

	@DefaultMessage("Sincronizando cálculos")
	String syncingCalculations();

	@DefaultMessage("Sincronización de cálculos completada")
	String syncCalculationsComplete();

	@DefaultMessage("No se han podido sincronizar los cálculos. {0}")
	String syncCalculationsError(String message);

	// ----------------------------------------------------------------- SalaryDraft — payments table headers

	@DefaultMessage("CUANTÍA")
	String amountTitle();

	@DefaultMessage("CONCEPTO")
	String conceptTitle();

	// ----------------------------------------------------------------- SalaryDraft — issue date

	@DefaultMessage("COBRO")
	String issue();

	@DefaultMessage("Prorrat.")
	String proratedShort();

	// ----------------------------------------------------------------- ContractAttachUIImpl

	@DefaultMessage("Generando borrador de contrato")
	String generatingContractDraft();

	@DefaultMessage("El borrador de contrato se ha generado correctamente")
	String contractDraftSuccess();

	@DefaultMessage("El borrador de la prórroga de contrato se ha generado correctamente")
	String contractExtensionDraftSuccess();

	@DefaultMessage("Generando borrador propuesta recolocación")
	String generatingRelocationDraft();

	@DefaultMessage("El borrador de la propuesta recolocación del contrato se ha generado correctamente")
	String contractRelocationDraftSuccess();

	@DefaultMessage("Generando borrador de la copia basica")
	String generatingBasicCopyDraft();

	// ----------------------------------------------------------------- TGSSContextMenu

	@DefaultMessage("Cambios AFI")
	String afiChanges();

	@DefaultMessage("Duplicados de Documentos TA")
	String duplicateDocumentsTA();

	@DefaultMessage("Duplicados de Documentos TA (Baja)")
	String duplicateDocumentsTAEnd();

	@DefaultMessage("IDC-Trab Cuenta Ajena")
	String idcTrabCuentaAjena();

	@DefaultMessage("IDC/Periodo Liquidación-NSS")
	String idcPeriodoLiquidacionNss();

	@DefaultMessage("Vida Laboral")
	String laboralLife();

	@DefaultMessage("Eliminar alta consolidada")
	String deleteAltaConsolidada();

	@DefaultMessage("Notificación AFI (TGSS)")
	String notificacionAFITgss();

	// ----------------------------------------------------------------- AttachContextMenu

	@DefaultMessage("Borrador Contrato")
	String borradorContrato();

	@DefaultMessage("Borrador Copia Basica")
	String borradorCopiaBasica();

	@DefaultMessage("Borrador Contrato (Transformación)")
	String borradorContratoTransformacion();

	@DefaultMessage("Borrador Contrato (Prórroga)")
	String borradorContratoProrroqa();

	@DefaultMessage("Borrador Propuesta Recolocación")
	String borradorPropuestaRecolocacion();

	@DefaultMessage("Notificación Laboral")
	String notificacionLaboral();

}
