package com.twofortyfouram.ci

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.File
import javax.xml.parsers.SAXParserFactory


open class DetektPrintTask : DefaultTask() {

    @get:InputFile
    lateinit var reportFile: File

    @TaskAction
    fun doIt() {
        val checkStyleReport = parseXml(reportFile)

        val projectDir = project.projectDir

        println("::group::Detekt Errors")
        checkStyleReport.forEach {
            println(it.formatForGitHub(projectDir))
        }
        println("::endgroup::")
    }

    companion object {

        fun parseXml(file: File): Collection<CheckStyleFile> {
            val handler = CheckStyleHandler()
            SAXParserFactory.newInstance().newSAXParser().parse(file, handler)
            return handler.getFiles()
        }

        fun CheckStyleFile.formatForGitHub(projectDir: File): String {
            return this.errors.joinToString(separator = "\n") {
                "::error file=${this.name.toRelativeString(projectDir)},${it.formatForGitHub()}"
            }
        }
        fun CheckStyleError.formatForGitHub() = "line=${this.line},col=${this.column}::{${this.message}}"
    }

    class CheckStyleHandler : DefaultHandler() {
        private val files = mutableListOf<CheckStyleFile>()

        private var currentFileName: String? = null
        private var currentErrors = mutableListOf<CheckStyleError>()

        override fun startElement(uri: String?, lName: String?, qName: String?, attr: Attributes?) {
            when (qName) {
                TAG_FILE -> {
                    currentFileName = attr?.getValue(ATTRIBUTE_FILE_NAME)
                    currentErrors = mutableListOf()
                }
                TAG_ERROR -> {
                    attr?.let {
                        val line = it.getValue(ATTRIBUTE_ERROR_LINE).toInt()
                        val column = it.getValue(ATTRIBUTE_ERROR_COLUMN).toInt()
                        val severity = it.getValue(ATTRIBUTE_ERROR_SEVERITY)
                        val message = it.getValue(ATTRIBUTE_ERROR_MESSAGE)
                        val source = it.getValue(ATTRIBUTE_ERROR_SOURCE)

                        currentErrors.add(CheckStyleError(line, column, severity, message, source))
                    }
                }

            }
        }

        override fun endElement(uri: String?, localName: String?, qName: String?) {
            when (qName) {
                TAG_FILE -> {
                    currentFileName?.let { currentFileName ->
                        files.add(CheckStyleFile(File(currentFileName), currentErrors))
                    }
                }
            }
        }

        fun getFiles() = files

        companion object {
            const val TAG_FILE = "file"
            const val ATTRIBUTE_FILE_NAME = "name"

            const val TAG_ERROR = "error"
            const val ATTRIBUTE_ERROR_LINE = "line"
            const val ATTRIBUTE_ERROR_COLUMN = "column"
            const val ATTRIBUTE_ERROR_SEVERITY = "severity"
            const val ATTRIBUTE_ERROR_MESSAGE = "message"
            const val ATTRIBUTE_ERROR_SOURCE = "source"

        }
    }
}

data class CheckStyleFile(val name: File, val errors: List<CheckStyleError>)

data class CheckStyleError(val line: Int, val column: Int, val severity: String, val message: String, val source: String)
