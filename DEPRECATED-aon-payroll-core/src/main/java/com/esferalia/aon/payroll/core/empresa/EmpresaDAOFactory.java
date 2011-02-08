package com.esferalia.aon.payroll.core.empresa;

public class EmpresaDAOFactory {

	private static EmpresaDAOFactory instance;
	private IEmpresaDAO empresaDAO;
	private boolean configured;
	
	private EmpresaDAOFactory() {

	}

	public static EmpresaDAOFactory getInstance() {
		if (instance == null) {
			instance = new EmpresaDAOFactory();
		}
		return instance;
	}

	public IEmpresaDAO getEmpresaDAO() {
		if (empresaDAO == null) {
			throw new IllegalStateException("No hay un IEmpresaDAO registrado.");
		}
		if (! configured ) {
			empresaDAO.configure();
			configured = true;
		}		
		return empresaDAO;
	}

	public static void register(IEmpresaDAO empresaDAO) {
		EmpresaDAOFactory.getInstance().setEmpresaDAO(empresaDAO);
	}

	public void setEmpresaDAO(IEmpresaDAO empresaDAO) {
		this.empresaDAO = empresaDAO;
	}
}
