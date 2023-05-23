package net.aonsolutions.occam.api.filter;

import java.util.LinkedList;
import java.util.stream.Stream;

import net.aonsolutions.occam.api.config.Domain;

public class ConfigurationFacade {
	
	private ConfigurationFacade() {
	}

	public interface ConfigurationBuilder<T> {
		ConfigurationBuilder<T> withAccounting();
		T build();
	}
	
	
	public abstract static class CompositeConfigurationBuilder<T> implements ConfigurationBuilder<T> {
		private LinkedList<ConfigurationBuilder<?>> builders = new LinkedList<>();
		
		public CompositeConfigurationBuilder<T> addBuilder(ConfigurationBuilder<?> builder) {
			builders.add(builder);
			return this;
		}
		@Override
		public ConfigurationBuilder<T> withAccounting() {
			builders.stream().forEach( b -> b.withAccounting());
			return this;
		}
	}

	@FunctionalInterface
	public interface ConfigurationBuilderFactory {
		public ConfigurationBuilder<Stream<Domain>> create( ConfigurationBuilder<Stream<Domain>> builder );
	}
	
}
