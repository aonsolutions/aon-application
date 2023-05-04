package net.aonsolutions.occam.api.filter;

import java.io.Serializable;
public interface AonFacade extends Serializable{

	public interface AonBuilder<T> {
		public AonBuilder<T> limit(int offest, int rows);
		public T build();
	}
	
}
