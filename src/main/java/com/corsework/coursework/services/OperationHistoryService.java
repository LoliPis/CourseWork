package com.corsework.coursework.services;

import com.corsework.coursework.model.Enums.TypeOfOperation;
import com.corsework.coursework.model.OperationHistory;
import com.corsework.coursework.model.Socks;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface OperationHistoryService {
    void addOperation(Socks socks, TypeOfOperation typeOfOperation, int quantity);

    void addSocksAfterImportHistory(Map<Integer, OperationHistory> operationHistory);

    Map<Integer, OperationHistory> importOperationFile(MultipartFile file) throws IOException;
}
