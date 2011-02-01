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

public interface CtsqlDBVisitor {
	
	public boolean visit ( CtsqlDB ctsqlDB ) throws SQLException;
	

	public boolean visitAction(Action action)throws SQLException;

	public boolean visitAction_denied(Action_denied action_denied, Action action)throws SQLException;


	public boolean visitAction_denied(Action_denied action_denied)throws SQLException;


	public boolean visitAction_entry(Action_entry action_entry)throws SQLException;


	public boolean visitAction_favorite(Action_favorite action_favorite)throws SQLException;


	public boolean visitAdmon(Admon admon)throws SQLException;

	public boolean visitEmprnif(Emprnif emprnif, Admon admon)throws SQLException;

	public boolean visitImpr11x(Impr11x impr11x, Admon admon)throws SQLException;

	public boolean visitImpr190(Impr190 impr190, Admon admon)throws SQLException;


	public boolean visitAjustes(Ajustes ajustes)throws SQLException;


	public boolean visitApplication(Application application)throws SQLException;

	public boolean visitSession(Session session, Application application)throws SQLException;

	public boolean visitAction(Action action, Application application)throws SQLException;


	public boolean visitAutbases(Autbases autbases)throws SQLException;


	public boolean visitAutomat(Automat automat)throws SQLException;

	public boolean visitLinautom(Linautom linautom, Automat automat)throws SQLException;


	public boolean visitAutonomos(Autonomos autonomos)throws SQLException;

	public boolean visitAutbases(Autbases autbases, Autonomos autonomos)throws SQLException;


	public boolean visitAvisos(Avisos avisos)throws SQLException;


	public boolean visitBasecoti(Basecoti basecoti)throws SQLException;

	public boolean visitLinbasec(Linbasec linbasec, Basecoti basecoti)throws SQLException;

	public boolean visitCategoria(Categoria categoria, Basecoti basecoti)throws SQLException;

	public boolean visitTrabajo(Trabajo trabajo, Basecoti basecoti)throws SQLException;

	public boolean visitHttrabajador(Httrabajador httrabajador, Basecoti basecoti)throws SQLException;

	public boolean visitCostes(Costes costes, Basecoti basecoti)throws SQLException;


	public boolean visitBonifica(Bonifica bonifica)throws SQLException;


	public boolean visitCalculo(Calculo calculo)throws SQLException;


	public boolean visitCalen(Calen calen)throws SQLException;


	public boolean visitCalendar(Calendar calendar)throws SQLException;


	public boolean visitCalfiniquito(Calfiniquito calfiniquito)throws SQLException;


	public boolean visitCategoria(Categoria categoria)throws SQLException;


	public boolean visitCliente(Cliente cliente)throws SQLException;

	public boolean visitEmprnif(Emprnif emprnif, Cliente cliente)throws SQLException;

	public boolean visitDomicilio(Domicilio domicilio, Cliente cliente)throws SQLException;

	public boolean visitEmprdom(Emprdom emprdom, Cliente cliente)throws SQLException;

	public boolean visitEmprban(Emprban emprban, Cliente cliente)throws SQLException;

	public boolean visitEmprlban(Emprlban emprlban, Cliente cliente)throws SQLException;

	public boolean visitAvisos(Avisos avisos, Cliente cliente)throws SQLException;

	public boolean visitVariaciones(Variaciones variaciones, Cliente cliente)throws SQLException;

	public boolean visitRegidocu(Regidocu regidocu, Cliente cliente)throws SQLException;


	public boolean visitCnae(Cnae cnae)throws SQLException;

	public boolean visitLincnae(Lincnae lincnae, Cnae cnae)throws SQLException;


	public boolean visitCnae2009(Cnae2009 cnae2009)throws SQLException;

	public boolean visitLincnae2009(Lincnae2009 lincnae2009, Cnae2009 cnae2009)throws SQLException;


	public boolean visitColectivos(Colectivos colectivos)throws SQLException;

	public boolean visitTrabajo(Trabajo trabajo, Colectivos colectivos)throws SQLException;


	public boolean visitComplemento(Complemento complemento)throws SQLException;

	public boolean visitPagaext(Pagaext pagaext, Complemento complemento)throws SQLException;

	public boolean visitPercniv(Percniv percniv, Complemento complemento)throws SQLException;

	public boolean visitNominaex(Nominaex nominaex, Complemento complemento)throws SQLException;

	public boolean visitPercep(Percep percep, Complemento complemento)throws SQLException;

	public boolean visitFinipext(Finipext finipext, Complemento complemento)throws SQLException;

	public boolean visitFinipextdf(Finipextdf finipextdf, Complemento complemento)throws SQLException;

