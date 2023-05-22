package net.aonsolutions.occam.dao;

import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Iae.IAE;

import java.util.Optional;
import java.util.function.Function;

import org.jooq.Record;

import net.aonsolutions.occam.api.config.Activity;

public class ActivityDAO {
	
	private ActivityDAO() {

	}
	
	static class ActivityFiller implements Function<Record,Optional<Activity>> {

		@Override
		public Optional<Activity> apply(Record r) {
			Activity act = new Activity()
				.setId(r.getValue(ENTERPRISE_ACTIVITY.ID))
				.setDomain(r.getValue(ENTERPRISE_ACTIVITY.DOMAIN))
				.setDescription(r.getValue(ENTERPRISE_ACTIVITY.DESCRIPTION))
				.setEpigraph(r.getValue(IAE.EPIGRAPH))
			;
			return act.isDirty()?Optional.of(act.setDirty(false)):Optional.empty();
		}

	}
	
}
