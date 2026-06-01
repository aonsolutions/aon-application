import { MSG, CSS, EVENT, TAG } from '../../environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from '../../gwt/gwt.js';
import * as JSF from '../aon-jsf-app.js';

export class AonWarehouseMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;

    constructor () {
        super();
		this.almacenInitialize()
    }

    connectedCallback () {
        this.clear();
        this.initialize();
        this.build();
    }

    almacenInitialize() {
        this.AON_MENU = 'aonMenu';
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.last = MSG.LAST_ELABORATIONS;
        this.new = MSG.NEW_DELIVERY_NOTE;
        this.cardData={
            title: MSG.ACTIVITY,
            info:[MSG.EXPEDITIONS_OPEN, MSG.OPEN_ELABORATIONS]
        };
        this.selectOptions= [{
            title: MSG.ELABORATION,
            action: () => alert("description")
        },{
            title: MSG.PURCHASE_DELIVERY,
            action: () => alert("description")
        },{
            title: MSG.SALE_DELIVERY,
            action: () => alert("description")
        }];
        this.initOptions();
    }

    initOptions() {
        this.options = [{
            title: MSG.MOVEMENTS,
            options: [{
                description: MSG.INCOMING_PURCHASE_NOTES,
                title: MSG.INCOMING_PURCHASE_NOTES,
                action: () => this.rootPanel(new JSF.AonJsfIncome)
            }, {
                description: MSG.OUTGOING_SALES_NOTES,
                title: MSG.OUTGOING_SALES_NOTES,
                action: () => this.rootPanel(new JSF.AonJsfDelivery)
            }, {
                description: MSG.WAREHOUSE_TRANSFERS,
                title: MSG.WAREHOUSE_TRANSFERS,
                action: () => this.rootPanel(new JSF.AonJsfWarehouseTransfer)
            }, {
                description: MSG.SERVE_ORDERS,
                title: MSG.SERVE_ORDERS,
                action: () => this.rootPanel(new JSF.AonJsfOrderServer)
            }]
        }, {
            title: MSG.REPORTS,
            options: [{
                description: MSG.WAREHOUSE_LIST,
                title: MSG.WAREHOUSE_LIST,
                action: () => this.rootPanel(new JSF.AonJsfStockReportWarehouse)
            }, {
                description: MSG.WAREHOUSE_ITEM_LIST,
                title: MSG.WAREHOUSE_ITEM_LIST,
                action: () => this.rootPanel(new JSF.AonJsfStockReportItem)
            }, {
                description: MSG.VALUED_WAREHOUSE_LIST,
                title: MSG.VALUED_WAREHOUSE_LIST,
                action: () => this.rootPanel(new JSF.AonJsfStockReportWarehouseValued)
            }, {
                description: MSG.VALUED_ITEM_LIST,
                title: MSG.VALUED_ITEM_LIST,
                action: () => this.rootPanel(new JSF.AonJsfStockReportItemValued)
            }, {
                description: MSG.SUPPLY_BY_CONSUMPTION,
                title: MSG.SUPPLY_BY_CONSUMPTION,
                action: () => GWT.iLoad(GWT.STOCK_FORECAST)
            }, {
                description: MSG.MOVEMENTS_LIST,
                title: MSG.MOVEMENTS_LIST,
                action: () => GWT.iLoad(GWT.MOVEMENT_LIST)
            }]
        }, {
            title: MSG.STOCK_CONTROL,
            options: [{
                description: MSG.INVENTORY_CLOSURE,
                title: MSG.INVENTORY_CLOSURE,
                action: () => this.rootPanel(new JSF.AonJsfInventoryClose)
            }, {
                description: MSG.MANAGEMENT_INVENTORY,
                title: MSG.MANAGEMENT_INVENTORY,
                action: () => this.rootPanel(new JSF.AonJsfInventory)
            }, {
                description: MSG.STOCK_CONTROL,
                title: MSG.STOCK_CONTROL,
                action: () => this.rootPanel(new JSF.AonJsfStock)
            }, {
                description: MSG.ORDER_PROPOSAL,
                title: MSG.ORDER_PROPOSAL,
                action: () => this.rootPanel(new JSF.AonJsfOrderProposal)
            }]
        }, {
            title: MSG.ELABORATIONS,
            options: [{
                description: MSG.ELABORATION_ORDER,
                title: MSG.ELABORATION_ORDER,
                action: () => GWT.iLoad(GWT.ELABORATION)
            }, {
                description: MSG.PACKING_LIST,
                title: MSG.PACKING_LIST,
                action: () => GWT.iLoad(GWT.PACKING_LIST)
            }, {
                description: MSG.SHIPPING_LABELS,
                title: MSG.SHIPPING_LABELS,
                action: () => this.rootPanel(new JSF.AonJsfCorporateIdentityLabel)
            }]
        }, {
            title: MSG.WAREHOUSES,
            options: [{
                description: MSG.WAREHOUSE_DEFINITION,
                title: MSG.WAREHOUSE_DEFINITION,
                action: () => this.rootPanel(new JSF.AonJsfWarehouse)
            }, {
                description: MSG.CARRIERS,
                title: MSG.CARRIERS,
                action: () => this.rootPanel(new JSF.AonJsfCarrier)
            }, {
                description: MSG.PRODUCT_TAG_PRINT,
                title: MSG.PRODUCT_TAG_PRINT,
                action: () => this.rootPanel(new JSF.AonJsfItemTagPrint)

            }]
        }];
    }
}
if(!window.customElements.get(TAG.AON_WAREHOUSE_MENU)){
    window.customElements.define(TAG.AON_WAREHOUSE_MENU, AonWarehouseMenu);
}