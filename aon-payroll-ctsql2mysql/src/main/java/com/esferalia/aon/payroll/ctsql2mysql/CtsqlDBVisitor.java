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

import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Remesa_inss;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipocausabaja;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipocnae2009;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominaitnu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lincalcu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprban;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tr_peculiar;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Autbases;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cuota_31;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprnif;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Delegacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Opercepciones;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Minor_48;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipempr;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linautom;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Persona;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Impr296;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lintipocargos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Epigrafe;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Automat;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finiquito;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linfpercep;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominait;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprccc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Porcoti;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Httbonificacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lindatosweb;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Unidades;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Honorarios;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipboni;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nivel;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cuota_48;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linmutua;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Datosafi;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabinci;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finiquitodf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Baseartista;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finidto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipocargos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Variaciones;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finindem;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cargos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lin_divisa;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finipextdf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Remesace;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Minor_20;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Comunidad;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Calendar;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Variables;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Categoria;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Prestaciones;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tr_tramos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cuota_20;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Avisos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Rem_cert_empr_det;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Sucursal;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Basehogar;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Costes;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Minor_31;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Config;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linirpf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linepigr;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Parteitnu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominadf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Convenio;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Calfiniquito;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdfdtoex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linprestacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipinc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Elemcoti;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Parteconf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Pluses;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lin190;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Complemento;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lincnae2009;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdfdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finidtonu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Remesaafi;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Bonifica;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Empract;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprccos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cra_trb;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tr_parametro;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Httcomplemento;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cliente;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Autonomos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Minora;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprlban;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Impr11x;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Rem_cert_empr;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipcotc2;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabajadores;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cuota;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Regidocu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lintc2epi;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Provincia;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linocupacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Datosweb;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominaex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lin296;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tr_datos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Comunica;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipocnae;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Divisa;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Calculo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cra_dde;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Pais;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percep;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finindemdf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Entidad;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linhonorarios;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Embargo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cra_cre;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percniv;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tiposdoc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Ajustes;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Httaviso;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lbonifica;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Ocupacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finiquitonu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Workplaces;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linworkplaces;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Opfile;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finipextnu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lincomun;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linelem;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linpercepcion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Perfil;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lintc2;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cnae2009;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linplus;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Impr216;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Complevar;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdfdev;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Exclusion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Httincidencia;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Httrabajador;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Domicilio;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipdoc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tr_serie;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Basecoti;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linvariables;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Fpercep;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lincnae;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Elemirpf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprdom;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Mutua;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Remesainss;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linbasec;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percepcion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprctra;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Remesa_parte_it;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Procesos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipovia;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipocont;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabajo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finindemnu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Grupotrabajo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Colectivos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Linporco;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Admon;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomina;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Minor_01;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Prcdivnom;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tc2;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tc1;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lincargos;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipaut;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Detalle;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Pagaext;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cuota_01;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lcomunica;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Formcont;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finipext;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Empresa;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tr_liquidacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominaexdf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Parteit;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdtoex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Finidtodf;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Otrperc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Codigocreta;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cnae;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Printers;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tr_fichero;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Impr190;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Prcdivtrab;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Sincomun;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipreg;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominadev;

public interface CtsqlDBVisitor {
	
	void visit ( AbstractCtsqlDB ctsqlDB ) 
	throws SQLException;
	

	 void visitTrabdto(Trabdto trabdto)
	throws SQLException;


	 void visitRemesa_inss(Remesa_inss remesa_inss)
	throws SQLException;

	void visitPit_remesa_inss(Remesa_parte_it remesa_parte_it, Remesa_inss remesa_inss)
	throws SQLException;


	 void visitTipocausabaja(Tipocausabaja tipocausabaja)
	throws SQLException;


	 void visitTipocnae2009(Tipocnae2009 tipocnae2009)
	throws SQLException;


	 void visitNominaitnu(Nominaitnu nominaitnu)
	throws SQLException;


	 void visitLincalcu(Lincalcu lincalcu)
	throws SQLException;


	 void visitEmprban(Emprban emprban)
	throws SQLException;

	void visitEmprlban_emprban(Emprlban emprlban, Emprban emprban)
	throws SQLException;


	 void visitTr_peculiar(Tr_peculiar tr_peculiar)
	throws SQLException;


	 void visitAutbases(Autbases autbases)
	throws SQLException;


	 void visitCuota_31(Cuota_31 cuota_31)
	throws SQLException;


	 void visitEmprnif(Emprnif emprnif)
	throws SQLException;

	void visitEmpract_emprnif(Empract empract, Emprnif emprnif)
	throws SQLException;

	void visitEmprctra_emprnif(Emprctra emprctra, Emprnif emprnif)
	throws SQLException;

	void visitEmprdom_emprnif(Emprdom emprdom, Emprnif emprnif)
	throws SQLException;

	void visitEmprlban_emprnif(Emprlban emprlban, Emprnif emprnif)
	throws SQLException;

	void visitRel_epp_emp(Emprper emprper, Emprnif emprnif)
	throws SQLException;

	void visitOtrperc_emprnif(Otrperc otrperc, Emprnif emprnif)
	throws SQLException;

	void visitAvisos_codemp(Avisos avisos, Emprnif emprnif)
	throws SQLException;

	void visitRegidocu_emprnif(Regidocu regidocu, Emprnif emprnif)
	throws SQLException;

	void visitImpr190_codemp(Impr190 impr190, Emprnif emprnif)
	throws SQLException;

	void visitImpr190_repres(Impr190 impr190, Emprnif emprnif)
	throws SQLException;

