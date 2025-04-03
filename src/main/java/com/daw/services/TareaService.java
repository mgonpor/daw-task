package com.daw.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.daw.persistence.entities.Tarea;
import com.daw.persistence.entities.enums.Estado;
import com.daw.persistence.repositories.TareaRepository;
import com.daw.services.exceptions.TareaException;
import com.daw.services.exceptions.TareaNotFoundException;

@Service
public class TareaService {
	
	@Autowired
	private TareaRepository tareaRepository;

	public List<Tarea> findAll(){		
        return tareaRepository.findAll();
	}
		
	public Tarea findById (int idTarea) {
		if(!this.tareaRepository.existsById(idTarea)) {
			throw new TareaNotFoundException("La tarea con id " + idTarea + " no existe");
		}
		return this.tareaRepository.findById(idTarea).get();
	}	

	public boolean existsById(int idTarea) {
		return this.tareaRepository.existsById(idTarea);
	}
	
	public void deleteById (int idTarea) {
		if(!this.tareaRepository.existsById(idTarea)) {
			throw new TareaNotFoundException("La tarea con id " + idTarea + " no existe");
		}
		this.tareaRepository.deleteById(idTarea);
	}

		
	public Tarea create (Tarea tarea) {
		tarea.setFechaCreacion(LocalDate.now());
		tarea.setEstado(Estado.PENDIENTE);
		return this.tareaRepository.save(tarea);
	}
	

	public Tarea update(int idTarea, Tarea tarea) {
		if(idTarea != tarea.getId()) {
			throw new TareaException("El id del Path ("+ idTarea +") es distinto del id del body ("+tarea.getId()+")");
		}
		if(!existsById(idTarea)) {
			throw new TareaNotFoundException("No se ha encontrado la tarea con ID "+ idTarea);
		}
		if(tarea.getEstado() != null || tarea.getFechaCreacion() != null) {
			throw new TareaException("No se permite modificar la fecha de creación y/o el estado");
		}
		
		Tarea tareaBD = this.findById(tarea.getId());
		tarea.setFechaCreacion(tareaBD.getFechaCreacion());
		tarea.setEstado(tareaBD.getEstado());
		return this.tareaRepository.save(tarea);
	}
}
