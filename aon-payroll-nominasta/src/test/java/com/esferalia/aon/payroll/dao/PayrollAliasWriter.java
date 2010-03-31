package com.esferalia.aon.payroll.dao;

import java.io.File;

import junit.framework.TestCase;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.esferalia.aon.payroll.Actividad;
import com.esferalia.aon.payroll.Contrato;
import com.esferalia.aon.payroll.Empleado;
import com.esferalia.aon.payroll.Empresa;
import com.esferalia.aon.payroll.Nomina;
import com.esferalia.aon.payroll.ParteConfirmacionIT;
import com.esferalia.aon.payroll.ParteIT;
import com.esferalia.aon.payroll.Persona;
import com.esferalia.aon.payroll.TipoBonificacion;

public class PayrollAliasWriter extends TestCase{

	public void testAlias() throws Exception {
		File file = new File("/AON-TRUNK/aon-payroll-nominasta/src/main/java/com/esferalia/aon/payroll/dao/IPayrollAlias.java");
		String[] classes = new String[] {
				Actividad.class.getName(),
				Empleado.class.getName(),			
				Empresa.class.getName(),
				Nomina.class.getName(),
				Persona.class.getName(),
				ParteIT.class.getName(),
				ParteConfirmacionIT.class.getName(),
				TipoBonificacion.class.getName(),
				Contrato.class.getName()
		        };
		AliasWriter writer = new AliasWriter("com.esferalia.aon.payroll.dao");
		HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName());
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}
