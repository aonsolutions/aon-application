
let invoices;
let index;

export const getInvoices = () => {
  return invoices;
}

export const setInvoices = (data) => {
  invoices = data;
}

export const addInvoices = (data) => {
  if(invoices) {
    data.forEach((item, i) => {
      invoices.push(item);
    });
  } else invoices = data;
}

export const getInvoice = (data) => {
  return invoices[data];
}

export const getPreviousInvoice = () => {
  if(index === 0) {
    return false;
  }
  index = index - 1;
  return invoices[index];
}

export const getNextInvoice = () => {
  if(index === invoices.length - 1) {
    return false;
  }
  index = index + 1;
  return invoices[index];
}

export const getIndex = () => {
  return index;
}

export const setIndex = (data) => {
  index = data;
}
