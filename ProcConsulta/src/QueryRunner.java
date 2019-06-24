
import indice.estrutura.Indice;
import indice.estrutura.IndiceLight;
import query_eval.IndicePreCompModelo;
import indice.estrutura.Ocorrencia;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import org.w3c.dom.css.Counter;
import query_eval.BM25RankingModel;
import query_eval.BooleanRankingModel;
import query_eval.BooleanRankingModel.OPERATOR;
import query_eval.RankingModel;
import query_eval.VectorRankingModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document; 
import org.jsoup.nodes.Element; 
import org.jsoup.select.Elements;


public class  QueryRunner {

	public enum OPERACAO{
		AND,OR;
	}
	private static RankingModel queryRankingModel;
	private static Indice idx;
	
	public QueryRunner(Indice idx,RankingModel m)
	{
		this.queryRankingModel = m;
		this.idx = idx;
	}

	/**
	* Adiciona a lista de documentos relevantes para um determinada query (os documentos relevantes foram
	* fornecidos no ".dat" correspondente. Por ex, Belo Horizonte.dat possui os documentos relevantes da consulta "Belo Horizonte"
	* Exemplo de saida:
	* "Sao paulo"=>[1,3,5,6,233],
        *  "Belo Horizonte"=>[2,4,5,3]
	**/
	public static HashMap<String,Set<Integer>> getRelevancePerQuery() throws Exception {
            
            File file1 = new File("Belo_Horizonte.dat");
            File file2 = new File("Irlanda.dat");
            File file3 = new File("Sao_Paulo.dat");
            Set<Integer> Docs = new HashSet<Integer>();
            String[] Docs_str;  
            HashMap<String, Set<Integer>> docCidadesRelevantes = new HashMap<String, Set<Integer>>();
            
            BufferedReader br = new BufferedReader(new FileReader(file1));
             
            String st;
            if ((st = br.readLine()) != null){
                Docs_str = st.split(",");
                for(String doc : Docs_str){
                    Docs.add(Integer.parseInt(doc));
                }
            }
            
            docCidadesRelevantes.put("Belo Horizonte",Docs);
            Docs.clear();
            
            br = new BufferedReader(new FileReader(file2));
             
            if ((st = br.readLine()) != null){
                Docs_str = st.split(",");
                for(String doc : Docs_str){
                    Docs.add(Integer.parseInt(doc));
                }
            }
            
            docCidadesRelevantes.put("Irlanda",Docs);
            Docs.clear();
            
            br = new BufferedReader(new FileReader(file3));
             
            if ((st = br.readLine()) != null){
                Docs_str = st.split(",");
                for(String doc : Docs_str){
                    Docs.add(Integer.parseInt(doc));
                }
            }
            
            docCidadesRelevantes.put("Sao Paulo",Docs);
            Docs.clear();
            
            return docCidadesRelevantes;
	}
	/**
	* Calcula a quantidade de documentos relevantes na top n posições da lista lstResposta que é a resposta a uma consulta
 	* Considere que lstResposta já é a lista de respostas ordenadas por um método de processamento de consulta (BM25, Modelo vetorial).
	* Os documentos relevantes estão no parametro docRelevantes
	*/
	public static int countTopNRelevants(int n, List<Integer> lstRespostas, Set<Integer> docRelevantes){
            int count = 0;            
            for(int i=0; i < n; i++){
                for(Integer doc : docRelevantes){
                    if(doc.equals(lstRespostas.get(i))){
                        count++;
                    }
                }
            }            
            return count;
	}
	/**
	Preprocesse a consulta da mesma forma que foi preprocessado o texto do documento.
	E transforme a consulta em um Map<String,Ocorrencia> onde a String é o termo que ocorreu
	e Ocorencia define quantas vezes esse termo ocorreu na consulta. Coloque o docId como -1.
	DICA: tente reaproveitar metodos do indexador para isso. Além disso, se você considerar a consulta como um documento, é possivel fazer
	algo parecido com o que foi feito no metodo index do Indexador.
	*/
	public static Map<String,Ocorrencia> getOcorrenciaTermoConsulta(String consulta){
            Map<String,Ocorrencia> termosNaConsulta = new HashMap<String, Ocorrencia>() {};
            Set<String> termos = idx.getListTermos();
            
            String[] consultaSplitted;
            //quebra string consulta nos espaços
            consultaSplitted = consulta.split(" ");
            Ocorrencia occur;
            int freq;
            
            //percorre cada termo do índice e verifica se ele se encontra na consulta
            for(String termo : termos){
                for(String word : consultaSplitted){
                    if(word.equalsIgnoreCase(termo)){
                        //se já existe o termo no map, apenas incrementa a frequência
                        if(termosNaConsulta.containsKey(termo)){
                            freq = termosNaConsulta.get(termo).getFreq() + 1;
                            termosNaConsulta.get(termo).setFreq(freq);
                        }
                        //se termo não existe no map, acrescenta com docId = -1 e frequencia = 1
                        else{
                            //cria novo objeto ocorrencia com docId = -1 e frequencia = 1.
                            occur = new Ocorrencia(-1, 1);                            
                            termosNaConsulta.put(termo, occur);
                        }
                    }
                }
            }         
            return termosNaConsulta;
	}
	/**
	Retorna um mapa para cada termo existente em setTermo, sua lista ocorrencia no indice (atributo idx do QueryRunner).
	*/
	public static Map<String,List<Ocorrencia>> getOcorrenciaTermoColecao(Set<String> setTermo){
            
            Map<String, List<Ocorrencia>> OcorrenciaTermoColecao = new HashMap<String, List<Ocorrencia>>();
            for(String termo : setTermo){
                OcorrenciaTermoColecao.put(termo, idx.getListOccur(termo));
            }
            
            return OcorrenciaTermoColecao;
	}

