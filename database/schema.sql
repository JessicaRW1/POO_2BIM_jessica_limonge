CREATE DATABASE IF NOT EXISTS sacm_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE sacm_db;

CREATE TABLE IF NOT EXISTS pessoas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tipo VARCHAR(10) NOT NULL,
    nome VARCHAR(120) NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    data_nascimento DATE NOT NULL,
    email VARCHAR(120) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    cidade VARCHAR(90) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    data_cadastro DATETIME NOT NULL,
    status VARCHAR(10) NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_pessoas_cpf UNIQUE (cpf)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS medicos (
    pessoa_id BIGINT NOT NULL,
    crm VARCHAR(25) NOT NULL,
    especialidade VARCHAR(30) NOT NULL,
    disponibilidade VARCHAR(15) NOT NULL,
    valor_consulta DECIMAL(10,2) NOT NULL,

    PRIMARY KEY (pessoa_id),

    CONSTRAINT uk_medicos_crm UNIQUE (crm),

    CONSTRAINT fk_medicos_pessoa
        FOREIGN KEY (pessoa_id)
        REFERENCES pessoas(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pacientes (
    pessoa_id BIGINT NOT NULL,
    numero_prontuario VARCHAR(25) NOT NULL,
    convenio VARCHAR(100) NOT NULL,
    contato_emergencia VARCHAR(120) NOT NULL,
    telefone_emergencia VARCHAR(20) NOT NULL,

    PRIMARY KEY (pessoa_id),

    CONSTRAINT uk_pacientes_prontuario
        UNIQUE (numero_prontuario),

    CONSTRAINT fk_pacientes_pessoa
        FOREIGN KEY (pessoa_id)
        REFERENCES pessoas(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS consultas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    medico_id BIGINT NOT NULL,
    paciente_id BIGINT NOT NULL,
    data_hora DATETIME NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    status VARCHAR(15) NOT NULL,
    data_agendamento DATETIME NOT NULL,
    observacao VARCHAR(500),

    PRIMARY KEY (id),

    INDEX idx_medico_data (medico_id, data_hora),
    INDEX idx_paciente_data (paciente_id, data_hora),

    CONSTRAINT fk_consulta_medico
        FOREIGN KEY (medico_id)
        REFERENCES medicos(pessoa_id),

    CONSTRAINT fk_consulta_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES pacientes(pessoa_id)
) ENGINE=InnoDB;