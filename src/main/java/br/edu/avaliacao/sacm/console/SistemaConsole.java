package br.edu.avaliacao.sacm.console;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import br.edu.avaliacao.sacm.enums.*;
import br.edu.avaliacao.sacm.exception.RegraNegocioException;
import br.edu.avaliacao.sacm.model.*;
import br.edu.avaliacao.sacm.repository.*;
import br.edu.avaliacao.sacm.service.ConsultaService;

@Component
public class SistemaConsole implements CommandLineRunner {

    private static final DateTimeFormatter DATA =
        DateTimeFormatter.ofPattern("dd/MM/uuuu");

    private static final DateTimeFormatter DATA_HORA =
        DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

    private final Scanner scanner = new Scanner(System.in);
    private final ConsultaService consultas;
    private final MedicoRepository medicos;
    private final PacienteRepository pacientes;

    public SistemaConsole(
        ConsultaService consultas,
        MedicoRepository medicos,
        PacienteRepository pacientes
    ) {
        this.consultas = consultas;
        this.medicos = medicos;
        this.pacientes = pacientes;
    }

    @Override
    public void run(String... args) {
        System.out.println(
            "\nSACM - Sistema de Agendamento de Consultas Medicas"
        );
        exibirMenu();
    }

    private void exibirMenu() {

        boolean funcionando = true;

        while (funcionando) {

            System.out.println("\n========== MENU PRINCIPAL ==========");
            System.out.println("1 - Cadastrar medico");
            System.out.println("2 - Cadastrar paciente");
            System.out.println("3 - Agendar consulta");
            System.out.println("4 - Listar consultas");
            System.out.println("5 - Confirmar / cancelar / finalizar consulta");
            System.out.println("6 - Alterar disponibilidade do medico");
            System.out.println("7 - Listar medicos");
            System.out.println("8 - Listar pacientes");
            System.out.println("9 - Ativar / desativar medico ou paciente");
            System.out.println("0 - Encerrar");

            try {
                switch (ler("Escolha uma opcao: ")) {

                    case "1" -> cadastrarMedico();

                    case "2" -> cadastrarPaciente();

                    case "3" -> agendarConsulta();

                    case "4" -> listarConsultas();

                    case "5" -> alterarStatusConsulta();

                    case "6" -> alterarDisponibilidade();

                    case "7" -> listarMedicos();

                    case "8" -> listarPacientes();

                    case "9" -> alterarStatusPessoa();

                    case "0" -> funcionando = false;

                    default -> System.out.println("Opcao invalida.");
                }

            } catch (
                RegraNegocioException |
                IllegalArgumentException |
                DateTimeParseException e
            ) {
                System.out.println("AVISO: " + e.getMessage());

            } catch (DataAccessException e) {
                System.out.println(
                    "ERRO NO BANCO: nao foi possivel concluir a operacao."
                );
                System.out.println(
                    "Detalhe tecnico: "
                    + e.getMostSpecificCause().getMessage()
                );
            }
        }

        encerrar();
    }

    private void cadastrarMedico() {

        System.out.println("\n--- CADASTRAR MEDICO ---");

        String nome = ler("Nome: ");
        String cpf = ler("CPF (11 numeros): ");
        LocalDate nascimento = lerData(
            "Nascimento (dd/MM/aaaa): "
        );
        String email = ler("E-mail: ");
        String telefone = ler("Telefone: ");
        String cidade = ler("Cidade: ");
        String estado = ler("UF (ex.: PR): ");
        String crm = ler("CRM: ");

        Especialidade especialidade = escolherEnum(
            Especialidade.values(),
            "Especialidade"
        );

        BigDecimal valor = new BigDecimal(
            ler("Valor da consulta (ex.: 150.00): ")
                .replace(",", ".")
        );

        Medico medico = new Medico(
            null, nome, cpf, nascimento,
            email, telefone, cidade, estado,
            null, StatusPessoa.ATIVO,
            crm, especialidade,
            DisponibilidadeMedico.DISPONIVEL,
            valor
        );

        Medico salvo = medicos.salvar(medico);

        System.out.println(
            "Medico cadastrado com ID " + salvo.getId() + "."
        );
    }

    private void cadastrarPaciente() {

        System.out.println("\n--- CADASTRAR PACIENTE ---");

        String nome = ler("Nome: ");
        String cpf = ler("CPF (11 numeros): ");
        LocalDate nascimento = lerData(
            "Nascimento (dd/MM/aaaa): "
        );
        String email = ler("E-mail: ");
        String telefone = ler("Telefone: ");
        String cidade = ler("Cidade: ");
        String estado = ler("UF (ex.: PR): ");

        String prontuario = ler("Numero do prontuario: ");
        String convenio = ler("Convenio (ou PARTICULAR): ");
        String contato = ler(
            "Nome do contato de emergencia: "
        );
        String telContato = ler(
            "Telefone do contato de emergencia: "
        );

        Paciente paciente = new Paciente(
            null, nome, cpf, nascimento,
            email, telefone, cidade, estado,
            null, StatusPessoa.ATIVO,
            prontuario, convenio,
            contato, telContato
        );

        Paciente salvo = pacientes.salvar(paciente);

        System.out.println(
            "Paciente cadastrado com ID " + salvo.getId() + "."
        );
    }

