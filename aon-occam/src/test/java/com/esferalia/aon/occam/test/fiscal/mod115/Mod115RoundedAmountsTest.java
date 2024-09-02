package com.esferalia.aon.occam.test.fiscal.mod115;

import java.text.MessageFormat;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod115RoundedAmountsTest extends AbstractOccamTest {
	
	@Test
	public void testRound() {
		MODEL115.getMod115s(getOccam())
			.stream()
			.map( model -> MODEL115.get(getOccam(), model.getId()))
			.flatMap(mod115 -> mod115.getMap().values().stream())
			.forEach( det -> {
				Asserts.assertEqualsDouble(
					MessageFormat.format("Type: {0} Acumulado",det.getType())
					,AonMathUtils.round(det.getAccumulatedAmount()) 
					,det.getAccumulatedAmount());
				Asserts.assertEqualsDouble(
					MessageFormat.format("Type: {0} Declarado",det.getType())
					,AonMathUtils.round(det.getDeclaredAmount()) 
					,det.getDeclaredAmount());
				Asserts.assertEqualsDouble(
					MessageFormat.format("Type: {0} Resultado",det.getType())
					,AonMathUtils.round(det.getResultAmount()) 
					,det.getResultAmount());
				Asserts.assertEqualsDouble(
					MessageFormat.format("Type: {0} Ajuste",det.getType())
					,AonMathUtils.round(det.getAdjustAmount()) 
					,det.getAdjustAmount());
				Asserts.assertEqualsDouble(
					MessageFormat.format("Type: {0} Amount",det.getType())
					,AonMathUtils.round(det.getAmount()) 
					,det.getAmount());
		});
	}
	
}