	void visitImpr190_cargo(Impr190 impr190, Emprnif emprnif)
	throws SQLException;

	void visitRel_var_emp(Variaciones variaciones, Emprnif emprnif)
	throws SQLException;

	void visitFk_cert_rem_empr(Rem_cert_empr rem_cert_empr, Emprnif emprnif)
	throws SQLException;

	void visitHon_emprnif(Honorarios honorarios, Emprnif emprnif)
	throws SQLException;

	void visitImpr11x_emprnif(Impr11x impr11x, Emprnif emprnif)
	throws SQLException;

	void visitImpr216_emprnif(Impr216 impr216, Emprnif emprnif)
	throws SQLException;

	void visitImpr296_codemp(Impr296 impr296, Emprnif emprnif)
	throws SQLException;

	void visitImpr296_repres(Impr296 impr296, Emprnif emprnif)
	throws SQLException;

	void visitImpr296_cargo(Impr296 impr296, Emprnif emprnif)
	throws SQLException;

	void visitFpe_emprnif(Fpercep fpercep, Emprnif emprnif)
	throws SQLException;


	 void visitDelegacion(Delegacion delegacion)
	throws SQLException;

	void visitCliente_delegacion(Cliente cliente, Delegacion delegacion)
	throws SQLException;


	 void visitOpercepciones(Opercepciones opercepciones)
	throws SQLException;


	 void visitMinor_48(Minor_48 minor_48)
	throws SQLException;


	 void visitTipempr(Tipempr tipempr)
	throws SQLException;

	void visitRel_cli_emp(Cliente cliente, Tipempr tipempr)
	throws SQLException;

	void visitRel_emp_epr(Emprnif emprnif, Tipempr tipempr)
	throws SQLException;


	 void visitLinautom(Linautom linautom)
	throws SQLException;


	 void visitPersona(Persona persona)
	throws SQLException;

	void visitRel_epp_per(Emprper emprper, Persona persona)
	throws SQLException;

	void visitOtrperc_persona(Otrperc otrperc, Persona persona)
	throws SQLException;

	void visitLintc2_persona(Lintc2 lintc2, Persona persona)
	throws SQLException;

	void visitJautpersona(Autonomos autonomos, Persona persona)
	throws SQLException;

	void visitTr_persona_nums(Tr_tramos tr_tramos, Persona persona)
	throws SQLException;

	void visitTr_persona_indd(Tr_tramos tr_tramos, Persona persona)
	throws SQLException;

	void visitTr_persona_numd(Tr_tramos tr_tramos, Persona persona)
	throws SQLException;

	void visitTr_persona_atc2(Tr_tramos tr_tramos, Persona persona)
	throws SQLException;

	void visitFpe_persona(Fpercep fpercep, Persona persona)
	throws SQLException;


	 void visitImpr296(Impr296 impr296)
	throws SQLException;

	void visitLin296_impr296(Lin296 lin296, Impr296 impr296)
	throws SQLException;


	 void visitLintipocargos(Lintipocargos lintipocargos)
	throws SQLException;


	 void visitEpigrafe(Epigrafe epigrafe)
	throws SQLException;

	void visitRel_lep_epi(Linepigr linepigr, Epigrafe epigrafe)
	throws SQLException;

	void visitRel_cat_epi(Categoria categoria, Epigrafe epigrafe)
	throws SQLException;

	void visitRel_cos_epi(Costes costes, Epigrafe epigrafe)
	throws SQLException;

	void visitRel_tra_epi(Trabajo trabajo, Epigrafe epigrafe)
	throws SQLException;

	void visitJepigrafe(Httrabajador httrabajador, Epigrafe epigrafe)
	throws SQLException;


	 void visitAutomat(Automat automat)
	throws SQLException;

	void visitLinautom_automati(Linautom linautom, Automat automat)
	throws SQLException;


	 void visitFiniquito(Finiquito finiquito)
	throws SQLException;

	void visitRel_fpe_fin(Finipext finipext, Finiquito finiquito)
	throws SQLException;

	void visitRel_fii_fin(Finindem finindem, Finiquito finiquito)
	throws SQLException;

	void visitRel_fid_fin(Finidto finidto, Finiquito finiquito)
	throws SQLException;


	 void visitLinfpercep(Linfpercep linfpercep)
	throws SQLException;


	 void visitNominait(Nominait nominait)
	throws SQLException;


	 void visitEmprccc(Emprccc emprccc)
	throws SQLException;

	void visitFormcont_emprccc(Formcont formcont, Emprccc emprccc)
	throws SQLException;

	void visitRel_epp_ccc(Emprper emprper, Emprccc emprccc)
	throws SQLException;


	 void visitPorcoti(Porcoti porcoti)
	throws SQLException;

	void visitRel_lpc_pct(Linporco linporco, Porcoti porcoti)
	throws SQLException;

	void visitRel_tco_codpct(Tipocont tipocont, Porcoti porcoti)
	throws SQLException;

	void visitRel_cos_pct(Costes costes, Porcoti porcoti)
	throws SQLException;

	void visitRel_tra_pct(Trabajo trabajo, Porcoti porcoti)
	throws SQLException;


	 void visitHttbonificacion(Httbonificacion httbonificacion)
	throws SQLException;


	 void visitLindatosweb(Lindatosweb lindatosweb)
	throws SQLException;


	 void visitUnidades(Unidades unidades)
	throws SQLException;


	 void visitHonorarios(Honorarios honorarios)
	throws SQLException;

	void visitLin_honorarios(Linhonorarios linhonorarios, Honorarios honorarios)
	throws SQLException;


	 void visitTipboni(Tipboni tipboni)
	throws SQLException;


