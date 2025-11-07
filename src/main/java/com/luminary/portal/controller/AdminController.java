package com.luminary.portal.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin APIs", description = "Endpoints available only to administrators for managing system data")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    // future admin endpoints
}
