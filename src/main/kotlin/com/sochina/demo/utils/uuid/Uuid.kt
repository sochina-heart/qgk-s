package com.sochina.demo.utils.uuid

import java.io.Serializable
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

class Uuid : Serializable, Comparable<Uuid> {
    /**
     * 返回此 UUID 的 128 位值中的最高有效 64 位。
     *
     * @return 此 UUID 的 128 位值中最高有效 64 位。
     */
    /**
     * 此UUID的最高64有效位
     */
    private val mostSignificantBits: Long
    /**
     * 返回此 UUID 的 128 位值中的最低有效 64 位。
     *
     * @return 此 UUID 的 128 位值中的最低有效 64 位。
     */
    /**
     * 此UUID的最低64有效位
     */
    private val leastSignificantBits: Long

    /**
     * 私有构造
     *
     * @param data 数据
     */
    private constructor(data: ByteArray) {
        var msb: Long = 0
        var lsb: Long = 0
        assert(data.size == 16) { "data must be 16 bytes in length" }
        for (i in 0..7) {
            msb = (msb shl 8) or (data[i].toInt() and 0xff).toLong()
        }
        for (i in 8..15) {
            lsb = (lsb shl 8) or (data[i].toInt() and 0xff).toLong()
        }
        this.mostSignificantBits = msb
        this.leastSignificantBits = lsb
    }

    /**
     * 使用指定的数据构造新的 UUID。
     *
     * @param mostSigBits  用于 `UUID` 的最高有效 64 位
     * @param leastSigBits 用于 `UUID` 的最低有效 64 位
     */
    constructor(mostSigBits: Long, leastSigBits: Long) {
        this.mostSignificantBits = mostSigBits
        this.leastSignificantBits = leastSigBits
    }

    /**
     * 与此 `UUID` 相关联的版本号. 版本号描述此 `UUID` 是如何生成的。
     *
     *
     * 版本号具有以下含意:
     *
     *  * 1 基于时间的 UUID
     *  * 2 DCE 安全 UUID
     *  * 3 基于名称的 UUID
     *  * 4 随机生成的 UUID
     *
     *
     * @return 此 `UUID` 的版本号
     */
    fun version(): Int {
        // Version is bits masked by 0x000000000000F000 in MS long
        return ((mostSignificantBits shr 12) and 0x0fL).toInt()
    }

    /**
     * 与此 `UUID` 相关联的变体号。变体号描述 `UUID` 的布局。
     *
     *
     * 变体号具有以下含意：
     *
     *  * 0 为 NCS 向后兼容保留
     *  * 2 [&nbsp;&nbsp;IETF&nbsp;RFC&nbsp;4122(Leach-Salz'" DESIGNTIMESP=518>http://www.ietf.org/rfc/rfc4122.txt">IETF&nbsp;RFC&nbsp;4122](IETF&nbsp;RFC&nbsp;4122(Leach-Salz')(Leach-Salz), 用于此类
     *  * 6 保留，微软向后兼容
     *  * 7 保留供以后定义使用
     *
     *
     * @return 此 `UUID` 相关联的变体号
     */
    fun variant(): Int {
        // This field is composed of a varying number of bits.
        // 0 - - Reserved for NCS backward compatibility
        // 1 0 - The IETF aka Leach-Salz variant (used by this class)
        // 1 1 0 Reserved, Microsoft backward compatibility
        // 1 1 1 Reserved for future definition.
        return ((leastSignificantBits ushr (64 - (leastSignificantBits ushr 62)).toInt()) and (leastSignificantBits shr 63)).toInt()
    }

