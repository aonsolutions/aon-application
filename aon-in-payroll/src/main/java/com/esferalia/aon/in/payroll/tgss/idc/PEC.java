package com.esferalia.aon.in.payroll.tgss.idc;

import java.util.Date;

public abstract class PEC {
	
	public static interface Visitor<T> {
		T visitCost(Cost cost);
		T visitBonus(Bonus bonus);
		T visitDeduction(Deduction deduction);
	}
	
	public static class Cost extends PEC {
		@Override
		public <T> T visit(Visitor<T> visitor) {
			return visitor.visitCost(this);
		}
	}

	public static class Bonus extends PEC {
		@Override
		public <T> T visit(Visitor<T> visitor) {
			return visitor.visitBonus(this);
		}
	}

	public static class Deduction extends PEC {
		@Override
		public <T> T visit(Visitor<T> visitor) {
			return visitor.visitDeduction(this);
		}
	}

	public static boolean isCost(PEC pec) {
		return pec.visit(new Visitor<Boolean>() {
			@Override
			public Boolean visitCost(Cost cost) {
				return true;
			}
			@Override
			public Boolean visitBonus(Bonus bonus) {
				return false;
			}
			@Override
			public Boolean visitDeduction(Deduction deduction) {
				return false;
			}
		});
	}

	public static boolean isBonus(PEC pec) {
		return pec.visit(new Visitor<Boolean>() {
			@Override
			public Boolean visitCost(Cost cost) {
				return false;
			}
			@Override
			public Boolean visitBonus(Bonus bonus) {
				return true;
			}
			@Override
			public Boolean visitDeduction(Deduction deduction) {
				return false;
			}
		});
	}

	public static boolean isDeduction(PEC pec) {
		return pec.visit(new Visitor<Boolean>() {
			@Override
			public Boolean visitCost(Cost cost) {
				return false;
			}
			@Override
			public Boolean visitBonus(Bonus bonus) {
				return false;
			}
			@Override
			public Boolean visitDeduction(Deduction deduction) {
				return true;
			}
		});
	}

	private String naf;
	private String ccc;
	private Date endDate;
	private Date startDate;
	private String name;
	private String description;
	private String expression;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFormula() {
		return expression;
	}

	public void setFormula(String formula) {
		this.expression = formula;
	}

	public String getSsNum() {
		return naf;
	}

	public void setNss(String ssNum) {
		this.naf = ssNum;
	}

	public String getCcc() {
		return ccc;
	}

	public void setCcc(String ccc) {
		this.ccc = ccc;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public abstract <T> T visit(Visitor<T> visitor);
	
	@Override
	public String toString() {
		return "SSBonus -> SS Number : " + getSsNum() + ", CCC : " + getCcc() + ", Description : " + getDescription() + ", Formula : " + getFormula() + ", Start : "
				+ getStartDate() + ", End : " + getEndDate();
	}
}