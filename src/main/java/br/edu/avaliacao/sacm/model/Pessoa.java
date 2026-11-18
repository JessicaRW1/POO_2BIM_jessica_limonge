package br.edu.avaliacao.sacm.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import br.edu.avaliacao.sacm.enums.StatusPessoa;
import br.edu.avaliacao.sacm.exception.RegraNegocioException;

public abstract class Pessoa {

    public static final int MINIMO_CARACTERES_NOME = 3;

    private final Long id;
    private final String nome;
    private final String cpf;
    private final LocalDate dataNascimento;
    private String email;
    private String telefone;
    private final String cidade;
    private final String estado;
    private final LocalDateTime dataCadastro;
    private StatusPessoa status;

    protected Pessoa(
        Long id, String nome, String cpf,
        LocalDate dataNascimento, String email,
        String telefone, String cidade, String estado,
        LocalDateTime dataCadastro, StatusPessoa status
    ) {
        if (nome == null ||
            nome.trim().length() < MINIMO_CARACTERES_NOME) {
            throw new RegraNegocioException(
                "O nome precisa ter ao menos 3 caracteres."
            );
        }

        String cpfLimpo = cpf == null
            ? "" : cpf.replaceAll("\\D", "");

        if (!cpfLimpo.matches("\\d{11}")) {
            throw new RegraNegocioException(
                "O CPF deve ter exatamente 11 digitos."
            );
        }

        if (dataNascimento == null ||
            !dataNascimento.isBefore(LocalDate.now())) {
            throw new RegraNegocioException(
                "A data de nascimento deve ser anterior a hoje."
            );
        }

        if (cidade == null || cidade.isBlank() ||
            estado == null ||
            !estado.trim().matches("[A-Za-z]{2}")) {
            throw new RegraNegocioException(
                "Informe cidade e UF com duas letras."
            );
        }

        this.id = id;
        this.nome = nome.trim();
        this.cpf = cpfLimpo;
        this.dataNascimento = dataNascimento;
        this.cidade = cidade.trim();
        this.estado = estado.trim().toUpperCase();
        this.dataCadastro = dataCadastro == null
            ? LocalDateTime.now() : dataCadastro;
        this.status = status == null
            ? StatusPessoa.ATIVO : status;

        atualizarContato(email, telefone);
    }

    public int getIdade() {
        return java.time.Period.between(
            dataNascimento, LocalDate.now()
        ).getYears();
    }

    public void atualizarContato(String email, String telefone) {
        if (email == null ||
            !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new RegraNegocioException(
                "Informe um e-mail valido."
            );
        }

        if (telefone == null || telefone.isBlank()) {
            throw new RegraNegocioException(
                "O telefone e obrigatorio."
            );
        }

        this.email = email.trim();
        this.telefone = telefone.trim();
    }

    public void ativar() {
        status = StatusPessoa.ATIVO;
    }

    public void desativar() {
        status = StatusPessoa.INATIVO;
    }

    public boolean isAtivo() {
        return status == StatusPessoa.ATIVO;
    }

    public abstract String getDescricao();

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public StatusPessoa getStatus() { return status; }
}