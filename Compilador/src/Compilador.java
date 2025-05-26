// Ficheiro: Compilador.java
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Compilador {

    public static void main(String[] args) {
        String caminhoArquivo = "Programa_Fonte.txt";
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
            System.out.println("Erro ao ler o ficheiro: " + e.getMessage());
        }
    }
}