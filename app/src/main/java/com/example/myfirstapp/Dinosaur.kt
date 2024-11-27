package com.example.myfirstapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.os.Handler
import android.os.Looper
import kotlin.math.abs
import kotlin.random.Random

class DinosaurGame(context: Context) : View(context) {
    private val paint = Paint()
    private var grid = Array(8) { Array(8) { 0 } }
    private var selectedDot: Pair<Int, Int>? = null
    private var score = 0
    private var isGameOver = false
    private var currentLevel = 1
    private val colorPalette = listOf(
        Color.RED, Color.BLUE, Color.GREEN, 
        Color.YELLOW, Color.MAGENTA, Color.CYAN, 
        Color.ORANGE, Color.PINK
    )
    private var screenWidth = 0
    private var screenHeight = 0
    private val dotSize = 80f
    private val dotSpacing = 10f

    private val updateHandler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateGame()
            invalidate()
            updateHandler.postDelayed(this, 16)
        }
    }

    init {
        paint.textSize = 40f
        paint.color = Color.WHITE
        startGame()
    }

    private fun startGame() {
        score = 0
        isGameOver = false
        currentLevel = 1
        initializeGrid()
        updateHandler.post(updateRunnable)
    }

    private fun initializeGrid() {
        grid = Array(8) { x ->
            Array(8) { y ->
                Random.nextInt(minOf(colorPalette.size, 2 + currentLevel))
            }
        }
    }

    private fun updateGame() {
        // Gravity effect and match checking
        applyGravity()
        val matches = findMatches()
        if (matches.isNotEmpty()) {
            removeMatches(matches)
            score += matches.size * currentLevel
        }

        // Level progression
        if (score > currentLevel * 1000) {
            currentLevel++
            initializeGrid()
        }
    }

    private fun applyGravity() {
        for (x in 0 until 8) {
            val column = grid[x].filterNot { it == -1 }
            val newColumn = MutableList(8) { -1 }
            
            // Fill bottom of column with existing dots
            for (i in column.indices) {
                newColumn[8 - column.size + i] = column[i]
            }
            
            // Refill top with new random colors
            for (y in 0 until 8) {
                if (newColumn[y] == -1) {
                    newColumn[y] = Random.nextInt(minOf(colorPalette.size, 2 + currentLevel))
                }
            }
            
            // Update grid column
            grid[x] = newColumn.toTypedArray()
        }
    }

    private fun findMatches(): List<Pair<Int, Int>> {
        val matches = mutableSetOf<Pair<Int, Int>>()
        
        // Horizontal matches
        for (y in 0 until 8) {
            for (x in 0 until 6) {
                if (grid[x][y] != -1 && grid[x][y] == grid[x+1][y] && grid[x][y] == grid[x+2][y]) {
                    matches.addAll(listOf(Pair(x,y), Pair(x+1,y), Pair(x+2,y)))
                }
            }
        }
        
        // Vertical matches
        for (x in 0 until 8) {
            for (y in 0 until 6) {
                if (grid[x][y] != -1 && grid[x][y] == grid[x][y+1] && grid[x][y] == grid[x][y+2]) {
                    matches.addAll(listOf(Pair(x,y), Pair(x,y+1), Pair(x,y+2)))
                }
            }
        }
        
        return matches.toList()
    }

    private fun removeMatches(matches: List<Pair<Int, Int>>) {
        matches.forEach { (x, y) ->
            grid[x][y] = -1
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Calculate centered grid positioning
        val gridWidth = 8 * (dotSize + dotSpacing)
        val gridStartX = (screenWidth - gridWidth) / 2
        val gridStartY = (screenHeight - gridWidth) / 2

        // Draw grid of dots
        for (x in 0 until 8) {
            for (y in 0 until 8) {
                if (grid[x][y] != -1) {
                    paint.color = colorPalette[grid[x][y]]
                    canvas.drawCircle(
                        gridStartX + x * (dotSize + dotSpacing) + dotSize / 2,
                        gridStartY + y * (dotSize + dotSpacing) + dotSize / 2,
                        dotSize / 2,
                        paint
                    )
                }
            }
        }

        // Draw score and level
        paint.color = Color.WHITE
        paint.textSize = 40f
        canvas.drawText("Score: $score", 50f, 50f, paint)
        canvas.drawText("Level: $currentLevel", screenWidth - 250f, 50f, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Calculate centered grid positioning
        val gridWidth = 8 * (dotSize + dotSpacing)
        val gridStartX = (screenWidth - gridWidth) / 2
        val gridStartY = (screenHeight - gridWidth) / 2

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Find touched dot
                for (x in 0 until 8) {
                    for (y in 0 until 8) {
                        val dotCenterX = gridStartX + x * (dotSize + dotSpacing) + dotSize / 2
                        val dotCenterY = gridStartY + y * (dotSize + dotSpacing) + dotSize / 2

                        // Check if touch is inside dot
                        if (abs(event.x - dotCenterX) <= dotSize / 2 && 
                            abs(event.y - dotCenterY) <= dotSize / 2) {
                            
                            // If no dot selected, select current dot
                            if (selectedDot == null) {
                                selectedDot = Pair(x, y)
                            } else {
                                // Check if new dot is adjacent to selected dot
                                val (prevX, prevY) = selectedDot!!
                                if (isAdjacent(prevX, prevY, x, y)) {
                                    // Swap dots
                                    swapDots(prevX, prevY, x, y)
                                }
                                // Reset selection
                                selectedDot = null
                            }
                            break
                        }
                    }
                }
            }
        }
        return true
    }

    private fun isAdjacent(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
        return (abs(x1 - x2) == 1 && y1 == y2) || 
               (abs(y1 - y2) == 1 && x1 == x2)
    }

    private fun swapDots(x1: Int, y1: Int, x2: Int, y2: Int) {
        // Swap dots in the grid
        val temp = grid[x1][y1]
        grid[x1][y1] = grid[x2][y2]
        grid[x2][y2] = temp
        
        // Check if swap creates a match
        val matches = findMatches()
        if (matches.isEmpty()) {
            // If no match, swap back
            grid[x1][y1] = grid[x2][y2]
            grid[x2][y2] = temp
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        screenWidth = w
        screenHeight = h
    }
}