    /**
     * 与此 UUID 相关联的时间戳值。
     *
     *
     *
     * 60 位的时间戳值根据此 `UUID` 的 time_low、time_mid 和 time_hi 字段构造。<br></br>
     * 所得到的时间戳以 100 毫微秒为单位，从 UTC（通用协调时间） 1582 年 10 月 15 日零时开始。
     *
     *
     *
     * 时间戳值仅在在基于时间的 UUID（其 version 类型为 1）中才有意义。<br></br>
     * 如果此 `UUID` 不是基于时间的 UUID，则此方法抛出 UnsupportedOperationException。
     *
     * @throws UnsupportedOperationException 如果此 `UUID` 不是 version 为 1 的 UUID。
     */
    @Throws(UnsupportedOperationException::class)
    fun timestamp(): Long {
        checkTimeBase()
        return (mostSignificantBits and 0x0FFFL) shl 48 or (((mostSignificantBits shr 16) and 0x0FFFFL) shl 32
                ) or (mostSignificantBits ushr 32)
    }

    /**
     * 与此 UUID 相关联的时钟序列值。
     *
     *
     *
     * 14 位的时钟序列值根据此 UUID 的 clock_seq 字段构造。clock_seq 字段用于保证在基于时间的 UUID 中的时间唯一性。
     *
     *
     * `clockSequence` 值仅在基于时间的 UUID（其 version 类型为 1）中才有意义。 如果此 UUID 不是基于时间的 UUID，则此方法抛出 UnsupportedOperationException。
     *
     * @return 此 `UUID` 的时钟序列
     * @throws UnsupportedOperationException 如果此 UUID 的 version 不为 1
     */
    @Throws(UnsupportedOperationException::class)
    fun clockSequence(): Int {
        checkTimeBase()
        return ((leastSignificantBits and 0x3FFF000000000000L) ushr 48).toInt()
    }

    /**
     * 与此 UUID 相关的节点值。
     *
     *
     *
     * 48 位的节点值根据此 UUID 的 node 字段构造。此字段旨在用于保存机器的 IEEE 802 地址，该地址用于生成此 UUID 以保证空间唯一性。
     *
     *
     * 节点值仅在基于时间的 UUID（其 version 类型为 1）中才有意义。<br></br>
     * 如果此 UUID 不是基于时间的 UUID，则此方法抛出 UnsupportedOperationException。
     *
     * @return 此 `UUID` 的节点值
     * @throws UnsupportedOperationException 如果此 UUID 的 version 不为 1
     */
    @Throws(UnsupportedOperationException::class)
    fun node(): Long {
        checkTimeBase()
        return leastSignificantBits and 0x0000FFFFFFFFFFFFL
    }

    /**
     * 返回此`UUID` 的字符串表现形式。
     *
     *
     *
     * UUID 的字符串表示形式由此 BNF 描述：
     *
     * <pre>
     * `UUID                   = <time_low>-<time_mid>-<time_high_and_version>-<variant_and_sequence>-<node>
     * time_low               = 4*<hexOctet>
     * time_mid               = 2*<hexOctet>
     * time_high_and_version  = 2*<hexOctet>
     * variant_and_sequence   = 2*<hexOctet>
     * node                   = 6*<hexOctet>
     * hexOctet               = <hexDigit><hexDigit>
     * hexDigit               = [0-9a-fA-F]
    ` *
    </pre> *
     *
     *
     *
     * @return 此{@code UUID} 的字符串表现形式
     * @see .toString
     */
    override fun toString(): String {
        return toString(false)
    }

