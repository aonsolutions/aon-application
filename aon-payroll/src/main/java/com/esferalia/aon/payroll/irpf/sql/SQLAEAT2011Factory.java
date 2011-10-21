package com.esferalia.aon.payroll.irpf.sql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import com.aeat.jaxb.AEATRetencionesEntrada2011;
import com.aeat.jaxb.ObjectFactory;
import com.aeat.jaxb.TipoDiscapacidad;
import com.aeat.jaxb.TipoDiscapacidad.Grado1;
import com.aeat.jaxb.TipoDiscapacidad.Grado1.MovilidadReducida;
import com.aeat.jaxb.TipoDiscapacidad.Grado2;
import com.aeat.jaxb.TipoError;
import com.aeat.jaxb.TipoRetenciones;
import com.aeat.jaxb.TipoRetenedorEntrada2011;
import com.aeat.jaxb.TipoRetenedorError2011;
import com.aeat.jaxb.TipoRetenidoEntrada2011;
import com.aeat.jaxb.TipoRetenidoEntrada2011.Ascendiente;
import com.aeat.jaxb.TipoRetenidoEntrada2011.Descendiente;
import com.aeat.jaxb.TipoRetenidoEntrada2011.Descendiente.ComputadoEntero;
import com.aeat.jaxb.TipoRetenidoEntrada2011.PagoPrestamosVivienda;
import com.aeat.jaxb.TipoRetenidoEntrada2011.PagoPrestamosVivienda.RegimenGeneral;
import com.aeat.jaxb.TipoRetenidoEntrada2011.PagoPrestamosVivienda.RegimenTransitorio;
import com.aeat.jaxb.TipoRetenidoEntrada2011.Reducciones;
import com.aeat.jaxb.TipoRetenidoEntrada2011.Regularizacion;
import com.aeat.jaxb.TipoRetenidoEntrada2011.Regularizacion.MinoracionPrestamosVivienda;
import com.aeat.jaxb.TipoRetenidoEntrada2011.SituacionFamiliar;
import com.aeat.jaxb.TipoRetenidoEntrada2011.SituacionFamiliar.Situacion1;
import com.aeat.jaxb.TipoRetenidoEntrada2011.SituacionFamiliar.Situacion2;
import com.aeat.jaxb.TipoRetenidoEntrada2011.SituacionFamiliar.Situacion3;
import com.aeat.jaxb.TipoRetenidoEntrada2011.SituacionLaboral;
import com.aeat.jaxb.TipoRetenidoEntrada2011.SituacionLaboral.TrabajadorActivo;
import com.aeat.jaxb.TipoRetenidoEntrada2011.SituacionLaboral.TrabajadorActivo.MovilidadGeografica;
import com.aeat.jaxb.TipoRetenidoEntrada2011.SituacionLaboral.TrabajadorActivo.ProlongacionLaboral;
import com.aeat.jaxb.TipoRetenidoError2011;
import com.code.aon.common.AonException;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryDocument;
import com.code.aon.registry.enumeration.DocumentType;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractCost;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.sql.SQLCollection;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.payroll.enumeration.DisabilityLevel;
import com.esferalia.aon.payroll.enumeration.FamilySituation;
import com.esferalia.aon.payroll.enumeration.IrpfContractType;
import com.esferalia.aon.payroll.enumeration.IrpfDeductHomeLoan;
import com.esferalia.aon.payroll.enumeration.IrpfRegularizationReason;
import com.esferalia.aon.payroll.irpf.IrpfCalculator;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfDataAscendantsColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfDataColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfDataDescendientsColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfRegularizationColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.IrpfResultColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PaymentConceptColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PersonColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.WorkplaceColumns;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.Period;
import com.sun.net.ssl.internal.www.protocol.https.Handler;

public class SQLAEAT2011Factory extends ObjectFactory {
	
	
	public static interface Entrada2011Handler {
		void onAEATRetencionesEntrada2011(AEATRetencionesEntrada2011 entrada2011);
		void onTipoRetenedorEntrada2011(TipoRetenedorEntrada2011 retenedorEntrada2011);
		void onTipoRetenidoEntrada2011 ( Administration administration, TipoRetenidoEntrada2011 retenidoEntrada2011);
	}
	
	public static final String PERSON_REGISTRY = "person_registry";
	public static final String ENTERPRISE_REGISTRY = "enterprise_registry";
	
	private static Criteria DEFAULT_CRITERIA = new Criteria(){
		{
			addOrder(SQLConstants.WORKPLACE + "." + WorkplaceColumns.ID);
			addOrder(SQLConstants.CONTRACT + "." + ContractColumns.ID);
		}
	};

	private static final String MAIN_SQL = 
		"SELECT * "
		+" FROM contract "
		+" LEFT JOIN irpf_data ON ( contract.id = irpf_data.contract"
		+						" AND irpf_data.start_date <= ?"
		+						" AND ( irpf_data.end_date IS NULL OR irpf_data.end_date >= ? ))"
		+" LEFT JOIN irpf_regularization ON ( contract.id = irpf_regularization.contract"
		+									" AND irpf_regularization.effective_date = ? )"
		+", person"
		+", registry AS " + PERSON_REGISTRY
		+", workplace"
		+", enterprise"
		+", registry AS " + ENTERPRISE_REGISTRY
		+" LEFT JOIN customer ON ( customer.registry = " + ENTERPRISE_REGISTRY + ".id)"
		+", raddress"
		+" WHERE contract.person = person.registry"	
		+" AND person.registry = person_registry.id"
		+" AND contract.workplace = workplace.id"				
		+" AND workplace.enterprise = enterprise.registry"		
		+" AND enterprise.registry = enterprise_registry.id"				
		+" AND workplace.address = raddress.id"					
		+" AND contract.start_date <= ? "					 
		+" AND ( contract.end_date  IS NULL"
		+" OR contract.end_date >= ? )" ;
//		+" AND ( workplace.economicAgreement = ? "
//		+" OR ( ? = 4 AND  workplace.economicAgreement IS NULL ))";

	
	
	private static final String ASCENDIENTS_SQL = "SELECT * " 
		+ " FROM irpf_data_ascendants"
		+ " WHERE irpf_data = ? ";

