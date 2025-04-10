package com.practica.genericas;


import java.time.LocalDateTime;

public class FechaHora implements Comparable<FechaHora>{
	public record Fecha(int dia, int mes, int anio) {


		@Override
			public String toString() {
			return String.format("%2d/%02d/%4d", dia, mes, anio);
			}


	}

	public record Hora(int hora, int minuto) {


		@Override
			public String toString() {
				return String.format("%02d:%02d", hora, minuto);
			}


	}

	Fecha fecha;
	Hora hora;

	public FechaHora(int dia, int mes, int anio, int hora, int minuto) {
		this.fecha = new Fecha(dia, mes, anio);
		this.hora = new Hora(hora, minuto);
	}

	public Fecha getFecha() {
		return fecha;
	}

	public Hora getHora() {
		return hora;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fecha == null) ? 0 : fecha.hashCode());
		result = prime * result + ((hora == null) ? 0 : hora.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FechaHora fecha = (FechaHora) obj;
		return getFecha().dia() == fecha.getFecha().dia() && getFecha().mes() == fecha.getFecha().mes()
				&& getFecha().anio() == fecha.getFecha().anio()
				&& getHora().hora() == fecha.getHora().hora()
				&& getHora().minuto() == fecha.getHora().minuto();
	}

	@Override
	public int compareTo(FechaHora o) {
		LocalDateTime dateTime1= LocalDateTime.of(this.getFecha().anio(), this.getFecha().mes(), this.getFecha().dia(),
				this.getHora().hora(), this.getHora().minuto());
		LocalDateTime dateTime2= LocalDateTime.of(o.getFecha().anio(), o.getFecha().mes(), o.getFecha().dia(),
				o.getHora().hora(), o.getHora().minuto());
		
		return dateTime1.compareTo(dateTime2);
	}
	
	
}
