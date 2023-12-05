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

public class RealDecreeLaw012023421Fix implements Update {
	public static RealDecreeLaw012023421Fix REALDECREELAW012023421FIX = new RealDecreeLaw012023421Fix();


	
	private RealDecreeLaw012023421Fix() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
						
			dslContext
			.update(BONUS_CONCEPT)
			.set(BONUS_CONCEPT.EXPRESSION, 
				"/*pec:16,quota:01*/"
				+ "/*read-only*/"
				+ "CHECK(\"421\".indexOf(TC2) >= 0 , \"<span>El contrato debe ser de formación en alternancia 421</span><div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
				+ "SELF.addDeduction(\"BONIF\", \"BONIF. CONTRATO. FORMACION ALTERNANCIA-RDL1/2023\", \"TC2 == '421' ? (-MIN(( DIAS_COTIZADOS >= DIAS_MES ? 28.00 :  0.93 * DIAS_COTIZADOS ), CGC + FP + DESMPL)) : HIDE()\");"
				+ "MIN( ( DIAS_COTIZADOS >= DIAS_MES ? 91.00 :  3.03 * DIAS_COTIZADOS ), CGC_E + IT_E + IMS_E + FOGASA_E + FP_E + DESMPL_E )"
				+"/**/")
			.where(BONUS_CONCEPT.DOMAIN.eq(0))
			.and(BONUS_CONCEPT.TYPE.isNull())
			.and(BONUS_CONCEPT.DESCRIPTION.eq("BONIF. CONTRATO. FORMACION ALTERNANCIA-RDL1/2023"))
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
