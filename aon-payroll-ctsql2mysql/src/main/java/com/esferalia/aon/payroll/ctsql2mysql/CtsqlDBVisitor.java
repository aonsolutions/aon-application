/********************************************************************
* Copyright (c) 2010, esferalia NETWORKS S.A
*
* The copyright of the computer program herein is the property 
* of esferalia NETWORKS.
*********************************************************************
* The program may be used and/or copied only with the written 
* permission of esferalia NETWORKS, or in accordance with the 
* terms and conditions stipulated in the agreement contract 
* under which the program has been supplied.
*********************************************************************
*/
package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.SQLException;

import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finipextnu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finindem;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cuota;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Sincomun;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Unidades;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Detalle;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Minor_01;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Avisos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszavis;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipovia;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lintc2;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Variables;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lintc2epi;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Remesa_inss;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linprestacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipocnae2009;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cliente;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nsztrab;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Autonomos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipreg;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Comunica;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Epigrafe;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Httincidencia;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Minora;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszpeop;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finidtonu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Pluses;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Httaviso;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszanex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Action_entry;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Basecoti;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lincnae2009;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percniv;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominaexdf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Mutua;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Divisa;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominadev;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszrece;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lincomun;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipcotc2;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszunco;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszotpe;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Otrperc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lincalcu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprban;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Colectivos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabajadores;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finipext;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszfini;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Pagaext;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszcoco;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linporco;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Httrabajador;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipocont;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linbasec;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszcatg;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linocupacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Remesainss;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lincnae;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finindemdf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Entidad;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszreac;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cnae;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Impr190;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Usuario;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finiquitonu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linplus;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Rem_cert_empr;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Embargo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdfdev;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Automat;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszcopa;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Parteitnu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Categoria;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprccos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Porcoti;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Regidocu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Calendar;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Elemirpf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Domicilio;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Masivo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Complevar;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszcere;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cuota_01;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finidto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszrari;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszdomi;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Ocupacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabajo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszconv;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Empresa;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszcont;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cnae2009;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprccc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszepig;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Persona;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Variaciones;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nsztido;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Opercepciones;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprnif;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipboni;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Config;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprdom;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdtoex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lbonifica;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Parteit;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Calen;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Formcont;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Httcomplemento;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominadf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomina;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Exclusion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszpaga;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdfdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszmupa;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tiposdoc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszbolc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszpoco;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipinc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Session;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lcomunica;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linautom;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cuota_20;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Opfile;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tc2;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tc1;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipaut;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Delegacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Sucursal;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Pais;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Convenio;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Action_denied;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Admon;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linelem;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszmest;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Calfiniquito;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cuota_31;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percepcion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linpercepcion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Perfil;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszinci;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszcdtr;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Complemento;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszadmh;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszbase;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finiquito;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Action_favorite;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipocnae;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Prcdivnom;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszilte;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominaex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cuota_48;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominaitnu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Parteconf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipdoc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Elemcoti;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Remesaafi;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Impr11x;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominait;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Httbonificacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Prestaciones;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Autbases;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Rem_cert_empr_det;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprctra;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszprov;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Bonifica;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Minor_48;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percep;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Calculo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Action;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszdcpr;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nivel;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszodet;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Procesos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Remesa_parte_it;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linepigr;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lin190;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Ajustes;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finipextdf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipempr;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprlban;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finiquitodf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszbanc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lin_divisa;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszempr;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linmutua;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Provincia;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Minor_20;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Printers;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Comunidad;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszcala;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabinci;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdfdtoex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Application;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszboni;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszregi;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Costes;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Prcdivtrab;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finidtodf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Datosafi;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Empract;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Minor_31;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finindemnu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linvariables;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszcomp;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linirpf;

public interface CtsqlDBVisitor {
	
	public void visit ( AbstractCtsqlDB ctsqlDB ) 
	throws SQLException;
	

	public void visitFinipextnu(Finipextnu finipextnu)
	throws SQLException;


	public void visitFinindem(Finindem finindem)
	throws SQLException;


	public void visitCuota(Cuota cuota)
	throws SQLException;


	public void visitSincomun(Sincomun sincomun)
	throws SQLException;


	public void visitUnidades(Unidades unidades)
	throws SQLException;


	public void visitDetalle(Detalle detalle)
	throws SQLException;


	public void visitMinor_01(Minor_01 minor_01)
	throws SQLException;


	public void visitAvisos(Avisos avisos)
	throws SQLException;


	public void visitNszavis(Nszavis nszavis)
	throws SQLException;


	public void visitTipovia(Tipovia tipovia)
	throws SQLException;

	public void visitRel_dlg_via(Delegacion delegacion, Tipovia tipovia)
	throws SQLException;

	public void visitRel_cli_via(Cliente cliente, Tipovia tipovia)
	throws SQLException;

	public void visitRel_dom_via(Domicilio domicilio, Tipovia tipovia)
	throws SQLException;

	public void visitRel_per_via(Persona persona, Tipovia tipovia)
	throws SQLException;

	public void visitOpfile_tipovia(Opfile opfile, Tipovia tipovia)
	throws SQLException;

	public void visitJtipvia(Httrabajador httrabajador, Tipovia tipovia)
	throws SQLException;

	public void visitJauttipovia(Autonomos autonomos, Tipovia tipovia)
	throws SQLException;


	public void visitLintc2(Lintc2 lintc2)
	throws SQLException;


	public void visitVariables(Variables variables)
	throws SQLException;

