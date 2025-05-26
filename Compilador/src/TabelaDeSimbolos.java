// Ficheiro: TabelaDeSimbolos.java
import java.util.HashMap;
import java.util.Map;

public class TabelaDeSimbolos {
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