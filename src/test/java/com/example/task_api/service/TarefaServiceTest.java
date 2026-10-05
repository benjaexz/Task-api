package com.example.task_api.service;

import com.example.task_api.dto.TarefaRequestDTO;
import com.example.task_api.dto.TarefaResponseDTO;
import com.example.task_api.entity.Tarefa;
import com.example.task_api.exception.ResourceNotFoundException;
import com.example.task_api.repository.TarefaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    private TarefaRepository repository;

    @InjectMocks
    private TarefaService service;

    @Nested
    @DisplayName("Cenários de busca e listagem")
    class BuscaListagemTests {

        @Test
        @DisplayName("Deve retornar página de TarefaResponseDTO ao listar todas")
        void deveListarTodasComSucesso() {
            Pageable pageable = PageRequest.of(0, 10);
            Tarefa tarefa = new Tarefa("Estudar Spring", "Revisar testes", false);
            tarefa.setId(1L);
            Page<Tarefa> pagina = new PageImpl<>(List.of(tarefa));

            when(repository.findAll(pageable)).thenReturn(pagina);

            Page<TarefaResponseDTO> resultado = service.listarTodas(pageable);

            assertNotNull(resultado);
            assertEquals(1, resultado.getTotalElements());
            assertEquals("Estudar Spring", resultado.getContent().get(0).titulo());
            verify(repository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Deve retornar TarefaResponseDTO quando ID existir")
        void deveBuscarPorIdComSucesso() {
            Long id = 1L;
            Tarefa tarefa = new Tarefa("Finalizar API", "Adicionar testes", false);
            tarefa.setId(id);

            when(repository.findById(id)).thenReturn(Optional.of(tarefa));

            TarefaResponseDTO resultado = service.buscarPorId(id);

            assertNotNull(resultado);
            assertEquals(id, resultado.id());
            assertEquals("Finalizar API", resultado.titulo());
            verify(repository, times(1)).findById(id);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando ID não existir na busca")
        void deveLancarExcecaoAoBuscarIdInexistente() {
            Long id = 99L;
            when(repository.findById(id)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> service.buscarPorId(id)
            );

            assertEquals("Tarefa não encontrada com ID: 99", exception.getMessage());
            verify(repository, times(1)).findById(id);
        }
    }

    @Nested
    @DisplayName("Cenários de criação e atualização")
    class CriacaoAtualizacaoTests {

        @Test
        @DisplayName("Deve salvar tarefa com sucesso e retornar DTO")
        void deveSalvarComSucesso() {
            TarefaRequestDTO requestDTO = new TarefaRequestDTO("Nova Tarefa", "Descricao teste", false);
            Tarefa tarefaSalva = new Tarefa("Nova Tarefa", "Descricao teste", false);
            tarefaSalva.setId(1L);

            when(repository.save(any(Tarefa.class))).thenReturn(tarefaSalva);

            TarefaResponseDTO resultado = service.salvar(requestDTO);

            assertNotNull(resultado);
            assertEquals(1L, resultado.id());
            assertEquals("Nova Tarefa", resultado.titulo());
            assertFalse(resultado.concluida());
            verify(repository, times(1)).save(any(Tarefa.class));
        }

        @Test
        @DisplayName("Deve atualizar tarefa existente com sucesso")
        void deveAtualizarComSucesso() {
            Long id = 1L;
            TarefaRequestDTO requestDTO = new TarefaRequestDTO("Título Atualizado", "Nova descrição", true);
            Tarefa tarefaExistente = new Tarefa("Título Antigo", "Descrição antiga", false);
            tarefaExistente.setId(id);

            Tarefa tarefaAtualizada = new Tarefa("Título Atualizado", "Nova descrição", true);
            tarefaAtualizada.setId(id);

            when(repository.findById(id)).thenReturn(Optional.of(tarefaExistente));
            when(repository.save(any(Tarefa.class))).thenReturn(tarefaAtualizada);

            TarefaResponseDTO resultado = service.atualizar(id, requestDTO);

            assertNotNull(resultado);
            assertEquals(id, resultado.id());
            assertEquals("Título Atualizado", resultado.titulo());
            assertTrue(resultado.concluida());
            verify(repository, times(1)).findById(id);
            verify(repository, times(1)).save(any(Tarefa.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar ID inexistente")
        void deveLancarExcecaoAoAtualizarIdInexistente() {
            Long id = 99L;
            TarefaRequestDTO requestDTO = new TarefaRequestDTO("Título", "Descrição", false);

            when(repository.findById(id)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.atualizar(id, requestDTO));

            verify(repository, times(1)).findById(id);
            verify(repository, never()).save(any(Tarefa.class));
        }
    }

    @Nested
    @DisplayName("Cenários de exclusão")
    class DelecaoTests {

        @Test
        @DisplayName("Deve deletar tarefa quando ID existir")
        void deveDeletarComSucesso() {
            Long id = 1L;
            when(repository.existsById(id)).thenReturn(true);
            doNothing().when(repository).deleteById(id);

            assertDoesNotThrow(() -> service.deletar(id));

            verify(repository, times(1)).existsById(id);
            verify(repository, times(1)).deleteById(id);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao tentar deletar ID inexistente")
        void deveLancarExcecaoAoDeletarIdInexistente() {
            Long id = 99L;
            when(repository.existsById(id)).thenReturn(false);

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> service.deletar(id)
            );

            assertEquals("Tarefa não encontrada com ID: 99", exception.getMessage());
            verify(repository, times(1)).existsById(id);
            verify(repository, never()).deleteById(anyLong());
        }
    }
}