	public void visitRel_var_lin(Linvariables linvariables, Variables variables)
	throws SQLException;


	public void visitLintc2epi(Lintc2epi lintc2epi)
	throws SQLException;


	public void visitRemesa_inss(Remesa_inss remesa_inss)
	throws SQLException;

	public void visitPit_remesa_inss(Remesa_parte_it remesa_parte_it, Remesa_inss remesa_inss)
	throws SQLException;


	public void visitLinprestacion(Linprestacion linprestacion)
	throws SQLException;


	public void visitTipocnae2009(Tipocnae2009 tipocnae2009)
	throws SQLException;


	public void visitCliente(Cliente cliente)
	throws SQLException;

	public void visitRel_emp_cli(Emprnif emprnif, Cliente cliente)
	throws SQLException;

	public void visitRel_dom_cli(Domicilio domicilio, Cliente cliente)
	throws SQLException;

	public void visitEmprdom_cliente(Emprdom emprdom, Cliente cliente)
	throws SQLException;

	public void visitEmprbanc_cliente(Emprban emprban, Cliente cliente)
	throws SQLException;

	public void visitEmprlban_cliente(Emprlban emprlban, Cliente cliente)
	throws SQLException;

	public void visitAvisos_codcli(Avisos avisos, Cliente cliente)
	throws SQLException;

	public void visitRel_var_cli(Variaciones variaciones, Cliente cliente)
	throws SQLException;

	public void visitRegidocu_cliente(Regidocu regidocu, Cliente cliente)
	throws SQLException;


	public void visitNsztrab(Nsztrab nsztrab)
	throws SQLException;


	public void visitAutonomos(Autonomos autonomos)
	throws SQLException;

	public void visitJautautonomos(Autbases autbases, Autonomos autonomos)
	throws SQLException;


	public void visitTipreg(Tipreg tipreg)
	throws SQLException;

	public void visitRegidocu_tipreg(Regidocu regidocu, Tipreg tipreg)
	throws SQLException;


	public void visitComunica(Comunica comunica)
	throws SQLException;

	public void visitLincomun_comunica(Lincomun lincomun, Comunica comunica)
	throws SQLException;


	public void visitEpigrafe(Epigrafe epigrafe)
	throws SQLException;

	public void visitRel_lep_epi(Linepigr linepigr, Epigrafe epigrafe)
	throws SQLException;

	public void visitRel_cat_epi(Categoria categoria, Epigrafe epigrafe)
	throws SQLException;

	public void visitRel_tra_epi(Trabajo trabajo, Epigrafe epigrafe)
	throws SQLException;

	public void visitJepigrafe(Httrabajador httrabajador, Epigrafe epigrafe)
	throws SQLException;

	public void visitRel_cos_epi(Costes costes, Epigrafe epigrafe)
	throws SQLException;


	public void visitHttincidencia(Httincidencia httincidencia)
	throws SQLException;


	public void visitMinora(Minora minora)
	throws SQLException;


	public void visitNszpeop(Nszpeop nszpeop)
	throws SQLException;


	public void visitFinidtonu(Finidtonu finidtonu)
	throws SQLException;


	public void visitPluses(Pluses pluses)
	throws SQLException;

	public void visitRel_lpl_plu(Linplus linplus, Pluses pluses)
	throws SQLException;


	public void visitHttaviso(Httaviso httaviso)
	throws SQLException;


	public void visitNszanex(Nszanex nszanex)
	throws SQLException;


	public void visitAction_entry(Action_entry action_entry)
	throws SQLException;


	public void visitBasecoti(Basecoti basecoti)
	throws SQLException;

	public void visitRel_lba_bas(Linbasec linbasec, Basecoti basecoti)
	throws SQLException;

	public void visitRel_cat_com(Categoria categoria, Basecoti basecoti)
	throws SQLException;

	public void visitRel_tra_bas(Trabajo trabajo, Basecoti basecoti)
	throws SQLException;

	public void visitJtarifa(Httrabajador httrabajador, Basecoti basecoti)
	throws SQLException;

	public void visitRel_cos_bas(Costes costes, Basecoti basecoti)
	throws SQLException;


	public void visitLincnae2009(Lincnae2009 lincnae2009)
	throws SQLException;


	public void visitPercniv(Percniv percniv)
	throws SQLException;


	public void visitNominaexdf(Nominaexdf nominaexdf)
	throws SQLException;

	public void visitRel_nominaexdf(Nomdfdtoex nomdfdtoex, Nominaexdf nominaexdf)
	throws SQLException;


	public void visitMutua(Mutua mutua)
	throws SQLException;

	public void visitRel_ccc_mut(Emprccc emprccc, Mutua mutua)
	throws SQLException;

	public void visitRel_mut_lin(Linmutua linmutua, Mutua mutua)
	throws SQLException;

	public void visitJautmutua(Autonomos autonomos, Mutua mutua)
	throws SQLException;


	public void visitDivisa(Divisa divisa)
	throws SQLException;

	public void visitLin_divisa_divisa(Lin_divisa lin_divisa, Divisa divisa)
	throws SQLException;

	public void visitLin_divisa_divisa2(Lin_divisa lin_divisa, Divisa divisa)
	throws SQLException;

	public void visitNominaexdf_divisa(Nominaexdf nominaexdf, Divisa divisa)
	throws SQLException;

	public void visitRel_pex_divisa(Nominaex nominaex, Divisa divisa)
	throws SQLException;

	public void visitRel_cli_divisa(Cliente cliente, Divisa divisa)
	throws SQLException;