	 void visitNivel(Nivel nivel)
	throws SQLException;

	void visitPercniv_nivel(Percniv percniv, Nivel nivel)
	throws SQLException;


	 void visitCuota_48(Cuota_48 cuota_48)
	throws SQLException;


	 void visitLinmutua(Linmutua linmutua)
	throws SQLException;


	 void visitDatosafi(Datosafi datosafi)
	throws SQLException;


	 void visitTrabinci(Trabinci trabinci)
	throws SQLException;


	 void visitFiniquitodf(Finiquitodf finiquitodf)
	throws SQLException;


	 void visitBaseartista(Baseartista baseartista)
	throws SQLException;


	 void visitFinidto(Finidto finidto)
	throws SQLException;


	 void visitTipocargos(Tipocargos tipocargos)
	throws SQLException;

	void visitLin_tipocargos(Lintipocargos lintipocargos, Tipocargos tipocargos)
	throws SQLException;

	void visitRel_tipocargos(Lincargos lincargos, Tipocargos tipocargos)
	throws SQLException;


	 void visitVariaciones(Variaciones variaciones)
	throws SQLException;


	 void visitFinindem(Finindem finindem)
	throws SQLException;


	 void visitCargos(Cargos cargos)
	throws SQLException;

	void visitLin_cargos(Lincargos lincargos, Cargos cargos)
	throws SQLException;


	 void visitLin_divisa(Lin_divisa lin_divisa)
	throws SQLException;


	 void visitFinipextdf(Finipextdf finipextdf)
	throws SQLException;


	 void visitRemesace(Remesace remesace)
	throws SQLException;


	 void visitMinor_20(Minor_20 minor_20)
	throws SQLException;


	 void visitComunidad(Comunidad comunidad)
	throws SQLException;

	void visitProvincia_comunida(Provincia provincia, Comunidad comunidad)
	throws SQLException;


	 void visitCalendar(Calendar calendar)
	throws SQLException;


	 void visitVariables(Variables variables)
	throws SQLException;

	void visitRel_var_lin(Linvariables linvariables, Variables variables)
	throws SQLException;


	 void visitCategoria(Categoria categoria)
	throws SQLException;


	 void visitPrestaciones(Prestaciones prestaciones)
	throws SQLException;

	void visitRel_lpr_pre(Linprestacion linprestacion, Prestaciones prestaciones)
	throws SQLException;


	 void visitTr_tramos(Tr_tramos tr_tramos)
	throws SQLException;

	void visitTr_d_tramos(Tr_datos tr_datos, Tr_tramos tr_tramos)
	throws SQLException;

	void visitTr_p_tramos(Tr_peculiar tr_peculiar, Tr_tramos tr_tramos)
	throws SQLException;


	 void visitCuota_20(Cuota_20 cuota_20)
	throws SQLException;


	 void visitAvisos(Avisos avisos)
	throws SQLException;


	 void visitRem_cert_empr_det(Rem_cert_empr_det rem_cert_empr_det)
	throws SQLException;


	 void visitSucursal(Sucursal sucursal)
	throws SQLException;


	 void visitBasehogar(Basehogar basehogar)
	throws SQLException;


	 void visitCostes(Costes costes)
	throws SQLException;

	void visitLcomunica_costes(Lcomunica lcomunica, Costes costes)
	throws SQLException;

	void visitLbonifica_costes(Lbonifica lbonifica, Costes costes)
	throws SQLException;


	 void visitMinor_31(Minor_31 minor_31)
	throws SQLException;


	 void visitConfig(Config config)
	throws SQLException;


	 void visitLinirpf(Linirpf linirpf)
	throws SQLException;


	 void visitLinepigr(Linepigr linepigr)
	throws SQLException;


	 void visitParteitnu(Parteitnu parteitnu)
	throws SQLException;


	 void visitNominadf(Nominadf nominadf)
	throws SQLException;

	void visitNomdfdev_nominadf(Nomdfdev nomdfdev, Nominadf nominadf)
	throws SQLException;

	void visitNomdfdto_nominadf(Nomdfdto nomdfdto, Nominadf nominadf)
	throws SQLException;


	 void visitConvenio(Convenio convenio)
	throws SQLException;

	void visitRel_pga_con(Pagaext pagaext, Convenio convenio)
	throws SQLException;

	void visitRel_niv_con(Nivel nivel, Convenio convenio)
	throws SQLException;

	void visitRel_cat_con(Categoria categoria, Convenio convenio)
	throws SQLException;

	void visitRel_pcn_con(Percniv percniv, Convenio convenio)
	throws SQLException;

	void visitEmpract_convenio(Empract empract, Convenio convenio)
	throws SQLException;

	void visitEmprctra_convenio(Emprctra emprctra, Convenio convenio)
	throws SQLException;

	void visitRel_tra_con(Trabajo trabajo, Convenio convenio)
	throws SQLException;

	void visitFpe_convenio(Fpercep fpercep, Convenio convenio)
	throws SQLException;


	 void visitCalfiniquito(Calfiniquito calfiniquito)
	throws SQLException;


	 void visitNomdfdtoex(Nomdfdtoex nomdfdtoex)
	throws SQLException;


	 void visitLinprestacion(Linprestacion linprestacion)
	throws SQLException;


	 void visitTipinc(Tipinc tipinc)
	throws SQLException;

	void visitRel_inc_tip(Trabinci trabinci, Tipinc tipinc)
	throws SQLException;


	 void visitElemcoti(Elemcoti elemcoti)
	throws SQLException;

	void visitRel_lel_ele(Linelem linelem, Elemcoti elemcoti)
	throws SQLException;


