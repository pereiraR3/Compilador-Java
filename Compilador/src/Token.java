// Ficheiro: Token.java
public class Token {
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