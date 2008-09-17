package com.code.aon.payroll.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.payroll.cotizacion.Bonificacion;
import com.code.aon.payroll.cotizacion.Porcentaje;
import com.code.aon.payroll.cotizacion.PorcentajeMaestro;
import com.code.aon.payroll.irpf.Cuota;
import com.code.aon.payroll.tipos.Autorizacion;
import com.code.aon.payroll.tipos.Documento;
import com.code.aon.payroll.tipos.Empresario;
import com.code.aon.payroll.tipos.Incidencia;
import com.code.aon.payroll.tipos.Registro;
import com.code.aon.payroll.tipos.TipoCnae;
import com.code.aon.payroll.tipos.Tipovia;
import com.code.aon.payroll.geograficas.Nacion;

public class PayrollAliasWriter {

	public static void main(String[] args) throws IOException {
		File file = new File("/AON-PROJECT/aon-payroll/src/main/java/com/code/aon/payroll/dao/IPayrollAlias.java");
		
		String[] classes = new String[] {
				PorcentajeMaestro.class.getName(), 
				Porcentaje.class.getName(),
				Documento.class.getName(),
				Autorizacion.class.getName(),
				Incidencia.class.getName(),
				Registro.class.getName(),
				Empresario.class.getName(),
				TipoCnae.class.getName(),
				Bonificacion.class.getName(),
				Tipovia.class.getName(),
				Nacion.class.getName()};

		AliasWriter writer = new AliasWriter("com.code.aon.payroll.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}
