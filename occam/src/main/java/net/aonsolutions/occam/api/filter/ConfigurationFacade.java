package net.aonsolutions.occam.api.filter;

import java.util.LinkedList;

import net.aonsolutions.occam.api.config.Configuration;

public class ConfigurationFacade {
	
	private ConfigurationFacade() {
	}

	public interface ConfigurationBuilder {
		ConfigurationBuilder withAccountingConfiguration();
		Configuration build();
	}
	
	
	public abstract static class CompositeConfigurationBuilder implements ConfigurationBuilder {
		private LinkedList<ConfigurationBuilder> builders = new LinkedList<>();
		
		public CompositeConfigurationBuilder addBuilder(ConfigurationBuilder builder) {
			builders.add(builder);
			return this;
		}
		@Override
		public ConfigurationBuilder withAccountingConfiguration() {
			builders.stream().forEach( b -> b.withAccountingConfiguration());
			return this;
		}
		
	}

	@FunctionalInterface
	public interface ConfigurationBuilderFactory {
		public ConfigurationBuilder create( ConfigurationBuilder builder );
	}
	
}
