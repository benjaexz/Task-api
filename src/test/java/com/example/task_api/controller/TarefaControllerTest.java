package com.example.task_api.controller;

import com.example.task_api.dto.TarefaRequestDTO;
import com.example.task_api.dto.TarefaResponseDTO;
import com.example.task_api.exception.ResourceNotFoundException;
import com.example.task_api.service.TarefaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TarefaController.class)
class TarefaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TarefaService service;

    @Nested
    @DisplayName("GET /tarefas")
    class ListagemTests {

        @Test
        @DisplayName("Deve retornar status 200 OK com paginação")
        void deveListarComSucesso() throws Exception {
            TarefaResponseDTO dto = new TarefaResponseDTO(1L, "Estudar Spring", "Revisar testes", false);
            when(service.listarTodas(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1));

            mockMvc.perform(get("/tarefas")
                            .param("page", "0")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(1L))
                    .andExpect(jsonPath("$.content[0].titulo").value("Estudar Spring"))
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(service, times(1)).listarTodas(any(Pageable.class));
        }

        @Test
        @DisplayName("Deve retornar status 200 OK quando ID existir")
        void deveBuscarPorIdComSucesso() throws Exception {
            TarefaResponseDTO dto = new TarefaResponseDTO(1L, "Estudar Spring", "Revisar testes", false);
            when(service.buscarPorId(1L)).thenReturn(dto);

            mockMvc.perform(get("/tarefas/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.titulo").value("Estudar Spring"));

            verify(service, times(1)).buscarPorId(1L);
        }

        @Test
        @DisplayName("Deve retornar status 404 Not Found quando ID não existir")
        void deveRetornar404QuandoIdInexistente() throws Exception {
            when(service.buscarPorId(99L))
                    .thenThrow(new ResourceNotFoundException("Tarefa não encontrada com ID: 99"));

            mockMvc.perform(get("/tarefas/99")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).buscarPorId(99L);
        }
    }

    @Nested
    @DisplayName("POST /tarefas")
    class CriacaoTests {

        @Test
        @DisplayName("Deve retornar 201 Created quando payload for válido")
        void deveCriarTarefaComSucesso() throws Exception {
            TarefaRequestDTO requestDTO = new TarefaRequestDTO("Nova Tarefa", "Descricao", false);
            TarefaResponseDTO responseDTO = new TarefaResponseDTO(1L, "Nova Tarefa", "Descricao", false);

            when(service.salvar(any(TarefaRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(post("/tarefas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.titulo").value("Nova Tarefa"));

            verify(service, times(1)).salvar(any(TarefaRequestDTO.class));
        }

        @Test
        @DisplayName("Deve retornar 400 Bad Request quando título estiver em branco")
        void deveRetornar400QuandoTituloEmBranco() throws Exception {
            TarefaRequestDTO requestDTO = new TarefaRequestDTO("", "Descricao", false);

            mockMvc.perform(post("/tarefas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest());

            verify(service, never()).salvar(any(TarefaRequestDTO.class));
        }

        @Test
        @DisplayName("Deve retornar 400 Bad Request quando título tiver menos de 3 caracteres")
        void deveRetornar400QuandoTituloMuitoCurto() throws Exception {
            TarefaRequestDTO requestDTO = new TarefaRequestDTO("AB", "Descricao", false);

            mockMvc.perform(post("/tarefas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest());

            verify(service, never()).salvar(any(TarefaRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("PUT /tarefas/{id}")
    class AtualizacaoTests {

        @Test
        @DisplayName("Deve retornar 200 OK ao atualizar com sucesso")
        void deveAtualizarComSucesso() throws Exception {
            TarefaRequestDTO requestDTO = new TarefaRequestDTO("Título Atualizado", "Descricao", true);
            TarefaResponseDTO responseDTO = new TarefaResponseDTO(1L, "Título Atualizado", "Descricao", true);

            when(service.atualizar(eq(1L), any(TarefaRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(put("/tarefas/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.titulo").value("Título Atualizado"))
                    .andExpect(jsonPath("$.concluida").value(true));

            verify(service, times(1)).atualizar(eq(1L), any(TarefaRequestDTO.class));
        }

        @Test
        @DisplayName("Deve retornar 404 Not Found ao atualizar ID inexistente")
        void deveRetornar404AoAtualizarIdInexistente() throws Exception {
            TarefaRequestDTO requestDTO = new TarefaRequestDTO("Título Atualizado", "Descricao", true);

            when(service.atualizar(eq(99L), any(TarefaRequestDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Tarefa não encontrada com ID: 99"));

            mockMvc.perform(put("/tarefas/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).atualizar(eq(99L), any(TarefaRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("DELETE /tarefas/{id}")
    class DelecaoTests {

        @Test
        @DisplayName("Deve retornar 204 No Content ao deletar com sucesso")
        void deveDeletarComSucesso() throws Exception {
            doNothing().when(service).deletar(1L);

            mockMvc.perform(delete("/tarefas/1"))
                    .andExpect(status().isNoContent());

            verify(service, times(1)).deletar(1L);
        }

        @Test
        @DisplayName("Deve retornar 404 Not Found ao tentar deletar ID inexistente")
        void deveRetornar404AoDeletarIdInexistente() throws Exception {
            doThrow(new ResourceNotFoundException("Tarefa não encontrada com ID: 99"))
                    .when(service).deletar(99L);

            mockMvc.perform(delete("/tarefas/99"))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).deletar(99L);
        }
    }
}