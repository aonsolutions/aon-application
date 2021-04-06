package solutions.aon.seg.social.objects;

import java.util.Date;

public class SituacionEmpresa {
	// DATOS IDENTIFICATIVOS
	private String ccc;
	private String regimen;
	private String idEmpresario;
	private String nifEmpresa;
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
	private String cEspNum;
	private String cEspCad;
	private String cnae93Num;
	private String cnae93Cad;
	private Integer ta2Alta;
	private Integer ta2Baja;


	private String cnae09Num;
	private String cnae09Cad;
	private Float tiposATyEPIT;
	private Float ims;
	private Float total;
	private String coeJubNum;
	private String coeJubCad;
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
	private Boolean notifDomEmpresa;
	private String tipoViaDirEmpresa;
	
	private String tipoViaDirActividad;
	
	private String dirEmpCalle;
	private String dirEmpNum;
	private String dirEmpBis;
	private String dirEmpBloq;
	private String dirEmpEs;
	private String dirEmpPiso;
	private String dirEmpP;
	private String dirEmpCP;
	private String dirEmpNumMuni;
	private String  dirEmpNomMuni;
	private String dirEmpTlf;
	private Boolean notifDomActividad;
	private String actUgtgss;
	private String dirActCalle;
	private String dirActNum;

	private String dirActBis;
	private String dirActBloq;
	private String dirActEs;
	private String dirActPiso;
	private String dirActP;
	private String dirActCP;
	private String dirActNumMuni;
	private String  dirActNomMuni;
	private String dirActTlf;
	
	
	
	public static interface Visitor{
	public void  visitCcc(String ccc);
	public void visitRegimen(String regimen);
	public void visitIdEmpresario(String idemp);
	public void visitNifEmpresa(String nif);
	public void visitNss(String nss);
	public void visitCccp(String cccp);
	public void visitUgtgss(String ugtgss);
	public void visitUgtgsscccp(String ugtgsscccp);
	public void visitUgcentral(String ugcentral);
	public void visitOgism(String ogism);
	public void visitCccAnt(String cccAnt);
	public void visitCccSuc(String cccSuc);
	public void visitSit(String sit);
	public void visitFSit(Date fSit);
	public void visitFAltaInicial(Date fAltaInicial);
	public void visitTrabajadorAlta(Integer trabajadorAlta);
	public void visitAltaPrTrab(Date altaPrTrab);
	public void visitUltBajaEfCot(Date ultBajaEfCot);
	public void visitTrl(String trl);
	public void visitCEspNum(String cEspNum);
	public void visitCEspCad(String cEspCad);
	public void visitCnae93Num(String cnae93Num);
	public void visitCnae93Cad(String cnae93Cad);
	public void visitTa2Alta(Integer ta2Alta);
	public void visitTa2Baja(Integer ta2Baja);
	public void visitCnae09Num(String cnae09Num);
	public void visitCnae09Cad(String cnae09Cad);
	public void visitTiposATyEPIT(Float tiposATyEPIT);
	public void visitIms(Float ims);
	public void visitTotal(Float total);
	public void visitCoeJubNum(String coeJubNum);
	public void visitCoeJubCad(String coeJubCad);
	public void visitAconExtra(String aconExtra);
	public void visitEscTaller(Boolean escTaller);
	public void visitAutorizacionRed(String autorizacionRed);
	public void visitPlazoIncorpRed(Date plazoIncorpRed);
	public void visitFechaAutCan(Date fechaAutCan);
	
	//DATOS DE GESTIÓN (no repetidos)
	public void visitAnagrama(String anagrama);
	public void visitEmbarcacion(String embarcacion);
	public void visitTlfMovil(String tlfMovil);
	public void visitTlfFijo(String tlfFijo);
	public void visitEmail(String email);
	public void visitNotifDomEmpresa(Boolean notifDomEmpresa);
	public void visitTipoViaDirEmpresa(String tipoViaDirEmpresa);
	
	public void visitTipoViaDirActividad(String tipoViaDirActividad);
	
	public void visitDirEmpCalle(String dirEmpCalle);
	public void visitDirEmpNum(String dirEmpNum);
	public void visitDirEmpBis(String dirEmpBis);
	public void visitDirEmpBloq(String dirEmpBloq);
	public void visitDirEmpEs(String dirEmpEs);
	public void visitDirEmpPiso(String dirEmpPiso);
	public void visitDirEmpP(String dirEmpP);
	public void visitDirEmpCP(String dirEmpCP);
	public void visitDirEmpNumMuni(String dirEmpNumMuni);
	public void visitDirEmpNomMuni(String  dirEmpNomMuni);
	public void visitDirEmpTlf(String dirEmpTlf);
	public void visitNotifDomActividad(Boolean notifDomActividad);
	public void visitActUgtgss(String actUgtgss);
	public void visitDirActCalle(String dirActCalle);
	public void visitDirActNum(String dirActNum);

	public void visitDirActBis(String dirActBis);
	public void visitDirActBloq(String dirActBloq);
	public void visitDirActEs(String dirActEs);
	public void visitDirActPiso(String dirActPiso);
	public void visitDirActP(String dirActP);
	public void visitDirActCP(String dirActCP);
	public void visitDirActNumMuni(String dirActNumMuni);
	public void visitDirActNomMuni(String  dirActNomMuni);
	public void visitDirActTlf(String dirActTlf);
		
		
		//public String visit
	}
	
