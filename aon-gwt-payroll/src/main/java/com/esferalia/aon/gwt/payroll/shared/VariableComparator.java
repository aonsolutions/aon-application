package com.esferalia.aon.gwt.payroll.shared;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;

public class VariableComparator implements Comparator<Variable> {

	private static Map<Scope, Short> SCOPE_WEIGHT = new HashMap<Scope, Short>() {
		{
			put(Scope.SALARY, (short) 0);
			put(Scope.CONTRACT, (short) 0);
			put(Scope.AGREEMENT, (short) 1);
			put(Scope.APPLICATION, (short) 2);
			put(Scope.SYSTEM, (short) 2);
		}
	};

	@Override
	public int compare(Variable var1, Variable var2) {

		Scope scope1 = var1.getScope();
		Scope scope2 = var2.getScope();

		int compare = compareTo(SCOPE_WEIGHT.get(scope1),
				SCOPE_WEIGHT.get(scope2));
		if (compare != 0)
			return compare;

		compare = compareTo(var1.getName(), var2.getName());
		if (compare != 0)
			return compare;

		compare = compareTo(var1.getStartDate(), var2.getStartDate());
		if (compare != 0)
			return compare;

		compare = compareTo(var1.getEndDate(), var2.getEndDate());
		if (compare != 0)
			return compare;

		return 0;
	}
	
	static <V extends Comparable<V>> int compareTo(V obj1, V obj2) {
		if (obj1 == obj2)
			return 0;
		if (obj1 == null)
			return -1;
		if (obj2 == null)
			return 1;
		return obj1.compareTo(obj2);
	}
	
}