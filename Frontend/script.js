let currentGridSize = 10;
let currentDrawMode = null;
let isPointerDown = false;

function setDrawMode(mode) {
    currentDrawMode = mode;
      
    // Reset all mode buttons so are not highlighted
    const buttons = ['blackModeBtn', 'blueModeBtn', 'redModeBtn', 'yellowModeBtn', 'whiteModeBtn'];
    buttons.forEach(btn => {
        document.getElementById(btn).classList.remove(
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
        cell.classList.remove('black', 'blue', 'red', 'yellow', 'green-circle');
        cell.dataset.color = 'white';
        cell.textContent = "";
    });
}

function clearAllCircles() {
    document.querySelectorAll('.cell.green-circle').forEach(cell => {
        cell.classList.remove('green-circle');
    });
}

function showCellValues() {
    const cells = document.querySelectorAll('.cell');
    cells.forEach(cell => {
        const color = cell.dataset.color || 'white';
        cell.textContent = color;
    });
}

function updateCellColor(cell, color) {
    cell.classList.remove('black', 'blue', 'red', 'yellow');
    if (color === 'white') {
        // When using eraser, also remove green circles
        cell.classList.remove('green-circle');
    } else {
        cell.classList.add(color);
    }

    cell.dataset.color = color;
}

function addGreenCircle(cell) {
    if (!cell.classList.contains('green-circle')) {
        cell.classList.add('green-circle');
    }
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
}

function setButtonAction(colour){
    document.getElementById(`${colour}ModeBtn`).addEventListener('pointerdown', (e) => {
        e.preventDefault();
        setDrawMode(`${colour}`);
    });
}



function initialize(){
    // Track pointer state
    document.addEventListener('pointerdown', () => { isPointerDown = true; });
    document.addEventListener('pointerup', () => { isPointerDown = false; });

    // Initialize buttons with event listeners
    document.getElementById('generateBtn').addEventListener('pointerdown', generateGrid);
    document.getElementById('clearBtn').addEventListener('pointerdown', clearGrid);
    document.getElementById('showValuesBtn').addEventListener('pointerdown', showCellValues);

    // Color mode buttons
    setButtonAction('black');
    setButtonAction('red');
    setButtonAction('yellow');
    setButtonAction('blue');
    setButtonAction('white');

    // Initialize grid
    generateGrid();
    setDrawMode('black'); // Set default mode
}

    
initialize();


