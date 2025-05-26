// Ficheiro: TipoToken.java
public enum TipoToken {
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