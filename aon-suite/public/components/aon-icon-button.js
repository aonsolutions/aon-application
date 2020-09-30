(function() {

	class AonIconButton extends HTMLElement {

		static get observedAttributes() {
			return ['disabled', 'visible', 'icon'];
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

		get image() {
			return this.getAttribute('image');
		}

		set image(image) {
			this.setAttribute('image', image);
		}

		get color() {
			return this.getAttribute('color');
		}

		set color(color) {
			this.setAttribute('color', color);
		}

		get outlined() {
			return this.getAttribute('outlined');
		}

		set outlined(outlined) {
			this.setAttribute('outlined', outlined);
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

			if('icon' === name){
				let icon = document.getElementById(this.getAttribute('id') + 'Icon');
				if(icon) icon.innerHTML = this.getAttribute('icon');
			}

			if('image' === name){
				let image = document.getElementById(this.getAttribute('id') + 'Image');
				if(image) image.src = this.getAttribute('image');
			}
		}

		constructor () {
			super();
		}

		connectedCallback () {
			this.appendChild(this.build());
		}

		build() {
			let button = document.createElement('button');
			button.setAttribute('id', this.getAttribute('id') + 'IconButton');
			button.className = "aonIconButton";
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
					button.style.backgroundColor = 'transparent';
					button.style.color = this.getAttribute('color') ? this.getAttribute('color') : '#5f6368';
				});

				this.addEventListener('click', () => {
					button.style.backgroundColor = '#ddd';
				});
			}

			if(this.hasAttribute('icon')){
				let icon = document.createElement('i');
				icon.setAttribute('id', this.getAttribute('id') + 'Icon');
				icon.className = this.getAttribute('outlined') ? 'material-icons-outlined' : 'material-icons';
				icon.innerHTML = this.getAttribute('icon');
				button.appendChild(icon);
			} else if(this.hasAttribute('image')) {
				let image = document.createElement('img');
				image.setAttribute('id', this.getAttribute('id') + 'Image');
				image.style.width = '24px';
				image.style.height = '24px';
				image.src = this.getAttribute('image');
				button.appendChild(image);
			}
			return button;
		}
	}

	window.customElements.define('aon-icon-button',  AonIconButton);

})();
