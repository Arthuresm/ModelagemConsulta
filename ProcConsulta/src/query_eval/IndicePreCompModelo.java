package query_eval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import indice.estrutura.Indice;
import indice.estrutura.Ocorrencia;
import java.util.Iterator;
import java.util.Set;




public class IndicePreCompModelo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int numDocumentos = 0;
	private double avgLenPerDocument = 0;
	private Map<Integer,Integer> tamPorDocumento = new HashMap<Integer,Integer>();
	private Map<Integer,Double> normaPorDocumento = new HashMap<Integer,Double>();
	
	
	
	private Indice idx;
	
	public IndicePreCompModelo(Indice idx)
	{
		this.idx = idx;
		
		precomputeValues(idx);
		
		
	}
	/**
	 * Acumula o (tfxidf)^2 de mais uma ocorrencia (oc) no somatorio para calcular a norma por documento 
	 * Usar a propria norma para acumular o somatorio
	 * @param numDocsTerm
	 * @param oc
	 */
	public void updateSumSquaredForNorm(int numDocsTerm, Ocorrencia oc) {
            
            Double tf = VectorRankingModel.tf(oc.getFreq());
            Double idf = VectorRankingModel.idf(numDocumentos, numDocsTerm);
            Double aux;
            
            if(normaPorDocumento.get(oc.getDocId())!= null){
                //Armazenando o somatorio do valor corrente com a (tf*idf)^2
                aux = Math.pow(tf*idf, 2) + normaPorDocumento.get(oc.getDocId());
  
            }
            else{
                aux = Math.pow(tf*idf, 2);
            }
            normaPorDocumento.put(oc.getDocId(), aux); //Utilizando a propria norma para acumular
	}
	/**
	 * Atualiza o tamPorDocumento com mais uma cocorrencia 
	 * @param oc
	 */
	public void updateDocTam(Ocorrencia oc) {
            Integer id = oc.getDocId();
            Integer freq = oc.getFreq();
            Integer tamAtual = tamPorDocumento.get(id);
            
            tamPorDocumento.remove(id);
            tamPorDocumento.put(id, tamAtual+freq);
	}
	/**
	 * Inicializa os atributos por meio do indice (idx):
	 * numDocumentos: o numero de documentos que o indice possui
	 * avgLenPerDocument: média do tamanho (em palavras) dos documentos
	 * tamPorDocumento: para cada doc id, seu tamanho (em palavras) - use o metodo updateDocTam para auxiliar
	 * normaPorDocumento: A norma por documento (cada termo é presentado pelo seu peso (tfxidf) - use o metodo updateSumSquaredForNorm para auxiliar
	 * @param idx
	 */
	private void precomputeValues(Indice idx) {
            //Numero de documentos do indice
            numDocumentos = idx.getNumDocumentos();
            
            if(numDocumentos != 0)
                //Media de palavras dos documentos
                avgLenPerDocument = idx.getListTermos().size()/numDocumentos;
            
            Set <String> termos = idx.getListTermos();
            Iterator <String> it = termos.iterator();
            String chave = null;
            List<Ocorrencia> occur = null;
            Map<String, Integer> numDocsPerTerm = idx.getNumDocPerTerm();
            
            while(it.hasNext()){
                chave = it.next();
                occur = idx.getListOccur(chave); //Buscando a lista de ocorrencias de um termo
                
                for(int i=0; i<occur.size(); i++){ 
                    //Atualizando o tamanho por doc
                    updateDocTam(occur.get(i));   
                    
                    //Atualizando o somatorio do quadrado dos pesos
                    updateSumSquaredForNorm(numDocsPerTerm.get(chave), occur.get(i));
                }
            }  
	}


	public int getDocumentLength(int docId)
	{
		return this.tamPorDocumento.get(docId);
	}
	public int getNumDocumentos() {
		return numDocumentos;
	}

	public void setNumDocumentos(int numDocumentos) {
		this.numDocumentos = numDocumentos;
	}

	public double getAvgLenPerDocument() {
		return avgLenPerDocument;
	}

	public void setAvgLenPerDocument(double avgLenPerDocument) {
		this.avgLenPerDocument = avgLenPerDocument;
	}

	public Map<Integer, Double> getNormaPorDocumento() {
		return normaPorDocumento;
	}

	public void setNormaPorDocumento(Map<Integer, Double> normaPorDocumento) {
		this.normaPorDocumento = normaPorDocumento;
	}

	public double getNormaDocumento(int docId)
	{
		return this.normaPorDocumento.get(docId);
	}
	
}