	 void visitParteconf(Parteconf parteconf)
	throws SQLException;


	 void visitPluses(Pluses pluses)
	throws SQLException;

	void visitRel_lpl_plu(Linplus linplus, Pluses pluses)
	throws SQLException;


	 void visitLin190(Lin190 lin190)
	throws SQLException;


	 void visitComplemento(Complemento complemento)
	throws SQLException;

	void visitRel_pga_com(Pagaext pagaext, Complemento complemento)
	throws SQLException;

	void visitRel_pcn_com(Percniv percniv, Complemento complemento)
	throws SQLException;

	void visitRel_pcn_cap(Percniv percniv, Complemento complemento)
	throws SQLException;

	void visitRel_lpl_com(Linplus linplus, Complemento complemento)
	throws SQLException;

	void visitRel_pcp_com(Percep percep, Complemento complemento)
	throws SQLException;

	void visitRel_pcp_comapl(Percep percep, Complemento complemento)
	throws SQLException;

	void visitRel_pex_com(Nominaex nominaex, Complemento complemento)
	throws SQLException;

	void visitRel_fpe_com(Finipext finipext, Complemento complemento)
	throws SQLException;

	void visitFinexdf_com(Finipextdf finipextdf, Complemento complemento)
	throws SQLException;

	void visitFinexnu_com(Finipextnu finipextnu, Complemento complemento)
	throws SQLException;

	void visitLin_complemento(Linfpercep linfpercep, Complemento complemento)
	throws SQLException;


	 void visitLincnae2009(Lincnae2009 lincnae2009)
	throws SQLException;


	 void visitNomdfdto(Nomdfdto nomdfdto)
	throws SQLException;


	 void visitFinidtonu(Finidtonu finidtonu)
	throws SQLException;


	 void visitRemesaafi(Remesaafi remesaafi)
	throws SQLException;


	 void visitBonifica(Bonifica bonifica)
	throws SQLException;


	 void visitEmpract(Empract empract)
	throws SQLException;

	void visitEmprctra_empract(Emprctra emprctra, Empract empract)
	throws SQLException;

	void visitEmprccc_empract(Emprccc emprccc, Empract empract)
	throws SQLException;

	void visitRel_cco_act(Emprccos emprccos, Empract empract)
	throws SQLException;

	void visitEmprdom_empract(Emprdom emprdom, Empract empract)
	throws SQLException;

	void visitEmprlban_empract(Emprlban emprlban, Empract empract)
	throws SQLException;

	void visitRel_epp_act(Emprper emprper, Empract empract)
	throws SQLException;

	void visitAvisos_empract(Avisos avisos, Empract empract)
	throws SQLException;

	void visitRegidocu_empract(Regidocu regidocu, Empract empract)
	throws SQLException;

	void visitRel_var_act(Variaciones variaciones, Empract empract)
	throws SQLException;

	void visitJactividad(Httrabajador httrabajador, Empract empract)
	throws SQLException;

	void visitHon_empract(Honorarios honorarios, Empract empract)
	throws SQLException;

	void visitFpe_empract(Fpercep fpercep, Empract empract)
	throws SQLException;


	 void visitEmprccos(Emprccos emprccos)
	throws SQLException;

	void visitRel_epp_cco(Emprper emprper, Emprccos emprccos)
	throws SQLException;


	 void visitCra_trb(Cra_trb cra_trb)
	throws SQLException;

	void visitRel_cra_trb_per(Cra_cre cra_cre, Cra_trb cra_trb)
	throws SQLException;


	 void visitTr_parametro(Tr_parametro tr_parametro)
	throws SQLException;


	 void visitHttcomplemento(Httcomplemento httcomplemento)
	throws SQLException;


	 void visitCliente(Cliente cliente)
	throws SQLException;

	void visitRel_emp_cli(Emprnif emprnif, Cliente cliente)
	throws SQLException;

	void visitRel_dom_cli(Domicilio domicilio, Cliente cliente)
	throws SQLException;

	void visitEmprdom_cliente(Emprdom emprdom, Cliente cliente)
	throws SQLException;

	void visitEmprbanc_cliente(Emprban emprban, Cliente cliente)
	throws SQLException;

	void visitEmprlban_cliente(Emprlban emprlban, Cliente cliente)
	throws SQLException;

	void visitAvisos_codcli(Avisos avisos, Cliente cliente)
	throws SQLException;

	void visitRegidocu_cliente(Regidocu regidocu, Cliente cliente)
	throws SQLException;

	void visitRel_var_cli(Variaciones variaciones, Cliente cliente)
	throws SQLException;

	void visitHon_cliente(Honorarios honorarios, Cliente cliente)
	throws SQLException;

	void visitFpe_cliente(Fpercep fpercep, Cliente cliente)
	throws SQLException;


	 void visitAutonomos(Autonomos autonomos)
	throws SQLException;

	void visitJautautonomos(Autbases autbases, Autonomos autonomos)
	throws SQLException;


	 void visitMinora(Minora minora)
	throws SQLException;


	 void visitEmprlban(Emprlban emprlban)
	throws SQLException;


	 void visitImpr11x(Impr11x impr11x)
	throws SQLException;


	 void visitRem_cert_empr(Rem_cert_empr rem_cert_empr)
	throws SQLException;

	void visitFk_rem_cert_empr(Rem_cert_empr_det rem_cert_empr_det, Rem_cert_empr rem_cert_empr)
	throws SQLException;


	 void visitTipcotc2(Tipcotc2 tipcotc2)
	throws SQLException;

