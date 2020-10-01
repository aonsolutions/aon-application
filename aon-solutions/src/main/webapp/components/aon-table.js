
(function() {
	class AonTable extends HTMLElement {

		columns = [];

		get id() {
			return this.getAttribute('id');
		}

		set id(id) {
			this.setAttribute('id', id);
		}


		constructor () {
			super();
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
		}

		addColumn(name, type, id) {
			let header = document.getElementById(this.getId() + 'TableHeader');
			let th = document.createElement('th');
			// if('number' !== type ){
			// 	//th.className = 'mdl-data-table__cell--non-numeric';
			// }
			th.innerHTML = name;
			this.columns.push({
				name,
				type,
				id
			});
			header.appendChild(th);
		}

		addRow(value, fn) {
			let body = document.getElementById(this.getId() + 'TableBody');
			let tr = document.createElement('tr');
			tr.style.cursor = 'pointer';
			tr.addEventListener('click', fn);

			this.columns.forEach((item, i) => {
				let td = document.createElement('td');
				// if('number' !== item.type ){
				// 	//td.className = 'mdl-data-table__cell--non-numeric';
				// }
				td.innerHTML = value[item.id];
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
})();
