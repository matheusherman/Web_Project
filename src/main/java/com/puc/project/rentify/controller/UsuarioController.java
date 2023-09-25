package com.puc.project.rentify.controller;

import com.puc.project.rentify.model.ApiError;
import com.puc.project.rentify.model.UsuarioRegistro;
import com.puc.project.rentify.model.Usuario;
import com.puc.project.rentify.model.Auth;
import com.puc.project.rentify.repository.LocadoraRepository;
import com.puc.project.rentify.repository.LocatarioRepository;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuário", description = "API Usuário contendo todos os processos relacionado.")
public class UsuarioController {
    @Autowired
    UsuarioRepository repository;
    @Autowired
    LocatarioRepository locatarioRepository;
    @Autowired
    LocadoraRepository locadoraRepository;
    @PersistenceContext
    EntityManager entity;

    @GetMapping("/")
    @Operation(summary = "Listar Usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listagem de Usuários"),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<Object> findAll() {
        try {
            List<Usuario> data = new ArrayList<Usuario>();
            repository.findAll().forEach(data::add);
            if (data.isEmpty()) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiError("500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Pesquisar Usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pesquisa por um Usuário usando qualquer parâmetro"),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("/search")
    public ResponseEntity<Object> findBy(@RequestParam Map<String, String> allParams) {
        try {
            if (allParams.containsKey("id")) {
                try {
                    Optional<Usuario> single_data = repository.findById(Long.valueOf(allParams.get("id")));
                    if (single_data.isPresent()){
                        List<Usuario> data = new ArrayList<>();
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
                CriteriaQuery<Usuario> query = criteria.createQuery(Usuario.class);
                Root<Usuario> root = query.from(Usuario.class);
                List<Predicate> predicates = new ArrayList<>();
                if (allParams.containsKey("usuario")) {
                    predicates.add(criteria.equal(root.get("usuario"), allParams.get("usuario")));
                }
                if (allParams.containsKey("email")) {
                    predicates.add(criteria.equal(root.get("email"), allParams.get("email")));
                }
                query.where(predicates.toArray(new Predicate[0]));
                List<Usuario> result = entity.createQuery(query).getResultList();
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiError("500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Criar Usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criada", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioRegistro.class))}),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "406", description = "Usuário ou email já está em uso, ou Role inválida"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @PostMapping("/")
    public ResponseEntity<Object> create(@RequestBody UsuarioRegistro registro) {
        try {
            if (repository.existsUsuarioByUsuario(registro.getUsuario())) {
                return new ResponseEntity<>(new ApiError("406", "Usuário já existente"), HttpStatus.NOT_ACCEPTABLE);
            } else if (repository.existsUsuarioByEmail(registro.getEmail())) {
                return new ResponseEntity<>(new ApiError("406", "Email já existente"), HttpStatus.NOT_ACCEPTABLE);
            } else {
                Usuario _usuario = new Usuario();
                _usuario.setUsuario(registro.getUsuario());
                _usuario.setEmail(registro.getEmail());
                _usuario.setSenha(registro.getSenha());
                return new ResponseEntity<>(repository.save(_usuario), HttpStatus.CREATED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiError("500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Alterar Usuário pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário alterada", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioRegistro.class))}),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "404", description = "Usuário id não encontrado"),
            @ApiResponse(responseCode = "406", description = "Usuário ou email já está em uso, ou Role inválida")
    })
    @PutMapping("/")
    public ResponseEntity<Object> update(@RequestParam long id, @RequestBody UsuarioRegistro registro) {
        Optional<Usuario> data = repository.findById(id);
        if (data.isPresent()) {
            if (repository.existsUsuarioByUsuario(registro.getUsuario())) {
                return new ResponseEntity<>(new ApiError("406", "Usuário já existente"), HttpStatus.NOT_ACCEPTABLE);
            } else if (repository.existsUsuarioByEmail(registro.getEmail())) {
                return new ResponseEntity<>(new ApiError("406", "Email já existente"), HttpStatus.NOT_ACCEPTABLE);
            } else {
                Usuario _usuario = data.get();
                _usuario.setUsuario(registro.getUsuario());
                _usuario.setEmail(registro.getEmail());
                _usuario.setSenha(registro.getSenha());
                return new ResponseEntity<>(repository.save(_usuario), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(new ApiError("404"), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Deletar Usuário por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário deletada"),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "404", description = "ID não existente"),
            @ApiResponse(responseCode = "406", description = "ID sendo utilizado na tabela Locatario ou Locadora"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @DeleteMapping("/")
    public ResponseEntity<Object> delete(@RequestParam long id) {
        try {
            Optional<Usuario> usuario = repository.findById(id);
            if (usuario.isPresent()) {
                if (locatarioRepository.existsLocatarioByUsuario(usuario.get())) {
                    return new ResponseEntity<Object>(new ApiError("406", "ID sendo utilizado na tabela 'locatario'"), new HttpHeaders(),  HttpStatus.NOT_ACCEPTABLE);
                } else if (locadoraRepository.existsLocadoraByUsuario(usuario.get())) {
                    return new ResponseEntity<Object>(new ApiError("406", "ID sendo utilizado na tabela 'locadora'"), new HttpHeaders(),  HttpStatus.NOT_ACCEPTABLE);
                } else {
                    repository.deleteById(id);
                    return new ResponseEntity<Object>(null, new HttpHeaders(),  HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<Object>(new ApiError("404").toString(), new HttpHeaders(),  HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<Object>(new ApiError("500").toString(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Operation(summary = "Autenticar usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário logado"),
            @ApiResponse(responseCode = "404", description = "Usuario e/ou senha não encontrados"),
    })
    @PostMapping("auth")
    public ResponseEntity<Usuario> findBy(@RequestBody Auth auth) {
        try {
            Usuario usuario = repository.findByUsuarioAndSenha(auth.getUsuario(), auth.getSenha());
            return Optional
                    .ofNullable(usuario)
                    .map( user -> ResponseEntity.ok().body(user) )          //200 OK
                    .orElseGet( () -> ResponseEntity.notFound().build() );  //404 Not found
//            if (usuario != null) {
//                return new ResponseEntity<>(null, new HttpHeaders(),  HttpStatus.OK);
//            } else {
//                return new ResponseEntity<>(new ApiError("404", "Usuario e/ou senha não encontrados"), HttpStatus.NOT_FOUND);
//            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

