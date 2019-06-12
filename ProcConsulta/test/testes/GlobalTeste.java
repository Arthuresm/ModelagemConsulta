package testes;

import indice.estrutura.Indice;
import indice.estrutura.IndiceLight;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aluno
 */
public class GlobalTeste {
    

    public static Indice readFile(File arq) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader("IndiceLight.txt"));
        String linha; 
        IndiceLight index = new IndiceLight(10);
        
        while(br.ready()){
            linha = br.readLine(); 
        }
        br.close();
       
        
    }
    
    public static void main(String[] args){
        private Indice ind; 
        private VectorRankingModel vectorRanking; 
        
      

    }
}
