package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.JobType.JOB_TYPE;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.JobTypeFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.JobTypeProperties;
import com.esferalia.aon.occam.api.model.task.JobType;

public class JobTypeDAO {

	private JobTypeDAO() {
		  throw new IllegalStateException("Utility class");
    }
	
	private static final JobTypePropertiesDAO JOB_TYPE_PROPERTIES = new JobTypePropertiesDAO();

	protected static class JobTypePropertiesDAO implements JobTypeProperties {
		protected Select<Record> build(SelectJoinStep<Record> select,  JobTypeFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(JobTypeFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(JOB_TYPE.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(JOB_TYPE.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(JOB_TYPE.DESCRIPTION);}
	}
	
	public static Stream<JobType> getStream(AONContext ctx, JobTypeFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select()
			.from(JOB_TYPE)
			.where(JOB_TYPE_PROPERTIES.getConditions(filter))
			.orderBy(JOB_TYPE.ID.desc())
			.fetch().stream().map(new JobTypeFiller());
	}
	
	public static JobType save(AONContext ctx, JobType jobType) {
		if(jobType.getId() != null && jobType.getId()>0) {
			update(ctx, jobType);
		} else {
			insert(ctx, jobType);
		}

		return jobType;
	}
	
	private static JobType insert(AONContext ctx, JobType jobType) {
		ctx.checkWrite();
		Integer id = ctx.getDslContext()
				.insertInto(JOB_TYPE)
				.set(JOB_TYPE.DOMAIN, jobType.getDomain().getId())
				.set(JOB_TYPE.DESCRIPTION, jobType.getDescription())
			.returning(JOB_TYPE.ID).fetchOne().getId();
		ctx.log().debug("INSERT JOB_TYPE id: " + id);		
		return jobType.setId(id);
	}
	
	private static JobType update(AONContext ctx, JobType jobType) {
		ctx.checkWrite();
		ctx.getDslContext()
			.update(JOB_TYPE)
			.set(JOB_TYPE.DESCRIPTION, jobType.getDescription())
			.where(JOB_TYPE.ID.eq(jobType.getId()))
			.execute();		
		ctx.log().debug("UPDATE JOB_TYPE id: " + jobType.getId());		
		return jobType;
	}
	
	
	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		ctx.getDslContext().delete(JOB_TYPE).where(JOB_TYPE.ID.eq(id)).execute();	
		ctx.log().debug("DELETE JOB_TYPE id: " + id);		
	}
	
	public static JobType get(AONContext ctx, JobTypeFilter filter) {
		ctx.checkRead();
		return  ctx.getDslContext()
				.select()
				.from(JOB_TYPE)
				.where(JOB_TYPE_PROPERTIES.getConditions(filter))
				.stream()
				.map( new JobTypeFiller() )
				.findFirst()
				.orElse(null);
	}

	public static class JobTypeFiller  implements Function<Record, JobType> {
		@Override
		public JobType apply(Record r) {
			return new JobType()
					.setId(r.getValue(JOB_TYPE.ID))
					.setDomain(new Domain().setId(r.getValue(JOB_TYPE.DOMAIN)))
					.setDescription(r.getValue(JOB_TYPE.DESCRIPTION));
		}
	}
	
}
