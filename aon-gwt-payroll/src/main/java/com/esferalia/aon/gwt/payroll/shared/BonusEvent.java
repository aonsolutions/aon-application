package com.esferalia.aon.gwt.payroll.shared;

public class BonusEvent extends Event implements HasBonus {
	Bonus bonus;

	@Override
	public Bonus getBonus() {
		return bonus;
	}

	public BonusEvent setBonus(Bonus bonus) {
		this.bonus = bonus;
		return this;
	}

}