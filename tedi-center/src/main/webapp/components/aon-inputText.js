(function() {

	class AonInputText extends HTMLElement {

		static get observedAttributes() {
			return ['value', 'readonly', 'visible'];
		}

		get id() {
			return this.getAttribute('id');
		}

		set id(id) {
			this.setAttribute('id', id);
		}

		get type() {
			return this.getAttribute('type');
		}

		set type(type) {
			this.setAttribute('type', type);
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
			//console.log(`attribute ${name} change!! ${newValue}`);
			if('value' === name) {
				document.getElementById(this.getAttribute('id') + 'Text').value = newValue;

				componentHandler.upgradeDom();

				var textField = document.getElementById(this.getAttribute('id') + 'TextField');
				textField.MaterialTextfield.checkDirty();
			}

			if('readonly' === name){
				document.getElementById(this.getAttribute('id') + 'Text').setAttribute('readonly', 'readonly');
			}
			
			if('visible' === name){
				if(this.getAttribute('visible') != undefined && 'false' == this.getAttribute('visible')){
					this.style.width = '0px';
					this.style.display = 'none';
				} else {
					this.style.width = null;
					this.style.display = 'block';
				}
			
			}
		}

		constructor () {
			super();
			this.appendChild(this.build());
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

			let input = document.createElement('input');
			input.className = "mdl-textfield__input";
			input.setAttribute('id', this.getAttribute('id') + 'Text');
			input.setAttribute('value', this.getAttribute('value') ? this.getAttribute('value') : '');
			input.setAttribute('type', this.getAttribute('type') ? this.getAttribute('type') : 'text');
			if('date' === this.getAttribute('type')){
				input.style['padding-top'] = '1px';
				input.style['padding-bottom'] = '1px';
			}
			input.addEventListener('change', () => {
				this.setAttribute('value', document.getElementById(input.getAttribute('id')).value);
			});

			let label = document.createElement('label');
			label.className = "mdl-textfield__label";
			label.setAttribute('id', this.getAttribute('id') + 'Label');
			label.setAttribute('for', input.getAttribute('id'));
			label.innerHTML = this.getAttribute('description');

			div.appendChild(input);
			div.appendChild(label);

			return div;
		}
	}

	window.customElements.define('aon-input-text',  AonInputText);

})();
