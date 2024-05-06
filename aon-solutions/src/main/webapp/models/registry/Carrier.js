import { Registry } from "./Registry.js";

export class Carrier extends Registry {

    scope;
    status;

    constructor(carrier) {
        super(carrier);
        if(carrier) {
            this.status = carrier.status || "ACTIVE";
            this.scope = carrier.scope;
        } else {
            this.status = "ACTIVE";
        }
    }

    getScope() {
        return this.scope;
    }

    setScope(scope) {
        this.scope = scope;
    }

    getStatus() {
        return this.status;
    }

    setStatus(status) {
        this.status = status;
    }

}