const apiBaseUrl = "http://localhost:8080";
const wsUrl = "ws://localhost:8080/ws/graph_explore";
let socket;

let currentGridSize = 10;
let currentDrawMode = null;
let isPointerDown = false;

let orangeCellId = null;
let purpleCellId = null;


function openWebSocket() {
    socket = new WebSocket(wsUrl);

    socket.onopen = function(event) {
        console.log("WebSocket is connected meow meow");
    };

    socket.onmessage = processGrid;

    socket.onerror = function(error) {
        console.log('WebSocket Error:', error);
    };

    socket.onclose = function(event) {
        console.log('WebSocket connection closed');
    };
}

function processGrid(event){
    let str = event.data;
    let fivePart = str.split("::");

    let code = fivePart[0];
    let explored = fivePart[1]; //dark green dots?, maybe change this later so not so prominent
    let path = fivePart[2]; //green dots
    let costString = fivePart[3];
    let numExploredString = fivePart[4];

    clearAllCircles();


    let exploredArray = processCoordinates(explored);
    let pathArray = processCoordinates(path);

    fillExploredDarkGreen(exploredArray);
    fillPathGreen(pathArray);

        if(code === "solved"){
            updateCostAndNumExplored(costString, numExploredString);
        }
        else if(code === "failed"){

        }

    
}

function updateCostAndNumExplored(costString, numExploredString){
    let costBox = document.getElementById("pathCost");
    let numExploredBox = document.getElementById("nodesExplored");

    costBox.innerHTML = costString;
    numExploredBox.innerHTML = numExploredString;
}

function fillExploredDarkGreen(exploredArray){
    exploredArray.forEach(function(coord){
        let row = coord[0];
        let col = coord[1];
        let cell = document.getElementById(`cell-${row}-${col}`);
        addDarkGreenCircle(cell);
    });
}

function fillPathGreen(pathArray){
    pathArray.forEach(function(coord){
        let row = coord[0];
        let col = coord[1];
        let cell = document.getElementById(`cell-${row}-${col}`);
        addGreenCircle(cell);
    });
}

//takes coordinate string and turns it into 2d array like [[0, 0], [1, 0], [1, 1]]
function processCoordinates(coords){
    let individualCoords = coords.split("|");

    let coordsArray = [];

    individualCoords.forEach(function(individualCoord){
        let rc = individualCoord.split(" ");
        let rcArray = [];
        rcArray.push(parseInt(rc[0]));
        rcArray.push(parseInt(rc[1]));
        coordsArray.push(rcArray);

    });

    return coordsArray;


}


function setDrawMode(mode) {
    currentDrawMode = mode;
      
    //reset all buttons to default color
    const buttons = ['blackModeBtn', 'blueModeBtn', 'redModeBtn','yellowModeBtn', 'whiteModeBtn','orangeModeBtn', 'purpleModeBtn'];
    buttons.forEach(btn => {
        document.getElementById(btn).classList.remove(
            'draw-mode-orange',
            'draw-mode-purple',
            'draw-mode-black', 
            'draw-mode-blue',
            'draw-mode-red',
            'draw-mode-yellow',
            'draw-mode-white',
            'active-mode'
        );
    });
      
    //coloring the chosen button so ppl know it is chosen
    if (mode) {
        document.getElementById(`${mode}ModeBtn`).classList.add(
            `draw-mode-${mode}`,
            'active-mode'
        );
    }
}

function clearGrid() {
    const cells = document.querySelectorAll('.cell');
    cells.forEach(cell => {
        clearCell(cell);
    });

    //reset orange to top-left
    const topLeft = document.getElementById("cell-0-0");
    updateCellColor(topLeft, "orange");
    orangeCellId = topLeft.id;

    //reset purple to bottom-right
    const bottomRight = document.getElementById(`cell-${currentGridSize - 1}-${currentGridSize - 1}`);
    updateCellColor(bottomRight, "purple");
    purpleCellId = bottomRight.id;

    resetStats();
}



function clearAllCircles() {
    document.querySelectorAll('.cell.green-circle, .cell.dark-green-circle').forEach(cell => {
        cell.classList.remove('green-circle', 'dark-green-circle');
    });

    resetStats();
}


