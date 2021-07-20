
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

