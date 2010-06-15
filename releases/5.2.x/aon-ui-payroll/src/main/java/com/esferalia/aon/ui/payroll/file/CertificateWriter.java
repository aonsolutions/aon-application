package com.esferalia.aon.ui.payroll.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.file.format.output.FileOutput;
import com.esferalia.aon.file.payroll.certificate.Certificate;
import com.esferalia.aon.file.payroll.certificate.data.CuentaCotizacion;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresa;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresaDetalle;

public class CertificateWriter {
	
	private Certificate certificate;
	
	public Certificate getCertificate() {
		return certificate;
	}

	public void setCertificate(Certificate certificate) {
		this.certificate = certificate;
	}

	public FileOutput createCertificate(IRemesaCertificadoEmpresa remesa, List<IRemesaCertificadoEmpresaDetalle> remesaDetail ) throws ManagerBeanException {
		try {
			CuentaCotizacion cuentaCotizacion = createCuentaCotizacionRecord(remesaDetail);
			File file = File.createTempFile("XXXXXXXX", ".XML");
			Certificate certificate = new Certificate();
			FileOutput output = new FileOutput();
			output.setFile(file);
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		} 
		catch (PayrollException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	private CuentaCotizacion createCuentaCotizacionRecord( List<IRemesaCertificadoEmpresaDetalle> remesa) throws PayrollException {
		CuentaCotizacion cuentaCotizacion = new CuentaCotizacion();
//		cuentaCotizacion.setClave(Integer.parseInt(usuario.getAutorizacion()));
//		for (IRemesaCertificadoEmpresaDetalle parte: remesa) {
//			EMP emp = createEMPrecord(parte);
//			eti.getEmpresas().add(emp);
//		}
		return cuentaCotizacion;
	}
	
	
	
	
}
