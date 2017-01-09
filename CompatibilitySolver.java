package edu.rice.cs.bioinfo.programs.phylonet.algos.ppn;

import edu.rice.cs.bioinfo.programs.phylonet.structs.network.Network;
import edu.rice.cs.bioinfo.programs.phylonet.structs.network.characterization.NetworkTree;
import edu.rice.cs.bioinfo.programs.phylonet.structs.network.model.bni.BniNetNode;
import edu.rice.cs.bioinfo.programs.phylonet.structs.network.model.bni.BniNetwork;
import edu.rice.cs.bioinfo.programs.phylonet.structs.network.rearrangement.ReticulationEdgeAddition;
import edu.rice.cs.bioinfo.programs.phylonet.structs.network.util.Networks;
import edu.rice.cs.bioinfo.programs.phylonet.structs.tree.model.Tree;
import edu.rice.cs.bioinfo.programs.phylonet.structs.tree.util.Trees;


public class CompatibilitySolver{


    //k -> #edges added,  return compatible networks or null
    public <T> BniNetNode<T> KNetworkCompatible(BniNetwork<T> network, List cSet, int k){


    }

    //k -> #edges added,  return compatible networks or null
    public <T> BniNetNode<T> MIPPN(BniNetwork<T> network, List cSet){


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

    void addReticulation(Network<Object> network){
        ArrayList<Tuple<NetNode, NetNode>> allEdges = new ArrayList<>();
        ArrayList<Tuple<NetNode, NetNode>> allEdgesNeedBrlens = new ArrayList<>();
        ArrayList<Tuple<NetNode, NetNode>> allReticulationEdges = new ArrayList<>();
        ArrayList<Tuple<NetNode, NetNode>> allRemovableReticulationEdges = new ArrayList<>();
        Set<String> taxa = new HashSet<>();

        getNetworkInfo(network, allEdges, allEdgesNeedBrlens, allReticulationEdges, allRemovableReticulationEdges, taxa);

        /*for(Tuple<NetNode, NetNode> edge : allEdges) {
            if(edge.Item1 == network.getRoot())
                allEdges.remove(edge);
        }*/

        ReticulationEdgeAddition reticulationEdgeAddition = new ReticulationEdgeAddition();

        boolean successRearrangment;
        do{
            Set<Integer> previousTriedEdges = new HashSet<>();
            successRearrangment = false;
            while(!successRearrangment){
                setParametersForReticulationEdgeAddition(allEdges);
                reticulationEdgeAddition.setParameters(network, _targetEdge, _targetEdgeBrlen, _targetEdgeInheriProb, _sourceEdge, null, null, _destinationEdge, null, null);

                if (reticulationEdgeAddition.performOperation()) {

                    if (Networks.hasCycle(network) || !isNetworkValid(network, taxa) || !isFullReticulation(network)) {
                        successRearrangment = false;
                        reticulationEdgeAddition.undoOperation();
                    }
                    else{
                        successRearrangment = true;
                        if(_printDetails){
                            System.out.println();
                        }
                    }
                }
            }
        }while(!successRearrangment);
    }

}
