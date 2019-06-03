package query_eval;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import indice.estrutura.Ocorrencia;
import java.util.Iterator;

public class BooleanRankingModel implements RankingModel {
	public enum OPERATOR{
		AND,OR;
	}
	private OPERATOR operator;
	public BooleanRankingModel(OPERATOR op)
	{
		this.operator = op;
	}
	
	/**
	 * Retorna a lista de documentos (nao eh necessário fazer a ordenação) para a consulta  mapQueryOcur e a lista de
	 * ocorrencias de documentos lstOcorrPorTermoDocs.
	 *
	 * mapQueryOcur: Mapa de ocorrencia de termos na consulta
	 * lstOcorrPorTermoDocs: lista de ocorrencia dos termos nos documentos (apenas os termos que exitem na consulta)
	 */
	@Override
	public List<Integer> getOrderedDocs(Map<String, Ocorrencia> mapQueryOcur,
			Map<String, List<Ocorrencia>> lstOcorrPorTermoDocs){
		
		if(this.operator == OPERATOR.AND){
			return intersectionAll(lstOcorrPorTermoDocs);
		} else {
			return unionAll(lstOcorrPorTermoDocs);
		}
	}
	/**
	 * Faz a uniao de todos os elementos
	 * @param lstOcorrPorTermoDocs
	 * @return
	 */
	public List<Integer> unionAll(Map<String, List<Ocorrencia>> lstOcorrPorTermoDocs){
            Set<String> chaves = lstOcorrPorTermoDocs.keySet();
            Iterator<String> it = chaves.iterator();
            List<Integer> docsConsult = new ArrayList<Integer>();
            
            
            while(it.hasNext()){
                String chave = it.next();
                List<Ocorrencia> occur = lstOcorrPorTermoDocs.get(chave);
                
                for(int i=0; i < occur.size(); i++){
                    if(!docsConsult.contains(occur.get(i).getDocId())){
                        docsConsult.add(occur.get(i).getDocId());
                    }
                }
                
            }
            
            return docsConsult;
            
	}
        
        public List<Integer> intersection(List<Integer> lista1, List<Integer> lista2){
            List<Integer> result = new ArrayList<Integer>(); 
            
            for(int i = 0; i < lista1.size(); i++) { 
                if(lista2.contains(lista1.get(i))){
                    result.add(lista1.get(i)); 
                }
            } 
            return result; 
        }
        
	/**
	 * Faz a interseção de todos os elementos
	 * @param lstOcorrPorTermoDocs
	 * @return
	 */
	public List<Integer> intersectionAll(Map<String, List<Ocorrencia>> lstOcorrPorTermoDocs){
            Set<String> chaves = lstOcorrPorTermoDocs.keySet();
            Iterator<String> it = chaves.iterator();
            List<Integer> docsConsult = new ArrayList<Integer>();
            List<Integer> aux = new ArrayList<Integer>();
            
            if(it.hasNext()){ 
                String proximo = it.next();
                
                for (int i = 0; i < lstOcorrPorTermoDocs.get(proximo).size(); i++){
                    if(!docsConsult.contains(lstOcorrPorTermoDocs.get(proximo).get(i).getDocId())){
                        docsConsult.add(lstOcorrPorTermoDocs.get(proximo).get(i).getDocId());
                    }
                }
            }
            
            while(it.hasNext()){
                String prox = it.next();
                
                for(int i = 0; i < lstOcorrPorTermoDocs.get(prox).size(); i++){
                    if(!aux.contains(lstOcorrPorTermoDocs.get(prox).get(i).getDocId())){
                        aux.add(lstOcorrPorTermoDocs.get(prox).get(i).getDocId());
                    }
                }
                docsConsult = intersection(docsConsult, aux);
            }
            return docsConsult;        
	}
}
