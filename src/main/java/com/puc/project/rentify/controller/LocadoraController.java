package com.puc.project.rentify.controller;

import com.puc.project.rentify.model.*;
import com.puc.project.rentify.repository.ImovelRepository;
import com.puc.project.rentify.repository.LocadoraRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/locadoras")
@Tag(name = "Locadora", description = "API Locadora contendo todos os processos relacionado.")
public class LocadoraController {
    @Autowired
    LocadoraRepository repository;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    ImovelRepository imovelRepository;
    @PersistenceContext
    EntityManager entity;

    @GetMapping("/")
    @Operation(summary = "Listar Locadora")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listagem de Locadoras"),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<Object> findAll() {
        try {
            List<Locadora> data = new ArrayList<Locadora>();
            repository.findAll().forEach(data::add);
            if (data.isEmpty()) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiError("500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Pesquisar Locadora")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pesquisa por um Locadora usando qualquer parâmetro"),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("/search")
    public ResponseEntity<Object> findBy(@RequestParam Map<String, String> allParams) {
        try {
            if (allParams.containsKey("id")) {
                try {
                    Optional<Locadora> single_data = repository.findById(Long.valueOf(allParams.get("id")));
                    if (single_data.isPresent()){
                        List<Locadora> data = new ArrayList<>();
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
                CriteriaQuery<Locadora> query = criteria.createQuery(Locadora.class);
                Root<Locadora> root = query.from(Locadora.class);
                List<Predicate> predicates = new ArrayList<>();
                if (allParams.containsKey("nome_fantasia")) {
                    predicates.add(criteria.equal(root.get("nome_fantasia"), allParams.get("nome_fantasia")));
                }
                if (allParams.containsKey("cnpj")) {
                    predicates.add(criteria.equal(root.get("cnpj"), allParams.get("cnpj")));
                }
                if (allParams.containsKey("telefone")) {
                    predicates.add(criteria.equal(root.get("telefone"), allParams.get("telefone")));
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
                List<Locadora> result = entity.createQuery(query).getResultList();
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiError("500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Criar Locadora")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Locadora criada", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = LocadoraRegistro.class))}),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "406", description = "Usuário ou email já está em uso, ou Role inválida, ou CNPJ em uso"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @PostMapping("/")
    public ResponseEntity<Object> create(@RequestBody LocadoraRegistro registro) {
        try {
            if (repository.existsLocadoraByCnpj(registro.getCnpj())) {
                return new ResponseEntity<>(new ApiError("406", "CNPJ já existente"), HttpStatus.NOT_ACCEPTABLE);
            } else if (usuarioRepository.existsUsuarioByUsuario(registro.getUsuarioRegistro().getUsuario())) {
                return new ResponseEntity<>(new ApiError("406", "usuario já existente"), HttpStatus.NOT_ACCEPTABLE);
            } else if (usuarioRepository.existsUsuarioByEmail(registro.getUsuarioRegistro().getEmail())) {
                return new ResponseEntity<>(new ApiError("406", "Email já existente"), HttpStatus.NOT_ACCEPTABLE);
            } else {
                Locadora locadora = new Locadora();
                Usuario usuario = new Usuario();
                locadora.setNome_fantasia(registro.getNome_fantasia());
                locadora.setCnpj(registro.getCnpj());
                locadora.setTelefone(registro.getTelefone());
                locadora.setCep(registro.getCep());
                locadora.setNumero(registro.getNumero());
                locadora.setComplemento(registro.getComplemento());
                usuario.setUsuario(registro.getUsuarioRegistro().getUsuario());
                usuario.setEmail(registro.getUsuarioRegistro().getEmail());
                usuario.setSenha(registro.getUsuarioRegistro().getSenha());
//                usuario.setSenha(Usuario.encodeSenha(registro.getUsuarioRegistro().getSenha()));
                locadora.setUsuario(usuario);
                return new ResponseEntity<>(repository.save(locadora), HttpStatus.CREATED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiError("500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Alterar Locadora pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locadora alterada", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = LocadoraAtualizacao.class))}),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "404", description = "Locadora ID não encontrado"),
            @ApiResponse(responseCode = "406", description = "Usuário id não existe ou CNPJ em uso")
    })
    @PutMapping("/")
    public ResponseEntity<Object> update(@RequestParam long id, @RequestBody LocadoraAtualizacao atualizacao) {
        Optional<Locadora> data = repository.findById(id);
        if (data.isPresent()) {
            if (repository.existsLocadoraByCnpj(atualizacao.getCnpj())) {
                return new ResponseEntity<>(new ApiError("406", "CNPJ já existente"), HttpStatus.NOT_ACCEPTABLE);
            } else if (usuarioRepository.findById(atualizacao.getUsuario_id()).isEmpty()) {
                return new ResponseEntity<>(new ApiError("406", "Usuario já existente"), HttpStatus.NOT_ACCEPTABLE);
            } else {
                Locadora locadora = data.get();
                Usuario usuario = usuarioRepository.findById(atualizacao.getUsuario_id()).get();
                locadora.setNome_fantasia(atualizacao.getNome_fantasia());
                locadora.setCnpj(atualizacao.getCnpj());
                locadora.setTelefone(atualizacao.getTelefone());
                locadora.setCep(atualizacao.getCep());
                locadora.setNumero(atualizacao.getNumero());
                locadora.setComplemento(atualizacao.getComplemento());
                locadora.setUsuario(usuario);
                return new ResponseEntity<>(repository.save(locadora), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(new ApiError("404"), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Deletar Locadora por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locadora deletada"),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "404", description = "ID não existente"),
            @ApiResponse(responseCode = "406", description = "ID sendo utilizado na tabela Imovel"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @DeleteMapping("/")
    public ResponseEntity<Object> delete(@RequestParam long id) {
        try {
            Optional<Locadora> locadora = repository.findById(id);
            if (locadora.isPresent()) {
                if (imovelRepository.existsImovelByLocadora(locadora.get())) {
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
