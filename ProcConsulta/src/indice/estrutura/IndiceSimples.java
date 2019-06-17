package indice.estrutura;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;




public class IndiceSimples extends Indice
{
	
	
	/**
	 * Versao - para gravação do arquivo binário
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String,List<Ocorrencia>> mapIndice = new HashMap<String,List<Ocorrencia>>();
	
	public IndiceSimples()
	{

	}
	


	@Override
	public void index(String termo,int docId,int freqTermo) 
	{
            List<Ocorrencia> occurence = new ArrayList<Ocorrencia>();  
            if(termo!=null){
                // Caso em que o termo ja existe dentro do mapIndice
                if(mapIndice.containsKey(termo)){
                   
                   occurence = mapIndice.get(termo);
                   Iterator<Ocorrencia> ocorrenciaAsIterator = occurence.iterator();
                   while(ocorrenciaAsIterator.hasNext()){
                        Ocorrencia it = ocorrenciaAsIterator.next();
                        
                        if(it.getDocId()==docId){
                            it.setFreq(freqTermo);
                            mapIndice.remove(termo);
                            mapIndice.put(termo, occurence);
                            break;
                        }
                   }
                   Ocorrencia aux = new Ocorrencia(docId, freqTermo);
                   occurence.add(aux);
                   mapIndice.put(termo, occurence);
                }
                else { //Caso em que o termo ainda nao existe no mapIndice
                    Ocorrencia oc = new Ocorrencia(docId, freqTermo);
                    occurence.add(oc);
                    mapIndice.put(termo, occurence);
                    
                }                
            }
            
	}


	@Override
	public Map<String,Integer> getNumDocPerTerm()
	{
            Map<String, Integer> aux = new HashMap<String, Integer>();
            Set <String> chaves = mapIndice.keySet();
                
            if(chaves.size() > 0){
                Iterator<String> chavesAsIterator = chaves.iterator();
                
                while(chavesAsIterator.hasNext()){
                    String it = chavesAsIterator.next();
                    Integer docOccurrence = mapIndice.get(it).size();
                    aux.put(it, docOccurrence);
                }
          
            }
            return aux;  
	}
	
	@Override
	public int getNumDocumentos()
	{
            ArrayList <Integer> numDocs = new ArrayList<Integer>();
            Set <String> chaves = mapIndice.keySet();
                
            if(chaves.size() > 0){
                Iterator<String> chavesAsIterator = chaves.iterator();
                while(chavesAsIterator.hasNext()){
                    String it = chavesAsIterator.next();
                    List<Ocorrencia> occurence = mapIndice.get(it);
                    int i = 0;
                    while(i < occurence.size()){
                        
                        if(!numDocs.contains(occurence.get(i).getDocId())){
                            
                            numDocs.add(occurence.get(i).getDocId());
                        } 
                        i+=1;
                    }
                }
            }
            return numDocs.size();
	}
	
	@Override
	public Set<String> getListTermos()
	{
            Set <String> chaves = mapIndice.keySet();
            return chaves;
	}	
	
	@Override
	public List<Ocorrencia> getListOccur(String termo)
	{
            List<Ocorrencia> occurence = mapIndice.get(termo);
            return occurence; 
        }
        
        public boolean hasDocId(int idDoc){
            Set<String> chaves = getListTermos();
            List<Ocorrencia> occur = null;
            
            for(String termo : chaves){
                occur = getListOccur(termo);
                for(int i = 0; i < occur.size(); i++){
                    if(occur.get(i).getDocId() == idDoc){
                        return true;
                    }
                }
            }
            return false;
        }

}
