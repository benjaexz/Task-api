package com.example.task_api.service;

import com.example.task_api.dto.TarefaRequestDTO;
import com.example.task_api.dto.TarefaResponseDTO;
import com.example.task_api.entity.Tarefa;
import com.example.task_api.exception.ResourceNotFoundException;
import com.example.task_api.repository.TarefaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarefaService {

    private final TarefaRepository repository;

    public TarefaService(TarefaRepository repository) {
        this.repository = repository;
    }

    public List<TarefaResponseDTO> listarTodas() {
        return repository.findAll().stream()
                .map(TarefaResponseDTO::new)
                .toList();
    }

    public TarefaResponseDTO buscarPorId(Long id) {
        Tarefa tarefa = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com ID: " + id));
        return new TarefaResponseDTO(tarefa);
    }

    public TarefaResponseDTO salvar(TarefaRequestDTO dto) {
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(dto.titulo());
        tarefa.setDescricao(dto.descricao());
        tarefa.setConcluida(dto.concluido());

        Tarefa salva = repository.save(tarefa);
        return new TarefaResponseDTO(salva);
    }

    public TarefaResponseDTO atualizar(Long id, TarefaRequestDTO dto) {
        Tarefa tarefa = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com ID: " + id));

        tarefa.setTitulo(dto.titulo());
        tarefa.setDescricao(dto.descricao());
        tarefa.setConcluida(dto.concluido());

        Tarefa atualizada = repository.save(tarefa);
        return new TarefaResponseDTO(atualizada);
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Tarefa não encontrada com ID: " + id);
        }
        repository.deleteById(id);
    }
}