	/**
	* A partir do indice (atributo idx), retorna a lista de ids de documentos desta consulta 
	* usando o modelo especificado pelo atributo queryRankingModel
	*/
	public static List<Integer> getDocsTermo(String consulta)
	{
            //Obtenha, para cada termo da consulta, sua ocorrencia por meio do método getOcorrenciaTermoConsulta
            Map<String,Ocorrencia> mapOcorrencia = getOcorrenciaTermoConsulta(consulta);
               
            //obtenha a lista de ocorrencia dos termos na colecao por meio do método  getOcorrenciaTermoColecao
            Map<String,List<Ocorrencia>> lstOcorrPorTermoDocs = getOcorrenciaTermoColecao(mapOcorrencia.keySet());
           
            //utilize o queryRankingModel para retornar o documentos ordenados de acordo com a ocorrencia de termos na consulta e na colecao
            return queryRankingModel.getOrderedDocs(mapOcorrencia, lstOcorrPorTermoDocs);
	}

	
	public static void main(String[] args) throws IOException, ClassNotFoundException, Exception{
                HashMap<String,Integer> TermsFreq = new HashMap<String,Integer>(); 
                HashMap<String,Set<Integer>> database = getRelevancePerQuery();
                String pag = "HTML5";
                String [] aux1, termos;
                Integer freq;
                
                //pasta principal
                File file = new File("C:\\Users\\NataliaNatsumy\\Documents\\ModelagemConsulta\\ProcConsulta\\src\\wikiSample");
                
                //lista de pastas dentro da pasta principal
                File subs[] = file.listFiles();
                int docId = 0;
                
                //leia o indice (da base de dados fornecida)
  		Indice idx = new IndiceLight(10);
                
                
                //percorre lista de pastas da pasta principal
                for(int j=0; j< subs.length;j++){
                    //para cada pasta, cria uma lista de arquivos que a pasta contém
                    File arqs[] = subs[j].listFiles();

                    //percorre cada arquivo da subpasta
                    for(int i=0;i<arqs.length;i++){
                        
                        File arq = arqs[i];
                        String[] name = arq.getName().split(Pattern.quote("."));
                        
                        docId = Integer.parseInt(name[0]);    //docId será nome do arquivo
                        
                        BufferedReader br = new BufferedReader(new FileReader(arq));
             
                        String st;
                        while ((st = br.readLine()) != null){
                            
                            //aux1 = Jsoup.parse(st).text().split("\n");

                            //for(String aux2 : aux1){
                                termos = Jsoup.parse(st).text().split(" ");

                                for(String termo : termos){
                                    if(TermsFreq.containsKey(termo)){
                                        freq = TermsFreq.remove(termo);
                                        freq++;
                                        idx.index(termo, docId, freq);
                                        TermsFreq.put(termo, freq);
                                    }else{
                                        idx.index(termo, docId, 1);
                                        TermsFreq.put(termo, 1);
                                    }
                                }
                            //}
                        }
                    }    
                }        
 		//Checagem se existe um documento (apenas para teste, deveria existir)
		System.out.println("Existe o doc? "+idx.hasDocId(105047));
		
		//Instancie o IndicePreCompModelo para precomputar os valores necessarios para a query
		System.out.println("Precomputando valores atraves do indice...");
                IndicePreCompModelo idxPreCom = new IndicePreCompModelo(idx);
		long time = System.currentTimeMillis();
                
		System.out.println("Total (precompta o valor da : "+(System.currentTimeMillis()-time)/1000.0+" segs");
		
		//encontra os docs relevantes
		HashMap<String,Set<Integer>> mapRelevances = getRelevancePerQuery();

		System.out.println("Fazendo query...");
                System.out.println("Digite uma query: ");
		//String query = "São Paulo";//aquui, peça para o usuário uma query (voce pode deixar isso num while ou fazer um interface grafica se estiver bastante animado ;)	
		String query;
                Scanner in = new Scanner(System.in);
                query = in.nextLine();
                runQuery(query,idx, idxPreCom,mapRelevances);
		
	}
	
