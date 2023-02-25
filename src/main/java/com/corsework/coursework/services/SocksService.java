package com.corsework.coursework.services;


import com.corsework.coursework.SocksDTO;
import com.corsework.coursework.model.Socks;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface SocksService {


    Socks addSocks(SocksDTO socksDTO);

    boolean editSocks(SocksDTO socksDTO);

    int getSocks(String color, int size, int cottonMin, int cottonMax);

    boolean deleteSocks(SocksDTO socksDTO);

    Map<Socks, Integer> importSocksFile(MultipartFile file) throws IOException;
}
