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
	
	private int tem;			// Actividad de temporada: Número de días de ejercicio de la actividad (año anterior o año actual)
	private int dia;			// Número de días de ejercicio de la actividad en el trimestre

	private double dev;			// Cuota devengada operaciones corrientes
	private double ict;			// Indice corrector de actividades de temporada
	
	// 1T, 2T, 3T
	private double por;			// Porcentaje de ingreso a cuenta
	private double ing;			// Ingreso a cuenta
	
	// 4T	
	private double sop1;		// 1% cuota devengada
	private double sopR;		// Resto de cuotas soportadas
	private double sop;			// Total cuotas soportadas operaciones corrientes
	private double res;			// RESULTADO 
	private double pcm;			// Porcentaje cuota mínima
	private double cmn;			// Cuota mínima
	private double cad;			// Cuota anual derivada régimen simplificado
	
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
	}
	
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

	public int getTem() {
		return tem;
	}

	public Mod421Activity setTem(int tem) {
		this.tem = tem;
		return this;

	}

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
		this.setDev(0);
		this.setDia(0);
		this.setPor(0);
		this.setIng(0);
		this.setSop1(0);
		this.setSopR(0);
		this.setSop(0);
		this.setIct(0);
		this.setRes(0);
		this.setPcm(0);
		this.setCmn(0);
		this.setCad(0);
		this.setModules(new LinkedList<>());
	}

	public static Mod421Activity clone(Mod421Activity toClone) {
		Mod421Activity act =new Mod421Activity()
				.setEpigraph(toClone.getEpigraph())
				.setSpecialEpigraph(toClone.getSpecialEpigraph())
				.setDescription(toClone.getDescription())
				.setTem(toClone.getTem())
				.setDia(toClone.getDia())
				.setDev(toClone.getDev())
				.setPor(toClone.getPor())
				.setIng(toClone.getIng())
				.setSop1(toClone.getSop1())
				.setSopR(toClone.getSopR())
				.setSop(toClone.getSop())
				.setIct(toClone.getIct())
				.setRes(toClone.getRes())
				.setPcm(toClone.getPcm())
				.setCmn(toClone.getCmn())
				.setCad(toClone.getCad())
				;
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

	public boolean isAgraria() {
		return getEpigraph() != null && getEpigraph().trim().startsWith("0") ;
	}
	
}
