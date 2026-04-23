import {AonElement} from './AonElement.js';
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from '../environments/environments.js';
import { AonInput } from './aon-input.js';
import { AonCheckbox } from './aon-checkbox.js';
import { AonNewInput } from './aon-new-input.js';

export class AonNewSelect extends AonNewInput {

  OPTIONS;
  detail;
  _selected;

  valueAlias;
  nameAlias;
  disableKeyUp = true;
  selectable;
  static get observedAttributes() {
    return [CONSTANT.VALUE, CONSTANT.OPTIONS, CONSTANT.DISABLED];
  }


  get multiple() {
    return this.getAttribute("multiple");
  }

  set multiple(multiple) {
    this.setAttribute("multiple", multiple);
  }

  get options() {
    return this.getAttribute(CONSTANT.OPTIONS);
  }

	set options(options) {
		this.setAttribute(CONSTANT.OPTIONS, options);
	}

  get autocomplete() {
    return this.getAttribute(CONSTANT.AUTOCOMPLETE);
  }

  set autocomplete(autocomplete) {
    this.setAttribute(CONSTANT.AUTOCOMPLETE, autocomplete);
  }

  get emptyclear() {
    return this.getAttribute("emptyclear");
  }

  set emptyclear(emptyclear) {
    this.setAttribute("emptyclear", emptyclear);
  }

  attributeChangedCallback(name, oldValue, newValue) {
    if(CONSTANT.VALUE === name) {
      let options = this.hasAttribute(CONSTANT.OPTIONS) ? JSON.parse(this.getAttribute(CONSTANT.OPTIONS)) : [];
      this.detail = {};
      if(this.default && '' === newValue) {
        let input = this.getElement(this.INPUT);
        if(input) {
          input.value = '-';
        }
      } else {
        options.forEach((item, i) => {
          if(item[this.valueAlias] == newValue) {
            let input = this.getElement(this.INPUT);
            if(input) input.value = item[this.nameAlias];
          }
        });
      }
      if(options.length > 0)
        this.detail =  options.find(v => v[this.valueAlias] == newValue);
      this.dispatchEvent(new CustomEvent(EVENT.CHANGE,{detail: this.detail || {} }));
    } else if(CONSTANT.DISABLED === name){
      if(CONSTANT.TRUE == this.disabled){
        let input = this.getElement(this.INPUT);
        if(input) input.disabled = true;
      } else {
        let input = this.getElement(this.INPUT);
        if(input) input.disabled = false;
      }
    }
  }

  constructor () {
    super();
  }

  connectedCallback () {
    this.initialize();
    this.build();
    this.buildSelect();
  }

  initialize() {
    super.initialize();
    this.OPTIONS = this.id + CONSTANT.OPTIONS;
    this.valueAlias = this.valueAlias || 'value';
    this.nameAlias = this.nameAlias || 'name';
    this.selectable = this.selectable || [];
  }

