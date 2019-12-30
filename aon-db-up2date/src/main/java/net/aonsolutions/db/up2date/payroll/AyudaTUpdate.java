package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.Keys.FK_AGREEMENT_LEVEL_CATEGORY_AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.Keys.FK_AGREEMENT_LEVEL_DATA_AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.Keys.FK_AGREEMENT_PAYMENT_PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static java.lang.Double.parseDouble;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

import java.sql.Connection;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.mvel2.MVEL;

import com.esferalia.aon.jooq.tables.records.AgreementLevelDataRecord;
import com.esferalia.aon.jooq.tables.records.AgreementPaymentRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;

import net.aonsolutions.db.up2date.Update;

public class AyudaTUpdate implements Update {
	
	private static final double DELTA = 0.01;

	public static final AyudaTUpdate AYUDATUPDATE = new AyudaTUpdate();
	
	private static final Date EPOCH = new Date(0);
	private static final Date FOREVER = null;
	
	private static final String WARNNING = "HIDE(\""
	+"<div>Este convenio ha sido modificado en la &uacute;ltima actualizaci&oacute;n."
	+"El convenio original est&aacute; disponible en la papelera.</div>"
	+"<div>Se ha detectado que el salario base y las pagas extras son iguales."
	+"Se han igualado las pagas al salario base eliminado los importes 'duplicados' de las pagas.</div>"
	+"<div>Si desea recuperar el convenio original, envie este convenio a la papelera.</div>"
	+"<div>&nbsp;</div><div class='aon-text-right'>Disculpe las molestias, <span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
	;
	

	private AyudaTUpdate() {
	}

	@Override
	public void upgrade(Connection connection) {
		
		
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
			
		boolean ayudaT = dslContext.fetchCount(
		dslContext
		.select()
		.from(DOMAIN)
		.where(DOMAIN.NAME.eq("ayudat.aonsolutions.net"))
		) > 0;
		
		if ( !ayudaT )
			return;
		
		
		
		dslContext.transaction( (config) -> {
			dslContext
			.select()
			.from(AGREEMENT)
			.where(AGREEMENT.ID.gt(0))
			.and(AGREEMENT.DOMAIN.gt(0))
			.fetchStreamInto(AGREEMENT)
			.forEach(a -> {
				fixPluses(dslContext, a);
				fixExtras(dslContext, a);
			})
			;
		});
	}
	
	private void fixPluses(DSLContext dslContext, AgreementRecord a ) {
		dslContext
		.select()
		.from(AGREEMENT_PAYMENT)
		.innerJoin(PAYMENT_CONCEPT)
		.onKey(FK_AGREEMENT_PAYMENT_PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.CODE.likeRegex("^__[0-9]*"))
		.and(PAYMENT_CONCEPT.EXPRESSION.likeRegex("^PLUS_"))
		.fetchStreamInto(PAYMENT_CONCEPT)
		.forEach( (paymentConcept) -> {
//		System.out.println(paymentConcept.getExpression() + ", " + paymentConcept.getCode() );
		 dslContext
		.update(PAYMENT_CONCEPT)
		.set(PAYMENT_CONCEPT.CODE, DSL.replace(PAYMENT_CONCEPT.EXPRESSION, "PLUS_", ""))
		.where(PAYMENT_CONCEPT.ID.eq(paymentConcept.getId()))
		.execute();
		}
		)
		;
	}

	private void fixExtras(DSLContext dslContext, AgreementRecord a ) {
		
		List<Integer> extraConcepts =
		dslContext
		.select()
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("PAGA_EXTRA"))
		.fetch(PAYMENT_CONCEPT.ID)
		;
		
		
		AgreementPaymentRecord extras []= 
		dslContext
		.select()
		.from(AGREEMENT_PAYMENT)
		.where(AGREEMENT_PAYMENT.AGREEMENT.eq(a.getId()))
		.and(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.in(extraConcepts))
		.fetchStreamInto(AGREEMENT_PAYMENT)
		.toArray(AgreementPaymentRecord[]::new)
		;
		
		if ( extras.length == 0 )
			return;
		
		//INPUT("/*user*/<VAR>/**/",PAGA_EXTRA_HELP)
		Pattern extraPattern = Pattern.compile("INPUT\\(\"/\\*user\\*/(.*)/\\*\\*/\",PAGA_EXTRA_HELP\\)");
		
