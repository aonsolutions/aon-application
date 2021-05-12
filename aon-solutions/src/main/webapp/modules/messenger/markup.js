
export const bold = (text) => "**" + text.trim() + "**";
export const italic = (text) => "_" + text.trim() + "_";
export const list = (text) => "- " + text.trim() + "\n";
export const tab = (text) => "\t" + text.trim();
export const link = (text) => "[" + text.trim() + "](URL)";

export const compileHTML = (parent) =>{
    const textarea = parent.querySelector("textarea");
  
    let bold = /\*\*(.+)\*\*/gi;
    let italic = /\_(.+)\_/gi;
    let list = /\-\s(.+)/gi;
    let link = /\[(.+)\]\((.+)\)/gi;
  
    let compiled = textarea.value;
    compiled = compiled.replaceAll(list, '<li>$1</li>');
    compiled = compiled.replaceAll(bold, '<b>$1</b>');
    compiled = compiled.replaceAll(italic, '<i>$1</i>');
    compiled = compiled.replaceAll(link, '<a href="$2">$1</a>');
    compiled = compiled.replaceAll(/\t/gi, '&emsp;&emsp;');
  
    return compiled;
  
  }