	public boolean visitFinipextnu(Finipextnu finipextnu, Complemento complemento)throws SQLException;

	public boolean visitLinplus(Linplus linplus, Complemento complemento)throws SQLException;


	public boolean visitComplevar(Complevar complevar)throws SQLException;


	public boolean visitComunica(Comunica comunica)throws SQLException;

	public boolean visitLincomun(Lincomun lincomun, Comunica comunica)throws SQLException;


	public boolean visitComunidad(Comunidad comunidad)throws SQLException;

	public boolean visitProvincia(Provincia provincia, Comunidad comunidad)throws SQLException;


	public boolean visitConfig(Config config)throws SQLException;


	public boolean visitConvenio(Convenio convenio)throws SQLException;

	public boolean visitPagaext(Pagaext pagaext, Convenio convenio)throws SQLException;

	public boolean visitNivel(Nivel nivel, Convenio convenio)throws SQLException;

	public boolean visitCategoria(Categoria categoria, Convenio convenio)throws SQLException;

	public boolean visitPercniv(Percniv percniv, Convenio convenio)throws SQLException;

	public boolean visitEmpract(Empract empract, Convenio convenio)throws SQLException;

	public boolean visitEmprctra(Emprctra emprctra, Convenio convenio)throws SQLException;

	public boolean visitTrabajo(Trabajo trabajo, Convenio convenio)throws SQLException;


	public boolean visitCostes(Costes costes)throws SQLException;

	public boolean visitLcomunica(Lcomunica lcomunica, Costes costes)throws SQLException;

	public boolean visitLbonifica(Lbonifica lbonifica, Costes costes)throws SQLException;


	public boolean visitCuota(Cuota cuota)throws SQLException;


	public boolean visitCuota_01(Cuota_01 cuota_01)throws SQLException;


	public boolean visitCuota_20(Cuota_20 cuota_20)throws SQLException;


	public boolean visitCuota_31(Cuota_31 cuota_31)throws SQLException;


	public boolean visitCuota_48(Cuota_48 cuota_48)throws SQLException;


	public boolean visitDatosafi(Datosafi datosafi)throws SQLException;


	public boolean visitDelegacion(Delegacion delegacion)throws SQLException;

	public boolean visitCliente(Cliente cliente, Delegacion delegacion)throws SQLException;


	public boolean visitDetalle(Detalle detalle)throws SQLException;


	public boolean visitDivisa(Divisa divisa)throws SQLException;

	public boolean visitLin_divisa(Lin_divisa lin_divisa, Divisa divisa)throws SQLException;

	public boolean visitNominaexdf(Nominaexdf nominaexdf, Divisa divisa)throws SQLException;

	public boolean visitNominaex(Nominaex nominaex, Divisa divisa)throws SQLException;

	public boolean visitCliente(Cliente cliente, Divisa divisa)throws SQLException;

	public boolean visitEmprnif(Emprnif emprnif, Divisa divisa)throws SQLException;

	public boolean visitNomina(Nomina nomina, Divisa divisa)throws SQLException;

	public boolean visitFiniquito(Finiquito finiquito, Divisa divisa)throws SQLException;

	public boolean visitFiniquitodf(Finiquitodf finiquitodf, Divisa divisa)throws SQLException;

	public boolean visitFiniquitonu(Finiquitonu finiquitonu, Divisa divisa)throws SQLException;

	public boolean visitImpr11x(Impr11x impr11x, Divisa divisa)throws SQLException;

	public boolean visitImpr190(Impr190 impr190, Divisa divisa)throws SQLException;

	public boolean visitNominadf(Nominadf nominadf, Divisa divisa)throws SQLException;


	public boolean visitDomicilio(Domicilio domicilio)throws SQLException;

	public boolean visitEmprctra(Emprctra emprctra, Domicilio domicilio)throws SQLException;

	public boolean visitEmprdom(Emprdom emprdom, Domicilio domicilio)throws SQLException;

	public boolean visitEmprper(Emprper emprper, Domicilio domicilio)throws SQLException;

	public boolean visitVariaciones(Variaciones variaciones, Domicilio domicilio)throws SQLException;

	public boolean visitPrestaciones(Prestaciones prestaciones, Domicilio domicilio)throws SQLException;

	public boolean visitHttrabajador(Httrabajador httrabajador, Domicilio domicilio)throws SQLException;


	public boolean visitElemcoti(Elemcoti elemcoti)throws SQLException;

	public boolean visitLinelem(Linelem linelem, Elemcoti elemcoti)throws SQLException;


	public boolean visitElemirpf(Elemirpf elemirpf)throws SQLException;

	public boolean visitLinirpf(Linirpf linirpf, Elemirpf elemirpf)throws SQLException;


