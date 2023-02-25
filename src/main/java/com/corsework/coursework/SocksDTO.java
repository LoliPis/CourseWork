package com.corsework.coursework;

import com.corsework.coursework.model.Enums.Color;
import com.corsework.coursework.model.Enums.Size;
import com.corsework.coursework.model.Socks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SocksDTO {
    private String color;
    private Integer size;
    private Integer cottonPart;
    private Integer quantity;

    public static Socks convertDtoToSocks(SocksDTO socksDTO){
        Color color = null;
        Size size = null;
        if (Color.findColorByTitle(socksDTO.getColor()) != null) {
            color = Color.findColorByTitle(socksDTO.getColor());
        }
        if (Size.findSizeByTitle(socksDTO.getSize()) != null) {
            size = Size.findSizeByTitle(socksDTO.getSize());
        }
        int cottonPart = 0;
        if (socksDTO.getCottonPart() > 0){
            cottonPart = socksDTO.getCottonPart();
        }
        return new Socks(color, size, cottonPart);
    }

    public static SocksDTO convertSocksToDTO(Socks socks, Integer quantity){
        return new SocksDTO(socks.getColor().getTitle(),
                Size.getTitleBySize(socks.getSize()),
                socks.getCottonPart(),
                quantity);
    }

    public static List<SocksDTO> convertSocksToDTOList(Map<Socks, Integer> socksMap){
        List<SocksDTO> socksDTOList = new LinkedList<>();
        for (Map.Entry<Socks, Integer> socks : socksMap.entrySet()) {
            socksDTOList.add(SocksDTO.convertSocksToDTO(socks.getKey(), socks.getValue()));
        }
        return socksDTOList;
    }
}
