package com.sochina.demo.utils

object Base64Utils {
    private const val BASE_LENGTH = 128
    private const val LOOKUP_LENGTH = 64
    private const val TWENTY_FOUR_BIT_GROUP = 24
    private const val EIGHT_BIT = 8
    private const val SIXTEEN_BIT = 16
    private const val FOUR_BYTE = 4
    private const val SIGN = -128
    private const val PAD = '='
    private val BASE64_ALPHABET = ByteArray(BASE_LENGTH)
    private val LOOK_UP_BASE64_ALPHABET = CharArray(LOOKUP_LENGTH)

    init {
        for (i in 0 until BASE_LENGTH) {
            BASE64_ALPHABET[i] = -1
        }
        run {
            var i = 'Z'.code
            while (i >= 'A'.code) {
                BASE64_ALPHABET[i] = (i - 'A'.code).toByte()
                i--
            }
        }
        run {
            var i = 'z'.code
            while (i >= 'a'.code) {
                BASE64_ALPHABET[i] = (i - 'a'.code + 26).toByte()
                i--
            }
        }
        run {
            var i = '9'.code
            while (i >= '0'.code) {
                BASE64_ALPHABET[i] = (i - '0'.code + 52).toByte()
                i--
            }
        }
        BASE64_ALPHABET['+'.code] = 62
        BASE64_ALPHABET['/'.code] = 63
        for (i in 0..25) {
            LOOK_UP_BASE64_ALPHABET[i] = ('A'.code + i).toChar()
        }
        run {
            var i = 26
            var j = 0
            while (i <= 51) {
                LOOK_UP_BASE64_ALPHABET[i] = ('a'.code + j).toChar()
                i++
                j++
            }
        }
        var i = 52
        var j = 0
        while (i <= 61) {
            LOOK_UP_BASE64_ALPHABET[i] = ('0'.code + j).toChar()
            i++
            j++
        }
        LOOK_UP_BASE64_ALPHABET[62] = '+'
        LOOK_UP_BASE64_ALPHABET[63] = '/'
    }

    private fun isWhiteSpace(octect: Char): Boolean {
        return (octect.code == 0x20 || octect.code == 0xd || octect.code == 0xa || octect.code == 0x9)
    }

    private fun isPad(octect: Char): Boolean {
        return (octect == PAD)
    }

    private fun isData(octect: Char): Boolean {
        return (octect.code < BASE_LENGTH && BASE64_ALPHABET[octect.code].toInt() != -1)
    }

    /**
     * Encodes hex octects into Base64
     *
     * @param binaryData Array containing binaryData
     * @return Encoded Base64 array
     */
    fun encode(binaryData: ByteArray?): String? {
        if (binaryData == null) {
            return null
        }
        val lengthDataBits = binaryData.size * EIGHT_BIT
        if (lengthDataBits == 0) {
            return ""
        }
        val fewerThan24bits = lengthDataBits % TWENTY_FOUR_BIT_GROUP
        val numberTriplets = lengthDataBits / TWENTY_FOUR_BIT_GROUP
        val numberQuartet = if (fewerThan24bits != 0) numberTriplets + 1 else numberTriplets
        var encodedData: CharArray? = null
        encodedData = CharArray(numberQuartet * 4)
        var k: Byte = 0
        var l: Byte = 0
        var b1: Byte = 0
        var b2: Byte = 0
        var b3: Byte = 0
        var encodedIndex = 0
        var dataIndex = 0
        for (i in 0 until numberTriplets) {
            b1 = binaryData[dataIndex++]
            b2 = binaryData[dataIndex++]
            b3 = binaryData[dataIndex++]
            l = (b2.toInt() and 0x0f).toByte()
            k = (b1.toInt() and 0x03).toByte()
            val val1 =
                if (((b1.toInt() and SIGN) == 0)) (b1.toInt() shr 2).toByte() else (b1.toInt() shr 2 xor 0xc0).toByte()
            val val2 =
                if (((b2.toInt() and SIGN) == 0)) (b2.toInt() shr 4).toByte() else (b2.toInt() shr 4 xor 0xf0).toByte()
            val val3 =
                if (((b3.toInt() and SIGN) == 0)) (b3.toInt() shr 6).toByte() else (b3.toInt() shr 6 xor 0xfc).toByte()
            encodedData[encodedIndex++] = LOOK_UP_BASE64_ALPHABET[val1.toInt()]
            encodedData[encodedIndex++] = LOOK_UP_BASE64_ALPHABET[val2.toInt() or (k.toInt() shl 4)]
            encodedData[encodedIndex++] = LOOK_UP_BASE64_ALPHABET[l.toInt() shl 2 or val3.toInt()]
            encodedData[encodedIndex++] = LOOK_UP_BASE64_ALPHABET[b3.toInt() and 0x3f]
        }
        // form integral number of 6-bit groups
        if (fewerThan24bits == EIGHT_BIT) {
            b1 = binaryData[dataIndex]
            k = (b1.toInt() and 0x03).toByte()
            val val1 =
                if (((b1.toInt() and SIGN) == 0)) (b1.toInt() shr 2).toByte() else (b1.toInt() shr 2 xor 0xc0).toByte()
            encodedData[encodedIndex++] = LOOK_UP_BASE64_ALPHABET[val1.toInt()]
            encodedData[encodedIndex++] = LOOK_UP_BASE64_ALPHABET[k.toInt() shl 4]
            encodedData[encodedIndex++] = PAD
            encodedData[encodedIndex++] = PAD
        } else if (fewerThan24bits == SIXTEEN_BIT) {
            b1 = binaryData[dataIndex]
            b2 = binaryData[dataIndex + 1]
            l = (b2.toInt() and 0x0f).toByte()
            k = (b1.toInt() and 0x03).toByte()
            val val1 =
                if (((b1.toInt() and SIGN) == 0)) (b1.toInt() shr 2).toByte() else (b1.toInt() shr 2 xor 0xc0).toByte()
            val val2 =
                if (((b2.toInt() and SIGN) == 0)) (b2.toInt() shr 4).toByte() else (b2.toInt() shr 4 xor 0xf0).toByte()
            encodedData[encodedIndex++] = LOOK_UP_BASE64_ALPHABET[val1.toInt()]
            encodedData[encodedIndex++] = LOOK_UP_BASE64_ALPHABET[val2.toInt() or (k.toInt() shl 4)]
            encodedData[encodedIndex++] = LOOK_UP_BASE64_ALPHABET[l.toInt() shl 2]
            encodedData[encodedIndex++] = PAD
        }
        return String(encodedData)
    }

