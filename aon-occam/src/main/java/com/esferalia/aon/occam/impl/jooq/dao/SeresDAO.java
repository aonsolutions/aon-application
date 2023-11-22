package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.occam.api.model.seres.IEdiSupport.ALBARANES;
import static com.esferalia.aon.occam.api.model.seres.IEdiSupport.CABECERA;
import static com.esferalia.aon.occam.api.model.seres.IEdiSupport.DEPARTMENT;
import static com.esferalia.aon.occam.api.model.seres.IEdiSupport.EDI_CODES_PATTERN;
import static com.esferalia.aon.occam.api.model.seres.IEdiSupport.EDI_DEPARTMENT_PATTERN;
import static com.esferalia.aon.occam.api.model.seres.IEdiSupport.EDI_PACKING_PATTERN;
import static com.esferalia.aon.occam.api.model.seres.IEdiSupport.FACTURA;
import static com.esferalia.aon.occam.api.model.seres.IEdiSupport.FINANCIERA;
import static com.esferalia.aon.occam.api.model.seres.IEdiSupport.MEDIDA;
import static com.esferalia.aon.occam.api.model.seres.IEdiSupport.MEDIDA_FACTURA;
import static com.esferalia.aon.occam.api.model.seres.IEdiSupport.PEDIDOS;
import static com.esferalia.aon.occam.api.model.seres.IEdiSupport.PTO_ENTREGA;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.seres.EdiCodes;
import com.esferalia.aon.occam.api.model.seres.IEdiSupport;
import com.esferalia.aon.occam.api.model.seres.SeresInfo;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;

public class SeresDAO {
	
	private SeresDAO() {

	}
	
