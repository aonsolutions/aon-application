package com.code.aon.ui.academy.print.utils;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.fill.AbstractValueProvider;
import net.sf.jasperreports.engine.fill.JRAbstractExtendedIncrementer;
import net.sf.jasperreports.engine.fill.JRAbstractExtendedIncrementerFactory;
import net.sf.jasperreports.engine.fill.JRCalculable;
import net.sf.jasperreports.engine.fill.JRExtendedIncrementer;

public class CommentsIncrementorFactory extends JRAbstractExtendedIncrementerFactory {

	public static JRExtendedIncrementer INCREMENTOR = new JRAbstractExtendedIncrementer() {
		
		public Object increment(JRCalculable variablePublicNotes,
				Object publicNote, AbstractValueProvider abstractValueProvider)
				throws JRException {
			Object publicNotes = variablePublicNotes.getIncrementedValue();
			if (publicNote != null) {
				if (publicNotes == null) {
					publicNotes = initialValue().toString();
				}
				publicNotes = publicNotes + " " + publicNote;
			}
			return publicNotes;
		}

		public Object initialValue() {
			return "";
		}
	};

	public JRExtendedIncrementer getExtendedIncrementer(byte calculation) {
		if (calculation == JRVariable.CALCULATION_NOTHING) {
			return INCREMENTOR;
		}
		throw new UnsupportedOperationException(
				"CommentsIncrementorFactory can only do Nothing calculations");
	}
}