	void visitRel_tra_tc2(Trabajo trabajo, Tipcotc2 tipcotc2)
	throws SQLException;

	void visitJcontratotc2(Httrabajador httrabajador, Tipcotc2 tipcotc2)
	throws SQLException;


	 void visitTrabajadores(Trabajadores trabajadores)
	throws SQLException;


	 void visitCuota(Cuota cuota)
	throws SQLException;


	 void visitRegidocu(Regidocu regidocu)
	throws SQLException;


	 void visitLintc2epi(Lintc2epi lintc2epi)
	throws SQLException;


	 void visitProvincia(Provincia provincia)
	throws SQLException;

	void visitRel_dlg_pro(Delegacion delegacion, Provincia provincia)
	throws SQLException;

	void visitRel_cli_pro(Cliente cliente, Provincia provincia)
	throws SQLException;

	void visitRel_dom_pro(Domicilio domicilio, Provincia provincia)
	throws SQLException;

	void visitRel_per_prd(Persona persona, Provincia provincia)
	throws SQLException;

	void visitRel_per_prn(Persona persona, Provincia provincia)
	throws SQLException;

	void visitImpr190_provincia(Impr190 impr190, Provincia provincia)
	throws SQLException;

	void visitJpronac(Httrabajador httrabajador, Provincia provincia)
	throws SQLException;

	void visitJprovincia(Httrabajador httrabajador, Provincia provincia)
	throws SQLException;

	void visitOpfile_provincia(Opfile opfile, Provincia provincia)
	throws SQLException;

	void visitJautprovincia(Autonomos autonomos, Provincia provincia)
	throws SQLException;

	void visitImpr11x_provincia(Impr11x impr11x, Provincia provincia)
	throws SQLException;

	void visitImpr216_provincia(Impr216 impr216, Provincia provincia)
	throws SQLException;

	void visitImpr296_provincia(Impr296 impr296, Provincia provincia)
	throws SQLException;

	void visitLin296_provincia(Lin296 lin296, Provincia provincia)
	throws SQLException;


	 void visitLinocupacion(Linocupacion linocupacion)
	throws SQLException;


	 void visitDatosweb(Datosweb datosweb)
	throws SQLException;

	void visitLin_datosweb(Lindatosweb lindatosweb, Datosweb datosweb)
	throws SQLException;


	 void visitNominaex(Nominaex nominaex)
	throws SQLException;

	void visitNomdtoex_nominaex(Nomdtoex nomdtoex, Nominaex nominaex)
	throws SQLException;


	 void visitLin296(Lin296 lin296)
	throws SQLException;


	 void visitTr_datos(Tr_datos tr_datos)
	throws SQLException;


	 void visitComunica(Comunica comunica)
	throws SQLException;

	void visitLincomun_comunica(Lincomun lincomun, Comunica comunica)
	throws SQLException;


	 void visitTipocnae(Tipocnae tipocnae)
	throws SQLException;


	 void visitDivisa(Divisa divisa)
	throws SQLException;

	void visitLin_divisa_divisa(Lin_divisa lin_divisa, Divisa divisa)
	throws SQLException;

	void visitLin_divisa_divisa2(Lin_divisa lin_divisa, Divisa divisa)
	throws SQLException;

	void visitNominaexdf_divisa(Nominaexdf nominaexdf, Divisa divisa)
	throws SQLException;

	void visitRel_cli_divisa(Cliente cliente, Divisa divisa)
	throws SQLException;

	void visitRel_emp_divisa(Emprnif emprnif, Divisa divisa)
	throws SQLException;

	void visitRel_nom_divisa(Nomina nomina, Divisa divisa)
	throws SQLException;

	void visitRel_pex_divisa(Nominaex nominaex, Divisa divisa)
	throws SQLException;

	void visitFiniquito_divisa(Finiquito finiquito, Divisa divisa)
	throws SQLException;

	void visitFindf_divisa(Finiquitodf finiquitodf, Divisa divisa)
	throws SQLException;

	void visitFinnu_divisa(Finiquitonu finiquitonu, Divisa divisa)
	throws SQLException;

	void visitImpr190_divisa(Impr190 impr190, Divisa divisa)
	throws SQLException;

	void visitNominadf_divisa(Nominadf nominadf, Divisa divisa)
	throws SQLException;

	void visitImpr11x_divisa(Impr11x impr11x, Divisa divisa)
	throws SQLException;

	void visitImpr216_divisa(Impr216 impr216, Divisa divisa)
	throws SQLException;

	void visitImpr296_divisa(Impr296 impr296, Divisa divisa)
	throws SQLException;


	 void visitCalculo(Calculo calculo)
	throws SQLException;


	 void visitCra_dde(Cra_dde cra_dde)
	throws SQLException;

	void visitRel_cra_dde_cdg(Cra_trb cra_trb, Cra_dde cra_dde)
	throws SQLException;


	 void visitPais(Pais pais)
	throws SQLException;

	void visitRel_com_pai(Comunidad comunidad, Pais pais)
	throws SQLException;

	void visitRel_cli_pai(Cliente cliente, Pais pais)
	throws SQLException;

	void visitRel_emp_pai(Emprnif emprnif, Pais pais)
	throws SQLException;

	void visitRel_emp_pai2(Emprnif emprnif, Pais pais)
	throws SQLException;

	void visitRel_per_pem(Persona persona, Pais pais)
	throws SQLException;

	void visitRel_per_pna(Persona persona, Pais pais)
	throws SQLException;

	void visitJpaiemi(Httrabajador httrabajador, Pais pais)
	throws SQLException;


	 void visitPercep(Percep percep)
	throws SQLException;