	public boolean visitEmbargo(Embargo embargo)throws SQLException;


	public boolean visitEmpract(Empract empract)throws SQLException;

	public boolean visitEmprctra(Emprctra emprctra, Empract empract)throws SQLException;

	public boolean visitEmprccc(Emprccc emprccc, Empract empract)throws SQLException;

	public boolean visitEmprccos(Emprccos emprccos, Empract empract)throws SQLException;

	public boolean visitEmprdom(Emprdom emprdom, Empract empract)throws SQLException;

	public boolean visitEmprlban(Emprlban emprlban, Empract empract)throws SQLException;

	public boolean visitEmprper(Emprper emprper, Empract empract)throws SQLException;

	public boolean visitAvisos(Avisos avisos, Empract empract)throws SQLException;

	public boolean visitVariaciones(Variaciones variaciones, Empract empract)throws SQLException;

	public boolean visitRegidocu(Regidocu regidocu, Empract empract)throws SQLException;

	public boolean visitHttrabajador(Httrabajador httrabajador, Empract empract)throws SQLException;


	public boolean visitEmprban(Emprban emprban)throws SQLException;

	public boolean visitEmprlban(Emprlban emprlban, Emprban emprban)throws SQLException;


	public boolean visitEmprccc(Emprccc emprccc)throws SQLException;

	public boolean visitEmprper(Emprper emprper, Emprccc emprccc)throws SQLException;


	public boolean visitEmprccos(Emprccos emprccos)throws SQLException;

	public boolean visitEmprper(Emprper emprper, Emprccos emprccos)throws SQLException;


	public boolean visitEmprctra(Emprctra emprctra)throws SQLException;


	public boolean visitEmprdom(Emprdom emprdom)throws SQLException;


	public boolean visitEmpresa(Empresa empresa)throws SQLException;


	public boolean visitEmprlban(Emprlban emprlban)throws SQLException;


	public boolean visitEmprnif(Emprnif emprnif)throws SQLException;

	public boolean visitEmpract(Empract empract, Emprnif emprnif)throws SQLException;

	public boolean visitEmprctra(Emprctra emprctra, Emprnif emprnif)throws SQLException;

	public boolean visitEmprdom(Emprdom emprdom, Emprnif emprnif)throws SQLException;

	public boolean visitEmprlban(Emprlban emprlban, Emprnif emprnif)throws SQLException;

	public boolean visitEmprper(Emprper emprper, Emprnif emprnif)throws SQLException;

	public boolean visitOtrperc(Otrperc otrperc, Emprnif emprnif)throws SQLException;

	public boolean visitAvisos(Avisos avisos, Emprnif emprnif)throws SQLException;

	public boolean visitImpr11x(Impr11x impr11x, Emprnif emprnif)throws SQLException;

	public boolean visitImpr190(Impr190 impr190, Emprnif emprnif)throws SQLException;

	public boolean visitVariaciones(Variaciones variaciones, Emprnif emprnif)throws SQLException;

	public boolean visitRegidocu(Regidocu regidocu, Emprnif emprnif)throws SQLException;

	public boolean visitRem_cert_empr(Rem_cert_empr rem_cert_empr, Emprnif emprnif)throws SQLException;


	public boolean visitEmprper(Emprper emprper)throws SQLException;

	public boolean visitTrabajo(Trabajo trabajo, Emprper emprper)throws SQLException;

	public boolean visitPercep(Percep percep, Emprper emprper)throws SQLException;

	public boolean visitBonifica(Bonifica bonifica, Emprper emprper)throws SQLException;

	public boolean visitAvisos(Avisos avisos, Emprper emprper)throws SQLException;

	public boolean visitNomina(Nomina nomina, Emprper emprper)throws SQLException;

	public boolean visitTrabinci(Trabinci trabinci, Emprper emprper)throws SQLException;

	public boolean visitNominait(Nominait nominait, Emprper emprper)throws SQLException;

	public boolean visitTrabdto(Trabdto trabdto, Emprper emprper)throws SQLException;

	public boolean visitParteit(Parteit parteit, Emprper emprper)throws SQLException;

	public boolean visitParteitnu(Parteitnu parteitnu, Emprper emprper)throws SQLException;

	public boolean visitNominaitnu(Nominaitnu nominaitnu, Emprper emprper)throws SQLException;

	public boolean visitFiniquito(Finiquito finiquito, Emprper emprper)throws SQLException;

	public boolean visitFiniquitodf(Finiquitodf finiquitodf, Emprper emprper)throws SQLException;

	public boolean visitFiniquitonu(Finiquitonu finiquitonu, Emprper emprper)throws SQLException;

