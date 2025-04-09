package com.daw.web.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daw.persistence.entities.Tarea;
import com.daw.services.TareaService;
import com.daw.services.exceptions.TareaException;
import com.daw.services.exceptions.TareaNotFoundException;

@RestController
@RequestMapping("/tareas")
public class TareaController {

	@Autowired
	private TareaService tareaService;
	
	@GetMapping
	public ResponseEntity<List<Tarea>> list(){
		return ResponseEntity.ok(this.tareaService.findAll());
//		return ResponseEntity.status(HttpStatus.OK).body(this.tareaService.findAll());
	}
	
	@GetMapping("/{idTarea}")
	public ResponseEntity<?> findById(@PathVariable int idTarea) {
		try {
			return ResponseEntity.ok(this.tareaService.findById(idTarea));
		}
		catch(TareaNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}
	}

	@DeleteMapping("/{idTarea}")
	public ResponseEntity<?> deleteById(@PathVariable int idTarea) {
		try {
			this.tareaService.deleteById(idTarea);
			return ResponseEntity.ok("Tarea eliminada con éxito");
		}
		catch(TareaNotFoundException ex) {	
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}
	}

	@PostMapping
	public ResponseEntity<Tarea> create(@RequestBody Tarea tarea) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.tareaService.create(tarea));	
	}

	@PutMapping("/{idTarea}")
	public ResponseEntity<?> update(@PathVariable int idTarea, @RequestBody Tarea tarea) {
		try {
			return ResponseEntity.ok().body(this.tareaService.update(idTarea, tarea));
		}
		catch(TareaNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}
		catch(TareaException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		}
	}
	
	@PutMapping("/iniciar/{idTarea}")
	public ResponseEntity<?> iniciar(@PathVariable int idTarea){
		try {
			return ResponseEntity.ok(this.tareaService.iniciar(idTarea));
		}
		catch(TareaNotFoundException ex){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}
		catch(TareaException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		}
	}
	
	@PutMapping("/completar/{idTarea}")
	public ResponseEntity<?> completar(@PathVariable int idTarea){
		try {
			return ResponseEntity.ok(this.tareaService.completar(idTarea));
		}
		catch(TareaNotFoundException ex){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}
		catch(TareaException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		}
	}
	
	@GetMapping("/pendientes")
	public ResponseEntity<List<Tarea>> findPendientes() {
		return ResponseEntity.ok(this.tareaService.findPendientes());
	}
	
	@GetMapping("/en_progreso")
	public ResponseEntity<List<Tarea>> findEnProgreso() {
		return ResponseEntity.ok(this.tareaService.findEnProgreso());
	}
	
	@GetMapping("/completadas")
	public ResponseEntity<List<Tarea>> findCompletadas() {
		return ResponseEntity.ok(this.tareaService.findCompletadas());
	}
	
	@GetMapping("/vencidas")
	public ResponseEntity<List<Tarea>> findVencidas(){
		return ResponseEntity.ok(this.tareaService.findVencidas());
	}
	
	@GetMapping("/no_vencidas")
	public ResponseEntity<List<Tarea>> findNoVencidas(){
		return ResponseEntity.ok(this.tareaService.findNoVencidas());
	}
	
	@GetMapping("/buscar")
	public ResponseEntity<List<Tarea>> findPorTitulo(@RequestParam String titulo){
		return ResponseEntity.ok(this.tareaService.findPorTitulo(titulo));
	}
	
}
