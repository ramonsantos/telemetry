package io.github.ramonsantos.telemetry.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TelemetryController {

    @GetMapping("/")
    fun index() = "Ramon!"

}