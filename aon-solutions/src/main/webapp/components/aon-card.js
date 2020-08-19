(function() {

	class AonCard extends HTMLElement {

		static get observedAttributes() {
			return ['visible'];
		}

		get id() {
			return this.getAttribute('id');
		}

		set id(id) {
			this.setAttribute('id', id);
		}

		get title() {
			return this.getAttribute('title');
		}

		set title(title) {
			this.setAttribute('title', title);
		}

		get visible() {
			return this.getAttribute('visible');
		}

		set visible(visible) {
			this.setAttribute('visible', visible);
		}

		attributeChangedCallback(name, oldValue, newValue) {
			if('visible' === name){
				if(this.getAttribute('visible') != undefined && 'false' == this.getAttribute('visible')){
					this.style.display = 'none';
				} else {
					this.style.display = 'block';
				}
			}
		}

		constructor () {
			super();
		}

		connectedCallback () {
			this.appendChild(this.build());
		}s

		build() {
			let div = document.createElement('div');
			div.setAttribute('id', this.getAttribute('id') + '-div');
			div.className = "demo-card-wide mdl-card mdl-shadow--2dp aonCard";

			let div1 = document.createElement('div');
			div1.className = "mdl-card__title";

			let h2 = document.createElement('h2');
			h2.className = "mdl-card__title-text";
			h2.innerHTML = this.getAttribute('title');

			div1.appendChild(h2);
			div.appendChild(div1);

			return div;
		}

		addContent(content) {
			let div = document.getElementById(this.getAttribute('id') + '-div');
			let div2 = document.createElement('div');
			div2.innerHTML = content;
			div.appendChild(div2);
		}

		setVisible(visible) {
			this.setAttribute('visible', visible)
		}
	}

	window.customElements.define('aon-card',  AonCard);

})();
