import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.regex.*;
import java.util.*;
class Node{
    String nodename;
    int value;
    ArrayList<String> children;
    boolean isRoot;
    boolean isTerminal;

    Node(String name, int val,ArrayList<String> children, boolean isRoot, boolean isTerminal){
        this.nodename = name;
        this.value=val;
        this.children=children;
        this.isRoot=isRoot;
        this.isTerminal=isTerminal;
    }

    public String getName(){
        return this.nodename;
    }
    public ArrayList<String> getChildren() {
        return this.children;
    }
    public int getValue(){
        return this.value;
    }
    public boolean isRoot(){
        return this.isRoot;
    }
    public boolean isTerminal(){
        return this.isTerminal;
    }
    public void getInfo() {
        System.out.println("Nodename : "+nodename);
        System.out.println("Root? :"+isRoot);
        System.out.println("Terminal? :"+isTerminal);
        System.out.print("Children :");
        if(children!=null)
        for (String i : children)
            System.out.print(i+" ");
        System.out.println();
    }
    public void setValue(int value){
        this.value=value;
    }
}

public class minimaxAI{
    // Initial values of Alpha and Beta
    static int MAX = Integer.MAX_VALUE;
    static int MIN = Integer.MIN_VALUE;
    static HashMap<String, Node> hm=new HashMap<>();

    static HashMap<String,ArrayList<String>> parentChildSet=new HashMap<>();
    static HashMap<String,ArrayList<String>> childParentSet=new HashMap<>();
    static HashMap<String,Integer> terminals=new HashMap<>();
    static HashSet<Node> nodes = new HashSet<>();
    static String root=null;
    static Node rootNode=null;



    public static Node minimaxAlgo(boolean isMaxPlayer, Node n, int alpha, int beta, boolean isAlphaBetaAllowed, boolean isVerboseMode, int size){
        if(n.isTerminal()) return n;
        boolean flag=false;

        if (isMaxPlayer)
        {
            int best = MIN;
            Node bestNode = null;

            // Recur for all children
            ArrayList<String> children = n.getChildren();

            for (int i = 0; i < children.size(); i++)
            {
                Node child = hm.get(children.get(i));
                Node mostEfficientChild = minimaxAlgo(false,  child, alpha, beta, isAlphaBetaAllowed, isVerboseMode, size);
                child.setValue(mostEfficientChild.getValue());
                if(best<=child.getValue()){
                    best=child.getValue();
                    bestNode=child;
                }

                alpha = Math.max(alpha, best);

                if(best>size){
                    flag=true;
                    break;
                }
                // Alpha Beta Pruning
                if (isAlphaBetaAllowed && beta <= alpha){
                    flag=true;
                    break;
                }

            }
            if(!flag) {
                if(isVerboseMode || n.isRoot())
                System.out.println("max(" + n.getName() + ") chooses " + bestNode.getName() + " for " + best);
            }
            return bestNode;
        }
        else
        {
            int best = MAX;
            Node bestNode = null;

            // Recur for all children
            ArrayList<String> children = n.getChildren();
            for (int i = 0; i < children.size(); i++)
            {
                Node child = hm.get(children.get(i));
                Node mostEfficientChild = minimaxAlgo(true,  child, alpha, beta, isAlphaBetaAllowed, isVerboseMode,size);
                child.setValue(mostEfficientChild.getValue());
                if(best>child.getValue()){
                    best=child.getValue();
                    bestNode=child;
                }
                beta = Math.min(beta, best);

                if(best<0-size){
                    flag=true;
                    break;
                }
                // Alpha Beta Pruning
                if (isAlphaBetaAllowed && beta <= alpha){
                    flag=true;
                    break;
                }

            }
            if(!flag) {
                if(isVerboseMode || n.isRoot())
                    System.out.println("min(" + n.getName() + ") chooses " + bestNode.getName() + " for " + best);
            }
            return bestNode;
        }
    }



