package com.esferalia.aon.payroll.calculator.sql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Ascendiente;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.CausaRegularizacion;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Contrato;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Descendiente;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.Discapacidad;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.SituacionFamiliar;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext.SituacionLaboral;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.AgreementLevelCategoryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PersonColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RaddressColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.WorkplaceColumns;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.ExpressionContext.ExpressionExceptionWrapper;

public class SQLAgreementSalaryCalculatorContext extends SQLContractSalaryCalculatorContext {

	// @formatter:off
	private static final String MAIN_SQL = "SELECT * " + " FROM %s"
			+ " LEFT JOIN enterprise_ccc ON (contract.enterprise_ccc = enterprise_ccc.id)"
			+ " LEFT JOIN enterprise_activity ON (contract.enterprise_activity = enterprise_activity.id)"
			+ " LEFT JOIN agreement_level ON (contract.agreement_level = agreement_level.id)"
//			+ " LEFT JOIN %s ON (contract.agreement_level_category = agreement_level_category.id)"
//			+ " LEFT JOIN agreement_level ON (agreement_level.id = agreement_level_category.agreement_level)"
			+ " LEFT JOIN agreement ON (agreement.id = agreement_level.agreement)" + ", %s" + ", %s " + ", %s"
			+ " LEFT JOIN payroll_workplace ON (payroll_workplace.workplace = workplace.id)" + ", %s" + ", %s"
			+ " LEFT JOIN customer ON (customer.registry = " + ENTERPRISE_REGISTRY + ".id)" + ", %s"
			+ " WHERE contract.person = person.registry" // INNER JOIN: person
															// is NOT NULL
			+ " AND person.registry = person_registry.id" // INNER JOIN: //
															// registry is NOT
															// NULL
			+ " AND contract.workplace = workplace.id" // INNER JOIN: workplace
														// is NOT NULL
			+ " AND workplace.enterprise = enterprise.registry" // INNER JOIN:
																// enterprise is
																// NOT NULL
			+ " AND enterprise.registry = enterprise_registry.id" // INNER JOIN:
																	// registry
																	// is NOT
																	// NULL
			+ " AND workplace.address = raddress.id" // INNER JOIN: address is
														// NOT NULL
			+ " AND contract.start_date <= ? " + " AND ( contract.end_date  IS NULL" + " OR contract.end_date >= ? )";
			// @formatter:on

	// @formatter:off
	private static final String CONTRACT = "( SELECT " + "0 " + ContractColumns.ID + " ,0 " + ContractColumns.DOMAIN
			+ " ,0 " + ContractColumns.PERSON + " ,0 " + ContractColumns.WORKPLACE + " ,NULL "
			+ ContractColumns.ENTERPRISE_CCC + " ,'%1$tY-%1$tm-%1$td' " + ContractColumns.START_DATE + // TODO:
			" ,NULL " + ContractColumns.END_DATE + " ,NULL " + ContractColumns.CALENDAR + " ,NULL "
			+ ContractColumns.DESCRIPTION + " ,0 " + ContractColumns.SEPE_STATUS + " ,NULL "
			+ ContractColumns.REGISTRATION + " ,'%2$tY-%2$tm-%2$td' " + ContractColumns.SENIORITY_DATE + " ,NULL "
			+ ContractColumns.ENTERPRISE_ACTIVITY + " ,NULL " + ContractColumns.SS_REGIME + " ,%3$d "
//			+ ContractColumns.AGREEMENT_LEVEL_CATEGORY + " ,NULL " + ContractColumns.MODEL + " ,NULL "
			+ ContractColumns.AGREEMENT_LEVEL + " ,NULL " + ContractColumns.MODEL + " ,NULL "
			+ ContractColumns.CATEGORY_DESCRIPTION + " ,0 " + ContractColumns.SS_STATUS + " ) AS  "
			+ SQLConstants.CONTRACT;
			// @formatter:on

	// @formatter:off
	private static final String __CATEGORY = "( SELECT " + "0 " + AgreementLevelCategoryColumns.ID + " ,0 "
			+ AgreementLevelCategoryColumns.DOMAIN + " ,%d " + AgreementLevelCategoryColumns.AGREEMENT_LEVEL + " ,NULL "
			+ AgreementLevelCategoryColumns.DESCRIPTION + " ) AS  " + SQLConstants.AGREEMENT_LEVEL_CATEGORY;
			// @formatter:on

	// @formatter:off
	private static final String PERSON = "( SELECT " + "0 " + PersonColumns.REGISTRY + " ,0 " + PersonColumns.DOMAIN
			+ " ,NULL " + PersonColumns.BIRTH_DATE + " ,0 " + PersonColumns.GENDER + " ,0 "
			+ PersonColumns.MARITAL_STATUS + " ,NULL " + PersonColumns.SOCIAL_SECURITY_NUM + " ,NULL "
			+ PersonColumns.NAME + " ,NULL " + PersonColumns.FIRST_SURNAME + " ,NULL " + PersonColumns.SECOND_SURNAME
			+ " ) AS  " + SQLConstants.PERSON;
			// @formatter:on

