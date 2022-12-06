import { AonBasicTable } from '../../../components/aon-basic-table.js';
import { AonCard } from '../../../components/aon-card.js';
import { AonDate } from '../../../components/aon-date.js';
import { AonInput } from '../../../components/aon-input.js';
import { AonSelect } from '../../../components/aon-select.js';
import { AonToolbar } from '../../../components/aon-toolbar.js';
import {AonElement} from '../../../components/AonElement.js';
import { CONSTANT, EVENT, MSG, TAG } from '../../../environments/environments.js';
import { ToolbarType } from '../../../models/enums.js';
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
	}

	build() {
		this.buildToolbar();
		this.buildContent();
	} 

	buildToolbar() {
		let toolbar = this.createAonElement(new AonToolbar(), this.TOOLBAR, MSG.DELIVERY_TAG);
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.removeButtons();
		toolbar.addButton2({
			id: CONSTANT.DOWNLOAD.initCap(),
			name: MSG.DOWNLOAD,
			title: MSG.DOWNLOAD,
			icon: 'file'
		}, () => this.download());
		this.appendChild(toolbar);
	}

	download() {
		
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

		let tag = this.createAonElement(new AonSelect(), this.TAG, MSG.TAG);
		table.addCell(tag, 2);
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
			} else if(tag.value === 'pingo') {
				this.buildPingo(tagContent);
			} else this.buildGenerica(tagContent);
 		});



	}	

	buildMercadona(tagContent) {
		this.clearElement(tagContent);
		let table = this.createAonElement(new AonBasicTable(), this.DATA_TABLE + 'Mercadona');
		tagContent.appendChild(table);

		table.addRow();

		let gtin = this.createAonElement(new AonInput(), this.GTIN, 'GTIN');
		table.addCell(gtin);

		let box = this.createAonElement(new AonInput(), this.BOX, 'Cajas');
		table.addCell(box);

		table.addRow();

		let lote = this.createAonElement(new AonInput(), this.LOTE, 'Lote');
		table.addCell(lote);

		let fecha = this.createAonElement(new AonDate(), this.DATE, 'Fecha');
		table.addCell(fecha);

		table.addRow();
		
		let sscc = this.createAonElement(new AonInput(), this.SSCC, 'SSCC');
		table.addCell(sscc, 2);

	}

	buildPingo(tagContent) {
		this.clearElement(tagContent);
		let table = this.createAonElement(new AonBasicTable(), this.DATA_TABLE + 'Pingo');
		tagContent.appendChild(table);

		table.addRow();

		let gtin = this.createAonElement(new AonInput(), this.GTIN, 'GTIN');
		table.addCell(gtin);

		let box = this.createAonElement(new AonInput(), this.BOX, 'Cajas');
		table.addCell(box);

		table.addRow();

		let lote = this.createAonElement(new AonInput(), this.LOTE, 'Lote');
		table.addCell(lote);

		let fecha = this.createAonElement(new AonDate(), this.DATE, 'Fecha');
		table.addCell(fecha);

		table.addRow();
		
		let sscc = this.createAonElement(new AonInput(), this.SSCC, 'SSCC');
		table.addCell(sscc);

		let sales = this.createAonElement(new AonInput(), this.SALES, 'Pedido');
		table.addCell(sales);
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

		table.addRow();
		
		let transporte = this.createAonElement(new AonInput(), this.TRANSPORTE, 'Transporte');
		table.addCell(transporte, 2);

		table.addRow();
		
		let sscc = this.createAonElement(new AonInput(), this.SSCC, 'SSCC');
		table.addCell(sscc);

		let delivery = this.createAonElement(new AonInput(), this.DELIVERY, 'Albaran');
		table.addCell(delivery);
	}

}
if(!window.customElements.get(TAG.AON_DELIVERY_TAG)){
	window.customElements.define(TAG.AON_DELIVERY_TAG, AonDeliveryTag);
}