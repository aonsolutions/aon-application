import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import { AonNewInput } from './aon-new-input.js';
import { AonIconButton } from './aon-icon-button.js';
import { AonDateUtils } from '../modules/utils/AonDateUtils.js';

export class AonNewDate extends AonNewInput {
  activePicker;
  date;
  day;
  month;
  year;
  yearTotal = 20;
  today = new Date();
  INPUT;
  SPAN;
  DATEPICKER;
  DATEPICKER_PREVIOUS;
  DATEPICKER_NEXT;
  DATEPICKER_MONTH;
  DATEPICKER_YEAR;
  DATEPICKER_DAYS;
  startYear;
  YEARPICKER;
  MOTHPICKER;

  connectedCallback () {
    this.initialize();
    this.build();
    this.buildDate();
    this.buildDatepicker();
  }

  initialize() {
    super.initialize();
    this.id = this.id || 'aonDate';
    this.DATEPICKER = this.id + 'Datepicker';
    this.SPAN = this.DATEPICKER + 'Span';
    this.DATEPICKER_PREVIOUS = this.DATEPICKER + 'Previous';
    this.DATEPICKER_NEXT = this.DATEPICKER + 'Next';
    this.DATEPICKER_MONTH = this.DATEPICKER + 'Month';
    this.DATEPICKER_YEAR = this.DATEPICKER + 'Year';
    this.DATEPICKER_DAYS = this.DATEPICKER + 'Days';
    this.YEARPICKER = this.DATEPICKER + 'YearPicker';
    this.MONTHPICKER = this.MONTHPICKER + 'MonthPicker';
    this.getDate();
    // Si no tiene date, cogemos la fecha de hoy para montar el calendario
    this.day   = this.date ? this.date.getDate()    : this.today.getDate();
    this.month = this.date ? this.date.getMonth()   : this.today.getMonth();
    this.year  = this.date ? this.date.getFullYear(): this.today.getFullYear();
    this.startYear = this.date ? this.date.getFullYear(): this.today.getFullYear();
    this.startYear = this.startYear - (this.startYear % this.yearTotal);
    this.maxlength = 10;
  }

  getDate(){
    // Si le pasamos un date
    const dateAttr = this.getAttribute('date');
    if (dateAttr && dateAttr.trim() !== ''){
      // Si tiene fecha pasada
      const parsedDate = new Date(dateAttr);
      if (!isNaN(parsedDate.getTime())) {
        this.date = parsedDate;
      }
    } else if (dateAttr === null || dateAttr.trim() !== ''){
      // Si no se le pasa el valor date
      this.date  = this.date || this.today;
    }
    // 
    this.setType('text');
  }

  buildDate() {
    let rootDiv = this.getElement(this.ROOT);
    rootDiv.style.minWidth = '75px';
    this.addIcon(MATERIAL_ICONS.CALENDAR_TODAY, undefined, () => this.openDatepicker());
  }

  onBlur = ({target}) => {
    this.checkRequired();

    const input = target.value || "";
    if (input) {
      const values = input ? input.split('/').map((v)  => v.replace(/\D/g, '')) : [];
      let output = '';
      if (values.length === 3) {
        let year = parseInt(values[2]);
        if(values[2].length === 2) {
          let now = new Date(Date.now());
          let y = now.getFullYear() + '';
          let a = parseInt(y.substring(2,4));
          year = a >= year ? year + 2000 : year + 1900;
        }
        const month = parseInt(values[1]) - 1;
        const day = parseInt(values[0]);

        const d = new Date(year, month, day);

        if (!isNaN(d)) {          
          this.setDate(d);
          const dates = [d.getDate(), d.getMonth() + 1, d.getFullYear()];
          output = dates.map((v) =>{
            v = v.toString();
            return v.length === 1 ? '0' + v : v;
          }).join('/');
        }
      }
      target.value = output.replaceAll(" ", "");

      this.dispatchEvent(new Event(EVENT.BLUR));
    } else {
      target.value = "";
    }
  };

