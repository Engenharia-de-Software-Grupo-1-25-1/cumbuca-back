package br.com.cumbuca.dto.tag;

import br.com.cumbuca.model.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TagResponseDTO {
    private Long id;
    private String tag;
    private Integer quantidade;

    public TagResponseDTO(Tag tag) {
        this.id = tag.getId();
        this.tag = tag.getTag();
        this.quantidade = 0;
    }
}
