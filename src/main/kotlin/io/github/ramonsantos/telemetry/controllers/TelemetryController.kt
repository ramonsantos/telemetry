package io.github.ramonsantos.telemetry.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import io.github.ramonsantos.telemetry.service.OSDataCollectorService

@RestController
class TelemetryController(val osDataCollector: OSDataCollectorService = OSDataCollectorService()) {

    @GetMapping("/status")
    fun status(): Map<String, Any> {
        val result = HashMap<String, Any>()

        result["uptime"] = osDataCollector.uptime
        result["kernel"] = osDataCollector.kernelInfo
        result["linux_distribution"] = osDataCollector.osInfo
        result["memory"] = osDataCollector.memoryInfo
        result["disk"] = osDataCollector.diskInfo

        return result
    }

}


