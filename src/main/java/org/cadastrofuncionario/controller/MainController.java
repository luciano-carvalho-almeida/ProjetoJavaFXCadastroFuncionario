package org.cadastrofuncionario.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.cadastrofuncionario.model.Endereco;
import org.cadastrofuncionario.model.Funcionario;
import org.cadastrofuncionario.repository.FuncionarioRepository;
import org.cadastrofuncionario.service.RelatorioService;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainController {

    //Aba Cadastro
    @FXML private TextField txtMatricula;
    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private TextField txtCargo;
    @FXML private DatePicker dpNascimento;
    @FXML private DatePicker dpContratacao;
    @FXML private TextField txtSalario;
    @FXML private TextField txtLogradouro;
    @FXML private TextField txtNumero;
    @FXML private TextField txtComplemento;
    @FXML private TextField txtBairro;
    @FXML private TextField txtCidade;
    @FXML private TextField txtEstado;
    @FXML private TextField txtCep;
    @FXML private Button btnLimpar;
    @FXML private Button btnSalvar;
    @FXML private Label lblMensagemCadastro;

    //Aba Listagem
    @FXML private TextField txtBuscaMatricula;
    @FXML private Button btnConsultar;
    @FXML private Button btnListarTodos;
    @FXML private TableView<Funcionario> tabelaFuncionarios;
    @FXML private TableColumn<Funcionario, String> colMatricula;
    @FXML private TableColumn<Funcionario, String> colNome;
    @FXML private TableColumn<Funcionario, String> colCpf;
    @FXML private TableColumn<Funcionario, String> colCargo;
    @FXML private TableColumn<Funcionario, String> colSalario;
    @FXML private TableColumn<Funcionario, String> colCidade;
    @FXML private Button btnExcluir;
    @FXML private Label lblMensagemListagem;

    // Aba Relatórios
    @FXML private TextField txtCargoFiltro;
    @FXML private TextField txtSalMin;
    @FXML private TextField txtSalMax;
    @FXML private Button btnFiltrarCargo;
    @FXML private Button btnFiltrarFaixa;
    @FXML private Button btnMediaCargo;
    @FXML private Button btnAgruparCidade;
    @FXML private TextArea txtResultado;
    @FXML private Label lblMensagemRelatorio;

    //Dados e serviços
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final String COR_ERRO = "-fx-text-fill: #c62828;";
    private static final String COR_OK = "-fx-text-fill: #1a7f37;";

    private final FuncionarioRepository repositorio = new FuncionarioRepository();
    private final RelatorioService relatorios = new RelatorioService();
    private final ObservableList<Funcionario> dadosTabela = FXCollections.observableArrayList();

    //  Inicialização
    @FXML
    private void initialize() {
        // cada coluna recebe uma "receita" para tirar o texto de um Funcionario
        colMatricula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMatricula()));
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colCpf.setCellValueFactory(c -> new SimpleStringProperty(formatarCpf(c.getValue().getCpf())));
        colCargo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCargo()));
        colSalario.setCellValueFactory(c -> new SimpleStringProperty(moeda(c.getValue().getSalario())));
        colCidade.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEndereco().getCidade() + "/" + c.getValue().getEndereco().getEstado()));
        tabelaFuncionarios.setItems(dadosTabela);

        try {
            repositorio.carregar(); // lê o CSV ao iniciar (requisito do enunciado)
            dadosTabela.setAll(repositorio.listarTodos());
        } catch (IOException e) {
            mostrar(lblMensagemListagem, "Não foi possível ler o arquivo CSV: " + e.getMessage(), false);
        }
    }

    //  Aba Cadastro
    @FXML
    private void salvar() {
        try {
            // os construtores chamam os setters, então toda validação acontece aqui
            Endereco endereco = new Endereco(
                    txtLogradouro.getText(), txtNumero.getText(), txtComplemento.getText(),
                    txtBairro.getText(), txtCidade.getText(), txtEstado.getText(), txtCep.getText());
            Funcionario funcionario = new Funcionario(
                    txtMatricula.getText().trim(), txtNome.getText(), txtCpf.getText(),
                    dpNascimento.getValue(), txtCargo.getText(), lerValor(txtSalario, "Salário"),
                    dpContratacao.getValue(), endereco);

            repositorio.adicionar(funcionario); // valida matrícula repetida e grava no CSV
            dadosTabela.setAll(repositorio.listarTodos());
            limpar();
            mostrar(lblMensagemCadastro, "Funcionário cadastrado com sucesso!", true);
        } catch (IllegalArgumentException e) {
            mostrar(lblMensagemCadastro, e.getMessage(), false); // texto escrito nos setters
        } catch (IOException e) {
            mostrar(lblMensagemCadastro, "Erro ao gravar o arquivo: " + e.getMessage(), false);
        }
    }

    @FXML
    private void limpar() {
        for (TextField campo : List.of(txtMatricula, txtNome, txtCpf, txtCargo, txtSalario,
                txtLogradouro, txtNumero, txtComplemento, txtBairro, txtCidade, txtEstado, txtCep)) {
            campo.clear();
        }
        dpNascimento.setValue(null);
        dpContratacao.setValue(null);
        lblMensagemCadastro.setText("");
    }


    //  Aba Listagem
    @FXML
    private void consultar() {
        String matricula = txtBuscaMatricula.getText().trim();
        if (matricula.isEmpty()) {
            mostrar(lblMensagemListagem, "Digite a matrícula para consultar.", false);
            return;
        }
        Optional<Funcionario> achado = repositorio.buscar(matricula);
        if (achado.isPresent()) {
            dadosTabela.setAll(achado.get());
            mostrar(lblMensagemListagem, "Funcionário encontrado.", true);
        } else {
            dadosTabela.clear();
            mostrar(lblMensagemListagem, "Nenhum funcionário com a matrícula " + matricula + ".", false);
        }
    }

    @FXML
    private void listarTodos() {
        dadosTabela.setAll(repositorio.listarTodos());
        txtBuscaMatricula.clear();
        mostrar(lblMensagemListagem, dadosTabela.size() + " funcionário(s) cadastrado(s).", true);
    }

    @FXML
    private void excluir() {
        Funcionario selecionado = tabelaFuncionarios.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrar(lblMensagemListagem, "Selecione um funcionário na tabela para excluir.", false);
            return;
        }
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Excluir " + selecionado.getNome() + " (matrícula " + selecionado.getMatricula() + ")?",
                ButtonType.YES, ButtonType.NO);
        confirmacao.setHeaderText(null);
        if (confirmacao.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
            return;
        }
        try {
            repositorio.remover(selecionado.getMatricula());
            dadosTabela.setAll(repositorio.listarTodos());
            mostrar(lblMensagemListagem, "Funcionário excluído.", true);
        } catch (IOException e) {
            mostrar(lblMensagemListagem, "Erro ao gravar o arquivo: " + e.getMessage(), false);
        }
    }

    //  Aba Relatórios
    @FXML
    private void filtrarPorCargo() {
        String cargo = txtCargoFiltro.getText().trim();
        if (cargo.isEmpty()) {
            mostrar(lblMensagemRelatorio, "Digite um cargo para filtrar.", false);
            return;
        }
        List<Funcionario> resultado = relatorios.filtrarPorCargo(repositorio.listarTodos(), cargo);
        txtResultado.setText("Funcionários do cargo \"" + cargo + "\" (" + resultado.size() + "):\n\n"
                + formatarLista(resultado));
        lblMensagemRelatorio.setText("");
    }

    @FXML
    private void filtrarPorFaixaSalarial() {
        try {
            BigDecimal min = lerValor(txtSalMin, "Salário mínimo");
            BigDecimal max = lerValor(txtSalMax, "Salário máximo");
            List<Funcionario> resultado = relatorios.filtrarPorFaixaSalarial(repositorio.listarTodos(), min, max);
            txtResultado.setText("Salário entre " + moeda(min) + " e " + moeda(max)
                    + " (" + resultado.size() + "):\n\n" + formatarLista(resultado));
            lblMensagemRelatorio.setText("");
        } catch (IllegalArgumentException e) {
            mostrar(lblMensagemRelatorio, e.getMessage(), false);
        }
    }

    @FXML
    private void mediaSalarialPorCargo() {
        Map<String, BigDecimal> medias = relatorios.mediaSalarialPorCargo(repositorio.listarTodos());
        if (medias.isEmpty()) {
            txtResultado.setText("Nenhum funcionário cadastrado.");
            return;
        }
        txtResultado.setText("Média salarial por cargo:\n\n" + medias.entrySet().stream()
                .map(e -> e.getKey() + ": " + moeda(e.getValue()))
                .collect(Collectors.joining("\n")));
        lblMensagemRelatorio.setText("");
    }

    @FXML
    private void agruparPorCidade() {
        Map<String, List<Funcionario>> grupos = relatorios.agruparPorCidade(repositorio.listarTodos());
        if (grupos.isEmpty()) {
            txtResultado.setText("Nenhum funcionário cadastrado.");
            return;
        }
        txtResultado.setText(grupos.entrySet().stream()
                .map(e -> e.getKey() + " (" + e.getValue().size() + ")\n" + e.getValue().stream()
                        .map(f -> "   - " + f.getNome() + " (matrícula " + f.getMatricula() + ")")
                        .collect(Collectors.joining("\n")))
                .collect(Collectors.joining("\n\n")));
        lblMensagemRelatorio.setText("");
    }

    //Auxiliares
    /** Mostra texto num Label, verde se deu certo e vermelho se deu erro. */
    private void mostrar(Label label, String texto, boolean sucesso) {
        label.setStyle(sucesso ? COR_OK : COR_ERRO);
        label.setText(texto);
    }

    /** Converte "3500.00" ou "3.500,00" em BigDecimal; erro vira mensagem amigável. */
    private BigDecimal lerValor(TextField campo, String nomeCampo) {
        String texto = campo.getText().trim().replace(" ", "");
        if (texto.contains(",")) {
            texto = texto.replace(".", "").replace(",", ".");
        }
        try {
            return new BigDecimal(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(nomeCampo + " inválido: use apenas números (ex.: 3500.00).");
        }
    }

    private String moeda(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance(PT_BR).format(valor);
    }

    private String formatarCpf(String cpf) {
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    private String formatarLista(List<Funcionario> lista) {
        if (lista.isEmpty()) {
            return "Nenhum funcionário encontrado.";
        }
        return lista.stream()
                .map(f -> f.getMatricula() + " - " + f.getNome() + " - " + f.getCargo() + " - " + moeda(f.getSalario()))
                .collect(Collectors.joining("\n"));
    }
}