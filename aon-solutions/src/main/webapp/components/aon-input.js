      class AonInput extends HTMLElement {

        static get observedAttributes() {
          return ['value', 'disabled', 'readonly', 'visible', 'options'];
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

        get disabled() {
          return this.getAttribute('disabled');
        }

        set disabled(disabled) {
          this.setAttribute('disabled', disabled);
        }

        get filled() {
          return this.getAttribute('filled');
        }

        set filled(filled) {
          this.setAttribute('filled', filled);
        }

        get options() {
    			return this.getAttribute('options');
    		}

    		set options(options) {
    			this.setAttribute('options', options);
    		}

        attributeChangedCallback(name, oldValue, newValue) {
          //console.log(`attribute ${name} change!! ${newValue}`);
          if('value' === name) {
            let input = document.getElementById(this.getAttribute('id') + 'Input');
            if(this.isTypeList()) {
        			let options = this.hasAttribute('options') ? JSON.parse(this.getAttribute('options')) : [];
              options.forEach((item, i) => {
                if(item.value = newValue) {
                  input.value = item.name;
                }
              });
            } else {
              if(newValue && 'undefined' !== newValue && input) input.value = newValue;
            }
          }

          if('disabled' === name){
            document.getElementById(this.getAttribute('id') + 'Input').setAttribute('disabled', this.isDisabled());
          }

          if('readonly' === name){
            document.getElementById(this.getAttribute('id') + 'Input').setAttribute('readonly', this.isReadonly());
          }

          if('visible' === name){
            if(!this.isVisible()){
              this.style.width = '0px';
              this.style.display = 'none !important';
            } else {
              this.style.width = null;
              this.style.display = 'block';
            }
          }

          if('filled' === name) {
            let label = document.getElementById(this.getAttribute('id') + 'Label');
            label.className = this.isFilled() ? 'omrs-input-filled' : 'omrs-input-underlined';
          }

          if('options' === name) {
              this.buildOptions();
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
          div.className = 'omrs-input-group';
          div.style.width = '100%';
          this.appendChild(div);

          let label = document.createElement('label');
          label.id = this.getAttribute('id') + 'Label';
          label.className = this.isFilled() ? 'omrs-input-filled' : 'omrs-input-underlined';
          label.style.marginBottom = '0px';
          label.style.width = '100%';

          let input = document.createElement('input');
          input.required = true;
          input.id = this.getAttribute('id') + 'Input';
          input.value = this.getAttribute('value') ? this.getAttribute('value') : '';
          input.type = this.getAttribute('type') && !this.isTypeList() ? this.getAttribute('type') : 'text';
          if('date' === this.getAttribute('type')){
            this.style.minWidth = '150px';
          }
          if(this.isDisabled())
            input.disabled = true;
          if(this.isReadonly() || this.isTypeList())
            input.readonly = true;

          input.addEventListener('change', () => {
            this.setAttribute('value', document.getElementById(input.getAttribute('id')).value);
          });

          label.appendChild(input);

          let span = document.createElement('span');
          span.className = 'omrs-input-label';
          span.id = this.getAttribute('id') + 'Description';
          span.innerHTML = this.getAttribute('description');

          label.appendChild(span);

          // if('password' === this.getAttribute('type')){
          //   let icon = document.createElement('i');
    			// 	icon.setAttribute('id', this.getAttribute('id') + 'Icon');
    			// 	icon.className =  'material-icons';
    			// 	icon.innerHTML = 'visibility'; //'visibility_off'
    			// 	label.appendChild(icon);
          // }

          if(!this.isVisible()){
            this.style.width = '0px';
            this.style.display = 'none !important';
          }

          div.appendChild(label);

          if(this.isTypeList()) {
            let iconLabel = document.createElement('label');
            iconLabel.style.position = 'absolute';
            iconLabel.style.top = '5px';
      			iconLabel.style.right = '0px';
            iconLabel.style.marginBottom = '0px';
      			iconLabel.setAttribute('id', this.getAttribute('id') + 'Icon');
      			iconLabel.setAttribute('for', input.getAttribute('id'));
            iconLabel.innerHTML = `<aon-icon-button id="${this.getAttribute('id') + 'IconLabel'}" icon="arrow_drop_down" noHover="true"></aon-icon-button>`;
            div.appendChild(iconLabel);

            let span = document.createElement('span');
            span.style.width = '100%';
            span.setAttribute('id', this.getAttribute('id') + 'Span');
            div.appendChild(span);
          }
        }

        buildOptions() {
          let span = document.getElementById(this.getAttribute('id') + 'Span');
          span.innerHTML = "";

    			let options = this.hasAttribute('options') ? JSON.parse(this.getAttribute('options')) : [];
          let div = document.createElement('div')
          div.id = this.getAttribute('id') + 'Options';
          div.className = 'aonInputListOptions';
          span.appendChild(div);


          if(options.length === 0) return div;

    			let ul = document.createElement('ul');
          ul.className = 'aonInputListOptionsUl';
        	ul.setAttribute('for', this.getAttribute('id') + 'Icon');
    			for(let i = 0; i < options.length; i++) {
    				let li = document.createElement('li');
            li.className = 'aonInputListOptionsItem'
    				li.innerHTML = options[i].name;
    				li.addEventListener('click', (e) => {
              div.classList.remove('is-visible');
    					this.value = options[i].value;
              let input = document.getElementById(this.getAttribute('id') + 'Input');
              input.value = options[i].name;
    			    this.dispatchEvent(new Event('select'));
    				});
    				ul.appendChild(li);
    			}
          div.appendChild(ul)

          let button = document.getElementById(this.getAttribute('id') + 'IconLabel');
          button.addEventListener('click', (event) => {
            event.stopPropagation();
            let el = document.getElementById(this.getAttribute('id') + 'Options');
            if(el.classList.contains('is-visible')) {
              el.classList.remove('is-visible');
            } else el.classList.add('is-visible');
          });

          document.addEventListener('click', function(event) {
            var isClickInside = div.contains(event.target);
            if(!isClickInside){
              if(div.classList.contains('is-visible')){
                div.classList.remove('is-visible');
              }
            }
          });
        }


        onChange(fn){
          let input = document.getElementById(this.getAttribute('id') + 'Input');
          input.addEventListener('change', fn);
        }

        isTypeList() {
          return this.hasAttribute('type') && this.getAttribute('type') === 'list';
        }

        isVisible(){
          return !this.hasAttribute('visible') || (this.hasAttribute('visible') && 'false' !== this.getAttribute('visible'));
        }

        setVisible(visible){
          this.setAttribute('visible', visible);
        }

        isReadonly(){
          return this.hasAttribute('readonly') && this.getAttribute('readonly')
            && 'false' !== this.getAttribute('readonly')
        }

        setReadonly(readonly){
          this.setAttribute('readonly', readonly);
        }

        isDisabled(){
          return this.hasAttribute('disabled') && 'false' !== this.getAttribute('disabled')
        }

        setDisabled(disabled){
          this.setAttribute('disabled', disabled);
        }

        isFilled(){
          return this.hasAttribute('filled') && 'false' !== this.getAttribute('filled')
        }

        setFilled(filled){
          this.setAttribute('filled', filled);
        }
      }

      window.customElements.define('aon-input',  AonInput);
