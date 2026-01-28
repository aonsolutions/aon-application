package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class FlexibleCRAFix implements Update {

	public static final FlexibleCRAFix FLEXIBLECRAFIX = new FlexibleCRAFix();
	
	
	//	0013 R. ESPECIE NO INCLUIDA EN OTROS APARTADOS
	//	0014 R.ESP.VIVIENDA.PROP.PAGAD.C/VALOR.CATAST.
	//	0015 R.ESP.VIVIENDA.PROP.PAGAD.PTE.VALOR.CAT.
	//	0016 R.ESP.VIVIENDA.NO PROPIEDAD PAGADOR
	//	0017 R.ESP.VEHÍCULO. ENTREGA AL TRABAJADOR
	//	0018 R.ESP.VEHÍCULO.USO.PROPIEDAD PAGADOR
	//	0019 R.ESP.VEHÍCULO USO.NO PROPIEDAD PAGADOR
	//	0020 R.ESP.VEHÍCULO USO Y POSTERIOR ENTREGA
	//	0021 R.ESP.PRÉSTAMO. TIPO INTERÉS < LEGAL
	//	0022 R.ESP. MANUTENCIÓN Y SIMILARES
	//	0023 R.ESP. HOSPEDAJE Y SIMILARES
	//	0024 R.ESP. VIAJES Y SIMILARES
	//	0025 R.ESP.GASTOS DE ESTUDIOS Y MANUTENCIÓN
	//	0026 R.ESP.DERECHOS FUNDADORES DE SOCIEDADES
	
	private static final Map<String, Byte> CONCEPTS_CRA = new HashMap<>(){
		{ put("FLEXIBLE"	, (byte)13); }
		{ put("FLEXIBLE_TRANSPORTE"	, (byte)13); }
		{ put("FLEXIBLE_COMIDA"		, (byte)22); }
		{ put("FLEXIBLE_GUARDERIA"	, (byte)25); }
		{ put("FLEXIBLE_SEGURO"		, (byte)13); }
		{ put("FLEXIBLE_FORMACION"	, (byte)25); }
	};
	
	private static final String CHECK_EXPRESSION = 
			"SELF.addBonus("
			+ "'CHECK(FLEXIBLE <= (0.30*(FLEXIBLE+TOTAL_DEVENGADO)),"
			+ "\"<div>Retribuci\u00F3n flexible no podr\u00E1n exceder el 30% del salario bruto anual</div><div>&nbsp;</div><div class=\\'aon-text-right\\'><span class=\\'aon-icon aon-icon-logo\\' />aon Solutions</div>\""
			+ ");"
			+ "REMOVE()')";

	private FlexibleCRAFix() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		boolean upgraded = dslContext.fetchCount(
			dslContext
			.select(PAYMENT_CONCEPT.ID)
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq( "DTO_FLEXIBLE" ) )
		) >= 1;
		
		if ( upgraded )
			return;
		

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			for ( Map.Entry<String, Byte>  entry : CONCEPTS_CRA.entrySet() ) {
				Byte type = entry.getValue();
				String concept = entry.getKey();
				dslContext
				.update(PAYMENT_CONCEPT)
				.set(PAYMENT_CONCEPT.TYPE, type )
				.set(PAYMENT_CONCEPT.EXPRESSION, getExpression4(PAYMENT_CONCEPT.EXPRESSION) )
				.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, DSL.regexpReplaceAll(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P$", "-_P"))
				.where(PAYMENT_CONCEPT.CODE.eq(concept))
				.execute();
				
				dslContext
				.update(CONTRACT_PAYMENT)
				.set(CONTRACT_PAYMENT.EXPRESSION, getExpression4(CONTRACT_PAYMENT.EXPRESSION) )
				.from(PAYMENT_CONCEPT)
				.where(CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(PAYMENT_CONCEPT.ID))
				.and(PAYMENT_CONCEPT.CODE.eq(concept))
				.execute();
				
				dslContext
				.update(AGREEMENT_PAYMENT)
				.set(AGREEMENT_PAYMENT.EXPRESSION, getExpression4(AGREEMENT_PAYMENT.EXPRESSION) )
				.from(PAYMENT_CONCEPT)
				.where(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.eq(PAYMENT_CONCEPT.ID))
				.and(PAYMENT_CONCEPT.CODE.eq(concept))
				.execute();
				
			}
			
			
			dslContext
			.insertInto(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)1)
			.set(PAYMENT_CONCEPT.CODE, "DTO_FLEXIBLE")
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "0.00")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "APORTACI\u00D3N VOLUNTARIA RETRIBUCI\u00D3N FLEXIBLE")
			.set(PAYMENT_CONCEPT.EXPRESSION, "/*read-only*/TOTAL_DEVENGADO;-FLEXIBLE/**/")
			.execute();

			//			Calendar calendar = Calendar.getInstance();
			//			calendar.set(Calendar.MILLISECOND, 0);
			//			calendar.set(Calendar.SECOND, 0);
			//			calendar.set(Calendar.MINUTE, 0);
			//			calendar.set(Calendar.HOUR_OF_DAY, 0);
			//			calendar.set(Calendar.DAY_OF_MONTH, 1);
			//			calendar.set(Calendar.MONTH, Calendar.JANUARY);
			//			calendar.set(Calendar.YEAR, 2010);
			//			Date startOf2010Date = new Date(calendar.getTimeInMillis());
			//
			//			dslContext
			//			.insertInto(SYSTEM_COST)
			//			.set(SYSTEM_COST.DOMAIN, 0)
			//			.set(SYSTEM_COST.EXPRESSION, 
			//				"CHECK(_EN_ESPECIE <= (0.30 * TOTAL_DEVENGADO),"
			//				+ "\"<div>Retribuci\u00F3n en especie no podr\u00E1n exceder el 30% del salario bruto anual</div><div>&nbsp;</div><div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\""
			//				+ ");"
			//				+ "REMOVE();")
			//			.set(SYSTEM_COST.START_DATE, startOf2010Date)
			//			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}
	
	private Field<String> getExpression4(Field<String> expressionField) {
		return DSL.replace(
					DSL.regexpReplaceAll(
							DSL.regexpReplaceAll(expressionField,
							"^.*/\\*user\\*/", ""), // Remove /*user*/
					"/\\*\\*/\\)$", "" ),			// Remove /**/
					"BENEFICIARIOS_SEGURO * CUOTA_SEGURO", "CHECK_DEF({'BENEFICIARIOS_SEGURO', 'CUOTA_SEGURO'});(/*read-only*/BENEFICIARIOS_SEGURO * CUOTA_SEGURO/**/)"  // Check BENEFICIARIOS_SEGURO * CUOTA_SEGURO
			); 		 
	}

}
