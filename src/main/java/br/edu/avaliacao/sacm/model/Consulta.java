package br.edu.avaliacao.sacm.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import br.edu.avaliacao.sacm.enums.StatusConsulta;
import br.edu.avaliacao.sacm.exception.RegraNegocioException;

public class Consulta {

    private final Long id;
    private final Medico medico;
    private final Paciente paciente;
    private final LocalDateTime dataHora;
    private final String motivo;
    private StatusConsulta status;
    private final LocalDateTime dataAgendamento;
    private String observacao;

    public Consulta(
        Long id, Medico medico, Paciente paciente,
        LocalDateTime dataHora, String motivo,
        StatusConsulta status,
        LocalDateTime dataAgendamento,
        String observacao
    ) {
        if (medico == null || paciente == null || dataHora == null) {
            throw new RegraNegocioException(
                "Medico, paciente e data sao obrigatorios."
            );
        }

        if (motivo == null || motivo.isBlank()) {
            throw new RegraNegocioException(
                "O motivo e obrigatorio."
            );
        }

        this.id = id;
        this.medico = medico;
        this.paciente = paciente;
        this.dataHora = dataHora;
        this.motivo = motivo.trim();
        this.status = status == null
            ? StatusConsulta.AGENDADA : status;
        this.dataAgendamento = dataAgendamento == null
            ? LocalDateTime.now() : dataAgendamento;
        this.observacao = observacao;
    }

    public Consulta comId(long novoId) {
        return new Consulta(
            novoId, medico, paciente, dataHora,
            motivo, status, dataAgendamento, observacao
        );
    }

    public void confirmar() {
        if (status != StatusConsulta.AGENDADA) {
            throw new RegraNegocioException(
                "Somente consulta AGENDADA pode ser confirmada."
            );
        }
        status = StatusConsulta.CONFIRMADA;
    }

    public void cancelar() {
        if (!isAtiva()) {
            throw new RegraNegocioException(
                "Esta consulta nao pode mais ser cancelada."
            );
        }
        status = StatusConsulta.CANCELADA;
    }

    public void finalizar() {
        if (status != StatusConsulta.CONFIRMADA) {
            throw new RegraNegocioException(
                "Somente consulta CONFIRMADA pode ser finalizada."
            );
        }
        status = StatusConsulta.FINALIZADA;
    }

    public boolean isAtiva() {
        return status == StatusConsulta.AGENDADA ||
               status == StatusConsulta.CONFIRMADA;
    }

    public String getResumo() {
        return "Consulta #" + id
            + " | " + dataHora.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            )
            + " | Medico: " + medico.getNome()
            + " | Paciente: " + paciente.getNome()
            + " | " + status
            + " | Motivo: " + motivo;
    }

    public Long getId() { return id; }
    public Medico getMedico() { return medico; }
    public Paciente getPaciente() { return paciente; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getMotivo() { return motivo; }
    public StatusConsulta getStatus() { return status; }
    public LocalDateTime getDataAgendamento() {
        return dataAgendamento;
    }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}