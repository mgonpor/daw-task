package com.daw.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
		if(tarea.getFechaVencimiento().isBefore(LocalDate.now())) {
			throw new TareaException("La fecha de vencimiento es anterior a la de creación (IMPOSIBLE)");
		}
		tarea.setFechaCreacion(LocalDate.now());
		tarea.setEstado(Estado.PENDIENTE);
		return this.tareaRepository.save(tarea);
	}
	

	public Tarea update(int idTarea, Tarea tarea) {
		if(idTarea != tarea.getId()) {
			throw new TareaException("El id del Path ("+ idTarea +") es distinto del id del body ("+tarea.getId()+")");
		}
		if(!this.existsById(idTarea)) {
			throw new TareaNotFoundException("No se ha encontrado la tarea con ID "+ idTarea);
		}
		if(tarea.getEstado() != null || tarea.getFechaCreacion() != null) {
			throw new TareaException("No se permite modificar la fecha de creación y/o el estado");
		}
		
		Tarea tareaBD = this.findById(tarea.getId());
		
		if(tarea.getFechaVencimiento().isBefore(tareaBD.getFechaCreacion())) {
			throw new TareaException("La fecha de vencimiento es anterior a la de creación (IMPOSIBLE)");
		}
		
		tarea.setFechaCreacion(tareaBD.getFechaCreacion());
		return this.tareaRepository.save(tarea);
	}
	
	public Tarea iniciar(int idTarea) {
		if(!this.existsById(idTarea)) {
			throw new TareaNotFoundException("La tarea con id " + idTarea + " no existe");
		}
		if(this.findById(idTarea).getEstado() != Estado.PENDIENTE) {
			throw new TareaException("La tarea con id " + idTarea + " no se puede iniciar. No está 'PENDIENTE'");
		}
		Tarea tareaDB = this.findById(idTarea);
		tareaDB.setEstado(Estado.EN_PROGRESO);
		return this.tareaRepository.save(tareaDB);
	}
	
	public Tarea completar(int idTarea) {
		if(!this.existsById(idTarea)) {
			throw new TareaNotFoundException("La tarea con id " + idTarea + " no existe");
		}
		if(this.findById(idTarea).getEstado() != Estado.EN_PROGRESO) {
			throw new TareaException("La tarea con id " + idTarea + " no se puede completar. No está 'EN_PROGRESO'");
		}
		Tarea tareaDB = this.findById(idTarea);
		tareaDB.setEstado(Estado.COMPLETADA);
		return this.tareaRepository.save(tareaDB);
	}
	
	public List<Tarea> findPendientes(){
		return this.tareaRepository.findAll().stream()
				.filter(t -> t.getEstado() == Estado.PENDIENTE)
				.collect(Collectors.toList());
	}
	
	public List<Tarea> findEnProgreso(){
		return this.tareaRepository.findAll().stream()
				.filter(t -> t.getEstado() == Estado.EN_PROGRESO)
				.collect(Collectors.toList());
	}
	
	public List<Tarea> findCompletadas(){
		return this.tareaRepository.findAll().stream()
				.filter(t -> t.getEstado() == Estado.COMPLETADA)
				.collect(Collectors.toList());
	}
	
	public List<Tarea> findVencidas(){
		return this.tareaRepository.findAll().stream()
				.filter(t -> t.getFechaVencimiento().isBefore(LocalDate.now()))
				.collect(Collectors.toList());
	}
	
	public List<Tarea> findNoVencidas(){
		return this.tareaRepository.findAll().stream()
				.filter(t -> t.getFechaVencimiento().isAfter(LocalDate.now()))
				.collect(Collectors.toList());
	}
	
	public List<Tarea> findPorTitulo(String titulo){
		return this.tareaRepository.findAll().stream()
				.filter(t -> t.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
				.collect(Collectors.toList());
	}
	
//	Reals
	public long totalTareasCompletadas() {
		return this.tareaRepository.countByEstado(Estado.COMPLETADA);
	}
	
	public List<LocalDate> fechasVencimientoProgreso(){
		return this.tareaRepository.findByEstado(Estado.EN_PROGRESO).stream()
				.map(t -> t.getFechaVencimiento())
				.collect(Collectors.toList());
	}
	
	public List<Tarea> tareasVencidas(){
		return this.tareaRepository.findByFechaVencimientoBefore(LocalDate.now());
	}
	
	public List<String> titulosPendientes(){
		return this.tareaRepository.findByEstado(Estado.PENDIENTE).stream()
				.map(t -> t.getTitulo())
				.collect(Collectors.toList());
	}
	
	public List<Tarea> ordenadasVencimientoMenorMayor(){
		return this.tareaRepository.findAllByOrderByFechaVencimiento();
	}
	
//	------------------------------------------------------------------------------
	
//	Optional
	
	public Tarea findByIdFuncional(int idTarea) {
		return this.tareaRepository.findById(idTarea)
			.orElseThrow( () -> new TareaNotFoundException("Tarea no encontrada"));
	}	
	
	public boolean deleteFuncional(int idTarea) {
		return this.tareaRepository.findById(idTarea)
			.map( t -> {
				this.tareaRepository.deleteById(idTarea);
				return true;
			})
			.orElse(false);
	}
	
	
//	Stream
	
//	Obtener el número total de tareas completadas.
	public long totalTareasCompletadasFunc() {
		return this.tareaRepository.findAll().stream()
				.filter(t -> t.getEstado() == Estado.COMPLETADA)
				.count();
	}
	
//	Obtener una lista de las fechas de vencimiento de las tareas que estén en progreso.
	public List<LocalDate> fechasVencimientoProgresoFunc(){
		return this.tareaRepository.findAll().stream()
				.filter(t -> t.getEstado() == Estado.EN_PROGRESO)
				.map(t -> t.getFechaVencimiento())
				.collect(Collectors.toList());
	}
	
//	Obtener las tareas vencidas.
	public List<Tarea> tareasVencidasFunc(){
		return this.tareaRepository.findAll().stream()
				.filter(t -> t.getFechaVencimiento().isBefore(LocalDate.now()))
				.collect(Collectors.toList());
	}
	
//	Obtener los títulos de tareas pendientes.
	public List<String> titulosPendientesFunc(){
		return this.tareaRepository.findAll().stream()
				.filter(t -> t.getEstado() == Estado.PENDIENTE)
				.map(t -> t.getTitulo())
				.collect(Collectors.toList());
	}
	
//	Ordenar tareas por fecha de vencimiento.
	public List<Tarea> ordenadasVencimientoMenorMayorFunc(){
		return this.tareaRepository.findAll().stream()
				.sorted((t1, t2) -> t1.getFechaVencimiento().compareTo(t2.getFechaVencimiento()))
				.collect(Collectors.toList());
	}
	
	
	
}
