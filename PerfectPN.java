package edu.rice.cs.bioinfo.programs.phylonet.algos.ppn;

import edu.rice.cs.bioinfo.programs.phylonet.structs.network.Network;
import edu.rice.cs.bioinfo.programs.phylonet.structs.network.NetNode;
import edu.rice.cs.bioinfo.programs.phylonet.structs.network.characterization.NetworkTree;
import edu.rice.cs.bioinfo.programs.phylonet.structs.network.rearrangement.ReticulationEdgeAddition;
import edu.rice.cs.bioinfo.programs.phylonet.structs.network.util.Networks;
import edu.rice.cs.bioinfo.programs.phylonet.structs.tree.model.Tree;
import edu.rice.cs.bioinfo.programs.phylonet.structs.tree.util.Trees;
import edu.rice.cs.bioinfo.programs.phylonet.structs.network.util.Networks;
import edu.rice.cs.bioinfo.programs.phylonet.structs.network.model.bni.BniNetNode;
import edu.rice.cs.bioinfo.programs.phylonet.structs.network.model.bni.BniNetwork;

import java.io.*;
import java.util.*;

public class PerfectPN{
    public String newick; //(((A,B),((C,D),E))((F,G),H))
    public Reader _reader;     // Input stream.
    public int leafNum; //# of leaves
    public int cNum; //# of total characters

    // strings of character setting e.g. "A 1 1 7/8" -> {"A", "1", "1", "7/8"}
    public List<String> cSettings;

    //states per leaf per character {7,8}
    public HashSet<Integer> states;

    //all c of one leaf e.g. (A 1 1 7/8)-> {1, 1, $states:{7,8}}
    public List<HashSet> leafLabels;

    //list of c for all leaves e.g. a[0]=(List){ $name: "A", $leafLabels:{1, 1, {7,8}} }
    public Map<String, List> allLeavesLabels;
    //allLeavesLabels{}

    public CompatibilityChecker cChecker;
    public CompatibilitySolver cSolver;


    public String getNewick(){
        return newick;
    }
    public Map getAllLeavesLabels(){
        return allLeavesLabels;
    }

    private void getInput() throws IOException, ExNewickException //from "ExNewickReader"
    {
        BufferedReader br = new BufferedReader(_reader);
        String _first_line;
        // Read the first non-blank line as the main tree.
        while ((_first_line = br.readLine()) != null) {
            _first_line = _first_line.trim();
            if (_first_line.length() != 0) {
                break;
            }
        }

        if (_first_line.indexOf(";") < 0) {
            _first_line += ";";
        }
        newick = _first_line;


        // Read subsequent non-blank lines as allLeavesLabels[i].
        String[] paras =  br.readLine().split(" ");
        leafNum = (Integer)paras[0];
        cNum = (Integer)paras[1];
        for(int i=0; i<leafNum; i++) {
            paras =  br.readLine().split(" ");

            for(int j=1; j<=cNum; j++) {
                if(paras.get(j).size()>1)// polymorphic e.g.7/8
                {
                    Set states = new HashSet();
                    String[] state = paras.get(j).split("/");
                    states.add((Integer)state[0]);
                    states.add((Integer)state[1]);
                }
                else{ //normal single state
                    Set states = new HashSet();
                    state.add((Integer)paras.get(j));
                }
                leafLabels.add(j,states);
            }
            allLeavesLabels.put(paras.get(0),leafLabels);//read node_name
        }
    }

    public Network<Object> getNewNetwork(String newick) {

        getInput();
        Network<Object> trueNetwork;
        trueNetwork = Networks.readNetwork(newick);

        return trueNetwork;
    }

    public Network<Object> getNetworkWithLabel(String newick, Map allLeavesLabels) {
        //trueNetwork = Networks.readNetwork(newick);

        //getInput();
        Network<Object> trueNetwork;
        trueNetwork = Networks.readNetwork(newick);
        List<NetNode<T>> leaves = trueNetwork.getLeaves();
        //TODO: add char label of each node
        for(int i=0; i<leaves.size(); i++){
            NetNode<T> node = leaves.get(i);
            node.setData(allLeavesLabels.get(node.getName()));
            //all c of one leaf e.g. (A 1 1 7/8)-> {1, 1, {7,8}}
            //public List<HashSet> leafLabels;
        }
        return trueNetwork;
    }

    private boolean isFullReticulation(Network<Object> network){
        //if(true)
        //return true;
        int number = 0;
        for (NetNode<Object> node : network.dfs()) {
            if(node.isNetworkNode())
                number++;
        }
        int count = 0;
        for (NetworkTree<Object> nt : Networks.getTrees(network)) {
            count++;
        }
        if((1 << number) == count)
            return true;
        else
            return false;
    }

    //TODO: test input
    public static void main(String[] args){
        
    	/*
    	 * PerfectPN ppn = new PerfectPN();
    	 
        ppn.getInput();
        Network labeledNetwork = ppn.getNetworkWithLabel(ppn.getNewick(),ppn.getAllLeavesLabels());
        System.out.printly(labeledNetwork.toString());
        */
    	System.out.println("Success");

    }
}
