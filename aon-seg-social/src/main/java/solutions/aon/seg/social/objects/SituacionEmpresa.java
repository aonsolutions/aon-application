package solutions.aon.seg.social.objects;

import java.util.Date;

public class SituacionEmpresa {
	// DATOS IDENTIFICATIVOS
	private String ccc;
	private String regimen;
	private String idEmpresario;
	private String nif_empresa;
	private String nss;
	private String cccp;
	private String ugtgss;
	private String ugtgsscccp;
	private String ugcentral;
	private String ogism;
	private String cccAnt;
	private String cccSuc;
	private String sit;
	private Date fSit;
	private Date fAltaInicial;
	private Integer trabajadorAlta;
	private Date altaPrTrab;
	private Date ultBajaEfCot;
	private String trl;
	private String cEsp_num;
	private String cEsp_cad;
	private String cnae93_num;
	private String cnae93_cad;
	private Integer ta2Alta;
	private Integer ta2Baja;


	private String cnae09_num;
	private String cnae09_cad;
	private Float tiposATyEPIT;
	private Float ims;
	private Float total;
	private String coeJub_num;
	private String coeJub_cad;
	private String aconExtra;
	private Boolean escTaller;
	private String autorizacionRed;
	private Date plazoIncorpRed;
	private Date fechaAutCan;
	
	//DATOS DE GESTIÓN (no repetidos)
	private String anagrama;
	private String embarcacion;
	private String tlfMovil;
	private String tlfFijo;
	private String email;
	private Boolean notif_dom_empresa;
	private String tipo_via_dir_empresa;
	
	private String tipo_via_dir_actividad;
	
	private String dir_emp_calle;
	private String dir_emp_num;
	private String dir_emp_bis;
	private String dir_emp_bloq;
	private String dir_emp_es;
	private String dir_emp_piso;
	private String dir_emp_p;
	private String dir_emp_CP;
	private String dir_emp_num_muni;
	private String  dir_emp_nom_muni;
	private String dir_emp_tlf;
	private Boolean notif_dom_actividad;
	private String act_ugtgss;
	private String dir_act_calle;
	private String dir_act_num;

	private String dir_act_bis;
	private String dir_act_bloq;
	private String dir_act_es;
	private String dir_act_piso;
	private String dir_act_p;
	private String dir_act_CP;
	private String dir_act_num_muni;
	private String  dir_act_nom_muni;
	private String dir_act_tlf;
	
	
	
	public static interface Visitor{
	public void  visit_ccc(String ccc);
	public void visit_regimen(String regimen);
	public void visit_idEmpresario(String idemp);
	public void visit_nif_empresa(String nif);
	public void visit_nss(String nss);
	public void visit_cccp(String cccp);
	public void visit_ugtgss(String ugtgss);
	public void visit_ugtgsscccp(String ugtgsscccp);
	public void visit_ugcentral(String ugcentral);
	public void visit_ogism(String ogism);
	public void visit_cccAnt(String cccAnt);
	public void visit_cccSuc(String cccSuc);
	public void visit_sit(String sit);
	public void visit_fSit(Date fSit);
	public void visit_fAltaInicial(Date fAltaInicial);
	public void visit_trabajadorAlta(Integer trabajadorAlta);
	public void visit_altaPrTrab(Date altaPrTrab);
	public void visit_ultBajaEfCot(Date ultBajaEfCot);
	public void visit_trl(String trl);
	public void visit_cEsp_num(String cEsp_num);
	public void visit_cEsp_cad(String cEsp_cad);
	public void visit_cnae93_num(String cnae93_num);
	public void visit_cnae93_cad(String cnae93_cad);
	public void visit_ta2Alta(Integer ta2Alta);
	public void visit_ta2Baja(Integer ta2Baja);
	public void visit_cnae09_num(String cnae09_num);
	public void visit_cnae09_cad(String cnae09_cad);
	public void visit_tiposATyEPIT(Float tiposATyEPIT);
	public void visit_ims(Float ims);
	public void visit_total(Float total);
	public void visit_coeJub_num(String coeJub_num);
	public void visit_coeJub_cad(String coeJub_cad);
	public void visit_aconExtra(String aconExtra);
	public void visit_escTaller(Boolean escTaller);
	public void visit_autorizacionRed(String autorizacionRed);
	public void visit_plazoIncorpRed(Date plazoIncorpRed);
	public void visit_fechaAutCan(Date fechaAutCan);
	
	//DATOS DE GESTIÓN (no repetidos)
	public void visit_anagrama(String anagrama);
	public void visit_embarcacion(String embarcacion);
	public void visit_tlfMovil(String tlfMovil);
	public void visit_tlfFijo(String tlfFijo);
	public void visit_email(String email);
	public void visit_notif_dom_empresa(Boolean notif_dom_empresa);
	public void visit_tipo_via_dir_empresa(String tipo_via_dir_empresa);
	
	public void visit_tipo_via_dir_actividad(String tipo_via_dir_actividad);
	
	public void visit_dir_emp_calle(String dir_emp_calle);
	public void visit_dir_emp_num(String dir_emp_num);
	public void visit_dir_emp_bis(String dir_emp_bis);
	public void visit_dir_emp_bloq(String dir_emp_bloq);
	public void visit_dir_emp_es(String dir_emp_es);
	public void visit_dir_emp_piso(String dir_emp_piso);
	public void visit_dir_emp_p(String dir_emp_p);
	public void visit_dir_emp_CP(String dir_emp_CP);
	public void visit_dir_emp_num_muni(String dir_emp_num_muni);
	public void visit_dir_emp_nom_muni(String  dir_emp_nom_muni);
	public void visit_dir_emp_tlf(String dir_emp_tlf);
	public void visit_notif_dom_actividad(Boolean notif_dom_actividad);
	public void visit_act_ugtgss(String act_ugtgss);
	public void visit_dir_act_calle(String dir_act_calle);
	public void visit_dir_act_num(String dir_act_num);

	public void visit_dir_act_bis(String dir_act_bis);
	public void visit_dir_act_bloq(String dir_act_bloq);
	public void visit_dir_act_es(String dir_act_es);
	public void visit_dir_act_piso(String dir_act_piso);
	public void visit_dir_act_p(String dir_act_p);
	public void visit_dir_act_CP(String dir_act_CP);
	public void visit_dir_act_num_muni(String dir_act_num_muni);
	public void visit_dir_act_nom_muni(String  dir_act_nom_muni);
	public void visit_dir_act_tlf(String dir_act_tlf);
		
		
		//public String visit
	}
	