    public static void main(String args[]) {
        String file1=args[args.length-1];
        int size=Integer.MAX_VALUE;
        boolean isMaxPlayer=true, isVerboseRequired=false, isAlphaBetaAllowed=false;
        if(args.length<2){
            System.out.print("Minimum 2 arguments expected : Please pass root player min/max and input file location atleast");
            System.exit(0);
        }
        for(int i=0;i<args.length-1;i++){
            if(args[i].toLowerCase().compareTo("min")==0) {
                isMaxPlayer=false;
                continue;
            }
            if(args[i].toLowerCase().compareTo("max")==0) {
                isMaxPlayer=true;
                continue;
            }
            if(args[i].toLowerCase().compareTo("-v")==0) {
                isVerboseRequired=true;
                continue;
            }
            if(args[i].toLowerCase().compareTo("-ab")==0)  {
                isAlphaBetaAllowed=true;
                continue;
            }
            else size = Integer.parseInt(args[i]);
        }
        // We know the parameters passed in the command by now

        File f1 = new File(file1);
        parser(f1); // parses the file

        if(cycleExists()){
            System.out.println("Cycles exist. Input not valid. Exiting..");
            System.exit(0);
        }
        if(!checkAllTerminalsHaveValues()){
            System.exit(0);
        }

        ArrayList<String> rootNodes = multipleRootsPresent();
        if(rootNodes.size()>1){
            System.out.print("multiple roots: '");
            for(int i=0;i<rootNodes.size()-1;i++) System.out.print(rootNodes.get(i)+"', '");
            System.out.print(rootNodes.get(rootNodes.size()-1)+"'");
            System.out.println();
            System.exit(0);
        }


        // creating nodes structure - storing name,list of children, isterminal
        // and isroot information
        for (Map.Entry<String,ArrayList<String>> entry: parentChildSet.entrySet()){
            String parent = entry.getKey();
            int tempVal=MAX;
            if(!childParentSet.containsKey(parent)){    //  parent is not a child of any node -> meaning it's a root
                root=parent;
                Node n= new Node(parent,tempVal, entry.getValue(), true, false);
                rootNode=n;
                nodes.add(n);
                hm.put(parent,n);
            }
            else if(terminals.containsKey(parent)){
                tempVal=terminals.get(parent);
                Node n=new Node(entry.getKey(),tempVal,entry.getValue(),false,true);
                nodes.add(n);
                hm.put(parent,n);
            }
            else {
                Node n=new Node(entry.getKey(),tempVal,entry.getValue(),false,false);
                nodes.add(n);
                hm.put(parent,n);
            }

        }

        minimaxAlgo(isMaxPlayer,rootNode,MIN,MAX,isAlphaBetaAllowed,isVerboseRequired,size);

    }

    public static void parser(File f1){
        try {
            BufferedReader br1 = new BufferedReader(new FileReader(f1));
            String input;
            while ((input = br1.readLine()) != null) {
                boolean isTerminal = Pattern.matches("[a-zA-Z0-9]+[=][-]?[a-zA-Z0-9]+", input);
                String[] equals = input.split("=");
                boolean isNonTerminal = Pattern.matches("[a-zA-Z0-9]+[:]\\s\\[([a-zA-Z0-9]+[,]?\\s?)+\\]", input);
                String[] contains = input.split("[:\\s,\\[\\]]");

                if (isTerminal) {
                    terminals.put(equals[0], Integer.parseInt(equals[1]));
                    parentChildSet.put(equals[0], null);
                }
                if (isNonTerminal) {
                    String symbol = contains[0];
                    ArrayList<String> temp = new ArrayList<>();
                    for (int i = 1; i < contains.length; i++) {
                        if (contains[i].length() != 0) {
                            temp.add(contains[i]);
                            parentChildSet.put(symbol, temp);
                            if (childParentSet.containsKey(contains[i])) {
                                ArrayList<String> arr = childParentSet.get(contains[i]);
                                arr.add(symbol);
                                childParentSet.put(contains[i], arr);
                            } else {
                                ArrayList<String> arr = new ArrayList<>();
                                arr.add(symbol);
                                childParentSet.put(contains[i], arr);
                            }

                        }
                    }

                }
            }
        }catch (Exception e){
            System.out.print("Exception encountered while opening file"+ e);
        }



    }
    public static boolean checkAllTerminalsHaveValues(){

        for (Map.Entry<String,ArrayList<String>> entry: childParentSet.entrySet()){

//            if a node is not in list of terminals -
//            then either it is a valid non terminal or
//            it's an invalid terminal with no value assigned in the file
            if(!terminals.containsKey(entry.getKey())){
                if(!parentChildSet.containsKey(entry.getKey())){
                    System.out.print("child node '"+entry.getKey()+"' of ");
                    for(int i=0;i<entry.getValue().size()-1;i++) System.out.print("'"+entry.getValue().get(i)+"', ");
                    System.out.print("'"+entry.getValue().get(entry.getValue().size()-1)+"' ");
                    System.out.print("not found");
                    System.out.println();
                    return false;
                }
            }
        }

        return true;
    }

    public static ArrayList<String> multipleRootsPresent() {
        ArrayList<String> rootNodes=new ArrayList<>();

        for (Map.Entry<String, ArrayList<String>> entry : parentChildSet.entrySet()) {
            String parent = entry.getKey();
            if (!childParentSet.containsKey(parent)) {    //  parent is not a child of any node -> meaning it's a root
                rootNodes.add(parent);
            }
        }
        return rootNodes;
    }

    public static boolean cycleExists(){
        for (Map.Entry<String, ArrayList<String>> entry : childParentSet.entrySet()) {
            String child = entry.getKey();
            ArrayList<String> p1=entry.getValue();
            for(int j=0;j<p1.size();j++) {
                String p=p1.get(j);
                if(parentChildSet.containsKey(p)){
                    ArrayList<String> c1 = parentChildSet.get(p);
                    for(int i=0;i<c1.size();i++){
                        if(c1.get(i).compareTo(child)==0) return true;
                    }
                }
            }
        }
        return false;
    }

}
