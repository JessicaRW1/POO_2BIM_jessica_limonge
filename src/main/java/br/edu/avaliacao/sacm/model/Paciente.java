package br.edu.avaliacao.sacm.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import br.edu.avaliacao.sacm.enums.StatusPessoa;
import br.edu.avaliacao.sacm.exception.RegraNegocioException;

public class Paciente extends Pessoa {

    private final String numeroProntuario;
    private String convenio;
    private String contatoEmergencia;
    private String telefoneEmergencia;

    public Paciente(
        Long id, String nome, String cpf,
        LocalDate nascimento, String email,
        String telefone, String cidade, String estado,
        LocalDateTime cadastro, StatusPessoa status,
        String numeroProntuario, String convenio,
        String contatoEmergencia, String telefoneEmergencia
    ) {
        super(id, nome, cpf, nascimento, email,
            telefone, cidade, estado, cadastro, status);

        if (numeroProntuario == null || numeroProntuario.isBlank()) {
            throw new RegraNegocioException(
                "O numero do prontuario e obrigatorio."
            );
        }

        this.numeroProntuario = numeroProntuario.trim();

        atualizarConvenio(convenio);
        atualizarContatoEmergencia(
            contatoEmergencia, telefoneEmergencia
        );
    }

    public void atualizarConvenio(String convenio) {
        this.convenio = convenio == null || convenio.isBlank()
            ? "PARTICULAR" : convenio.trim();
    }

    public void atualizarContatoEmergencia(
        String nome, String telefone
    ) {
        if (nome == null || nome.isBlank() ||
            telefone == null || telefone.isBlank()) {
            throw new RegraNegocioException(
                "Contato e telefone de emergencia sao obrigatorios."
            );
        }

        this.contatoEmergencia = nome.trim();
        this.telefoneEmergencia = telefone.trim();
    }

    public boolean podeAgendar() {
        return isAtivo();
    }

    @Override
    public String getDescricao() {
        return "Paciente #" + getId()
            + " | " + getNome()
            + " | Prontuario: " + numeroProntuario
            + " | " + getStatus();
    }

    public String getNumeroProntuario() {
        return numeroProntuario;
    }

    public String getConvenio() { return convenio; }

    public String getContatoEmergencia() {
        return contatoEmergencia;
    }

    public String getTelefoneEmergencia() {
        return telefoneEmergencia;
    }
}