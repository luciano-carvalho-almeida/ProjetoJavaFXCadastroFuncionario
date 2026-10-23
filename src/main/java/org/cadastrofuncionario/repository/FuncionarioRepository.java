package org.cadastrofuncionario.repository;

import org.cadastrofuncionario.model.Endereco;
import org.cadastrofuncionario.model.Funcionario;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Guarda os funcionários em memória e mantém o arquivo CSV (separado por ';') sincronizado.
 * Cada linha do CSV tem 14 campos: 7 do funcionário + 7 do endereço.
 */
public class FuncionarioRepository {

    private static final String SEPARADOR = ";";
    private static final String CABECALHO =
            "matricula;nome;cpf;dataNascimento;cargo;salario;dataContratacao;"
                    + "logradouro;numero;complemento;bairro;cidade;estado;cep";
    private static final int TOTAL_CAMPOS = 14;

    private final List<Funcionario> funcionarios = new ArrayList<>();
    private final Path arquivo;

    public FuncionarioRepository() {
        this(Path.of("funcionarios.csv"));
    }

    public FuncionarioRepository(Path arquivo) {
        this.arquivo = arquivo;
    }

    //consultas

    public List<Funcionario> listarTodos() {
        return new ArrayList<>(funcionarios);
    }

    public Optional<Funcionario> buscar(String matricula) {
        return funcionarios.stream()
                .filter(f -> f.getMatricula().equals(matricula))
                .findFirst();
    }

    //alterações

    public void adicionar(Funcionario f) throws IOException {
        if (buscar(f.getMatricula()).isPresent()) {
            throw new IllegalArgumentException("Já existe funcionário com a matrícula " + f.getMatricula() + ".");
        }
        funcionarios.add(f);
        try {
            salvar();
        } catch (IOException e) {
            funcionarios.remove(f); // desfaz: memória e arquivo não podem ficar diferentes
            throw e;
        }
    }

    public boolean remover(String matricula) throws IOException {
        Optional<Funcionario> alvo = buscar(matricula);
        if (alvo.isEmpty()) {
            return false;
        }
        funcionarios.remove(alvo.get());
        try {
            salvar();
        } catch (IOException e) {
            funcionarios.add(alvo.get());
            throw e;
        }
        return true;
    }

    //arquivo CSV

    /** Lê o CSV (se existir) e preenche a lista. Linhas inválidas são ignoradas. */
    public void carregar() throws IOException {
        funcionarios.clear();
        if (!Files.exists(arquivo)) {
            return; // primeira execução: ainda não há arquivo
        }
        List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
        for (int i = 1; i < linhas.size(); i++) { // i = 1 pula o cabeçalho
            String linha = linhas.get(i).trim();
            if (linha.isEmpty()) {
                continue;
            }
            try {
                funcionarios.add(deLinha(linha));
            } catch (RuntimeException e) {
                System.err.println("Linha " + (i + 1) + " ignorada (" + e.getMessage() + ")");
            }
        }
    }

    private void salvar() throws IOException {
        List<String> linhas = new ArrayList<>();
        linhas.add(CABECALHO);
        for (Funcionario f : funcionarios) {
            linhas.add(paraLinha(f));
        }
        Files.write(arquivo, linhas, StandardCharsets.UTF_8);
    }

    private String paraLinha(Funcionario f) {
        Endereco e = f.getEndereco();
        return String.join(SEPARADOR,
                f.getMatricula(),
                f.getNome(),
                f.getCpf(),
                f.getDataNascimento().toString(),
                f.getCargo(),
                f.getSalario().toPlainString(),
                f.getDataContratacao().toString(),
                e.getLogradouro(),
                e.getNumero(),
                e.getComplemento(),
                e.getBairro(),
                e.getCidade(),
                e.getEstado(),
                e.getCep());
    }

    private Funcionario deLinha(String linha) {
        String[] c = linha.split(SEPARADOR, -1); // -1 mantém campos vazios no fim
        if (c.length != TOTAL_CAMPOS) {
            throw new IllegalArgumentException("esperados " + TOTAL_CAMPOS + " campos, achei " + c.length);
        }
        Endereco endereco = new Endereco(c[7], c[8], c[9], c[10], c[11], c[12], c[13]);
        return new Funcionario(c[0], c[1], c[2], LocalDate.parse(c[3]), c[4],
                new BigDecimal(c[5]), LocalDate.parse(c[6]), endereco);
    }
}