	public void visitRel_emp_divisa(Emprnif emprnif, Divisa divisa)
	throws SQLException;

	public void visitRel_nom_divisa(Nomina nomina, Divisa divisa)
	throws SQLException;

	public void visitFiniquito_divisa(Finiquito finiquito, Divisa divisa)
	throws SQLException;

	public void visitFindf_divisa(Finiquitodf finiquitodf, Divisa divisa)
	throws SQLException;

	public void visitFinnu_divisa(Finiquitonu finiquitonu, Divisa divisa)
	throws SQLException;

	public void visitImpr11x_divisa(Impr11x impr11x, Divisa divisa)
	throws SQLException;

	public void visitImpr190_divisa(Impr190 impr190, Divisa divisa)
	throws SQLException;

	public void visitNominadf_divisa(Nominadf nominadf, Divisa divisa)
	throws SQLException;


	public void visitNominadev(Nominadev nominadev)
	throws SQLException;


	public void visitNszrece(Nszrece nszrece)
	throws SQLException;


	public void visitLincomun(Lincomun lincomun)
	throws SQLException;


	public void visitTipcotc2(Tipcotc2 tipcotc2)
	throws SQLException;

	public void visitRel_tra_tc2(Trabajo trabajo, Tipcotc2 tipcotc2)
	throws SQLException;

	public void visitJcontratotc2(Httrabajador httrabajador, Tipcotc2 tipcotc2)
	throws SQLException;


	public void visitNszunco(Nszunco nszunco)
	throws SQLException;


	public void visitNszotpe(Nszotpe nszotpe)
	throws SQLException;


	public void visitOtrperc(Otrperc otrperc)
	throws SQLException;


	public void visitLincalcu(Lincalcu lincalcu)
	throws SQLException;


	public void visitEmprban(Emprban emprban)
	throws SQLException;

	public void visitEmprlban_emprban(Emprlban emprlban, Emprban emprban)
	throws SQLException;


	public void visitColectivos(Colectivos colectivos)
	throws SQLException;

	public void visitRel_tra_col(Trabajo trabajo, Colectivos colectivos)
	throws SQLException;


	public void visitTrabajadores(Trabajadores trabajadores)
	throws SQLException;


	public void visitFinipext(Finipext finipext)
	throws SQLException;


	public void visitEmprper(Emprper emprper)
	throws SQLException;

	public void visitTrabajo_emprper(Trabajo trabajo, Emprper emprper)
	throws SQLException;

	public void visitRel_pcp_epp(Percep percep, Emprper emprper)
	throws SQLException;

	public void visitBonifica_emprper(Bonifica bonifica, Emprper emprper)
	throws SQLException;

	public void visitAvisos_emprper(Avisos avisos, Emprper emprper)
	throws SQLException;

	public void visitRel_nom_per(Nomina nomina, Emprper emprper)
	throws SQLException;

	public void visitTrabinci_emprper(Trabinci trabinci, Emprper emprper)
	throws SQLException;

	public void visitNominait_emprper(Nominait nominait, Emprper emprper)
	throws SQLException;

	public void visitRel_dto_per(Trabdto trabdto, Emprper emprper)
	throws SQLException;

	public void visitRel_pit_epp(Parteit parteit, Emprper emprper)
	throws SQLException;

	public void visitPitnu_emprper(Parteitnu parteitnu, Emprper emprper)
	throws SQLException;

	public void visitNitnu_emprper(Nominaitnu nominaitnu, Emprper emprper)
	throws SQLException;

	public void visitRel_fin_epp(Finiquito finiquito, Emprper emprper)
	throws SQLException;

	public void visitFindf_emprper(Finiquitodf finiquitodf, Emprper emprper)
	throws SQLException;

	public void visitFinnu_emprper(Finiquitonu finiquitonu, Emprper emprper)
	throws SQLException;

	public void visitComunica_emprper(Comunica comunica, Emprper emprper)
	throws SQLException;

	public void visitCalculo_emprper(Calculo calculo, Emprper emprper)
	throws SQLException;

	public void visitNominadf_emprper(Nominadf nominadf, Emprper emprper)
	throws SQLException;

	public void visitEmbargo_emprper(Embargo embargo, Emprper emprper)
	throws SQLException;

	public void visitRel_pex_per(Nominaex nominaex, Emprper emprper)
	throws SQLException;

	public void visitRegidocu_emprper(Regidocu regidocu, Emprper emprper)
	throws SQLException;

	public void visitPrc_emprper(Prcdivtrab prcdivtrab, Emprper emprper)
	throws SQLException;

	public void visitPit_remesa_emp(Remesa_parte_it remesa_parte_it, Emprper emprper)
	throws SQLException;

	public void visitFk_cert_remesa_emp(Rem_cert_empr_det rem_cert_empr_det, Emprper emprper)
	throws SQLException;


	public void visitNszfini(Nszfini nszfini)
	throws SQLException;


	public void visitPagaext(Pagaext pagaext)
	throws SQLException;


	public void visitNszcoco(Nszcoco nszcoco)
	throws SQLException;


	public void visitLinporco(Linporco linporco)
	throws SQLException;


	public void visitHttrabajador(Httrabajador httrabajador)
	throws SQLException;

	public void visitRel_htt_bon(Httbonificacion httbonificacion, Httrabajador httrabajador)
	throws SQLException;

	public void visitRel_htt_avi(Httaviso httaviso, Httrabajador httrabajador)
	throws SQLException;

	public void visitRel_htt_com(Httcomplemento httcomplemento, Httrabajador httrabajador)
	throws SQLException;

