package com.esferalia.aon.occam.test.fiscal.mod131;

import java.text.MessageFormat;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL131;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod131RoundedAmountsTest extends AbstractOccamTest {
	
	@Test
	public void testRound() {
		MODEL131.getMod131s(getOccam())
			.stream()
			.map( model -> MODEL131.get(getOccam(), model.getId()))
			.flatMap(mod131 -> mod131.getMap().values().stream())
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
