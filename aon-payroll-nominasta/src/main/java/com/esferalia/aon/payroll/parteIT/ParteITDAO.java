package com.esferalia.aon.payroll.parteIT;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.core.util.DateUtils;
import com.esferalia.aon.payroll.ConfirmacionParteITPK;
import com.esferalia.aon.payroll.Contrato;
import com.esferalia.aon.payroll.ParteConfirmacionIT;
import com.esferalia.aon.payroll.ParteIT;
import com.esferalia.aon.payroll.ParteITPK;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.RemesaINSS;
import com.esferalia.aon.payroll.RemesaParteIT;
import com.esferalia.aon.payroll.core.IBonificacion;
import com.esferalia.aon.payroll.core.IContrato;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.cotizacion.ITipoBonificacion;
import com.esferalia.aon.payroll.core.enumeration.CuentaCotizacion;
import com.esferalia.aon.payroll.core.enumeration.TipoContingencia;
import com.esferalia.aon.payroll.core.it.IParteConfirmacionIT;
import com.esferalia.aon.payroll.core.it.IParteIT;
import com.esferalia.aon.payroll.core.it.IParteITCalculator;
import com.esferalia.aon.payroll.core.it.IParteITDAO;
import com.esferalia.aon.payroll.core.it.ParteITCalculatorFactory;
import com.esferalia.aon.payroll.core.it.ParteITDAOFactory;
import com.esferalia.aon.payroll.core.it.ParteITParams;
import com.esferalia.aon.payroll.core.it.RemesaINSSParams;
import com.esferalia.aon.payroll.core.remesa.IRemesaINSS;
import com.esferalia.aon.payroll.core.remesa.IRemesaParteIT;
import com.esferalia.aon.payroll.cotizacion.Bonificacion;
import com.esferalia.aon.payroll.cotizacion.BonificacionPK;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.utils.NumberValidation;

public class ParteITDAO implements IParteITDAO {

	private static String EMP_ALIAS;
	private static String FEC_INI_ALIAS;
	private static String BAJ_PROC_ALIAS;
	private static String ALT_PROC_ALIAS;
	private static String EMPRESA_CDG_ALIAS;
	private static String PERSONA_CDG_ALIAS;
	private static String CONF_PROC_ALIAS;
	private static String CONF_FEC_ALIAS;
	private static String CONF_EMPRESA_CDG_ALIAS;
	private static String CONF_PERSONA_CDG_ALIAS;
	private static String REMESA_INSS_CDG_ALIAS;

	static {
		ParteITDAOFactory.register(new ParteITDAO());
	}
	
