# Algoritmo do Banqueiro

Este projeto implementa o Algoritmo do Banqueiro em Java, como descrito na Seção 7.5.3 do livro "Fundamentos de Sistemas Operacionais" de Abraham Silberschatz et al. O programa simula um sistema multithreaded onde múltiplos clientes solicitam e liberam recursos controlados por um "banqueiro" que garante que o sistema permaneça em um estado seguro, evitando deadlocks.

## Funcionalidades Principais
- Controle de Recursos: Gerencia 3 tipos de recursos com quantidades iniciais fornecidas via linha de comando.
- Clientes Multithreaded: 5 threads de clientes que continuamente solicitam ou liberam recursos de forma aleatória.
- Algoritmo de Segurança: Verifica se uma solicitação deixa o sistema em um estado seguro antes de aprová-la.
- Sincronização: Usa blocos synchronized para prevenir condições de corrida.
- Logs de Operações: Exibe no console as solicitações e liberações com status de sucesso ou falha.

## Como Compilar e Executar
### Pré-requisitos
- Java JDK 17 ou superior instalado.
- Sistema operacional Windows (ou compatível com comandos javac/java).

### Passos
1. Navegue até o diretório do projeto: `cd trabalho`
2. Compile o código: `javac -d target/classes src/main/java/so/br/Main.java`
3. Execute o programa com os recursos iniciais: `java -cp target/classes so.br.Main <recurso1> <recurso2> <recurso3>`
   - Exemplo: `java -cp target/classes so.br.Main 10 5 7`

O programa executará indefinidamente, simulando as operações dos clientes. Para interromper, use Ctrl+C.

## Estrutura do Projeto
- `src/main/java/so/br/Main.java`: Código principal.
- `pom.xml`: Arquivo Maven (opcional, para projetos Maven).
- `target/classes/`: Diretório de compilação.

## Algoritmo do Banqueiro
### Solicitação de Recursos
1. Verifica se a solicitação <= necessidade do cliente.
2. Verifica se a solicitação <= recursos disponíveis.
3. Aloca temporariamente os recursos.
4. Executa o algoritmo de segurança.
5. Se seguro, confirma a alocação; caso contrário, reverte.

### Liberação de Recursos
1. Verifica se a liberação <= alocação atual do cliente.
2. Libera os recursos de volta para disponíveis.

### Algoritmo de Segurança
- Inicializa work = available e finish = false para todos.
- Enquanto houver clientes não finalizados:
  - Encontra um cliente não finalizado onde need <= work.
  - Adiciona allocation do cliente a work e marca como finalizado.
- Se todos forem finalizados, o estado é seguro.

## Valores de Configuração
- Número de Clientes: 5
- Número de Recursos: 3
- Demandas Máximas:
  - Cliente 0: [7, 5, 3]
  - Cliente 1: [3, 2, 2]
  - Cliente 2: [9, 0, 2]
  - Cliente 3: [2, 2, 2]
  - Cliente 4: [4, 3, 3]

## Referências
- SILBERSCHATZ, Abraham; GALVIN, Peter B.; GAGNE, Greg. Fundamentos de sistemas operacionais. 9. ed. Rio de Janeiro, RJ: LTC, c2015.</content>
<parameter name="filePath">c:\Users\lucam\OneDrive\Área de Trabalho\Traballho-de-Sistemas-Operacionais\trabalho\README.md