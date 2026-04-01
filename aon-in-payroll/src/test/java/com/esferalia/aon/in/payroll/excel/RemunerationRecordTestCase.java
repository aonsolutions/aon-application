package com.esferalia.aon.in.payroll.excel;

import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.code.aon.person.enumeration.Gender;
import com.esferalia.aon.in.payroll.excel.IRemunerationRecordEntry.Schedule;
import com.esferalia.aon.in.payroll.excel.IRetributiveConcept.RetributionForm;
import com.esferalia.aon.in.payroll.excel.IRetributiveConcept.RetributionType;
import com.esferalia.aon.occam.test.Repeat;
import com.esferalia.aon.payroll.enumeration.FamilySituation;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.github.javafaker.Faker;
//@Ignore
public class RemunerationRecordTestCase {
public static class RemunerationRecordEntry implements IRemunerationRecordEntry{
		
		private String name;
		private String socialSecurityNumber;
		private Gender gender;
		private Date birthDate;
		private String studies;
		private FamilySituation familySituation;
		private Integer children;
		private Date hireDate;
		private Date contractEndDate;
		private Date seniorityDate;
		private Date contractSituationStartDate;
		private Date contractSituationEndDate;
		private Double workdayPercent;
		private Double reducedWorkdayPercent;
		private String workdayReductionReason;
		private String contractKey;
		private String enterpriseArea;
		private String enterpriseDepartment;
		private String category;
		private Schedule schedule;
		private Boolean byTurns;
		private String enterpriseScale;
		private String professionalClass;
		private String scale;
		private String agreement;
		private String professionalCategory;
		private String professionalGroup;
		private String level;
		private Integer quoteGroup;
		
		private Map<String, Double> payments;
		
		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public String getSocialSecurityNumber() {
			return socialSecurityNumber;
		}

		@Override
		public Gender getGender() {
			return gender;
		}

		@Override
		public Date getBirthDate() {
			return birthDate;
		}

		@Override
		public String getStudies() {
			return studies;
		}

		@Override
		public FamilySituation getFamilySituation() {
			return familySituation;
		}

		@Override
		public Integer getChildren() {
			return children;
		}

		@Override
		public Date getHireDate() {
			return hireDate;
		}

		@Override
		public Date getContractEndDate() {
			return contractEndDate;
		}

		@Override
		public Date getSeniorityDate() {
			return seniorityDate;
		}

		@Override
		public Date getContractSituationStartDate() {
			return contractSituationStartDate;
		}

		@Override
		public Date getContractSituationEndDate() {
			return contractSituationEndDate;
		}

		@Override
		public Double getWorkdayPercent() {
			return workdayPercent;
		}

		@Override
		public Double getReducedWorkdayPercent() {
			return reducedWorkdayPercent;
		}

		@Override
		public String getWorkdayReductionReason() {
			return workdayReductionReason;
		}

		@Override
		public String getContractKey() {
			return contractKey;
		}

		@Override
		public String getEnterpriseArea() {
			return enterpriseArea;
		}

		@Override
		public String getEnterpriseDepartment() {
			return enterpriseDepartment;
		}

		@Override
		public String getCategory() {
			return category;
		}

		@Override
		public Schedule getSchedule() {
			return schedule;
		}

		@Override
		public Boolean isByTurns() {
			return byTurns;
		}

		@Override
		public String getEnterpriseScale() {
			return enterpriseScale;
		}

		@Override
		public String getProfessionalClass() {
			return professionalClass;
		}

		@Override
		public String getScale() {
			return scale;
		}

		@Override
		public String getAgreement() {
			return agreement;
		}

		@Override
		public String getProfessionalCategory() {
			return professionalCategory;
		}

		@Override
		public String getProfessionalGroup() {
			return professionalGroup;
		}

		@Override
		public String getLevel() {
			return level;
		}

		@Override
		public Integer getQuoteGroup() {
			return quoteGroup;
		}

		@Override
		public Map<String, Double> getPayments() {
			return payments;
		}
		
	}
	
	public static class RetributiveConcept implements IRetributiveConcept {
		
		PaymentType type;
		String name;
		String description;
		RetributionForm retributionForm;
		RetributionType retributionType;
		Boolean normalizable;
		Boolean anualizable;
		
		@Override
		public PaymentType getType() {
			return type;
		}
		
		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public RetributionForm getRetributionForm() {
			return retributionForm;
		}

		@Override
		public RetributionType getRetributionType() {
			return retributionType;
		}

		@Override
		public Boolean isNormalizable() {
			return normalizable;
		}

		@Override
		public Boolean isAnualizable() {
			return anualizable;
		}
		
	}
	

