package com.esferalia.aon.payroll.parteIT;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.esferalia.aon.payroll.ParteIT;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.it.IParteIT;
import com.esferalia.aon.payroll.core.it.IParteITDAO;
import com.esferalia.aon.payroll.core.it.ParteITDAOFactory;
import com.esferalia.aon.payroll.core.it.ParteITParams;
import com.esferalia.aon.payroll.dao.IPayrollAlias;

public class ParteITDAO implements IParteITDAO {

	private static String EMP_ALIAS;
	private static String FEC_INI_ALIAS;

	static {
		ParteITDAOFactory.register(new ParteITDAO());

		try {
			IManagerBean nominaBean = BeanManager.getManagerBean(ParteIT.class);
			EMP_ALIAS = nominaBean.getFieldName(IPayrollAlias.PARTE_IT_EMPLEADO_ID);
			FEC_INI_ALIAS = nominaBean.getFieldName(IPayrollAlias.PARTE_IT_ID_FECHA_BAJA);
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
	public IParteIT initialize(IParteIT parteIT) throws PayrollException {
		if (parteIT == null) {
			throw new IllegalArgumentException("ParteIT a inicializar no puede ser nulo.");
		}
		if (parteIT.getEmpleado() == null || parteIT.getEmpleado().getId() == null) {
			throw new IllegalArgumentException("El Empleado del parteIT no puede ser nulo.");
		}
		List<IParteIT> list = getPartesEmpleado(parteIT.getEmpleado());
		if (list.size() > 0) {
			IParteIT ultimoParte = list.get(0);
			parteIT.setCiasBaja(ultimoParte.getCiasBaja());
			parteIT.setNumeroColegiadoBaja(ultimoParte.getCiasBaja());
			parteIT.setCiasAlta(ultimoParte.getCiasAlta());
			parteIT.setNumeroColegiadoAlta(ultimoParte.getCiasAlta());
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