	public boolean visitComunica(Comunica comunica, Emprper emprper)throws SQLException;

	public boolean visitCalculo(Calculo calculo, Emprper emprper)throws SQLException;

	public boolean visitNominadf(Nominadf nominadf, Emprper emprper)throws SQLException;

	public boolean visitEmbargo(Embargo embargo, Emprper emprper)throws SQLException;

	public boolean visitNominaex(Nominaex nominaex, Emprper emprper)throws SQLException;

	public boolean visitRegidocu(Regidocu regidocu, Emprper emprper)throws SQLException;

	public boolean visitPrcdivtrab(Prcdivtrab prcdivtrab, Emprper emprper)throws SQLException;

	public boolean visitRemesa_parte_it(Remesa_parte_it remesa_parte_it, Emprper emprper)throws SQLException;

	public boolean visitRem_cert_empr_det(Rem_cert_empr_det rem_cert_empr_det, Emprper emprper)throws SQLException;


	public boolean visitEntidad(Entidad entidad)throws SQLException;

	public boolean visitSucursal(Sucursal sucursal, Entidad entidad)throws SQLException;

	public boolean visitEmprban(Emprban emprban, Entidad entidad)throws SQLException;

	public boolean visitTrabajo(Trabajo trabajo, Entidad entidad)throws SQLException;

	public boolean visitHttrabajador(Httrabajador httrabajador, Entidad entidad)throws SQLException;

	public boolean visitAutonomos(Autonomos autonomos, Entidad entidad)throws SQLException;


	public boolean visitEpigrafe(Epigrafe epigrafe)throws SQLException;

	public boolean visitLinepigr(Linepigr linepigr, Epigrafe epigrafe)throws SQLException;

	public boolean visitCategoria(Categoria categoria, Epigrafe epigrafe)throws SQLException;

	public boolean visitTrabajo(Trabajo trabajo, Epigrafe epigrafe)throws SQLException;

	public boolean visitHttrabajador(Httrabajador httrabajador, Epigrafe epigrafe)throws SQLException;

	public boolean visitCostes(Costes costes, Epigrafe epigrafe)throws SQLException;


	public boolean visitExclusion(Exclusion exclusion)throws SQLException;


	public boolean visitFinidto(Finidto finidto)throws SQLException;


	public boolean visitFinidtodf(Finidtodf finidtodf)throws SQLException;


	public boolean visitFinidtonu(Finidtonu finidtonu)throws SQLException;


	public boolean visitFinindem(Finindem finindem)throws SQLException;


	public boolean visitFinindemdf(Finindemdf finindemdf)throws SQLException;


	public boolean visitFinindemnu(Finindemnu finindemnu)throws SQLException;


	public boolean visitFinipext(Finipext finipext)throws SQLException;


	public boolean visitFinipextdf(Finipextdf finipextdf)throws SQLException;


	public boolean visitFinipextnu(Finipextnu finipextnu)throws SQLException;


	public boolean visitFiniquito(Finiquito finiquito)throws SQLException;

	public boolean visitFinipext(Finipext finipext, Finiquito finiquito)throws SQLException;

	public boolean visitFinindem(Finindem finindem, Finiquito finiquito)throws SQLException;

	public boolean visitFinidto(Finidto finidto, Finiquito finiquito)throws SQLException;


	public boolean visitFiniquitodf(Finiquitodf finiquitodf)throws SQLException;

	public boolean visitFinipextdf(Finipextdf finipextdf, Finiquitodf finiquitodf)throws SQLException;

	public boolean visitFinindemdf(Finindemdf finindemdf, Finiquitodf finiquitodf)throws SQLException;

	public boolean visitFinidtodf(Finidtodf finidtodf, Finiquitodf finiquitodf)throws SQLException;


	public boolean visitFiniquitonu(Finiquitonu finiquitonu)throws SQLException;

	public boolean visitFinipextnu(Finipextnu finipextnu, Finiquitonu finiquitonu)throws SQLException;

	public boolean visitFinindemnu(Finindemnu finindemnu, Finiquitonu finiquitonu)throws SQLException;

	public boolean visitFinidtonu(Finidtonu finidtonu, Finiquitonu finiquitonu)throws SQLException;


	public boolean visitHttaviso(Httaviso httaviso)throws SQLException;


	public boolean visitHttbonificacion(Httbonificacion httbonificacion)throws SQLException;


	public boolean visitHttcomplemento(Httcomplemento httcomplemento)throws SQLException;


	public boolean visitHttincidencia(Httincidencia httincidencia)throws SQLException;


	public boolean visitHttrabajador(Httrabajador httrabajador)throws SQLException;

