package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod421Activity implements Serializable {
	
	private static final long serialVersionUID = -7985206570258633419L;

	private int index;
	
	private String epigraph;
	private String description;
	private int specialEpigraph;
	
	private int tem;			// Actividad de temporada: Número de días de ejercicio en año anterior
	private int dia;			// Número de días de ejercicio de la actividad en el trimestre o numero de días de ejercicio en el año para las actividades de temporada en el 4T
//	private int emp;			// Número de empleados a 1-01 (o en la fecha de inicio de la actividad)
//	private int lor;			// Si realiza la actividad en LORCA, seleccione lo que proceda
//	private int cov;			// Si aplica la reduccion extraordinaria por covid-19, art. 9 RD-Ley 35/2020)
//	private int dana;			// Si en 2024 realiza la actividad en municipios afectados por la DANA (ver anexo del RD-ley 6/2024), seleccione lo que proceda

	private double dev;			   // Cuota devengada operaciones corrientes
//	private double lorcaReduction; // Importe reducción Lorca (se añade en el último periodo de 2024, junto con la reducción de la DANA)
//	private double danaReduction;  // Importe reducción DANA 2024
//	private double red;			   // Reducciones (total)
	private double ict;			// Indice corrector de actividades de temporada
	
	// 1T, 2T, 3T
//	private double ind;			// Índice corrector de actividades de temporada
	private double por;			// Porcentaje de ingreso a cuenta
	private double ing;			// Ingreso a cuenta
	
	// 4T	
	private double sop1;		// 1% cuota devengada
	private double sopR;		// Resto de cuotas soportadas
	private double sop;			// Total cuotas soportadas operaciones corrientes
	private double res;			// RESULTADO 
	private double pcm;			// Porcentaje cuota mínima
//	private double dvc;			// Devolución cuotas soportadas otros países
	private double cmn;			// Cuota mínima
	private double cad;			// Cuota anual derivada régimen simplificado
//	private double maxImport;
	
//	private double may19Hours; 		//Mayores de 19 años
//	private double men19Hours; 		//Menores de 19 años y trabajadores con contratos de aprendizaje o formación, que no sean discapacitados.
//	private double disHours; 		//Discapacitados con grado de minusvalía igual o superior al 33 por 100
//	private double yearHours; 		//Horas anuales
	
//	private double ownerHours; 		//Horas anuales del titular. (máximo 1.800 horas)
//	private boolean ownerDis; 		//Indique si el titular es discapacitado en grado igual o superior al 33%
//	private double spouseHours; 	//Horas anuales del cónyuge. (máximo 1.800 horas)
//	private double childMen18Hours; //Horas anuales de los hijos menores de 18 años.
	
//	private LinkedList<Mod421ActivityDesk> desks;
//	private LinkedList<Mod421ActivityOven> ovens; // Desglose Superficio del horno (se usa en el 4T a partir de 2025)
	private LinkedList<Mod421ActivityModule> modules;
	
	public Mod421Activity() {
		modules = new LinkedList<>();
		modules.add(new Mod421ActivityModule());
		modules.add(new Mod421ActivityModule());
		modules.add(new Mod421ActivityModule());
		modules.add(new Mod421ActivityModule());
		modules.add(new Mod421ActivityModule());
		modules.add(new Mod421ActivityModule());
		modules.add(new Mod421ActivityModule());
//		initializeDesks();
//		initializeOvens();
	}
	
//	private void initializeDesks() {
//		desks = new LinkedList<>();
//		desks.add(new Mod421ActivityDesk());
//		desks.add(new Mod421ActivityDesk());
//		desks.add(new Mod421ActivityDesk());
//		desks.add(new Mod421ActivityDesk());
//	}
	
//	private void initializeOvens() {
//		ovens = new LinkedList<>();
//		ovens.add(new Mod421ActivityOven());
//		ovens.add(new Mod421ActivityOven());
//		ovens.add(new Mod421ActivityOven());
//		ovens.add(new Mod421ActivityOven());
//	}

	public int getIndex() {
		return index;
	}
	public Mod421Activity setIndex(int index) {
		this.index = index;
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

	public Mod421Activity setEpigraph(String epigraph) {
		this.epigraph = epigraph;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Mod421Activity setDescription(String description) {
		this.description = description;
		return this;
	}

	public int getSpecialEpigraph() {
		return specialEpigraph;
	}

	public Mod421Activity setSpecialEpigraph(int specialEpigraph) {
		this.specialEpigraph = specialEpigraph;
		return this;
	}

//	public double getMaxImport() {
//		return maxImport;
//	}
//
//	public Mod421Activity setMaxImport(double maxImport) {
//		this.maxImport = maxImport;
//		return this;
//	}

	public int getTem() {
		return tem;
	}

	public Mod421Activity setTem(int tem) {
		this.tem = tem;
		return this;

	}

//	public int getEmp() {
//		return emp;
//	}
//
//	public Mod421Activity setEmp(int emp) {
//		this.emp = emp;
//		return this;
//	}
//
//	public int getLor() {
//		return lor;
//	}
//	public Mod421Activity setLor(int lor) {
//		this.lor = lor;
//		return this;
//	}
//
//	public int getCov() {
//		return cov;
//	}
//	public Mod421Activity setCov(int cov) {
//		this.cov = cov;
//		return this;
//	}
	
	public int getDia() {
		return dia;
	}

	public Mod421Activity setDia(int dia) {
		this.dia = dia;
		return this;
	}

	public double getDev() {
		return dev;
	}

	public Mod421Activity setDev(double dev) {
		this.dev = dev;
		return this;
	}

//	public double getRed() {
//		return red;
//	}
//
//	public Mod421Activity setRed(double red) {
//		this.red = red;
//		return this;
//	}
//
//	public double getInd() {
//		return ind;
//	}
//
//	public Mod421Activity setInd(double ind) {
//		this.ind = ind;
//		return this;
//	}

	public double getPor() {
		return por;
	}

	public Mod421Activity setPor(double por) {
		this.por = por;
		return this;
	}

	public double getIng() {
		return ing;
	}

	public Mod421Activity setIng(double ing) {
		this.ing = ing;
		return this;
	}

	public double getSop1() {
		return sop1;
	}

	public Mod421Activity setSop1(double sop1) {
		this.sop1 = sop1;
		return this;
	}

	public double getSopR() {
		return sopR;
	}

	public Mod421Activity setSopR(double sopR) {
		this.sopR = sopR;
		return this;
	}
	
	public double getSop() {
		return sop;
	}

	public Mod421Activity setSop(double sop) {
		this.sop = sop;
		return this;
	}

	public double getIct() {
		return ict;
	}

	public Mod421Activity setIct(double ict) {
		this.ict = ict;
		return this;
	}

	public double getRes() {
		return res;
	}

	public Mod421Activity setRes(double res) {
		this.res = res;
		return this;
	}

	public double getPcm() {
		return pcm;
	}

	public Mod421Activity setPcm(double pcm) {
		this.pcm = pcm;
		return this;		
	}

//	public double getDvc() {
//		return dvc;
//	}
//
//	public Mod421Activity setDvc(double dvc) {
//		this.dvc = dvc;
//		return this;
//	}

	public double getCmn() {
		return cmn;
	}

	public Mod421Activity setCmn(double cmn) {
		this.cmn = cmn;
		return this;
	}

	public double getCad() {
		return cad;
	}

	public Mod421Activity setCad(double cad) {
		this.cad = cad;
		return this;
	}
	
//	public double getMay19Hours() {
//		return may19Hours;
//	}
//	public Mod421Activity setMay19Hours(double may19Hours) {
//		this.may19Hours = may19Hours;
//		return this;
//	}
//
//	public double getMen19Hours() {
//		return men19Hours;
//	}
//	public Mod421Activity setMen19Hours(double men19Hours) {
//		this.men19Hours = men19Hours;
//		return this;
//	}
//
//	public double getDisHours() {
//		return disHours;
//	}
//	public Mod421Activity setDisHours(double disHours) {
//		this.disHours = disHours;
//		return this;
//	}
//
//	public double getYearHours() {
//		return yearHours;
//	}
//	public Mod421Activity setYearHours(double yearHours) {
//		this.yearHours = yearHours;
//		return this;
//	}
//
//	public double getOwnerHours() {
//		return ownerHours;
//	}
//	public Mod421Activity setOwnerHours(double ownerHours) {
//		this.ownerHours = ownerHours;
//		return this;
//	}
//
//	public boolean isOwnerDis() {
//		return ownerDis;
//	}
//	public Mod421Activity setOwnerDis(boolean ownerDis) {
//		this.ownerDis = ownerDis;
//		return this;
//	}
//
//	public double getSpouseHours() {
//		return spouseHours;
//	}
//	public Mod421Activity setSpouseHours(double spouseHours) {
//		this.spouseHours = spouseHours;
//		return this;
//	}
//
//	public double getChildMen18Hours() {
//		return childMen18Hours;
//	}
//	public Mod421Activity setChildMen18Hours(double childMen18Hours) {
//		this.childMen18Hours = childMen18Hours;
//		return this;
//	}
//	
//	public LinkedList<Mod421ActivityDesk> getDesks() {
//		return desks;
//	}
//	public Mod421Activity setDesks(LinkedList<Mod421ActivityDesk> desks) {
//		this.desks = desks;
//		return this;
//	}
//	
//	public LinkedList<Mod421ActivityOven> getOvens() {
//		return ovens;
//	}
//	public Mod421Activity setOvens(LinkedList<Mod421ActivityOven> ovens) {
//		this.ovens = ovens;
//		return this;
//	}
	
	public LinkedList<Mod421ActivityModule> getModules() {
		return modules;
	}
	public Mod421Activity setModules(LinkedList<Mod421ActivityModule> modules) {
		this.modules = modules;
		return this;
	}
	
	public void initialize() {
		this.setEpigraph(null);
		this.setSpecialEpigraph(0);
		this.setDescription(null);
		this.setTem(0);
//		this.setEmp(0);
//		this.setLor(0);
//		this.setDana(0);
//		this.setCov(0);
		this.setDev(0);
//		this.setLorcaReduction(0);
//		this.setDanaReduction(0);
//		this.setRed(0);
//		this.setInd(0);
		this.setDia(0);
		this.setPor(0);
		this.setIng(0);
		this.setSop1(0);
		this.setSopR(0);
//		this.setSop3(0);
		this.setSop(0);
		this.setIct(0);
		this.setRes(0);
		this.setPcm(0);
//		this.setDvc(0);
		this.setCmn(0);
		this.setCad(0);
//		this.setMaxImport(Double.MAX_VALUE);
//		this.setMay19Hours(0.0);
//		this.setMen19Hours(0.0);
//		this.setDisHours(0.0);
//		this.setYearHours(0.0);
//		this.setOwnerHours(0.0);
//		this.setOwnerDis(false);
//		this.setSpouseHours(0.0);
//		this.setChildMen18Hours(0.0);
		this.setModules(new LinkedList<>());
//		initializeDesks();
//		initializeOvens();
	}

	public static Mod421Activity clone(Mod421Activity toClone) {
		Mod421Activity act =new Mod421Activity()
				.setEpigraph(toClone.getEpigraph())
				.setSpecialEpigraph(toClone.getSpecialEpigraph())
				.setDescription(toClone.getDescription())
				.setTem(toClone.getTem())
				.setDia(toClone.getDia())
//				.setEmp(toClone.getEmp())
//				.setLor(toClone.getLor())
//				.setDana(toClone.getDana())
//				.setCov(toClone.getCov())
				.setDev(toClone.getDev())
//				.setLorcaReduction(toClone.getLorcaReduction())
//				.setDanaReduction(toClone.getDanaReduction())
//				.setRed(toClone.getRed())
//				.setInd(toClone.getInd())
				.setPor(toClone.getPor())
				.setIng(toClone.getIng())
				.setSop1(toClone.getSop1())
				.setSopR(toClone.getSopR())
				.setSop(toClone.getSop())
				.setIct(toClone.getIct())
				.setRes(toClone.getRes())
				.setPcm(toClone.getPcm())
//				.setDvc(toClone.getDvc())
				.setCmn(toClone.getCmn())
				.setCad(toClone.getCad())
//				.setMaxImport(toClone.getMaxImport())
//				.setMay19Hours(toClone.getMay19Hours())
//				.setMen19Hours(toClone.getMen19Hours())
//				.setDisHours(toClone.getDisHours())
//				.setYearHours(toClone.getYearHours())
//				.setOwnerHours(toClone.getOwnerHours())
//				.setOwnerDis(toClone.isOwnerDis())
//				.setSpouseHours(toClone.getSpouseHours())
//				.setChildMen18Hours(toClone.getChildMen18Hours())
				;
//		if (toClone.getDesks() != null) {
//			act.setDesks( new LinkedList<>());
//			for (Mod421ActivityDesk desk : toClone.getDesks()) {
//				act.getDesks().add( Mod421ActivityDesk.clone(desk) );
//			}
//		}
//		if (toClone.getOvens() != null) {
//			act.setOvens( new LinkedList<>());
//			for (Mod421ActivityOven oven : toClone.getOvens()) {
//				act.getOvens().add( Mod421ActivityOven.clone(oven) );
//			}
//		}
		if (toClone.getModules() != null) {
			act.setModules( new LinkedList<>());
			for (Mod421ActivityModule mod : toClone.getModules()) {
				act.getModules().add( Mod421ActivityModule.clone(mod) );
			}
		}
		return act;
	}

	public boolean isNotEmpty() {
		return !isEmpty();
	}

	private boolean isEmpty() {
		return AonStringUtils.isBlank(epigraph);
	}

//	public int getDana() {
//		return dana;
//	}
//
//	public Mod421Activity setDana(int dana) {
//		this.dana = dana;
//		return this;
//	}
//
//	public double getLorcaReduction() {
//		return lorcaReduction;
//	}
//
//	public Mod421Activity setLorcaReduction(double lorcaReduction) {
//		this.lorcaReduction = lorcaReduction;
//		return this;
//	}
//
//	public double getDanaReduction() {
//		return danaReduction;
//	}
//
//	public Mod421Activity setDanaReduction(double danaReduction) {
//		this.danaReduction = danaReduction;
//		return this;
//	}

}
