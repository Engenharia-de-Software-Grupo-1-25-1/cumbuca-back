package br.com.cumbuca.service.foto;

import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Foto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FotoService {

    List<Foto> criarFotos(List<MultipartFile> arquivos, Avaliacao avaliacao);

}