    private void agendarConsulta() {

        System.out.println("\n--- AGENDAR CONSULTA ---");

        listarMedicos();
        listarPacientes();

        long medicoId = lerId("ID do medico: ");
        long pacienteId = lerId("ID do paciente: ");

        LocalDateTime horario = lerDataHora(
            "Data e hora futura (dd/MM/aaaa HH:mm): "
        );

        String motivo = ler("Motivo da consulta: ");

        Consulta consulta = consultas.agendarConsulta(
            medicoId, pacienteId, horario, motivo
        );

        System.out.println(
            "AGENDAMENTO REALIZADO! " + consulta.getResumo()
        );
    }

    private void listarConsultas() {

        System.out.println("\n--- LISTAR CONSULTAS ---");
        System.out.println(
            "1 - Todas | 2 - Por medico | 3 - Por paciente"
        );

        String opcao = ler("Opcao: ");

        List<Consulta> lista = switch (opcao) {

            case "1" -> consultas.listarConsultas();

            case "2" -> consultas.listarConsultasPorMedico(
                lerId("ID do medico: ")
            );

            case "3" -> consultas.listarConsultasPorPaciente(
                lerId("ID do paciente: ")
            );

            default -> throw new RegraNegocioException(
                "Opcao invalida."
            );
        };

        if (lista.isEmpty()) {
            System.out.println("Nenhuma consulta encontrada.");
        } else {
            lista.forEach(
                c -> System.out.println(c.getResumo())
            );
        }
    }

    private void alterarStatusConsulta() {

        System.out.println(
            "\n--- ALTERAR STATUS DA CONSULTA ---"
        );

        long id = lerId("ID da consulta: ");

        System.out.println(
            "1 - Confirmar | 2 - Cancelar | 3 - Finalizar"
        );

        String opcao = ler("Opcao: ");

        switch (opcao) {

            case "1" -> consultas.confirmarConsulta(id);

            case "2" -> consultas.cancelarConsulta(id);

            case "3" -> consultas.finalizarConsulta(id);

            default -> throw new RegraNegocioException(
                "Opcao invalida."
            );
        }

        System.out.println(
            "Status atualizado no banco de dados."
        );
    }

    private void alterarDisponibilidade() {

        listarMedicos();

        long id = lerId("ID do medico: ");

        DisponibilidadeMedico nova = escolherEnum(
            DisponibilidadeMedico.values(),
            "Disponibilidade"
        );

        medicos.alterarDisponibilidade(id, nova);

        System.out.println("Disponibilidade alterada.");
    }

    private void alterarStatusPessoa() {

        System.out.println("1 - Medico | 2 - Paciente");

        String tipo = ler("Tipo: ");
        long id = lerId("ID: ");

        StatusPessoa status = escolherEnum(
            StatusPessoa.values(),
            "Novo status"
        );

        switch (tipo) {

            case "1" -> medicos.alterarStatus(id, status);

            case "2" -> pacientes.alterarStatus(id, status);

            default -> throw new RegraNegocioException(
                "Tipo invalido."
            );
        }

        System.out.println("Status da pessoa atualizado.");
    }

    private void listarMedicos() {

        System.out.println("\n--- MEDICOS ---");

        List<Medico> lista = medicos.listarTodos();

        if (lista.isEmpty()) {
            System.out.println("Nenhum medico cadastrado.");
        } else {
            lista.forEach(
                m -> System.out.println(m.getDescricao())
            );
        }
    }

    private void listarPacientes() {

        System.out.println("\n--- PACIENTES ---");

        List<Paciente> lista = pacientes.listarTodos();

        if (lista.isEmpty()) {
            System.out.println("Nenhum paciente cadastrado.");
        } else {
            lista.forEach(
                p -> System.out.println(p.getDescricao())
            );
        }
    }

    private String ler(String pergunta) {
        System.out.print(pergunta);
        return scanner.nextLine().trim();
    }

    private long lerId(String pergunta) {
        return Long.parseLong(ler(pergunta));
    }

    private LocalDate lerData(String pergunta) {
        return LocalDate.parse(
            ler(pergunta), DATA
        );
    }

    private LocalDateTime lerDataHora(String pergunta) {
        return LocalDateTime.parse(
            ler(pergunta), DATA_HORA
        );
    }

    private <T extends Enum<T>> T escolherEnum(
        T[] valores, String titulo
    ) {

        System.out.println("Opcoes de " + titulo + ":");

        for (int i = 0; i < valores.length; i++) {
            System.out.println(
                (i + 1) + " - " + valores[i].name()
            );
        }

        int indice = Integer.parseInt(
            ler("Numero da opcao: ")
        ) - 1;

        if (indice < 0 || indice >= valores.length) {
            throw new RegraNegocioException(
                "Opcao invalida para " + titulo + "."
            );
        }

        return valores[indice];
    }

    private void encerrar() {
        System.out.println(
            "Programa encerrado. "
            + "Os dados permanecem salvos no MySQL."
        );
    }
}