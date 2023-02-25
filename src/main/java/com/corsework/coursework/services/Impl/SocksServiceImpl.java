package com.corsework.coursework.services.Impl;

import com.corsework.coursework.SocksDTO;
import com.corsework.coursework.model.Enums.Color;
import com.corsework.coursework.model.Enums.Size;
import com.corsework.coursework.model.Socks;
import com.corsework.coursework.services.FileService;
import com.corsework.coursework.services.SocksService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class SocksServiceImpl implements SocksService {

    @Value("${name.of.socks.data.file}")
    private String socksFileName;

    final private FileService fileService;
    private static Map<Socks, Integer> socksMap = new LinkedHashMap<>();

    public SocksServiceImpl(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public Socks addSocks(SocksDTO socksDTO) {
        Socks socks = SocksDTO.convertDtoToSocks(socksDTO);
        if(socks.getSize() != null && socks.getColor() != null && socks.getCottonPart() != 0) {
            if (socksMap.containsKey(socks)) {
                socksMap.put(socks, socksMap.get(socks) + socksDTO.getQuantity());
            } else {
                socksMap.put(socks, socksDTO.getQuantity());
            }
            saveToFile();
            return socks;
        } else {
            return null;
        }
    }


    @Override
    public boolean editSocks(SocksDTO socksDTO) {
        Socks socks = SocksDTO.convertDtoToSocks(socksDTO);
        if (socksMap.containsKey(socks) && socksMap.get(socks)>= socksDTO.getQuantity()) {
            socksMap.put(socks, socksMap.get(socks) - socksDTO.getQuantity());
            saveToFile();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getSocks(String color, int size, int cottonMin, int cottonMax) {
        int socksCount = 0;
        for (Map.Entry<Socks, Integer> socks : socksMap.entrySet()) {
            if (socks.getKey().getColor() == Color.findColorByTitle(color) &&
                    socks.getKey().getSize() == Size.findSizeByTitle(size) &&
                    (socks.getKey().getCottonPart() >= cottonMin &&
                            socks.getKey().getCottonPart() <= cottonMax)){
                socksCount += socks.getValue();
            }
        }
        return socksCount;
    }

    @Override
    public boolean deleteSocks(SocksDTO socksDTO) {
        Socks socks = SocksDTO.convertDtoToSocks(socksDTO);
        if (socksMap.containsKey(socks) && socksMap.get(socks)>= socksDTO.getQuantity()) {
            socksMap.put(socks, socksMap.get(socks) - socksDTO.getQuantity());
            saveToFile();
            return true;
        } else {
            return false;
        }
    }

    @PostConstruct
    private void init(){
        readFromFile();
    }

    private void saveToFile() {
        try {
            String json = new ObjectMapper()
                    .writeValueAsString(SocksDTO
                    .convertSocksToDTOList(socksMap));
            fileService.saveToFile(json, socksFileName);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void readFromFile() {
        String json = fileService.readFromFile(socksFileName);
        if (json == null) return;
        List<SocksDTO> dtoSocks;
        try {
            dtoSocks = new  ObjectMapper()
                    .readValue(json, new TypeReference<LinkedList<SocksDTO>>() {
                    });
            for (SocksDTO socksDTO : dtoSocks) {
                socksMap.put(SocksDTO.convertDtoToSocks(socksDTO), socksDTO.getQuantity());
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<Socks, Integer> importSocksFile(MultipartFile file) throws IOException {
        List<SocksDTO> dtoList  =  new ObjectMapper()
                .readValue(file.getInputStream(), new TypeReference<>(){});
        socksMap = new LinkedHashMap<>();
        for (SocksDTO socksDTO : dtoList) {
            addSocks(socksDTO);
        }
        return socksMap;
    }


}