	public boolean visitHttbonificacion(Httbonificacion httbonificacion, Httrabajador httrabajador)throws SQLException;

	public boolean visitHttaviso(Httaviso httaviso, Httrabajador httrabajador)throws SQLException;

	public boolean visitHttcomplemento(Httcomplemento httcomplemento, Httrabajador httrabajador)throws SQLException;

	public boolean visitHttincidencia(Httincidencia httincidencia, Httrabajador httrabajador)throws SQLException;


	public boolean visitImpr11x(Impr11x impr11x)throws SQLException;


	public boolean visitImpr190(Impr190 impr190)throws SQLException;

	public boolean visitLin190(Lin190 lin190, Impr190 impr190)throws SQLException;


	public boolean visitLbonifica(Lbonifica lbonifica)throws SQLException;


	public boolean visitLcomunica(Lcomunica lcomunica)throws SQLException;


	public boolean visitLin190(Lin190 lin190)throws SQLException;


	public boolean visitLin_divisa(Lin_divisa lin_divisa)throws SQLException;


	public boolean visitLinautom(Linautom linautom)throws SQLException;


	public boolean visitLinbasec(Linbasec linbasec)throws SQLException;


	public boolean visitLincalcu(Lincalcu lincalcu)throws SQLException;


	public boolean visitLincnae(Lincnae lincnae)throws SQLException;


	public boolean visitLincnae2009(Lincnae2009 lincnae2009)throws SQLException;


	public boolean visitLincomun(Lincomun lincomun)throws SQLException;


	public boolean visitLinelem(Linelem linelem)throws SQLException;


	public boolean visitLinepigr(Linepigr linepigr)throws SQLException;


	public boolean visitLinirpf(Linirpf linirpf)throws SQLException;


	public boolean visitLinmutua(Linmutua linmutua)throws SQLException;


	public boolean visitLinocupacion(Linocupacion linocupacion)throws SQLException;


	public boolean visitLinpercepcion(Linpercepcion linpercepcion)throws SQLException;


	public boolean visitLinplus(Linplus linplus)throws SQLException;


	public boolean visitLinporco(Linporco linporco)throws SQLException;


	public boolean visitLinprestacion(Linprestacion linprestacion)throws SQLException;


	public boolean visitLintc2(Lintc2 lintc2)throws SQLException;


	public boolean visitLintc2epi(Lintc2epi lintc2epi)throws SQLException;


	public boolean visitLinvariables(Linvariables linvariables)throws SQLException;


	public boolean visitMasivo(Masivo masivo)throws SQLException;


	public boolean visitMinor_01(Minor_01 minor_01)throws SQLException;


	public boolean visitMinor_20(Minor_20 minor_20)throws SQLException;


	public boolean visitMinor_31(Minor_31 minor_31)throws SQLException;


	public boolean visitMinor_48(Minor_48 minor_48)throws SQLException;


	public boolean visitMinora(Minora minora)throws SQLException;


	public boolean visitMutua(Mutua mutua)throws SQLException;

	public boolean visitEmprccc(Emprccc emprccc, Mutua mutua)throws SQLException;

	public boolean visitLinmutua(Linmutua linmutua, Mutua mutua)throws SQLException;

	public boolean visitAutonomos(Autonomos autonomos, Mutua mutua)throws SQLException;


	public boolean visitNacion(Nacion nacion)throws SQLException;

	public boolean visitPersona(Persona persona, Nacion nacion)throws SQLException;


	public boolean visitNivel(Nivel nivel)throws SQLException;

	public boolean visitPercniv(Percniv percniv, Nivel nivel)throws SQLException;


	public boolean visitNomdfdev(Nomdfdev nomdfdev)throws SQLException;


	public boolean visitNomdfdto(Nomdfdto nomdfdto)throws SQLException;


	public boolean visitNomdfdtoex(Nomdfdtoex nomdfdtoex)throws SQLException;


	public boolean visitNomdto(Nomdto nomdto)throws SQLException;


	public boolean visitNomdtoex(Nomdtoex nomdtoex)throws SQLException;


	public boolean visitNomina(Nomina nomina)throws SQLException;

	public boolean visitNomdto(Nomdto nomdto, Nomina nomina)throws SQLException;

	public boolean visitNominadev(Nominadev nominadev, Nomina nomina)throws SQLException;

	public boolean visitPrcdivnom(Prcdivnom prcdivnom, Nomina nomina)throws SQLException;


	public boolean visitNominadev(Nominadev nominadev)throws SQLException;


	public boolean visitNominadf(Nominadf nominadf)throws SQLException;

	public boolean visitNomdfdev(Nomdfdev nomdfdev, Nominadf nominadf)throws SQLException;