function clearCell(cell){
    cell.classList.remove('black', 'blue', 'red', 'yellow', 'orange', 'purple','green-circle', 'dark-green-circle');
    cell.dataset.color = 'white';
}

//maybe make sure we can't draw over purple and orange in here
function updateCellColor(cell, color) {
    if((cell.id === orangeCellId && color !== "orange") || (cell.id === purpleCellId && color !== "purple")){
        return
    }
    //making sure there is only 1 purple or orange
    if (color === 'orange') {
        if (orangeCellId && orangeCellId !== cell.id) {
            const prev = document.getElementById(orangeCellId);
            clearCell(prev);
        }
        orangeCellId = cell.id;
    }

    if (color === 'purple') {
        if (purpleCellId && purpleCellId !== cell.id) {
            const prev = document.getElementById(purpleCellId);
            clearCell(prev);
        }
        purpleCellId = cell.id;
    }

    clearCell(cell);
    if (color !== 'white') {
        cell.classList.add(color);
    }

    cell.dataset.color = color;

}

function addGreenCircle(cell) {
    cell.classList.remove('dark-green-circle');
    cell.classList.add('green-circle');
}

function addDarkGreenCircle(cell) {
    cell.classList.remove('green-circle');
    cell.classList.add('dark-green-circle');
}

function colorPointerDown(e) {
    if (isPointerDown) {
        e.preventDefault();
        if (currentDrawMode) {
            updateCellColor(e.target, currentDrawMode);
        }
    }
}

function colorPointerPress(e){
    e.preventDefault();
    if (currentDrawMode) {
        updateCellColor(e.target, currentDrawMode);
    }
}

function generateGrid() {
    const n = parseInt(document.getElementById("gridSize").value);
    currentGridSize = n;
      
    const grid = document.getElementById("grid");
    grid.innerHTML = "";

    grid.style.gridTemplateColumns = `repeat(${n}, 1fr)`;
    grid.style.gridTemplateRows = `repeat(${n}, 1fr)`;

    for (let row = 0; row < n; row++) {
        for (let col = 0; col < n; col++) {
            const cell = document.createElement("div");
            cell.className = "cell";
            cell.id = `cell-${row}-${col}`;
            cell.dataset.color = 'white';
          
            //add event listener for each cell
            cell.addEventListener('pointerover', colorPointerDown);
            cell.addEventListener('pointerdown', colorPointerPress);
          
            grid.appendChild(cell);
        }
    }

    //set top-left to orange (start)
    const topLeft = document.getElementById("cell-0-0");
    orangeCellId = topLeft.id;
    updateCellColor(topLeft, "orange");


    //set bottom-right to purple (end)
    const bottomRight = document.getElementById(`cell-${n - 1}-${n - 1}`);
    purpleCellId = bottomRight.id;
    updateCellColor(bottomRight, "purple");

}

function setButtonAction(colour){
    document.getElementById(`${colour}ModeBtn`).addEventListener('pointerdown', (e) => {
        e.preventDefault();
        setDrawMode(`${colour}`);
    });
}

function getIntValueFromColour(colour){
    if(colour == "white"){
        return 1;
    }
    else if(colour == "black"){
        return 10_000;
    }
    else if(colour == "blue"){
        return 10;
    }
    else if(colour == "red"){
        return 5;
    }
    else if(colour == "yellow"){
        return 2
    }
    else if(colour == "orange"){ //start
        return -1;
    }
    //purple end
    else{
        return -2;
    }
}

function getGridString(){
    let sentString = currentGridSize.toString() + "::";
    for (let row = 0; row < currentGridSize; row++) {
        for (let col = 0; col < currentGridSize; col++) {
            let cell = document.getElementById(`cell-${row}-${col}`);
            let colourValue = cell.dataset.color;
            let colourValueString = getIntValueFromColour(colourValue).toString();

            sentString += colourValueString + " ";
        }
        sentString += "| ";
    }
    return sentString;
}

function startSolving(algo){
    let sentString = getGridString();
    sendToBackend(sentString, algo);
}

