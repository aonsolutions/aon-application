import { AonBasicTable } from '../../../components/aon-basic-table.js';
import { AonCard } from '../../../components/aon-card.js';
import { AonDate } from '../../../components/aon-date.js';
import { AonInput } from '../../../components/aon-input.js';
import { AonSelect } from '../../../components/aon-select.js';
import { AonToolbar } from '../../../components/aon-toolbar.js';
import {AonElement} from '../../../components/AonElement.js';
import { CONSTANT, MSG, TAG } from '../../../environments/environments.js';
import { ToolbarType } from '../../../models/enums.js';
export class AonDeliveryTag extends AonElement {

	TOOLBAR;
	GTIN;
	BOX;
	LOTE;
	DATE;
	SSCC;
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
	}

	build() {
		this.buildToolbar();
		this.buildContent();
	} 

	buildToolbar() {
		let toolbar = this.createAonElement(new AonToolbar(), this.TOOLBAR, MSG.DELIVERY_TAG);
		toolbar.type = ToolbarType.SECONDARY;
		this.appendChild(toolbar);
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
	
		let table = this.createAonElement(new AonBasicTable(), this.DATA_TABLE);
			card.setContent(table);
	
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

}
if(!window.customElements.get(TAG.AON_DELIVERY_TAG)){
	window.customElements.define(TAG.AON_DELIVERY_TAG, AonDeliveryTag);
}