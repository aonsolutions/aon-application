import { Domain } from "../Domain.js";
import { Product } from "./Product.js";

export class Item {

    id;
	domain;
	detail;
	detail2;
	detail3;
	description;
	serialNumber;
	serialDate;
	barcode;
	status;
	product;
	price;
	expensesPercent;
	expensesFixed;
	profitPercent;
	purchasePrice;
	internet;
	packFormatTag;
	packUnits;
	packUnitsTag;
    packMeasurement;
	packMeasurementTag;
	stockUnitTag;
    creationUser;
	creationDate;
	modificationUser;
	modificationDate;

    removed;

    constructor(item) {
        if(item) {
            this.id = item.id;
            this.domain = item.domain;
            this.detail = item.detail;
            this.detail2 = item.detail2;
            this.detail3 = item.detail3;
            this.description = item.description;
            this.serialNumber = item.serialNumber;
            this.serialDate = item.serialDate;
            this.barcode = item.barcode || '';
            this.status = item.status;
            this.product = new Product(item.product);
            this.price = item.price;
            this.expensesPercent = item.expensesPercent;
            this.expensesFixed = item.expensesFixed;
            this.profitPercent = item.profitPercent || 0;
            this.purchasePrice = item.purchasePrice || 0;
            this.internet = item.internet;
            this.packFormatTag = item.packFormatTag;
            this.packUnits = item.packUnits;
            this.packUnitsTag = item.packUnitsTag;
            this.packMeasurement = item.packMeasurement;
            this.packMeasurementTag = item.packMeasurementTag;
            this.stockUnitTag = item.stockUnitTag;
            this.creationUser = item.creationUser;
            this.creationDate = item.creationDate;
            this.modificationUser = item.modificationUser;
            this.modificationDate = item.modificationDate;

            this.removed = item.removed || false;
        } else {
            this.domain = new Domain();
            this.price = 0;
            this.purchasePrice = 0;
            this.profitPercent = 0;
            this.description = '';
            this.barcode = '';
            this.status = 'active';

            this.removed = false;
        }
    }

    getId() {
        return this.id;
    }

    setId(id) {
        this.id = id;
        return this;
    }

    getDomain() {
        return this.domain;
    }

    setDomain(domain) {
        this.domain = domain;
        return this;
    }

    getDetail() {
        return this.detail;
    }

    setDetail(detail) {
        this.detail = detail;
        return this;
    }

    getDetail2() {
        return this.detail2;
    }

    setDetail2(detail2) {
        this.detail2 = detail2;
        return this;
    }

    getDetail3() {
        return this.detail3;
    }

    setDetail3(detail3) {
        this.detail3 = detail3;
        return this;
    }

    getDescription() {
        return this.description;
    }

    setDescription(description) {
        this.description = description;
        return this;
    }

    getSerialNumber() {
        return this.serialNumber;
    }

    setSerialNumber(serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    getSerialDate() {
        return this.serialDate;
    }

    setSerialDate(serialDate){
        this.serialDate = serialDate;
        return this.serialDate;
    }
    
    getBarcode() {
        return this.barcode;
    }

    setBarcode(barcode) {
        this.barcode = barcode;
        return this.barcode;
    }

    getStatus() {
        return this.status;
    }

    setStatus(status) {
        this.status = status;
        return this.status;
    }
    
    getProduct() {
        return this.product;
    }

    setProduct(product) {
        this.product = product;
        return this;
    }

    getPrice() {
        return this.price;
    }

    setPrice(price) {
        this.price = price;
        this.calculateProfitPercent();
        return this;
    }

    getExpensesPercent() {
        return this.expensesPercent;    
    }

    setExpensesPercent(expensesPercent) {
        this.expensesPercent = expensesPercent;
        return this;
    }

    getExpensesFixed() {
        return this.expensesFixed;
    }

    setExpensesFixed(expensesFixed) {
        this.expensesFixed = expensesFixed;
        return this;
    }

    getProfitPercent() {
        return this.profitPercent;        
    }

    setProfitPercent(profitPercent) {
        this.profitPercent = Number(profitPercent);
        this.calculatePriceByPurchasePrice()
        return this;
    }

    getPurchasePrice() {
        return this.purchasePrice;
    }

    setPurchasePrice(purchasePrice) {
        this.purchasePrice = Number(purchasePrice);
        this.calculatePriceByPurchasePrice()
        return this;
    }

    calculatePriceByPurchasePrice() {
        let price = Number(this.purchasePrice);
        if(this.profitPercent){
            price = price + (price * Number(this.profitPercent) / 100);
        }
        this.price = price;
    }

    calculateProfitPercent() {
        if(this.purchasePrice > 0) {
            this.profitPercent = (this.price - this.purchasePrice) * 100 / this.purchasePrice;
        } else {
            this.profitPercent = 0;
        }
    }

    isInternet() {
        return this.internet;
    }

    setInternet(internet) {
        this.internet = internet;
        return this;
    }

    getPackFormatTag() {
       return this.packFormatTag; 
    }

    setPackFormatTag(packFormatTag) {
        this.packFormatTag = packFormatTag;
        return this;
    }

    getPackUnits() {
        return this.packUnits;      
    }

    setPackUnits(packUnits) {
        this.packUnits = packUnits;
        return this;
    }

    getPackUnitsTag() {
        return this.packUnitsTag;
    }

    setPackUnitsTag(packUnitsTag) {
        this.packUnitsTag = packUnitsTag;
        return this;
    }
    
    getPackMeasurement(){
        return this.packMeasurement;
    }

    setPackMeasurement(packMeasurement) {
        this.packMeasurement = packMeasurement;
        return this;
    }
    
    getPackMeasurementTag() {
        return this.packMeasurementTag;
    }

    setPackMeasurementTag(packMeasurementTag) {
        this.packMeasurementTag = packMeasurementTag;
        return this;
    }

    getStockUnitTag() {
        return this.stockUnitTag;
    }

    setStockUnitTag(stockUnitTag) {
        this.stockUnitTag = stockUnitTag;
        return this;
    }

    getCreationUser() {
        return this.creationUser;
    }

    setCreationUser(creationUser) {
        this.creationUser = creationUser;
        return this;
    }
    
    getCreationDate() {
        return this.creationDate;
    }

    setCreationDate(creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    getModificationUser() {
        return this.modificationUser;
    }

    setModificationUser(modificationUser) {
        this.modificationUser = modificationUser;
        return this;
    }

    getModificationDate() {
        return this.modificationDate;
    }

    setModificationDate(modificationDate) {
        this.modificationDate = modificationDate;
        return this;
    }

    isRemoved() {
        return this.removed;
    }

    setRemoved(removed) {
        this.removed = removed;
        return this;
    }

    remove(){
        this.setRemoved(true);
    }
}