package com.corsework.coursework.model;

import com.corsework.coursework.model.Enums.Color;
import com.corsework.coursework.model.Enums.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Socks {
    private Color color;
    private Size size;
    private int cottonPart;

}