async function sendToBackend(dataAsString, algo) {
    const data = { input: dataAsString };

    try {
        const response = await fetch(`${apiBaseUrl}/start-solving-${algo}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data), //sendinginfo
        });

        const result = await response.text(); // Extract result
        console.log(result);
        return result; //return result, unnecessary here but we keep for fun

    } catch (error) {
        console.error("Error:", error);
        throw error; //throw error if needed
    }
}


//info text for each hover over button, could perhaps change to make more detailed
const buttonInfo = {
  'blackModeBtn': 'Black cells represent walls/obstacles (high cost: 10,000). They block all pathfinding algorithms.',
  'blueModeBtn': 'Blue cells represent water (cost: 10). Pathfinding algorithms will avoid these unless necessary.',
  'redModeBtn': 'Red cells represent mud (cost: 5). Pathfinding algorithms will prefer to avoid these.',
  'yellowModeBtn': 'Yellow cells represent sand (cost: 2). Pathfinding algorithms will prefer paths without these.',
  'whiteModeBtn': 'White cells are normal terrain (cost: 1). This is also the eraser tool.',
  'orangeModeBtn': 'Orange is the START point. Click this button then click where you want the path to begin.',
  'purpleModeBtn': 'Purple is the END point. Click this button then click where you want the path to end.',
  'generateBtn': 'Generate a new grid with the specified size (N x N).',
  'clearBtn': 'Reset the entire grid, keeping the current size but clearing all colors.',
  'removeCirclesBtn': 'Remove all path visualization (green and dark green circles).',
  'solveButtonDijkstra': 'Run Dijkstra\'s algorithm - finds the shortest path considering cell costs.',
  'solveButtonAstar': 'Run A* algorithm - heuristic-based search that is usually faster than Dijkstra.',
  'solveButtonBFS': 'Run Breadth-First Search - explores all neighbors equally (ignores costs).',
  'solveButtonDFS': 'Run Depth-First Search - explores one path as far as possible before backtracking.',
  'solveButtonRandomDFS': 'Run Randomized DFS - explores paths in random order.'
};

function setupHoverInfo() {
  const infoText = document.getElementById('infoText');

  //adding hover for every button
  Object.keys(buttonInfo).forEach(buttonId => {
    const button = document.getElementById(buttonId);
    if (button) {
      button.addEventListener('mouseenter', () => {
        infoText.textContent = buttonInfo[buttonId];
      });
      button.addEventListener('mouseleave', () => {
        infoText.textContent = 'Hover over any button to see information about it.';
      });
    }
  });
}

function updateStats(exploredCount, pathCost) {
  document.getElementById('nodesExplored').textContent = exploredCount;
  document.getElementById('pathCost').textContent = pathCost;
}

function resetStats() {
  updateStats(0, 0);
}

function initialize(){
    //tracking state of pointer for functions that need it
    document.addEventListener('pointerdown', () => { isPointerDown = true; });
    document.addEventListener('pointerup', () => { isPointerDown = false; });

    //init other buttons
    document.getElementById('generateBtn').addEventListener('pointerdown', generateGrid);
    document.getElementById('clearBtn').addEventListener('pointerdown', clearGrid);
    document.getElementById('removeCirclesBtn').addEventListener('pointerdown', clearAllCircles);

    //init solve buttons
    document.getElementById('solveButtonDijkstra').addEventListener('pointerdown', () => {
        startSolving("dijkstra");
    });
    document.getElementById('solveButtonAstar').addEventListener('pointerdown', () => {
        startSolving("astar");
    });
    document.getElementById('solveButtonBFS').addEventListener('pointerdown', () => {
        startSolving("bfs");
    });
    document.getElementById('solveButtonDFS').addEventListener('pointerdown', () => {
        startSolving("dfs");
    });
    document.getElementById('solveButtonRandomDFS').addEventListener('pointerdown', () => {
        startSolving("randomdfs");
    });


    //init color buttons
    setButtonAction('black');
    setButtonAction('red');
    setButtonAction('yellow');
    setButtonAction('blue');
    setButtonAction('white');
    setButtonAction('orange');
    setButtonAction('purple');

    setupHoverInfo();

    generateGrid();
    setDrawMode('black'); //set start to black

    openWebSocket();
}

    
window.onload = initialize;


