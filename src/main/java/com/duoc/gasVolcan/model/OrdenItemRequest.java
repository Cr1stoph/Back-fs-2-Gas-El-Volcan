package com.duoc.gasVolcan.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrdenItemRequest(@NotNull Long productoId, @Min(1) int cantidad) {
}



