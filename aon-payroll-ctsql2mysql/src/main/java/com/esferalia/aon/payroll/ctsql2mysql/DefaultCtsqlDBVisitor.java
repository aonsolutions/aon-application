package com.esferalia.aon.payroll.ctsql2mysql;


import java.sql.SQLException;

import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Action;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Action_denied;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Action_entry;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Action_favorite;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Admon;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Ajustes;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Application;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Autbases;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Automat;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Autonomos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Avisos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Basecoti;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Bonifica;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Calculo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Calen;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Calendar;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Calfiniquito;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Categoria;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cliente;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cnae;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cnae2009;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Colectivos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Complemento;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Complevar;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Comunica;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Comunidad;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Config;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Convenio;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Costes;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cuota;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cuota_01;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cuota_20;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cuota_31;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cuota_48;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Datosafi;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Delegacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Detalle;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Divisa;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Domicilio;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Elemcoti;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Elemirpf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Embargo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Empract;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprban;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprccc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprccos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprctra;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprdom;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Empresa;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprlban;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprnif;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Entidad;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Epigrafe;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Exclusion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finidto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finidtodf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finidtonu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finindem;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finindemdf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finindemnu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finipext;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finipextdf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finipextnu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finiquito;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finiquitodf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finiquitonu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Httaviso;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Httbonificacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Httcomplemento;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Httincidencia;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Httrabajador;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Impr11x;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Impr190;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lbonifica;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lcomunica;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lin190;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lin_divisa;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linautom;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linbasec;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lincalcu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lincnae;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lincnae2009;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lincomun;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linelem;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linepigr;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linirpf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linmutua;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linocupacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linpercepcion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linplus;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linporco;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linprestacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lintc2;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lintc2epi;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linvariables;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Masivo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Minor_01;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Minor_20;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Minor_31;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Minor_48;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Minora;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Mutua;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nivel;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdfdev;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdfdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdfdtoex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdtoex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomina;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominadev;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominadf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominaex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominaexdf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominait;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominaitnu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszadmh;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszanex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszavis;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszbanc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszbase;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszbolc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszboni;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszcala;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszcatg;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszcdtr;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszcere;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszcoco;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszcomp;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszcont;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszconv;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszcopa;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszdcpr;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszdomi;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszempr;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszepig;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszfini;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszilte;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszinci;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszmest;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszmupa;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszodet;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszotpe;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszpaga;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszpeop;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszpoco;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszprov;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszrari;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszreac;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszrece;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszregi;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nsztido;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nsztrab;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nszunco;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Ocupacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Opercepciones;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Opfile;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Otrperc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Pagaext;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Pais;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Parteconf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Parteit;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Parteitnu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percep;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percepcion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percniv;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Perfil;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Persona;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Pluses;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Porcoti;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Prcdivnom;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Prcdivtrab;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Prestaciones;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Printers;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Procesos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Provincia;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Regidocu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Rem_cert_empr;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Rem_cert_empr_det;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Remesa_inss;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Remesa_parte_it;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Remesaafi;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Remesainss;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Session;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Sincomun;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Sucursal;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tc1;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tc2;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipaut;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipboni;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipcotc2;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipdoc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipempr;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipinc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipocnae;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipocnae2009;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipocont;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tiposdoc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipovia;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipreg;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabajadores;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabajo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabinci;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Unidades;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Usuario;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Variables;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Variaciones;

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

public class DefaultCtsqlDBVisitor implements CtsqlDBVisitor {
	
	public boolean visit ( CtsqlDB ctsqlDB ) 
	throws SQLException	{
		return true;
	};
	
	public boolean visitAction(Action action)
	throws SQLException{
		return true;
	}

	public boolean visitAction_denied(Action_denied action_denied, Action action)
	throws SQLException {
		return true;
	}

	public boolean visitAction_denied(Action_denied action_denied)
	throws SQLException{
		return true;
	}

	public boolean visitAction_entry(Action_entry action_entry)
	throws SQLException{
		return true;
	}

	public boolean visitAction_favorite(Action_favorite action_favorite)
	throws SQLException{
		return true;
	}

	public boolean visitAdmon(Admon admon)
	throws SQLException{
		return true;
	}

	public boolean visitEmprnif(Emprnif emprnif, Admon admon)
	throws SQLException {
		return true;
	}

	public boolean visitImpr11x(Impr11x impr11x, Admon admon)
	throws SQLException {
		return true;
	}

	public boolean visitImpr190(Impr190 impr190, Admon admon)
	throws SQLException {
		return true;
	}

	public boolean visitAjustes(Ajustes ajustes)
	throws SQLException{
		return true;
	}

	public boolean visitApplication(Application application)
	throws SQLException{
		return true;
	}

	public boolean visitSession(Session session, Application application)
	throws SQLException {
		return true;
	}

	public boolean visitAction(Action action, Application application)
	throws SQLException {
		return true;
	}

	public boolean visitAutbases(Autbases autbases)
	throws SQLException{
		return true;
	}

	public boolean visitAutomat(Automat automat)
	throws SQLException{
		return true;
	}

	public boolean visitLinautom(Linautom linautom, Automat automat)
	throws SQLException {
		return true;
	}

	public boolean visitAutonomos(Autonomos autonomos)
	throws SQLException{
		return true;
	}

	public boolean visitAutbases(Autbases autbases, Autonomos autonomos)
	throws SQLException {
		return true;
	}

	public boolean visitAvisos(Avisos avisos)
	throws SQLException{
		return true;
	}

	public boolean visitBasecoti(Basecoti basecoti)
	throws SQLException{
		return true;
	}

	public boolean visitLinbasec(Linbasec linbasec, Basecoti basecoti)
	throws SQLException {
		return true;
	}

	public boolean visitCategoria(Categoria categoria, Basecoti basecoti)
	throws SQLException {
		return true;
	}

	public boolean visitTrabajo(Trabajo trabajo, Basecoti basecoti)
	throws SQLException {
		return true;
	}

	public boolean visitHttrabajador(Httrabajador httrabajador, Basecoti basecoti)
	throws SQLException {
		return true;
	}

