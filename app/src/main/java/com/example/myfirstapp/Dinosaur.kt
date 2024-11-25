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
    private var dinosaurY = 0f
    private var dinosaurX = 0f
    private var isJumping = false
    private var jumpVelocity = 0f
    private val gravity = 0.4f  // Reduced gravity for bigger jumps
    private val jumpStrength = -20f  // Increased jump strength for higher jumps
    private var obstacles = mutableListOf<Pair<Float, Float>>()
    private var score = 0
    private var isGameOver = false
    private var gameSpeed = 12f
    private var screenWidth = 0
    private var screenHeight = 0
    private val dinosaurSize = 80f
    private val obstacleSize = 60f

    private val updateHandler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateGame()
            invalidate()
            if (!isGameOver) {
                updateHandler.postDelayed(this, 12) // Faster updates (~80 FPS)
            }
        }
    }

    // Variable for the connection message
    var connectionMessage: String? = null

    init {
        paint.textSize = 40f
        paint.color = Color.WHITE
        startGame()
    }

    private fun startGame() {
        score = 0
        isGameOver = false
        obstacles.clear()

        post {
            screenWidth = width
            screenHeight = height
            dinosaurX = screenWidth / 5f
            dinosaurY = screenHeight / 2f
        }

        updateHandler.post(updateRunnable)
    }

    private fun updateGame() {
        if (isJumping) {
            jumpVelocity += gravity
            dinosaurY += jumpVelocity
            if (dinosaurY > screenHeight / 2f) {
                dinosaurY = screenHeight / 2f
                isJumping = false
                jumpVelocity = 0f
            }
        }

        if (Random.nextFloat() < 0.02) {
            obstacles.add(Pair(screenWidth.toFloat(), screenHeight / 2f))
        }

        obstacles = obstacles.mapNotNull { (x, y) ->
            val newX = x - gameSpeed
            if (newX < -obstacleSize) null else Pair(newX, y)
        }.toMutableList()

        obstacles.forEach { (x, y) ->
            if (Math.abs(x - dinosaurX) < dinosaurSize && Math.abs(y - dinosaurY) < dinosaurSize) {
                isGameOver = true
            }
        }

        if (!isGameOver) {
            score++
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.color = Color.GREEN
        canvas.drawText("X", dinosaurX, dinosaurY, paint.apply { textSize = dinosaurSize })

        paint.color = Color.RED
        obstacles.forEach { (x, y) ->
            canvas.drawText("O", x, y, paint.apply { textSize = obstacleSize })
        }

        paint.color = Color.WHITE
        paint.textSize = 30f
        canvas.drawText("Score: $score", 50f, 50f, paint)

        if (isGameOver) {
            paint.color = Color.YELLOW
            paint.textSize = 60f
            canvas.drawText("Game Over!", screenWidth / 2f - 120f, screenHeight / 2f, paint)
            paint.textSize = 30f
            canvas.drawText("Tap to restart", screenWidth / 2f - 80f, screenHeight / 2f + 50f, paint)
        }

        // Show connection message if it's set
        connectionMessage?.let {
            paint.color = Color.WHITE
            paint.textSize = 50f
            canvas.drawText(it, screenWidth / 2f - 180f, screenHeight / 3f, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isGameOver) {
                    startGame()
                } else if (!isJumping) {
                    isJumping = true
                    jumpVelocity = jumpStrength
                }
            }
        }
        return true
    }

    fun stopGame() {
        updateHandler.removeCallbacks(updateRunnable)
    }
}