  onInput = ({target}) => {
    let value = target.value || "";
    if (value) {
      if (/\D\/$/.test(value)) value = value.substr(0, value.length - 3);
      const values = value.split('/').map((v) => v.replace(/\D/g, ''));
      if (values[0]) values[0] = this.checkValue(values[0], 31);
      if (values[1]) values[1] = this.checkValue(values[1], 12);
      const output = values.map((v, i) => v.length === 2 && i < 2 ? v + '/' : v);
      target.value = output.join('').substr(0, 14);
      if (value.length >= 14) target.blur();
    } else {
      target.value = "";
    }
  };

  checkValue(str, max){
    if (str.charAt(0) !== '0' || str == '00') {
      let num = parseInt(str);
      if (isNaN(num) || num <= 0 || num > max) num = 1;
      str = num > parseInt(max.toString().charAt(0)) && num.toString().length == 1 ? '0' + num : num.toString();
    }
    return str;
  }
  
  buildDatepicker() {
    let rootDiv = this.getElement(this.ROOT);

    let span = this.createElement(TAG.SPAN);
    span.id = this.SPAN;
    rootDiv.appendChild(span);

    let datepicker =  this.getElement(this.DATEPICKER) || this.createElement(TAG.DIV);
    datepicker.id = this.DATEPICKER;
    datepicker.classList.add('aonDatepicker', CSS.AON_BOX_SHADOW);
    if(this.width)
      datepicker.style.width = this.width;
    span.appendChild(datepicker);
    let datepickerHeaderId =this.DATEPICKER +"Header";
    let datepickerHeader = this.getElement(datepickerHeaderId) || this.createElement(TAG.DIV);
    datepickerHeader.id = datepickerHeaderId;
    datepickerHeader.className = "date-picker-title";

    let aib1 = new AonIconButton();
    aib1.id = this.DATEPICKER_PREVIOUS;
    aib1.className = "date-picker-title-icon";
    aib1.icon = "keyboard_arrow_left";
    datepickerHeader.appendChild(aib1);

    aib1.addEventListener(EVENT.CLICK, (ev) => {
      if (!aib1.disabled) {
        if (this.activePicker === this.YEARPICKER) {
          this.startYear -= this.yearTotal;;
          this.renderYears();
        } else {
          ev.stopPropagation();
          ev.preventDefault();
          this.previousMonth();
        }
      }
    });

    let span1 = this.createElement(TAG.SPAN);
    span1.id = this.DATEPICKER_MONTH;
    span1.classList.add('datepicker-month');
    span1.textContent = this.getMonthName();
    datepickerHeader.appendChild(span1);
    
    let span2 = this.createElement(TAG.SPAN);
    span2.id = this.DATEPICKER_YEAR;
    span2.classList.add('datepicker-year');
    span2.textContent = this.year;
    datepickerHeader.appendChild(span2);
	
    span1.addEventListener(EVENT.CLICK, () => {
      if (this.activePicker === this.MONTHPICKER) {
        this.showHideButtonsMonthsYears();
        this.showRenderPicker();
        this.activePicker = null;
      } else {
        this.showHideButtonsMonthsYears(this.MONTHPICKER);
        this.showRenderPicker(this.MONTHPICKER);
        this.activePicker = this.MONTHPICKER;
      }
    });
    
    span2.addEventListener(EVENT.CLICK, () => {
      if (this.activePicker === this.YEARPICKER) {
        this.showHideButtonsMonthsYears();
        this.showRenderPicker();
        this.activePicker = null;
      } else {
        this.initYearSelection();
        this.showHideButtonsMonthsYears(this.YEARPICKER);
        this.showRenderPicker(this.YEARPICKER);
        this.activePicker = this.YEARPICKER;
      }
    });

    let aib2 = new AonIconButton();
    aib2.id = this.DATEPICKER_NEXT;
    aib2.className = "date-picker-title-icon";
    aib2.icon = "keyboard_arrow_right";
    datepickerHeader.appendChild(aib2);

    aib2.addEventListener(EVENT.CLICK, (ev) => {
      if (!aib2.disabled) {
        if (this.activePicker === this.YEARPICKER) {
          this.startYear += this.yearTotal;
          this.renderYears();
        }else{
          ev.stopPropagation();
          ev.preventDefault();
          this.nextMonth();
        }
      }
    });

    datepicker.appendChild(datepickerHeader);

    let datepickerDays = this.createElement(TAG.DIV);
    let table = this.createElement(TAG.TABLE);
    table.id = this.DATEPICKER_DAYS;
    table.style.width = "100%";
    datepickerDays.appendChild(table);

    datepicker.appendChild(datepickerDays);
    this.buildCalendar();

    document.addEventListener(EVENT.CLICK, (event) => {
      let isClickInside = this.contains(event.target);
      if(!isClickInside){
        if(datepicker.classList.contains('is-visible')){
          datepicker.classList.remove('is-visible');
        }
      }
    });
  }