  buildSelect() {
    let input = this.getElement(this.INPUT);
    if(input){
      input.readonly = this.isReadonly();
      if(!this.hasAttribute(CONSTANT.AUTOCOMPLETE)) {
        input.setAttribute(CONSTANT.READONLY, true);
      }
      this.addIcon(MATERIAL_ICONS.ARROW_DROP_DOWN, undefined, () => this.showOptions());

      input.addEventListener(EVENT.CLICK, () => this.showOptions());

      const emptyclear = this.hasAttribute("emptyclear");

      input.addEventListener(EVENT.BLUR, ()=>{
        const value = input.value;
        const exists = this.getOptions().some(opt => opt[this.nameAlias] == value);
        if(!exists && !emptyclear ){
          const option = this.getOptions().find(f => f[this.valueAlias] == this.value);
          if(option)
            input.value = option[this.nameAlias];
        } else if(!value && emptyclear){
          this.clear();
        }
      });

      if(this.disabled)
        input.disabled = true;
      
      if(this.readonly && this.readonly == "true")
        input.readonly = true;

      let rootDiv = this.getElement(this.ROOT);
      let span = this.createSpan();
      span.id = this.id + "OptionsSpan";
      rootDiv.appendChild(span);
  
      let optionsDiv = this.createDiv();
      optionsDiv.id = this.OPTIONS;
      optionsDiv.className = 'aonInputListOptions';
      span.appendChild(optionsDiv);
  
      let opts = this.hasAttribute(CONSTANT.OPTIONS) ? JSON.parse(this.getAttribute(CONSTANT.OPTIONS)) : [];
      opts.forEach((item, i) => {
        if(item[this.valueAlias] == this.value) {
          this.getElement(this.INPUT).value = item[this.nameAlias];
        }
      });

      input.addEventListener(EVENT.INPUT, ({target})=>{
        if(this.disableKeyUp) {
          let val = target.value.toUpperCase();
          let optios = this.getOptions();
          if (val) {
            optios = this.getOptions().filter(opt => {
              return opt[this.nameAlias].toUpperCase().includes(val) || this.checkSelectable(opt);
            })
          }
          this.buildOptions(optios);
        }
      });
                  
      let keys = ["ArrowUp", "ArrowRight", "ArrowDown", "ArrowLeft", "Enter"];
      input.addEventListener(EVENT.KEYDOWN, (ev) => {
        let key = ev.key;
        if(keys.includes(key)){
          ev.preventDefault();
          ev.stopPropagation();
          this.keyboardSelected(ev);
        } 
      });
      if (this.multiple) {
        this.displayMultiple();
      }
    }
  }

  buildOptions(options) {
    this.clearElementById(this.OPTIONS);

    if(options.length === 0) return null;

    let input = this.getElement(this.INPUT);
    let div = this.getElement(this.OPTIONS);
    div.classList.add('is-visible');
    div.style.width = this.getBoundingClientRect().width;
    if(this.default ||  this.hasAttribute(CONSTANT.DEFAULT)) {
      let empty = {};
      empty[this.nameAlias] = '-';
      empty[this.valueAlias] = '';
      options.unshift(empty); //EMPTY
    }

    let ul = this.createElement(TAG.UL);
    ul.classList.add(CSS.AON_UL_OPTIONS);
    ul.classList.add(CSS.AON_INPUT_LIST_OPTIONS_UL);
    ul.setAttribute('for', this.getAttribute(CONSTANT.ID) + 'Icon');
    div.appendChild(ul);

    const isMultiple = this.multiple;
    for (const option of options) {
      isMultiple ? this.buildLiMultiple(option, ul) : this.buildLi(option, ul, div);
    }

    let icon = this.getElement(this.ICON_BUTTON);
    document.addEventListener(EVENT.CLICK, function(event) {
      this.value = this._selected ? this._selected[this.nameAlias] : '';
      let isClickInside = input.contains(event.target) || icon.contains(event.target);
      if(!isClickInside){
        if(div.classList.contains('is-visible')){
          div.classList.remove('is-visible');
        }
      }
    });
  }

  buildLi(option, ul, div){
    let input = this.getElement(this.INPUT);

    let li = this.createElement(TAG.LI);
    li.className = 'aonInputListOptionsItem';
    li.innerHTML = option[this.nameAlias];
    li.setAttribute(CONSTANT.VALUE, option[this.valueAlias]);
    ul.appendChild(li);

    li.addEventListener(EVENT.CLICK, () => {
      div.classList.remove('is-visible');
      this.value = option[this.valueAlias];
      input.value = option[this.nameAlias];
      this._selected = option;
      this.dispatchEvent(new CustomEvent(EVENT.SELECT, {detail: option}));
    });

    return li;
  }

