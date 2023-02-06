package com.esferalia.aon.occam.impl.jooq.dao.mod145;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.IrpfData.IRPF_DATA;
import static com.esferalia.aon.jooq.tables.IrpfDataAscendants.IRPF_DATA_ASCENDANTS;
import static com.esferalia.aon.jooq.tables.IrpfDataDescendients.IRPF_DATA_DESCENDIENTS;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Select;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Mod145Filter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.finance.Properties.Mod145Properties;
import com.esferalia.aon.occam.api.model.mod145.IrpfDataAscendants;
import com.esferalia.aon.occam.api.model.mod145.IrpfDataDescendients;
import com.esferalia.aon.occam.api.model.mod145.Mod145;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FilterDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod145DAO {
	
	// -------------------------------------- Constructor

	private Mod145DAO() {
		throw new IllegalStateException("Utility Class");
	}
	
	// -------------------------------------- Mod145 Properties
	
	private static final Mod145PropertiesDAO MOD_145_PROPERTIES = new Mod145PropertiesDAO();
	protected static class Mod145PropertiesDAO implements Mod145Properties {
		protected Select<Record> build(SelectJoinStep<Record> select, Mod145Filter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(Mod145Filter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.DOMAIN);}
		@Override public Property<Integer> getContractProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.CONTRACT);}
		@Override public Property<Date> getStartDateProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.START_DATE);}
		@Override public Property<Date> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.END_DATE);}
		
	}
	
	// -------------------------------------- Mod145 Filler
	
	public static class Mod145Filler extends Filler implements Function<Record, Mod145> {

		@Override
		public Mod145 apply(Record r) {
			return new Mod145()
					.setId(r.getValue(IRPF_DATA.ID))
					.setDomain(r.getValue(IRPF_DATA.DOMAIN))
					.setContract(r.getValue(IRPF_DATA.CONTRACT))
					.setNif(r.getValue(REGISTRY.DOCUMENT))
					.setFullName(createFullName(r))
					.setBirthDate(r.get(PERSON.BIRTH_DATE))
					.setFamilySituation(r.getValue(IRPF_DATA.FAMILY_SITUATION))
					.setSpouseDocument(r.getValue(IRPF_DATA.SPOUSE_DOCUMENT))
					.setDisabilityLevel(r.getValue(IRPF_DATA.DISABILITY_LEVEL))
					.setDependence(getBoolean(r, IRPF_DATA.DEPENDENCE))
					.setMovingDate(r.getValue(IRPF_DATA.MOVING_DATE))
					.setLabourProlongation(getBoolean(r, IRPF_DATA.LABOUR_PROLONGATION))
					.setDescendientCount(r.getValue(IRPF_DATA.DESCENDIENT_COUNT))
					.setStartDate(r.getValue(IRPF_DATA.START_DATE))
					.setEndDate(r.getValue(IRPF_DATA.END_DATE))
					.setFiscalExclusion(getBoolean(r, IRPF_DATA.FISCAL_EXCLUSION))
					.setIssueDate(r.getValue(IRPF_DATA.ISSUE_DATE))
					.setSpousalSupport(r.getValue(IRPF_DATA.SPOUSAL_SUPPORT))
					.setFoodAnnuity(r.getValue(IRPF_DATA.FOOD_ANNUITY))
					.setIrpfPercent(r.getValue(IRPF_DATA.REQUEST_IRPF))
					.setDeductionHomeLoan(getBoolean(r, IRPF_DATA.DEDUCT_HOME_LOAN))
					.setDeleted(false)
					;
					
		}

		private String createFullName(Record r) {
			String secondSurname = r.get(PERSON.SECOND_SURNAME);
			String firstSurname = r.get(PERSON.FIRST_SURNAME);
			String name = r.get(PERSON.NAME);
			
			String fullName = firstSurname + " " + secondSurname;
			fullName = fullName.trim();
			fullName = AonStringUtils.isBlank(fullName) ? name : fullName + ", " + name;
			
			return fullName;
		}

	}
	
	public static class IrpfDataAscendantsFiller extends Filler implements Function<Record, IrpfDataAscendants> {

		@Override
		public IrpfDataAscendants apply(Record r) {
			return new IrpfDataAscendants()
					.setId(r.getValue(IRPF_DATA_ASCENDANTS.ID))
					.setDomain(r.getValue(IRPF_DATA_ASCENDANTS.DOMAIN))
					.setIrpfData(r.getValue(IRPF_DATA_ASCENDANTS.IRPF_DATA))
					.setBirthYear(r.getValue(IRPF_DATA_ASCENDANTS.BIRTH_YEAR))
					.setDisabilityLevel(r.getValue(IRPF_DATA_ASCENDANTS.DISABILITY_LEVEL))
					.setDependence(getBoolean(r, IRPF_DATA_ASCENDANTS.DEPENDENCE))
					.setAnotherDescendient(getBoolean(r, IRPF_DATA_ASCENDANTS.ANOTHER_DESCENDIENT))
					.setDeleted(false)
					;
					
		}

	}
	
	public static class IrpfDataDescendientsFiller extends Filler implements Function<Record, IrpfDataDescendients> {

		@Override
		public IrpfDataDescendients apply(Record r) {
			return new IrpfDataDescendients()
					.setId(r.getValue(IRPF_DATA_DESCENDIENTS.ID))
					.setDomain(r.getValue(IRPF_DATA_DESCENDIENTS.DOMAIN))
					.setIrpfData(r.getValue(IRPF_DATA_DESCENDIENTS.IRPF_DATA))
					.setBirthYear(r.getValue(IRPF_DATA_DESCENDIENTS.BIRTH_YEAR))
					.setAdoptionYear(r.getValue(IRPF_DATA_DESCENDIENTS.ADOPTION_YEAR))
					.setDisabilityLevel(r.getValue(IRPF_DATA_DESCENDIENTS.DISABILITY_LEVEL))
					.setDependence(getBoolean(r, IRPF_DATA_DESCENDIENTS.DEPENDENCE))
					.setUniqueParent(getBoolean(r, IRPF_DATA_DESCENDIENTS.UNIQUE_PARENT))
					.setDeleted(false)
					;
					
		}

	}
	
	// -------------------------------------- CRUD Methods
	
	public static List<Mod145> getList(AONContext ctx, Mod145Filter filter) {
		List<Mod145> mod145List = ctx.getDslContext()
				.select()
				.from(IRPF_DATA)
				.innerJoin(CONTRACT)
				.on(CONTRACT.ID.eq(IRPF_DATA.CONTRACT))
				.innerJoin(PERSON)
				.on(PERSON.REGISTRY.eq(CONTRACT.PERSON))
				.innerJoin(REGISTRY)
				.on(REGISTRY.ID.eq(CONTRACT.PERSON))
				.where(MOD_145_PROPERTIES.getConditions(filter))
				.fetch()
				.stream()
				.map(new Mod145Filler())
				.collect(Collectors.toList());
		
		mod145List.forEach(mod145 -> {
			mod145.setEnterpriseName(getEnterpriseName(ctx, mod145.getDomain()));
			mod145.setIrpfPercent(getIrpfPercent(ctx, mod145.getContract(), mod145.getStartDate()));
			mod145.setAscendants(getAscendants(ctx, mod145.getId()));
			mod145.setDescendients(getDescendients(ctx, mod145.getId()));
		});
		
		return mod145List;
	}

	public static Mod145 get(AONContext ctx, Mod145Filter filter) {
		ctx.checkRead();
		
		Mod145 mod145 = ctx.getDslContext()
				.select()
				.from(IRPF_DATA)
				.innerJoin(CONTRACT)
				.on(CONTRACT.ID.eq(IRPF_DATA.CONTRACT))
				.innerJoin(PERSON)
				.on(PERSON.REGISTRY.eq(CONTRACT.PERSON))
				.innerJoin(REGISTRY)
				.on(REGISTRY.ID.eq(CONTRACT.PERSON))
				.where(MOD_145_PROPERTIES.getConditions(filter))
				.fetch()
				.stream()
				.map(new Mod145Filler())
				.findFirst()
				.orElse(new Mod145());

		mod145.setEnterpriseName(getEnterpriseName(ctx, mod145.getDomain()));
		mod145.setIrpfPercent(getIrpfPercent(ctx, mod145.getContract(), mod145.getStartDate()));
		mod145.setAscendants(getAscendants(ctx, mod145.getId()));
		mod145.setDescendients(getDescendients(ctx, mod145.getId()));
		
		return mod145;
	}
	
	private static String getEnterpriseName(AONContext ctx, Integer domain) {
		return ctx.getDslContext().select(REGISTRY.NAME)
				.from(REGISTRY)
				.innerJoin(ENTERPRISE)
				.on(ENTERPRISE.REGISTRY.eq(REGISTRY.ID))
				.where(ENTERPRISE.DOMAIN.eq(domain))
				.fetchOne().getValue(REGISTRY.NAME);
	}

	private static Double getIrpfPercent(AONContext ctx, Integer contract, java.util.Date startDate) {
		List<String> irpfPercents = ctx.getDslContext().select(CONTRACT_DATA.EXPRESSION)
				.from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contract))
				.and(CONTRACT_DATA.START_DATE.eq(parseToSqlDate(startDate)))
				.and(CONTRACT_DATA.NAME.eq("PORCENTAJE_IRPF"))
				.fetch(CONTRACT_DATA.EXPRESSION);
		
		return irpfPercents.isEmpty() || AonStringUtils.isBlank(irpfPercents.get(0)) ? null : Double.parseDouble(irpfPercents.get(0));
	}
	
	private static List<IrpfDataAscendants> getAscendants(AONContext ctx, Integer irpfData) {
		return ctx.getDslContext()
				.select()
				.from(IRPF_DATA_ASCENDANTS)
				.where(IRPF_DATA_ASCENDANTS.IRPF_DATA.eq(irpfData))
				.fetch()
				.stream()
				.map(new IrpfDataAscendantsFiller())
				.collect(Collectors.toList());
	}

	private static List<IrpfDataDescendients> getDescendients(AONContext ctx, Integer irpfData) {
		return ctx.getDslContext()
				.select()
				.from(IRPF_DATA_DESCENDIENTS)
				.where(IRPF_DATA_DESCENDIENTS.IRPF_DATA.eq(irpfData))
				.fetch()
				.stream()
				.map(new IrpfDataDescendientsFiller())
				.collect(Collectors.toList());
	}
	
	public static void save(AONContext ctx, Mod145 mod145) {
		if((mod145.getId() == null || mod145.getId() < 0) && !mod145.isDeleted()) insert(ctx, mod145);
		else if(mod145.isDeleted()) delete(ctx, mod145);
		else update(ctx, mod145);
	}
	
	private static Mod145 insert(AONContext ctx, Mod145 mod145) {
		ctx.checkWrite();
		Integer id = ctx.getDslContext().insertInto(IRPF_DATA)
			.set(IRPF_DATA.DOMAIN, mod145.getDomain())
			.set(IRPF_DATA.CONTRACT, mod145.getContract())
			.set(IRPF_DATA.FAMILY_SITUATION, mod145.getFamilySituation())
			.set(IRPF_DATA.SPOUSE_DOCUMENT, mod145.getSpouseDocument())
			.set(IRPF_DATA.DISABILITY_LEVEL, mod145.getDisabilityLevel())
			.set(IRPF_DATA.DEPENDENCE, mod145.isDependence() ? (byte)1 : (byte)0)
			.set(IRPF_DATA.MOVING_DATE, parseToSqlDate(mod145.getMovingDate()))
			.set(IRPF_DATA.LABOUR_PROLONGATION, mod145.isLabourProlongation() ? (byte)1 : (byte)0)
			.set(IRPF_DATA.DESCENDIENT_COUNT, mod145.getDescendientCount())
			.set(IRPF_DATA.START_DATE, parseToSqlDate(mod145.getStartDate()))
			.set(IRPF_DATA.END_DATE, parseToSqlDate(mod145.getEndDate()))
			.set(IRPF_DATA.FISCAL_EXCLUSION, mod145.isFiscalExclusion() ? (byte)1 : (byte)0)
			.set(IRPF_DATA.ISSUE_DATE, parseToSqlDate(mod145.getIssueDate()))
			.set(IRPF_DATA.SPOUSAL_SUPPORT, mod145.getSpousalSupport())
			.set(IRPF_DATA.FOOD_ANNUITY, mod145.getFoodAnnuity())
			.set(IRPF_DATA.REQUEST_IRPF, mod145.getIrpfPercent())
			.set(IRPF_DATA.DEDUCT_HOME_LOAN, mod145.isDeductionHomeLoan() ? (byte)1 : null)
			.returning(IRPF_DATA.ID).fetchOne().getId();
			ctx.log().debug("INSERT IRPF DATA id: " + id);
			
		setIrpfPercent(ctx, mod145.getContract(), mod145.getDomain(), mod145.getStartDate(), mod145.getEndDate(), mod145.getIrpfPercent());
		mod145.getAscendants().forEach(ascendant -> ascendant.setIrpfData(id));
		mod145.getDescendients().forEach(descendient -> descendient.setIrpfData(id));
				
		saveAscendants(ctx, mod145);
		saveDescendents(ctx, mod145);
		
		return mod145.setId(id);
	}

	private static Mod145 update(AONContext ctx, Mod145 mod145) {
		ctx.checkWrite();
		ctx.getDslContext()
			.update(IRPF_DATA)
			.set(IRPF_DATA.FAMILY_SITUATION, mod145.getFamilySituation())
			.set(IRPF_DATA.SPOUSE_DOCUMENT, mod145.getSpouseDocument())
			.set(IRPF_DATA.DISABILITY_LEVEL, mod145.getDisabilityLevel())
			.set(IRPF_DATA.DEPENDENCE, mod145.isDependence() ? (byte)1 : (byte)0)
			.set(IRPF_DATA.MOVING_DATE, parseToSqlDate(mod145.getMovingDate()))
			.set(IRPF_DATA.LABOUR_PROLONGATION, mod145.isLabourProlongation() ? (byte)1 : (byte)0)
			.set(IRPF_DATA.DESCENDIENT_COUNT, mod145.getDescendientCount())
			.set(IRPF_DATA.START_DATE, parseToSqlDate(mod145.getStartDate()))
			.set(IRPF_DATA.END_DATE, parseToSqlDate(mod145.getEndDate()))
			.set(IRPF_DATA.FISCAL_EXCLUSION, mod145.isFiscalExclusion() ? (byte)1 : (byte)0)
			.set(IRPF_DATA.ISSUE_DATE, parseToSqlDate(mod145.getIssueDate()))
			.set(IRPF_DATA.SPOUSAL_SUPPORT, mod145.getSpousalSupport())
			.set(IRPF_DATA.FOOD_ANNUITY, mod145.getFoodAnnuity())
			.set(IRPF_DATA.REQUEST_IRPF, mod145.getIrpfPercent())
			.set(IRPF_DATA.DEDUCT_HOME_LOAN, mod145.isDeductionHomeLoan() ? (byte)1 : null)
			.where(IRPF_DATA.ID.eq(mod145.getId()))
			.execute();		
		ctx.log().debug("UPDATE IRPF DATA id: " + mod145.getId());	
		
		setIrpfPercent(ctx, mod145.getContract(), mod145.getDomain(), mod145.getStartDate(), mod145.getEndDate(), mod145.getIrpfPercent());
		saveAscendants(ctx, mod145);
		saveDescendents(ctx, mod145);
		
		return mod145;
	}

	private static void delete(AONContext ctx, Mod145 mod145) {
		ctx.checkWrite();
		ctx.getDslContext()
			.delete(IRPF_DATA_ASCENDANTS)
			.where(IRPF_DATA_ASCENDANTS.IRPF_DATA.eq(mod145.getId()))
			.execute();	
		
		ctx.getDslContext()
			.delete(IRPF_DATA_DESCENDIENTS)
			.where(IRPF_DATA_DESCENDIENTS.IRPF_DATA.eq(mod145.getId()))
			.execute();	
		
		ctx.getDslContext()
			.delete(IRPF_DATA)
			.where(IRPF_DATA.ID.eq(mod145.getId()))
			.execute();
		
		ctx.getDslContext()
			.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.NAME.eq("PORCENTAJE_IRPF"))
			.and(CONTRACT_DATA.CONTRACT.eq(mod145.getContract()))
			.and(CONTRACT_DATA.START_DATE.eq(parseToSqlDate(mod145.getStartDate())))
			.execute();
			
		ctx.log().debug("DELETE IRPF DATA id: " + mod145.getId());	
	}
	
	private static void setIrpfPercent(AONContext ctx, Integer contract, Integer domain, java.util.Date startDate, java.util.Date endDate, Double irpfPercent) {
		Result<ContractDataRecord> updateIrpfPercent = ctx.getDslContext().selectFrom(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contract))
			.and(CONTRACT_DATA.NAME.eq("PORCENTAJE_IRPF"))
			.and(CONTRACT_DATA.START_DATE.eq(parseToSqlDate(startDate))).fetch();
		
		if(updateIrpfPercent.isEmpty() && null != irpfPercent) {
			ctx.getDslContext().insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.CONTRACT, contract)
				.set(CONTRACT_DATA.NAME, "PORCENTAJE_IRPF")
				.set(CONTRACT_DATA.EXPRESSION, irpfPercent.toString())
				.set(CONTRACT_DATA.START_DATE, parseToSqlDate(startDate))
				.set(CONTRACT_DATA.END_DATE, parseToSqlDate(endDate))
				.execute();
		} else if(!updateIrpfPercent.isEmpty()) {
			if(null != irpfPercent)
				ctx.getDslContext().update(CONTRACT_DATA)
					.set(CONTRACT_DATA.EXPRESSION, irpfPercent.toString())
					.set(CONTRACT_DATA.START_DATE, parseToSqlDate(startDate))
					.set(CONTRACT_DATA.END_DATE, parseToSqlDate(endDate))
					.where(CONTRACT_DATA.ID.eq(updateIrpfPercent.get(0).getId()))
					.execute();
			else
				ctx.getDslContext().delete(CONTRACT_DATA)
					.where(CONTRACT_DATA.ID.eq(updateIrpfPercent.get(0).getId()))
					.execute();
		}	
		
	}

	private static void saveAscendants(AONContext ctx, Mod145 mod145) {
		if(mod145.getAscendants().isEmpty()) return;
		
		mod145.getAscendants().forEach(ascendant -> {
			if((ascendant.getId() == null || ascendant.getId() < 0) && !ascendant.isDeleted()) insert(ctx, ascendant);
			else if(ascendant.isDeleted()) delete(ctx, ascendant);
			else update(ctx, ascendant);
		});
	}

	private static IrpfDataAscendants insert(AONContext ctx, IrpfDataAscendants ascendant) {
		Integer id = ctx.getDslContext().insertInto(IRPF_DATA_ASCENDANTS)
				.set(IRPF_DATA_ASCENDANTS.DOMAIN, ascendant.getDomain())
				.set(IRPF_DATA_ASCENDANTS.IRPF_DATA, ascendant.getIrpfData())
				.set(IRPF_DATA_ASCENDANTS.BIRTH_YEAR, ascendant.getBirthYear())
				.set(IRPF_DATA_ASCENDANTS.DISABILITY_LEVEL, ascendant.getDisabilityLevel())
				.set(IRPF_DATA_ASCENDANTS.DEPENDENCE, ascendant.isDependence() ? (byte)1 : (byte)0)
				.set(IRPF_DATA_ASCENDANTS.ANOTHER_DESCENDIENT, ascendant.isAnotherDescendient() ? (byte)1 : (byte)0)
				.returning(IRPF_DATA_ASCENDANTS.ID).fetchOne().getId();
				ctx.log().debug("INSERT IRPF DATA ASCENDANTS id: " + id);
				
		return ascendant.setId(id);
	}
	
	private static void update(AONContext ctx, IrpfDataAscendants ascendant) {
		ctx.checkWrite();
		ctx.getDslContext()
			.update(IRPF_DATA_ASCENDANTS)
			.set(IRPF_DATA_ASCENDANTS.BIRTH_YEAR, ascendant.getBirthYear())
			.set(IRPF_DATA_ASCENDANTS.DISABILITY_LEVEL, ascendant.getDisabilityLevel())
			.set(IRPF_DATA_ASCENDANTS.DEPENDENCE, ascendant.isDependence() ? (byte)1 : (byte)0)
			.set(IRPF_DATA_ASCENDANTS.ANOTHER_DESCENDIENT, ascendant.isAnotherDescendient() ? (byte)1 : (byte)0)
			.where(IRPF_DATA_ASCENDANTS.ID.eq(ascendant.getId()))
			.execute();		
		ctx.log().debug("UPDATE IRPF DATA ASCENDANTS id: " + ascendant.getId());	
	}

	private static void delete(AONContext ctx, IrpfDataAscendants ascendant) {
		ctx.checkWrite();
		ctx.getDslContext()
			.delete(IRPF_DATA_ASCENDANTS)
			.where(IRPF_DATA_ASCENDANTS.ID.eq(ascendant.getId()))
			.execute();	
		ctx.log().debug("DELETE IRPF DATA ASCENDANTS id: " + ascendant.getId());	
	}
	
	private static void saveDescendents(AONContext ctx, Mod145 mod145) {
		if(mod145.getDescendients().isEmpty()) return;
		
		mod145.getDescendients().forEach(descendient -> {
			if((descendient.getId() == null || descendient.getId() < 0) && !descendient.isDeleted()) insert(ctx, descendient);
			else if(descendient.isDeleted()) delete(ctx, descendient);
			else update(ctx, descendient);
		});
	}

	private static IrpfDataDescendients insert(AONContext ctx, IrpfDataDescendients descendient) {
		Integer id = ctx.getDslContext().insertInto(IRPF_DATA_DESCENDIENTS)
				.set(IRPF_DATA_DESCENDIENTS.DOMAIN, descendient.getDomain())
				.set(IRPF_DATA_DESCENDIENTS.IRPF_DATA, descendient.getIrpfData())
				.set(IRPF_DATA_DESCENDIENTS.BIRTH_YEAR, descendient.getBirthYear())
				.set(IRPF_DATA_DESCENDIENTS.ADOPTION_YEAR, descendient.getAdoptionYear())
				.set(IRPF_DATA_DESCENDIENTS.DISABILITY_LEVEL, descendient.getDisabilityLevel())
				.set(IRPF_DATA_DESCENDIENTS.DEPENDENCE, descendient.isDependence() ? (byte)1 : (byte)0)
				.set(IRPF_DATA_DESCENDIENTS.UNIQUE_PARENT, descendient.isUniqueParent() ? (byte)1 : (byte)0)
				.returning(IRPF_DATA_DESCENDIENTS.ID).fetchOne().getId();
				ctx.log().debug("INSERT IRPF DATA DESCENDIENTS id: " + id);
				
		return descendient.setId(id);
	}
	
	private static void update(AONContext ctx, IrpfDataDescendients descendient) {
		ctx.checkWrite();
		ctx.getDslContext()
			.update(IRPF_DATA_DESCENDIENTS)
			.set(IRPF_DATA_DESCENDIENTS.BIRTH_YEAR, descendient.getBirthYear())
			.set(IRPF_DATA_DESCENDIENTS.ADOPTION_YEAR, descendient.getAdoptionYear())
			.set(IRPF_DATA_DESCENDIENTS.DISABILITY_LEVEL, descendient.getDisabilityLevel())
			.set(IRPF_DATA_DESCENDIENTS.DEPENDENCE, descendient.isDependence() ? (byte)1 : (byte)0)
			.set(IRPF_DATA_DESCENDIENTS.UNIQUE_PARENT, descendient.isUniqueParent() ? (byte)1 : (byte)0)
			.where(IRPF_DATA_DESCENDIENTS.ID.eq(descendient.getId()))
			.execute();		
		ctx.log().debug("UPDATE IRPF DATA DESCENDIENTS id: " + descendient.getId());	
	}

	private static void delete(AONContext ctx, IrpfDataDescendients descendient) {
		ctx.checkWrite();
		ctx.getDslContext()
			.delete(IRPF_DATA_DESCENDIENTS)
			.where(IRPF_DATA_DESCENDIENTS.ID.eq(descendient.getId()))
			.execute();	
		ctx.log().debug("DELETE IRPF DATA DESCENDIENTS id: " + descendient.getId());	
	}
	
	private static Date parseToSqlDate(java.util.Date date){
		if(null == date) return null;
		return new Date(date.getTime());
	}
	
}
