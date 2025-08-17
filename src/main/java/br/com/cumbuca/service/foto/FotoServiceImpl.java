package br.com.cumbuca.service.foto;

import br.com.cumbuca.exception.CumbucaException;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Foto;
import br.com.cumbuca.repository.FotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class FotoServiceImpl implements FotoService {

    private final FotoRepository fotoRepository;

    public FotoServiceImpl(FotoRepository fotoRepository) {
        this.fotoRepository = fotoRepository;
    }

    @Override
    public void criar(List<MultipartFile> fotos, Avaliacao avaliacao) {
        fotos.stream()
                .filter(f -> !f.isEmpty())
                .forEach(f -> {
                    try {
                       final Foto foto = new Foto();
                        foto.setFoto(f.getBytes());
                        foto.setAvaliacao(avaliacao);
                        fotoRepository.save(foto);
                    } catch (IOException e) {
                        throw new CumbucaException("Erro ao processar arquivo de foto: " + f.getOriginalFilename());
                    }
                });
    }

    @Override
    public List<String> recuperar(Long avaliacaoId) {
        final List<Foto> fotos = fotoRepository.findByAvaliacaoId(avaliacaoId);
        return fotos.stream()
                .map(foto -> Base64.getEncoder().encodeToString(foto.getFoto()))
                .toList();
    }

}