    /**
     * 返回此`UUID` 的字符串表现形式。
     *
     *
     *
     * UUID 的字符串表示形式由此 BNF 描述：
     *
     * <pre>
     * `UUID                   = <time_low>-<time_mid>-<time_high_and_version>-<variant_and_sequence>-<node>
     * time_low               = 4*<hexOctet>
     * time_mid               = 2*<hexOctet>
     * time_high_and_version  = 2*<hexOctet>
     * variant_and_sequence   = 2*<hexOctet>
     * node                   = 6*<hexOctet>
     * hexOctet               = <hexDigit><hexDigit>
     * hexDigit               = [0-9a-fA-F]
    ` *
    </pre> *
     *
     *
     *
     * @param isSimple 是否简单模式，简单模式为不带'-'的UUID字符串
     * @return 此{@code UUID} 的字符串表现形式
     */
    fun toString(isSimple: Boolean): String {
        val separator = if (isSimple) "" else "-"
        return buildString {
            append(digits(mostSignificantBits shr 32, 8))
            append(separator)
            append(digits(mostSignificantBits shr 16, 4))
            append(separator)
            append(digits(mostSignificantBits, 4))
            append(separator)
            append(digits(leastSignificantBits shr 48, 4))
            append(separator)
            append(digits(leastSignificantBits, 12))
        }
    }
    // Comparison Operations
    /**
     * 返回此 UUID 的哈希码。
     *
     * @return UUID 的哈希码值。
     */
    override fun hashCode(): Int {
        val hilo = mostSignificantBits xor leastSignificantBits
        return ((hilo shr 32).toInt()) xor hilo.toInt()
    }
    // -------------
    // Private method start
    /**
     * 将此对象与指定对象比较。
     *
     *
     * 当且仅当参数不为 `null`、而是一个 UUID 对象、具有与此 UUID 相同的 varriant、包含相同的值（每一位均相同）时，结果才为 `true`。
     *
     * @param other 要与之比较的对象
     * @return 如果对象相同，则返回 `true`；否则返回 `false`
     */
    override fun equals(other: Any?): Boolean {
        if ((null == other) || (other.javaClass != UUID::class.java)) {
            return false
        }
        val id = other as UUID
        return (mostSignificantBits == id.mostSignificantBits && leastSignificantBits == id.leastSignificantBits)
    }

    /**
     * 将此 UUID 与指定的 UUID 比较。
     *
     *
     *
     * 如果两个 UUID 不同，且第一个 UUID 的最高有效字段大于第二个 UUID 的对应字段，则第一个 UUID 大于第二个 UUID。
     *
     * @param other 与此 UUID 比较的 UUID
     * @return 在此 UUID 小于、等于或大于 val 时，分别返回 -1、0 或 1。
     */
    override fun compareTo(other: Uuid): Int {
        // The ordering is intentionally set up so that the UUIDs
        // can simply be numerically compared as two numbers
        return (if (this.mostSignificantBits < other.mostSignificantBits) -1 else (if (this.mostSignificantBits > other.mostSignificantBits) 1 else (if (this.leastSignificantBits < other.leastSignificantBits) -1 else (if (this.leastSignificantBits > other.leastSignificantBits) 1 else 0))))
    }

    /**
     * 检查是否为time-based版本UUID
     */
    private fun checkTimeBase() {
        if (version() != 1) {
            throw UnsupportedOperationException("Not a time-based UUID")
        }
    }

    /**
     * SecureRandom 的单例
     */
    private object Holder {
        val NUMBER_GENERATOR: SecureRandom = secureRandom
    }