	public boolean visitNomdfdto(Nomdfdto nomdfdto, Nominadf nominadf)throws SQLException;


	public boolean visitNominaex(Nominaex nominaex)throws SQLException;

	public boolean visitNomdtoex(Nomdtoex nomdtoex, Nominaex nominaex)throws SQLException;


	public boolean visitNominaexdf(Nominaexdf nominaexdf)throws SQLException;

	public boolean visitNomdfdtoex(Nomdfdtoex nomdfdtoex, Nominaexdf nominaexdf)throws SQLException;


	public boolean visitNominait(Nominait nominait)throws SQLException;


	public boolean visitNominaitnu(Nominaitnu nominaitnu)throws SQLException;


	public boolean visitNszadmh(Nszadmh nszadmh)throws SQLException;


	public boolean visitNszanex(Nszanex nszanex)throws SQLException;


	public boolean visitNszavis(Nszavis nszavis)throws SQLException;


	public boolean visitNszbanc(Nszbanc nszbanc)throws SQLException;


	public boolean visitNszbase(Nszbase nszbase)throws SQLException;


	public boolean visitNszbolc(Nszbolc nszbolc)throws SQLException;


	public boolean visitNszboni(Nszboni nszboni)throws SQLException;


	public boolean visitNszcala(Nszcala nszcala)throws SQLException;


	public boolean visitNszcatg(Nszcatg nszcatg)throws SQLException;


	public boolean visitNszcdtr(Nszcdtr nszcdtr)throws SQLException;


	public boolean visitNszcere(Nszcere nszcere)throws SQLException;


	public boolean visitNszcoco(Nszcoco nszcoco)throws SQLException;


	public boolean visitNszcomp(Nszcomp nszcomp)throws SQLException;


	public boolean visitNszcont(Nszcont nszcont)throws SQLException;


	public boolean visitNszconv(Nszconv nszconv)throws SQLException;


	public boolean visitNszcopa(Nszcopa nszcopa)throws SQLException;


	public boolean visitNszdcpr(Nszdcpr nszdcpr)throws SQLException;


	public boolean visitNszdomi(Nszdomi nszdomi)throws SQLException;


	public boolean visitNszempr(Nszempr nszempr)throws SQLException;


	public boolean visitNszepig(Nszepig nszepig)throws SQLException;


	public boolean visitNszfini(Nszfini nszfini)throws SQLException;


	public boolean visitNszilte(Nszilte nszilte)throws SQLException;


	public boolean visitNszinci(Nszinci nszinci)throws SQLException;


	public boolean visitNszmest(Nszmest nszmest)throws SQLException;


	public boolean visitNszmupa(Nszmupa nszmupa)throws SQLException;


	public boolean visitNszodet(Nszodet nszodet)throws SQLException;


	public boolean visitNszotpe(Nszotpe nszotpe)throws SQLException;


	public boolean visitNszpaga(Nszpaga nszpaga)throws SQLException;


	public boolean visitNszpeop(Nszpeop nszpeop)throws SQLException;


	public boolean visitNszpoco(Nszpoco nszpoco)throws SQLException;


	public boolean visitNszprov(Nszprov nszprov)throws SQLException;


	public boolean visitNszrari(Nszrari nszrari)throws SQLException;


	public boolean visitNszreac(Nszreac nszreac)throws SQLException;


	public boolean visitNszrece(Nszrece nszrece)throws SQLException;


	public boolean visitNszregi(Nszregi nszregi)throws SQLException;


	public boolean visitNsztido(Nsztido nsztido)throws SQLException;


	public boolean visitNsztrab(Nsztrab nsztrab)throws SQLException;


	public boolean visitNszunco(Nszunco nszunco)throws SQLException;


	public boolean visitOcupacion(Ocupacion ocupacion)throws SQLException;

	public boolean visitLinocupacion(Linocupacion linocupacion, Ocupacion ocupacion)throws SQLException;


	public boolean visitOpercepciones(Opercepciones opercepciones)throws SQLException;


	public boolean visitOpfile(Opfile opfile)throws SQLException;


	public boolean visitOtrperc(Otrperc otrperc)throws SQLException;


	public boolean visitPagaext(Pagaext pagaext)throws SQLException;


	public boolean visitPais(Pais pais)throws SQLException;

	public boolean visitComunidad(Comunidad comunidad, Pais pais)throws SQLException;

	public boolean visitCliente(Cliente cliente, Pais pais)throws SQLException;

	public boolean visitEmprnif(Emprnif emprnif, Pais pais)throws SQLException;

	public boolean visitPersona(Persona persona, Pais pais)throws SQLException;

	public boolean visitHttrabajador(Httrabajador httrabajador, Pais pais)throws SQLException;


