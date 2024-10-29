package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.DatosEpisodio;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporadas;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class PrincipalAnterior {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=839d3133";
    private ConvierteDatos conversor = new ConvierteDatos();

    public void showMenu(){
        System.out.println("Por favor escriba el nombre de la serie que desea buscar");
        //Busca los datos generales de las Series
        var nombreSerie = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + nombreSerie.replace(" ","+")+API_KEY);
        var datos = conversor.obtenerDatos(json, DatosSerie.class);
        System.out.println(datos);

        //Busca los datos de todas las temporadas
        List<DatosTemporadas> temporadas = new ArrayList<>();
        for (int i = 1; i < datos.totalDeTemporadas() ; i++) {
            json = consumoAPI.obtenerDatos(URL_BASE + nombreSerie.replace(" ","+")+"&season="+i+API_KEY);
            var datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
            temporadas.add(datosTemporada);
        }
        //temporadas.forEach(System.out::println);
        //Mostrar solo el titulo de los episodios para las temporadas
        //for (int i = 0; i < datos.totalDeTemporadas(); i++) {
        //    List<DatosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
        //    for (int j = 0; j < episodiosTemporada.size(); j++) {
        //        System.out.println(episodiosTemporada.get(j).titulo());
        //    }
        //}
        //temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo() +
        //        "\n*****************************")));

        //Convertir todas las informaciones a una lista del tipo datosEpisodio
        List<DatosEpisodio> datosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());
        //top 5 episodios
        //System.out.println("**************************" +
        //        "\nTop 5 mejores episodios");
        //datosEpisodios.stream()
        //        .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
        //        .peek(e -> System.out.println("Primer filtro (N/A)" + e))
        //        .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
        //        .peek(e -> System.out.println("Segundo filtro ordenacion(M>m)" + e))
        //        .map(e -> e.titulo().toUpperCase())
        //        .peek(e -> System.out.println("Tercer filtro Mayusculas(m>M)" + e+
        //                "\n**********************"))
        //        .limit(5)
        //        .forEach(System.out::println);

        //Convirtiendo los datos a lista de tipo Episodio
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(),d)))
                .collect(Collectors.toList());
        //episodios.forEach(System.out::println);

        //Busqueda de episodios apartir de x año
        //System.out.println("Por favor indique el año apartir del cual desea ver los episodios");
        //var fecha = teclado.nextInt();
        //teclado.nextLine();
        //LocalDate fechaBusqueda =  LocalDate.of(fecha,1,1);
        //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        //episodios.stream()
        //        .filter(e -> e.getFechadDeLanzamiento() != null && e.getFechadDeLanzamiento().isAfter(fechaBusqueda))
        //        .forEach(e -> System.out.println(
        //                "Temporada " + e.getTemporada() +
        //                        "Episodio " + e.getTitulo() +
        //                        "Fecha de Lanzamiento " + e.getFechadDeLanzamiento().format(dtf)
        //        ));

        //Busca episodios por pedazos de Titulo
        //System.out.println("Por favor escriba el titulo del episodio que desea ver:");
        //var pedazoTitulo = teclado.nextLine();
        //Optional<Episodio> episodioBuscado = episodios.stream()
        //        .filter(e -> e.getTitulo().toUpperCase().contains(pedazoTitulo.toUpperCase()))
        //        .findFirst();
        //if(episodioBuscado.isPresent()){
        //    System.out.println("Episodio encontrado\nLos datos son: " + episodioBuscado.get());
        //} else {
        //    System.out.println("Episodio no encontrado");
        //}

        Map<Integer, Double> evaluacionesPorTemporada = episodios.stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getEvaluacion)));
        System.out.println(evaluacionesPorTemporada);
        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
        System.out.println("Media de las evaluaciones: " + est.getAverage()+
                "\nEpisodio mejor evaluado: " + est.getMax() +
                "\nEpisodio peor evaluado: " + est.getMin());

    }
}
