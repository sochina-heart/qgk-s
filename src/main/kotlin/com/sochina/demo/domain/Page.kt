package com.sochina.demo.domain

open class Page<T> {

    var records: List<T>? = null

    var pageNumber: Int = 0

    var pageSize: Int = 0

    var totalPage: Long = 0

    var totalRow: Long = 0
}