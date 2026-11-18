package br.edu.avaliacao.sacm.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import br.edu.avaliacao.sacm.enums.*;
import br.edu.avaliacao.sacm.exception.RegraNegocioException;

public class Medico extends Pessoa {

    private final String crm;
    private final Especialidade especialidade;
    private DisponibilidadeMedico disponibilidade;
    private final BigDecimal valorConsulta;

    public Medico(
        Long id, String nome, String cpf,
        LocalDate nascimento, String email,
        String telefone, String cidade, String estado,
        LocalDateTime cadastro, StatusPessoa status,
        String crm, Especialidade especialidade,
        DisponibilidadeMedico disponibilidade,
        BigDecimal valorConsulta
    ) {
        super(id, nome, cpf, nascimento, email,
            telefone, cidade, estado, cadastro, status);

        if (crm == null || crm.isBlank()) {
            throw new RegraNegocioException("O CRM e obrigatorio.");
        }

        if (especialidade == null || disponibilidade == null) {
            throw new RegraNegocioException(
                "Especialidade e disponibilidade sao obrigatorias."
            );
        }

        if (valorConsulta == null || valorConsulta.signum() < 0) {
            throw new RegraNegocioException(
                "O valor da consulta nao pode ser negativo."
            );
        }

        this.crm = crm.trim();
        this.especialidade = especialidade;
        this.disponibilidade = disponibilidade;
        this.valorConsulta = valorConsulta;
    }

    public void alterarDisponibilidade(DisponibilidadeMedico nova) {
        if (nova == null) {
            throw new RegraNegocioException(
                "Informe a disponibilidade."
            );
        }
        this.disponibilidade = nova;
    }

    public boolean isDisponivel() {
        return disponibilidade == DisponibilidadeMedico.DISPONIVEL;
    }

    public boolean podeAtender() {
        return isAtivo() && isDisponivel();
    }

    @Override
    public String getDescricao() {
        return "Medico #" + getId()
            + " | " + getNome()
            + " | CRM: " + crm
            + " | " + especialidade
            + " | " + getStatus()
            + " | " + disponibilidade;
    }

    public String getCrm() { return crm; }
    public Especialidade getEspecialidade() { return especialidade; }
    public DisponibilidadeMedico getDisponibilidade() {
        return disponibilidade;
    }
    public BigDecimal getValorConsulta() { return valorConsulta; }
}