package org.cadastrofuncionario.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public class Funcionario {
    private String matricula;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String cargo;
    private BigDecimal salario;
    private LocalDate dataContratacao;
    private Endereco endereco;

    public Funcionario() {
    }

    public Funcionario(String matricula, String nome, String cpf, LocalDate dataNascimento, String cargo, BigDecimal salario, LocalDate dataContratacao, Endereco endereco) {
        setMatricula(matricula);
        setNome(nome);
        setCpf(cpf);
        setDataNascimento(dataNascimento);
        setCargo(cargo);
        setSalario(salario);
        setDataContratacao(dataContratacao);
        setEndereco(endereco);
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        if (matricula == null || !matricula.matches("\\d{6}")) {
            throw new IllegalArgumentException("Matrícula inválida: use exatamente 6 dígitos numéricos.");
        }
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().length() < 3) {
            throw new IllegalArgumentException("Nome inválido: informe pelo menos 3 caracteres.");
        }
        this.nome = nome.trim();
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        if (cpf == null) {
            throw new IllegalArgumentException("CPF inválido: campo vazio.");
        }
        String digitos = cpf.replaceAll("\\D", "");
        if (digitos.length() != 11 || digitos.chars().distinct().count() == 1) {
            throw new IllegalArgumentException("CPF inválido: formato incorreto.");
        }

        int dv1 = calcularDigito(digitos, 9);
        int dv2 = calcularDigito(digitos, 10);

        if (dv1 != Character.getNumericValue(digitos.charAt(9))
                || dv2 != Character.getNumericValue(digitos.charAt(10))) {
            throw new IllegalArgumentException("CPF inválido: dígitos verificadores não conferem.");
        }

        this.cpf = digitos;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null || Period.between(dataNascimento, LocalDate.now()).getYears() < 18) {
            throw new IllegalArgumentException("Data de nascimento inválida: o funcionário deve ter pelo menos 18 anos.");
        }
        this.dataNascimento = dataNascimento;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        if (cargo == null || cargo.trim().isEmpty()) {
            throw new IllegalArgumentException("Cargo inválido: campo obrigatório.");
        }
        this.cargo = cargo.trim();
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        if (salario == null || salario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Salário inválido: informe um valor maior que zero.");
        }
        this.salario = salario;
    }

    public LocalDate getDataContratacao() {
        return dataContratacao;
    }

    public void setDataContratacao(LocalDate dataContratacao) {
        if (dataContratacao == null || dataContratacao.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de contratação inválida: não pode ser vazia nem no futuro.");
        }
        this.dataContratacao = dataContratacao;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        if (endereco == null) {
            throw new IllegalArgumentException("Endereço inválido: campo obrigatório.");
        }
        this.endereco = endereco;
    }

    private int calcularDigito(String digitos, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            int digito = Character.getNumericValue(digitos.charAt(i));
            int peso = quantidade + 1 - i;
            soma += digito * peso;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

}

