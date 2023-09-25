package com.puc.project.rentify.controller;

import com.puc.project.rentify.model.*;
import com.puc.project.rentify.repository.LocatarioRepository;
import com.puc.project.rentify.repository.ReservaRepository;
import com.puc.project.rentify.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/locatarios")
@Tag(name = "Locatario", description = "API Locatario contendo todos os processos relacionado.")
public class LocatarioController {
    @Autowired
    LocatarioRepository repository;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    ReservaRepository reservaRepository;
    @PersistenceContext
    EntityManager entity;

    @GetMapping("/")
    @Operation(summary = "Listar Locatario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listagem de Locatarios"),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<Object> findAll() {
        try {
            List<Locatario> data = new ArrayList<Locatario>();
            repository.findAll().forEach(data::add);
            if (data.isEmpty()) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiError("500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Pesquisar Locatario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pesquisa por um Locatario usando qualquer parâmetro"),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("/search")
    public ResponseEntity<Object> findBy(@RequestParam Map<String, String> allParams) {
        try {
            if (allParams.containsKey("id")) {
                try {
                    Optional<Locatario> single_data = repository.findById(Long.valueOf(allParams.get("id")));
                    if (single_data.isPresent()){
                        List<Locatario> data = new ArrayList<>();
                        data.add(single_data.get());
                        return new ResponseEntity<>(data, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
                    }
                } catch (Exception e) {
                    return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
                }
            } else if (!allParams.isEmpty()) {
                CriteriaBuilder criteria = entity.getCriteriaBuilder();
                CriteriaQuery<Locatario> query = criteria.createQuery(Locatario.class);
                Root<Locatario> root = query.from(Locatario.class);
                List<Predicate> predicates = new ArrayList<>();
                if (allParams.containsKey("cpf")) {
                    predicates.add(criteria.equal(root.get("cpf"), allParams.get("cpf")));
                }
                if (allParams.containsKey("celular")) {
                    predicates.add(criteria.equal(root.get("celular"), allParams.get("celular")));
                }
                if (allParams.containsKey("data_nascimento")) {
                    predicates.add(criteria.equal(root.get("data_nascimento"), LocalDate.parse(allParams.get("data_nascimento"))));
                }
                if (allParams.containsKey("cep")) {
                    predicates.add(criteria.equal(root.get("cep"), allParams.get("cep")));
                }
                if (allParams.containsKey("numero")) {
                    predicates.add(criteria.equal(root.get("numero"), allParams.get("numero")));
                }
                if (allParams.containsKey("complemento")) {
                    predicates.add(criteria.equal(root.get("complemento"), allParams.get("complemento")));
                }
                if (allParams.containsKey("usuario")) {
                    predicates.add(criteria.equal(root.get("usuario").get("id"), Long.valueOf(allParams.get("usuario"))));
                }
                query.where(predicates.toArray(new Predicate[0]));
                List<Locatario> result = entity.createQuery(query).getResultList();
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiError("500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Criar Locatario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Locatario criada", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = LocatarioRegistro.class))}),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "406", description = "Usuário ou email já está em uso, ou Role inválida, ou CPF em uso"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @PostMapping("/")
    public ResponseEntity<Object> create(@RequestBody LocatarioRegistro registro) {
        try {
            if (repository.existsLocatarioByCpf(registro.getCpf())) {
                return new ResponseEntity<>(new ApiError("406", "CPF já existente"), HttpStatus.NOT_ACCEPTABLE);
            } else if (usuarioRepository.existsUsuarioByUsuario(registro.getUsuarioRegistro().getUsuario())) {
                return new ResponseEntity<>(new ApiError("406", "Usuário já existente"), HttpStatus.NOT_ACCEPTABLE);
            } else if (usuarioRepository.existsUsuarioByEmail(registro.getUsuarioRegistro().getEmail())) {
                return new ResponseEntity<>(new ApiError("406", "Email já existente"), HttpStatus.NOT_ACCEPTABLE);
            } else {
                Locatario locatario = new Locatario();
                Usuario usuario = new Usuario();

                locatario.setCpf(registro.getCpf());
                locatario.setCelular(registro.getCelular());
                locatario.setData_nascimento(registro.getData_nascimento());
                locatario.setCep(registro.getCep());
                locatario.setNumero(registro.getNumero());
                locatario.setComplemento(registro.getComplemento());
                usuario.setUsuario(registro.getUsuarioRegistro().getUsuario());
                usuario.setEmail(registro.getUsuarioRegistro().getEmail());
                usuario.setSenha(registro.getUsuarioRegistro().getSenha());
//                usuario.setSenha(Usuario.encodeSenha(registro.getUsuarioRegistro().getSenha()));
                locatario.setUsuario(usuario);
                return new ResponseEntity<>(repository.save(locatario), HttpStatus.CREATED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiError("500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Alterar Locatario pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locatario alterada", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = LocatarioAtualizacao.class))}),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "404", description = "Locatario ID não encontrado"),
            @ApiResponse(responseCode = "406", description = "Usuário id não existe ou CPF em uso")
    })
    @PutMapping("/")
    public ResponseEntity<Object> update(@RequestParam long id, @RequestBody LocatarioAtualizacao atualizacao) {
        Optional<Locatario> data = repository.findById(id);
        if (data.isPresent()) {
            if (repository.existsLocatarioByCpf(atualizacao.getCpf())) {
                return new ResponseEntity<>(new ApiError("406", "CPF já existente"), HttpStatus.NOT_ACCEPTABLE);
            } else if (usuarioRepository.findById(atualizacao.getUsuario_id()).isEmpty()) {
                return new ResponseEntity<>(new ApiError("406", "Usuário já existente"), HttpStatus.NOT_ACCEPTABLE);
            } else {
                Locatario locatario = data.get();
                Usuario usuario = usuarioRepository.findById(atualizacao.getUsuario_id()).get();
                locatario.setCpf(atualizacao.getCpf());
                locatario.setCelular(atualizacao.getCelular());
                locatario.setData_nascimento(atualizacao.getData_nascimento());
                locatario.setCep(atualizacao.getCep());
                locatario.setNumero(atualizacao.getNumero());
                locatario.setComplemento(atualizacao.getComplemento());
                locatario.setUsuario(usuario);
                return new ResponseEntity<>(repository.save(locatario), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(new ApiError("404"), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Deletar Locatario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locatario deletada"),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "404", description = "ID não existente"),
            @ApiResponse(responseCode = "406", description = "ID sendo utilizado na tabela Imovel"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @DeleteMapping("/")
    public ResponseEntity<Object> delete(@RequestParam long id) {
        try {
            Optional<Locatario> locatario = repository.findById(id);
            if (locatario.isPresent()) {
                if (reservaRepository.existsReservaByLocatario(locatario.get())) {
                    return new ResponseEntity<>(new ApiError("406", "ID sendo utilizado na tabela 'imovel'"), HttpStatus.NOT_ACCEPTABLE);
                } else {
                    repository.deleteById(id);
                    return new ResponseEntity<>(null, HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new ApiError("404"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiError("500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