	public void visitRel_htt_inc(Httincidencia httincidencia, Httrabajador httrabajador)
	throws SQLException;


	public void visitTipocont(Tipocont tipocont)
	throws SQLException;

	public void visitRel_tra_cont(Trabajo trabajo, Tipocont tipocont)
	throws SQLException;

	public void visitJcontrato(Httrabajador httrabajador, Tipocont tipocont)
	throws SQLException;


	public void visitLinbasec(Linbasec linbasec)
	throws SQLException;


	public void visitNszcatg(Nszcatg nszcatg)
	throws SQLException;


	public void visitLinocupacion(Linocupacion linocupacion)
	throws SQLException;


	public void visitRemesainss(Remesainss remesainss)
	throws SQLException;


	public void visitLincnae(Lincnae lincnae)
	throws SQLException;


	public void visitFinindemdf(Finindemdf finindemdf)
	throws SQLException;


	public void visitEntidad(Entidad entidad)
	throws SQLException;

	public void visitSucursal_entidad(Sucursal sucursal, Entidad entidad)
	throws SQLException;

	public void visitEmprbanc_entidad(Emprban emprban, Entidad entidad)
	throws SQLException;

	public void visitRel_tra_ent(Trabajo trabajo, Entidad entidad)
	throws SQLException;

	public void visitJentidad(Httrabajador httrabajador, Entidad entidad)
	throws SQLException;

	public void visitJautentidad(Autonomos autonomos, Entidad entidad)
	throws SQLException;


	public void visitNomdto(Nomdto nomdto)
	throws SQLException;


	public void visitNszreac(Nszreac nszreac)
	throws SQLException;


	public void visitCnae(Cnae cnae)
	throws SQLException;

	public void visitJlincnae(Lincnae lincnae, Cnae cnae)
	throws SQLException;


	public void visitImpr190(Impr190 impr190)
	throws SQLException;

	public void visitLin190_impr190(Lin190 lin190, Impr190 impr190)
	throws SQLException;


	public void visitUsuario(Usuario usuario)
	throws SQLException;

	public void visitFk_user(Session session, Usuario usuario)
	throws SQLException;

	public void visitFk_af_user(Action_favorite action_favorite, Usuario usuario)
	throws SQLException;

	public void visitFk_ad_user(Action_denied action_denied, Usuario usuario)
	throws SQLException;


	public void visitFiniquitonu(Finiquitonu finiquitonu)
	throws SQLException;

	public void visitFinexnu_finnu(Finipextnu finipextnu, Finiquitonu finiquitonu)
	throws SQLException;

	public void visitFinidnu_finnu(Finindemnu finindemnu, Finiquitonu finiquitonu)
	throws SQLException;

	public void visitFindtonu_finnu(Finidtonu finidtonu, Finiquitonu finiquitonu)
	throws SQLException;


	public void visitLinplus(Linplus linplus)
	throws SQLException;


	public void visitRem_cert_empr(Rem_cert_empr rem_cert_empr)
	throws SQLException;

	public void visitFk_rem_cert_empr(Rem_cert_empr_det rem_cert_empr_det, Rem_cert_empr rem_cert_empr)
	throws SQLException;


	public void visitEmbargo(Embargo embargo)
	throws SQLException;


	public void visitNomdfdev(Nomdfdev nomdfdev)
	throws SQLException;


	public void visitAutomat(Automat automat)
	throws SQLException;

	public void visitLinautom_automati(Linautom linautom, Automat automat)
	throws SQLException;


	public void visitNszcopa(Nszcopa nszcopa)
	throws SQLException;


	public void visitParteitnu(Parteitnu parteitnu)
	throws SQLException;


	public void visitCategoria(Categoria categoria)
	throws SQLException;


	public void visitEmprccos(Emprccos emprccos)
	throws SQLException;

	public void visitRel_epp_cco(Emprper emprper, Emprccos emprccos)
	throws SQLException;


	public void visitPorcoti(Porcoti porcoti)
	throws SQLException;

	public void visitRel_lpc_pct(Linporco linporco, Porcoti porcoti)
	throws SQLException;

	public void visitRel_tco_codpct(Tipocont tipocont, Porcoti porcoti)
	throws SQLException;

	public void visitRel_tra_pct(Trabajo trabajo, Porcoti porcoti)
	throws SQLException;

	public void visitRel_cos_pct(Costes costes, Porcoti porcoti)
	throws SQLException;


	public void visitRegidocu(Regidocu regidocu)
	throws SQLException;


	public void visitCalendar(Calendar calendar)
	throws SQLException;


	public void visitNacion(Nacion nacion)
	throws SQLException;

	public void visitRel_per_nac(Persona persona, Nacion nacion)
	throws SQLException;


	public void visitElemirpf(Elemirpf elemirpf)
	throws SQLException;

	public void visitLinirpf_elemirpf(Linirpf linirpf, Elemirpf elemirpf)
	throws SQLException;


	public void visitTrabdto(Trabdto trabdto)
	throws SQLException;


	public void visitDomicilio(Domicilio domicilio)
	throws SQLException;

	public void visitEmprctra_domicilio(Emprctra emprctra, Domicilio domicilio)
	throws SQLException;

	public void visitEmprdom_domicilio(Emprdom emprdom, Domicilio domicilio)
	throws SQLException;

	public void visitEmprper_domiclio(Emprper emprper, Domicilio domicilio)
	throws SQLException;

	public void visitRel_var_dom(Variaciones variaciones, Domicilio domicilio)
	throws SQLException;

