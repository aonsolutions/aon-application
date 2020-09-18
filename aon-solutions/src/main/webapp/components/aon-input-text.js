      class AonInputText extends HTMLElement {

        static get observedAttributes() {
          return ['value', 'disabled', 'readonly', 'visible'];
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

        attributeChangedCallback(name, oldValue, newValue) {
          //console.log(`attribute ${name} change!! ${newValue}`);
          if('value' === name) {
            let input = document.getElementById(this.getAttribute('id') + 'Input');
            if(newValue && input) input.value = newValue;
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
              this.style.display = 'none';
            } else {
              this.style.width = null;
              this.style.display = 'block';
            }
          }

          if('filled' === name) {
            let label = document.getElementById(this.getAttribute('id') + 'Label');
            label.className = this.isFilled() ? 'omrs-input-filled' : 'omrs-input-underlined';
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
          div.className = 'omrs-input-group';
          div.style.width = '100%';

          let label = document.createElement('label');
          label.id = this.getAttribute('id') + 'Label';
          label.className = this.isFilled() ? 'omrs-input-filled' : 'omrs-input-underlined';
          label.style.width = '100%';

          let input = document.createElement('input');
          input.required = true;
          input.id = this.getAttribute('id') + 'Input';
          input.value = this.getAttribute('value') ? this.getAttribute('value') : '';
          input.type = this.getAttribute('type') ? this.getAttribute('type') : 'text';
          if('date' === this.getAttribute('type')){
            input.style.paddingTop = '1px';
            input.style.paddingBottom = '1px';
          }
          if(this.isDisabled())
            input.disabled = true;
          if(this.isReadonly())
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
            this.style.display = 'none';
          }

          div.appendChild(label);
          return div;
        }

        onChange(fn){
          let input = document.getElementById(this.getAttribute('id') + 'Input');
          input.addEventListener('change', fn);
        }

        isVisible(){
          return !this.hasAttribute('visible') || (this.hasAttribute('visible') && 'false' !== this.getAttribute('visible'));
        }

        setVisible(visible){
          this.setAttribute('visible', visible);
        }

        isReadonly(){
          return this.hasAttribute('readonly') && 'false' !== this.getAttribute('readonly')
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

      window.customElements.define('aon-input-text',  AonInputText);
