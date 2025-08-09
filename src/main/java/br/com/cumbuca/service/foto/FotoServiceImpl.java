package br.com.cumbuca.service.foto;

import br.com.cumbuca.exception.CumbucaException;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Foto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class FotoServiceImpl implements FotoService {

    public List<Foto> criarFotos(List<MultipartFile> arquivos, Avaliacao avaliacao) {
        return arquivos.stream()
                .filter(f -> !f.isEmpty())
                .map(f -> {
                    try {
                        final Foto foto = new Foto();
                        foto.setFoto(f.getBytes());
                        foto.setAvaliacao(avaliacao);
                        return foto;
                    } catch (IOException e) {
                        throw new CumbucaException("Erro ao processar arquivo de foto: " + f.getOriginalFilename());
                    }
                }).toList();
    }
}
