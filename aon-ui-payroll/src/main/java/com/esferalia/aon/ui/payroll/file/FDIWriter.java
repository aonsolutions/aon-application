package com.esferalia.aon.ui.payroll.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.esferalia.aon.file.payroll.fdi.FDI;
import com.esferalia.aon.file.payroll.fdi.data.EMP;
import com.esferalia.aon.file.payroll.fdi.data.ETI;
import com.esferalia.aon.payroll.core.IActividad;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.IEmpresa;
import com.esferalia.aon.payroll.core.enumeration.Regimen;
import com.esferalia.aon.payroll.core.it.IParteIT;

public class FDIWriter {
	
	private ETI eti;
	
	public FileOutput createFDI(List<IParteIT> partes) throws ManagerBeanException {
		try {
			ETI eti = createETIRecord( partes );
			File file = File.createTempFile("XXXXXXXX", ".FDI");
			FileFiller fdi = new FDI(eti, file.getAbsolutePath());
			FileOutput output = new FileOutput();
			output.setFile(file);
			output.setErrors(fdi.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}

	private ETI createETIRecord( List<IParteIT> partes ) {
		ETI eti = new ETI();
		for (IParteIT parte: partes) {
			IEmpleado empleado = parte.getEmpleado();
			IEmpresa empresa = empleado.getEmpresa();
			IActividad actividad = empleado.getActividad();
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
			// Provincia emprccc.
			cccss += "12";
			// Número emprccc.
			cccss += "123";
			emp.setCodigoCuentaCotizacionSeguridadSocial(cccss);
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
