package net.aonsolutions.aon.gwt.udapa.server;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Optional;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.Income;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
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
	
	public HashMap<String, String> getValues(String domainName, Integer domainId, Integer drId){
		String login = "";
		
		HashMap<String, String> map = new HashMap<>();
		QualitySheetCode.valueLinkedList().stream()
		.forEach(key -> map.put(key, "0.0"));
		map.put(QualitySheetCode.UFQO.getName(), "");
		
		AON.getDataResponseDetailStream(domainName, domainId, login, 
				f -> f.getDataResponseProperty().eq(drId))
		.forEach(drd -> {
			map.put(drd.getDataVariable(), drd.getDataValue());
		});
		String source = map.get("source");
		System.out.println(source);
		String[] arr = source.split("@");
		System.out.println(arr);
		Optional<IncomeDetail> idOptional = AON.getIncomeDetail(domainName, domainId, login, f-> f.getIdProperty().eq(Integer.parseInt(arr[1])));
	
		if(idOptional.isPresent()){
			IncomeDetail id = idOptional.get();
			Warehouse w = AON.getWarehouse(domainName, domainId, login, f -> f.getIdProperty().eq(id.getWarehouse()));
			Income i = AON.getIncome(domainName, domainId, login, f -> f.getIdProperty().eq(id.getIncome().getId())).get();
			RAddress addr = AON.getRAddres(domainName, domainId, login, i.getSupplier());
		
			map.put("full_address", addr.getFullAddress());
			map.put("end_address", addr.getZip() + " " + addr.getCity() + " " + addr.getGeozoneName());
		
			map.put("warehouse", w.getName());
			map.put("product_description", id.getDescription());
			map.put("product_supplier", i.getSupplierName());
			map.put("product_quantity", id.getQuantity() + "");

			if(i.getCarrierPacking() != null){
				CarrierPacking cp = AON.getCarrierPacking(domainName, domainId, login, f -> f.getIdProperty().eq(i.getCarrierPacking()));
				map.put("transport_carrier", cp.getCarrierName());
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				map.put("transport_delivery_date", cp.getDeliveryDate() != null ? dateFormat.format(cp.getDeliveryDate()) : "-");
				map.put("transport_driver_name", cp.getDriverName() != null ? cp.getDriverName() : "-");
				map.put("transport_driver_document", cp.getDriverDocument() != null ? cp.getDriverDocument() : "-");
				map.put("transport_number_plate", cp.getNumberPlate() != null ? cp.getNumberPlate() : "-");
				map.put("bruto", cp.getGross() != null ? cp.getGross().toString() : "-");
				map.put("tara", cp.getTare() != null ? cp.getTare().toString() : "-");
				Double net = cp.getNet();
				if(map.containsKey(QualitySheetCode.UFQDT1.getName())){
					String str = map.get(QualitySheetCode.UFQDT1.getName());
					Double dbl = Double.parseDouble(str);
					net = net != null && dbl!= null ? AonMathUtils.round(net - dbl) : 0.0;
				}
				map.put("neto", cp.getNet() != null ? cp.getNet().toString() : "0.0");
				map.put(QualitySheetCode.UFQDT2.getName(), net != null ? net.toString() : "0.0");
			}
		}
		return compute(map);
	}
	
	public HashMap<String, String> updateValue(String domainName, Integer domainId, Integer drId, QualitySheetCode code, String value, HashMap<String, String> map){
		String login = "";
		DataResponseDetail drd = new DataResponseDetail();
		drd.setDomain(domainId);
		drd.setDataResponse(drId);
		drd.setDataVariable(code.getName());
		drd.setDataValue(value);
		Optional<DataResponseDetail> opt = AON.getDataResponseDetail(domainName, domainId, login, f ->
			f.getDomainProperty().eq(domainId).and(f.getDataVariableProperty().eq(code.getName())));
		if(opt.isPresent()){			
			AON.updateDataResponseDetail(domainName, domainId, login, drd, f -> f.getIdProperty().eq(opt.get().getId()));
		} else AON.insertDataResponseDetail(domainName, domainId, login, drd);
		map.put(code.getName(), value);
		
		if(QualitySheetCode.UFQDT1.equals(code)){
			Double net = Double.parseDouble(map.get("neto"));
			if(map.containsKey(QualitySheetCode.UFQDT1.getName())){
				String str = map.get(QualitySheetCode.UFQDT1.getName());
				Double dbl = Double.parseDouble(str);
				net = AonMathUtils.round(net - dbl);
			}
			map.put(QualitySheetCode.UFQDT2.getName(), net != null ? net.toString() : "0.0");
		}
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

	@Override
	public void deleteQuality(String domainName, Integer domainId, Integer drId) {
		//AON.deleteAttach(domainName, domainId, "", f -> f.getAttachModuleProperty().eq(drId), AttachType.DATA);
		AON.deleteDataResponseDetail(domainName, domainId, "", f -> f.getDataResponseProperty().eq(drId));
		AON.deleteDataResponse(domainName, domainId, "", f -> f.getIdProperty().eq(drId));
	}
}