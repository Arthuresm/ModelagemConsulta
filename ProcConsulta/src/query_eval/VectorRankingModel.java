package query_eval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import indice.estrutura.Ocorrencia;


public class VectorRankingModel implements RankingModel 
{


	private IndicePreCompModelo idxPrecompVals;
	
	
	public static double tf(int freqTerm){
            return freqTerm > 0 ? 1 + (Math.log((double)freqTerm)/Math.log(2)) : 0;
	}
	public static double idf(int numDocs,int numDocsTerm){
            return (numDocs/numDocsTerm) > 0 ? Math.log(numDocs/numDocsTerm)/Math.log(2) : 0;
	}
	public static double tfIdf(int numDocs,int freqTerm,int numDocsTerm){
            return freqTerm > 0 ? tf(freqTerm)*idf(numDocs, numDocsTerm) : 0;
	}
	public VectorRankingModel(IndicePreCompModelo idxPrecomp)
	{
		this.idxPrecompVals = idxPrecomp;
	}

	/**
	 * Retorna uma lista ordenada de documentos usando o tf xidf.
	 * Para isso, para cada termo da consulta, calcule o w_iq (peso do termo i na consulta q) e o w_ij e, logo apos, acumule 
	 * o w_iq x w_ij no peso do documento (dj_weight). Depois de ter o peso de cada documento, 
	 * divida pela norma do documento (use o idxPrecompVals.getNormaDocumento)
	 * Apos ter o peso para cada documento, rode o UtilQuery.getOrderedList para retornar a lista de documentos
	 * ordenados pela consulta
	 */
        
        /*
        * DICA DO PROF : quando wiq tiver mais de uma ocorrencia, é necessario acumular o valor que repete. 
        * Dessa forma apenas depois de percorrer todo wij é que faremos a atualizacao do dj
        *
        */
	@Override
	public List<Integer> getOrderedDocs(Map<String, Ocorrencia> mapQueryOcur,
			Map<String, List<Ocorrencia>> lstOcorrPorTermoDocs) {
		
		Map<Integer,Double> dj_weight = new HashMap<Integer,Double>();
		
		return null;
	}
	
	
}