	public boolean visitParteconf(Parteconf parteconf)throws SQLException;


	public boolean visitParteit(Parteit parteit)throws SQLException;

	public boolean visitParteconf(Parteconf parteconf, Parteit parteit)throws SQLException;


	public boolean visitParteitnu(Parteitnu parteitnu)throws SQLException;


	public boolean visitPercep(Percep percep)throws SQLException;


	public boolean visitPercepcion(Percepcion percepcion)throws SQLException;

	public boolean visitLinpercepcion(Linpercepcion linpercepcion, Percepcion percepcion)throws SQLException;


	public boolean visitPercniv(Percniv percniv)throws SQLException;


	public boolean visitPerfil(Perfil perfil)throws SQLException;


	public boolean visitPersona(Persona persona)throws SQLException;

	public boolean visitEmprper(Emprper emprper, Persona persona)throws SQLException;

	public boolean visitOtrperc(Otrperc otrperc, Persona persona)throws SQLException;

	public boolean visitLintc2(Lintc2 lintc2, Persona persona)throws SQLException;

	public boolean visitAutonomos(Autonomos autonomos, Persona persona)throws SQLException;


	public boolean visitPluses(Pluses pluses)throws SQLException;

	public boolean visitLinplus(Linplus linplus, Pluses pluses)throws SQLException;


	public boolean visitPorcoti(Porcoti porcoti)throws SQLException;

	public boolean visitLinporco(Linporco linporco, Porcoti porcoti)throws SQLException;

	public boolean visitTipocont(Tipocont tipocont, Porcoti porcoti)throws SQLException;

	public boolean visitTrabajo(Trabajo trabajo, Porcoti porcoti)throws SQLException;

	public boolean visitCostes(Costes costes, Porcoti porcoti)throws SQLException;


	public boolean visitPrcdivnom(Prcdivnom prcdivnom)throws SQLException;


	public boolean visitPrcdivtrab(Prcdivtrab prcdivtrab)throws SQLException;


	public boolean visitPrestaciones(Prestaciones prestaciones)throws SQLException;

	public boolean visitLinprestacion(Linprestacion linprestacion, Prestaciones prestaciones)throws SQLException;


	public boolean visitPrinters(Printers printers)throws SQLException;


	public boolean visitProcesos(Procesos procesos)throws SQLException;


	public boolean visitProvincia(Provincia provincia)throws SQLException;

	public boolean visitDelegacion(Delegacion delegacion, Provincia provincia)throws SQLException;

	public boolean visitCliente(Cliente cliente, Provincia provincia)throws SQLException;

	public boolean visitDomicilio(Domicilio domicilio, Provincia provincia)throws SQLException;

	public boolean visitPersona(Persona persona, Provincia provincia)throws SQLException;

	public boolean visitImpr11x(Impr11x impr11x, Provincia provincia)throws SQLException;

	public boolean visitImpr190(Impr190 impr190, Provincia provincia)throws SQLException;

	public boolean visitOpfile(Opfile opfile, Provincia provincia)throws SQLException;

	public boolean visitHttrabajador(Httrabajador httrabajador, Provincia provincia)throws SQLException;

	public boolean visitAutonomos(Autonomos autonomos, Provincia provincia)throws SQLException;


	public boolean visitRegidocu(Regidocu regidocu)throws SQLException;


	public boolean visitRem_cert_empr(Rem_cert_empr rem_cert_empr)throws SQLException;


	public boolean visitRem_cert_empr_det(Rem_cert_empr_det rem_cert_empr_det)throws SQLException;


	public boolean visitRemesa_inss(Remesa_inss remesa_inss)throws SQLException;

	public boolean visitRemesa_parte_it(Remesa_parte_it remesa_parte_it, Remesa_inss remesa_inss)throws SQLException;


	public boolean visitRemesa_parte_it(Remesa_parte_it remesa_parte_it)throws SQLException;


	public boolean visitRemesaafi(Remesaafi remesaafi)throws SQLException;


	public boolean visitRemesainss(Remesainss remesainss)throws SQLException;


	public boolean visitSession(Session session)throws SQLException;

	public boolean visitAction_entry(Action_entry action_entry, Session session)throws SQLException;


	public boolean visitSincomun(Sincomun sincomun)throws SQLException;


	public boolean visitSucursal(Sucursal sucursal)throws SQLException;

	public boolean visitEmprban(Emprban emprban, Sucursal sucursal)throws SQLException;

	public boolean visitTrabajo(Trabajo trabajo, Sucursal sucursal)throws SQLException;

	public boolean visitHttrabajador(Httrabajador httrabajador, Sucursal sucursal)throws SQLException;

