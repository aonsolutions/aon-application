package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod190;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.IrpfData.IRPF_DATA;
import static com.esferalia.aon.jooq.tables.IrpfDataAscendants.IRPF_DATA_ASCENDANTS;
import static com.esferalia.aon.jooq.tables.IrpfDataDescendients.IRPF_DATA_DESCENDIENTS;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.jooq.Field;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.IrpfDataAscendantsRecord;
import com.esferalia.aon.jooq.tables.records.IrpfDataDescendientsRecord;
import com.esferalia.aon.jooq.tables.records.IrpfDataRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.type.Mod1902015Key;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class Mod190ALL2016Declaration extends Mod190Declaration {

	Mod190 insertDetailsFromSalary(AONContext ctx, final Mod190 mod190) {
		Date firstDay = AonDateUtils.getYearFirstDay(mod190.getYear());
		Date lastDay = AonDateUtils.getYearLastDay(mod190.getYear());
		
		Field<Integer> birthYear = DSL.year(PERSON.BIRTH_DATE);
		final TreeMap<String, Mod190Detail> map = new TreeMap<>();
		ctx.getDslContext().select(
				SALARY.EMPLOYEE_DOCUMENT
				,SALARY.EMPLOYEE_NAME
				,SALARY.IRPF_BASE
				,SALARY.MONEY_IRPF_BASE
				,SALARY.INKIND_IRPF_BASE
				,SALARY.TOTAL_IRPF
				,SALARY.SOCIAL_SECURITY_CONTRIBUTIONS
				,PERSON.REGISTRY
				,birthYear)
			.from(SALARY)
			.join(CONTRACT).on(SALARY.CONTRACT.equal(CONTRACT.ID))
			.join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
			.join(PERSON).on(PERSON.REGISTRY.equal(CONTRACT.PERSON))
			.where(SALARY.ISSUE_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
			.and(WORKPLACE.ENTERPRISE.equal(mod190.getEnterprise()))
			.and(WORKPLACE.ECONOMICAGREEMENT.equal(mod190.getAdministration().value()))
			.and(SALARY.TYPE.in(SalaryType.IRPF_SALARIES )) // Skip SLD ( L00, L13... )
			.orderBy(SALARY.EMPLOYEE_DOCUMENT)
			.fetch()
			.stream()
			.forEach(
				salaryData -> {
					String document = salaryData.getValue(SALARY.EMPLOYEE_DOCUMENT);
					Integer person = salaryData.getValue(PERSON.REGISTRY);
					String key = document + "_" + person;
					if (!map.containsKey(key)) {
						final Mod190Detail detail = new Mod190Detail();
						map.put(key, detail);
						detail.setDomain(mod190.getDomain());
						detail.setMod190(mod190.getId());
						detail.setDocument(salaryData.getValue(SALARY.EMPLOYEE_DOCUMENT));
						detail.setName(salaryData.getValue(SALARY.EMPLOYEE_NAME));
						Integer birthData = salaryData.getValue(birthYear);
						detail.setBirthYear(birthData==null?0:birthData);
						fillLastIrpfDataByPerson(ctx,salaryData.getValue(PERSON.REGISTRY),firstDay, lastDay, detail);
						ctx.getDslContext()
							.select(GEOZONE.CODE)
							.from(RADDRESS)
							.join(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
							.where(RADDRESS.REGISTRY.equal(salaryData.getValue(PERSON.REGISTRY)))
							.and(RADDRESS.TYPE.equal((byte) 0))
							.limit(1)
							.fetch()
							.stream()
							.forEach(province -> AonNumberUtils.toint(province.getValue(GEOZONE.CODE)));
					}
					Mod190Detail detail = map.get(key);
					
					double irpfBase = AonMathUtils.round( salaryData.getValue(SALARY.IRPF_BASE));
					double moneyIrpfBase = AonMathUtils.round( salaryData.getValue(SALARY.MONEY_IRPF_BASE));
					double inKindIrpfBase = AonMathUtils.round( salaryData.getValue(SALARY.INKIND_IRPF_BASE));
					double totalIrpf = AonMathUtils.round( salaryData.getValue(SALARY.TOTAL_IRPF));
					double socialSecurityContributions = AonMathUtils.round( salaryData.getValue(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS));
					double moneyQuota = 0;
					double inKindQuota = 0;
					
					if (inKindIrpfBase == 0.0) {
						moneyIrpfBase = irpfBase;
						moneyQuota = totalIrpf; 	
						inKindQuota = 0;
					} else {
						moneyQuota = AonMathUtils.round(moneyIrpfBase * totalIrpf / irpfBase);
						inKindQuota = AonMathUtils.round(totalIrpf - moneyQuota);
					}
					
					detail.setKey(Mod1902015Key.A.getValue());
					if (mod190.getYear() == 2015) {
						detail.setSubKey("01");
					} 
					detail.setPerception(AonMathUtils.round( detail.getPerception() + moneyIrpfBase ));
					detail.setRetention(AonMathUtils.round( detail.getRetention() + moneyQuota));
					
					detail.setInKindPerception(AonMathUtils.round( detail.getInKindPerception() + inKindIrpfBase));
					detail.setInKindDeposit(AonMathUtils.round( detail.getInKindDeposit() + inKindQuota));

					detail.setDeducibleExpense(AonMathUtils.round( detail.getDeducibleExpense() + socialSecurityContributions ));
		});
		mod190.getDetails().addAll( map.values() );
		return mod190;
	}

	private static void fillLastIrpfDataByPerson(AONContext ctx, int person, Date fromDate, Date toDate, Mod190Detail detail) {
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
		
		if (list != null && list.size() > 0) { 
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
			if (descs != null && descs.size() > 0) {
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
			if (ascs != null && ascs.size() > 0) {
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
	
	public LinkedList<Mod190Detail> validateSalaries(AONContext ctx, Mod190 mod190) {
		return null; 
	}

}
