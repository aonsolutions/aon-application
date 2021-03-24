const drawLine = (ctx, startX, startY, endX, endY) =>{
    ctx.beginPath();
    ctx.moveTo(startX,startY);
    ctx.lineTo(endX,endY);
    ctx.stroke();
  }

  const drawArc = (ctx, centerX, centerY, radius, startAngle, endAngle) => {
    ctx.beginPath();
    ctx.arc(centerX, centerY, radius, startAngle, endAngle);
    ctx.stroke();
  }
  const drawPieSlice = (ctx, centerX, centerY, radius, startAngle, endAngle, color ) => {
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.moveTo(centerX,centerY);
    ctx.arc(centerX, centerY, radius, startAngle, endAngle);
    ctx.closePath();
    ctx.fill();
  }


export const pieChar = (options) => {
  const canvas = options.canvas;
  const ctx = canvas.getContext("2d");
  const colors = options.colors;
  const doughnutHoleSize = options.doughnutHoleSize;
  const data = options.data;
  const legend = options.legend;
  let total_value = 0;
  let color_index = 0;
  let start_angle = 0;

  for (let categ in data) { total_value += data[categ];}

  for (let categ in data) {
    let val = data[categ];
    let slice_angle = (2 * Math.PI * val) / total_value;
    drawPieSlice(
      ctx,
      canvas.width / 2,
      canvas.height / 2,
      Math.min(canvas.width / 2, canvas.height / 2),
      start_angle,
      start_angle + slice_angle,
      colors[color_index % colors.length]
    );
    start_angle += slice_angle;

    //----LEGEND
    if (legend) {
      let legendHTML =
        "<div><span style='display:inline-block;width:20px;background-color:" +
        colors[color_index] +
        ";'>&nbsp;</span> " +
        categ +
        "</div>";
      legend.innerHTML = legend.innerHTML + legendHTML;
    }
    color_index++;
  }
  //CREATE doughnut chart
  if (doughnutHoleSize) {
    drawPieSlice(
      ctx,
      canvas.width / 2,
      canvas.height / 2,
      doughnutHoleSize * Math.min(canvas.width / 2, canvas.height / 2),
      0,
      2 * Math.PI,
      "#FFFFFF"
    );
  }
};
