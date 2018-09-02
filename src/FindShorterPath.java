

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * delete duplicate moves to get a close to shortest path 
 * 
 * read from input file and write improved path to the same file
 * 
 */
public class FindShorterPath {
        
        private static String MOVES_FILE = "mapTxt/movesToLoad.txt";
        private static boolean found = false;
        //private static String OPTIMAL_FILE = "M:/optimalPath.txt";
                
        public static void main(String[] args){   
                while (!found) {
                        Scanner sc = null;      
                        ArrayList<String> moves = new ArrayList<String>();
                        try{
                                sc = new Scanner(new FileReader(MOVES_FILE));                                                                       
                                while(sc.hasNextLine()){
                                        moves.add(sc.nextLine());                                      
                                }                                                                                                                                                                                                  
                        }
                        catch(FileNotFoundException e)
                        {
                                System.err.println("File not found.");
                        }
                        finally{
                                if (sc != null){
                                        sc.close();
                                }
                        }
                        
                        System.out.println("Total moves before: " 
                                        + moves.size());
                        
                        // print path
                        for (int i = 0; i < moves.size(); i++) {
                              //System.out.println(moves.get(i));
                        }
                                         
                        int count = 0;                
                        int max = 0;
                        int from = 0;
                        int to = 0;
                        while (count < moves.size()) {
                                String line = moves.get(count);
                                if (moves.indexOf(line) 
                                                != moves.lastIndexOf(line)) {                                
                                        if (max < moves.lastIndexOf(line) 
                                                        - moves.indexOf(line)){
                                                max = to - from;
                                                from = moves.indexOf(line);
                                                to = moves.lastIndexOf(line);
                                        }                                      
                                }
                                count++;                        
                        }
                        int printFrom = from + 1;
                        int printTo = to + 1;
                        if (from == to) {
                                found = true;
                        }
                        System.out.println("from = " + printFrom);
                        System.out.println("to = " + printTo);
                        
                        for (int i = from; i < to; i++) {
                                moves.remove(from);                        
                        }
                        System.out.println("Total moves after: "+moves.size());                                                                                               
                        try {
                                PrintWriter pw = new PrintWriter(MOVES_FILE); 
                                for (int i = 0; i < moves.size(); i++) {
                                        pw.write(moves.get(i));
                                        if (i != moves.size()-1) {
                                                pw.write("\n");                                             
                                        }                                
                                }                    
                                pw.close();
                        } catch (IOException e) {  
                                System.err.println("File not found.");
                        }                        
                }                
        }        
}
