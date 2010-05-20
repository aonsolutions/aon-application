package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.cms.Brand;
import com.code.aon.cms.BrandDetail;
import com.code.aon.cms.ProductCategory;
import com.code.aon.cms.ProductCategoryConfig;
import com.code.aon.cms.ProductCategoryDetail;
import com.code.aon.cms.ProductDetail;
import com.code.aon.cms.Section;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ui.cms.controller.GeneratorConfigController;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.BrandHandler;
import com.code.aon.ui.cms.velocity.attribute.ProductCategoryHandler;
import com.code.aon.ui.cms.velocity.attribute.ProductHandler;

public class ProductGenerator extends Generator {

	private final static Logger LOGGER = LoggerFactory.getLogger(ProductGenerator.class);
	
	public static final String MAIN_PAGE = "main_page"; 

	public void generate() {
		List<ProductCategoryDetail> productCategoryDetailList;
		List<ProductCategoryDetail> productSubCategoryDetailList;
		List<BrandDetail> brandDetailList;
		try {
			String mainPageURL = getMainPageUrl();
			
			String stmtPC = "SELECT pcd FROM ProductCategory pc,ProductCategoryDetail pcd " +
					"WHERE pcd.productCategory.id = pc.id " +
					"AND pc.parent is null "+
					"AND pcd.language.id = ? " +
					"AND pc.active = ?";
			Query queryPC = HibernateUtil.getSession().createQuery(stmtPC);
			queryPC.setInteger(0, ControllerUtil.getCurrentLanguage().getId());
			queryPC.setBoolean(1, Boolean.TRUE);
			productCategoryDetailList = queryPC.list();

			List<ProductCategoryHandler> listCategoryHandler = new ArrayList<ProductCategoryHandler>();

			for( ProductCategoryDetail productCategoryDetail : productCategoryDetailList ) {

				ProductCategoryHandler categoryHandler = new ProductCategoryHandler(productCategoryDetail);

				listCategoryHandler.add(categoryHandler);
				
				VelocityUtil vu = context.initVelocityUtil();
				vu.put("mainPageURL", mainPageURL);

				String stmtPSC = "SELECT pcd FROM ProductCategory pc,ProductCategoryDetail pcd " +
					"WHERE pcd.productCategory.id = pc.id " +
					"AND pc.parent.id = ? "+
					"AND pcd.language.id = ? "+
					"AND pc.active = ?";
				Query queryPSC = HibernateUtil.getSession().createQuery(stmtPSC);
				queryPSC.setInteger(0, productCategoryDetail.getProductCategory().getId());
				queryPSC.setInteger(1, ControllerUtil.getCurrentLanguage().getId());
				queryPSC.setBoolean(2, Boolean.TRUE);
				productSubCategoryDetailList = queryPSC.list();

				chargeProductCategoryContext(vu, productCategoryDetail.getProductCategory());

				List<ProductCategoryHandler> listSubCategoryHandler = new ArrayList<ProductCategoryHandler>();
				
				for (Iterator<ProductCategoryDetail> iteratorPSC = productSubCategoryDetailList.iterator(); iteratorPSC.hasNext();) {
					ProductCategoryDetail productSubCategoryDetail = iteratorPSC.next();
					chargeProductSubCategoryContext(vu, productSubCategoryDetail.getProductCategory());
					List<ProductHandler> listProductHandler = new ArrayList<ProductHandler>();
					listProductHandler = getProducts(productSubCategoryDetail.getProductCategory(),vu,true);
					
					ProductCategoryHandler subCategoryHandler = new ProductCategoryHandler(productSubCategoryDetail);
					listSubCategoryHandler.add(subCategoryHandler);
					
					vu.put("category", categoryHandler);
					vu.put("subCategory", subCategoryHandler);
					vu.put("productList", listProductHandler);
					logger.info(" Generando Subcategoria " + productSubCategoryDetail.getProductCategory().getAlias() + ".");
					generate(vu, Templates.PRODUCT_CATEGORY, productSubCategoryDetail.getProductCategory().getAlias());
					vu.remove("productList");
					vu.remove("subCategory");
					vu.remove("category");
					
					listProductHandler = null;
				}

				vu.put("category", categoryHandler);
				vu.put("subCategoryList", listSubCategoryHandler);
				logger.info(" Generando Categoria " + productCategoryDetail.getProductCategory().getAlias() + ".");
				generate(vu, Templates.PRODUCT_CATEGORY, productCategoryDetail.getProductCategory().getAlias());
				vu.remove("subCategoryList");
				vu.remove("category");

				listSubCategoryHandler = null;

				vu.remove("mainPageURL");	
			}
			
			VelocityUtil vu = context.initVelocityUtil();
			chargeProductCategoryContext(vu, null);
			vu.put("mainPageURL", mainPageURL);
			vu.put("mainCategoryList", listCategoryHandler);
			logger.info(" Generando category main.");
			generate(vu, Templates.PRODUCT_CATEGORY, ProductGenerator.MAIN_PAGE);
			vu.remove("mainCategoryList");
			vu.remove("mainPageURL");
			
			listCategoryHandler = null;

			mainPageURL = getBrandMainPageUrl();

			String stmtB = "SELECT bd FROM Brand b,BrandDetail bd " +
					"WHERE bd.brand.id = b.id " +
					"AND bd.language.id = ? " +
					"AND b.active = ?";
			Query queryB = HibernateUtil.getSession().createQuery(stmtB);
			queryB.setInteger(0, ControllerUtil.getCurrentLanguage().getId());
			queryB.setBoolean(1, Boolean.TRUE);
			brandDetailList = queryB.list();

			List<BrandHandler> listBrandHandler = new ArrayList<BrandHandler>();

			vu = context.initVelocityUtil();
			chargeProductCategoryContext(vu, null);
			vu.put("mainPageURL", mainPageURL);
			
			for (Iterator<BrandDetail> iteratorB = brandDetailList.iterator(); iteratorB.hasNext();) {
				BrandDetail brandDetail = iteratorB.next();
				
				BrandHandler brandHandler = new BrandHandler(brandDetail);
				listBrandHandler.add(brandHandler);
				
				List<ProductHandler> listProductHandler = new ArrayList<ProductHandler>();
				listProductHandler = getProducts(brandDetail.getBrand(),vu,false);

				vu.put("brand", brandHandler);
				vu.put("products", listProductHandler);
				logger.info(" Generando marca "+brandDetail.getBrand().getAlias()+".");
				generate(vu, Templates.BRAND, brandDetail.getBrand().getAlias());
				vu.remove("products");
				vu.remove("brand");
				
				listProductHandler = null;
			}

			vu.put("brandList", listBrandHandler);
			logger.info(" Generando marcas.");
			generate(vu, Templates.BRAND, ProductGenerator.MAIN_PAGE);
			vu.remove("brandList");

			vu.remove("mainPageURL");

		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			logger.error(th.getMessage());
		} finally {
			productCategoryDetailList = null;
			productSubCategoryDetailList = null;
		}
	}
	
