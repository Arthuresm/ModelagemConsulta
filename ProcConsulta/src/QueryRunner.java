
import indice.estrutura.Indice;
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
import java.util.Set;
import query_eval.RankingModel;


public class  QueryRunner {

	public enum OPERACAO{
		AND,OR;
	}
	private RankingModel queryRankingModel;
	private Indice idx;
	
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
	public HashMap<String,Set<Integer>> getRelevancePerQuery() throws Exception {
            
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
	public int countTopNRelevants(int n, List<Integer> lstRespostas, Set<Integer> docRelevantes){
            
	}
	/**
	Preprocesse a consulta da mesma forma que foi preprocessado o texto do documento.
	E transforme a consulta em um Map<String,Ocorrencia> onde a String é o termo que ocorreu
	e Ocorencia define quantas vezes esse termo ocorreu na consulta. Coloque o docId como -1.
	DICA: tente reaproveitar metodos do indexador para isso. Além disso, se você considerar a consulta como um documento, é possivel fazer
	algo parecido com o que foi feito no metodo index do Indexador.
	*/
	public Map<String,Ocorrencia> getOcorrenciaTermoConsulta(String consulta){
	}
	/**
	Retorna um mapa para cada termo existente em setTermo, sua lista ocorrencia no indice (atributo idx do QueryRunner).
	*/
	public Map<String,List<Ocorrencia>> getOcorrenciaTermoColecao(Set<String> setTermo){


	}

	/**
	* A partir do indice (atributo idx), retorna a lista de ids de documentos desta consulta 
	* usando o modelo especificado pelo atributo queryRankingModel
	*/
	public static List<Integer> getDocsTermo(String consulta)
	{

		
		
		//Obtenha, para cada termo da consulta, sua ocorrencia por meio do método getOcorrenciaTermoConsulta
		Map<String,Ocorrencia> mapOcorrencia = null;

		//obtenha a lista de ocorrencia dos termos na colecao por meio do método  getOcorrenciaTermoColecao
		Map<String,List<Ocorrencia>> lstOcorrPorTermoDocs = null;
	 	

		//utilize o queryRankingModel para retornar o documentos ordenados de acordo com a ocorrencia de termos na consulta e na colecao
		return null;
	}

	
	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
		
		
		//leia o indice (base da dados fornecida)
  		Indice idx = null;

 		//Checagem se existe um documento (apenas para teste, deveria existir)
		System.out.println("Existe o doc? "+idx.hasDocId(105047));
		
		//Instancie o IndicePreCompModelo para pr ecomputar os valores necessarios para a query
		System.out.println("Precomputando valores atraves do indice...");
		long time = System.currentTimeMillis();



		
		System.out.println("Total (precompta o valor da : "+(System.currentTimeMillis()-time)/1000.0+" segs");
		
		//encontra os docs relevantes
		HashMap<String,Set<Integer>> mapRelevances = getRelevancePerQuery();

		System.out.println("Fazendo query...");
		String query = "São Paulo";//aquui, peça para o usuário uma query (voce pode deixar isso num while ou fazer um interface grafica se estiver bastante animado ;)	
		runQuery(query,idx, idxPreCom,mapRelevances);
		
		
	}
	
	public static void runQuery(String query,Indice idx, IndicePreCompModelo idxPreCom ,HashMap<String,Set<Integer>> mapRelevantes) {
		long time;
		time = System.currentTimeMillis();
		
		//PEça para usuario selecionar entre BM25, Booleano ou modelo vetorial para intanciar o QueryRunner 
		//apropriadamente. NO caso do booleano, vc deve pedir ao usuario se será um "and" ou "or" entre os termos. 
		//abaixo, existem exemplos fixos.
		//QueryRunner qr = new QueryRunner(idx,new BooleanRankingModel(OPERATOR.AND));
		//QueryRunner qr = new QueryRunner(idx,new VectorRankingModel(idxPreCom));
		QueryRunner qr = new QueryRunner(idx,new BM25RankingModel(idxPreCom, 0.75, 1));
		
		System.out.println("Total: "+(System.currentTimeMillis()-time)/1000.0+" segs");
		
		List<Integer> lstResposta = /**utilize o metodo getDocsTerm para pegar a lista de termos da resposta**/;
		System.out.println("Tamanho: "+lstResposta.size());
		
		//nesse if, vc irá verificar se a consulta possui documentos relevantes
		//se possuir, vc deverá calcular a Precisao e revocação nos top 5, 10, 20, 50. O for que fiz abaixo é só uma sugestao e o metododo countTopNRelevants podera auxiliar no calculo da revocacao e precisao 
		if(true)
		{
			int[] arrPrec = {5,10,20,50};
			double revocacao = 0;
			double precisao = 0;
			for(int n : arrPrec)
			{
				revocacao = 0;//substitua aqui pelo calculo da revocacao topN
				precisao = 0;//substitua aqui pelo calculo da revocacao topN
				System.out.println("PRecisao @"+n+": "+precisao);
				System.out.println("Recall @"+n+": "+revocacao);
			}
		}

		//imprima aas top 10 respostas

	}
	
	
}
