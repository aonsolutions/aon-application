package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedList;

public class Mod131 extends FiscalModel implements Serializable {

	public static class Mod131Activity implements Serializable {

		private static final long serialVersionUID = 1614297937612202267L;
		
		private String epigraph;
		private String description;
		private double netYield;
		private double percent;
		private double result;
		
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
		public double getNetYield() {
			return netYield;
		}
		public Mod131Activity setNetYield(double netYield) {
			this.netYield = netYield;
			return this;
		}
		public double getPercent() {
			return percent;
		}
		public Mod131Activity setPercent(double percent) {
			this.percent = percent;
			return this;
		}
		public double getResult() {
			return result;
		}
		public Mod131Activity setResult(double result) {
			this.result = result;
			return this;
		}
	}
	
	private static final long serialVersionUID = 3614782856588153510L;
	
	private LinkedList<Mod131Activity> activities = new LinkedList<Mod131Activity>();
	private double c01;
	private double c02;
	private double c03;
	private double c04;
	private double c05;
	private double c06;
	private double c07;
	private double c08;
	private double c09;
	private double c10;
	private double c11;
	private double c12;
	private double c13;
	private double c14;
	private double c15;
	private double c16;
	
	public LinkedList<Mod131Activity> getActivities() {
		return activities;
	}
	public Mod131 setActivities(LinkedList<Mod131Activity> activities) {
		this.activities = activities;
		return this;
	}
	public double getC01() {
		return c01;
	}
	public Mod131 setC01(double c01) {
		this.c01 = c01;
		return this;
	}
	public double getC02() {
		return c02;
	}
	public Mod131 setC02(double c02) {
		this.c02 = c02;
		return this;
	}
	public double getC03() {
		return c03;
	}
	public Mod131 setC03(double c03) {
		this.c03 = c03;
		return this;
	}
	public double getC04() {
		return c04;
	}
	public Mod131 setC04(double c04) {
		this.c04 = c04;
		return this;
	}
	public double getC05() {
		return c05;
	}
	public Mod131 setC05(double c05) {
		this.c05 = c05;
		return this;
	}
	public double getC06() {
		return c06;
	}
	public Mod131 setC06(double c06) {
		this.c06 = c06;
		return this;
	}
	public double getC07() {
		return c07;
	}
	public Mod131 setC07(double c07) {
		this.c07 = c07;
		return this;
	}
	public double getC08() {
		return c08;
	}
	public Mod131 setC08(double c08) {
		this.c08 = c08;
		return this;
	}
	public double getC09() {
		return c09;
	}
	public Mod131 setC09(double c09) {
		this.c09 = c09;
		return this;
	}
	public double getC10() {
		return c10;
	}
	public Mod131 setC10(double c10) {
		this.c10 = c10;
		return this;
	}
	public double getC11() {
		return c11;
	}
	public Mod131 setC11(double c11) {
		this.c11 = c11;
		return this;
	}
	public double getC12() {
		return c12;
	}
	public Mod131 setC12(double c12) {
		this.c12 = c12;
		return this;
	}
	public double getC13() {
		return c13;
	}
	public Mod131 setC13(double c13) {
		this.c13 = c13;
		return this;
	}
	public double getC14() {
		return c14;
	}
	public Mod131 setC14(double c14) {
		this.c14 = c14;
		return this;
	}
	public double getC15() {
		return c15;
	}
	public Mod131 setC15(double c15) {
		this.c15 = c15;
		return this;
	}
	public double getC16() {
		return c16;
	}
	public Mod131 setC16(double c16) {
		this.c16 = c16;
		return this;
	}
	public Mod131Activity getActivity(int i) {
		Mod131Activity act = null;
		if (!hasActivity(i)) {
			act = new Mod131Activity();
			getActivities().add(act);
		} 
		act = getActivities().get(i); 	
		return act;
	}
	public boolean hasActivity(int i) {
		return (getActivities() != null
			&& !getActivities().isEmpty()
			&& getActivities().size() > i
			&& getActivities().get(i) != null);
	}
	
}
