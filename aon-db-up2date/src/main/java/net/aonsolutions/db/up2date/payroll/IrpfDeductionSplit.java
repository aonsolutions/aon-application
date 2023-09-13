package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.DeductionConcept;
import com.esferalia.aon.jooq.tables.records.DeductionConceptRecord;
import com.esferalia.aon.jooq.tables.records.SystemDeductionRecord;

import net.aonsolutions.db.up2date.Update;

public class IrpfDeductionSplit implements Update {

	public static final IrpfDeductionSplit IRPFDEDUCTIONSPLIT = new IrpfDeductionSplit();
	
	private IrpfDeductionSplit() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			Integer [] irpfConcepts = 
			dslContext
			.select()
			.from(DEDUCTION_CONCEPT)
			.where(DEDUCTION_CONCEPT.TYPE.eq((byte)6))
			.fetchStreamInto(DEDUCTION_CONCEPT).map( DeductionConceptRecord::getId ).toArray(Integer[]::new )
			;
			
			SystemDeductionRecord [] irpfDeductions =
			dslContext
			.select()
			.from(SYSTEM_DEDUCTION)
			.where(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.in(irpfConcepts))
			.and(SYSTEM_DEDUCTION.EXPRESSION.likeRegex("[:blank:]*BASE_IRPF[:blank:]*\\*"))
			.fetchStreamInto(SYSTEM_DEDUCTION)
			.toArray(SystemDeductionRecord[]::new )
			;
			
			
			for (SystemDeductionRecord irpfDeduction : irpfDeductions) {
			    dslContext
			    .insertInto(SYSTEM_DEDUCTION)
			    .set(SYSTEM_DEDUCTION.DOMAIN, irpfDeduction.getDomain())
			    .set(SYSTEM_DEDUCTION.MONTH, irpfDeduction.getMonth())
			    .set(SYSTEM_DEDUCTION.END_DATE, irpfDeduction.getEndDate())
			    .set(SYSTEM_DEDUCTION.START_DATE, irpfDeduction.getStartDate())
			    .set(SYSTEM_DEDUCTION.DESCRIPTION, "IRPF Retribución Dineraria")
			    .set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, irpfDeduction.getDeductionConcept())
			    .set(SYSTEM_DEDUCTION.DESCRIPTION_DECORABLE, irpfDeduction.getDescriptionDecorable())
			    .set(SYSTEM_DEDUCTION.EXPRESSION, "BASE_IRPF_DINERO * PORCENTAJE_IRPF/100")
			    .newRecord()
			    .set(SYSTEM_DEDUCTION.DOMAIN, irpfDeduction.getDomain())
			    .set(SYSTEM_DEDUCTION.MONTH, irpfDeduction.getMonth())
			    .set(SYSTEM_DEDUCTION.END_DATE, irpfDeduction.getEndDate())
			    .set(SYSTEM_DEDUCTION.START_DATE, irpfDeduction.getStartDate())
			    .set(SYSTEM_DEDUCTION.DESCRIPTION, "IRPF Retribución en Especie")
			    .set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, irpfDeduction.getDeductionConcept())
			    .set(SYSTEM_DEDUCTION.DESCRIPTION_DECORABLE, irpfDeduction.getDescriptionDecorable())
			    .set(SYSTEM_DEDUCTION.EXPRESSION, "_P=(BASE_IRPF_ESPECIE * PORCENTAJE_IRPF/100 - (isdef IRPF_CTA_ESP ? IRPF_CTA_ESP : 0.00)); (_P > 0.0049 ) ? _P : HIDE()")
			    .newRecord()
			    .set(SYSTEM_DEDUCTION.DOMAIN, irpfDeduction.getDomain())
			    .set(SYSTEM_DEDUCTION.MONTH, irpfDeduction.getMonth())
			    .set(SYSTEM_DEDUCTION.END_DATE, irpfDeduction.getEndDate())
			    .set(SYSTEM_DEDUCTION.START_DATE, irpfDeduction.getStartDate())
			    .set(SYSTEM_DEDUCTION.DESCRIPTION, "IRPF Ingreso a Cuenta Especie a cargo de la Empresa")
			    .set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, irpfDeduction.getDeductionConcept())
			    .set(SYSTEM_DEDUCTION.DESCRIPTION_DECORABLE, irpfDeduction.getDescriptionDecorable())
			    .set(SYSTEM_DEDUCTION.EXPRESSION, "isdef BASE_CTA_ESP ? BASE_CTA_ESP : HIDE(); IRPF_CTA_ESP")
			    .execute()
			    ;
			    
			    irpfDeduction.delete();
			}
			
			dslContext
			.delete(SYSTEM_DEDUCTION)
			.where(SYSTEM_DEDUCTION.TYPE.eq((byte)8))
			.and(SYSTEM_DEDUCTION.EXPRESSION.like("%BASE_CTA_ESP%"))
			.execute();
			
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
