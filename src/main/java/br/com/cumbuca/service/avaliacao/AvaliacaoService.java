package br.com.cumbuca.service.avaliacao;

import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.dto.avaliacao.AvaliacaoResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public interface AvaliacaoService {
    AvaliacaoResponseDTO criar(AvaliacaoRequestDTO avaliacaoRequestDTO);

    AvaliacaoResponseDTO atualizar(Long id, AvaliacaoRequestDTO dto);

    void remover(Long id);

    AvaliacaoResponseDTO recuperar(Long id);

    List<AvaliacaoResponseDTO> listar(Long idUsuario, Long idEstabelecimento, String usuario, String estabelecimento,
                                      String itemConsumido, List<String> tags, BigDecimal preco, int notaGeral,
                                      int notaComida, int notaAmbiente, int notaAtendimento);

}
