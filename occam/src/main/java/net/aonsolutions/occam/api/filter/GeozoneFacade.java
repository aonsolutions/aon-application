package net.aonsolutions.occam.api.filter;

import java.util.LinkedList;
import java.util.stream.Stream;

import net.aonsolutions.occam.api.Filter;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.api.config.Geozone;

public class GeozoneFacade {
	
	private GeozoneFacade() {
		
	}

	@FunctionalInterface
	public interface GeozoneFilter {
		Filter filter(GeozoneFilters properties);
	}

	public interface GeozoneFilters {
		Property<Integer> withId();
		Property<Integer> withDomain();
		Property<String> withName();
		Property<String> withCode();
		Property<Byte> withSystem();
	}
	
	public interface GeozoneBuilder<T> {
		GeozoneBuilder<T> orderByCode();
		GeozoneBuilder<T> orderByName();
		GeozoneBuilder<T> orderByRandom();
		GeozoneBuilder<T> limit(int offset, int rows);
		T build();
	}

	public abstract static class CompositeGeozoneBuilder<T> implements GeozoneBuilder<T> {
		private LinkedList<GeozoneBuilder<?>> builders = new LinkedList<>();
		
		public CompositeGeozoneBuilder<T> addBuilder(GeozoneBuilder<?> builder) {
			builders.add(builder);
			return this;
		}
		@Override
		public GeozoneBuilder<T> orderByCode() {
			builders.stream().forEach( b -> b.orderByCode());
			return this;
		}
		
		@Override
		public GeozoneBuilder<T> orderByName() {
			builders.stream().forEach( b -> b.orderByName());
			return this;
		}
		
		@Override
		public GeozoneBuilder<T> orderByRandom() {
			builders.stream().forEach( b -> b.orderByRandom());
			return this;
		}

		@Override
		public GeozoneBuilder<T> limit(int offset, int rows){
			builders.stream().forEach( b -> b.limit(offset,rows));
			return this;
		}
	}

	@FunctionalInterface
	public interface GeozoneBuilderFactory {
		public GeozoneBuilder<Stream<Geozone>> create( GeozoneBuilder<Stream<Geozone>> builder );
	}
}
