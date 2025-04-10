package com.practica.ems.covid;


import java.util.LinkedList;

import com.practica.excecption.EmsDuplicateLocationException;
import com.practica.excecption.EmsLocalizationNotFoundException;
import com.practica.genericas.FechaHora;
import com.practica.genericas.PosicionPersona;

public class Localizacion {
	LinkedList<PosicionPersona> lista;

	public Localizacion() {
		super();
		this.lista = new LinkedList<>();
	}
	
	public LinkedList<PosicionPersona> getLista() {
		return lista;
	}

	public void addLocalizacion (PosicionPersona p) throws EmsDuplicateLocationException {
		try {
			findLocalizacion(p.getDocumento(), p.getFechaPosicion().getFecha().toString(),p.getFechaPosicion().getHora().toString() );
			throw new EmsDuplicateLocationException();
		}catch(EmsLocalizationNotFoundException e) {
			lista.add(p);
		}
	}
	
	public int findLocalizacion (String documento, String fecha, String hora) throws EmsLocalizationNotFoundException {
	    int cont = 0;
        for (PosicionPersona posicionPersona : lista) {
            cont++;
            FechaHora fechaHora = this.parsearFecha(fecha, hora);
            if (posicionPersona.getDocumento().equals(documento) &&
                    posicionPersona.getFechaPosicion().equals(fechaHora)) {
                return cont;
            }
        }
	    throw new EmsLocalizationNotFoundException();
	}

	@Override
	public String toString() {
		StringBuilder cadena = new StringBuilder();
        for (PosicionPersona pp : this.lista) {
            cadena.append(String.format("%s;", pp.getDocumento()));
            FechaHora fecha = pp.getFechaPosicion();
            cadena.append(String.format("%02d/%02d/%04d;%02d:%02d;",
                    fecha.getFecha().getDia(),
                    fecha.getFecha().getMes(),
                    fecha.getFecha().getAnio(),
                    fecha.getHora().getHora(),
                    fecha.getHora().getMinuto()));
            cadena.append(String.format("%.4f;%.4f\n", pp.getCoordenada().getLatitud(),
                    pp.getCoordenada().getLongitud()));
        }
		
		return cadena.toString();
	}

	private  FechaHora parsearFecha (String fecha, String hora) {
		int dia, mes, anio;
		String[] valores = fecha.split("/");
		dia = Integer.parseInt(valores[0]);
		mes = Integer.parseInt(valores[1]);
		anio = Integer.parseInt(valores[2]);
        return ContactosCovid.getFechaHora(hora, dia, mes, anio);
    }
	
}