	public void accept(Visitor visitor) {
		if(ccc!=null)
			visitor.visitCcc(ccc);
		if(idEmpresario!=null)
			visitor.visitIdEmpresario(idEmpresario);
		if(nifEmpresa!=null)
			visitor.visitNifEmpresa(this.nifEmpresa);
		if(nss!=null)
			visitor.visitNss(this.nss);
		if(cccp!=null)
			visitor.visitCccp(this.cccp);
		if(ugtgss!=null)
			visitor.visitUgtgss(this.ugtgss);
		if(ugtgsscccp!=null)
			visitor.visitUgtgsscccp(this.ugtgsscccp);
		if(ugcentral!=null)
			visitor.visitUgcentral(this.ugcentral);
		if(ogism!=null)
			visitor.visitOgism(this.ogism);
		if(cccAnt!=null)
			visitor.visitCccAnt(this.cccAnt);
		if(cccSuc!=null)
			visitor.visitCccSuc(this.cccSuc);
		if(sit!=null)
			visitor.visitSit(this.sit);
		if(fSit!=null)
			visitor.visitFSit(this.fSit);
		if(fAltaInicial!=null)
			visitor.visitFAltaInicial(this.fAltaInicial);
		if(trabajadorAlta!=null)
			visitor.visitTrabajadorAlta(this.trabajadorAlta);
		if(altaPrTrab!=null)
			visitor.visitAltaPrTrab(this.altaPrTrab);
		if(ultBajaEfCot!=null)
			visitor.visitUltBajaEfCot(this.ultBajaEfCot);
		if(trl!=null)
			visitor.visitTrl(this.trl);
		if(cEspNum!=null)
			visitor.visitCEspNum(this.cEspNum);
		if(cEspCad!=null)
			visitor.visitCEspCad(this.cEspCad);
		if(cnae93Num!=null)
			visitor.visitCnae93Num(this.cnae93Num);
		if(cnae93Cad!=null)
			visitor.visitCnae93Cad(this.cnae93Cad);
		if(ta2Alta!=null)
			visitor.visitTa2Alta(this.ta2Alta);
		if(ta2Baja!=null)
			visitor.visitTa2Baja(this.ta2Baja);
		if(cnae09Num!=null)
			visitor.visitCnae09Num(this.cnae09Num);

		if(cnae09Cad!=null)
			visitor.visitCnae09Cad(this.cnae09Cad);

		if(tiposATyEPIT!=null)
			visitor.visitTiposATyEPIT(this.tiposATyEPIT);

		if(ims!=null)
			visitor.visitIms(this.ims);

		if(total!=null)
			visitor.visitTotal(this.total);

		if(coeJubNum!=null)
			visitor.visitCoeJubNum(this.coeJubNum);

		if(coeJubCad!=null)
			visitor.visitCoeJubCad(this.coeJubCad);

		if(aconExtra!=null)
			visitor.visitAconExtra(this.aconExtra);

		if(escTaller!=null)
			visitor.visitEscTaller(this.escTaller);

		if(autorizacionRed!=null)
			visitor.visitAutorizacionRed(this.autorizacionRed);

		if(plazoIncorpRed!=null)
			visitor.visitPlazoIncorpRed(this.plazoIncorpRed);

		if(fechaAutCan!=null)
			visitor.visitFechaAutCan(this.fechaAutCan);
		if(anagrama!=null)
			visitor.visitAnagrama(this.anagrama);
		if(embarcacion!=null)
			visitor.visitEmbarcacion(this.embarcacion);
		if(tlfMovil!=null)
			visitor.visitTlfMovil(this.tlfMovil);
		if(tlfFijo!=null)
			visitor.visitTlfFijo(this.tlfFijo);
		if(email!=null)
			visitor.visitEmail(this.email);
		if(notifDomEmpresa!=null)
			visitor.visitNotifDomEmpresa(this.notifDomEmpresa);
		if(tipoViaDirEmpresa!=null)
			visitor.visitTipoViaDirEmpresa(this.tipoViaDirEmpresa);
		if(tipoViaDirActividad!=null)
			visitor.visitTipoViaDirActividad(this.tipoViaDirActividad);
		if(dirEmpCalle!=null)
			visitor.visitDirEmpCalle(this.dirEmpCalle);
		if(dirEmpNum!=null)
			visitor.visitDirEmpNum(this.dirEmpNum);
		if(dirEmpBis!=null)
			visitor.visitDirEmpBis(this.dirEmpBis);
		if(dirEmpBloq!=null)
			visitor.visitDirEmpBloq(this.dirEmpBloq);
		if(dirEmpEs!=null)
			visitor.visitDirEmpEs(this.dirEmpEs);
		if(dirEmpPiso!=null)
			visitor.visitDirEmpPiso(this.dirEmpPiso);
		if(dirEmpP!=null)
			visitor.visitDirEmpP(this.dirEmpP);
		if(dirEmpCP!=null)
			visitor.visitDirEmpCP(this.dirEmpCP);
		if(dirEmpNumMuni!=null)
			visitor.visitDirEmpNumMuni(this.dirEmpNumMuni);
		if(dirEmpNomMuni!=null)
			visitor.visitDirEmpNomMuni(this.dirEmpNomMuni);
		if(dirEmpTlf!=null)
			visitor.visitDirEmpTlf(this.dirEmpTlf);
		if(notifDomActividad!=null)
			visitor.visitNotifDomActividad(this.notifDomActividad);
		if(actUgtgss!=null)
			visitor.visitActUgtgss(this.actUgtgss);
		if(dirActCalle!=null)
			visitor.visitDirActCalle(this.dirActCalle);
		if(dirActCalle!=null)
			visitor.visitDirActCalle(this.dirActCalle);
		if(dirActBis!=null)
			visitor.visitDirActBis(this.dirActBis);
		if(dirActBloq!=null)
			visitor.visitDirActBloq(this.dirActBloq);
		if(dirActEs!=null)
			visitor.visitDirActEs(this.dirActEs);
		if(dirActPiso!=null)
			visitor.visitDirActPiso(this.dirActPiso);
		if(dirActP!=null)
			visitor.visitDirActP(this.dirActP);
		if(dirActCP!=null)
			visitor.visitDirActCP(this.dirActCP);
		if(dirActNumMuni!=null)
			visitor.visitDirActNumMuni(this.dirActNumMuni);
		if(dirActNomMuni!=null)
			visitor.visitDirActNomMuni(this.dirActNomMuni);
		if(dirActTlf!=null)
			visitor.visitDirActTlf(this.dirActTlf);

	}
	
