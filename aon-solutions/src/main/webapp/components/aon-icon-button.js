(function() {

	class AonIconButton extends HTMLElement {

		static get observedAttributes() {
			return ['disabled', 'visible'];
		}

		get id() {
			return this.getAttribute('id');
		}

		set id(id) {
			this.setAttribute('id', id);
		}

		get icon() {
			return this.getAttribute('icon');
		}

		set icon(icon) {
			this.setAttribute('icon', icon);
		}

		get color() {
			return this.getAttribute('color');
		}

		set color(color) {
			this.setAttribute('color', color);
		}

		get outline() {
			return this.getAttribute('outline');
		}

		set outline(outline) {
			this.setAttribute('outline', outline);
		}

		get noHover() {
			return this.getAttribute('noHover');
		}

		set noHover(noHover) {
			this.setAttribute('noHover', noHover);
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

		attributeChangedCallback(name, oldValue, newValue) {


			if('disabled' === name){
				document.getElementById(this.getAttribute('id') + 'IconButton').setAttribute('disabled', newValue);
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
			let button = document.createElement('button');
			button.setAttribute('id', this.getAttribute('id') + 'IconButton');
			button.className = "aon-icon-button";
			button.style.color = this.getAttribute('color') ? this.getAttribute('color') : '#5f6368';

			if(this.getAttribute('disabled')){
					button.setAttribute('disabled', true);
			}

			if(this.getAttribute('visible') != undefined && 'false' == this.getAttribute('visible')){
				this.style.width = '0px';
				this.style.display = 'none';
			}

			if(!this.getAttribute('noHover')) {
				button.addEventListener('mouseover', () => {
					button.style.backgroundColor = '#f1f1f1';
					button.style.color = 'black';
				});

				button.addEventListener('mouseleave', () => {
					button.style.backgroundColor = 'white';
					button.style.color = this.getAttribute('color') ? this.getAttribute('color') : '#5f6368';
				});
			}

			this.addEventListener('click', () => {
				button.style.backgroundColor = '#ddd';
			});

			let icon = document.createElement('i');
			icon.className = "material-icons";
			icon.innerHTML = this.getAttribute('icon');
			button.appendChild(icon);
			return button;
		}
	}

	window.customElements.define('aon-icon-button',  AonIconButton);

})();
