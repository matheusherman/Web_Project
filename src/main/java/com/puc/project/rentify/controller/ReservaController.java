package com.puc.project.rentify.controller;

import com.puc.project.rentify.model.*;
import com.puc.project.rentify.repository.ImovelRepository;
import com.puc.project.rentify.repository.LocatarioRepository;
import com.puc.project.rentify.repository.ReservaRepository;
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
@RequestMapping("/api/reservas")
@Tag(name = "Reserva", description = "API Reserva contendo todos os processos relacionado.")
public class ReservaController {
    @Autowired
    ReservaRepository repository;
    @Autowired
    LocatarioRepository locatarioRepository;
    @Autowired
    ImovelRepository imovelRepository;
    @PersistenceContext
    EntityManager entity;

    @GetMapping("/")
    @Operation(summary = "Listar Reserva")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listagem de Reservas"),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    public ResponseEntity<Object> findAll() {
        try {
            List<Reserva> data = new ArrayList<Reserva>();
            repository.findAll().forEach(data::add);
            if (data.isEmpty()) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }
            return new ResponseEntity<>(data, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiError("500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Pesquisar Reserva")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pesquisa por um Reserva usando qualquer parâmetro"),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("/search")
    public ResponseEntity<Object> findBy(@RequestParam Map<String, String> allParams) {
        try {
            if (allParams.containsKey("id")) {
                try {
                    Optional<Reserva> single_data = repository.findById(Long.valueOf(allParams.get("id")));
                    if (single_data.isPresent()){
                        List<Reserva> data = new ArrayList<>();
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
                CriteriaQuery<Reserva> query = criteria.createQuery(Reserva.class);
                Root<Reserva> root = query.from(Reserva.class);
                List<Predicate> predicates = new ArrayList<>();
                if (allParams.containsKey("data_inicio")) {
                    predicates.add(criteria.equal(root.get("data_inicio"), LocalDate.parse(allParams.get("data_inicio"))));
                }
                if (allParams.containsKey("data_fim")) {
                    predicates.add(criteria.equal(root.get("data_fim"), LocalDate.parse(allParams.get("data_fim"))));
                }
                if (allParams.containsKey("valor_reserva")) {
                    predicates.add(criteria.equal(root.get("valor_reserva"), Double.valueOf(allParams.get("valor_reserva"))));
                }
                if (allParams.containsKey("locatario")) {
                    predicates.add(criteria.equal(root.get("locatario").get("id"), Long.valueOf(allParams.get("locatario"))));
                }
                if (allParams.containsKey("imovel")) {
                    predicates.add(criteria.equal(root.get("imovel").get("id"), Long.valueOf(allParams.get("imovel"))));
                }
                query.where(predicates.toArray(new Predicate[0]));
                List<Reserva> result = entity.createQuery(query).getResultList();
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiError("500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Criar Reserva")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva criada", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaRegistro.class))}),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "404", description = "Locatário ou Imóvel não existente ou Imóvel e Data da reserva já em uso"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @PostMapping("/")
    public ResponseEntity<Object> create(@RequestBody ReservaRegistro registro) {
        try {
            if (!locatarioRepository.existsById(registro.getLocatario_id())) {
                return new ResponseEntity<>(new ApiError("406", "Não tem locatario"), HttpStatus.NOT_ACCEPTABLE);
            } else if (!imovelRepository.existsById(registro.getImovel_id())) {
                return new ResponseEntity<>(new ApiError("406", "Não tem Imovel"), HttpStatus.NOT_ACCEPTABLE);
            } else if (!repository.existsReservaByImovelInRangeDataInicio(imovelRepository.findById(registro.getImovel_id()).get(), registro.getData_inicio(), registro.getData_inicio()).isEmpty()) {
                return new ResponseEntity<>(new ApiError("406", "Já existe uma reserva com esse imovel nas datas"), HttpStatus.NOT_ACCEPTABLE);
            } else {
                Reserva reserva = new Reserva();
                Locatario locatario = locatarioRepository.findById(registro.getLocatario_id()).get();
                Imovel imovel = imovelRepository.findById(registro.getImovel_id()).get();
                reserva.setData_inicio(registro.getData_inicio());
                reserva.setData_fim(registro.getData_fim());
                reserva.setValor_reserva(registro.getValor_reserva());
                reserva.setLocatario(locatario);
                reserva.setImovel(imovel);
                return new ResponseEntity<>(repository.save(reserva), HttpStatus.CREATED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiError("500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Alterar Reserva pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva alterada", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaRegistro.class))}),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "404", description = "Reserva ID não encontrado"),
            @ApiResponse(responseCode = "406", description = "Locatário ou Imóvel não existente ou Imóvel e Data da reserva já em uso")
    })
    @PutMapping("/")
    public ResponseEntity<Object> update(@RequestParam long id, @RequestBody ReservaRegistro registro) {
        Optional<Reserva> data = repository.findById(id);
        if (data.isPresent()) {
            if (!locatarioRepository.existsById(registro.getLocatario_id())) {
                return new ResponseEntity<>(new ApiError("406", "Locatario não existente"), HttpStatus.NOT_ACCEPTABLE);
            } else if (!imovelRepository.existsById(registro.getImovel_id())) {
                return new ResponseEntity<>(new ApiError("406", "Imovel não existente"), HttpStatus.NOT_ACCEPTABLE);
            } else if (!repository.existsReservaByImovelInRangeDataInicio(imovelRepository.findById(registro.getImovel_id()).get(), registro.getData_inicio(), registro.getData_inicio()).isEmpty()) {
                return new ResponseEntity<>(new ApiError("406", "Já existe uma reserva com esse imovel nas datas"), HttpStatus.NOT_ACCEPTABLE);
            } else {
                Reserva reserva = data.get();
                Locatario locatario = locatarioRepository.findById(registro.getLocatario_id()).get();
                Imovel imovel = imovelRepository.findById(registro.getImovel_id()).get();
                reserva.setData_inicio(registro.getData_inicio());
                reserva.setData_fim(registro.getData_fim());
                reserva.setValor_reserva(registro.getValor_reserva());
                reserva.setLocatario(locatario);
                reserva.setImovel(imovel);
                return new ResponseEntity<>(repository.save(reserva), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(new ApiError("404"), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Deletar Reserva por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva deletada"),
            @ApiResponse(responseCode = "403", description = "Não permitido a ação atual. Usuário não autenticado ou sem Permissão suficiente"),
            @ApiResponse(responseCode = "404", description = "ID não existente"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @DeleteMapping("/")
    public ResponseEntity<Object> delete(@RequestParam long id) {
        try {
            Optional<Reserva> reserva = repository.findById(id);
            if (reserva.isPresent()) {
                repository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiError("404"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiError("500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

