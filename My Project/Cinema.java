import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Cinema {
    Sala sala = new Sala();
    //int capacitate;
    ArrayList clientiCinema = new ArrayList();
   // String rezervareClient;
    //CapacitateCinema capac = new CapacitateCinema();

    List<String> filme = List.of("Avengers" , "Snowpiercer" , "Avatar" , "Batman" , "Jumanji");


    public void afisareFilme(){
        for(String e : filme){
            System.out.println(e);
        }
    }

    public void rezervare(String film, int nrSala, String numeRezervare){
        if(filme.contains(film)){
            if(clientiCinema.size() <= 19){
                int x = sala.salaCinematograf[nrSala];
                String numeRezervare1 = numeRezervare;
                LocalDateTime now = LocalDateTime.now();

            }else{
                System.out.println("Sala este plina");
            }

            int i = sala.salaCinematograf[nrSala];

        }else{
            System.out.println("Filmul nu exista, in lista.\n" +
                    "Te rog sa selectezi un film din lista de mai sus.");
        }
    }



    public void rezervareAll(String film, int nrSala, String numeRezervare, LocalDateTime localDateTime){
        if(filme.contains(film)){
            System.out.println("filmul exista");
        }else{
            System.out.println("Filmul nu exista, in lista.\n" +
                    "Te rog sa selectezi un film din lista de mai sus.");
        }
    }


}
