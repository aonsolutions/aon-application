package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IPerson;
import com.esferalia.aon.occam.api.model.Filter.PersonFilter;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.impl.jooq.dao.PersonDAO;

public class PersonImpl implements IPerson {
	
	@Override
	public Person savePerson(AONContext ctx, Person lc) {
		return ctx.getDslContext().transactionResult(configuration -> PersonDAO.save(ctx, lc));
	}
	
	
	@Override
	public Person getPerson(AONContext ctx, PersonFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> PersonDAO.get(ctx, filter));
	}


	@Override
	public Stream<Person> getPersonStream(AONContext ctx, PersonFilter filter) {
		return  ctx.getDslContext().transactionResult(
				configuration -> PersonDAO.getStream(ctx, filter));
	}

	@Override
	public void deletePerson(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(
				configuration -> PersonDAO.delete(ctx, id)
		);
	}
	
}
