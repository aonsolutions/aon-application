package net.aonsolutions.occam.dao;

import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.util.function.Function;
import java.util.function.Supplier;

import org.jooq.Record;

import net.aonsolutions.occam.api.config.Scope;

public class ScopeDAO {
	
	private ScopeDAO() {
		
	}
	
	public static class ScopeFiller  implements Function<Record,Scope> {
		
		@Override
		public Scope apply(Record r) {
			return map(r, Scope::new);
		}
		
		Scope map(Record r, Supplier<Scope> supplier) {
			return supplier.get()
				.setId(FillerUtils.getValue(r,SCOPE.ID))
				.setDomain(FillerUtils.getValue(r,SCOPE.DOMAIN))
				.setDescription(FillerUtils.getValue(r,SCOPE.DESCRIPTION))
				.setDirty(false)
				;	
		}
	}
	
	
	
}
