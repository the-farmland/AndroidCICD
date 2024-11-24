package com.example.myfirstapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import android.os.Handler
import android.os.Looper
import kotlin.random.Random

class DinosaurGame(context: Context) : View(context) {
    private val paint = Paint()
    private var dinosaurY = 300f
    private var dinosaurX = 100f
    private var isJumping = false
    private var jumpVelocity = 0f
    private val gravity = 0.8f
    private val jumpStrength = -15f
    private var obstacles = mutableListOf<Pair<Float, Float>>()
    private var score = 0
    private var isGameOver = false
    private var gameSpeed = 10f
    
    private val updateHandler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateGame()
            invalidate()
            if (!isGameOver) {
                updateHandler.postDelayed(this, 16) // ~60 FPS
            }
        }
    }

    init {
        paint.textSize = 40f
        startGame()
    }

    private fun startGame() {
        score = 0
        isGameOver = false
        obstacles.clear()
        dinosaurY = 300f
        updateHandler.post(updateRunnable)
    }

    private fun updateGame() {
        // Update dinosaur position
        if (isJumping) {
            jumpVelocity += gravity
            dinosaurY += jumpVelocity
            if (dinosaurY > 300f) {
                dinosaurY = 300f
                isJumping = false
                jumpVelocity = 0f
            }
        }

        // Update obstacles
        if (Random.nextFloat() < 0.02) {
            obstacles.add(Pair(width.toFloat(), 300f))
        }

        obstacles = obstacles.mapNotNull { (x, y) ->
            val newX = x - gameSpeed
            if (newX < -20) null else Pair(newX, y)
        }.toMutableList()

        // Check collisions
        obstacles.forEach { (x, y) ->
            if (Math.abs(x - dinosaurX) < 30 && Math.abs(y - dinosaurY) < 30) {
                isGameOver = true
            }
        }

        // Update score
        if (!isGameOver) {
            score++
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw dinosaur (X)
        paint.textSize = 40f
        canvas.drawText("X", dinosaurX, dinosaurY, paint)

        // Draw obstacles (O)
        obstacles.forEach { (x, y) ->
            canvas.drawText("O", x, y, paint)
        }

        // Draw score
        paint.textSize = 30f
        canvas.drawText("Score: $score", 50f, 50f, paint)

        if (isGameOver) {
            paint.textSize = 60f
            canvas.drawText("Game Over!", width/2f - 120f, height/2f, paint)
            paint.textSize = 30f
            canvas.drawText("Tap to restart", width/2f - 80f, height/2f + 50f, paint)
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