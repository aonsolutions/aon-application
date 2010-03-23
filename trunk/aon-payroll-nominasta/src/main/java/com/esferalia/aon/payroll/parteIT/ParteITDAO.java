package com.esferalia.aon.payroll.parteIT;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.esferalia.aon.core.util.DateUtils;
import com.esferalia.aon.payroll.ParteIT;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.ITrabajo;
import com.esferalia.aon.payroll.core.enumeration.CuentaCotizacion;
import com.esferalia.aon.payroll.core.enumeration.Periodicidad;
import com.esferalia.aon.payroll.core.enumeration.TipoContingencia;
import com.esferalia.aon.payroll.core.it.IParteIT;
import com.esferalia.aon.payroll.core.it.IParteITCalculator;
import com.esferalia.aon.payroll.core.it.IParteITDAO;
import com.esferalia.aon.payroll.core.it.ParteITCalculatorFactory;
import com.esferalia.aon.payroll.core.it.ParteITDAOFactory;
import com.esferalia.aon.payroll.core.it.ParteITParams;
import com.esferalia.aon.payroll.dao.IPayrollAlias;

public class ParteITDAO implements IParteITDAO {

	private static String EMP_ALIAS;
	private static String FEC_INI_ALIAS;

	static {
		ParteITDAOFactory.register(new ParteITDAO());

		try {
			IManagerBean parteITBean = BeanManager.getManagerBean(ParteIT.class);
			EMP_ALIAS = parteITBean.getFieldName(IPayrollAlias.PARTE_IT_EMPLEADO_ID);
			FEC_INI_ALIAS = parteITBean.getFieldName(IPayrollAlias.PARTE_IT_ID_FECHA_BAJA);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Criteria getCriteria(ParteITParams params) throws PayrollException {
		try {
			Criteria c = new Criteria();
			if (!StringUtils.isBlank(params.getEmpleadoID())) {
				c.addExpression(EMP_ALIAS, params.getEmpleadoID());
			}
			c.addOrder(FEC_INI_ALIAS,false);
			return c;
		} catch (ExpressionException e) {
			throw new PayrollException( e );
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IParteIT> getPartesEmpleado(IEmpleado empleado) throws PayrollException {
		try {
			if (empleado == null || empleado.getId() == null) {
				throw new IllegalArgumentException("Empleado no puede ser nulo.");
			}
			IManagerBean bean = BeanManager.getManagerBean(ParteIT.class);
			ParteITParams p = new ParteITParams();
			p.setEmpleadoID( Integer.toString(empleado.getId()) );
			List<?> list = bean.getList( getCriteria(p) );
			return (List<IParteIT>) list;
		} catch (ManagerBeanException e) {
			throw new PayrollException( e );
		}
	}

@Override
	public int validate(IParteIT parteIT) {
		// El empleado es un dato requerido.
		if (parteIT.getEmpleado() == null) {
			return 1;
		}

		// El empleado debe estar activo.
		if (parteIT.getEmpleado().getFechaFin() != null) {
			return 2;
		}

		// El empresa del empleado debe estar activa.
		if (!parteIT.getEmpleado().getEmpresa().isActive()) {
			return 3;
		}

		// La fecha de la baja es un dato requerido.
		if (parteIT.getFechaBaja() == null) {
			return 4;
		}

		// Si se está grabando una baja, uno de los datos, nº colegiado o CIAS
		// es requerido.
		if (StringUtils.isBlank(parteIT.getCiasBaja())
				&& StringUtils.isBlank(parteIT.getNumeroColegiadoBaja())) {
			return 5;
		}

		// Si se está grabando una alta, uno de los datos, nº colegiado o CIAS
		// es requerido.
		if (parteIT.getFechaAlta() != null
				&& StringUtils.isBlank(parteIT.getCiasAlta())
				&& StringUtils.isBlank(parteIT.getNumeroColegiadoAlta())) {
			return 6;
		}

		int valid = DateUtils.validateRange(parteIT.getFechaBaja(), parteIT.getFechaAlta(), 
				parteIT.getEmpleado().getFechaInicio(), parteIT.getEmpleado().getFechaFin());
		if (valid > 0) {
			if (valid == 1) return 4; //La fecha de la baja es un dato requerido.
			if (valid == 2) return 7; //La fecha de Alta es menor a la de Baja.
			if (valid == 3) return 8; //La fecha de Baja no estÃ¡ en el rango de fechas del empleado.
			if (valid == 4) return 9; //La fecha de Alta no estÃ¡ en el rango de fechas del empleado.
		}
		
		
		//No se genera parte de I.T. por tratarse de un Alto Cargo.
		if (parteIT.getEmpleado().getCuentaCotizacion() == CuentaCotizacion.ALTO_CARGO) {
			return 10;
		}
		
		//No se genera Parte de I.T. por Maternidad por tratarse de un Trabajador Mayor de 65 años y más de 35 años Cotizados.
		if (parteIT.getEmpleado().isMayor65() && parteIT.getTipoContingencia() == TipoContingencia.MATERNIDAD) {
			return 11;
		}
		
		//El el caso de recaída, debe indicar la primera I.T.
		if (parteIT.isRecaida() && parteIT.getParteITRecaida() == null) {
			return 12;
		}
		
		return 0;
	}
	
	@Override
	public void calculate(IParteIT parteIT,ITrabajo trabajo) throws PayrollException {
		ParteITCalculatorFactory factory = ParteITCalculatorFactory.getInstance();
		IParteITCalculator calculator = factory.getParteITCalculator( parteIT, trabajo );
		parteIT.setBaseRetribucionPeriodoAnterior( calculator.getBaseRetribucionPeriodoAnterior(parteIT) );
		parteIT.setDiasPeriodoAnterior(calculator.getDiasPeriodoAnterior(parteIT) );
		parteIT.setBaseReguladoraDiaria( calculator.getBaseReguladoraDiaria(parteIT) );
		parteIT.setBaseDiariaContingenciasComunes(calculator.getBaseDiariaContingenciasComunes(parteIT));
		parteIT.setBaseDiariaAccidentesTrabajo(calculator.getBaseDiariaAccidentesTrabajo(parteIT) );
		parteIT.setPrestacionDiaria60(calculator.getPrestacionDiaria60(parteIT));
		parteIT.setPrestacionDiaria75(calculator.getPrestacionDiaria75(parteIT));
	}
	
	@Override
	public IParteIT initialize(IEmpleado  empleado) throws PayrollException {
		if (empleado == null || empleado.getId() == null) {
			throw new IllegalArgumentException("El Empleado del parteIT no puede ser nulo.");
		}
		ParteIT parteIT = new ParteIT();
		parteIT.setEmpleado(empleado);
		List<IParteIT> list = getPartesEmpleado(parteIT.getEmpleado());
		if (list.size() > 0) {
			IParteIT ultimoParte = list.get(0);
			parteIT.setCiasBaja(ultimoParte.getCiasBaja());
			parteIT.setNumeroColegiadoBaja(ultimoParte.getNumeroColegiadoBaja());
			parteIT.setCiasAlta(ultimoParte.getCiasAlta());
			parteIT.setNumeroColegiadoAlta(ultimoParte.getNumeroColegiadoAlta());
			parteIT.setFechaBaja(ultimoParte.getFechaBaja());
			if (!ultimoParte.isAltaProcesada()) {
				parteIT.setFechaBaja(ultimoParte.getFechaBaja());
				parteIT.setBajaProcesada(ultimoParte.isBajaProcesada());
				parteIT.setFechaAlta(ultimoParte.getFechaAlta());
				parteIT.setAltaProcesada(ultimoParte.isAltaProcesada());
				parteIT.setProcesada(ultimoParte.isProcesada());
				parteIT.setTipoContingencia(ultimoParte.getTipoContingencia());
				parteIT.setRecaida(ultimoParte.isRecaida());
				parteIT.setParteITRecaida(ultimoParte.getParteITRecaida());
				parteIT.setProrrateoCotizacion(ultimoParte.getProrrateoCotizacion());
			}
		}
		return parteIT;
	}
}

/*
		Criteria allParteitCriteria = new Criteria();
		allParteitCriteria.addEqualExpression(getParteitBean().getFieldName(IPayrollAlias.PARTEIT_EMPRPER_CDG), ((Trabajador)getTo()).getCdg());
		allParteitCriteria.addOrder(getParteitBean().getFieldName(IPayrollAlias.PARTEIT_ID_FECINI), false);
		List<ITransferObject> pit = getParteitBean().getList(allParteitCriteria);
		if(pit.size()>0){
			String cias = ((Parteit)pit.get(pit.size()-1)).getCiasalt();
			String numcol = ((Parteit)pit.get(pit.size()-1)).getNumcolalt();
//			String ciasbaj = ((Parteit)pit.get(pit.size()-1)).getCiasbaj();
//			String numcolbaj = ((Parteit)pit.get(pit.size()-1)).getNumcolbaj();
//			recoge los datos del ultimo parte
//			getParteRen().setCias(cias);
//			getParteRen().setNumcol(numcol);
			
//			se busca el estado actual del trabajador
			Criteria bajasParteitCriteria = new Criteria();
			bajasParteitCriteria.addExpression(allParteitCriteria.getExpression());
			bajasParteitCriteria.addEqualExpression(getParteitBean().getFieldName(IPayrollAlias.PARTEIT_ALTPROC), false);
			bajasParteitCriteria.addOrder(getParteitBean().getFieldName(IPayrollAlias.PARTEIT_ID_FECINI), false);
			// se da por supuesto que solo hay un parte de baja activo, el primero y unico de la lista
			pit = getParteitBean().getList(bajasParteitCriteria);
			if(pit.size()<=0){
				trabajadorStatus = ItStatus.ALTA;
				getParteRen().setFecconf(Calendar.getInstance().getTime());
				getParteRen().setCias(cias);
				getParteRen().setNumcol(numcol);
				setRenovacion(false);
				setParteitList(getParteitBean().getList(allParteitCriteria));
			} else {
				trabajadorStatus = ItStatus.BAJA;
				getParteRen().setParteit((Parteit)pit.get(pit.size()-1));
				cias = ((Parteit)pit.get(pit.size()-1)).getCiasbaj();
				numcol = ((Parteit)pit.get(pit.size()-1)).getNumcolbaj();
				getParteRen().setCias(cias);
				getParteRen().setNumcol(numcol);
				searchNumRenovacion();
				setParteitList(pit);
				
				List<ITransferObject> lista = getParteitRenovations(getParteRen().getParteit());
				if(lista.size()>0){
					setParteconfList(lista);
				}

//				getParteRen().setFecconf(getParteRen().getParteit().getFeciniori().+3+((getNumParteRenovacion()-1*7))
				Calendar cal = new GregorianCalendar();
				cal.setTime(((Parteit)pit.get(0)).getId().getFecini());
				cal.add(Calendar.DAY_OF_YEAR, 3+(((getNumParteRenovacion()-1)*7)));
				getParteRen().setFecconf(cal.getTime());
			}
			
//			se busca si es un parte de recaida
			Criteria recaidasParteitCriteria = new Criteria();
			recaidasParteitCriteria.addExpression(allParteitCriteria.getExpression());
			recaidasParteitCriteria.addEqualExpression(getParteitBean().getFieldName(IPayrollAlias.PARTEIT_ALTPROC), true);
			pit = getParteitBean().getList(recaidasParteitCriteria);
			if(pit.size()>0 && getTrabajadorStatus().equals(ItStatus.ALTA)){
				searchRecaida((Parteit)pit.get(0));
			}
		} else {
			trabajadorStatus = ItStatus.ALTA;
		}
*/



