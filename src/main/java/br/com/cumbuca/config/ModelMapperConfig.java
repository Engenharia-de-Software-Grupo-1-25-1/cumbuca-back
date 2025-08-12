package br.com.cumbuca.config;

import br.com.cumbuca.dto.avaliacao.AvaliacaoRequestDTO;
import br.com.cumbuca.model.Avaliacao;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mm = new ModelMapper();
        mm.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT).setSkipNullEnabled(true);

        mm.addConverter(ctx -> {
            AvaliacaoRequestDTO s = ctx.getSource();
            Avaliacao d = new Avaliacao();
            d.setItemConsumido(s.getItemConsumido());
            d.setPreco(s.getPreco());
            d.setDescricao(s.getDescricao());
            d.setNotaGeral(s.getNotaGeral());
            d.setNotaAmbiente(s.getNotaAmbiente());
            d.setNotaComida(s.getNotaComida());
            d.setNotaAtendimento(s.getNotaAtendimento());
            return d;
        }, AvaliacaoRequestDTO.class, Avaliacao.class);

        return mm;
    }
}
