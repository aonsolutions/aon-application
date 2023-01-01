package com.esferalia.aon.occam.test.fiscal.mod303;

import java.text.MessageFormat;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod303RoundedAmountsTest extends Mod303AbstractTest {
	
	@Test
	public void testFinalize() {
		MODEL303.getMod303s(getOccam())
			.stream()
			.map( model -> MODEL303.get(getOccam(), model.getId()))
			.flatMap(mod303 -> mod303.getMap().values().stream())
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
