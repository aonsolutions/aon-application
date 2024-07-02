import { MSG, EVENT, TAG, CSS } from '../environments/environments.js'
import { AonNewInput } from './aon-new-input.js';

export class AonQuantity extends AonNewInput {

    TAG_SPAN;

    quantity;
    format;
    
    stockUnitTag;
    packFormatTag;
    packUnitsTag;
    packUnits;
    packMeasurementTag;
    packMeasurement;

	connectedCallback () {
        this.initialize();
        this.initializeQuantity();
        this.build();
        this.addTags();
    }

    initializeQuantity() {
        this.format = this.format || 'format';
        this.stockUnitTag = this.stockUnitTag || '';
        this.packFormatTag = this.packFormatTag || '';
        this.quantity = this.quantity || 0.0;
        this.TAG_SPAN = this.id + 'TagSpan';
    }

    addTags() {
        let div = this.getElement(this.BOX);
        let iconLabel = this.getElement(this.ICON);
        if (!iconLabel) {
          iconLabel = this.createElement(TAG.LABEL);
          div.appendChild(iconLabel);
        }
        iconLabel.className = CSS.AON_INPUT_ICON_LABEL;
        iconLabel.style.top = '15px';
        iconLabel.style.right = '15px';
        iconLabel.id = this.ICON;
        iconLabel.setAttribute("for", this.INPUT);

        let span = this.createSpan();
        span.id = this.TAG_SPAN;
        span.innerHTML = this.format === 'stockUnit' ? this.stockUnitTag : this.packFormatTag;
        span.style.cursor = 'pointer';
        span.addEventListener(EVENT.CLICK, () => this.changeFormat());
        iconLabel.appendChild(span);
        

        this.getElement(this.INPUT).style.paddingRight = '40px';
    }

    changeFormat() {
        if(this.format === 'stockUnit' && this.packFormatTag != '') {
            this.format = 'format';
            this.toFormat();
            this.getElement(this.TAG_SPAN).innerHTML = this.packFormatTag; 
        } else if(this.format === 'format' && this.stockUnitTag != '') {
            this.format = 'stockUnit';
            this.toStockUnit();
            this.getElement(this.TAG_SPAN).innerHTML = this.stockUnitTag; 
        }

    }

    toFormat() {
        let formatQuantity = this.quantity;
        if(this.stockUnitTag === this.packMeasurementTag) {
            formatQuantity = this.quantity / this.packMeasurement;
            formatQuantity = formatQuantity / this.packUnits;	
        } else if(this.stockUnitTag === this.packUnitsTag) {
            formatQuantity = this.quantity / this.packUnits;	
        }    
        // formatQuantity = AonMathUtils.round(formatQuantity);
        this.setValue(formatQuantity);  
    }

    toStockUnit() {
        this.setValue(this.quantity);
    }

    setValue(value) {
        this.value = value;
        let input = this.getElement(this.INPUT);
        input.value = this.value;
    }

    getQuantity() {
        return this.quantity;
    }

    setQuantity(quantity) {
        this.quantity = quantity;
        if(this.format === 'stockUnit') {
            this.toStockUnit();
        } else this.toFormat();
    }

    setTags(object) {
        this.stockUnitTag = object.stockUnitTag.name;
        this.packFormatTag = object.packFormatTag.name;
        this.packUnitsTag = object.packUnitsTag.name;
        this.packUnits = object.packUnits;
        this.packMeasurementTag = object.packMeasurementTag.name;
        this.packMeasurement = object.packMeasurement;

        if(this.format === 'stockUnit') {
            this.getElement(this.TAG_SPAN).innerHTML = this.stockUnitTag;
        } else {
            this.getElement(this.TAG_SPAN).innerHTML = this.packFormatTag; 
        }
    }
}
if(!window.customElements.get(TAG.AON_QUANTITY)){
    window.customElements.define(TAG.AON_QUANTITY, AonQuantity);
}