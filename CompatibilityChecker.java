package edu.rice.cs.bioinfo.programs.phylonet.algos.ppn;

import edu.rice.cs.bioinfo.programs.phylonet.structs.network.Network;
import edu.rice.cs.bioinfo.programs.phylonet.structs.network.model.bni.BniNetNode;
import edu.rice.cs.bioinfo.programs.phylonet.structs.network.model.bni.BniNetwork;
import edu.rice.cs.bioinfo.programs.phylonet.structs.network.util.Networks;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

//one checker for one character per network
public class CompatibilityChecker{
	public List<Integer> cSet//characters list
	public BniNetwork<T> network;
    //list of c for all leaves e.g. a[0]=(List){ $name: "A", $leafLabels:{1, 1, {7,8}} }
    //public Map<String, List> allLeavesLabels;
	
	//TODO set a switch to decide if compute all Bi
    public Map<Integer,Integer> stateCounter;// <#state, times> e.g <1, 3> -> 1st state appears three times 
    
    public Map<String,Integer> mustHave; //must-have <A,1> <A,2> ..
    public Map<String,Map> Bi;  //must-have: Map<nodeName, musthaveList> || musthaveList<#state, times> -> <int, int>
    public Set<String> hasChecked;
    public int leafNum; //# of leaves
    public int cNum; //# of total characters
    public boolean[] compatibility;
    
    //TODO create function
    
    
	//count states
    public Map<Integer,Integer> countState(int c){
    	List<BniNetNode> leaves = network.getLeaves();
    	Map<Integer,Integer> currentCounter = new Map<Integer,Integer>;
    	for(int i=0; i<leaves.size(); i++){
    		HashSet s = leaves.get(i).getData().get(c+1);
    		//only parse single state
    		if(s.size()==1){
    			 Iterator iterator=s.iterator();
    			 while(it.hasNext()) {
    			    int stateNum = it.next();
    			    }
    			if(currentCounter.get(stateNum)==null){
    				currentCounter.put(stateNum, 1);
    			}
    			else
    				currentCounter.put(stateNum, currentCounter.get(stateNum)+1);
    		}
    	}
    	return currentCounter;
    }
    
    //compute must-have state for leaves
    public void computeBiLeaf(int c){
    	for(BniNetNode<T> node : network.getLeaves()){
			hasChecked.add(node.getName());
    		HashSet s = node.getData().get(c+1);
    		//single leaf state
    		if(s.size()==1){
    			Iterator it = s.iterator();
    			int state = it.next();
    			if(stateCounter.get(state)>1)
    				mustHave.put(node.getName(),state);
    				Map<Integer,Integer> temp = new Map<Integer,Integer>();
    				temp.add(state,1);
    				Bi.put(node.getName(), temp);
    		}
    	}
    }
    
  //compute must-have state for non-leaf
    public void computeBiNonLeaf(int c, BniNetNode node){
    	Iterator it = node.getChildren();
		Map<Integer,Integer> tempCounter = new Map<Integer,Integer>();
		while(it.hasNext()) {
			BniNetNode<T> child = it.next();
			if(!hasChecked.contains(child.getName())){
				computeBiNonLeaf(network, c,child);
			}
			Map temp = Bi.get(child.getName());// child's must have maps
			Set states = temp.keySet();// states.size()>0, compatibility=false
			Iterator it = states.iterator();
			 	while(it.hasNext()) {
			 		int stateNum = it.next();
			 		int stateCount = temp.get(stateNum);
			 		if (tempCounter.containsKey(stateNum)){
			 			tempCounter.get(stateNum)++;
			 		}
			 		else
			 			tempCounter.put(stateNum, stateCount);
			    }
		}
		Set states = tempCounter.keySet();// states.size()>0, compatibility=false
		Iterator it = states.iterator();
		Map<Integer, Integer> temp;
		while(it.hasNext()) {
		 		int stateNum = it.next();
		 		int stateCount = tempCounter.get(stateNum);
		 		if (stateCount < stateCounter.get(stateNum)){
		 			temp.put(stateNum, stateCount);
		 		}
		}
		Bi.put(node.getName(), temp);
		if(temp.size()>1)
			compatibility = false;
		//TODO if sttates>1 , compatibility=false;
    }
    
  //compute must-have state set for a network
    public void computeBi(int c){
    	computeBiLeaf(network);
    	BniNetNode<T> node = network.getRoot();
    	computeBiNonLeaf(network, c, node);
    }
    
    /*
    //TODO:delete this func
    //return all B[i] -> states that non-leaf node_i must have
    public List getB(BniNetwork<T> network, int c){
    	List<BniNetNode> leaves = network.getLeaves();
    	Map currentCounter = countState(network, c);
    	for(int i=0; i<leaves.size(); i++){
    		HashSet s = leaves.get(i).getData().get(c+1);
    		//only parse single state
    		if(s.size()==1){
    			 Iterator it=s.iterator();
    			 while(it.hasNext()) {
    			    int stateNum = it.next();
    			    }
    			if(currentCounter.get(stateNum)==null){
    				currentCounter.put(stateNum, 1);
    			}
    			else
    				currentCounter.put(stateNum, currentCounter.get(stateNum)+1);
    		}
    	}
    }*/
    
    
	public <T> boolean isTreeCompatible(BniNetwork<T> network, int c){
		Map<Integer,Integer> stateCounter = countState(c);
		//TODO getBi -> return compatibility
		computeBi(c);
		Set<String> nodes = Bi.keySet();
		Iterator it = nodes.iterator();
		boolean cComp = true;
		while(it.hasNext()) {
			String name = it.next();
			List l = Bi.get(name);
			if(l.size()>1)
				cComp = false;
		}
		compatibility[c] = cComp;
		return cComp;
		
	}

	//TODO just like isTreeC
	public <T> boolean isNetworkCompatible(BniNetwork<T> network, int c){
		Map<Integer,Integer> stateCounter = countState(c);
		//TODO getBi -> return compatibility
		computeBi(c);
		Set<String> nodes = Bi.keySet();
		Iterator it = nodes.iterator();
		boolean cComp = true;
		while(it.hasNext()) {
			String name = it.next();
			List l = Bi.get(name);
			if(l.size()>1)
				cComp = false;
		}
		compatibility[c] = cComp;
		return cComp;
		
	}

	//cSet-> character set, return compatible characters set
	public <T> String[] TreeCompatible(BniNetwork<T> network, List cSet) {

	}


	//cSet-> character set, return compatible characters set
	public <T> String[] NetworkCompatible(BniNetwork<T> network, List cSet) {

	}



}
