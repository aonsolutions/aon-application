package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod190;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
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

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.IrpfDataAscendantsRecord;
import com.esferalia.aon.jooq.tables.records.IrpfDataDescendientsRecord;
import com.esferalia.aon.jooq.tables.records.IrpfDataRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IWithholdingTypeVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.type.Mod1902025Key;
import com.esferalia.aon.occam.api.model.type.PaymentType;
import com.esferalia.aon.occam.api.model.type.PaymentType.PaymentTypeVisitor;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.impl.jooq.dao.irpf.IRPFDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod190ALL2025Declaration extends Mod190Declaration {
	
	private static final String PREST_IT = "PREST_IT";
	private static final String PPE = "PPE";

	Mod190 insertDetailsFromSalary(AONContext ctx, final Mod190 mod190) {
		Field<java.sql.Date> dateField =  mod190.mustUseChargeDate() ? SALARY.CHARGE_DATE : SALARY.ISSUE_DATE;
		Date firstDay = AonDateUtils.getYearFirstDay(mod190.getYear());
		Date lastDay = AonDateUtils.getYearLastDay(mod190.getYear());
		Field<Integer> birthYear = DSL.year(PERSON.BIRTH_DATE);
		final TreeMap<String, Mod190Detail> map = new TreeMap<>();
		final HashSet<Integer> salaries = new HashSet<>();
		ctx.getDslContext().select(
				 SALARY.ID
				,SALARY.ISSUE_DATE
				,SALARY.CHARGE_DATE
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
				,SALARY_PAYMENT.QUOTE
				
				,CONTRACT.ID
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
		.where(dateField.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
		.and(WORKPLACE.ENTERPRISE.equal(mod190.getEnterprise()))
//		.and(WORKPLACE.ECONOMICAGREEMENT.equal(mod190.getAdministration().value()))
		.and(IRPFDAO.getEconomicAgreementCondition(mod190))
		.and(SALARY.TYPE.in(SalaryType.IRPF_SALARIES )) // Skip SLD ( L00, L13... )
		.and(CONTRACT_DATA.EXPRESSION.isNull().or(CONTRACT_DATA.EXPRESSION.ne("\"3\"")))  // No tener en cuenta los no residentes
//		.orderBy(SALARY.EMPLOYEE_DOCUMENT)
		.orderBy(SALARY.END_DATE) // ORDENAR POR FECHA PARA QUE DETERMINADOS DATOS SE COJAN SIEMPRE DE LA ULTIMA NOMINA DE CADA PERSONA		
		.fetch()
		.stream()
		.forEach(rec -> {
				String document = AonStringUtils.upperCase(rec.getValue(SALARY.EMPLOYEE_DOCUMENT));
				Integer person = rec.getValue(PERSON.REGISTRY);
				Date issueDate = rec.getValue(SALARY.ISSUE_DATE);
				Integer issueYear = AonDateUtils.getYear(issueDate);
				Date chargeDate = rec.getValue(SALARY.CHARGE_DATE);
				Integer chargeYear = AonDateUtils.getYear(chargeDate);
				final Integer accrualYear = (!AonNumberUtils.equals(issueYear, chargeYear))
						? (AonNumberUtils.equals(mod190.getYear(),issueYear)? null : issueYear) 
						: null;
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
//						if (!isBoss()) visitAInKindKey();
						visitAEInKindKey();
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
								? 0.0
								: (irpfBase * totalIrpf / totalIrpfBase);
						
						Mod190Detail detail = null;
						
						if(isE01())
							detail = getDetail(document,person,Mod1902025Key.E,"01",accrualYear);
						else if(isE02())
							detail = getDetail(document,person,Mod1902025Key.E,"02",accrualYear);
						else if(isE03()) 
							detail = getDetail(document,person,Mod1902025Key.E,"03",accrualYear);
						else if(isE04())
							detail = getDetail(document,person,Mod1902025Key.E,"04",accrualYear);
						
						if(null != detail) {
							irpfBase = fixRoundBaseProblem();
							detail.setPerception(AonMathUtils.round(detail.getPerception() + irpfBase ));
							detail.setRetention(AonMathUtils.round(detail.getRetention() + irpfQuota ));
							Integer salary = rec.getValue(SALARY.ID);
							if (!salaries.contains(salary)) {
								salaries.add(salary);
								double ss = rec.getValue(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
								detail.setDeducibleExpense(AonMathUtils.round(detail.getDeducibleExpense() + ss ));
							}
							
							// Asignar tambien el importe a la administracion del modelo
							switch (mod190.getAdministration()) {
								case ALAVA -> detail.setArabaRetention(AonMathUtils.round(detail.getArabaRetention() + irpfQuota));
								case BIZKAIA -> detail.setBizkaiaRetention(AonMathUtils.round(detail.getBizkaiaRetention() + irpfQuota));
								case GIPUZKOA -> detail.setGipuzkoaRetention(AonMathUtils.round(detail.getGipuzkoaRetention() + irpfQuota));
								case NAVARRA -> detail.setNavarraRetention(AonMathUtils.round(detail.getNavarraRetention() + irpfQuota));
								default -> detail.setCommonRetention(AonMathUtils.round(detail.getCommonRetention() + irpfQuota));
							}
						}
						
						return isE01() || isE02() || isE03() || isE04();
					}

					private void visitExpenses() {
						double amount = rec.getValue(SALARY_PAYMENT.AMOUNT);
						double irpfBase = rec.getValue(SALARY_PAYMENT.IRPF);
						
						// Parte exenta va a la L
						double expense = AonMathUtils.round(amount - irpfBase);  
						Mod190Detail detail = getDetail(document,person,Mod1902025Key.L,"01",accrualYear);
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
								? 0.0
								: (irpfBase * totalIrpf / totalIrpfBase);
						if ( AonMathUtils.isGreatherThanZero(irpfBase )) {
							visitAKey();		
						} else if(AonMathUtils.isGreatherThanZero(amount )) {
							//Esta comprobacion es por que puede haber payments CRA 0054 que tengan amount 0 (pe.: DIAS_PREAVISO)
							Mod190Detail detail = getDetail(document,person,Mod1902025Key.L,"05",accrualYear);
							detail.setPerception(AonMathUtils.round(detail.getPerception() + amount ));
							detail.setRetention(AonMathUtils.round(detail.getRetention() + irpfQuota ));
						}
					}
					
					private void visitInsurance() {
						double amount = rec.getValue(SALARY_PAYMENT.AMOUNT);
//						double irpfBase = rec.getValue(SALARY_PAYMENT.IRPF);
						double irpfBase = AonMathUtils.round(rec.getValue(SALARY_PAYMENT.IRPF));
						
						// Parte exenta va a la L
						double expense = AonMathUtils.round(amount - irpfBase);  
//						if ( AonMathUtils.isGreatherThanZero(expense )) {
						if (AonMathUtils.isGreatherThan(expense,0.01)) {
							Mod190Detail detail = getDetail(document,person,Mod1902025Key.L,"24",accrualYear);
							detail.setInKindPerception(AonMathUtils.round(detail.getInKindPerception() + expense ));
						} 
//						else {
							// Parte no exenta va a la A a la parte en especie
							double totalIrpf = rec.getValue(SALARY.TOTAL_IRPF);
							double totalIrpfBase = rec.getValue(SALARY.IRPF_BASE);
							double irpfQuota = ( AonMathUtils.isZero( irpfBase) || AonMathUtils.isZero( totalIrpfBase ) )
									? 0.0
									: (irpfBase * totalIrpf / totalIrpfBase);
							
							Double enterpriseIrpfQuota = getEnterpriseIrpfQuota();
							
							Mod190Detail detail = getDetail(document,person,Mod1902025Key.A,null,accrualYear);
							detail.setInKindPerception(AonMathUtils.round(detail.getInKindPerception() + irpfBase ));
							detail.setInKindDeposit(AonMathUtils.round(detail.getInKindDeposit() + irpfQuota ));
							if(detail.getInKindDepositIL() - enterpriseIrpfQuota > 1)
								detail.setInKindOutputDeposit(enterpriseIrpfQuota);
//						}
					}
					
					private void visitSupport() {
						double amount = rec.getValue(SALARY_PAYMENT.AMOUNT);
						double irpfBase = rec.getValue(SALARY_PAYMENT.IRPF);
						
						// Parte exenta va a la L
						double expense = AonMathUtils.round(amount - irpfBase);  
						if ( AonMathUtils.isGreatherThanZero(expense )) {
							Mod190Detail detail = getDetail(document,person,Mod1902025Key.L,"25",accrualYear);
							detail.setPerception(AonMathUtils.round(detail.getPerception() + expense ));
						} else
							// Parte no exenta va a la A
							visitAKey();	
					}
					
					private void visitAKey() {
						String paymentConcept = rec.getValue(SALARY_PAYMENT.PAYMENT_CONCEPT);
						if (PPE.equals(paymentConcept) && mod190.getYear() >= 2026) {
							Mod190Detail detail = getDetail(document,person,Mod1902025Key.A,null,accrualYear);
							double quote = AonNumberUtils.todouble(rec.getValue(SALARY_PAYMENT.QUOTE));
							detail.setForecastPlanContributions(AonMathUtils.round(detail.getForecastPlanContributions() + quote));
						} else {
							double totalIrpf = rec.getValue(SALARY.TOTAL_IRPF);
							double totalIrpfBase = rec.getValue(SALARY.IRPF_BASE);
							double irpfBase = rec.getValue(SALARY_PAYMENT.IRPF);
							double irpfQuota = ( AonMathUtils.isZero( irpfBase) || AonMathUtils.isZero( totalIrpfBase) )
									? 0.0
									: (irpfBase * totalIrpf / totalIrpfBase);
							
							Mod190Detail detail = getDetail(document,person,Mod1902025Key.A,null,accrualYear);
							Integer salary = rec.getValue(SALARY.ID);
							if (!salaries.contains(salary)) {
								salaries.add(salary);
								double ss = rec.getValue(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS);
								detail.setDeducibleExpense(AonMathUtils.round(detail.getDeducibleExpense() + ss ));
							}
		
							irpfBase = fixRoundBaseProblem();
							
//							String prest = rec.getValue(SALARY_PAYMENT.PAYMENT_CONCEPT);
							if (PREST_IT.equals(paymentConcept)) {
								detail.setPerceptionIL(AonMathUtils.round(detail.getPerceptionIL() + irpfBase ));
								detail.setRetentionIL(AonMathUtils.round(detail.getRetentionIL() + irpfQuota ));
							} else {
								detail.setPerception(AonMathUtils.round(detail.getPerception() + irpfBase ));
								detail.setRetention(AonMathUtils.round(detail.getRetention() + irpfQuota ));
							}
						}
					}
					
					// Se devuelve la base redondeada corregida en caso de que haya una pequeña diferencia con el importe
					// Se ha detectado que en algunos casos la base de IRPF de la línea del concepto se graba con tres decimales
					// y no coincide con el importe redondeado a dos decimales
					private double fixRoundBaseProblem() {
						double amount = AonMathUtils.round(rec.getValue(SALARY_PAYMENT.AMOUNT));
						double irpfRoundBase = AonMathUtils.round(rec.getValue(SALARY_PAYMENT.IRPF));
						double difference = AonMathUtils.absRounded(amount - irpfRoundBase);
						if (difference > 0.00 && difference <= 0.03) {							
							irpfRoundBase = amount;
						}
						return irpfRoundBase;
					}

					private void visitAEInKindKey() {
						double totalIrpf = rec.getValue(SALARY.TOTAL_IRPF);
						double totalIrpfBase = rec.getValue(SALARY.IRPF_BASE);
						double irpfBase = rec.getValue(SALARY_PAYMENT.IRPF);
						double irpfQuota = ( AonMathUtils.isZero( irpfBase) || AonMathUtils.isZero( totalIrpfBase ) )
								? 0.0
								: (irpfBase * totalIrpf / totalIrpfBase);
						
//						Mod190Detail detail = getDetail(document,person,Mod1902025Key.A,null,accrualYear);
						Mod1902025Key key = Mod1902025Key.A;
						String subKey = null;
						
						if (isE01()) {
							key = Mod1902025Key.E;
							subKey = "01";
						} else if (isE02()) {
							key = Mod1902025Key.E;
							subKey = "02";
						} else if (isE03()) {
							key = Mod1902025Key.E;
							subKey = "03";
						} else if (isE04()) {
							key = Mod1902025Key.E;
							subKey = "04";
						}
						Mod190Detail detail = getDetail(document, person, key, subKey, accrualYear);
						String prest = rec.getValue( SALARY_PAYMENT.PAYMENT_CONCEPT);
						Double enterpriseIrpfQuota = getEnterpriseIrpfQuota();
						
						// Si esta exento deberia ir al L.24
						if (key == Mod1902025Key.A && irpfQuota == 0.00) {
							visitInsurance();
						} else {
							if (key == Mod1902025Key.A && PREST_IT.equals(prest)) {
								detail.setInKindPerceptionIL(AonMathUtils.round(detail.getInKindPerceptionIL() + irpfBase ));
								detail.setInKindDepositIL(AonMathUtils.round(detail.getInKindDepositIL() + irpfQuota ));
								double dif = AonMathUtils.round(irpfQuota) - AonMathUtils.round(enterpriseIrpfQuota);
								if (dif > 0) {
									detail.setInKindOutputDepositIL(AonMathUtils.sum(detail.getInKindOutputDepositIL(), dif));
								}
							} else {
								detail.setInKindPerception(AonMathUtils.round(detail.getInKindPerception() + irpfBase ));
								detail.setInKindDeposit(AonMathUtils.round(detail.getInKindDeposit() + irpfQuota ));
								// Cuota repercutida = Cuota total - Cuota empresa
								double dif = AonMathUtils.round(irpfQuota) - AonMathUtils.round(enterpriseIrpfQuota);
								if (dif > 0) {
									detail.setInKindOutputDeposit(AonMathUtils.sum(detail.getInKindOutputDeposit(), dif));
								}
							}
							
							if (key == Mod1902025Key.E) {
								// Asignar tambien el importe a la administracion del modelo
								switch (mod190.getAdministration()) {
									case ALAVA -> detail.setArabaRetention(AonMathUtils.round(detail.getArabaRetention() + irpfQuota));
									case BIZKAIA -> detail.setBizkaiaRetention(AonMathUtils.round(detail.getBizkaiaRetention() + irpfQuota));
									case GIPUZKOA -> detail.setGipuzkoaRetention(AonMathUtils.round(detail.getGipuzkoaRetention() + irpfQuota));
									case NAVARRA -> detail.setNavarraRetention(AonMathUtils.round(detail.getNavarraRetention() + irpfQuota));
									default -> detail.setCommonRetention(AonMathUtils.round(detail.getCommonRetention() + irpfQuota));
								}
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
								? 0.0
								: (irpfBase * totalIrpf / totalIrpfBase);
						
						// Esto es el total de aportacion? Si es así estaría bien saber cuanto aporta por devengo de tipo En Especie
						// Puede existir mas de una entrada en la tabla salaryData
						Result<Record> ifpfCTAs = ctx.getDslContext().select().from(SALARY_DATA)
							.where(SALARY_DATA.NAME.eq("IRPF_CTA_ESP"))
							.and(SALARY_DATA.SALARY.eq(rec.getValue(SALARY.ID)))
							.fetch();
						
						double ifpfCTAESP = ifpfCTAs.stream()
								.map(ifpfCTA ->  ifpfCTA == null ? 0.00 : Double.parseDouble(ifpfCTA.get(SALARY_DATA.EXPRESSION)))
								.reduce(0.00, (a, b) -> a + b);
						
						double irpfQuotaEnterprise = 0.00;
						irpfQuotaEnterprise = irpfQuota - ifpfCTAESP;
						
						return irpfQuota == 0.00 || irpfQuotaEnterprise <= 0.00 ? 0.00 : irpfQuotaEnterprise;
					}
					
					// AHORA SE ACUMULA POR NIF + CLAVE + SUBCLAVE + AÑO DEVENGO
					private Mod190Detail getDetail(String document,int person, Mod1902025Key key, String subKey, Integer accrualYear) {
						String mapKey =  document 
									+ "_" 
//									+ person 
//									+ "_" 
									+ key.getValue()
									+ "_"
									+ AonStringUtils.defaultIfBlank(subKey)
									+ "_"
									+ AonStringUtils.defaultIfBlank(AonNumberUtils.toString(accrualYear))
									;
						// SI NO EXISTE SE CREA Y SI EXISTE SE ACTUALIZAN SUS DATOS, PARA COGER DETERMINADOS DATOS DE LA PERSONA DE LA ULTIMA NOMINA
						if ( !map.containsKey(mapKey) ) {
							final Mod190Detail det = new Mod190Detail();
							map.put(mapKey, det);
							det.setDomain(mod190.getDomain());
							det.setMod190(mod190.getId());
							det.setKey(key.getValue());
							det.setSubKey(subKey);
//							detail.setDocument(rec.getValue(SALARY.EMPLOYEE_DOCUMENT));
//							detail.setName(rec.getValue(SALARY.EMPLOYEE_NAME));
							det.setDocument(document);
							if (accrualYear != null) 
								det.setAccrualYear( accrualYear );
							
//							ctx.getDslContext().select(GEOZONE.CODE)
//								.from(RADDRESS)
//								.join(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
//								.where(RADDRESS.REGISTRY.equal(rec.getValue(PERSON.REGISTRY)))
//								.and(RADDRESS.TYPE.equal((byte) 0))
//								.limit(1)
//								.fetch()
//								.stream()
//								.forEach(
//									province -> {
//										try {detail.setProvince(Integer.parseInt(province.getValue(GEOZONE.CODE)));
//										} catch (NumberFormatException e) {
//											// nothing. If not a number,  not a valid province.
//										}
//									}
//								);
//							if (key == Mod1902025Key.A) {
//								Integer birthData = rec.getValue(birthYear);
//								detail.setBirthYear(birthData==null?0:birthData);
//								fillLastIrpfDataByPerson(ctx,rec.getValue(PERSON.REGISTRY),firstDay, lastDay, detail);
//							}
						}
						
						// ACTUALIZAR LOS DATOS DE LA PERSONA
						Mod190Detail detail = map.get(mapKey);						
						detail.setName(rec.getValue(SALARY.EMPLOYEE_NAME)); // Nombre
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
								try {
									detail.setProvince(Integer.parseInt(province.getValue(GEOZONE.CODE))); // Código de provincia (si está indicado)
								} catch (NumberFormatException e) {
									// nothing. If not a number,  not a valid province.
								}
							}
						);
						if (key == Mod1902025Key.A) {
							Integer birthData = rec.getValue(birthYear);
							if (birthData != null && birthData != 0)
								detail.setBirthYear(birthData == null ? 0 : birthData); // Año de nacimiento (si está indicado)
							fillLastIrpfDataByPerson(ctx,rec.getValue(PERSON.REGISTRY),firstDay, lastDay, detail); // Datos adicionales 
						}
						
//						return map.get(mapKey);
						return detail;
						
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
			detail.setCeutaMelillaPalma(record.getCeutaMelilla() == null ? (byte) 0 : record.getCeutaMelilla());			
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
			
			detail.setLessThan3Descendent((byte) 0);
			detail.setLessThan3DescendentRatio((byte) 0);
			detail.setOtherDescendent((byte) 0);
			detail.setOtherDescendentRatio((byte) 0);
			detail.setDisabilityDescendent33((byte) 0);
			detail.setDisabilityDescendent33Ratio((byte) 0);
			detail.setDisabilityDescendentDependence((byte) 0);
			detail.setDisabilityDescendentDependenceRatio((byte) 0);
			detail.setDisabilityDescendent65((byte) 0);
			detail.setDisabilityDescendent65Ratio((byte) 0);
			detail.setLessThan75Ascendant((byte) 0);
			detail.setLessThan75AscendantRatio((byte) 0);
			detail.setAscendant((byte) 0);
			detail.setAscendantRatio((byte) 0);
			detail.setDisabilityAscendant33((byte) 0);
			detail.setDisabilityAscendant33Ratio((byte) 0);
			detail.setDisabilityAscendantDependence((byte) 0);
			detail.setDisabilityAscendantDependenceRatio((byte) 0);
			detail.setDisabilityAscendant65((byte) 0);
			detail.setDisabilityAscendant65Ratio((byte) 0);
			
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
					Integer birthYear = desc.getBirthYear();
					if (birthYear == null) birthYear = Integer.valueOf(0);
					int descYear = desc.getAdoptionYear() == null?birthYear:desc.getAdoptionYear();
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
					boolean lessThan75 = asc.getBirthYear() != null && ( curYear - 75 ) <  asc.getBirthYear() ;
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
		Field<java.sql.Date> dateField = mod190.mustUseChargeDate() ? SALARY.CHARGE_DATE : SALARY.ISSUE_DATE;
		if (mod190.getDetails() == null || mod190.getDetails().isEmpty()) return;		
		java.sql.Date firstDay = AonDateUtils.toSql(AonDateUtils.getYearFirstDay(mod190.getYear()));
		java.sql.Date lastDay = AonDateUtils.toSql(AonDateUtils.getYearLastDay(mod190.getYear()));
		LinkedHashMap<String,Mod190Detail> map = getUniqueMap( mod190 );	
		for (Mod190Detail detail : map.values()) {
			// Si ejercicio de deveng distinto de cero entonces buscar solo salary cuyo año de la fecha de emisión sea ese año (son nominas de atrasos de años anteriores)
			Condition c = DSL.noCondition();
			if (detail.getAccrualYear() != 0)
				c = DSL.year(SALARY.ISSUE_DATE).eq(detail.getAccrualYear());
			// En caso contrario no buscar los registros cuya fecha de emisión no esté en el año del modelo (solo si se creo el modelo 190 según la fecha de pago de las nóminas)
			else if (mod190.mustUseChargeDate())
				c = DSL.year(SALARY.ISSUE_DATE).eq(mod190.getYear());
			
			ctx.getDslContext().select(SALARY.IRPF_BASE,SALARY.TOTAL_IRPF)
				.from(SALARY)
				.join(CONTRACT).on(SALARY.CONTRACT.equal(CONTRACT.ID))
				.join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
				.where(dateField.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
				.and(SALARY.EMPLOYEE_DOCUMENT.eq(detail.getDocument()))
				.and(WORKPLACE.ENTERPRISE.equal(mod190.getEnterprise()))
//				.and(WORKPLACE.ECONOMICAGREEMENT.equal(mod190.getAdministration().value()))
				.and(IRPFDAO.getEconomicAgreementCondition(mod190))
				.and(SALARY.TYPE.in(SalaryType.IRPF_SALARIES )) // Skip SLD ( L00, L13... )
				.and(c)
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
					if ( AonStringUtils.equals(detail.getDocument(),det.getDocument()) && detail.getAccrualYear() == det.getAccrualYear() && AonMathUtils.isNotZero(tmpRet) ) {
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
			String mapKey = detail.getDocument() + "-" + detail.getAccrualYear();
			double ret = AonMathUtils.round(detail.getRetention() + detail.getInKindDeposit() + detail.getRetentionIL() + detail.getInKindDepositIL());
			if (!AonStringUtils.equals("G", detail.getKey()) &&
				!AonStringUtils.equals("H", detail.getKey()) &&
				AonMathUtils.isNotZero(ret)) {
				Mod190Detail det = null;
				if (!map.containsKey(mapKey)) {
					det = new Mod190Detail();
					det.setDocument(detail.getDocument());
					det.setName(detail.getName());
					det.setAccrualYear(detail.getAccrualYear());
					map.put(mapKey, det);
				} else {
					det = map.get(mapKey);
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
		Field<java.sql.Date> dateField =  mod190.mustUseChargeDate() ? SALARY.CHARGE_DATE : SALARY.ISSUE_DATE;
		java.sql.Date firstDay = AonDateUtils.toSql(AonDateUtils.getYearFirstDay(mod190.getYear()));
		java.sql.Date lastDay = AonDateUtils.toSql(AonDateUtils.getYearLastDay(mod190.getYear()));
		LinkedHashMap<String,Mod190Detail> map = getUniqueMap( mod190 );	
		for (Mod190Detail detail : map.values()) {
			int accrualYear = detail.getAccrualYear();
			Condition accrualCondition = DSL.trueCondition(); 
			if (accrualYear != 0) {
				java.sql.Date accrualFirstDay = AonDateUtils.toSql(AonDateUtils.getYearFirstDay(accrualYear));
				java.sql.Date accrualLastDay = AonDateUtils.toSql(AonDateUtils.getYearLastDay(accrualYear));
				accrualCondition = SALARY.ISSUE_DATE.between(AonDateUtils.toSql(accrualFirstDay),AonDateUtils.toSql(accrualLastDay));
			}
			ctx.getDslContext().select(SALARY.IRPF_BASE,SALARY.TOTAL_IRPF)
				.from(SALARY)
				.join(CONTRACT).on(SALARY.CONTRACT.equal(CONTRACT.ID))
				.join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
				.where(dateField.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
				.and(SALARY.EMPLOYEE_DOCUMENT.eq(detail.getDocument()))
				.and(accrualCondition)
				.and(WORKPLACE.ENTERPRISE.equal(mod190.getEnterprise()))
//				.and(WORKPLACE.ECONOMICAGREEMENT.equal(mod190.getAdministration().value()))
				.and(IRPFDAO.getEconomicAgreementCondition(mod190))
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
			if ( AonNumberUtils.notEquals(detail.getSalaryPerception(), per) 
				|| AonNumberUtils.notEquals(detail.getSalaryRetention(), ret)) {
				retList.add(detail);
			}
		}
		return retList;
	}
	
	private static class Mod190DetailKey {
		private String document;
		private String key;
		private String subKey;
		
		public String getDocument() {
			return document;
		}
		public Mod190DetailKey setDocument(String document) {
			this.document = document;
			return this;
		}
		public String getKey() {
			return key;
		}
		public Mod190DetailKey setKey(String key) {
			this.key = key;
			return this;
		}
		public String getSubKey() {
			return subKey;
		}
		public Mod190DetailKey setSubKey(String subKey) {
			this.subKey = subKey;
			return this;
		}
		public String toMapKey() {
			return document + "|" + key + "|" + subKey;
		}
		public boolean hasKey() {
			return AonStringUtils.isNotBlank(this.key);
		}
	}
	
	private static class Mod190WithholdingTypeVisitor implements IWithholdingTypeVisitor<Mod190DetailKey> {

		@Override public Mod190DetailKey visitRenting(Mod190DetailKey detailKey) {return detailKey;}
		@Override public Mod190DetailKey visitMovableCapital(Mod190DetailKey detailKey) {return detailKey;}
		@Override public Mod190DetailKey visitM193C1(Mod190DetailKey detailKey) {return detailKey;}
		@Override public Mod190DetailKey visitM193C2(Mod190DetailKey detailKey) {return detailKey;}
		@Override public Mod190DetailKey visitM193C3(Mod190DetailKey detailKey) {return detailKey;}
		@Override public Mod190DetailKey visitM193C4(Mod190DetailKey detailKey) {return detailKey;}
		@Override public Mod190DetailKey visitM193B01(Mod190DetailKey detailKey) {return detailKey;}
		@Override public Mod190DetailKey visitM193B02(Mod190DetailKey detailKey) {return detailKey;}
		@Override public Mod190DetailKey visitM193B03(Mod190DetailKey detailKey) {return detailKey;}
		@Override public Mod190DetailKey visitM193B04(Mod190DetailKey detailKey) {return detailKey;}
		@Override public Mod190DetailKey visitM193B05(Mod190DetailKey detailKey) {return detailKey;}
		@Override public Mod190DetailKey visitM193B06(Mod190DetailKey detailKey) {return detailKey;}
		@Override public Mod190DetailKey visitM193B07(Mod190DetailKey detailKey) {return detailKey;}
		@Override public Mod190DetailKey visitM193D01(Mod190DetailKey detailKey) {return detailKey;}
		@Override public Mod190DetailKey visitM193D02(Mod190DetailKey detailKey) {return detailKey;}
		@Override public Mod190DetailKey visitM193D03(Mod190DetailKey detailKey) {return detailKey;}
		@Override public Mod190DetailKey visitM193D04(Mod190DetailKey detailKey) {return detailKey;}
		@Override public Mod190DetailKey visitM193D05(Mod190DetailKey detailKey) {return detailKey;}
		@Override public Mod190DetailKey visitM193D06(Mod190DetailKey detailKey) {return detailKey;}
		@Override public Mod190DetailKey visitM193D07(Mod190DetailKey detailKey) {return detailKey;}
		
		@Override
		public Mod190DetailKey visitProfessional(Mod190DetailKey detailKey) {
			return detailKey
				.setKey( Mod1902025Key.G.getValue())
				.setSubKey( Mod1902025Key.G.getSubKeys()[0] );
		}
		@Override public Mod190DetailKey visitM190G02(Mod190DetailKey detailKey) {
			return detailKey
				.setKey( Mod1902025Key.G.getValue())
				.setSubKey( Mod1902025Key.G.getSubKeys()[1] );
		}
		
		@Override 
		public Mod190DetailKey visitM190G03(Mod190DetailKey detailKey) {
			return detailKey
				.setKey( Mod1902025Key.G.getValue())
				.setSubKey( Mod1902025Key.G.getSubKeys()[2] );
		}

		@Override
		public Mod190DetailKey visitFarmer(Mod190DetailKey detailKey) {
			return detailKey
				.setKey( Mod1902025Key.H.getValue())
				.setSubKey( Mod1902025Key.H.getSubKeys()[0]);
		}

		@Override public Mod190DetailKey visitM190H02(Mod190DetailKey detailKey) {
			return detailKey
				.setKey( Mod1902025Key.H.getValue())
				.setSubKey( Mod1902025Key.H.getSubKeys()[1]);
		}
		
		@Override public Mod190DetailKey visitM190H03(Mod190DetailKey detailKey) { 
			return detailKey
				.setKey( Mod1902025Key.H.getValue())
				.setSubKey( Mod1902025Key.H.getSubKeys()[2]);
		}

		@Override
		public Mod190DetailKey visitTransportOperator(Mod190DetailKey detailKey) {
			return detailKey
				.setKey( Mod1902025Key.H.getValue())
				.setSubKey( Mod1902025Key.H.getSubKeys()[3]);
		}
		
		@Override 
		public Mod190DetailKey visitM190I01(Mod190DetailKey detailKey) {
			return detailKey
				.setKey( Mod1902025Key.I.getValue())
				.setSubKey( Mod1902025Key.I.getSubKeys()[0]);
		}
		@Override 
		public Mod190DetailKey visitM190I02(Mod190DetailKey detailKey) {
			return detailKey
				.setKey( Mod1902025Key.I.getValue())
				.setSubKey( Mod1902025Key.I.getSubKeys()[1]);
		}
		
		@Override 
		public Mod190DetailKey visitM190J(Mod190DetailKey detailKey) {
			return detailKey
				.setKey( Mod1902025Key.J.getValue())
				.setSubKey( null );
		}
		@Override 
		public Mod190DetailKey visitM190K01(Mod190DetailKey detailKey) {
			return detailKey
				.setKey( Mod1902025Key.K.getValue())
				.setSubKey( Mod1902025Key.K.getSubKeys()[0]);
		}
		@Override 
		public Mod190DetailKey visitM190K02(Mod190DetailKey detailKey) {
			return detailKey
				.setKey( Mod1902025Key.K.getValue())
				.setSubKey( Mod1902025Key.K.getSubKeys()[1]);
		}
		@Override 
		public Mod190DetailKey visitM190K03(Mod190DetailKey detailKey) {
			return detailKey
				.setKey( Mod1902025Key.K.getValue())
				.setSubKey( Mod1902025Key.K.getSubKeys()[2]);
		}
		@Override 
		public Mod190DetailKey visitM190F01(Mod190DetailKey detailKey) {
			return detailKey
				.setKey( Mod1902025Key.F.getValue())
				.setSubKey( Mod1902025Key.F.getSubKeys()[0]);
		}
		@Override 
		public Mod190DetailKey visitM190F021(Mod190DetailKey detailKey) {
			return detailKey
				.setKey( Mod1902025Key.F.getValue())
				.setSubKey( Mod1902025Key.F.getSubKeys()[1]);
		}
		@Override 
		public Mod190DetailKey visitM190F022(Mod190DetailKey detailKey) {
			return detailKey
				.setKey( Mod1902025Key.F.getValue())
				.setSubKey( Mod1902025Key.F.getSubKeys()[1]);
		}

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
		final Mod190WithholdingTypeVisitor visitor = new Mod190WithholdingTypeVisitor();
		ctx.getDslContext()
				.select(INVOICE.RDOCUMENT, INVOICE.RNAME,INVOICE_TAX.WITHHOLDING_TYPE, INVOICE_TAX.PERCENTAGE, minRegistry, sumBase,quotaOp)
				.from(INVOICE)
				.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
				.where(INVOICE.DOMAIN.equal(mod190.getDomain()))
				.and(INVOICE.TYPE.notEqual((byte) 1)) 		// No Ventas
				.and(INVOICE_TAX.TAX_TYPE.equal((byte) 2))	// IRPF
				// .and(INVOICE_TAX.WITHHOLDING_TYPE.in((byte) 0, (byte) 3,(byte) 4)) 
				.and(INVOICE.ISSUE_DATE.between(firstDay,lastDay))
				.and(INVOICE.NUMBER.ge(0))   // No facturas proforma (factura proforma es la que su numero de factura es menor que cero)
				.and(InvoiceDAO.NOT_ANNULLED) // No facturas anuladas
				.and(getAlcatrazCondition(mod190)) // LA FACTURA TIENE QUE ESTAR EN ALCATRAZ EN UN MODELO 111 DEL MISMO AÑO Y LA MISMA ADMINISTRACION
				.groupBy(INVOICE.RDOCUMENT, INVOICE.RNAME,INVOICE_TAX.WITHHOLDING_TYPE,INVOICE_TAX.PERCENTAGE)
				.fetch()
				.stream()
				.forEach(
						rec -> {
							WithholdingType withholding = WithholdingType.safeValueOf(rec.getValue(INVOICE_TAX.WITHHOLDING_TYPE));
							Mod190DetailKey detailKey = withholding.visit( visitor , new Mod190DetailKey().setDocument( rec.getValue(INVOICE.RDOCUMENT) ));
							if ( detailKey.hasKey() ) {
								String mapKey = detailKey.toMapKey();
								Mod190Detail detail = null; 
								if (!map.containsKey(mapKey)) {
									detail = new Mod190Detail();
									detail.setDomain(mod190.getDomain());
									detail.setMod190(mod190.getId());
									detail.setDocument(detailKey.getDocument());
									detail.setName(rec.getValue(INVOICE.RNAME));
									detail.setProvince( RegistryAddressDAO.getMainAddressProvince(ctx, rec.getValue(minRegistry)) );
									detail.setKey(detailKey.getKey());
									detail.setSubKey(detailKey.getSubKey());
									map.put(mapKey, detail);
								}
								detail = map.get(mapKey);
								detail.setPerception(AonMathUtils.round(detail.getPerception() + rec.getValue(sumBase).doubleValue()));
								detail.setRetention(AonMathUtils.round(detail.getRetention() + rec.getValue(quotaOp).doubleValue()));
							}
						});
		mod190.getDetails().addAll(map.values());
		return mod190;
	}

	private Condition getAlcatrazCondition(Mod190 mod190) {
		return DSL.exists(DSL.selectOne()
							.from(ALCATRAZ)
							.innerJoin(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
							.where(ALCATRAZ.INVOICE.equal(INVOICE.ID)
									.and(FS_MODEL.MODEL.equal(FiscalModelType.M111.getValue()))
									.and(FS_MODEL.YEAR.eq(mod190.getYear()))
									.and(FS_MODEL.ADMINISTRATION.equal(mod190.getAdministration().value())))
						);
	}
	
}


