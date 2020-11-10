// import {AonElement} from './AonElement.js';
import '../../components/aon-checkbox.js';

export class AonTable extends HTMLElement {

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
				let header = document.getElementById(this.getId() + 'TableHeader');
				let th = document.createElement('th');
				th.innerHTML = '<aon-checkbox id="aonTableAllSelection"> </aon-checkbox>';
				header.appendChild(th);
				let ch = document.getElementById('aonTableAllSelection');
				ch.addEventListener('change',() => {
					document.querySelectorAll('aon-checkbox').forEach((item, i) => {
						item.value = ch.value;
					});
				});

			}
		}

		addColumn(name, type, id) {
			let header = document.getElementById(this.getId() + 'TableHeader');
			let th = document.createElement('th');
			th.innerHTML = name;
			this.columns.push({name, type, id});
			header.appendChild(th);
		}

		addRow(value, fn) {
			let body = document.getElementById(this.getId() + 'TableBody');
			let tr = document.createElement('tr');
			tr.style.cursor = 'pointer';


			if(this.hasAttribute('selectable')) {
				let tdCheckBox = document.createElement('td');
				tdCheckBox.innerHTML = `<aon-checkbox id="aaa${body.children.length}"> </aon-checkbox>`;
				tr.appendChild(tdCheckBox);
			}

			this.columns.forEach((item, i) => {
				let td = document.createElement('td');
				td.innerHTML = value[item.id];
				td.addEventListener('click', fn);
				tr.appendChild(td);
			});
			body.appendChild(tr);
		}

		removeRows() {
			let body = document.getElementById(this.getId() + 'TableBody');
			body.innerHTML = '';
		}

		getId(){
			return this.getAttribute('id');
		}
}

window.customElements.define('aon-table', AonTable);