	// @formatter:off
	private static final String P_REGISTRY = "( SELECT " + "0 " + RegistryColumns.ID + " ,0 " + RegistryColumns.DOMAIN
			+ " ,NULL " + RegistryColumns.DOCUMENT + " ,0 " + RegistryColumns.DOCUMENT_TYPE + " ,'ES' "
			+ RegistryColumns.DOCUMENT_COUNTRY + " ,NULL " + RegistryColumns.NAME + " ,NULL " + RegistryColumns.ALIAS
			+ " ,NULL " + RegistryColumns.TYPE + " ,'ES' " + RegistryColumns.NATIONALITY + " ,0 "
			+ RegistryColumns.SECURITY_LEVEL + " ) AS " + PERSON_REGISTRY;
			// @formatter:on

	// @formatter:off
	private static final String WORKPLACE = "( SELECT " + "0 " + WorkplaceColumns.ID + " ,0 " + WorkplaceColumns.DOMAIN
			+ " ,0 " + WorkplaceColumns.ENTERPRISE + " ,'' " + WorkplaceColumns.DESCRIPTION + " ,0 "
			+ WorkplaceColumns.ADDRESS + " ,0 " + WorkplaceColumns.CUSTOMER + " ,0 " + WorkplaceColumns.SCOPE + " ,0 "
			+ WorkplaceColumns.ECONOMICAGREEMENT + " ,1 " + WorkplaceColumns.ACTIVE + " ) AS " + SQLConstants.WORKPLACE;
			// @formatter:on

	// @formatter:off
	private static final String ENTERPRISE = "( SELECT " + "0 " + EnterpriseColumns.REGISTRY + " ,0 "
			+ EnterpriseColumns.DOMAIN + " ,0 " + EnterpriseColumns.SCOPE + " ,NULL " + EnterpriseColumns.CALENDAR
			+ " ) AS " + SQLConstants.ENTERPRISE;
			// @formatter:on

	// @formatter:off
	private static final String E_REGISTRY = "( SELECT " + "0 " + RegistryColumns.ID + " ,0 " + RegistryColumns.DOMAIN
			+ " ,NULL " + RegistryColumns.DOCUMENT + " ,0 " + RegistryColumns.DOCUMENT_TYPE + " ,'ES' "
			+ RegistryColumns.DOCUMENT_COUNTRY + " ,NULL " + RegistryColumns.NAME + " ,NULL " + RegistryColumns.ALIAS
			+ " ,NULL " + RegistryColumns.TYPE + " ,'ES' " + RegistryColumns.NATIONALITY + " ,0 "
			+ RegistryColumns.SECURITY_LEVEL + " ) AS " + ENTERPRISE_REGISTRY;
			// @formatter:on

	// @formatter:off
	private static final String RADDRESS = "( SELECT " + "0 " + RaddressColumns.ID + " ,0 " + RaddressColumns.DOMAIN
			+ " ,0 " + RaddressColumns.REGISTRY + " ,0 " + RaddressColumns.TYPE + " ,NULL " + RaddressColumns.RECIPIENT
			+ " ,0 " + RaddressColumns.STREET_TYPE + " ,NULL " + RaddressColumns.ADDRESS + " ,NULL "
			+ RaddressColumns.NUMBER + " ,NULL " + RaddressColumns.ADDRESS2 + " ,NULL " + RaddressColumns.ADDRESS3
			+ " ,NULL " + RaddressColumns.ZIP + " ,NULL " + RaddressColumns.CITY + " ,NULL " + RaddressColumns.GEOZONE
			+ " ,NULL " + RaddressColumns.ALIAS + " ,NULL " + RaddressColumns.MUNICIPALITY_CODE + " ) AS "
			+ SQLConstants.RADDRESS;
	// @formatter:on

	private CCCType cccType;
	private SSRegimeType ssRegimeType;
	private int agreementLevelId;

	public SQLAgreementSalaryCalculatorContext(Connection connection, Date startDate, Date endDate,
			int agreementLevelId) throws SQLException, ExpressionException {
		this(connection, startDate, endDate, endDate, endDate, agreementLevelId);
	}