	public boolean visitCostes(Costes costes, Basecoti basecoti)
	throws SQLException {
		return true;
	}

	public boolean visitBonifica(Bonifica bonifica)
	throws SQLException{
		return true;
	}

	public boolean visitCalculo(Calculo calculo)
	throws SQLException{
		return true;
	}

	public boolean visitCalen(Calen calen)
	throws SQLException{
		return true;
	}

	public boolean visitCalendar(Calendar calendar)
	throws SQLException{
		return true;
	}

	public boolean visitCalfiniquito(Calfiniquito calfiniquito)
	throws SQLException{
		return true;
	}

	public boolean visitCategoria(Categoria categoria)
	throws SQLException{
		return true;
	}

	public boolean visitCliente(Cliente cliente)
	throws SQLException{
		return true;
	}

	public boolean visitEmprnif(Emprnif emprnif, Cliente cliente)
	throws SQLException {
		return true;
	}

	public boolean visitDomicilio(Domicilio domicilio, Cliente cliente)
	throws SQLException {
		return true;
	}

	public boolean visitEmprdom(Emprdom emprdom, Cliente cliente)
	throws SQLException {
		return true;
	}

	public boolean visitEmprban(Emprban emprban, Cliente cliente)
	throws SQLException {
		return true;
	}

	public boolean visitEmprlban(Emprlban emprlban, Cliente cliente)
	throws SQLException {
		return true;
	}

	public boolean visitAvisos(Avisos avisos, Cliente cliente)
	throws SQLException {
		return true;
	}

	public boolean visitVariaciones(Variaciones variaciones, Cliente cliente)
	throws SQLException {
		return true;
	}

	public boolean visitRegidocu(Regidocu regidocu, Cliente cliente)
	throws SQLException {
		return true;
	}

	public boolean visitCnae(Cnae cnae)
	throws SQLException{
		return true;
	}

	public boolean visitLincnae(Lincnae lincnae, Cnae cnae)
	throws SQLException {
		return true;
	}

	public boolean visitCnae2009(Cnae2009 cnae2009)
	throws SQLException{
		return true;
	}

	public boolean visitLincnae2009(Lincnae2009 lincnae2009, Cnae2009 cnae2009)
	throws SQLException {
		return true;
	}

	public boolean visitColectivos(Colectivos colectivos)
	throws SQLException{
		return true;
	}

	public boolean visitTrabajo(Trabajo trabajo, Colectivos colectivos)
	throws SQLException {
		return true;
	}

	public boolean visitComplemento(Complemento complemento)
	throws SQLException{
		return true;
	}

	public boolean visitPagaext(Pagaext pagaext, Complemento complemento)
	throws SQLException {
		return true;
	}

	public boolean visitPercniv(Percniv percniv, Complemento complemento)
	throws SQLException {
		return true;
	}

	public boolean visitNominaex(Nominaex nominaex, Complemento complemento)
	throws SQLException {
		return true;
	}

	public boolean visitPercep(Percep percep, Complemento complemento)
	throws SQLException {
		return true;
	}

	public boolean visitFinipext(Finipext finipext, Complemento complemento)
	throws SQLException {
		return true;
	}

	public boolean visitFinipextdf(Finipextdf finipextdf, Complemento complemento)
	throws SQLException {
		return true;
	}

	public boolean visitFinipextnu(Finipextnu finipextnu, Complemento complemento)
	throws SQLException {
		return true;
	}

	public boolean visitLinplus(Linplus linplus, Complemento complemento)
	throws SQLException {
		return true;
	}

	public boolean visitComplevar(Complevar complevar)
	throws SQLException{
		return true;
	}

	public boolean visitComunica(Comunica comunica)
	throws SQLException{
		return true;
	}

	public boolean visitLincomun(Lincomun lincomun, Comunica comunica)
	throws SQLException {
		return true;
	}

	public boolean visitComunidad(Comunidad comunidad)
	throws SQLException{
		return true;
	}

	public boolean visitProvincia(Provincia provincia, Comunidad comunidad)
	throws SQLException {
		return true;
	}

	public boolean visitConfig(Config config)
	throws SQLException{
		return true;
	}

	public boolean visitConvenio(Convenio convenio)
	throws SQLException{
		return true;
	}

	public boolean visitPagaext(Pagaext pagaext, Convenio convenio)
	throws SQLException {
		return true;
	}

	public boolean visitNivel(Nivel nivel, Convenio convenio)
	throws SQLException {
		return true;
	}

	public boolean visitCategoria(Categoria categoria, Convenio convenio)
	throws SQLException {
		return true;
	}

	public boolean visitPercniv(Percniv percniv, Convenio convenio)
	throws SQLException {
		return true;
	}

	public boolean visitEmpract(Empract empract, Convenio convenio)
	throws SQLException {
		return true;
	}

	public boolean visitEmprctra(Emprctra emprctra, Convenio convenio)
	throws SQLException {
		return true;
	}

	public boolean visitTrabajo(Trabajo trabajo, Convenio convenio)
	throws SQLException {
		return true;
	}

	public boolean visitCostes(Costes costes)
	throws SQLException{
		return true;
	}

	public boolean visitLcomunica(Lcomunica lcomunica, Costes costes)
	throws SQLException {
		return true;
	}

	public boolean visitLbonifica(Lbonifica lbonifica, Costes costes)
	throws SQLException {
		return true;
	}

	public boolean visitCuota(Cuota cuota)
	throws SQLException{
		return true;
	}

	public boolean visitCuota_01(Cuota_01 cuota_01)
	throws SQLException{
		return true;
	}

	public boolean visitCuota_20(Cuota_20 cuota_20)
	throws SQLException{
		return true;
	}

	public boolean visitCuota_31(Cuota_31 cuota_31)
	throws SQLException{
		return true;
	}

	public boolean visitCuota_48(Cuota_48 cuota_48)
	throws SQLException{
		return true;
	}

	public boolean visitDatosafi(Datosafi datosafi)
	throws SQLException{
		return true;
	}

	public boolean visitDelegacion(Delegacion delegacion)
	throws SQLException{
		return true;
	}

	public boolean visitCliente(Cliente cliente, Delegacion delegacion)
	throws SQLException {
		return true;
	}

