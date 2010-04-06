package com.esferalia.aon.payroll.core.empleado;

public class EmpleadoDAOFactory {

	private static EmpleadoDAOFactory instance;
	private IEmpleadoDAO empleadoDAO;
	private boolean configured;
	
	private EmpleadoDAOFactory() {

	}

	public static EmpleadoDAOFactory getInstance() {
		if (instance == null) {
			instance = new EmpleadoDAOFactory();
		}
		return instance;
	}

	public IEmpleadoDAO getEmpleadoDAO() {
		if (empleadoDAO == null) {
			throw new IllegalStateException("No hay un IEmpleadoDAO registrado.");
		}
		if (! configured ) {
			empleadoDAO.configure();
			configured = true;
		}		
		return empleadoDAO;
	}

	public static void register(IEmpleadoDAO empleadoDAO) {
		EmpleadoDAOFactory.getInstance().setEmpleadoDAO(empleadoDAO);
	}

	public void setEmpleadoDAO(IEmpleadoDAO empleadoDAO) {
		this.empleadoDAO = empleadoDAO;
	}
}
