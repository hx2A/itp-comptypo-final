
let ONE_CONTOUR_SNOWFLAKES = "abrEQR0#$%&";
let TWO_CONTOUR_SNOWFLAKES = "cxF16";

let normalFontBytes;
let snowflakeFontBytes;
let normalFont;
let snowflakeFont;

let boundingBox;
let twoSolids;

let letters = "Hapyolids";


// ***********************************************************
// Thanks to Allison Parrish for the following two functions!
// ***********************************************************

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

function preload() {
  // font from https://www.fontspace.com/billy-argel/bella-fashion-personal-use
  normalFontBytes = loadBytes('BellaFashion.ttf');
  // font from https://www.fontspace.com/windwalker64/wwflakes
  snowflakeFontBytes = loadBytes('WWFlakes.ttf');
}

function setup() {
  createCanvas(400, 400);

  normalFont = opentype.parse(normalFontBytes.bytes.buffer);
  snowflakeFont = opentype.parse(snowflakeFontBytes.bytes.buffer);

  for (let c = 0; c < letters.length; c++) {
    character = letters[c];
    jsonData = constructCharacterMorphs(character);
    saveJSON(jsonData, `path_data_${character}.json`)
  }

  noLoop();
}

function constructCharacterMorphs(character) {
  // first get the path for the selected character
  let normalPath = normalFont.getPath(character, 0, 0, 100);
  boundingBox = normalPath.getBoundingBox();
  let out = convertCommandsToStrings(normalPath);
  let normalStrings = out[0];
  twoSolids = normalStrings.length == 2 && out[1][1];

  // what is the list of snowflakes that are suitable for this character?
  let snowflakes;
  if (normalStrings.length == 1 || twoSolids) {
    snowflakes = ONE_CONTOUR_SNOWFLAKES;
  } else if (normalStrings.length == 2) {
    snowflakes = TWO_CONTOUR_SNOWFLAKES;
  } else {
    console.log(normalStrings.length);
    throw "Too many contours!!";
  }

  let result = {};
  result.twoSolids = twoSolids;
  result.snowflakes = {}

  // for each snowflake get every possible morph with the character
  for (let i = 0; i < snowflakes.length; i++) {
    snowflake = snowflakes[i];
    console.log(`character=${character} snowflake=${snowflake}`);

    let snowflakeStrings = convertCommandsToStrings(snowflakeFont.getPath(snowflake, 0, 0, 150))[0];

    let interpolators;

    // get the interpolators
    if (twoSolids) {
      interpolators = flubber.combine(normalStrings, snowflakeStrings[0], {
        string: false,
        maxSegmentLength: 3
      });
    } else {
      interpolators = flubber.interpolateAll(normalStrings, snowflakeStrings, {
        string: false,
        maxSegmentLength: 3
      });
    }

    // extract the relevant data from the interpolators
    let snowflake_data = {};
    for (let t = 0; t <= 100; t++) {
      console.log(`character=${character} snowflake=${snowflake} t=${t}`);

      // this gets the raw path coordinates
      snowflake_data[t] = {};
      snowflake_data[t].paths = interpolators.map((interpolator) => {
        return interpolator(t / 100);
      });

      // calculate the bounding box and the center, for animation purposes
      let stats = {};
      stats.maxX = -99999;
      stats.minX = 99999;
      stats.maxY = -99999;
      stats.minY = 99999;
      for (let path of snowflake_data[t].paths) {
        for (let z = 0; z < path.length; z++) {
          stats.minX = Math.min(stats.minX, path[z][0]);
          stats.maxX = Math.max(stats.maxX, path[z][0]);
          stats.minY = Math.min(stats.minY, path[z][1]);
          stats.maxY = Math.max(stats.maxY, path[z][1]);
        }
      }
      stats.centerX = (stats.maxX + stats.minX) / 2;
      stats.centerY = (stats.maxY + stats.minY) / 2;
      snowflake_data[t].stats = stats;
    }

    result.snowflakes[(ONE_CONTOUR_SNOWFLAKES + TWO_CONTOUR_SNOWFLAKES).indexOf(snowflake)] = snowflake_data;
  }

  return result;
}