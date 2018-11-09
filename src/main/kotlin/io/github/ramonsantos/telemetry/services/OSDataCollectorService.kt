package io.github.ramonsantos.telemetry.services

import java.io.IOException
import java.time.LocalTime
import java.util.*

class OSDataCollectorService {

    companion object {
        private const val OS_INFO = 1
        private const val MEMORY_INFO = 2
        private const val FREE_DISK_INFO = 3
        private const val USED_DISK_INFO = 4
        private const val UNAVAILABLE = "unavailable"
        private const val SEC_IN_DAY = 86400
    }

    fun getUptime(): String {
        try {
            var uptimeOut = this.getOutOSCommand("cat /proc/uptime")
            uptimeOut = uptimeOut.substring(0, uptimeOut.indexOf('.'))

            val timeIn = Integer.parseInt(uptimeOut)

            val secOfDay = timeIn % SEC_IN_DAY
            val days = timeIn / SEC_IN_DAY

            val timeOfDay = LocalTime.ofSecondOfDay(secOfDay.toLong())
            val time = timeOfDay.toString()

            return days.toString() + ":" + time

        } catch (ex: IOException) {
            return Companion.UNAVAILABLE
        }
    }

    fun getKernelInfo(): String {
        try {
            return this.getOutOSCommand("uname -o -m -v -r").trim()
        } catch (ex: IOException) {
            return Companion.UNAVAILABLE
        }
    }

    fun getOSInfo(): String {
        try {
            var osInfo = this.getOutScript(OS_INFO)
            osInfo = osInfo.replace("PRETTY_NAME=".toRegex(), "").replace("\"".toRegex(), "")

            return osInfo

        } catch (ex: IOException) {
            return Companion.UNAVAILABLE
        }
    }

    fun getMemoryInfo(): Map<String, String> {
        val result = HashMap<String, String>()

        try {
            val values = this.getOutScript(MEMORY_INFO).replace("\n".toRegex(), "").split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            result["total"] = values[0]
            result["used"] = values[1]
            result["free"] = values[2]
            result["buff/cache"] = values[3]

        } catch (ex: IOException) {
            result["total"] = Companion.UNAVAILABLE
            result["used"] = Companion.UNAVAILABLE
            result["free"] = Companion.UNAVAILABLE
            result["buff/cache"] = Companion.UNAVAILABLE
        }

        return result
    }

    fun getDiskInfo(): Map<String, String> {
        val result = HashMap<String, String>()

        try {
            val freeSpace = this.getSpaceDisk(FREE_DISK_INFO)
            val usedSpace = this.getSpaceDisk(USED_DISK_INFO)

            result["total"] = (freeSpace + usedSpace).toString() + ""
            result["used"] = usedSpace.toString()
            result["free"] = freeSpace.toString()

        } catch (ex: IOException) {
            result["total"] = Companion.UNAVAILABLE
            result["used"] = Companion.UNAVAILABLE
            result["free"] = Companion.UNAVAILABLE
        }

        return result
    }

    private fun getPathScript(): String {
        return javaClass.getResource("/data_collector.sh").path
    }

    @Throws(IOException::class)
    private fun getOutScript(option: Int): String {
        return this.getOutOSCommand("bash " + this.getPathScript() + " " + option)
    }

    @Throws(IOException::class)
    private fun getOutOSCommand(command: String): String {
        val commandOut = StringBuilder()
        val child = Runtime.getRuntime().exec(command)
        val input = child.inputStream
        var c = input.read()

        while (c != -1) {
            commandOut.append(c.toChar())
            c = input.read()
        }

        input.close()

        return commandOut.toString()
    }

    @Throws(IOException::class)
    private fun getSpaceDisk(option: Int): Int {
        var disk = 0
        val out = this.getOutScript(option)
        val array = out.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for (i in array.indices) {
            disk = disk!! + Integer.parseInt(array[i])
        }

        return disk
    }
}
