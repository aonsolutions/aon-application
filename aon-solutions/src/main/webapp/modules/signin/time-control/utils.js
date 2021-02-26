export const StringTwoLetters = (str) => {
  let newStr = "";
  if (str) {
    let newArray = str.trim().split(" ");
    if (newArray.length > 1) {
      newArray = newArray.filter(Boolean);
      newStr =
        newArray[0].substr(0, 1).toUpperCase() +
        newArray[1].substr(0, 1).toUpperCase();
    } else {
      newStr =
        newArray[0].substr(0, 1).toUpperCase() +
        newArray[0].substr(1, 1).toUpperCase();
    }
  }
  return newStr;
};

export const firstLetters = (l) => l.replace(/^.{1}/g, l[0].toUpperCase());