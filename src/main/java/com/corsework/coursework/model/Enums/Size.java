package com.corsework.coursework.model.Enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Size {
    XS("28-32"),
    S("33-37"),
    M("38-40"),
    L("41-43"),
    XL("44-46"),
    XXL("47-50");

    private final String title;

    public static Size findSizeByTitle(int sizeTitle){
        for (Size sizes : values()) {
            String[] sizeArr = sizes.getTitle().split("-");
            int firstSize = Integer.parseInt(sizeArr[0]);
            int secondSize = Integer.parseInt(sizeArr[1]);
            if (sizeTitle >= firstSize && sizeTitle <= secondSize){
                return sizes;
            }
        }
        return null;
    }

    public static Integer getTitleBySize(Size size){
        String[] sizeArr = size.getTitle().split("-");
        int secondSize = Integer.parseInt(sizeArr[1]);
        if (secondSize != 0) {
            return secondSize;
        }
        return null;
    }
}