  buildCalendar() {
    this.showHideButtonsMonthsYears();
    this.showButtonsPreviousNext();
    this.clearElementById(this.DATEPICKER_DAYS);
    let datepickerDaysTable = this.getElement(this.DATEPICKER_DAYS);
    let trDays = this.createElement(TAG.TR);
    datepickerDaysTable.appendChild(trDays);
    for(let d = 0; d < 7; d++) {
      let td = this.createElement(TAG.TD);
      td.className = 'aonDatepickerDays aonDatepickerDaysTitle';
      let innerDiv = this.createElement(TAG.DIV);
      innerDiv.className = 'day-content-title';
      innerDiv.innerHTML = this.getDayName(d);

      td.appendChild(innerDiv);
      trDays.appendChild(td);
    }

    for(let i = 0; i < this.getWeeksInMonth(this.year, this.month); i++) {
      let tr = this.createElement(TAG.TR);
      datepickerDaysTable.appendChild(tr);
      for(let j = 0; j < 7; j++) {
        let td = this.createElement(TAG.TD);
        td.className = 'aonDatepickerDays';
        td.id = this.DATEPICKER_DAYS + i + j;

        let innerDiv = this.createElement(TAG.DIV);
        innerDiv.className = 'day-content';

        td.appendChild(innerDiv);
        tr.appendChild(td);
      }
    }

    let line = 0;
    let day  = 1;
    let date = new Date(this.year, this.month, day);

    while(date.getMonth() === this.month) {
      let actDate = date;
      let pos = (date.getDay() - 1 < 0) ? 6 : date.getDay() - 1;
      let td = this.getElement(this.DATEPICKER_DAYS + line + pos);
      // Crear el div contenedor
      let innerDiv = document.createElement(TAG.DIV);
      innerDiv.className = 'day-content';  // La clase para aplicar flex y estilos

      // Poner la fecha como texto dentro del div
      innerDiv.textContent = date.getDate();

      // Limpiar contenido anterior y agregar el div al td
      td.innerHTML = '';
      td.appendChild(innerDiv);
      // Marcar el dia seleccionado
      innerDiv.classList.toggle('day-selected', this.isSameDate(date));

      td.addEventListener(EVENT.CLICK, (event) => {
        event.stopPropagation();
        this.setDate(actDate);
        this.closeDatepicker();
      });

      if(pos === 6) {
        line = line + 1;
      }
      day = day + 1;
      date = new Date(this.year, this.month, day);
    }
  }

  getMonthName(value) {
    switch (value != undefined ? value : this.month) {
      case 0: return MSG.JANUARY.toUpperCase();
      case 1: return MSG.FEBRUARY.toUpperCase();
      case 2: return MSG.MARCH.toUpperCase();
      case 3: return MSG.APRIL.toUpperCase();
      case 4: return MSG.MAY.toUpperCase();
      case 5: return MSG.JUNE.toUpperCase();
      case 6: return MSG.JULY.toUpperCase();
      case 7: return MSG.AUGUST.toUpperCase();
      case 8: return MSG.SEPTEMBER.toUpperCase();
      case 9: return MSG.OCTOBER.toUpperCase();
      case 10: return MSG.NOVEMBER.toUpperCase();
      case 11: return MSG.DECEMBER.toUpperCase();
      default: return MSG.JANUARY.toUpperCase();
    }
  }

