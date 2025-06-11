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
    let threePart = str.split("::");

    let code = threePart[0];
    let explored = threePart[1]; //dark green dots?
    let path = threePart[2]; //green dots

    clearAllCircles();


    let exploredArray = processCoordinates(explored);
    let pathArray = processCoordinates(path);

    fillExploredDarkGreen(exploredArray);
    fillPathGreen(pathArray);

    
}

function fillExploredDarkGreen(exploredArray){
    console.log(exploredArray);
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
      
    // Reset all mode buttons so are not highlighted
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
      
    // Set active mode
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

    // Reset orange to top-left
    const topLeft = document.getElementById("cell-0-0");
    updateCellColor(topLeft, "orange");
    orangeCellId = topLeft.id;

    // Reset purple to bottom-right
    const bottomRight = document.getElementById(`cell-${currentGridSize - 1}-${currentGridSize - 1}`);
    updateCellColor(bottomRight, "purple");
    purpleCellId = bottomRight.id;
}



function clearAllCircles() {
    document.querySelectorAll('.cell.green-circle, .cell.dark-green-circle').forEach(cell => {
        cell.classList.remove('green-circle', 'dark-green-circle');
    });
}


function clearCell(cell){
    cell.classList.remove('black', 'blue', 'red', 'yellow', 'orange', 'purple','green-circle', 'dark-green-circle');
    cell.dataset.color = 'white';
}

function updateCellColor(cell, color) {
    // Ensure only one orange or purple cell
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
          
            // Add event listeners for drawing
            cell.addEventListener('pointerover', colorPointerDown);
            cell.addEventListener('pointerdown', colorPointerPress);
          
            grid.appendChild(cell);
        }
    }

    // ✅ Set top-left to orange (start)
    const topLeft = document.getElementById("cell-0-0");
    updateCellColor(topLeft, "orange");
    orangeCellId = topLeft.id;

    // ✅ Set bottom-right to purple (end)
    const bottomRight = document.getElementById(`cell-${n - 1}-${n - 1}`);
    updateCellColor(bottomRight, "purple");
    purpleCellId = bottomRight.id;
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

function startSolvingDijkstra(){
    let sentString = getGridString();
    sendToBackendDijkstra(sentString);
}

function startSolvingAstar(){
    let sentString = getGridString();
    sendToBackendAstar(sentString);
}

async function sendToBackendDijkstra(dataAsString) {
    const data = { input: dataAsString };

    try {
        const response = await fetch(`${apiBaseUrl}/start-solving-dijkstra`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data), // Convert the payload to JSON
        });

        const result = await response.text(); // Extract result
        console.log(result);
        return result; // Return the result, unnecessary here but we keep for fun

    } catch (error) {
        console.error("Error:", error);
        throw error; // Re-throw the error if needed
    }
}

async function sendToBackendAstar(dataAsString) {
    const data = { input: dataAsString };

    try {
        const response = await fetch(`${apiBaseUrl}/start-solving-astar`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data), // Convert the payload to JSON
        });

        const result = await response.text(); // Extract result
        console.log(result);
        return result; // Return the result, unnecessary here but we keep for fun

    } catch (error) {
        console.error("Error:", error);
        throw error; // Re-throw the error if needed
    }
}



function initialize(){
    // Track pointer state
    document.addEventListener('pointerdown', () => { isPointerDown = true; });
    document.addEventListener('pointerup', () => { isPointerDown = false; });

    // Initialize buttons with event listeners
    document.getElementById('generateBtn').addEventListener('pointerdown', generateGrid);
    document.getElementById('clearBtn').addEventListener('pointerdown', clearGrid);
    document.getElementById('showValuesBtn').addEventListener('pointerdown', clearAllCircles);
    document.getElementById('solveButtonDijkstra').addEventListener('pointerdown', startSolvingDijkstra);
    document.getElementById('solveButtonAstar').addEventListener('pointerdown', startSolvingAstar);

    // Color mode buttons
    setButtonAction('black');
    setButtonAction('red');
    setButtonAction('yellow');
    setButtonAction('blue');
    setButtonAction('white');
    setButtonAction('orange');
    setButtonAction('purple');

    // Initialize grid
    generateGrid();
    setDrawMode('black'); // Set default mode

    openWebSocket();
}

    
initialize();


