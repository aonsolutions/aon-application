package com.esferalia.aon.payroll.dao;


import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.esferalia.aon.payroll.Actividad;
import com.esferalia.aon.payroll.ActividadCCC;
import com.esferalia.aon.payroll.Cliente;
import com.esferalia.aon.payroll.Complemento;
import com.esferalia.aon.payroll.Contrato;
import com.esferalia.aon.payroll.ContratosTc2;
import com.esferalia.aon.payroll.Empleado;
import com.esferalia.aon.payroll.Empresa;
import com.esferalia.aon.payroll.Finiquito;
import com.esferalia.aon.payroll.FiniquitoDiferencia;
import com.esferalia.aon.payroll.Nomina;
import com.esferalia.aon.payroll.NominaDiferencia;
import com.esferalia.aon.payroll.ParteConfirmacionIT;
import com.esferalia.aon.payroll.ParteIT;
import com.esferalia.aon.payroll.Percepcion;
import com.esferalia.aon.payroll.Persona;
import com.esferalia.aon.payroll.RemesaINSS;
import com.esferalia.aon.payroll.RemesaParteIT;
import com.esferalia.aon.payroll.Trabajo;
import com.esferalia.aon.payroll.Usuario;
import com.esferalia.aon.payroll.cotizacion.BaseCotizacion;
import com.esferalia.aon.payroll.cotizacion.Bonificacion;
import com.esferalia.aon.payroll.cotizacion.TipoBonificacion;
import com.esferalia.aon.payroll.empresa.RemesaCertificadoEmpresa;
import com.esferalia.aon.payroll.empresa.RemesaCertificadoEmpresaDetalle;

public class PayrollAliasWriter {

	public static void main(String[] args) throws IOException {
		File file = new File("/AON-TRUNK/aon-payroll-nominasta/src/main/java/com/esferalia/aon/payroll/dao/IPayrollAlias.java");
		String[] classes = new String[] {
				Actividad.class.getName(),
				ActividadCCC.class.getName(),
				BaseCotizacion.class.getName(),
				Bonificacion.class.getName(),
				Cliente.class.getName(),
				Complemento.class.getName(),
				Contrato.class.getName(),
				ContratosTc2.class.getName(),
				Empleado.class.getName(),			
				Empresa.class.getName(),
				Finiquito.class.getName(),
				FiniquitoDiferencia.class.getName(),
				Nomina.class.getName(),
				NominaDiferencia.class.getName(),
				ParteConfirmacionIT.class.getName(),
				ParteIT.class.getName(),
				Percepcion.class.getName(),
				Persona.class.getName(),
				RemesaCertificadoEmpresa.class.getName(),
				RemesaCertificadoEmpresaDetalle.class.getName(),
				RemesaINSS.class.getName(),
				RemesaParteIT.class.getName(),
				Trabajo.class.getName(),
				TipoBonificacion.class.getName(),
				Usuario.class.getName()
		        };
		AliasWriter writer = new AliasWriter("com.esferalia.aon.payroll.dao");
		HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName());
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
	
}