	public void accept(Visitor visitor) {
		if(ccc!=null)
			visitor.visit_ccc(ccc);
		if(idEmpresario!=null)
			visitor.visit_idEmpresario(idEmpresario);
		if(nif_empresa!=null)
			visitor.visit_nif_empresa(this.nif_empresa);
		if(nss!=null)
			visitor.visit_nss(this.nss);
		if(cccp!=null)
			visitor.visit_cccp(this.cccp);
		if(ugtgss!=null)
			visitor.visit_ugtgss(this.ugtgss);
		if(ugtgsscccp!=null)
			visitor.visit_ugtgsscccp(this.ugtgsscccp);
		if(ugcentral!=null)
			visitor.visit_ugcentral(this.ugcentral);
		if(ogism!=null)
			visitor.visit_ogism(this.ogism);
		if(cccAnt!=null)
			visitor.visit_cccAnt(this.cccAnt);
		if(cccSuc!=null)
			visitor.visit_cccSuc(this.cccSuc);
		if(sit!=null)
			visitor.visit_sit(this.sit);
		if(fSit!=null)
			visitor.visit_fSit(this.fSit);
		if(fAltaInicial!=null)
			visitor.visit_fAltaInicial(this.fAltaInicial);
		if(trabajadorAlta!=null)
			visitor.visit_trabajadorAlta(this.trabajadorAlta);
		if(altaPrTrab!=null)
			visitor.visit_altaPrTrab(this.altaPrTrab);
		if(ultBajaEfCot!=null)
			visitor.visit_ultBajaEfCot(this.ultBajaEfCot);
		if(trl!=null)
			visitor.visit_trl(this.trl);
		if(cEsp_num!=null)
			visitor.visit_cEsp_num(this.cEsp_num);
		if(cEsp_cad!=null)
			visitor.visit_cEsp_cad(this.cEsp_cad);
		if(cnae93_num!=null)
			visitor.visit_cnae93_num(this.cnae93_num);
		if(cnae93_cad!=null)
			visitor.visit_cnae93_cad(this.cnae93_cad);
		if(ta2Alta!=null)
			visitor.visit_ta2Alta(this.ta2Alta);
		if(ta2Baja!=null)
			visitor.visit_ta2Baja(this.ta2Baja);
		if(cnae09_num!=null)
			visitor.visit_cnae09_num(this.cnae09_num);

		if(cnae09_cad!=null)
			visitor.visit_cnae09_cad(this.cnae09_cad);

		if(tiposATyEPIT!=null)
			visitor.visit_tiposATyEPIT(this.tiposATyEPIT);

		if(ims!=null)
			visitor.visit_ims(this.ims);

		if(total!=null)
			visitor.visit_total(this.total);

		if(coeJub_num!=null)
			visitor.visit_coeJub_num(this.coeJub_num);

		if(coeJub_cad!=null)
			visitor.visit_coeJub_cad(this.coeJub_cad);

		if(aconExtra!=null)
			visitor.visit_aconExtra(this.aconExtra);

		if(escTaller!=null)
			visitor.visit_escTaller(this.escTaller);

		if(autorizacionRed!=null)
			visitor.visit_autorizacionRed(this.autorizacionRed);

		if(plazoIncorpRed!=null)
			visitor.visit_plazoIncorpRed(this.plazoIncorpRed);

		if(fechaAutCan!=null)
			visitor.visit_fechaAutCan(this.fechaAutCan);
		if(anagrama!=null)
			visitor.visit_anagrama(this.anagrama);
		if(embarcacion!=null)
			visitor.visit_embarcacion(this.embarcacion);
		if(tlfMovil!=null)
			visitor.visit_tlfMovil(this.tlfMovil);
		if(tlfFijo!=null)
			visitor.visit_tlfFijo(this.tlfFijo);
		if(email!=null)
			visitor.visit_email(this.email);
		if(notif_dom_empresa!=null)
			visitor.visit_notif_dom_empresa(this.notif_dom_empresa);
		if(tipo_via_dir_empresa!=null)
			visitor.visit_tipo_via_dir_empresa(this.tipo_via_dir_empresa);
		if(tipo_via_dir_actividad!=null)
			visitor.visit_tipo_via_dir_actividad(this.tipo_via_dir_actividad);
		if(dir_emp_calle!=null)
			visitor.visit_dir_emp_calle(this.dir_emp_calle);
		if(dir_emp_num!=null)
			visitor.visit_dir_emp_num(this.dir_emp_num);
		if(dir_emp_bis!=null)
			visitor.visit_dir_emp_bis(this.dir_emp_bis);
		if(dir_emp_bloq!=null)
			visitor.visit_dir_emp_bloq(this.dir_emp_bloq);
		if(dir_emp_es!=null)
			visitor.visit_dir_emp_es(this.dir_emp_es);
		if(dir_emp_piso!=null)
			visitor.visit_dir_emp_piso(this.dir_emp_piso);
		if(dir_emp_p!=null)
			visitor.visit_dir_emp_p(this.dir_emp_p);
		if(dir_emp_CP!=null)
			visitor.visit_dir_emp_CP(this.dir_emp_CP);
		if(dir_emp_num_muni!=null)
			visitor.visit_dir_emp_num_muni(this.dir_emp_num_muni);
		if(dir_emp_nom_muni!=null)
			visitor.visit_dir_emp_nom_muni(this.dir_emp_nom_muni);
		if(dir_emp_tlf!=null)
			visitor.visit_dir_emp_tlf(this.dir_emp_tlf);
		if(notif_dom_actividad!=null)
			visitor.visit_notif_dom_actividad(this.notif_dom_actividad);
		if(act_ugtgss!=null)
			visitor.visit_act_ugtgss(this.act_ugtgss);
		if(dir_act_calle!=null)
			visitor.visit_dir_act_calle(this.dir_act_calle);
		if(dir_act_calle!=null)
			visitor.visit_dir_act_calle(this.dir_act_calle);
		if(dir_act_bis!=null)
			visitor.visit_dir_act_bis(this.dir_act_bis);
		if(dir_act_bloq!=null)
			visitor.visit_dir_act_bloq(this.dir_act_bloq);
		if(dir_act_es!=null)
			visitor.visit_dir_act_es(this.dir_act_es);
		if(dir_act_piso!=null)
			visitor.visit_dir_act_piso(this.dir_act_piso);
		if(dir_act_p!=null)
			visitor.visit_dir_act_p(this.dir_act_p);
		if(dir_act_CP!=null)
			visitor.visit_dir_act_CP(this.dir_act_CP);
		if(dir_act_num_muni!=null)
			visitor.visit_dir_act_num_muni(this.dir_act_num_muni);
		if(dir_act_nom_muni!=null)
			visitor.visit_dir_act_nom_muni(this.dir_act_nom_muni);
		if(dir_act_tlf!=null)
			visitor.visit_dir_act_tlf(this.dir_act_tlf);

	}
	