  buildLiMultiple(option, ul){

    const valueAlias = option[this.valueAlias];
    if(this.isBetaDoc()){
      var checkBoxId = `${this.id}_checkbox_${valueAlias}`;
    }else{
      var checkBoxId = "checkbox"+valueAlias;
    }

    let checkbox = this.getElement(checkBoxId);
    if(checkbox) return;

    let li = this.createElement(TAG.LI);
    li.className = 'aonInputListOptionsItem';
    li.style.display = "flex";
    li.style.textAlign = "initial";
    li.setAttribute(CONSTANT.VALUE, valueAlias);
    ul.appendChild(li);

    if(valueAlias){
      checkbox =  new AonCheckbox();
      if(this.isBetaDoc()){
        checkbox.name = checkBoxId;
      }else{
        checkbox.name = "checkbox"+valueAlias;
      }
      checkbox.id = checkBoxId;
      li.appendChild(checkbox);
      checkbox.value = this.isSelectable(option);

      checkbox.addEventListener(EVENT.CHANGE, ()=>{
        let check = checkbox.getValue();
        if(check){
          this.addSelectable(option);
        } else {
          this.removeSelectable(option);
        }
        this.dispatchEvent(new Event(EVENT.CHANGE));
      });
    }
  

    let span = this.createElement(TAG.SPAN);
    span.innerHTML = option[this.nameAlias];
    span.style.fontSize = "12px";
    li.appendChild(span);

    li.addEventListener(EVENT.CLICK, (ev) => {
      ev.preventDefault();
      ev.stopPropagation();
      if(checkbox){
        checkbox.value = !checkbox.getValue();
      }
    });
  }

  onChangeCheckBox(add=false, option=null){
    const selectable = this.getSelectable();
    this.displayMultiple();

    this.dispatchEvent(new CustomEvent(EVENT.SELECT, {
      detail: {
        selectable, 
        add,
        option
      }
    }));
  }

  displayMultiple() {
    const selectable  = this.getSelectable();
    const length      = selectable.length;
    
    if(length > 0){
      super.setValue(selectable.map(item => item[this.nameAlias]).join(', '));
    } else {
      super.setValue('');
    }
  }

  keyboardSelected({key}){
    const options = this.getElement(this.OPTIONS);
    let items = options.querySelectorAll('li');

    if (key === "ArrowDown") {//down.
        let index = -1;
        for(let i = 0; i < items.length; i++){
          if(items[i].classList.contains('is-selected')){
            index = i;
            break;
          }
        }
        if(index < items.length - 1){
          if(index >= 0){
            items[index].classList.remove('is-selected');
          }
          let selected = items[index + 1];
          selected.classList.add('is-selected');
          this.setValue(selected.getAttribute(CONSTANT.VALUE));
          // scroll center smooth
          selected.scrollIntoView({block: "center", behavior: "smooth"});
        }
    } else if (key === "ArrowUp") {
        let index = 0;
        for(let i = 0; i < items.length; i++){
          if(items[i].classList.contains('is-selected')){
            index = i;
            break;
          }
        }
        if(index > 0){
          items[index].classList.remove('is-selected');
          let selected = items[index - 1];
          selected.classList.add('is-selected');
          this.setValue(selected.getAttribute(CONSTANT.VALUE));
          // scroll center smooth
          selected.scrollIntoView({block: "center", behavior: "smooth"});
        }
    } else if (key === "Enter"){
      const isVisible = options.classList.contains('is-visible');
      if(isVisible){
        this.closeOptions();
      } else {
        this.showOptions();
      }
    }
  }

  getInput(){
    return this.getElement(this.INPUT);
  }

  showOptions(){
    if(!this.isReadonly() && !this.isDisabled()) {
      const optios = this.hasAttribute(CONSTANT.OPTIONS) && !this.getDisabled() ? JSON.parse(this.getAttribute(CONSTANT.OPTIONS)) : [];
      this.buildOptions(optios);
    }
  }

  closeOptions() {
    let div = this.getElement(this.OPTIONS);
    if(div.classList.contains('is-visible')){
      div.classList.remove('is-visible');
    }
  }

  setOptions(options) {
    this.setAttribute(CONSTANT.OPTIONS, JSON.stringify(options));
  }

  setOptionsBuild(options) {
    this.disableKeyUp = false;
    if(this.multiple && this.getSelectable().length){
      options = [ ...new Set(this.getSelectable()), ...new Set(options) ];
    }

    this.setOptions(options);
    this.buildOptions(options);
  }

  getOptions() {
    const options = this.options ? this.options : "[]";
    return JSON.parse(options);
  }

  checkSelectable(opt){
    try {
      return this.getSelectable().some(select =>  opt[this.nameAlias].toUpperCase().includes(select.name.toUpperCase()));
    } catch (error) { console.log(error); }
    return false;
  }
  
