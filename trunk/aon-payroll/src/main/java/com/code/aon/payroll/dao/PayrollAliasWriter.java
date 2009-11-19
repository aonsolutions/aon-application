package com.code.aon.payroll.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
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
import com.code.aon.payroll.auxiliares.convenios.calendar.Calendario;
import com.code.aon.payroll.auxiliares.organismosyentidades.Delegacion;
import com.code.aon.payroll.auxiliares.organismosyentidades.Entidad;
import com.code.aon.payroll.auxiliares.organismosyentidades.Linmutua;
import com.code.aon.payroll.auxiliares.organismosyentidades.Mutua;
import com.code.aon.payroll.auxiliares.organismosyentidades.Sucursal;
import com.code.aon.payroll.avanzadas.gestel.Linvariable;
import com.code.aon.payroll.avanzadas.gestel.Variable;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httaviso;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httbonificacion;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httcomplemento;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httincidencia;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httrabajador;
import com.code.aon.payroll.avanzadas.kartel.Linpercepcion;
import com.code.aon.payroll.avanzadas.kartel.Percepcion;
import com.code.aon.payroll.avanzadas.simulacion.Costes;
import com.code.aon.payroll.avanzadas.simulacion.Lbonifica;
import com.code.aon.payroll.avanzadas.simulacion.Lcomunica;
import com.code.aon.payroll.cotizacion.Base;
import com.code.aon.payroll.cotizacion.Bonificacion;
import com.code.aon.payroll.cotizacion.Cnae;
import com.code.aon.payroll.cotizacion.Cnae2009;
import com.code.aon.payroll.cotizacion.Cnae2009Maestro;
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
import com.code.aon.payroll.principales.Avisos;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.payroll.principales.Cuentas;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.payroll.principales.autonomos.Autbases;
import com.code.aon.payroll.principales.autonomos.Autonomos;
import com.code.aon.payroll.principales.empresa.Actividad;
import com.code.aon.payroll.principales.empresa.Emprccc;
import com.code.aon.payroll.principales.empresa.Emprccos;
import com.code.aon.payroll.principales.empresa.Emprctra;
import com.code.aon.payroll.principales.empresa.Emprdom;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.payroll.principales.empresa.Emprlban;
import com.code.aon.payroll.principales.persona.Embargo;
import com.code.aon.payroll.principales.persona.Trabajador;
import com.code.aon.payroll.principales.personas.Bonifica;
import com.code.aon.payroll.principales.personas.Nominait;
import com.code.aon.payroll.principales.personas.Otrperc;
import com.code.aon.payroll.principales.personas.Parteconf;
import com.code.aon.payroll.principales.personas.Parteit;
import com.code.aon.payroll.principales.personas.Percep;
import com.code.aon.payroll.principales.personas.Persona;
import com.code.aon.payroll.principales.personas.Prcdivtrab;
import com.code.aon.payroll.principales.personas.Tipocont;
import com.code.aon.payroll.principales.personas.Trabajo;
import com.code.aon.payroll.principales.personas.Trabdto;
import com.code.aon.payroll.principales.personas.Trabinci;
import com.code.aon.payroll.resultados.irpf.Calculo;
import com.code.aon.payroll.resultados.irpf.Impresos11x;
import com.code.aon.payroll.resultados.irpf.Impresos190;
import com.code.aon.payroll.resultados.irpf.LinImpresos190;
import com.code.aon.payroll.resultados.irpf.Lincalcu;
import com.code.aon.payroll.resultados.nomina.Nomdto;
import com.code.aon.payroll.resultados.nomina.Nomina;
import com.code.aon.payroll.resultados.nomina.Nominadev;
import com.code.aon.payroll.resultados.salarios.Finidto;
import com.code.aon.payroll.resultados.salarios.Finindem;
import com.code.aon.payroll.resultados.salarios.Finipext;
import com.code.aon.payroll.resultados.salarios.Finiquito;
import com.code.aon.payroll.resultados.salarios.Nomdtoex;
import com.code.aon.payroll.resultados.salarios.Nominaex;
import com.code.aon.payroll.resultados.seguros.Lintc2;
import com.code.aon.payroll.resultados.seguros.Tc1;
import com.code.aon.payroll.resultados.seguros.Tc2;
import com.code.aon.payroll.tipos.Autorizacion;
import com.code.aon.payroll.tipos.Documento;
import com.code.aon.payroll.tipos.Empresario;
import com.code.aon.payroll.tipos.Incidencia;
import com.code.aon.payroll.tipos.Registro;
import com.code.aon.payroll.tipos.TipoCnae;
import com.code.aon.payroll.tipos.TipoCnae2009;
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
				Cnae2009Maestro.class.getName(), 
				Cnae2009.class.getName(),
				Documento.class.getName(),
				Autorizacion.class.getName(),
				Base.class.getName(),
				Linbasec.class.getName(),
				Incidencia.class.getName(),
				Registro.class.getName(),
				Empresario.class.getName(),
				TipoCnae.class.getName(),
				TipoCnae2009.class.getName(),
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
		        Linirpf.class.getName(),
		        Cliente.class.getName(),
		        Domicilio.class.getName(),
		        Cuentas.class.getName(),
		        Avisos.class.getName(),	
		        Empresa.class.getName(),
		        Actividad.class.getName(),
		        Emprdom.class.getName(),
		        Emprlban.class.getName(),
		        Variable.class.getName(),
		        Linvariable.class.getName(),
		        Percepcion.class.getName(),
		        Linpercepcion.class.getName(),
		        Embargo.class.getName(),
		        Trabajador.class.getName(),
		        Otrperc.class.getName(),
		        Autonomos.class.getName(),
		        Nominaex.class.getName(),
		        Nomdtoex.class.getName(),
		        Autbases.class.getName(),
		        Percep.class.getName(),
		        Emprctra.class.getName(),
		        Emprccc.class.getName(),
		        Emprccos.class.getName(),
		        Persona.class.getName(),
		        Impresos11x.class.getName(),
		        Impresos190.class.getName(),
		        LinImpresos190.class.getName(),
		        Httaviso.class.getName(),
		        Httbonificacion.class.getName(),
		        Httcomplemento.class.getName(),
		        Httrabajador.class.getName(),
		        Httincidencia.class.getName(),
		        Finidto.class.getName(),
				Finindem.class.getName(),
				Finipext.class.getName(),
				Finiquito.class.getName(),
		        Nomina.class.getName(),
		        Nominadev.class.getName(),
		        Nomdto.class.getName(),
		        Calculo.class.getName(),
		        Lincalcu.class.getName(),
		        Bonifica.class.getName(),
		        Nominait.class.getName(),
		        Parteit.class.getName(),
		        Parteconf.class.getName(),
		        Prcdivtrab.class.getName(),
		        Tipocont.class.getName(),
		        Trabajo.class.getName(),
		        Trabdto.class.getName(),
		        Trabinci.class.getName(),
		        Tc2.class.getName(),
		        Lintc2.class.getName(),
		        Tc1.class.getName(),
		        Costes.class.getName(),
		        Lbonifica.class.getName(),
		        Lcomunica.class.getName(),
		        Calendario.class.getName()
		        };
		AliasWriter writer = new AliasWriter("com.code.aon.payroll.dao");
		HibernateUtil.getSessionFactory();
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}
