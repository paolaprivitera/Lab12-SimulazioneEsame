package it.polito.tdp.model;

import java.time.Year;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.db.EventsDao;

public class Model {

	private EventsDao dao;
	private SimpleWeightedGraph<Integer, DefaultWeightedEdge> grafo;
	private List<Integer> vertici;
	
	public Model() {
		dao = new EventsDao();
	}
	
	public List<Year> getAnni() {
		return dao.getYears();
	}

	public void creaGrafo(Year anno) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		vertici = dao.getVertici(anno);
		
		Graphs.addAllVertices(this.grafo, this.vertici);
		
		for(Integer v1 : this.grafo.vertexSet()) {
			for(Integer v2 : this.grafo.vertexSet()) {
				if(!v1.equals(v2)) { // metodo equals perche' sto usando la classe Integer
					if(this.grafo.getEdge(v1,v2) == null) {
						// vuol dire che l'arco non c'e' ancora e lo posso aggiungere
						Double latMediaV1 = dao.getLatMedia(anno, v1) ;
						Double lonMediaV1 = dao.getLonMedia(anno, v1);
						
						Double latMediaV2 = dao.getLatMedia(anno, v2);
						Double lonMediaV2 = dao.getLonMedia(anno, v2);
					
						Double distanzaMedia = LatLngTool.distance(
								new LatLng(latMediaV1,lonMediaV1),
								new LatLng(latMediaV2,lonMediaV2),
								LengthUnit.KILOMETER);
						
						Graphs.addEdgeWithVertices(this.grafo, v1, v2, distanzaMedia);
						
					}
					
				}
			}
		}
		
		System.out.println("Grafo creato!");
		System.out.println("Vertici: " + grafo.vertexSet().size());
		System.out.println("Archi: " + grafo.edgeSet().size());
		
	}
	
	public List<Vicino> getVicini(Integer distretto) { // metodo che richiamo per tutti i vertici
		List<Vicino> vicini = new LinkedList<Vicino>();										   // presenti nel grafo dal controllore
		List<Integer> neighbors = Graphs.neighborListOf(this.grafo, distretto);
		
		for(Integer n : neighbors) {
			vicini.add(new Vicino(n, this.grafo.getEdgeWeight(this.grafo.getEdge(distretto, n))));
		}
		
		Collections.sort(vicini);
		
		return vicini;
	}
	
	public List<Integer> getDistretti(Year anno) {
		return dao.getVertici(anno);
	}
	
	public void simula(Integer anno, Integer mese, Integer giorno, Integer N) {
		Simulatore sim = new Simulatore();
		sim.init(N, anno, mese, giorno, grafo);
		sim.run();
		
	}
}
