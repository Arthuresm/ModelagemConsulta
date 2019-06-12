package query_eval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import indice.estrutura.Ocorrencia;
import java.util.Iterator;
import java.util.Set;

public class BM25RankingModel implements RankingModel 
{
	private IndicePreCompModelo idxPrecompVals;
	private double b;
	private int k1;
	
	public BM25RankingModel(IndicePreCompModelo idxPrecomp,double b,int k1)
	{
		this.idxPrecompVals = idxPrecomp;
		this.b = b;
		this.k1 = k1;
	}
	/**
	 * Calcula o idf (adaptado) do bm25
	 * @param numDocs
	 * @param numDocsArticle
	 * @return
	 */
	public double idf(int numDocs,int numDocsArticle)
	{
		return (numDocs - numDocsArticle + 0.5/numDocsArticle + 0.5) > 0 ? Math.log(numDocs - numDocsArticle + 0.5/numDocsArticle + 0.5)/Math.log(2) : 0;
	}
	/**
	 * Calcula o beta_{i,j}
	 * @param freqTerm
	 * @return
	 */
	public double beta_ij(int freqTermDoc, int docId) {
            double k = 1;
            double b = 0.75;
            
            double tamanhoDoc = idxPrecompVals.getDocumentLength(docId);
            double mediaTamDocs = idxPrecompVals.getAvgLenPerDocument();
            
            double beta = (k + 1)*freqTermDoc/(k*((1-b) + b*tamanhoDoc/mediaTamDocs)+freqTermDoc);
            
            return beta;
            
	}
	
	/**
	 * Retorna a lista ordenada de documentos usando o modelo do BM25.
	 * para isso, em dj_weight calcule o peso do documento j para a consulta. 
	 * Para cada termo, calcule o Beta_{i,j} e o idf e acumule o pesso desse termo para o documento. 
	 * Logo após, utilize UtilQuery.getOrderedList para ordenar e retornar os docs ordenado
	 * mapQueryOcour: Lista de ocorrencia de termos na consulta
	 * lstOcorrPorTermoDocs: Lista de ocorrencia dos termos nos documentos (apenas termos que ocorrem na consulta)
	 */
	@Override
	public List<Integer> getOrderedDocs(Map<String, Ocorrencia> mapQueryOcur,
			Map<String, List<Ocorrencia>> lstOcorrPorTermoDocs) {
		
		
		Map<Integer,Double> dj_weight = new HashMap<Integer,Double>();
		
		Set<String> termos = mapQueryOcur.keySet();
                Iterator<String> it = termos.iterator();
                String chave = null;
                List<Ocorrencia> occur;
                double idf, bij;
                double acumulador = 0;
                
                while(it.hasNext()){
                    chave = it.next();
                    idf = idf(idxPrecompVals.getNumDocumentos(), lstOcorrPorTermoDocs.get(chave).size());
                    occur = lstOcorrPorTermoDocs.get(chave);
                    
                    
                    for(int i=0; i < occur.size(); i++){
                        acumulador = 0;
                        bij = beta_ij(occur.get(i).getFreq(), occur.get(i).getDocId());
                        
                        if(dj_weight.containsKey(occur.get(i).getDocId())){
                            acumulador = dj_weight.get(occur.get(i).getDocId());
                        }
                        dj_weight.put(occur.get(i).getDocId(),acumulador + (idf*bij));
                    }
                }
                return UtilQuery.getOrderedList(dj_weight); 
		

	}
	

}