		String extraVars []= 
		Arrays.stream(extras)
		.map(p -> p.getExpression())
		.map(d -> extraPattern.matcher(d))
		.filter(m -> m.find())
		.map(m -> m.group(1))
		.toArray(String[]::new)
		;
		
		Map<Integer, List<AgreementLevelDataRecord>> levelsMap = 
		dslContext
		.select()
		.from(AGREEMENT_LEVEL_DATA)
		.innerJoin(AGREEMENT_LEVEL)
		.on(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.eq(AGREEMENT_LEVEL.ID))
		.where(AGREEMENT_LEVEL.AGREEMENT.eq(a.getId()))
		.fetchStreamInto(AGREEMENT_LEVEL_DATA)
		.collect(Collectors.groupingBy(AgreementLevelDataRecord::getAgreementLevel))
		;
		
		try {
		

			int diffs = 0; 
			for ( Map.Entry<Integer, List<AgreementLevelDataRecord>> entry : levelsMap.entrySet() ) {
			
				Map<String, Double> vars = 
				entry.getValue().stream()
				.collect(
				groupingBy(AgreementLevelDataRecord::getName, 
				summingDouble(r -> parse(r.getExpression())))
				);
				

				double salarioMensual = vars.getOrDefault("SALARIO_MENSUAL", 0.00);
				if ( salarioMensual == 0.00 )
					salarioMensual = vars.getOrDefault("SALARIO_DIARIO", 0.00) * 30.00;
				if ( salarioMensual == 0.00 )
					salarioMensual = vars.getOrDefault("SALARIO_ANUAL", 0.00) / 14.00;
				
				
				for ( String extraVar: extraVars ) {
					double extraValue = vars.getOrDefault(extraVar, 0.00);
					if ( Math.abs(extraValue - salarioMensual) > DELTA && calculateEditInstance(extraValue, salarioMensual) > 1) {
//						System.out.println(a.getDescription() + " , " + extraValue + " = " + salarioMensual );
						diffs++;
						break;
					}
				}
				
			}
			
			if ( diffs > 1 )
				throw new InterruptedException();
			

			move2Trash(dslContext, a.getId());

			// Clean PAGA_EXTRA variables ...
			dslContext.delete(AGREEMENT_LEVEL_DATA)
			.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.in(levelsMap.keySet()))
			.and(AGREEMENT_LEVEL_DATA.NAME.in(extraVars))
			.execute()
			;
			
			Set<Integer> extraIds = Arrays.stream(extras)
			.map(e->e.getId()).collect(Collectors.toSet());
			
