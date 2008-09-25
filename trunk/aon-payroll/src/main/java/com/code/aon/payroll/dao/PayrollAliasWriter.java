package com.code.aon.payroll.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.payroll.auxiliares.Admon;
import com.code.aon.payroll.auxiliares.Colectivos;
import com.code.aon.payroll.auxiliares.contratos.ContratosInternos;
import com.code.aon.payroll.auxiliares.contratos.ContratosTc2;
import com.code.aon.payroll.cotizacion.Bonificacion;
import com.code.aon.payroll.cotizacion.Porcentaje;
import com.code.aon.payroll.cotizacion.PorcentajeMaestro;
import com.code.aon.payroll.geograficas.Nacion;
import com.code.aon.payroll.irpf.Cuota;
import com.code.aon.payroll.irpf.Exclusion;
import com.code.aon.payroll.irpfforal.CuotaRetencionAlava;
import com.code.aon.payroll.irpfforal.CuotaRetencionGuipuzcoa;
import com.code.aon.payroll.irpfforal.CuotaRetencionNavarra;
import com.code.aon.payroll.irpfforal.CuotaRetencionVizcaya;
import com.code.aon.payroll.irpfforal.MinoracionesAlava;
import com.code.aon.payroll.irpfforal.MinoracionesGuipuzcoa;
import com.code.aon.payroll.irpfforal.MinoracionesNavarra;
import com.code.aon.payroll.irpfforal.MinoracionesVizcaya;
import com.code.aon.payroll.tipos.Autorizacion;
import com.code.aon.payroll.tipos.Documento;
import com.code.aon.payroll.tipos.Empresario;
import com.code.aon.payroll.tipos.Incidencia;
import com.code.aon.payroll.tipos.Registro;
import com.code.aon.payroll.tipos.TipoCnae;
import com.code.aon.payroll.tipos.Tipovia;

public class PayrollAliasWriter {

	public static void main(String[] args) throws IOException {
		File file = new File("/AON-PROJECT/aon-payroll/src/main/java/com/code/aon/payroll/dao/IPayrollAlias.java");
		String[] classes = new String[] {
				ContratosInternos.class.getName(),
				ContratosTc2.class.getName(),
				PorcentajeMaestro.class.getName(), 
				Porcentaje.class.getName(),
				Documento.class.getName(),
				Autorizacion.class.getName(),
				Incidencia.class.getName(),
				Registro.class.getName(),
				Empresario.class.getName(),
				TipoCnae.class.getName(),
				Bonificacion.class.getName(),
				Cuota.class.getName(),
				Tipovia.class.getName(),
				Nacion.class.getName(), 
				CuotaRetencionAlava.class.getName(),
				CuotaRetencionVizcaya.class.getName(),
				CuotaRetencionNavarra.class.getName(),
				CuotaRetencionGuipuzcoa.class.getName(),
				MinoracionesAlava.class.getName(),
				MinoracionesVizcaya.class.getName(),
				MinoracionesGuipuzcoa.class.getName(),
				MinoracionesNavarra.class.getName(),
				Admon.class.getName(),
				Colectivos.class.getName(),
				Exclusion.class.getName()};
		AliasWriter writer = new AliasWriter("com.code.aon.payroll.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}
