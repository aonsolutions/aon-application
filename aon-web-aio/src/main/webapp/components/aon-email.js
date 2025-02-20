import { MSG, EVENT, TAG } from '../environments/environments.js'
import { AonNewInput } from './aon-new-input.js';

export class AonEmail extends AonNewInput {

    onBlur = () => {
        this.checkRequired();
        this.checkEmail();
        this.dispatchEvent(new Event(EVENT.BLUR));
    };

    checkEmail() {
        if(!this.getValue().isEmpty() && !this.getValue().includes('@')) {
            this.addError(this.getValue() + " " + MSG.IS_NOT_VALID_EMAIL);
        } else if (!this.getValue().isEmpty()) { 
            this.removeError();
        }
    }
}
if(!window.customElements.get(TAG.AON_EMAIL)){
    window.customElements.define(TAG.AON_EMAIL, AonEmail);
}