	public void visitRel_pre_dom(Prestaciones prestaciones, Domicilio domicilio)
	throws SQLException;

	public void visitJdomicilio(Httrabajador httrabajador, Domicilio domicilio)
	throws SQLException;


	public void visitMasivo(Masivo masivo)
	throws SQLException;


	public void visitComplevar(Complevar complevar)
	throws SQLException;


	public void visitNszcere(Nszcere nszcere)
	throws SQLException;


	public void visitCuota_01(Cuota_01 cuota_01)
	throws SQLException;


	public void visitFinidto(Finidto finidto)
	throws SQLException;


	public void visitNszrari(Nszrari nszrari)
	throws SQLException;


	public void visitNszdomi(Nszdomi nszdomi)
	throws SQLException;


	public void visitOcupacion(Ocupacion ocupacion)
	throws SQLException;

	public void visitJlinocupacion(Linocupacion linocupacion, Ocupacion ocupacion)
	throws SQLException;


	public void visitTrabajo(Trabajo trabajo)
	throws SQLException;


	public void visitNszconv(Nszconv nszconv)
	throws SQLException;


	public void visitEmpresa(Empresa empresa)
	throws SQLException;


	public void visitNszcont(Nszcont nszcont)
	throws SQLException;


	public void visitCnae2009(Cnae2009 cnae2009)
	throws SQLException;

	public void visitJlincnae2009(Lincnae2009 lincnae2009, Cnae2009 cnae2009)
	throws SQLException;


	public void visitEmprccc(Emprccc emprccc)
	throws SQLException;

	public void visitRel_epp_ccc(Emprper emprper, Emprccc emprccc)
	throws SQLException;

	public void visitFormcont_emprccc(Formcont formcont, Emprccc emprccc)
	throws SQLException;


	public void visitNszepig(Nszepig nszepig)
	throws SQLException;


	public void visitPersona(Persona persona)
	throws SQLException;

	public void visitRel_epp_per(Emprper emprper, Persona persona)
	throws SQLException;

	public void visitOtrperc_persona(Otrperc otrperc, Persona persona)
	throws SQLException;

	public void visitLintc2_persona(Lintc2 lintc2, Persona persona)
	throws SQLException;

	public void visitJautpersona(Autonomos autonomos, Persona persona)
	throws SQLException;


	public void visitVariaciones(Variaciones variaciones)
	throws SQLException;


	public void visitNsztido(Nsztido nsztido)
	throws SQLException;


	public void visitOpercepciones(Opercepciones opercepciones)
	throws SQLException;


	public void visitEmprnif(Emprnif emprnif)
	throws SQLException;

	public void visitEmpract_emprnif(Empract empract, Emprnif emprnif)
	throws SQLException;

	public void visitEmprctra_emprnif(Emprctra emprctra, Emprnif emprnif)
	throws SQLException;

	public void visitEmprdom_emprnif(Emprdom emprdom, Emprnif emprnif)
	throws SQLException;

	public void visitEmprlban_emprnif(Emprlban emprlban, Emprnif emprnif)
	throws SQLException;

	public void visitRel_epp_emp(Emprper emprper, Emprnif emprnif)
	throws SQLException;

	public void visitOtrperc_emprnif(Otrperc otrperc, Emprnif emprnif)
	throws SQLException;

	public void visitAvisos_codemp(Avisos avisos, Emprnif emprnif)
	throws SQLException;

	public void visitImpr11x_emprnif(Impr11x impr11x, Emprnif emprnif)
	throws SQLException;

	public void visitImpr190_codemp(Impr190 impr190, Emprnif emprnif)
	throws SQLException;

	public void visitImpr190_repres(Impr190 impr190, Emprnif emprnif)
	throws SQLException;

	public void visitImpr190_cargo(Impr190 impr190, Emprnif emprnif)
	throws SQLException;

	public void visitRel_var_emp(Variaciones variaciones, Emprnif emprnif)
	throws SQLException;

	public void visitRegidocu_emprnif(Regidocu regidocu, Emprnif emprnif)
	throws SQLException;

	public void visitFk_cert_rem_empr(Rem_cert_empr rem_cert_empr, Emprnif emprnif)
	throws SQLException;


	public void visitTipboni(Tipboni tipboni)
	throws SQLException;

	public void visitRel_bpe_bon(Bonifica bonifica, Tipboni tipboni)
	throws SQLException;


	public void visitConfig(Config config)
	throws SQLException;


	public void visitEmprdom(Emprdom emprdom)
	throws SQLException;


	public void visitNomdtoex(Nomdtoex nomdtoex)
	throws SQLException;


	public void visitLbonifica(Lbonifica lbonifica)
	throws SQLException;


	public void visitParteit(Parteit parteit)
	throws SQLException;

	public void visitParteconf_parteit(Parteconf parteconf, Parteit parteit)
	throws SQLException;


	public void visitCalen(Calen calen)
	throws SQLException;


	public void visitFormcont(Formcont formcont)
	throws SQLException;


	public void visitHttcomplemento(Httcomplemento httcomplemento)
	throws SQLException;


	public void visitNominadf(Nominadf nominadf)
	throws SQLException;

	public void visitNomdfdev_nominadf(Nomdfdev nomdfdev, Nominadf nominadf)
	throws SQLException;

	public void visitNomdfdto_nominadf(Nomdfdto nomdfdto, Nominadf nominadf)
	throws SQLException;


	public void visitNomina(Nomina nomina)
	throws SQLException;

	public void visitRel_dto_nom(Nomdto nomdto, Nomina nomina)
	throws SQLException;