	@Override
	public String toString() {
		StringBuffer stringBuffer=new StringBuffer();
		stringBuffer.append("Situación de la empresa: \n");
		accept(new Visitor() {
			
			@Override
			public void visitRegimen(String regimen) {
				stringBuffer.append(String.format("\tRégimen: \"%S\"\n", regimen));
			}
			
			@Override
			public void visitNss(String nss) {
				stringBuffer.append(String.format("\tNSS: \"%S\"\n", nss));
			}
			
			@Override
			public void visitNifEmpresa(String nif) {
				stringBuffer.append(String.format("\tNIF: \"%S\"\n", nif));
			}
			
			@Override
			public void visitIdEmpresario(String idemp) {
				stringBuffer.append(String.format("\tID Empresario: \"%S\"\n", idemp));
			}
			
			@Override
			public void visitCcc(String ccc) {
				stringBuffer.append(String.format("\tCCC: \"%S\"\n", ccc));
			}

			@Override
			public void visitCccp(String cccp) {
				stringBuffer.append(String.format("\tCCCP: \"%S\"\n", cccp));
				
			}

			@Override
			public void visitUgtgss(String ugtgss) {
				stringBuffer.append(String.format("\tUGTGSS: \"%S\"\n", ugtgss));
				
			}

			@Override
			public void visitUgtgsscccp(String ugtgsscccp) {
				stringBuffer.append(String.format("\tUGTGSSCCCP: \"%S\"\n", ugtgsscccp));
				
			}

			@Override
			public void visitUgcentral(String ugcentral) {
				stringBuffer.append(String.format("\tUGCENTRAL: \"%S\"\n", ugcentral));
				
			}

			@Override
			public void visitOgism(String ogism) {
				stringBuffer.append(String.format("\tOGISM: \"%S\"\n", ogism));
				
			}

			@Override
			public void visitCccAnt(String cccAnt) {
				stringBuffer.append(String.format("\tCCCANT: \"%S\"\n", cccAnt));
				
			}

			@Override
			public void visitCccSuc(String cccSuc) {
				stringBuffer.append(String.format("\tCCCSUC: \"%S\"\n", cccSuc));
				
			}

			@Override
			public void visitSit(String sit) {
				stringBuffer.append(String.format("\tSIT: \"%S\"\n", sit));
				
			}

			@Override
			public void visitFSit(Date fSit) {
				stringBuffer.append(String.format("\tFSit: \"%S\"\n", fSit));
				
			}

			@Override
			public void visitFAltaInicial(Date fAltaInicial) {
				stringBuffer.append(String.format("\tFecha alta inicial: \"%S\"\n", fAltaInicial));
				
			}

			@Override
			public void visitTrabajadorAlta(Integer trabajadorAlta) {
				stringBuffer.append(String.format("\tTrabajadores de alta: \"%S\"\n", trabajadorAlta));
				
			}

			@Override
			public void visitAltaPrTrab(Date altaPrTrab) {
				stringBuffer.append(String.format("\tAlta primer trabaajdor: \"%S\"\n", altaPrTrab));
				
			}

			@Override
			public void visitUltBajaEfCot(Date ultBajaEfCot) {
				stringBuffer.append(String.format("\tUltima baja ef. cot.: \"%S\"\n", ultBajaEfCot));
				
			}

			@Override
			public void visitTrl(String trl) {
				stringBuffer.append(String.format("\tTRL: \"%S\"\n", trl));
				
			}
			
			@Override
			public void visitCEspNum(String cEspNum) {
				stringBuffer.append(String.format("\tC.Esp num.: \"%S\"\n", cEspNum));
				
			}

			@Override
			public void visitCEspCad(String cEspCad) {
				stringBuffer.append(String.format("\tC.Esp cad.: \"%S\"\n", cEspCad));
				
			}

			@Override
			public void visitCnae93Num(String cnae93Num) {
				stringBuffer.append(String.format("\tC.CNAE93 NUMvisitIms.: \"%S\"\n", cnae93Num));
				
			}

			@Override
			public void visitCnae93Cad(String cnae93Cad) {
				stringBuffer.append(String.format("\tC.CNAE93 CAD..: \"%S\"\n", cEspCad));
				
			}

			@Override
			public void visitTa2Alta(Integer ta2Alta) {
				stringBuffer.append(String.format("\tC.TA2ALTA: \"%S\"\n", ta2Alta));
				
			}

			@Override
			public void visitTa2Baja(Integer ta2Baja) {
				stringBuffer.append(String.format("\tTA2BAJA: \"%S\"\n", ta2Baja));
				
			}

			@Override
			public void visitCnae09Num(String cnae09Num) {
				stringBuffer.append(String.format("\tCNAE09 num.: \"%S\"\n", cnae09Num));
				
			}

			@Override
			public void visitCnae09Cad(String cnae09Cad) {
				stringBuffer.append(String.format("\tCNAE09 cad.: \"%S\"\n", cnae09Cad));
				
			}

			@Override
			public void visitTiposATyEPIT(Float tiposATyEPIT) {
				stringBuffer.append(String.format("\tTipos ATyEPIT.: \"%S\"\n", tiposATyEPIT));
				
			}

			@Override
			public void visitIms(Float ims) {
				stringBuffer.append(String.format("\tIMS: \"%S\"\n", ims));
				
			}

			@Override
			public void visitTotal(Float total) {
				stringBuffer.append(String.format("\tTotal: \"%S\"\n", total));
				
			}

			@Override
			public void visitCoeJubNum(String coeJubNum) {
				stringBuffer.append(String.format("\t Coeficiente jubilación:\"%S\"\n", coeJubNum));
				
			}

			@Override
			public void visitCoeJubCad(String coeJubCad) {
				stringBuffer.append(String.format("\tCad. coef. jubilación: \"%S\"\n", coeJubCad));
				
			}

			@Override
			public void visitAconExtra(String aconExtra) {
				stringBuffer.append(String.format("\tAcon. extra: \"%S\"\n", aconExtra));
				
			}

			@Override
			public void visitEscTaller(Boolean escTaller) {
				stringBuffer.append(String.format("\tEsc taller: \"%S\"\n", escTaller));
				
			}

			@Override
			public void visitAutorizacionRed(String autorizacionRed) {
				stringBuffer.append(String.format("\tAutorización red: \"%S\"\n", autorizacionRed));
				
			}

			@Override
			public void visitPlazoIncorpRed(Date plazoIncorpRed) {
				stringBuffer.append(String.format("\tPlazo incorp. red: \"%S\"\n", plazoIncorpRed));
				
			}

			@Override
			public void visitFechaAutCan(Date fechaAutCan) {
				stringBuffer.append(String.format("\tFecha aut. can: \"%S\"\n", fechaAutCan));
				
			}

			@Override
			public void visitAnagrama(String anagrama) {
				stringBuffer.append(String.format("\tAnagrama: \"%S\"\n", anagrama));
				
			}

			@Override
			public void visitEmbarcacion(String embarcacion) {
				stringBuffer.append(String.format("\tEmbarcación: \"%S\"\n", embarcacion));				
			}

			@Override
			public void visitTlfMovil(String tlfMovil) {
				stringBuffer.append(String.format("\tMóvil: \"%S\"\n", tlfMovil));				
			}

			@Override
			public void visitTlfFijo(String tlfFijo) {
				stringBuffer.append(String.format("\tTlf. fijo: \"%S\"\n", tlfFijo));				
			}

			@Override
			public void visitEmail(String email) {
				stringBuffer.append(String.format("\tEmail: \"%S\"\n", email));				
			}

			@Override
			public void visitNotifDomEmpresa(Boolean notifDomEmpresa) {
				stringBuffer.append(String.format("\tFNotif. domicilio empresa: \"%S\"\n", notifDomEmpresa));				
			}

			@Override
			public void visitTipoViaDirEmpresa(String tipoViaDirEmpresa) {
				stringBuffer.append(String.format("\tTipo vía domicilio empresa: \"%S\"\n", tipoViaDirEmpresa));				
			}

			@Override
			public void visitDirEmpCalle(String dirEmpCalle) {
				stringBuffer.append(String.format("\tDir. empresa calle: \"%S\"\n", dirEmpCalle));				
			}

			@Override
			public void visitDirEmpNum(String dirEmpNum) {
				stringBuffer.append(String.format("\tNum. dir. empresa: \"%S\"\n", dirEmpNum));				
			}

			@Override
			public void visitDirEmpBis(String dirEmpBis) {
				stringBuffer.append(String.format("\tDir. emp. bis: \"%S\"\n", dirEmpBis));				
			}

			@Override
			public void visitDirEmpBloq(String dirEmpBloq) {
				stringBuffer.append(String.format("\tDir. emp. bloque: \"%S\"\n", dirEmpBloq));				
			}

			@Override
			public void visitDirEmpEs(String dirEmpEs) {
				stringBuffer.append(String.format("\tDir. emp. escalera: \"%S\"\n", dirEmpEs));				
			}

			@Override
			public void visitDirEmpPiso(String dirEmpPiso) {
				stringBuffer.append(String.format("\tDir. emp. piso: \"%S\"\n", dirEmpPiso));				
			}

			@Override
			public void visitDirEmpP(String dirEmpP) {
				stringBuffer.append(String.format("\tDir. emp. puerta: \"%S\"\n", dirEmpP));				
			}

			@Override
			public void visitDirEmpCP(String dirEmpCP) {
				stringBuffer.append(String.format("\tCP empresa: \"%S\"\n", dirEmpCP));				
			}

			@Override
			public void visitDirEmpNumMuni(String dirEmpNumMuni) {
				stringBuffer.append(String.format("\tNum municipio empresa: \"%S\"\n", dirEmpNumMuni));				
			}

			@Override
			public void visitDirEmpNomMuni(String dirEmpNomMuni) {
				stringBuffer.append(String.format("\tMunicipio empresa: \"%S\"\n", dirEmpNomMuni));				
			}

			@Override
			public void visitDirEmpTlf(String dirEmpTlf) {
				stringBuffer.append(String.format("\tTlf. empresa: \"%S\"\n", dirEmpTlf));				
			}

			@Override
			public void visitNotifDomActividad(Boolean notifDomActividad) {
				stringBuffer.append(String.format("\tNotif. domicilio actividad: \"%S\"\n", notifDomActividad));				
			}

			@Override
			public void visitActUgtgss(String actUgtgss) {
				stringBuffer.append(String.format("\tAct UGTGSS: \"%S\"\n", actUgtgss));				
			}
			
			@Override
			public void visitTipoViaDirActividad(String tipoViaDirActividad) {
				stringBuffer.append(String.format("\tTipo vía dirección actividad: \"%S\"\n", tipoViaDirActividad));				
			}

			@Override
			public void visitDirActCalle(String dirActCalle) {
				stringBuffer.append(String.format("\tDir. actividad calle: \"%S\"\n", dirActCalle));				
			}

			@Override
			public void visitDirActNum(String dirActNum) {
				stringBuffer.append(String.format("\tDir. actividad número: \"%S\"\n", dirActNum));				
			}

			@Override
			public void visitDirActBis(String dirActBis) {
				stringBuffer.append(String.format("\tDir. actividad bis.: \"%S\"\n", dirActBis));
				
			}

			@Override
			public void visitDirActBloq(String dirActBloq) {
				stringBuffer.append(String.format("\tDir. act. bloque: \"%S\"\n", dirActBloq));
				
			}

			@Override
			public void visitDirActEs(String dirActEs) {
				stringBuffer.append(String.format("\tDir. act. escalera: \"%S\"\n", dirActEs));
				
			}

			@Override
			public void visitDirActPiso(String dirActPiso) {
				stringBuffer.append(String.format("\tDir. act. piso: \"%S\"\n", dirActPiso));
				
			}

			@Override
			public void visitDirActP(String dirActP) {
				stringBuffer.append(String.format("\tDir. act. puerta: \"%S\"\n", dirActP));
				
			}

			@Override
			public void visitDirActCP(String dirActCP) {
				stringBuffer.append(String.format("\tCP actividad: \"%S\"\n", dirActCP));
				
			}

			@Override
			public void visitDirActNumMuni(String dirActNumMuni) {
				stringBuffer.append(String.format("\tDir. act. num. municipio: \"%S\"\n", dirActNumMuni));
				
			}

			@Override
			public void visitDirActNomMuni(String dirActNomMuni) {
				stringBuffer.append(String.format("\tDir. act. nom. muni.: \"%S\"\n", dirActNomMuni));
				
			}

			@Override
			public void visitDirActTlf(String dirActTlf) {
				stringBuffer.append(String.format("\tDir. act. tlf: \"%S\"\n", dirActTlf));
				
			}
			
		});
		return stringBuffer.toString();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public String getTipoViaDirActividad() {
		return tipoViaDirActividad;
	}

	public void setTipoViaDirActividad(String tipoViaDirActividad) {
		this.tipoViaDirActividad = tipoViaDirActividad;
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

	public String getNifEmpresa() {
		return nifEmpresa;
	}

	public void setNifEmpresa(String nifEmpresa) {
		this.nifEmpresa = nifEmpresa;
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

	public String getcEspNum() {
		return cEspNum;
	}

	public void setcEspNum(String cEspNum) {
		this.cEspNum = cEspNum;
	}

	public String getcEspCad() {
		return cEspCad;
	}

	public void setcEspCad(String cEspCad) {
		this.cEspCad = cEspCad;
	}

	public String getCnae93Num() {
		return cnae93Num;
	}

	public void setCnae93Num(String cnae93Num) {
		this.cnae93Num = cnae93Num;
	}

	public String getCnae93Cad() {
		return cnae93Cad;
	}

	public void setCnae93Cad(String cnae93Cad) {
		this.cnae93Cad = cnae93Cad;
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

	public String getCnae09Num() {
		return cnae09Num;
	}

	public void setCnae09Num(String cnae09Num) {
		this.cnae09Num = cnae09Num;
	}

	public String getCnae09Cad() {
		return cnae09Cad;
	}

	public void setCnae09Cad(String cnae09Cad) {
		this.cnae09Cad = cnae09Cad;
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

	public String getCoeJubNum() {
		return coeJubNum;
	}

	public void setCoeJubNum(String coeJubNum) {
		this.coeJubNum = coeJubNum;
	}

	public String getCoeJubCad() {
		return coeJubCad;
	}

	public void setCoeJubCad(String coeJubCad) {
		this.coeJubCad = coeJubCad;
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

	public Boolean isNotifDomEmpresa() {
		return notifDomEmpresa;
	}

	public void setNotifDomEmpresa(Boolean notifDomEmpresa) {
		this.notifDomEmpresa = notifDomEmpresa;
	}

	public String getDirEmpCalle() {
		return dirEmpCalle;
	}

	public String getTipoViaDirEmpresa() {
		return tipoViaDirEmpresa;
	}

	public void setTipoViaDirEmpresa(String tipoViaDirEmpresa) {
		this.tipoViaDirEmpresa = tipoViaDirEmpresa;
	}

	public Boolean getNotifDomEmpresa() {
		return notifDomEmpresa;
	}

	public Boolean getNotifDomActividad() {
		return notifDomActividad;
	}

	public void setDirEmpCalle(String dirEmpCalle) {
		this.dirEmpCalle = dirEmpCalle;
	}

	public String getDirEmpNum() {
		return dirEmpNum;
	}

	public void setDirEmpNum(String dirEmpNum) {
		this.dirEmpNum = dirEmpNum;
	}

	public String getDirEmpBis() {
		return dirEmpBis;
	}

	public void setDirEmpBis(String dirEmpBis) {
		this.dirEmpBis = dirEmpBis;
	}

	public String getDirEmpBloq() {
		return dirEmpBloq;
	}

	public void setDirEmpBloq(String dirEmpBloq) {
		this.dirEmpBloq = dirEmpBloq;
	}

	public String getDirEmpEs() {
		return dirEmpEs;
	}

	public void setDirEmpEs(String dirEmpEs) {
		this.dirEmpEs = dirEmpEs;
	}

	public String getDirEmpPiso() {
		return dirEmpPiso;
	}

	public void setDirEmpPiso(String dirEmpPiso) {
		this.dirEmpPiso = dirEmpPiso;
	}

	public String getDirEmpP() {
		return dirEmpP;
	}

	public void setDirEmpP(String dirEmpP) {
		this.dirEmpP = dirEmpP;
	}

	public String getDirEmpCP() {
		return dirEmpCP;
	}

	public void setDirEmpCP(String dirEmpCP) {
		this.dirEmpCP = dirEmpCP;
	}

	public String getDirEmpNumMuni() {
		return dirEmpNumMuni;
	}

	public void setDirEmpNumMuni(String dirEmpNumMuni) {
		this.dirEmpNumMuni = dirEmpNumMuni;
	}

	public String getDirEmpNomMuni() {
		return dirEmpNomMuni;
	}

	public void setDirEmpNomMuni(String dirEmpNomMuni) {
		this.dirEmpNomMuni = dirEmpNomMuni;
	}

	public String getDirEmpTlf() {
		return dirEmpTlf;
	}

	public void setDirEmpTlf(String dirEmpTlf) {
		this.dirEmpTlf = dirEmpTlf;
	}

	public Boolean isNotifDomActividad() {
		return notifDomActividad;
	}

	public void setNotifDomActividad(Boolean notifDomActividad) {
		this.notifDomActividad = notifDomActividad;
	}

	public String getActUgtgss() {
		return actUgtgss;
	}

	public void setActUgtgss(String actUgtgss) {
		this.actUgtgss = actUgtgss;
	}

	public String getDirActCalle() {
		return dirActCalle;
	}

	public void setDirActCalle(String dirActCalle) {
		this.dirActCalle = dirActCalle;
	}

	public String getDirActNum() {
		return dirActNum;
	}

	public void setDirActNum(String dirActNum) {
		this.dirActNum = dirActNum;
	}

	public String getDirActBis() {
		return dirActBis;
	}

	public void setDirActBis(String dirActBis) {
		this.dirActBis = dirActBis;
	}

	public String getDirActBloq() {
		return dirActBloq;
	}

	public void setDirActBloq(String dirActBloq) {
		this.dirActBloq = dirActBloq;
	}

	public String getDirActEs() {
		return dirActEs;
	}

	public void setDirActEs(String dirActEs) {
		this.dirActEs = dirActEs;
	}

	public String getDirActPiso() {
		return dirActPiso;
	}

	public void setDirActPiso(String dirActPiso) {
		this.dirActPiso = dirActPiso;
	}

	public String getDirActP() {
		return dirActP;
	}

	public void setDirActP(String dirActP) {
		this.dirActP = dirActP;
	}

	public String getDirActCP() {
		return dirActCP;
	}

	public void setDirActCP(String dirActCP) {
		this.dirActCP = dirActCP;
	}

	public String getDirActNumMuni() {
		return dirActNumMuni;
	}

	public void setDirActNumMuni(String dirActNumMuni) {
		this.dirActNumMuni = dirActNumMuni;
	}

	public String getDirActNomMuni() {
		return dirActNomMuni;
	}

	public void setDirActNomMuni(String dirActNomMuni) {
		this.dirActNomMuni = dirActNomMuni;
	}

	public String getDirActTlf() {
		return dirActTlf;
	}

	public void setDirActTlf(String dirActTlf) {
		this.dirActTlf = dirActTlf;
	}

	
	
	
	private SituacionEmpresa () {
		
	}
	
	public static class SituacionEmpresaBuilder{
		private String ccc;
		private String regimen;
		private String idEmpresario;
		private String nifEmpresa;
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
		private String cEspNum;
		private String cEspCad;
		private String cnae93Num;
		private String cnae93Cad;
		private Integer ta2Alta;
		private Integer ta2Baja;


		private String cnae09Num;
		private String cnae09Cad;
		private Float tiposATyEPIT;
		private Float ims;
		private Float total;
		private String coeJubNum;
		private String coeJubCad;
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
		private Boolean notifDomEmpresa;
		private String tipoViaDirEmpresa;
		private String tipoViaDirActividad;

		
		private String dirEmpCalle;
		private String dirEmpNum;
		private String dirEmpBis;
		private String dirEmpbloq;
		private String dirEmpEs;
		private String dirEmpPiso;
		private String dirEmpP;
		private String dirEmpCP;
		private String dirEmpNumMuni;
		private String  dirEmpNomMuni;
		private String dirEmpTlf;
		private Boolean notifDomActividad;
		private String actUgtgss;
		private String dirActCalle;
		private String dirActNum;

		private String dirActBis;
		private String dirActBloq;
		private String dirActEs;
		private String dirActPiso;
		private String dirActP;
		private String dirActCP;
		private String dirActNumMuni;
		private String  dirActNomMuni;
		private String dirActTlf;
		
		
		
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
		public SituacionEmpresaBuilder setNifempresa(String nifempresa) {
			if(!((nifempresa.equals(""))||(nifempresa==null)))
				this.nifEmpresa = nifempresa;
			else
				this.nifEmpresa=null;
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
		public SituacionEmpresaBuilder setcEspNum(String cEspNum) {
			if(!((cEspNum.equals(""))||(cEspNum==null)))
				this.cEspNum = cEspNum;
			else
				this.cEspNum=null;
			return this;
		}
		public SituacionEmpresaBuilder setcEspCad(String cEspCad) {
			if(!((cEspCad.equals(""))||(cEspCad==null)))
				this.cEspCad = cEspCad;
			else
				this.cEspCad=null;
			return this;
		}
		public SituacionEmpresaBuilder setCnae93Num(String cnae93Num) {
			if(!((cnae93Num.equals(""))||(cnae93Num==null)))
				this.cnae93Num = cnae93Num;
			else
				this.cnae93Num=null;
			return this;
		}
		public SituacionEmpresaBuilder setCnae93Cad(String cnae93Cad) {
			if(!((cnae93Cad.equals(""))||(cnae93Cad==null)))
				this.cnae93Cad = cnae93Cad;
			else
				this.cnae93Cad=null;
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
		public SituacionEmpresaBuilder setCnae09Num(String cnae09Num) {
			if(!((cnae09Num.equals(""))||(cnae09Num==null)))
				this.cnae09Num = cnae09Num;
			else
				this.cnae09Num=null;
			return this;
		}
		public SituacionEmpresaBuilder setCnae09Cad(String cnae09Cad) {
			if(!((cnae09Cad.equals(""))||(cnae09Cad==null)))
				this.cnae09Cad = cnae09Cad;
			else
				this.cnae09Cad=null;
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
		public SituacionEmpresaBuilder setCoeJubNum(String coeJubNum) {
			if(!((coeJubNum.equals(""))||(coeJubNum==null)))
				this.coeJubNum = coeJubNum;
			else
				this.coeJubNum=null;
			return this;
		}
		public SituacionEmpresaBuilder setCoeJubCad(String coeJubCad) {
			if(!((coeJubCad.equals(""))||(coeJubCad==null)))
				this.coeJubCad = coeJubCad;
			else
				this.coeJubCad=null;
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
		public SituacionEmpresaBuilder setNotifDomEmpresa(Boolean notifDomEmpresa) {
			this.notifDomEmpresa = notifDomEmpresa;
			return this;
		}
		public SituacionEmpresaBuilder setDirEmpCalle(String dirEmpCalle) {
			if(!((dirEmpCalle.equals(""))||(dirEmpCalle==null)))
				this.dirEmpCalle = dirEmpCalle;
			else
				this.dirEmpCalle=null;
			return this;
		}
		
		public SituacionEmpresaBuilder setTipoViaDirEmpresa(String tipoViaDirEmpresa) {
			if(!((tipoViaDirEmpresa.equals(""))||(tipoViaDirEmpresa==null)))
				this.tipoViaDirEmpresa = tipoViaDirEmpresa;
			else
				this.tipoViaDirEmpresa=null;
			return this;
		}
		
		public SituacionEmpresaBuilder setTipoViaDirActividad(String tipoViaDirActividad) {
			if(!((tipoViaDirActividad.equals(""))||(tipoViaDirActividad==null)))
				this.tipoViaDirActividad = tipoViaDirActividad;
			else
				this.tipoViaDirActividad=null;
			return this;
		}
		
		
		public SituacionEmpresaBuilder setDirEmpNum(String dirEmpNum) {
			if(!((dirEmpNum.equals(""))||(dirEmpNum==null)))
				this.dirEmpNum = dirEmpNum;
			else
				this.dirEmpNum=null;
			return this;
		}
		public SituacionEmpresaBuilder setDirEmpBis(String dirEmpBis) {
			if(!((dirEmpBis.equals(""))||(dirEmpBis==null)))
				this.dirEmpBis = dirEmpBis;
			else
				this.dirEmpBis=null;
			return this;
		}
		public SituacionEmpresaBuilder setDirEmpBloq(String dirEmpBloq) {
			if(!((dirEmpBloq.equals(""))||(dirEmpBloq==null)))
				this.dirEmpbloq = dirEmpBloq;
			else
				this.dirEmpbloq=null;
			return this;
		}
		public SituacionEmpresaBuilder setDirEmpEs(String dirEmpEs) {
			if(!((dirEmpEs.equals(""))||(dirEmpEs==null)))
				this.dirEmpEs = dirEmpEs;
			else
				this.dirEmpEs=null;
			return this;
		}
		public SituacionEmpresaBuilder setDirEmpPiso(String dirEmpPiso) {
			if(!((dirEmpPiso.equals(""))||(dirEmpPiso==null)))
				this.dirEmpPiso = dirEmpPiso;
			else
				this.dirEmpPiso=null;
			return this;
		}
		public SituacionEmpresaBuilder setDirEmpP(String dirEmpP) {
			if(!((dirEmpP.equals(""))||(dirEmpP==null)))
				this.dirEmpP = dirEmpP;
			else
				this.dirEmpP=null;
			return this;
		}
		
		public SituacionEmpresaBuilder setDirEmpCP(String dirEmpCP) {
			if(!((dirEmpCP.equals(""))||(dirEmpCP==null)))
				this.dirEmpCP = dirEmpCP;
			else
				this.dirEmpCP=null;
			return this;
		}
		public SituacionEmpresaBuilder setDirEmpNumMuni(String dirEmpNumMuni) {
			if(!((dirEmpNumMuni.equals(""))||(dirEmpNumMuni==null)))
				this.dirEmpNumMuni = dirEmpNumMuni;
			else
				this.dirEmpNumMuni=null;
			return this;
		}
		public SituacionEmpresaBuilder setDirEmpNomMuni(String dirEmpNomMuni) {
			if(!((dirEmpNomMuni.equals(""))||(dirEmpNomMuni==null)))
				this.dirEmpNomMuni = dirEmpNomMuni;
			else
				this.dirEmpNomMuni=null;
			return this;
		}
		public SituacionEmpresaBuilder setDirEmpTlf(String dirEmpTlf) {
			if(!((dirEmpTlf.equals(""))||(dirEmpTlf==null)))
				this.dirEmpTlf = dirEmpTlf;
			else
				this.dirEmpTlf=null;
			return this;
		}
		public SituacionEmpresaBuilder setNotifDomActividad(Boolean notifDomActividad) {
			this.notifDomActividad = notifDomActividad;
			return this;
		}
		public SituacionEmpresaBuilder setActUgtgss(String actUgtgss) {
			if(!((actUgtgss.equals(""))||(actUgtgss==null)))
				this.actUgtgss = actUgtgss;
			else
				this.actUgtgss=null;
			return this;
		}
		public SituacionEmpresaBuilder setDirActCalle(String dirActCalle) {
			if(!((dirActCalle.equals(""))||(dirActCalle==null)))
				this.dirActCalle = dirActCalle;
			else
				this.dirActCalle=null;
			return this;
		}
		public SituacionEmpresaBuilder setDirActNum(String dirActNum) {
			if(!((dirActNum.equals(""))||(dirActNum==null)))
				this.dirActNum = dirActNum;
			else
				this.dirActNum=null;
			return this;
		}
		public SituacionEmpresaBuilder setDirActBis(String dirActBis) {
			if(!((dirActBis.equals(""))||(dirActBis==null)))
				this.dirActBis = dirActBis;
			else
				this.dirActBis=null;
			return this;
		}
		public SituacionEmpresaBuilder setDirActBloq(String dirActBloq) {
			if(!((dirActBloq.equals(""))||(dirActBloq==null)))
				this.dirActBloq = dirActBloq;
			else
				this.dirActBloq=null;
			return this;
		}
		public SituacionEmpresaBuilder setDirActEs(String dirActEs) {
			if(!((dirActEs.equals(""))||(dirActEs==null)))
				this.dirActEs = dirActEs;
			else
				this.dirActEs=null;
			return this;
		}
		public SituacionEmpresaBuilder setDirActPiso(String dirActPiso) {
			if(!((dirActPiso.equals(""))||(dirActPiso==null)))
				this.dirActPiso = dirActPiso;
			else
				this.dirActPiso=null;
			return this;
		}
		public SituacionEmpresaBuilder setDirActP(String dirActP) {
			if(!((dirActP.equals(""))||(dirActP==null)))
				this.dirActP = dirActP;
			else
				this.dirActP=null;
			return this;
		}
		public SituacionEmpresaBuilder setDirActCP(String dirActCP) {
			if(!((dirActCP.equals(""))||(dirActCP==null)))
				this.dirActCP = dirActCP;
			else
				this.dirActCP=null;
			return this;
		}
		public SituacionEmpresaBuilder setDirActNumMuni(String dirActNumMuni) {
			if(!((dirActNumMuni.equals(""))||(dirActNumMuni==null)))
				this.dirActNumMuni = dirActNumMuni;
			else
				this.dirActNumMuni=null;
			return this;
		}
		public SituacionEmpresaBuilder setDirActNomMuni(String dirActNomMuni) {
			if(!((dirActNomMuni.equals(""))||(dirActNomMuni==null)))
				this.dirActNomMuni = dirActNomMuni;
			else
				this.dirActNomMuni=null;
			return this;
		}
		public SituacionEmpresaBuilder setDirActTlf(String dirActTlf) {
			if(!((dirActTlf.equals(""))||(dirActTlf==null)))
				this.dirActTlf = dirActTlf;
			else
				this.dirActTlf=null;
			return this;
		}
		public SituacionEmpresa build() {
			SituacionEmpresa r=new SituacionEmpresa();
			r.setAconExtra(this.aconExtra);
			r.setActUgtgss(this.actUgtgss);
			r.setAltaPrTrab(this.altaPrTrab);
			r.setAnagrama(this.anagrama);
			r.setAutorizacionRed(this.autorizacionRed);
			r.setCcc(this.ccc);
			r.setCccAnt(this.cccAnt);
			r.setCccp(this.cccp);
			r.setCccSuc(this.cccSuc);
			r.setcEspCad(this.cEspCad);
			r.setcEspNum(this.cEspNum);
			r.setCnae09Cad(this.cnae09Cad);
			r.setCnae09Num(this.cnae09Num);
			r.setCnae93Cad(this.cnae93Cad);
			r.setCnae93Num(this.cnae93Num);
			r.setCoeJubCad(this.coeJubCad);
			r.setCoeJubNum(this.coeJubNum);
			r.setDirActBis(this.dirActBis);
			r.setDirActBloq(this.dirActBloq);
			r.setDirActCalle(this.dirActCalle);
			r.setDirActCP(this.dirActCP);
			r.setDirActEs(this.dirActEs);
			r.setDirActNomMuni(this.dirActNomMuni);
			r.setDirActP(this.dirActP);
			r.setDirActPiso(this.dirActPiso);
			r.setDirActTlf(this.dirActTlf);
			r.setDirEmpBis(this.dirEmpBis);
			r.setDirEmpBloq(this.dirEmpbloq);
			r.setDirEmpCalle(this.dirEmpCalle);
			r.setDirEmpCP(this.dirEmpCP);
			r.setDirEmpEs(this.dirEmpEs);
			r.setDirEmpNomMuni(this.dirEmpNomMuni);
			r.setDirEmpNum(this.dirEmpNum);
			r.setDirEmpNumMuni(this.dirEmpNumMuni);
			r.setDirEmpP(this.dirEmpP);
			r.setDirEmpPiso(this.dirEmpPiso);
			r.setDirEmpTlf(this.dirEmpTlf);
			r.setEmail(this.email);
			r.setEmbarcacion(this.embarcacion);
			r.setEscTaller(this.escTaller);
			r.setfAltaInicial(this.fAltaInicial);
			r.setFechaAutCan(this.fechaAutCan);
			r.setfSit(this.fSit);
			r.setIdEmpresario(this.idEmpresario);
			r.setIms(this.ims);
			r.setNifEmpresa(this.nifEmpresa);
			r.setNotifDomActividad(this.notifDomActividad);
			r.setNotifDomEmpresa(this.notifDomEmpresa);
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
			r.setTipoViaDirEmpresa(this.tipoViaDirEmpresa);
			r.setTipoViaDirActividad(this.tipoViaDirActividad);
			r.setDirActNum(this.dirActNum);
			r.setDirActNumMuni(this.dirActNumMuni);
			return r;
		}
		
		
	}
}