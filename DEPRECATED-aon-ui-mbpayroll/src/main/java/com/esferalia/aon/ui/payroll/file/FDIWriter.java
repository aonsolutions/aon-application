package com.esferalia.aon.ui.payroll.file;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.esferalia.aon.file.payroll.fdi.FDI;
import com.esferalia.aon.file.payroll.fdi.data.DEC;
import com.esferalia.aon.file.payroll.fdi.data.DIT;
import com.esferalia.aon.file.payroll.fdi.data.EMP;
import com.esferalia.aon.file.payroll.fdi.data.ETI;
import com.esferalia.aon.file.payroll.fdi.data.ODP;
import com.esferalia.aon.file.payroll.fdi.data.TRA;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IActividad;
import com.esferalia.aon.payroll.core.IActividadCCC;
import com.esferalia.aon.payroll.core.IContrato;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.IEmpresa;
import com.esferalia.aon.payroll.core.IUsuario;
import com.esferalia.aon.payroll.core.commons.CommonsPayrollDAOFactory;
import com.esferalia.aon.payroll.core.commons.ICommonsPayrollDAO;
import com.esferalia.aon.payroll.core.empresa.EmpresaDAOFactory;
import com.esferalia.aon.payroll.core.empresa.IEmpresaDAO;
import com.esferalia.aon.payroll.core.enumeration.Regimen;
import com.esferalia.aon.payroll.core.enumeration.TipoContingencia;
import com.esferalia.aon.payroll.core.enumeration.TipoContrato;
import com.esferalia.aon.payroll.core.enumeration.TipoOperacionIT;
import com.esferalia.aon.payroll.core.it.IParteITDAO;
import com.esferalia.aon.payroll.core.it.ParteITDAOFactory;
import com.esferalia.aon.payroll.core.remesa.IRemesaParteIT;

public class FDIWriter {
	
	private ICommonsPayrollDAO commonsPayrollDAO;
	private IEmpresaDAO empresaDAO;
	private IParteITDAO parteITDAO;
	private ETI eti;
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
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

	private IParteITDAO getParteITDAO() {
		if (parteITDAO == null) {
			parteITDAO = ParteITDAOFactory.getInstance().getParteITDAO();
		}
		return parteITDAO;
	}