	public static SeresInfo getSeresInfo(AONContext ctx) {
		ctx.checkRead();
		SeresInfo seresInfo = new SeresInfo();

		AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getNameProperty().like("SERES_%")))
		.forEach(r -> {
			if(r.getName().equalsIgnoreCase(AppParam.SERES_FTP_SERVER_NAME.toString())) {
				seresInfo.setServer(r.getValue());
			}
			
			if(r.getName().equalsIgnoreCase(AppParam.SERES_FTP_USER.toString())) {
				seresInfo.setUser(r.getValue());
			}
			
			if(r.getName().equalsIgnoreCase(AppParam.SERES_FTP_PASSWORD.toString())) {
				seresInfo.setPassword(r.getValue());
			}
			
			if(r.getName().equalsIgnoreCase(AppParam.SERES_FTP_PORT.toString())) {
				seresInfo.setPort(Integer.parseInt(r.getValue()));
			}
			
		});

		return seresInfo;
	}
	
	public static EdiCodes getEdiCodes(AONContext ctx, Invoice invoice) {
	    Integer customerId = invoice.getRegistry();
        Integer addressId = invoice.getAddress().getId();
        Map<String, String> ediCodes = obtainEdiCodes(ctx, customerId, addressId);  
        
        return new EdiCodes()
                .setDepartment(ediCodes.get(IEdiSupport.DEPARTMENT))
                .setCustomerEdiHeader(ediCodes.get(IEdiSupport.CABECERA))
                .setCustomerEdiInvoice(ediCodes.get(IEdiSupport.FACTURA))
                .setCustomerEdiPoint(ediCodes.get(IEdiSupport.PTO_ENTREGA))
                .setCustomerPackage(obtainPackingTag(ctx, customerId, addressId))
                .setCustomerInvoicePackage(obtainInvoicePackingTag(ctx, customerId, addressId))
                .setCompanyEdiCode(obtainEdiCompanyCode(ctx));
	}
	
	public static EdiCodes getEdiCodes(AONContext ctx, Delivery delivery) {
		Integer customerId = delivery.getCustomer().getId();
		Integer addressId = delivery.getAddress().getId();
		Map<String, String> ediCodes = obtainEdiCodes(ctx, customerId, addressId);	

		EdiCodes codes = new EdiCodes()
			.setDepartment(ediCodes.get(IEdiSupport.DEPARTMENT))
			.setCustomerEdiCode(ediCodes.get(IEdiSupport.ALBARANES))
			.setDeliveryPointEdiCode(ediCodes.get(IEdiSupport.PTO_ENTREGA))
			.setCustomerPackage(obtainPackingTag(ctx, customerId, addressId))
			.setCompanyEdiCode(obtainEdiCompanyCode(ctx));
		
		Integer[] array = delivery.getDetails().stream().map(DeliveryDetail::getSalesDetail).toArray(Integer[]::new);
		Options options = new Options().setFull(true);
		Integer[] salesArray = SalesDAO.getStream(ctx, f -> f.getDomainProperty().eq(delivery.getDomain())
				.and(f.getSalesDetailIdProperty().in(array)), options)
		.map(Sales::getId).toArray(Integer[]::new);
		
		Integer[] drArray = DataResponseDAO.getStream(ctx, f -> f.getSourceProperty().eq(DataResponseSource.SERES_SALES.value())
				.and(f.getSourceIdProperty().in(salesArray)))
			.map(DataResponse::getId).toArray(Integer[]::new);

		
		DataResponseDAO.getDataResponseDetailStream(ctx, f -> f.getDataResponseProperty().in(drArray))
		.forEach( drd -> {
			if("MS".equalsIgnoreCase(drd.getDataVariable())) {
				codes.setMrcode(drd.getDataValue());
			} else if("DP".equalsIgnoreCase(drd.getDataVariable())) {
				codes.setDpcode(drd.getDataValue());
			} else if("UC".equalsIgnoreCase(drd.getDataVariable())) {
				codes.setUccode(drd.getDataValue());
			} else if("BY".equalsIgnoreCase(drd.getDataVariable())) {
				codes.setBycode(drd.getDataValue());
			} else if("IV".equalsIgnoreCase(drd.getDataVariable())) {
				codes.setIvcode(drd.getDataValue());
			}
		});

		return codes;
	}
	
	public static Map<String, String> obtainEdiCodes(AONContext ctx, Integer registryId, Integer rAddressId) {
		String value = getRegistryNoteComments(ctx, rAddressId.toString(), registryId);
		Map<String, String> values = new HashMap<>();
		Matcher m;
		Pattern p = Pattern.compile(EDI_CODES_PATTERN + EDI_PACKING_PATTERN + EDI_DEPARTMENT_PATTERN);
		if (value != null && (m = p.matcher(value)).find()) {
			values.put(CABECERA, m.groupCount() > 0 ? m.group(1) : null);
			values.put(PEDIDOS, m.groupCount() > 1 ? m.group(2) : null);
			values.put(PTO_ENTREGA, m.groupCount() > 2 ? m.group(3) : null);
			values.put(FACTURA, m.groupCount() > 3 ? m.group(4) : null);
			values.put(FINANCIERA, m.groupCount() > 4 ? m.group(5) : null);
			values.put(ALBARANES, m.groupCount() > 5 ? m.group(6) : null);
			values.put(MEDIDA, m.groupCount() > 6 ? m.group(7) : null);
			values.put(MEDIDA_FACTURA, m.groupCount() > 7 ? m.group(8) : null);
			values.put(DEPARTMENT, m.groupCount() > 8 ? m.group(9) : null);
		}
		return values;
	}
	
	public static String obtainPackingTag(AONContext ctx, Integer registryId, Integer rAddressId) {
		String value = getRegistryNoteComments(ctx, rAddressId.toString(), registryId);
		Matcher m;
		Pattern p = Pattern.compile(MEDIDA + "=([^;]*);");
		try {
			if (value != null && (m = p.matcher(value)).find()) {
				Integer tagId = Integer.valueOf(m.group(1));
				com.esferalia.aon.occam.api.model.office.Tag tag = TagDAO.getTag(ctx, tagId);
				return tag!=null ? tag.getName() : "";
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String obtainInvoicePackingTag(AONContext ctx, Integer registryId, Integer rAddressId) {
		String value = getRegistryNoteComments(ctx, rAddressId.toString(), registryId);
		Matcher m;
		Pattern p = Pattern.compile(MEDIDA_FACTURA + "=([^;]*);");
		try {
			if (value != null && (m = p.matcher(value)).find()) {
				Integer tagId = Integer.valueOf(m.group(1));
				com.esferalia.aon.occam.api.model.office.Tag tag = TagDAO.getTag(ctx, tagId);
				return tag!=null ? tag.getName() : "";
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String getRegistryNoteComments(AONContext ctx, String key, Integer registryId) {
		return RegistryNoteDAO.getStream(ctx, f -> f.getNoteTypeProperty().eq(NoteType.FACTURAE.value())
						.and(f.getRegistryProperty().eq(registryId))
						.and(f.getDescriptionProperty().eq(key)))
			.findFirst().orElse(new RegistryNote())
			.getComments();		
	}
	
	private static String obtainEdiCompanyCode(AONContext ctx) {
		ApplicationParameter param = AppParamDAO.getApplicationParameterStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getNameProperty().eq(AppParam.EDI_COMPANY_CODE.getValue()))).findFirst().orElse(null);
		return param !=null ? param.getValue() : null;
	}
}




