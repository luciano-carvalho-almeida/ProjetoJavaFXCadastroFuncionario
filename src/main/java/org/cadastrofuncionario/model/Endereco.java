package org.cadastrofuncionario.model;

public class Endereco {
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;

    public Endereco() {
    }

    public Endereco(String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String cep) {
        setLogradouro(logradouro);
        setNumero(numero);
        setComplemento(complemento);
        setBairro(bairro);
        setCidade(cidade);
        setEstado(estado);
        setCep(cep);
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = obrigatorio(logradouro, "Logradouro");
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = obrigatorio(numero, "Número");
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        // Complemento é opcional: vazio vira "" (nunca null)
        String valor = (complemento == null) ? "" : complemento.trim();
        if (valor.contains(";")) {
            throw new IllegalArgumentException("Complemento inválido: não use ponto-e-vírgula.");
        }
        this.complemento = valor;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = obrigatorio(bairro, "Bairro");
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = obrigatorio(cidade, "Cidade");
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        if (estado == null || !estado.trim().matches("[A-Za-z]{2}")) {
            throw new IllegalArgumentException("Estado inválido: use a sigla com 2 letras (ex.: GO).");
        }
        this.estado = estado.trim().toUpperCase();
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        if (cep == null || !cep.trim().matches("\\d{5}-?\\d{3}")) {
            throw new IllegalArgumentException("CEP inválido: use 8 dígitos (ex.: 74000-000).");
        }
        this.cep = cep.trim().replace("-", ""); // guarda só os dígitos
    }

    private static String obrigatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " inválido: campo obrigatório.");
        }
        if (valor.contains(";")) {
            throw new IllegalArgumentException(campo + " inválido: não use ponto-e-vírgula.");
        }
        return valor.trim();
    }
}