	private static final String DESCENDIENTS_SQL = "SELECT * " 
		+ " FROM irpf_data_descendients"
		+ " WHERE irpf_data = ? ";
	
	private static final String RESULTS_SQL = "SELECT * " 
		+ " FROM irpf_result"
		+ " WHERE effective_date <= ?"
		+ " AND contract = ? "
		+ " ORDER BY effective_date DESC" 
		+ " LIMIT 1 ";

	private static final String SALARY_SQL = "SELECT"
		+ " sum(irpf_base) AS irpf_base " 
		+ ",sum(total_irpf) AS total_irpf " 
		+ ",sum(social_security_contributions) AS social_security_contributions " 
		+ " FROM salary"
		+ " WHERE contract = ? "
		+ " AND start_date >= ? "
		+ " AND end_date < ? ";

	private Date 										date ;
	private Criteria 									criteria;
	private Connection 									connection;
	
	private PreparedStatement 							mainStmt;
	private PreparedStatement 							salaryStmt;
	private PreparedStatement 							resultsStmt;
	private PreparedStatement 							ascendientsStmt;
	private PreparedStatement 							descendientsStmt;
	
	private SQLContractSalaryCalculatorContext			sqlContractSalaryCalculatorContext;
	
	public SQLAEAT2011Factory(Connection connection, Date date) 
	throws SQLException {
		this ( connection, date, DEFAULT_CRITERIA );
	}

	public SQLAEAT2011Factory(Connection connection, Date date, Criteria criteria) 
	throws SQLException {
		this.date = date;
		this.connection = connection;
		this.criteria = new Criteria();
		this.criteria.addExpression(criteria.getExpression());
		this.criteria.setOrderByList(DEFAULT_CRITERIA.getOrderByList());
		prepareStatements();
	}
	
	public void forEachTipoRetenidoEntrada2011( Entrada2011Handler handler ) 
	throws SQLException, ExpressionException, SalaryException {

		initContractSalaryCalculatorContext();
		
		AEATRetencionesEntrada2011 aeatRetencionesEntrada2011 =
			createAEATRetencionesEntrada2011();
		
		TipoRetenciones tipoRetenciones = 
			createTipoRetenciones();
		tipoRetenciones.setCodModelo("RET");
		tipoRetenciones.setEjercicio(getYear());
		
		aeatRetencionesEntrada2011.setIdDoc(tipoRetenciones);
		
		handler.onAEATRetencionesEntrada2011(aeatRetencionesEntrada2011);
		
		forEachTipoRetenedorEntrada2011(handler);
		
	}
	
	protected AEATRetencionesEntrada2011 getAeatRetencionesEntrada2011() 
	throws SQLException, ExpressionException, SalaryException {
		
		Entrada2011Builder entrada2011Builder = 
			new Entrada2011Builder();
		
		forEachTipoRetenidoEntrada2011(entrada2011Builder);
		entrada2011Builder.onTipoRetenedorEntrada2011(null);
		
		return entrada2011Builder.getEntrada2011();
	}
	

