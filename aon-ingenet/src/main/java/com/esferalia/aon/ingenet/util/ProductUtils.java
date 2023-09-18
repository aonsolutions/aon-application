package com.esferalia.aon.ingenet.util;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.common.AonException;
import com.esferalia.aon.ingenet.api.albaranes.DATOSDIRECCIONTYPE;
import com.esferalia.aon.ingenet.api.albaranes.PRODUCTOTYPE;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.RegistryAddressProperties;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductKind;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ProductUtils {

    private ProductUtils() {
   
    }
    
    public static Item obtainItem(AONContext ctx, PRODUCTOTYPE productoelaborado) throws AonException { 
        Product product = ProductDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
                .and(f.getCodeProperty().eq(productoelaborado.getCODIGO())));
        if(product.getId() == null) {
            Item it = ItemDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
                .and(f.getBarcodeProperty().eq(productoelaborado.getCODIGOBARRAS())));
            product = it.getProduct();
        }

        Integer productId = product.getId();
        Item item = ItemDAO.get(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
                .and(f.getProductProperty().eq(productId))
                .and(StringUtils.isNotBlank(productoelaborado.getNUMEROLOTESERIE())
                        ? f.getSerialNumberProperty().eq(productoelaborado.getNUMEROLOTESERIE())
                        : f.getSerialNumberProperty().isNull()));
        
        if(item.isEmpty()) {
            item = createItem(ctx, product, productoelaborado);
        }
        return item;
    }
    
    public static Item createItem(AONContext ctx, Product product, PRODUCTOTYPE producttype) throws AonException {
        Item baseItem = ItemDAO.get(ctx,
                        f -> f.getDomainProperty()
                                .eq(ctx.getDomainId())
                                .and(f.getProductProperty().eq(
                                        product.getId()))
                                .and(f.getSerialDateProperty().isNull())
                                .and(f.getSerialNumberProperty()
                                        .isNull()));
        Item item = new Item()
                .setDomain(baseItem.getDomain())
                .setProduct(baseItem.getProduct())
                .setBarcode(null)
                .setDescription(producttype.getDESCRIPCION())
                .setDetail(producttype.getDETALLE())
                .setDetail2(producttype.getDETALLE2())
                .setDetail3(producttype.getDETALLE3())
                .setPackFormatTag(baseItem.getPackFormatTag())
                .setPackMeasurement(baseItem.getPackMeasurement())
                .setPackMeasurementTag(baseItem.getPackMeasurementTag())
                .setPackUnits(baseItem.getPackUnits())
                .setPackUnitsTag(baseItem.getPackUnitsTag())
                .setStockUnitTag(baseItem.getStockUnitTag());
        
        if(producttype.getFECHALOTESERIE()!=null){
            Date serialDate = AonDateUtils.parse(producttype.getFECHALOTESERIE(), "yyyyMMdd");
            item.setSerialDate(serialDate != null ? serialDate : new Date());
            if(product.isPerishable()) {
            	Date expireDate = AonDateUtils.addDays(item.getSerialDate(),
            			product.getDaysToExpire() != null ? product.getDaysToExpire() : 0);
            	item.setExpireDate(expireDate);
            }
        }
        if(producttype.getNUMEROLOTESERIE()!=null){
            item.setSerialNumber(producttype.getNUMEROLOTESERIE());
        }
        item.setStatus(ProductStatus.DISCONTINUED);
        return ItemDAO.save(ctx, item);
    }

    public static Item createPackage(AONContext ctx, PRODUCTOTYPE productotype) throws AonException {
        Product product = ProductDAO.get(ctx, f -> f.getDomainProperty()
                                .eq(ctx.getDomainId())
                                .and(f.getCodeProperty().eq(
                                        productotype.getCODIGO())));
    
        if (product == null || product.getId() == null) {
            product = new Product();
            product.setDomain(new Domain().setId(ctx.getDomainId()));
            product.setStatus(ProductStatus.ACTIVE);
            product.setLotable(Boolean.FALSE);
            product.setSerializable(Boolean.FALSE);
            product.setPackaged(Boolean.FALSE);
            product.setInventoriable(Boolean.TRUE);
            product.setCode(productotype.getCODIGO());
            product.setName("ENVASE AUTOGENERADO ("+productotype.getCODIGO()+")");
            product.setType(ProductType.AUXILIARY);
            product.setKind(ProductKind.SALE_PURCHASE);
            product.setVat(new Tax().setId(obtainDefaultVat(ctx)));
            product.setCreationUser(ctx.getUser());
            product.setCreationDate(new Date());
            product = ProductDAO.save(ctx, product);
        }
        
        return createItem(ctx, product, productotype);
    }
    
    public static int obtainDefaultVat(AONContext ctx) throws AonException {
        ApplicationParameter ap = AON.getApplicationParameter(ctx.getDomainName(),
                ctx.getDomainId(),
                ctx.getUser(),
                AppParam.ACC_DEFAULT_VAT_PERCENT.name());
        if(ap!=null && ap.getValue()!=null && NumberUtils.isNumber(ap.getValue())) {
            return Integer.valueOf(ap.getValue());
        } else {
            Tax tax = AON.getTaxStream(ctx.getDomainName(),
                    ctx.getDomainId(),
                    ctx.getUser(),
                    f -> f.getDomainProperty().eq(ctx.getDomainId())
                    .and(f.getTaxTypeProperty().eq(TaxType.VAT.value()))
                    ).sorted((o1, o2) -> o1.getId().compareTo(o2.getId()))
                    .findFirst().orElse(new Tax());
            return tax.getId();
        }
    }
    
    public static RegistryAddress obtainAddress(AONContext ctx, Customer customer, DATOSDIRECCIONTYPE datosdireccionentrega) {
        return RegistryAddressDAO.get(ctx, f -> addressFilter(ctx, customer, datosdireccionentrega, f));
    }
    
    private static Filter addressFilter(AONContext ctx, Customer customer, DATOSDIRECCIONTYPE datosdireccionentrega, RegistryAddressProperties f) {
        Filter filter = f.getDomainProperty().eq(ctx.getDomainId())
                .and(f.getRegistryProperty().eq(customer.getId()));
        
        if(!AonStringUtils.isBlank(datosdireccionentrega.getCIUDAD())) {
            filter = filter.and(f.getCityProperty().eq(datosdireccionentrega.getCIUDAD()));
        }
        
        if(!AonStringUtils.isBlank(datosdireccionentrega.getCODIGOPOSTAL())) {
            filter = filter.and(f.getZipProperty().eq(datosdireccionentrega.getCODIGOPOSTAL()));
        }
        return filter;
    }
    
    public static boolean isEroski(String document) {
        return "F20033361".equalsIgnoreCase(document)
                || "B88512975".equalsIgnoreCase(document)
                || "A08115032".equalsIgnoreCase(document);
   }
}
