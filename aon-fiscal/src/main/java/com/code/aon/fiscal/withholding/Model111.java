package com.code.aon.fiscal.withholding;

import java.util.HashMap;
import java.util.Map;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.fiscal.enumeration.Period;

public class Model111 {

	private Enterprise enterprise;
	private Integer year;
	private Period period;
	private Map<Integer,ModelDetail > map;

	public Map<Integer, ModelDetail> getMap() {
		if (map == null) {
			map = new HashMap<Integer, ModelDetail>();
			for (int i = 1; i < 11; i++) {
				map.put(i, new ModelDetail());
			}
		}
		return map;
	}
	
	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public double getTotal() {
		double total = 0;
		for (ModelDetail detail : getMap().values() ) {
			total = CommonUtil.round(total + detail.getQuota()); 
		}
		return total;
	}
	
	public ModelDetail getDetail1() {
		return getMap().get(1);
	}
	public ModelDetail getDetail2() {
		return getMap().get(2);
	}
	public ModelDetail getDetail3() {
		return getMap().get(3);
	}
	public ModelDetail getDetail4() {
		return getMap().get(4);
	}
	public ModelDetail getDetail5() {
		return getMap().get(5);
	}
	public ModelDetail getDetail6() {
		return getMap().get(6);
	}
	public ModelDetail getDetail7() {
		return getMap().get(7);
	}
	public ModelDetail getDetail8() {
		return getMap().get(8);
	}
	public ModelDetail getDetail9() {
		return getMap().get(9);
	}
	public ModelDetail getDetail10() {
		return getMap().get(10);
	}
}
