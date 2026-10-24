**Aluno:** Luciano Carvalho de Almeida
**Curso:** Engenharia de Software – 4º período
**Professor:** Daniel Corrêa da Silva

# Sistema de Cadastro de Funcionários

Projeto P1 de Programação Orientada a Objetos III.

## Sobre o projeto

Este projeto foi desenvolvido para facilitar o cadastro e a consulta de funcionários de uma empresa. Ele possui uma interface gráfica feita com JavaFX e permite organizar informações pessoais, dados de contratação e endereço em um só lugar.

Os dados são armazenados em um arquivo CSV, separado por ponto e vírgula (`;`). Assim, as informações continuam disponíveis quando o programa é fechado e aberto novamente.

O relatório com as decisões de design está no final deste arquivo, na seção [Relatório](#relatório--sistema-de-cadastro-de-funcionários).

## Funcionalidades

- Cadastrar funcionários com dados pessoais, profissionais e endereço.
- Listar os funcionários cadastrados em uma tabela.
- Consultar um funcionário pela matrícula.
- Excluir um funcionário cadastrado.
- Salvar e carregar os dados pelo arquivo `funcionarios.csv`.
- Gerar relatórios usando a Stream API do Java:
  - funcionários filtrados por cargo;
  - funcionários dentro de uma faixa salarial;
  - média salarial por cargo;
  - funcionários agrupados por cidade e estado.
- Validar informações como matrícula, CPF, data de nascimento, salário e CEP.

## Tecnologias utilizadas

- Java
- JavaFX 17, para a interface gráfica
- FXML, para estruturar a tela
- Maven, para gerenciar as dependências
- Stream API, para filtrar, agrupar e calcular informações
- Arquivo CSV, para guardar os dados

## Organização do código

O código está separado por responsabilidades:

- `model`: classes `Funcionario` e `Endereco`, que representam os dados do sistema.
- `controller`: `MainController`, responsável pelas ações da interface.
- `repository`: `FuncionarioRepository`, responsável por ler e salvar os funcionários no CSV.
- `service`: `RelatorioService`, responsável pelos relatórios com Stream API.
- `view`: arquivo FXML da interface.
- `utils`: utilitários usados pelo projeto.

## Como executar

1. Instale um JDK compatível com o projeto e o IntelliJ IDEA ou outra IDE com suporte a Maven.
2. Extraia o projeto e abra a pasta que contém o arquivo `pom.xml`.
3. Aguarde a IDE baixar e configurar as dependências do Maven.
4. Confira se o arquivo `main-view.fxml` está no caminho esperado pela classe `Main` e pela classe utilitária `PathFXML`.
5. Execute a classe `org.cadastrofuncionario.Launcher` pela IDE.

O projeto utiliza o JavaFX 17. Caso a execução apresente erro de JavaFX ou de caminho do FXML, confira a configuração de execução e os caminhos dos arquivos antes de rodar novamente.

## Arquivo de dados

O arquivo `funcionarios.csv` guarda os funcionários cadastrados. Como ele contém os dados utilizados pelo programa, evite apagar ou editar o arquivo enquanto o sistema estiver aberto.

---

# Relatório – Sistema de Cadastro de Funcionários

**Disciplina:** Programação Orientada a Objetos III · **Projeto:** P1

## 1. Sobre o projeto

Neste trabalho eu desenvolvi um sistema em Java com interface JavaFX para cadastrar e gerenciar os funcionários de uma empresa. O programa guarda os dados pessoais e o endereço de cada funcionário, salva tudo em um arquivo CSV e gera alguns relatórios usando a Stream API. A ideia foi seguir o que o enunciado pedia e, ao mesmo tempo, deixar o código organizado para eu conseguir entender e explicar cada parte.

## 2. O que o sistema faz

A tela principal tem três abas:

- **Cadastro:** um formulário com os dados do funcionário (matrícula, nome, CPF, cargo, salário e as datas de nascimento e contratação) e do endereço. Quando alguma informação está errada, o programa avisa na própria tela o que precisa ser corrigido.
- **Listagem:** mostra todos os funcionários em uma tabela. Dá para consultar um funcionário pela matrícula, listar todos de novo e excluir o que estiver selecionado. Antes de excluir, o sistema pede uma confirmação para evitar acidentes.
- **Relatórios:** filtra os funcionários por cargo, filtra por faixa salarial, calcula a média salarial de cada cargo e agrupa os funcionários por cidade.

Os dados ficam no arquivo `funcionarios.csv`, com os campos separados por ponto-e-vírgula. O programa lê esse arquivo quando abre e grava de novo a cada cadastro ou exclusão, então nada se perde ao fechar.

## 3. Como organizei o código

Eu dividi o projeto em partes, cada uma com uma função:

- `model`: as classes `Funcionario` e `Endereco`. O funcionário tem um endereço dentro dele (composição), como o enunciado pedia.
- `repository`: a classe `FuncionarioRepository`, responsável por guardar a lista em memória e ler/gravar o CSV.
- `service`: a classe `RelatorioService`, onde ficam os relatórios feitos com Stream.
- `controller` e `view`: o `MainController` e o arquivo FXML, que cuidam da tela e dos botões.

Essa separação ajudou bastante: a tela só chama os outros pedaços, e as regras do sistema não ficam misturadas com o visual.

## 4. Decisões que tomei

**Validação dentro das classes do modelo.** Em vez de validar só na tela, coloquei as regras nos setters, e os construtores usam os setters. Assim é impossível criar um funcionário inválido, venha o dado do formulário ou do arquivo CSV. Quando algo está errado, o setter lança uma `IllegalArgumentException` com uma mensagem clara, e o controller mostra essa mensagem ao usuário. As regras são: matrícula com 6 dígitos, nome com pelo menos 3 letras, CPF válido (com conferência dos dois dígitos verificadores), idade mínima de 18 anos, salário maior que zero e CEP no formato correto.

**Tipos de dados.** Usei `BigDecimal` para o salário, porque `double` pode dar erros de arredondamento com dinheiro, e `LocalDate` para as datas.

**CPF e CEP só com números.** O sistema aceita com ou sem pontuação, mas guarda apenas os dígitos. A formatação (pontos e traço) só aparece na hora de mostrar na tabela. Isso deixa o arquivo mais limpo e facilita comparações.

**Cuidado com o CSV.** Como o separador é o ponto-e-vírgula, não deixei os campos de texto aceitarem esse caractere, senão uma linha poderia "quebrar" em colunas erradas. Se alguma linha do arquivo estiver inválida, o programa a ignora e registra um aviso, em vez de travar na abertura.

**Memória e arquivo sempre iguais.** Se der erro ao gravar no arquivo, o repositório desfaz a alteração que tinha feito na lista. Assim o que aparece na tela nunca fica diferente do que está salvo. A matrícula é o identificador único, então o sistema não deixa cadastrar duas pessoas com a mesma matrícula.

**Stream API nos relatórios.** Usei `filter` para os filtros de cargo e de faixa salarial. Na média por cargo, agrupo os funcionários com `groupingBy`, pego só os salários com `map`, somo com `reduce` e divido pela quantidade de pessoas do grupo. O agrupamento por cidade também usa `groupingBy`.

## 5. Limitações

O sistema permite cadastrar, consultar, listar e excluir, que são as operações pedidas no enunciado. Não implementei a edição de um funcionário já cadastrado. Além disso, o arquivo CSV é criado na pasta de onde o programa é executado.

## 6. O que aprendi

O que mais me ajudou foi perceber como separar responsabilidades deixa o projeto mais fácil de testar e de mudar. Também aprendi a usar a Stream API de um jeito prático, com agrupamentos e somas que, sem ela, dariam bem mais código.
