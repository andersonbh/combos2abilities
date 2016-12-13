import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {
        //Inicia uma lista de abilities que conterá todas as abilities
        List<Ability> laGeral = new ArrayList<>();

        //Inicia a Lista de Combos
        List<Combo> listaCombos = new ArrayList<>();

        //Inicia a lista de abilities para fins de referencia
        List<String> lsGeral = new ArrayList<>();

        //inicia a lista de cartas contendo somente seus nomes
        List<String> nomesCartas = new ArrayList<>();

        //Lista de médias

        String[][] vetMedias = new String[3][54];

        /*
        Ler arquivos de médias
         */
        try {
            FileReader arq = new FileReader("medias.txt");
            BufferedReader lerArq = new BufferedReader(arq);

            String linha = lerArq.readLine(); // lê a primeira linha
            int i = 0;
            while (linha != null) {
                //   Faz um split por tab
                String array[] = linha.split("\t");
                vetMedias[i] = array;
                i++;
                linha = lerArq.readLine(); // lê da segunda até a última linha
            }

            arq.close();
        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n",
                    e.getMessage());
        }


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
        for (int i = 1; i < 568; i++) {
            JSONObject carta = todasCartas.getJSONObject("" + i);
            JSONArray abilities = carta.getJSONArray("abilities");
            nomesCartas.add(carta.getString("name"));
            for (int j = 0; j < abilities.length(); j++) {
                JSONObject ability = abilities.getJSONObject(j);
                String nomeAbility = ability.getString("ability");
                if (!lsGeral.contains(nomeAbility)) {
                    lsGeral.add(nomeAbility);
                    Ability a = new Ability();
                    a.setAbility(nomeAbility);
                    a.setnOcorrencias(0);
                    laGeral.add(a);
                }
            }
        }

        //pega um combo e verifica suas respectivas cartas
        for (int i = 1; i < 55; i++) {
            List<Ability> laAtual = new ArrayList<>(laGeral);
            JSONObject combo = todosCombos.getJSONObject("" + i);
            JSONArray cartasDoCombo = combo.getJSONArray("cardsNames");
            for (int j = 0; j < cartasDoCombo.length(); j++) {
                if (nomesCartas.contains(cartasDoCombo.get(j).toString())) {
                    int index = nomesCartas.indexOf(cartasDoCombo.get(j).toString()) + 1;
                    JSONObject carta = todasCartas.getJSONObject("" + index);
                    JSONArray abilities = carta.getJSONArray("abilities");
                    List<String> abilitiesAtual = new ArrayList<>();
                    //pega as cartas e verifica suas abilitites
                    for (int k = 0; k < abilities.length(); k++) {
                        JSONObject ability = abilities.getJSONObject(k);
                        String nomeAbility = ability.getString("ability");

                        //Para contar o numero de habilidades de forma unica por carta, descomentar if abaixo
            //          if(!abilitiesAtual.contains(nomeAbility)){
            //               abilitiesAtual.add(nomeAbility);
                            //incrementa o contador de abilities dependendo de cada carta
                            if (lsGeral.contains(nomeAbility)) {
                                int numOcorrAtual = laAtual.get(lsGeral.indexOf(nomeAbility)).getnOcorrencias() + 1;
                                Ability bla = new Ability();
                                bla.setnOcorrencias(numOcorrAtual);
                                bla.setAbility(nomeAbility);
                                laAtual.remove(lsGeral.indexOf(nomeAbility));
                                laAtual.add(lsGeral.indexOf(nomeAbility), bla);
                            }
            //          }
                    }
                }
            }
            //Combo iniciado e inserido na lista
            Combo bla = new Combo();
            bla.setLa(laAtual);
            listaCombos.add(bla);
        }

        char separador = ';';
        //for para geração do arquivo csv.
        PrintWriter pw = new PrintWriter(new File("result.csv"));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lsGeral.size(); i++) {
            sb.append(lsGeral.get(i));
            sb.append(separador);
        }
        sb.append("media_novidade");
        sb.append(separador);
        sb.append("media_valor");
        sb.append(separador);
        sb.append("media_rdc\n");

        for (int i = 0; i < listaCombos.size(); i++) {
            for (int j = 0; j < listaCombos.get(i).getLa().size(); j++) {
                sb.append(listaCombos.get(i).getLa().get(j).getnOcorrencias());
                sb.append(separador);
            }
            sb.append(vetMedias[0][i]);
            sb.append(separador);
            sb.append(vetMedias[1][i]);
            sb.append(separador);
            sb.append(vetMedias[2][i]);
            sb.append("\n");
        }

        pw.write(sb.toString());
        pw.close();
    }
}
