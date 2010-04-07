package com.esferalia.aon.ui.payroll.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.esferalia.aon.file.payroll.fdi.FDI;
import com.esferalia.aon.file.payroll.fdi.data.EMP;
import com.esferalia.aon.file.payroll.fdi.data.ETI;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IActividad;
import com.esferalia.aon.payroll.core.IActividadCCC;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.IEmpresa;
import com.esferalia.aon.payroll.core.IUsuario;
import com.esferalia.aon.payroll.core.commons.CommonsPayrollDAOFactory;
import com.esferalia.aon.payroll.core.commons.ICommonsPayrollDAO;
import com.esferalia.aon.payroll.core.empresa.EmpresaDAOFactory;
import com.esferalia.aon.payroll.core.empresa.IEmpresaDAO;
import com.esferalia.aon.payroll.core.enumeration.Regimen;
import com.esferalia.aon.payroll.core.it.IParteIT;

public class FDIWriter {
	
	private ICommonsPayrollDAO commonsPayrollDAO;
	private IEmpresaDAO empresaDAO;
	private ETI eti;
	
	private ICommonsPayrollDAO getCommonsPayrollDAO() {
		if (commonsPayrollDAO == null) {
			commonsPayrollDAO = CommonsPayrollDAOFactory.getInstance().getCommonsPayrollDAO();
		}
		return commonsPayrollDAO;
	}

	private IEmpresaDAO getEmpresaDAO() {
		if (empresaDAO == null) {
			empresaDAO = EmpresaDAOFactory.getInstance().getEmpresaDAO();
		}
		return empresaDAO;
	}

	public FileOutput createFDI(List<IParteIT> partes, String loggedUser ) throws ManagerBeanException {
		try {
			ETI eti = createETIRecord( partes, loggedUser );
			File file = File.createTempFile("XXXXXXXX", ".FDI");
			FileFiller fdi = new FDI(eti, file.getAbsolutePath());
			FileOutput output = new FileOutput();
			output.setFile(file);
			output.setErrors(fdi.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		} catch (PayrollException e) {
			throw new ManagerBeanException(e);
		}
	}

	private ETI createETIRecord( List<IParteIT> partes, String loggedUser ) throws PayrollException {
		ETI eti = new ETI();
		IUsuario usuario = getCommonsPayrollDAO().getUsuarioActivo(loggedUser);
		eti.setClave(Integer.parseInt(usuario.getAutorizacion()));
		for (IParteIT parte: partes) {
			IEmpleado empleado = parte.getEmpleado();
			IActividad actividad = empleado.getActividad();
			IEmpresa empresa = actividad.getEmpresa();
			EMP emp = new EMP();
			String cccss = null;
			if (actividad.getRegimen() == Regimen.AGRARIO) {
				cccss = "0613";
			} else if (actividad.getRegimen() == Regimen.GENERAL) {
				cccss = "0111";
			} else if (actividad.getRegimen() == Regimen.ARTISTAS) {
				cccss = "0112";
			} else if (actividad.getRegimen() == Regimen.MARITIMO) {
				cccss = "0811";
			} else {
				cccss = "    ";
			}
			IActividadCCC acc = getEmpresaDAO().getActividadCCC(actividad, empleado.getCuentaCotizacion());
			String provincia = null;
			String numero = null;
			if (acc != null) {
				String ccc = acc.getDescripcion();
				provincia = StringUtils.substring(ccc, 0,2);
				numero = StringUtils.substring(ccc, 2,12);
			}
			cccss += provincia;
			cccss += numero;
			emp.setCodigoCuentaCotizacionSeguridadSocial(cccss);
			String tipo = empresa.getRegistry().getDocument().getTipo();
			if (StringUtils.isBlank(tipo)) {
				tipo = "9";
			}
			String pais = empresa.getRegistry().getDocument().getPais();			
			if (StringUtils.isBlank(pais)) {
				pais = "   ";
			}
			emp.setTipo(tipo);
			emp.setPais(pais);
			emp.setNumero(empresa.getRegistry().getDocument().getValue());
			emp.setCodigoCuentaCotizacionPrincipal(cccss);
			
			eti.getEmpresas().add(emp);
		}
		setEti( eti );
		return getEti();
	}
	
	public ETI getEti() {
		return eti;
	}
	public void setEti(ETI eti) {
		this.eti = eti;
	}
	
}
