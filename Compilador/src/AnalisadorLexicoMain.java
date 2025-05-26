// Ficheiro: Compilador.java (versão com todas as classes)

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// CLASSE PRINCIPAL PÚBLICA (Ponto de entrada do programa)
public class AnalisadorLexicoMain {

    public static void main(String[] args) {
        String caminhoArquivo = "Programa_Fonte.txt"; // Certifique-se de que este arquivo existe!
        try {
            Path path = Paths.get(caminhoArquivo);
            String codigoFonte = new String(Files.readAllBytes(path));

            System.out.println("--- Iniciando Análise do Ficheiro: " + caminhoArquivo + " ---\n");
            System.out.println("--- Código Fonte Original ---");
            System.out.println(codigoFonte);
            System.out.println("-".repeat(30));

            AnalisadorLexico analisador = new AnalisadorLexico();
            analisador.analisar(codigoFonte);

            List<String> erros = analisador.getErros();
            if (!erros.isEmpty()) {
                System.out.println("\n🚨 Relatório de Erros Encontrados:\n");
                for (String erro : erros) {
                    System.out.println("- " + erro);
                }
                System.out.println("\n❌ Análise falhou. Por favor, corrija os erros e tente novamente.");
            } else {
                System.out.println("\n✅ Análise concluída com sucesso! Nenhum erro encontrado.\n");
                System.out.println(analisador.getTabelaDeSimbolos());
            }

        } catch (Exception e) {
            System.out.println("Erro ao ler o ficheiro '" + caminhoArquivo + "': " + e.getMessage());
            System.out.println("Por favor, certifique-se de que o arquivo existe no mesmo diretório que o .jar.");
        }
    }
}

// DEMAIS CLASSES (sem o modificador 'public')

// Enumeração para os tipos de token
enum TipoToken {
    // Palavras-chave
    PROGRAM, VAR, INTEGER, BEGIN, END, WRITELN,

    // Símbolos e Operadores
    ID,          // Identificador (variáveis)
    NUMERO,      // Número inteiro
    ATRIBUICAO,  // :=
    OPERADOR,    // +
    SEPARADOR,   // , : ; ( )
    PONTO_FINAL, // .

    // Erros
    ERRO_ATRIBUICAO, // =
    DESCONHECIDO    // Token não reconhecido
}

// Classe para representar um Token
class Token {
    public final TipoToken tipo;
    public final String valor;
    public final int linha;

    public Token(TipoToken tipo, String valor, int linha) {
        this.tipo = tipo;
        this.valor = valor;
        this.linha = linha;
    }

    @Override
    public String toString() {
        return String.format("Token[Tipo=%s, Valor='%s', Linha=%d]", tipo, valor, linha);
    }
}

// Classe para a Tabela de Símbolos
class TabelaDeSimbolos {
    private final Map<String, String> simbolos = new HashMap<>();

    public void adicionar(String nome, String tipo) {
        simbolos.put(nome, tipo);
    }

