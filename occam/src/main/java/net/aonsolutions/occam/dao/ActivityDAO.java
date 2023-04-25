package net.aonsolutions.occam.dao;

import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Iae.IAE;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jooq.Record;

import net.aonsolutions.occam.api.config.Activity;

public class ActivityDAO {
	
	private ActivityDAO() {

	}
	
	static class ActivityFiller extends Filler<Optional<Activity>>  implements Function<Record,Optional<Activity>> {

		@Override
		public Optional<Activity> apply(Record r) {
			return map(r, () -> Optional.of(new Activity()));
		}
		
		@Override
		Optional<Activity> map(Record r, Supplier<Optional<Activity>> supplier) {
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