	@Test
	@Repeat (100)
	public void test() {
		Faker faker = Faker.instance(new Locale("es"));
		RemunerationRecordData data = new RemunerationRecordData();
		data.setStartDate(faker.date().past(365, TimeUnit.DAYS));
		data.setEndDate(faker.date().future(100, TimeUnit.DAYS));
		data.setEnterpriseDocument(faker.chuckNorris().fact());
		data.setSocialReason(faker.company().name());
		
		LinkedList<IRetributiveConcept> conceptList = new LinkedList<IRetributiveConcept>();
		int conceptNum = faker.number().numberBetween(0, 30);
		for (int i=1; i<conceptNum; i++) {
			RetributiveConcept concept = new RetributiveConcept();
			concept.anualizable = (faker.number().numberBetween(1, 100) < 20) ? null :faker.bool().bool();
			concept.description = (faker.number().numberBetween(1, 100) < 20) ? null : faker.chuckNorris().fact();
			concept.name = (faker.number().numberBetween(1, 100) < 20) ? null : faker.pokemon().name();
			concept.normalizable = (faker.number().numberBetween(1, 100) < 20) ? null : faker.bool().bool();
			concept.retributionForm = (faker.number().numberBetween(1, 100) < 20) ? null : faker.bool().bool() ? RetributionForm.IN_KIND : RetributionForm.MONEY;
			concept.retributionType = (faker.number().numberBetween(1, 100) < 20) ? null : RetributionType.values()[faker.number().numberBetween(0, RetributionType.values().length-1)];
			concept.type = (faker.number().numberBetween(1, 100) < 20) ? null : PaymentType.values()[faker.number().numberBetween(0, PaymentType.values().length-1)];
			conceptList.add(concept);
		}
		data.setConcepts(conceptList);
		LinkedHashMap<String, LinkedList<IRemunerationRecordEntry>> entries = new LinkedHashMap<String, LinkedList<IRemunerationRecordEntry>>();
		
		for (int i=1; i<faker.number().numberBetween(0, 90); i++) {
			LinkedList<IRemunerationRecordEntry> entryList = new LinkedList<IRemunerationRecordEntry>(); 
			for (int j=1; j<faker.number().numberBetween(0, 2); j++) {
				RemunerationRecordEntry entry = new RemunerationRecordEntry();
				entry.agreement = (faker.number().numberBetween(1, 100) < 20) ? null : faker.chuckNorris().fact();
				entry.birthDate = (faker.number().numberBetween(1, 100) < 20) ? null : faker.date().past(68*365, TimeUnit.DAYS);
				entry.byTurns = (faker.number().numberBetween(1, 100) < 20) ? null : faker.bool().bool();
				entry.category = (faker.number().numberBetween(1, 100) < 20) ? null : faker.pokemon().name();
				entry.children = (faker.number().numberBetween(1, 100) < 20) ? null : faker.number().numberBetween(0, 10);
				entry.contractEndDate = (faker.number().numberBetween(1, 100) < 20) ? null : faker.date().past(5000, TimeUnit.DAYS);
				entry.contractKey = (faker.number().numberBetween(1, 100) < 20) ? null : ""+faker.number().numberBetween(0, 1000);
				entry.contractSituationEndDate = (faker.number().numberBetween(1, 100) < 20) ? null : faker.date().past(365, TimeUnit.DAYS);
				entry.contractSituationStartDate = (faker.number().numberBetween(1, 100) < 20) ? null : faker.date().past(365*10, TimeUnit.DAYS);
				entry.enterpriseArea = (faker.number().numberBetween(1, 100) < 20) ? null : faker.lebowski().quote();
				entry.enterpriseDepartment = (faker.number().numberBetween(1, 100) < 20) ? null : faker.esports().game();
				entry.enterpriseScale = (faker.number().numberBetween(1, 100) < 20) ? null : faker.internet().domainName();
				entry.familySituation = (faker.number().numberBetween(1, 100) < 20) ? null : FamilySituation.values()[faker.number().numberBetween(0, FamilySituation.values().length-1)];
				entry.gender = (faker.number().numberBetween(1, 100) < 20) ? null : Gender.values()[faker.number().numberBetween(0, Gender.values().length-1)];
				entry.hireDate = (faker.number().numberBetween(1, 100) < 20) ? null : faker.date().past(5000, TimeUnit.DAYS);
				entry.level = (faker.number().numberBetween(1, 100) < 20) ? null : faker.ancient().titan();
				entry.name = (faker.number().numberBetween(1, 100) < 20) ? null : faker.lebowski().actor();
				LinkedHashMap<String, Double> payments = new LinkedHashMap<String, Double>();
				data.getConcepts().forEach(c -> {
					payments.put(c.getName(), faker.number().numberBetween(0, 5000) * Math.random());
				});
				entry.payments = (faker.number().numberBetween(1, 100) < 20) ? null : payments;
				entry.professionalCategory = (faker.number().numberBetween(1, 100) < 20) ? null : faker.chuckNorris().fact();
				entry.professionalClass = (faker.number().numberBetween(1, 100) < 20) ? null : faker.currency().name();
				entry.professionalGroup = (faker.number().numberBetween(1, 100) < 20) ? null : faker.beer().name();
				entry.quoteGroup = (faker.number().numberBetween(1, 100) < 20) ? null : faker.number().numberBetween(0, 10);
				entry.reducedWorkdayPercent = (faker.number().numberBetween(1, 100) < 20) ? null : Math.random() * faker.number().numberBetween(0, 100);
				entry.scale = (faker.number().numberBetween(1, 100) < 20) ? null : faker.book().title();
				entry.schedule = (faker.number().numberBetween(1, 100) < 20) ? null : Schedule.values()[faker.number().numberBetween(0, Schedule.values().length-1)];
				entry.seniorityDate = (faker.number().numberBetween(1, 100) < 20) ? null : faker.date().past(7000, TimeUnit.DAYS);
				entry.socialSecurityNumber = (faker.number().numberBetween(1, 100) < 20) ? null : faker.business().creditCardNumber();
				entry.studies = (faker.number().numberBetween(1, 100) < 20) ? null : faker.educator().course();
				entry.workdayPercent = (faker.number().numberBetween(1, 100) < 20) ? null : faker.number().numberBetween(0, 100) * Math.random();
				entry.workdayReductionReason = (faker.number().numberBetween(1, 100) < 20) ? null : faker.demographic().educationalAttainment();
				entryList.add(entry);
			}
			entries.put(faker.friends().character(), entryList);
		}
		data.setEntries(faker.number().numberBetween(1, 100) < 20 ? null : entries);
		RemunerationRecord.getExcel(OutputStream.nullOutputStream(), data);
	}

}
