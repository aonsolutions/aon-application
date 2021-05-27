package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.modules.IEpigraph;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod131Activity implements Serializable {

	private static final long serialVersionUID = 1503338850244122274L;

	private IEpigraph epi;
	private String epigraph;
	private String description;
	
	private int year;
	private Period period;
	
	private double maxImport;
	
	private boolean dis; 		// A13. Si el contribuyente es discapacitado en grado igual o superior al 33 por 100, marque X
	private double com;			// A02. Comunidad, Sociedad civil o similar: porcentaje de participaci\u00F3n			
	private int tem;			// A03. Actividad de temporada: n\u00AA de d\u00EDas de ejercicio en el a\u00F1o anterior
	private int nue;			// A04. Nuevas actividades iniciadas a partir del 1-1-2014: A\u00F1o de inicio
	private boolean ceu;		// A05. Deducci\u00F3n por rentas obtenidas en Ceuta y Melilla
	private boolean loc;		// A06. \u00BFEjerce la actividad en un solo local o sin \u00E9l? (S/N).
	private int veh;			// A07. Indique el n\u00FAmero de veh\u00EDculos afectos a la actividad
	private boolean cap;		// A08. \u00BFLa capacidad de carga del veh\u00EDculo es superior a 1000 Kg.? (S/N)
	private boolean tns;		// C10. Indique si la actividad se realiza con tractocamiones y el titular carece de semirremolques.
	private boolean tss;		// C11. Indique si la actividad se realiza con un único tractocamión y sin semirremolques.
	private int mun;			// A09. Municipio donde se ejerce la actividad:
	private int emp;			// A10. N\u00AA de empleados a 1-01-2015 (o en la fecha de inicio de la actividad)
	private int lor;			// A11. Si en 2015 realiza la actividad en LORCA, seleccione lo que proceda
	private int bat;			// B06. Número de bateas y de barcos auxiliares de la empresa.
	private double prc;			// Si para el c\u00E1lculo del pago fraccionado desea aplicar un porcentaje superior al que establece la normativa, indique el porcentaje que desea aplicar
	
	private double rnp;			// I01. Rendimiento neto previo
	private double iem;			// I02. Incentivos al empleo
	private double iin;			// I03. Incentivos a la inversi\u00F3n
	private double rnm;			// I04. Rendimiento neto minorado
	private double ic1;			// I06. \u00CDNDICES CORRECTORES: 1. Especiales
	private double ic2;			// I07. \u00CDNDICES CORRECTORES: 2. Empresas de peque\u00F1a dimensi\u00F3n
	private double ic3;			// I08. \u00CDNDICES CORRECTORES: 3. De temporada
	private double ic4;			// I09. \u00CDNDICES CORRECTORES: 4. De exceso
	private double ic5;			// I10. \u00CDNDICES CORRECTORES: 5. De inicio de nueva actividad
	private double rpf;			// I11. Rendimiento a efectos de pagos fraccionados
	private double rlo;			// I12. Reducci\u00F3n para actividades econ\u00F3micas realizadas en el t\u00E9rmino municipal de Lorca
	private double rdr;			// I13. Rendimiento a efectos de pagos fraccionados despu\u00E9s de la reducci\u00F3n
	private int dia;			//      D\u00EDas de ejercicio en 2015
	private double net;			//      Rendimiento neto de la actividad a efectos del pago fraccionado 
	private double por;			// I14. Porcentaje aplicable
	private double res;			// I15. Resultado de aplicar el porcentaje correspondiente a cada actividad

//	private double salariedStaff;
//	private double noSalariedStaff;
	
	private boolean indiceEmpresasPequenaDimensionAplicable;
	
	private LinkedList<Mod131ActivityModule> modules;
	
	public IEpigraph getEpi() {
		return epi;
	}
	public Mod131Activity setEpi(IEpigraph epi) {
		this.epi = epi;
		return this;
	}

	public String getFullDescription() {
		return AonStringUtils.trimToEmpty(epigraph)
			+ (AonStringUtils.isBlank(description)?AonStringUtils.EMPTY:AonStringUtils.HYPHEN) 
			+ AonStringUtils.trimToEmpty(description);
	}
		
	public String getEpigraph() {
		return epigraph;
	}

	public Mod131Activity setEpigraph(String epigraph) {
		this.epigraph = epigraph;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Mod131Activity setDescription(String description) {
		this.description = description;
		return this;

	}
	
	public int getYear() {
		return year;
	}
	
	public Mod131Activity setYear(int year) {
		this.year = year;
		return this;
	}

	public Period getPeriod() {
		return period;
	}

	public Mod131Activity setPeriod(Period period) {
		this.period = period;
		return this;
	}

	public double getMaxImport() {
		return maxImport;
	}

	public Mod131Activity setMaxImport(double maxImport) {
		this.maxImport = maxImport;
		return this;
	}

	public boolean isDis() {
		return dis;
	}

	public Mod131Activity setDis(boolean dis) {
		this.dis = dis;
		return this;
	}

	public double getCom() {
		return com;
	}

	public Mod131Activity setCom(double com) {
		this.com = com;
		return this;

	}

	public int getTem() {
		return tem;
	}

	public Mod131Activity setTem(int tem) {
		this.tem = tem;
		return this;

	}

	public int getNue() {
		return nue;
	}

	public Mod131Activity setNue(int nue) {
		this.nue = nue;
		return this;
	}

	public boolean isCeu() {
		return ceu;
	}

	public Mod131Activity setCeu(boolean ceu) {
		this.ceu = ceu;
		return this;
	}

	public boolean isLoc() {
		return loc;
	}

	public Mod131Activity setLoc(boolean loc) {
		this.loc = loc;
		return this;

	}

	public int getVeh() {
		return veh;
	}

	public Mod131Activity setVeh(int veh) {
		this.veh = veh;
		return this;

	}

	public boolean isCap() {
		return cap;
	}

	public Mod131Activity setCap(boolean cap) {
		this.cap = cap;
		return this;
	}

	public boolean isTns() {
		return tns;
	}

	public Mod131Activity setTns(boolean tns) {
		this.tns = tns;
		return this;
	}

	public boolean isTss() {
		return tss;
	}

	public Mod131Activity setTss(boolean tss) {
		this.tss = tss;
		return this;
	}

	public int getMun() {
		return mun;
	}

	public Mod131Activity setMun(int mun) {
		this.mun = mun;
		return this;
	}

	public int getEmp() {
		return emp;
	}

	public Mod131Activity setEmp(int Emp) {
		this.emp = Emp;
		return this;
	}

	public int getLor() {
		return lor;
	}

	public Mod131Activity setLor(int lor) {
		this.lor = lor;
		return this;
	}

	public int getBat() {
		return bat;
	}

	public Mod131Activity setBat(int bat) {
		this.bat = bat;
		return this;
	}

	public double getPrc() {
		return prc;
	}

	public Mod131Activity setPrc(double prc) {
		this.prc = prc;
		return this;
	}

	public double getRnp() {
		return rnp;
	}

	public Mod131Activity setRnp(double rnp) {
		this.rnp = rnp;
		return this;
	}

	public double getIem() {
		return iem;
	}

	public Mod131Activity setIem(double iem) {
		this.iem = iem;
		return this;
	}

	public double getIin() {
		return iin;
	}

	public Mod131Activity setIin(double iin) {
		this.iin = iin;
		return this;
	}

	public double getRnm() {
		return rnm;
	}

	public Mod131Activity setRnm(double rnm) {
		this.rnm = rnm;
		return this;
	}

	public double getIc1() {
		return ic1;
	}

	public Mod131Activity setIc1(double ic1) {
		this.ic1 = ic1;
		return this;
	}

	public double getIc2() {
		return ic2;
	}

	public Mod131Activity setIc2(double ic2) {
		this.ic2 = ic2;
		return this;
	}

	public double getIc3() {
		return ic3;
	}

	public Mod131Activity setIc3(double ic3) {
		this.ic3 = ic3;
		return this;
	}

	public double getIc4() {
		return ic4;
	}

	public Mod131Activity setIc4(double ic4) {
		this.ic4 = ic4;
		return this;
	}

	public double getIc5() {
		return ic5;
	}

	public Mod131Activity setIc5(double ic5) {
		this.ic5 = ic5;
		return this;
	}

	public double getRpf() {
		return rpf;
	}

	public Mod131Activity setRpf(double rpf) {
		this.rpf = rpf;
		return this;
	}

	public double getRlo() {
		return rlo;
	}

	public Mod131Activity setRlo(double rlo) {
		this.rlo = rlo;
		return this;
	}

	public double getRdr() {
		return rdr;
	}

	public Mod131Activity setRdr(double rdr) {
		this.rdr = rdr;
		return this;
	}

	public int getDia() {
		return dia;
	}

	public Mod131Activity setDia(int dia) {
		this.dia = dia;
		return this;
	}

	public double getNet() {
		return net;
	}

	public Mod131Activity setNet(double net) {
		this.net = net;
		return this;
	}

	public double getPor() {
		return por;
	}

	public Mod131Activity setPor(double por) {
		this.por = por;
		return this;
	}

	public double getRes() {
		return res;
	}

	public Mod131Activity setRes(double res) {
		this.res = res;
		return this;
	}

	public LinkedList<Mod131ActivityModule> getModules() {
		return modules;
	}

	public Mod131Activity setModules(LinkedList<Mod131ActivityModule> modules) {
		this.modules = modules;
		return this;
	}
//
//	public double getSalariedStaff() {
//		return salariedStaff;
//	}
//
//	public Mod131Activity setSalariedStaff(double salariedStaff) {
//		this.salariedStaff = salariedStaff;
//		return this;
//	}
//
//	public double getNoSalariedStaff() {
//		return noSalariedStaff;
//	}
//
//	public Mod131Activity setNoSalariedStaff(double noSalariedStaff) {
//		this.noSalariedStaff = noSalariedStaff;
//		return this;
//	}
//
	public boolean isIndiceEmpresasPequenaDimensionAplicable() {
		return indiceEmpresasPequenaDimensionAplicable;
	}

	public void setIndiceEmpresasPequenaDimensionAplicable(boolean indiceEmpresasPequenaDimensionAplicable) {
		this.indiceEmpresasPequenaDimensionAplicable = indiceEmpresasPequenaDimensionAplicable;
	}
	public void initialize() {
		this.setEpi(null);
		this.setEpigraph(null);
		this.setDescription(null);
		this.setMaxImport(Double.MAX_VALUE);
		this.setDis(false);
		this.setCom(0);			
		this.setTem(0);
		this.setNue(0);
		this.setCeu(false);
		this.setLoc(false);
		this.setVeh(0);
		this.setCap(false);
		this.setTns(false);
		this.setTss(false);
		this.setMun(0);
		this.setEmp(0);
		this.setLor(0);
		this.setBat(0);
		this.setPrc(0);
		this.setIin(0);
		this.setRnp(0);
		this.setIem(0);
		this.setRnm(0);
		this.setIc1(0);
		this.setIc2(0);
		this.setIc3(0);
		this.setIc4(0);
		this.setIc5(0);
		this.setRpf(0);
		this.setRlo(0);
		this.setRdr(0);
		this.setNet(0); 
		this.setPor(0);
		this.setRes(0);
		this.setModules(new LinkedList<Mod131ActivityModule>());
	}

}