    public boolean existe(String nome) {
        return simbolos.containsKey(nome);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Tabela de Símbolos ---\n");
        sb.append(String.format("%-15s | %-15s\n", "ID", "Tipo"));
        sb.append("-".repeat(33)).append("\n");
        for (Map.Entry<String, String> entry : simbolos.entrySet()) {
            sb.append(String.format("%-15s | %-15s\n", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }
}

// Classe para o Analisador Léxico e Semântico
class AnalisadorLexico {

    private final TabelaDeSimbolos tabelaDeSimbolos = new TabelaDeSimbolos();
    private final List<String> erros = new ArrayList<>();

    public void analisar(String codigoFonte) {
        List<Token> tokens = tokenizar(codigoFonte);

        if (!erros.isEmpty()) {
            return;
        }

        analiseSemantica(tokens);
    }

    public List<String> getErros() {
        return erros;
    }

    public TabelaDeSimbolos getTabelaDeSimbolos() {
        return tabelaDeSimbolos;
    }

    private List<Token> tokenizar(String codigoFonte) {
        List<Token> tokens = new ArrayList<>();
        String[] linhas = codigoFonte.split("\n");

        String padraoToken =
                "(\\b(program|var|integer|begin|end|writeln)\\b)|" + // Grupo 1 e 2: Palavra-chave
                        "(\\b[a-zA-Z][a-zA-Z0-9]*\\b)|" +                    // Grupo 3: ID
                        "(\\b\\d+\\b)|" +                                     // Grupo 4: NUMERO
                        "(:=)|" +                                             // Grupo 5: ATRIBUICAO
                        "(=)|" +                                              // Grupo 6: ERRO_ATRIBUICAO
                        "([+\\-*/])|" +                                       // Grupo 7: OPERADOR
                        "([,:;()])|" +                                        // Grupo 8: SEPARADOR
                        "(\\.)|" +                                            // Grupo 9: PONTO_FINAL
                        "(\\s+)|" +                                           // Grupo 10: ESPACO
                        "(.)";                                                // Grupo 11: DESCONHECIDO

        Pattern pattern = Pattern.compile(padraoToken);

        for (int i = 0; i < linhas.length; i++) {
            Matcher matcher = pattern.matcher(linhas[i]);
            while (matcher.find()) {
                String valor = matcher.group();
                int linhaNum = i + 1;

                if (matcher.group(10) != null) continue;

                if (matcher.group(1) != null) {
                    tokens.add(new Token(TipoToken.valueOf(valor.toUpperCase()), valor, linhaNum));
                } else if (matcher.group(3) != null) {
                    if (valor.equals("progra") || valor.equals("intege") || valor.equals("begi")) {
                        erros.add(String.format("Erro Léxico na Linha %d: Palavra-chave '%s' inválida. Verifique a ortografia.", linhaNum, valor));
                    }
                    tokens.add(new Token(TipoToken.ID, valor, linhaNum));
                } else if (matcher.group(4) != null) {
                    tokens.add(new Token(TipoToken.NUMERO, valor, linhaNum));
                } else if (matcher.group(5) != null) {
                    tokens.add(new Token(TipoToken.ATRIBUICAO, valor, linhaNum));
                } else if (matcher.group(6) != null) {
                    erros.add(String.format("Erro de Atribuição na Linha %d: Operador de atribuição inválido '='. Use ':='.", linhaNum));
                } else if (matcher.group(7) != null) {
                    tokens.add(new Token(TipoToken.OPERADOR, valor, linhaNum));
                } else if (matcher.group(8) != null) {
                    tokens.add(new Token(TipoToken.SEPARADOR, valor, linhaNum));
                } else if (matcher.group(9) != null) {
                    tokens.add(new Token(TipoToken.PONTO_FINAL, valor, linhaNum));
                } else {
                    erros.add(String.format("Erro Léxico na Linha %d: Caractere desconhecido '%s'.", linhaNum, valor));
                }
            }
        }
        return tokens;
    }

    private void analiseSemantica(List<Token> tokens) {
        boolean emDeclaracao = false;
        List<String> varsParaDeclarar = new ArrayList<>();

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (token.tipo == TipoToken.VAR) {
                emDeclaracao = true;
                continue;
            }
            if (emDeclaracao && token.tipo == TipoToken.ID) {
                varsParaDeclarar.add(token.valor);
            }
            if (emDeclaracao && token.valor.equals(":")) {
                if (i + 1 < tokens.size()) {
                    Token tipoToken = tokens.get(i + 1);
                    String tipo = tipoToken.valor;
                    for (String var : varsParaDeclarar) {
                        tabelaDeSimbolos.adicionar(var, tipo);
                    }
                    varsParaDeclarar.clear();
                }
            }
            if (token.tipo == TipoToken.BEGIN) {
                emDeclaracao = false;
            }
        }

        boolean dentroDoBlocoBeginEnd = false;
        for (Token token : tokens) {
            if (token.tipo == TipoToken.BEGIN) {
                dentroDoBlocoBeginEnd = true;
                continue;
            }
            if (token.tipo == TipoToken.END) {
                dentroDoBlocoBeginEnd = false;
                continue;
            }
            if (dentroDoBlocoBeginEnd && token.tipo == TipoToken.ID) {
                if (token.valor.equals("writeln")) continue;
                if (!tabelaDeSimbolos.existe(token.valor)) {
                    erros.add(String.format("Erro Semântico na Linha %d: O identificador '%s' foi usado mas não foi declarado.", token.linha, token.valor));
                }
            }
        }
    }
}