package br.edu.avaliacao.sacm.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import br.edu.avaliacao.sacm.enums.StatusPessoa;
import br.edu.avaliacao.sacm.model.Paciente;

@Repository
public class PacienteRepository {

    private final JdbcTemplate jdbc;

    private static final String SELECT =
        "SELECT p.*, a.numero_prontuario, a.convenio, "
        + "a.contato_emergencia, a.telefone_emergencia "
        + "FROM pessoas p JOIN pacientes a "
        + "ON p.id = a.pessoa_id";

    public PacienteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional
    public Paciente salvar(Paciente paciente) {

        String sqlPessoa =
            "INSERT INTO pessoas (tipo, nome, cpf, data_nascimento, "
            + "email, telefone, cidade, estado, data_cadastro, status) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder chave = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                sqlPessoa, Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, "PACIENTE");
            ps.setString(2, paciente.getNome());
            ps.setString(3, paciente.getCpf());
            ps.setObject(4, paciente.getDataNascimento());
            ps.setString(5, paciente.getEmail());
            ps.setString(6, paciente.getTelefone());
            ps.setString(7, paciente.getCidade());
            ps.setString(8, paciente.getEstado());
            ps.setObject(9, paciente.getDataCadastro());
            ps.setString(10, paciente.getStatus().name());

            return ps;
        }, chave);

        long id = chave.getKey().longValue();

        jdbc.update(
            "INSERT INTO pacientes "
            + "(pessoa_id, numero_prontuario, convenio, "
            + "contato_emergencia, telefone_emergencia) "
            + "VALUES (?, ?, ?, ?, ?)",
            id,
            paciente.getNumeroProntuario(),
            paciente.getConvenio(),
            paciente.getContatoEmergencia(),
            paciente.getTelefoneEmergencia()
        );

        return buscarPorId(id).orElseThrow();
    }

    public Optional<Paciente> buscarPorId(long id) {

        return jdbc.query(
            SELECT + " WHERE p.id = ?",
            (rs, n) -> new Paciente(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getString("cpf"),
                rs.getDate("data_nascimento").toLocalDate(),
                rs.getString("email"),
                rs.getString("telefone"),
                rs.getString("cidade"),
                rs.getString("estado"),
                rs.getTimestamp("data_cadastro").toLocalDateTime(),
                StatusPessoa.valueOf(rs.getString("status")),
                rs.getString("numero_prontuario"),
                rs.getString("convenio"),
                rs.getString("contato_emergencia"),
                rs.getString("telefone_emergencia")
            ),
            id
        ).stream().findFirst();
    }

    public List<Paciente> listarTodos() {

        return jdbc.query(
            SELECT + " ORDER BY p.nome",
            (rs, n) -> new Paciente(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getString("cpf"),
                rs.getDate("data_nascimento").toLocalDate(),
                rs.getString("email"),
                rs.getString("telefone"),
                rs.getString("cidade"),
                rs.getString("estado"),
                rs.getTimestamp("data_cadastro").toLocalDateTime(),
                StatusPessoa.valueOf(rs.getString("status")),
                rs.getString("numero_prontuario"),
                rs.getString("convenio"),
                rs.getString("contato_emergencia"),
                rs.getString("telefone_emergencia")
            )
        );
    }

    public void alterarStatus(long id, StatusPessoa status) {

        if (jdbc.update(
            "UPDATE pessoas p JOIN pacientes a "
            + "ON p.id = a.pessoa_id "
            + "SET p.status = ? WHERE p.id = ?",
            status.name(), id
        ) == 0) {
            throw new br.edu.avaliacao.sacm.exception
                .RegraNegocioException("Paciente nao encontrado.");
        }
    }
}