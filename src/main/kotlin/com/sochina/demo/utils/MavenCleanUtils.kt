package com.sochina.demo.utils

import cn.hutool.core.io.FileUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

object MavenCleanUtils {

    private val LOGGER: Logger = LoggerFactory.getLogger(MavenCleanUtils::class.java)

    private var patternList: List<String> = emptyList()

    init {
        patternList = listOf(
            ".*lastUpdated.*",
            ".*-in-progress",
            ".*maven-metadata-.*\\.xml.*",
            ".*resolver-status\\.properties",
            ".*_remote.repositories.*",
            "unknown",
        )
    }


    fun clean(url: File) {
        // handleFile(url)
        val patterList = listOf(
            ".*lastUpdated.*",
            ".*-in-progress",
            ".*maven-metadata-.*\\.xml.*",
            ".*resolver-status\\.properties",
            ".*_remote.repositories.*",
            "unknown",
        )
        handleFile(url, patterList)
        handleEmptyDirectory(url)
    }

    fun handleFile(url: File) {
        url.listFiles()?.forEach {
            if (it.isDirectory) {
                handleFile(it)
            } else {
                if (isMatchPattern(it.name, patternList)) {
                    it.delete()
                    LOGGER.info("file ${it.absolutePath} delete")
                }
            }
        }
    }

    fun handleFile(url: File, list: List<String>) {
        url.listFiles()?.forEach {
            if (it.isDirectory) {
                handleFile(it)
            } else {
                if (isMatchPattern(it.name, list)) {
                    it.delete()
                    LOGGER.info("file ${it.absolutePath} delete")
                }
            }
        }
    }

    private fun handleEmptyDirectory(url: File) {
        url.listFiles()?.forEach {
            if (FileUtil.isDirEmpty(it)) {
                it.delete()
                LOGGER.info("folder ${it.absolutePath} delete")
            }
        }
    }

    private fun isMatchPattern(url: String, list: List<String>): Boolean {
        return StringUtils.matches(url, list)
    }
}