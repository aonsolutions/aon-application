package com.esferalia.aon.ui.payroll.controller;

import javax.faces.event.ActionEvent;

import org.mvel2.PropertyAccessException;
import org.mvel2.UnresolveablePropertyException;

import com.esferalia.aon.payroll.DeductionConcept;
import com.esferalia.aon.salary.expression.ExpressionContext;




// Extiende de PaymentConceptController, porque los métodos para obtener el 
// contexto básico para el insight de las expresiones, son los mismos.
// en el momento en el que eso cambie, la forma de obtener el contexto básico se debe 
// externalizar y aplicar los cambios a ambos controladores.
public class DeductionConceptController extends AbstractConceptController {
	private Throwable expressionException;

	// -------------------------------------------------------------------------

	public boolean isExpressionValid() {
		try {
			analyze(getDeductionConcept().getExpression());
			return true;
		} catch (Throwable e) {
			expressionException = e;
			return false;
		}
	}

	public String getExpressionErrorMessage() {
		return expressionException.getMessage();
	}

	public void onChangeExpression(ActionEvent event) {
	}
	// --------------------------------------------------------- Private Methods

	private void analyze(String expression) {
		try {
			ExpressionContext.analyze(expression);
		} catch ( PropertyAccessException e ){
			
		} catch ( UnresolveablePropertyException e ){
		}
	}

	private DeductionConcept getDeductionConcept() {
		return (DeductionConcept) getTo();
	}
	

}
