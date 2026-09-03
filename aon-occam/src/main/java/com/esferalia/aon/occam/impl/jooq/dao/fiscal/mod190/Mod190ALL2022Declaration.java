package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod190;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.IrpfData.IRPF_DATA;
import static com.esferalia.aon.jooq.tables.IrpfDataAscendants.IRPF_DATA_ASCENDANTS;
import static com.esferalia.aon.jooq.tables.IrpfDataDescendients.IRPF_DATA_DESCENDIENTS;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.IrpfDataAscendantsRecord;
import com.esferalia.aon.jooq.tables.records.IrpfDataDescendientsRecord;
import com.esferalia.aon.jooq.tables.records.IrpfDataRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.type.Mod1902022Key;
import com.esferalia.aon.occam.api.model.type.PaymentType;
import com.esferalia.aon.occam.api.model.type.PaymentType.PaymentTypeVisitor;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod190ALL2022Declaration extends Mod190Declaration {
	
	private static final String PREST_IT = "PREST_IT";


	Mod190 insertDetailsFromSalary(AONContext ctx, final Mod190 mod190) {
		Date firstDay = AonDateUtils.getYearFirstDay(mod190.getYear());
		Date lastDay = AonDateUtils.getYearLastDay(mod190.getYear());
		Field<Integer> birthYear = DSL.year(PERSON.BIRTH_DATE);
		final TreeMap<String, Mod190Detail> map = new TreeMap<>();
		final HashSet<Integer> salaries = new HashSet<>();
		ctx.getDslContext().select(
				 SALARY.ID
				,SALARY.EMPLOYEE_DOCUMENT
				,SALARY.EMPLOYEE_NAME
				,SALARY.TOTAL_IRPF
				,SALARY.IRPF_BASE
				,SALARY.SOCIAL_SECURITY_CONTRIBUTIONS

				,ENTERPRISE_CCC.TYPE
				
				,SALARY_PAYMENT.TYPE
				,SALARY_PAYMENT.PAYMENT_CONCEPT
				,SALARY_PAYMENT.AMOUNT
				,SALARY_PAYMENT.IRPF
				
				,CONTRACT.SS_REGIME
				
				,PERSON.REGISTRY
				,birthYear)
		.from(SALARY_PAYMENT)
		.join(SALARY).on(SALARY_PAYMENT.SALARY.equal(SALARY.ID))
		.join(CONTRACT).on(SALARY.CONTRACT.equal(CONTRACT.ID))
		.join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
		.join(PERSON).on(PERSON.REGISTRY.equal(CONTRACT.PERSON))
		.leftOuterJoin(ENTERPRISE_CCC).on(CONTRACT.ENTERPRISE_CCC.equal(ENTERPRISE_CCC.ID))
		.leftJoin(CONTRACT_DATA).on(SALARY.CONTRACT.equal(CONTRACT_DATA.CONTRACT).and(CONTRACT_DATA.NAME.equal("IRPF_TYPE")))
		.where(SALARY.ISSUE_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
		.and(WORKPLACE.ENTERPRISE.equal(mod190.getEnterprise()))
		.and(WORKPLACE.ECONOMICAGREEMENT.equal(mod190.getAdministration().value()))
		.and(SALARY.TYPE.in(SalaryType.IRPF_SALARIES )) // Skip SLD ( L00, L13... )
		.and(CONTRACT_DATA.EXPRESSION.isNull().or(CONTRACT_DATA.EXPRESSION.ne("\"3\"")))  // No tener en cuenta los no residentes
		.orderBy(SALARY.EMPLOYEE_DOCUMENT)
		.fetch()
		.stream()
		.forEach(rec -> {
				String document = rec.getValue(SALARY.EMPLOYEE_DOCUMENT);
				Integer person = rec.getValue(PERSON.REGISTRY);
				
				Byte p = rec.getValue(SALARY_PAYMENT.TYPE);
				PaymentType paymentType = (p == null)?PaymentType.CRA_0001 : PaymentType.values()[p.intValue()];
				paymentType.accept(new PaymentTypeVisitor() {

					@Override
					public void visitNonStructuralHours(PaymentType paymentType) {
						visitOther(paymentType);
					}

					@Override
					public void visitStructuralHours(PaymentType paymentType) {
						visitOther(paymentType);
					}

					@Override
					public void visitOther(PaymentType paymentType) {
						if (!isBoss()) visitAKey();
					}

					@Override
					public void visitSalaryInKind(PaymentType paymentType) {
						if (!isBoss()) visitAInKindKey();
					}
					
					@Override
					public void visitCompensation(PaymentType paymentType) {
						if (!isBoss()) visitCompensation();
					}
					
					@Override
					public void visitExpenses(PaymentType paymentType) {
						if (!isBoss()) visitExpenses();
					}
					
					@Override
					public void visitInsurance(PaymentType paymentType) {
						if (!isBoss()) visitInsurance();
					}
					
					@Override
					public void visitSupport(PaymentType paymentType) {
						if (!isBoss()) visitSupport();
					}

					// -------------------------------------------------------------------------------------
					// -------------------------------------------------------------------------------------
					
					private boolean isBoss() {
						double totalIrpf = rec.getValue(SALARY.TOTAL_IRPF);
						double totalIrpfBase = rec.getValue(SALARY.IRPF_BASE);
						double irpfBase = rec.getValue(SALARY_PAYMENT.IRPF);
						double irpfQuota = ( AonMathUtils.isZero( irpfBase) || AonMathUtils.isZero( totalIrpfBase) )
								?0.0
								:(irpfBase * totalIrpf / totalIrpfBase);
						
						Mod190Detail detail = null;
						
						if(isE01())
							detail = getDetail(document,person,Mod1902022Key.E,"01");
						else if(isE02())
							detail = getDetail(document,person,Mod1902022Key.E,"02");
						else if(isE03()) 
							detail = getDetail(document,person,Mod1902022Key.E,"03");
						else if(isE04())
							detail = getDetail(document,person,Mod1902022Key.E,"04");
						
						if(null != detail) {
							detail.setPerception(AonMathUtils.round(detail.getPerception() + irpfBase ));
							detail.setRetention(AonMathUtils.round(detail.getRetention() + irpfQuota ));
							Integer salary = rec.getValue(SALARY.ID);
							if (!salaries.contains(salary)) {
								salaries.add(salary);
								double ss = rec.getValue(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
								detail.setDeducibleExpense(AonMathUtils.round(detail.getDeducibleExpense() + ss ));
							}
						}
						
						return isE01() || isE02() || isE03() || isE04();
					}

					private void visitExpenses() {
						double amount = rec.getValue(SALARY_PAYMENT.AMOUNT);
						double irpfBase = rec.getValue(SALARY_PAYMENT.IRPF);
						
						// Parte exenta va a la L
						double expense = AonMathUtils.round(amount - irpfBase);  
						Mod190Detail detail = getDetail(document,person,Mod1902022Key.L,"01");
						detail.setPerception(AonMathUtils.round(detail.getPerception() + expense ));
						
						// Parte no exenta va a la A
						visitAKey();	
					}
					
					private void visitCompensation() {
						double amount = rec.getValue(SALARY_PAYMENT.AMOUNT);
						double totalIrpf = rec.getValue(SALARY.TOTAL_IRPF);
						double totalIrpfBase = rec.getValue(SALARY.IRPF_BASE);
						double irpfBase = rec.getValue(SALARY_PAYMENT.IRPF);
						double irpfQuota = ( AonMathUtils.isZero( irpfBase) ||  AonMathUtils.isZero( totalIrpfBase) )
								?0.0
								:(irpfBase * totalIrpf / totalIrpfBase);
						if ( AonMathUtils.isGreatherThanZero(irpfBase )) {
							visitAKey();		
						} else if(AonMathUtils.isGreatherThanZero(amount )) {
							//Esta comprobacion es por que puede haber payments CRA 0054 que tengan amount 0 (pe.: DIAS_PREAVISO)
							Mod190Detail detail = getDetail(document,person,Mod1902022Key.L,"05");
							detail.setPerception(AonMathUtils.round(detail.getPerception() + amount ));
							detail.setRetention(AonMathUtils.round(detail.getRetention() + irpfQuota ));
						}
					}
					
					private void visitInsurance() {
						double amount = rec.getValue(SALARY_PAYMENT.AMOUNT);
						double irpfBase = rec.getValue(SALARY_PAYMENT.IRPF);
						
						// Parte exenta va a la L
						double expense = AonMathUtils.round(amount - irpfBase);  
						if ( AonMathUtils.isGreatherThanZero(expense )) {
							Mod190Detail detail = getDetail(document,person,Mod1902022Key.L,"24");
							detail.setInKindPerception(AonMathUtils.round(detail.getInKindPerception() + expense ));
						} else {
							// Parte no exenta va a la A a la parte en especie
							double totalIrpf = rec.getValue(SALARY.TOTAL_IRPF);
							double totalIrpfBase = rec.getValue(SALARY.IRPF_BASE);
							double irpfQuota = ( AonMathUtils.isZero( irpfBase) || AonMathUtils.isZero( totalIrpfBase ) )
									?0.0
									:(irpfBase * totalIrpf / totalIrpfBase);
							
							Double enterpriseIrpfQuota = getEnterpriseIrpfQuota();
							
							Mod190Detail detail = getDetail(document,person,Mod1902022Key.A,null);
							detail.setInKindPerception(AonMathUtils.round(detail.getInKindPerception() + irpfBase ));
							detail.setInKindDeposit(AonMathUtils.round(detail.getInKindDeposit() + irpfQuota ));
							if(detail.getInKindDepositIL() - enterpriseIrpfQuota > 1)
								detail.setInKindOutputDeposit(enterpriseIrpfQuota);
						}
					}
					
					private void visitSupport() {
						double amount = rec.getValue(SALARY_PAYMENT.AMOUNT);
						double irpfBase = rec.getValue(SALARY_PAYMENT.IRPF);
						
						// Parte exenta va a la L
						double expense = AonMathUtils.round(amount - irpfBase);  
						if ( AonMathUtils.isGreatherThanZero(expense )) {
							Mod190Detail detail = getDetail(document,person,Mod1902022Key.L,"25");
							detail.setPerception(AonMathUtils.round(detail.getPerception() + expense ));
						} else
							// Parte no exenta va a la A
							visitAKey();	
					}
					
					private void visitAKey() {
						double totalIrpf = rec.getValue(SALARY.TOTAL_IRPF);
						double totalIrpfBase = rec.getValue(SALARY.IRPF_BASE);
						double irpfBase = rec.getValue(SALARY_PAYMENT.IRPF);
						double irpfQuota = ( AonMathUtils.isZero( irpfBase) || AonMathUtils.isZero( totalIrpfBase) )
								?0.0
								:(irpfBase * totalIrpf / totalIrpfBase);
						Mod190Detail detail = getDetail(document,person,Mod1902022Key.A,null);
						Integer salary = rec.getValue(SALARY.ID);
						if (!salaries.contains(salary)) {
							salaries.add(salary);
							double ss = rec.getValue(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
							detail.setDeducibleExpense(AonMathUtils.round(detail.getDeducibleExpense() + ss ));
						}
						String prest = rec.getValue( SALARY_PAYMENT.PAYMENT_CONCEPT);
						if (PREST_IT.equals(prest)) {
							detail.setPerceptionIL(AonMathUtils.round(detail.getPerceptionIL() + irpfBase ));
							detail.setRetentionIL(AonMathUtils.round(detail.getRetentionIL() + irpfQuota ));
						} else {
							detail.setPerception(AonMathUtils.round(detail.getPerception() + irpfBase ));
							detail.setRetention(AonMathUtils.round(detail.getRetention() + irpfQuota ));
						}
					}
					
					private void visitAInKindKey() {
						double totalIrpf = rec.getValue(SALARY.TOTAL_IRPF);
						double totalIrpfBase = rec.getValue(SALARY.IRPF_BASE);
						double irpfBase = rec.getValue(SALARY_PAYMENT.IRPF);
						double irpfQuota = ( AonMathUtils.isZero( irpfBase) || AonMathUtils.isZero( totalIrpfBase ) )
								?0.0
								:(irpfBase * totalIrpf / totalIrpfBase);
						Mod190Detail detail = getDetail(document,person,Mod1902022Key.A,null);
						String prest = rec.getValue( SALARY_PAYMENT.PAYMENT_CONCEPT);
						Double enterpriseIrpfQuota = getEnterpriseIrpfQuota();
						// Si esta exento deberia ir al L.24
						if(irpfQuota == 0.00) {
							visitInsurance();
						} else {
							if (PREST_IT.equals(prest)) {
								detail.setInKindPerceptionIL(AonMathUtils.round(detail.getInKindPerceptionIL() + irpfBase ));
								detail.setInKindDepositIL(AonMathUtils.round(detail.getInKindDepositIL() + irpfQuota ));
								if(detail.getInKindDepositIL() - enterpriseIrpfQuota > 1)
									detail.setInKindOutputDepositIL(enterpriseIrpfQuota);
							} else {
								detail.setInKindPerception(AonMathUtils.round(detail.getInKindPerception() + irpfBase ));
								detail.setInKindDeposit(AonMathUtils.round(detail.getInKindDeposit() + irpfQuota ));
								if(detail.getInKindDepositIL() - enterpriseIrpfQuota > 1)
									detail.setInKindOutputDeposit(enterpriseIrpfQuota);
							}
						}
					}
					
					private Double getEnterpriseIrpfQuota() {
						// INGRESO A CUENTA ESPECIE A CARGO DE LA EMPRESA
						// Sale de resta el IRPF del devengo menos el IRPF cubierto por la empresa para este devengo
						double totalIrpf = rec.getValue(SALARY.TOTAL_IRPF);
						double totalIrpfBase = rec.getValue(SALARY.IRPF_BASE);
						double irpfBase = rec.getValue(SALARY_PAYMENT.IRPF);
						double irpfQuota = ( AonMathUtils.isZero( irpfBase) || AonMathUtils.isZero( totalIrpfBase ) )
								?0.0
								:(irpfBase * totalIrpf / totalIrpfBase);
						
						// Esto es el total de aportacion? Si es así estaría bien saber cuanto aporta por devengo de tipo En Especie
						Record ifpfCTA = ctx.getDslContext().select().from(SALARY_DATA)
							.where(SALARY_DATA.NAME.eq("IRPF_CTA_ESP"))
							.and(SALARY_DATA.SALARY.eq(rec.getValue(SALARY.ID)))
							.fetchOne();
						
						double ifpfCTAESP = ifpfCTA == null ? 0.00 : Double.parseDouble(ifpfCTA.get(SALARY_DATA.EXPRESSION));
						double irpfQuotaEnterprise = 0.00;
						irpfQuotaEnterprise = irpfQuota - ifpfCTAESP;
						
						return irpfQuota == 0.00 || irpfQuotaEnterprise <= 0.00 ? 0.00 : irpfQuotaEnterprise;
					}

					private Mod190Detail getDetail(String document,int person, Mod1902022Key key, String subKey) {
						String mapKey =  document + "_" + person + "_" + key.getValue() + (subKey != null?("_" + subKey):"");
						if ( !map.containsKey(mapKey) ) {
							final Mod190Detail detail = new Mod190Detail();
							map.put(mapKey, detail);
							detail.setDomain(mod190.getDomain());
							detail.setMod190(mod190.getId());
							detail.setKey(key.getValue());
							detail.setSubKey(subKey);
							detail.setDocument(rec.getValue(SALARY.EMPLOYEE_DOCUMENT));
							detail.setName(rec.getValue(SALARY.EMPLOYEE_NAME));
							ctx.getDslContext().select(GEOZONE.CODE)
								.from(RADDRESS)
								.join(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
								.where(RADDRESS.REGISTRY.equal(rec.getValue(PERSON.REGISTRY)))
								.and(RADDRESS.TYPE.equal((byte) 0))
								.limit(1)
								.fetch()
								.stream()
								.forEach(
									province -> {
										try {detail.setProvince(Integer.parseInt(province.getValue(GEOZONE.CODE)));
										} catch (NumberFormatException e) {
											// nothing. If not a number,  not a valid province.
										}
									}
								);
							if (key == Mod1902022Key.A) {
								Integer birthData = rec.getValue(birthYear);
								detail.setBirthYear(birthData==null?0:birthData);
								fillLastIrpfDataByPerson(ctx,rec.getValue(PERSON.REGISTRY),firstDay, lastDay, detail);
							}
						}
						return map.get(mapKey);
					}
					
					// Regimen general asimilado && Adm./Consejero Negocio > 100.000
					private boolean isE01() {
						Byte irpfType = getIrpfType();
						Byte rgAsimilados = rec.getValue(ENTERPRISE_CCC.TYPE);
						return rgAsimilados != null && rgAsimilados.byteValue() == 4 && irpfType != null && irpfType.byteValue() == 2;
					}
					
					// Regimen general asimilado && Adm./Consejero Negocio < 100.000
					private boolean isE02() {
						Byte irpfType = getIrpfType();
						Byte rgAsimilados = rec.getValue(ENTERPRISE_CCC.TYPE);
						return rgAsimilados != null && rgAsimilados.byteValue() == 4 && irpfType != null && irpfType.byteValue() == 1;
					}
					
					// RETA  && Adm./Consejero Negocio < 100.000
					private boolean isE03() {
						Byte irpfType = getIrpfType();
						Byte ssRegime = rec.getValue(CONTRACT.SS_REGIME);
						return ssRegime != null && ssRegime.byteValue() == 3 && irpfType != null && irpfType.byteValue() == 1;
					}
					
					// RETA  && Adm./Consejero Negocio > 100.000
					private boolean isE04() {
						Byte irpfType = getIrpfType();
						Byte ssRegime = rec.getValue(CONTRACT.SS_REGIME);
						return ssRegime != null && ssRegime.byteValue() == 3 && irpfType != null && irpfType.byteValue() == 2;
					}
					
					private Byte getIrpfType() {
						// 0 == COMUN
						// 1 == Adm./Consejero Negocio < 100.000
						// 2 == Adm./Consejero Negocio > 100.000
						
						Record1<String> irpfTypeRecord = ctx.getDslContext().select(CONTRACT_DATA.EXPRESSION)
								.from(CONTRACT_DATA)
								.where(CONTRACT_DATA.NAME.eq("IRPF_TYPE"))
								.and(CONTRACT_DATA.CONTRACT.eq(rec.getValue(CONTRACT.ID)))
								.limit(1)
								.fetchOne();
						
						if(null == irpfTypeRecord) return null;
						
						String irpfTypeExpression = parseContractData(irpfTypeRecord.get(CONTRACT_DATA.EXPRESSION));
						
						return AonStringUtils.isBlank(irpfTypeExpression) ? null : Byte.parseByte(irpfTypeExpression);
					}
					
					private String parseContractData(String exp) {
						if(null == exp)
							return null;
						try {
							exp = exp.split("\"")[1];
							return exp;
						} catch (Exception e) {
							return exp;
						}
					}
					
				}); 
		});
		mod190.getDetails().addAll(map.values());
		fixRoundProblems( ctx, mod190);
		return mod190;
	}


	private void fillLastIrpfDataByPerson(AONContext ctx, int person, Date fromDate, Date toDate, Mod190Detail detail) {
		ctx.checkRead();
		List<IrpfDataRecord> list = ctx.getDslContext()
				.select(IRPF_DATA.fields())
				.from(IRPF_DATA)
				.join(CONTRACT)
				.on(IRPF_DATA.CONTRACT.equal(CONTRACT.ID))
				.where(IRPF_DATA.END_DATE.isNull().or(
						IRPF_DATA.END_DATE.between(
								AonDateUtils.toSql(fromDate),
								AonDateUtils.toSql(toDate))))
				.and(CONTRACT.PERSON.equal(person))
				.orderBy(IRPF_DATA.END_DATE.desc(),IRPF_DATA.START_DATE.asc())
				.fetchInto(IrpfDataRecord.class);
		
		detail.setContract((byte) 1);
		
		if (list != null && !list.isEmpty()) { 
			IrpfDataRecord record = list.get(0);
			detail.setCeutaMelilla(AonEnumUtils.getBoolean(record.getCeutaMelilla()));
			Byte familySituation = record.getFamilySituation();
			if (familySituation != null) {
				familySituation = (byte) (familySituation + 1);
			} else {
				familySituation = (byte) 0;
			}
			detail.setFamilySituation(familySituation);
			detail.setSpouseDocument(record.getSpouseDocument());
			Byte disabilityLevel = record.getDisabilityLevel();
			if (disabilityLevel != null) {
				disabilityLevel = (byte) (disabilityLevel + 1);
			} else {
				disabilityLevel = (byte) 0;
			}
			detail.setDisability(disabilityLevel);
			detail.setContract((byte) (record.getContractType() + 1));
			detail.setWorkActivityExtension(AonEnumUtils.getBoolean(record.getLabourProlongation()));
			detail.setGeographicMobility(record.getMovingDate() != null);
			
			List<IrpfDataDescendientsRecord> descs = ctx.getDslContext()
				.select(IRPF_DATA_DESCENDIENTS.fields())
				.from(IRPF_DATA_DESCENDIENTS)
				.where(IRPF_DATA_DESCENDIENTS.IRPF_DATA.eq(record.getId()))
				.orderBy(IRPF_DATA_DESCENDIENTS.BIRTH_YEAR)
				.fetchInto(IrpfDataDescendientsRecord.class);
			int curYear = AonDateUtils.getYear(fromDate);
			byte ZERO = 0;
			byte ONE = 1;
			byte TWO = 2;
			if (descs != null && !descs.isEmpty()) {
				int i = 1;
				for (IrpfDataDescendientsRecord desc : descs) {
					int descYear = desc.getAdoptionYear() == null?desc.getBirthYear():desc.getAdoptionYear();
					boolean lessThan3 = ( curYear - 3 ) <=  descYear;
					boolean disability = desc.getDisabilityLevel() != null;
					boolean disability33 = desc.getDisabilityLevel() != null && desc.getDisabilityLevel() == 0;
					boolean disability65 = desc.getDisabilityLevel() != null && desc.getDisabilityLevel() == 2;
					boolean dependence = desc.getDependence() != null && desc.getDependence() == 1;
					boolean byInteger = desc.getUniqueParent() != null && desc.getUniqueParent() == 1;
					if (lessThan3) {
						detail.setLessThan3Descendent( (byte) (detail.getLessThan3Descendent() + ONE) );
						detail.setLessThan3DescendentRatio( (byte) (detail.getLessThan3DescendentRatio() + (byInteger?ONE:ZERO)));
					} else {
						detail.setOtherDescendent( (byte) (detail.getOtherDescendent() + ONE) );
						detail.setOtherDescendentRatio( (byte) (detail.getOtherDescendentRatio() + (byInteger?ONE:ZERO)));
					}
					if (disability) {
						if (disability33) {
							detail.setDisabilityDescendent33( (byte) (detail.getDisabilityDescendent33() + ONE) );
							detail.setDisabilityDescendent33Ratio( (byte) (detail.getDisabilityDescendent33Ratio() + (byInteger?ONE:ZERO)));
							if ( dependence ) {
								detail.setDisabilityDescendentDependence( (byte) (detail.getDisabilityDescendentDependence() + ONE) );
								detail.setDisabilityDescendentDependenceRatio( (byte) (detail.getDisabilityDescendentDependenceRatio() + (byInteger?ONE:ZERO)));
							}
						}
						if (disability65) {
							detail.setDisabilityDescendent65( (byte) (detail.getDisabilityDescendent65() + ONE) );
							detail.setDisabilityDescendent65Ratio( (byte) (detail.getDisabilityDescendent65Ratio() + (byInteger?ONE:ZERO)));
						}
					}
					if (i == 1) {
						detail.setFirstChildCalculation(byInteger?ONE:TWO);
					} else if (i == 2) {
						detail.setSecondChildCalculation(byInteger?ONE:TWO);
					} else if (i == 3) {
						detail.setThirdChildCalculation(byInteger?ONE:TWO);
					}
					i++;
				}
			}
			
			List<IrpfDataAscendantsRecord> ascs = ctx.getDslContext()
					.select(IRPF_DATA_ASCENDANTS.fields())
					.from(IRPF_DATA_ASCENDANTS)
					.where(IRPF_DATA_ASCENDANTS.IRPF_DATA.eq(record.getId()))
					.orderBy(IRPF_DATA_ASCENDANTS.BIRTH_YEAR)
					.fetchInto(IrpfDataAscendantsRecord.class);
			if (ascs != null && !ascs.isEmpty()) {
				for (IrpfDataAscendantsRecord asc : ascs) {
					boolean lessThan75 = ( curYear - 75 ) <  asc.getBirthYear();
					boolean byInteger = asc.getAnotherDescendient() != null && asc.getAnotherDescendient() == 0;
					boolean disability = asc.getDisabilityLevel() != null;
					boolean disability33 = asc.getDisabilityLevel() != null && asc.getDisabilityLevel() == 0;
					boolean disability65 = asc.getDisabilityLevel() != null && asc.getDisabilityLevel() == 2;
					boolean dependence = asc.getDependence() != null && asc.getDependence() == 1;
					
					if (lessThan75) {
						detail.setLessThan75Ascendant( (byte) (detail.getLessThan75Ascendant() + ONE) );
						detail.setLessThan75AscendantRatio( (byte) (detail.getLessThan75AscendantRatio() + (byInteger?ONE:ZERO)));
					} else {
						detail.setAscendant( (byte) (detail.getAscendant() + ONE) );
						detail.setAscendantRatio( (byte) (detail.getAscendantRatio() + (byInteger?ONE:ZERO)));
					}
					
					if (disability) {
						if (disability33) {
							detail.setDisabilityAscendant33( (byte) (detail.getDisabilityAscendant33() + ONE) );
							detail.setDisabilityAscendant33Ratio( (byte) (detail.getDisabilityAscendant33Ratio() + (byInteger?ONE:ZERO)));
							if ( dependence ) {
								detail.setDisabilityAscendantDependence( (byte) (detail.getDisabilityAscendantDependence() + ONE) );
								detail.setDisabilityAscendantDependenceRatio( (byte) (detail.getDisabilityAscendantDependenceRatio() + (byInteger?ONE:ZERO)));
							}
						}
						if (disability65) {
							detail.setDisabilityAscendant65( (byte) (detail.getDisabilityAscendant65() + ONE) );
							detail.setDisabilityAscendant65Ratio( (byte) (detail.getDisabilityAscendant65Ratio() + (byInteger?ONE:ZERO)));
						}
					}
				}
			}
				
			
		}
	}
	
	private void fixRoundProblems(AONContext ctx, Mod190 mod190) {
		if (mod190.getDetails() == null || mod190.getDetails().isEmpty()) return;		
		java.sql.Date firstDay = AonDateUtils.toSql(AonDateUtils.getYearFirstDay(mod190.getYear()));
		java.sql.Date lastDay = AonDateUtils.toSql(AonDateUtils.getYearLastDay(mod190.getYear()));
		LinkedHashMap<String,Mod190Detail> map = getUniqueMap( mod190 );	
		for (Mod190Detail detail : map.values()) {
			ctx.getDslContext().select(SALARY.IRPF_BASE,SALARY.TOTAL_IRPF)
				.from(SALARY)
				.join(CONTRACT).on(SALARY.CONTRACT.equal(CONTRACT.ID))
				.join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
				.where(SALARY.ISSUE_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
				.and(SALARY.EMPLOYEE_DOCUMENT.eq(detail.getDocument()))
				.and(WORKPLACE.ENTERPRISE.equal(mod190.getEnterprise()))
				.and(WORKPLACE.ECONOMICAGREEMENT.equal(mod190.getAdministration().value()))
				.and(SALARY.TYPE.in(SalaryType.IRPF_SALARIES )) // Skip SLD ( L00, L13... )
				.orderBy(SALARY.EMPLOYEE_DOCUMENT)
				.fetch()
				.stream()
				.forEach(rec -> {
					double base = AonMathUtils.round(rec.getValue(SALARY.IRPF_BASE));
					double quota = AonMathUtils.round(rec.getValue(SALARY.TOTAL_IRPF));
					detail.setSalaryPerception(AonMathUtils.round(detail.getSalaryPerception() + base));				
					detail.setSalaryRetention(AonMathUtils.round(detail.getSalaryRetention() + quota));
				});
		}
		LinkedList<Mod190Detail> retList = new LinkedList<>();
		for (Mod190Detail detail : map.values()) {
			double per = AonMathUtils.round(detail.getPerception() + detail.getPerceptionIL() + detail.getInKindPerception() + detail.getInKindPerceptionIL());
			double ret = AonMathUtils.round(detail.getRetention() + detail.getInKindDeposit() + detail.getRetentionIL() + detail.getInKindDepositIL());
			double perDiff = AonMathUtils.round(detail.getSalaryPerception() - per);
			double retDiff = AonMathUtils.round(detail.getSalaryRetention() - ret);
			if ( (AonMathUtils.isNotZero(perDiff) && AonMathUtils.absRounded(perDiff) <=0.5)
 			  || (AonMathUtils.isNotZero(retDiff) && AonMathUtils.absRounded(retDiff) <=0.5)) {
				LinkedList<Mod190Detail> selected = new LinkedList<>();
				for (Mod190Detail det : mod190.getDetails() ) {
					double tmpRet = AonMathUtils.round(det.getRetention() + det.getInKindDeposit() + det.getRetentionIL() + det.getInKindDepositIL());
					if ( AonStringUtils.equals (detail.getDocument(),det.getDocument()) && AonMathUtils.isNotZero(tmpRet))  {
						selected.add(det);		
					}
				}
				if ( selected.size() == 1) {
					if (AonMathUtils.isNotZero(per)) {
						selected.get(0).setPerception(AonMathUtils.round( selected.get(0).getPerception() + getPercetionDiff(detail)));
					}
					if (AonMathUtils.isNotZero(ret)) {
						selected.get(0).setRetention(AonMathUtils.round( selected.get(0).getRetention() + getRetentionDiff(detail)));
					}
				}
				retList.add(detail);
			}
		}
	}

	private static LinkedHashMap<String, Mod190Detail> getUniqueMap(Mod190 mod190) {
		LinkedHashMap<String,Mod190Detail> map = new LinkedHashMap<>();	
		for (Mod190Detail detail : mod190.getDetails()) {
			double ret = AonMathUtils.round(detail.getRetention() + detail.getInKindDeposit() + detail.getRetentionIL() + detail.getInKindDepositIL());
			if (!AonStringUtils.equals("G", detail.getKey()) &&
				!AonStringUtils.equals("H", detail.getKey()) &&
				AonMathUtils.isNotZero(ret)) {
				
				Mod190Detail det = null;
				if (!map.containsKey(detail.getDocument())) {
					det = new Mod190Detail();
					det.setDocument(detail.getDocument());
					det.setName(detail.getName());
					map.put(detail.getDocument(), det);
				} else {
					det = map.get(detail.getDocument());
				}
				det.setPerception( AonMathUtils.round(det.getPerception() + detail.getPerception()));
				det.setPerceptionIL( AonMathUtils.round(det.getPerceptionIL() + detail.getPerceptionIL()));
				det.setInKindPerception( AonMathUtils.round(det.getInKindPerception() + detail.getInKindPerception())); 
				det.setInKindPerceptionIL( AonMathUtils.round(det.getInKindPerceptionIL() + detail.getInKindPerceptionIL()));
				det.setRetention( AonMathUtils.round(det.getRetention() + detail.getRetention()));
				det.setRetentionIL( AonMathUtils.round(det.getRetentionIL() + detail.getRetentionIL()));
				det.setInKindDeposit( AonMathUtils.round(det.getInKindDeposit() + detail.getInKindDeposit())); 
				det.setInKindDepositIL( AonMathUtils.round(det.getInKindDepositIL() + detail.getInKindDepositIL()));
			}
		}
		return map;
	}

	private double getPercetionDiff(Mod190Detail detail) {
		return AonMathUtils.round(detail.getSalaryPerception() 
				- detail.getPerception()
				- detail.getPerceptionIL() 
				- detail.getInKindPerception() 
				- detail.getInKindPerceptionIL());
	}
	private double getRetentionDiff(Mod190Detail detail) {
		return AonMathUtils.round(detail.getSalaryRetention() 
				- detail.getRetention()
				- detail.getRetentionIL() 
				- detail.getInKindDeposit() 
				- detail.getInKindDepositIL());			
	}
	
	public LinkedList<Mod190Detail> validateSalaries(AONContext ctx, Mod190 mod190) {
		if (mod190 == null) return null; 
		if (mod190.getDetails() == null || mod190.getDetails().isEmpty()) return null;
		
		java.sql.Date firstDay = AonDateUtils.toSql(AonDateUtils.getYearFirstDay(mod190.getYear()));
		java.sql.Date lastDay = AonDateUtils.toSql(AonDateUtils.getYearLastDay(mod190.getYear()));
		LinkedHashMap<String,Mod190Detail> map = getUniqueMap( mod190 );	
		for (Mod190Detail detail : map.values()) {
			ctx.getDslContext().select(SALARY.IRPF_BASE,SALARY.TOTAL_IRPF)
				.from(SALARY)
				.join(CONTRACT).on(SALARY.CONTRACT.equal(CONTRACT.ID))
				.join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
				.where(SALARY.ISSUE_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
				.and(SALARY.EMPLOYEE_DOCUMENT.eq(detail.getDocument()))
				.and(WORKPLACE.ENTERPRISE.equal(mod190.getEnterprise()))
				.and(WORKPLACE.ECONOMICAGREEMENT.equal(mod190.getAdministration().value()))
				.and(SALARY.TYPE.in(SalaryType.IRPF_SALARIES )) // Skip SLD ( L00, L13... )
				.orderBy(SALARY.EMPLOYEE_DOCUMENT)
				.fetch()
				.stream()
				.forEach(rec -> {
					double base = AonMathUtils.round(rec.getValue(SALARY.IRPF_BASE));
					double quota = AonMathUtils.round(rec.getValue(SALARY.TOTAL_IRPF));
					detail.setSalaryPerception(AonMathUtils.round(detail.getSalaryPerception() + base));				
					detail.setSalaryRetention(AonMathUtils.round(detail.getSalaryRetention() + quota));
				});
		}
		LinkedList<Mod190Detail> retList = new LinkedList<>();
		for (Mod190Detail detail : map.values()) {
			double per = AonMathUtils.round(detail.getPerception() + detail.getPerceptionIL() + detail.getInKindPerception() + detail.getInKindPerceptionIL());
			double ret = AonMathUtils.round(detail.getRetention() + detail.getInKindDeposit() + detail.getRetentionIL() + detail.getInKindDepositIL());
			if ( AonNumberUtils.notEquals(detail.getSalaryPerception(), per) || AonNumberUtils.notEquals(detail.getSalaryRetention(), ret)) {
				retList.add(detail);
			}
		}
		return retList;
	}
	
	Mod190 insertDetailsFromInvoice(AONContext ctx, final Mod190 mod190) {
		java.sql.Date firstDay = AonDateUtils.toSql(AonDateUtils.getYearFirstDay(mod190.getYear()));
		java.sql.Date lastDay = AonDateUtils.toSql(AonDateUtils.getYearLastDay(mod190.getYear()));

		Field<Integer> minRegistry = DSL.min(INVOICE.REGISTRY).as(INVOICE.REGISTRY.getName());
		Field<BigDecimal> sumBase = DSL.sum(INVOICE_TAX.BASE).as(INVOICE_TAX.BASE.getName());
		Field<Double> invoiceTaxSum = DSL.round((INVOICE_TAX.BASE.mul(INVOICE_TAX.PERCENTAGE)).div(100), 2);
		Field<BigDecimal> quotaOp = DSL.sum(DSL.decode()
				.when(INVOICE_TAX.QUOTA.notEqual(0.0), INVOICE_TAX.QUOTA)
				.when(INVOICE_TAX.QUOTA.equal(0.0), invoiceTaxSum));
		Map<String,Mod190Detail> map = new LinkedHashMap<>();
		ctx.getDslContext()
				.select(INVOICE.RDOCUMENT, INVOICE.RNAME,INVOICE_TAX.WITHHOLDING_TYPE, INVOICE_TAX.PERCENTAGE, minRegistry, sumBase,quotaOp)
				.from(INVOICE)
				.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
				.where(INVOICE.DOMAIN.equal(mod190.getDomain()))
				.and(INVOICE.TYPE.notEqual((byte) 1)) 		// No Ventas
				.and(INVOICE_TAX.TAX_TYPE.equal((byte) 2))	// IRPF
				.and(INVOICE_TAX.WITHHOLDING_TYPE.in((byte) 0, (byte) 3,(byte) 4)) 
				.and(INVOICE.ISSUE_DATE.between(firstDay,lastDay))
				.and(INVOICE.NUMBER.ge(0))   // No facturas proforma (factura proforma es la que su numero de factura es menor que cero)
				.and(InvoiceDAO.NOT_ANNULLED) // No facturas anuladas
				.groupBy(INVOICE.RDOCUMENT, INVOICE.RNAME,INVOICE_TAX.WITHHOLDING_TYPE,INVOICE_TAX.PERCENTAGE)
				.fetch()
				.stream()
				.forEach(
						rec -> {
							
							String document = rec.getValue(INVOICE.RDOCUMENT);
							String key = null;
							String subKey = null;
							
							double percent = rec.getValue(INVOICE_TAX.PERCENTAGE);
							WithholdingType withholding = WithholdingType.safeValueOf(rec.getValue(INVOICE_TAX.WITHHOLDING_TYPE));
							if (withholding == WithholdingType.PROFESSIONAL) {
								key = Mod1902022Key.G.getValue();
								if (AonNumberUtils.equals(percent, 7.0) ) {
									subKey = Mod1902022Key.G.getSubKeys()[2];
								} else {
									subKey = Mod1902022Key.G.getSubKeys()[0];;
								}
							} else if (withholding == WithholdingType.FARMER) { // AGRICULTOR - FARMER
								key = Mod1902022Key.H.getValue();
								subKey = Mod1902022Key.H.getSubKeys()[0];
							} else if (withholding == WithholdingType.TRANSPORT_OPERATOR) { // TRANSPORTISTAS Y ASIMILADOS - TRANSPORT_OPERATOR
								key = Mod1902022Key.H.getValue();
								subKey = Mod1902022Key.H.getSubKeys()[3];
							}
							String mapKey = document + "|" + key + "|" + subKey;
							Mod190Detail detail = null; 
							if (!map.containsKey(mapKey)) {
								detail = new Mod190Detail();
								detail.setDomain(mod190.getDomain());
								detail.setMod190(mod190.getId());
								detail.setDocument(document);
								detail.setName(rec.getValue(INVOICE.RNAME));
								detail.setProvince( RegistryAddressDAO.getMainAddressProvince(ctx, rec.getValue(minRegistry)) );
								detail.setKey(key);
								detail.setSubKey(subKey);
								map.put(mapKey, detail);
							}
							detail = map.get(mapKey);
							detail.setPerception(AonMathUtils.round(detail.getPerception() + rec.getValue(sumBase).doubleValue()));
							detail.setRetention(AonMathUtils.round(detail.getRetention() + rec.getValue(quotaOp).doubleValue()));
						});
		mod190.getDetails().addAll(map.values());
		return mod190;
	}
}