	 void visitFinindemdf(Finindemdf finindemdf)
	throws SQLException;


	 void visitEntidad(Entidad entidad)
	throws SQLException;

	void visitSucursal_entidad(Sucursal sucursal, Entidad entidad)
	throws SQLException;

	void visitEmprbanc_entidad(Emprban emprban, Entidad entidad)
	throws SQLException;

	void visitRel_tra_ent(Trabajo trabajo, Entidad entidad)
	throws SQLException;

	void visitJentidad(Httrabajador httrabajador, Entidad entidad)
	throws SQLException;

	void visitJautentidad(Autonomos autonomos, Entidad entidad)
	throws SQLException;

	void visitEmprbanc_entbic(Emprban emprban, Entidad entidad)
	throws SQLException;


	 void visitLinhonorarios(Linhonorarios linhonorarios)
	throws SQLException;


	 void visitEmbargo(Embargo embargo)
	throws SQLException;


	 void visitCra_cre(Cra_cre cra_cre)
	throws SQLException;


	 void visitPercniv(Percniv percniv)
	throws SQLException;


	 void visitTiposdoc(Tiposdoc tiposdoc)
	throws SQLException;


	 void visitAjustes(Ajustes ajustes)
	throws SQLException;


	 void visitHttaviso(Httaviso httaviso)
	throws SQLException;


	 void visitEmprper(Emprper emprper)
	throws SQLException;

	void visitTrabajo_emprper(Trabajo trabajo, Emprper emprper)
	throws SQLException;

	void visitRel_pcp_epp(Percep percep, Emprper emprper)
	throws SQLException;

	void visitBonifica_emprper(Bonifica bonifica, Emprper emprper)
	throws SQLException;

	void visitAvisos_emprper(Avisos avisos, Emprper emprper)
	throws SQLException;

	void visitPrc_emprper(Prcdivtrab prcdivtrab, Emprper emprper)
	throws SQLException;

	void visitRel_nom_per(Nomina nomina, Emprper emprper)
	throws SQLException;

	void visitRel_pex_per(Nominaex nominaex, Emprper emprper)
	throws SQLException;

	void visitTrabinci_emprper(Trabinci trabinci, Emprper emprper)
	throws SQLException;

	void visitNominait_emprper(Nominait nominait, Emprper emprper)
	throws SQLException;

	void visitRel_dto_per(Trabdto trabdto, Emprper emprper)
	throws SQLException;

	void visitRel_pit_epp(Parteit parteit, Emprper emprper)
	throws SQLException;

	void visitEmbargo_emprper(Embargo embargo, Emprper emprper)
	throws SQLException;

	void visitPitnu_emprper(Parteitnu parteitnu, Emprper emprper)
	throws SQLException;

	void visitNitnu_emprper(Nominaitnu nominaitnu, Emprper emprper)
	throws SQLException;

	void visitRel_fin_epp(Finiquito finiquito, Emprper emprper)
	throws SQLException;

	void visitFindf_emprper(Finiquitodf finiquitodf, Emprper emprper)
	throws SQLException;

	void visitFinnu_emprper(Finiquitonu finiquitonu, Emprper emprper)
	throws SQLException;

	void visitComunica_emprper(Comunica comunica, Emprper emprper)
	throws SQLException;

	void visitCalculo_emprper(Calculo calculo, Emprper emprper)
	throws SQLException;

	void visitRegidocu_emprper(Regidocu regidocu, Emprper emprper)
	throws SQLException;

	void visitNominadf_emprper(Nominadf nominadf, Emprper emprper)
	throws SQLException;

	void visitFk_cert_remesa_emp(Rem_cert_empr_det rem_cert_empr_det, Emprper emprper)
	throws SQLException;

	void visitPit_remesa_emp(Remesa_parte_it remesa_parte_it, Emprper emprper)
	throws SQLException;


	 void visitLbonifica(Lbonifica lbonifica)
	throws SQLException;


	 void visitOcupacion(Ocupacion ocupacion)
	throws SQLException;

	void visitJlinocupacion(Linocupacion linocupacion, Ocupacion ocupacion)
	throws SQLException;


	 void visitFiniquitonu(Finiquitonu finiquitonu)
	throws SQLException;

	void visitFinexnu_finnu(Finipextnu finipextnu, Finiquitonu finiquitonu)
	throws SQLException;

	void visitFinidnu_finnu(Finindemnu finindemnu, Finiquitonu finiquitonu)
	throws SQLException;

	void visitFindtonu_finnu(Finidtonu finidtonu, Finiquitonu finiquitonu)
	throws SQLException;


	 void visitWorkplaces(Workplaces workplaces)
	throws SQLException;

	void visitLin_workplaces(Linworkplaces linworkplaces, Workplaces workplaces)
	throws SQLException;

	void visitRel_dlg_workplaces(Delegacion delegacion, Workplaces workplaces)
	throws SQLException;

	void visitRel_workplaces(Lintipocargos lintipocargos, Workplaces workplaces)
	throws SQLException;


	 void visitLinworkplaces(Linworkplaces linworkplaces)
	throws SQLException;

	void visitRel_linworkplaces(Lintipocargos lintipocargos, Linworkplaces linworkplaces)
	throws SQLException;


	 void visitOpfile(Opfile opfile)
	throws SQLException;


	 void visitFinipextnu(Finipextnu finipextnu)
	throws SQLException;


	 void visitLincomun(Lincomun lincomun)
	throws SQLException;


	 void visitLinelem(Linelem linelem)
	throws SQLException;


	 void visitLinpercepcion(Linpercepcion linpercepcion)
	throws SQLException;


	 void visitPerfil(Perfil perfil)
	throws SQLException;


