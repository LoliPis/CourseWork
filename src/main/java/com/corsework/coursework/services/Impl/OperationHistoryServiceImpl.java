package com.corsework.coursework.services.Impl;

import com.corsework.coursework.OperationDTO;
import com.corsework.coursework.SocksDTO;
import com.corsework.coursework.model.Enums.TypeOfOperation;
import com.corsework.coursework.model.OperationHistory;
import com.corsework.coursework.model.Socks;
import com.corsework.coursework.services.FileService;
import com.corsework.coursework.services.OperationHistoryService;
import com.corsework.coursework.services.SocksService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.corsework.coursework.OperationDTO.formatter;

@Service
public class OperationHistoryServiceImpl implements OperationHistoryService {

    @Value("operationHistory.json")
    private String operationFileName;

    final private FileService fileService;
    private final SocksService socksService;
    private int id = 0;
    private Map<Integer, OperationHistory> operationHistory = new LinkedHashMap<>();

    public OperationHistoryServiceImpl(FileService fileService, SocksService socksService) {
        this.fileService = fileService;
        this.socksService = socksService;
    }

    @Override
    public void addOperation(Socks socks, TypeOfOperation typeOfOperation, int quantity){
        OperationHistory operationHistory1 = new OperationHistory(typeOfOperation,
                LocalDateTime.now(), socks , quantity);
        operationHistory.put(id,  operationHistory1);
        saveToFile();
        id++;
    }

    @PostConstruct
    private void init(){
        readFromFile();
    }

    private void saveToFile() {
        try {
            String json = new ObjectMapper()
                    .writeValueAsString(OperationDTO
                    .convertOperationHistoryToDTOList(operationHistory));
            fileService.saveToFile(json, operationFileName);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addSocksAfterImportHistory(Map<Integer, OperationHistory> operationHistory){
        for (Map.Entry<Integer, OperationHistory> operationM : operationHistory.entrySet()) {
            SocksDTO sockDTO = SocksDTO.convertSocksToDTO(operationM.getValue().getSocks(),
                    operationM.getValue().getQuantity());
            switch (operationM.getValue().getTypeOfOperation()){
                case ACCEPTANCE:
                    socksService.addSocks(sockDTO);
                case SHIPMENT:
                    socksService.editSocks(sockDTO);
                case WRITTEN_OFF:
                    socksService.deleteSocks(sockDTO);
            }
        }
    }
    private void readFromFile() {
        String json = fileService.readFromFile(operationFileName);
        if (json == null) return;
        List<OperationDTO> dtoOperation;
        try {
            dtoOperation = new  ObjectMapper()
                    .readValue(json, new TypeReference<LinkedList<OperationDTO>>() {
                    });
            for (OperationDTO operationDTO : dtoOperation) {
                OperationHistory operation = new OperationHistory(operationDTO.getOperationType(),
                        LocalDateTime.parse(operationDTO.getDateTime(), formatter),
                        SocksDTO.convertDtoToSocks(operationDTO.getSocks()), operationDTO.getSocks().getQuantity());
                operationHistory.put(operationDTO.getId(), operation);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<Integer, OperationHistory> importOperationFile(MultipartFile file) throws IOException {
        List<OperationDTO> dtoOperation = new ObjectMapper()
                .readValue(file.getInputStream(), new TypeReference<>() {});
        operationHistory = new LinkedHashMap<>();
        for (OperationDTO  operationDTO : dtoOperation) {
            addOperation( SocksDTO.convertDtoToSocks(operationDTO.getSocks()),
                    operationDTO.getOperationType(),
                    operationDTO.getSocks().getQuantity());
        }
        return operationHistory;
    }
}
