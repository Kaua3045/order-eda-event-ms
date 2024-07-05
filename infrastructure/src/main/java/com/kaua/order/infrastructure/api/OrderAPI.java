package com.kaua.order.infrastructure.api;

import com.kaua.order.infrastructure.models.OrderCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Order API", description = "Order API for managing orders")
@RequestMapping(value = "v1/orders")
public interface OrderAPI {

    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "422", description = "Invalid order data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<?> createOrder(@RequestBody OrderCreateRequest orderCreateRequest);
}
