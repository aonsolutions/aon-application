package com.esferalia.aon.dsi.nominas.model;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.dsi.nominas.Traspaso;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Trabajador {

	private String sscod;
	private String ssnum;
	private String ssctrl;
	private Date falta;
	private Date fbaja;
	private String apell1;
	private String apell2;
	private String nombre;
	private String dni;
	private String identif;
	private String paisemi;
	private String sg;
	private String direcci;
	private String numero;
	private String esc;
	private String piso;
	private String puerta;
	private String cp;
	private String poblaci;
	private String provin;
	private String telef;
	private String sexo;
	private String ecivil2;
	private Date fnac;
	private String naciona;
	private Date fantig;
	private String banco;
	private String iban;
	private String bic;
	private String talon;
	private String transf;
	private String conven;
	private String categ;
	private String nomcat;
	private double porcen;
	private String grupo;
	private String matric;
	private String puesto;
	private String centro;
	private String calend;
	private String clcto;
	private Date ffincto;
	private String retrib;
	private String cotatep;
	private double dias;
	private double horas;
	private String diastra;
	private String reamodcot;
	private String agrdes;
	private String autono;
	private String socio;
	private String mculto;
	private String pfrdl1493;
	private String cespsol;
	private String excldes;
	private String exclfgs;
	private String exclfp;
	private double horasl;
	private double horasm;
	private double horasx;
	private double horasj;
	private double horasv;
	private double horass;
	private double horasd;
	private String bonif65;
	private Date fini65;
	private String sngj;
	private String ecivil;
	private String nifcony;
	private String gradomi;
	private String ayudami;
	private String redmovi;
	private Date ftrasl;
	private String redacti;
	private String redcopa;
	private String presviv;
	private String ceuta;
	private String relaesp;
	private double redar17;
	private double redar183;
	private double pension;
	private double anualid;
	private String irpfijo;
	private double irpf;
	private String tipoAntig;  // 1-Acumulativa, 2-Fija
	
	private LinkedList<Descendiente> descendientes;
	private LinkedList<Ascendiente> ascendientes;
	
	private LinkedList<Concepto> conceptos;
	private LinkedList<Paga> pagas;
	private LinkedList<Antiguedad> antiguedades;	
		
	public String getSscod() {
		return sscod;
	}
	public Trabajador setSscod(String sscod) {
		this.sscod = sscod;
		return this;
	}
	public String getSsnum() {
		return ssnum;
	}
	public Trabajador setSsnum(String ssnum) {
		this.ssnum = ssnum;
		return this;
	}
	public String getSsctrl() {
		return ssctrl;
	}
	public Trabajador setSsctrl(String ssctrl) {
		this.ssctrl = ssctrl;
		return this;
	}
	public Date getFalta() {
		return falta;
	}
	public Trabajador setFalta(Date falta) {
		this.falta = falta;
		return this;
	}
	public Date getFbaja() {
		return fbaja;
	}
	public Trabajador setFbaja(Date fbaja) {
		this.fbaja = fbaja;
		return this;
	}
	public String getApell1() {
		return apell1;
	}
	public Trabajador setApell1(String apell1) {
		this.apell1 = apell1;
		return this;
	}
	public String getApell2() {
		return apell2;
	}
	public Trabajador setApell2(String apell2) {
		this.apell2 = apell2;
		return this;
	}
	public String getNombre() {
		return nombre;
	}
	public Trabajador setNombre(String nombre) {
		this.nombre = nombre;
		return this;
	}
	public String getDni() {
		return dni;
	}
	public Trabajador setDni(String dni) {
		this.dni = dni;
		return this;
	}
	public String getIdentif() {
		return identif;
	}
	public Trabajador setIdentif(String identif) {
		this.identif = identif;
		return this;
	}
	public String getPaisemi() {
		return paisemi;
	}
	public Trabajador setPaisemi(String paisemi) {
		this.paisemi = paisemi;
		return this;
	}
	public String getSg() {
		return sg;
	}
	public Trabajador setSg(String sg) {
		this.sg = sg;
		return this;
	}
	public String getDirecci() {
		return direcci;
	}
	public Trabajador setDirecci(String direcci) {
		this.direcci = direcci;
		return this;
	}
	public String getNumero() {
		return numero;
	}
	public Trabajador setNumero(String numero) {
		this.numero = numero;
		return this;
	}
	public String getEsc() {
		return esc;
	}
	public Trabajador setEsc(String esc) {
		this.esc = esc;
		return this;
	}
	public String getPiso() {
		return piso;
	}
	public Trabajador setPiso(String piso) {
		this.piso = piso;
		return this;
	}
	public String getPuerta() {
		return puerta;
	}
	public Trabajador setPuerta(String puerta) {
		this.puerta = puerta;
		return this;
	}
	public String getCp() {
		return cp;
	}
	public Trabajador setCp(String cp) {
		this.cp = cp;
		return this;
	}
	public String getPoblaci() {
		return poblaci;
	}
	public Trabajador setPoblaci(String poblaci) {
		this.poblaci = poblaci;
		return this;
	}
	public String getProvin() {
		return provin;
	}
	public Trabajador setProvin(String provin) {
		this.provin = provin;
		return this;
	}
	public String getTelef() {
		return telef;
	}
	public Trabajador setTelef(String telef) {
		this.telef = telef;
		return this;
	}
	public String getSexo() {
		return sexo;
	}
	public Trabajador setSexo(String sexo) {
		this.sexo = sexo;
		return this;
	}
	public String getEcivil2() {
		return ecivil2;
	}
	public Trabajador setEcivil2(String ecivil2) {
		this.ecivil2 = ecivil2;
		return this;
	}
	public Date getFnac() {
		return fnac;
	}
	public Trabajador setFnac(Date fnac) {
		this.fnac = fnac;
		return this;
	}
	public String getNaciona() {
		return naciona;
	}
	public Trabajador setNaciona(String naciona) {
		this.naciona = naciona;
		return this;
	}
	public Date getFantig() {
		return fantig;
	}
	public Trabajador setFantig(Date fantig) {
		this.fantig = fantig;
		return this;
	}
	public String getBanco() {
		return banco;
	}
	public Trabajador setBanco(String banco) {
		this.banco = banco;
		return this;
	}
	public String getIban() {
		return iban;
	}
	public Trabajador setIban(String iban) {
		this.iban = iban;
		return this;
	}
	public String getBic() {
		return bic;
	}
	public Trabajador setBic(String bic) {
		this.bic = bic;
		return this;
	}
	public String getTalon() {
		return talon;
	}
	public Trabajador setTalon(String talon) {
		this.talon = talon;
		return this;
	}
	public String getTransf() {
		return transf;
	}
	public Trabajador setTransf(String transf) {
		this.transf = transf;
		return this;
	}
	public String getConven() {
		return conven;
	}
	public Trabajador setConven(String conven) {
		this.conven = conven;
		return this;
	}
	public String getCateg() {
		return categ;
	}
	public Trabajador setCateg(String categ) {
		this.categ = categ;
		return this;
	}
	public String getNomcat() {
		return nomcat;
	}
	public Trabajador setNomcat(String nomcat) {
		this.nomcat = nomcat;
		return this;
	}
	public double getPorcen() {
		return porcen;
	}
	public Trabajador setPorcen(double porcen) {
		this.porcen = porcen;
		return this;
	}
	public String getGrupo() {
		return grupo;
	}
	public Trabajador setGrupo(String grupo) {
		this.grupo = grupo;
		return this;
	}
	public String getMatric() {
		return matric;
	}
	public Trabajador setMatric(String matric) {
		this.matric = matric;
		return this;
	}
	public String getPuesto() {
		return puesto;
	}
	public Trabajador setPuesto(String puesto) {
		this.puesto = puesto;
		return this;
	}
	public String getCentro() {
		return centro;
	}
	public Trabajador setCentro(String centro) {
		this.centro = centro;
		return this;
	}
	public String getCalend() {
		return calend;
	}
	public Trabajador setCalend(String calend) {
		this.calend = calend;
		return this;
	}
	public String getClcto() {
		return clcto;
	}
	public Trabajador setClcto(String clcto) {
		this.clcto = clcto;
		return this;
	}
	public Date getFfincto() {
		return ffincto;
	}
	public Trabajador setFfincto(Date ffincto) {
		this.ffincto = ffincto;
		return this;
	}
	public String getRetrib() {
		return retrib;
	}
	public Trabajador setRetrib(String retrib) {
		this.retrib = retrib;
		return this;
	}
	public String getCotatep() {
		return cotatep;
	}
	public Trabajador setCotatep(String cotatep) {
		this.cotatep = cotatep;
		return this;
	}
	public double getDias() {
		return dias;
	}
	public Trabajador setDias(double dias) {
		this.dias = dias;
		return this;
	}
	public double getHoras() {
		return horas;
	}
	public Trabajador setHoras(double horas) {
		this.horas = horas;
		return this;
	}
	public String getDiastra() {
		return diastra;
	}
	public Trabajador setDiastra(String diastra) {
		this.diastra = diastra;
		return this;
	}
	public String getReamodcot() {
		return reamodcot;
	}
	public Trabajador setReamodcot(String reamodcot) {
		this.reamodcot = reamodcot;
		return this;
	}
	public String getAgrdes() {
		return agrdes;
	}
	public Trabajador setAgrdes(String agrdes) {
		this.agrdes = agrdes;
		return this;
	}
	public String getAutono() {
		return autono;
	}
	public Trabajador setAutono(String autono) {
		this.autono = autono;
		return this;
	}
	public String getSocio() {
		return socio;
	}
	public Trabajador setSocio(String socio) {
		this.socio = socio;
		return this;
	}
	public String getMculto() {
		return mculto;
	}
	public Trabajador setMculto(String mculto) {
		this.mculto = mculto;
		return this;
	}
	public String getPfrdl1493() {
		return pfrdl1493;
	}
	public Trabajador setPfrdl1493(String pfrdl1493) {
		this.pfrdl1493 = pfrdl1493;
		return this;
	}
	public String getCespsol() {
		return cespsol;
	}
	public Trabajador setCespsol(String cespsol) {
		this.cespsol = cespsol;
		return this;
	}
	public String getExcldes() {
		return excldes;
	}
	public Trabajador setExcldes(String excldes) {
		this.excldes = excldes;
		return this;
	}
	public String getExclfgs() {
		return exclfgs;
	}
	public Trabajador setExclfgs(String exclfgs) {
		this.exclfgs = exclfgs;
		return this;
	}
	public String getExclfp() {
		return exclfp;
	}
	public Trabajador setExclfp(String exclfp) {
		this.exclfp = exclfp;
		return this;
	}
	public double getHorasl() {
		return horasl;
	}
	public Trabajador setHorasl(double horasl) {
		this.horasl = horasl;
		return this;
	}
	public double getHorasm() {
		return horasm;
	}
	public Trabajador setHorasm(double horasm) {
		this.horasm = horasm;
		return this;
	}
	public double getHorasx() {
		return horasx;
	}
	public Trabajador setHorasx(double horasx) {
		this.horasx = horasx;
		return this;
	}
	public double getHorasj() {
		return horasj;
	}
	public Trabajador setHorasj(double horasj) {
		this.horasj = horasj;
		return this;
	}
	public double getHorasv() {
		return horasv;
	}
	public Trabajador setHorasv(double horasv) {
		this.horasv = horasv;
		return this;
	}
	public double getHorass() {
		return horass;
	}
	public Trabajador setHorass(double horass) {
		this.horass = horass;
		return this;
	}
	public double getHorasd() {
		return horasd;
	}
	public Trabajador setHorasd(double horasd) {
		this.horasd = horasd;
		return this;
	}
	public String getBonif65() {
		return bonif65;
	}
	public Trabajador setBonif65(String bonif65) {
		this.bonif65 = bonif65;
		return this;
	}
	public Date getFini65() {
		return fini65;
	}
	public Trabajador setFini65(Date fini65) {
		this.fini65 = fini65;
		return this;
	}
	public String getSngj() {
		return sngj;
	}
	public Trabajador setSngj(String sngj) {
		this.sngj = sngj;
		return this;
	}
	public String getEcivil() {
		return ecivil;
	}
	public Trabajador setEcivil(String ecivil) {
		this.ecivil = ecivil;
		return this;
	}
	public String getNifcony() {
		return nifcony;
	}
	public Trabajador setNifcony(String nifcony) {
		this.nifcony = nifcony;
		return this;
	}
	public String getGradomi() {
		return gradomi;
	}
	public Trabajador setGradomi(String gradomi) {
		this.gradomi = gradomi;
		return this;
	}
	public String getAyudami() {
		return ayudami;
	}
	public Trabajador setAyudami(String ayudami) {
		this.ayudami = ayudami;
		return this;
	}
	public String getRedmovi() {
		return redmovi;
	}
	public Trabajador setRedmovi(String redmovi) {
		this.redmovi = redmovi;
		return this;
	}
	public Date getFtrasl() {
		return ftrasl;
	}
	public Trabajador setFtrasl(Date ftrasl) {
		this.ftrasl = ftrasl;
		return this;
	}
	public String getRedacti() {
		return redacti;
	}
	public Trabajador setRedacti(String redacti) {
		this.redacti = redacti;
		return this;
	}
	public String getRedcopa() {
		return redcopa;
	}
	public Trabajador setRedcopa(String redcopa) {
		this.redcopa = redcopa;
		return this;
	}
	public String getPresviv() {
		return presviv;
	}
	public Trabajador setPresviv(String presviv) {
		this.presviv = presviv;
		return this;
	}
	public String getCeuta() {
		return ceuta;
	}
	public Trabajador setCeuta(String ceuta) {
		this.ceuta = ceuta;
		return this;
	}
	public String getRelaesp() {
		return relaesp;
	}
	public Trabajador setRelaesp(String relaesp) {
		this.relaesp = relaesp;
		return this;
	}
	public double getRedar17() {
		return redar17;
	}
	public Trabajador setRedar17(double redar17) {
		this.redar17 = redar17;
		return this;
	}
	public double getRedar183() {
		return redar183;
	}
	public Trabajador setRedar183(double redar183) {
		this.redar183 = redar183;
		return this;
	}
	public double getPension() {
		return pension;
	}
	public Trabajador setPension(double pension) {
		this.pension = pension;
		return this;
	}
	public double getAnualid() {
		return anualid;
	}
	public Trabajador setAnualid(double anualid) {
		this.anualid = anualid;
		return this;
	}
	public String getIrpfijo() {
		return irpfijo;
	}
	public Trabajador setIrpfijo(String irpfijo) {
		this.irpfijo = irpfijo;
		return this;
	}
	public double getIrpf() {
		return irpf;
	}
	public Trabajador setIrpf(double irpf) {
		this.irpf = irpf;
		return this;
	}
	public String getTipoAntig() {
		return tipoAntig;
	}
	public Trabajador setTipoAntig(String tipoAntig) {
		this.tipoAntig = tipoAntig;
		return this;
	}
	public LinkedList<Descendiente> getDescendientes() {
		if (descendientes == null) {
			descendientes = new LinkedList<Descendiente>();
		}
		return descendientes;
	}
	public Trabajador setDescendientes(LinkedList<Descendiente> hijos) {
		this.descendientes = hijos;
		return this;
	}
	public LinkedList<Ascendiente> getAscendientes() {
		if (ascendientes == null) {
			ascendientes = new LinkedList<Ascendiente>();
		}
		return ascendientes;
	}
	public Trabajador setAscendientes(LinkedList<Ascendiente> ascen) {
		this.ascendientes = ascen;
		return this;
	}
	
	public LinkedList<Concepto> getConceptos() {
		if (conceptos == null) {
			conceptos = new LinkedList<Concepto>();
		}
		return conceptos;
	}
	public Trabajador setConceptos(LinkedList<Concepto> conceptos) {
		this.conceptos = conceptos;
		return this;
	}
	public LinkedList<Paga> getPagas() {
		if (pagas == null) {
			pagas = new LinkedList<Paga>();
		}
		return pagas;
	}
	public Trabajador setPagas(LinkedList<Paga> pagas) {
		this.pagas = pagas;
		return this;
	}
	public LinkedList<Antiguedad> getAntiguedades() {
		if (antiguedades == null) {
			antiguedades = new LinkedList<Antiguedad>();
		}
		return antiguedades;
	}
	public Trabajador setAntiguedades(LinkedList<Antiguedad> antiguedades) {
		this.antiguedades = antiguedades;
		return this;
	}
	
	// Devuelve nombre completo del trabajador (ape1 ape2, nombre)
	public String getNombreCompleto() {
		return AonStringUtils.trimToEmpty(AonStringUtils.trimToEmpty(this.apell1) + " " +
										  AonStringUtils.trimToEmpty(this.apell2)) + ", " +
			   AonStringUtils.trimToEmpty(this.nombre);	
	}
	
	// Coeficiente de parcialidad, según porcentaje de convenio u horas/dia
	public double getCoeficienteParcialidad() {

		double coeficiente = 1.0;
		if (this.porcen < 100 && this.porcen > 0) {
			coeficiente = this.porcen / 100;
		} else if (this.horasl != 0 || this.horasm != 0 || this.horasx != 0 || this.horasj != 0 || this.horasv != 0	|| this.horass != 0 || this.horasd != 0) {
			double horas = this.horasl + this.horasm + this.horasx + this.horasj + this.horasv + this.horass + this.horasd;
			// Asi es como lo hace AON
			horas = horas / 40;
			coeficiente = Math.round(horas * 100.0) / 100.0;
		} else if (this.horas != 0) {
			double horasDia = this.horas;
			String diasTra = AonStringUtils.trimToEmpty(this.diastra);
			// Si esta vacio dias de trabajo, se asume que trabaja de lunes a viernes y
			// que las horas estan puestas en base a 7 dias
			if (diasTra.isEmpty()) {
				diasTra = "LMXJV";
				horasDia = AonMathUtils.round(this.horas * 7 / 5);
			}
			double horas = 0;
			if (diasTra.contains("L"))
				horas = horas + horasDia;
			if (diasTra.contains("M"))
				horas = horas + horasDia;
			if (diasTra.contains("X"))
				horas = horas + horasDia;
			if (diasTra.contains("J"))
				horas = horas + horasDia;
			if (diasTra.contains("V"))
				horas = horas + horasDia;
			if (diasTra.contains("S"))
				horas = horas + horasDia;
			if (diasTra.contains("D"))
				horas = horas + horasDia;
			// Asi es como lo hace AON
			horas = horas / 40;
			coeficiente = Math.round(horas * 100.0) / 100.0;
		}
		
		return coeficiente;
		
	}
	
	// Formula para la tabla de antiguedad del trabajador
	// - Si la tabla tiene una sola linea, formula = ANTIGÜEDAD(IMPORTE_ANTIGUEDAD, periodo)
	// - En caso contrario formula if else años e importe, formula = (AÑOS_ANTIGUEDAD >= año_n ? importe_n : AÑOS_ANTIGUEDAD >= año_n-1 ? importe_n-1 : ... : AÑOS_ANTIGUEDAD >= año1 ? importe1 : 0.0)                
	public String getAonFormulaAntiguedad() {
		String formula = "";
		if (getAntiguedades().size() == 1) {
			if (AonStringUtils.isNotBlank(getAntiguedades().get(0).getAnos()) && AonStringUtils.isNotBlank(getAntiguedades().get(0).getTipo()))
				formula = "ANTIGÜEDAD(IMPORTE_ANTIGUEDAD, " + Traspaso.getPeriod(getAntiguedades().get(0).getAnos()) + ")"; 
		} else if (getAntiguedades().size() > 1) {
			// Se hace la formula con if else AÑOS_ANTIGUEDAD, leyendo la tabla en orden inverso
			// AÑOS_ANTIGUEDAD >= años_n ? importe_n : AÑOS_ANTIGUEDAD >= años_n-1 ? importe_n-1 :, ... : 0
			formula = "";
			for (int i = getAntiguedades().size()-1; i >= 0; i--) {
				Antiguedad antig = getAntiguedades().get(i);
				formula = formula + "AÑOS_ANTIGUEDAD >= " + Integer.parseInt(antig.getAnos()) + " ? ";
				if ("P".equals(antig.getTipo())) 
					formula = formula + (antig.getImporte() / 100);
				else
					// Tipo M o D, tener en cuenta el coeficiente de parcialidad del trabajador
					formula = formula + AonMathUtils.round(antig.getImporte()/getCoeficienteParcialidad(),2);
				formula = formula + " : ";				
			}			
			if (!formula.isEmpty())
				formula = "(" + formula + "0.0)";
			
		}
		return formula.trim();
	}
	
	// Expresion para el concepto de la tabla de antigüedad en AON
	public String getAonSeniorityExpression() {
		String expression = getAonFormulaAntiguedad();
		if (!expression.isEmpty()) {
			if (expression.contains("IMPORTE_ANTIGUEDAD")) {
				// Tabla de antiguedad de una sola línea				
				switch (getTipoCobroAntiguedad()) {
					case "M":
						expression = expression + " * DIAS_TRABAJADOS / DIAS_MES";	
						break;
					case "D":
						expression = expression + " * DIAS_TRABAJADOS";	
						break;
					case "P":
						expression = expression.replace("IMPORTE_ANTIGUEDAD", "IMPORTE_ANTIGUEDAD * SALARIO_BASE");	
						break;
					default:
						break;
				}
			}
			else {
				// Tabla de antiguedad de más de una línea				
				switch (getTipoCobroAntiguedad()) {
					case "M":
						expression = expression + " * DIAS_TRABAJADOS / DIAS_MES";	
						break;
					case "D":
						expression = expression + " * DIAS_TRABAJADOS";	
						break;
					case "P":
						expression = expression + " * SALARIO_BASE";	
						break;
					default:
						break;
				}							
			}
		}
		return expression;
	}
	
	// Tipo de Cobro de la tabla de antiguedad, se asume que todas las líneas de la tabla de antiguedad tienen el mismo tipo de cobro (P, D o M)
	private String getTipoCobroAntiguedad() {
		for (Antiguedad antig : getAntiguedades()) {
			if (AonStringUtils.isNotBlank(antig.getAnos()) && AonStringUtils.isNotBlank(antig.getTipo())) {
				return antig.getTipo();				
			}
		}
		return "";
	}
	
	@Override
	public String toString() {
		return "Trabajador [sscod=" + sscod + ", ssnum=" + ssnum + ", ssctrl=" + ssctrl + ", falta=" + falta
				+ ", fbaja=" + fbaja + ", apell1=" + apell1 + ", apell2=" + apell2 + ", nombre=" + nombre + ", dni="
				+ dni + "]";
//		return "Trabajador [dni=" + dni +", sscod=" + sscod + ", ssnum=" + ssnum + ", ssctrl=" + ssctrl + ", falta=" + falta
//				+ ", fbaja=" + fbaja + ", apell1=" + apell1 + ", apell2=" + apell2 + ", nombre=" + nombre + ", dni="
//				+ dni + ", identif=" + identif + ", paisemi=" + paisemi + ", sg=" + sg + ", direcci=" + direcci
//				+ ", numero=" + numero + ", esc=" + esc + ", piso=" + piso + ", puerta=" + puerta + ", cp=" + cp
//				+ ", poblaci=" + poblaci + ", provin=" + provin + ", telef=" + telef + ", sexo=" + sexo + ", ecivil2="
//				+ ecivil2 + ", fnac=" + fnac + ", naciona=" + naciona + ", fantig=" + fantig + ", banco=" + banco
//				+ ", iban=" + iban + ", bic=" + bic + ", talon=" + talon + ", transf=" + transf + ", categ=" + categ
//				+ ", nomcat=" + nomcat + ", porcen=" + porcen + ", grupo=" + grupo + ", matric=" + matric + ", puesto="
//				+ puesto + ", centro=" + centro + ", calend=" + calend + ", clcto=" + clcto + ", ffincto=" + ffincto
//				+ ", retrib=" + retrib + ", cotatep=" + cotatep + ", dias=" + dias + ", horas=" + horas + ", diastra="
//				+ diastra + ", reamodcot=" + reamodcot + ", agrdes=" + agrdes + ", autono=" + autono + ", socio="
//				+ socio + ", mculto=" + mculto + ", pfrdl1493=" + pfrdl1493 + ", cespsol=" + cespsol + ", excldes="
//				+ excldes + ", exclfgs=" + exclfgs + ", exclfp=" + exclfp + ", horasl=" + horasl + ", horasm=" + horasm
//				+ ", horasx=" + horasx + ", horasj=" + horasj + ", horasv=" + horasv + ", horass=" + horass
//				+ ", horasd=" + horasd + ", bonif65=" + bonif65 + ", fini65=" + fini65 + ", sngj=" + sngj + ", ecivil="
//				+ ecivil + ", nifcony=" + nifcony + ", gradomi=" + gradomi + ", ayudami=" + ayudami + ", redmovi="
//				+ redmovi + ", ftrasl=" + ftrasl + ", redacti=" + redacti + ", redcopa=" + redcopa + ", presviv="
//				+ presviv + ", ceuta=" + ceuta + ", relaesp=" + relaesp + ", redar17=" + redar17 + ", redar183="
//				+ redar183 + ", pension=" + pension + ", anualid=" + anualid + ", irpfijo=" + irpfijo + ", irpf=" + irpf
//				+ ", tipoAntig=" + tipoAntig + ", descendientes=" + descendientes + ", ascendientes=" + ascendientes
//				+ ", conceptos=" + conceptos + ", pagas=" + pagas + ", antiguedades=" + antiguedades + "]";
	}

}
