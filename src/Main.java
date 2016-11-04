import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {
        //Inicia uma lista de abilities que conterá todas as abilities
        List<Abilitie> laGeral = new ArrayList<>();

        //Inicia a Lista de Combos
        List<Combo> listaCombos = new ArrayList<>();

        //Inicia a lista de abilities para fins de referencia
        List<String> lsGeral = new ArrayList<>();

        //inicia a lista de cartas contendo somente seus nomes
        List<String> nomesCartas = new ArrayList<>();

        //#################################################################################
        //Pega os respectivos Jsons de combos, e de cartas
        URI uri = new URI("http://andersoncarvalho.com/shirley/formatedCombos.json");
        JSONTokener tokener = new JSONTokener(uri.toURL().openStream());
        JSONObject todosCombos = new JSONObject(tokener);

        uri = new URI("http://andersoncarvalho.com/shirley/document-2.json");
        tokener = new JSONTokener(uri.toURL().openStream());
        JSONObject todasCartas = new JSONObject(tokener);
        //#################################################################################

        //Percorre o vetor de cartas e adiciona todas as abilities em uma lista gigante
        for(int i = 1; i < 568; i++){
            JSONObject carta = todasCartas.getJSONObject("" + i);
            JSONArray abilities = carta.getJSONArray("abilities");
            nomesCartas.add(carta.getString("name"));
            for(int j = 0; j < abilities.length(); j++){
                JSONObject abilitie = abilities.getJSONObject(j);
                String nomeAbilitie = abilitie.getString("ability");
                if(!lsGeral.contains(nomeAbilitie)) {
                    lsGeral.add(nomeAbilitie);
                    Abilitie a = new Abilitie();
                    a.setAbility(nomeAbilitie);
                    a.setnOcorrencias(0);
                    laGeral.add(a);
                }
            }
        }

        //pega um combo e verifica suas respectivas cartas
        for(int i = 1 ; i< 55; i++){
            List<Abilitie> laAtual = new ArrayList<>(laGeral);
            JSONObject combo = todosCombos.getJSONObject("" + i);
            JSONArray cartasDoCombo = combo.getJSONArray("cardsNames");
            for(int j = 0 ; j < cartasDoCombo.length();j++){
                if(nomesCartas.contains(cartasDoCombo.get(j).toString())){
                    int index = nomesCartas.indexOf(cartasDoCombo.get(j).toString()) + 1;
                    JSONObject carta = todasCartas.getJSONObject("" + index);
                    JSONArray abilities = carta.getJSONArray("abilities");
                    //pega as cartas e verifica suas abilitites
                    for(int k = 0; k < abilities.length(); k++){
                        JSONObject abilitie = abilities.getJSONObject(k);
                        String nomeAbilitie = abilitie.getString("ability");
                        //incrementa o contador de abilities dependendo de cada carta
                        if(lsGeral.contains(nomeAbilitie)) {
                            int numOcorrAtual = laAtual.get(lsGeral.indexOf(nomeAbilitie)).getnOcorrencias() + 1;
                            Abilitie bla = new Abilitie();
                            bla.setnOcorrencias(numOcorrAtual);
                            bla.setAbility(nomeAbilitie);
                            laAtual.remove(lsGeral.indexOf(nomeAbilitie));
                            laAtual.add(lsGeral.indexOf(nomeAbilitie),bla);
                        }
                    }
                }
            }
            //Combo iniciado e inserido na lista
            Combo bla = new Combo();
            bla.setLa(laAtual);
            listaCombos.add(bla);
        }


        //for somente para fins de impressao da lista de combos e seus respectivios abilities.
        for(int i = 0 ; i < listaCombos.size();i ++){
            System.out.printf("Combo " + (i + 1) + " ");
            for (int j = 0; j < listaCombos.get(i).getLa().size(); j++){
                if(listaCombos.get(i).getLa().get(j).getnOcorrencias() != 0) {
                    if(listaCombos.get(i).getLa().get(j).getnOcorrencias() != 0) {
                        System.out.printf(listaCombos.get(i).getLa().get(j).getAbility() + ":" + listaCombos.get(i).getLa().get(j).getnOcorrencias() + "|");
                    }
                }
            }
            System.out.println("");
        }
    }
}