	public static void runQuery(String query,Indice idx, IndicePreCompModelo idxPreCom ,HashMap<String,Set<Integer>> mapRelevantes) {
		long time;
		time = System.currentTimeMillis();
		
		//PEça para usuario selecionar entre BM25, Booleano ou modelo vetorial para intanciar o QueryRunner 
		//apropriadamente. NO caso do booleano, vc deve pedir ao usuario se será um "and" ou "or" entre os termos. 
		//abaixo, existem exemplos fixos.
                int opcao;
                int opcaobool;
                boolean selecao = false;
                boolean selecaobool = false;
                Scanner scanner;
                
                while(!selecao){
                    System.out.println("Digite a opção desejada:");
                    System.out.println("1 - BM25");
                    System.out.println("2 - Booleano");
                    System.out.println("3 - Modelo Vetorial");

                    scanner = new Scanner(System.in);
                    opcao = scanner.nextInt();
                    if(opcao != 1 && opcao != 2 && opcao != 3){
                        System.out.println("Opção inválida.");
                    }
                    if(opcao == 1){
                        selecao = true;
                        QueryRunner qr = new QueryRunner(idx,new BM25RankingModel(idxPreCom, 0.75, 1));
                    }
                    if(opcao == 2){
                        while(!selecaobool){
                            System.out.println("Digite a operação desejada:");
                            System.out.println("1 - AND");
                            System.out.println("2 - OR");
                            System.out.println("3 - voltar");
                            scanner = new Scanner(System.in);
                            opcaobool = scanner.nextInt();
                            
                            if(opcaobool != 1 && opcaobool != 2 && opcaobool != 3){
                                System.out.println("Opção inválida."); 
                            }
                            if(opcaobool == 1){
                                selecaobool = true;
                                selecao = true;
                                QueryRunner qr = new QueryRunner(idx,new BooleanRankingModel(OPERATOR.AND));
                            }
                            if(opcaobool == 2){
                                selecaobool = true;
                                selecao = true;
                                QueryRunner qr = new QueryRunner(idx,new BooleanRankingModel(OPERATOR.OR));
                            }
                            if(opcaobool == 3){
                                selecaobool = true;
                            }
                        }                        
                    }
                    if(opcao == 3){
                        selecao = true;
                        QueryRunner qr = new QueryRunner(idx,new VectorRankingModel(idxPreCom));
                    }                    
                }                	
		System.out.println("Total: "+(System.currentTimeMillis()-time)/1000.0+" segs");
		
		//List<Integer> lstResposta = /**utilize o metodo getDocsTerm para pegar a lista de termos da resposta**/;
                List<Integer> lstResposta = getDocsTermo(query);
		System.out.println("Tamanho: "+lstResposta.size());
		
		//nesse if, vc irá verificar se a consulta possui documentos relevantes
		//se possuir, vc deverá calcular a Precisao e revocação nos top 5, 10, 20, 50. O for que fiz abaixo é só uma sugestao e o metododo countTopNRelevants podera auxiliar no calculo da revocacao e precisao 
		
                Set<Integer> docsRelevantes = new HashSet<Integer>();
                        
                for(Set<Integer> set : mapRelevantes.values()){
                    docsRelevantes.addAll(set);
                }
                if(true){
			int[] arrPrec = {5,10,20,50};
			double revocacao = 0;
			double precisao = 0;
                        
                        
			for(int n : arrPrec)
			{
				revocacao = countTopNRelevants(n, lstResposta, docsRelevantes);//substitua aqui pelo calculo da revocacao topN
				precisao = 0;//substitua aqui pelo calculo da revocacao topN
				System.out.println("PRecisao @"+n+": "+precisao);
				System.out.println("Recall @"+n+": "+revocacao);
			}
		}

		//imprima aas top 10 respostas
                System.out.println("Top 10 respostas: ");
                printTopNRelevants(10, lstResposta, docsRelevantes);

	}
        
        public static void printTopNRelevants(int n, List<Integer> lstRespostas, Set<Integer> docRelevantes){
                       
            for(int i=0; i < n; i++){
                for(Integer doc : docRelevantes){
                    if(doc.equals(lstRespostas.get(i))){
                        System.out.println(doc);
                    }
                }
            }            
        }	
}
