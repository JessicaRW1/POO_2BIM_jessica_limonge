package br.edu.avaliacao.sacm.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.avaliacao.sacm.exception.ConflitoHorarioException;
import br.edu.avaliacao.sacm.exception.RegraNegocioException;
import br.edu.avaliacao.sacm.interfaces.Agendavel;
import br.edu.avaliacao.sacm.model.*;
import br.edu.avaliacao.sacm.repository.*;

@Service
public class ConsultaService implements Agendavel {

    private final MedicoRepository medicos;
    private final PacienteRepository pacientes;
    private final ConsultaRepository consultas;

    public ConsultaService(
        MedicoRepository medicos,
        PacienteRepository pacientes,
        ConsultaRepository consultas
    ) {
        this.medicos = medicos;
        this.pacientes = pacientes;
        this.consultas = consultas;
    }

    @Override
    @Transactional
    public Consulta agendarConsulta(
        long medicoId,
        long pacienteId,
        LocalDateTime dataHora,
        String motivo
    ) {

        if (dataHora == null ||
            !dataHora.isAfter(LocalDateTime.now())) {
            throw new RegraNegocioException(
                "A data e hora da consulta devem estar no futuro."
            );
        }

        if (motivo == null || motivo.isBlank()) {
            throw new RegraNegocioException(
                "O motivo da consulta e obrigatorio."
            );
        }

        consultas.bloquearParticipantes(medicoId, pacienteId);

        Medico medico = medicos.buscarPorId(medicoId)
            .orElseThrow(() -> new RegraNegocioException(
                "Medico nao encontrado."
            ));

        Paciente paciente = pacientes.buscarPorId(pacienteId)
            .orElseThrow(() -> new RegraNegocioException(
                "Paciente nao encontrado."
            ));

        if (!medico.isAtivo() || !paciente.isAtivo()) {
            throw new RegraNegocioException(
                "Medico e paciente precisam estar ATIVOS."
            );
        }

        if (!medico.isDisponivel()) {
            throw new RegraNegocioException(
                "O medico esta INDISPONIVEL."
            );
        }

        verificarConflitoHorario(
            medicoId, pacienteId, dataHora
        );

        Consulta consulta = new Consulta(
            null,
            medico,
            paciente,
            dataHora,
            motivo,
            null,
            null,
            null
        );

        return consultas.salvar(consulta);
    }

    public void verificarConflitoHorario(
        long medicoId,
        long pacienteId,
        LocalDateTime dataHora
    ) {
        if (consultas.existeConflitoHorario(
            medicoId, pacienteId, dataHora
        )) {
            throw new ConflitoHorarioException(
                "Conflito de horario: o medico ou o paciente "
                + "ja possui consulta ativa nessa data/hora."
            );
        }
    }

    @Transactional
    public void confirmarConsulta(long id) {

        Consulta consulta = consultas.buscarPorId(id)
            .orElseThrow(() -> new RegraNegocioException(
                "Consulta nao encontrada."
            ));

        consulta.confirmar();
        consultas.alterarStatus(id, consulta.getStatus());
    }

    @Override
    @Transactional
    public void cancelarConsulta(long id) {

        Consulta consulta = consultas.buscarPorId(id)
            .orElseThrow(() -> new RegraNegocioException(
                "Consulta nao encontrada."
            ));

        consulta.cancelar();
        consultas.alterarStatus(id, consulta.getStatus());
    }

    @Transactional
    public void finalizarConsulta(long id) {

        Consulta consulta = consultas.buscarPorId(id)
            .orElseThrow(() -> new RegraNegocioException(
                "Consulta nao encontrada."
            ));

        consulta.finalizar();
        consultas.alterarStatus(id, consulta.getStatus());
    }

    public List<Consulta> listarConsultas() {
        return consultas.listarTodos();
    }

    public List<Consulta> listarConsultasPorMedico(long id) {
        return consultas.listarPorMedico(id);
    }

    public List<Consulta> listarConsultasPorPaciente(long id) {
        return consultas.listarPorPaciente(id);
    }
}