	public void visitRel_nmd_nom(Nominadev nominadev, Nomina nomina)
	throws SQLException;

	public void visitPrc_nomina(Prcdivnom prcdivnom, Nomina nomina)
	throws SQLException;


	public void visitExclusion(Exclusion exclusion)
	throws SQLException;


	public void visitNszpaga(Nszpaga nszpaga)
	throws SQLException;


	public void visitNomdfdto(Nomdfdto nomdfdto)
	throws SQLException;


	public void visitNszmupa(Nszmupa nszmupa)
	throws SQLException;


	public void visitTiposdoc(Tiposdoc tiposdoc)
	throws SQLException;


	public void visitNszbolc(Nszbolc nszbolc)
	throws SQLException;


	public void visitNszpoco(Nszpoco nszpoco)
	throws SQLException;


	public void visitTipinc(Tipinc tipinc)
	throws SQLException;

	public void visitRel_inc_tip(Trabinci trabinci, Tipinc tipinc)
	throws SQLException;


	public void visitSession(Session session)
	throws SQLException;

	public void visitFk_ae_session(Action_entry action_entry, Session session)
	throws SQLException;


	public void visitLcomunica(Lcomunica lcomunica)
	throws SQLException;


	public void visitLinautom(Linautom linautom)
	throws SQLException;


	public void visitCuota_20(Cuota_20 cuota_20)
	throws SQLException;


	public void visitOpfile(Opfile opfile)
	throws SQLException;


	public void visitTc2(Tc2 tc2)
	throws SQLException;

	public void visitLintc2epi_tc2(Lintc2epi lintc2epi, Tc2 tc2)
	throws SQLException;

	public void visitLintc2_tc2(Lintc2 lintc2, Tc2 tc2)
	throws SQLException;


	public void visitTc1(Tc1 tc1)
	throws SQLException;


	public void visitTipaut(Tipaut tipaut)
	throws SQLException;

	public void visitRel_tra_aut(Trabajo trabajo, Tipaut tipaut)
	throws SQLException;


	public void visitDelegacion(Delegacion delegacion)
	throws SQLException;

	public void visitCliente_delegacion(Cliente cliente, Delegacion delegacion)
	throws SQLException;


	public void visitSucursal(Sucursal sucursal)
	throws SQLException;

	public void visitEmprbanc_sucursal(Emprban emprban, Sucursal sucursal)
	throws SQLException;

	public void visitRel_tra_suc(Trabajo trabajo, Sucursal sucursal)
	throws SQLException;

	public void visitJsucursal(Httrabajador httrabajador, Sucursal sucursal)
	throws SQLException;

	public void visitJautsucursal(Autonomos autonomos, Sucursal sucursal)
	throws SQLException;


	public void visitPais(Pais pais)
	throws SQLException;

	public void visitRel_com_pai(Comunidad comunidad, Pais pais)
	throws SQLException;

	public void visitRel_cli_pai(Cliente cliente, Pais pais)
	throws SQLException;

	public void visitRel_emp_pai(Emprnif emprnif, Pais pais)
	throws SQLException;

	public void visitRel_emp_pai2(Emprnif emprnif, Pais pais)
	throws SQLException;

	public void visitRel_per_pem(Persona persona, Pais pais)
	throws SQLException;

	public void visitRel_per_pna(Persona persona, Pais pais)
	throws SQLException;

	public void visitJpaiemi(Httrabajador httrabajador, Pais pais)
	throws SQLException;


	public void visitConvenio(Convenio convenio)
	throws SQLException;

	public void visitRel_pga_con(Pagaext pagaext, Convenio convenio)
	throws SQLException;

	public void visitRel_niv_con(Nivel nivel, Convenio convenio)
	throws SQLException;

	public void visitRel_cat_con(Categoria categoria, Convenio convenio)
	throws SQLException;

	public void visitRel_pcn_con(Percniv percniv, Convenio convenio)
	throws SQLException;

	public void visitEmpract_convenio(Empract empract, Convenio convenio)
	throws SQLException;

	public void visitEmprctra_convenio(Emprctra emprctra, Convenio convenio)
	throws SQLException;

	public void visitRel_tra_con(Trabajo trabajo, Convenio convenio)
	throws SQLException;


	public void visitAction_denied(Action_denied action_denied)
	throws SQLException;


	public void visitAdmon(Admon admon)
	throws SQLException;

	public void visitRel_emp_adm(Emprnif emprnif, Admon admon)
	throws SQLException;

	public void visitImpr11x_admon(Impr11x impr11x, Admon admon)
	throws SQLException;

	public void visitImpr190_admon(Impr190 impr190, Admon admon)
	throws SQLException;


	public void visitLinelem(Linelem linelem)
	throws SQLException;


	public void visitNszmest(Nszmest nszmest)
	throws SQLException;


	public void visitCalfiniquito(Calfiniquito calfiniquito)
	throws SQLException;


	public void visitCuota_31(Cuota_31 cuota_31)
	throws SQLException;


	public void visitPercepcion(Percepcion percepcion)
	throws SQLException;

	public void visitRel_per_lin(Linpercepcion linpercepcion, Percepcion percepcion)
	throws SQLException;


	public void visitLinpercepcion(Linpercepcion linpercepcion)
	throws SQLException;


	public void visitPerfil(Perfil perfil)
	throws SQLException;


	public void visitNszinci(Nszinci nszinci)
	throws SQLException;


	public void visitNszcdtr(Nszcdtr nszcdtr)
	throws SQLException;