	public SQLAgreementSalaryCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			int agreementLevelId) throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, endDate, agreementLevelId);
	}

	public SQLAgreementSalaryCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Date chargeDate, int agreementLevelId) throws SQLException, ExpressionException {
		this(connection, startDate, endDate, issueDate, chargeDate, agreementLevelId, CCCType.PRINCIPAL,
				SSRegimeType.GENERAL);
	}

	public SQLAgreementSalaryCalculatorContext(Connection connection, Date startDate, Date endDate, Date issueDate,
			Date chargeDate, int agreementLevelId, CCCType cccType, SSRegimeType ssRegimeType)
					throws SQLException, ExpressionException {
		super(connection, startDate, endDate, issueDate, chargeDate, new Criteria(),
				getPaymentsCriteria(SalaryType.SALARY, SalaryType.EXTRA), NEWER, startDate, agreementLevelId);
		this.cccType = cccType;
		this.ssRegimeType = ssRegimeType;
		this.agreementLevelId = agreementLevelId;
	}
	// ------------------------------------------------------- Protected methods

	@Override
	protected String getMainSql(Object... args) {
		Date startDate = (Date) args[0];
		int agreementLevelId = (Integer) args[1];
		return String.format(MAIN_SQL, String.format(CONTRACT, startDate, startDate, agreementLevelId),
				/*String.format(CATEGORY, agreementLevelId),*/ PERSON, P_REGISTRY, WORKPLACE, ENTERPRISE, E_REGISTRY,
				RADDRESS);
	}
	
	@Override
	protected IIrpfCalculatorContext getIrpfCalculatorContext(Connection conn, Date startDate, Date endDate,
			Criteria criteria) {
		
		
		
		return new IIrpfCalculatorContext() {
			
			@Override
			public boolean next() {
				return false;
			}
			
			@Override
			public BigDecimal getTipoRetencion() {
				return null;
			}
			
			@Override
			public SituacionLaboral getSituacionLaboral() {
				return SituacionLaboral.TRABAJADOR_ACTIVO;
			}
			
			@Override
			public SituacionFamiliar getSituacionFamiliar() {
				return SituacionFamiliar.TRES;
			}
			
			@Override
			public BigDecimal getRetribSatisfechas() {
				return null;
			}
			
			@Override
			public BigDecimal getRetribAnualesIniciales() {
				return null ; 
			}
			
			@Override
			public BigDecimal getRetribAnuales() {
				double cgcBase = getContextVariable(ContextVariable.CGC_BASE);
				return BigDecimal.valueOf(Math.round(cgcBase * 12) );
			}
			
			@Override
			public String getRetenedorNif() {
				return "Z7896423E";
			}
			
			@Override
			public String getRetenedorApellidosNombre() {
				return "LINUX FOUNDATION";
			}
			
			@Override
			public BigDecimal getRetencionPracticada() {
				return null;
			}
			
			@Override
			public BigDecimal getRetencionAnualInicial() {
				return null ; //BigDecimal.ZERO;
			}
			
			@Override
			public boolean getResidenciaInicialCeutaMelilla() {
				return false;
			}
			
			@Override
			public boolean getResidenciaCeutaMelilla() {
				return false;
			}
			
			@Override
			public boolean getRdtosObtenidosCeutaMelilla() {
				return false;
			}
			
			@Override
			public boolean getProlongacionLaboral() {
				return false;
			}
			
			@Override
			public BigDecimal getPensionCompensatoria() {
				return null ; //BigDecimal.ZERO;
			}
			
			@Override
			public boolean getPagoPrestamosVivienda() {
				return false;
			}
			
			@Override
			public String getNifConyuge() {
				return null;
			}
			
			@Override
			public String getNif() {
				return "87449445H";
			}
			
			@Override
			public boolean getMovilidadReducida() {
				return false;
			}
			
			@Override
			public boolean getMovilidadGeografica() {
				return false;
			}
			
			@Override
			public BigDecimal getMinoracionPrestamosVivienda() {
				return null ; //BigDecimal.ZERO;
			}
			
			@Override
			public BigDecimal getMinimoPersonalFamiliarInicial() {
				return null ; //BigDecimal.ZERO;
			}
			
			@Override
			public BigDecimal getIrregularidad2() {
				return null ; //BigDecimal.ZERO;
			}
			
			@Override
			public BigDecimal getIrregularidad1() {
				return null ; //BigDecimal.ZERO;
			}
			
			@Override
			public BigDecimal getGastosAnuales() {
				double employeeQuote = getContextVariable(ContextVariable.EMPLOYEE_QUOTA);
				return BigDecimal.valueOf(Math.round(employeeQuote * 12));
			}
			
			@Override
			public Discapacidad getDiscapacidad() {
				return Discapacidad.GRADO0;
			}
			
			@Override
			public Iterable<Descendiente> getDescendientes() {
				return Collections.emptyList();
			}
			
			@Override
			public Contrato getContrato() {
				return Contrato.UNO;
			}
			
			@Override
			public String getComunidadAutonoma() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public CausaRegularizacion getCausaRegularizacion() {
				return null;
			}
			
			@Override
			public BigDecimal getBaseRetencion() {
				return null;
			}
			
			@Override
			public int getAñoNacimiento() {
				return 1969;
			}
			
			@Override
			public Iterable<Ascendiente> getAscendientes() {
				return Collections.emptyList();
			}
			
			@Override
			public String getApellidosNombre() {
				return "TORVALDS BENEDICT LINUS";
			}
			
			@Override
			public BigDecimal getAnualidadesHijos() {
				return null ; //BigDecimal.ZERO;
			}
		};
	}

	// ------------------------------------------------------------------------

	@Override
	public CCCType getCCCType() {
		// TODO Auto-generated method stub
		return cccType;
	}

	@Override
	public SSRegimeType getSSRegime() {
		return ssRegimeType;
	}

	// ------------------------------------------------------------------------
	
	private double getContextVariable(ContextVariable var) {
		//@formatter:off
		return
		getExpressionContext().getVariables(var.getName()).stream()
		.collect(Collectors.summingDouble(v->((Number)v.getValue(v.getPeriod())).doubleValue()))
		;
		//@formatter:on
	}
	
	
}
