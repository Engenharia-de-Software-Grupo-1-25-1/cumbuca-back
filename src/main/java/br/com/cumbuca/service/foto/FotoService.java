package br.com.cumbuca.service.foto;

import br.com.cumbuca.model.Avaliacao;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FotoService {
    void criar(List<MultipartFile> fotos, Avaliacao avaliacao);
}


