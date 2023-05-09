package net.aonsolutions.occam.api.filter;

import java.io.Serializable;
import java.util.stream.Stream;

import net.aonsolutions.occam.api.Filter;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.api.config.User;
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

	public interface UserBuilder<T>  {
		public UserBuilder<T> limit(int offest, int rows);
		public UserBuilder<T> full();
		public T build();
	}

	@FunctionalInterface
	public interface UserBuilderFactory {
		public UserBuilder<Stream<User>> create( UserBuilder<Stream<User>> builder );
	}
	

}
