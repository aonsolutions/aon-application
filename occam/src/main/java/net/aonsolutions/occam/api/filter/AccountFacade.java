package net.aonsolutions.occam.api.filter;

import java.util.LinkedList;
import java.util.stream.Stream;

import net.aonsolutions.occam.api.Filter;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.api.accounting.Account;

public class AccountFacade {
	
	private AccountFacade() {
		
	}

	@FunctionalInterface
	public interface AccountFilter {
		Filter filter(AccountFilters properties);
	}

	public interface AccountFilters {
		Property<Integer> withId();
		Property<Integer> withDomain();
		Property<String> withCode();
		Property<String> withDescription();
		Property<String> withAlias();
		Property<Byte> withActive();
	}
	
	public interface AccountBuilder<T> {
		AccountBuilder<T> limit(int offset, int rows);
		T build();
	}

	public abstract static class CompositeAccountBuilder<T> implements AccountBuilder<T> {
		private LinkedList<AccountBuilder<?>> builders = new LinkedList<>();
		
		public CompositeAccountBuilder<T> addBuilder(AccountBuilder<?> builder) {
			builders.add(builder);
			return this;
		}
		
		@Override
		public AccountBuilder<T> limit(int offset, int rows){
			builders.stream().forEach( b -> b.limit(offset,rows));
			return this;
		}
	}

	@FunctionalInterface
	public interface AccountBuilderFactory {
		public AccountBuilder<Stream<Account>> create( AccountBuilder<Stream<Account>> builder );
	}
}
