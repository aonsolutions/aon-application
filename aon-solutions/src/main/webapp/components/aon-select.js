(function() {

	class AonSelect extends HTMLElement {

		static get observedAttributes() {
      		return ['value', 'options', 'readonly'];
   		}

		get id() {
			return this.getAttribute('id');
		}

		set id(id) {
			this.setAttribute('id', id);
		}

		get options() {
			return this.getAttribute('options');
		}

		set options(options) {
			this.setAttribute('options', options);
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
			if('options' === name) {
				let span = document.getElementById(this.getAttribute('id') + 'Span');
				if(span){
					span.innerHTML = "";
					span.appendChild(this.buildOptions());
				}
			}

			if('value' === name) {
				let options = this.hasAttribute('options') ? JSON.parse(this.getAttribute('options')) : [];
				let arr = options.filter(f => f.value === newValue);
				let val = arr.length > 0 ? arr[0].name : '';
				document.getElementById(this.getAttribute('id') + 'Select').value = val;

				componentHandler.upgradeDom();

				var textField = document.getElementById(this.getAttribute('id') + 'TextField');
				textField.MaterialTextfield.checkDirty();
			}

			if('readonly' === name) {
				let span = document.getElementById(this.getAttribute('id') + 'Span');
				span.innerHTML = "";
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

			let input = document.createElement('input');
			input.className = "mdl-textfield__input";
			input.setAttribute('id', this.getAttribute('id') + 'Select');
			input.setAttribute('type', 'text');
			input.setAttribute('value',  this.getAttribute('value') ? this.getAttribute('value') : '' );
			input.setAttribute('readonly', 'readonly');

			let label = document.createElement('label');
			label.className = "mdl-textfield__label";
			label.setAttribute('id', this.getAttribute('id') + 'Label');
			label.setAttribute('for', input.getAttribute('id'));
			label.innerHTML = this.getAttribute('description');

			let icon = document.createElement('i');
			icon.className = "material-icons";
			icon.innerHTML = "arrow_drop_down";

			let iconLabel = document.createElement('label');
			iconLabel.className = "mdl-button mdl-js-button mdl-button--icon";
			iconLabel.style.right = "0px";
			iconLabel.setAttribute('id', this.getAttribute('id') + 'Icon');
			iconLabel.setAttribute('for', input.getAttribute('id'));

			iconLabel.appendChild(icon);

			div.appendChild(input);
			div.appendChild(iconLabel);
			div.appendChild(label);

			let span = document.createElement('span');
			span.setAttribute('id', this.getAttribute('id') + 'Span');
			span.style.position = "absolute";
			span.style.left = -(this.offsetWidth-32) + "px";
			span.style.top = "0px";

			if(!this.hasAttribute('readonly')){
				span.appendChild(this.buildOptions());
			}
			div.appendChild(span);
			this.appendChild(div);

		}

		buildOptions() {
			let options = this.hasAttribute('options') ? JSON.parse(this.getAttribute('options')) : [];
			if(options.length === 0) return document.createElement('div');
			let ul = document.createElement('ul');
			ul.style['max-height'] = "200px";
			ul.className = "mdl-menu mdl-js-menu mdl-js-ripple-effect aonScroll";
			ul.setAttribute('for', this.getAttribute('id') + 'Icon');
			for(let i = 0; i < options.length; i++) {
				let li = document.createElement('li');
				li.className = "mdl-menu__item";
				li.style.width = (this.offsetWidth-32) + "px"
				li.innerHTML = options[i].name;
				li.addEventListener('click', () => {
					document.getElementById(this.getAttribute('id') + 'Select').setAttribute('value', options[i].name);
					this.value = options[i].value;
					this.dispatchEvent(new Event('select'));
					document.getElementById(this.getAttribute('id') + 'TextField').MaterialTextfield.checkDirty();
				})
				ul.appendChild(li);
			}
			return ul;
		}
	}

	window.customElements.define('aon-select',  AonSelect);

})();
