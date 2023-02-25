package com.corsework.coursework;


import com.corsework.coursework.model.Enums.TypeOfOperation;
import com.corsework.coursework.model.OperationHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationDTO {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private Integer id;
    private TypeOfOperation operationType;
    private String dateTime;
    private SocksDTO socks;

    public static List<OperationDTO> convertOperationHistoryToDTOList(Map<Integer, OperationHistory> operationHistory) {
        List<OperationDTO> operationHistoryDTOList = new LinkedList<>();
        for (Map.Entry<Integer, OperationHistory> operations : operationHistory.entrySet()) {
            operationHistoryDTOList.add(new OperationDTO(operations.getKey(),
                    operations.getValue().getTypeOfOperation(),
                    operations.getValue().getDateTime().format(OperationDTO.formatter),
                    SocksDTO.convertSocksToDTO(operations.getValue().getSocks(), operations.getValue().getQuantity())));
        }
        return operationHistoryDTOList;
    }
}
