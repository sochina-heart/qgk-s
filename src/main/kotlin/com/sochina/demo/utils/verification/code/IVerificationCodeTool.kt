package com.sochina.demo.utils.verification.code

import com.sochina.demo.domain.VerificationCode
import java.awt.Color
import java.awt.Graphics
import java.security.SecureRandom
import kotlin.math.sin
import kotlin.random.Random

abstract class IVerificationCodeTool {
    protected var random: SecureRandom = SecureRandom()

    abstract fun createVerificationCodeImage(img_width: Int, img_height: Int): VerificationCode?

    protected fun drawDisturbLineAdd(g: Graphics, x: Int, y: Int, img_width: Int, img_height: Int, color: Color?) {
        g.color = color
        val x1 = random.nextInt(img_width)
        val y1 = random.nextInt(img_height)
        val x2 = random.nextInt(x)
        val y2 = random.nextInt(y)
        g.drawLine(x1, y1, x1 + x2, y1 + y2)
    }

    protected fun drawDisturbLineReduce(g: Graphics, x: Int, y: Int, img_width: Int, img_height: Int, color: Color?) {
        g.color = color
        val x1 = random.nextInt(img_width)
        val y1 = random.nextInt(img_height)
        val x2 = random.nextInt(x)
        val y2 = random.nextInt(y)
        g.drawLine(x1, y1, x1 - x2, y1 - y2)
    }

    protected fun getRandomColor(fc: Int, bc: Int): Color {
        var fc = fc
        var bc = bc
        if (fc > 255) {
            fc = 255
        }
        if (bc > 255) {
            bc = 255
        }
        val r = fc + random.nextInt(bc - fc - 16)
        val g = fc + random.nextInt(bc - fc - 14)
        val b = fc + random.nextInt(bc - fc - 18)
        return Color(r, g, b)
    }

    protected fun shearX(g: Graphics, img_width: Int, img_height: Int, color: Color?) {
        val period = Random.nextInt(img_width)
        val frames = 1
        val phase = Random.nextInt(2)
        for (i in 0 until img_height) {
            val d =
                (period shr 1).toDouble() * sin(i.toDouble() / period.toDouble() + (6.2831853071795862 * phase.toDouble()) / frames.toDouble())
            g.copyArea(0, i, img_width, 1, d.toInt(), 0)
            g.color = color
            g.drawLine(d.toInt(), i, 0, i)
            g.drawLine(d.toInt() + img_width, i, img_width, i)
        }
    }

    protected fun shearY(g: Graphics, img_width: Int, img_height: Int, color: Color?) {
        val period = Random.nextInt(img_height shr 1)
        val frames = 20
        val phase = 7
        for (i in 0 until img_width) {
            val d =
                (period shr 1).toDouble() * sin(i.toDouble() / period.toDouble() + (6.2831853071795862 * phase.toDouble()) / frames.toDouble())
            g.copyArea(i, 0, 1, img_height, 0, d.toInt())
            g.color = color
            g.drawLine(i, d.toInt(), i, 0)
            g.drawLine(i, d.toInt() + img_height, i, img_height)
        }
    }
}