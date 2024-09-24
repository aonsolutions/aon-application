package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import static com.esferalia.aon.jooq.tables.Brand.BRAND;
import static com.esferalia.aon.jooq.tables.InvestAsset.INVEST_ASSET;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.InvoiceTaxAccount.INVOICE_TAX_ACCOUNT;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.Warehouse.WAREHOUSE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.SelectOnConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceSource.IInvoiceSourceVisitor;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.InvestAssetDAO.InvestAssetFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO.ItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SellerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SellerDAO.SellerFiller;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO.WarehouseFiller;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO.WorkplaceFiller;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.AccountingInvoiceDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

class InvoiceDetailDAO {

   
    private InvoiceDetailDAO() {
     
    }

	private static SelectOnConditionStep<Record> select(AONContext ctx){  
	  return ctx.getDslContext()
          .select()
          .from(INVOICE_DETAIL)
          .leftOuterJoin(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
          .leftOuterJoin(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
          .leftOuterJoin(PROJECT).on(INVOICE_DETAIL.PROJECT.eq(PROJECT.ID))
          .leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.eq(PCATEGORY.ID))
          .leftOuterJoin(BRAND).on(PRODUCT.BRAND.eq(BRAND.ID))
          .leftOuterJoin(SELLER).on(SELLER.REGISTRY.eq(INVOICE_DETAIL.SELLER))
          .leftOuterJoin(SellerDAO.SELLER_ALIAS).on(SellerDAO.SELLER_ALIAS.ID.eq(INVOICE_DETAIL.SELLER))
          .leftOuterJoin(WAREHOUSE).on(WAREHOUSE.ID.eq(INVOICE_DETAIL.WAREHOUSE))
          .leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.eq(INVOICE_DETAIL.WORKPLACE))
          .leftOuterJoin(INVEST_ASSET).on(INVEST_ASSET.ID.eq(INVOICE_DETAIL.INVEST_ASSET))
          ;
	}

    private static Stream<InvoiceDetail> basicStream(AONContext ctx, Integer invoiceId) {
		return select(ctx)
			.where(INVOICE_DETAIL.INVOICE.eq(invoiceId))
			.orderBy(INVOICE_DETAIL.LINE)
			.fetch()
			.stream()
			.map(new InvoiceDetailFiller())
			.map(d -> d.setExpAccount(AccountingInvoiceDAO.getInvoiceDetailAccount(ctx, d.getId()).orElse(null)));		
	}

    static Stream<InvoiceDetail> stream(AONContext ctx, Integer invoiceId) {
		return basicStream(ctx, invoiceId)
			.map(id -> id.setInvoiceTaxes( InvoiceTaxDAO.list(ctx, id.getId())));
	}    
    
	static Optional<InvoiceDetail> get(AONContext ctx, Integer detailId) {
		return select(ctx)
			.where(INVOICE_DETAIL.ID.eq(detailId))
			.fetch()
			.stream()
			.map(new InvoiceDetailFiller())
			.findFirst();
	}
	
	private static class InvoiceDetailFiller extends Filler implements Function<Record, InvoiceDetail> {

		@Override
		public InvoiceDetail apply(Record r) {
			return build(r);
		}
		
