
// export const bold = (text) => "**" + text.trim() + "**";
// export const italic = (text) => "_" + text.trim() + "_";
// export const list = (text) => "- " + text.trim() + "\n";
// export const tab = (text) => "\t" + text.trim();
// export const link = (text) => "[" + text.trim() + "](URL)";
// export const image = (text) => "!img[" + text.trim() + "]";

// export const compileHTML = (parent) =>{

//     const textarea = parent.querySelector("textarea");
//     const bold = /\*\*(.+)\*\*/gi;
//     const italic = /\_(.+)\_/gi;
//     const list = /\-\s(.+)/gi;
//     const link = /\[(.+)\]\((.+)\)/gi;
//     const image = /!img\((.+)\)/gi;
//     const script = /!#([\s,\S]*)#!/gi;
//     const tab = /\t/gi;
//     const intro = /^(!#)\n^(#!)/gi
   
//     const listFormat    = "<li>$1</li>"; 
//     const italicFormat  = "<i>$1</i>";
//     const boldFormat    = "<b>$1</b>";
//     const linkFormat    = "<a href='$2'>$1</a>";
//     const imageFormat   = "<img src='$1' alt='La imagen no pudo cargarse.' style='max-width:100%;max-height:100%'/>"
//     const tabFormat     = "&emsp;&emsp;";
//     const introFormat   = "<br/>"; 
//     const scriptFormat  = `<button class="aonButton" onclick="setTimeout(()=>{$1},15)">Run</button>`;

//     let compiled = textarea.value;

//     const scripts = compiled.match(script);
//     compiled = compiled.replaceAll(script, "");

//     compiled = compiled.replaceAll(list, listFormat)
//     compiled = compiled.replaceAll(bold, boldFormat).replaceAll("*","");
//     compiled = compiled.replaceAll(italic, italicFormat).replaceAll("_","");
//     compiled = compiled.replaceAll(link, linkFormat);
//     compiled = compiled.replaceAll(image, imageFormat);
//     compiled = compiled.replaceAll(tab, tabFormat);
  
//     if(scripts)
//       scripts.forEach(scr => compiled += scr.replaceAll(script,scriptFormat));

//     return compiled;
//   }



/**
 * Set markup to selection
 * @param {*} element - The input itself (Aon-textarea>textarea)
 * @param {*} funct - The Compile function.
 */
//  const setSelectionMarkup = (element, funct, conditions) => {
//     /**
//      * Get text and selected 
//      * text start and end indexes
//      */
//     const text = element.innerText;
//     let start = element.dataset.start;
//     let end = element.dataset.end;
//     /**
//      * If invalid index then return;
//      */
//     if (start == -1)  return;
//     /**
//      * If conditions are valid,
//      * then compile in markup.
//      */
//     const selection = text.substring(start, end);

//     console.log(start, end, selection);

//     if (conditions(text, selection , start, end)){

//         const compiled = funct(selection);
//         let pre = "";
//         let post = "";

//         if(start !== 0)
//             pre = text.substr(0, start);
        
//         if(end !== text.length)
//             post = text.substr(end, text.length);

//         element.value = pre + compiled + post;
//     }
// }