	 void visitLintc2(Lintc2 lintc2)
	throws SQLException;


	 void visitCnae2009(Cnae2009 cnae2009)
	throws SQLException;

	void visitJlincnae2009(Lincnae2009 lincnae2009, Cnae2009 cnae2009)
	throws SQLException;


	 void visitLinplus(Linplus linplus)
	throws SQLException;


	 void visitImpr216(Impr216 impr216)
	throws SQLException;


	 void visitComplevar(Complevar complevar)
	throws SQLException;


	 void visitNomdfdev(Nomdfdev nomdfdev)
	throws SQLException;


	 void visitExclusion(Exclusion exclusion)
	throws SQLException;


	 void visitHttincidencia(Httincidencia httincidencia)
	throws SQLException;


	 void visitHttrabajador(Httrabajador httrabajador)
	throws SQLException;

	void visitRel_htt_bon(Httbonificacion httbonificacion, Httrabajador httrabajador)
	throws SQLException;

	void visitRel_htt_avi(Httaviso httaviso, Httrabajador httrabajador)
	throws SQLException;

	void visitRel_htt_com(Httcomplemento httcomplemento, Httrabajador httrabajador)
	throws SQLException;

	void visitRel_htt_inc(Httincidencia httincidencia, Httrabajador httrabajador)
	throws SQLException;


	 void visitDomicilio(Domicilio domicilio)
	throws SQLException;

	void visitEmprctra_domicilio(Emprctra emprctra, Domicilio domicilio)
	throws SQLException;

	void visitEmprdom_domicilio(Emprdom emprdom, Domicilio domicilio)
	throws SQLException;

	void visitEmprper_domiclio(Emprper emprper, Domicilio domicilio)
	throws SQLException;

	void visitRel_var_dom(Variaciones variaciones, Domicilio domicilio)
	throws SQLException;

	void visitRel_pre_dom(Prestaciones prestaciones, Domicilio domicilio)
	throws SQLException;

	void visitJdomicilio(Httrabajador httrabajador, Domicilio domicilio)
	throws SQLException;


	 void visitTipdoc(Tipdoc tipdoc)
	throws SQLException;

	void visitRel_cli_doc(Cliente cliente, Tipdoc tipdoc)
	throws SQLException;

	void visitRel_emp_doc(Emprnif emprnif, Tipdoc tipdoc)
	throws SQLException;

	void visitRel_emp_doc2(Emprnif emprnif, Tipdoc tipdoc)
	throws SQLException;

	void visitRel_per_doc(Persona persona, Tipdoc tipdoc)
	throws SQLException;

	void visitComunica_tipdoc(Comunica comunica, Tipdoc tipdoc)
	throws SQLException;

	void visitJinddoc(Httrabajador httrabajador, Tipdoc tipdoc)
	throws SQLException;


	 void visitTr_serie(Tr_serie tr_serie)
	throws SQLException;


	 void visitBasecoti(Basecoti basecoti)
	throws SQLException;

	void visitRel_lba_bas(Linbasec linbasec, Basecoti basecoti)
	throws SQLException;

	void visitRel_cat_com(Categoria categoria, Basecoti basecoti)
	throws SQLException;

	void visitRel_cos_bas(Costes costes, Basecoti basecoti)
	throws SQLException;

	void visitRel_tra_bas(Trabajo trabajo, Basecoti basecoti)
	throws SQLException;

	void visitJtarifa(Httrabajador httrabajador, Basecoti basecoti)
	throws SQLException;


	 void visitLinvariables(Linvariables linvariables)
	throws SQLException;


	 void visitFpercep(Fpercep fpercep)
	throws SQLException;

	void visitLin_fpercep(Linfpercep linfpercep, Fpercep fpercep)
	throws SQLException;


	 void visitLincnae(Lincnae lincnae)
	throws SQLException;


	 void visitElemirpf(Elemirpf elemirpf)
	throws SQLException;

	void visitLinirpf_elemirpf(Linirpf linirpf, Elemirpf elemirpf)
	throws SQLException;


	 void visitEmprdom(Emprdom emprdom)
	throws SQLException;


	 void visitMutua(Mutua mutua)
	throws SQLException;

	void visitRel_mut_lin(Linmutua linmutua, Mutua mutua)
	throws SQLException;

	void visitRel_ccc_mut(Emprccc emprccc, Mutua mutua)
	throws SQLException;

	void visitJautmutua(Autonomos autonomos, Mutua mutua)
	throws SQLException;


	 void visitRemesainss(Remesainss remesainss)
	throws SQLException;


	 void visitLinbasec(Linbasec linbasec)
	throws SQLException;


	 void visitPercepcion(Percepcion percepcion)
	throws SQLException;

	void visitRel_per_lin(Linpercepcion linpercepcion, Percepcion percepcion)
	throws SQLException;


	 void visitEmprctra(Emprctra emprctra)
	throws SQLException;


	 void visitRemesa_parte_it(Remesa_parte_it remesa_parte_it)
	throws SQLException;


	 void visitProcesos(Procesos procesos)
	throws SQLException;


	 void visitTipovia(Tipovia tipovia)
	throws SQLException;

	void visitRel_dlg_via(Delegacion delegacion, Tipovia tipovia)
	throws SQLException;

	void visitRel_cli_via(Cliente cliente, Tipovia tipovia)
	throws SQLException;

	void visitRel_dom_via(Domicilio domicilio, Tipovia tipovia)
	throws SQLException;

	void visitRel_per_via(Persona persona, Tipovia tipovia)
	throws SQLException;

