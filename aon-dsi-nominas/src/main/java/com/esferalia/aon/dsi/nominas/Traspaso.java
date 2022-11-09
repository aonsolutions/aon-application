package com.esferalia.aon.dsi.nominas;

import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;
import java.util.ResourceBundle;

import org.jooq.DSLContext;

import com.code.aon.common.ILogger;
import com.esferalia.aon.dsi.nominas.model.Complemento;
import com.esferalia.aon.dsi.nominas.model.Concepto;
import com.esferalia.aon.dsi.nominas.model.Paga;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Traspaso {
	
	private static ILogger log; 
	private static int parentDomain; 
	
	public static int getParentDomain() {
		return parentDomain;
	}
	
	public static void execute(String paradoxDirectory, String domainName, int parentDom, String user, ILogger iloguer) throws Exception {
		
		// Logger
		log = iloguer;

		// Dominio padre
		parentDomain = parentDom;

		info("[INICIO DEL TRASPASO]");
		info("PARENT_DOMAIN_NAME = " + domainName);
		info("PARENT_DOMAIN_ID = " + parentDomain);
		
		Date date1 = new Date();
		info("HORA INICIO = " + date1);
						
		try (Connection dsiConn = getDsiConnection("jdbc:paradox:" + paradoxDirectory)) {

			try (CloseableAONContext ctx = AONContext.getAONContext(domainName, parentDom, user)) {
								
				ctx.transaction(configuration -> {
					// Convenios y Categorías
					TraspasoConvenios.execute(dsiConn, ctx);

					// Calendarios
					TraspasoCalendarios.execute(dsiConn, ctx);

				});

			}
			
			TraspasoEmpresas.execute(dsiConn, domainName, parentDom, user);
			
			Date date2 = new Date();
			info("HORA FIN = " + date2);
			long diff = date2.getTime() - date1.getTime();
			info("DURACION DEL TRASPASO = " + (diff / (60 * 1000) % 60) + " min. " + (diff / 1000 % 60) + " seg.");
			info("[FIN DEL TRASPASO]");

		} catch (Exception e) {
			try {
				error("ERROR:");
				error(e.getClass().getSimpleName() + ": " + e.getMessage());
				
				Date date2 = new Date();
				info("HORA FIN = " + date2);
				long diff = date2.getTime() - date1.getTime();
				info("DURACION DEL TRASPASO = " + (diff / (60 * 1000) % 60) + " min. " + (diff / 1000 % 60) + " seg.");
				error("[EL TRASPASO NO SE HA COMPLETADO]");
				throw e.getCause();
			} catch (Throwable throwable) {
				throw e;
			}
		}
		

	}

	public static Connection getDsiConnection(String url) throws SQLException {
		Properties info = new Properties();
		Driver driver = new com.googlecode.paradox.Driver();
		return driver.connect(url, info);
	}
		
	public static void info(String msg) {
		if (log==null) 
			System.out.println(msg);
		else 
			log.info(msg);
	}
	
	private static void error(String msg) {
		if (log==null) 
			System.err.println(msg);
		else 
			log.error(msg);
	}
	
	// Busco geozone en dominio 0 o dominio padre, según cp o según provincia
	public static Integer getGeozone(DSLContext ctx, String cp, String provincia) {
		
		Integer id = null;
		if (AonStringUtils.isNotBlank(cp)) {
			// Buscar geozone por provincia del codigo postal
			cp = AonStringUtils.left(cp, 2);
			id = ctx.select(GEOZONE.ID)
						.from(GEOZONE)
						.where(GEOZONE.DOMAIN.eq(0).or(GEOZONE.DOMAIN.eq(parentDomain)))
						.and(GEOZONE.CODE.eq(cp))
						.fetchAny(GEOZONE.ID);			
			
		} 
		if (id == null && AonStringUtils.isNotBlank(provincia)) {
			// Buscar geozone por nombre de provincia
			id = ctx.select(GEOZONE.ID)
						.from(GEOZONE)
						.where(GEOZONE.DOMAIN.eq(0).or(GEOZONE.DOMAIN.eq(parentDomain)))
						.and(GEOZONE.NAME.equalIgnoreCase(provincia))
						.fetchAny(GEOZONE.ID);
		}
		return id;
		
	}
	
	// Buscar Código Municipio, según nombre población y código postal
	public static String getMunicipalityCode(String poblacion, String cp) {
		if (AonStringUtils.isNotBlank(cp) && AonStringUtils.isNotBlank(poblacion)) {
			ResourceBundle municipalities = ResourceBundle.getBundle("com.code.aon.common.i18n.municipalities");
			for (String key : municipalities.keySet())		    
				if (key != null && key.startsWith(AonStringUtils.left(cp, 2)) && AonStringUtils.startsWith(municipalities.getString(key), AonStringUtils.trimToEmpty(poblacion)))
					return key;
		}
		return null;
	}
	
	// Obtener paymentConcept, según concepto Omega que se le pasa. Si no existe, se crea
	public static int getPaymentConcept(DSLContext ctx, Concepto concepto) {

		// En payment_concept la expresión siempre es "IMP_"+getAonCode()
		// excepto el concepto de antiguedad, que siempre es IMPORTE_ANTIGUEDAD 
		String expression = "02".equals(concepto.getClave()) ? "IMPORTE_ANTIGUEDAD" : "IMP_"+concepto.getAonCode();
		
		// Localizar si ya existe un concepto igual en payment_concept
		Integer id = ctx.select(PAYMENT_CONCEPT.ID)
							.from(PAYMENT_CONCEPT)
							.where(PAYMENT_CONCEPT.DOMAIN.equal(parentDomain)
							.and(PAYMENT_CONCEPT.CODE.equal(concepto.getAonCode())
							.and(PAYMENT_CONCEPT.TYPE.equal(concepto.getAonClaveCRA())
							.and(PAYMENT_CONCEPT.EXPRESSION.equal(expression)
							.and(PAYMENT_CONCEPT.IRPF_EXPRESSION.equal(concepto.getAonIrpfExpression())
							.and(PAYMENT_CONCEPT.QUOTE_EXPRESSION.equal(concepto.getAonQuoteExpression())))))))
							.fetchAny(PAYMENT_CONCEPT.ID);

		// Si no se encuentra un concepto igual, entonces se añade el concepto a payment_concept
		if (id == null) {
			id = ctx.insertInto(PAYMENT_CONCEPT)
						.set(PAYMENT_CONCEPT.DOMAIN, parentDomain)
						.set(PAYMENT_CONCEPT.CODE, concepto.getAonCode())
						.set(PAYMENT_CONCEPT.DESCRIPTION, concepto.getNombre())
						.set(PAYMENT_CONCEPT.TYPE, concepto.getAonClaveCRA())
						.set(PAYMENT_CONCEPT.EXPRESSION, expression)
						.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, concepto.getAonIrpfExpression())
						.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, concepto.getAonQuoteExpression())
						.returning(PAYMENT_CONCEPT.ID)
						.fetchOne()
						.getId();
		}

		return id;
	}
	
	// Obtener paymentConcept, según paga extra Omega que se le pasa. Si no existe, se crea
	public static int getPaymentConcept(DSLContext ctx, Paga paga) {

		// Localizar si ya existe un concepto igual (según code) en payment_concept
		Integer id = ctx.select(PAYMENT_CONCEPT.ID)
							.from(PAYMENT_CONCEPT)
							.where(PAYMENT_CONCEPT.DOMAIN.equal(parentDomain)
							.and(PAYMENT_CONCEPT.CODE.equal(paga.getAonCode())))
							.fetchAny(PAYMENT_CONCEPT.ID);

		// Si no se encuentra un concepto igual, entonces se añade el concepto a payment_concept
		if (id == null) {
			id = ctx.insertInto(PAYMENT_CONCEPT)
						.set(PAYMENT_CONCEPT.DOMAIN, parentDomain)
						.set(PAYMENT_CONCEPT.CODE, paga.getAonCode())
						.set(PAYMENT_CONCEPT.DESCRIPTION, "PAGA EXTRA")
						.set(PAYMENT_CONCEPT.TYPE, (byte) 4)
						.set(PAYMENT_CONCEPT.EXPRESSION, "")
						.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
						.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "_P")
						.returning(PAYMENT_CONCEPT.ID)
						.fetchOne()
						.getId();
		}

		return id;
	}
	
	// Obtener paymentConcept, según complemento e/a Omega que se le pasa. Si no existe, se crea
	public static int getPaymentConcept(DSLContext ctx, Complemento complemento) {

		// Descripción que se usa para grabar los complementos e/a en payment_concept
		String description = "MEJORAS PREST.SS.INCAPACIDAD TEMPORAL";
		
		// Localizar si ya existe un concepto igual en payment_concept
		Integer id = ctx.select(PAYMENT_CONCEPT.ID)
							.from(PAYMENT_CONCEPT)
							.where(PAYMENT_CONCEPT.DOMAIN.equal(parentDomain)
							.and(PAYMENT_CONCEPT.CODE.equal(complemento.getAonCode())
							.and(PAYMENT_CONCEPT.TYPE.equal(complemento.getAonClaveCRA())
							.and(PAYMENT_CONCEPT.DESCRIPTION.equal(description)
							.and(PAYMENT_CONCEPT.EXPRESSION.equal("")))))) // En payment_concept no se graba expresion
							.fetchAny(PAYMENT_CONCEPT.ID);

		// Si no se encuentra un concepto igual, entonces se añade el concepto a payment_concept
		if (id == null) {
			id = ctx.insertInto(PAYMENT_CONCEPT)
						.set(PAYMENT_CONCEPT.DOMAIN, parentDomain)
						.set(PAYMENT_CONCEPT.CODE, complemento.getAonCode())
						.set(PAYMENT_CONCEPT.DESCRIPTION, description)
						.set(PAYMENT_CONCEPT.TYPE, complemento.getAonClaveCRA())
						.set(PAYMENT_CONCEPT.EXPRESSION, "")
						.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
						.returning(PAYMENT_CONCEPT.ID)
						.fetchOne()
						.getId();
		}

		return id;
	}
	
	// Devuelve periodo en texto, según los años que se pasan (se usa en la formula de la antiguedad)
	public static String getPeriod(String year) {

		String period = "";
		switch (Integer.parseInt(year)) {
			case 1: 
				period = "ANUAL";
				break;
			case 2: 				
				period = "BIENIO";
				break;
			case 3:				
				period = "TRIENIO";
				break;
			case 4: 				
				period = "CUATRIENIO";
				break;
			case 5: 				
				period = "QUINQUENIO";
				break;
			case 6: 				
				period = "SEXENIO";
				break;
			case 7: 				
				period = "SEPTENIO";
				break;	
			default:
				period = String.valueOf(Integer.parseInt(year));
				break;
		}
		return period;

	}
	
	// Comprobar tabla de complementos enf/acc de Omega
	// Si es de tipo "complemento", se devuelve como tal
	// Si es de tipo "compensacion", se transforma en una tabla de tipo complemento, pues AON no soporta complementos enf/acc de tipo compensacion
	public static LinkedList<Complemento> comprobarComplementosEnfAcc(LinkedList<Complemento> complementosConvenio, String tipoComple) {
		if ("1".equals(tipoComple)) {
			// Tipo de Tabla Complemento
			return complementosConvenio;
		} else {
			// Tipo de Tabla Compensacion
			LinkedList<Complemento> complementos = new LinkedList<Complemento>();
			for (Complemento compleConven : complementosConvenio) {
				if (AonStringUtils.isNotBlank(compleConven.getTipo()) && AonStringUtils.isNotBlank(compleConven.getCalculo())) {
					if ("A".equals(compleConven.getTipo())) {
						// Accidente, el subsidio es el 75% desde el primer día, por lo tanto el
						// complemento equivalente a esa compensacion será sumar 75 al porcentaje indicado
						int porcentaje = compleConven.getPorcentaje() + 75;
						if (porcentaje > 100)
							porcentaje = 100;
						complementos.add(new Complemento()
											.setTipo("A")
											.setPorcentaje(porcentaje)
											.setDiaDesde(compleConven.getDiaDesde())
											.setDiaHasta(compleConven.getDiaHasta())
											.setCalculo(compleConven.getCalculo()));
					} else if ("E".equals(compleConven.getTipo())) {
						// Enfermedad, subdisio 1-3=0, 4-20=60%, 21 en adelante=75%
						// Debemos comprobar los 3 bloques de porcentajes que paga el subsidio, para
						// ver como transformar esa compensacion en una o varias lineas de complemento
						int diaDesdeConven = compleConven.getDiaDesde();
						if (diaDesdeConven == 0)
							diaDesdeConven = 1;
						int diaHastaConven = compleConven.getDiaHasta();
						if (diaHastaConven == 0)
							diaHastaConven = 999;
						// Bloque 1: días del 1 al 3, subsidio = 0
						if (diaDesdeConven <= 3 && diaHastaConven >= 1) {
							int diaDesde = 1;
							if (diaDesdeConven > 1)
								diaDesde = diaDesdeConven;
							int diaHasta = 3;
							if (diaHastaConven < 3)
								diaHasta = diaHastaConven;
							int porcentaje = compleConven.getPorcentaje();
							if (porcentaje > 100)
								porcentaje = 100;
							complementos.add(new Complemento()
												.setTipo("E")
												.setPorcentaje(porcentaje)
												.setDiaDesde(diaDesde)
												.setDiaHasta(diaHasta)
												.setCalculo(compleConven.getCalculo()));
						}
						// Bloque 2: días del 4 al 20, subsidio = 60%
						if (diaDesdeConven <= 20 && diaHastaConven >= 4) {
							int diaDesde = 4;
							if (diaDesdeConven > 4)
								diaDesde = diaDesdeConven;
							int diaHasta = 20;
							if (diaHastaConven < 20)
								diaHasta = diaHastaConven;
							int porcentaje = compleConven.getPorcentaje() + 60;
							if (porcentaje > 100)
								porcentaje = 100;
							complementos.add(new Complemento()
												.setTipo("E")
												.setPorcentaje(porcentaje)
												.setDiaDesde(diaDesde)
												.setDiaHasta(diaHasta)
												.setCalculo(compleConven.getCalculo()));
						}
						// Bloque 3: día desde 21, subsidio = 75%
						if (diaHastaConven >= 21) {
							int diaDesde = 21;
							if (diaDesdeConven > 21)
								diaDesde = diaDesdeConven;
							int diaHasta = diaHastaConven;
							int porcentaje = compleConven.getPorcentaje() + 75;
							if (porcentaje > 100)
								porcentaje = 100;
							complementos.add(new Complemento()
												.setTipo("E")
												.setPorcentaje(porcentaje)
												.setDiaDesde(diaDesde)
												.setDiaHasta(diaHasta)
												.setCalculo(compleConven.getCalculo()));
						}
					}
				}
			}
			return complementos;
		}
			
	}

}
