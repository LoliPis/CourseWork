package com.corsework.coursework.controllers;

import com.corsework.coursework.SocksDTO;
import com.corsework.coursework.model.Enums.TypeOfOperation;
import com.corsework.coursework.services.OperationHistoryService;
import com.corsework.coursework.services.SocksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/socks")
@Tag(name = "Носки", description = "CRUD-операции и другие эндпоинты для работы с носками")
public class SocksController {

    private final SocksService socksService;
    private final OperationHistoryService operationHistoryService;

    public SocksController(SocksService socksService, OperationHistoryService operationHistoryService) {
        this.socksService = socksService;
        this.operationHistoryService = operationHistoryService;
    }

    @PostMapping
    @Operation(
            summary = "Регистрирует приход товара на склад",
            description = "Можно добавить носки в соответствии со схемой объекта"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "удалось добавить приход"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "параметры запроса отсутствуют или имеют некорректный формат"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "произошла ошибка, не зависящая от вызывающей стороны"
            )
    })
    public ResponseEntity<Integer> addSocks(@RequestBody SocksDTO socksDTO) {
        if (socksService.addSocks(socksDTO) != null) {
            operationHistoryService.addOperation(SocksDTO.convertDtoToSocks(socksDTO), TypeOfOperation.ACCEPTANCE, socksDTO.getQuantity());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{color}&{size}&{cottonMin}&{cottonMax}")
    @Operation(
            summary = "Возвращает общее количество носков на складе, соответствующих переданным в параметрах" +
                    " критериям запроса. ",
            description = "Можно получить общее чисто пар носков по номеру цвету, размеру, минимальному и " +
                    "максимальному содержанию хлопка"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "запрос выполнен, результат в теле ответа в виде строкового представления целого числа"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "параметры запроса отсутствуют или имеют некорректный формат"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "произошла ошибка, не зависящая от вызывающей стороны"
            )
    })
    public ResponseEntity<Integer> getSocks(@PathVariable String color, int size, int cottonMin, int cottonMax) {
        int socksCount =  socksService.getSocks(color, size, cottonMin, cottonMax);
        if (socksCount == 0){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(socksCount);
    }

    @PutMapping
    @GetMapping("/putSocks")
    @Operation(
            summary = "Регистрирует отпуск носков со склада",
            description = "Можно получить общее чисто пар носков по номеру цвету, размеру, минимальному и максимальному " +
                    "содержанию хлопка"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "удалось произвести отпуск носков со склада"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "товара нет на складе в нужном количестве или параметры запроса имеют некорректный формат"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "произошла ошибка, не зависящая от вызывающей стороны"
            )
    })
    public ResponseEntity<Object> editSocks(@RequestBody SocksDTO socksDTO) {
        boolean socks1 = socksService.editSocks(socksDTO);
        if (socks1) {
            operationHistoryService.addOperation(SocksDTO.convertDtoToSocks(socksDTO), TypeOfOperation.SHIPMENT, socksDTO.getQuantity());
            return ResponseEntity.ok(socks1);
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping
    @GetMapping("/deleteSocks")
    @Operation(
            summary = "Регистрирует списание испорченных (бракованных) носков",
            description = "Можно списать носки по количеству пар"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "запрос выполнен, товар списан со склада"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "параметры запроса отсутствуют или имеют некорректный формат"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "произошла ошибка, не зависящая от вызывающей стороны"
            )
    })
    public ResponseEntity<Void> deleteSocks(@RequestBody SocksDTO socksDTO){
        if (socksService.deleteSocks(socksDTO)) {
            operationHistoryService.addOperation(SocksDTO.convertDtoToSocks(socksDTO), TypeOfOperation.WRITTEN_OFF, socksDTO.getQuantity());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
