package com.esferalia.aon.gwt.fiscal.server.product;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.product.ItemTariff;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductParams;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.tariff.TariffParams;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.ProductType;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(name = "Tariff Catalogue (Excel)", urlPatterns = { "/ms/api/tariffCatalogue/*"})
public class TariffCatalogueExcelServlet extends HttpServlet {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		try {
			
			String domainName = req.getParameter( IRequestParamsNames.DOMAIN_NAME);
			Integer domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			String user = req.getParameter( IRequestParamsNames.USER);
			
			DomainType domainTypeParam;
			try {  domainTypeParam = DomainType.safeValueOf(Byte.parseByte(req.getParameter("domainType"))); } 
			catch (Exception e) { domainTypeParam = null; }
			
			ArrayList<DomainType> domainTypeList = new ArrayList<DomainType>();
			
			if(null != domainTypeParam) domainTypeList.add(domainTypeParam);
			else domainTypeList.addAll(DomainType.getValues());
			
			TariffParams tariffParams = new TariffParams()
					.setDomainName(domainName)
					.setDomain(domainId)
					.setUser(user)
					.setOffset(0)
					.setLimit(Integer.MAX_VALUE)
					;
			
			List<Tariff> tariffs = AON.getTariffList(new Domain().setName(domainName).setId(domainId), user, tariffParams);
			
			TariffCatalogueExcelAction action = new TariffCatalogueExcelAction();
			action.setTariffs(tariffs);
			
			domainTypeList.forEach(domainType -> {
				ProductParams params = new ProductParams()
						.setDomainName(domainName)
						.setDomain(domainId)
						.setUser(user)
						.setType(ProductType.AUXILIARY)
						.setDomainType(domainType)
						.setOffset(0)
						.setLimit(Integer.MAX_VALUE)
						;
				
				LinkedList<Product> products = AON.getProductList(new Domain().setName(domainName).setId(domainId), user, params);
				
				List<ProductTariffsEntryExcel> productTariffs = new ArrayList<ProductTariffsEntryExcel>();
				
				products.forEach(product -> {
					List<ItemTariff> itemTariffs = AON.getItemTariffStream(
								new Domain().setName(domainName).setId(domainId), 
								user, f -> 
								f.getDomainProperty().eq(domainId).and(f.getItemProperty().eq(product.getItem().getId())))
							.collect(Collectors.toList());
					
					TreeMap<String, Double> tariffsMap = new TreeMap<String, Double>();
					
					tariffs.forEach(tariff -> {
						Optional<ItemTariff> itemTariffOpt = itemTariffs.stream().filter(itemTariff -> itemTariff.getTariff().getId().equals(tariff.getId())).findFirst();
						tariffsMap.put(tariff.getCode(), getNeto(itemTariffOpt.isEmpty() ? tariff.getDiscount() : itemTariffOpt.get().getProfitPercent(), product.getItem().getPrice()) );
					});
					
					productTariffs.add(new ProductTariffsEntryExcel()
						.setCode(product.getCode())
						.setDescription(product.getName())
						.setType(null == product.getComposition() ? "" : (product.getComposition() ? "Pack" : "Servicio"))
						.setPrice(product.getItem().getPrice())
						.setTariffs(tariffsMap)
					);
				});
				
				if(productTariffs.isEmpty()) {
					productTariffs.add(new ProductTariffsEntryExcel()
							.setDescription("No existen productos para este tipo de dominio")
							.setType("")
							.setTariffs(new TreeMap<String, Double>())
							);
				}
				
				productTariffs.sort(Comparator.comparing( ProductTariffsEntryExcel::getType ));
				
				action.createSheet(domainType.getName().toUpperCase());
				action.headerRow();
				productTariffs.forEach(action);
			});
			
			

			String fileName = "Tarifas_" + (null != domainTypeParam ? (domainTypeParam.getName() + "_") : "") + DATE_FORMAT.format(new Date()) ;
			resp.setContentType(MimeType.MS_EXCEL_2007.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\""+ fileName + ".xlsx\";");
			action.finalize(resp.getOutputStream());
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		} 
	}
	
	private double getNeto(double percent, double price) {
		return price - (price * percent / 100);
	}

}