	public boolean visitAutonomos(Autonomos autonomos, Sucursal sucursal)throws SQLException;


	public boolean visitTc1(Tc1 tc1)throws SQLException;


	public boolean visitTc2(Tc2 tc2)throws SQLException;

	public boolean visitLintc2epi(Lintc2epi lintc2epi, Tc2 tc2)throws SQLException;

	public boolean visitLintc2(Lintc2 lintc2, Tc2 tc2)throws SQLException;


	public boolean visitTipaut(Tipaut tipaut)throws SQLException;

	public boolean visitTrabajo(Trabajo trabajo, Tipaut tipaut)throws SQLException;


	public boolean visitTipboni(Tipboni tipboni)throws SQLException;

	public boolean visitBonifica(Bonifica bonifica, Tipboni tipboni)throws SQLException;


	public boolean visitTipcotc2(Tipcotc2 tipcotc2)throws SQLException;

	public boolean visitTrabajo(Trabajo trabajo, Tipcotc2 tipcotc2)throws SQLException;

	public boolean visitHttrabajador(Httrabajador httrabajador, Tipcotc2 tipcotc2)throws SQLException;


	public boolean visitTipdoc(Tipdoc tipdoc)throws SQLException;

	public boolean visitCliente(Cliente cliente, Tipdoc tipdoc)throws SQLException;

	public boolean visitEmprnif(Emprnif emprnif, Tipdoc tipdoc)throws SQLException;

	public boolean visitPersona(Persona persona, Tipdoc tipdoc)throws SQLException;

	public boolean visitComunica(Comunica comunica, Tipdoc tipdoc)throws SQLException;

	public boolean visitHttrabajador(Httrabajador httrabajador, Tipdoc tipdoc)throws SQLException;


	public boolean visitTipempr(Tipempr tipempr)throws SQLException;

	public boolean visitCliente(Cliente cliente, Tipempr tipempr)throws SQLException;

	public boolean visitEmprnif(Emprnif emprnif, Tipempr tipempr)throws SQLException;


	public boolean visitTipinc(Tipinc tipinc)throws SQLException;

	public boolean visitTrabinci(Trabinci trabinci, Tipinc tipinc)throws SQLException;


	public boolean visitTipocnae(Tipocnae tipocnae)throws SQLException;


	public boolean visitTipocnae2009(Tipocnae2009 tipocnae2009)throws SQLException;


	public boolean visitTipocont(Tipocont tipocont)throws SQLException;

	public boolean visitTrabajo(Trabajo trabajo, Tipocont tipocont)throws SQLException;

	public boolean visitHttrabajador(Httrabajador httrabajador, Tipocont tipocont)throws SQLException;


	public boolean visitTiposdoc(Tiposdoc tiposdoc)throws SQLException;


	public boolean visitTipovia(Tipovia tipovia)throws SQLException;

	public boolean visitDelegacion(Delegacion delegacion, Tipovia tipovia)throws SQLException;

	public boolean visitCliente(Cliente cliente, Tipovia tipovia)throws SQLException;

	public boolean visitDomicilio(Domicilio domicilio, Tipovia tipovia)throws SQLException;

	public boolean visitPersona(Persona persona, Tipovia tipovia)throws SQLException;

	public boolean visitOpfile(Opfile opfile, Tipovia tipovia)throws SQLException;

	public boolean visitHttrabajador(Httrabajador httrabajador, Tipovia tipovia)throws SQLException;

	public boolean visitAutonomos(Autonomos autonomos, Tipovia tipovia)throws SQLException;


	public boolean visitTipreg(Tipreg tipreg)throws SQLException;

	public boolean visitRegidocu(Regidocu regidocu, Tipreg tipreg)throws SQLException;


	public boolean visitTrabajadores(Trabajadores trabajadores)throws SQLException;


	public boolean visitTrabajo(Trabajo trabajo)throws SQLException;


	public boolean visitTrabdto(Trabdto trabdto)throws SQLException;


	public boolean visitTrabinci(Trabinci trabinci)throws SQLException;


	public boolean visitUnidades(Unidades unidades)throws SQLException;


	public boolean visitUsuario(Usuario usuario)throws SQLException;

	public boolean visitSession(Session session, Usuario usuario)throws SQLException;

	public boolean visitAction_favorite(Action_favorite action_favorite, Usuario usuario)throws SQLException;

	public boolean visitAction_denied(Action_denied action_denied, Usuario usuario)throws SQLException;


	public boolean visitVariables(Variables variables)throws SQLException;

	public boolean visitLinvariables(Linvariables linvariables, Variables variables)throws SQLException;


	public boolean visitVariaciones(Variaciones variaciones)throws SQLException;

}
