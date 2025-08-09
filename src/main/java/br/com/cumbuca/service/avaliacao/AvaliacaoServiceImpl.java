package br.com.cumbuca.service.avaliacao;

import br.com.cumbuca.dto.avaliacao.AvaliacaoAtualizacaoRequestDTO;
import br.com.cumbuca.exception.CumbucaException;
import br.com.cumbuca.model.Avaliacao;
import br.com.cumbuca.model.Usuario;
import br.com.cumbuca.repository.AvaliacaoRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class AvaliacaoServiceImpl implements AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;

    public AvaliacaoServiceImpl(AvaliacaoRepository avaliacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
    }

    @Override
    public Avaliacao atualizar(Long id, AvaliacaoAtualizacaoRequestDTO dto) {
        final Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Avaliação não encontrada."));

        final Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!avaliacao.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new CumbucaException("Você não tem permissão para editar esta avaliação.");
        }

        avaliacao.setConsumo(dto.getConsumo());
        avaliacao.setDescricao(dto.getDescricao());
        avaliacao.setPreco(dto.getPreco());
        avaliacao.setNotaGeral(dto.getNotaGeral());
        avaliacao.setNotaComida(dto.getNotaComida());
        avaliacao.setNotaAtendimento(dto.getNotaAtendimento());
        avaliacao.setNotaAmbiente(dto.getNotaAmbiente());

        return avaliacaoRepository.save(avaliacao);
    }

    @Override
    public void remover(Long id) {
        final Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Avaliação não encontrada."));

        final Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!avaliacao.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new CumbucaException("Você não tem permissão para remover esta avaliação.");
        }

        avaliacaoRepository.delete(avaliacao);
    }
}