	public void visitComplemento(Complemento complemento)
	throws SQLException;

	public void visitRel_pga_com(Pagaext pagaext, Complemento complemento)
	throws SQLException;

	public void visitRel_pcn_com(Percniv percniv, Complemento complemento)
	throws SQLException;

	public void visitRel_pcn_cap(Percniv percniv, Complemento complemento)
	throws SQLException;

	public void visitRel_pex_com(Nominaex nominaex, Complemento complemento)
	throws SQLException;

	public void visitRel_pcp_com(Percep percep, Complemento complemento)
	throws SQLException;

	public void visitRel_pcp_comapl(Percep percep, Complemento complemento)
	throws SQLException;

	public void visitRel_fpe_com(Finipext finipext, Complemento complemento)
	throws SQLException;

	public void visitFinexdf_com(Finipextdf finipextdf, Complemento complemento)
	throws SQLException;

	public void visitFinexnu_com(Finipextnu finipextnu, Complemento complemento)
	throws SQLException;

	public void visitRel_lpl_com(Linplus linplus, Complemento complemento)
	throws SQLException;


	public void visitNszadmh(Nszadmh nszadmh)
	throws SQLException;


	public void visitNszbase(Nszbase nszbase)
	throws SQLException;


	public void visitFiniquito(Finiquito finiquito)
	throws SQLException;

	public void visitRel_fpe_fin(Finipext finipext, Finiquito finiquito)
	throws SQLException;

	public void visitRel_fii_fin(Finindem finindem, Finiquito finiquito)
	throws SQLException;

	public void visitRel_fid_fin(Finidto finidto, Finiquito finiquito)
	throws SQLException;


	public void visitAction_favorite(Action_favorite action_favorite)
	throws SQLException;


	public void visitTipocnae(Tipocnae tipocnae)
	throws SQLException;


	public void visitPrcdivnom(Prcdivnom prcdivnom)
	throws SQLException;


	public void visitNszilte(Nszilte nszilte)
	throws SQLException;


	public void visitNominaex(Nominaex nominaex)
	throws SQLException;

	public void visitNomdtoex_nominaex(Nomdtoex nomdtoex, Nominaex nominaex)
	throws SQLException;


	public void visitCuota_48(Cuota_48 cuota_48)
	throws SQLException;


	public void visitNominaitnu(Nominaitnu nominaitnu)
	throws SQLException;


	public void visitParteconf(Parteconf parteconf)
	throws SQLException;


	public void visitTipdoc(Tipdoc tipdoc)
	throws SQLException;

	public void visitRel_cli_doc(Cliente cliente, Tipdoc tipdoc)
	throws SQLException;

	public void visitRel_emp_doc(Emprnif emprnif, Tipdoc tipdoc)
	throws SQLException;

	public void visitRel_emp_doc2(Emprnif emprnif, Tipdoc tipdoc)
	throws SQLException;

	public void visitRel_per_doc(Persona persona, Tipdoc tipdoc)
	throws SQLException;

	public void visitComunica_tipdoc(Comunica comunica, Tipdoc tipdoc)
	throws SQLException;

	public void visitJinddoc(Httrabajador httrabajador, Tipdoc tipdoc)
	throws SQLException;


	public void visitElemcoti(Elemcoti elemcoti)
	throws SQLException;

	public void visitRel_lel_ele(Linelem linelem, Elemcoti elemcoti)
	throws SQLException;


	public void visitRemesaafi(Remesaafi remesaafi)
	throws SQLException;


	public void visitImpr11x(Impr11x impr11x)
	throws SQLException;


	public void visitNominait(Nominait nominait)
	throws SQLException;


	public void visitHttbonificacion(Httbonificacion httbonificacion)
	throws SQLException;


	public void visitPrestaciones(Prestaciones prestaciones)
	throws SQLException;

	public void visitRel_lpr_pre(Linprestacion linprestacion, Prestaciones prestaciones)
	throws SQLException;


	public void visitAutbases(Autbases autbases)
	throws SQLException;


	public void visitRem_cert_empr_det(Rem_cert_empr_det rem_cert_empr_det)
	throws SQLException;


	public void visitEmprctra(Emprctra emprctra)
	throws SQLException;

	public void visitRel_cal_ctra(Calendar calendar, Emprctra emprctra)
	throws SQLException;


	public void visitNszprov(Nszprov nszprov)
	throws SQLException;


	public void visitBonifica(Bonifica bonifica)
	throws SQLException;


	public void visitMinor_48(Minor_48 minor_48)
	throws SQLException;


	public void visitPercep(Percep percep)
	throws SQLException;


	public void visitCalculo(Calculo calculo)
	throws SQLException;


	public void visitAction(Action action)
	throws SQLException;

	public void visitFk_af_action(Action_favorite action_favorite, Action action)
	throws SQLException;

	public void visitFk_ae_action(Action_entry action_entry, Action action)
	throws SQLException;

	public void visitFk_ad_action(Action_denied action_denied, Action action)
	throws SQLException;


	public void visitNszdcpr(Nszdcpr nszdcpr)
	throws SQLException;


	public void visitNivel(Nivel nivel)
	throws SQLException;

	public void visitPercniv_nivel(Percniv percniv, Nivel nivel)
	throws SQLException;


	public void visitNszodet(Nszodet nszodet)
	throws SQLException;


	public void visitProcesos(Procesos procesos)
	throws SQLException;


	public void visitRemesa_parte_it(Remesa_parte_it remesa_parte_it)
	throws SQLException;


	public void visitLinepigr(Linepigr linepigr)
	throws SQLException;