		public static InvoiceDetail build(Record r) {
			return new InvoiceDetail()
				.setId(getValue(r, INVOICE_DETAIL.ID))
				.setDomain(getValue(r, INVOICE_DETAIL.DOMAIN))
				.setInvoice(getValue(r,INVOICE_DETAIL.INVOICE))
				.setProject(getValue(r, INVOICE_DETAIL.PROJECT))
				.setInvestAsset( getOpt(r, INVEST_ASSET.ID).map(i -> InvestAssetFiller.build(r)).orElse(null))
				.setSeller( getOpt(r, SELLER.REGISTRY).map(i -> SellerFiller.build(r, SellerDAO.SELLER_ALIAS)).orElse(null))
				.setItem( getOpt(r, ITEM.ID).map( i -> ItemFiller.build(r) ).orElse(null))
				.setLine(getValue(r, INVOICE_DETAIL.LINE))
				.setDescription(getValue(r, INVOICE_DETAIL.DESCRIPTION ))
				.setQuantity(getValue(r, INVOICE_DETAIL.QUANTITY))
				.setPrice(getValue(r, INVOICE_DETAIL.PRICE))
				.setDiscountExpression(getValue(r, INVOICE_DETAIL.DISCOUNT_EXPR))
				.setTaxableBase(getValue(r, INVOICE_DETAIL.TAXABLE_BASE))
				.setTaxes(getValue(r, INVOICE_DETAIL.TAXES))
				.setPrepayment(getBoolean(r, INVOICE_DETAIL.PREPAYMENT))
				.setWorkplace(getOpt(r, WORKPLACE.ID).map(w -> WorkplaceFiller.build(r)).orElse(null))
				.setWarehouse(getOpt(r, WAREHOUSE.ID).map(w -> WarehouseFiller.build(r)).orElse(null))
				.setSource(InvoiceSource.safeValueOf(getValue(r, INVOICE_DETAIL.SOURCE)))
				.setSourceId(getValue(r, INVOICE_DETAIL.SOURCE_ID))
				.setCreationDate(r.getValue(INVOICE_DETAIL.CREATION_DATE))
				.setCreationUser(r.getValue(INVOICE_DETAIL.CREATION_USER))
				.setModificationDate(r.getValue(INVOICE_DETAIL.MODIFICATION_DATE))
				.setModificationUser(r.getValue(INVOICE_DETAIL.MODIFICATION_USER));
		}
	}

	static void save(AONContext ctx, Invoice invoice) {
		// Se guardan las líneas no marcadas como borradas
		AonCollectionUtils.stream(invoice.getDetails())
			.filter(det -> !det.isDeleted())
			.forEach(det -> save(ctx,invoice, det));
		
		// Se borran las las líneas marcadas como borradas
		AonCollectionUtils.stream(invoice.getDetails())
			.filter(det -> det.isDeleted())
			.map(det -> {			
				// REAPSAR ESTE COMPORTAMIENTO!!
				// Si está marcado como borrado, porque el numero negativo???
				if (AonMathUtils.isLessThanZero(det.getId()) ) {
					det.setId(det.getId() * -1);
				}
				return det;
			})
			.forEach(det -> deleteDetail(ctx, det));
	}

	static InvoiceDetail save(AONContext ctx, Invoice invoice, InvoiceDetail invoiceDetail) {
		invoiceDetail = (invoiceDetail.getId() != null)
			? update(ctx, invoice, invoiceDetail)
			: insert(ctx, invoice, invoiceDetail);
		return invoiceDetail;
	}
	
