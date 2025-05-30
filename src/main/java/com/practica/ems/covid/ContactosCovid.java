package com.practica.ems.covid;


import java.io.BufferedReader;
import java.io.File;
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
			esTipoValido(datos);
		}
	}

	public void loadDataFile(String fichero, boolean reset) {
        loadDataFile(fichero, reset, null);
	}

	@SuppressWarnings("resource")
	public void loadDataFile(String fichero, boolean reset, FileReader fr) {
		try {
			// Apertura del fichero y creación de BufferedReader para poder
			// hacer una lectura cómoda (disponer del metodo readLine()).
			File archivo = new File(fichero);
			fr = new FileReader(archivo);
			BufferedReader br = new BufferedReader(fr);
			if (reset) {
				this.poblacion = new Poblacion();
				this.localizacion = new Localizacion();
				this.listaContactos = new ListaContactos();
			}
            String data;
			while ((data = br.readLine()) != null) {
				String[] datas = dividirEntrada(data.trim());
				for (String linea : datas) {
					String[] datos = this.dividirLineaData(linea);
					esTipoValido(datos);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// En el finally cerramos el fichero, para asegurarnos
			// que se cierra tanto si todo va bien como si salta
			// una excepción.
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	private void esTipoValido(String[] datos) throws EmsInvalidTypeException, EmsInvalidNumberOfDataException, EmsDuplicatePersonException, EmsDuplicateLocationException {
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
				throw new EmsInvalidNumberOfDataException(
						"El número de datos para LOCALIZACION es menor de 6" );
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


	private Persona crearPersona(String[] data) {
		Persona persona = new Persona();
		if (data.length == Constantes.MAX_DATOS_PERSONA) {
			persona.setDocumento(data[1]);
			persona.setNombre(data[2]);
			persona.setApellidos(data[3]);
			persona.setEmail(data[4]);
			persona.setDireccion(data[5]);
			persona.setCp(data[6]);
			if (data[7] != null) {
				persona.setFechaNacimiento(parsearFecha(data[7]));
			}
		}
		return persona;
	}

	private PosicionPersona crearPosicionPersona(String[] data) {
		PosicionPersona posicionPersona = new PosicionPersona();
		if (data.length == 6) {
			String documento = data[1];
			posicionPersona.setDocumento(documento);

			String fecha = data[2];
			String hora = data[3];

			if (fecha != null && hora != null) {
				posicionPersona.setFechaPosicion(parsearFecha(fecha, hora));
			}

			float latitud = Float.parseFloat(data[4]);
			float longitud = Float.parseFloat(data[5]);

			posicionPersona.setCoordenada(new Coordenada(latitud, longitud));

		}

		return posicionPersona;
	}
	
	private FechaHora parsearFecha (String fecha) {
		return getFechaHora(fecha);
	}

	// replace lines 274 to 276
	private FechaHora getFechaHora(String fecha) {
		int dia, mes, anio;
		String[] valores = fecha.split("/");
		dia = Integer.parseInt(valores[0]);
		mes = Integer.parseInt(valores[1]);
		anio = Integer.parseInt(valores[2]);
		return new FechaHora(dia, mes, anio, 0, 0);
	}

	private FechaHora parsearFecha (String fecha, String hora) {
		int dia, mes, anio;
		String[] valores = fecha.split("/");
		dia = Integer.parseInt(valores[0]);
		mes = Integer.parseInt(valores[1]);
		anio = Integer.parseInt(valores[2]);
		return getFechaHora(hora, dia, mes, anio);
	}

	static FechaHora getFechaHora(String hora, int dia, int mes, int anio) {
		String[] valores;
		int minuto, segundo;
		valores = hora.split(":");
		minuto = Integer.parseInt(valores[0]);
		segundo = Integer.parseInt(valores[1]);
        return new FechaHora(dia, mes, anio, minuto, segundo);
	}
}
