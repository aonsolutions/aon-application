package com.esferalia.aon.payroll.test;

import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.AonPayroll;
import com.esferalia.aon.payroll.ParteIT;
import com.esferalia.aon.payroll.core.it.IParteITDAO;
import com.esferalia.aon.payroll.core.it.ParteITDAOFactory;
import com.esferalia.aon.payroll.dao.IPayrollAlias;


public class ParteITTest extends TestCase{

	public void testParteIT() throws Exception {
		try {
			AonPayroll.configure();
			IManagerBean bean = BeanManager.getManagerBean(ParteIT.class);
			Criteria criteria = new Criteria();
			String alias = bean.getFieldName( IPayrollAlias.PARTE_IT_ID_FECHA_BAJA);
			Calendar c = Calendar.getInstance();
			c.set(Calendar.DAY_OF_MONTH, 1);
			c.set(Calendar.MONTH, 0);
			c.set(Calendar.YEAR, 2009);
			criteria.addGreaterThanExpression(alias, c.getTime() );
			List<ITransferObject> list = bean.getList( criteria,0,10 );
			if (list.size() == 0) {
				fail("No hay datos en ParteIT");	
			}
			ParteIT original = (ParteIT) list.get(0); 
			
			ParteIT duplicado = new ParteIT();
			duplicado.setTipoContingencia(original.getTipoContingencia());
			duplicado.setTipoIT(original.getTipoIT());
			duplicado.setEmpleado( original.getEmpleado());
			System.out.println( original.getEmpleado().getId() );
			duplicado.setFechaBaja( original.getFechaBaja() );
			System.out.println( original.getFechaBaja() );
			duplicado.setCiasBaja( original.getCiasBaja());
			duplicado.setNumeroColegiadoBaja(original.getNumeroColegiadoBaja());
			
			ParteITDAOFactory fac = ParteITDAOFactory.getInstance();
			IParteITDAO parteITDAO = fac.getParteITDAO();
			int err = parteITDAO.validate(duplicado);
			if (err > 0) {
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.core.impl.messages");
				fail( bundle.getString("aon_payroll_error_" + err) );
			}
			
			parteITDAO.calculate(duplicado);
			assertEquals(original.getDiasPeriodoAnterior(), duplicado.getDiasPeriodoAnterior());
			assertEquals(original.getBaseRetribucionPeriodoAnterior(), CommonUtil.round(duplicado.getBaseRetribucionPeriodoAnterior()));
			assertEquals(original.getBaseReguladoraDiaria(), CommonUtil.round(duplicado.getBaseReguladoraDiaria()));
			assertEquals(original.getBaseDiariaContingenciasComunes(), CommonUtil.round(duplicado.getBaseDiariaContingenciasComunes()));
			assertEquals(original.getBaseDiariaAccidentesTrabajo(), CommonUtil.round(duplicado.getBaseDiariaAccidentesTrabajo()));
//			assertEquals(original.getPrestacionDiaria60(), CommonUtil.round(duplicado.getPrestacionDiaria60()));
//			assertEquals(original.getPrestacionDiaria75(), CommonUtil.round(duplicado.getPrestacionDiaria75()));
			
			
		} catch (Throwable e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
