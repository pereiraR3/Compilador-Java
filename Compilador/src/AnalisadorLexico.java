// Ficheiro: AnalisadorLexico.java (VERSÃO CORRIGIDA)
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalisadorLexico {

    private final TabelaDeSimbolos tabelaDeSimbolos = new TabelaDeSimbolos();
    private final List<String> erros = new ArrayList<>();

    public void analisar(String codigoFonte) {
        List<Token> tokens = tokenizar(codigoFonte);

        // Se houver erros léxicos, para a execução e reporta-os
        if (!erros.isEmpty()) {
            return;
        }

        // Se a tokenização foi bem-sucedida, faz a análise semântica
        analiseSemantica(tokens);
    }

    public List<String> getErros() {
        return erros;
    }

    public TabelaDeSimbolos getTabelaDeSimbolos() {
        return tabelaDeSimbolos;
    }

    // 1ª Etapa: Transformar o código em uma lista de tokens
    private List<Token> tokenizar(String codigoFonte) {
        List<Token> tokens = new ArrayList<>();
        String[] linhas = codigoFonte.split("\n");

        // Regex para identificar todos os tokens de uma vez (SEM GRUPOS NOMEADOS)
        // Adicionei comentários para sabermos o número de cada grupo.
        String padraoToken =
                "(\\b(program|var|integer|begin|end|writeln)\\b)|" + // Grupo 1: PALAVRA_CHAVE (e Grupo 2 é o conteúdo)
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

                // Verificação por número do grupo em vez do nome
                if (matcher.group(10) != null) continue; // Grupo 10: ESPACO

                if (matcher.group(1) != null) { // Grupo 1: PALAVRA_CHAVE
                    tokens.add(new Token(TipoToken.valueOf(valor.toUpperCase()), valor, linhaNum));
                } else if (matcher.group(3) != null) { // Grupo 3: ID
                    // Verificação de palavras-chave mal escritas
                    if (valor.equals("progra") || valor.equals("intege") || valor.equals("begi")) {
                        erros.add(String.format("Erro Léxico na Linha %d: Palavra-chave '%s' inválida. Verifique a ortografia.", linhaNum, valor));
                    }
                    tokens.add(new Token(TipoToken.ID, valor, linhaNum));
                } else if (matcher.group(4) != null) { // Grupo 4: NUMERO
                    tokens.add(new Token(TipoToken.NUMERO, valor, linhaNum));
                } else if (matcher.group(5) != null) { // Grupo 5: ATRIBUICAO
                    tokens.add(new Token(TipoToken.ATRIBUICAO, valor, linhaNum));
                } else if (matcher.group(6) != null) { // Grupo 6: ERRO_ATRIBUICAO
                    erros.add(String.format("Erro de Atribuição na Linha %d: Operador de atribuição inválido '='. Use ':='.", linhaNum));
                } else if (matcher.group(7) != null) { // Grupo 7: OPERADOR
                    tokens.add(new Token(TipoToken.OPERADOR, valor, linhaNum));
                } else if (matcher.group(8) != null) { // Grupo 8: SEPARADOR
                    tokens.add(new Token(TipoToken.SEPARADOR, valor, linhaNum));
                } else if (matcher.group(9) != null) { // Grupo 9: PONTO_FINAL
                    tokens.add(new Token(TipoToken.PONTO_FINAL, valor, linhaNum));
                } else { // Grupo 11: DESCONHECIDO
                    erros.add(String.format("Erro Léxico na Linha %d: Caractere desconhecido '%s'.", linhaNum, valor));
                }
            }
        }
        return tokens;
    }

    // 2ª Etapa: Analisar a lista de tokens para regras semânticas
    private void analiseSemantica(List<Token> tokens) {
        boolean emDeclaracao = false;
        List<String> varsParaDeclarar = new ArrayList<>();

        // Passa pelos tokens para preencher a tabela de símbolos
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
                // Se o próximo token for o tipo, adiciona as variáveis à tabela
                if (i + 1 < tokens.size()) {
                    Token tipoToken = tokens.get(i + 1);
                    String tipo = tipoToken.valor;
                    for (String var : varsParaDeclarar) {
                        tabelaDeSimbolos.adicionar(var, tipo);
                    }
                    varsParaDeclarar.clear();
                    // Não definimos emDeclaracao como false para permitir múltiplas declarações
                    // Ex: var x:integer; y:integer;
                }
            }

            // Sai do modo de declaração ao encontrar o bloco begin
            if (token.tipo == TipoToken.BEGIN) {
                emDeclaracao = false;
            }
        }

        // Passa novamente para verificar o uso de variáveis não declaradas
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
                if (token.valor.equals("writeln")) continue; // Ignora a função writeln
                if (!tabelaDeSimbolos.existe(token.valor)) {
                    erros.add(String.format("Erro Semântico na Linha %d: O identificador '%s' foi usado mas não foi declarado.", token.linha, token.valor));
                }
            }
        }
    }
}