package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;

import java.sql.Connection;
import java.util.Arrays;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import net.aonsolutions.db.up2date.Update;

public class InKindConceptsIrpfFix implements Update {

	public static final InKindConceptsIrpfFix INKINDCONCEPTSIRPFFIX = new InKindConceptsIrpfFix();
	
	//	Están exentos del IRPF los siguientes rendimientos de trabajo en especie:
	//
	//	    a) Entregas a empleados de productos a precios rebajados que se realicen en comedo­res de empresa, cantinas o economatos de carácter social
	//	    b) Utilización de los bienes destinados a los servicios sociales y culturales del personal empleado
	//	    c) Gastos por seguros de enfermedad
	//	    d) Prestación de determinados servicios de educación a los hijos de los empleados de centros educativos autorizados
	//	    e) Cantidades satisfechas por la empresa para el transporte colectivo de sus empleados entre su lugar de residencia y el centro de trabajo
	//	    f) Entrega a los trabajadores de acciones o participaciones de la propia empresa o de otras de grupo de sociedades

	private InKindConceptsIrpfFix() {
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
			.and(PAYMENT_CONCEPT.TYPE.in( 
					(byte)36, // PRODUCTOS PRECIOS REBAJADOS-CANTINAS COMEDORES ECONOMICOS
					(byte)39, // PRIMAS SEGURO ENFERMEDAD COMÚN TRABAJ
					(byte)40, // PRIMAS SEGURO ENFERMEDAD COMÚN FAMILIAR
					(byte)41, // PRESTACIÓN DEL SERVICIO DE EDUCACIÓN A HIJOS DE TRABAJADORES
					(byte)61  // PLUSES DE TRANSPORTE Y DE DISTANCIA (MEDIOS COLECTIVOS EMPRESA) 
					)  
			)
			.and(PAYMENT_CONCEPT.IRPF_EXPRESSION.isNull()) 
		) >= 5;
		
		if ( upgraded )
			return;
		

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(PAYMENT_CONCEPT)
			.setNull(PAYMENT_CONCEPT.IRPF_EXPRESSION)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.TYPE.in( 
					(byte)36, // PRODUCTOS PRECIOS REBAJADOS-CANTINAS COMEDORES ECONOMICOS
					(byte)39, // PRIMAS SEGURO ENFERMEDAD COMÚN TRABAJ
					(byte)40, // PRIMAS SEGURO ENFERMEDAD COMÚN FAMILIAR
					(byte)41, // PRESTACIÓN DEL SERVICIO DE EDUCACIÓN A HIJOS DE TRABAJADORES
					(byte)61  // PLUSES DE TRANSPORTE Y DE DISTANCIA (MEDIOS COLECTIVOS EMPRESA) 
					)  
			)
			.execute();


			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