  getWeeksInMonth(year, month) {
    // Día de la semana en que empieza el mes (0 = domingo, 1 = lunes, ...)
    const firstDay = new Date(year, month, 1).getDay();
    // Total de días en el mes
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    // Ajustar el offset porque muchos calendarios empiezan lunes
    const offset = (firstDay === 0 ? 6 : firstDay - 1);
    // Total de celdas necesarias en el calendario
    const totalCells = offset + daysInMonth;
    // Número de semanas (redondeando hacia arriba)
    return Math.ceil(totalCells / 7);
  }

  getDayName(d) {
    switch (d) {
      case 0: return 'L';
      case 1: return 'M';
      case 2: return 'X';
      case 3: return 'J';
      case 4: return 'V';
      case 5: return 'S';
      case 6: return 'D';
    }
  }

  previousMonth() {
    if(this.month === 0) {
      this.month = 11;
      this.year = this.year- 1;
    } else {
      this.month = this.month- 1;
    }
    this.getElement(this.DATEPICKER_MONTH).innerHTML = this.getMonthName();
    this.getElement(this.DATEPICKER_YEAR).innerHTML = this.year;
    this.buildCalendar();
  }

  nextMonth() {
    if(this.month === 11) {
      this.month = 0;
      this.year = this.year+ 1;
    } else {
      this.month = this.month + 1;
    }
    this.getElement(this.DATEPICKER_MONTH).innerHTML = this.getMonthName();
    this.getElement(this.DATEPICKER_YEAR).innerHTML = this.year;
    this.buildCalendar();
  }

  openDatepicker() {
    const datePicker = this.getElement(this.DATEPICKER);
    if(datePicker){
      if(!this.isReadonly()) {
        this.buildCalendar();
        datePicker.classList.add('is-visible');
      }
      if(this.isMobile()){
        datePicker.classList.add('is-mobile');
      }
    }
  }

  closeDatepicker() {
    let div = this.getElement(this.DATEPICKER);
    if(div && div.classList.contains('is-visible')){
      div.classList.remove('is-visible');
    }
  }

  isSameDate(date) {
    // Tenga en cuenta la fecha de hoy si no se le pasa datos
    const reference = this.date || this.today;
    return date.getDate() === reference.getDate() &&
           date.getMonth() === reference.getMonth() &&
           date.getFullYear() === reference.getFullYear();
  }

  setDate(date) {
    let input = this.getElement(this.INPUT);
    if ( date ) {
      this.date = (date instanceof Date)
        ? date : AonDateUtils.parse(date);
      if (input)
        input.value = AonDateUtils.formatDate(this.date, '/');
    } else {
      // Reseteamos el input
      this.date   = '';
      this.day   = this.today.getDate();
      this.month = this.today.getMonth();
      this.year  = this.today.getFullYear();
      input.value = '';
    }

    let datepickerDays = this.getElement(this.DATEPICKER_DAYS);
    if(datepickerDays)
      this.buildCalendar();
    if(input && datepickerDays)
      this.dispatchEvent(new CustomEvent(EVENT.CHANGE, {detail: this.date}));
  }

  setValue() { 

  }

  focus() {
    this.getElement(this.INPUT).focus();
  }

  isReadonly() {
    return this.hasAttribute(CONSTANT.READONLY) && this.getAttribute(CONSTANT.READONLY)
      && CONSTANT.UNDEFINED !== this.getAttribute(CONSTANT.READONLY) && CONSTANT.FALSE !== this.getAttribute(CONSTANT.READONLY);
  }

  disabledDate(b){
    let input = this.getElement(this.INPUT);
    if(input) {
      input.readonly = b;
      input.disabled = b;
    }
  }

  setDisabled(b){
    this.disabledDate(b);
    this.readonly = "true";
  }

  setVisible(visible){
    if(visible){
      this.setAttribute(CONSTANT.HIDDEN, visible);
    } else {
      this.removeAttribute(CONSTANT.HIDDEN);
    }
  }

  clear(){
    
  }

  getValue() {
    return AonDateUtils.formatDate(this.date);
  } 
  
  getDateValue() {
    return AonDateUtils.formatDate(this.date, 'yyyy-MM-dd');
  }
  
