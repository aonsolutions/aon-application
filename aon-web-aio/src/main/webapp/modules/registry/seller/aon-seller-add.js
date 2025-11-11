import { AonElement } from "../../../components/AonElement.js";
import { AonNewSelect } from '../../../components/aon-new-select.js';
import { AonNewDate } from "../../../components/aon-new-date.js";
import { MSG } from "../../../environments/environments.js";
import { Customer } from "../../../models/registry/Customer.js";
import { RegistrySellerStatus, RegistrySellerType } from "../../../models/enums.js";
import { deleteRegistrySeller, getSellers, updateRegistrySeller } from "../../../services/commercialService.js";

export class AonSellerAdd extends AonElement {
  ITEM_SELECT;
  STATUS_SELECT;
  TYPE_SELECT;
  START_DATE_INPUT;
  END_DATE_INPUT;

  _sellers = [];
  _seller = null;
  _customers = [];
  _status = null;
  _startDate = null;
  _endDate = null;
  _type = null;

  _selectedRSeller;

  setSellers(items) {
    this._sellers = [];
    items.forEach((item) => this.addSeller(item));
  }

  getSellers() {
    return this._sellers || [];
  }

  setCustomers(customers) {
    this._customers = [];
    customers.forEach((customer) => this.addCustomer(customer));
  }

  getCustomers() {
    return this._customers || [];
  }

  setStatus(status) {
    this._status = status;
  }

  getStatus() {
    return this._status;
  }

  setType(type) {
    this._type = type;
  }

  getType() {
    return this._type;
  }

  setStartDate(startDate) {
    this._startDate = startDate;
  }

  getStartDate() {
    return this._startDate;
  }

  setEndDate(endDate) {
    this._endDate = endDate;
  }

  getEndDate() {
    return this._endDate;
  }

  setSeller(seller) {
    this._seller = seller;
  }

  getSeller() {
    return this._seller;
  }

  setSelectedRSeller(selectedRSeller) {
    this._selectedRSeller = selectedRSeller;
  }

  getSelectedRSeller() {
    return this._selectedRSeller;
  }

  addSeller(seller) {
    let items = this.getSellers();
    const isSome = items.some(({ id }) => id == seller.id);

    if (!isSome) {
      items.push(seller);
    }
  }

  removeItem(item) {
    this.setSellers(this.getSellers().filter(({ id }) => id !== item.id));
  }

  addCustomer(customer) {
    let customers = this.getCustomers();
    const isSome = customers.some((item) => item.id == customer.id);

    if (!isSome) {
      customers.push(new Customer(customer));
    }
  }

  removeCustomer(customer) {
    this.setCustomers(
      this.getCustomers().filter(({ id }) => id !== customer.id)
    );
  }

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id = this.id || "aonTargetItem";
  }

  build() {
    this.buildSelectItem();
    this.buildSelectType();
    this.buildSelectStatus();

    this.START_DATE_INPUT = new AonNewDate();
    this.START_DATE_INPUT.id = this.id + "startDateInput";
    this.START_DATE_INPUT.title = MSG.START_DATE;
    this.appendChild(this.START_DATE_INPUT);

    if (this.getSelectedRSeller().startDate) {
      this.START_DATE_INPUT.setDate(this.getSelectedRSeller().startDate);
      this.setStartDate(this.START_DATE_INPUT.getValue());
    } else {
      this.START_DATE_INPUT.setDate(new Date());
      this.setStartDate(new Date());
    }

    this.END_DATE_INPUT = new AonNewDate();
    this.END_DATE_INPUT.id = this.id + "endDateInput";
    this.END_DATE_INPUT.title = MSG.END_DATE;
    this.appendChild(this.END_DATE_INPUT);
    if (this.getSelectedRSeller().endDate) {
      this.END_DATE_INPUT.setDate(this.getSelectedRSeller().endDate);
      this.setEndDate(this.END_DATE_INPUT.getValue());
    } else {
      this.END_DATE_INPUT.setDate('');
    }
  }

  buildSelectItem() {
    this.ITEM_SELECT = new AonNewSelect();
    this.ITEM_SELECT.title = "Agente";
    this.ITEM_SELECT.id = this.id + "seller";
    this.ITEM_SELECT.loading(true);
    this.appendChild(this.ITEM_SELECT);

    // Por que se limitan los agente????
    const params = { page: 1, perPage: 20 };
    let firstTime = true;

    getSellers(params).then((opts) => {
      this.ITEM_SELECT.setOptions(
        opts.map((p) => ({ ...p, value: p.id }))
      )
      this.ITEM_SELECT.loading(false);
    }).finally(() => {
      if (
          this.getSelectedRSeller().rseller &&
          this.getSelectedRSeller().rseller.seller &&
          this.getSelectedRSeller().rseller.seller.id
      ) {
        this.ITEM_SELECT.setValue(this.getSelectedRSeller().rseller.seller.id);
      }
    });
  }

  buildSelectType() {
    this.TYPE_SELECT = new AonNewSelect();
    this.TYPE_SELECT.title = MSG.TYPE;
    this.TYPE_SELECT.id = this.id + "TYPE";
    this.TYPE_SELECT.setOptions(
      Object.entries(RegistrySellerType).map(
        ([value, name]) => ({ name, value })
      )
    );

    this.appendChild(this.TYPE_SELECT);
    if (this.getSelectedRSeller().rsellerType) {
      this.TYPE_SELECT.setValue(this.getSelectedRSeller().rsellerType);
    }
  }

  buildSelectStatus() {
    this.STATUS_SELECT = new AonNewSelect();
    this.STATUS_SELECT.title = MSG.STATUS;
    this.STATUS_SELECT.id = this.id + "status";
    this.STATUS_SELECT.setOptions(
      Object.entries(RegistrySellerStatus).map(
        ([value, name]) => ({ name, value })
      )
    );

    this.appendChild(this.STATUS_SELECT);
    if (this.getSelectedRSeller().statusText) {
      this.STATUS_SELECT.setValue(this.getSelectedRSeller().statusText);
    }
  }

  async save() {
    let error  = false;
    let params = {
      id        : this.getSelectedRSeller().rseller.id,
      type      : this.TYPE_SELECT.getValue(),
      start_date: this.START_DATE_INPUT.getValue(),
      end_date  : this.END_DATE_INPUT.getValue(),
      customer  : this.getSelectedRSeller().rseller.registry,
      seller    : this.ITEM_SELECT.getValue(),
      status    : this.STATUS_SELECT.getValue()
    };

console.log(params)

    error = this.checkError(params);
    if (!error) {
      await updateRegistrySeller(params);
      this.showMessage();
      return true;
    }

    return false;
  }

  async delete(id) {
    let params = { id: id };
    await deleteRegistrySeller(params);
  }

  async remove(id) {
    if (id) {
      this.getSellers()
        .filter((r) => r.id === id)
        .forEach((item) => (item.removed = true));
    } else {
      this.getSellers().forEach((item) => (item.removed = true));
    }

    await this.delete(id);
  }

  checkError(params) {
    if (!params.seller) {
      this.showMessageError("El campo agente es obligatorio");
      return true;
    } else if (!params.type) {
      this.showMessageError("El campo tipo es obligatorio");
      return true;
    } else if (!params.start_date) {
      this.showMessageError("El campo fecha de inicio es obligatorio");
      return true;
    }
    return false;
  }
}

if (!window.customElements.get("aon-seller-add")) {
  window.customElements.define("aon-seller-add", AonSellerAdd);
}
