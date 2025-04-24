package com.practica.ems.covid;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.practica.excecption.EmsDuplicateLocationException;
import com.practica.excecption.EmsDuplicatePersonException;
import com.practica.excecption.EmsInvalidNumberOfDataException;
import com.practica.excecption.EmsInvalidTypeException;
import com.practica.excecption.EmsLocalizationNotFoundException;
import com.practica.excecption.EmsPersonNotFoundException;
import com.practica.genericas.Constantes;
import com.practica.genericas.Coordenada;
import com.practica.genericas.FechaHora;
import com.practica.genericas.Persona;
import com.practica.genericas.PosicionPersona;
import com.practica.lista.ListaContactos;

public class ContactosCovid {
	private Poblacion poblacion;
	private Localizacion localizacion;
	private ListaContactos listaContactos;

	public ContactosCovid() {
		this.poblacion = new Poblacion();
		this.localizacion = new Localizacion();
		this.listaContactos = new ListaContactos();
	}

	public Poblacion getPoblacion() {
		return poblacion;
	}

	public Localizacion getLocalizacion() {
		return localizacion;
	}

	public void setLocalizacion(Localizacion localizacion) {
		this.localizacion = localizacion;
	}

	public ListaContactos getListaContactos() {
		return listaContactos;
	}

	public void loadData(String data, boolean reset) throws EmsInvalidTypeException, EmsInvalidNumberOfDataException,
			EmsDuplicatePersonException, EmsDuplicateLocationException {
		// borro información anterior
		if (reset) {
			this.poblacion = new Poblacion();
			this.localizacion = new Localizacion();
			this.listaContactos = new ListaContactos();
		}
		String[] datas = dividirEntrada(data);
		for (String linea : datas) {
			String[] datos = this.dividirLineaData(linea);
			verifyData(datos);
		}
	}

	public void loadDataFile(String fichero, boolean reset) {
	    String[] datas;
	    String data;
	    try(BufferedReader br = new BufferedReader(new FileReader(fichero))) {

	        if (reset) {
				resetData();
	        }

	        while ((data = br.readLine()) != null) {
	            datas = dividirEntrada(data.trim());
	            for (String linea : datas) {
	                String[] datos = this.dividirLineaData(linea);
					verifyData(datos);
				}
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	private void resetData() {
		this.poblacion = new Poblacion();
		this.localizacion = new Localizacion();
		this.listaContactos = new ListaContactos();
	}

	private void verifyData(String[] datos) throws EmsInvalidTypeException, EmsInvalidNumberOfDataException, EmsDuplicatePersonException, EmsDuplicateLocationException {
		if (!datos[0].equals("PERSONA") && !datos[0].equals("LOCALIZACION")) {
			throw new EmsInvalidTypeException();
		}
		if (datos[0].equals("PERSONA")) {
			if (datos.length != Constantes.MAX_DATOS_PERSONA) {
				throw new EmsInvalidNumberOfDataException("El número de datos para PERSONA es menor de 8");
			}
			this.poblacion.addPersona(this.crearPersona(datos));
		}
		if (datos[0].equals("LOCALIZACION")) {
			if (datos.length != Constantes.MAX_DATOS_LOCALIZACION) {
				throw new EmsInvalidNumberOfDataException("El número de datos para LOCALIZACION es menor de 6");
			}
			PosicionPersona pp = this.crearPosicionPersona(datos);
			this.localizacion.addLocalizacion(pp);
			this.listaContactos.insertarNodoTemporal(pp);
		}
	}

	public int findPersona(String documento) throws EmsPersonNotFoundException {
		int pos;
		try {
			pos = this.poblacion.findPersona(documento);
			return pos;
		} catch (EmsPersonNotFoundException e) {
			throw new EmsPersonNotFoundException();
		}
	}

	public int findLocalizacion(String documento, String fecha, String hora) throws EmsLocalizationNotFoundException {

		int pos;
		try {
			pos = localizacion.findLocalizacion(documento, fecha, hora);
			return pos;
		} catch (EmsLocalizationNotFoundException e) {
			throw new EmsLocalizationNotFoundException();
		}
	}

	public List<PosicionPersona> localizacionPersona(String documento) throws EmsPersonNotFoundException {
		int cont = 0;
		List<PosicionPersona> lista = new ArrayList<>();
        for (PosicionPersona pp : this.localizacion.getLista()) {
            if (pp.getDocumento().equals(documento)) {
                cont++;
                lista.add(pp);
            }
        }
		if (cont == 0)
			throw new EmsPersonNotFoundException();
		else
			return lista;
	}

	public void delPersona(String documento) throws EmsPersonNotFoundException {
		int cont = 0, pos = -1;
        for (Persona persona : this.poblacion.getLista()) {
            if (persona.getDocumento().equals(documento)) {
                pos = cont;
            }
            cont++;
        }
		if (pos == -1) {
			throw new EmsPersonNotFoundException();
		}
		this.poblacion.getLista().remove(pos);
	}

	private String[] dividirEntrada(String input) {
        return input.split("\\n");
	}

	private String[] dividirLineaData(String data) {
        return data.split(";");
	}

	/*private Persona crearPersona(String[] data) {
	    Persona persona = new Persona();
		persona.setDocumento(data[0]);
		persona.setNombre(data[1]);
		persona.setApellidos(data[2]);
		persona.setEmail(data[3]);
		persona.setDireccion(data[4]);
		persona.setCp(data[5]);
		persona.setFechaNacimiento(parsearFecha(data[6]));
		return persona;
	}*/

	private Persona crearPersona(String[] datos) {
		return new Persona(datos[0], datos[1], datos[2], datos[3], datos[4], parsearFecha(datos[6]));
	}

	private PosicionPersona crearPosicionPersona(String[] data) {
        return new PosicionPersona(data[1], parsearFecha(data[2], data[3]), new Coordenada(Float.parseFloat(data[4]), Float.parseFloat(data[5])));
	}
	
	private FechaHora parsearFecha(String fecha) {
	    return parsearFecha(fecha, "");
	}

	private FechaHora parsearFecha(String fecha, String hora) {
	    int dia, mes, anio;
	    String[] valores = fecha.split("/");
	    dia = Integer.parseInt(valores[0]);
	    mes = Integer.parseInt(valores[1]);
	    anio = Integer.parseInt(valores[2]);
	    return construirFechaHora(dia, mes, anio, hora);
	}

	private FechaHora construirFechaHora(int dia, int mes, int anio, String hora) {
	    int minuto, segundo;
	    String[] valores = hora.split(":");
	    minuto = Integer.parseInt(valores[0]);
	    segundo = Integer.parseInt(valores[1]);
	    return new FechaHora(dia, mes, anio, minuto, segundo);
	}
}
