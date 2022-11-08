package com.esferalia.aon.dsi.nominas;

import static com.esferalia.aon.dsi.nominas.Traspaso.getParentDomain;
import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import org.jooq.DSLContext;

import com.esferalia.aon.dsi.nominas.dao.ConvenioDAO;
import com.esferalia.aon.dsi.nominas.model.Categoria;
import com.esferalia.aon.dsi.nominas.model.Complemento;
import com.esferalia.aon.dsi.nominas.model.Concepto;
import com.esferalia.aon.dsi.nominas.model.Convenio;
import com.esferalia.aon.dsi.nominas.model.Paga;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class TraspasoConvenios {

	private static DSLContext ctx;
	
	// Fecha por defecto que pone AON en los convenios, cuando creas uno nuevo (01/01/2015)
	private static final Date DEFAULT_DATE = AonDateUtils.getDate(2015, 0, 1);

	private static String conceptosPag = ""; // Conceptos para las pagas extras por días
	private static String conceptosEnf = ""; // Conceptos para los complementos por enfermedad
	private static String conceptosAcc = ""; // Conceptos para las complementos por accidente
	
	private static LinkedList<Convenio> convenios = null;

	// Lista de las expresiones de la antiguedad de Omega, se las distintas categorias
	private static ArrayList<String> listaExpresionesAntig = new ArrayList<String>();

	public static void execute(Connection dsiConn, AONContext aonContext) throws SQLException {
		
		ctx = aonContext.getDslContext();

		convenios = ConvenioDAO.select(dsiConn);

		int contador = 0;

		for (Convenio convenio : convenios) {

			contador++;

			Traspaso.info(convenio.toString());

			// Hay que comprobar si el convenio ya existe en AON y si existe, borrarlo
			checkAgreement(convenio.getCodigo());

			// Fecha que se grabará en todas aquellas tablas del convenio que llevan startDate (niveles, categorías, conceptos, etc.)
			Date startDate = convenio.getFecha() != null ? convenio.getFecha() : DEFAULT_DATE;

			// DATOS GENERALES DEL CONVENIO

			// Nombre del Convenio y Código según TC2 (tabla agreement de AON)
			
			int agreement = ctx.insertInto(AGREEMENT)
								.set(AGREEMENT.DOMAIN, getParentDomain())									
								.set(AGREEMENT.DESCRIPTION, convenio.getNombre()+" [" + convenio.getCodigo() + "]")
								.set(AGREEMENT.SS_NUMBER, convenio.getTc2conv())
								.returning(AGREEMENT.ID).fetchOne().getId();

			// Código de Convenio Omega en agreement_data
			ctx.insertInto(AGREEMENT_DATA)
				.set(AGREEMENT_DATA.DOMAIN, getParentDomain())
				.set(AGREEMENT_DATA.AGREEMENT, agreement)
				.set(AGREEMENT_DATA.NAME, "CODIGO_OMEGA")
				.set(AGREEMENT_DATA.EXPRESSION, getCodigoOmega(convenio.getCodigo()))
				.set(AGREEMENT_DATA.START_DATE, AonDateUtils.toSql(startDate))
				.execute();

			// Horas convenio en agreement_data, si estan cumplimentadas y son distintas de 40
			double horasConvenio = convenio.getHorsem() != 0 ? convenio.getHorsem() : 40;

			if (horasConvenio != 40) {
				ctx.insertInto(AGREEMENT_DATA)
			        .set(AGREEMENT_DATA.DOMAIN, getParentDomain())
					.set(AGREEMENT_DATA.AGREEMENT, agreement)
					.set(AGREEMENT_DATA.NAME, "HORAS_CONVENIO")
					.set(AGREEMENT_DATA.EXPRESSION, Double.toString(horasConvenio))
					.set(AGREEMENT_DATA.START_DATE, AonDateUtils.toSql(startDate))
					.execute();
			}

			// Preparamos los datos del convenio (conceptos, pagas, etc.) para el traspaso
			prepararConvenio(convenio);

			// CATEGORIAS DEL CONVENIO
			
			listaExpresionesAntig.clear();
			
			for (Categoria categoria : convenio.getCategorias()) {

				Traspaso.info(categoria.toString());

				// DATOS GENERALES DE LA CATEGORIA

				// Crear agreement_level y agreement_level_category, se crea un registro en
				// ambos con la descripción de la categoria de Omega
				int agreementLevel = ctx.insertInto(AGREEMENT_LEVEL)
										.set(AGREEMENT_LEVEL.DOMAIN, getParentDomain())
										.set(AGREEMENT_LEVEL.AGREEMENT, agreement)											
										.set(AGREEMENT_LEVEL.DESCRIPTION, categoria.getNomcat()+" [" + categoria.getCodcat() + "]")
										.returning(AGREEMENT_LEVEL.ID)
										.fetchOne()
										.getId();

				ctx.insertInto(AGREEMENT_LEVEL_CATEGORY)
					.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, getParentDomain())
					.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevel)
					.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, categoria.getNomcat())
					.execute();

				// Código de Categoria Omega en agreement_level_data
				ctx.insertInto(AGREEMENT_LEVEL_DATA)
					.set(AGREEMENT_LEVEL_DATA.DOMAIN, getParentDomain())
					.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, agreementLevel)
					.set(AGREEMENT_LEVEL_DATA.NAME, "CODIGO_OMEGA")
					.set(AGREEMENT_LEVEL_DATA.EXPRESSION, getCodigoOmega(categoria.getCodcat()))
					.set(AGREEMENT_LEVEL_DATA.START_DATE, AonDateUtils.toSql(startDate))
					.execute();

				// Horas convenio, si estan cumplimentadas y son distintas de las del convenio
				double horasCategoria = categoria.getHorsem() != 0 ? categoria.getHorsem() : 40;

				if (horasCategoria != horasConvenio) {
					ctx.insertInto(AGREEMENT_LEVEL_DATA)
				  		.set(AGREEMENT_LEVEL_DATA.DOMAIN, getParentDomain())
						.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, agreementLevel)
						.set(AGREEMENT_LEVEL_DATA.NAME, "HORAS_CONVENIO")
						.set(AGREEMENT_LEVEL_DATA.EXPRESSION, Double.toString(horasCategoria))
						.set(AGREEMENT_LEVEL_DATA.START_DATE, AonDateUtils.toSql(startDate))
						.execute();
				}

				// CONCEPTOS DE LA CATEGORIA

				// Si no hay concepto con clave 02 (antiguedad) y hay tabla de antiguedad, entonces
				// se añade un concepto con clave 02, para que se añada la antiguedad a AON.
				if (!categoria.existeConcepto("02") && !categoria.getAonFormulaAntiguedad().isEmpty()) {
					categoria.getConceptos().add( new Concepto()
													.setClave("02")
								                	.setNombre("ANTIGÜEDAD")
								                	.setSs("S")
								                	.setIrpf("S")
								                	.setPag("")
								                	.setEnf("")
								                	.setAcc("")
								                	.setTipo("G")
								                	.setClaveCRA("0001")
								                	.setImporte(0)
								                	.setCobro(""));
				}
				
				// Añadir concepto al convenio y variable a la categoria
				for (Concepto conceptoCat : categoria.getConceptos()) {

					// Conceptos de descuentos (claves 70, 71, 80, 81, 90 y 91), se ignoran porque
					// en AON no se pueden poner descuentos en el convenio
					if (conceptoCat.esDescuento()) {
						continue;
					}

					// Añadir concepto al convenio (agreement_payment)					
					addAgreementPayment(agreement, convenio, categoria, conceptoCat, startDate, (byte) 0);

					// Añadir variable al nivel/categoria (agreement_level_data)
					
					if ("02".equals(conceptoCat.getClave()) && !categoria.getAonFormulaAntiguedad().isEmpty()) {
						
						// Concepto de Antiguedad que lleva tabla de antiguedad asociada						
						String variable = categoria.getAonSeniorityVariable();
						String expression = categoria.getAonFormulaAntiguedad();
						
						if (expression.contains("IMPORTE_ANTIGUEDAD")) {
							if ("P".equals(categoria.getAntiguedades().get(0).getTipo()))
								expression = Double.toString(categoria.getAntiguedades().get(0).getImporte()/100);
							else 
								expression = Double.toString(categoria.getAntiguedades().get(0).getImporte());
						}
						else if (!convenio.esAntiguedadDistinta())
							variable = "";
						
					    if (!variable.isEmpty()) {
							// Comprobar longitud mayor de 128, si es así se quitan los espacios a ver si cabe
							// si aun así no cabe, se trunca
							if (expression.length() > 128)
								expression = expression.replace(" ", "");
							if (expression.length() > 128)
								expression = AonStringUtils.left(expression,128);
							
							// Si no todas las formulas caben en las expresiones de las variables, se habrán creado
							// varios conceptos de antiguedad, con una variable (APLICAR...), de valor 0 o 1
							if (convenio.maximaLongitudAntiguedad() > 128) {
								String exp = categoria.getAonSeniorityExpression().replace(categoria.getAonSeniorityVariable(), categoria.getAonFormulaAntiguedad());
								variable = "APLICAR_ANTIG_" + (listaExpresionesAntig.indexOf(exp)+1);
								expression = "1";
							}
							
							ctx.insertInto(AGREEMENT_LEVEL_DATA)
								.set(AGREEMENT_LEVEL_DATA.DOMAIN, getParentDomain())
								.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, agreementLevel)
								.set(AGREEMENT_LEVEL_DATA.NAME, variable)								
								.set(AGREEMENT_LEVEL_DATA.EXPRESSION, expression) 
								.set(AGREEMENT_LEVEL_DATA.START_DATE, AonDateUtils.toSql(startDate))
								.execute();
					    }
						
					}
					else {
						// Resto de conceptos añadimos la variable, si el importe es distinto de cero y el cobro está cumplimentado o es el concepto 75
						if (conceptoCat.getImporte() != 0 && (AonStringUtils.isNotBlank(conceptoCat.getCobro()) || "75".equals(conceptoCat.getClave())))
							ctx.insertInto(AGREEMENT_LEVEL_DATA)
								.set(AGREEMENT_LEVEL_DATA.DOMAIN, getParentDomain())
								.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, agreementLevel)
								.set(AGREEMENT_LEVEL_DATA.NAME, conceptoCat.getAonVariable())
								.set(AGREEMENT_LEVEL_DATA.EXPRESSION, Double.toString(conceptoCat.getImporte()))
								.set(AGREEMENT_LEVEL_DATA.START_DATE, AonDateUtils.toSql(startDate))
								.execute();
					}
				}

				// PAGAS EXTRAS DE LA CATEGORIA
				
				for (Paga pagaCat : categoria.getPagas()) {

					// Controla si la paga es distinta en las categorias del convenio
					boolean pagaDistinta = convenio.esPagaDistinta(pagaCat.getMes());
					
					// Crear el concepto en agreement_payment
					addAgreementPayment(agreement, pagaCat, startDate, pagaDistinta);

					// Añadir la variable a la categoría, si es el caso
					if (pagaCat.getImporte() != 0 && !pagaCat.getAonVariable(pagaDistinta).isEmpty()) {
						ctx.insertInto(AGREEMENT_LEVEL_DATA)
							.set(AGREEMENT_LEVEL_DATA.DOMAIN, getParentDomain())
							.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, agreementLevel)
							.set(AGREEMENT_LEVEL_DATA.NAME, pagaCat.getAonVariable(pagaDistinta))
							.set(AGREEMENT_LEVEL_DATA.EXPRESSION, pagaCat.esPorDias() ? "1" : Double.toString(pagaCat.getImporte()))
							.set(AGREEMENT_LEVEL_DATA.START_DATE, AonDateUtils.toSql(startDate))
							.execute();						
					}
				}
				
			}
			
			// COMPLEMENTOS ENFERMEDAD/ACCIDENTE DEL CONVENIO 
			
			// Añadir concepto al convenio, por cada línea de la tabla de complementos enf/acc de Omega
			LinkedList<Complemento> complementos = Traspaso.comprobarComplementosEnfAcc(convenio.getComplementos(), convenio.getTipoComple());
			for (Complemento complemento : complementos) {
				if (AonStringUtils.isNotBlank(complemento.getTipo()) && AonStringUtils.isNotBlank(complemento.getCalculo())) {
					if (complemento.getTipo().equals("E") || complemento.getTipo().equals("A"))
						addAgreementPayment(agreement, complemento, startDate);					
				}				
			}
		}

		Traspaso.info("[TOTAL CONVENIOS = " + contador + "]");

	}

	// Devuelve el código de Omega (convenio o categoría), tal y como se debe grabar en AON
	// Es necesario grabarlo entre comillas, porque si no da error al cargar el convenio, si no
	// lo grabamos como si fuera una cadena
	public static String getCodigoOmega(String codigo) {
		return '"' + codigo + '"';
	}

	private static void checkAgreement(String codigoConvenio) {

		Integer idConvenio = ctx.select(AGREEMENT_DATA.AGREEMENT)
								.from(AGREEMENT_DATA)
								.where(AGREEMENT_DATA.DOMAIN.eq(getParentDomain())
								.and(AGREEMENT_DATA.NAME.equal("CODIGO_OMEGA")
								.and(AGREEMENT_DATA.EXPRESSION.equal(getCodigoOmega(codigoConvenio)))))
								.fetchOne(AGREEMENT_DATA.AGREEMENT);

		if (idConvenio != null) {

			// CONTRACTOS (Se pone a null el campo de contratos donde se esten usando las categorias del convenio que se va a borrar)
			ctx.update(CONTRACT)
				.set(CONTRACT.AGREEMENT_LEVEL, (Integer) null)
				.where(CONTRACT.AGREEMENT_LEVEL.in(ctx.select(AGREEMENT_LEVEL.ID).from(AGREEMENT_LEVEL).where(AGREEMENT_LEVEL.AGREEMENT.eq(idConvenio))))
				.execute();
			
			// CENTROS DE TRABAJO (Se pone a null el campo de centros de trabajo donde se este usando el convenio que se va a borrar)
			ctx.update(PAYROLL_WORKPLACE)
				.set(PAYROLL_WORKPLACE.AGREEMENT, (Integer) null)
				.where(PAYROLL_WORKPLACE.AGREEMENT.eq(idConvenio))
				.execute();

			// AGREEMENT_LEVEL_DATA
			ctx.delete(AGREEMENT_LEVEL_DATA)
				.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.in(ctx.select(AGREEMENT_LEVEL.ID).from(AGREEMENT_LEVEL).where(AGREEMENT_LEVEL.AGREEMENT.eq(idConvenio))))
				.execute();

			// AGREEMENT_LEVEL_CATEGORY
			ctx.delete(AGREEMENT_LEVEL_CATEGORY)
				.where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.in(ctx.select(AGREEMENT_LEVEL.ID).from(AGREEMENT_LEVEL).where(AGREEMENT_LEVEL.AGREEMENT.eq(idConvenio))))
				.execute();

			// AGREEMENT_EXTRA
			ctx.delete(AGREEMENT_EXTRA).where(AGREEMENT_EXTRA.AGREEMENT.eq(idConvenio)).execute();

			// AGREEMENT_PAYMENT
			ctx.delete(AGREEMENT_PAYMENT).where(AGREEMENT_PAYMENT.AGREEMENT.eq(idConvenio)).execute();

			// AGREEMENT_LEVEL
			ctx.delete(AGREEMENT_LEVEL).where(AGREEMENT_LEVEL.AGREEMENT.eq(idConvenio)).execute();

			// AGREEMENT_DATA
			ctx.delete(AGREEMENT_DATA).where(AGREEMENT_DATA.AGREEMENT.eq(idConvenio)).execute();

			// AGREEMENT
			ctx.delete(AGREEMENT).where(AGREEMENT.ID.eq(idConvenio)).execute();

		}

	}
	
	// Añadir Concepto al convenio de AON, para los Conceptos Salariales de Omega (incluido el generado por la tabla de antiguedad de Omega)
	private static void addAgreementPayment(int agreement, Convenio convenio, Categoria categoria, Concepto concepto, Date startDate, byte salaryType) {

		// Buscar o añadir el concepto a payment_concept
		int paymentConcept = Traspaso.getPaymentConcept(ctx, concepto);
		
		// Expresion que se usa para el concepto
		String expression = concepto.getAonExpression();
		
		// Controlar concepto de antiguedad con tabla asociada
		if ("02".equals(concepto.getClave()) && !categoria.getAonFormulaAntiguedad().isEmpty()) {
			if (categoria.getAonFormulaAntiguedad().contains("IMPORTE_ANTIGUEDAD") || (convenio.esAntiguedadDistinta() && convenio.maximaLongitudAntiguedad() <= 128)) {
				expression = categoria.getAonSeniorityExpression(); // Tabla de una sola linea, o antiguedad distinta en las categorias (y cabe la formula en la expresion de la variable)
			}
			else {				
				expression = categoria.getAonSeniorityExpression().replace(categoria.getAonSeniorityVariable(), categoria.getAonFormulaAntiguedad());
				// Si la antiguedad es distinta en las categorias y ademas la formula de la 
				// antiguedad no cabe en todas las variables de todas las categorias, entonces
				// se crean varios conceptos de antiguedad con una variable "APLICAR..."
				if (convenio.esAntiguedadDistinta() && convenio.maximaLongitudAntiguedad() > 128) {
					// Almacenamos al expresion en un array para crear una variable distinta para cada expression
					if (!listaExpresionesAntig.contains(expression))
						listaExpresionesAntig.add(expression);
					
					// Expresion que se guardará realmente en el concepto					
					expression = "APLICAR_ANTIG_" + listaExpresionesAntig.size() + " * " + expression;
				}
			}
		}

		// Añadir el concepto a agreement_payment (si no existe) 
		// El nombre del concepto no se tiene en cuenta para comprobar si existe
		Integer id = ctx.select(AGREEMENT_PAYMENT.ID)
						.from(AGREEMENT_PAYMENT)
						.where(AGREEMENT_PAYMENT.DOMAIN.equal(getParentDomain())
						.and(AGREEMENT_PAYMENT.AGREEMENT.equal(agreement)
						.and(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.equal(paymentConcept)
						.and(AGREEMENT_PAYMENT.TYPE.equal(concepto.getAonClaveCRA())
						.and(AGREEMENT_PAYMENT.EXPRESSION.equal(expression)
						.and(AGREEMENT_PAYMENT.IRPF_EXPRESSION.equal(concepto.getAonIrpfExpression())
						.and(AGREEMENT_PAYMENT.QUOTE_EXPRESSION.equal(concepto.getAonQuoteExpression()))))))))
						.fetchOne(AGREEMENT_PAYMENT.ID);

		if (id == null) {
			ctx.insertInto(AGREEMENT_PAYMENT)
				.set(AGREEMENT_PAYMENT.DOMAIN, getParentDomain())
				.set(AGREEMENT_PAYMENT.AGREEMENT, agreement)
				.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, paymentConcept)
				.set(AGREEMENT_PAYMENT.TYPE, concepto.getAonClaveCRA())
				.set(AGREEMENT_PAYMENT.EXPRESSION, expression)
				.set(AGREEMENT_PAYMENT.DESCRIPTION, concepto.getAonDescription())
				.set(AGREEMENT_PAYMENT.START_DATE, AonDateUtils.toSql(startDate))
				.set(AGREEMENT_PAYMENT.SALARY_TYPE, salaryType)
				.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, concepto.getAonIrpfExpression())
				.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, concepto.getAonQuoteExpression())
				.execute();
		}

	}

	// Añadir Concepto al convenio de AON, para las Pagas Extras de Omega
	private static void addAgreementPayment(int agreement, Paga paga, Date startDate, boolean pagaDistinta) {

		// Buscar o añadir el concepto a payment_concept
		int paymentConcept = Traspaso.getPaymentConcept(ctx, paga);

		// Añadir el concepto a agreement_payment, si no existe previamente
		Integer id = ctx.select(AGREEMENT_PAYMENT.ID)
						.from(AGREEMENT_PAYMENT)
						.where(AGREEMENT_PAYMENT.DOMAIN.equal(getParentDomain())
						.and(AGREEMENT_PAYMENT.AGREEMENT.equal(agreement)
						.and(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.equal(paymentConcept)
						.and(AGREEMENT_PAYMENT.EXPRESSION.equal(paga.getAonExpression(conceptosPag, pagaDistinta))))))
						.fetchOne(AGREEMENT_PAYMENT.ID);

		if (id == null) {
			int agreementPayment = ctx.insertInto(AGREEMENT_PAYMENT)
										.set(AGREEMENT_PAYMENT.DOMAIN, getParentDomain())
										.set(AGREEMENT_PAYMENT.AGREEMENT, agreement)
										.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, paymentConcept)
										.set(AGREEMENT_PAYMENT.TYPE, (byte) 4)
										.set(AGREEMENT_PAYMENT.EXPRESSION, paga.getAonExpression(conceptosPag, pagaDistinta))
										.set(AGREEMENT_PAYMENT.DESCRIPTION, paga.getAonDescription())
										.set(AGREEMENT_PAYMENT.START_DATE, AonDateUtils.toSql(startDate))
										.set(AGREEMENT_PAYMENT.SALARY_TYPE, paga.getAonSalaryType())
										.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, "_P")
										.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, paga.getAonQuoteExpression())
										.set(AGREEMENT_PAYMENT.MONTH, paga.getAonMonth())
										.returning(PAYMENT_CONCEPT.ID)
										.fetchOne()
										.getId();

			// Crear registro en agreement_extra si la paga no está prorrateada
			if (!"P".equals(paga.getTipo())) {
				ctx.insertInto(AGREEMENT_EXTRA)
						.set(AGREEMENT_EXTRA.DOMAIN, getParentDomain())
						.set(AGREEMENT_EXTRA.AGREEMENT, agreement)
						.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, agreementPayment)
						.set(AGREEMENT_EXTRA.START_DATE, paga.getAonStartDate())
						.set(AGREEMENT_EXTRA.END_DATE, paga.getAonEndDate())
						.set(AGREEMENT_EXTRA.ISSUE_DATE, paga.getAonIssueDate())
						.execute();
			}
		}

	}
	
	// Añadir Concepto al convenio de AON, para los Complementos enfermedad/accidente de Omega
	private static void addAgreementPayment(int agreement, Complemento complemento, Date startDate) {

		// Buscar o añadir el concepto a payment_concept
		int paymentConcept = Traspaso.getPaymentConcept(ctx, complemento);

		// Añadir el concepto a agreement_payment, si no existe previamente
		Integer id = ctx.select(AGREEMENT_PAYMENT.ID)
							.from(AGREEMENT_PAYMENT)
							.where(AGREEMENT_PAYMENT.DOMAIN.equal(getParentDomain())
							.and(AGREEMENT_PAYMENT.AGREEMENT.equal(agreement)
							.and(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.equal(paymentConcept)
							.and(AGREEMENT_PAYMENT.TYPE.equal(complemento.getAonClaveCRA())
							.and(AGREEMENT_PAYMENT.EXPRESSION.equal(complemento.getAonExpression(conceptosEnf, conceptosAcc))
							.and(AGREEMENT_PAYMENT.IRPF_EXPRESSION.equal("_P")))))))								
							.fetchOne(AGREEMENT_PAYMENT.ID);

		if (id == null) {
			ctx.insertInto(AGREEMENT_PAYMENT)
					.set(AGREEMENT_PAYMENT.DOMAIN, getParentDomain())
					.set(AGREEMENT_PAYMENT.AGREEMENT, agreement)
					.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, paymentConcept)
					.set(AGREEMENT_PAYMENT.TYPE, complemento.getAonClaveCRA())
					.set(AGREEMENT_PAYMENT.EXPRESSION, complemento.getAonExpression(conceptosEnf, conceptosAcc))
					.set(AGREEMENT_PAYMENT.DESCRIPTION, complemento.getAonDescription())
					.set(AGREEMENT_PAYMENT.START_DATE, AonDateUtils.toSql(startDate))
					.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0)
					.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, "_P")
					.execute();		
		}

	}

	private static void prepararConvenio(Convenio convenio) {

		// Conceptos Salariales
		// Si el concepto existe en las categorias (según su clave), los datos nombre, ss, irpf, pag, enf, acc, tipo y clave CRA, son
		// los que tiene el concepto en el convenio, por lo tanto se sobreescriben los de la categoría
		// Además se obtienen tambien los conceptos que formarán parte de las pagas
		// extras por días y de los complementos enf/acc
		ArrayList<String> listaConceptosPagas = new ArrayList<String>();
		ArrayList<String> listaConceptosEnfermedad = new ArrayList<String>();
		ArrayList<String> listaConceptosAccidente = new ArrayList<String>();
		for (Concepto concepto : convenio.getConceptos()) {

			// Conceptos de descuentos (claves 70, 71, 80, 81, 90 y 91), se ignoran porque
			// en AON no se pueden poner descuentos en el convenio
			if (concepto.esDescuento()) {
				continue;
			}

			// Conceptos que forman parte de las pagas extras por días
			if ("S".equals(concepto.getPag())) {
				if (!listaConceptosPagas.contains(concepto.getAonCode()))
					listaConceptosPagas.add(concepto.getAonCode());
			}
			
			// Conceptos que forman parte de los complementos por enfermedad
			if ("S".equals(concepto.getEnf())) {
				if (!listaConceptosEnfermedad.contains(concepto.getAonCode()))
					listaConceptosEnfermedad.add(concepto.getAonCode());
			}
			
			// Conceptos que forman parte de los complementos por accidente
			if ("S".equals(concepto.getAcc())) {
				if (!listaConceptosAccidente.contains(concepto.getAonCode()))
					listaConceptosAccidente.add(concepto.getAonCode());
			}

			// Buscamos el mismo concepto en todas las categorías del convenio y sobreescribimos los datos
			for (Categoria categoria : convenio.getCategorias()) {
				for (Concepto conceptoCat : categoria.getConceptos()) {
					if (conceptoCat.getClave().equals(concepto.getClave())) {
						conceptoCat.setNombre(concepto.getNombre());
						conceptoCat.setSs(concepto.getSs());
						conceptoCat.setIrpf(concepto.getIrpf());
						conceptoCat.setPag(concepto.getPag());
						conceptoCat.setEnf(concepto.getEnf());
						conceptoCat.setAcc(concepto.getAcc());
						conceptoCat.setTipo(concepto.getTipo());
						conceptoCat.setClaveCRA(concepto.getClaveCRA());
					}
				}
			}
		}

		// Completar los conceptos que forman parte de las pagas por días y de complementos enf/acc
		// con aquellos conceptos de las categorías, que no están en el convenio (según su clave)
		for (Categoria categoria : convenio.getCategorias()) {
			for (Concepto conceptoCat : categoria.getConceptos()) {

				// Conceptos de descuentos (claves 70, 71, 80, 81, 90 y 91), se ignoran porque
				// en AON no se pueden poner descuentos en el convenio
				if (conceptoCat.esDescuento()) {
					continue;
				}
				
				if (!convenio.existeConcepto(conceptoCat.getClave())) {

					// Conceptos que forman parte de las pagas extras por días
					if ("S".equals(conceptoCat.getPag())) {
						if (!listaConceptosPagas.contains(conceptoCat.getAonCode()))
							listaConceptosPagas.add(conceptoCat.getAonCode());
					}
					
					// Conceptos que forman parte de los complementos por enfermedad
					if ("S".equals(conceptoCat.getEnf())) {
						if (!listaConceptosEnfermedad.contains(conceptoCat.getAonCode()))
							listaConceptosEnfermedad.add(conceptoCat.getAonCode());
					}
					
					// Conceptos que forman parte de los complementos por accidente
					if ("S".equals(conceptoCat.getAcc())) {
						if (!listaConceptosAccidente.contains(conceptoCat.getAonCode()))
							listaConceptosAccidente.add(conceptoCat.getAonCode());
					}
					
				}
			}
		}

		// Conceptos para las pagas extras por días
		conceptosPag = "";
		for (int i = 0; i < listaConceptosPagas.size(); i++) {
			conceptosPag = conceptosPag + (i > 0 ? "+" : "") + listaConceptosPagas.get(i);
		}
		
		// Conceptos para los complementos por enfermedad
		conceptosEnf = "";
		for (int i = 0; i < listaConceptosEnfermedad.size(); i++) {
			conceptosEnf = conceptosEnf + (i > 0 ? "+" : "") + listaConceptosEnfermedad.get(i);
		}
		
		// Conceptos para los complementos por accidente
		conceptosAcc = "";
		for (int i = 0; i < listaConceptosAccidente.size(); i++) {
			conceptosAcc = conceptosAcc + (i > 0 ? "+" : "") + listaConceptosAccidente.get(i);
		}

		// Pagas Extras
		// Si la paga extra existe en las categorías (según el mes), entonces la
		// descripcion, inicio devengo y fin devengo, se cogen de la definicion 
		// de la paga en el convenio
		for (Paga pagaCon : convenio.getPagas()) {
			for (Categoria categoria : convenio.getCategorias()) {
				Paga pagaCat = categoria.buscarPaga(pagaCon.getMes());
				if (pagaCat != null) {
					// Sobreescribir los datos de la categoria
					pagaCat.setDescripcion(pagaCon.getDescripcion());
					pagaCat.setDiaInicio(pagaCon.getDiaInicio());
					pagaCat.setMesInicio(pagaCon.getMesInicio());
					pagaCat.setAnoInicio(pagaCon.getAnoInicio());
					pagaCat.setDiaFin(pagaCon.getDiaFin());
					pagaCat.setMesFin(pagaCon.getMesFin());
					pagaCat.setAnoFin(pagaCon.getAnoFin());
				}
			}
		}
	}
	
	// Busca la categoria (segun la clave de Omega)
	public static Categoria buscarCategoria(String codigoConvenio, String codigoCategoria) {
		
		for (Convenio convenio : convenios) {
			if (convenio.getCodigo().equals(codigoConvenio)) 
				for (Categoria categoria : convenio.getCategorias())
					if (categoria.getCodcat().equals(codigoCategoria))
						return categoria;
		}
		return null;
		
	}

}
