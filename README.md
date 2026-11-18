
# SACM - Sistema de Agendamento de Consultas Médicas

## 1. Sobre o projeto

O SACM é um sistema de agendamento de consultas médicas desenvolvido para a avaliação do 2º bimestre da disciplina de Programação Orientada a Objetos, do curso de Engenharia de Software.

O objetivo do projeto é organizar os cadastros de médicos e pacientes, permitir o agendamento de consultas e evitar conflitos de horários.

O sistema funciona pelo terminal e utiliza um banco de dados MySQL para armazenar as informações, permitindo que os registros sejam consultados mesmo depois de encerrar o programa.

## 2. Tecnologias utilizadas

- Java 21
- Spring Boot
- JDBC (JdbcTemplate)
- MySQL
- Maven
- Visual Studio Code
- XAMPP e DBeaver

## 3. Funcionalidades

O sistema possui as seguintes funcionalidades:

- Cadastrar médicos e pacientes.
- Consultar os cadastros realizados.
- Ativar e desativar médicos e pacientes.
- Alterar a disponibilidade dos médicos.
- Agendar consultas.
- Impedir consultas com conflito de horário.
- Confirmar, cancelar e finalizar consultas.
- Listar consultas por médico ou paciente.
- Armazenar os dados no MySQL.

## 4. Organização do projeto

O código foi separado em pacotes para facilitar a organização e a manutenção.

- **model:** contém as classes Pessoa, Medico, Paciente e Consulta.
- **enums:** define os estados, as especialidades e a disponibilidade.
- **interfaces:** contém a interface Agendavel.
- **repository:** realiza o acesso ao banco de dados.
- **service:** contém as regras de negócio dos agendamentos.
- **exception:** trata os erros relacionados às regras do sistema.
- **console:** apresenta o menu e recebe as informações digitadas pelo usuário.

A classe Pessoa é abstrata e serve como base para Médico e Paciente, utilizando o conceito de herança.

## 5. Como executar o projeto

Para executar, é necessário ter o Java 21, Maven e MySQL instalados.

1. Abrir o projeto no Visual Studio Code.
2. Iniciar o MySQL pelo XAMPP.
3. Abrir o DBeaver e conectar ao MySQL.
4. Executar o arquivo `database/schema.sql` para criar o banco `sacm_db` e suas tabelas.
5. Configurar as variáveis de ambiente `DB_USER` e `DB_PASSWORD` de acordo com o usuário do MySQL.
6. Abrir o terminal na pasta principal do projeto.
7. Compilar o projeto com `mvn clean compile`.
8. Iniciar o sistema com `mvn spring-boot:run`.

No PowerShell, para uma instalação local com usuário root e sem senha, as variáveis podem ser configuradas assim:

    $env:DB_USER="root"
    Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue

Caso o Maven não esteja configurado no PATH, é possível executar o arquivo `mvn.cmd` diretamente pela pasta em que ele foi instalado.

Depois da inicialização, o menu principal aparecerá no terminal com as opções do sistema.

## 6. Regras de negócio

Para realizar um agendamento, o médico e o paciente precisam estar cadastrados e ativos. O médico também precisa estar disponível.

O sistema verifica se a data da consulta está no futuro e se o motivo foi informado.

Não é permitido agendar duas consultas ativas para o mesmo médico ou paciente na mesma data e horário.

As consultas podem passar pelos seguintes estados:

- AGENDADA
- CONFIRMADA
- CANCELADA
- FINALIZADA

Quando uma consulta é cancelada, seu horário pode ser utilizado em um novo agendamento.

## 7. Testes realizados

Os testes foram feitos pelo terminal do VS Code, utilizando o MySQL para armazenar os dados.

Foram verificados os cadastros de médico e paciente, o agendamento de uma consulta, a tentativa de marcar outra consulta no mesmo horário e o cancelamento com liberação do horário.

Também foram realizados testes de alteração do status das consultas.

Para verificar os registros no banco de dados, foi utilizado o DBeaver com consultas SQL.

Exemplo de consulta utilizada:

    SELECT id, medico_id, paciente_id,
           data_hora, motivo, status
    FROM sacm_db.consultas
    ORDER BY id;

Os dados também foram consultados após reiniciar a aplicação, confirmando que permaneceram armazenados no banco.

## 8. Uso de inteligência artificial

O ChatGPT foi utilizado como ferramenta de apoio na organização e elaboração de partes do código, na configuração do ambiente e na identificação de erros durante a execução.

As orientações foram aplicadas no VS Code e os testes foram realizados manualmente. Durante o processo, também foram feitos ajustes na configuração da conexão com o MySQL.

## 9. Autoria

Jéssica Limonge

Curso: Engenharia de Software

Disciplina: Programação Orientada a Objetos

Projeto: Avaliação do 2º Bimestre - 2026