	public boolean visitDetalle(Detalle detalle)
	throws SQLException{
		return true;
	}

	public boolean visitDivisa(Divisa divisa)
	throws SQLException{
		return true;
	}

	public boolean visitLin_divisa(Lin_divisa lin_divisa, Divisa divisa)
	throws SQLException {
		return true;
	}

	public boolean visitNominaexdf(Nominaexdf nominaexdf, Divisa divisa)
	throws SQLException {
		return true;
	}

	public boolean visitNominaex(Nominaex nominaex, Divisa divisa)
	throws SQLException {
		return true;
	}

	public boolean visitCliente(Cliente cliente, Divisa divisa)
	throws SQLException {
		return true;
	}

	public boolean visitEmprnif(Emprnif emprnif, Divisa divisa)
	throws SQLException {
		return true;
	}

	public boolean visitNomina(Nomina nomina, Divisa divisa)
	throws SQLException {
		return true;
	}

	public boolean visitFiniquito(Finiquito finiquito, Divisa divisa)
	throws SQLException {
		return true;
	}

	public boolean visitFiniquitodf(Finiquitodf finiquitodf, Divisa divisa)
	throws SQLException {
		return true;
	}

	public boolean visitFiniquitonu(Finiquitonu finiquitonu, Divisa divisa)
	throws SQLException {
		return true;
	}

	public boolean visitImpr11x(Impr11x impr11x, Divisa divisa)
	throws SQLException {
		return true;
	}

	public boolean visitImpr190(Impr190 impr190, Divisa divisa)
	throws SQLException {
		return true;
	}

	public boolean visitNominadf(Nominadf nominadf, Divisa divisa)
	throws SQLException {
		return true;
	}

	public boolean visitDomicilio(Domicilio domicilio)
	throws SQLException{
		return true;
	}

	public boolean visitEmprctra(Emprctra emprctra, Domicilio domicilio)
	throws SQLException {
		return true;
	}

	public boolean visitEmprdom(Emprdom emprdom, Domicilio domicilio)
	throws SQLException {
		return true;
	}

	public boolean visitEmprper(Emprper emprper, Domicilio domicilio)
	throws SQLException {
		return true;
	}

	public boolean visitVariaciones(Variaciones variaciones, Domicilio domicilio)
	throws SQLException {
		return true;
	}

	public boolean visitPrestaciones(Prestaciones prestaciones, Domicilio domicilio)
	throws SQLException {
		return true;
	}

	public boolean visitHttrabajador(Httrabajador httrabajador, Domicilio domicilio)
	throws SQLException {
		return true;
	}

	public boolean visitElemcoti(Elemcoti elemcoti)
	throws SQLException{
		return true;
	}

	public boolean visitLinelem(Linelem linelem, Elemcoti elemcoti)
	throws SQLException {
		return true;
	}

	public boolean visitElemirpf(Elemirpf elemirpf)
	throws SQLException{
		return true;
	}

	public boolean visitLinirpf(Linirpf linirpf, Elemirpf elemirpf)
	throws SQLException {
		return true;
	}

	public boolean visitEmbargo(Embargo embargo)
	throws SQLException{
		return true;
	}

	public boolean visitEmpract(Empract empract)
	throws SQLException{
		return true;
	}

	public boolean visitEmprctra(Emprctra emprctra, Empract empract)
	throws SQLException {
		return true;
	}

	public boolean visitEmprccc(Emprccc emprccc, Empract empract)
	throws SQLException {
		return true;
	}

	public boolean visitEmprccos(Emprccos emprccos, Empract empract)
	throws SQLException {
		return true;
	}

	public boolean visitEmprdom(Emprdom emprdom, Empract empract)
	throws SQLException {
		return true;
	}

	public boolean visitEmprlban(Emprlban emprlban, Empract empract)
	throws SQLException {
		return true;
	}

	public boolean visitEmprper(Emprper emprper, Empract empract)
	throws SQLException {
		return true;
	}

	public boolean visitAvisos(Avisos avisos, Empract empract)
	throws SQLException {
		return true;
	}

	public boolean visitVariaciones(Variaciones variaciones, Empract empract)
	throws SQLException {
		return true;
	}

	public boolean visitRegidocu(Regidocu regidocu, Empract empract)
	throws SQLException {
		return true;
	}

	public boolean visitHttrabajador(Httrabajador httrabajador, Empract empract)
	throws SQLException {
		return true;
	}

	public boolean visitEmprban(Emprban emprban)
	throws SQLException{
		return true;
	}

	public boolean visitEmprlban(Emprlban emprlban, Emprban emprban)
	throws SQLException {
		return true;
	}

	public boolean visitEmprccc(Emprccc emprccc)
	throws SQLException{
		return true;
	}

	public boolean visitEmprper(Emprper emprper, Emprccc emprccc)
	throws SQLException {
		return true;
	}

	public boolean visitEmprccos(Emprccos emprccos)
	throws SQLException{
		return true;
	}

	public boolean visitEmprper(Emprper emprper, Emprccos emprccos)
	throws SQLException {
		return true;
	}

	public boolean visitEmprctra(Emprctra emprctra)
	throws SQLException{
		return true;
	}

	public boolean visitEmprdom(Emprdom emprdom)
	throws SQLException{
		return true;
	}

	public boolean visitEmpresa(Empresa empresa)
	throws SQLException{
		return true;
	}

	public boolean visitEmprlban(Emprlban emprlban)
	throws SQLException{
		return true;
	}

	public boolean visitEmprnif(Emprnif emprnif)
	throws SQLException{
		return true;
	}

	public boolean visitEmpract(Empract empract, Emprnif emprnif)
	throws SQLException {
		return true;
	}

	public boolean visitEmprctra(Emprctra emprctra, Emprnif emprnif)
	throws SQLException {
		return true;
	}

	public boolean visitEmprdom(Emprdom emprdom, Emprnif emprnif)
	throws SQLException {
		return true;
	}

	public boolean visitEmprlban(Emprlban emprlban, Emprnif emprnif)
	throws SQLException {
		return true;
	}

	public boolean visitEmprper(Emprper emprper, Emprnif emprnif)
	throws SQLException {
		return true;
	}

