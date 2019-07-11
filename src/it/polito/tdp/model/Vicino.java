package it.polito.tdp.model;

public class Vicino implements Comparable<Vicino>{

	private Integer vicino;
	private Double distanza;
	
	public Vicino(Integer vicino, Double distanza) {
		this.vicino = vicino;
		this.distanza = distanza;
	}

	public Integer getVicino() {
		return vicino;
	}

	public void setVicino(Integer vicino) {
		this.vicino = vicino;
	}

	public Double getDistanza() {
		return distanza;
	}

	public void setDistanza(Double distanza) {
		this.distanza = distanza;
	}

	@Override
	public int compareTo(Vicino altro) {
		
		return this.distanza.compareTo(altro.distanza);
	}
	
	
	
}