	@Override
	public String toString() {
		StringBuffer stringBuffer=new StringBuffer();
		stringBuffer.append("Situación de la empresa: \n");
		accept(new Visitor() {
			
			@Override
			public void visit_regimen(String regimen) {
				stringBuffer.append(String.format("\tRégimen: \"%S\"\n", regimen));
			}
			
			@Override
			public void visit_nss(String nss) {
				stringBuffer.append(String.format("\tNSS: \"%S\"\n", nss));
			}
			
			@Override
			public void visit_nif_empresa(String nif) {
				stringBuffer.append(String.format("\tNIF: \"%S\"\n", nif));
			}
			
			@Override
			public void visit_idEmpresario(String idemp) {
				stringBuffer.append(String.format("\tID Empresario: \"%S\"\n", idemp));
			}
			
			@Override
			public void visit_ccc(String ccc) {
				stringBuffer.append(String.format("\tCCC: \"%S\"\n", ccc));
			}

			@Override
			public void visit_cccp(String cccp) {
				stringBuffer.append(String.format("\tCCCP: \"%S\"\n", cccp));
				
			}

			@Override
			public void visit_ugtgss(String ugtgss) {
				stringBuffer.append(String.format("\tUGTGSS: \"%S\"\n", ugtgss));
				
			}

			@Override
			public void visit_ugtgsscccp(String ugtgsscccp) {
				stringBuffer.append(String.format("\tUGTGSSCCCP: \"%S\"\n", ugtgsscccp));
				
			}

			@Override
			public void visit_ugcentral(String ugcentral) {
				stringBuffer.append(String.format("\tUGCENTRAL: \"%S\"\n", ugcentral));
				
			}

			@Override
			public void visit_ogism(String ogism) {
				stringBuffer.append(String.format("\tOGISM: \"%S\"\n", ogism));
				
			}

			@Override
			public void visit_cccAnt(String cccAnt) {
				stringBuffer.append(String.format("\tCCCANT: \"%S\"\n", cccAnt));
				
			}

			@Override
			public void visit_cccSuc(String cccSuc) {
				stringBuffer.append(String.format("\tCCCSUC: \"%S\"\n", cccSuc));
				
			}

			@Override
			public void visit_sit(String sit) {
				stringBuffer.append(String.format("\tSIT: \"%S\"\n", sit));
				
			}

			@Override
			public void visit_fSit(Date fSit) {
				stringBuffer.append(String.format("\tFSit: \"%S\"\n", fSit));
				
			}

			@Override
			public void visit_fAltaInicial(Date fAltaInicial) {
				stringBuffer.append(String.format("\tFecha alta inicial: \"%S\"\n", fAltaInicial));
				
			}

			@Override
			public void visit_trabajadorAlta(Integer trabajadorAlta) {
				stringBuffer.append(String.format("\tTrabajadores de alta: \"%S\"\n", trabajadorAlta));
				
			}

			@Override
			public void visit_altaPrTrab(Date altaPrTrab) {
				stringBuffer.append(String.format("\tAlta primer trabaajdor: \"%S\"\n", altaPrTrab));
				
			}

			@Override
			public void visit_ultBajaEfCot(Date ultBajaEfCot) {
				stringBuffer.append(String.format("\tUltima baja ef. cot.: \"%S\"\n", ultBajaEfCot));
				
			}

			@Override
			public void visit_trl(String trl) {
				stringBuffer.append(String.format("\tTRL: \"%S\"\n", trl));
				
			}
			
			@Override
			public void visit_cEsp_num(String cEsp_num) {
				stringBuffer.append(String.format("\tC.Esp num.: \"%S\"\n", cEsp_num));
				
			}

			@Override
			public void visit_cEsp_cad(String cEsp_cad) {
				stringBuffer.append(String.format("\tC.Esp cad.: \"%S\"\n", cEsp_cad));
				
			}

			@Override
			public void visit_cnae93_num(String cnae93_num) {
				stringBuffer.append(String.format("\tC.CNAE93 NUMvisit_ims.: \"%S\"\n", cnae93_num));
				
			}

			@Override
			public void visit_cnae93_cad(String cnae93_cad) {
				stringBuffer.append(String.format("\tC.CNAE93 CAD..: \"%S\"\n", cEsp_cad));
				
			}

			@Override
			public void visit_ta2Alta(Integer ta2Alta) {
				stringBuffer.append(String.format("\tC.TA2ALTA: \"%S\"\n", ta2Alta));
				
			}

			@Override
			public void visit_ta2Baja(Integer ta2Baja) {
				stringBuffer.append(String.format("\tTA2BAJA: \"%S\"\n", ta2Baja));
				
			}

			@Override
			public void visit_cnae09_num(String cnae09_num) {
				stringBuffer.append(String.format("\tCNAE09 num.: \"%S\"\n", cnae09_num));
				
			}

			@Override
			public void visit_cnae09_cad(String cnae09_cad) {
				stringBuffer.append(String.format("\tCNAE09 cad.: \"%S\"\n", cnae09_cad));
				
			}

			@Override
			public void visit_tiposATyEPIT(Float tiposATyEPIT) {
				stringBuffer.append(String.format("\tTipos ATyEPIT.: \"%S\"\n", tiposATyEPIT));
				
			}

			@Override
			public void visit_ims(Float ims) {
				stringBuffer.append(String.format("\tIMS: \"%S\"\n", ims));
				
			}

			@Override
			public void visit_total(Float total) {
				stringBuffer.append(String.format("\tTotal: \"%S\"\n", total));
				
			}

			@Override
			public void visit_coeJub_num(String coeJub_num) {
				stringBuffer.append(String.format("\t Coeficiente jubilación:\"%S\"\n", coeJub_num));
				
			}

			@Override
			public void visit_coeJub_cad(String coeJub_cad) {
				stringBuffer.append(String.format("\tCad. coef. jubilación: \"%S\"\n", coeJub_cad));
				
			}

			@Override
			public void visit_aconExtra(String aconExtra) {
				stringBuffer.append(String.format("\tAcon. extra: \"%S\"\n", aconExtra));
				
			}

			@Override
			public void visit_escTaller(Boolean escTaller) {
				stringBuffer.append(String.format("\tEsc taller: \"%S\"\n", escTaller));
				
			}

			@Override
			public void visit_autorizacionRed(String autorizacionRed) {
				stringBuffer.append(String.format("\tAutorización red: \"%S\"\n", autorizacionRed));
				
			}

			@Override
			public void visit_plazoIncorpRed(Date plazoIncorpRed) {
				stringBuffer.append(String.format("\tPlazo incorp. red: \"%S\"\n", plazoIncorpRed));
				
			}

			@Override
			public void visit_fechaAutCan(Date fechaAutCan) {
				stringBuffer.append(String.format("\tFecha aut. can: \"%S\"\n", fechaAutCan));
				
			}

			@Override
			public void visit_anagrama(String anagrama) {
				stringBuffer.append(String.format("\tAnagrama: \"%S\"\n", anagrama));
				
			}

			@Override
			public void visit_embarcacion(String embarcacion) {
				stringBuffer.append(String.format("\tEmbarcación: \"%S\"\n", embarcacion));				
			}

			@Override
			public void visit_tlfMovil(String tlfMovil) {
				stringBuffer.append(String.format("\tMóvil: \"%S\"\n", tlfMovil));				
			}

			@Override
			public void visit_tlfFijo(String tlfFijo) {
				stringBuffer.append(String.format("\tTlf. fijo: \"%S\"\n", tlfFijo));				
			}

			@Override
			public void visit_email(String email) {
				stringBuffer.append(String.format("\tEmail: \"%S\"\n", email));				
			}

			@Override
			public void visit_notif_dom_empresa(Boolean notif_dom_empresa) {
				stringBuffer.append(String.format("\tFNotif. domicilio empresa: \"%S\"\n", notif_dom_empresa));				
			}

			@Override
			public void visit_tipo_via_dir_empresa(String tipo_via_dir_empresa) {
				stringBuffer.append(String.format("\tTipo vía domicilio empresa: \"%S\"\n", tipo_via_dir_empresa));				
			}

			@Override
			public void visit_dir_emp_calle(String dir_emp_calle) {
				stringBuffer.append(String.format("\tDir. empresa calle: \"%S\"\n", dir_emp_calle));				
			}

			@Override
			public void visit_dir_emp_num(String dir_emp_num) {
				stringBuffer.append(String.format("\tNum. dir. empresa: \"%S\"\n", dir_emp_num));				
			}

			@Override
			public void visit_dir_emp_bis(String dir_emp_bis) {
				stringBuffer.append(String.format("\tDir. emp. bis: \"%S\"\n", dir_emp_bis));				
			}

			@Override
			public void visit_dir_emp_bloq(String dir_emp_bloq) {
				stringBuffer.append(String.format("\tDir. emp. bloque: \"%S\"\n", dir_emp_bloq));				
			}

			@Override
			public void visit_dir_emp_es(String dir_emp_es) {
				stringBuffer.append(String.format("\tDir. emp. escalera: \"%S\"\n", dir_emp_es));				
			}

			@Override
			public void visit_dir_emp_piso(String dir_emp_piso) {
				stringBuffer.append(String.format("\tDir. emp. piso: \"%S\"\n", dir_emp_piso));				
			}

			@Override
			public void visit_dir_emp_p(String dir_emp_p) {
				stringBuffer.append(String.format("\tDir. emp. puerta: \"%S\"\n", dir_emp_p));				
			}

			@Override
			public void visit_dir_emp_CP(String dir_emp_CP) {
				stringBuffer.append(String.format("\tCP empresa: \"%S\"\n", dir_emp_CP));				
			}

			@Override
			public void visit_dir_emp_num_muni(String dir_emp_num_muni) {
				stringBuffer.append(String.format("\tNum municipio empresa: \"%S\"\n", dir_emp_num_muni));				
			}

			@Override
			public void visit_dir_emp_nom_muni(String dir_emp_nom_muni) {
				stringBuffer.append(String.format("\tMunicipio empresa: \"%S\"\n", dir_emp_nom_muni));				
			}

			@Override
			public void visit_dir_emp_tlf(String dir_emp_tlf) {
				stringBuffer.append(String.format("\tTlf. empresa: \"%S\"\n", dir_emp_tlf));				
			}

			@Override
			public void visit_notif_dom_actividad(Boolean notif_dom_actividad) {
				stringBuffer.append(String.format("\tNotif. domicilio actividad: \"%S\"\n", notif_dom_actividad));				
			}

			@Override
			public void visit_act_ugtgss(String act_ugtgss) {
				stringBuffer.append(String.format("\tAct UGTGSS: \"%S\"\n", act_ugtgss));				
			}
			
			@Override
			public void visit_tipo_via_dir_actividad(String tipo_via_dir_actividad) {
				stringBuffer.append(String.format("\tTipo vía dirección actividad: \"%S\"\n", tipo_via_dir_actividad));				
			}

			@Override
			public void visit_dir_act_calle(String dir_act_calle) {
				stringBuffer.append(String.format("\tDir. actividad calle: \"%S\"\n", dir_act_calle));				
			}

			@Override
			public void visit_dir_act_num(String dir_act_num) {
				stringBuffer.append(String.format("\tDir. actividad número: \"%S\"\n", dir_act_num));				
			}

			@Override
			public void visit_dir_act_bis(String dir_act_bis) {
				stringBuffer.append(String.format("\tDir. actividad bis.: \"%S\"\n", dir_act_bis));
				
			}

			@Override
			public void visit_dir_act_bloq(String dir_act_bloq) {
				stringBuffer.append(String.format("\tDir. act. bloque: \"%S\"\n", dir_act_bloq));
				
			}

			@Override
			public void visit_dir_act_es(String dir_act_es) {
				stringBuffer.append(String.format("\tDir. act. escalera: \"%S\"\n", dir_act_es));
				
			}

			@Override
			public void visit_dir_act_piso(String dir_act_piso) {
				stringBuffer.append(String.format("\tDir. act. piso: \"%S\"\n", dir_act_piso));
				
			}

			@Override
			public void visit_dir_act_p(String dir_act_p) {
				stringBuffer.append(String.format("\tDir. act. puerta: \"%S\"\n", dir_act_p));
				
			}

			@Override
			public void visit_dir_act_CP(String dir_act_CP) {
				stringBuffer.append(String.format("\tCP actividad: \"%S\"\n", dir_act_CP));
				
			}

			@Override
			public void visit_dir_act_num_muni(String dir_act_num_muni) {
				stringBuffer.append(String.format("\tDir. act. num. municipio: \"%S\"\n", dir_act_num_muni));
				
			}

			@Override
			public void visit_dir_act_nom_muni(String dir_act_nom_muni) {
				stringBuffer.append(String.format("\tDir. act. nom. muni.: \"%S\"\n", dir_act_nom_muni));
				
			}

			@Override
			public void visit_dir_act_tlf(String dir_act_tlf) {
				stringBuffer.append(String.format("\tDir. act. tlf: \"%S\"\n", dir_act_tlf));
				
			}
			
		});
		return stringBuffer.toString();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public String getTipo_via_dir_actividad() {
		return tipo_via_dir_actividad;
	}

	public void setTipo_via_dir_actividad(String tipo_via_dir_actividad) {
		this.tipo_via_dir_actividad = tipo_via_dir_actividad;
	}

	public String getCcc() {
		return ccc;
	}

	public void setCcc(String ccc) {
		this.ccc = ccc;
	}

	public String getRegimen() {
		return regimen;
	}

	public void setRegimen(String regimen) {
		this.regimen = regimen;
	}

	public String getIdEmpresario() {
		return idEmpresario;
	}

	public void setIdEmpresario(String idEmpresario) {
		this.idEmpresario = idEmpresario;
	}

	public String getNif_empresa() {
		return nif_empresa;
	}

	public void setNif_empresa(String nif_empresa) {
		this.nif_empresa = nif_empresa;
	}

	public String getNss() {
		return nss;
	}

	public void setNss(String nss) {
		this.nss = nss;
	}

	public String getCccp() {
		return cccp;
	}

	public void setCccp(String cccp) {
		this.cccp = cccp;
	}

	public String getUgtgss() {
		return ugtgss;
	}

	public void setUgtgss(String ugtgss) {
		this.ugtgss = ugtgss;
	}

	public String getUgtgsscccp() {
		return ugtgsscccp;
	}

	public void setUgtgsscccp(String ugtgsscccp) {
		this.ugtgsscccp = ugtgsscccp;
	}

	public String getUgcentral() {
		return ugcentral;
	}

	public void setUgcentral(String ugcentral) {
		this.ugcentral = ugcentral;
	}

	public String getOgism() {
		return ogism;
	}

	public void setOgism(String ogism) {
		this.ogism = ogism;
	}

	public String getCccAnt() {
		return cccAnt;
	}

	public void setCccAnt(String cccAnt) {
		this.cccAnt = cccAnt;
	}

	public String getCccSuc() {
		return cccSuc;
	}

	public void setCccSuc(String cccSuc) {
		this.cccSuc = cccSuc;
	}

	public String getSit() {
		return sit;
	}

	public void setSit(String sit) {
		this.sit = sit;
	}

	public Date getfSit() {
		return fSit;
	}

	public void setfSit(Date fSit) {
		this.fSit = fSit;
	}

	public Date getfAltaInicial() {
		return fAltaInicial;
	}

	public void setfAltaInicial(Date fAltaInicial) {
		this.fAltaInicial = fAltaInicial;
	}

	public Integer getTrabajadorAlta() {
		return trabajadorAlta;
	}

	public void setTrabajadorAlta(Integer trabajadorAlta) {
		this.trabajadorAlta = trabajadorAlta;
	}

	public Date getAltaPrTrab() {
		return altaPrTrab;
	}

	public void setAltaPrTrab(Date altaPrTrab) {
		this.altaPrTrab = altaPrTrab;
	}

	public Date getUltBajaEfCot() {
		return ultBajaEfCot;
	}

	public void setUltBajaEfCot(Date ultBajaEfCot) {
		this.ultBajaEfCot = ultBajaEfCot;
	}

	public String getTrl() {
		return trl;
	}

	public void setTrl(String trl) {
		this.trl = trl;
	}

	public String getcEsp_num() {
		return cEsp_num;
	}

	public void setcEsp_num(String cEsp_num) {
		this.cEsp_num = cEsp_num;
	}

	public String getcEsp_cad() {
		return cEsp_cad;
	}

	public void setcEsp_cad(String cEsp_cad) {
		this.cEsp_cad = cEsp_cad;
	}

	public String getCnae93_num() {
		return cnae93_num;
	}

	public void setCnae93_num(String cnae93_num) {
		this.cnae93_num = cnae93_num;
	}

	public String getCnae93_cad() {
		return cnae93_cad;
	}

	public void setCnae93_cad(String cnae93_cad) {
		this.cnae93_cad = cnae93_cad;
	}

	public Integer getTa2Alta() {
		return ta2Alta;
	}

	public void setTa2Alta(Integer ta2Alta) {
		this.ta2Alta = ta2Alta;
	}

	public Integer getTa2Baja() {
		return ta2Baja;
	}

	public void setTa2Baja(Integer ta2Baja) {
		this.ta2Baja = ta2Baja;
	}

	public String getCnae09_num() {
		return cnae09_num;
	}

	public void setCnae09_num(String cnae09_num) {
		this.cnae09_num = cnae09_num;
	}

	public String getCnae09_cad() {
		return cnae09_cad;
	}

	public void setCnae09_cad(String cnae09_cad) {
		this.cnae09_cad = cnae09_cad;
	}

	public Float getTiposATyEPIT() {
		return tiposATyEPIT;
	}

	public void setTiposATyEPIT(Float tiposATyEPIT) {
		this.tiposATyEPIT = tiposATyEPIT;
	}

	public Float getIms() {
		return ims;
	}

	public void setIms(Float ims) {
		this.ims = ims;
	}

	public Float getTotal() {
		return total;
	}

	public void setTotal(Float total) {
		this.total = total;
	}

	public String getCoeJub_num() {
		return coeJub_num;
	}

	public void setCoeJub_num(String coeJub_num) {
		this.coeJub_num = coeJub_num;
	}

	public String getCoeJub_cad() {
		return coeJub_cad;
	}

	public void setCoeJub_cad(String coeJub_cad) {
		this.coeJub_cad = coeJub_cad;
	}

	public String getAconExtra() {
		return aconExtra;
	}

	public void setAconExtra(String aconExtra) {
		this.aconExtra = aconExtra;
	}

	public Boolean getEscTaller() {
		return escTaller;
	}

	public void setEscTaller(Boolean escTaller) {
		this.escTaller = escTaller;
	}

	public String getAutorizacionRed() {
		return autorizacionRed;
	}

	public void setAutorizacionRed(String autorizacionRed) {
		this.autorizacionRed = autorizacionRed;
	}

	public Date getPlazoIncorpRed() {
		return plazoIncorpRed;
	}

	public void setPlazoIncorpRed(Date plazoIncorpRed) {
		this.plazoIncorpRed = plazoIncorpRed;
	}

	public Date getFechaAutCan() {
		return fechaAutCan;
	}

	public void setFechaAutCan(Date fechaAutCan) {
		this.fechaAutCan = fechaAutCan;
	}

	public String getAnagrama() {
		return anagrama;
	}

	public void setAnagrama(String anagrama) {
		this.anagrama = anagrama;
	}

	public String getEmbarcacion() {
		return embarcacion;
	}

	public void setEmbarcacion(String embarcacion) {
		this.embarcacion = embarcacion;
	}

	public String getTlfMovil() {
		return tlfMovil;
	}

	public void setTlfMovil(String tlfMovil) {
		this.tlfMovil = tlfMovil;
	}

	public String getTlfFijo() {
		return tlfFijo;
	}

	public void setTlfFijo(String tlfFijo) {
		this.tlfFijo = tlfFijo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean isNotif_dom_empresa() {
		return notif_dom_empresa;
	}

	public void setNotif_dom_empresa(Boolean notif_dom_empresa) {
		this.notif_dom_empresa = notif_dom_empresa;
	}

	public String getDir_emp_calle() {
		return dir_emp_calle;
	}

	public String getTipo_via_dir_empresa() {
		return tipo_via_dir_empresa;
	}

	public void setTipo_via_dir_empresa(String tipo_via_dir_empresa) {
		this.tipo_via_dir_empresa = tipo_via_dir_empresa;
	}

	public Boolean getNotif_dom_empresa() {
		return notif_dom_empresa;
	}

	public Boolean getNotif_dom_actividad() {
		return notif_dom_actividad;
	}

	public void setDir_emp_calle(String dir_emp_calle) {
		this.dir_emp_calle = dir_emp_calle;
	}

	public String getDir_emp_num() {
		return dir_emp_num;
	}

	public void setDir_emp_num(String dir_emp_num) {
		this.dir_emp_num = dir_emp_num;
	}

	public String getDir_emp_bis() {
		return dir_emp_bis;
	}

	public void setDir_emp_bis(String dir_emp_bis) {
		this.dir_emp_bis = dir_emp_bis;
	}

	public String getDir_emp_bloq() {
		return dir_emp_bloq;
	}

	public void setDir_emp_bloq(String dir_emp_bloq) {
		this.dir_emp_bloq = dir_emp_bloq;
	}

	public String getDir_emp_es() {
		return dir_emp_es;
	}

	public void setDir_emp_es(String dir_emp_es) {
		this.dir_emp_es = dir_emp_es;
	}

	public String getDir_emp_piso() {
		return dir_emp_piso;
	}

	public void setDir_emp_piso(String dir_emp_piso) {
		this.dir_emp_piso = dir_emp_piso;
	}

	public String getDir_emp_p() {
		return dir_emp_p;
	}

	public void setDir_emp_p(String dir_emp_p) {
		this.dir_emp_p = dir_emp_p;
	}

	public String getDir_emp_CP() {
		return dir_emp_CP;
	}

	public void setDir_emp_CP(String dir_emp_CP) {
		this.dir_emp_CP = dir_emp_CP;
	}

	public String getDir_emp_num_muni() {
		return dir_emp_num_muni;
	}

	public void setDir_emp_num_muni(String dir_emp_num_muni) {
		this.dir_emp_num_muni = dir_emp_num_muni;
	}

	public String getDir_emp_nom_muni() {
		return dir_emp_nom_muni;
	}

	public void setDir_emp_nom_muni(String dir_emp_nom_muni) {
		this.dir_emp_nom_muni = dir_emp_nom_muni;
	}

	public String getDir_emp_tlf() {
		return dir_emp_tlf;
	}

	public void setDir_emp_tlf(String dir_emp_tlf) {
		this.dir_emp_tlf = dir_emp_tlf;
	}

	public Boolean isNotif_dom_actividad() {
		return notif_dom_actividad;
	}

	public void setNotif_dom_actividad(Boolean notif_dom_actividad) {
		this.notif_dom_actividad = notif_dom_actividad;
	}

	public String getAct_ugtgss() {
		return act_ugtgss;
	}

	public void setAct_ugtgss(String act_ugtgss) {
		this.act_ugtgss = act_ugtgss;
	}

	public String getDir_act_calle() {
		return dir_act_calle;
	}

	public void setDir_act_calle(String dir_act_calle) {
		this.dir_act_calle = dir_act_calle;
	}

	public String getDir_act_num() {
		return dir_act_num;
	}

	public void setDir_act_num(String dir_act_num) {
		this.dir_act_num = dir_act_num;
	}

	public String getDir_act_bis() {
		return dir_act_bis;
	}

	public void setDir_act_bis(String dir_act_bis) {
		this.dir_act_bis = dir_act_bis;
	}

	public String getDir_act_bloq() {
		return dir_act_bloq;
	}

	public void setDir_act_bloq(String dir_act_bloq) {
		this.dir_act_bloq = dir_act_bloq;
	}

	public String getDir_act_es() {
		return dir_act_es;
	}

	public void setDir_act_es(String dir_act_es) {
		this.dir_act_es = dir_act_es;
	}

	public String getDir_act_piso() {
		return dir_act_piso;
	}

	public void setDir_act_piso(String dir_act_piso) {
		this.dir_act_piso = dir_act_piso;
	}

	public String getDir_act_p() {
		return dir_act_p;
	}

	public void setDir_act_p(String dir_act_p) {
		this.dir_act_p = dir_act_p;
	}

	public String getDir_act_CP() {
		return dir_act_CP;
	}

	public void setDir_act_CP(String dir_act_CP) {
		this.dir_act_CP = dir_act_CP;
	}

	public String getDir_act_num_muni() {
		return dir_act_num_muni;
	}

	public void setDir_act_num_muni(String dir_act_num_muni) {
		this.dir_act_num_muni = dir_act_num_muni;
	}

	public String getDir_act_nom_muni() {
		return dir_act_nom_muni;
	}

	public void setDir_act_nom_muni(String dir_act_nom_muni) {
		this.dir_act_nom_muni = dir_act_nom_muni;
	}

	public String getDir_act_tlf() {
		return dir_act_tlf;
	}

	public void setDir_act_tlf(String dir_act_tlf) {
		this.dir_act_tlf = dir_act_tlf;
	}

	
	
	
	private SituacionEmpresa () {
		
	}
	
	public static class SituacionEmpresaBuilder{
		private String ccc;
		private String regimen;
		private String idEmpresario;
		private String nif_empresa;
		private String nss;
		private String cccp;
		private String ugtgss;
		private String ugtgsscccp;
		private String ugcentral;
		private String ogism;
		private String cccAnt;
		private String cccSuc;
		private String sit;
		private Date fSit;
		private Date fAltaInicial;
		private Integer trabajadorAlta;
		private Date altaPrTrab;
		private Date ultBajaEfCot;
		private String trl;
		private String cEsp_num;
		private String cEsp_cad;
		private String cnae93_num;
		private String cnae93_cad;
		private Integer ta2Alta;
		private Integer ta2Baja;


		private String cnae09_num;
		private String cnae09_cad;
		private Float tiposATyEPIT;
		private Float ims;
		private Float total;
		private String coeJub_num;
		private String coeJub_cad;
		private String aconExtra;
		private Boolean escTaller;
		private String autorizacionRed;
		private Date plazoIncorpRed;
		private Date fechaAutCan;
		
		//DATOS DE GESTIÓN (no repetidos)
		private String anagrama;
		private String embarcacion;
		private String tlfMovil;
		private String tlfFijo;
		private String email;
		private Boolean notif_dom_empresa;
		private String tipo_via_dir_empresa;
		private String tipo_via_dir_actividad;

		
		private String dir_emp_calle;
		private String dir_emp_num;
		private String dir_emp_bis;
		private String dir_emp_bloq;
		private String dir_emp_es;
		private String dir_emp_piso;
		private String dir_emp_p;
		private String dir_emp_CP;
		private String dir_emp_num_muni;
		private String  dir_emp_nom_muni;
		private String dir_emp_tlf;
		private Boolean notif_dom_actividad;
		private String act_ugtgss;
		private String dir_act_calle;
		private String dir_act_num;

		private String dir_act_bis;
		private String dir_act_bloq;
		private String dir_act_es;
		private String dir_act_piso;
		private String dir_act_p;
		private String dir_act_CP;
		private String dir_act_num_muni;
		private String  dir_act_nom_muni;
		private String dir_act_tlf;
		
		
		
		public SituacionEmpresaBuilder setCcc(String ccc) {
			if(!((ccc.equals(""))||(ccc==null)))
				this.ccc = ccc;
			else
				this.ccc=null;
			return this;
		}
		public SituacionEmpresaBuilder setRegimen(String regimen) {
			if(!((regimen.equals(""))||(regimen==null)))
				this.regimen = regimen;
			else
				this.regimen=null;
			return this;
		}
		public SituacionEmpresaBuilder setIdEmpresario(String idEmpresario) {
			if(!((idEmpresario.equals(""))||(idEmpresario==null)))
				this.idEmpresario = idEmpresario;
			else
				this.idEmpresario=null;
			return this;
		}
		public SituacionEmpresaBuilder setNif_empresa(String nif_empresa) {
			if(!((nif_empresa.equals(""))||(nif_empresa==null)))
				this.nif_empresa = nif_empresa;
			else
				this.nif_empresa=null;
			return this;
		}
		public SituacionEmpresaBuilder setNss(String nss) {
			if(!((nss.equals(""))||(nss==null)))
				this.nss = nss;
			else
				this.nss=null;
			return this;
		}
		public SituacionEmpresaBuilder setCccp(String cccp) {
			if(!((cccp.equals(""))||(cccp==null)))
				this.cccp = cccp;
			else
				this.cccp=null;
			return this;
		}
		public SituacionEmpresaBuilder setUgtgss(String ugtgss) {
			if(!((ugtgss.equals(""))||(ugtgss==null)))
				this.ugtgss = ugtgss;
			else
				this.ugtgss=null;
			return this;
		}
		public SituacionEmpresaBuilder setUgtgsscccp(String ugtgsscccp) {
			if(!((ugtgsscccp.equals(""))||(ugtgsscccp==null)))
				this.ugtgsscccp = ugtgsscccp;
			else
				this.ugtgsscccp=null;
			return this;
		}
		public SituacionEmpresaBuilder setUgcentral(String ugcentral) {
			if(!((ugcentral.equals(""))||(ugcentral==null)))
				this.ugcentral = ugcentral;
			else
				this.ugcentral=null;
			return this;
		}
		public SituacionEmpresaBuilder setOgism(String ogism) {
			if(!((ogism.equals(""))||(ogism==null)))
				this.ogism = ogism;
			else
				this.ogism=null;
			return this;
		}
		public SituacionEmpresaBuilder setCccAnt(String cccAnt) {
			if(!((cccAnt.equals(""))||(cccAnt==null)))
				this.cccAnt = cccAnt;
			else
				this.cccAnt=null;
			return this;
		}
		public SituacionEmpresaBuilder setCccSuc(String cccSuc) {
			if(!((cccSuc.equals(""))||(cccSuc==null)))
				this.cccSuc = cccSuc;
			else
				this.cccSuc=null;
			return this;
		}
		public SituacionEmpresaBuilder setSit(String sit) {
			if(!((sit.equals(""))||(sit==null)))
				this.sit = sit;
			else
				this.sit=null;
			return this;
		}
		public SituacionEmpresaBuilder setfSit(Date fSit) {
			this.fSit = fSit;
			return this;
		}
		public SituacionEmpresaBuilder setfAltaInicial(Date fAltaInicial) {
			this.fAltaInicial = fAltaInicial;
			return this;
		}
		public SituacionEmpresaBuilder setTrabajadorAlta(Integer trabajadorAlta) {
			this.trabajadorAlta = trabajadorAlta;
			return this;
		}
		public SituacionEmpresaBuilder setAltaPrTrab(Date altaPrTrab) {
			this.altaPrTrab = altaPrTrab;
			return this;
		}
		public SituacionEmpresaBuilder setUltBajaEfCot(Date ultBajaEfCot) {
			this.ultBajaEfCot = ultBajaEfCot;
			return this;
		}
		public SituacionEmpresaBuilder setTrl(String trl) {
			if(!((trl.equals(""))||(trl==null)))
				this.trl = trl;
			else
				this.trl=null;
			return this;
		}
		public SituacionEmpresaBuilder setcEsp_num(String cEsp_num) {
			if(!((cEsp_num.equals(""))||(cEsp_num==null)))
				this.cEsp_num = cEsp_num;
			else
				this.cEsp_num=null;
			return this;
		}
		public SituacionEmpresaBuilder setcEsp_cad(String cEsp_cad) {
			if(!((cEsp_cad.equals(""))||(cEsp_cad==null)))
				this.cEsp_cad = cEsp_cad;
			else
				this.cEsp_cad=null;
			return this;
		}
		public SituacionEmpresaBuilder setCnae93_num(String cnae93_num) {
			if(!((cnae93_num.equals(""))||(cnae93_num==null)))
				this.cnae93_num = cnae93_num;
			else
				this.cnae93_num=null;
			return this;
		}
		public SituacionEmpresaBuilder setCnae93_cad(String cnae93_cad) {
			if(!((cnae93_cad.equals(""))||(cnae93_cad==null)))
				this.cnae93_cad = cnae93_cad;
			else
				this.cnae93_cad=null;
			return this;
		}
		public SituacionEmpresaBuilder setTa2Alta(Integer ta2Alta) {
			this.ta2Alta = ta2Alta;
			return this;
		}
		public SituacionEmpresaBuilder setTa2Baja(Integer ta2Baja) {
			
			this.ta2Baja = ta2Baja;
			return this;
		}
		public SituacionEmpresaBuilder setCnae09_num(String cnae09_num) {
			if(!((cnae09_num.equals(""))||(cnae09_num==null)))
				this.cnae09_num = cnae09_num;
			else
				this.cnae09_num=null;
			return this;
		}
		public SituacionEmpresaBuilder setCnae09_cad(String cnae09_cad) {
			if(!((cnae09_cad.equals(""))||(cnae09_cad==null)))
				this.cnae09_cad = cnae09_cad;
			else
				this.cnae09_cad=null;
			return this;
		}
		public SituacionEmpresaBuilder setTiposATyEPIT(Float tiposATyEPIT) {
			this.tiposATyEPIT = tiposATyEPIT;
			return this;
		}
		public SituacionEmpresaBuilder setIms(Float ims) {
			this.ims = ims;
			return this;
		}
		public SituacionEmpresaBuilder setTotal(Float total) {
			this.total = total;
			return this;
		}
		public SituacionEmpresaBuilder setCoeJub_num(String coeJub_num) {
			if(!((coeJub_num.equals(""))||(coeJub_num==null)))
				this.coeJub_num = coeJub_num;
			else
				this.coeJub_num=null;
			return this;
		}
		public SituacionEmpresaBuilder setCoeJub_cad(String coeJub_cad) {
			if(!((coeJub_cad.equals(""))||(coeJub_cad==null)))
				this.coeJub_cad = coeJub_cad;
			else
				this.coeJub_cad=null;
			return this;
		}
		public SituacionEmpresaBuilder setAconExtra(String aconExtra) {
			if(!((aconExtra.equals(""))||(aconExtra==null)))
				this.aconExtra = aconExtra;
			else
				this.aconExtra=null;
			return this;
		}
		public SituacionEmpresaBuilder setEscTaller(Boolean escTaller) {
			
			this.escTaller = escTaller;
			return this;
		}
		public SituacionEmpresaBuilder setAutorizacionRed(String autorizacionRed) {
			if(!((autorizacionRed.equals(""))||(autorizacionRed==null)))
				this.autorizacionRed = autorizacionRed;
			else
				this.autorizacionRed=null;
			return this;
		}
		public SituacionEmpresaBuilder setPlazoIncorpRed(Date plazoIncorpRed) {
			
			this.plazoIncorpRed = plazoIncorpRed;
			return this;
		}
		public SituacionEmpresaBuilder setFechaAutCan(Date fechaAutCan) {
			this.fechaAutCan = fechaAutCan;
			return this;
		}
		public SituacionEmpresaBuilder setAnagrama(String anagrama) {
			if(!((anagrama.equals(""))||(anagrama==null)))
				this.anagrama = anagrama;
			else
				this.anagrama=null;
			return this;
		}
		public SituacionEmpresaBuilder setEmbarcacion(String embarcacion) {
			if(!((embarcacion.equals(""))||(embarcacion==null)))
				this.embarcacion = embarcacion;
			else
				this.embarcacion=null;
			return this;
		}
		public SituacionEmpresaBuilder setTlfMovil(String tlfMovil) {
			if(!((tlfMovil.equals(""))||(tlfMovil==null)))
				this.tlfMovil = tlfMovil;
			else
				this.tlfMovil=null;
			return this;
		}
		public SituacionEmpresaBuilder setTlfFijo(String tlfFijo) {
			if(!((tlfFijo.equals(""))||(tlfFijo==null)))
				this.tlfFijo = tlfFijo;
			else
				this.tlfFijo=null;
			return this;
		}
		public SituacionEmpresaBuilder setEmail(String email) {
			if(!((email.equals(""))||(email==null)))
				this.email = email;
			else
				this.email=null;
			return this;
		}
		public SituacionEmpresaBuilder setNotif_dom_empresa(Boolean notif_dom_empresa) {
			this.notif_dom_empresa = notif_dom_empresa;
			return this;
		}
		public SituacionEmpresaBuilder setDir_emp_calle(String dir_emp_calle) {
			if(!((dir_emp_calle.equals(""))||(dir_emp_calle==null)))
				this.dir_emp_calle = dir_emp_calle;
			else
				this.dir_emp_calle=null;
			return this;
		}
		
		public SituacionEmpresaBuilder setTipo_via_dir_empresa(String tipo_via_dir_empresa) {
			if(!((tipo_via_dir_empresa.equals(""))||(tipo_via_dir_empresa==null)))
				this.tipo_via_dir_empresa = tipo_via_dir_empresa;
			else
				this.tipo_via_dir_empresa=null;
			return this;
		}
		
		public SituacionEmpresaBuilder setTipo_via_dir_actividad(String tipo_via_dir_actividad) {
			if(!((tipo_via_dir_actividad.equals(""))||(tipo_via_dir_actividad==null)))
				this.tipo_via_dir_actividad = tipo_via_dir_actividad;
			else
				this.tipo_via_dir_actividad=null;
			return this;
		}
		
		
		public SituacionEmpresaBuilder setDir_emp_num(String dir_emp_num) {
			if(!((dir_emp_num.equals(""))||(dir_emp_num==null)))
				this.dir_emp_num = dir_emp_num;
			else
				this.dir_emp_num=null;
			return this;
		}
		public SituacionEmpresaBuilder setDir_emp_bis(String dir_emp_bis) {
			if(!((dir_emp_bis.equals(""))||(dir_emp_bis==null)))
				this.dir_emp_bis = dir_emp_bis;
			else
				this.dir_emp_bis=null;
			return this;
		}
		public SituacionEmpresaBuilder setDir_emp_bloq(String dir_emp_bloq) {
			if(!((dir_emp_bloq.equals(""))||(dir_emp_bloq==null)))
				this.dir_emp_bloq = dir_emp_bloq;
			else
				this.dir_emp_bloq=null;
			return this;
		}
		public SituacionEmpresaBuilder setDir_emp_es(String dir_emp_es) {
			if(!((dir_emp_es.equals(""))||(dir_emp_es==null)))
				this.dir_emp_es = dir_emp_es;
			else
				this.dir_emp_es=null;
			return this;
		}
		public SituacionEmpresaBuilder setDir_emp_piso(String dir_emp_piso) {
			if(!((dir_emp_piso.equals(""))||(dir_emp_piso==null)))
				this.dir_emp_piso = dir_emp_piso;
			else
				this.dir_emp_piso=null;
			return this;
		}
		public SituacionEmpresaBuilder setDir_emp_p(String dir_emp_p) {
			if(!((dir_emp_p.equals(""))||(dir_emp_p==null)))
				this.dir_emp_p = dir_emp_p;
			else
				this.dir_emp_p=null;
			return this;
		}
		
		public SituacionEmpresaBuilder setDir_emp_CP(String dir_emp_CP) {
			if(!((dir_emp_CP.equals(""))||(dir_emp_CP==null)))
				this.dir_emp_CP = dir_emp_CP;
			else
				this.dir_emp_CP=null;
			return this;
		}
		public SituacionEmpresaBuilder setDir_emp_num_muni(String dir_emp_num_muni) {
			if(!((dir_emp_num_muni.equals(""))||(dir_emp_num_muni==null)))
				this.dir_emp_num_muni = dir_emp_num_muni;
			else
				this.dir_emp_num_muni=null;
			return this;
		}
		public SituacionEmpresaBuilder setDir_emp_nom_muni(String dir_emp_nom_muni) {
			if(!((dir_emp_nom_muni.equals(""))||(dir_emp_nom_muni==null)))
				this.dir_emp_nom_muni = dir_emp_nom_muni;
			else
				this.dir_emp_nom_muni=null;
			return this;
		}
		public SituacionEmpresaBuilder setDir_emp_tlf(String dir_emp_tlf) {
			if(!((dir_emp_tlf.equals(""))||(dir_emp_tlf==null)))
				this.dir_emp_tlf = dir_emp_tlf;
			else
				this.dir_emp_tlf=null;
			return this;
		}
		public SituacionEmpresaBuilder setNotif_dom_actividad(Boolean notif_dom_actividad) {
			this.notif_dom_actividad = notif_dom_actividad;
			return this;
		}
		public SituacionEmpresaBuilder setAct_ugtgss(String act_ugtgss) {
			if(!((act_ugtgss.equals(""))||(act_ugtgss==null)))
				this.act_ugtgss = act_ugtgss;
			else
				this.act_ugtgss=null;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_calle(String dir_act_calle) {
			if(!((dir_act_calle.equals(""))||(dir_act_calle==null)))
				this.dir_act_calle = dir_act_calle;
			else
				this.dir_act_calle=null;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_num(String dir_act_num) {
			if(!((dir_act_num.equals(""))||(dir_act_num==null)))
				this.dir_act_num = dir_act_num;
			else
				this.dir_act_num=null;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_bis(String dir_act_bis) {
			if(!((dir_act_bis.equals(""))||(dir_act_bis==null)))
				this.dir_act_bis = dir_act_bis;
			else
				this.dir_act_bis=null;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_bloq(String dir_act_bloq) {
			if(!((dir_act_bloq.equals(""))||(dir_act_bloq==null)))
				this.dir_act_bloq = dir_act_bloq;
			else
				this.dir_act_bloq=null;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_es(String dir_act_es) {
			if(!((dir_act_es.equals(""))||(dir_act_es==null)))
				this.dir_act_es = dir_act_es;
			else
				this.dir_act_es=null;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_piso(String dir_act_piso) {
			if(!((dir_act_piso.equals(""))||(dir_act_piso==null)))
				this.dir_act_piso = dir_act_piso;
			else
				this.dir_act_piso=null;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_p(String dir_act_p) {
			if(!((dir_act_p.equals(""))||(dir_act_p==null)))
				this.dir_act_p = dir_act_p;
			else
				this.dir_act_p=null;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_CP(String dir_act_CP) {
			if(!((dir_act_CP.equals(""))||(dir_act_CP==null)))
				this.dir_act_CP = dir_act_CP;
			else
				this.dir_act_CP=null;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_num_muni(String dir_act_num_muni) {
			if(!((dir_act_num_muni.equals(""))||(dir_act_num_muni==null)))
				this.dir_act_num_muni = dir_act_num_muni;
			else
				this.dir_act_num_muni=null;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_nom_muni(String dir_act_nom_muni) {
			if(!((dir_act_nom_muni.equals(""))||(dir_act_nom_muni==null)))
				this.dir_act_nom_muni = dir_act_nom_muni;
			else
				this.dir_act_nom_muni=null;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_tlf(String dir_act_tlf) {
			if(!((dir_act_tlf.equals(""))||(dir_act_tlf==null)))
				this.dir_act_tlf = dir_act_tlf;
			else
				this.dir_act_tlf=null;
			return this;
		}
		public SituacionEmpresa build() {
			SituacionEmpresa r=new SituacionEmpresa();
			r.setAconExtra(this.aconExtra);
			r.setAct_ugtgss(this.act_ugtgss);
			r.setAltaPrTrab(this.altaPrTrab);
			r.setAnagrama(this.anagrama);
			r.setAutorizacionRed(this.autorizacionRed);
			r.setCcc(this.ccc);
			r.setCccAnt(this.cccAnt);
			r.setCccp(this.cccp);
			r.setCccSuc(this.cccSuc);
			r.setcEsp_cad(this.cEsp_cad);
			r.setcEsp_num(this.cEsp_num);
			r.setCnae09_cad(this.cnae09_cad);
			r.setCnae09_num(this.cnae09_num);
			r.setCnae93_cad(this.cnae93_cad);
			r.setCnae93_num(this.cnae93_num);
			r.setCoeJub_cad(this.coeJub_cad);
			r.setCoeJub_num(this.coeJub_num);
			r.setDir_act_bis(this.dir_act_bis);
			r.setDir_act_bloq(this.dir_act_bloq);
			r.setDir_act_calle(this.dir_act_calle);
			r.setDir_act_CP(this.dir_act_CP);
			r.setDir_act_es(this.dir_act_es);
			r.setDir_act_nom_muni(this.dir_act_nom_muni);
			r.setDir_act_p(this.dir_act_p);
			r.setDir_act_piso(this.dir_act_piso);
			r.setDir_act_tlf(this.dir_act_tlf);
			r.setDir_emp_bis(this.dir_emp_bis);
			r.setDir_emp_bloq(this.dir_emp_bloq);
			r.setDir_emp_calle(this.dir_emp_calle);
			r.setDir_emp_CP(this.dir_emp_CP);
			r.setDir_emp_es(this.dir_emp_es);
			r.setDir_emp_nom_muni(this.dir_emp_nom_muni);
			r.setDir_emp_num(this.dir_emp_num);
			r.setDir_emp_num_muni(this.dir_emp_num_muni);
			r.setDir_emp_p(this.dir_emp_p);
			r.setDir_emp_piso(this.dir_emp_piso);
			r.setDir_emp_tlf(this.dir_emp_tlf);
			r.setEmail(this.email);
			r.setEmbarcacion(this.embarcacion);
			r.setEscTaller(this.escTaller);
			r.setfAltaInicial(this.fAltaInicial);
			r.setFechaAutCan(this.fechaAutCan);
			r.setfSit(this.fSit);
			r.setIdEmpresario(this.idEmpresario);
			r.setIms(this.ims);
			r.setNif_empresa(this.nif_empresa);
			r.setNotif_dom_actividad(this.notif_dom_actividad);
			r.setNotif_dom_empresa(this.notif_dom_empresa);
			r.setNss(this.nss);
			r.setOgism(this.ogism);
			r.setPlazoIncorpRed(this.plazoIncorpRed);
			r.setRegimen(this.regimen);
			r.setSit(this.sit);
			r.setTa2Alta(this.ta2Alta);
			r.setTa2Baja(this.ta2Baja);
			r.setTiposATyEPIT(this.tiposATyEPIT);
			r.setTlfFijo(this.tlfFijo);
			r.setTlfMovil(this.tlfMovil);
			r.setTotal(this.total);
			r.setTrabajadorAlta(this.trabajadorAlta);
			r.setTrl(this.trl);
			r.setUgcentral(this.ugcentral);
			r.setUgtgss(this.ugtgss);
			r.setUgtgsscccp(this.ugtgsscccp);
			r.setUltBajaEfCot(this.ultBajaEfCot);
			r.setTipo_via_dir_empresa(this.tipo_via_dir_empresa);
			r.setTipo_via_dir_actividad(this.tipo_via_dir_actividad);
			r.setDir_act_num(this.dir_act_num);
			r.setDir_act_num_muni(this.dir_act_num_muni);
			return r;
		}
		
		
	}
}