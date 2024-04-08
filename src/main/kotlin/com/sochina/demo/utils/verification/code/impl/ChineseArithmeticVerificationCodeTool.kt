package com.sochina.demo.utils.verification.code.impl

import com.sochina.demo.constants.Constants
import com.sochina.demo.domain.VerificationCode
import com.sochina.demo.utils.verification.code.IVerificationCodeTool
import jakarta.enterprise.context.ApplicationScoped
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.Random

@ApplicationScoped
class ChineseArithmeticVerificationCodeTool : IVerificationCodeTool() {
    private val logger: Logger = LoggerFactory.getLogger(ChineseArithmeticVerificationCodeTool::class.java)
    private val DISTURB_LINE_SIZE = 15
    private val CVC_NUMBERS = arrayOf(
        "\u96F6",
        "\u4E00",
        "\u4E8C",
        "\u4E09",
        "\u56DB",
        "\u4E94",
        "\u516D",
        "\u4E03",
        "\u516B",
        "\u4E5D",
        "\u5341",
        "\u4E58",
        "\u9664",
        "\u52A0",
        "\u51CF"
    )
    private val OP_MAP: MutableMap<String, Int> = HashMap()

    init {
        OP_MAP[Constants.TAKE] = 11;
        OP_MAP[Constants.REMOVE] = 12;
        OP_MAP[Constants.ADD] = 13;
        OP_MAP[Constants.REDUCE] = 14;
    }

    private val font = Font("黑体", Font.BOLD, 18)
    private var xyResult = 0
    private var randomString: String? = null

    override fun createVerificationCodeImage(img_width: Int, img_height: Int): VerificationCode {
        val image = BufferedImage(img_width, img_height, BufferedImage.TYPE_INT_BGR)
        val g = image.graphics
        g.color = Color.WHITE
        g.fillRect(0, 0, img_width, img_height)
        g.color = getRandomColor(200, 250)
        g.drawRect(0, 0, img_width - 2, img_height - 2)
        g.color = getRandomColor(200, 255)
        shearX(g, img_width, img_height, getRandomColor(200, 255))
        shearY(g, img_width, img_height, getRandomColor(200, 255))
        for (i in 0 until DISTURB_LINE_SIZE) {
            drawDisturbLineAdd(g, 13, 15, img_width, img_height, getRandomColor(200, 255))
            drawDisturbLineReduce(g, 13, 15, img_width, img_height, getRandomColor(200, 255))
        }
        getRandomMathString()
        logger.debug("验证码：{}", randomString)
        logger.debug("验证码结果：{}", xyResult)
        val logsu = StringBuffer()
        var j = 0
        val k = randomString!!.length
        while (j < k) {
            var chid = 0
            chid = if (j == 1) {
                OP_MAP[randomString!![j].toString()]!!
            } else {
                randomString!![j].toString().toInt()
            }
            val ch = CVC_NUMBERS[chid]
            logsu.append(ch)
            drawRandomString(g as Graphics2D, ch, j)
            j++
        }
        drawRandomString(g as Graphics2D, "\u7B49\u4E8E\uFF1F", 3)
        logsu.append("\u7B49\u4E8E \uFF1F")
        logger.debug("汉字验证码 : {}", logsu)
        randomString = logsu.toString()
        g.dispose()
        val verificationCode = VerificationCode()
        verificationCode.image = image
        verificationCode.code = xyResult.toString()
        return verificationCode
    }

    private fun getRandomMathString() {
        val xx = random.nextInt(10)
        val yy = random.nextInt(10)
        val suChinese = StringBuilder()
        val randomOperations = Math.round(Math.random() * 2).toInt()
        if (randomOperations == 0) {
            this.xyResult = yy * xx
            suChinese.append(yy)
            suChinese.append(Constants.TAKE)
            suChinese.append(xx)
        } else if (randomOperations == 1) {
            if (xx != 0 && (yy % xx == 0)) {
                this.xyResult = yy / xx
                suChinese.append(yy)
                suChinese.append(Constants.REMOVE)
                suChinese.append(xx)
            } else {
                this.xyResult = yy + xx
                suChinese.append(yy)
                suChinese.append(Constants.ADD)
                suChinese.append(xx)
            }
        } else if (randomOperations == 2) {
            this.xyResult = yy - xx
            suChinese.append(yy)
            suChinese.append(Constants.REDUCE)
            suChinese.append(xx)
        } else {
            this.xyResult = yy + xx
            suChinese.append(yy)
            suChinese.append(Constants.ADD)
            suChinese.append(xx)
        }
        this.randomString = suChinese.toString()
    }

    private fun drawRandomString(g: Graphics2D, randomString: String, i: Int) {
        g.font = font
        val rc = random.nextInt(255)
        val gc = random.nextInt(255)
        val bc = random.nextInt(255)
        g.color = Color(rc, gc, bc)
        val x = random.nextInt(3)
        val y = random.nextInt(2)
        g.translate(x, y)
        val degree = Random().nextInt() % 15
        g.rotate(degree * Math.PI / 180, (5 + i * 25).toDouble(), 20.0)
        g.drawString(randomString, 5 + i * 25, 20)
        g.rotate(-degree * Math.PI / 180, (5 + i * 25).toDouble(), 20.0)
    }
}