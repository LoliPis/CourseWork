package com.corsework.coursework.controllers;

import com.corsework.coursework.model.Enums.TypeOfOperation;
import com.corsework.coursework.model.OperationHistory;
import com.corsework.coursework.model.Socks;
import com.corsework.coursework.services.FileService;
import com.corsework.coursework.services.OperationHistoryService;
import com.corsework.coursework.services.SocksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Map;

@RestController
@RequestMapping("/files")
@Tag(name = "Импорт/экспорт файлов", description = "Контроллер позволяет делать импорт/экспорт фалов носков " +
        "и файлов истории операций")
public class FilesController {

    @Value("socks.json")
    private String socksFileName;

    @Value("operationHistory.json")
    private String operationHistoryFileName;

    private final OperationHistoryService operationHistoryService;

    private final SocksService socksService;

    private final FileService filesService;

    public FilesController(OperationHistoryService operationHistoryService, SocksService socksService, FileService filesService) {
        this.operationHistoryService = operationHistoryService;
        this.socksService = socksService;
        this.filesService = filesService;
    }

    @GetMapping("/socksExport")
    @Operation(
            summary = "Экспорт файла с носками",
            description = "Можно экспортровать всю информацию о носках в файл формата json"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Файл скачен"
            )
    })
    private ResponseEntity<InputStreamResource> downloadSocksFile() throws FileNotFoundException {
        File socksFile = filesService.getDataFile(socksFileName);
        if (socksFile.exists()) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(socksFile));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(socksFile.length())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Socks.json\"")
                    .body(resource);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value = "/socksImport", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Импорт файла с носками",
            description = "Можно импортировать файл с носками в формате json"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Файл импортирован"
            )
    })
    public ResponseEntity<Void> uploadSocksFile(@RequestParam MultipartFile file) {
        try {
            Map<Socks, Integer> socksMap = socksService.importSocksFile(file);
            for (Map.Entry<Socks, Integer> socks1 : socksMap.entrySet()) {
                operationHistoryService.addOperation(socks1.getKey(), TypeOfOperation.ACCEPTANCE, socks1.getValue());
            }
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/operationHistoryExport")
    @Operation(
            summary = "Экспорт файла с историей операций",
            description = "Можно экспортровать всю историю операций в файл формата json"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Файл скачен"
            )
    })
    private ResponseEntity<InputStreamResource> downloadOperationHistoryFile() throws FileNotFoundException {
        File operationHistoryFile = filesService.getDataFile(operationHistoryFileName);
        if (operationHistoryFile.exists()) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(operationHistoryFile));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(operationHistoryFile.length())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"OperationHistory.json\"")
                    .body(resource);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value = "/operationHistoryImport", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Импорт файла с историей операций",
            description = "Можно импортировать файл с историей операций в формате json"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Файл импортирован"
            )
    })
    public ResponseEntity<Void> uploadOperationHistoryFile(@RequestParam MultipartFile file) {
        try  {
            Map<Integer, OperationHistory> operationMap = operationHistoryService.importOperationFile(file);
            operationHistoryService.addSocksAfterImportHistory(operationMap);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
