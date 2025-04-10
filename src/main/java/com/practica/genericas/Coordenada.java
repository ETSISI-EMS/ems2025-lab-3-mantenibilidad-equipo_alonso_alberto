package com.practica.genericas;


public record Coordenada(float latitud, float longitud) {


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordenada other = (Coordenada) obj;
		if (Float.floatToIntBits(latitud) != Float.floatToIntBits(other.latitud))
			return false;
		return Float.floatToIntBits(longitud) == Float.floatToIntBits(other.longitud);
	}

}
