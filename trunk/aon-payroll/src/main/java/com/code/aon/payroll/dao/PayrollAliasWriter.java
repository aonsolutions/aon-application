package com.code.aon.payroll.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.payroll.cotizacion.Porcentaje;
import com.code.aon.payroll.cotizacion.PorcentajeMaestro;
import com.code.aon.payroll.tipos.Autorizacion;
import com.code.aon.payroll.tipos.Documento;
import com.code.aon.payroll.tipos.Incidencia;
import com.code.aon.payroll.tipos.Registro;

public class PayrollAliasWriter {

	public static void main(String[] args) throws IOException {
		File file = new File("/AON-PROJECT/aon-payroll/src/main/java/com/code/aon/payroll/dao/IPayrollAlias.java");
		String[] classes = new String[6];
		classes[0] = PorcentajeMaestro.class.getName(); 
		classes[1] = Porcentaje.class.getName();
		classes[2] = Documento.class.getName();
		classes[3] = Autorizacion.class.getName();
		classes[4] = Incidencia.class.getName();
		classes[5] = Registro.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.payroll.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}