	private int getYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.date);
		return calendar.get(Calendar.YEAR);
	}
	
	private void forEachTipoRetenedorEntrada2011 ( Entrada2011Handler handler) 
	throws SQLException, ExpressionException, SalaryException {
		ResultSet resultSet = null ;
		try {
			TipoRetenedorEntrada2011 tipoRetenedorEntrada2011 = null;
			resultSet = this.mainStmt.executeQuery();
			while (resultSet.next()) {

				this.sqlContractSalaryCalculatorContext.next();
				Integer contractId = getContract(resultSet, ContractColumns.ID);
				int ctxId = this.sqlContractSalaryCalculatorContext.getId();
				if ( contractId != ctxId )
					System.out.println(  contractId + "= " + ctxId );
				
				
				String enterpriseNif = getEnterpriseRegistry(resultSet, RegistryColumns.DOCUMENT);

				if ( tipoRetenedorEntrada2011 == null || 
					!tipoRetenedorEntrada2011.getNif().equalsIgnoreCase(enterpriseNif) ) {
					
					tipoRetenedorEntrada2011 =
						createTipoRetenedorEntrada2011();
					

					tipoRetenedorEntrada2011.setNif(enterpriseNif);
					String name = getEnterpriseRegistry(resultSet, RegistryColumns.NAME);
					tipoRetenedorEntrada2011.setApellidosNombre(toTipoApellidosyNombre(name));

					handler.onTipoRetenedorEntrada2011(tipoRetenedorEntrada2011);
				}
				
				forEachTipoRetenidoEntrada2011(resultSet, handler );
			}
		}
		finally {
			if ( resultSet != null ) {
				resultSet.close();
			}
		}
	}
	
	private void forEachTipoRetenidoEntrada2011 ( ResultSet rs,  Entrada2011Handler handler) 
	throws SQLException, ExpressionException, SalaryException{
		
		TipoRetenidoEntrada2011 tipoRetenidoEntrada2011 = 
			createTipoRetenidoEntrada2011();
		

		String nif = toTipoNif ( ( String) getPersonRegistry(rs, RegistryColumns.DOCUMENT));
		tipoRetenidoEntrada2011.setNif(nif != null ? nif : "77777777B");
		
		String name = getPersonRegistry(rs, RegistryColumns.NAME);
		tipoRetenidoEntrada2011.setApellidosNombre(toTipoApellidosyNombre(name));
		
		Date birthDate = getPerson(rs, PersonColumns.BIRTH_DATE);
		int birthYear = birthDate != null ? CommonUtil.getYear(birthDate) : 2011;
		tipoRetenidoEntrada2011.setAñoNacimiento(birthYear);
		int age = getAge(birthYear);
		
		Integer irpfDataId = getIrpfData(rs, IrpfDataColumns.ID);
		
		List<Descendiente> descendientes = 
			tipoRetenidoEntrada2011.getDescendiente();
		fillDescendiente(descendientes, irpfDataId);
		
		List<Ascendiente> ascendientes = 
			tipoRetenidoEntrada2011.getAscendiente();
		fillAscendiente(ascendientes, irpfDataId);

		// Not required : 
		//tipoRetenidoEntrada2011.setComunidadAutonoma(paramString); 
		//tipoRetenidoEntrada2011.setResidenciaCeutaMelilla()
	
		SituacionFamiliar situacionFamiliar = getSituacionFamiliar(rs, descendientes); 
		tipoRetenidoEntrada2011.setSituacionFamiliar(situacionFamiliar);
		
		TipoDiscapacidad tipoDiscapacidad = getDiscapacidad(rs);
		tipoRetenidoEntrada2011.setDiscapacidad(tipoDiscapacidad);
		
		SituacionLaboral situacionLaboral = new SituacionLaboral();
		TrabajadorActivo trabajadorActivo = getTrabajadorActivo(rs, age); 
		situacionLaboral.setTrabajadorActivo(trabajadorActivo);
		
		tipoRetenidoEntrada2011.setSituacionLaboral(situacionLaboral);
		
		ISalary paidSalary = getRetribSatisfechas(rs);
		ISalary annualSalary = getRetribucionesAnuales(rs, paidSalary);
		
		Double irpfBase = annualSalary.getIrpfBase();
		BigDecimal retribAnuales = toTipoImpositivo(irpfBase);
		if ( retribAnuales == null ) {
			return;
		}
		tipoRetenidoEntrada2011.setRetribAnuales(retribAnuales);
		
		
		Double irregularidad1 = 
			getIrpfData(rs, IrpfDataColumns.IRREGULAR_18_2_REDUCTION);
		Double irregularidad2 = 
			getIrpfData(rs, IrpfDataColumns.IRREGULAR_18_3_REDUCTION);
		if ( irregularidad1 != null || irregularidad2 != null) {
			Reducciones reducciones = new Reducciones();
			reducciones.setIrregularidad1(toTipoImpositivo(irregularidad1));
			reducciones.setIrregularidad2(toTipoImpositivo(irregularidad2));
			tipoRetenidoEntrada2011.setReducciones(reducciones);
		}
		
		BigDecimal gastosAnuales = toTipoImpositivo(annualSalary.getSocialSecurityContributions());
		tipoRetenidoEntrada2011.setGastosAnuales(gastosAnuales);
		
		Double pensionCompensatoria = getIrpfData(rs, IrpfDataColumns.SPOUSAL_SUPPORT);
		tipoRetenidoEntrada2011.setPensionCompensatoria(toTipoImpositivo(pensionCompensatoria));

		Double anualidadesHijos = getIrpfData(rs, IrpfDataColumns.FOOD_ANNUITY);
		tipoRetenidoEntrada2011.setAnualidadesHijos(toTipoImpositivo(anualidadesHijos));

		// Not required : 
		//tipoRetenidoEntrada2011.setRdtosObtenidosCeutaMelilla(paramRdtosObtenidosCeutaMelilla); 
		
		PagoPrestamosVivienda pagoPrestamosVivienda = null; //getPagoPrestamosVivienda(rs);
		tipoRetenidoEntrada2011.setPagoPrestamosVivienda(pagoPrestamosVivienda);
		
		Regularizacion regularizacion = getRegularizacion(rs, paidSalary, handler);

		tipoRetenidoEntrada2011.setRegularizacion(regularizacion);
		Administration administration = getAdminitratio(rs);
		handler.onTipoRetenidoEntrada2011(administration, tipoRetenidoEntrada2011);
		
	}
	
	private Administration getAdminitratio( ResultSet rs ) throws SQLException {
		Integer economicAgreement = getWorplace(rs, WorkplaceColumns.ECONOMICAGREEMENT);
		Administration values [] = Administration.values();
		return ( economicAgreement == null || economicAgreement < 0 || economicAgreement >= values.length ) ? 
			null : values[economicAgreement];

	}
	
	private void fillDescendiente(List<Descendiente> descendientes, Integer irpfDataId) 
	throws SQLException{
		if ( irpfDataId == null )
			return;
		
		ResultSet rs = null;
		try {
			descendientsStmt.setInt(1, irpfDataId );
			rs = descendientsStmt.executeQuery();
			while ( rs.next() ){
				int birthYear = 
					rs.getInt(IrpfDataDescendientsColumns.BIRTH_YEAR);
				
				Integer disabilityLevelOrdinal = ( Integer )
					rs.getObject(IrpfDataDescendientsColumns.DISABILITY_LEVEL);
				TipoDiscapacidad tipoDiscapacidad = 
					getTipoDiscapacidad(disabilityLevelOrdinal);
				
				int age = getAge(birthYear);
				
				if ( age >= 25  && tipoDiscapacidad == null ){
					continue;
				} // Descendientes mayores de 25 años sin discapacidad no dan derecho a mínimo.
				
				Descendiente descendiente = 
					new Descendiente();
				descendiente.setAñoNacimiento(birthYear);
				
				int adoptionYear = 
					rs.getInt(IrpfDataDescendientsColumns.ADOPTION_YEAR);
				if ( adoptionYear != 0  ) {
					descendiente.setAñoAdopcion(adoptionYear);
				}
				boolean uniqueParent = 
					rs.getBoolean(IrpfDataDescendientsColumns.UNIQUE_PARENT);
				if ( uniqueParent ) {
					descendiente.setComputadoEntero(new ComputadoEntero());
				}
				descendiente.setDiscapacidad(tipoDiscapacidad);
				
				descendientes.add(descendiente);
			}
		}finally {
			if ( rs != null )
				rs.close();
		}
	}
	
	private void fillAscendiente(List<Ascendiente> ascendientes,  Integer irpfDataId ) 
	throws SQLException{
		if ( irpfDataId == null )
			return;
		ResultSet rs = null;
		try {
			ascendientsStmt.setInt(1, irpfDataId );
			rs = ascendientsStmt.executeQuery();
			while ( rs.next() ){
				Ascendiente ascendiente = 
					new Ascendiente();
				int birthYear = 
					rs.getInt(IrpfDataAscendantsColumns.BIRTH_YEAR);
				ascendiente.setAñoNacimiento(birthYear);
				
				Integer convivencia = ( Integer )
					rs.getObject(IrpfDataAscendantsColumns.ANOTHER_DESCENDIENT);
				if ( convivencia == null || convivencia == 0 ) {
					convivencia = 1;
				}
				ascendiente.setConvivencia(convivencia);
				
				Integer disabilityLevelOrdinal = ( Integer )
					rs.getObject(IrpfDataAscendantsColumns.DISABILITY_LEVEL);
				TipoDiscapacidad tipoDiscapacidad = 
					getTipoDiscapacidad(disabilityLevelOrdinal);
				ascendiente.setDiscapacidad(tipoDiscapacidad);
				
				ascendientes.add(ascendiente);
			}
		}finally {
			if ( rs != null )
				rs.close();
		}
	}

	private void prepareStatements () throws SQLException { 
		String mainSql = CriteriaUtilities.toSQLString(criteria, MAIN_SQL);
		this.mainStmt = this.connection.prepareStatement(mainSql);

		java.sql.Date sqlDate = new java.sql.Date(this.date.getTime());
		this.mainStmt.setDate(1, sqlDate); // irpf_data.start_date <= ?
		this.mainStmt.setDate(2, sqlDate); // irpf_data.end_date >= ? 
		this.mainStmt.setDate(3, sqlDate); // irpf_regularization.effective_date = ?
		this.mainStmt.setDate(4, sqlDate); // contract.start_date <= ?
		this.mainStmt.setDate(5, sqlDate); // contract.end_date >= ?

		this.salaryStmt = this.connection.prepareStatement(SALARY_SQL);
		Date newYear = CommonUtil.getYearFirstDay(this.date);
		java.sql.Date sqlNewYear = new java.sql.Date(newYear.getTime());
		this.salaryStmt.setDate(2, sqlNewYear); // start_date >= ?
		this.salaryStmt.setDate(3, sqlDate); // end_date < ?

		this.resultsStmt = this.connection.prepareStatement(RESULTS_SQL);
		this.resultsStmt.setDate(1, sqlDate); // effective_date <= ?
		
		this.ascendientsStmt = this.connection.prepareStatement(ASCENDIENTS_SQL);
		this.descendientsStmt = this.connection.prepareStatement(DESCENDIENTS_SQL);
	}
	
	private void initContractSalaryCalculatorContext() throws ExpressionException, SQLException {
		
		Date startDate = CommonUtil.getMonthFirstDay(date);
		Date endDate = CommonUtil.getYearLastDay(date);
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		
		Criteria ctxCriteria = new Criteria();
		ctxCriteria.addExpression(criteria.getExpression());
		ctxCriteria.addLessThanOrEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.START_DATE , sqlDate);
		ctxCriteria.setOrderByList(criteria.getOrderByList());
		
		
		this.sqlContractSalaryCalculatorContext	=
			new SQLAnnualRemunerationCalculatorContext(connection, startDate, endDate, ctxCriteria);
		
	}
	
	
	private Salary _calculateAnnualRemuneration (int contractId) throws SQLException, ExpressionException, SalaryException {
		
		ResultSet rs = null;
		try {
			
			resultsStmt.setInt(2, contractId); // contract = ?
			
			rs = resultsStmt.executeQuery();
			
			Salary salary = new Salary();

			double irpfBase = 0.00;  
			double socialSecurityContributions = 0.00;
			if ( rs.next() ){
				irpfBase =  
					rs.getDouble(IrpfResultColumns.ANNUAL_REMUNERATION);
				socialSecurityContributions = 
					rs.getDouble(IrpfResultColumns.DEDUCCIBLES_EXPENSES);
			}

			salary.setIrpfBase(irpfBase);
			salary.setSocialSecurityContributions(socialSecurityContributions);
			return salary;
			
			
			
		} finally {
			if ( rs != null ) {
				rs.close();
			}
		}
	}
	private ISalary calculateRemainAnnualRemuneration (int contractId) throws SQLException, ExpressionException, SalaryException {
		
		ContractSalaryCalculator calculator = 
			new ContractSalaryCalculator();
		
		SalaryBuilder salaryBuilder = 
			new SalaryBuilder();
		
		calculator.setSalaryBuilder(salaryBuilder);
		
		calculator.calculate(this.sqlContractSalaryCalculatorContext);
		
		ISalary salary = salaryBuilder.getSalary();

		
		return salary;
	}
	
	private int getAge(int birthYear) {
		Calendar calendar = Calendar.getInstance();
		int currentYear = calendar.get(Calendar.YEAR);
		return currentYear - birthYear;
	}
	
	private String toTipoNif(String nif) {
		RegistryDocument registryDocument = 
			new RegistryDocument(Country.ES, DocumentType.NIF, nif);
		
		return registryDocument.isValid() ? nif : null;		
	}
	
	private BigDecimal toTipoRetencion(Double val) {
		if ( val == null ) {
			return null;
		}
		val = CommonUtil.round(val, 2);
		return BigDecimal.valueOf(val);
	}

	private BigDecimal toTipoImpositivo(Double val) {
		if ( val == null || val < 0.01 ) {
			return null;
		}
		val = CommonUtil.round(val, 2);
		return BigDecimal.valueOf(val);
	}
	
	private String toTipoApellidosyNombre(String str ) {
		if ( str == null || str.isEmpty() )
			return "?";
		
		return str.length() > 40 ? str.substring(0,40): str;
	}
	
	private <T> T get ( ResultSet rs, String table, String col ) 
	throws SQLException {
		return ( T ) rs.getObject(table + "." + col );
	}


	private <T> T getContract ( ResultSet rs, String col ) 
	throws SQLException {
		return ( T ) get(rs, SQLConstants.CONTRACT, col);
	}

	private <T> T getPerson ( ResultSet rs, String col ) 
	throws SQLException {
		return ( T )get(rs, SQLConstants.PERSON, col);
	}

	private <T> T getIrpfData ( ResultSet rs, String col ) 
	throws SQLException {
		return ( T )get(rs, SQLConstants.IRPF_DATA, col);
	}

	private <T> T getWorplace ( ResultSet rs, String col ) 
	throws SQLException {
		return ( T )get(rs, SQLConstants.WORKPLACE, col);
	}


	private <T> T getPersonRegistry ( ResultSet rs, String col ) 
	throws SQLException {
		return ( T )get(rs, PERSON_REGISTRY, col);
	}

	private <T> T getEnterpriseRegistry ( ResultSet rs, String col ) 
	throws SQLException {
		return ( T )get(rs, ENTERPRISE_REGISTRY, col);
	}
	
	
	private <T> T getIrpfResult ( ResultSet rs, String col ) 
	throws SQLException {
		return ( T )get(rs, SQLConstants.IRPF_RESULT, col);
	}

	private <T> T getIrpfRegularization ( ResultSet rs, String col ) 
	throws SQLException {
		return ( T ) get(rs, SQLConstants.IRPF_REGULARIZATION , col);
	}
	
	private Regularizacion getRegularizacion ( ResultSet rs, ISalary paidSalary , Entrada2011Handler handler) 
	throws SQLException {
		Regularizacion regularizacion = null;
		
		Integer id = 
			getIrpfRegularization(rs, IrpfRegularizationColumns.ID);
		
		if ( id != null ){
			regularizacion = new Regularizacion();
			
			Integer reason = 
				getCausa(( Integer ) getIrpfRegularization(rs, IrpfRegularizationColumns.REASON));
			List<Integer> reasons = 
				new ArrayList<Integer>(1);
			reasons.add(reason);
			regularizacion.setCausa(reasons);
			
			Double paidRemuneration = 
				getIrpfRegularization(rs, IrpfRegularizationColumns.PAID_REMUNERATION);
			
			BigDecimal retribSatisfechas = toTipoImpositivo(paidRemuneration);
			if ( retribSatisfechas == null ) {
				return null;
			} // Las Retribuciones ya satisfechas con anterioridad a la regularización son obligatorias
			//System.out.println("Retribuciones ya satisfechas " + retribSatisfechas);
			regularizacion.setRetribSatisfechas(retribSatisfechas);
			
			Double paidIrpf = 
				getIrpfRegularization(rs, IrpfRegularizationColumns.PAID_IRPF);
			regularizacion.setRetencionPracticada(toTipoImpositivo(paidIrpf));
			//System.out.println("Retenciones ya paracticadas " + paidIrpf);
			

			if ( reason == 11 /*IrpfRegularizationReason.OTHER */ ) {
				regularizacion.setTipoRetencion(toTipoRetencion(0.00));
				return regularizacion;
			}
			
			Double startMinPersonal = 
				getIrpfRegularization(rs, IrpfRegularizationColumns.PRIOR_MINIMUN_PERSONAL_FAMILY);
			BigDecimal minimoPersonalFamiliarInicial = toTipoImpositivo(startMinPersonal);
			
			if ( minimoPersonalFamiliarInicial != null ) {
				regularizacion.setMinimoPersonalFamiliarInicial(minimoPersonalFamiliarInicial);
			}

			Double startAnnualRemuneration = 
				getIrpfRegularization(rs, IrpfRegularizationColumns.PRIOR_ANNUAL_REMUNERATION);
			regularizacion.setRetribAnualesIniciales(toTipoImpositivo(startAnnualRemuneration));
			Double startAnnualIrpf = 
				getIrpfRegularization(rs, IrpfRegularizationColumns.PRIOR_ANNUAL_IRPF);
			regularizacion.setRetencionAnualInicial(toTipoImpositivo(startAnnualIrpf));

			Double irpfBase = 
				getIrpfRegularization(rs, IrpfRegularizationColumns.PRIOR_BASE_IRPF);
			regularizacion.setBaseRetencion(toTipoImpositivo(irpfBase));
			Double startIrpf = 
				getIrpfRegularization(rs, IrpfRegularizationColumns.PRIOR_IRPF);
			regularizacion.setTipoRetencion(toTipoRetencion(startIrpf));
			
			// TODO : Suppose that no.
			//regularizacion.setResidenciaInicialCeutaMelilla()
			
			
			Integer ordinal = 
				getIrpfRegularization(rs, IrpfRegularizationColumns.PRIOR_DEDUCT_HOME_LOAN);
			PagoPrestamosVivienda pagoPrestamosVivienda = getPagoPrestamosVivienda(ordinal);
			if ( pagoPrestamosVivienda != null ) {
				Double deductHomeLoanAmount = 
					getIrpfRegularization(rs, IrpfRegularizationColumns.PRIOR_DEDUCT_HOME_LOAN_AMOUNT);
				MinoracionPrestamosVivienda minoracionPrestamosVivienda = 
					new MinoracionPrestamosVivienda();
				if ( pagoPrestamosVivienda.getRegimenGeneral() != null ) {
					minoracionPrestamosVivienda.setRegimenGeneral(createElementObject("RegimenGeneral"));
				}
				else {
					minoracionPrestamosVivienda.setRegimenTransitorio(createElementObject("RegimenTransitorio"));
				}
				minoracionPrestamosVivienda.setImporteMinoracion(toTipoImpositivo(deductHomeLoanAmount));
				
				regularizacion.setMinoracionPrestamosVivienda(minoracionPrestamosVivienda);
			}
		} // We have saved regularization data...
		else {
			
		}
		
		return regularizacion;
	}
	
	private Integer getCausa(Integer ordinal) throws SQLException {
		
		IrpfRegularizationReason IrpfRegularizationReasons [] = 
			IrpfRegularizationReason.values();
		if ( ordinal != null && 
				ordinal < IrpfRegularizationReasons.length) {
			return ordinal + 1; // TODO: Very tricky The order in AEAT and aon must be identical 
		}else {
			return null;
		}
	}

	private int getContrato ( ResultSet rs  ) throws SQLException {
		// TIPOS DE CONTRATO 
		// 1 General
		// 2 Duración menor a un año, excepto relaciones esporádicas ( peonadas o jornales diarios )
		// 3 Relaciones laborales especiales de caracter dependiente ( salvo penados y discapacitados )
		// 4 Relaciones esporádicas propias de retribuciones por peonadas o jornales diarios
		
		int contrato = 1; 

		Integer contractTypeOrdinal = 
			getIrpfData(rs, IrpfDataColumns.CONTRACT_TYPE);
		if (contractTypeOrdinal != null ) {
			IrpfContractType types [] = IrpfContractType.values();
			if ( contractTypeOrdinal >= 0 
					&& contractTypeOrdinal < types.length ){
				contrato = contractTypeOrdinal + 1;
			}
		}
		else {
			Date contractStart = getContract(rs, ContractColumns.START_DATE);
			Date contractEnd = getContract(rs, ContractColumns.END_DATE);
			if ( isLessThanOneYear(contractStart, contractEnd ) ) {
				contrato = 2;
			}
		}
		
		return contrato;
	}
	
	private SituacionFamiliar getSituacionFamiliar (ResultSet rs, List<Descendiente> descendientes) throws SQLException {
		SituacionFamiliar situacionFamiliar = 
			new SituacionFamiliar();
		
		Integer familySituationOrdinal = 
			getIrpfData(rs, IrpfDataColumns.FAMILY_SITUATION);
		FamilySituation familySituation = FamilySituation.OTHER;
		
		FamilySituation familySituations [] = FamilySituation.values();
		if ( familySituationOrdinal != null && 
				familySituationOrdinal < familySituations.length  ) {
			familySituation = FamilySituation.values()[familySituationOrdinal];
		}
		
		switch (familySituation) {
		case NO_MARRIED_WITH_SONS:
			for (Descendiente descendiente: descendientes) {
				int age = getAge(descendiente.getAñoNacimiento());
				if ( age < 18 ){
					situacionFamiliar.setSituacion1(new Situacion1());
					break;
				} // TODO: La situación familiar "1" exige que el contribuyente tenga descendientes que den derecho a mínimo.
			}
			if ( situacionFamiliar.getSituacion1() == null ) {
				situacionFamiliar.setSituacion3(new Situacion3());
			}
			break;
		case MARRIED:
			Situacion2 situacion2 = new Situacion2();
			String nif = toTipoNif(( String ) getIrpfData(rs, IrpfDataColumns.SPOUSE_DOCUMENT));
			situacion2.setNifConyuge(nif != null ? nif : "66666666Q");
			situacionFamiliar.setSituacion2(situacion2);
			break;
		default:
			situacionFamiliar.setSituacion3(new Situacion3());
			break;
		}
		
		return situacionFamiliar;
		
	}
	
	private TrabajadorActivo getTrabajadorActivo ( ResultSet rs, int age) throws SQLException {
		TrabajadorActivo trabajadorActivo = 
			new TrabajadorActivo();
		
		int contrato = getContrato(rs); 
		trabajadorActivo.setContrato(contrato);
		
		// Movilidad geográfica
		Date today = new Date ( Calendar.getInstance().getTimeInMillis() );
		Date movingDate = getIrpfData(rs, IrpfDataColumns.MOVING_DATE);
		if ( movingDate != null && !movingDate.after(today)) {
			MovilidadGeografica movilidadGeografica = 
				new MovilidadGeografica();
			trabajadorActivo.setMovilidadGeografica(movilidadGeografica);
		}
		
		// Prolongación actividad laboral
		Boolean labourProlongation = getIrpfData(rs, IrpfDataColumns.LABOUR_PROLONGATION);
		
		
		if ( labourProlongation != null && age > 65 ) {
			ProlongacionLaboral prolongacionLaboral = 
				new ProlongacionLaboral();
			trabajadorActivo.setProlongacionLaboral(prolongacionLaboral);
		}
		return trabajadorActivo ;
	}
	
	private TipoDiscapacidad getDiscapacidad ( ResultSet rs ) throws SQLException {
		
		
		Integer disabilityLevelOrdinal = 
			getIrpfData(rs, IrpfDataColumns.DISABILITY_LEVEL);
		
		
		return getTipoDiscapacidad(disabilityLevelOrdinal) ;
	}
	
	private TipoDiscapacidad getTipoDiscapacidad ( Integer disabilityLevelOrdinal ) 
		throws SQLException {
		TipoDiscapacidad tipoDiscapacidad = null;

		DisabilityLevel disabilityLevels [] = 
			DisabilityLevel.values();
		if ( disabilityLevelOrdinal != null && 
				disabilityLevelOrdinal < disabilityLevels.length) {
			tipoDiscapacidad = new TipoDiscapacidad();
			DisabilityLevel disabilityLevel = 
				disabilityLevels[disabilityLevelOrdinal];
			switch (disabilityLevel) {
			case GT_EQ_33_LT_65:
				tipoDiscapacidad.setGrado1(new Grado1());
				break;
			case GT_EQ_33_LT_65_DEPENDENCE:
				Grado1 grado1 = new Grado1();
				grado1.setMovilidadReducida(new MovilidadReducida());
				tipoDiscapacidad.setGrado1(grado1);
			case GT_EQ_65:
				tipoDiscapacidad.setGrado2(new Grado2());
			}
		}
		
		return tipoDiscapacidad ;
		
	}

		
	private PagoPrestamosVivienda getPagoPrestamosVivienda( ResultSet rs ) throws SQLException {
		
		Integer ordinal = 
			getIrpfData(rs, IrpfDataColumns.DEDUCT_HOME_LOAN);
		
		return getPagoPrestamosVivienda(ordinal) ;
	}

	private PagoPrestamosVivienda getPagoPrestamosVivienda( Integer ordinal) 
	throws SQLException {
		PagoPrestamosVivienda pagoPrestamosVivienda = null;
		
		IrpfDeductHomeLoan irpDeductHomeLoans [] = 
			IrpfDeductHomeLoan.values();
		if ( ordinal != null && 
				ordinal < irpDeductHomeLoans.length) {
			pagoPrestamosVivienda = new PagoPrestamosVivienda();
			IrpfDeductHomeLoan deductHomeLoan = 
				irpDeductHomeLoans[ordinal];
			switch (deductHomeLoan) {
			case GENERAL_REGIME:
				pagoPrestamosVivienda.setRegimenGeneral(new RegimenGeneral());
				break;
			case TRANSIENT_REGIME:
				pagoPrestamosVivienda.setRegimenTransitorio(new RegimenTransitorio());
			}
		}
		
		return pagoPrestamosVivienda ;
	}

	private Salary getRetribucionesAnuales(ResultSet rs, ISalary paidSalary) throws SQLException, ExpressionException, SalaryException {
		Integer contractId = getContract(rs, ContractColumns.ID);
		return _calculateAnnualRemuneration(contractId);
		
	}

	private Salary _getRetribucionesAnuales(ResultSet rs, ISalary paidSalary) throws SQLException, ExpressionException, SalaryException {
		
		Salary salary = new Salary(); 
		

		Double annualRemuneration = 
			getIrpfData(rs, IrpfDataColumns.ANNUAL_REMUNERATION);
		if ( annualRemuneration != null && annualRemuneration > 0.00){ 
			salary.setIrpfBase(annualRemuneration);
		}
		
		Double deducciblesExpenses = 
			getIrpfData(rs, IrpfDataColumns.DEDUCCIBLES_EXPENSES);
		if ( deducciblesExpenses != null && deducciblesExpenses > 0.00){ 
			salary.setSocialSecurityContributions(deducciblesExpenses);
		}
		
		if ( salary.getIrpfBase() != null 
				&& salary.getSocialSecurityContributions() != null ){
			return salary;
		}
		
		
		Integer contractId = getContract(rs, ContractColumns.ID);
		
		ISalary remainSalary = 
			calculateRemainAnnualRemuneration(contractId);
		if ( salary.getIrpfBase() == null ) {
			
			salary.setIrpfBase(remainSalary.getIrpfBase() + 
					paidSalary.getIrpfBase());
		}
		
		if ( salary.getSocialSecurityContributions() == null ) {
			salary.setSocialSecurityContributions(remainSalary.getSocialSecurityContributions() + 
					paidSalary.getSocialSecurityContributions());
		}
		
		
		return salary;
	}
	
	// TODO: use this
	private Salary getRetribSatisfechas(ResultSet contractRs) throws SQLException, ExpressionException, SalaryException {
		ResultSet salaryRs = null;
		try {
			
			Integer contractId = getContract(contractRs, ContractColumns.ID);
			
			salaryStmt.setInt(1, contractId); // contract = ?
			salaryRs = salaryStmt.executeQuery();
			
			Salary salary = new Salary(); 

			if ( !salaryRs.next() ) {
				return salary;
			}
			
			
			Double irpfBase = 
				salaryRs.getDouble(SalaryColumns.IRPF_BASE);
			salary.setIrpfBase(irpfBase);
			
			Double totalIrpf = 
				salaryRs.getDouble( SalaryColumns.TOTAL_IRPF);
			salary.setTotalIrpf(totalIrpf);
			
			Double totalSS = 
				salaryRs.getDouble( SalaryColumns.SOCIAL_SECURITY_CONTRIBUTIONS);
			salary.setSocialSecurityContributions(totalSS);

			return salary;

		} finally {
			if ( salaryRs != null ) {
				salaryRs.close();
			}
		}
	}
	
	
	private static Object createElementObject(String tagName ) {
		Object obj = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			
			obj = document.createElement(tagName);
		} catch ( ParserConfigurationException e ) {
			
		}
		return obj;
	}
	
	private static class DelegatePaymentsCollection 
		extends AbstractCollection<IContractPayment> {
		
		private Collection<IContractPayment> 	payments;
		private int								end;
		private int								start;
		
		
		public DelegatePaymentsCollection(Collection<IContractPayment> payments, Date start, Date end) {
			this.payments = payments;
			this.start = CommonUtil.getMonth(start);
			this.end = CommonUtil.getMonth(end);
		}
		
		@Override
		public Iterator<IContractPayment> iterator() {
			return new DelegatePaymentsIterator(payments.iterator(), start, end );
		}

		@Override
		public int size() {
			return payments.size();
		}
		
	}
	
	private static class DelegatePaymentsIterator 
		implements Iterator <IContractPayment> {
		
		private int						end;
		private int						start;
		private Iterator <IContractPayment> payments;
		
		public DelegatePaymentsIterator(Iterator <IContractPayment> payments, int start, int end) {
			this.start = start;
			this.end = end;
			this.payments = payments;
		}
		
		@Override
		public boolean hasNext() {
			return payments.hasNext();
		}

		@Override
		public IContractPayment next() {
			return new DelegateContractPayment(payments.next()){
				@Override
				public SalaryType getSalaryType() {
					return SalaryType.SALARY;
				}
				
				@Override
				public Month getMonth() {
					Month month =  super.getMonth();
					if ( month == null )
						return null;
					
					int ordinal = month.ordinal();
					
					if (ordinal < start)
						return month;
					
					if (ordinal > end)
						return month;
					
					return   null;
				}
				
				@Override
				public String getDescription() {
					return null;
				}
				
				@Override
				public boolean isDescriptionDecorable() {
					return false;
				}
			};
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		
	}
	
	
	private static class SQLAnnualRemunerationCalculatorContext extends SQLContractSalaryCalculatorContext {
		
		private static final String SALARY_PAYMENT_SQL =
			"SELECT *"
			+" FROM salary"
			+",salary_payment"
			+" LEFT JOIN  payment_concept" 							// LEFT JOIN: payment_concept puede ser NULL
			+" ON salary_payment.payment_concept = payment_concept.code"	
			+" WHERE salary.id = salary_payment.salary"
			+" AND salary.type = ? "
			+" AND salary.contract = ? "
			+" AND salary.end_date <= ? "
			+" AND salary.start_date >= ? ";
		
		private PreparedStatement salaryPaymentsStmt;
		
		public SQLAnnualRemunerationCalculatorContext(Connection connection, Date startDate, Date endDate, Criteria criteria) 
		throws ExpressionException, SQLException {
			super ( connection, startDate, endDate, endDate , criteria);
			initSalaryPayment(connection);
		}
		
		@Override
		protected Long getLeaveDays(Period p ) {
			return ( long ) 0; //leaveLoader.getLeavesDays(p);
		}

		@Override
		public boolean next() throws SQLException, ExpressionException {
			boolean next = super.next();
			
			redefine(getExpressionContext());
			
			return next;
		}
		
		@Override
		public Collection<IContractBonus> getContractBonus()
				throws AonException {
			return Collections.emptyList();
		}

		@Override
		public Collection<IContractEmbargo> getContractEmbargos()
				throws AonException {
			return Collections.emptyList();
		}
		
		@Override
		public Collection<IContractCost> getContractCosts() throws AonException {
			return Collections.emptyList();
		}
		
		@Override
		public Collection<IContractPayment> getContractPayments()
				throws AonException {
			Collection<IContractPayment>  salaryPayments = getSalaryPayments();
			Collection<IContractPayment>  contractPayments =
				new DelegatePaymentsCollection( super.getContractPayments() , getStartDate(), getEndDate());
			return new HierarchyMonthPayments(contractPayments.iterator(), salaryPayments.iterator() );
			
 			//return new DelegatePaymentsCollection( super.getContractPayments() , getStartDate(), getEndDate());
		}
		
		@Override
		public void close() throws SQLException {
			super.close();
			if ( salaryPaymentsStmt != null ){
				salaryPaymentsStmt .close();
			}
				
		}
		
		// TODO: This FIX over MOTNH_DAYS, must be present at super class SQLContractSalaryCalculatorContext
		private void redefine(ExpressionContext ctx ) {
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(getStartDate()) ;
			
			int startMonth = calendar.get(Calendar.MARCH) ;
			int endMonth = CommonUtil.getMonth(getEndDate()) ;

			for ( int month = startMonth ; month <= endMonth ; month++){
				calendar.set(Calendar.MONTH, month);

				calendar.set(Calendar.DAY_OF_MONTH, 1);
				Date start = calendar.getTime();
				
				int monthDays = 
					calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				calendar.set(Calendar.DAY_OF_MONTH, monthDays);
				Date end = calendar.getTime();
				
				ctx.addVariable(ContractVariables.MONTH_DAYS, monthDays, start, end);
			}
			
		}
		
		private void initSalaryPayment(Connection connection) throws SQLException {
			salaryPaymentsStmt = connection.prepareStatement(SALARY_PAYMENT_SQL);
			salaryPaymentsStmt.setInt(1, SalaryType.SALARY.ordinal()); // salary.type = ?
		}
		
		private Collection<IContractPayment> getSalaryPayments() throws AonException {
			ResultSet rs = null;
			try {
				Collection<IContractPayment> payments  = null;
				
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(getStartDate());
				calendar.add(Calendar.YEAR, -1);
				java.sql.Date startDate = new java.sql.Date(calendar.getTimeInMillis()); 
				
				calendar.setTime(getEndDate());
				calendar.add(Calendar.YEAR, -1);
				java.sql.Date endDate = new java.sql.Date(calendar.getTimeInMillis());
				
				salaryPaymentsStmt.setInt(2, getId()) ; 	// salary.contract = ?
				salaryPaymentsStmt.setDate(3, endDate) ; 	// salary.end_date <= ? 
				salaryPaymentsStmt.setDate(4, startDate) ; 	// salary.start_date >= ?  
				
				rs = salaryPaymentsStmt.executeQuery();
				
				payments = new SalaryPayments(rs);
				
				return payments;
			} catch ( SQLException e ) {
				throw new AonException(e);
			} 
		}
		
		
	}
	
	private static boolean isLessThanOneYear(Date start, Date end ) {
		if ( end == null ) {
			return false;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(start);
		calendar.add(Calendar.YEAR, 1);

		Date nextYear = calendar.getTime();
		
		return end.before(nextYear);
	}
	
	private static class SalaryPayments extends SQLCollection<IContractPayment> implements IContractPayment{
		
		public SalaryPayments( ResultSet rs ) {
			super(rs);
		}
		
		
		@Override
		public IContractPayment next() {
			return this;
		}

		@Override
		public String getExpression() {

			ResultSet rs = getResultSet();
			try {
				double amount;
				amount = rs.getDouble(SalaryPaymentColumns.AMOUNT);
				return String.valueOf (amount);
			} catch (SQLException e) {
				return null;
			}
		}

		@Override
		public PaymentType getType() {
			return PaymentType.SALARY_SUPPLEMENTS;
		}

		@Override
		public String getName() {
			return getString(PaymentConceptColumns.CODE);
		}


		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public Month getMonth() {
			return null;
		}

		@Override
		public Date getStartDate() {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(getDate(SalaryColumns.START_DATE));
			calendar.add(Calendar.YEAR, 1 );
			return calendar.getTime();			
		}

		@Override
		public Date getEndDate() {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(getDate(SalaryColumns.END_DATE));
			calendar.add(Calendar.YEAR, 1 );
			return calendar.getTime();			
		}

		@Override
		public String getIrpfExpression() {
			return getString(PaymentConceptColumns.IRPF_EXPRESSION);
		}

		@Override
		public String getQuoteExpression() {
			return getString(PaymentConceptColumns.QUOTE_EXPRESSION);
		}

		@Override
		public SalaryType getSalaryType() {
			return SalaryType.SALARY;
		}

		@Override
		public boolean isDescriptionDecorable() {
			return false;
		}
		
		@Override
		public double getAmount() {
			throw new UnsupportedOperationException();
		}

		@Override
		public ExpressionScope getScope() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isReadOnly() {
			throw new UnsupportedOperationException();
		}
		
		
		
	}
	
	private static class Entrada2011Builder implements Entrada2011Handler {
		
		private AEATRetencionesEntrada2011 entrada2011;
		
		private TipoRetenedorEntrada2011 retenedorEntrada2011;
		
		public AEATRetencionesEntrada2011 getEntrada2011() {
			return entrada2011;
		}
		
		@Override
		public void onAEATRetencionesEntrada2011(
				AEATRetencionesEntrada2011 entrada2011) {
			this.entrada2011 = entrada2011;
		}

		@Override
		public void onTipoRetenedorEntrada2011(
				TipoRetenedorEntrada2011 retenedorEntrada2011) {
			
			if ( this.retenedorEntrada2011 != null && 
					this.retenedorEntrada2011.getRetenido().size() > 0 ){
				List<TipoRetenedorEntrada2011> retenedor = 
					entrada2011.getRetenedor();
				retenedor.add(this.retenedorEntrada2011);
			}

			this.retenedorEntrada2011 = 
				retenedorEntrada2011;
		}

		@Override
		public void onTipoRetenidoEntrada2011(Administration administration,
				TipoRetenidoEntrada2011 retenidoEntrada2011) {
			
			List<TipoRetenidoEntrada2011> retenido =  
				retenedorEntrada2011.getRetenido();
			retenido.add(retenidoEntrada2011);
		}
		
		
		
	}

}
