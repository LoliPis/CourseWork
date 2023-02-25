package com.corsework.coursework.model;

import com.corsework.coursework.model.Enums.TypeOfOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationHistory {
    private TypeOfOperation typeOfOperation;
    private LocalDateTime dateTime;
    private Socks socks;
    private Integer quantity;
}
