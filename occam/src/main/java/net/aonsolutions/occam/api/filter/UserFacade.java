package net.aonsolutions.occam.api.filter;

import java.io.Serializable;

import net.aonsolutions.occam.api.Filter;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.api.filter.AonFacade.AonBuilder;
public interface UserFacade extends Serializable{

	@FunctionalInterface
	public interface UserFilter {
		Filter filter(UserFilters properties);
	}

	public interface UserFilters {
		Property<Integer> withId();
		Property<Integer> withDomain();
		Property<String> withName();
		Property<String> withLogin();
		Property<Byte> withActive();
	}

	@FunctionalInterface
	public interface UserBuilderFactory<T> {
		public AonBuilder<T> create( AonBuilder<T> builder );
	}

}
