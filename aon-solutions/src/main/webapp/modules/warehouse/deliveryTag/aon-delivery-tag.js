import { AonBasicTable } from '../../../components/aon-basic-table.js';
import { AonCard } from '../../../components/aon-card.js';
import { AonDate } from '../../../components/aon-date.js';
import { AonInput } from '../../../components/aon-input.js';
import { AonSelect } from '../../../components/aon-select.js';
import { AonToolbar } from '../../../components/aon-toolbar.js';
import {AonElement} from '../../../components/AonElement.js';
import { CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../../environments/environments.js';
import { ToolbarType } from '../../../models/enums.js';
import { printDeliveryTag } from '../../../services/bartenderService.js';
import { AonCustomerSuggestion } from '../../registry/customer/aon-customer-suggestion.js';
export class AonDeliveryTag extends AonElement {

	TOOLBAR;
	GTIN;
	BOX;
	LOTE;
	DATE;
	SSCC;
	SALES;
	DATA_TABLE;
	TRANSPORTE;
	DELIVERY;
	PRINTER;
	TAG;

	printer;
	tag;
	data;
	
	connectedCallback () {
		this.initialize();
		this.build();	
	}

	initialize() {
		this.id = this.id || 'aonDeliveryTag';
		this.TOOLBAR = this.id + CONSTANT.TOOLBAR.initCap();
		this.GTIN = this.id + 'Gtin';
		this.BOX = this.id + 'Box';
		this.LOTE = this.id + 'Lote';
		this.DATE = this.id + 'Date';
		this.SSCC = this.id + 'Sscc';
		this.SALES = this.id + 'Sales';	
		this.DATA_TABLE = this.id + 'DataTable';
		this.TRANSPORTE = this.id + 'Transporte';
		this.DELIVERY = this.id + 'Delivery';
		this.TAG = this.id + 'Tag';
		this.PRINTER = this.id + 'Printer';
		this.data = {};
		this.printer = 'ZEBRA 93';
		this.tag = 'C:\\Bartender\\Diseños\\DiseñoMercadona.btw';
	}

	build() {
		this.buildToolbar();
		this.buildContent();
	} 

	buildToolbar() {
		let toolbar = this.createAonElement(new AonToolbar(), this.TOOLBAR, MSG.DELIVERY_TAG);
		toolbar.type = ToolbarType.SECONDARY;
		this.appendChild(toolbar);
		toolbar.addButton2({
			id: CONSTANT.DOWNLOAD.initCap(),
			name: MSG.DOWNLOAD,
			title: MSG.DOWNLOAD,
			icon: MATERIAL_ICONS.PRINT
		}, () => this.print());	
	}

	print() {
		printDeliveryTag(this.printer, this.tag, this.data);
	}

	buildContent() {
		let div = this.createElement(TAG.DIV);
		this.appendChild(div);
		if(!this.isMobile()){
		  div.style.display = 'flex';
		  div.style.height = '100%';
		}
		
		let dataDiv = this.createElement(TAG.DIV, this.DATA, CSS.AON_SUB_CONTENT);
		dataDiv.style.width = this.isMobile() ? '100%' : '50%';
		dataDiv.style.height = '100%';
		div.appendChild(dataDiv);
	
		this.buildData(dataDiv);
	}

	buildData(parent) {
		let card = this.createAonElement(new AonCard(), this.DATA_CARD, 'Datos Etiqueta');
		parent.appendChild(card);

		let div = this.createElement(TAG.DIV);
		card.setContent(div);

		let table = this.createAonElement(new AonBasicTable(), this.DATA_TABLE);
		div.appendChild(table);

		table.addRow();

		let printer = this.createAonElement(new AonSelect(), this.PRINTER, MSG.PRINTER);
		table.addCell(printer);
		printer.options = JSON.stringify([{
			name: 'ZEBRA 93',
			value: 'ZEBRA 93'
		},{
			name: 'ZEBRA 94',
			value: 'ZEBRA 94'
		}]);
		printer.value = this.printer;

		printer.addEventListener(EVENT.CHANGE, () => {
			this.printer = printer.value;
 		});

		let tag = this.createAonElement(new AonSelect(), this.TAG, MSG.TAG);
		table.addCell(tag);
		tag.options = JSON.stringify([{
			name: 'Mercadona',
			value: 'mercadona'
		},{
			name: 'Pingo',
			value: 'pingo'
		},{
			name: 'Generica',
			value: 'generica'
		}]);

		let tagContent = this.createElement(TAG.DIV);
		div.appendChild(tagContent);
		tag.value = 'mercadona';
		this.buildMercadona(tagContent);

		tag.addEventListener(EVENT.SELECT, () => {
			if(tag.value === 'mercadona') {
				this.buildMercadona(tagContent);
				this.tag = 'C:\\Bartender\\Diseños\\DiseñoMercadona.btw';
			} else if(tag.value === 'pingo') {
				this.buildPingo(tagContent);
				this.tag = 'C:\\Bartender\\Diseños\\DiseñoPingoDoce.btw';
			} else {
				this.buildGenerica(tagContent);
				this.tag = 'C:\\Bartender\\Diseños\\DiseñoGenerico.btw';
			}
			this.data =  {};
 		});
	}	

	buildMercadona(tagContent) {
		this.clearElement(tagContent);
		let table = this.createAonElement(new AonBasicTable(), this.DATA_TABLE + 'Mercadona');
		tagContent.appendChild(table);

		table.addRow();

		let gtin = this.createAonElement(new AonInput(), this.GTIN, 'GTIN');
		table.addCell(gtin);
		gtin.addEventListener(EVENT.CHANGE, () => {
			this.data.GTIN = gtin.value;
		});

		let box = this.createAonElement(new AonInput(), this.BOX, 'Cajas');
		table.addCell(box);
		box.addEventListener(EVENT.CHANGE, () => {
			this.data.CANTIDAD = box.value;
		});

		table.addRow();

		let lote = this.createAonElement(new AonInput(), this.LOTE, 'Lote');
		table.addCell(lote);
		lote.addEventListener(EVENT.CHANGE, () => {
			this.data.LOTE = lote.value;
		});

		let fecha = this.createAonElement(new AonDate(), this.DATE, 'Fecha');
		table.addCell(fecha);
		fecha.addEventListener(EVENT.CHANGE, () => {
			this.data.F_CONSUMO_PREFERENTE = fecha.value;
		});

		table.addRow();
		
		let sscc = this.createAonElement(new AonInput(), this.SSCC, 'SSCC');
		table.addCell(sscc, 2);
		sscc.addEventListener(EVENT.CHANGE, () => {
			this.data.SSCC = sscc.value;
		});
	}

	buildPingo(tagContent) {
		this.clearElement(tagContent);
		let table = this.createAonElement(new AonBasicTable(), this.DATA_TABLE + 'Pingo');
		tagContent.appendChild(table);

		table.addRow();

		let gtin = this.createAonElement(new AonInput(), this.GTIN, 'GTIN');
		table.addCell(gtin);
		gtin.addEventListener(EVENT.CHANGE, () => {
			this.data.GTIN = gtin.value;
		});

		let box = this.createAonElement(new AonInput(), this.BOX, 'Cajas');
		table.addCell(box);
		box.addEventListener(EVENT.CHANGE, () => {
			this.data.CANTIDAD = box.value;
		});

		table.addRow();

		let lote = this.createAonElement(new AonInput(), this.LOTE, 'Lote');
		table.addCell(lote);
		lote.addEventListener(EVENT.CHANGE, () => {
			this.data.LOTE = lote.value;
		});

		let fecha = this.createAonElement(new AonDate(), this.DATE, 'Fecha');
		table.addCell(fecha);
		fecha.addEventListener(EVENT.CHANGE, () => {
			this.data.F_CONSUMO_PREFERENTE = fecha.value;
		});

		table.addRow();
		
		let sscc = this.createAonElement(new AonInput(), this.SSCC, 'SSCC');
		table.addCell(sscc);
		sscc.addEventListener(EVENT.CHANGE, () => {
			this.data.SSCC = sscc.value;
		});

		let sales = this.createAonElement(new AonInput(), this.SALES, 'Pedido');
		table.addCell(sales);
		sales.addEventListener(EVENT.CHANGE, () => {
			this.data.ORDEN_COMPRA = sales.value;
		});
	}

	buildGenerica(tagContent) {
		this.clearElement(tagContent);
		let table = this.createAonElement(new AonBasicTable(), this.DATA_TABLE + 'Generic');
		tagContent.appendChild(table);

		table.addRow();

		let customer = new AonCustomerSuggestion();	
		customer.id = this.REGISTRY;
		customer.showAddress = true;
		table.addCell(customer, 2);
		customer.addEventListener(EVENT.CHANGE, () => {
			this.data.NOMBRE_CLIENTE = '';
			this.data.DIRECCION_CLIENTE = '';
		});

		table.addRow();
		
		let transporte = this.createAonElement(new AonInput(), this.TRANSPORTE, 'Transporte');
		table.addCell(transporte, 2);
		transporte.addEventListener(EVENT.CHANGE, () => {
			this.data.AGENCIA_TRANSPORTE = '';
		});

		table.addRow();
		
		let sscc = this.createAonElement(new AonInput(), this.SSCC, 'SSCC');
		table.addCell(sscc);
		sscc.addEventListener(EVENT.CHANGE, () => {
			// this.data.SSCC = sscc.value;
		});

		let delivery = this.createAonElement(new AonInput(), this.DELIVERY, 'Albaran');
		table.addCell(delivery);
		delivery.addEventListener(EVENT.CHANGE, () => {
			// this.data.DELIVERY = delivery.value;
		});
	}

}
if(!window.customElements.get(TAG.AON_DELIVERY_TAG)){
	window.customElements.define(TAG.AON_DELIVERY_TAG, AonDeliveryTag);
}