  getSelectable(){
    return this.selectable || [];
  }

  addSelectable(option){
    this.selectable.push(option);
    this.onChangeCheckBox(true, option);
  }

  addSelectableByValue(value){
    const opt = this.getOptions().find(d=> d.id == value);
    if(opt){
      this.addSelectable(opt);
    }
  }
  
  removeSelectable(option){
    this.selectable = this.selectable.filter(opt => opt.value !=option.value);
    this.onChangeCheckBox(false, option);
  }

  clearSelectable(){
    this.selectable = [];
    this.onChangeCheckBox(false);
  }

  isSelectable({value}){
    return this.selectable.some(p => p.value == value);
  }

  setEnumOptions(options) {
    let opts = [];
    for(let key in options) {
      opts.push({
        value: key,
        name: options[key]
      });
    }
    this.setAttribute(CONSTANT.OPTIONS, JSON.stringify(opts));
  }

  getValueObject() {
    return this.getOptions().filter(f => f[this.valueAlias] == this.value)[0];
  }

  getDisabled(){
    return CONSTANT.TRUE == this.disabled;
  }

  setDisabled(disabled) {
    this.disabled = disabled;
    let input = this.getElement(this.INPUT);
    input.disabled = disabled; 
  } 
  
  isReadonly() {
    return this.hasAttribute(CONSTANT.READONLY) && this.getAttribute(CONSTANT.READONLY)
      && CONSTANT.UNDEFINED !== this.getAttribute(CONSTANT.READONLY) && CONSTANT.FALSE !== this.getAttribute(CONSTANT.READONLY);
  }

  isDisabled() {
    return this.hasAttribute(CONSTANT.DISABLED) && this.getAttribute(CONSTANT.DISABLED)
      && CONSTANT.UNDEFINED !== this.getAttribute(CONSTANT.DISABLED) && CONSTANT.FALSE !== this.getAttribute(CONSTANT.DISABLED);
  }

  focus() {
    this.getElement(this.INPUT).focus();
  }

  clear(){
    this.value = "";
    let input = this.getElement(this.INPUT);
    if(input) input.value = "";
    // limpiar los datos del multiple
    if(this.multiple){
      this.clearSelectable();
    }
  }

  getText() {
    const input = this.getElement(this.INPUT);
    if(!input) return null;
    return input.value;
  }

  /**
   * 
   * @param {Boolean} load
   */
  loading(load){
    const input = this.getElement(this.INPUT);
    if(input){
      input.loading?.(load);
    }
  }

  getDetail(){
    return this.detail || {};
  }

  setAlias(valueAlias, nameAlias) {
    this.valueAlias = valueAlias;
    this.nameAlias = nameAlias;
  }

  setIndexOf(idx){
    if(idx>=0){
      let options = this.getOptions();
      if(options.length){
        const option = options[idx];
        if(option){
          this.value = option.value;
        }
      }
    } else {
      this.clear();
    }
  }
  
  setValueObject(object) {
    if(object && object[this.valueAlias] !== undefined) {
      this.setValue(object[this.valueAlias]);
    }
  }

  setValue(value){
    if(value && value!=0){
      let options = this.getOptions();
      if(options.length){
        const option = options.find(opt => opt[this.valueAlias] == value);
        if(option){
          this.value = option[this.valueAlias];
        }
      }
    } else {
      this.clear();
    }
  }
  
  setValueZero(value) {
	  if (value !== null && value !== undefined) {
	    const options = this.getOptions();
	
	    if (options?.length) {
	      const option = options.find(opt => opt[this.valueAlias] == value);
	      if (option) {
	        this.value = option[this.valueAlias];
	      }
	    }
	  } else {
	    this.clear();
	  }
	}

  setValueAlias(valueAlias) {
    this.valueAlias = valueAlias;
  }

  setNameAlias(nameAlias) {
    this.nameAlias = nameAlias;
  }
}
if(!window.customElements.get(TAG.AON_NEW_SELECT)){
  window.customElements.define(TAG.AON_NEW_SELECT, AonNewSelect);
}
