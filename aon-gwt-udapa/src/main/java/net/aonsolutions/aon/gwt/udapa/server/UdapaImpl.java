package net.aonsolutions.aon.gwt.udapa.server;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Optional;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.Income;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.server.accounting.AccMiningMVELContext;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import net.aonsolutions.aon.gwt.udapa.client.IUdapa;
import net.aonsolutions.aon.gwt.udapa.shared.quality.Destiny;
import net.aonsolutions.aon.gwt.udapa.shared.quality.QualitySheetCode;

@WebServlet(name = "UdapaGwtServlet", urlPatterns = { "/aon_gwt_aio/gwt_udapa" })
public class UdapaImpl extends RemoteServiceServlet implements IUdapa{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static UdapaImpl getInstance() {
		return new UdapaImpl();
	}
	
	public HashMap<String, String> getValues(String domainName, Integer domainId, Integer drId){
		String login = "";
		DataResponse dr = AON.getDataResponse(domainName, domainId, login, DataResponseSource.QUALITY, f -> f.getIdProperty().eq(drId));
		
		HashMap<String, String> map = new HashMap<>();
		QualitySheetCode.valueLinkedList().stream()
		.forEach(key -> map.put(key, "0.0"));
		map.put(QualitySheetCode.UFQO.getName(), "");
		map.put(QualitySheetCode.UFQCE011.getName(), "");
		map.put(QualitySheetCode.UFQCE021.getName(), "");
		
		AON.getDataResponseDetailStream(domainName, domainId, login, 
				f -> f.getDataResponseProperty().eq(drId))
		.forEach(drd -> {
			map.put(drd.getDataVariable(), drd.getDataValue());
		});
		Integer idIdAux = dr.getSourceId();
					
		if(idIdAux == null && map.containsKey("source")) {
			String source = map.get("source");
			System.out.println(source);
			String[] arr = source.split("@");
			System.out.println(arr);
			idIdAux = Integer.parseInt(arr[1]);
		}
		Integer idId = idIdAux;
		Optional<IncomeDetail> idOptional = AON.getIncomeDetail(domainName, domainId, login, f-> f.getIdProperty().eq(idId));
	
		if(idOptional.isPresent()){
			IncomeDetail id = idOptional.get();
			Warehouse w = AON.getWarehouse(domainName, domainId, login, f -> f.getIdProperty().eq(id.getWarehouse()));
			Income i = AON.getIncome(domainName, domainId, login, f -> f.getIdProperty().eq(id.getIncome().getId())).get();
			RAddress addr = AON.getRAddres(domainName, domainId, login, i.getSupplier());
		
			map.put("full_address", addr.getFullAddress());
			map.put("end_address", addr.getZip() + " " + addr.getCity() + " " + addr.getGeozoneName());
		
			map.put("warehouse", w.getName());
			map.put("product_description", id.getDescription());
			
			Item item = AON.getItem(domainName, domainId, login, f -> f.getIdProperty().eq(id.getItem().getId()));
			Product product = AON.getProduct(domainName, domainId, login, f -> f.getIdProperty().eq(item.getProductId()));
			Optional<Supplier> supplier = AON.getSupplier(domainName, domainId, login, f -> f.getIdProperty().eq(i.getSupplier()));
			map.put("product_name", product.getName());
			map.put("product_supplier", i.getSupplierName());
			if(supplier.get().getAlias() != null && !supplier.get().getAlias().isEmpty()) {
				map.put("product_supplier_alias", supplier.get().getAlias());
			}
			map.put("product_quantity", id.getQuantity() + "");
				
			if(map.get(QualitySheetCode.UFQCC01.getName()).equals("0.0") 
					|| (!isPropaco(map) && Double.parseDouble(map.get(QualitySheetCode.UFQCC01.getName())) > id.getQuantity())) {
				map.put(QualitySheetCode.UFQCC01.getName(), id.getQuantity() + "");
				updateValue(domainName, domainId, drId, QualitySheetCode.UFQCC01.getName(), id.getQuantity() + "", map);
			}
			
			if(!map.containsKey("product_price")) {
				DataResponseDetail drd = new DataResponseDetail();
				drd.setDomain(domainId);
				drd.setDataResponse(drId);
				drd.setDataVariable("product_price");
				drd.setDataValue(id.getPrice() + "");
				AON.insertDataResponseDetail(domainName, domainId, login, drd);
				map.put("product_price", id.getPrice() + "");
			} else if(map.get("product_price").equals("0") || map.get("product_price").equals("0.0")) {
				if(id.getPurchaseDetail() != null) {
					PurchaseDetail pd = AON.getPurchaseDetail(domainName, domainId, login, f -> f.getIdProperty().eq(id.getPurchaseDetail()));
					DataResponseDetail drd = new DataResponseDetail();
					drd.setDomain(domainId);
					drd.setDataResponse(drId);
					drd.setDataVariable("product_price");
					drd.setDataValue(pd.getPrice() + "");
					AON.insertDataResponseDetail(domainName, domainId, login, drd);
					map.put("product_price", pd.getPrice() + "");
				}
			}
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
				map.put("tara_adicional", cp.getAdditionalTare() != null ? cp.getAdditionalTare().toString() : "-");
				map.put("neto", cp.getNet() != null ? cp.getNet().toString() : "0.0");
				
				if(isPropaco(map) && Double.parseDouble(map.get(QualitySheetCode.UFQCC01.getName())) > cp.getNet()) {
					map.put(QualitySheetCode.UFQCC01.getName(), cp.getNet() + "");
					updateValue(domainName, domainId, drId, QualitySheetCode.UFQCC01.getName(), cp.getNet() + "", map);
				}
			}
			
			if("0.0".equals(map.get(QualitySheetCode.UFQC2.getName()))){
				ApplicationParameter app = AON.getApplicationParameter(domainName, domainId, login, AppParam.QUALITY_PFONDO);
				map.put(QualitySheetCode.UFQC2.getName(), app != null ? app.getValue(): "0.0");
			}
 		}
		return compute(map);
	}

	public HashMap<String, String> getPaturpatValues(String domainName, Integer domainId, Integer drId){
		String login = "";
		DataResponse dr = AON.getDataResponse(domainName, domainId, login, DataResponseSource.PATURPAT_QUALITY, f -> f.getIdProperty().eq(drId));
		
		HashMap<String, String> map = new HashMap<>();
		net.aonsolutions.aon.gwt.udapa.shared.quality.paturpat.QualitySheetCode.valueLinkedList().stream()
		.forEach(key -> map.put(key, "0.0"));
		map.put(net.aonsolutions.aon.gwt.udapa.shared.quality.paturpat.QualitySheetCode.PFQO.getName(), "");
		
		AON.getDataResponseDetailStream(domainName, domainId, login, 
				f -> f.getDataResponseProperty().eq(drId))
		.forEach(drd -> {
			map.put(drd.getDataVariable(), drd.getDataValue());
		});
					
		ElaborationDetail ed = AON.getFullElaborationDetail(domainName, domainId, login, dr.getSourceId());
		map.put("warehouse", ed.getWarehouse().getName());
		map.put("product_description", "A");
		map.put("product_quantity", "B");
		map.put("product_supplier", "D");
		map.put("full_address", "E");
		map.put("end_address", "F");

		return map;
	}

	
	public HashMap<String, String> updateValue(String domainName, Integer domainId, Integer drId, String code, String value, HashMap<String, String> map){
		String login = "";
		DataResponseDetail drd = new DataResponseDetail();
		drd.setDomain(domainId);
		drd.setDataResponse(drId);
		drd.setDataVariable(code);
		drd.setDataValue(value);
		Optional<DataResponseDetail> opt = AON.getDataResponseDetail(domainName, domainId, login, f ->
			f.getDomainProperty().eq(domainId).and(f.getDataVariableProperty().eq(code))
			.and(f.getDataResponseProperty().eq(drId)));
		if(opt.isPresent()){			
			AON.updateDataResponseDetail(domainName, domainId, login, drd, f -> f.getIdProperty().eq(opt.get().getId()));
		} else AON.insertDataResponseDetail(domainName, domainId, login, drd);
		map.put(code, value);

		if(QualitySheetCode.UFQC2.getName().equals(code)) {
			AON.insertApplicationParameter(domainName, domainId, login, AppParam.QUALITY_PFONDO, value);
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
		ctx.put("PROPACO", isPropaco(map));
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
	
	private static Boolean isPropaco(HashMap<String, String> map) {
		return map.containsKey(QualitySheetCode.UFQDP1.getName()) && 
			(map.get(QualitySheetCode.UFQDP1.getName()).equals(Integer.toString(Destiny.BASERRI.ordinal() + 1))
			|| map.get(QualitySheetCode.UFQDP1.getName()).equals(Integer.toString(Destiny.EUSKOLABEL.ordinal() + 1)));
	}
	
	public void updateIncomeDetail(String domainName, Integer domainId, Double price, Double quantity, Integer incomeDetailId) {
		Optional<IncomeDetail> incomeDetail = AON.getIncomeDetail(domainName, domainId, "", f -> f.getIdProperty().eq(incomeDetailId));
		if(incomeDetail.isPresent()) {
			incomeDetail.get().setQuantity(quantity);
			incomeDetail.get().setPrice(price);
			incomeDetail.get().setDiscountExpression("0");
			AON.updateIncomeDetail(domainName, domainId, "", incomeDetail.get());
		}
	}

}