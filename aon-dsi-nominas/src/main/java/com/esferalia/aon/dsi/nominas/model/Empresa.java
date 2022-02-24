package com.esferalia.aon.dsi.nominas.model;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.watson.util.AonStringUtils;

public class Empresa {
	
	private String sscod;
	private String ssnum;
	private String ssctrl;	
	private String nif;
	private String tipo;
	private String sscodp;
	private String ssnump;
	private String ssctrp;	
	private String cotatep;
	private String rsocial;
	private String sg;
	private String domicil;
	private String numero;
	private String escaler;
	private String piso;
	private String puerta;
	private String cp;
	private String poblaci;
	private String provin;
	private String telef;
	private Date falta;
	private Date fbaja;
	private String conven;
	private String activ;
	private String nomact;
	private String licfis;
	private String entiat;
	private String codadm;
	private String nomlab;
	private String niflab;
	private String nomfis;
	private String niffis;
	private String calend;
	private String ctasegs;
	private String ctassac;
	private String ctahacp;
	private String ctacaja;
	private String ctasuel;
	private String ctaanti;
	private String ctapend;
	private String tipoAntig;  // 1-Acumulativa, 2-Fija
	private String tipoComple; // 1-Complemento, 2-Compensacion
	
	private LinkedList<Concepto> conceptos;
	private LinkedList<Paga> pagas;
	private LinkedList<Antiguedad> antiguedades;
	private LinkedList<Complemento> complementos;	
	private LinkedList<Banco> bancos;
	private LinkedList<Centro> centros;
	
	// Ids de varias tablas de AON que llevan datos de la empresa (las rellena el proceso del traspaso)
	private Integer enterpriseActivity; // Id de la actividad de la empresa
	private Integer enterpriseCCC;      // Id de la Cuenta de Cotizacion de la empresa
	private Integer workplace;          // Id del centro de trabajo que se crea para la empresa
	
