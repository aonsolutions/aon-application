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

	public interface UserBuilder<T> extends AonBuilder<T> {
		public UserBuilder<T> limit(int offest, int rows);
		public UserBuilder<T> full();
	}

	@FunctionalInterface
	public interface UserBuilderFactory<T> {
		public UserBuilder<T> create( UserBuilder<T> builder );
	}

}
