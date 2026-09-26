package com.example.task_api.dto;

import com.example.task_api.entity.Tarefa;

public record TarefaResponseDTO(
        Long id,
        String titulo,
        String descricao,
        boolean concluida
) {
    public TarefaResponseDTO(Tarefa tarefa) {
        this(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.isConcluida()
        );
    }
}