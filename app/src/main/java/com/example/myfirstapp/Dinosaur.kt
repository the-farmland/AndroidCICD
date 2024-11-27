package com.example.myfirstapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.os.Handler
import android.os.Looper
import kotlin.random.Random

class DinosaurGame(context: Context) : View(context) {
    private val paint = Paint()
    private var grid = Array(8) { Array(8) { 0 } }
    private var selectedDots = mutableListOf<Pair<Int, Int>>()
    private var score = 0
    private var isGameOver = false
    private var currentLevel = 1
    private val colorPalette = mutableListOf(
        Color.RED, 
        Color.BLUE, 
        Color.GREEN, 
        Color.YELLOW, 
        Color.MAGENTA
    )
    private var screenWidth = 0
    private var screenHeight = 0
    private val dotSize = 80f
    private val dotSpacing = 10f

    // Add callback for connection restored
    var onConnectionRestored: (() -> Unit)? = null
    private var showConnectionMessage = false
    private var connectionMessageBounds = android.graphics.RectF()

    private val updateHandler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateGame()
            invalidate()
            updateHandler.postDelayed(this, 16) // Smoother updates
        }
    }

    // Variable for the connection message
    var connectionMessage: String? = null
        set(value) {
            field = value
            showConnectionMessage = value != null
            invalidate()
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
        // Initialize grid with random colors based on current level
        val maxColor = minOf(colorPalette.size, 2 + currentLevel)
        grid = Array(8) { Array(8) { 
            Random.nextInt(maxColor)
        } }
    }

    private fun updateGame() {
        // Check for matches and remove them
        val matchedDots = findMatches()
        if (matchedDots.isNotEmpty()) {
            removeMatches(matchedDots)
            fillEmptySpaces()
            score += matchedDots.size * currentLevel
        }

        // Increase difficulty periodically
        if (score > currentLevel * 1000) {
            currentLevel++
            if (currentLevel > 5) {
                // Add a new color if we haven't reached max colors
                if (colorPalette.size < 8) {
                    colorPalette.add(generateNewColor())
                }
            }
            initializeGrid()
        }
    }

    private fun generateNewColor(): Int {
        // Generate a random color that's not already in the palette
        while (true) {
            val newColor = Color.rgb(
                Random.nextInt(256), 
                Random.nextInt(256), 
                Random.nextInt(256)
            )
            if (!colorPalette.contains(newColor)) {
                return newColor
            }
        }
    }

    private fun findMatches(): List<Pair<Int, Int>> {
        val matches = mutableSetOf<Pair<Int, Int>>()
        
        // Check horizontal matches
        for (y in 0 until 8) {
            for (x in 0 until 6) {
                if (grid[x][y] == grid[x+1][y] && grid[x][y] == grid[x+2][y]) {
                    matches.add(Pair(x,y))
                    matches.add(Pair(x+1,y))
                    matches.add(Pair(x+2,y))
                }
            }
        }
        
        // Check vertical matches
        for (x in 0 until 8) {
            for (y in 0 until 6) {
                if (grid[x][y] == grid[x][y+1] && grid[x][y] == grid[x][y+2]) {
                    matches.add(Pair(x,y))
                    matches.add(Pair(x,y+1))
                    matches.add(Pair(x,y+2))
                }
            }
        }
        
        return matches.toList()
    }

    private fun removeMatches(matches: List<Pair<Int, Int>>) {
        matches.forEach { (x, y) ->
            grid[x][y] = -1  // Mark as removed
        }
    }

    private fun fillEmptySpaces() {
        // Drop down non-removed dots
        for (x in 0 until 8) {
            val column = (0 until 8).mapNotNull { y ->
                if (grid[x][y] != -1) grid[x][y] else null
            }.toMutableList()
            
            // Fill top with new random colors
            while (column.size < 8) {
                column.add(0, Random.nextInt(minOf(colorPalette.size, 2 + currentLevel)))
            }
            
            // Update the column
            for (y in 0 until 8) {
                grid[x][y] = column[y]
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Calculate grid positioning
        val gridStartX = (screenWidth - (8 * (dotSize + dotSpacing))) / 2
        val gridStartY = screenHeight / 4f

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

        // Game over screen
        if (isGameOver) {
            paint.color = Color.YELLOW
            paint.textSize = 60f
            canvas.drawText("Game Over!", screenWidth / 2f - 120f, screenHeight / 2f, paint)
            paint.textSize = 30f
            canvas.drawText("Tap to restart", screenWidth / 2f - 80f, screenHeight / 2f + 50f, paint)
        }

        // Connection message logic remains the same as in original code
        if (showConnectionMessage && connectionMessage != null) {
            paint.color = Color.parseColor("#007AFF")
            paint.textSize = 50f
            
            val message = connectionMessage!!
            val padding = 40f
            val textWidth = paint.measureText(message)
            val textHeight = paint.descent() - paint.ascent()
            
            connectionMessageBounds.set(
                screenWidth / 2f - textWidth / 2f - padding,
                screenHeight / 3f + paint.ascent() - padding,
                screenWidth / 2f + textWidth / 2f + padding,
                screenHeight / 3f + paint.descent() + padding
            )
            
            paint.style = Paint.Style.FILL
            canvas.drawRoundRect(
                connectionMessageBounds,
                25f, 25f,
                paint
            )
            
            paint.color = Color.WHITE
            canvas.drawText(
                message,
                screenWidth / 2f - textWidth / 2f,
                screenHeight / 3f,
                paint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Connection message handler
                if (showConnectionMessage && connectionMessageBounds.contains(event.x, event.y)) {
                    onConnectionRestored?.invoke()
                    return true
                }
                
                // Restart game if game over
                if (isGameOver) {
                    startGame()
                    return true
                }

                // Calculate grid positioning
                val gridStartX = (screenWidth - (8 * (dotSize + dotSpacing))) / 2
                val gridStartY = screenHeight / 4f

                // Calculate touched dot
                val touchX = event.x
                val touchY = event.y
                
                for (x in 0 until 8) {
                    for (y in 0 until 8) {
                        val dotCenterX = gridStartX + x * (dotSize + dotSpacing) + dotSize / 2
                        val dotCenterY = gridStartY + y * (dotSize + dotSpacing) + dotSize / 2

                        // Check if touch is inside dot
                        if (Math.sqrt(Math.pow((touchX - dotCenterX).toDouble(), 2.0) + 
                                       Math.pow((touchY - dotCenterY).toDouble(), 2.0)) <= dotSize / 2) {
                            // Handle dot selection/deselection
                            val dot = Pair(x, y)
                            if (selectedDots.contains(dot)) {
                                selectedDots.remove(dot)
                            } else {
                                selectedDots.add(dot)
                            }
                            break
                        }
                    }
                }
            }
        }
        return true
    }

    fun stopGame() {
        updateHandler.removeCallbacks(updateRunnable)
    }
}