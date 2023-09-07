package com.esferalia.aon.occam.test.fiscal.mod130;

import java.text.MessageFormat;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL130;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod130RoundedAmountsTest extends AbstractOccamTest {
	
	@Test
	public void testRound() {
		MODEL130.getMod130s(getOccam())
			.stream()
			.map( model -> MODEL130.get(getOccam(), model.getId()))
			.flatMap(mod130 -> mod130.getMap().values().stream())
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
