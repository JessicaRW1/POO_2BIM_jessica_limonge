package br.edu.avaliacao.sacm.interfaces;

import java.time.LocalDateTime;
import br.edu.avaliacao.sacm.model.Consulta;

public interface Agendavel {

    Consulta agendarConsulta(
        long medicoId,
        long pacienteId,
        LocalDateTime dataHora,
        String motivo
    );

    void cancelarConsulta(long consultaId);
}