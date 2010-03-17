package com.esferalia.aon.payroll.core.empleado;

public class EmpleadoDAOFactory {

	private static EmpleadoDAOFactory instance;
	private IEmpleadoDAO empleadoDAO;

	private EmpleadoDAOFactory() {

	}

	public static EmpleadoDAOFactory getInstance() {
		if (instance == null) {
			instance = new EmpleadoDAOFactory();
		}
		return instance;
	}

	public IEmpleadoDAO getEmpleadoDAO() {
		return empleadoDAO;
	}

	public static void register(IEmpleadoDAO empleadoDAO) {
		EmpleadoDAOFactory.getInstance().setEmpleadoDAO(empleadoDAO);
	}

	public void setEmpleadoDAO(IEmpleadoDAO empleadoDAO) {
		this.empleadoDAO = empleadoDAO;
	}
}
