package de.unipotsdam.anh.reflexion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import uzuzjmd.competence.shared.dto.Graph;
import uzuzjmd.competence.shared.dto.GraphNode;
import uzuzjmd.competence.shared.dto.GraphTriple;
import uzuzjmd.competence.shared.dto.LearningTemplateResultSet;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

import de.unipotsdam.anh.dao.LearningTemplateDao;

@ManagedBean(name = "templateCompetenceView")
@SessionScoped
public class TemplateCompetenceView implements Serializable{

	private static final long serialVersionUID = 1L;
	private String learnProject;
	
	private Map<String, List<String>> competencen;
	
	private List<String> catchWords;
	
	private String newCatchWord;
	private String newCompetence;
	
	private String selectedCatchword;
	
	private String selectedCompetenceToNode;
	private String selectedCompetenceFromNode;
	
	private LearningTemplateResultSet learningTemplateResultSet;
	
	private Map<String, List<TreeNode>> treeNodeMap;

	@PostConstruct
	public void init() {
		learningTemplateResultSet = LearningTemplateDao.getLearningProjectTemplate("TestLernprojekt");
		treeNodeMap = new HashMap<String, List<TreeNode>>();
		competencen = new HashMap<String, List<String>>();
		catchWords = new ArrayList<String>();
		
		updateListAndMap();
	}
	
	private void updateListAndMap() {
		for(Entry<GraphTriple, String[]> entry : learningTemplateResultSet.getCatchwordMap().entrySet()) {
			for(String catchword : entry.getValue()) {
				if(!catchWords.contains(catchword)) {
					catchWords.add(catchword);
					treeNodeMap.put(catchword, getTreeForCatchword(catchword));
				}	
			}
		}
	}

	public void update(String learnProject) {
		this.learnProject = learnProject;
		learningTemplateResultSet = LearningTemplateDao.getLearningProjectTemplate(learnProject);
		updateListAndMap();
	}
	
	private List<TreeNode> getTreeForCatchword(String catchword) {
		Graph graph = getGraphForCatchword(catchword);
		System.out.println("########");
		System.out.println("graph: " + graph.toString());
		System.out.println("########");
		Map<Integer, DefaultTreeNode> nodes = new HashMap<Integer, DefaultTreeNode>();
		for(GraphNode n : graph.nodes) {
			DefaultTreeNode node = new DefaultTreeNode(n.getLabel());
			node.setExpanded(true);
			nodes.put(n.getId(), node);
		}
		List<TreeNode> roots = new ArrayList<TreeNode>();
		for(GraphTriple t : graph.triples) {
//			if(roots.contains(nodes.get(t.getNode2id()))) {
//				roots.add(nodes.get(t.getNode1id()));
			
			roots.remove(nodes.get(t.getNode2id()));
//			if(!roots.contains(nodes.get(t.getNode1id()))){
				roots.add(nodes.get(t.getNode1id()));
//			}
			nodes.get(t.getNode1id()).getChildren().add(nodes.get(t.getNode2id()));
			
			System.out.println(t.fromNode + "---" + t.toNode);
		}

		return roots;
	}

	private Graph getGraphForCatchword(String catchword) {
		Graph graph = new Graph();
		for(Entry<GraphTriple, String[]> entry : learningTemplateResultSet.getCatchwordMap().entrySet()) {
			System.out.println(Arrays.asList(entry.getValue()) + "-----" + catchword);
			System.out.println(entry.getKey().toString());
			if(Arrays.asList(entry.getValue()).contains(catchword)) {
				GraphTriple triple = entry.getKey();
				graph.addTriple(triple.fromNode, triple.toNode, triple.label, triple.directed);
			}
		}
		return graph;
	}

	public void addNewCatchWord(ActionEvent e) {
		catchWords.add(newCatchWord);
	}
	
	public void addNewCompetence(ActionEvent e) {
		final List<String> newCompetencen = new ArrayList<String>();
		newCompetencen.add(newCompetence);
		
		List<String> competencenFromCatchWord = competencen.get(selectedCatchword);
		if(competencenFromCatchWord == null) {
			competencen.put(selectedCatchword, newCompetencen);
		} else {
			competencenFromCatchWord.addAll(newCompetencen);
		}
	}
	
	public void selecteCatchWord(String catchWord) {
		this.selectedCatchword = catchWord;
		System.out.println(selectedCatchword);
	}
	
	public void selecteCompetence(String competence) {
		this.selectedCompetenceFromNode = competence;
		System.out.println(selectedCompetenceFromNode);
	}
	
	public List<String> complete(String query) {
		final List<String> results = new ArrayList<String>();
        final List<String> dbCompetencen = new ArrayList<String>();
        
        for(Entry<String, List<String>> entry : competencen.entrySet()) {
        	dbCompetencen.addAll(entry.getValue());
        }
        
		final Collection<String> tmp = Collections2.filter(dbCompetencen, Predicates.containsPattern(query));	
		results.addAll(tmp);
		
        return results;
	}
	
	public void branchCompetenceAction(ActionEvent e) {

	}

	public String getLearnProject() {
		return learnProject;
	}

	public void setLearnProject(String learnProject) {
		this.learnProject = learnProject;
	}

	public List<String> getCatchWords() {
		return catchWords;
	}

	public void setCatchWords(List<String> catchWords) {
		this.catchWords = catchWords;
	}

	public String getNewCatchWord() {
		return newCatchWord;
	}

	public void setNewCatchWord(String newCatchWord) {
		this.newCatchWord = newCatchWord;
	}

	public String getNewCompetence() {
		return newCompetence;
	}

	public void setNewCompetence(String newCompetence) {
		this.newCompetence = newCompetence;
	}

	public Map<String, List<String>> getCompetencen() {
		return competencen;
	}

	public void setCompetencen(Map<String, List<String>> competencen) {
		this.competencen = competencen;
	}

	public String getSelectedCatchword() {
		return selectedCatchword;
	}

	public void setSelectedCatchword(String selectedCatchword) {
		this.selectedCatchword = selectedCatchword;
	}

	public String getSelectedCompetenceToNode() {
		return selectedCompetenceToNode;
	}

	public void setSelectedCompetenceToNode(String selectedCompetenceToNode) {
		this.selectedCompetenceToNode = selectedCompetenceToNode;
	}

	public String getSelectedCompetenceFromNode() {
		return selectedCompetenceFromNode;
	}

	public void setSelectedCompetenceFromNode(String selectedCompetenceFromNode) {
		this.selectedCompetenceFromNode = selectedCompetenceFromNode;
	}

	public LearningTemplateResultSet getLearningTemplateResultSet() {
		return learningTemplateResultSet;
	}

	public void setLearningTemplateResultSet(
			LearningTemplateResultSet learningTemplateResultSet) {
		this.learningTemplateResultSet = learningTemplateResultSet;
	}

	public Map<String, List<TreeNode>> getTreeNodeMap() {
		return treeNodeMap;
	}

	public void setTreeNodeMap(Map<String, List<TreeNode>> treeNodeMap) {
		this.treeNodeMap = treeNodeMap;
	}
}
