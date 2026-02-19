package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class RealDecreeLaw1782025Art284XXFix implements Update {

	private static final String ART_28_CORTA_DURACION = "ART_28_CORTA_DURACION";
	public static RealDecreeLaw1782025Art284XXFix REALDECREELAW1782025ART284XXFIX = new RealDecreeLaw1782025Art284XXFix();

	
	private RealDecreeLaw1782025Art284XXFix() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2025);		
		Date start2025Date = new Date(calendar.getTimeInMillis());

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION,"DIAS(FIN_CONTRATO,INICIO_CONTRATO) < 30 "
					+ "&& '"
					+"401," //DURACIÓN DETERMINADA TIEMPO COMPLETO - OBRA O SERVICIO DETERMINADO
					+"402," //DURACIÓN DETERMINADA TIEMPO COMPLETO - EVENTUAL POR CIRCUNSTANCIAS DE LA PRODUCCIÓN
					+"403," //DURACIÓN DETERMINADA TIEMPO COMPLETO - INSERCIÓN
					+"404," //CONTRATO PREDOCTORAL
					+"406," //ADMINISTRACIONES PÚBLICAS. PLAN RECUPERACIÓN,TRANSFORMACIÓN Y RESILIENCIA, Y FONDOS UNIÓNEUROPEA. TIEMPO COMPLETO.
					+"407," //DURACIÓN DETERMINADA. ARTISTAS, TÉCNICOS Y AUXILIARES. TIEMPO COMPLETO.
					+"408," //DURACIÓN DETERMINADA TIEMPO COMPLETO - CARÁCTER ADMINISTRATIVO
					+"411," //DURACIÓN DETERMINADA.TIEMPO COMPLETO. PERSONAL DOCENTE INVESTIGADOR UNIVERSITARIO.
					+"412," //ACCESO PERSONAL INVESTIGADOR DOCTOR
					+"413," //DURACIÓN DETERMINADA. TIEMPO COMPLETO. DEPORTISTAS PROFESIONALES
					+"420," //DURACIÓN DETERMINADA TIEMPO COMPLETO - PRÁCTICAS
					+"430," //DURACIÓN DETERMINADA TIEMPO COMPLETO - DISCAPACITADOS
					+"441," //DURACIÓN DETERMINADA TIEMPO COMPLETO - RELEVO
					+"450," //DURACIÓN DETERMINADA TIEMPO COMPLETO - FOMENTO CONTRATACIÓN INDEFINIDA/EMPLEO ESTABLE
					+"452," //DURACIÓN DETERMINADA TIEMPO COMPLETO - TRABAJADORES DESEMPLEADOS CONTRATADOS POR EMPRESAS DE INSERCIÓN
					+"500," //TEMPORAL. TIEMPO PARCIAL ORDINARIO
					+"501," //DURACIÓN DETERMINADA TIEMPO PARCIAL - OBRA O SERVICIO DETERMINADO Baja desde 31.12.2021
					+"502," //DURACIÓN DETERMINADA TIEMPO PARCIAL - EVENTUAL POR CIRCUNSTANCIAS DE LA PRODUCCIÓN
					+"503," //DURACIÓN DETERMINADA TIEMPO PARCIAL - INSERCIÓN
					+"506," //ADMINISTRACIONES PÚBLICAS. PLAN RECUPERACIÓN, TRANSFORMACIÓN Y RESILIENCIA, Y FONDOS UNIÓN EUROPEA. TIEMPO PARCIAL.
					+"507," //DURACIÓN DETERMINADA. ARTISTAS, TÉCNICOS Y AUXILIARES. TIEMPO PARCIAL.
					+"508," //DURACIÓN DETERMINADA TIEMPO PARCIAL - CARÁCTER ADMINISTRATIVO
					+"511," //DURACIÓN DETERMINADA.TIEMPO PARCIAL. PERSONAL DOCENTE INVESTIGADOR UNIVERSITARIO.
					+"513," //DURACIÓN DETERMINADA. TIEMPO PARCIAL. DEPORTISTAS PROFESIONALES
					+"520," //DURACIÓN DETERMINADA TIEMPO PARCIAL - PRÁCTICAS
					+"530," //DURACIÓN DETERMINADA TIEMPO PARCIAL -	DISCAPACITADOS
					+"540," //DURACIÓN DETERMINADA TIEMPO PARCIAL - JUBILACIÓN PARCIAL
					+"541," //DURACIÓN DETERMINADA TIEMPO PARCIAL - RELEVO
					+"550," //DURACIÓN DETERMINADA TIEMPO PARCIAL - FOMENTO CONTRATACIÓN INDEFINIDA/EMPLEO ESTABLE
					+"552," //DURACIÓN DETERMINADA TIEMPO PARCIAL - TRABAJADORES DESEMPLEADOS CONTRATADOS POR EMPRESAS DE INSERCIÓN
					+ "'.contains(TC2) "
					+ "&& {"
					+ "HOGAR:false,"
					+ "ARTISTAS:false,"
					+ "AGRARIO:false,"
					+ "GENERAL:true,"
					+ "BECARIOS:true,"
					+ "FORMACION:true,"
					+ "APRENDIZAJE:true,"
					+ "REPRESENTANTES:true,"
					+ "ASIMILADOS:true"
					+ "}[REGIMEN]")
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(ART_28_CORTA_DURACION))
			.and(SYSTEM_DATA.START_DATE.eq(start2025Date))
			.execute()
			;
			

			// Esta cotización adicional tampoco se aplicará a los contratos por sustitución, 

			//	+"410," //DURACIÓN DETERMINADA TIEMPO COMPLETO - INTERINIDAD
			//	+"418," //DURACIÓN DETERMINADA TIEMPO COMPLETO - INTERINIDAD CARÁCTER ADMINISTRATIVO
			//	+"510," //DURACIÓN DETERMINADA TIEMPO PARCIAL - INTERINIDAD
			//	+"518," //DURACIÓN DETERMINADA TIEMPO PARCIAL - INTERINIDAD CARÁCTER ADMINISTRATIVO
			
			// a los contratos para la formación y el aprendizaje ni a los contratos de formación en alternancia
			//	+"421," //TEMPORAL TIEMPO COMPLETO. FORMACIÓN EN ALTERNANCIA
			//	+"521," //TEMPORAL TIEMPO PARCIAL. FORMACIÓN EN ALTERNANCIA
			
			
			// Esta cotización adicional no se aplicará a los contratos a los que se refiere este artículo cuando sean celebrados con personas trabajadoras incluidas en el :
			// Sistema Especial para Trabajadores por Cuenta Ajena Agrarios o en el 
			// Sistema Especial para Empleados de Hogar, ambos establecidos en el Régimen General de la Seguridad Social, o en el 
			// Régimen Especial de la Seguridad Social para la Minería del Carbón, en 
			// la relación laboral especial de las personas artistas que desarrollan su actividad en las artes escénicas, audiovisuales y musicales, así como de las personas que realizan actividades, técnicas o auxiliares necesarias para el desarrollo de dicha actividad			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}
	
}
