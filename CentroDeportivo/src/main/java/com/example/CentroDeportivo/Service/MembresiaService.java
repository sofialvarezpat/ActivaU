package com.example.CentroDeportivo.Service;


import com.example.CentroDeportivo.DTO.request.MembresiaRequest;
import com.example.CentroDeportivo.DTO.response.MembresiaResponse;

import java.util.List;

public interface MembresiaService {

    MembresiaResponse comprar(MembresiaRequest request);

    List<MembresiaResponse> mias();
}
