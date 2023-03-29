import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;

public class App {

    public static void main(String[] args) throws Exception {

        // fazer uma conexão HTTP e buscar os top 250 filmes
        //String url = "https://imdb-api.com/en/API/Top250Movies/k_0ojt0yvm";
        String url = "https://raw.githubusercontent.com/alura-cursos/imersao-java-2-api/main/TopMovies.json";
        URI endereco = URI.create(url);
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(endereco).GET().build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        String body = response.body();

        // extrair só os dados que interessam (titulo, poster, classificação)
        var parser = new JsonParser();
        List<Map<String, String>> listaDeFilmes = parser.parse(body);


        var diretorio = new File("figurinhas/");
        diretorio.mkdir();
        // exibir e manipular os dados 
        var geradora = new GeradoraDeFigurinhas();
        for (Map<String,String> filme : listaDeFilmes) {

            String urlImagem = filme.get("image");
            String urlImagemMaior = urlImagem.replaceFirst("(@?//.)([0-9A-Z,_]+).jpg$", "$1.jpg");
            String titulo = filme.get("title");
            double classificacao = Double.parseDouble(filme.get("imDbRating"));
             
            String textoFigurinha;
            InputStream imagemMarina;
            if(classificacao >= 9.0) {
                textoFigurinha = "ÓTIMO"; 
                imagemMarina = new FileInputStream(new File("sobreposicao/otimo.jpg"));               
            }else if (classificacao >= 6.0 && classificacao < 9.0){
                textoFigurinha = "MÉDIO";
                imagemMarina = new FileInputStream(new File("sobreposicao/medio.jpg"));
        }else {
                textoFigurinha = "RUIM";
                imagemMarina = new FileInputStream(new File("sobreposicao/ruim.jpg"));
            }
            


            InputStream inputStream = new URL(urlImagemMaior).openStream();
            String nomeArquivo = "figurinhas/" + titulo + ".png";

            geradora.cria(inputStream, nomeArquivo, textoFigurinha, imagemMarina);

            System.out.println(titulo);
            System.out.println(classificacao);
            System.out.println();
        }
    }
}