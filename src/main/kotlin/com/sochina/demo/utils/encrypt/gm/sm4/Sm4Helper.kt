package com.sochina.demo.utils.encrypt.gm.sm4

enum class Sm4Helper(var msg: String) {
    ENCODING("UTF-8"),
    ALGORITHM_NAME("SM4"),
    ALGORITHM_NAME_CBC_PADDING("SM4/CBC/PKCS7Padding"),
    ALGORITHM_NAME_ECB_PADDING("SM4/ECB/PKCS5Padding"),
    ;
}