			dslContext.update(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.EXPRESSION, "SALARIO_BASE" )
			.where(AGREEMENT_PAYMENT.ID.in(extraIds))
			.execute()
			;
			dslContext.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN,a.getDomain())
			.set(AGREEMENT_PAYMENT.AGREEMENT,a.getId())
			.set(AGREEMENT_PAYMENT.TYPE, (byte) 1)
			.set(AGREEMENT_PAYMENT.START_DATE, EPOCH)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 1)
			.set(AGREEMENT_PAYMENT.EXPRESSION, WARNNING)
			.set(AGREEMENT_PAYMENT.DESCRIPTION, "WARNNING")	
			.execute()
			;
			
			System.out.println("Agreement '" + a.getDescription() +"' fixed." );

		} catch ( InterruptedException e ) {
		} 
		
	}
	
	protected void move2Trash(DSLContext dslContext, Integer agreement ) throws InterruptedException {
		
		int count = 
		dslContext.fetchCount(
		dslContext
		.select()
		.from(AGREEMENT)
		.where(AGREEMENT.ID.eq(agreement*-1))
		);
		if ( count > 0 ) {
			throw new InterruptedException();
		}
		
		
		
		dslContext
		.insertInto(AGREEMENT)
		.columns(
		AGREEMENT.ID
		, AGREEMENT.DOMAIN
		, AGREEMENT.DESCRIPTION
		, AGREEMENT.CALENDAR)
		.select(
		dslContext.select(
		AGREEMENT.ID.mul(-1)
		, AGREEMENT.DOMAIN
		, AGREEMENT.DESCRIPTION
		, AGREEMENT.CALENDAR)
		.from(AGREEMENT)
		.where(AGREEMENT.ID.eq(agreement)))
		.execute()
		;
		
		dslContext
		.insertInto(AGREEMENT_PAYMENT)
		.columns(
		AGREEMENT_PAYMENT.ID
		, AGREEMENT_PAYMENT.DOMAIN
		, AGREEMENT_PAYMENT.AGREEMENT
		, AGREEMENT_PAYMENT.PAYMENT_CONCEPT
		, AGREEMENT_PAYMENT.START_DATE
		, AGREEMENT_PAYMENT.END_DATE
		, AGREEMENT_PAYMENT.DESCRIPTION
		, AGREEMENT_PAYMENT.DESCRIPTION_DECORABLE
		, AGREEMENT_PAYMENT.EXPRESSION
		, AGREEMENT_PAYMENT.IRPF_EXPRESSION
		, AGREEMENT_PAYMENT.QUOTE_EXPRESSION
		, AGREEMENT_PAYMENT.TYPE
		, AGREEMENT_PAYMENT.SALARY_TYPE
		, AGREEMENT_PAYMENT.MONTH)
		.select(
		dslContext.select(
		AGREEMENT_PAYMENT.ID.mul(-1)
		, AGREEMENT_PAYMENT.DOMAIN
		, AGREEMENT_PAYMENT.AGREEMENT.mul(-1)
		, AGREEMENT_PAYMENT.PAYMENT_CONCEPT
		, AGREEMENT_PAYMENT.START_DATE
		, AGREEMENT_PAYMENT.END_DATE
		, AGREEMENT_PAYMENT.DESCRIPTION
		, AGREEMENT_PAYMENT.DESCRIPTION_DECORABLE
		, AGREEMENT_PAYMENT.EXPRESSION
		, AGREEMENT_PAYMENT.IRPF_EXPRESSION
		, AGREEMENT_PAYMENT.QUOTE_EXPRESSION
		, AGREEMENT_PAYMENT.TYPE
		, AGREEMENT_PAYMENT.SALARY_TYPE
		, AGREEMENT_PAYMENT.MONTH)
		.from(AGREEMENT_PAYMENT)
		.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement))
		)
		.execute()
		;

		dslContext
		.insertInto(AGREEMENT_EXTRA)
		.columns(
		AGREEMENT_EXTRA.ID
		, AGREEMENT_EXTRA.DOMAIN
		, AGREEMENT_EXTRA.AGREEMENT
		, AGREEMENT_EXTRA.AGREEMENT_PAYMENT
		, AGREEMENT_EXTRA.START_DATE
		, AGREEMENT_EXTRA.END_DATE
		, AGREEMENT_EXTRA.ISSUE_DATE)
		.select(
		dslContext.select(
		AGREEMENT_EXTRA.ID.mul(-1)
		, AGREEMENT_EXTRA.DOMAIN
		, AGREEMENT_EXTRA.AGREEMENT.mul(-1)
		, AGREEMENT_EXTRA.AGREEMENT_PAYMENT.mul(-1)
		, AGREEMENT_EXTRA.START_DATE
		, AGREEMENT_EXTRA.END_DATE
		, AGREEMENT_EXTRA.ISSUE_DATE)
		.from(AGREEMENT_EXTRA)
		.where(AGREEMENT_EXTRA.AGREEMENT.eq(agreement)))
		.execute()
		;
		
		dslContext
		.insertInto(AGREEMENT_DATA)
		.columns(
		AGREEMENT_DATA.ID
		, AGREEMENT_DATA.DOMAIN
		, AGREEMENT_DATA.AGREEMENT
		, AGREEMENT_DATA.START_DATE
		, AGREEMENT_DATA.END_DATE
		, AGREEMENT_DATA.NAME
		, AGREEMENT_DATA.EXPRESSION)
		.select(
		dslContext.select(
		AGREEMENT_DATA.ID.mul(-1)
		, AGREEMENT_DATA.DOMAIN
		, AGREEMENT_DATA.AGREEMENT.mul(-1)
		, AGREEMENT_DATA.START_DATE
		, AGREEMENT_DATA.END_DATE
		, AGREEMENT_DATA.NAME
		, AGREEMENT_DATA.EXPRESSION)
		.from(AGREEMENT_DATA)
		.where(AGREEMENT_DATA.AGREEMENT.eq(agreement)))
		.execute()
		;
		
		dslContext
		.insertInto(AGREEMENT_LEVEL)
		.columns(
		AGREEMENT_LEVEL.ID
		, AGREEMENT_LEVEL.DOMAIN
		, AGREEMENT_LEVEL.AGREEMENT
		, AGREEMENT_LEVEL.DESCRIPTION)
		.select(
		dslContext.select(
		AGREEMENT_LEVEL.ID.mul(-1)
		, AGREEMENT_LEVEL.DOMAIN
		, AGREEMENT_LEVEL.AGREEMENT.mul(-1)
		, AGREEMENT_LEVEL.DESCRIPTION)
		.from(AGREEMENT_LEVEL)
		.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement)))
		.execute()
		;

		dslContext
		.insertInto(AGREEMENT_LEVEL_CATEGORY)
		.columns(
		AGREEMENT_LEVEL_CATEGORY.ID
		, AGREEMENT_LEVEL_CATEGORY.DOMAIN
		, AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL
		, AGREEMENT_LEVEL_CATEGORY.DESCRIPTION)
		.select(
		dslContext.select(
		AGREEMENT_LEVEL_CATEGORY.ID.mul(-1)
		, AGREEMENT_LEVEL_CATEGORY.DOMAIN
		, AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.mul(-1)
		, AGREEMENT_LEVEL_CATEGORY.DESCRIPTION)
		.from(AGREEMENT_LEVEL_CATEGORY)
		.join(AGREEMENT_LEVEL)
		.onKey(FK_AGREEMENT_LEVEL_CATEGORY_AGREEMENT_LEVEL)
		.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement)))
		.execute()
		;

		dslContext
		.insertInto(AGREEMENT_LEVEL_DATA)
		.columns(
		AGREEMENT_LEVEL_DATA.ID
		, AGREEMENT_LEVEL_DATA.DOMAIN
		, AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL
		, AGREEMENT_LEVEL_DATA.START_DATE
		, AGREEMENT_LEVEL_DATA.END_DATE
		, AGREEMENT_LEVEL_DATA.NAME
		, AGREEMENT_LEVEL_DATA.EXPRESSION )
		.select(dslContext.select(
		AGREEMENT_LEVEL_DATA.ID.mul(-1)
		, AGREEMENT_LEVEL_DATA.DOMAIN
		, AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.mul(-1)
		, AGREEMENT_LEVEL_DATA.START_DATE
		, AGREEMENT_LEVEL_DATA.END_DATE
		, AGREEMENT_LEVEL_DATA.NAME
		, AGREEMENT_LEVEL_DATA.EXPRESSION )
		.from(AGREEMENT_LEVEL_DATA)
		.join(AGREEMENT_LEVEL).onKey(FK_AGREEMENT_LEVEL_DATA_AGREEMENT_LEVEL)
		.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement)))
		.execute()
		;
	}

	
	private static double parse(String str) {
		try {
			return parseDouble(str);
		} catch ( NumberFormatException e ) {
			Map<String, Object> vars = new HashMap<String, Object>(){
				{
					put("SMI", 1050.00);
				}
			};
			return MVEL.eval(str, vars, Double.class);
		}
	}
	
	private static int calculateEditInstance(Number x, Number y) {
		return calculateEditInstance(x.toString(), y.toString());
	}
	
	private static int calculateEditInstance(String x, String y) {
		if (x.isEmpty()) {
		    return y.length();
		}
 
		if (y.isEmpty()) {
		    return x.length();
		} 
 
        int substitution = calculateEditInstance(x.substring(1), y.substring(1)) 
         + costOfSubstitution(x.charAt(0), y.charAt(0));
        int insertion = calculateEditInstance(x, y.substring(1)) + 1;
        int deletion = calculateEditInstance(x.substring(1), y) + 1;
 
        return min(substitution, insertion, deletion);
	}	
	
	private static int costOfSubstitution(char a, char b) {
		return a == b ? 0 : 1;
	}	   
   
   public static int min(int... numbers) {
       return Arrays.stream(numbers)
         .min().orElse(Integer.MAX_VALUE);
   }   
	
}
