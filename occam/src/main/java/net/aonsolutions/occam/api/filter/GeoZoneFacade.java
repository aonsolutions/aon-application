package net.aonsolutions.occam.api.filter;

import java.util.LinkedList;
import java.util.stream.Stream;

import net.aonsolutions.occam.api.Filter;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.api.config.Geozone;

public class GeoZoneFacade {
	
	private GeoZoneFacade() {
		
	}

	@FunctionalInterface
	public interface GeoZoneFilter {
		Filter filter(GeoZoneFilters properties);
	}

	public interface GeoZoneFilters {
		Property<Integer> withId();
		Property<Integer> withDomain();
		Property<String> withName();
		Property<String> withCode();
		Property<Byte> withSystem();
	}
	
	public interface GeoZoneBuilder<T> {
		GeoZoneBuilder<T> limit(int offset, int rows);
		T build();
	}

	public abstract static class CompositeGeoZoneBuilder<T> implements GeoZoneBuilder<T> {
		private LinkedList<GeoZoneBuilder<?>> builders = new LinkedList<>();
		
		public CompositeGeoZoneBuilder<T> addBuilder(GeoZoneBuilder<?> builder) {
			builders.add(builder);
			return this;
		}
		
		@Override
		public GeoZoneBuilder<T> limit(int offset, int rows){
			builders.stream().forEach( b -> b.limit(offset,rows));
			return this;
		}
	}

	@FunctionalInterface
	public interface GeoZoneBuilderFactory {
		public GeoZoneBuilder<Stream<Geozone>> create( GeoZoneBuilder<Stream<Geozone>> builder );
	}
}
