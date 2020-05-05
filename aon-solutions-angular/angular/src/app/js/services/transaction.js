window.getTransactions = getTransactions;

function getTransactions() {
  return transactions;
}

const transactions = [
  {value: 'NAC', name: 'Nacional'},
  {value: 'INTR', name: 'Intracomunitaria'},
  {value: 'EXTR', name: 'Extracomunitaria'},
  {value: 'CCM', name: 'Canarias, Ceuta y Melilla'},
  {value: 'ISP', name: 'I.S.P.'},
];
