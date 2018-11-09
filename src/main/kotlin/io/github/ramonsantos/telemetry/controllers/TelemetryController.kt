package io.github.ramonsantos.telemetry.controllers

import io.github.ramonsantos.telemetry.services.OSDataCollectorService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class TelemetryController {

    val osDataCollector = OSDataCollectorService()

    @GetMapping("/status")
    fun status(): Map<String, Any> {

        val result = HashMap<String, Any>()
        result["uptime"] = osDataCollector.getUptime()
        result["kernel"] = osDataCollector.getKernelInfo()
        result["linux_distribution"] = osDataCollector.getOSInfo()
        result["memory"] = osDataCollector.getMemoryInfo()
        result["disk"] = osDataCollector.getDiskInfo()

        return result
    }
}
