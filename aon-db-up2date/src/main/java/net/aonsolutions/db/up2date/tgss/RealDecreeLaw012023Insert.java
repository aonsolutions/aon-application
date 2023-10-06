package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.BonusConcept.BONUS_CONCEPT;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.BonusConcept;
import com.esferalia.aon.jooq.tables.ContractBonus;

import net.aonsolutions.db.up2date.Update;

public class RealDecreeLaw012023Insert implements Update {
	public static RealDecreeLaw012023Insert REALDECREELAW012023INSERT = new RealDecreeLaw012023Insert();

	private static final String CUANTIA_91 = "MIN( ( DIAS_COTIZADOS >= DIAS_MES ? 91.00 :  3.03 * DIAS_COTIZADOS )  * COEFICIENTE_PARCIALIDAD, CGC_E + IT_E + IMS_E + FOGASA_E + FP_E + DESMPL_E )";
	private static final String CUANTIA_262 = "MIN( ( DIAS_COTIZADOS >= DIAS_MES ? 262.00 :  8.73 * DIAS_COTIZADOS )  * COEFICIENTE_PARCIALIDAD, CGC_E + IT_E + IMS_E + FOGASA_E + FP_E + DESMPL_E )";
	private static final String CUANTIA_128 = "MIN( ( DIAS_COTIZADOS >= DIAS_MES ? 128.00 :  4.27 * DIAS_COTIZADOS )  * COEFICIENTE_PARCIALIDAD, CGC_E + IT_E + IMS_E + FOGASA_E + FP_E + DESMPL_E )";
	private static final String CUANTIA_366 = "MIN(366.00/30 * MIN(DIAS_COTIZADOS,30) * COEFICIENTE_PARCIALIDAD, CGC_E + IT_E + IMS_E + FOGASA_E + FP_E + DESMPL_E )";
	private static final String CUANTIA_138 = "MIN(138.00/30 * MIN(DIAS_COTIZADOS,30) * COEFICIENTE_PARCIALIDAD, CGC_E + IT_E + IMS_E + FOGASA_E + FP_E + DESMPL_E )";
	private static final String READ_ONLY = "/*read-only*/";
	private static final String PEC_16_QUOTA_01 = "/*pec:16,quota:01*/";
	private static final String CHECK_PARCIALIDAD_GE_50 = "CHECK(COEFICIENTE_PARCIALIDAD >= 0.50,\"<span>Cuando la jornada sea inferior al 50%, no se aplicará ningún importe de bonificación (Art. 10 RDL 1/2023)</span><div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\");";


	
	private RealDecreeLaw012023Insert() {
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
		


		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.deleteFrom(BONUS_CONCEPT)
			.using(BONUS_CONCEPT.leftJoin(CONTRACT_BONUS).onKey())
			.where(BONUS_CONCEPT.DOMAIN.eq(0))
			.and(CONTRACT_BONUS.ID.isNull())
			.execute();

			dslContext
			.update(BONUS_CONCEPT)
			.set(BONUS_CONCEPT.TYPE, (byte)0)
			.where(BONUS_CONCEPT.DOMAIN.eq(0))
			.and(BONUS_CONCEPT.TYPE.isNull() )
			.execute();
						
			dslContext
			.insertInto(BONUS_CONCEPT)
			.set(BONUS_CONCEPT.DOMAIN, 0)
			.set(BONUS_CONCEPT.TYPE, DSL.castNull(Byte.class))
			.set(BONUS_CONCEPT.DESCRIPTION, "BONIF. FIJOS DISC.TURISMO.COM.HOSTE-RDL1/2023")
			.set(BONUS_CONCEPT.EXPRESSION, 
				PEC_16_QUOTA_01
				+ READ_ONLY
				+ "CHECK(\"420\".indexOf(TC2) >= 0 , \"<span>El contrato debe ser 420</span><div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
				+ CHECK_PARCIALIDAD_GE_50
				+ CUANTIA_262
				+ "/**/"
				)
			.newRecord()
			.set(BONUS_CONCEPT.DOMAIN, 0)
			.set(BONUS_CONCEPT.TYPE, DSL.castNull(Byte.class))
			.set(BONUS_CONCEPT.DESCRIPTION, "BONIF. CEUTA/MELILLA RDL 1/2023 CUENTA AJENA-RDL1/2023")
			.set(BONUS_CONCEPT.EXPRESSION, 
				PEC_16_QUOTA_01
				+ READ_ONLY
				+ "CHECK(\"150,250,350,109,209,309\".indexOf(TC2) >= 0 , \"<span>El contrato debe ser 150, 250, 350, 109, 209 ó 309</span><div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
				+ CHECK_PARCIALIDAD_GE_50
				+ CUANTIA_262 
				+ "/**/")
			.newRecord()
			.set(BONUS_CONCEPT.DOMAIN, 0)
			.set(BONUS_CONCEPT.TYPE, DSL.castNull(Byte.class))
			.set(BONUS_CONCEPT.DESCRIPTION, "BONIF. TRABAJ.SUSTITUIDO NCM/RIESGOS-RDL1/2023")
			.set(BONUS_CONCEPT.EXPRESSION, 
				PEC_16_QUOTA_01
				+ READ_ONLY
				+ CHECK_PARCIALIDAD_GE_50
				+ CUANTIA_366 
				+ "/**/")
			.newRecord()
			.set(BONUS_CONCEPT.DOMAIN, 0)
			.set(BONUS_CONCEPT.TYPE, DSL.castNull(Byte.class))
			.set(BONUS_CONCEPT.DESCRIPTION, "BONIF. SUSTITUCIÓN POR DESCANSO DE NAC.CUIDADO MENOR/RIESGO EMBARAZO O LACTANCIA-RDL1/2023")
			.set(BONUS_CONCEPT.EXPRESSION, 
				PEC_16_QUOTA_01
				+ READ_ONLY
				+ "CHECK(\"410,510\".indexOf(TC2) >= 0 , \"<span>El contrato debe ser 410 ó 510</span><div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
				+ CHECK_PARCIALIDAD_GE_50
				+ CUANTIA_366
				+"/**/")
			.newRecord()
			.set(BONUS_CONCEPT.DOMAIN, 0)
			.set(BONUS_CONCEPT.TYPE, DSL.castNull(Byte.class))
			.set(BONUS_CONCEPT.DESCRIPTION, "BONIF. SUSTITUCIÓN DISCAPACIDAD EN SITUACIÓN DE INCAPACIDAD TEMPORAL-RDL1/2023")
			.set(BONUS_CONCEPT.EXPRESSION, 
				PEC_16_QUOTA_01
				+ READ_ONLY
				+ "CHECK(\"410,510\".indexOf(TC2) >= 0 , \"<span>El contrato debe ser 410 ó 510</span><div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
				+ CHECK_PARCIALIDAD_GE_50
				+ CUANTIA_366
				+"/**/")
			.newRecord()
			.set(BONUS_CONCEPT.DOMAIN, 0)
			.set(BONUS_CONCEPT.TYPE, DSL.castNull(Byte.class))
			.set(BONUS_CONCEPT.DESCRIPTION, "BONIF. VÍCTIMAS DE VIOLENCIA DE GÉNERO/SEXUALES/T.SERES-RDL1/2023")
			.set(BONUS_CONCEPT.EXPRESSION, 
				PEC_16_QUOTA_01
				+ READ_ONLY
				+ "CHECK(\"150,250,350\".indexOf(TC2) >= 0 , \"<span>El contrato debe ser 150, 250 ó 350</span><div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
				+ CHECK_PARCIALIDAD_GE_50
				+ CUANTIA_128
				+"/**/")
			.newRecord()
			.set(BONUS_CONCEPT.DOMAIN, 0)
			.set(BONUS_CONCEPT.TYPE, DSL.castNull(Byte.class))
			.set(BONUS_CONCEPT.DESCRIPTION, "BONIF. EXCLUIDO SOCIALES INDEF.-RDL1/2023")
			.set(BONUS_CONCEPT.EXPRESSION, 
				PEC_16_QUOTA_01
				+ READ_ONLY
				+ "CHECK(\"150,250,350\".indexOf(TC2) >= 0 , \"<span>El contrato debe ser 150, 250 ó 350</span><div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
				+ CHECK_PARCIALIDAD_GE_50
				+ CUANTIA_128
				+"/**/")
			.newRecord()
			.set(BONUS_CONCEPT.DOMAIN, 0)
			.set(BONUS_CONCEPT.TYPE, DSL.castNull(Byte.class))
			.set(BONUS_CONCEPT.DESCRIPTION, "BONIF. EXCLUIDOS SOCIALES EMP.INS.12M.ANTES INDEF.-RDL1/2023")
			.set(BONUS_CONCEPT.EXPRESSION, 
				PEC_16_QUOTA_01
				+ READ_ONLY
				+ "CHECK(\"150,250,350\".indexOf(TC2) >= 0 , \"<span>El contrato debe ser 150, 250 ó 350</span><div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
				+ CHECK_PARCIALIDAD_GE_50
				+ CUANTIA_128
				+"/**/")
			.newRecord()
			.set(BONUS_CONCEPT.DOMAIN, 0)
			.set(BONUS_CONCEPT.TYPE, DSL.castNull(Byte.class))
			.set(BONUS_CONCEPT.DESCRIPTION, "BONIF. CONTRATACIÓN INDEFINIDA CAPACIDAD INTELECTUAL LÍMITE-RDL1/2023")
			.set(BONUS_CONCEPT.EXPRESSION, 
				PEC_16_QUOTA_01
				+ READ_ONLY
				+ "CHECK(\"150,250,350\".indexOf(TC2) >= 0 , \"<span>El contrato debe ser 150, 250 ó 350</span><div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
				+ CHECK_PARCIALIDAD_GE_50
				+ CUANTIA_128
				+"/**/")
			.newRecord()
			.set(BONUS_CONCEPT.DOMAIN, 0)
			.set(BONUS_CONCEPT.TYPE, DSL.castNull(Byte.class))
			.set(BONUS_CONCEPT.DESCRIPTION, "BONIF. DESEMPLEADOS 12 MESES EN 18 MESES-RDL1/2023")
			.set(BONUS_CONCEPT.EXPRESSION, 
				PEC_16_QUOTA_01
				+ READ_ONLY
				+ "CHECK(\"150,250,350\".indexOf(TC2) >= 0 , \"<span>El contrato debe ser 150, 250 ó 350</span><div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
				+ CHECK_PARCIALIDAD_GE_50
				+ CUANTIA_128
				+"/**/")
			.newRecord()
			.set(BONUS_CONCEPT.DOMAIN, 0)
			.set(BONUS_CONCEPT.TYPE, DSL.castNull(Byte.class))
			.set(BONUS_CONCEPT.DESCRIPTION, "BONIF. TRANSFORMACIÓN EN INDEFINIDOS DE CONTRATOS FORMATIVOS-RDL1/2023")
			.set(BONUS_CONCEPT.EXPRESSION, 
				PEC_16_QUOTA_01
				+ READ_ONLY
				+ "CHECK(\"150,250,350\".indexOf(TC2) >= 0 , \"<span>El contrato debe ser 150, 250 ó 350</span><div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
				+ CHECK_PARCIALIDAD_GE_50
				+ CUANTIA_128
				+"/**/")
			.newRecord()
			.set(BONUS_CONCEPT.DOMAIN, 0)
			.set(BONUS_CONCEPT.TYPE, DSL.castNull(Byte.class))
			.set(BONUS_CONCEPT.DESCRIPTION, "BONIF. VÍCTIMAS DEL TERRORISMO-RDL1/2023")
			.set(BONUS_CONCEPT.EXPRESSION, 
				PEC_16_QUOTA_01
				+ READ_ONLY
				+ "CHECK(\"150,250,350\".indexOf(TC2) >= 0 , \"<span>El contrato debe ser 150, 250 ó 350</span><div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
				+ CHECK_PARCIALIDAD_GE_50
				+ CUANTIA_128
				+"/**/")
			.newRecord()
			.set(BONUS_CONCEPT.DOMAIN, 0)
			.set(BONUS_CONCEPT.TYPE, DSL.castNull(Byte.class))
			.set(BONUS_CONCEPT.DESCRIPTION, "BONIF. CONTRATACIÓN INDEFINIDA FORMACIÓN PRÁCTICA-RDL1/2023")
			.set(BONUS_CONCEPT.EXPRESSION, 
				PEC_16_QUOTA_01
				+ READ_ONLY
				+ "CHECK(\"150,250,350\".indexOf(TC2) >= 0 , \"<span>El contrato debe ser 150, 250 ó 350</span><div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
				+ CHECK_PARCIALIDAD_GE_50
				+ CUANTIA_138
				+"/**/")
			.newRecord()
			.set(BONUS_CONCEPT.DOMAIN, 0)
			.set(BONUS_CONCEPT.TYPE, DSL.castNull(Byte.class))
			.set(BONUS_CONCEPT.DESCRIPTION, "BONIF. INCORPORACIÓN SOCIO FORMACIÓN PRÁCTICA-RDL1/2023")
			.set(BONUS_CONCEPT.EXPRESSION, 
				PEC_16_QUOTA_01
				+ READ_ONLY
				+ "CHECK(\"150,250,350\".indexOf(TC2) >= 0 , \"<span>El contrato debe ser 150, 250 ó 350</span><div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
				+ CHECK_PARCIALIDAD_GE_50
				+ CUANTIA_138
				+"/**/")
			.newRecord()
			.set(BONUS_CONCEPT.DOMAIN, 0)
			.set(BONUS_CONCEPT.TYPE, DSL.castNull(Byte.class))
			.set(BONUS_CONCEPT.DESCRIPTION, "BONIF. CAMBIO PUESTO TRABAJO RIESGO EMBARAZO/LACTANCIA-RDL1/2023")
			.set(BONUS_CONCEPT.EXPRESSION, 
				PEC_16_QUOTA_01
				+ READ_ONLY
				+ CHECK_PARCIALIDAD_GE_50
				+ CUANTIA_138
				+"/**/")
			.newRecord()
			.set(BONUS_CONCEPT.DOMAIN, 0)
			.set(BONUS_CONCEPT.TYPE, DSL.castNull(Byte.class))
			.set(BONUS_CONCEPT.DESCRIPTION, "BONIF. CAMBIO PUESTO TRABAJO ENFERMEDAD PROFESIONAL-RDL1/2023")
			.set(BONUS_CONCEPT.EXPRESSION, 
				PEC_16_QUOTA_01
				+ READ_ONLY
				+ CHECK_PARCIALIDAD_GE_50
				+ CUANTIA_138
				+"/**/")

			.newRecord()
			.set(BONUS_CONCEPT.DOMAIN, 0)
			.set(BONUS_CONCEPT.TYPE, DSL.castNull(Byte.class))
			.set(BONUS_CONCEPT.DESCRIPTION, "BONIF. CONTRATO. FORMACION ALTERNANCIA-RDL1/2023")
			.set(BONUS_CONCEPT.EXPRESSION, 
				PEC_16_QUOTA_01
				+ READ_ONLY
				+ "CHECK(\"421\".indexOf(TC2) >= 0 , \"<span>El contrato debe ser de formación en alternancia 421</span><div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
				+ CHECK_PARCIALIDAD_GE_50
				+ "SELF.addDeduction(\"BONIF\", \"BONIF. CONTRATO. FORMACION ALTERNANCIA-RDL1/2023\", \"TC2 == '421' ? (-MIN(( DIAS_COTIZADOS >= DIAS_MES ? 28.00 :  0.93 * DIAS_COTIZADOS )  * COEFICIENTE_PARCIALIDAD, CGC + FP + DESMPL)) : HIDE()\");"
				+ CUANTIA_91
				+"/**/")
			
			
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