	private static InvoiceDetail update(AONContext ctx, Invoice invoice, InvoiceDetail invoiceDetail) {
		InvoiceDetailAutoComplete.complete(ctx, invoice, invoiceDetail);
		InvoiceDetailValidation.validate(ctx, invoiceDetail);
		ctx.getDslContext().update(INVOICE_DETAIL)
			.set(INVOICE_DETAIL.DOMAIN, invoiceDetail.getDomain())
			.set(INVOICE_DETAIL.INVOICE, invoiceDetail.getInvoice())
			.set(INVOICE_DETAIL.INVEST_ASSET, invoiceDetail.getInvestAsset().map(ias -> ias.getId()).orElse(null))
			.set(INVOICE_DETAIL.PROJECT, invoiceDetail.getProject())
			.set(INVOICE_DETAIL.LINE, invoiceDetail.getLine())
			.set(INVOICE_DETAIL.ITEM, invoiceDetail.getItem()==null ? null : invoiceDetail.getItem().getId())
			.set(INVOICE_DETAIL.DESCRIPTION, invoiceDetail.getDescription())
			.set(INVOICE_DETAIL.QUANTITY, invoiceDetail.getQuantity())
			.set(INVOICE_DETAIL.PRICE, invoiceDetail.getPrice())
			.set(INVOICE_DETAIL.DISCOUNT_EXPR, invoiceDetail.getDiscountExpression().getDiscountExpr())
			.set(INVOICE_DETAIL.SOURCE, invoiceDetail.getSource().value())
			.set(INVOICE_DETAIL.SOURCE_ID, invoiceDetail.getSourceId())
			.set(INVOICE_DETAIL.TAXABLE_BASE, invoiceDetail.getTaxableBase())
			.set(INVOICE_DETAIL.TAXES, invoiceDetail.getTaxes())
			.set(INVOICE_DETAIL.PREPAYMENT, AonEnumUtils.getByte(invoiceDetail.isPrepayment())) 
			.set(INVOICE_DETAIL.SELLER, invoiceDetail.getSeller() == null ? null : invoiceDetail.getSeller().getId())
			.set(INVOICE_DETAIL.WORKPLACE, invoiceDetail.getWorkplace() == null ? null : invoiceDetail.getWorkplace().getId())
			.set(INVOICE_DETAIL.WAREHOUSE, invoiceDetail.getWarehouse() == null ? null : invoiceDetail.getWarehouse().getId())
			.set(INVOICE_DETAIL.MODIFICATION_USER ,ctx.getUser())
			.set(INVOICE_DETAIL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.where(INVOICE_DETAIL.ID.eq(invoiceDetail.getId()))
			.execute();
		InvoiceTaxDAO.save(ctx, invoice, invoiceDetail);
		return invoiceDetail;
	}


	private static InvoiceDetail insert(AONContext ctx, Invoice invoice, InvoiceDetail invoiceDetail) {
		InvoiceDetailAutoComplete.complete(ctx, invoice, invoiceDetail);
		InvoiceDetailValidation.validate(ctx, invoiceDetail);
		Integer id = ctx.getDslContext().insertInto(INVOICE_DETAIL)
			.set(INVOICE_DETAIL.DOMAIN, invoiceDetail.getDomain())
			.set(INVOICE_DETAIL.INVOICE, invoiceDetail.getInvoice())
			.set(INVOICE_DETAIL.INVEST_ASSET, invoiceDetail.getInvestAsset().map(ias -> ias.getId()).orElse(null))
			.set(INVOICE_DETAIL.PROJECT, invoiceDetail.getProject())
			.set(INVOICE_DETAIL.LINE, invoiceDetail.getLine())
			.set(INVOICE_DETAIL.ITEM, invoiceDetail.getItem()==null ? null : invoiceDetail.getItem().getId())
			.set(INVOICE_DETAIL.DESCRIPTION, invoiceDetail.getDescription())
			.set(INVOICE_DETAIL.QUANTITY, invoiceDetail.getQuantity())
			.set(INVOICE_DETAIL.PRICE, invoiceDetail.getPrice())
			.set(INVOICE_DETAIL.DISCOUNT_EXPR, invoiceDetail.getDiscountExpression().getDiscountExpr())
			.set(INVOICE_DETAIL.SOURCE, invoiceDetail.getSource().value())
			.set(INVOICE_DETAIL.SOURCE_ID, invoiceDetail.getSourceId())
			.set(INVOICE_DETAIL.TAXABLE_BASE, invoiceDetail.getTaxableBase())
			.set(INVOICE_DETAIL.TAXES, invoiceDetail.getTaxes())
			.set(INVOICE_DETAIL.PREPAYMENT, AonEnumUtils.getByte(invoiceDetail.isPrepayment())) 
			.set(INVOICE_DETAIL.SELLER, invoiceDetail.getSeller() == null ? null : invoiceDetail.getSeller().getId())
			.set(INVOICE_DETAIL.WORKPLACE, invoiceDetail.getWorkplace() == null ? null : invoiceDetail.getWorkplace().getId())
			.set(INVOICE_DETAIL.WAREHOUSE, invoiceDetail.getWarehouse() == null ? null : invoiceDetail.getWarehouse().getId())
			.set(INVOICE_DETAIL.CREATION_USER ,ctx.getUser())
			.set(INVOICE_DETAIL.CREATION_DATE, new Timestamp( System.currentTimeMillis()))
			.set(INVOICE_DETAIL.MODIFICATION_USER ,ctx.getUser())
			.set(INVOICE_DETAIL.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
			.returning(INVOICE_DETAIL.ID).fetchOne().getId();
		ctx.log().debug("\tINSERT INVOICE_DETAIL detalles invoice: {0}",invoiceDetail.getId());
		invoiceDetail.setId(id);
		InvoiceTaxDAO.save(ctx, invoice, invoiceDetail);
		afterInsert(ctx, invoice, invoiceDetail);
		return invoiceDetail;
	}

	private static void afterInsert(AONContext ctx, Invoice invoice, InvoiceDetail detail) {
		detail.getSource().visit(detail, new IInvoiceSourceVisitor() {
			
			private static final long serialVersionUID = -9008741708561768671L;
	
			@Override public void visitSales(InvoiceDetail detail) {/* nothing */ }
			@Override public void visitReservation(InvoiceDetail detail) {/* nothing */ }
			@Override public void visitPurchase(InvoiceDetail detail) {/* nothing */ }
			@Override public void visitOffer(InvoiceDetail detail) {/* nothing */ }
			@Override public void visitIncome(InvoiceDetail detail) {/* nothing */ }
			@Override public void visitFee(InvoiceDetail detail) {/* nothing */ }
			@Override public void visitDirectInvoice(InvoiceDetail detail) {/* nothing */ }
			@Override public void visitDirectExpense(InvoiceDetail detail) {/* nothing */ }
			@Override public void visitDelivery(InvoiceDetail detail) {/* nothing */ }
			
			private void saveAcountingTables(InvoiceDetail detail) {
				AccountingInvoiceDAO.saveInvoiceDetailAccount(ctx, detail);
				AonCollectionUtils.stream( detail.getInvoiceTaxes() )
					.forEach( tax -> AccountingInvoiceDAO.saveInvoiceTaxAccount(ctx,invoice, detail, tax));
			}
			
			@Override 
			public void visitAccount(InvoiceDetail detail) {
				if (detail.getExpAccount() == null || detail.getExpAccount().getId() == null) {
					throw new AonCoreException(AonError.ACCOUNT_ENTRY_NO_EXP_ACCOUNT.getMessage());
				}
				saveAcountingTables(detail);
			}
			
			@Override 
			public void visitTedi(InvoiceDetail detail) {
				if (detail.getExpAccount() != null && detail.getExpAccount().getId() != null) { 
					saveAcountingTables(detail);
				}
			}
		});
	}

	static void deleteInvoice(AONContext ctx, Integer invoice) {
		basicStream(ctx, invoice)
			.forEach(detail -> deleteDetail(ctx, detail));
	}

	static void deleteDetail(AONContext ctx, InvoiceDetail detail) {
		beforeDeleteDetail(ctx, detail);
		InvoiceTaxDAO.delete(ctx,detail);
		int count = ctx.getDslContext()
			.delete(INVOICE_DETAIL)
			.where(INVOICE_DETAIL.ID.equal(detail.getId()))
			.execute();
		ctx.log().debug("DELETE INVOICE_DETAIL detalle de la factura: {0} ({1} filas)",detail.getId(),count);
	}
	
	private static void beforeDeleteDetail(AONContext ctx, InvoiceDetail detail) {
		detail.getSource().visit(detail, new IInvoiceSourceVisitor() {
			
			private static final long serialVersionUID = -715576035920671791L;
			
			@Override public void visitSales(InvoiceDetail detail) {/* Nothing */}
			@Override public void visitReservation(InvoiceDetail detail) {/* Nothing */}
			@Override public void visitPurchase(InvoiceDetail detail) {/* Nothing */}
			@Override public void visitOffer(InvoiceDetail detail) {/* Nothing */}
			@Override public void visitIncome(InvoiceDetail detail) {/* Nothing */}
			@Override public void visitFee(InvoiceDetail detail) {/* Nothing */}
			@Override public void visitDirectInvoice(InvoiceDetail detail) {/* Nothing */}
			@Override public void visitDirectExpense(InvoiceDetail detail) {/* Nothing */}
			@Override public void visitDelivery(InvoiceDetail detail) {/* Nothing */}
			
			@Override 
			public void visitAccount(InvoiceDetail detail) {
				int count = ctx.getDslContext()
					.delete(INVOICE_DETAIL_ACCOUNT)
					.where(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.eq(detail.getId()))
					.execute();
				ctx.log().debug("DELETE INVOICE_DETAIL_ACCOUNT ({0} filas.)",count);
				
				ctx.getDslContext().select(INVOICE_TAX.ID)
					.from(INVOICE_TAX)
					.where(INVOICE_TAX.INVOICE_DETAIL.eq(detail.getId()))
					.fetch()
					.stream()
					.mapToInt(rec -> rec.getValue(INVOICE_TAX.ID))
					.forEach(id -> {
						int x = ctx.getDslContext()
								.delete(INVOICE_TAX_ACCOUNT)
								.where(INVOICE_TAX_ACCOUNT.INVOICE_TAX.eq(id))
								.execute();
						ctx.log().debug("DELETE INVOICE_TAX_ACCOUNT ({0} filas.)",x);
					});
			}
			
			@Override 
			public void visitTedi(InvoiceDetail detail) {
				visitAccount(detail);
			}
		});
	}

	// --------------------------------------------------------------------
	// --------------------------------------------------------------------
	// 						REFACTOR
	// --------------------------------------------------------------------
	// --------------------------------------------------------------------
    
//	private static final InvoiceDetailPropertiesDAO INVOICE_DETAIL_PROPERTIES = new InvoiceDetailPropertiesDAO();
//	private static class InvoiceDetailPropertiesDAO implements InvoiceDetailProperties {
//		
//		public Condition[] getConditions(InvoiceDetailFilter filter) {
//			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
//			if (filterDAO == null)
//				return new Condition[0];
//
//			return new Condition[] { filterDAO.getCondition() };
//		}
//		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.ID);}
//		@Override public Property<Integer> getDomainProperty(){return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.DOMAIN);}
//		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.CREATION_USER);}
//		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.CREATION_DATE);}
//		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.MODIFICATION_USER);}
//		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.MODIFICATION_DATE);}
//		@Override public Property<Integer> getInvoiceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.INVOICE);}
//		@Override public Property<Integer> getInvestAssetProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.INVEST_ASSET);}
//		@Override public Property<Short> getLineProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.LINE);}
//		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.ITEM);}
//		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.DESCRIPTION);}
//		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.QUANTITY);}
//		@Override public Property<Double> getPriceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.PRICE);}
//		@Override public Property<String> getDiscountExprProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.DISCOUNT_EXPR);}
//		@Override public Property<Byte> getSourceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.SOURCE);}
//		@Override public Property<Integer> getSourceIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.SOURCE_ID);}
//		@Override public Property<Double> getTaxableBaseProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.TAXABLE_BASE);}
//		@Override public Property<Double> getTaxesProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.TAXES);}
//		@Override public Property<Byte> getPrepaymentProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.PREPAYMENT);}
//		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.SELLER);}
//		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.WORKPLACE);}
//		@Override public Property<Integer> getWarehouseProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.WAREHOUSE);}
//	}
//	
//	
//	
//	private static SelectSeekStep1<Record, Short> select(AONContext ctx, InvoiceDetailFilter filter){	
//		return ctx.getDslContext()
//				.select()
//				.from(INVOICE_DETAIL)
//				.where(INVOICE_DETAIL_PROPERTIES.getConditions(filter))
//				.orderBy(INVOICE_DETAIL.LINE);
//	}
//	
//	
//
//	
//	static List<InvoiceDetail> getFullList(AONContext ctx, Invoice invoice) {  
//        return getFullStream(ctx, p -> p.getInvoiceProperty().eq(invoice.getId()))
//        	.map( detail -> {
//    			detail.getSource().visit(detail, new InvoiceDetailSourceVisitor(ctx, invoice));
//    			detail.setExpAccount( AccountingInvoiceDAO.getInvoiceDetailAccount(ctx, detail.getId()).orElse(new Account()));
//    			detail.setInvoiceTaxes(InvoiceTaxDAO.getInvoiceDetailTaxes( ctx, detail.getId()));
//    			return detail;
//        	})
//    		.collect(Collectors.toCollection(LinkedList::new));
//    }
//
//	static Stream<InvoiceDetail> getDetails(AONContext ctx, Integer invoiceId) {  
//        return getFullStream(ctx, p -> p.getInvoiceProperty().eq(invoiceId));
//    }
//	
//	
//	
//	
//	
//	
//	
//	
//	
//
//	
//	
//	// --------------------------------------------------------------------
//	// --------------------------------------------------------------------
//	//	 Los siguientes métodos (hasta final de fichero) deben pasar por refactor.
//	// --------------------------------------------------------------------
//	// --------------------------------------------------------------------
//
//	static Stream<InvoiceDetail> getInvoiceDetails(AONContext ctx, InvoiceFilter filter) {
//		return getFullInvoices(ctx, filter)
//			.stream()
//			.map(new FullInvoiceDetailFiller());
//	}
//
//	static Stream<InvoiceDetailExtended> getInvoiceDetailsExtended(AONContext ctx, InvoiceFilter filter, IDAOCallback callback) {
//		return getFullInvoices(ctx, filter)
//			.stream()
//			.onClose(() -> { if (callback != null) callback.onFinish();})
//			.map(new FullInvoiceDetailFiller())
//			.map( d -> new InvoiceDetailExtended()
//				.setDetail(d)
//				.setSegments(
//					RegistryDAO.getRegistrySegmentNames(ctx, d.getInvoice().getRegistry())
//						.collect(Collectors.toCollection(LinkedList::new)
//					)
//				)
//				.setSellerSupport(
//					RegistryDAO.getRegistrySellerNames(ctx, d.getInvoice().getRegistry(), d.getInvoice().getIssueDate())
//						.collect(Collectors.joining(", ")))
//			);
//	}
//
//	private static Result<Record> getFullInvoices(AONContext ctx, InvoiceFilter filter) {
//		ctx.checkRead();
//		return ctx.getDslContext()
//			.selectDistinct(
//				 INVOICE.ID
//				,INVOICE.DOMAIN
//				,ORDERED_TYPE
//				,INVOICE.ACTIVITY
//				,IAE.EPIGRAPH
//				,INVOICE.INVEST_ASSET
//				,INVOICE.PROJECT
//				,INVOICE.TYPE
//				,INVOICE.SERIES
//				,INVOICE.NUMBER
//				,INVOICE.REFERENCE_CODE
//				,INVOICE.ISSUE_DATE
//				,INVOICE.TAX_DATE
//				,INVOICE.REGISTRY
//				,INVOICE.RDOCUMENT
//				,INVOICE.RDOCUMENT_TYPE
//				,INVOICE.RDOCUMENT_COUNTRY
//				,INVOICE.RNAME
//				,INVOICE.SECURITY_LEVEL
//				,INVOICE.RECTIFICATION_TYPE
//				,INVOICE.RECTIFICATION_INVOICE
//				,GEOZONE.CODE
//				,GEOZONE.NAME
//				,RADDRESS.ZIP
//				,RADDRESS.CITY
//				,SCOPE.ID
//				,SCOPE.DESCRIPTION
//				,PROJECT.NAME
//				,INVOICE_DETAIL.LINE
//				
//				,INVOICE_DETAIL.ITEM
//				,PCATEGORY.ID
//				,PCATEGORY.NAME
//				,BRAND.NAME
//				,PRODUCT.ID
//				,PRODUCT.NAME
//				,PRODUCT.CODE
//				,PRODUCT.TYPE
//				,PRODUCT.PACKAGED
//				,ITEM.DETAIL
//				,ITEM.DETAIL2
//				,ITEM.DETAIL3
//				,ITEM.DESCRIPTION
//				,ITEM.PURCHASE_PRICE
//				,ITEM.PRICE
//				,ITEM.PACK_FORMAT_TAG
//				,ITEM.PACK_UNITS
//                ,ITEM.PACK_UNITS_TAG
//                ,ITEM.PACK_MEASUREMENT
//                ,ITEM.PACK_MEASUREMENT_TAG
//                ,ITEM.STOCK_UNIT_TAG
//				
//				,INVOICE_DETAIL.DESCRIPTION
//				,INVOICE_DETAIL.DOMAIN
//				,INVOICE_DETAIL.QUANTITY
//				,INVOICE_DETAIL.PRICE
//				,INVOICE_DETAIL.DISCOUNT_EXPR
//				,INVOICE_DETAIL.TAXABLE_BASE
//				,INVOICE_DETAIL.SELLER
//				,INVOICE_DETAIL.PROJECT
//				,INVOICE_DETAIL.WAREHOUSE
//				,INVOICE_DETAIL.WORKPLACE
//				,INVOICE_DETAIL.SOURCE
//				,INVOICE_DETAIL.SOURCE_ID
//				,INVOICE_DETAIL.INVEST_ASSET
//				,INVOICE_DETAIL.PREPAYMENT
//				,SellerDAO.SELLER_ALIAS.ID
//				,SellerDAO.SELLER_ALIAS.NAME
//				,WORKPLACE.DESCRIPTION
//				,WAREHOUSE.NAME
//				,SCOPE.DESCRIPTION
//				,INVOICE_DETAIL.ID
//				,PRODUCT.CATEGORY
//				,ITEM.ID
//			)
//			.from(INVOICE)
//			.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
//			.join(REGISTRY).on(REGISTRY.ID.equal(INVOICE.REGISTRY))
//			.join(SCOPE).on(SCOPE.ID.equal(INVOICE.SCOPE))
//			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
//			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
//			.leftOuterJoin(INVEST_ASSET).on(INVEST_ASSET.ID.equal(INVOICE.INVEST_ASSET))
//			.leftOuterJoin(RADDRESS).on(RADDRESS.REGISTRY.equal(REGISTRY.ID).and(RADDRESS.TYPE.equal((byte) 0)))
//			.leftOuterJoin(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
//			.leftOuterJoin(PROJECT).on(PROJECT.ID.equal(INVOICE_DETAIL.PROJECT))
//			.leftOuterJoin(ITEM).on(ITEM.ID.equal(INVOICE_DETAIL.ITEM))
//			.leftOuterJoin(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
//			.leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.equal(PCATEGORY.ID))
//			.leftOuterJoin(BRAND).on(PRODUCT.BRAND.equal(BRAND.ID))
//			.leftOuterJoin(SellerDAO.SELLER_ALIAS).on(SellerDAO.SELLER_ALIAS.ID.equal(INVOICE_DETAIL.SELLER))
//			.leftOuterJoin(WAREHOUSE).on(WAREHOUSE.ID.equal(INVOICE_DETAIL.WAREHOUSE))
//			.leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.equal(INVOICE_DETAIL.WORKPLACE))
//			.where(InvoiceDAO.INVOICE_PROPERTIES.getConditions(filter))
//			.orderBy(ORDERED_TYPE,INVOICE.TYPE,INVOICE.ISSUE_DATE,INVOICE.REFERENCE_CODE,INVOICE_DETAIL.LINE)
//			.fetch();
//	}
//
//	private static class FullInvoiceDetailFiller extends Filler implements Function<Record,InvoiceDetail> {
//
//		@Override
//		public InvoiceDetail apply(Record r) {
//			return new InvoiceDetail()
//				.setId(r.getValue(INVOICE_DETAIL.ID))
//				.setInvoice(new Invoice()
//					.setId(r.getValue(INVOICE.ID))
//					.setDomain(r.getValue(INVOICE.DOMAIN))
//					.setType(AonEnumUtils.enumValue(InvoiceType.class, r.getValue(INVOICE.TYPE)))
//					.setSeries(r.getValue(INVOICE.SERIES))
//					.setNumber(r.getValue(INVOICE.NUMBER))
//					.setReferenceCode(r.getValue(INVOICE.REFERENCE_CODE))
//					.setIssueDate(r.getValue(INVOICE.ISSUE_DATE))
//					.setTaxDate(r.getValue(INVOICE.TAX_DATE))
//					.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class, r.getValue(INVOICE.SECURITY_LEVEL)))
//					.setRegistry(r.getValue(INVOICE.REGISTRY))
//					.setRegistryDocument(r.getValue(INVOICE.RDOCUMENT))
//					.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class, r.getValue(INVOICE.RDOCUMENT_TYPE)))
//					.setRegistryDocumentCountry(Country.safeValueOf(r.getValue(INVOICE.RDOCUMENT_COUNTRY)))
//					.setRegistryName(r.getValue(INVOICE.RNAME))
//					.setAddress(new RegistryAddress()
//							.setGeozoneCode(r.getValue(GEOZONE.CODE))
//							.setGeozoneName(r.getValue(GEOZONE.NAME))
//							.setCity(r.getValue(RADDRESS.CITY))
//							.setZip(r.getValue(RADDRESS.ZIP)))
//					.setScope(new Scope().setId(r.getValue(SCOPE.ID)).setDescription(r.getValue(SCOPE.DESCRIPTION)))
//				)
//				.setInvestAsset(checkField(r, INVEST_ASSET.ID)
//					? InvestAssetFiller.build(r)
//					: null )
//				.setProject( r.getValue( INVOICE_DETAIL.PROJECT ))
//				.setProjectName( r.getValue( PROJECT.NAME ))
//				.setLine(r.getValue( INVOICE_DETAIL.LINE ))
//				.setDescription(r.getValue( INVOICE_DETAIL.DESCRIPTION ))
//				.setQuantity(r.getValue(INVOICE_DETAIL.QUANTITY))
//				.setPrice(r.getValue(INVOICE_DETAIL.PRICE))
//				.setDiscountExpression(r.getValue(INVOICE_DETAIL.DISCOUNT_EXPR))
//				.setTaxableBase(r.getValue(INVOICE_DETAIL.TAXABLE_BASE))
//				.setItem(checkField(r, ITEM.ID)
//					? ItemFiller.build(r)
//					: new Item().setId(r.getValue(INVOICE_DETAIL.ITEM)))
//				.setSeller(checkField(r, SellerDAO.SELLER_ALIAS.ID)
//					? new Seller().copy(RegistryFiller.build(r, SellerDAO.SELLER_ALIAS))
//					: new Seller().setId(r.getValue(INVOICE_DETAIL.SELLER)))
//				.setWorkplace( new Workplace()
//					.setId( r.getValue(INVOICE_DETAIL.WORKPLACE) )
//					.setDescription(getValue(r,WORKPLACE.DESCRIPTION)))
//				.setWarehouse(r.getValue(INVOICE_DETAIL.WAREHOUSE))
//				.setWarehouseName(r.getValue(WAREHOUSE.NAME))
//				.setSource(InvoiceSource.safeValueOf(r.getValue(INVOICE_DETAIL.SOURCE)))
//				.setSourceId(getValue(r, INVOICE_DETAIL.SOURCE_ID))
//				.setPrepayment(getBoolean(r, INVOICE_DETAIL.PREPAYMENT))
//				;
//			
//		}
//		
//	}
//	
//	private static class InvoiceDetailSourceVisitor implements IInvoiceSourceVisitor {
//
//		private static final long serialVersionUID = 1891794805038724206L;
//		
//		private AONContext ctx;
//		private Invoice invoice;
//		
//		private InvoiceDetailSourceVisitor(AONContext ctx, Invoice invoice) {
//			this.ctx = ctx;
//			this.invoice = invoice;
//		}
//	
//		@Override 
//		public void visitSales(InvoiceDetail detail) {
//			detail.setSalesDetail(SalesDetailDAO.get(ctx, detail.getSourceId()));						
//		}
//		
//		@Override 
//		public void visitPurchase(InvoiceDetail detail) {
//			PurchaseDetail d = PurchaseDAO.getPurchaseDetailStream(ctx
//					, f -> f.getDomainProperty().eq(invoice.getDomain()).and(f.getIdProperty().eq(detail.getSourceId())))
//				.findFirst()
//				.orElse(new PurchaseDetail());
//			detail.setPurchaseDetail(d);						
//		}
//		
//		@Override 
//		public void visitOffer(InvoiceDetail detail) {
//			detail.setOfferDetail(OfferDetailDAO.get(ctx, detail.getSourceId()));						
//		}
//		@Override 
//		public void visitIncome(InvoiceDetail detail) {
//			IncomeDetail d = IncomeDAO.getIncomeDetailStream(ctx
//					, f -> f.getDomainProperty().eq(invoice.getDomain()).and(f.getIdProperty().eq(detail.getSourceId())))
//				.findFirst()
//				.orElse(new IncomeDetail());	
//			detail.setIncomeDetail(d);						
//		}
//		@Override 
//		public void visitDelivery(InvoiceDetail detail) {
//			detail.setDeliveryDetail(DeliveryDetailDAO.getFull(ctx, detail.getSourceId()));						
//		}
//		
//		@Override public void visitReservation(InvoiceDetail detail) { /* Nothing*/}
//		@Override public void visitFee(InvoiceDetail detail) { /* Nothing*/}
//		@Override public void visitDirectInvoice(InvoiceDetail detail) { /* Nothing*/}
//		@Override public void visitDirectExpense(InvoiceDetail detail) { /* Nothing*/}
//		@Override public void visitAccount(InvoiceDetail detail) { /* Nothing*/}
//		@Override public void visitTedi(InvoiceDetail detail) { /* Nothing*/}
//	}

}