	public String getSscod() {
		return sscod;
	}
	public Empresa setSscod(String ss) {
		this.sscod = ss;
		return this;
	}
	public String getSsnum() {
		return ssnum;
	}
	public Empresa setSsnum(String ssnum) {
		this.ssnum = ssnum;
		return this;
	}
	public String getSsctrl() {
		return ssctrl;
	}
	public Empresa setSsctrl(String ssctrl) {
		this.ssctrl = ssctrl;
		return this;
	}
	public String getNif() {
		return nif;
	}
	public Empresa setNif(String cif) {
		this.nif = cif;
		return this;
	}
	public String getTipo() {
		return tipo;
	}
	public Empresa setTipo(String tipo) {
		this.tipo = tipo;
		return this;
	}
	public String getSscodp() {
		return sscodp;
	}
	public Empresa setSscodp(String ssPrincipal) {
		this.sscodp = ssPrincipal;
		return this;
	}
	public String getSsnump() {
		return ssnump;
	}
	public Empresa setSsnump(String ssnump) {
		this.ssnump = ssnump;
		return this;
	}
	public String getSsctrp() {
		return ssctrp;
	}
	public Empresa setSsctrp(String ssctrp) {
		this.ssctrp = ssctrp;
		return this;
	}
	public String getCotatep() {
		return cotatep;
	}
	public Empresa setCotatep(String cotATEP) {
		this.cotatep = cotATEP;
		return this;
	}
	public String getRsocial() {
		return rsocial;
	}
	public Empresa setRsocial(String razonSocial) {
		this.rsocial = razonSocial;
		return this;
	}
	public String getSg() {
		return sg;
	}
	public Empresa setSg(String sg) {
		this.sg = sg;
		return this;
	}
	public String getDomicil() {
		return domicil;
	}
	public Empresa setDomicil(String domicil) {
		this.domicil = domicil;
		return this;
	}
	public String getNumero() {
		return numero;
	}
	public Empresa setNumero(String numero) {
		this.numero = numero;
		return this;
	}
	public String getEscaler() {
		return escaler;
	}
	public Empresa setEscaler(String escaler) {
		this.escaler = escaler;
		return this;
	}
	public String getPiso() {
		return piso;
	}
	public Empresa setPiso(String piso) {
		this.piso = piso;
		return this;
	}
	public String getPuerta() {
		return puerta;
	}
	public Empresa setPuerta(String puerta) {
		this.puerta = puerta;
		return this;
	}
	public String getCp() {
		return cp;
	}
	public Empresa setCp(String cp) {
		this.cp = cp;
		return this;
	}
	public String getPoblaci() {
		return poblaci;
	}
	public Empresa setPoblaci(String poblaci) {
		this.poblaci = poblaci;
		return this;
	}
	public String getProvin() {
		return provin;
	}
	public Empresa setProvin(String provin) {
		this.provin = provin;
		return this;
	}
	public String getTelef() {
		return telef;
	}
	public Empresa setTelef(String telef) {
		this.telef = telef;
		return this;
	}
	public Date getFalta() {
		return falta;
	}
	public Empresa setFalta(Date fechaAlta) {
		this.falta = fechaAlta;
		return this;
	}
	public Date getFbaja() {
		return fbaja;
	}
	public Empresa setFbaja(Date fechaBaja) {
		this.fbaja = fechaBaja;
		return this;
	}
	public String getConven() {
		return conven;
	}
	public Empresa setConven(String conven) {
		this.conven = conven;
		return this;
	}
	public String getActiv() {
		return activ;
	}
	public Empresa setActiv(String activ) {
		this.activ = activ;
		return this;
	}
	public String getNomact() {
		return nomact;
	}
	public Empresa setNomact(String nomact) {
		this.nomact = nomact;
		return this;
	}
	public String getLicfis() {
		return licfis;
	}
	public Empresa setLicfis(String licfis) {
		this.licfis = licfis;
		return this;
	}
	public String getEntiat() {
		return entiat;
	}
	public Empresa setEntiat(String entiat) {
		this.entiat = entiat;
		return this;
	}
	public String getCodadm() {
		return codadm;
	}
	public Empresa setCodadm(String codadm) {
		this.codadm = codadm;
		return this;
	}
	public String getNomlab() {
		return nomlab;
	}
	public Empresa setNomlab(String nomlab) {
		this.nomlab = nomlab;
		return this;
	}
	public String getNiflab() {
		return niflab;
	}
	public Empresa setNiflab(String niflab) {
		this.niflab = niflab;
		return this;
	}
	public String getNomfis() {
		return nomfis;
	}
	public Empresa setNomfis(String nomfis) {
		this.nomfis = nomfis;
		return this;
	}
	public String getNiffis() {
		return niffis;
	}
	public Empresa setNiffis(String niffis) {
		this.niffis = niffis;
		return this;
	}
	public String getCalend() {
		return calend;
	}
	public Empresa setCalend(String calend) {
		this.calend = calend;
		return this;
	}
	public String getCtasegs() {
		return ctasegs;
	}
	public Empresa setCtasegs(String ctasegs) {
		this.ctasegs = ctasegs;
		return this;
	}
	public String getCtassac() {
		return ctassac;
	}
	public Empresa setCtassac(String ctassac) {
		this.ctassac = ctassac;
		return this;
	}
	public String getCtahacp() {
		return ctahacp;
	}
	public Empresa setCtahacp(String ctahacp) {
		this.ctahacp = ctahacp;
		return this;
	}
	public String getCtacaja() {
		return ctacaja;
	}
	public Empresa setCtacaja(String ctacaja) {
		this.ctacaja = ctacaja;
		return this;
	}
	public String getCtasuel() {
		return ctasuel;
	}
	public Empresa setCtasuel(String ctasuel) {
		this.ctasuel = ctasuel;
		return this;
	}
	public String getCtaanti() {
		return ctaanti;
	}
	public Empresa setCtaanti(String ctaanti) {
		this.ctaanti = ctaanti;
		return this;
	}
	public String getCtapend() {
		return ctapend;
	}
	public Empresa setCtapend(String ctapend) {
		this.ctapend = ctapend;
		return this;
	}
	public String getTipoAntig() {
		return tipoAntig;
	}
	public Empresa setTipoAntig(String tipoAntig) {
		this.tipoAntig = tipoAntig;
		return this;
	}
	public String getTipoComple() {
		return tipoComple;
	}
	public Empresa setTipoComple(String tipoComple) {
		this.tipoComple = tipoComple;
		return this;
	}
	public LinkedList<Concepto> getConceptos() {
		if (conceptos == null) {
			conceptos = new LinkedList<Concepto>();
		}
		return conceptos;
	}
	public Empresa setConceptos(LinkedList<Concepto> conceptos) {
		this.conceptos = conceptos;
		return this;
	}
	public LinkedList<Paga> getPagas() {
		if (pagas == null) {
			pagas = new LinkedList<Paga>();
		}
		return pagas;
	}
	public Empresa setPagas(LinkedList<Paga> pagas) {
		this.pagas = pagas;
		return this;
	}
	public LinkedList<Antiguedad> getAntiguedades() {
		if (antiguedades == null) {
			antiguedades = new LinkedList<Antiguedad>();
		}
		return antiguedades;
	}
	public Empresa setAntiguedades(LinkedList<Antiguedad> antiguedades) {
		this.antiguedades = antiguedades;
		return this;
	}
	public LinkedList<Complemento> getComplementos() {
		if (complementos == null) {
			complementos = new LinkedList<Complemento>();
		}
		return complementos;
	}
	public Empresa setComplementos(LinkedList<Complemento> complementos) {
		this.complementos = complementos;
		return this;
	}
	public LinkedList<Banco> getBancos() {
		if (bancos == null) {
			bancos = new LinkedList<Banco>();
		}
		return bancos;
	}
	public Empresa setBancos(LinkedList<Banco> bancos) {
		this.bancos = bancos;
		return this;
	}
	public LinkedList<Centro> getCentros() {
		if (centros == null) {
			centros = new LinkedList<Centro>();
		}
		return centros;
	}
	public Empresa setCentros(LinkedList<Centro> centros) {
		this.centros = centros;
		return this;
	}
	
