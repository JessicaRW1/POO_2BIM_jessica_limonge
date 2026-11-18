package br.edu.avaliacao.sacm.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import br.edu.avaliacao.sacm.enums.StatusConsulta;
import br.edu.avaliacao.sacm.exception.RegraNegocioException;
import br.edu.avaliacao.sacm.model.Consulta;

@Repository
public class ConsultaRepository {

    private final JdbcTemplate jdbc;
    private final MedicoRepository medicos;
    private final PacienteRepository pacientes;

    private static final String SELECT =
        "SELECT * FROM consultas";

    public ConsultaRepository(
        JdbcTemplate jdbc,
        MedicoRepository medicos,
        PacienteRepository pacientes
    ) {
        this.jdbc = jdbc;
        this.medicos = medicos;
        this.pacientes = pacientes;
    }

    public Consulta salvar(Consulta consulta) {

        KeyHolder chave = new GeneratedKeyHolder();

        String sql =
            "INSERT INTO consultas "
            + "(medico_id, paciente_id, data_hora, motivo, "
            + "status, data_agendamento, observacao) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS
            );

            ps.setLong(1, consulta.getMedico().getId());
            ps.setLong(2, consulta.getPaciente().getId());
            ps.setObject(3, consulta.getDataHora());
            ps.setString(4, consulta.getMotivo());
            ps.setString(5, consulta.getStatus().name());
            ps.setObject(6, consulta.getDataAgendamento());
            ps.setString(7, consulta.getObservacao());

            return ps;
        }, chave);

        return consulta.comId(chave.getKey().longValue());
    }

    private Consulta montar(java.sql.ResultSet rs)
        throws java.sql.SQLException {

        long medicoId = rs.getLong("medico_id");
        long pacienteId = rs.getLong("paciente_id");

        return new Consulta(
            rs.getLong("id"),

            medicos.buscarPorId(medicoId).orElseThrow(
                () -> new RegraNegocioException(
                    "Medico nao encontrado."
                )
            ),

            pacientes.buscarPorId(pacienteId).orElseThrow(
                () -> new RegraNegocioException(
                    "Paciente nao encontrado."
                )
            ),

            rs.getTimestamp("data_hora").toLocalDateTime(),
            rs.getString("motivo"),
            StatusConsulta.valueOf(rs.getString("status")),
            rs.getTimestamp("data_agendamento").toLocalDateTime(),
            rs.getString("observacao")
        );
    }

    public Optional<Consulta> buscarPorId(long id) {

        return jdbc.query(
            SELECT + " WHERE id = ?",
            (rs, n) -> montar(rs),
            id
        ).stream().findFirst();
    }

    public List<Consulta> listarTodos() {

        return jdbc.query(
            SELECT + " ORDER BY data_hora, id",
            (rs, n) -> montar(rs)
        );
    }

    public List<Consulta> listarPorMedico(long medicoId) {

        return jdbc.query(
            SELECT + " WHERE medico_id = ? ORDER BY data_hora, id",
            (rs, n) -> montar(rs),
            medicoId
        );
    }

    public List<Consulta> listarPorPaciente(long pacienteId) {

        return jdbc.query(
            SELECT + " WHERE paciente_id = ? ORDER BY data_hora, id",
            (rs, n) -> montar(rs),
            pacienteId
        );
    }

    public boolean existeConflitoHorario(
        long medicoId,
        long pacienteId,
        java.time.LocalDateTime dataHora
    ) {
        Integer total = jdbc.queryForObject(
            "SELECT COUNT(*) FROM consultas "
            + "WHERE data_hora = ? "
            + "AND status IN ('AGENDADA', 'CONFIRMADA') "
            + "AND (medico_id = ? OR paciente_id = ?)",
            Integer.class,
            dataHora, medicoId, pacienteId
        );

        return total != null && total > 0;
    }

    public void bloquearParticipantes(
        long medicoId, long pacienteId
    ) {
        List<Long> ids = jdbc.query(
            "SELECT id FROM pessoas "
            + "WHERE id IN (?, ?) ORDER BY id FOR UPDATE",
            (rs, n) -> rs.getLong("id"),
            medicoId, pacienteId
        );

        if (ids.size() != 2) {
            throw new RegraNegocioException(
                "Medico ou paciente nao encontrado."
            );
        }
    }

    public void alterarStatus(long id, StatusConsulta status) {

        if (jdbc.update(
            "UPDATE consultas SET status = ? WHERE id = ?",
            status.name(), id
        ) == 0) {
            throw new RegraNegocioException(
                "Consulta nao encontrada."
            );
        }
    }
}