	public FileOutput createFDI(List<IRemesaParteIT> partes, String loggedUser ) throws ManagerBeanException {
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

	private ETI createETIRecord( List<IRemesaParteIT> partes, String loggedUser ) throws PayrollException {
		ETI eti = new ETI();
		IUsuario usuario = getCommonsPayrollDAO().getUsuarioActivo(loggedUser);
		eti.setClave(Integer.parseInt(usuario.getAutorizacion()));
		for (IRemesaParteIT parte: partes) {
			EMP emp = createEMPrecord(parte);
			eti.getEmpresas().add(emp);
		}
		setEti( eti );
		return getEti();
	}
	
	private EMP createEMPrecord(IRemesaParteIT remesable) throws PayrollException {
//		IParteIT parte = remesable.getParteIT();
		IEmpleado empleado = remesable.getEmpleado();
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
		TRA tra = createTRARecord(remesable);
		emp.getTrabajadores().add(tra);
		return emp;
	}
	
	private TRA createTRARecord(IRemesaParteIT remesable) throws PayrollException {
//		IParteIT parte = remesable.getParteIT();
		IEmpleado empleado = remesable.getEmpleado();
		TRA tra = new TRA();
		tra.setNumeroAfiliacion( empleado.getPersona().getNumSS() );
		String tipo = empleado.getPersona().getRegistry().getDocument().getTipo(); 
		String pais = empleado.getPersona().getRegistry().getDocument().getPais();
		String doc = empleado.getPersona().getRegistry().getDocument().getValue();
		String ipf = StringUtils.isBlank(tipo)?"9":tipo;
		ipf += StringUtils.isBlank(pais)?"   ":pais; 
		ipf += doc;
		tra.setIpf(ipf);
		DIT dit = createDITRecord( remesable );
		List<DIT> dits = new LinkedList<DIT>();
		dits.add(dit);
		tra.setDatosIT(dits);
		return tra;
	}

	private DIT createDITRecord(IRemesaParteIT remesable) throws PayrollException {
//		IParteIT parte = remesable.getParteIT();
		DIT dit = new DIT();
		// si es parte de confirmacion 
		if (remesable.getTipoOperacionIT() == TipoOperacionIT.CONFIRMACION) {
			dit.setAccion("PC ");
//			IParteConfirmacionIT conf =  remesable.getConfirmacionIT();
			dit.setNumeroColegiado(remesable.getNumeroColegiado());
			dit.setCias(remesable.getCias());
			ODP odp = createODPRecord(remesable);
			dit.setOdp(odp);
		} else {
			// si es parte de baja
			if (remesable.getTipoOperacionIT() == TipoOperacionIT.BAJA) {
				dit.setAccion("PB ");
				dit.setNumeroColegiado(remesable.getNumeroColegiado());
				dit.setCias(remesable.getCias());
				DEC dec = createDECRecord(remesable);
				dit.setDec(dec);
			}
			// si es parte de alta
			if (remesable.getTipoOperacionIT() == TipoOperacionIT.ALTA) {
				dit.setAccion("PA ");
				dit.setNumeroColegiado(remesable.getNumeroColegiado());
				dit.setCias(remesable.getCias());
				// *************************************
				// TODO Dar soporte a la causa del alta. 
				// *************************************
			}
		}
		if (dit.getNumeroColegiado() != null) {
			String prov = dit.getNumeroColegiado().substring(0, 2);
			dit.setNumeroColegiado(prov + dit.getNumeroColegiado());
		}
		
		dit.setRecaida( remesable.isRecaida()?"S":"N");
			
		if (remesable.getTipoContingencia() == TipoContingencia.ENFERMEDAD_COMUN) {
			dit.setContingencia("1");
		} else if (remesable.getTipoContingencia() == TipoContingencia.ACCIDENTE_NO_LABORAL) {
			dit.setContingencia("2");
		} else if (remesable.getTipoContingencia() == TipoContingencia.ACCIDENTE_LABORAL) { 
			dit.setContingencia("3");
			dit.setFechaATEP( Integer.parseInt( dateFormatter.format( remesable.getFechaBaja() )) );
		}
		
		dit.setFechaBaja(  Integer.parseInt( dateFormatter.format( remesable.getFechaBaja() )) );
		if (remesable.getTipoOperacionIT() == TipoOperacionIT.ALTA) {
			dit.setFechaAlta(  Integer.parseInt( dateFormatter.format( remesable.getFechaParte() )) );
		}
		return dit;
	}

	private ODP createODPRecord(IRemesaParteIT confirmacionIT) {
		ODP odp = new ODP();
		odp.setFecha(Integer.parseInt( dateFormatter.format(confirmacionIT.getFechaParte())));
		odp.setNumero(confirmacionIT.getNumero());
		odp.setEntidadAseguradora(0);
		odp.setFechaCambioEntidad(0);
		return odp;
	}

	private DEC createDECRecord(IRemesaParteIT parte) throws PayrollException {
		DEC dec = new DEC();
		IContrato contrato = getParteITDAO().getContrato(parte);
		if (contrato.getTipoContrato() == TipoContrato.TIEMPO_COMPLETO) {
			dec.setBaseCotizacion( parte.getBaseRetribucionPeriodoAnterior() );
			dec.setDiasCotizados( parte.getDiasPeriodoAnterior() );
		} else {
			dec.setSumaBasesCotizacion(parte.getBaseRetribucionPeriodoAnterior() );
			dec.setSumaDiasCotizados( parte.getDiasPeriodoAnterior() );
		}
		dec.setCotizacionAnteriorHorasExtras(0.0);
		dec.setCotizacionAnteriorOtros(0.0);
		return dec;
	}

	public ETI getEti() {
		return eti;
	}
	public void setEti(ETI eti) {
		this.eti = eti;
	}
	
}
