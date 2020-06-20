import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;


public class BoggleSolver {
    private final ExtTrieST dic;
    private char[] arrBoard; // current board
    private int r, c; // row, col numbers
    private SET<String> StringsFound;
  
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        dic = new ExtTrieST();
        int i = 0;
        for (String st : dictionary) dic.put(st, i++);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        /* INPUT
        0  1  2  3
        4  5  6  7
        8  9  10 11
        12 13 14 15
        */
        StringsFound = new SET<String>();
        r = board.rows();
        c = board.cols();
        // Get array {0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15}
        arrBoard = new char[r * c];

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++)
                arrBoard[id(i, j)] = board.getLetter(i, j);
        }


        LookForStrings lookForStrings = new LookForStrings();
     
        for (int i = 0; i < arrBoard.length; i++) {
                      lookForStrings.searchStrings(i);
        }
               return StringsFound;
    }


    // add found strings to StringsFound set
    private class LookForStrings {
        
        private void searchStrings(int position) {
            boolean[] visited = new boolean[arrBoard.length];
            StringBuilder currentString = new StringBuilder(arrBoard.length);
           
            dfs(position, currentString, visited, dic.root);
          }

        // dfs method
        private void dfs(int position, StringBuilder currentString, boolean[] visited,
                         ExtTrieST.Node x) {           
            char newChar = arrBoard[position];
            ExtTrieST.Node thisDfsNode = dic.getNextNode(x, newChar);

            // Does such prefix exist?
            if (thisDfsNode == null) {
                //  StdOut.println("RETURN NULL");
                return;
            }

            int charsN = 1; // number of adding chars (2 if QU case)
            visited[position] = true;

            if (newChar == 81) {                
                ExtTrieST.Node tempDfsNode = dic.getNextNode(thisDfsNode, (char) 85);
                if (tempDfsNode != null) {
                    currentString.append(newChar);
                    currentString.append((char) 85);
                    charsN++;
                    thisDfsNode = tempDfsNode;
                }
                else return;
            }
            else {
                currentString.append(arrBoard[position]);
            }           
            // Does such word exist?
            if (currentString.length() > 2 && thisDfsNode.val != 0)
                StringsFound.add(currentString.toString());           


            for (int p : adj(position)) {
                if (!visited[p]) {

                    dfs(p, currentString, visited, thisDfsNode);
                }
            }            
            currentString.delete(currentString.length() - charsN, currentString.length());
            visited[position] = false;
        }
    }
   
    // convert coordinates row x col into 1d coordinates (row, col, colNum)
    private int id(int row, int col) {
        return row * c + col;
    }

    // return all adjascent (id)
    private Iterable<Integer> adj(int id) {
        // expAdj++;
        int row = id / c;
        int col = id - row * c;
        return adj(row, col);
    }

    // return all adjascent ids (row, col)
    private Iterable<Integer> adj(int row, int col) {
        // shifting coordinates to lower left corner
        ArrayList<Integer> adj = new ArrayList<>();
        int x0 = col - 1;
        int y0 = row - 1;
        for (int y = y0; y <= y0 + 2; y++) {
            for (int x = x0; x <= x0 + 2; x++) {
                if (x < 0 || y < 0 || x > c - 1 || y > r - 1 || (x == x0 + 1 && y == y0 + 1))
                    continue;
                int id = id(y, x);
                adj.add(id);
            }
        }
        return adj;
    }
 
    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        StringBuilder sb = new StringBuilder(word);
        if (!dic.contains(sb)) return 0;
        int length = word.length();
        if (length >= 8) length = 8;
        switch (length) {
            case 3:
                return 1;
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;
            case 8:
                return 11;
        }

        return 0;
    }

    // Test client: prints all solutions for given board and dictionary. Dictionary is given via running argument
    // Prints number of solutions
    // Counts number of similar solutions for 5 seconds
    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] newDic = in.readAllStrings();
        BoggleSolver bs = new BoggleSolver(newDic);
        
        BoggleBoard bb = new BoggleBoard("board4x4.txt");
       

        int s = 0;
        StdOut.println(bb.toString());
        for (String str : bs.getAllValidWords(bb)) {
            StdOut.println(str);
            s++;
        }
        StdOut.println("Solutions..." + s);
          
        double time = 0;
        double startTime;
        double endTime;      

        int n = 0;
        while (time < 5000) {
            startTime = System.currentTimeMillis();
            bs.getAllValidWords(bb);
            endTime = System.currentTimeMillis();
            time += endTime - startTime;
            n++;
        }

        StdOut.printf("getallvalid for 5 sec %d \n", n);
    }
}
