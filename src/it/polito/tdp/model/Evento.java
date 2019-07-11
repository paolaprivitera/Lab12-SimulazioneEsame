package it.polito.tdp.model;

import java.time.LocalDateTime;

public class Evento implements Comparable<Evento>{

	public enum TipoEvento{
		CRIMINE,
		ARRIVA_AGENTE,
		GESTITO		
	}
	
	// Campo sul tipo di evento
	private TipoEvento tipo;
	
	// Campo su cui si fara' l'ordinamento (ad esempio il tempo)
	private LocalDateTime data;
	
	// Campo di cui mi servono le informazioni
	private Event crimine;

	public Evento(TipoEvento tipo, LocalDateTime data, Event crimine) {
		this.tipo = tipo;
		this.data = data;
		this.crimine = crimine;
	}

	public TipoEvento getTipo() {
		return tipo;
	}

	public void setTipo(TipoEvento tipo) {
		this.tipo = tipo;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

	public Event getCrimine() {
		return crimine;
	}

	public void setCrimine(Event crimine) {
		this.crimine = crimine;
	}

	@Override
	public int compareTo(Evento other) {
	
		return this.data.compareTo(other.getData());
	}
	
	
	
}
