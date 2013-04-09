package com.esferalia.aon.gwt.payroll.shared;

import java.util.Comparator;

import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.Level;

public class LevelComparator implements Comparator<Level> {

	public int compare(Level l0, Level l1) {
		
		String description0 = l0.getDescription();
		String description1 = l1.getDescription();
		if (description0 == null) {
			return description1 == null ? 0 : -1;
		}
		return description0.compareTo(description1);
		
	};
}
