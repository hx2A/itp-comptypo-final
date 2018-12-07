var flubber = require("flubber");
var opentype = require("opentype.js");

let ONE_CONTOUR_SNOWFLAKES = "abrEQR0#$%&";
let TWO_CONTOUR_SNOWFLAKES = "cxF16";

// groups a list of opentype.js vector commands by contour
function groupByContour(cmds) {
  contours = [];
  current = [];
  for (let cmd of cmds) {
    current.push(cmd);
    if (cmd.type == 'Z') {
      contours.push(current);
      current = [];
    }
  }

  return contours;
}

// determines if a list of commands specify a path in clockwise
// or counter-clockwise order
function clockwise(cmds) {
  let sum = 0;
  for (let i = 0; i < cmds.length - 1; i++) {
    let a = cmds[i];
    let b = cmds[i + 1];
    if (!(a.hasOwnProperty('x') && b.hasOwnProperty('x'))) {
      continue;
    }
    sum += (b.x - a.x) * (b.y + a.y);
  }

  return sum < 0;
}

function convertCommandsToStrings(path) {
  let contours = groupByContour(path.commands);
  let strings = [];
  let rotations = [];

  contours.forEach((contour) => {
    rotations.push(clockwise(contour));
    let out = "";
    contour.forEach((cmd) => {
      switch (cmd.type) {
        case 'M':
          out += `M${cmd.x},${cmd.y} `;
          break;
        case 'L':
          out += `L${cmd.x},${cmd.y} `;
          break;
        case 'C':
          out += `C${cmd.x1},${cmd.y1},${cmd.x2},${cmd.y2},${cmd.x},${cmd.y}`;
          break;
        case 'Q':
          out += `Q${cmd.x1},${cmd.y1},${cmd.x},${cmd.y} `;
          break;
        case 'Z':
          out += "Z";
          break;
      };
    });
    strings.push(out);
  });

  return [strings, rotations];
}

function makeInterpolators(character) {
  let normalPath = normalFont.getPath(character, 0, 0, 200);
  boundingBox = normalPath.getBoundingBox();
  let out = convertCommandsToStrings(normalPath);
  let normalStrings = out[0];
  twoSolids = normalStrings.length == 2 && out[1][1];

  if (normalStrings.length == 1 || twoSolids) {
    snowflake = ONE_CONTOUR_SNOWFLAKES[Math.floor(ONE_CONTOUR_SNOWFLAKES.length * Math.random())];
  } else if (normalStrings.length == 2) {
    snowflake = TWO_CONTOUR_SNOWFLAKES[Math.floor(TWO_CONTOUR_SNOWFLAKES.length * Math.random())];
  } else {
    throw "Too many contours!!";
  }

  let snowflakeStrings = convertCommandsToStrings(snowflakeFont.getPath(snowflake, 0, 0, 200))[0];

  if (twoSolids) {
    interpolators = flubber.combine(normalStrings, snowflakeStrings[0], {
      string: false,
      maxSegmentLength: 9
    });
  } else {
    console.log(normalStrings);
    console.log(snowflakeStrings);
    interpolators = flubber.interpolateAll(normalStrings, snowflakeStrings, {
      string: false,
      maxSegmentLength: 9
    });
  }

  return interpolators;
}



var normalFont = opentype.loadSync('fonts/BellaFashion.ttf');
var snowflakeFont = opentype.loadSync('fonts/WWFlakes.ttf');

let interpolators = makeInterpolators('a');
