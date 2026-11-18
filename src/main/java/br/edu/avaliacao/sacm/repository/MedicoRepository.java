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
import br.edu.avaliacao.sacm.enums.*;
import br.edu.avaliacao.sacm.model.Medico;

@Repository
public class MedicoRepository {

    private final JdbcTemplate jdbc;

    private static final String SELECT =
        "SELECT p.*, m.crm, m.especialidade, m.disponibilidade, "
        + "m.valor_consulta FROM pessoas p "
        + "JOIN medicos m ON p.id = m.pessoa_id";

    public MedicoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional
    public Medico salvar(Medico medico) {

        String sqlPessoa =
            "INSERT INTO pessoas (tipo, nome, cpf, data_nascimento, "
            + "email, telefone, cidade, estado, data_cadastro, status) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder chave = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                sqlPessoa, Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, "MEDICO");
            ps.setString(2, medico.getNome());
            ps.setString(3, medico.getCpf());
            ps.setObject(4, medico.getDataNascimento());
            ps.setString(5, medico.getEmail());
            ps.setString(6, medico.getTelefone());
            ps.setString(7, medico.getCidade());
            ps.setString(8, medico.getEstado());
            ps.setObject(9, medico.getDataCadastro());
            ps.setString(10, medico.getStatus().name());

            return ps;
        }, chave);

        long id = chave.getKey().longValue();

        jdbc.update(
            "INSERT INTO medicos "
            + "(pessoa_id, crm, especialidade, "
            + "disponibilidade, valor_consulta) "
            + "VALUES (?, ?, ?, ?, ?)",
            id,
            medico.getCrm(),
            medico.getEspecialidade().name(),
            medico.getDisponibilidade().name(),
            medico.getValorConsulta()
        );

        return buscarPorId(id).orElseThrow();
    }

    public Optional<Medico> buscarPorId(long id) {

        return jdbc.query(
            SELECT + " WHERE p.id = ?",
            (rs, n) -> new Medico(
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
                rs.getString("crm"),
                Especialidade.valueOf(rs.getString("especialidade")),
                DisponibilidadeMedico.valueOf(
                    rs.getString("disponibilidade")
                ),
                rs.getBigDecimal("valor_consulta")
            ),
            id
        ).stream().findFirst();
    }

    public List<Medico> listarTodos() {

        return jdbc.query(
            SELECT + " ORDER BY p.nome",
            (rs, n) -> new Medico(
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
                rs.getString("crm"),
                Especialidade.valueOf(rs.getString("especialidade")),
                DisponibilidadeMedico.valueOf(
                    rs.getString("disponibilidade")
                ),
                rs.getBigDecimal("valor_consulta")
            )
        );
    }

    public void alterarDisponibilidade(
        long id, DisponibilidadeMedico disponibilidade
    ) {
        if (jdbc.update(
            "UPDATE medicos SET disponibilidade = ? "
            + "WHERE pessoa_id = ?",
            disponibilidade.name(), id
        ) == 0) {
            throw new br.edu.avaliacao.sacm.exception
                .RegraNegocioException("Medico nao encontrado.");
        }
    }

    public void alterarStatus(long id, StatusPessoa status) {

        if (jdbc.update(
            "UPDATE pessoas p JOIN medicos m "
            + "ON p.id = m.pessoa_id "
            + "SET p.status = ? WHERE p.id = ?",
            status.name(), id
        ) == 0) {
            throw new br.edu.avaliacao.sacm.exception
                .RegraNegocioException("Medico nao encontrado.");
        }
    }
}