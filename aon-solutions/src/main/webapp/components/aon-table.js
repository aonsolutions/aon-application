import {AonElement} from './AonElement.js';
import './aon-checkbox.js';
import './aon-dialog-menu.js';

export class AonTable extends AonElement {

		columns;
		selected;

		THEADER;
		TBODY;

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
			this.THEADER = this.id + 'TableHeader';
			this.TBODY = this.id + 'TableBody';
			this.selected = [];
		}


		connectedCallback () {
			this.innerHTML = `
				<aon-dialog-menu id="${this.getId() + 'aonDialogAddOption'}" ></aon-dialog-menu>
				<table class="aonTable">
					<thead>
						<tr id="${this.THEADER}" >

						</tr>
					</thead>
					<tbody id="${this.TBODY}">

					</tbody>
				</table>
			`;

			if(this.hasAttribute('selectable')) {
				let header = this.getElement(this.getId() + 'TableHeader');
				let th = document.createElement('th');
				th.style.width = '5%';
				th.innerHTML = '<aon-checkbox id="aonTableAllSelection"> </aon-checkbox>';
				header.appendChild(th);
				let ch = this.getElement('aonTableAllSelection');
				ch.addEventListener('change',() => {
					document.querySelectorAll('aon-checkbox').forEach((item, i) => {
						if(item.value != ch.value){
							let it = this.getElement(item.id + 'Input');
							it.click();
						}

					});
				});
			}


			let tbody = this.getElement(this.TBODY);

			let offset = tbody.getBoundingClientRect();
			tbody.style.height = `calc(100vh - ${offset.top + 2}px)`;

			tbody.addEventListener('scroll', () => {
				let scrollTop = tbody.scrollTop;
				let offsetHeight = tbody.offsetHeight;
				let physicalSize = tbody.scrollHeight;
				let maxScrollPosition = physicalSize - offsetHeight;
				if(scrollTop >= maxScrollPosition) {
					this.dispatchEvent(new CustomEvent('more'));
				}
			});
		}

		addColumn(name, type, id, width) {
			let header = this.getElement(this.getId() + 'TableHeader');
			let th = document.createElement('th');
			th.innerHTML = name;
			th.style.width = width;
			this.columns.push({name, type, id, width});
			header.appendChild(th);
		}

		addRow(value, fn) {
			let body = this.getElement(this.getId() + 'TableBody');
			if(!body) return true;
			let tr = document.createElement('tr');
			tr.style.cursor = 'pointer';
			body.appendChild(tr);

			if(this.hasAttribute('selectable')) {
				let tdCheckBox = document.createElement('td');
				tdCheckBox.style.width = '5%';
				tdCheckBox.innerHTML = `<aon-checkbox id="aaa${body.children.length}"> </aon-checkbox>`;
				tr.appendChild(tdCheckBox);
				let checkbox = this.getElement(`aaa${body.children.length}`);
				checkbox.addEventListener('change', () => {
					if(checkbox.getValue()) {
						this.selected.push(value);
					} else {
						this.selected.forEach((item, i) => {
							if(item == value) {
								this.selected.splice(i, 1);
							}
						});
					}
					this.dispatchEvent(new CustomEvent('select'));
				});
			}

			this.columns.forEach((item, i) => {
				let td = document.createElement('td');
				td.style.width = item.width;
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

			return tr;
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
