package solutions.aon.seg.social;

import java.util.Collections;
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
	/*private String ccc;
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
	private String dir_act_tlf;*/
		
		
		//public String visit
	}
	public static interface Visitable {
		public void accept(Visitor visitor);
	};
	
	
	@Override
	public String toString() {
		return "SituacionEmpresa [ccc=" + ccc + ", regimen=" + regimen + ", idEmpresario=" + idEmpresario
				+ ", nif_empresa=" + nif_empresa + ", nss=" + nss + ", cccp=" + cccp + ", ugtgss=" + ugtgss
				+ ", ugtgsscccp=" + ugtgsscccp + ", ugcentral=" + ugcentral + ", ogism=" + ogism + ", cccAnt=" + cccAnt
				+ ", cccSuc=" + cccSuc + ", sit=" + sit + ", fSit=" + fSit + ", fAltaInicial=" + fAltaInicial
				+ ", trabajadorAlta=" + trabajadorAlta + ", altaPrTrab=" + altaPrTrab + ", ultBajaEfCot=" + ultBajaEfCot
				+ ", trl=" + trl + ", cEsp_num=" + cEsp_num + ", cEsp_cad=" + cEsp_cad + ", cnae93_num=" + cnae93_num
				+ ", cnae93_cad=" + cnae93_cad + ", ta2Alta=" + ta2Alta + ", ta2Baja=" + ta2Baja + ", cnae09_num="
				+ cnae09_num + ", cnae09_cad=" + cnae09_cad + ", tiposATyEPIT=" + tiposATyEPIT + ", ims=" + ims
				+ ", total=" + total + ", coeJub_num=" + coeJub_num + ", coeJub_cad=" + coeJub_cad + ", aconExtra="
				+ aconExtra + ", escTaller=" + escTaller + ", autorizacionRed=" + autorizacionRed + ", plazoIncorpRed="
				+ plazoIncorpRed + ", fechaAutCan=" + fechaAutCan + ", anagrama=" + anagrama + ", embarcacion="
				+ embarcacion + ", tlfMovil=" + tlfMovil + ", tlfFijo=" + tlfFijo + ", email=" + email
				+ ", notif_dom_empresa=" + notif_dom_empresa + ", dir_emp_calle=" + dir_emp_calle + ", dir_emp_num="
				+ dir_emp_num + ", dir_emp_bis=" + dir_emp_bis + ", dir_emp_bloq=" + dir_emp_bloq + ", dir_emp_es="
				+ dir_emp_es + ", dir_emp_piso=" + dir_emp_piso + ", dir_emp_p=" + dir_emp_p + ", dir_emp_CP="
				+ dir_emp_CP + ", dir_emp_num_muni=" + dir_emp_num_muni + ", dir_emp_nom_muni=" + dir_emp_nom_muni
				+ ", dir_emp_tlf=" + dir_emp_tlf + ", notif_dom_actividad=" + notif_dom_actividad + ", act_ugtgss="
				+ act_ugtgss + ", dir_act_calle=" + dir_act_calle + ", dir_act_num=" + dir_act_num + ", dir_act_bis="
				+ dir_act_bis + ", dir_act_bloq=" + dir_act_bloq + ", dir_act_es=" + dir_act_es + ", dir_act_piso="
				+ dir_act_piso + ", dir_act_p=" + dir_act_p + ", dir_act_CP=" + dir_act_CP + ", dir_act_num_muni="
				+ dir_act_num_muni + ", dir_act_nom_muni=" + dir_act_nom_muni + ", dir_act_tlf=" + dir_act_tlf + "]";
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
			if(ccc.equals(""))
				return null;
			this.ccc = ccc;
			return this;
		}
		public SituacionEmpresaBuilder setRegimen(String regimen) {
			if(regimen.equals(""))
				return null;
			this.regimen = regimen;
			return this;
		}
		public SituacionEmpresaBuilder setIdEmpresario(String idEmpresario) {
			if(idEmpresario.equals(""))
				return null;
			this.idEmpresario = idEmpresario;
			return this;
		}
		public SituacionEmpresaBuilder setNif_empresa(String nif_empresa) {
			if(nif_empresa.equals(""))
				return null;
			this.nif_empresa = nif_empresa;
			return this;
		}
		public SituacionEmpresaBuilder setNss(String nss) {
			if(nss.equals(""))
				return null;
			this.nss = nss;
			return this;
		}
		public SituacionEmpresaBuilder setCccp(String cccp) {
			if(cccp.equals(""))
				return null;
			this.cccp = cccp;
			return this;
		}
		public SituacionEmpresaBuilder setUgtgss(String ugtgss) {
			if(ugtgss.equals(""))
				return null;
			this.ugtgss = ugtgss;
			return this;
		}
		public SituacionEmpresaBuilder setUgtgsscccp(String ugtgsscccp) {
			if(ugtgsscccp.equals(""))
				return null;
			this.ugtgsscccp = ugtgsscccp;
			return this;
		}
		public SituacionEmpresaBuilder setUgcentral(String ugcentral) {
			if(ugcentral.equals(""))
				return null;
			this.ugcentral = ugcentral;
			return this;
		}
		public SituacionEmpresaBuilder setOgism(String ogism) {
			if(ogism.equals(""))
				return null;
			this.ogism = ogism;
			return this;
		}
		public SituacionEmpresaBuilder setCccAnt(String cccAnt) {
			if(cccAnt.equals(""))
				return null;
			this.cccAnt = cccAnt;
			return this;
		}
		public SituacionEmpresaBuilder setCccSuc(String cccSuc) {
			if(cccSuc.equals(""))
				return null;
			this.cccSuc = cccSuc;
			return this;
		}
		public SituacionEmpresaBuilder setSit(String sit) {
			if(sit.equals(""))
				return null;
			this.sit = sit;
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
			if(trl.equals(""))
				return null;
			this.trl = trl;
			return this;
		}
		public SituacionEmpresaBuilder setcEsp_num(String cEsp_num) {
			if(cEsp_num.equals(""))
				return null;
			this.cEsp_num = cEsp_num;
			return this;
		}
		public SituacionEmpresaBuilder setcEsp_cad(String cEsp_cad) {
			if(cEsp_cad.equals(""))
				return null;
			this.cEsp_cad = cEsp_cad;
			return this;
		}
		public SituacionEmpresaBuilder setCnae93_num(String cnae93_num) {
			if(cnae93_num.equals(""))
				return null;
			this.cnae93_num = cnae93_num;
			return this;
		}
		public SituacionEmpresaBuilder setCnae93_cad(String cnae93_cad) {
			if(cnae93_cad.equals(""))
				return null;
			this.cnae93_cad = cnae93_cad;
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
			if(cnae09_num.equals(""))
				return null;
			this.cnae09_num = cnae09_num;
			return this;
		}
		public SituacionEmpresaBuilder setCnae09_cad(String cnae09_cad) {
			if(cnae09_cad.equals(""))
				return null;
			this.cnae09_cad = cnae09_cad;
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
			if(coeJub_num.equals(""))
				return null;
			this.coeJub_num = coeJub_num;
			return this;
		}
		public SituacionEmpresaBuilder setCoeJub_cad(String coeJub_cad) {
			if(coeJub_cad.equals(""))
				return null;
			this.coeJub_cad = coeJub_cad;
			return this;
		}
		public SituacionEmpresaBuilder setAconExtra(String aconExtra) {
			if(aconExtra.equals(""))
				return null;
			this.aconExtra = aconExtra;
			return this;
		}
		public SituacionEmpresaBuilder setEscTaller(Boolean escTaller) {
			
			this.escTaller = escTaller;
			return this;
		}
		public SituacionEmpresaBuilder setAutorizacionRed(String autorizacionRed) {
			if(autorizacionRed.equals(""))
				return null;
			this.autorizacionRed = autorizacionRed;
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
			if(anagrama.equals(""))
				return null;
			this.anagrama = anagrama;
			return this;
		}
		public SituacionEmpresaBuilder setEmbarcacion(String embarcacion) {
			if(embarcacion.equals(""))
				return null;
			this.embarcacion = embarcacion;
			return this;
		}
		public SituacionEmpresaBuilder setTlfMovil(String tlfMovil) {
			if(tlfMovil.equals(""))
				return null;
			this.tlfMovil = tlfMovil;
			return this;
		}
		public SituacionEmpresaBuilder setTlfFijo(String tlfFijo) {
			if(tlfFijo.equals(""))
				return null;
			this.tlfFijo = tlfFijo;
			return this;
		}
		public SituacionEmpresaBuilder setEmail(String email) {
			if(email.equals(""))
				return null;
			this.email = email;
			return this;
		}
		public SituacionEmpresaBuilder setNotif_dom_empresa(Boolean notif_dom_empresa) {
			this.notif_dom_empresa = notif_dom_empresa;
			return this;
		}
		public SituacionEmpresaBuilder setDir_emp_calle(String dir_emp_calle) {
			if(dir_emp_calle.equals(""))
				return null;
			this.dir_emp_calle = dir_emp_calle;
			return this;
		}
		
		public SituacionEmpresaBuilder setTipo_via_dir_empresa(String tipo_via_dir_empresa) {
			if(tipo_via_dir_empresa.equals(""))
				return null;
			this.tipo_via_dir_empresa = tipo_via_dir_empresa;
			return this;
		}
		
		public SituacionEmpresaBuilder setTipo_via_dir_actividad(String tipo_via_dir_actividad) {
			if(tipo_via_dir_actividad.equals(""))
				return null;
			this.tipo_via_dir_actividad = tipo_via_dir_actividad;
			return this;
		}
		
		
		public SituacionEmpresaBuilder setDir_emp_num(String dir_emp_num) {
			if(dir_emp_num.equals(""))
				return null;
			this.dir_emp_num = dir_emp_num;
			return this;
		}
		public SituacionEmpresaBuilder setDir_emp_bis(String dir_emp_bis) {
			if(dir_emp_bis.equals(""))
				return null;
			this.dir_emp_bis = dir_emp_bis;
			return this;
		}
		public SituacionEmpresaBuilder setDir_emp_bloq(String dir_emp_bloq) {
			if(dir_emp_bloq.equals(""))
				return null;
			this.dir_emp_bloq = dir_emp_bloq;
			return this;
		}
		public SituacionEmpresaBuilder setDir_emp_es(String dir_emp_es) {
			if(dir_emp_es.equals(""))
				return null;
			this.dir_emp_es = dir_emp_es;
			return this;
		}
		public SituacionEmpresaBuilder setDir_emp_piso(String dir_emp_piso) {
			if(dir_emp_piso.equals(""))
				return null;
			this.dir_emp_piso = dir_emp_piso;
			return this;
		}
		public SituacionEmpresaBuilder setDir_emp_p(String dir_emp_p) {
			if(dir_emp_p.equals(""))
				return null;
			this.dir_emp_p = dir_emp_p;
			return this;
		}
		
		public SituacionEmpresaBuilder setDir_emp_CP(String dir_emp_CP) {
			if(dir_emp_CP.equals(""))
				return null;
			this.dir_emp_CP = dir_emp_CP;
			return this;
		}
		public SituacionEmpresaBuilder setDir_emp_num_muni(String dir_emp_num_muni) {
			if(dir_emp_num_muni.equals(""))
				return null;
			this.dir_emp_num_muni = dir_emp_num_muni;
			return this;
		}
		public SituacionEmpresaBuilder setDir_emp_nom_muni(String dir_emp_nom_muni) {
			if(dir_emp_nom_muni.equals(""))
				return null;
			this.dir_emp_nom_muni = dir_emp_nom_muni;
			return this;
		}
		public SituacionEmpresaBuilder setDir_emp_tlf(String dir_emp_tlf) {
			if(dir_emp_tlf.equals(""))
				return null;
			this.dir_emp_tlf = dir_emp_tlf;
			return this;
		}
		public SituacionEmpresaBuilder setNotif_dom_actividad(Boolean notif_dom_actividad) {
			this.notif_dom_actividad = notif_dom_actividad;
			return this;
		}
		public SituacionEmpresaBuilder setAct_ugtgss(String act_ugtgss) {
			if(act_ugtgss.equals(""))
				return null;
			this.act_ugtgss = act_ugtgss;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_calle(String dir_act_calle) {
			if(dir_act_calle.equals(""))
				return null;
			this.dir_act_calle = dir_act_calle;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_num(String dir_act_num) {
			if(dir_act_num.equals(""))
				return null;
			this.dir_act_num = dir_act_num;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_bis(String dir_act_bis) {
			if(dir_act_bis.equals(""))
				return null;
			this.dir_act_bis = dir_act_bis;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_bloq(String dir_act_bloq) {
			if(dir_act_bloq.equals(""))
				return null;
			this.dir_act_bloq = dir_act_bloq;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_es(String dir_act_es) {
			if(dir_act_es.equals(""))
				return null;
			this.dir_act_es = dir_act_es;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_piso(String dir_act_piso) {
			if(dir_act_piso.equals(""))
				return null;
			this.dir_act_piso = dir_act_piso;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_p(String dir_act_p) {
			if(dir_act_p.equals(""))
				return null;
			this.dir_act_p = dir_act_p;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_CP(String dir_act_CP) {
			if(dir_act_CP.equals(""))
				return null;
			this.dir_act_CP = dir_act_CP;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_num_muni(String dir_act_num_muni) {
			if(dir_act_num_muni.equals(""))
				return null;
			this.dir_act_num_muni = dir_act_num_muni;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_nom_muni(String dir_act_nom_muni) {
			if(dir_act_nom_muni.equals(""))
				return null;
			this.dir_act_nom_muni = dir_act_nom_muni;
			return this;
		}
		public SituacionEmpresaBuilder setDir_act_tlf(String dir_act_tlf) {
			if(dir_act_tlf.equals(""))
				return null;
			this.dir_act_tlf = dir_act_tlf;
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
