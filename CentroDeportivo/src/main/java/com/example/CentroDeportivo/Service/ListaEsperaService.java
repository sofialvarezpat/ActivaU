package com.example.CentroDeportivo.Service;


import com.example.CentroDeportivo.DTO.response.ListaEsperaResponse;
import java.util.List;

public interface ListaEsperaService {

    ListaEsperaResponse unirse(Long actividadId);

    List<ListaEsperaResponse> mias();
}
