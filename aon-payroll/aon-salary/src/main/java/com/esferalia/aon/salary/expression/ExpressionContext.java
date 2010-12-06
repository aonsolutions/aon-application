package com.esferalia.aon.salary.expression;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.salary.ISalary;

public class ExpressionContext {
	
	private Map<String,IExpression> map;
	
	public ExpressionContext() {
		map = new HashMap<String, IExpression>();
	}

	public void putAll( Map<String,IExpression> map ) {
		map.putAll(map);
	}
	
	public IExpression get(String key) {
		return map.get(key);
	}
	public IExpression put(IExpression expression) {
		return map.put(expression.getName(),expression);
	}
	
	public double resolve(IExpression expression) throws ExpressionException {
		double d = 0.0;
		if (NumberUtils.isNumber(expression.getExpression()) ) {
			d= NumberUtils.toDouble(expression.getExpression());	
		}
		return d;
	}
	
	// TODO este método hay que borrarlo cuando se resulvan las expresiones correctamente.
	@Deprecated
	public double resolve(IExpression expression,double totalDevengado) throws ExpressionException {
		double d = 0.0;
		if (NumberUtils.isNumber(expression.getExpression()) ) {
			d= NumberUtils.toDouble(expression.getExpression());	
		} else {
			if (StringUtils.endsWith(expression.getExpression(), "%")) {
				String func = StringUtils.stripEnd(expression.getExpression(), "%");
				if (NumberUtils.isNumber(func) ) {
					double percent = NumberUtils.toDouble(func);
					d= CommonUtil.round(totalDevengado * percent / 100 );
				}
			}
		}
		return d;
	}

	public Collection<IExpression> getValues() {
		return map.values();
	}
	
}
