package com.corsework.coursework.model.Enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Color {
    BLACK("Черный"),
    WHITE("Белый"),
    PINK("Розовый"),
    BLUE("Голубой"),
    GREEN("Зеленый"),
    BROWN("Коричневый"),
    YELLOW("Желтый");

    private final String title;

    public static Color findColorByTitle(String title){
        for (Color colors : values()) {
            if (colors.getTitle().equals(title)){
                return colors;
            }
        }
        return null;
    }

}
