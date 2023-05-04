package net.aonsolutions.occam.test;

import java.sql.Connection;

import net.aonsolutions.occam.api.AONContext;

public class AONTestContext extends AONContext {
	
	/**
	 * ESTE MÉTODO NO SE DEBE USAR EN LA APLICACION.
	 * Se utiliza para test y clases de utilidad.
	 * 
	 * @param connection
	 */
	public AONTestContext(Connection connection) {
		super(connection,null,null);
	}
	
}
