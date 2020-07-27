import '../services/country.js';
import './aon-select.js';

(function() {

	class AonAddress extends HTMLElement {

		static get observedAttributes() {
      		return ['value'];
   		}

		get id() {
			return this.getAttribute('id');
		}

		set id(id) {
			this.setAttribute('id', id);
		}

		get value() {
			return this.getAttribute('value');
		}

		set value(value) {
			this.setAttribute('value', value);
		}

		get description() {
			return this.getAttribute('description');
		}

		set description(description) {
			this.setAttribute('description', description);
		}

		get visible() {
			return this.getAttribute('visible');
		}

		set visible(visible) {
			this.setAttribute('visible', visible);
		}

		get readonly() {
			return this.getAttribute('readonly');
		}

		set readonly(readonly) {
			this.setAttribute('readonly', readonly);
		}

		attributeChangedCallback(name, oldValue, newValue) {
			if('value' === name) {
				let value = this.hasAttribute('value') ? JSON.parse(this.getAttribute('value')) : {
						country: 'ES',
						address: '',
						zip: '',
						city: '',
						province: ''
				};
				console.log(JSON.stringify(value));
				let val = `${value.address}, ${value.zip} ${value.city}, ${value.province}, ${value.country}`;
				document.getElementById(this.getAttribute('id') + 'Input').value = val;

				componentHandler.upgradeDom();

				var textField = document.getElementById(this.getAttribute('id') + 'TextField');
				textField.MaterialTextfield.checkDirty();
			}

		}

		constructor () {
			super();
			this.build();
		}

		connectedCallback () {

		}

		build() {
			let div = document.createElement('div');
			div.setAttribute('id', this.getAttribute('id') + 'TextField');
			div.className = "mdl-textfield mdl-js-textfield mdl-textfield--floating-label";
			div.style.width = '100%';
			if(this.getAttribute('visible') != undefined && 'false' == this.getAttribute('visible')){
				this.style.width = '0px';
				this.style.display = 'none';
			}
			let value = this.hasAttribute('value') ? JSON.parse(this.getAttribute('value')) : {
			 		country: 'ES',
			 		address: '',
			 		zip: '',
			 		city: '',
			 		province: ''
			};
			let input = document.createElement('input');
			input.className = "mdl-textfield__input";
			input.setAttribute('id', this.getAttribute('id') + 'Input');
			input.setAttribute('type', 'text');
			input.setAttribute('value',  `${value.address}, ${value.zip} ${value.city}, ${value.province}, ${value.country}`);
			input.setAttribute('readonly', 'readonly');

			let label = document.createElement('label');
			label.className = "mdl-textfield__label";
			label.setAttribute('id', this.getAttribute('id') + 'Label');
			label.setAttribute('for', input.getAttribute('id'));
			label.innerHTML = this.getAttribute('description');

			let icon = document.createElement('i');
			icon.className = "material-icons";
			icon.innerHTML = "room";

			let iconLabel = document.createElement('label');
			iconLabel.className = "mdl-button mdl-js-button mdl-button--icon";
			iconLabel.style.right = "0px";
			iconLabel.setAttribute('id', this.getAttribute('id') + 'Icon');
			iconLabel.setAttribute('for', input.getAttribute('id'));
			iconLabel.appendChild(icon);
			iconLabel.addEventListener('click', () => {
				let divEdit = document.getElementById(this.getAttribute('id') + 'Edit');
				if(divEdit.style.display === 'block'){
					divEdit.style.display = 'none';
				} else {
					divEdit.style.display = 'block';
				}
			});
			div.appendChild(input);
			div.appendChild(iconLabel);
			div.appendChild(label);

			this.appendChild(div);
			this.buildEdit(value);
		}

		buildEdit(value) {
			let div = document.createElement('div');
			div.setAttribute('id', this.getAttribute('id') + 'Edit');
			div.style.width = '100%';
			div.style.display = 'none';
			let html = `
				<aon-input-text class='aon-width-100' id='${this.getAttribute('id') + 'Address'}' description='Dirección'></aon-input-text>
				<aon-input-text class='aonWidth25' id='${this.getAttribute('id') + 'Zip'}' description='Código Postal'></aon-input-text>
				<aon-input-text class='aonWidth25' id='${this.getAttribute('id') + 'City'}' description='Ciudad'></aon-input-text>
				<aon-input-text class='aonWidth25' id='${this.getAttribute('id') + 'Province'}' description='Provincia'></aon-input-text>
				<aon-select class='aonWidth25' id='${this.getAttribute('id') + 'Country'}' description='País' ></aon-select>
			`;

			div.innerHTML = html;
			this.appendChild(div);

			let address = document.getElementById(this.getAttribute('id') + 'Address');
			if(address) {
				address.value = value.address;
				address.addEventListener('change', () => this.updateAddress());
			}

			let zip = document.getElementById(this.getAttribute('id') + 'Zip')
			if(zip) {
				zip.value = value.zip;
				zip.addEventListener('change', () => this.updateZip());
			}

			let city = document.getElementById(this.getAttribute('id') + 'City');
			if(city) {
				city.value = value.city;
				city.addEventListener('change', () => this.updateCity());
			}
			let province = document.getElementById(this.getAttribute('id') + 'Province');
			if(province) {
				province.value = value.province;
				province.addEventListener('change', () => this.updateProvince());
			}

			let country = document.getElementById(this.getAttribute('id') + 'Country');
			if(country) {
				country.options = JSON.stringify(getCountries().map(c => {return {value: c.iso2, name: c.nombre};}));
				country.value = value.country;
				country.addEventListener('select', () => this.updateCountry());
			}
		}

		updateAddress() {
			if(this.hasAttribute('value')){
				let address = document.getElementById(this.getAttribute('id') + 'Address');
				let value = JSON.parse(this.getAttribute('value'));
				value.address = address.value;
				this.value = JSON.stringify(value);
			}
		}

		updateZip() {
			if(this.hasAttribute('value')){
				let zip = document.getElementById(this.getAttribute('id') + 'Zip');
				let value = JSON.parse(this.getAttribute('value'));
				value.zip = zip.value;
				this.value = JSON.stringify(value);
			}
		}

		updateCity() {
			if(this.hasAttribute('value')) {
				let city = document.getElementById(this.getAttribute('id') + 'City');
				let value = JSON.parse(this.getAttribute('value'));
				value.city = city.value;
				this.setAttribute('value', JSON.stringify(value));
			}
		}

		updateProvince() {
			if(this.hasAttribute('value')){
				let province = document.getElementById(this.getAttribute('id') + 'Province');
				let value = JSON.parse(this.getAttribute('value'));
				value.province = province.value;
				this.setAttribute('value', JSON.stringify(value));
			}
		}

		updateCountry() {
			if(this.hasAttribute('value')){
				let country = document.getElementById(this.getAttribute('id') + 'Country');
				let value = JSON.parse(this.getAttribute('value'));
				value.country = country.value;
				this.setAttribute('value', JSON.stringify(value));
			}
		}

	}

	window.customElements.define('aon-address',  AonAddress);

})();
