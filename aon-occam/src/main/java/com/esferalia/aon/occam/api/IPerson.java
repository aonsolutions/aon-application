package com.esferalia.aon.occam.api;

import java.util.stream.Stream;
import com.esferalia.aon.occam.api.model.Filter.PersonFilter;
import com.esferalia.aon.occam.api.model.Person;

public interface IPerson {
	
	public Stream<Person> getPersonStream(AONContext ctx, PersonFilter filter);
	public Person getPerson(AONContext ctx, PersonFilter filter);
	public Person savePerson(AONContext ctx, Person person);
	public void deletePerson(AONContext ctx, Integer id);
	
}
