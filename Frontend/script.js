const apiBaseUrl = "https://graph-exploration-visualiser-backend.onrender.com";
const wsUrl = "wss://graph-exploration-visualiser-backend.onrender.com/ws/graph_explore";
// const apiBaseUrl = "http://localhost:8080";
// const wsUrl = "ws://localhost:8080/ws/graph_explore";


let socket;

let currentGridSize = 10;
let currentDrawMode = null;
let isPointerDown = false;

let orangeCellId = null;
let purpleCellId = null;
let disabled;
let intervalId;
let timeToReady;

let uniqueId;


function openWebSocket() {
    socket = new WebSocket(wsUrl);

    socket.onopen = function(event) {
        console.log("WebSocket is connected meow meow");
        markOpen();
        
        setTimeout(() => {
            enableMainButtons();
        }, 100);
    };


    socket.onmessage = function (event) {
        let str = event.data;
        if(str.startsWith("SessionId:")){
            uniqueId = str.split(":")[1];
        }
        else{
            processGrid(event);
        }
    };

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
        //maybe flash red? idk
        alert("No path possible");
    }

    if(code === "solved" || code === "failed"){
        enableMainButtons();
    }

    
}


function markOpen(){
    clearInterval(intervalId);
    document.getElementById("status-label").textContent = "Ready :)";
    let statusCircle = document.getElementById("status-circle");
    // statusCircle.style.height = "75px";
    // statusCircle.style.width = "75px";
    statusCircle.style.bottom = "50%";
    statusCircle.style.backgroundColor = "lightgreen";
}

function startTimer(){
    intervalId = setInterval(() => {
        timeToReady--;
        updateTimer();
    }, 1000);
}

function updateTimer(){
    let min = Math.floor(timeToReady/60);
    let seconds = timeToReady % 60;
    document.getElementById("status-label").innerHTML = `This will turn green when the webpage is ready to be used. <br> Time remaining: ${min} min, ${seconds} seconds`;
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
    if(coords === " "){
        return [];
    }

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
async function getRandomGridFromBackend() {
    if(disabled){
        return;
    }
    const data = { 
        input: currentGridSize,
        id: uniqueId
     };

    try {
        const response = await fetch(`${apiBaseUrl}/get-random-grid`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data), //sendinginfo
        });

        const result = await response.text(); // Extract result

        fillGrid(result);

    } catch (error) {
        console.error("Error:", error);
        throw error; //throw error if needed
    }
}

function fillGrid(gridString){
    let cellColours = gridString.split(" ");

    for(let r = 0; r < currentGridSize; r++){
        for(let c = 0; c < currentGridSize; c++){
            let cell = document.getElementById(`cell-${r}-${c}`);
            let color = cellColours[r*currentGridSize + c];

            clearCell(cell);
            if(color !== 'white'){
                cell.classList.add(color);
            }
            if(color === 'orange'){
                orangeCellId = cell.id;
            }
            if(color === 'purple'){
                purpleCellId = cell.id;
            }

            cell.dataset.color = color;

        }
    }
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
    if(disabled){
        return;
    }
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
    if(disabled){
        return;
    }
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
    if(disabled){
        return;
    }
    if (isPointerDown) {
        e.preventDefault();
        if (currentDrawMode) {
            updateCellColor(e.target, currentDrawMode);
        }
    }
}

function colorPointerPress(e){
    if(disabled){
        return;
    }
    e.preventDefault();
    if (currentDrawMode) {
        updateCellColor(e.target, currentDrawMode);
    }
}

function generateGrid(initial) {
    if(!initial && disabled){
        return;
    }
    let n = parseInt(document.getElementById("gridSize").value);

    //clamp the grid size between 2 and 100
    if(isNaN(n) || n < 2){
        n = 2;
    } 
    else if(n > 100){
        n = 100;
    }

    document.getElementById("gridSize").value = n; 
    
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
    if(disabled){
        return;
    }
    clearAllCircles();
    disableMainButtons();
    let sentString = getGridString();
    sendToBackend(sentString, algo);
}

async function sendToBackend(dataAsString, algo) {
    const data = { 
        input: dataAsString,
        id: uniqueId
     };

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
    'solveButtonRandomDFS': 'Run Randomized DFS - explores paths in random order.',
    'grid': "Click in here with your selected colour to draw on the grid",

    'next-button': 'Click here to get the next part of the instructions',
    'infoText': 'Find explanations in here',
    'status-container': "This will turn green once the website is ready to be used, until then it will be red",
    'instruction-container': "This box contains instructions",
    'hide-button': "click this to hide all extra widgets that are just for ease of use"
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

let instructions = ["Click here to get instructions", 
                    "This is a site where you can visualize different graph exploration/shortest path algorithms and compare their efficiency",
                    "The grid starts out as all white with the start (orange) and end (purple) nodes marked in the top left and bottom right corners respectively",
                    "The white squares have a cost of 1 to be traveled to, then yellow (2), red (5), and blue (10) have increased costs and are costlier nodes",
                    "The black squares are walls and cannot be traveled to or through",
                    "You can resize the grid by entering a new size for the NxN grid and clicking generate grid",
                    "You are also allowed to move the start (orange) and end (purple) nodes, and you can only have one of each on the grid at once",
                    "Once you are ready you can click one of the graph algorithms in green, if you want to try the same board with a different algorithm then you can click the remove paths button",
                    "The path will be shown as follows. Explored nodes: dark green, Explored and used in path nodes: light green",
                    "Once an algorithm has found its path from the start to the end, it will show the number of nodes explored to find this path as well as the cost of the path found",
                    "Have fun :)"];
let instructionNumber = 0;

function setInstructionInfo(){
    document.getElementById("instruction-text").textContent = instructions[instructionNumber];
    instructionNumber++;
    if(instructionNumber == instructions.length){
        instructionNumber = 0;
    }
    
}

function updateStats(exploredCount, pathCost) {
    document.getElementById('nodesExplored').textContent = exploredCount;
    document.getElementById('pathCost').textContent = pathCost;
}

function resetStats() {
    updateStats(0, 0);
}

function hideOrShow(){
    const extras = document.querySelectorAll('.extra');
    
    extras.forEach(element => {
        element.hidden = !element.hidden;
    });
}

function disableMainButtons(){
    // Disable main buttons
    disabled = true;
}

function enableMainButtons(){
    // Enable main buttons
    disabled = false;
}

function initialize(){
    disabled = true;
    timeToReady = 120;
    //tracking state of pointer for functions that need it
    document.addEventListener('pointerdown', () => { isPointerDown = true; });
    document.addEventListener('pointerup', () => { isPointerDown = false; });

    //init other buttons
    document.getElementById('generateBtn').addEventListener('pointerdown', () => {
        generateGrid(false);
    });
    document.getElementById('randomBtn').addEventListener('pointerdown', getRandomGridFromBackend);
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
    setInstructionInfo();

    generateGrid(true);
    setDrawMode('black'); //set start to black

    openWebSocket();
    startTimer();
}

    
window.onload = initialize;