  initYearSelection(){
    this.year = this.date ? this.date.getFullYear() : this.today.getFullYear();
    this.startYear = this.year - (this.year % this.yearTotal);
  }

  renderYears() {
    let yearPicker     = this.getElement(this.DATEPICKER_YEAR);
    let yearGrid       = this.getElement(this.DATEPICKER_DAYS);
    yearGrid.innerHTML = "";
    let tr;
    for (let i = 0; i < this.yearTotal; i++) {
      if (i % 5 === 0) {
        tr = this.createElement(TAG.TR);
        tr.className = "year-tr";
        yearGrid.appendChild(tr);
      }

      const td = this.createElement(TAG.TD);
      td.className = "year-td";

      const year = this.startYear + i;
      const btn = document.createElement(TAG.DIV);
      btn.textContent = year;
      btn.className = "year-btn";
      btn.classList.toggle('year-selected', year === this.year);

      td.appendChild(btn);
      tr.appendChild(td);

      btn.addEventListener(EVENT.CLICK, (event) => {
        this.year = +event.target.innerHTML;
        yearPicker.innerHTML = this.year;
        this.buildCalendar();
        event.stopPropagation();
      });
    }
    // Rango de fechas mostrado
    yearPicker.innerHTML = `${this.startYear} - ${this.startYear + (this.yearTotal - 1)}`;
  }
  
  renderMonths(){
    const monthGrid = this.getElement(this.DATEPICKER_DAYS);
    monthGrid.innerHTML = "";
    this.hideButtonsPreviousNext();
    let tr;
    for (let i = 0; i < 12; i++) {
      if (i % 3 === 0) {
        tr = this.createElement(TAG.TR);
        tr.className = "months-tr";
        monthGrid.appendChild(tr);
      }

      const td = this.createElement(TAG.TD);
      td.className = "months-td";

      const btn = this.createElement(TAG.DIV);
      btn.textContent = this.getMonthName(i);
      btn.value = i;
      btn.className = "months-btn";

      td.appendChild(btn);
      tr.appendChild(td);

      btn.addEventListener(EVENT.CLICK, (event) => {
        this.month = +event.target.value;
        this.getElement(this.DATEPICKER_MONTH).innerHTML = this.getMonthName();
        this.buildCalendar();
        event.stopPropagation();
      });
    }
  }
  
  showRenderPicker(elementPicker){
    if(elementPicker === this.MONTHPICKER){
      this.renderMonths();
    } else if(elementPicker === this.YEARPICKER){
      this.renderYears();
    } else {
      this.buildCalendar();
    }
  }

  showButtonsPreviousNext(){
    let buttonPrevious = this.getElement(this.DATEPICKER_PREVIOUS);
    let buttonNext     = this.getElement(this.DATEPICKER_NEXT);
    buttonPrevious.removeAttribute('disabled');
    buttonNext.removeAttribute('disabled');
  }
  hideButtonsPreviousNext(){
    let buttonPrevious = this.getElement(this.DATEPICKER_PREVIOUS);
    let buttonNext     = this.getElement(this.DATEPICKER_NEXT);
    buttonPrevious.setAttribute('disabled', 'true');
    buttonNext.setAttribute('disabled', 'true');
  }

  showHideButtonsMonthsYears(elementPicker){
    const monthPicker = this.getElement(this.DATEPICKER_MONTH);
    const yearPicker  = this.getElement(this.DATEPICKER_YEAR);

    if (elementPicker === this.MONTHPICKER) {
      monthPicker.classList.remove('hidden');
      yearPicker.classList.add('hidden');
      this.activePicker = this.MONTHPICKER;
    } else if (elementPicker === this.YEARPICKER) {
      yearPicker.classList.remove('hidden');
      monthPicker.classList.add('hidden');
      this.activePicker = this.YEARPICKER;
    } else {
      monthPicker.classList.remove('hidden');
      yearPicker.classList.remove('hidden');
      yearPicker.innerHTML = this.year;
      this.activePicker = '';
    }
  }

}
if(!window.customElements.get(TAG.AON_NEW_DATE)){
  window.customElements.define(TAG.AON_NEW_DATE,  AonNewDate);
}
