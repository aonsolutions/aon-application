package net.aonsolutions.aon.gwt.udapa.server;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.server.accounting.AccMiningMVELContext;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import net.aonsolutions.aon.gwt.udapa.client.IUdapa;
import net.aonsolutions.aon.gwt.udapa.shared.quality.QualitySheetCode;

@WebServlet(name = "UdapaGwtServlet", urlPatterns = { "/aon_gwt_aio/gwt_udapa" })
public class UdapaImpl extends RemoteServiceServlet implements IUdapa{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public HashMap<String, String> getValues(){
		// TODO GET VALUES TO BD
		HashMap<String, String> map = new HashMap<>();
		QualitySheetCode.valueLinkedList().stream()
			.forEach(key -> map.put(key, "0.0"));
		map.put(QualitySheetCode.UFQO.getName(), "");
		return compute(map);
	}
	
	public HashMap<String, String> updateValue(QualitySheetCode code, String value, HashMap<String, String> map){
		// TODO UPDATE VALUE IN BD
		map.put(code.getName(), value);
		return compute(map);
	}
	
	public static HashMap<String, String> compute(HashMap<String, String> map) {
		AccMiningMVELContext ctx = new AccMiningMVELContext(new IAccMiningKeyAccept() {
			@Override
			public boolean acceptKey(Object key) {
				return AonStringUtils.isNotEmpty((String) key);
			}
		});
		ctx.setExpressionMap(QualitySheetCompute.COMPUTE_MAP);
		
		for(String key : map.keySet()){
			try {
				if (AonStringUtils.isNotEmpty( map.get(key) )) {
					Double d = Double.parseDouble(map.get(key));
					ctx.put("Q"+key, d);
				}
			} catch (NumberFormatException e) {
				// Ignore value
			}
		}

		for (String key : QualitySheetCompute.COMPUTE_MAP.keySet()) {
			String expression = QualitySheetCompute.COMPUTE_MAP.get(key);
			Object ret = ctx.evaluateExpression(key,expression);
			if (ret instanceof Double) {
				Double calculated = (Double) ret;
				ctx.put("Q"+key, calculated);
				if(map.containsKey(key)) map.remove(key);
				Double d = calculated.isInfinite() || calculated.isNaN()
						? 0.0 : AonMathUtils.round(calculated);
				map.put(key, d.toString());
			}
		}
		return map;
	}
}