	@Override
	public void configure() {
		try {
			IManagerBean parteITBean = BeanManager.getManagerBean(ParteIT.class);
			EMP_ALIAS = parteITBean.getFieldName(IPayrollAlias.PARTE_IT_EMPLEADO_ID);
			FEC_INI_ALIAS = parteITBean.getFieldName(IPayrollAlias.PARTE_IT_ID_FECHA_BAJA);
			BAJ_PROC_ALIAS = parteITBean.getFieldName(IPayrollAlias.PARTE_IT_BAJA_PROCESADA_BD);
			ALT_PROC_ALIAS = parteITBean.getFieldName(IPayrollAlias.PARTE_IT_ALTA_PROCESADA_BD);
			EMPRESA_CDG_ALIAS = parteITBean.getFieldName(IPayrollAlias.PARTE_IT_EMPLEADO_ACTIVIDAD_EMPRESA_ID);
			PERSONA_CDG_ALIAS = parteITBean.getFieldName(IPayrollAlias.PARTE_IT_EMPLEADO_PERSONA_ID);
			IManagerBean parteConfITBean = BeanManager.getManagerBean(ParteConfirmacionIT.class);
			CONF_PROC_ALIAS = parteConfITBean.getFieldName(IPayrollAlias.PARTE_CONFIRMACION_IT_PROCESADO_BD);
			CONF_FEC_ALIAS = parteConfITBean.getFieldName(IPayrollAlias.PARTE_CONFIRMACION_IT_FECHA);
			CONF_EMPRESA_CDG_ALIAS = parteConfITBean.getFieldName(IPayrollAlias.PARTE_CONFIRMACION_IT_PARTE_IT_EMPLEADO_ACTIVIDAD_EMPRESA_ID);
			CONF_PERSONA_CDG_ALIAS = parteConfITBean.getFieldName(IPayrollAlias.PARTE_CONFIRMACION_IT_PARTE_IT_EMPLEADO_PERSONA_ID);
			IManagerBean remesaINSSBean = BeanManager.getManagerBean(RemesaINSS.class);
			REMESA_INSS_CDG_ALIAS = remesaINSSBean.getFieldName(IPayrollAlias.REMESA_INSS_ID);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}	

	@Override
	public Criteria getParteITCriteria(ParteITParams params) throws PayrollException {
		try {
			Criteria c = new Criteria();
			if (!StringUtils.isBlank(params.getEmpleadoID())) {
				c.addExpression(EMP_ALIAS, params.getEmpleadoID());
			}
			if (params.isBaja()) {
				if (!params.isAlta()) {
					c.addEqualExpression(ALT_PROC_ALIAS, "S");	
				}
				c.addEqualExpression(BAJ_PROC_ALIAS, "N");
			}
			if (params.isAlta()) {
				if (!params.isBaja()) {
					c.addEqualExpression(BAJ_PROC_ALIAS, "S");	
				}
				c.addEqualExpression(ALT_PROC_ALIAS, "N");
			}
			c.addOrder(EMPRESA_CDG_ALIAS);
			c.addOrder(PERSONA_CDG_ALIAS);
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
			List<?> list = bean.getList( getParteITCriteria(p) );
			return (List<IParteIT>) list;
		} catch (ManagerBeanException e) {
			throw new PayrollException( e );
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<IParteConfirmacionIT> getPartesConfirmacion(IParteIT parteIT) throws PayrollException {
		try {
			ParteIT parte = (ParteIT)parteIT; 
			IManagerBean bean = BeanManager.getManagerBean(ParteConfirmacionIT.class);
			Criteria criteria = new Criteria();
			String id = bean.getFieldName(IPayrollAlias.PARTE_CONFIRMACION_IT_ID_CDG);
			criteria.addEqualExpression(id, parte.getId().getCdg());
			id = bean.getFieldName(IPayrollAlias.PARTE_CONFIRMACION_IT_ID_FECHA_BAJA);
			criteria.addEqualExpression(id, parte.getId().getFechaBaja());
			id = bean.getFieldName(IPayrollAlias.PARTE_CONFIRMACION_IT_ID_NUMERO);
			criteria.addOrder(id, false);
			List<?> list = bean.getList( criteria ); 
			return (List<IParteConfirmacionIT>) list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
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
		if (!parteIT.getEmpleado().getActividad().getEmpresa().isActive()) {
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
		
		// El CIAS debe cumplir una mascara
		if ((!StringUtils.isBlank(parteIT.getCiasAlta()) && !NumberValidation.validCiasPattern(parteIT.getCiasAlta())) 
				|| (!StringUtils.isBlank(parteIT.getCiasBaja()) && !NumberValidation.validCiasPattern(parteIT.getCiasBaja())) ) {
			return 13;
		}
		
		// El nº colegiado debe cumplir una mascara
		if ((!StringUtils.isBlank(parteIT.getNumeroColegiadoAlta()) && !NumberValidation.validNumeroColegiadoPattern(parteIT.getNumeroColegiadoAlta())) 
				|| (!StringUtils.isBlank(parteIT.getNumeroColegiadoBaja()) && !NumberValidation.validNumeroColegiadoPattern(parteIT.getNumeroColegiadoBaja())) ) {
			return 14;
		}
		
		// El CIAS debe corresponder con su digito de control
		if ((!StringUtils.isBlank(parteIT.getCiasAlta()) && !NumberValidation.validCiasControlDigit(parteIT.getCiasAlta())) 
				|| (!StringUtils.isBlank(parteIT.getCiasBaja()) && !NumberValidation.validCiasControlDigit(parteIT.getCiasBaja())) ) {
			return 15;
		}
		
		// El nº colegiado corresponder con su digito de control
		if ((!StringUtils.isBlank(parteIT.getNumeroColegiadoAlta()) && !NumberValidation.validNumeroColegiadoControlDigit(parteIT.getNumeroColegiadoAlta())) 
				|| (!StringUtils.isBlank(parteIT.getNumeroColegiadoBaja()) && !NumberValidation.validNumeroColegiadoControlDigit(parteIT.getNumeroColegiadoBaja())) ) {
			return 16;
		}
		
		return 0;
	}
	
	@Override
	public void calculate(IParteIT parteIT) throws PayrollException {
		IContrato contrato = getContrato(parteIT);
		ParteITCalculatorFactory factory = ParteITCalculatorFactory.getInstance();
		IParteITCalculator calculator = factory.getParteITCalculator( parteIT, contrato );
		parteIT.setBaseRetribucionPeriodoAnterior( calculator.getBaseRetribucionPeriodoAnterior(parteIT) );
		parteIT.setDiasPeriodoAnterior(calculator.getDiasPeriodoAnterior(parteIT) );
		parteIT.setBaseReguladoraDiaria( calculator.getBaseReguladoraDiaria(parteIT) );
		parteIT.setBaseDiariaContingenciasComunes(calculator.getBaseDiariaContingenciasComunes(parteIT));
		parteIT.setBaseDiariaAccidentesTrabajo(calculator.getBaseDiariaAccidentesTrabajo(parteIT) );
		parteIT.setPrestacionDiaria60(calculator.getPrestacionDiaria60(parteIT));
		parteIT.setPrestacionDiaria75(calculator.getPrestacionDiaria75(parteIT));
	}
	
	@Override
	public IContrato getContrato(IParteIT parteIT) throws PayrollException {
		try {
			IManagerBean trabajoBean = BeanManager.getManagerBean(Contrato.class);
			String eAlias = trabajoBean.getFieldName(IPayrollAlias.CONTRATO_ID_CDG);
			String iAlias = trabajoBean.getFieldName(IPayrollAlias.CONTRATO_ID_FECINI);
			String fAlias = trabajoBean.getFieldName(IPayrollAlias.CONTRATO_FECHA_FIN);
			Criteria tCriteria = new Criteria();
			tCriteria.addEqualExpression(eAlias, parteIT.getEmpleado().getId());
			tCriteria.addLessThanOrEqualExpression(iAlias, parteIT.getFechaBaja());
			Expression or1 = ExpressionUtilities.getGreaterThanOrEqualExpression(fAlias, parteIT.getFechaBaja());  
			Expression or2 = ExpressionUtilities.getNullExpression(fAlias);
			tCriteria.addExpression(ExpressionUtilities.getOrExpression(or1, or2));
			List<ITransferObject> trabajos = trabajoBean.getList(tCriteria);
			if (trabajos.size() == 0) {
				throw new PayrollException("No hay datos en CONTRATO");
			}
			Contrato trabajo = (Contrato) trabajos.get(0);
			return trabajo;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}

	@Override
	public IParteIT initialize(IEmpleado  empleado) throws PayrollException {
		if (empleado == null || empleado.getId() == null) {
			throw new IllegalArgumentException("El Empleado del parteIT no puede ser nulo.");
		}
		ParteIT parteIT = new ParteIT();
		parteIT.setEmpleado(empleado);
		parteIT.setAltaProcesada(false);
		parteIT.setBajaProcesada(false);
		parteIT.setProcesada(false);
		List<IParteIT> list = getPartesEmpleado(parteIT.getEmpleado());
		if (list.size() > 0) {
			IParteIT ultimoParte = list.get(0);
			if (ultimoParte.getFechaAlta() != null) {
				// NUEVA BAJA
				parteIT.setCiasBaja(ultimoParte.getCiasBaja());
				parteIT.setNumeroColegiadoBaja(ultimoParte.getNumeroColegiadoBaja());
				parteIT.setCiasAlta(ultimoParte.getCiasAlta());
				parteIT.setNumeroColegiadoAlta(ultimoParte.getNumeroColegiadoAlta());
				parteIT.setTipoContingencia(TipoContingencia.ENFERMEDAD_COMUN);
			} else {
				// MODIFICACION DEL REGISTRO PARA EL ALTA 
				// O NUEVO PARTE DE CONFIRMACION.
				parteIT = (ParteIT) ultimoParte;
				parteIT.setFechaAlta(new Date() );
				parteIT.setCiasAlta(ultimoParte.getCiasBaja());
				parteIT.setNumeroColegiadoAlta(ultimoParte.getNumeroColegiadoBaja());
				parteIT.setAltaProcesada(false);
				parteIT.setProcesada(false);
			}
		}
		return parteIT;
	}
	
	@Override
	public IParteConfirmacionIT initialize(IParteIT  parteIT) throws PayrollException {
		ParteConfirmacionIT confirmacionParteIT = new ParteConfirmacionIT();
		ParteIT newParteIT = new ParteIT();
		newParteIT.setId(new ParteITPK());
		newParteIT.getId().setCdg(((ParteIT)parteIT).getId().getCdg());
		newParteIT.getId().setFechaBaja(((ParteIT)parteIT).getId().getFechaBaja());
		confirmacionParteIT.setParteIT(newParteIT);
		confirmacionParteIT.setId(new ConfirmacionParteITPK());
		confirmacionParteIT.setProcesado(false);
		confirmacionParteIT.getId().setCdg(((ParteIT)parteIT).getId().getCdg());
		confirmacionParteIT.getId().setFechaBaja(((ParteIT)parteIT).getId().getFechaBaja());
		
		return confirmacionParteIT;
	}
	
	@Override
	public IRemesaINSS initializeRemesa() throws PayrollException {
		RemesaINSS remesa = new RemesaINSS();
		remesa.setFecha(new Date() );
		remesa.setHora(new Date());
		return remesa;
	}
	
	@Override
	public IRemesaParteIT initializePartesRemesa() throws PayrollException {
		return new RemesaParteIT();
	}
	

	@Override
	public void accept(IParteIT parteIT) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ParteIT.class);
			ParteIT p = (ParteIT) parteIT;
			bean.insertOrUpdate(p);
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
	
	@Override
	public void accept(IParteConfirmacionIT parteConfirmacionIT) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ParteConfirmacionIT.class);
			ParteConfirmacionIT c = (ParteConfirmacionIT) parteConfirmacionIT;
			bean.insertOrUpdate(c);
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
	
	// metodo para grabar la remesa de partes de it
	@Override
	public IRemesaINSS accept(IRemesaINSS remesaINSS) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(RemesaINSS.class);
			RemesaINSS r = (RemesaINSS) remesaINSS;
			return (IRemesaINSS) bean.insertOrUpdate(r);
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
	
	//	metodo para grabar los parteIT de una remesa 
	@Override
	public IRemesaParteIT accept(IRemesaParteIT remesaParteIT) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(RemesaParteIT.class);
			RemesaParteIT r = (RemesaParteIT) remesaParteIT;
			return (IRemesaParteIT) bean.insertOrUpdate(r);
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}

	@Override
	public int getCount(ParteITParams params) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ParteIT.class);
			return bean.getCount( getParteITCriteria(params) );
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IParteIT> getPartes(ParteITParams params) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ParteIT.class);
			List<?> list = bean.getList( getParteITCriteria(params) ); 
			return (List<IParteIT>) list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IParteIT> getPartes(ParteITParams params, int start, int count) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ParteIT.class);
			List<?> list = bean.getList( getParteITCriteria(params), start, count ); 
			return (List<IParteIT>) list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}

	@Override
	public IBonificacion initializeBonificacion(IParteIT parteIT, ITipoBonificacion tipoBonificacion) {
		Bonificacion b = new Bonificacion();
		BonificacionPK pk = new BonificacionPK();
		pk.setCdg(parteIT.getEmpleado().getId());
		pk.setNumero(tipoBonificacion.getId());
		Calendar c = Calendar.getInstance();
		c.setTime(parteIT.getFechaAlta());
		c.add(Calendar.DAY_OF_MONTH, 1);
		Date fechaInicio = c.getTime();
		c.setTime(parteIT.getFechaAlta());
		c.add(Calendar.YEAR, 1);
		Date fechaFin = c.getTime();
		pk.setFechaInicio(fechaInicio);
		b.setFechaFin(fechaFin);
		b.setId(pk);
		b.setHoras(0);
		b.setImporte(0.0);
		b.setTipoBonificacion(tipoBonificacion);
		return b;
	}

	@Override
	public Criteria getParteConfCriteria(ParteITParams params) throws PayrollException {
		Criteria c = new Criteria();
		if (params.isConfirmacion()) {
			c.addEqualExpression(CONF_PROC_ALIAS, "N");
		}
		c.addOrder(CONF_EMPRESA_CDG_ALIAS);
		c.addOrder(CONF_PERSONA_CDG_ALIAS);
		c.addOrder(CONF_FEC_ALIAS);
		return c;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IParteConfirmacionIT> getPartesConfirmacion(ParteITParams params) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ParteConfirmacionIT.class);
			List<?> list = bean.getList( getParteConfCriteria(params) ); 
			return (List<IParteConfirmacionIT>) list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IRemesaINSS> getRemesaINSS(RemesaINSSParams params) throws PayrollException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(RemesaINSS.class);
			List<?> list = bean.getList( getRemesaINSSCriteria(params) ); 
			return (List<IRemesaINSS>) list;
		} catch (ManagerBeanException e) {
			throw new PayrollException(e);
		}
	}
	
	@Override
	public Criteria getRemesaINSSCriteria(RemesaINSSParams params) throws PayrollException {
		Criteria c = new Criteria();
		if (params!=null && params.getId()!=null) {
			c.addEqualExpression(REMESA_INSS_CDG_ALIAS, params.getId());
		}
//		c.addOrder(CONF_EMPRESA_CDG_ALIAS);
//		c.addOrder(CONF_PERSONA_CDG_ALIAS);
//		c.addOrder(CONF_FEC_ALIAS);
		return c;
	}
}

