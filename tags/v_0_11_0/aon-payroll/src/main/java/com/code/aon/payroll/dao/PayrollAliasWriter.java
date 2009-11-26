package com.code.aon.payroll.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.payroll.auxiliares.Admon;
import com.code.aon.payroll.auxiliares.Colectivos;
import com.code.aon.payroll.auxiliares.contratos.ContratosInternos;
import com.code.aon.payroll.auxiliares.contratos.ContratosTc2;
import com.code.aon.payroll.auxiliares.convenios.Categoria;
import com.code.aon.payroll.auxiliares.convenios.Complemento;
import com.code.aon.payroll.auxiliares.convenios.Convenio;
import com.code.aon.payroll.auxiliares.convenios.Nivel;
import com.code.aon.payroll.auxiliares.convenios.Pagaext;
import com.code.aon.payroll.auxiliares.convenios.Percniv;
import com.code.aon.payroll.cotizacion.Base;
import com.code.aon.payroll.cotizacion.Bonificacion;
import com.code.aon.payroll.cotizacion.Cnae;
import com.code.aon.payroll.cotizacion.CnaeMaestro;
import com.code.aon.payroll.cotizacion.Elemento;
import com.code.aon.payroll.cotizacion.ElementoMaestro;
import com.code.aon.payroll.cotizacion.Epigrafe;
import com.code.aon.payroll.cotizacion.Linbasec;
import com.code.aon.payroll.cotizacion.Linepigr;
import com.code.aon.payroll.cotizacion.Ocupacion;
import com.code.aon.payroll.cotizacion.OcupacionMaestro;
import com.code.aon.payroll.cotizacion.Porcentaje;
import com.code.aon.payroll.cotizacion.PorcentajeMaestro;
import com.code.aon.payroll.divisa.Divisa;
import com.code.aon.payroll.divisa.LinDivisa;
import com.code.aon.payroll.geograficas.Comunidad;
import com.code.aon.payroll.geograficas.Nacion;
import com.code.aon.payroll.geograficas.Pais;
import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.payroll.irpf.Cuota;
import com.code.aon.payroll.irpf.Elemirpf;
import com.code.aon.payroll.irpf.Exclusion;
import com.code.aon.payroll.irpf.Linirpf;
import com.code.aon.payroll.irpfforal.CuotaRetencionAlava;
import com.code.aon.payroll.irpfforal.CuotaRetencionGuipuzcoa;
import com.code.aon.payroll.irpfforal.CuotaRetencionNavarra;
import com.code.aon.payroll.irpfforal.CuotaRetencionVizcaya;
import com.code.aon.payroll.irpfforal.MinoracionesAlava;
import com.code.aon.payroll.irpfforal.MinoracionesGuipuzcoa;
import com.code.aon.payroll.irpfforal.MinoracionesNavarra;
import com.code.aon.payroll.irpfforal.MinoracionesVizcaya;
import com.code.aon.payroll.auxiliares.organismosyentidades.Delegacion;
import com.code.aon.payroll.auxiliares.organismosyentidades.Entidad;
import com.code.aon.payroll.auxiliares.organismosyentidades.Linmutua;
import com.code.aon.payroll.auxiliares.organismosyentidades.Mutua;
import com.code.aon.payroll.auxiliares.organismosyentidades.Sucursal;
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
				Complemento.class.getName(),
				PorcentajeMaestro.class.getName(), 
				Porcentaje.class.getName(),
				ElementoMaestro.class.getName(), 
				Elemento.class.getName(),
				OcupacionMaestro.class.getName(), 
				Ocupacion.class.getName(),
				CnaeMaestro.class.getName(), 
				Cnae.class.getName(),
				Documento.class.getName(),
				Autorizacion.class.getName(),
				Base.class.getName(),
				Linbasec.class.getName(),
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
				Exclusion.class.getName(), 
		        Pais.class.getName(),
		        Comunidad.class.getName(),
		        Provincia.class.getName(),
		        Delegacion.class.getName(),
		        Epigrafe.class.getName(),
		        Linepigr.class.getName(),
		        Divisa.class.getName(),
		        LinDivisa.class.getName(),
		        Convenio.class.getName(),
		        Nivel.class.getName(),
		        Pagaext.class.getName(),
		        Categoria.class.getName(),
		        Percniv.class.getName(),
		        Entidad.class.getName(),
		        Sucursal.class.getName(),
		        Mutua.class.getName(),
		        Linmutua.class.getName(),
		        Elemirpf.class.getName(),
		        Linirpf.class.getName()
		        };		
		AliasWriter writer = new AliasWriter("com.code.aon.payroll.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}