	void visitJtipvia(Httrabajador httrabajador, Tipovia tipovia)
	throws SQLException;

	void visitOpfile_tipovia(Opfile opfile, Tipovia tipovia)
	throws SQLException;

	void visitJauttipovia(Autonomos autonomos, Tipovia tipovia)
	throws SQLException;


	 void visitTipocont(Tipocont tipocont)
	throws SQLException;

	void visitJcontrato(Httrabajador httrabajador, Tipocont tipocont)
	throws SQLException;


	 void visitTrabajo(Trabajo trabajo)
	throws SQLException;


	 void visitFinindemnu(Finindemnu finindemnu)
	throws SQLException;


	 void visitGrupotrabajo(Grupotrabajo grupotrabajo)
	throws SQLException;

	void visitRel_cli_grptrabajo(Cliente cliente, Grupotrabajo grupotrabajo)
	throws SQLException;


	 void visitColectivos(Colectivos colectivos)
	throws SQLException;

	void visitRel_tra_col(Trabajo trabajo, Colectivos colectivos)
	throws SQLException;


	 void visitLinporco(Linporco linporco)
	throws SQLException;


	 void visitAdmon(Admon admon)
	throws SQLException;

	void visitRel_emp_adm(Emprnif emprnif, Admon admon)
	throws SQLException;

	void visitImpr190_admon(Impr190 impr190, Admon admon)
	throws SQLException;

	void visitImpr11x_admon(Impr11x impr11x, Admon admon)
	throws SQLException;

	void visitImpr216_admon(Impr216 impr216, Admon admon)
	throws SQLException;

	void visitImpr296_admon(Impr296 impr296, Admon admon)
	throws SQLException;


	 void visitNomina(Nomina nomina)
	throws SQLException;

	void visitRel_dto_nom(Nomdto nomdto, Nomina nomina)
	throws SQLException;

	void visitRel_nmd_nom(Nominadev nominadev, Nomina nomina)
	throws SQLException;

	void visitPrc_nomina(Prcdivnom prcdivnom, Nomina nomina)
	throws SQLException;


	 void visitNomdto(Nomdto nomdto)
	throws SQLException;


	 void visitMinor_01(Minor_01 minor_01)
	throws SQLException;


	 void visitPrcdivnom(Prcdivnom prcdivnom)
	throws SQLException;


	 void visitTc2(Tc2 tc2)
	throws SQLException;

	void visitLintc2epi_tc2(Lintc2epi lintc2epi, Tc2 tc2)
	throws SQLException;

	void visitLintc2_tc2(Lintc2 lintc2, Tc2 tc2)
	throws SQLException;


	 void visitTc1(Tc1 tc1)
	throws SQLException;


	 void visitLincargos(Lincargos lincargos)
	throws SQLException;


	 void visitTipaut(Tipaut tipaut)
	throws SQLException;

	void visitRel_tra_aut(Trabajo trabajo, Tipaut tipaut)
	throws SQLException;


	 void visitDetalle(Detalle detalle)
	throws SQLException;


	 void visitPagaext(Pagaext pagaext)
	throws SQLException;


	 void visitCuota_01(Cuota_01 cuota_01)
	throws SQLException;


	 void visitLcomunica(Lcomunica lcomunica)
	throws SQLException;


	 void visitFormcont(Formcont formcont)
	throws SQLException;


	 void visitFinipext(Finipext finipext)
	throws SQLException;


	 void visitEmpresa(Empresa empresa)
	throws SQLException;


	 void visitTr_liquidacion(Tr_liquidacion tr_liquidacion)
	throws SQLException;

	void visitRel_tr_liquidacion(Tr_tramos tr_tramos, Tr_liquidacion tr_liquidacion)
	throws SQLException;


	 void visitNominaexdf(Nominaexdf nominaexdf)
	throws SQLException;

	void visitRel_nominaexdf(Nomdfdtoex nomdfdtoex, Nominaexdf nominaexdf)
	throws SQLException;


	 void visitParteit(Parteit parteit)
	throws SQLException;

	void visitParteconf_parteit(Parteconf parteconf, Parteit parteit)
	throws SQLException;


	 void visitNacion(Nacion nacion)
	throws SQLException;

	void visitRel_per_nac(Persona persona, Nacion nacion)
	throws SQLException;


	 void visitNomdtoex(Nomdtoex nomdtoex)
	throws SQLException;


	 void visitFinidtodf(Finidtodf finidtodf)
	throws SQLException;


	 void visitOtrperc(Otrperc otrperc)
	throws SQLException;


	 void visitCodigocreta(Codigocreta codigocreta)
	throws SQLException;

	void visitTr_codigocreta(Tr_datos tr_datos, Codigocreta codigocreta)
	throws SQLException;


	 void visitCnae(Cnae cnae)
	throws SQLException;

	void visitJlincnae(Lincnae lincnae, Cnae cnae)
	throws SQLException;


	 void visitPrinters(Printers printers)
	throws SQLException;


	 void visitTr_fichero(Tr_fichero tr_fichero)
	throws SQLException;


	 void visitImpr190(Impr190 impr190)
	throws SQLException;

	void visitLin190_impr190(Lin190 lin190, Impr190 impr190)
	throws SQLException;


	 void visitPrcdivtrab(Prcdivtrab prcdivtrab)
	throws SQLException;


	 void visitSincomun(Sincomun sincomun)
	throws SQLException;


	 void visitTipreg(Tipreg tipreg)
	throws SQLException;

	void visitRegidocu_tipreg(Regidocu regidocu, Tipreg tipreg)
	throws SQLException;


	 void visitNominadev(Nominadev nominadev)
	throws SQLException;

}
