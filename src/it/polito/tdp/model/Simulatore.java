package it.polito.tdp.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.db.EventsDao;
import it.polito.tdp.model.Evento.TipoEvento;

public class Simulatore {

	// TIPI DI EVENTO

	// 1. Evento Criminoso
	//	1.1 La centrale seleziona l'agente piu' vicino
	/*
	 * Per ogni nodo in cui ci sono degli agenti liberi,
	 * devo calcolare il CAMMINO MINIMO da quel nodo al nodo
	 * in cui c'e' il crimine
	 */
	/*
	 * Pero' poiche' il grafo e' completamente connesso, vale la
	 * disuguaglianza triangolare secondo cui e' meglio procedere
	 * con il cammino diretto (quindi vedere gli adiacenti di ogni nodo)
	 * quindi non e' necessario utilizzare i cammini minimi per ogni nodo
	 */
	//	1.2 Setta l'agente a occupato

	// 2. Arriva agente (nel luogo del crimine)
	//	2.1 Definisco quanto durera' l'intervento
	//	2.2 Controlla se l'evento e' mal gestito

	// 3. Crimine terminato (dopo 1 ora o 2)
	//	3.1 Libero l'agente

	// STRUTTURE DATI CHE CI SERVONO
	// Variabili in output
	private Integer malGestiti;

	// Parametri
	private Integer N;
	private Integer anno;
	private Integer mese;
	private Integer giorno;
	private Graph<Integer, DefaultWeightedEdge> grafo;
	private PriorityQueue<Evento> queue;

	// Siccome tra gli agenti liberi devo selezionare il piu' vicino
	// devo utilizzare una mappa
	// Mappa di distretto - # agenti(liberi)
	// cioe' quanti agenti ho per ogni distretto
	private Map<Integer, Integer> agenti;


	public void init(Integer N, Integer anno, Integer mese, Integer giorno,
			Graph<Integer, DefaultWeightedEdge> grafo) {

		// Setto i parametri
		this.N = N;
		this.anno = anno;
		this.mese = mese;
		this.giorno = giorno;

		// Setto il grafo
		this.grafo = grafo;

		// Inizializzo le strutture dati
		this.malGestiti = 0;
		this.agenti = new HashMap<Integer, Integer>();
		for(Integer d : this.grafo.vertexSet()) {
			this.agenti.put(d, 0);
		}

		// Scelgo dove sta la centrale con il minor crimine nell'anno in corso
		EventsDao dao = new EventsDao();
		Integer minD = dao.getDistrettoMin(anno); // devo fare una query
		this.agenti.put(minD, N);

		// Creo la coda
		this.queue = new PriorityQueue<Evento>();

		for(Event e : dao.listAllEventsByDate(this.anno, this.mese, this.giorno)) {
			queue.add(new Evento(TipoEvento.CRIMINE, e.getReported_date(), e));
		}

	}

	public void run() {
		Evento e;
		while((e = queue.poll()) != null) {

			switch(e.getTipo()) {

			case CRIMINE:
				System.out.println("NUOVO CRIMINE! "+e.getCrimine().getIncident_id());
				Integer partenza = null;
				partenza = cercaAgente(e.getCrimine().getDistrict_id());

				// Suppongo di avere una funzione che mi dia l'agente
				// e sulla base di quella controllo i casi limite

				if(partenza!=null) {
					// c'e' un agente libero

					if(partenza.equals(e.getCrimine().getDistrict_id())) {
						// tempo di arrivo = 0;
						// e' come se fossimo nel tipo ARRIVA_AGENTE
						// quindi copio il codice (senza il controllo del mal gestito perche'
						// e' ben gestito per costruzione)
						System.out.println("AGENTE ARRIVA PER CRIMINE: " + e.getCrimine().getIncident_id()+"\n");
						Long duration = getDuration(e.getCrimine().getOffense_category_id());
						this.queue.add(new Evento(TipoEvento.GESTITO, e.getData().plusSeconds(duration), e.getCrimine()));
					}
					else {
						Double distance = this.grafo.getEdgeWeight(this.grafo.getEdge(partenza,
								e.getCrimine().getDistrict_id()));
						Long seconds = (long) ((distance * 1000)/(60/3.6));
						this.queue.add(new Evento(TipoEvento.ARRIVA_AGENTE, e.getData().plusSeconds(seconds), e.getCrimine()));
					}
				}
				else {
					// nessun agente libero -> non specificato dal testo
					System.out.println("CRIMINE "+ e.getCrimine().getIncident_id()+ " GESTITO!\n");
					this.malGestiti++;
				}

				break;

			case ARRIVA_AGENTE:
				System.out.println("AGENTE ARRIVA PER CRIMINE: " + e.getCrimine().getIncident_id()+"\n");
				Long duration = getDuration(e.getCrimine().getOffense_category_id());
				this.queue.add(new Evento(TipoEvento.GESTITO, e.getData().plusSeconds(duration), e.getCrimine()));

				// Controllo se il crimine e' mal gestito
				if(e.getData().isAfter(e.getCrimine().getReported_date().plusMinutes(15))) {
					System.out.println("CRIMINE "+e.getCrimine().getIncident_id()+" MAL GESTITI\n");
					this.malGestiti++;
				}

				break;

			case GESTITO:
				System.out.println("CRIMINE "+ e.getCrimine().getIncident_id()+ " GESTITO!\n");
				this.agenti.put(e.getCrimine().getDistrict_id(),
						this.agenti.get(e.getCrimine().getDistrict_id())+1);

				break;

			}

		}
		
		System.out.println("TERMINATO!! MAL GESTITI = "+this.malGestiti);

	}

	private Integer cercaAgente(Integer district_id) {

		Double distanza = Double.MAX_VALUE; // numero sicuramente piu' grosso di tutte le distanze presenti nel grafo
		Integer distretto = null;

		for(Integer d : this.agenti.keySet()) {

			if(this.agenti.get(d) > 0) {
				if(district_id.equals(d)) {
					distanza = Double.valueOf(0);
					distretto = d;
				}
				else if(this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d)) < distanza) {
					distanza = this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d));
					distretto = d;
				}
			}
		}


		return null;
	}

	private Long getDuration(String offense_category_id) {

		if(offense_category_id.equals("all_other_crimes")) {
			Random r = new Random();
			if(r.nextDouble() > 0.5) // ritorna un numero tra 0 e 1 casuale
				return Long.valueOf(2*60*60); // in secondi
			else
				return Long.valueOf(1*60*60);
		}
		else 
			return Long.valueOf(2*60*60);
	}

}