	public boolean visitOtrperc(Otrperc otrperc, Emprnif emprnif)
	throws SQLException {
		return true;
	}

	public boolean visitAvisos(Avisos avisos, Emprnif emprnif)
	throws SQLException {
		return true;
	}

	public boolean visitImpr11x(Impr11x impr11x, Emprnif emprnif)
	throws SQLException {
		return true;
	}

	public boolean visitImpr190(Impr190 impr190, Emprnif emprnif)
	throws SQLException {
		return true;
	}

	public boolean visitVariaciones(Variaciones variaciones, Emprnif emprnif)
	throws SQLException {
		return true;
	}

	public boolean visitRegidocu(Regidocu regidocu, Emprnif emprnif)
	throws SQLException {
		return true;
	}

	public boolean visitRem_cert_empr(Rem_cert_empr rem_cert_empr, Emprnif emprnif)
	throws SQLException {
		return true;
	}

	public boolean visitEmprper(Emprper emprper)
	throws SQLException{
		return true;
	}

	public boolean visitTrabajo(Trabajo trabajo, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitPercep(Percep percep, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitBonifica(Bonifica bonifica, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitAvisos(Avisos avisos, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitNomina(Nomina nomina, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitTrabinci(Trabinci trabinci, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitNominait(Nominait nominait, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitTrabdto(Trabdto trabdto, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitParteit(Parteit parteit, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitParteitnu(Parteitnu parteitnu, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitNominaitnu(Nominaitnu nominaitnu, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitFiniquito(Finiquito finiquito, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitFiniquitodf(Finiquitodf finiquitodf, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitFiniquitonu(Finiquitonu finiquitonu, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitComunica(Comunica comunica, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitCalculo(Calculo calculo, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitNominadf(Nominadf nominadf, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitEmbargo(Embargo embargo, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitNominaex(Nominaex nominaex, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitRegidocu(Regidocu regidocu, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitPrcdivtrab(Prcdivtrab prcdivtrab, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitRemesa_parte_it(Remesa_parte_it remesa_parte_it, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitRem_cert_empr_det(Rem_cert_empr_det rem_cert_empr_det, Emprper emprper)
	throws SQLException {
		return true;
	}

	public boolean visitEntidad(Entidad entidad)
	throws SQLException{
		return true;
	}

	public boolean visitSucursal(Sucursal sucursal, Entidad entidad)
	throws SQLException {
		return true;
	}

	public boolean visitEmprban(Emprban emprban, Entidad entidad)
	throws SQLException {
		return true;
	}

	public boolean visitTrabajo(Trabajo trabajo, Entidad entidad)
	throws SQLException {
		return true;
	}

	public boolean visitHttrabajador(Httrabajador httrabajador, Entidad entidad)
	throws SQLException {
		return true;
	}

	public boolean visitAutonomos(Autonomos autonomos, Entidad entidad)
	throws SQLException {
		return true;
	}

	public boolean visitEpigrafe(Epigrafe epigrafe)
	throws SQLException{
		return true;
	}

	public boolean visitLinepigr(Linepigr linepigr, Epigrafe epigrafe)
	throws SQLException {
		return true;
	}

	public boolean visitCategoria(Categoria categoria, Epigrafe epigrafe)
	throws SQLException {
		return true;
	}

	public boolean visitTrabajo(Trabajo trabajo, Epigrafe epigrafe)
	throws SQLException {
		return true;
	}

	public boolean visitHttrabajador(Httrabajador httrabajador, Epigrafe epigrafe)
	throws SQLException {
		return true;
	}

	public boolean visitCostes(Costes costes, Epigrafe epigrafe)
	throws SQLException {
		return true;
	}

	public boolean visitExclusion(Exclusion exclusion)
	throws SQLException{
		return true;
	}

	public boolean visitFinidto(Finidto finidto)
	throws SQLException{
		return true;
	}

	public boolean visitFinidtodf(Finidtodf finidtodf)
	throws SQLException{
		return true;
	}

	public boolean visitFinidtonu(Finidtonu finidtonu)
	throws SQLException{
		return true;
	}

	public boolean visitFinindem(Finindem finindem)
	throws SQLException{
		return true;
	}

	public boolean visitFinindemdf(Finindemdf finindemdf)
	throws SQLException{
		return true;
	}

	public boolean visitFinindemnu(Finindemnu finindemnu)
	throws SQLException{
		return true;
	}

	public boolean visitFinipext(Finipext finipext)
	throws SQLException{
		return true;
	}

	public boolean visitFinipextdf(Finipextdf finipextdf)
	throws SQLException{
		return true;
	}

	public boolean visitFinipextnu(Finipextnu finipextnu)
	throws SQLException{
		return true;
	}

	public boolean visitFiniquito(Finiquito finiquito)
	throws SQLException{
		return true;
	}

	public boolean visitFinipext(Finipext finipext, Finiquito finiquito)
	throws SQLException {
		return true;
	}

	public boolean visitFinindem(Finindem finindem, Finiquito finiquito)
	throws SQLException {
		return true;
	}

	public boolean visitFinidto(Finidto finidto, Finiquito finiquito)
	throws SQLException {
		return true;
	}

	public boolean visitFiniquitodf(Finiquitodf finiquitodf)
	throws SQLException{
		return true;
	}

	public boolean visitFinipextdf(Finipextdf finipextdf, Finiquitodf finiquitodf)
	throws SQLException {
		return true;
	}

	public boolean visitFinindemdf(Finindemdf finindemdf, Finiquitodf finiquitodf)
	throws SQLException {
		return true;
	}

	public boolean visitFinidtodf(Finidtodf finidtodf, Finiquitodf finiquitodf)
	throws SQLException {
		return true;
	}

	public boolean visitFiniquitonu(Finiquitonu finiquitonu)
	throws SQLException{
		return true;
	}

	public boolean visitFinipextnu(Finipextnu finipextnu, Finiquitonu finiquitonu)
	throws SQLException {
		return true;
	}

	public boolean visitFinindemnu(Finindemnu finindemnu, Finiquitonu finiquitonu)
	throws SQLException {
		return true;
	}

	public boolean visitFinidtonu(Finidtonu finidtonu, Finiquitonu finiquitonu)
	throws SQLException {
		return true;
	}

	public boolean visitHttaviso(Httaviso httaviso)
	throws SQLException{
		return true;
	}

	public boolean visitHttbonificacion(Httbonificacion httbonificacion)
	throws SQLException{
		return true;
	}

	public boolean visitHttcomplemento(Httcomplemento httcomplemento)
	throws SQLException{
		return true;
	}

	public boolean visitHttincidencia(Httincidencia httincidencia)
	throws SQLException{
		return true;
	}

	public boolean visitHttrabajador(Httrabajador httrabajador)
	throws SQLException{
		return true;
	}

	public boolean visitHttbonificacion(Httbonificacion httbonificacion, Httrabajador httrabajador)
	throws SQLException {
		return true;
	}

	public boolean visitHttaviso(Httaviso httaviso, Httrabajador httrabajador)
	throws SQLException {
		return true;
	}

	public boolean visitHttcomplemento(Httcomplemento httcomplemento, Httrabajador httrabajador)
	throws SQLException {
		return true;
	}

	public boolean visitHttincidencia(Httincidencia httincidencia, Httrabajador httrabajador)
	throws SQLException {
		return true;
	}

	public boolean visitImpr11x(Impr11x impr11x)
	throws SQLException{
		return true;
	}

	public boolean visitImpr190(Impr190 impr190)
	throws SQLException{
		return true;
	}

	public boolean visitLin190(Lin190 lin190, Impr190 impr190)
	throws SQLException {
		return true;
	}

	public boolean visitLbonifica(Lbonifica lbonifica)
	throws SQLException{
		return true;
	}

	public boolean visitLcomunica(Lcomunica lcomunica)
	throws SQLException{
		return true;
	}

	public boolean visitLin190(Lin190 lin190)
	throws SQLException{
		return true;
	}

	public boolean visitLin_divisa(Lin_divisa lin_divisa)
	throws SQLException{
		return true;
	}

	public boolean visitLinautom(Linautom linautom)
	throws SQLException{
		return true;
	}

	public boolean visitLinbasec(Linbasec linbasec)
	throws SQLException{
		return true;
	}

	public boolean visitLincalcu(Lincalcu lincalcu)
	throws SQLException{
		return true;
	}

	public boolean visitLincnae(Lincnae lincnae)
	throws SQLException{
		return true;
	}

	public boolean visitLincnae2009(Lincnae2009 lincnae2009)
	throws SQLException{
		return true;
	}

	public boolean visitLincomun(Lincomun lincomun)
	throws SQLException{
		return true;
	}

	public boolean visitLinelem(Linelem linelem)
	throws SQLException{
		return true;
	}

	public boolean visitLinepigr(Linepigr linepigr)
	throws SQLException{
		return true;
	}

	public boolean visitLinirpf(Linirpf linirpf)
	throws SQLException{
		return true;
	}

	public boolean visitLinmutua(Linmutua linmutua)
	throws SQLException{
		return true;
	}

	public boolean visitLinocupacion(Linocupacion linocupacion)
	throws SQLException{
		return true;
	}

	public boolean visitLinpercepcion(Linpercepcion linpercepcion)
	throws SQLException{
		return true;
	}

	public boolean visitLinplus(Linplus linplus)
	throws SQLException{
		return true;
	}

	public boolean visitLinporco(Linporco linporco)
	throws SQLException{
		return true;
	}

	public boolean visitLinprestacion(Linprestacion linprestacion)
	throws SQLException{
		return true;
	}

	public boolean visitLintc2(Lintc2 lintc2)
	throws SQLException{
		return true;
	}

	public boolean visitLintc2epi(Lintc2epi lintc2epi)
	throws SQLException{
		return true;
	}

	public boolean visitLinvariables(Linvariables linvariables)
	throws SQLException{
		return true;
	}

	public boolean visitMasivo(Masivo masivo)
	throws SQLException{
		return true;
	}

	public boolean visitMinor_01(Minor_01 minor_01)
	throws SQLException{
		return true;
	}

	public boolean visitMinor_20(Minor_20 minor_20)
	throws SQLException{
		return true;
	}

	public boolean visitMinor_31(Minor_31 minor_31)
	throws SQLException{
		return true;
	}

	public boolean visitMinor_48(Minor_48 minor_48)
	throws SQLException{
		return true;
	}

	public boolean visitMinora(Minora minora)
	throws SQLException{
		return true;
	}

	public boolean visitMutua(Mutua mutua)
	throws SQLException{
		return true;
	}

	public boolean visitEmprccc(Emprccc emprccc, Mutua mutua)
	throws SQLException {
		return true;
	}

	public boolean visitLinmutua(Linmutua linmutua, Mutua mutua)
	throws SQLException {
		return true;
	}

	public boolean visitAutonomos(Autonomos autonomos, Mutua mutua)
	throws SQLException {
		return true;
	}

	public boolean visitNacion(Nacion nacion)
	throws SQLException{
		return true;
	}

	public boolean visitPersona(Persona persona, Nacion nacion)
	throws SQLException {
		return true;
	}

	public boolean visitNivel(Nivel nivel)
	throws SQLException{
		return true;
	}

	public boolean visitPercniv(Percniv percniv, Nivel nivel)
	throws SQLException {
		return true;
	}

	public boolean visitNomdfdev(Nomdfdev nomdfdev)
	throws SQLException{
		return true;
	}

	public boolean visitNomdfdto(Nomdfdto nomdfdto)
	throws SQLException{
		return true;
	}

	public boolean visitNomdfdtoex(Nomdfdtoex nomdfdtoex)
	throws SQLException{
		return true;
	}

	public boolean visitNomdto(Nomdto nomdto)
	throws SQLException{
		return true;
	}

	public boolean visitNomdtoex(Nomdtoex nomdtoex)
	throws SQLException{
		return true;
	}

	public boolean visitNomina(Nomina nomina)
	throws SQLException{
		return true;
	}

	public boolean visitNomdto(Nomdto nomdto, Nomina nomina)
	throws SQLException {
		return true;
	}

	public boolean visitNominadev(Nominadev nominadev, Nomina nomina)
	throws SQLException {
		return true;
	}

	public boolean visitPrcdivnom(Prcdivnom prcdivnom, Nomina nomina)
	throws SQLException {
		return true;
	}

	public boolean visitNominadev(Nominadev nominadev)
	throws SQLException{
		return true;
	}

	public boolean visitNominadf(Nominadf nominadf)
	throws SQLException{
		return true;
	}

	public boolean visitNomdfdev(Nomdfdev nomdfdev, Nominadf nominadf)
	throws SQLException {
		return true;
	}

	public boolean visitNomdfdto(Nomdfdto nomdfdto, Nominadf nominadf)
	throws SQLException {
		return true;
	}

	public boolean visitNominaex(Nominaex nominaex)
	throws SQLException{
		return true;
	}

	public boolean visitNomdtoex(Nomdtoex nomdtoex, Nominaex nominaex)
	throws SQLException {
		return true;
	}

	public boolean visitNominaexdf(Nominaexdf nominaexdf)
	throws SQLException{
		return true;
	}

	public boolean visitNomdfdtoex(Nomdfdtoex nomdfdtoex, Nominaexdf nominaexdf)
	throws SQLException {
		return true;
	}

	public boolean visitNominait(Nominait nominait)
	throws SQLException{
		return true;
	}

	public boolean visitNominaitnu(Nominaitnu nominaitnu)
	throws SQLException{
		return true;
	}

	public boolean visitNszadmh(Nszadmh nszadmh)
	throws SQLException{
		return true;
	}

	public boolean visitNszanex(Nszanex nszanex)
	throws SQLException{
		return true;
	}

	public boolean visitNszavis(Nszavis nszavis)
	throws SQLException{
		return true;
	}

	public boolean visitNszbanc(Nszbanc nszbanc)
	throws SQLException{
		return true;
	}

	public boolean visitNszbase(Nszbase nszbase)
	throws SQLException{
		return true;
	}

	public boolean visitNszbolc(Nszbolc nszbolc)
	throws SQLException{
		return true;
	}

	public boolean visitNszboni(Nszboni nszboni)
	throws SQLException{
		return true;
	}

	public boolean visitNszcala(Nszcala nszcala)
	throws SQLException{
		return true;
	}

	public boolean visitNszcatg(Nszcatg nszcatg)
	throws SQLException{
		return true;
	}

	public boolean visitNszcdtr(Nszcdtr nszcdtr)
	throws SQLException{
		return true;
	}

	public boolean visitNszcere(Nszcere nszcere)
	throws SQLException{
		return true;
	}

	public boolean visitNszcoco(Nszcoco nszcoco)
	throws SQLException{
		return true;
	}

	public boolean visitNszcomp(Nszcomp nszcomp)
	throws SQLException{
		return true;
	}

	public boolean visitNszcont(Nszcont nszcont)
	throws SQLException{
		return true;
	}

	public boolean visitNszconv(Nszconv nszconv)
	throws SQLException{
		return true;
	}

	public boolean visitNszcopa(Nszcopa nszcopa)
	throws SQLException{
		return true;
	}

	public boolean visitNszdcpr(Nszdcpr nszdcpr)
	throws SQLException{
		return true;
	}

	public boolean visitNszdomi(Nszdomi nszdomi)
	throws SQLException{
		return true;
	}

	public boolean visitNszempr(Nszempr nszempr)
	throws SQLException{
		return true;
	}

	public boolean visitNszepig(Nszepig nszepig)
	throws SQLException{
		return true;
	}

	public boolean visitNszfini(Nszfini nszfini)
	throws SQLException{
		return true;
	}

	public boolean visitNszilte(Nszilte nszilte)
	throws SQLException{
		return true;
	}

	public boolean visitNszinci(Nszinci nszinci)
	throws SQLException{
		return true;
	}

	public boolean visitNszmest(Nszmest nszmest)
	throws SQLException{
		return true;
	}

	public boolean visitNszmupa(Nszmupa nszmupa)
	throws SQLException{
		return true;
	}

	public boolean visitNszodet(Nszodet nszodet)
	throws SQLException{
		return true;
	}

	public boolean visitNszotpe(Nszotpe nszotpe)
	throws SQLException{
		return true;
	}

	public boolean visitNszpaga(Nszpaga nszpaga)
	throws SQLException{
		return true;
	}

	public boolean visitNszpeop(Nszpeop nszpeop)
	throws SQLException{
		return true;
	}

	public boolean visitNszpoco(Nszpoco nszpoco)
	throws SQLException{
		return true;
	}

	public boolean visitNszprov(Nszprov nszprov)
	throws SQLException{
		return true;
	}

	public boolean visitNszrari(Nszrari nszrari)
	throws SQLException{
		return true;
	}

	public boolean visitNszreac(Nszreac nszreac)
	throws SQLException{
		return true;
	}

	public boolean visitNszrece(Nszrece nszrece)
	throws SQLException{
		return true;
	}

	public boolean visitNszregi(Nszregi nszregi)
	throws SQLException{
		return true;
	}

	public boolean visitNsztido(Nsztido nsztido)
	throws SQLException{
		return true;
	}

	public boolean visitNsztrab(Nsztrab nsztrab)
	throws SQLException{
		return true;
	}

	public boolean visitNszunco(Nszunco nszunco)
	throws SQLException{
		return true;
	}

	public boolean visitOcupacion(Ocupacion ocupacion)
	throws SQLException{
		return true;
	}

	public boolean visitLinocupacion(Linocupacion linocupacion, Ocupacion ocupacion)
	throws SQLException {
		return true;
	}

	public boolean visitOpercepciones(Opercepciones opercepciones)
	throws SQLException{
		return true;
	}

	public boolean visitOpfile(Opfile opfile)
	throws SQLException{
		return true;
	}

	public boolean visitOtrperc(Otrperc otrperc)
	throws SQLException{
		return true;
	}

	public boolean visitPagaext(Pagaext pagaext)
	throws SQLException{
		return true;
	}

	public boolean visitPais(Pais pais)
	throws SQLException{
		return true;
	}

	public boolean visitComunidad(Comunidad comunidad, Pais pais)
	throws SQLException {
		return true;
	}

	public boolean visitCliente(Cliente cliente, Pais pais)
	throws SQLException {
		return true;
	}

	public boolean visitEmprnif(Emprnif emprnif, Pais pais)
	throws SQLException {
		return true;
	}

	public boolean visitPersona(Persona persona, Pais pais)
	throws SQLException {
		return true;
	}

	public boolean visitHttrabajador(Httrabajador httrabajador, Pais pais)
	throws SQLException {
		return true;
	}

	public boolean visitParteconf(Parteconf parteconf)
	throws SQLException{
		return true;
	}

	public boolean visitParteit(Parteit parteit)
	throws SQLException{
		return true;
	}

	public boolean visitParteconf(Parteconf parteconf, Parteit parteit)
	throws SQLException {
		return true;
	}

	public boolean visitParteitnu(Parteitnu parteitnu)
	throws SQLException{
		return true;
	}

	public boolean visitPercep(Percep percep)
	throws SQLException{
		return true;
	}

	public boolean visitPercepcion(Percepcion percepcion)
	throws SQLException{
		return true;
	}

	public boolean visitLinpercepcion(Linpercepcion linpercepcion, Percepcion percepcion)
	throws SQLException {
		return true;
	}

	public boolean visitPercniv(Percniv percniv)
	throws SQLException{
		return true;
	}

	public boolean visitPerfil(Perfil perfil)
	throws SQLException{
		return true;
	}

	public boolean visitPersona(Persona persona)
	throws SQLException{
		return true;
	}

	public boolean visitEmprper(Emprper emprper, Persona persona)
	throws SQLException {
		return true;
	}

	public boolean visitOtrperc(Otrperc otrperc, Persona persona)
	throws SQLException {
		return true;
	}

	public boolean visitLintc2(Lintc2 lintc2, Persona persona)
	throws SQLException {
		return true;
	}

	public boolean visitAutonomos(Autonomos autonomos, Persona persona)
	throws SQLException {
		return true;
	}

	public boolean visitPluses(Pluses pluses)
	throws SQLException{
		return true;
	}

	public boolean visitLinplus(Linplus linplus, Pluses pluses)
	throws SQLException {
		return true;
	}

	public boolean visitPorcoti(Porcoti porcoti)
	throws SQLException{
		return true;
	}

	public boolean visitLinporco(Linporco linporco, Porcoti porcoti)
	throws SQLException {
		return true;
	}

	public boolean visitTipocont(Tipocont tipocont, Porcoti porcoti)
	throws SQLException {
		return true;
	}

	public boolean visitTrabajo(Trabajo trabajo, Porcoti porcoti)
	throws SQLException {
		return true;
	}

	public boolean visitCostes(Costes costes, Porcoti porcoti)
	throws SQLException {
		return true;
	}

	public boolean visitPrcdivnom(Prcdivnom prcdivnom)
	throws SQLException{
		return true;
	}

	public boolean visitPrcdivtrab(Prcdivtrab prcdivtrab)
	throws SQLException{
		return true;
	}

	public boolean visitPrestaciones(Prestaciones prestaciones)
	throws SQLException{
		return true;
	}

	public boolean visitLinprestacion(Linprestacion linprestacion, Prestaciones prestaciones)
	throws SQLException {
		return true;
	}

	public boolean visitPrinters(Printers printers)
	throws SQLException{
		return true;
	}

	public boolean visitProcesos(Procesos procesos)
	throws SQLException{
		return true;
	}

	public boolean visitProvincia(Provincia provincia)
	throws SQLException{
		return true;
	}

	public boolean visitDelegacion(Delegacion delegacion, Provincia provincia)
	throws SQLException {
		return true;
	}

	public boolean visitCliente(Cliente cliente, Provincia provincia)
	throws SQLException {
		return true;
	}

	public boolean visitDomicilio(Domicilio domicilio, Provincia provincia)
	throws SQLException {
		return true;
	}

	public boolean visitPersona(Persona persona, Provincia provincia)
	throws SQLException {
		return true;
	}

	public boolean visitImpr11x(Impr11x impr11x, Provincia provincia)
	throws SQLException {
		return true;
	}

	public boolean visitImpr190(Impr190 impr190, Provincia provincia)
	throws SQLException {
		return true;
	}

	public boolean visitOpfile(Opfile opfile, Provincia provincia)
	throws SQLException {
		return true;
	}

	public boolean visitHttrabajador(Httrabajador httrabajador, Provincia provincia)
	throws SQLException {
		return true;
	}

	public boolean visitAutonomos(Autonomos autonomos, Provincia provincia)
	throws SQLException {
		return true;
	}

	public boolean visitRegidocu(Regidocu regidocu)
	throws SQLException{
		return true;
	}

	public boolean visitRem_cert_empr(Rem_cert_empr rem_cert_empr)
	throws SQLException{
		return true;
	}

	public boolean visitRem_cert_empr_det(Rem_cert_empr_det rem_cert_empr_det)
	throws SQLException{
		return true;
	}

	public boolean visitRemesa_inss(Remesa_inss remesa_inss)
	throws SQLException{
		return true;
	}

	public boolean visitRemesa_parte_it(Remesa_parte_it remesa_parte_it, Remesa_inss remesa_inss)
	throws SQLException {
		return true;
	}

	public boolean visitRemesa_parte_it(Remesa_parte_it remesa_parte_it)
	throws SQLException{
		return true;
	}

	public boolean visitRemesaafi(Remesaafi remesaafi)
	throws SQLException{
		return true;
	}

	public boolean visitRemesainss(Remesainss remesainss)
	throws SQLException{
		return true;
	}

	public boolean visitSession(Session session)
	throws SQLException{
		return true;
	}

	public boolean visitAction_entry(Action_entry action_entry, Session session)
	throws SQLException {
		return true;
	}

	public boolean visitSincomun(Sincomun sincomun)
	throws SQLException{
		return true;
	}

	public boolean visitSucursal(Sucursal sucursal)
	throws SQLException{
		return true;
	}

	public boolean visitEmprban(Emprban emprban, Sucursal sucursal)
	throws SQLException {
		return true;
	}

	public boolean visitTrabajo(Trabajo trabajo, Sucursal sucursal)
	throws SQLException {
		return true;
	}

	public boolean visitHttrabajador(Httrabajador httrabajador, Sucursal sucursal)
	throws SQLException {
		return true;
	}

	public boolean visitAutonomos(Autonomos autonomos, Sucursal sucursal)
	throws SQLException {
		return true;
	}

	public boolean visitTc1(Tc1 tc1)
	throws SQLException{
		return true;
	}

	public boolean visitTc2(Tc2 tc2)
	throws SQLException{
		return true;
	}

	public boolean visitLintc2epi(Lintc2epi lintc2epi, Tc2 tc2)
	throws SQLException {
		return true;
	}

	public boolean visitLintc2(Lintc2 lintc2, Tc2 tc2)
	throws SQLException {
		return true;
	}

	public boolean visitTipaut(Tipaut tipaut)
	throws SQLException{
		return true;
	}

	public boolean visitTrabajo(Trabajo trabajo, Tipaut tipaut)
	throws SQLException {
		return true;
	}

	public boolean visitTipboni(Tipboni tipboni)
	throws SQLException{
		return true;
	}

	public boolean visitBonifica(Bonifica bonifica, Tipboni tipboni)
	throws SQLException {
		return true;
	}

	public boolean visitTipcotc2(Tipcotc2 tipcotc2)
	throws SQLException{
		return true;
	}

	public boolean visitTrabajo(Trabajo trabajo, Tipcotc2 tipcotc2)
	throws SQLException {
		return true;
	}

	public boolean visitHttrabajador(Httrabajador httrabajador, Tipcotc2 tipcotc2)
	throws SQLException {
		return true;
	}

	public boolean visitTipdoc(Tipdoc tipdoc)
	throws SQLException{
		return true;
	}

	public boolean visitCliente(Cliente cliente, Tipdoc tipdoc)
	throws SQLException {
		return true;
	}

	public boolean visitEmprnif(Emprnif emprnif, Tipdoc tipdoc)
	throws SQLException {
		return true;
	}

	public boolean visitPersona(Persona persona, Tipdoc tipdoc)
	throws SQLException {
		return true;
	}

	public boolean visitComunica(Comunica comunica, Tipdoc tipdoc)
	throws SQLException {
		return true;
	}

	public boolean visitHttrabajador(Httrabajador httrabajador, Tipdoc tipdoc)
	throws SQLException {
		return true;
	}

	public boolean visitTipempr(Tipempr tipempr)
	throws SQLException{
		return true;
	}

	public boolean visitCliente(Cliente cliente, Tipempr tipempr)
	throws SQLException {
		return true;
	}

	public boolean visitEmprnif(Emprnif emprnif, Tipempr tipempr)
	throws SQLException {
		return true;
	}

	public boolean visitTipinc(Tipinc tipinc)
	throws SQLException{
		return true;
	}

	public boolean visitTrabinci(Trabinci trabinci, Tipinc tipinc)
	throws SQLException {
		return true;
	}

	public boolean visitTipocnae(Tipocnae tipocnae)
	throws SQLException{
		return true;
	}

	public boolean visitTipocnae2009(Tipocnae2009 tipocnae2009)
	throws SQLException{
		return true;
	}

	public boolean visitTipocont(Tipocont tipocont)
	throws SQLException{
		return true;
	}

	public boolean visitTrabajo(Trabajo trabajo, Tipocont tipocont)
	throws SQLException {
		return true;
	}

	public boolean visitHttrabajador(Httrabajador httrabajador, Tipocont tipocont)
	throws SQLException {
		return true;
	}

	public boolean visitTiposdoc(Tiposdoc tiposdoc)
	throws SQLException{
		return true;
	}

	public boolean visitTipovia(Tipovia tipovia)
	throws SQLException{
		return true;
	}

	public boolean visitDelegacion(Delegacion delegacion, Tipovia tipovia)
	throws SQLException {
		return true;
	}

	public boolean visitCliente(Cliente cliente, Tipovia tipovia)
	throws SQLException {
		return true;
	}

	public boolean visitDomicilio(Domicilio domicilio, Tipovia tipovia)
	throws SQLException {
		return true;
	}

	public boolean visitPersona(Persona persona, Tipovia tipovia)
	throws SQLException {
		return true;
	}

	public boolean visitOpfile(Opfile opfile, Tipovia tipovia)
	throws SQLException {
		return true;
	}

	public boolean visitHttrabajador(Httrabajador httrabajador, Tipovia tipovia)
	throws SQLException {
		return true;
	}

	public boolean visitAutonomos(Autonomos autonomos, Tipovia tipovia)
	throws SQLException {
		return true;
	}

	public boolean visitTipreg(Tipreg tipreg)
	throws SQLException{
		return true;
	}

	public boolean visitRegidocu(Regidocu regidocu, Tipreg tipreg)
	throws SQLException {
		return true;
	}

	public boolean visitTrabajadores(Trabajadores trabajadores)
	throws SQLException{
		return true;
	}

	public boolean visitTrabajo(Trabajo trabajo)
	throws SQLException{
		return true;
	}

	public boolean visitTrabdto(Trabdto trabdto)
	throws SQLException{
		return true;
	}

	public boolean visitTrabinci(Trabinci trabinci)
	throws SQLException{
		return true;
	}

	public boolean visitUnidades(Unidades unidades)
	throws SQLException{
		return true;
	}

	public boolean visitUsuario(Usuario usuario)
	throws SQLException{
		return true;
	}

	public boolean visitSession(Session session, Usuario usuario)
	throws SQLException {
		return true;
	}

	public boolean visitAction_favorite(Action_favorite action_favorite, Usuario usuario)
	throws SQLException {
		return true;
	}

	public boolean visitAction_denied(Action_denied action_denied, Usuario usuario)
	throws SQLException {
		return true;
	}

	public boolean visitVariables(Variables variables)
	throws SQLException{
		return true;
	}

	public boolean visitLinvariables(Linvariables linvariables, Variables variables)
	throws SQLException {
		return true;
	}

	public boolean visitVariaciones(Variaciones variaciones)
	throws SQLException{
		return true;
	}

}
