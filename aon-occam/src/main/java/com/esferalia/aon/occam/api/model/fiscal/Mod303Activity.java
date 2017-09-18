package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod303Activity implements Serializable {
	
	
	private static final long serialVersionUID = 1503338850244122274L;
	
	private String epigraph;
	private String description;
	private int specialEpigraph;
	
	private int tem;			// Actividad de temporada: n\u00AA de d\u00EDas de ejercicio en el a\u00F1o anterior
	private int dia;			// Número de días de ejercicio de la actividad en el trimestre
	private int emp;			// Número de empleados a 1-01 (o en la fecha de inicio de la actividad)
	private int lor;			// Si realiza la actividad en LORCA, seleccione lo que proceda
	
	private double dev;			// Cuota devengada operaciones corrientes
	private double red;			// Reducciones
	
	// 1T, 2T, 3T
	private double ind;			// Índice corrector de actividades de temporada
	private double por;			// Porcentaje de ingreso a cuenta
	private double ing;			// Ingreso a cuenta
	
	// 4T	
	private double sop;			// Cuotas soportadas operaciones corrientes
	private double ict;			// Índice corrector de actividades de temporada
	private double res;			// RESULTADO ( [C] - [D] - [G] ) x [H]
	private double pcm;			// Porcentaje cuota mínima
	private double dvc;			// Devolución cuotas soportadas otros países
	private double cmn;			// Cuota mínima
	private double cad;			// Cuota anual derivada régimen simplificado
	
	private double maxImport;
	
	private LinkedList<Mod303ActivityModule> modules;
	
	public Mod303Activity() {
		modules = new LinkedList<Mod303ActivityModule>();
		modules.add(new Mod303ActivityModule());
		modules.add(new Mod303ActivityModule());
		modules.add(new Mod303ActivityModule());
		modules.add(new Mod303ActivityModule());
		modules.add(new Mod303ActivityModule());
		modules.add(new Mod303ActivityModule());
		modules.add(new Mod303ActivityModule());
	}
	
	public String getFullDescription() {
		return AonStringUtils.trimToEmpty(epigraph)
			+ (AonStringUtils.isBlank(description)?AonStringUtils.EMPTY:AonStringUtils.HYPHEN) 
			+ AonStringUtils.trimToEmpty(description);
	}
		
	public String getEpigraph() {
		return epigraph;
	}

	public Mod303Activity setEpigraph(String epigraph) {
		this.epigraph = epigraph;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Mod303Activity setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public int getSpecialEpigraph() {
		return specialEpigraph;
	}

	public Mod303Activity setSpecialEpigraph(int specialEpigraph) {
		this.specialEpigraph = specialEpigraph;
		return this;
	}

	public double getMaxImport() {
		return maxImport;
	}

	public Mod303Activity setMaxImport(double maxImport) {
		this.maxImport = maxImport;
		return this;
	}

	public int getTem() {
		return tem;
	}

	public Mod303Activity setTem(int tem) {
		this.tem = tem;
		return this;

	}

	public int getEmp() {
		return emp;
	}

	public Mod303Activity setEmp(int Emp) {
		this.emp = Emp;
		return this;
	}

	public int getLor() {
		return lor;
	}

	public Mod303Activity setLor(int lor) {
		this.lor = lor;
		return this;
	}

	public int getDia() {
		return dia;
	}

	public Mod303Activity setDia(int dia) {
		this.dia = dia;
		return this;
	}

	public double getDev() {
		return dev;
	}

	public Mod303Activity setDev(double dev) {
		this.dev = dev;
		return this;
	}

	public double getRed() {
		return red;
	}

	public Mod303Activity setRed(double red) {
		this.red = red;
		return this;
	}

	public double getInd() {
		return ind;
	}

	public Mod303Activity setInd(double ind) {
		this.ind = ind;
		return this;
	}

	public double getPor() {
		return por;
	}

	public Mod303Activity setPor(double por) {
		this.por = por;
		return this;
	}

	public double getIng() {
		return ing;
	}

	public Mod303Activity setIng(double ing) {
		this.ing = ing;
		return this;
	}

	public double getSop() {
		return sop;
	}

	public Mod303Activity setSop(double sop) {
		this.sop = sop;
		return this;
	}

	public double getIct() {
		return ict;
	}

	public Mod303Activity setIct(double ict) {
		this.ict = ict;
		return this;
	}

	public double getRes() {
		return res;
	}

	public Mod303Activity setRes(double res) {
		this.res = res;
		return this;
	}

	public double getPcm() {
		return pcm;
	}

	public Mod303Activity setPcm(double pcm) {
		this.pcm = pcm;
		return this;		
	}

	public double getDvc() {
		return dvc;
	}

	public Mod303Activity setDvc(double dvc) {
		this.dvc = dvc;
		return this;
	}

	public double getCmn() {
		return cmn;
	}

	public Mod303Activity setCmn(double cmn) {
		this.cmn = cmn;
		return this;
	}

	public double getCad() {
		return cad;
	}

	public Mod303Activity setCad(double cad) {
		this.cad = cad;
		return this;
	}

	public LinkedList<Mod303ActivityModule> getModules() {
		return modules;
	}

	public Mod303Activity setModules(LinkedList<Mod303ActivityModule> modules) {
		this.modules = modules;
		return this;
	}
	public void initialize() {
		this.setEpigraph(null);
		this.setDescription(null);
		this.setTem(0);
		this.setEmp(0);
		this.setLor(0);
		this.setDev(0);
		this.setRed(0);
		this.setInd(0);
		this.setDia(0);
		this.setPor(0);
		this.setIng(0);
		this.setMaxImport(Double.MAX_VALUE);
		this.setModules(new LinkedList<Mod303ActivityModule>());
	}

	public static Mod303Activity clone(Mod303Activity toClone) {
		Mod303Activity act =new Mod303Activity()
				.setEpigraph(toClone.getEpigraph())
				.setDescription(toClone.getDescription())
				.setTem(toClone.getTem())
				.setDia(toClone.getDia())
				.setEmp(toClone.getEmp())
				.setLor(toClone.getLor())
				.setDev(toClone.getDev())
				.setRed(toClone.getRed())
				.setInd(toClone.getInd())
				.setPor(toClone.getPor())
				.setIng(toClone.getIng())
				.setMaxImport(toClone.getMaxImport())
				;
		if (toClone.getModules() != null) {
			act.setModules( new LinkedList<Mod303ActivityModule>());
			for (Mod303ActivityModule mod : toClone.getModules()) {
				act.getModules().add( Mod303ActivityModule.clone(mod) );
			}
		}
		return act;
	}

}
