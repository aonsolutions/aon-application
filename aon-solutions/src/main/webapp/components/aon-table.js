import {AonElement} from './AonElement.js';
import '../../components/aon-checkbox.js';
import '../../components/aon-dialog.js';

export class AonTable extends AonElement {

		columns;

		get id() {
			return this.getAttribute('id');
		}

		set id(id) {
			this.setAttribute('id', id);
		}

		get selectable() {
			return this.getAttribute('selectable');
		}

		set selectable(selectable) {
			this.setAttribute('selectable', selectable);
		}

		constructor () {
			super();
			this.columns = [];
		}


		connectedCallback () {
			this.innerHTML = `
				<aon-dialog id="${this.getId() + 'aonDialogAddOption'}" type="menu"></aon-dialog>
				<table class="aonTable">
					<thead>
						<tr id="${this.getId() + 'TableHeader'}" >

						</tr>
					</thead>
					<tbody id="${this.getId() + 'TableBody'}">

					</tbody>
				</table>
			`;

			if(this.hasAttribute('selectable')) {
				let header = this.getElement(this.getId() + 'TableHeader');
				let th = document.createElement('th');
				th.innerHTML = '<aon-checkbox id="aonTableAllSelection"> </aon-checkbox>';
				header.appendChild(th);
				let ch = this.getElement('aonTableAllSelection');
				ch.addEventListener('change',() => {
					document.querySelectorAll('aon-checkbox').forEach((item, i) => {
						item.value = ch.value;
					});
				});

			}
		}

		addColumn(name, type, id) {
			let header = this.getElement(this.getId() + 'TableHeader');
			let th = document.createElement('th');
			th.innerHTML = name;
			this.columns.push({name, type, id});
			header.appendChild(th);
		}

		addRow(value, fn) {
			let body = this.getElement(this.getId() + 'TableBody');
			if(!body) return true;
			let tr = document.createElement('tr');
			tr.style.cursor = 'pointer';


			if(this.hasAttribute('selectable')) {
				let tdCheckBox = document.createElement('td');
				tdCheckBox.innerHTML = `<aon-checkbox id="aaa${body.children.length}"> </aon-checkbox>`;
				tr.appendChild(tdCheckBox);
			}

			this.columns.forEach((item, i) => {
				let td = document.createElement('td');
				let id = item.id;
				if('option' === id){
					td.innerHTML = `<aon-icon-button id="${this.getId()}IconOption" icon="more_vert"></aon-icon-button>`;
					td.addEventListener('click', () => this.getOptions(tr,td, value[id]));
				} else {
					td.innerHTML = value[id] ? value[id] : '';
					td.addEventListener('click', fn);
				}
				tr.appendChild(td);
			});

			body.appendChild(tr);
		}

		removeRows() {
			let body = this.getElement(this.getId() + 'TableBody');
			if(body) body.innerHTML = '';
		}

		getId(){
			return this.getAttribute('id');
		}


		getOptions(tr, td, options) {
			const top  = td.getBoundingClientRect().top;
			const left = td.getBoundingClientRect().left;
			let d = this.getElement(this.getId() + 'aonDialogAddOption');
			options = options.map( ({icon, name, fn}) => {
				return {
					icon,
					name,
					fn: () => fn(tr)
				};
			})

			d.setMenuOptions(options, top, left);
			d.open();
		}
}

window.customElements.define('aon-table', AonTable);
