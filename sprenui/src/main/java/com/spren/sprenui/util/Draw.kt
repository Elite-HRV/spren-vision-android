package com.spren.sprenui.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

class Draw(context: Context?, var pose: Pose) : View(context) {
    lateinit var boundaryPaint: Paint
    lateinit var leftPaint: Paint
    lateinit var rightPaint: Paint

    init {
        init()
    }

    private fun init() {
        boundaryPaint = Paint()
        boundaryPaint.color = Color.WHITE
        boundaryPaint.strokeWidth = 10f
        boundaryPaint.style = Paint.Style.STROKE

        leftPaint = Paint()
        leftPaint.strokeWidth = 10f
        leftPaint.color = Color.GREEN
        rightPaint = Paint()
        rightPaint.strokeWidth = 10f
        rightPaint.color = Color.YELLOW


    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val landmarks = pose.allPoseLandmarks

        for (landmark in landmarks) {
            canvas.drawCircle(
                translateX(landmark.position.x),
                landmark.position.y,
                8.0f,
                boundaryPaint
            )
        }

        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
        val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

        val leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)
        val rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)
        val leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
        val rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
        val leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
        val rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
        val leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
        val rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
        val leftFootIndex = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)
        val rightFootIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)


        if (leftShoulder != null && rightShoulder != null) {
            canvas?.drawLine(
                translateX(leftShoulder.position.x),
                leftShoulder.position.y,
                translateX(rightShoulder.position.x),
                rightShoulder.position.y,
                boundaryPaint
            )
        }
        if (leftHip != null && rightHip != null) {
            canvas?.drawLine(
                translateX(leftHip.position.x),
                leftHip.position.y,
                translateX(rightHip.position.x),
                rightHip.position.y,
                boundaryPaint
            )
        }

        //Left body

        if (leftShoulder != null && leftElbow != null) {
            canvas?.drawLine(
                translateX(leftShoulder.position.x),
                leftShoulder.position.y,
                translateX(leftElbow.position.x),
                leftElbow.position.y,
                leftPaint
            )
        }

        if (leftElbow != null && leftWrist != null) {
            canvas?.drawLine(
                translateX(leftElbow.position.x),
                leftElbow.position.y,
                translateX(leftWrist.position.x),
                leftWrist.position.y,
                leftPaint
            )
        }

        if (leftShoulder != null && leftHip != null) {
            canvas?.drawLine(
                translateX(leftShoulder.position.x),
                leftShoulder.position.y,
                translateX(leftHip.position.x),
                leftHip.position.y,
                leftPaint
            )
        }

        if (leftHip != null && leftKnee != null) {
            canvas?.drawLine(
                translateX(leftHip.position.x),
                leftHip.position.y,
                translateX(leftKnee.position.x),
                leftKnee.position.y,
                leftPaint
            )
        }
        if (leftKnee != null && leftAnkle != null) {
            canvas?.drawLine(
                translateX(leftKnee.position.x),
                leftKnee.position.y,
                translateX(leftAnkle.position.x),
                leftAnkle.position.y,
                leftPaint
            )
        }

        if (leftWrist != null && leftThumb != null) {
            canvas?.drawLine(
                translateX(leftWrist.position.x),
                leftWrist.position.y,
                translateX(leftThumb.position.x),
                leftThumb.position.y,
                leftPaint
            )
        }

        if (leftWrist != null && leftPinky != null) {
            canvas?.drawLine(
                translateX(leftWrist.position.x),
                leftWrist.position.y,
                translateX(leftPinky.position.x),
                leftPinky.position.y,
                leftPaint
            )
        }

        if (leftWrist != null && leftIndex != null) {
            canvas?.drawLine(
                translateX(leftWrist.position.x),
                leftWrist.position.y,
                translateX(leftIndex.position.x),
                leftIndex.position.y,
                leftPaint
            )
        }

        if (leftIndex != null && leftPinky != null) {
            canvas?.drawLine(
                translateX(leftIndex.position.x),
                leftIndex.position.y,
                translateX(leftPinky.position.x),
                leftPinky.position.y,
                leftPaint
            )
        }

        if (leftAnkle != null && leftHeel != null) {
            canvas?.drawLine(
                translateX(leftAnkle.position.x),
                leftAnkle.position.y,
                translateX(leftHeel.position.x),
                leftHeel.position.y,
                leftPaint
            )
        }

        if (leftHeel != null && leftFootIndex != null) {
            canvas?.drawLine(
                translateX(leftHeel.position.x),
                leftHeel.position.y,
                translateX(leftFootIndex.position.x),
                leftFootIndex.position.y,
                leftPaint
            )
        }

        //Right body
        if (rightShoulder != null && rightElbow != null) {
            canvas?.drawLine(
                translateX(rightShoulder.position.x),
                rightShoulder.position.y,
                translateX(rightElbow.position.x),
                rightElbow.position.y,
                rightPaint
            )
        }

        if (rightElbow != null && rightWrist != null) {
            canvas?.drawLine(
                translateX(rightElbow.position.x),
                rightElbow.position.y,
                translateX(rightWrist.position.x),
                rightWrist.position.y,
                rightPaint
            )
        }

        if (rightShoulder != null && rightHip != null) {
            canvas?.drawLine(
                translateX(rightShoulder.position.x),
                rightShoulder.position.y,
                translateX(rightHip.position.x),
                rightHip.position.y,
                rightPaint
            )
        }

        if (rightHip != null && rightKnee != null) {
            canvas?.drawLine(
                translateX(rightHip.position.x),
                rightHip.position.y,
                translateX(rightKnee.position.x),
                rightKnee.position.y,
                rightPaint
            )
        }

        if (rightKnee != null && rightAnkle != null) {
            canvas?.drawLine(
                translateX(rightKnee.position.x),
                rightKnee.position.y,
                translateX(rightAnkle.position.x),
                rightAnkle.position.y,
                rightPaint
            )
        }

        if (rightWrist != null && rightThumb != null) {
            canvas?.drawLine(
                translateX(rightWrist.position.x),
                rightWrist.position.y,
                translateX(rightThumb.position.x),
                rightThumb.position.y,
                rightPaint
            )
        }

        if (rightWrist != null && rightPinky != null) {
            canvas?.drawLine(
                translateX(rightWrist.position.x),
                rightWrist.position.y,
                translateX(rightPinky.position.x),
                rightPinky.position.y,
                rightPaint
            )
        }

        if (rightWrist != null && rightIndex != null) {
            canvas?.drawLine(
                translateX(rightWrist.position.x),
                rightWrist.position.y,
                translateX(rightIndex.position.x),
                rightIndex.position.y,
                rightPaint
            )
        }

        if (rightIndex != null && rightPinky != null) {
            canvas?.drawLine(
                translateX(rightIndex.position.x),
                rightIndex.position.y,
                translateX(rightPinky.position.x),
                rightPinky.position.y,
                rightPaint
            )
        }

        if (rightAnkle != null && rightHeel != null) {
            canvas?.drawLine(
                translateX(rightAnkle.position.x),
                rightAnkle.position.y,
                translateX(rightHeel.position.x),
                rightHeel.position.y,
                rightPaint
            )
        }

        if (rightHeel != null && rightFootIndex != null) {
            canvas?.drawLine(
                translateX(rightHeel.position.x),
                rightHeel.position.y,
                translateX(rightFootIndex.position.x),
                rightFootIndex.position.y,
                rightPaint
            )
        }

    }


    @RequiresApi(Build.VERSION_CODES.R)
    fun translateX(x: Float): Float {

        // you will need this for the inverted image in case of using front camera
        // return context.display?.width?.minus(x)!!

        return x;
    }


}