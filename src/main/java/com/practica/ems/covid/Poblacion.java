package com.practica.ems.covid;


import java.util.LinkedList;

import com.practica.excecption.EmsDuplicatePersonException;
import com.practica.excecption.EmsPersonNotFoundException;
import com.practica.genericas.FechaHora;
import com.practica.genericas.Persona;

public class Poblacion {
	LinkedList<Persona> lista ;

	public Poblacion() {
		super();
		this.lista = new LinkedList<>();
	}
	
	public LinkedList<Persona> getLista() {
		return lista;
	}

	public void addPersona (Persona persona) throws EmsDuplicatePersonException {
		try {
			findPersona(persona.getDocumento());
			throw new EmsDuplicatePersonException();
		} catch (EmsPersonNotFoundException e) {
			lista.add(persona);
		} 
	}

	public int findPersona (String documento) throws EmsPersonNotFoundException {
		int cont=0;
        for (Persona persona : lista) {
            cont++;
            if (persona.getDocumento().equals(documento)) {
                return cont;
            }
        }
		throw new EmsPersonNotFoundException();
	}

	@Override
	public String toString() {
		StringBuilder cadena = new StringBuilder();
        for (Persona persona : lista) {
            FechaHora fecha = persona.getFechaNacimiento();
            // Documento
            cadena.append(String.format("%s;", persona.getDocumento()));
            // nombre y apellidos
            cadena.append(String.format("%s,%s;", persona.getApellidos(), persona.getNombre()));
            // correo electrónico
            cadena.append(String.format("%s;", persona.getEmail()));
            // Direccion y código postal
            cadena.append(String.format("%s,%s;", persona.getDireccion(), persona.getCp()));
            // Fecha de nacimiento
            cadena.append(String.format("%02d/%02d/%04d\n", fecha.getFecha().getDia(),
                    fecha.getFecha().getMes(),
                    fecha.getFecha().getAnio()));
        }
		return cadena.toString();
	}
	
	
	
}
