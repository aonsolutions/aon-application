(function() {

	class AonCheckbox extends HTMLElement {

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

		get disabled() {
			return this.getAttribute('disabled');
		}

		set disabled(disabled) {
			this.setAttribute('disabled', disabled);
		}

		constructor () {
			super();
		}

		connectedCallback () {
			this.appendChild(this.build());
			componentHandler.upgradeDom();
		}

		build() {
			if(this.hasAttribute('visible') && 'false' == this.getAttribute('visible')){
				this.style.width = '0px';
				this.style.display = 'none';
			}

			let input = document.createElement('input');
			input.className = "mdl-checkbox__input";
			input.setAttribute('id', this.getAttribute('id') + 'Input');
			input.setAttribute('type', 'checkbox');
			if(this.hasAttribute('value') && "true" === this.getAttribute('value')){
				input.setAttribute('checked', 'checked');
			}
			if(this.hasAttribute('disabled')){
				input.setAttribute('disabled', 'disabled');
			}
			input.addEventListener('change', () => {
				this.setAttribute('value', document.getElementById(input.getAttribute('id')).checked);
			});

			let label = document.createElement('label');
			label.className = "mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect";
			label.setAttribute('for', input.getAttribute('id'));

			let span = document.createElement('span');
			span.className = "mdl-checkbox__label";
			span.innerHTML = this.getAttribute('description');

			label.appendChild(input);
			label.appendChild(span);

			return label;
		}
	}

	window.customElements.define('aon-checkbox',  AonCheckbox);

})();