	public void visitLin190(Lin190 lin190)
	throws SQLException;


	public void visitAjustes(Ajustes ajustes)
	throws SQLException;


	public void visitFinipextdf(Finipextdf finipextdf)
	throws SQLException;


	public void visitTipempr(Tipempr tipempr)
	throws SQLException;

	public void visitRel_cli_emp(Cliente cliente, Tipempr tipempr)
	throws SQLException;

	public void visitRel_emp_epr(Emprnif emprnif, Tipempr tipempr)
	throws SQLException;


	public void visitEmprlban(Emprlban emprlban)
	throws SQLException;


	public void visitFiniquitodf(Finiquitodf finiquitodf)
	throws SQLException;

	public void visitFinexdf_findf(Finipextdf finipextdf, Finiquitodf finiquitodf)
	throws SQLException;

	public void visitFiniddf_findf(Finindemdf finindemdf, Finiquitodf finiquitodf)
	throws SQLException;

	public void visitFindtodf_findf(Finidtodf finidtodf, Finiquitodf finiquitodf)
	throws SQLException;


	public void visitNszbanc(Nszbanc nszbanc)
	throws SQLException;


	public void visitLin_divisa(Lin_divisa lin_divisa)
	throws SQLException;


	public void visitNszempr(Nszempr nszempr)
	throws SQLException;


	public void visitLinmutua(Linmutua linmutua)
	throws SQLException;


	public void visitProvincia(Provincia provincia)
	throws SQLException;

	public void visitRel_dlg_pro(Delegacion delegacion, Provincia provincia)
	throws SQLException;

	public void visitRel_cli_pro(Cliente cliente, Provincia provincia)
	throws SQLException;

	public void visitRel_dom_pro(Domicilio domicilio, Provincia provincia)
	throws SQLException;

	public void visitRel_per_prd(Persona persona, Provincia provincia)
	throws SQLException;

	public void visitRel_per_prn(Persona persona, Provincia provincia)
	throws SQLException;

	public void visitImpr11x_provincia(Impr11x impr11x, Provincia provincia)
	throws SQLException;

	public void visitImpr190_provincia(Impr190 impr190, Provincia provincia)
	throws SQLException;

	public void visitOpfile_provincia(Opfile opfile, Provincia provincia)
	throws SQLException;

	public void visitJpronac(Httrabajador httrabajador, Provincia provincia)
	throws SQLException;

	public void visitJprovincia(Httrabajador httrabajador, Provincia provincia)
	throws SQLException;

	public void visitJautprovincia(Autonomos autonomos, Provincia provincia)
	throws SQLException;


	public void visitMinor_20(Minor_20 minor_20)
	throws SQLException;


	public void visitPrinters(Printers printers)
	throws SQLException;


	public void visitComunidad(Comunidad comunidad)
	throws SQLException;

	public void visitProvincia_comunida(Provincia provincia, Comunidad comunidad)
	throws SQLException;


	public void visitNszcala(Nszcala nszcala)
	throws SQLException;


	public void visitTrabinci(Trabinci trabinci)
	throws SQLException;


	public void visitNomdfdtoex(Nomdfdtoex nomdfdtoex)
	throws SQLException;


	public void visitApplication(Application application)
	throws SQLException;

	public void visitFk_action_app(Action action, Application application)
	throws SQLException;

	public void visitFk_application(Session session, Application application)
	throws SQLException;


	public void visitNszboni(Nszboni nszboni)
	throws SQLException;


	public void visitNszregi(Nszregi nszregi)
	throws SQLException;


	public void visitCostes(Costes costes)
	throws SQLException;

	public void visitLcomunica_costes(Lcomunica lcomunica, Costes costes)
	throws SQLException;

	public void visitLbonifica_costes(Lbonifica lbonifica, Costes costes)
	throws SQLException;


	public void visitPrcdivtrab(Prcdivtrab prcdivtrab)
	throws SQLException;


	public void visitFinidtodf(Finidtodf finidtodf)
	throws SQLException;


	public void visitDatosafi(Datosafi datosafi)
	throws SQLException;


	public void visitEmpract(Empract empract)
	throws SQLException;

	public void visitEmprctra_empract(Emprctra emprctra, Empract empract)
	throws SQLException;

	public void visitEmprccc_empract(Emprccc emprccc, Empract empract)
	throws SQLException;

	public void visitRel_cco_act(Emprccos emprccos, Empract empract)
	throws SQLException;

	public void visitEmprdom_empract(Emprdom emprdom, Empract empract)
	throws SQLException;

	public void visitEmprlban_empract(Emprlban emprlban, Empract empract)
	throws SQLException;

	public void visitRel_epp_act(Emprper emprper, Empract empract)
	throws SQLException;

	public void visitAvisos_empract(Avisos avisos, Empract empract)
	throws SQLException;

	public void visitRel_var_act(Variaciones variaciones, Empract empract)
	throws SQLException;

	public void visitRegidocu_empract(Regidocu regidocu, Empract empract)
	throws SQLException;

	public void visitJactividad(Httrabajador httrabajador, Empract empract)
	throws SQLException;


	public void visitMinor_31(Minor_31 minor_31)
	throws SQLException;


	public void visitFinindemnu(Finindemnu finindemnu)
	throws SQLException;


	public void visitLinvariables(Linvariables linvariables)
	throws SQLException;


	public void visitNszcomp(Nszcomp nszcomp)
	throws SQLException;


	public void visitLinirpf(Linirpf linirpf)
	throws SQLException;

}