	private String getMainPageUrl(){
		String url = Templates.PRODUCT_CATEGORY.getHtmlName();
		url = url.replaceAll("%NAME%", ProductGenerator.MAIN_PAGE);
		return url;
	}

	private String getBrandMainPageUrl(){
		String url = Templates.BRAND.getHtmlName();
		url = url.replaceAll("%NAME%", ProductGenerator.MAIN_PAGE);
		return url;
	}

	private List<ProductHandler> getProducts(ProductCategory category,VelocityUtil vu,boolean generate) throws ManagerBeanException{
		List<ProductHandler> list = new ArrayList<ProductHandler>();

		String stmtP = "SELECT pd FROM Product p,ProductDetail pd " +
			"WHERE pd.product.id = p.id " +
			"AND p.productCategory.id = ? "+
			"AND pd.language.id = ? " +
			"AND p.active = ?";
		Query queryP = HibernateUtil.getSession().createQuery(stmtP);
		queryP.setInteger(0, category.getId());
		queryP.setInteger(1, ControllerUtil.getCurrentLanguage().getId());
		queryP.setBoolean(2, Boolean.TRUE);
		List<ProductDetail> productDetailList = queryP.list();
	
		for (Iterator<ProductDetail> iterator = productDetailList.iterator(); iterator.hasNext();) {
			ProductDetail productDetail = iterator.next();
			
			ProductHandler productHandler = new ProductHandler(productDetail);
			
			if (generate){
				vu.put("product", productHandler);
				logger.info(" Generando producto " + productDetail.getProduct().getAlias() + ".");
				generate(vu, Templates.PRODUCT, productDetail.getProduct().getAlias());
				vu.remove("product");
			}
			
			list.add(productHandler);
		}
		productDetailList = null;
		return list;
	}

	private List<ProductHandler> getProducts(Brand brand,VelocityUtil vu, boolean generate) throws ManagerBeanException{
		List<ProductHandler> list = new ArrayList<ProductHandler>();

		String stmtP = "SELECT pd FROM Product p,ProductDetail pd " +
			"WHERE pd.product.id = p.id " +
			"AND p.brand.id = ? "+
			"AND pd.language.id = ? " +
			"AND p.active = ?";
		Query queryP = HibernateUtil.getSession().createQuery(stmtP);
		queryP.setInteger(0, brand.getId());
		queryP.setInteger(1, ControllerUtil.getCurrentLanguage().getId());
		queryP.setBoolean(2, Boolean.TRUE);
		List<ProductDetail> productDetailList = queryP.list();
	
		for (Iterator<ProductDetail> iterator = productDetailList.iterator(); iterator.hasNext();) {
			ProductDetail productDetail = iterator.next();
			
			ProductHandler productHandler = new ProductHandler(productDetail);
			if (generate){
				vu.put("product", productHandler);
				logger.info(" Generando producto " + productDetail.getProduct().getAlias() + ".");
				generate(vu, Templates.PRODUCT, productDetail.getProduct().getAlias());
				vu.remove("product");
			}
			
			list.add(productHandler);
		}
		productDetailList = null;
		return list;
	}

	private void chargeProductCategoryContext(VelocityUtil vu, ProductCategory productCategory) throws ManagerBeanException{
		if (productCategory!=null && productCategory.getSection()!=null){
			context.changeSection(vu, productCategory.getSection());
		}else{
			Section configSection = GeneratorConfigController.currentSection(ProductCategoryConfig.class);
			context.changeSection(vu, configSection);
		}
	}

	private void chargeProductSubCategoryContext(VelocityUtil vu, ProductCategory productCategory) throws ManagerBeanException{
		if (productCategory.getSection()!=null)
			context.changeSection(vu, productCategory.getSection());
	}


}
