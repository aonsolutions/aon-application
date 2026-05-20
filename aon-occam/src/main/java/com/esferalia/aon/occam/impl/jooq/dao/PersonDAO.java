package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.sql.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.PersonFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.Properties.PersonProperties;
import com.esferalia.aon.occam.api.model.type.Gender;
import com.esferalia.aon.occam.api.model.type.MaritalStatus;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryPropertiesDAO;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class PersonDAO {
	
	private PersonDAO() {}
	
	private static final PersonPropertiesDAO PERSON_PROPERTIES = new PersonPropertiesDAO();
	public static class PersonPropertiesDAO extends RegistryPropertiesDAO implements PersonProperties {
		
		
		protected Condition[] getConditions(PersonFilter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(PERSON.REGISTRY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(PERSON.DOMAIN);}
		@Override public Property<Date> getBirthDateProperty() {return new FilterDAO.PropertyDAO<>(PERSON.BIRTH_DATE);}
		@Override public Property<Byte> getGenderProperty() {return new FilterDAO.PropertyDAO<>(PERSON.GENDER);}
		@Override public Property<Byte> getMaritalStatusProperty() {return new FilterDAO.PropertyDAO<>(PERSON.MARITAL_STATUS);}
		@Override public Property<String> getSocialSecurityNumProperty() {return new FilterDAO.PropertyDAO<>(PERSON.SOCIAL_SECURITY_NUM);}
		@Override public Property<String> getFirstNameProperty()  {return new FilterDAO.PropertyDAO<>(PERSON.NAME);}
		@Override public Property<String> getFirstSurnameProperty() {return new FilterDAO.PropertyDAO<>(PERSON.FIRST_SURNAME);}
		@Override public Property<String> getSecondSurnameProperty() {return new FilterDAO.PropertyDAO<>(PERSON.SECOND_SURNAME);}
	}

	public static SelectConditionStep<Record> select(AONContext ctx, PersonFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(PERSON)
				.join(DOMAIN).on(DOMAIN.ID.eq(PERSON.DOMAIN))
				.join(REGISTRY).on(REGISTRY.ID.eq(PERSON.REGISTRY))
				.where(PERSON_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<Person> getStream(AONContext ctx, PersonFilter filter){	
		return select(ctx, filter).fetch().stream().map(new PersonFiller());
	}
	
	public static Stream<Person> getStream(AONContext ctx, PersonFilter filter, Integer page, Integer perPage){	
		return select(ctx, filter).limit(perPage).offset(perPage * (page -1)).fetch().stream().map(new PersonFiller());
	}
	
	public static LinkedList<Person> getList(AONContext ctx, PersonFilter filter){	
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Person> getList(AONContext ctx, PersonFilter filter, Integer page, Integer perPage){	
		return getStream(ctx, filter, page, perPage).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Person get(AONContext ctx, PersonFilter filter) {
		ctx.checkRead();
		return select(ctx, filter).limit(1).stream().map(new PersonFiller()).findFirst().orElse(new Person());
	}
	
	
	public static Person save(AONContext ctx, Person person) {
		ctx.checkWrite();
		Person aux = new Person();
		if(person.getId() != null) {
			 aux = get(ctx, f -> f.getIdProperty().eq(person.getId()));
		}
		return person.getId()!=null && aux.getId() != null 
			? update(ctx, person) : insert(ctx, person);
	}
	
	public static Person insert(AONContext ctx, Person person) {
		ctx.checkWrite();
		ctx.getDslContext().insertInto(PERSON)
		.set(PERSON.REGISTRY, person.getId())
		.set(PERSON.DOMAIN, person.getDomain().getId())
		.set(PERSON.BIRTH_DATE,  converDateSql(person.getBirthDate()) )
		.set(PERSON.GENDER, AonEnumUtils.getByte( person.getGender() ))
		.set(PERSON.MARITAL_STATUS, AonEnumUtils.getByte( person.getMaritalStatus() ) )
		.set(PERSON.SOCIAL_SECURITY_NUM, person.getSocialSecurityNum())
		.set(PERSON.NAME, person.getFirstName())
		.set(PERSON.FIRST_SURNAME, person.getFirstSurname())
		.set(PERSON.SECOND_SURNAME, person.getSecondSurname())
		.execute();
		ctx.log().debug("INSERT PERSON id: {0}", person.getId());		
		return person;
	}
	
	public static Person update(AONContext ctx, Person person) {
		ctx.checkWrite();
		ctx.getDslContext().update(PERSON)
			.set(PERSON.BIRTH_DATE, converDateSql(person.getBirthDate()) )
			.set(PERSON.GENDER, AonEnumUtils.getByte( person.getGender() )  )
			.set(PERSON.MARITAL_STATUS, AonEnumUtils.getByte( person.getMaritalStatus()))
			.set(PERSON.SOCIAL_SECURITY_NUM, person.getSocialSecurityNum())
			.set(PERSON.NAME, person.getFirstName())
			.set(PERSON.FIRST_SURNAME, person.getFirstSurname())
			.set(PERSON.SECOND_SURNAME, person.getSecondSurname())
			.where(PERSON.REGISTRY.eq(person.getId()))
			.execute();
		ctx.log().debug("UPDATE PERSON id: {0}. ({1} rows)", person.getId());		
		return person;
	}

	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f -> f.getIdProperty().eq(id));
		ctx.log().debug("DELETE PERSON id:" + id);
	}
	
	private static void delete(AONContext ctx, PersonFilter filter) {
		ctx.getDslContext()
			.delete(PERSON)
			.where(PERSON_PROPERTIES.getConditions(filter))
			.execute();
	}
	

	public static class PersonFiller extends Filler implements Function<Record, Person> {
		
		public Person apply(Record r) {
			return build(r, REGISTRY);
		}
		
		public static Person build(Record r, com.esferalia.aon.jooq.tables.Registry registry) {
			if(registry == null) registry = REGISTRY;
			Person person = new Person()
					.copy(RegistryFiller.build(r, registry))
					.setGender(Gender.safeValueOf(r.getValue(PERSON.GENDER)))
					.setBirthDate( converDateSql(r.getValue(PERSON.BIRTH_DATE)) )
					.setMaritalStatus(MaritalStatus.safeValueOf(r.getValue(PERSON.MARITAL_STATUS)))
					.setSocialSecurityNum(r.getValue(PERSON.SOCIAL_SECURITY_NUM))
					.setFirstName(r.getValue(PERSON.NAME))
					.setFirstSurname(r.getValue(PERSON.FIRST_SURNAME))
					.setSecondSurname(r.getValue(PERSON.SECOND_SURNAME));
			person.setName(r.getValue(PERSON.NAME));
			return person;
		}
		
	}
	
	private static Date converDateSql(java.util.Date date) {
	    return date != null ? new Date(date.getTime()) : null;
	}
}