    companion object {
        private const val serialVersionUID = -1185015143654744140L

        /**
         * 获取类型 4（伪随机生成的）UUID 的静态工厂。 使用加密的本地线程伪随机数生成器生成该 UUID。
         *
         * @return 随机生成的 `UUID`
         */
        @JvmStatic
        fun fastUUID(): Uuid {
            return randomUUID(false)
        }
        /**
         * 获取类型 4（伪随机生成的）UUID 的静态工厂。 使用加密的强伪随机数生成器生成该 UUID。
         *
         * @param isSecure 是否使用[SecureRandom]如果是可以获得更安全的随机码，否则可以得到更好的性能
         * @return 随机生成的 `UUID`
         */
        /**
         * 获取类型 4（伪随机生成的）UUID 的静态工厂。 使用加密的强伪随机数生成器生成该 UUID。
         *
         * @return 随机生成的 `UUID`
         */
        @JvmOverloads
        fun randomUUID(isSecure: Boolean = true): Uuid {
            val ng = if (isSecure) Holder.NUMBER_GENERATOR else random
            val randomBytes = ByteArray(16)
            ng.nextBytes(randomBytes)
            /* clear version */
            randomBytes[6] = (randomBytes[6].toInt() and 0x0f).toByte()
            /* set to version 4 */
            randomBytes[6] = (randomBytes[6].toInt() or 0x40).toByte()
            /* clear variant */
            randomBytes[8] = (randomBytes[8].toInt() and 0x3f).toByte()
            /* set to IETF variant */
            randomBytes[8] = (randomBytes[8].toInt() or 0x80).toByte()
            return Uuid(randomBytes)
        }

        /**
         * 根据指定的字节数组获取类型 3（基于名称的）UUID 的静态工厂。
         *
         * @param name 用于构造 UUID 的字节数组。
         * @return 根据指定数组生成的 `UUID`
         */
        fun nameUUIDFromBytes(name: ByteArray?): Uuid {
            val md: MessageDigest
            try {
                md = MessageDigest.getInstance("MD5")
            } catch (nsae: NoSuchAlgorithmException) {
                throw InternalError("MD5 not supported")
            }
            val md5Bytes = md.digest(name)
            /* clear version */
            md5Bytes[6] = (md5Bytes[6].toInt() and 0x0f).toByte()
            /* set to version 3 */
            md5Bytes[6] = (md5Bytes[6].toInt() or 0x30).toByte()
            /* clear variant */
            md5Bytes[8] = (md5Bytes[8].toInt() and 0x3f).toByte()
            /* set to IETF variant */
            md5Bytes[8] = (md5Bytes[8].toInt() or 0x80).toByte()
            return Uuid(md5Bytes)
        }

        /**
         * 根据 [.toString] 方法中描述的字符串标准表示形式创建`UUID`。
         *
         * @param name 指定 `UUID` 字符串
         * @return 具有指定值的 `UUID`
         * @throws IllegalArgumentException 如果 name 与 [.toString] 中描述的字符串表示形式不符抛出此异常
         */
        fun fromString(name: String): Uuid {
            val components = name.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            require(components.size == 5) { "Invalid UUID string: $name" }
            for (i in 0..4) {
                components[i] = "0x" + components[i]
            }
            var mostSigBits = java.lang.Long.decode(components[0])
            mostSigBits = mostSigBits shl 16
            mostSigBits = mostSigBits or java.lang.Long.decode(components[1])
            mostSigBits = mostSigBits shl 16
            mostSigBits = mostSigBits or java.lang.Long.decode(components[2])
            var leastSigBits = java.lang.Long.decode(components[3])
            leastSigBits = leastSigBits shl 48
            leastSigBits = leastSigBits or java.lang.Long.decode(components[4])
            return Uuid(mostSigBits, leastSigBits)
        }

        /**
         * 返回指定数字对应的hex值
         *
         * @param val    值
         * @param digits 位
         * @return 值
         */
        private fun digits(`val`: Long, digits: Int): String {
            val hi = 1L shl (digits * 4)
            return java.lang.Long.toHexString(hi or (`val` and (hi - 1))).substring(1)
        }

        val secureRandom: SecureRandom
            /**
             * 获取[SecureRandom]，类提供加密的强随机数生成器 (RNG)
             *
             * @return [SecureRandom]
             */
            get() {
                try {
                    return SecureRandom.getInstance("SHA1PRNG")
                } catch (e: NoSuchAlgorithmException) {
                    throw Exception(e)
                }
            }
        val random: ThreadLocalRandom
            /**
             * 获取随机数生成器对象<br></br>
             * ThreadLocalRandom是JDK 7之后提供并发产生随机数，能够解决多个线程发生的竞争争夺。
             *
             * @return [ThreadLocalRandom]
             */
            get() = ThreadLocalRandom.current()
    }
}