    /**
     * Decodes Base64 data into octects
     *
     * @param encoded string containing Base64 data
     * @return Array containind decoded data.
     */
    fun decode(encoded: String?): ByteArray? {
        if (encoded == null) {
            return null
        }
        val base64Data = encoded.toCharArray()
        // remove white spaces
        val len = removeWhiteSpace(base64Data)
        if (len % FOUR_BYTE != 0) {
            // should be divisible by four
            return null
        }
        val numberQuadruple = (len / FOUR_BYTE)
        if (numberQuadruple == 0) {
            return ByteArray(0)
        }
        var decodedData: ByteArray? = null
        var b1: Byte = 0
        var b2: Byte = 0
        var b3: Byte = 0
        var b4: Byte = 0
        var d1 = 0.toChar()
        var d2 = 0.toChar()
        var d3 = 0.toChar()
        var d4 = 0.toChar()
        var i = 0
        var encodedIndex = 0
        var dataIndex = 0
        decodedData = ByteArray((numberQuadruple) * 3)
        while (i < numberQuadruple - 1) {
            // if found "no data" just return null
            if (!isData((base64Data[dataIndex++].also { d1 = it })) || !isData((base64Data[dataIndex++].also {
                    d2 = it
                }))
                || !isData((base64Data[dataIndex++].also { d3 = it })) || !isData((base64Data[dataIndex++].also {
                    d4 = it
                }))
            ) {
                return null
            }
            b1 = BASE64_ALPHABET[d1.code]
            b2 = BASE64_ALPHABET[d2.code]
            b3 = BASE64_ALPHABET[d3.code]
            b4 = BASE64_ALPHABET[d4.code]
            decodedData[encodedIndex++] = (b1.toInt() shl 2 or (b2.toInt() shr 4)).toByte()
            decodedData[encodedIndex++] = (((b2.toInt() and 0xf) shl 4) or ((b3.toInt() shr 2) and 0xf)).toByte()
            decodedData[encodedIndex++] = (b3.toInt() shl 6 or b4.toInt()).toByte()
            i++
        }
        // if found "no data" just return null
        if (!isData((base64Data[dataIndex++].also { d1 = it })) || !isData((base64Data[dataIndex++].also {
                d2 = it
            }))) {
            return null
        }
        b1 = BASE64_ALPHABET[d1.code]
        b2 = BASE64_ALPHABET[d2.code]
        d3 = base64Data[dataIndex++]
        d4 = base64Data[dataIndex++]
        // Check if they are PAD characters
        if (!isData((d3)) || !isData((d4))) {
            if (isPad(d3) && isPad(d4)) {
                // last 4 bits should be zero
                if ((b2.toInt() and 0xf) != 0) {
                    return null
                }
                val tmp = ByteArray(i * 3 + 1)
                System.arraycopy(decodedData, 0, tmp, 0, i * 3)
                tmp[encodedIndex] = (b1.toInt() shl 2 or (b2.toInt() shr 4)).toByte()
                return tmp
            } else if (!isPad(d3) && isPad(d4)) {
                b3 = BASE64_ALPHABET[d3.code]
                // last 2 bits should be zero
                if ((b3.toInt() and 0x3) != 0) {
                    return null
                }
                val tmp = ByteArray(i * 3 + 2)
                System.arraycopy(decodedData, 0, tmp, 0, i * 3)
                tmp[encodedIndex++] = (b1.toInt() shl 2 or (b2.toInt() shr 4)).toByte()
                tmp[encodedIndex] = (((b2.toInt() and 0xf) shl 4) or ((b3.toInt() shr 2) and 0xf)).toByte()
                return tmp
            } else {
                return null
            }
        } else { // No PAD e.g 3cQl
            b3 = BASE64_ALPHABET[d3.code]
            b4 = BASE64_ALPHABET[d4.code]
            decodedData[encodedIndex++] = (b1.toInt() shl 2 or (b2.toInt() shr 4)).toByte()
            decodedData[encodedIndex++] = (((b2.toInt() and 0xf) shl 4) or ((b3.toInt() shr 2) and 0xf)).toByte()
            decodedData[encodedIndex++] = (b3.toInt() shl 6 or b4.toInt()).toByte()
        }
        return decodedData
    }

    /**
     * remove WhiteSpace from MIME containing encoded Base64 data.
     *
     * @param data the byte array of base64 data (with WS)
     * @return the new length
     */
    private fun removeWhiteSpace(data: CharArray?): Int {
        if (data == null) {
            return 0
        }
        // count characters that's not whitespace
        var newSize = 0
        val len = data.size
        for (i in 0 until len) {
            if (!isWhiteSpace(data[i])) {
                data[newSize++] = data[i]
            }
        }
        return newSize
    }
}