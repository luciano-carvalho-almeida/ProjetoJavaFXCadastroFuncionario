package org.cadastrofuncionario.service;

import org.cadastrofuncionario.model.Funcionario;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Relatórios feitos só com a Stream API (filter, map, reduce, groupingBy). */
public class RelatorioService {

    /** filter: mantém só os funcionários do cargo informado (ignora maiúsculas/minúsculas). */
    public List<Funcionario> filtrarPorCargo(List<Funcionario> lista, String cargo) {
        return lista.stream()
                .filter(f -> f.getCargo().equalsIgnoreCase(cargo.trim()))
                .collect(Collectors.toList());
    }

    /** filter: salário entre min e max (inclusive). */
    public List<Funcionario> filtrarPorFaixaSalarial(List<Funcionario> lista, BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Faixa inválida: o mínimo é maior que o máximo.");
        }
        return lista.stream()
                .filter(f -> f.getSalario().compareTo(min) >= 0 && f.getSalario().compareTo(max) <= 0)
                .collect(Collectors.toList());
    }

    /** groupingBy + map + reduce: média salarial de cada cargo, em ordem alfabética. */
    public Map<String, BigDecimal> mediaSalarialPorCargo(List<Funcionario> lista) {
        return lista.stream().collect(Collectors.groupingBy(
                Funcionario::getCargo,
                () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER),
                Collectors.collectingAndThen(Collectors.toList(), grupo -> {
                    BigDecimal soma = grupo.stream()
                            .map(Funcionario::getSalario)                  // map: só os salários
                            .reduce(BigDecimal.ZERO, BigDecimal::add);     // reduce: soma tudo
                    return soma.divide(BigDecimal.valueOf(grupo.size()), 2, RoundingMode.HALF_UP);
                })));
    }

    /** groupingBy: funcionários agrupados por "Cidade/UF". */
    public Map<String, List<Funcionario>> agruparPorCidade(List<Funcionario> lista) {
        return lista.stream().collect(Collectors.groupingBy(
                f -> f.getEndereco().getCidade() + "/" + f.getEndereco().getEstado(),
                () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER),
                Collectors.toList()));
    }
}
