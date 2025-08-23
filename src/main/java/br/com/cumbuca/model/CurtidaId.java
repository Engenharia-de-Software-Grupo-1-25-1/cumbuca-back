package br.com.cumbuca.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurtidaId implements Serializable {
    private Long usuario;
    private Long avaliacao;
}