	public Integer getEnterpriseActivity() {
		return enterpriseActivity;
	}
	public Empresa setEnterpriseActivity(Integer enterpriseActivity) {
		this.enterpriseActivity = enterpriseActivity;
		return this;
	}
	public Integer getEnterpriseCCC() {
		return enterpriseCCC;
	}
	public Empresa setEnterpriseCCC(Integer enterpriseCCC) {
		this.enterpriseCCC = enterpriseCCC;
		return this;
	}
	public Integer getWorkplace() {
		return workplace;
	}
	public Empresa setWorkplace(Integer workplace) {
		this.workplace = workplace;
		return this;
	}
	
	// Devuelve el CCC completo, pues asi se graba en AON
	public String getAonCCC() {
		
		return AonStringUtils.trimToEmpty(this.sscod) +
	           AonStringUtils.trimToEmpty(this.ssnum) +
			   AonStringUtils.trimToEmpty(this.ssctrl);
		
	}
	
	@Override
	public String toString() {
		return "Empresa [sscod=" + sscod + ", ssnum=" + ssnum + ", ssctrl=" + ssctrl + ", nif=" + nif + ", rsocial=" + rsocial + "]";
//		return "Empresa [sscod=" + sscod + ", ssnum=" + ssnum + ", ssctrl=" + ssctrl + ", nif=" + nif + ", tipo=" + tipo
//		+ ", sscodp=" + sscodp + ", ssnump=" + ssnump + ", ssctrp=" + ssctrp + ", cotatep=" + cotatep
//		+ ", rsocial=" + rsocial + ", sg=" + sg + ", domicil=" + domicil + ", numero=" + numero + ", escaler="
//		+ escaler + ", piso=" + piso + ", puerta=" + puerta + ", cp=" + cp + ", poblaci=" + poblaci
//		+ ", provin=" + provin + ", telef=" + telef + ", falta=" + falta + ", fbaja=" + fbaja + ", conven="
//		+ conven + ", activ=" + activ + ", nomact=" + nomact + ", licfis=" + licfis + ", entiat=" + entiat
//		+ ", codadm=" + codadm + ", nomlab=" + nomlab + ", niflab=" + niflab + ", nomfis=" + nomfis
//		+ ", niffis=" + niffis + ", calend=" + calend + ", ctasegs=" + ctasegs + ", ctassac=" + ctassac
//		+ ", ctahacp=" + ctahacp + ", ctacaja=" + ctacaja + ", ctasuel=" + ctasuel + ", ctaanti=" + ctaanti
//		+ ", ctapend=" + ctapend + ", tipoAntig=" + tipoAntig + ", tipoComple=" + tipoComple + ", conceptos="
//		+ conceptos + ", pagas=" + pagas + ", antiguedades=" + antiguedades + ", complementos=" + complementos
//		+ ", bancos=" + bancos + ", centros=" + centros + "]";
	}

}
